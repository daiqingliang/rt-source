package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLDataSource implements DataSource {
  private URL url = null;
  
  private URLConnection url_conn = null;
  
  public URLDataSource(URL paramURL) { this.url = paramURL; }
  
  public String getContentType() {
    String str = null;
    try {
      if (this.url_conn == null)
        this.url_conn = this.url.openConnection(); 
    } catch (IOException iOException) {}
    if (this.url_conn != null)
      str = this.url_conn.getContentType(); 
    if (str == null)
      str = "application/octet-stream"; 
    return str;
  }
  
  public String getName() { return this.url.getFile(); }
  
  public InputStream getInputStream() throws IOException { return this.url.openStream(); }
  
  public OutputStream getOutputStream() throws IOException {
    this.url_conn = this.url.openConnection();
    if (this.url_conn != null) {
      this.url_conn.setDoOutput(true);
      return this.url_conn.getOutputStream();
    } 
    return null;
  }
  
  public URL getURL() { return this.url; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\URLDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */