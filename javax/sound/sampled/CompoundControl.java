package javax.sound.sampled;

public abstract class CompoundControl extends Control {
  private Control[] controls;
  
  protected CompoundControl(Type paramType, Control[] paramArrayOfControl) {
    super(paramType);
    this.controls = paramArrayOfControl;
  }
  
  public Control[] getMemberControls() {
    Control[] arrayOfControl = new Control[this.controls.length];
    for (byte b = 0; b < this.controls.length; b++)
      arrayOfControl[b] = this.controls[b]; 
    return arrayOfControl;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.controls.length; b++) {
      if (b) {
        stringBuffer.append(", ");
        if (b + true == this.controls.length)
          stringBuffer.append("and "); 
      } 
      stringBuffer.append(this.controls[b].getType());
    } 
    return new String(getType() + " Control containing " + stringBuffer + " Controls.");
  }
  
  public static class Type extends Control.Type {
    protected Type(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\CompoundControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */