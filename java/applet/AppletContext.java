package java.applet;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public interface AppletContext {
  AudioClip getAudioClip(URL paramURL);
  
  Image getImage(URL paramURL);
  
  Applet getApplet(String paramString);
  
  Enumeration<Applet> getApplets();
  
  void showDocument(URL paramURL);
  
  void showDocument(URL paramURL, String paramString);
  
  void showStatus(String paramString);
  
  void setStream(String paramString, InputStream paramInputStream) throws IOException;
  
  InputStream getStream(String paramString);
  
  Iterator<String> getStreamKeys();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\applet\AppletContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */