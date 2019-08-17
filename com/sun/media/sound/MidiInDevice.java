package com.sun.media.sound;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

final class MidiInDevice extends AbstractMidiDevice implements Runnable {
  private Thread midiInThread = null;
  
  MidiInDevice(AbstractMidiDeviceProvider.Info paramInfo) { super(paramInfo); }
  
  protected void implOpen() throws MidiUnavailableException {
    int i = ((MidiInDeviceProvider.MidiInDeviceInfo)getDeviceInfo()).getIndex();
    this.id = nOpen(i);
    if (this.id == 0L)
      throw new MidiUnavailableException("Unable to open native device"); 
    if (this.midiInThread == null)
      this.midiInThread = JSSecurityManager.createThread(this, "Java Sound MidiInDevice Thread", false, -1, true); 
    nStart(this.id);
  }
  
  protected void implClose() throws MidiUnavailableException {
    long l = this.id;
    this.id = 0L;
    super.implClose();
    nStop(l);
    if (this.midiInThread != null)
      try {
        this.midiInThread.join(1000L);
      } catch (InterruptedException interruptedException) {} 
    nClose(l);
  }
  
  public long getMicrosecondPosition() {
    long l = -1L;
    if (isOpen())
      l = nGetTimeStamp(this.id); 
    return l;
  }
  
  protected boolean hasTransmitters() { return true; }
  
  protected Transmitter createTransmitter() { return new MidiInTransmitter(null); }
  
  public void run() throws MidiUnavailableException {
    while (this.id != 0L) {
      nGetMessages(this.id);
      if (this.id != 0L)
        try {
          Thread.sleep(1L);
        } catch (InterruptedException interruptedException) {} 
    } 
    this.midiInThread = null;
  }
  
  void callbackShortMessage(int paramInt, long paramLong) {
    if (paramInt == 0 || this.id == 0L)
      return; 
    getTransmitterList().sendMessage(paramInt, paramLong);
  }
  
  void callbackLongMessage(byte[] paramArrayOfByte, long paramLong) {
    if (this.id == 0L || paramArrayOfByte == null)
      return; 
    getTransmitterList().sendMessage(paramArrayOfByte, paramLong);
  }
  
  private native long nOpen(int paramInt) throws MidiUnavailableException;
  
  private native void nClose(long paramLong);
  
  private native void nStart(long paramLong);
  
  private native void nStop(long paramLong);
  
  private native long nGetTimeStamp(long paramLong);
  
  private native void nGetMessages(long paramLong);
  
  private final class MidiInTransmitter extends AbstractMidiDevice.BasicTransmitter {
    private MidiInTransmitter() { super(MidiInDevice.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\MidiInDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */