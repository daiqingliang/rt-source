package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class LogicalMessageImpl implements LogicalMessage {
  private Packet packet;
  
  protected BindingContext defaultJaxbContext;
  
  private ImmutableLM lm = null;
  
  public LogicalMessageImpl(BindingContext paramBindingContext, Packet paramPacket) {
    this.packet = paramPacket;
    this.defaultJaxbContext = paramBindingContext;
  }
  
  public Source getPayload() {
    if (this.lm == null) {
      Source source = this.packet.getMessage().copy().readPayloadAsSource();
      if (source instanceof DOMSource)
        this.lm = createLogicalMessageImpl(source); 
      return source;
    } 
    return this.lm.getPayload();
  }
  
  public void setPayload(Source paramSource) { this.lm = createLogicalMessageImpl(paramSource); }
  
  private ImmutableLM createLogicalMessageImpl(Source paramSource) {
    if (paramSource == null) {
      this.lm = new EmptyLogicalMessageImpl();
    } else if (paramSource instanceof DOMSource) {
      this.lm = new DOMLogicalMessageImpl((DOMSource)paramSource);
    } else {
      this.lm = new SourceLogicalMessageImpl(paramSource);
    } 
    return this.lm;
  }
  
  public Object getPayload(BindingContext paramBindingContext) {
    Object object;
    if (paramBindingContext == null)
      paramBindingContext = this.defaultJaxbContext; 
    if (paramBindingContext == null)
      throw new WebServiceException("JAXBContext parameter cannot be null"); 
    if (this.lm == null) {
      try {
        object = this.packet.getMessage().copy().readPayloadAsJAXB(paramBindingContext.createUnmarshaller());
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    } else {
      object = this.lm.getPayload(paramBindingContext);
      this.lm = new JAXBLogicalMessageImpl(paramBindingContext.getJAXBContext(), object);
    } 
    return object;
  }
  
  public Object getPayload(JAXBContext paramJAXBContext) {
    Object object;
    if (paramJAXBContext == null)
      return getPayload(this.defaultJaxbContext); 
    if (paramJAXBContext == null)
      throw new WebServiceException("JAXBContext parameter cannot be null"); 
    if (this.lm == null) {
      try {
        object = this.packet.getMessage().copy().readPayloadAsJAXB(paramJAXBContext.createUnmarshaller());
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    } else {
      object = this.lm.getPayload(paramJAXBContext);
      this.lm = new JAXBLogicalMessageImpl(paramJAXBContext, object);
    } 
    return object;
  }
  
  public void setPayload(Object paramObject, BindingContext paramBindingContext) {
    if (paramBindingContext == null)
      paramBindingContext = this.defaultJaxbContext; 
    if (paramObject == null) {
      this.lm = new EmptyLogicalMessageImpl();
    } else {
      this.lm = new JAXBLogicalMessageImpl(paramBindingContext.getJAXBContext(), paramObject);
    } 
  }
  
  public void setPayload(Object paramObject, JAXBContext paramJAXBContext) {
    if (paramJAXBContext == null)
      setPayload(paramObject, this.defaultJaxbContext); 
    if (paramObject == null) {
      this.lm = new EmptyLogicalMessageImpl();
    } else {
      this.lm = new JAXBLogicalMessageImpl(paramJAXBContext, paramObject);
    } 
  }
  
  public boolean isPayloadModifed() { return (this.lm != null); }
  
  public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding) {
    assert isPayloadModifed();
    return isPayloadModifed() ? this.lm.getMessage(paramMessageHeaders, paramAttachmentSet, paramWSBinding) : this.packet.getMessage();
  }
  
  private class DOMLogicalMessageImpl extends SourceLogicalMessageImpl {
    private DOMSource dom;
    
    public DOMLogicalMessageImpl(DOMSource param1DOMSource) {
      super(LogicalMessageImpl.this, param1DOMSource);
      this.dom = param1DOMSource;
    }
    
    public Source getPayload() { return this.dom; }
    
    public Message getMessage(MessageHeaders param1MessageHeaders, AttachmentSet param1AttachmentSet, WSBinding param1WSBinding) {
      Node node = this.dom.getNode();
      if (node.getNodeType() == 9)
        node = ((Document)node).getDocumentElement(); 
      return new DOMMessage(param1WSBinding.getSOAPVersion(), param1MessageHeaders, (Element)node, param1AttachmentSet);
    }
  }
  
  private class EmptyLogicalMessageImpl extends ImmutableLM {
    public EmptyLogicalMessageImpl() { super(LogicalMessageImpl.this, null); }
    
    public Source getPayload() { return null; }
    
    public Object getPayload(JAXBContext param1JAXBContext) { return null; }
    
    public Object getPayload(BindingContext param1BindingContext) { return null; }
    
    public Message getMessage(MessageHeaders param1MessageHeaders, AttachmentSet param1AttachmentSet, WSBinding param1WSBinding) { return new EmptyMessageImpl(param1MessageHeaders, param1AttachmentSet, param1WSBinding.getSOAPVersion()); }
  }
  
  private abstract class ImmutableLM {
    private ImmutableLM() {}
    
    public abstract Source getPayload();
    
    public abstract Object getPayload(BindingContext param1BindingContext);
    
    public abstract Object getPayload(JAXBContext param1JAXBContext);
    
    public abstract Message getMessage(MessageHeaders param1MessageHeaders, AttachmentSet param1AttachmentSet, WSBinding param1WSBinding);
  }
  
  private class JAXBLogicalMessageImpl extends ImmutableLM {
    private JAXBContext ctxt;
    
    private Object o;
    
    public JAXBLogicalMessageImpl(JAXBContext param1JAXBContext, Object param1Object) {
      super(LogicalMessageImpl.this, null);
      this.ctxt = param1JAXBContext;
      this.o = param1Object;
    }
    
    public Source getPayload() {
      JAXBContext jAXBContext = this.ctxt;
      if (jAXBContext == null)
        jAXBContext = LogicalMessageImpl.this.defaultJaxbContext.getJAXBContext(); 
      try {
        return new JAXBSource(jAXBContext, this.o);
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    }
    
    public Object getPayload(JAXBContext param1JAXBContext) {
      try {
        Source source = getPayload();
        if (source == null)
          return null; 
        Unmarshaller unmarshaller = param1JAXBContext.createUnmarshaller();
        return unmarshaller.unmarshal(source);
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    }
    
    public Object getPayload(BindingContext param1BindingContext) {
      try {
        Source source = getPayload();
        if (source == null)
          return null; 
        Unmarshaller unmarshaller = param1BindingContext.createUnmarshaller();
        return unmarshaller.unmarshal(source);
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    }
    
    public Message getMessage(MessageHeaders param1MessageHeaders, AttachmentSet param1AttachmentSet, WSBinding param1WSBinding) { return JAXBMessage.create(BindingContextFactory.create(this.ctxt), this.o, param1WSBinding.getSOAPVersion(), param1MessageHeaders, param1AttachmentSet); }
  }
  
  private class SourceLogicalMessageImpl extends ImmutableLM {
    private Source payloadSrc;
    
    public SourceLogicalMessageImpl(Source param1Source) {
      super(LogicalMessageImpl.this, null);
      this.payloadSrc = param1Source;
    }
    
    public Source getPayload() {
      assert !(this.payloadSrc instanceof DOMSource);
      try {
        Transformer transformer = XmlUtil.newTransformer();
        DOMResult dOMResult = new DOMResult();
        transformer.transform(this.payloadSrc, dOMResult);
        DOMSource dOMSource = new DOMSource(dOMResult.getNode());
        LogicalMessageImpl.this.lm = new LogicalMessageImpl.DOMLogicalMessageImpl(LogicalMessageImpl.this, dOMSource);
        this.payloadSrc = null;
        return dOMSource;
      } catch (TransformerException transformerException) {
        throw new WebServiceException(transformerException);
      } 
    }
    
    public Object getPayload(JAXBContext param1JAXBContext) {
      try {
        Source source = getPayload();
        if (source == null)
          return null; 
        Unmarshaller unmarshaller = param1JAXBContext.createUnmarshaller();
        return unmarshaller.unmarshal(source);
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    }
    
    public Object getPayload(BindingContext param1BindingContext) {
      try {
        Source source = getPayload();
        if (source == null)
          return null; 
        Unmarshaller unmarshaller = param1BindingContext.createUnmarshaller();
        return unmarshaller.unmarshal(source);
      } catch (JAXBException jAXBException) {
        throw new WebServiceException(jAXBException);
      } 
    }
    
    public Message getMessage(MessageHeaders param1MessageHeaders, AttachmentSet param1AttachmentSet, WSBinding param1WSBinding) {
      assert this.payloadSrc != null;
      return new PayloadSourceMessage(param1MessageHeaders, this.payloadSrc, param1AttachmentSet, param1WSBinding.getSOAPVersion());
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\LogicalMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */