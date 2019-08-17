package com.sun.media.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioFormat;

public final class ModelMappedInstrument extends ModelInstrument {
  private final ModelInstrument ins;
  
  public ModelMappedInstrument(ModelInstrument paramModelInstrument, Patch paramPatch) {
    super(paramModelInstrument.getSoundbank(), paramPatch, paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    this.ins = paramModelInstrument;
  }
  
  public Object getData() { return this.ins.getData(); }
  
  public ModelPerformer[] getPerformers() { return this.ins.getPerformers(); }
  
  public ModelDirector getDirector(ModelPerformer[] paramArrayOfModelPerformer, MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer) { return this.ins.getDirector(paramArrayOfModelPerformer, paramMidiChannel, paramModelDirectedPlayer); }
  
  public ModelChannelMixer getChannelMixer(MidiChannel paramMidiChannel, AudioFormat paramAudioFormat) { return this.ins.getChannelMixer(paramMidiChannel, paramAudioFormat); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelMappedInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */