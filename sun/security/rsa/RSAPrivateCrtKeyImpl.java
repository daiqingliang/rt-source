package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public final class RSAPrivateCrtKeyImpl extends PKCS8Key implements RSAPrivateCrtKey {
  private static final long serialVersionUID = -1326088454257084918L;
  
  private BigInteger n;
  
  private BigInteger e;
  
  private BigInteger d;
  
  private BigInteger p;
  
  private BigInteger q;
  
  private BigInteger pe;
  
  private BigInteger qe;
  
  private BigInteger coeff;
  
  static final AlgorithmId rsaId = new AlgorithmId(AlgorithmId.RSAEncryption_oid);
  
  public static RSAPrivateKey newKey(byte[] paramArrayOfByte) throws InvalidKeyException {
    RSAPrivateCrtKeyImpl rSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(paramArrayOfByte);
    return (rSAPrivateCrtKeyImpl.getPublicExponent().signum() == 0) ? new RSAPrivateKeyImpl(rSAPrivateCrtKeyImpl.getModulus(), rSAPrivateCrtKeyImpl.getPrivateExponent()) : rSAPrivateCrtKeyImpl;
  }
  
  RSAPrivateCrtKeyImpl(byte[] paramArrayOfByte) throws InvalidKeyException {
    decode(paramArrayOfByte);
    RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
  }
  
  RSAPrivateCrtKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8) throws InvalidKeyException {
    this.n = paramBigInteger1;
    this.e = paramBigInteger2;
    this.d = paramBigInteger3;
    this.p = paramBigInteger4;
    this.q = paramBigInteger5;
    this.pe = paramBigInteger6;
    this.qe = paramBigInteger7;
    this.coeff = paramBigInteger8;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
    this.algid = rsaId;
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(paramBigInteger1);
      derOutputStream.putInteger(paramBigInteger2);
      derOutputStream.putInteger(paramBigInteger3);
      derOutputStream.putInteger(paramBigInteger4);
      derOutputStream.putInteger(paramBigInteger5);
      derOutputStream.putInteger(paramBigInteger6);
      derOutputStream.putInteger(paramBigInteger7);
      derOutputStream.putInteger(paramBigInteger8);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      this.key = derValue.toByteArray();
    } catch (IOException iOException) {
      throw new InvalidKeyException(iOException);
    } 
  }
  
  public String getAlgorithm() { return "RSA"; }
  
  public BigInteger getModulus() { return this.n; }
  
  public BigInteger getPublicExponent() { return this.e; }
  
  public BigInteger getPrivateExponent() { return this.d; }
  
  public BigInteger getPrimeP() { return this.p; }
  
  public BigInteger getPrimeQ() { return this.q; }
  
  public BigInteger getPrimeExponentP() { return this.pe; }
  
  public BigInteger getPrimeExponentQ() { return this.qe; }
  
  public BigInteger getCrtCoefficient() { return this.coeff; }
  
  protected void parseKeyBits() throws InvalidKeyException {
    try {
      DerInputStream derInputStream1 = new DerInputStream(this.key);
      DerValue derValue = derInputStream1.getDerValue();
      if (derValue.tag != 48)
        throw new IOException("Not a SEQUENCE"); 
      DerInputStream derInputStream2 = derValue.data;
      int i = derInputStream2.getInteger();
      if (i != 0)
        throw new IOException("Version must be 0"); 
      this.n = derInputStream2.getPositiveBigInteger();
      this.e = derInputStream2.getPositiveBigInteger();
      this.d = derInputStream2.getPositiveBigInteger();
      this.p = derInputStream2.getPositiveBigInteger();
      this.q = derInputStream2.getPositiveBigInteger();
      this.pe = derInputStream2.getPositiveBigInteger();
      this.qe = derInputStream2.getPositiveBigInteger();
      this.coeff = derInputStream2.getPositiveBigInteger();
      if (derValue.data.available() != 0)
        throw new IOException("Extra data available"); 
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid RSA private key", iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSAPrivateCrtKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */