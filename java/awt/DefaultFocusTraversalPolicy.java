package java.awt;

import java.awt.peer.ComponentPeer;

public class DefaultFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy {
  private static final long serialVersionUID = 8876966522510157497L;
  
  protected boolean accept(Component paramComponent) {
    if (!paramComponent.isVisible() || !paramComponent.isDisplayable() || !paramComponent.isEnabled())
      return false; 
    if (!(paramComponent instanceof Window))
      for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
        if (!container.isEnabled() && !container.isLightweight())
          return false; 
        if (container instanceof Window)
          break; 
      }  
    boolean bool = paramComponent.isFocusable();
    if (paramComponent.isFocusTraversableOverridden())
      return bool; 
    ComponentPeer componentPeer = paramComponent.getPeer();
    return (componentPeer != null && componentPeer.isFocusable());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\DefaultFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */