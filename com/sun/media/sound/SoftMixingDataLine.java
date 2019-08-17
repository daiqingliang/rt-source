package com.sun.media.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public abstract class SoftMixingDataLine implements DataLine {
  public static final FloatControl.Type CHORUS_SEND = new FloatControl.Type("Chorus Send") {
    
    };
  
  private final Gain gain_control = new Gain(null);
  
  private final Mute mute_control = new Mute(null);
  
  private final Balance balance_control = new Balance(null);
  
  private final Pan pan_control = new Pan(null);
  
  private final ReverbSend reverbsend_control = new ReverbSend(null);
  
  private final ChorusSend chorussend_control = new ChorusSend(null);
  
  private final ApplyReverb apply_reverb = new ApplyReverb(null);
  
  private final Control[] controls;
  
  float leftgain = 1.0F;
  
  float rightgain = 1.0F;
  
  float eff1gain = 0.0F;
  
  float eff2gain = 0.0F;
  
  List<LineListener> listeners = new ArrayList();
  
  final Object control_mutex;
  
  SoftMixingMixer mixer;
  
  DataLine.Info info;
  
  protected abstract void processControlLogic();
  
  protected abstract void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer);
  
  SoftMixingDataLine(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo) {
    this.mixer = paramSoftMixingMixer;
    this.info = paramInfo;
    this.control_mutex = paramSoftMixingMixer.control_mutex;
    this.controls = new Control[] { this.gain_control, this.mute_control, this.balance_control, this.pan_control, this.reverbsend_control, this.chorussend_control, this.apply_reverb };
    calcVolume();
  }
  
  final void calcVolume() {
    synchronized (this.control_mutex) {
      double d = Math.pow(10.0D, this.gain_control.getValue() / 20.0D);
      if (this.mute_control.getValue())
        d = 0.0D; 
      this.leftgain = (float)d;
      this.rightgain = (float)d;
      if (this.mixer.getFormat().getChannels() > 1) {
        double d1 = this.balance_control.getValue();
        if (d1 > 0.0D) {
          this.leftgain = (float)(this.leftgain * (1.0D - d1));
        } else {
          this.rightgain = (float)(this.rightgain * (1.0D + d1));
        } 
      } 
    } 
    this.eff1gain = (float)Math.pow(10.0D, this.reverbsend_control.getValue() / 20.0D);
    this.eff2gain = (float)Math.pow(10.0D, this.chorussend_control.getValue() / 20.0D);
    if (!this.apply_reverb.getValue())
      this.eff1gain = 0.0F; 
  }
  
  final void sendEvent(LineEvent paramLineEvent) {
    if (this.listeners.size() == 0)
      return; 
    LineListener[] arrayOfLineListener = (LineListener[])this.listeners.toArray(new LineListener[this.listeners.size()]);
    for (LineListener lineListener : arrayOfLineListener)
      lineListener.update(paramLineEvent); 
  }
  
  public final void addLineListener(LineListener paramLineListener) {
    synchronized (this.control_mutex) {
      this.listeners.add(paramLineListener);
    } 
  }
  
  public final void removeLineListener(LineListener paramLineListener) {
    synchronized (this.control_mutex) {
      this.listeners.add(paramLineListener);
    } 
  }
  
  public final Line.Info getLineInfo() { return this.info; }
  
  public final Control getControl(Control.Type paramType) {
    if (paramType != null)
      for (byte b = 0; b < this.controls.length; b++) {
        if (this.controls[b].getType() == paramType)
          return this.controls[b]; 
      }  
    throw new IllegalArgumentException("Unsupported control type : " + paramType);
  }
  
  public final Control[] getControls() { return (Control[])Arrays.copyOf(this.controls, this.controls.length); }
  
  public final boolean isControlSupported(Control.Type paramType) {
    if (paramType != null)
      for (byte b = 0; b < this.controls.length; b++) {
        if (this.controls[b].getType() == paramType)
          return true; 
      }  
    return false;
  }
  
  private final class ApplyReverb extends BooleanControl {
    private ApplyReverb() { super(BooleanControl.Type.APPLY_REVERB, false, "True", "False"); }
    
    public void setValue(boolean param1Boolean) {
      super.setValue(param1Boolean);
      SoftMixingDataLine.this.calcVolume();
    }
  }
  
  protected static final class AudioFloatInputStreamResampler extends AudioFloatInputStream {
    private final AudioFloatInputStream ais;
    
    private final AudioFormat targetFormat;
    
    private float[] skipbuffer;
    
    private SoftAbstractResampler resampler;
    
    private final float[] pitch = new float[1];
    
    private final float[] ibuffer2;
    
    private final float[][] ibuffer;
    
    private float ibuffer_index = 0.0F;
    
    private int ibuffer_len = 0;
    
    private int nrofchannels = 0;
    
    private float[][] cbuffer;
    
    private final int buffer_len = 512;
    
    private final int pad;
    
    private final int pad2;
    
    private final float[] ix = new float[1];
    
    private final int[] ox = new int[1];
    
    private float[][] mark_ibuffer = (float[][])null;
    
    private float mark_ibuffer_index = 0.0F;
    
    private int mark_ibuffer_len = 0;
    
    public AudioFloatInputStreamResampler(AudioFloatInputStream param1AudioFloatInputStream, AudioFormat param1AudioFormat) {
      this.ais = param1AudioFloatInputStream;
      AudioFormat audioFormat = param1AudioFloatInputStream.getFormat();
      this.targetFormat = new AudioFormat(audioFormat.getEncoding(), param1AudioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), param1AudioFormat.getSampleRate(), audioFormat.isBigEndian());
      this.nrofchannels = this.targetFormat.getChannels();
      Object object = param1AudioFormat.getProperty("interpolation");
      if (object != null && object instanceof String) {
        String str = (String)object;
        if (str.equalsIgnoreCase("point"))
          this.resampler = new SoftPointResampler(); 
        if (str.equalsIgnoreCase("linear"))
          this.resampler = new SoftLinearResampler2(); 
        if (str.equalsIgnoreCase("linear1"))
          this.resampler = new SoftLinearResampler(); 
        if (str.equalsIgnoreCase("linear2"))
          this.resampler = new SoftLinearResampler2(); 
        if (str.equalsIgnoreCase("cubic"))
          this.resampler = new SoftCubicResampler(); 
        if (str.equalsIgnoreCase("lanczos"))
          this.resampler = new SoftLanczosResampler(); 
        if (str.equalsIgnoreCase("sinc"))
          this.resampler = new SoftSincResampler(); 
      } 
      if (this.resampler == null)
        this.resampler = new SoftLinearResampler2(); 
      this.pitch[0] = audioFormat.getSampleRate() / param1AudioFormat.getSampleRate();
      this.pad = this.resampler.getPadding();
      this.pad2 = this.pad * 2;
      this.ibuffer = new float[this.nrofchannels][512 + this.pad2];
      this.ibuffer2 = new float[this.nrofchannels * 512];
      this.ibuffer_index = (512 + this.pad);
      this.ibuffer_len = 512;
    }
    
    public int available() throws IOException { return 0; }
    
    public void close() { this.ais.close(); }
    
    public AudioFormat getFormat() { return this.targetFormat; }
    
    public long getFrameLength() { return -1L; }
    
    public void mark(int param1Int) {
      this.ais.mark((int)(param1Int * this.pitch[0]));
      this.mark_ibuffer_index = this.ibuffer_index;
      this.mark_ibuffer_len = this.ibuffer_len;
      if (this.mark_ibuffer == null)
        this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length]; 
      for (byte b = 0; b < this.ibuffer.length; b++) {
        float[] arrayOfFloat1 = this.ibuffer[b];
        float[] arrayOfFloat2 = this.mark_ibuffer[b];
        for (byte b1 = 0; b1 < arrayOfFloat2.length; b1++)
          arrayOfFloat2[b1] = arrayOfFloat1[b1]; 
      } 
    }
    
    public boolean markSupported() { return this.ais.markSupported(); }
    
    private void readNextBuffer() {
      if (this.ibuffer_len == -1)
        return; 
      int i;
      for (i = 0; i < this.nrofchannels; i++) {
        float[] arrayOfFloat = this.ibuffer[i];
        int j = this.ibuffer_len + this.pad2;
        int k = this.ibuffer_len;
        for (byte b1 = 0; k < j; b1++) {
          arrayOfFloat[b1] = arrayOfFloat[k];
          k++;
        } 
      } 
      this.ibuffer_index -= this.ibuffer_len;
      this.ibuffer_len = this.ais.read(this.ibuffer2);
      if (this.ibuffer_len >= 0) {
        while (this.ibuffer_len < this.ibuffer2.length) {
          i = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
          if (i == -1)
            break; 
          this.ibuffer_len += i;
        } 
        Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0F);
        this.ibuffer_len /= this.nrofchannels;
      } else {
        Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0F);
      } 
      i = this.ibuffer2.length;
      for (byte b = 0; b < this.nrofchannels; b++) {
        float[] arrayOfFloat = this.ibuffer[b];
        int j = b;
        for (int k = this.pad2; j < i; k++) {
          arrayOfFloat[k] = this.ibuffer2[j];
          j += this.nrofchannels;
        } 
      } 
    }
    
    public int read(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      if (this.cbuffer == null || this.cbuffer[0].length < param1Int2 / this.nrofchannels)
        this.cbuffer = new float[this.nrofchannels][param1Int2 / this.nrofchannels]; 
      if (this.ibuffer_len == -1)
        return -1; 
      if (param1Int2 < 0)
        return 0; 
      int i = param1Int2 / this.nrofchannels;
      int j = 0;
      int k = this.ibuffer_len;
      while (i > 0) {
        if (this.ibuffer_len >= 0) {
          if (this.ibuffer_index >= (this.ibuffer_len + this.pad))
            readNextBuffer(); 
          k = this.ibuffer_len + this.pad;
        } 
        if (this.ibuffer_len < 0) {
          k = this.pad2;
          if (this.ibuffer_index >= k)
            break; 
        } 
        if (this.ibuffer_index < 0.0F)
          break; 
        int m = j;
        for (byte b1 = 0; b1 < this.nrofchannels; b1++) {
          this.ix[0] = this.ibuffer_index;
          this.ox[0] = j;
          float[] arrayOfFloat = this.ibuffer[b1];
          this.resampler.interpolate(arrayOfFloat, this.ix, k, this.pitch, 0.0F, this.cbuffer[b1], this.ox, param1Int2 / this.nrofchannels);
        } 
        this.ibuffer_index = this.ix[0];
        j = this.ox[0];
        i -= j - m;
      } 
      for (byte b = 0; b < this.nrofchannels; b++) {
        byte b1 = 0;
        float[] arrayOfFloat = this.cbuffer[b];
        int m;
        for (m = b; m < param1ArrayOfFloat.length; m += this.nrofchannels)
          param1ArrayOfFloat[m] = arrayOfFloat[b1++]; 
      } 
      return param1Int2 - i * this.nrofchannels;
    }
    
    public void reset() {
      this.ais.reset();
      if (this.mark_ibuffer == null)
        return; 
      this.ibuffer_index = this.mark_ibuffer_index;
      this.ibuffer_len = this.mark_ibuffer_len;
      for (byte b = 0; b < this.ibuffer.length; b++) {
        float[] arrayOfFloat1 = this.mark_ibuffer[b];
        float[] arrayOfFloat2 = this.ibuffer[b];
        for (byte b1 = 0; b1 < arrayOfFloat2.length; b1++)
          arrayOfFloat2[b1] = arrayOfFloat1[b1]; 
      } 
    }
    
    public long skip(long param1Long) throws IOException {
      if (param1Long > 0L)
        return 0L; 
      if (this.skipbuffer == null)
        this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()]; 
      float[] arrayOfFloat = this.skipbuffer;
      long l;
      for (l = param1Long; l > 0L; l -= i) {
        int i = read(arrayOfFloat, 0, (int)Math.min(l, this.skipbuffer.length));
        if (i < 0) {
          if (l == param1Long)
            return i; 
          break;
        } 
      } 
      return param1Long - l;
    }
  }
  
  private final class Balance extends FloatControl {
    private Balance() { super(FloatControl.Type.BALANCE, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right"); }
    
    public void setValue(float param1Float) {
      super.setValue(param1Float);
      SoftMixingDataLine.this.calcVolume();
    }
  }
  
  private final class ChorusSend extends FloatControl {
    private ChorusSend() { super(SoftMixingDataLine.CHORUS_SEND, -80.0F, 6.0206F, 0.625F, -1, -80.0F, "dB", "Minimum", "", "Maximum"); }
    
    public void setValue(float param1Float) {
      super.setValue(param1Float);
      SoftMixingDataLine.this.balance_control.setValue(param1Float);
    }
  }
  
  private final class Gain extends FloatControl {
    private Gain() { super(FloatControl.Type.MASTER_GAIN, -80.0F, 6.0206F, 0.625F, -1, 0.0F, "dB", "Minimum", "", "Maximum"); }
    
    public void setValue(float param1Float) {
      super.setValue(param1Float);
      SoftMixingDataLine.this.calcVolume();
    }
  }
  
  private final class Mute extends BooleanControl {
    private Mute() { super(BooleanControl.Type.MUTE, false, "True", "False"); }
    
    public void setValue(boolean param1Boolean) {
      super.setValue(param1Boolean);
      SoftMixingDataLine.this.calcVolume();
    }
  }
  
  private final class Pan extends FloatControl {
    private Pan() { super(FloatControl.Type.PAN, -1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right"); }
    
    public void setValue(float param1Float) {
      super.setValue(param1Float);
      SoftMixingDataLine.this.balance_control.setValue(param1Float);
    }
    
    public float getValue() { return SoftMixingDataLine.this.balance_control.getValue(); }
  }
  
  private final class ReverbSend extends FloatControl {
    private ReverbSend() { super(FloatControl.Type.REVERB_SEND, -80.0F, 6.0206F, 0.625F, -1, -80.0F, "dB", "Minimum", "", "Maximum"); }
    
    public void setValue(float param1Float) {
      super.setValue(param1Float);
      SoftMixingDataLine.this.balance_control.setValue(param1Float);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMixingDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */