package sun.java2d.cmm.kcms;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ICC_Transform implements ColorTransform {
  long ID;
  
  long getID() { return this.ID; }
  
  public void finalize() { CMM.checkStatus(CMM.cmmFreeTransform(this.ID)); }
  
  public int getNumInComponents() {
    int[] arrayOfInt = new int[2];
    CMM.checkStatus(CMM.cmmGetNumComponents(this.ID, arrayOfInt));
    return arrayOfInt[0];
  }
  
  public int getNumOutComponents() {
    int[] arrayOfInt = new int[2];
    CMM.checkStatus(CMM.cmmGetNumComponents(this.ID, arrayOfInt));
    return arrayOfInt[1];
  }
  
  public void colorConvert(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    float[] arrayOfFloat5;
    CMMImageLayout cMMImageLayout = getImageLayout(paramBufferedImage1);
    if (cMMImageLayout != null) {
      CMMImageLayout cMMImageLayout1 = getImageLayout(paramBufferedImage2);
      if (cMMImageLayout1 != null) {
        synchronized (this) {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout, cMMImageLayout1));
        } 
        return;
      } 
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
      CMMImageLayout cMMImageLayout1;
      byte[] arrayOfByte1 = new byte[i * k];
      byte[] arrayOfByte2 = new byte[i * m];
      float[] arrayOfFloat = null;
      if (bool1)
        arrayOfFloat = new float[i]; 
      pelArrayInfo pelArrayInfo = new pelArrayInfo(this, arrayOfByte1, arrayOfByte2);
      try {
        cMMImageLayout = new CMMImageLayout(arrayOfByte1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
        cMMImageLayout1 = new CMMImageLayout(arrayOfByte2, pelArrayInfo.nPels, pelArrayInfo.nDest);
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
        synchronized (this) {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout, cMMImageLayout1));
        } 
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
      CMMImageLayout cMMImageLayout1;
      short[] arrayOfShort1 = new short[i * k];
      short[] arrayOfShort2 = new short[i * m];
      float[] arrayOfFloat = null;
      if (bool1)
        arrayOfFloat = new float[i]; 
      pelArrayInfo pelArrayInfo = new pelArrayInfo(this, arrayOfShort1, arrayOfShort2);
      try {
        cMMImageLayout = new CMMImageLayout(arrayOfShort1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
        cMMImageLayout1 = new CMMImageLayout(arrayOfShort2, pelArrayInfo.nPels, pelArrayInfo.nDest);
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
        synchronized (this) {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout, cMMImageLayout1));
        } 
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
  
  private CMMImageLayout getImageLayout(BufferedImage paramBufferedImage) {
    try {
      ComponentColorModel componentColorModel;
      switch (paramBufferedImage.getType()) {
        case 1:
        case 2:
        case 4:
          return new CMMImageLayout(paramBufferedImage);
        case 5:
        case 6:
          componentColorModel = (ComponentColorModel)paramBufferedImage.getColorModel();
          return (componentColorModel.getClass() == ComponentColorModel.class || checkMinMaxScaling(componentColorModel)) ? new CMMImageLayout(paramBufferedImage) : null;
        case 10:
          componentColorModel = (ComponentColorModel)paramBufferedImage.getColorModel();
          return (componentColorModel.getComponentSize(0) != 8) ? null : ((componentColorModel.getClass() == ComponentColorModel.class || checkMinMaxScaling(componentColorModel)) ? new CMMImageLayout(paramBufferedImage) : null);
        case 11:
          componentColorModel = (ComponentColorModel)paramBufferedImage.getColorModel();
          return (componentColorModel.getComponentSize(0) != 16) ? null : ((componentColorModel.getClass() == ComponentColorModel.class || checkMinMaxScaling(componentColorModel)) ? new CMMImageLayout(paramBufferedImage) : null);
      } 
      ColorModel colorModel = paramBufferedImage.getColorModel();
      if (colorModel instanceof DirectColorModel) {
        SampleModel sampleModel = paramBufferedImage.getSampleModel();
        if (!(sampleModel instanceof SinglePixelPackedSampleModel))
          return null; 
        if (colorModel.getTransferType() != 3)
          return null; 
        if (colorModel.hasAlpha() && colorModel.isAlphaPremultiplied())
          return null; 
        DirectColorModel directColorModel = (DirectColorModel)colorModel;
        int i = directColorModel.getRedMask();
        int j = directColorModel.getGreenMask();
        int k = directColorModel.getBlueMask();
        int m = directColorModel.getAlphaMask();
        byte b4 = -1;
        byte b3 = b4;
        byte b2 = b3;
        byte b1 = b2;
        byte b5 = 0;
        byte b6 = 3;
        if (m != 0)
          b6 = 4; 
        byte b7 = 0;
        int n;
        for (n = -16777216; b7 < 4; n >>>= 8) {
          if (i == n) {
            b1 = b7;
            b5++;
          } else if (j == n) {
            b2 = b7;
            b5++;
          } else if (k == n) {
            b3 = b7;
            b5++;
          } else if (m == n) {
            b4 = b7;
            b5++;
          } 
          b7++;
        } 
        return (b5 != b6) ? null : new CMMImageLayout(paramBufferedImage, (SinglePixelPackedSampleModel)sampleModel, b1, b2, b3, b4);
      } 
      if (colorModel instanceof ComponentColorModel) {
        SampleModel sampleModel = paramBufferedImage.getSampleModel();
        if (!(sampleModel instanceof ComponentSampleModel))
          return null; 
        if (colorModel.hasAlpha() && colorModel.isAlphaPremultiplied())
          return null; 
        int i = colorModel.getNumComponents();
        if (sampleModel.getNumBands() != i)
          return null; 
        int j = colorModel.getTransferType();
        if (j == 0) {
          for (byte b = 0; b < i; b++) {
            if (colorModel.getComponentSize(b) != 8)
              return null; 
          } 
        } else if (j == 1) {
          for (byte b = 0; b < i; b++) {
            if (colorModel.getComponentSize(b) != 16)
              return null; 
          } 
        } else {
          return null;
        } 
        ComponentColorModel componentColorModel1 = (ComponentColorModel)colorModel;
        return (componentColorModel1.getClass() == ComponentColorModel.class || checkMinMaxScaling(componentColorModel1)) ? new CMMImageLayout(paramBufferedImage, (ComponentSampleModel)sampleModel) : null;
      } 
      return null;
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert image");
    } 
  }
  
  private boolean checkMinMaxScaling(ComponentColorModel paramComponentColorModel) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    float f;
    float[] arrayOfFloat2;
    float[] arrayOfFloat1;
    int i = paramComponentColorModel.getNumComponents();
    int j = paramComponentColorModel.getNumColorComponents();
    int[] arrayOfInt = paramComponentColorModel.getComponentSize();
    boolean bool = paramComponentColorModel.hasAlpha();
    switch (paramComponentColorModel.getTransferType()) {
      case 0:
        arrayOfByte = new byte[i];
        for (b = 0; b < j; b++)
          arrayOfByte[b] = 0; 
        if (bool)
          arrayOfByte[j] = (byte)((1 << arrayOfInt[j]) - 1); 
        arrayOfFloat1 = paramComponentColorModel.getNormalizedComponents(arrayOfByte, null, 0);
        for (b = 0; b < j; b++)
          arrayOfByte[b] = (byte)((1 << arrayOfInt[b]) - 1); 
        arrayOfFloat2 = paramComponentColorModel.getNormalizedComponents(arrayOfByte, null, 0);
        f = 256.0F;
        break;
      case 1:
        arrayOfShort = new short[i];
        for (b = 0; b < j; b++)
          arrayOfShort[b] = 0; 
        if (bool)
          arrayOfShort[j] = (short)(byte)((1 << arrayOfInt[j]) - 1); 
        arrayOfFloat1 = paramComponentColorModel.getNormalizedComponents(arrayOfShort, null, 0);
        for (b = 0; b < j; b++)
          arrayOfShort[b] = (short)(byte)((1 << arrayOfInt[b]) - 1); 
        arrayOfFloat2 = paramComponentColorModel.getNormalizedComponents(arrayOfShort, null, 0);
        f = 65536.0F;
        break;
      default:
        return false;
    } 
    ColorSpace colorSpace = paramComponentColorModel.getColorSpace();
    for (byte b = 0; b < j; b++) {
      float f1 = colorSpace.getMinValue(b);
      float f2 = colorSpace.getMaxValue(b);
      float f3 = (f2 - f1) / f;
      f1 -= arrayOfFloat1[b];
      if (f1 < 0.0F)
        f1 = -f1; 
      f2 -= arrayOfFloat2[b];
      if (f2 < 0.0F)
        f2 = -f2; 
      if (f1 > f3 || f2 > f3)
        return false; 
    } 
    return true;
  }
  
  public void colorConvert(Raster paramRaster, WritableRaster paramWritableRaster, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4) {
    boolean bool2;
    boolean bool1;
    CMMImageLayout cMMImageLayout2;
    CMMImageLayout cMMImageLayout1;
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
    pelArrayInfo pelArrayInfo = new pelArrayInfo(this, arrayOfShort1, arrayOfShort2);
    try {
      cMMImageLayout1 = new CMMImageLayout(arrayOfShort1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
      cMMImageLayout2 = new CMMImageLayout(arrayOfShort2, pelArrayInfo.nPels, pelArrayInfo.nDest);
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
      synchronized (this) {
        CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout1, cMMImageLayout2));
      } 
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
    CMMImageLayout cMMImageLayout = getImageLayout(paramRaster);
    if (cMMImageLayout != null) {
      CMMImageLayout cMMImageLayout1 = getImageLayout(paramWritableRaster);
      if (cMMImageLayout1 != null) {
        synchronized (this) {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout, cMMImageLayout1));
        } 
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
      CMMImageLayout cMMImageLayout1;
      byte[] arrayOfByte1 = new byte[k * n];
      byte[] arrayOfByte2 = new byte[k * i1];
      pelArrayInfo pelArrayInfo = new pelArrayInfo(this, arrayOfByte1, arrayOfByte2);
      try {
        cMMImageLayout = new CMMImageLayout(arrayOfByte1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
        cMMImageLayout1 = new CMMImageLayout(arrayOfByte2, pelArrayInfo.nPels, pelArrayInfo.nDest);
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
        synchronized (this) {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout, cMMImageLayout1));
        } 
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
      CMMImageLayout cMMImageLayout1;
      short[] arrayOfShort1 = new short[k * n];
      short[] arrayOfShort2 = new short[k * i1];
      pelArrayInfo pelArrayInfo = new pelArrayInfo(this, arrayOfShort1, arrayOfShort2);
      try {
        cMMImageLayout = new CMMImageLayout(arrayOfShort1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
        cMMImageLayout1 = new CMMImageLayout(arrayOfShort2, pelArrayInfo.nPels, pelArrayInfo.nDest);
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
        synchronized (this) {
          CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout, cMMImageLayout1));
        } 
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
  
  private CMMImageLayout getImageLayout(Raster paramRaster) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (sampleModel instanceof ComponentSampleModel) {
      int i = paramRaster.getNumBands();
      int j = sampleModel.getTransferType();
      if (j == 0) {
        for (byte b = 0; b < i; b++) {
          if (sampleModel.getSampleSize(b) != 8)
            return null; 
        } 
      } else if (j == 1) {
        for (byte b = 0; b < i; b++) {
          if (sampleModel.getSampleSize(b) != 16)
            return null; 
        } 
      } else {
        return null;
      } 
      try {
        return new CMMImageLayout(paramRaster, (ComponentSampleModel)sampleModel);
      } catch (ImageLayoutException imageLayoutException) {
        throw new CMMException("Unable to convert raster");
      } 
    } 
    return null;
  }
  
  public short[] colorConvert(short[] paramArrayOfShort1, short[] paramArrayOfShort2) {
    short[] arrayOfShort;
    CMMImageLayout cMMImageLayout2;
    CMMImageLayout cMMImageLayout1;
    pelArrayInfo pelArrayInfo = new pelArrayInfo(this, paramArrayOfShort1, paramArrayOfShort2);
    if (paramArrayOfShort2 != null) {
      arrayOfShort = paramArrayOfShort2;
    } else {
      arrayOfShort = new short[pelArrayInfo.destSize];
    } 
    try {
      cMMImageLayout1 = new CMMImageLayout(paramArrayOfShort1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
      cMMImageLayout2 = new CMMImageLayout(arrayOfShort, pelArrayInfo.nPels, pelArrayInfo.nDest);
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert data");
    } 
    synchronized (this) {
      CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout1, cMMImageLayout2));
    } 
    return arrayOfShort;
  }
  
  public byte[] colorConvert(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    byte[] arrayOfByte;
    CMMImageLayout cMMImageLayout2;
    CMMImageLayout cMMImageLayout1;
    pelArrayInfo pelArrayInfo = new pelArrayInfo(this, paramArrayOfByte1, paramArrayOfByte2);
    if (paramArrayOfByte2 != null) {
      arrayOfByte = paramArrayOfByte2;
    } else {
      arrayOfByte = new byte[pelArrayInfo.destSize];
    } 
    try {
      cMMImageLayout1 = new CMMImageLayout(paramArrayOfByte1, pelArrayInfo.nPels, pelArrayInfo.nSrc);
      cMMImageLayout2 = new CMMImageLayout(arrayOfByte, pelArrayInfo.nPels, pelArrayInfo.nDest);
    } catch (ImageLayoutException imageLayoutException) {
      throw new CMMException("Unable to convert data");
    } 
    synchronized (this) {
      CMM.checkStatus(CMM.cmmColorConvert(this.ID, cMMImageLayout1, cMMImageLayout2));
    } 
    return arrayOfByte;
  }
  
  static  {
    if (ProfileDeferralMgr.deferring)
      ProfileDeferralMgr.activateProfiles(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\kcms\ICC_Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */