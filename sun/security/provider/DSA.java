package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.Arrays;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

abstract class DSA extends SignatureSpi {
  private static final boolean debug = false;
  
  private static final int BLINDING_BITS = 7;
  
  private static final BigInteger BLINDING_CONSTANT = BigInteger.valueOf(128L);
  
  private DSAParams params;
  
  private BigInteger presetP;
  
  private BigInteger presetQ;
  
  private BigInteger presetG;
  
  private BigInteger presetY;
  
  private BigInteger presetX;
  
  private SecureRandom signingRandom;
  
  private final MessageDigest md;
  
  DSA(MessageDigest paramMessageDigest) { this.md = paramMessageDigest; }
  
  private static void checkKey(DSAParams paramDSAParams, int paramInt, String paramString) throws InvalidKeyException {
    int i = paramDSAParams.getQ().bitLength();
    if (i > paramInt)
      throw new InvalidKeyException("The security strength of " + paramString + " digest algorithm is not sufficient for this key size"); 
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (!(paramPrivateKey instanceof DSAPrivateKey))
      throw new InvalidKeyException("not a DSA private key: " + paramPrivateKey); 
    DSAPrivateKey dSAPrivateKey = (DSAPrivateKey)paramPrivateKey;
    DSAParams dSAParams = dSAPrivateKey.getParams();
    if (dSAParams == null)
      throw new InvalidKeyException("DSA private key lacks parameters"); 
    if (this.md.getAlgorithm() != "NullDigest20")
      checkKey(dSAParams, this.md.getDigestLength() * 8, this.md.getAlgorithm()); 
    this.params = dSAParams;
    this.presetX = dSAPrivateKey.getX();
    this.presetY = null;
    this.presetP = dSAParams.getP();
    this.presetQ = dSAParams.getQ();
    this.presetG = dSAParams.getG();
    this.md.reset();
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (!(paramPublicKey instanceof DSAPublicKey))
      throw new InvalidKeyException("not a DSA public key: " + paramPublicKey); 
    DSAPublicKey dSAPublicKey = (DSAPublicKey)paramPublicKey;
    DSAParams dSAParams = dSAPublicKey.getParams();
    if (dSAParams == null)
      throw new InvalidKeyException("DSA public key lacks parameters"); 
    this.params = dSAParams;
    this.presetY = dSAPublicKey.getY();
    this.presetX = null;
    this.presetP = dSAParams.getP();
    this.presetQ = dSAParams.getQ();
    this.presetG = dSAParams.getG();
    this.md.reset();
  }
  
  protected void engineUpdate(byte paramByte) { this.md.update(paramByte); }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.md.update(paramArrayOfByte, paramInt1, paramInt2); }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer) { this.md.update(paramByteBuffer); }
  
  protected byte[] engineSign() throws SignatureException {
    BigInteger bigInteger1 = generateK(this.presetQ);
    BigInteger bigInteger2 = generateR(this.presetP, this.presetQ, this.presetG, bigInteger1);
    BigInteger bigInteger3 = generateS(this.presetX, this.presetQ, bigInteger2, bigInteger1);
    try {
      DerOutputStream derOutputStream = new DerOutputStream(100);
      derOutputStream.putInteger(bigInteger2);
      derOutputStream.putInteger(bigInteger3);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      return derValue.toByteArray();
    } catch (IOException iOException) {
      throw new SignatureException("error encoding signature");
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte) throws SignatureException { return engineVerify(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SignatureException {
    BigInteger bigInteger1 = null;
    BigInteger bigInteger2 = null;
    try {
      DerInputStream derInputStream = new DerInputStream(paramArrayOfByte, paramInt1, paramInt2, false);
      DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
      if (arrayOfDerValue.length != 2 || derInputStream.available() != 0)
        throw new IOException("Invalid encoding for signature"); 
      bigInteger1 = arrayOfDerValue[0].getBigInteger();
      bigInteger2 = arrayOfDerValue[1].getBigInteger();
    } catch (IOException iOException) {
      throw new SignatureException("Invalid encoding for signature", iOException);
    } 
    if (bigInteger1.signum() < 0)
      bigInteger1 = new BigInteger(1, bigInteger1.toByteArray()); 
    if (bigInteger2.signum() < 0)
      bigInteger2 = new BigInteger(1, bigInteger2.toByteArray()); 
    if (bigInteger1.compareTo(this.presetQ) == -1 && bigInteger2.compareTo(this.presetQ) == -1) {
      BigInteger bigInteger3 = generateW(this.presetP, this.presetQ, this.presetG, bigInteger2);
      BigInteger bigInteger4 = generateV(this.presetY, this.presetP, this.presetQ, this.presetG, bigInteger3, bigInteger1);
      return bigInteger4.equals(bigInteger1);
    } 
    throw new SignatureException("invalid signature: out of range values");
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject) { throw new InvalidParameterException("No parameter accepted"); }
  
  @Deprecated
  protected Object engineGetParameter(String paramString) { return null; }
  
  private BigInteger generateR(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    SecureRandom secureRandom = getSigningRandom();
    BigInteger bigInteger1 = new BigInteger(7, secureRandom);
    bigInteger1 = bigInteger1.add(BLINDING_CONSTANT);
    paramBigInteger4 = paramBigInteger4.add(paramBigInteger2.multiply(bigInteger1));
    BigInteger bigInteger2 = paramBigInteger3.modPow(paramBigInteger4, paramBigInteger1);
    return bigInteger2.mod(paramBigInteger2);
  }
  
  private BigInteger generateS(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    byte[] arrayOfByte;
    try {
      arrayOfByte = this.md.digest();
    } catch (RuntimeException runtimeException) {
      throw new SignatureException(runtimeException.getMessage());
    } 
    int i = paramBigInteger2.bitLength() / 8;
    if (i < arrayOfByte.length)
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i); 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger2 = paramBigInteger4.modInverse(paramBigInteger2);
    return paramBigInteger1.multiply(paramBigInteger3).add(bigInteger1).multiply(bigInteger2).mod(paramBigInteger2);
  }
  
  private BigInteger generateW(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) { return paramBigInteger4.modInverse(paramBigInteger2); }
  
  private BigInteger generateV(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6) throws SignatureException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = this.md.digest();
    } catch (RuntimeException runtimeException) {
      throw new SignatureException(runtimeException.getMessage());
    } 
    int i = paramBigInteger3.bitLength() / 8;
    if (i < arrayOfByte.length)
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i); 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger2 = bigInteger1.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger bigInteger3 = paramBigInteger6.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger bigInteger4 = paramBigInteger4.modPow(bigInteger2, paramBigInteger2);
    BigInteger bigInteger5 = paramBigInteger1.modPow(bigInteger3, paramBigInteger2);
    BigInteger bigInteger6 = bigInteger4.multiply(bigInteger5);
    BigInteger bigInteger7 = bigInteger6.mod(paramBigInteger2);
    return bigInteger7.mod(paramBigInteger3);
  }
  
  protected BigInteger generateK(BigInteger paramBigInteger) {
    SecureRandom secureRandom = getSigningRandom();
    byte[] arrayOfByte = new byte[(paramBigInteger.bitLength() + 7) / 8 + 8];
    secureRandom.nextBytes(arrayOfByte);
    return (new BigInteger(1, arrayOfByte)).mod(paramBigInteger.subtract(BigInteger.ONE)).add(BigInteger.ONE);
  }
  
  protected SecureRandom getSigningRandom() {
    if (this.signingRandom == null)
      if (this.appRandom != null) {
        this.signingRandom = this.appRandom;
      } else {
        this.signingRandom = JCAUtil.getSecureRandom();
      }  
    return this.signingRandom;
  }
  
  public String toString() {
    String str = "DSA Signature";
    if (this.presetP != null && this.presetQ != null && this.presetG != null) {
      str = str + "\n\tp: " + Debug.toHexString(this.presetP);
      str = str + "\n\tq: " + Debug.toHexString(this.presetQ);
      str = str + "\n\tg: " + Debug.toHexString(this.presetG);
    } else {
      str = str + "\n\t P, Q or G not initialized.";
    } 
    if (this.presetY != null)
      str = str + "\n\ty: " + Debug.toHexString(this.presetY); 
    if (this.presetY == null && this.presetX == null)
      str = str + "\n\tUNINIIALIZED"; 
    return str;
  }
  
  private static void debug(Exception paramException) {}
  
  private static void debug(String paramString) {}
  
  public static final class RawDSA extends DSA {
    public RawDSA() throws NoSuchAlgorithmException { super(new NullDigest20()); }
    
    public static final class NullDigest20 extends MessageDigest {
      private final byte[] digestBuffer = new byte[20];
      
      private int ofs = 0;
      
      protected NullDigest20() throws NoSuchAlgorithmException { super("NullDigest20"); }
      
      protected void engineUpdate(byte param2Byte) {
        if (this.ofs == this.digestBuffer.length) {
          this.ofs = Integer.MAX_VALUE;
        } else {
          this.digestBuffer[this.ofs++] = param2Byte;
        } 
      }
      
      protected void engineUpdate(byte[] param2ArrayOfByte, int param2Int1, int param2Int2) {
        if (this.ofs + param2Int2 > this.digestBuffer.length) {
          this.ofs = Integer.MAX_VALUE;
        } else {
          System.arraycopy(param2ArrayOfByte, param2Int1, this.digestBuffer, this.ofs, param2Int2);
          this.ofs += param2Int2;
        } 
      }
      
      protected final void engineUpdate(ByteBuffer param2ByteBuffer) {
        int i = param2ByteBuffer.remaining();
        if (this.ofs + i > this.digestBuffer.length) {
          this.ofs = Integer.MAX_VALUE;
        } else {
          param2ByteBuffer.get(this.digestBuffer, this.ofs, i);
          this.ofs += i;
        } 
      }
      
      protected byte[] engineDigest() throws SignatureException {
        if (this.ofs != this.digestBuffer.length)
          throw new RuntimeException("Data for RawDSA must be exactly 20 bytes long"); 
        reset();
        return this.digestBuffer;
      }
      
      protected int engineDigest(byte[] param2ArrayOfByte, int param2Int1, int param2Int2) throws DigestException {
        if (this.ofs != this.digestBuffer.length)
          throw new DigestException("Data for RawDSA must be exactly 20 bytes long"); 
        if (param2Int2 < this.digestBuffer.length)
          throw new DigestException("Output buffer too small; must be at least 20 bytes"); 
        System.arraycopy(this.digestBuffer, 0, param2ArrayOfByte, param2Int1, this.digestBuffer.length);
        reset();
        return this.digestBuffer.length;
      }
      
      protected void engineReset() throws NoSuchAlgorithmException { this.ofs = 0; }
      
      protected final int engineGetDigestLength() { return this.digestBuffer.length; }
    }
  }
  
  public static final class SHA1withDSA extends DSA {
    public SHA1withDSA() throws NoSuchAlgorithmException { super(MessageDigest.getInstance("SHA-1")); }
  }
  
  public static final class SHA224withDSA extends DSA {
    public SHA224withDSA() throws NoSuchAlgorithmException { super(MessageDigest.getInstance("SHA-224")); }
  }
  
  public static final class SHA256withDSA extends DSA {
    public SHA256withDSA() throws NoSuchAlgorithmException { super(MessageDigest.getInstance("SHA-256")); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */