package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.plaf.ColorChooserUI;

public class JColorChooser extends JComponent implements Accessible {
  private static final String uiClassID = "ColorChooserUI";
  
  private ColorSelectionModel selectionModel;
  
  private JComponent previewPanel = ColorChooserComponentFactory.getPreviewPanel();
  
  private AbstractColorChooserPanel[] chooserPanels = new AbstractColorChooserPanel[0];
  
  private boolean dragEnabled;
  
  public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
  
  public static final String PREVIEW_PANEL_PROPERTY = "previewPanel";
  
  public static final String CHOOSER_PANELS_PROPERTY = "chooserPanels";
  
  protected AccessibleContext accessibleContext = null;
  
  public static Color showDialog(Component paramComponent, String paramString, Color paramColor) throws HeadlessException {
    JColorChooser jColorChooser = new JColorChooser((paramColor != null) ? paramColor : Color.white);
    ColorTracker colorTracker = new ColorTracker(jColorChooser);
    JDialog jDialog = createDialog(paramComponent, paramString, true, jColorChooser, colorTracker, null);
    jDialog.addComponentListener(new ColorChooserDialog.DisposeOnClose());
    jDialog.show();
    return colorTracker.getColor();
  }
  
  public static JDialog createDialog(Component paramComponent, String paramString, boolean paramBoolean, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2) throws HeadlessException {
    ColorChooserDialog colorChooserDialog;
    Window window = JOptionPane.getWindowForComponent(paramComponent);
    if (window instanceof Frame) {
      colorChooserDialog = new ColorChooserDialog((Frame)window, paramString, paramBoolean, paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
    } else {
      colorChooserDialog = new ColorChooserDialog((Dialog)window, paramString, paramBoolean, paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
    } 
    colorChooserDialog.getAccessibleContext().setAccessibleDescription(paramString);
    return colorChooserDialog;
  }
  
  public JColorChooser() { this(Color.white); }
  
  public JColorChooser(Color paramColor) { this(new DefaultColorSelectionModel(paramColor)); }
  
  public JColorChooser(ColorSelectionModel paramColorSelectionModel) {
    this.selectionModel = paramColorSelectionModel;
    updateUI();
    this.dragEnabled = false;
  }
  
  public ColorChooserUI getUI() { return (ColorChooserUI)this.ui; }
  
  public void setUI(ColorChooserUI paramColorChooserUI) { setUI(paramColorChooserUI); }
  
  public void updateUI() { setUI((ColorChooserUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ColorChooserUI"; }
  
  public Color getColor() { return this.selectionModel.getSelectedColor(); }
  
  public void setColor(Color paramColor) { this.selectionModel.setSelectedColor(paramColor); }
  
  public void setColor(int paramInt1, int paramInt2, int paramInt3) { setColor(new Color(paramInt1, paramInt2, paramInt3)); }
  
  public void setColor(int paramInt) { setColor(paramInt >> 16 & 0xFF, paramInt >> 8 & 0xFF, paramInt & 0xFF); }
  
  public void setDragEnabled(boolean paramBoolean) {
    if (paramBoolean && GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    this.dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled() { return this.dragEnabled; }
  
  public void setPreviewPanel(JComponent paramJComponent) {
    if (this.previewPanel != paramJComponent) {
      JComponent jComponent = this.previewPanel;
      this.previewPanel = paramJComponent;
      firePropertyChange("previewPanel", jComponent, paramJComponent);
    } 
  }
  
  public JComponent getPreviewPanel() { return this.previewPanel; }
  
  public void addChooserPanel(AbstractColorChooserPanel paramAbstractColorChooserPanel) {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel1 = getChooserPanels();
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel2 = new AbstractColorChooserPanel[arrayOfAbstractColorChooserPanel1.length + 1];
    System.arraycopy(arrayOfAbstractColorChooserPanel1, 0, arrayOfAbstractColorChooserPanel2, 0, arrayOfAbstractColorChooserPanel1.length);
    arrayOfAbstractColorChooserPanel2[arrayOfAbstractColorChooserPanel2.length - 1] = paramAbstractColorChooserPanel;
    setChooserPanels(arrayOfAbstractColorChooserPanel2);
  }
  
  public AbstractColorChooserPanel removeChooserPanel(AbstractColorChooserPanel paramAbstractColorChooserPanel) {
    int i = -1;
    for (byte b = 0; b < this.chooserPanels.length; b++) {
      if (this.chooserPanels[b] == paramAbstractColorChooserPanel) {
        i = b;
        break;
      } 
    } 
    if (i == -1)
      throw new IllegalArgumentException("chooser panel not in this chooser"); 
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = new AbstractColorChooserPanel[this.chooserPanels.length - 1];
    if (i == this.chooserPanels.length - 1) {
      System.arraycopy(this.chooserPanels, 0, arrayOfAbstractColorChooserPanel, 0, arrayOfAbstractColorChooserPanel.length);
    } else if (i == 0) {
      System.arraycopy(this.chooserPanels, 1, arrayOfAbstractColorChooserPanel, 0, arrayOfAbstractColorChooserPanel.length);
    } else {
      System.arraycopy(this.chooserPanels, 0, arrayOfAbstractColorChooserPanel, 0, i);
      System.arraycopy(this.chooserPanels, i + 1, arrayOfAbstractColorChooserPanel, i, this.chooserPanels.length - i - 1);
    } 
    setChooserPanels(arrayOfAbstractColorChooserPanel);
    return paramAbstractColorChooserPanel;
  }
  
  public void setChooserPanels(AbstractColorChooserPanel[] paramArrayOfAbstractColorChooserPanel) {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = this.chooserPanels;
    this.chooserPanels = paramArrayOfAbstractColorChooserPanel;
    firePropertyChange("chooserPanels", arrayOfAbstractColorChooserPanel, paramArrayOfAbstractColorChooserPanel);
  }
  
  public AbstractColorChooserPanel[] getChooserPanels() { return this.chooserPanels; }
  
  public ColorSelectionModel getSelectionModel() { return this.selectionModel; }
  
  public void setSelectionModel(ColorSelectionModel paramColorSelectionModel) {
    ColorSelectionModel colorSelectionModel = this.selectionModel;
    this.selectionModel = paramColorSelectionModel;
    firePropertyChange("selectionModel", colorSelectionModel, paramColorSelectionModel);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ColorChooserUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    StringBuffer stringBuffer = new StringBuffer("");
    for (byte b = 0; b < this.chooserPanels.length; b++)
      stringBuffer.append("[" + this.chooserPanels[b].toString() + "]"); 
    String str = (this.previewPanel != null) ? this.previewPanel.toString() : "";
    return super.paramString() + ",chooserPanels=" + stringBuffer.toString() + ",previewPanel=" + str;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJColorChooser(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJColorChooser extends JComponent.AccessibleJComponent {
    protected AccessibleJColorChooser() { super(JColorChooser.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.COLOR_CHOOSER; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JColorChooser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */