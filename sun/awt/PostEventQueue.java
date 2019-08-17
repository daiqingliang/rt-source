package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

class PostEventQueue {
  private EventQueueItem queueHead = null;
  
  private EventQueueItem queueTail = null;
  
  private final EventQueue eventQueue;
  
  private Thread flushThread = null;
  
  PostEventQueue(EventQueue paramEventQueue) { this.eventQueue = paramEventQueue; }
  
  public void flush() {
    Thread thread = Thread.currentThread();
    try {
      EventQueueItem eventQueueItem;
      synchronized (this) {
        if (thread == this.flushThread)
          return; 
        while (this.flushThread != null)
          wait(); 
        if (this.queueHead == null)
          return; 
        this.flushThread = thread;
        eventQueueItem = this.queueHead;
        this.queueHead = this.queueTail = null;
      } 
      try {
        while (eventQueueItem != null) {
          this.eventQueue.postEvent(eventQueueItem.event);
          eventQueueItem = eventQueueItem.next;
        } 
      } finally {
        synchronized (this) {
          this.flushThread = null;
          notifyAll();
        } 
      } 
    } catch (InterruptedException interruptedException) {
      thread.interrupt();
    } 
  }
  
  void postEvent(AWTEvent paramAWTEvent) {
    EventQueueItem eventQueueItem = new EventQueueItem(paramAWTEvent);
    synchronized (this) {
      if (this.queueHead == null) {
        this.queueHead = this.queueTail = eventQueueItem;
      } else {
        this.queueTail.next = eventQueueItem;
        this.queueTail = eventQueueItem;
      } 
    } 
    SunToolkit.wakeupEventQueue(this.eventQueue, (paramAWTEvent.getSource() == AWTAutoShutdown.getInstance()));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\PostEventQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */