package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class DefaultTransducedAccessor<T> extends TransducedAccessor<T> {
  public abstract String print(T paramT) throws AccessorException, SAXException;
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, T paramT, String paramString) throws SAXException, AccessorException, IOException, XMLStreamException { paramXMLSerializer.leafElement(paramName, print(paramT), paramString); }
  
  public void writeText(XMLSerializer paramXMLSerializer, T paramT, String paramString) throws AccessorException, SAXException, IOException, XMLStreamException { paramXMLSerializer.text(print(paramT), paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\DefaultTransducedAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */