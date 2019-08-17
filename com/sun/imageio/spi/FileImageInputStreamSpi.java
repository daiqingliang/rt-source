package com.sun.imageio.spi;

import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

public class FileImageInputStreamSpi extends ImageInputStreamSpi {
  private static final String vendorName = "Oracle Corporation";
  
  private static final String version = "1.0";
  
  private static final Class inputClass = File.class;
  
  public FileImageInputStreamSpi() { super("Oracle Corporation", "1.0", inputClass); }
  
  public String getDescription(Locale paramLocale) { return "Service provider that instantiates a FileImageInputStream from a File"; }
  
  public ImageInputStream createInputStreamInstance(Object paramObject, boolean paramBoolean, File paramFile) {
    if (paramObject instanceof File)
      try {
        return new FileImageInputStream((File)paramObject);
      } catch (Exception exception) {
        return null;
      }  
    throw new IllegalArgumentException();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\spi\FileImageInputStreamSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */