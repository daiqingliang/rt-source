package com.sun.imageio.plugins.jpeg;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

public class JPEGImageWriterSpi extends ImageWriterSpi {
  private static String[] readerSpiNames = { "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi" };
  
  public JPEGImageWriterSpi() { super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageWriter", new Class[] { javax.imageio.stream.ImageOutputStream.class }, readerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null); }
  
  public String getDescription(Locale paramLocale) { return "Standard JPEG Image Writer"; }
  
  public boolean isFormatLossless() { return false; }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    int[] arrayOfInt = sampleModel.getSampleSize();
    int i = arrayOfInt[0];
    for (byte b = 1; b < arrayOfInt.length; b++) {
      if (arrayOfInt[b] > i)
        i = arrayOfInt[b]; 
    } 
    return !(i < 1 || i > 8);
  }
  
  public ImageWriter createWriterInstance(Object paramObject) throws IIOException { return new JPEGImageWriter(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageWriterSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */