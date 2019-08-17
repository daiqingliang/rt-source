package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class Pipe {
  public abstract SourceChannel source();
  
  public abstract SinkChannel sink();
  
  public static Pipe open() throws IOException { return SelectorProvider.provider().openPipe(); }
  
  public static abstract class SinkChannel extends AbstractSelectableChannel implements WritableByteChannel, GatheringByteChannel {
    protected SinkChannel(SelectorProvider param1SelectorProvider) { super(param1SelectorProvider); }
    
    public final int validOps() { return 4; }
  }
  
  public static abstract class SourceChannel extends AbstractSelectableChannel implements ReadableByteChannel, ScatteringByteChannel {
    protected SourceChannel(SelectorProvider param1SelectorProvider) { super(param1SelectorProvider); }
    
    public final int validOps() { return 1; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\Pipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */