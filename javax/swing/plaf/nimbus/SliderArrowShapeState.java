package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class SliderArrowShapeState extends State {
  SliderArrowShapeState() { super("ArrowShape"); }
  
  protected boolean isInState(JComponent paramJComponent) { return (paramJComponent.getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SliderArrowShapeState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */