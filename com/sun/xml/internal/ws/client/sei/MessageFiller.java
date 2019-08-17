package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.message.ByteArrayAttachment;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.message.JAXBAttachment;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;

abstract class MessageFiller {
  protected final int methodPos;
  
  protected MessageFiller(int paramInt) { this.methodPos = paramInt; }
  
  abstract void fillIn(Object[] paramArrayOfObject, Message paramMessage);
  
  private static boolean isXMLMimeType(String paramString) { return (paramString.equals("text/xml") || paramString.equals("application/xml")); }
  
  static abstract class AttachmentFiller extends MessageFiller {
    protected final ParameterImpl param;
    
    protected final ValueGetter getter;
    
    protected final String mimeType;
    
    private final String contentIdPart;
    
    protected AttachmentFiller(ParameterImpl param1ParameterImpl, ValueGetter param1ValueGetter) {
      super(param1ParameterImpl.getIndex());
      this.param = param1ParameterImpl;
      this.getter = param1ValueGetter;
      this.mimeType = param1ParameterImpl.getBinding().getMimeType();
      try {
        this.contentIdPart = URLEncoder.encode(param1ParameterImpl.getPartName(), "UTF-8") + '=';
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new WebServiceException(unsupportedEncodingException);
      } 
    }
    
    public static MessageFiller createAttachmentFiller(ParameterImpl param1ParameterImpl, ValueGetter param1ValueGetter) {
      Class clazz = (Class)(param1ParameterImpl.getTypeInfo()).type;
      return (DataHandler.class.isAssignableFrom(clazz) || javax.xml.transform.Source.class.isAssignableFrom(clazz)) ? new MessageFiller.DataHandlerFiller(param1ParameterImpl, param1ValueGetter) : ((byte[].class == clazz) ? new MessageFiller.ByteArrayFiller(param1ParameterImpl, param1ValueGetter) : (MessageFiller.isXMLMimeType(param1ParameterImpl.getBinding().getMimeType()) ? new MessageFiller.JAXBFiller(param1ParameterImpl, param1ValueGetter) : new MessageFiller.DataHandlerFiller(param1ParameterImpl, param1ValueGetter)));
    }
    
    String getContentId() { return this.contentIdPart + UUID.randomUUID() + "@jaxws.sun.com"; }
  }
  
  private static class ByteArrayFiller extends AttachmentFiller {
    protected ByteArrayFiller(ParameterImpl param1ParameterImpl, ValueGetter param1ValueGetter) { super(param1ParameterImpl, param1ValueGetter); }
    
    void fillIn(Object[] param1ArrayOfObject, Message param1Message) {
      String str = getContentId();
      Object object = this.getter.get(param1ArrayOfObject[this.methodPos]);
      ByteArrayAttachment byteArrayAttachment = new ByteArrayAttachment(str, (byte[])object, this.mimeType);
      param1Message.getAttachments().add(byteArrayAttachment);
    }
  }
  
  private static class DataHandlerFiller extends AttachmentFiller {
    protected DataHandlerFiller(ParameterImpl param1ParameterImpl, ValueGetter param1ValueGetter) { super(param1ParameterImpl, param1ValueGetter); }
    
    void fillIn(Object[] param1ArrayOfObject, Message param1Message) {
      String str = getContentId();
      Object object = this.getter.get(param1ArrayOfObject[this.methodPos]);
      DataHandler dataHandler = (object instanceof DataHandler) ? (DataHandler)object : new DataHandler(object, this.mimeType);
      DataHandlerAttachment dataHandlerAttachment = new DataHandlerAttachment(str, dataHandler);
      param1Message.getAttachments().add(dataHandlerAttachment);
    }
  }
  
  static final class Header extends MessageFiller {
    private final XMLBridge bridge;
    
    private final ValueGetter getter;
    
    protected Header(int param1Int, XMLBridge param1XMLBridge, ValueGetter param1ValueGetter) {
      super(param1Int);
      this.bridge = param1XMLBridge;
      this.getter = param1ValueGetter;
    }
    
    void fillIn(Object[] param1ArrayOfObject, Message param1Message) {
      Object object = this.getter.get(param1ArrayOfObject[this.methodPos]);
      param1Message.getHeaders().add(Headers.create(this.bridge, object));
    }
  }
  
  private static class JAXBFiller extends AttachmentFiller {
    protected JAXBFiller(ParameterImpl param1ParameterImpl, ValueGetter param1ValueGetter) { super(param1ParameterImpl, param1ValueGetter); }
    
    void fillIn(Object[] param1ArrayOfObject, Message param1Message) {
      String str = getContentId();
      Object object = this.getter.get(param1ArrayOfObject[this.methodPos]);
      JAXBAttachment jAXBAttachment = new JAXBAttachment(str, object, this.param.getXMLBridge(), this.mimeType);
      param1Message.getAttachments().add(jAXBAttachment);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\MessageFiller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */