package sun.audio;

import java.io.ByteArrayInputStream;

public class AudioDataStream extends ByteArrayInputStream {
  private final AudioData ad;
  
  public AudioDataStream(AudioData paramAudioData) {
    super(paramAudioData.buffer);
    this.ad = paramAudioData;
  }
  
  final AudioData getAudioData() { return this.ad; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioDataStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */