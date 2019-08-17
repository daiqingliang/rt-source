package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlDataContentHandler implements DataContentHandler {
  public static final String STR_SRC = "javax.xml.transform.stream.StreamSource";
  
  private static Class streamSourceClass = null;
  
  public XmlDataContentHandler() throws ClassNotFoundException {
    if (streamSourceClass == null)
      streamSourceClass = Class.forName("javax.xml.transform.stream.StreamSource"); 
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[2];
    arrayOfDataFlavor[0] = new ActivationDataFlavor(streamSourceClass, "text/xml", "XML");
    arrayOfDataFlavor[1] = new ActivationDataFlavor(streamSourceClass, "application/xml", "XML");
    return arrayOfDataFlavor;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws IOException { return ((paramDataFlavor.getMimeType().startsWith("text/xml") || paramDataFlavor.getMimeType().startsWith("application/xml")) && paramDataFlavor.getRepresentationClass().getName().equals("javax.xml.transform.stream.StreamSource")) ? new StreamSource(paramDataSource.getInputStream()) : null; }
  
  public Object getContent(DataSource paramDataSource) throws IOException { return new StreamSource(paramDataSource.getInputStream()); }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (!paramString.startsWith("text/xml") && !paramString.startsWith("application/xml"))
      throw new IOException("Invalid content type \"" + paramString + "\" for XmlDCH"); 
    try {
      Transformer transformer = EfficientStreamingTransformer.newTransformer();
      StreamResult streamResult = new StreamResult(paramOutputStream);
      if (paramObject instanceof DataSource) {
        transformer.transform((Source)getContent((DataSource)paramObject), streamResult);
      } else {
        Source source = null;
        if (paramObject instanceof String) {
          source = new StreamSource(new StringReader((String)paramObject));
        } else {
          source = (Source)paramObject;
        } 
        transformer.transform(source, streamResult);
      } 
    } catch (Exception exception) {
      throw new IOException("Unable to run the JAXP transformer on a stream " + exception.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\XmlDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */