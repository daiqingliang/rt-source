package com.sun.xml.internal.ws.encoding.xml;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import com.sun.xml.internal.ws.encoding.ContentType;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class XMLMessage {
  private static final int PLAIN_XML_FLAG = 1;
  
  private static final int MIME_MULTIPART_FLAG = 2;
  
  private static final int FI_ENCODED_FLAG = 16;
  
  public static Message create(String paramString, InputStream paramInputStream, WSFeatureList paramWSFeatureList) {
    UnknownContent unknownContent;
    try {
      paramInputStream = StreamUtils.hasSomeData(paramInputStream);
      if (paramInputStream == null)
        return Messages.createEmpty(SOAPVersion.SOAP_11); 
      if (paramString != null) {
        ContentType contentType = new ContentType(paramString);
        int i = identifyContentType(contentType);
        if ((i & 0x2) != 0) {
          unknownContent = new XMLMultiPart(paramString, paramInputStream, paramWSFeatureList);
        } else if ((i & true) != 0) {
          unknownContent = new XmlContent(paramString, paramInputStream, paramWSFeatureList);
        } else {
          unknownContent = new UnknownContent(paramString, paramInputStream);
        } 
      } else {
        unknownContent = new UnknownContent("application/octet-stream", paramInputStream);
      } 
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return unknownContent;
  }
  
  public static Message create(Source paramSource) { return (paramSource == null) ? Messages.createEmpty(SOAPVersion.SOAP_11) : Messages.createUsingPayload(paramSource, SOAPVersion.SOAP_11); }
  
  public static Message create(DataSource paramDataSource, WSFeatureList paramWSFeatureList) {
    try {
      return (paramDataSource == null) ? Messages.createEmpty(SOAPVersion.SOAP_11) : create(paramDataSource.getContentType(), paramDataSource.getInputStream(), paramWSFeatureList);
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  public static Message create(Exception paramException) { return new FaultMessage(SOAPVersion.SOAP_11); }
  
  private static int getContentId(String paramString) {
    try {
      ContentType contentType = new ContentType(paramString);
      return identifyContentType(contentType);
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  public static boolean isFastInfoset(String paramString) { return ((getContentId(paramString) & 0x10) != 0); }
  
  public static int identifyContentType(ContentType paramContentType) {
    String str1 = paramContentType.getPrimaryType();
    String str2 = paramContentType.getSubType();
    if (str1.equalsIgnoreCase("multipart") && str2.equalsIgnoreCase("related")) {
      String str = paramContentType.getParameter("type");
      if (str != null) {
        if (isXMLType(str))
          return 3; 
        if (isFastInfosetType(str))
          return 18; 
      } 
      return 0;
    } 
    return isXMLType(str1, str2) ? 1 : (isFastInfosetType(str1, str2) ? 16 : 0);
  }
  
  protected static boolean isXMLType(@NotNull String paramString1, @NotNull String paramString2) { return ((paramString1.equalsIgnoreCase("text") && paramString2.equalsIgnoreCase("xml")) || (paramString1.equalsIgnoreCase("application") && paramString2.equalsIgnoreCase("xml")) || (paramString1.equalsIgnoreCase("application") && paramString2.toLowerCase().endsWith("+xml"))); }
  
  protected static boolean isXMLType(String paramString) {
    String str = paramString.toLowerCase();
    return (str.startsWith("text/xml") || str.startsWith("application/xml") || (str.startsWith("application/") && str.indexOf("+xml") != -1));
  }
  
  protected static boolean isFastInfosetType(String paramString1, String paramString2) { return (paramString1.equalsIgnoreCase("application") && paramString2.equalsIgnoreCase("fastinfoset")); }
  
  protected static boolean isFastInfosetType(String paramString) { return paramString.toLowerCase().startsWith("application/fastinfoset"); }
  
  public static DataSource getDataSource(Message paramMessage, WSFeatureList paramWSFeatureList) {
    if (paramMessage == null)
      return null; 
    if (paramMessage instanceof MessageDataSource)
      return ((MessageDataSource)paramMessage).getDataSource(); 
    AttachmentSet attachmentSet = paramMessage.getAttachments();
    if (attachmentSet != null && !attachmentSet.isEmpty()) {
      ByteArrayBuffer byteArrayBuffer1 = new ByteArrayBuffer();
      try {
        XMLHTTPBindingCodec xMLHTTPBindingCodec = new XMLHTTPBindingCodec(paramWSFeatureList);
        Packet packet = new Packet(paramMessage);
        ContentType contentType = xMLHTTPBindingCodec.getStaticContentType(packet);
        xMLHTTPBindingCodec.encode(packet, byteArrayBuffer1);
        return createDataSource(contentType.getContentType(), byteArrayBuffer1.newInputStream());
      } catch (IOException iOException) {
        throw new WebServiceException(iOException);
      } 
    } 
    ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
    XMLStreamWriter xMLStreamWriter = XMLStreamWriterFactory.create(byteArrayBuffer);
    try {
      paramMessage.writePayloadTo(xMLStreamWriter);
      xMLStreamWriter.flush();
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(xMLStreamException);
    } 
    return createDataSource("text/xml", byteArrayBuffer.newInputStream());
  }
  
  public static DataSource createDataSource(String paramString, InputStream paramInputStream) { return new XmlDataSource(paramString, paramInputStream); }
  
  private static class FaultMessage extends EmptyMessageImpl {
    public FaultMessage(SOAPVersion param1SOAPVersion) { super(param1SOAPVersion); }
    
    public boolean isFault() { return true; }
  }
  
  public static interface MessageDataSource {
    boolean hasUnconsumedDataSource();
    
    DataSource getDataSource();
  }
  
  public static class UnknownContent extends AbstractMessageImpl implements MessageDataSource {
    private final DataSource ds;
    
    private final HeaderList headerList;
    
    public UnknownContent(String param1String, InputStream param1InputStream) { this(XMLMessage.createDataSource(param1String, param1InputStream)); }
    
    public UnknownContent(DataSource param1DataSource) {
      super(SOAPVersion.SOAP_11);
      this.ds = param1DataSource;
      this.headerList = new HeaderList(SOAPVersion.SOAP_11);
    }
    
    private UnknownContent(UnknownContent param1UnknownContent) {
      super(param1UnknownContent.soapVersion);
      this.ds = param1UnknownContent.ds;
      this.headerList = HeaderList.copy(param1UnknownContent.headerList);
    }
    
    public boolean hasUnconsumedDataSource() { return true; }
    
    public DataSource getDataSource() {
      assert this.ds != null;
      return this.ds;
    }
    
    protected void writePayloadTo(ContentHandler param1ContentHandler, ErrorHandler param1ErrorHandler, boolean param1Boolean) throws SAXException { throw new UnsupportedOperationException(); }
    
    public boolean hasHeaders() { return false; }
    
    public boolean isFault() { return false; }
    
    public MessageHeaders getHeaders() { return this.headerList; }
    
    public String getPayloadLocalPart() { throw new UnsupportedOperationException(); }
    
    public String getPayloadNamespaceURI() { throw new UnsupportedOperationException(); }
    
    public boolean hasPayload() { return false; }
    
    public Source readPayloadAsSource() { return null; }
    
    public XMLStreamReader readPayload() throws XMLStreamException { throw new WebServiceException("There isn't XML payload. Shouldn't come here."); }
    
    public void writePayloadTo(XMLStreamWriter param1XMLStreamWriter) throws XMLStreamException {}
    
    public Message copy() { return new UnknownContent(this); }
  }
  
  public static final class XMLMultiPart extends AbstractMessageImpl implements MessageDataSource {
    private final DataSource dataSource;
    
    private final StreamingAttachmentFeature feature;
    
    private Message delegate;
    
    private HeaderList headerList = new HeaderList(SOAPVersion.SOAP_11);
    
    private final WSFeatureList features;
    
    public XMLMultiPart(String param1String, InputStream param1InputStream, WSFeatureList param1WSFeatureList) {
      super(SOAPVersion.SOAP_11);
      this.dataSource = XMLMessage.createDataSource(param1String, param1InputStream);
      this.feature = (StreamingAttachmentFeature)param1WSFeatureList.get(StreamingAttachmentFeature.class);
      this.features = param1WSFeatureList;
    }
    
    private Message getMessage() {
      if (this.delegate == null) {
        MimeMultipartParser mimeMultipartParser;
        try {
          mimeMultipartParser = new MimeMultipartParser(this.dataSource.getInputStream(), this.dataSource.getContentType(), this.feature);
        } catch (IOException iOException) {
          throw new WebServiceException(iOException);
        } 
        InputStream inputStream = mimeMultipartParser.getRootPart().asInputStream();
        assert inputStream != null;
        this.delegate = new PayloadSourceMessage(this.headerList, new StreamSource(inputStream), new MimeAttachmentSet(mimeMultipartParser), SOAPVersion.SOAP_11);
      } 
      return this.delegate;
    }
    
    public boolean hasUnconsumedDataSource() { return (this.delegate == null); }
    
    public DataSource getDataSource() { return hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(getMessage(), this.features); }
    
    public boolean hasHeaders() { return false; }
    
    @NotNull
    public MessageHeaders getHeaders() { return this.headerList; }
    
    public String getPayloadLocalPart() { return getMessage().getPayloadLocalPart(); }
    
    public String getPayloadNamespaceURI() { return getMessage().getPayloadNamespaceURI(); }
    
    public boolean hasPayload() { return true; }
    
    public boolean isFault() { return false; }
    
    public Source readEnvelopeAsSource() { return getMessage().readEnvelopeAsSource(); }
    
    public Source readPayloadAsSource() { return getMessage().readPayloadAsSource(); }
    
    public SOAPMessage readAsSOAPMessage() throws SOAPException { return getMessage().readAsSOAPMessage(); }
    
    public SOAPMessage readAsSOAPMessage(Packet param1Packet, boolean param1Boolean) throws SOAPException { return getMessage().readAsSOAPMessage(param1Packet, param1Boolean); }
    
    public <T> T readPayloadAsJAXB(Unmarshaller param1Unmarshaller) throws JAXBException { return (T)getMessage().readPayloadAsJAXB(param1Unmarshaller); }
    
    public <T> T readPayloadAsJAXB(Bridge<T> param1Bridge) throws JAXBException { return (T)getMessage().readPayloadAsJAXB(param1Bridge); }
    
    public XMLStreamReader readPayload() throws XMLStreamException { return getMessage().readPayload(); }
    
    public void writePayloadTo(XMLStreamWriter param1XMLStreamWriter) throws XMLStreamException { getMessage().writePayloadTo(param1XMLStreamWriter); }
    
    public void writeTo(XMLStreamWriter param1XMLStreamWriter) throws XMLStreamException { getMessage().writeTo(param1XMLStreamWriter); }
    
    public void writeTo(ContentHandler param1ContentHandler, ErrorHandler param1ErrorHandler) throws SAXException { getMessage().writeTo(param1ContentHandler, param1ErrorHandler); }
    
    public Message copy() { return getMessage().copy(); }
    
    protected void writePayloadTo(ContentHandler param1ContentHandler, ErrorHandler param1ErrorHandler, boolean param1Boolean) throws SAXException { throw new UnsupportedOperationException(); }
    
    public boolean isOneWay(@NotNull WSDLPort param1WSDLPort) { return false; }
    
    @NotNull
    public AttachmentSet getAttachments() { return getMessage().getAttachments(); }
  }
  
  private static class XmlContent extends AbstractMessageImpl implements MessageDataSource {
    private final XMLMessage.XmlDataSource dataSource;
    
    private boolean consumed;
    
    private Message delegate;
    
    private final HeaderList headerList;
    
    private WSFeatureList features;
    
    public XmlContent(String param1String, InputStream param1InputStream, WSFeatureList param1WSFeatureList) {
      super(SOAPVersion.SOAP_11);
      this.dataSource = new XMLMessage.XmlDataSource(param1String, param1InputStream);
      this.headerList = new HeaderList(SOAPVersion.SOAP_11);
      this.features = param1WSFeatureList;
    }
    
    private Message getMessage() {
      if (this.delegate == null) {
        InputStream inputStream = this.dataSource.getInputStream();
        assert inputStream != null;
        this.delegate = Messages.createUsingPayload(new StreamSource(inputStream), SOAPVersion.SOAP_11);
        this.consumed = true;
      } 
      return this.delegate;
    }
    
    public boolean hasUnconsumedDataSource() { return (!this.dataSource.consumed() && !this.consumed); }
    
    public DataSource getDataSource() { return hasUnconsumedDataSource() ? this.dataSource : XMLMessage.getDataSource(getMessage(), this.features); }
    
    public boolean hasHeaders() { return false; }
    
    @NotNull
    public MessageHeaders getHeaders() { return this.headerList; }
    
    public String getPayloadLocalPart() { return getMessage().getPayloadLocalPart(); }
    
    public String getPayloadNamespaceURI() { return getMessage().getPayloadNamespaceURI(); }
    
    public boolean hasPayload() { return true; }
    
    public boolean isFault() { return false; }
    
    public Source readEnvelopeAsSource() { return getMessage().readEnvelopeAsSource(); }
    
    public Source readPayloadAsSource() { return getMessage().readPayloadAsSource(); }
    
    public SOAPMessage readAsSOAPMessage() throws SOAPException { return getMessage().readAsSOAPMessage(); }
    
    public SOAPMessage readAsSOAPMessage(Packet param1Packet, boolean param1Boolean) throws SOAPException { return getMessage().readAsSOAPMessage(param1Packet, param1Boolean); }
    
    public <T> T readPayloadAsJAXB(Unmarshaller param1Unmarshaller) throws JAXBException { return (T)getMessage().readPayloadAsJAXB(param1Unmarshaller); }
    
    public <T> T readPayloadAsJAXB(Bridge<T> param1Bridge) throws JAXBException { return (T)getMessage().readPayloadAsJAXB(param1Bridge); }
    
    public XMLStreamReader readPayload() throws XMLStreamException { return getMessage().readPayload(); }
    
    public void writePayloadTo(XMLStreamWriter param1XMLStreamWriter) throws XMLStreamException { getMessage().writePayloadTo(param1XMLStreamWriter); }
    
    public void writeTo(XMLStreamWriter param1XMLStreamWriter) throws XMLStreamException { getMessage().writeTo(param1XMLStreamWriter); }
    
    public void writeTo(ContentHandler param1ContentHandler, ErrorHandler param1ErrorHandler) throws SAXException { getMessage().writeTo(param1ContentHandler, param1ErrorHandler); }
    
    public Message copy() { return getMessage().copy(); }
    
    protected void writePayloadTo(ContentHandler param1ContentHandler, ErrorHandler param1ErrorHandler, boolean param1Boolean) throws SAXException { throw new UnsupportedOperationException(); }
  }
  
  private static class XmlDataSource implements DataSource {
    private final String contentType;
    
    private final InputStream is;
    
    private boolean consumed;
    
    XmlDataSource(String param1String, InputStream param1InputStream) {
      this.contentType = param1String;
      this.is = param1InputStream;
    }
    
    public boolean consumed() { return this.consumed; }
    
    public InputStream getInputStream() {
      this.consumed = !this.consumed;
      return this.is;
    }
    
    public OutputStream getOutputStream() { return null; }
    
    public String getContentType() { return this.contentType; }
    
    public String getName() { return ""; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\xml\XMLMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */