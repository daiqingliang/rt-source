package com.sun.xml.internal.fastinfoset;

import javax.xml.namespace.QName;

public class QualifiedName {
  public String prefix;
  
  public String namespaceName;
  
  public String localName;
  
  public String qName;
  
  public int index;
  
  public int prefixIndex;
  
  public int namespaceNameIndex;
  
  public int localNameIndex;
  
  public int attributeId;
  
  public int attributeHash;
  
  private QName qNameObject;
  
  public QualifiedName() {}
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = paramString4;
    this.index = -1;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
  }
  
  public void set(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = paramString4;
    this.index = -1;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
    this.qNameObject = null;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = paramString4;
    this.index = paramInt;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = paramString4;
    this.index = paramInt;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
    this.qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = paramString4;
    this.index = paramInt1;
    this.prefixIndex = paramInt2 + 1;
    this.namespaceNameIndex = paramInt3 + 1;
    this.localNameIndex = paramInt4;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = paramString4;
    this.index = paramInt1;
    this.prefixIndex = paramInt2 + 1;
    this.namespaceNameIndex = paramInt3 + 1;
    this.localNameIndex = paramInt4;
    this.qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = createQNameString(paramString1, paramString3);
    this.index = -1;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = createQNameString(paramString1, paramString3);
    this.index = -1;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
    this.qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    if (paramArrayOfChar != null) {
      int i = paramString1.length();
      int j = paramString3.length();
      int k = i + j + 1;
      if (k < paramArrayOfChar.length) {
        paramString1.getChars(0, i, paramArrayOfChar, 0);
        paramArrayOfChar[i] = ':';
        paramString3.getChars(0, j, paramArrayOfChar, i + 1);
        this.qName = new String(paramArrayOfChar, 0, k);
      } else {
        this.qName = createQNameString(paramString1, paramString3);
      } 
    } else {
      this.qName = this.localName;
    } 
    this.prefixIndex = paramInt1 + 1;
    this.namespaceNameIndex = paramInt2 + 1;
    this.localNameIndex = paramInt3;
    this.index = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, char[] paramArrayOfChar) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    if (paramArrayOfChar != null) {
      int i = paramString1.length();
      int j = paramString3.length();
      int k = i + j + 1;
      if (k < paramArrayOfChar.length) {
        paramString1.getChars(0, i, paramArrayOfChar, 0);
        paramArrayOfChar[i] = ':';
        paramString3.getChars(0, j, paramArrayOfChar, i + 1);
        this.qName = new String(paramArrayOfChar, 0, k);
      } else {
        this.qName = createQNameString(paramString1, paramString3);
      } 
    } else {
      this.qName = this.localName;
    } 
    this.prefixIndex = paramInt1 + 1;
    this.namespaceNameIndex = paramInt2 + 1;
    this.localNameIndex = paramInt3;
    this.index = -1;
    this.qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, int paramInt) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = createQNameString(paramString1, paramString3);
    this.index = paramInt;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, int paramInt) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = createQNameString(paramString1, paramString3);
    this.index = paramInt;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
    this.qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = createQNameString(paramString1, paramString3);
    this.index = paramInt1;
    this.prefixIndex = paramInt2 + 1;
    this.namespaceNameIndex = paramInt3 + 1;
    this.localNameIndex = paramInt4;
  }
  
  public final QualifiedName set(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = paramString3;
    this.qName = createQNameString(paramString1, paramString3);
    this.index = paramInt1;
    this.prefixIndex = paramInt2 + 1;
    this.namespaceNameIndex = paramInt3 + 1;
    this.localNameIndex = paramInt4;
    this.qNameObject = null;
    return this;
  }
  
  public QualifiedName(String paramString1, String paramString2) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = "";
    this.qName = "";
    this.index = -1;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
  }
  
  public final QualifiedName set(String paramString1, String paramString2) {
    this.prefix = paramString1;
    this.namespaceName = paramString2;
    this.localName = "";
    this.qName = "";
    this.index = -1;
    this.prefixIndex = 0;
    this.namespaceNameIndex = 0;
    this.localNameIndex = -1;
    this.qNameObject = null;
    return this;
  }
  
  public final QName getQName() {
    if (this.qNameObject == null)
      this.qNameObject = new QName(this.namespaceName, this.localName, this.prefix); 
    return this.qNameObject;
  }
  
  public final String getQNameString() { return (this.qName != "") ? this.qName : (this.qName = createQNameString(this.prefix, this.localName)); }
  
  public final void createAttributeValues(int paramInt) {
    this.attributeId = this.localNameIndex | this.namespaceNameIndex << 20;
    this.attributeHash = this.localNameIndex % paramInt;
  }
  
  private final String createQNameString(String paramString1, String paramString2) {
    if (paramString1 != null && paramString1.length() > 0) {
      StringBuffer stringBuffer = new StringBuffer(paramString1);
      stringBuffer.append(':');
      stringBuffer.append(paramString2);
      return stringBuffer.toString();
    } 
    return paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\QualifiedName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */