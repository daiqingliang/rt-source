package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class InlineBinaryTransducer<V> extends FilterTransducer<V> {
  public InlineBinaryTransducer(Transducer<V> paramTransducer) { super(paramTransducer); }
  
  @NotNull
  public CharSequence print(@NotNull V paramV) throws AccessorException {
    xMLSerializer = XMLSerializer.getInstance();
    bool = xMLSerializer.setInlineBinaryFlag(true);
    try {
      return this.core.print(paramV);
    } finally {
      xMLSerializer.setInlineBinaryFlag(bool);
    } 
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException {
    bool = paramXMLSerializer.setInlineBinaryFlag(true);
    try {
      this.core.writeText(paramXMLSerializer, paramV, paramString);
    } finally {
      paramXMLSerializer.setInlineBinaryFlag(bool);
    } 
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException {
    bool = paramXMLSerializer.setInlineBinaryFlag(true);
    try {
      this.core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    } finally {
      paramXMLSerializer.setInlineBinaryFlag(bool);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\InlineBinaryTransducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */