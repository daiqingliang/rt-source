package com.sun.imageio.plugins.gif;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class GIFImageReaderSpi extends ImageReaderSpi {
  private static final String vendorName = "Oracle Corporation";
  
  private static final String version = "1.0";
  
  private static final String[] names = { "gif", "GIF" };
  
  private static final String[] suffixes = { "gif" };
  
  private static final String[] MIMETypes = { "image/gif" };
  
  private static final String readerClassName = "com.sun.imageio.plugins.gif.GIFImageReader";
  
  private static final String[] writerSpiNames = { "com.sun.imageio.plugins.gif.GIFImageWriterSpi" };
  
  public GIFImageReaderSpi() { super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.gif.GIFImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null); }
  
  public String getDescription(Locale paramLocale) { return "Standard GIF image reader"; }
  
  public boolean canDecodeInput(Object paramObject) throws IOException {
    if (!(paramObject instanceof ImageInputStream))
      return false; 
    ImageInputStream imageInputStream = (ImageInputStream)paramObject;
    byte[] arrayOfByte = new byte[6];
    imageInputStream.mark();
    imageInputStream.readFully(arrayOfByte);
    imageInputStream.reset();
    return (arrayOfByte[0] == 71 && arrayOfByte[1] == 73 && arrayOfByte[2] == 70 && arrayOfByte[3] == 56 && (arrayOfByte[4] == 55 || arrayOfByte[4] == 57) && arrayOfByte[5] == 97);
  }
  
  public ImageReader createReaderInstance(Object paramObject) { return new GIFImageReader(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFImageReaderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */