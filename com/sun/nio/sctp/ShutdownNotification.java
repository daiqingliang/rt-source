package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public abstract class ShutdownNotification implements Notification {
  public abstract Association association();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\ShutdownNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */