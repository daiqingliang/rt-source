package sun.awt;

import java.awt.event.WindowEvent;

public interface WindowClosingListener {
  RuntimeException windowClosingNotify(WindowEvent paramWindowEvent);
  
  RuntimeException windowClosingDelivered(WindowEvent paramWindowEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\WindowClosingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */