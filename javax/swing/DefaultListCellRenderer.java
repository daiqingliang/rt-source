package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class DefaultListCellRenderer extends JLabel implements ListCellRenderer<Object>, Serializable {
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
  
  public DefaultListCellRenderer() {
    setOpaque(true);
    setBorder(getNoFocusBorder());
    setName("List.cellRenderer");
  }
  
  private Border getNoFocusBorder() {
    Border border = DefaultLookup.getBorder(this, this.ui, "List.cellNoFocusBorder");
    return (System.getSecurityManager() != null) ? ((border != null) ? border : SAFE_NO_FOCUS_BORDER) : ((border != null && (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER)) ? border : noFocusBorder);
  }
  
  public Component getListCellRendererComponent(JList<?> paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    setComponentOrientation(paramJList.getComponentOrientation());
    Color color1 = null;
    Color color2 = null;
    JList.DropLocation dropLocation = paramJList.getDropLocation();
    if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == paramInt) {
      color1 = DefaultLookup.getColor(this, this.ui, "List.dropCellBackground");
      color2 = DefaultLookup.getColor(this, this.ui, "List.dropCellForeground");
      paramBoolean1 = true;
    } 
    if (paramBoolean1) {
      setBackground((color1 == null) ? paramJList.getSelectionBackground() : color1);
      setForeground((color2 == null) ? paramJList.getSelectionForeground() : color2);
    } else {
      setBackground(paramJList.getBackground());
      setForeground(paramJList.getForeground());
    } 
    if (paramObject instanceof Icon) {
      setIcon((Icon)paramObject);
      setText("");
    } else {
      setIcon(null);
      setText((paramObject == null) ? "" : paramObject.toString());
    } 
    setEnabled(paramJList.isEnabled());
    setFont(paramJList.getFont());
    Border border = null;
    if (paramBoolean2) {
      if (paramBoolean1)
        border = DefaultLookup.getBorder(this, this.ui, "List.focusSelectedCellHighlightBorder"); 
      if (border == null)
        border = DefaultLookup.getBorder(this, this.ui, "List.focusCellHighlightBorder"); 
    } else {
      border = getNoFocusBorder();
    } 
    setBorder(border);
    return this;
  }
  
  public boolean isOpaque() {
    Color color = getBackground();
    Container container = getParent();
    if (container != null)
      container = container.getParent(); 
    boolean bool = (color != null && container != null && color.equals(container.getBackground()) && container.isOpaque()) ? 1 : 0;
    return (!bool && super.isOpaque());
  }
  
  public void validate() {}
  
  public void invalidate() {}
  
  public void repaint() {}
  
  public void revalidate() {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void repaint(Rectangle paramRectangle) {}
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramString == "text" || ((paramString == "font" || paramString == "foreground") && paramObject1 != paramObject2 && getClientProperty("html") != null))
      super.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  public void firePropertyChange(String paramString, byte paramByte1, byte paramByte2) {}
  
  public void firePropertyChange(String paramString, char paramChar1, char paramChar2) {}
  
  public void firePropertyChange(String paramString, short paramShort1, short paramShort2) {}
  
  public void firePropertyChange(String paramString, int paramInt1, int paramInt2) {}
  
  public void firePropertyChange(String paramString, long paramLong1, long paramLong2) {}
  
  public void firePropertyChange(String paramString, float paramFloat1, float paramFloat2) {}
  
  public void firePropertyChange(String paramString, double paramDouble1, double paramDouble2) {}
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {}
  
  public static class UIResource extends DefaultListCellRenderer implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultListCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */