package sun.security.rsa;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyRep;
import java.security.interfaces.RSAPublicKey;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.X509Key;

public final class RSAPublicKeyImpl extends X509Key implements RSAPublicKey {
  private static final long serialVersionUID = 2644735423591199609L;
  
  private static final BigInteger THREE = BigInteger.valueOf(3L);
  
  private BigInteger n;
  
  private BigInteger e;
  
  public RSAPublicKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws InvalidKeyException {
    this.n = paramBigInteger1;
    this.e = paramBigInteger2;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), paramBigInteger2);
    checkExponentRange();
    this.algid = RSAPrivateCrtKeyImpl.rsaId;
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(paramBigInteger1);
      derOutputStream.putInteger(paramBigInteger2);
      byte[] arrayOfByte = (new DerValue((byte)48, derOutputStream.toByteArray())).toByteArray();
      setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
    } catch (IOException iOException) {
      throw new InvalidKeyException(iOException);
    } 
  }
  
  public RSAPublicKeyImpl(byte[] paramArrayOfByte) throws InvalidKeyException {
    decode(paramArrayOfByte);
    RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
    checkExponentRange();
  }
  
  private void checkExponentRange() throws InvalidKeyException {
    if (this.e.compareTo(this.n) >= 0)
      throw new InvalidKeyException("exponent is larger than modulus"); 
    if (this.e.compareTo(THREE) < 0)
      throw new InvalidKeyException("exponent is smaller than 3"); 
  }
  
  public String getAlgorithm() { return "RSA"; }
  
  public BigInteger getModulus() { return this.n; }
  
  public BigInteger getPublicExponent() { return this.e; }
  
  protected void parseKeyBits() throws InvalidKeyException {
    try {
      DerInputStream derInputStream1 = new DerInputStream(getKey().toByteArray());
      DerValue derValue = derInputStream1.getDerValue();
      if (derValue.tag != 48)
        throw new IOException("Not a SEQUENCE"); 
      DerInputStream derInputStream2 = derValue.data;
      this.n = derInputStream2.getPositiveBigInteger();
      this.e = derInputStream2.getPositiveBigInteger();
      if (derValue.data.available() != 0)
        throw new IOException("Extra data available"); 
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid RSA public key", iOException);
    } 
  }
  
  public String toString() { return "Sun RSA public key, " + this.n.bitLength() + " bits\n  modulus: " + this.n + "\n  public exponent: " + this.e; }
  
  protected Object writeReplace() throws ObjectStreamException { return new KeyRep(KeyRep.Type.PUBLIC, getAlgorithm(), getFormat(), getEncoded()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSAPublicKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */