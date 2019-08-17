package javax.swing.plaf.nimbus;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

public abstract class State<T extends JComponent> extends Object {
  static final Map<String, StandardState> standardStates = new HashMap(7);
  
  static final State Enabled = new StandardState(1, null);
  
  static final State MouseOver = new StandardState(2, null);
  
  static final State Pressed = new StandardState(4, null);
  
  static final State Disabled = new StandardState(8, null);
  
  static final State Focused = new StandardState(256, null);
  
  static final State Selected = new StandardState(512, null);
  
  static final State Default = new StandardState(1024, null);
  
  private String name;
  
  protected State(String paramString) { this.name = paramString; }
  
  public String toString() { return this.name; }
  
  boolean isInState(T paramT, int paramInt) { return isInState(paramT); }
  
  protected abstract boolean isInState(T paramT);
  
  String getName() { return this.name; }
  
  static boolean isStandardStateName(String paramString) { return standardStates.containsKey(paramString); }
  
  static StandardState getStandardState(String paramString) { return (StandardState)standardStates.get(paramString); }
  
  static final class StandardState extends State<JComponent> {
    private int state;
    
    private StandardState(int param1Int) {
      super(toString(param1Int));
      this.state = param1Int;
      standardStates.put(getName(), this);
    }
    
    public int getState() { return this.state; }
    
    boolean isInState(JComponent param1JComponent, int param1Int) { return ((param1Int & this.state) == this.state); }
    
    protected boolean isInState(JComponent param1JComponent) { throw new AssertionError("This method should never be called"); }
    
    private static String toString(int param1Int) {
      StringBuffer stringBuffer = new StringBuffer();
      if ((param1Int & 0x400) == 1024)
        stringBuffer.append("Default"); 
      if ((param1Int & 0x8) == 8) {
        if (stringBuffer.length() > 0)
          stringBuffer.append("+"); 
        stringBuffer.append("Disabled");
      } 
      if ((param1Int & true) == 1) {
        if (stringBuffer.length() > 0)
          stringBuffer.append("+"); 
        stringBuffer.append("Enabled");
      } 
      if ((param1Int & 0x100) == 256) {
        if (stringBuffer.length() > 0)
          stringBuffer.append("+"); 
        stringBuffer.append("Focused");
      } 
      if ((param1Int & 0x2) == 2) {
        if (stringBuffer.length() > 0)
          stringBuffer.append("+"); 
        stringBuffer.append("MouseOver");
      } 
      if ((param1Int & 0x4) == 4) {
        if (stringBuffer.length() > 0)
          stringBuffer.append("+"); 
        stringBuffer.append("Pressed");
      } 
      if ((param1Int & 0x200) == 512) {
        if (stringBuffer.length() > 0)
          stringBuffer.append("+"); 
        stringBuffer.append("Selected");
      } 
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\State.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */