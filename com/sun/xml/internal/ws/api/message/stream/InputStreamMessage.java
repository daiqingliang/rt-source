package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.InputStream;

public class InputStreamMessage extends StreamBasedMessage {
  public final String contentType;
  
  public final InputStream msg;
  
  public InputStreamMessage(Packet paramPacket, String paramString, InputStream paramInputStream) {
    super(paramPacket);
    this.contentType = paramString;
    this.msg = paramInputStream;
  }
  
  public InputStreamMessage(Packet paramPacket, AttachmentSet paramAttachmentSet, String paramString, InputStream paramInputStream) {
    super(paramPacket, paramAttachmentSet);
    this.contentType = paramString;
    this.msg = paramInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\stream\InputStreamMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */