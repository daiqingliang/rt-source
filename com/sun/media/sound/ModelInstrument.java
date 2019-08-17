package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.sampled.AudioFormat;

public abstract class ModelInstrument extends Instrument {
  protected ModelInstrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass) { super(paramSoundbank, paramPatch, paramString, paramClass); }
  
  public ModelDirector getDirector(ModelPerformer[] paramArrayOfModelPerformer, MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer) { return new ModelStandardIndexedDirector(paramArrayOfModelPerformer, paramModelDirectedPlayer); }
  
  public ModelPerformer[] getPerformers() { return new ModelPerformer[0]; }
  
  public ModelChannelMixer getChannelMixer(MidiChannel paramMidiChannel, AudioFormat paramAudioFormat) { return null; }
  
  public final Patch getPatchAlias() {
    Patch patch = getPatch();
    int i = patch.getProgram();
    int j = patch.getBank();
    if (j != 0)
      return patch; 
    boolean bool = false;
    if (getPatch() instanceof ModelPatch)
      bool = ((ModelPatch)getPatch()).isPercussion(); 
    return bool ? new Patch(15360, i) : new Patch(15488, i);
  }
  
  public final String[] getKeys() {
    String[] arrayOfString = new String[128];
    for (ModelPerformer modelPerformer : getPerformers()) {
      for (int i = modelPerformer.getKeyFrom(); i <= modelPerformer.getKeyTo(); i++) {
        if (i >= 0 && i < 128 && arrayOfString[i] == null) {
          String str = modelPerformer.getName();
          if (str == null)
            str = "untitled"; 
          arrayOfString[i] = str;
        } 
      } 
    } 
    return arrayOfString;
  }
  
  public final boolean[] getChannels() {
    boolean bool = false;
    if (getPatch() instanceof ModelPatch)
      bool = ((ModelPatch)getPatch()).isPercussion(); 
    if (bool) {
      boolean[] arrayOfBoolean1 = new boolean[16];
      for (byte b1 = 0; b1 < arrayOfBoolean1.length; b1++)
        arrayOfBoolean1[b1] = false; 
      arrayOfBoolean1[9] = true;
      return arrayOfBoolean1;
    } 
    int i = getPatch().getBank();
    if (i >> 7 == 120 || i >> 7 == 121) {
      boolean[] arrayOfBoolean1 = new boolean[16];
      for (byte b1 = 0; b1 < arrayOfBoolean1.length; b1++)
        arrayOfBoolean1[b1] = true; 
      return arrayOfBoolean1;
    } 
    boolean[] arrayOfBoolean = new boolean[16];
    for (byte b = 0; b < arrayOfBoolean.length; b++)
      arrayOfBoolean[b] = true; 
    arrayOfBoolean[9] = false;
    return arrayOfBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */