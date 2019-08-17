package javax.swing.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;
import sun.reflect.misc.ReflectUtil;

public class EventListenerList implements Serializable {
  private static final Object[] NULL_ARRAY = new Object[0];
  
  protected Object[] listenerList = NULL_ARRAY;
  
  public Object[] getListenerList() { return this.listenerList; }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass) {
    Object[] arrayOfObject = this.listenerList;
    int i = getListenerCount(arrayOfObject, paramClass);
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(paramClass, i);
    byte b = 0;
    for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
      if (arrayOfObject[j] == paramClass)
        arrayOfEventListener[b++] = (EventListener)arrayOfObject[j + 1]; 
    } 
    return (T[])arrayOfEventListener;
  }
  
  public int getListenerCount() { return this.listenerList.length / 2; }
  
  public int getListenerCount(Class<?> paramClass) {
    Object[] arrayOfObject = this.listenerList;
    return getListenerCount(arrayOfObject, paramClass);
  }
  
  private int getListenerCount(Object[] paramArrayOfObject, Class paramClass) {
    byte b = 0;
    for (boolean bool = false; bool < paramArrayOfObject.length; bool += true) {
      if (paramClass == (Class)paramArrayOfObject[bool])
        b++; 
    } 
    return b;
  }
  
  public <T extends EventListener> void add(Class<T> paramClass, T paramT) {
    if (paramT == null)
      return; 
    if (!paramClass.isInstance(paramT))
      throw new IllegalArgumentException("Listener " + paramT + " is not of type " + paramClass); 
    if (this.listenerList == NULL_ARRAY) {
      this.listenerList = new Object[] { paramClass, paramT };
    } else {
      int i = this.listenerList.length;
      Object[] arrayOfObject = new Object[i + 2];
      System.arraycopy(this.listenerList, 0, arrayOfObject, 0, i);
      arrayOfObject[i] = paramClass;
      arrayOfObject[i + 1] = paramT;
      this.listenerList = arrayOfObject;
    } 
  }
  
  public <T extends EventListener> void remove(Class<T> paramClass, T paramT) {
    if (paramT == null)
      return; 
    if (!paramClass.isInstance(paramT))
      throw new IllegalArgumentException("Listener " + paramT + " is not of type " + paramClass); 
    int i = -1;
    for (int j = this.listenerList.length - 2; j >= 0; j -= 2) {
      if (this.listenerList[j] == paramClass && this.listenerList[j + 1].equals(paramT) == true) {
        i = j;
        break;
      } 
    } 
    if (i != -1) {
      Object[] arrayOfObject = new Object[this.listenerList.length - 2];
      System.arraycopy(this.listenerList, 0, arrayOfObject, 0, i);
      if (i < arrayOfObject.length)
        System.arraycopy(this.listenerList, i + 2, arrayOfObject, i, arrayOfObject.length - i); 
      this.listenerList = (arrayOfObject.length == 0) ? NULL_ARRAY : arrayOfObject;
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Object[] arrayOfObject = this.listenerList;
    paramObjectOutputStream.defaultWriteObject();
    for (boolean bool = false; bool < arrayOfObject.length; bool += true) {
      Class clazz = (Class)arrayOfObject[bool];
      EventListener eventListener = (EventListener)arrayOfObject[bool + true];
      if (eventListener != null && eventListener instanceof Serializable) {
        paramObjectOutputStream.writeObject(clazz.getName());
        paramObjectOutputStream.writeObject(eventListener);
      } 
    } 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.listenerList = NULL_ARRAY;
    paramObjectInputStream.defaultReadObject();
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      EventListener eventListener = (EventListener)paramObjectInputStream.readObject();
      String str = (String)object;
      ReflectUtil.checkPackageAccess(str);
      add(Class.forName(str, true, classLoader), eventListener);
    } 
  }
  
  public String toString() {
    Object[] arrayOfObject = this.listenerList;
    String str = "EventListenerList: ";
    str = str + (arrayOfObject.length / 2) + " listeners: ";
    for (boolean bool = false; bool <= arrayOfObject.length - 2; bool += true) {
      str = str + " type " + ((Class)arrayOfObject[bool]).getName();
      str = str + " listener " + arrayOfObject[bool + true];
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\EventListenerList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */