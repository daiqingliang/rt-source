package java.security.interfaces;

import java.math.BigInteger;

public interface RSAPrivateCrtKey extends RSAPrivateKey {
  public static final long serialVersionUID = -5682214253527700368L;
  
  BigInteger getPublicExponent();
  
  BigInteger getPrimeP();
  
  BigInteger getPrimeQ();
  
  BigInteger getPrimeExponentP();
  
  BigInteger getPrimeExponentQ();
  
  BigInteger getCrtCoefficient();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\RSAPrivateCrtKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */