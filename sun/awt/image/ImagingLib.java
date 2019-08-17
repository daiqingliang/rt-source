package sun.awt.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ImagingLib {
  static boolean useLib = true;
  
  static boolean verbose = false;
  
  private static final int NUM_NATIVE_OPS = 3;
  
  private static final int LOOKUP_OP = 0;
  
  private static final int AFFINE_OP = 1;
  
  private static final int CONVOLVE_OP = 2;
  
  private static Class[] nativeOpClass = new Class[3];
  
  private static native boolean init();
  
  public static native int transformBI(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, double[] paramArrayOfDouble, int paramInt);
  
  public static native int transformRaster(Raster paramRaster1, Raster paramRaster2, double[] paramArrayOfDouble, int paramInt);
  
  public static native int convolveBI(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, Kernel paramKernel, int paramInt);
  
  public static native int convolveRaster(Raster paramRaster1, Raster paramRaster2, Kernel paramKernel, int paramInt);
  
  public static native int lookupByteBI(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, byte[][] paramArrayOfByte);
  
  public static native int lookupByteRaster(Raster paramRaster1, Raster paramRaster2, byte[][] paramArrayOfByte);
  
  private static int getNativeOpIndex(Class paramClass) {
    byte b1 = -1;
    for (byte b2 = 0; b2 < 3; b2++) {
      if (paramClass == nativeOpClass[b2]) {
        b1 = b2;
        break;
      } 
    } 
    return b1;
  }
  
  public static WritableRaster filter(RasterOp paramRasterOp, Raster paramRaster, WritableRaster paramWritableRaster) {
    ConvolveOp convolveOp;
    double[] arrayOfDouble;
    AffineTransformOp affineTransformOp;
    LookupTable lookupTable;
    if (!useLib)
      return null; 
    if (paramWritableRaster == null)
      paramWritableRaster = paramRasterOp.createCompatibleDestRaster(paramRaster); 
    WritableRaster writableRaster = null;
    switch (getNativeOpIndex(paramRasterOp.getClass())) {
      case 0:
        lookupTable = ((LookupOp)paramRasterOp).getTable();
        if (lookupTable.getOffset() != 0)
          return null; 
        if (lookupTable instanceof ByteLookupTable) {
          ByteLookupTable byteLookupTable = (ByteLookupTable)lookupTable;
          if (lookupByteRaster(paramRaster, paramWritableRaster, byteLookupTable.getTable()) > 0)
            writableRaster = paramWritableRaster; 
        } 
        break;
      case 1:
        affineTransformOp = (AffineTransformOp)paramRasterOp;
        arrayOfDouble = new double[6];
        affineTransformOp.getTransform().getMatrix(arrayOfDouble);
        if (transformRaster(paramRaster, paramWritableRaster, arrayOfDouble, affineTransformOp.getInterpolationType()) > 0)
          writableRaster = paramWritableRaster; 
        break;
      case 2:
        convolveOp = (ConvolveOp)paramRasterOp;
        if (convolveRaster(paramRaster, paramWritableRaster, convolveOp.getKernel(), convolveOp.getEdgeCondition()) > 0)
          writableRaster = paramWritableRaster; 
        break;
    } 
    if (writableRaster != null)
      SunWritableRaster.markDirty(writableRaster); 
    return writableRaster;
  }
  
  public static BufferedImage filter(BufferedImageOp paramBufferedImageOp, BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    ConvolveOp convolveOp;
    AffineTransform affineTransform;
    double[] arrayOfDouble;
    AffineTransformOp affineTransformOp;
    LookupTable lookupTable;
    if (verbose)
      System.out.println("in filter and op is " + paramBufferedImageOp + "bufimage is " + paramBufferedImage1 + " and " + paramBufferedImage2); 
    if (!useLib)
      return null; 
    if (paramBufferedImage2 == null)
      paramBufferedImage2 = paramBufferedImageOp.createCompatibleDestImage(paramBufferedImage1, null); 
    BufferedImage bufferedImage = null;
    switch (getNativeOpIndex(paramBufferedImageOp.getClass())) {
      case 0:
        lookupTable = ((LookupOp)paramBufferedImageOp).getTable();
        if (lookupTable.getOffset() != 0)
          return null; 
        if (lookupTable instanceof ByteLookupTable) {
          ByteLookupTable byteLookupTable = (ByteLookupTable)lookupTable;
          if (lookupByteBI(paramBufferedImage1, paramBufferedImage2, byteLookupTable.getTable()) > 0)
            bufferedImage = paramBufferedImage2; 
        } 
        break;
      case 1:
        affineTransformOp = (AffineTransformOp)paramBufferedImageOp;
        arrayOfDouble = new double[6];
        affineTransform = affineTransformOp.getTransform();
        affineTransformOp.getTransform().getMatrix(arrayOfDouble);
        if (transformBI(paramBufferedImage1, paramBufferedImage2, arrayOfDouble, affineTransformOp.getInterpolationType()) > 0)
          bufferedImage = paramBufferedImage2; 
        break;
      case 2:
        convolveOp = (ConvolveOp)paramBufferedImageOp;
        if (convolveBI(paramBufferedImage1, paramBufferedImage2, convolveOp.getKernel(), convolveOp.getEdgeCondition()) > 0)
          bufferedImage = paramBufferedImage2; 
        break;
    } 
    if (bufferedImage != null)
      SunWritableRaster.markDirty(bufferedImage); 
    return bufferedImage;
  }
  
  static  {
    PrivilegedAction<Boolean> privilegedAction = new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str = System.getProperty("os.arch");
          if (str == null || !str.startsWith("sparc"))
            try {
              System.loadLibrary("mlib_image");
            } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
              return Boolean.FALSE;
            }  
          boolean bool = ImagingLib.init();
          return Boolean.valueOf(bool);
        }
      };
    useLib = ((Boolean)AccessController.doPrivileged(privilegedAction)).booleanValue();
    try {
      nativeOpClass[0] = Class.forName("java.awt.image.LookupOp");
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("Could not find class: " + classNotFoundException);
    } 
    try {
      nativeOpClass[1] = Class.forName("java.awt.image.AffineTransformOp");
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("Could not find class: " + classNotFoundException);
    } 
    try {
      nativeOpClass[2] = Class.forName("java.awt.image.ConvolveOp");
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("Could not find class: " + classNotFoundException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImagingLib.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */