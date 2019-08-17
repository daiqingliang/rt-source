package sun.awt.windows;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.peer.RobotPeer;

final class WRobotPeer extends WObjectPeer implements RobotPeer {
  WRobotPeer() { create(); }
  
  WRobotPeer(GraphicsDevice paramGraphicsDevice) { create(); }
  
  private native void _dispose();
  
  protected void disposeImpl() { _dispose(); }
  
  public native void create();
  
  public native void mouseMoveImpl(int paramInt1, int paramInt2);
  
  public void mouseMove(int paramInt1, int paramInt2) { mouseMoveImpl(paramInt1, paramInt2); }
  
  public native void mousePress(int paramInt);
  
  public native void mouseRelease(int paramInt);
  
  public native void mouseWheel(int paramInt);
  
  public native void keyPress(int paramInt);
  
  public native void keyRelease(int paramInt);
  
  public int getRGBPixel(int paramInt1, int paramInt2) { return getRGBPixels(new Rectangle(paramInt1, paramInt2, 1, 1))[0]; }
  
  public int[] getRGBPixels(Rectangle paramRectangle) {
    int[] arrayOfInt = new int[paramRectangle.width * paramRectangle.height];
    getRGBPixels(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, arrayOfInt);
    return arrayOfInt;
  }
  
  private native void getRGBPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WRobotPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */