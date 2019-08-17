package javax.accessibility;

import java.awt.datatransfer.DataFlavor;
import java.io.InputStream;

public interface AccessibleStreamable {
  DataFlavor[] getMimeTypes();
  
  InputStream getStream(DataFlavor paramDataFlavor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleStreamable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */