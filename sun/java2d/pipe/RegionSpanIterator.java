package sun.java2d.pipe;

public class RegionSpanIterator implements SpanIterator {
  RegionIterator ri;
  
  int lox;
  
  int loy;
  
  int hix;
  
  int hiy;
  
  int curloy;
  
  int curhiy;
  
  boolean done = false;
  
  boolean isrect;
  
  public RegionSpanIterator(Region paramRegion) {
    int[] arrayOfInt = new int[4];
    paramRegion.getBounds(arrayOfInt);
    this.lox = arrayOfInt[0];
    this.loy = arrayOfInt[1];
    this.hix = arrayOfInt[2];
    this.hiy = arrayOfInt[3];
    this.isrect = paramRegion.isRectangular();
    this.ri = paramRegion.getIterator();
  }
  
  public void getPathBox(int[] paramArrayOfInt) {
    paramArrayOfInt[0] = this.lox;
    paramArrayOfInt[1] = this.loy;
    paramArrayOfInt[2] = this.hix;
    paramArrayOfInt[3] = this.hiy;
  }
  
  public void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt1 > this.lox)
      this.lox = paramInt1; 
    if (paramInt2 > this.loy)
      this.loy = paramInt2; 
    if (paramInt3 < this.hix)
      this.hix = paramInt3; 
    if (paramInt4 < this.hiy)
      this.hiy = paramInt4; 
    this.done = (this.lox >= this.hix || this.loy >= this.hiy);
  }
  
  public boolean nextSpan(int[] paramArrayOfInt) {
    int j;
    int i;
    if (this.done)
      return false; 
    if (this.isrect) {
      getPathBox(paramArrayOfInt);
      this.done = true;
      return true;
    } 
    int k = this.curloy;
    int m = this.curhiy;
    do {
      while (!this.ri.nextXBand(paramArrayOfInt)) {
        if (!this.ri.nextYRange(paramArrayOfInt)) {
          this.done = true;
          return false;
        } 
        k = paramArrayOfInt[1];
        m = paramArrayOfInt[3];
        if (k < this.loy)
          k = this.loy; 
        if (m > this.hiy)
          m = this.hiy; 
        if (k >= this.hiy) {
          this.done = true;
          return false;
        } 
      } 
      i = paramArrayOfInt[0];
      j = paramArrayOfInt[2];
      if (i < this.lox)
        i = this.lox; 
      if (j <= this.hix)
        continue; 
      j = this.hix;
    } while (i >= j || k >= m);
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = this.curloy = k;
    paramArrayOfInt[2] = j;
    paramArrayOfInt[3] = this.curhiy = m;
    return true;
  }
  
  public void skipDownTo(int paramInt) { this.loy = paramInt; }
  
  public long getNativeIterator() { return 0L; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\RegionSpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */