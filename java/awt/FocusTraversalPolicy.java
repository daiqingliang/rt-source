package java.awt;

public abstract class FocusTraversalPolicy {
  public abstract Component getComponentAfter(Container paramContainer, Component paramComponent);
  
  public abstract Component getComponentBefore(Container paramContainer, Component paramComponent);
  
  public abstract Component getFirstComponent(Container paramContainer);
  
  public abstract Component getLastComponent(Container paramContainer);
  
  public abstract Component getDefaultComponent(Container paramContainer);
  
  public Component getInitialComponent(Window paramWindow) {
    if (paramWindow == null)
      throw new IllegalArgumentException("window cannot be equal to null."); 
    Component component = getDefaultComponent(paramWindow);
    if (component == null && paramWindow.isFocusableWindow())
      component = paramWindow; 
    return component;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\FocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */