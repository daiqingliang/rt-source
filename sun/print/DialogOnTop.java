package sun.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;

public class DialogOnTop implements PrintRequestAttribute {
  private static final long serialVersionUID = -1901909867156076547L;
  
  long id;
  
  public DialogOnTop() {}
  
  public DialogOnTop(long paramLong) { this.id = paramLong; }
  
  public final Class<? extends Attribute> getCategory() { return DialogOnTop.class; }
  
  public long getID() { return this.id; }
  
  public final String getName() { return "dialog-on-top"; }
  
  public String toString() { return "dialog-on-top"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\DialogOnTop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */