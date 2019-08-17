package com.sun.xml.internal.ws.server.sei;

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

public abstract class EndpointResponseMessageBuilder {
  public static final EndpointResponseMessageBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
  
  public static final EndpointResponseMessageBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);
  
  public abstract Message createMessage(Object[] paramArrayOfObject, Object paramObject);
  
  public static final class Bare extends JAXB {
    private final int methodPos;
    
    private final ValueGetter getter;
    
    public Bare(ParameterImpl param1ParameterImpl, SOAPVersion param1SOAPVersion) {
      super(param1ParameterImpl.getXMLBridge(), param1SOAPVersion);
      this.methodPos = param1ParameterImpl.getIndex();
      this.getter = ValueGetter.get(param1ParameterImpl);
    }
    
    Object build(Object[] param1ArrayOfObject, Object param1Object) { return (this.methodPos == -1) ? param1Object : this.getter.get(param1ArrayOfObject[this.methodPos]); }
  }
  
  public static final class DocLit extends Wrapped {
    private final PropertyAccessor[] accessors;
    
    private final Class wrapper;
    
    private boolean dynamicWrapper;
    
    private BindingContext bindingContext;
    
    public DocLit(WrapperParameter param1WrapperParameter, SOAPVersion param1SOAPVersion) {
      super(param1WrapperParameter, param1SOAPVersion);
      this.bindingContext = param1WrapperParameter.getOwner().getBindingContext();
      this.wrapper = (Class)(param1WrapperParameter.getXMLBridge().getTypeInfo()).type;
      this.dynamicWrapper = WrapperComposite.class.equals(this.wrapper);
      this.children = param1WrapperParameter.getWrapperChildren();
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
            this.accessors[b] = this.dynamicWrapper ? null : parameterImpl.getOwner().getBindingContext().getElementPropertyAccessor(this.wrapper, qName.getNamespaceURI(), qName.getLocalPart());
          } catch (JAXBException jAXBException) {
            throw new WebServiceException(this.wrapper + " do not have a property of the name " + qName, jAXBException);
          } 
        } 
      } 
    }
    
    Object build(Object[] param1ArrayOfObject, Object param1Object) {
      if (this.dynamicWrapper)
        return buildWrapperComposite(param1ArrayOfObject, param1Object); 
      try {
        Object object = this.bindingContext.newWrapperInstace(this.wrapper);
        for (int i = this.indices.length - 1; i >= 0; i--) {
          if (this.indices[i] == -1) {
            this.accessors[i].set(object, param1Object);
          } else {
            this.accessors[i].set(object, this.getters[i].get(param1ArrayOfObject[this.indices[i]]));
          } 
        } 
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
  
  private static final class Empty extends EndpointResponseMessageBuilder {
    private final SOAPVersion soapVersion;
    
    public Empty(SOAPVersion param1SOAPVersion) { this.soapVersion = param1SOAPVersion; }
    
    public Message createMessage(Object[] param1ArrayOfObject, Object param1Object) { return Messages.createEmpty(this.soapVersion); }
  }
  
  private static abstract class JAXB extends EndpointResponseMessageBuilder {
    private final XMLBridge bridge;
    
    private final SOAPVersion soapVersion;
    
    protected JAXB(XMLBridge param1XMLBridge, SOAPVersion param1SOAPVersion) {
      assert param1XMLBridge != null;
      this.bridge = param1XMLBridge;
      this.soapVersion = param1SOAPVersion;
    }
    
    public final Message createMessage(Object[] param1ArrayOfObject, Object param1Object) { return JAXBMessage.create(this.bridge, build(param1ArrayOfObject, param1Object), this.soapVersion); }
    
    abstract Object build(Object[] param1ArrayOfObject, Object param1Object);
  }
  
  public static final class RpcLit extends Wrapped {
    public RpcLit(WrapperParameter param1WrapperParameter, SOAPVersion param1SOAPVersion) {
      super(param1WrapperParameter, param1SOAPVersion);
      assert (param1WrapperParameter.getTypeInfo()).type == WrapperComposite.class;
      this.parameterBridges = new XMLBridge[this.children.size()];
      for (byte b = 0; b < this.parameterBridges.length; b++)
        this.parameterBridges[b] = ((ParameterImpl)this.children.get(b)).getXMLBridge(); 
    }
    
    Object build(Object[] param1ArrayOfObject, Object param1Object) { return buildWrapperComposite(param1ArrayOfObject, param1Object); }
  }
  
  static abstract class Wrapped extends JAXB {
    protected final int[] indices;
    
    protected final ValueGetter[] getters;
    
    protected XMLBridge[] parameterBridges;
    
    protected List<ParameterImpl> children;
    
    protected Wrapped(WrapperParameter param1WrapperParameter, SOAPVersion param1SOAPVersion) {
      super(param1WrapperParameter.getXMLBridge(), param1SOAPVersion);
      this.children = param1WrapperParameter.getWrapperChildren();
      this.indices = new int[this.children.size()];
      this.getters = new ValueGetter[this.children.size()];
      for (byte b = 0; b < this.indices.length; b++) {
        ParameterImpl parameterImpl = (ParameterImpl)this.children.get(b);
        this.indices[b] = parameterImpl.getIndex();
        this.getters[b] = ValueGetter.get(parameterImpl);
      } 
    }
    
    WrapperComposite buildWrapperComposite(Object[] param1ArrayOfObject, Object param1Object) {
      WrapperComposite wrapperComposite = new WrapperComposite();
      wrapperComposite.bridges = this.parameterBridges;
      wrapperComposite.values = new Object[this.parameterBridges.length];
      for (int i = this.indices.length - 1; i >= 0; i--) {
        Object object;
        if (this.indices[i] == -1) {
          object = this.getters[i].get(param1Object);
        } else {
          object = this.getters[i].get(param1ArrayOfObject[this.indices[i]]);
        } 
        if (object == null)
          throw new WebServiceException("Method Parameter: " + ((ParameterImpl)this.children.get(i)).getName() + " cannot be null. This is BP 1.1 R2211 violation."); 
        wrapperComposite.values[i] = object;
      } 
      return wrapperComposite;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\EndpointResponseMessageBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */