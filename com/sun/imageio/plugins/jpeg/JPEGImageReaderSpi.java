package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class JPEGImageReaderSpi extends ImageReaderSpi {
  private static String[] writerSpiNames = { "com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi" };
  
  public JPEGImageReaderSpi() { super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null); }
  
  public String getDescription(Locale paramLocale) { return "Standard JPEG Image Reader"; }
  
  public boolean canDecodeInput(Object paramObject) throws IOException {
    if (!(paramObject instanceof ImageInputStream))
      return false; 
    ImageInputStream imageInputStream = (ImageInputStream)paramObject;
    imageInputStream.mark();
    int i = imageInputStream.read();
    int j = imageInputStream.read();
    imageInputStream.reset();
    return (i == 255 && j == 216);
  }
  
  public ImageReader createReaderInstance(Object paramObject) throws IIOException { return new JPEGImageReader(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageReaderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */