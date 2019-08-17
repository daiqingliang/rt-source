package sun.audio;

import java.io.IOException;
import java.io.InputStream;

public final class AudioTranslatorStream extends NativeAudioStream {
  private final int length = 0;
  
  public AudioTranslatorStream(InputStream paramInputStream) throws IOException {
    super(paramInputStream);
    throw new InvalidAudioFormatException();
  }
  
  public int getLength() { return 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioTranslatorStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */