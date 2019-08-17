package javax.management;

import com.sun.jmx.remote.util.ClassLogger;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class NotificationBroadcasterSupport implements NotificationEmitter {
  private List<ListenerInfo> listenerList = new CopyOnWriteArrayList();
  
  private final Executor executor;
  
  private final MBeanNotificationInfo[] notifInfo;
  
  private static final Executor defaultExecutor = new Executor() {
      public void execute(Runnable param1Runnable) { param1Runnable.run(); }
    };
  
  private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
  
  private static final ClassLogger logger = new ClassLogger("javax.management", "NotificationBroadcasterSupport");
  
  public NotificationBroadcasterSupport() { this(null, (MBeanNotificationInfo[])null); }
  
  public NotificationBroadcasterSupport(Executor paramExecutor) { this(paramExecutor, (MBeanNotificationInfo[])null); }
  
  public NotificationBroadcasterSupport(MBeanNotificationInfo... paramVarArgs) { this(null, paramVarArgs); }
  
  public NotificationBroadcasterSupport(Executor paramExecutor, MBeanNotificationInfo... paramVarArgs) {
    this.executor = (paramExecutor != null) ? paramExecutor : defaultExecutor;
    this.notifInfo = (paramVarArgs == null) ? NO_NOTIFICATION_INFO : (MBeanNotificationInfo[])paramVarArgs.clone();
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    if (paramNotificationListener == null)
      throw new IllegalArgumentException("Listener can't be null"); 
    this.listenerList.add(new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject));
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    WildcardListenerInfo wildcardListenerInfo = new WildcardListenerInfo(paramNotificationListener);
    boolean bool = this.listenerList.removeAll(Collections.singleton(wildcardListenerInfo));
    if (!bool)
      throw new ListenerNotFoundException("Listener not registered"); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    ListenerInfo listenerInfo = new ListenerInfo(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool = this.listenerList.remove(listenerInfo);
    if (!bool)
      throw new ListenerNotFoundException("Listener not registered (with this filter and handback)"); 
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() { return (this.notifInfo.length == 0) ? this.notifInfo : (MBeanNotificationInfo[])this.notifInfo.clone(); }
  
  public void sendNotification(Notification paramNotification) {
    if (paramNotification == null)
      return; 
    for (ListenerInfo listenerInfo : this.listenerList) {
      boolean bool;
      try {
        bool = (listenerInfo.filter == null || listenerInfo.filter.isNotificationEnabled(paramNotification)) ? 1 : 0;
      } catch (Exception exception) {
        if (logger.debugOn())
          logger.debug("sendNotification", exception); 
        continue;
      } 
      if (bool)
        this.executor.execute(new SendNotifJob(paramNotification, listenerInfo)); 
    } 
  }
  
  protected void handleNotification(NotificationListener paramNotificationListener, Notification paramNotification, Object paramObject) { paramNotificationListener.handleNotification(paramNotification, paramObject); }
  
  private static class ListenerInfo {
    NotificationListener listener;
    
    NotificationFilter filter;
    
    Object handback;
    
    ListenerInfo(NotificationListener param1NotificationListener, NotificationFilter param1NotificationFilter, Object param1Object) {
      this.listener = param1NotificationListener;
      this.filter = param1NotificationFilter;
      this.handback = param1Object;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof ListenerInfo))
        return false; 
      ListenerInfo listenerInfo = (ListenerInfo)param1Object;
      return (listenerInfo instanceof NotificationBroadcasterSupport.WildcardListenerInfo) ? ((listenerInfo.listener == this.listener)) : ((listenerInfo.listener == this.listener && listenerInfo.filter == this.filter && listenerInfo.handback == this.handback));
    }
    
    public int hashCode() { return Objects.hashCode(this.listener); }
  }
  
  private class SendNotifJob implements Runnable {
    private final Notification notif;
    
    private final NotificationBroadcasterSupport.ListenerInfo listenerInfo;
    
    public SendNotifJob(Notification param1Notification, NotificationBroadcasterSupport.ListenerInfo param1ListenerInfo) {
      this.notif = param1Notification;
      this.listenerInfo = param1ListenerInfo;
    }
    
    public void run() {
      try {
        NotificationBroadcasterSupport.this.handleNotification(this.listenerInfo.listener, this.notif, this.listenerInfo.handback);
      } catch (Exception exception) {
        if (logger.debugOn())
          logger.debug("SendNotifJob-run", exception); 
      } 
    }
  }
  
  private static class WildcardListenerInfo extends ListenerInfo {
    WildcardListenerInfo(NotificationListener param1NotificationListener) throws ListenerNotFoundException { super(param1NotificationListener, null, null); }
    
    public boolean equals(Object param1Object) {
      assert !(param1Object instanceof WildcardListenerInfo);
      return param1Object.equals(this);
    }
    
    public int hashCode() { return super.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotificationBroadcasterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */