package javax.swing.plaf.nimbus;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

class TableScrollPaneCorner extends JComponent implements UIResource {
  protected void paintComponent(Graphics paramGraphics) {
    Painter painter = (Painter)UIManager.get("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter");
    if (painter != null)
      if (paramGraphics instanceof Graphics2D) {
        painter.paint((Graphics2D)paramGraphics, this, getWidth() + 1, getHeight());
      } else {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), 2);
        Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        painter.paint(graphics2D, this, getWidth() + 1, getHeight());
        graphics2D.dispose();
        paramGraphics.drawImage(bufferedImage, 0, 0, null);
        bufferedImage = null;
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TableScrollPaneCorner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */