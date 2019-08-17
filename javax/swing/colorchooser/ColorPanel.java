package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Container;
import java.awt.ContainerOrderFocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

final class ColorPanel extends JPanel implements ActionListener {
  private final SlidingSpinner[] spinners = new SlidingSpinner[5];
  
  private final float[] values = new float[this.spinners.length];
  
  private final ColorModel model;
  
  private Color color;
  
  private int x = 1;
  
  private int y = 2;
  
  private int z;
  
  ColorPanel(ColorModel paramColorModel) {
    super(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridx = 1;
    ButtonGroup buttonGroup = new ButtonGroup();
    EmptyBorder emptyBorder = null;
    for (byte b = 0; b < this.spinners.length; b++) {
      if (b < 3) {
        JRadioButton jRadioButton = new JRadioButton();
        if (!b) {
          Insets insets = jRadioButton.getInsets();
          insets.left = (jRadioButton.getPreferredSize()).width;
          emptyBorder = new EmptyBorder(insets);
          jRadioButton.setSelected(true);
          gridBagConstraints.insets.top = 5;
        } 
        add(jRadioButton, gridBagConstraints);
        buttonGroup.add(jRadioButton);
        jRadioButton.setActionCommand(Integer.toString(b));
        jRadioButton.addActionListener(this);
        this.spinners[b] = new SlidingSpinner(this, jRadioButton);
      } else {
        JLabel jLabel = new JLabel();
        add(jLabel, gridBagConstraints);
        jLabel.setBorder(emptyBorder);
        jLabel.setFocusable(false);
        this.spinners[b] = new SlidingSpinner(this, jLabel);
      } 
    } 
    gridBagConstraints.gridx = 2;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.insets.top = 0;
    gridBagConstraints.insets.left = 5;
    for (SlidingSpinner slidingSpinner : this.spinners) {
      add(slidingSpinner.getSlider(), gridBagConstraints);
      gridBagConstraints.insets.top = 5;
    } 
    gridBagConstraints.gridx = 3;
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.insets.top = 0;
    for (SlidingSpinner slidingSpinner : this.spinners) {
      add(slidingSpinner.getSpinner(), gridBagConstraints);
      gridBagConstraints.insets.top = 5;
    } 
    setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
    setFocusTraversalPolicyProvider(true);
    setFocusable(false);
    this.model = paramColorModel;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    try {
      this.z = Integer.parseInt(paramActionEvent.getActionCommand());
      this.y = (this.z != 2) ? 2 : 1;
      this.x = (this.z != 0) ? 0 : 1;
      getParent().repaint();
    } catch (NumberFormatException numberFormatException) {}
  }
  
  void buildPanel() {
    int i = this.model.getCount();
    this.spinners[4].setVisible((i > 4));
    for (byte b = 0; b < i; b++) {
      String str = this.model.getLabel(this, b);
      JComponent jComponent = this.spinners[b].getLabel();
      if (jComponent instanceof JRadioButton) {
        JRadioButton jRadioButton = (JRadioButton)jComponent;
        jRadioButton.setText(str);
        jRadioButton.getAccessibleContext().setAccessibleDescription(str);
      } else if (jComponent instanceof JLabel) {
        JLabel jLabel = (JLabel)jComponent;
        jLabel.setText(str);
      } 
      this.spinners[b].setRange(this.model.getMinimum(b), this.model.getMaximum(b));
      this.spinners[b].setValue(this.values[b]);
      this.spinners[b].getSlider().getAccessibleContext().setAccessibleName(str);
      this.spinners[b].getSpinner().getAccessibleContext().setAccessibleName(str);
      JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)this.spinners[b].getSpinner().getEditor();
      defaultEditor.getTextField().getAccessibleContext().setAccessibleName(str);
      this.spinners[b].getSlider().getAccessibleContext().setAccessibleDescription(str);
      this.spinners[b].getSpinner().getAccessibleContext().setAccessibleDescription(str);
      defaultEditor.getTextField().getAccessibleContext().setAccessibleDescription(str);
    } 
  }
  
  void colorChanged() {
    this.color = new Color(getColor(0), true);
    Container container = getParent();
    if (container instanceof ColorChooserPanel) {
      ColorChooserPanel colorChooserPanel = (ColorChooserPanel)container;
      colorChooserPanel.setSelectedColor(this.color);
      colorChooserPanel.repaint();
    } 
  }
  
  float getValueX() { return this.spinners[this.x].getValue(); }
  
  float getValueY() { return 1.0F - this.spinners[this.y].getValue(); }
  
  float getValueZ() { return 1.0F - this.spinners[this.z].getValue(); }
  
  void setValue(float paramFloat) {
    this.spinners[this.z].setValue(1.0F - paramFloat);
    colorChanged();
  }
  
  void setValue(float paramFloat1, float paramFloat2) {
    this.spinners[this.x].setValue(paramFloat1);
    this.spinners[this.y].setValue(1.0F - paramFloat2);
    colorChanged();
  }
  
  int getColor(float paramFloat) {
    setDefaultValue(this.x);
    setDefaultValue(this.y);
    this.values[this.z] = 1.0F - paramFloat;
    return getColor(3);
  }
  
  int getColor(float paramFloat1, float paramFloat2) {
    this.values[this.x] = paramFloat1;
    this.values[this.y] = 1.0F - paramFloat2;
    setValue(this.z);
    return getColor(3);
  }
  
  void setColor(Color paramColor) {
    if (!paramColor.equals(this.color)) {
      this.color = paramColor;
      this.model.setColor(paramColor.getRGB(), this.values);
      for (byte b = 0; b < this.model.getCount(); b++)
        this.spinners[b].setValue(this.values[b]); 
    } 
  }
  
  private int getColor(int paramInt) {
    while (paramInt < this.model.getCount())
      setValue(paramInt++); 
    return this.model.getColor(this.values);
  }
  
  private void setValue(int paramInt) { this.values[paramInt] = this.spinners[paramInt].getValue(); }
  
  private void setDefaultValue(int paramInt) {
    float f = this.model.getDefault(paramInt);
    this.values[paramInt] = (f < 0.0F) ? this.spinners[paramInt].getValue() : f;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */