package sun.misc;

public abstract class LRUCache<N, V> extends Object {
  private V[] oa = null;
  
  private final int size;
  
  public LRUCache(int paramInt) { this.size = paramInt; }
  
  protected abstract V create(N paramN);
  
  protected abstract boolean hasName(V paramV, N paramN);
  
  public static void moveToFront(Object[] paramArrayOfObject, int paramInt) {
    Object object = paramArrayOfObject[paramInt];
    for (int i = paramInt; i > 0; i--)
      paramArrayOfObject[i] = paramArrayOfObject[i - 1]; 
    paramArrayOfObject[0] = object;
  }
  
  public V forName(N paramN) {
    if (this.oa == null) {
      Object[] arrayOfObject = (Object[])new Object[this.size];
      this.oa = arrayOfObject;
    } else {
      for (byte b = 0; b < this.oa.length; b++) {
        Object object1 = this.oa[b];
        if (object1 != null && hasName(object1, paramN)) {
          if (b)
            moveToFront(this.oa, b); 
          return (V)object1;
        } 
      } 
    } 
    Object object = create(paramN);
    this.oa[this.oa.length - 1] = object;
    moveToFront(this.oa, this.oa.length - 1);
    return (V)object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\LRUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */