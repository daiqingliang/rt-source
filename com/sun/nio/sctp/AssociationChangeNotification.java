package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class AssociationChangeNotification implements Notification {
  public abstract Association association();
  
  public abstract AssocChangeEvent event();
  
  @Exported
  public enum AssocChangeEvent {
    COMM_UP, COMM_LOST, RESTART, SHUTDOWN, CANT_START;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\AssociationChangeNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */