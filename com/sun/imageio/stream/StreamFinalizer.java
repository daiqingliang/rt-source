package com.sun.imageio.stream;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class StreamFinalizer {
  private ImageInputStream stream;
  
  public StreamFinalizer(ImageInputStream paramImageInputStream) { this.stream = paramImageInputStream; }
  
  protected void finalize() throws Throwable {
    try {
      this.stream.close();
    } catch (IOException iOException) {
    
    } finally {
      this.stream = null;
      super.finalize();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\stream\StreamFinalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */