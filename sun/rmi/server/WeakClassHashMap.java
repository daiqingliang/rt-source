package sun.rmi.server;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class WeakClassHashMap<V> extends Object {
  private Map<Class<?>, ValueCell<V>> internalMap = new WeakHashMap();
  
  public V get(Class<?> paramClass) {
    ValueCell valueCell;
    synchronized (this.internalMap) {
      valueCell = (ValueCell)this.internalMap.get(paramClass);
      if (valueCell == null) {
        valueCell = new ValueCell();
        this.internalMap.put(paramClass, valueCell);
      } 
    } 
    synchronized (valueCell) {
      Object object = null;
      if (valueCell.ref != null)
        object = valueCell.ref.get(); 
      if (object == null) {
        object = computeValue(paramClass);
        valueCell.ref = new SoftReference(object);
      } 
      return (V)object;
    } 
  }
  
  protected abstract V computeValue(Class<?> paramClass);
  
  private static class ValueCell<T> extends Object {
    Reference<T> ref = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\WeakClassHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */