package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ValueListBeanInfoImpl extends JaxBeanInfo {
  private final Class itemType = this.jaxbType.getComponentType();
  
  private final Transducer xducer;
  
  private final Loader loader = new Loader(true) {
      public void text(UnmarshallingContext.State param1State, CharSequence param1CharSequence) throws SAXException {
        FinalArrayList finalArrayList = new FinalArrayList();
        byte b = 0;
        int i = param1CharSequence.length();
        while (true) {
          byte b1;
          for (b1 = b; b1 < i && !WhiteSpaceProcessor.isWhiteSpace(param1CharSequence.charAt(b1)); b1++);
          CharSequence charSequence = param1CharSequence.subSequence(b, b1);
          if (!charSequence.equals(""))
            try {
              finalArrayList.add(ValueListBeanInfoImpl.this.xducer.parse(charSequence));
            } catch (AccessorException accessorException) {
              ValueListBeanInfoImpl.null.handleGenericException(accessorException, true);
              continue;
            }  
          if (b1 == i)
            break; 
          while (b1 < i && WhiteSpaceProcessor.isWhiteSpace(param1CharSequence.charAt(b1)))
            b1++; 
          if (b1 == i)
            break; 
          b = b1;
        } 
        param1State.setTarget(ValueListBeanInfoImpl.this.toArray(finalArrayList));
      }
    };
  
  public ValueListBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, Class paramClass) throws JAXBException {
    super(paramJAXBContextImpl, null, paramClass, false, true, false);
    this.xducer = paramJAXBContextImpl.getBeanInfo(paramClass.getComponentType(), true).getTransducer();
    assert this.xducer != null;
  }
  
  private Object toArray(List paramList) {
    int i = paramList.size();
    Object object = Array.newInstance(this.itemType, i);
    for (byte b = 0; b < i; b++)
      Array.set(object, b, paramList.get(b)); 
    return object;
  }
  
  public void serializeBody(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    int i = Array.getLength(paramObject);
    for (byte b = 0; b < i; b++) {
      Object object = Array.get(paramObject, b);
      try {
        this.xducer.writeText(paramXMLSerializer, object, "arrayItem");
      } catch (AccessorException accessorException) {
        paramXMLSerializer.reportError("arrayItem", accessorException);
      } 
    } 
  }
  
  public final void serializeURIs(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    if (this.xducer.useNamespace()) {
      int i = Array.getLength(paramObject);
      for (byte b = 0; b < i; b++) {
        Object object = Array.get(paramObject, b);
        try {
          this.xducer.declareNamespace(object, paramXMLSerializer);
        } catch (AccessorException accessorException) {
          paramXMLSerializer.reportError("arrayItem", accessorException);
        } 
      } 
    } 
  }
  
  public final String getElementNamespaceURI(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public final String getElementLocalName(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public final Object createInstance(UnmarshallingContext paramUnmarshallingContext) { throw new UnsupportedOperationException(); }
  
  public final boolean reset(Object paramObject, UnmarshallingContext paramUnmarshallingContext) { return false; }
  
  public final String getId(Object paramObject, XMLSerializer paramXMLSerializer) { return null; }
  
  public final void serializeAttributes(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {}
  
  public final void serializeRoot(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException { paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramObject.getClass().getName() }, ), null, null)); }
  
  public final Transducer getTransducer() { return null; }
  
  public final Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean) { return this.loader; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ValueListBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */