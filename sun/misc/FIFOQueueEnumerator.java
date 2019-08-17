package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class FIFOQueueEnumerator<T> extends Object implements Enumeration<T> {
  Queue<T> queue;
  
  QueueElement<T> cursor;
  
  FIFOQueueEnumerator(Queue<T> paramQueue) {
    this.queue = paramQueue;
    this.cursor = paramQueue.tail;
  }
  
  public boolean hasMoreElements() { return (this.cursor != null); }
  
  public T nextElement() {
    synchronized (this.queue) {
      if (this.cursor != null) {
        QueueElement queueElement = this.cursor;
        this.cursor = this.cursor.prev;
        return (T)queueElement.obj;
      } 
    } 
    throw new NoSuchElementException("FIFOQueueEnumerator");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\FIFOQueueEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */