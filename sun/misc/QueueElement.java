package sun.misc;

class QueueElement<T> extends Object {
  QueueElement<T> next = null;
  
  QueueElement<T> prev = null;
  
  T obj = null;
  
  QueueElement(T paramT) { this.obj = paramT; }
  
  public String toString() { return "QueueElement[obj=" + this.obj + ((this.prev == null) ? " null" : " prev") + ((this.next == null) ? " null" : " next") + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\QueueElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */