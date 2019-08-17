package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public class SimpleSoundbank implements Soundbank {
  String name = "";
  
  String version = "";
  
  String vendor = "";
  
  String description = "";
  
  List<SoundbankResource> resources = new ArrayList();
  
  List<Instrument> instruments = new ArrayList();
  
  public String getName() { return this.name; }
  
  public String getVersion() { return this.version; }
  
  public String getVendor() { return this.vendor; }
  
  public String getDescription() { return this.description; }
  
  public void setDescription(String paramString) { this.description = paramString; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public void setVendor(String paramString) { this.vendor = paramString; }
  
  public void setVersion(String paramString) { this.version = paramString; }
  
  public SoundbankResource[] getResources() { return (SoundbankResource[])this.resources.toArray(new SoundbankResource[this.resources.size()]); }
  
  public Instrument[] getInstruments() {
    Instrument[] arrayOfInstrument = (Instrument[])this.instruments.toArray(new Instrument[this.resources.size()]);
    Arrays.sort(arrayOfInstrument, new ModelInstrumentComparator());
    return arrayOfInstrument;
  }
  
  public Instrument getInstrument(Patch paramPatch) {
    int i = paramPatch.getProgram();
    int j = paramPatch.getBank();
    boolean bool = false;
    if (paramPatch instanceof ModelPatch)
      bool = ((ModelPatch)paramPatch).isPercussion(); 
    for (Instrument instrument : this.instruments) {
      Patch patch = instrument.getPatch();
      int k = patch.getProgram();
      int m = patch.getBank();
      if (i == k && j == m) {
        boolean bool1 = false;
        if (patch instanceof ModelPatch)
          bool1 = ((ModelPatch)patch).isPercussion(); 
        if (bool == bool1)
          return instrument; 
      } 
    } 
    return null;
  }
  
  public void addResource(SoundbankResource paramSoundbankResource) {
    if (paramSoundbankResource instanceof Instrument) {
      this.instruments.add((Instrument)paramSoundbankResource);
    } else {
      this.resources.add(paramSoundbankResource);
    } 
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource) {
    if (paramSoundbankResource instanceof Instrument) {
      this.instruments.remove((Instrument)paramSoundbankResource);
    } else {
      this.resources.remove(paramSoundbankResource);
    } 
  }
  
  public void addInstrument(Instrument paramInstrument) { this.instruments.add(paramInstrument); }
  
  public void removeInstrument(Instrument paramInstrument) { this.instruments.remove(paramInstrument); }
  
  public void addAllInstruments(Soundbank paramSoundbank) {
    for (Instrument instrument : paramSoundbank.getInstruments())
      addInstrument(instrument); 
  }
  
  public void removeAllInstruments(Soundbank paramSoundbank) {
    for (Instrument instrument : paramSoundbank.getInstruments())
      removeInstrument(instrument); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SimpleSoundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */