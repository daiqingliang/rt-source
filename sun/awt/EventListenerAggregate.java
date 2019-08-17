package sun.awt;

import java.lang.reflect.Array;
import java.util.EventListener;

public class EventListenerAggregate {
  private EventListener[] listenerList;
  
  public EventListenerAggregate(Class<? extends EventListener> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("listener class is null"); 
    this.listenerList = (EventListener[])Array.newInstance(paramClass, 0);
  }
  
  private Class<?> getListenerClass() { return this.listenerList.getClass().getComponentType(); }
  
  public void add(EventListener paramEventListener) {
    Class clazz = getListenerClass();
    if (!clazz.isInstance(paramEventListener))
      throw new ClassCastException("listener " + paramEventListener + " is not an instance of listener class " + clazz); 
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(clazz, this.listenerList.length + 1);
    System.arraycopy(this.listenerList, 0, arrayOfEventListener, 0, this.listenerList.length);
    arrayOfEventListener[this.listenerList.length] = paramEventListener;
    this.listenerList = arrayOfEventListener;
  }
  
  public boolean remove(EventListener paramEventListener) {
    Class clazz = getListenerClass();
    if (!clazz.isInstance(paramEventListener))
      throw new ClassCastException("listener " + paramEventListener + " is not an instance of listener class " + clazz); 
    for (int i = 0; i < this.listenerList.length; i++) {
      if (this.listenerList[i].equals(paramEventListener)) {
        EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(clazz, this.listenerList.length - 1);
        System.arraycopy(this.listenerList, 0, arrayOfEventListener, 0, i);
        System.arraycopy(this.listenerList, i + 1, arrayOfEventListener, i, this.listenerList.length - i - 1);
        this.listenerList = arrayOfEventListener;
        return true;
      } 
    } 
    return false;
  }
  
  public EventListener[] getListenersInternal() { return this.listenerList; }
  
  public EventListener[] getListenersCopy() { return (this.listenerList.length == 0) ? this.listenerList : (EventListener[])this.listenerList.clone(); }
  
  public int size() { return this.listenerList.length; }
  
  public boolean isEmpty() { return (this.listenerList.length == 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\EventListenerAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */