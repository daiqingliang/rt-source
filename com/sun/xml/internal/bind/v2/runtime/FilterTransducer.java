package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class FilterTransducer<T> extends Object implements Transducer<T> {
  protected final Transducer<T> core;
  
  protected FilterTransducer(Transducer<T> paramTransducer) { this.core = paramTransducer; }
  
  public final boolean isDefault() { return false; }
  
  public boolean useNamespace() { return this.core.useNamespace(); }
  
  public void declareNamespace(T paramT, XMLSerializer paramXMLSerializer) throws AccessorException { this.core.declareNamespace(paramT, paramXMLSerializer); }
  
  @NotNull
  public CharSequence print(@NotNull T paramT) throws AccessorException { return this.core.print(paramT); }
  
  public T parse(CharSequence paramCharSequence) throws AccessorException, SAXException { return (T)this.core.parse(paramCharSequence); }
  
  public void writeText(XMLSerializer paramXMLSerializer, T paramT, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException { this.core.writeText(paramXMLSerializer, paramT, paramString); }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, T paramT, String paramString) throws IOException, SAXException, XMLStreamException, AccessorException { this.core.writeLeafElement(paramXMLSerializer, paramName, paramT, paramString); }
  
  public QName getTypeName(T paramT) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\FilterTransducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */