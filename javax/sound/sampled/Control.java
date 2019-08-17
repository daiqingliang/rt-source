package javax.sound.sampled;

public abstract class Control {
  private final Type type;
  
  protected Control(Type paramType) { this.type = paramType; }
  
  public Type getType() { return this.type; }
  
  public String toString() { return new String(getType() + " Control"); }
  
  public static class Type {
    private String name;
    
    protected Type(String param1String) { this.name = param1String; }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\Control.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */