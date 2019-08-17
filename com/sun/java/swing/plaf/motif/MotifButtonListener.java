package com.sun.java.swing.plaf.motif;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

public class MotifButtonListener extends BasicButtonListener {
  public MotifButtonListener(AbstractButton paramAbstractButton) { super(paramAbstractButton); }
  
  protected void checkOpacity(AbstractButton paramAbstractButton) { paramAbstractButton.setOpaque(false); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */