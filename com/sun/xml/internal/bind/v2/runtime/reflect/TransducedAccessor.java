package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXException2;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class TransducedAccessor<BeanT> extends Object {
  public boolean useNamespace() { return false; }
  
  public void declareNamespace(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws AccessorException, SAXException {}
  
  @Nullable
  public abstract CharSequence print(@NotNull BeanT paramBeanT) throws AccessorException, SAXException;
  
  public abstract void parse(BeanT paramBeanT, CharSequence paramCharSequence) throws AccessorException, SAXException;
  
  public abstract boolean hasValue(BeanT paramBeanT) throws AccessorException;
  
  public static <T> TransducedAccessor<T> get(JAXBContextImpl paramJAXBContextImpl, RuntimeNonElementRef paramRuntimeNonElementRef) {
    Transducer transducer = RuntimeModelBuilder.createTransducer(paramRuntimeNonElementRef);
    RuntimePropertyInfo runtimePropertyInfo = paramRuntimeNonElementRef.getSource();
    if (runtimePropertyInfo.isCollection())
      return new ListTransducedAccessorImpl(transducer, runtimePropertyInfo.getAccessor(), Lister.create((Type)Utils.REFLECTION_NAVIGATOR.erasure(runtimePropertyInfo.getRawType()), runtimePropertyInfo.id(), runtimePropertyInfo.getAdapter())); 
    if (runtimePropertyInfo.id() == ID.IDREF)
      return new IDREFTransducedAccessorImpl(runtimePropertyInfo.getAccessor()); 
    if (transducer.isDefault() && paramJAXBContextImpl != null && !paramJAXBContextImpl.fastBoot) {
      TransducedAccessor transducedAccessor = OptimizedTransducedAccessorFactory.get(runtimePropertyInfo);
      if (transducedAccessor != null)
        return transducedAccessor; 
    } 
    return transducer.useNamespace() ? new CompositeContextDependentTransducedAccessorImpl(paramJAXBContextImpl, transducer, runtimePropertyInfo.getAccessor()) : new CompositeTransducedAccessorImpl(paramJAXBContextImpl, transducer, runtimePropertyInfo.getAccessor());
  }
  
  public abstract void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, BeanT paramBeanT, String paramString) throws SAXException, AccessorException, IOException, XMLStreamException;
  
  public abstract void writeText(XMLSerializer paramXMLSerializer, BeanT paramBeanT, String paramString) throws AccessorException, SAXException, IOException, XMLStreamException;
  
  static class CompositeContextDependentTransducedAccessorImpl<BeanT, ValueT> extends CompositeTransducedAccessorImpl<BeanT, ValueT> {
    public CompositeContextDependentTransducedAccessorImpl(JAXBContextImpl param1JAXBContextImpl, Transducer<ValueT> param1Transducer, Accessor<BeanT, ValueT> param1Accessor) {
      super(param1JAXBContextImpl, param1Transducer, param1Accessor);
      assert param1Transducer.useNamespace();
    }
    
    public boolean useNamespace() { return true; }
    
    public void declareNamespace(BeanT param1BeanT, XMLSerializer param1XMLSerializer) throws AccessorException, SAXException {
      Object object = this.acc.get(param1BeanT);
      if (object != null)
        this.xducer.declareNamespace(object, param1XMLSerializer); 
    }
    
    public void writeLeafElement(XMLSerializer param1XMLSerializer, Name param1Name, BeanT param1BeanT, String param1String) throws SAXException, AccessorException, IOException, XMLStreamException {
      param1XMLSerializer.startElement(param1Name, null);
      declareNamespace(param1BeanT, param1XMLSerializer);
      param1XMLSerializer.endNamespaceDecls(null);
      param1XMLSerializer.endAttributes();
      this.xducer.writeText(param1XMLSerializer, this.acc.get(param1BeanT), param1String);
      param1XMLSerializer.endElement();
    }
  }
  
  public static class CompositeTransducedAccessorImpl<BeanT, ValueT> extends TransducedAccessor<BeanT> {
    protected final Transducer<ValueT> xducer;
    
    protected final Accessor<BeanT, ValueT> acc;
    
    public CompositeTransducedAccessorImpl(JAXBContextImpl param1JAXBContextImpl, Transducer<ValueT> param1Transducer, Accessor<BeanT, ValueT> param1Accessor) {
      this.xducer = param1Transducer;
      this.acc = param1Accessor.optimize(param1JAXBContextImpl);
    }
    
    public CharSequence print(BeanT param1BeanT) throws AccessorException, SAXException {
      Object object = this.acc.get(param1BeanT);
      return (object == null) ? null : this.xducer.print(object);
    }
    
    public void parse(BeanT param1BeanT, CharSequence param1CharSequence) throws AccessorException, SAXException { this.acc.set(param1BeanT, this.xducer.parse(param1CharSequence)); }
    
    public boolean hasValue(BeanT param1BeanT) throws AccessorException { return (this.acc.getUnadapted(param1BeanT) != null); }
    
    public void writeLeafElement(XMLSerializer param1XMLSerializer, Name param1Name, BeanT param1BeanT, String param1String) throws SAXException, AccessorException, IOException, XMLStreamException { this.xducer.writeLeafElement(param1XMLSerializer, param1Name, this.acc.get(param1BeanT), param1String); }
    
    public void writeText(XMLSerializer param1XMLSerializer, BeanT param1BeanT, String param1String) throws AccessorException, SAXException, IOException, XMLStreamException { this.xducer.writeText(param1XMLSerializer, this.acc.get(param1BeanT), param1String); }
  }
  
  private static final class IDREFTransducedAccessorImpl<BeanT, TargetT> extends DefaultTransducedAccessor<BeanT> {
    private final Accessor<BeanT, TargetT> acc;
    
    private final Class<TargetT> targetType;
    
    public IDREFTransducedAccessorImpl(Accessor<BeanT, TargetT> param1Accessor) {
      this.acc = param1Accessor;
      this.targetType = param1Accessor.getValueType();
    }
    
    public String print(BeanT param1BeanT) throws AccessorException, SAXException {
      Object object = this.acc.get(param1BeanT);
      if (object == null)
        return null; 
      XMLSerializer xMLSerializer = XMLSerializer.getInstance();
      try {
        String str = xMLSerializer.grammar.getBeanInfo(object, true).getId(object, xMLSerializer);
        if (str == null)
          xMLSerializer.errorMissingId(object); 
        return str;
      } catch (JAXBException jAXBException) {
        xMLSerializer.reportError(null, jAXBException);
        return null;
      } 
    }
    
    private void assign(BeanT param1BeanT, TargetT param1TargetT, UnmarshallingContext param1UnmarshallingContext) throws AccessorException {
      if (!this.targetType.isInstance(param1TargetT)) {
        param1UnmarshallingContext.handleError(Messages.UNASSIGNABLE_TYPE.format(new Object[] { this.targetType, param1TargetT.getClass() }));
      } else {
        this.acc.set(param1BeanT, param1TargetT);
      } 
    }
    
    public void parse(final BeanT bean, CharSequence param1CharSequence) throws AccessorException, SAXException {
      Object object;
      final String idref = WhiteSpaceProcessor.trim(param1CharSequence).toString();
      final UnmarshallingContext context = UnmarshallingContext.getInstance();
      final Callable callable = unmarshallingContext.getObjectFromId(str, this.acc.valueType);
      if (callable == null) {
        unmarshallingContext.errorUnresolvedIDREF(param1BeanT, str, unmarshallingContext.getLocator());
        return;
      } 
      try {
        object = callable.call();
      } catch (SAXException sAXException) {
        throw sAXException;
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new SAXException2(exception);
      } 
      if (object != null) {
        assign(param1BeanT, object, unmarshallingContext);
      } else {
        final LocatorEx.Snapshot loc = new LocatorEx.Snapshot(unmarshallingContext.getLocator());
        unmarshallingContext.addPatcher(new Patcher() {
              public void run() {
                try {
                  Object object = callable.call();
                  if (object == null) {
                    context.errorUnresolvedIDREF(bean, idref, loc);
                  } else {
                    TransducedAccessor.IDREFTransducedAccessorImpl.this.assign(bean, object, context);
                  } 
                } catch (AccessorException accessorException) {
                  context.handleError(accessorException);
                } catch (SAXException sAXException) {
                  throw sAXException;
                } catch (RuntimeException runtimeException) {
                  throw runtimeException;
                } catch (Exception exception) {
                  throw new SAXException2(exception);
                } 
              }
            });
      } 
    }
    
    public boolean hasValue(BeanT param1BeanT) throws AccessorException { return (this.acc.get(param1BeanT) != null); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\TransducedAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */