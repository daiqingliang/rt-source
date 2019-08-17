package javax.management.remote;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.StringTokenizer;

public class JMXServiceURL implements Serializable {
  private static final long serialVersionUID = 8173364409860779292L;
  
  private static final String INVALID_INSTANCE_MSG = "Trying to deserialize an invalid instance of JMXServiceURL";
  
  private static final Exception randomException = new Exception();
  
  private static final BitSet alphaBitSet = new BitSet(128);
  
  private static final BitSet numericBitSet = new BitSet(128);
  
  private static final BitSet alphaNumericBitSet = new BitSet(128);
  
  private static final BitSet protocolBitSet = new BitSet(128);
  
  private static final BitSet hostNameBitSet = new BitSet(128);
  
  private String protocol;
  
  private String host;
  
  private int port;
  
  private String urlPath;
  
  private String toString;
  
  private static final ClassLogger logger;
  
  public JMXServiceURL(String paramString) throws MalformedURLException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c < ' ' || c >= '')
        throw new MalformedURLException("Service URL contains non-ASCII character 0x" + Integer.toHexString(c)); 
    } 
    int j = "service:jmx:".length();
    if (!paramString.regionMatches(true, 0, "service:jmx:", 0, j))
      throw new MalformedURLException("Service URL must start with service:jmx:"); 
    int k = j;
    int m = indexOf(paramString, ':', k);
    this.protocol = paramString.substring(k, m).toLowerCase();
    if (!paramString.regionMatches(m, "://", 0, 3))
      throw new MalformedURLException("Missing \"://\" after protocol name"); 
    int n = m + 3;
    if (n < i && paramString.charAt(n) == '[') {
      i1 = paramString.indexOf(']', n) + 1;
      if (i1 == 0)
        throw new MalformedURLException("Bad host name: [ without ]"); 
      this.host = paramString.substring(n + 1, i1 - 1);
      if (!isNumericIPv6Address(this.host))
        throw new MalformedURLException("Address inside [...] must be numeric IPv6 address"); 
    } else {
      i1 = indexOfFirstNotInSet(paramString, hostNameBitSet, n);
      this.host = paramString.substring(n, i1);
    } 
    if (i1 < i && paramString.charAt(i1) == ':') {
      if (this.host.length() == 0)
        throw new MalformedURLException("Cannot give port number without host name"); 
      int i4 = i1 + 1;
      i2 = indexOfFirstNotInSet(paramString, numericBitSet, i4);
      String str = paramString.substring(i4, i2);
      try {
        this.port = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        throw new MalformedURLException("Bad port number: \"" + str + "\": " + numberFormatException);
      } 
    } else {
      i2 = i1;
      this.port = 0;
    } 
    int i3 = i2;
    if (i3 < i) {
      this.urlPath = paramString.substring(i3);
    } else {
      this.urlPath = "";
    } 
    validate();
  }
  
  public JMXServiceURL(String paramString1, String paramString2, int paramInt) throws MalformedURLException { this(paramString1, paramString2, paramInt, null); }
  
  public JMXServiceURL(String paramString1, String paramString2, int paramInt, String paramString3) throws MalformedURLException {
    if (paramString1 == null)
      paramString1 = "jmxmp"; 
    if (paramString2 == null) {
      InetAddress inetAddress;
      try {
        inetAddress = InetAddress.getLocalHost();
      } catch (UnknownHostException unknownHostException) {
        throw new MalformedURLException("Local host name unknown: " + unknownHostException);
      } 
      paramString2 = inetAddress.getHostName();
      try {
        validateHost(paramString2, paramInt);
      } catch (MalformedURLException malformedURLException) {
        if (logger.fineOn())
          logger.fine("JMXServiceURL", "Replacing illegal local host name " + paramString2 + " with numeric IP address (see RFC 1034)", malformedURLException); 
        paramString2 = inetAddress.getHostAddress();
      } 
    } 
    if (paramString2.startsWith("[")) {
      if (!paramString2.endsWith("]"))
        throw new MalformedURLException("Host starts with [ but does not end with ]"); 
      paramString2 = paramString2.substring(1, paramString2.length() - 1);
      if (!isNumericIPv6Address(paramString2))
        throw new MalformedURLException("Address inside [...] must be numeric IPv6 address"); 
      if (paramString2.startsWith("["))
        throw new MalformedURLException("More than one [[...]]"); 
    } 
    this.protocol = paramString1.toLowerCase();
    this.host = paramString2;
    this.port = paramInt;
    if (paramString3 == null)
      paramString3 = ""; 
    this.urlPath = paramString3;
    validate();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str1 = (String)getField.get("host", null);
    int i = getField.get("port", -1);
    String str2 = (String)getField.get("protocol", null);
    String str3 = (String)getField.get("urlPath", null);
    if (str2 == null || str3 == null || str1 == null) {
      StringBuilder stringBuilder = (new StringBuilder("Trying to deserialize an invalid instance of JMXServiceURL")).append('[');
      boolean bool = true;
      if (str2 == null) {
        stringBuilder.append("protocol=null");
        bool = false;
      } 
      if (str1 == null) {
        stringBuilder.append(bool ? "" : ",").append("host=null");
        bool = false;
      } 
      if (str3 == null)
        stringBuilder.append(bool ? "" : ",").append("urlPath=null"); 
      stringBuilder.append(']');
      throw new InvalidObjectException(stringBuilder.toString());
    } 
    if (str1.contains("[") || str1.contains("]"))
      throw new InvalidObjectException("Invalid host name: " + str1); 
    try {
      validate(str2, str1, i, str3);
      this.protocol = str2;
      this.host = str1;
      this.port = i;
      this.urlPath = str3;
    } catch (MalformedURLException malformedURLException) {
      throw new InvalidObjectException("Trying to deserialize an invalid instance of JMXServiceURL: " + malformedURLException.getMessage());
    } 
  }
  
  private void validate(String paramString1, String paramString2, int paramInt, String paramString3) throws MalformedURLException {
    int i = indexOfFirstNotInSet(paramString1, protocolBitSet, 0);
    if (i == 0 || i < paramString1.length() || !alphaBitSet.get(paramString1.charAt(0)))
      throw new MalformedURLException("Missing or invalid protocol name: \"" + paramString1 + "\""); 
    validateHost(paramString2, paramInt);
    if (paramInt < 0)
      throw new MalformedURLException("Bad port: " + paramInt); 
    if (paramString3.length() > 0 && !paramString3.startsWith("/") && !paramString3.startsWith(";"))
      throw new MalformedURLException("Bad URL path: " + paramString3); 
  }
  
  private void validate() throws MalformedURLException { validate(this.protocol, this.host, this.port, this.urlPath); }
  
  private static void validateHost(String paramString, int paramInt) throws MalformedURLException {
    if (paramString.length() == 0) {
      if (paramInt != 0)
        throw new MalformedURLException("Cannot give port number without host name"); 
      return;
    } 
    if (isNumericIPv6Address(paramString)) {
      try {
        InetAddress.getByName(paramString);
      } catch (Exception exception) {
        MalformedURLException malformedURLException = new MalformedURLException("Bad IPv6 address: " + paramString);
        EnvHelp.initCause(malformedURLException, exception);
        throw malformedURLException;
      } 
    } else {
      int i = paramString.length();
      byte b1 = 46;
      boolean bool = false;
      char c = Character.MIN_VALUE;
      for (b2 = 0; b2 < i; b2++) {
        char c1 = paramString.charAt(b2);
        boolean bool1 = alphaNumericBitSet.get(c1);
        if (b1 == 46)
          c = c1; 
        if (bool1) {
          b1 = 97;
        } else if (c1 == '-') {
          if (b1 == 46)
            break; 
          b1 = 45;
        } else if (c1 == '.') {
          bool = true;
          if (b1 != 97)
            break; 
          b1 = 46;
        } else {
          b1 = 46;
          break;
        } 
      } 
      try {
        if (b1 != 97)
          throw randomException; 
        if (bool && !alphaBitSet.get(c)) {
          StringTokenizer stringTokenizer = new StringTokenizer(paramString, ".", true);
          for (byte b = 0; b < 4; b++) {
            String str = stringTokenizer.nextToken();
            int j = Integer.parseInt(str);
            if (j < 0 || j > 255)
              throw randomException; 
            if (b < 3 && !stringTokenizer.nextToken().equals("."))
              throw randomException; 
          } 
          if (stringTokenizer.hasMoreTokens())
            throw randomException; 
        } 
      } catch (Exception b2) {
        Exception exception;
        throw new MalformedURLException("Bad host: \"" + paramString + "\"");
      } 
    } 
  }
  
  public String getProtocol() { return this.protocol; }
  
  public String getHost() { return this.host; }
  
  public int getPort() { return this.port; }
  
  public String getURLPath() { return this.urlPath; }
  
  public String toString() {
    if (this.toString != null)
      return this.toString; 
    StringBuilder stringBuilder = new StringBuilder("service:jmx:");
    stringBuilder.append(getProtocol()).append("://");
    String str = getHost();
    if (isNumericIPv6Address(str)) {
      stringBuilder.append('[').append(str).append(']');
    } else {
      stringBuilder.append(str);
    } 
    int i = getPort();
    if (i != 0)
      stringBuilder.append(':').append(i); 
    stringBuilder.append(getURLPath());
    this.toString = stringBuilder.toString();
    return this.toString;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof JMXServiceURL))
      return false; 
    JMXServiceURL jMXServiceURL = (JMXServiceURL)paramObject;
    return (jMXServiceURL.getProtocol().equalsIgnoreCase(getProtocol()) && jMXServiceURL.getHost().equalsIgnoreCase(getHost()) && jMXServiceURL.getPort() == getPort() && jMXServiceURL.getURLPath().equals(getURLPath()));
  }
  
  public int hashCode() { return toString().hashCode(); }
  
  private static boolean isNumericIPv6Address(String paramString) { return (paramString.indexOf(':') >= 0); }
  
  private static int indexOf(String paramString, char paramChar, int paramInt) {
    int i = paramString.indexOf(paramChar, paramInt);
    return (i < 0) ? paramString.length() : i;
  }
  
  private static int indexOfFirstNotInSet(String paramString, BitSet paramBitSet, int paramInt) {
    int i = paramString.length();
    int j;
    for (j = paramInt; j < i; j++) {
      char c = paramString.charAt(j);
      if (c >= '' || !paramBitSet.get(c))
        break; 
    } 
    return j;
  }
  
  static  {
    char c;
    for (c = '0'; c <= '9'; c = (char)(c + 1))
      numericBitSet.set(c); 
    for (c = 'A'; c <= 'Z'; c = (char)(c + '\001'))
      alphaBitSet.set(c); 
    for (c = 'a'; c <= 'z'; c = (char)(c + '\001'))
      alphaBitSet.set(c); 
    alphaNumericBitSet.or(alphaBitSet);
    alphaNumericBitSet.or(numericBitSet);
    protocolBitSet.or(alphaNumericBitSet);
    protocolBitSet.set(43);
    protocolBitSet.set(45);
    hostNameBitSet.or(alphaNumericBitSet);
    hostNameBitSet.set(45);
    hostNameBitSet.set(46);
    logger = new ClassLogger("javax.management.remote.misc", "JMXServiceURL");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXServiceURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */