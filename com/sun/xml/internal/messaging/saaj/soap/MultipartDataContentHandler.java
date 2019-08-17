package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class MultipartDataContentHandler implements DataContentHandler {
  private ActivationDataFlavor myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");
  
  public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { this.myDF }; }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) { return this.myDF.equals(paramDataFlavor) ? getContent(paramDataSource) : null; }
  
  public Object getContent(DataSource paramDataSource) {
    try {
      return new MimeMultipart(paramDataSource, new ContentType(paramDataSource.getContentType()));
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (paramObject instanceof MimeMultipart)
      try {
        ByteOutputStream byteOutputStream = null;
        if (paramOutputStream instanceof ByteOutputStream) {
          byteOutputStream = (ByteOutputStream)paramOutputStream;
        } else {
          throw new IOException("Input Stream expected to be a com.sun.xml.internal.messaging.saaj.util.ByteOutputStream, but found " + paramOutputStream.getClass().getName());
        } 
        ((MimeMultipart)paramObject).writeTo(byteOutputStream);
      } catch (Exception exception) {
        throw new IOException(exception.toString());
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\MultipartDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */