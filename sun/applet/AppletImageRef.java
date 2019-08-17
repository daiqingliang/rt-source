package sun.applet;

import java.awt.Toolkit;
import java.net.URL;
import sun.awt.image.URLImageSource;
import sun.misc.Ref;

class AppletImageRef extends Ref {
  URL url;
  
  AppletImageRef(URL paramURL) { this.url = paramURL; }
  
  public void flush() { super.flush(); }
  
  public Object reconstitute() { return Toolkit.getDefaultToolkit().createImage(new URLImageSource(this.url)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletImageRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */