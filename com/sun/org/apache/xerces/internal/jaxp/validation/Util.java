package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class Util {
  public static final XMLInputSource toXMLInputSource(StreamSource paramStreamSource) { return (paramStreamSource.getReader() != null) ? new XMLInputSource(paramStreamSource.getPublicId(), paramStreamSource.getSystemId(), paramStreamSource.getSystemId(), paramStreamSource.getReader(), null) : ((paramStreamSource.getInputStream() != null) ? new XMLInputSource(paramStreamSource.getPublicId(), paramStreamSource.getSystemId(), paramStreamSource.getSystemId(), paramStreamSource.getInputStream(), null) : new XMLInputSource(paramStreamSource.getPublicId(), paramStreamSource.getSystemId(), paramStreamSource.getSystemId())); }
  
  public static SAXException toSAXException(XNIException paramXNIException) { return (paramXNIException instanceof XMLParseException) ? toSAXParseException((XMLParseException)paramXNIException) : ((paramXNIException.getException() instanceof SAXException) ? (SAXException)paramXNIException.getException() : new SAXException(paramXNIException.getMessage(), paramXNIException.getException())); }
  
  public static SAXParseException toSAXParseException(XMLParseException paramXMLParseException) { return (paramXMLParseException.getException() instanceof SAXParseException) ? (SAXParseException)paramXMLParseException.getException() : new SAXParseException(paramXMLParseException.getMessage(), paramXMLParseException.getPublicId(), paramXMLParseException.getExpandedSystemId(), paramXMLParseException.getLineNumber(), paramXMLParseException.getColumnNumber(), paramXMLParseException.getException()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */