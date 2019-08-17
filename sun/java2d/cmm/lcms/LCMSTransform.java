package sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.ProfileDeferralMgr;

public class LCMSTransform implements ColorTransform {
  long ID;
  
  private int inFormatter = 0;
  
  private boolean isInIntPacked = false;
  
  private int outFormatter = 0;
  
  private boolean isOutIntPacked = false;
  
  ICC_Profile[] profiles;
  
  LCMSProfile[] lcmsProfiles;
  
  int renderType;
  
  int transformType;
  
  private int numInComponents = -1;
  
  private int numOutComponents = -1;
  
  private Object disposerReferent = new Object();
  
  public LCMSTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2) {
    this.profiles = new ICC_Profile[1];
    this.profiles[0] = paramICC_Profile;
    this.lcmsProfiles = new LCMSProfile[1];
    this.lcmsProfiles[0] = LCMS.getProfileID(paramICC_Profile);
    this.renderType = (paramInt1 == -1) ? 0 : paramInt1;
    this.transformType = paramInt2;
    this.numInComponents = this.profiles[0].getNumComponents();
    this.numOutComponents = this.profiles[this.profiles.length - 1].getNumComponents();
  }
  
  public LCMSTransform(ColorTransform[] paramArrayOfColorTransform) {
    int i = 0;
    int j;
    for (j = 0; j < paramArrayOfColorTransform.length; j++)
      i += ((LCMSTransform)paramArrayOfColorTransform[j]).profiles.length; 
    this.profiles = new ICC_Profile[i];
    this.lcmsProfiles = new LCMSProfile[i];
    j = 0;
    for (byte b = 0; b < paramArrayOfColorTransform.length; b++) {
      LCMSTransform lCMSTransform = (LCMSTransform)paramArrayOfColorTransform[b];
      System.arraycopy(lCMSTransform.profiles, 0, this.profiles, j, lCMSTransform.profiles.length);
      System.arraycopy(lCMSTransform.lcmsProfiles, 0, this.lcmsProfiles, j, lCMSTransform.lcmsProfiles.length);
      j += lCMSTransform.profiles.length;
    } 
    this.renderType = ((LCMSTransform)paramArrayOfColorTransform[0]).renderType;
    this.numInComponents = this.profiles[0].getNumComponents();
    this.numOutComponents = this.profiles[this.profiles.length - 1].getNumComponents();
  }
  
  public int getNumInComponents() { return this.numInComponents; }
  
  public int getNumOutComponents() { return this.numOutComponents; }
  
  private void doTransform(LCMSImageLayout paramLCMSImageLayout1, LCMSImageLayout paramLCMSImageLayout2) {
    if (this.ID == 0L || this.inFormatter != paramLCMSImageLayout1.pixelType || this.isInIntPacked != paramLCMSImageLayout1.isIntPacked || this.outFormatter != paramLCMSImageLayout2.pixelType || this.isOutIntPacked != paramLCMSImageLayout2.isIntPacked) {
      if (this.ID != 0L)
        this.disposerReferent = new Object(); 
      this.inFormatter = paramLCMSImageLayout1.pixelType;
      this.isInIntPacked = paramLCMSImageLayout1.isIntPacked;
      this.outFormatter = paramLCMSImageLayout2.pixelType;
      this.isOutIntPacked = paramLCMSImageLayout2.isIntPacked;
      this.ID = LCMS.createTransform(this.lcmsProfiles, this.renderType, this.inFormatter, this.isInIntPacked, this.outFormatter, this.isOutIntPacked, this.disposerReferent);
    } 
    LCMS.colorConvert(this, paramLCMSImageLayout1, paramLCMSImageLayout2);
  }
  
  public void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    float[] arrayOfFloat5;
    try {
      if (!paramBufferedImage2.getColorModel().hasAlpha()) {
        LCMSImageLayout lCMSImageLayout = LCMSImageLayout.createImageLayout(paramBufferedImage2);
        if (lCMSImageLayout != null) {
          LCMSImageLayout lCMSImageLayout1 = LCMSImageLayout.createImageLayout(paramBufferedImage1);
          if (lCMSImageLayout1 != null) {
            doTransform(lCMSImageLayout1, lCMSImageLayout);
            return;
          } 
        } 
      } 
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert images");
    } 
    WritableRaster writableRaster1 = paramBufferedImage1.getRaster();
    WritableRaster writableRaster2 = paramBufferedImage2.getRaster();
    ColorModel colorModel1 = paramBufferedImage1.getColorModel();
    ColorModel colorModel2 = paramBufferedImage2.getColorModel();
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    int k = colorModel1.getNumColorComponents();
    int m = colorModel2.getNumColorComponents();
    byte b1 = 8;
    float f = 255.0F;
    byte b2;
    for (b2 = 0; b2 < k; b2++) {
      if (colorModel1.getComponentSize(b2) > 8) {
        b1 = 16;
        f = 65535.0F;
      } 
    } 
    for (b2 = 0; b2 < m; b2++) {
      if (colorModel2.getComponentSize(b2) > 8) {
        b1 = 16;
        f = 65535.0F;
      } 
    } 
    float[] arrayOfFloat1 = new float[k];
    float[] arrayOfFloat2 = new float[k];
    ColorSpace colorSpace = colorModel1.getColorSpace();
    for (byte b3 = 0; b3 < k; b3++) {
      arrayOfFloat1[b3] = colorSpace.getMinValue(b3);
      arrayOfFloat2[b3] = f / (colorSpace.getMaxValue(b3) - arrayOfFloat1[b3]);
    } 
    colorSpace = colorModel2.getColorSpace();
    float[] arrayOfFloat3 = new float[m];
    float[] arrayOfFloat4 = new float[m];
    for (byte b4 = 0; b4 < m; b4++) {
      arrayOfFloat3[b4] = colorSpace.getMinValue(b4);
      arrayOfFloat4[b4] = (colorSpace.getMaxValue(b4) - arrayOfFloat3[b4]) / f;
    } 
    boolean bool = colorModel2.hasAlpha();
    boolean bool1 = (colorModel1.hasAlpha() && bool) ? 1 : 0;
    if (bool) {
      arrayOfFloat5 = new float[m + 1];
    } else {
      arrayOfFloat5 = new float[m];
    } 
    if (b1 == 8) {
      LCMSImageLayout lCMSImageLayout2;
      LCMSImageLayout lCMSImageLayout1;
      byte[] arrayOfByte1 = new byte[i * k];
      byte[] arrayOfByte2 = new byte[i * m];
      float[] arrayOfFloat = null;
      if (bool1)
        arrayOfFloat = new float[i]; 
      try {
        lCMSImageLayout2 = new LCMSImageLayout(arrayOfByte2, arrayOfByte2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(arrayOfByte1, arrayOfByte1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents())).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
      } catch (ImageLayoutException imageLayoutException) {
        throw new CMMException("Unable to convert images");
      } 
      for (byte b = 0; b < j; b++) {
        Object object = null;
        float[] arrayOfFloat6 = null;
        byte b5 = 0;
        byte b6;
        for (b6 = 0; b6 < i; b6++) {
          object = writableRaster1.getDataElements(b6, b, object);
          arrayOfFloat6 = colorModel1.getNormalizedComponents(object, arrayOfFloat6, 0);
          for (byte b7 = 0; b7 < k; b7++)
            arrayOfByte1[b5++] = (byte)(int)((arrayOfFloat6[b7] - arrayOfFloat1[b7]) * arrayOfFloat2[b7] + 0.5F); 
          if (bool1)
            arrayOfFloat[b6] = arrayOfFloat6[k]; 
        } 
        doTransform(lCMSImageLayout1, lCMSImageLayout2);
        object = null;
        b5 = 0;
        for (b6 = 0; b6 < i; b6++) {
          for (byte b7 = 0; b7 < m; b7++)
            arrayOfFloat5[b7] = (arrayOfByte2[b5++] & 0xFF) * arrayOfFloat4[b7] + arrayOfFloat3[b7]; 
          if (bool1) {
            arrayOfFloat5[m] = arrayOfFloat[b6];
          } else if (bool) {
            arrayOfFloat5[m] = 1.0F;
          } 
          object = colorModel2.getDataElements(arrayOfFloat5, 0, object);
          writableRaster2.setDataElements(b6, b, object);
        } 
      } 
    } else {
      LCMSImageLayout lCMSImageLayout2;
      LCMSImageLayout lCMSImageLayout1;
      short[] arrayOfShort1 = new short[i * k];
      short[] arrayOfShort2 = new short[i * m];
      float[] arrayOfFloat = null;
      if (bool1)
        arrayOfFloat = new float[i]; 
      try {
        lCMSImageLayout2 = new LCMSImageLayout(arrayOfShort2, arrayOfShort2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(arrayOfShort1, arrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2)).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
      } catch (ImageLayoutException imageLayoutException) {
        throw new CMMException("Unable to convert images");
      } 
      for (byte b = 0; b < j; b++) {
        Object object = null;
        float[] arrayOfFloat6 = null;
        byte b5 = 0;
        byte b6;
        for (b6 = 0; b6 < i; b6++) {
          object = writableRaster1.getDataElements(b6, b, object);
          arrayOfFloat6 = colorModel1.getNormalizedComponents(object, arrayOfFloat6, 0);
          for (byte b7 = 0; b7 < k; b7++)
            arrayOfShort1[b5++] = (short)(int)((arrayOfFloat6[b7] - arrayOfFloat1[b7]) * arrayOfFloat2[b7] + 0.5F); 
          if (bool1)
            arrayOfFloat[b6] = arrayOfFloat6[k]; 
        } 
        doTransform(lCMSImageLayout1, lCMSImageLayout2);
        object = null;
        b5 = 0;
        for (b6 = 0; b6 < i; b6++) {
          for (byte b7 = 0; b7 < m; b7++)
            arrayOfFloat5[b7] = (arrayOfShort2[b5++] & 0xFFFF) * arrayOfFloat4[b7] + arrayOfFloat3[b7]; 
          if (bool1) {
            arrayOfFloat5[m] = arrayOfFloat[b6];
          } else if (bool) {
            arrayOfFloat5[m] = 1.0F;
          } 
          object = colorModel2.getDataElements(arrayOfFloat5, 0, object);
          writableRaster2.setDataElements(b6, b, object);
        } 
      } 
    } 
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4) {
    boolean bool2;
    boolean bool1;
    LCMSImageLayout lCMSImageLayout2;
    LCMSImageLayout lCMSImageLayout1;
    SampleModel sampleModel1 = paramRaster.getSampleModel();
    SampleModel sampleModel2 = paramWritableRaster.getSampleModel();
    int i = paramRaster.getTransferType();
    int j = paramWritableRaster.getTransferType();
    if (i == 4 || i == 5) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    if (j == 4 || j == 5) {
      bool2 = true;
    } else {
      bool2 = false;
    } 
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    int n = paramRaster.getNumBands();
    int i1 = paramWritableRaster.getNumBands();
    float[] arrayOfFloat1 = new float[n];
    float[] arrayOfFloat2 = new float[i1];
    float[] arrayOfFloat3 = new float[n];
    float[] arrayOfFloat4 = new float[i1];
    int i2;
    for (i2 = 0; i2 < n; i2++) {
      if (bool1) {
        arrayOfFloat1[i2] = 65535.0F / (paramArrayOfFloat2[i2] - paramArrayOfFloat1[i2]);
        arrayOfFloat3[i2] = paramArrayOfFloat1[i2];
      } else {
        if (i == 2) {
          arrayOfFloat1[i2] = 2.0000305F;
        } else {
          arrayOfFloat1[i2] = 65535.0F / ((1 << sampleModel1.getSampleSize(i2)) - 1);
        } 
        arrayOfFloat3[i2] = 0.0F;
      } 
    } 
    for (i2 = 0; i2 < i1; i2++) {
      if (bool2) {
        arrayOfFloat2[i2] = (paramArrayOfFloat4[i2] - paramArrayOfFloat3[i2]) / 65535.0F;
        arrayOfFloat4[i2] = paramArrayOfFloat3[i2];
      } else {
        if (j == 2) {
          arrayOfFloat2[i2] = 0.49999237F;
        } else {
          arrayOfFloat2[i2] = ((1 << sampleModel2.getSampleSize(i2)) - 1) / 65535.0F;
        } 
        arrayOfFloat4[i2] = 0.0F;
      } 
    } 
    i2 = paramRaster.getMinY();
    int i3 = paramWritableRaster.getMinY();
    short[] arrayOfShort1 = new short[k * n];
    short[] arrayOfShort2 = new short[k * i1];
    try {
      lCMSImageLayout2 = new LCMSImageLayout(arrayOfShort2, arrayOfShort2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(arrayOfShort1, arrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2)).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert rasters");
    } 
    byte b = 0;
    while (b < m) {
      int i4 = paramRaster.getMinX();
      byte b1 = 0;
      byte b2 = 0;
      while (b2 < k) {
        for (byte b3 = 0; b3 < n; b3++) {
          float f = paramRaster.getSampleFloat(i4, i2, b3);
          arrayOfShort1[b1++] = (short)(int)((f - arrayOfFloat3[b3]) * arrayOfFloat1[b3] + 0.5F);
        } 
        b2++;
        i4++;
      } 
      doTransform(lCMSImageLayout1, lCMSImageLayout2);
      int i5 = paramWritableRaster.getMinX();
      b1 = 0;
      b2 = 0;
      while (b2 < k) {
        for (byte b3 = 0; b3 < i1; b3++) {
          float f = (arrayOfShort2[b1++] & 0xFFFF) * arrayOfFloat2[b3] + arrayOfFloat4[b3];
          paramWritableRaster.setSample(i5, i3, b3, f);
        } 
        b2++;
        i5++;
      } 
      b++;
      i2++;
      i3++;
    } 
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster) {
    LCMSImageLayout lCMSImageLayout = LCMSImageLayout.createImageLayout(paramWritableRaster);
    if (lCMSImageLayout != null) {
      LCMSImageLayout lCMSImageLayout1 = LCMSImageLayout.createImageLayout(paramRaster);
      if (lCMSImageLayout1 != null) {
        doTransform(lCMSImageLayout1, lCMSImageLayout);
        return;
      } 
    } 
    SampleModel sampleModel1 = paramRaster.getSampleModel();
    SampleModel sampleModel2 = paramWritableRaster.getSampleModel();
    int i = paramRaster.getTransferType();
    int j = paramWritableRaster.getTransferType();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    int n = paramRaster.getNumBands();
    int i1 = paramWritableRaster.getNumBands();
    byte b1 = 8;
    float f = 255.0F;
    byte b2;
    for (b2 = 0; b2 < n; b2++) {
      if (sampleModel1.getSampleSize(b2) > 8) {
        b1 = 16;
        f = 65535.0F;
      } 
    } 
    for (b2 = 0; b2 < i1; b2++) {
      if (sampleModel2.getSampleSize(b2) > 8) {
        b1 = 16;
        f = 65535.0F;
      } 
    } 
    float[] arrayOfFloat1 = new float[n];
    float[] arrayOfFloat2 = new float[i1];
    int i2;
    for (i2 = 0; i2 < n; i2++) {
      if (i == 2) {
        arrayOfFloat1[i2] = f / 32767.0F;
      } else {
        arrayOfFloat1[i2] = f / ((1 << sampleModel1.getSampleSize(i2)) - 1);
      } 
    } 
    for (i2 = 0; i2 < i1; i2++) {
      if (j == 2) {
        arrayOfFloat2[i2] = 32767.0F / f;
      } else {
        arrayOfFloat2[i2] = ((1 << sampleModel2.getSampleSize(i2)) - 1) / f;
      } 
    } 
    i2 = paramRaster.getMinY();
    int i3 = paramWritableRaster.getMinY();
    if (b1 == 8) {
      LCMSImageLayout lCMSImageLayout1;
      byte[] arrayOfByte1 = new byte[k * n];
      byte[] arrayOfByte2 = new byte[k * i1];
      try {
        lCMSImageLayout = new LCMSImageLayout(arrayOfByte2, arrayOfByte2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(arrayOfByte1, arrayOfByte1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents())).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
      } catch (ImageLayoutException imageLayoutException) {
        throw new CMMException("Unable to convert rasters");
      } 
      byte b = 0;
      while (b < m) {
        int i4 = paramRaster.getMinX();
        byte b3 = 0;
        byte b4 = 0;
        while (b4 < k) {
          for (byte b5 = 0; b5 < n; b5++) {
            int i6 = paramRaster.getSample(i4, i2, b5);
            arrayOfByte1[b3++] = (byte)(int)(i6 * arrayOfFloat1[b5] + 0.5F);
          } 
          b4++;
          i4++;
        } 
        doTransform(lCMSImageLayout1, lCMSImageLayout);
        int i5 = paramWritableRaster.getMinX();
        b3 = 0;
        b4 = 0;
        while (b4 < k) {
          for (byte b5 = 0; b5 < i1; b5++) {
            int i6 = (int)((arrayOfByte2[b3++] & 0xFF) * arrayOfFloat2[b5] + 0.5F);
            paramWritableRaster.setSample(i5, i3, b5, i6);
          } 
          b4++;
          i5++;
        } 
        b++;
        i2++;
        i3++;
      } 
    } else {
      LCMSImageLayout lCMSImageLayout1;
      short[] arrayOfShort1 = new short[k * n];
      short[] arrayOfShort2 = new short[k * i1];
      try {
        lCMSImageLayout = new LCMSImageLayout(arrayOfShort2, arrayOfShort2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(arrayOfShort1, arrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2)).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
      } catch (ImageLayoutException imageLayoutException) {
        throw new CMMException("Unable to convert rasters");
      } 
      byte b = 0;
      while (b < m) {
        int i4 = paramRaster.getMinX();
        byte b3 = 0;
        byte b4 = 0;
        while (b4 < k) {
          for (byte b5 = 0; b5 < n; b5++) {
            int i6 = paramRaster.getSample(i4, i2, b5);
            arrayOfShort1[b3++] = (short)(int)(i6 * arrayOfFloat1[b5] + 0.5F);
          } 
          b4++;
          i4++;
        } 
        doTransform(lCMSImageLayout1, lCMSImageLayout);
        int i5 = paramWritableRaster.getMinX();
        b3 = 0;
        b4 = 0;
        while (b4 < k) {
          for (byte b5 = 0; b5 < i1; b5++) {
            int i6 = (int)((arrayOfShort2[b3++] & 0xFFFF) * arrayOfFloat2[b5] + 0.5F);
            paramWritableRaster.setSample(i5, i3, b5, i6);
          } 
          b4++;
          i5++;
        } 
        b++;
        i2++;
        i3++;
      } 
    } 
  }
  
  public short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2) {
    if (paramArrayOfShort2 == null)
      paramArrayOfShort2 = new short[paramArrayOfShort1.length / getNumInComponents() * getNumOutComponents()]; 
    try {
      LCMSImageLayout lCMSImageLayout1;
      LCMSImageLayout lCMSImageLayout2 = new LCMSImageLayout(paramArrayOfShort2, paramArrayOfShort2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(paramArrayOfShort1, paramArrayOfShort1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(2), getNumInComponents() * 2)).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(2), getNumOutComponents() * 2);
      doTransform(lCMSImageLayout1, lCMSImageLayout2);
      return paramArrayOfShort2;
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert data");
    } 
  }
  
  public byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    if (paramArrayOfByte2 == null)
      paramArrayOfByte2 = new byte[paramArrayOfByte1.length / getNumInComponents() * getNumOutComponents()]; 
    try {
      LCMSImageLayout lCMSImageLayout1;
      LCMSImageLayout lCMSImageLayout2 = new LCMSImageLayout(paramArrayOfByte2, paramArrayOfByte2.length / getNumOutComponents(), (lCMSImageLayout1 = new LCMSImageLayout(paramArrayOfByte1, paramArrayOfByte1.length / getNumInComponents(), LCMSImageLayout.CHANNELS_SH(getNumInComponents()) | LCMSImageLayout.BYTES_SH(1), getNumInComponents())).CHANNELS_SH(getNumOutComponents()) | LCMSImageLayout.BYTES_SH(1), getNumOutComponents());
      doTransform(lCMSImageLayout1, lCMSImageLayout2);
      return paramArrayOfByte2;
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert data");
    } 
  }
  
  static  {
    if (ProfileDeferralMgr.deferring)
      ProfileDeferralMgr.activateProfiles(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\lcms\LCMSTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */