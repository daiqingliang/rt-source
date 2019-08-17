package javax.sql;

import java.util.EventListener;

public interface ConnectionEventListener extends EventListener {
  void connectionClosed(ConnectionEvent paramConnectionEvent);
  
  void connectionErrorOccurred(ConnectionEvent paramConnectionEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\ConnectionEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */