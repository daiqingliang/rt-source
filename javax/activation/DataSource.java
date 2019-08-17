package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataSource {
  InputStream getInputStream() throws IOException;
  
  OutputStream getOutputStream() throws IOException;
  
  String getContentType();
  
  String getName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\DataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */