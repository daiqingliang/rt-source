package javax.net.ssl;

import java.util.EventListener;

public interface HandshakeCompletedListener extends EventListener {
  void handshakeCompleted(HandshakeCompletedEvent paramHandshakeCompletedEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\HandshakeCompletedListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */