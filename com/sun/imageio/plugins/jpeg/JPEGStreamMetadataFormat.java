package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGStreamMetadataFormat extends JPEGMetadataFormat {
  private static JPEGStreamMetadataFormat theInstance = null;
  
  private JPEGStreamMetadataFormat() {
    super("javax_imageio_jpeg_stream_1.0", 4);
    addStreamElements(getRootName());
  }
  
  public static IIOMetadataFormat getInstance() {
    if (theInstance == null)
      theInstance = new JPEGStreamMetadataFormat(); 
    return theInstance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGStreamMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */