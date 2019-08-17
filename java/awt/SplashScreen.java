package java.awt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.image.SunWritableRaster;
import sun.util.logging.PlatformLogger;

public final class SplashScreen {
  private BufferedImage image;
  
  private final long splashPtr;
  
  private static boolean wasClosed = false;
  
  private URL imageURL;
  
  private static SplashScreen theInstance = null;
  
  private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.SplashScreen");
  
  SplashScreen(long paramLong) { this.splashPtr = paramLong; }
  
  public static SplashScreen getSplashScreen() {
    synchronized (SplashScreen.class) {
      if (GraphicsEnvironment.isHeadless())
        throw new HeadlessException(); 
      if (!wasClosed && theInstance == null) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                System.loadLibrary("splashscreen");
                return null;
              }
            });
        long l = _getInstance();
        if (l != 0L && _isVisible(l))
          theInstance = new SplashScreen(l); 
      } 
      return theInstance;
    } 
  }
  
  public void setImageURL(URL paramURL) throws NullPointerException, IOException, IllegalStateException {
    checkVisible();
    URLConnection uRLConnection = paramURL.openConnection();
    uRLConnection.connect();
    int i = uRLConnection.getContentLength();
    InputStream inputStream = uRLConnection.getInputStream();
    byte[] arrayOfByte = new byte[i];
    int j;
    for (j = 0;; j += m) {
      int k = inputStream.available();
      if (k <= 0)
        k = 1; 
      if (j + k > i) {
        i = j * 2;
        if (j + k > i)
          i = k + j; 
        byte[] arrayOfByte1 = arrayOfByte;
        arrayOfByte = new byte[i];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte, 0, j);
      } 
      int m = inputStream.read(arrayOfByte, j, k);
      if (m < 0)
        break; 
    } 
    synchronized (SplashScreen.class) {
      checkVisible();
      if (!_setImageData(this.splashPtr, arrayOfByte))
        throw new IOException("Bad image format or i/o error when loading image"); 
      this.imageURL = paramURL;
    } 
  }
  
  private void checkVisible() {
    if (!isVisible())
      throw new IllegalStateException("no splash screen available"); 
  }
  
  public URL getImageURL() throws IllegalStateException {
    synchronized (SplashScreen.class) {
      checkVisible();
      if (this.imageURL == null)
        try {
          String str1 = _getImageFileName(this.splashPtr);
          String str2 = _getImageJarName(this.splashPtr);
          if (str1 != null)
            if (str2 != null) {
              this.imageURL = new URL("jar:" + (new File(str2)).toURL().toString() + "!/" + str1);
            } else {
              this.imageURL = (new File(str1)).toURL();
            }  
        } catch (MalformedURLException malformedURLException) {
          if (log.isLoggable(PlatformLogger.Level.FINE))
            log.fine("MalformedURLException caught in the getImageURL() method", malformedURLException); 
        }  
      return this.imageURL;
    } 
  }
  
  public Rectangle getBounds() throws IllegalStateException {
    synchronized (SplashScreen.class) {
      checkVisible();
      float f = _getScaleFactor(this.splashPtr);
      Rectangle rectangle = _getBounds(this.splashPtr);
      assert f > 0.0F;
      if (f > 0.0F && f != 1.0F)
        rectangle.setSize((int)(rectangle.getWidth() / f), (int)(rectangle.getHeight() / f)); 
      return rectangle;
    } 
  }
  
  public Dimension getSize() throws IllegalStateException { return getBounds().getSize(); }
  
  public Graphics2D createGraphics() throws IllegalStateException {
    synchronized (SplashScreen.class) {
      checkVisible();
      if (this.image == null) {
        Dimension dimension = _getBounds(this.splashPtr).getSize();
        this.image = new BufferedImage(dimension.width, dimension.height, 2);
      } 
      float f = _getScaleFactor(this.splashPtr);
      Graphics2D graphics2D = this.image.createGraphics();
      assert f > 0.0F;
      if (f <= 0.0F)
        f = 1.0F; 
      graphics2D.scale(f, f);
      return graphics2D;
    } 
  }
  
  public void update() {
    BufferedImage bufferedImage;
    synchronized (SplashScreen.class) {
      checkVisible();
      bufferedImage = this.image;
    } 
    if (bufferedImage == null)
      throw new IllegalStateException("no overlay image available"); 
    DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();
    if (!(dataBuffer instanceof DataBufferInt))
      throw new AssertionError("Overlay image DataBuffer is of invalid type == " + dataBuffer.getClass().getName()); 
    int i = dataBuffer.getNumBanks();
    if (i != 1)
      throw new AssertionError("Invalid number of banks ==" + i + " in overlay image DataBuffer"); 
    if (!(bufferedImage.getSampleModel() instanceof SinglePixelPackedSampleModel))
      throw new AssertionError("Overlay image has invalid sample model == " + bufferedImage.getSampleModel().getClass().getName()); 
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)bufferedImage.getSampleModel();
    int j = singlePixelPackedSampleModel.getScanlineStride();
    Rectangle rectangle = bufferedImage.getRaster().getBounds();
    int[] arrayOfInt = SunWritableRaster.stealData((DataBufferInt)dataBuffer, 0);
    synchronized (SplashScreen.class) {
      checkVisible();
      _update(this.splashPtr, arrayOfInt, rectangle.x, rectangle.y, rectangle.width, rectangle.height, j);
    } 
  }
  
  public void close() {
    synchronized (SplashScreen.class) {
      checkVisible();
      _close(this.splashPtr);
      this.image = null;
      markClosed();
    } 
  }
  
  static void markClosed() {
    synchronized (SplashScreen.class) {
      wasClosed = true;
      theInstance = null;
    } 
  }
  
  public boolean isVisible() {
    synchronized (SplashScreen.class) {
      return (!wasClosed && _isVisible(this.splashPtr));
    } 
  }
  
  private static native void _update(long paramLong, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private static native boolean _isVisible(long paramLong);
  
  private static native Rectangle _getBounds(long paramLong);
  
  private static native long _getInstance();
  
  private static native void _close(long paramLong);
  
  private static native String _getImageFileName(long paramLong);
  
  private static native String _getImageJarName(long paramLong);
  
  private static native boolean _setImageData(long paramLong, byte[] paramArrayOfByte);
  
  private static native float _getScaleFactor(long paramLong);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\SplashScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */