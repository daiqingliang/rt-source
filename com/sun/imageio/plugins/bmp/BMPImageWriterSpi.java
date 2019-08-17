package com.sun.imageio.plugins.bmp;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

public class BMPImageWriterSpi extends ImageWriterSpi {
  private static String[] readerSpiNames = { "com.sun.imageio.plugins.bmp.BMPImageReaderSpi" };
  
  private static String[] formatNames = { "bmp", "BMP" };
  
  private static String[] entensions = { "bmp" };
  
  private static String[] mimeType = { "image/bmp" };
  
  private boolean registered = false;
  
  public BMPImageWriterSpi() { super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.bmp.BMPImageWriter", new Class[] { javax.imageio.stream.ImageOutputStream.class }, readerSpiNames, false, null, null, null, null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null); }
  
  public String getDescription(Locale paramLocale) { return "Standard BMP Image Writer"; }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {
    if (this.registered)
      return; 
    this.registered = true;
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    int i = paramImageTypeSpecifier.getSampleModel().getDataType();
    if (i < 0 || i > 3)
      return false; 
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    int j = sampleModel.getNumBands();
    return (j != 1 && j != 3) ? false : ((j == 1 && i != 0) ? false : (!(i > 0 && !(sampleModel instanceof java.awt.image.SinglePixelPackedSampleModel))));
  }
  
  public ImageWriter createWriterInstance(Object paramObject) throws IIOException { return new BMPImageWriter(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */