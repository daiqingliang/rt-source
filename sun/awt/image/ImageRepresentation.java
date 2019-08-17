package sun.awt.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageRepresentation extends ImageWatched implements ImageConsumer {
  InputStreamImageSource src;
  
  ToolkitImage image;
  
  int tag;
  
  long pData;
  
  int width = -1;
  
  int height = -1;
  
  int hints;
  
  int availinfo;
  
  Rectangle newbits;
  
  BufferedImage bimage;
  
  WritableRaster biRaster;
  
  protected ColorModel cmodel;
  
  ColorModel srcModel = null;
  
  int[] srcLUT = null;
  
  int srcLUTtransIndex = -1;
  
  int numSrcLUT = 0;
  
  boolean forceCMhint;
  
  int sstride;
  
  boolean isDefaultBI = false;
  
  boolean isSameCM = false;
  
  static boolean s_useNative;
  
  private boolean consuming = false;
  
  private int numWaiters;
  
  private static native void initIDs();
  
  public ImageRepresentation(ToolkitImage paramToolkitImage, ColorModel paramColorModel, boolean paramBoolean) {
    this.image = paramToolkitImage;
    if (this.image.getSource() instanceof InputStreamImageSource)
      this.src = (InputStreamImageSource)this.image.getSource(); 
    setColorModel(paramColorModel);
    this.forceCMhint = paramBoolean;
  }
  
  public void reconstruct(int paramInt) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    int i = paramInt & (this.availinfo ^ 0xFFFFFFFF);
    if ((this.availinfo & 0x40) == 0 && i != 0) {
      this.numWaiters++;
      try {
        startProduction();
        for (i = paramInt & (this.availinfo ^ 0xFFFFFFFF); (this.availinfo & 0x40) == 0 && i != 0; i = paramInt & (this.availinfo ^ 0xFFFFFFFF)) {
          try {
            wait();
          } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return;
          } 
        } 
      } finally {
        decrementWaiters();
      } 
    } 
  }
  
  public void setDimensions(int paramInt1, int paramInt2) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    this.image.setDimensions(paramInt1, paramInt2);
    newInfo(this.image, 3, 0, 0, paramInt1, paramInt2);
    if (paramInt1 <= 0 || paramInt2 <= 0) {
      imageComplete(1);
      return;
    } 
    if (this.width != paramInt1 || this.height != paramInt2)
      this.bimage = null; 
    this.width = paramInt1;
    this.height = paramInt2;
    this.availinfo |= 0x3;
  }
  
  public int getWidth() { return this.width; }
  
  public int getHeight() { return this.height; }
  
  ColorModel getColorModel() { return this.cmodel; }
  
  BufferedImage getBufferedImage() { return this.bimage; }
  
  protected BufferedImage createImage(ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean, Hashtable paramHashtable) {
    BufferedImage bufferedImage = new BufferedImage(paramColorModel, paramWritableRaster, paramBoolean, null);
    bufferedImage.setAccelerationPriority(this.image.getAccelerationPriority());
    return bufferedImage;
  }
  
  public void setProperties(Hashtable<?, ?> paramHashtable) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    this.image.setProperties(paramHashtable);
    newInfo(this.image, 4, 0, 0, 0, 0);
  }
  
  public void setColorModel(ColorModel paramColorModel) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    this.srcModel = paramColorModel;
    if (paramColorModel instanceof IndexColorModel) {
      paramColorModel;
      if (paramColorModel.getTransparency() == 3) {
        this.cmodel = ColorModel.getRGBdefault();
        this.srcLUT = null;
      } else {
        IndexColorModel indexColorModel = (IndexColorModel)paramColorModel;
        this.numSrcLUT = indexColorModel.getMapSize();
        this.srcLUT = new int[Math.max(this.numSrcLUT, 256)];
        indexColorModel.getRGBs(this.srcLUT);
        this.srcLUTtransIndex = indexColorModel.getTransparentPixel();
        this.cmodel = paramColorModel;
      } 
    } else if (this.cmodel == null) {
      this.cmodel = paramColorModel;
      this.srcLUT = null;
    } else if (paramColorModel instanceof DirectColorModel) {
      DirectColorModel directColorModel = (DirectColorModel)paramColorModel;
      if (directColorModel.getRedMask() == 16711680 && directColorModel.getGreenMask() == 65280 && directColorModel.getBlueMask() == 255) {
        this.cmodel = paramColorModel;
        this.srcLUT = null;
      } 
    } 
    this.isSameCM = (this.cmodel == paramColorModel);
  }
  
  void createBufferedImage() {
    this.isDefaultBI = false;
    try {
      this.biRaster = this.cmodel.createCompatibleWritableRaster(this.width, this.height);
      this.bimage = createImage(this.cmodel, this.biRaster, this.cmodel.isAlphaPremultiplied(), null);
    } catch (Exception exception) {
      this.cmodel = ColorModel.getRGBdefault();
      this.biRaster = this.cmodel.createCompatibleWritableRaster(this.width, this.height);
      this.bimage = createImage(this.cmodel, this.biRaster, false, null);
    } 
    int i = this.bimage.getType();
    if (this.cmodel == ColorModel.getRGBdefault() || i == 1 || i == 3) {
      this.isDefaultBI = true;
    } else if (this.cmodel instanceof DirectColorModel) {
      DirectColorModel directColorModel = (DirectColorModel)this.cmodel;
      if (directColorModel.getRedMask() == 16711680 && directColorModel.getGreenMask() == 65280 && directColorModel.getBlueMask() == 255)
        this.isDefaultBI = true; 
    } 
  }
  
  private void convertToRGB() {
    int i = this.bimage.getWidth();
    int j = this.bimage.getHeight();
    int k = i * j;
    DataBufferInt dataBufferInt = new DataBufferInt(k);
    int[] arrayOfInt1 = SunWritableRaster.stealData(dataBufferInt, 0);
    if (this.cmodel instanceof IndexColorModel && this.biRaster instanceof ByteComponentRaster && this.biRaster.getNumDataElements() == 1) {
      ByteComponentRaster byteComponentRaster = (ByteComponentRaster)this.biRaster;
      byte[] arrayOfByte = byteComponentRaster.getDataStorage();
      int m = byteComponentRaster.getDataOffset(0);
      for (int n = 0; n < k; n++)
        arrayOfInt1[n] = this.srcLUT[arrayOfByte[m + n] & 0xFF]; 
    } else {
      Object object = null;
      byte b1 = 0;
      for (byte b2 = 0; b2 < j; b2++) {
        for (byte b = 0; b < i; b++) {
          object = this.biRaster.getDataElements(b, b2, object);
          arrayOfInt1[b1++] = this.cmodel.getRGB(object);
        } 
      } 
    } 
    SunWritableRaster.markDirty(dataBufferInt);
    this.isSameCM = false;
    this.cmodel = ColorModel.getRGBdefault();
    int[] arrayOfInt2 = { 16711680, 65280, 255, -16777216 };
    this.biRaster = Raster.createPackedRaster(dataBufferInt, i, j, i, arrayOfInt2, null);
    this.bimage = createImage(this.cmodel, this.biRaster, this.cmodel.isAlphaPremultiplied(), null);
    this.srcLUT = null;
    this.isDefaultBI = true;
  }
  
  public void setHints(int paramInt) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    this.hints = paramInt;
  }
  
  private native boolean setICMpixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt5, int paramInt6, IntegerComponentRaster paramIntegerComponentRaster);
  
  private native boolean setDiffICM(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6, IndexColorModel paramIndexColorModel, byte[] paramArrayOfByte, int paramInt7, int paramInt8, ByteComponentRaster paramByteComponentRaster, int paramInt9);
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    int i = paramInt5;
    Object object = null;
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    synchronized (this) {
      int i2;
      if (this.bimage == null) {
        if (this.cmodel == null)
          this.cmodel = paramColorModel; 
        createBufferedImage();
      } 
      if (paramInt3 <= 0 || paramInt4 <= 0)
        return; 
      int j = this.biRaster.getWidth();
      int k = this.biRaster.getHeight();
      int m = paramInt1 + paramInt3;
      int n = paramInt2 + paramInt4;
      if (paramInt1 < 0) {
        paramInt5 -= paramInt1;
        paramInt1 = 0;
      } else if (m < 0) {
        m = j;
      } 
      if (paramInt2 < 0) {
        paramInt5 -= paramInt2 * paramInt6;
        paramInt2 = 0;
      } else if (n < 0) {
        n = k;
      } 
      if (m > j)
        m = j; 
      if (n > k)
        n = k; 
      if (paramInt1 >= m || paramInt2 >= n)
        return; 
      paramInt3 = m - paramInt1;
      paramInt4 = n - paramInt2;
      if (paramInt5 < 0 || paramInt5 >= paramArrayOfByte.length)
        throw new ArrayIndexOutOfBoundsException("Data offset out of bounds."); 
      int i1 = paramArrayOfByte.length - paramInt5;
      if (i1 < paramInt3)
        throw new ArrayIndexOutOfBoundsException("Data array is too short."); 
      if (paramInt6 < 0) {
        i2 = paramInt5 / -paramInt6 + 1;
      } else if (paramInt6 > 0) {
        i2 = (i1 - paramInt3) / paramInt6 + 1;
      } else {
        i2 = paramInt4;
      } 
      if (paramInt4 > i2)
        throw new ArrayIndexOutOfBoundsException("Data array is too short."); 
      if (this.isSameCM && this.cmodel != paramColorModel && this.srcLUT != null && paramColorModel instanceof IndexColorModel && this.biRaster instanceof ByteComponentRaster) {
        IndexColorModel indexColorModel = (IndexColorModel)paramColorModel;
        ByteComponentRaster byteComponentRaster = (ByteComponentRaster)this.biRaster;
        int i3 = this.numSrcLUT;
        if (!setDiffICM(paramInt1, paramInt2, paramInt3, paramInt4, this.srcLUT, this.srcLUTtransIndex, this.numSrcLUT, indexColorModel, paramArrayOfByte, paramInt5, paramInt6, byteComponentRaster, byteComponentRaster.getDataOffset(0))) {
          convertToRGB();
        } else {
          byteComponentRaster.markDirty();
          if (i3 != this.numSrcLUT) {
            boolean bool = indexColorModel.hasAlpha();
            if (this.srcLUTtransIndex != -1)
              bool = true; 
            int i4 = indexColorModel.getPixelSize();
            indexColorModel = new IndexColorModel(i4, this.numSrcLUT, this.srcLUT, 0, bool, this.srcLUTtransIndex, (i4 > 8) ? 1 : 0);
            this.cmodel = indexColorModel;
            this.bimage = createImage(indexColorModel, byteComponentRaster, false, null);
          } 
          return;
        } 
      } 
      if (this.isDefaultBI) {
        IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)this.biRaster;
        if (this.srcLUT != null && paramColorModel instanceof IndexColorModel) {
          if (paramColorModel != this.srcModel) {
            ((IndexColorModel)paramColorModel).getRGBs(this.srcLUT);
            this.srcModel = paramColorModel;
          } 
          if (s_useNative) {
            if (setICMpixels(paramInt1, paramInt2, paramInt3, paramInt4, this.srcLUT, paramArrayOfByte, paramInt5, paramInt6, integerComponentRaster)) {
              integerComponentRaster.markDirty();
            } else {
              abort();
              return;
            } 
          } else {
            int[] arrayOfInt = new int[paramInt3 * paramInt4];
            byte b1 = 0;
            byte b2 = 0;
            while (b2 < paramInt4) {
              int i3 = i;
              for (byte b = 0; b < paramInt3; b++)
                arrayOfInt[b1++] = this.srcLUT[paramArrayOfByte[i3++] & 0xFF]; 
              b2++;
              i += paramInt6;
            } 
            integerComponentRaster.setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, arrayOfInt);
          } 
        } else {
          int[] arrayOfInt = new int[paramInt3];
          int i3 = paramInt2;
          while (i3 < paramInt2 + paramInt4) {
            int i4 = i;
            for (byte b = 0; b < paramInt3; b++)
              arrayOfInt[b] = paramColorModel.getRGB(paramArrayOfByte[i4++] & 0xFF); 
            integerComponentRaster.setDataElements(paramInt1, i3, paramInt3, 1, arrayOfInt);
            i3++;
            i += paramInt6;
          } 
          this.availinfo |= 0x8;
        } 
      } else if (this.cmodel == paramColorModel && this.biRaster instanceof ByteComponentRaster && this.biRaster.getNumDataElements() == 1) {
        ByteComponentRaster byteComponentRaster = (ByteComponentRaster)this.biRaster;
        if (paramInt5 == 0 && paramInt6 == paramInt3) {
          byteComponentRaster.putByteData(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte);
        } else {
          byte[] arrayOfByte = new byte[paramInt3];
          int i3 = paramInt5;
          for (int i4 = paramInt2; i4 < paramInt2 + paramInt4; i4++) {
            System.arraycopy(paramArrayOfByte, i3, arrayOfByte, 0, paramInt3);
            byteComponentRaster.putByteData(paramInt1, i4, paramInt3, 1, arrayOfByte);
            i3 += paramInt6;
          } 
        } 
      } else {
        int i3 = paramInt2;
        while (i3 < paramInt2 + paramInt4) {
          int i4 = i;
          for (int i5 = paramInt1; i5 < paramInt1 + paramInt3; i5++)
            this.bimage.setRGB(i5, i3, paramColorModel.getRGB(paramArrayOfByte[i4++] & 0xFF)); 
          i3++;
          i += paramInt6;
        } 
        this.availinfo |= 0x8;
      } 
    } 
    if ((this.availinfo & 0x10) == 0)
      newInfo(this.image, 8, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int i = paramInt5;
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    synchronized (this) {
      if (this.bimage == null) {
        if (this.cmodel == null)
          this.cmodel = paramColorModel; 
        createBufferedImage();
      } 
      int[] arrayOfInt = new int[paramInt3];
      if (this.cmodel instanceof IndexColorModel)
        convertToRGB(); 
      if (paramColorModel == this.cmodel && this.biRaster instanceof IntegerComponentRaster) {
        IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)this.biRaster;
        if (paramInt5 == 0 && paramInt6 == paramInt3) {
          integerComponentRaster.setDataElements(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
        } else {
          int j = paramInt2;
          while (j < paramInt2 + paramInt4) {
            System.arraycopy(paramArrayOfInt, i, arrayOfInt, 0, paramInt3);
            integerComponentRaster.setDataElements(paramInt1, j, paramInt3, 1, arrayOfInt);
            j++;
            i += paramInt6;
          } 
        } 
      } else {
        paramColorModel;
        this.cmodel;
        if (paramColorModel.getTransparency() != 1 && this.cmodel.getTransparency() == 1)
          convertToRGB(); 
        if (this.isDefaultBI) {
          IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)this.biRaster;
          int[] arrayOfInt1 = integerComponentRaster.getDataStorage();
          if (this.cmodel.equals(paramColorModel)) {
            int j = integerComponentRaster.getScanlineStride();
            int k = paramInt2 * j + paramInt1;
            byte b = 0;
            while (b < paramInt4) {
              System.arraycopy(paramArrayOfInt, i, arrayOfInt1, k, paramInt3);
              k += j;
              b++;
              i += paramInt6;
            } 
            integerComponentRaster.markDirty();
          } else {
            int j = paramInt2;
            while (j < paramInt2 + paramInt4) {
              int k = i;
              for (byte b = 0; b < paramInt3; b++)
                arrayOfInt[b] = paramColorModel.getRGB(paramArrayOfInt[k++]); 
              integerComponentRaster.setDataElements(paramInt1, j, paramInt3, 1, arrayOfInt);
              j++;
              i += paramInt6;
            } 
          } 
          this.availinfo |= 0x8;
        } else {
          Object object = null;
          int j = paramInt2;
          while (j < paramInt2 + paramInt4) {
            int k = i;
            for (int m = paramInt1; m < paramInt1 + paramInt3; m++) {
              int n = paramColorModel.getRGB(paramArrayOfInt[k++]);
              object = this.cmodel.getDataElements(n, object);
              this.biRaster.setDataElements(m, j, object);
            } 
            j++;
            i += paramInt6;
          } 
          this.availinfo |= 0x8;
        } 
      } 
    } 
    if ((this.availinfo & 0x10) == 0)
      newInfo(this.image, 8, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  public BufferedImage getOpaqueRGBImage() {
    if (this.bimage.getType() == 2) {
      int i = this.bimage.getWidth();
      int j = this.bimage.getHeight();
      int k = i * j;
      DataBufferInt dataBufferInt = (DataBufferInt)this.biRaster.getDataBuffer();
      int[] arrayOfInt1 = SunWritableRaster.stealData(dataBufferInt, 0);
      for (byte b = 0; b < k; b++) {
        if (arrayOfInt1[b] >>> 24 != 255)
          return this.bimage; 
      } 
      DirectColorModel directColorModel = new DirectColorModel(24, 16711680, 65280, 255);
      int[] arrayOfInt2 = { 16711680, 65280, 255 };
      WritableRaster writableRaster = Raster.createPackedRaster(dataBufferInt, i, j, i, arrayOfInt2, null);
      try {
        return createImage(directColorModel, writableRaster, false, null);
      } catch (Exception exception) {
        return this.bimage;
      } 
    } 
    return this.bimage;
  }
  
  public void imageComplete(int paramInt) {
    int i;
    boolean bool;
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    switch (paramInt) {
      default:
        bool = true;
        i = 128;
        break;
      case 1:
        this.image.addInfo(64);
        bool = true;
        i = 64;
        dispose();
        break;
      case 3:
        bool = true;
        i = 32;
        break;
      case 2:
        bool = false;
        i = 16;
        break;
    } 
    synchronized (this) {
      if (bool) {
        this.image.getSource().removeConsumer(this);
        this.consuming = false;
        this.newbits = null;
        if (this.bimage != null)
          this.bimage = getOpaqueRGBImage(); 
      } 
      this.availinfo |= i;
      notifyAll();
    } 
    newInfo(this.image, i, 0, 0, this.width, this.height);
    this.image.infoDone(paramInt);
  }
  
  void startProduction() {
    if (!this.consuming) {
      this.consuming = true;
      this.image.getSource().startProduction(this);
    } 
  }
  
  private void checkConsumption() {
    if (isWatcherListEmpty() && this.numWaiters == 0 && (this.availinfo & 0x20) == 0)
      dispose(); 
  }
  
  public void notifyWatcherListEmpty() { checkConsumption(); }
  
  private void decrementWaiters() {
    this.numWaiters--;
    checkConsumption();
  }
  
  public boolean prepare(ImageObserver paramImageObserver) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    if ((this.availinfo & 0x40) != 0) {
      if (paramImageObserver != null)
        paramImageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1); 
      return false;
    } 
    boolean bool = ((this.availinfo & 0x20) != 0);
    if (!bool) {
      addWatcher(paramImageObserver);
      startProduction();
      bool = ((this.availinfo & 0x20) != 0);
    } 
    return bool;
  }
  
  public int check(ImageObserver paramImageObserver) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    if ((this.availinfo & 0x60) == 0)
      addWatcher(paramImageObserver); 
    return this.availinfo;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    if ((this.availinfo & 0x40) != 0) {
      if (paramImageObserver != null)
        paramImageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1); 
      return false;
    } 
    boolean bool = ((this.availinfo & 0x20) != 0);
    boolean bool1 = ((this.availinfo & 0x80) != 0) ? 1 : 0;
    if (!bool && !bool1) {
      addWatcher(paramImageObserver);
      startProduction();
      bool = ((this.availinfo & 0x20) != 0);
    } 
    if (bool || 0 != (this.availinfo & 0x10))
      paramGraphics.drawImage(this.bimage, paramInt1, paramInt2, paramColor, null); 
    return bool;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    if ((this.availinfo & 0x40) != 0) {
      if (paramImageObserver != null)
        paramImageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1); 
      return false;
    } 
    boolean bool = ((this.availinfo & 0x20) != 0);
    boolean bool1 = ((this.availinfo & 0x80) != 0) ? 1 : 0;
    if (!bool && !bool1) {
      addWatcher(paramImageObserver);
      startProduction();
      bool = ((this.availinfo & 0x20) != 0);
    } 
    if (bool || 0 != (this.availinfo & 0x10))
      paramGraphics.drawImage(this.bimage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, null); 
    return bool;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    if ((this.availinfo & 0x40) != 0) {
      if (paramImageObserver != null)
        paramImageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1); 
      return false;
    } 
    boolean bool = ((this.availinfo & 0x20) != 0);
    boolean bool1 = ((this.availinfo & 0x80) != 0) ? 1 : 0;
    if (!bool && !bool1) {
      addWatcher(paramImageObserver);
      startProduction();
      bool = ((this.availinfo & 0x20) != 0);
    } 
    if (bool || 0 != (this.availinfo & 0x10))
      paramGraphics.drawImage(this.bimage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, null); 
    return bool;
  }
  
  public boolean drawToBufImage(Graphics paramGraphics, ToolkitImage paramToolkitImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) {
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    if (this.src != null)
      this.src.checkSecurity(null, false); 
    if ((this.availinfo & 0x40) != 0) {
      if (paramImageObserver != null)
        paramImageObserver.imageUpdate(this.image, 192, -1, -1, -1, -1); 
      return false;
    } 
    boolean bool = ((this.availinfo & 0x20) != 0);
    boolean bool1 = ((this.availinfo & 0x80) != 0) ? 1 : 0;
    if (!bool && !bool1) {
      addWatcher(paramImageObserver);
      startProduction();
      bool = ((this.availinfo & 0x20) != 0);
    } 
    if (bool || 0 != (this.availinfo & 0x10))
      graphics2D.drawImage(this.bimage, paramAffineTransform, null); 
    return bool;
  }
  
  void abort() {
    this.image.getSource().removeConsumer(this);
    this.consuming = false;
    this.newbits = null;
    this.bimage = null;
    this.biRaster = null;
    this.cmodel = null;
    this.srcLUT = null;
    this.isDefaultBI = false;
    this.isSameCM = false;
    newInfo(this.image, 128, -1, -1, -1, -1);
    this.availinfo &= 0xFFFFFF87;
  }
  
  void dispose() {
    this.image.getSource().removeConsumer(this);
    this.consuming = false;
    this.newbits = null;
    this.availinfo &= 0xFFFFFFC7;
  }
  
  public void setAccelerationPriority(float paramFloat) {
    if (this.bimage != null)
      this.bimage.setAccelerationPriority(paramFloat); 
  }
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
    s_useNative = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\ImageRepresentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */