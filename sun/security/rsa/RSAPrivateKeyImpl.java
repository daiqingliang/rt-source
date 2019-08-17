package sun.security.rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public final class RSAPrivateKeyImpl extends PKCS8Key implements RSAPrivateKey {
  private static final long serialVersionUID = -33106691987952810L;
  
  private final BigInteger n;
  
  private final BigInteger d;
  
  RSAPrivateKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws InvalidKeyException {
    this.n = paramBigInteger1;
    this.d = paramBigInteger2;
    RSAKeyFactory.checkRSAProviderKeyLengths(paramBigInteger1.bitLength(), null);
    this.algid = RSAPrivateCrtKeyImpl.rsaId;
    try {
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(paramBigInteger1);
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(paramBigInteger2);
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(0);
      derOutputStream.putInteger(0);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      this.key = derValue.toByteArray();
    } catch (IOException iOException) {
      throw new InvalidKeyException(iOException);
    } 
  }
  
  public String getAlgorithm() { return "RSA"; }
  
  public BigInteger getModulus() { return this.n; }
  
  public BigInteger getPrivateExponent() { return this.d; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSAPrivateKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */