package com.sun.imageio.plugins.png;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class PNGMetadataFormat extends IIOMetadataFormatImpl {
  private static IIOMetadataFormat instance = null;
  
  private static String VALUE_0 = "0";
  
  private static String VALUE_1 = "1";
  
  private static String VALUE_12 = "12";
  
  private static String VALUE_23 = "23";
  
  private static String VALUE_31 = "31";
  
  private static String VALUE_59 = "59";
  
  private static String VALUE_60 = "60";
  
  private static String VALUE_255 = "255";
  
  private static String VALUE_MAX_16 = "65535";
  
  private static String VALUE_MAX_32 = "2147483647";
  
  private PNGMetadataFormat() {
    super("javax_imageio_png_1.0", 2);
    addElement("IHDR", "javax_imageio_png_1.0", 0);
    addAttribute("IHDR", "width", 2, true, null, VALUE_1, VALUE_MAX_32, true, true);
    addAttribute("IHDR", "height", 2, true, null, VALUE_1, VALUE_MAX_32, true, true);
    addAttribute("IHDR", "bitDepth", 2, true, null, Arrays.asList(PNGMetadata.IHDR_bitDepths));
    String[] arrayOfString = { "Grayscale", "RGB", "Palette", "GrayAlpha", "RGBAlpha" };
    addAttribute("IHDR", "colorType", 0, true, null, Arrays.asList(arrayOfString));
    addAttribute("IHDR", "compressionMethod", 0, true, null, Arrays.asList(PNGMetadata.IHDR_compressionMethodNames));
    addAttribute("IHDR", "filterMethod", 0, true, null, Arrays.asList(PNGMetadata.IHDR_filterMethodNames));
    addAttribute("IHDR", "interlaceMethod", 0, true, null, Arrays.asList(PNGMetadata.IHDR_interlaceMethodNames));
    addElement("PLTE", "javax_imageio_png_1.0", 1, 256);
    addElement("PLTEEntry", "PLTE", 0);
    addAttribute("PLTEEntry", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("PLTEEntry", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("PLTEEntry", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("PLTEEntry", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("bKGD", "javax_imageio_png_1.0", 3);
    addElement("bKGD_Grayscale", "bKGD", 0);
    addAttribute("bKGD_Grayscale", "gray", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addElement("bKGD_RGB", "bKGD", 0);
    addAttribute("bKGD_RGB", "red", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("bKGD_RGB", "green", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("bKGD_RGB", "blue", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addElement("bKGD_Palette", "bKGD", 0);
    addAttribute("bKGD_Palette", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("cHRM", "javax_imageio_png_1.0", 0);
    addAttribute("cHRM", "whitePointX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "whitePointY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "redX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "redY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "greenX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "greenY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "blueX", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("cHRM", "blueY", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addElement("gAMA", "javax_imageio_png_1.0", 0);
    addAttribute("gAMA", "value", 2, true, null, VALUE_0, VALUE_MAX_32, true, true);
    addElement("hIST", "javax_imageio_png_1.0", 1, 256);
    addElement("hISTEntry", "hIST", 0);
    addAttribute("hISTEntry", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("hISTEntry", "value", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addElement("iCCP", "javax_imageio_png_1.0", 0);
    addAttribute("iCCP", "profileName", 0, true, null);
    addAttribute("iCCP", "compressionMethod", 0, true, null, Arrays.asList(PNGMetadata.iCCP_compressionMethodNames));
    addObjectValue("iCCP", byte.class, 0, 2147483647);
    addElement("iTXt", "javax_imageio_png_1.0", 1, 2147483647);
    addElement("iTXtEntry", "iTXt", 0);
    addAttribute("iTXtEntry", "keyword", 0, true, null);
    addBooleanAttribute("iTXtEntry", "compressionFlag", false, false);
    addAttribute("iTXtEntry", "compressionMethod", 0, true, null);
    addAttribute("iTXtEntry", "languageTag", 0, true, null);
    addAttribute("iTXtEntry", "translatedKeyword", 0, true, null);
    addAttribute("iTXtEntry", "text", 0, true, null);
    addElement("pHYS", "javax_imageio_png_1.0", 0);
    addAttribute("pHYS", "pixelsPerUnitXAxis", 2, true, null, VALUE_0, VALUE_MAX_32, true, true);
    addAttribute("pHYS", "pixelsPerUnitYAxis", 2, true, null, VALUE_0, VALUE_MAX_32, true, true);
    addAttribute("pHYS", "unitSpecifier", 0, true, null, Arrays.asList(PNGMetadata.unitSpecifierNames));
    addElement("sBIT", "javax_imageio_png_1.0", 3);
    addElement("sBIT_Grayscale", "sBIT", 0);
    addAttribute("sBIT_Grayscale", "gray", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("sBIT_GrayAlpha", "sBIT", 0);
    addAttribute("sBIT_GrayAlpha", "gray", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_GrayAlpha", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("sBIT_RGB", "sBIT", 0);
    addAttribute("sBIT_RGB", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_RGB", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_RGB", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("sBIT_RGBAlpha", "sBIT", 0);
    addAttribute("sBIT_RGBAlpha", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_RGBAlpha", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_RGBAlpha", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_RGBAlpha", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("sBIT_Palette", "sBIT", 0);
    addAttribute("sBIT_Palette", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_Palette", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sBIT_Palette", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("sPLT", "javax_imageio_png_1.0", 1, 256);
    addElement("sPLTEntry", "sPLT", 0);
    addAttribute("sPLTEntry", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sPLTEntry", "red", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sPLTEntry", "green", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sPLTEntry", "blue", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("sPLTEntry", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("sRGB", "javax_imageio_png_1.0", 0);
    addAttribute("sRGB", "renderingIntent", 0, true, null, Arrays.asList(PNGMetadata.renderingIntentNames));
    addElement("tEXt", "javax_imageio_png_1.0", 1, 2147483647);
    addElement("tEXtEntry", "tEXt", 0);
    addAttribute("tEXtEntry", "keyword", 0, true, null);
    addAttribute("tEXtEntry", "value", 0, true, null);
    addElement("tIME", "javax_imageio_png_1.0", 0);
    addAttribute("tIME", "year", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("tIME", "month", 2, true, null, VALUE_1, VALUE_12, true, true);
    addAttribute("tIME", "day", 2, true, null, VALUE_1, VALUE_31, true, true);
    addAttribute("tIME", "hour", 2, true, null, VALUE_0, VALUE_23, true, true);
    addAttribute("tIME", "minute", 2, true, null, VALUE_0, VALUE_59, true, true);
    addAttribute("tIME", "second", 2, true, null, VALUE_0, VALUE_60, true, true);
    addElement("tRNS", "javax_imageio_png_1.0", 3);
    addElement("tRNS_Grayscale", "tRNS", 0);
    addAttribute("tRNS_Grayscale", "gray", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addElement("tRNS_RGB", "tRNS", 0);
    addAttribute("tRNS_RGB", "red", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("tRNS_RGB", "green", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addAttribute("tRNS_RGB", "blue", 2, true, null, VALUE_0, VALUE_MAX_16, true, true);
    addElement("tRNS_Palette", "tRNS", 0);
    addAttribute("tRNS_Palette", "index", 2, true, null, VALUE_0, VALUE_255, true, true);
    addAttribute("tRNS_Palette", "alpha", 2, true, null, VALUE_0, VALUE_255, true, true);
    addElement("zTXt", "javax_imageio_png_1.0", 1, 2147483647);
    addElement("zTXtEntry", "zTXt", 0);
    addAttribute("zTXtEntry", "keyword", 0, true, null);
    addAttribute("zTXtEntry", "compressionMethod", 0, true, null, Arrays.asList(PNGMetadata.zTXt_compressionMethodNames));
    addAttribute("zTXtEntry", "text", 0, true, null);
    addElement("UnknownChunks", "javax_imageio_png_1.0", 1, 2147483647);
    addElement("UnknownChunk", "UnknownChunks", 0);
    addAttribute("UnknownChunk", "type", 0, true, null);
    addObjectValue("UnknownChunk", byte.class, 0, 2147483647);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return true; }
  
  public static IIOMetadataFormat getInstance() {
    if (instance == null)
      instance = new PNGMetadataFormat(); 
    return instance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\PNGMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */