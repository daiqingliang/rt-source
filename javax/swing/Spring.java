package javax.swing;

import java.awt.Component;

public abstract class Spring {
  public static final int UNSET = -2147483648;
  
  public abstract int getMinimumValue();
  
  public abstract int getPreferredValue();
  
  public abstract int getMaximumValue();
  
  public abstract int getValue();
  
  public abstract void setValue(int paramInt);
  
  private double range(boolean paramBoolean) { return paramBoolean ? (getPreferredValue() - getMinimumValue()) : (getMaximumValue() - getPreferredValue()); }
  
  double getStrain() {
    double d = (getValue() - getPreferredValue());
    return d / range((getValue() < getPreferredValue()));
  }
  
  void setStrain(double paramDouble) { setValue(getPreferredValue() + (int)(paramDouble * range((paramDouble < 0.0D)))); }
  
  boolean isCyclic(SpringLayout paramSpringLayout) { return false; }
  
  public static Spring constant(int paramInt) { return constant(paramInt, paramInt, paramInt); }
  
  public static Spring constant(int paramInt1, int paramInt2, int paramInt3) { return new StaticSpring(paramInt1, paramInt2, paramInt3); }
  
  public static Spring minus(Spring paramSpring) { return new NegativeSpring(paramSpring); }
  
  public static Spring sum(Spring paramSpring1, Spring paramSpring2) { return new SumSpring(paramSpring1, paramSpring2); }
  
  public static Spring max(Spring paramSpring1, Spring paramSpring2) { return new MaxSpring(paramSpring1, paramSpring2); }
  
  static Spring difference(Spring paramSpring1, Spring paramSpring2) { return sum(paramSpring1, minus(paramSpring2)); }
  
  public static Spring scale(Spring paramSpring, float paramFloat) {
    checkArg(paramSpring);
    return new ScaleSpring(paramSpring, paramFloat, null);
  }
  
  public static Spring width(Component paramComponent) {
    checkArg(paramComponent);
    return new WidthSpring(paramComponent);
  }
  
  public static Spring height(Component paramComponent) {
    checkArg(paramComponent);
    return new HeightSpring(paramComponent);
  }
  
  private static void checkArg(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException("Argument must not be null"); 
  }
  
  static abstract class AbstractSpring extends Spring {
    protected int size = Integer.MIN_VALUE;
    
    public int getValue() { return (this.size != Integer.MIN_VALUE) ? this.size : getPreferredValue(); }
    
    public final void setValue(int param1Int) {
      if (this.size == param1Int)
        return; 
      if (param1Int == Integer.MIN_VALUE) {
        clear();
      } else {
        setNonClearValue(param1Int);
      } 
    }
    
    protected void clear() { this.size = Integer.MIN_VALUE; }
    
    protected void setNonClearValue(int param1Int) { this.size = param1Int; }
  }
  
  static abstract class CompoundSpring extends StaticSpring {
    protected Spring s1;
    
    protected Spring s2;
    
    public CompoundSpring(Spring param1Spring1, Spring param1Spring2) {
      super(-2147483648);
      this.s1 = param1Spring1;
      this.s2 = param1Spring2;
    }
    
    public String toString() { return "CompoundSpring of " + this.s1 + " and " + this.s2; }
    
    protected void clear() {
      super.clear();
      this.min = this.pref = this.max = Integer.MIN_VALUE;
      this.s1.setValue(-2147483648);
      this.s2.setValue(-2147483648);
    }
    
    protected abstract int op(int param1Int1, int param1Int2);
    
    public int getMinimumValue() {
      if (this.min == Integer.MIN_VALUE)
        this.min = op(this.s1.getMinimumValue(), this.s2.getMinimumValue()); 
      return this.min;
    }
    
    public int getPreferredValue() {
      if (this.pref == Integer.MIN_VALUE)
        this.pref = op(this.s1.getPreferredValue(), this.s2.getPreferredValue()); 
      return this.pref;
    }
    
    public int getMaximumValue() {
      if (this.max == Integer.MIN_VALUE)
        this.max = op(this.s1.getMaximumValue(), this.s2.getMaximumValue()); 
      return this.max;
    }
    
    public int getValue() {
      if (this.size == Integer.MIN_VALUE)
        this.size = op(this.s1.getValue(), this.s2.getValue()); 
      return this.size;
    }
    
    boolean isCyclic(SpringLayout param1SpringLayout) { return (param1SpringLayout.isCyclic(this.s1) || param1SpringLayout.isCyclic(this.s2)); }
  }
  
  static class HeightSpring extends AbstractSpring {
    Component c;
    
    public HeightSpring(Component param1Component) { this.c = param1Component; }
    
    public int getMinimumValue() { return (this.c.getMinimumSize()).height; }
    
    public int getPreferredValue() { return (this.c.getPreferredSize()).height; }
    
    public int getMaximumValue() { return Math.min(32767, (this.c.getMaximumSize()).height); }
  }
  
  private static class MaxSpring extends CompoundSpring {
    public MaxSpring(Spring param1Spring1, Spring param1Spring2) { super(param1Spring1, param1Spring2); }
    
    protected int op(int param1Int1, int param1Int2) { return Math.max(param1Int1, param1Int2); }
    
    protected void setNonClearValue(int param1Int) {
      super.setNonClearValue(param1Int);
      this.s1.setValue(param1Int);
      this.s2.setValue(param1Int);
    }
  }
  
  private static class NegativeSpring extends Spring {
    private Spring s;
    
    public NegativeSpring(Spring param1Spring) { this.s = param1Spring; }
    
    public int getMinimumValue() { return -this.s.getMaximumValue(); }
    
    public int getPreferredValue() { return -this.s.getPreferredValue(); }
    
    public int getMaximumValue() { return -this.s.getMinimumValue(); }
    
    public int getValue() { return -this.s.getValue(); }
    
    public void setValue(int param1Int) { this.s.setValue(-param1Int); }
    
    boolean isCyclic(SpringLayout param1SpringLayout) { return this.s.isCyclic(param1SpringLayout); }
  }
  
  private static class ScaleSpring extends Spring {
    private Spring s;
    
    private float factor;
    
    private ScaleSpring(Spring param1Spring, float param1Float) {
      this.s = param1Spring;
      this.factor = param1Float;
    }
    
    public int getMinimumValue() { return Math.round(((this.factor < 0.0F) ? this.s.getMaximumValue() : this.s.getMinimumValue()) * this.factor); }
    
    public int getPreferredValue() { return Math.round(this.s.getPreferredValue() * this.factor); }
    
    public int getMaximumValue() { return Math.round(((this.factor < 0.0F) ? this.s.getMinimumValue() : this.s.getMaximumValue()) * this.factor); }
    
    public int getValue() { return Math.round(this.s.getValue() * this.factor); }
    
    public void setValue(int param1Int) {
      if (param1Int == Integer.MIN_VALUE) {
        this.s.setValue(-2147483648);
      } else {
        this.s.setValue(Math.round(param1Int / this.factor));
      } 
    }
    
    boolean isCyclic(SpringLayout param1SpringLayout) { return this.s.isCyclic(param1SpringLayout); }
  }
  
  static abstract class SpringMap extends Spring {
    private Spring s;
    
    public SpringMap(Spring param1Spring) { this.s = param1Spring; }
    
    protected abstract int map(int param1Int);
    
    protected abstract int inv(int param1Int);
    
    public int getMinimumValue() { return map(this.s.getMinimumValue()); }
    
    public int getPreferredValue() { return map(this.s.getPreferredValue()); }
    
    public int getMaximumValue() { return Math.min(32767, map(this.s.getMaximumValue())); }
    
    public int getValue() { return map(this.s.getValue()); }
    
    public void setValue(int param1Int) {
      if (param1Int == Integer.MIN_VALUE) {
        this.s.setValue(-2147483648);
      } else {
        this.s.setValue(inv(param1Int));
      } 
    }
    
    boolean isCyclic(SpringLayout param1SpringLayout) { return this.s.isCyclic(param1SpringLayout); }
  }
  
  private static class StaticSpring extends AbstractSpring {
    protected int min;
    
    protected int pref;
    
    protected int max;
    
    public StaticSpring(int param1Int) { this(param1Int, param1Int, param1Int); }
    
    public StaticSpring(int param1Int1, int param1Int2, int param1Int3) {
      this.min = param1Int1;
      this.pref = param1Int2;
      this.max = param1Int3;
    }
    
    public String toString() { return "StaticSpring [" + this.min + ", " + this.pref + ", " + this.max + "]"; }
    
    public int getMinimumValue() { return this.min; }
    
    public int getPreferredValue() { return this.pref; }
    
    public int getMaximumValue() { return this.max; }
  }
  
  private static class SumSpring extends CompoundSpring {
    public SumSpring(Spring param1Spring1, Spring param1Spring2) { super(param1Spring1, param1Spring2); }
    
    protected int op(int param1Int1, int param1Int2) { return param1Int1 + param1Int2; }
    
    protected void setNonClearValue(int param1Int) {
      super.setNonClearValue(param1Int);
      this.s1.setStrain(getStrain());
      this.s2.setValue(param1Int - this.s1.getValue());
    }
  }
  
  static class WidthSpring extends AbstractSpring {
    Component c;
    
    public WidthSpring(Component param1Component) { this.c = param1Component; }
    
    public int getMinimumValue() { return (this.c.getMinimumSize()).width; }
    
    public int getPreferredValue() { return (this.c.getPreferredSize()).width; }
    
    public int getMaximumValue() { return Math.min(32767, (this.c.getMaximumSize()).width); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\Spring.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */