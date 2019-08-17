package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.VoiceStatus;

public final class SoftVoice extends VoiceStatus {
  public int exclusiveClass = 0;
  
  public boolean releaseTriggered = false;
  
  private int noteOn_noteNumber = 0;
  
  private int noteOn_velocity = 0;
  
  private int noteOff_velocity = 0;
  
  private int delay = 0;
  
  ModelChannelMixer channelmixer = null;
  
  double tunedKey = 0.0D;
  
  SoftTuning tuning = null;
  
  SoftChannel stealer_channel = null;
  
  ModelConnectionBlock[] stealer_extendedConnectionBlocks = null;
  
  SoftPerformer stealer_performer = null;
  
  ModelChannelMixer stealer_channelmixer = null;
  
  int stealer_voiceID = -1;
  
  int stealer_noteNumber = 0;
  
  int stealer_velocity = 0;
  
  boolean stealer_releaseTriggered = false;
  
  int voiceID = -1;
  
  boolean sustain = false;
  
  boolean sostenuto = false;
  
  boolean portamento = false;
  
  private final SoftFilter filter_left;
  
  private final SoftFilter filter_right;
  
  private final SoftProcess eg = new SoftEnvelopeGenerator();
  
  private final SoftProcess lfo = new SoftLowFrequencyOscillator();
  
  Map<String, SoftControl> objects = new HashMap();
  
  SoftSynthesizer synthesizer;
  
  SoftInstrument instrument;
  
  SoftPerformer performer;
  
  SoftChannel softchannel = null;
  
  boolean on = false;
  
  private boolean audiostarted = false;
  
  private boolean started = false;
  
  private boolean stopping = false;
  
  private float osc_attenuation = 0.0F;
  
  private ModelOscillatorStream osc_stream;
  
  private int osc_stream_nrofchannels;
  
  private float[][] osc_buff = new float[2][];
  
  private boolean osc_stream_off_transmitted = false;
  
  private boolean out_mixer_end = false;
  
  private float out_mixer_left = 0.0F;
  
  private float out_mixer_right = 0.0F;
  
  private float out_mixer_effect1 = 0.0F;
  
  private float out_mixer_effect2 = 0.0F;
  
  private float last_out_mixer_left = 0.0F;
  
  private float last_out_mixer_right = 0.0F;
  
  private float last_out_mixer_effect1 = 0.0F;
  
  private float last_out_mixer_effect2 = 0.0F;
  
  ModelConnectionBlock[] extendedConnectionBlocks = null;
  
  private ModelConnectionBlock[] connections;
  
  private double[] connections_last = new double[50];
  
  private double[][][] connections_src = new double[50][3][];
  
  private int[][] connections_src_kc = new int[50][3];
  
  private double[][] connections_dst = new double[50][];
  
  private boolean soundoff = false;
  
  private float lastMuteValue = 0.0F;
  
  private float lastSoloMuteValue = 0.0F;
  
  double[] co_noteon_keynumber = new double[1];
  
  double[] co_noteon_velocity = new double[1];
  
  double[] co_noteon_on = new double[1];
  
  private final SoftControl co_noteon = new SoftControl() {
      double[] keynumber = SoftVoice.this.co_noteon_keynumber;
      
      double[] velocity = SoftVoice.this.co_noteon_velocity;
      
      double[] on = SoftVoice.this.co_noteon_on;
      
      public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : (param1String.equals("keynumber") ? this.keynumber : (param1String.equals("velocity") ? this.velocity : (param1String.equals("on") ? this.on : null))); }
    };
  
  private final double[] co_mixer_active = new double[1];
  
  private final double[] co_mixer_gain = new double[1];
  
  private final double[] co_mixer_pan = new double[1];
  
  private final double[] co_mixer_balance = new double[1];
  
  private final double[] co_mixer_reverb = new double[1];
  
  private final double[] co_mixer_chorus = new double[1];
  
  private final SoftControl co_mixer = new SoftControl() {
      double[] active = SoftVoice.this.co_mixer_active;
      
      double[] gain = SoftVoice.this.co_mixer_gain;
      
      double[] pan = SoftVoice.this.co_mixer_pan;
      
      double[] balance = SoftVoice.this.co_mixer_balance;
      
      double[] reverb = SoftVoice.this.co_mixer_reverb;
      
      double[] chorus = SoftVoice.this.co_mixer_chorus;
      
      public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : (param1String.equals("active") ? this.active : (param1String.equals("gain") ? this.gain : (param1String.equals("pan") ? this.pan : (param1String.equals("balance") ? this.balance : (param1String.equals("reverb") ? this.reverb : (param1String.equals("chorus") ? this.chorus : null)))))); }
    };
  
  private final double[] co_osc_pitch = new double[1];
  
  private final SoftControl co_osc = new SoftControl() {
      double[] pitch = SoftVoice.this.co_osc_pitch;
      
      public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : (param1String.equals("pitch") ? this.pitch : null); }
    };
  
  private final double[] co_filter_freq = new double[1];
  
  private final double[] co_filter_type = new double[1];
  
  private final double[] co_filter_q = new double[1];
  
  private final SoftControl co_filter = new SoftControl() {
      double[] freq = SoftVoice.this.co_filter_freq;
      
      double[] ftype = SoftVoice.this.co_filter_type;
      
      double[] q = SoftVoice.this.co_filter_q;
      
      public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : (param1String.equals("freq") ? this.freq : (param1String.equals("type") ? this.ftype : (param1String.equals("q") ? this.q : null))); }
    };
  
  SoftResamplerStreamer resampler;
  
  private final int nrofchannels;
  
  public SoftVoice(SoftSynthesizer paramSoftSynthesizer) {
    this.synthesizer = paramSoftSynthesizer;
    this.filter_left = new SoftFilter(paramSoftSynthesizer.getFormat().getSampleRate());
    this.filter_right = new SoftFilter(paramSoftSynthesizer.getFormat().getSampleRate());
    this.nrofchannels = paramSoftSynthesizer.getFormat().getChannels();
  }
  
  private int getValueKC(ModelIdentifier paramModelIdentifier) {
    if (paramModelIdentifier.getObject().equals("midi_cc")) {
      int i = Integer.parseInt(paramModelIdentifier.getVariable());
      if (i != 0 && i != 32 && i < 120)
        return i; 
    } else if (paramModelIdentifier.getObject().equals("midi_rpn")) {
      if (paramModelIdentifier.getVariable().equals("1"))
        return 120; 
      if (paramModelIdentifier.getVariable().equals("2"))
        return 121; 
    } 
    return -1;
  }
  
  private double[] getValue(ModelIdentifier paramModelIdentifier) {
    SoftControl softControl = (SoftControl)this.objects.get(paramModelIdentifier.getObject());
    return (softControl == null) ? null : softControl.get(paramModelIdentifier.getInstance(), paramModelIdentifier.getVariable());
  }
  
  private double transformValue(double paramDouble, ModelSource paramModelSource) { return (paramModelSource.getTransform() != null) ? paramModelSource.getTransform().transform(paramDouble) : paramDouble; }
  
  private double transformValue(double paramDouble, ModelDestination paramModelDestination) { return (paramModelDestination.getTransform() != null) ? paramModelDestination.getTransform().transform(paramDouble) : paramDouble; }
  
  private double processKeyBasedController(double paramDouble, int paramInt) {
    if (paramInt == -1)
      return paramDouble; 
    if (this.softchannel.keybasedcontroller_active != null && this.softchannel.keybasedcontroller_active[this.note] != null && this.softchannel.keybasedcontroller_active[this.note][paramInt]) {
      double d = this.softchannel.keybasedcontroller_value[this.note][paramInt];
      if (paramInt == 10 || paramInt == 91 || paramInt == 93)
        return d; 
      paramDouble += d * 2.0D - 1.0D;
      if (paramDouble > 1.0D) {
        paramDouble = 1.0D;
      } else if (paramDouble < 0.0D) {
        paramDouble = 0.0D;
      } 
    } 
    return paramDouble;
  }
  
  private void processConnection(int paramInt) {
    ModelConnectionBlock modelConnectionBlock = this.connections[paramInt];
    double[][] arrayOfDouble = this.connections_src[paramInt];
    double[] arrayOfDouble1 = this.connections_dst[paramInt];
    if (arrayOfDouble1 == null || Double.isInfinite(arrayOfDouble1[0]))
      return; 
    double d = modelConnectionBlock.getScale();
    if (this.softchannel.keybasedcontroller_active == null) {
      ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
      for (byte b = 0; b < arrayOfModelSource.length; b++) {
        d *= transformValue(arrayOfDouble[b][0], arrayOfModelSource[b]);
        if (d == 0.0D)
          break; 
      } 
    } else {
      ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
      int[] arrayOfInt = this.connections_src_kc[paramInt];
      for (byte b = 0; b < arrayOfModelSource.length; b++) {
        d *= transformValue(processKeyBasedController(arrayOfDouble[b][0], arrayOfInt[b]), arrayOfModelSource[b]);
        if (d == 0.0D)
          break; 
      } 
    } 
    d = transformValue(d, modelConnectionBlock.getDestination());
    arrayOfDouble1[0] = arrayOfDouble1[0] - this.connections_last[paramInt] + d;
    this.connections_last[paramInt] = d;
  }
  
  void updateTuning(SoftTuning paramSoftTuning) {
    this.tuning = paramSoftTuning;
    this.tunedKey = this.tuning.getTuning(this.note) / 100.0D;
    if (!this.portamento) {
      this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125D;
      if (this.performer == null)
        return; 
      int[] arrayOfInt = this.performer.midi_connections[4];
      if (arrayOfInt == null)
        return; 
      for (byte b = 0; b < arrayOfInt.length; b++)
        processConnection(arrayOfInt[b]); 
    } 
  }
  
  void setNote(int paramInt) {
    this.note = paramInt;
    this.tunedKey = this.tuning.getTuning(paramInt) / 100.0D;
  }
  
  void noteOn(int paramInt1, int paramInt2, int paramInt3) {
    this.sustain = false;
    this.sostenuto = false;
    this.portamento = false;
    this.soundoff = false;
    this.on = true;
    this.active = true;
    this.started = true;
    this.noteOn_noteNumber = paramInt1;
    this.noteOn_velocity = paramInt2;
    this.delay = paramInt3;
    this.lastMuteValue = 0.0F;
    this.lastSoloMuteValue = 0.0F;
    setNote(paramInt1);
    if (this.performer.forcedKeynumber) {
      this.co_noteon_keynumber[0] = 0.0D;
    } else {
      this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125D;
    } 
    if (this.performer.forcedVelocity) {
      this.co_noteon_velocity[0] = 0.0D;
    } else {
      this.co_noteon_velocity[0] = (paramInt2 * 0.0078125F);
    } 
    this.co_mixer_active[0] = 0.0D;
    this.co_mixer_gain[0] = 0.0D;
    this.co_mixer_pan[0] = 0.0D;
    this.co_mixer_balance[0] = 0.0D;
    this.co_mixer_reverb[0] = 0.0D;
    this.co_mixer_chorus[0] = 0.0D;
    this.co_osc_pitch[0] = 0.0D;
    this.co_filter_freq[0] = 0.0D;
    this.co_filter_q[0] = 0.0D;
    this.co_filter_type[0] = 0.0D;
    this.co_noteon_on[0] = 1.0D;
    this.eg.reset();
    this.lfo.reset();
    this.filter_left.reset();
    this.filter_right.reset();
    this.objects.put("master", (this.synthesizer.getMainMixer()).co_master);
    this.objects.put("eg", this.eg);
    this.objects.put("lfo", this.lfo);
    this.objects.put("noteon", this.co_noteon);
    this.objects.put("osc", this.co_osc);
    this.objects.put("mixer", this.co_mixer);
    this.objects.put("filter", this.co_filter);
    this.connections = this.performer.connections;
    if (this.connections_last == null || this.connections_last.length < this.connections.length)
      this.connections_last = new double[this.connections.length]; 
    if (this.connections_src == null || this.connections_src.length < this.connections.length) {
      this.connections_src = new double[this.connections.length][][];
      this.connections_src_kc = new int[this.connections.length][];
    } 
    if (this.connections_dst == null || this.connections_dst.length < this.connections.length)
      this.connections_dst = new double[this.connections.length][]; 
    byte b;
    for (b = 0; b < this.connections.length; b++) {
      ModelConnectionBlock modelConnectionBlock = this.connections[b];
      this.connections_last[b] = 0.0D;
      if (modelConnectionBlock.getSources() != null) {
        ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
        if (this.connections_src[b] == null || this.connections_src[b].length < arrayOfModelSource.length) {
          this.connections_src[b] = new double[arrayOfModelSource.length][];
          this.connections_src_kc[b] = new int[arrayOfModelSource.length];
        } 
        double[][] arrayOfDouble = this.connections_src[b];
        int[] arrayOfInt = this.connections_src_kc[b];
        this.connections_src[b] = arrayOfDouble;
        for (byte b1 = 0; b1 < arrayOfModelSource.length; b1++) {
          arrayOfInt[b1] = getValueKC(arrayOfModelSource[b1].getIdentifier());
          arrayOfDouble[b1] = getValue(arrayOfModelSource[b1].getIdentifier());
        } 
      } 
      if (modelConnectionBlock.getDestination() != null) {
        this.connections_dst[b] = getValue(modelConnectionBlock.getDestination().getIdentifier());
      } else {
        this.connections_dst[b] = null;
      } 
    } 
    for (b = 0; b < this.connections.length; b++)
      processConnection(b); 
    if (this.extendedConnectionBlocks != null)
      for (ModelConnectionBlock modelConnectionBlock : this.extendedConnectionBlocks) {
        double d = 0.0D;
        if (this.softchannel.keybasedcontroller_active == null) {
          for (ModelSource modelSource : modelConnectionBlock.getSources()) {
            double d1 = getValue(modelSource.getIdentifier())[0];
            ModelTransform modelTransform1 = modelSource.getTransform();
            if (modelTransform1 == null) {
              d += d1;
            } else {
              d += modelTransform1.transform(d1);
            } 
          } 
        } else {
          for (ModelSource modelSource : modelConnectionBlock.getSources()) {
            double d1 = getValue(modelSource.getIdentifier())[0];
            d1 = processKeyBasedController(d1, getValueKC(modelSource.getIdentifier()));
            ModelTransform modelTransform1 = modelSource.getTransform();
            if (modelTransform1 == null) {
              d += d1;
            } else {
              d += modelTransform1.transform(d1);
            } 
          } 
        } 
        ModelDestination modelDestination = modelConnectionBlock.getDestination();
        ModelTransform modelTransform = modelDestination.getTransform();
        if (modelTransform != null)
          d = modelTransform.transform(d); 
        getValue(modelDestination.getIdentifier())[0] = getValue(modelDestination.getIdentifier())[0] + d;
      }  
    this.eg.init(this.synthesizer);
    this.lfo.init(this.synthesizer);
  }
  
  void setPolyPressure(int paramInt) {
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_connections[2];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void setChannelPressure(int paramInt) {
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_connections[1];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void controlChange(int paramInt1, int paramInt2) {
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_ctrl_connections[paramInt1];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void nrpnChange(int paramInt1, int paramInt2) {
    if (this.performer == null)
      return; 
    int[] arrayOfInt = (int[])this.performer.midi_nrpn_connections.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void rpnChange(int paramInt1, int paramInt2) {
    if (this.performer == null)
      return; 
    int[] arrayOfInt = (int[])this.performer.midi_rpn_connections.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void setPitchBend(int paramInt) {
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_connections[0];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void setMute(boolean paramBoolean) {
    this.co_mixer_gain[0] = this.co_mixer_gain[0] - this.lastMuteValue;
    this.lastMuteValue = paramBoolean ? -960.0F : 0.0F;
    this.co_mixer_gain[0] = this.co_mixer_gain[0] + this.lastMuteValue;
  }
  
  void setSoloMute(boolean paramBoolean) {
    this.co_mixer_gain[0] = this.co_mixer_gain[0] - this.lastSoloMuteValue;
    this.lastSoloMuteValue = paramBoolean ? -960.0F : 0.0F;
    this.co_mixer_gain[0] = this.co_mixer_gain[0] + this.lastSoloMuteValue;
  }
  
  void shutdown() {
    if (this.co_noteon_on[0] < -0.5D)
      return; 
    this.on = false;
    this.co_noteon_on[0] = -1.0D;
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_connections[3];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void soundOff() {
    this.on = false;
    this.soundoff = true;
  }
  
  void noteOff(int paramInt) {
    if (!this.on)
      return; 
    this.on = false;
    this.noteOff_velocity = paramInt;
    if (this.softchannel.sustain) {
      this.sustain = true;
      return;
    } 
    if (this.sostenuto)
      return; 
    this.co_noteon_on[0] = 0.0D;
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_connections[3];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void redamp() {
    if (this.co_noteon_on[0] > 0.5D)
      return; 
    if (this.co_noteon_on[0] < -0.5D)
      return; 
    this.sustain = true;
    this.co_noteon_on[0] = 1.0D;
    if (this.performer == null)
      return; 
    int[] arrayOfInt = this.performer.midi_connections[3];
    if (arrayOfInt == null)
      return; 
    for (byte b = 0; b < arrayOfInt.length; b++)
      processConnection(arrayOfInt[b]); 
  }
  
  void processControlLogic() {
    if (this.stopping) {
      this.active = false;
      this.stopping = false;
      this.audiostarted = false;
      this.instrument = null;
      this.performer = null;
      this.connections = null;
      this.extendedConnectionBlocks = null;
      this.channelmixer = null;
      if (this.osc_stream != null)
        try {
          this.osc_stream.close();
        } catch (IOException iOException) {} 
      if (this.stealer_channel != null) {
        this.stealer_channel.initVoice(this, this.stealer_performer, this.stealer_voiceID, this.stealer_noteNumber, this.stealer_velocity, 0, this.stealer_extendedConnectionBlocks, this.stealer_channelmixer, this.stealer_releaseTriggered);
        this.stealer_releaseTriggered = false;
        this.stealer_channel = null;
        this.stealer_performer = null;
        this.stealer_voiceID = -1;
        this.stealer_noteNumber = 0;
        this.stealer_velocity = 0;
        this.stealer_extendedConnectionBlocks = null;
        this.stealer_channelmixer = null;
      } 
    } 
    if (this.started) {
      this.audiostarted = true;
      ModelOscillator modelOscillator = this.performer.oscillators[0];
      this.osc_stream_off_transmitted = false;
      if (modelOscillator instanceof ModelWavetable) {
        try {
          this.resampler.open((ModelWavetable)modelOscillator, this.synthesizer.getFormat().getSampleRate());
          this.osc_stream = this.resampler;
        } catch (IOException iOException) {}
      } else {
        this.osc_stream = modelOscillator.open(this.synthesizer.getFormat().getSampleRate());
      } 
      this.osc_attenuation = modelOscillator.getAttenuation();
      this.osc_stream_nrofchannels = modelOscillator.getChannels();
      if (this.osc_buff == null || this.osc_buff.length < this.osc_stream_nrofchannels)
        this.osc_buff = new float[this.osc_stream_nrofchannels][]; 
      if (this.osc_stream != null)
        this.osc_stream.noteOn(this.softchannel, this, this.noteOn_noteNumber, this.noteOn_velocity); 
    } 
    if (this.audiostarted) {
      double d1;
      if (this.portamento) {
        double d5 = this.tunedKey - this.co_noteon_keynumber[0] * 128.0D;
        double d6 = Math.abs(d5);
        if (d6 < 1.0E-10D) {
          this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125D;
          this.portamento = false;
        } else {
          if (d6 > this.softchannel.portamento_time)
            d5 = Math.signum(d5) * this.softchannel.portamento_time; 
          this.co_noteon_keynumber[0] = this.co_noteon_keynumber[0] + d5 * 0.0078125D;
        } 
        int[] arrayOfInt = this.performer.midi_connections[4];
        if (arrayOfInt == null)
          return; 
        for (byte b = 0; b < arrayOfInt.length; b++)
          processConnection(arrayOfInt[b]); 
      } 
      this.eg.processControlLogic();
      this.lfo.processControlLogic();
      int i;
      for (i = 0; i < this.performer.ctrl_connections.length; i++)
        processConnection(this.performer.ctrl_connections[i]); 
      this.osc_stream.setPitch((float)this.co_osc_pitch[0]);
      i = (int)this.co_filter_type[0];
      if (this.co_filter_freq[0] == 13500.0D) {
        d1 = 19912.126958213175D;
      } else {
        d1 = 440.0D * Math.exp((this.co_filter_freq[0] - 6900.0D) * Math.log(2.0D) / 1200.0D);
      } 
      double d2 = this.co_filter_q[0] / 10.0D;
      this.filter_left.setFilterType(i);
      this.filter_left.setFrequency(d1);
      this.filter_left.setResonance(d2);
      this.filter_right.setFilterType(i);
      this.filter_right.setFrequency(d1);
      this.filter_right.setResonance(d2);
      float f = (float)Math.exp((-this.osc_attenuation + this.co_mixer_gain[0]) * Math.log(10.0D) / 200.0D);
      if (this.co_mixer_gain[0] <= -960.0D)
        f = 0.0F; 
      if (this.soundoff) {
        this.stopping = true;
        f = 0.0F;
      } 
      this.volume = (int)(Math.sqrt(f) * 128.0D);
      double d3 = this.co_mixer_pan[0] * 0.001D;
      if (d3 < 0.0D) {
        d3 = 0.0D;
      } else if (d3 > 1.0D) {
        d3 = 1.0D;
      } 
      if (d3 == 0.5D) {
        this.out_mixer_left = f * 0.70710677F;
        this.out_mixer_right = this.out_mixer_left;
      } else {
        this.out_mixer_left = f * (float)Math.cos(d3 * Math.PI * 0.5D);
        this.out_mixer_right = f * (float)Math.sin(d3 * Math.PI * 0.5D);
      } 
      double d4 = this.co_mixer_balance[0] * 0.001D;
      if (d4 != 0.5D)
        if (d4 > 0.5D) {
          this.out_mixer_left = (float)(this.out_mixer_left * (1.0D - d4) * 2.0D);
        } else {
          this.out_mixer_right = (float)(this.out_mixer_right * d4 * 2.0D);
        }  
      if (this.synthesizer.reverb_on) {
        this.out_mixer_effect1 = (float)(this.co_mixer_reverb[0] * 0.001D);
        this.out_mixer_effect1 *= f;
      } else {
        this.out_mixer_effect1 = 0.0F;
      } 
      if (this.synthesizer.chorus_on) {
        this.out_mixer_effect2 = (float)(this.co_mixer_chorus[0] * 0.001D);
        this.out_mixer_effect2 *= f;
      } else {
        this.out_mixer_effect2 = 0.0F;
      } 
      this.out_mixer_end = (this.co_mixer_active[0] < 0.5D);
      if (!this.on && !this.osc_stream_off_transmitted) {
        this.osc_stream_off_transmitted = true;
        if (this.osc_stream != null)
          this.osc_stream.noteOff(this.noteOff_velocity); 
      } 
    } 
    if (this.started) {
      this.last_out_mixer_left = this.out_mixer_left;
      this.last_out_mixer_right = this.out_mixer_right;
      this.last_out_mixer_effect1 = this.out_mixer_effect1;
      this.last_out_mixer_effect2 = this.out_mixer_effect2;
      this.started = false;
    } 
  }
  
  void mixAudioStream(SoftAudioBuffer paramSoftAudioBuffer1, SoftAudioBuffer paramSoftAudioBuffer2, SoftAudioBuffer paramSoftAudioBuffer3, float paramFloat1, float paramFloat2) {
    int i = paramSoftAudioBuffer1.getSize();
    if (paramFloat1 < 1.0E-9D && paramFloat2 < 1.0E-9D)
      return; 
    if (paramSoftAudioBuffer3 != null && this.delay != 0) {
      if (paramFloat1 == paramFloat2) {
        float[] arrayOfFloat1 = paramSoftAudioBuffer2.array();
        float[] arrayOfFloat2 = paramSoftAudioBuffer1.array();
        byte b = 0;
        int j;
        for (j = this.delay; j < i; j++)
          arrayOfFloat1[j] = arrayOfFloat1[j] + arrayOfFloat2[b++] * paramFloat2; 
        arrayOfFloat1 = paramSoftAudioBuffer3.array();
        for (j = 0; j < this.delay; j++)
          arrayOfFloat1[j] = arrayOfFloat1[j] + arrayOfFloat2[b++] * paramFloat2; 
      } else {
        float f1 = paramFloat1;
        float f2 = (paramFloat2 - paramFloat1) / i;
        float[] arrayOfFloat1 = paramSoftAudioBuffer2.array();
        float[] arrayOfFloat2 = paramSoftAudioBuffer1.array();
        byte b = 0;
        int j;
        for (j = this.delay; j < i; j++) {
          f1 += f2;
          arrayOfFloat1[j] = arrayOfFloat1[j] + arrayOfFloat2[b++] * f1;
        } 
        arrayOfFloat1 = paramSoftAudioBuffer3.array();
        for (j = 0; j < this.delay; j++) {
          f1 += f2;
          arrayOfFloat1[j] = arrayOfFloat1[j] + arrayOfFloat2[b++] * f1;
        } 
      } 
    } else if (paramFloat1 == paramFloat2) {
      float[] arrayOfFloat1 = paramSoftAudioBuffer2.array();
      float[] arrayOfFloat2 = paramSoftAudioBuffer1.array();
      for (byte b = 0; b < i; b++)
        arrayOfFloat1[b] = arrayOfFloat1[b] + arrayOfFloat2[b] * paramFloat2; 
    } else {
      float f1 = paramFloat1;
      float f2 = (paramFloat2 - paramFloat1) / i;
      float[] arrayOfFloat1 = paramSoftAudioBuffer2.array();
      float[] arrayOfFloat2 = paramSoftAudioBuffer1.array();
      for (byte b = 0; b < i; b++) {
        f1 += f2;
        arrayOfFloat1[b] = arrayOfFloat1[b] + arrayOfFloat2[b] * f1;
      } 
    } 
  }
  
  void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer) {
    if (!this.audiostarted)
      return; 
    int i = paramArrayOfSoftAudioBuffer[0].getSize();
    try {
      this.osc_buff[0] = paramArrayOfSoftAudioBuffer[10].array();
      if (this.nrofchannels != 1)
        this.osc_buff[1] = paramArrayOfSoftAudioBuffer[11].array(); 
      int j = this.osc_stream.read(this.osc_buff, 0, i);
      if (j == -1) {
        this.stopping = true;
        return;
      } 
      if (j != i) {
        Arrays.fill(this.osc_buff[0], j, i, 0.0F);
        if (this.nrofchannels != 1)
          Arrays.fill(this.osc_buff[1], j, i, 0.0F); 
      } 
    } catch (IOException iOException) {}
    SoftAudioBuffer softAudioBuffer1 = paramArrayOfSoftAudioBuffer[0];
    SoftAudioBuffer softAudioBuffer2 = paramArrayOfSoftAudioBuffer[1];
    SoftAudioBuffer softAudioBuffer3 = paramArrayOfSoftAudioBuffer[2];
    SoftAudioBuffer softAudioBuffer4 = paramArrayOfSoftAudioBuffer[6];
    SoftAudioBuffer softAudioBuffer5 = paramArrayOfSoftAudioBuffer[7];
    SoftAudioBuffer softAudioBuffer6 = paramArrayOfSoftAudioBuffer[3];
    SoftAudioBuffer softAudioBuffer7 = paramArrayOfSoftAudioBuffer[4];
    SoftAudioBuffer softAudioBuffer8 = paramArrayOfSoftAudioBuffer[5];
    SoftAudioBuffer softAudioBuffer9 = paramArrayOfSoftAudioBuffer[8];
    SoftAudioBuffer softAudioBuffer10 = paramArrayOfSoftAudioBuffer[9];
    SoftAudioBuffer softAudioBuffer11 = paramArrayOfSoftAudioBuffer[10];
    SoftAudioBuffer softAudioBuffer12 = paramArrayOfSoftAudioBuffer[11];
    if (this.osc_stream_nrofchannels == 1)
      softAudioBuffer12 = null; 
    if (!Double.isInfinite(this.co_filter_freq[0])) {
      this.filter_left.processAudio(softAudioBuffer11);
      if (softAudioBuffer12 != null)
        this.filter_right.processAudio(softAudioBuffer12); 
    } 
    if (this.nrofchannels == 1) {
      this.out_mixer_left = (this.out_mixer_left + this.out_mixer_right) / 2.0F;
      mixAudioStream(softAudioBuffer11, softAudioBuffer1, softAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
      if (softAudioBuffer12 != null)
        mixAudioStream(softAudioBuffer12, softAudioBuffer1, softAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left); 
    } else if (softAudioBuffer12 == null && this.last_out_mixer_left == this.last_out_mixer_right && this.out_mixer_left == this.out_mixer_right) {
      mixAudioStream(softAudioBuffer11, softAudioBuffer3, softAudioBuffer8, this.last_out_mixer_left, this.out_mixer_left);
    } else {
      mixAudioStream(softAudioBuffer11, softAudioBuffer1, softAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
      if (softAudioBuffer12 != null) {
        mixAudioStream(softAudioBuffer12, softAudioBuffer2, softAudioBuffer7, this.last_out_mixer_right, this.out_mixer_right);
      } else {
        mixAudioStream(softAudioBuffer11, softAudioBuffer2, softAudioBuffer7, this.last_out_mixer_right, this.out_mixer_right);
      } 
    } 
    if (softAudioBuffer12 == null) {
      mixAudioStream(softAudioBuffer11, softAudioBuffer4, softAudioBuffer9, this.last_out_mixer_effect1, this.out_mixer_effect1);
      mixAudioStream(softAudioBuffer11, softAudioBuffer5, softAudioBuffer10, this.last_out_mixer_effect2, this.out_mixer_effect2);
    } else {
      mixAudioStream(softAudioBuffer11, softAudioBuffer4, softAudioBuffer9, this.last_out_mixer_effect1 * 0.5F, this.out_mixer_effect1 * 0.5F);
      mixAudioStream(softAudioBuffer11, softAudioBuffer5, softAudioBuffer10, this.last_out_mixer_effect2 * 0.5F, this.out_mixer_effect2 * 0.5F);
      mixAudioStream(softAudioBuffer12, softAudioBuffer4, softAudioBuffer9, this.last_out_mixer_effect1 * 0.5F, this.out_mixer_effect1 * 0.5F);
      mixAudioStream(softAudioBuffer12, softAudioBuffer5, softAudioBuffer10, this.last_out_mixer_effect2 * 0.5F, this.out_mixer_effect2 * 0.5F);
    } 
    this.last_out_mixer_left = this.out_mixer_left;
    this.last_out_mixer_right = this.out_mixer_right;
    this.last_out_mixer_effect1 = this.out_mixer_effect1;
    this.last_out_mixer_effect2 = this.out_mixer_effect2;
    if (this.out_mixer_end)
      this.stopping = true; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftVoice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */