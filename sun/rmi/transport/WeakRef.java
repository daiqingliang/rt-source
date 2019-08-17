package sun.rmi.transport;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import sun.rmi.runtime.Log;

class WeakRef extends WeakReference<Object> {
  private int hashValue;
  
  private Object strongRef = null;
  
  public WeakRef(Object paramObject) {
    super(paramObject);
    setHashValue(paramObject);
  }
  
  public WeakRef(Object paramObject, ReferenceQueue<Object> paramReferenceQueue) {
    super(paramObject, paramReferenceQueue);
    setHashValue(paramObject);
  }
  
  public void pin() {
    if (this.strongRef == null) {
      this.strongRef = get();
      if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
        DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + this.strongRef); 
    } 
  }
  
  public void unpin() {
    if (this.strongRef != null) {
      if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
        DGCImpl.dgcLog.log(Log.VERBOSE, "strongRef = " + this.strongRef); 
      this.strongRef = null;
    } 
  }
  
  private void setHashValue(Object paramObject) {
    if (paramObject != null) {
      this.hashValue = System.identityHashCode(paramObject);
    } else {
      this.hashValue = 0;
    } 
  }
  
  public int hashCode() { return this.hashValue; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof WeakRef) {
      if (paramObject == this)
        return true; 
      Object object = get();
      return (object != null && object == ((WeakRef)paramObject).get());
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\WeakRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */