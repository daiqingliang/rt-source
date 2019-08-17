package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class BasicColorChooserUI extends ColorChooserUI {
  protected JColorChooser chooser;
  
  JTabbedPane tabbedPane;
  
  JPanel singlePanel;
  
  JPanel previewPanelHolder;
  
  JComponent previewPanel;
  
  boolean isMultiPanel = false;
  
  private static TransferHandler defaultTransferHandler = new ColorTransferHandler();
  
  protected AbstractColorChooserPanel[] defaultChoosers;
  
  protected ChangeListener previewListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  private Handler handler;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicColorChooserUI(); }
  
  protected AbstractColorChooserPanel[] createDefaultChoosers() { return ColorChooserComponentFactory.getDefaultChooserPanels(); }
  
  protected void uninstallDefaultChoosers() {
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = this.chooser.getChooserPanels();
    for (byte b = 0; b < arrayOfAbstractColorChooserPanel.length; b++)
      this.chooser.removeChooserPanel(arrayOfAbstractColorChooserPanel[b]); 
  }
  
  public void installUI(JComponent paramJComponent) {
    this.chooser = (JColorChooser)paramJComponent;
    super.installUI(paramJComponent);
    installDefaults();
    installListeners();
    this.tabbedPane = new JTabbedPane();
    this.tabbedPane.setName("ColorChooser.tabPane");
    this.tabbedPane.setInheritsPopupMenu(true);
    this.tabbedPane.getAccessibleContext().setAccessibleDescription(this.tabbedPane.getName());
    this.singlePanel = new JPanel(new CenterLayout());
    this.singlePanel.setName("ColorChooser.panel");
    this.singlePanel.setInheritsPopupMenu(true);
    this.chooser.setLayout(new BorderLayout());
    this.defaultChoosers = createDefaultChoosers();
    this.chooser.setChooserPanels(this.defaultChoosers);
    this.previewPanelHolder = new JPanel(new CenterLayout());
    this.previewPanelHolder.setName("ColorChooser.previewPanelHolder");
    if (DefaultLookup.getBoolean(this.chooser, this, "ColorChooser.showPreviewPanelText", true)) {
      String str = UIManager.getString("ColorChooser.previewText", this.chooser.getLocale());
      this.previewPanelHolder.setBorder(new TitledBorder(str));
    } 
    this.previewPanelHolder.setInheritsPopupMenu(true);
    installPreviewPanel();
    this.chooser.applyComponentOrientation(paramJComponent.getComponentOrientation());
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    this.chooser.remove(this.tabbedPane);
    this.chooser.remove(this.singlePanel);
    this.chooser.remove(this.previewPanelHolder);
    uninstallDefaultChoosers();
    uninstallListeners();
    uninstallPreviewPanel();
    uninstallDefaults();
    this.previewPanelHolder = null;
    this.previewPanel = null;
    this.defaultChoosers = null;
    this.chooser = null;
    this.tabbedPane = null;
    this.handler = null;
  }
  
  protected void installPreviewPanel() {
    JComponent jComponent = this.chooser.getPreviewPanel();
    if (jComponent == null) {
      jComponent = ColorChooserComponentFactory.getPreviewPanel();
    } else if (JPanel.class.equals(jComponent.getClass()) && 0 == jComponent.getComponentCount()) {
      jComponent = null;
    } 
    this.previewPanel = jComponent;
    if (jComponent != null) {
      this.chooser.add(this.previewPanelHolder, "South");
      jComponent.setForeground(this.chooser.getColor());
      this.previewPanelHolder.add(jComponent);
      jComponent.addMouseListener(getHandler());
      jComponent.setInheritsPopupMenu(true);
    } 
  }
  
  protected void uninstallPreviewPanel() {
    if (this.previewPanel != null) {
      this.previewPanel.removeMouseListener(getHandler());
      this.previewPanelHolder.remove(this.previewPanel);
    } 
    this.chooser.remove(this.previewPanelHolder);
  }
  
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(this.chooser, "ColorChooser.background", "ColorChooser.foreground", "ColorChooser.font");
    LookAndFeel.installProperty(this.chooser, "opaque", Boolean.TRUE);
    TransferHandler transferHandler = this.chooser.getTransferHandler();
    if (transferHandler == null || transferHandler instanceof UIResource)
      this.chooser.setTransferHandler(defaultTransferHandler); 
  }
  
  protected void uninstallDefaults() {
    if (this.chooser.getTransferHandler() instanceof UIResource)
      this.chooser.setTransferHandler(null); 
  }
  
  protected void installListeners() {
    this.propertyChangeListener = createPropertyChangeListener();
    this.chooser.addPropertyChangeListener(this.propertyChangeListener);
    this.previewListener = getHandler();
    this.chooser.getSelectionModel().addChangeListener(this.previewListener);
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  protected void uninstallListeners() {
    this.chooser.removePropertyChangeListener(this.propertyChangeListener);
    this.chooser.getSelectionModel().removeChangeListener(this.previewListener);
    this.previewListener = null;
  }
  
  private void selectionChanged(ColorSelectionModel paramColorSelectionModel) {
    JComponent jComponent = this.chooser.getPreviewPanel();
    if (jComponent != null) {
      jComponent.setForeground(paramColorSelectionModel.getSelectedColor());
      jComponent.repaint();
    } 
    AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel = this.chooser.getChooserPanels();
    if (arrayOfAbstractColorChooserPanel != null)
      for (AbstractColorChooserPanel abstractColorChooserPanel : arrayOfAbstractColorChooserPanel) {
        if (abstractColorChooserPanel != null)
          abstractColorChooserPanel.updateChooser(); 
      }  
  }
  
  static class ColorTransferHandler extends TransferHandler implements UIResource {
    ColorTransferHandler() { super("color"); }
  }
  
  private class Handler implements ChangeListener, MouseListener, PropertyChangeListener {
    private Handler() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicColorChooserUI.this.selectionChanged((ColorSelectionModel)param1ChangeEvent.getSource()); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (BasicColorChooserUI.this.chooser.getDragEnabled()) {
        TransferHandler transferHandler = BasicColorChooserUI.this.chooser.getTransferHandler();
        transferHandler.exportAsDrag(BasicColorChooserUI.this.chooser, param1MouseEvent, 1);
      } 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {}
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "chooserPanels") {
        AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel1 = (AbstractColorChooserPanel[])param1PropertyChangeEvent.getOldValue();
        AbstractColorChooserPanel[] arrayOfAbstractColorChooserPanel2 = (AbstractColorChooserPanel[])param1PropertyChangeEvent.getNewValue();
        int i;
        for (i = 0; i < arrayOfAbstractColorChooserPanel1.length; i++) {
          Container container = arrayOfAbstractColorChooserPanel1[i].getParent();
          if (container != null) {
            Container container1 = container.getParent();
            if (container1 != null)
              container1.remove(container); 
            arrayOfAbstractColorChooserPanel1[i].uninstallChooserPanel(BasicColorChooserUI.this.chooser);
          } 
        } 
        i = arrayOfAbstractColorChooserPanel2.length;
        if (i == 0) {
          BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
          return;
        } 
        if (i == 1) {
          BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.tabbedPane);
          JPanel jPanel = new JPanel(new CenterLayout());
          jPanel.setInheritsPopupMenu(true);
          jPanel.add(arrayOfAbstractColorChooserPanel2[0]);
          BasicColorChooserUI.this.singlePanel.add(jPanel, "Center");
          BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.singlePanel);
        } else {
          if (arrayOfAbstractColorChooserPanel1.length < 2) {
            BasicColorChooserUI.this.chooser.remove(BasicColorChooserUI.this.singlePanel);
            BasicColorChooserUI.this.chooser.add(BasicColorChooserUI.this.tabbedPane, "Center");
          } 
          for (byte b1 = 0; b1 < arrayOfAbstractColorChooserPanel2.length; b1++) {
            JPanel jPanel = new JPanel(new CenterLayout());
            jPanel.setInheritsPopupMenu(true);
            String str1 = arrayOfAbstractColorChooserPanel2[b1].getDisplayName();
            int j = arrayOfAbstractColorChooserPanel2[b1].getMnemonic();
            jPanel.add(arrayOfAbstractColorChooserPanel2[b1]);
            BasicColorChooserUI.this.tabbedPane.addTab(str1, jPanel);
            if (j > 0) {
              BasicColorChooserUI.this.tabbedPane.setMnemonicAt(b1, j);
              int k = arrayOfAbstractColorChooserPanel2[b1].getDisplayedMnemonicIndex();
              if (k >= 0)
                BasicColorChooserUI.this.tabbedPane.setDisplayedMnemonicIndexAt(b1, k); 
            } 
          } 
        } 
        BasicColorChooserUI.this.chooser.applyComponentOrientation(BasicColorChooserUI.this.chooser.getComponentOrientation());
        for (byte b = 0; b < arrayOfAbstractColorChooserPanel2.length; b++)
          arrayOfAbstractColorChooserPanel2[b].installChooserPanel(BasicColorChooserUI.this.chooser); 
      } else if (str == "previewPanel") {
        BasicColorChooserUI.this.uninstallPreviewPanel();
        BasicColorChooserUI.this.installPreviewPanel();
      } else if (str == "selectionModel") {
        ColorSelectionModel colorSelectionModel1 = (ColorSelectionModel)param1PropertyChangeEvent.getOldValue();
        colorSelectionModel1.removeChangeListener(BasicColorChooserUI.this.previewListener);
        ColorSelectionModel colorSelectionModel2 = (ColorSelectionModel)param1PropertyChangeEvent.getNewValue();
        colorSelectionModel2.addChangeListener(BasicColorChooserUI.this.previewListener);
        BasicColorChooserUI.this.selectionChanged(colorSelectionModel2);
      } else if (str == "componentOrientation") {
        ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
        JColorChooser jColorChooser = (JColorChooser)param1PropertyChangeEvent.getSource();
        if (componentOrientation != (ComponentOrientation)param1PropertyChangeEvent.getOldValue()) {
          jColorChooser.applyComponentOrientation(componentOrientation);
          jColorChooser.updateUI();
        } 
      } 
    }
  }
  
  public class PropertyHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicColorChooserUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicColorChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */