package com.sun.media.sound;

public final class SoftLimiter implements SoftAudioProcessor {
  float lastmax = 0.0F;
  
  float gain = 1.0F;
  
  float[] temp_bufferL;
  
  float[] temp_bufferR;
  
  boolean mix = false;
  
  SoftAudioBuffer bufferL;
  
  SoftAudioBuffer bufferR;
  
  SoftAudioBuffer bufferLout;
  
  SoftAudioBuffer bufferRout;
  
  float controlrate;
  
  double silentcounter = 0.0D;
  
  public void init(float paramFloat1, float paramFloat2) { this.controlrate = paramFloat2; }
  
  public void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer) {
    if (paramInt == 0)
      this.bufferL = paramSoftAudioBuffer; 
    if (paramInt == 1)
      this.bufferR = paramSoftAudioBuffer; 
  }
  
  public void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer) {
    if (paramInt == 0)
      this.bufferLout = paramSoftAudioBuffer; 
    if (paramInt == 1)
      this.bufferRout = paramSoftAudioBuffer; 
  }
  
  public void setMixMode(boolean paramBoolean) { this.mix = paramBoolean; }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2) {}
  
  public void processAudio() {
    if (this.bufferL.isSilent() && (this.bufferR == null || this.bufferR.isSilent())) {
      this.silentcounter += (1.0F / this.controlrate);
      if (this.silentcounter > 60.0D) {
        if (!this.mix) {
          this.bufferLout.clear();
          if (this.bufferRout != null)
            this.bufferRout.clear(); 
        } 
        return;
      } 
    } else {
      this.silentcounter = 0.0D;
    } 
    float[] arrayOfFloat1 = this.bufferL.array();
    float[] arrayOfFloat2 = (this.bufferR == null) ? null : this.bufferR.array();
    float[] arrayOfFloat3 = this.bufferLout.array();
    float[] arrayOfFloat4 = (this.bufferRout == null) ? null : this.bufferRout.array();
    if (this.temp_bufferL == null || this.temp_bufferL.length < arrayOfFloat1.length)
      this.temp_bufferL = new float[arrayOfFloat1.length]; 
    if (arrayOfFloat2 != null && (this.temp_bufferR == null || this.temp_bufferR.length < arrayOfFloat2.length))
      this.temp_bufferR = new float[arrayOfFloat2.length]; 
    float f1 = 0.0F;
    int i = arrayOfFloat1.length;
    if (arrayOfFloat2 == null) {
      for (byte b = 0; b < i; b++) {
        if (arrayOfFloat1[b] > f1)
          f1 = arrayOfFloat1[b]; 
        if (-arrayOfFloat1[b] > f1)
          f1 = -arrayOfFloat1[b]; 
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        if (arrayOfFloat1[b] > f1)
          f1 = arrayOfFloat1[b]; 
        if (arrayOfFloat2[b] > f1)
          f1 = arrayOfFloat2[b]; 
        if (-arrayOfFloat1[b] > f1)
          f1 = -arrayOfFloat1[b]; 
        if (-arrayOfFloat2[b] > f1)
          f1 = -arrayOfFloat2[b]; 
      } 
    } 
    float f2 = this.lastmax;
    this.lastmax = f1;
    if (f2 > f1)
      f1 = f2; 
    float f3 = 1.0F;
    if (f1 > 0.99F) {
      f3 = 0.99F / f1;
    } else {
      f3 = 1.0F;
    } 
    if (f3 > this.gain)
      f3 = (f3 + this.gain * 9.0F) / 10.0F; 
    float f4 = (f3 - this.gain) / i;
    if (this.mix) {
      if (arrayOfFloat2 == null) {
        for (byte b = 0; b < i; b++) {
          this.gain += f4;
          float f5 = arrayOfFloat1[b];
          float f6 = this.temp_bufferL[b];
          this.temp_bufferL[b] = f5;
          arrayOfFloat3[b] = arrayOfFloat3[b] + f6 * this.gain;
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          this.gain += f4;
          float f5 = arrayOfFloat1[b];
          float f6 = arrayOfFloat2[b];
          float f7 = this.temp_bufferL[b];
          float f8 = this.temp_bufferR[b];
          this.temp_bufferL[b] = f5;
          this.temp_bufferR[b] = f6;
          arrayOfFloat3[b] = arrayOfFloat3[b] + f7 * this.gain;
          arrayOfFloat4[b] = arrayOfFloat4[b] + f8 * this.gain;
        } 
      } 
    } else if (arrayOfFloat2 == null) {
      for (byte b = 0; b < i; b++) {
        this.gain += f4;
        float f5 = arrayOfFloat1[b];
        float f6 = this.temp_bufferL[b];
        this.temp_bufferL[b] = f5;
        arrayOfFloat3[b] = f6 * this.gain;
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        this.gain += f4;
        float f5 = arrayOfFloat1[b];
        float f6 = arrayOfFloat2[b];
        float f7 = this.temp_bufferL[b];
        float f8 = this.temp_bufferR[b];
        this.temp_bufferL[b] = f5;
        this.temp_bufferR[b] = f6;
        arrayOfFloat3[b] = f7 * this.gain;
        arrayOfFloat4[b] = f8 * this.gain;
      } 
    } 
    this.gain = f3;
  }
  
  public void processControlLogic() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftLimiter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */