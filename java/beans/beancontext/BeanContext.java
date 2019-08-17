package java.beans.beancontext;

import java.beans.DesignMode;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public interface BeanContext extends BeanContextChild, Collection, DesignMode, Visibility {
  public static final Object globalHierarchyLock = new Object();
  
  Object instantiateChild(String paramString) throws IOException, ClassNotFoundException;
  
  InputStream getResourceAsStream(String paramString, BeanContextChild paramBeanContextChild) throws IllegalArgumentException;
  
  URL getResource(String paramString, BeanContextChild paramBeanContextChild) throws IllegalArgumentException;
  
  void addBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener);
  
  void removeBeanContextMembershipListener(BeanContextMembershipListener paramBeanContextMembershipListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */