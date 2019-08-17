package com.sun.media.sound;

public final class SoftLowFrequencyOscillator implements SoftProcess {
  private final int max_count = 10;
  
  private int used_count = 0;
  
  private final double[][] out = new double[10][1];
  
  private final double[][] delay = new double[10][1];
  
  private final double[][] delay2 = new double[10][1];
  
  private final double[][] freq = new double[10][1];
  
  private final int[] delay_counter = new int[10];
  
  private final double[] sin_phase = new double[10];
  
  private final double[] sin_stepfreq = new double[10];
  
  private final double[] sin_step = new double[10];
  
  private double control_time = 0.0D;
  
  private double sin_factor = 0.0D;
  
  private static final double PI2 = 6.283185307179586D;
  
  public SoftLowFrequencyOscillator() {
    for (byte b = 0; b < this.sin_stepfreq.length; b++)
      this.sin_stepfreq[b] = Double.NEGATIVE_INFINITY; 
  }
  
  public void reset() {
    for (byte b = 0; b < this.used_count; b++) {
      this.out[b][0] = 0.0D;
      this.delay[b][0] = 0.0D;
      this.delay2[b][0] = 0.0D;
      this.freq[b][0] = 0.0D;
      this.delay_counter[b] = 0;
      this.sin_phase[b] = 0.0D;
      this.sin_stepfreq[b] = Double.NEGATIVE_INFINITY;
      this.sin_step[b] = 0.0D;
    } 
    this.used_count = 0;
  }
  
  public void init(SoftSynthesizer paramSoftSynthesizer) {
    this.control_time = 1.0D / paramSoftSynthesizer.getControlRate();
    this.sin_factor = this.control_time * 2.0D * Math.PI;
    for (byte b = 0; b < this.used_count; b++) {
      this.delay_counter[b] = (int)(Math.pow(2.0D, this.delay[b][0] / 1200.0D) / this.control_time);
      this.delay_counter[b] = this.delay_counter[b] + (int)(this.delay2[b][0] / this.control_time * 1000.0D);
    } 
    processControlLogic();
  }
  
  public void processControlLogic() {
    for (byte b = 0; b < this.used_count; b++) {
      if (this.delay_counter[b] > 0) {
        this.delay_counter[b] = this.delay_counter[b] - 1;
        this.out[b][0] = 0.5D;
      } else {
        double d1 = this.freq[b][0];
        if (this.sin_stepfreq[b] != d1) {
          this.sin_stepfreq[b] = d1;
          double d = 440.0D * Math.exp((d1 - 6900.0D) * Math.log(2.0D) / 1200.0D);
          this.sin_step[b] = d * this.sin_factor;
        } 
        double d2 = this.sin_phase[b];
        for (d2 += this.sin_step[b]; d2 > 6.283185307179586D; d2 -= 6.283185307179586D);
        this.out[b][0] = 0.5D + Math.sin(d2) * 0.5D;
        this.sin_phase[b] = d2;
      } 
    } 
  }
  
  public double[] get(int paramInt, String paramString) {
    if (paramInt >= this.used_count)
      this.used_count = paramInt + 1; 
    return (paramString == null) ? this.out[paramInt] : (paramString.equals("delay") ? this.delay[paramInt] : (paramString.equals("delay2") ? this.delay2[paramInt] : (paramString.equals("freq") ? this.freq[paramInt] : null)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftLowFrequencyOscillator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */