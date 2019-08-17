package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.Node;

class GIFWritableStreamMetadata extends GIFStreamMetadata {
  static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_stream_1.0";
  
  public GIFWritableStreamMetadata() {
    super(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null);
    reset();
  }
  
  public boolean isReadOnly() { return false; }
  
  public void mergeTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    if (paramString.equals("javax_imageio_gif_stream_1.0")) {
      if (paramNode == null)
        throw new IllegalArgumentException("root == null!"); 
      mergeNativeTree(paramNode);
    } else if (paramString.equals("javax_imageio_1.0")) {
      if (paramNode == null)
        throw new IllegalArgumentException("root == null!"); 
      mergeStandardTree(paramNode);
    } else {
      throw new IllegalArgumentException("Not a recognized format!");
    } 
  }
  
  public void reset() {
    this.version = null;
    this.logicalScreenWidth = -1;
    this.logicalScreenHeight = -1;
    this.colorResolution = -1;
    this.pixelAspectRatio = 0;
    this.backgroundColorIndex = 0;
    this.sortFlag = false;
    this.globalColorTable = null;
  }
  
  protected void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException {
    Node node = paramNode;
    if (!node.getNodeName().equals("javax_imageio_gif_stream_1.0"))
      fatal(node, "Root must be javax_imageio_gif_stream_1.0"); 
    for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
      String str = node.getNodeName();
      if (str.equals("Version")) {
        this.version = getStringAttribute(node, "value", null, true, versionStrings);
      } else if (str.equals("LogicalScreenDescriptor")) {
        this.logicalScreenWidth = getIntAttribute(node, "logicalScreenWidth", -1, true, true, 1, 65535);
        this.logicalScreenHeight = getIntAttribute(node, "logicalScreenHeight", -1, true, true, 1, 65535);
        this.colorResolution = getIntAttribute(node, "colorResolution", -1, true, true, 1, 8);
        this.pixelAspectRatio = getIntAttribute(node, "pixelAspectRatio", 0, true, true, 0, 255);
      } else if (str.equals("GlobalColorTable")) {
        int i = getIntAttribute(node, "sizeOfGlobalColorTable", true, 2, 256);
        if (i != 2 && i != 4 && i != 8 && i != 16 && i != 32 && i != 64 && i != 128 && i != 256)
          fatal(node, "Bad value for GlobalColorTable attribute sizeOfGlobalColorTable!"); 
        this.backgroundColorIndex = getIntAttribute(node, "backgroundColorIndex", 0, true, true, 0, 255);
        this.sortFlag = getBooleanAttribute(node, "sortFlag", false, true);
        this.globalColorTable = getColorTable(node, "ColorTableEntry", true, i);
      } else {
        fatal(node, "Unknown child of root node!");
      } 
    } 
  }
  
  protected void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException {
    Node node = paramNode;
    if (!node.getNodeName().equals("javax_imageio_1.0"))
      fatal(node, "Root must be javax_imageio_1.0"); 
    for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
      String str = node.getNodeName();
      if (str.equals("Chroma")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("Palette")) {
            this.globalColorTable = getColorTable(node1, "PaletteEntry", false, -1);
          } else if (str1.equals("BackgroundIndex")) {
            this.backgroundColorIndex = getIntAttribute(node1, "value", -1, true, true, 0, 255);
          } 
        } 
      } else if (str.equals("Data")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("BitsPerSample")) {
            this.colorResolution = getIntAttribute(node1, "value", -1, true, true, 1, 8);
            break;
          } 
        } 
      } else if (str.equals("Dimension")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("PixelAspectRatio")) {
            float f = getFloatAttribute(node1, "value");
            if (f == 1.0F) {
              this.pixelAspectRatio = 0;
            } else {
              int i = (int)(f * 64.0F - 15.0F);
              this.pixelAspectRatio = Math.max(Math.min(i, 255), 0);
            } 
          } else if (str1.equals("HorizontalScreenSize")) {
            this.logicalScreenWidth = getIntAttribute(node1, "value", -1, true, true, 1, 65535);
          } else if (str1.equals("VerticalScreenSize")) {
            this.logicalScreenHeight = getIntAttribute(node1, "value", -1, true, true, 1, 65535);
          } 
        } 
      } else if (str.equals("Document")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("FormatVersion")) {
            String str2 = getStringAttribute(node1, "value", null, true, null);
            for (byte b = 0; b < versionStrings.length; b++) {
              if (str2.equals(versionStrings[b])) {
                this.version = str2;
                break;
              } 
            } 
            break;
          } 
        } 
      } 
    } 
  }
  
  public void setFromTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    reset();
    mergeTree(paramString, paramNode);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFWritableStreamMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */