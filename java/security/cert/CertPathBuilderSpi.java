package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathBuilderSpi {
  public abstract CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException;
  
  public CertPathChecker engineGetRevocationChecker() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertPathBuilderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */