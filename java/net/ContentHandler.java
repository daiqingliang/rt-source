package java.net;

import java.io.IOException;

public abstract class ContentHandler {
  public abstract Object getContent(URLConnection paramURLConnection) throws IOException;
  
  public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass) throws IOException {
    Object object = getContent(paramURLConnection);
    for (byte b = 0; b < paramArrayOfClass.length; b++) {
      if (paramArrayOfClass[b].isInstance(object))
        return object; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\ContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */