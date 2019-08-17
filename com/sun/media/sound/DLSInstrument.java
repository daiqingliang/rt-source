package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.sound.midi.Patch;

public final class DLSInstrument extends ModelInstrument {
  int preset = 0;
  
  int bank = 0;
  
  boolean druminstrument = false;
  
  byte[] guid = null;
  
  DLSInfo info = new DLSInfo();
  
  List<DLSRegion> regions = new ArrayList();
  
  List<DLSModulator> modulators = new ArrayList();
  
  public DLSInstrument() { super(null, null, null, null); }
  
  public DLSInstrument(DLSSoundbank paramDLSSoundbank) { super(paramDLSSoundbank, null, null, null); }
  
  public DLSInfo getInfo() { return this.info; }
  
  public String getName() { return this.info.name; }
  
  public void setName(String paramString) { this.info.name = paramString; }
  
  public ModelPatch getPatch() { return new ModelPatch(this.bank, this.preset, this.druminstrument); }
  
  public void setPatch(Patch paramPatch) {
    if (paramPatch instanceof ModelPatch && ((ModelPatch)paramPatch).isPercussion()) {
      this.druminstrument = true;
      this.bank = paramPatch.getBank();
      this.preset = paramPatch.getProgram();
    } else {
      this.druminstrument = false;
      this.bank = paramPatch.getBank();
      this.preset = paramPatch.getProgram();
    } 
  }
  
  public Object getData() { return null; }
  
  public List<DLSRegion> getRegions() { return this.regions; }
  
  public List<DLSModulator> getModulators() { return this.modulators; }
  
  public String toString() { return this.druminstrument ? ("Drumkit: " + this.info.name + " bank #" + this.bank + " preset #" + this.preset) : ("Instrument: " + this.info.name + " bank #" + this.bank + " preset #" + this.preset); }
  
  private ModelIdentifier convertToModelDest(int paramInt) { return (paramInt == 0) ? null : ((paramInt == 1) ? ModelDestination.DESTINATION_GAIN : ((paramInt == 3) ? ModelDestination.DESTINATION_PITCH : ((paramInt == 4) ? ModelDestination.DESTINATION_PAN : ((paramInt == 260) ? ModelDestination.DESTINATION_LFO1_FREQ : ((paramInt == 261) ? ModelDestination.DESTINATION_LFO1_DELAY : ((paramInt == 518) ? ModelDestination.DESTINATION_EG1_ATTACK : ((paramInt == 519) ? ModelDestination.DESTINATION_EG1_DECAY : ((paramInt == 521) ? ModelDestination.DESTINATION_EG1_RELEASE : ((paramInt == 522) ? ModelDestination.DESTINATION_EG1_SUSTAIN : ((paramInt == 778) ? ModelDestination.DESTINATION_EG2_ATTACK : ((paramInt == 779) ? ModelDestination.DESTINATION_EG2_DECAY : ((paramInt == 781) ? ModelDestination.DESTINATION_EG2_RELEASE : ((paramInt == 782) ? ModelDestination.DESTINATION_EG2_SUSTAIN : ((paramInt == 5) ? ModelDestination.DESTINATION_KEYNUMBER : ((paramInt == 128) ? ModelDestination.DESTINATION_CHORUS : ((paramInt == 129) ? ModelDestination.DESTINATION_REVERB : ((paramInt == 276) ? ModelDestination.DESTINATION_LFO2_FREQ : ((paramInt == 277) ? ModelDestination.DESTINATION_LFO2_DELAY : ((paramInt == 523) ? ModelDestination.DESTINATION_EG1_DELAY : ((paramInt == 524) ? ModelDestination.DESTINATION_EG1_HOLD : ((paramInt == 525) ? ModelDestination.DESTINATION_EG1_SHUTDOWN : ((paramInt == 783) ? ModelDestination.DESTINATION_EG2_DELAY : ((paramInt == 784) ? ModelDestination.DESTINATION_EG2_HOLD : ((paramInt == 1280) ? ModelDestination.DESTINATION_FILTER_FREQ : ((paramInt == 1281) ? ModelDestination.DESTINATION_FILTER_Q : null))))))))))))))))))))))))); }
  
  private ModelIdentifier convertToModelSrc(int paramInt) { return (paramInt == 0) ? null : ((paramInt == 1) ? ModelSource.SOURCE_LFO1 : ((paramInt == 2) ? ModelSource.SOURCE_NOTEON_VELOCITY : ((paramInt == 3) ? ModelSource.SOURCE_NOTEON_KEYNUMBER : ((paramInt == 4) ? ModelSource.SOURCE_EG1 : ((paramInt == 5) ? ModelSource.SOURCE_EG2 : ((paramInt == 6) ? ModelSource.SOURCE_MIDI_PITCH : ((paramInt == 129) ? new ModelIdentifier("midi_cc", "1", 0) : ((paramInt == 135) ? new ModelIdentifier("midi_cc", "7", 0) : ((paramInt == 138) ? new ModelIdentifier("midi_cc", "10", 0) : ((paramInt == 139) ? new ModelIdentifier("midi_cc", "11", 0) : ((paramInt == 256) ? new ModelIdentifier("midi_rpn", "0", 0) : ((paramInt == 257) ? new ModelIdentifier("midi_rpn", "1", 0) : ((paramInt == 7) ? ModelSource.SOURCE_MIDI_POLY_PRESSURE : ((paramInt == 8) ? ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE : ((paramInt == 9) ? ModelSource.SOURCE_LFO2 : ((paramInt == 10) ? ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE : ((paramInt == 219) ? new ModelIdentifier("midi_cc", "91", 0) : ((paramInt == 221) ? new ModelIdentifier("midi_cc", "93", 0) : null)))))))))))))))))); }
  
  private ModelConnectionBlock convertToModel(DLSModulator paramDLSModulator) {
    double d;
    ModelIdentifier modelIdentifier1 = convertToModelSrc(paramDLSModulator.getSource());
    ModelIdentifier modelIdentifier2 = convertToModelSrc(paramDLSModulator.getControl());
    ModelIdentifier modelIdentifier3 = convertToModelDest(paramDLSModulator.getDestination());
    int i = paramDLSModulator.getScale();
    if (i == Integer.MIN_VALUE) {
      d = Double.NEGATIVE_INFINITY;
    } else {
      d = i / 65536.0D;
    } 
    if (modelIdentifier3 != null) {
      ModelSource modelSource1 = null;
      ModelSource modelSource2 = null;
      ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock();
      if (modelIdentifier2 != null) {
        ModelSource modelSource = new ModelSource();
        if (modelIdentifier2 == ModelSource.SOURCE_MIDI_PITCH) {
          ((ModelStandardTransform)modelSource.getTransform()).setPolarity(true);
        } else if (modelIdentifier2 == ModelSource.SOURCE_LFO1 || modelIdentifier2 == ModelSource.SOURCE_LFO2) {
          ((ModelStandardTransform)modelSource.getTransform()).setPolarity(true);
        } 
        modelSource.setIdentifier(modelIdentifier2);
        modelConnectionBlock.addSource(modelSource);
        modelSource2 = modelSource;
      } 
      if (modelIdentifier1 != null) {
        ModelSource modelSource = new ModelSource();
        if (modelIdentifier1 == ModelSource.SOURCE_MIDI_PITCH) {
          ((ModelStandardTransform)modelSource.getTransform()).setPolarity(true);
        } else if (modelIdentifier1 == ModelSource.SOURCE_LFO1 || modelIdentifier1 == ModelSource.SOURCE_LFO2) {
          ((ModelStandardTransform)modelSource.getTransform()).setPolarity(true);
        } 
        modelSource.setIdentifier(modelIdentifier1);
        modelConnectionBlock.addSource(modelSource);
        modelSource1 = modelSource;
      } 
      ModelDestination modelDestination = new ModelDestination();
      modelDestination.setIdentifier(modelIdentifier3);
      modelConnectionBlock.setDestination(modelDestination);
      if (paramDLSModulator.getVersion() == 1) {
        if (paramDLSModulator.getTransform() == 1) {
          if (modelSource1 != null) {
            ((ModelStandardTransform)modelSource1.getTransform()).setTransform(1);
            ((ModelStandardTransform)modelSource1.getTransform()).setDirection(true);
          } 
          if (modelSource2 != null) {
            ((ModelStandardTransform)modelSource2.getTransform()).setTransform(1);
            ((ModelStandardTransform)modelSource2.getTransform()).setDirection(true);
          } 
        } 
      } else if (paramDLSModulator.getVersion() == 2) {
        int j = paramDLSModulator.getTransform();
        int k = j >> 15 & true;
        int m = j >> 14 & true;
        int n = j >> 10 & 0x8;
        int i1 = j >> 9 & true;
        int i2 = j >> 8 & true;
        int i3 = j >> 4 & 0x8;
        if (modelSource1 != null) {
          byte b = 0;
          if (n == 3)
            b = 3; 
          if (n == 1)
            b = 1; 
          if (n == 2)
            b = 2; 
          ((ModelStandardTransform)modelSource1.getTransform()).setTransform(b);
          ((ModelStandardTransform)modelSource1.getTransform()).setPolarity((m == 1));
          ((ModelStandardTransform)modelSource1.getTransform()).setDirection((k == 1));
        } 
        if (modelSource2 != null) {
          byte b = 0;
          if (i3 == 3)
            b = 3; 
          if (i3 == 1)
            b = 1; 
          if (i3 == 2)
            b = 2; 
          ((ModelStandardTransform)modelSource2.getTransform()).setTransform(b);
          ((ModelStandardTransform)modelSource2.getTransform()).setPolarity((i2 == 1));
          ((ModelStandardTransform)modelSource2.getTransform()).setDirection((i1 == 1));
        } 
      } 
      modelConnectionBlock.setScale(d);
      return modelConnectionBlock;
    } 
    return null;
  }
  
  public ModelPerformer[] getPerformers() {
    ArrayList arrayList = new ArrayList();
    HashMap hashMap1 = new HashMap();
    for (DLSModulator dLSModulator : getModulators())
      hashMap1.put(dLSModulator.getSource() + "x" + dLSModulator.getControl() + "=" + dLSModulator.getDestination(), dLSModulator); 
    HashMap hashMap2 = new HashMap();
    for (DLSRegion dLSRegion : this.regions) {
      ModelPerformer modelPerformer = new ModelPerformer();
      modelPerformer.setName(dLSRegion.getSample().getName());
      modelPerformer.setSelfNonExclusive(((dLSRegion.getFusoptions() & true) != 0));
      modelPerformer.setExclusiveClass(dLSRegion.getExclusiveClass());
      modelPerformer.setKeyFrom(dLSRegion.getKeyfrom());
      modelPerformer.setKeyTo(dLSRegion.getKeyto());
      modelPerformer.setVelFrom(dLSRegion.getVelfrom());
      modelPerformer.setVelTo(dLSRegion.getVelto());
      hashMap2.clear();
      hashMap2.putAll(hashMap1);
      for (DLSModulator dLSModulator : dLSRegion.getModulators())
        hashMap2.put(dLSModulator.getSource() + "x" + dLSModulator.getControl() + "=" + dLSModulator.getDestination(), dLSModulator); 
      List list = modelPerformer.getConnectionBlocks();
      for (DLSModulator dLSModulator : hashMap2.values()) {
        ModelConnectionBlock modelConnectionBlock = convertToModel(dLSModulator);
        if (modelConnectionBlock != null)
          list.add(modelConnectionBlock); 
      } 
      DLSSample dLSSample = dLSRegion.getSample();
      DLSSampleOptions dLSSampleOptions = dLSRegion.getSampleoptions();
      if (dLSSampleOptions == null)
        dLSSampleOptions = dLSSample.getSampleoptions(); 
      ModelByteBuffer modelByteBuffer = dLSSample.getDataBuffer();
      float f = (-dLSSampleOptions.unitynote * 100 + dLSSampleOptions.finetune);
      ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(modelByteBuffer, dLSSample.getFormat(), f);
      modelByteBufferWavetable.setAttenuation(modelByteBufferWavetable.getAttenuation() / 65536.0F);
      if (dLSSampleOptions.getLoops().size() != 0) {
        DLSSampleLoop dLSSampleLoop = (DLSSampleLoop)dLSSampleOptions.getLoops().get(0);
        modelByteBufferWavetable.setLoopStart((int)dLSSampleLoop.getStart());
        modelByteBufferWavetable.setLoopLength((int)dLSSampleLoop.getLength());
        if (dLSSampleLoop.getType() == 0L)
          modelByteBufferWavetable.setLoopType(1); 
        if (dLSSampleLoop.getType() == 1L) {
          modelByteBufferWavetable.setLoopType(2);
        } else {
          modelByteBufferWavetable.setLoopType(1);
        } 
      } 
      modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(1.0D, new ModelDestination(new ModelIdentifier("filter", "type", 1))));
      modelPerformer.getOscillators().add(modelByteBufferWavetable);
      arrayList.add(modelPerformer);
    } 
    return (ModelPerformer[])arrayList.toArray(new ModelPerformer[arrayList.size()]);
  }
  
  public byte[] getGuid() { return (this.guid == null) ? null : Arrays.copyOf(this.guid, this.guid.length); }
  
  public void setGuid(byte[] paramArrayOfByte) { this.guid = (paramArrayOfByte == null) ? null : Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */