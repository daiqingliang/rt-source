package javax.sound.midi;

public interface Receiver extends AutoCloseable {
  void send(MidiMessage paramMidiMessage, long paramLong);
  
  void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Receiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */