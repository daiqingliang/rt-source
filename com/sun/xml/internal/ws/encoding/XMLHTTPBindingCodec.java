package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.encoding.xml.XMLCodec;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.StringTokenizer;
import javax.activation.DataSource;
import javax.xml.ws.WebServiceException;

public final class XMLHTTPBindingCodec extends MimeCodec {
  private static final String BASE_ACCEPT_VALUE = "*";
  
  private static final String APPLICATION_FAST_INFOSET_MIME_TYPE = "application/fastinfoset";
  
  private boolean useFastInfosetForEncoding;
  
  private final Codec xmlCodec;
  
  private final Codec fiCodec;
  
  private static final String xmlAccept = null;
  
  private static final String fiXmlAccept = "application/fastinfoset, *";
  
  private ContentTypeImpl setAcceptHeader(Packet paramPacket, ContentType paramContentType) {
    ContentTypeImpl contentTypeImpl = (ContentTypeImpl)paramContentType;
    if (paramPacket.contentNegotiation == ContentNegotiation.optimistic || paramPacket.contentNegotiation == ContentNegotiation.pessimistic) {
      contentTypeImpl.setAcceptHeader("application/fastinfoset, *");
    } else {
      contentTypeImpl.setAcceptHeader(xmlAccept);
    } 
    paramPacket.setContentType(contentTypeImpl);
    return contentTypeImpl;
  }
  
  public XMLHTTPBindingCodec(WSFeatureList paramWSFeatureList) {
    super(SOAPVersion.SOAP_11, paramWSFeatureList);
    this.xmlCodec = new XMLCodec(paramWSFeatureList);
    this.fiCodec = getFICodec();
  }
  
  public String getMimeType() { return null; }
  
  public ContentType getStaticContentType(Packet paramPacket) {
    if (paramPacket.getInternalMessage() instanceof XMLMessage.MessageDataSource) {
      XMLMessage.MessageDataSource messageDataSource = (XMLMessage.MessageDataSource)paramPacket.getInternalMessage();
      if (messageDataSource.hasUnconsumedDataSource()) {
        ContentType contentType1 = getStaticContentType(messageDataSource);
        return (contentType1 != null) ? setAcceptHeader(paramPacket, contentType1) : null;
      } 
    } 
    ContentType contentType = super.getStaticContentType(paramPacket);
    return (contentType != null) ? setAcceptHeader(paramPacket, contentType) : null;
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException {
    if (paramPacket.getInternalMessage() instanceof XMLMessage.MessageDataSource) {
      XMLMessage.MessageDataSource messageDataSource = (XMLMessage.MessageDataSource)paramPacket.getInternalMessage();
      if (messageDataSource.hasUnconsumedDataSource())
        return setAcceptHeader(paramPacket, encode(messageDataSource, paramOutputStream)); 
    } 
    return setAcceptHeader(paramPacket, super.encode(paramPacket, paramOutputStream));
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel) { throw new UnsupportedOperationException(); }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException {
    if (paramPacket.contentNegotiation == null)
      this.useFastInfosetForEncoding = false; 
    if (paramString == null) {
      this.xmlCodec.decode(paramInputStream, paramString, paramPacket);
    } else if (isMultipartRelated(paramString)) {
      paramPacket.setMessage(new XMLMessage.XMLMultiPart(paramString, paramInputStream, this.features));
    } else if (isFastInfoset(paramString)) {
      if (this.fiCodec == null)
        throw new RuntimeException(StreamingMessages.FASTINFOSET_NO_IMPLEMENTATION()); 
      this.useFastInfosetForEncoding = true;
      this.fiCodec.decode(paramInputStream, paramString, paramPacket);
    } else if (isXml(paramString)) {
      this.xmlCodec.decode(paramInputStream, paramString, paramPacket);
    } else {
      paramPacket.setMessage(new XMLMessage.UnknownContent(paramString, paramInputStream));
    } 
    if (!this.useFastInfosetForEncoding)
      this.useFastInfosetForEncoding = isFastInfosetAcceptable(paramPacket.acceptableMimeTypes); 
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket) throws IOException {}
  
  public MimeCodec copy() { return new XMLHTTPBindingCodec(this.features); }
  
  private boolean isMultipartRelated(String paramString) { return compareStrings(paramString, "multipart/related"); }
  
  private boolean isXml(String paramString) { return (compareStrings(paramString, "application/xml") || compareStrings(paramString, "text/xml") || (compareStrings(paramString, "application/") && paramString.toLowerCase().indexOf("+xml") != -1)); }
  
  private boolean isFastInfoset(String paramString) { return compareStrings(paramString, "application/fastinfoset"); }
  
  private boolean compareStrings(String paramString1, String paramString2) { return (paramString1.length() >= paramString2.length() && paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length()))); }
  
  private boolean isFastInfosetAcceptable(String paramString) {
    if (paramString == null)
      return false; 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken().trim();
      if (str.equalsIgnoreCase("application/fastinfoset"))
        return true; 
    } 
    return false;
  }
  
  private ContentType getStaticContentType(XMLMessage.MessageDataSource paramMessageDataSource) {
    String str = paramMessageDataSource.getDataSource().getContentType();
    boolean bool = XMLMessage.isFastInfoset(str);
    return !requiresTransformationOfDataSource(bool, this.useFastInfosetForEncoding) ? new ContentTypeImpl(str) : null;
  }
  
  private ContentType encode(XMLMessage.MessageDataSource paramMessageDataSource, OutputStream paramOutputStream) {
    try {
      boolean bool = XMLMessage.isFastInfoset(paramMessageDataSource.getDataSource().getContentType());
      DataSource dataSource = transformDataSource(paramMessageDataSource.getDataSource(), bool, this.useFastInfosetForEncoding, this.features);
      InputStream inputStream = dataSource.getInputStream();
      byte[] arrayOfByte = new byte[1024];
      int i;
      while ((i = inputStream.read(arrayOfByte)) != -1)
        paramOutputStream.write(arrayOfByte, 0, i); 
      return new ContentTypeImpl(dataSource.getContentType());
    } catch (IOException iOException) {
      throw new WebServiceException(iOException);
    } 
  }
  
  protected Codec getMimeRootCodec(Packet paramPacket) {
    if (paramPacket.contentNegotiation == ContentNegotiation.none) {
      this.useFastInfosetForEncoding = false;
    } else if (paramPacket.contentNegotiation == ContentNegotiation.optimistic) {
      this.useFastInfosetForEncoding = true;
    } 
    return (this.useFastInfosetForEncoding && this.fiCodec != null) ? this.fiCodec : this.xmlCodec;
  }
  
  public static boolean requiresTransformationOfDataSource(boolean paramBoolean1, boolean paramBoolean2) { return ((paramBoolean1 && !paramBoolean2) || (!paramBoolean1 && paramBoolean2)); }
  
  public static DataSource transformDataSource(DataSource paramDataSource, boolean paramBoolean1, boolean paramBoolean2, WSFeatureList paramWSFeatureList) {
    try {
      if (paramBoolean1 && !paramBoolean2) {
        XMLHTTPBindingCodec xMLHTTPBindingCodec = new XMLHTTPBindingCodec(paramWSFeatureList);
        Packet packet = new Packet();
        xMLHTTPBindingCodec.decode(paramDataSource.getInputStream(), paramDataSource.getContentType(), packet);
        packet.getMessage().getAttachments();
        xMLHTTPBindingCodec.getStaticContentType(packet);
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
        ContentType contentType = xMLHTTPBindingCodec.encode(packet, byteArrayBuffer);
        return XMLMessage.createDataSource(contentType.getContentType(), byteArrayBuffer.newInputStream());
      } 
      if (!paramBoolean1 && paramBoolean2) {
        XMLHTTPBindingCodec xMLHTTPBindingCodec = new XMLHTTPBindingCodec(paramWSFeatureList);
        Packet packet = new Packet();
        xMLHTTPBindingCodec.decode(paramDataSource.getInputStream(), paramDataSource.getContentType(), packet);
        packet.contentNegotiation = ContentNegotiation.optimistic;
        packet.getMessage().getAttachments();
        xMLHTTPBindingCodec.getStaticContentType(packet);
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer();
        ContentType contentType = xMLHTTPBindingCodec.encode(packet, byteArrayBuffer);
        return XMLMessage.createDataSource(contentType.getContentType(), byteArrayBuffer.newInputStream());
      } 
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return paramDataSource;
  }
  
  private static Codec getFICodec() {
    try {
      Class clazz = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetCodec");
      Method method = clazz.getMethod("create", new Class[0]);
      return (Codec)method.invoke(null, new Object[0]);
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\XMLHTTPBindingCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */