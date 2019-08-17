package sun.security.provider.certpath;

import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Date;
import java.util.Set;
import sun.security.x509.GeneralNameInterface;

public abstract class CertPathHelper {
  protected static CertPathHelper instance;
  
  protected abstract void implSetPathToNames(X509CertSelector paramX509CertSelector, Set<GeneralNameInterface> paramSet);
  
  protected abstract void implSetDateAndTime(X509CRLSelector paramX509CRLSelector, Date paramDate, long paramLong);
  
  static void setPathToNames(X509CertSelector paramX509CertSelector, Set<GeneralNameInterface> paramSet) { instance.implSetPathToNames(paramX509CertSelector, paramSet); }
  
  public static void setDateAndTime(X509CRLSelector paramX509CRLSelector, Date paramDate, long paramLong) { instance.implSetDateAndTime(paramX509CRLSelector, paramDate, paramLong); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\CertPathHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */