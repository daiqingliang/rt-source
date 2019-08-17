package sun.java2d.pipe;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformHelper;

public class DrawImage implements DrawImagePipe {
  private static final double MAX_TX_ERROR = 1.0E-4D;
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor) {
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (isSimpleTranslate(paramSunGraphics2D))
      return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, 0, 0, i, j); 
    AffineTransform affineTransform = paramSunGraphics2D.transform;
    if ((paramInt1 | paramInt2) != 0) {
      affineTransform = new AffineTransform(affineTransform);
      affineTransform.translate(paramInt1, paramInt2);
    } 
    transformImage(paramSunGraphics2D, paramImage, affineTransform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramColor);
    return true;
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor) {
    if (isSimpleTranslate(paramSunGraphics2D))
      return renderImageCopy(paramSunGraphics2D, paramImage, paramColor, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6); 
    scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor);
    return true;
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) {
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (paramInt3 > 0 && paramInt4 > 0 && isSimpleTranslate(paramSunGraphics2D)) {
      double d1 = (paramInt1 + paramSunGraphics2D.transX);
      double d2 = (paramInt2 + paramSunGraphics2D.transY);
      double d3 = d1 + paramInt3;
      double d4 = d2 + paramInt4;
      if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramSunGraphics2D.interpolationType, 0, 0, i, j, d1, d2, d3, d4))
        return true; 
    } 
    AffineTransform affineTransform = paramSunGraphics2D.transform;
    if ((paramInt1 | paramInt2) != 0 || paramInt3 != i || paramInt4 != j) {
      affineTransform = new AffineTransform(affineTransform);
      affineTransform.translate(paramInt1, paramInt2);
      affineTransform.scale(paramInt3 / i, paramInt4 / j);
    } 
    transformImage(paramSunGraphics2D, paramImage, affineTransform, paramSunGraphics2D.interpolationType, 0, 0, i, j, paramColor);
    return true;
  }
  
  protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, AffineTransform paramAffineTransform, int paramInt3) {
    boolean bool;
    int i = paramAffineTransform.getType();
    int j = paramImage.getWidth(null);
    int k = paramImage.getHeight(null);
    if (paramSunGraphics2D.transformState <= 2 && (i == 0 || i == 1)) {
      double d1 = paramAffineTransform.getTranslateX();
      double d2 = paramAffineTransform.getTranslateY();
      d1 += paramSunGraphics2D.transform.getTranslateX();
      d2 += paramSunGraphics2D.transform.getTranslateY();
      int m = (int)Math.floor(d1 + 0.5D);
      int n = (int)Math.floor(d2 + 0.5D);
      if (paramInt3 == 1 || (closeToInteger(m, d1) && closeToInteger(n, d2))) {
        renderImageCopy(paramSunGraphics2D, paramImage, null, paramInt1 + m, paramInt2 + n, 0, 0, j, k);
        return;
      } 
      bool = false;
    } else if (paramSunGraphics2D.transformState <= 3 && (i & 0x78) == 0) {
      double[] arrayOfDouble = { 0.0D, 0.0D, j, k };
      paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
      arrayOfDouble[0] = arrayOfDouble[0] + paramInt1;
      arrayOfDouble[1] = arrayOfDouble[1] + paramInt2;
      arrayOfDouble[2] = arrayOfDouble[2] + paramInt1;
      arrayOfDouble[3] = arrayOfDouble[3] + paramInt2;
      paramSunGraphics2D.transform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 2);
      if (tryCopyOrScale(paramSunGraphics2D, paramImage, 0, 0, j, k, null, paramInt3, arrayOfDouble))
        return; 
      bool = false;
    } else {
      bool = true;
    } 
    AffineTransform affineTransform = new AffineTransform(paramSunGraphics2D.transform);
    affineTransform.translate(paramInt1, paramInt2);
    affineTransform.concatenate(paramAffineTransform);
    if (bool) {
      transformImage(paramSunGraphics2D, paramImage, affineTransform, paramInt3, 0, 0, j, k, null);
    } else {
      renderImageXform(paramSunGraphics2D, paramImage, affineTransform, paramInt3, 0, 0, j, k, null);
    } 
  }
  
  protected void transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor) {
    double[] arrayOfDouble = new double[6];
    arrayOfDouble[2] = (paramInt4 - paramInt2);
    arrayOfDouble[5] = (paramInt5 - paramInt3);
    arrayOfDouble[3] = (paramInt5 - paramInt3);
    paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 3);
    if (Math.abs(arrayOfDouble[0] - arrayOfDouble[4]) < 1.0E-4D && Math.abs(arrayOfDouble[3] - arrayOfDouble[5]) < 1.0E-4D && tryCopyOrScale(paramSunGraphics2D, paramImage, paramInt2, paramInt3, paramInt4, paramInt5, paramColor, paramInt1, arrayOfDouble))
      return; 
    renderImageXform(paramSunGraphics2D, paramImage, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramColor);
  }
  
  protected boolean tryCopyOrScale(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, int paramInt5, double[] paramArrayOfDouble) {
    double d1 = paramArrayOfDouble[0];
    double d2 = paramArrayOfDouble[1];
    double d3 = paramArrayOfDouble[2];
    double d4 = paramArrayOfDouble[3];
    double d5 = d3 - d1;
    double d6 = d4 - d2;
    if (d1 < -2.147483648E9D || d1 > 2.147483647E9D || d2 < -2.147483648E9D || d2 > 2.147483647E9D || d3 < -2.147483648E9D || d3 > 2.147483647E9D || d4 < -2.147483648E9D || d4 > 2.147483647E9D)
      return false; 
    if (closeToInteger(paramInt3 - paramInt1, d5) && closeToInteger(paramInt4 - paramInt2, d6)) {
      int i = (int)Math.floor(d1 + 0.5D);
      int j = (int)Math.floor(d2 + 0.5D);
      if (paramInt5 == 1 || (closeToInteger(i, d1) && closeToInteger(j, d2))) {
        renderImageCopy(paramSunGraphics2D, paramImage, paramColor, i, j, paramInt1, paramInt2, paramInt3 - paramInt1, paramInt4 - paramInt2);
        return true;
      } 
    } 
    return (d5 > 0.0D && d6 > 0.0D && renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramInt5, paramInt1, paramInt2, paramInt3, paramInt4, d1, d2, d3, d4));
  }
  
  BufferedImage makeBufferedImage(Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    int i = paramInt4 - paramInt2;
    int j = paramInt5 - paramInt3;
    BufferedImage bufferedImage = new BufferedImage(i, j, paramInt1);
    SunGraphics2D sunGraphics2D = (SunGraphics2D)bufferedImage.createGraphics();
    sunGraphics2D.setComposite(AlphaComposite.Src);
    bufferedImage.setAccelerationPriority(0.0F);
    if (paramColor != null) {
      sunGraphics2D.setColor(paramColor);
      sunGraphics2D.fillRect(0, 0, i, j);
      sunGraphics2D.setComposite(AlphaComposite.SrcOver);
    } 
    sunGraphics2D.copyImage(paramImage, 0, 0, paramInt2, paramInt3, i, j, null, null);
    sunGraphics2D.dispose();
    return bufferedImage;
  }
  
  protected void renderImageXform(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Color paramColor) {
    AffineTransform affineTransform;
    try {
      affineTransform = paramAffineTransform.createInverse();
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      return;
    } 
    double[] arrayOfDouble = new double[8];
    arrayOfDouble[6] = (paramInt4 - paramInt2);
    arrayOfDouble[2] = (paramInt4 - paramInt2);
    arrayOfDouble[7] = (paramInt5 - paramInt3);
    arrayOfDouble[5] = (paramInt5 - paramInt3);
    paramAffineTransform.transform(arrayOfDouble, 0, arrayOfDouble, 0, 4);
    double d3 = arrayOfDouble[0];
    double d1 = d3;
    double d4 = arrayOfDouble[1];
    double d2 = d4;
    for (byte b = 2; b < arrayOfDouble.length; b += 2) {
      double d = arrayOfDouble[b];
      if (d1 > d) {
        d1 = d;
      } else if (d3 < d) {
        d3 = d;
      } 
      d = arrayOfDouble[b + 1];
      if (d2 > d) {
        d2 = d;
      } else if (d4 < d) {
        d4 = d;
      } 
    } 
    Region region1 = paramSunGraphics2D.getCompClip();
    int i = Math.max((int)Math.floor(d1), region1.lox);
    int j = Math.max((int)Math.floor(d2), region1.loy);
    int k = Math.min((int)Math.ceil(d3), region1.hix);
    int m = Math.min((int)Math.ceil(d4), region1.hiy);
    if (k <= i || m <= j)
      return; 
    SurfaceData surfaceData1 = paramSunGraphics2D.surfaceData;
    SurfaceData surfaceData2 = surfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
    if (surfaceData2 == null) {
      paramImage = getBufferedImage(paramImage);
      surfaceData2 = surfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
      if (surfaceData2 == null)
        return; 
    } 
    if (isBgOperation(surfaceData2, paramColor)) {
      paramImage = makeBufferedImage(paramImage, paramColor, 1, paramInt2, paramInt3, paramInt4, paramInt5);
      paramInt4 -= paramInt2;
      paramInt5 -= paramInt3;
      paramInt2 = paramInt3 = 0;
      surfaceData2 = surfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, paramColor);
    } 
    SurfaceType surfaceType1 = surfaceData2.getSurfaceType();
    TransformHelper transformHelper = TransformHelper.getFromCache(surfaceType1);
    if (transformHelper == null) {
      byte b1 = (surfaceData2.getTransparency() == 1) ? 1 : 2;
      paramImage = makeBufferedImage(paramImage, null, b1, paramInt2, paramInt3, paramInt4, paramInt5);
      paramInt4 -= paramInt2;
      paramInt5 -= paramInt3;
      paramInt2 = paramInt3 = 0;
      surfaceData2 = surfaceData1.getSourceSurfaceData(paramImage, 4, paramSunGraphics2D.imageComp, null);
      surfaceType1 = surfaceData2.getSurfaceType();
      transformHelper = TransformHelper.getFromCache(surfaceType1);
    } 
    SurfaceType surfaceType2 = surfaceData1.getSurfaceType();
    if (paramSunGraphics2D.compositeState <= 1) {
      MaskBlit maskBlit1 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, paramSunGraphics2D.imageComp, surfaceType2);
      if (maskBlit1.getNativePrim() != 0L) {
        transformHelper.Transform(maskBlit1, surfaceData2, surfaceData1, paramSunGraphics2D.composite, region1, affineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, i, j, k, m, null, 0, 0);
        return;
      } 
    } 
    int n = k - i;
    int i1 = m - j;
    BufferedImage bufferedImage = new BufferedImage(n, i1, 3);
    SurfaceData surfaceData3 = SurfaceData.getPrimarySurfaceData(bufferedImage);
    SurfaceType surfaceType3 = surfaceData3.getSurfaceType();
    MaskBlit maskBlit = MaskBlit.getFromCache(SurfaceType.IntArgbPre, CompositeType.SrcNoEa, surfaceType3);
    int[] arrayOfInt = new int[i1 * 2 + 2];
    transformHelper.Transform(maskBlit, surfaceData2, surfaceData3, AlphaComposite.Src, null, affineTransform, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, n, i1, arrayOfInt, i, j);
    Region region2 = Region.getInstance(i, j, k, m, arrayOfInt);
    region1 = region1.getIntersection(region2);
    Blit blit = Blit.getFromCache(surfaceType3, paramSunGraphics2D.imageComp, surfaceType2);
    blit.Blit(surfaceData3, surfaceData1, paramSunGraphics2D.composite, region1, 0, 0, i, j, n, i1);
  }
  
  protected boolean renderImageCopy(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Region region = paramSunGraphics2D.getCompClip();
    SurfaceData surfaceData = paramSunGraphics2D.surfaceData;
    byte b = 0;
    while (true) {
      SurfaceData surfaceData1 = surfaceData.getSourceSurfaceData(paramImage, 0, paramSunGraphics2D.imageComp, paramColor);
      if (surfaceData1 == null)
        return false; 
      try {
        SurfaceType surfaceType1 = surfaceData1.getSurfaceType();
        SurfaceType surfaceType2 = surfaceData.getSurfaceType();
        blitSurfaceData(paramSunGraphics2D, region, surfaceData1, surfaceData, surfaceType1, surfaceType2, paramInt3, paramInt4, paramInt1, paramInt2, paramInt5, paramInt6, paramColor);
        return true;
      } catch (NullPointerException nullPointerException) {
        if (!SurfaceData.isNull(surfaceData) && !SurfaceData.isNull(surfaceData1))
          throw nullPointerException; 
        return false;
      } catch (InvalidPipeException invalidPipeException) {
        b++;
        region = paramSunGraphics2D.getCompClip();
        if ((surfaceData = paramSunGraphics2D.surfaceData).isNull(surfaceData) || SurfaceData.isNull(surfaceData1) || b > 1)
          break; 
      } 
    } 
    return false;
  }
  
  protected boolean renderImageScale(SunGraphics2D paramSunGraphics2D, Image paramImage, Color paramColor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramInt1 != 1)
      return false; 
    Region region = paramSunGraphics2D.getCompClip();
    SurfaceData surfaceData = paramSunGraphics2D.surfaceData;
    byte b = 0;
    while (true) {
      SurfaceData surfaceData1 = surfaceData.getSourceSurfaceData(paramImage, 3, paramSunGraphics2D.imageComp, paramColor);
      if (surfaceData1 == null || isBgOperation(surfaceData1, paramColor))
        return false; 
      try {
        SurfaceType surfaceType1 = surfaceData1.getSurfaceType();
        SurfaceType surfaceType2 = surfaceData.getSurfaceType();
        return scaleSurfaceData(paramSunGraphics2D, region, surfaceData1, surfaceData, surfaceType1, surfaceType2, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
      } catch (NullPointerException nullPointerException) {
        if (!SurfaceData.isNull(surfaceData))
          throw nullPointerException; 
        return false;
      } catch (InvalidPipeException invalidPipeException) {
        b++;
        region = paramSunGraphics2D.getCompClip();
        if ((surfaceData = paramSunGraphics2D.surfaceData).isNull(surfaceData) || SurfaceData.isNull(surfaceData1) || b > 1)
          break; 
      } 
    } 
    return false;
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor) {
    int i3;
    int i2;
    int i1;
    int n;
    int m;
    int k;
    int j;
    int i;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    if (paramInt7 > paramInt5) {
      i = paramInt7 - paramInt5;
      n = paramInt5;
    } else {
      bool1 = true;
      i = paramInt5 - paramInt7;
      n = paramInt7;
    } 
    if (paramInt8 > paramInt6) {
      j = paramInt8 - paramInt6;
      i1 = paramInt6;
    } else {
      bool2 = true;
      j = paramInt6 - paramInt8;
      i1 = paramInt8;
    } 
    if (paramInt3 > paramInt1) {
      k = paramInt3 - paramInt1;
      i2 = paramInt1;
    } else {
      k = paramInt1 - paramInt3;
      bool3 = true;
      i2 = paramInt3;
    } 
    if (paramInt4 > paramInt2) {
      m = paramInt4 - paramInt2;
      i3 = paramInt2;
    } else {
      m = paramInt2 - paramInt4;
      bool4 = true;
      i3 = paramInt4;
    } 
    if (i <= 0 || j <= 0)
      return true; 
    if (bool1 == bool3 && bool2 == bool4 && isSimpleTranslate(paramSunGraphics2D)) {
      double d3 = (i2 + paramSunGraphics2D.transX);
      double d4 = (i3 + paramSunGraphics2D.transY);
      double d5 = d3 + k;
      double d6 = d4 + m;
      if (renderImageScale(paramSunGraphics2D, paramImage, paramColor, paramSunGraphics2D.interpolationType, n, i1, n + i, i1 + j, d3, d4, d5, d6))
        return true; 
    } 
    AffineTransform affineTransform = new AffineTransform(paramSunGraphics2D.transform);
    affineTransform.translate(paramInt1, paramInt2);
    double d1 = (paramInt3 - paramInt1) / (paramInt7 - paramInt5);
    double d2 = (paramInt4 - paramInt2) / (paramInt8 - paramInt6);
    affineTransform.scale(d1, d2);
    affineTransform.translate((n - paramInt5), (i1 - paramInt6));
    int i4 = SurfaceManager.getImageScale(paramImage);
    int i5 = paramImage.getWidth(null) * i4;
    int i6 = paramImage.getHeight(null) * i4;
    i += n;
    j += i1;
    if (i > i5)
      i = i5; 
    if (j > i6)
      j = i6; 
    if (n < 0) {
      affineTransform.translate(-n, 0.0D);
      n = 0;
    } 
    if (i1 < 0) {
      affineTransform.translate(0.0D, -i1);
      i1 = 0;
    } 
    if (n >= i || i1 >= j)
      return true; 
    transformImage(paramSunGraphics2D, paramImage, affineTransform, paramSunGraphics2D.interpolationType, n, i1, i, j, paramColor);
    return true;
  }
  
  public static boolean closeToInteger(int paramInt, double paramDouble) { return (Math.abs(paramDouble - paramInt) < 1.0E-4D); }
  
  public static boolean isSimpleTranslate(SunGraphics2D paramSunGraphics2D) {
    int i = paramSunGraphics2D.transformState;
    return (i <= 1) ? true : ((i >= 3) ? false : ((paramSunGraphics2D.interpolationType == 1)));
  }
  
  protected static boolean isBgOperation(SurfaceData paramSurfaceData, Color paramColor) { return (paramSurfaceData == null || (paramColor != null && paramSurfaceData.getTransparency() != 1)); }
  
  protected BufferedImage getBufferedImage(Image paramImage) { return (paramImage instanceof BufferedImage) ? (BufferedImage)paramImage : ((VolatileImage)paramImage).getSnapshot(); }
  
  private ColorModel getTransformColorModel(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, AffineTransform paramAffineTransform) {
    ColorModel colorModel1 = paramBufferedImage.getColorModel();
    ColorModel colorModel2 = colorModel1;
    if (paramAffineTransform.isIdentity())
      return colorModel2; 
    int i = paramAffineTransform.getType();
    boolean bool = ((i & 0x38) != 0) ? 1 : 0;
    if (!bool && i != 1 && i != 0) {
      double[] arrayOfDouble = new double[4];
      paramAffineTransform.getMatrix(arrayOfDouble);
      bool = (arrayOfDouble[0] != (int)arrayOfDouble[0] || arrayOfDouble[3] != (int)arrayOfDouble[3]) ? 1 : 0;
    } 
    if (paramSunGraphics2D.renderHint != 2) {
      if (colorModel1 instanceof IndexColorModel) {
        WritableRaster writableRaster = paramBufferedImage.getRaster();
        IndexColorModel indexColorModel = (IndexColorModel)colorModel1;
        if (bool && colorModel1.getTransparency() == 1)
          if (writableRaster instanceof sun.awt.image.BytePackedRaster) {
            colorModel2 = ColorModel.getRGBdefault();
          } else {
            double[] arrayOfDouble = new double[6];
            paramAffineTransform.getMatrix(arrayOfDouble);
            if (arrayOfDouble[1] != 0.0D || arrayOfDouble[2] != 0.0D || arrayOfDouble[4] != 0.0D || arrayOfDouble[5] != 0.0D) {
              int j = indexColorModel.getMapSize();
              if (j < 256) {
                int[] arrayOfInt = new int[j + 1];
                indexColorModel.getRGBs(arrayOfInt);
                arrayOfInt[j] = 0;
                colorModel2 = new IndexColorModel(indexColorModel.getPixelSize(), j + 1, arrayOfInt, 0, true, j, 0);
              } else {
                colorModel2 = ColorModel.getRGBdefault();
              } 
            } 
          }  
      } else if (bool && colorModel1.getTransparency() == 1) {
        colorModel2 = ColorModel.getRGBdefault();
      } 
    } else if (colorModel1 instanceof IndexColorModel || (bool && colorModel1.getTransparency() == 1)) {
      colorModel2 = ColorModel.getRGBdefault();
    } 
    return colorModel2;
  }
  
  protected void blitSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor) {
    if (paramInt5 <= 0 || paramInt6 <= 0)
      return; 
    CompositeType compositeType = paramSunGraphics2D.imageComp;
    if (CompositeType.SrcOverNoEa.equals(compositeType) && (paramSurfaceData1.getTransparency() == 1 || (paramColor != null && paramColor.getTransparency() == 1)))
      compositeType = CompositeType.SrcNoEa; 
    if (!isBgOperation(paramSurfaceData1, paramColor)) {
      Blit blit = Blit.getFromCache(paramSurfaceType1, compositeType, paramSurfaceType2);
      blit.Blit(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } else {
      BlitBg blitBg = BlitBg.getFromCache(paramSurfaceType1, compositeType, paramSurfaceType2);
      blitBg.BlitBg(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramColor.getRGB(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    } 
  }
  
  protected boolean scaleSurfaceData(SunGraphics2D paramSunGraphics2D, Region paramRegion, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    CompositeType compositeType = paramSunGraphics2D.imageComp;
    if (CompositeType.SrcOverNoEa.equals(compositeType) && paramSurfaceData1.getTransparency() == 1)
      compositeType = CompositeType.SrcNoEa; 
    ScaledBlit scaledBlit = ScaledBlit.getFromCache(paramSurfaceType1, compositeType, paramSurfaceType2);
    if (scaledBlit != null) {
      scaledBlit.Scale(paramSurfaceData1, paramSurfaceData2, paramSunGraphics2D.composite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
      return true;
    } 
    return false;
  }
  
  protected static boolean imageReady(ToolkitImage paramToolkitImage, ImageObserver paramImageObserver) {
    if (paramToolkitImage.hasError()) {
      if (paramImageObserver != null)
        paramImageObserver.imageUpdate(paramToolkitImage, 192, -1, -1, -1, -1); 
      return false;
    } 
    return true;
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    if (!(paramImage instanceof ToolkitImage))
      return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramColor); 
    ToolkitImage toolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(toolkitImage, paramImageObserver))
      return false; 
    ImageRepresentation imageRepresentation = toolkitImage.getImageRep();
    return imageRepresentation.drawToBufImage(paramSunGraphics2D, toolkitImage, paramInt1, paramInt2, paramColor, paramImageObserver);
  }
  
  public boolean copyImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Color paramColor, ImageObserver paramImageObserver) {
    if (!(paramImage instanceof ToolkitImage))
      return copyImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramColor); 
    ToolkitImage toolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(toolkitImage, paramImageObserver))
      return false; 
    ImageRepresentation imageRepresentation = toolkitImage.getImageRep();
    return imageRepresentation.drawToBufImage(paramSunGraphics2D, toolkitImage, paramInt1, paramInt2, paramInt1 + paramInt5, paramInt2 + paramInt6, paramInt3, paramInt4, paramInt3 + paramInt5, paramInt4 + paramInt6, paramColor, paramImageObserver);
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    if (!(paramImage instanceof ToolkitImage))
      return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor); 
    ToolkitImage toolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(toolkitImage, paramImageObserver))
      return false; 
    ImageRepresentation imageRepresentation = toolkitImage.getImageRep();
    return imageRepresentation.drawToBufImage(paramSunGraphics2D, toolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
  }
  
  public boolean scaleImage(SunGraphics2D paramSunGraphics2D, Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    if (!(paramImage instanceof ToolkitImage))
      return scaleImage(paramSunGraphics2D, paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor); 
    ToolkitImage toolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(toolkitImage, paramImageObserver))
      return false; 
    ImageRepresentation imageRepresentation = toolkitImage.getImageRep();
    return imageRepresentation.drawToBufImage(paramSunGraphics2D, toolkitImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
  }
  
  public boolean transformImage(SunGraphics2D paramSunGraphics2D, Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) {
    if (!(paramImage instanceof ToolkitImage)) {
      transformImage(paramSunGraphics2D, paramImage, 0, 0, paramAffineTransform, paramSunGraphics2D.interpolationType);
      return true;
    } 
    ToolkitImage toolkitImage = (ToolkitImage)paramImage;
    if (!imageReady(toolkitImage, paramImageObserver))
      return false; 
    ImageRepresentation imageRepresentation = toolkitImage.getImageRep();
    return imageRepresentation.drawToBufImage(paramSunGraphics2D, toolkitImage, paramAffineTransform, paramImageObserver);
  }
  
  public void transformImage(SunGraphics2D paramSunGraphics2D, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {
    if (paramBufferedImageOp != null) {
      if (paramBufferedImageOp instanceof AffineTransformOp) {
        AffineTransformOp affineTransformOp = (AffineTransformOp)paramBufferedImageOp;
        transformImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, affineTransformOp.getTransform(), affineTransformOp.getInterpolationType());
        return;
      } 
      paramBufferedImage = paramBufferedImageOp.filter(paramBufferedImage, null);
    } 
    copyImage(paramSunGraphics2D, paramBufferedImage, paramInt1, paramInt2, null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\DrawImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */