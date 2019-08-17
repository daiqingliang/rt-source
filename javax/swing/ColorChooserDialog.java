package javax.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Locale;
import sun.swing.SwingUtilities2;

class ColorChooserDialog extends JDialog {
  private Color initialColor;
  
  private JColorChooser chooserPane;
  
  private JButton cancelButton;
  
  public ColorChooserDialog(Dialog paramDialog, String paramString, boolean paramBoolean, Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2) throws HeadlessException {
    super(paramDialog, paramString, paramBoolean);
    initColorChooserDialog(paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
  }
  
  public ColorChooserDialog(Frame paramFrame, String paramString, boolean paramBoolean, Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2) throws HeadlessException {
    super(paramFrame, paramString, paramBoolean);
    initColorChooserDialog(paramComponent, paramJColorChooser, paramActionListener1, paramActionListener2);
  }
  
  protected void initColorChooserDialog(Component paramComponent, JColorChooser paramJColorChooser, ActionListener paramActionListener1, ActionListener paramActionListener2) {
    this.chooserPane = paramJColorChooser;
    Locale locale = getLocale();
    String str1 = UIManager.getString("ColorChooser.okText", locale);
    String str2 = UIManager.getString("ColorChooser.cancelText", locale);
    String str3 = UIManager.getString("ColorChooser.resetText", locale);
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    container.add(paramJColorChooser, "Center");
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new FlowLayout(1));
    JButton jButton1 = new JButton(str1);
    getRootPane().setDefaultButton(jButton1);
    jButton1.getAccessibleContext().setAccessibleDescription(str1);
    jButton1.setActionCommand("OK");
    jButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) { ColorChooserDialog.this.hide(); }
        });
    if (paramActionListener1 != null)
      jButton1.addActionListener(paramActionListener1); 
    jPanel.add(jButton1);
    this.cancelButton = new JButton(str2);
    this.cancelButton.getAccessibleContext().setAccessibleDescription(str2);
    AbstractAction abstractAction = new AbstractAction() {
        public void actionPerformed(ActionEvent param1ActionEvent) { ((AbstractButton)param1ActionEvent.getSource()).fireActionPerformed(param1ActionEvent); }
      };
    KeyStroke keyStroke = KeyStroke.getKeyStroke(27, 0);
    InputMap inputMap = this.cancelButton.getInputMap(2);
    ActionMap actionMap = this.cancelButton.getActionMap();
    if (inputMap != null && actionMap != null) {
      inputMap.put(keyStroke, "cancel");
      actionMap.put("cancel", abstractAction);
    } 
    this.cancelButton.setActionCommand("cancel");
    this.cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) { ColorChooserDialog.this.hide(); }
        });
    if (paramActionListener2 != null)
      this.cancelButton.addActionListener(paramActionListener2); 
    jPanel.add(this.cancelButton);
    JButton jButton2 = new JButton(str3);
    jButton2.getAccessibleContext().setAccessibleDescription(str3);
    jButton2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) { ColorChooserDialog.this.reset(); }
        });
    int i = SwingUtilities2.getUIDefaultsInt("ColorChooser.resetMnemonic", locale, -1);
    if (i != -1)
      jButton2.setMnemonic(i); 
    jPanel.add(jButton2);
    container.add(jPanel, "South");
    if (JDialog.isDefaultLookAndFeelDecorated()) {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool)
        getRootPane().setWindowDecorationStyle(5); 
    } 
    applyComponentOrientation(((paramComponent == null) ? getRootPane() : paramComponent).getComponentOrientation());
    pack();
    setLocationRelativeTo(paramComponent);
    addWindowListener(new Closer());
  }
  
  public void show() {
    this.initialColor = this.chooserPane.getColor();
    super.show();
  }
  
  public void reset() { this.chooserPane.setColor(this.initialColor); }
  
  class Closer extends WindowAdapter implements Serializable {
    public void windowClosing(WindowEvent param1WindowEvent) {
      ColorChooserDialog.this.cancelButton.doClick(0);
      Window window = param1WindowEvent.getWindow();
      window.hide();
    }
  }
  
  static class DisposeOnClose extends ComponentAdapter implements Serializable {
    public void componentHidden(ComponentEvent param1ComponentEvent) {
      Window window = (Window)param1ComponentEvent.getComponent();
      window.dispose();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ColorChooserDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */