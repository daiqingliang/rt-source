package sun.java2d;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Spans {
  private static final int kMaxAddsSinceSort = 256;
  
  private List mSpans = new Vector(256);
  
  private int mAddsSinceSort = 0;
  
  public void add(float paramFloat1, float paramFloat2) {
    this.mSpans.add(new Span(paramFloat1, paramFloat2));
    if (this.mSpans != null && ++this.mAddsSinceSort >= 256)
      sortAndCollapse(); 
  }
  
  public void addInfinite() { this.mSpans = null; }
  
  public boolean intersects(float paramFloat1, float paramFloat2) {
    boolean bool;
    if (this.mSpans != null) {
      if (this.mAddsSinceSort > 0)
        sortAndCollapse(); 
      int i = Collections.binarySearch(this.mSpans, new Span(paramFloat1, paramFloat2), SpanIntersection.instance);
      bool = (i >= 0);
    } else {
      bool = true;
    } 
    return bool;
  }
  
  private void sortAndCollapse() {
    Collections.sort(this.mSpans);
    this.mAddsSinceSort = 0;
    Iterator iterator = this.mSpans.iterator();
    Span span = null;
    if (iterator.hasNext())
      span = (Span)iterator.next(); 
    while (iterator.hasNext()) {
      Span span1 = (Span)iterator.next();
      if (span.subsume(span1)) {
        iterator.remove();
        continue;
      } 
      span = span1;
    } 
  }
  
  static class Span implements Comparable {
    private float mStart;
    
    private float mEnd;
    
    Span(float param1Float1, float param1Float2) {
      this.mStart = param1Float1;
      this.mEnd = param1Float2;
    }
    
    final float getStart() { return this.mStart; }
    
    final float getEnd() { return this.mEnd; }
    
    final void setStart(float param1Float) { this.mStart = param1Float; }
    
    final void setEnd(float param1Float) { this.mEnd = param1Float; }
    
    boolean subsume(Span param1Span) {
      boolean bool = contains(param1Span.mStart);
      if (bool && param1Span.mEnd > this.mEnd)
        this.mEnd = param1Span.mEnd; 
      return bool;
    }
    
    boolean contains(float param1Float) { return (this.mStart <= param1Float && param1Float < this.mEnd); }
    
    public int compareTo(Object param1Object) {
      byte b;
      Span span = (Span)param1Object;
      float f = span.getStart();
      if (this.mStart < f) {
        b = -1;
      } else if (this.mStart > f) {
        b = 1;
      } else {
        b = 0;
      } 
      return b;
    }
    
    public String toString() { return "Span: " + this.mStart + " to " + this.mEnd; }
  }
  
  static class SpanIntersection implements Comparator {
    static final SpanIntersection instance = new SpanIntersection();
    
    public int compare(Object param1Object1, Object param1Object2) {
      byte b;
      Spans.Span span1 = (Spans.Span)param1Object1;
      Spans.Span span2 = (Spans.Span)param1Object2;
      if (span1.getEnd() <= span2.getStart()) {
        b = -1;
      } else if (span1.getStart() >= span2.getEnd()) {
        b = 1;
      } else {
        b = 0;
      } 
      return b;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\Spans.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */