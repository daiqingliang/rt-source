package javax.sound.sampled;

import java.io.IOException;

public interface Clip extends DataLine {
  public static final int LOOP_CONTINUOUSLY = -1;
  
  void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws LineUnavailableException;
  
  void open(AudioInputStream paramAudioInputStream) throws LineUnavailableException, IOException;
  
  int getFrameLength();
  
  long getMicrosecondLength();
  
  void setFramePosition(int paramInt);
  
  void setMicrosecondPosition(long paramLong);
  
  void setLoopPoints(int paramInt1, int paramInt2);
  
  void loop(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\Clip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */