package sun.print;

import java.awt.Frame;
import javax.print.attribute.PrintRequestAttribute;

public final class DialogOwner implements PrintRequestAttribute {
  private Frame dlgOwner;
  
  public DialogOwner(Frame paramFrame) { this.dlgOwner = paramFrame; }
  
  public Frame getOwner() { return this.dlgOwner; }
  
  public final Class getCategory() { return DialogOwner.class; }
  
  public final String getName() { return "dialog-owner"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\DialogOwner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */