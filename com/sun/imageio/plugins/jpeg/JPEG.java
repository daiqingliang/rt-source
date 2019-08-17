package com.sun.imageio.plugins.jpeg;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;

public class JPEG {
  public static final int TEM = 1;
  
  public static final int SOF0 = 192;
  
  public static final int SOF1 = 193;
  
  public static final int SOF2 = 194;
  
  public static final int SOF3 = 195;
  
  public static final int DHT = 196;
  
  public static final int SOF5 = 197;
  
  public static final int SOF6 = 198;
  
  public static final int SOF7 = 199;
  
  public static final int JPG = 200;
  
  public static final int SOF9 = 201;
  
  public static final int SOF10 = 202;
  
  public static final int SOF11 = 203;
  
  public static final int DAC = 204;
  
  public static final int SOF13 = 205;
  
  public static final int SOF14 = 206;
  
  public static final int SOF15 = 207;
  
  public static final int RST0 = 208;
  
  public static final int RST1 = 209;
  
  public static final int RST2 = 210;
  
  public static final int RST3 = 211;
  
  public static final int RST4 = 212;
  
  public static final int RST5 = 213;
  
  public static final int RST6 = 214;
  
  public static final int RST7 = 215;
  
  public static final int RESTART_RANGE = 8;
  
  public static final int SOI = 216;
  
  public static final int EOI = 217;
  
  public static final int SOS = 218;
  
  public static final int DQT = 219;
  
  public static final int DNL = 220;
  
  public static final int DRI = 221;
  
  public static final int DHP = 222;
  
  public static final int EXP = 223;
  
  public static final int APP0 = 224;
  
  public static final int APP1 = 225;
  
  public static final int APP2 = 226;
  
  public static final int APP3 = 227;
  
  public static final int APP4 = 228;
  
  public static final int APP5 = 229;
  
  public static final int APP6 = 230;
  
  public static final int APP7 = 231;
  
  public static final int APP8 = 232;
  
  public static final int APP9 = 233;
  
  public static final int APP10 = 234;
  
  public static final int APP11 = 235;
  
  public static final int APP12 = 236;
  
  public static final int APP13 = 237;
  
  public static final int APP14 = 238;
  
  public static final int APP15 = 239;
  
  public static final int COM = 254;
  
  public static final int DENSITY_UNIT_ASPECT_RATIO = 0;
  
  public static final int DENSITY_UNIT_DOTS_INCH = 1;
  
  public static final int DENSITY_UNIT_DOTS_CM = 2;
  
  public static final int NUM_DENSITY_UNIT = 3;
  
  public static final int ADOBE_IMPOSSIBLE = -1;
  
  public static final int ADOBE_UNKNOWN = 0;
  
  public static final int ADOBE_YCC = 1;
  
  public static final int ADOBE_YCCK = 2;
  
  public static final String vendor = "Oracle Corporation";
  
  public static final String version = "0.5";
  
  static final String[] names = { "JPEG", "jpeg", "JPG", "jpg" };
  
  static final String[] suffixes = { "jpg", "jpeg" };
  
  static final String[] MIMETypes = { "image/jpeg" };
  
  public static final String nativeImageMetadataFormatName = "javax_imageio_jpeg_image_1.0";
  
  public static final String nativeImageMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat";
  
  public static final String nativeStreamMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
  
  public static final String nativeStreamMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
  
  public static final int JCS_UNKNOWN = 0;
  
  public static final int JCS_GRAYSCALE = 1;
  
  public static final int JCS_RGB = 2;
  
  public static final int JCS_YCbCr = 3;
  
  public static final int JCS_CMYK = 4;
  
  public static final int JCS_YCC = 5;
  
  public static final int JCS_RGBA = 6;
  
  public static final int JCS_YCbCrA = 7;
  
  public static final int JCS_YCCA = 10;
  
  public static final int JCS_YCCK = 11;
  
  public static final int NUM_JCS_CODES = 12;
  
  static final int[][] bandOffsets = { { 0 }, { 0, 1 }, { 0, 1, 2 }, { 0, 1, 2, 3 } };
  
  static final int[] bOffsRGB = { 2, 1, 0 };
  
  public static final float DEFAULT_QUALITY = 0.75F;
  
  static boolean isNonStandardICC(ColorSpace paramColorSpace) {
    boolean bool = false;
    if (paramColorSpace instanceof java.awt.color.ICC_ColorSpace && !paramColorSpace.isCS_sRGB() && !paramColorSpace.equals(ColorSpace.getInstance(1001)) && !paramColorSpace.equals(ColorSpace.getInstance(1003)) && !paramColorSpace.equals(ColorSpace.getInstance(1004)) && !paramColorSpace.equals(ColorSpace.getInstance(1002)))
      bool = true; 
    return bool;
  }
  
  static boolean isJFIFcompliant(ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean) {
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    if (colorModel.hasAlpha())
      return false; 
    int i = paramImageTypeSpecifier.getNumComponents();
    if (i == 1)
      return true; 
    if (i != 3)
      return false; 
    if (paramBoolean) {
      if (colorModel.getColorSpace().getType() == 5)
        return true; 
    } else if (colorModel.getColorSpace().getType() == 3) {
      return true;
    } 
    return false;
  }
  
  static int transformForType(ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean) {
    byte b = -1;
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    switch (colorModel.getColorSpace().getType()) {
      case 6:
        b = 0;
        break;
      case 5:
        b = paramBoolean ? 1 : 0;
        break;
      case 3:
        b = 1;
        break;
      case 9:
        b = paramBoolean ? 2 : -1;
        break;
    } 
    return b;
  }
  
  static float convertToLinearQuality(float paramFloat) {
    if (paramFloat <= 0.0F)
      paramFloat = 0.01F; 
    if (paramFloat > 1.0F)
      paramFloat = 1.0F; 
    if (paramFloat < 0.5F) {
      paramFloat = 0.5F / paramFloat;
    } else {
      paramFloat = 2.0F - paramFloat * 2.0F;
    } 
    return paramFloat;
  }
  
  static JPEGQTable[] getDefaultQTables() {
    JPEGQTable[] arrayOfJPEGQTable = new JPEGQTable[2];
    arrayOfJPEGQTable[0] = JPEGQTable.K1Div2Luminance;
    arrayOfJPEGQTable[1] = JPEGQTable.K2Div2Chrominance;
    return arrayOfJPEGQTable;
  }
  
  static JPEGHuffmanTable[] getDefaultHuffmanTables(boolean paramBoolean) {
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable = new JPEGHuffmanTable[2];
    if (paramBoolean) {
      arrayOfJPEGHuffmanTable[0] = JPEGHuffmanTable.StdDCLuminance;
      arrayOfJPEGHuffmanTable[1] = JPEGHuffmanTable.StdDCChrominance;
    } else {
      arrayOfJPEGHuffmanTable[0] = JPEGHuffmanTable.StdACLuminance;
      arrayOfJPEGHuffmanTable[1] = JPEGHuffmanTable.StdACChrominance;
    } 
    return arrayOfJPEGHuffmanTable;
  }
  
  public static class JCS {
    public static final ColorSpace sRGB = ColorSpace.getInstance(1000);
    
    private static ColorSpace YCC = null;
    
    private static boolean yccInited = false;
    
    public static ColorSpace getYCC() {
      if (!yccInited)
        try {
          YCC = ColorSpace.getInstance(1002);
        } catch (IllegalArgumentException illegalArgumentException) {
        
        } finally {
          yccInited = true;
        }  
      return YCC;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */