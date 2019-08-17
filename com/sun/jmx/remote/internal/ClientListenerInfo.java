package com.sun.jmx.remote.internal;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class ClientListenerInfo {
  private final ObjectName name;
  
  private final Integer listenerID;
  
  private final NotificationFilter filter;
  
  private final NotificationListener listener;
  
  private final Object handback;
  
  private final Subject delegationSubject;
  
  public ClientListenerInfo(Integer paramInteger, ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject, Subject paramSubject) {
    this.listenerID = paramInteger;
    this.name = paramObjectName;
    this.listener = paramNotificationListener;
    this.filter = paramNotificationFilter;
    this.handback = paramObject;
    this.delegationSubject = paramSubject;
  }
  
  public ObjectName getObjectName() { return this.name; }
  
  public Integer getListenerID() { return this.listenerID; }
  
  public NotificationFilter getNotificationFilter() { return this.filter; }
  
  public NotificationListener getListener() { return this.listener; }
  
  public Object getHandback() { return this.handback; }
  
  public Subject getDelegationSubject() { return this.delegationSubject; }
  
  public boolean sameAs(ObjectName paramObjectName) { return getObjectName().equals(paramObjectName); }
  
  public boolean sameAs(ObjectName paramObjectName, NotificationListener paramNotificationListener) { return (getObjectName().equals(paramObjectName) && getListener() == paramNotificationListener); }
  
  public boolean sameAs(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) { return (getObjectName().equals(paramObjectName) && getListener() == paramNotificationListener && getNotificationFilter() == paramNotificationFilter && getHandback() == paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\ClientListenerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */