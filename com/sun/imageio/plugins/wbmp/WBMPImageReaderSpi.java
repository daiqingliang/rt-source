package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReaderSpi extends ImageReaderSpi {
  private static final int MAX_WBMP_WIDTH = 1024;
  
  private static final int MAX_WBMP_HEIGHT = 768;
  
  private static String[] writerSpiNames = { "com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi" };
  
  private static String[] formatNames = { "wbmp", "WBMP" };
  
  private static String[] entensions = { "wbmp" };
  
  private static String[] mimeType = { "image/vnd.wap.wbmp" };
  
  private boolean registered = false;
  
  public WBMPImageReaderSpi() { super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, true, null, null, null, null, true, "javax_imageio_wbmp_1.0", "com.sun.imageio.plugins.wbmp.WBMPMetadataFormat", null, null); }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {
    if (this.registered)
      return; 
    this.registered = true;
  }
  
  public String getDescription(Locale paramLocale) { return "Standard WBMP Image Reader"; }
  
  public boolean canDecodeInput(Object paramObject) throws IOException {
    if (!(paramObject instanceof ImageInputStream))
      return false; 
    imageInputStream = (ImageInputStream)paramObject;
    imageInputStream.mark();
    try {
      byte b1 = imageInputStream.readByte();
      byte b2 = imageInputStream.readByte();
      if (b1 != 0 || b2 != 0)
        return false; 
      int i = ReaderUtil.readMultiByteInteger(imageInputStream);
      int j = ReaderUtil.readMultiByteInteger(imageInputStream);
      if (i <= 0 || j <= 0)
        return false; 
      long l1 = imageInputStream.length();
      if (l1 == -1L)
        return (i < 1024 && j < 768); 
      l1 -= imageInputStream.getStreamPosition();
      long l2 = (i / 8 + ((i % 8 == 0) ? 0 : 1));
      return (l1 == l2 * j);
    } finally {
      imageInputStream.reset();
    } 
  }
  
  public ImageReader createReaderInstance(Object paramObject) throws IIOException { return new WBMPImageReader(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPImageReaderSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */