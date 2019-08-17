package sun.awt;

import java.net.URL;

public abstract class DesktopBrowse {
  public static void setInstance(DesktopBrowse paramDesktopBrowse) {
    if (mInstance != null)
      throw new IllegalStateException("DesktopBrowse instance has already been set."); 
    mInstance = paramDesktopBrowse;
  }
  
  public static DesktopBrowse getInstance() { return mInstance; }
  
  public abstract void browse(URL paramURL);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\DesktopBrowse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */