package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class SortingFocusTraversalPolicy extends InternalFrameFocusTraversalPolicy {
  private Comparator<? super Component> comparator;
  
  private boolean implicitDownCycleTraversal = true;
  
  private PlatformLogger log = PlatformLogger.getLogger("javax.swing.SortingFocusTraversalPolicy");
  
  private Container cachedRoot;
  
  private List<Component> cachedCycle;
  
  private static final SwingContainerOrderFocusTraversalPolicy fitnessTestPolicy = new SwingContainerOrderFocusTraversalPolicy();
  
  private final int FORWARD_TRAVERSAL = 0;
  
  private final int BACKWARD_TRAVERSAL = 1;
  
  private static final boolean legacySortingFTPEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.legacySortingFTPEnabled", "true")));
  
  private static final Method legacyMergeSortMethod = legacySortingFTPEnabled ? (Method)AccessController.doPrivileged(new PrivilegedAction<Method>() {
        public Method run() {
          try {
            Class clazz = Class.forName("java.util.Arrays");
            Method method = clazz.getDeclaredMethod("legacyMergeSort", new Class[] { Object[].class, Comparator.class });
            method.setAccessible(true);
            return method;
          } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException) {
            return null;
          } 
        }
      }) : null;
  
  protected SortingFocusTraversalPolicy() {}
  
  public SortingFocusTraversalPolicy(Comparator<? super Component> paramComparator) { this.comparator = paramComparator; }
  
  private List<Component> getFocusTraversalCycle(Container paramContainer) {
    ArrayList arrayList = new ArrayList();
    enumerateAndSortCycle(paramContainer, arrayList);
    return arrayList;
  }
  
  private int getComponentIndex(List<Component> paramList, Component paramComponent) {
    int i;
    try {
      i = Collections.binarySearch(paramList, paramComponent, this.comparator);
    } catch (ClassCastException classCastException) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### During the binary search for " + paramComponent + " the exception occurred: ", classCastException); 
      return -1;
    } 
    if (i < 0)
      i = paramList.indexOf(paramComponent); 
    return i;
  }
  
  private void enumerateAndSortCycle(Container paramContainer, List<Component> paramList) {
    if (paramContainer.isShowing()) {
      enumerateCycle(paramContainer, paramList);
      if (!legacySortingFTPEnabled || !legacySort(paramList, this.comparator))
        Collections.sort(paramList, this.comparator); 
    } 
  }
  
  private boolean legacySort(List<Component> paramList, Comparator<? super Component> paramComparator) {
    if (legacyMergeSortMethod == null)
      return false; 
    Object[] arrayOfObject = paramList.toArray();
    try {
      legacyMergeSortMethod.invoke(null, new Object[] { arrayOfObject, paramComparator });
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException illegalAccessException) {
      return false;
    } 
    ListIterator listIterator = paramList.listIterator();
    for (Object object : arrayOfObject) {
      listIterator.next();
      listIterator.set((Component)object);
    } 
    return true;
  }
  
  private void enumerateCycle(Container paramContainer, List<Component> paramList) {
    if (!paramContainer.isVisible() || !paramContainer.isDisplayable())
      return; 
    paramList.add(paramContainer);
    Component[] arrayOfComponent = paramContainer.getComponents();
    for (Component component : arrayOfComponent) {
      if (component instanceof Container) {
        Container container = (Container)component;
        if (!container.isFocusCycleRoot() && !container.isFocusTraversalPolicyProvider() && (!(container instanceof JComponent) || !((JComponent)container).isManagingFocus())) {
          enumerateCycle(container, paramList);
          continue;
        } 
      } 
      paramList.add(component);
      continue;
    } 
  }
  
  Container getTopmostProvider(Container paramContainer, Component paramComponent) {
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
          if (component != null && this.log.isLoggable(PlatformLogger.Level.FINE))
            this.log.fine("### Transfered focus down-cycle to " + component + " in the focus cycle root " + container); 
        } else {
          return null;
        } 
      } else if (container.isFocusTraversalPolicyProvider()) {
        component = (paramInt == 0) ? container.getFocusTraversalPolicy().getDefaultComponent(container) : container.getFocusTraversalPolicy().getLastComponent(container);
        if (component != null && this.log.isLoggable(PlatformLogger.Level.FINE))
          this.log.fine("### Transfered focus to " + component + " in the FTP provider " + container); 
      } 
    } 
    return component;
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent) {
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Searching in " + paramContainer + " for component after " + paramComponent); 
    if (paramContainer == null || paramComponent == null)
      throw new IllegalArgumentException("aContainer and aComponent cannot be null"); 
    if (!paramContainer.isFocusTraversalPolicyProvider() && !paramContainer.isFocusCycleRoot())
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider"); 
    if (paramContainer.isFocusCycleRoot() && !paramComponent.isFocusCycleRoot(paramContainer))
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent"); 
    Component component = getComponentDownCycle(paramComponent, 0);
    if (component != null)
      return component; 
    Container container = getTopmostProvider(paramContainer, paramComponent);
    if (container != null) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### Asking FTP " + container + " for component after " + paramComponent); 
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      Component component1 = focusTraversalPolicy.getComponentAfter(container, paramComponent);
      if (component1 != null) {
        if (this.log.isLoggable(PlatformLogger.Level.FINE))
          this.log.fine("### FTP returned " + component1); 
        return component1;
      } 
      paramComponent = container;
    } 
    List list = getFocusTraversalCycle(paramContainer);
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Cycle is " + list + ", component is " + paramComponent); 
    int i = getComponentIndex(list, paramComponent);
    if (i < 0) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer); 
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
    return null;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent) {
    if (paramContainer == null || paramComponent == null)
      throw new IllegalArgumentException("aContainer and aComponent cannot be null"); 
    if (!paramContainer.isFocusTraversalPolicyProvider() && !paramContainer.isFocusCycleRoot())
      throw new IllegalArgumentException("aContainer should be focus cycle root or focus traversal policy provider"); 
    if (paramContainer.isFocusCycleRoot() && !paramComponent.isFocusCycleRoot(paramContainer))
      throw new IllegalArgumentException("aContainer is not a focus cycle root of aComponent"); 
    Container container = getTopmostProvider(paramContainer, paramComponent);
    if (container != null) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### Asking FTP " + container + " for component after " + paramComponent); 
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      Component component = focusTraversalPolicy.getComponentBefore(container, paramComponent);
      if (component != null) {
        if (this.log.isLoggable(PlatformLogger.Level.FINE))
          this.log.fine("### FTP returned " + component); 
        return component;
      } 
      paramComponent = container;
      if (accept(paramComponent))
        return paramComponent; 
    } 
    List list = getFocusTraversalCycle(paramContainer);
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Cycle is " + list + ", component is " + paramComponent); 
    int i = getComponentIndex(list, paramComponent);
    if (i < 0) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### Didn't find component " + paramComponent + " in a cycle " + paramContainer); 
      return getLastComponent(paramContainer);
    } 
    while (--i >= 0) {
      Component component1 = (Component)list.get(i);
      Component component2;
      if (component1 != paramContainer && (component2 = getComponentDownCycle(component1, true)) != null)
        return component2; 
      if (accept(component1))
        return component1; 
      i--;
    } 
    if (paramContainer.isFocusCycleRoot()) {
      this.cachedRoot = paramContainer;
      this.cachedCycle = list;
      Component component = getLastComponent(paramContainer);
      this.cachedRoot = null;
      this.cachedCycle = null;
      return component;
    } 
    return null;
  }
  
  public Component getFirstComponent(Container paramContainer) {
    List list;
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Getting first component in " + paramContainer); 
    if (paramContainer == null)
      throw new IllegalArgumentException("aContainer cannot be null"); 
    if (this.cachedRoot == paramContainer) {
      list = this.cachedCycle;
    } else {
      list = getFocusTraversalCycle(paramContainer);
    } 
    if (list.size() == 0) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### Cycle is empty"); 
      return null;
    } 
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Cycle is " + list); 
    for (Component component : list) {
      if (accept(component))
        return component; 
      if (component != paramContainer && (component = getComponentDownCycle(component, false)) != null)
        return component; 
    } 
    return null;
  }
  
  public Component getLastComponent(Container paramContainer) {
    List list;
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Getting last component in " + paramContainer); 
    if (paramContainer == null)
      throw new IllegalArgumentException("aContainer cannot be null"); 
    if (this.cachedRoot == paramContainer) {
      list = this.cachedCycle;
    } else {
      list = getFocusTraversalCycle(paramContainer);
    } 
    if (list.size() == 0) {
      if (this.log.isLoggable(PlatformLogger.Level.FINE))
        this.log.fine("### Cycle is empty"); 
      return null;
    } 
    if (this.log.isLoggable(PlatformLogger.Level.FINE))
      this.log.fine("### Cycle is " + list); 
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
    return null;
  }
  
  public Component getDefaultComponent(Container paramContainer) { return getFirstComponent(paramContainer); }
  
  public void setImplicitDownCycleTraversal(boolean paramBoolean) { this.implicitDownCycleTraversal = paramBoolean; }
  
  public boolean getImplicitDownCycleTraversal() { return this.implicitDownCycleTraversal; }
  
  protected void setComparator(Comparator<? super Component> paramComparator) { this.comparator = paramComparator; }
  
  protected Comparator<? super Component> getComparator() { return this.comparator; }
  
  protected boolean accept(Component paramComponent) { return fitnessTestPolicy.accept(paramComponent); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SortingFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */