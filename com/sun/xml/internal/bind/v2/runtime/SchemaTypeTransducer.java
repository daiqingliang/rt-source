package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class SchemaTypeTransducer<V> extends FilterTransducer<V> {
  private final QName schemaType;
  
  public SchemaTypeTransducer(Transducer<V> paramTransducer, QName paramQName) {
    super(paramTransducer);
    this.schemaType = paramQName;
  }
  
  public CharSequence print(V paramV) throws AccessorException {
    xMLSerializer = XMLSerializer.getInstance();
    qName = xMLSerializer.setSchemaType(this.schemaType);
    try {
      return this.core.print(paramV);
    } finally {
      xMLSerializer.setSchemaType(qName);
    } 
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException {
    qName = paramXMLSerializer.setSchemaType(this.schemaType);
    try {
      this.core.writeText(paramXMLSerializer, paramV, paramString);
    } finally {
      paramXMLSerializer.setSchemaType(qName);
    } 
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException {
    qName = paramXMLSerializer.setSchemaType(this.schemaType);
    try {
      this.core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    } finally {
      paramXMLSerializer.setSchemaType(qName);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\SchemaTypeTransducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */