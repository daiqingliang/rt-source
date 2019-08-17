package javax.swing.plaf.metal;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class MetalInternalFrameUI extends BasicInternalFrameUI {
  private static final PropertyChangeListener metalPropertyChangeListener = new MetalPropertyChangeHandler(null);
  
  private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
  
  protected static String IS_PALETTE = "JInternalFrame.isPalette";
  
  private static String IS_PALETTE_KEY = "JInternalFrame.isPalette";
  
  private static String FRAME_TYPE = "JInternalFrame.frameType";
  
  private static String NORMAL_FRAME = "normal";
  
  private static String PALETTE_FRAME = "palette";
  
  private static String OPTION_DIALOG = "optionDialog";
  
  public MetalInternalFrameUI(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalInternalFrameUI((JInternalFrame)paramJComponent); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    Object object = paramJComponent.getClientProperty(IS_PALETTE_KEY);
    if (object != null)
      setPalette(((Boolean)object).booleanValue()); 
    Container container = this.frame.getContentPane();
    stripContentBorder(container);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    this.frame = (JInternalFrame)paramJComponent;
    Container container = ((JInternalFrame)paramJComponent).getContentPane();
    if (container instanceof JComponent) {
      JComponent jComponent = (JComponent)container;
      if (jComponent.getBorder() == handyEmptyBorder)
        jComponent.setBorder(null); 
    } 
    super.uninstallUI(paramJComponent);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.frame.addPropertyChangeListener(metalPropertyChangeListener);
  }
  
  protected void uninstallListeners() {
    this.frame.removePropertyChangeListener(metalPropertyChangeListener);
    super.uninstallListeners();
  }
  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    ActionMap actionMap = SwingUtilities.getUIActionMap(this.frame);
    if (actionMap != null)
      actionMap.remove("showSystemMenu"); 
  }
  
  protected void uninstallKeyboardActions() { super.uninstallKeyboardActions(); }
  
  protected void uninstallComponents() {
    this.titlePane = null;
    super.uninstallComponents();
  }
  
  private void stripContentBorder(Object paramObject) {
    if (paramObject instanceof JComponent) {
      JComponent jComponent = (JComponent)paramObject;
      Border border = jComponent.getBorder();
      if (border == null || border instanceof javax.swing.plaf.UIResource)
        jComponent.setBorder(handyEmptyBorder); 
    } 
  }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame) { return new MetalInternalFrameTitlePane(paramJInternalFrame); }
  
  private void setFrameType(String paramString) {
    if (paramString.equals(OPTION_DIALOG)) {
      LookAndFeel.installBorder(this.frame, "InternalFrame.optionDialogBorder");
      ((MetalInternalFrameTitlePane)this.titlePane).setPalette(false);
    } else if (paramString.equals(PALETTE_FRAME)) {
      LookAndFeel.installBorder(this.frame, "InternalFrame.paletteBorder");
      ((MetalInternalFrameTitlePane)this.titlePane).setPalette(true);
    } else {
      LookAndFeel.installBorder(this.frame, "InternalFrame.border");
      ((MetalInternalFrameTitlePane)this.titlePane).setPalette(false);
    } 
  }
  
  public void setPalette(boolean paramBoolean) {
    if (paramBoolean) {
      LookAndFeel.installBorder(this.frame, "InternalFrame.paletteBorder");
    } else {
      LookAndFeel.installBorder(this.frame, "InternalFrame.border");
    } 
    ((MetalInternalFrameTitlePane)this.titlePane).setPalette(paramBoolean);
  }
  
  protected MouseInputAdapter createBorderListener(JInternalFrame paramJInternalFrame) { return new BorderListener1(null); }
  
  private class BorderListener1 extends BasicInternalFrameUI.BorderListener implements SwingConstants {
    private BorderListener1() { super(MetalInternalFrameUI.this); }
    
    Rectangle getIconBounds() {
      boolean bool = MetalUtils.isLeftToRight(MetalInternalFrameUI.this.frame);
      int i = bool ? 5 : (MetalInternalFrameUI.this.titlePane.getWidth() - 5);
      Rectangle rectangle = null;
      Icon icon = MetalInternalFrameUI.this.frame.getFrameIcon();
      if (icon != null) {
        if (!bool)
          i -= icon.getIconWidth(); 
        int j = MetalInternalFrameUI.this.titlePane.getHeight() / 2 - icon.getIconHeight() / 2;
        rectangle = new Rectangle(i, j, icon.getIconWidth(), icon.getIconHeight());
      } 
      return rectangle;
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getClickCount() == 2 && param1MouseEvent.getSource() == MetalInternalFrameUI.this.getNorthPane() && MetalInternalFrameUI.this.frame.isClosable() && !MetalInternalFrameUI.this.frame.isIcon()) {
        Rectangle rectangle = getIconBounds();
        if (rectangle != null && rectangle.contains(param1MouseEvent.getX(), param1MouseEvent.getY())) {
          MetalInternalFrameUI.this.frame.doDefaultCloseAction();
        } else {
          super.mouseClicked(param1MouseEvent);
        } 
      } else {
        super.mouseClicked(param1MouseEvent);
      } 
    }
  }
  
  private static class MetalPropertyChangeHandler implements PropertyChangeListener {
    private MetalPropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      JInternalFrame jInternalFrame = (JInternalFrame)param1PropertyChangeEvent.getSource();
      if (!(jInternalFrame.getUI() instanceof MetalInternalFrameUI))
        return; 
      MetalInternalFrameUI metalInternalFrameUI;
      if (str.equals(FRAME_TYPE)) {
        if (param1PropertyChangeEvent.getNewValue() instanceof String)
          metalInternalFrameUI.setFrameType((String)param1PropertyChangeEvent.getNewValue()); 
      } else if (str.equals(IS_PALETTE_KEY)) {
        if (param1PropertyChangeEvent.getNewValue() != null) {
          metalInternalFrameUI.setPalette(((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue());
        } else {
          metalInternalFrameUI.setPalette(false);
        } 
      } else if (str.equals("contentPane")) {
        metalInternalFrameUI.stripContentBorder(param1PropertyChangeEvent.getNewValue());
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */