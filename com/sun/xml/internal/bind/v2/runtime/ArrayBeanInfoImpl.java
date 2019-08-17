package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class ArrayBeanInfoImpl extends JaxBeanInfo {
  private final Class itemType = this.jaxbType.getComponentType();
  
  private final JaxBeanInfo itemBeanInfo;
  
  private Loader loader;
  
  public ArrayBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeArrayInfo paramRuntimeArrayInfo) {
    super(paramJAXBContextImpl, paramRuntimeArrayInfo, paramRuntimeArrayInfo.getType(), paramRuntimeArrayInfo.getTypeName(), false, true, false);
    this.itemBeanInfo = paramJAXBContextImpl.getOrCreate(paramRuntimeArrayInfo.getItemType());
  }
  
  protected void link(JAXBContextImpl paramJAXBContextImpl) {
    getLoader(paramJAXBContextImpl, false);
    super.link(paramJAXBContextImpl);
  }
  
  protected Object toArray(List paramList) {
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
      paramXMLSerializer.startElement("", "item", null, null);
      if (object == null) {
        paramXMLSerializer.writeXsiNilTrue();
      } else {
        paramXMLSerializer.childAsXsiType(object, "arrayItem", this.itemBeanInfo, false);
      } 
      paramXMLSerializer.endElement();
    } 
  }
  
  public final String getElementNamespaceURI(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public final String getElementLocalName(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public final Object createInstance(UnmarshallingContext paramUnmarshallingContext) { return new ArrayList(); }
  
  public final boolean reset(Object paramObject, UnmarshallingContext paramUnmarshallingContext) { return false; }
  
  public final String getId(Object paramObject, XMLSerializer paramXMLSerializer) { return null; }
  
  public final void serializeAttributes(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {}
  
  public final void serializeRoot(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException { paramXMLSerializer.reportError(new ValidationEventImpl(1, Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { paramObject.getClass().getName() }, ), null, null)); }
  
  public final void serializeURIs(Object paramObject, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {}
  
  public final Transducer getTransducer() { return null; }
  
  public final Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean) {
    if (this.loader == null)
      this.loader = new ArrayLoader(paramJAXBContextImpl); 
    return this.loader;
  }
  
  private final class ArrayLoader extends Loader implements Receiver {
    private final Loader itemLoader;
    
    public ArrayLoader(JAXBContextImpl param1JAXBContextImpl) {
      super(false);
      this.itemLoader = this$0.itemBeanInfo.getLoader(param1JAXBContextImpl, true);
    }
    
    public void startElement(UnmarshallingContext.State param1State, TagName param1TagName) { param1State.setTarget(new ArrayList()); }
    
    public void leaveElement(UnmarshallingContext.State param1State, TagName param1TagName) { param1State.setTarget(ArrayBeanInfoImpl.this.toArray((List)param1State.getTarget())); }
    
    public void childElement(UnmarshallingContext.State param1State, TagName param1TagName) {
      if (param1TagName.matches("", "item")) {
        param1State.setLoader(this.itemLoader);
        param1State.setReceiver(this);
      } else {
        super.childElement(param1State, param1TagName);
      } 
    }
    
    public Collection<QName> getExpectedChildElements() { return Collections.singleton(new QName("", "item")); }
    
    public void receive(UnmarshallingContext.State param1State, Object param1Object) { ((List)param1State.getTarget()).add(param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ArrayBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */