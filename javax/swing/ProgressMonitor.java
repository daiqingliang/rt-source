package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;

public class ProgressMonitor implements Accessible {
  private ProgressMonitor root;
  
  private JDialog dialog;
  
  private JOptionPane pane;
  
  private JProgressBar myBar;
  
  private JLabel noteLabel;
  
  private Component parentComponent;
  
  private String note;
  
  private Object[] cancelOption = null;
  
  private Object message;
  
  private long T0;
  
  private int millisToDecideToPopup = 500;
  
  private int millisToPopup = 2000;
  
  private int min;
  
  private int max;
  
  protected AccessibleContext accessibleContext = null;
  
  private AccessibleContext accessibleJOptionPane = null;
  
  public ProgressMonitor(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2) { this(paramComponent, paramObject, paramString, paramInt1, paramInt2, null); }
  
  private ProgressMonitor(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, ProgressMonitor paramProgressMonitor) {
    this.min = paramInt1;
    this.max = paramInt2;
    this.parentComponent = paramComponent;
    this.cancelOption = new Object[1];
    this.cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");
    this.message = paramObject;
    this.note = paramString;
    if (paramProgressMonitor != null) {
      this.root = (paramProgressMonitor.root != null) ? paramProgressMonitor.root : paramProgressMonitor;
      this.T0 = this.root.T0;
      this.dialog = this.root.dialog;
    } else {
      this.T0 = System.currentTimeMillis();
    } 
  }
  
  public void setProgress(int paramInt) {
    if (paramInt >= this.max) {
      close();
    } else if (this.myBar != null) {
      this.myBar.setValue(paramInt);
    } else {
      long l1 = System.currentTimeMillis();
      long l2 = (int)(l1 - this.T0);
      if (l2 >= this.millisToDecideToPopup) {
        int i;
        if (paramInt > this.min) {
          i = (int)(l2 * (this.max - this.min) / (paramInt - this.min));
        } else {
          i = this.millisToPopup;
        } 
        if (i >= this.millisToPopup) {
          this.myBar = new JProgressBar();
          this.myBar.setMinimum(this.min);
          this.myBar.setMaximum(this.max);
          this.myBar.setValue(paramInt);
          if (this.note != null)
            this.noteLabel = new JLabel(this.note); 
          this.pane = new ProgressOptionPane(this, new Object[] { this.message, this.noteLabel, this.myBar });
          this.dialog = this.pane.createDialog(this.parentComponent, UIManager.getString("ProgressMonitor.progressText"));
          this.dialog.show();
        } 
      } 
    } 
  }
  
  public void close() {
    if (this.dialog != null) {
      this.dialog.setVisible(false);
      this.dialog.dispose();
      this.dialog = null;
      this.pane = null;
      this.myBar = null;
    } 
  }
  
  public int getMinimum() { return this.min; }
  
  public void setMinimum(int paramInt) {
    if (this.myBar != null)
      this.myBar.setMinimum(paramInt); 
    this.min = paramInt;
  }
  
  public int getMaximum() { return this.max; }
  
  public void setMaximum(int paramInt) {
    if (this.myBar != null)
      this.myBar.setMaximum(paramInt); 
    this.max = paramInt;
  }
  
  public boolean isCanceled() {
    if (this.pane == null)
      return false; 
    Object object = this.pane.getValue();
    return (object != null && this.cancelOption.length == 1 && object.equals(this.cancelOption[0]));
  }
  
  public void setMillisToDecideToPopup(int paramInt) { this.millisToDecideToPopup = paramInt; }
  
  public int getMillisToDecideToPopup() { return this.millisToDecideToPopup; }
  
  public void setMillisToPopup(int paramInt) { this.millisToPopup = paramInt; }
  
  public int getMillisToPopup() { return this.millisToPopup; }
  
  public void setNote(String paramString) {
    this.note = paramString;
    if (this.noteLabel != null)
      this.noteLabel.setText(paramString); 
  }
  
  public String getNote() { return this.note; }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleProgressMonitor(); 
    if (this.pane != null && this.accessibleJOptionPane == null && this.accessibleContext instanceof AccessibleProgressMonitor)
      ((AccessibleProgressMonitor)this.accessibleContext).optionPaneCreated(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleProgressMonitor extends AccessibleContext implements AccessibleText, ChangeListener, PropertyChangeListener {
    private Object oldModelValue;
    
    private void optionPaneCreated() {
      ProgressMonitor.this.accessibleJOptionPane = ((ProgressMonitor.ProgressOptionPane)ProgressMonitor.this.pane).getAccessibleJOptionPane();
      if (ProgressMonitor.this.myBar != null)
        ProgressMonitor.this.myBar.addChangeListener(this); 
      if (ProgressMonitor.this.noteLabel != null)
        ProgressMonitor.this.noteLabel.addPropertyChangeListener(this); 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (param1ChangeEvent == null)
        return; 
      if (ProgressMonitor.this.myBar != null) {
        Integer integer = Integer.valueOf(ProgressMonitor.this.myBar.getValue());
        firePropertyChange("AccessibleValue", this.oldModelValue, integer);
        this.oldModelValue = integer;
      } 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getSource() == ProgressMonitor.this.noteLabel && param1PropertyChangeEvent.getPropertyName() == "text")
        firePropertyChange("AccessibleText", null, Integer.valueOf(0)); 
    }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((ProgressMonitor.this.accessibleJOptionPane != null) ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleName() : null); }
    
    public String getAccessibleDescription() { return (this.accessibleDescription != null) ? this.accessibleDescription : ((ProgressMonitor.this.accessibleJOptionPane != null) ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleDescription() : null); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PROGRESS_MONITOR; }
    
    public AccessibleStateSet getAccessibleStateSet() { return (ProgressMonitor.this.accessibleJOptionPane != null) ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleStateSet() : null; }
    
    public Accessible getAccessibleParent() { return ProgressMonitor.this.dialog; }
    
    private AccessibleContext getParentAccessibleContext() { return (ProgressMonitor.this.dialog != null) ? ProgressMonitor.this.dialog.getAccessibleContext() : null; }
    
    public int getAccessibleIndexInParent() { return (ProgressMonitor.this.accessibleJOptionPane != null) ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleIndexInParent() : -1; }
    
    public int getAccessibleChildrenCount() {
      AccessibleContext accessibleContext = getPanelAccessibleContext();
      return (accessibleContext != null) ? accessibleContext.getAccessibleChildrenCount() : 0;
    }
    
    public Accessible getAccessibleChild(int param1Int) {
      AccessibleContext accessibleContext = getPanelAccessibleContext();
      return (accessibleContext != null) ? accessibleContext.getAccessibleChild(param1Int) : null;
    }
    
    private AccessibleContext getPanelAccessibleContext() {
      if (ProgressMonitor.this.myBar != null) {
        Container container = ProgressMonitor.this.myBar.getParent();
        if (container instanceof Accessible)
          return container.getAccessibleContext(); 
      } 
      return null;
    }
    
    public Locale getLocale() throws IllegalComponentStateException { return (ProgressMonitor.this.accessibleJOptionPane != null) ? ProgressMonitor.this.accessibleJOptionPane.getLocale() : null; }
    
    public AccessibleComponent getAccessibleComponent() { return (ProgressMonitor.this.accessibleJOptionPane != null) ? ProgressMonitor.this.accessibleJOptionPane.getAccessibleComponent() : null; }
    
    public AccessibleValue getAccessibleValue() { return (ProgressMonitor.this.myBar != null) ? ProgressMonitor.this.myBar.getAccessibleContext().getAccessibleValue() : null; }
    
    public AccessibleText getAccessibleText() { return (getNoteLabelAccessibleText() != null) ? this : null; }
    
    private AccessibleText getNoteLabelAccessibleText() { return (ProgressMonitor.this.noteLabel != null) ? ProgressMonitor.this.noteLabel.getAccessibleContext().getAccessibleText() : null; }
    
    public int getIndexAtPoint(Point param1Point) {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      if (accessibleText != null && sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)) {
        Point point = SwingUtilities.convertPoint(ProgressMonitor.this.pane, param1Point, ProgressMonitor.this.noteLabel);
        if (point != null)
          return accessibleText.getIndexAtPoint(point); 
      } 
      return -1;
    }
    
    public Rectangle getCharacterBounds(int param1Int) {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      if (accessibleText != null && sameWindowAncestor(ProgressMonitor.this.pane, ProgressMonitor.this.noteLabel)) {
        Rectangle rectangle = accessibleText.getCharacterBounds(param1Int);
        if (rectangle != null)
          return SwingUtilities.convertRectangle(ProgressMonitor.this.noteLabel, rectangle, ProgressMonitor.this.pane); 
      } 
      return null;
    }
    
    private boolean sameWindowAncestor(Component param1Component1, Component param1Component2) { return (param1Component1 == null || param1Component2 == null) ? false : ((SwingUtilities.getWindowAncestor(param1Component1) == SwingUtilities.getWindowAncestor(param1Component2))); }
    
    public int getCharCount() {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getCharCount() : -1;
    }
    
    public int getCaretPosition() {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getCaretPosition() : -1;
    }
    
    public String getAtIndex(int param1Int1, int param1Int2) {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getAtIndex(param1Int1, param1Int2) : null;
    }
    
    public String getAfterIndex(int param1Int1, int param1Int2) {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getAfterIndex(param1Int1, param1Int2) : null;
    }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getBeforeIndex(param1Int1, param1Int2) : null;
    }
    
    public AttributeSet getCharacterAttribute(int param1Int) {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getCharacterAttribute(param1Int) : null;
    }
    
    public int getSelectionStart() {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getSelectionStart() : -1;
    }
    
    public int getSelectionEnd() {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getSelectionEnd() : -1;
    }
    
    public String getSelectedText() {
      AccessibleText accessibleText = getNoteLabelAccessibleText();
      return (accessibleText != null) ? accessibleText.getSelectedText() : null;
    }
  }
  
  private class ProgressOptionPane extends JOptionPane {
    ProgressOptionPane(ProgressMonitor this$0, Object param1Object) { super(param1Object, 1, -1, null, this$0.cancelOption, null); }
    
    public int getMaxCharactersPerLineCount() { return 60; }
    
    public JDialog createDialog(Component param1Component, String param1String) {
      final JDialog dialog;
      Window window = JOptionPane.getWindowForComponent(param1Component);
      if (window instanceof Frame) {
        jDialog = new JDialog((Frame)window, param1String, false);
      } else {
        jDialog = new JDialog((Dialog)window, param1String, false);
      } 
      if (window instanceof SwingUtilities.SharedOwnerFrame) {
        WindowListener windowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
        jDialog.addWindowListener(windowListener);
      } 
      Container container = jDialog.getContentPane();
      container.setLayout(new BorderLayout());
      container.add(this, "Center");
      jDialog.pack();
      jDialog.setLocationRelativeTo(param1Component);
      jDialog.addWindowListener(new WindowAdapter() {
            boolean gotFocus = false;
            
            public void windowClosing(WindowEvent param2WindowEvent) { ProgressMonitor.ProgressOptionPane.this.setValue(ProgressMonitor.ProgressOptionPane.this.this$0.cancelOption[0]); }
            
            public void windowActivated(WindowEvent param2WindowEvent) {
              if (!this.gotFocus) {
                ProgressMonitor.ProgressOptionPane.this.selectInitialValue();
                this.gotFocus = true;
              } 
            }
          });
      addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent param2PropertyChangeEvent) {
              if (dialog.isVisible() && param2PropertyChangeEvent.getSource() == ProgressMonitor.ProgressOptionPane.this && (param2PropertyChangeEvent.getPropertyName().equals("value") || param2PropertyChangeEvent.getPropertyName().equals("inputValue"))) {
                dialog.setVisible(false);
                dialog.dispose();
              } 
            }
          });
      return jDialog;
    }
    
    public AccessibleContext getAccessibleContext() { return this.this$0.getAccessibleContext(); }
    
    private AccessibleContext getAccessibleJOptionPane() { return super.getAccessibleContext(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ProgressMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */