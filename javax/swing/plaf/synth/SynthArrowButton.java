package javax.swing.plaf.synth;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.UIResource;

class SynthArrowButton extends JButton implements SwingConstants, UIResource {
  private int direction;
  
  public SynthArrowButton(int paramInt) {
    super.setFocusable(false);
    setDirection(paramInt);
    setDefaultCapable(false);
  }
  
  public String getUIClassID() { return "ArrowButtonUI"; }
  
  public void updateUI() { setUI(new SynthArrowButtonUI(null)); }
  
  public void setDirection(int paramInt) {
    this.direction = paramInt;
    putClientProperty("__arrow_direction__", Integer.valueOf(paramInt));
    repaint();
  }
  
  public int getDirection() { return this.direction; }
  
  public void setFocusable(boolean paramBoolean) {}
  
  private static class SynthArrowButtonUI extends SynthButtonUI {
    private SynthArrowButtonUI() {}
    
    protected void installDefaults(AbstractButton param1AbstractButton) {
      super.installDefaults(param1AbstractButton);
      updateStyle(param1AbstractButton);
    }
    
    protected void paint(SynthContext param1SynthContext, Graphics param1Graphics) {
      SynthArrowButton synthArrowButton = (SynthArrowButton)param1SynthContext.getComponent();
      param1SynthContext.getPainter().paintArrowButtonForeground(param1SynthContext, param1Graphics, 0, 0, synthArrowButton.getWidth(), synthArrowButton.getHeight(), synthArrowButton.getDirection());
    }
    
    void paintBackground(SynthContext param1SynthContext, Graphics param1Graphics, JComponent param1JComponent) { param1SynthContext.getPainter().paintArrowButtonBackground(param1SynthContext, param1Graphics, 0, 0, param1JComponent.getWidth(), param1JComponent.getHeight()); }
    
    public void paintBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { param1SynthContext.getPainter().paintArrowButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public Dimension getMinimumSize() { return new Dimension(5, 5); }
    
    public Dimension getMaximumSize() { return new Dimension(2147483647, 2147483647); }
    
    public Dimension getPreferredSize(JComponent param1JComponent) {
      SynthContext synthContext = getContext(param1JComponent);
      Dimension dimension = null;
      if (synthContext.getComponent().getName() == "ScrollBar.button")
        dimension = (Dimension)synthContext.getStyle().get(synthContext, "ScrollBar.buttonSize"); 
      if (dimension == null) {
        int i = synthContext.getStyle().getInt(synthContext, "ArrowButton.size", 16);
        dimension = new Dimension(i, i);
      } 
      Container container = synthContext.getComponent().getParent();
      if (container instanceof JComponent && !(container instanceof javax.swing.JComboBox)) {
        Object object = ((JComponent)container).getClientProperty("JComponent.sizeVariant");
        if (object != null)
          if ("large".equals(object)) {
            dimension = new Dimension((int)(dimension.width * 1.15D), (int)(dimension.height * 1.15D));
          } else if ("small".equals(object)) {
            dimension = new Dimension((int)(dimension.width * 0.857D), (int)(dimension.height * 0.857D));
          } else if ("mini".equals(object)) {
            dimension = new Dimension((int)(dimension.width * 0.714D), (int)(dimension.height * 0.714D));
          }  
      } 
      synthContext.dispose();
      return dimension;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthArrowButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */