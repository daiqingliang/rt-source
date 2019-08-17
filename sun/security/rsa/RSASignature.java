package sun.security.rsa;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.BadPaddingException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public abstract class RSASignature extends SignatureSpi {
  private static final int baseLength = 8;
  
  private final ObjectIdentifier digestOID;
  
  private final int encodedLength;
  
  private final MessageDigest md;
  
  private boolean digestReset;
  
  private RSAPrivateKey privateKey;
  
  private RSAPublicKey publicKey;
  
  private RSAPadding padding;
  
  RSASignature(String paramString, ObjectIdentifier paramObjectIdentifier, int paramInt) {
    this.digestOID = paramObjectIdentifier;
    try {
      this.md = MessageDigest.getInstance(paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new ProviderException(noSuchAlgorithmException);
    } 
    this.digestReset = true;
    this.encodedLength = 8 + paramInt + this.md.getDigestLength();
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    RSAPublicKey rSAPublicKey = (RSAPublicKey)RSAKeyFactory.toRSAKey(paramPublicKey);
    this.privateKey = null;
    this.publicKey = rSAPublicKey;
    initCommon(rSAPublicKey, null);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException { engineInitSign(paramPrivateKey, null); }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    RSAPrivateKey rSAPrivateKey = (RSAPrivateKey)RSAKeyFactory.toRSAKey(paramPrivateKey);
    this.privateKey = rSAPrivateKey;
    this.publicKey = null;
    initCommon(rSAPrivateKey, paramSecureRandom);
  }
  
  private void initCommon(RSAKey paramRSAKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    resetDigest();
    int i = RSACore.getByteLength(paramRSAKey);
    try {
      this.padding = RSAPadding.getInstance(1, i, paramSecureRandom);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new InvalidKeyException(invalidAlgorithmParameterException.getMessage());
    } 
    int j = this.padding.getMaxDataSize();
    if (this.encodedLength > j)
      throw new InvalidKeyException("Key is too short for this signature algorithm"); 
  }
  
  private void resetDigest() {
    if (!this.digestReset) {
      this.md.reset();
      this.digestReset = true;
    } 
  }
  
  private byte[] getDigestValue() {
    this.digestReset = true;
    return this.md.digest();
  }
  
  protected void engineUpdate(byte paramByte) throws SignatureException {
    this.md.update(paramByte);
    this.digestReset = false;
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SignatureException {
    this.md.update(paramArrayOfByte, paramInt1, paramInt2);
    this.digestReset = false;
  }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer) {
    this.md.update(paramByteBuffer);
    this.digestReset = false;
  }
  
  protected byte[] engineSign() {
    byte[] arrayOfByte = getDigestValue();
    try {
      byte[] arrayOfByte1 = encodeSignature(this.digestOID, arrayOfByte);
      byte[] arrayOfByte2 = this.padding.pad(arrayOfByte1);
      return RSACore.rsa(arrayOfByte2, this.privateKey, true);
    } catch (GeneralSecurityException generalSecurityException) {
      throw new SignatureException("Could not sign data", generalSecurityException);
    } catch (IOException iOException) {
      throw new SignatureException("Could not encode data", iOException);
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte) throws SignatureException {
    if (paramArrayOfByte.length != RSACore.getByteLength(this.publicKey))
      throw new SignatureException("Signature length not correct: got " + paramArrayOfByte.length + " but was expecting " + RSACore.getByteLength(this.publicKey)); 
    byte[] arrayOfByte = getDigestValue();
    try {
      byte[] arrayOfByte1 = RSACore.rsa(paramArrayOfByte, this.publicKey);
      byte[] arrayOfByte2 = this.padding.unpad(arrayOfByte1);
      byte[] arrayOfByte3 = decodeSignature(this.digestOID, arrayOfByte2);
      return MessageDigest.isEqual(arrayOfByte, arrayOfByte3);
    } catch (BadPaddingException badPaddingException) {
      return false;
    } catch (IOException iOException) {
      throw new SignatureException("Signature encoding error", iOException);
    } 
  }
  
  public static byte[] encodeSignature(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    (new AlgorithmId(paramObjectIdentifier)).encode(derOutputStream);
    derOutputStream.putOctetString(paramArrayOfByte);
    DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
    return derValue.toByteArray();
  }
  
  public static byte[] decodeSignature(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte) throws IOException {
    DerInputStream derInputStream = new DerInputStream(paramArrayOfByte, 0, paramArrayOfByte.length, false);
    DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
    if (arrayOfDerValue.length != 2 || derInputStream.available() != 0)
      throw new IOException("SEQUENCE length error"); 
    AlgorithmId algorithmId = AlgorithmId.parse(arrayOfDerValue[0]);
    if (!algorithmId.getOID().equals(paramObjectIdentifier))
      throw new IOException("ObjectIdentifier mismatch: " + algorithmId.getOID()); 
    if (algorithmId.getEncodedParams() != null)
      throw new IOException("Unexpected AlgorithmId parameters"); 
    return arrayOfDerValue[1].getOctetString();
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject) throws InvalidParameterException { throw new UnsupportedOperationException("setParameter() not supported"); }
  
  @Deprecated
  protected Object engineGetParameter(String paramString) throws InvalidParameterException { throw new UnsupportedOperationException("getParameter() not supported"); }
  
  public static final class MD2withRSA extends RSASignature {
    public MD2withRSA() { super("MD2", AlgorithmId.MD2_oid, 10); }
  }
  
  public static final class MD5withRSA extends RSASignature {
    public MD5withRSA() { super("MD5", AlgorithmId.MD5_oid, 10); }
  }
  
  public static final class SHA1withRSA extends RSASignature {
    public SHA1withRSA() { super("SHA-1", AlgorithmId.SHA_oid, 7); }
  }
  
  public static final class SHA224withRSA extends RSASignature {
    public SHA224withRSA() { super("SHA-224", AlgorithmId.SHA224_oid, 11); }
  }
  
  public static final class SHA256withRSA extends RSASignature {
    public SHA256withRSA() { super("SHA-256", AlgorithmId.SHA256_oid, 11); }
  }
  
  public static final class SHA384withRSA extends RSASignature {
    public SHA384withRSA() { super("SHA-384", AlgorithmId.SHA384_oid, 11); }
  }
  
  public static final class SHA512withRSA extends RSASignature {
    public SHA512withRSA() { super("SHA-512", AlgorithmId.SHA512_oid, 11); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSASignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */