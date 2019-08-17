package com.sun.media.sound;

import javax.sound.midi.MidiChannel;

public interface ModelChannelMixer extends MidiChannel {
  boolean process(float[][] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  void stop();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelChannelMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */