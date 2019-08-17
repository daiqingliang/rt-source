package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.xml.internal.stream.writers.XMLDOMWriterImpl;
import com.sun.xml.internal.stream.writers.XMLEventWriterImpl;
import com.sun.xml.internal.stream.writers.XMLStreamWriterImpl;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamResult;

public class XMLOutputFactoryImpl extends XMLOutputFactory {
  private PropertyManager fPropertyManager = new PropertyManager(2);
  
  private XMLStreamWriterImpl fStreamWriter = null;
  
  boolean fReuseInstance = false;
  
  private static final boolean DEBUG = false;
  
  private boolean fPropertyChanged;
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream) throws XMLStreamException { return createXMLEventWriter(paramOutputStream, null); }
  
  public XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream, String paramString) throws XMLStreamException { return new XMLEventWriterImpl(createXMLStreamWriter(paramOutputStream, paramString)); }
  
  public XMLEventWriter createXMLEventWriter(Result paramResult) throws XMLStreamException { return (paramResult instanceof StAXResult && ((StAXResult)paramResult).getXMLEventWriter() != null) ? ((StAXResult)paramResult).getXMLEventWriter() : new XMLEventWriterImpl(createXMLStreamWriter(paramResult)); }
  
  public XMLEventWriter createXMLEventWriter(Writer paramWriter) throws XMLStreamException { return new XMLEventWriterImpl(createXMLStreamWriter(paramWriter)); }
  
  public XMLStreamWriter createXMLStreamWriter(Result paramResult) throws XMLStreamException {
    if (paramResult instanceof StreamResult)
      return createXMLStreamWriter((StreamResult)paramResult, null); 
    if (paramResult instanceof DOMResult)
      return new XMLDOMWriterImpl((DOMResult)paramResult); 
    if (paramResult instanceof StAXResult) {
      if (((StAXResult)paramResult).getXMLStreamWriter() != null)
        return ((StAXResult)paramResult).getXMLStreamWriter(); 
      throw new UnsupportedOperationException("Result of type " + paramResult + " is not supported");
    } 
    if (paramResult.getSystemId() != null)
      return createXMLStreamWriter(new StreamResult(paramResult.getSystemId())); 
    throw new UnsupportedOperationException("Result of type " + paramResult + " is not supported. Supported result types are: DOMResult, StAXResult and StreamResult.");
  }
  
  public XMLStreamWriter createXMLStreamWriter(Writer paramWriter) throws XMLStreamException { return createXMLStreamWriter(toStreamResult(null, paramWriter, null), null); }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream) throws XMLStreamException { return createXMLStreamWriter(paramOutputStream, null); }
  
  public XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString) throws XMLStreamException { return createXMLStreamWriter(toStreamResult(paramOutputStream, null, null), paramString); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("Property not supported"); 
    if (this.fPropertyManager.containsProperty(paramString))
      return this.fPropertyManager.getProperty(paramString); 
    throw new IllegalArgumentException("Property not supported");
  }
  
  public boolean isPropertySupported(String paramString) { return (paramString == null) ? false : this.fPropertyManager.containsProperty(paramString); }
  
  public void setProperty(String paramString, Object paramObject) throws IllegalArgumentException {
    if (paramString == null || paramObject == null || !this.fPropertyManager.containsProperty(paramString))
      throw new IllegalArgumentException("Property " + paramString + "is not supported"); 
    if (paramString == "reuse-instance" || paramString.equals("reuse-instance")) {
      this.fReuseInstance = ((Boolean)paramObject).booleanValue();
      if (this.fReuseInstance)
        throw new IllegalArgumentException("Property " + paramString + " is not supported: XMLStreamWriters are not Thread safe"); 
    } else {
      this.fPropertyChanged = true;
    } 
    this.fPropertyManager.setProperty(paramString, paramObject);
  }
  
  StreamResult toStreamResult(OutputStream paramOutputStream, Writer paramWriter, String paramString) {
    StreamResult streamResult = new StreamResult();
    streamResult.setOutputStream(paramOutputStream);
    streamResult.setWriter(paramWriter);
    streamResult.setSystemId(paramString);
    return streamResult;
  }
  
  XMLStreamWriter createXMLStreamWriter(StreamResult paramStreamResult, String paramString) throws XMLStreamException {
    try {
      if (this.fReuseInstance && this.fStreamWriter != null && this.fStreamWriter.canReuse() && !this.fPropertyChanged) {
        this.fStreamWriter.reset();
        this.fStreamWriter.setOutput(paramStreamResult, paramString);
        return this.fStreamWriter;
      } 
      return this.fStreamWriter = new XMLStreamWriterImpl(paramStreamResult, paramString, new PropertyManager(this.fPropertyManager));
    } catch (IOException iOException) {
      throw new XMLStreamException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\XMLOutputFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */