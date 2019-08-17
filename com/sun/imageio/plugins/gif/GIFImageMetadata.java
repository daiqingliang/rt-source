package com.sun.imageio.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFImageMetadata extends GIFMetadata {
  static final String nativeMetadataFormatName = "javax_imageio_gif_image_1.0";
  
  static final String[] disposalMethodNames = { "none", "doNotDispose", "restoreToBackgroundColor", "restoreToPrevious", "undefinedDisposalMethod4", "undefinedDisposalMethod5", "undefinedDisposalMethod6", "undefinedDisposalMethod7" };
  
  public int imageLeftPosition;
  
  public int imageTopPosition;
  
  public int imageWidth;
  
  public int imageHeight;
  
  public boolean interlaceFlag = false;
  
  public boolean sortFlag = false;
  
  public byte[] localColorTable = null;
  
  public int disposalMethod = 0;
  
  public boolean userInputFlag = false;
  
  public boolean transparentColorFlag = false;
  
  public int delayTime = 0;
  
  public int transparentColorIndex = 0;
  
  public boolean hasPlainTextExtension = false;
  
  public int textGridLeft;
  
  public int textGridTop;
  
  public int textGridWidth;
  
  public int textGridHeight;
  
  public int characterCellWidth;
  
  public int characterCellHeight;
  
  public int textForegroundColor;
  
  public int textBackgroundColor;
  
  public byte[] text;
  
  public List applicationIDs = null;
  
  public List authenticationCodes = null;
  
  public List applicationData = null;
  
  public List comments = null;
  
  protected GIFImageMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2) { super(paramBoolean, paramString1, paramString2, paramArrayOfString1, paramArrayOfString2); }
  
  public GIFImageMetadata() { this(true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null); }
  
  public boolean isReadOnly() { return true; }
  
  public Node getAsTree(String paramString) {
    if (paramString.equals("javax_imageio_gif_image_1.0"))
      return getNativeTree(); 
    if (paramString.equals("javax_imageio_1.0"))
      return getStandardTree(); 
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private String toISO8859(byte[] paramArrayOfByte) {
    try {
      return new String(paramArrayOfByte, "ISO-8859-1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return "";
    } 
  }
  
  private Node getNativeTree() {
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("javax_imageio_gif_image_1.0");
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("ImageDescriptor");
    iIOMetadataNode1.setAttribute("imageLeftPosition", Integer.toString(this.imageLeftPosition));
    iIOMetadataNode1.setAttribute("imageTopPosition", Integer.toString(this.imageTopPosition));
    iIOMetadataNode1.setAttribute("imageWidth", Integer.toString(this.imageWidth));
    iIOMetadataNode1.setAttribute("imageHeight", Integer.toString(this.imageHeight));
    iIOMetadataNode1.setAttribute("interlaceFlag", this.interlaceFlag ? "TRUE" : "FALSE");
    iIOMetadataNode2.appendChild(iIOMetadataNode1);
    if (this.localColorTable != null) {
      iIOMetadataNode1 = new IIOMetadataNode("LocalColorTable");
      int i = this.localColorTable.length / 3;
      iIOMetadataNode1.setAttribute("sizeOfLocalColorTable", Integer.toString(i));
      iIOMetadataNode1.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
      for (byte b = 0; b < i; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("ColorTableEntry");
        iIOMetadataNode.setAttribute("index", Integer.toString(b));
        byte b1 = this.localColorTable[3 * b] & 0xFF;
        byte b2 = this.localColorTable[3 * b + 1] & 0xFF;
        byte b3 = this.localColorTable[3 * b + 2] & 0xFF;
        iIOMetadataNode.setAttribute("red", Integer.toString(b1));
        iIOMetadataNode.setAttribute("green", Integer.toString(b2));
        iIOMetadataNode.setAttribute("blue", Integer.toString(b3));
        iIOMetadataNode1.appendChild(iIOMetadataNode);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode1);
    } 
    iIOMetadataNode1 = new IIOMetadataNode("GraphicControlExtension");
    iIOMetadataNode1.setAttribute("disposalMethod", disposalMethodNames[this.disposalMethod]);
    iIOMetadataNode1.setAttribute("userInputFlag", this.userInputFlag ? "TRUE" : "FALSE");
    iIOMetadataNode1.setAttribute("transparentColorFlag", this.transparentColorFlag ? "TRUE" : "FALSE");
    iIOMetadataNode1.setAttribute("delayTime", Integer.toString(this.delayTime));
    iIOMetadataNode1.setAttribute("transparentColorIndex", Integer.toString(this.transparentColorIndex));
    iIOMetadataNode2.appendChild(iIOMetadataNode1);
    if (this.hasPlainTextExtension) {
      iIOMetadataNode1 = new IIOMetadataNode("PlainTextExtension");
      iIOMetadataNode1.setAttribute("textGridLeft", Integer.toString(this.textGridLeft));
      iIOMetadataNode1.setAttribute("textGridTop", Integer.toString(this.textGridTop));
      iIOMetadataNode1.setAttribute("textGridWidth", Integer.toString(this.textGridWidth));
      iIOMetadataNode1.setAttribute("textGridHeight", Integer.toString(this.textGridHeight));
      iIOMetadataNode1.setAttribute("characterCellWidth", Integer.toString(this.characterCellWidth));
      iIOMetadataNode1.setAttribute("characterCellHeight", Integer.toString(this.characterCellHeight));
      iIOMetadataNode1.setAttribute("textForegroundColor", Integer.toString(this.textForegroundColor));
      iIOMetadataNode1.setAttribute("textBackgroundColor", Integer.toString(this.textBackgroundColor));
      iIOMetadataNode1.setAttribute("text", toISO8859(this.text));
      iIOMetadataNode2.appendChild(iIOMetadataNode1);
    } 
    boolean bool1 = (this.applicationIDs == null) ? 0 : this.applicationIDs.size();
    if (bool1) {
      iIOMetadataNode1 = new IIOMetadataNode("ApplicationExtensions");
      for (byte b = 0; b < bool1; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("ApplicationExtension");
        byte[] arrayOfByte1 = (byte[])this.applicationIDs.get(b);
        iIOMetadataNode.setAttribute("applicationID", toISO8859(arrayOfByte1));
        byte[] arrayOfByte2 = (byte[])this.authenticationCodes.get(b);
        iIOMetadataNode.setAttribute("authenticationCode", toISO8859(arrayOfByte2));
        byte[] arrayOfByte3 = (byte[])this.applicationData.get(b);
        iIOMetadataNode.setUserObject((byte[])arrayOfByte3.clone());
        iIOMetadataNode1.appendChild(iIOMetadataNode);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode1);
    } 
    boolean bool2 = (this.comments == null) ? 0 : this.comments.size();
    if (bool2) {
      iIOMetadataNode1 = new IIOMetadataNode("CommentExtensions");
      for (byte b = 0; b < bool2; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("CommentExtension");
        byte[] arrayOfByte = (byte[])this.comments.get(b);
        iIOMetadataNode.setAttribute("value", toISO8859(arrayOfByte));
        iIOMetadataNode1.appendChild(iIOMetadataNode);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode1);
    } 
    return iIOMetadataNode2;
  }
  
  public IIOMetadataNode getStandardChromaNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    iIOMetadataNode2.setAttribute("name", "RGB");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("NumChannels");
    iIOMetadataNode2.setAttribute("value", this.transparentColorFlag ? "4" : "3");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    iIOMetadataNode2.setAttribute("value", "TRUE");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.localColorTable != null) {
      iIOMetadataNode2 = new IIOMetadataNode("Palette");
      int i = this.localColorTable.length / 3;
      for (byte b = 0; b < i; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("PaletteEntry");
        iIOMetadataNode.setAttribute("index", Integer.toString(b));
        iIOMetadataNode.setAttribute("red", Integer.toString(this.localColorTable[3 * b] & 0xFF));
        iIOMetadataNode.setAttribute("green", Integer.toString(this.localColorTable[3 * b + 1] & 0xFF));
        iIOMetadataNode.setAttribute("blue", Integer.toString(this.localColorTable[3 * b + 2] & 0xFF));
        iIOMetadataNode2.appendChild(iIOMetadataNode);
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardCompressionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    iIOMetadataNode2.setAttribute("value", "lzw");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("Lossless");
    iIOMetadataNode2.setAttribute("value", "TRUE");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("NumProgressiveScans");
    iIOMetadataNode2.setAttribute("value", this.interlaceFlag ? "4" : "1");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDataNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Data");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("SampleFormat");
    iIOMetadataNode2.setAttribute("value", "Index");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    iIOMetadataNode2.setAttribute("value", "Normal");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("HorizontalPixelOffset");
    iIOMetadataNode2.setAttribute("value", Integer.toString(this.imageLeftPosition));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("VerticalPixelOffset");
    iIOMetadataNode2.setAttribute("value", Integer.toString(this.imageTopPosition));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode() {
    if (this.comments == null)
      return null; 
    Iterator iterator = this.comments.iterator();
    if (!iterator.hasNext())
      return null; 
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Text");
    IIOMetadataNode iIOMetadataNode2 = null;
    while (iterator.hasNext()) {
      byte[] arrayOfByte = (byte[])iterator.next();
      String str = null;
      try {
        str = new String(arrayOfByte, "ISO-8859-1");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new RuntimeException("Encoding ISO-8859-1 unknown!");
      } 
      iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      iIOMetadataNode2.setAttribute("value", str);
      iIOMetadataNode2.setAttribute("encoding", "ISO-8859-1");
      iIOMetadataNode2.setAttribute("compression", "none");
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTransparencyNode() {
    if (!this.transparentColorFlag)
      return null; 
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Transparency");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("TransparentIndex");
    iIOMetadataNode2.setAttribute("value", Integer.toString(this.transparentColorIndex));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public void setFromTree(String paramString, Node paramNode) throws IIOInvalidTreeException { throw new IllegalStateException("Metadata is read-only!"); }
  
  protected void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException { throw new IllegalStateException("Metadata is read-only!"); }
  
  protected void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException { throw new IllegalStateException("Metadata is read-only!"); }
  
  public void reset() { throw new IllegalStateException("Metadata is read-only!"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFImageMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */