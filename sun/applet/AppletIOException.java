package sun.applet;

import java.io.IOException;

public class AppletIOException extends IOException {
  private String key = null;
  
  private Object msgobj = null;
  
  private static AppletMessageHandler amh = new AppletMessageHandler("appletioexception");
  
  public AppletIOException(String paramString) {
    super(paramString);
    this.key = paramString;
  }
  
  public AppletIOException(String paramString, Object paramObject) {
    this(paramString);
    this.msgobj = paramObject;
  }
  
  public String getLocalizedMessage() { return (this.msgobj != null) ? amh.getMessage(this.key, this.msgobj) : amh.getMessage(this.key); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */