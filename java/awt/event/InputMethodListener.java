package java.awt.event;

import java.util.EventListener;

public interface InputMethodListener extends EventListener {
  void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent);
  
  void caretPositionChanged(InputMethodEvent paramInputMethodEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\InputMethodListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */