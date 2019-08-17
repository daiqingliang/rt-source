package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class WindowsAsynchronousChannelProvider extends AsynchronousChannelProvider {
  private Iocp defaultIocp() throws IOException {
    if (defaultIocp == null)
      synchronized (WindowsAsynchronousChannelProvider.class) {
        if (defaultIocp == null)
          defaultIocp = (new Iocp(this, ThreadPool.getDefault())).start(); 
      }  
    return defaultIocp;
  }
  
  public AsynchronousChannelGroup openAsynchronousChannelGroup(int paramInt, ThreadFactory paramThreadFactory) throws IOException { return (new Iocp(this, ThreadPool.create(paramInt, paramThreadFactory))).start(); }
  
  public AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService paramExecutorService, int paramInt) throws IOException { return (new Iocp(this, ThreadPool.wrap(paramExecutorService, paramInt))).start(); }
  
  private Iocp toIocp(AsynchronousChannelGroup paramAsynchronousChannelGroup) throws IOException {
    if (paramAsynchronousChannelGroup == null)
      return defaultIocp(); 
    if (!(paramAsynchronousChannelGroup instanceof Iocp))
      throw new IllegalChannelGroupException(); 
    return (Iocp)paramAsynchronousChannelGroup;
  }
  
  public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup) throws IOException { return new WindowsAsynchronousServerSocketChannelImpl(toIocp(paramAsynchronousChannelGroup)); }
  
  public AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup) throws IOException { return new WindowsAsynchronousSocketChannelImpl(toIocp(paramAsynchronousChannelGroup)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\WindowsAsynchronousChannelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */