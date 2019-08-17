package java.beans;

import com.sun.beans.finder.BeanInfoFinder;
import com.sun.beans.finder.PropertyEditorFinder;
import java.awt.GraphicsEnvironment;
import java.util.Map;
import java.util.WeakHashMap;

final class ThreadGroupContext {
  private static final WeakIdentityMap<ThreadGroupContext> contexts = new WeakIdentityMap<ThreadGroupContext>() {
      protected ThreadGroupContext create(Object param1Object) { return new ThreadGroupContext(null); }
    };
  
  private Map<Class<?>, BeanInfo> beanInfoCache;
  
  private BeanInfoFinder beanInfoFinder;
  
  private PropertyEditorFinder propertyEditorFinder;
  
  static ThreadGroupContext getContext() { return (ThreadGroupContext)contexts.get(Thread.currentThread().getThreadGroup()); }
  
  private ThreadGroupContext() {}
  
  boolean isDesignTime() { return this.isDesignTime; }
  
  void setDesignTime(boolean paramBoolean) { this.isDesignTime = paramBoolean; }
  
  boolean isGuiAvailable() {
    Boolean bool = this.isGuiAvailable;
    return (bool != null) ? bool.booleanValue() : (!GraphicsEnvironment.isHeadless() ? 1 : 0);
  }
  
  void setGuiAvailable(boolean paramBoolean) { this.isGuiAvailable = Boolean.valueOf(paramBoolean); }
  
  BeanInfo getBeanInfo(Class<?> paramClass) { return (this.beanInfoCache != null) ? (BeanInfo)this.beanInfoCache.get(paramClass) : null; }
  
  BeanInfo putBeanInfo(Class<?> paramClass, BeanInfo paramBeanInfo) {
    if (this.beanInfoCache == null)
      this.beanInfoCache = new WeakHashMap(); 
    return (BeanInfo)this.beanInfoCache.put(paramClass, paramBeanInfo);
  }
  
  void removeBeanInfo(Class<?> paramClass) {
    if (this.beanInfoCache != null)
      this.beanInfoCache.remove(paramClass); 
  }
  
  void clearBeanInfoCache() {
    if (this.beanInfoCache != null)
      this.beanInfoCache.clear(); 
  }
  
  BeanInfoFinder getBeanInfoFinder() {
    if (this.beanInfoFinder == null)
      this.beanInfoFinder = new BeanInfoFinder(); 
    return this.beanInfoFinder;
  }
  
  PropertyEditorFinder getPropertyEditorFinder() {
    if (this.propertyEditorFinder == null)
      this.propertyEditorFinder = new PropertyEditorFinder(); 
    return this.propertyEditorFinder;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\ThreadGroupContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */