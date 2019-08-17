package com.sun.media.sound;

public interface SoftAudioProcessor {
  void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2);
  
  void init(float paramFloat1, float paramFloat2);
  
  void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer);
  
  void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer);
  
  void setMixMode(boolean paramBoolean);
  
  void processAudio();
  
  void processControlLogic();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftAudioProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */