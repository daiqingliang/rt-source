package com.sun.imageio.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class GIFStreamMetadata extends GIFMetadata {
  static final String nativeMetadataFormatName = "javax_imageio_gif_stream_1.0";
  
  static final String[] versionStrings = { "87a", "89a" };
  
  public String version;
  
  public int logicalScreenWidth;
  
  public int logicalScreenHeight;
  
  public int colorResolution;
  
  public int pixelAspectRatio;
  
  public int backgroundColorIndex;
  
  public boolean sortFlag;
  
  static final String[] colorTableSizes = { "2", "4", "8", "16", "32", "64", "128", "256" };
  
  public byte[] globalColorTable = null;
  
  protected GIFStreamMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2) { super(paramBoolean, paramString1, paramString2, paramArrayOfString1, paramArrayOfString2); }
  
  public GIFStreamMetadata() { this(true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null); }
  
  public boolean isReadOnly() { return true; }
  
  public Node getAsTree(String paramString) {
    if (paramString.equals("javax_imageio_gif_stream_1.0"))
      return getNativeTree(); 
    if (paramString.equals("javax_imageio_1.0"))
      return getStandardTree(); 
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private Node getNativeTree() {
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("javax_imageio_gif_stream_1.0");
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Version");
    iIOMetadataNode1.setAttribute("value", this.version);
    iIOMetadataNode2.appendChild(iIOMetadataNode1);
    iIOMetadataNode1 = new IIOMetadataNode("LogicalScreenDescriptor");
    iIOMetadataNode1.setAttribute("logicalScreenWidth", (this.logicalScreenWidth == -1) ? "" : Integer.toString(this.logicalScreenWidth));
    iIOMetadataNode1.setAttribute("logicalScreenHeight", (this.logicalScreenHeight == -1) ? "" : Integer.toString(this.logicalScreenHeight));
    iIOMetadataNode1.setAttribute("colorResolution", (this.colorResolution == -1) ? "" : Integer.toString(this.colorResolution));
    iIOMetadataNode1.setAttribute("pixelAspectRatio", Integer.toString(this.pixelAspectRatio));
    iIOMetadataNode2.appendChild(iIOMetadataNode1);
    if (this.globalColorTable != null) {
      iIOMetadataNode1 = new IIOMetadataNode("GlobalColorTable");
      int i = this.globalColorTable.length / 3;
      iIOMetadataNode1.setAttribute("sizeOfGlobalColorTable", Integer.toString(i));
      iIOMetadataNode1.setAttribute("backgroundColorIndex", Integer.toString(this.backgroundColorIndex));
      iIOMetadataNode1.setAttribute("sortFlag", this.sortFlag ? "TRUE" : "FALSE");
      for (byte b = 0; b < i; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("ColorTableEntry");
        iIOMetadataNode.setAttribute("index", Integer.toString(b));
        byte b1 = this.globalColorTable[3 * b] & 0xFF;
        byte b2 = this.globalColorTable[3 * b + 1] & 0xFF;
        byte b3 = this.globalColorTable[3 * b + 2] & 0xFF;
        iIOMetadataNode.setAttribute("red", Integer.toString(b1));
        iIOMetadataNode.setAttribute("green", Integer.toString(b2));
        iIOMetadataNode.setAttribute("blue", Integer.toString(b3));
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
    iIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    iIOMetadataNode2.setAttribute("value", "TRUE");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.globalColorTable != null) {
      iIOMetadataNode2 = new IIOMetadataNode("Palette");
      int i = this.globalColorTable.length / 3;
      for (byte b = 0; b < i; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("PaletteEntry");
        iIOMetadataNode.setAttribute("index", Integer.toString(b));
        iIOMetadataNode.setAttribute("red", Integer.toString(this.globalColorTable[3 * b] & 0xFF));
        iIOMetadataNode.setAttribute("green", Integer.toString(this.globalColorTable[3 * b + 1] & 0xFF));
        iIOMetadataNode.setAttribute("blue", Integer.toString(this.globalColorTable[3 * b + 2] & 0xFF));
        iIOMetadataNode2.appendChild(iIOMetadataNode);
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
      iIOMetadataNode2 = new IIOMetadataNode("BackgroundIndex");
      iIOMetadataNode2.setAttribute("value", Integer.toString(this.backgroundColorIndex));
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
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDataNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Data");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("SampleFormat");
    iIOMetadataNode2.setAttribute("value", "Index");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    iIOMetadataNode2.setAttribute("value", (this.colorResolution == -1) ? "" : Integer.toString(this.colorResolution));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
    float f = 1.0F;
    if (this.pixelAspectRatio != 0)
      f = (this.pixelAspectRatio + 15) / 64.0F; 
    iIOMetadataNode2.setAttribute("value", Float.toString(f));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    iIOMetadataNode2.setAttribute("value", "Normal");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("HorizontalScreenSize");
    iIOMetadataNode2.setAttribute("value", (this.logicalScreenWidth == -1) ? "" : Integer.toString(this.logicalScreenWidth));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("VerticalScreenSize");
    iIOMetadataNode2.setAttribute("value", (this.logicalScreenHeight == -1) ? "" : Integer.toString(this.logicalScreenHeight));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDocumentNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Document");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("FormatVersion");
    iIOMetadataNode2.setAttribute("value", this.version);
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode() { return null; }
  
  public IIOMetadataNode getStandardTransparencyNode() { return null; }
  
  public void setFromTree(String paramString, Node paramNode) throws IIOInvalidTreeException { throw new IllegalStateException("Metadata is read-only!"); }
  
  protected void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException { throw new IllegalStateException("Metadata is read-only!"); }
  
  protected void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException { throw new IllegalStateException("Metadata is read-only!"); }
  
  public void reset() { throw new IllegalStateException("Metadata is read-only!"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFStreamMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */