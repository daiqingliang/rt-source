package com.sun.media.sound;

public final class ModelDestination {
  public static final ModelIdentifier DESTINATION_NONE = null;
  
  public static final ModelIdentifier DESTINATION_KEYNUMBER = new ModelIdentifier("noteon", "keynumber");
  
  public static final ModelIdentifier DESTINATION_VELOCITY = new ModelIdentifier("noteon", "velocity");
  
  public static final ModelIdentifier DESTINATION_PITCH = new ModelIdentifier("osc", "pitch");
  
  public static final ModelIdentifier DESTINATION_GAIN = new ModelIdentifier("mixer", "gain");
  
  public static final ModelIdentifier DESTINATION_PAN = new ModelIdentifier("mixer", "pan");
  
  public static final ModelIdentifier DESTINATION_REVERB = new ModelIdentifier("mixer", "reverb");
  
  public static final ModelIdentifier DESTINATION_CHORUS = new ModelIdentifier("mixer", "chorus");
  
  public static final ModelIdentifier DESTINATION_LFO1_DELAY = new ModelIdentifier("lfo", "delay", 0);
  
  public static final ModelIdentifier DESTINATION_LFO1_FREQ = new ModelIdentifier("lfo", "freq", 0);
  
  public static final ModelIdentifier DESTINATION_LFO2_DELAY = new ModelIdentifier("lfo", "delay", 1);
  
  public static final ModelIdentifier DESTINATION_LFO2_FREQ = new ModelIdentifier("lfo", "freq", 1);
  
  public static final ModelIdentifier DESTINATION_EG1_DELAY = new ModelIdentifier("eg", "delay", 0);
  
  public static final ModelIdentifier DESTINATION_EG1_ATTACK = new ModelIdentifier("eg", "attack", 0);
  
  public static final ModelIdentifier DESTINATION_EG1_HOLD = new ModelIdentifier("eg", "hold", 0);
  
  public static final ModelIdentifier DESTINATION_EG1_DECAY = new ModelIdentifier("eg", "decay", 0);
  
  public static final ModelIdentifier DESTINATION_EG1_SUSTAIN = new ModelIdentifier("eg", "sustain", 0);
  
  public static final ModelIdentifier DESTINATION_EG1_RELEASE = new ModelIdentifier("eg", "release", 0);
  
  public static final ModelIdentifier DESTINATION_EG1_SHUTDOWN = new ModelIdentifier("eg", "shutdown", 0);
  
  public static final ModelIdentifier DESTINATION_EG2_DELAY = new ModelIdentifier("eg", "delay", 1);
  
  public static final ModelIdentifier DESTINATION_EG2_ATTACK = new ModelIdentifier("eg", "attack", 1);
  
  public static final ModelIdentifier DESTINATION_EG2_HOLD = new ModelIdentifier("eg", "hold", 1);
  
  public static final ModelIdentifier DESTINATION_EG2_DECAY = new ModelIdentifier("eg", "decay", 1);
  
  public static final ModelIdentifier DESTINATION_EG2_SUSTAIN = new ModelIdentifier("eg", "sustain", 1);
  
  public static final ModelIdentifier DESTINATION_EG2_RELEASE = new ModelIdentifier("eg", "release", 1);
  
  public static final ModelIdentifier DESTINATION_EG2_SHUTDOWN = new ModelIdentifier("eg", "shutdown", 1);
  
  public static final ModelIdentifier DESTINATION_FILTER_FREQ = new ModelIdentifier("filter", "freq", 0);
  
  public static final ModelIdentifier DESTINATION_FILTER_Q = new ModelIdentifier("filter", "q", 0);
  
  private ModelIdentifier destination = DESTINATION_NONE;
  
  private ModelTransform transform = new ModelStandardTransform();
  
  public ModelDestination() {}
  
  public ModelDestination(ModelIdentifier paramModelIdentifier) { this.destination = paramModelIdentifier; }
  
  public ModelIdentifier getIdentifier() { return this.destination; }
  
  public void setIdentifier(ModelIdentifier paramModelIdentifier) { this.destination = paramModelIdentifier; }
  
  public ModelTransform getTransform() { return this.transform; }
  
  public void setTransform(ModelTransform paramModelTransform) { this.transform = paramModelTransform; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelDestination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */