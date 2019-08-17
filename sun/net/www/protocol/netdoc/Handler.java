package sun.net.www.protocol.netdoc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class Handler extends URLStreamHandler {
  static URL base;
  
  public URLConnection openConnection(URL paramURL) throws IOException {
    URLConnection uRLConnection = null;
    Boolean bool = (Boolean)AccessController.doPrivileged(new GetBooleanAction("newdoc.localonly"));
    boolean bool1 = bool.booleanValue();
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("doc.url"));
    String str2 = paramURL.getFile();
    if (!bool1) {
      Object object;
      try {
        if (base == null)
          base = new URL(str1); 
        object = new URL(base, str2);
      } catch (MalformedURLException malformedURLException) {
        object = null;
      } 
      if (object != null)
        uRLConnection = object.openConnection(); 
    } 
    if (uRLConnection == null)
      try {
        URL uRL = new URL("file", "~", str2);
        uRLConnection = uRL.openConnection();
        InputStream inputStream = uRLConnection.getInputStream();
      } catch (MalformedURLException malformedURLException) {
        uRLConnection = null;
      } catch (IOException iOException) {
        uRLConnection = null;
      }  
    if (uRLConnection == null)
      throw new IOException("Can't find file for URL: " + paramURL.toExternalForm()); 
    return uRLConnection;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\netdoc\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */