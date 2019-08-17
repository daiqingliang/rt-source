package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

public class DefaultFocusManager extends FocusManager {
  final FocusTraversalPolicy gluePolicy = new LegacyGlueFocusTraversalPolicy(this);
  
  private final FocusTraversalPolicy layoutPolicy = new LegacyLayoutFocusTraversalPolicy(this);
  
  private final LayoutComparator comparator = new LayoutComparator();
  
  public DefaultFocusManager() { setDefaultFocusTraversalPolicy(this.gluePolicy); }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent) {
    Container container = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (container != null) {
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      if (focusTraversalPolicy != this.gluePolicy)
        return focusTraversalPolicy.getComponentAfter(container, paramComponent); 
      this.comparator.setComponentOrientation(container.getComponentOrientation());
      return this.layoutPolicy.getComponentAfter(container, paramComponent);
    } 
    return null;
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent) {
    Container container = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (container != null) {
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      if (focusTraversalPolicy != this.gluePolicy)
        return focusTraversalPolicy.getComponentBefore(container, paramComponent); 
      this.comparator.setComponentOrientation(container.getComponentOrientation());
      return this.layoutPolicy.getComponentBefore(container, paramComponent);
    } 
    return null;
  }
  
  public Component getFirstComponent(Container paramContainer) {
    Container container = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (container != null) {
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      if (focusTraversalPolicy != this.gluePolicy)
        return focusTraversalPolicy.getFirstComponent(container); 
      this.comparator.setComponentOrientation(container.getComponentOrientation());
      return this.layoutPolicy.getFirstComponent(container);
    } 
    return null;
  }
  
  public Component getLastComponent(Container paramContainer) {
    Container container = paramContainer.isFocusCycleRoot() ? paramContainer : paramContainer.getFocusCycleRootAncestor();
    if (container != null) {
      FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
      if (focusTraversalPolicy != this.gluePolicy)
        return focusTraversalPolicy.getLastComponent(container); 
      this.comparator.setComponentOrientation(container.getComponentOrientation());
      return this.layoutPolicy.getLastComponent(container);
    } 
    return null;
  }
  
  public boolean compareTabOrder(Component paramComponent1, Component paramComponent2) { return (this.comparator.compare(paramComponent1, paramComponent2) < 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultFocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */