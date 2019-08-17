package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface Codec {
  String getMimeType();
  
  ContentType getStaticContentType(Packet paramPacket);
  
  ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException;
  
  ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel);
  
  Codec copy();
  
  void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException;
  
  void decode(ReadableByteChannel paramReadableByteChannel, String paramString, Packet paramPacket);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\Codec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */