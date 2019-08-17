package com.sun.media.sound;

import java.util.Arrays;

public final class SoftChorus implements SoftAudioProcessor {
  private boolean mix = true;
  
  private SoftAudioBuffer inputA;
  
  private SoftAudioBuffer left;
  
  private SoftAudioBuffer right;
  
  private SoftAudioBuffer reverb;
  
  private LFODelay vdelay1L;
  
  private LFODelay vdelay1R;
  
  private float rgain = 0.0F;
  
  private boolean dirty = true;
  
  private double dirty_vdelay1L_rate;
  
  private double dirty_vdelay1R_rate;
  
  private double dirty_vdelay1L_depth;
  
  private double dirty_vdelay1R_depth;
  
  private float dirty_vdelay1L_feedback;
  
  private float dirty_vdelay1R_feedback;
  
  private float dirty_vdelay1L_reverbsendgain;
  
  private float dirty_vdelay1R_reverbsendgain;
  
  private float controlrate;
  
  double silentcounter = 1000.0D;
  
  public void init(float paramFloat1, float paramFloat2) {
    this.controlrate = paramFloat2;
    this.vdelay1L = new LFODelay(paramFloat1, paramFloat2);
    this.vdelay1R = new LFODelay(paramFloat1, paramFloat2);
    this.vdelay1L.setGain(1.0F);
    this.vdelay1R.setGain(1.0F);
    this.vdelay1L.setPhase(1.5707963267948966D);
    this.vdelay1R.setPhase(0.0D);
    globalParameterControlChange(new int[] { 130 }, 0L, 2L);
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2) {
    if (paramArrayOfInt.length == 1 && paramArrayOfInt[0] == 130) {
      if (paramLong1 == 0L) {
        switch ((int)paramLong2) {
          case 0:
            globalParameterControlChange(paramArrayOfInt, 3L, 0L);
            globalParameterControlChange(paramArrayOfInt, 1L, 3L);
            globalParameterControlChange(paramArrayOfInt, 2L, 5L);
            globalParameterControlChange(paramArrayOfInt, 4L, 0L);
            break;
          case 1:
            globalParameterControlChange(paramArrayOfInt, 3L, 5L);
            globalParameterControlChange(paramArrayOfInt, 1L, 9L);
            globalParameterControlChange(paramArrayOfInt, 2L, 19L);
            globalParameterControlChange(paramArrayOfInt, 4L, 0L);
            break;
          case 2:
            globalParameterControlChange(paramArrayOfInt, 3L, 8L);
            globalParameterControlChange(paramArrayOfInt, 1L, 3L);
            globalParameterControlChange(paramArrayOfInt, 2L, 19L);
            globalParameterControlChange(paramArrayOfInt, 4L, 0L);
            break;
          case 3:
            globalParameterControlChange(paramArrayOfInt, 3L, 16L);
            globalParameterControlChange(paramArrayOfInt, 1L, 9L);
            globalParameterControlChange(paramArrayOfInt, 2L, 16L);
            globalParameterControlChange(paramArrayOfInt, 4L, 0L);
            break;
          case 4:
            globalParameterControlChange(paramArrayOfInt, 3L, 64L);
            globalParameterControlChange(paramArrayOfInt, 1L, 2L);
            globalParameterControlChange(paramArrayOfInt, 2L, 24L);
            globalParameterControlChange(paramArrayOfInt, 4L, 0L);
            break;
          case 5:
            globalParameterControlChange(paramArrayOfInt, 3L, 112L);
            globalParameterControlChange(paramArrayOfInt, 1L, 1L);
            globalParameterControlChange(paramArrayOfInt, 2L, 5L);
            globalParameterControlChange(paramArrayOfInt, 4L, 0L);
            break;
        } 
      } else if (paramLong1 == 1L) {
        this.dirty_vdelay1L_rate = paramLong2 * 0.122D;
        this.dirty_vdelay1R_rate = paramLong2 * 0.122D;
        this.dirty = true;
      } else if (paramLong1 == 2L) {
        this.dirty_vdelay1L_depth = (paramLong2 + 1L) / 3200.0D;
        this.dirty_vdelay1R_depth = (paramLong2 + 1L) / 3200.0D;
        this.dirty = true;
      } else if (paramLong1 == 3L) {
        this.dirty_vdelay1L_feedback = (float)paramLong2 * 0.00763F;
        this.dirty_vdelay1R_feedback = (float)paramLong2 * 0.00763F;
        this.dirty = true;
      } 
      if (paramLong1 == 4L) {
        this.rgain = (float)paramLong2 * 0.00787F;
        this.dirty_vdelay1L_reverbsendgain = (float)paramLong2 * 0.00787F;
        this.dirty_vdelay1R_reverbsendgain = (float)paramLong2 * 0.00787F;
        this.dirty = true;
      } 
    } 
  }
  
  public void processControlLogic() {
    if (this.dirty) {
      this.dirty = false;
      this.vdelay1L.setRate(this.dirty_vdelay1L_rate);
      this.vdelay1R.setRate(this.dirty_vdelay1R_rate);
      this.vdelay1L.setDepth(this.dirty_vdelay1L_depth);
      this.vdelay1R.setDepth(this.dirty_vdelay1R_depth);
      this.vdelay1L.setFeedBack(this.dirty_vdelay1L_feedback);
      this.vdelay1R.setFeedBack(this.dirty_vdelay1R_feedback);
      this.vdelay1L.setReverbSendGain(this.dirty_vdelay1L_reverbsendgain);
      this.vdelay1R.setReverbSendGain(this.dirty_vdelay1R_reverbsendgain);
    } 
  }
  
  public void processAudio() {
    if (this.inputA.isSilent()) {
      this.silentcounter += (1.0F / this.controlrate);
      if (this.silentcounter > 1.0D) {
        if (!this.mix) {
          this.left.clear();
          this.right.clear();
        } 
        return;
      } 
    } else {
      this.silentcounter = 0.0D;
    } 
    float[] arrayOfFloat1 = this.inputA.array();
    float[] arrayOfFloat2 = this.left.array();
    float[] arrayOfFloat3 = (this.right == null) ? null : this.right.array();
    float[] arrayOfFloat4 = (this.rgain != 0.0F) ? this.reverb.array() : null;
    if (this.mix) {
      this.vdelay1L.processMix(arrayOfFloat1, arrayOfFloat2, arrayOfFloat4);
      if (arrayOfFloat3 != null)
        this.vdelay1R.processMix(arrayOfFloat1, arrayOfFloat3, arrayOfFloat4); 
    } else {
      this.vdelay1L.processReplace(arrayOfFloat1, arrayOfFloat2, arrayOfFloat4);
      if (arrayOfFloat3 != null)
        this.vdelay1R.processReplace(arrayOfFloat1, arrayOfFloat3, arrayOfFloat4); 
    } 
  }
  
  public void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer) {
    if (paramInt == 0)
      this.inputA = paramSoftAudioBuffer; 
  }
  
  public void setMixMode(boolean paramBoolean) { this.mix = paramBoolean; }
  
  public void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer) {
    if (paramInt == 0)
      this.left = paramSoftAudioBuffer; 
    if (paramInt == 1)
      this.right = paramSoftAudioBuffer; 
    if (paramInt == 2)
      this.reverb = paramSoftAudioBuffer; 
  }
  
  private static class LFODelay {
    private double phase = 1.0D;
    
    private double phase_step = 0.0D;
    
    private double depth = 0.0D;
    
    private SoftChorus.VariableDelay vdelay;
    
    private final double samplerate;
    
    private final double controlrate;
    
    LFODelay(double param1Double1, double param1Double2) {
      this.samplerate = param1Double1;
      this.controlrate = param1Double2;
      this.vdelay = new SoftChorus.VariableDelay((int)((this.depth + 10.0D) * 2.0D));
    }
    
    public void setDepth(double param1Double) {
      this.depth = param1Double * this.samplerate;
      this.vdelay = new SoftChorus.VariableDelay((int)((this.depth + 10.0D) * 2.0D));
    }
    
    public void setRate(double param1Double) {
      double d = 6.283185307179586D * param1Double / this.controlrate;
      this.phase_step = d;
    }
    
    public void setPhase(double param1Double) { this.phase = param1Double; }
    
    public void setFeedBack(float param1Float) { this.vdelay.setFeedBack(param1Float); }
    
    public void setGain(float param1Float) { this.vdelay.setGain(param1Float); }
    
    public void setReverbSendGain(float param1Float) { this.vdelay.setReverbSendGain(param1Float); }
    
    public void processMix(float[] param1ArrayOfFloat1, float[] param1ArrayOfFloat2, float[] param1ArrayOfFloat3) {
      this.phase += this.phase_step;
      while (this.phase > 6.283185307179586D)
        this.phase -= 6.283185307179586D; 
      this.vdelay.setDelay((float)(this.depth * 0.5D * (Math.cos(this.phase) + 2.0D)));
      this.vdelay.processMix(param1ArrayOfFloat1, param1ArrayOfFloat2, param1ArrayOfFloat3);
    }
    
    public void processReplace(float[] param1ArrayOfFloat1, float[] param1ArrayOfFloat2, float[] param1ArrayOfFloat3) {
      this.phase += this.phase_step;
      while (this.phase > 6.283185307179586D)
        this.phase -= 6.283185307179586D; 
      this.vdelay.setDelay((float)(this.depth * 0.5D * (Math.cos(this.phase) + 2.0D)));
      this.vdelay.processReplace(param1ArrayOfFloat1, param1ArrayOfFloat2, param1ArrayOfFloat3);
    }
  }
  
  private static class VariableDelay {
    private final float[] delaybuffer;
    
    private int rovepos = 0;
    
    private float gain = 1.0F;
    
    private float rgain = 0.0F;
    
    private float delay = 0.0F;
    
    private float lastdelay = 0.0F;
    
    private float feedback = 0.0F;
    
    VariableDelay(int param1Int) { this.delaybuffer = new float[param1Int]; }
    
    public void setDelay(float param1Float) { this.delay = param1Float; }
    
    public void setFeedBack(float param1Float) { this.feedback = param1Float; }
    
    public void setGain(float param1Float) { this.gain = param1Float; }
    
    public void setReverbSendGain(float param1Float) { this.rgain = param1Float; }
    
    public void processMix(float[] param1ArrayOfFloat1, float[] param1ArrayOfFloat2, float[] param1ArrayOfFloat3) {
      float f1 = this.gain;
      float f2 = this.delay;
      float f3 = this.feedback;
      float[] arrayOfFloat = this.delaybuffer;
      int i = param1ArrayOfFloat1.length;
      float f4 = (f2 - this.lastdelay) / i;
      int j = arrayOfFloat.length;
      int k = this.rovepos;
      if (param1ArrayOfFloat3 == null) {
        for (byte b = 0; b < i; b++) {
          float f5 = k - this.lastdelay + 2.0F + j;
          int m = (int)f5;
          float f6 = f5 - m;
          float f7 = arrayOfFloat[m % j];
          float f8 = arrayOfFloat[(m + 1) % j];
          float f9 = f7 * (1.0F - f6) + f8 * f6;
          param1ArrayOfFloat2[b] = param1ArrayOfFloat2[b] + f9 * f1;
          arrayOfFloat[k] = param1ArrayOfFloat1[b] + f9 * f3;
          k = (k + 1) % j;
          this.lastdelay += f4;
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          float f5 = k - this.lastdelay + 2.0F + j;
          int m = (int)f5;
          float f6 = f5 - m;
          float f7 = arrayOfFloat[m % j];
          float f8 = arrayOfFloat[(m + 1) % j];
          float f9 = f7 * (1.0F - f6) + f8 * f6;
          param1ArrayOfFloat2[b] = param1ArrayOfFloat2[b] + f9 * f1;
          param1ArrayOfFloat3[b] = param1ArrayOfFloat3[b] + f9 * this.rgain;
          arrayOfFloat[k] = param1ArrayOfFloat1[b] + f9 * f3;
          k = (k + 1) % j;
          this.lastdelay += f4;
        } 
      } 
      this.rovepos = k;
      this.lastdelay = f2;
    }
    
    public void processReplace(float[] param1ArrayOfFloat1, float[] param1ArrayOfFloat2, float[] param1ArrayOfFloat3) {
      Arrays.fill(param1ArrayOfFloat2, 0.0F);
      Arrays.fill(param1ArrayOfFloat3, 0.0F);
      processMix(param1ArrayOfFloat1, param1ArrayOfFloat2, param1ArrayOfFloat3);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftChorus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */