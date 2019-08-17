package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.swing.SwingUtilities2;

class DefaultPreviewPanel extends JPanel {
  private int squareSize = 25;
  
  private int squareGap = 5;
  
  private int innerGap = 5;
  
  private int textGap = 5;
  
  private Font font = new Font("Dialog", 0, 12);
  
  private String sampleText;
  
  private int swatchWidth = 50;
  
  private Color oldColor = null;
  
  private JColorChooser getColorChooser() { return (JColorChooser)SwingUtilities.getAncestorOfClass(JColorChooser.class, this); }
  
  public Dimension getPreferredSize() {
    DefaultPreviewPanel defaultPreviewPanel = getColorChooser();
    if (defaultPreviewPanel == null)
      defaultPreviewPanel = this; 
    FontMetrics fontMetrics = defaultPreviewPanel.getFontMetrics(getFont());
    int i = fontMetrics.getAscent();
    int j = fontMetrics.getHeight();
    int k = SwingUtilities2.stringWidth(defaultPreviewPanel, fontMetrics, getSampleText());
    int m = j * 3 + this.textGap * 3;
    int n = this.squareSize * 3 + this.squareGap * 2 + this.swatchWidth + k + this.textGap * 3;
    return new Dimension(n, m);
  }
  
  public void paintComponent(Graphics paramGraphics) {
    if (this.oldColor == null)
      this.oldColor = getForeground(); 
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
    if (getComponentOrientation().isLeftToRight()) {
      int i = paintSquares(paramGraphics, 0);
      int j = paintText(paramGraphics, i);
      paintSwatch(paramGraphics, i + j);
    } else {
      int i = paintSwatch(paramGraphics, 0);
      int j = paintText(paramGraphics, i);
      paintSquares(paramGraphics, i + j);
    } 
  }
  
  private int paintSwatch(Graphics paramGraphics, int paramInt) {
    int i = paramInt;
    paramGraphics.setColor(this.oldColor);
    paramGraphics.fillRect(i, 0, this.swatchWidth, this.squareSize + this.squareGap / 2);
    paramGraphics.setColor(getForeground());
    paramGraphics.fillRect(i, this.squareSize + this.squareGap / 2, this.swatchWidth, this.squareSize + this.squareGap / 2);
    return i + this.swatchWidth;
  }
  
  private int paintText(Graphics paramGraphics, int paramInt) {
    paramGraphics.setFont(getFont());
    DefaultPreviewPanel defaultPreviewPanel = getColorChooser();
    if (defaultPreviewPanel == null)
      defaultPreviewPanel = this; 
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(defaultPreviewPanel, paramGraphics);
    int i = fontMetrics.getAscent();
    int j = fontMetrics.getHeight();
    int k = SwingUtilities2.stringWidth(defaultPreviewPanel, fontMetrics, getSampleText());
    int m = paramInt + this.textGap;
    Color color = getForeground();
    paramGraphics.setColor(color);
    SwingUtilities2.drawString(defaultPreviewPanel, paramGraphics, getSampleText(), m + this.textGap / 2, i + 2);
    paramGraphics.fillRect(m, j + this.textGap, k + this.textGap, j + 2);
    paramGraphics.setColor(Color.black);
    SwingUtilities2.drawString(defaultPreviewPanel, paramGraphics, getSampleText(), m + this.textGap / 2, j + i + this.textGap + 2);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(m, (j + this.textGap) * 2, k + this.textGap, j + 2);
    paramGraphics.setColor(color);
    SwingUtilities2.drawString(defaultPreviewPanel, paramGraphics, getSampleText(), m + this.textGap / 2, (j + this.textGap) * 2 + i + 2);
    return k + this.textGap * 3;
  }
  
  private int paintSquares(Graphics paramGraphics, int paramInt) {
    int i = paramInt;
    Color color = getForeground();
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i, 0, this.squareSize, this.squareSize);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(i + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(i, this.squareSize + this.squareGap, this.squareSize, this.squareSize);
    paramGraphics.translate(this.squareSize + this.squareGap, 0);
    paramGraphics.setColor(Color.black);
    paramGraphics.fillRect(i, 0, this.squareSize, this.squareSize);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(i + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
    paramGraphics.translate(-(this.squareSize + this.squareGap), 0);
    paramGraphics.translate(this.squareSize + this.squareGap, this.squareSize + this.squareGap);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i, 0, this.squareSize, this.squareSize);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(i + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
    paramGraphics.translate(-(this.squareSize + this.squareGap), -(this.squareSize + this.squareGap));
    paramGraphics.translate((this.squareSize + this.squareGap) * 2, 0);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i, 0, this.squareSize, this.squareSize);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(i + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
    paramGraphics.setColor(Color.black);
    paramGraphics.fillRect(i + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
    paramGraphics.translate(-((this.squareSize + this.squareGap) * 2), 0);
    paramGraphics.translate((this.squareSize + this.squareGap) * 2, this.squareSize + this.squareGap);
    paramGraphics.setColor(Color.black);
    paramGraphics.fillRect(i, 0, this.squareSize, this.squareSize);
    paramGraphics.setColor(color);
    paramGraphics.fillRect(i + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
    paramGraphics.translate(-((this.squareSize + this.squareGap) * 2), -(this.squareSize + this.squareGap));
    return this.squareSize * 3 + this.squareGap * 2;
  }
  
  private String getSampleText() {
    if (this.sampleText == null)
      this.sampleText = UIManager.getString("ColorChooser.sampleText", getLocale()); 
    return this.sampleText;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\DefaultPreviewPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */