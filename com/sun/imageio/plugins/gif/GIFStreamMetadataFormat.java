package com.sun.imageio.plugins.gif;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class GIFStreamMetadataFormat extends IIOMetadataFormatImpl {
  private static IIOMetadataFormat instance = null;
  
  private GIFStreamMetadataFormat() {
    super("javax_imageio_gif_stream_1.0", 2);
    addElement("Version", "javax_imageio_gif_stream_1.0", 0);
    addAttribute("Version", "value", 0, true, null, Arrays.asList(GIFStreamMetadata.versionStrings));
    addElement("LogicalScreenDescriptor", "javax_imageio_gif_stream_1.0", 0);
    addAttribute("LogicalScreenDescriptor", "logicalScreenWidth", 2, true, null, "1", "65535", true, true);
    addAttribute("LogicalScreenDescriptor", "logicalScreenHeight", 2, true, null, "1", "65535", true, true);
    addAttribute("LogicalScreenDescriptor", "colorResolution", 2, true, null, "1", "8", true, true);
    addAttribute("LogicalScreenDescriptor", "pixelAspectRatio", 2, true, null, "0", "255", true, true);
    addElement("GlobalColorTable", "javax_imageio_gif_stream_1.0", 2, 256);
    addAttribute("GlobalColorTable", "sizeOfGlobalColorTable", 2, true, null, Arrays.asList(GIFStreamMetadata.colorTableSizes));
    addAttribute("GlobalColorTable", "backgroundColorIndex", 2, true, null, "0", "255", true, true);
    addBooleanAttribute("GlobalColorTable", "sortFlag", false, false);
    addElement("ColorTableEntry", "GlobalColorTable", 0);
    addAttribute("ColorTableEntry", "index", 2, true, null, "0", "255", true, true);
    addAttribute("ColorTableEntry", "red", 2, true, null, "0", "255", true, true);
    addAttribute("ColorTableEntry", "green", 2, true, null, "0", "255", true, true);
    addAttribute("ColorTableEntry", "blue", 2, true, null, "0", "255", true, true);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return true; }
  
  public static IIOMetadataFormat getInstance() {
    if (instance == null)
      instance = new GIFStreamMetadataFormat(); 
    return instance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFStreamMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */