package javax.swing.plaf.synth;

import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComboBox;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;

class SynthComboPopup extends BasicComboPopup {
  public SynthComboPopup(JComboBox paramJComboBox) { super(paramJComboBox); }
  
  protected void configureList() {
    this.list.setFont(this.comboBox.getFont());
    this.list.setCellRenderer(this.comboBox.getRenderer());
    this.list.setFocusable(false);
    this.list.setSelectionMode(0);
    int i = this.comboBox.getSelectedIndex();
    if (i == -1) {
      this.list.clearSelection();
    } else {
      this.list.setSelectedIndex(i);
      this.list.ensureIndexIsVisible(i);
    } 
    installListListeners();
  }
  
  protected Rectangle computePopupBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    ComboBoxUI comboBoxUI = this.comboBox.getUI();
    if (comboBoxUI instanceof SynthComboBoxUI) {
      SynthComboBoxUI synthComboBoxUI = (SynthComboBoxUI)comboBoxUI;
      if (synthComboBoxUI.popupInsets != null) {
        Insets insets = synthComboBoxUI.popupInsets;
        return super.computePopupBounds(paramInt1 + insets.left, paramInt2 + insets.top, paramInt3 - insets.left - insets.right, paramInt4 - insets.top - insets.bottom);
      } 
    } 
    return super.computePopupBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthComboPopup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */