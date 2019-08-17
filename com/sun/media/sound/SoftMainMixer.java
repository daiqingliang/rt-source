package com.sun.media.sound;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioInputStream;

public final class SoftMainMixer {
  public static final int CHANNEL_LEFT = 0;
  
  public static final int CHANNEL_RIGHT = 1;
  
  public static final int CHANNEL_MONO = 2;
  
  public static final int CHANNEL_DELAY_LEFT = 3;
  
  public static final int CHANNEL_DELAY_RIGHT = 4;
  
  public static final int CHANNEL_DELAY_MONO = 5;
  
  public static final int CHANNEL_EFFECT1 = 6;
  
  public static final int CHANNEL_EFFECT2 = 7;
  
  public static final int CHANNEL_DELAY_EFFECT1 = 8;
  
  public static final int CHANNEL_DELAY_EFFECT2 = 9;
  
  public static final int CHANNEL_LEFT_DRY = 10;
  
  public static final int CHANNEL_RIGHT_DRY = 11;
  
  public static final int CHANNEL_SCRATCH1 = 12;
  
  public static final int CHANNEL_SCRATCH2 = 13;
  
  boolean active_sensing_on = false;
  
  private long msec_last_activity = -1L;
  
  private boolean pusher_silent = false;
  
  private int pusher_silent_count = 0;
  
  private long sample_pos = 0L;
  
  boolean readfully = true;
  
  private final Object control_mutex;
  
  private SoftSynthesizer synth;
  
  private float samplerate = 44100.0F;
  
  private int nrofchannels = 2;
  
  private SoftVoice[] voicestatus = null;
  
  private SoftAudioBuffer[] buffers;
  
  private SoftReverb reverb;
  
  private SoftAudioProcessor chorus;
  
  private SoftAudioProcessor agc;
  
  private long msec_buffer_len = 0L;
  
  private int buffer_len = 0;
  
  TreeMap<Long, Object> midimessages = new TreeMap();
  
  private int delay_midievent = 0;
  
  private int max_delay_midievent = 0;
  
  double last_volume_left = 1.0D;
  
  double last_volume_right = 1.0D;
  
  private double[] co_master_balance = new double[1];
  
  private double[] co_master_volume = new double[1];
  
  private double[] co_master_coarse_tuning = new double[1];
  
  private double[] co_master_fine_tuning = new double[1];
  
  private AudioInputStream ais;
  
  private Set<SoftChannelMixerContainer> registeredMixers = null;
  
  private Set<ModelChannelMixer> stoppedMixers = null;
  
  private SoftChannelMixerContainer[] cur_registeredMixers = null;
  
  SoftControl co_master = new SoftControl() {
      double[] balance = SoftMainMixer.this.co_master_balance;
      
      double[] volume = SoftMainMixer.this.co_master_volume;
      
      double[] coarse_tuning = SoftMainMixer.this.co_master_coarse_tuning;
      
      double[] fine_tuning = SoftMainMixer.this.co_master_fine_tuning;
      
      public double[] get(int param1Int, String param1String) { return (param1String == null) ? null : (param1String.equals("balance") ? this.balance : (param1String.equals("volume") ? this.volume : (param1String.equals("coarse_tuning") ? this.coarse_tuning : (param1String.equals("fine_tuning") ? this.fine_tuning : null)))); }
    };
  
  private void processSystemExclusiveMessage(byte[] paramArrayOfByte) {
    synchronized (this.synth.control_mutex) {
      activity();
      if ((paramArrayOfByte[1] & 0xFF) == 126) {
        byte b = paramArrayOfByte[2] & 0xFF;
        if (b == Byte.MAX_VALUE || b == this.synth.getDeviceID()) {
          byte b4;
          SoftChannel[] arrayOfSoftChannel;
          byte b3;
          SoftTuning softTuning;
          byte b2;
          byte b1 = paramArrayOfByte[3] & 0xFF;
          switch (b1) {
            case 8:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 1:
                  softTuning = this.synth.getTuning(new Patch(0, paramArrayOfByte[5] & 0xFF));
                  softTuning.load(paramArrayOfByte);
                  break;
                case 4:
                case 5:
                case 6:
                case 7:
                  softTuning = this.synth.getTuning(new Patch(paramArrayOfByte[5] & 0xFF, paramArrayOfByte[6] & 0xFF));
                  softTuning.load(paramArrayOfByte);
                  break;
                case 8:
                case 9:
                  softTuning = new SoftTuning(paramArrayOfByte);
                  b3 = (paramArrayOfByte[5] & 0xFF) * 16384 + (paramArrayOfByte[6] & 0xFF) * 128 + (paramArrayOfByte[7] & 0xFF);
                  arrayOfSoftChannel = this.synth.channels;
                  for (b4 = 0; b4 < arrayOfSoftChannel.length; b4++) {
                    if ((b3 & true << b4) != 0)
                      (arrayOfSoftChannel[b4]).tuning = softTuning; 
                  } 
                  break;
              } 
              break;
            case 9:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 1:
                  this.synth.setGeneralMidiMode(1);
                  reset();
                  break;
                case 2:
                  this.synth.setGeneralMidiMode(0);
                  reset();
                  break;
                case 3:
                  this.synth.setGeneralMidiMode(2);
                  reset();
                  break;
              } 
              break;
            case 10:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 1:
                  if (this.synth.getGeneralMidiMode() == 0)
                    this.synth.setGeneralMidiMode(1); 
                  this.synth.voice_allocation_mode = 1;
                  reset();
                  break;
                case 2:
                  this.synth.setGeneralMidiMode(0);
                  this.synth.voice_allocation_mode = 0;
                  reset();
                  break;
                case 3:
                  this.synth.voice_allocation_mode = 0;
                  break;
                case 4:
                  this.synth.voice_allocation_mode = 1;
                  break;
              } 
              break;
          } 
        } 
      } 
      if ((paramArrayOfByte[1] & 0xFF) == Byte.MAX_VALUE) {
        byte b = paramArrayOfByte[2] & 0xFF;
        if (b == Byte.MAX_VALUE || b == this.synth.getDeviceID()) {
          byte b13;
          long[] arrayOfLong2;
          long[] arrayOfLong1;
          int j;
          byte b12;
          int[] arrayOfInt3;
          byte b11;
          SoftChannel softChannel2;
          byte b9;
          SoftVoice[] arrayOfSoftVoice2;
          byte b10;
          byte b8;
          SoftChannel[] arrayOfSoftChannel;
          byte b7;
          SoftChannel softChannel1;
          SoftVoice[] arrayOfSoftVoice1;
          byte b5;
          byte b6;
          int i;
          int[] arrayOfInt2;
          SoftTuning softTuning;
          byte b3;
          byte b4;
          int[] arrayOfInt1;
          byte b2;
          byte b1 = paramArrayOfByte[3] & 0xFF;
          switch (b1) {
            case 4:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 1:
                case 2:
                case 3:
                case 4:
                  b4 = (paramArrayOfByte[5] & 0x7F) + (paramArrayOfByte[6] & 0x7F) * 128;
                  if (b2 == 1) {
                    setVolume(b4);
                    break;
                  } 
                  if (b2 == 2) {
                    setBalance(b4);
                    break;
                  } 
                  if (b2 == 3) {
                    setFineTuning(b4);
                    break;
                  } 
                  if (b2 == 4)
                    setCoarseTuning(b4); 
                  break;
                case 5:
                  i = 5;
                  b8 = paramArrayOfByte[i++] & 0xFF;
                  b10 = paramArrayOfByte[i++] & 0xFF;
                  b11 = paramArrayOfByte[i++] & 0xFF;
                  arrayOfInt3 = new int[b8];
                  for (j = 0; j < b8; j++) {
                    byte b14 = paramArrayOfByte[i++] & 0xFF;
                    byte b15 = paramArrayOfByte[i++] & 0xFF;
                    arrayOfInt3[j] = b14 * 128 + b15;
                  } 
                  j = (paramArrayOfByte.length - 1 - i) / (b10 + b11);
                  arrayOfLong1 = new long[j];
                  arrayOfLong2 = new long[j];
                  for (b13 = 0; b13 < j; b13++) {
                    arrayOfLong2[b13] = 0L;
                    byte b14;
                    for (b14 = 0; b14 < b10; b14++)
                      arrayOfLong1[b13] = arrayOfLong1[b13] * 128L + (paramArrayOfByte[i++] & 0xFF); 
                    for (b14 = 0; b14 < b11; b14++)
                      arrayOfLong2[b13] = arrayOfLong2[b13] * 128L + (paramArrayOfByte[i++] & 0xFF); 
                  } 
                  globalParameterControlChange(arrayOfInt3, arrayOfLong1, arrayOfLong2);
                  break;
              } 
              break;
            case 8:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 2:
                  softTuning = this.synth.getTuning(new Patch(0, paramArrayOfByte[5] & 0xFF));
                  softTuning.load(paramArrayOfByte);
                  arrayOfSoftVoice1 = this.synth.getVoices();
                  for (b8 = 0; b8 < arrayOfSoftVoice1.length; b8++) {
                    if ((arrayOfSoftVoice1[b8]).active && (arrayOfSoftVoice1[b8]).tuning == softTuning)
                      arrayOfSoftVoice1[b8].updateTuning(softTuning); 
                  } 
                  break;
                case 7:
                  softTuning = this.synth.getTuning(new Patch(paramArrayOfByte[5] & 0xFF, paramArrayOfByte[6] & 0xFF));
                  softTuning.load(paramArrayOfByte);
                  arrayOfSoftVoice1 = this.synth.getVoices();
                  for (b8 = 0; b8 < arrayOfSoftVoice1.length; b8++) {
                    if ((arrayOfSoftVoice1[b8]).active && (arrayOfSoftVoice1[b8]).tuning == softTuning)
                      arrayOfSoftVoice1[b8].updateTuning(softTuning); 
                  } 
                  break;
                case 8:
                case 9:
                  softTuning = new SoftTuning(paramArrayOfByte);
                  b6 = (paramArrayOfByte[5] & 0xFF) * 16384 + (paramArrayOfByte[6] & 0xFF) * 128 + (paramArrayOfByte[7] & 0xFF);
                  arrayOfSoftChannel = this.synth.channels;
                  for (b10 = 0; b10 < arrayOfSoftChannel.length; b10++) {
                    if ((b6 & 1 << b10) != 0)
                      (arrayOfSoftChannel[b10]).tuning = softTuning; 
                  } 
                  arrayOfSoftVoice2 = this.synth.getVoices();
                  for (b11 = 0; b11 < arrayOfSoftVoice2.length; b11++) {
                    if ((arrayOfSoftVoice2[b11]).active && (b6 & 1 << (arrayOfSoftVoice2[b11]).channel) != 0)
                      arrayOfSoftVoice2[b11].updateTuning(softTuning); 
                  } 
                  break;
              } 
              break;
            case 9:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 1:
                  arrayOfInt1 = new int[(paramArrayOfByte.length - 7) / 2];
                  arrayOfInt2 = new int[(paramArrayOfByte.length - 7) / 2];
                  b7 = 0;
                  for (b9 = 6; b9 < paramArrayOfByte.length - 1; b9 += 2) {
                    arrayOfInt1[b7] = paramArrayOfByte[b9] & 0xFF;
                    arrayOfInt2[b7] = paramArrayOfByte[b9 + 1] & 0xFF;
                    b7++;
                  } 
                  b9 = paramArrayOfByte[5] & 0xFF;
                  softChannel2 = this.synth.channels[b9];
                  softChannel2.mapChannelPressureToDestination(arrayOfInt1, arrayOfInt2);
                  break;
                case 2:
                  arrayOfInt1 = new int[(paramArrayOfByte.length - 7) / 2];
                  arrayOfInt2 = new int[(paramArrayOfByte.length - 7) / 2];
                  b7 = 0;
                  for (b9 = 6; b9 < paramArrayOfByte.length - 1; b9 += 2) {
                    arrayOfInt1[b7] = paramArrayOfByte[b9] & 0xFF;
                    arrayOfInt2[b7] = paramArrayOfByte[b9 + 1] & 0xFF;
                    b7++;
                  } 
                  b9 = paramArrayOfByte[5] & 0xFF;
                  softChannel2 = this.synth.channels[b9];
                  softChannel2.mapPolyPressureToDestination(arrayOfInt1, arrayOfInt2);
                  break;
                case 3:
                  arrayOfInt1 = new int[(paramArrayOfByte.length - 7) / 2];
                  arrayOfInt2 = new int[(paramArrayOfByte.length - 7) / 2];
                  b7 = 0;
                  for (b9 = 7; b9 < paramArrayOfByte.length - 1; b9 += 2) {
                    arrayOfInt1[b7] = paramArrayOfByte[b9] & 0xFF;
                    arrayOfInt2[b7] = paramArrayOfByte[b9 + 1] & 0xFF;
                    b7++;
                  } 
                  b9 = paramArrayOfByte[5] & 0xFF;
                  softChannel2 = this.synth.channels[b9];
                  b12 = paramArrayOfByte[6] & 0xFF;
                  softChannel2.mapControlToDestination(b12, arrayOfInt1, arrayOfInt2);
                  break;
              } 
              break;
            case 10:
              b2 = paramArrayOfByte[4] & 0xFF;
              switch (b2) {
                case 1:
                  b3 = paramArrayOfByte[5] & 0xFF;
                  b5 = paramArrayOfByte[6] & 0xFF;
                  softChannel1 = this.synth.channels[b3];
                  for (b9 = 7; b9 < paramArrayOfByte.length - 1; b9 += 2) {
                    byte b14 = paramArrayOfByte[b9] & 0xFF;
                    b12 = paramArrayOfByte[b9 + 1] & 0xFF;
                    softChannel1.controlChangePerNote(b5, b14, b12);
                  } 
                  break;
              } 
              break;
          } 
        } 
      } 
    } 
  }
  
  private void processMessages(long paramLong) {
    Iterator iterator = this.midimessages.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry)iterator.next();
      if (((Long)entry.getKey()).longValue() >= paramLong + this.msec_buffer_len)
        return; 
      long l = ((Long)entry.getKey()).longValue() - paramLong;
      this.delay_midievent = (int)(l * this.samplerate / 1000000.0D + 0.5D);
      if (this.delay_midievent > this.max_delay_midievent)
        this.delay_midievent = this.max_delay_midievent; 
      if (this.delay_midievent < 0)
        this.delay_midievent = 0; 
      processMessage(entry.getValue());
      iterator.remove();
    } 
    this.delay_midievent = 0;
  }
  
  void processAudioBuffers() {
    SoftChannelMixerContainer[] arrayOfSoftChannelMixerContainer;
    double d2;
    if (this.synth.weakstream != null && this.synth.weakstream.silent_samples != 0L) {
      this.sample_pos += this.synth.weakstream.silent_samples;
      this.synth.weakstream.silent_samples = 0L;
    } 
    double d1;
    for (d1 = false; d1 < this.buffers.length; d1++) {
      if (d1 != 3 && d1 != 4 && d1 != 5 && d1 != 8 && d1 != 9)
        this.buffers[d1].clear(); 
    } 
    if (!this.buffers[3].isSilent())
      this.buffers[0].swap(this.buffers[3]); 
    if (!this.buffers[4].isSilent())
      this.buffers[1].swap(this.buffers[4]); 
    if (!this.buffers[5].isSilent())
      this.buffers[2].swap(this.buffers[5]); 
    if (!this.buffers[8].isSilent())
      this.buffers[6].swap(this.buffers[8]); 
    if (!this.buffers[9].isSilent())
      this.buffers[7].swap(this.buffers[9]); 
    synchronized (this.control_mutex) {
      long l = (long)(this.sample_pos * 1000000.0D / this.samplerate);
      processMessages(l);
      if (this.active_sensing_on && l - this.msec_last_activity > 1000000L) {
        this.active_sensing_on = false;
        for (SoftChannel softChannel : this.synth.channels)
          softChannel.allSoundOff(); 
      } 
      for (byte b = 0; b < this.voicestatus.length; b++) {
        if ((this.voicestatus[b]).active)
          this.voicestatus[b].processControlLogic(); 
      } 
      this.sample_pos += this.buffer_len;
      double d3 = this.co_master_volume[0];
      d1 = d3;
      d2 = d3;
      double d4 = this.co_master_balance[0];
      if (d4 > 0.5D) {
        d1 *= (1.0D - d4) * 2.0D;
      } else {
        d2 *= d4 * 2.0D;
      } 
      this.chorus.processControlLogic();
      this.reverb.processControlLogic();
      this.agc.processControlLogic();
      if (this.cur_registeredMixers == null && this.registeredMixers != null) {
        this.cur_registeredMixers = new SoftChannelMixerContainer[this.registeredMixers.size()];
        this.registeredMixers.toArray(this.cur_registeredMixers);
      } 
      arrayOfSoftChannelMixerContainer = this.cur_registeredMixers;
      if (arrayOfSoftChannelMixerContainer != null && arrayOfSoftChannelMixerContainer.length == 0)
        arrayOfSoftChannelMixerContainer = null; 
    } 
    if (arrayOfSoftChannelMixerContainer != null) {
      SoftAudioBuffer softAudioBuffer1 = this.buffers[0];
      SoftAudioBuffer softAudioBuffer2 = this.buffers[1];
      SoftAudioBuffer softAudioBuffer3 = this.buffers[2];
      SoftAudioBuffer softAudioBuffer4 = this.buffers[3];
      SoftAudioBuffer softAudioBuffer5 = this.buffers[4];
      SoftAudioBuffer softAudioBuffer6 = this.buffers[5];
      int j = this.buffers[0].getSize();
      float[][] arrayOfFloat1 = new float[this.nrofchannels][];
      float[][] arrayOfFloat2 = new float[this.nrofchannels][];
      arrayOfFloat2[0] = softAudioBuffer1.array();
      if (this.nrofchannels != 1)
        arrayOfFloat2[1] = softAudioBuffer2.array(); 
      for (SoftChannelMixerContainer softChannelMixerContainer : arrayOfSoftChannelMixerContainer) {
        this.buffers[0] = softChannelMixerContainer.buffers[0];
        this.buffers[1] = softChannelMixerContainer.buffers[1];
        this.buffers[2] = softChannelMixerContainer.buffers[2];
        this.buffers[3] = softChannelMixerContainer.buffers[3];
        this.buffers[4] = softChannelMixerContainer.buffers[4];
        this.buffers[5] = softChannelMixerContainer.buffers[5];
        this.buffers[0].clear();
        this.buffers[1].clear();
        this.buffers[2].clear();
        if (!this.buffers[3].isSilent())
          this.buffers[0].swap(this.buffers[3]); 
        if (!this.buffers[4].isSilent())
          this.buffers[1].swap(this.buffers[4]); 
        if (!this.buffers[5].isSilent())
          this.buffers[2].swap(this.buffers[5]); 
        arrayOfFloat1[0] = this.buffers[0].array();
        if (this.nrofchannels != 1)
          arrayOfFloat1[1] = this.buffers[1].array(); 
        boolean bool = false;
        byte b;
        for (b = 0; b < this.voicestatus.length; b++) {
          if ((this.voicestatus[b]).active && (this.voicestatus[b]).channelmixer == softChannelMixerContainer.mixer) {
            this.voicestatus[b].processAudioLogic(this.buffers);
            bool = true;
          } 
        } 
        if (!this.buffers[2].isSilent()) {
          float[] arrayOfFloat3 = this.buffers[2].array();
          float[] arrayOfFloat4 = this.buffers[0].array();
          if (this.nrofchannels != 1) {
            float[] arrayOfFloat = this.buffers[1].array();
            for (byte b1 = 0; b1 < j; b1++) {
              float f = arrayOfFloat3[b1];
              arrayOfFloat4[b1] = arrayOfFloat4[b1] + f;
              arrayOfFloat[b1] = arrayOfFloat[b1] + f;
            } 
          } else {
            for (byte b1 = 0; b1 < j; b1++)
              arrayOfFloat4[b1] = arrayOfFloat4[b1] + arrayOfFloat3[b1]; 
          } 
        } 
        if (!softChannelMixerContainer.mixer.process(arrayOfFloat1, 0, j))
          synchronized (this.control_mutex) {
            this.registeredMixers.remove(softChannelMixerContainer);
            this.cur_registeredMixers = null;
          }  
        for (b = 0; b < arrayOfFloat1.length; b++) {
          float[] arrayOfFloat3 = arrayOfFloat1[b];
          float[] arrayOfFloat4 = arrayOfFloat2[b];
          for (byte b1 = 0; b1 < j; b1++)
            arrayOfFloat4[b1] = arrayOfFloat4[b1] + arrayOfFloat3[b1]; 
        } 
        if (!bool)
          synchronized (this.control_mutex) {
            if (this.stoppedMixers != null && this.stoppedMixers.contains(softChannelMixerContainer)) {
              this.stoppedMixers.remove(softChannelMixerContainer);
              softChannelMixerContainer.mixer.stop();
            } 
          }  
      } 
      this.buffers[0] = softAudioBuffer1;
      this.buffers[1] = softAudioBuffer2;
      this.buffers[2] = softAudioBuffer3;
      this.buffers[3] = softAudioBuffer4;
      this.buffers[4] = softAudioBuffer5;
      this.buffers[5] = softAudioBuffer6;
    } 
    int i;
    for (i = 0; i < this.voicestatus.length; i++) {
      if ((this.voicestatus[i]).active && (this.voicestatus[i]).channelmixer == null)
        this.voicestatus[i].processAudioLogic(this.buffers); 
    } 
    if (!this.buffers[2].isSilent()) {
      float[] arrayOfFloat1 = this.buffers[2].array();
      float[] arrayOfFloat2 = this.buffers[0].array();
      int j = this.buffers[0].getSize();
      if (this.nrofchannels != 1) {
        float[] arrayOfFloat = this.buffers[1].array();
        for (byte b = 0; b < j; b++) {
          float f = arrayOfFloat1[b];
          arrayOfFloat2[b] = arrayOfFloat2[b] + f;
          arrayOfFloat[b] = arrayOfFloat[b] + f;
        } 
      } else {
        for (byte b = 0; b < j; b++)
          arrayOfFloat2[b] = arrayOfFloat2[b] + arrayOfFloat1[b]; 
      } 
    } 
    if (this.synth.chorus_on)
      this.chorus.processAudio(); 
    if (this.synth.reverb_on)
      this.reverb.processAudio(); 
    if (this.nrofchannels == 1)
      d1 = (d1 + d2) / 2.0D; 
    if (this.last_volume_left != d1 || this.last_volume_right != d2) {
      float[] arrayOfFloat1 = this.buffers[0].array();
      float[] arrayOfFloat2 = this.buffers[1].array();
      int j = this.buffers[0].getSize();
      float f1 = (float)(this.last_volume_left * this.last_volume_left);
      float f2 = (float)((d1 * d1 - f1) / j);
      byte b;
      for (b = 0; b < j; b++) {
        f1 += f2;
        arrayOfFloat1[b] = arrayOfFloat1[b] * f1;
      } 
      if (this.nrofchannels != 1) {
        f1 = (float)(this.last_volume_right * this.last_volume_right);
        f2 = (float)((d2 * d2 - f1) / j);
        for (b = 0; b < j; b++) {
          f1 += f2;
          arrayOfFloat2[b] = (float)(arrayOfFloat2[b] * d2);
        } 
      } 
      this.last_volume_left = d1;
      this.last_volume_right = d2;
    } else if (d1 != 1.0D || d2 != 1.0D) {
      float[] arrayOfFloat1 = this.buffers[0].array();
      float[] arrayOfFloat2 = this.buffers[1].array();
      int j = this.buffers[0].getSize();
      float f = (float)(d1 * d1);
      byte b;
      for (b = 0; b < j; b++)
        arrayOfFloat1[b] = arrayOfFloat1[b] * f; 
      if (this.nrofchannels != 1) {
        f = (float)(d2 * d2);
        for (b = 0; b < j; b++)
          arrayOfFloat2[b] = arrayOfFloat2[b] * f; 
      } 
    } 
    if (this.buffers[0].isSilent() && this.buffers[1].isSilent()) {
      synchronized (this.control_mutex) {
        i = this.midimessages.size();
      } 
      if (i == 0) {
        this.pusher_silent_count++;
        if (this.pusher_silent_count > 5) {
          this.pusher_silent_count = 0;
          synchronized (this.control_mutex) {
            this.pusher_silent = true;
            if (this.synth.weakstream != null)
              this.synth.weakstream.setInputStream(null); 
          } 
        } 
      } 
    } else {
      this.pusher_silent_count = 0;
    } 
    if (this.synth.agc_on)
      this.agc.processAudio(); 
  }
  
  public void activity() {
    long l = 0L;
    if (this.pusher_silent) {
      this.pusher_silent = false;
      if (this.synth.weakstream != null) {
        this.synth.weakstream.setInputStream(this.ais);
        l = this.synth.weakstream.silent_samples;
      } 
    } 
    this.msec_last_activity = (long)((this.sample_pos + l) * 1000000.0D / this.samplerate);
  }
  
  public void stopMixer(ModelChannelMixer paramModelChannelMixer) {
    if (this.stoppedMixers == null)
      this.stoppedMixers = new HashSet(); 
    this.stoppedMixers.add(paramModelChannelMixer);
  }
  
  public void registerMixer(ModelChannelMixer paramModelChannelMixer) {
    if (this.registeredMixers == null)
      this.registeredMixers = new HashSet(); 
    SoftChannelMixerContainer softChannelMixerContainer = new SoftChannelMixerContainer(null);
    softChannelMixerContainer.buffers = new SoftAudioBuffer[6];
    for (byte b = 0; b < softChannelMixerContainer.buffers.length; b++)
      softChannelMixerContainer.buffers[b] = new SoftAudioBuffer(this.buffer_len, this.synth.getFormat()); 
    softChannelMixerContainer.mixer = paramModelChannelMixer;
    this.registeredMixers.add(softChannelMixerContainer);
    this.cur_registeredMixers = null;
  }
  
  public SoftMainMixer(SoftSynthesizer paramSoftSynthesizer) {
    this.synth = paramSoftSynthesizer;
    this.sample_pos = 0L;
    this.co_master_balance[0] = 0.5D;
    this.co_master_volume[0] = 1.0D;
    this.co_master_coarse_tuning[0] = 0.5D;
    this.co_master_fine_tuning[0] = 0.5D;
    this.msec_buffer_len = (long)(1000000.0D / paramSoftSynthesizer.getControlRate());
    this.samplerate = paramSoftSynthesizer.getFormat().getSampleRate();
    this.nrofchannels = paramSoftSynthesizer.getFormat().getChannels();
    int i = (int)(paramSoftSynthesizer.getFormat().getSampleRate() / paramSoftSynthesizer.getControlRate());
    this.buffer_len = i;
    this.max_delay_midievent = i;
    this.control_mutex = paramSoftSynthesizer.control_mutex;
    this.buffers = new SoftAudioBuffer[14];
    for (byte b = 0; b < this.buffers.length; b++)
      this.buffers[b] = new SoftAudioBuffer(i, paramSoftSynthesizer.getFormat()); 
    this.voicestatus = paramSoftSynthesizer.getVoices();
    this.reverb = new SoftReverb();
    this.chorus = new SoftChorus();
    this.agc = new SoftLimiter();
    float f1 = paramSoftSynthesizer.getFormat().getSampleRate();
    float f2 = paramSoftSynthesizer.getControlRate();
    this.reverb.init(f1, f2);
    this.chorus.init(f1, f2);
    this.agc.init(f1, f2);
    this.reverb.setLightMode(paramSoftSynthesizer.reverb_light);
    this.reverb.setMixMode(true);
    this.chorus.setMixMode(true);
    this.agc.setMixMode(false);
    this.chorus.setInput(0, this.buffers[7]);
    this.chorus.setOutput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.chorus.setOutput(1, this.buffers[1]); 
    this.chorus.setOutput(2, this.buffers[6]);
    this.reverb.setInput(0, this.buffers[6]);
    this.reverb.setOutput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.reverb.setOutput(1, this.buffers[1]); 
    this.agc.setInput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.agc.setInput(1, this.buffers[1]); 
    this.agc.setOutput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.agc.setOutput(1, this.buffers[1]); 
    InputStream inputStream = new InputStream() {
        private final SoftAudioBuffer[] buffers = SoftMainMixer.this.buffers;
        
        private final int nrofchannels = SoftMainMixer.this.synth.getFormat().getChannels();
        
        private final int buffersize = this.buffers[0].getSize();
        
        private final byte[] bbuffer = new byte[this.buffersize * SoftMainMixer.this.synth.getFormat().getSampleSizeInBits() / 8 * this.nrofchannels];
        
        private int bbuffer_pos = 0;
        
        private final byte[] single = new byte[1];
        
        public void fillBuffer() {
          SoftMainMixer.this.processAudioBuffers();
          for (byte b = 0; b < this.nrofchannels; b++)
            this.buffers[b].get(this.bbuffer, b); 
          this.bbuffer_pos = 0;
        }
        
        public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
          int i = this.bbuffer.length;
          int j = param1Int1 + param1Int2;
          int k = param1Int1;
          byte[] arrayOfByte = this.bbuffer;
          while (param1Int1 < j) {
            if (available() == 0) {
              fillBuffer();
              continue;
            } 
            int m = this.bbuffer_pos;
            while (param1Int1 < j && m < i)
              param1ArrayOfByte[param1Int1++] = arrayOfByte[m++]; 
            this.bbuffer_pos = m;
            if (!SoftMainMixer.this.readfully)
              return param1Int1 - k; 
          } 
          return param1Int2;
        }
        
        public int read() {
          int i = read(this.single);
          return (i == -1) ? -1 : (this.single[0] & 0xFF);
        }
        
        public int available() { return this.bbuffer.length - this.bbuffer_pos; }
        
        public void close() { SoftMainMixer.this.synth.close(); }
      };
    this.ais = new AudioInputStream(inputStream, paramSoftSynthesizer.getFormat(), -1L);
  }
  
  public AudioInputStream getInputStream() { return this.ais; }
  
  public void reset() {
    SoftChannel[] arrayOfSoftChannel = this.synth.channels;
    for (byte b = 0; b < arrayOfSoftChannel.length; b++) {
      arrayOfSoftChannel[b].allSoundOff();
      arrayOfSoftChannel[b].resetAllControllers(true);
      if (this.synth.getGeneralMidiMode() == 2) {
        if (b == 9) {
          arrayOfSoftChannel[b].programChange(0, 15360);
        } else {
          arrayOfSoftChannel[b].programChange(0, 15488);
        } 
      } else {
        arrayOfSoftChannel[b].programChange(0, 0);
      } 
    } 
    setVolume(16383);
    setBalance(8192);
    setCoarseTuning(8192);
    setFineTuning(8192);
    globalParameterControlChange(new int[] { 129 }, new long[] { 0L }, new long[] { 4L });
    globalParameterControlChange(new int[] { 130 }, new long[] { 0L }, new long[] { 2L });
  }
  
  public void setVolume(int paramInt) {
    synchronized (this.control_mutex) {
      this.co_master_volume[0] = paramInt / 16384.0D;
    } 
  }
  
  public void setBalance(int paramInt) {
    synchronized (this.control_mutex) {
      this.co_master_balance[0] = paramInt / 16384.0D;
    } 
  }
  
  public void setFineTuning(int paramInt) {
    synchronized (this.control_mutex) {
      this.co_master_fine_tuning[0] = paramInt / 16384.0D;
    } 
  }
  
  public void setCoarseTuning(int paramInt) {
    synchronized (this.control_mutex) {
      this.co_master_coarse_tuning[0] = paramInt / 16384.0D;
    } 
  }
  
  public int getVolume() {
    synchronized (this.control_mutex) {
      return (int)(this.co_master_volume[0] * 16384.0D);
    } 
  }
  
  public int getBalance() {
    synchronized (this.control_mutex) {
      return (int)(this.co_master_balance[0] * 16384.0D);
    } 
  }
  
  public int getFineTuning() {
    synchronized (this.control_mutex) {
      return (int)(this.co_master_fine_tuning[0] * 16384.0D);
    } 
  }
  
  public int getCoarseTuning() {
    synchronized (this.control_mutex) {
      return (int)(this.co_master_coarse_tuning[0] * 16384.0D);
    } 
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2) {
    if (paramArrayOfInt.length == 0)
      return; 
    synchronized (this.control_mutex) {
      if (paramArrayOfInt[0] == 129)
        for (byte b = 0; b < paramArrayOfLong2.length; b++)
          this.reverb.globalParameterControlChange(paramArrayOfInt, paramArrayOfLong1[b], paramArrayOfLong2[b]);  
      if (paramArrayOfInt[0] == 130)
        for (byte b = 0; b < paramArrayOfLong2.length; b++)
          this.chorus.globalParameterControlChange(paramArrayOfInt, paramArrayOfLong1[b], paramArrayOfLong2[b]);  
    } 
  }
  
  public void processMessage(Object paramObject) {
    if (paramObject instanceof byte[])
      processMessage((byte[])paramObject); 
    if (paramObject instanceof MidiMessage)
      processMessage((MidiMessage)paramObject); 
  }
  
  public void processMessage(MidiMessage paramMidiMessage) {
    if (paramMidiMessage instanceof ShortMessage) {
      ShortMessage shortMessage = (ShortMessage)paramMidiMessage;
      processMessage(shortMessage.getChannel(), shortMessage.getCommand(), shortMessage.getData1(), shortMessage.getData2());
      return;
    } 
    processMessage(paramMidiMessage.getMessage());
  }
  
  public void processMessage(byte[] paramArrayOfByte) {
    byte b4;
    byte b3;
    byte b1 = 0;
    if (paramArrayOfByte.length > 0)
      b1 = paramArrayOfByte[0] & 0xFF; 
    if (b1 == 240) {
      processSystemExclusiveMessage(paramArrayOfByte);
      return;
    } 
    short s = b1 & 0xF0;
    byte b2 = b1 & 0xF;
    if (paramArrayOfByte.length > 1) {
      b3 = paramArrayOfByte[1] & 0xFF;
    } else {
      b3 = 0;
    } 
    if (paramArrayOfByte.length > 2) {
      b4 = paramArrayOfByte[2] & 0xFF;
    } else {
      b4 = 0;
    } 
    processMessage(b2, s, b3, b4);
  }
  
  public void processMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    synchronized (this.synth.control_mutex) {
      activity();
    } 
    if (paramInt2 == 240) {
      int i = paramInt2 | paramInt1;
      switch (i) {
        case 254:
          synchronized (this.synth.control_mutex) {
            this.active_sensing_on = true;
          } 
          break;
      } 
      return;
    } 
    SoftChannel[] arrayOfSoftChannel = this.synth.channels;
    if (paramInt1 >= arrayOfSoftChannel.length)
      return; 
    SoftChannel softChannel = arrayOfSoftChannel[paramInt1];
    switch (paramInt2) {
      case 144:
        if (this.delay_midievent != 0) {
          softChannel.noteOn(paramInt3, paramInt4, this.delay_midievent);
          break;
        } 
        softChannel.noteOn(paramInt3, paramInt4);
        break;
      case 128:
        softChannel.noteOff(paramInt3, paramInt4);
        break;
      case 160:
        softChannel.setPolyPressure(paramInt3, paramInt4);
        break;
      case 176:
        softChannel.controlChange(paramInt3, paramInt4);
        break;
      case 192:
        softChannel.programChange(paramInt3);
        break;
      case 208:
        softChannel.setChannelPressure(paramInt3);
        break;
      case 224:
        softChannel.setPitchBend(paramInt3 + paramInt4 * 128);
        break;
    } 
  }
  
  public long getMicrosecondPosition() { return (this.pusher_silent && this.synth.weakstream != null) ? (long)((this.sample_pos + this.synth.weakstream.silent_samples) * 1000000.0D / this.samplerate) : (long)(this.sample_pos * 1000000.0D / this.samplerate); }
  
  public void close() {}
  
  private class SoftChannelMixerContainer {
    ModelChannelMixer mixer;
    
    SoftAudioBuffer[] buffers;
    
    private SoftChannelMixerContainer() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMainMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */