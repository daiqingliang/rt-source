package com.sun.imageio.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

class GIFWritableImageMetadata extends GIFImageMetadata {
  static final String NATIVE_FORMAT_NAME = "javax_imageio_gif_image_1.0";
  
  GIFWritableImageMetadata() { super(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null); }
  
  public boolean isReadOnly() { return false; }
  
  public void reset() {
    this.imageLeftPosition = 0;
    this.imageTopPosition = 0;
    this.imageWidth = 0;
    this.imageHeight = 0;
    this.interlaceFlag = false;
    this.sortFlag = false;
    this.localColorTable = null;
    this.disposalMethod = 0;
    this.userInputFlag = false;
    this.transparentColorFlag = false;
    this.delayTime = 0;
    this.transparentColorIndex = 0;
    this.hasPlainTextExtension = false;
    this.textGridLeft = 0;
    this.textGridTop = 0;
    this.textGridWidth = 0;
    this.textGridHeight = 0;
    this.characterCellWidth = 0;
    this.characterCellHeight = 0;
    this.textForegroundColor = 0;
    this.textBackgroundColor = 0;
    this.text = null;
    this.applicationIDs = null;
    this.authenticationCodes = null;
    this.applicationData = null;
    this.comments = null;
  }
  
  private byte[] fromISO8859(String paramString) {
    try {
      return paramString.getBytes("ISO-8859-1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return "".getBytes();
    } 
  }
  
  protected void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException {
    Node node = paramNode;
    if (!node.getNodeName().equals("javax_imageio_gif_image_1.0"))
      fatal(node, "Root must be javax_imageio_gif_image_1.0"); 
    for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
      String str = node.getNodeName();
      if (str.equals("ImageDescriptor")) {
        this.imageLeftPosition = getIntAttribute(node, "imageLeftPosition", -1, true, true, 0, 65535);
        this.imageTopPosition = getIntAttribute(node, "imageTopPosition", -1, true, true, 0, 65535);
        this.imageWidth = getIntAttribute(node, "imageWidth", -1, true, true, 1, 65535);
        this.imageHeight = getIntAttribute(node, "imageHeight", -1, true, true, 1, 65535);
        this.interlaceFlag = getBooleanAttribute(node, "interlaceFlag", false, true);
      } else if (str.equals("LocalColorTable")) {
        int i = getIntAttribute(node, "sizeOfLocalColorTable", true, 2, 256);
        if (i != 2 && i != 4 && i != 8 && i != 16 && i != 32 && i != 64 && i != 128 && i != 256)
          fatal(node, "Bad value for LocalColorTable attribute sizeOfLocalColorTable!"); 
        this.sortFlag = getBooleanAttribute(node, "sortFlag", false, true);
        this.localColorTable = getColorTable(node, "ColorTableEntry", true, i);
      } else if (str.equals("GraphicControlExtension")) {
        String str1 = getStringAttribute(node, "disposalMethod", null, true, disposalMethodNames);
        this.disposalMethod = 0;
        while (!str1.equals(disposalMethodNames[this.disposalMethod]))
          this.disposalMethod++; 
        this.userInputFlag = getBooleanAttribute(node, "userInputFlag", false, true);
        this.transparentColorFlag = getBooleanAttribute(node, "transparentColorFlag", false, true);
        this.delayTime = getIntAttribute(node, "delayTime", -1, true, true, 0, 65535);
        this.transparentColorIndex = getIntAttribute(node, "transparentColorIndex", -1, true, true, 0, 65535);
      } else if (str.equals("PlainTextExtension")) {
        this.hasPlainTextExtension = true;
        this.textGridLeft = getIntAttribute(node, "textGridLeft", -1, true, true, 0, 65535);
        this.textGridTop = getIntAttribute(node, "textGridTop", -1, true, true, 0, 65535);
        this.textGridWidth = getIntAttribute(node, "textGridWidth", -1, true, true, 1, 65535);
        this.textGridHeight = getIntAttribute(node, "textGridHeight", -1, true, true, 1, 65535);
        this.characterCellWidth = getIntAttribute(node, "characterCellWidth", -1, true, true, 1, 65535);
        this.characterCellHeight = getIntAttribute(node, "characterCellHeight", -1, true, true, 1, 65535);
        this.textForegroundColor = getIntAttribute(node, "textForegroundColor", -1, true, true, 0, 255);
        this.textBackgroundColor = getIntAttribute(node, "textBackgroundColor", -1, true, true, 0, 255);
        String str1 = getStringAttribute(node, "text", "", false, null);
        this.text = fromISO8859(str1);
      } else if (str.equals("ApplicationExtensions")) {
        IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)node.getFirstChild();
        if (!iIOMetadataNode.getNodeName().equals("ApplicationExtension"))
          fatal(node, "Only a ApplicationExtension may be a child of a ApplicationExtensions!"); 
        String str1 = getStringAttribute(iIOMetadataNode, "applicationID", null, true, null);
        String str2 = getStringAttribute(iIOMetadataNode, "authenticationCode", null, true, null);
        Object object = iIOMetadataNode.getUserObject();
        if (object == null || !(object instanceof byte[]))
          fatal(iIOMetadataNode, "Bad user object in ApplicationExtension!"); 
        if (this.applicationIDs == null) {
          this.applicationIDs = new ArrayList();
          this.authenticationCodes = new ArrayList();
          this.applicationData = new ArrayList();
        } 
        this.applicationIDs.add(fromISO8859(str1));
        this.authenticationCodes.add(fromISO8859(str2));
        this.applicationData.add(object);
      } else if (str.equals("CommentExtensions")) {
        Node node1 = node.getFirstChild();
        if (node1 != null)
          while (node1 != null) {
            if (!node1.getNodeName().equals("CommentExtension"))
              fatal(node, "Only a CommentExtension may be a child of a CommentExtensions!"); 
            if (this.comments == null)
              this.comments = new ArrayList(); 
            String str1 = getStringAttribute(node1, "value", null, true, null);
            this.comments.add(fromISO8859(str1));
            node1 = node1.getNextSibling();
          }  
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
            this.localColorTable = getColorTable(node1, "PaletteEntry", false, -1);
            break;
          } 
        } 
      } else if (str.equals("Compression")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("NumProgressiveScans")) {
            int i = getIntAttribute(node1, "value", 4, false, true, 1, 2147483647);
            if (i > 1)
              this.interlaceFlag = true; 
            break;
          } 
        } 
      } else if (str.equals("Dimension")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("HorizontalPixelOffset")) {
            this.imageLeftPosition = getIntAttribute(node1, "value", -1, true, true, 0, 65535);
          } else if (str1.equals("VerticalPixelOffset")) {
            this.imageTopPosition = getIntAttribute(node1, "value", -1, true, true, 0, 65535);
          } 
        } 
      } else if (str.equals("Text")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("TextEntry") && getAttribute(node1, "compression", "none", false).equals("none") && Charset.isSupported(getAttribute(node1, "encoding", "ISO-8859-1", false))) {
            String str2 = getAttribute(node1, "value");
            byte[] arrayOfByte = fromISO8859(str2);
            if (this.comments == null)
              this.comments = new ArrayList(); 
            this.comments.add(arrayOfByte);
          } 
        } 
      } else if (str.equals("Transparency")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("TransparentIndex")) {
            this.transparentColorIndex = getIntAttribute(node1, "value", -1, true, true, 0, 255);
            this.transparentColorFlag = true;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFWritableImageMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */