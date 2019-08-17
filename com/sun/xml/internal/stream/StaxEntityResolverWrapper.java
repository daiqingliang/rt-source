package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StaxEntityResolverWrapper {
  XMLResolver fStaxResolver;
  
  public StaxEntityResolverWrapper(XMLResolver paramXMLResolver) { this.fStaxResolver = paramXMLResolver; }
  
  public void setStaxEntityResolver(XMLResolver paramXMLResolver) { this.fStaxResolver = paramXMLResolver; }
  
  public XMLResolver getStaxEntityResolver() { return this.fStaxResolver; }
  
  public StaxXMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier) throws XNIException, IOException {
    Object object = null;
    try {
      object = this.fStaxResolver.resolveEntity(paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId(), null);
      return getStaxInputSource(object);
    } catch (XMLStreamException xMLStreamException) {
      throw new XNIException(xMLStreamException);
    } 
  }
  
  StaxXMLInputSource getStaxInputSource(Object paramObject) { return (paramObject == null) ? null : ((paramObject instanceof InputStream) ? new StaxXMLInputSource(new XMLInputSource(null, null, null, (InputStream)paramObject, null)) : ((paramObject instanceof XMLStreamReader) ? new StaxXMLInputSource((XMLStreamReader)paramObject) : ((paramObject instanceof XMLEventReader) ? new StaxXMLInputSource((XMLEventReader)paramObject) : null))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\StaxEntityResolverWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */