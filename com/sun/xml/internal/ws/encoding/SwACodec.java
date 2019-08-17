package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Map;

public final class SwACodec extends MimeCodec {
  public SwACodec(SOAPVersion paramSOAPVersion, WSFeatureList paramWSFeatureList, Codec paramCodec) {
    super(paramSOAPVersion, paramWSFeatureList);
    this.mimeRootCodec = paramCodec;
  }
  
  private SwACodec(SwACodec paramSwACodec) {
    super(paramSwACodec);
    this.mimeRootCodec = paramSwACodec.mimeRootCodec.copy();
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket) throws IOException {
    Attachment attachment = paramMimeMultipartParser.getRootPart();
    Codec codec = getMimeRootCodec(paramPacket);
    if (codec instanceof RootOnlyCodec) {
      ((RootOnlyCodec)codec).decode(attachment.asInputStream(), attachment.getContentType(), paramPacket, new MimeAttachmentSet(paramMimeMultipartParser));
    } else {
      codec.decode(attachment.asInputStream(), attachment.getContentType(), paramPacket);
      Map map = paramMimeMultipartParser.getAttachmentParts();
      for (Map.Entry entry : map.entrySet())
        paramPacket.getMessage().getAttachments().add((Attachment)entry.getValue()); 
    } 
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel) { throw new UnsupportedOperationException(); }
  
  public SwACodec copy() { return new SwACodec(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\SwACodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */