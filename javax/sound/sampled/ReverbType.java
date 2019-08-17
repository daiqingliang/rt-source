package javax.sound.sampled;

public class ReverbType {
  private String name;
  
  private int earlyReflectionDelay;
  
  private float earlyReflectionIntensity;
  
  private int lateReflectionDelay;
  
  private float lateReflectionIntensity;
  
  private int decayTime;
  
  protected ReverbType(String paramString, int paramInt1, float paramFloat1, int paramInt2, float paramFloat2, int paramInt3) {
    this.name = paramString;
    this.earlyReflectionDelay = paramInt1;
    this.earlyReflectionIntensity = paramFloat1;
    this.lateReflectionDelay = paramInt2;
    this.lateReflectionIntensity = paramFloat2;
    this.decayTime = paramInt3;
  }
  
  public String getName() { return this.name; }
  
  public final int getEarlyReflectionDelay() { return this.earlyReflectionDelay; }
  
  public final float getEarlyReflectionIntensity() { return this.earlyReflectionIntensity; }
  
  public final int getLateReflectionDelay() { return this.lateReflectionDelay; }
  
  public final float getLateReflectionIntensity() { return this.lateReflectionIntensity; }
  
  public final int getDecayTime() { return this.decayTime; }
  
  public final boolean equals(Object paramObject) { return super.equals(paramObject); }
  
  public final int hashCode() { return super.hashCode(); }
  
  public final String toString() { return this.name + ", early reflection delay " + this.earlyReflectionDelay + " ns, early reflection intensity " + this.earlyReflectionIntensity + " dB, late deflection delay " + this.lateReflectionDelay + " ns, late reflection intensity " + this.lateReflectionIntensity + " dB, decay time " + this.decayTime; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\ReverbType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */