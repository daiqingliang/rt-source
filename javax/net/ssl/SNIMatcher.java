package javax.net.ssl;

public abstract class SNIMatcher {
  private final int type;
  
  protected SNIMatcher(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Server name type cannot be less than zero"); 
    if (paramInt > 255)
      throw new IllegalArgumentException("Server name type cannot be greater than 255"); 
    this.type = paramInt;
  }
  
  public final int getType() { return this.type; }
  
  public abstract boolean matches(SNIServerName paramSNIServerName);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SNIMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */