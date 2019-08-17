package javax.imageio.spi;

import com.sun.imageio.plugins.bmp.BMPImageReaderSpi;
import com.sun.imageio.plugins.bmp.BMPImageWriterSpi;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi;
import com.sun.imageio.spi.FileImageInputStreamSpi;
import com.sun.imageio.spi.FileImageOutputStreamSpi;
import com.sun.imageio.spi.InputStreamImageInputStreamSpi;
import com.sun.imageio.spi.OutputStreamImageOutputStreamSpi;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import com.sun.imageio.spi.RAFImageOutputStreamSpi;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Vector;
import sun.awt.AppContext;

public final class IIORegistry extends ServiceRegistry {
  private static final Vector initialCategories = new Vector(5);
  
  private IIORegistry() {
    super(initialCategories.iterator());
    registerStandardSpis();
    registerApplicationClasspathSpis();
  }
  
  public static IIORegistry getDefaultInstance() {
    AppContext appContext = AppContext.getAppContext();
    IIORegistry iIORegistry = (IIORegistry)appContext.get(IIORegistry.class);
    if (iIORegistry == null) {
      iIORegistry = new IIORegistry();
      appContext.put(IIORegistry.class, iIORegistry);
    } 
    return iIORegistry;
  }
  
  private void registerStandardSpis() {
    registerServiceProvider(new GIFImageReaderSpi());
    registerServiceProvider(new GIFImageWriterSpi());
    registerServiceProvider(new BMPImageReaderSpi());
    registerServiceProvider(new BMPImageWriterSpi());
    registerServiceProvider(new WBMPImageReaderSpi());
    registerServiceProvider(new WBMPImageWriterSpi());
    registerServiceProvider(new PNGImageReaderSpi());
    registerServiceProvider(new PNGImageWriterSpi());
    registerServiceProvider(new JPEGImageReaderSpi());
    registerServiceProvider(new JPEGImageWriterSpi());
    registerServiceProvider(new FileImageInputStreamSpi());
    registerServiceProvider(new FileImageOutputStreamSpi());
    registerServiceProvider(new InputStreamImageInputStreamSpi());
    registerServiceProvider(new OutputStreamImageOutputStreamSpi());
    registerServiceProvider(new RAFImageInputStreamSpi());
    registerServiceProvider(new RAFImageOutputStreamSpi());
    registerInstalledProviders();
  }
  
  public void registerApplicationClasspathSpis() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    Iterator iterator = getCategories();
    while (iterator.hasNext()) {
      Class clazz = (Class)iterator.next();
      Iterator iterator1 = ServiceLoader.load(clazz, classLoader).iterator();
      while (iterator1.hasNext()) {
        try {
          IIOServiceProvider iIOServiceProvider = (IIOServiceProvider)iterator1.next();
          registerServiceProvider(iIOServiceProvider);
        } catch (ServiceConfigurationError serviceConfigurationError) {
          if (System.getSecurityManager() != null) {
            serviceConfigurationError.printStackTrace();
            continue;
          } 
          throw serviceConfigurationError;
        } 
      } 
    } 
  }
  
  private void registerInstalledProviders() {
    PrivilegedAction privilegedAction = new PrivilegedAction() {
        public Object run() {
          Iterator iterator = IIORegistry.this.getCategories();
          while (iterator.hasNext()) {
            Class clazz = (Class)iterator.next();
            for (IIOServiceProvider iIOServiceProvider : ServiceLoader.loadInstalled(clazz))
              IIORegistry.this.registerServiceProvider(iIOServiceProvider); 
          } 
          return this;
        }
      };
    AccessController.doPrivileged(privilegedAction);
  }
  
  static  {
    initialCategories.add(ImageReaderSpi.class);
    initialCategories.add(ImageWriterSpi.class);
    initialCategories.add(ImageTranscoderSpi.class);
    initialCategories.add(ImageInputStreamSpi.class);
    initialCategories.add(ImageOutputStreamSpi.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\IIORegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */