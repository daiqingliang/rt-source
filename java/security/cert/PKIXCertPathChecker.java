package java.security.cert;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public abstract class PKIXCertPathChecker implements CertPathChecker, Cloneable {
  public abstract void init(boolean paramBoolean) throws CertPathValidatorException;
  
  public abstract boolean isForwardCheckingSupported();
  
  public abstract Set<String> getSupportedExtensions();
  
  public abstract void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException;
  
  public void check(Certificate paramCertificate) throws CertPathValidatorException { check(paramCertificate, Collections.emptySet()); }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\PKIXCertPathChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */