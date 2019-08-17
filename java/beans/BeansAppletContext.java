package java.beans;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

class BeansAppletContext implements AppletContext {
  Applet target;
  
  Hashtable<URL, Object> imageCache = new Hashtable();
  
  BeansAppletContext(Applet paramApplet) { this.target = paramApplet; }
  
  public AudioClip getAudioClip(URL paramURL) {
    try {
      return (AudioClip)paramURL.getContent();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Image getImage(URL paramURL) {
    Object object = this.imageCache.get(paramURL);
    if (object != null)
      return (Image)object; 
    try {
      object = paramURL.getContent();
      if (object == null)
        return null; 
      if (object instanceof Image) {
        this.imageCache.put(paramURL, object);
        return (Image)object;
      } 
      Image image = this.target.createImage((ImageProducer)object);
      this.imageCache.put(paramURL, image);
      return image;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Applet getApplet(String paramString) { return null; }
  
  public Enumeration<Applet> getApplets() {
    Vector vector = new Vector();
    vector.addElement(this.target);
    return vector.elements();
  }
  
  public void showDocument(URL paramURL) {}
  
  public void showDocument(URL paramURL, String paramString) {}
  
  public void showStatus(String paramString) {}
  
  public void setStream(String paramString, InputStream paramInputStream) throws IOException {}
  
  public InputStream getStream(String paramString) { return null; }
  
  public Iterator<String> getStreamKeys() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\BeansAppletContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */