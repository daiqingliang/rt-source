package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;

public final class SoftInstrument extends Instrument {
  private SoftPerformer[] performers;
  
  private ModelPerformer[] modelperformers;
  
  private final Object data;
  
  private final ModelInstrument ins;
  
  public SoftInstrument(ModelInstrument paramModelInstrument) {
    super(paramModelInstrument.getSoundbank(), paramModelInstrument.getPatch(), paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    this.data = paramModelInstrument.getData();
    this.ins = paramModelInstrument;
    initPerformers(paramModelInstrument.getPerformers());
  }
  
  public SoftInstrument(ModelInstrument paramModelInstrument, ModelPerformer[] paramArrayOfModelPerformer) {
    super(paramModelInstrument.getSoundbank(), paramModelInstrument.getPatch(), paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    this.data = paramModelInstrument.getData();
    this.ins = paramModelInstrument;
    initPerformers(paramArrayOfModelPerformer);
  }
  
  private void initPerformers(ModelPerformer[] paramArrayOfModelPerformer) {
    this.modelperformers = paramArrayOfModelPerformer;
    this.performers = new SoftPerformer[paramArrayOfModelPerformer.length];
    for (byte b = 0; b < paramArrayOfModelPerformer.length; b++)
      this.performers[b] = new SoftPerformer(paramArrayOfModelPerformer[b]); 
  }
  
  public ModelDirector getDirector(MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer) { return this.ins.getDirector(this.modelperformers, paramMidiChannel, paramModelDirectedPlayer); }
  
  public ModelInstrument getSourceInstrument() { return this.ins; }
  
  public Object getData() { return this.data; }
  
  public SoftPerformer getPerformer(int paramInt) { return this.performers[paramInt]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */