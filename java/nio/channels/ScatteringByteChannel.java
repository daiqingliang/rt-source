package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ScatteringByteChannel extends ReadableByteChannel {
  long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  long read(ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\ScatteringByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */