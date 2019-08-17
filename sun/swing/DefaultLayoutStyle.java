package sun.swing;

import java.awt.Container;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class DefaultLayoutStyle extends LayoutStyle {
  private static final DefaultLayoutStyle INSTANCE = new DefaultLayoutStyle();
  
  public static LayoutStyle getInstance() { return INSTANCE; }
  
  public int getPreferredGap(JComponent paramJComponent1, JComponent paramJComponent2, LayoutStyle.ComponentPlacement paramComponentPlacement, int paramInt, Container paramContainer) {
    if (paramJComponent1 == null || paramJComponent2 == null || paramComponentPlacement == null)
      throw new NullPointerException(); 
    checkPosition(paramInt);
    if (paramComponentPlacement == LayoutStyle.ComponentPlacement.INDENT && (paramInt == 3 || paramInt == 7)) {
      int i = getIndent(paramJComponent1, paramInt);
      if (i > 0)
        return i; 
    } 
    return (paramComponentPlacement == LayoutStyle.ComponentPlacement.UNRELATED) ? 12 : 6;
  }
  
  public int getContainerGap(JComponent paramJComponent, int paramInt, Container paramContainer) {
    if (paramJComponent == null)
      throw new NullPointerException(); 
    checkPosition(paramInt);
    return 6;
  }
  
  protected boolean isLabelAndNonlabel(JComponent paramJComponent1, JComponent paramJComponent2, int paramInt) {
    if (paramInt == 3 || paramInt == 7) {
      boolean bool1 = paramJComponent1 instanceof javax.swing.JLabel;
      boolean bool2 = paramJComponent2 instanceof javax.swing.JLabel;
      return ((bool1 || bool2) && bool1 != bool2);
    } 
    return false;
  }
  
  protected int getButtonGap(JComponent paramJComponent1, JComponent paramJComponent2, int paramInt1, int paramInt2) {
    paramInt2 -= getButtonGap(paramJComponent1, paramInt1);
    if (paramInt2 > 0)
      paramInt2 -= getButtonGap(paramJComponent2, flipDirection(paramInt1)); 
    return (paramInt2 < 0) ? 0 : paramInt2;
  }
  
  protected int getButtonGap(JComponent paramJComponent, int paramInt1, int paramInt2) {
    paramInt2 -= getButtonGap(paramJComponent, paramInt1);
    return Math.max(paramInt2, 0);
  }
  
  public int getButtonGap(JComponent paramJComponent, int paramInt) {
    String str = paramJComponent.getUIClassID();
    if ((str == "CheckBoxUI" || str == "RadioButtonUI") && !((AbstractButton)paramJComponent).isBorderPainted()) {
      Border border = paramJComponent.getBorder();
      if (border instanceof javax.swing.plaf.UIResource)
        return getInset(paramJComponent, paramInt); 
    } 
    return 0;
  }
  
  private void checkPosition(int paramInt) {
    if (paramInt != 1 && paramInt != 5 && paramInt != 7 && paramInt != 3)
      throw new IllegalArgumentException(); 
  }
  
  protected int flipDirection(int paramInt) {
    switch (paramInt) {
      case 1:
        return 5;
      case 5:
        return 1;
      case 3:
        return 7;
      case 7:
        return 3;
    } 
    assert false;
    return 0;
  }
  
  protected int getIndent(JComponent paramJComponent, int paramInt) {
    String str = paramJComponent.getUIClassID();
    if (str == "CheckBoxUI" || str == "RadioButtonUI") {
      AbstractButton abstractButton = (AbstractButton)paramJComponent;
      Insets insets = paramJComponent.getInsets();
      Icon icon = getIcon(abstractButton);
      int i = abstractButton.getIconTextGap();
      if (isLeftAligned(abstractButton, paramInt))
        return insets.left + icon.getIconWidth() + i; 
      if (isRightAligned(abstractButton, paramInt))
        return insets.right + icon.getIconWidth() + i; 
    } 
    return 0;
  }
  
  private Icon getIcon(AbstractButton paramAbstractButton) {
    Icon icon = paramAbstractButton.getIcon();
    if (icon != null)
      return icon; 
    String str = null;
    if (paramAbstractButton instanceof javax.swing.JCheckBox) {
      str = "CheckBox.icon";
    } else if (paramAbstractButton instanceof javax.swing.JRadioButton) {
      str = "RadioButton.icon";
    } 
    if (str != null) {
      Object object = UIManager.get(str);
      if (object instanceof Icon)
        return (Icon)object; 
    } 
    return null;
  }
  
  private boolean isLeftAligned(AbstractButton paramAbstractButton, int paramInt) {
    if (paramInt == 7) {
      boolean bool = paramAbstractButton.getComponentOrientation().isLeftToRight();
      int i = paramAbstractButton.getHorizontalAlignment();
      return ((bool && (i == 2 || i == 10)) || (!bool && i == 11));
    } 
    return false;
  }
  
  private boolean isRightAligned(AbstractButton paramAbstractButton, int paramInt) {
    if (paramInt == 3) {
      boolean bool = paramAbstractButton.getComponentOrientation().isLeftToRight();
      int i = paramAbstractButton.getHorizontalAlignment();
      return ((bool && (i == 4 || i == 11)) || (!bool && i == 10));
    } 
    return false;
  }
  
  private int getInset(JComponent paramJComponent, int paramInt) { return getInset(paramJComponent.getInsets(), paramInt); }
  
  private int getInset(Insets paramInsets, int paramInt) {
    if (paramInsets == null)
      return 0; 
    switch (paramInt) {
      case 1:
        return paramInsets.top;
      case 5:
        return paramInsets.bottom;
      case 3:
        return paramInsets.right;
      case 7:
        return paramInsets.left;
    } 
    assert false;
    return 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\DefaultLayoutStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */