package java.security.cert;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class CertificateFactorySpi {
  public abstract Certificate engineGenerateCertificate(InputStream paramInputStream) throws CertificateException;
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream) throws CertificateException { throw new UnsupportedOperationException(); }
  
  public CertPath engineGenerateCertPath(InputStream paramInputStream, String paramString) throws CertificateException { throw new UnsupportedOperationException(); }
  
  public CertPath engineGenerateCertPath(List<? extends Certificate> paramList) throws CertificateException { throw new UnsupportedOperationException(); }
  
  public Iterator<String> engineGetCertPathEncodings() { throw new UnsupportedOperationException(); }
  
  public abstract Collection<? extends Certificate> engineGenerateCertificates(InputStream paramInputStream) throws CertificateException;
  
  public abstract CRL engineGenerateCRL(InputStream paramInputStream) throws CRLException;
  
  public abstract Collection<? extends CRL> engineGenerateCRLs(InputStream paramInputStream) throws CRLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertificateFactorySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */