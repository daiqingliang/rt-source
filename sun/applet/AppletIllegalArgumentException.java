package sun.applet;

public class AppletIllegalArgumentException extends IllegalArgumentException {
  private String key = null;
  
  private static AppletMessageHandler amh = new AppletMessageHandler("appletillegalargumentexception");
  
  public AppletIllegalArgumentException(String paramString) {
    super(paramString);
    this.key = paramString;
  }
  
  public String getLocalizedMessage() { return amh.getMessage(this.key); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletIllegalArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */