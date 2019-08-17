package javax.sound.midi;

public interface Transmitter extends AutoCloseable {
  void setReceiver(Receiver paramReceiver);
  
  Receiver getReceiver();
  
  void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Transmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */