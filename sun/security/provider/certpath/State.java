package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

interface State extends Cloneable {
  void updateState(X509Certificate paramX509Certificate) throws CertificateException, IOException, CertPathValidatorException;
  
  Object clone();
  
  boolean isInitial();
  
  boolean keyParamsNeeded();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\State.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */