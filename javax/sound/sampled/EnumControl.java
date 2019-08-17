package javax.sound.sampled;

public abstract class EnumControl extends Control {
  private Object[] values;
  
  private Object value;
  
  protected EnumControl(Type paramType, Object[] paramArrayOfObject, Object paramObject) {
    super(paramType);
    this.values = paramArrayOfObject;
    this.value = paramObject;
  }
  
  public void setValue(Object paramObject) {
    if (!isValueSupported(paramObject))
      throw new IllegalArgumentException("Requested value " + paramObject + " is not supported."); 
    this.value = paramObject;
  }
  
  public Object getValue() { return this.value; }
  
  public Object[] getValues() {
    Object[] arrayOfObject = new Object[this.values.length];
    for (byte b = 0; b < this.values.length; b++)
      arrayOfObject[b] = this.values[b]; 
    return arrayOfObject;
  }
  
  private boolean isValueSupported(Object paramObject) {
    for (byte b = 0; b < this.values.length; b++) {
      if (paramObject.equals(this.values[b]))
        return true; 
    } 
    return false;
  }
  
  public String toString() { return new String(getType() + " with current value: " + getValue()); }
  
  public static class Type extends Control.Type {
    public static final Type REVERB = new Type("Reverb");
    
    protected Type(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\EnumControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */