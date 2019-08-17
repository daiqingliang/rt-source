package com.sun.media.sound;

public final class SoftFilter {
  public static final int FILTERTYPE_LP6 = 0;
  
  public static final int FILTERTYPE_LP12 = 1;
  
  public static final int FILTERTYPE_HP12 = 17;
  
  public static final int FILTERTYPE_BP12 = 33;
  
  public static final int FILTERTYPE_NP12 = 49;
  
  public static final int FILTERTYPE_LP24 = 3;
  
  public static final int FILTERTYPE_HP24 = 19;
  
  private int filtertype = 0;
  
  private final float samplerate;
  
  private float x1;
  
  private float x2;
  
  private float y1;
  
  private float y2;
  
  private float xx1;
  
  private float xx2;
  
  private float yy1;
  
  private float yy2;
  
  private float a0;
  
  private float a1;
  
  private float a2;
  
  private float b1;
  
  private float b2;
  
  private float q;
  
  private float gain = 1.0F;
  
  private float wet = 0.0F;
  
  private float last_wet = 0.0F;
  
  private float last_a0;
  
  private float last_a1;
  
  private float last_a2;
  
  private float last_b1;
  
  private float last_b2;
  
  private float last_q;
  
  private float last_gain;
  
  private boolean last_set = false;
  
  private double cutoff = 44100.0D;
  
  private double resonancedB = 0.0D;
  
  private boolean dirty = true;
  
  public SoftFilter(float paramFloat) {
    this.samplerate = paramFloat;
    this.dirty = true;
  }
  
  public void setFrequency(double paramDouble) {
    if (this.cutoff == paramDouble)
      return; 
    this.cutoff = paramDouble;
    this.dirty = true;
  }
  
  public void setResonance(double paramDouble) {
    if (this.resonancedB == paramDouble)
      return; 
    this.resonancedB = paramDouble;
    this.dirty = true;
  }
  
  public void reset() {
    this.dirty = true;
    this.last_set = false;
    this.x1 = 0.0F;
    this.x2 = 0.0F;
    this.y1 = 0.0F;
    this.y2 = 0.0F;
    this.xx1 = 0.0F;
    this.xx2 = 0.0F;
    this.yy1 = 0.0F;
    this.yy2 = 0.0F;
    this.wet = 0.0F;
    this.gain = 1.0F;
    this.a0 = 0.0F;
    this.a1 = 0.0F;
    this.a2 = 0.0F;
    this.b1 = 0.0F;
    this.b2 = 0.0F;
  }
  
  public void setFilterType(int paramInt) { this.filtertype = paramInt; }
  
  public void processAudio(SoftAudioBuffer paramSoftAudioBuffer) {
    if (this.filtertype == 0)
      filter1(paramSoftAudioBuffer); 
    if (this.filtertype == 1)
      filter2(paramSoftAudioBuffer); 
    if (this.filtertype == 17)
      filter2(paramSoftAudioBuffer); 
    if (this.filtertype == 33)
      filter2(paramSoftAudioBuffer); 
    if (this.filtertype == 49)
      filter2(paramSoftAudioBuffer); 
    if (this.filtertype == 3)
      filter4(paramSoftAudioBuffer); 
    if (this.filtertype == 19)
      filter4(paramSoftAudioBuffer); 
  }
  
  public void filter4(SoftAudioBuffer paramSoftAudioBuffer) {
    float[] arrayOfFloat = paramSoftAudioBuffer.array();
    if (this.dirty) {
      filter2calc();
      this.dirty = false;
    } 
    if (!this.last_set) {
      this.last_a0 = this.a0;
      this.last_a1 = this.a1;
      this.last_a2 = this.a2;
      this.last_b1 = this.b1;
      this.last_b2 = this.b2;
      this.last_gain = this.gain;
      this.last_wet = this.wet;
      this.last_set = true;
    } 
    if (this.wet > 0.0F || this.last_wet > 0.0F) {
      int i = arrayOfFloat.length;
      float f1 = this.last_a0;
      float f2 = this.last_a1;
      float f3 = this.last_a2;
      float f4 = this.last_b1;
      float f5 = this.last_b2;
      float f6 = this.last_gain;
      float f7 = this.last_wet;
      float f8 = (this.a0 - this.last_a0) / i;
      float f9 = (this.a1 - this.last_a1) / i;
      float f10 = (this.a2 - this.last_a2) / i;
      float f11 = (this.b1 - this.last_b1) / i;
      float f12 = (this.b2 - this.last_b2) / i;
      float f13 = (this.gain - this.last_gain) / i;
      float f14 = (this.wet - this.last_wet) / i;
      float f15 = this.x1;
      float f16 = this.x2;
      float f17 = this.y1;
      float f18 = this.y2;
      float f19 = this.xx1;
      float f20 = this.xx2;
      float f21 = this.yy1;
      float f22 = this.yy2;
      if (f14 != 0.0F) {
        for (byte b = 0; b < i; b++) {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          f7 += f14;
          float f23 = arrayOfFloat[b];
          float f24 = f1 * f23 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          float f25 = f24 * f6 * f7 + f23 * (1.0F - f7);
          f16 = f15;
          f15 = f23;
          f18 = f17;
          f17 = f24;
          float f26 = f1 * f25 + f2 * f19 + f3 * f20 - f4 * f21 - f5 * f22;
          arrayOfFloat[b] = f26 * f6 * f7 + f25 * (1.0F - f7);
          f20 = f19;
          f19 = f25;
          f22 = f21;
          f21 = f26;
        } 
      } else if (f8 == 0.0F && f9 == 0.0F && f10 == 0.0F && f11 == 0.0F && f12 == 0.0F) {
        for (byte b = 0; b < i; b++) {
          float f23 = arrayOfFloat[b];
          float f24 = f1 * f23 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          float f25 = f24 * f6 * f7 + f23 * (1.0F - f7);
          f16 = f15;
          f15 = f23;
          f18 = f17;
          f17 = f24;
          float f26 = f1 * f25 + f2 * f19 + f3 * f20 - f4 * f21 - f5 * f22;
          arrayOfFloat[b] = f26 * f6 * f7 + f25 * (1.0F - f7);
          f20 = f19;
          f19 = f25;
          f22 = f21;
          f21 = f26;
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          float f23 = arrayOfFloat[b];
          float f24 = f1 * f23 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          float f25 = f24 * f6 * f7 + f23 * (1.0F - f7);
          f16 = f15;
          f15 = f23;
          f18 = f17;
          f17 = f24;
          float f26 = f1 * f25 + f2 * f19 + f3 * f20 - f4 * f21 - f5 * f22;
          arrayOfFloat[b] = f26 * f6 * f7 + f25 * (1.0F - f7);
          f20 = f19;
          f19 = f25;
          f22 = f21;
          f21 = f26;
        } 
      } 
      if (Math.abs(f15) < 1.0E-8D)
        f15 = 0.0F; 
      if (Math.abs(f16) < 1.0E-8D)
        f16 = 0.0F; 
      if (Math.abs(f17) < 1.0E-8D)
        f17 = 0.0F; 
      if (Math.abs(f18) < 1.0E-8D)
        f18 = 0.0F; 
      this.x1 = f15;
      this.x2 = f16;
      this.y1 = f17;
      this.y2 = f18;
      this.xx1 = f19;
      this.xx2 = f20;
      this.yy1 = f21;
      this.yy2 = f22;
    } 
    this.last_a0 = this.a0;
    this.last_a1 = this.a1;
    this.last_a2 = this.a2;
    this.last_b1 = this.b1;
    this.last_b2 = this.b2;
    this.last_gain = this.gain;
    this.last_wet = this.wet;
  }
  
  private double sinh(double paramDouble) { return (Math.exp(paramDouble) - Math.exp(-paramDouble)) * 0.5D; }
  
  public void filter2calc() {
    double d = this.resonancedB;
    if (d < 0.0D)
      d = 0.0D; 
    if (d > 30.0D)
      d = 30.0D; 
    if (this.filtertype == 3 || this.filtertype == 19)
      d *= 0.6D; 
    if (this.filtertype == 33) {
      this.wet = 1.0F;
      double d1 = this.cutoff / this.samplerate;
      if (d1 > 0.45D)
        d1 = 0.45D; 
      double d2 = Math.PI * Math.pow(10.0D, -(d / 20.0D));
      double d3 = 6.283185307179586D * d1;
      double d4 = Math.cos(d3);
      double d5 = Math.sin(d3);
      double d6 = d5 * sinh(Math.log(2.0D) * d2 * d3 / d5 * 2.0D);
      double d7 = d6;
      double d8 = 0.0D;
      double d9 = -d6;
      double d10 = 1.0D + d6;
      double d11 = -2.0D * d4;
      double d12 = 1.0D - d6;
      double d13 = 1.0D / d10;
      this.b1 = (float)(d11 * d13);
      this.b2 = (float)(d12 * d13);
      this.a0 = (float)(d7 * d13);
      this.a1 = (float)(d8 * d13);
      this.a2 = (float)(d9 * d13);
    } 
    if (this.filtertype == 49) {
      this.wet = 1.0F;
      double d1 = this.cutoff / this.samplerate;
      if (d1 > 0.45D)
        d1 = 0.45D; 
      double d2 = Math.PI * Math.pow(10.0D, -(d / 20.0D));
      double d3 = 6.283185307179586D * d1;
      double d4 = Math.cos(d3);
      double d5 = Math.sin(d3);
      double d6 = d5 * sinh(Math.log(2.0D) * d2 * d3 / d5 * 2.0D);
      double d7 = 1.0D;
      double d8 = -2.0D * d4;
      double d9 = 1.0D;
      double d10 = 1.0D + d6;
      double d11 = -2.0D * d4;
      double d12 = 1.0D - d6;
      double d13 = 1.0D / d10;
      this.b1 = (float)(d11 * d13);
      this.b2 = (float)(d12 * d13);
      this.a0 = (float)(d7 * d13);
      this.a1 = (float)(d8 * d13);
      this.a2 = (float)(d9 * d13);
    } 
    if (this.filtertype == 1 || this.filtertype == 3) {
      double d1 = this.cutoff / this.samplerate;
      if (d1 > 0.45D) {
        if (this.wet == 0.0F)
          if (d < 1.0E-5D) {
            this.wet = 0.0F;
          } else {
            this.wet = 1.0F;
          }  
        d1 = 0.45D;
      } else {
        this.wet = 1.0F;
      } 
      double d2 = 1.0D / Math.tan(Math.PI * d1);
      double d3 = d2 * d2;
      double d4 = Math.pow(10.0D, -(d / 20.0D));
      double d5 = Math.sqrt(2.0D) * d4;
      double d6 = 1.0D / (1.0D + d5 * d2 + d3);
      double d7 = 2.0D * d6;
      double d8 = d6;
      double d9 = 2.0D * d6 * (1.0D - d3);
      double d10 = d6 * (1.0D - d5 * d2 + d3);
      this.a0 = (float)d6;
      this.a1 = (float)d7;
      this.a2 = (float)d8;
      this.b1 = (float)d9;
      this.b2 = (float)d10;
    } 
    if (this.filtertype == 17 || this.filtertype == 19) {
      double d1 = this.cutoff / this.samplerate;
      if (d1 > 0.45D)
        d1 = 0.45D; 
      if (d1 < 1.0E-4D)
        d1 = 1.0E-4D; 
      this.wet = 1.0F;
      double d2 = Math.tan(Math.PI * d1);
      double d3 = d2 * d2;
      double d4 = Math.pow(10.0D, -(d / 20.0D));
      double d5 = Math.sqrt(2.0D) * d4;
      double d6 = 1.0D / (1.0D + d5 * d2 + d3);
      double d7 = -2.0D * d6;
      double d8 = d6;
      double d9 = 2.0D * d6 * (d3 - 1.0D);
      double d10 = d6 * (1.0D - d5 * d2 + d3);
      this.a0 = (float)d6;
      this.a1 = (float)d7;
      this.a2 = (float)d8;
      this.b1 = (float)d9;
      this.b2 = (float)d10;
    } 
  }
  
  public void filter2(SoftAudioBuffer paramSoftAudioBuffer) {
    float[] arrayOfFloat = paramSoftAudioBuffer.array();
    if (this.dirty) {
      filter2calc();
      this.dirty = false;
    } 
    if (!this.last_set) {
      this.last_a0 = this.a0;
      this.last_a1 = this.a1;
      this.last_a2 = this.a2;
      this.last_b1 = this.b1;
      this.last_b2 = this.b2;
      this.last_q = this.q;
      this.last_gain = this.gain;
      this.last_wet = this.wet;
      this.last_set = true;
    } 
    if (this.wet > 0.0F || this.last_wet > 0.0F) {
      int i = arrayOfFloat.length;
      float f1 = this.last_a0;
      float f2 = this.last_a1;
      float f3 = this.last_a2;
      float f4 = this.last_b1;
      float f5 = this.last_b2;
      float f6 = this.last_gain;
      float f7 = this.last_wet;
      float f8 = (this.a0 - this.last_a0) / i;
      float f9 = (this.a1 - this.last_a1) / i;
      float f10 = (this.a2 - this.last_a2) / i;
      float f11 = (this.b1 - this.last_b1) / i;
      float f12 = (this.b2 - this.last_b2) / i;
      float f13 = (this.gain - this.last_gain) / i;
      float f14 = (this.wet - this.last_wet) / i;
      float f15 = this.x1;
      float f16 = this.x2;
      float f17 = this.y1;
      float f18 = this.y2;
      if (f14 != 0.0F) {
        for (byte b = 0; b < i; b++) {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          f7 += f14;
          float f19 = arrayOfFloat[b];
          float f20 = f1 * f19 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          arrayOfFloat[b] = f20 * f6 * f7 + f19 * (1.0F - f7);
          f16 = f15;
          f15 = f19;
          f18 = f17;
          f17 = f20;
        } 
      } else if (f8 == 0.0F && f9 == 0.0F && f10 == 0.0F && f11 == 0.0F && f12 == 0.0F) {
        for (byte b = 0; b < i; b++) {
          float f19 = arrayOfFloat[b];
          float f20 = f1 * f19 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          arrayOfFloat[b] = f20 * f6;
          f16 = f15;
          f15 = f19;
          f18 = f17;
          f17 = f20;
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          f1 += f8;
          f2 += f9;
          f3 += f10;
          f4 += f11;
          f5 += f12;
          f6 += f13;
          float f19 = arrayOfFloat[b];
          float f20 = f1 * f19 + f2 * f15 + f3 * f16 - f4 * f17 - f5 * f18;
          arrayOfFloat[b] = f20 * f6;
          f16 = f15;
          f15 = f19;
          f18 = f17;
          f17 = f20;
        } 
      } 
      if (Math.abs(f15) < 1.0E-8D)
        f15 = 0.0F; 
      if (Math.abs(f16) < 1.0E-8D)
        f16 = 0.0F; 
      if (Math.abs(f17) < 1.0E-8D)
        f17 = 0.0F; 
      if (Math.abs(f18) < 1.0E-8D)
        f18 = 0.0F; 
      this.x1 = f15;
      this.x2 = f16;
      this.y1 = f17;
      this.y2 = f18;
    } 
    this.last_a0 = this.a0;
    this.last_a1 = this.a1;
    this.last_a2 = this.a2;
    this.last_b1 = this.b1;
    this.last_b2 = this.b2;
    this.last_q = this.q;
    this.last_gain = this.gain;
    this.last_wet = this.wet;
  }
  
  public void filter1calc() {
    if (this.cutoff < 120.0D)
      this.cutoff = 120.0D; 
    double d = 7.3303828583761845D * this.cutoff / this.samplerate;
    if (d > 1.0D)
      d = 1.0D; 
    this.a0 = (float)(Math.sqrt(1.0D - Math.cos(d)) * Math.sqrt(1.5707963267948966D));
    if (this.resonancedB < 0.0D)
      this.resonancedB = 0.0D; 
    if (this.resonancedB > 20.0D)
      this.resonancedB = 20.0D; 
    this.q = (float)(Math.sqrt(0.5D) * Math.pow(10.0D, -(this.resonancedB / 20.0D)));
    this.gain = (float)Math.pow(10.0D, -this.resonancedB / 40.0D);
    if (this.wet == 0.0F && (this.resonancedB > 1.0E-5D || d < 0.9999999D))
      this.wet = 1.0F; 
  }
  
  public void filter1(SoftAudioBuffer paramSoftAudioBuffer) {
    if (this.dirty) {
      filter1calc();
      this.dirty = false;
    } 
    if (!this.last_set) {
      this.last_a0 = this.a0;
      this.last_q = this.q;
      this.last_gain = this.gain;
      this.last_wet = this.wet;
      this.last_set = true;
    } 
    if (this.wet > 0.0F || this.last_wet > 0.0F) {
      float[] arrayOfFloat = paramSoftAudioBuffer.array();
      int i = arrayOfFloat.length;
      float f1 = this.last_a0;
      float f2 = this.last_q;
      float f3 = this.last_gain;
      float f4 = this.last_wet;
      float f5 = (this.a0 - this.last_a0) / i;
      float f6 = (this.q - this.last_q) / i;
      float f7 = (this.gain - this.last_gain) / i;
      float f8 = (this.wet - this.last_wet) / i;
      float f9 = this.y2;
      float f10 = this.y1;
      if (f8 != 0.0F) {
        for (byte b = 0; b < i; b++) {
          f1 += f5;
          f2 += f6;
          f3 += f7;
          f4 += f8;
          float f = 1.0F - f2 * f1;
          f10 = f * f10 + f1 * (arrayOfFloat[b] - f9);
          f9 = f * f9 + f1 * f10;
          arrayOfFloat[b] = f9 * f3 * f4 + arrayOfFloat[b] * (1.0F - f4);
        } 
      } else if (f5 == 0.0F && f6 == 0.0F) {
        float f = 1.0F - f2 * f1;
        for (byte b = 0; b < i; b++) {
          f10 = f * f10 + f1 * (arrayOfFloat[b] - f9);
          f9 = f * f9 + f1 * f10;
          arrayOfFloat[b] = f9 * f3;
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          f1 += f5;
          f2 += f6;
          f3 += f7;
          float f = 1.0F - f2 * f1;
          f10 = f * f10 + f1 * (arrayOfFloat[b] - f9);
          f9 = f * f9 + f1 * f10;
          arrayOfFloat[b] = f9 * f3;
        } 
      } 
      if (Math.abs(f9) < 1.0E-8D)
        f9 = 0.0F; 
      if (Math.abs(f10) < 1.0E-8D)
        f10 = 0.0F; 
      this.y2 = f9;
      this.y1 = f10;
    } 
    this.last_a0 = this.a0;
    this.last_q = this.q;
    this.last_gain = this.gain;
    this.last_wet = this.wet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */