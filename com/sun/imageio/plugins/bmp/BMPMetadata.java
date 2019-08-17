package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class BMPMetadata extends IIOMetadata implements BMPConstants {
  public static final String nativeMetadataFormatName = "javax_imageio_bmp_1.0";
  
  public String bmpVersion;
  
  public int width;
  
  public int height;
  
  public short bitsPerPixel;
  
  public int compression;
  
  public int imageSize;
  
  public int xPixelsPerMeter;
  
  public int yPixelsPerMeter;
  
  public int colorsUsed;
  
  public int colorsImportant;
  
  public int redMask;
  
  public int greenMask;
  
  public int blueMask;
  
  public int alphaMask;
  
  public int colorSpace;
  
  public double redX;
  
  public double redY;
  
  public double redZ;
  
  public double greenX;
  
  public double greenY;
  
  public double greenZ;
  
  public double blueX;
  
  public double blueY;
  
  public double blueZ;
  
  public int gammaRed;
  
  public int gammaGreen;
  
  public int gammaBlue;
  
  public int intent;
  
  public byte[] palette = null;
  
  public int paletteSize;
  
  public int red;
  
  public int green;
  
  public int blue;
  
  public List comments = null;
  
  public BMPMetadata() { super(true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null); }
  
  public boolean isReadOnly() { return true; }
  
  public Node getAsTree(String paramString) {
    if (paramString.equals("javax_imageio_bmp_1.0"))
      return getNativeTree(); 
    if (paramString.equals("javax_imageio_1.0"))
      return getStandardTree(); 
    throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
  }
  
  private String toISO8859(byte[] paramArrayOfByte) {
    try {
      return new String(paramArrayOfByte, "ISO-8859-1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return "";
    } 
  }
  
  private Node getNativeTree() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("javax_imageio_bmp_1.0");
    addChildNode(iIOMetadataNode1, "BMPVersion", this.bmpVersion);
    addChildNode(iIOMetadataNode1, "Width", new Integer(this.width));
    addChildNode(iIOMetadataNode1, "Height", new Integer(this.height));
    addChildNode(iIOMetadataNode1, "BitsPerPixel", new Short(this.bitsPerPixel));
    addChildNode(iIOMetadataNode1, "Compression", new Integer(this.compression));
    addChildNode(iIOMetadataNode1, "ImageSize", new Integer(this.imageSize));
    IIOMetadataNode iIOMetadataNode2 = addChildNode(iIOMetadataNode1, "PixelsPerMeter", null);
    addChildNode(iIOMetadataNode2, "X", new Integer(this.xPixelsPerMeter));
    addChildNode(iIOMetadataNode2, "Y", new Integer(this.yPixelsPerMeter));
    addChildNode(iIOMetadataNode1, "ColorsUsed", new Integer(this.colorsUsed));
    addChildNode(iIOMetadataNode1, "ColorsImportant", new Integer(this.colorsImportant));
    char c = Character.MIN_VALUE;
    int i;
    for (i = 0; i < this.bmpVersion.length(); i++) {
      if (Character.isDigit(this.bmpVersion.charAt(i)))
        c = this.bmpVersion.charAt(i) - '0'; 
    } 
    if (c >= '\004') {
      iIOMetadataNode2 = addChildNode(iIOMetadataNode1, "Mask", null);
      addChildNode(iIOMetadataNode2, "Red", new Integer(this.redMask));
      addChildNode(iIOMetadataNode2, "Green", new Integer(this.greenMask));
      addChildNode(iIOMetadataNode2, "Blue", new Integer(this.blueMask));
      addChildNode(iIOMetadataNode2, "Alpha", new Integer(this.alphaMask));
      addChildNode(iIOMetadataNode1, "ColorSpaceType", new Integer(this.colorSpace));
      iIOMetadataNode2 = addChildNode(iIOMetadataNode1, "CIEXYZEndPoints", null);
      addXYZPoints(iIOMetadataNode2, "Red", this.redX, this.redY, this.redZ);
      addXYZPoints(iIOMetadataNode2, "Green", this.greenX, this.greenY, this.greenZ);
      addXYZPoints(iIOMetadataNode2, "Blue", this.blueX, this.blueY, this.blueZ);
      iIOMetadataNode2 = addChildNode(iIOMetadataNode1, "Intent", new Integer(this.intent));
    } 
    if (this.palette != null && this.paletteSize > 0) {
      iIOMetadataNode2 = addChildNode(iIOMetadataNode1, "Palette", null);
      i = this.palette.length / this.paletteSize;
      byte b1 = 0;
      byte b2 = 0;
      while (b1 < this.paletteSize) {
        IIOMetadataNode iIOMetadataNode = addChildNode(iIOMetadataNode2, "PaletteEntry", null);
        this.red = this.palette[b2++] & 0xFF;
        this.green = this.palette[b2++] & 0xFF;
        this.blue = this.palette[b2++] & 0xFF;
        addChildNode(iIOMetadataNode, "Red", new Byte((byte)this.red));
        addChildNode(iIOMetadataNode, "Green", new Byte((byte)this.green));
        addChildNode(iIOMetadataNode, "Blue", new Byte((byte)this.blue));
        if (i == 4)
          addChildNode(iIOMetadataNode, "Alpha", new Byte((byte)(this.palette[b2++] & 0xFF))); 
        b1++;
      } 
    } 
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardChromaNode() {
    if (this.palette != null && this.paletteSize > 0) {
      IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Chroma");
      IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Palette");
      int i = this.palette.length / this.paletteSize;
      iIOMetadataNode2.setAttribute("value", "" + i);
      byte b1 = 0;
      byte b2 = 0;
      while (b1 < this.paletteSize) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("PaletteEntry");
        iIOMetadataNode.setAttribute("index", "" + b1);
        iIOMetadataNode.setAttribute("red", "" + this.palette[b2++]);
        iIOMetadataNode.setAttribute("green", "" + this.palette[b2++]);
        iIOMetadataNode.setAttribute("blue", "" + this.palette[b2++]);
        if (i == 4 && this.palette[b2] != 0)
          iIOMetadataNode.setAttribute("alpha", "" + this.palette[b2++]); 
        iIOMetadataNode2.appendChild(iIOMetadataNode);
        b1++;
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
      return iIOMetadataNode1;
    } 
    return null;
  }
  
  protected IIOMetadataNode getStandardCompressionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    iIOMetadataNode2.setAttribute("value", BMPCompressionTypes.getName(this.compression));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDataNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Data");
    String str = "";
    if (this.bitsPerPixel == 24) {
      str = "8 8 8 ";
    } else if (this.bitsPerPixel == 16 || this.bitsPerPixel == 32) {
      str = "" + countBits(this.redMask) + " " + countBits(this.greenMask) + countBits(this.blueMask) + "" + countBits(this.alphaMask);
    } 
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    iIOMetadataNode2.setAttribute("value", str);
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDimensionNode() {
    if (this.yPixelsPerMeter > 0.0F && this.xPixelsPerMeter > 0.0F) {
      IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Dimension");
      float f = (this.yPixelsPerMeter / this.xPixelsPerMeter);
      IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
      iIOMetadataNode2.setAttribute("value", "" + f);
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
      iIOMetadataNode2 = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
      iIOMetadataNode2.setAttribute("value", "" + (1 / this.xPixelsPerMeter * 1000));
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
      iIOMetadataNode2 = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
      iIOMetadataNode2.setAttribute("value", "" + (1 / this.yPixelsPerMeter * 1000));
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
      return iIOMetadataNode1;
    } 
    return null;
  }
  
  public void setFromTree(String paramString, Node paramNode) { throw new IllegalStateException(I18N.getString("BMPMetadata1")); }
  
  public void mergeTree(String paramString, Node paramNode) { throw new IllegalStateException(I18N.getString("BMPMetadata1")); }
  
  public void reset() { throw new IllegalStateException(I18N.getString("BMPMetadata1")); }
  
  private String countBits(int paramInt) {
    byte b = 0;
    while (paramInt > 0) {
      if ((paramInt & true) == 1)
        b++; 
      paramInt >>>= 1;
    } 
    return (b == 0) ? "" : ("" + b);
  }
  
  private void addXYZPoints(IIOMetadataNode paramIIOMetadataNode, String paramString, double paramDouble1, double paramDouble2, double paramDouble3) {
    IIOMetadataNode iIOMetadataNode = addChildNode(paramIIOMetadataNode, paramString, null);
    addChildNode(iIOMetadataNode, "X", new Double(paramDouble1));
    addChildNode(iIOMetadataNode, "Y", new Double(paramDouble2));
    addChildNode(iIOMetadataNode, "Z", new Double(paramDouble3));
  }
  
  private IIOMetadataNode addChildNode(IIOMetadataNode paramIIOMetadataNode, String paramString, Object paramObject) {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(paramString);
    if (paramObject != null) {
      iIOMetadataNode.setUserObject(paramObject);
      iIOMetadataNode.setNodeValue(ImageUtil.convertObjectToString(paramObject));
    } 
    paramIIOMetadataNode.appendChild(iIOMetadataNode);
    return iIOMetadataNode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */