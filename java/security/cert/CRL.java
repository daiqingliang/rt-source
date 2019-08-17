package java.security.cert;

public abstract class CRL {
  private String type;
  
  protected CRL(String paramString) { this.type = paramString; }
  
  public final String getType() { return this.type; }
  
  public abstract String toString();
  
  public abstract boolean isRevoked(Certificate paramCertificate);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CRL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */