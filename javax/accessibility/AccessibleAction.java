package javax.accessibility;

public interface AccessibleAction {
  public static final String TOGGLE_EXPAND = new String("toggleexpand");
  
  public static final String INCREMENT = new String("increment");
  
  public static final String DECREMENT = new String("decrement");
  
  public static final String CLICK = new String("click");
  
  public static final String TOGGLE_POPUP = new String("toggle popup");
  
  int getAccessibleActionCount();
  
  String getAccessibleActionDescription(int paramInt);
  
  boolean doAccessibleAction(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */