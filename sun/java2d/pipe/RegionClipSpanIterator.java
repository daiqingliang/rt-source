package sun.java2d.pipe;

public class RegionClipSpanIterator implements SpanIterator {
  Region rgn;
  
  SpanIterator spanIter;
  
  RegionIterator resetState;
  
  RegionIterator lwm;
  
  RegionIterator row;
  
  RegionIterator box;
  
  int spanlox;
  
  int spanhix;
  
  int spanloy;
  
  int spanhiy;
  
  int lwmloy;
  
  int lwmhiy;
  
  int rgnlox;
  
  int rgnloy;
  
  int rgnhix;
  
  int rgnhiy;
  
  int rgnbndslox;
  
  int rgnbndsloy;
  
  int rgnbndshix;
  
  int rgnbndshiy;
  
  int[] rgnbox = new int[4];
  
  int[] spanbox = new int[4];
  
  boolean doNextSpan;
  
  boolean doNextBox;
  
  boolean done = false;
  
  public RegionClipSpanIterator(Region paramRegion, SpanIterator paramSpanIterator) {
    this.spanIter = paramSpanIterator;
    this.resetState = paramRegion.getIterator();
    this.lwm = this.resetState.createCopy();
    if (!this.lwm.nextYRange(this.rgnbox)) {
      this.done = true;
      return;
    } 
    this.rgnloy = this.lwmloy = this.rgnbox[1];
    this.rgnhiy = this.lwmhiy = this.rgnbox[3];
    paramRegion.getBounds(this.rgnbox);
    this.rgnbndslox = this.rgnbox[0];
    this.rgnbndsloy = this.rgnbox[1];
    this.rgnbndshix = this.rgnbox[2];
    this.rgnbndshiy = this.rgnbox[3];
    if (this.rgnbndslox >= this.rgnbndshix || this.rgnbndsloy >= this.rgnbndshiy) {
      this.done = true;
      return;
    } 
    this.rgn = paramRegion;
    this.row = this.lwm.createCopy();
    this.box = this.row.createCopy();
    this.doNextSpan = true;
    this.doNextBox = false;
  }
  
  public void getPathBox(int[] paramArrayOfInt) {
    int[] arrayOfInt = new int[4];
    this.rgn.getBounds(arrayOfInt);
    this.spanIter.getPathBox(paramArrayOfInt);
    if (paramArrayOfInt[0] < arrayOfInt[0])
      paramArrayOfInt[0] = arrayOfInt[0]; 
    if (paramArrayOfInt[1] < arrayOfInt[1])
      paramArrayOfInt[1] = arrayOfInt[1]; 
    if (paramArrayOfInt[2] > arrayOfInt[2])
      paramArrayOfInt[2] = arrayOfInt[2]; 
    if (paramArrayOfInt[3] > arrayOfInt[3])
      paramArrayOfInt[3] = arrayOfInt[3]; 
  }
  
  public void intersectClipBox(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this.spanIter.intersectClipBox(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public boolean nextSpan(int[] paramArrayOfInt) {
    int m;
    int k;
    int j;
    int i;
    if (this.done)
      return false; 
    boolean bool = false;
    while (true) {
      while (this.doNextSpan) {
        if (!this.spanIter.nextSpan(this.spanbox)) {
          this.done = true;
          return false;
        } 
        this.spanlox = this.spanbox[0];
        if (this.spanlox >= this.rgnbndshix)
          continue; 
        this.spanloy = this.spanbox[1];
        if (this.spanloy >= this.rgnbndshiy)
          continue; 
        this.spanhix = this.spanbox[2];
        if (this.spanhix <= this.rgnbndslox)
          continue; 
        this.spanhiy = this.spanbox[3];
        if (this.spanhiy <= this.rgnbndsloy)
          continue; 
        if (this.lwmloy > this.spanloy) {
          this.lwm.copyStateFrom(this.resetState);
          this.lwm.nextYRange(this.rgnbox);
          this.lwmloy = this.rgnbox[1];
          this.lwmhiy = this.rgnbox[3];
        } 
        while (this.lwmhiy <= this.spanloy && this.lwm.nextYRange(this.rgnbox)) {
          this.lwmloy = this.rgnbox[1];
          this.lwmhiy = this.rgnbox[3];
        } 
        if (this.lwmhiy > this.spanloy && this.lwmloy < this.spanhiy) {
          if (this.rgnloy != this.lwmloy) {
            this.row.copyStateFrom(this.lwm);
            this.rgnloy = this.lwmloy;
            this.rgnhiy = this.lwmhiy;
          } 
          this.box.copyStateFrom(this.row);
          this.doNextBox = true;
          this.doNextSpan = false;
        } 
      } 
      if (bool) {
        bool = false;
        boolean bool1 = this.row.nextYRange(this.rgnbox);
        if (bool1) {
          this.rgnloy = this.rgnbox[1];
          this.rgnhiy = this.rgnbox[3];
        } 
        if (!bool1 || this.rgnloy >= this.spanhiy) {
          this.doNextSpan = true;
          continue;
        } 
        this.box.copyStateFrom(this.row);
        this.doNextBox = true;
        continue;
      } 
      if (this.doNextBox) {
        boolean bool1 = this.box.nextXBand(this.rgnbox);
        if (bool1) {
          this.rgnlox = this.rgnbox[0];
          this.rgnhix = this.rgnbox[2];
        } 
        if (!bool1 || this.rgnlox >= this.spanhix) {
          this.doNextBox = false;
          if (this.rgnhiy >= this.spanhiy) {
            this.doNextSpan = true;
            continue;
          } 
          bool = true;
          continue;
        } 
        this.doNextBox = (this.rgnhix <= this.spanlox);
        continue;
      } 
      this.doNextBox = true;
      if (this.spanlox > this.rgnlox) {
        i = this.spanlox;
      } else {
        i = this.rgnlox;
      } 
      if (this.spanloy > this.rgnloy) {
        j = this.spanloy;
      } else {
        j = this.rgnloy;
      } 
      if (this.spanhix < this.rgnhix) {
        k = this.spanhix;
      } else {
        k = this.rgnhix;
      } 
      if (this.spanhiy < this.rgnhiy) {
        m = this.spanhiy;
      } else {
        m = this.rgnhiy;
      } 
      if (i >= k || j >= m)
        continue; 
      break;
    } 
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
    paramArrayOfInt[2] = k;
    paramArrayOfInt[3] = m;
    return true;
  }
  
  public void skipDownTo(int paramInt) { this.spanIter.skipDownTo(paramInt); }
  
  public long getNativeIterator() { return 0L; }
  
  protected void finalize() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\RegionClipSpanIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */