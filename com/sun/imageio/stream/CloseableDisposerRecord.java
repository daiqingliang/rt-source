package com.sun.imageio.stream;

import java.io.Closeable;
import java.io.IOException;
import sun.java2d.DisposerRecord;

public class CloseableDisposerRecord implements DisposerRecord {
  private Closeable closeable;
  
  public CloseableDisposerRecord(Closeable paramCloseable) { this.closeable = paramCloseable; }
  
  public void dispose() {
    if (this.closeable != null)
      try {
        this.closeable.close();
      } catch (IOException iOException) {
      
      } finally {
        this.closeable = null;
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\stream\CloseableDisposerRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */