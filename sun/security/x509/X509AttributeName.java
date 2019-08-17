package sun.security.x509;

public class X509AttributeName {
  private static final char SEPARATOR = '.';
  
  private String prefix = null;
  
  private String suffix = null;
  
  public X509AttributeName(String paramString) {
    int i = paramString.indexOf('.');
    if (i < 0) {
      this.prefix = paramString;
    } else {
      this.prefix = paramString.substring(0, i);
      this.suffix = paramString.substring(i + 1);
    } 
  }
  
  public String getPrefix() { return this.prefix; }
  
  public String getSuffix() { return this.suffix; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X509AttributeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */