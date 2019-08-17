package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface WritableByteChannel extends Channel {
  int write(ByteBuffer paramByteBuffer) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\WritableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */