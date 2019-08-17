package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EntityResolverWrapper implements XMLEntityResolver {
  protected EntityResolver fEntityResolver;
  
  public EntityResolverWrapper() {}
  
  public EntityResolverWrapper(EntityResolver paramEntityResolver) { setEntityResolver(paramEntityResolver); }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) { this.fEntityResolver = paramEntityResolver; }
  
  public EntityResolver getEntityResolver() { return this.fEntityResolver; }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier) throws XNIException, IOException {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getExpandedSystemId();
    if (str1 == null && str2 == null)
      return null; 
    if (this.fEntityResolver != null && paramXMLResourceIdentifier != null)
      try {
        InputSource inputSource = this.fEntityResolver.resolveEntity(str1, str2);
        if (inputSource != null) {
          String str3 = inputSource.getPublicId();
          String str4 = inputSource.getSystemId();
          String str5 = paramXMLResourceIdentifier.getBaseSystemId();
          InputStream inputStream = inputSource.getByteStream();
          Reader reader = inputSource.getCharacterStream();
          String str6 = inputSource.getEncoding();
          XMLInputSource xMLInputSource = new XMLInputSource(str3, str4, str5);
          xMLInputSource.setByteStream(inputStream);
          xMLInputSource.setCharacterStream(reader);
          xMLInputSource.setEncoding(str6);
          return xMLInputSource;
        } 
      } catch (SAXException sAXException) {
        Exception exception = sAXException.getException();
        if (exception == null)
          exception = sAXException; 
        throw new XNIException(exception);
      }  
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\EntityResolverWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */