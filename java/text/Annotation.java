package java.text;

public class Annotation {
  private Object value;
  
  public Annotation(Object paramObject) { this.value = paramObject; }
  
  public Object getValue() { return this.value; }
  
  public String toString() { return getClass().getName() + "[value=" + this.value + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\Annotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */