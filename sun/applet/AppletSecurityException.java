package sun.applet;

public class AppletSecurityException extends SecurityException {
  private String key = null;
  
  private Object[] msgobj = null;
  
  private static AppletMessageHandler amh = new AppletMessageHandler("appletsecurityexception");
  
  public AppletSecurityException(String paramString) {
    super(paramString);
    this.key = paramString;
  }
  
  public AppletSecurityException(String paramString1, String paramString2) {
    this(paramString1);
    this.msgobj = new Object[1];
    this.msgobj[0] = paramString2;
  }
  
  public AppletSecurityException(String paramString1, String paramString2, String paramString3) {
    this(paramString1);
    this.msgobj = new Object[2];
    this.msgobj[0] = paramString2;
    this.msgobj[1] = paramString3;
  }
  
  public String getLocalizedMessage() { return (this.msgobj != null) ? amh.getMessage(this.key, this.msgobj) : amh.getMessage(this.key); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletSecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */