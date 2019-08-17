package java.nio.channels;

import java.io.Closeable;
import java.io.IOException;

public interface Channel extends Closeable {
  boolean isOpen();
  
  void close() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */