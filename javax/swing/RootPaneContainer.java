package javax.swing;

import java.awt.Component;
import java.awt.Container;

public interface RootPaneContainer {
  JRootPane getRootPane();
  
  void setContentPane(Container paramContainer);
  
  Container getContentPane();
  
  void setLayeredPane(JLayeredPane paramJLayeredPane);
  
  JLayeredPane getLayeredPane();
  
  void setGlassPane(Component paramComponent);
  
  Component getGlassPane();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\RootPaneContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */