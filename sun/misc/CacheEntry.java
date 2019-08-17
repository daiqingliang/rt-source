package sun.misc;

class CacheEntry extends Ref {
  int hash;
  
  Object key;
  
  CacheEntry next;
  
  public Object reconstitute() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\CacheEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */