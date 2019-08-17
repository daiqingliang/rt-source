package com.sun.xml.internal.stream.buffer;

import org.xml.sax.Attributes;

public final class AttributesHolder implements Attributes {
  private static final int DEFAULT_CAPACITY = 8;
  
  private static final int ITEM_SIZE = 8;
  
  private static final int PREFIX = 0;
  
  private static final int URI = 1;
  
  private static final int LOCAL_NAME = 2;
  
  private static final int QNAME = 3;
  
  private static final int TYPE = 4;
  
  private static final int VALUE = 5;
  
  private int _attributeCount;
  
  private String[] _strings = new String[64];
  
  public final int getLength() { return this._attributeCount; }
  
  public final String getPrefix(int paramInt) { return (paramInt >= 0 && paramInt < this._attributeCount) ? this._strings[(paramInt << 3) + 0] : null; }
  
  public final String getLocalName(int paramInt) { return (paramInt >= 0 && paramInt < this._attributeCount) ? this._strings[(paramInt << 3) + 2] : null; }
  
  public final String getQName(int paramInt) { return (paramInt >= 0 && paramInt < this._attributeCount) ? this._strings[(paramInt << 3) + 3] : null; }
  
  public final String getType(int paramInt) { return (paramInt >= 0 && paramInt < this._attributeCount) ? this._strings[(paramInt << 3) + 4] : null; }
  
  public final String getURI(int paramInt) { return (paramInt >= 0 && paramInt < this._attributeCount) ? this._strings[(paramInt << 3) + 1] : null; }
  
  public final String getValue(int paramInt) { return (paramInt >= 0 && paramInt < this._attributeCount) ? this._strings[(paramInt << 3) + 5] : null; }
  
  public final int getIndex(String paramString) {
    for (byte b = 0; b < this._attributeCount; b++) {
      if (paramString.equals(this._strings[(b << 3) + 3]))
        return b; 
    } 
    return -1;
  }
  
  public final String getType(String paramString) {
    int i = (getIndex(paramString) << 3) + 4;
    return (i >= 0) ? this._strings[i] : null;
  }
  
  public final String getValue(String paramString) {
    int i = (getIndex(paramString) << 3) + 5;
    return (i >= 0) ? this._strings[i] : null;
  }
  
  public final int getIndex(String paramString1, String paramString2) {
    for (byte b = 0; b < this._attributeCount; b++) {
      if (paramString2.equals(this._strings[(b << 3) + 2]) && paramString1.equals(this._strings[(b << 3) + 1]))
        return b; 
    } 
    return -1;
  }
  
  public final String getType(String paramString1, String paramString2) {
    int i = (getIndex(paramString1, paramString2) << 3) + 4;
    return (i >= 0) ? this._strings[i] : null;
  }
  
  public final String getValue(String paramString1, String paramString2) {
    int i = (getIndex(paramString1, paramString2) << 3) + 5;
    return (i >= 0) ? this._strings[i] : null;
  }
  
  public final void clear() {
    if (this._attributeCount > 0) {
      for (byte b = 0; b < this._attributeCount; b++)
        this._strings[(b << 3) + 5] = null; 
      this._attributeCount = 0;
    } 
  }
  
  public final void addAttributeWithQName(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    int i = this._attributeCount << 3;
    if (i == this._strings.length)
      resize(i); 
    this._strings[i + 0] = null;
    this._strings[i + 1] = paramString1;
    this._strings[i + 2] = paramString2;
    this._strings[i + 3] = paramString3;
    this._strings[i + 4] = paramString4;
    this._strings[i + 5] = paramString5;
    this._attributeCount++;
  }
  
  public final void addAttributeWithPrefix(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    int i = this._attributeCount << 3;
    if (i == this._strings.length)
      resize(i); 
    this._strings[i + 0] = paramString1;
    this._strings[i + 1] = paramString2;
    this._strings[i + 2] = paramString3;
    this._strings[i + 3] = null;
    this._strings[i + 4] = paramString4;
    this._strings[i + 5] = paramString5;
    this._attributeCount++;
  }
  
  private void resize(int paramInt) {
    int i = paramInt * 2;
    String[] arrayOfString = new String[i];
    System.arraycopy(this._strings, 0, arrayOfString, 0, paramInt);
    this._strings = arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\AttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */