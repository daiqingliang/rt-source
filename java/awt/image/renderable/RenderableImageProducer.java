package java.awt.image.renderable;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Enumeration;
import java.util.Vector;

public class RenderableImageProducer implements ImageProducer, Runnable {
  RenderableImage rdblImage;
  
  RenderContext rc;
  
  Vector ics = new Vector();
  
  public RenderableImageProducer(RenderableImage paramRenderableImage, RenderContext paramRenderContext) {
    this.rdblImage = paramRenderableImage;
    this.rc = paramRenderContext;
  }
  
  public void setRenderContext(RenderContext paramRenderContext) { this.rc = paramRenderContext; }
  
  public void addConsumer(ImageConsumer paramImageConsumer) {
    if (!this.ics.contains(paramImageConsumer))
      this.ics.addElement(paramImageConsumer); 
  }
  
  public boolean isConsumer(ImageConsumer paramImageConsumer) { return this.ics.contains(paramImageConsumer); }
  
  public void removeConsumer(ImageConsumer paramImageConsumer) { this.ics.removeElement(paramImageConsumer); }
  
  public void startProduction(ImageConsumer paramImageConsumer) {
    addConsumer(paramImageConsumer);
    Thread thread = new Thread(this, "RenderableImageProducer Thread");
    thread.start();
  }
  
  public void requestTopDownLeftRightResend(ImageConsumer paramImageConsumer) {}
  
  public void run() {
    RenderedImage renderedImage;
    if (this.rc != null) {
      renderedImage = this.rdblImage.createRendering(this.rc);
    } else {
      renderedImage = this.rdblImage.createDefaultRendering();
    } 
    ColorModel colorModel = renderedImage.getColorModel();
    Raster raster = renderedImage.getData();
    SampleModel sampleModel = raster.getSampleModel();
    DataBuffer dataBuffer = raster.getDataBuffer();
    if (colorModel == null)
      colorModel = ColorModel.getRGBdefault(); 
    int i = raster.getMinX();
    int j = raster.getMinY();
    int k = raster.getWidth();
    int m = raster.getHeight();
    Enumeration enumeration = this.ics.elements();
    while (enumeration.hasMoreElements()) {
      ImageConsumer imageConsumer = (ImageConsumer)enumeration.nextElement();
      imageConsumer.setDimensions(k, m);
      imageConsumer.setHints(30);
    } 
    int[] arrayOfInt1 = new int[k];
    int n = sampleModel.getNumBands();
    int[] arrayOfInt2 = new int[n];
    for (byte b = 0; b < m; b++) {
      for (byte b1 = 0; b1 < k; b1++) {
        sampleModel.getPixel(b1, b, arrayOfInt2, dataBuffer);
        arrayOfInt1[b1] = colorModel.getDataElement(arrayOfInt2, 0);
      } 
      enumeration = this.ics.elements();
      while (enumeration.hasMoreElements()) {
        ImageConsumer imageConsumer = (ImageConsumer)enumeration.nextElement();
        imageConsumer.setPixels(0, b, k, 1, colorModel, arrayOfInt1, 0, k);
      } 
    } 
    enumeration = this.ics.elements();
    while (enumeration.hasMoreElements()) {
      ImageConsumer imageConsumer = (ImageConsumer)enumeration.nextElement();
      imageConsumer.imageComplete(3);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\renderable\RenderableImageProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */