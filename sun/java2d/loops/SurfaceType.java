package sun.java2d.loops;

import java.awt.image.ColorModel;
import java.util.HashMap;
import sun.awt.image.PixelConverter;

public final class SurfaceType {
  private static int unusedUID = 1;
  
  private static HashMap<String, Integer> surfaceUIDMap = new HashMap(100);
  
  public static final String DESC_ANY = "Any Surface";
  
  public static final String DESC_INT_RGB = "Integer RGB";
  
  public static final String DESC_INT_ARGB = "Integer ARGB";
  
  public static final String DESC_INT_ARGB_PRE = "Integer ARGB Premultiplied";
  
  public static final String DESC_INT_BGR = "Integer BGR";
  
  public static final String DESC_3BYTE_BGR = "3 Byte BGR";
  
  public static final String DESC_4BYTE_ABGR = "4 Byte ABGR";
  
  public static final String DESC_4BYTE_ABGR_PRE = "4 Byte ABGR Premultiplied";
  
  public static final String DESC_USHORT_565_RGB = "Short 565 RGB";
  
  public static final String DESC_USHORT_555_RGB = "Short 555 RGB";
  
  public static final String DESC_USHORT_555_RGBx = "Short 555 RGBx";
  
  public static final String DESC_USHORT_4444_ARGB = "Short 4444 ARGB";
  
  public static final String DESC_BYTE_GRAY = "8-bit Gray";
  
  public static final String DESC_USHORT_INDEXED = "16-bit Indexed";
  
  public static final String DESC_USHORT_GRAY = "16-bit Gray";
  
  public static final String DESC_BYTE_BINARY = "Packed Binary Bitmap";
  
  public static final String DESC_BYTE_INDEXED = "8-bit Indexed";
  
  public static final String DESC_ANY_INT = "Any Discrete Integer";
  
  public static final String DESC_ANY_SHORT = "Any Discrete Short";
  
  public static final String DESC_ANY_BYTE = "Any Discrete Byte";
  
  public static final String DESC_ANY_3BYTE = "Any 3 Byte Component";
  
  public static final String DESC_ANY_4BYTE = "Any 4 Byte Component";
  
  public static final String DESC_ANY_INT_DCM = "Any Integer DCM";
  
  public static final String DESC_INT_RGBx = "Integer RGBx";
  
  public static final String DESC_INT_BGRx = "Integer BGRx";
  
  public static final String DESC_3BYTE_RGB = "3 Byte RGB";
  
  public static final String DESC_INT_ARGB_BM = "Int ARGB (Bitmask)";
  
  public static final String DESC_BYTE_INDEXED_BM = "8-bit Indexed (Bitmask)";
  
  public static final String DESC_BYTE_INDEXED_OPAQUE = "8-bit Indexed (Opaque)";
  
  public static final String DESC_INDEX8_GRAY = "8-bit Palettized Gray";
  
  public static final String DESC_INDEX12_GRAY = "12-bit Palettized Gray";
  
  public static final String DESC_BYTE_BINARY_1BIT = "Packed Binary 1-bit Bitmap";
  
  public static final String DESC_BYTE_BINARY_2BIT = "Packed Binary 2-bit Bitmap";
  
  public static final String DESC_BYTE_BINARY_4BIT = "Packed Binary 4-bit Bitmap";
  
  public static final String DESC_ANY_PAINT = "Paint Object";
  
  public static final String DESC_ANY_COLOR = "Single Color";
  
  public static final String DESC_OPAQUE_COLOR = "Opaque Color";
  
  public static final String DESC_GRADIENT_PAINT = "Gradient Paint";
  
  public static final String DESC_OPAQUE_GRADIENT_PAINT = "Opaque Gradient Paint";
  
  public static final String DESC_TEXTURE_PAINT = "Texture Paint";
  
  public static final String DESC_OPAQUE_TEXTURE_PAINT = "Opaque Texture Paint";
  
  public static final String DESC_LINEAR_GRADIENT_PAINT = "Linear Gradient Paint";
  
  public static final String DESC_OPAQUE_LINEAR_GRADIENT_PAINT = "Opaque Linear Gradient Paint";
  
  public static final String DESC_RADIAL_GRADIENT_PAINT = "Radial Gradient Paint";
  
  public static final String DESC_OPAQUE_RADIAL_GRADIENT_PAINT = "Opaque Radial Gradient Paint";
  
  public static final SurfaceType Any = new SurfaceType(null, "Any Surface", PixelConverter.instance);
  
  public static final SurfaceType AnyInt = Any.deriveSubType("Any Discrete Integer");
  
  public static final SurfaceType AnyShort = Any.deriveSubType("Any Discrete Short");
  
  public static final SurfaceType AnyByte = Any.deriveSubType("Any Discrete Byte");
  
  public static final SurfaceType AnyByteBinary = Any.deriveSubType("Packed Binary Bitmap");
  
  public static final SurfaceType Any3Byte = Any.deriveSubType("Any 3 Byte Component");
  
  public static final SurfaceType Any4Byte = Any.deriveSubType("Any 4 Byte Component");
  
  public static final SurfaceType AnyDcm = AnyInt.deriveSubType("Any Integer DCM");
  
  public static final SurfaceType Custom = Any;
  
  public static final SurfaceType IntRgb = AnyDcm.deriveSubType("Integer RGB", PixelConverter.Xrgb.instance);
  
  public static final SurfaceType IntArgb = AnyDcm.deriveSubType("Integer ARGB", PixelConverter.Argb.instance);
  
  public static final SurfaceType IntArgbPre = AnyDcm.deriveSubType("Integer ARGB Premultiplied", PixelConverter.ArgbPre.instance);
  
  public static final SurfaceType IntBgr = AnyDcm.deriveSubType("Integer BGR", PixelConverter.Xbgr.instance);
  
  public static final SurfaceType ThreeByteBgr = Any3Byte.deriveSubType("3 Byte BGR", PixelConverter.Xrgb.instance);
  
  public static final SurfaceType FourByteAbgr = Any4Byte.deriveSubType("4 Byte ABGR", PixelConverter.Rgba.instance);
  
  public static final SurfaceType FourByteAbgrPre = Any4Byte.deriveSubType("4 Byte ABGR Premultiplied", PixelConverter.RgbaPre.instance);
  
  public static final SurfaceType Ushort565Rgb = AnyShort.deriveSubType("Short 565 RGB", PixelConverter.Ushort565Rgb.instance);
  
  public static final SurfaceType Ushort555Rgb = AnyShort.deriveSubType("Short 555 RGB", PixelConverter.Ushort555Rgb.instance);
  
  public static final SurfaceType Ushort555Rgbx = AnyShort.deriveSubType("Short 555 RGBx", PixelConverter.Ushort555Rgbx.instance);
  
  public static final SurfaceType Ushort4444Argb = AnyShort.deriveSubType("Short 4444 ARGB", PixelConverter.Ushort4444Argb.instance);
  
  public static final SurfaceType UshortIndexed = AnyShort.deriveSubType("16-bit Indexed");
  
  public static final SurfaceType ByteGray = AnyByte.deriveSubType("8-bit Gray", PixelConverter.ByteGray.instance);
  
  public static final SurfaceType UshortGray = AnyShort.deriveSubType("16-bit Gray", PixelConverter.UshortGray.instance);
  
  public static final SurfaceType ByteBinary1Bit = AnyByteBinary.deriveSubType("Packed Binary 1-bit Bitmap");
  
  public static final SurfaceType ByteBinary2Bit = AnyByteBinary.deriveSubType("Packed Binary 2-bit Bitmap");
  
  public static final SurfaceType ByteBinary4Bit = AnyByteBinary.deriveSubType("Packed Binary 4-bit Bitmap");
  
  public static final SurfaceType ByteIndexed = AnyByte.deriveSubType("8-bit Indexed");
  
  public static final SurfaceType IntRgbx = AnyDcm.deriveSubType("Integer RGBx", PixelConverter.Rgbx.instance);
  
  public static final SurfaceType IntBgrx = AnyDcm.deriveSubType("Integer BGRx", PixelConverter.Bgrx.instance);
  
  public static final SurfaceType ThreeByteRgb = Any3Byte.deriveSubType("3 Byte RGB", PixelConverter.Xbgr.instance);
  
  public static final SurfaceType IntArgbBm = AnyDcm.deriveSubType("Int ARGB (Bitmask)", PixelConverter.ArgbBm.instance);
  
  public static final SurfaceType ByteIndexedBm = ByteIndexed.deriveSubType("8-bit Indexed (Bitmask)");
  
  public static final SurfaceType ByteIndexedOpaque = ByteIndexedBm.deriveSubType("8-bit Indexed (Opaque)");
  
  public static final SurfaceType Index8Gray = ByteIndexedOpaque.deriveSubType("8-bit Palettized Gray");
  
  public static final SurfaceType Index12Gray = Any.deriveSubType("12-bit Palettized Gray");
  
  public static final SurfaceType AnyPaint = Any.deriveSubType("Paint Object");
  
  public static final SurfaceType AnyColor = AnyPaint.deriveSubType("Single Color");
  
  public static final SurfaceType OpaqueColor = AnyColor.deriveSubType("Opaque Color");
  
  public static final SurfaceType GradientPaint = AnyPaint.deriveSubType("Gradient Paint");
  
  public static final SurfaceType OpaqueGradientPaint = GradientPaint.deriveSubType("Opaque Gradient Paint");
  
  public static final SurfaceType LinearGradientPaint = AnyPaint.deriveSubType("Linear Gradient Paint");
  
  public static final SurfaceType OpaqueLinearGradientPaint = LinearGradientPaint.deriveSubType("Opaque Linear Gradient Paint");
  
  public static final SurfaceType RadialGradientPaint = AnyPaint.deriveSubType("Radial Gradient Paint");
  
  public static final SurfaceType OpaqueRadialGradientPaint = RadialGradientPaint.deriveSubType("Opaque Radial Gradient Paint");
  
  public static final SurfaceType TexturePaint = AnyPaint.deriveSubType("Texture Paint");
  
  public static final SurfaceType OpaqueTexturePaint = TexturePaint.deriveSubType("Opaque Texture Paint");
  
  private int uniqueID;
  
  private String desc;
  
  private SurfaceType next;
  
  protected PixelConverter pixelConverter;
  
  public SurfaceType deriveSubType(String paramString) { return new SurfaceType(this, paramString); }
  
  public SurfaceType deriveSubType(String paramString, PixelConverter paramPixelConverter) { return new SurfaceType(this, paramString, paramPixelConverter); }
  
  private SurfaceType(SurfaceType paramSurfaceType, String paramString, PixelConverter paramPixelConverter) {
    this.next = paramSurfaceType;
    this.desc = paramString;
    this.uniqueID = makeUniqueID(paramString);
    this.pixelConverter = paramPixelConverter;
  }
  
  private SurfaceType(SurfaceType paramSurfaceType, String paramString) {
    this.next = paramSurfaceType;
    this.desc = paramString;
    this.uniqueID = makeUniqueID(paramString);
    this.pixelConverter = paramSurfaceType.pixelConverter;
  }
  
  public static final int makeUniqueID(String paramString) {
    Integer integer = (Integer)surfaceUIDMap.get(paramString);
    if (integer == null) {
      if (unusedUID > 255)
        throw new InternalError("surface type id overflow"); 
      integer = Integer.valueOf(unusedUID++);
      surfaceUIDMap.put(paramString, integer);
    } 
    return integer.intValue();
  }
  
  public int getUniqueID() { return this.uniqueID; }
  
  public String getDescriptor() { return this.desc; }
  
  public SurfaceType getSuperType() { return this.next; }
  
  public PixelConverter getPixelConverter() { return this.pixelConverter; }
  
  public int pixelFor(int paramInt, ColorModel paramColorModel) { return this.pixelConverter.rgbToPixel(paramInt, paramColorModel); }
  
  public int rgbFor(int paramInt, ColorModel paramColorModel) { return this.pixelConverter.pixelToRgb(paramInt, paramColorModel); }
  
  public int getAlphaMask() { return this.pixelConverter.getAlphaMask(); }
  
  public int hashCode() { return this.desc.hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof SurfaceType) ? ((((SurfaceType)paramObject).uniqueID == this.uniqueID)) : false; }
  
  public String toString() { return this.desc; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\SurfaceType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */