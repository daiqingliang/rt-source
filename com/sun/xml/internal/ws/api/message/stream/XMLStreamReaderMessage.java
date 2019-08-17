package com.sun.xml.internal.ws.api.message.stream;

import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderMessage extends StreamBasedMessage {
  public final XMLStreamReader msg;
  
  public XMLStreamReaderMessage(Packet paramPacket, XMLStreamReader paramXMLStreamReader) {
    super(paramPacket);
    this.msg = paramXMLStreamReader;
  }
  
  public XMLStreamReaderMessage(Packet paramPacket, AttachmentSet paramAttachmentSet, XMLStreamReader paramXMLStreamReader) {
    super(paramPacket, paramAttachmentSet);
    this.msg = paramXMLStreamReader;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\stream\XMLStreamReaderMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */