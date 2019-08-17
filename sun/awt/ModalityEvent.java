package sun.awt;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;

public class ModalityEvent extends AWTEvent implements ActiveEvent {
  public static final int MODALITY_PUSHED = 1300;
  
  public static final int MODALITY_POPPED = 1301;
  
  private ModalityListener listener;
  
  public ModalityEvent(Object paramObject, ModalityListener paramModalityListener, int paramInt) {
    super(paramObject, paramInt);
    this.listener = paramModalityListener;
  }
  
  public void dispatch() {
    switch (getID()) {
      case 1300:
        this.listener.modalityPushed(this);
        return;
      case 1301:
        this.listener.modalityPopped(this);
        return;
    } 
    throw new Error("Invalid event id.");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\ModalityEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */