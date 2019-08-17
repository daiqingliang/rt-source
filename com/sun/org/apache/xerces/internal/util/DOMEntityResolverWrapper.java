package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMEntityResolverWrapper implements XMLEntityResolver {
  private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
  
  private static final String XSD_TYPE = "http://www.w3.org/2001/XMLSchema";
  
  protected LSResourceResolver fEntityResolver;
  
  public DOMEntityResolverWrapper() {}
  
  public DOMEntityResolverWrapper(LSResourceResolver paramLSResourceResolver) { setEntityResolver(paramLSResourceResolver); }
  
  public void setEntityResolver(LSResourceResolver paramLSResourceResolver) { this.fEntityResolver = paramLSResourceResolver; }
  
  public LSResourceResolver getEntityResolver() { return this.fEntityResolver; }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier) throws XNIException, IOException {
    if (this.fEntityResolver != null) {
      LSInput lSInput = (paramXMLResourceIdentifier == null) ? this.fEntityResolver.resolveResource(null, null, null, null, null) : this.fEntityResolver.resolveResource(getType(paramXMLResourceIdentifier), paramXMLResourceIdentifier.getNamespace(), paramXMLResourceIdentifier.getPublicId(), paramXMLResourceIdentifier.getLiteralSystemId(), paramXMLResourceIdentifier.getBaseSystemId());
      if (lSInput != null) {
        String str1 = lSInput.getPublicId();
        String str2 = lSInput.getSystemId();
        String str3 = lSInput.getBaseURI();
        InputStream inputStream = lSInput.getByteStream();
        Reader reader = lSInput.getCharacterStream();
        String str4 = lSInput.getEncoding();
        String str5 = lSInput.getStringData();
        XMLInputSource xMLInputSource = new XMLInputSource(str1, str2, str3);
        if (reader != null) {
          xMLInputSource.setCharacterStream(reader);
        } else if (inputStream != null) {
          xMLInputSource.setByteStream(inputStream);
        } else if (str5 != null && str5.length() != 0) {
          xMLInputSource.setCharacterStream(new StringReader(str5));
        } 
        xMLInputSource.setEncoding(str4);
        return xMLInputSource;
      } 
    } 
    return null;
  }
  
  private String getType(XMLResourceIdentifier paramXMLResourceIdentifier) {
    if (paramXMLResourceIdentifier instanceof XMLGrammarDescription) {
      XMLGrammarDescription xMLGrammarDescription = (XMLGrammarDescription)paramXMLResourceIdentifier;
      if ("http://www.w3.org/2001/XMLSchema".equals(xMLGrammarDescription.getGrammarType()))
        return "http://www.w3.org/2001/XMLSchema"; 
    } 
    return "http://www.w3.org/TR/REC-xml";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\DOMEntityResolverWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */