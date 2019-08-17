package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class URI implements Serializable {
  static final long serialVersionUID = 7096266377907081897L;
  
  private static final String RESERVED_CHARACTERS = ";/?:@&=+$,";
  
  private static final String MARK_CHARACTERS = "-_.!~*'() ";
  
  private static final String SCHEME_CHARACTERS = "+-.";
  
  private static final String USERINFO_CHARACTERS = ";:&=+$,";
  
  private String m_scheme = null;
  
  private String m_userinfo = null;
  
  private String m_host = null;
  
  private int m_port = -1;
  
  private String m_path = null;
  
  private String m_queryString = null;
  
  private String m_fragment = null;
  
  private static boolean DEBUG = false;
  
  public URI() {}
  
  public URI(URI paramURI) { initialize(paramURI); }
  
  public URI(String paramString) throws MalformedURIException { this((URI)null, paramString); }
  
  public URI(URI paramURI, String paramString) throws MalformedURIException { initialize(paramURI, paramString); }
  
  public URI(String paramString1, String paramString2) throws MalformedURIException {
    if (paramString1 == null || paramString1.trim().length() == 0)
      throw new MalformedURIException("Cannot construct URI with null/empty scheme!"); 
    if (paramString2 == null || paramString2.trim().length() == 0)
      throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!"); 
    setScheme(paramString1);
    setPath(paramString2);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws MalformedURIException { this(paramString1, null, paramString2, -1, paramString3, paramString4, paramString5); }
  
  public URI(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6) throws MalformedURIException {
    if (paramString1 == null || paramString1.trim().length() == 0)
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_SCHEME_REQUIRED", null)); 
    if (paramString3 == null) {
      if (paramString2 != null)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_USERINFO_IF_NO_HOST", null)); 
      if (paramInt != -1)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_PORT_IF_NO_HOST", null)); 
    } 
    if (paramString4 != null) {
      if (paramString4.indexOf('?') != -1 && paramString5 != null)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_QUERY_STRING_IN_PATH", null)); 
      if (paramString4.indexOf('#') != -1 && paramString6 != null)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_FRAGMENT_STRING_IN_PATH", null)); 
    } 
    setScheme(paramString1);
    setHost(paramString3);
    setPort(paramInt);
    setUserinfo(paramString2);
    setPath(paramString4);
    setQueryString(paramString5);
    setFragment(paramString6);
  }
  
  private void initialize(URI paramURI) {
    this.m_scheme = paramURI.getScheme();
    this.m_userinfo = paramURI.getUserinfo();
    this.m_host = paramURI.getHost();
    this.m_port = paramURI.getPort();
    this.m_path = paramURI.getPath();
    this.m_queryString = paramURI.getQueryString();
    this.m_fragment = paramURI.getFragment();
  }
  
  private void initialize(URI paramURI, String paramString) throws MalformedURIException {
    if (paramURI == null && (paramString == null || paramString.trim().length() == 0))
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_CANNOT_INIT_URI_EMPTY_PARMS", null)); 
    if (paramString == null || paramString.trim().length() == 0) {
      initialize(paramURI);
      return;
    } 
    String str = paramString.trim();
    int i = str.length();
    int j = 0;
    int k = str.indexOf(':');
    if (k < 0) {
      if (paramURI == null)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_SCHEME_IN_URI", new Object[] { str })); 
    } else {
      initializeScheme(str);
      str = str.substring(k + 1);
      if (this.m_scheme != null && paramURI != null && (str.startsWith("/") || !this.m_scheme.equals(paramURI.m_scheme) || !paramURI.getSchemeSpecificPart().startsWith("/")))
        paramURI = null; 
      i = str.length();
    } 
    if (j + true < i && str.substring(j).startsWith("//")) {
      j += 2;
      byte b = j;
      char c = Character.MIN_VALUE;
      while (j < i) {
        c = str.charAt(j);
        if (c == '/' || c == '?' || c == '#')
          break; 
        j++;
      } 
      if (j > b) {
        initializeAuthority(str.substring(b, j));
      } else {
        this.m_host = "";
      } 
    } 
    initializePath(str.substring(j));
    if (paramURI != null) {
      if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null) {
        this.m_scheme = paramURI.getScheme();
        this.m_userinfo = paramURI.getUserinfo();
        this.m_host = paramURI.getHost();
        this.m_port = paramURI.getPort();
        this.m_path = paramURI.getPath();
        if (this.m_queryString == null)
          this.m_queryString = paramURI.getQueryString(); 
        return;
      } 
      if (this.m_scheme == null)
        this.m_scheme = paramURI.getScheme(); 
      if (this.m_host == null) {
        this.m_userinfo = paramURI.getUserinfo();
        this.m_host = paramURI.getHost();
        this.m_port = paramURI.getPort();
      } else {
        return;
      } 
      if (this.m_path.length() > 0 && this.m_path.startsWith("/"))
        return; 
      String str1 = "";
      String str2 = paramURI.getPath();
      if (str2 != null) {
        int n = str2.lastIndexOf('/');
        if (n != -1)
          str1 = str2.substring(0, n + 1); 
      } 
      str1 = str1.concat(this.m_path);
      j = -1;
      while ((j = str1.indexOf("/./")) != -1)
        str1 = str1.substring(0, j + 1).concat(str1.substring(j + 3)); 
      if (str1.endsWith("/."))
        str1 = str1.substring(0, str1.length() - 1); 
      j = -1;
      int m = -1;
      String str3 = null;
      while ((j = str1.indexOf("/../")) > 0) {
        str3 = str1.substring(0, str1.indexOf("/../"));
        m = str3.lastIndexOf('/');
        if (m != -1 && !str3.substring(m++).equals(".."))
          str1 = str1.substring(0, m).concat(str1.substring(j + 4)); 
      } 
      if (str1.endsWith("/..")) {
        str3 = str1.substring(0, str1.length() - 3);
        m = str3.lastIndexOf('/');
        if (m != -1)
          str1 = str1.substring(0, m + 1); 
      } 
      this.m_path = str1;
    } 
  }
  
  private void initializeScheme(String paramString) throws MalformedURIException {
    int i = paramString.length();
    byte b = 0;
    String str = null;
    char c = Character.MIN_VALUE;
    while (b < i) {
      c = paramString.charAt(b);
      if (c == ':' || c == '/' || c == '?' || c == '#')
        break; 
      b++;
    } 
    str = paramString.substring(0, b);
    if (str.length() == 0)
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_NO_SCHEME_INURI", null)); 
    setScheme(str);
  }
  
  private void initializeAuthority(String paramString) throws MalformedURIException {
    byte b1 = 0;
    byte b2 = 0;
    int i = paramString.length();
    char c = Character.MIN_VALUE;
    String str1 = null;
    if (paramString.indexOf('@', b2) != -1) {
      while (b1 < i) {
        c = paramString.charAt(b1);
        if (c == '@')
          break; 
        b1++;
      } 
      str1 = paramString.substring(b2, b1);
      b1++;
    } 
    String str2 = null;
    b2 = b1;
    while (b1 < i) {
      c = paramString.charAt(b1);
      if (c == ':')
        break; 
      b1++;
    } 
    str2 = paramString.substring(b2, b1);
    int j = -1;
    if (str2.length() > 0 && c == ':') {
      b2 = ++b1;
      while (b1 < i)
        b1++; 
      String str = paramString.substring(b2, b1);
      if (str.length() > 0) {
        for (b = 0; b < str.length(); b++) {
          if (!isDigit(str.charAt(b)))
            throw new MalformedURIException(str + " is invalid. Port should only contain digits!"); 
        } 
        try {
          j = Integer.parseInt(str);
        } catch (NumberFormatException b) {
          NumberFormatException numberFormatException;
        } 
      } 
    } 
    setHost(str2);
    setPort(j);
    setUserinfo(str1);
  }
  
  private void initializePath(String paramString) throws MalformedURIException {
    if (paramString == null)
      throw new MalformedURIException("Cannot initialize path from null string!"); 
    byte b1 = 0;
    byte b2 = 0;
    int i = paramString.length();
    char c = Character.MIN_VALUE;
    while (b1 < i) {
      c = paramString.charAt(b1);
      if (c == '?' || c == '#')
        break; 
      if (c == '%') {
        if (b1 + 2 >= i || !isHex(paramString.charAt(b1 + 1)) || !isHex(paramString.charAt(b1 + 2)))
          throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", null)); 
      } else if (!isReservedCharacter(c) && !isUnreservedCharacter(c) && '\\' != c) {
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PATH_INVALID_CHAR", new Object[] { String.valueOf(c) }));
      } 
      b1++;
    } 
    this.m_path = paramString.substring(b2, b1);
    if (c == '?') {
      b2 = ++b1;
      while (b1 < i) {
        c = paramString.charAt(b1);
        if (c == '#')
          break; 
        if (c == '%') {
          if (b1 + 2 >= i || !isHex(paramString.charAt(b1 + 1)) || !isHex(paramString.charAt(b1 + 2)))
            throw new MalformedURIException("Query string contains invalid escape sequence!"); 
        } else if (!isReservedCharacter(c) && !isUnreservedCharacter(c)) {
          throw new MalformedURIException("Query string contains invalid character:" + c);
        } 
        b1++;
      } 
      this.m_queryString = paramString.substring(b2, b1);
    } 
    if (c == '#') {
      b2 = ++b1;
      while (b1 < i) {
        c = paramString.charAt(b1);
        if (c == '%') {
          if (b1 + 2 >= i || !isHex(paramString.charAt(b1 + 1)) || !isHex(paramString.charAt(b1 + 2)))
            throw new MalformedURIException("Fragment contains invalid escape sequence!"); 
        } else if (!isReservedCharacter(c) && !isUnreservedCharacter(c)) {
          throw new MalformedURIException("Fragment contains invalid character:" + c);
        } 
        b1++;
      } 
      this.m_fragment = paramString.substring(b2, b1);
    } 
  }
  
  public String getScheme() { return this.m_scheme; }
  
  public String getSchemeSpecificPart() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.m_userinfo != null || this.m_host != null || this.m_port != -1)
      stringBuilder.append("//"); 
    if (this.m_userinfo != null) {
      stringBuilder.append(this.m_userinfo);
      stringBuilder.append('@');
    } 
    if (this.m_host != null)
      stringBuilder.append(this.m_host); 
    if (this.m_port != -1) {
      stringBuilder.append(':');
      stringBuilder.append(this.m_port);
    } 
    if (this.m_path != null)
      stringBuilder.append(this.m_path); 
    if (this.m_queryString != null) {
      stringBuilder.append('?');
      stringBuilder.append(this.m_queryString);
    } 
    if (this.m_fragment != null) {
      stringBuilder.append('#');
      stringBuilder.append(this.m_fragment);
    } 
    return stringBuilder.toString();
  }
  
  public String getUserinfo() { return this.m_userinfo; }
  
  public String getHost() { return this.m_host; }
  
  public int getPort() { return this.m_port; }
  
  public String getPath(boolean paramBoolean1, boolean paramBoolean2) {
    StringBuilder stringBuilder = new StringBuilder(this.m_path);
    if (paramBoolean1 && this.m_queryString != null) {
      stringBuilder.append('?');
      stringBuilder.append(this.m_queryString);
    } 
    if (paramBoolean2 && this.m_fragment != null) {
      stringBuilder.append('#');
      stringBuilder.append(this.m_fragment);
    } 
    return stringBuilder.toString();
  }
  
  public String getPath() { return this.m_path; }
  
  public String getQueryString() { return this.m_queryString; }
  
  public String getFragment() { return this.m_fragment; }
  
  public void setScheme(String paramString) throws MalformedURIException {
    if (paramString == null)
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_SCHEME_FROM_NULL_STRING", null)); 
    if (!isConformantSchemeName(paramString))
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_SCHEME_NOT_CONFORMANT", null)); 
    this.m_scheme = paramString.toLowerCase();
  }
  
  public void setUserinfo(String paramString) throws MalformedURIException {
    if (paramString == null) {
      this.m_userinfo = null;
    } else {
      if (this.m_host == null)
        throw new MalformedURIException("Userinfo cannot be set when host is null!"); 
      byte b = 0;
      int i = paramString.length();
      char c = Character.MIN_VALUE;
      while (b < i) {
        c = paramString.charAt(b);
        if (c == '%') {
          if (b + 2 >= i || !isHex(paramString.charAt(b + 1)) || !isHex(paramString.charAt(b + 2)))
            throw new MalformedURIException("Userinfo contains invalid escape sequence!"); 
        } else if (!isUnreservedCharacter(c) && ";:&=+$,".indexOf(c) == -1) {
          throw new MalformedURIException("Userinfo contains invalid character:" + c);
        } 
        b++;
      } 
    } 
    this.m_userinfo = paramString;
  }
  
  public void setHost(String paramString) throws MalformedURIException {
    if (paramString == null || paramString.trim().length() == 0) {
      this.m_host = paramString;
      this.m_userinfo = null;
      this.m_port = -1;
    } else if (!isWellFormedAddress(paramString)) {
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_HOST_ADDRESS_NOT_WELLFORMED", null));
    } 
    this.m_host = paramString;
  }
  
  public void setPort(int paramInt) throws MalformedURIException {
    if (paramInt >= 0 && paramInt <= 65535) {
      if (this.m_host == null)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PORT_WHEN_HOST_NULL", null)); 
    } else if (paramInt != -1) {
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_INVALID_PORT", null));
    } 
    this.m_port = paramInt;
  }
  
  public void setPath(String paramString) throws MalformedURIException {
    if (paramString == null) {
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
    } else {
      initializePath(paramString);
    } 
  }
  
  public void appendPath(String paramString) throws MalformedURIException {
    if (paramString == null || paramString.trim().length() == 0)
      return; 
    if (!isURIString(paramString))
      throw new MalformedURIException(XMLMessages.createXMLMessage("ER_PATH_INVALID_CHAR", new Object[] { paramString })); 
    if (this.m_path == null || this.m_path.trim().length() == 0) {
      if (paramString.startsWith("/")) {
        this.m_path = paramString;
      } else {
        this.m_path = "/" + paramString;
      } 
    } else if (this.m_path.endsWith("/")) {
      if (paramString.startsWith("/")) {
        this.m_path = this.m_path.concat(paramString.substring(1));
      } else {
        this.m_path = this.m_path.concat(paramString);
      } 
    } else if (paramString.startsWith("/")) {
      this.m_path = this.m_path.concat(paramString);
    } else {
      this.m_path = this.m_path.concat("/" + paramString);
    } 
  }
  
  public void setQueryString(String paramString) throws MalformedURIException {
    if (paramString == null) {
      this.m_queryString = null;
    } else {
      if (!isGenericURI())
        throw new MalformedURIException("Query string can only be set for a generic URI!"); 
      if (getPath() == null)
        throw new MalformedURIException("Query string cannot be set when path is null!"); 
      if (!isURIString(paramString))
        throw new MalformedURIException("Query string contains invalid character!"); 
      this.m_queryString = paramString;
    } 
  }
  
  public void setFragment(String paramString) throws MalformedURIException {
    if (paramString == null) {
      this.m_fragment = null;
    } else {
      if (!isGenericURI())
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_FRAG_FOR_GENERIC_URI", null)); 
      if (getPath() == null)
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_FRAG_WHEN_PATH_NULL", null)); 
      if (!isURIString(paramString))
        throw new MalformedURIException(XMLMessages.createXMLMessage("ER_FRAG_INVALID_CHAR", null)); 
      this.m_fragment = paramString;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof URI) {
      URI uRI = (URI)paramObject;
      if (((this.m_scheme == null && uRI.m_scheme == null) || (this.m_scheme != null && uRI.m_scheme != null && this.m_scheme.equals(uRI.m_scheme))) && ((this.m_userinfo == null && uRI.m_userinfo == null) || (this.m_userinfo != null && uRI.m_userinfo != null && this.m_userinfo.equals(uRI.m_userinfo))) && ((this.m_host == null && uRI.m_host == null) || (this.m_host != null && uRI.m_host != null && this.m_host.equals(uRI.m_host))) && this.m_port == uRI.m_port && ((this.m_path == null && uRI.m_path == null) || (this.m_path != null && uRI.m_path != null && this.m_path.equals(uRI.m_path))) && ((this.m_queryString == null && uRI.m_queryString == null) || (this.m_queryString != null && uRI.m_queryString != null && this.m_queryString.equals(uRI.m_queryString))) && ((this.m_fragment == null && uRI.m_fragment == null) || (this.m_fragment != null && uRI.m_fragment != null && this.m_fragment.equals(uRI.m_fragment))))
        return true; 
    } 
    return false;
  }
  
  public int hashCode() {
    null = 7;
    null = 59 * null + Objects.hashCode(this.m_scheme);
    null = 59 * null + Objects.hashCode(this.m_userinfo);
    null = 59 * null + Objects.hashCode(this.m_host);
    null = 59 * null + this.m_port;
    null = 59 * null + Objects.hashCode(this.m_path);
    null = 59 * null + Objects.hashCode(this.m_queryString);
    return 59 * null + Objects.hashCode(this.m_fragment);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.m_scheme != null) {
      stringBuilder.append(this.m_scheme);
      stringBuilder.append(':');
    } 
    stringBuilder.append(getSchemeSpecificPart());
    return stringBuilder.toString();
  }
  
  public boolean isGenericURI() { return (this.m_host != null); }
  
  public static boolean isConformantSchemeName(String paramString) {
    if (paramString == null || paramString.trim().length() == 0)
      return false; 
    if (!isAlpha(paramString.charAt(0)))
      return false; 
    for (byte b = 1; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (!isAlphanum(c) && "+-.".indexOf(c) == -1)
        return false; 
    } 
    return true;
  }
  
  public static boolean isWellFormedAddress(String paramString) {
    if (paramString == null)
      return false; 
    String str = paramString.trim();
    int i = str.length();
    if (i == 0 || i > 255)
      return false; 
    if (str.startsWith(".") || str.startsWith("-"))
      return false; 
    int j = str.lastIndexOf('.');
    if (str.endsWith("."))
      j = str.substring(0, j).lastIndexOf('.'); 
    if (j + 1 < i && isDigit(paramString.charAt(j + 1))) {
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        char c = str.charAt(b2);
        if (c == '.') {
          if (!isDigit(str.charAt(b2 - 1)) || (b2 + 1 < i && !isDigit(str.charAt(b2 + 1))))
            return false; 
          b1++;
        } else if (!isDigit(c)) {
          return false;
        } 
      } 
      if (b1 != 3)
        return false; 
    } else {
      for (byte b = 0; b < i; b++) {
        char c = str.charAt(b);
        if (c == '.') {
          if (!isAlphanum(str.charAt(b - 1)))
            return false; 
          if (b + 1 < i && !isAlphanum(str.charAt(b + 1)))
            return false; 
        } else if (!isAlphanum(c) && c != '-') {
          return false;
        } 
      } 
    } 
    return true;
  }
  
  private static boolean isDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  private static boolean isHex(char paramChar) { return (isDigit(paramChar) || (paramChar >= 'a' && paramChar <= 'f') || (paramChar >= 'A' && paramChar <= 'F')); }
  
  private static boolean isAlpha(char paramChar) { return ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z')); }
  
  private static boolean isAlphanum(char paramChar) { return (isAlpha(paramChar) || isDigit(paramChar)); }
  
  private static boolean isReservedCharacter(char paramChar) { return (";/?:@&=+$,".indexOf(paramChar) != -1); }
  
  private static boolean isUnreservedCharacter(char paramChar) { return (isAlphanum(paramChar) || "-_.!~*'() ".indexOf(paramChar) != -1); }
  
  private static boolean isURIString(String paramString) {
    if (paramString == null)
      return false; 
    int i = paramString.length();
    char c = Character.MIN_VALUE;
    for (byte b = 0; b < i; b++) {
      c = paramString.charAt(b);
      if (c == '%') {
        if (b + 2 >= i || !isHex(paramString.charAt(b + 1)) || !isHex(paramString.charAt(b + 2)))
          return false; 
        b += 2;
      } else if (!isReservedCharacter(c) && !isUnreservedCharacter(c)) {
        return false;
      } 
    } 
    return true;
  }
  
  public static class MalformedURIException extends IOException {
    public MalformedURIException() {}
    
    public MalformedURIException(String param1String) throws MalformedURIException { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\URI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */