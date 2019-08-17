package com.sun.imageio.plugins.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageTypeSpecifier;

public class PaletteBuilder {
  protected static final int MAXLEVEL = 8;
  
  protected RenderedImage src;
  
  protected ColorModel srcColorModel;
  
  protected Raster srcRaster;
  
  protected int requiredSize;
  
  protected ColorNode root;
  
  protected int numNodes;
  
  protected int maxNodes;
  
  protected int currLevel;
  
  protected int currSize;
  
  protected ColorNode[] reduceList;
  
  protected ColorNode[] palette;
  
  protected int transparency;
  
  protected ColorNode transColor;
  
  public static RenderedImage createIndexedImage(RenderedImage paramRenderedImage) {
    PaletteBuilder paletteBuilder = new PaletteBuilder(paramRenderedImage);
    paletteBuilder.buildPalette();
    return paletteBuilder.getIndexedImage();
  }
  
  public static IndexColorModel createIndexColorModel(RenderedImage paramRenderedImage) {
    PaletteBuilder paletteBuilder = new PaletteBuilder(paramRenderedImage);
    paletteBuilder.buildPalette();
    return paletteBuilder.getIndexColorModel();
  }
  
  public static boolean canCreatePalette(ImageTypeSpecifier paramImageTypeSpecifier) {
    if (paramImageTypeSpecifier == null)
      throw new IllegalArgumentException("type == null"); 
    return true;
  }
  
  public static boolean canCreatePalette(RenderedImage paramRenderedImage) {
    if (paramRenderedImage == null)
      throw new IllegalArgumentException("image == null"); 
    ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
    return canCreatePalette(imageTypeSpecifier);
  }
  
  protected RenderedImage getIndexedImage() {
    IndexColorModel indexColorModel = getIndexColorModel();
    BufferedImage bufferedImage = new BufferedImage(this.src.getWidth(), this.src.getHeight(), 13, indexColorModel);
    WritableRaster writableRaster = bufferedImage.getRaster();
    for (byte b = 0; b < bufferedImage.getHeight(); b++) {
      for (byte b1 = 0; b1 < bufferedImage.getWidth(); b1++) {
        Color color = getSrcColor(b1, b);
        writableRaster.setSample(b1, b, 0, findColorIndex(this.root, color));
      } 
    } 
    return bufferedImage;
  }
  
  protected PaletteBuilder(RenderedImage paramRenderedImage) { this(paramRenderedImage, 256); }
  
  protected PaletteBuilder(RenderedImage paramRenderedImage, int paramInt) {
    this.src = paramRenderedImage;
    this.srcColorModel = paramRenderedImage.getColorModel();
    this.srcRaster = paramRenderedImage.getData();
    this.transparency = this.srcColorModel.getTransparency();
    this.requiredSize = paramInt;
  }
  
  private Color getSrcColor(int paramInt1, int paramInt2) {
    int i = this.srcColorModel.getRGB(this.srcRaster.getDataElements(paramInt1, paramInt2, null));
    return new Color(i, (this.transparency != 1));
  }
  
  protected int findColorIndex(ColorNode paramColorNode, Color paramColor) {
    if (this.transparency != 1 && paramColor.getAlpha() != 255)
      return 0; 
    if (paramColorNode.isLeaf)
      return paramColorNode.paletteIndex; 
    int i = getBranchIndex(paramColor, paramColorNode.level);
    return findColorIndex(paramColorNode.children[i], paramColor);
  }
  
  protected void buildPalette() {
    this.reduceList = new ColorNode[9];
    int i;
    for (i = 0; i < this.reduceList.length; i++)
      this.reduceList[i] = null; 
    this.numNodes = 0;
    this.maxNodes = 0;
    this.root = null;
    this.currSize = 0;
    this.currLevel = 8;
    i = this.src.getWidth();
    int j = this.src.getHeight();
    for (int k = 0; k < j; k++) {
      for (int m = 0; m < i; m++) {
        Color color = getSrcColor(i - m - 1, j - k - 1);
        if (this.transparency != 1 && color.getAlpha() != 255) {
          if (this.transColor == null) {
            this.requiredSize--;
            this.transColor = new ColorNode();
            this.transColor.isLeaf = true;
          } 
          this.transColor = insertNode(this.transColor, color, 0);
        } else {
          this.root = insertNode(this.root, color, 0);
        } 
        if (this.currSize > this.requiredSize)
          reduceTree(); 
      } 
    } 
  }
  
  protected ColorNode insertNode(ColorNode paramColorNode, Color paramColor, int paramInt) {
    if (paramColorNode == null) {
      paramColorNode = new ColorNode();
      this.numNodes++;
      if (this.numNodes > this.maxNodes)
        this.maxNodes = this.numNodes; 
      paramColorNode.level = paramInt;
      paramColorNode.isLeaf = (paramInt > 8);
      if (paramColorNode.isLeaf)
        this.currSize++; 
    } 
    paramColorNode.colorCount++;
    paramColorNode.red += paramColor.getRed();
    paramColorNode.green += paramColor.getGreen();
    paramColorNode.blue += paramColor.getBlue();
    if (!paramColorNode.isLeaf) {
      int i = getBranchIndex(paramColor, paramInt);
      if (paramColorNode.children[i] == null) {
        paramColorNode.childCount++;
        if (paramColorNode.childCount == 2) {
          paramColorNode.nextReducible = this.reduceList[paramInt];
          this.reduceList[paramInt] = paramColorNode;
        } 
      } 
      paramColorNode.children[i] = insertNode(paramColorNode.children[i], paramColor, paramInt + 1);
    } 
    return paramColorNode;
  }
  
  protected IndexColorModel getIndexColorModel() {
    int i = this.currSize;
    if (this.transColor != null)
      i++; 
    byte[] arrayOfByte1 = new byte[i];
    byte[] arrayOfByte2 = new byte[i];
    byte[] arrayOfByte3 = new byte[i];
    byte b = 0;
    this.palette = new ColorNode[i];
    if (this.transColor != null)
      b++; 
    if (this.root != null)
      findPaletteEntry(this.root, b, arrayOfByte1, arrayOfByte2, arrayOfByte3); 
    IndexColorModel indexColorModel = null;
    if (this.transColor != null) {
      indexColorModel = new IndexColorModel(8, i, arrayOfByte1, arrayOfByte2, arrayOfByte3, 0);
    } else {
      indexColorModel = new IndexColorModel(8, this.currSize, arrayOfByte1, arrayOfByte2, arrayOfByte3);
    } 
    return indexColorModel;
  }
  
  protected int findPaletteEntry(ColorNode paramColorNode, int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    if (paramColorNode.isLeaf) {
      paramArrayOfByte1[paramInt] = (byte)(int)(paramColorNode.red / paramColorNode.colorCount);
      paramArrayOfByte2[paramInt] = (byte)(int)(paramColorNode.green / paramColorNode.colorCount);
      paramArrayOfByte3[paramInt] = (byte)(int)(paramColorNode.blue / paramColorNode.colorCount);
      paramColorNode.paletteIndex = paramInt;
      this.palette[paramInt] = paramColorNode;
      paramInt++;
    } else {
      for (byte b = 0; b < 8; b++) {
        if (paramColorNode.children[b] != null)
          paramInt = findPaletteEntry(paramColorNode.children[b], paramInt, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3); 
      } 
    } 
    return paramInt;
  }
  
  protected int getBranchIndex(Color paramColor, int paramInt) {
    if (paramInt > 8 || paramInt < 0)
      throw new IllegalArgumentException("Invalid octree node depth: " + paramInt); 
    int i = 8 - paramInt;
    int j = true & (0xFF & paramColor.getRed()) >> i;
    int k = true & (0xFF & paramColor.getGreen()) >> i;
    int m = true & (0xFF & paramColor.getBlue()) >> i;
    return j << 2 | k << 1 | m;
  }
  
  protected void reduceTree() {
    int i;
    for (i = this.reduceList.length - 1; this.reduceList[i] == null && i >= 0; i--);
    ColorNode colorNode1 = this.reduceList[i];
    if (colorNode1 == null)
      return; 
    ColorNode colorNode2 = colorNode1;
    int j = colorNode2.colorCount;
    for (byte b1 = 1; colorNode2.nextReducible != null; b1++) {
      if (j > colorNode2.nextReducible.colorCount) {
        colorNode1 = colorNode2;
        j = colorNode2.colorCount;
      } 
      colorNode2 = colorNode2.nextReducible;
    } 
    if (colorNode1 == this.reduceList[i]) {
      this.reduceList[i] = colorNode1.nextReducible;
    } else {
      colorNode2 = colorNode1.nextReducible;
      colorNode1.nextReducible = colorNode2.nextReducible;
      colorNode1 = colorNode2;
    } 
    if (colorNode1.isLeaf)
      return; 
    int k = colorNode1.getLeafChildCount();
    colorNode1.isLeaf = true;
    this.currSize -= k - 1;
    int m = colorNode1.level;
    for (byte b2 = 0; b2 < 8; b2++)
      colorNode1.children[b2] = freeTree(colorNode1.children[b2]); 
    colorNode1.childCount = 0;
  }
  
  protected ColorNode freeTree(ColorNode paramColorNode) {
    if (paramColorNode == null)
      return null; 
    for (byte b = 0; b < 8; b++)
      paramColorNode.children[b] = freeTree(paramColorNode.children[b]); 
    this.numNodes--;
    return null;
  }
  
  protected class ColorNode {
    public boolean isLeaf = false;
    
    public int childCount = 0;
    
    ColorNode[] children = new ColorNode[8];
    
    public int colorCount;
    
    public long red;
    
    public long blue;
    
    public long green;
    
    public int paletteIndex;
    
    public int level = 0;
    
    ColorNode nextReducible;
    
    public ColorNode() {
      for (byte b = 0; b < 8; b++)
        this.children[b] = null; 
      this.colorCount = 0;
      this.red = this.green = this.blue = 0L;
      this.paletteIndex = 0;
    }
    
    public int getLeafChildCount() {
      if (this.isLeaf)
        return 0; 
      int i = 0;
      for (byte b = 0; b < this.children.length; b++) {
        if (this.children[b] != null)
          if ((this.children[b]).isLeaf) {
            i++;
          } else {
            i += this.children[b].getLeafChildCount();
          }  
      } 
      return i;
    }
    
    public int getRGB() {
      int i = (int)this.red / this.colorCount;
      int j = (int)this.green / this.colorCount;
      int k = (int)this.blue / this.colorCount;
      return 0xFF000000 | (0xFF & i) << 16 | (0xFF & j) << 8 | 0xFF & k;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\PaletteBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */