package com.sun.xml.internal.org.jvnet.fastinfoset.sax.helpers;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.Map;
import org.xml.sax.Attributes;

public class EncodingAlgorithmAttributesImpl implements EncodingAlgorithmAttributes {
  private static final int DEFAULT_CAPACITY = 8;
  
  private static final int URI_OFFSET = 0;
  
  private static final int LOCALNAME_OFFSET = 1;
  
  private static final int QNAME_OFFSET = 2;
  
  private static final int TYPE_OFFSET = 3;
  
  private static final int VALUE_OFFSET = 4;
  
  private static final int ALGORITHMURI_OFFSET = 5;
  
  private static final int SIZE = 6;
  
  private Map _registeredEncodingAlgorithms;
  
  private int _length;
  
  private String[] _data = new String[48];
  
  private int[] _algorithmIds = new int[8];
  
  private Object[] _algorithmData = new Object[8];
  
  private String[] _alphabets = new String[8];
  
  private boolean[] _toIndex = new boolean[8];
  
  public EncodingAlgorithmAttributesImpl() { this(null, null); }
  
  public EncodingAlgorithmAttributesImpl(Attributes paramAttributes) { this(null, paramAttributes); }
  
  public EncodingAlgorithmAttributesImpl(Map paramMap, Attributes paramAttributes) {
    this._registeredEncodingAlgorithms = paramMap;
    if (paramAttributes != null)
      if (paramAttributes instanceof EncodingAlgorithmAttributes) {
        setAttributes((EncodingAlgorithmAttributes)paramAttributes);
      } else {
        setAttributes(paramAttributes);
      }  
  }
  
  public final void clear() {
    for (byte b = 0; b < this._length; b++) {
      this._data[b * 6 + 4] = null;
      this._algorithmData[b] = null;
    } 
    this._length = 0;
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    if (this._length >= this._algorithmData.length)
      resize(); 
    int i = this._length * 6;
    this._data[i++] = replaceNull(paramString1);
    this._data[i++] = replaceNull(paramString2);
    this._data[i++] = replaceNull(paramString3);
    this._data[i++] = replaceNull(paramString4);
    this._data[i++] = replaceNull(paramString5);
    this._toIndex[this._length] = false;
    this._alphabets[this._length] = null;
    this._length++;
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean, String paramString6) {
    if (this._length >= this._algorithmData.length)
      resize(); 
    int i = this._length * 6;
    this._data[i++] = replaceNull(paramString1);
    this._data[i++] = replaceNull(paramString2);
    this._data[i++] = replaceNull(paramString3);
    this._data[i++] = replaceNull(paramString4);
    this._data[i++] = replaceNull(paramString5);
    this._toIndex[this._length] = paramBoolean;
    this._alphabets[this._length] = paramString6;
    this._length++;
  }
  
  public void addAttributeWithBuiltInAlgorithmData(String paramString1, String paramString2, String paramString3, int paramInt, Object paramObject) {
    if (this._length >= this._algorithmData.length)
      resize(); 
    int i = this._length * 6;
    this._data[i++] = replaceNull(paramString1);
    this._data[i++] = replaceNull(paramString2);
    this._data[i++] = replaceNull(paramString3);
    this._data[i++] = "CDATA";
    this._data[i++] = "";
    this._data[i++] = null;
    this._algorithmIds[this._length] = paramInt;
    this._algorithmData[this._length] = paramObject;
    this._toIndex[this._length] = false;
    this._alphabets[this._length] = null;
    this._length++;
  }
  
  public void addAttributeWithAlgorithmData(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, Object paramObject) {
    if (this._length >= this._algorithmData.length)
      resize(); 
    int i = this._length * 6;
    this._data[i++] = replaceNull(paramString1);
    this._data[i++] = replaceNull(paramString2);
    this._data[i++] = replaceNull(paramString3);
    this._data[i++] = "CDATA";
    this._data[i++] = "";
    this._data[i++] = paramString4;
    this._algorithmIds[this._length] = paramInt;
    this._algorithmData[this._length] = paramObject;
    this._toIndex[this._length] = false;
    this._alphabets[this._length] = null;
    this._length++;
  }
  
  public void replaceWithAttributeAlgorithmData(int paramInt1, String paramString, int paramInt2, Object paramObject) {
    if (paramInt1 < 0 || paramInt1 >= this._length)
      return; 
    int i = paramInt1 * 6;
    this._data[i + 4] = null;
    this._data[i + 5] = paramString;
    this._algorithmIds[paramInt1] = paramInt2;
    this._algorithmData[paramInt1] = paramObject;
    this._toIndex[paramInt1] = false;
    this._alphabets[paramInt1] = null;
  }
  
  public void setAttributes(Attributes paramAttributes) {
    this._length = paramAttributes.getLength();
    if (this._length > 0) {
      if (this._length >= this._algorithmData.length)
        resizeNoCopy(); 
      byte b1 = 0;
      for (byte b2 = 0; b2 < this._length; b2++) {
        this._data[b1++] = paramAttributes.getURI(b2);
        this._data[b1++] = paramAttributes.getLocalName(b2);
        this._data[b1++] = paramAttributes.getQName(b2);
        this._data[b1++] = paramAttributes.getType(b2);
        this._data[b1++] = paramAttributes.getValue(b2);
        b1++;
        this._toIndex[b2] = false;
        this._alphabets[b2] = null;
      } 
    } 
  }
  
  public void setAttributes(EncodingAlgorithmAttributes paramEncodingAlgorithmAttributes) {
    this._length = paramEncodingAlgorithmAttributes.getLength();
    if (this._length > 0) {
      if (this._length >= this._algorithmData.length)
        resizeNoCopy(); 
      byte b1 = 0;
      for (byte b2 = 0; b2 < this._length; b2++) {
        this._data[b1++] = paramEncodingAlgorithmAttributes.getURI(b2);
        this._data[b1++] = paramEncodingAlgorithmAttributes.getLocalName(b2);
        this._data[b1++] = paramEncodingAlgorithmAttributes.getQName(b2);
        this._data[b1++] = paramEncodingAlgorithmAttributes.getType(b2);
        this._data[b1++] = paramEncodingAlgorithmAttributes.getValue(b2);
        this._data[b1++] = paramEncodingAlgorithmAttributes.getAlgorithmURI(b2);
        this._algorithmIds[b2] = paramEncodingAlgorithmAttributes.getAlgorithmIndex(b2);
        this._algorithmData[b2] = paramEncodingAlgorithmAttributes.getAlgorithmData(b2);
        this._toIndex[b2] = false;
        this._alphabets[b2] = null;
      } 
    } 
  }
  
  public final int getLength() { return this._length; }
  
  public final String getLocalName(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._data[paramInt * 6 + 1] : null; }
  
  public final String getQName(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._data[paramInt * 6 + 2] : null; }
  
  public final String getType(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._data[paramInt * 6 + 3] : null; }
  
  public final String getURI(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._data[paramInt * 6 + 0] : null; }
  
  public final String getValue(int paramInt) {
    if (paramInt >= 0 && paramInt < this._length) {
      String str = this._data[paramInt * 6 + 4];
      if (str != null)
        return str; 
    } else {
      return null;
    } 
    if (this._algorithmData[paramInt] == null || this._registeredEncodingAlgorithms == null)
      return null; 
    try {
      this._data[paramInt * 6 + 4] = convertEncodingAlgorithmDataToString(this._algorithmIds[paramInt], this._data[paramInt * 6 + 5], this._algorithmData[paramInt]).toString();
      return convertEncodingAlgorithmDataToString(this._algorithmIds[paramInt], this._data[paramInt * 6 + 5], this._algorithmData[paramInt]).toString();
    } catch (IOException iOException) {
      return null;
    } catch (FastInfosetException fastInfosetException) {
      return null;
    } 
  }
  
  public final int getIndex(String paramString) {
    for (byte b = 0; b < this._length; b++) {
      if (paramString.equals(this._data[b * 6 + 2]))
        return b; 
    } 
    return -1;
  }
  
  public final String getType(String paramString) {
    int i = getIndex(paramString);
    return (i >= 0) ? this._data[i * 6 + 3] : null;
  }
  
  public final String getValue(String paramString) {
    int i = getIndex(paramString);
    return (i >= 0) ? getValue(i) : null;
  }
  
  public final int getIndex(String paramString1, String paramString2) {
    for (byte b = 0; b < this._length; b++) {
      if (paramString2.equals(this._data[b * 6 + 1]) && paramString1.equals(this._data[b * 6 + 0]))
        return b; 
    } 
    return -1;
  }
  
  public final String getType(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i >= 0) ? this._data[i * 6 + 3] : null;
  }
  
  public final String getValue(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    return (i >= 0) ? getValue(i) : null;
  }
  
  public final String getAlgorithmURI(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._data[paramInt * 6 + 5] : null; }
  
  public final int getAlgorithmIndex(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._algorithmIds[paramInt] : -1; }
  
  public final Object getAlgorithmData(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._algorithmData[paramInt] : null; }
  
  public final String getAlpababet(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._alphabets[paramInt] : null; }
  
  public final boolean getToIndex(int paramInt) { return (paramInt >= 0 && paramInt < this._length) ? this._toIndex[paramInt] : false; }
  
  private final String replaceNull(String paramString) { return (paramString != null) ? paramString : ""; }
  
  private final void resizeNoCopy() {
    int i = this._length * 3 / 2 + 1;
    this._data = new String[i * 6];
    this._algorithmIds = new int[i];
    this._algorithmData = new Object[i];
  }
  
  private final void resize() {
    int i = this._length * 3 / 2 + 1;
    String[] arrayOfString1 = new String[i * 6];
    int[] arrayOfInt = new int[i];
    Object[] arrayOfObject = new Object[i];
    String[] arrayOfString2 = new String[i];
    boolean[] arrayOfBoolean = new boolean[i];
    System.arraycopy(this._data, 0, arrayOfString1, 0, this._length * 6);
    System.arraycopy(this._algorithmIds, 0, arrayOfInt, 0, this._length);
    System.arraycopy(this._algorithmData, 0, arrayOfObject, 0, this._length);
    System.arraycopy(this._alphabets, 0, arrayOfString2, 0, this._length);
    System.arraycopy(this._toIndex, 0, arrayOfBoolean, 0, this._length);
    this._data = arrayOfString1;
    this._algorithmIds = arrayOfInt;
    this._algorithmData = arrayOfObject;
    this._alphabets = arrayOfString2;
    this._toIndex = arrayOfBoolean;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\helpers\EncodingAlgorithmAttributesImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */