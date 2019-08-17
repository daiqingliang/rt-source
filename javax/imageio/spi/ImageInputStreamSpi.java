package javax.imageio.spi;

import java.io.File;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageInputStreamSpi extends IIOServiceProvider {
  protected Class<?> inputClass;
  
  protected ImageInputStreamSpi() {}
  
  public ImageInputStreamSpi(String paramString1, String paramString2, Class<?> paramClass) {
    super(paramString1, paramString2);
    this.inputClass = paramClass;
  }
  
  public Class<?> getInputClass() { return this.inputClass; }
  
  public boolean canUseCacheFile() { return false; }
  
  public boolean needsCacheFile() { return false; }
  
  public abstract ImageInputStream createInputStreamInstance(Object paramObject, boolean paramBoolean, File paramFile) throws IOException;
  
  public ImageInputStream createInputStreamInstance(Object paramObject) throws IOException { return createInputStreamInstance(paramObject, true, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\ImageInputStreamSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */