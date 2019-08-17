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
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

final class DigestMD5Client extends DigestMD5Base implements SaslClient {
  private static final String MY_CLASS_NAME = DigestMD5Client.class.getName();
  
  private static final String CIPHER_PROPERTY = "com.sun.security.sasl.digest.cipher";
  
  private static final String[] DIRECTIVE_KEY = { "realm", "qop", "algorithm", "nonce", "maxbuf", "charset", "cipher", "rspauth", "stale" };
  
  private static final int REALM = 0;
  
  private static final int QOP = 1;
  
  private static final int ALGORITHM = 2;
  
  private static final int NONCE = 3;
  
  private static final int MAXBUF = 4;
  
  private static final int CHARSET = 5;
  
  private static final int CIPHER = 6;
  
  private static final int RESPONSE_AUTH = 7;
  
  private static final int STALE = 8;
  
  private int nonceCount;
  
  private String specifiedCipher;
  
  private byte[] cnonce;
  
  private String username;
  
  private char[] passwd;
  
  private byte[] authzidBytes;
  
  DigestMD5Client(String paramString1, String paramString2, String paramString3, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler) throws SaslException {
    super(paramMap, MY_CLASS_NAME, 2, paramString2 + "/" + paramString3, paramCallbackHandler);
    if (paramString1 != null) {
      this.authzid = paramString1;
      try {
        this.authzidBytes = paramString1.getBytes("UTF8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new SaslException("DIGEST-MD5: Error encoding authzid value into UTF-8", unsupportedEncodingException);
      } 
    } 
    if (paramMap != null) {
      this.specifiedCipher = (String)paramMap.get("com.sun.security.sasl.digest.cipher");
      logger.log(Level.FINE, "DIGEST60:Explicitly specified cipher: {0}", this.specifiedCipher);
    } 
  }
  
  public boolean hasInitialResponse() { return false; }
  
  public byte[] evaluateChallenge(byte[] paramArrayOfByte) throws SaslException {
    ArrayList arrayList;
    byte[][] arrayOfByte;
    if (paramArrayOfByte.length > 2048)
      throw new SaslException("DIGEST-MD5: Invalid digest-challenge length. Got:  " + paramArrayOfByte.length + " Expected < " + 'ࠀ'); 
    switch (this.step) {
      case 2:
        arrayList = new ArrayList(3);
        arrayOfByte = parseDirectives(paramArrayOfByte, DIRECTIVE_KEY, arrayList, 0);
        try {
          processChallenge(arrayOfByte, arrayList);
          checkQopSupport(arrayOfByte[1], arrayOfByte[6]);
          this.step++;
          return generateClientResponse(arrayOfByte[5]);
        } catch (SaslException saslException) {
          this.step = 0;
          clearPassword();
          throw saslException;
        } catch (IOException iOException) {
          this.step = 0;
          clearPassword();
          throw new SaslException("DIGEST-MD5: Error generating digest response-value", iOException);
        } 
      case 3:
        try {
          arrayOfByte = parseDirectives(paramArrayOfByte, DIRECTIVE_KEY, null, 0);
          validateResponseValue(arrayOfByte[7]);
          if (this.integrity && this.privacy) {
            this.secCtx = new DigestMD5Base.DigestPrivacy(this, true);
          } else if (this.integrity) {
            this.secCtx = new DigestMD5Base.DigestIntegrity(this, true);
          } 
          return null;
        } finally {
          clearPassword();
          this.step = 0;
          this.completed = true;
        } 
    } 
    throw new SaslException("DIGEST-MD5: Client at illegal state");
  }
  
  private void processChallenge(byte[][] paramArrayOfByte, List<byte[]> paramList) throws SaslException, UnsupportedEncodingException {
    if (paramArrayOfByte[5] != null) {
      if (!"utf-8".equals(new String(paramArrayOfByte[5], this.encoding)))
        throw new SaslException("DIGEST-MD5: digest-challenge format violation. Unrecognised charset value: " + new String(paramArrayOfByte[5])); 
      this.encoding = "UTF8";
      this.useUTF8 = true;
    } 
    if (paramArrayOfByte[2] == null)
      throw new SaslException("DIGEST-MD5: Digest-challenge format violation: algorithm directive missing"); 
    if (!"md5-sess".equals(new String(paramArrayOfByte[2], this.encoding)))
      throw new SaslException("DIGEST-MD5: Digest-challenge format violation. Invalid value for 'algorithm' directive: " + paramArrayOfByte[2]); 
    if (paramArrayOfByte[3] == null)
      throw new SaslException("DIGEST-MD5: Digest-challenge format violation: nonce directive missing"); 
    this.nonce = paramArrayOfByte[3];
    try {
      String[] arrayOfString = null;
      if (paramArrayOfByte[0] != null)
        if (paramList == null || paramList.size() <= 1) {
          this.negotiatedRealm = new String(paramArrayOfByte[0], this.encoding);
        } else {
          arrayOfString = new String[paramList.size()];
          for (byte b = 0; b < arrayOfString.length; b++)
            arrayOfString[b] = new String((byte[])paramList.get(b), this.encoding); 
        }  
      NameCallback nameCallback = (this.authzid == null) ? new NameCallback("DIGEST-MD5 authentication ID: ") : new NameCallback("DIGEST-MD5 authentication ID: ", this.authzid);
      PasswordCallback passwordCallback = new PasswordCallback("DIGEST-MD5 password: ", false);
      if (arrayOfString == null) {
        RealmCallback realmCallback = (this.negotiatedRealm == null) ? new RealmCallback("DIGEST-MD5 realm: ") : new RealmCallback("DIGEST-MD5 realm: ", this.negotiatedRealm);
        this.cbh.handle(new Callback[] { realmCallback, nameCallback, passwordCallback });
        this.negotiatedRealm = realmCallback.getText();
        if (this.negotiatedRealm == null)
          this.negotiatedRealm = ""; 
      } else {
        RealmChoiceCallback realmChoiceCallback = new RealmChoiceCallback("DIGEST-MD5 realm: ", arrayOfString, 0, false);
        this.cbh.handle(new Callback[] { realmChoiceCallback, nameCallback, passwordCallback });
        int[] arrayOfInt = realmChoiceCallback.getSelectedIndexes();
        if (arrayOfInt == null || arrayOfInt[0] < 0 || arrayOfInt[0] >= arrayOfString.length)
          throw new SaslException("DIGEST-MD5: Invalid realm chosen"); 
        this.negotiatedRealm = arrayOfString[arrayOfInt[0]];
      } 
      this.passwd = passwordCallback.getPassword();
      passwordCallback.clearPassword();
      this.username = nameCallback.getName();
    } catch (SaslException saslException) {
      throw saslException;
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new SaslException("DIGEST-MD5: Cannot perform callback to acquire realm, authentication ID or password", unsupportedCallbackException);
    } catch (IOException iOException) {
      throw new SaslException("DIGEST-MD5: Error acquiring realm, authentication ID or password", iOException);
    } 
    if (this.username == null || this.passwd == null)
      throw new SaslException("DIGEST-MD5: authentication ID and password must be specified"); 
    int i = (paramArrayOfByte[4] == null) ? 65536 : Integer.parseInt(new String(paramArrayOfByte[4], this.encoding));
    this.sendMaxBufSize = (this.sendMaxBufSize == 0) ? i : Math.min(this.sendMaxBufSize, i);
  }
  
  private void checkQopSupport(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws IOException {
    String str;
    if (paramArrayOfByte1 == null) {
      str = "auth";
    } else {
      str = new String(paramArrayOfByte1, this.encoding);
    } 
    String[] arrayOfString = new String[3];
    byte[] arrayOfByte = parseQop(str, arrayOfString, true);
    byte b = combineMasks(arrayOfByte);
    switch (findPreferredMask(b, this.qop)) {
      case 0:
        throw new SaslException("DIGEST-MD5: No common protection layer between client and server");
      case 1:
        this.negotiatedQop = "auth";
        break;
      case 2:
        this.negotiatedQop = "auth-int";
        this.integrity = true;
        this.rawSendSize = this.sendMaxBufSize - 16;
        break;
      case 4:
        this.negotiatedQop = "auth-conf";
        this.privacy = this.integrity = true;
        this.rawSendSize = this.sendMaxBufSize - 26;
        checkStrengthSupport(paramArrayOfByte2);
        break;
    } 
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST61:Raw send size: {0}", new Integer(this.rawSendSize)); 
  }
  
  private void checkStrengthSupport(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte == null)
      throw new SaslException("DIGEST-MD5: server did not specify cipher to use for 'auth-conf'"); 
    String str1 = new String(paramArrayOfByte, this.encoding);
    StringTokenizer stringTokenizer = new StringTokenizer(str1, ", \t\n");
    int i = stringTokenizer.countTokens();
    String str2 = null;
    byte[] arrayOfByte1 = { 0, 0, 0, 0, 0 };
    String[] arrayOfString = new String[arrayOfByte1.length];
    for (byte b1 = 0; b1 < i; b1++) {
      str2 = stringTokenizer.nextToken();
      for (byte b3 = 0; b3 < CIPHER_TOKENS.length; b3++) {
        if (str2.equals(CIPHER_TOKENS[b3])) {
          arrayOfByte1[b3] = (byte)(arrayOfByte1[b3] | CIPHER_MASKS[b3]);
          arrayOfString[b3] = str2;
          logger.log(Level.FINE, "DIGEST62:Server supports {0}", str2);
        } 
      } 
    } 
    byte[] arrayOfByte2 = getPlatformCiphers();
    byte b = 0;
    for (byte b2 = 0; b2 < arrayOfByte1.length; b2++) {
      arrayOfByte1[b2] = (byte)(arrayOfByte1[b2] & arrayOfByte2[b2]);
      b = (byte)(b | arrayOfByte1[b2]);
    } 
    if (b == 0)
      throw new SaslException("DIGEST-MD5: Client supports none of these cipher suites: " + str1); 
    this.negotiatedCipher = findCipherAndStrength(arrayOfByte1, arrayOfString);
    if (this.negotiatedCipher == null)
      throw new SaslException("DIGEST-MD5: Unable to negotiate a strength level for 'auth-conf'"); 
    logger.log(Level.FINE, "DIGEST63:Cipher suite: {0}", this.negotiatedCipher);
  }
  
  private String findCipherAndStrength(byte[] paramArrayOfByte, String[] paramArrayOfString) {
    for (byte b = 0; b < this.strength.length; b++) {
      byte b1;
      if ((b1 = this.strength[b]) != 0)
        for (byte b2 = 0; b2 < paramArrayOfByte.length; b2++) {
          if (b1 == paramArrayOfByte[b2] && (this.specifiedCipher == null || this.specifiedCipher.equals(paramArrayOfString[b2]))) {
            switch (b1) {
              case 4:
                this.negotiatedStrength = "high";
                break;
              case 2:
                this.negotiatedStrength = "medium";
                break;
              case 1:
                this.negotiatedStrength = "low";
                break;
            } 
            return paramArrayOfString[b2];
          } 
        }  
    } 
    return null;
  }
  
  private byte[] generateClientResponse(byte[] paramArrayOfByte) throws SaslException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    if (this.useUTF8) {
      byteArrayOutputStream.write("charset=".getBytes(this.encoding));
      byteArrayOutputStream.write(paramArrayOfByte);
      byteArrayOutputStream.write(44);
    } 
    byteArrayOutputStream.write(("username=\"" + quotedStringValue(this.username) + "\",").getBytes(this.encoding));
    if (this.negotiatedRealm.length() > 0)
      byteArrayOutputStream.write(("realm=\"" + quotedStringValue(this.negotiatedRealm) + "\",").getBytes(this.encoding)); 
    byteArrayOutputStream.write("nonce=\"".getBytes(this.encoding));
    writeQuotedStringValue(byteArrayOutputStream, this.nonce);
    byteArrayOutputStream.write(34);
    byteArrayOutputStream.write(44);
    this.nonceCount = getNonceCount(this.nonce);
    byteArrayOutputStream.write(("nc=" + nonceCountToHex(this.nonceCount) + ",").getBytes(this.encoding));
    this.cnonce = generateNonce();
    byteArrayOutputStream.write("cnonce=\"".getBytes(this.encoding));
    writeQuotedStringValue(byteArrayOutputStream, this.cnonce);
    byteArrayOutputStream.write("\",".getBytes(this.encoding));
    byteArrayOutputStream.write(("digest-uri=\"" + this.digestUri + "\",").getBytes(this.encoding));
    byteArrayOutputStream.write("maxbuf=".getBytes(this.encoding));
    byteArrayOutputStream.write(String.valueOf(this.recvMaxBufSize).getBytes(this.encoding));
    byteArrayOutputStream.write(44);
    try {
      byteArrayOutputStream.write("response=".getBytes(this.encoding));
      byteArrayOutputStream.write(generateResponseValue("AUTHENTICATE", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes));
      byteArrayOutputStream.write(44);
    } catch (Exception exception) {
      throw new SaslException("DIGEST-MD5: Error generating response value", exception);
    } 
    byteArrayOutputStream.write(("qop=" + this.negotiatedQop).getBytes(this.encoding));
    if (this.negotiatedCipher != null)
      byteArrayOutputStream.write((",cipher=\"" + this.negotiatedCipher + "\"").getBytes(this.encoding)); 
    if (this.authzidBytes != null) {
      byteArrayOutputStream.write(",authzid=\"".getBytes(this.encoding));
      writeQuotedStringValue(byteArrayOutputStream, this.authzidBytes);
      byteArrayOutputStream.write("\"".getBytes(this.encoding));
    } 
    if (byteArrayOutputStream.size() > 4096)
      throw new SaslException("DIGEST-MD5: digest-response size too large. Length: " + byteArrayOutputStream.size()); 
    return byteArrayOutputStream.toByteArray();
  }
  
  private void validateResponseValue(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte == null)
      throw new SaslException("DIGEST-MD5: Authenication failed. Expecting 'rspauth' authentication success message"); 
    try {
      byte[] arrayOfByte = generateResponseValue("", this.digestUri, this.negotiatedQop, this.username, this.negotiatedRealm, this.passwd, this.nonce, this.cnonce, this.nonceCount, this.authzidBytes);
      if (!Arrays.equals(arrayOfByte, paramArrayOfByte))
        throw new SaslException("Server's rspauth value does not match what client expects"); 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SaslException("Problem generating response value for verification", noSuchAlgorithmException);
    } catch (IOException iOException) {
      throw new SaslException("Problem generating response value for verification", iOException);
    } 
  }
  
  private static int getNonceCount(byte[] paramArrayOfByte) { return 1; }
  
  private void clearPassword() {
    if (this.passwd != null) {
      for (byte b = 0; b < this.passwd.length; b++)
        this.passwd[b] = Character.MIN_VALUE; 
      this.passwd = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\digest\DigestMD5Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */