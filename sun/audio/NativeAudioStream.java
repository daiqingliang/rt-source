package sun.audio;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NativeAudioStream extends FilterInputStream {
  public NativeAudioStream(InputStream paramInputStream) throws IOException { super(paramInputStream); }
  
  public int getLength() { return 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\NativeAudioStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */