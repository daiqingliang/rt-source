package javax.swing;

import java.awt.DefaultFocusTraversalPolicy;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;

public abstract class FocusManager extends DefaultKeyboardFocusManager {
  public static final String FOCUS_MANAGER_CLASS_PROPERTY = "FocusManagerClassName";
  
  private static boolean enabled = true;
  
  public static FocusManager getCurrentManager() {
    KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    return (keyboardFocusManager instanceof FocusManager) ? (FocusManager)keyboardFocusManager : new DelegatingDefaultFocusManager(keyboardFocusManager);
  }
  
  public static void setCurrentManager(FocusManager paramFocusManager) throws SecurityException {
    KeyboardFocusManager keyboardFocusManager;
    (keyboardFocusManager = (paramFocusManager instanceof DelegatingDefaultFocusManager) ? ((DelegatingDefaultFocusManager)paramFocusManager).getDelegate() : paramFocusManager).setCurrentKeyboardFocusManager(keyboardFocusManager);
  }
  
  @Deprecated
  public static void disableSwingFocusManager() {
    if (enabled) {
      enabled = false;
      KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new DefaultFocusTraversalPolicy());
    } 
  }
  
  @Deprecated
  public static boolean isFocusManagerEnabled() { return enabled; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\FocusManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */