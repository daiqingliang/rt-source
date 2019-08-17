package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class AsynchronousChannelGroup {
  private final AsynchronousChannelProvider provider;
  
  protected AsynchronousChannelGroup(AsynchronousChannelProvider paramAsynchronousChannelProvider) { this.provider = paramAsynchronousChannelProvider; }
  
  public final AsynchronousChannelProvider provider() { return this.provider; }
  
  public static AsynchronousChannelGroup withFixedThreadPool(int paramInt, ThreadFactory paramThreadFactory) throws IOException { return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(paramInt, paramThreadFactory); }
  
  public static AsynchronousChannelGroup withCachedThreadPool(ExecutorService paramExecutorService, int paramInt) throws IOException { return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(paramExecutorService, paramInt); }
  
  public static AsynchronousChannelGroup withThreadPool(ExecutorService paramExecutorService) throws IOException { return AsynchronousChannelProvider.provider().openAsynchronousChannelGroup(paramExecutorService, 0); }
  
  public abstract boolean isShutdown();
  
  public abstract boolean isTerminated();
  
  public abstract void shutdown();
  
  public abstract void shutdownNow();
  
  public abstract boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\AsynchronousChannelGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */