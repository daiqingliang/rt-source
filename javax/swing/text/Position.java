package javax.swing.text;

public interface Position {
  int getOffset();
  
  public static final class Bias {
    public static final Bias Forward = new Bias("Forward");
    
    public static final Bias Backward = new Bias("Backward");
    
    private String name;
    
    public String toString() { return this.name; }
    
    private Bias(String param1String) { this.name = param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\Position.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */