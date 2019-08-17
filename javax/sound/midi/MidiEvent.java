package javax.sound.midi;

public class MidiEvent {
  private final MidiMessage message;
  
  private long tick;
  
  public MidiEvent(MidiMessage paramMidiMessage, long paramLong) {
    this.message = paramMidiMessage;
    this.tick = paramLong;
  }
  
  public MidiMessage getMessage() { return this.message; }
  
  public void setTick(long paramLong) { this.tick = paramLong; }
  
  public long getTick() { return this.tick; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MidiEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */