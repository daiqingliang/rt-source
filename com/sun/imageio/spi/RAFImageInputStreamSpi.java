package com.sun.imageio.spi;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class RAFImageInputStreamSpi extends ImageInputStreamSpi {
  private static final String vendorName = "Oracle Corporation";
  
  private static final String version = "1.0";
  
  private static final Class inputClass = RandomAccessFile.class;
  
  public RAFImageInputStreamSpi() { super("Oracle Corporation", "1.0", inputClass); }
  
  public String getDescription(Locale paramLocale) { return "Service provider that instantiates a FileImageInputStream from a RandomAccessFile"; }
  
  public ImageInputStream createInputStreamInstance(Object paramObject, boolean paramBoolean, File paramFile) {
    if (paramObject instanceof RandomAccessFile)
      try {
        return new FileImageInputStream((RandomAccessFile)paramObject);
      } catch (Exception exception) {
        return null;
      }  
    throw new IllegalArgumentException("input not a RandomAccessFile!");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\spi\RAFImageInputStreamSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */