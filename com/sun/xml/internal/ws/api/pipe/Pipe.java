package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

public interface Pipe {
  Packet process(Packet paramPacket);
  
  void preDestroy();
  
  Pipe copy(PipeCloner paramPipeCloner);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\Pipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */