package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class NoFramesView extends BlockView {
  boolean visible = false;
  
  public NoFramesView(Element paramElement, int paramInt) { super(paramElement, paramInt); }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Container container = getContainer();
    if (container != null && this.visible != ((JTextComponent)container).isEditable())
      this.visible = ((JTextComponent)container).isEditable(); 
    if (!isVisible())
      return; 
    super.paint(paramGraphics, paramShape);
  }
  
  public void setParent(View paramView) {
    if (paramView != null) {
      Container container = paramView.getContainer();
      if (container != null)
        this.visible = ((JTextComponent)container).isEditable(); 
    } 
    super.setParent(paramView);
  }
  
  public boolean isVisible() { return this.visible; }
  
  protected void layout(int paramInt1, int paramInt2) {
    if (!isVisible())
      return; 
    super.layout(paramInt1, paramInt2);
  }
  
  public float getPreferredSpan(int paramInt) { return !this.visible ? 0.0F : super.getPreferredSpan(paramInt); }
  
  public float getMinimumSpan(int paramInt) { return !this.visible ? 0.0F : super.getMinimumSpan(paramInt); }
  
  public float getMaximumSpan(int paramInt) { return !this.visible ? 0.0F : super.getMaximumSpan(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\NoFramesView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */