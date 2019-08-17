package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class StAXOutputFactory extends XMLOutputFactory {
  private StAXManager _manager = null;
  
  public StAXOutputFactory() { this._manager = new StAXManager(2); }
  
  public XMLEventWriter createXMLEventWriter(Result paramResult) throws XMLStreamException { return new StAXEventWriter(createXMLStreamWriter(paramResult)); }
  
  public XMLEventWriter createXMLEventWriter(Writer paramWriter) throws XMLStreamException { return new StAXEventWriter(createXMLStreamWriter(paramWriter)); }
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream) throws XMLStreamException { return new StAXEventWriter(createXMLStreamWriter(paramOutputStream)); }
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream, String paramString) throws XMLStreamException { return new StAXEventWriter(createXMLStreamWriter(paramOutputStream, paramString)); }
  
  public XMLStreamWriter createXMLStreamWriter(Result paramResult) throws XMLStreamException {
    if (paramResult instanceof StreamResult) {
      StreamResult streamResult = (StreamResult)paramResult;
      if (streamResult.getWriter() != null)
        return createXMLStreamWriter(streamResult.getWriter()); 
      if (streamResult.getOutputStream() != null)
        return createXMLStreamWriter(streamResult.getOutputStream()); 
      if (streamResult.getSystemId() != null) {
        fileWriter = null;
        bool = true;
        try {
          fileWriter = new FileWriter(new File(streamResult.getSystemId()));
          XMLStreamWriter xMLStreamWriter = createXMLStreamWriter(fileWriter);
          bool = false;
          return xMLStreamWriter;
        } catch (IOException iOException) {
          throw new XMLStreamException(iOException);
        } finally {
          if (bool && fileWriter != null)
            try {
              fileWriter.close();
            } catch (IOException iOException) {} 
        } 
      } 
    } else {
      fileWriter = null;
      bool = true;
      try {
        fileWriter = new FileWriter(new File(paramResult.getSystemId()));
        XMLStreamWriter xMLStreamWriter = createXMLStreamWriter(fileWriter);
        bool = false;
        return xMLStreamWriter;
      } catch (IOException iOException) {
        throw new XMLStreamException(iOException);
      } finally {
        if (bool && fileWriter != null)
          try {
            fileWriter.close();
          } catch (IOException iOException) {} 
      } 
    } 
    throw new UnsupportedOperationException();
  }
  
  public XMLStreamWriter createXMLStreamWriter(Writer paramWriter) throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream) throws XMLStreamException { return new StAXDocumentSerializer(paramOutputStream, new StAXManager(this._manager)); }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString) throws XMLStreamException {
    StAXDocumentSerializer stAXDocumentSerializer = new StAXDocumentSerializer(paramOutputStream, new StAXManager(this._manager));
    stAXDocumentSerializer.setEncoding(paramString);
    return stAXDocumentSerializer;
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { null })); 
    if (this._manager.containsProperty(paramString))
      return this._manager.getProperty(paramString); 
    throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { paramString }));
  }
  
  public boolean isPropertySupported(String paramString) { return (paramString == null) ? false : this._manager.containsProperty(paramString); }
  
  public void setProperty(String paramString, Object paramObject) throws IllegalArgumentException { this._manager.setProperty(paramString, paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\factory\StAXOutputFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */