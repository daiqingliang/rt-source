package javax.swing;

import java.awt.Component;
import java.util.Comparator;

final class CompareTabOrderComparator extends Object implements Comparator<Component> {
  private final DefaultFocusManager defaultFocusManager;
  
  CompareTabOrderComparator(DefaultFocusManager paramDefaultFocusManager) { this.defaultFocusManager = paramDefaultFocusManager; }
  
  public int compare(Component paramComponent1, Component paramComponent2) { return (paramComponent1 == paramComponent2) ? 0 : (this.defaultFocusManager.compareTabOrder(paramComponent1, paramComponent2) ? -1 : 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\CompareTabOrderComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */