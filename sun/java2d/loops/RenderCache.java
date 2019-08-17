package sun.java2d.loops;

public final class RenderCache {
  private Entry[] entries;
  
  public RenderCache(int paramInt) { this.entries = new Entry[paramInt]; }
  
  public Object get(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    int i = this.entries.length - 1;
    for (int j = i; j >= 0; j--) {
      Entry entry = this.entries[j];
      if (entry == null)
        break; 
      if (entry.matches(paramSurfaceType1, paramCompositeType, paramSurfaceType2)) {
        if (j < i - 4) {
          System.arraycopy(this.entries, j + 1, this.entries, j, i - j);
          this.entries[i] = entry;
        } 
        return entry.getValue();
      } 
    } 
    return null;
  }
  
  public void put(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2, Object paramObject) {
    Entry entry = new Entry(paramSurfaceType1, paramCompositeType, paramSurfaceType2, paramObject);
    int i = this.entries.length;
    System.arraycopy(this.entries, 1, this.entries, 0, i - 1);
    this.entries[i - 1] = entry;
  }
  
  final class Entry {
    private SurfaceType src;
    
    private CompositeType comp;
    
    private SurfaceType dst;
    
    private Object value;
    
    public Entry(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2, Object param1Object) {
      this.src = param1SurfaceType1;
      this.comp = param1CompositeType;
      this.dst = param1SurfaceType2;
      this.value = param1Object;
    }
    
    public boolean matches(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) { return (this.src == param1SurfaceType1 && this.comp == param1CompositeType && this.dst == param1SurfaceType2); }
    
    public Object getValue() { return this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\RenderCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */