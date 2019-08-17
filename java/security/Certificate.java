package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Deprecated
public interface Certificate {
  Principal getGuarantor();
  
  Principal getPrincipal();
  
  PublicKey getPublicKey();
  
  void encode(OutputStream paramOutputStream) throws KeyException, IOException;
  
  void decode(InputStream paramInputStream) throws KeyException, IOException;
  
  String getFormat();
  
  String toString(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Certificate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */