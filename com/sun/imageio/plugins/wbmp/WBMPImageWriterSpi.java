package com.sun.imageio.plugins.wbmp;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;

public class WBMPImageWriterSpi extends ImageWriterSpi {
  private static String[] readerSpiNames = { "com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi" };
  
  private static String[] formatNames = { "wbmp", "WBMP" };
  
  private static String[] entensions = { "wbmp" };
  
  private static String[] mimeType = { "image/vnd.wap.wbmp" };
  
  private boolean registered = false;
  
  public WBMPImageWriterSpi() { super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageWriter", new Class[] { javax.imageio.stream.ImageOutputStream.class }, readerSpiNames, true, null, null, null, null, true, null, null, null, null); }
  
  public String getDescription(Locale paramLocale) { return "Standard WBMP Image Writer"; }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {
    if (this.registered)
      return; 
    this.registered = true;
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    return !(sampleModel instanceof java.awt.image.MultiPixelPackedSampleModel) ? false : (!(sampleModel.getSampleSize(0) != 1));
  }
  
  public ImageWriter createWriterInstance(Object paramObject) throws IIOException { return new WBMPImageWriter(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */