package sun.security.provider;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;

public final class DSAPrivateKey extends PKCS8Key implements DSAPrivateKey, Serializable {
  private static final long serialVersionUID = -3244453684193605938L;
  
  private BigInteger x;
  
  public DSAPrivateKey() {}
  
  public DSAPrivateKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) throws InvalidKeyException {
    this.x = paramBigInteger1;
    this.algid = new AlgIdDSA(paramBigInteger2, paramBigInteger3, paramBigInteger4);
    try {
      this.key = (new DerValue((byte)2, paramBigInteger1.toByteArray())).toByteArray();
      encode();
    } catch (IOException iOException) {
      InvalidKeyException invalidKeyException = new InvalidKeyException("could not DER encode x: " + iOException.getMessage());
      invalidKeyException.initCause(iOException);
      throw invalidKeyException;
    } 
  }
  
  public DSAPrivateKey(byte[] paramArrayOfByte) throws InvalidKeyException {
    clearOldKey();
    decode(paramArrayOfByte);
  }
  
  public DSAParams getParams() {
    try {
      if (this.algid instanceof DSAParams)
        return (DSAParams)this.algid; 
      AlgorithmParameters algorithmParameters = this.algid.getParameters();
      return (algorithmParameters == null) ? null : (DSAParameterSpec)algorithmParameters.getParameterSpec(DSAParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public BigInteger getX() { return this.x; }
  
  private void clearOldKey() {
    if (this.encodedKey != null)
      for (byte b = 0; b < this.encodedKey.length; b++)
        this.encodedKey[b] = 0;  
    if (this.key != null)
      for (byte b = 0; b < this.key.length; b++)
        this.key[b] = 0;  
  }
  
  protected void parseKeyBits() {
    try {
      DerInputStream derInputStream = new DerInputStream(this.key);
      this.x = derInputStream.getBigInteger();
    } catch (IOException iOException) {
      InvalidKeyException invalidKeyException = new InvalidKeyException(iOException.getMessage());
      invalidKeyException.initCause(iOException);
      throw invalidKeyException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSAPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */