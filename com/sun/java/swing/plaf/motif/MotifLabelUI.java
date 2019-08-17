package com.sun.java.swing.plaf.motif;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;

public class MotifLabelUI extends BasicLabelUI {
  private static final Object MOTIF_LABEL_UI_KEY = new Object();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MotifLabelUI motifLabelUI = (MotifLabelUI)appContext.get(MOTIF_LABEL_UI_KEY);
    if (motifLabelUI == null) {
      motifLabelUI = new MotifLabelUI();
      appContext.put(MOTIF_LABEL_UI_KEY, motifLabelUI);
    } 
    return motifLabelUI;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */