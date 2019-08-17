package com.sun.imageio.plugins.bmp;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class BMPImageReaderSpi extends ImageReaderSpi {
  private static String[] writerSpiNames = { "com.sun.imageio.plugins.bmp.BMPImageWriterSpi" };
  
  private static String[] formatNames = { "bmp", "BMP" };
  
  private static String[] entensions = { "bmp" };
  
  private static String[] mimeType = { "image/bmp" };
  
  private boolean registered = false;
  
  public BMPImageReaderSpi() { super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.bmp.BMPImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, false, null, null, null, null, true, "javax_imageio_bmp_1.0", "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null); }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {
    if (this.registered)
      return; 
    this.registered = true;
  }
  
  public String getDescription(Locale paramLocale) { return "Standard BMP Image Reader"; }
  
  public boolean canDecodeInput(Object paramObject) throws IOException {
    if (!(paramObject instanceof ImageInputStream))
      return false; 
    ImageInputStream imageInputStream = (ImageInputStream)paramObject;
    byte[] arrayOfByte = new byte[2];
    imageInputStream.mark();
    imageInputStream.readFully(arrayOfByte);
    imageInputStream.reset();
    return (arrayOfByte[0] == 66 && arrayOfByte[1] == 77);
  }
  
  public ImageReader createReaderInstance(Object paramObject) throws IIOException { return new BMPImageReader(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPImageReaderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */