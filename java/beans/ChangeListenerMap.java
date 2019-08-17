package java.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class ChangeListenerMap<L extends EventListener> extends Object {
  private Map<String, L[]> map;
  
  protected abstract L[] newArray(int paramInt);
  
  protected abstract L newProxy(String paramString, L paramL);
  
  public final void add(String paramString, L paramL) {
    if (this.map == null)
      this.map = new HashMap(); 
    EventListener[] arrayOfEventListener1 = (EventListener[])this.map.get(paramString);
    int i = (arrayOfEventListener1 != null) ? arrayOfEventListener1.length : 0;
    EventListener[] arrayOfEventListener2 = newArray(i + 1);
    arrayOfEventListener2[i] = paramL;
    if (arrayOfEventListener1 != null)
      System.arraycopy(arrayOfEventListener1, 0, arrayOfEventListener2, 0, i); 
    this.map.put(paramString, arrayOfEventListener2);
  }
  
  public final void remove(String paramString, L paramL) {
    if (this.map != null) {
      EventListener[] arrayOfEventListener = (EventListener[])this.map.get(paramString);
      if (arrayOfEventListener != null)
        for (int i = 0; i < arrayOfEventListener.length; i++) {
          if (paramL.equals(arrayOfEventListener[i])) {
            int j = arrayOfEventListener.length - 1;
            if (j > 0) {
              EventListener[] arrayOfEventListener1 = newArray(j);
              System.arraycopy(arrayOfEventListener, 0, arrayOfEventListener1, 0, i);
              System.arraycopy(arrayOfEventListener, i + 1, arrayOfEventListener1, i, j - i);
              this.map.put(paramString, arrayOfEventListener1);
              break;
            } 
            this.map.remove(paramString);
            if (this.map.isEmpty())
              this.map = null; 
            break;
          } 
        }  
    } 
  }
  
  public final L[] get(String paramString) { return (L[])((this.map != null) ? (EventListener[])this.map.get(paramString) : null); }
  
  public final void set(String paramString, L[] paramArrayOfL) {
    if (paramArrayOfL != null) {
      if (this.map == null)
        this.map = new HashMap(); 
      this.map.put(paramString, paramArrayOfL);
    } else if (this.map != null) {
      this.map.remove(paramString);
      if (this.map.isEmpty())
        this.map = null; 
    } 
  }
  
  public final L[] getListeners() {
    if (this.map == null)
      return (L[])newArray(0); 
    ArrayList arrayList = new ArrayList();
    EventListener[] arrayOfEventListener = (EventListener[])this.map.get(null);
    if (arrayOfEventListener != null)
      for (EventListener eventListener : arrayOfEventListener)
        arrayList.add(eventListener);  
    for (Map.Entry entry : this.map.entrySet()) {
      String str = (String)entry.getKey();
      if (str != null)
        for (EventListener eventListener : (EventListener[])entry.getValue())
          arrayList.add(newProxy(str, eventListener));  
    } 
    return (L[])(EventListener[])arrayList.toArray(newArray(arrayList.size()));
  }
  
  public final L[] getListeners(String paramString) {
    if (paramString != null) {
      EventListener[] arrayOfEventListener = get(paramString);
      if (arrayOfEventListener != null)
        return (L[])(EventListener[])arrayOfEventListener.clone(); 
    } 
    return (L[])newArray(0);
  }
  
  public final boolean hasListeners(String paramString) {
    if (this.map == null)
      return false; 
    EventListener[] arrayOfEventListener = (EventListener[])this.map.get(null);
    return (arrayOfEventListener != null || (paramString != null && null != this.map.get(paramString)));
  }
  
  public final Set<Map.Entry<String, L[]>> getEntries() { return (this.map != null) ? this.map.entrySet() : Collections.emptySet(); }
  
  public abstract L extract(L paramL);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\ChangeListenerMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */