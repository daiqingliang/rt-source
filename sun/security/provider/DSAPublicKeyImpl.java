package sun.security.provider;

import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyRep;

public final class DSAPublicKeyImpl extends DSAPublicKey {
  private static final long serialVersionUID = 7819830118247182730L;
  
  public DSAPublicKeyImpl(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) throws InvalidKeyException { super(paramBigInteger1, paramBigInteger2, paramBigInteger3, paramBigInteger4); }
  
  public DSAPublicKeyImpl(byte[] paramArrayOfByte) throws InvalidKeyException { super(paramArrayOfByte); }
  
  protected Object writeReplace() throws ObjectStreamException { return new KeyRep(KeyRep.Type.PUBLIC, getAlgorithm(), getFormat(), getEncoded()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSAPublicKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */