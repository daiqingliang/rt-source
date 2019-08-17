package java.security.cert;

import java.io.IOException;
import java.io.OutputStream;

public interface Extension {
  String getId();
  
  boolean isCritical();
  
  byte[] getValue();
  
  void encode(OutputStream paramOutputStream) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\Extension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */