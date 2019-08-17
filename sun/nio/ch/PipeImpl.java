package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.Random;

class PipeImpl extends Pipe {
  private static final int NUM_SECRET_BYTES = 16;
  
  private static final Random RANDOM_NUMBER_GENERATOR = new SecureRandom();
  
  private Pipe.SourceChannel source;
  
  private Pipe.SinkChannel sink;
  
  PipeImpl(SelectorProvider paramSelectorProvider) throws IOException {
    try {
      AccessController.doPrivileged(new Initializer(paramSelectorProvider, null));
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getCause();
    } 
  }
  
  public Pipe.SourceChannel source() { return this.source; }
  
  public Pipe.SinkChannel sink() { return this.sink; }
  
  private class Initializer extends Object implements PrivilegedExceptionAction<Void> {
    private final SelectorProvider sp;
    
    private IOException ioe = null;
    
    private Initializer(SelectorProvider param1SelectorProvider) { this.sp = param1SelectorProvider; }
    
    public Void run() throws IOException {
      LoopbackConnector loopbackConnector = new LoopbackConnector(null);
      loopbackConnector.run();
      if (this.ioe instanceof java.nio.channels.ClosedByInterruptException) {
        this.ioe = null;
        Thread thread = new Thread(loopbackConnector) {
            public void interrupt() {}
          };
        thread.start();
        while (true) {
          try {
            thread.join();
            break;
          } catch (InterruptedException interruptedException) {}
        } 
        Thread.currentThread().interrupt();
      } 
      if (this.ioe != null)
        throw new IOException("Unable to establish loopback connection", this.ioe); 
      return null;
    }
    
    private class LoopbackConnector implements Runnable {
      private LoopbackConnector() {}
      
      public void run() {
        serverSocketChannel = null;
        SocketChannel socketChannel1 = null;
        SocketChannel socketChannel2 = null;
        try {
          ByteBuffer byteBuffer2 = (byteBuffer1 = ByteBuffer.allocate(16)).allocate(16);
          InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
          assert inetAddress.isLoopbackAddress();
          InetSocketAddress inetSocketAddress = null;
          while (true) {
            if (serverSocketChannel == null || !serverSocketChannel.isOpen()) {
              serverSocketChannel = ServerSocketChannel.open();
              serverSocketChannel.socket().bind(new InetSocketAddress(inetAddress, 0));
              inetSocketAddress = new InetSocketAddress(inetAddress, serverSocketChannel.socket().getLocalPort());
            } 
            socketChannel1 = SocketChannel.open(inetSocketAddress);
            RANDOM_NUMBER_GENERATOR.nextBytes(byteBuffer1.array());
            do {
              socketChannel1.write(byteBuffer1);
            } while (byteBuffer1.hasRemaining());
            byteBuffer1.rewind();
            socketChannel2 = serverSocketChannel.accept();
            do {
              socketChannel2.read(byteBuffer2);
            } while (byteBuffer2.hasRemaining());
            byteBuffer2.rewind();
            if (byteBuffer2.equals(byteBuffer1))
              break; 
            socketChannel2.close();
            socketChannel1.close();
          } 
          PipeImpl.Initializer.this.this$0.source = new SourceChannelImpl(PipeImpl.Initializer.this.sp, socketChannel1);
          PipeImpl.Initializer.this.this$0.sink = new SinkChannelImpl(PipeImpl.Initializer.this.sp, socketChannel2);
        } catch (IOException iOException) {
          try {
            if (socketChannel1 != null)
              socketChannel1.close(); 
            if (socketChannel2 != null)
              socketChannel2.close(); 
          } catch (IOException iOException1) {}
          PipeImpl.Initializer.this.ioe = iOException;
        } finally {
          try {
            if (serverSocketChannel != null)
              serverSocketChannel.close(); 
          } catch (IOException iOException) {}
        } 
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\PipeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */