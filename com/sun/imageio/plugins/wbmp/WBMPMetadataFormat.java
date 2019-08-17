package com.sun.imageio.plugins.wbmp;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class WBMPMetadataFormat extends IIOMetadataFormatImpl {
  private static IIOMetadataFormat instance = null;
  
  private WBMPMetadataFormat() {
    super("javax_imageio_wbmp_1.0", 2);
    addElement("ImageDescriptor", "javax_imageio_wbmp_1.0", 0);
    addAttribute("ImageDescriptor", "WBMPType", 2, true, "0");
    addAttribute("ImageDescriptor", "Width", 2, true, null, "0", "65535", true, true);
    addAttribute("ImageDescriptor", "Height", 2, true, null, "1", "65535", true, true);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return true; }
  
  public static IIOMetadataFormat getInstance() {
    if (instance == null)
      instance = new WBMPMetadataFormat(); 
    return instance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */