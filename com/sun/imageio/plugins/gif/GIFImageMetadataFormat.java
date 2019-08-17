package com.sun.imageio.plugins.gif;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class GIFImageMetadataFormat extends IIOMetadataFormatImpl {
  private static IIOMetadataFormat instance = null;
  
  private GIFImageMetadataFormat() {
    super("javax_imageio_gif_image_1.0", 2);
    addElement("ImageDescriptor", "javax_imageio_gif_image_1.0", 0);
    addAttribute("ImageDescriptor", "imageLeftPosition", 2, true, null, "0", "65535", true, true);
    addAttribute("ImageDescriptor", "imageTopPosition", 2, true, null, "0", "65535", true, true);
    addAttribute("ImageDescriptor", "imageWidth", 2, true, null, "1", "65535", true, true);
    addAttribute("ImageDescriptor", "imageHeight", 2, true, null, "1", "65535", true, true);
    addBooleanAttribute("ImageDescriptor", "interlaceFlag", false, false);
    addElement("LocalColorTable", "javax_imageio_gif_image_1.0", 2, 256);
    addAttribute("LocalColorTable", "sizeOfLocalColorTable", 2, true, null, Arrays.asList(GIFStreamMetadata.colorTableSizes));
    addBooleanAttribute("LocalColorTable", "sortFlag", false, false);
    addElement("ColorTableEntry", "LocalColorTable", 0);
    addAttribute("ColorTableEntry", "index", 2, true, null, "0", "255", true, true);
    addAttribute("ColorTableEntry", "red", 2, true, null, "0", "255", true, true);
    addAttribute("ColorTableEntry", "green", 2, true, null, "0", "255", true, true);
    addAttribute("ColorTableEntry", "blue", 2, true, null, "0", "255", true, true);
    addElement("GraphicControlExtension", "javax_imageio_gif_image_1.0", 0);
    addAttribute("GraphicControlExtension", "disposalMethod", 0, true, null, Arrays.asList(GIFImageMetadata.disposalMethodNames));
    addBooleanAttribute("GraphicControlExtension", "userInputFlag", false, false);
    addBooleanAttribute("GraphicControlExtension", "transparentColorFlag", false, false);
    addAttribute("GraphicControlExtension", "delayTime", 2, true, null, "0", "65535", true, true);
    addAttribute("GraphicControlExtension", "transparentColorIndex", 2, true, null, "0", "255", true, true);
    addElement("PlainTextExtension", "javax_imageio_gif_image_1.0", 0);
    addAttribute("PlainTextExtension", "textGridLeft", 2, true, null, "0", "65535", true, true);
    addAttribute("PlainTextExtension", "textGridTop", 2, true, null, "0", "65535", true, true);
    addAttribute("PlainTextExtension", "textGridWidth", 2, true, null, "1", "65535", true, true);
    addAttribute("PlainTextExtension", "textGridHeight", 2, true, null, "1", "65535", true, true);
    addAttribute("PlainTextExtension", "characterCellWidth", 2, true, null, "1", "65535", true, true);
    addAttribute("PlainTextExtension", "characterCellHeight", 2, true, null, "1", "65535", true, true);
    addAttribute("PlainTextExtension", "textForegroundColor", 2, true, null, "0", "255", true, true);
    addAttribute("PlainTextExtension", "textBackgroundColor", 2, true, null, "0", "255", true, true);
    addElement("ApplicationExtensions", "javax_imageio_gif_image_1.0", 1, 2147483647);
    addElement("ApplicationExtension", "ApplicationExtensions", 0);
    addAttribute("ApplicationExtension", "applicationID", 0, true, null);
    addAttribute("ApplicationExtension", "authenticationCode", 0, true, null);
    addObjectValue("ApplicationExtension", byte.class, 0, 2147483647);
    addElement("CommentExtensions", "javax_imageio_gif_image_1.0", 1, 2147483647);
    addElement("CommentExtension", "CommentExtensions", 0);
    addAttribute("CommentExtension", "value", 0, true, null);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return true; }
  
  public static IIOMetadataFormat getInstance() {
    if (instance == null)
      instance = new GIFImageMetadataFormat(); 
    return instance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFImageMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */