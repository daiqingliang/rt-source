package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.CanvasPeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.PanelPeer;
import sun.java2d.pipe.Region;

public class NullComponentPeer implements LightweightPeer, CanvasPeer, PanelPeer {
  public boolean isObscured() { return false; }
  
  public boolean canDetermineObscurity() { return false; }
  
  public boolean isFocusable() { return false; }
  
  public void setVisible(boolean paramBoolean) {}
  
  public void show() {}
  
  public void hide() {}
  
  public void setEnabled(boolean paramBoolean) {}
  
  public void enable() {}
  
  public void disable() {}
  
  public void paint(Graphics paramGraphics) {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void print(Graphics paramGraphics) {}
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {}
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void coalescePaintEvent(PaintEvent paramPaintEvent) {}
  
  public boolean handleEvent(Event paramEvent) { return false; }
  
  public void handleEvent(AWTEvent paramAWTEvent) {}
  
  public Dimension getPreferredSize() { return new Dimension(1, 1); }
  
  public Dimension getMinimumSize() { return new Dimension(1, 1); }
  
  public ColorModel getColorModel() { return null; }
  
  public Graphics getGraphics() { return null; }
  
  public GraphicsConfiguration getGraphicsConfiguration() { return null; }
  
  public FontMetrics getFontMetrics(Font paramFont) { return null; }
  
  public void dispose() {}
  
  public void setForeground(Color paramColor) {}
  
  public void setBackground(Color paramColor) {}
  
  public void setFont(Font paramFont) {}
  
  public void updateCursorImmediately() {}
  
  public void setCursor(Cursor paramCursor) {}
  
  public boolean requestFocus(Component paramComponent, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause) { return false; }
  
  public Image createImage(ImageProducer paramImageProducer) { return null; }
  
  public Image createImage(int paramInt1, int paramInt2) { return null; }
  
  public boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return false; }
  
  public int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) { return 0; }
  
  public Dimension preferredSize() { return getPreferredSize(); }
  
  public Dimension minimumSize() { return getMinimumSize(); }
  
  public Point getLocationOnScreen() { return new Point(0, 0); }
  
  public Insets getInsets() { return insets(); }
  
  public void beginValidate() {}
  
  public void endValidate() {}
  
  public Insets insets() { return new Insets(0, 0, 0, 0); }
  
  public boolean isPaintPending() { return false; }
  
  public boolean handlesWheelScrolling() { return false; }
  
  public VolatileImage createVolatileImage(int paramInt1, int paramInt2) { return null; }
  
  public void beginLayout() {}
  
  public void endLayout() {}
  
  public void createBuffers(int paramInt, BufferCapabilities paramBufferCapabilities) throws AWTException { throw new AWTException("Page-flipping is not allowed on a lightweight component"); }
  
  public Image getBackBuffer() { throw new IllegalStateException("Page-flipping is not allowed on a lightweight component"); }
  
  public void flip(int paramInt1, int paramInt2, int paramInt3, int paramInt4, BufferCapabilities.FlipContents paramFlipContents) { throw new IllegalStateException("Page-flipping is not allowed on a lightweight component"); }
  
  public void destroyBuffers() {}
  
  public boolean isReparentSupported() { return false; }
  
  public void reparent(ContainerPeer paramContainerPeer) { throw new UnsupportedOperationException(); }
  
  public void layout() {}
  
  public Rectangle getBounds() { return new Rectangle(0, 0, 0, 0); }
  
  public void applyShape(Region paramRegion) {}
  
  public void setZOrder(ComponentPeer paramComponentPeer) {}
  
  public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration) { return false; }
  
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration) { return paramGraphicsConfiguration; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\NullComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */