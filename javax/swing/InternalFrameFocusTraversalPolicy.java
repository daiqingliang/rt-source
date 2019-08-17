package javax.swing;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;

public abstract class InternalFrameFocusTraversalPolicy extends FocusTraversalPolicy {
  public Component getInitialComponent(JInternalFrame paramJInternalFrame) { return getDefaultComponent(paramJInternalFrame); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\InternalFrameFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */