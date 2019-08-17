package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;

public class JpegDataContentHandler extends Component implements DataContentHandler {
  public static final String STR_SRC = "java.awt.Image";
  
  public DataFlavor[] getTransferDataFlavors() {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
    try {
      arrayOfDataFlavor[0] = new ActivationDataFlavor(Class.forName("java.awt.Image"), "image/jpeg", "JPEG");
    } catch (Exception exception) {
      System.out.println(exception);
    } 
    return arrayOfDataFlavor;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) {
    if (paramDataFlavor.getMimeType().startsWith("image/jpeg") && paramDataFlavor.getRepresentationClass().getName().equals("java.awt.Image")) {
      InputStream inputStream = null;
      BufferedImage bufferedImage = null;
      try {
        inputStream = paramDataSource.getInputStream();
        bufferedImage = ImageIO.read(inputStream);
      } catch (Exception exception) {
        System.out.println(exception);
      } 
      return bufferedImage;
    } 
    return null;
  }
  
  public Object getContent(DataSource paramDataSource) {
    InputStream inputStream = null;
    BufferedImage bufferedImage = null;
    try {
      inputStream = paramDataSource.getInputStream();
      bufferedImage = ImageIO.read(inputStream);
    } catch (Exception exception) {}
    return bufferedImage;
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (!paramString.equals("image/jpeg"))
      throw new IOException("Invalid content type \"" + paramString + "\" for ImageContentHandler"); 
    if (paramObject == null)
      throw new IOException("Null object for ImageContentHandler"); 
    try {
      BufferedImage bufferedImage = null;
      if (paramObject instanceof BufferedImage) {
        bufferedImage = (BufferedImage)paramObject;
      } else {
        Image image = (Image)paramObject;
        MediaTracker mediaTracker = new MediaTracker(this);
        mediaTracker.addImage(image, 0);
        mediaTracker.waitForAll();
        if (mediaTracker.isErrorAny())
          throw new IOException("Error while loading image"); 
        bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), 1);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
      } 
      ImageIO.write(bufferedImage, "jpeg", paramOutputStream);
    } catch (Exception exception) {
      throw new IOException("Unable to run the JPEG Encoder on a stream " + exception.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\JpegDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */