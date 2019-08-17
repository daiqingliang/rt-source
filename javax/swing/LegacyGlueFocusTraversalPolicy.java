package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

final class LegacyGlueFocusTraversalPolicy extends FocusTraversalPolicy implements Serializable {
  private FocusTraversalPolicy delegatePolicy;
  
  private DefaultFocusManager delegateManager;
  
  private HashMap<Component, Component> forwardMap = new HashMap();
  
  private HashMap<Component, Component> backwardMap = new HashMap();
  
  LegacyGlueFocusTraversalPolicy(FocusTraversalPolicy paramFocusTraversalPolicy) { this.delegatePolicy = paramFocusTraversalPolicy; }
  
  LegacyGlueFocusTraversalPolicy(DefaultFocusManager paramDefaultFocusManager) { this.delegateManager = paramDefaultFocusManager; }
  
  void setNextFocusableComponent(Component paramComponent1, Component paramComponent2) {
    this.forwardMap.put(paramComponent1, paramComponent2);
    this.backwardMap.put(paramComponent2, paramComponent1);
  }
  
  void unsetNextFocusableComponent(Component paramComponent1, Component paramComponent2) {
    this.forwardMap.remove(paramComponent1);
    this.backwardMap.remove(paramComponent2);
  }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent) {
    Component component = paramComponent;
    HashSet hashSet = new HashSet();
    do {
      Component component1 = component;
      component = (Component)this.forwardMap.get(component);
      if (component == null)
        return (this.delegatePolicy != null && component1.isFocusCycleRoot(paramContainer)) ? this.delegatePolicy.getComponentAfter(paramContainer, component1) : ((this.delegateManager != null) ? this.delegateManager.getComponentAfter(paramContainer, paramComponent) : null); 
      if (hashSet.contains(component))
        return null; 
      hashSet.add(component);
    } while (!accept(component));
    return component;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent) {
    Component component = paramComponent;
    HashSet hashSet = new HashSet();
    do {
      Component component1 = component;
      component = (Component)this.backwardMap.get(component);
      if (component == null)
        return (this.delegatePolicy != null && component1.isFocusCycleRoot(paramContainer)) ? this.delegatePolicy.getComponentBefore(paramContainer, component1) : ((this.delegateManager != null) ? this.delegateManager.getComponentBefore(paramContainer, paramComponent) : null); 
      if (hashSet.contains(component))
        return null; 
      hashSet.add(component);
    } while (!accept(component));
    return component;
  }
  
  public Component getFirstComponent(Container paramContainer) { return (this.delegatePolicy != null) ? this.delegatePolicy.getFirstComponent(paramContainer) : ((this.delegateManager != null) ? this.delegateManager.getFirstComponent(paramContainer) : null); }
  
  public Component getLastComponent(Container paramContainer) { return (this.delegatePolicy != null) ? this.delegatePolicy.getLastComponent(paramContainer) : ((this.delegateManager != null) ? this.delegateManager.getLastComponent(paramContainer) : null); }
  
  public Component getDefaultComponent(Container paramContainer) { return (this.delegatePolicy != null) ? this.delegatePolicy.getDefaultComponent(paramContainer) : getFirstComponent(paramContainer); }
  
  private boolean accept(Component paramComponent) {
    if (!paramComponent.isVisible() || !paramComponent.isDisplayable() || !paramComponent.isFocusable() || !paramComponent.isEnabled())
      return false; 
    if (!(paramComponent instanceof java.awt.Window))
      for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
        if (!container.isEnabled() && !container.isLightweight())
          return false; 
        if (container instanceof java.awt.Window)
          break; 
      }  
    return true;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.delegatePolicy instanceof Serializable) {
      paramObjectOutputStream.writeObject(this.delegatePolicy);
    } else {
      paramObjectOutputStream.writeObject(null);
    } 
    if (this.delegateManager instanceof Serializable) {
      paramObjectOutputStream.writeObject(this.delegateManager);
    } else {
      paramObjectOutputStream.writeObject(null);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.delegatePolicy = (FocusTraversalPolicy)paramObjectInputStream.readObject();
    this.delegateManager = (DefaultFocusManager)paramObjectInputStream.readObject();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\LegacyGlueFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */