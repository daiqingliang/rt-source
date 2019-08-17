package javax.swing.plaf;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;

public class IconUIResource implements Icon, UIResource, Serializable {
  private Icon delegate;
  
  public IconUIResource(Icon paramIcon) {
    if (paramIcon == null)
      throw new IllegalArgumentException("null delegate icon argument"); 
    this.delegate = paramIcon;
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) { this.delegate.paintIcon(paramComponent, paramGraphics, paramInt1, paramInt2); }
  
  public int getIconWidth() { return this.delegate.getIconWidth(); }
  
  public int getIconHeight() { return this.delegate.getIconHeight(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\IconUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */