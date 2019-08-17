package java.awt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sun.util.logging.PlatformLogger;

public class ContainerOrderFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable {
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.ContainerOrderFocusTraversalPolicy");
  
  private final int FORWARD_TRAVERSAL = 0;
  
  private final int BACKWARD_TRAVERSAL = 1;
  
  private static final long serialVersionUID = 486933713763926351L;
  
  private boolean implicitDownCycleTraversal = true;
  
  private Container cachedRoot;
  
  private List<Component> cachedCycle;
  
  private List<Component> getFocusTraversalCycle(Container paramContainer) {
    ArrayList arrayList = new ArrayList();
    enumerateCycle(paramContainer, arrayList);
    return arrayList;
  }
  
  private int getComponentIndex(List<Component> paramList, Component paramComponent) { return paramList.indexOf(paramComponent); }
  
  private void enumerateCycle(Container paramContainer, List<Component> paramList) {
    if (!paramContainer.isVisible() || !paramContainer.isDisplayable())
      return; 
    paramList.add(paramContainer);
    Component[] arrayOfComponent = paramContainer.getComponents();
    for (byte b = 0; b < arrayOfComponent.length; b++) {
      Component component = arrayOfComponent[b];
      if (component instanceof Container) {
        Container container = (Container)component;
        if (!container.isFocusCycleRoot() && !container.isFocusTraversalPolicyProvider()) {
          enumerateCycle(container, paramList);
          continue;
        } 
      } 
      paramList.add(component);
      continue;
    } 
  }
  
  private Container getTopmostProvider(Container paramContainer, Component paramComponent) {
    Container container1 = paramComponent.getParent();
    Container container2 = null;
    while (container1 != paramContainer && container1 != null) {
      if (container1.isFocusTraversalPolicyProvider())
        container2 = container1; 
      container1 = container1.getParent();
    } 
    return (container1 == null) ? null : container2;
  }
  
  private Component getComponentDownCycle(Component paramComponent, int paramInt) {
    Component component = null;
    if (paramComponent instanceof Container) {
      Container container = (Container)paramComponent;
      if (container.isFocusCycleRoot()) {
        if (getImplicitDownCycleTraversal()) {
          component = container.getFocusTraversalPolicy().getDefaultComponent(container);
          if (component != null && log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("### Transfered focus down-cycle to " + component + " in the focus cycle root " + container); 
        } else {
          return null;
        } 
      } else if (container.isFocusTraversalPolicyProvider()) {
        component = (paramInt == 0) ? container.getFocusTraversalPolicy().getDefaultComponent(container) : container.getFocusTraversalPolicy().getLastComponent(container);
        if (component != null && log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Transfered focus to " + component + " in the FTP provider " + container); 
      } 
    } 
    return component;
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent) {
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("### Searching in " + paramContainer + " for component after " + paramComponent); 
    if (paramContainer == null || paramComponent == null)
      throw new IllegalArgumentException("aContainer and aComponent cannot be null"); 
    if (!paramContainer.isFocusTraversalPolicyProvider() && !paramContainer.isFocusCycleRoot())
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider"); 
    if (paramContainer.isFocusCycleRoot() && !paramComponent.isFocusCycleRoot(paramContainer))
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent"); 
    synchronized (paramContainer.getTreeLock()) {
      if (!paramContainer.isVisible() || !paramContainer.isDisplayable())
        return null; 
      Component component = getComponentDownCycle(paramComponent, 0);
      if (component != null)
        return component; 
      Container container = getTopmostProvider(paramContainer, paramComponent);
      if (container != null) {
        if (log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Asking FTP " + container + " for component after " + paramComponent); 
        FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
        Component component1 = focusTraversalPolicy.getComponentAfter(container, paramComponent);
        if (component1 != null) {
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("### FTP returned " + component1); 
          return component1;
        } 
        paramComponent = container;
      } 
      List list = getFocusTraversalCycle(paramContainer);
      if (log.isLoggable(PlatformLogger.Level.FINE))
        log.fine("### Cycle is " + list + ", component is " + paramComponent); 
      int i = getComponentIndex(list, paramComponent);
      if (i < 0) {
        if (log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer); 
        return getFirstComponent(paramContainer);
      } 
      while (++i < list.size()) {
        component = (Component)list.get(i);
        if (accept(component))
          return component; 
        if ((component = getComponentDownCycle(component, false)) != null)
          return component; 
        i++;
      } 
      if (paramContainer.isFocusCycleRoot()) {
        this.cachedRoot = paramContainer;
        this.cachedCycle = list;
        component = getFirstComponent(paramContainer);
        this.cachedRoot = null;
        this.cachedCycle = null;
        return component;
      } 
    } 
    return null;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent) {
    if (paramContainer == null || paramComponent == null)
      throw new IllegalArgumentException("aContainer and aComponent cannot be null"); 
    if (!paramContainer.isFocusTraversalPolicyProvider() && !paramContainer.isFocusCycleRoot())
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider"); 
    if (paramContainer.isFocusCycleRoot() && !paramComponent.isFocusCycleRoot(paramContainer))
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent"); 
    synchronized (paramContainer.getTreeLock()) {
      if (!paramContainer.isVisible() || !paramContainer.isDisplayable())
        return null; 
      Container container = getTopmostProvider(paramContainer, paramComponent);
      if (container != null) {
        if (log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Asking FTP " + container + " for component after " + paramComponent); 
        FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
        Component component = focusTraversalPolicy.getComponentBefore(container, paramComponent);
        if (component != null) {
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("### FTP returned " + component); 
          return component;
        } 
        paramComponent = container;
        if (accept(paramComponent))
          return paramComponent; 
      } 
      List list = getFocusTraversalCycle(paramContainer);
      if (log.isLoggable(PlatformLogger.Level.FINE))
        log.fine("### Cycle is " + list + ", component is " + paramComponent); 
      int i = getComponentIndex(list, paramComponent);
      if (i < 0) {
        if (log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer); 
        return getLastComponent(paramContainer);
      } 
      Component component1 = null;
      Component component2 = null;
      while (--i >= 0) {
        component1 = (Component)list.get(i);
        if (component1 != paramContainer && (component2 = getComponentDownCycle(component1, true)) != null)
          return component2; 
        if (accept(component1))
          return component1; 
        i--;
      } 
      if (paramContainer.isFocusCycleRoot()) {
        this.cachedRoot = paramContainer;
        this.cachedCycle = list;
        component1 = getLastComponent(paramContainer);
        this.cachedRoot = null;
        this.cachedCycle = null;
        return component1;
      } 
    } 
    return null;
  }
  
  public Component getFirstComponent(Container paramContainer) {
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("### Getting first component in " + paramContainer); 
    if (paramContainer == null)
      throw new IllegalArgumentException("aContainer cannot be null"); 
    synchronized (paramContainer.getTreeLock()) {
      List list;
      if (!paramContainer.isVisible() || !paramContainer.isDisplayable())
        return null; 
      if (this.cachedRoot == paramContainer) {
        list = this.cachedCycle;
      } else {
        list = getFocusTraversalCycle(paramContainer);
      } 
      if (list.size() == 0) {
        if (log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Cycle is empty"); 
        return null;
      } 
      if (log.isLoggable(PlatformLogger.Level.FINE))
        log.fine("### Cycle is " + list); 
      for (Component component : list) {
        if (accept(component))
          return component; 
        if (component != paramContainer && (component = getComponentDownCycle(component, false)) != null)
          return component; 
      } 
    } 
    return null;
  }
  
  public Component getLastComponent(Container paramContainer) {
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("### Getting last component in " + paramContainer); 
    if (paramContainer == null)
      throw new IllegalArgumentException("aContainer cannot be null"); 
    synchronized (paramContainer.getTreeLock()) {
      List list;
      if (!paramContainer.isVisible() || !paramContainer.isDisplayable())
        return null; 
      if (this.cachedRoot == paramContainer) {
        list = this.cachedCycle;
      } else {
        list = getFocusTraversalCycle(paramContainer);
      } 
      if (list.size() == 0) {
        if (log.isLoggable(PlatformLogger.Level.FINE))
          log.fine("### Cycle is empty"); 
        return null;
      } 
      if (log.isLoggable(PlatformLogger.Level.FINE))
        log.fine("### Cycle is " + list); 
      for (int i = list.size() - 1; i >= 0; i--) {
        Component component = (Component)list.get(i);
        if (accept(component))
          return component; 
        if (component instanceof Container && component != paramContainer) {
          Container container = (Container)component;
          if (container.isFocusTraversalPolicyProvider()) {
            Component component1 = container.getFocusTraversalPolicy().getLastComponent(container);
            if (component1 != null)
              return component1; 
          } 
        } 
      } 
    } 
    return null;
  }
  
  public Component getDefaultComponent(Container paramContainer) { return getFirstComponent(paramContainer); }
  
  public void setImplicitDownCycleTraversal(boolean paramBoolean) { this.implicitDownCycleTraversal = paramBoolean; }
  
  public boolean getImplicitDownCycleTraversal() { return this.implicitDownCycleTraversal; }
  
  protected boolean accept(Component paramComponent) {
    if (!paramComponent.canBeFocusOwner())
      return false; 
    if (!(paramComponent instanceof Window))
      for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
        if (!container.isEnabled() && !container.isLightweight())
          return false; 
        if (container instanceof Window)
          break; 
      }  
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ContainerOrderFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */