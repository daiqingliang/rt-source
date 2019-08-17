package sun.java2d.pipe;

public class RegionIterator {
  Region region;
  
  int curIndex;
  
  int numXbands;
  
  RegionIterator(Region paramRegion) { this.region = paramRegion; }
  
  public RegionIterator createCopy() {
    RegionIterator regionIterator = new RegionIterator(this.region);
    regionIterator.curIndex = this.curIndex;
    regionIterator.numXbands = this.numXbands;
    return regionIterator;
  }
  
  public void copyStateFrom(RegionIterator paramRegionIterator) {
    if (this.region != paramRegionIterator.region)
      throw new InternalError("region mismatch"); 
    this.curIndex = paramRegionIterator.curIndex;
    this.numXbands = paramRegionIterator.numXbands;
  }
  
  public boolean nextYRange(int[] paramArrayOfInt) {
    this.curIndex += this.numXbands * 2;
    this.numXbands = 0;
    if (this.curIndex >= this.region.endIndex)
      return false; 
    paramArrayOfInt[1] = this.region.bands[this.curIndex++];
    paramArrayOfInt[3] = this.region.bands[this.curIndex++];
    this.numXbands = this.region.bands[this.curIndex++];
    return true;
  }
  
  public boolean nextXBand(int[] paramArrayOfInt) {
    if (this.numXbands <= 0)
      return false; 
    this.numXbands--;
    paramArrayOfInt[0] = this.region.bands[this.curIndex++];
    paramArrayOfInt[2] = this.region.bands[this.curIndex++];
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\RegionIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */