package com.sun.media.sound;

public final class ModelSource {
  public static final ModelIdentifier SOURCE_NONE = null;
  
  public static final ModelIdentifier SOURCE_NOTEON_KEYNUMBER = new ModelIdentifier("noteon", "keynumber");
  
  public static final ModelIdentifier SOURCE_NOTEON_VELOCITY = new ModelIdentifier("noteon", "velocity");
  
  public static final ModelIdentifier SOURCE_EG1 = new ModelIdentifier("eg", null, 0);
  
  public static final ModelIdentifier SOURCE_EG2 = new ModelIdentifier("eg", null, 1);
  
  public static final ModelIdentifier SOURCE_LFO1 = new ModelIdentifier("lfo", null, 0);
  
  public static final ModelIdentifier SOURCE_LFO2 = new ModelIdentifier("lfo", null, 1);
  
  public static final ModelIdentifier SOURCE_MIDI_PITCH = new ModelIdentifier("midi", "pitch", 0);
  
  public static final ModelIdentifier SOURCE_MIDI_CHANNEL_PRESSURE = new ModelIdentifier("midi", "channel_pressure", 0);
  
  public static final ModelIdentifier SOURCE_MIDI_POLY_PRESSURE = new ModelIdentifier("midi", "poly_pressure", 0);
  
  public static final ModelIdentifier SOURCE_MIDI_CC_0 = new ModelIdentifier("midi_cc", "0", 0);
  
  public static final ModelIdentifier SOURCE_MIDI_RPN_0 = new ModelIdentifier("midi_rpn", "0", 0);
  
  private ModelIdentifier source = SOURCE_NONE;
  
  private ModelTransform transform;
  
  public ModelSource() { this.transform = new ModelStandardTransform(); }
  
  public ModelSource(ModelIdentifier paramModelIdentifier) {
    this.source = paramModelIdentifier;
    this.transform = new ModelStandardTransform();
  }
  
  public ModelSource(ModelIdentifier paramModelIdentifier, boolean paramBoolean) {
    this.source = paramModelIdentifier;
    this.transform = new ModelStandardTransform(paramBoolean);
  }
  
  public ModelSource(ModelIdentifier paramModelIdentifier, boolean paramBoolean1, boolean paramBoolean2) {
    this.source = paramModelIdentifier;
    this.transform = new ModelStandardTransform(paramBoolean1, paramBoolean2);
  }
  
  public ModelSource(ModelIdentifier paramModelIdentifier, boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
    this.source = paramModelIdentifier;
    this.transform = new ModelStandardTransform(paramBoolean1, paramBoolean2, paramInt);
  }
  
  public ModelSource(ModelIdentifier paramModelIdentifier, ModelTransform paramModelTransform) {
    this.source = paramModelIdentifier;
    this.transform = paramModelTransform;
  }
  
  public ModelIdentifier getIdentifier() { return this.source; }
  
  public void setIdentifier(ModelIdentifier paramModelIdentifier) { this.source = paramModelIdentifier; }
  
  public ModelTransform getTransform() { return this.transform; }
  
  public void setTransform(ModelTransform paramModelTransform) { this.transform = paramModelTransform; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */