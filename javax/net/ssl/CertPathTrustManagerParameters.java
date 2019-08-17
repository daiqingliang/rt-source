package javax.net.ssl;

import java.security.cert.CertPathParameters;

public class CertPathTrustManagerParameters implements ManagerFactoryParameters {
  private final CertPathParameters parameters;
  
  public CertPathTrustManagerParameters(CertPathParameters paramCertPathParameters) { this.parameters = (CertPathParameters)paramCertPathParameters.clone(); }
  
  public CertPathParameters getParameters() { return (CertPathParameters)this.parameters.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\CertPathTrustManagerParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */