package java.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.OffScreenImageSource;
import sun.awt.image.ShortComponentRaster;

public class BufferedImage extends Image implements WritableRenderedImage, Transparency {
  private int imageType = 0;
  
  private ColorModel colorModel;
  
  private final WritableRaster raster;
  
  private OffScreenImageSource osis;
  
  private Hashtable<String, Object> properties;
  
  public static final int TYPE_CUSTOM = 0;
  
  public static final int TYPE_INT_RGB = 1;
  
  public static final int TYPE_INT_ARGB = 2;
  
  public static final int TYPE_INT_ARGB_PRE = 3;
  
  public static final int TYPE_INT_BGR = 4;
  
  public static final int TYPE_3BYTE_BGR = 5;
  
  public static final int TYPE_4BYTE_ABGR = 6;
  
  public static final int TYPE_4BYTE_ABGR_PRE = 7;
  
  public static final int TYPE_USHORT_565_RGB = 8;
  
  public static final int TYPE_USHORT_555_RGB = 9;
  
  public static final int TYPE_BYTE_GRAY = 10;
  
  public static final int TYPE_USHORT_GRAY = 11;
  
  public static final int TYPE_BYTE_BINARY = 12;
  
  public static final int TYPE_BYTE_INDEXED = 13;
  
  private static final int DCM_RED_MASK = 16711680;
  
  private static final int DCM_GREEN_MASK = 65280;
  
  private static final int DCM_BLUE_MASK = 255;
  
  private static final int DCM_ALPHA_MASK = -16777216;
  
  private static final int DCM_565_RED_MASK = 63488;
  
  private static final int DCM_565_GRN_MASK = 2016;
  
  private static final int DCM_565_BLU_MASK = 31;
  
  private static final int DCM_555_RED_MASK = 31744;
  
  private static final int DCM_555_GRN_MASK = 992;
  
  private static final int DCM_555_BLU_MASK = 31;
  
  private static final int DCM_BGR_RED_MASK = 255;
  
  private static final int DCM_BGR_GRN_MASK = 65280;
  
  private static final int DCM_BGR_BLU_MASK = 16711680;
  
  private static native void initIDs();
  
  public BufferedImage(int paramInt1, int paramInt2, int paramInt3) {
    switch (paramInt3) {
      case 1:
        this.colorModel = new DirectColorModel(24, 16711680, 65280, 255, 0);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 2:
        this.colorModel = ColorModel.getRGBdefault();
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 3:
        this.colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 4:
        this.colorModel = new DirectColorModel(24, 255, 65280, 16711680);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 5:
        colorSpace = ColorSpace.getInstance(1000);
        arrayOfInt2 = new int[] { 8, 8, 8 };
        arrayOfInt3 = new int[] { 2, 1, 0 };
        this.colorModel = new ComponentColorModel(colorSpace, arrayOfInt2, false, false, 1, 0);
        this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 3, 3, arrayOfInt3, null);
        break;
      case 6:
        colorSpace = ColorSpace.getInstance(1000);
        arrayOfInt2 = new int[] { 8, 8, 8, 8 };
        arrayOfInt3 = new int[] { 3, 2, 1, 0 };
        this.colorModel = new ComponentColorModel(colorSpace, arrayOfInt2, true, false, 3, 0);
        this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 4, 4, arrayOfInt3, null);
        break;
      case 7:
        colorSpace = ColorSpace.getInstance(1000);
        arrayOfInt2 = new int[] { 8, 8, 8, 8 };
        arrayOfInt3 = new int[] { 3, 2, 1, 0 };
        this.colorModel = new ComponentColorModel(colorSpace, arrayOfInt2, true, true, 3, 0);
        this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 4, 4, arrayOfInt3, null);
        break;
      case 10:
        colorSpace = ColorSpace.getInstance(1003);
        arrayOfInt2 = new int[] { 8 };
        this.colorModel = new ComponentColorModel(colorSpace, arrayOfInt2, false, true, 1, 0);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 11:
        colorSpace = ColorSpace.getInstance(1003);
        arrayOfInt2 = new int[] { 16 };
        this.colorModel = new ComponentColorModel(colorSpace, arrayOfInt2, false, true, 1, 1);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 12:
        arrayOfByte = new byte[] { 0, -1 };
        this.colorModel = new IndexColorModel(1, 2, arrayOfByte, arrayOfByte, arrayOfByte);
        this.raster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, 1, null);
        break;
      case 13:
        arrayOfInt1 = new int[256];
        b = 0;
        for (c1 = Character.MIN_VALUE; c1 < 'Ā'; c1 += true) {
          for (boolean bool = false; bool < 'Ā'; bool += true) {
            for (byte b1 = 0; b1 < 'Ā'; b1 += 51)
              arrayOfInt1[b++] = c1 << 16 | bool << 8 | b1; 
          } 
        } 
        c1 = 'Ā' / ('Ā' - b);
        c2 = c1 * '\003';
        while (b < 'Ā') {
          arrayOfInt1[b] = c2 << '\020' | c2 << '\b' | c2;
          c2 += c1;
          b++;
        } 
        this.colorModel = new IndexColorModel(8, 256, arrayOfInt1, 0, false, -1, 0);
        this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
        break;
      case 8:
        this.colorModel = new DirectColorModel(16, 63488, 2016, 31);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      case 9:
        this.colorModel = new DirectColorModel(15, 31744, 992, 31);
        this.raster = this.colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
        break;
      default:
        throw new IllegalArgumentException("Unknown image type " + paramInt3);
    } 
    this.imageType = paramInt3;
  }
  
  public BufferedImage(int paramInt1, int paramInt2, int paramInt3, IndexColorModel paramIndexColorModel) {
    if (paramIndexColorModel.hasAlpha() && paramIndexColorModel.isAlphaPremultiplied())
      throw new IllegalArgumentException("This image types do not have premultiplied alpha."); 
    switch (paramInt3) {
      case 12:
        i = paramIndexColorModel.getMapSize();
        if (i <= 2) {
          b = 1;
        } else if (i <= 4) {
          b = 2;
        } else if (i <= 16) {
          b = 4;
        } else {
          throw new IllegalArgumentException("Color map for TYPE_BYTE_BINARY must have no more than 16 entries");
        } 
        this.raster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, b, null);
        break;
      case 13:
        this.raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
        break;
      default:
        throw new IllegalArgumentException("Invalid image type (" + paramInt3 + ").  Image type must be either TYPE_BYTE_BINARY or  TYPE_BYTE_INDEXED");
    } 
    if (!paramIndexColorModel.isCompatibleRaster(this.raster))
      throw new IllegalArgumentException("Incompatible image type and IndexColorModel"); 
    this.colorModel = paramIndexColorModel;
    this.imageType = paramInt3;
  }
  
  public BufferedImage(ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean, Hashtable<?, ?> paramHashtable) {
    if (!paramColorModel.isCompatibleRaster(paramWritableRaster))
      throw new IllegalArgumentException("Raster " + paramWritableRaster + " is incompatible with ColorModel " + paramColorModel); 
    if (paramWritableRaster.minX != 0 || paramWritableRaster.minY != 0)
      throw new IllegalArgumentException("Raster " + paramWritableRaster + " has minX or minY not equal to zero: " + paramWritableRaster.minX + " " + paramWritableRaster.minY); 
    this.colorModel = paramColorModel;
    this.raster = paramWritableRaster;
    if (paramHashtable != null && !paramHashtable.isEmpty()) {
      this.properties = new Hashtable();
      for (Object object : paramHashtable.keySet()) {
        if (object instanceof String)
          this.properties.put((String)object, paramHashtable.get(object)); 
      } 
    } 
    int i = paramWritableRaster.getNumBands();
    boolean bool1 = paramColorModel.isAlphaPremultiplied();
    boolean bool2 = isStandard(paramColorModel, paramWritableRaster);
    coerceData(paramBoolean);
    SampleModel sampleModel = paramWritableRaster.getSampleModel();
    ColorSpace colorSpace = paramColorModel.getColorSpace();
    int j = colorSpace.getType();
    if (j != 5) {
      if (j == 6 && bool2 && paramColorModel instanceof ComponentColorModel) {
        if (sampleModel instanceof ComponentSampleModel && ((ComponentSampleModel)sampleModel).getPixelStride() != i) {
          this.imageType = 0;
        } else if (paramWritableRaster instanceof ByteComponentRaster && paramWritableRaster.getNumBands() == 1 && paramColorModel.getComponentSize(0) == 8 && ((ByteComponentRaster)paramWritableRaster).getPixelStride() == 1) {
          this.imageType = 10;
        } else if (paramWritableRaster instanceof ShortComponentRaster && paramWritableRaster.getNumBands() == 1 && paramColorModel.getComponentSize(0) == 16 && ((ShortComponentRaster)paramWritableRaster).getPixelStride() == 1) {
          this.imageType = 11;
        } 
      } else {
        this.imageType = 0;
      } 
      return;
    } 
    if (paramWritableRaster instanceof IntegerComponentRaster && (i == 3 || i == 4)) {
      IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)paramWritableRaster;
      int k = paramColorModel.getPixelSize();
      if (integerComponentRaster.getPixelStride() == 1 && bool2 && paramColorModel instanceof DirectColorModel && (k == 32 || k == 24)) {
        DirectColorModel directColorModel = (DirectColorModel)paramColorModel;
        int m = directColorModel.getRedMask();
        int n = directColorModel.getGreenMask();
        int i1 = directColorModel.getBlueMask();
        if (m == 16711680 && n == 65280 && i1 == 255) {
          if (directColorModel.getAlphaMask() == -16777216) {
            this.imageType = bool1 ? 3 : 2;
          } else if (!directColorModel.hasAlpha()) {
            this.imageType = 1;
          } 
        } else if (m == 255 && n == 65280 && i1 == 16711680 && !directColorModel.hasAlpha()) {
          this.imageType = 4;
        } 
      } 
    } else if (paramColorModel instanceof IndexColorModel && i == 1 && bool2 && (!paramColorModel.hasAlpha() || !bool1)) {
      IndexColorModel indexColorModel = (IndexColorModel)paramColorModel;
      int k = indexColorModel.getPixelSize();
      if (paramWritableRaster instanceof sun.awt.image.BytePackedRaster) {
        this.imageType = 12;
      } else if (paramWritableRaster instanceof ByteComponentRaster) {
        ByteComponentRaster byteComponentRaster = (ByteComponentRaster)paramWritableRaster;
        if (byteComponentRaster.getPixelStride() == 1 && k <= 8)
          this.imageType = 13; 
      } 
    } else if (paramWritableRaster instanceof ShortComponentRaster && paramColorModel instanceof DirectColorModel && bool2 && i == 3 && !paramColorModel.hasAlpha()) {
      DirectColorModel directColorModel = (DirectColorModel)paramColorModel;
      if (directColorModel.getRedMask() == 63488) {
        if (directColorModel.getGreenMask() == 2016 && directColorModel.getBlueMask() == 31)
          this.imageType = 8; 
      } else if (directColorModel.getRedMask() == 31744 && directColorModel.getGreenMask() == 992 && directColorModel.getBlueMask() == 31) {
        this.imageType = 9;
      } 
    } else if (paramWritableRaster instanceof ByteComponentRaster && paramColorModel instanceof ComponentColorModel && bool2 && paramWritableRaster.getSampleModel() instanceof PixelInterleavedSampleModel && (i == 3 || i == 4)) {
      ComponentColorModel componentColorModel = (ComponentColorModel)paramColorModel;
      PixelInterleavedSampleModel pixelInterleavedSampleModel = (PixelInterleavedSampleModel)paramWritableRaster.getSampleModel();
      ByteComponentRaster byteComponentRaster = (ByteComponentRaster)paramWritableRaster;
      int[] arrayOfInt1 = pixelInterleavedSampleModel.getBandOffsets();
      if (componentColorModel.getNumComponents() != i)
        throw new RasterFormatException("Number of components in ColorModel (" + componentColorModel.getNumComponents() + ") does not match # in  Raster (" + i + ")"); 
      int[] arrayOfInt2 = componentColorModel.getComponentSize();
      boolean bool = true;
      for (byte b = 0; b < i; b++) {
        if (arrayOfInt2[b] != 8) {
          bool = false;
          break;
        } 
      } 
      if (bool && byteComponentRaster.getPixelStride() == i && arrayOfInt1[0] == i - 1 && arrayOfInt1[1] == i - 2 && arrayOfInt1[2] == i - 3)
        if (i == 3 && !componentColorModel.hasAlpha()) {
          this.imageType = 5;
        } else if (arrayOfInt1[3] == 0 && componentColorModel.hasAlpha()) {
          this.imageType = bool1 ? 7 : 6;
        }  
    } 
  }
  
  private static boolean isStandard(ColorModel paramColorModel, WritableRaster paramWritableRaster) {
    final Class cmClass = paramColorModel.getClass();
    final Class wrClass = paramWritableRaster.getClass();
    final Class smClass = paramWritableRaster.getSampleModel().getClass();
    PrivilegedAction<Boolean> privilegedAction = new PrivilegedAction<Boolean>() {
        public Boolean run() {
          ClassLoader classLoader = System.class.getClassLoader();
          return Boolean.valueOf((cmClass.getClassLoader() == classLoader && smClass.getClassLoader() == classLoader && wrClass.getClassLoader() == classLoader));
        }
      };
    return ((Boolean)AccessController.doPrivileged(privilegedAction)).booleanValue();
  }
  
  public int getType() { return this.imageType; }
  
  public ColorModel getColorModel() { return this.colorModel; }
  
  public WritableRaster getRaster() { return this.raster; }
  
  public WritableRaster getAlphaRaster() { return this.colorModel.getAlphaRaster(this.raster); }
  
  public int getRGB(int paramInt1, int paramInt2) { return this.colorModel.getRGB(this.raster.getDataElements(paramInt1, paramInt2, null)); }
  
  public int[] getRGB(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    double[] arrayOfDouble;
    float[] arrayOfFloat;
    int i = paramInt5;
    int j = this.raster.getNumBands();
    int k = this.raster.getDataBuffer().getDataType();
    switch (k) {
      case 0:
        arrayOfByte = new byte[j];
        break;
      case 1:
        arrayOfShort = new short[j];
        break;
      case 3:
        arrayOfInt = new int[j];
        break;
      case 4:
        arrayOfFloat = new float[j];
        break;
      case 5:
        arrayOfDouble = new double[j];
        break;
      default:
        throw new IllegalArgumentException("Unknown data buffer type: " + k);
    } 
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt5 + paramInt4 * paramInt6]; 
    int m = paramInt2;
    while (m < paramInt2 + paramInt4) {
      int n = i;
      for (int i1 = paramInt1; i1 < paramInt1 + paramInt3; i1++)
        paramArrayOfInt[n++] = this.colorModel.getRGB(this.raster.getDataElements(i1, m, arrayOfDouble)); 
      m++;
      i += paramInt6;
    } 
    return paramArrayOfInt;
  }
  
  public void setRGB(int paramInt1, int paramInt2, int paramInt3) { this.raster.setDataElements(paramInt1, paramInt2, this.colorModel.getDataElements(paramInt3, null)); }
  
  public void setRGB(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int i = paramInt5;
    Object object = null;
    int j = paramInt2;
    while (j < paramInt2 + paramInt4) {
      int k = i;
      for (int m = paramInt1; m < paramInt1 + paramInt3; m++) {
        object = this.colorModel.getDataElements(paramArrayOfInt[k++], object);
        this.raster.setDataElements(m, j, object);
      } 
      j++;
      i += paramInt6;
    } 
  }
  
  public int getWidth() { return this.raster.getWidth(); }
  
  public int getHeight() { return this.raster.getHeight(); }
  
  public int getWidth(ImageObserver paramImageObserver) { return this.raster.getWidth(); }
  
  public int getHeight(ImageObserver paramImageObserver) { return this.raster.getHeight(); }
  
  public ImageProducer getSource() {
    if (this.osis == null) {
      if (this.properties == null)
        this.properties = new Hashtable(); 
      this.osis = new OffScreenImageSource(this, this.properties);
    } 
    return this.osis;
  }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver) { return getProperty(paramString); }
  
  public Object getProperty(String paramString) {
    if (paramString == null)
      throw new NullPointerException("null property name is not allowed"); 
    if (this.properties == null)
      return Image.UndefinedProperty; 
    Object object = this.properties.get(paramString);
    if (object == null)
      object = Image.UndefinedProperty; 
    return object;
  }
  
  public Graphics getGraphics() { return createGraphics(); }
  
  public Graphics2D createGraphics() {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return graphicsEnvironment.createGraphics(this);
  }
  
  public BufferedImage getSubimage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new BufferedImage(this.colorModel, this.raster.createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, 0, 0, null), this.colorModel.isAlphaPremultiplied(), this.properties); }
  
  public boolean isAlphaPremultiplied() { return this.colorModel.isAlphaPremultiplied(); }
  
  public void coerceData(boolean paramBoolean) {
    if (this.colorModel.hasAlpha() && this.colorModel.isAlphaPremultiplied() != paramBoolean)
      this.colorModel = this.colorModel.coerceData(this.raster, paramBoolean); 
  }
  
  public String toString() { return "BufferedImage@" + Integer.toHexString(hashCode()) + ": type = " + this.imageType + " " + this.colorModel + " " + this.raster; }
  
  public Vector<RenderedImage> getSources() { return null; }
  
  public String[] getPropertyNames() {
    if (this.properties == null || this.properties.isEmpty())
      return null; 
    Set set = this.properties.keySet();
    return (String[])set.toArray(new String[set.size()]);
  }
  
  public int getMinX() { return this.raster.getMinX(); }
  
  public int getMinY() { return this.raster.getMinY(); }
  
  public SampleModel getSampleModel() { return this.raster.getSampleModel(); }
  
  public int getNumXTiles() { return 1; }
  
  public int getNumYTiles() { return 1; }
  
  public int getMinTileX() { return 0; }
  
  public int getMinTileY() { return 0; }
  
  public int getTileWidth() { return this.raster.getWidth(); }
  
  public int getTileHeight() { return this.raster.getHeight(); }
  
  public int getTileGridXOffset() { return this.raster.getSampleModelTranslateX(); }
  
  public int getTileGridYOffset() { return this.raster.getSampleModelTranslateY(); }
  
  public Raster getTile(int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == 0)
      return this.raster; 
    throw new ArrayIndexOutOfBoundsException("BufferedImages only have one tile with index 0,0");
  }
  
  public Raster getData() {
    int i = this.raster.getWidth();
    int j = this.raster.getHeight();
    int k = this.raster.getMinX();
    int m = this.raster.getMinY();
    WritableRaster writableRaster = Raster.createWritableRaster(this.raster.getSampleModel(), new Point(this.raster.getSampleModelTranslateX(), this.raster.getSampleModelTranslateY()));
    Object object = null;
    for (int n = m; n < m + j; n++) {
      object = this.raster.getDataElements(k, n, i, 1, object);
      writableRaster.setDataElements(k, n, i, 1, object);
    } 
    return writableRaster;
  }
  
  public Raster getData(Rectangle paramRectangle) {
    SampleModel sampleModel1 = this.raster.getSampleModel();
    SampleModel sampleModel2 = sampleModel1.createCompatibleSampleModel(paramRectangle.width, paramRectangle.height);
    WritableRaster writableRaster = Raster.createWritableRaster(sampleModel2, paramRectangle.getLocation());
    int i = paramRectangle.width;
    int j = paramRectangle.height;
    int k = paramRectangle.x;
    int m = paramRectangle.y;
    Object object = null;
    for (int n = m; n < m + j; n++) {
      object = this.raster.getDataElements(k, n, i, 1, object);
      writableRaster.setDataElements(k, n, i, 1, object);
    } 
    return writableRaster;
  }
  
  public WritableRaster copyData(WritableRaster paramWritableRaster) {
    if (paramWritableRaster == null)
      return (WritableRaster)getData(); 
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = paramWritableRaster.getMinX();
    int m = paramWritableRaster.getMinY();
    Object object = null;
    for (int n = m; n < m + j; n++) {
      object = this.raster.getDataElements(k, n, i, 1, object);
      paramWritableRaster.setDataElements(k, n, i, 1, object);
    } 
    return paramWritableRaster;
  }
  
  public void setData(Raster paramRaster) {
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int[] arrayOfInt = null;
    Rectangle rectangle1 = new Rectangle(k, m, i, j);
    Rectangle rectangle2 = new Rectangle(0, 0, this.raster.width, this.raster.height);
    Rectangle rectangle3 = rectangle1.intersection(rectangle2);
    if (rectangle3.isEmpty())
      return; 
    i = rectangle3.width;
    j = rectangle3.height;
    k = rectangle3.x;
    m = rectangle3.y;
    for (int n = m; n < m + j; n++) {
      arrayOfInt = paramRaster.getPixels(k, n, i, 1, arrayOfInt);
      this.raster.setPixels(k, n, i, 1, arrayOfInt);
    } 
  }
  
  public void addTileObserver(TileObserver paramTileObserver) {}
  
  public void removeTileObserver(TileObserver paramTileObserver) {}
  
  public boolean isTileWritable(int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == 0)
      return true; 
    throw new IllegalArgumentException("Only 1 tile in image");
  }
  
  public Point[] getWritableTileIndices() {
    Point[] arrayOfPoint = new Point[1];
    arrayOfPoint[0] = new Point(0, 0);
    return arrayOfPoint;
  }
  
  public boolean hasTileWriters() { return true; }
  
  public WritableRaster getWritableTile(int paramInt1, int paramInt2) { return this.raster; }
  
  public void releaseWritableTile(int paramInt1, int paramInt2) {}
  
  public int getTransparency() { return this.colorModel.getTransparency(); }
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\BufferedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */