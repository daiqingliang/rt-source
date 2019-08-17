package javax.swing.text.html;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class EditableView extends ComponentView {
  private boolean isVisible;
  
  EditableView(Element paramElement) { super(paramElement); }
  
  public float getMinimumSpan(int paramInt) { return this.isVisible ? super.getMinimumSpan(paramInt) : 0.0F; }
  
  public float getPreferredSpan(int paramInt) { return this.isVisible ? super.getPreferredSpan(paramInt) : 0.0F; }
  
  public float getMaximumSpan(int paramInt) { return this.isVisible ? super.getMaximumSpan(paramInt) : 0.0F; }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Component component = getComponent();
    Container container = getContainer();
    if (container instanceof JTextComponent && this.isVisible != ((JTextComponent)container).isEditable()) {
      this.isVisible = ((JTextComponent)container).isEditable();
      preferenceChanged(null, true, true);
      container.repaint();
    } 
    if (this.isVisible) {
      super.paint(paramGraphics, paramShape);
    } else {
      setSize(0.0F, 0.0F);
    } 
    if (component != null)
      component.setFocusable(this.isVisible); 
  }
  
  public void setParent(View paramView) {
    if (paramView != null) {
      Container container = paramView.getContainer();
      if (container != null)
        if (container instanceof JTextComponent) {
          this.isVisible = ((JTextComponent)container).isEditable();
        } else {
          this.isVisible = false;
        }  
    } 
    super.setParent(paramView);
  }
  
  public boolean isVisible() { return this.isVisible; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\EditableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */