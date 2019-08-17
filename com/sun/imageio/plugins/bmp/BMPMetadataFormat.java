package com.sun.imageio.plugins.bmp;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class BMPMetadataFormat extends IIOMetadataFormatImpl {
  private static IIOMetadataFormat instance = null;
  
  private BMPMetadataFormat() {
    super("javax_imageio_bmp_1.0", 2);
    addElement("ImageDescriptor", "javax_imageio_bmp_1.0", 0);
    addAttribute("ImageDescriptor", "bmpVersion", 0, true, null);
    addAttribute("ImageDescriptor", "width", 2, true, null, "0", "65535", true, true);
    addAttribute("ImageDescriptor", "height", 2, true, null, "1", "65535", true, true);
    addAttribute("ImageDescriptor", "bitsPerPixel", 2, true, null, "1", "65535", true, true);
    addAttribute("ImageDescriptor", "compression", 2, false, null);
    addAttribute("ImageDescriptor", "imageSize", 2, true, null, "1", "65535", true, true);
    addElement("PixelsPerMeter", "javax_imageio_bmp_1.0", 0);
    addAttribute("PixelsPerMeter", "X", 2, false, null, "1", "65535", true, true);
    addAttribute("PixelsPerMeter", "Y", 2, false, null, "1", "65535", true, true);
    addElement("ColorsUsed", "javax_imageio_bmp_1.0", 0);
    addAttribute("ColorsUsed", "value", 2, true, null, "0", "65535", true, true);
    addElement("ColorsImportant", "javax_imageio_bmp_1.0", 0);
    addAttribute("ColorsImportant", "value", 2, false, null, "0", "65535", true, true);
    addElement("BI_BITFIELDS_Mask", "javax_imageio_bmp_1.0", 0);
    addAttribute("BI_BITFIELDS_Mask", "red", 2, false, null, "0", "65535", true, true);
    addAttribute("BI_BITFIELDS_Mask", "green", 2, false, null, "0", "65535", true, true);
    addAttribute("BI_BITFIELDS_Mask", "blue", 2, false, null, "0", "65535", true, true);
    addElement("ColorSpace", "javax_imageio_bmp_1.0", 0);
    addAttribute("ColorSpace", "value", 2, false, null, "0", "65535", true, true);
    addElement("LCS_CALIBRATED_RGB", "javax_imageio_bmp_1.0", 0);
    addAttribute("LCS_CALIBRATED_RGB", "redX", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "redY", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "redZ", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "greenX", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "greenY", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "greenZ", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "blueX", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "blueY", 4, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB", "blueZ", 4, false, null, "0", "65535", true, true);
    addElement("LCS_CALIBRATED_RGB_GAMMA", "javax_imageio_bmp_1.0", 0);
    addAttribute("LCS_CALIBRATED_RGB_GAMMA", "red", 2, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB_GAMMA", "green", 2, false, null, "0", "65535", true, true);
    addAttribute("LCS_CALIBRATED_RGB_GAMMA", "blue", 2, false, null, "0", "65535", true, true);
    addElement("Intent", "javax_imageio_bmp_1.0", 0);
    addAttribute("Intent", "value", 2, false, null, "0", "65535", true, true);
    addElement("Palette", "javax_imageio_bmp_1.0", 2, 256);
    addAttribute("Palette", "sizeOfPalette", 2, true, null);
    addBooleanAttribute("Palette", "sortFlag", false, false);
    addElement("PaletteEntry", "Palette", 0);
    addAttribute("PaletteEntry", "index", 2, true, null, "0", "255", true, true);
    addAttribute("PaletteEntry", "red", 2, true, null, "0", "255", true, true);
    addAttribute("PaletteEntry", "green", 2, true, null, "0", "255", true, true);
    addAttribute("PaletteEntry", "blue", 2, true, null, "0", "255", true, true);
    addElement("CommentExtensions", "javax_imageio_bmp_1.0", 1, 2147483647);
    addElement("CommentExtension", "CommentExtensions", 0);
    addAttribute("CommentExtension", "value", 0, true, null);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return true; }
  
  public static IIOMetadataFormat getInstance() {
    if (instance == null)
      instance = new BMPMetadataFormat(); 
    return instance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */