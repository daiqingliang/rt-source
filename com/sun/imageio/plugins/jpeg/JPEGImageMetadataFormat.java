package com.sun.imageio.plugins.jpeg;

import java.util.ArrayList;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

public class JPEGImageMetadataFormat extends JPEGMetadataFormat {
  private static JPEGImageMetadataFormat theInstance = null;
  
  private JPEGImageMetadataFormat() {
    super("javax_imageio_jpeg_image_1.0", 1);
    addElement("JPEGvariety", "javax_imageio_jpeg_image_1.0", 3);
    addElement("markerSequence", "javax_imageio_jpeg_image_1.0", 4);
    addElement("app0JFIF", "JPEGvariety", 2);
    addStreamElements("markerSequence");
    addElement("app14Adobe", "markerSequence", 0);
    addElement("sof", "markerSequence", 1, 4);
    addElement("sos", "markerSequence", 1, 4);
    addElement("JFXX", "app0JFIF", 1, 2147483647);
    addElement("app0JFXX", "JFXX", 3);
    addElement("app2ICC", "app0JFIF", 0);
    addAttribute("app0JFIF", "majorVersion", 2, false, "1", "0", "255", true, true);
    addAttribute("app0JFIF", "minorVersion", 2, false, "2", "0", "255", true, true);
    ArrayList arrayList1 = new ArrayList();
    arrayList1.add("0");
    arrayList1.add("1");
    arrayList1.add("2");
    addAttribute("app0JFIF", "resUnits", 2, false, "0", arrayList1);
    addAttribute("app0JFIF", "Xdensity", 2, false, "1", "1", "65535", true, true);
    addAttribute("app0JFIF", "Ydensity", 2, false, "1", "1", "65535", true, true);
    addAttribute("app0JFIF", "thumbWidth", 2, false, "0", "0", "255", true, true);
    addAttribute("app0JFIF", "thumbHeight", 2, false, "0", "0", "255", true, true);
    addElement("JFIFthumbJPEG", "app0JFXX", 2);
    addElement("JFIFthumbPalette", "app0JFXX", 0);
    addElement("JFIFthumbRGB", "app0JFXX", 0);
    ArrayList arrayList2 = new ArrayList();
    arrayList2.add("16");
    arrayList2.add("17");
    arrayList2.add("19");
    addAttribute("app0JFXX", "extensionCode", 2, false, null, arrayList2);
    addChildElement("markerSequence", "JFIFthumbJPEG");
    addAttribute("JFIFthumbPalette", "thumbWidth", 2, false, null, "0", "255", true, true);
    addAttribute("JFIFthumbPalette", "thumbHeight", 2, false, null, "0", "255", true, true);
    addAttribute("JFIFthumbRGB", "thumbWidth", 2, false, null, "0", "255", true, true);
    addAttribute("JFIFthumbRGB", "thumbHeight", 2, false, null, "0", "255", true, true);
    addObjectValue("app2ICC", java.awt.color.ICC_Profile.class, false, null);
    addAttribute("app14Adobe", "version", 2, false, "100", "100", "255", true, true);
    addAttribute("app14Adobe", "flags0", 2, false, "0", "0", "65535", true, true);
    addAttribute("app14Adobe", "flags1", 2, false, "0", "0", "65535", true, true);
    ArrayList arrayList3 = new ArrayList();
    arrayList3.add("0");
    arrayList3.add("1");
    arrayList3.add("2");
    addAttribute("app14Adobe", "transform", 2, true, null, arrayList3);
    addElement("componentSpec", "sof", 0);
    ArrayList arrayList4 = new ArrayList();
    arrayList4.add("0");
    arrayList4.add("1");
    arrayList4.add("2");
    addAttribute("sof", "process", 2, false, null, arrayList4);
    addAttribute("sof", "samplePrecision", 2, false, "8");
    addAttribute("sof", "numLines", 2, false, null, "0", "65535", true, true);
    addAttribute("sof", "samplesPerLine", 2, false, null, "0", "65535", true, true);
    ArrayList arrayList5 = new ArrayList();
    arrayList5.add("1");
    arrayList5.add("2");
    arrayList5.add("3");
    arrayList5.add("4");
    addAttribute("sof", "numFrameComponents", 2, false, null, arrayList5);
    addAttribute("componentSpec", "componentId", 2, true, null, "0", "255", true, true);
    addAttribute("componentSpec", "HsamplingFactor", 2, true, null, "1", "255", true, true);
    addAttribute("componentSpec", "VsamplingFactor", 2, true, null, "1", "255", true, true);
    ArrayList arrayList6 = new ArrayList();
    arrayList6.add("0");
    arrayList6.add("1");
    arrayList6.add("2");
    arrayList6.add("3");
    addAttribute("componentSpec", "QtableSelector", 2, true, null, arrayList6);
    addElement("scanComponentSpec", "sos", 0);
    addAttribute("sos", "numScanComponents", 2, true, null, arrayList5);
    addAttribute("sos", "startSpectralSelection", 2, false, "0", "0", "63", true, true);
    addAttribute("sos", "endSpectralSelection", 2, false, "63", "0", "63", true, true);
    addAttribute("sos", "approxHigh", 2, false, "0", "0", "15", true, true);
    addAttribute("sos", "approxLow", 2, false, "0", "0", "15", true, true);
    addAttribute("scanComponentSpec", "componentSelector", 2, true, null, "0", "255", true, true);
    addAttribute("scanComponentSpec", "dcHuffTable", 2, true, null, arrayList6);
    addAttribute("scanComponentSpec", "acHuffTable", 2, true, null, arrayList6);
  }
  
  public boolean canNodeAppear(String paramString, ImageTypeSpecifier paramImageTypeSpecifier) { return (paramString.equals(getRootName()) || paramString.equals("JPEGvariety") || isInSubtree(paramString, "markerSequence")) ? true : ((isInSubtree(paramString, "app0JFIF") && JPEG.isJFIFcompliant(paramImageTypeSpecifier, true))); }
  
  public static IIOMetadataFormat getInstance() {
    if (theInstance == null)
      theInstance = new JPEGImageMetadataFormat(); 
    return theInstance;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageMetadataFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */