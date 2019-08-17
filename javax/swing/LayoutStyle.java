package javax.swing;

import java.awt.Container;
import sun.awt.AppContext;

public abstract class LayoutStyle {
  public static void setInstance(LayoutStyle paramLayoutStyle) {
    synchronized (LayoutStyle.class) {
      if (paramLayoutStyle == null) {
        AppContext.getAppContext().remove(LayoutStyle.class);
      } else {
        AppContext.getAppContext().put(LayoutStyle.class, paramLayoutStyle);
      } 
    } 
  }
  
  public static LayoutStyle getInstance() {
    LayoutStyle layoutStyle;
    synchronized (LayoutStyle.class) {
      layoutStyle = (LayoutStyle)AppContext.getAppContext().get(LayoutStyle.class);
    } 
    return (layoutStyle == null) ? UIManager.getLookAndFeel().getLayoutStyle() : layoutStyle;
  }
  
  public abstract int getPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, ComponentPlacement paramComponentPlacement, int paramInt, Container paramContainer);
  
  public abstract int getContainerGap(JComponent paramJComponent, int paramInt, Container paramContainer);
  
  public enum ComponentPlacement {
    RELATED, UNRELATED, INDENT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\LayoutStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */