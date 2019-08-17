package java.security.cert;

public interface CRLSelector extends Cloneable {
  boolean match(CRL paramCRL);
  
  Object clone();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CRLSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */