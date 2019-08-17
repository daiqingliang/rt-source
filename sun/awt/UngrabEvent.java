package sun.awt;

import java.awt.AWTEvent;
import java.awt.Component;

public class UngrabEvent extends AWTEvent {
  private static final int UNGRAB_EVENT_ID = 1998;
  
  public UngrabEvent(Component paramComponent) { super(paramComponent, 1998); }
  
  public String toString() { return "sun.awt.UngrabEvent[" + getSource() + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\UngrabEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */