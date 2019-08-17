package sun.nio.fs;

import java.io.IOException;
import java.util.Map;

interface DynamicFileAttributeView {
  void setAttribute(String paramString, Object paramObject) throws IOException;
  
  Map<String, Object> readAttributes(String[] paramArrayOfString) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\DynamicFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */