package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.io.IOException;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class ArrayERProperty<BeanT, ListT, ItemT> extends ArrayProperty<BeanT, ListT, ItemT> {
  protected final Name wrapperTagName;
  
  protected final boolean isWrapperNillable;
  
  protected ArrayERProperty(JAXBContextImpl paramJAXBContextImpl, RuntimePropertyInfo paramRuntimePropertyInfo, QName paramQName, boolean paramBoolean) {
    super(paramJAXBContextImpl, paramRuntimePropertyInfo);
    if (paramQName == null) {
      this.wrapperTagName = null;
    } else {
      this.wrapperTagName = paramJAXBContextImpl.nameBuilder.createElementName(paramQName);
    } 
    this.isWrapperNillable = paramBoolean;
  }
  
  public final void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, Object paramObject) throws SAXException, AccessorException, IOException, XMLStreamException {
    Object object = this.acc.get(paramBeanT);
    if (object != null) {
      if (this.wrapperTagName != null) {
        paramXMLSerializer.startElement(this.wrapperTagName, null);
        paramXMLSerializer.endNamespaceDecls(object);
        paramXMLSerializer.endAttributes();
      } 
      serializeListBody(paramBeanT, paramXMLSerializer, object);
      if (this.wrapperTagName != null)
        paramXMLSerializer.endElement(); 
    } else if (this.isWrapperNillable) {
      paramXMLSerializer.startElement(this.wrapperTagName, null);
      paramXMLSerializer.writeXsiNilTrue();
      paramXMLSerializer.endElement();
    } 
  }
  
  protected abstract void serializeListBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer, ListT paramListT) throws IOException, XMLStreamException, SAXException, AccessorException;
  
  protected abstract void createBodyUnmarshaller(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap);
  
  public final void buildChildElementUnmarshallers(UnmarshallerChain paramUnmarshallerChain, QNameMap<ChildLoader> paramQNameMap) {
    if (this.wrapperTagName != null) {
      UnmarshallerChain unmarshallerChain = new UnmarshallerChain(paramUnmarshallerChain.context);
      QNameMap qNameMap = new QNameMap();
      createBodyUnmarshaller(unmarshallerChain, qNameMap);
      XsiNilLoader xsiNilLoader = new ItemsLoader(this.acc, this.lister, qNameMap);
      if (this.isWrapperNillable || paramUnmarshallerChain.context.allNillable)
        xsiNilLoader = new XsiNilLoader(xsiNilLoader); 
      paramQNameMap.put(this.wrapperTagName, new ChildLoader(xsiNilLoader, null));
    } else {
      createBodyUnmarshaller(paramUnmarshallerChain, paramQNameMap);
    } 
  }
  
  private static final class ItemsLoader extends Loader {
    private final Accessor acc;
    
    private final Lister lister;
    
    private final QNameMap<ChildLoader> children;
    
    public ItemsLoader(Accessor param1Accessor, Lister param1Lister, QNameMap<ChildLoader> param1QNameMap) {
      super(false);
      this.acc = param1Accessor;
      this.lister = param1Lister;
      this.children = param1QNameMap;
    }
    
    public void startElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
      UnmarshallingContext unmarshallingContext = param1State.getContext();
      unmarshallingContext.startScope(1);
      param1State.setTarget(param1State.getPrev().getTarget());
      unmarshallingContext.getScope(0).start(this.acc, this.lister);
    }
    
    public void childElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
      ChildLoader childLoader = (ChildLoader)this.children.get(param1TagName.uri, param1TagName.local);
      if (childLoader == null)
        childLoader = (ChildLoader)this.children.get(StructureLoaderBuilder.CATCH_ALL); 
      if (childLoader == null) {
        super.childElement(param1State, param1TagName);
        return;
      } 
      param1State.setLoader(childLoader.loader);
      param1State.setReceiver(childLoader.receiver);
    }
    
    public void leaveElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException { param1State.getContext().endScope(1); }
    
    public Collection<QName> getExpectedChildElements() { return this.children.keySet(); }
  }
  
  protected final class ReceiverImpl implements Receiver {
    private final int offset;
    
    protected ReceiverImpl(int param1Int) { this.offset = param1Int; }
    
    public void receive(UnmarshallingContext.State param1State, Object param1Object) throws SAXException { param1State.getContext().getScope(this.offset).add(ArrayERProperty.this.acc, ArrayERProperty.this.lister, param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\ArrayERProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */