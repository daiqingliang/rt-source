package javax.sound.sampled;

public abstract class BooleanControl extends Control {
  private final String trueStateLabel;
  
  private final String falseStateLabel;
  
  private boolean value;
  
  protected BooleanControl(Type paramType, boolean paramBoolean, String paramString1, String paramString2) {
    super(paramType);
    this.value = paramBoolean;
    this.trueStateLabel = paramString1;
    this.falseStateLabel = paramString2;
  }
  
  protected BooleanControl(Type paramType, boolean paramBoolean) { this(paramType, paramBoolean, "true", "false"); }
  
  public void setValue(boolean paramBoolean) { this.value = paramBoolean; }
  
  public boolean getValue() { return this.value; }
  
  public String getStateLabel(boolean paramBoolean) { return (paramBoolean == true) ? this.trueStateLabel : this.falseStateLabel; }
  
  public String toString() { return new String(super.toString() + " with current value: " + getStateLabel(getValue())); }
  
  public static class Type extends Control.Type {
    public static final Type MUTE = new Type("Mute");
    
    public static final Type APPLY_REVERB = new Type("Apply Reverb");
    
    protected Type(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\BooleanControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */