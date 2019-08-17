package javax.swing.border;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

public interface Border {
  void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  Insets getBorderInsets(Component paramComponent);
  
  boolean isBorderOpaque();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\Border.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */