package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public final class MexEntityResolver implements XMLEntityResolver {
  private final Map<String, SDDocumentSource> wsdls = new HashMap();
  
  public MexEntityResolver(List<? extends Source> paramList) throws IOException {
    Transformer transformer = XmlUtil.newTransformer();
    for (Source source : paramList) {
      XMLStreamBufferResult xMLStreamBufferResult = new XMLStreamBufferResult();
      try {
        transformer.transform(source, xMLStreamBufferResult);
      } catch (TransformerException transformerException) {
        throw new WebServiceException(transformerException);
      } 
      String str = source.getSystemId();
      if (str != null) {
        SDDocumentSource sDDocumentSource = SDDocumentSource.create(JAXWSUtils.getFileOrURL(str), xMLStreamBufferResult.getXMLStreamBuffer());
        this.wsdls.put(str, sDDocumentSource);
      } 
    } 
  }
  
  public XMLEntityResolver.Parser resolveEntity(String paramString1, String paramString2) throws SAXException, IOException, XMLStreamException {
    if (paramString2 != null) {
      SDDocumentSource sDDocumentSource = (SDDocumentSource)this.wsdls.get(paramString2);
      if (sDDocumentSource != null)
        return new XMLEntityResolver.Parser(sDDocumentSource); 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\MexEntityResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */