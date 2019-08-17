package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.ExternalSubsetResolver;
import com.sun.org.apache.xerces.internal.impl.XMLEntityDescription;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

public class EntityResolver2Wrapper implements ExternalSubsetResolver {
  protected EntityResolver2 fEntityResolver;
  
  public EntityResolver2Wrapper() {}
  
  public EntityResolver2Wrapper(EntityResolver2 paramEntityResolver2) { setEntityResolver(paramEntityResolver2); }
  
  public void setEntityResolver(EntityResolver2 paramEntityResolver2) { this.fEntityResolver = paramEntityResolver2; }
  
  public EntityResolver2 getEntityResolver() { return this.fEntityResolver; }
  
  public XMLInputSource getExternalSubset(XMLDTDDescription paramXMLDTDDescription) throws XNIException, IOException {
    if (this.fEntityResolver != null) {
      String str1 = paramXMLDTDDescription.getRootName();
      String str2 = paramXMLDTDDescription.getBaseSystemId();
      try {
        InputSource inputSource = this.fEntityResolver.getExternalSubset(str1, str2);
        return (inputSource != null) ? createXMLInputSource(inputSource, str2) : null;
      } catch (SAXException sAXException) {
        Exception exception = sAXException.getException();
        if (exception == null)
          exception = sAXException; 
        throw new XNIException(exception);
      } 
    } 
    return null;
  }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier paramXMLResourceIdentifier) throws XNIException, IOException {
    if (this.fEntityResolver != null) {
      String str1 = paramXMLResourceIdentifier.getPublicId();
      String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
      String str3 = paramXMLResourceIdentifier.getBaseSystemId();
      String str4 = null;
      if (paramXMLResourceIdentifier instanceof XMLDTDDescription) {
        str4 = "[dtd]";
      } else if (paramXMLResourceIdentifier instanceof XMLEntityDescription) {
        str4 = ((XMLEntityDescription)paramXMLResourceIdentifier).getEntityName();
      } 
      if (str1 == null && str2 == null)
        return null; 
      try {
        InputSource inputSource = this.fEntityResolver.resolveEntity(str4, str1, str3, str2);
        return (inputSource != null) ? createXMLInputSource(inputSource, str3) : null;
      } catch (SAXException sAXException) {
        Exception exception = sAXException.getException();
        if (exception == null)
          exception = sAXException; 
        throw new XNIException(exception);
      } 
    } 
    return null;
  }
  
  private XMLInputSource createXMLInputSource(InputSource paramInputSource, String paramString) {
    String str1 = paramInputSource.getPublicId();
    String str2 = paramInputSource.getSystemId();
    String str3 = paramString;
    InputStream inputStream = paramInputSource.getByteStream();
    Reader reader = paramInputSource.getCharacterStream();
    String str4 = paramInputSource.getEncoding();
    XMLInputSource xMLInputSource = new XMLInputSource(str1, str2, str3);
    xMLInputSource.setByteStream(inputStream);
    xMLInputSource.setCharacterStream(reader);
    xMLInputSource.setEncoding(str4);
    return xMLInputSource;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\EntityResolver2Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */