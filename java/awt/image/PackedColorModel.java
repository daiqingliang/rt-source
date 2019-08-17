package java.awt.image;

import java.awt.color.ColorSpace;

public abstract class PackedColorModel extends ColorModel {
  int[] maskArray;
  
  int[] maskOffsets;
  
  float[] scaleFactors;
  
  public PackedColorModel(ColorSpace paramColorSpace, int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4) {
    super(paramInt1, createBitsArray(paramArrayOfInt, paramInt2), paramColorSpace, !(paramInt2 == 0), paramBoolean, paramInt3, paramInt4);
    if (paramInt1 < 1 || paramInt1 > 32)
      throw new IllegalArgumentException("Number of bits must be between 1 and 32."); 
    this.maskArray = new int[this.numComponents];
    this.maskOffsets = new int[this.numComponents];
    this.scaleFactors = new float[this.numComponents];
    for (byte b = 0; b < this.numColorComponents; b++)
      DecomposeMask(paramArrayOfInt[b], b, paramColorSpace.getName(b)); 
    if (paramInt2 != 0) {
      DecomposeMask(paramInt2, this.numColorComponents, "alpha");
      if (this.nBits[this.numComponents - 1] == 1)
        this.transparency = 2; 
    } 
  }
  
  public PackedColorModel(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6, int paramInt7) {
    super(paramInt1, createBitsArray(paramInt2, paramInt3, paramInt4, paramInt5), paramColorSpace, !(paramInt5 == 0), paramBoolean, paramInt6, paramInt7);
    if (paramColorSpace.getType() != 5)
      throw new IllegalArgumentException("ColorSpace must be TYPE_RGB."); 
    this.maskArray = new int[this.numComponents];
    this.maskOffsets = new int[this.numComponents];
    this.scaleFactors = new float[this.numComponents];
    DecomposeMask(paramInt2, 0, "red");
    DecomposeMask(paramInt3, 1, "green");
    DecomposeMask(paramInt4, 2, "blue");
    if (paramInt5 != 0) {
      DecomposeMask(paramInt5, 3, "alpha");
      if (this.nBits[3] == 1)
        this.transparency = 2; 
    } 
  }
  
  public final int getMask(int paramInt) { return this.maskArray[paramInt]; }
  
  public final int[] getMasks() { return (int[])this.maskArray.clone(); }
  
  private void DecomposeMask(int paramInt1, int paramInt2, String paramString) {
    int i = 0;
    int j = this.nBits[paramInt2];
    this.maskArray[paramInt2] = paramInt1;
    if (paramInt1 != 0)
      while ((paramInt1 & true) == 0) {
        paramInt1 >>>= 1;
        i++;
      }  
    if (i + j > this.pixel_bits)
      throw new IllegalArgumentException(paramString + " mask " + Integer.toHexString(this.maskArray[paramInt2]) + " overflows pixel (expecting " + this.pixel_bits + " bits"); 
    this.maskOffsets[paramInt2] = i;
    if (j == 0) {
      this.scaleFactors[paramInt2] = 256.0F;
    } else {
      this.scaleFactors[paramInt2] = 255.0F / ((1 << j) - 1);
    } 
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) { return new SinglePixelPackedSampleModel(this.transferType, paramInt1, paramInt2, this.maskArray); }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel) {
    if (!(paramSampleModel instanceof SinglePixelPackedSampleModel))
      return false; 
    if (this.numComponents != paramSampleModel.getNumBands())
      return false; 
    if (paramSampleModel.getTransferType() != this.transferType)
      return false; 
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
    int[] arrayOfInt = singlePixelPackedSampleModel.getBitMasks();
    if (arrayOfInt.length != this.maskArray.length)
      return false; 
    int i = (int)((1L << DataBuffer.getDataTypeSize(this.transferType)) - 1L);
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if ((i & arrayOfInt[b]) != (i & this.maskArray[b]))
        return false; 
    } 
    return true;
  }
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster) {
    if (!hasAlpha())
      return null; 
    int i = paramWritableRaster.getMinX();
    int j = paramWritableRaster.getMinY();
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramWritableRaster.getNumBands() - 1;
    return paramWritableRaster.createWritableChild(i, j, paramWritableRaster.getWidth(), paramWritableRaster.getHeight(), i, j, arrayOfInt);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof PackedColorModel))
      return false; 
    if (!super.equals(paramObject))
      return false; 
    PackedColorModel packedColorModel = (PackedColorModel)paramObject;
    int i = packedColorModel.getNumComponents();
    if (i != this.numComponents)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (this.maskArray[b] != packedColorModel.getMask(b))
        return false; 
    } 
    return true;
  }
  
  private static final int[] createBitsArray(int[] paramArrayOfInt, int paramInt) {
    int i = paramArrayOfInt.length;
    int j = (paramInt == 0) ? 0 : 1;
    int[] arrayOfInt = new int[i + j];
    for (byte b = 0; b < i; b++) {
      arrayOfInt[b] = countBits(paramArrayOfInt[b]);
      if (arrayOfInt[b] < 0)
        throw new IllegalArgumentException("Noncontiguous color mask (" + Integer.toHexString(paramArrayOfInt[b]) + "at index " + b); 
    } 
    if (paramInt != 0) {
      arrayOfInt[i] = countBits(paramInt);
      if (arrayOfInt[i] < 0)
        throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(paramInt)); 
    } 
    return arrayOfInt;
  }
  
  private static final int[] createBitsArray(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int[] arrayOfInt = new int[3 + ((paramInt4 == 0) ? 0 : 1)];
    arrayOfInt[0] = countBits(paramInt1);
    arrayOfInt[1] = countBits(paramInt2);
    arrayOfInt[2] = countBits(paramInt3);
    if (arrayOfInt[0] < 0)
      throw new IllegalArgumentException("Noncontiguous red mask (" + Integer.toHexString(paramInt1)); 
    if (arrayOfInt[1] < 0)
      throw new IllegalArgumentException("Noncontiguous green mask (" + Integer.toHexString(paramInt2)); 
    if (arrayOfInt[2] < 0)
      throw new IllegalArgumentException("Noncontiguous blue mask (" + Integer.toHexString(paramInt3)); 
    if (paramInt4 != 0) {
      arrayOfInt[3] = countBits(paramInt4);
      if (arrayOfInt[3] < 0)
        throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(paramInt4)); 
    } 
    return arrayOfInt;
  }
  
  private static final int countBits(int paramInt) {
    byte b = 0;
    if (paramInt != 0) {
      while ((paramInt & true) == 0)
        paramInt >>>= 1; 
      while ((paramInt & true) == 1) {
        paramInt >>>= 1;
        b++;
      } 
    } 
    return (paramInt != 0) ? -1 : b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\PackedColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */