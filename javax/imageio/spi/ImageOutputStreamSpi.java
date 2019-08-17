package javax.imageio.spi;

import java.io.File;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public abstract class ImageOutputStreamSpi extends IIOServiceProvider {
  protected Class<?> outputClass;
  
  protected ImageOutputStreamSpi() {}
  
  public ImageOutputStreamSpi(String paramString1, String paramString2, Class<?> paramClass) {
    super(paramString1, paramString2);
    this.outputClass = paramClass;
  }
  
  public Class<?> getOutputClass() { return this.outputClass; }
  
  public boolean canUseCacheFile() { return false; }
  
  public boolean needsCacheFile() { return false; }
  
  public abstract ImageOutputStream createOutputStreamInstance(Object paramObject, boolean paramBoolean, File paramFile) throws IOException;
  
  public ImageOutputStream createOutputStreamInstance(Object paramObject) throws IOException { return createOutputStreamInstance(paramObject, true, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\ImageOutputStreamSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */