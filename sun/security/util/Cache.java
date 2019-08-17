package sun.security.util;

import java.util.Arrays;
import java.util.Map;

public abstract class Cache<K, V> extends Object {
  public abstract int size();
  
  public abstract void clear();
  
  public abstract void put(K paramK, V paramV);
  
  public abstract V get(Object paramObject);
  
  public abstract void remove(Object paramObject);
  
  public abstract void setCapacity(int paramInt);
  
  public abstract void setTimeout(int paramInt);
  
  public abstract void accept(CacheVisitor<K, V> paramCacheVisitor);
  
  public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt) { return new MemoryCache(true, paramInt); }
  
  public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt1, int paramInt2) { return new MemoryCache(true, paramInt1, paramInt2); }
  
  public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt) { return new MemoryCache(false, paramInt); }
  
  public static <K, V> Cache<K, V> newNullCache() { return NullCache.INSTANCE; }
  
  public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt1, int paramInt2) { return new MemoryCache(false, paramInt1, paramInt2); }
  
  public static interface CacheVisitor<K, V> {
    void visit(Map<K, V> param1Map);
  }
  
  public static class EqualByteArray {
    private final byte[] b;
    
    public EqualByteArray(byte[] param1ArrayOfByte) { this.b = param1ArrayOfByte; }
    
    public int hashCode() {
      int i = this.hash;
      if (i == 0) {
        i = this.b.length + 1;
        for (byte b1 = 0; b1 < this.b.length; b1++)
          i += (this.b[b1] & 0xFF) * 37; 
        this.hash = i;
      } 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof EqualByteArray))
        return false; 
      EqualByteArray equalByteArray = (EqualByteArray)param1Object;
      return Arrays.equals(this.b, equalByteArray.b);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */