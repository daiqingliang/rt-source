package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.developer.SerializationFeature;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import com.sun.xml.internal.ws.util.xml.NamespaceContextExAdaper;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderFilter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;

public class MtomCodec extends MimeCodec {
  public static final String XOP_XML_MIME_TYPE = "application/xop+xml";
  
  public static final String XOP_LOCALNAME = "Include";
  
  public static final String XOP_NAMESPACEURI = "http://www.w3.org/2004/08/xop/include";
  
  private final StreamSOAPCodec codec;
  
  private final MTOMFeature mtomFeature;
  
  private final SerializationFeature sf;
  
  private static final String DECODED_MESSAGE_CHARSET = "decodedMessageCharset";
  
  MtomCodec(SOAPVersion paramSOAPVersion, StreamSOAPCodec paramStreamSOAPCodec, WSFeatureList paramWSFeatureList) {
    super(paramSOAPVersion, paramWSFeatureList);
    this.codec = paramStreamSOAPCodec;
    this.sf = (SerializationFeature)paramWSFeatureList.get(SerializationFeature.class);
    MTOMFeature mTOMFeature = (MTOMFeature)paramWSFeatureList.get(MTOMFeature.class);
    if (mTOMFeature == null) {
      this.mtomFeature = new MTOMFeature();
    } else {
      this.mtomFeature = mTOMFeature;
    } 
  }
  
  public ContentType getStaticContentType(Packet paramPacket) { return getStaticContentTypeStatic(paramPacket, this.version); }
  
  public static ContentType getStaticContentTypeStatic(Packet paramPacket, SOAPVersion paramSOAPVersion) {
    ContentType contentType = (ContentType)paramPacket.getInternalContentType();
    if (contentType != null)
      return contentType; 
    String str1 = UUID.randomUUID().toString();
    String str2 = "uuid:" + str1;
    String str3 = "<rootpart*" + str1 + "@example.jaxws.sun.com>";
    String str4 = SOAPVersion.SOAP_11.equals(paramSOAPVersion) ? null : createActionParameter(paramPacket);
    String str5 = "boundary=\"" + str2 + "\"";
    String str6 = "multipart/related;start=\"" + str3 + "\";type=\"" + "application/xop+xml" + "\";" + str5 + ";start-info=\"" + paramSOAPVersion.contentType + ((str4 == null) ? "" : str4) + "\"";
    if (SOAPVersion.SOAP_11.equals(paramSOAPVersion)) {
    
    } else {
    
    } 
    ContentTypeImpl contentTypeImpl = new ContentTypeImpl(str6, null, null);
    contentTypeImpl.setBoundary(str2);
    contentTypeImpl.setRootId(str3);
    paramPacket.setContentType(contentTypeImpl);
    return contentTypeImpl;
  }
  
  private static String createActionParameter(Packet paramPacket) { return (paramPacket.soapAction != null) ? (";action=\\\"" + paramPacket.soapAction + "\\\"") : ""; }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException {
    ContentTypeImpl contentTypeImpl = (ContentTypeImpl)getStaticContentType(paramPacket);
    String str1 = contentTypeImpl.getBoundary();
    String str2 = contentTypeImpl.getRootId();
    if (paramPacket.getMessage() != null)
      try {
        String str3 = getPacketEncoding(paramPacket);
        paramPacket.invocationProperties.remove("decodedMessageCharset");
        String str4 = getActionParameter(paramPacket, this.version);
        String str5 = getSOAPXopContentType(str3, this.version, str4);
        writeln("--" + str1, paramOutputStream);
        writeMimeHeaders(str5, str2, paramOutputStream);
        ArrayList arrayList = new ArrayList();
        MtomStreamWriterImpl mtomStreamWriterImpl = new MtomStreamWriterImpl(XMLStreamWriterFactory.create(paramOutputStream, str3), arrayList, str1, this.mtomFeature);
        paramPacket.getMessage().writeTo(mtomStreamWriterImpl);
        XMLStreamWriterFactory.recycle(mtomStreamWriterImpl);
        writeln(paramOutputStream);
        for (ByteArrayBuffer byteArrayBuffer : arrayList)
          byteArrayBuffer.write(paramOutputStream); 
        writeNonMtomAttachments(paramPacket.getMessage().getAttachments(), paramOutputStream, str1);
        writeAsAscii("--" + str1, paramOutputStream);
        writeAsAscii("--", paramOutputStream);
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      }  
    return contentTypeImpl;
  }
  
  public static String getSOAPXopContentType(String paramString1, SOAPVersion paramSOAPVersion, String paramString2) { return "application/xop+xml;charset=" + paramString1 + ";type=\"" + paramSOAPVersion.contentType + paramString2 + "\""; }
  
  public static String getActionParameter(Packet paramPacket, SOAPVersion paramSOAPVersion) { return (paramSOAPVersion == SOAPVersion.SOAP_11) ? "" : createActionParameter(paramPacket); }
  
  public static void writeMimeHeaders(String paramString1, String paramString2, OutputStream paramOutputStream) throws IOException {
    String str = paramString2;
    if (str != null && str.length() > 0 && str.charAt(0) != '<')
      str = '<' + str + '>'; 
    writeln("Content-Id: " + str, paramOutputStream);
    writeln("Content-Type: " + paramString1, paramOutputStream);
    writeln("Content-Transfer-Encoding: binary", paramOutputStream);
    writeln(paramOutputStream);
  }
  
  private void writeNonMtomAttachments(AttachmentSet paramAttachmentSet, OutputStream paramOutputStream, String paramString) throws IOException {
    for (Attachment attachment : paramAttachmentSet) {
      DataHandler dataHandler = attachment.asDataHandler();
      if (dataHandler instanceof StreamingDataHandler) {
        StreamingDataHandler streamingDataHandler = (StreamingDataHandler)dataHandler;
        if (streamingDataHandler.getHrefCid() != null)
          continue; 
      } 
      writeln("--" + paramString, paramOutputStream);
      writeMimeHeaders(attachment.getContentType(), attachment.getContentId(), paramOutputStream);
      attachment.writeTo(paramOutputStream);
      writeln(paramOutputStream);
    } 
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel) { throw new UnsupportedOperationException(); }
  
  public MtomCodec copy() { return new MtomCodec(this.version, (StreamSOAPCodec)this.codec.copy(), this.features); }
  
  private static String encodeCid() {
    String str1 = "example.jaxws.sun.com";
    String str2 = UUID.randomUUID() + "@";
    return str2 + str1;
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket) throws IOException {
    String str1 = null;
    String str2 = paramMimeMultipartParser.getRootPart().getContentType();
    if (str2 != null)
      str1 = (new ContentTypeImpl(str2)).getCharSet(); 
    if (str1 != null && !Charset.isSupported(str1))
      throw new UnsupportedMediaException(str1); 
    if (str1 != null) {
      paramPacket.invocationProperties.put("decodedMessageCharset", str1);
    } else {
      paramPacket.invocationProperties.remove("decodedMessageCharset");
    } 
    MtomXMLStreamReaderEx mtomXMLStreamReaderEx = new MtomXMLStreamReaderEx(paramMimeMultipartParser, XMLStreamReaderFactory.create(null, paramMimeMultipartParser.getRootPart().asInputStream(), str1, true));
    paramPacket.setMessage(this.codec.decode(mtomXMLStreamReaderEx, new MimeAttachmentSet(paramMimeMultipartParser)));
    paramPacket.setMtomFeature(this.mtomFeature);
    paramPacket.setContentType(paramMimeMultipartParser.getContentType());
  }
  
  private String getPacketEncoding(Packet paramPacket) { return (this.sf != null && this.sf.getEncoding() != null) ? (this.sf.getEncoding().equals("") ? "utf-8" : this.sf.getEncoding()) : determinePacketEncoding(paramPacket); }
  
  public static String determinePacketEncoding(Packet paramPacket) {
    if (paramPacket != null && paramPacket.endpoint != null) {
      String str = (String)paramPacket.invocationProperties.get("decodedMessageCharset");
      return (str == null) ? "utf-8" : str;
    } 
    return "utf-8";
  }
  
  public static class ByteArrayBuffer {
    final String contentId;
    
    private final DataHandler dh;
    
    private final String boundary;
    
    ByteArrayBuffer(@NotNull DataHandler param1DataHandler, String param1String) {
      this.dh = param1DataHandler;
      String str = null;
      if (param1DataHandler instanceof StreamingDataHandler) {
        StreamingDataHandler streamingDataHandler = (StreamingDataHandler)param1DataHandler;
        if (streamingDataHandler.getHrefCid() != null)
          str = streamingDataHandler.getHrefCid(); 
      } 
      this.contentId = (str != null) ? str : MtomCodec.encodeCid();
      this.boundary = param1String;
    }
    
    public void write(OutputStream param1OutputStream) throws IOException {
      MimeCodec.writeln("--" + this.boundary, param1OutputStream);
      MtomCodec.writeMimeHeaders(this.dh.getContentType(), this.contentId, param1OutputStream);
      this.dh.writeTo(param1OutputStream);
      MimeCodec.writeln(param1OutputStream);
    }
  }
  
  public static class MtomStreamWriterImpl extends XMLStreamWriterFilter implements XMLStreamWriterEx, MtomStreamWriter, HasEncoding {
    private final List<MtomCodec.ByteArrayBuffer> mtomAttachments;
    
    private final String boundary;
    
    private final MTOMFeature myMtomFeature;
    
    public MtomStreamWriterImpl(XMLStreamWriter param1XMLStreamWriter, List<MtomCodec.ByteArrayBuffer> param1List, String param1String, MTOMFeature param1MTOMFeature) {
      super(param1XMLStreamWriter);
      this.mtomAttachments = param1List;
      this.boundary = param1String;
      this.myMtomFeature = param1MTOMFeature;
    }
    
    public void writeBinary(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, String param1String) throws XMLStreamException {
      if (this.myMtomFeature.getThreshold() > param1Int2) {
        writeCharacters(DatatypeConverterImpl._printBase64Binary(param1ArrayOfByte, param1Int1, param1Int2));
        return;
      } 
      MtomCodec.ByteArrayBuffer byteArrayBuffer = new MtomCodec.ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(param1ArrayOfByte, param1Int1, param1Int2, param1String)), this.boundary);
      writeBinary(byteArrayBuffer);
    }
    
    public void writeBinary(DataHandler param1DataHandler) throws XMLStreamException { writeBinary(new MtomCodec.ByteArrayBuffer(param1DataHandler, this.boundary)); }
    
    public OutputStream writeBinary(String param1String) throws XMLStreamException { throw new UnsupportedOperationException(); }
    
    public void writePCDATA(CharSequence param1CharSequence) throws XMLStreamException {
      if (param1CharSequence == null)
        return; 
      if (param1CharSequence instanceof Base64Data) {
        Base64Data base64Data = (Base64Data)param1CharSequence;
        writeBinary(base64Data.getDataHandler());
        return;
      } 
      writeCharacters(param1CharSequence.toString());
    }
    
    private void writeBinary(MtomCodec.ByteArrayBuffer param1ByteArrayBuffer) {
      try {
        this.mtomAttachments.add(param1ByteArrayBuffer);
        String str = this.writer.getPrefix("http://www.w3.org/2004/08/xop/include");
        if (str == null || !str.equals("xop")) {
          this.writer.setPrefix("xop", "http://www.w3.org/2004/08/xop/include");
          this.writer.writeNamespace("xop", "http://www.w3.org/2004/08/xop/include");
        } 
        this.writer.writeStartElement("http://www.w3.org/2004/08/xop/include", "Include");
        this.writer.writeAttribute("href", "cid:" + param1ByteArrayBuffer.contentId);
        this.writer.writeEndElement();
        this.writer.flush();
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      } 
    }
    
    public Object getProperty(String param1String) throws IllegalArgumentException {
      if (param1String.equals("sjsxp-outputstream") && this.writer instanceof Map) {
        Object object = ((Map)this.writer).get("sjsxp-outputstream");
        if (object != null)
          return object; 
      } 
      return super.getProperty(param1String);
    }
    
    public AttachmentMarshaller getAttachmentMarshaller() { return new AttachmentMarshaller() {
          public String addMtomAttachment(DataHandler param2DataHandler, String param2String1, String param2String2) {
            MtomCodec.ByteArrayBuffer byteArrayBuffer = new MtomCodec.ByteArrayBuffer(param2DataHandler, MtomCodec.MtomStreamWriterImpl.this.boundary);
            MtomCodec.MtomStreamWriterImpl.this.mtomAttachments.add(byteArrayBuffer);
            return "cid:" + byteArrayBuffer.contentId;
          }
          
          public String addMtomAttachment(byte[] param2ArrayOfByte, int param2Int1, int param2Int2, String param2String1, String param2String2, String param2String3) {
            if (MtomCodec.MtomStreamWriterImpl.this.myMtomFeature.getThreshold() > param2Int2)
              return null; 
            MtomCodec.ByteArrayBuffer byteArrayBuffer = new MtomCodec.ByteArrayBuffer(new DataHandler(new ByteArrayDataSource(param2ArrayOfByte, param2Int1, param2Int2, param2String1)), MtomCodec.MtomStreamWriterImpl.this.boundary);
            MtomCodec.MtomStreamWriterImpl.this.mtomAttachments.add(byteArrayBuffer);
            return "cid:" + byteArrayBuffer.contentId;
          }
          
          public String addSwaRefAttachment(DataHandler param2DataHandler) {
            MtomCodec.ByteArrayBuffer byteArrayBuffer = new MtomCodec.ByteArrayBuffer(param2DataHandler, MtomCodec.MtomStreamWriterImpl.this.boundary);
            MtomCodec.MtomStreamWriterImpl.this.mtomAttachments.add(byteArrayBuffer);
            return "cid:" + byteArrayBuffer.contentId;
          }
          
          public boolean isXOPPackage() { return true; }
        }; }
    
    public List<MtomCodec.ByteArrayBuffer> getMtomAttachments() { return this.mtomAttachments; }
    
    public String getEncoding() { return XMLStreamWriterUtil.getEncoding(this.writer); }
    
    public NamespaceContextEx getNamespaceContext() {
      NamespaceContext namespaceContext = this.writer.getNamespaceContext();
      return new MtomNamespaceContextEx(namespaceContext);
    }
    
    private static class MtomNamespaceContextEx implements NamespaceContextEx {
      private final NamespaceContext nsContext;
      
      public MtomNamespaceContextEx(NamespaceContext param2NamespaceContext) { this.nsContext = param2NamespaceContext; }
      
      public Iterator<NamespaceContextEx.Binding> iterator() { throw new UnsupportedOperationException(); }
      
      public String getNamespaceURI(String param2String) { return this.nsContext.getNamespaceURI(param2String); }
      
      public String getPrefix(String param2String) { return this.nsContext.getPrefix(param2String); }
      
      public Iterator getPrefixes(String param2String) { return this.nsContext.getPrefixes(param2String); }
    }
  }
  
  public static class MtomXMLStreamReaderEx extends XMLStreamReaderFilter implements XMLStreamReaderEx {
    private final MimeMultipartParser mimeMP;
    
    private boolean xopReferencePresent = false;
    
    private Base64Data base64AttData;
    
    private char[] base64EncodedText;
    
    private String xopHref;
    
    public MtomXMLStreamReaderEx(MimeMultipartParser param1MimeMultipartParser, XMLStreamReader param1XMLStreamReader) {
      super(param1XMLStreamReader);
      this.mimeMP = param1MimeMultipartParser;
    }
    
    public CharSequence getPCDATA() throws XMLStreamException { return this.xopReferencePresent ? this.base64AttData : this.reader.getText(); }
    
    public NamespaceContextEx getNamespaceContext() { return new NamespaceContextExAdaper(this.reader.getNamespaceContext()); }
    
    public String getElementTextTrim() { throw new UnsupportedOperationException(); }
    
    public int getTextLength() { return this.xopReferencePresent ? this.base64AttData.length() : this.reader.getTextLength(); }
    
    public int getTextStart() { return this.xopReferencePresent ? 0 : this.reader.getTextStart(); }
    
    public int getEventType() { return this.xopReferencePresent ? 4 : super.getEventType(); }
    
    public int next() {
      int i = this.reader.next();
      if (i == 1 && this.reader.getLocalName().equals("Include") && this.reader.getNamespaceURI().equals("http://www.w3.org/2004/08/xop/include")) {
        String str = this.reader.getAttributeValue(null, "href");
        try {
          this.xopHref = str;
          Attachment attachment = getAttachment(str);
          if (attachment != null) {
            DataHandler dataHandler = attachment.asDataHandler();
            if (dataHandler instanceof StreamingDataHandler)
              ((StreamingDataHandler)dataHandler).setHrefCid(attachment.getContentId()); 
            this.base64AttData = new Base64Data();
            this.base64AttData.set(dataHandler);
          } 
          this.xopReferencePresent = true;
        } catch (IOException iOException) {
          throw new WebServiceException(iOException);
        } 
        XMLStreamReaderUtil.nextElementContent(this.reader);
        return 4;
      } 
      if (this.xopReferencePresent) {
        this.xopReferencePresent = false;
        this.base64EncodedText = null;
        this.xopHref = null;
      } 
      return i;
    }
    
    private String decodeCid(String param1String) {
      try {
        param1String = URLDecoder.decode(param1String, "utf-8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {}
      return param1String;
    }
    
    private Attachment getAttachment(String param1String) throws IOException {
      if (param1String.startsWith("cid:"))
        param1String = param1String.substring(4, param1String.length()); 
      if (param1String.indexOf('%') != -1) {
        param1String = decodeCid(param1String);
        return this.mimeMP.getAttachmentPart(param1String);
      } 
      return this.mimeMP.getAttachmentPart(param1String);
    }
    
    public char[] getTextCharacters() {
      if (this.xopReferencePresent) {
        char[] arrayOfChar = new char[this.base64AttData.length()];
        this.base64AttData.writeTo(arrayOfChar, 0);
        return arrayOfChar;
      } 
      return this.reader.getTextCharacters();
    }
    
    public int getTextCharacters(int param1Int1, char[] param1ArrayOfChar, int param1Int2, int param1Int3) throws XMLStreamException {
      if (this.xopReferencePresent) {
        if (param1ArrayOfChar == null)
          throw new NullPointerException("target char array can't be null"); 
        if (param1Int2 < 0 || param1Int3 < 0 || param1Int1 < 0 || param1Int2 >= param1ArrayOfChar.length || param1Int2 + param1Int3 > param1ArrayOfChar.length)
          throw new IndexOutOfBoundsException(); 
        int i = this.base64AttData.length();
        if (param1Int1 > i)
          throw new IndexOutOfBoundsException(); 
        if (this.base64EncodedText == null) {
          this.base64EncodedText = new char[this.base64AttData.length()];
          this.base64AttData.writeTo(this.base64EncodedText, 0);
        } 
        int j = Math.min(i - param1Int1, param1Int3);
        System.arraycopy(this.base64EncodedText, param1Int1, param1ArrayOfChar, param1Int2, j);
        return j;
      } 
      return this.reader.getTextCharacters(param1Int1, param1ArrayOfChar, param1Int2, param1Int3);
    }
    
    public String getText() { return this.xopReferencePresent ? this.base64AttData.toString() : this.reader.getText(); }
    
    protected boolean isXopReference() { return this.xopReferencePresent; }
    
    protected String getXopHref() { return this.xopHref; }
    
    public MimeMultipartParser getMimeMultipartParser() { return this.mimeMP; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\MtomCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */