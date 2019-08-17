package javax.swing;

final class LegacyLayoutFocusTraversalPolicy extends LayoutFocusTraversalPolicy {
  LegacyLayoutFocusTraversalPolicy(DefaultFocusManager paramDefaultFocusManager) { super(new CompareTabOrderComparator(paramDefaultFocusManager)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\LegacyLayoutFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */