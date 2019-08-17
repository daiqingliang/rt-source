package javax.sound.sampled;

public interface TargetDataLine extends DataLine {
  void open(AudioFormat paramAudioFormat, int paramInt) throws LineUnavailableException;
  
  void open(AudioFormat paramAudioFormat) throws LineUnavailableException;
  
  int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\TargetDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */