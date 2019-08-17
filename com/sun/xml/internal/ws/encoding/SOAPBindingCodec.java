package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.SOAPBindingCodec;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.protocol.soap.MessageCreationException;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class SOAPBindingCodec extends MimeCodec implements SOAPBindingCodec {
  public static final String UTF8_ENCODING = "utf-8";
  
  public static final String DEFAULT_ENCODING = "utf-8";
  
  private boolean isFastInfosetDisabled;
  
  private boolean useFastInfosetForEncoding;
  
  private boolean ignoreContentNegotiationProperty;
  
  private final StreamSOAPCodec xmlSoapCodec;
  
  private final Codec fiSoapCodec;
  
  private final MimeCodec xmlMtomCodec;
  
  private final MimeCodec xmlSwaCodec;
  
  private final MimeCodec fiSwaCodec;
  
  private final String xmlMimeType;
  
  private final String fiMimeType;
  
  private final String xmlAccept;
  
  private final String connegXmlAccept;
  
  public StreamSOAPCodec getXMLCodec() { return this.xmlSoapCodec; }
  
  private ContentTypeImpl setAcceptHeader(Packet paramPacket, ContentTypeImpl paramContentTypeImpl) {
    String str;
    if (!this.ignoreContentNegotiationProperty && paramPacket.contentNegotiation != ContentNegotiation.none) {
      str = this.connegXmlAccept;
    } else {
      str = this.xmlAccept;
    } 
    paramContentTypeImpl.setAcceptHeader(str);
    return paramContentTypeImpl;
  }
  
  public SOAPBindingCodec(WSFeatureList paramWSFeatureList) { this(paramWSFeatureList, Codecs.createSOAPEnvelopeXmlCodec(paramWSFeatureList)); }
  
  public SOAPBindingCodec(WSFeatureList paramWSFeatureList, StreamSOAPCodec paramStreamSOAPCodec) {
    super(WebServiceFeatureList.getSoapVersion(paramWSFeatureList), paramWSFeatureList);
    this.xmlSoapCodec = paramStreamSOAPCodec;
    this.xmlMimeType = paramStreamSOAPCodec.getMimeType();
    this.xmlMtomCodec = new MtomCodec(this.version, paramStreamSOAPCodec, paramWSFeatureList);
    this.xmlSwaCodec = new SwACodec(this.version, paramWSFeatureList, paramStreamSOAPCodec);
    String str = paramStreamSOAPCodec.getMimeType() + ", " + this.xmlMtomCodec.getMimeType();
    WebServiceFeature webServiceFeature = paramWSFeatureList.get(com.sun.xml.internal.ws.api.fastinfoset.FastInfosetFeature.class);
    this.isFastInfosetDisabled = (webServiceFeature != null && !webServiceFeature.isEnabled());
    if (!this.isFastInfosetDisabled) {
      this.fiSoapCodec = getFICodec(paramStreamSOAPCodec, this.version);
      if (this.fiSoapCodec != null) {
        this.fiMimeType = this.fiSoapCodec.getMimeType();
        this.fiSwaCodec = new SwACodec(this.version, paramWSFeatureList, this.fiSoapCodec);
        this.connegXmlAccept = this.fiMimeType + ", " + str;
        WebServiceFeature webServiceFeature1 = paramWSFeatureList.get(com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature.class);
        if (webServiceFeature1 != null) {
          this.ignoreContentNegotiationProperty = true;
          if (webServiceFeature1.isEnabled()) {
            if (webServiceFeature != null)
              this.useFastInfosetForEncoding = true; 
            str = this.connegXmlAccept;
          } else {
            this.isFastInfosetDisabled = true;
          } 
        } 
      } else {
        this.isFastInfosetDisabled = true;
        this.fiSwaCodec = null;
        this.fiMimeType = "";
        this.connegXmlAccept = str;
        this.ignoreContentNegotiationProperty = true;
      } 
    } else {
      this.fiSoapCodec = this.fiSwaCodec = null;
      this.fiMimeType = "";
      this.connegXmlAccept = str;
      this.ignoreContentNegotiationProperty = true;
    } 
    this.xmlAccept = str;
    if (WebServiceFeatureList.getSoapVersion(paramWSFeatureList) == null)
      throw new WebServiceException("Expecting a SOAP binding but found "); 
  }
  
  public String getMimeType() { return null; }
  
  public ContentType getStaticContentType(Packet paramPacket) {
    ContentType contentType = getEncoder(paramPacket).getStaticContentType(paramPacket);
    return setAcceptHeader(paramPacket, (ContentTypeImpl)contentType);
  }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException {
    preEncode(paramPacket);
    ContentType contentType = getEncoder(paramPacket).encode(paramPacket, paramOutputStream);
    contentType = setAcceptHeader(paramPacket, (ContentTypeImpl)contentType);
    postEncode();
    return contentType;
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel) {
    preEncode(paramPacket);
    ContentType contentType = getEncoder(paramPacket).encode(paramPacket, paramWritableByteChannel);
    contentType = setAcceptHeader(paramPacket, (ContentTypeImpl)contentType);
    postEncode();
    return contentType;
  }
  
  private void preEncode(Packet paramPacket) {}
  
  private void postEncode() {}
  
  private void preDecode(Packet paramPacket) {
    if (paramPacket.contentNegotiation == null)
      this.useFastInfosetForEncoding = false; 
  }
  
  private void postDecode(Packet paramPacket) {
    paramPacket.setFastInfosetDisabled(this.isFastInfosetDisabled);
    if (this.features.isEnabled(MTOMFeature.class))
      paramPacket.checkMtomAcceptable(); 
    MTOMFeature mTOMFeature = (MTOMFeature)this.features.get(MTOMFeature.class);
    if (mTOMFeature != null)
      paramPacket.setMtomFeature(mTOMFeature); 
    if (!this.useFastInfosetForEncoding)
      this.useFastInfosetForEncoding = paramPacket.getFastInfosetAcceptable(this.fiMimeType).booleanValue(); 
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException {
    if (paramString == null)
      paramString = this.xmlMimeType; 
    paramPacket.setContentType(new ContentTypeImpl(paramString));
    preDecode(paramPacket);
    try {
      if (isMultipartRelated(paramString)) {
        super.decode(paramInputStream, paramString, paramPacket);
      } else if (isFastInfoset(paramString)) {
        if (!this.ignoreContentNegotiationProperty && paramPacket.contentNegotiation == ContentNegotiation.none)
          throw noFastInfosetForDecoding(); 
        this.useFastInfosetForEncoding = true;
        this.fiSoapCodec.decode(paramInputStream, paramString, paramPacket);
      } else {
        this.xmlSoapCodec.decode(paramInputStream, paramString, paramPacket);
      } 
    } catch (RuntimeException runtimeException) {
      if (runtimeException instanceof com.sun.xml.internal.ws.api.message.ExceptionHasMessage || runtimeException instanceof UnsupportedMediaException)
        throw runtimeException; 
      throw new MessageCreationException(this.version, new Object[] { runtimeException });
    } 
    postDecode(paramPacket);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket) {
    if (paramString == null)
      throw new UnsupportedMediaException(); 
    preDecode(paramPacket);
    try {
      if (isMultipartRelated(paramString)) {
        super.decode(paramReadableByteChannel, paramString, paramPacket);
      } else if (isFastInfoset(paramString)) {
        if (paramPacket.contentNegotiation == ContentNegotiation.none)
          throw noFastInfosetForDecoding(); 
        this.useFastInfosetForEncoding = true;
        this.fiSoapCodec.decode(paramReadableByteChannel, paramString, paramPacket);
      } else {
        this.xmlSoapCodec.decode(paramReadableByteChannel, paramString, paramPacket);
      } 
    } catch (RuntimeException runtimeException) {
      if (runtimeException instanceof com.sun.xml.internal.ws.api.message.ExceptionHasMessage || runtimeException instanceof UnsupportedMediaException)
        throw runtimeException; 
      throw new MessageCreationException(this.version, new Object[] { runtimeException });
    } 
    postDecode(paramPacket);
  }
  
  public SOAPBindingCodec copy() { return new SOAPBindingCodec(this.features, (StreamSOAPCodec)this.xmlSoapCodec.copy()); }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket) throws IOException {
    String str = paramMimeMultipartParser.getRootPart().getContentType();
    boolean bool = isApplicationXopXml(str);
    paramPacket.setMtomRequest(Boolean.valueOf(bool));
    if (bool) {
      this.xmlMtomCodec.decode(paramMimeMultipartParser, paramPacket);
    } else if (isFastInfoset(str)) {
      if (paramPacket.contentNegotiation == ContentNegotiation.none)
        throw noFastInfosetForDecoding(); 
      this.useFastInfosetForEncoding = true;
      this.fiSwaCodec.decode(paramMimeMultipartParser, paramPacket);
    } else if (isXml(str)) {
      this.xmlSwaCodec.decode(paramMimeMultipartParser, paramPacket);
    } else {
      throw new IOException("");
    } 
  }
  
  private boolean isMultipartRelated(String paramString) { return compareStrings(paramString, "multipart/related"); }
  
  private boolean isApplicationXopXml(String paramString) { return compareStrings(paramString, "application/xop+xml"); }
  
  private boolean isXml(String paramString) { return compareStrings(paramString, this.xmlMimeType); }
  
  private boolean isFastInfoset(String paramString) { return this.isFastInfosetDisabled ? false : compareStrings(paramString, this.fiMimeType); }
  
  private boolean compareStrings(String paramString1, String paramString2) { return (paramString1.length() >= paramString2.length() && paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length()))); }
  
  private Codec getEncoder(Packet paramPacket) {
    if (!this.ignoreContentNegotiationProperty)
      if (paramPacket.contentNegotiation == ContentNegotiation.none) {
        this.useFastInfosetForEncoding = false;
      } else if (paramPacket.contentNegotiation == ContentNegotiation.optimistic) {
        this.useFastInfosetForEncoding = true;
      }  
    if (this.useFastInfosetForEncoding) {
      Message message1 = paramPacket.getMessage();
      return (message1 == null || message1.getAttachments().isEmpty() || this.features.isEnabled(MTOMFeature.class)) ? this.fiSoapCodec : this.fiSwaCodec;
    } 
    if (paramPacket.getBinding() == null && this.features != null)
      paramPacket.setMtomFeature((MTOMFeature)this.features.get(MTOMFeature.class)); 
    if (paramPacket.shouldUseMtom())
      return this.xmlMtomCodec; 
    Message message = paramPacket.getMessage();
    return (message == null || message.getAttachments().isEmpty()) ? this.xmlSoapCodec : this.xmlSwaCodec;
  }
  
  private RuntimeException noFastInfosetForDecoding() { return new RuntimeException(StreamingMessages.FASTINFOSET_DECODING_NOT_ACCEPTED()); }
  
  private static Codec getFICodec(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion) {
    try {
      Class clazz = Class.forName("com.sun.xml.internal.ws.encoding.fastinfoset.FastInfosetStreamSOAPCodec");
      Method method = clazz.getMethod("create", new Class[] { StreamSOAPCodec.class, SOAPVersion.class });
      return (Codec)method.invoke(null, new Object[] { paramStreamSOAPCodec, paramSOAPVersion });
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\SOAPBindingCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */