package com.sun.xml.internal.ws.api.server;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;
import java.io.IOException;

public abstract class AbstractServerAsyncTransport<T> extends Object {
  private final WSEndpoint endpoint;
  
  private final CodecPool codecPool;
  
  public AbstractServerAsyncTransport(WSEndpoint paramWSEndpoint) {
    this.endpoint = paramWSEndpoint;
    this.codecPool = new CodecPool(paramWSEndpoint);
  }
  
  protected Packet decodePacket(T paramT, @NotNull Codec paramCodec) throws IOException {
    Packet packet = new Packet();
    packet.acceptableMimeTypes = getAcceptableMimeTypes(paramT);
    packet.addSatellite(getPropertySet(paramT));
    packet.transportBackChannel = getTransportBackChannel(paramT);
    return packet;
  }
  
  protected abstract void encodePacket(T paramT, @NotNull Packet paramPacket, @NotNull Codec paramCodec) throws IOException;
  
  @Nullable
  protected abstract String getAcceptableMimeTypes(T paramT);
  
  @Nullable
  protected abstract TransportBackChannel getTransportBackChannel(T paramT);
  
  @NotNull
  protected abstract PropertySet getPropertySet(T paramT);
  
  @NotNull
  protected abstract WebServiceContextDelegate getWebServiceContextDelegate(T paramT);
  
  protected void handle(final T connection) throws IOException {
    final Codec codec = (Codec)this.codecPool.take();
    Packet packet = decodePacket(paramT, codec);
    if (!packet.getMessage().isFault())
      this.endpoint.schedule(packet, new WSEndpoint.CompletionCallback() {
            public void onCompletion(@NotNull Packet param1Packet) {
              try {
                AbstractServerAsyncTransport.this.encodePacket(connection, param1Packet, codec);
              } catch (IOException iOException) {
                iOException.printStackTrace();
              } 
              AbstractServerAsyncTransport.this.codecPool.recycle(codec);
            }
          }); 
  }
  
  private static final class CodecPool extends Pool<Codec> {
    WSEndpoint endpoint;
    
    CodecPool(WSEndpoint param1WSEndpoint) { this.endpoint = param1WSEndpoint; }
    
    protected Codec create() { return this.endpoint.createCodec(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\AbstractServerAsyncTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */