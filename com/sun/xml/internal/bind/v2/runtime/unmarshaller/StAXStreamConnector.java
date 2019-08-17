package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import java.lang.reflect.Constructor;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class StAXStreamConnector extends StAXConnector {
  private final XMLStreamReader staxStreamReader;
  
  protected final StringBuilder buffer = new StringBuilder();
  
  protected boolean textReported = false;
  
  private final Attributes attributes = new Attributes() {
      public int getLength() { return StAXStreamConnector.this.staxStreamReader.getAttributeCount(); }
      
      public String getURI(int param1Int) {
        String str = StAXStreamConnector.this.staxStreamReader.getAttributeNamespace(param1Int);
        return (str == null) ? "" : str;
      }
      
      public String getLocalName(int param1Int) { return StAXStreamConnector.this.staxStreamReader.getAttributeLocalName(param1Int); }
      
      public String getQName(int param1Int) {
        String str = StAXStreamConnector.this.staxStreamReader.getAttributePrefix(param1Int);
        return (str == null || str.length() == 0) ? getLocalName(param1Int) : (str + ':' + getLocalName(param1Int));
      }
      
      public String getType(int param1Int) { return StAXStreamConnector.this.staxStreamReader.getAttributeType(param1Int); }
      
      public String getValue(int param1Int) { return StAXStreamConnector.this.staxStreamReader.getAttributeValue(param1Int); }
      
      public int getIndex(String param1String1, String param1String2) {
        for (int i = getLength() - 1; i >= 0; i--) {
          if (param1String2.equals(getLocalName(i)) && param1String1.equals(getURI(i)))
            return i; 
        } 
        return -1;
      }
      
      public int getIndex(String param1String) {
        for (int i = getLength() - 1; i >= 0; i--) {
          if (param1String.equals(getQName(i)))
            return i; 
        } 
        return -1;
      }
      
      public String getType(String param1String1, String param1String2) {
        int i = getIndex(param1String1, param1String2);
        return (i < 0) ? null : getType(i);
      }
      
      public String getType(String param1String) {
        int i = getIndex(param1String);
        return (i < 0) ? null : getType(i);
      }
      
      public String getValue(String param1String1, String param1String2) {
        int i = getIndex(param1String1, param1String2);
        return (i < 0) ? null : getValue(i);
      }
      
      public String getValue(String param1String) {
        int i = getIndex(param1String);
        return (i < 0) ? null : getValue(i);
      }
    };
  
  private static final Class FI_STAX_READER_CLASS = initFIStAXReaderClass();
  
  private static final Constructor<? extends StAXConnector> FI_CONNECTOR_CTOR = initFastInfosetConnectorClass();
  
  private static final Class STAX_EX_READER_CLASS = initStAXExReader();
  
  private static final Constructor<? extends StAXConnector> STAX_EX_CONNECTOR_CTOR = initStAXExConnector();
  
  public static StAXConnector create(XMLStreamReader paramXMLStreamReader, XmlVisitor paramXmlVisitor) {
    Class clazz = paramXMLStreamReader.getClass();
    if (FI_STAX_READER_CLASS != null && FI_STAX_READER_CLASS.isAssignableFrom(clazz) && FI_CONNECTOR_CTOR != null)
      try {
        return (StAXConnector)FI_CONNECTOR_CTOR.newInstance(new Object[] { paramXMLStreamReader, paramXmlVisitor });
      } catch (Exception exception) {} 
    boolean bool = clazz.getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
    if ((!getBoolProp(paramXMLStreamReader, "org.codehaus.stax2.internNames") || !getBoolProp(paramXMLStreamReader, "org.codehaus.stax2.internNsUris")) && !bool && !checkImplementaionNameOfSjsxp(paramXMLStreamReader))
      paramXmlVisitor = new InterningXmlVisitor(paramXmlVisitor); 
    if (STAX_EX_READER_CLASS != null && STAX_EX_READER_CLASS.isAssignableFrom(clazz))
      try {
        return (StAXConnector)STAX_EX_CONNECTOR_CTOR.newInstance(new Object[] { paramXMLStreamReader, paramXmlVisitor });
      } catch (Exception exception) {} 
    return new StAXStreamConnector(paramXMLStreamReader, paramXmlVisitor);
  }
  
  private static boolean checkImplementaionNameOfSjsxp(XMLStreamReader paramXMLStreamReader) {
    try {
      Object object = paramXMLStreamReader.getProperty("http://java.sun.com/xml/stream/properties/implementation-name");
      return (object != null && object.equals("sjsxp"));
    } catch (Exception exception) {
      return false;
    } 
  }
  
  private static boolean getBoolProp(XMLStreamReader paramXMLStreamReader, String paramString) {
    try {
      Object object = paramXMLStreamReader.getProperty(paramString);
      return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : 0;
    } catch (Exception exception) {
      return false;
    } 
  }
  
  protected StAXStreamConnector(XMLStreamReader paramXMLStreamReader, XmlVisitor paramXmlVisitor) {
    super(paramXmlVisitor);
    this.staxStreamReader = paramXMLStreamReader;
  }
  
  public void bridge() throws XMLStreamException {
    try {
      byte b = 0;
      int i = this.staxStreamReader.getEventType();
      if (i == 7)
        while (!this.staxStreamReader.isStartElement())
          i = this.staxStreamReader.next();  
      if (i != 1)
        throw new IllegalStateException("The current event is not START_ELEMENT\n but " + i); 
      handleStartDocument(this.staxStreamReader.getNamespaceContext());
      while (true) {
        switch (i) {
          case 1:
            handleStartElement();
            b++;
            break;
          case 2:
            b--;
            handleEndElement();
            if (b == 0)
              break; 
            break;
          case 4:
          case 6:
          case 12:
            handleCharacters();
            break;
        } 
        i = this.staxStreamReader.next();
      } 
      this.staxStreamReader.next();
      handleEndDocument();
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  protected Location getCurrentLocation() { return this.staxStreamReader.getLocation(); }
  
  protected String getCurrentQName() { return getQName(this.staxStreamReader.getPrefix(), this.staxStreamReader.getLocalName()); }
  
  private void handleEndElement() throws XMLStreamException {
    processText(false);
    this.tagName.uri = fixNull(this.staxStreamReader.getNamespaceURI());
    this.tagName.local = this.staxStreamReader.getLocalName();
    this.visitor.endElement(this.tagName);
    int i = this.staxStreamReader.getNamespaceCount();
    for (int j = i - 1; j >= 0; j--)
      this.visitor.endPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(j))); 
  }
  
  private void handleStartElement() throws XMLStreamException {
    processText(true);
    int i = this.staxStreamReader.getNamespaceCount();
    for (byte b = 0; b < i; b++)
      this.visitor.startPrefixMapping(fixNull(this.staxStreamReader.getNamespacePrefix(b)), fixNull(this.staxStreamReader.getNamespaceURI(b))); 
    this.tagName.uri = fixNull(this.staxStreamReader.getNamespaceURI());
    this.tagName.local = this.staxStreamReader.getLocalName();
    this.tagName.atts = this.attributes;
    this.visitor.startElement(this.tagName);
  }
  
  protected void handleCharacters() throws XMLStreamException {
    if (this.predictor.expectText())
      this.buffer.append(this.staxStreamReader.getTextCharacters(), this.staxStreamReader.getTextStart(), this.staxStreamReader.getTextLength()); 
  }
  
  private void processText(boolean paramBoolean) throws SAXException {
    if (this.predictor.expectText() && (!paramBoolean || !WhiteSpaceProcessor.isWhiteSpace(this.buffer) || this.context.getCurrentState().isMixed()))
      if (this.textReported) {
        this.textReported = false;
      } else {
        this.visitor.text(this.buffer);
      }  
    this.buffer.setLength(0);
  }
  
  private static Class initFIStAXReaderClass() {
    try {
      Class clazz1;
      Class clazz2 = (clazz1 = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader")).forName("com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser");
      return clazz1.isAssignableFrom(clazz2) ? clazz2 : null;
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static Constructor<? extends StAXConnector> initFastInfosetConnectorClass() {
    try {
      if (FI_STAX_READER_CLASS == null)
        return null; 
      Class clazz = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.FastInfosetConnector");
      return clazz.getConstructor(new Class[] { FI_STAX_READER_CLASS, XmlVisitor.class });
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static Class initStAXExReader() {
    try {
      return Class.forName("com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx");
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  private static Constructor<? extends StAXConnector> initStAXExConnector() {
    try {
      Class clazz = Class.forName("com.sun.xml.internal.bind.v2.runtime.unmarshaller.StAXExConnector");
      return clazz.getConstructor(new Class[] { STAX_EX_READER_CLASS, XmlVisitor.class });
    } catch (Throwable throwable) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\StAXStreamConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */