package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeMapPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleMapNodeProperty<BeanT, ValueT extends Map> extends PropertyImpl<BeanT> {
  private final Accessor<BeanT, ValueT> acc;
  
  private final Name tagName;
  
  private final Name entryTag;
  
  private final Name keyTag;
  
  private final Name valueTag;
  
  private final boolean nillable;
  
  private JaxBeanInfo keyBeanInfo;
  
  private JaxBeanInfo valueBeanInfo;
  
  private final Class<? extends ValueT> mapImplClass;
  
  private static final Class[] knownImplClasses = { HashMap.class, java.util.TreeMap.class, java.util.LinkedHashMap.class };
  
  private Loader keyLoader;
  
  private Loader valueLoader;
  
  private final Loader itemsLoader = new Loader(false) {
      private ThreadLocal<BeanT> target = new ThreadLocal();
      
      private ThreadLocal<ValueT> map = new ThreadLocal();
      
      private int depthCounter = 0;
      
      public void startElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
        try {
          this.target.set(param1State.getPrev().getTarget());
          this.map.set(SingleMapNodeProperty.this.acc.get(this.target.get()));
          this.depthCounter++;
          if (this.map.get() == null)
            this.map.set(ClassFactory.create(SingleMapNodeProperty.this.mapImplClass)); 
          ((Map)this.map.get()).clear();
          param1State.setTarget(this.map.get());
        } catch (AccessorException accessorException) {
          SingleMapNodeProperty.null.handleGenericException(accessorException, true);
          param1State.setTarget(new HashMap());
        } 
      }
      
      public void leaveElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
        super.leaveElement(param1State, param1TagName);
        try {
          SingleMapNodeProperty.this.acc.set(this.target.get(), this.map.get());
          if (--this.depthCounter == 0) {
            this.target.remove();
            this.map.remove();
          } 
        } catch (AccessorException accessorException) {
          SingleMapNodeProperty.null.handleGenericException(accessorException, true);
        } 
      }
      
      public void childElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
        if (param1TagName.matches(SingleMapNodeProperty.this.entryTag)) {
          param1State.setLoader(SingleMapNodeProperty.this.entryLoader);
        } else {
          super.childElement(param1State, param1TagName);
        } 
      }
      
      public Collection<QName> getExpectedChildElements() { return Collections.singleton(SingleMapNodeProperty.this.entryTag.toQName()); }
    };
  
  private final Loader entryLoader = new Loader(false) {
      public void startElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException { param1State.setTarget(new Object[2]); }
      
      public void leaveElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
        Object[] arrayOfObject = (Object[])param1State.getTarget();
        Map map = (Map)param1State.getPrev().getTarget();
        map.put(arrayOfObject[0], arrayOfObject[1]);
      }
      
      public void childElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
        if (param1TagName.matches(SingleMapNodeProperty.this.keyTag)) {
          param1State.setLoader(SingleMapNodeProperty.this.keyLoader);
          param1State.setReceiver(keyReceiver);
          return;
        } 
        if (param1TagName.matches(SingleMapNodeProperty.this.valueTag)) {
          param1State.setLoader(SingleMapNodeProperty.this.valueLoader);
          param1State.setReceiver(valueReceiver);
          return;
        } 
        super.childElement(param1State, param1TagName);
      }
      
      public Collection<QName> getExpectedChildElements() { return Arrays.asList(new QName[] { SingleMapNodeProperty.access$400(SingleMapNodeProperty.this).toQName(), SingleMapNodeProperty.access$700(SingleMapNodeProperty.this).toQName() }); }
    };
  
  private static final Receiver keyReceiver = new ReceiverImpl(0);
  
  private static final Receiver valueReceiver = new ReceiverImpl(1);
  
  public SingleMapNodeProperty(JAXBContextImpl paramJAXBContextImpl, RuntimeMapPropertyInfo paramRuntimeMapPropertyInfo) {
    super(paramJAXBContextImpl, paramRuntimeMapPropertyInfo);
    this.acc = paramRuntimeMapPropertyInfo.getAccessor().optimize(paramJAXBContextImpl);
    this.tagName = paramJAXBContextImpl.nameBuilder.createElementName(paramRuntimeMapPropertyInfo.getXmlName());
    this.entryTag = paramJAXBContextImpl.nameBuilder.createElementName("", "entry");
    this.keyTag = paramJAXBContextImpl.nameBuilder.createElementName("", "key");
    this.valueTag = paramJAXBContextImpl.nameBuilder.createElementName("", "value");
    this.nillable = paramRuntimeMapPropertyInfo.isCollectionNillable();
    this.keyBeanInfo = paramJAXBContextImpl.getOrCreate(paramRuntimeMapPropertyInfo.getKeyType());
    this.valueBeanInfo = paramJAXBContextImpl.getOrCreate(paramRuntimeMapPropertyInfo.getValueType());
    Class clazz = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramRuntimeMapPropertyInfo.getRawType());
    this.mapImplClass = ClassFactory.inferImplClass(clazz, knownImplClasses);
  }
  
  public void reset(BeanT paramBeanT) throws AccessorException { this.acc.set(paramBeanT, null); }
  
  public String getIdValue(BeanT paramBeanT) { return null; }
  
  public PropertyKind getKind() { return PropertyKind.MAP; }
  
  public void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    this.keyLoader = this.keyBeanInfo.getLoader(paramUnmarshallerChain.context, true);
    this.valueLoader = this.valueBeanInfo.getLoader(paramUnmarshallerChain.context, true);
    paramQNameMap.put(this.tagName, new ChildLoader(this.itemsLoader, null));
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    Map map = (Map)this.acc.get(paramBeanT);
    if (map != null) {
      bareStartTag(paramXMLSerializer, this.tagName, map);
      for (Map.Entry entry : map.entrySet()) {
        bareStartTag(paramXMLSerializer, this.entryTag, null);
        Object object1 = entry.getKey();
        if (object1 != null) {
          paramXMLSerializer.startElement(this.keyTag, object1);
          paramXMLSerializer.childAsXsiType(object1, this.fieldName, this.keyBeanInfo, false);
          paramXMLSerializer.endElement();
        } 
        Object object2 = entry.getValue();
        if (object2 != null) {
          paramXMLSerializer.startElement(this.valueTag, object2);
          paramXMLSerializer.childAsXsiType(object2, this.fieldName, this.valueBeanInfo, false);
          paramXMLSerializer.endElement();
        } 
        paramXMLSerializer.endElement();
      } 
      paramXMLSerializer.endElement();
    } else if (this.nillable) {
      paramXMLSerializer.startElement(this.tagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    } 
  }
  
  private void bareStartTag(XMLSerializer paramXMLSerializer, Name paramName, Object paramObject) throws IOException, XMLStreamException, SAXException {
    paramXMLSerializer.startElement(paramName, paramObject);
    paramXMLSerializer.endNamespaceDecls(paramObject);
    paramXMLSerializer.endAttributes();
  }
  
  public Accessor getElementPropertyAccessor(String paramString1, String paramString2) { return this.tagName.equals(paramString1, paramString2) ? this.acc : null; }
  
  private static final class ReceiverImpl implements Receiver {
    private final int index;
    
    public ReceiverImpl(int param1Int) { this.index = param1Int; }
    
    public void receive(UnmarshallingContext.State param1State, Object param1Object) { (Object[])param1State.getTarget()[this.index] = param1Object; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\SingleMapNodeProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */