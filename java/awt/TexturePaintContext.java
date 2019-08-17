package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.ref.WeakReference;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.SunWritableRaster;

abstract class TexturePaintContext implements PaintContext {
  public static ColorModel xrgbmodel;
  
  public static ColorModel argbmodel = (xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255)).getRGBdefault();
  
  ColorModel colorModel;
  
  int bWidth;
  
  int bHeight;
  
  int maxWidth;
  
  WritableRaster outRas;
  
  double xOrg;
  
  double yOrg;
  
  double incXAcross;
  
  double incYAcross;
  
  double incXDown;
  
  double incYDown;
  
  int colincx;
  
  int colincy;
  
  int colincxerr;
  
  int colincyerr;
  
  int rowincx;
  
  int rowincy;
  
  int rowincxerr;
  
  int rowincyerr;
  
  private static WeakReference<Raster> xrgbRasRef;
  
  private static WeakReference<Raster> argbRasRef;
  
  private static WeakReference<Raster> byteRasRef;
  
  public static PaintContext getContext(BufferedImage paramBufferedImage, AffineTransform paramAffineTransform, RenderingHints paramRenderingHints, Rectangle paramRectangle) {
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    ColorModel colorModel1 = paramBufferedImage.getColorModel();
    int i = paramRectangle.width;
    Object object = paramRenderingHints.get(RenderingHints.KEY_INTERPOLATION);
    boolean bool = (object == null) ? ((paramRenderingHints.get(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY)) : ((object != RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
    if (writableRaster instanceof IntegerInterleavedRaster && (!bool || isFilterableDCM(colorModel1))) {
      IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)writableRaster;
      if (integerInterleavedRaster.getNumDataElements() == 1 && integerInterleavedRaster.getPixelStride() == 1)
        return new Int(integerInterleavedRaster, colorModel1, paramAffineTransform, i, bool); 
    } else if (writableRaster instanceof ByteInterleavedRaster) {
      ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)writableRaster;
      if (byteInterleavedRaster.getNumDataElements() == 1 && byteInterleavedRaster.getPixelStride() == 1)
        if (bool) {
          if (isFilterableICM(colorModel1))
            return new ByteFilter(byteInterleavedRaster, colorModel1, paramAffineTransform, i); 
        } else {
          return new Byte(byteInterleavedRaster, colorModel1, paramAffineTransform, i);
        }  
    } 
    return new Any(writableRaster, colorModel1, paramAffineTransform, i, bool);
  }
  
  public static boolean isFilterableICM(ColorModel paramColorModel) {
    if (paramColorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)paramColorModel;
      if (indexColorModel.getMapSize() <= 256)
        return true; 
    } 
    return false;
  }
  
  public static boolean isFilterableDCM(ColorModel paramColorModel) {
    if (paramColorModel instanceof DirectColorModel) {
      DirectColorModel directColorModel = (DirectColorModel)paramColorModel;
      return (isMaskOK(directColorModel.getAlphaMask(), true) && isMaskOK(directColorModel.getRedMask(), false) && isMaskOK(directColorModel.getGreenMask(), false) && isMaskOK(directColorModel.getBlueMask(), false));
    } 
    return false;
  }
  
  public static boolean isMaskOK(int paramInt, boolean paramBoolean) { return (paramBoolean && paramInt == 0) ? true : ((paramInt == 255 || paramInt == 65280 || paramInt == 16711680 || paramInt == -16777216)); }
  
  public static ColorModel getInternedColorModel(ColorModel paramColorModel) { return (xrgbmodel == paramColorModel || xrgbmodel.equals(paramColorModel)) ? xrgbmodel : ((argbmodel == paramColorModel || argbmodel.equals(paramColorModel)) ? argbmodel : paramColorModel); }
  
  TexturePaintContext(ColorModel paramColorModel, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3) {
    this.colorModel = getInternedColorModel(paramColorModel);
    this.bWidth = paramInt1;
    this.bHeight = paramInt2;
    this.maxWidth = paramInt3;
    try {
      paramAffineTransform = paramAffineTransform.createInverse();
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      paramAffineTransform.setToScale(0.0D, 0.0D);
    } 
    this.incXAcross = mod(paramAffineTransform.getScaleX(), paramInt1);
    this.incYAcross = mod(paramAffineTransform.getShearY(), paramInt2);
    this.incXDown = mod(paramAffineTransform.getShearX(), paramInt1);
    this.incYDown = mod(paramAffineTransform.getScaleY(), paramInt2);
    this.xOrg = paramAffineTransform.getTranslateX();
    this.yOrg = paramAffineTransform.getTranslateY();
    this.colincx = (int)this.incXAcross;
    this.colincy = (int)this.incYAcross;
    this.colincxerr = fractAsInt(this.incXAcross);
    this.colincyerr = fractAsInt(this.incYAcross);
    this.rowincx = (int)this.incXDown;
    this.rowincy = (int)this.incYDown;
    this.rowincxerr = fractAsInt(this.incXDown);
    this.rowincyerr = fractAsInt(this.incYDown);
  }
  
  static int fractAsInt(double paramDouble) { return (int)(paramDouble % 1.0D * 2.147483647E9D); }
  
  static double mod(double paramDouble1, double paramDouble2) {
    paramDouble1 %= paramDouble2;
    if (paramDouble1 < 0.0D) {
      paramDouble1 += paramDouble2;
      if (paramDouble1 >= paramDouble2)
        paramDouble1 = 0.0D; 
    } 
    return paramDouble1;
  }
  
  public void dispose() { dropRaster(this.colorModel, this.outRas); }
  
  public ColorModel getColorModel() { return this.colorModel; }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.outRas == null || this.outRas.getWidth() < paramInt3 || this.outRas.getHeight() < paramInt4)
      this.outRas = makeRaster((paramInt4 == 1) ? Math.max(paramInt3, this.maxWidth) : paramInt3, paramInt4); 
    double d1 = mod(this.xOrg + paramInt1 * this.incXAcross + paramInt2 * this.incXDown, this.bWidth);
    double d2 = mod(this.yOrg + paramInt1 * this.incYAcross + paramInt2 * this.incYDown, this.bHeight);
    setRaster((int)d1, (int)d2, fractAsInt(d1), fractAsInt(d2), paramInt3, paramInt4, this.bWidth, this.bHeight, this.colincx, this.colincxerr, this.colincy, this.colincyerr, this.rowincx, this.rowincxerr, this.rowincy, this.rowincyerr);
    SunWritableRaster.markDirty(this.outRas);
    return this.outRas;
  }
  
  static WritableRaster makeRaster(ColorModel paramColorModel, Raster paramRaster, int paramInt1, int paramInt2) {
    if (xrgbmodel == paramColorModel) {
      if (xrgbRasRef != null) {
        WritableRaster writableRaster = (WritableRaster)xrgbRasRef.get();
        if (writableRaster != null && writableRaster.getWidth() >= paramInt1 && writableRaster.getHeight() >= paramInt2) {
          xrgbRasRef = null;
          return writableRaster;
        } 
      } 
      if (paramInt1 <= 32 && paramInt2 <= 32)
        paramInt1 = paramInt2 = 32; 
    } else if (argbmodel == paramColorModel) {
      if (argbRasRef != null) {
        WritableRaster writableRaster = (WritableRaster)argbRasRef.get();
        if (writableRaster != null && writableRaster.getWidth() >= paramInt1 && writableRaster.getHeight() >= paramInt2) {
          argbRasRef = null;
          return writableRaster;
        } 
      } 
      if (paramInt1 <= 32 && paramInt2 <= 32)
        paramInt1 = paramInt2 = 32; 
    } 
    return (paramRaster != null) ? paramRaster.createCompatibleWritableRaster(paramInt1, paramInt2) : paramColorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
  }
  
  static void dropRaster(ColorModel paramColorModel, Raster paramRaster) {
    if (paramRaster == null)
      return; 
    if (xrgbmodel == paramColorModel) {
      xrgbRasRef = new WeakReference(paramRaster);
    } else if (argbmodel == paramColorModel) {
      argbRasRef = new WeakReference(paramRaster);
    } 
  }
  
  static WritableRaster makeByteRaster(Raster paramRaster, int paramInt1, int paramInt2) {
    if (byteRasRef != null) {
      WritableRaster writableRaster = (WritableRaster)byteRasRef.get();
      if (writableRaster != null && writableRaster.getWidth() >= paramInt1 && writableRaster.getHeight() >= paramInt2) {
        byteRasRef = null;
        return writableRaster;
      } 
    } 
    if (paramInt1 <= 32 && paramInt2 <= 32)
      paramInt1 = paramInt2 = 32; 
    return paramRaster.createCompatibleWritableRaster(paramInt1, paramInt2);
  }
  
  static void dropByteRaster(Raster paramRaster) {
    if (paramRaster == null)
      return; 
    byteRasRef = new WeakReference(paramRaster);
  }
  
  public abstract WritableRaster makeRaster(int paramInt1, int paramInt2);
  
  public abstract void setRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14, int paramInt15, int paramInt16);
  
  public static int blend(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    paramInt1 >>>= 19;
    paramInt2 >>>= 19;
    int m = 0;
    int k = m;
    int j = k;
    int i = j;
    for (byte b = 0; b < 4; b++) {
      int n = paramArrayOfInt[b];
      paramInt1 = 4096 - paramInt1;
      if (!(b & true))
        paramInt2 = 4096 - paramInt2; 
      int i1 = paramInt1 * paramInt2;
      if (i1 != 0) {
        i += (n >>> 24) * i1;
        j += (n >>> 16 & 0xFF) * i1;
        k += (n >>> 8 & 0xFF) * i1;
        m += (n & 0xFF) * i1;
      } 
    } 
    return i + 8388608 >>> 24 << 24 | j + 8388608 >>> 24 << 16 | k + 8388608 >>> 24 << 8 | m + 8388608 >>> 24;
  }
  
  static class Any extends TexturePaintContext {
    WritableRaster srcRas;
    
    boolean filter;
    
    public Any(WritableRaster param1WritableRaster, ColorModel param1ColorModel, AffineTransform param1AffineTransform, int param1Int, boolean param1Boolean) {
      super(param1ColorModel, param1AffineTransform, param1WritableRaster.getWidth(), param1WritableRaster.getHeight(), param1Int);
      this.srcRas = param1WritableRaster;
      this.filter = param1Boolean;
    }
    
    public WritableRaster makeRaster(int param1Int1, int param1Int2) { return makeRaster(this.colorModel, this.srcRas, param1Int1, param1Int2); }
    
    public void setRaster(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int param1Int10, int param1Int11, int param1Int12, int param1Int13, int param1Int14, int param1Int15, int param1Int16) {
      Object object = null;
      int i = param1Int1;
      int j = param1Int2;
      int k = param1Int3;
      int m = param1Int4;
      WritableRaster writableRaster1 = this.srcRas;
      WritableRaster writableRaster2 = this.outRas;
      int[] arrayOfInt = this.filter ? new int[4] : null;
      for (byte b = 0; b < param1Int6; b++) {
        param1Int1 = i;
        param1Int2 = j;
        param1Int3 = k;
        param1Int4 = m;
        for (byte b1 = 0; b1 < param1Int5; b1++) {
          object = writableRaster1.getDataElements(param1Int1, param1Int2, object);
          if (this.filter) {
            int n;
            if ((n = param1Int1 + 1) >= param1Int7)
              n = 0; 
            int i1;
            if ((i1 = param1Int2 + 1) >= param1Int8)
              i1 = 0; 
            arrayOfInt[0] = this.colorModel.getRGB(object);
            object = writableRaster1.getDataElements(n, param1Int2, object);
            arrayOfInt[1] = this.colorModel.getRGB(object);
            object = writableRaster1.getDataElements(param1Int1, i1, object);
            arrayOfInt[2] = this.colorModel.getRGB(object);
            object = writableRaster1.getDataElements(n, i1, object);
            arrayOfInt[3] = this.colorModel.getRGB(object);
            int i2 = TexturePaintContext.blend(arrayOfInt, param1Int3, param1Int4);
            object = this.colorModel.getDataElements(i2, object);
          } 
          writableRaster2.setDataElements(b1, b, object);
          if (param1Int3 += param1Int10 < 0) {
            param1Int3 &= Integer.MAX_VALUE;
            param1Int1++;
          } 
          if (param1Int1 += param1Int9 >= param1Int7)
            param1Int1 -= param1Int7; 
          if (param1Int4 += param1Int12 < 0) {
            param1Int4 &= Integer.MAX_VALUE;
            param1Int2++;
          } 
          if (param1Int2 += param1Int11 >= param1Int8)
            param1Int2 -= param1Int8; 
        } 
        if (k += param1Int14 < 0) {
          k &= Integer.MAX_VALUE;
          i++;
        } 
        if (i += param1Int13 >= param1Int7)
          i -= param1Int7; 
        if (m += param1Int16 < 0) {
          m &= Integer.MAX_VALUE;
          j++;
        } 
        if (j += param1Int15 >= param1Int8)
          j -= param1Int8; 
      } 
    }
  }
  
  static class Byte extends TexturePaintContext {
    ByteInterleavedRaster srcRas;
    
    byte[] inData;
    
    int inOff;
    
    int inSpan;
    
    byte[] outData;
    
    int outOff;
    
    int outSpan;
    
    public Byte(ByteInterleavedRaster param1ByteInterleavedRaster, ColorModel param1ColorModel, AffineTransform param1AffineTransform, int param1Int) {
      super(param1ColorModel, param1AffineTransform, param1ByteInterleavedRaster.getWidth(), param1ByteInterleavedRaster.getHeight(), param1Int);
      this.srcRas = param1ByteInterleavedRaster;
      this.inData = param1ByteInterleavedRaster.getDataStorage();
      this.inSpan = param1ByteInterleavedRaster.getScanlineStride();
      this.inOff = param1ByteInterleavedRaster.getDataOffset(0);
    }
    
    public WritableRaster makeRaster(int param1Int1, int param1Int2) {
      WritableRaster writableRaster = makeByteRaster(this.srcRas, param1Int1, param1Int2);
      ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)writableRaster;
      this.outData = byteInterleavedRaster.getDataStorage();
      this.outSpan = byteInterleavedRaster.getScanlineStride();
      this.outOff = byteInterleavedRaster.getDataOffset(0);
      return writableRaster;
    }
    
    public void dispose() { dropByteRaster(this.outRas); }
    
    public void setRaster(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int param1Int10, int param1Int11, int param1Int12, int param1Int13, int param1Int14, int param1Int15, int param1Int16) {
      byte[] arrayOfByte1 = this.inData;
      byte[] arrayOfByte2 = this.outData;
      int i = this.outOff;
      int j = this.inSpan;
      int k = this.inOff;
      int m = this.outSpan;
      boolean bool = (param1Int9 == 1 && param1Int10 == 0 && param1Int11 == 0 && param1Int12 == 0) ? 1 : 0;
      int n = param1Int1;
      int i1 = param1Int2;
      int i2 = param1Int3;
      int i3 = param1Int4;
      if (bool)
        m -= param1Int5; 
      for (byte b = 0; b < param1Int6; b++) {
        if (bool) {
          int i4 = k + i1 * j + param1Int7;
          param1Int1 = param1Int7 - n;
          i += param1Int5;
          if (param1Int7 >= 32) {
            int i5 = param1Int5;
            while (i5 > 0) {
              int i6 = (i5 < param1Int1) ? i5 : param1Int1;
              System.arraycopy(arrayOfByte1, i4 - param1Int1, arrayOfByte2, i - i5, i6);
              i5 -= i6;
              if (param1Int1 -= i6 == 0)
                param1Int1 = param1Int7; 
            } 
          } else {
            for (int i5 = param1Int5; i5 > 0; i5--) {
              arrayOfByte2[i - i5] = arrayOfByte1[i4 - param1Int1];
              if (--param1Int1 == 0)
                param1Int1 = param1Int7; 
            } 
          } 
        } else {
          param1Int1 = n;
          param1Int2 = i1;
          param1Int3 = i2;
          param1Int4 = i3;
          for (int i4 = 0; i4 < param1Int5; i4++) {
            arrayOfByte2[i + i4] = arrayOfByte1[k + param1Int2 * j + param1Int1];
            if (param1Int3 += param1Int10 < 0) {
              param1Int3 &= Integer.MAX_VALUE;
              param1Int1++;
            } 
            if (param1Int1 += param1Int9 >= param1Int7)
              param1Int1 -= param1Int7; 
            if (param1Int4 += param1Int12 < 0) {
              param1Int4 &= Integer.MAX_VALUE;
              param1Int2++;
            } 
            if (param1Int2 += param1Int11 >= param1Int8)
              param1Int2 -= param1Int8; 
          } 
        } 
        if (i2 += param1Int14 < 0) {
          i2 &= Integer.MAX_VALUE;
          n++;
        } 
        if (n += param1Int13 >= param1Int7)
          n -= param1Int7; 
        if (i3 += param1Int16 < 0) {
          i3 &= Integer.MAX_VALUE;
          i1++;
        } 
        if (i1 += param1Int15 >= param1Int8)
          i1 -= param1Int8; 
        i += m;
      } 
    }
  }
  
  static class ByteFilter extends TexturePaintContext {
    ByteInterleavedRaster srcRas;
    
    int[] inPalette = new int[256];
    
    byte[] inData;
    
    int inOff;
    
    int inSpan;
    
    int[] outData;
    
    int outOff;
    
    int outSpan;
    
    public ByteFilter(ByteInterleavedRaster param1ByteInterleavedRaster, ColorModel param1ColorModel, AffineTransform param1AffineTransform, int param1Int) {
      super((param1ColorModel.getTransparency() == 1) ? xrgbmodel : argbmodel, param1AffineTransform, param1ByteInterleavedRaster.getWidth(), param1ByteInterleavedRaster.getHeight(), param1Int);
      ((IndexColorModel)param1ColorModel).getRGBs(this.inPalette);
      this.srcRas = param1ByteInterleavedRaster;
      this.inData = param1ByteInterleavedRaster.getDataStorage();
      this.inSpan = param1ByteInterleavedRaster.getScanlineStride();
      this.inOff = param1ByteInterleavedRaster.getDataOffset(0);
    }
    
    public WritableRaster makeRaster(int param1Int1, int param1Int2) {
      WritableRaster writableRaster = makeRaster(this.colorModel, null, param1Int1, param1Int2);
      IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)writableRaster;
      this.outData = integerInterleavedRaster.getDataStorage();
      this.outSpan = integerInterleavedRaster.getScanlineStride();
      this.outOff = integerInterleavedRaster.getDataOffset(0);
      return writableRaster;
    }
    
    public void setRaster(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int param1Int10, int param1Int11, int param1Int12, int param1Int13, int param1Int14, int param1Int15, int param1Int16) {
      byte[] arrayOfByte = this.inData;
      int[] arrayOfInt1 = this.outData;
      int i = this.outOff;
      int j = this.inSpan;
      int k = this.inOff;
      int m = this.outSpan;
      int n = param1Int1;
      int i1 = param1Int2;
      int i2 = param1Int3;
      int i3 = param1Int4;
      int[] arrayOfInt2 = new int[4];
      for (byte b = 0; b < param1Int6; b++) {
        param1Int1 = n;
        param1Int2 = i1;
        param1Int3 = i2;
        param1Int4 = i3;
        for (int i4 = 0; i4 < param1Int5; i4++) {
          int i5;
          if ((i5 = param1Int1 + 1) >= param1Int7)
            i5 = 0; 
          int i6;
          if ((i6 = param1Int2 + 1) >= param1Int8)
            i6 = 0; 
          arrayOfInt2[0] = this.inPalette[0xFF & arrayOfByte[k + param1Int1 + j * param1Int2]];
          arrayOfInt2[1] = this.inPalette[0xFF & arrayOfByte[k + i5 + j * param1Int2]];
          arrayOfInt2[2] = this.inPalette[0xFF & arrayOfByte[k + param1Int1 + j * i6]];
          arrayOfInt2[3] = this.inPalette[0xFF & arrayOfByte[k + i5 + j * i6]];
          arrayOfInt1[i + i4] = TexturePaintContext.blend(arrayOfInt2, param1Int3, param1Int4);
          if (param1Int3 += param1Int10 < 0) {
            param1Int3 &= Integer.MAX_VALUE;
            param1Int1++;
          } 
          if (param1Int1 += param1Int9 >= param1Int7)
            param1Int1 -= param1Int7; 
          if (param1Int4 += param1Int12 < 0) {
            param1Int4 &= Integer.MAX_VALUE;
            param1Int2++;
          } 
          if (param1Int2 += param1Int11 >= param1Int8)
            param1Int2 -= param1Int8; 
        } 
        if (i2 += param1Int14 < 0) {
          i2 &= Integer.MAX_VALUE;
          n++;
        } 
        if (n += param1Int13 >= param1Int7)
          n -= param1Int7; 
        if (i3 += param1Int16 < 0) {
          i3 &= Integer.MAX_VALUE;
          i1++;
        } 
        if (i1 += param1Int15 >= param1Int8)
          i1 -= param1Int8; 
        i += m;
      } 
    }
  }
  
  static class Int extends TexturePaintContext {
    IntegerInterleavedRaster srcRas;
    
    int[] inData;
    
    int inOff;
    
    int inSpan;
    
    int[] outData;
    
    int outOff;
    
    int outSpan;
    
    boolean filter;
    
    public Int(IntegerInterleavedRaster param1IntegerInterleavedRaster, ColorModel param1ColorModel, AffineTransform param1AffineTransform, int param1Int, boolean param1Boolean) {
      super(param1ColorModel, param1AffineTransform, param1IntegerInterleavedRaster.getWidth(), param1IntegerInterleavedRaster.getHeight(), param1Int);
      this.srcRas = param1IntegerInterleavedRaster;
      this.inData = param1IntegerInterleavedRaster.getDataStorage();
      this.inSpan = param1IntegerInterleavedRaster.getScanlineStride();
      this.inOff = param1IntegerInterleavedRaster.getDataOffset(0);
      this.filter = param1Boolean;
    }
    
    public WritableRaster makeRaster(int param1Int1, int param1Int2) {
      WritableRaster writableRaster = makeRaster(this.colorModel, this.srcRas, param1Int1, param1Int2);
      IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)writableRaster;
      this.outData = integerInterleavedRaster.getDataStorage();
      this.outSpan = integerInterleavedRaster.getScanlineStride();
      this.outOff = integerInterleavedRaster.getDataOffset(0);
      return writableRaster;
    }
    
    public void setRaster(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int param1Int10, int param1Int11, int param1Int12, int param1Int13, int param1Int14, int param1Int15, int param1Int16) {
      int[] arrayOfInt1 = this.inData;
      int[] arrayOfInt2 = this.outData;
      int i = this.outOff;
      int j = this.inSpan;
      int k = this.inOff;
      int m = this.outSpan;
      boolean bool = this.filter;
      boolean bool1 = (param1Int9 == 1 && param1Int10 == 0 && param1Int11 == 0 && param1Int12 == 0 && !bool) ? 1 : 0;
      int n = param1Int1;
      int i1 = param1Int2;
      int i2 = param1Int3;
      int i3 = param1Int4;
      if (bool1)
        m -= param1Int5; 
      int[] arrayOfInt3 = bool ? new int[4] : null;
      for (byte b = 0; b < param1Int6; b++) {
        if (bool1) {
          int i4 = k + i1 * j + param1Int7;
          param1Int1 = param1Int7 - n;
          i += param1Int5;
          if (param1Int7 >= 32) {
            int i5 = param1Int5;
            while (i5 > 0) {
              int i6 = (i5 < param1Int1) ? i5 : param1Int1;
              System.arraycopy(arrayOfInt1, i4 - param1Int1, arrayOfInt2, i - i5, i6);
              i5 -= i6;
              if (param1Int1 -= i6 == 0)
                param1Int1 = param1Int7; 
            } 
          } else {
            for (int i5 = param1Int5; i5 > 0; i5--) {
              arrayOfInt2[i - i5] = arrayOfInt1[i4 - param1Int1];
              if (--param1Int1 == 0)
                param1Int1 = param1Int7; 
            } 
          } 
        } else {
          param1Int1 = n;
          param1Int2 = i1;
          param1Int3 = i2;
          param1Int4 = i3;
          for (int i4 = 0; i4 < param1Int5; i4++) {
            if (bool) {
              int i5;
              if ((i5 = param1Int1 + 1) >= param1Int7)
                i5 = 0; 
              int i6;
              if ((i6 = param1Int2 + 1) >= param1Int8)
                i6 = 0; 
              arrayOfInt3[0] = arrayOfInt1[k + param1Int2 * j + param1Int1];
              arrayOfInt3[1] = arrayOfInt1[k + param1Int2 * j + i5];
              arrayOfInt3[2] = arrayOfInt1[k + i6 * j + param1Int1];
              arrayOfInt3[3] = arrayOfInt1[k + i6 * j + i5];
              arrayOfInt2[i + i4] = TexturePaintContext.blend(arrayOfInt3, param1Int3, param1Int4);
            } else {
              arrayOfInt2[i + i4] = arrayOfInt1[k + param1Int2 * j + param1Int1];
            } 
            if (param1Int3 += param1Int10 < 0) {
              param1Int3 &= Integer.MAX_VALUE;
              param1Int1++;
            } 
            if (param1Int1 += param1Int9 >= param1Int7)
              param1Int1 -= param1Int7; 
            if (param1Int4 += param1Int12 < 0) {
              param1Int4 &= Integer.MAX_VALUE;
              param1Int2++;
            } 
            if (param1Int2 += param1Int11 >= param1Int8)
              param1Int2 -= param1Int8; 
          } 
        } 
        if (i2 += param1Int14 < 0) {
          i2 &= Integer.MAX_VALUE;
          n++;
        } 
        if (n += param1Int13 >= param1Int7)
          n -= param1Int7; 
        if (i3 += param1Int16 < 0) {
          i3 &= Integer.MAX_VALUE;
          i1++;
        } 
        if (i1 += param1Int15 >= param1Int8)
          i1 -= param1Int8; 
        i += m;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\TexturePaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */