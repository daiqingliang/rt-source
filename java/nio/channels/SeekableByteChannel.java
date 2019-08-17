package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface SeekableByteChannel extends ByteChannel {
  int read(ByteBuffer paramByteBuffer) throws IOException;
  
  int write(ByteBuffer paramByteBuffer) throws IOException;
  
  long position() throws IOException;
  
  SeekableByteChannel position(long paramLong) throws IOException;
  
  long size() throws IOException;
  
  SeekableByteChannel truncate(long paramLong) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\SeekableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */