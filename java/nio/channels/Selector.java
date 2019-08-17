package java.nio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public abstract class Selector implements Closeable {
  public static Selector open() throws IOException { return SelectorProvider.provider().openSelector(); }
  
  public abstract boolean isOpen();
  
  public abstract SelectorProvider provider();
  
  public abstract Set<SelectionKey> keys();
  
  public abstract Set<SelectionKey> selectedKeys();
  
  public abstract int selectNow() throws IOException;
  
  public abstract int select(long paramLong) throws IOException;
  
  public abstract int select() throws IOException;
  
  public abstract Selector wakeup() throws IOException;
  
  public abstract void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\Selector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */