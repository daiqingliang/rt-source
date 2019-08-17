package sun.net.www.content.image;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;
import sun.awt.image.URLImageSource;

public class gif extends ContentHandler {
  public Object getContent(URLConnection paramURLConnection) throws IOException { return new URLImageSource(paramURLConnection); }
  
  public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass) throws IOException {
    Class[] arrayOfClass = paramArrayOfClass;
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (arrayOfClass[b].isAssignableFrom(URLImageSource.class))
        return new URLImageSource(paramURLConnection); 
      if (arrayOfClass[b].isAssignableFrom(java.awt.Image.class)) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.createImage(new URLImageSource(paramURLConnection));
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\content\image\gif.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */