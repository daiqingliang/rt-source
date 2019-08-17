package javax.imageio;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;

public abstract class ImageWriter implements ImageTranscoder {
  protected ImageWriterSpi originatingProvider = null;
  
  protected Object output = null;
  
  protected Locale[] availableLocales = null;
  
  protected Locale locale = null;
  
  protected List<IIOWriteWarningListener> warningListeners = null;
  
  protected List<Locale> warningLocales = null;
  
  protected List<IIOWriteProgressListener> progressListeners = null;
  
  private boolean abortFlag = false;
  
  protected ImageWriter(ImageWriterSpi paramImageWriterSpi) { this.originatingProvider = paramImageWriterSpi; }
  
  public ImageWriterSpi getOriginatingProvider() { return this.originatingProvider; }
  
  public void setOutput(Object paramObject) {
    if (paramObject != null) {
      ImageWriterSpi imageWriterSpi = getOriginatingProvider();
      if (imageWriterSpi != null) {
        Class[] arrayOfClass = imageWriterSpi.getOutputTypes();
        boolean bool = false;
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (arrayOfClass[b].isInstance(paramObject)) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          throw new IllegalArgumentException("Illegal output type!"); 
      } 
    } 
    this.output = paramObject;
  }
  
  public Object getOutput() { return this.output; }
  
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
  
  public ImageWriteParam getDefaultWriteParam() { return new ImageWriteParam(getLocale()); }
  
  public abstract IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam);
  
  public abstract IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam);
  
  public int getNumThumbnailsSupported(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2) { return 0; }
  
  public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2) { return null; }
  
  public boolean canWriteRasters() { return false; }
  
  public abstract void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException;
  
  public void write(IIOImage paramIIOImage) throws IOException { write(null, paramIIOImage, null); }
  
  public void write(RenderedImage paramRenderedImage) throws IOException { write(null, new IIOImage(paramRenderedImage, null, null), null); }
  
  private void unsupported() {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    throw new UnsupportedOperationException("Unsupported write variant!");
  }
  
  public boolean canWriteSequence() { return false; }
  
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata) throws IOException { unsupported(); }
  
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException { unsupported(); }
  
  public void endWriteSequence() { unsupported(); }
  
  public boolean canReplaceStreamMetadata() {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void replaceStreamMetadata(IIOMetadata paramIIOMetadata) throws IOException { unsupported(); }
  
  public boolean canReplaceImageMetadata(int paramInt) throws IOException {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void replaceImageMetadata(int paramInt, IIOMetadata paramIIOMetadata) throws IOException { unsupported(); }
  
  public boolean canInsertImage(int paramInt) throws IOException {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void writeInsert(int paramInt, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException { unsupported(); }
  
  public boolean canRemoveImage(int paramInt) throws IOException {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void removeImage(int paramInt) throws IOException { unsupported(); }
  
  public boolean canWriteEmpty() {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void prepareWriteEmpty(IIOMetadata paramIIOMetadata1, ImageTypeSpecifier paramImageTypeSpecifier, int paramInt1, int paramInt2, IIOMetadata paramIIOMetadata2, List<? extends BufferedImage> paramList, ImageWriteParam paramImageWriteParam) throws IOException { unsupported(); }
  
  public void endWriteEmpty() {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    throw new IllegalStateException("No call to prepareWriteEmpty!");
  }
  
  public boolean canInsertEmpty(int paramInt) throws IOException {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void prepareInsertEmpty(int paramInt1, ImageTypeSpecifier paramImageTypeSpecifier, int paramInt2, int paramInt3, IIOMetadata paramIIOMetadata, List<? extends BufferedImage> paramList, ImageWriteParam paramImageWriteParam) throws IOException { unsupported(); }
  
  public void endInsertEmpty() { unsupported(); }
  
  public boolean canReplacePixels(int paramInt) throws IOException {
    if (getOutput() == null)
      throw new IllegalStateException("getOutput() == null!"); 
    return false;
  }
  
  public void prepareReplacePixels(int paramInt, Rectangle paramRectangle) throws IOException { unsupported(); }
  
  public void replacePixels(RenderedImage paramRenderedImage, ImageWriteParam paramImageWriteParam) throws IOException { unsupported(); }
  
  public void replacePixels(Raster paramRaster, ImageWriteParam paramImageWriteParam) throws IOException { unsupported(); }
  
  public void endReplacePixels() { unsupported(); }
  
  public void abort() { this.abortFlag = true; }
  
  protected boolean abortRequested() { return this.abortFlag; }
  
  protected void clearAbortRequest() { this.abortFlag = false; }
  
  public void addIIOWriteWarningListener(IIOWriteWarningListener paramIIOWriteWarningListener) {
    if (paramIIOWriteWarningListener == null)
      return; 
    this.warningListeners = ImageReader.addToList(this.warningListeners, paramIIOWriteWarningListener);
    this.warningLocales = ImageReader.addToList(this.warningLocales, getLocale());
  }
  
  public void removeIIOWriteWarningListener(IIOWriteWarningListener paramIIOWriteWarningListener) {
    if (paramIIOWriteWarningListener == null || this.warningListeners == null)
      return; 
    int i = this.warningListeners.indexOf(paramIIOWriteWarningListener);
    if (i != -1) {
      this.warningListeners.remove(i);
      this.warningLocales.remove(i);
      if (this.warningListeners.size() == 0) {
        this.warningListeners = null;
        this.warningLocales = null;
      } 
    } 
  }
  
  public void removeAllIIOWriteWarningListeners() {
    this.warningListeners = null;
    this.warningLocales = null;
  }
  
  public void addIIOWriteProgressListener(IIOWriteProgressListener paramIIOWriteProgressListener) {
    if (paramIIOWriteProgressListener == null)
      return; 
    this.progressListeners = ImageReader.addToList(this.progressListeners, paramIIOWriteProgressListener);
  }
  
  public void removeIIOWriteProgressListener(IIOWriteProgressListener paramIIOWriteProgressListener) {
    if (paramIIOWriteProgressListener == null || this.progressListeners == null)
      return; 
    this.progressListeners = ImageReader.removeFromList(this.progressListeners, paramIIOWriteProgressListener);
  }
  
  public void removeAllIIOWriteProgressListeners() { this.progressListeners = null; }
  
  protected void processImageStarted(int paramInt) throws IOException {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.imageStarted(this, paramInt);
    } 
  }
  
  protected void processImageProgress(float paramFloat) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.imageProgress(this, paramFloat);
    } 
  }
  
  protected void processImageComplete() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.imageComplete(this);
    } 
  }
  
  protected void processThumbnailStarted(int paramInt1, int paramInt2) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.thumbnailStarted(this, paramInt1, paramInt2);
    } 
  }
  
  protected void processThumbnailProgress(float paramFloat) {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.thumbnailProgress(this, paramFloat);
    } 
  }
  
  protected void processThumbnailComplete() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.thumbnailComplete(this);
    } 
  }
  
  protected void processWriteAborted() {
    if (this.progressListeners == null)
      return; 
    int i = this.progressListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteProgressListener iIOWriteProgressListener = (IIOWriteProgressListener)this.progressListeners.get(b);
      iIOWriteProgressListener.writeAborted(this);
    } 
  }
  
  protected void processWarningOccurred(int paramInt, String paramString) {
    if (this.warningListeners == null)
      return; 
    if (paramString == null)
      throw new IllegalArgumentException("warning == null!"); 
    int i = this.warningListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteWarningListener iIOWriteWarningListener = (IIOWriteWarningListener)this.warningListeners.get(b);
      iIOWriteWarningListener.warningOccurred(this, paramInt, paramString);
    } 
  }
  
  protected void processWarningOccurred(int paramInt, String paramString1, String paramString2) {
    if (this.warningListeners == null)
      return; 
    if (paramString1 == null)
      throw new IllegalArgumentException("baseName == null!"); 
    if (paramString2 == null)
      throw new IllegalArgumentException("keyword == null!"); 
    int i = this.warningListeners.size();
    for (byte b = 0; b < i; b++) {
      IIOWriteWarningListener iIOWriteWarningListener = (IIOWriteWarningListener)this.warningListeners.get(b);
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
      iIOWriteWarningListener.warningOccurred(this, paramInt, str);
    } 
  }
  
  public void reset() {
    setOutput(null);
    setLocale(null);
    removeAllIIOWriteWarningListeners();
    removeAllIIOWriteProgressListeners();
    clearAbortRequest();
  }
  
  public void dispose() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\ImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */