package sun.security.provider;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.BitArray;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgIdDSA;
import sun.security.x509.X509Key;

public class DSAPublicKey extends X509Key implements DSAPublicKey, Serializable {
  private static final long serialVersionUID = -2994193307391104133L;
  
  private BigInteger y;
  
  public DSAPublicKey() {}
  
  public DSAPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) throws InvalidKeyException {
    this.y = paramBigInteger1;
    this.algid = new AlgIdDSA(paramBigInteger2, paramBigInteger3, paramBigInteger4);
    try {
      byte[] arrayOfByte = (new DerValue((byte)2, paramBigInteger1.toByteArray())).toByteArray();
      setKey(new BitArray(arrayOfByte.length * 8, arrayOfByte));
      encode();
    } catch (IOException iOException) {
      throw new InvalidKeyException("could not DER encode y: " + iOException.getMessage());
    } 
  }
  
  public DSAPublicKey(byte[] paramArrayOfByte) throws InvalidKeyException { decode(paramArrayOfByte); }
  
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
  
  public BigInteger getY() { return this.y; }
  
  public String toString() { return "Sun DSA Public Key\n    Parameters:" + this.algid + "\n  y:\n" + Debug.toHexString(this.y) + "\n"; }
  
  protected void parseKeyBits() {
    try {
      DerInputStream derInputStream = new DerInputStream(getKey().toByteArray());
      this.y = derInputStream.getBigInteger();
    } catch (IOException iOException) {
      throw new InvalidKeyException("Invalid key: y value\n" + iOException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSAPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */