package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class LIFOQueueEnumerator<T> extends Object implements Enumeration<T> {
  Queue<T> queue;
  
  QueueElement<T> cursor;
  
  LIFOQueueEnumerator(Queue<T> paramQueue) {
    this.queue = paramQueue;
    this.cursor = paramQueue.head;
  }
  
  public boolean hasMoreElements() { return (this.cursor != null); }
  
  public T nextElement() {
    synchronized (this.queue) {
      if (this.cursor != null) {
        QueueElement queueElement = this.cursor;
        this.cursor = this.cursor.next;
        return (T)queueElement.obj;
      } 
    } 
    throw new NoSuchElementException("LIFOQueueEnumerator");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\LIFOQueueEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */