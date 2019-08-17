package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.pipe.StreamSOAPCodec;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public abstract class FastInfosetStreamSOAPCodec implements Codec {
  private static final FastInfosetStreamReaderFactory READER_FACTORY = FastInfosetStreamReaderFactory.getInstance();
  
  private StAXDocumentParser _statefulParser;
  
  private StAXDocumentSerializer _serializer;
  
  private final StreamSOAPCodec _soapCodec;
  
  private final boolean _retainState;
  
  protected final ContentType _defaultContentType;
  
  FastInfosetStreamSOAPCodec(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion, boolean paramBoolean, String paramString) {
    this._soapCodec = paramStreamSOAPCodec;
    this._retainState = paramBoolean;
    this._defaultContentType = new ContentTypeImpl(paramString);
  }
  
  FastInfosetStreamSOAPCodec(FastInfosetStreamSOAPCodec paramFastInfosetStreamSOAPCodec) {
    this._soapCodec = (StreamSOAPCodec)paramFastInfosetStreamSOAPCodec._soapCodec.copy();
    this._retainState = paramFastInfosetStreamSOAPCodec._retainState;
    this._defaultContentType = paramFastInfosetStreamSOAPCodec._defaultContentType;
  }
  
  public String getMimeType() { return this._defaultContentType.getContentType(); }
  
  public ContentType getStaticContentType(Packet paramPacket) { return getContentType(paramPacket.soapAction); }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) {
    if (paramPacket.getMessage() != null) {
      XMLStreamWriter xMLStreamWriter = getXMLStreamWriter(paramOutputStream);
      try {
        paramPacket.getMessage().writeTo(xMLStreamWriter);
        xMLStreamWriter.flush();
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      } 
    } 
    return getContentType(paramPacket.soapAction);
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel) { throw new UnsupportedOperationException(); }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException { paramPacket.setMessage(this._soapCodec.decode(getXMLStreamReader(paramInputStream))); }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket) { throw new UnsupportedOperationException(); }
  
  protected abstract StreamHeader createHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer);
  
  protected abstract ContentType getContentType(String paramString);
  
  private XMLStreamWriter getXMLStreamWriter(OutputStream paramOutputStream) {
    if (this._serializer != null) {
      this._serializer.setOutputStream(paramOutputStream);
      return this._serializer;
    } 
    return this._serializer = FastInfosetCodec.createNewStreamWriter(paramOutputStream, this._retainState);
  }
  
  private XMLStreamReader getXMLStreamReader(InputStream paramInputStream) {
    if (this._retainState) {
      if (this._statefulParser != null) {
        this._statefulParser.setInputStream(paramInputStream);
        return this._statefulParser;
      } 
      return this._statefulParser = FastInfosetCodec.createNewStreamReader(paramInputStream, this._retainState);
    } 
    return READER_FACTORY.doCreate(null, paramInputStream, false);
  }
  
  public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion) { return create(paramStreamSOAPCodec, paramSOAPVersion, false); }
  
  public static FastInfosetStreamSOAPCodec create(StreamSOAPCodec paramStreamSOAPCodec, SOAPVersion paramSOAPVersion, boolean paramBoolean) {
    if (paramSOAPVersion == null)
      throw new IllegalArgumentException(); 
    switch (paramSOAPVersion) {
      case SOAP_11:
        return new FastInfosetStreamSOAP11Codec(paramStreamSOAPCodec, paramBoolean);
      case SOAP_12:
        return new FastInfosetStreamSOAP12Codec(paramStreamSOAPCodec, paramBoolean);
    } 
    throw new AssertionError();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetStreamSOAPCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */