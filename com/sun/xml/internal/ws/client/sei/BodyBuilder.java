package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

abstract class BodyBuilder {
  static final BodyBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
  
  static final BodyBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
  
  abstract Message createMessage(Object[] paramArrayOfObject);
  
  static final class Bare extends JAXB {
    private final int methodPos;
    
    private final ValueGetter getter;
    
    Bare(ParameterImpl param1ParameterImpl, SOAPVersion param1SOAPVersion, ValueGetter param1ValueGetter) {
      super(param1ParameterImpl.getXMLBridge(), param1SOAPVersion);
      this.methodPos = param1ParameterImpl.getIndex();
      this.getter = param1ValueGetter;
    }
    
    Object build(Object[] param1ArrayOfObject) { return this.getter.get(param1ArrayOfObject[this.methodPos]); }
  }
  
  static final class DocLit extends Wrapped {
    private final PropertyAccessor[] accessors;
    
    private final Class wrapper;
    
    private BindingContext bindingContext;
    
    private boolean dynamicWrapper;
    
    DocLit(WrapperParameter param1WrapperParameter, SOAPVersion param1SOAPVersion, ValueGetterFactory param1ValueGetterFactory) {
      super(param1WrapperParameter, param1SOAPVersion, param1ValueGetterFactory);
      this.bindingContext = param1WrapperParameter.getOwner().getBindingContext();
      this.wrapper = (Class)(param1WrapperParameter.getXMLBridge().getTypeInfo()).type;
      this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
      this.parameterBridges = new XMLBridge[this.children.size()];
      this.accessors = new PropertyAccessor[this.children.size()];
      for (byte b = 0; b < this.accessors.length; b++) {
        ParameterImpl parameterImpl = (ParameterImpl)this.children.get(b);
        QName qName = parameterImpl.getName();
        if (this.dynamicWrapper) {
          this.parameterBridges[b] = ((ParameterImpl)this.children.get(b)).getInlinedRepeatedElementBridge();
          if (this.parameterBridges[b] == null)
            this.parameterBridges[b] = ((ParameterImpl)this.children.get(b)).getXMLBridge(); 
        } else {
          try {
            this.accessors[b] = parameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(this.wrapper, qName.getNamespaceURI(), qName.getLocalPart());
          } catch (JAXBException jAXBException) {
            throw new WebServiceException(this.wrapper + " do not have a property of the name " + qName, jAXBException);
          } 
        } 
      } 
    }
    
    Object build(Object[] param1ArrayOfObject) {
      if (this.dynamicWrapper)
        return buildWrapperComposite(param1ArrayOfObject); 
      try {
        Object object = this.bindingContext.newWrapperInstace(this.wrapper);
        for (int i = this.indices.length - 1; i >= 0; i--)
          this.accessors[i].set(object, this.getters[i].get(param1ArrayOfObject[this.indices[i]])); 
        return object;
      } catch (InstantiationException instantiationException) {
        InstantiationError instantiationError = new InstantiationError(instantiationException.getMessage());
        instantiationError.initCause(instantiationException);
        throw instantiationError;
      } catch (IllegalAccessException illegalAccessException) {
        IllegalAccessError illegalAccessError = new IllegalAccessError(illegalAccessException.getMessage());
        illegalAccessError.initCause(illegalAccessException);
        throw illegalAccessError;
      } catch (DatabindingException databindingException) {
        throw new WebServiceException(databindingException);
      } 
    }
  }
  
  private static final class Empty extends BodyBuilder {
    private final SOAPVersion soapVersion;
    
    public Empty(SOAPVersion param1SOAPVersion) { this.soapVersion = param1SOAPVersion; }
    
    Message createMessage(Object[] param1ArrayOfObject) { return Messages.createEmpty(this.soapVersion); }
  }
  
  private static abstract class JAXB extends BodyBuilder {
    private final XMLBridge bridge;
    
    private final SOAPVersion soapVersion;
    
    protected JAXB(XMLBridge param1XMLBridge, SOAPVersion param1SOAPVersion) {
      assert param1XMLBridge != null;
      this.bridge = param1XMLBridge;
      this.soapVersion = param1SOAPVersion;
    }
    
    final Message createMessage(Object[] param1ArrayOfObject) { return JAXBMessage.create(this.bridge, build(param1ArrayOfObject), this.soapVersion); }
    
    abstract Object build(Object[] param1ArrayOfObject);
  }
  
  static final class RpcLit extends Wrapped {
    RpcLit(WrapperParameter param1WrapperParameter, SOAPVersion param1SOAPVersion, ValueGetterFactory param1ValueGetterFactory) {
      super(param1WrapperParameter, param1SOAPVersion, param1ValueGetterFactory);
      assert (param1WrapperParameter.getTypeInfo()).type == WrapperComposite.class;
      this.parameterBridges = new XMLBridge[this.children.size()];
      for (byte b = 0; b < this.parameterBridges.length; b++)
        this.parameterBridges[b] = ((ParameterImpl)this.children.get(b)).getXMLBridge(); 
    }
    
    Object build(Object[] param1ArrayOfObject) { return buildWrapperComposite(param1ArrayOfObject); }
  }
  
  static abstract class Wrapped extends JAXB {
    protected final int[] indices;
    
    protected final ValueGetter[] getters;
    
    protected XMLBridge[] parameterBridges;
    
    protected List<ParameterImpl> children;
    
    protected Wrapped(WrapperParameter param1WrapperParameter, SOAPVersion param1SOAPVersion, ValueGetterFactory param1ValueGetterFactory) {
      super(param1WrapperParameter.getXMLBridge(), param1SOAPVersion);
      this.children = param1WrapperParameter.getWrapperChildren();
      this.indices = new int[this.children.size()];
      this.getters = new ValueGetter[this.children.size()];
      for (byte b = 0; b < this.indices.length; b++) {
        ParameterImpl parameterImpl = (ParameterImpl)this.children.get(b);
        this.indices[b] = parameterImpl.getIndex();
        this.getters[b] = param1ValueGetterFactory.get(parameterImpl);
      } 
    }
    
    protected WrapperComposite buildWrapperComposite(Object[] param1ArrayOfObject) {
      WrapperComposite wrapperComposite = new WrapperComposite();
      wrapperComposite.bridges = this.parameterBridges;
      wrapperComposite.values = new Object[this.parameterBridges.length];
      for (int i = this.indices.length - 1; i >= 0; i--) {
        Object object = this.getters[i].get(param1ArrayOfObject[this.indices[i]]);
        if (object == null)
          throw new WebServiceException("Method Parameter: " + ((ParameterImpl)this.children.get(i)).getName() + " cannot be null. This is BP 1.1 R2211 violation."); 
        wrapperComposite.values[i] = object;
      } 
      return wrapperComposite;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\BodyBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */