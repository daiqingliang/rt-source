package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.net.ApplicationProxy;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;

public final class URL implements Serializable {
  static final String BUILTIN_HANDLERS_PREFIX = "sun.net.www.protocol";
  
  static final long serialVersionUID = -7627629688361524110L;
  
  private static final String protocolPathProp = "java.protocol.handler.pkgs";
  
  private String protocol;
  
  private String host;
  
  private int port = -1;
  
  private String file;
  
  private String query;
  
  private String authority;
  
  private String path;
  
  private String userInfo;
  
  private String ref;
  
  InetAddress hostAddress;
  
  URLStreamHandler handler;
  
  private int hashCode = -1;
  
  private UrlDeserializedState tempState;
  
  static URLStreamHandlerFactory factory;
  
  static Hashtable<String, URLStreamHandler> handlers = new Hashtable();
  
  private static Object streamHandlerLock = new Object();
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("protocol", String.class), new ObjectStreamField("host", String.class), new ObjectStreamField("port", int.class), new ObjectStreamField("authority", String.class), new ObjectStreamField("file", String.class), new ObjectStreamField("ref", String.class), new ObjectStreamField("hashCode", int.class) };
  
  public URL(String paramString1, String paramString2, int paramInt, String paramString3) throws MalformedURLException { this(paramString1, paramString2, paramInt, paramString3, null); }
  
  public URL(String paramString1, String paramString2, String paramString3) throws MalformedURLException { this(paramString1, paramString2, -1, paramString3); }
  
  public URL(String paramString1, String paramString2, int paramInt, String paramString3, URLStreamHandler paramURLStreamHandler) throws MalformedURLException {
    if (paramURLStreamHandler != null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        checkSpecifyHandler(securityManager); 
    } 
    paramString1 = paramString1.toLowerCase();
    this.protocol = paramString1;
    if (paramString2 != null) {
      if (paramString2.indexOf(':') >= 0 && !paramString2.startsWith("["))
        paramString2 = "[" + paramString2 + "]"; 
      this.host = paramString2;
      if (paramInt < -1)
        throw new MalformedURLException("Invalid port number :" + paramInt); 
      this.port = paramInt;
      this.authority = (paramInt == -1) ? paramString2 : (paramString2 + ":" + paramInt);
    } 
    Parts parts = new Parts(paramString3);
    this.path = parts.getPath();
    this.query = parts.getQuery();
    if (this.query != null) {
      this.file = this.path + "?" + this.query;
    } else {
      this.file = this.path;
    } 
    this.ref = parts.getRef();
    if (paramURLStreamHandler == null && (paramURLStreamHandler = getURLStreamHandler(paramString1)) == null)
      throw new MalformedURLException("unknown protocol: " + paramString1); 
    this.handler = paramURLStreamHandler;
  }
  
  public URL(String paramString) throws MalformedURLException { this(null, paramString); }
  
  public URL(URL paramURL, String paramString) throws MalformedURLException { this(paramURL, paramString, null); }
  
  public URL(URL paramURL, String paramString, URLStreamHandler paramURLStreamHandler) throws MalformedURLException {
    String str1 = paramString;
    byte b = 0;
    String str2 = null;
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramURLStreamHandler != null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        checkSpecifyHandler(securityManager); 
    } 
    try {
      int j;
      for (j = paramString.length(); j > 0 && paramString.charAt(j - 1) <= ' '; j--);
      while (b < j && paramString.charAt(b) <= ' ')
        b++; 
      if (paramString.regionMatches(true, b, "url:", 0, 4))
        b += 4; 
      if (b < paramString.length() && paramString.charAt(b) == '#')
        bool1 = true; 
      int i;
      char c;
      for (i = b; !bool1 && i < j && (c = paramString.charAt(i)) != '/'; i++) {
        if (c == ':') {
          String str = paramString.substring(b, i).toLowerCase();
          if (isValidProtocol(str)) {
            str2 = str;
            b = i + 1;
          } 
          break;
        } 
      } 
      this.protocol = str2;
      if (paramURL != null && (str2 == null || str2.equalsIgnoreCase(paramURL.protocol))) {
        if (paramURLStreamHandler == null)
          paramURLStreamHandler = paramURL.handler; 
        if (paramURL.path != null && paramURL.path.startsWith("/"))
          str2 = null; 
        if (str2 == null) {
          this.protocol = paramURL.protocol;
          this.authority = paramURL.authority;
          this.userInfo = paramURL.userInfo;
          this.host = paramURL.host;
          this.port = paramURL.port;
          this.file = paramURL.file;
          this.path = paramURL.path;
          bool2 = true;
        } 
      } 
      if (this.protocol == null)
        throw new MalformedURLException("no protocol: " + str1); 
      if (paramURLStreamHandler == null && (paramURLStreamHandler = getURLStreamHandler(this.protocol)) == null)
        throw new MalformedURLException("unknown protocol: " + this.protocol); 
      this.handler = paramURLStreamHandler;
      i = paramString.indexOf('#', b);
      if (i >= 0) {
        this.ref = paramString.substring(i + 1, j);
        j = i;
      } 
      if (bool2 && b == j) {
        this.query = paramURL.query;
        if (this.ref == null)
          this.ref = paramURL.ref; 
      } 
      paramURLStreamHandler.parseURL(this, paramString, b, j);
    } catch (MalformedURLException malformedURLException) {
      throw malformedURLException;
    } catch (Exception exception) {
      MalformedURLException malformedURLException = new MalformedURLException(exception.getMessage());
      malformedURLException.initCause(exception);
      throw malformedURLException;
    } 
  }
  
  private boolean isValidProtocol(String paramString) {
    int i = paramString.length();
    if (i < 1)
      return false; 
    char c = paramString.charAt(0);
    if (!Character.isLetter(c))
      return false; 
    for (byte b = 1; b < i; b++) {
      c = paramString.charAt(b);
      if (!Character.isLetterOrDigit(c) && c != '.' && c != '+' && c != '-')
        return false; 
    } 
    return true;
  }
  
  private void checkSpecifyHandler(SecurityManager paramSecurityManager) { paramSecurityManager.checkPermission(SecurityConstants.SPECIFY_HANDLER_PERMISSION); }
  
  void set(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4) {
    synchronized (this) {
      this.protocol = paramString1;
      this.host = paramString2;
      this.authority = (paramInt == -1) ? paramString2 : (paramString2 + ":" + paramInt);
      this.port = paramInt;
      this.file = paramString3;
      this.ref = paramString4;
      this.hashCode = -1;
      this.hostAddress = null;
      int i = paramString3.lastIndexOf('?');
      if (i != -1) {
        this.query = paramString3.substring(i + 1);
        this.path = paramString3.substring(0, i);
      } else {
        this.path = paramString3;
      } 
    } 
  }
  
  void set(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) {
    synchronized (this) {
      this.protocol = paramString1;
      this.host = paramString2;
      this.port = paramInt;
      this.file = (paramString6 == null) ? paramString5 : (paramString5 + "?" + paramString6);
      this.userInfo = paramString4;
      this.path = paramString5;
      this.ref = paramString7;
      this.hashCode = -1;
      this.hostAddress = null;
      this.query = paramString6;
      this.authority = paramString3;
    } 
  }
  
  public String getQuery() { return this.query; }
  
  public String getPath() { return this.path; }
  
  public String getUserInfo() { return this.userInfo; }
  
  public String getAuthority() { return this.authority; }
  
  public int getPort() { return this.port; }
  
  public int getDefaultPort() { return this.handler.getDefaultPort(); }
  
  public String getProtocol() { return this.protocol; }
  
  public String getHost() { return this.host; }
  
  public String getFile() { return this.file; }
  
  public String getRef() { return this.ref; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof URL))
      return false; 
    URL uRL = (URL)paramObject;
    return this.handler.equals(this, uRL);
  }
  
  public int hashCode() {
    if (this.hashCode != -1)
      return this.hashCode; 
    this.hashCode = this.handler.hashCode(this);
    return this.hashCode;
  }
  
  public boolean sameFile(URL paramURL) { return this.handler.sameFile(this, paramURL); }
  
  public String toString() { return toExternalForm(); }
  
  public String toExternalForm() { return this.handler.toExternalForm(this); }
  
  public URI toURI() throws URISyntaxException { return new URI(toString()); }
  
  public URLConnection openConnection() throws IOException { return this.handler.openConnection(this); }
  
  public URLConnection openConnection(Proxy paramProxy) throws IOException {
    if (paramProxy == null)
      throw new IllegalArgumentException("proxy can not be null"); 
    Proxy proxy = (paramProxy == Proxy.NO_PROXY) ? Proxy.NO_PROXY : ApplicationProxy.create(paramProxy);
    SecurityManager securityManager = System.getSecurityManager();
    if (proxy.type() != Proxy.Type.DIRECT && securityManager != null) {
      InetSocketAddress inetSocketAddress = (InetSocketAddress)proxy.address();
      if (inetSocketAddress.isUnresolved()) {
        securityManager.checkConnect(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
      } else {
        securityManager.checkConnect(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
      } 
    } 
    return this.handler.openConnection(this, proxy);
  }
  
  public final InputStream openStream() throws IOException { return openConnection().getInputStream(); }
  
  public final Object getContent() throws IOException { return openConnection().getContent(); }
  
  public final Object getContent(Class[] paramArrayOfClass) throws IOException { return openConnection().getContent(paramArrayOfClass); }
  
  public static void setURLStreamHandlerFactory(URLStreamHandlerFactory paramURLStreamHandlerFactory) {
    synchronized (streamHandlerLock) {
      if (factory != null)
        throw new Error("factory already defined"); 
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkSetFactory(); 
      handlers.clear();
      factory = paramURLStreamHandlerFactory;
    } 
  }
  
  static URLStreamHandler getURLStreamHandler(String paramString) {
    URLStreamHandler uRLStreamHandler = (URLStreamHandler)handlers.get(paramString);
    if (uRLStreamHandler == null) {
      boolean bool = false;
      if (factory != null) {
        uRLStreamHandler = factory.createURLStreamHandler(paramString);
        bool = true;
      } 
      if (uRLStreamHandler == null) {
        String str = null;
        str = (String)AccessController.doPrivileged(new GetPropertyAction("java.protocol.handler.pkgs", ""));
        if (str != "")
          str = str + "|"; 
        str = str + "sun.net.www.protocol";
        StringTokenizer stringTokenizer = new StringTokenizer(str, "|");
        while (uRLStreamHandler == null && stringTokenizer.hasMoreTokens()) {
          String str1 = stringTokenizer.nextToken().trim();
          try {
            String str2 = str1 + "." + paramString + ".Handler";
            Class clazz = null;
            try {
              clazz = Class.forName(str2);
            } catch (ClassNotFoundException classNotFoundException) {
              ClassLoader classLoader = ClassLoader.getSystemClassLoader();
              if (classLoader != null)
                clazz = classLoader.loadClass(str2); 
            } 
            if (clazz != null)
              uRLStreamHandler = (URLStreamHandler)clazz.newInstance(); 
          } catch (Exception exception) {}
        } 
      } 
      synchronized (streamHandlerLock) {
        URLStreamHandler uRLStreamHandler1 = null;
        uRLStreamHandler1 = (URLStreamHandler)handlers.get(paramString);
        if (uRLStreamHandler1 != null)
          return uRLStreamHandler1; 
        if (!bool && factory != null)
          uRLStreamHandler1 = factory.createURLStreamHandler(paramString); 
        if (uRLStreamHandler1 != null)
          uRLStreamHandler = uRLStreamHandler1; 
        if (uRLStreamHandler != null)
          handlers.put(paramString, uRLStreamHandler); 
      } 
    } 
    return uRLStreamHandler;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.defaultWriteObject(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str1 = (String)getField.get("protocol", null);
    if (getURLStreamHandler(str1) == null)
      throw new IOException("unknown protocol: " + str1); 
    String str2 = (String)getField.get("host", null);
    int i = getField.get("port", -1);
    String str3 = (String)getField.get("authority", null);
    String str4 = (String)getField.get("file", null);
    String str5 = (String)getField.get("ref", null);
    int j = getField.get("hashCode", -1);
    if (str3 == null && ((str2 != null && str2.length() > 0) || i != -1)) {
      if (str2 == null)
        str2 = ""; 
      str3 = (i == -1) ? str2 : (str2 + ":" + i);
    } 
    this.tempState = new UrlDeserializedState(str1, str2, i, str3, str4, str5, j);
  }
  
  private Object readResolve() throws IOException {
    URLStreamHandler uRLStreamHandler = null;
    uRLStreamHandler = getURLStreamHandler(this.tempState.getProtocol());
    URL uRL = null;
    if (isBuiltinStreamHandler(uRLStreamHandler.getClass().getName())) {
      uRL = fabricateNewURL();
    } else {
      uRL = setDeserializedFields(uRLStreamHandler);
    } 
    return uRL;
  }
  
  private URL setDeserializedFields(URLStreamHandler paramURLStreamHandler) {
    String str1 = null;
    String str2 = this.tempState.getProtocol();
    String str3 = this.tempState.getHost();
    int i = this.tempState.getPort();
    String str4 = this.tempState.getAuthority();
    String str5 = this.tempState.getFile();
    String str6 = this.tempState.getRef();
    int j = this.tempState.getHashCode();
    if (str4 == null && ((str3 != null && str3.length() > 0) || i != -1)) {
      if (str3 == null)
        str3 = ""; 
      str4 = (i == -1) ? str3 : (str3 + ":" + i);
      int k = str3.lastIndexOf('@');
      if (k != -1) {
        str1 = str3.substring(0, k);
        str3 = str3.substring(k + 1);
      } 
    } else if (str4 != null) {
      int k = str4.indexOf('@');
      if (k != -1)
        str1 = str4.substring(0, k); 
    } 
    String str7 = null;
    String str8 = null;
    if (str5 != null) {
      int k = str5.lastIndexOf('?');
      if (k != -1) {
        str8 = str5.substring(k + 1);
        str7 = str5.substring(0, k);
      } else {
        str7 = str5;
      } 
    } 
    this.protocol = str2;
    this.host = str3;
    this.port = i;
    this.file = str5;
    this.authority = str4;
    this.ref = str6;
    this.hashCode = j;
    this.handler = paramURLStreamHandler;
    this.query = str8;
    this.path = str7;
    this.userInfo = str1;
    return this;
  }
  
  private URL fabricateNewURL() throws InvalidObjectException {
    URL uRL = null;
    String str = this.tempState.reconstituteUrlString();
    try {
      uRL = new URL(str);
    } catch (MalformedURLException malformedURLException) {
      resetState();
      InvalidObjectException invalidObjectException = new InvalidObjectException("Malformed URL: " + str);
      invalidObjectException.initCause(malformedURLException);
      throw invalidObjectException;
    } 
    uRL.setSerializedHashCode(this.tempState.getHashCode());
    resetState();
    return uRL;
  }
  
  private boolean isBuiltinStreamHandler(String paramString) { return paramString.startsWith("sun.net.www.protocol"); }
  
  private void resetState() {
    this.protocol = null;
    this.host = null;
    this.port = -1;
    this.file = null;
    this.authority = null;
    this.ref = null;
    this.hashCode = -1;
    this.handler = null;
    this.query = null;
    this.path = null;
    this.userInfo = null;
    this.tempState = null;
  }
  
  private void setSerializedHashCode(int paramInt) { this.hashCode = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */