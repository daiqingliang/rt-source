package javax.swing.plaf.synth;

public class ColorType {
  public static final ColorType FOREGROUND = new ColorType("Foreground");
  
  public static final ColorType BACKGROUND = new ColorType("Background");
  
  public static final ColorType TEXT_FOREGROUND = new ColorType("TextForeground");
  
  public static final ColorType TEXT_BACKGROUND = new ColorType("TextBackground");
  
  public static final ColorType FOCUS = new ColorType("Focus");
  
  public static final int MAX_COUNT = Math.max(FOREGROUND.getID(), Math.max(BACKGROUND.getID(), FOCUS.getID())) + 1;
  
  private static int nextID;
  
  private String description;
  
  private int index;
  
  protected ColorType(String paramString) {
    if (paramString == null)
      throw new NullPointerException("ColorType must have a valid description"); 
    this.description = paramString;
    synchronized (ColorType.class) {
      this.index = nextID++;
    } 
  }
  
  public final int getID() { return this.index; }
  
  public String toString() { return this.description; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\ColorType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */