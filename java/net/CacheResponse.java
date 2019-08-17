package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class CacheResponse {
  public abstract Map<String, List<String>> getHeaders() throws IOException;
  
  public abstract InputStream getBody() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\CacheResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */