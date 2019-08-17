package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public interface Tube {
  @NotNull
  NextAction processRequest(@NotNull Packet paramPacket);
  
  @NotNull
  NextAction processResponse(@NotNull Packet paramPacket);
  
  @NotNull
  NextAction processException(@NotNull Throwable paramThrowable);
  
  void preDestroy();
  
  Tube copy(TubeCloner paramTubeCloner);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\Tube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */