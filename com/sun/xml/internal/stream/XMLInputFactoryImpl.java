package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLStreamFilterImpl;
import com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
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
import javax.xml.transform.stream.StreamSource;

public class XMLInputFactoryImpl extends XMLInputFactory {
  private PropertyManager fPropertyManager = new PropertyManager(1);
  
  private static final boolean DEBUG = false;
  
  private XMLStreamReaderImpl fTempReader = null;
  
  boolean fPropertyChanged = false;
  
  boolean fReuseInstance = false;
  
  void initEventReader() { this.fPropertyChanged = true; }
  
  public XMLEventReader createXMLEventReader(InputStream paramInputStream) throws XMLStreamException {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramInputStream));
  }
  
  public XMLEventReader createXMLEventReader(Reader paramReader) throws XMLStreamException {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramReader));
  }
  
  public XMLEventReader createXMLEventReader(Source paramSource) throws XMLStreamException {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramSource));
  }
  
  public XMLEventReader createXMLEventReader(String paramString, InputStream paramInputStream) throws XMLStreamException {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramString, paramInputStream));
  }
  
  public XMLEventReader createXMLEventReader(InputStream paramInputStream, String paramString) throws XMLStreamException {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramInputStream, paramString));
  }
  
  public XMLEventReader createXMLEventReader(String paramString, Reader paramReader) throws XMLStreamException {
    initEventReader();
    return new XMLEventReaderImpl(createXMLStreamReader(paramString, paramReader));
  }
  
  public XMLEventReader createXMLEventReader(XMLStreamReader paramXMLStreamReader) throws XMLStreamException { return new XMLEventReaderImpl(paramXMLStreamReader); }
  
  public XMLStreamReader createXMLStreamReader(InputStream paramInputStream) throws XMLStreamException {
    XMLInputSource xMLInputSource = new XMLInputSource(null, null, null, paramInputStream, null);
    return getXMLStreamReaderImpl(xMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(Reader paramReader) throws XMLStreamException {
    XMLInputSource xMLInputSource = new XMLInputSource(null, null, null, paramReader, null);
    return getXMLStreamReaderImpl(xMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(String paramString, Reader paramReader) throws XMLStreamException {
    XMLInputSource xMLInputSource = new XMLInputSource(null, paramString, null, paramReader, null);
    return getXMLStreamReaderImpl(xMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(Source paramSource) throws XMLStreamException { return new XMLStreamReaderImpl(jaxpSourcetoXMLInputSource(paramSource), new PropertyManager(this.fPropertyManager)); }
  
  public XMLStreamReader createXMLStreamReader(String paramString, InputStream paramInputStream) throws XMLStreamException {
    XMLInputSource xMLInputSource = new XMLInputSource(null, paramString, null, paramInputStream, null);
    return getXMLStreamReaderImpl(xMLInputSource);
  }
  
  public XMLStreamReader createXMLStreamReader(InputStream paramInputStream, String paramString) throws XMLStreamException {
    XMLInputSource xMLInputSource = new XMLInputSource(null, null, null, paramInputStream, paramString);
    return getXMLStreamReaderImpl(xMLInputSource);
  }
  
  public XMLEventAllocator getEventAllocator() { return (XMLEventAllocator)getProperty("javax.xml.stream.allocator"); }
  
  public XMLReporter getXMLReporter() { return (XMLReporter)this.fPropertyManager.getProperty("javax.xml.stream.reporter"); }
  
  public XMLResolver getXMLResolver() {
    Object object = this.fPropertyManager.getProperty("javax.xml.stream.resolver");
    return (XMLResolver)object;
  }
  
  public void setXMLReporter(XMLReporter paramXMLReporter) { this.fPropertyManager.setProperty("javax.xml.stream.reporter", paramXMLReporter); }
  
  public void setXMLResolver(XMLResolver paramXMLResolver) { this.fPropertyManager.setProperty("javax.xml.stream.resolver", paramXMLResolver); }
  
  public XMLEventReader createFilteredReader(XMLEventReader paramXMLEventReader, EventFilter paramEventFilter) throws XMLStreamException { return new EventFilterSupport(paramXMLEventReader, paramEventFilter); }
  
  public XMLStreamReader createFilteredReader(XMLStreamReader paramXMLStreamReader, StreamFilter paramStreamFilter) throws XMLStreamException { return (paramXMLStreamReader != null && paramStreamFilter != null) ? new XMLStreamFilterImpl(paramXMLStreamReader, paramStreamFilter) : null; }
  
  public Object getProperty(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("Property not supported"); 
    if (this.fPropertyManager.containsProperty(paramString))
      return this.fPropertyManager.getProperty(paramString); 
    throw new IllegalArgumentException("Property not supported");
  }
  
  public boolean isPropertySupported(String paramString) { return (paramString == null) ? false : this.fPropertyManager.containsProperty(paramString); }
  
  public void setEventAllocator(XMLEventAllocator paramXMLEventAllocator) { this.fPropertyManager.setProperty("javax.xml.stream.allocator", paramXMLEventAllocator); }
  
  public void setProperty(String paramString, Object paramObject) throws IllegalArgumentException {
    if (paramString == null || paramObject == null || !this.fPropertyManager.containsProperty(paramString))
      throw new IllegalArgumentException("Property " + paramString + " is not supported"); 
    if (paramString == "reuse-instance" || paramString.equals("reuse-instance")) {
      this.fReuseInstance = ((Boolean)paramObject).booleanValue();
    } else {
      this.fPropertyChanged = true;
    } 
    this.fPropertyManager.setProperty(paramString, paramObject);
  }
  
  XMLStreamReader getXMLStreamReaderImpl(XMLInputSource paramXMLInputSource) throws XMLStreamException {
    if (this.fTempReader == null) {
      this.fPropertyChanged = false;
      return this.fTempReader = new XMLStreamReaderImpl(paramXMLInputSource, new PropertyManager(this.fPropertyManager));
    } 
    if (this.fReuseInstance && this.fTempReader.canReuse() && !this.fPropertyChanged) {
      this.fTempReader.reset();
      this.fTempReader.setInputSource(paramXMLInputSource);
      this.fPropertyChanged = false;
      return this.fTempReader;
    } 
    this.fPropertyChanged = false;
    return this.fTempReader = new XMLStreamReaderImpl(paramXMLInputSource, new PropertyManager(this.fPropertyManager));
  }
  
  XMLInputSource jaxpSourcetoXMLInputSource(Source paramSource) {
    if (paramSource instanceof StreamSource) {
      StreamSource streamSource = (StreamSource)paramSource;
      String str1 = streamSource.getSystemId();
      String str2 = streamSource.getPublicId();
      InputStream inputStream = streamSource.getInputStream();
      Reader reader = streamSource.getReader();
      return (inputStream != null) ? new XMLInputSource(str2, str1, null, inputStream, null) : ((reader != null) ? new XMLInputSource(str2, str1, null, reader, null) : new XMLInputSource(str2, str1, null));
    } 
    throw new UnsupportedOperationException("Cannot create XMLStreamReader or XMLEventReader from a " + paramSource.getClass().getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\XMLInputFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */