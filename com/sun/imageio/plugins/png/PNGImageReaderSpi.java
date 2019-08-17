package com.sun.imageio.plugins.png;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class PNGImageReaderSpi extends ImageReaderSpi {
  private static final String vendorName = "Oracle Corporation";
  
  private static final String version = "1.0";
  
  private static final String[] names = { "png", "PNG" };
  
  private static final String[] suffixes = { "png" };
  
  private static final String[] MIMETypes = { "image/png", "image/x-png" };
  
  private static final String readerClassName = "com.sun.imageio.plugins.png.PNGImageReader";
  
  private static final String[] writerSpiNames = { "com.sun.imageio.plugins.png.PNGImageWriterSpi" };
  
  public PNGImageReaderSpi() { super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.png.PNGImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, false, null, null, null, null, true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", null, null); }
  
  public String getDescription(Locale paramLocale) { return "Standard PNG image reader"; }
  
  public boolean canDecodeInput(Object paramObject) throws IOException {
    if (!(paramObject instanceof ImageInputStream))
      return false; 
    ImageInputStream imageInputStream = (ImageInputStream)paramObject;
    byte[] arrayOfByte = new byte[8];
    imageInputStream.mark();
    imageInputStream.readFully(arrayOfByte);
    imageInputStream.reset();
    return (arrayOfByte[0] == -119 && arrayOfByte[1] == 80 && arrayOfByte[2] == 78 && arrayOfByte[3] == 71 && arrayOfByte[4] == 13 && arrayOfByte[5] == 10 && arrayOfByte[6] == 26 && arrayOfByte[7] == 10);
  }
  
  public ImageReader createReaderInstance(Object paramObject) { return new PNGImageReader(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\PNGImageReaderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */