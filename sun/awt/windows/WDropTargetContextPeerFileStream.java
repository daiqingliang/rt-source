package sun.awt.windows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final class WDropTargetContextPeerFileStream extends FileInputStream {
  private long stgmedium;
  
  WDropTargetContextPeerFileStream(String paramString, long paramLong) throws FileNotFoundException {
    super(paramString);
    this.stgmedium = paramLong;
  }
  
  public void close() throws IOException {
    if (this.stgmedium != 0L) {
      super.close();
      freeStgMedium(this.stgmedium);
      this.stgmedium = 0L;
    } 
  }
  
  private native void freeStgMedium(long paramLong);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WDropTargetContextPeerFileStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */