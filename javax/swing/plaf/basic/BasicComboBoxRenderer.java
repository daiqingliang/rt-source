package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

public class BasicComboBoxRenderer extends JLabel implements ListCellRenderer, Serializable {
  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  public BasicComboBoxRenderer() {
    setOpaque(true);
    setBorder(getNoFocusBorder());
  }
  
  private static Border getNoFocusBorder() { return (System.getSecurityManager() != null) ? SAFE_NO_FOCUS_BORDER : noFocusBorder; }
  
  public Dimension getPreferredSize() {
    Dimension dimension;
    if (getText() == null || getText().equals("")) {
      setText(" ");
      dimension = super.getPreferredSize();
      setText("");
    } else {
      dimension = super.getPreferredSize();
    } 
    return dimension;
  }
  
  public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1) {
      setBackground(paramJList.getSelectionBackground());
      setForeground(paramJList.getSelectionForeground());
    } else {
      setBackground(paramJList.getBackground());
      setForeground(paramJList.getForeground());
    } 
    setFont(paramJList.getFont());
    if (paramObject instanceof Icon) {
      setIcon((Icon)paramObject);
    } else {
      setText((paramObject == null) ? "" : paramObject.toString());
    } 
    return this;
  }
  
  public static class UIResource extends BasicComboBoxRenderer implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicComboBoxRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */