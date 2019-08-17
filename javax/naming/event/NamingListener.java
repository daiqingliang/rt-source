package javax.naming.event;

import java.util.EventListener;

public interface NamingListener extends EventListener {
  void namingExceptionThrown(NamingExceptionEvent paramNamingExceptionEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\event\NamingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */