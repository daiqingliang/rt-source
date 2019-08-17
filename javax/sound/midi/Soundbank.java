package javax.sound.midi;

public interface Soundbank {
  String getName();
  
  String getVersion();
  
  String getVendor();
  
  String getDescription();
  
  SoundbankResource[] getResources();
  
  Instrument[] getInstruments();
  
  Instrument getInstrument(Patch paramPatch);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\Soundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */