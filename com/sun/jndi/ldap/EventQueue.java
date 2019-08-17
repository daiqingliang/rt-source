package com.sun.jndi.ldap;

import java.util.EventObject;
import java.util.Vector;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;

final class EventQueue implements Runnable {
  private static final boolean debug = false;
  
  private QueueElement head = null;
  
  private QueueElement tail = null;
  
  private Thread qThread = Obj.helper.createThread(this);
  
  EventQueue() {
    this.qThread.setDaemon(true);
    this.qThread.start();
  }
  
  void enqueue(EventObject paramEventObject, Vector<NamingListener> paramVector) {
    QueueElement queueElement = new QueueElement(paramEventObject, paramVector);
    if (this.head == null) {
      this.head = queueElement;
      this.tail = queueElement;
    } else {
      queueElement.next = this.head;
      this.head.prev = queueElement;
      this.head = queueElement;
    } 
    notify();
  }
  
  private QueueElement dequeue() throws InterruptedException {
    while (this.tail == null)
      wait(); 
    QueueElement queueElement = this.tail;
    this.tail = queueElement.prev;
    if (this.tail == null) {
      this.head = null;
    } else {
      this.tail.next = null;
    } 
    queueElement.prev = queueElement.next = null;
    return queueElement;
  }
  
  public void run() {
    try {
      QueueElement queueElement;
      while ((queueElement = dequeue()) != null) {
        EventObject eventObject = queueElement.event;
        Vector vector = queueElement.vector;
        for (byte b = 0; b < vector.size(); b++) {
          if (eventObject instanceof NamingEvent) {
            ((NamingEvent)eventObject).dispatch((NamingListener)vector.elementAt(b));
          } else if (eventObject instanceof NamingExceptionEvent) {
            ((NamingExceptionEvent)eventObject).dispatch((NamingListener)vector.elementAt(b));
          } else if (eventObject instanceof UnsolicitedNotificationEvent) {
            ((UnsolicitedNotificationEvent)eventObject).dispatch((UnsolicitedNotificationListener)vector.elementAt(b));
          } 
        } 
        queueElement = null;
        eventObject = null;
        vector = null;
      } 
    } catch (InterruptedException interruptedException) {}
  }
  
  void stop() {
    if (this.qThread != null) {
      this.qThread.interrupt();
      this.qThread = null;
    } 
  }
  
  private static class QueueElement {
    QueueElement next = null;
    
    QueueElement prev = null;
    
    EventObject event = null;
    
    Vector<NamingListener> vector = null;
    
    QueueElement(EventObject param1EventObject, Vector<NamingListener> param1Vector) {
      this.event = param1EventObject;
      this.vector = param1Vector;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\EventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */