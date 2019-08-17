package javax.sound.sampled;

import java.util.EventObject;

public class LineEvent extends EventObject {
  private final Type type;
  
  private final long position;
  
  public LineEvent(Line paramLine, Type paramType, long paramLong) {
    super(paramLine);
    this.type = paramType;
    this.position = paramLong;
  }
  
  public final Line getLine() { return (Line)getSource(); }
  
  public final Type getType() { return this.type; }
  
  public final long getFramePosition() { return this.position; }
  
  public String toString() {
    String str2;
    String str1 = "";
    if (this.type != null)
      str1 = this.type.toString() + " "; 
    if (getLine() == null) {
      str2 = "null";
    } else {
      str2 = getLine().toString();
    } 
    return new String(str1 + "event from line " + str2);
  }
  
  public static class Type {
    private String name;
    
    public static final Type OPEN = new Type("Open");
    
    public static final Type CLOSE = new Type("Close");
    
    public static final Type START = new Type("Start");
    
    public static final Type STOP = new Type("Stop");
    
    protected Type(String param1String) { this.name = param1String; }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\LineEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */