package com.sun.imageio.plugins.jpeg;

public class JPEGImageMetadataFormatResources extends JPEGMetadataFormatResources {
  static final Object[][] imageContents = { 
      { "JPEGvariety", "A node grouping all marker segments specific to the variety of stream being read/written (e.g. JFIF) - may be empty" }, { "markerSequence", "A node grouping all non-jfif marker segments" }, { "app0jfif", "A JFIF APP0 marker segment" }, { "app14Adobe", "An Adobe APP14 marker segment" }, { "sof", "A Start Of Frame marker segment" }, { "sos", "A Start Of Scan marker segment" }, { "app0JFXX", "A JFIF extension marker segment" }, { "app2ICC", "An ICC profile APP2 marker segment" }, { "JFIFthumbJPEG", "A JFIF thumbnail in JPEG format (no JFIF segments permitted)" }, { "JFIFthumbPalette", "A JFIF thumbnail as an RGB indexed image" }, 
      { "JFIFthumbRGB", "A JFIF thumbnail as an RGB image" }, { "componentSpec", "A component specification for a frame" }, { "scanComponentSpec", "A component specification for a scan" }, { "app0JFIF/majorVersion", "The major JFIF version number" }, { "app0JFIF/minorVersion", "The minor JFIF version number" }, { "app0JFIF/resUnits", "The resolution units for Xdensity and Ydensity (0 = no units, just aspect ratio; 1 = dots/inch; 2 = dots/cm)" }, { "app0JFIF/Xdensity", "The horizontal density or aspect ratio numerator" }, { "app0JFIF/Ydensity", "The vertical density or aspect ratio denominator" }, { "app0JFIF/thumbWidth", "The width of the thumbnail, or 0 if there isn't one" }, { "app0JFIF/thumbHeight", "The height of the thumbnail, or 0 if there isn't one" }, 
      { "app0JFXX/extensionCode", "The JFXX extension code identifying thumbnail type: (16 = JPEG, 17 = indexed, 19 = RGB" }, { "JFIFthumbPalette/thumbWidth", "The width of the thumbnail" }, { "JFIFthumbPalette/thumbHeight", "The height of the thumbnail" }, { "JFIFthumbRGB/thumbWidth", "The width of the thumbnail" }, { "JFIFthumbRGB/thumbHeight", "The height of the thumbnail" }, { "app14Adobe/version", "The version of Adobe APP14 marker segment" }, { "app14Adobe/flags0", "The flags0 variable of an APP14 marker segment" }, { "app14Adobe/flags1", "The flags1 variable of an APP14 marker segment" }, { "app14Adobe/transform", "The color transform applied to the image (0 = Unknown, 1 = YCbCr, 2 = YCCK)" }, { "sof/process", "The JPEG process (0 = Baseline sequential, 1 = Extended sequential, 2 = Progressive)" }, 
      { "sof/samplePrecision", "The number of bits per sample" }, { "sof/numLines", "The number of lines in the image" }, { "sof/samplesPerLine", "The number of samples per line" }, { "sof/numFrameComponents", "The number of components in the image" }, { "componentSpec/componentId", "The id for this component" }, { "componentSpec/HsamplingFactor", "The horizontal sampling factor for this component" }, { "componentSpec/VsamplingFactor", "The vertical sampling factor for this component" }, { "componentSpec/QtableSelector", "The quantization table to use for this component" }, { "sos/numScanComponents", "The number of components in the scan" }, { "sos/startSpectralSelection", "The first spectral band included in this scan" }, 
      { "sos/endSpectralSelection", "The last spectral band included in this scan" }, { "sos/approxHigh", "The highest bit position included in this scan" }, { "sos/approxLow", "The lowest bit position included in this scan" }, { "scanComponentSpec/componentSelector", "The id of this component" }, { "scanComponentSpec/dcHuffTable", "The huffman table to use for encoding DC coefficients" }, { "scanComponentSpec/acHuffTable", "The huffman table to use for encoding AC coefficients" } };
  
  protected Object[][] getContents() {
    Object[][] arrayOfObject = new Object[commonContents.length + imageContents.length][2];
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < commonContents.length) {
      arrayOfObject[b1][0] = commonContents[b2][0];
      arrayOfObject[b1][1] = commonContents[b2][1];
      b2++;
      b1++;
    } 
    b2 = 0;
    while (b2 < imageContents.length) {
      arrayOfObject[b1][0] = imageContents[b2][0];
      arrayOfObject[b1][1] = imageContents[b2][1];
      b2++;
      b1++;
    } 
    return arrayOfObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageMetadataFormatResources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */