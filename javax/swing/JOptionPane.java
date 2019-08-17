package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.OptionPaneUI;

public class JOptionPane extends JComponent implements Accessible {
  private static final String uiClassID = "OptionPaneUI";
  
  public static final Object UNINITIALIZED_VALUE = "uninitializedValue";
  
  public static final int DEFAULT_OPTION = -1;
  
  public static final int YES_NO_OPTION = 0;
  
  public static final int YES_NO_CANCEL_OPTION = 1;
  
  public static final int OK_CANCEL_OPTION = 2;
  
  public static final int YES_OPTION = 0;
  
  public static final int NO_OPTION = 1;
  
  public static final int CANCEL_OPTION = 2;
  
  public static final int OK_OPTION = 0;
  
  public static final int CLOSED_OPTION = -1;
  
  public static final int ERROR_MESSAGE = 0;
  
  public static final int INFORMATION_MESSAGE = 1;
  
  public static final int WARNING_MESSAGE = 2;
  
  public static final int QUESTION_MESSAGE = 3;
  
  public static final int PLAIN_MESSAGE = -1;
  
  public static final String ICON_PROPERTY = "icon";
  
  public static final String MESSAGE_PROPERTY = "message";
  
  public static final String VALUE_PROPERTY = "value";
  
  public static final String OPTIONS_PROPERTY = "options";
  
  public static final String INITIAL_VALUE_PROPERTY = "initialValue";
  
  public static final String MESSAGE_TYPE_PROPERTY = "messageType";
  
  public static final String OPTION_TYPE_PROPERTY = "optionType";
  
  public static final String SELECTION_VALUES_PROPERTY = "selectionValues";
  
  public static final String INITIAL_SELECTION_VALUE_PROPERTY = "initialSelectionValue";
  
  public static final String INPUT_VALUE_PROPERTY = "inputValue";
  
  public static final String WANTS_INPUT_PROPERTY = "wantsInput";
  
  protected Icon icon;
  
  protected Object message;
  
  protected Object[] options;
  
  protected Object initialValue;
  
  protected int messageType;
  
  protected int optionType;
  
  protected Object value;
  
  protected Object[] selectionValues;
  
  protected Object inputValue;
  
  protected Object initialSelectionValue;
  
  protected boolean wantsInput;
  
  private static final Object sharedFrameKey = JOptionPane.class;
  
  public static String showInputDialog(Object paramObject) throws HeadlessException { return showInputDialog(null, paramObject); }
  
  public static String showInputDialog(Object paramObject1, Object paramObject2) { return showInputDialog(null, paramObject1, paramObject2); }
  
  public static String showInputDialog(Component paramComponent, Object paramObject) throws HeadlessException { return showInputDialog(paramComponent, paramObject, UIManager.getString("OptionPane.inputDialogTitle", paramComponent), 3); }
  
  public static String showInputDialog(Component paramComponent, Object paramObject1, Object paramObject2) { return (String)showInputDialog(paramComponent, paramObject1, UIManager.getString("OptionPane.inputDialogTitle", paramComponent), 3, null, null, paramObject2); }
  
  public static String showInputDialog(Component paramComponent, Object paramObject, String paramString, int paramInt) throws HeadlessException { return (String)showInputDialog(paramComponent, paramObject, paramString, paramInt, null, null, null); }
  
  public static Object showInputDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2) throws HeadlessException {
    JOptionPane jOptionPane = new JOptionPane(paramObject1, paramInt, 2, paramIcon, null, null);
    jOptionPane.setWantsInput(true);
    jOptionPane.setSelectionValues(paramArrayOfObject);
    jOptionPane.setInitialSelectionValue(paramObject2);
    jOptionPane.setComponentOrientation(((paramComponent == null) ? getRootFrame() : paramComponent).getComponentOrientation());
    int i = styleFromMessageType(paramInt);
    JDialog jDialog = jOptionPane.createDialog(paramComponent, paramString, i);
    jOptionPane.selectInitialValue();
    jDialog.show();
    jDialog.dispose();
    Object object = jOptionPane.getInputValue();
    return (object == UNINITIALIZED_VALUE) ? null : object;
  }
  
  public static void showMessageDialog(Component paramComponent, Object paramObject) throws HeadlessException { showMessageDialog(paramComponent, paramObject, UIManager.getString("OptionPane.messageDialogTitle", paramComponent), 1); }
  
  public static void showMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt) throws HeadlessException { showMessageDialog(paramComponent, paramObject, paramString, paramInt, null); }
  
  public static void showMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt, Icon paramIcon) throws HeadlessException { showOptionDialog(paramComponent, paramObject, paramString, -1, paramInt, paramIcon, null, null); }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject) throws HeadlessException { return showConfirmDialog(paramComponent, paramObject, UIManager.getString("OptionPane.titleText"), 1); }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt) throws HeadlessException { return showConfirmDialog(paramComponent, paramObject, paramString, paramInt, 3); }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2) throws HeadlessException { return showConfirmDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, null); }
  
  public static int showConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, Icon paramIcon) throws HeadlessException { return showOptionDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, paramIcon, null, null); }
  
  public static int showOptionDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2) throws HeadlessException {
    JOptionPane jOptionPane = new JOptionPane(paramObject1, paramInt2, paramInt1, paramIcon, paramArrayOfObject, paramObject2);
    jOptionPane.setInitialValue(paramObject2);
    jOptionPane.setComponentOrientation(((paramComponent == null) ? getRootFrame() : paramComponent).getComponentOrientation());
    int i = styleFromMessageType(paramInt2);
    JDialog jDialog = jOptionPane.createDialog(paramComponent, paramString, i);
    jOptionPane.selectInitialValue();
    jDialog.show();
    jDialog.dispose();
    Object object = jOptionPane.getValue();
    if (object == null)
      return -1; 
    if (paramArrayOfObject == null)
      return (object instanceof Integer) ? ((Integer)object).intValue() : -1; 
    byte b = 0;
    int j = paramArrayOfObject.length;
    while (b < j) {
      if (paramArrayOfObject[b].equals(object))
        return b; 
      b++;
    } 
    return -1;
  }
  
  public JDialog createDialog(Component paramComponent, String paramString) throws HeadlessException {
    int i = styleFromMessageType(getMessageType());
    return createDialog(paramComponent, paramString, i);
  }
  
  public JDialog createDialog(String paramString) throws HeadlessException {
    int i = styleFromMessageType(getMessageType());
    JDialog jDialog = new JDialog((Dialog)null, paramString, true);
    initDialog(jDialog, i, null);
    return jDialog;
  }
  
  private JDialog createDialog(Component paramComponent, String paramString, int paramInt) throws HeadlessException {
    JDialog jDialog;
    Window window = getWindowForComponent(paramComponent);
    if (window instanceof Frame) {
      jDialog = new JDialog((Frame)window, paramString, true);
    } else {
      jDialog = new JDialog((Dialog)window, paramString, true);
    } 
    if (window instanceof SwingUtilities.SharedOwnerFrame) {
      WindowListener windowListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
      jDialog.addWindowListener(windowListener);
    } 
    initDialog(jDialog, paramInt, paramComponent);
    return jDialog;
  }
  
  private void initDialog(final JDialog dialog, int paramInt, Component paramComponent) {
    paramJDialog.setComponentOrientation(getComponentOrientation());
    Container container = paramJDialog.getContentPane();
    container.setLayout(new BorderLayout());
    container.add(this, "Center");
    paramJDialog.setResizable(false);
    if (JDialog.isDefaultLookAndFeelDecorated()) {
      boolean bool = UIManager.getLookAndFeel().getSupportsWindowDecorations();
      if (bool) {
        paramJDialog.setUndecorated(true);
        getRootPane().setWindowDecorationStyle(paramInt);
      } 
    } 
    paramJDialog.pack();
    paramJDialog.setLocationRelativeTo(paramComponent);
    final PropertyChangeListener listener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
          if (dialog.isVisible() && param1PropertyChangeEvent.getSource() == JOptionPane.this && param1PropertyChangeEvent.getPropertyName().equals("value") && param1PropertyChangeEvent.getNewValue() != null && param1PropertyChangeEvent.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)
            dialog.setVisible(false); 
        }
      };
    WindowAdapter windowAdapter = new WindowAdapter() {
        private boolean gotFocus = false;
        
        public void windowClosing(WindowEvent param1WindowEvent) { JOptionPane.this.setValue(null); }
        
        public void windowClosed(WindowEvent param1WindowEvent) {
          JOptionPane.this.removePropertyChangeListener(listener);
          dialog.getContentPane().removeAll();
        }
        
        public void windowGainedFocus(WindowEvent param1WindowEvent) {
          if (!this.gotFocus) {
            JOptionPane.this.selectInitialValue();
            this.gotFocus = true;
          } 
        }
      };
    paramJDialog.addWindowListener(windowAdapter);
    paramJDialog.addWindowFocusListener(windowAdapter);
    paramJDialog.addComponentListener(new ComponentAdapter() {
          public void componentShown(ComponentEvent param1ComponentEvent) { JOptionPane.this.setValue(JOptionPane.UNINITIALIZED_VALUE); }
        });
    addPropertyChangeListener(propertyChangeListener);
  }
  
  public static void showInternalMessageDialog(Component paramComponent, Object paramObject) throws HeadlessException { showInternalMessageDialog(paramComponent, paramObject, UIManager.getString("OptionPane.messageDialogTitle", paramComponent), 1); }
  
  public static void showInternalMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt) throws HeadlessException { showInternalMessageDialog(paramComponent, paramObject, paramString, paramInt, null); }
  
  public static void showInternalMessageDialog(Component paramComponent, Object paramObject, String paramString, int paramInt, Icon paramIcon) throws HeadlessException { showInternalOptionDialog(paramComponent, paramObject, paramString, -1, paramInt, paramIcon, null, null); }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject) throws HeadlessException { return showInternalConfirmDialog(paramComponent, paramObject, UIManager.getString("OptionPane.titleText"), 1); }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt) throws HeadlessException { return showInternalConfirmDialog(paramComponent, paramObject, paramString, paramInt, 3); }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2) throws HeadlessException { return showInternalConfirmDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, null); }
  
  public static int showInternalConfirmDialog(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, Icon paramIcon) throws HeadlessException { return showInternalOptionDialog(paramComponent, paramObject, paramString, paramInt1, paramInt2, paramIcon, null, null); }
  
  public static int showInternalOptionDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2) throws HeadlessException {
    JOptionPane jOptionPane = new JOptionPane(paramObject1, paramInt2, paramInt1, paramIcon, paramArrayOfObject, paramObject2);
    jOptionPane.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
    Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    jOptionPane.setInitialValue(paramObject2);
    JInternalFrame jInternalFrame = jOptionPane.createInternalFrame(paramComponent, paramString);
    jOptionPane.selectInitialValue();
    jInternalFrame.setVisible(true);
    if (jInternalFrame.isVisible() && !jInternalFrame.isShowing())
      for (Container container = jInternalFrame.getParent(); container != null; container = container.getParent()) {
        if (!container.isVisible())
          container.setVisible(true); 
      }  
    try {
      Method method = (Method)AccessController.doPrivileged(new ModalPrivilegedAction(Container.class, "startLWModal"));
      if (method != null)
        method.invoke(jInternalFrame, (Object[])null); 
    } catch (IllegalAccessException illegalAccessException) {
    
    } catch (IllegalArgumentException illegalArgumentException) {
    
    } catch (InvocationTargetException invocationTargetException) {}
    if (paramComponent instanceof JInternalFrame)
      try {
        ((JInternalFrame)paramComponent).setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {} 
    Object object = jOptionPane.getValue();
    if (component != null && component.isShowing())
      component.requestFocus(); 
    if (object == null)
      return -1; 
    if (paramArrayOfObject == null)
      return (object instanceof Integer) ? ((Integer)object).intValue() : -1; 
    byte b = 0;
    int i = paramArrayOfObject.length;
    while (b < i) {
      if (paramArrayOfObject[b].equals(object))
        return b; 
      b++;
    } 
    return -1;
  }
  
  public static String showInternalInputDialog(Component paramComponent, Object paramObject) throws HeadlessException { return showInternalInputDialog(paramComponent, paramObject, UIManager.getString("OptionPane.inputDialogTitle", paramComponent), 3); }
  
  public static String showInternalInputDialog(Component paramComponent, Object paramObject, String paramString, int paramInt) throws HeadlessException { return (String)showInternalInputDialog(paramComponent, paramObject, paramString, paramInt, null, null, null); }
  
  public static Object showInternalInputDialog(Component paramComponent, Object paramObject1, String paramString, int paramInt, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2) throws HeadlessException {
    JOptionPane jOptionPane = new JOptionPane(paramObject1, paramInt, 2, paramIcon, null, null);
    jOptionPane.putClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP, Boolean.TRUE);
    Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    jOptionPane.setWantsInput(true);
    jOptionPane.setSelectionValues(paramArrayOfObject);
    jOptionPane.setInitialSelectionValue(paramObject2);
    JInternalFrame jInternalFrame = jOptionPane.createInternalFrame(paramComponent, paramString);
    jOptionPane.selectInitialValue();
    jInternalFrame.setVisible(true);
    if (jInternalFrame.isVisible() && !jInternalFrame.isShowing())
      for (Container container = jInternalFrame.getParent(); container != null; container = container.getParent()) {
        if (!container.isVisible())
          container.setVisible(true); 
      }  
    try {
      Method method = (Method)AccessController.doPrivileged(new ModalPrivilegedAction(Container.class, "startLWModal"));
      if (method != null)
        method.invoke(jInternalFrame, (Object[])null); 
    } catch (IllegalAccessException illegalAccessException) {
    
    } catch (IllegalArgumentException illegalArgumentException) {
    
    } catch (InvocationTargetException invocationTargetException) {}
    if (paramComponent instanceof JInternalFrame)
      try {
        ((JInternalFrame)paramComponent).setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {} 
    if (component != null && component.isShowing())
      component.requestFocus(); 
    Object object = jOptionPane.getInputValue();
    return (object == UNINITIALIZED_VALUE) ? null : object;
  }
  
  public JInternalFrame createInternalFrame(Component paramComponent, String paramString) {
    JDesktopPane jDesktopPane = getDesktopPaneForComponent(paramComponent);
    Container container;
    if (jDesktopPane == null && (paramComponent == null || (container = paramComponent.getParent()) == null))
      throw new RuntimeException("JOptionPane: parentComponent does not have a valid parent"); 
    final JInternalFrame iFrame = new JInternalFrame(paramString, false, true, false, false);
    jInternalFrame.putClientProperty("JInternalFrame.frameType", "optionDialog");
    jInternalFrame.putClientProperty("JInternalFrame.messageType", Integer.valueOf(getMessageType()));
    jInternalFrame.addInternalFrameListener(new InternalFrameAdapter() {
          public void internalFrameClosing(InternalFrameEvent param1InternalFrameEvent) {
            if (JOptionPane.this.getValue() == JOptionPane.UNINITIALIZED_VALUE)
              JOptionPane.this.setValue(null); 
          }
        });
    addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            if (iFrame.isVisible() && param1PropertyChangeEvent.getSource() == JOptionPane.this && param1PropertyChangeEvent.getPropertyName().equals("value")) {
              try {
                Method method = (Method)AccessController.doPrivileged(new JOptionPane.ModalPrivilegedAction(Container.class, "stopLWModal"));
                if (method != null)
                  method.invoke(iFrame, (Object[])null); 
              } catch (IllegalAccessException illegalAccessException) {
              
              } catch (IllegalArgumentException illegalArgumentException) {
              
              } catch (InvocationTargetException invocationTargetException) {}
              try {
                iFrame.setClosed(true);
              } catch (PropertyVetoException propertyVetoException) {}
              iFrame.setVisible(false);
            } 
          }
        });
    jInternalFrame.getContentPane().add(this, "Center");
    if (container instanceof JDesktopPane) {
      container.add(jInternalFrame, JLayeredPane.MODAL_LAYER);
    } else {
      container.add(jInternalFrame, "Center");
    } 
    Dimension dimension1 = jInternalFrame.getPreferredSize();
    Dimension dimension2 = container.getSize();
    Dimension dimension3 = paramComponent.getSize();
    jInternalFrame.setBounds((dimension2.width - dimension1.width) / 2, (dimension2.height - dimension1.height) / 2, dimension1.width, dimension1.height);
    Point point = SwingUtilities.convertPoint(paramComponent, 0, 0, container);
    int i = (dimension3.width - dimension1.width) / 2 + point.x;
    int j = (dimension3.height - dimension1.height) / 2 + point.y;
    int k = i + dimension1.width - dimension2.width;
    int m = j + dimension1.height - dimension2.height;
    i = Math.max((k > 0) ? (i - k) : i, 0);
    j = Math.max((m > 0) ? (j - m) : j, 0);
    jInternalFrame.setBounds(i, j, dimension1.width, dimension1.height);
    container.validate();
    try {
      jInternalFrame.setSelected(true);
    } catch (PropertyVetoException propertyVetoException) {}
    return jInternalFrame;
  }
  
  public static Frame getFrameForComponent(Component paramComponent) throws HeadlessException { return (paramComponent == null) ? getRootFrame() : ((paramComponent instanceof Frame) ? (Frame)paramComponent : getFrameForComponent(paramComponent.getParent())); }
  
  static Window getWindowForComponent(Component paramComponent) throws HeadlessException { return (paramComponent == null) ? getRootFrame() : ((paramComponent instanceof Frame || paramComponent instanceof Dialog) ? (Window)paramComponent : getWindowForComponent(paramComponent.getParent())); }
  
  public static JDesktopPane getDesktopPaneForComponent(Component paramComponent) { return (paramComponent == null) ? null : ((paramComponent instanceof JDesktopPane) ? (JDesktopPane)paramComponent : getDesktopPaneForComponent(paramComponent.getParent())); }
  
  public static void setRootFrame(Frame paramFrame) {
    if (paramFrame != null) {
      SwingUtilities.appContextPut(sharedFrameKey, paramFrame);
    } else {
      SwingUtilities.appContextRemove(sharedFrameKey);
    } 
  }
  
  public static Frame getRootFrame() throws HeadlessException {
    Frame frame = (Frame)SwingUtilities.appContextGet(sharedFrameKey);
    if (frame == null) {
      frame = SwingUtilities.getSharedOwnerFrame();
      SwingUtilities.appContextPut(sharedFrameKey, frame);
    } 
    return frame;
  }
  
  public JOptionPane() { this("JOptionPane message"); }
  
  public JOptionPane(Object paramObject) { this(paramObject, -1); }
  
  public JOptionPane(Object paramObject, int paramInt) { this(paramObject, paramInt, -1); }
  
  public JOptionPane(Object paramObject, int paramInt1, int paramInt2) { this(paramObject, paramInt1, paramInt2, null); }
  
  public JOptionPane(Object paramObject, int paramInt1, int paramInt2, Icon paramIcon) { this(paramObject, paramInt1, paramInt2, paramIcon, null); }
  
  public JOptionPane(Object paramObject, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject) { this(paramObject, paramInt1, paramInt2, paramIcon, paramArrayOfObject, null); }
  
  public JOptionPane(Object paramObject1, int paramInt1, int paramInt2, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject2) {
    this.message = paramObject1;
    this.options = paramArrayOfObject;
    this.initialValue = paramObject2;
    this.icon = paramIcon;
    setMessageType(paramInt1);
    setOptionType(paramInt2);
    this.value = UNINITIALIZED_VALUE;
    this.inputValue = UNINITIALIZED_VALUE;
    updateUI();
  }
  
  public void setUI(OptionPaneUI paramOptionPaneUI) {
    if (this.ui != paramOptionPaneUI) {
      setUI(paramOptionPaneUI);
      invalidate();
    } 
  }
  
  public OptionPaneUI getUI() { return (OptionPaneUI)this.ui; }
  
  public void updateUI() { setUI((OptionPaneUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "OptionPaneUI"; }
  
  public void setMessage(Object paramObject) {
    Object object = this.message;
    this.message = paramObject;
    firePropertyChange("message", object, this.message);
  }
  
  public Object getMessage() { return this.message; }
  
  public void setIcon(Icon paramIcon) {
    Icon icon1 = this.icon;
    this.icon = paramIcon;
    firePropertyChange("icon", icon1, this.icon);
  }
  
  public Icon getIcon() { return this.icon; }
  
  public void setValue(Object paramObject) {
    Object object = this.value;
    this.value = paramObject;
    firePropertyChange("value", object, this.value);
  }
  
  public Object getValue() { return this.value; }
  
  public void setOptions(Object[] paramArrayOfObject) {
    Object[] arrayOfObject = this.options;
    this.options = paramArrayOfObject;
    firePropertyChange("options", arrayOfObject, this.options);
  }
  
  public Object[] getOptions() {
    if (this.options != null) {
      int i = this.options.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.options, 0, arrayOfObject, 0, i);
      return arrayOfObject;
    } 
    return this.options;
  }
  
  public void setInitialValue(Object paramObject) {
    Object object = this.initialValue;
    this.initialValue = paramObject;
    firePropertyChange("initialValue", object, this.initialValue);
  }
  
  public Object getInitialValue() { return this.initialValue; }
  
  public void setMessageType(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2 && paramInt != 3 && paramInt != -1)
      throw new RuntimeException("JOptionPane: type must be one of JOptionPane.ERROR_MESSAGE, JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE, JOptionPane.QUESTION_MESSAGE or JOptionPane.PLAIN_MESSAGE"); 
    int i = this.messageType;
    this.messageType = paramInt;
    firePropertyChange("messageType", i, this.messageType);
  }
  
  public int getMessageType() { return this.messageType; }
  
  public void setOptionType(int paramInt) {
    if (paramInt != -1 && paramInt != 0 && paramInt != 1 && paramInt != 2)
      throw new RuntimeException("JOptionPane: option type must be one of JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION or JOptionPane.OK_CANCEL_OPTION"); 
    int i = this.optionType;
    this.optionType = paramInt;
    firePropertyChange("optionType", i, this.optionType);
  }
  
  public int getOptionType() { return this.optionType; }
  
  public void setSelectionValues(Object[] paramArrayOfObject) {
    Object[] arrayOfObject = this.selectionValues;
    this.selectionValues = paramArrayOfObject;
    firePropertyChange("selectionValues", arrayOfObject, paramArrayOfObject);
    if (this.selectionValues != null)
      setWantsInput(true); 
  }
  
  public Object[] getSelectionValues() { return this.selectionValues; }
  
  public void setInitialSelectionValue(Object paramObject) {
    Object object = this.initialSelectionValue;
    this.initialSelectionValue = paramObject;
    firePropertyChange("initialSelectionValue", object, paramObject);
  }
  
  public Object getInitialSelectionValue() { return this.initialSelectionValue; }
  
  public void setInputValue(Object paramObject) {
    Object object = this.inputValue;
    this.inputValue = paramObject;
    firePropertyChange("inputValue", object, paramObject);
  }
  
  public Object getInputValue() { return this.inputValue; }
  
  public int getMaxCharactersPerLineCount() { return Integer.MAX_VALUE; }
  
  public void setWantsInput(boolean paramBoolean) {
    boolean bool = this.wantsInput;
    this.wantsInput = paramBoolean;
    firePropertyChange("wantsInput", bool, paramBoolean);
  }
  
  public boolean getWantsInput() { return this.wantsInput; }
  
  public void selectInitialValue() {
    OptionPaneUI optionPaneUI = getUI();
    if (optionPaneUI != null)
      optionPaneUI.selectInitialValue(this); 
  }
  
  private static int styleFromMessageType(int paramInt) {
    switch (paramInt) {
      case 0:
        return 4;
      case 3:
        return 7;
      case 2:
        return 8;
      case 1:
        return 3;
    } 
    return 2;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if (this.icon != null && this.icon instanceof java.io.Serializable) {
      vector.addElement("icon");
      vector.addElement(this.icon);
    } 
    if (this.message != null && this.message instanceof java.io.Serializable) {
      vector.addElement("message");
      vector.addElement(this.message);
    } 
    if (this.options != null) {
      Vector vector1 = new Vector();
      int i = 0;
      int j = this.options.length;
      while (i < j) {
        if (this.options[i] instanceof java.io.Serializable)
          vector1.addElement(this.options[i]); 
        i++;
      } 
      if (vector1.size() > 0) {
        i = vector1.size();
        Object[] arrayOfObject = new Object[i];
        vector1.copyInto(arrayOfObject);
        vector.addElement("options");
        vector.addElement(arrayOfObject);
      } 
    } 
    if (this.initialValue != null && this.initialValue instanceof java.io.Serializable) {
      vector.addElement("initialValue");
      vector.addElement(this.initialValue);
    } 
    if (this.value != null && this.value instanceof java.io.Serializable) {
      vector.addElement("value");
      vector.addElement(this.value);
    } 
    if (this.selectionValues != null) {
      boolean bool = true;
      byte b = 0;
      int i = this.selectionValues.length;
      while (b < i) {
        if (this.selectionValues[b] != null && !(this.selectionValues[b] instanceof java.io.Serializable)) {
          bool = false;
          break;
        } 
        b++;
      } 
      if (bool) {
        vector.addElement("selectionValues");
        vector.addElement(this.selectionValues);
      } 
    } 
    if (this.inputValue != null && this.inputValue instanceof java.io.Serializable) {
      vector.addElement("inputValue");
      vector.addElement(this.inputValue);
    } 
    if (this.initialSelectionValue != null && this.initialSelectionValue instanceof java.io.Serializable) {
      vector.addElement("initialSelectionValue");
      vector.addElement(this.initialSelectionValue);
    } 
    paramObjectOutputStream.writeObject(vector);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Vector vector = (Vector)paramObjectInputStream.readObject();
    byte b = 0;
    int i = vector.size();
    if (b < i && vector.elementAt(b).equals("icon")) {
      this.icon = (Icon)vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("message")) {
      this.message = vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("options")) {
      this.options = (Object[])vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("initialValue")) {
      this.initialValue = vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("value")) {
      this.value = vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("selectionValues")) {
      this.selectionValues = (Object[])vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("inputValue")) {
      this.inputValue = vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("initialSelectionValue")) {
      this.initialSelectionValue = vector.elementAt(++b);
      b++;
    } 
    if (getUIClassID().equals("OptionPaneUI")) {
      byte b1 = JComponent.getWriteObjCounter(this);
      b1 = (byte)(b1 - 1);
      JComponent.setWriteObjCounter(this, b1);
      if (b1 == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str5;
    String str4;
    String str1 = (this.icon != null) ? this.icon.toString() : "";
    String str2 = (this.initialValue != null) ? this.initialValue.toString() : "";
    String str3 = (this.message != null) ? this.message.toString() : "";
    if (this.messageType == 0) {
      str4 = "ERROR_MESSAGE";
    } else if (this.messageType == 1) {
      str4 = "INFORMATION_MESSAGE";
    } else if (this.messageType == 2) {
      str4 = "WARNING_MESSAGE";
    } else if (this.messageType == 3) {
      str4 = "QUESTION_MESSAGE";
    } else if (this.messageType == -1) {
      str4 = "PLAIN_MESSAGE";
    } else {
      str4 = "";
    } 
    if (this.optionType == -1) {
      str5 = "DEFAULT_OPTION";
    } else if (this.optionType == 0) {
      str5 = "YES_NO_OPTION";
    } else if (this.optionType == 1) {
      str5 = "YES_NO_CANCEL_OPTION";
    } else if (this.optionType == 2) {
      str5 = "OK_CANCEL_OPTION";
    } else {
      str5 = "";
    } 
    String str6 = this.wantsInput ? "true" : "false";
    return super.paramString() + ",icon=" + str1 + ",initialValue=" + str2 + ",message=" + str3 + ",messageType=" + str4 + ",optionType=" + str5 + ",wantsInput=" + str6;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJOptionPane(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJOptionPane extends JComponent.AccessibleJComponent {
    protected AccessibleJOptionPane() { super(JOptionPane.this); }
    
    public AccessibleRole getAccessibleRole() {
      switch (JOptionPane.this.messageType) {
        case 0:
        case 1:
        case 2:
          return AccessibleRole.ALERT;
      } 
      return AccessibleRole.OPTION_PANE;
    }
  }
  
  private static class ModalPrivilegedAction extends Object implements PrivilegedAction<Method> {
    private Class<?> clazz;
    
    private String methodName;
    
    public ModalPrivilegedAction(Class<?> param1Class, String param1String) {
      this.clazz = param1Class;
      this.methodName = param1String;
    }
    
    public Method run() {
      Method method = null;
      try {
        method = this.clazz.getDeclaredMethod(this.methodName, (Class[])null);
      } catch (NoSuchMethodException noSuchMethodException) {}
      if (method != null)
        method.setAccessible(true); 
      return method;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JOptionPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */