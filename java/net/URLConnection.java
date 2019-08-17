package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import sun.net.www.MessageHeader;
import sun.net.www.MimeTable;
import sun.security.action.GetPropertyAction;
import sun.security.util.SecurityConstants;

public abstract class URLConnection {
  protected URL url;
  
  protected boolean doInput = true;
  
  protected boolean doOutput = false;
  
  private static boolean defaultAllowUserInteraction = false;
  
  protected boolean allowUserInteraction = defaultAllowUserInteraction;
  
  private static boolean defaultUseCaches = true;
  
  protected boolean useCaches = defaultUseCaches;
  
  protected long ifModifiedSince = 0L;
  
  protected boolean connected = false;
  
  private int connectTimeout;
  
  private int readTimeout;
  
  private MessageHeader requests;
  
  private static FileNameMap fileNameMap;
  
  private static boolean fileNameMapLoaded = false;
  
  static ContentHandlerFactory factory;
  
  private static Hashtable<String, ContentHandler> handlers = new Hashtable();
  
  private static final String contentClassPrefix = "sun.net.www.content";
  
  private static final String contentPathProp = "java.content.handler.pkgs";
  
  public static FileNameMap getFileNameMap() {
    if (fileNameMap == null && !fileNameMapLoaded) {
      fileNameMap = MimeTable.loadTable();
      fileNameMapLoaded = true;
    } 
    return new FileNameMap() {
        private FileNameMap map = fileNameMap;
        
        public String getContentTypeFor(String param1String) { return this.map.getContentTypeFor(param1String); }
      };
  }
  
  public static void setFileNameMap(FileNameMap paramFileNameMap) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    fileNameMap = paramFileNameMap;
  }
  
  public abstract void connect() throws IOException;
  
  public void setConnectTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeout can not be negative"); 
    this.connectTimeout = paramInt;
  }
  
  public int getConnectTimeout() { return this.connectTimeout; }
  
  public void setReadTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("timeout can not be negative"); 
    this.readTimeout = paramInt;
  }
  
  public int getReadTimeout() { return this.readTimeout; }
  
  protected URLConnection(URL paramURL) { this.url = paramURL; }
  
  public URL getURL() { return this.url; }
  
  public int getContentLength() {
    long l = getContentLengthLong();
    return (l > 2147483647L) ? -1 : (int)l;
  }
  
  public long getContentLengthLong() { return getHeaderFieldLong("content-length", -1L); }
  
  public String getContentType() { return getHeaderField("content-type"); }
  
  public String getContentEncoding() { return getHeaderField("content-encoding"); }
  
  public long getExpiration() { return getHeaderFieldDate("expires", 0L); }
  
  public long getDate() { return getHeaderFieldDate("date", 0L); }
  
  public long getLastModified() { return getHeaderFieldDate("last-modified", 0L); }
  
  public String getHeaderField(String paramString) { return null; }
  
  public Map<String, List<String>> getHeaderFields() { return Collections.emptyMap(); }
  
  public int getHeaderFieldInt(String paramString, int paramInt) {
    String str = getHeaderField(paramString);
    try {
      return Integer.parseInt(str);
    } catch (Exception exception) {
      return paramInt;
    } 
  }
  
  public long getHeaderFieldLong(String paramString, long paramLong) {
    String str = getHeaderField(paramString);
    try {
      return Long.parseLong(str);
    } catch (Exception exception) {
      return paramLong;
    } 
  }
  
  public long getHeaderFieldDate(String paramString, long paramLong) {
    String str = getHeaderField(paramString);
    try {
      return Date.parse(str);
    } catch (Exception exception) {
      return paramLong;
    } 
  }
  
  public String getHeaderFieldKey(int paramInt) { return null; }
  
  public String getHeaderField(int paramInt) { return null; }
  
  public Object getContent() throws IOException {
    getInputStream();
    return getContentHandler().getContent(this);
  }
  
  public Object getContent(Class[] paramArrayOfClass) throws IOException {
    getInputStream();
    return getContentHandler().getContent(this, paramArrayOfClass);
  }
  
  public Permission getPermission() throws IOException { return SecurityConstants.ALL_PERMISSION; }
  
  public InputStream getInputStream() throws IOException { throw new UnknownServiceException("protocol doesn't support input"); }
  
  public OutputStream getOutputStream() throws IOException { throw new UnknownServiceException("protocol doesn't support output"); }
  
  public String toString() { return getClass().getName() + ":" + this.url; }
  
  public void setDoInput(boolean paramBoolean) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    this.doInput = paramBoolean;
  }
  
  public boolean getDoInput() { return this.doInput; }
  
  public void setDoOutput(boolean paramBoolean) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    this.doOutput = paramBoolean;
  }
  
  public boolean getDoOutput() { return this.doOutput; }
  
  public void setAllowUserInteraction(boolean paramBoolean) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    this.allowUserInteraction = paramBoolean;
  }
  
  public boolean getAllowUserInteraction() { return this.allowUserInteraction; }
  
  public static void setDefaultAllowUserInteraction(boolean paramBoolean) { defaultAllowUserInteraction = paramBoolean; }
  
  public static boolean getDefaultAllowUserInteraction() { return defaultAllowUserInteraction; }
  
  public void setUseCaches(boolean paramBoolean) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    this.useCaches = paramBoolean;
  }
  
  public boolean getUseCaches() { return this.useCaches; }
  
  public void setIfModifiedSince(long paramLong) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    this.ifModifiedSince = paramLong;
  }
  
  public long getIfModifiedSince() { return this.ifModifiedSince; }
  
  public boolean getDefaultUseCaches() { return defaultUseCaches; }
  
  public void setDefaultUseCaches(boolean paramBoolean) { defaultUseCaches = paramBoolean; }
  
  public void setRequestProperty(String paramString1, String paramString2) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    if (paramString1 == null)
      throw new NullPointerException("key is null"); 
    if (this.requests == null)
      this.requests = new MessageHeader(); 
    this.requests.set(paramString1, paramString2);
  }
  
  public void addRequestProperty(String paramString1, String paramString2) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    if (paramString1 == null)
      throw new NullPointerException("key is null"); 
    if (this.requests == null)
      this.requests = new MessageHeader(); 
    this.requests.add(paramString1, paramString2);
  }
  
  public String getRequestProperty(String paramString) {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    return (this.requests == null) ? null : this.requests.findValue(paramString);
  }
  
  public Map<String, List<String>> getRequestProperties() {
    if (this.connected)
      throw new IllegalStateException("Already connected"); 
    return (this.requests == null) ? Collections.emptyMap() : this.requests.getHeaders(null);
  }
  
  @Deprecated
  public static void setDefaultRequestProperty(String paramString1, String paramString2) {}
  
  @Deprecated
  public static String getDefaultRequestProperty(String paramString) { return null; }
  
  public static void setContentHandlerFactory(ContentHandlerFactory paramContentHandlerFactory) {
    if (factory != null)
      throw new Error("factory already defined"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    factory = paramContentHandlerFactory;
  }
  
  ContentHandler getContentHandler() throws UnknownServiceException {
    String str = stripOffParameters(getContentType());
    ContentHandler contentHandler = null;
    if (str == null)
      throw new UnknownServiceException("no content-type"); 
    try {
      contentHandler = (ContentHandler)handlers.get(str);
      if (contentHandler != null)
        return contentHandler; 
    } catch (Exception exception) {}
    if (factory != null)
      contentHandler = factory.createContentHandler(str); 
    if (contentHandler == null) {
      try {
        contentHandler = lookupContentHandlerClassFor(str);
      } catch (Exception exception) {
        exception.printStackTrace();
        contentHandler = UnknownContentHandler.INSTANCE;
      } 
      handlers.put(str, contentHandler);
    } 
    return contentHandler;
  }
  
  private String stripOffParameters(String paramString) {
    if (paramString == null)
      return null; 
    int i = paramString.indexOf(';');
    return (i > 0) ? paramString.substring(0, i) : paramString;
  }
  
  private ContentHandler lookupContentHandlerClassFor(String paramString) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    String str1 = typeToPackageName(paramString);
    String str2 = getContentHandlerPkgPrefixes();
    StringTokenizer stringTokenizer = new StringTokenizer(str2, "|");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken().trim();
      try {
        String str3 = str + "." + str1;
        Class clazz = null;
        try {
          clazz = Class.forName(str3);
        } catch (ClassNotFoundException classNotFoundException) {
          ClassLoader classLoader = ClassLoader.getSystemClassLoader();
          if (classLoader != null)
            clazz = classLoader.loadClass(str3); 
        } 
        if (clazz != null)
          return (ContentHandler)clazz.newInstance(); 
      } catch (Exception exception) {}
    } 
    return UnknownContentHandler.INSTANCE;
  }
  
  private String typeToPackageName(String paramString) {
    paramString = paramString.toLowerCase();
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    paramString.getChars(0, i, arrayOfChar, 0);
    for (byte b = 0; b < i; b++) {
      char c = arrayOfChar[b];
      if (c == '/') {
        arrayOfChar[b] = '.';
      } else if (('A' > c || c > 'Z') && ('a' > c || c > 'z') && ('0' > c || c > '9')) {
        arrayOfChar[b] = '_';
      } 
    } 
    return new String(arrayOfChar);
  }
  
  private String getContentHandlerPkgPrefixes() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.content.handler.pkgs", ""));
    if (str != "")
      str = str + "|"; 
    return str + "sun.net.www.content";
  }
  
  public static String guessContentTypeFromName(String paramString) { return getFileNameMap().getContentTypeFor(paramString); }
  
  public static String guessContentTypeFromStream(InputStream paramInputStream) throws IOException {
    if (!paramInputStream.markSupported())
      return null; 
    paramInputStream.mark(16);
    int i = paramInputStream.read();
    int j = paramInputStream.read();
    int k = paramInputStream.read();
    int m = paramInputStream.read();
    int n = paramInputStream.read();
    int i1 = paramInputStream.read();
    int i2 = paramInputStream.read();
    int i3 = paramInputStream.read();
    int i4 = paramInputStream.read();
    int i5 = paramInputStream.read();
    int i6 = paramInputStream.read();
    int i7 = paramInputStream.read();
    int i8 = paramInputStream.read();
    int i9 = paramInputStream.read();
    int i10 = paramInputStream.read();
    int i11 = paramInputStream.read();
    paramInputStream.reset();
    if (i == 202 && j == 254 && k == 186 && m == 190)
      return "application/java-vm"; 
    if (i == 172 && j == 237)
      return "application/x-java-serialized-object"; 
    if (i == 60) {
      if (j == 33 || (j == 104 && ((k == 116 && m == 109 && n == 108) || (k == 101 && m == 97 && n == 100))) || (j == 98 && k == 111 && m == 100 && n == 121) || (j == 72 && ((k == 84 && m == 77 && n == 76) || (k == 69 && m == 65 && n == 68))) || (j == 66 && k == 79 && m == 68 && n == 89))
        return "text/html"; 
      if (j == 63 && k == 120 && m == 109 && n == 108 && i1 == 32)
        return "application/xml"; 
    } 
    if (i == 239 && j == 187 && k == 191 && m == 60 && n == 63 && i1 == 120)
      return "application/xml"; 
    if (i == 254 && j == 255 && k == 0 && m == 60 && n == 0 && i1 == 63 && i2 == 0 && i3 == 120)
      return "application/xml"; 
    if (i == 255 && j == 254 && k == 60 && m == 0 && n == 63 && i1 == 0 && i2 == 120 && i3 == 0)
      return "application/xml"; 
    if (i == 0 && j == 0 && k == 254 && m == 255 && n == 0 && i1 == 0 && i2 == 0 && i3 == 60 && i4 == 0 && i5 == 0 && i6 == 0 && i7 == 63 && i8 == 0 && i9 == 0 && i10 == 0 && i11 == 120)
      return "application/xml"; 
    if (i == 255 && j == 254 && k == 0 && m == 0 && n == 60 && i1 == 0 && i2 == 0 && i3 == 0 && i4 == 63 && i5 == 0 && i6 == 0 && i7 == 0 && i8 == 120 && i9 == 0 && i10 == 0 && i11 == 0)
      return "application/xml"; 
    if (i == 71 && j == 73 && k == 70 && m == 56)
      return "image/gif"; 
    if (i == 35 && j == 100 && k == 101 && m == 102)
      return "image/x-bitmap"; 
    if (i == 33 && j == 32 && k == 88 && m == 80 && n == 77 && i1 == 50)
      return "image/x-pixmap"; 
    if (i == 137 && j == 80 && k == 78 && m == 71 && n == 13 && i1 == 10 && i2 == 26 && i3 == 10)
      return "image/png"; 
    if (i == 255 && j == 216 && k == 255) {
      if (m == 224 || m == 238)
        return "image/jpeg"; 
      if (m == 225 && i2 == 69 && i3 == 120 && i4 == 105 && i5 == 102 && i6 == 0)
        return "image/jpeg"; 
    } 
    return (i == 208 && j == 207 && k == 17 && m == 224 && n == 161 && i1 == 177 && i2 == 26 && i3 == 225 && checkfpx(paramInputStream)) ? "image/vnd.fpx" : ((i == 46 && j == 115 && k == 110 && m == 100) ? "audio/basic" : ((i == 100 && j == 110 && k == 115 && m == 46) ? "audio/basic" : ((i == 82 && j == 73 && k == 70 && m == 70) ? "audio/x-wav" : null)));
  }
  
  private static boolean checkfpx(InputStream paramInputStream) throws IOException {
    int k;
    int j;
    paramInputStream.mark(256);
    long l1 = 28L;
    long l2;
    if ((l2 = skipForward(paramInputStream, l1)) < l1) {
      paramInputStream.reset();
      return false;
    } 
    int[] arrayOfInt = new int[16];
    if (readBytes(arrayOfInt, 2, paramInputStream) < 0) {
      paramInputStream.reset();
      return false;
    } 
    int i = arrayOfInt[0];
    l2 += 2L;
    if (readBytes(arrayOfInt, 2, paramInputStream) < 0) {
      paramInputStream.reset();
      return false;
    } 
    if (i == 254) {
      j = arrayOfInt[0];
      j += (arrayOfInt[1] << 8);
    } else {
      j = arrayOfInt[0] << 8;
      j += arrayOfInt[1];
    } 
    l2 += 2L;
    l1 = 48L - l2;
    long l3 = 0L;
    if ((l3 = skipForward(paramInputStream, l1)) < l1) {
      paramInputStream.reset();
      return false;
    } 
    l2 += l3;
    if (readBytes(arrayOfInt, 4, paramInputStream) < 0) {
      paramInputStream.reset();
      return false;
    } 
    if (i == 254) {
      k = arrayOfInt[0];
      k += (arrayOfInt[1] << 8);
      k += (arrayOfInt[2] << 16);
      k += (arrayOfInt[3] << 24);
    } else {
      k = arrayOfInt[0] << 24;
      k += (arrayOfInt[1] << 16);
      k += (arrayOfInt[2] << 8);
      k += arrayOfInt[3];
    } 
    l2 += 4L;
    paramInputStream.reset();
    l1 = 512L + (1 << j) * k + 80L;
    if (l1 < 0L)
      return false; 
    paramInputStream.mark((int)l1 + 48);
    if (skipForward(paramInputStream, l1) < l1) {
      paramInputStream.reset();
      return false;
    } 
    if (readBytes(arrayOfInt, 16, paramInputStream) < 0) {
      paramInputStream.reset();
      return false;
    } 
    if (i == 254 && arrayOfInt[0] == 0 && arrayOfInt[2] == 97 && arrayOfInt[3] == 86 && arrayOfInt[4] == 84 && arrayOfInt[5] == 193 && arrayOfInt[6] == 206 && arrayOfInt[7] == 17 && arrayOfInt[8] == 133 && arrayOfInt[9] == 83 && arrayOfInt[10] == 0 && arrayOfInt[11] == 170 && arrayOfInt[12] == 0 && arrayOfInt[13] == 161 && arrayOfInt[14] == 249 && arrayOfInt[15] == 91) {
      paramInputStream.reset();
      return true;
    } 
    if (arrayOfInt[3] == 0 && arrayOfInt[1] == 97 && arrayOfInt[0] == 86 && arrayOfInt[5] == 84 && arrayOfInt[4] == 193 && arrayOfInt[7] == 206 && arrayOfInt[6] == 17 && arrayOfInt[8] == 133 && arrayOfInt[9] == 83 && arrayOfInt[10] == 0 && arrayOfInt[11] == 170 && arrayOfInt[12] == 0 && arrayOfInt[13] == 161 && arrayOfInt[14] == 249 && arrayOfInt[15] == 91) {
      paramInputStream.reset();
      return true;
    } 
    paramInputStream.reset();
    return false;
  }
  
  private static int readBytes(int[] paramArrayOfInt, int paramInt, InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[paramInt];
    if (paramInputStream.read(arrayOfByte, 0, paramInt) < paramInt)
      return -1; 
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfInt[b] = arrayOfByte[b] & 0xFF; 
    return 0;
  }
  
  private static long skipForward(InputStream paramInputStream, long paramLong) throws IOException {
    long l1 = 0L;
    long l2;
    for (l2 = 0L; l2 != paramLong; l2 += l1) {
      l1 = paramInputStream.skip(paramLong - l2);
      if (l1 <= 0L) {
        if (paramInputStream.read() == -1)
          return l2; 
        l2++;
      } 
    } 
    return l2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */