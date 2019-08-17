package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;

abstract class StreamBasedMessage {
  public final Packet properties;
  
  public final AttachmentSet attachments;
  
  protected StreamBasedMessage(Packet paramPacket) {
    this.properties = paramPacket;
    this.attachments = new AttachmentSetImpl();
  }
  
  protected StreamBasedMessage(Packet paramPacket, AttachmentSet paramAttachmentSet) {
    this.properties = paramPacket;
    this.attachments = paramAttachmentSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\stream\StreamBasedMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */