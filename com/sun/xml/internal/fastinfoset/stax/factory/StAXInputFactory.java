package com.sun.xml.internal.fastinfoset.stax.factory;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXManager;
import com.sun.xml.internal.fastinfoset.stax.events.StAXEventReader;
import com.sun.xml.internal.fastinfoset.stax.events.StAXFilteredEvent;
import com.sun.xml.internal.fastinfoset.stax.util.StAXFilteredParser;
import com.sun.xml.internal.fastinfoset.tools.XML_SAX_FI;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

public class StAXInputFactory extends XMLInputFactory {
  private StAXManager _manager = new StAXManager(1);
  
  public static XMLInputFactory newInstance() { return XMLInputFactory.newInstance(); }
  
  public XMLStreamReader createXMLStreamReader(Reader paramReader) throws XMLStreamException { return getXMLStreamReader(paramReader); }
  
  public XMLStreamReader createXMLStreamReader(InputStream paramInputStream) throws XMLStreamException { return new StAXDocumentParser(paramInputStream, this._manager); }
  
  public XMLStreamReader createXMLStreamReader(String paramString, Reader paramReader) throws XMLStreamException { return getXMLStreamReader(paramReader); }
  
  public XMLStreamReader createXMLStreamReader(Source paramSource) throws XMLStreamException { return null; }
  
  public XMLStreamReader createXMLStreamReader(String paramString, InputStream paramInputStream) throws XMLStreamException { return createXMLStreamReader(paramInputStream); }
  
  public XMLStreamReader createXMLStreamReader(InputStream paramInputStream, String paramString) throws XMLStreamException { return createXMLStreamReader(paramInputStream); }
  
  XMLStreamReader getXMLStreamReader(String paramString1, InputStream paramInputStream, String paramString2) throws XMLStreamException { return createXMLStreamReader(paramInputStream); }
  
  XMLStreamReader getXMLStreamReader(Reader paramReader) throws XMLStreamException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
    StAXDocumentParser stAXDocumentParser = null;
    try {
      XML_SAX_FI xML_SAX_FI = new XML_SAX_FI();
      xML_SAX_FI.convert(paramReader, bufferedOutputStream);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      BufferedInputStream bufferedInputStream = new BufferedInputStream(byteArrayInputStream);
      stAXDocumentParser = new StAXDocumentParser();
      stAXDocumentParser.setInputStream(bufferedInputStream);
      stAXDocumentParser.setManager(this._manager);
      return stAXDocumentParser;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public XMLEventReader createXMLEventReader(InputStream paramInputStream) throws XMLStreamException { return new StAXEventReader(createXMLStreamReader(paramInputStream)); }
  
  public XMLEventReader createXMLEventReader(Reader paramReader) throws XMLStreamException { return new StAXEventReader(createXMLStreamReader(paramReader)); }
  
  public XMLEventReader createXMLEventReader(Source paramSource) throws XMLStreamException { return new StAXEventReader(createXMLStreamReader(paramSource)); }
  
  public XMLEventReader createXMLEventReader(String paramString, InputStream paramInputStream) throws XMLStreamException { return new StAXEventReader(createXMLStreamReader(paramString, paramInputStream)); }
  
  public XMLEventReader createXMLEventReader(InputStream paramInputStream, String paramString) throws XMLStreamException { return new StAXEventReader(createXMLStreamReader(paramInputStream, paramString)); }
  
  public XMLEventReader createXMLEventReader(String paramString, Reader paramReader) throws XMLStreamException { return new StAXEventReader(createXMLStreamReader(paramString, paramReader)); }
  
  public XMLEventReader createXMLEventReader(XMLStreamReader paramXMLStreamReader) throws XMLStreamException { return new StAXEventReader(paramXMLStreamReader); }
  
  public XMLEventAllocator getEventAllocator() { return (XMLEventAllocator)getProperty("javax.xml.stream.allocator"); }
  
  public XMLReporter getXMLReporter() { return (XMLReporter)this._manager.getProperty("javax.xml.stream.reporter"); }
  
  public XMLResolver getXMLResolver() {
    Object object = this._manager.getProperty("javax.xml.stream.resolver");
    return (XMLResolver)object;
  }
  
  public void setXMLReporter(XMLReporter paramXMLReporter) { this._manager.setProperty("javax.xml.stream.reporter", paramXMLReporter); }
  
  public void setXMLResolver(XMLResolver paramXMLResolver) { this._manager.setProperty("javax.xml.stream.resolver", paramXMLResolver); }
  
  public XMLEventReader createFilteredReader(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter) throws XMLStreamException { return new StAXFilteredEvent(paramXMLEventReader, paramEventFilter); }
  
  public XMLStreamReader createFilteredReader(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter) throws XMLStreamException { return (paramXMLStreamReader != null && paramStreamFilter != null) ? new StAXFilteredParser(paramXMLStreamReader, paramStreamFilter) : null; }
  
  public Object getProperty(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPropertyName")); 
    if (this._manager.containsProperty(paramString))
      return this._manager.getProperty(paramString); 
    throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { paramString }));
  }
  
  public boolean isPropertySupported(String paramString) { return (paramString == null) ? false : this._manager.containsProperty(paramString); }
  
  public void setEventAllocator(XMLEventAllocator paramXMLEventAllocator) { this._manager.setProperty("javax.xml.stream.allocator", paramXMLEventAllocator); }
  
  public void setProperty(String paramString, Object paramObject) throws IllegalArgumentException { this._manager.setProperty(paramString, paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\factory\StAXInputFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */