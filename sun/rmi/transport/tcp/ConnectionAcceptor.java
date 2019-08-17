package sun.rmi.transport.tcp;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Connection;

class ConnectionAcceptor implements Runnable {
  private TCPTransport transport;
  
  private List<Connection> queue = new ArrayList();
  
  private static int threadNum = 0;
  
  public ConnectionAcceptor(TCPTransport paramTCPTransport) { this.transport = paramTCPTransport; }
  
  public void startNewAcceptor() {
    Thread thread = (Thread)AccessController.doPrivileged(new NewThreadAction(this, "Multiplex Accept-" + ++threadNum, true));
    thread.start();
  }
  
  public void accept(Connection paramConnection) {
    synchronized (this.queue) {
      this.queue.add(paramConnection);
      this.queue.notify();
    } 
  }
  
  public void run() {
    Connection connection;
    synchronized (this.queue) {
      while (this.queue.size() == 0) {
        try {
          this.queue.wait();
        } catch (InterruptedException interruptedException) {}
      } 
      startNewAcceptor();
      connection = (Connection)this.queue.remove(0);
    } 
    this.transport.handleMessages(connection, true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\ConnectionAcceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */