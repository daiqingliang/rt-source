package sun.net.www.content.text;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URLConnection;

public class plain extends ContentHandler {
  public Object getContent(URLConnection paramURLConnection) {
    try {
      InputStream inputStream = paramURLConnection.getInputStream();
      return new PlainTextInputStream(paramURLConnection.getInputStream());
    } catch (IOException iOException) {
      return "Error reading document:\n" + iOException.toString();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\content\text\plain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */