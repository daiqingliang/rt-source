package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.Serializable;
import java.util.Stack;
import java.util.StringTokenizer;
import org.w3c.dom.Element;

public class QName implements Serializable {
  static final long serialVersionUID = 467434581652829920L;
  
  protected String _localName;
  
  protected String _namespaceURI;
  
  protected String _prefix;
  
  public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
  
  private int m_hashCode;
  
  public QName() {}
  
  public QName(String paramString1, String paramString2) { this(paramString1, paramString2, false); }
  
  public QName(String paramString1, String paramString2, boolean paramBoolean) {
    if (paramString2 == null)
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null)); 
    if (paramBoolean && !XML11Char.isXML11ValidNCName(paramString2))
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null)); 
    this._namespaceURI = paramString1;
    this._localName = paramString2;
    this.m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString1, String paramString2, String paramString3) { this(paramString1, paramString2, paramString3, false); }
  
  public QName(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (paramString3 == null)
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null)); 
    if (paramBoolean) {
      if (!XML11Char.isXML11ValidNCName(paramString3))
        throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null)); 
      if (null != paramString2 && !XML11Char.isXML11ValidNCName(paramString2))
        throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_PREFIX_INVALID", null)); 
    } 
    this._namespaceURI = paramString1;
    this._prefix = paramString2;
    this._localName = paramString3;
    this.m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString) { this(paramString, false); }
  
  public QName(String paramString, boolean paramBoolean) {
    if (paramString == null)
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null)); 
    if (paramBoolean && !XML11Char.isXML11ValidNCName(paramString))
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null)); 
    this._namespaceURI = null;
    this._localName = paramString;
    this.m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString, Stack paramStack) { this(paramString, paramStack, false); }
  
  public QName(String paramString, Stack paramStack, boolean paramBoolean) {
    String str1 = null;
    String str2 = null;
    int i = paramString.indexOf(':');
    if (i > 0) {
      str2 = paramString.substring(0, i);
      if (str2.equals("xml")) {
        str1 = "http://www.w3.org/XML/1998/namespace";
      } else {
        if (str2.equals("xmlns"))
          return; 
        int j = paramStack.size();
        for (int k = j - 1; k >= 0; k--) {
          for (NameSpace nameSpace = (NameSpace)paramStack.elementAt(k); null != nameSpace; nameSpace = nameSpace.m_next) {
            if (null != nameSpace.m_prefix && str2.equals(nameSpace.m_prefix)) {
              str1 = nameSpace.m_uri;
              k = -1;
              break;
            } 
          } 
        } 
      } 
      if (null == str1)
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { str2 })); 
    } 
    this._localName = (i < 0) ? paramString : paramString.substring(i + 1);
    if (paramBoolean && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName)))
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null)); 
    this._namespaceURI = str1;
    this._prefix = str2;
    this.m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString, Element paramElement, PrefixResolver paramPrefixResolver) { this(paramString, paramElement, paramPrefixResolver, false); }
  
  public QName(String paramString, Element paramElement, PrefixResolver paramPrefixResolver, boolean paramBoolean) {
    this._namespaceURI = null;
    int i = paramString.indexOf(':');
    if (i > 0 && null != paramElement) {
      String str = paramString.substring(0, i);
      this._prefix = str;
      if (str.equals("xml")) {
        this._namespaceURI = "http://www.w3.org/XML/1998/namespace";
      } else {
        if (str.equals("xmlns"))
          return; 
        this._namespaceURI = paramPrefixResolver.getNamespaceForPrefix(str, paramElement);
      } 
      if (null == this._namespaceURI)
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { str })); 
    } 
    this._localName = (i < 0) ? paramString : paramString.substring(i + 1);
    if (paramBoolean && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName)))
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null)); 
    this.m_hashCode = toString().hashCode();
  }
  
  public QName(String paramString, PrefixResolver paramPrefixResolver) { this(paramString, paramPrefixResolver, false); }
  
  public QName(String paramString, PrefixResolver paramPrefixResolver, boolean paramBoolean) {
    String str = null;
    this._namespaceURI = null;
    int i = paramString.indexOf(':');
    if (i > 0) {
      str = paramString.substring(0, i);
      if (str.equals("xml")) {
        this._namespaceURI = "http://www.w3.org/XML/1998/namespace";
      } else {
        this._namespaceURI = paramPrefixResolver.getNamespaceForPrefix(str);
      } 
      if (null == this._namespaceURI)
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { str })); 
      this._localName = paramString.substring(i + 1);
    } else {
      if (i == 0)
        throw new RuntimeException(XMLMessages.createXMLMessage("ER_NAME_CANT_START_WITH_COLON", null)); 
      this._localName = paramString;
    } 
    if (paramBoolean && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName)))
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null)); 
    this.m_hashCode = toString().hashCode();
    this._prefix = str;
  }
  
  public String getNamespaceURI() { return this._namespaceURI; }
  
  public String getPrefix() { return this._prefix; }
  
  public String getLocalName() { return this._localName; }
  
  public String toString() { return (this._prefix != null) ? (this._prefix + ":" + this._localName) : ((this._namespaceURI != null) ? ("{" + this._namespaceURI + "}" + this._localName) : this._localName); }
  
  public String toNamespacedString() { return (this._namespaceURI != null) ? ("{" + this._namespaceURI + "}" + this._localName) : this._localName; }
  
  public String getNamespace() { return getNamespaceURI(); }
  
  public String getLocalPart() { return getLocalName(); }
  
  public int hashCode() { return this.m_hashCode; }
  
  public boolean equals(String paramString1, String paramString2) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getNamespaceURI : ()Ljava/lang/String;
    //   4: astore_3
    //   5: aload_0
    //   6: invokevirtual getLocalName : ()Ljava/lang/String;
    //   9: aload_2
    //   10: invokevirtual equals : (Ljava/lang/Object;)Z
    //   13: ifeq -> 51
    //   16: aconst_null
    //   17: aload_3
    //   18: if_acmpeq -> 37
    //   21: aconst_null
    //   22: aload_1
    //   23: if_acmpeq -> 37
    //   26: aload_3
    //   27: aload_1
    //   28: invokevirtual equals : (Ljava/lang/Object;)Z
    //   31: ifeq -> 51
    //   34: goto -> 47
    //   37: aconst_null
    //   38: aload_3
    //   39: if_acmpne -> 51
    //   42: aconst_null
    //   43: aload_1
    //   44: if_acmpne -> 51
    //   47: iconst_1
    //   48: goto -> 52
    //   51: iconst_0
    //   52: ireturn }
  
  public boolean equals(Object paramObject) { // Byte code:
    //   0: aload_1
    //   1: aload_0
    //   2: if_acmpne -> 7
    //   5: iconst_1
    //   6: ireturn
    //   7: aload_1
    //   8: instanceof com/sun/org/apache/xml/internal/utils/QName
    //   11: ifeq -> 84
    //   14: aload_1
    //   15: checkcast com/sun/org/apache/xml/internal/utils/QName
    //   18: astore_2
    //   19: aload_0
    //   20: invokevirtual getNamespaceURI : ()Ljava/lang/String;
    //   23: astore_3
    //   24: aload_2
    //   25: invokevirtual getNamespaceURI : ()Ljava/lang/String;
    //   28: astore #4
    //   30: aload_0
    //   31: invokevirtual getLocalName : ()Ljava/lang/String;
    //   34: aload_2
    //   35: invokevirtual getLocalName : ()Ljava/lang/String;
    //   38: invokevirtual equals : (Ljava/lang/Object;)Z
    //   41: ifeq -> 82
    //   44: aconst_null
    //   45: aload_3
    //   46: if_acmpeq -> 67
    //   49: aconst_null
    //   50: aload #4
    //   52: if_acmpeq -> 67
    //   55: aload_3
    //   56: aload #4
    //   58: invokevirtual equals : (Ljava/lang/Object;)Z
    //   61: ifeq -> 82
    //   64: goto -> 78
    //   67: aconst_null
    //   68: aload_3
    //   69: if_acmpne -> 82
    //   72: aconst_null
    //   73: aload #4
    //   75: if_acmpne -> 82
    //   78: iconst_1
    //   79: goto -> 83
    //   82: iconst_0
    //   83: ireturn
    //   84: iconst_0
    //   85: ireturn }
  
  public static QName getQNameFromString(String paramString) {
    QName qName;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "{}", false);
    String str1 = stringTokenizer.nextToken();
    String str2 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null;
    if (null == str2) {
      qName = new QName(null, str1);
    } else {
      qName = new QName(str1, str2);
    } 
    return qName;
  }
  
  public static boolean isXMLNSDecl(String paramString) { return (paramString.startsWith("xmlns") && (paramString.equals("xmlns") || paramString.startsWith("xmlns:"))); }
  
  public static String getPrefixFromXMLNSDecl(String paramString) {
    int i = paramString.indexOf(':');
    return (i >= 0) ? paramString.substring(i + 1) : "";
  }
  
  public static String getLocalPart(String paramString) {
    int i = paramString.indexOf(':');
    return (i < 0) ? paramString : paramString.substring(i + 1);
  }
  
  public static String getPrefixPart(String paramString) {
    int i = paramString.indexOf(':');
    return (i >= 0) ? paramString.substring(0, i) : "";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */