package sun.awt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;

public class IconInfo {
  private int[] intIconData;
  
  private long[] longIconData;
  
  private Image image;
  
  private final int width;
  
  private final int height;
  
  private int scaledWidth;
  
  private int scaledHeight;
  
  private int rawLength;
  
  public IconInfo(int[] paramArrayOfInt) {
    this.intIconData = (null == paramArrayOfInt) ? null : Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    this.width = paramArrayOfInt[0];
    this.height = paramArrayOfInt[1];
    this.scaledWidth = this.width;
    this.scaledHeight = this.height;
    this.rawLength = this.width * this.height + 2;
  }
  
  public IconInfo(long[] paramArrayOfLong) {
    this.longIconData = (null == paramArrayOfLong) ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length);
    this.width = (int)paramArrayOfLong[0];
    this.height = (int)paramArrayOfLong[1];
    this.scaledWidth = this.width;
    this.scaledHeight = this.height;
    this.rawLength = this.width * this.height + 2;
  }
  
  public IconInfo(Image paramImage) {
    this.image = paramImage;
    if (paramImage instanceof ToolkitImage) {
      ImageRepresentation imageRepresentation = ((ToolkitImage)paramImage).getImageRep();
      imageRepresentation.reconstruct(32);
      this.width = imageRepresentation.getWidth();
      this.height = imageRepresentation.getHeight();
    } else {
      this.width = paramImage.getWidth(null);
      this.height = paramImage.getHeight(null);
    } 
    this.scaledWidth = this.width;
    this.scaledHeight = this.height;
    this.rawLength = this.width * this.height + 2;
  }
  
  public void setScaledSize(int paramInt1, int paramInt2) {
    this.scaledWidth = paramInt1;
    this.scaledHeight = paramInt2;
    this.rawLength = paramInt1 * paramInt2 + 2;
  }
  
  public boolean isValid() { return (this.width > 0 && this.height > 0); }
  
  public int getWidth() { return this.width; }
  
  public int getHeight() { return this.height; }
  
  public String toString() { return "IconInfo[w=" + this.width + ",h=" + this.height + ",sw=" + this.scaledWidth + ",sh=" + this.scaledHeight + "]"; }
  
  public int getRawLength() { return this.rawLength; }
  
  public int[] getIntData() {
    if (this.intIconData == null)
      if (this.longIconData != null) {
        this.intIconData = longArrayToIntArray(this.longIconData);
      } else if (this.image != null) {
        this.intIconData = imageToIntArray(this.image, this.scaledWidth, this.scaledHeight);
      }  
    return this.intIconData;
  }
  
  public long[] getLongData() {
    if (this.longIconData == null)
      if (this.intIconData != null) {
        this.longIconData = intArrayToLongArray(this.intIconData);
      } else if (this.image != null) {
        int[] arrayOfInt = imageToIntArray(this.image, this.scaledWidth, this.scaledHeight);
        this.longIconData = intArrayToLongArray(arrayOfInt);
      }  
    return this.longIconData;
  }
  
  public Image getImage() {
    if (this.image == null)
      if (this.intIconData != null) {
        this.image = intArrayToImage(this.intIconData);
      } else if (this.longIconData != null) {
        int[] arrayOfInt = longArrayToIntArray(this.longIconData);
        this.image = intArrayToImage(arrayOfInt);
      }  
    return this.image;
  }
  
  private static int[] longArrayToIntArray(long[] paramArrayOfLong) {
    int[] arrayOfInt = new int[paramArrayOfLong.length];
    for (byte b = 0; b < paramArrayOfLong.length; b++)
      arrayOfInt[b] = (int)paramArrayOfLong[b]; 
    return arrayOfInt;
  }
  
  private static long[] intArrayToLongArray(int[] paramArrayOfInt) {
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      arrayOfLong[b] = paramArrayOfInt[b]; 
    return arrayOfLong;
  }
  
  static Image intArrayToImage(int[] paramArrayOfInt) {
    DirectColorModel directColorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
    DataBufferInt dataBufferInt = new DataBufferInt(paramArrayOfInt, paramArrayOfInt.length - 2, 2);
    WritableRaster writableRaster = Raster.createPackedRaster(dataBufferInt, paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[0], new int[] { 16711680, 65280, 255, -16777216 }, null);
    return new BufferedImage(directColorModel, writableRaster, false, null);
  }
  
  static int[] imageToIntArray(Image paramImage, int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      return null; 
    DirectColorModel directColorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, false, 3);
    DataBufferInt dataBufferInt = new DataBufferInt(paramInt1 * paramInt2);
    WritableRaster writableRaster = Raster.createPackedRaster(dataBufferInt, paramInt1, paramInt2, paramInt1, new int[] { 16711680, 65280, 255, -16777216 }, null);
    BufferedImage bufferedImage = new BufferedImage(directColorModel, writableRaster, false, null);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.drawImage(paramImage, 0, 0, paramInt1, paramInt2, null);
    graphics.dispose();
    int[] arrayOfInt1 = dataBufferInt.getData();
    int[] arrayOfInt2 = new int[paramInt1 * paramInt2 + 2];
    arrayOfInt2[0] = paramInt1;
    arrayOfInt2[1] = paramInt2;
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 2, paramInt1 * paramInt2);
    return arrayOfInt2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\IconInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */