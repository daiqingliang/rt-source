package javax.swing;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Hashtable;

class DebugGraphicsInfo {
  Color flashColor = Color.red;
  
  int flashTime = 100;
  
  int flashCount = 2;
  
  Hashtable<JComponent, Integer> componentToDebug;
  
  JFrame debugFrame = null;
  
  PrintStream stream = System.out;
  
  void setDebugOptions(JComponent paramJComponent, int paramInt) {
    if (paramInt == 0)
      return; 
    if (this.componentToDebug == null)
      this.componentToDebug = new Hashtable(); 
    if (paramInt > 0) {
      this.componentToDebug.put(paramJComponent, Integer.valueOf(paramInt));
    } else {
      this.componentToDebug.remove(paramJComponent);
    } 
  }
  
  int getDebugOptions(JComponent paramJComponent) {
    if (this.componentToDebug == null)
      return 0; 
    Integer integer = (Integer)this.componentToDebug.get(paramJComponent);
    return (integer == null) ? 0 : integer.intValue();
  }
  
  void log(String paramString) { this.stream.println(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DebugGraphicsInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */