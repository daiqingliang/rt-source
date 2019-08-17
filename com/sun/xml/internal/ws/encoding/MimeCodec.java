package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.UUID;

abstract class MimeCodec implements Codec {
  public static final String MULTIPART_RELATED_MIME_TYPE = "multipart/related";
  
  protected Codec mimeRootCodec;
  
  protected final SOAPVersion version;
  
  protected final WSFeatureList features;
  
  protected MimeCodec(SOAPVersion paramSOAPVersion, WSFeatureList paramWSFeatureList) {
    this.version = paramSOAPVersion;
    this.features = paramWSFeatureList;
  }
  
  public String getMimeType() { return "multipart/related"; }
  
  protected Codec getMimeRootCodec(Packet paramPacket) { return this.mimeRootCodec; }
  
  public ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException {
    Message message = paramPacket.getMessage();
    if (message == null)
      return null; 
    ContentTypeImpl contentTypeImpl = (ContentTypeImpl)getStaticContentType(paramPacket);
    String str = contentTypeImpl.getBoundary();
    boolean bool = (str != null) ? 1 : 0;
    Codec codec = getMimeRootCodec(paramPacket);
    if (bool) {
      writeln("--" + str, paramOutputStream);
      ContentType contentType1 = codec.getStaticContentType(paramPacket);
      String str1 = (contentType1 != null) ? contentType1.getContentType() : codec.getMimeType();
      writeln("Content-Type: " + str1, paramOutputStream);
      writeln(paramOutputStream);
    } 
    ContentType contentType = codec.encode(paramPacket, paramOutputStream);
    if (bool) {
      writeln(paramOutputStream);
      for (Attachment attachment : message.getAttachments()) {
        writeln("--" + str, paramOutputStream);
        String str1 = attachment.getContentId();
        if (str1 != null && str1.length() > 0 && str1.charAt(0) != '<')
          str1 = '<' + str1 + '>'; 
        writeln("Content-Id:" + str1, paramOutputStream);
        writeln("Content-Type: " + attachment.getContentType(), paramOutputStream);
        writeCustomMimeHeaders(attachment, paramOutputStream);
        writeln("Content-Transfer-Encoding: binary", paramOutputStream);
        writeln(paramOutputStream);
        attachment.writeTo(paramOutputStream);
        writeln(paramOutputStream);
      } 
      writeAsAscii("--" + str, paramOutputStream);
      writeAsAscii("--", paramOutputStream);
    } 
    return bool ? contentTypeImpl : contentType;
  }
  
  private void writeCustomMimeHeaders(Attachment paramAttachment, OutputStream paramOutputStream) throws IOException {
    if (paramAttachment instanceof AttachmentEx) {
      Iterator iterator = ((AttachmentEx)paramAttachment).getMimeHeaders();
      while (iterator.hasNext()) {
        AttachmentEx.MimeHeader mimeHeader = (AttachmentEx.MimeHeader)iterator.next();
        String str = mimeHeader.getName();
        if (!"Content-Type".equalsIgnoreCase(str) && !"Content-Id".equalsIgnoreCase(str))
          writeln(str + ": " + mimeHeader.getValue(), paramOutputStream); 
      } 
    } 
  }
  
  public ContentType getStaticContentType(Packet paramPacket) {
    ContentType contentType = (ContentType)paramPacket.getInternalContentType();
    if (contentType != null)
      return contentType; 
    Message message = paramPacket.getMessage();
    boolean bool = !message.getAttachments().isEmpty() ? 1 : 0;
    Codec codec = getMimeRootCodec(paramPacket);
    if (bool) {
      String str1 = "uuid:" + UUID.randomUUID().toString();
      String str2 = "boundary=\"" + str1 + "\"";
      String str3 = "multipart/related; type=\"" + codec.getMimeType() + "\"; " + str2;
      ContentTypeImpl contentTypeImpl = new ContentTypeImpl(str3, paramPacket.soapAction, null);
      contentTypeImpl.setBoundary(str1);
      contentTypeImpl.setBoundaryParameter(str2);
      paramPacket.setContentType(contentTypeImpl);
      return contentTypeImpl;
    } 
    contentType = codec.getStaticContentType(paramPacket);
    paramPacket.setContentType(contentType);
    return contentType;
  }
  
  protected MimeCodec(MimeCodec paramMimeCodec) {
    this.version = paramMimeCodec.version;
    this.features = paramMimeCodec.features;
  }
  
  public void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException {
    MimeMultipartParser mimeMultipartParser = new MimeMultipartParser(paramInputStream, paramString, (StreamingAttachmentFeature)this.features.get(StreamingAttachmentFeature.class));
    decode(mimeMultipartParser, paramPacket);
  }
  
  public void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket) { throw new UnsupportedOperationException(); }
  
  protected abstract void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket) throws IOException;
  
  public abstract MimeCodec copy();
  
  public static void writeln(String paramString, OutputStream paramOutputStream) throws IOException {
    writeAsAscii(paramString, paramOutputStream);
    writeln(paramOutputStream);
  }
  
  public static void writeAsAscii(String paramString, OutputStream paramOutputStream) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      paramOutputStream.write((byte)paramString.charAt(b)); 
  }
  
  public static void writeln(OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(13);
    paramOutputStream.write(10);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\MimeCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */