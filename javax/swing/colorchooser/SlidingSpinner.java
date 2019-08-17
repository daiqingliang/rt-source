package javax.swing.colorchooser;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

final class SlidingSpinner implements ChangeListener {
  private final ColorPanel panel;
  
  private final JComponent label;
  
  private final SpinnerNumberModel model = new SpinnerNumberModel();
  
  private final JSlider slider = new JSlider();
  
  private final JSpinner spinner = new JSpinner(this.model);
  
  private float value;
  
  private boolean internal;
  
  SlidingSpinner(ColorPanel paramColorPanel, JComponent paramJComponent) {
    this.panel = paramColorPanel;
    this.label = paramJComponent;
    this.slider.addChangeListener(this);
    this.spinner.addChangeListener(this);
    JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)this.spinner.getEditor();
    ValueFormatter.init(3, false, defaultEditor.getTextField());
    defaultEditor.setFocusable(false);
    this.spinner.setFocusable(false);
  }
  
  JComponent getLabel() { return this.label; }
  
  JSlider getSlider() { return this.slider; }
  
  JSpinner getSpinner() { return this.spinner; }
  
  float getValue() { return this.value; }
  
  void setValue(float paramFloat) {
    int i = this.slider.getMinimum();
    int j = this.slider.getMaximum();
    this.internal = true;
    this.slider.setValue(i + (int)(paramFloat * (j - i)));
    this.spinner.setValue(Integer.valueOf(this.slider.getValue()));
    this.internal = false;
    this.value = paramFloat;
  }
  
  void setRange(int paramInt1, int paramInt2) {
    this.internal = true;
    this.slider.setMinimum(paramInt1);
    this.slider.setMaximum(paramInt2);
    this.model.setMinimum(Integer.valueOf(paramInt1));
    this.model.setMaximum(Integer.valueOf(paramInt2));
    this.internal = false;
  }
  
  void setVisible(boolean paramBoolean) {
    this.label.setVisible(paramBoolean);
    this.slider.setVisible(paramBoolean);
    this.spinner.setVisible(paramBoolean);
  }
  
  public void stateChanged(ChangeEvent paramChangeEvent) {
    if (!this.internal) {
      if (this.spinner == paramChangeEvent.getSource()) {
        Object object = this.spinner.getValue();
        if (object instanceof Integer) {
          this.internal = true;
          this.slider.setValue(((Integer)object).intValue());
          this.internal = false;
        } 
      } 
      int i = this.slider.getValue();
      this.internal = true;
      this.spinner.setValue(Integer.valueOf(i));
      this.internal = false;
      int j = this.slider.getMinimum();
      int k = this.slider.getMaximum();
      this.value = (i - j) / (k - j);
      this.panel.colorChanged();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\SlidingSpinner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */