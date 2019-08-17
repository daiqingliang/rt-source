package sun.applet;

import java.awt.Image;
import java.net.URL;
import sun.misc.Ref;

public class AppletResourceLoader {
  public static Image getImage(URL paramURL) { return AppletViewer.getCachedImage(paramURL); }
  
  public static Ref getImageRef(URL paramURL) { return AppletViewer.getCachedImageRef(paramURL); }
  
  public static void flushImages() { AppletViewer.flushImageCache(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */