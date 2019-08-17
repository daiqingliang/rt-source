package com.sun.security.sasl.digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class DigestMD5Server extends DigestMD5Base implements SaslServer {
  private static final String MY_CLASS_NAME = DigestMD5Server.class.getName();
  
  private static final String UTF8_DIRECTIVE = "charset=utf-8,";
  
  private static final String ALGORITHM_DIRECTIVE = "algorithm=md5-sess";
  
  private static final int NONCE_COUNT_VALUE = 1;
  
  private static final String UTF8_PROPERTY = "com.sun.security.sasl.digest.utf8";
  
  private static final String REALM_PROPERTY = "com.sun.security.sasl.digest.realm";
  
  private static final String[] DIRECTIVE_KEY = { 
      "username", "realm", "nonce", "cnonce", "nonce-count", "qop", "digest-uri", "response", "maxbuf", "charset", 
      "cipher", "authzid", "auth-param" };
  
  private static final int USERNAME = 0;
  
  private static final int REALM = 1;
  
  private static final int NONCE = 2;
  
  private static final int CNONCE = 3;
  
  private static final int NONCE_COUNT = 4;
  
  private static final int QOP = 5;
  
  private static final int DIGEST_URI = 6;
  
  private static final int RESPONSE = 7;
  
  private static final int MAXBUF = 8;
  
  private static final int CHARSET = 9;
  
  private static final int CIPHER = 10;
  
  private static final int AUTHZID = 11;
  
  private static final int AUTH_PARAM = 12;
  
  private String specifiedQops;
  
  private byte[] myCiphers;
  
  private List<String> serverRealms = new ArrayList();
  
  DigestMD5Server(String paramString1, String paramString2, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    super(paramMap, MY_CLASS_NAME, 1, paramString1 + "/" + ((paramString2 == null) ? "*" : paramString2), paramCallbackHandler);
    if (paramMap != null) {
      this.specifiedQops = (String)paramMap.get("javax.security.sasl.qop");
      if ("false".equals((String)paramMap.get("com.sun.security.sasl.digest.utf8"))) {
        this.useUTF8 = false;
        logger.log(Level.FINE, "DIGEST80:Server supports ISO-Latin-1");
      } 
      String str = (String)paramMap.get("com.sun.security.sasl.digest.realm");
      if (str != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, ", \t\n");
        int i = stringTokenizer.countTokens();
        String str1 = null;
        for (byte b = 0; b < i; b++) {
          str1 = stringTokenizer.nextToken();
          logger.log(Level.FINE, "DIGEST81:Server supports realm {0}", str1);
          this.serverRealms.add(str1);
        } 
      } 
    } 
    this.encoding = this.useUTF8 ? "UTF8" : "8859_1";
    if (this.serverRealms.isEmpty()) {
      if (paramString2 == null)
        throw new SaslException("A realm must be provided in props or serverName"); 
      this.serverRealms.add(paramString2);
    } 
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte) throws SaslException {
    String str;
    byte[] arrayOfByte;
    if (paramArrayOfByte.length > 4096)
      throw new SaslException("DIGEST-MD5: Invalid digest response length. Got:  " + paramArrayOfByte.length + " Expected < " + 'က'); 
    switch (this.step) {
      case 1:
        if (paramArrayOfByte.length != 0)
          throw new SaslException("DIGEST-MD5 must not have an initial response"); 
        str = null;
        if ((this.allQop & 0x4) != 0) {
          this.myCiphers = getPlatformCiphers();
          StringBuffer stringBuffer = new StringBuffer();
          for (byte b = 0; b < CIPHER_TOKENS.length; b++) {
            if (this.myCiphers[b] != 0) {
              if (stringBuffer.length() > 0)
                stringBuffer.append(','); 
              stringBuffer.append(CIPHER_TOKENS[b]);
            } 
          } 
          str = stringBuffer.toString();
        } 
        try {
          arrayOfByte = generateChallenge(this.serverRealms, this.specifiedQops, str);
          this.step = 3;
          return arrayOfByte;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new SaslException("DIGEST-MD5: Error encoding challenge", unsupportedEncodingException);
        } catch (IOException iOException) {
          throw new SaslException("DIGEST-MD5: Error generating challenge", iOException);
        } 
      case 3:
        try {
          byte[][] arrayOfByte1 = parseDirectives(paramArrayOfByte, DIRECTIVE_KEY, null, 1);
          arrayOfByte = validateClientResponse(arrayOfByte1);
        } catch (SaslException saslException) {
          throw saslException;
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new SaslException("DIGEST-MD5: Error validating client response", unsupportedEncodingException);
        } finally {
          this.step = 0;
        } 
        this.completed = true;
        if (this.integrity && this.privacy) {
          this.secCtx = new DigestMD5Base.DigestPrivacy(this, false);
        } else if (this.integrity) {
          this.secCtx = new DigestMD5Base.DigestIntegrity(this, false);
        } 
        return arrayOfByte;
    } 
    throw new SaslException("DIGEST-MD5: Server at illegal state");
  }
  
  private byte[] generateChallenge(List<String> paramList, String paramString1, String paramString2) throws UnsupportedEncodingException, IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; paramList != null && b < paramList.size(); b++) {
      byteArrayOutputStream.write("realm=\"".getBytes(this.encoding));
      writeQuotedStringValue(byteArrayOutputStream, ((String)paramList.get(b)).getBytes(this.encoding));
      byteArrayOutputStream.write(34);
      byteArrayOutputStream.write(44);
    } 
    byteArrayOutputStream.write("nonce=\"".getBytes(this.encoding));
    this.nonce = generateNonce();
    writeQuotedStringValue(byteArrayOutputStream, this.nonce);
    byteArrayOutputStream.write(34);
    byteArrayOutputStream.write(44);
    if (paramString1 != null) {
      byteArrayOutputStream.write("qop=\"".getBytes(this.encoding));
      writeQuotedStringValue(byteArrayOutputStream, paramString1.getBytes(this.encoding));
      byteArrayOutputStream.write(34);
      byteArrayOutputStream.write(44);
    } 
    if (this.recvMaxBufSize != 65536)
      byteArrayOutputStream.write(("maxbuf=\"" + this.recvMaxBufSize + "\",").getBytes(this.encoding)); 
    if (this.useUTF8)
      byteArrayOutputStream.write("charset=utf-8,".getBytes(this.encoding)); 
    if (paramString2 != null) {
      byteArrayOutputStream.write("cipher=\"".getBytes(this.encoding));
      writeQuotedStringValue(byteArrayOutputStream, paramString2.getBytes(this.encoding));
      byteArrayOutputStream.write(34);
      byteArrayOutputStream.write(44);
    } 
    byteArrayOutputStream.write("algorithm=md5-sess".getBytes(this.encoding));
    return byteArrayOutputStream.toByteArray();
  }
  
  private byte[] validateClientResponse(byte[][] paramArrayOfByte) throws SaslException, UnsupportedEncodingException {
    byte b;
    String str1;
    if (paramArrayOfByte[9] != null && (!this.useUTF8 || !"utf-8".equals(new String(paramArrayOfByte[9], this.encoding))))
      throw new SaslException("DIGEST-MD5: digest response format violation. Incompatible charset value: " + new String(paramArrayOfByte[9])); 
    int i = (paramArrayOfByte[8] == null) ? 65536 : Integer.parseInt(new String(paramArrayOfByte[8], this.encoding));
    this.sendMaxBufSize = (this.sendMaxBufSize == 0) ? i : Math.min(this.sendMaxBufSize, i);
    if (paramArrayOfByte[0] != null) {
      str1 = new String(paramArrayOfByte[0], this.encoding);
      logger.log(Level.FINE, "DIGEST82:Username: {0}", str1);
    } else {
      throw new SaslException("DIGEST-MD5: digest response format violation. Missing username.");
    } 
    this.negotiatedRealm = (paramArrayOfByte[1] != null) ? new String(paramArrayOfByte[1], this.encoding) : "";
    logger.log(Level.FINE, "DIGEST83:Client negotiated realm: {0}", this.negotiatedRealm);
    if (!this.serverRealms.contains(this.negotiatedRealm))
      throw new SaslException("DIGEST-MD5: digest response format violation. Nonexistent realm: " + this.negotiatedRealm); 
    if (paramArrayOfByte[2] == null)
      throw new SaslException("DIGEST-MD5: digest response format violation. Missing nonce."); 
    byte[] arrayOfByte1 = paramArrayOfByte[2];
    if (!Arrays.equals(arrayOfByte1, this.nonce))
      throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched nonce."); 
    if (paramArrayOfByte[3] == null)
      throw new SaslException("DIGEST-MD5: digest response format violation. Missing cnonce."); 
    byte[] arrayOfByte2 = paramArrayOfByte[3];
    if (paramArrayOfByte[4] != null && 1 != Integer.parseInt(new String(paramArrayOfByte[4], this.encoding), 16))
      throw new SaslException("DIGEST-MD5: digest response format violation. Nonce count does not match: " + new String(paramArrayOfByte[4])); 
    this.negotiatedQop = (paramArrayOfByte[5] != null) ? new String(paramArrayOfByte[5], this.encoding) : "auth";
    logger.log(Level.FINE, "DIGEST84:Client negotiated qop: {0}", this.negotiatedQop);
    switch (this.negotiatedQop) {
      case "auth":
        b = 1;
        break;
      case "auth-int":
        b = 2;
        this.integrity = true;
        this.rawSendSize = this.sendMaxBufSize - 16;
        break;
      case "auth-conf":
        b = 4;
        this.integrity = this.privacy = true;
        this.rawSendSize = this.sendMaxBufSize - 26;
        break;
      default:
        throw new SaslException("DIGEST-MD5: digest response format violation. Invalid QOP: " + this.negotiatedQop);
    } 
    if ((b & this.allQop) == 0)
      throw new SaslException("DIGEST-MD5: server does not support  qop: " + this.negotiatedQop); 
    if (this.privacy) {
      this.negotiatedCipher = (paramArrayOfByte[10] != null) ? new String(paramArrayOfByte[10], this.encoding) : null;
      if (this.negotiatedCipher == null)
        throw new SaslException("DIGEST-MD5: digest response format violation. No cipher specified."); 
      byte b1 = -1;
      logger.log(Level.FINE, "DIGEST85:Client negotiated cipher: {0}", this.negotiatedCipher);
      for (byte b2 = 0; b2 < CIPHER_TOKENS.length; b2++) {
        if (this.negotiatedCipher.equals(CIPHER_TOKENS[b2]) && this.myCiphers[b2] != 0) {
          b1 = b2;
          break;
        } 
      } 
      if (b1 == -1)
        throw new SaslException("DIGEST-MD5: server does not support cipher: " + this.negotiatedCipher); 
      if ((CIPHER_MASKS[b1] & 0x4) != 0) {
        this.negotiatedStrength = "high";
      } else if ((CIPHER_MASKS[b1] & 0x2) != 0) {
        this.negotiatedStrength = "medium";
      } else {
        this.negotiatedStrength = "low";
      } 
      logger.log(Level.FINE, "DIGEST86:Negotiated strength: {0}", this.negotiatedStrength);
    } 
    String str2 = (paramArrayOfByte[6] != null) ? new String(paramArrayOfByte[6], this.encoding) : null;
    if (str2 != null)
      logger.log(Level.FINE, "DIGEST87:digest URI: {0}", str2); 
    if (uriMatches(this.digestUri, str2)) {
      this.digestUri = str2;
    } else {
      throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched URI: " + str2 + "; expecting: " + this.digestUri);
    } 
    byte[] arrayOfByte3 = paramArrayOfByte[7];
    if (arrayOfByte3 == null)
      throw new SaslException("DIGEST-MD5: digest response format  violation. Missing response."); 
    byte[] arrayOfByte4;
    String str3 = ((arrayOfByte4 = paramArrayOfByte[11]) != null) ? new String(arrayOfByte4, this.encoding) : str1;
    if (arrayOfByte4 != null)
      logger.log(Level.FINE, "DIGEST88:Authzid: {0}", new String(arrayOfByte4)); 
    try {
      RealmCallback realmCallback = new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
      NameCallback nameCallback = new NameCallback("DIGEST-MD5 authentication ID: ", str1);
      PasswordCallback passwordCallback = new PasswordCallback("DIGEST-MD5 password: ", false);
      this.cbh.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
      arrayOfChar = passwordCallback.getPassword();
      passwordCallback.clearPassword();
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire password", unsupportedCallbackException);
    } catch (IOException iOException) {
      throw new SaslException("DIGEST-MD5: IO error acquiring password", iOException);
    } 
    if (arrayOfChar == null)
      throw new SaslException("DIGEST-MD5: cannot acquire password for " + str1 + " in realm : " + this.negotiatedRealm); 
    try {
      byte[] arrayOfByte;
      try {
        arrayOfByte = generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, str1, this.negotiatedRealm, arrayOfChar, this.nonce, arrayOfByte2, 1, arrayOfByte4);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new SaslException("DIGEST-MD5: problem duplicating client response", noSuchAlgorithmException);
      } catch (IOException iOException) {
        throw new SaslException("DIGEST-MD5: problem duplicating client response", iOException);
      } 
      if (!Arrays.equals(arrayOfByte3, arrayOfByte))
        throw new SaslException("DIGEST-MD5: digest response format violation. Mismatched response."); 
      try {
        AuthorizeCallback authorizeCallback = new AuthorizeCallback(str1, str3);
        this.cbh.handle(new Callback[] { authorizeCallback });
        if (authorizeCallback.isAuthorized()) {
          this.authzid = authorizeCallback.getAuthorizedID();
        } else {
          throw new SaslException("DIGEST-MD5: " + str1 + " is not authorized to act as " + str3);
        } 
      } catch (SaslException saslException) {
        throw saslException;
      } catch (UnsupportedCallbackException unsupportedCallbackException) {
        throw new SaslException("DIGEST-MD5: Cannot perform callback to check authzid", unsupportedCallbackException);
      } catch (IOException iOException) {
        throw new SaslException("DIGEST-MD5: IO error checking authzid", iOException);
      } 
      return generateResponseAuth(str1, arrayOfChar, arrayOfByte2, 1, arrayOfByte4);
    } finally {
      for (byte b1 = 0; b1 < arrayOfChar.length; b1++)
        arrayOfChar[b1] = Character.MIN_VALUE; 
    } 
  }
  
  private static boolean uriMatches(String paramString1, String paramString2) {
    if (paramString1.equalsIgnoreCase(paramString2))
      return true; 
    if (paramString1.endsWith("/*")) {
      int i = paramString1.length() - 1;
      String str1 = paramString1.substring(0, i);
      String str2 = paramString2.substring(0, i);
      return str1.equalsIgnoreCase(str2);
    } 
    return false;
  }
  
  private byte[] generateResponseAuth(String paramString, char[] paramArrayOfChar, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) throws SaslException {
    try {
      byte[] arrayOfByte1 = generateResponseValue("", this.digestUri, this.negotiatedQop, paramString, this.negotiatedRealm, paramArrayOfChar, this.nonce, paramArrayOfByte1, paramInt, paramArrayOfByte2);
      byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 8];
      System.arraycopy("rspauth=".getBytes(this.encoding), 0, arrayOfByte2, 0, 8);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 8, arrayOfByte1.length);
      return arrayOfByte2;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SaslException("DIGEST-MD5: problem generating response", noSuchAlgorithmException);
    } catch (IOException iOException) {
      throw new SaslException("DIGEST-MD5: problem generating response", iOException);
    } 
  }
  
  public String getAuthorizationID() {
    if (this.completed)
      return this.authzid; 
    throw new IllegalStateException("DIGEST-MD5 server negotiation not complete");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\digest\DigestMD5Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */