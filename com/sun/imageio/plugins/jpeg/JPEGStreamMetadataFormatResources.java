package com.sun.imageio.plugins.jpeg;

public class JPEGStreamMetadataFormatResources extends JPEGMetadataFormatResources {
  protected Object[][] getContents() {
    Object[][] arrayOfObject = new Object[commonContents.length][2];
    for (byte b = 0; b < commonContents.length; b++) {
      arrayOfObject[b][0] = commonContents[b][0];
      arrayOfObject[b][1] = commonContents[b][1];
    } 
    return arrayOfObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGStreamMetadataFormatResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */