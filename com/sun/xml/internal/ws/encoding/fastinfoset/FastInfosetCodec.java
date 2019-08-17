package com.sun.xml.internal.ws.encoding.fastinfoset;

import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.encoding.ContentTypeImpl;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;

public class FastInfosetCodec implements Codec {
  private static final int DEFAULT_INDEXED_STRING_SIZE_LIMIT = 32;
  
  private static final int DEFAULT_INDEXED_STRING_MEMORY_LIMIT = 4194304;
  
  private StAXDocumentParser _parser;
  
  private StAXDocumentSerializer _serializer;
  
  private final boolean _retainState;
  
  private final ContentType _contentType;
  
  FastInfosetCodec(boolean paramBoolean) {
    this._retainState = paramBoolean;
    this._contentType = paramBoolean ? new ContentTypeImpl("application/vnd.sun.stateful.fastinfoset") : new ContentTypeImpl("application/fastinfoset");
  }
  
  public String getMimeType() { return this._contentType.getContentType(); }
  
  public Codec copy() { return new FastInfosetCodec(this._retainState); }
  
  public ContentType getStaticContentType(Packet paramPacket) { return this._contentType; }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) {
    Message message = paramPacket.getMessage();
    if (message != null && message.hasPayload()) {
      XMLStreamWriter xMLStreamWriter = getXMLStreamWriter(paramOutputStream);
      try {
        xMLStreamWriter.writeStartDocument();
        paramPacket.getMessage().writePayloadTo(xMLStreamWriter);
        xMLStreamWriter.writeEndDocument();
        xMLStreamWriter.flush();
      } catch (XMLStreamException xMLStreamException) {
        throw new WebServiceException(xMLStreamException);
      } 
    } 
    return this._contentType;
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel) { throw new UnsupportedOperationException(); }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException {
    Message message;
    paramInputStream = hasSomeData(paramInputStream);
    if (paramInputStream != null) {
      message = Messages.createUsingPayload(new FastInfosetSource(paramInputStream), SOAPVersion.SOAP_11);
    } else {
      message = Messages.createEmpty(SOAPVersion.SOAP_11);
    } 
    paramPacket.setMessage(message);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket) { throw new UnsupportedOperationException(); }
  
  private XMLStreamWriter getXMLStreamWriter(OutputStream paramOutputStream) {
    if (this._serializer != null) {
      this._serializer.setOutputStream(paramOutputStream);
      return this._serializer;
    } 
    return this._serializer = createNewStreamWriter(paramOutputStream, this._retainState);
  }
  
  public static FastInfosetCodec create() { return create(false); }
  
  public static FastInfosetCodec create(boolean paramBoolean) { return new FastInfosetCodec(paramBoolean); }
  
  static StAXDocumentSerializer createNewStreamWriter(OutputStream paramOutputStream, boolean paramBoolean) { return createNewStreamWriter(paramOutputStream, paramBoolean, 32, 4194304); }
  
  static StAXDocumentSerializer createNewStreamWriter(OutputStream paramOutputStream, boolean paramBoolean, int paramInt1, int paramInt2) {
    StAXDocumentSerializer stAXDocumentSerializer = new StAXDocumentSerializer(paramOutputStream);
    if (paramBoolean) {
      SerializerVocabulary serializerVocabulary = new SerializerVocabulary();
      stAXDocumentSerializer.setVocabulary(serializerVocabulary);
      stAXDocumentSerializer.setMinAttributeValueSize(0);
      stAXDocumentSerializer.setMaxAttributeValueSize(paramInt1);
      stAXDocumentSerializer.setMinCharacterContentChunkSize(0);
      stAXDocumentSerializer.setMaxCharacterContentChunkSize(paramInt1);
      stAXDocumentSerializer.setAttributeValueMapMemoryLimit(paramInt2);
      stAXDocumentSerializer.setCharacterContentChunkMapMemoryLimit(paramInt2);
    } 
    return stAXDocumentSerializer;
  }
  
  static StAXDocumentParser createNewStreamReader(InputStream paramInputStream, boolean paramBoolean) {
    StAXDocumentParser stAXDocumentParser = new StAXDocumentParser(paramInputStream);
    stAXDocumentParser.setStringInterning(true);
    if (paramBoolean) {
      ParserVocabulary parserVocabulary = new ParserVocabulary();
      stAXDocumentParser.setVocabulary(parserVocabulary);
    } 
    return stAXDocumentParser;
  }
  
  static StAXDocumentParser createNewStreamReaderRecyclable(InputStream paramInputStream, boolean paramBoolean) {
    FastInfosetStreamReaderRecyclable fastInfosetStreamReaderRecyclable = new FastInfosetStreamReaderRecyclable(paramInputStream);
    fastInfosetStreamReaderRecyclable.setStringInterning(true);
    fastInfosetStreamReaderRecyclable.setForceStreamClose(true);
    if (paramBoolean) {
      ParserVocabulary parserVocabulary = new ParserVocabulary();
      fastInfosetStreamReaderRecyclable.setVocabulary(parserVocabulary);
    } 
    return fastInfosetStreamReaderRecyclable;
  }
  
  private static InputStream hasSomeData(InputStream paramInputStream) throws IOException {
    if (paramInputStream != null && paramInputStream.available() < 1) {
      if (!paramInputStream.markSupported())
        paramInputStream = new BufferedInputStream(paramInputStream); 
      paramInputStream.mark(1);
      if (paramInputStream.read() != -1) {
        paramInputStream.reset();
      } else {
        paramInputStream = null;
      } 
    } 
    return paramInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\fastinfoset\FastInfosetCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */