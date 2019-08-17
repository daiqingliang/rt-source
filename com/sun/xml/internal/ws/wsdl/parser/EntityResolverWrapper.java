package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

final class EntityResolverWrapper implements XMLEntityResolver {
  private final EntityResolver core;
  
  private boolean useStreamFromEntityResolver = false;
  
  public EntityResolverWrapper(EntityResolver paramEntityResolver) { this.core = paramEntityResolver; }
  
  public EntityResolverWrapper(EntityResolver paramEntityResolver, boolean paramBoolean) {
    this.core = paramEntityResolver;
    this.useStreamFromEntityResolver = paramBoolean;
  }
  
  public XMLEntityResolver.Parser resolveEntity(String paramString1, String paramString2) throws SAXException, IOException {
    InputStream inputStream;
    InputSource inputSource = this.core.resolveEntity(paramString1, paramString2);
    if (inputSource == null)
      return null; 
    if (inputSource.getSystemId() != null)
      paramString2 = inputSource.getSystemId(); 
    URL uRL = new URL(paramString2);
    if (this.useStreamFromEntityResolver) {
      inputStream = inputSource.getByteStream();
    } else {
      inputStream = uRL.openStream();
    } 
    return new XMLEntityResolver.Parser(uRL, new TidyXMLStreamReader(XMLStreamReaderFactory.create(uRL.toExternalForm(), inputStream, true), inputStream));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\EntityResolverWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */