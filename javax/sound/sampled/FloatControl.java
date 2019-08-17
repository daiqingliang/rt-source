package javax.sound.sampled;

public abstract class FloatControl extends Control {
  private float minimum;
  
  private float maximum;
  
  private float precision;
  
  private int updatePeriod;
  
  private final String units;
  
  private final String minLabel;
  
  private final String maxLabel;
  
  private final String midLabel;
  
  private float value;
  
  protected FloatControl(Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, float paramFloat4, String paramString1, String paramString2, String paramString3, String paramString4) {
    super(paramType);
    if (paramFloat1 > paramFloat2)
      throw new IllegalArgumentException("Minimum value " + paramFloat1 + " exceeds maximum value " + paramFloat2 + "."); 
    if (paramFloat4 < paramFloat1)
      throw new IllegalArgumentException("Initial value " + paramFloat4 + " smaller than allowable minimum value " + paramFloat1 + "."); 
    if (paramFloat4 > paramFloat2)
      throw new IllegalArgumentException("Initial value " + paramFloat4 + " exceeds allowable maximum value " + paramFloat2 + "."); 
    this.minimum = paramFloat1;
    this.maximum = paramFloat2;
    this.precision = paramFloat3;
    this.updatePeriod = paramInt;
    this.value = paramFloat4;
    this.units = paramString1;
    this.minLabel = (paramString2 == null) ? "" : paramString2;
    this.midLabel = (paramString3 == null) ? "" : paramString3;
    this.maxLabel = (paramString4 == null) ? "" : paramString4;
  }
  
  protected FloatControl(Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt, float paramFloat4, String paramString) { this(paramType, paramFloat1, paramFloat2, paramFloat3, paramInt, paramFloat4, paramString, "", "", ""); }
  
  public void setValue(float paramFloat) {
    if (paramFloat > this.maximum)
      throw new IllegalArgumentException("Requested value " + paramFloat + " exceeds allowable maximum value " + this.maximum + "."); 
    if (paramFloat < this.minimum)
      throw new IllegalArgumentException("Requested value " + paramFloat + " smaller than allowable minimum value " + this.minimum + "."); 
    this.value = paramFloat;
  }
  
  public float getValue() { return this.value; }
  
  public float getMaximum() { return this.maximum; }
  
  public float getMinimum() { return this.minimum; }
  
  public String getUnits() { return this.units; }
  
  public String getMinLabel() { return this.minLabel; }
  
  public String getMidLabel() { return this.midLabel; }
  
  public String getMaxLabel() { return this.maxLabel; }
  
  public float getPrecision() { return this.precision; }
  
  public int getUpdatePeriod() { return this.updatePeriod; }
  
  public void shift(float paramFloat1, float paramFloat2, int paramInt) {
    if (paramFloat1 < this.minimum)
      throw new IllegalArgumentException("Requested value " + paramFloat1 + " smaller than allowable minimum value " + this.minimum + "."); 
    if (paramFloat1 > this.maximum)
      throw new IllegalArgumentException("Requested value " + paramFloat1 + " exceeds allowable maximum value " + this.maximum + "."); 
    setValue(paramFloat2);
  }
  
  public String toString() { return new String(getType() + " with current value: " + getValue() + " " + this.units + " (range: " + this.minimum + " - " + this.maximum + ")"); }
  
  public static class Type extends Control.Type {
    public static final Type MASTER_GAIN = new Type("Master Gain");
    
    public static final Type AUX_SEND = new Type("AUX Send");
    
    public static final Type AUX_RETURN = new Type("AUX Return");
    
    public static final Type REVERB_SEND = new Type("Reverb Send");
    
    public static final Type REVERB_RETURN = new Type("Reverb Return");
    
    public static final Type VOLUME = new Type("Volume");
    
    public static final Type PAN = new Type("Pan");
    
    public static final Type BALANCE = new Type("Balance");
    
    public static final Type SAMPLE_RATE = new Type("Sample Rate");
    
    protected Type(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\FloatControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */