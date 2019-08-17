package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.encoding.DataHandlerDataSource;
import com.sun.xml.internal.ws.encoding.StringDataContentHandler;
import com.sun.xml.internal.ws.message.AttachmentUnmarshallerImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.jws.WebParam;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class EndpointArgumentsBuilder {
  public static final EndpointArgumentsBuilder NONE = new None(null);
  
  private static final Map<Class, Object> primitiveUninitializedValues = new HashMap();
  
  protected QName wrapperName;
  
  protected Map<QName, WrappedPartBuilder> wrappedParts = null;
  
  public abstract void readRequest(Message paramMessage, Object[] paramArrayOfObject) throws JAXBException, XMLStreamException;
  
  public static Object getVMUninitializedValue(Type paramType) { return primitiveUninitializedValues.get(paramType); }
  
  protected void readWrappedRequest(Message paramMessage, Object[] paramArrayOfObject) throws JAXBException, XMLStreamException {
    if (!paramMessage.hasPayload())
      throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element"); 
    XMLStreamReader xMLStreamReader = paramMessage.readPayload();
    XMLStreamReaderUtil.verifyTag(xMLStreamReader, this.wrapperName);
    xMLStreamReader.nextTag();
    while (xMLStreamReader.getEventType() == 1) {
      QName qName = xMLStreamReader.getName();
      WrappedPartBuilder wrappedPartBuilder = (WrappedPartBuilder)this.wrappedParts.get(qName);
      if (wrappedPartBuilder == null) {
        XMLStreamReaderUtil.skipElement(xMLStreamReader);
        xMLStreamReader.nextTag();
      } else {
        wrappedPartBuilder.readRequest(paramArrayOfObject, xMLStreamReader, paramMessage.getAttachments());
      } 
      XMLStreamReaderUtil.toNextTag(xMLStreamReader, qName);
    } 
    xMLStreamReader.close();
    XMLStreamReaderFactory.recycle(xMLStreamReader);
  }
  
  public static final String getWSDLPartName(Attachment paramAttachment) {
    String str1 = paramAttachment.getContentId();
    int i = str1.lastIndexOf('@', str1.length());
    if (i == -1)
      return null; 
    String str2 = str1.substring(0, i);
    i = str2.lastIndexOf('=', str2.length());
    if (i == -1)
      return null; 
    try {
      return URLDecoder.decode(str2.substring(0, i), "UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new WebServiceException(unsupportedEncodingException);
    } 
  }
  
  private static boolean isXMLMimeType(String paramString) { return (paramString.equals("text/xml") || paramString.equals("application/xml")); }
  
  static  {
    Map map = primitiveUninitializedValues;
    map.put(int.class, Integer.valueOf(0));
    map.put(char.class, Character.valueOf(false));
    map.put(byte.class, Byte.valueOf((byte)0));
    map.put(short.class, Short.valueOf((short)0));
    map.put(long.class, Long.valueOf(0L));
    map.put(float.class, Float.valueOf(0.0F));
    map.put(double.class, Double.valueOf(0.0D));
  }
  
  public static abstract class AttachmentBuilder extends EndpointArgumentsBuilder {
    protected final EndpointValueSetter setter;
    
    protected final ParameterImpl param;
    
    protected final String pname;
    
    protected final String pname1;
    
    AttachmentBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) {
      this.setter = param1EndpointValueSetter;
      this.param = param1ParameterImpl;
      this.pname = param1ParameterImpl.getPartName();
      this.pname1 = "<" + this.pname;
    }
    
    public static EndpointArgumentsBuilder createAttachmentBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) {
      Class clazz = (Class)(param1ParameterImpl.getTypeInfo()).type;
      if (javax.activation.DataHandler.class.isAssignableFrom(clazz))
        return new EndpointArgumentsBuilder.DataHandlerBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      if (byte[].class == clazz)
        return new EndpointArgumentsBuilder.ByteArrayBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      if (javax.xml.transform.Source.class.isAssignableFrom(clazz))
        return new EndpointArgumentsBuilder.SourceBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      if (java.awt.Image.class.isAssignableFrom(clazz))
        return new EndpointArgumentsBuilder.ImageBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      if (InputStream.class == clazz)
        return new EndpointArgumentsBuilder.InputStreamBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      if (EndpointArgumentsBuilder.isXMLMimeType(param1ParameterImpl.getBinding().getMimeType()))
        return new EndpointArgumentsBuilder.JAXBBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      if (String.class.isAssignableFrom(clazz))
        return new EndpointArgumentsBuilder.StringBuilder(param1ParameterImpl, param1EndpointValueSetter); 
      throw new UnsupportedOperationException("Unknown Type=" + clazz + " Attachment is not mapped.");
    }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException {
      boolean bool = false;
      for (Attachment attachment : param1Message.getAttachments()) {
        String str = getWSDLPartName(attachment);
        if (str != null && (str.equals(this.pname) || str.equals(this.pname1))) {
          bool = true;
          mapAttachment(attachment, param1ArrayOfObject);
          break;
        } 
      } 
      if (!bool)
        throw new WebServiceException("Missing Attachment for " + this.pname); 
    }
    
    abstract void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException;
  }
  
  public static final class Body extends EndpointArgumentsBuilder {
    private final XMLBridge<?> bridge;
    
    private final EndpointValueSetter setter;
    
    public Body(XMLBridge<?> param1XMLBridge, EndpointValueSetter param1EndpointValueSetter) {
      this.bridge = param1XMLBridge;
      this.setter = param1EndpointValueSetter;
    }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException { this.setter.put(param1Message.readPayloadAsJAXB(this.bridge), param1ArrayOfObject); }
  }
  
  private static final class ByteArrayBuilder extends AttachmentBuilder {
    ByteArrayBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException { this.setter.put(param1Attachment.asByteArray(), param1ArrayOfObject); }
  }
  
  public static final class Composite extends EndpointArgumentsBuilder {
    private final EndpointArgumentsBuilder[] builders;
    
    public Composite(EndpointArgumentsBuilder... param1VarArgs) { this.builders = param1VarArgs; }
    
    public Composite(Collection<? extends EndpointArgumentsBuilder> param1Collection) { this((EndpointArgumentsBuilder[])param1Collection.toArray(new EndpointArgumentsBuilder[param1Collection.size()])); }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException {
      for (EndpointArgumentsBuilder endpointArgumentsBuilder : this.builders)
        endpointArgumentsBuilder.readRequest(param1Message, param1ArrayOfObject); 
    }
  }
  
  private static final class DataHandlerBuilder extends AttachmentBuilder {
    DataHandlerBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException { this.setter.put(param1Attachment.asDataHandler(), param1ArrayOfObject); }
  }
  
  public static final class DocLit extends EndpointArgumentsBuilder {
    private final PartBuilder[] parts;
    
    private final XMLBridge wrapper;
    
    private boolean dynamicWrapper;
    
    public DocLit(WrapperParameter param1WrapperParameter, WebParam.Mode param1Mode) {
      this.wrapperName = param1WrapperParameter.getName();
      this.wrapper = param1WrapperParameter.getXMLBridge();
      Class clazz = (Class)(this.wrapper.getTypeInfo()).type;
      this.dynamicWrapper = com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals(clazz);
      ArrayList arrayList = new ArrayList();
      List list = param1WrapperParameter.getWrapperChildren();
      for (ParameterImpl parameterImpl : list) {
        if (parameterImpl.getMode() == param1Mode)
          continue; 
        QName qName = parameterImpl.getName();
        try {
          if (this.dynamicWrapper) {
            if (this.wrappedParts == null)
              this.wrappedParts = new HashMap(); 
            XMLBridge xMLBridge = parameterImpl.getInlinedRepeatedElementBridge();
            if (xMLBridge == null)
              xMLBridge = parameterImpl.getXMLBridge(); 
            this.wrappedParts.put(parameterImpl.getName(), new EndpointArgumentsBuilder.WrappedPartBuilder(xMLBridge, EndpointValueSetter.get(parameterImpl)));
            continue;
          } 
          arrayList.add(new PartBuilder(param1WrapperParameter.getOwner().getBindingContext().getElementPropertyAccessor(clazz, qName.getNamespaceURI(), parameterImpl.getName().getLocalPart()), EndpointValueSetter.get(parameterImpl)));
          assert parameterImpl.getBinding() == ParameterBinding.BODY;
        } catch (JAXBException jAXBException) {
          throw new WebServiceException(clazz + " do not have a property of the name " + qName, jAXBException);
        } 
      } 
      this.parts = (PartBuilder[])arrayList.toArray(new PartBuilder[arrayList.size()]);
    }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException {
      if (this.dynamicWrapper) {
        readWrappedRequest(param1Message, param1ArrayOfObject);
      } else if (this.parts.length > 0) {
        if (!param1Message.hasPayload())
          throw new WebServiceException("No payload. Expecting payload with " + this.wrapperName + " element"); 
        XMLStreamReader xMLStreamReader = param1Message.readPayload();
        XMLStreamReaderUtil.verifyTag(xMLStreamReader, this.wrapperName);
        Object object = this.wrapper.unmarshal(xMLStreamReader, (param1Message.getAttachments() != null) ? new AttachmentUnmarshallerImpl(param1Message.getAttachments()) : null);
        try {
          for (PartBuilder partBuilder : this.parts)
            partBuilder.readRequest(param1ArrayOfObject, object); 
        } catch (DatabindingException databindingException) {
          throw new WebServiceException(databindingException);
        } 
        xMLStreamReader.close();
        XMLStreamReaderFactory.recycle(xMLStreamReader);
      } else {
        param1Message.consume();
      } 
    }
    
    static final class PartBuilder {
      private final PropertyAccessor accessor;
      
      private final EndpointValueSetter setter;
      
      public PartBuilder(PropertyAccessor param2PropertyAccessor, EndpointValueSetter param2EndpointValueSetter) {
        this.accessor = param2PropertyAccessor;
        this.setter = param2EndpointValueSetter;
        assert param2PropertyAccessor != null && param2EndpointValueSetter != null;
      }
      
      final void readRequest(Object[] param2ArrayOfObject, Object param2Object) {
        Object object = this.accessor.get(param2Object);
        this.setter.put(object, param2ArrayOfObject);
      }
    }
  }
  
  public static final class Header extends EndpointArgumentsBuilder {
    private final XMLBridge<?> bridge;
    
    private final EndpointValueSetter setter;
    
    private final QName headerName;
    
    private final SOAPVersion soapVersion;
    
    public Header(SOAPVersion param1SOAPVersion, QName param1QName, XMLBridge<?> param1XMLBridge, EndpointValueSetter param1EndpointValueSetter) {
      this.soapVersion = param1SOAPVersion;
      this.headerName = param1QName;
      this.bridge = param1XMLBridge;
      this.setter = param1EndpointValueSetter;
    }
    
    public Header(SOAPVersion param1SOAPVersion, ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) {
      this(param1SOAPVersion, (param1ParameterImpl.getTypeInfo()).tagName, param1ParameterImpl.getXMLBridge(), param1EndpointValueSetter);
      assert param1ParameterImpl.getOutBinding() == ParameterBinding.HEADER;
    }
    
    private SOAPFaultException createDuplicateHeaderException() {
      try {
        SOAPFault sOAPFault = this.soapVersion.getSOAPFactory().createFault();
        sOAPFault.setFaultCode(this.soapVersion.faultCodeClient);
        sOAPFault.setFaultString(ServerMessages.DUPLICATE_PORT_KNOWN_HEADER(this.headerName));
        return new SOAPFaultException(sOAPFault);
      } catch (SOAPException sOAPException) {
        throw new WebServiceException(sOAPException);
      } 
    }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException {
      Header header = null;
      Iterator iterator = param1Message.getHeaders().getHeaders(this.headerName, true);
      if (iterator.hasNext()) {
        header = (Header)iterator.next();
        if (iterator.hasNext())
          throw createDuplicateHeaderException(); 
      } 
      if (header != null)
        this.setter.put(header.readAsJAXB(this.bridge), param1ArrayOfObject); 
    }
  }
  
  private static final class ImageBuilder extends AttachmentBuilder {
    ImageBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException {
      BufferedImage bufferedImage;
      inputStream = null;
      try {
        inputStream = param1Attachment.asInputStream();
        bufferedImage = ImageIO.read(inputStream);
      } catch (IOException iOException) {
        throw new WebServiceException(iOException);
      } finally {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (IOException iOException) {
            throw new WebServiceException(iOException);
          }  
      } 
      this.setter.put(bufferedImage, param1ArrayOfObject);
    }
  }
  
  private static final class InputStreamBuilder extends AttachmentBuilder {
    InputStreamBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException { this.setter.put(param1Attachment.asInputStream(), param1ArrayOfObject); }
  }
  
  private static final class JAXBBuilder extends AttachmentBuilder {
    JAXBBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException {
      Object object = this.param.getXMLBridge().unmarshal(param1Attachment.asInputStream());
      this.setter.put(object, param1ArrayOfObject);
    }
  }
  
  static final class None extends EndpointArgumentsBuilder {
    private None() {}
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException { param1Message.consume(); }
  }
  
  public static final class NullSetter extends EndpointArgumentsBuilder {
    private final EndpointValueSetter setter;
    
    private final Object nullValue;
    
    public NullSetter(EndpointValueSetter param1EndpointValueSetter, Object param1Object) {
      assert param1EndpointValueSetter != null;
      this.nullValue = param1Object;
      this.setter = param1EndpointValueSetter;
    }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException { this.setter.put(this.nullValue, param1ArrayOfObject); }
  }
  
  public static final class RpcLit extends EndpointArgumentsBuilder {
    public RpcLit(WrapperParameter param1WrapperParameter) {
      assert (param1WrapperParameter.getTypeInfo()).type == com.sun.xml.internal.ws.spi.db.WrapperComposite.class;
      this.wrapperName = param1WrapperParameter.getName();
      this.wrappedParts = new HashMap();
      List list = param1WrapperParameter.getWrapperChildren();
      for (ParameterImpl parameterImpl : list) {
        this.wrappedParts.put(parameterImpl.getName(), new EndpointArgumentsBuilder.WrappedPartBuilder(parameterImpl.getXMLBridge(), EndpointValueSetter.get(parameterImpl)));
        assert parameterImpl.getBinding() == ParameterBinding.BODY;
      } 
    }
    
    public void readRequest(Message param1Message, Object[] param1ArrayOfObject) throws JAXBException, XMLStreamException { readWrappedRequest(param1Message, param1ArrayOfObject); }
  }
  
  private static final class SourceBuilder extends AttachmentBuilder {
    SourceBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException { this.setter.put(param1Attachment.asSource(), param1ArrayOfObject); }
  }
  
  private static final class StringBuilder extends AttachmentBuilder {
    StringBuilder(ParameterImpl param1ParameterImpl, EndpointValueSetter param1EndpointValueSetter) { super(param1ParameterImpl, param1EndpointValueSetter); }
    
    void mapAttachment(Attachment param1Attachment, Object[] param1ArrayOfObject) throws JAXBException {
      param1Attachment.getContentType();
      StringDataContentHandler stringDataContentHandler = new StringDataContentHandler();
      try {
        String str = (String)stringDataContentHandler.getContent(new DataHandlerDataSource(param1Attachment.asDataHandler()));
        this.setter.put(str, param1ArrayOfObject);
      } catch (Exception exception) {
        throw new WebServiceException(exception);
      } 
    }
  }
  
  static final class WrappedPartBuilder {
    private final XMLBridge bridge;
    
    private final EndpointValueSetter setter;
    
    public WrappedPartBuilder(XMLBridge param1XMLBridge, EndpointValueSetter param1EndpointValueSetter) {
      this.bridge = param1XMLBridge;
      this.setter = param1EndpointValueSetter;
    }
    
    void readRequest(Object[] param1ArrayOfObject, XMLStreamReader param1XMLStreamReader, AttachmentSet param1AttachmentSet) throws JAXBException {
      Object object = null;
      AttachmentUnmarshallerImpl attachmentUnmarshallerImpl = (param1AttachmentSet != null) ? new AttachmentUnmarshallerImpl(param1AttachmentSet) : null;
      if (this.bridge instanceof RepeatedElementBridge) {
        RepeatedElementBridge repeatedElementBridge = (RepeatedElementBridge)this.bridge;
        ArrayList arrayList = new ArrayList();
        QName qName = param1XMLStreamReader.getName();
        while (param1XMLStreamReader.getEventType() == 1 && qName.equals(param1XMLStreamReader.getName())) {
          arrayList.add(repeatedElementBridge.unmarshal(param1XMLStreamReader, attachmentUnmarshallerImpl));
          XMLStreamReaderUtil.toNextTag(param1XMLStreamReader, qName);
        } 
        object = repeatedElementBridge.collectionHandler().convert(arrayList);
      } else {
        object = this.bridge.unmarshal(param1XMLStreamReader, attachmentUnmarshallerImpl);
      } 
      this.setter.put(object, param1ArrayOfObject);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\EndpointArgumentsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */