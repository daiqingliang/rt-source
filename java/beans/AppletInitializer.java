package java.beans;

import java.applet.Applet;
import java.beans.beancontext.BeanContext;

public interface AppletInitializer {
  void initialize(Applet paramApplet, BeanContext paramBeanContext);
  
  void activate(Applet paramApplet);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\AppletInitializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */