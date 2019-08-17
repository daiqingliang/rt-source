package java.net;

import java.util.Formatter;
import java.util.Locale;
import sun.net.util.IPAddressUtil;

class HostPortrange {
  String hostname;
  
  String scheme;
  
  int[] portrange;
  
  boolean wildcard;
  
  boolean literal;
  
  boolean ipv6;
  
  boolean ipv4;
  
  static final int PORT_MIN = 0;
  
  static final int PORT_MAX = 65535;
  
  static final int CASE_DIFF = -32;
  
  static final int[] HTTP_PORT = { 80, 80 };
  
  static final int[] HTTPS_PORT = { 443, 443 };
  
  static final int[] NO_PORT = { -1, -1 };
  
  boolean equals(HostPortrange paramHostPortrange) { return (this.hostname.equals(paramHostPortrange.hostname) && this.portrange[0] == paramHostPortrange.portrange[0] && this.portrange[1] == paramHostPortrange.portrange[1] && this.wildcard == paramHostPortrange.wildcard && this.literal == paramHostPortrange.literal); }
  
  public int hashCode() { return this.hostname.hashCode() + this.portrange[0] + this.portrange[1]; }
  
  HostPortrange(String paramString1, String paramString2) {
    String str = null;
    this.scheme = paramString1;
    if (paramString2.charAt(0) == '[') {
      String str1;
      this.ipv6 = this.literal = true;
      int i = paramString2.indexOf(']');
      if (i != -1) {
        str1 = paramString2.substring(1, i);
      } else {
        throw new IllegalArgumentException("invalid IPv6 address: " + paramString2);
      } 
      int j = paramString2.indexOf(':', i + 1);
      if (j != -1 && paramString2.length() > j)
        str = paramString2.substring(j + 1); 
      byte[] arrayOfByte = IPAddressUtil.textToNumericFormatV6(str1);
      if (arrayOfByte == null)
        throw new IllegalArgumentException("illegal IPv6 address"); 
      StringBuilder stringBuilder = new StringBuilder();
      Formatter formatter = new Formatter(stringBuilder, Locale.US);
      formatter.format("%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x", new Object[] { 
            Byte.valueOf(arrayOfByte[0]), Byte.valueOf(arrayOfByte[1]), Byte.valueOf(arrayOfByte[2]), Byte.valueOf(arrayOfByte[3]), Byte.valueOf(arrayOfByte[4]), Byte.valueOf(arrayOfByte[5]), Byte.valueOf(arrayOfByte[6]), Byte.valueOf(arrayOfByte[7]), Byte.valueOf(arrayOfByte[8]), Byte.valueOf(arrayOfByte[9]), 
            Byte.valueOf(arrayOfByte[10]), Byte.valueOf(arrayOfByte[11]), Byte.valueOf(arrayOfByte[12]), Byte.valueOf(arrayOfByte[13]), Byte.valueOf(arrayOfByte[14]), Byte.valueOf(arrayOfByte[15]) });
      this.hostname = stringBuilder.toString();
    } else {
      String str1;
      int i = paramString2.indexOf(':');
      if (i != -1 && paramString2.length() > i) {
        str1 = paramString2.substring(0, i);
        str = paramString2.substring(i + 1);
      } else {
        str1 = (i == -1) ? paramString2 : paramString2.substring(0, i);
      } 
      if (str1.lastIndexOf('*') > 0)
        throw new IllegalArgumentException("invalid host wildcard specification"); 
      if (str1.startsWith("*")) {
        this.wildcard = true;
        if (str1.equals("*")) {
          str1 = "";
        } else if (str1.startsWith("*.")) {
          str1 = toLowerCase(str1.substring(1));
        } else {
          throw new IllegalArgumentException("invalid host wildcard specification");
        } 
      } else {
        int j = str1.lastIndexOf('.');
        if (j != -1 && str1.length() > 1) {
          boolean bool = true;
          int k = j + 1;
          int m = str1.length();
          while (k < m) {
            char c = str1.charAt(k);
            if (c < '0' || c > '9') {
              bool = false;
              break;
            } 
            k++;
          } 
          this.ipv4 = this.literal = bool;
          if (bool) {
            byte[] arrayOfByte = IPAddressUtil.textToNumericFormatV4(str1);
            if (arrayOfByte == null)
              throw new IllegalArgumentException("illegal IPv4 address"); 
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder, Locale.US);
            formatter.format("%d.%d.%d.%d", new Object[] { Byte.valueOf(arrayOfByte[0]), Byte.valueOf(arrayOfByte[1]), Byte.valueOf(arrayOfByte[2]), Byte.valueOf(arrayOfByte[3]) });
            str1 = stringBuilder.toString();
          } else {
            str1 = toLowerCase(str1);
          } 
        } 
      } 
      this.hostname = str1;
    } 
    try {
      this.portrange = parsePort(str);
    } catch (Exception exception) {
      throw new IllegalArgumentException("invalid port range: " + str);
    } 
  }
  
  static String toLowerCase(String paramString) {
    int i = paramString.length();
    StringBuilder stringBuilder = null;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if ((c >= 'a' && c <= 'z') || c == '.') {
        if (stringBuilder != null)
          stringBuilder.append(c); 
      } else if ((c >= '0' && c <= '9') || c == '-') {
        if (stringBuilder != null)
          stringBuilder.append(c); 
      } else if (c >= 'A' && c <= 'Z') {
        if (stringBuilder == null) {
          stringBuilder = new StringBuilder(i);
          stringBuilder.append(paramString, 0, b);
        } 
        stringBuilder.append((char)(c - -32));
      } else {
        throw new IllegalArgumentException("Invalid characters in hostname");
      } 
    } 
    return (stringBuilder == null) ? paramString : stringBuilder.toString();
  }
  
  public boolean literal() { return this.literal; }
  
  public boolean ipv4Literal() { return this.ipv4; }
  
  public boolean ipv6Literal() { return this.ipv6; }
  
  public String hostname() { return this.hostname; }
  
  public int[] portrange() { return this.portrange; }
  
  public boolean wildcard() { return this.wildcard; }
  
  int[] defaultPort() { return this.scheme.equals("http") ? HTTP_PORT : (this.scheme.equals("https") ? HTTPS_PORT : NO_PORT); }
  
  int[] parsePort(String paramString) {
    if (paramString == null || paramString.equals(""))
      return defaultPort(); 
    if (paramString.equals("*"))
      return new int[] { 0, 65535 }; 
    try {
      int k;
      int j;
      int i = paramString.indexOf('-');
      if (i == -1) {
        int m = Integer.parseInt(paramString);
        return new int[] { m, m };
      } 
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      if (str1.equals("")) {
        j = 0;
      } else {
        j = Integer.parseInt(str1);
      } 
      if (str2.equals("")) {
        k = 65535;
      } else {
        k = Integer.parseInt(str2);
      } 
      return (j < 0 || k < 0 || k < j) ? defaultPort() : new int[] { j, k };
    } catch (IllegalArgumentException illegalArgumentException) {
      return defaultPort();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\HostPortrange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */