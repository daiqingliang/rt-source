package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageDataContentHandler extends Component implements DataContentHandler {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  private DataFlavor[] flavor;
  
  public ImageDataContentHandler() {
    String[] arrayOfString = ImageIO.getReaderMIMETypes();
    this.flavor = new DataFlavor[arrayOfString.length];
    for (byte b = 0; b < arrayOfString.length; b++)
      this.flavor[b] = new ActivationDataFlavor(Image.class, arrayOfString[b], "Image"); 
  }
  
  public DataFlavor[] getTransferDataFlavors() { return (DataFlavor[])Arrays.copyOf(this.flavor, this.flavor.length); }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws IOException {
    for (byte b = 0; b < this.flavor.length; b++) {
      if (this.flavor[b].equals(paramDataFlavor))
        return getContent(paramDataSource); 
    } 
    return null;
  }
  
  public Object getContent(DataSource paramDataSource) throws IOException { return ImageIO.read(new BufferedInputStream(paramDataSource.getInputStream())); }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    try {
      BufferedImage bufferedImage = null;
      if (paramObject instanceof BufferedImage) {
        bufferedImage = (BufferedImage)paramObject;
      } else if (paramObject instanceof Image) {
        bufferedImage = render((Image)paramObject);
      } else {
        log.log(Level.SEVERE, "SAAJ0520.soap.invalid.obj.type", new String[] { paramObject.getClass().toString() });
        throw new IOException("ImageDataContentHandler requires Image object, was given object of type " + paramObject.getClass().toString());
      } 
      ImageWriter imageWriter = null;
      Iterator iterator = ImageIO.getImageWritersByMIMEType(paramString);
      if (iterator.hasNext())
        imageWriter = (ImageWriter)iterator.next(); 
      if (imageWriter != null) {
        ImageOutputStream imageOutputStream = null;
        imageOutputStream = ImageIO.createImageOutputStream(paramOutputStream);
        imageWriter.setOutput(imageOutputStream);
        imageWriter.write(bufferedImage);
        imageWriter.dispose();
        imageOutputStream.close();
      } else {
        log.log(Level.SEVERE, "SAAJ0526.soap.unsupported.mime.type", new String[] { paramString });
        throw new IOException("Unsupported mime type:" + paramString);
      } 
    } catch (Exception exception) {
      log.severe("SAAJ0525.soap.cannot.encode.img");
      throw new IOException("Unable to encode the image to a stream " + exception.getMessage());
    } 
  }
  
  private BufferedImage render(Image paramImage) throws InterruptedException {
    MediaTracker mediaTracker = new MediaTracker(this);
    mediaTracker.addImage(paramImage, 0);
    mediaTracker.waitForAll();
    BufferedImage bufferedImage = new BufferedImage(paramImage.getWidth(null), paramImage.getHeight(null), 1);
    Graphics2D graphics2D = bufferedImage.createGraphics();
    graphics2D.drawImage(paramImage, 0, 0, null);
    graphics2D.dispose();
    return bufferedImage;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ImageDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */