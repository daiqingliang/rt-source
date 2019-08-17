package com.sun.jndi.ldap;

import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.UnsolicitedNotification;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;

final class EventSupport {
  private static final boolean debug = false;
  
  private LdapCtx ctx;
  
  private Hashtable<NotifierArgs, NamingEventNotifier> notifiers = new Hashtable(11);
  
  private Vector<UnsolicitedNotificationListener> unsolicited = null;
  
  private EventQueue eventQueue;
  
  EventSupport(LdapCtx paramLdapCtx) { this.ctx = paramLdapCtx; }
  
  void addNamingListener(String paramString, int paramInt, NamingListener paramNamingListener) throws NamingException {
    if (paramNamingListener instanceof javax.naming.event.ObjectChangeListener || paramNamingListener instanceof javax.naming.event.NamespaceChangeListener) {
      NotifierArgs notifierArgs = new NotifierArgs(paramString, paramInt, paramNamingListener);
      NamingEventNotifier namingEventNotifier = (NamingEventNotifier)this.notifiers.get(notifierArgs);
      if (namingEventNotifier == null) {
        namingEventNotifier = new NamingEventNotifier(this, this.ctx, notifierArgs, paramNamingListener);
        this.notifiers.put(notifierArgs, namingEventNotifier);
      } else {
        namingEventNotifier.addNamingListener(paramNamingListener);
      } 
    } 
    if (paramNamingListener instanceof UnsolicitedNotificationListener) {
      if (this.unsolicited == null)
        this.unsolicited = new Vector(3); 
      this.unsolicited.addElement((UnsolicitedNotificationListener)paramNamingListener);
    } 
  }
  
  void addNamingListener(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException {
    if (paramNamingListener instanceof javax.naming.event.ObjectChangeListener || paramNamingListener instanceof javax.naming.event.NamespaceChangeListener) {
      NotifierArgs notifierArgs = new NotifierArgs(paramString1, paramString2, paramSearchControls, paramNamingListener);
      NamingEventNotifier namingEventNotifier = (NamingEventNotifier)this.notifiers.get(notifierArgs);
      if (namingEventNotifier == null) {
        namingEventNotifier = new NamingEventNotifier(this, this.ctx, notifierArgs, paramNamingListener);
        this.notifiers.put(notifierArgs, namingEventNotifier);
      } else {
        namingEventNotifier.addNamingListener(paramNamingListener);
      } 
    } 
    if (paramNamingListener instanceof UnsolicitedNotificationListener) {
      if (this.unsolicited == null)
        this.unsolicited = new Vector(3); 
      this.unsolicited.addElement((UnsolicitedNotificationListener)paramNamingListener);
    } 
  }
  
  void removeNamingListener(NamingListener paramNamingListener) {
    for (NamingEventNotifier namingEventNotifier : this.notifiers.values()) {
      if (namingEventNotifier != null) {
        namingEventNotifier.removeNamingListener(paramNamingListener);
        if (!namingEventNotifier.hasNamingListeners()) {
          namingEventNotifier.stop();
          this.notifiers.remove(namingEventNotifier.info);
        } 
      } 
    } 
    if (this.unsolicited != null)
      this.unsolicited.removeElement(paramNamingListener); 
  }
  
  boolean hasUnsolicited() { return (this.unsolicited != null && this.unsolicited.size() > 0); }
  
  void removeDeadNotifier(NotifierArgs paramNotifierArgs) { this.notifiers.remove(paramNotifierArgs); }
  
  void fireUnsolicited(Object paramObject) {
    if (this.unsolicited == null || this.unsolicited.size() == 0)
      return; 
    if (paramObject instanceof UnsolicitedNotification) {
      UnsolicitedNotificationEvent unsolicitedNotificationEvent = new UnsolicitedNotificationEvent(this.ctx, (UnsolicitedNotification)paramObject);
      queueEvent(unsolicitedNotificationEvent, this.unsolicited);
    } else if (paramObject instanceof NamingException) {
      NamingExceptionEvent namingExceptionEvent = new NamingExceptionEvent(this.ctx, (NamingException)paramObject);
      queueEvent(namingExceptionEvent, this.unsolicited);
      this.unsolicited = null;
    } 
  }
  
  void cleanup() {
    if (this.notifiers != null) {
      for (NamingEventNotifier namingEventNotifier : this.notifiers.values())
        namingEventNotifier.stop(); 
      this.notifiers = null;
    } 
    if (this.eventQueue != null) {
      this.eventQueue.stop();
      this.eventQueue = null;
    } 
  }
  
  void queueEvent(EventObject paramEventObject, Vector<? extends NamingListener> paramVector) {
    if (this.eventQueue == null)
      this.eventQueue = new EventQueue(); 
    Vector vector = (Vector)paramVector.clone();
    this.eventQueue.enqueue(paramEventObject, vector);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\EventSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */