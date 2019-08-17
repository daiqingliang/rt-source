package com.sun.awt;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.geom.Point2D;
import sun.awt.AWTAccessor;

public final class SecurityWarning {
  public static Dimension getSize(Window paramWindow) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    if (paramWindow.getWarningString() == null)
      throw new IllegalArgumentException("The window must have a non-null warning string."); 
    return AWTAccessor.getWindowAccessor().getSecurityWarningSize(paramWindow);
  }
  
  public static void setPosition(Window paramWindow, Point2D paramPoint2D, float paramFloat1, float paramFloat2) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    if (paramWindow.getWarningString() == null)
      throw new IllegalArgumentException("The window must have a non-null warning string."); 
    if (paramPoint2D == null)
      throw new NullPointerException("The point argument must not be null"); 
    if (paramFloat1 < 0.0F || paramFloat1 > 1.0F)
      throw new IllegalArgumentException("alignmentX must be in the range [0.0f ... 1.0f]."); 
    if (paramFloat2 < 0.0F || paramFloat2 > 1.0F)
      throw new IllegalArgumentException("alignmentY must be in the range [0.0f ... 1.0f]."); 
    AWTAccessor.getWindowAccessor().setSecurityWarningPosition(paramWindow, paramPoint2D, paramFloat1, paramFloat2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\awt\SecurityWarning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */