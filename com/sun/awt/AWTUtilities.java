package com.sun.awt;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

public final class AWTUtilities {
  public static boolean isTranslucencySupported(Translucency paramTranslucency) {
    switch (paramTranslucency) {
      case PERPIXEL_TRANSPARENT:
        return isWindowShapingSupported();
      case TRANSLUCENT:
        return isWindowOpacitySupported();
      case PERPIXEL_TRANSLUCENT:
        return isWindowTranslucencySupported();
    } 
    return false;
  }
  
  private static boolean isWindowOpacitySupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return !(toolkit instanceof SunToolkit) ? false : ((SunToolkit)toolkit).isWindowOpacitySupported();
  }
  
  public static void setWindowOpacity(Window paramWindow, float paramFloat) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    AWTAccessor.getWindowAccessor().setOpacity(paramWindow, paramFloat);
  }
  
  public static float getWindowOpacity(Window paramWindow) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    return AWTAccessor.getWindowAccessor().getOpacity(paramWindow);
  }
  
  public static boolean isWindowShapingSupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return !(toolkit instanceof SunToolkit) ? false : ((SunToolkit)toolkit).isWindowShapingSupported();
  }
  
  public static Shape getWindowShape(Window paramWindow) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    return AWTAccessor.getWindowAccessor().getShape(paramWindow);
  }
  
  public static void setWindowShape(Window paramWindow, Shape paramShape) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    AWTAccessor.getWindowAccessor().setShape(paramWindow, paramShape);
  }
  
  private static boolean isWindowTranslucencySupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!(toolkit instanceof SunToolkit))
      return false; 
    if (!((SunToolkit)toolkit).isWindowTranslucencySupported())
      return false; 
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (isTranslucencyCapable(graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration()))
      return true; 
    GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
    for (byte b = 0; b < arrayOfGraphicsDevice.length; b++) {
      GraphicsConfiguration[] arrayOfGraphicsConfiguration = arrayOfGraphicsDevice[b].getConfigurations();
      for (byte b1 = 0; b1 < arrayOfGraphicsConfiguration.length; b1++) {
        if (isTranslucencyCapable(arrayOfGraphicsConfiguration[b1]))
          return true; 
      } 
    } 
    return false;
  }
  
  public static void setWindowOpaque(Window paramWindow, boolean paramBoolean) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    if (!paramBoolean && !isTranslucencySupported(Translucency.PERPIXEL_TRANSLUCENT))
      throw new UnsupportedOperationException("The PERPIXEL_TRANSLUCENT translucency kind is not supported"); 
    AWTAccessor.getWindowAccessor().setOpaque(paramWindow, paramBoolean);
  }
  
  public static boolean isWindowOpaque(Window paramWindow) {
    if (paramWindow == null)
      throw new NullPointerException("The window argument should not be null."); 
    return paramWindow.isOpaque();
  }
  
  public static boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration) {
    if (paramGraphicsConfiguration == null)
      throw new NullPointerException("The gc argument should not be null"); 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return !(toolkit instanceof SunToolkit) ? false : ((SunToolkit)toolkit).isTranslucencyCapable(paramGraphicsConfiguration);
  }
  
  public static void setComponentMixingCutoutShape(Component paramComponent, Shape paramShape) {
    if (paramComponent == null)
      throw new NullPointerException("The component argument should not be null."); 
    AWTAccessor.getComponentAccessor().setMixingCutoutShape(paramComponent, paramShape);
  }
  
  public enum Translucency {
    PERPIXEL_TRANSPARENT, TRANSLUCENT, PERPIXEL_TRANSLUCENT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\awt\AWTUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */