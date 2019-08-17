package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;

public class NonAnonymousResponseProcessor {
  private static final NonAnonymousResponseProcessor DEFAULT = new NonAnonymousResponseProcessor();
  
  public static NonAnonymousResponseProcessor getDefault() { return DEFAULT; }
  
  public Packet process(Packet paramPacket) {
    Fiber.CompletionCallback completionCallback = null;
    Fiber fiber1 = Fiber.getCurrentIfSet();
    if (fiber1 != null) {
      final Fiber.CompletionCallback currentFiberCallback = fiber1.getCompletionCallback();
      if (completionCallback1 != null) {
        completionCallback = new Fiber.CompletionCallback() {
            public void onCompletion(@NotNull Packet param1Packet) { currentFiberCallback.onCompletion(param1Packet); }
            
            public void onCompletion(@NotNull Throwable param1Throwable) { currentFiberCallback.onCompletion(param1Throwable); }
          };
        fiber1.setCompletionCallback(null);
      } 
    } 
    WSEndpoint wSEndpoint = paramPacket.endpoint;
    WSBinding wSBinding = wSEndpoint.getBinding();
    Tube tube = TransportTubeFactory.create(Thread.currentThread().getContextClassLoader(), new ClientTubeAssemblerContext(paramPacket.endpointAddress, wSEndpoint.getPort(), (WSService)null, wSBinding, wSEndpoint.getContainer(), ((BindingImpl)wSBinding).createCodec(), null, null));
    Fiber fiber2 = wSEndpoint.getEngine().createFiber();
    fiber2.start(tube, paramPacket, completionCallback);
    Packet packet = paramPacket.copy(false);
    packet.endpointAddress = null;
    return packet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\NonAnonymousResponseProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */