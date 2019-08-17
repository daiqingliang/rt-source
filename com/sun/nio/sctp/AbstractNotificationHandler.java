package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class AbstractNotificationHandler<T> extends Object implements NotificationHandler<T> {
  public HandlerResult handleNotification(Notification paramNotification, T paramT) { return HandlerResult.CONTINUE; }
  
  public HandlerResult handleNotification(AssociationChangeNotification paramAssociationChangeNotification, T paramT) { return HandlerResult.CONTINUE; }
  
  public HandlerResult handleNotification(PeerAddressChangeNotification paramPeerAddressChangeNotification, T paramT) { return HandlerResult.CONTINUE; }
  
  public HandlerResult handleNotification(SendFailedNotification paramSendFailedNotification, T paramT) { return HandlerResult.CONTINUE; }
  
  public HandlerResult handleNotification(ShutdownNotification paramShutdownNotification, T paramT) { return HandlerResult.CONTINUE; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\AbstractNotificationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */