package java.security.cert;

public interface CertSelector extends Cloneable {
  boolean match(Certificate paramCertificate);
  
  Object clone();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */