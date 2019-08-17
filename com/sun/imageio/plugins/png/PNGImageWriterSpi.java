package com.sun.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

public class PNGImageWriterSpi extends ImageWriterSpi {
  private static final String vendorName = "Oracle Corporation";
  
  private static final String version = "1.0";
  
  private static final String[] names = { "png", "PNG" };
  
  private static final String[] suffixes = { "png" };
  
  private static final String[] MIMETypes = { "image/png", "image/x-png" };
  
  private static final String writerClassName = "com.sun.imageio.plugins.png.PNGImageWriter";
  
  private static final String[] readerSpiNames = { "com.sun.imageio.plugins.png.PNGImageReaderSpi" };
  
  public PNGImageWriterSpi() { super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.png.PNGImageWriter", new Class[] { javax.imageio.stream.ImageOutputStream.class }, readerSpiNames, false, null, null, null, null, true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null); }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    int[] arrayOfInt = sampleModel.getSampleSize();
    int i = arrayOfInt[0];
    int j;
    for (j = 1; j < arrayOfInt.length; j++) {
      if (arrayOfInt[j] > i)
        i = arrayOfInt[j]; 
    } 
    if (i < 1 || i > 16)
      return false; 
    j = sampleModel.getNumBands();
    if (j < 1 || j > 4)
      return false; 
    boolean bool = colorModel.hasAlpha();
    return (colorModel instanceof java.awt.image.IndexColorModel) ? true : (((j == 1 || j == 3) && bool) ? false : (!((j == 2 || j == 4) && !bool)));
  }
  
  public String getDescription(Locale paramLocale) { return "Standard PNG image writer"; }
  
  public ImageWriter createWriterInstance(Object paramObject) { return new PNGImageWriter(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\PNGImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */