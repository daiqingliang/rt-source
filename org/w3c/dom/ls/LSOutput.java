package org.w3c.dom.ls;

import java.io.OutputStream;
import java.io.Writer;

public interface LSOutput {
  Writer getCharacterStream();
  
  void setCharacterStream(Writer paramWriter);
  
  OutputStream getByteStream();
  
  void setByteStream(OutputStream paramOutputStream);
  
  String getSystemId();
  
  void setSystemId(String paramString);
  
  String getEncoding();
  
  void setEncoding(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ls\LSOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */