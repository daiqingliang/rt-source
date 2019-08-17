package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

final class FastShortMessage extends ShortMessage {
  private int packedMsg;
  
  FastShortMessage(int paramInt) throws InvalidMidiDataException {
    this.packedMsg = paramInt;
    getDataLength(paramInt & 0xFF);
  }
  
  FastShortMessage(ShortMessage paramShortMessage) { this.packedMsg = paramShortMessage.getStatus() | paramShortMessage.getData1() << 8 | paramShortMessage.getData2() << 16; }
  
  int getPackedMsg() { return this.packedMsg; }
  
  public byte[] getMessage() {
    int i = 0;
    try {
      i = getDataLength(this.packedMsg & 0xFF) + 1;
    } catch (InvalidMidiDataException invalidMidiDataException) {}
    byte[] arrayOfByte = new byte[i];
    if (i > 0) {
      arrayOfByte[0] = (byte)(this.packedMsg & 0xFF);
      if (i > 1) {
        arrayOfByte[1] = (byte)((this.packedMsg & 0xFF00) >> 8);
        if (i > 2)
          arrayOfByte[2] = (byte)((this.packedMsg & 0xFF0000) >> 16); 
      } 
    } 
    return arrayOfByte;
  }
  
  public int getLength() {
    try {
      return getDataLength(this.packedMsg & 0xFF) + 1;
    } catch (InvalidMidiDataException invalidMidiDataException) {
      return 0;
    } 
  }
  
  public void setMessage(int paramInt) throws InvalidMidiDataException {
    int i = getDataLength(paramInt);
    if (i != 0)
      super.setMessage(paramInt); 
    this.packedMsg = this.packedMsg & 0xFFFF00 | paramInt & 0xFF;
  }
  
  public void setMessage(int paramInt1, int paramInt2, int paramInt3) throws InvalidMidiDataException {
    getDataLength(paramInt1);
    this.packedMsg = paramInt1 & 0xFF | (paramInt2 & 0xFF) << 8 | (paramInt3 & 0xFF) << 16;
  }
  
  public void setMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws InvalidMidiDataException {
    getDataLength(paramInt1);
    this.packedMsg = paramInt1 & 0xF0 | paramInt2 & 0xF | (paramInt3 & 0xFF) << 8 | (paramInt4 & 0xFF) << 16;
  }
  
  public int getChannel() { return this.packedMsg & 0xF; }
  
  public int getCommand() { return this.packedMsg & 0xF0; }
  
  public int getData1() { return (this.packedMsg & 0xFF00) >> 8; }
  
  public int getData2() { return (this.packedMsg & 0xFF0000) >> 16; }
  
  public int getStatus() { return this.packedMsg & 0xFF; }
  
  public Object clone() {
    try {
      return new FastShortMessage(this.packedMsg);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\FastShortMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */