package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.UIManager;

class RecentSwatchPanel extends SwatchPanel {
  protected void initValues() {
    this.swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize", getLocale());
    this.numSwatches = new Dimension(5, 7);
    this.gap = new Dimension(1, 1);
  }
  
  protected void initColors() {
    Color color = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor", getLocale());
    int i = this.numSwatches.width * this.numSwatches.height;
    this.colors = new Color[i];
    for (byte b = 0; b < i; b++)
      this.colors[b] = color; 
  }
  
  public void setMostRecentColor(Color paramColor) {
    System.arraycopy(this.colors, 0, this.colors, 1, this.colors.length - 1);
    this.colors[0] = paramColor;
    repaint();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\RecentSwatchPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */