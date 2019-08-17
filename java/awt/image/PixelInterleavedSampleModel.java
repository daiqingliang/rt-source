package java.awt.image;

public class PixelInterleavedSampleModel extends ComponentSampleModel {
  public PixelInterleavedSampleModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt) {
    super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramArrayOfInt);
    int i = this.bandOffsets[0];
    int j = this.bandOffsets[0];
    for (byte b = 1; b < this.bandOffsets.length; b++) {
      i = Math.min(i, this.bandOffsets[b]);
      j = Math.max(j, this.bandOffsets[b]);
    } 
    j -= i;
    if (j > paramInt5)
      throw new IllegalArgumentException("Offsets between bands must be less than the scanline  stride"); 
    if (paramInt4 * paramInt2 > paramInt5)
      throw new IllegalArgumentException("Pixel stride times width must be less than or equal to the scanline stride"); 
    if (paramInt4 < j)
      throw new IllegalArgumentException("Pixel stride must be greater than or equal to the offsets between bands"); 
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt;
    int i = this.bandOffsets[0];
    int j = this.bandOffsets.length;
    for (byte b = 1; b < j; b++) {
      if (this.bandOffsets[b] < i)
        i = this.bandOffsets[b]; 
    } 
    if (i > 0) {
      arrayOfInt = new int[j];
      for (byte b1 = 0; b1 < j; b1++)
        arrayOfInt[b1] = this.bandOffsets[b1] - i; 
    } else {
      arrayOfInt = this.bandOffsets;
    } 
    return new PixelInterleavedSampleModel(this.dataType, paramInt1, paramInt2, this.pixelStride, this.pixelStride * paramInt1, arrayOfInt);
  }
  
  public SampleModel createSubsetSampleModel(int[] paramArrayOfInt) {
    int[] arrayOfInt = new int[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      arrayOfInt[b] = this.bandOffsets[paramArrayOfInt[b]]; 
    return new PixelInterleavedSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, arrayOfInt);
  }
  
  public int hashCode() { return super.hashCode() ^ true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\PixelInterleavedSampleModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */