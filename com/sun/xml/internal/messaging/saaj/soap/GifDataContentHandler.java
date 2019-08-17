package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class GifDataContentHandler extends Component implements DataContentHandler {
  private static ActivationDataFlavor myDF = new ActivationDataFlavor(java.awt.Image.class, "image/gif", "GIF Image");
  
  protected ActivationDataFlavor getDF() { return myDF; }
  
  public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { getDF() }; }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws IOException { return getDF().equals(paramDataFlavor) ? getContent(paramDataSource) : null; }
  
  public Object getContent(DataSource paramDataSource) throws IOException {
    InputStream inputStream = paramDataSource.getInputStream();
    int i = 0;
    byte[] arrayOfByte = new byte[1024];
    int j;
    while ((j = inputStream.read(arrayOfByte, i, arrayOfByte.length - i)) != -1) {
      i += j;
      if (i >= arrayOfByte.length) {
        int k = arrayOfByte.length;
        if (k < 262144) {
          k += k;
        } else {
          k += 262144;
        } 
        byte[] arrayOfByte1 = new byte[k];
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, i);
        arrayOfByte = arrayOfByte1;
      } 
    } 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return toolkit.createImage(arrayOfByte, 0, i);
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (paramObject != null && !(paramObject instanceof java.awt.Image))
      throw new IOException("\"" + getDF().getMimeType() + "\" DataContentHandler requires Image object, was given object of type " + paramObject.getClass().toString()); 
    throw new IOException(getDF().getMimeType() + " encoding not supported");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\GifDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */