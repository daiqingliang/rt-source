package javax.swing;

import java.io.Serializable;

public class SpinnerNumberModel extends AbstractSpinnerModel implements Serializable {
  private Number stepSize;
  
  private Number value;
  
  private Comparable minimum;
  
  private Comparable maximum;
  
  public SpinnerNumberModel(Number paramNumber1, Comparable paramComparable1, Comparable paramComparable2, Number paramNumber2) {
    if (paramNumber1 == null || paramNumber2 == null)
      throw new IllegalArgumentException("value and stepSize must be non-null"); 
    if ((paramComparable1 != null && paramComparable1.compareTo(paramNumber1) > 0) || (paramComparable2 != null && paramComparable2.compareTo(paramNumber1) < 0))
      throw new IllegalArgumentException("(minimum <= value <= maximum) is false"); 
    this.value = paramNumber1;
    this.minimum = paramComparable1;
    this.maximum = paramComparable2;
    this.stepSize = paramNumber2;
  }
  
  public SpinnerNumberModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4)); }
  
  public SpinnerNumberModel(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { this(new Double(paramDouble1), new Double(paramDouble2), new Double(paramDouble3), new Double(paramDouble4)); }
  
  public SpinnerNumberModel() { this(Integer.valueOf(0), null, null, Integer.valueOf(1)); }
  
  public void setMinimum(Comparable paramComparable) {
    if ((paramComparable == null) ? (this.minimum != null) : !paramComparable.equals(this.minimum)) {
      this.minimum = paramComparable;
      fireStateChanged();
    } 
  }
  
  public Comparable getMinimum() { return this.minimum; }
  
  public void setMaximum(Comparable paramComparable) {
    if ((paramComparable == null) ? (this.maximum != null) : !paramComparable.equals(this.maximum)) {
      this.maximum = paramComparable;
      fireStateChanged();
    } 
  }
  
  public Comparable getMaximum() { return this.maximum; }
  
  public void setStepSize(Number paramNumber) {
    if (paramNumber == null)
      throw new IllegalArgumentException("null stepSize"); 
    if (!paramNumber.equals(this.stepSize)) {
      this.stepSize = paramNumber;
      fireStateChanged();
    } 
  }
  
  public Number getStepSize() { return this.stepSize; }
  
  private Number incrValue(int paramInt) {
    Byte byte;
    if (this.value instanceof Float || this.value instanceof Double) {
      double d = this.value.doubleValue() + this.stepSize.doubleValue() * paramInt;
      if (this.value instanceof Double) {
        byte = new Double(d);
      } else {
        byte = new Float(d);
      } 
    } else {
      long l = this.value.longValue() + this.stepSize.longValue() * paramInt;
      if (this.value instanceof Long) {
        byte = Long.valueOf(l);
      } else if (this.value instanceof Integer) {
        byte = Integer.valueOf((int)l);
      } else if (this.value instanceof Short) {
        byte = Short.valueOf((short)(int)l);
      } else {
        byte = Byte.valueOf((byte)(int)l);
      } 
    } 
    return (this.maximum != null && this.maximum.compareTo(byte) < 0) ? null : ((this.minimum != null && this.minimum.compareTo(byte) > 0) ? null : byte);
  }
  
  public Object getNextValue() { return incrValue(1); }
  
  public Object getPreviousValue() { return incrValue(-1); }
  
  public Number getNumber() { return this.value; }
  
  public Object getValue() { return this.value; }
  
  public void setValue(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Number))
      throw new IllegalArgumentException("illegal value"); 
    if (!paramObject.equals(this.value)) {
      this.value = (Number)paramObject;
      fireStateChanged();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SpinnerNumberModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */