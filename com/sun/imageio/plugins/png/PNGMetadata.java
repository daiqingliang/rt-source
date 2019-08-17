package com.sun.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class PNGMetadata extends IIOMetadata implements Cloneable {
  public static final String nativeMetadataFormatName = "javax_imageio_png_1.0";
  
  protected static final String nativeMetadataFormatClassName = "com.sun.imageio.plugins.png.PNGMetadataFormat";
  
  static final String[] IHDR_colorTypeNames = { "Grayscale", null, "RGB", "Palette", "GrayAlpha", null, "RGBAlpha" };
  
  static final int[] IHDR_numChannels = { 1, 0, 3, 3, 2, 0, 4 };
  
  static final String[] IHDR_bitDepths = { "1", "2", "4", "8", "16" };
  
  static final String[] IHDR_compressionMethodNames = { "deflate" };
  
  static final String[] IHDR_filterMethodNames = { "adaptive" };
  
  static final String[] IHDR_interlaceMethodNames = { "none", "adam7" };
  
  static final String[] iCCP_compressionMethodNames = { "deflate" };
  
  static final String[] zTXt_compressionMethodNames = { "deflate" };
  
  public static final int PHYS_UNIT_UNKNOWN = 0;
  
  public static final int PHYS_UNIT_METER = 1;
  
  static final String[] unitSpecifierNames = { "unknown", "meter" };
  
  static final String[] renderingIntentNames = { "Perceptual", "Relative colorimetric", "Saturation", "Absolute colorimetric" };
  
  static final String[] colorSpaceTypeNames = { "GRAY", null, "RGB", "RGB", "GRAY", null, "RGB" };
  
  public boolean IHDR_present;
  
  public int IHDR_width;
  
  public int IHDR_height;
  
  public int IHDR_bitDepth;
  
  public int IHDR_colorType;
  
  public int IHDR_compressionMethod;
  
  public int IHDR_filterMethod;
  
  public int IHDR_interlaceMethod;
  
  public boolean PLTE_present;
  
  public byte[] PLTE_red;
  
  public byte[] PLTE_green;
  
  public byte[] PLTE_blue;
  
  public int[] PLTE_order = null;
  
  public boolean bKGD_present;
  
  public int bKGD_colorType;
  
  public int bKGD_index;
  
  public int bKGD_gray;
  
  public int bKGD_red;
  
  public int bKGD_green;
  
  public int bKGD_blue;
  
  public boolean cHRM_present;
  
  public int cHRM_whitePointX;
  
  public int cHRM_whitePointY;
  
  public int cHRM_redX;
  
  public int cHRM_redY;
  
  public int cHRM_greenX;
  
  public int cHRM_greenY;
  
  public int cHRM_blueX;
  
  public int cHRM_blueY;
  
  public boolean gAMA_present;
  
  public int gAMA_gamma;
  
  public boolean hIST_present;
  
  public char[] hIST_histogram;
  
  public boolean iCCP_present;
  
  public String iCCP_profileName;
  
  public int iCCP_compressionMethod;
  
  public byte[] iCCP_compressedProfile;
  
  public ArrayList<String> iTXt_keyword = new ArrayList();
  
  public ArrayList<Boolean> iTXt_compressionFlag = new ArrayList();
  
  public ArrayList<Integer> iTXt_compressionMethod = new ArrayList();
  
  public ArrayList<String> iTXt_languageTag = new ArrayList();
  
  public ArrayList<String> iTXt_translatedKeyword = new ArrayList();
  
  public ArrayList<String> iTXt_text = new ArrayList();
  
  public boolean pHYs_present;
  
  public int pHYs_pixelsPerUnitXAxis;
  
  public int pHYs_pixelsPerUnitYAxis;
  
  public int pHYs_unitSpecifier;
  
  public boolean sBIT_present;
  
  public int sBIT_colorType;
  
  public int sBIT_grayBits;
  
  public int sBIT_redBits;
  
  public int sBIT_greenBits;
  
  public int sBIT_blueBits;
  
  public int sBIT_alphaBits;
  
  public boolean sPLT_present;
  
  public String sPLT_paletteName;
  
  public int sPLT_sampleDepth;
  
  public int[] sPLT_red;
  
  public int[] sPLT_green;
  
  public int[] sPLT_blue;
  
  public int[] sPLT_alpha;
  
  public int[] sPLT_frequency;
  
  public boolean sRGB_present;
  
  public int sRGB_renderingIntent;
  
  public ArrayList<String> tEXt_keyword = new ArrayList();
  
  public ArrayList<String> tEXt_text = new ArrayList();
  
  public boolean tIME_present;
  
  public int tIME_year;
  
  public int tIME_month;
  
  public int tIME_day;
  
  public int tIME_hour;
  
  public int tIME_minute;
  
  public int tIME_second;
  
  public boolean tRNS_present;
  
  public int tRNS_colorType;
  
  public byte[] tRNS_alpha;
  
  public int tRNS_gray;
  
  public int tRNS_red;
  
  public int tRNS_green;
  
  public int tRNS_blue;
  
  public ArrayList<String> zTXt_keyword = new ArrayList();
  
  public ArrayList<Integer> zTXt_compressionMethod = new ArrayList();
  
  public ArrayList<String> zTXt_text = new ArrayList();
  
  public ArrayList<String> unknownChunkType = new ArrayList();
  
  public ArrayList<byte[]> unknownChunkData = new ArrayList();
  
  public PNGMetadata() { super(true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null); }
  
  public PNGMetadata(IIOMetadata paramIIOMetadata) {}
  
  public void initialize(ImageTypeSpecifier paramImageTypeSpecifier, int paramInt) {
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    int[] arrayOfInt = sampleModel.getSampleSize();
    int i = arrayOfInt[0];
    for (byte b = 1; b < arrayOfInt.length; b++) {
      if (arrayOfInt[b] > i)
        i = arrayOfInt[b]; 
    } 
    if (arrayOfInt.length > 1 && i < 8)
      i = 8; 
    if (i > 2 && i < 4) {
      i = 4;
    } else if (i > 4 && i < 8) {
      i = 8;
    } else if (i > 8 && i < 16) {
      i = 16;
    } else if (i > 16) {
      throw new RuntimeException("bitDepth > 16!");
    } 
    this.IHDR_bitDepth = i;
    if (colorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)colorModel;
      int j = indexColorModel.getMapSize();
      byte[] arrayOfByte1 = new byte[j];
      indexColorModel.getReds(arrayOfByte1);
      byte[] arrayOfByte2 = new byte[j];
      indexColorModel.getGreens(arrayOfByte2);
      byte[] arrayOfByte3 = new byte[j];
      indexColorModel.getBlues(arrayOfByte3);
      boolean bool = false;
      if (!this.IHDR_present || this.IHDR_colorType != 3) {
        bool = true;
        int k = 255 / ((1 << this.IHDR_bitDepth) - 1);
        for (int m = 0; m < j; m++) {
          byte b1 = arrayOfByte1[m];
          if (b1 != (byte)(m * k) || b1 != arrayOfByte2[m] || b1 != arrayOfByte3[m]) {
            bool = false;
            break;
          } 
        } 
      } 
      boolean bool1 = colorModel.hasAlpha();
      byte[] arrayOfByte4 = null;
      if (bool1) {
        arrayOfByte4 = new byte[j];
        indexColorModel.getAlphas(arrayOfByte4);
      } 
      if (bool && bool1 && (i == 8 || i == 16)) {
        this.IHDR_colorType = 4;
      } else if (bool && !bool1) {
        this.IHDR_colorType = 0;
      } else {
        this.IHDR_colorType = 3;
        this.PLTE_present = true;
        this.PLTE_order = null;
        this.PLTE_red = (byte[])arrayOfByte1.clone();
        this.PLTE_green = (byte[])arrayOfByte2.clone();
        this.PLTE_blue = (byte[])arrayOfByte3.clone();
        if (bool1) {
          this.tRNS_present = true;
          this.tRNS_colorType = 3;
          this.PLTE_order = new int[arrayOfByte4.length];
          byte[] arrayOfByte5 = new byte[arrayOfByte4.length];
          byte b1 = 0;
          byte b2;
          for (b2 = 0; b2 < arrayOfByte4.length; b2++) {
            if (arrayOfByte4[b2] != -1) {
              this.PLTE_order[b2] = b1;
              arrayOfByte5[b1] = arrayOfByte4[b2];
              b1++;
            } 
          } 
          b2 = b1;
          for (byte b3 = 0; b3 < arrayOfByte4.length; b3++) {
            if (arrayOfByte4[b3] == -1)
              this.PLTE_order[b3] = b1++; 
          } 
          byte[] arrayOfByte6 = this.PLTE_red;
          byte[] arrayOfByte7 = this.PLTE_green;
          byte[] arrayOfByte8 = this.PLTE_blue;
          int k = arrayOfByte6.length;
          this.PLTE_red = new byte[k];
          this.PLTE_green = new byte[k];
          this.PLTE_blue = new byte[k];
          for (byte b4 = 0; b4 < k; b4++) {
            this.PLTE_red[this.PLTE_order[b4]] = arrayOfByte6[b4];
            this.PLTE_green[this.PLTE_order[b4]] = arrayOfByte7[b4];
            this.PLTE_blue[this.PLTE_order[b4]] = arrayOfByte8[b4];
          } 
          this.tRNS_alpha = new byte[b2];
          System.arraycopy(arrayOfByte5, 0, this.tRNS_alpha, 0, b2);
        } 
      } 
    } else if (paramInt == 1) {
      this.IHDR_colorType = 0;
    } else if (paramInt == 2) {
      this.IHDR_colorType = 4;
    } else if (paramInt == 3) {
      this.IHDR_colorType = 2;
    } else if (paramInt == 4) {
      this.IHDR_colorType = 6;
    } else {
      throw new RuntimeException("Number of bands not 1-4!");
    } 
    this.IHDR_present = true;
  }
  
  public boolean isReadOnly() { return false; }
  
  private ArrayList<byte[]> cloneBytesArrayList(ArrayList<byte[]> paramArrayList) {
    if (paramArrayList == null)
      return null; 
    ArrayList arrayList = new ArrayList(paramArrayList.size());
    for (byte[] arrayOfByte : paramArrayList)
      arrayList.add((arrayOfByte == null) ? null : (byte[])arrayOfByte.clone()); 
    return arrayList;
  }
  
  public Object clone() {
    PNGMetadata pNGMetadata;
    try {
      pNGMetadata = (PNGMetadata)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
    pNGMetadata.unknownChunkData = cloneBytesArrayList(this.unknownChunkData);
    return pNGMetadata;
  }
  
  public Node getAsTree(String paramString) {
    if (paramString.equals("javax_imageio_png_1.0"))
      return getNativeTree(); 
    if (paramString.equals("javax_imageio_1.0"))
      return getStandardTree(); 
    throw new IllegalArgumentException("Not a recognized format!");
  }
  
  private Node getNativeTree() {
    IIOMetadataNode iIOMetadataNode1 = null;
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("javax_imageio_png_1.0");
    if (this.IHDR_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("IHDR");
      iIOMetadataNode.setAttribute("width", Integer.toString(this.IHDR_width));
      iIOMetadataNode.setAttribute("height", Integer.toString(this.IHDR_height));
      iIOMetadataNode.setAttribute("bitDepth", Integer.toString(this.IHDR_bitDepth));
      iIOMetadataNode.setAttribute("colorType", IHDR_colorTypeNames[this.IHDR_colorType]);
      iIOMetadataNode.setAttribute("compressionMethod", IHDR_compressionMethodNames[this.IHDR_compressionMethod]);
      iIOMetadataNode.setAttribute("filterMethod", IHDR_filterMethodNames[this.IHDR_filterMethod]);
      iIOMetadataNode.setAttribute("interlaceMethod", IHDR_interlaceMethodNames[this.IHDR_interlaceMethod]);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.PLTE_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("PLTE");
      int i = this.PLTE_red.length;
      for (byte b = 0; b < i; b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("PLTEEntry");
        iIOMetadataNode3.setAttribute("index", Integer.toString(b));
        iIOMetadataNode3.setAttribute("red", Integer.toString(this.PLTE_red[b] & 0xFF));
        iIOMetadataNode3.setAttribute("green", Integer.toString(this.PLTE_green[b] & 0xFF));
        iIOMetadataNode3.setAttribute("blue", Integer.toString(this.PLTE_blue[b] & 0xFF));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.bKGD_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("bKGD");
      if (this.bKGD_colorType == 3) {
        iIOMetadataNode1 = new IIOMetadataNode("bKGD_Palette");
        iIOMetadataNode1.setAttribute("index", Integer.toString(this.bKGD_index));
      } else if (this.bKGD_colorType == 0) {
        iIOMetadataNode1 = new IIOMetadataNode("bKGD_Grayscale");
        iIOMetadataNode1.setAttribute("gray", Integer.toString(this.bKGD_gray));
      } else if (this.bKGD_colorType == 2) {
        iIOMetadataNode1 = new IIOMetadataNode("bKGD_RGB");
        iIOMetadataNode1.setAttribute("red", Integer.toString(this.bKGD_red));
        iIOMetadataNode1.setAttribute("green", Integer.toString(this.bKGD_green));
        iIOMetadataNode1.setAttribute("blue", Integer.toString(this.bKGD_blue));
      } 
      iIOMetadataNode.appendChild(iIOMetadataNode1);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.cHRM_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("cHRM");
      iIOMetadataNode.setAttribute("whitePointX", Integer.toString(this.cHRM_whitePointX));
      iIOMetadataNode.setAttribute("whitePointY", Integer.toString(this.cHRM_whitePointY));
      iIOMetadataNode.setAttribute("redX", Integer.toString(this.cHRM_redX));
      iIOMetadataNode.setAttribute("redY", Integer.toString(this.cHRM_redY));
      iIOMetadataNode.setAttribute("greenX", Integer.toString(this.cHRM_greenX));
      iIOMetadataNode.setAttribute("greenY", Integer.toString(this.cHRM_greenY));
      iIOMetadataNode.setAttribute("blueX", Integer.toString(this.cHRM_blueX));
      iIOMetadataNode.setAttribute("blueY", Integer.toString(this.cHRM_blueY));
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.gAMA_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("gAMA");
      iIOMetadataNode.setAttribute("value", Integer.toString(this.gAMA_gamma));
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.hIST_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("hIST");
      for (byte b = 0; b < this.hIST_histogram.length; b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("hISTEntry");
        iIOMetadataNode3.setAttribute("index", Integer.toString(b));
        iIOMetadataNode3.setAttribute("value", Integer.toString(this.hIST_histogram[b]));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.iCCP_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("iCCP");
      iIOMetadataNode.setAttribute("profileName", this.iCCP_profileName);
      iIOMetadataNode.setAttribute("compressionMethod", iCCP_compressionMethodNames[this.iCCP_compressionMethod]);
      Object object = this.iCCP_compressedProfile;
      if (object != null)
        object = ((byte[])object).clone(); 
      iIOMetadataNode.setUserObject(object);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.iTXt_keyword.size() > 0) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("iTXt");
      for (byte b = 0; b < this.iTXt_keyword.size(); b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("iTXtEntry");
        iIOMetadataNode3.setAttribute("keyword", (String)this.iTXt_keyword.get(b));
        iIOMetadataNode3.setAttribute("compressionFlag", ((Boolean)this.iTXt_compressionFlag.get(b)).booleanValue() ? "TRUE" : "FALSE");
        iIOMetadataNode3.setAttribute("compressionMethod", ((Integer)this.iTXt_compressionMethod.get(b)).toString());
        iIOMetadataNode3.setAttribute("languageTag", (String)this.iTXt_languageTag.get(b));
        iIOMetadataNode3.setAttribute("translatedKeyword", (String)this.iTXt_translatedKeyword.get(b));
        iIOMetadataNode3.setAttribute("text", (String)this.iTXt_text.get(b));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.pHYs_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("pHYs");
      iIOMetadataNode.setAttribute("pixelsPerUnitXAxis", Integer.toString(this.pHYs_pixelsPerUnitXAxis));
      iIOMetadataNode.setAttribute("pixelsPerUnitYAxis", Integer.toString(this.pHYs_pixelsPerUnitYAxis));
      iIOMetadataNode.setAttribute("unitSpecifier", unitSpecifierNames[this.pHYs_unitSpecifier]);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.sBIT_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("sBIT");
      if (this.sBIT_colorType == 0) {
        iIOMetadataNode1 = new IIOMetadataNode("sBIT_Grayscale");
        iIOMetadataNode1.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
      } else if (this.sBIT_colorType == 4) {
        iIOMetadataNode1 = new IIOMetadataNode("sBIT_GrayAlpha");
        iIOMetadataNode1.setAttribute("gray", Integer.toString(this.sBIT_grayBits));
        iIOMetadataNode1.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
      } else if (this.sBIT_colorType == 2) {
        iIOMetadataNode1 = new IIOMetadataNode("sBIT_RGB");
        iIOMetadataNode1.setAttribute("red", Integer.toString(this.sBIT_redBits));
        iIOMetadataNode1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
        iIOMetadataNode1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
      } else if (this.sBIT_colorType == 6) {
        iIOMetadataNode1 = new IIOMetadataNode("sBIT_RGBAlpha");
        iIOMetadataNode1.setAttribute("red", Integer.toString(this.sBIT_redBits));
        iIOMetadataNode1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
        iIOMetadataNode1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
        iIOMetadataNode1.setAttribute("alpha", Integer.toString(this.sBIT_alphaBits));
      } else if (this.sBIT_colorType == 3) {
        iIOMetadataNode1 = new IIOMetadataNode("sBIT_Palette");
        iIOMetadataNode1.setAttribute("red", Integer.toString(this.sBIT_redBits));
        iIOMetadataNode1.setAttribute("green", Integer.toString(this.sBIT_greenBits));
        iIOMetadataNode1.setAttribute("blue", Integer.toString(this.sBIT_blueBits));
      } 
      iIOMetadataNode.appendChild(iIOMetadataNode1);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.sPLT_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("sPLT");
      iIOMetadataNode.setAttribute("name", this.sPLT_paletteName);
      iIOMetadataNode.setAttribute("sampleDepth", Integer.toString(this.sPLT_sampleDepth));
      int i = this.sPLT_red.length;
      for (byte b = 0; b < i; b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("sPLTEntry");
        iIOMetadataNode3.setAttribute("index", Integer.toString(b));
        iIOMetadataNode3.setAttribute("red", Integer.toString(this.sPLT_red[b]));
        iIOMetadataNode3.setAttribute("green", Integer.toString(this.sPLT_green[b]));
        iIOMetadataNode3.setAttribute("blue", Integer.toString(this.sPLT_blue[b]));
        iIOMetadataNode3.setAttribute("alpha", Integer.toString(this.sPLT_alpha[b]));
        iIOMetadataNode3.setAttribute("frequency", Integer.toString(this.sPLT_frequency[b]));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.sRGB_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("sRGB");
      iIOMetadataNode.setAttribute("renderingIntent", renderingIntentNames[this.sRGB_renderingIntent]);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.tEXt_keyword.size() > 0) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("tEXt");
      for (byte b = 0; b < this.tEXt_keyword.size(); b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("tEXtEntry");
        iIOMetadataNode3.setAttribute("keyword", (String)this.tEXt_keyword.get(b));
        iIOMetadataNode3.setAttribute("value", (String)this.tEXt_text.get(b));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.tIME_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("tIME");
      iIOMetadataNode.setAttribute("year", Integer.toString(this.tIME_year));
      iIOMetadataNode.setAttribute("month", Integer.toString(this.tIME_month));
      iIOMetadataNode.setAttribute("day", Integer.toString(this.tIME_day));
      iIOMetadataNode.setAttribute("hour", Integer.toString(this.tIME_hour));
      iIOMetadataNode.setAttribute("minute", Integer.toString(this.tIME_minute));
      iIOMetadataNode.setAttribute("second", Integer.toString(this.tIME_second));
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.tRNS_present) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("tRNS");
      if (this.tRNS_colorType == 3) {
        iIOMetadataNode1 = new IIOMetadataNode("tRNS_Palette");
        for (byte b = 0; b < this.tRNS_alpha.length; b++) {
          IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("tRNS_PaletteEntry");
          iIOMetadataNode3.setAttribute("index", Integer.toString(b));
          iIOMetadataNode3.setAttribute("alpha", Integer.toString(this.tRNS_alpha[b] & 0xFF));
          iIOMetadataNode1.appendChild(iIOMetadataNode3);
        } 
      } else if (this.tRNS_colorType == 0) {
        iIOMetadataNode1 = new IIOMetadataNode("tRNS_Grayscale");
        iIOMetadataNode1.setAttribute("gray", Integer.toString(this.tRNS_gray));
      } else if (this.tRNS_colorType == 2) {
        iIOMetadataNode1 = new IIOMetadataNode("tRNS_RGB");
        iIOMetadataNode1.setAttribute("red", Integer.toString(this.tRNS_red));
        iIOMetadataNode1.setAttribute("green", Integer.toString(this.tRNS_green));
        iIOMetadataNode1.setAttribute("blue", Integer.toString(this.tRNS_blue));
      } 
      iIOMetadataNode.appendChild(iIOMetadataNode1);
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.zTXt_keyword.size() > 0) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("zTXt");
      for (byte b = 0; b < this.zTXt_keyword.size(); b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("zTXtEntry");
        iIOMetadataNode3.setAttribute("keyword", (String)this.zTXt_keyword.get(b));
        int i = ((Integer)this.zTXt_compressionMethod.get(b)).intValue();
        iIOMetadataNode3.setAttribute("compressionMethod", zTXt_compressionMethodNames[i]);
        iIOMetadataNode3.setAttribute("text", (String)this.zTXt_text.get(b));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    if (this.unknownChunkType.size() > 0) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("UnknownChunks");
      for (byte b = 0; b < this.unknownChunkType.size(); b++) {
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("UnknownChunk");
        iIOMetadataNode3.setAttribute("type", (String)this.unknownChunkType.get(b));
        iIOMetadataNode3.setUserObject((byte[])this.unknownChunkData.get(b));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
      } 
      iIOMetadataNode2.appendChild(iIOMetadataNode);
    } 
    return iIOMetadataNode2;
  }
  
  private int getNumChannels() {
    int i = IHDR_numChannels[this.IHDR_colorType];
    if (this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType)
      i = 4; 
    return i;
  }
  
  public IIOMetadataNode getStandardChromaNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    iIOMetadataNode2.setAttribute("name", colorSpaceTypeNames[this.IHDR_colorType]);
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("NumChannels");
    iIOMetadataNode2.setAttribute("value", Integer.toString(getNumChannels()));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.gAMA_present) {
      iIOMetadataNode2 = new IIOMetadataNode("Gamma");
      iIOMetadataNode2.setAttribute("value", Float.toString(this.gAMA_gamma * 1.0E-5F));
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    iIOMetadataNode2 = new IIOMetadataNode("BlackIsZero");
    iIOMetadataNode2.setAttribute("value", "TRUE");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.PLTE_present) {
      boolean bool = (this.tRNS_present && this.tRNS_colorType == 3) ? 1 : 0;
      iIOMetadataNode2 = new IIOMetadataNode("Palette");
      for (byte b = 0; b < this.PLTE_red.length; b++) {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("PaletteEntry");
        iIOMetadataNode.setAttribute("index", Integer.toString(b));
        iIOMetadataNode.setAttribute("red", Integer.toString(this.PLTE_red[b] & 0xFF));
        iIOMetadataNode.setAttribute("green", Integer.toString(this.PLTE_green[b] & 0xFF));
        iIOMetadataNode.setAttribute("blue", Integer.toString(this.PLTE_blue[b] & 0xFF));
        if (bool) {
          byte b1 = (b < this.tRNS_alpha.length) ? (this.tRNS_alpha[b] & 0xFF) : 255;
          iIOMetadataNode.setAttribute("alpha", Integer.toString(b1));
        } 
        iIOMetadataNode2.appendChild(iIOMetadataNode);
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    if (this.bKGD_present) {
      if (this.bKGD_colorType == 3) {
        iIOMetadataNode2 = new IIOMetadataNode("BackgroundIndex");
        iIOMetadataNode2.setAttribute("value", Integer.toString(this.bKGD_index));
      } else {
        int k;
        int j;
        int i;
        iIOMetadataNode2 = new IIOMetadataNode("BackgroundColor");
        if (this.bKGD_colorType == 0) {
          i = j = k = this.bKGD_gray;
        } else {
          i = this.bKGD_red;
          j = this.bKGD_green;
          k = this.bKGD_blue;
        } 
        iIOMetadataNode2.setAttribute("red", Integer.toString(i));
        iIOMetadataNode2.setAttribute("green", Integer.toString(j));
        iIOMetadataNode2.setAttribute("blue", Integer.toString(k));
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardCompressionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    iIOMetadataNode2.setAttribute("value", "deflate");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("Lossless");
    iIOMetadataNode2.setAttribute("value", "TRUE");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("NumProgressiveScans");
    iIOMetadataNode2.setAttribute("value", (this.IHDR_interlaceMethod == 0) ? "1" : "7");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  private String repeat(String paramString, int paramInt) {
    if (paramInt == 1)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer((paramString.length() + 1) * paramInt - 1);
    stringBuffer.append(paramString);
    for (byte b = 1; b < paramInt; b++) {
      stringBuffer.append(" ");
      stringBuffer.append(paramString);
    } 
    return stringBuffer.toString();
  }
  
  public IIOMetadataNode getStandardDataNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Data");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("PlanarConfiguration");
    iIOMetadataNode2.setAttribute("value", "PixelInterleaved");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("SampleFormat");
    iIOMetadataNode2.setAttribute("value", (this.IHDR_colorType == 3) ? "Index" : "UnsignedIntegral");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    String str = Integer.toString(this.IHDR_bitDepth);
    iIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
    iIOMetadataNode2.setAttribute("value", repeat(str, getNumChannels()));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.sBIT_present) {
      String str1;
      iIOMetadataNode2 = new IIOMetadataNode("SignificantBitsPerSample");
      if (this.sBIT_colorType == 0 || this.sBIT_colorType == 4) {
        str1 = Integer.toString(this.sBIT_grayBits);
      } else {
        str1 = Integer.toString(this.sBIT_redBits) + " " + Integer.toString(this.sBIT_greenBits) + " " + Integer.toString(this.sBIT_blueBits);
      } 
      if (this.sBIT_colorType == 4 || this.sBIT_colorType == 6)
        str1 = str1 + " " + Integer.toString(this.sBIT_alphaBits); 
      iIOMetadataNode2.setAttribute("value", str1);
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDimensionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("PixelAspectRatio");
    float f = this.pHYs_present ? (this.pHYs_pixelsPerUnitXAxis / this.pHYs_pixelsPerUnitYAxis) : 1.0F;
    iIOMetadataNode2.setAttribute("value", Float.toString(f));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    iIOMetadataNode2.setAttribute("value", "Normal");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.pHYs_present && this.pHYs_unitSpecifier == 1) {
      iIOMetadataNode2 = new IIOMetadataNode("HorizontalPixelSize");
      iIOMetadataNode2.setAttribute("value", Float.toString(1000.0F / this.pHYs_pixelsPerUnitXAxis));
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
      iIOMetadataNode2 = new IIOMetadataNode("VerticalPixelSize");
      iIOMetadataNode2.setAttribute("value", Float.toString(1000.0F / this.pHYs_pixelsPerUnitYAxis));
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardDocumentNode() {
    if (!this.tIME_present)
      return null; 
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Document");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("ImageModificationTime");
    iIOMetadataNode2.setAttribute("year", Integer.toString(this.tIME_year));
    iIOMetadataNode2.setAttribute("month", Integer.toString(this.tIME_month));
    iIOMetadataNode2.setAttribute("day", Integer.toString(this.tIME_day));
    iIOMetadataNode2.setAttribute("hour", Integer.toString(this.tIME_hour));
    iIOMetadataNode2.setAttribute("minute", Integer.toString(this.tIME_minute));
    iIOMetadataNode2.setAttribute("second", Integer.toString(this.tIME_second));
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTextNode() {
    int i = this.tEXt_keyword.size() + this.iTXt_keyword.size() + this.zTXt_keyword.size();
    if (i == 0)
      return null; 
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Text");
    IIOMetadataNode iIOMetadataNode2 = null;
    byte b;
    for (b = 0; b < this.tEXt_keyword.size(); b++) {
      iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      iIOMetadataNode2.setAttribute("keyword", (String)this.tEXt_keyword.get(b));
      iIOMetadataNode2.setAttribute("value", (String)this.tEXt_text.get(b));
      iIOMetadataNode2.setAttribute("encoding", "ISO-8859-1");
      iIOMetadataNode2.setAttribute("compression", "none");
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    for (b = 0; b < this.iTXt_keyword.size(); b++) {
      iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      iIOMetadataNode2.setAttribute("keyword", (String)this.iTXt_keyword.get(b));
      iIOMetadataNode2.setAttribute("value", (String)this.iTXt_text.get(b));
      iIOMetadataNode2.setAttribute("language", (String)this.iTXt_languageTag.get(b));
      if (((Boolean)this.iTXt_compressionFlag.get(b)).booleanValue()) {
        iIOMetadataNode2.setAttribute("compression", "zip");
      } else {
        iIOMetadataNode2.setAttribute("compression", "none");
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    for (b = 0; b < this.zTXt_keyword.size(); b++) {
      iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
      iIOMetadataNode2.setAttribute("keyword", (String)this.zTXt_keyword.get(b));
      iIOMetadataNode2.setAttribute("value", (String)this.zTXt_text.get(b));
      iIOMetadataNode2.setAttribute("compression", "zip");
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  public IIOMetadataNode getStandardTransparencyNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Transparency");
    IIOMetadataNode iIOMetadataNode2 = null;
    iIOMetadataNode2 = new IIOMetadataNode("Alpha");
    boolean bool = (this.IHDR_colorType == 6 || this.IHDR_colorType == 4 || (this.IHDR_colorType == 3 && this.tRNS_present && this.tRNS_colorType == this.IHDR_colorType && this.tRNS_alpha != null)) ? 1 : 0;
    iIOMetadataNode2.setAttribute("value", bool ? "nonpremultipled" : "none");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    if (this.tRNS_present) {
      iIOMetadataNode2 = new IIOMetadataNode("TransparentColor");
      if (this.tRNS_colorType == 2) {
        iIOMetadataNode2.setAttribute("value", Integer.toString(this.tRNS_red) + " " + Integer.toString(this.tRNS_green) + " " + Integer.toString(this.tRNS_blue));
      } else if (this.tRNS_colorType == 0) {
        iIOMetadataNode2.setAttribute("value", Integer.toString(this.tRNS_gray));
      } 
      iIOMetadataNode1.appendChild(iIOMetadataNode2);
    } 
    return iIOMetadataNode1;
  }
  
  private void fatal(Node paramNode, String paramString) throws IIOInvalidTreeException { throw new IIOInvalidTreeException(paramString, paramNode); }
  
  private String getStringAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString1);
    if (node == null) {
      if (!paramBoolean)
        return paramString2; 
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    } 
    return node.getNodeValue();
  }
  
  private int getIntAttribute(Node paramNode, String paramString, int paramInt, boolean paramBoolean) throws IIOInvalidTreeException {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean);
    return (str == null) ? paramInt : Integer.parseInt(str);
  }
  
  private float getFloatAttribute(Node paramNode, String paramString, float paramFloat, boolean paramBoolean) throws IIOInvalidTreeException {
    String str = getStringAttribute(paramNode, paramString, null, paramBoolean);
    return (str == null) ? paramFloat : Float.parseFloat(str);
  }
  
  private int getIntAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getIntAttribute(paramNode, paramString, -1, true); }
  
  private float getFloatAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getFloatAttribute(paramNode, paramString, -1.0F, true); }
  
  private boolean getBooleanAttribute(Node paramNode, String paramString, boolean paramBoolean1, boolean paramBoolean2) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString);
    if (node == null) {
      if (!paramBoolean2)
        return paramBoolean1; 
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    } 
    String str = node.getNodeValue();
    if (str.equals("TRUE") || str.equals("true"))
      return true; 
    if (str.equals("FALSE") || str.equals("false"))
      return false; 
    fatal(paramNode, "Attribute " + paramString + " must be 'TRUE' or 'FALSE'!");
    return false;
  }
  
  private boolean getBooleanAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getBooleanAttribute(paramNode, paramString, false, true); }
  
  private int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString);
    if (node == null) {
      if (!paramBoolean)
        return paramInt; 
      fatal(paramNode, "Required attribute " + paramString + " not present!");
    } 
    String str = node.getNodeValue();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (str.equals(paramArrayOfString[b]))
        return b; 
    } 
    fatal(paramNode, "Illegal value for attribute " + paramString + "!");
    return -1;
  }
  
  private int getEnumeratedAttribute(Node paramNode, String paramString, String[] paramArrayOfString) throws IIOInvalidTreeException { return getEnumeratedAttribute(paramNode, paramString, paramArrayOfString, -1, true); }
  
  private String getAttribute(Node paramNode, String paramString1, String paramString2, boolean paramBoolean) throws IIOInvalidTreeException {
    Node node = paramNode.getAttributes().getNamedItem(paramString1);
    if (node == null) {
      if (!paramBoolean)
        return paramString2; 
      fatal(paramNode, "Required attribute " + paramString1 + " not present!");
    } 
    return node.getNodeValue();
  }
  
  private String getAttribute(Node paramNode, String paramString) throws IIOInvalidTreeException { return getAttribute(paramNode, paramString, null, true); }
  
  public void mergeTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    if (paramString.equals("javax_imageio_png_1.0")) {
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
  
  private void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException {
    Node node = paramNode;
    if (!node.getNodeName().equals("javax_imageio_png_1.0"))
      fatal(node, "Root must be javax_imageio_png_1.0"); 
    for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
      String str = node.getNodeName();
      if (str.equals("IHDR")) {
        this.IHDR_width = getIntAttribute(node, "width");
        this.IHDR_height = getIntAttribute(node, "height");
        this.IHDR_bitDepth = Integer.valueOf(IHDR_bitDepths[getEnumeratedAttribute(node, "bitDepth", IHDR_bitDepths)]).intValue();
        this.IHDR_colorType = getEnumeratedAttribute(node, "colorType", IHDR_colorTypeNames);
        this.IHDR_compressionMethod = getEnumeratedAttribute(node, "compressionMethod", IHDR_compressionMethodNames);
        this.IHDR_filterMethod = getEnumeratedAttribute(node, "filterMethod", IHDR_filterMethodNames);
        this.IHDR_interlaceMethod = getEnumeratedAttribute(node, "interlaceMethod", IHDR_interlaceMethodNames);
        this.IHDR_present = true;
      } else if (str.equals("PLTE")) {
        byte[] arrayOfByte1 = new byte[256];
        byte[] arrayOfByte2 = new byte[256];
        byte[] arrayOfByte3 = new byte[256];
        int i = -1;
        Node node1 = node.getFirstChild();
        if (node1 == null)
          fatal(node, "Palette has no entries!"); 
        while (node1 != null) {
          if (!node1.getNodeName().equals("PLTEEntry"))
            fatal(node, "Only a PLTEEntry may be a child of a PLTE!"); 
          int k = getIntAttribute(node1, "index");
          if (k < 0 || k > 255)
            fatal(node, "Bad value for PLTEEntry attribute index!"); 
          if (k > i)
            i = k; 
          arrayOfByte1[k] = (byte)getIntAttribute(node1, "red");
          arrayOfByte2[k] = (byte)getIntAttribute(node1, "green");
          arrayOfByte3[k] = (byte)getIntAttribute(node1, "blue");
          node1 = node1.getNextSibling();
        } 
        int j = i + 1;
        this.PLTE_red = new byte[j];
        this.PLTE_green = new byte[j];
        this.PLTE_blue = new byte[j];
        System.arraycopy(arrayOfByte1, 0, this.PLTE_red, 0, j);
        System.arraycopy(arrayOfByte2, 0, this.PLTE_green, 0, j);
        System.arraycopy(arrayOfByte3, 0, this.PLTE_blue, 0, j);
        this.PLTE_present = true;
      } else if (str.equals("bKGD")) {
        this.bKGD_present = false;
        Node node1 = node.getFirstChild();
        if (node1 == null)
          fatal(node, "bKGD node has no children!"); 
        String str1 = node1.getNodeName();
        if (str1.equals("bKGD_Palette")) {
          this.bKGD_index = getIntAttribute(node1, "index");
          this.bKGD_colorType = 3;
        } else if (str1.equals("bKGD_Grayscale")) {
          this.bKGD_gray = getIntAttribute(node1, "gray");
          this.bKGD_colorType = 0;
        } else if (str1.equals("bKGD_RGB")) {
          this.bKGD_red = getIntAttribute(node1, "red");
          this.bKGD_green = getIntAttribute(node1, "green");
          this.bKGD_blue = getIntAttribute(node1, "blue");
          this.bKGD_colorType = 2;
        } else {
          fatal(node, "Bad child of a bKGD node!");
        } 
        if (node1.getNextSibling() != null)
          fatal(node, "bKGD node has more than one child!"); 
        this.bKGD_present = true;
      } else if (str.equals("cHRM")) {
        this.cHRM_whitePointX = getIntAttribute(node, "whitePointX");
        this.cHRM_whitePointY = getIntAttribute(node, "whitePointY");
        this.cHRM_redX = getIntAttribute(node, "redX");
        this.cHRM_redY = getIntAttribute(node, "redY");
        this.cHRM_greenX = getIntAttribute(node, "greenX");
        this.cHRM_greenY = getIntAttribute(node, "greenY");
        this.cHRM_blueX = getIntAttribute(node, "blueX");
        this.cHRM_blueY = getIntAttribute(node, "blueY");
        this.cHRM_present = true;
      } else if (str.equals("gAMA")) {
        this.gAMA_gamma = getIntAttribute(node, "value");
        this.gAMA_present = true;
      } else if (str.equals("hIST")) {
        char[] arrayOfChar = new char[256];
        int i = -1;
        Node node1 = node.getFirstChild();
        if (node1 == null)
          fatal(node, "hIST node has no children!"); 
        while (node1 != null) {
          if (!node1.getNodeName().equals("hISTEntry"))
            fatal(node, "Only a hISTEntry may be a child of a hIST!"); 
          int k = getIntAttribute(node1, "index");
          if (k < 0 || k > 255)
            fatal(node, "Bad value for histEntry attribute index!"); 
          if (k > i)
            i = k; 
          arrayOfChar[k] = (char)getIntAttribute(node1, "value");
          node1 = node1.getNextSibling();
        } 
        int j = i + 1;
        this.hIST_histogram = new char[j];
        System.arraycopy(arrayOfChar, 0, this.hIST_histogram, 0, j);
        this.hIST_present = true;
      } else if (str.equals("iCCP")) {
        this.iCCP_profileName = getAttribute(node, "profileName");
        this.iCCP_compressionMethod = getEnumeratedAttribute(node, "compressionMethod", iCCP_compressionMethodNames);
        Object object = ((IIOMetadataNode)node).getUserObject();
        if (object == null)
          fatal(node, "No ICCP profile present in user object!"); 
        if (!(object instanceof byte[]))
          fatal(node, "User object not a byte array!"); 
        this.iCCP_compressedProfile = (byte[])((byte[])object).clone();
        this.iCCP_present = true;
      } else if (str.equals("iTXt")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          if (!node1.getNodeName().equals("iTXtEntry"))
            fatal(node, "Only an iTXtEntry may be a child of an iTXt!"); 
          String str1 = getAttribute(node1, "keyword");
          if (isValidKeyword(str1)) {
            this.iTXt_keyword.add(str1);
            boolean bool = getBooleanAttribute(node1, "compressionFlag");
            this.iTXt_compressionFlag.add(Boolean.valueOf(bool));
            String str2 = getAttribute(node1, "compressionMethod");
            this.iTXt_compressionMethod.add(Integer.valueOf(str2));
            String str3 = getAttribute(node1, "languageTag");
            this.iTXt_languageTag.add(str3);
            String str4 = getAttribute(node1, "translatedKeyword");
            this.iTXt_translatedKeyword.add(str4);
            String str5 = getAttribute(node1, "text");
            this.iTXt_text.add(str5);
          } 
        } 
      } else if (str.equals("pHYs")) {
        this.pHYs_pixelsPerUnitXAxis = getIntAttribute(node, "pixelsPerUnitXAxis");
        this.pHYs_pixelsPerUnitYAxis = getIntAttribute(node, "pixelsPerUnitYAxis");
        this.pHYs_unitSpecifier = getEnumeratedAttribute(node, "unitSpecifier", unitSpecifierNames);
        this.pHYs_present = true;
      } else if (str.equals("sBIT")) {
        this.sBIT_present = false;
        Node node1 = node.getFirstChild();
        if (node1 == null)
          fatal(node, "sBIT node has no children!"); 
        String str1 = node1.getNodeName();
        if (str1.equals("sBIT_Grayscale")) {
          this.sBIT_grayBits = getIntAttribute(node1, "gray");
          this.sBIT_colorType = 0;
        } else if (str1.equals("sBIT_GrayAlpha")) {
          this.sBIT_grayBits = getIntAttribute(node1, "gray");
          this.sBIT_alphaBits = getIntAttribute(node1, "alpha");
          this.sBIT_colorType = 4;
        } else if (str1.equals("sBIT_RGB")) {
          this.sBIT_redBits = getIntAttribute(node1, "red");
          this.sBIT_greenBits = getIntAttribute(node1, "green");
          this.sBIT_blueBits = getIntAttribute(node1, "blue");
          this.sBIT_colorType = 2;
        } else if (str1.equals("sBIT_RGBAlpha")) {
          this.sBIT_redBits = getIntAttribute(node1, "red");
          this.sBIT_greenBits = getIntAttribute(node1, "green");
          this.sBIT_blueBits = getIntAttribute(node1, "blue");
          this.sBIT_alphaBits = getIntAttribute(node1, "alpha");
          this.sBIT_colorType = 6;
        } else if (str1.equals("sBIT_Palette")) {
          this.sBIT_redBits = getIntAttribute(node1, "red");
          this.sBIT_greenBits = getIntAttribute(node1, "green");
          this.sBIT_blueBits = getIntAttribute(node1, "blue");
          this.sBIT_colorType = 3;
        } else {
          fatal(node, "Bad child of an sBIT node!");
        } 
        if (node1.getNextSibling() != null)
          fatal(node, "sBIT node has more than one child!"); 
        this.sBIT_present = true;
      } else if (str.equals("sPLT")) {
        this.sPLT_paletteName = getAttribute(node, "name");
        this.sPLT_sampleDepth = getIntAttribute(node, "sampleDepth");
        int[] arrayOfInt1 = new int[256];
        int[] arrayOfInt2 = new int[256];
        int[] arrayOfInt3 = new int[256];
        int[] arrayOfInt4 = new int[256];
        int[] arrayOfInt5 = new int[256];
        int i = -1;
        Node node1 = node.getFirstChild();
        if (node1 == null)
          fatal(node, "sPLT node has no children!"); 
        while (node1 != null) {
          if (!node1.getNodeName().equals("sPLTEntry"))
            fatal(node, "Only an sPLTEntry may be a child of an sPLT!"); 
          int k = getIntAttribute(node1, "index");
          if (k < 0 || k > 255)
            fatal(node, "Bad value for PLTEEntry attribute index!"); 
          if (k > i)
            i = k; 
          arrayOfInt1[k] = getIntAttribute(node1, "red");
          arrayOfInt2[k] = getIntAttribute(node1, "green");
          arrayOfInt3[k] = getIntAttribute(node1, "blue");
          arrayOfInt4[k] = getIntAttribute(node1, "alpha");
          arrayOfInt5[k] = getIntAttribute(node1, "frequency");
          node1 = node1.getNextSibling();
        } 
        int j = i + 1;
        this.sPLT_red = new int[j];
        this.sPLT_green = new int[j];
        this.sPLT_blue = new int[j];
        this.sPLT_alpha = new int[j];
        this.sPLT_frequency = new int[j];
        System.arraycopy(arrayOfInt1, 0, this.sPLT_red, 0, j);
        System.arraycopy(arrayOfInt2, 0, this.sPLT_green, 0, j);
        System.arraycopy(arrayOfInt3, 0, this.sPLT_blue, 0, j);
        System.arraycopy(arrayOfInt4, 0, this.sPLT_alpha, 0, j);
        System.arraycopy(arrayOfInt5, 0, this.sPLT_frequency, 0, j);
        this.sPLT_present = true;
      } else if (str.equals("sRGB")) {
        this.sRGB_renderingIntent = getEnumeratedAttribute(node, "renderingIntent", renderingIntentNames);
        this.sRGB_present = true;
      } else if (str.equals("tEXt")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          if (!node1.getNodeName().equals("tEXtEntry"))
            fatal(node, "Only an tEXtEntry may be a child of an tEXt!"); 
          String str1 = getAttribute(node1, "keyword");
          this.tEXt_keyword.add(str1);
          String str2 = getAttribute(node1, "value");
          this.tEXt_text.add(str2);
        } 
      } else if (str.equals("tIME")) {
        this.tIME_year = getIntAttribute(node, "year");
        this.tIME_month = getIntAttribute(node, "month");
        this.tIME_day = getIntAttribute(node, "day");
        this.tIME_hour = getIntAttribute(node, "hour");
        this.tIME_minute = getIntAttribute(node, "minute");
        this.tIME_second = getIntAttribute(node, "second");
        this.tIME_present = true;
      } else if (str.equals("tRNS")) {
        this.tRNS_present = false;
        Node node1 = node.getFirstChild();
        if (node1 == null)
          fatal(node, "tRNS node has no children!"); 
        String str1 = node1.getNodeName();
        if (str1.equals("tRNS_Palette")) {
          byte[] arrayOfByte = new byte[256];
          int i = -1;
          Node node2 = node1.getFirstChild();
          if (node2 == null)
            fatal(node, "tRNS_Palette node has no children!"); 
          while (node2 != null) {
            if (!node2.getNodeName().equals("tRNS_PaletteEntry"))
              fatal(node, "Only a tRNS_PaletteEntry may be a child of a tRNS_Palette!"); 
            int k = getIntAttribute(node2, "index");
            if (k < 0 || k > 255)
              fatal(node, "Bad value for tRNS_PaletteEntry attribute index!"); 
            if (k > i)
              i = k; 
            arrayOfByte[k] = (byte)getIntAttribute(node2, "alpha");
            node2 = node2.getNextSibling();
          } 
          int j = i + 1;
          this.tRNS_alpha = new byte[j];
          this.tRNS_colorType = 3;
          System.arraycopy(arrayOfByte, 0, this.tRNS_alpha, 0, j);
        } else if (str1.equals("tRNS_Grayscale")) {
          this.tRNS_gray = getIntAttribute(node1, "gray");
          this.tRNS_colorType = 0;
        } else if (str1.equals("tRNS_RGB")) {
          this.tRNS_red = getIntAttribute(node1, "red");
          this.tRNS_green = getIntAttribute(node1, "green");
          this.tRNS_blue = getIntAttribute(node1, "blue");
          this.tRNS_colorType = 2;
        } else {
          fatal(node, "Bad child of a tRNS node!");
        } 
        if (node1.getNextSibling() != null)
          fatal(node, "tRNS node has more than one child!"); 
        this.tRNS_present = true;
      } else if (str.equals("zTXt")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          if (!node1.getNodeName().equals("zTXtEntry"))
            fatal(node, "Only an zTXtEntry may be a child of an zTXt!"); 
          String str1 = getAttribute(node1, "keyword");
          this.zTXt_keyword.add(str1);
          int i = getEnumeratedAttribute(node1, "compressionMethod", zTXt_compressionMethodNames);
          this.zTXt_compressionMethod.add(new Integer(i));
          String str2 = getAttribute(node1, "text");
          this.zTXt_text.add(str2);
        } 
      } else if (str.equals("UnknownChunks")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          if (!node1.getNodeName().equals("UnknownChunk"))
            fatal(node, "Only an UnknownChunk may be a child of an UnknownChunks!"); 
          String str1 = getAttribute(node1, "type");
          Object object = ((IIOMetadataNode)node1).getUserObject();
          if (str1.length() != 4)
            fatal(node1, "Chunk type must be 4 characters!"); 
          if (object == null)
            fatal(node1, "No chunk data present in user object!"); 
          if (!(object instanceof byte[]))
            fatal(node1, "User object not a byte array!"); 
          this.unknownChunkType.add(str1);
          this.unknownChunkData.add(((byte[])object).clone());
        } 
      } else {
        fatal(node, "Unknown child of root node!");
      } 
    } 
  }
  
  private boolean isValidKeyword(String paramString) {
    int i = paramString.length();
    return (i < 1 || i >= 80) ? false : ((paramString.startsWith(" ") || paramString.endsWith(" ") || paramString.contains("  ")) ? false : isISOLatin(paramString, false));
  }
  
  private boolean isISOLatin(String paramString, boolean paramBoolean) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if ((c < ' ' || c > 'ÿ' || (c > '~' && c < '¡')) && (!paramBoolean || c != '\020'))
        return false; 
    } 
    return true;
  }
  
  private void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException {
    Node node = paramNode;
    if (!node.getNodeName().equals("javax_imageio_1.0"))
      fatal(node, "Root must be javax_imageio_1.0"); 
    for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
      String str = node.getNodeName();
      if (str.equals("Chroma")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("Gamma")) {
            float f = getFloatAttribute(node1, "value");
            this.gAMA_present = true;
            this.gAMA_gamma = (int)((f * 100000.0F) + 0.5D);
          } else if (str1.equals("Palette")) {
            byte[] arrayOfByte1 = new byte[256];
            byte[] arrayOfByte2 = new byte[256];
            byte[] arrayOfByte3 = new byte[256];
            int i = -1;
            for (Node node2 = node1.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
              int k = getIntAttribute(node2, "index");
              if (k >= 0 && k <= 255) {
                arrayOfByte1[k] = (byte)getIntAttribute(node2, "red");
                arrayOfByte2[k] = (byte)getIntAttribute(node2, "green");
                arrayOfByte3[k] = (byte)getIntAttribute(node2, "blue");
                if (k > i)
                  i = k; 
              } 
            } 
            int j = i + 1;
            this.PLTE_red = new byte[j];
            this.PLTE_green = new byte[j];
            this.PLTE_blue = new byte[j];
            System.arraycopy(arrayOfByte1, 0, this.PLTE_red, 0, j);
            System.arraycopy(arrayOfByte2, 0, this.PLTE_green, 0, j);
            System.arraycopy(arrayOfByte3, 0, this.PLTE_blue, 0, j);
            this.PLTE_present = true;
          } else if (str1.equals("BackgroundIndex")) {
            this.bKGD_present = true;
            this.bKGD_colorType = 3;
            this.bKGD_index = getIntAttribute(node1, "value");
          } else if (str1.equals("BackgroundColor")) {
            int i = getIntAttribute(node1, "red");
            int j = getIntAttribute(node1, "green");
            int k = getIntAttribute(node1, "blue");
            if (i == j && i == k) {
              this.bKGD_colorType = 0;
              this.bKGD_gray = i;
            } else {
              this.bKGD_red = i;
              this.bKGD_green = j;
              this.bKGD_blue = k;
            } 
            this.bKGD_present = true;
          } 
        } 
      } else if (str.equals("Compression")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("NumProgressiveScans")) {
            int i = getIntAttribute(node1, "value");
            this.IHDR_interlaceMethod = (i > 1) ? 1 : 0;
          } 
        } 
      } else if (str.equals("Data")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("BitsPerSample")) {
            String str2 = getAttribute(node1, "value");
            StringTokenizer stringTokenizer = new StringTokenizer(str2);
            int i = -1;
            while (stringTokenizer.hasMoreTokens()) {
              int j = Integer.parseInt(stringTokenizer.nextToken());
              if (j > i)
                i = j; 
            } 
            if (i < 1)
              i = 1; 
            if (i == 3)
              i = 4; 
            if (i > 4 || i < 8)
              i = 8; 
            if (i > 8)
              i = 16; 
            this.IHDR_bitDepth = i;
          } else if (str1.equals("SignificantBitsPerSample")) {
            String str2 = getAttribute(node1, "value");
            StringTokenizer stringTokenizer = new StringTokenizer(str2);
            int i = stringTokenizer.countTokens();
            if (i == 1) {
              this.sBIT_colorType = 0;
              this.sBIT_grayBits = Integer.parseInt(stringTokenizer.nextToken());
            } else if (i == 2) {
              this.sBIT_colorType = 4;
              this.sBIT_grayBits = Integer.parseInt(stringTokenizer.nextToken());
              this.sBIT_alphaBits = Integer.parseInt(stringTokenizer.nextToken());
            } else if (i == 3) {
              this.sBIT_colorType = 2;
              this.sBIT_redBits = Integer.parseInt(stringTokenizer.nextToken());
              this.sBIT_greenBits = Integer.parseInt(stringTokenizer.nextToken());
              this.sBIT_blueBits = Integer.parseInt(stringTokenizer.nextToken());
            } else if (i == 4) {
              this.sBIT_colorType = 6;
              this.sBIT_redBits = Integer.parseInt(stringTokenizer.nextToken());
              this.sBIT_greenBits = Integer.parseInt(stringTokenizer.nextToken());
              this.sBIT_blueBits = Integer.parseInt(stringTokenizer.nextToken());
              this.sBIT_alphaBits = Integer.parseInt(stringTokenizer.nextToken());
            } 
            if (i >= 1 && i <= 4)
              this.sBIT_present = true; 
          } 
        } 
      } else if (str.equals("Dimension")) {
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        float f1 = -1.0F;
        float f2 = -1.0F;
        float f3 = -1.0F;
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("PixelAspectRatio")) {
            f3 = getFloatAttribute(node1, "value");
            bool3 = true;
          } else if (str1.equals("HorizontalPixelSize")) {
            f1 = getFloatAttribute(node1, "value");
            bool1 = true;
          } else if (str1.equals("VerticalPixelSize")) {
            f2 = getFloatAttribute(node1, "value");
            bool2 = true;
          } 
        } 
        if (bool1 && bool2) {
          this.pHYs_present = true;
          this.pHYs_unitSpecifier = 1;
          this.pHYs_pixelsPerUnitXAxis = (int)(f1 * 1000.0F + 0.5F);
          this.pHYs_pixelsPerUnitYAxis = (int)(f2 * 1000.0F + 0.5F);
        } else if (bool3) {
          this.pHYs_present = true;
          this.pHYs_unitSpecifier = 0;
          int i;
          for (i = 1; i < 100; i++) {
            int j = (int)(f3 * i);
            if (Math.abs((j / i) - f3) < 0.001D)
              break; 
          } 
          this.pHYs_pixelsPerUnitXAxis = (int)(f3 * i);
          this.pHYs_pixelsPerUnitYAxis = i;
        } 
      } else if (str.equals("Document")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("ImageModificationTime")) {
            this.tIME_present = true;
            this.tIME_year = getIntAttribute(node1, "year");
            this.tIME_month = getIntAttribute(node1, "month");
            this.tIME_day = getIntAttribute(node1, "day");
            this.tIME_hour = getIntAttribute(node1, "hour", 0, false);
            this.tIME_minute = getIntAttribute(node1, "minute", 0, false);
            this.tIME_second = getIntAttribute(node1, "second", 0, false);
          } 
        } 
      } else if (str.equals("Text")) {
        for (Node node1 = node.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
          String str1 = node1.getNodeName();
          if (str1.equals("TextEntry")) {
            String str2 = getAttribute(node1, "keyword", "", false);
            String str3 = getAttribute(node1, "value");
            String str4 = getAttribute(node1, "language", "", false);
            String str5 = getAttribute(node1, "compression", "none", false);
            if (isValidKeyword(str2))
              if (isISOLatin(str3, true)) {
                if (str5.equals("zip")) {
                  this.zTXt_keyword.add(str2);
                  this.zTXt_text.add(str3);
                  this.zTXt_compressionMethod.add(Integer.valueOf(0));
                } else {
                  this.tEXt_keyword.add(str2);
                  this.tEXt_text.add(str3);
                } 
              } else {
                this.iTXt_keyword.add(str2);
                this.iTXt_compressionFlag.add(Boolean.valueOf(str5.equals("zip")));
                this.iTXt_compressionMethod.add(Integer.valueOf(0));
                this.iTXt_languageTag.add(str4);
                this.iTXt_translatedKeyword.add(str2);
                this.iTXt_text.add(str3);
              }  
          } 
        } 
      } 
    } 
  }
  
  public void reset() {
    this.IHDR_present = false;
    this.PLTE_present = false;
    this.bKGD_present = false;
    this.cHRM_present = false;
    this.gAMA_present = false;
    this.hIST_present = false;
    this.iCCP_present = false;
    this.iTXt_keyword = new ArrayList();
    this.iTXt_compressionFlag = new ArrayList();
    this.iTXt_compressionMethod = new ArrayList();
    this.iTXt_languageTag = new ArrayList();
    this.iTXt_translatedKeyword = new ArrayList();
    this.iTXt_text = new ArrayList();
    this.pHYs_present = false;
    this.sBIT_present = false;
    this.sPLT_present = false;
    this.sRGB_present = false;
    this.tEXt_keyword = new ArrayList();
    this.tEXt_text = new ArrayList();
    this.tIME_present = false;
    this.tRNS_present = false;
    this.zTXt_keyword = new ArrayList();
    this.zTXt_compressionMethod = new ArrayList();
    this.zTXt_text = new ArrayList();
    this.unknownChunkType = new ArrayList();
    this.unknownChunkData = new ArrayList();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\PNGMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */