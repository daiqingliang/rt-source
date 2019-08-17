package sun.misc;

import java.util.Enumeration;

public class Queue<T> extends Object {
  int length = 0;
  
  QueueElement<T> head = null;
  
  QueueElement<T> tail = null;
  
  public void enqueue(T paramT) {
    QueueElement queueElement = new QueueElement(paramT);
    if (this.head == null) {
      this.head = queueElement;
      this.tail = queueElement;
      this.length = 1;
    } else {
      queueElement.next = this.head;
      this.head.prev = queueElement;
      this.head = queueElement;
      this.length++;
    } 
    notify();
  }
  
  public T dequeue() throws InterruptedException { return (T)dequeue(0L); }
  
  public T dequeue(long paramLong) throws InterruptedException {
    while (this.tail == null)
      wait(paramLong); 
    QueueElement queueElement = this.tail;
    this.tail = queueElement.prev;
    if (this.tail == null) {
      this.head = null;
    } else {
      this.tail.next = null;
    } 
    this.length--;
    return (T)queueElement.obj;
  }
  
  public boolean isEmpty() { return (this.tail == null); }
  
  public final Enumeration<T> elements() { return new LIFOQueueEnumerator(this); }
  
  public final Enumeration<T> reverseElements() { return new FIFOQueueEnumerator(this); }
  
  public void dump(String paramString) {
    System.err.println(">> " + paramString);
    System.err.println("[" + this.length + " elt(s); head = " + ((this.head == null) ? "null" : (this.head.obj + "")) + " tail = " + ((this.tail == null) ? "null" : (this.tail.obj + "")));
    QueueElement queueElement1 = this.head;
    QueueElement queueElement2 = null;
    while (queueElement1 != null) {
      System.err.println("  " + queueElement1);
      queueElement2 = queueElement1;
      queueElement1 = queueElement1.next;
    } 
    if (queueElement2 != this.tail)
      System.err.println("  tail != last: " + this.tail + ", " + queueElement2); 
    System.err.println("]");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Queue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */