package com.sun.media.sound;

import java.io.IOException;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.VoiceStatus;

public abstract class ModelAbstractOscillator implements ModelOscillator, ModelOscillatorStream, Soundbank {
  protected float pitch = 6000.0F;
  
  protected float samplerate;
  
  protected MidiChannel channel;
  
  protected VoiceStatus voice;
  
  protected int noteNumber;
  
  protected int velocity;
  
  protected boolean on = false;
  
  public void init() {}
  
  public void close() {}
  
  public void noteOff(int paramInt) { this.on = false; }
  
  public void noteOn(MidiChannel paramMidiChannel, VoiceStatus paramVoiceStatus, int paramInt1, int paramInt2) {
    this.channel = paramMidiChannel;
    this.voice = paramVoiceStatus;
    this.noteNumber = paramInt1;
    this.velocity = paramInt2;
    this.on = true;
  }
  
  public int read(float[][] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException { return -1; }
  
  public MidiChannel getChannel() { return this.channel; }
  
  public VoiceStatus getVoice() { return this.voice; }
  
  public int getNoteNumber() { return this.noteNumber; }
  
  public int getVelocity() { return this.velocity; }
  
  public boolean isOn() { return this.on; }
  
  public void setPitch(float paramFloat) { this.pitch = paramFloat; }
  
  public float getPitch() { return this.pitch; }
  
  public void setSampleRate(float paramFloat) { this.samplerate = paramFloat; }
  
  public float getSampleRate() { return this.samplerate; }
  
  public float getAttenuation() { return 0.0F; }
  
  public int getChannels() { return 1; }
  
  public String getName() { return getClass().getName(); }
  
  public Patch getPatch() { return new Patch(0, 0); }
  
  public ModelOscillatorStream open(float paramFloat) {
    ModelAbstractOscillator modelAbstractOscillator;
    try {
      modelAbstractOscillator = (ModelAbstractOscillator)getClass().newInstance();
    } catch (InstantiationException instantiationException) {
      throw new IllegalArgumentException(instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalArgumentException(illegalAccessException);
    } 
    modelAbstractOscillator.setSampleRate(paramFloat);
    modelAbstractOscillator.init();
    return modelAbstractOscillator;
  }
  
  public ModelPerformer getPerformer() {
    ModelPerformer modelPerformer = new ModelPerformer();
    modelPerformer.getOscillators().add(this);
    return modelPerformer;
  }
  
  public ModelInstrument getInstrument() {
    SimpleInstrument simpleInstrument = new SimpleInstrument();
    simpleInstrument.setName(getName());
    simpleInstrument.add(getPerformer());
    simpleInstrument.setPatch(getPatch());
    return simpleInstrument;
  }
  
  public Soundbank getSoundBank() {
    SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
    simpleSoundbank.addInstrument(getInstrument());
    return simpleSoundbank;
  }
  
  public String getDescription() { return getName(); }
  
  public Instrument getInstrument(Patch paramPatch) {
    ModelInstrument modelInstrument = getInstrument();
    Patch patch = modelInstrument.getPatch();
    return (patch.getBank() != paramPatch.getBank()) ? null : ((patch.getProgram() != paramPatch.getProgram()) ? null : ((patch instanceof ModelPatch && paramPatch instanceof ModelPatch && ((ModelPatch)patch).isPercussion() != ((ModelPatch)paramPatch).isPercussion()) ? null : modelInstrument));
  }
  
  public Instrument[] getInstruments() { return new Instrument[] { getInstrument() }; }
  
  public SoundbankResource[] getResources() { return new SoundbankResource[0]; }
  
  public String getVendor() { return null; }
  
  public String getVersion() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelAbstractOscillator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */