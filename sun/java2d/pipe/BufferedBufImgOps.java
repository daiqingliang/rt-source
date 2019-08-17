package sun.java2d.pipe;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import sun.java2d.SurfaceData;

public class BufferedBufImgOps {
  public static void enableBufImgOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp) {
    if (paramBufferedImageOp instanceof ConvolveOp) {
      enableConvolveOp(paramRenderQueue, paramSurfaceData, (ConvolveOp)paramBufferedImageOp);
    } else if (paramBufferedImageOp instanceof RescaleOp) {
      enableRescaleOp(paramRenderQueue, paramSurfaceData, paramBufferedImage, (RescaleOp)paramBufferedImageOp);
    } else if (paramBufferedImageOp instanceof LookupOp) {
      enableLookupOp(paramRenderQueue, paramSurfaceData, paramBufferedImage, (LookupOp)paramBufferedImageOp);
    } else {
      throw new InternalError("Unknown BufferedImageOp");
    } 
  }
  
  public static void disableBufImgOp(RenderQueue paramRenderQueue, BufferedImageOp paramBufferedImageOp) {
    if (paramBufferedImageOp instanceof ConvolveOp) {
      disableConvolveOp(paramRenderQueue);
    } else if (paramBufferedImageOp instanceof RescaleOp) {
      disableRescaleOp(paramRenderQueue);
    } else if (paramBufferedImageOp instanceof LookupOp) {
      disableLookupOp(paramRenderQueue);
    } else {
      throw new InternalError("Unknown BufferedImageOp");
    } 
  }
  
  public static boolean isConvolveOpValid(ConvolveOp paramConvolveOp) {
    Kernel kernel = paramConvolveOp.getKernel();
    int i = kernel.getWidth();
    int j = kernel.getHeight();
    return !((i != 3 || j != 3) && (i != 5 || j != 5));
  }
  
  private static void enableConvolveOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, ConvolveOp paramConvolveOp) {
    boolean bool = (paramConvolveOp.getEdgeCondition() == 0) ? 1 : 0;
    Kernel kernel = paramConvolveOp.getKernel();
    int i = kernel.getWidth();
    int j = kernel.getHeight();
    int k = i * j;
    int m = 4;
    int n = 24 + k * m;
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(n, 4);
    renderBuffer.putInt(120);
    renderBuffer.putLong(paramSurfaceData.getNativeOps());
    renderBuffer.putInt(bool ? 1 : 0);
    renderBuffer.putInt(i);
    renderBuffer.putInt(j);
    renderBuffer.put(kernel.getKernelData(null));
  }
  
  private static void disableConvolveOp(RenderQueue paramRenderQueue) {
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacity(4);
    renderBuffer.putInt(121);
  }
  
  public static boolean isRescaleOpValid(RescaleOp paramRescaleOp, BufferedImage paramBufferedImage) {
    int i = paramRescaleOp.getNumFactors();
    ColorModel colorModel = paramBufferedImage.getColorModel();
    if (colorModel instanceof java.awt.image.IndexColorModel)
      throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image"); 
    if (i != 1 && i != colorModel.getNumColorComponents() && i != colorModel.getNumComponents())
      throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components"); 
    int j = colorModel.getColorSpace().getType();
    return (j != 5 && j != 6) ? false : (!(i == 2 || i > 4));
  }
  
  private static void enableRescaleOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, BufferedImage paramBufferedImage, RescaleOp paramRescaleOp) {
    float[] arrayOfFloat4;
    float[] arrayOfFloat3;
    ColorModel colorModel = paramBufferedImage.getColorModel();
    boolean bool = (colorModel.hasAlpha() && colorModel.isAlphaPremultiplied()) ? 1 : 0;
    int i = paramRescaleOp.getNumFactors();
    float[] arrayOfFloat1 = paramRescaleOp.getScaleFactors(null);
    float[] arrayOfFloat2 = paramRescaleOp.getOffsets(null);
    if (i == 1) {
      arrayOfFloat3 = new float[4];
      arrayOfFloat4 = new float[4];
      for (byte b = 0; b < 3; b++) {
        arrayOfFloat3[b] = arrayOfFloat1[0];
        arrayOfFloat4[b] = arrayOfFloat2[0];
      } 
      arrayOfFloat3[3] = 1.0F;
      arrayOfFloat4[3] = 0.0F;
    } else if (i == 3) {
      arrayOfFloat3 = new float[4];
      arrayOfFloat4 = new float[4];
      for (byte b = 0; b < 3; b++) {
        arrayOfFloat3[b] = arrayOfFloat1[b];
        arrayOfFloat4[b] = arrayOfFloat2[b];
      } 
      arrayOfFloat3[3] = 1.0F;
      arrayOfFloat4[3] = 0.0F;
    } else {
      arrayOfFloat3 = arrayOfFloat1;
      arrayOfFloat4 = arrayOfFloat2;
    } 
    if (colorModel.getNumComponents() == 1) {
      int j = colorModel.getComponentSize(0);
      int k = (1 << j) - 1;
      for (byte b = 0; b < 3; b++)
        arrayOfFloat4[b] = arrayOfFloat4[b] / k; 
    } else {
      for (byte b = 0; b < colorModel.getNumComponents(); b++) {
        int j = colorModel.getComponentSize(b);
        int k = (1 << j) - 1;
        arrayOfFloat4[b] = arrayOfFloat4[b] / k;
      } 
    } 
    byte b1 = 4;
    byte b2 = 16 + 4 * b1 * 2;
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(b2, 4);
    renderBuffer.putInt(122);
    renderBuffer.putLong(paramSurfaceData.getNativeOps());
    renderBuffer.putInt(bool ? 1 : 0);
    renderBuffer.put(arrayOfFloat3);
    renderBuffer.put(arrayOfFloat4);
  }
  
  private static void disableRescaleOp(RenderQueue paramRenderQueue) {
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacity(4);
    renderBuffer.putInt(123);
  }
  
  public static boolean isLookupOpValid(LookupOp paramLookupOp, BufferedImage paramBufferedImage) {
    LookupTable lookupTable = paramLookupOp.getTable();
    int i = lookupTable.getNumComponents();
    ColorModel colorModel = paramBufferedImage.getColorModel();
    if (colorModel instanceof java.awt.image.IndexColorModel)
      throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image"); 
    if (i != 1 && i != colorModel.getNumComponents() && i != colorModel.getNumColorComponents())
      throw new IllegalArgumentException("Number of arrays in the  lookup table (" + i + ") is not compatible with the src image: " + paramBufferedImage); 
    int j = colorModel.getColorSpace().getType();
    if (j != 5 && j != 6)
      return false; 
    if (i == 2 || i > 4)
      return false; 
    if (lookupTable instanceof ByteLookupTable) {
      byte[][] arrayOfByte = ((ByteLookupTable)lookupTable).getTable();
      for (byte b = 1; b < arrayOfByte.length; b++) {
        if (arrayOfByte[b].length > 256 || arrayOfByte[b].length != arrayOfByte[b - true].length)
          return false; 
      } 
    } else if (lookupTable instanceof ShortLookupTable) {
      short[][] arrayOfShort = ((ShortLookupTable)lookupTable).getTable();
      for (byte b = 1; b < arrayOfShort.length; b++) {
        if (arrayOfShort[b].length > 256 || arrayOfShort[b].length != arrayOfShort[b - true].length)
          return false; 
      } 
    } else {
      return false;
    } 
    return true;
  }
  
  private static void enableLookupOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, BufferedImage paramBufferedImage, LookupOp paramLookupOp) {
    boolean bool2;
    int m;
    int k;
    boolean bool1 = (paramBufferedImage.getColorModel().hasAlpha() && paramBufferedImage.isAlphaPremultiplied()) ? 1 : 0;
    LookupTable lookupTable = paramLookupOp.getTable();
    int i = lookupTable.getNumComponents();
    int j = lookupTable.getOffset();
    if (lookupTable instanceof ShortLookupTable) {
      short[][] arrayOfShort = ((ShortLookupTable)lookupTable).getTable();
      k = arrayOfShort[0].length;
      m = 2;
      bool2 = true;
    } else {
      byte[][] arrayOfByte = ((ByteLookupTable)lookupTable).getTable();
      k = arrayOfByte[0].length;
      m = 1;
      bool2 = false;
    } 
    int n = i * k * m;
    int i1 = n + 3 & 0xFFFFFFFC;
    int i2 = i1 - n;
    int i3 = 32 + i1;
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(i3, 4);
    renderBuffer.putInt(124);
    renderBuffer.putLong(paramSurfaceData.getNativeOps());
    renderBuffer.putInt(bool1 ? 1 : 0);
    renderBuffer.putInt(bool2 ? 1 : 0);
    renderBuffer.putInt(i);
    renderBuffer.putInt(k);
    renderBuffer.putInt(j);
    if (bool2) {
      short[][] arrayOfShort = ((ShortLookupTable)lookupTable).getTable();
      for (byte b = 0; b < i; b++)
        renderBuffer.put(arrayOfShort[b]); 
    } else {
      byte[][] arrayOfByte = ((ByteLookupTable)lookupTable).getTable();
      for (byte b = 0; b < i; b++)
        renderBuffer.put(arrayOfByte[b]); 
    } 
    if (i2 != 0)
      renderBuffer.position((renderBuffer.position() + i2)); 
  }
  
  private static void disableLookupOp(RenderQueue paramRenderQueue) {
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacity(4);
    renderBuffer.putInt(125);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\BufferedBufImgOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */