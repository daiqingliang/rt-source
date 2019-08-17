package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.Map;

public class AttributesHolder implements EncodingAlgorithmAttributes {
  private static final int DEFAULT_CAPACITY = 8;
  
  private Map _registeredEncodingAlgorithms;
  
  private int _attributeCount;
  
  private QualifiedName[] _names = new QualifiedName[8];
  
  private String[] _values = new String[8];
  
  private String[] _algorithmURIs = new String[8];
  
  private int[] _algorithmIds = new int[8];
  
  private Object[] _algorithmData = new Object[8];
  
  public AttributesHolder() {}
  
  public AttributesHolder(Map paramMap) {
    this();
    this._registeredEncodingAlgorithms = paramMap;
  }
  
  public final int getLength() { return this._attributeCount; }
  
  public final String getLocalName(int paramInt) { return (this._names[paramInt]).localName; }
  
  public final String getQName(int paramInt) { return this._names[paramInt].getQNameString(); }
  
  public final String getType(int paramInt) { return "CDATA"; }
  
  public final String getURI(int paramInt) { return (this._names[paramInt]).namespaceName; }
  
  public final String getValue(int paramInt) {
    String str = this._values[paramInt];
    if (str != null)
      return str; 
    if (this._algorithmData[paramInt] == null || (this._algorithmIds[paramInt] >= 32 && this._registeredEncodingAlgorithms == null))
      return null; 
    try {
      this._values[paramInt] = convertEncodingAlgorithmDataToString(this._algorithmIds[paramInt], this._algorithmURIs[paramInt], this._algorithmData[paramInt]).toString();
      return convertEncodingAlgorithmDataToString(this._algorithmIds[paramInt], this._algorithmURIs[paramInt], this._algorithmData[paramInt]).toString();
    } catch (IOException iOException) {
      return null;
    } catch (FastInfosetException fastInfosetException) {
      return null;
    } 
  }
  
  public final int getIndex(String paramString) {
    int i = paramString.indexOf(':');
    String str1 = "";
    String str2 = paramString;
    if (i >= 0) {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } 
    for (i = 0; i < this._attributeCount; i++) {
      QualifiedName qualifiedName = this._names[i];
      if (str2.equals(qualifiedName.localName) && str1.equals(qualifiedName.prefix))
        return i; 
    } 
    return -1;
  }
  
  public final String getType(String paramString) {
    int i = getIndex(paramString);
    return (i >= 0) ? "CDATA" : null;
  }
  
  public final String getValue(String paramString) {
    int i = getIndex(paramString);
    return (i >= 0) ? this._values[i] : null;
  }
  
  public final int getIndex(String paramString1, String paramString2) {
    for (byte b = 0; b < this._attributeCount; b++) {
      QualifiedName qualifiedName = this._names[b];
      if (paramString2.equals(qualifiedName.localName) && paramString1.equals(qualifiedName.namespaceName))
        return b; 
    } 
    return -1;
  }
  
  public final String getType(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i >= 0) ? "CDATA" : null;
  }
  
  public final String getValue(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i >= 0) ? this._values[i] : null;
  }
  
  public final void clear() {
    for (byte b = 0; b < this._attributeCount; b++) {
      this._values[b] = null;
      this._algorithmData[b] = null;
    } 
    this._attributeCount = 0;
  }
  
  public final String getAlgorithmURI(int paramInt) { return this._algorithmURIs[paramInt]; }
  
  public final int getAlgorithmIndex(int paramInt) { return this._algorithmIds[paramInt]; }
  
  public final Object getAlgorithmData(int paramInt) { return this._algorithmData[paramInt]; }
  
  public String getAlpababet(int paramInt) { return null; }
  
  public boolean getToIndex(int paramInt) { return false; }
  
  public final void addAttribute(QualifiedName paramQualifiedName, String paramString) {
    if (this._attributeCount == this._names.length)
      resize(); 
    this._names[this._attributeCount] = paramQualifiedName;
    this._values[this._attributeCount++] = paramString;
  }
  
  public final void addAttributeWithAlgorithmData(QualifiedName paramQualifiedName, String paramString, int paramInt, Object paramObject) {
    if (this._attributeCount == this._names.length)
      resize(); 
    this._names[this._attributeCount] = paramQualifiedName;
    this._values[this._attributeCount] = null;
    this._algorithmURIs[this._attributeCount] = paramString;
    this._algorithmIds[this._attributeCount] = paramInt;
    this._algorithmData[this._attributeCount++] = paramObject;
  }
  
  public final QualifiedName getQualifiedName(int paramInt) { return this._names[paramInt]; }
  
  public final String getPrefix(int paramInt) { return (this._names[paramInt]).prefix; }
  
  private final void resize() {
    int i = this._attributeCount * 3 / 2 + 1;
    QualifiedName[] arrayOfQualifiedName = new QualifiedName[i];
    String[] arrayOfString1 = new String[i];
    String[] arrayOfString2 = new String[i];
    int[] arrayOfInt = new int[i];
    Object[] arrayOfObject = new Object[i];
    System.arraycopy(this._names, 0, arrayOfQualifiedName, 0, this._attributeCount);
    System.arraycopy(this._values, 0, arrayOfString1, 0, this._attributeCount);
    System.arraycopy(this._algorithmURIs, 0, arrayOfString2, 0, this._attributeCount);
    System.arraycopy(this._algorithmIds, 0, arrayOfInt, 0, this._attributeCount);
    System.arraycopy(this._algorithmData, 0, arrayOfObject, 0, this._attributeCount);
    this._names = arrayOfQualifiedName;
    this._values = arrayOfString1;
    this._algorithmURIs = arrayOfString2;
    this._algorithmIds = arrayOfInt;
    this._algorithmData = arrayOfObject;
  }
  
  private final StringBuffer convertEncodingAlgorithmDataToString(int paramInt, String paramString, Object paramObject) throws FastInfosetException, IOException {
    EncodingAlgorithm encodingAlgorithm = null;
    if (paramInt < 9) {
      encodingAlgorithm = BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt);
    } else {
      if (paramInt == 9)
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported")); 
      if (paramInt >= 32) {
        if (paramString == null)
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent") + paramInt); 
        encodingAlgorithm = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(paramString);
        if (encodingAlgorithm == null)
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmNotRegistered") + paramString); 
      } else {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
      } 
    } 
    StringBuffer stringBuffer = new StringBuffer();
    encodingAlgorithm.convertToCharacters(paramObject, stringBuffer);
    return stringBuffer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\sax\AttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */