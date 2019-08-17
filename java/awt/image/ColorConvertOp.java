package java.awt.image;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.ProfileDeferralMgr;

public class ColorConvertOp implements BufferedImageOp, RasterOp {
  ICC_Profile[] profileList;
  
  ColorSpace[] CSList;
  
  ColorTransform thisTransform;
  
  ColorTransform thisRasterTransform;
  
  ICC_Profile thisSrcProfile;
  
  ICC_Profile thisDestProfile;
  
  RenderingHints hints;
  
  boolean gotProfiles;
  
  float[] srcMinVals;
  
  float[] srcMaxVals;
  
  float[] dstMinVals;
  
  float[] dstMaxVals;
  
  public ColorConvertOp(RenderingHints paramRenderingHints) {
    this.profileList = new ICC_Profile[0];
    this.hints = paramRenderingHints;
  }
  
  public ColorConvertOp(ColorSpace paramColorSpace, RenderingHints paramRenderingHints) {
    if (paramColorSpace == null)
      throw new NullPointerException("ColorSpace cannot be null"); 
    if (paramColorSpace instanceof ICC_ColorSpace) {
      this.profileList = new ICC_Profile[1];
      this.profileList[0] = ((ICC_ColorSpace)paramColorSpace).getProfile();
    } else {
      this.CSList = new ColorSpace[1];
      this.CSList[0] = paramColorSpace;
    } 
    this.hints = paramRenderingHints;
  }
  
  public ColorConvertOp(ColorSpace paramColorSpace1, ColorSpace paramColorSpace2, RenderingHints paramRenderingHints) {
    if (paramColorSpace1 == null || paramColorSpace2 == null)
      throw new NullPointerException("ColorSpaces cannot be null"); 
    if (paramColorSpace1 instanceof ICC_ColorSpace && paramColorSpace2 instanceof ICC_ColorSpace) {
      this.profileList = new ICC_Profile[2];
      this.profileList[0] = ((ICC_ColorSpace)paramColorSpace1).getProfile();
      this.profileList[1] = ((ICC_ColorSpace)paramColorSpace2).getProfile();
      getMinMaxValsFromColorSpaces(paramColorSpace1, paramColorSpace2);
    } else {
      this.CSList = new ColorSpace[2];
      this.CSList[0] = paramColorSpace1;
      this.CSList[1] = paramColorSpace2;
    } 
    this.hints = paramRenderingHints;
  }
  
  public ColorConvertOp(ICC_Profile[] paramArrayOfICC_Profile, RenderingHints paramRenderingHints) {
    if (paramArrayOfICC_Profile == null)
      throw new NullPointerException("Profiles cannot be null"); 
    this.gotProfiles = true;
    this.profileList = new ICC_Profile[paramArrayOfICC_Profile.length];
    for (byte b = 0; b < paramArrayOfICC_Profile.length; b++)
      this.profileList[b] = paramArrayOfICC_Profile[b]; 
    this.hints = paramRenderingHints;
  }
  
  public final ICC_Profile[] getICC_Profiles() {
    if (this.gotProfiles) {
      ICC_Profile[] arrayOfICC_Profile = new ICC_Profile[this.profileList.length];
      for (byte b = 0; b < this.profileList.length; b++)
        arrayOfICC_Profile[b] = this.profileList[b]; 
      return arrayOfICC_Profile;
    } 
    return null;
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    ColorSpace colorSpace2;
    BufferedImage bufferedImage = null;
    if (paramBufferedImage1.getColorModel() instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)paramBufferedImage1.getColorModel();
      paramBufferedImage1 = indexColorModel.convertToIntDiscrete(paramBufferedImage1.getRaster(), true);
    } 
    ColorSpace colorSpace1 = paramBufferedImage1.getColorModel().getColorSpace();
    if (paramBufferedImage2 != null) {
      if (paramBufferedImage2.getColorModel() instanceof IndexColorModel) {
        bufferedImage = paramBufferedImage2;
        paramBufferedImage2 = null;
        colorSpace2 = null;
      } else {
        colorSpace2 = paramBufferedImage2.getColorModel().getColorSpace();
      } 
    } else {
      colorSpace2 = null;
    } 
    if (this.CSList != null || !(colorSpace1 instanceof ICC_ColorSpace) || (paramBufferedImage2 != null && !(colorSpace2 instanceof ICC_ColorSpace))) {
      paramBufferedImage2 = nonICCBIFilter(paramBufferedImage1, colorSpace1, paramBufferedImage2, colorSpace2);
    } else {
      paramBufferedImage2 = ICCBIFilter(paramBufferedImage1, colorSpace1, paramBufferedImage2, colorSpace2);
    } 
    if (bufferedImage != null) {
      graphics2D = bufferedImage.createGraphics();
      try {
        graphics2D.drawImage(paramBufferedImage2, 0, 0, null);
      } finally {
        graphics2D.dispose();
      } 
      return bufferedImage;
    } 
    return paramBufferedImage2;
  }
  
  private final BufferedImage ICCBIFilter(BufferedImage paramBufferedImage1, ColorSpace paramColorSpace1, BufferedImage paramBufferedImage2, ColorSpace paramColorSpace2) {
    int i = this.profileList.length;
    ICC_Profile iCC_Profile1 = null;
    ICC_Profile iCC_Profile2 = null;
    iCC_Profile1 = ((ICC_ColorSpace)paramColorSpace1).getProfile();
    if (paramBufferedImage2 == null) {
      if (i == 0)
        throw new IllegalArgumentException("Destination ColorSpace is undefined"); 
      iCC_Profile2 = this.profileList[i - 1];
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
    } else {
      if (paramBufferedImage1.getHeight() != paramBufferedImage2.getHeight() || paramBufferedImage1.getWidth() != paramBufferedImage2.getWidth())
        throw new IllegalArgumentException("Width or height of BufferedImages do not match"); 
      iCC_Profile2 = ((ICC_ColorSpace)paramColorSpace2).getProfile();
    } 
    if (iCC_Profile1 == iCC_Profile2) {
      boolean bool = true;
      for (byte b = 0; b < i; b++) {
        if (iCC_Profile1 != this.profileList[b]) {
          bool = false;
          break;
        } 
      } 
      if (bool) {
        graphics2D = paramBufferedImage2.createGraphics();
        try {
          graphics2D.drawImage(paramBufferedImage1, 0, 0, null);
        } finally {
          graphics2D.dispose();
        } 
        return paramBufferedImage2;
      } 
    } 
    if (this.thisTransform == null || this.thisSrcProfile != iCC_Profile1 || this.thisDestProfile != iCC_Profile2)
      updateBITransform(iCC_Profile1, iCC_Profile2); 
    this.thisTransform.colorConvert(paramBufferedImage1, paramBufferedImage2);
    return paramBufferedImage2;
  }
  
  private void updateBITransform(ICC_Profile paramICC_Profile1, ICC_Profile paramICC_Profile2) {
    boolean bool1 = false;
    boolean bool2 = false;
    int i = this.profileList.length;
    int j = i;
    if (i == 0 || paramICC_Profile1 != this.profileList[false]) {
      j++;
      bool1 = true;
    } 
    if (i == 0 || paramICC_Profile2 != this.profileList[i - true] || j < 2) {
      j++;
      bool2 = true;
    } 
    ICC_Profile[] arrayOfICC_Profile = new ICC_Profile[j];
    byte b3 = 0;
    if (bool1)
      arrayOfICC_Profile[b3++] = paramICC_Profile1; 
    byte b1;
    for (b1 = 0; b1 < i; b1++)
      arrayOfICC_Profile[b3++] = this.profileList[b1]; 
    if (bool2)
      arrayOfICC_Profile[b3] = paramICC_Profile2; 
    ColorTransform[] arrayOfColorTransform = new ColorTransform[j];
    if (arrayOfICC_Profile[0].getProfileClass() == 2) {
      boolean bool = true;
    } else {
      boolean bool = false;
    } 
    byte b2 = 1;
    PCMM pCMM = CMSManager.getModule();
    for (b1 = 0; b1 < j; b1++) {
      if (b1 == j - 1) {
        b2 = 2;
      } else if (b2 == 4 && arrayOfICC_Profile[b1].getProfileClass() == 5) {
        k = 0;
        b2 = 1;
      } 
      arrayOfColorTransform[b1] = pCMM.createTransform(arrayOfICC_Profile[b1], k, b2);
      int k = getRenderingIntent(arrayOfICC_Profile[b1]);
      b2 = 4;
    } 
    this.thisTransform = pCMM.createTransform(arrayOfColorTransform);
    this.thisSrcProfile = paramICC_Profile1;
    this.thisDestProfile = paramICC_Profile2;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster) {
    if (this.CSList != null)
      return nonICCRasterFilter(paramRaster, paramWritableRaster); 
    int i = this.profileList.length;
    if (i < 2)
      throw new IllegalArgumentException("Source or Destination ColorSpace is undefined"); 
    if (paramRaster.getNumBands() != this.profileList[0].getNumComponents())
      throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match"); 
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else {
      if (paramRaster.getHeight() != paramWritableRaster.getHeight() || paramRaster.getWidth() != paramWritableRaster.getWidth())
        throw new IllegalArgumentException("Width or height of Rasters do not match"); 
      if (paramWritableRaster.getNumBands() != this.profileList[i - 1].getNumComponents())
        throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match"); 
    } 
    if (this.thisRasterTransform == null) {
      ColorTransform[] arrayOfColorTransform = new ColorTransform[i];
      if (this.profileList[0].getProfileClass() == 2) {
        boolean bool = true;
      } else {
        boolean bool = false;
      } 
      byte b2 = 1;
      PCMM pCMM = CMSManager.getModule();
      for (byte b1 = 0; b1 < i; b1++) {
        if (b1 == i - 1) {
          b2 = 2;
        } else if (b2 == 4 && this.profileList[b1].getProfileClass() == 5) {
          m = 0;
          b2 = 1;
        } 
        arrayOfColorTransform[b1] = pCMM.createTransform(this.profileList[b1], m, b2);
        int m = getRenderingIntent(this.profileList[b1]);
        b2 = 4;
      } 
      this.thisRasterTransform = pCMM.createTransform(arrayOfColorTransform);
    } 
    int j = paramRaster.getTransferType();
    int k = paramWritableRaster.getTransferType();
    if (j == 4 || j == 5 || k == 4 || k == 5) {
      if (this.srcMinVals == null)
        getMinMaxValsFromProfiles(this.profileList[0], this.profileList[i - 1]); 
      this.thisRasterTransform.colorConvert(paramRaster, paramWritableRaster, this.srcMinVals, this.srcMaxVals, this.dstMinVals, this.dstMaxVals);
    } else {
      this.thisRasterTransform.colorConvert(paramRaster, paramWritableRaster);
    } 
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage) { return getBounds2D(paramBufferedImage.getRaster()); }
  
  public final Rectangle2D getBounds2D(Raster paramRaster) { return paramRaster.getBounds(); }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel) {
    ColorSpace colorSpace = null;
    if (paramColorModel == null)
      if (this.CSList == null) {
        int i = this.profileList.length;
        if (i == 0)
          throw new IllegalArgumentException("Destination ColorSpace is undefined"); 
        ICC_Profile iCC_Profile = this.profileList[i - 1];
        colorSpace = new ICC_ColorSpace(iCC_Profile);
      } else {
        int i = this.CSList.length;
        colorSpace = this.CSList[i - 1];
      }  
    return createCompatibleDestImage(paramBufferedImage, paramColorModel, colorSpace);
  }
  
  private BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel, ColorSpace paramColorSpace) {
    if (paramColorModel == null) {
      ColorModel colorModel = paramBufferedImage.getColorModel();
      int k = paramColorSpace.getNumComponents();
      boolean bool = colorModel.hasAlpha();
      if (bool)
        k++; 
      int[] arrayOfInt = new int[k];
      for (byte b = 0; b < k; b++)
        arrayOfInt[b] = 8; 
      paramColorModel = new ComponentColorModel(paramColorSpace, arrayOfInt, bool, colorModel.isAlphaPremultiplied(), colorModel.getTransparency(), 0);
    } 
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    return new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster) {
    int i;
    if (this.CSList != null) {
      if (this.CSList.length != 2)
        throw new IllegalArgumentException("Destination ColorSpace is undefined"); 
      i = this.CSList[1].getNumComponents();
    } else {
      int j = this.profileList.length;
      if (j < 2)
        throw new IllegalArgumentException("Destination ColorSpace is undefined"); 
      i = this.profileList[j - 1].getNumComponents();
    } 
    return Raster.createInterleavedRaster(0, paramRaster.getWidth(), paramRaster.getHeight(), i, new Point(paramRaster.getMinX(), paramRaster.getMinY()));
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2) {
    if (paramPoint2D2 == null)
      paramPoint2D2 = new Point2D.Float(); 
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  private int getRenderingIntent(ICC_Profile paramICC_Profile) {
    byte[] arrayOfByte = paramICC_Profile.getData(1751474532);
    byte b = 64;
    return (arrayOfByte[b + 2] & 0xFF) << 8 | arrayOfByte[b + 3] & 0xFF;
  }
  
  public final RenderingHints getRenderingHints() { return this.hints; }
  
  private final BufferedImage nonICCBIFilter(BufferedImage paramBufferedImage1, ColorSpace paramColorSpace1, BufferedImage paramBufferedImage2, ColorSpace paramColorSpace2) {
    int i = paramBufferedImage1.getWidth();
    int j = paramBufferedImage1.getHeight();
    ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1001);
    if (paramBufferedImage2 == null) {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      paramColorSpace2 = paramBufferedImage2.getColorModel().getColorSpace();
    } else if (j != paramBufferedImage2.getHeight() || i != paramBufferedImage2.getWidth()) {
      throw new IllegalArgumentException("Width or height of BufferedImages do not match");
    } 
    WritableRaster writableRaster1 = paramBufferedImage1.getRaster();
    WritableRaster writableRaster2 = paramBufferedImage2.getRaster();
    ColorModel colorModel1 = paramBufferedImage1.getColorModel();
    ColorModel colorModel2 = paramBufferedImage2.getColorModel();
    int k = colorModel1.getNumColorComponents();
    int m = colorModel2.getNumColorComponents();
    boolean bool = colorModel2.hasAlpha();
    boolean bool1 = (colorModel1.hasAlpha() && bool) ? 1 : 0;
    if (this.CSList == null && this.profileList.length != 0) {
      float[] arrayOfFloat5;
      int n;
      ColorSpace colorSpace;
      ICC_Profile iCC_Profile2;
      ICC_Profile iCC_Profile1;
      boolean bool3;
      boolean bool2;
      if (!(paramColorSpace1 instanceof ICC_ColorSpace)) {
        bool2 = true;
        iCC_Profile1 = iCC_ColorSpace.getProfile();
      } else {
        bool2 = false;
        iCC_Profile1 = ((ICC_ColorSpace)paramColorSpace1).getProfile();
      } 
      if (!(paramColorSpace2 instanceof ICC_ColorSpace)) {
        bool3 = true;
        iCC_Profile2 = iCC_ColorSpace.getProfile();
      } else {
        bool3 = false;
        iCC_Profile2 = ((ICC_ColorSpace)paramColorSpace2).getProfile();
      } 
      if (this.thisTransform == null || this.thisSrcProfile != iCC_Profile1 || this.thisDestProfile != iCC_Profile2)
        updateBITransform(iCC_Profile1, iCC_Profile2); 
      float f = 65535.0F;
      if (bool2) {
        colorSpace = iCC_ColorSpace;
        n = 3;
      } else {
        colorSpace = paramColorSpace1;
        n = k;
      } 
      float[] arrayOfFloat1 = new float[n];
      float[] arrayOfFloat2 = new float[n];
      int i1;
      for (i1 = 0; i1 < k; i1++) {
        arrayOfFloat1[i1] = colorSpace.getMinValue(i1);
        arrayOfFloat2[i1] = f / (colorSpace.getMaxValue(i1) - arrayOfFloat1[i1]);
      } 
      if (bool3) {
        colorSpace = iCC_ColorSpace;
        i1 = 3;
      } else {
        colorSpace = paramColorSpace2;
        i1 = m;
      } 
      float[] arrayOfFloat3 = new float[i1];
      float[] arrayOfFloat4 = new float[i1];
      for (byte b1 = 0; b1 < m; b1++) {
        arrayOfFloat3[b1] = colorSpace.getMinValue(b1);
        arrayOfFloat4[b1] = (colorSpace.getMaxValue(b1) - arrayOfFloat3[b1]) / f;
      } 
      if (bool) {
        int i2 = (m + 1 > 3) ? (m + 1) : 3;
        arrayOfFloat5 = new float[i2];
      } else {
        int i2 = (m > 3) ? m : 3;
        arrayOfFloat5 = new float[i2];
      } 
      short[] arrayOfShort1 = new short[i * n];
      short[] arrayOfShort2 = new short[i * i1];
      float[] arrayOfFloat6 = null;
      if (bool1)
        arrayOfFloat6 = new float[i]; 
      for (byte b2 = 0; b2 < j; b2++) {
        Object object = null;
        float[] arrayOfFloat = null;
        byte b3 = 0;
        byte b4;
        for (b4 = 0; b4 < i; b4++) {
          object = writableRaster1.getDataElements(b4, b2, object);
          arrayOfFloat = colorModel1.getNormalizedComponents(object, arrayOfFloat, 0);
          if (bool1)
            arrayOfFloat6[b4] = arrayOfFloat[k]; 
          if (bool2)
            arrayOfFloat = paramColorSpace1.toCIEXYZ(arrayOfFloat); 
          for (byte b = 0; b < n; b++)
            arrayOfShort1[b3++] = (short)(int)((arrayOfFloat[b] - arrayOfFloat1[b]) * arrayOfFloat2[b] + 0.5F); 
        } 
        this.thisTransform.colorConvert(arrayOfShort1, arrayOfShort2);
        object = null;
        b3 = 0;
        for (b4 = 0; b4 < i; b4++) {
          byte b;
          for (b = 0; b < i1; b++)
            arrayOfFloat5[b] = (arrayOfShort2[b3++] & 0xFFFF) * arrayOfFloat4[b] + arrayOfFloat3[b]; 
          if (bool3) {
            arrayOfFloat = paramColorSpace1.fromCIEXYZ(arrayOfFloat5);
            for (b = 0; b < m; b++)
              arrayOfFloat5[b] = arrayOfFloat[b]; 
          } 
          if (bool1) {
            arrayOfFloat5[m] = arrayOfFloat6[b4];
          } else if (bool) {
            arrayOfFloat5[m] = 1.0F;
          } 
          object = colorModel2.getDataElements(arrayOfFloat5, 0, object);
          writableRaster2.setDataElements(b4, b2, object);
        } 
      } 
    } else {
      float[] arrayOfFloat1;
      int n;
      if (this.CSList == null) {
        n = 0;
      } else {
        n = this.CSList.length;
      } 
      if (bool) {
        arrayOfFloat1 = new float[m + 1];
      } else {
        arrayOfFloat1 = new float[m];
      } 
      Object object1 = null;
      Object object2 = null;
      float[] arrayOfFloat2 = null;
      for (byte b = 0; b < j; b++) {
        for (byte b1 = 0; b1 < i; b1++) {
          object1 = writableRaster1.getDataElements(b1, b, object1);
          arrayOfFloat2 = colorModel1.getNormalizedComponents(object1, arrayOfFloat2, 0);
          float[] arrayOfFloat = paramColorSpace1.toCIEXYZ(arrayOfFloat2);
          byte b2;
          for (b2 = 0; b2 < n; b2++) {
            arrayOfFloat = this.CSList[b2].fromCIEXYZ(arrayOfFloat);
            arrayOfFloat = this.CSList[b2].toCIEXYZ(arrayOfFloat);
          } 
          arrayOfFloat = paramColorSpace2.fromCIEXYZ(arrayOfFloat);
          for (b2 = 0; b2 < m; b2++)
            arrayOfFloat1[b2] = arrayOfFloat[b2]; 
          if (bool1) {
            arrayOfFloat1[m] = arrayOfFloat2[k];
          } else if (bool) {
            arrayOfFloat1[m] = 1.0F;
          } 
          object2 = colorModel2.getDataElements(arrayOfFloat1, 0, object2);
          writableRaster2.setDataElements(b1, b, object2);
        } 
      } 
    } 
    return paramBufferedImage2;
  }
  
  private final WritableRaster nonICCRasterFilter(Raster paramRaster, WritableRaster paramWritableRaster) {
    boolean bool2;
    boolean bool1;
    if (this.CSList.length != 2)
      throw new IllegalArgumentException("Destination ColorSpace is undefined"); 
    if (paramRaster.getNumBands() != this.CSList[0].getNumComponents())
      throw new IllegalArgumentException("Numbers of source Raster bands and source color space components do not match"); 
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else {
      if (paramRaster.getHeight() != paramWritableRaster.getHeight() || paramRaster.getWidth() != paramWritableRaster.getWidth())
        throw new IllegalArgumentException("Width or height of Rasters do not match"); 
      if (paramWritableRaster.getNumBands() != this.CSList[1].getNumComponents())
        throw new IllegalArgumentException("Numbers of destination Raster bands and destination color space components do not match"); 
    } 
    if (this.srcMinVals == null)
      getMinMaxValsFromColorSpaces(this.CSList[0], this.CSList[1]); 
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
    float[] arrayOfFloat1 = null;
    float[] arrayOfFloat2 = null;
    if (!bool1) {
      arrayOfFloat1 = new float[n];
      for (byte b1 = 0; b1 < n; b1++) {
        if (i == 2) {
          arrayOfFloat1[b1] = (this.srcMaxVals[b1] - this.srcMinVals[b1]) / 32767.0F;
        } else {
          arrayOfFloat1[b1] = (this.srcMaxVals[b1] - this.srcMinVals[b1]) / ((1 << sampleModel1.getSampleSize(b1)) - 1);
        } 
      } 
    } 
    if (!bool2) {
      arrayOfFloat2 = new float[i1];
      for (byte b1 = 0; b1 < i1; b1++) {
        if (j == 2) {
          arrayOfFloat2[b1] = 32767.0F / (this.dstMaxVals[b1] - this.dstMinVals[b1]);
        } else {
          arrayOfFloat2[b1] = ((1 << sampleModel2.getSampleSize(b1)) - 1) / (this.dstMaxVals[b1] - this.dstMinVals[b1]);
        } 
      } 
    } 
    int i2 = paramRaster.getMinY();
    int i3 = paramWritableRaster.getMinY();
    float[] arrayOfFloat3 = new float[n];
    ColorSpace colorSpace1 = this.CSList[0];
    ColorSpace colorSpace2 = this.CSList[1];
    byte b = 0;
    while (b < m) {
      int i4 = paramRaster.getMinX();
      int i5 = paramWritableRaster.getMinX();
      byte b1 = 0;
      while (b1 < k) {
        byte b2;
        for (b2 = 0; b2 < n; b2++) {
          float f = paramRaster.getSampleFloat(i4, i2, b2);
          if (!bool1)
            f = f * arrayOfFloat1[b2] + this.srcMinVals[b2]; 
          arrayOfFloat3[b2] = f;
        } 
        float[] arrayOfFloat = colorSpace1.toCIEXYZ(arrayOfFloat3);
        arrayOfFloat = colorSpace2.fromCIEXYZ(arrayOfFloat);
        for (b2 = 0; b2 < i1; b2++) {
          float f = arrayOfFloat[b2];
          if (!bool2)
            f = (f - this.dstMinVals[b2]) * arrayOfFloat2[b2]; 
          paramWritableRaster.setSample(i5, i3, b2, f);
        } 
        b1++;
        i4++;
        i5++;
      } 
      b++;
      i2++;
      i3++;
    } 
    return paramWritableRaster;
  }
  
  private void getMinMaxValsFromProfiles(ICC_Profile paramICC_Profile1, ICC_Profile paramICC_Profile2) {
    int i = paramICC_Profile1.getColorSpaceType();
    int j = paramICC_Profile1.getNumComponents();
    this.srcMinVals = new float[j];
    this.srcMaxVals = new float[j];
    setMinMax(i, j, this.srcMinVals, this.srcMaxVals);
    i = paramICC_Profile2.getColorSpaceType();
    j = paramICC_Profile2.getNumComponents();
    this.dstMinVals = new float[j];
    this.dstMaxVals = new float[j];
    setMinMax(i, j, this.dstMinVals, this.dstMaxVals);
  }
  
  private void setMinMax(int paramInt1, int paramInt2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramInt1 == 1) {
      paramArrayOfFloat1[0] = 0.0F;
      paramArrayOfFloat2[0] = 100.0F;
      paramArrayOfFloat1[1] = -128.0F;
      paramArrayOfFloat2[1] = 127.0F;
      paramArrayOfFloat1[2] = -128.0F;
      paramArrayOfFloat2[2] = 127.0F;
    } else if (paramInt1 == 0) {
      paramArrayOfFloat1[2] = 0.0F;
      paramArrayOfFloat1[1] = 0.0F;
      paramArrayOfFloat1[0] = 0.0F;
      paramArrayOfFloat2[2] = 1.9999695F;
      paramArrayOfFloat2[1] = 1.9999695F;
      paramArrayOfFloat2[0] = 1.9999695F;
    } else {
      for (byte b = 0; b < paramInt2; b++) {
        paramArrayOfFloat1[b] = 0.0F;
        paramArrayOfFloat2[b] = 1.0F;
      } 
    } 
  }
  
  private void getMinMaxValsFromColorSpaces(ColorSpace paramColorSpace1, ColorSpace paramColorSpace2) {
    int i = paramColorSpace1.getNumComponents();
    this.srcMinVals = new float[i];
    this.srcMaxVals = new float[i];
    byte b;
    for (b = 0; b < i; b++) {
      this.srcMinVals[b] = paramColorSpace1.getMinValue(b);
      this.srcMaxVals[b] = paramColorSpace1.getMaxValue(b);
    } 
    i = paramColorSpace2.getNumComponents();
    this.dstMinVals = new float[i];
    this.dstMaxVals = new float[i];
    for (b = 0; b < i; b++) {
      this.dstMinVals[b] = paramColorSpace2.getMinValue(b);
      this.dstMaxVals[b] = paramColorSpace2.getMaxValue(b);
    } 
  }
  
  static  {
    if (ProfileDeferralMgr.deferring)
      ProfileDeferralMgr.activateProfiles(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ColorConvertOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */