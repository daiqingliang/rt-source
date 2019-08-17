package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

public final class SoftChannel implements MidiChannel, ModelDirectedPlayer {
  private static boolean[] dontResetControls = new boolean[128];
  
  private static final int RPN_NULL_VALUE = 16383;
  
  private int rpn_control = 16383;
  
  private int nrpn_control = 16383;
  
  double portamento_time = 1.0D;
  
  int[] portamento_lastnote = new int[128];
  
  int portamento_lastnote_ix = 0;
  
  private boolean portamento = false;
  
  private boolean mono = false;
  
  private boolean mute = false;
  
  private boolean solo = false;
  
  private boolean solomute = false;
  
  private final Object control_mutex;
  
  private int channel;
  
  private SoftVoice[] voices;
  
  private int bank;
  
  private int program;
  
  private SoftSynthesizer synthesizer;
  
  private SoftMainMixer mainmixer;
  
  private int[] polypressure = new int[128];
  
  private int channelpressure = 0;
  
  private int[] controller = new int[128];
  
  private int pitchbend;
  
  private double[] co_midi_pitch = new double[1];
  
  private double[] co_midi_channel_pressure = new double[1];
  
  SoftTuning tuning = new SoftTuning();
  
  int tuning_bank = 0;
  
  int tuning_program = 0;
  
  SoftInstrument current_instrument = null;
  
  ModelChannelMixer current_mixer = null;
  
  ModelDirector current_director = null;
  
  int cds_control_number = -1;
  
  ModelConnectionBlock[] cds_control_connections = null;
  
  ModelConnectionBlock[] cds_channelpressure_connections = null;
  
  ModelConnectionBlock[] cds_polypressure_connections = null;
  
  boolean sustain = false;
  
  boolean[][] keybasedcontroller_active = (boolean[][])null;
  
  double[][] keybasedcontroller_value = (double[][])null;
  
  private SoftControl[] co_midi = new SoftControl[128];
  
  private double[][] co_midi_cc_cc;
  
  private SoftControl co_midi_cc;
  
  Map<Integer, int[]> co_midi_rpn_rpn_i;
  
  Map<Integer, double[]> co_midi_rpn_rpn;
  
  private SoftControl co_midi_rpn;
  
  Map<Integer, int[]> co_midi_nrpn_nrpn_i;
  
  Map<Integer, double[]> co_midi_nrpn_nrpn;
  
  private SoftControl co_midi_nrpn;
  
  private int[] lastVelocity;
  
  private int prevVoiceID;
  
  private boolean firstVoice;
  
  private int voiceNo;
  
  private int play_noteNumber;
  
  private int play_velocity;
  
  private int play_delay;
  
  private boolean play_releasetriggered;
  
  private static int restrict7Bit(int paramInt) { return (paramInt < 0) ? 0 : ((paramInt > 127) ? 127 : paramInt); }
  
  private static int restrict14Bit(int paramInt) { return (paramInt < 0) ? 0 : ((paramInt > 16256) ? 16256 : paramInt); }
  
  public SoftChannel(SoftSynthesizer paramSoftSynthesizer, int paramInt) {
    for (byte b = 0; b < this.co_midi.length; b++)
      this.co_midi[b] = new MidiControlObject(null); 
    this.co_midi_cc_cc = new double[128][1];
    this.co_midi_cc = new SoftControl() {
        double[][] cc = SoftChannel.this.co_midi_cc_cc;
        
        public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : this.cc[Integer.parseInt(param1String)]; }
      };
    this.co_midi_rpn_rpn_i = new HashMap();
    this.co_midi_rpn_rpn = new HashMap();
    this.co_midi_rpn = new SoftControl() {
        Map<Integer, double[]> rpn = SoftChannel.this.co_midi_rpn_rpn;
        
        public double[] get(int param1Int, String param1String) {
          if (param1String == null)
            return null; 
          int i = Integer.parseInt(param1String);
          double[] arrayOfDouble = (double[])this.rpn.get(Integer.valueOf(i));
          if (arrayOfDouble == null) {
            arrayOfDouble = new double[1];
            this.rpn.put(Integer.valueOf(i), arrayOfDouble);
          } 
          return arrayOfDouble;
        }
      };
    this.co_midi_nrpn_nrpn_i = new HashMap();
    this.co_midi_nrpn_nrpn = new HashMap();
    this.co_midi_nrpn = new SoftControl() {
        Map<Integer, double[]> nrpn = SoftChannel.this.co_midi_nrpn_nrpn;
        
        public double[] get(int param1Int, String param1String) {
          if (param1String == null)
            return null; 
          int i = Integer.parseInt(param1String);
          double[] arrayOfDouble = (double[])this.nrpn.get(Integer.valueOf(i));
          if (arrayOfDouble == null) {
            arrayOfDouble = new double[1];
            this.nrpn.put(Integer.valueOf(i), arrayOfDouble);
          } 
          return arrayOfDouble;
        }
      };
    this.lastVelocity = new int[128];
    this.firstVoice = true;
    this.voiceNo = 0;
    this.play_noteNumber = 0;
    this.play_velocity = 0;
    this.play_delay = 0;
    this.play_releasetriggered = false;
    this.channel = paramInt;
    this.voices = paramSoftSynthesizer.getVoices();
    this.synthesizer = paramSoftSynthesizer;
    this.mainmixer = paramSoftSynthesizer.getMainMixer();
    this.control_mutex = paramSoftSynthesizer.control_mutex;
    resetAllControllers(true);
  }
  
  private int findFreeVoice(int paramInt) {
    if (paramInt == -1)
      return -1; 
    int i;
    for (i = paramInt; i < this.voices.length; i++) {
      if (!(this.voices[i]).active)
        return i; 
    } 
    i = this.synthesizer.getVoiceAllocationMode();
    if (i == 1) {
      int j = this.channel;
      byte b3;
      for (b3 = 0; b3 < this.voices.length; b3++) {
        if ((this.voices[b3]).stealer_channel == null)
          if (j == 9) {
            j = (this.voices[b3]).channel;
          } else if ((this.voices[b3]).channel != 9 && (this.voices[b3]).channel > j) {
            j = (this.voices[b3]).channel;
          }  
      } 
      b3 = -1;
      SoftVoice softVoice1 = null;
      byte b4;
      for (b4 = 0; b4 < this.voices.length; b4++) {
        if ((this.voices[b4]).channel == j && (this.voices[b4]).stealer_channel == null && !(this.voices[b4]).on) {
          if (softVoice1 == null) {
            softVoice1 = this.voices[b4];
            b3 = b4;
          } 
          if ((this.voices[b4]).voiceID < softVoice1.voiceID) {
            softVoice1 = this.voices[b4];
            b3 = b4;
          } 
        } 
      } 
      if (b3 == -1)
        for (b4 = 0; b4 < this.voices.length; b4++) {
          if ((this.voices[b4]).channel == j && (this.voices[b4]).stealer_channel == null) {
            if (softVoice1 == null) {
              softVoice1 = this.voices[b4];
              b3 = b4;
            } 
            if ((this.voices[b4]).voiceID < softVoice1.voiceID) {
              softVoice1 = this.voices[b4];
              b3 = b4;
            } 
          } 
        }  
      return b3;
    } 
    byte b1 = -1;
    SoftVoice softVoice = null;
    byte b2;
    for (b2 = 0; b2 < this.voices.length; b2++) {
      if ((this.voices[b2]).stealer_channel == null && !(this.voices[b2]).on) {
        if (softVoice == null) {
          softVoice = this.voices[b2];
          b1 = b2;
        } 
        if ((this.voices[b2]).voiceID < softVoice.voiceID) {
          softVoice = this.voices[b2];
          b1 = b2;
        } 
      } 
    } 
    if (b1 == -1)
      for (b2 = 0; b2 < this.voices.length; b2++) {
        if ((this.voices[b2]).stealer_channel == null) {
          if (softVoice == null) {
            softVoice = this.voices[b2];
            b1 = b2;
          } 
          if ((this.voices[b2]).voiceID < softVoice.voiceID) {
            softVoice = this.voices[b2];
            b1 = b2;
          } 
        } 
      }  
    return b1;
  }
  
  void initVoice(SoftVoice paramSoftVoice, SoftPerformer paramSoftPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ModelConnectionBlock[] paramArrayOfModelConnectionBlock, ModelChannelMixer paramModelChannelMixer, boolean paramBoolean) {
    if (paramSoftVoice.active) {
      paramSoftVoice.stealer_channel = this;
      paramSoftVoice.stealer_performer = paramSoftPerformer;
      paramSoftVoice.stealer_voiceID = paramInt1;
      paramSoftVoice.stealer_noteNumber = paramInt2;
      paramSoftVoice.stealer_velocity = paramInt3;
      paramSoftVoice.stealer_extendedConnectionBlocks = paramArrayOfModelConnectionBlock;
      paramSoftVoice.stealer_channelmixer = paramModelChannelMixer;
      paramSoftVoice.stealer_releaseTriggered = paramBoolean;
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active && (this.voices[b]).voiceID == paramSoftVoice.voiceID)
          this.voices[b].soundOff(); 
      } 
      return;
    } 
    paramSoftVoice.extendedConnectionBlocks = paramArrayOfModelConnectionBlock;
    paramSoftVoice.channelmixer = paramModelChannelMixer;
    paramSoftVoice.releaseTriggered = paramBoolean;
    paramSoftVoice.voiceID = paramInt1;
    paramSoftVoice.tuning = this.tuning;
    paramSoftVoice.exclusiveClass = paramSoftPerformer.exclusiveClass;
    paramSoftVoice.softchannel = this;
    paramSoftVoice.channel = this.channel;
    paramSoftVoice.bank = this.bank;
    paramSoftVoice.program = this.program;
    paramSoftVoice.instrument = this.current_instrument;
    paramSoftVoice.performer = paramSoftPerformer;
    paramSoftVoice.objects.clear();
    paramSoftVoice.objects.put("midi", this.co_midi[paramInt2]);
    paramSoftVoice.objects.put("midi_cc", this.co_midi_cc);
    paramSoftVoice.objects.put("midi_rpn", this.co_midi_rpn);
    paramSoftVoice.objects.put("midi_nrpn", this.co_midi_nrpn);
    paramSoftVoice.noteOn(paramInt2, paramInt3, paramInt4);
    paramSoftVoice.setMute(this.mute);
    paramSoftVoice.setSoloMute(this.solomute);
    if (paramBoolean)
      return; 
    if (this.controller[84] != 0) {
      paramSoftVoice.co_noteon_keynumber[0] = this.tuning.getTuning(this.controller[84]) / 100.0D * 0.0078125D;
      paramSoftVoice.portamento = true;
      controlChange(84, 0);
    } else if (this.portamento) {
      if (this.mono) {
        if (this.portamento_lastnote[0] != -1) {
          paramSoftVoice.co_noteon_keynumber[0] = this.tuning.getTuning(this.portamento_lastnote[0]) / 100.0D * 0.0078125D;
          paramSoftVoice.portamento = true;
          controlChange(84, 0);
        } 
        this.portamento_lastnote[0] = paramInt2;
      } else if (this.portamento_lastnote_ix != 0) {
        this.portamento_lastnote_ix--;
        paramSoftVoice.co_noteon_keynumber[0] = this.tuning.getTuning(this.portamento_lastnote[this.portamento_lastnote_ix]) / 100.0D * 0.0078125D;
        paramSoftVoice.portamento = true;
      } 
    } 
  }
  
  public void noteOn(int paramInt1, int paramInt2) { noteOn(paramInt1, paramInt2, 0); }
  
  void noteOn(int paramInt1, int paramInt2, int paramInt3) {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    noteOn_internal(paramInt1, paramInt2, paramInt3);
    if (this.current_mixer != null)
      this.current_mixer.noteOn(paramInt1, paramInt2); 
  }
  
  private void noteOn_internal(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 == 0) {
      noteOff_internal(paramInt1, 64);
      return;
    } 
    synchronized (this.control_mutex) {
      if (this.sustain) {
        this.sustain = false;
        for (byte b = 0; b < this.voices.length; b++) {
          if (((this.voices[b]).sustain || (this.voices[b]).on) && (this.voices[b]).channel == this.channel && (this.voices[b]).active && (this.voices[b]).note == paramInt1) {
            (this.voices[b]).sustain = false;
            (this.voices[b]).on = true;
            this.voices[b].noteOff(0);
          } 
        } 
        this.sustain = true;
      } 
      this.mainmixer.activity();
      if (this.mono) {
        if (this.portamento) {
          boolean bool = false;
          for (byte b = 0; b < this.voices.length; b++) {
            if ((this.voices[b]).on && (this.voices[b]).channel == this.channel && (this.voices[b]).active && !(this.voices[b]).releaseTriggered) {
              (this.voices[b]).portamento = true;
              this.voices[b].setNote(paramInt1);
              bool = true;
            } 
          } 
          if (bool) {
            this.portamento_lastnote[0] = paramInt1;
            return;
          } 
        } 
        if (this.controller[84] != 0) {
          boolean bool = false;
          for (byte b = 0; b < this.voices.length; b++) {
            if ((this.voices[b]).on && (this.voices[b]).channel == this.channel && (this.voices[b]).active && (this.voices[b]).note == this.controller[84] && !(this.voices[b]).releaseTriggered) {
              (this.voices[b]).portamento = true;
              this.voices[b].setNote(paramInt1);
              bool = true;
            } 
          } 
          controlChange(84, 0);
          if (bool)
            return; 
        } 
      } 
      if (this.mono)
        allNotesOff(); 
      if (this.current_instrument == null) {
        this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
        if (this.current_instrument == null)
          return; 
        if (this.current_mixer != null)
          this.mainmixer.stopMixer(this.current_mixer); 
        this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
        if (this.current_mixer != null)
          this.mainmixer.registerMixer(this.current_mixer); 
        this.current_director = this.current_instrument.getDirector(this, this);
        applyInstrumentCustomization();
      } 
      this.prevVoiceID = this.synthesizer.voiceIDCounter++;
      this.firstVoice = true;
      this.voiceNo = 0;
      int i = (int)Math.round(this.tuning.getTuning(paramInt1) / 100.0D);
      this.play_noteNumber = paramInt1;
      this.play_velocity = paramInt2;
      this.play_delay = paramInt3;
      this.play_releasetriggered = false;
      this.lastVelocity[paramInt1] = paramInt2;
      this.current_director.noteOn(i, paramInt2);
    } 
  }
  
  public void noteOff(int paramInt1, int paramInt2) {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    noteOff_internal(paramInt1, paramInt2);
    if (this.current_mixer != null)
      this.current_mixer.noteOff(paramInt1, paramInt2); 
  }
  
  private void noteOff_internal(int paramInt1, int paramInt2) {
    synchronized (this.control_mutex) {
      if (!this.mono && this.portamento && this.portamento_lastnote_ix != 127) {
        this.portamento_lastnote[this.portamento_lastnote_ix] = paramInt1;
        this.portamento_lastnote_ix++;
      } 
      this.mainmixer.activity();
      int i;
      for (i = 0; i < this.voices.length; i++) {
        if ((this.voices[i]).on && (this.voices[i]).channel == this.channel && (this.voices[i]).note == paramInt1 && !(this.voices[i]).releaseTriggered)
          this.voices[i].noteOff(paramInt2); 
        if ((this.voices[i]).stealer_channel == this && (this.voices[i]).stealer_noteNumber == paramInt1) {
          SoftVoice softVoice = this.voices[i];
          softVoice.stealer_releaseTriggered = false;
          softVoice.stealer_channel = null;
          softVoice.stealer_performer = null;
          softVoice.stealer_voiceID = -1;
          softVoice.stealer_noteNumber = 0;
          softVoice.stealer_velocity = 0;
          softVoice.stealer_extendedConnectionBlocks = null;
          softVoice.stealer_channelmixer = null;
        } 
      } 
      if (this.current_instrument == null) {
        this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
        if (this.current_instrument == null)
          return; 
        if (this.current_mixer != null)
          this.mainmixer.stopMixer(this.current_mixer); 
        this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
        if (this.current_mixer != null)
          this.mainmixer.registerMixer(this.current_mixer); 
        this.current_director = this.current_instrument.getDirector(this, this);
        applyInstrumentCustomization();
      } 
      this.prevVoiceID = this.synthesizer.voiceIDCounter++;
      this.firstVoice = true;
      this.voiceNo = 0;
      i = (int)Math.round(this.tuning.getTuning(paramInt1) / 100.0D);
      this.play_noteNumber = paramInt1;
      this.play_velocity = this.lastVelocity[paramInt1];
      this.play_releasetriggered = true;
      this.play_delay = 0;
      this.current_director.noteOff(i, paramInt2);
    } 
  }
  
  public void play(int paramInt, ModelConnectionBlock[] paramArrayOfModelConnectionBlock) {
    int i = this.play_noteNumber;
    int j = this.play_velocity;
    int k = this.play_delay;
    boolean bool = this.play_releasetriggered;
    SoftPerformer softPerformer = this.current_instrument.getPerformer(paramInt);
    if (this.firstVoice) {
      this.firstVoice = false;
      if (softPerformer.exclusiveClass != 0) {
        int m = softPerformer.exclusiveClass;
        for (byte b = 0; b < this.voices.length; b++) {
          if ((this.voices[b]).active && (this.voices[b]).channel == this.channel && (this.voices[b]).exclusiveClass == m && (!softPerformer.selfNonExclusive || (this.voices[b]).note != i))
            this.voices[b].shutdown(); 
        } 
      } 
    } 
    this.voiceNo = findFreeVoice(this.voiceNo);
    if (this.voiceNo == -1)
      return; 
    initVoice(this.voices[this.voiceNo], softPerformer, this.prevVoiceID, i, j, k, paramArrayOfModelConnectionBlock, this.current_mixer, bool);
  }
  
  public void noteOff(int paramInt) {
    if (paramInt < 0 || paramInt > 127)
      return; 
    noteOff_internal(paramInt, 64);
  }
  
  public void setPolyPressure(int paramInt1, int paramInt2) {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    if (this.current_mixer != null)
      this.current_mixer.setPolyPressure(paramInt1, paramInt2); 
    synchronized (this.control_mutex) {
      this.mainmixer.activity();
      this.co_midi[paramInt1].get(0, "poly_pressure")[0] = paramInt2 * 0.0078125D;
      this.polypressure[paramInt1] = paramInt2;
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active && (this.voices[b]).note == paramInt1)
          this.voices[b].setPolyPressure(paramInt2); 
      } 
    } 
  }
  
  public int getPolyPressure(int paramInt) {
    synchronized (this.control_mutex) {
      return this.polypressure[paramInt];
    } 
  }
  
  public void setChannelPressure(int paramInt) {
    paramInt = restrict7Bit(paramInt);
    if (this.current_mixer != null)
      this.current_mixer.setChannelPressure(paramInt); 
    synchronized (this.control_mutex) {
      this.mainmixer.activity();
      this.co_midi_channel_pressure[0] = paramInt * 0.0078125D;
      this.channelpressure = paramInt;
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active)
          this.voices[b].setChannelPressure(paramInt); 
      } 
    } 
  }
  
  public int getChannelPressure() {
    synchronized (this.control_mutex) {
      return this.channelpressure;
    } 
  }
  
  void applyInstrumentCustomization() {
    if (this.cds_control_connections == null && this.cds_channelpressure_connections == null && this.cds_polypressure_connections == null)
      return; 
    ModelInstrument modelInstrument = this.current_instrument.getSourceInstrument();
    ModelPerformer[] arrayOfModelPerformer1 = modelInstrument.getPerformers();
    ModelPerformer[] arrayOfModelPerformer2 = new ModelPerformer[arrayOfModelPerformer1.length];
    for (byte b = 0; b < arrayOfModelPerformer2.length; b++) {
      ModelPerformer modelPerformer1 = arrayOfModelPerformer1[b];
      ModelPerformer modelPerformer2 = new ModelPerformer();
      modelPerformer2.setName(modelPerformer1.getName());
      modelPerformer2.setExclusiveClass(modelPerformer1.getExclusiveClass());
      modelPerformer2.setKeyFrom(modelPerformer1.getKeyFrom());
      modelPerformer2.setKeyTo(modelPerformer1.getKeyTo());
      modelPerformer2.setVelFrom(modelPerformer1.getVelFrom());
      modelPerformer2.setVelTo(modelPerformer1.getVelTo());
      modelPerformer2.getOscillators().addAll(modelPerformer1.getOscillators());
      modelPerformer2.getConnectionBlocks().addAll(modelPerformer1.getConnectionBlocks());
      arrayOfModelPerformer2[b] = modelPerformer2;
      List list = modelPerformer2.getConnectionBlocks();
      if (this.cds_control_connections != null) {
        String str = Integer.toString(this.cds_control_number);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          ModelConnectionBlock modelConnectionBlock = (ModelConnectionBlock)iterator.next();
          ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
          boolean bool = false;
          if (arrayOfModelSource != null)
            for (byte b2 = 0; b2 < arrayOfModelSource.length; b2++) {
              ModelSource modelSource = arrayOfModelSource[b2];
              if ("midi_cc".equals(modelSource.getIdentifier().getObject()) && str.equals(modelSource.getIdentifier().getVariable()))
                bool = true; 
            }  
          if (bool)
            iterator.remove(); 
        } 
        for (byte b1 = 0; b1 < this.cds_control_connections.length; b1++)
          list.add(this.cds_control_connections[b1]); 
      } 
      if (this.cds_polypressure_connections != null) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          ModelConnectionBlock modelConnectionBlock = (ModelConnectionBlock)iterator.next();
          ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
          boolean bool = false;
          if (arrayOfModelSource != null)
            for (byte b2 = 0; b2 < arrayOfModelSource.length; b2++) {
              ModelSource modelSource = arrayOfModelSource[b2];
              if ("midi".equals(modelSource.getIdentifier().getObject()) && "poly_pressure".equals(modelSource.getIdentifier().getVariable()))
                bool = true; 
            }  
          if (bool)
            iterator.remove(); 
        } 
        for (byte b1 = 0; b1 < this.cds_polypressure_connections.length; b1++)
          list.add(this.cds_polypressure_connections[b1]); 
      } 
      if (this.cds_channelpressure_connections != null) {
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          ModelConnectionBlock modelConnectionBlock = (ModelConnectionBlock)iterator.next();
          ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
          boolean bool = false;
          if (arrayOfModelSource != null)
            for (byte b2 = 0; b2 < arrayOfModelSource.length; b2++) {
              ModelIdentifier modelIdentifier = arrayOfModelSource[b2].getIdentifier();
              if ("midi".equals(modelIdentifier.getObject()) && "channel_pressure".equals(modelIdentifier.getVariable()))
                bool = true; 
            }  
          if (bool)
            iterator.remove(); 
        } 
        for (byte b1 = 0; b1 < this.cds_channelpressure_connections.length; b1++)
          list.add(this.cds_channelpressure_connections[b1]); 
      } 
    } 
    this.current_instrument = new SoftInstrument(modelInstrument, arrayOfModelPerformer2);
  }
  
  private ModelConnectionBlock[] createModelConnections(ModelIdentifier paramModelIdentifier, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < paramArrayOfInt1.length; b++) {
      int i = paramArrayOfInt1[b];
      int j = paramArrayOfInt2[b];
      if (i == 0) {
        final double scale = ((j - 64) * 100);
        ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        arrayList.add(modelConnectionBlock);
      } 
      if (i == 1) {
        ModelConnectionBlock modelConnectionBlock;
        final double scale = (j / 64.0D - 1.0D) * 9600.0D;
        if (d > 0.0D) {
          modelConnectionBlock = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, true, false, 0), -d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        } else {
          modelConnectionBlock = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        } 
        arrayList.add(modelConnectionBlock);
      } 
      if (i == 2) {
        final double scale = j / 64.0D;
        ModelTransform modelTransform = new ModelTransform() {
            double s = scale;
            
            public double transform(double param1Double) {
              if (this.s < 1.0D) {
                param1Double = this.s + param1Double * (1.0D - this.s);
              } else if (this.s > 1.0D) {
                param1Double = 1.0D + param1Double * (this.s - 1.0D);
              } else {
                return 0.0D;
              } 
              return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(param1Double);
            }
          };
        ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, modelTransform), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        arrayList.add(modelConnectionBlock);
      } 
      if (i == 3) {
        final double scale = (j / 64.0D - 1.0D) * 9600.0D;
        ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_PITCH));
        arrayList.add(modelConnectionBlock);
      } 
      if (i == 4) {
        final double scale = j / 128.0D * 2400.0D;
        ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        arrayList.add(modelConnectionBlock);
      } 
      if (i == 5) {
        final double scale = j / 127.0D;
        ModelTransform modelTransform = new ModelTransform() {
            double s = scale;
            
            public double transform(double param1Double) { return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - param1Double * this.s); }
          };
        ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, false, 0), new ModelSource(paramModelIdentifier, modelTransform), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        arrayList.add(modelConnectionBlock);
      } 
    } 
    return (ModelConnectionBlock[])arrayList.toArray(new ModelConnectionBlock[arrayList.size()]);
  }
  
  public void mapPolyPressureToDestination(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    this.current_instrument = null;
    if (paramArrayOfInt1.length == 0) {
      this.cds_polypressure_connections = null;
      return;
    } 
    this.cds_polypressure_connections = createModelConnections(new ModelIdentifier("midi", "poly_pressure"), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void mapChannelPressureToDestination(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    this.current_instrument = null;
    if (paramArrayOfInt1.length == 0) {
      this.cds_channelpressure_connections = null;
      return;
    } 
    this.cds_channelpressure_connections = createModelConnections(new ModelIdentifier("midi", "channel_pressure"), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void mapControlToDestination(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if ((paramInt < 1 || paramInt > 31) && (paramInt < 64 || paramInt > 95)) {
      this.cds_control_connections = null;
      return;
    } 
    this.current_instrument = null;
    this.cds_control_number = paramInt;
    if (paramArrayOfInt1.length == 0) {
      this.cds_control_connections = null;
      return;
    } 
    this.cds_control_connections = createModelConnections(new ModelIdentifier("midi_cc", Integer.toString(paramInt)), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void controlChangePerNote(int paramInt1, int paramInt2, int paramInt3) {
    if (this.keybasedcontroller_active == null) {
      this.keybasedcontroller_active = new boolean[128][];
      this.keybasedcontroller_value = new double[128][];
    } 
    if (this.keybasedcontroller_active[paramInt1] == null) {
      this.keybasedcontroller_active[paramInt1] = new boolean[128];
      Arrays.fill(this.keybasedcontroller_active[paramInt1], false);
      this.keybasedcontroller_value[paramInt1] = new double[128];
      Arrays.fill(this.keybasedcontroller_value[paramInt1], 0.0D);
    } 
    if (paramInt3 == -1) {
      this.keybasedcontroller_active[paramInt1][paramInt2] = false;
    } else {
      this.keybasedcontroller_active[paramInt1][paramInt2] = true;
      this.keybasedcontroller_value[paramInt1][paramInt2] = paramInt3 / 128.0D;
    } 
    if (paramInt2 < 120) {
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active)
          this.voices[b].controlChange(paramInt2, -1); 
      } 
    } else if (paramInt2 == 120) {
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active)
          this.voices[b].rpnChange(1, -1); 
      } 
    } else if (paramInt2 == 121) {
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active)
          this.voices[b].rpnChange(2, -1); 
      } 
    } 
  }
  
  public int getControlPerNote(int paramInt1, int paramInt2) { return (this.keybasedcontroller_active == null) ? -1 : ((this.keybasedcontroller_active[paramInt1] == null) ? -1 : (!this.keybasedcontroller_active[paramInt1][paramInt2] ? -1 : (int)(this.keybasedcontroller_value[paramInt1][paramInt2] * 128.0D))); }
  
  public void controlChange(int paramInt1, int paramInt2) {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    if (this.current_mixer != null)
      this.current_mixer.controlChange(paramInt1, paramInt2); 
    synchronized (this.control_mutex) {
      boolean bool;
      int i;
      double d;
      switch (paramInt1) {
        case 5:
          d = -Math.asin(paramInt2 / 128.0D * 2.0D - 1.0D) / Math.PI + 0.5D;
          d = Math.pow(100000.0D, d) / 100.0D;
          d /= 100.0D;
          d *= 1000.0D;
          d /= this.synthesizer.getControlRate();
          this.portamento_time = d;
          break;
        case 6:
        case 38:
        case 96:
        case 97:
          i = 0;
          if (this.nrpn_control != 16383) {
            int[] arrayOfInt = (int[])this.co_midi_nrpn_nrpn_i.get(Integer.valueOf(this.nrpn_control));
            if (arrayOfInt != null)
              i = arrayOfInt[0]; 
          } 
          if (this.rpn_control != 16383) {
            int[] arrayOfInt = (int[])this.co_midi_rpn_rpn_i.get(Integer.valueOf(this.rpn_control));
            if (arrayOfInt != null)
              i = arrayOfInt[0]; 
          } 
          if (paramInt1 == 6) {
            i = (i & 0x7F) + (paramInt2 << 7);
          } else if (paramInt1 == 38) {
            i = (i & 0x3F80) + paramInt2;
          } else if (paramInt1 == 96 || paramInt1 == 97) {
            int j = 1;
            if (this.rpn_control == 2 || this.rpn_control == 3 || this.rpn_control == 4)
              j = 128; 
            if (paramInt1 == 96)
              i += j; 
            if (paramInt1 == 97)
              i -= j; 
          } 
          if (this.nrpn_control != 16383)
            nrpnChange(this.nrpn_control, i); 
          if (this.rpn_control != 16383)
            rpnChange(this.rpn_control, i); 
          break;
        case 64:
          bool = (paramInt2 >= 64) ? 1 : 0;
          if (this.sustain != bool) {
            this.sustain = bool;
            if (!bool) {
              for (byte b2 = 0; b2 < this.voices.length; b2++) {
                if ((this.voices[b2]).active && (this.voices[b2]).sustain && (this.voices[b2]).channel == this.channel) {
                  (this.voices[b2]).sustain = false;
                  if (!(this.voices[b2]).on) {
                    (this.voices[b2]).on = true;
                    this.voices[b2].noteOff(0);
                  } 
                } 
              } 
              break;
            } 
            for (byte b1 = 0; b1 < this.voices.length; b1++) {
              if ((this.voices[b1]).active && (this.voices[b1]).channel == this.channel)
                this.voices[b1].redamp(); 
            } 
          } 
          break;
        case 65:
          this.portamento = (paramInt2 >= 64);
          this.portamento_lastnote[0] = -1;
          this.portamento_lastnote_ix = 0;
          break;
        case 66:
          bool = (paramInt2 >= 64) ? 1 : 0;
          if (bool)
            for (byte b1 = 0; b1 < this.voices.length; b1++) {
              if ((this.voices[b1]).active && (this.voices[b1]).on && (this.voices[b1]).channel == this.channel)
                (this.voices[b1]).sostenuto = true; 
            }  
          if (!bool)
            for (byte b1 = 0; b1 < this.voices.length; b1++) {
              if ((this.voices[b1]).active && (this.voices[b1]).sostenuto && (this.voices[b1]).channel == this.channel) {
                (this.voices[b1]).sostenuto = false;
                if (!(this.voices[b1]).on) {
                  (this.voices[b1]).on = true;
                  this.voices[b1].noteOff(0);
                } 
              } 
            }  
          break;
        case 98:
          this.nrpn_control = (this.nrpn_control & 0x3F80) + paramInt2;
          this.rpn_control = 16383;
          break;
        case 99:
          this.nrpn_control = (this.nrpn_control & 0x7F) + (paramInt2 << 7);
          this.rpn_control = 16383;
          break;
        case 100:
          this.rpn_control = (this.rpn_control & 0x3F80) + paramInt2;
          this.nrpn_control = 16383;
          break;
        case 101:
          this.rpn_control = (this.rpn_control & 0x7F) + (paramInt2 << 7);
          this.nrpn_control = 16383;
          break;
        case 120:
          allSoundOff();
          break;
        case 121:
          resetAllControllers((paramInt2 == 127));
          break;
        case 122:
          localControl((paramInt2 >= 64));
          break;
        case 123:
          allNotesOff();
          break;
        case 124:
          setOmni(false);
          break;
        case 125:
          setOmni(true);
          break;
        case 126:
          if (paramInt2 == 1)
            setMono(true); 
          break;
        case 127:
          setMono(false);
          break;
      } 
      this.co_midi_cc_cc[paramInt1][0] = paramInt2 * 0.0078125D;
      if (paramInt1 == 0) {
        this.bank = paramInt2 << 7;
        return;
      } 
      if (paramInt1 == 32) {
        this.bank = (this.bank & 0x3F80) + paramInt2;
        return;
      } 
      this.controller[paramInt1] = paramInt2;
      if (paramInt1 < 32)
        this.controller[paramInt1 + 32] = 0; 
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active)
          this.voices[b].controlChange(paramInt1, paramInt2); 
      } 
    } 
  }
  
  public int getController(int paramInt) {
    synchronized (this.control_mutex) {
      return this.controller[paramInt] & 0x7F;
    } 
  }
  
  public void tuningChange(int paramInt) { tuningChange(0, paramInt); }
  
  public void tuningChange(int paramInt1, int paramInt2) {
    synchronized (this.control_mutex) {
      this.tuning = this.synthesizer.getTuning(new Patch(paramInt1, paramInt2));
    } 
  }
  
  public void programChange(int paramInt) { programChange(this.bank, paramInt); }
  
  public void programChange(int paramInt1, int paramInt2) {
    paramInt1 = restrict14Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    synchronized (this.control_mutex) {
      this.mainmixer.activity();
      if (this.bank != paramInt1 || this.program != paramInt2) {
        this.bank = paramInt1;
        this.program = paramInt2;
        this.current_instrument = null;
      } 
    } 
  }
  
  public int getProgram() {
    synchronized (this.control_mutex) {
      return this.program;
    } 
  }
  
  public void setPitchBend(int paramInt) {
    paramInt = restrict14Bit(paramInt);
    if (this.current_mixer != null)
      this.current_mixer.setPitchBend(paramInt); 
    synchronized (this.control_mutex) {
      this.mainmixer.activity();
      this.co_midi_pitch[0] = paramInt * 6.103515625E-5D;
      this.pitchbend = paramInt;
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active)
          this.voices[b].setPitchBend(paramInt); 
      } 
    } 
  }
  
  public int getPitchBend() {
    synchronized (this.control_mutex) {
      return this.pitchbend;
    } 
  }
  
  public void nrpnChange(int paramInt1, int paramInt2) {
    if (this.synthesizer.getGeneralMidiMode() == 0) {
      if (paramInt1 == 136)
        controlChange(76, paramInt2 >> 7); 
      if (paramInt1 == 137)
        controlChange(77, paramInt2 >> 7); 
      if (paramInt1 == 138)
        controlChange(78, paramInt2 >> 7); 
      if (paramInt1 == 160)
        controlChange(74, paramInt2 >> 7); 
      if (paramInt1 == 161)
        controlChange(71, paramInt2 >> 7); 
      if (paramInt1 == 227)
        controlChange(73, paramInt2 >> 7); 
      if (paramInt1 == 228)
        controlChange(75, paramInt2 >> 7); 
      if (paramInt1 == 230)
        controlChange(72, paramInt2 >> 7); 
      if (paramInt1 >> 7 == 24)
        controlChangePerNote(paramInt1 % 128, 120, paramInt2 >> 7); 
      if (paramInt1 >> 7 == 26)
        controlChangePerNote(paramInt1 % 128, 7, paramInt2 >> 7); 
      if (paramInt1 >> 7 == 28)
        controlChangePerNote(paramInt1 % 128, 10, paramInt2 >> 7); 
      if (paramInt1 >> 7 == 29)
        controlChangePerNote(paramInt1 % 128, 91, paramInt2 >> 7); 
      if (paramInt1 >> 7 == 30)
        controlChangePerNote(paramInt1 % 128, 93, paramInt2 >> 7); 
    } 
    int[] arrayOfInt = (int[])this.co_midi_nrpn_nrpn_i.get(Integer.valueOf(paramInt1));
    double[] arrayOfDouble = (double[])this.co_midi_nrpn_nrpn.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null) {
      arrayOfInt = new int[1];
      this.co_midi_nrpn_nrpn_i.put(Integer.valueOf(paramInt1), arrayOfInt);
    } 
    if (arrayOfDouble == null) {
      arrayOfDouble = new double[1];
      this.co_midi_nrpn_nrpn.put(Integer.valueOf(paramInt1), arrayOfDouble);
    } 
    arrayOfInt[0] = paramInt2;
    arrayOfDouble[0] = arrayOfInt[0] * 6.103515625E-5D;
    for (byte b = 0; b < this.voices.length; b++) {
      if ((this.voices[b]).active)
        this.voices[b].nrpnChange(paramInt1, arrayOfInt[0]); 
    } 
  }
  
  public void rpnChange(int paramInt1, int paramInt2) {
    if (paramInt1 == 3) {
      this.tuning_program = paramInt2 >> 7 & 0x7F;
      tuningChange(this.tuning_bank, this.tuning_program);
    } 
    if (paramInt1 == 4)
      this.tuning_bank = paramInt2 >> 7 & 0x7F; 
    int[] arrayOfInt = (int[])this.co_midi_rpn_rpn_i.get(Integer.valueOf(paramInt1));
    double[] arrayOfDouble = (double[])this.co_midi_rpn_rpn.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null) {
      arrayOfInt = new int[1];
      this.co_midi_rpn_rpn_i.put(Integer.valueOf(paramInt1), arrayOfInt);
    } 
    if (arrayOfDouble == null) {
      arrayOfDouble = new double[1];
      this.co_midi_rpn_rpn.put(Integer.valueOf(paramInt1), arrayOfDouble);
    } 
    arrayOfInt[0] = paramInt2;
    arrayOfDouble[0] = arrayOfInt[0] * 6.103515625E-5D;
    for (byte b = 0; b < this.voices.length; b++) {
      if ((this.voices[b]).active)
        this.voices[b].rpnChange(paramInt1, arrayOfInt[0]); 
    } 
  }
  
  public void resetAllControllers() { resetAllControllers(false); }
  
  public void resetAllControllers(boolean paramBoolean) {
    synchronized (this.control_mutex) {
      this.mainmixer.activity();
      byte b;
      for (b = 0; b < ''; b++)
        setPolyPressure(b, 0); 
      setChannelPressure(0);
      setPitchBend(8192);
      for (b = 0; b < ''; b++) {
        if (!dontResetControls[b])
          controlChange(b, 0); 
      } 
      controlChange(71, 64);
      controlChange(72, 64);
      controlChange(73, 64);
      controlChange(74, 64);
      controlChange(75, 64);
      controlChange(76, 64);
      controlChange(77, 64);
      controlChange(78, 64);
      controlChange(8, 64);
      controlChange(11, 127);
      controlChange(98, 127);
      controlChange(99, 127);
      controlChange(100, 127);
      controlChange(101, 127);
      if (paramBoolean) {
        this.keybasedcontroller_active = (boolean[][])null;
        this.keybasedcontroller_value = (double[][])null;
        controlChange(7, 100);
        controlChange(10, 64);
        controlChange(91, 40);
        Iterator iterator = this.co_midi_rpn_rpn.keySet().iterator();
        while (iterator.hasNext()) {
          int i = ((Integer)iterator.next()).intValue();
          if (i != 3 && i != 4)
            rpnChange(i, 0); 
        } 
        iterator = this.co_midi_nrpn_nrpn.keySet().iterator();
        while (iterator.hasNext()) {
          int i = ((Integer)iterator.next()).intValue();
          nrpnChange(i, 0);
        } 
        rpnChange(0, 256);
        rpnChange(1, 8192);
        rpnChange(2, 8192);
        rpnChange(5, 64);
        this.tuning_bank = 0;
        this.tuning_program = 0;
        this.tuning = new SoftTuning();
      } 
    } 
  }
  
  public void allNotesOff() {
    if (this.current_mixer != null)
      this.current_mixer.allNotesOff(); 
    synchronized (this.control_mutex) {
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).on && (this.voices[b]).channel == this.channel && !(this.voices[b]).releaseTriggered)
          this.voices[b].noteOff(0); 
      } 
    } 
  }
  
  public void allSoundOff() {
    if (this.current_mixer != null)
      this.current_mixer.allSoundOff(); 
    synchronized (this.control_mutex) {
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).on && (this.voices[b]).channel == this.channel)
          this.voices[b].soundOff(); 
      } 
    } 
  }
  
  public boolean localControl(boolean paramBoolean) { return false; }
  
  public void setMono(boolean paramBoolean) {
    if (this.current_mixer != null)
      this.current_mixer.setMono(paramBoolean); 
    synchronized (this.control_mutex) {
      allNotesOff();
      this.mono = paramBoolean;
    } 
  }
  
  public boolean getMono() {
    synchronized (this.control_mutex) {
      return this.mono;
    } 
  }
  
  public void setOmni(boolean paramBoolean) {
    if (this.current_mixer != null)
      this.current_mixer.setOmni(paramBoolean); 
    allNotesOff();
  }
  
  public boolean getOmni() { return false; }
  
  public void setMute(boolean paramBoolean) {
    if (this.current_mixer != null)
      this.current_mixer.setMute(paramBoolean); 
    synchronized (this.control_mutex) {
      this.mute = paramBoolean;
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active && (this.voices[b]).channel == this.channel)
          this.voices[b].setMute(paramBoolean); 
      } 
    } 
  }
  
  public boolean getMute() {
    synchronized (this.control_mutex) {
      return this.mute;
    } 
  }
  
  public void setSolo(boolean paramBoolean) {
    if (this.current_mixer != null)
      this.current_mixer.setSolo(paramBoolean); 
    synchronized (this.control_mutex) {
      this.solo = paramBoolean;
      boolean bool = false;
      for (SoftChannel softChannel : this.synthesizer.channels) {
        if (softChannel.solo) {
          bool = true;
          break;
        } 
      } 
      if (!bool) {
        for (SoftChannel softChannel : this.synthesizer.channels)
          softChannel.setSoloMute(false); 
        return;
      } 
      for (SoftChannel softChannel : this.synthesizer.channels)
        softChannel.setSoloMute(!softChannel.solo); 
    } 
  }
  
  private void setSoloMute(boolean paramBoolean) {
    synchronized (this.control_mutex) {
      if (this.solomute == paramBoolean)
        return; 
      this.solomute = paramBoolean;
      for (byte b = 0; b < this.voices.length; b++) {
        if ((this.voices[b]).active && (this.voices[b]).channel == this.channel)
          this.voices[b].setSoloMute(this.solomute); 
      } 
    } 
  }
  
  public boolean getSolo() {
    synchronized (this.control_mutex) {
      return this.solo;
    } 
  }
  
  static  {
    for (byte b = 0; b < dontResetControls.length; b++)
      dontResetControls[b] = false; 
    dontResetControls[0] = true;
    dontResetControls[32] = true;
    dontResetControls[7] = true;
    dontResetControls[8] = true;
    dontResetControls[10] = true;
    dontResetControls[11] = true;
    dontResetControls[91] = true;
    dontResetControls[92] = true;
    dontResetControls[93] = true;
    dontResetControls[94] = true;
    dontResetControls[95] = true;
    dontResetControls[70] = true;
    dontResetControls[71] = true;
    dontResetControls[72] = true;
    dontResetControls[73] = true;
    dontResetControls[74] = true;
    dontResetControls[75] = true;
    dontResetControls[76] = true;
    dontResetControls[77] = true;
    dontResetControls[78] = true;
    dontResetControls[79] = true;
    dontResetControls[120] = true;
    dontResetControls[121] = true;
    dontResetControls[122] = true;
    dontResetControls[123] = true;
    dontResetControls[124] = true;
    dontResetControls[125] = true;
    dontResetControls[126] = true;
    dontResetControls[127] = true;
    dontResetControls[6] = true;
    dontResetControls[38] = true;
    dontResetControls[96] = true;
    dontResetControls[97] = true;
    dontResetControls[98] = true;
    dontResetControls[99] = true;
    dontResetControls[100] = true;
    dontResetControls[101] = true;
  }
  
  private class MidiControlObject implements SoftControl {
    double[] pitch = SoftChannel.this.co_midi_pitch;
    
    double[] channel_pressure = SoftChannel.this.co_midi_channel_pressure;
    
    double[] poly_pressure = new double[1];
    
    private MidiControlObject() {}
    
    public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : (param1String.equals("pitch") ? this.pitch : (param1String.equals("channel_pressure") ? this.channel_pressure : (param1String.equals("poly_pressure") ? this.poly_pressure : null))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */