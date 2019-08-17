package javax.swing;

import java.awt.Image;
import java.awt.image.ImageObserver;

class DebugGraphicsObserver implements ImageObserver {
  int lastInfo;
  
  boolean allBitsPresent() { return ((this.lastInfo & 0x20) != 0); }
  
  boolean imageHasProblem() { return ((this.lastInfo & 0x40) != 0 || (this.lastInfo & 0x80) != 0); }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    this.lastInfo = paramInt1;
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DebugGraphicsObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */