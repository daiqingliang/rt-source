package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

public class C14nXmlOutput extends UTF8XmlOutput {
  private StaticAttribute[] staticAttributes = new StaticAttribute[8];
  
  private int len = 0;
  
  private int[] nsBuf = new int[8];
  
  private final FinalArrayList<DynamicAttribute> otherAttributes = new FinalArrayList();
  
  private final boolean namedAttributesAreOrdered;
  
  public C14nXmlOutput(OutputStream paramOutputStream, Encoded[] paramArrayOfEncoded, boolean paramBoolean, CharacterEscapeHandler paramCharacterEscapeHandler) {
    super(paramOutputStream, paramArrayOfEncoded, paramCharacterEscapeHandler);
    this.namedAttributesAreOrdered = paramBoolean;
    for (byte b = 0; b < this.staticAttributes.length; b++)
      this.staticAttributes[b] = new StaticAttribute(); 
  }
  
  public void attribute(Name paramName, String paramString) throws IOException {
    if (this.staticAttributes.length == this.len) {
      int i = this.len * 2;
      StaticAttribute[] arrayOfStaticAttribute = new StaticAttribute[i];
      System.arraycopy(this.staticAttributes, 0, arrayOfStaticAttribute, 0, this.len);
      for (int j = this.len; j < i; j++)
        this.staticAttributes[j] = new StaticAttribute(); 
      this.staticAttributes = arrayOfStaticAttribute;
    } 
    this.staticAttributes[this.len++].set(paramName, paramString);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException { this.otherAttributes.add(new DynamicAttribute(paramInt, paramString1, paramString2)); }
  
  public void endStartTag() throws IOException {
    if (this.otherAttributes.isEmpty()) {
      if (this.len != 0) {
        if (!this.namedAttributesAreOrdered)
          Arrays.sort(this.staticAttributes, 0, this.len); 
        for (byte b = 0; b < this.len; b++)
          this.staticAttributes[b].write(); 
        this.len = 0;
      } 
    } else {
      int i;
      for (i = 0; i < this.len; i++)
        this.otherAttributes.add(this.staticAttributes[i].toDynamicAttribute()); 
      this.len = 0;
      Collections.sort(this.otherAttributes);
      i = this.otherAttributes.size();
      for (byte b = 0; b < i; b++) {
        DynamicAttribute dynamicAttribute = (DynamicAttribute)this.otherAttributes.get(b);
        super.attribute(dynamicAttribute.prefix, dynamicAttribute.localName, dynamicAttribute.value);
      } 
      this.otherAttributes.clear();
    } 
    super.endStartTag();
  }
  
  protected void writeNsDecls(int paramInt) throws IOException {
    int i = this.nsContext.getCurrent().count();
    if (i == 0)
      return; 
    if (i > this.nsBuf.length)
      this.nsBuf = new int[i]; 
    int j;
    for (j = i - 1; j >= 0; j--)
      this.nsBuf[j] = paramInt + j; 
    for (j = 0; j < i; j++) {
      for (int k = j + 1; k < i; k++) {
        String str1 = this.nsContext.getPrefix(this.nsBuf[j]);
        String str2 = this.nsContext.getPrefix(this.nsBuf[k]);
        if (str1.compareTo(str2) > 0) {
          int m = this.nsBuf[k];
          this.nsBuf[k] = this.nsBuf[j];
          this.nsBuf[j] = m;
        } 
      } 
    } 
    for (j = 0; j < i; j++)
      writeNsDecl(this.nsBuf[j]); 
  }
  
  final class DynamicAttribute extends Object implements Comparable<DynamicAttribute> {
    final int prefix;
    
    final String localName;
    
    final String value;
    
    public DynamicAttribute(int param1Int, String param1String1, String param1String2) {
      this.prefix = param1Int;
      this.localName = param1String1;
      this.value = param1String2;
    }
    
    private String getURI() { return (this.prefix == -1) ? "" : C14nXmlOutput.this.nsContext.getNamespaceURI(this.prefix); }
    
    public int compareTo(DynamicAttribute param1DynamicAttribute) {
      int i = getURI().compareTo(param1DynamicAttribute.getURI());
      return (i != 0) ? i : this.localName.compareTo(param1DynamicAttribute.localName);
    }
  }
  
  final class StaticAttribute extends Object implements Comparable<StaticAttribute> {
    Name name;
    
    String value;
    
    public void set(Name param1Name, String param1String) throws IOException {
      this.name = param1Name;
      this.value = param1String;
    }
    
    void write() throws IOException { C14nXmlOutput.this.attribute(this.name, this.value); }
    
    C14nXmlOutput.DynamicAttribute toDynamicAttribute() {
      int i;
      short s = this.name.nsUriIndex;
      if (s == -1) {
        i = -1;
      } else {
        i = C14nXmlOutput.this.nsUriIndex2prefixIndex[s];
      } 
      return new C14nXmlOutput.DynamicAttribute(C14nXmlOutput.this, i, this.name.localName, this.value);
    }
    
    public int compareTo(StaticAttribute param1StaticAttribute) { return this.name.compareTo(param1StaticAttribute.name); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\C14nXmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */