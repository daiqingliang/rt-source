package sun.awt.im;

import java.awt.Component;
import java.util.Locale;

public abstract class InputMethodManager {
  private static final String threadName = "AWT-InputMethodManager";
  
  private static final Object LOCK = new Object();
  
  private static InputMethodManager inputMethodManager;
  
  public static final InputMethodManager getInstance() {
    if (inputMethodManager != null)
      return inputMethodManager; 
    synchronized (LOCK) {
      if (inputMethodManager == null) {
        ExecutableInputMethodManager executableInputMethodManager = new ExecutableInputMethodManager();
        if (executableInputMethodManager.hasMultipleInputMethods()) {
          executableInputMethodManager.initialize();
          Thread thread = new Thread(executableInputMethodManager, "AWT-InputMethodManager");
          thread.setDaemon(true);
          thread.setPriority(6);
          thread.start();
        } 
        inputMethodManager = executableInputMethodManager;
      } 
    } 
    return inputMethodManager;
  }
  
  public abstract String getTriggerMenuString();
  
  public abstract void notifyChangeRequest(Component paramComponent);
  
  public abstract void notifyChangeRequestByHotKey(Component paramComponent);
  
  abstract void setInputContext(InputContext paramInputContext);
  
  abstract InputMethodLocator findInputMethod(Locale paramLocale);
  
  abstract Locale getDefaultKeyboardLocale();
  
  abstract boolean hasMultipleInputMethods();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputMethodManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */