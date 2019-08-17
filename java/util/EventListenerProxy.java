package java.util;

public abstract class EventListenerProxy<T extends EventListener> extends Object implements EventListener {
  private final T listener;
  
  public EventListenerProxy(T paramT) { this.listener = paramT; }
  
  public T getListener() { return (T)this.listener; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\EventListenerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */