package javax.swing;

import java.awt.Graphics;
import java.awt.Rectangle;

interface GraphicsWrapper {
  Graphics subGraphics();
  
  boolean isClipIntersecting(Rectangle paramRectangle);
  
  int getClipX();
  
  int getClipY();
  
  int getClipWidth();
  
  int getClipHeight();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\GraphicsWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */