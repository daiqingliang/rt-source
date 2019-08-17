package javax.swing.colorchooser;

import javax.swing.JComponent;

public class ColorChooserComponentFactory {
  public static AbstractColorChooserPanel[] getDefaultChooserPanels() { return new AbstractColorChooserPanel[] { new DefaultSwatchChooserPanel(), new ColorChooserPanel(new ColorModelHSV()), new ColorChooserPanel(new ColorModelHSL()), new ColorChooserPanel(new ColorModel()), new ColorChooserPanel(new ColorModelCMYK()) }; }
  
  public static JComponent getPreviewPanel() { return new DefaultPreviewPanel(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorChooserComponentFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */