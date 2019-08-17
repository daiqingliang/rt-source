package com.sun.security.sasl.digest;

import com.sun.security.sasl.util.AbstractSaslImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;

abstract class DigestMD5Base extends AbstractSaslImpl {
  private static final String DI_CLASS_NAME = DigestIntegrity.class.getName();
  
  private static final String DP_CLASS_NAME = DigestPrivacy.class.getName();
  
  protected static final int MAX_CHALLENGE_LENGTH = 2048;
  
  protected static final int MAX_RESPONSE_LENGTH = 4096;
  
  protected static final int DEFAULT_MAXBUF = 65536;
  
  protected static final int DES3 = 0;
  
  protected static final int RC4 = 1;
  
  protected static final int DES = 2;
  
  protected static final int RC4_56 = 3;
  
  protected static final int RC4_40 = 4;
  
  protected static final String[] CIPHER_TOKENS = { "3des", "rc4", "des", "rc4-56", "rc4-40" };
  
  private static final String[] JCE_CIPHER_NAME = { "DESede/CBC/NoPadding", "RC4", "DES/CBC/NoPadding" };
  
  protected static final byte DES_3_STRENGTH = 4;
  
  protected static final byte RC4_STRENGTH = 4;
  
  protected static final byte DES_STRENGTH = 2;
  
  protected static final byte RC4_56_STRENGTH = 2;
  
  protected static final byte RC4_40_STRENGTH = 1;
  
  protected static final byte UNSET = 0;
  
  protected static final byte[] CIPHER_MASKS = { 4, 4, 2, 2, 1 };
  
  private static final String SECURITY_LAYER_MARKER = ":00000000000000000000000000000000";
  
  protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
  
  protected int step;
  
  protected CallbackHandler cbh;
  
  protected SecurityCtx secCtx;
  
  protected byte[] H_A1;
  
  protected byte[] nonce;
  
  protected String negotiatedStrength;
  
  protected String negotiatedCipher;
  
  protected String negotiatedQop;
  
  protected String negotiatedRealm;
  
  protected boolean useUTF8 = false;
  
  protected String encoding = "8859_1";
  
  protected String digestUri;
  
  protected String authzid;
  
  private static final char[] pem_array = { 
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
      'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
      'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
      'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
      'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
      '8', '9', '+', '/' };
  
  private static final int RAW_NONCE_SIZE = 30;
  
  private static final int ENCODED_NONCE_SIZE = 40;
  
  private static final BigInteger MASK = new BigInteger("7f", 16);
  
  protected DigestMD5Base(Map<String, ?> paramMap, String paramString1, int paramInt, String paramString2, CallbackHandler paramCallbackHandler) throws SaslException {
    super(paramMap, paramString1);
    this.step = paramInt;
    this.digestUri = paramString2;
    this.cbh = paramCallbackHandler;
  }
  
  public String getMechanismName() { return "DIGEST-MD5"; }
  
  public byte[] unwrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (!this.completed)
      throw new IllegalStateException("DIGEST-MD5 authentication not completed"); 
    if (this.secCtx == null)
      throw new IllegalStateException("Neither integrity nor privacy was negotiated"); 
    return this.secCtx.unwrap(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] wrap(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (!this.completed)
      throw new IllegalStateException("DIGEST-MD5 authentication not completed"); 
    if (this.secCtx == null)
      throw new IllegalStateException("Neither integrity nor privacy was negotiated"); 
    return this.secCtx.wrap(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void dispose() throws SaslException {
    if (this.secCtx != null)
      this.secCtx = null; 
  }
  
  public Object getNegotiatedProperty(String paramString) {
    if (this.completed)
      return paramString.equals("javax.security.sasl.strength") ? this.negotiatedStrength : (paramString.equals("javax.security.sasl.bound.server.name") ? this.digestUri.substring(this.digestUri.indexOf('/') + 1) : super.getNegotiatedProperty(paramString)); 
    throw new IllegalStateException("DIGEST-MD5 authentication not completed");
  }
  
  protected static final byte[] generateNonce() {
    Random random = new Random();
    byte[] arrayOfByte1 = new byte[30];
    random.nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[40];
    byte b = 0;
    for (boolean bool = false; bool < arrayOfByte1.length; bool += true) {
      byte b1 = arrayOfByte1[bool];
      byte b2 = arrayOfByte1[bool + true];
      byte b3 = arrayOfByte1[bool + 2];
      arrayOfByte2[b++] = (byte)pem_array[b1 >>> 2 & 0x3F];
      arrayOfByte2[b++] = (byte)pem_array[(b1 << 4 & 0x30) + (b2 >>> 4 & 0xF)];
      arrayOfByte2[b++] = (byte)pem_array[(b2 << 2 & 0x3C) + (b3 >>> 6 & 0x3)];
      arrayOfByte2[b++] = (byte)pem_array[b3 & 0x3F];
    } 
    return arrayOfByte2;
  }
  
  protected static void writeQuotedStringValue(ByteArrayOutputStream paramByteArrayOutputStream, byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    for (byte b = 0; b < i; b++) {
      byte b1 = paramArrayOfByte[b];
      if (needEscape((char)b1))
        paramByteArrayOutputStream.write(92); 
      paramByteArrayOutputStream.write(b1);
    } 
  }
  
  private static boolean needEscape(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      if (needEscape(paramString.charAt(b)))
        return true; 
    } 
    return false;
  }
  
  private static boolean needEscape(char paramChar) { return (paramChar == '"' || paramChar == '\\' || paramChar == '' || (paramChar >= '\000' && paramChar <= '\037' && paramChar != '\r' && paramChar != '\t' && paramChar != '\n')); }
  
  protected static String quotedStringValue(String paramString) {
    if (needEscape(paramString)) {
      int i = paramString.length();
      char[] arrayOfChar = new char[i + i];
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        char c = paramString.charAt(b2);
        if (needEscape(c))
          arrayOfChar[b1++] = '\\'; 
        arrayOfChar[b1++] = c;
      } 
      return new String(arrayOfChar, 0, b1);
    } 
    return paramString;
  }
  
  protected byte[] binaryToHex(byte[] paramArrayOfByte) throws UnsupportedEncodingException {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      if ((paramArrayOfByte[b] & 0xFF) < 16) {
        stringBuffer.append("0" + Integer.toHexString(paramArrayOfByte[b] & 0xFF));
      } else {
        stringBuffer.append(Integer.toHexString(paramArrayOfByte[b] & 0xFF));
      } 
    } 
    return stringBuffer.toString().getBytes(this.encoding);
  }
  
  protected byte[] stringToByte_8859_1(String paramString) throws SaslException {
    char[] arrayOfChar = paramString.toCharArray();
    try {
      if (this.useUTF8)
        for (byte b = 0; b < arrayOfChar.length; b++) {
          if (arrayOfChar[b] > 'ÿ')
            return paramString.getBytes("UTF8"); 
        }  
      return paramString.getBytes("8859_1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new SaslException("cannot encode string in UTF8 or 8859-1 (Latin-1)", unsupportedEncodingException);
    } 
  }
  
  protected static byte[] getPlatformCiphers() {
    byte[] arrayOfByte = new byte[CIPHER_TOKENS.length];
    for (byte b = 0; b < JCE_CIPHER_NAME.length; b++) {
      try {
        Cipher.getInstance(JCE_CIPHER_NAME[b]);
        logger.log(Level.FINE, "DIGEST01:Platform supports {0}", JCE_CIPHER_NAME[b]);
        arrayOfByte[b] = (byte)(arrayOfByte[b] | CIPHER_MASKS[b]);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      
      } catch (NoSuchPaddingException noSuchPaddingException) {}
    } 
    if (arrayOfByte[1] != 0) {
      arrayOfByte[3] = (byte)(arrayOfByte[3] | CIPHER_MASKS[3]);
      arrayOfByte[4] = (byte)(arrayOfByte[4] | CIPHER_MASKS[4]);
    } 
    return arrayOfByte;
  }
  
  protected byte[] generateResponseValue(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, char[] paramArrayOfChar, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt, byte[] paramArrayOfByte3) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
    byteArrayOutputStream1.write((paramString1 + ":" + paramString2).getBytes(this.encoding));
    if (paramString3.equals("auth-conf") || paramString3.equals("auth-int")) {
      logger.log(Level.FINE, "DIGEST04:QOP: {0}", paramString3);
      byteArrayOutputStream1.write(":00000000000000000000000000000000".getBytes(this.encoding));
    } 
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST05:A2: {0}", byteArrayOutputStream1.toString()); 
    messageDigest.update(byteArrayOutputStream1.toByteArray());
    byte[] arrayOfByte3 = messageDigest.digest();
    byte[] arrayOfByte2 = binaryToHex(arrayOfByte3);
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST06:HEX(H(A2)): {0}", new String(arrayOfByte2)); 
    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
    byteArrayOutputStream2.write(stringToByte_8859_1(paramString4));
    byteArrayOutputStream2.write(58);
    byteArrayOutputStream2.write(stringToByte_8859_1(paramString5));
    byteArrayOutputStream2.write(58);
    byteArrayOutputStream2.write(stringToByte_8859_1(new String(paramArrayOfChar)));
    messageDigest.update(byteArrayOutputStream2.toByteArray());
    arrayOfByte3 = messageDigest.digest();
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST07:H({0}) = {1}", new Object[] { byteArrayOutputStream2.toString(), new String(binaryToHex(arrayOfByte3)) }); 
    ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
    byteArrayOutputStream3.write(arrayOfByte3);
    byteArrayOutputStream3.write(58);
    byteArrayOutputStream3.write(paramArrayOfByte1);
    byteArrayOutputStream3.write(58);
    byteArrayOutputStream3.write(paramArrayOfByte2);
    if (paramArrayOfByte3 != null) {
      byteArrayOutputStream3.write(58);
      byteArrayOutputStream3.write(paramArrayOfByte3);
    } 
    messageDigest.update(byteArrayOutputStream3.toByteArray());
    arrayOfByte3 = messageDigest.digest();
    this.H_A1 = arrayOfByte3;
    byte[] arrayOfByte1 = binaryToHex(arrayOfByte3);
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST08:H(A1) = {0}", new String(arrayOfByte1)); 
    ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream();
    byteArrayOutputStream4.write(arrayOfByte1);
    byteArrayOutputStream4.write(58);
    byteArrayOutputStream4.write(paramArrayOfByte1);
    byteArrayOutputStream4.write(58);
    byteArrayOutputStream4.write(nonceCountToHex(paramInt).getBytes(this.encoding));
    byteArrayOutputStream4.write(58);
    byteArrayOutputStream4.write(paramArrayOfByte2);
    byteArrayOutputStream4.write(58);
    byteArrayOutputStream4.write(paramString3.getBytes(this.encoding));
    byteArrayOutputStream4.write(58);
    byteArrayOutputStream4.write(arrayOfByte2);
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST09:KD: {0}", byteArrayOutputStream4.toString()); 
    messageDigest.update(byteArrayOutputStream4.toByteArray());
    arrayOfByte3 = messageDigest.digest();
    byte[] arrayOfByte4 = binaryToHex(arrayOfByte3);
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "DIGEST10:response-value: {0}", new String(arrayOfByte4)); 
    return arrayOfByte4;
  }
  
  protected static String nonceCountToHex(int paramInt) {
    String str = Integer.toHexString(paramInt);
    StringBuffer stringBuffer = new StringBuffer();
    if (str.length() < 8)
      for (byte b = 0; b < 8 - str.length(); b++)
        stringBuffer.append("0");  
    return stringBuffer.toString() + str;
  }
  
  protected static byte[][] parseDirectives(byte[] paramArrayOfByte, String[] paramArrayOfString, List<byte[]> paramList, int paramInt) throws SaslException {
    byte[][] arrayOfByte = new byte[paramArrayOfString.length][];
    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream(10);
    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream(10);
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = false;
    for (int i = skipLws(paramArrayOfByte, 0); i < paramArrayOfByte.length; i++) {
      byte b = paramArrayOfByte[i];
      if (bool1) {
        if (b == 44) {
          if (byteArrayOutputStream1.size() != 0)
            throw new SaslException("Directive key contains a ',':" + byteArrayOutputStream1); 
          i = skipLws(paramArrayOfByte, i + 1);
          continue;
        } 
        if (b == 61) {
          if (byteArrayOutputStream1.size() == 0)
            throw new SaslException("Empty directive key"); 
          bool1 = false;
          i = skipLws(paramArrayOfByte, i + 1);
          if (i < paramArrayOfByte.length) {
            if (paramArrayOfByte[i] == 34) {
              bool2 = true;
              i++;
            } 
            continue;
          } 
          throw new SaslException("Valueless directive found: " + byteArrayOutputStream1.toString());
        } 
        if (isLws(b)) {
          i = skipLws(paramArrayOfByte, i + 1);
          if (i < paramArrayOfByte.length) {
            if (paramArrayOfByte[i] != 61)
              throw new SaslException("'=' expected after key: " + byteArrayOutputStream1.toString()); 
            continue;
          } 
          throw new SaslException("'=' expected after key: " + byteArrayOutputStream1.toString());
        } 
        byteArrayOutputStream1.write(b);
        i++;
        continue;
      } 
      if (bool2) {
        if (b == 92) {
          if (++i < paramArrayOfByte.length) {
            byteArrayOutputStream2.write(paramArrayOfByte[i]);
            i++;
            continue;
          } 
          throw new SaslException("Unmatched quote found for directive: " + byteArrayOutputStream1.toString() + " with value: " + byteArrayOutputStream2.toString());
        } 
        if (b == 34) {
          i++;
          bool2 = false;
          bool3 = true;
          continue;
        } 
        byteArrayOutputStream2.write(b);
        i++;
        continue;
      } 
      if (isLws(b) || b == 44) {
        extractDirective(byteArrayOutputStream1.toString(), byteArrayOutputStream2.toByteArray(), paramArrayOfString, arrayOfByte, paramList, paramInt);
        byteArrayOutputStream1.reset();
        byteArrayOutputStream2.reset();
        bool1 = true;
        bool2 = bool3 = false;
        i = skipLws(paramArrayOfByte, i + 1);
        continue;
      } 
      if (bool3)
        throw new SaslException("Expecting comma or linear whitespace after quoted string: \"" + byteArrayOutputStream2.toString() + "\""); 
      byteArrayOutputStream2.write(b);
    } 
    if (bool2)
      throw new SaslException("Unmatched quote found for directive: " + byteArrayOutputStream1.toString() + " with value: " + byteArrayOutputStream2.toString()); 
    if (byteArrayOutputStream1.size() > 0)
      extractDirective(byteArrayOutputStream1.toString(), byteArrayOutputStream2.toByteArray(), paramArrayOfString, arrayOfByte, paramList, paramInt); 
    return arrayOfByte;
  }
  
  private static boolean isLws(byte paramByte) {
    switch (paramByte) {
      case 9:
      case 10:
      case 13:
      case 32:
        return true;
    } 
    return false;
  }
  
  private static int skipLws(byte[] paramArrayOfByte, int paramInt) {
    int i;
    for (i = paramInt; i < paramArrayOfByte.length; i++) {
      if (!isLws(paramArrayOfByte[i]))
        return i; 
    } 
    return i;
  }
  
  private static void extractDirective(String paramString, byte[] paramArrayOfByte, String[] paramArrayOfString, byte[][] paramArrayOfByte1, List<byte[]> paramList, int paramInt) throws SaslException {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equalsIgnoreCase(paramArrayOfString[b])) {
        if (paramArrayOfByte1[b] == null) {
          paramArrayOfByte1[b] = paramArrayOfByte;
          if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "DIGEST11:Directive {0} = {1}", new Object[] { paramArrayOfString[b], new String(paramArrayOfByte1[b]) }); 
          break;
        } 
        if (paramList != null && b == paramInt) {
          if (paramList.isEmpty())
            paramList.add(paramArrayOfByte1[b]); 
          paramList.add(paramArrayOfByte);
          break;
        } 
        throw new SaslException("DIGEST-MD5: peer sent more than one " + paramString + " directive: " + new String(paramArrayOfByte));
      } 
    } 
  }
  
  private static void setParityBit(byte[] paramArrayOfByte) {
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      byte b1 = paramArrayOfByte[b] & 0xFE;
      b1 |= Integer.bitCount(b1) & true ^ true;
      paramArrayOfByte[b] = (byte)b1;
    } 
  }
  
  private static byte[] addDesParity(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SaslException {
    if (paramInt2 != 7)
      throw new IllegalArgumentException("Invalid length of DES Key Value:" + paramInt2); 
    byte[] arrayOfByte1 = new byte[7];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte1, 0, paramInt2);
    byte[] arrayOfByte2 = new byte[8];
    BigInteger bigInteger = new BigInteger(arrayOfByte1);
    for (int i = arrayOfByte2.length - 1; i >= 0; i--) {
      arrayOfByte2[i] = bigInteger.and(MASK).toByteArray()[0];
      arrayOfByte2[i] = (byte)(arrayOfByte2[i] << 1);
      bigInteger = bigInteger.shiftRight(7);
    } 
    setParityBit(arrayOfByte2);
    return arrayOfByte2;
  }
  
  private static SecretKey makeDesKeys(byte[] paramArrayOfByte, String paramString) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
    byte[] arrayOfByte3;
    byte[] arrayOfByte2;
    DESedeKeySpec dESedeKeySpec;
    byte[] arrayOfByte1 = addDesParity(paramArrayOfByte, 0, 7);
    DESKeySpec dESKeySpec = null;
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(paramString);
    switch (paramString) {
      case "des":
        dESKeySpec = new DESKeySpec(arrayOfByte1, 0);
        if (logger.isLoggable(Level.FINEST)) {
          traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST42:DES key input: ", paramArrayOfByte);
          traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST43:DES key parity-adjusted: ", arrayOfByte1);
          traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST44:DES key material: ", ((DESKeySpec)dESKeySpec).getKey());
          logger.log(Level.FINEST, "DIGEST45: is parity-adjusted? {0}", Boolean.valueOf(DESKeySpec.isParityAdjusted(arrayOfByte1, 0)));
        } 
        return secretKeyFactory.generateSecret(dESKeySpec);
      case "desede":
        arrayOfByte2 = addDesParity(paramArrayOfByte, 7, 7);
        arrayOfByte3 = new byte[arrayOfByte1.length * 2 + arrayOfByte2.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, arrayOfByte1.length + arrayOfByte2.length, arrayOfByte1.length);
        dESedeKeySpec = new DESedeKeySpec(arrayOfByte3, 0);
        if (logger.isLoggable(Level.FINEST)) {
          traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST46:3DES key input: ", paramArrayOfByte);
          traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST47:3DES key ede: ", arrayOfByte3);
          traceOutput(DP_CLASS_NAME, "makeDesKeys", "DIGEST48:3DES key material: ", ((DESedeKeySpec)dESedeKeySpec).getKey());
          logger.log(Level.FINEST, "DIGEST49: is parity-adjusted? ", Boolean.valueOf(DESedeKeySpec.isParityAdjusted(arrayOfByte3, 0)));
        } 
        return secretKeyFactory.generateSecret(dESedeKeySpec);
    } 
    throw new IllegalArgumentException("Invalid DES strength:" + paramString);
  }
  
  class DigestIntegrity implements SecurityCtx {
    private static final String CLIENT_INT_MAGIC = "Digest session key to client-to-server signing key magic constant";
    
    private static final String SVR_INT_MAGIC = "Digest session key to server-to-client signing key magic constant";
    
    protected byte[] myKi;
    
    protected byte[] peerKi;
    
    protected int mySeqNum = 0;
    
    protected int peerSeqNum = 0;
    
    protected final byte[] messageType = new byte[2];
    
    protected final byte[] sequenceNum = new byte[4];
    
    DigestIntegrity(boolean param1Boolean) throws SaslException {
      try {
        generateIntegrityKeyPair(param1Boolean);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new SaslException("DIGEST-MD5: Error encoding strings into UTF-8", unsupportedEncodingException);
      } catch (IOException iOException) {
        throw new SaslException("DIGEST-MD5: Error accessing buffers required to create integrity key pairs", iOException);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new SaslException("DIGEST-MD5: Unsupported digest algorithm used to create integrity key pairs", noSuchAlgorithmException);
      } 
      DigestMD5Base.intToNetworkByteOrder(1, this.messageType, 0, 2);
    }
    
    private void generateIntegrityKeyPair(boolean param1Boolean) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
      byte[] arrayOfByte1 = "Digest session key to client-to-server signing key magic constant".getBytes(DigestMD5Base.this.encoding);
      byte[] arrayOfByte2 = "Digest session key to server-to-client signing key magic constant".getBytes(DigestMD5Base.this.encoding);
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] arrayOfByte3 = new byte[DigestMD5Base.this.H_A1.length + arrayOfByte1.length];
      System.arraycopy(DigestMD5Base.this.H_A1, 0, arrayOfByte3, 0, DigestMD5Base.this.H_A1.length);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, DigestMD5Base.this.H_A1.length, arrayOfByte1.length);
      messageDigest.update(arrayOfByte3);
      byte[] arrayOfByte4 = messageDigest.digest();
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, DigestMD5Base.this.H_A1.length, arrayOfByte2.length);
      messageDigest.update(arrayOfByte3);
      byte[] arrayOfByte5 = messageDigest.digest();
      if (logger.isLoggable(Level.FINER)) {
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST12:Kic: ", arrayOfByte4);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "generateIntegrityKeyPair", "DIGEST13:Kis: ", arrayOfByte5);
      } 
      if (param1Boolean) {
        this.myKi = arrayOfByte4;
        this.peerKi = arrayOfByte5;
      } else {
        this.myKi = arrayOfByte5;
        this.peerKi = arrayOfByte4;
      } 
    }
    
    public byte[] wrap(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws SaslException {
      if (param1Int2 == 0)
        return DigestMD5Base.EMPTY_BYTE_ARRAY; 
      byte[] arrayOfByte1 = new byte[param1Int2 + 10 + 2 + 4];
      System.arraycopy(param1ArrayOfByte, param1Int1, arrayOfByte1, 0, param1Int2);
      incrementSeqNum();
      byte[] arrayOfByte2 = getHMAC(this.myKi, this.sequenceNum, param1ArrayOfByte, param1Int1, param1Int2);
      if (logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "wrap", "DIGEST14:outgoing: ", param1ArrayOfByte, param1Int1, param1Int2);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "wrap", "DIGEST15:seqNum: ", this.sequenceNum);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "wrap", "DIGEST16:MAC: ", arrayOfByte2);
      } 
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, param1Int2, 10);
      System.arraycopy(this.messageType, 0, arrayOfByte1, param1Int2 + 10, 2);
      System.arraycopy(this.sequenceNum, 0, arrayOfByte1, param1Int2 + 12, 4);
      if (logger.isLoggable(Level.FINEST))
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "wrap", "DIGEST17:wrapped: ", arrayOfByte1); 
      return arrayOfByte1;
    }
    
    public byte[] unwrap(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws SaslException {
      if (param1Int2 == 0)
        return DigestMD5Base.EMPTY_BYTE_ARRAY; 
      byte[] arrayOfByte1 = new byte[10];
      byte[] arrayOfByte2 = new byte[param1Int2 - 16];
      byte[] arrayOfByte3 = new byte[2];
      byte[] arrayOfByte4 = new byte[4];
      System.arraycopy(param1ArrayOfByte, param1Int1, arrayOfByte2, 0, arrayOfByte2.length);
      System.arraycopy(param1ArrayOfByte, param1Int1 + arrayOfByte2.length, arrayOfByte1, 0, 10);
      System.arraycopy(param1ArrayOfByte, param1Int1 + arrayOfByte2.length + 10, arrayOfByte3, 0, 2);
      System.arraycopy(param1ArrayOfByte, param1Int1 + arrayOfByte2.length + 12, arrayOfByte4, 0, 4);
      byte[] arrayOfByte5 = getHMAC(this.peerKi, arrayOfByte4, arrayOfByte2, 0, arrayOfByte2.length);
      if (logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "unwrap", "DIGEST18:incoming: ", arrayOfByte2);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "unwrap", "DIGEST19:MAC: ", arrayOfByte1);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "unwrap", "DIGEST20:messageType: ", arrayOfByte3);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "unwrap", "DIGEST21:sequenceNum: ", arrayOfByte4);
        DigestMD5Base.traceOutput(DI_CLASS_NAME, "unwrap", "DIGEST22:expectedMAC: ", arrayOfByte5);
      } 
      if (!Arrays.equals(arrayOfByte1, arrayOfByte5)) {
        logger.log(Level.INFO, "DIGEST23:Unmatched MACs");
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      } 
      if (this.peerSeqNum != DigestMD5Base.networkByteOrderToInt(arrayOfByte4, 0, 4))
        throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte4, 0, 4) + " Expected: " + this.peerSeqNum); 
      if (!Arrays.equals(this.messageType, arrayOfByte3))
        throw new SaslException("DIGEST-MD5: invalid message type: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte3, 0, 2)); 
      this.peerSeqNum++;
      return arrayOfByte2;
    }
    
    protected byte[] getHMAC(byte[] param1ArrayOfByte1, byte[] param1ArrayOfByte2, byte[] param1ArrayOfByte3, int param1Int1, int param1Int2) throws SaslException {
      byte[] arrayOfByte = new byte[4 + param1Int2];
      System.arraycopy(param1ArrayOfByte2, 0, arrayOfByte, 0, 4);
      System.arraycopy(param1ArrayOfByte3, param1Int1, arrayOfByte, 4, param1Int2);
      try {
        SecretKeySpec secretKeySpec = new SecretKeySpec(param1ArrayOfByte1, "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(secretKeySpec);
        mac.update(arrayOfByte);
        byte[] arrayOfByte1 = mac.doFinal();
        byte[] arrayOfByte2 = new byte[10];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 10);
        return arrayOfByte2;
      } catch (InvalidKeyException invalidKeyException) {
        throw new SaslException("DIGEST-MD5: Invalid bytes used for key of HMAC-MD5 hash.", invalidKeyException);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new SaslException("DIGEST-MD5: Error creating instance of MD5 digest algorithm", noSuchAlgorithmException);
      } 
    }
    
    protected void incrementSeqNum() throws SaslException { DigestMD5Base.intToNetworkByteOrder(this.mySeqNum++, this.sequenceNum, 0, 4); }
  }
  
  final class DigestPrivacy extends DigestIntegrity implements SecurityCtx {
    private static final String CLIENT_CONF_MAGIC = "Digest H(A1) to client-to-server sealing key magic constant";
    
    private static final String SVR_CONF_MAGIC = "Digest H(A1) to server-to-client sealing key magic constant";
    
    private Cipher encCipher;
    
    private Cipher decCipher;
    
    DigestPrivacy(boolean param1Boolean) throws SaslException {
      super(DigestMD5Base.this, param1Boolean);
      try {
        generatePrivacyKeyPair(param1Boolean);
      } catch (SaslException saslException) {
        throw saslException;
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new SaslException("DIGEST-MD5: Error encoding string value into UTF-8", unsupportedEncodingException);
      } catch (IOException iOException) {
        throw new SaslException("DIGEST-MD5: Error accessing buffers required to generate cipher keys", iOException);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new SaslException("DIGEST-MD5: Error creating instance of required cipher or digest", noSuchAlgorithmException);
      } 
    }
    
    private void generatePrivacyKeyPair(boolean param1Boolean) throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException {
      byte[] arrayOfByte7;
      byte[] arrayOfByte6;
      int i;
      byte[] arrayOfByte1 = "Digest H(A1) to client-to-server sealing key magic constant".getBytes(DigestMD5Base.this.encoding);
      byte[] arrayOfByte2 = "Digest H(A1) to server-to-client sealing key magic constant".getBytes(DigestMD5Base.this.encoding);
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[4])) {
        i = 5;
      } else if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[3])) {
        i = 7;
      } else {
        i = 16;
      } 
      byte[] arrayOfByte3 = new byte[i + arrayOfByte1.length];
      System.arraycopy(DigestMD5Base.this.H_A1, 0, arrayOfByte3, 0, i);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i, arrayOfByte1.length);
      messageDigest.update(arrayOfByte3);
      byte[] arrayOfByte4 = messageDigest.digest();
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, i, arrayOfByte2.length);
      messageDigest.update(arrayOfByte3);
      byte[] arrayOfByte5 = messageDigest.digest();
      if (logger.isLoggable(Level.FINER)) {
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST24:Kcc: ", arrayOfByte4);
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST25:Kcs: ", arrayOfByte5);
      } 
      if (param1Boolean) {
        arrayOfByte6 = arrayOfByte4;
        arrayOfByte7 = arrayOfByte5;
      } else {
        arrayOfByte6 = arrayOfByte5;
        arrayOfByte7 = arrayOfByte4;
      } 
      try {
        if (DigestMD5Base.this.negotiatedCipher.indexOf(DigestMD5Base.CIPHER_TOKENS[1]) > -1) {
          this.decCipher = (this.encCipher = Cipher.getInstance("RC4")).getInstance("RC4");
          SecretKeySpec secretKeySpec1 = new SecretKeySpec(arrayOfByte6, "RC4");
          SecretKeySpec secretKeySpec2 = new SecretKeySpec(arrayOfByte7, "RC4");
          this.encCipher.init(1, secretKeySpec1);
          this.decCipher.init(2, secretKeySpec2);
        } else if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2]) || DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[0])) {
          String str2;
          String str1;
          if (DigestMD5Base.this.negotiatedCipher.equals(DigestMD5Base.CIPHER_TOKENS[2])) {
            str1 = "DES/CBC/NoPadding";
            str2 = "des";
          } else {
            str1 = "DESede/CBC/NoPadding";
            str2 = "desede";
          } 
          this.decCipher = (this.encCipher = Cipher.getInstance(str1)).getInstance(str1);
          SecretKey secretKey1 = DigestMD5Base.makeDesKeys(arrayOfByte6, str2);
          SecretKey secretKey2 = DigestMD5Base.makeDesKeys(arrayOfByte7, str2);
          IvParameterSpec ivParameterSpec1 = new IvParameterSpec(arrayOfByte6, 8, 8);
          IvParameterSpec ivParameterSpec2 = new IvParameterSpec(arrayOfByte7, 8, 8);
          this.encCipher.init(1, secretKey1, ivParameterSpec1);
          this.decCipher.init(2, secretKey2, ivParameterSpec2);
          if (logger.isLoggable(Level.FINER)) {
            DigestMD5Base.traceOutput(DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST26:" + DigestMD5Base.this.negotiatedCipher + " IVcc: ", ivParameterSpec1.getIV());
            DigestMD5Base.traceOutput(DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST27:" + DigestMD5Base.this.negotiatedCipher + " IVcs: ", ivParameterSpec2.getIV());
            DigestMD5Base.traceOutput(DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST28:" + DigestMD5Base.this.negotiatedCipher + " encryption key: ", secretKey1.getEncoded());
            DigestMD5Base.traceOutput(DP_CLASS_NAME, "generatePrivacyKeyPair", "DIGEST29:" + DigestMD5Base.this.negotiatedCipher + " decryption key: ", secretKey2.getEncoded());
          } 
        } 
      } catch (InvalidKeySpecException invalidKeySpecException) {
        throw new SaslException("DIGEST-MD5: Unsupported key specification used.", invalidKeySpecException);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new SaslException("DIGEST-MD5: Invalid cipher algorithem parameter used to create cipher instance", invalidAlgorithmParameterException);
      } catch (NoSuchPaddingException noSuchPaddingException) {
        throw new SaslException("DIGEST-MD5: Unsupported padding used for chosen cipher", noSuchPaddingException);
      } catch (InvalidKeyException invalidKeyException) {
        throw new SaslException("DIGEST-MD5: Invalid data used to initialize keys", invalidKeyException);
      } 
    }
    
    public byte[] wrap(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws SaslException {
      byte[] arrayOfByte4;
      byte[] arrayOfByte2;
      if (param1Int2 == 0)
        return DigestMD5Base.EMPTY_BYTE_ARRAY; 
      incrementSeqNum();
      byte[] arrayOfByte1 = getHMAC(this.myKi, this.sequenceNum, param1ArrayOfByte, param1Int1, param1Int2);
      if (logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "wrap", "DIGEST30:Outgoing: ", param1ArrayOfByte, param1Int1, param1Int2);
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "wrap", "seqNum: ", this.sequenceNum);
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "wrap", "MAC: ", arrayOfByte1);
      } 
      int i = this.encCipher.getBlockSize();
      if (i > 1) {
        int j = i - (param1Int2 + 10) % i;
        arrayOfByte2 = new byte[j];
        for (byte b = 0; b < j; b++)
          arrayOfByte2[b] = (byte)j; 
      } else {
        arrayOfByte2 = DigestMD5Base.EMPTY_BYTE_ARRAY;
      } 
      byte[] arrayOfByte3 = new byte[param1Int2 + arrayOfByte2.length + 10];
      System.arraycopy(param1ArrayOfByte, param1Int1, arrayOfByte3, 0, param1Int2);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte3, param1Int2, arrayOfByte2.length);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte3, param1Int2 + arrayOfByte2.length, 10);
      if (logger.isLoggable(Level.FINEST))
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "wrap", "DIGEST31:{msg, pad, KicMAC}: ", arrayOfByte3); 
      try {
        arrayOfByte4 = this.encCipher.update(arrayOfByte3);
        if (arrayOfByte4 == null)
          throw new IllegalBlockSizeException("" + arrayOfByte3.length); 
      } catch (IllegalBlockSizeException illegalBlockSizeException) {
        throw new SaslException("DIGEST-MD5: Invalid block size for cipher", illegalBlockSizeException);
      } 
      byte[] arrayOfByte5 = new byte[arrayOfByte4.length + 2 + 4];
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte4.length);
      System.arraycopy(this.messageType, 0, arrayOfByte5, arrayOfByte4.length, 2);
      System.arraycopy(this.sequenceNum, 0, arrayOfByte5, arrayOfByte4.length + 2, 4);
      if (logger.isLoggable(Level.FINEST))
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "wrap", "DIGEST32:Wrapped: ", arrayOfByte5); 
      return arrayOfByte5;
    }
    
    public byte[] unwrap(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws SaslException {
      byte[] arrayOfByte4;
      if (param1Int2 == 0)
        return DigestMD5Base.EMPTY_BYTE_ARRAY; 
      byte[] arrayOfByte1 = new byte[param1Int2 - 6];
      byte[] arrayOfByte2 = new byte[2];
      byte[] arrayOfByte3 = new byte[4];
      System.arraycopy(param1ArrayOfByte, param1Int1, arrayOfByte1, 0, arrayOfByte1.length);
      System.arraycopy(param1ArrayOfByte, param1Int1 + arrayOfByte1.length, arrayOfByte2, 0, 2);
      System.arraycopy(param1ArrayOfByte, param1Int1 + arrayOfByte1.length + 2, arrayOfByte3, 0, 4);
      if (logger.isLoggable(Level.FINEST)) {
        logger.log(Level.FINEST, "DIGEST33:Expecting sequence num: {0}", new Integer(this.peerSeqNum));
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "unwrap", "DIGEST34:incoming: ", arrayOfByte1);
      } 
      try {
        arrayOfByte4 = this.decCipher.update(arrayOfByte1);
        if (arrayOfByte4 == null)
          throw new IllegalBlockSizeException("" + arrayOfByte1.length); 
      } catch (IllegalBlockSizeException illegalBlockSizeException) {
        throw new SaslException("DIGEST-MD5: Illegal block sizes used with chosen cipher", illegalBlockSizeException);
      } 
      byte[] arrayOfByte5 = new byte[arrayOfByte4.length - 10];
      byte[] arrayOfByte6 = new byte[10];
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 0, arrayOfByte5.length);
      System.arraycopy(arrayOfByte4, arrayOfByte5.length, arrayOfByte6, 0, 10);
      if (logger.isLoggable(Level.FINEST)) {
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "unwrap", "DIGEST35:Unwrapped (w/padding): ", arrayOfByte5);
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "unwrap", "DIGEST36:MAC: ", arrayOfByte6);
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "unwrap", "DIGEST37:messageType: ", arrayOfByte2);
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "unwrap", "DIGEST38:sequenceNum: ", arrayOfByte3);
      } 
      int i = arrayOfByte5.length;
      int j = this.decCipher.getBlockSize();
      if (j > 1) {
        i -= arrayOfByte5[arrayOfByte5.length - 1];
        if (i < 0) {
          if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "DIGEST39:Incorrect padding: {0}", new Byte(arrayOfByte5[arrayOfByte5.length - 1])); 
          return DigestMD5Base.EMPTY_BYTE_ARRAY;
        } 
      } 
      byte[] arrayOfByte7 = getHMAC(this.peerKi, arrayOfByte3, arrayOfByte5, 0, i);
      if (logger.isLoggable(Level.FINEST))
        DigestMD5Base.traceOutput(DP_CLASS_NAME, "unwrap", "DIGEST40:KisMAC: ", arrayOfByte7); 
      if (!Arrays.equals(arrayOfByte6, arrayOfByte7)) {
        logger.log(Level.INFO, "DIGEST41:Unmatched MACs");
        return DigestMD5Base.EMPTY_BYTE_ARRAY;
      } 
      if (this.peerSeqNum != DigestMD5Base.networkByteOrderToInt(arrayOfByte3, 0, 4))
        throw new SaslException("DIGEST-MD5: Out of order sequencing of messages from server. Got: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte3, 0, 4) + " Expected: " + this.peerSeqNum); 
      if (!Arrays.equals(this.messageType, arrayOfByte2))
        throw new SaslException("DIGEST-MD5: invalid message type: " + DigestMD5Base.networkByteOrderToInt(arrayOfByte2, 0, 2)); 
      this.peerSeqNum++;
      if (i == arrayOfByte5.length)
        return arrayOfByte5; 
      byte[] arrayOfByte8 = new byte[i];
      System.arraycopy(arrayOfByte5, 0, arrayOfByte8, 0, i);
      return arrayOfByte8;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sasl\digest\DigestMD5Base.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */