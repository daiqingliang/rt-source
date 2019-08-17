package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public final class SoftShortMessage extends ShortMessage {
  int channel = 0;
  
  public int getChannel() { return this.channel; }
  
  public void setMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws InvalidMidiDataException {
    this.channel = paramInt2;
    super.setMessage(paramInt1, paramInt2 & 0xF, paramInt3, paramInt4);
  }
  
  public Object clone() {
    SoftShortMessage softShortMessage = new SoftShortMessage();
    try {
      softShortMessage.setMessage(getCommand(), getChannel(), getData1(), getData2());
    } catch (InvalidMidiDataException invalidMidiDataException) {
      throw new IllegalArgumentException(invalidMidiDataException);
    } 
    return softShortMessage;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftShortMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */