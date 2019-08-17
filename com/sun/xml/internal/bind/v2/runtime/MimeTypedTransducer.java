package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.activation.MimeType;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class MimeTypedTransducer<V> extends FilterTransducer<V> {
  private final MimeType expectedMimeType;
  
  public MimeTypedTransducer(Transducer<V> paramTransducer, MimeType paramMimeType) {
    super(paramTransducer);
    this.expectedMimeType = paramMimeType;
  }
  
  public CharSequence print(V paramV) throws AccessorException {
    xMLSerializer = XMLSerializer.getInstance();
    mimeType = xMLSerializer.setExpectedMimeType(this.expectedMimeType);
    try {
      return this.core.print(paramV);
    } finally {
      xMLSerializer.setExpectedMimeType(mimeType);
    } 
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException {
    mimeType = paramXMLSerializer.setExpectedMimeType(this.expectedMimeType);
    try {
      this.core.writeText(paramXMLSerializer, paramV, paramString);
    } finally {
      paramXMLSerializer.setExpectedMimeType(mimeType);
    } 
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException {
    mimeType = paramXMLSerializer.setExpectedMimeType(this.expectedMimeType);
    try {
      this.core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    } finally {
      paramXMLSerializer.setExpectedMimeType(mimeType);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\MimeTypedTransducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */