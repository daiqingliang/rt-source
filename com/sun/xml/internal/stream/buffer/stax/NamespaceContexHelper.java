package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class NamespaceContexHelper implements NamespaceContextEx {
  private static int DEFAULT_SIZE = 8;
  
  private String[] prefixes = new String[DEFAULT_SIZE];
  
  private String[] namespaceURIs = new String[DEFAULT_SIZE];
  
  private int namespacePosition;
  
  private int[] contexts = new int[DEFAULT_SIZE];
  
  private int contextPosition;
  
  public NamespaceContexHelper() {
    this.prefixes[0] = "xml";
    this.namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
    this.prefixes[1] = "xmlns";
    this.namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
    this.namespacePosition = 2;
  }
  
  public String getNamespaceURI(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    paramString = paramString.intern();
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.prefixes[i];
      if (str == paramString)
        return this.namespaceURIs[i]; 
    } 
    return "";
  }
  
  public String getPrefix(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.namespaceURIs[i];
      if (str == paramString || str.equals(paramString)) {
        String str1 = this.prefixes[i];
        while (++i < this.namespacePosition) {
          if (str1 == this.prefixes[i])
            return null; 
          i++;
        } 
        return str1;
      } 
    } 
    return null;
  }
  
  public Iterator getPrefixes(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    ArrayList arrayList = new ArrayList();
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.namespaceURIs[i];
      if (str == paramString || str.equals(paramString)) {
        String str1 = this.prefixes[i];
        int j = i + 1;
        while (true) {
          if (j < this.namespacePosition) {
            if (str1 == this.prefixes[j])
              break; 
            j++;
            continue;
          } 
          arrayList.add(str1);
          break;
        } 
      } 
    } 
    return arrayList.iterator();
  }
  
  public Iterator<NamespaceContextEx.Binding> iterator() {
    if (this.namespacePosition == 2)
      return Collections.EMPTY_LIST.iterator(); 
    ArrayList arrayList = new ArrayList(this.namespacePosition);
    for (int i = this.namespacePosition - 1; i >= 2; i--) {
      String str = this.prefixes[i];
      for (int j = i + 1; j < this.namespacePosition && str != this.prefixes[j]; j++)
        arrayList.add(new NamespaceBindingImpl(i)); 
    } 
    return arrayList.iterator();
  }
  
  public void declareDefaultNamespace(String paramString) { declareNamespace("", paramString); }
  
  public void declareNamespace(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new IllegalArgumentException(); 
    paramString1 = paramString1.intern();
    if (paramString1 == "xml" || paramString1 == "xmlns")
      return; 
    if (paramString2 != null)
      paramString2 = paramString2.intern(); 
    if (this.namespacePosition == this.namespaceURIs.length)
      resizeNamespaces(); 
    this.prefixes[this.namespacePosition] = paramString1;
    this.namespaceURIs[this.namespacePosition++] = paramString2;
  }
  
  private void resizeNamespaces() {
    int i = this.namespaceURIs.length * 3 / 2 + 1;
    String[] arrayOfString1 = new String[i];
    System.arraycopy(this.prefixes, 0, arrayOfString1, 0, this.prefixes.length);
    this.prefixes = arrayOfString1;
    String[] arrayOfString2 = new String[i];
    System.arraycopy(this.namespaceURIs, 0, arrayOfString2, 0, this.namespaceURIs.length);
    this.namespaceURIs = arrayOfString2;
  }
  
  public void pushContext() {
    if (this.contextPosition == this.contexts.length)
      resizeContexts(); 
    this.contexts[this.contextPosition++] = this.namespacePosition;
  }
  
  private void resizeContexts() {
    int[] arrayOfInt = new int[this.contexts.length * 3 / 2 + 1];
    System.arraycopy(this.contexts, 0, arrayOfInt, 0, this.contexts.length);
    this.contexts = arrayOfInt;
  }
  
  public void popContext() {
    if (this.contextPosition > 0)
      this.namespacePosition = this.contexts[--this.contextPosition]; 
  }
  
  public void resetContexts() { this.namespacePosition = 2; }
  
  private final class NamespaceBindingImpl implements NamespaceContextEx.Binding {
    int index;
    
    NamespaceBindingImpl(int param1Int) { this.index = param1Int; }
    
    public String getPrefix() { return NamespaceContexHelper.this.prefixes[this.index]; }
    
    public String getNamespaceURI() { return NamespaceContexHelper.this.namespaceURIs[this.index]; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\stax\NamespaceContexHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */