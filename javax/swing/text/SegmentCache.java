package javax.swing.text;

import java.util.ArrayList;
import java.util.List;

class SegmentCache {
  private static SegmentCache sharedCache = new SegmentCache();
  
  private List<Segment> segments = new ArrayList(11);
  
  public static SegmentCache getSharedInstance() { return sharedCache; }
  
  public static Segment getSharedSegment() { return getSharedInstance().getSegment(); }
  
  public static void releaseSharedSegment(Segment paramSegment) { getSharedInstance().releaseSegment(paramSegment); }
  
  public Segment getSegment() {
    synchronized (this) {
      int i = this.segments.size();
      if (i > 0)
        return (Segment)this.segments.remove(i - 1); 
    } 
    return new CachedSegment(null);
  }
  
  public void releaseSegment(Segment paramSegment) {
    if (paramSegment instanceof CachedSegment)
      synchronized (this) {
        paramSegment.array = null;
        paramSegment.count = 0;
        this.segments.add(paramSegment);
      }  
  }
  
  private static class CachedSegment extends Segment {
    private CachedSegment() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\SegmentCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */