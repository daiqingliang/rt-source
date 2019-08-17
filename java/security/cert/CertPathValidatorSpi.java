package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathValidatorSpi {
  public abstract CertPathValidatorResult engineValidate(CertPath paramCertPath, CertPathParameters paramCertPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException;
  
  public CertPathChecker engineGetRevocationChecker() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertPathValidatorSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */