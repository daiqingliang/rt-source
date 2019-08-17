package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlDataContentHandler implements DataContentHandler {
  private final DataFlavor[] flavors = new DataFlavor[3];
  
  public XmlDataContentHandler() throws ClassNotFoundException {
    this.flavors[0] = new ActivationDataFlavor(StreamSource.class, "text/xml", "XML");
    this.flavors[1] = new ActivationDataFlavor(StreamSource.class, "application/xml", "XML");
    this.flavors[2] = new ActivationDataFlavor(String.class, "text/xml", "XML String");
  }
  
  public DataFlavor[] getTransferDataFlavors() { return (DataFlavor[])Arrays.copyOf(this.flavors, this.flavors.length); }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws IOException {
    for (DataFlavor dataFlavor : this.flavors) {
      if (dataFlavor.equals(paramDataFlavor))
        return getContent(paramDataSource); 
    } 
    return null;
  }
  
  public Object getContent(DataSource paramDataSource) throws IOException {
    String str1 = paramDataSource.getContentType();
    String str2 = null;
    if (str1 != null) {
      ContentType contentType = new ContentType(str1);
      if (!isXml(contentType))
        throw new IOException("Cannot convert DataSource with content type \"" + str1 + "\" to object in XmlDataContentHandler"); 
      str2 = contentType.getParameter("charset");
    } 
    return (str2 != null) ? new StreamSource(new InputStreamReader(paramDataSource.getInputStream()), str2) : new StreamSource(paramDataSource.getInputStream());
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    if (!(paramObject instanceof DataSource) && !(paramObject instanceof Source) && !(paramObject instanceof String))
      throw new IOException("Invalid Object type = " + paramObject.getClass() + ". XmlDataContentHandler can only convert DataSource|Source|String to XML."); 
    ContentType contentType = new ContentType(paramString);
    if (!isXml(contentType))
      throw new IOException("Invalid content type \"" + paramString + "\" for XmlDataContentHandler"); 
    String str = contentType.getParameter("charset");
    if (paramObject instanceof String) {
      String str1 = (String)paramObject;
      if (str == null)
        str = "utf-8"; 
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(paramOutputStream, str);
      outputStreamWriter.write(str1, 0, str1.length());
      outputStreamWriter.flush();
      return;
    } 
    Source source = (paramObject instanceof DataSource) ? (Source)getContent((DataSource)paramObject) : (Source)paramObject;
    try {
      Transformer transformer = XmlUtil.newTransformer();
      if (str != null)
        transformer.setOutputProperty("encoding", str); 
      StreamResult streamResult = new StreamResult(paramOutputStream);
      transformer.transform(source, streamResult);
    } catch (Exception exception) {
      throw new IOException("Unable to run the JAXP transformer in XmlDataContentHandler " + exception.getMessage());
    } 
  }
  
  private boolean isXml(ContentType paramContentType) { return (paramContentType.getSubType().equals("xml") && (paramContentType.getPrimaryType().equals("text") || paramContentType.getPrimaryType().equals("application"))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\XmlDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */