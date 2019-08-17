package javax.sound.midi;

import java.util.List;

public interface MidiDevice extends AutoCloseable {
  Info getDeviceInfo();
  
  void open() throws MidiUnavailableException;
  
  void close() throws MidiUnavailableException;
  
  boolean isOpen();
  
  long getMicrosecondPosition();
  
  int getMaxReceivers();
  
  int getMaxTransmitters();
  
  Receiver getReceiver() throws MidiUnavailableException;
  
  List<Receiver> getReceivers();
  
  Transmitter getTransmitter() throws MidiUnavailableException;
  
  List<Transmitter> getTransmitters();
  
  public static class Info {
    private String name;
    
    private String vendor;
    
    private String description;
    
    private String version;
    
    protected Info(String param1String1, String param1String2, String param1String3, String param1String4) {
      this.name = param1String1;
      this.vendor = param1String2;
      this.description = param1String3;
      this.version = param1String4;
    }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final String getName() { return this.name; }
    
    public final String getVendor() { return this.vendor; }
    
    public final String getDescription() { return this.description; }
    
    public final String getVersion() { return this.version; }
    
    public final String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\MidiDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */