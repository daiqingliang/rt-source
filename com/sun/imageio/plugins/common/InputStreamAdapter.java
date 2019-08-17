package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

public class InputStreamAdapter extends InputStream {
  ImageInputStream stream;
  
  public InputStreamAdapter(ImageInputStream paramImageInputStream) { this.stream = paramImageInputStream; }
  
  public int read() throws IOException { return this.stream.read(); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { return this.stream.read(paramArrayOfByte, paramInt1, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\InputStreamAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */