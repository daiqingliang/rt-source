package javax.imageio;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

public abstract class ImageReader {
  protected ImageReaderSpi originatingProvider;
  
  protected Object input = null;
  
  protected boolean seekForwardOnly = false;
  
  protected boolean ignoreMetadata = false;
  
  protected int minIndex = 0;
  
  protected Locale[] availableLocales = null;
  
  protected Locale locale = null;
  
  protected List<IIOReadWarningListener> warningListeners = null;
  
  protected List<Locale> warningLocales = null;
  
  protected List<IIOReadProgressListener> progressListeners = null;
  
  protected List<IIOReadUpdateListener> updateListeners = null;
  
  private boolean abortFlag = false;
  
  protected ImageReader(ImageReaderSpi paramImageReaderSpi) { this.originatingProvider = paramImageReaderSpi; }
  
  public String getFormatName() throws IOException { return this.originatingProvider.getFormatNames()[0]; }
  
  public ImageReaderSpi getOriginatingProvider() { return this.originatingProvider; }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramObject != null) {
      boolean bool = false;
      if (this.originatingProvider != null) {
        Class[] arrayOfClass = this.originatingProvider.getInputTypes();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (arrayOfClass[b].isInstance(paramObject)) {
            bool = true;
            break;
          } 
        } 
      } else if (paramObject instanceof javax.imageio.stream.ImageInputStream) {
        bool = true;
      } 
      if (!bool)
        throw new IllegalArgumentException("Incorrect input type!"); 
      this.seekForwardOnly = paramBoolean1;
      this.ignoreMetadata = paramBoolean2;
      this.minIndex = 0;
    } 
    this.input = paramObject;
  }
  
  public void setInput(Object paramObject, boolean paramBoolean) { setInput(paramObject, paramBoolean, false); }
  
  public void setInput(Object paramObject) { setInput(paramObject, false, false); }
  
  public Object getInput() { return this.input; }
  
  public boolean isSeekForwardOnly() { return this.seekForwardOnly; }
  
  public boolean isIgnoringMetadata() { return this.ignoreMetadata; }
  
  public int getMinIndex() { return this.minIndex; }
  
  public Locale[] getAvailableLocales() { return (this.availableLocales == null) ? null : (Locale[])this.availableLocales.clone(); }
  
  public void setLocale(Locale paramLocale) {
    if (paramLocale != null) {
      Locale[] arrayOfLocale = getAvailableLocales();
      boolean bool = false;
      if (arrayOfLocale != null)
        for (byte b = 0; b < arrayOfLocale.length; b++) {
          if (paramLocale.equals(arrayOfLocale[b])) {
            bool = true;
            break;
          } 
        }  
      if (!bool)
        throw new IllegalArgumentException("Invalid locale!"); 
    } 
    this.locale = paramLocale;
  }
  
  public Locale getLocale() { return this.locale; }
  
  public abstract int getNumImages(boolean paramBoolean) throws IOException;
  
  public abstract int getWidth(int paramInt) throws IOException;
  
  public abstract int getHeight(int paramInt) throws IOException;
  
  public boolean isRandomAccessEasy(int paramInt) throws IOException { return false; }
  
  public float getAspectRatio(int paramInt) throws IOException { return getWidth(paramInt) / getHeight(paramInt); }
  
  public ImageTypeSpecifier getRawImageType(int paramInt) throws IOException { return (ImageTypeSpecifier)getImageTypes(paramInt).next(); }
  
  public abstract Iterator<ImageTypeSpecifier> getImageTypes(int paramInt) throws IOException;
  
  public ImageReadParam getDefaultReadParam() { return new ImageReadParam(); }
  
  public abstract IIOMetadata getStreamMetadata() throws IOException;
  
  public IIOMetadata getStreamMetadata(String paramString, Set<String> paramSet) throws IOException { return getMetadata(paramString, paramSet, true, 0); }
  
  private IIOMetadata getMetadata(String paramString, Set paramSet, boolean paramBoolean, int paramInt) throws IOException {
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    if (paramSet == null)
      throw new IllegalArgumentException("nodeNames == null!"); 
    IIOMetadata iIOMetadata = paramBoolean ? getStreamMetadata() : getImageMetadata(paramInt);
    if (iIOMetadata != null) {
      if (iIOMetadata.isStandardMetadataFormatSupported() && paramString.equals("javax_imageio_1.0"))
        return iIOMetadata; 
      String str = iIOMetadata.getNativeMetadataFormatName();
      if (str != null && paramString.equals(str))
        return iIOMetadata; 
      String[] arrayOfString = iIOMetadata.getExtraMetadataFormatNames();
      if (arrayOfString != null)
        for (byte b = 0; b < arrayOfString.length; b++) {
          if (paramString.equals(arrayOfString[b]))
            return iIOMetadata; 
        }  
    } 
    return null;
  }
  
  public abstract IIOMetadata getImageMetadata(int paramInt) throws IOException;
  
  public IIOMetadata getImageMetadata(int paramInt, String paramString, Set<String> paramSet) throws IOException { return getMetadata(paramString, paramSet, false, paramInt); }
  
  public BufferedImage read(int paramInt) throws IOException { return read(paramInt, null); }
  
  public abstract BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IOException;
  
  public IIOImage readAll(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    if (paramInt < getMinIndex())
      throw new IndexOutOfBoundsException("imageIndex < getMinIndex()!"); 
    BufferedImage bufferedImage = read(paramInt, paramImageReadParam);
    ArrayList arrayList = null;
    int i = getNumThumbnails(paramInt);
    if (i > 0) {
      arrayList = new ArrayList();
      for (byte b = 0; b < i; b++)
        arrayList.add(readThumbnail(paramInt, b)); 
    } 
    IIOMetadata iIOMetadata = getImageMetadata(paramInt);
    return new IIOImage(bufferedImage, arrayList, iIOMetadata);
  }
  
  public Iterator<IIOImage> readAll(Iterator<? extends ImageReadParam> paramIterator) throws IOException {
    ArrayList arrayList = new ArrayList();
    int i = getMinIndex();
    processSequenceStarted(i);
    while (true) {
      ImageReadParam imageReadParam = null;
      if (paramIterator != null && paramIterator.hasNext()) {
        Object object = paramIterator.next();
        if (object != null)
          if (object instanceof ImageReadParam) {
            imageReadParam = (ImageReadParam)object;
          } else {
            throw new IllegalArgumentException("Non-ImageReadParam supplied as part of params!");
          }  
      } 
      BufferedImage bufferedImage = null;
      try {
        bufferedImage = read(i, imageReadParam);
      } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
        break;
      } 
      ArrayList arrayList1 = null;
      int j = getNumThumbnails(i);
      if (j > 0) {
        arrayList1 = new ArrayList();
        for (byte b = 0; b < j; b++)
          arrayList1.add(readThumbnail(i, b)); 
      } 
      IIOMetadata iIOMetadata = getImageMetadata(i);
      IIOImage iIOImage = new IIOImage(bufferedImage, arrayList1, iIOMetadata);
      arrayList.add(iIOImage);
      i++;
    } 
    processSequenceComplete();
    return arrayList.iterator();
  }
  
  public boolean canReadRaster() { return false; }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam) throws IOException { throw new UnsupportedOperationException("readRaster not supported!"); }
  
  public boolean isImageTiled(int paramInt) throws IOException { return false; }
  
  public int getTileWidth(int paramInt) throws IOException { return getWidth(paramInt); }
  
  public int getTileHeight(int paramInt) throws IOException { return getHeight(paramInt); }
  
  public int getTileGridXOffset(int paramInt) throws IOException { return 0; }
  
  public int getTileGridYOffset(int paramInt) throws IOException { return 0; }
  
  public BufferedImage readTile(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    if (paramInt2 != 0 || paramInt3 != 0)
      throw new IllegalArgumentException("Invalid tile indices"); 
    return read(paramInt1);
  }
  
  public Raster readTileRaster(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    if (!canReadRaster())
      throw new UnsupportedOperationException("readTileRaster not supported!"); 
    if (paramInt2 != 0 || paramInt3 != 0)
      throw new IllegalArgumentException("Invalid tile indices"); 
    return readRaster(paramInt1, null);
  }
  
  public RenderedImage readAsRenderedImage(int paramInt, ImageReadParam paramImageReadParam) throws IOException { return read(paramInt, paramImageReadParam); }
  
  public boolean readerSupportsThumbnails() { return false; }
  
  public boolean hasThumbnails(int paramInt) throws IOException { return (getNumThumbnails(paramInt) > 0); }
  
  public int getNumThumbnails(int paramInt) throws IOException { return 0; }
  
  public int getThumbnailWidth(int paramInt1, int paramInt2) throws IOException { return readThumbnail(paramInt1, paramInt2).getWidth(); }
  
  public int getThumbnailHeight(int paramInt1, int paramInt2) throws IOException { return readThumbnail(paramInt1, paramInt2).getHeight(); }
  
  public BufferedImage readThumbnail(int paramInt1, int paramInt2) throws IOException { throw new UnsupportedOperationException("Thumbnails not supported!"); }
  
  public void abort() { this.abortFlag = true; }
  
  protected boolean abortRequested() { return this.abortFlag; }
  
  protected void clearAbortRequest() { this.abortFlag = false; }
  
  static List addToList(List paramList, Object paramObject) {
    if (paramList == null)
      paramList = new ArrayList(); 
    paramList.add(paramObject);
    return paramList;
  }
  
  static List removeFromList(List paramList, Object paramObject) {
    if (paramList == null)
      return paramList; 
    paramList.remove(paramObject);
    if (paramList.size() == 0)
      paramList = null; 
    return paramList;
  }
  
  public void addIIOReadWarningListener(IIOReadWarningListener paramIIOReadWarningListener) {
    if (paramIIOReadWarningListener == null)
      return; 
    this.warningListeners = addToList(this.warningListeners, paramIIOReadWarningListener);
    this.warningLocales = addToList(this.warningLocales, getLocale());
  }
  
  public void removeIIOReadWarningListener(IIOReadWarningListener paramIIOReadWarningListener) {
    if (paramIIOReadWarningListener == null || this.warningListeners == null)
      return; 
    int i = this.warningListeners.indexOf(paramIIOReadWarningListener);
    if (i != -1) {
      this.warningListeners.remove(i);
      this.warningLocales.remove(i);
      if (this.warningListeners.size() == 0) {
        this.warningListeners = null;
        this.warningLocales = null;
      } 
    } 
  }
  
  public void removeAllIIOReadWarningListeners() {
    this.warningListeners = null;
    this.warningLocales = null;
  }
  
  public void addIIOReadProgressListener(IIOReadProgressListener paramIIOReadProgressListener) {
    if (paramIIOReadProgressListener == null)
      return; 
    this.progressListeners = addToList(this.progressListeners, paramIIOReadProgressListener);
  }
  
  public void removeIIOReadProgressListener(IIOReadProgressListener paramIIOReadProgressListener) {
    if (paramIIOReadProgressListener == null || this.progressListeners == null)
      return; 
    this.progressListeners = removeFromList(this.progressListeners, paramIIOReadProgressListener);
  }
  
  public void removeAllIIOReadProgressListeners() { this.progressListeners = null; }
  
  public void addIIOReadUpdateListener(IIOReadUpdateListener paramIIOReadUpdateListener) {
    if (paramIIOReadUpdateListener == null)
      return; 
    this.updateListeners = addToList(this.updateListeners, paramIIOReadUpdateListener);
  }
  
  public void removeIIOReadUpdateListener(IIOReadUpdateListener paramIIOReadUpdateListener) {
    if (paramIIOReadUpdateListener == null || this.updateListeners == null)
      return; 
    this.updateListeners = removeFromList(this.updateListeners, paramIIOReadUpdateListener);
  }
  
  public void removeAllIIOReadUpdateListeners() { this.updateListeners = null; }
  
  protected void processSequenceStarted(int paramInt) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.sequenceStarted(this, paramInt);
    } 
  }
  
  protected void processSequenceComplete() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.sequenceComplete(this);
    } 
  }
  
  protected void processImageStarted(int paramInt) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.imageStarted(this, paramInt);
    } 
  }
  
  protected void processImageProgress(float paramFloat) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.imageProgress(this, paramFloat);
    } 
  }
  
  protected void processImageComplete() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.imageComplete(this);
    } 
  }
  
  protected void processThumbnailStarted(int paramInt1, int paramInt2) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.thumbnailStarted(this, paramInt1, paramInt2);
    } 
  }
  
  protected void processThumbnailProgress(float paramFloat) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.thumbnailProgress(this, paramFloat);
    } 
  }
  
  protected void processThumbnailComplete() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.thumbnailComplete(this);
    } 
  }
  
  protected void processReadAborted() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadProgressListener iIOReadProgressListener = (IIOReadProgressListener)this.progressListeners.get(b);
      iIOReadProgressListener.readAborted(this);
    } 
  }
  
  protected void processPassStarted(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int[] paramArrayOfInt) {
    if (this.updateListeners == null)
      return; 
    int i = this.updateListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadUpdateListener iIOReadUpdateListener = (IIOReadUpdateListener)this.updateListeners.get(b);
      iIOReadUpdateListener.passStarted(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramArrayOfInt);
    } 
  }
  
  protected void processImageUpdate(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) {
    if (this.updateListeners == null)
      return; 
    int i = this.updateListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadUpdateListener iIOReadUpdateListener = (IIOReadUpdateListener)this.updateListeners.get(b);
      iIOReadUpdateListener.imageUpdate(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt);
    } 
  }
  
  protected void processPassComplete(BufferedImage paramBufferedImage) {
    if (this.updateListeners == null)
      return; 
    int i = this.updateListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadUpdateListener iIOReadUpdateListener = (IIOReadUpdateListener)this.updateListeners.get(b);
      iIOReadUpdateListener.passComplete(this, paramBufferedImage);
    } 
  }
  
  protected void processThumbnailPassStarted(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int[] paramArrayOfInt) {
    if (this.updateListeners == null)
      return; 
    int i = this.updateListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadUpdateListener iIOReadUpdateListener = (IIOReadUpdateListener)this.updateListeners.get(b);
      iIOReadUpdateListener.thumbnailPassStarted(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramArrayOfInt);
    } 
  }
  
  protected void processThumbnailUpdate(BufferedImage paramBufferedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) {
    if (this.updateListeners == null)
      return; 
    int i = this.updateListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadUpdateListener iIOReadUpdateListener = (IIOReadUpdateListener)this.updateListeners.get(b);
      iIOReadUpdateListener.thumbnailUpdate(this, paramBufferedImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt);
    } 
  }
  
  protected void processThumbnailPassComplete(BufferedImage paramBufferedImage) {
    if (this.updateListeners == null)
      return; 
    int i = this.updateListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadUpdateListener iIOReadUpdateListener = (IIOReadUpdateListener)this.updateListeners.get(b);
      iIOReadUpdateListener.thumbnailPassComplete(this, paramBufferedImage);
    } 
  }
  
  protected void processWarningOccurred(String paramString) {
    if (this.warningListeners == null)
      return; 
    if (paramString == null)
      throw new IllegalArgumentException("warning == null!"); 
    int i = this.warningListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadWarningListener iIOReadWarningListener = (IIOReadWarningListener)this.warningListeners.get(b);
      iIOReadWarningListener.warningOccurred(this, paramString);
    } 
  }
  
  protected void processWarningOccurred(String paramString1, String paramString2) {
    if (this.warningListeners == null)
      return; 
    if (paramString1 == null)
      throw new IllegalArgumentException("baseName == null!"); 
    if (paramString2 == null)
      throw new IllegalArgumentException("keyword == null!"); 
    int i = this.warningListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOReadWarningListener iIOReadWarningListener = (IIOReadWarningListener)this.warningListeners.get(b);
      Locale locale1 = (Locale)this.warningLocales.get(b);
      if (locale1 == null)
        locale1 = Locale.getDefault(); 
      ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return Thread.currentThread().getContextClassLoader(); }
          });
      ResourceBundle resourceBundle = null;
      try {
        resourceBundle = ResourceBundle.getBundle(paramString1, locale1, classLoader);
      } catch (MissingResourceException missingResourceException) {
        try {
          resourceBundle = ResourceBundle.getBundle(paramString1, locale1);
        } catch (MissingResourceException missingResourceException1) {
          throw new IllegalArgumentException("Bundle not found!");
        } 
      } 
      String str = null;
      try {
        str = resourceBundle.getString(paramString2);
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException("Resource is not a String!");
      } catch (MissingResourceException missingResourceException) {
        throw new IllegalArgumentException("Resource is missing!");
      } 
      iIOReadWarningListener.warningOccurred(this, str);
    } 
  }
  
  public void reset() {
    setInput(null, false, false);
    setLocale(null);
    removeAllIIOReadUpdateListeners();
    removeAllIIOReadProgressListeners();
    removeAllIIOReadWarningListeners();
    clearAbortRequest();
  }
  
  public void dispose() {}
  
  protected static Rectangle getSourceRegion(ImageReadParam paramImageReadParam, int paramInt1, int paramInt2) {
    Rectangle rectangle = new Rectangle(0, 0, paramInt1, paramInt2);
    if (paramImageReadParam != null) {
      Rectangle rectangle1 = paramImageReadParam.getSourceRegion();
      if (rectangle1 != null)
        rectangle = rectangle.intersection(rectangle1); 
      int i = paramImageReadParam.getSubsamplingXOffset();
      int j = paramImageReadParam.getSubsamplingYOffset();
      rectangle.x += i;
      rectangle.y += j;
      rectangle.width -= i;
      rectangle.height -= j;
    } 
    return rectangle;
  }
  
  protected static void computeRegions(ImageReadParam paramImageReadParam, int paramInt1, int paramInt2, BufferedImage paramBufferedImage, Rectangle paramRectangle1, Rectangle paramRectangle2) {
    if (paramRectangle1 == null)
      throw new IllegalArgumentException("srcRegion == null!"); 
    if (paramRectangle2 == null)
      throw new IllegalArgumentException("destRegion == null!"); 
    paramRectangle1.setBounds(0, 0, paramInt1, paramInt2);
    paramRectangle2.setBounds(0, 0, paramInt1, paramInt2);
    int i = 1;
    int j = 1;
    int k = 0;
    int m = 0;
    if (paramImageReadParam != null) {
      Rectangle rectangle = paramImageReadParam.getSourceRegion();
      if (rectangle != null)
        paramRectangle1.setBounds(paramRectangle1.intersection(rectangle)); 
      i = paramImageReadParam.getSourceXSubsampling();
      j = paramImageReadParam.getSourceYSubsampling();
      k = paramImageReadParam.getSubsamplingXOffset();
      m = paramImageReadParam.getSubsamplingYOffset();
      paramRectangle1.translate(k, m);
      paramRectangle1.width -= k;
      paramRectangle1.height -= m;
      paramRectangle2.setLocation(paramImageReadParam.getDestinationOffset());
    } 
    if (paramRectangle2.x < 0) {
      int i2 = -paramRectangle2.x * i;
      paramRectangle1.x += i2;
      paramRectangle1.width -= i2;
      paramRectangle2.x = 0;
    } 
    if (paramRectangle2.y < 0) {
      int i2 = -paramRectangle2.y * j;
      paramRectangle1.y += i2;
      paramRectangle1.height -= i2;
      paramRectangle2.y = 0;
    } 
    int n = (paramRectangle1.width + i - 1) / i;
    int i1 = (paramRectangle1.height + j - 1) / j;
    paramRectangle2.width = n;
    paramRectangle2.height = i1;
    if (paramBufferedImage != null) {
      Rectangle rectangle = new Rectangle(0, 0, paramBufferedImage.getWidth(), paramBufferedImage.getHeight());
      paramRectangle2.setBounds(paramRectangle2.intersection(rectangle));
      if (paramRectangle2.isEmpty())
        throw new IllegalArgumentException("Empty destination region!"); 
      int i2 = paramRectangle2.x + n - paramBufferedImage.getWidth();
      if (i2 > 0)
        paramRectangle1.width -= i2 * i; 
      int i3 = paramRectangle2.y + i1 - paramBufferedImage.getHeight();
      if (i3 > 0)
        paramRectangle1.height -= i3 * j; 
    } 
    if (paramRectangle1.isEmpty() || paramRectangle2.isEmpty())
      throw new IllegalArgumentException("Empty region!"); 
  }
  
  protected static void checkReadParamBandSettings(ImageReadParam paramImageReadParam, int paramInt1, int paramInt2) {
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    if (paramImageReadParam != null) {
      arrayOfInt1 = paramImageReadParam.getSourceBands();
      arrayOfInt2 = paramImageReadParam.getDestinationBands();
    } 
    int i = (arrayOfInt1 == null) ? paramInt1 : arrayOfInt1.length;
    int j = (arrayOfInt2 == null) ? paramInt2 : arrayOfInt2.length;
    if (i != j)
      throw new IllegalArgumentException("ImageReadParam num source & dest bands differ!"); 
    if (arrayOfInt1 != null)
      for (byte b = 0; b < arrayOfInt1.length; b++) {
        if (arrayOfInt1[b] >= paramInt1)
          throw new IllegalArgumentException("ImageReadParam source bands contains a value >= the number of source bands!"); 
      }  
    if (arrayOfInt2 != null)
      for (byte b = 0; b < arrayOfInt2.length; b++) {
        if (arrayOfInt2[b] >= paramInt2)
          throw new IllegalArgumentException("ImageReadParam dest bands contains a value >= the number of dest bands!"); 
      }  
  }
  
  protected static BufferedImage getDestination(ImageReadParam paramImageReadParam, Iterator<ImageTypeSpecifier> paramIterator, int paramInt1, int paramInt2) throws IIOException {
    if (paramIterator == null || !paramIterator.hasNext())
      throw new IllegalArgumentException("imageTypes null or empty!"); 
    if (paramInt1 * paramInt2 > 2147483647L)
      throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!"); 
    BufferedImage bufferedImage = null;
    ImageTypeSpecifier imageTypeSpecifier = null;
    if (paramImageReadParam != null) {
      bufferedImage = paramImageReadParam.getDestination();
      if (bufferedImage != null)
        return bufferedImage; 
      imageTypeSpecifier = paramImageReadParam.getDestinationType();
    } 
    if (imageTypeSpecifier == null) {
      Object object = paramIterator.next();
      if (!(object instanceof ImageTypeSpecifier))
        throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!"); 
      imageTypeSpecifier = (ImageTypeSpecifier)object;
    } else {
      boolean bool = false;
      while (paramIterator.hasNext()) {
        ImageTypeSpecifier imageTypeSpecifier1 = (ImageTypeSpecifier)paramIterator.next();
        if (imageTypeSpecifier1.equals(imageTypeSpecifier)) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        throw new IIOException("Destination type from ImageReadParam does not match!"); 
    } 
    Rectangle rectangle1 = new Rectangle(0, 0, 0, 0);
    Rectangle rectangle2 = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, paramInt1, paramInt2, null, rectangle1, rectangle2);
    int i = rectangle2.x + rectangle2.width;
    int j = rectangle2.y + rectangle2.height;
    return imageTypeSpecifier.createBufferedImage(i, j);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\ImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */