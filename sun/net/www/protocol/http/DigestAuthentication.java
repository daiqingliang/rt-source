package sun.net.www.protocol.http;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import sun.net.NetProperties;
import sun.net.www.HeaderParser;

class DigestAuthentication extends AuthenticationInfo {
  private static final long serialVersionUID = 100L;
  
  private String authMethod;
  
  private static final String compatPropName = "http.auth.digest.quoteParameters";
  
  private static final boolean delimCompatFlag;
  
  Parameters params;
  
  private static final char[] charArray;
  
  private static final String[] zeroPad;
  
  public DigestAuthentication(boolean paramBoolean, URL paramURL, String paramString1, String paramString2, PasswordAuthentication paramPasswordAuthentication, Parameters paramParameters) {
    super(paramBoolean ? 112 : 115, AuthScheme.DIGEST, paramURL, paramString1);
    this.authMethod = paramString2;
    this.pw = paramPasswordAuthentication;
    this.params = paramParameters;
  }
  
  public DigestAuthentication(boolean paramBoolean, String paramString1, int paramInt, String paramString2, String paramString3, PasswordAuthentication paramPasswordAuthentication, Parameters paramParameters) {
    super(paramBoolean ? 112 : 115, AuthScheme.DIGEST, paramString1, paramInt, paramString2);
    this.authMethod = paramString3;
    this.pw = paramPasswordAuthentication;
    this.params = paramParameters;
  }
  
  public boolean supportsPreemptiveAuthorization() { return true; }
  
  public String getHeaderValue(URL paramURL, String paramString) { return getHeaderValueImpl(paramURL.getFile(), paramString); }
  
  String getHeaderValue(String paramString1, String paramString2) { return getHeaderValueImpl(paramString1, paramString2); }
  
  public boolean isAuthorizationStale(String paramString) {
    HeaderParser headerParser = new HeaderParser(paramString);
    String str1 = headerParser.findValue("stale");
    if (str1 == null || !str1.equals("true"))
      return false; 
    String str2 = headerParser.findValue("nonce");
    if (str2 == null || "".equals(str2))
      return false; 
    this.params.setNonce(str2);
    return true;
  }
  
  public boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString) {
    String str2;
    this.params.setNonce(paramHeaderParser.findValue("nonce"));
    this.params.setOpaque(paramHeaderParser.findValue("opaque"));
    this.params.setQop(paramHeaderParser.findValue("qop"));
    String str1 = "";
    if (this.type == 'p' && paramHttpURLConnection.tunnelState() == HttpURLConnection.TunnelState.SETUP) {
      str1 = HttpURLConnection.connectRequestURI(paramHttpURLConnection.getURL());
      str2 = HttpURLConnection.HTTP_CONNECT;
    } else {
      try {
        str1 = paramHttpURLConnection.getRequestURI();
      } catch (IOException iOException) {}
      str2 = paramHttpURLConnection.getMethod();
    } 
    if (this.params.nonce == null || this.authMethod == null || this.pw == null || this.realm == null)
      return false; 
    if (this.authMethod.length() >= 1)
      this.authMethod = Character.toUpperCase(this.authMethod.charAt(0)) + this.authMethod.substring(1).toLowerCase(); 
    String str3 = paramHeaderParser.findValue("algorithm");
    if (str3 == null || "".equals(str3))
      str3 = "MD5"; 
    this.params.setAlgorithm(str3);
    if (this.params.authQop())
      this.params.setNewCnonce(); 
    String str4 = getHeaderValueImpl(str1, str2);
    if (str4 != null) {
      paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), str4);
      return true;
    } 
    return false;
  }
  
  private String getHeaderValueImpl(String paramString1, String paramString2) {
    String str9;
    String str8;
    String str1;
    char[] arrayOfChar = this.pw.getPassword();
    boolean bool = this.params.authQop();
    String str2 = this.params.getOpaque();
    String str3 = this.params.getCnonce();
    String str4 = this.params.getNonce();
    String str5 = this.params.getAlgorithm();
    this.params.incrementNC();
    int i = this.params.getNCCount();
    String str6 = null;
    if (i != -1) {
      str6 = Integer.toHexString(i).toLowerCase();
      int j = str6.length();
      if (j < 8)
        str6 = zeroPad[j] + str6; 
    } 
    try {
      str1 = computeDigest(true, this.pw.getUserName(), arrayOfChar, this.realm, paramString2, paramString1, str4, str3, str6);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return null;
    } 
    String str7 = "\"";
    if (bool)
      str7 = "\", nc=" + str6; 
    if (delimCompatFlag) {
      str8 = ", algorithm=\"" + str5 + "\"";
      str9 = ", qop=\"auth\"";
    } else {
      str8 = ", algorithm=" + str5;
      str9 = ", qop=auth";
    } 
    String str10 = this.authMethod + " username=\"" + this.pw.getUserName() + "\", realm=\"" + this.realm + "\", nonce=\"" + str4 + str7 + ", uri=\"" + paramString1 + "\", response=\"" + str1 + "\"" + str8;
    if (str2 != null)
      str10 = str10 + ", opaque=\"" + str2 + "\""; 
    if (str3 != null)
      str10 = str10 + ", cnonce=\"" + str3 + "\""; 
    if (bool)
      str10 = str10 + str9; 
    return str10;
  }
  
  public void checkResponse(String paramString1, String paramString2, URL paramURL) throws IOException { checkResponse(paramString1, paramString2, paramURL.getFile()); }
  
  public void checkResponse(String paramString1, String paramString2, String paramString3) throws IOException {
    char[] arrayOfChar = this.pw.getPassword();
    String str1 = this.pw.getUserName();
    boolean bool = this.params.authQop();
    String str2 = this.params.getOpaque();
    String str3 = this.params.cnonce;
    String str4 = this.params.getNonce();
    String str5 = this.params.getAlgorithm();
    int i = this.params.getNCCount();
    String str6 = null;
    if (paramString1 == null)
      throw new ProtocolException("No authentication information in response"); 
    if (i != -1) {
      str6 = Integer.toHexString(i).toUpperCase();
      int j = str6.length();
      if (j < 8)
        str6 = zeroPad[j] + str6; 
    } 
    try {
      String str7 = computeDigest(false, str1, arrayOfChar, this.realm, paramString2, paramString3, str4, str3, str6);
      HeaderParser headerParser = new HeaderParser(paramString1);
      String str8 = headerParser.findValue("rspauth");
      if (str8 == null)
        throw new ProtocolException("No digest in response"); 
      if (!str8.equals(str7))
        throw new ProtocolException("Response digest invalid"); 
      String str9 = headerParser.findValue("nextnonce");
      if (str9 != null && !"".equals(str9))
        this.params.setNonce(str9); 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new ProtocolException("Unsupported algorithm in response");
    } 
  }
  
  private String computeDigest(boolean paramBoolean, String paramString1, char[] paramArrayOfChar, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7) throws NoSuchAlgorithmException {
    String str5;
    String str3;
    String str1;
    String str2 = this.params.getAlgorithm();
    boolean bool = str2.equalsIgnoreCase("MD5-sess");
    MessageDigest messageDigest = MessageDigest.getInstance(bool ? "MD5" : str2);
    if (bool) {
      if ((str1 = this.params.getCachedHA1()) == null) {
        str3 = paramString1 + ":" + paramString2 + ":";
        String str7 = encode(str3, paramArrayOfChar, messageDigest);
        String str6 = str7 + ":" + paramString5 + ":" + paramString6;
        str1 = encode(str6, null, messageDigest);
        this.params.setCachedHA1(str1);
      } 
    } else {
      String str = paramString1 + ":" + paramString2 + ":";
      str1 = encode(str, paramArrayOfChar, messageDigest);
    } 
    if (paramBoolean) {
      str3 = paramString3 + ":" + paramString4;
    } else {
      str3 = ":" + paramString4;
    } 
    String str4 = encode(str3, null, messageDigest);
    if (this.params.authQop()) {
      str5 = str1 + ":" + paramString5 + ":" + paramString7 + ":" + paramString6 + ":auth:" + str4;
    } else {
      str5 = str1 + ":" + paramString5 + ":" + str4;
    } 
    return encode(str5, null, messageDigest);
  }
  
  private String encode(String paramString, char[] paramArrayOfChar, MessageDigest paramMessageDigest) {
    try {
      paramMessageDigest.update(paramString.getBytes("ISO-8859-1"));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      assert false;
    } 
    if (paramArrayOfChar != null) {
      byte[] arrayOfByte1 = new byte[paramArrayOfChar.length];
      for (byte b1 = 0; b1 < paramArrayOfChar.length; b1++)
        arrayOfByte1[b1] = (byte)paramArrayOfChar[b1]; 
      paramMessageDigest.update(arrayOfByte1);
      Arrays.fill(arrayOfByte1, (byte)0);
    } 
    byte[] arrayOfByte = paramMessageDigest.digest();
    StringBuffer stringBuffer = new StringBuffer(arrayOfByte.length * 2);
    for (byte b = 0; b < arrayOfByte.length; b++) {
      byte b1 = arrayOfByte[b] >>> 4 & 0xF;
      stringBuffer.append(charArray[b1]);
      b1 = arrayOfByte[b] & 0xF;
      stringBuffer.append(charArray[b1]);
    } 
    return stringBuffer.toString();
  }
  
  static  {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return NetProperties.getBoolean("http.auth.digest.quoteParameters"); }
        });
    delimCompatFlag = (bool == null) ? false : bool.booleanValue();
    charArray = new char[] { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f' };
    zeroPad = new String[] { "00000000", "0000000", "000000", "00000", "0000", "000", "00", "0" };
  }
  
  static class Parameters implements Serializable {
    private static final long serialVersionUID = -3584543755194526252L;
    
    private boolean serverQop = false;
    
    private String opaque = null;
    
    private String cnonce;
    
    private String nonce = null;
    
    private String algorithm = null;
    
    private int NCcount = 0;
    
    private String cachedHA1 = null;
    
    private boolean redoCachedHA1 = true;
    
    private static final int cnonceRepeat = 5;
    
    private static final int cnoncelen = 40;
    
    private static Random random = new Random();
    
    int cnonce_count = 0;
    
    Parameters() { setNewCnonce(); }
    
    boolean authQop() { return this.serverQop; }
    
    void incrementNC() { this.NCcount++; }
    
    int getNCCount() { return this.NCcount; }
    
    String getCnonce() {
      if (this.cnonce_count >= 5)
        setNewCnonce(); 
      this.cnonce_count++;
      return this.cnonce;
    }
    
    void setNewCnonce() {
      byte[] arrayOfByte = new byte[20];
      char[] arrayOfChar = new char[40];
      random.nextBytes(arrayOfByte);
      for (byte b = 0; b < 20; b++) {
        byte b1 = arrayOfByte[b] + 128;
        arrayOfChar[b * 2] = (char)(65 + b1 / 16);
        arrayOfChar[b * 2 + 1] = (char)(65 + b1 % 16);
      } 
      this.cnonce = new String(arrayOfChar, 0, 40);
      this.cnonce_count = 0;
      this.redoCachedHA1 = true;
    }
    
    void setQop(String param1String) {
      if (param1String != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(param1String, " ");
        while (stringTokenizer.hasMoreTokens()) {
          if (stringTokenizer.nextToken().equalsIgnoreCase("auth")) {
            this.serverQop = true;
            return;
          } 
        } 
      } 
      this.serverQop = false;
    }
    
    String getOpaque() { return this.opaque; }
    
    void setOpaque(String param1String) { this.opaque = param1String; }
    
    String getNonce() { return this.nonce; }
    
    void setNonce(String param1String) {
      if (!param1String.equals(this.nonce)) {
        this.nonce = param1String;
        this.NCcount = 0;
        this.redoCachedHA1 = true;
      } 
    }
    
    String getCachedHA1() { return this.redoCachedHA1 ? null : this.cachedHA1; }
    
    void setCachedHA1(String param1String) {
      this.cachedHA1 = param1String;
      this.redoCachedHA1 = false;
    }
    
    String getAlgorithm() { return this.algorithm; }
    
    void setAlgorithm(String param1String) { this.algorithm = param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\DigestAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */