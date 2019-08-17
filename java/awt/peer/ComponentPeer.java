package java.awt.peer;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import sun.awt.CausedFocusEvent;
import sun.java2d.pipe.Region;

public interface ComponentPeer {
  public static final int SET_LOCATION = 1;
  
  public static final int SET_SIZE = 2;
  
  public static final int SET_BOUNDS = 3;
  
  public static final int SET_CLIENT_SIZE = 4;
  
  public static final int RESET_OPERATION = 5;
  
  public static final int NO_EMBEDDED_CHECK = 16384;
  
  public static final int DEFAULT_OPERATION = 3;
  
  boolean isObscured();
  
  boolean canDetermineObscurity();
  
  void setVisible(boolean paramBoolean);
  
  void setEnabled(boolean paramBoolean);
  
  void paint(Graphics paramGraphics);
  
  void print(Graphics paramGraphics);
  
  void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  void handleEvent(AWTEvent paramAWTEvent);
  
  void coalescePaintEvent(PaintEvent paramPaintEvent);
  
  Point getLocationOnScreen();
  
  Dimension getPreferredSize();
  
  Dimension getMinimumSize();
  
  ColorModel getColorModel();
  
  Graphics getGraphics();
  
  FontMetrics getFontMetrics(Font paramFont);
  
  void dispose();
  
  void setForeground(Color paramColor);
  
  void setBackground(Color paramColor);
  
  void setFont(Font paramFont);
  
  void updateCursorImmediately();
  
  boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause);
  
  boolean isFocusable();
  
  Image createImage(ImageProducer paramImageProducer);
  
  Image createImage(int paramInt1, int paramInt2);
  
  VolatileImage createVolatileImage(int paramInt1, int paramInt2);
  
  boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  GraphicsConfiguration getGraphicsConfiguration();
  
  boolean handlesWheelScrolling();
  
  void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException;
  
  Image getBackBuffer();
  
  void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents);
  
  void destroyBuffers();
  
  void reparent(ContainerPeer paramContainerPeer);
  
  boolean isReparentSupported();
  
  void layout();
  
  void applyShape(Region paramRegion);
  
  void setZOrder(ComponentPeer paramComponentPeer);
  
  boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\ComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */