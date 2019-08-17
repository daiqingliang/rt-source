package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParametersSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class DSAParameters extends AlgorithmParametersSpi {
  protected BigInteger p;
  
  protected BigInteger q;
  
  protected BigInteger g;
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec))
      throw new InvalidParameterSpecException("Inappropriate parameter specification"); 
    this.p = ((DSAParameterSpec)paramAlgorithmParameterSpec).getP();
    this.q = ((DSAParameterSpec)paramAlgorithmParameterSpec).getQ();
    this.g = ((DSAParameterSpec)paramAlgorithmParameterSpec).getG();
  }
  
  protected void engineInit(byte[] paramArrayOfByte) throws IOException {
    DerValue derValue = new DerValue(paramArrayOfByte);
    if (derValue.tag != 48)
      throw new IOException("DSA params parsing error"); 
    derValue.data.reset();
    this.p = derValue.data.getBigInteger();
    this.q = derValue.data.getBigInteger();
    this.g = derValue.data.getBigInteger();
    if (derValue.data.available() != 0)
      throw new IOException("encoded params have " + derValue.data.available() + " extra bytes"); 
  }
  
  protected void engineInit(byte[] paramArrayOfByte, String paramString) throws IOException { engineInit(paramArrayOfByte); }
  
  protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> paramClass) throws InvalidParameterSpecException {
    try {
      Class clazz = Class.forName("java.security.spec.DSAParameterSpec");
      if (clazz.isAssignableFrom(paramClass))
        return (T)(AlgorithmParameterSpec)paramClass.cast(new DSAParameterSpec(this.p, this.q, this.g)); 
      throw new InvalidParameterSpecException("Inappropriate parameter Specification");
    } catch (ClassNotFoundException classNotFoundException) {
      throw new InvalidParameterSpecException("Unsupported parameter specification: " + classNotFoundException.getMessage());
    } 
  }
  
  protected byte[] engineGetEncoded() throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.putInteger(this.p);
    derOutputStream2.putInteger(this.q);
    derOutputStream2.putInteger(this.g);
    derOutputStream1.write((byte)48, derOutputStream2);
    return derOutputStream1.toByteArray();
  }
  
  protected byte[] engineGetEncoded(String paramString) throws IOException { return engineGetEncoded(); }
  
  protected String engineToString() { return "\n\tp: " + Debug.toHexString(this.p) + "\n\tq: " + Debug.toHexString(this.q) + "\n\tg: " + Debug.toHexString(this.g) + "\n"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSAParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */