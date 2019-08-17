package com.sun.media.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Patch;

public final class SF2Instrument extends ModelInstrument {
  String name = "";
  
  int preset = 0;
  
  int bank = 0;
  
  long library = 0L;
  
  long genre = 0L;
  
  long morphology = 0L;
  
  SF2GlobalRegion globalregion = null;
  
  List<SF2InstrumentRegion> regions = new ArrayList();
  
  public SF2Instrument() { super(null, null, null, null); }
  
  public SF2Instrument(SF2Soundbank paramSF2Soundbank) { super(paramSF2Soundbank, null, null, null); }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public Patch getPatch() { return (this.bank == 128) ? new ModelPatch(0, this.preset, true) : new ModelPatch(this.bank << 7, this.preset, false); }
  
  public void setPatch(Patch paramPatch) {
    if (paramPatch instanceof ModelPatch && ((ModelPatch)paramPatch).isPercussion()) {
      this.bank = 128;
      this.preset = paramPatch.getProgram();
    } else {
      this.bank = paramPatch.getBank() >> 7;
      this.preset = paramPatch.getProgram();
    } 
  }
  
  public Object getData() { return null; }
  
  public long getGenre() { return this.genre; }
  
  public void setGenre(long paramLong) { this.genre = paramLong; }
  
  public long getLibrary() { return this.library; }
  
  public void setLibrary(long paramLong) { this.library = paramLong; }
  
  public long getMorphology() { return this.morphology; }
  
  public void setMorphology(long paramLong) { this.morphology = paramLong; }
  
  public List<SF2InstrumentRegion> getRegions() { return this.regions; }
  
  public SF2GlobalRegion getGlobalRegion() { return this.globalregion; }
  
  public void setGlobalZone(SF2GlobalRegion paramSF2GlobalRegion) { this.globalregion = paramSF2GlobalRegion; }
  
  public String toString() { return (this.bank == 128) ? ("Drumkit: " + this.name + " preset #" + this.preset) : ("Instrument: " + this.name + " bank #" + this.bank + " preset #" + this.preset); }
  
  public ModelPerformer[] getPerformers() {
    int i = 0;
    for (SF2InstrumentRegion sF2InstrumentRegion : this.regions)
      i += sF2InstrumentRegion.getLayer().getRegions().size(); 
    ModelPerformer[] arrayOfModelPerformer = new ModelPerformer[i];
    byte b = 0;
    SF2GlobalRegion sF2GlobalRegion = this.globalregion;
    for (SF2InstrumentRegion sF2InstrumentRegion : this.regions) {
      HashMap hashMap = new HashMap();
      hashMap.putAll(sF2InstrumentRegion.getGenerators());
      if (sF2GlobalRegion != null)
        hashMap.putAll(sF2GlobalRegion.getGenerators()); 
      SF2Layer sF2Layer = sF2InstrumentRegion.getLayer();
      SF2GlobalRegion sF2GlobalRegion1 = sF2Layer.getGlobalRegion();
      for (SF2LayerRegion sF2LayerRegion : sF2Layer.getRegions()) {
        ModelPerformer modelPerformer = new ModelPerformer();
        if (sF2LayerRegion.getSample() != null) {
          modelPerformer.setName(sF2LayerRegion.getSample().getName());
        } else {
          modelPerformer.setName(sF2Layer.getName());
        } 
        arrayOfModelPerformer[b++] = modelPerformer;
        byte b1 = 0;
        byte b2 = Byte.MAX_VALUE;
        byte b3 = 0;
        byte b4 = Byte.MAX_VALUE;
        if (sF2LayerRegion.contains(57))
          modelPerformer.setExclusiveClass(sF2LayerRegion.getInteger(57)); 
        if (sF2LayerRegion.contains(43)) {
          byte[] arrayOfByte = sF2LayerRegion.getBytes(43);
          if (arrayOfByte[0] >= 0 && arrayOfByte[0] > b1)
            b1 = arrayOfByte[0]; 
          if (arrayOfByte[1] >= 0 && arrayOfByte[1] < b2)
            b2 = arrayOfByte[1]; 
        } 
        if (sF2LayerRegion.contains(44)) {
          byte[] arrayOfByte = sF2LayerRegion.getBytes(44);
          if (arrayOfByte[0] >= 0 && arrayOfByte[0] > b3)
            b3 = arrayOfByte[0]; 
          if (arrayOfByte[1] >= 0 && arrayOfByte[1] < b4)
            b4 = arrayOfByte[1]; 
        } 
        if (sF2InstrumentRegion.contains(43)) {
          byte[] arrayOfByte = sF2InstrumentRegion.getBytes(43);
          if (arrayOfByte[0] > b1)
            b1 = arrayOfByte[0]; 
          if (arrayOfByte[1] < b2)
            b2 = arrayOfByte[1]; 
        } 
        if (sF2InstrumentRegion.contains(44)) {
          byte[] arrayOfByte = sF2InstrumentRegion.getBytes(44);
          if (arrayOfByte[0] > b3)
            b3 = arrayOfByte[0]; 
          if (arrayOfByte[1] < b4)
            b4 = arrayOfByte[1]; 
        } 
        modelPerformer.setKeyFrom(b1);
        modelPerformer.setKeyTo(b2);
        modelPerformer.setVelFrom(b3);
        modelPerformer.setVelTo(b4);
        short s1 = sF2LayerRegion.getShort(0);
        short s2 = sF2LayerRegion.getShort(1);
        short s3 = sF2LayerRegion.getShort(2);
        short s4 = sF2LayerRegion.getShort(3);
        s1 += sF2LayerRegion.getShort(4) * 32768;
        s2 += sF2LayerRegion.getShort(12) * 32768;
        s3 += sF2LayerRegion.getShort(45) * 32768;
        s4 += sF2LayerRegion.getShort(50) * 32768;
        s3 -= s1;
        s4 -= s1;
        SF2Sample sF2Sample = sF2LayerRegion.getSample();
        int j = sF2Sample.originalPitch;
        if (sF2LayerRegion.getShort(58) != -1)
          j = sF2LayerRegion.getShort(58); 
        float f = (-j * 100 + sF2Sample.pitchCorrection);
        ModelByteBuffer modelByteBuffer1 = sF2Sample.getDataBuffer();
        ModelByteBuffer modelByteBuffer2 = sF2Sample.getData24Buffer();
        if (s1 != 0 || s2 != 0) {
          modelByteBuffer1 = modelByteBuffer1.subbuffer((s1 * 2), modelByteBuffer1.capacity() + (s2 * 2));
          if (modelByteBuffer2 != null)
            modelByteBuffer2 = modelByteBuffer2.subbuffer(s1, modelByteBuffer2.capacity() + s2); 
        } 
        ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(modelByteBuffer1, sF2Sample.getFormat(), f);
        if (modelByteBuffer2 != null)
          modelByteBufferWavetable.set8BitExtensionBuffer(modelByteBuffer2); 
        HashMap hashMap1 = new HashMap();
        if (sF2GlobalRegion1 != null)
          hashMap1.putAll(sF2GlobalRegion1.getGenerators()); 
        hashMap1.putAll(sF2LayerRegion.getGenerators());
        for (Map.Entry entry : hashMap.entrySet()) {
          if (!hashMap1.containsKey(entry.getKey())) {
            s = sF2LayerRegion.getShort(((Integer)entry.getKey()).intValue());
          } else {
            s = ((Short)hashMap1.get(entry.getKey())).shortValue();
          } 
          short s = (short)(s + ((Short)entry.getValue()).shortValue());
          hashMap1.put(entry.getKey(), Short.valueOf(s));
        } 
        short s5 = getGeneratorValue(hashMap1, 54);
        if ((s5 == 1 || s5 == 3) && sF2Sample.startLoop >= 0L && sF2Sample.endLoop > 0L) {
          modelByteBufferWavetable.setLoopStart((int)(sF2Sample.startLoop + s3));
          modelByteBufferWavetable.setLoopLength((int)(sF2Sample.endLoop - sF2Sample.startLoop + s4 - s3));
          if (s5 == 1)
            modelByteBufferWavetable.setLoopType(1); 
          if (s5 == 3)
            modelByteBufferWavetable.setLoopType(2); 
        } 
        modelPerformer.getOscillators().add(modelByteBufferWavetable);
        short s6 = getGeneratorValue(hashMap1, 33);
        short s7 = getGeneratorValue(hashMap1, 34);
        short s8 = getGeneratorValue(hashMap1, 35);
        short s9 = getGeneratorValue(hashMap1, 36);
        short s10 = getGeneratorValue(hashMap1, 37);
        short s11 = getGeneratorValue(hashMap1, 38);
        if (s8 != -12000) {
          short s = getGeneratorValue(hashMap1, 39);
          s8 = (short)(s8 + 60 * s);
          float f1 = (-s * 128);
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_EG1_HOLD;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), f1, new ModelDestination(modelIdentifier2)));
        } 
        if (s9 != -12000) {
          short s = getGeneratorValue(hashMap1, 40);
          s9 = (short)(s9 + 60 * s);
          float f1 = (-s * 128);
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_EG1_DECAY;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), f1, new ModelDestination(modelIdentifier2)));
        } 
        addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_DELAY, s6);
        addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_ATTACK, s7);
        addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_HOLD, s8);
        addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_DECAY, s9);
        s10 = (short)(1000 - s10);
        if (s10 < 0)
          s10 = 0; 
        if (s10 > 1000)
          s10 = 1000; 
        addValue(modelPerformer, ModelDestination.DESTINATION_EG1_SUSTAIN, s10);
        addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_RELEASE, s11);
        if (getGeneratorValue(hashMap1, 11) != 0 || getGeneratorValue(hashMap1, 7) != 0) {
          short s15 = getGeneratorValue(hashMap1, 25);
          short s16 = getGeneratorValue(hashMap1, 26);
          short s17 = getGeneratorValue(hashMap1, 27);
          short s18 = getGeneratorValue(hashMap1, 28);
          short s19 = getGeneratorValue(hashMap1, 29);
          short s20 = getGeneratorValue(hashMap1, 30);
          if (s17 != -12000) {
            short s = getGeneratorValue(hashMap1, 31);
            s17 = (short)(s17 + 60 * s);
            float f1 = (-s * 128);
            ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
            ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_EG2_HOLD;
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), f1, new ModelDestination(modelIdentifier2)));
          } 
          if (s18 != -12000) {
            short s = getGeneratorValue(hashMap1, 32);
            s18 = (short)(s18 + 60 * s);
            float f1 = (-s * 128);
            ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
            ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_EG2_DECAY;
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), f1, new ModelDestination(modelIdentifier2)));
          } 
          addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_DELAY, s15);
          addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_ATTACK, s16);
          addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_HOLD, s17);
          addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_DECAY, s18);
          if (s19 < 0)
            s19 = 0; 
          if (s19 > 1000)
            s19 = 1000; 
          addValue(modelPerformer, ModelDestination.DESTINATION_EG2_SUSTAIN, (1000 - s19));
          addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_RELEASE, s20);
          if (getGeneratorValue(hashMap1, 11) != 0) {
            double d = getGeneratorValue(hashMap1, 11);
            ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_EG2;
            ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_FILTER_FREQ;
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), d, new ModelDestination(modelIdentifier2)));
          } 
          if (getGeneratorValue(hashMap1, 7) != 0) {
            double d = getGeneratorValue(hashMap1, 7);
            ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_EG2;
            ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_PITCH;
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), d, new ModelDestination(modelIdentifier2)));
          } 
        } 
        if (getGeneratorValue(hashMap1, 10) != 0 || getGeneratorValue(hashMap1, 5) != 0 || getGeneratorValue(hashMap1, 13) != 0) {
          short s15 = getGeneratorValue(hashMap1, 22);
          short s16 = getGeneratorValue(hashMap1, 21);
          addTimecentValue(modelPerformer, ModelDestination.DESTINATION_LFO1_DELAY, s16);
          addValue(modelPerformer, ModelDestination.DESTINATION_LFO1_FREQ, s15);
        } 
        short s12 = getGeneratorValue(hashMap1, 24);
        short s13 = getGeneratorValue(hashMap1, 23);
        addTimecentValue(modelPerformer, ModelDestination.DESTINATION_LFO2_DELAY, s13);
        addValue(modelPerformer, ModelDestination.DESTINATION_LFO2_FREQ, s12);
        if (getGeneratorValue(hashMap1, 6) != 0) {
          double d = getGeneratorValue(hashMap1, 6);
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_LFO2;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_PITCH;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1, false, true), d, new ModelDestination(modelIdentifier2)));
        } 
        if (getGeneratorValue(hashMap1, 10) != 0) {
          double d = getGeneratorValue(hashMap1, 10);
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_LFO1;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_FILTER_FREQ;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1, false, true), d, new ModelDestination(modelIdentifier2)));
        } 
        if (getGeneratorValue(hashMap1, 5) != 0) {
          double d = getGeneratorValue(hashMap1, 5);
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_LFO1;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_PITCH;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1, false, true), d, new ModelDestination(modelIdentifier2)));
        } 
        if (getGeneratorValue(hashMap1, 13) != 0) {
          double d = getGeneratorValue(hashMap1, 13);
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_LFO1;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_GAIN;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1, false, true), d, new ModelDestination(modelIdentifier2)));
        } 
        if (sF2LayerRegion.getShort(46) != -1) {
          double d = sF2LayerRegion.getShort(46) / 128.0D;
          addValue(modelPerformer, ModelDestination.DESTINATION_KEYNUMBER, d);
        } 
        if (sF2LayerRegion.getShort(47) != -1) {
          double d = sF2LayerRegion.getShort(47) / 128.0D;
          addValue(modelPerformer, ModelDestination.DESTINATION_VELOCITY, d);
        } 
        if (getGeneratorValue(hashMap1, 8) < 13500) {
          short s15 = getGeneratorValue(hashMap1, 8);
          short s16 = getGeneratorValue(hashMap1, 9);
          addValue(modelPerformer, ModelDestination.DESTINATION_FILTER_FREQ, s15);
          addValue(modelPerformer, ModelDestination.DESTINATION_FILTER_Q, s16);
        } 
        short s14 = 100 * getGeneratorValue(hashMap1, 51);
        s14 += getGeneratorValue(hashMap1, 52);
        if (s14 != 0)
          addValue(modelPerformer, ModelDestination.DESTINATION_PITCH, (short)s14); 
        if (getGeneratorValue(hashMap1, 17) != 0) {
          short s = getGeneratorValue(hashMap1, 17);
          addValue(modelPerformer, ModelDestination.DESTINATION_PAN, s);
        } 
        if (getGeneratorValue(hashMap1, 48) != 0) {
          short s = getGeneratorValue(hashMap1, 48);
          addValue(modelPerformer, ModelDestination.DESTINATION_GAIN, (-0.376287F * s));
        } 
        if (getGeneratorValue(hashMap1, 15) != 0) {
          short s = getGeneratorValue(hashMap1, 15);
          addValue(modelPerformer, ModelDestination.DESTINATION_CHORUS, s);
        } 
        if (getGeneratorValue(hashMap1, 16) != 0) {
          short s = getGeneratorValue(hashMap1, 16);
          addValue(modelPerformer, ModelDestination.DESTINATION_REVERB, s);
        } 
        if (getGeneratorValue(hashMap1, 56) != 100) {
          short s = getGeneratorValue(hashMap1, 56);
          if (s == 0) {
            ModelIdentifier modelIdentifier = ModelDestination.DESTINATION_PITCH;
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(null, (j * 100), new ModelDestination(modelIdentifier)));
          } else {
            ModelIdentifier modelIdentifier = ModelDestination.DESTINATION_PITCH;
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(null, (j * (100 - s)), new ModelDestination(modelIdentifier)));
          } 
          ModelIdentifier modelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
          ModelIdentifier modelIdentifier2 = ModelDestination.DESTINATION_PITCH;
          modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(modelIdentifier1), (128 * s), new ModelDestination(modelIdentifier2)));
        } 
        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_VELOCITY, new ModelTransform(this) {
                  public double transform(double param1Double) { return (param1Double < 0.5D) ? (1.0D - param1Double * 2.0D) : 0.0D; }
                }), -2400.0D, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO2, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0D, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
        if (sF2Layer.getGlobalRegion() != null)
          for (SF2Modulator sF2Modulator : sF2Layer.getGlobalRegion().getModulators())
            convertModulator(modelPerformer, sF2Modulator);  
        for (SF2Modulator sF2Modulator : sF2LayerRegion.getModulators())
          convertModulator(modelPerformer, sF2Modulator); 
        if (sF2GlobalRegion != null)
          for (SF2Modulator sF2Modulator : sF2GlobalRegion.getModulators())
            convertModulator(modelPerformer, sF2Modulator);  
        for (SF2Modulator sF2Modulator : sF2InstrumentRegion.getModulators())
          convertModulator(modelPerformer, sF2Modulator); 
      } 
    } 
    return arrayOfModelPerformer;
  }
  
  private void convertModulator(ModelPerformer paramModelPerformer, SF2Modulator paramSF2Modulator) {
    ModelSource modelSource1 = convertSource(paramSF2Modulator.getSourceOperator());
    ModelSource modelSource2 = convertSource(paramSF2Modulator.getAmountSourceOperator());
    if (modelSource1 == null && paramSF2Modulator.getSourceOperator() != 0)
      return; 
    if (modelSource2 == null && paramSF2Modulator.getAmountSourceOperator() != 0)
      return; 
    double d = paramSF2Modulator.getAmount();
    double[] arrayOfDouble = new double[1];
    ModelSource[] arrayOfModelSource = new ModelSource[1];
    arrayOfDouble[0] = 1.0D;
    ModelDestination modelDestination = convertDestination(paramSF2Modulator.getDestinationOperator(), arrayOfDouble, arrayOfModelSource);
    d *= arrayOfDouble[0];
    if (modelDestination == null)
      return; 
    if (paramSF2Modulator.getTransportOperator() == 2)
      ((ModelStandardTransform)modelDestination.getTransform()).setTransform(4); 
    ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(modelSource1, modelSource2, d, modelDestination);
    if (arrayOfModelSource[false] != null)
      modelConnectionBlock.addSource(arrayOfModelSource[0]); 
    paramModelPerformer.getConnectionBlocks().add(modelConnectionBlock);
  }
  
  private static ModelSource convertSource(int paramInt) {
    if (paramInt == 0)
      return null; 
    ModelIdentifier modelIdentifier = null;
    int i = paramInt & 0x7F;
    if ((paramInt & 0x80) != 0) {
      modelIdentifier = new ModelIdentifier("midi_cc", Integer.toString(i));
    } else {
      if (i == 2)
        modelIdentifier = ModelSource.SOURCE_NOTEON_VELOCITY; 
      if (i == 3)
        modelIdentifier = ModelSource.SOURCE_NOTEON_KEYNUMBER; 
      if (i == 10)
        modelIdentifier = ModelSource.SOURCE_MIDI_POLY_PRESSURE; 
      if (i == 13)
        modelIdentifier = ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE; 
      if (i == 14)
        modelIdentifier = ModelSource.SOURCE_MIDI_PITCH; 
      if (i == 16)
        modelIdentifier = new ModelIdentifier("midi_rpn", "0"); 
    } 
    if (modelIdentifier == null)
      return null; 
    ModelSource modelSource = new ModelSource(modelIdentifier);
    ModelStandardTransform modelStandardTransform = (ModelStandardTransform)modelSource.getTransform();
    if ((0x100 & paramInt) != 0) {
      modelStandardTransform.setDirection(true);
    } else {
      modelStandardTransform.setDirection(false);
    } 
    if ((0x200 & paramInt) != 0) {
      modelStandardTransform.setPolarity(true);
    } else {
      modelStandardTransform.setPolarity(false);
    } 
    if ((0x400 & paramInt) != 0)
      modelStandardTransform.setTransform(1); 
    if ((0x800 & paramInt) != 0)
      modelStandardTransform.setTransform(2); 
    if ((0xC00 & paramInt) != 0)
      modelStandardTransform.setTransform(3); 
    return modelSource;
  }
  
  static ModelDestination convertDestination(int paramInt, double[] paramArrayOfDouble, ModelSource[] paramArrayOfModelSource) {
    ModelIdentifier modelIdentifier = null;
    switch (paramInt) {
      case 8:
        modelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
        break;
      case 9:
        modelIdentifier = ModelDestination.DESTINATION_FILTER_Q;
        break;
      case 15:
        modelIdentifier = ModelDestination.DESTINATION_CHORUS;
        break;
      case 16:
        modelIdentifier = ModelDestination.DESTINATION_REVERB;
        break;
      case 17:
        modelIdentifier = ModelDestination.DESTINATION_PAN;
        break;
      case 21:
        modelIdentifier = ModelDestination.DESTINATION_LFO1_DELAY;
        break;
      case 22:
        modelIdentifier = ModelDestination.DESTINATION_LFO1_FREQ;
        break;
      case 23:
        modelIdentifier = ModelDestination.DESTINATION_LFO2_DELAY;
        break;
      case 24:
        modelIdentifier = ModelDestination.DESTINATION_LFO2_FREQ;
        break;
      case 25:
        modelIdentifier = ModelDestination.DESTINATION_EG2_DELAY;
        break;
      case 26:
        modelIdentifier = ModelDestination.DESTINATION_EG2_ATTACK;
        break;
      case 27:
        modelIdentifier = ModelDestination.DESTINATION_EG2_HOLD;
        break;
      case 28:
        modelIdentifier = ModelDestination.DESTINATION_EG2_DECAY;
        break;
      case 29:
        modelIdentifier = ModelDestination.DESTINATION_EG2_SUSTAIN;
        paramArrayOfDouble[0] = -1.0D;
        break;
      case 30:
        modelIdentifier = ModelDestination.DESTINATION_EG2_RELEASE;
        break;
      case 33:
        modelIdentifier = ModelDestination.DESTINATION_EG1_DELAY;
        break;
      case 34:
        modelIdentifier = ModelDestination.DESTINATION_EG1_ATTACK;
        break;
      case 35:
        modelIdentifier = ModelDestination.DESTINATION_EG1_HOLD;
        break;
      case 36:
        modelIdentifier = ModelDestination.DESTINATION_EG1_DECAY;
        break;
      case 37:
        modelIdentifier = ModelDestination.DESTINATION_EG1_SUSTAIN;
        paramArrayOfDouble[0] = -1.0D;
        break;
      case 38:
        modelIdentifier = ModelDestination.DESTINATION_EG1_RELEASE;
        break;
      case 46:
        modelIdentifier = ModelDestination.DESTINATION_KEYNUMBER;
        break;
      case 47:
        modelIdentifier = ModelDestination.DESTINATION_VELOCITY;
        break;
      case 51:
        paramArrayOfDouble[0] = 100.0D;
        modelIdentifier = ModelDestination.DESTINATION_PITCH;
        break;
      case 52:
        modelIdentifier = ModelDestination.DESTINATION_PITCH;
        break;
      case 48:
        modelIdentifier = ModelDestination.DESTINATION_GAIN;
        paramArrayOfDouble[0] = -0.3762870132923126D;
        break;
      case 6:
        modelIdentifier = ModelDestination.DESTINATION_PITCH;
        paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO2, false, true);
        break;
      case 5:
        modelIdentifier = ModelDestination.DESTINATION_PITCH;
        paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
        break;
      case 10:
        modelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
        paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
        break;
      case 13:
        modelIdentifier = ModelDestination.DESTINATION_GAIN;
        paramArrayOfDouble[0] = -0.3762870132923126D;
        paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
        break;
      case 7:
        modelIdentifier = ModelDestination.DESTINATION_PITCH;
        paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
        break;
      case 11:
        modelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
        paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
        break;
    } 
    return (modelIdentifier != null) ? new ModelDestination(modelIdentifier) : null;
  }
  
  private void addTimecentValue(ModelPerformer paramModelPerformer, ModelIdentifier paramModelIdentifier, short paramShort) {
    double d;
    if (paramShort == -12000) {
      d = Double.NEGATIVE_INFINITY;
    } else {
      d = paramShort;
    } 
    paramModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(d, new ModelDestination(paramModelIdentifier)));
  }
  
  private void addValue(ModelPerformer paramModelPerformer, ModelIdentifier paramModelIdentifier, short paramShort) {
    double d = paramShort;
    paramModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(d, new ModelDestination(paramModelIdentifier)));
  }
  
  private void addValue(ModelPerformer paramModelPerformer, ModelIdentifier paramModelIdentifier, double paramDouble) {
    double d = paramDouble;
    paramModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(d, new ModelDestination(paramModelIdentifier)));
  }
  
  private short getGeneratorValue(Map<Integer, Short> paramMap, int paramInt) { return paramMap.containsKey(Integer.valueOf(paramInt)) ? ((Short)paramMap.get(Integer.valueOf(paramInt))).shortValue() : SF2Region.getDefaultValue(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SF2Instrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */