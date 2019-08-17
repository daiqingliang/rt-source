package com.sun.media.sound;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

final class MidiOutDevice extends AbstractMidiDevice {
  MidiOutDevice(AbstractMidiDeviceProvider.Info paramInfo) { super(paramInfo); }
  
  protected void implOpen() throws MidiUnavailableException {
    int i = ((AbstractMidiDeviceProvider.Info)getDeviceInfo()).getIndex();
    this.id = nOpen(i);
    if (this.id == 0L)
      throw new MidiUnavailableException("Unable to open native device"); 
  }
  
  protected void implClose() throws MidiUnavailableException {
    long l = this.id;
    this.id = 0L;
    super.implClose();
    nClose(l);
  }
  
  public long getMicrosecondPosition() {
    long l = -1L;
    if (isOpen())
      l = nGetTimeStamp(this.id); 
    return l;
  }
  
  protected boolean hasReceivers() { return true; }
  
  protected Receiver createReceiver() { return new MidiOutReceiver(); }
  
  private native long nOpen(int paramInt) throws MidiUnavailableException;
  
  private native void nClose(long paramLong);
  
  private native void nSendShortMessage(long paramLong1, int paramInt, long paramLong2);
  
  private native void nSendLongMessage(long paramLong1, byte[] paramArrayOfByte, int paramInt, long paramLong2);
  
  private native long nGetTimeStamp(long paramLong);
  
  final class MidiOutReceiver extends AbstractMidiDevice.AbstractReceiver {
    MidiOutReceiver() { super(MidiOutDevice.this); }
    
    void implSend(MidiMessage param1MidiMessage, long param1Long) {
      int i = param1MidiMessage.getLength();
      int j = param1MidiMessage.getStatus();
      if (i <= 3 && j != 240 && j != 247) {
        byte b;
        if (param1MidiMessage instanceof ShortMessage) {
          if (param1MidiMessage instanceof FastShortMessage) {
            b = ((FastShortMessage)param1MidiMessage).getPackedMsg();
          } else {
            ShortMessage shortMessage = (ShortMessage)param1MidiMessage;
            b = j & 0xFF | (shortMessage.getData1() & 0xFF) << 8 | (shortMessage.getData2() & 0xFF) << 16;
          } 
        } else {
          b = 0;
          byte[] arrayOfByte = param1MidiMessage.getMessage();
          if (i > 0) {
            b = arrayOfByte[0] & 0xFF;
            if (i > 1) {
              if (j == 255)
                return; 
              b |= (arrayOfByte[1] & 0xFF) << 8;
              if (i > 2)
                b |= (arrayOfByte[2] & 0xFF) << 16; 
            } 
          } 
        } 
        MidiOutDevice.this.nSendShortMessage(MidiOutDevice.this.id, b, param1Long);
      } else {
        byte[] arrayOfByte;
        if (param1MidiMessage instanceof FastSysexMessage) {
          arrayOfByte = ((FastSysexMessage)param1MidiMessage).getReadOnlyMessage();
        } else {
          arrayOfByte = param1MidiMessage.getMessage();
        } 
        int k = Math.min(i, arrayOfByte.length);
        if (k > 0)
          MidiOutDevice.this.nSendLongMessage(MidiOutDevice.this.id, arrayOfByte, k, param1Long); 
      } 
    }
    
    void sendPackedMidiMessage(int param1Int, long param1Long) {
      if (isOpen() && MidiOutDevice.this.id != 0L)
        MidiOutDevice.this.nSendShortMessage(MidiOutDevice.this.id, param1Int, param1Long); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\MidiOutDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */