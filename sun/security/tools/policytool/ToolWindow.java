package sun.security.tools.policytool;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessController;
import java.text.MessageFormat;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import sun.security.action.GetPropertyAction;

class ToolWindow extends JFrame {
  private static final long serialVersionUID = 5682568601210376777L;
  
  static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
  
  public static final Insets TOP_PADDING = new Insets(25, 0, 0, 0);
  
  public static final Insets BOTTOM_PADDING = new Insets(0, 0, 25, 0);
  
  public static final Insets LITE_BOTTOM_PADDING = new Insets(0, 0, 10, 0);
  
  public static final Insets LR_PADDING = new Insets(0, 10, 0, 10);
  
  public static final Insets TOP_BOTTOM_PADDING = new Insets(15, 0, 15, 0);
  
  public static final Insets L_TOP_BOTTOM_PADDING = new Insets(5, 10, 15, 0);
  
  public static final Insets LR_TOP_BOTTOM_PADDING = new Insets(15, 4, 15, 4);
  
  public static final Insets LR_BOTTOM_PADDING = new Insets(0, 10, 5, 10);
  
  public static final Insets L_BOTTOM_PADDING = new Insets(0, 10, 5, 0);
  
  public static final Insets R_BOTTOM_PADDING = new Insets(0, 0, 25, 5);
  
  public static final Insets R_PADDING = new Insets(0, 0, 0, 5);
  
  public static final String NEW_POLICY_FILE = "New";
  
  public static final String OPEN_POLICY_FILE = "Open";
  
  public static final String SAVE_POLICY_FILE = "Save";
  
  public static final String SAVE_AS_POLICY_FILE = "Save.As";
  
  public static final String VIEW_WARNINGS = "View.Warning.Log";
  
  public static final String QUIT = "Exit";
  
  public static final String ADD_POLICY_ENTRY = "Add.Policy.Entry";
  
  public static final String EDIT_POLICY_ENTRY = "Edit.Policy.Entry";
  
  public static final String REMOVE_POLICY_ENTRY = "Remove.Policy.Entry";
  
  public static final String EDIT_KEYSTORE = "Edit";
  
  public static final String ADD_PUBKEY_ALIAS = "Add.Public.Key.Alias";
  
  public static final String REMOVE_PUBKEY_ALIAS = "Remove.Public.Key.Alias";
  
  public static final int MW_FILENAME_LABEL = 0;
  
  public static final int MW_FILENAME_TEXTFIELD = 1;
  
  public static final int MW_PANEL = 2;
  
  public static final int MW_ADD_BUTTON = 0;
  
  public static final int MW_EDIT_BUTTON = 1;
  
  public static final int MW_REMOVE_BUTTON = 2;
  
  public static final int MW_POLICY_LIST = 3;
  
  static final int TEXTFIELD_HEIGHT = ((new JComboBox()).getPreferredSize()).height;
  
  private PolicyTool tool;
  
  private int shortCutModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  
  ToolWindow(PolicyTool paramPolicyTool) { this.tool = paramPolicyTool; }
  
  public Component getComponent(int paramInt) {
    Component component = getContentPane().getComponent(paramInt);
    if (component instanceof JScrollPane)
      component = ((JScrollPane)component).getViewport().getView(); 
    return component;
  }
  
  private void initWindow() {
    setDefaultCloseOperation(0);
    JMenuBar jMenuBar = new JMenuBar();
    JMenu jMenu = new JMenu();
    configureButton(jMenu, "File");
    FileMenuListener fileMenuListener = new FileMenuListener(this.tool, this);
    addMenuItem(jMenu, "New", fileMenuListener, "N");
    addMenuItem(jMenu, "Open", fileMenuListener, "O");
    addMenuItem(jMenu, "Save", fileMenuListener, "S");
    addMenuItem(jMenu, "Save.As", fileMenuListener, null);
    addMenuItem(jMenu, "View.Warning.Log", fileMenuListener, null);
    addMenuItem(jMenu, "Exit", fileMenuListener, null);
    jMenuBar.add(jMenu);
    jMenu = new JMenu();
    configureButton(jMenu, "KeyStore");
    MainWindowListener mainWindowListener = new MainWindowListener(this.tool, this);
    addMenuItem(jMenu, "Edit", mainWindowListener, null);
    jMenuBar.add(jMenu);
    setJMenuBar(jMenuBar);
    ((JPanel)getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
    JLabel jLabel = new JLabel(PolicyTool.getMessage("Policy.File."));
    addNewComponent(this, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
    JTextField jTextField = new JTextField(50);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Policy.File."));
    jTextField.setEditable(false);
    addNewComponent(this, jTextField, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_TOP_BOTTOM_PADDING);
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    JButton jButton = new JButton();
    configureButton(jButton, "Add.Policy.Entry");
    jButton.addActionListener(new MainWindowListener(this.tool, this));
    addNewComponent(jPanel, jButton, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
    jButton = new JButton();
    configureButton(jButton, "Edit.Policy.Entry");
    jButton.addActionListener(new MainWindowListener(this.tool, this));
    addNewComponent(jPanel, jButton, 1, 1, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
    jButton = new JButton();
    configureButton(jButton, "Remove.Policy.Entry");
    jButton.addActionListener(new MainWindowListener(this.tool, this));
    addNewComponent(jPanel, jButton, 2, 2, 0, 1, 1, 0.0D, 0.0D, 1, LR_PADDING);
    addNewComponent(this, jPanel, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
    String str = this.tool.getPolicyFileName();
    if (str == null) {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("user.home"));
      str = str1 + File.separatorChar + ".java.policy";
    } 
    try {
      this.tool.openPolicy(str);
      DefaultListModel defaultListModel = new DefaultListModel();
      JList jList = new JList(defaultListModel);
      jList.setVisibleRowCount(15);
      jList.setSelectionMode(0);
      jList.addMouseListener(new PolicyListListener(this.tool, this));
      PolicyEntry[] arrayOfPolicyEntry = this.tool.getEntry();
      if (arrayOfPolicyEntry != null)
        for (byte b = 0; b < arrayOfPolicyEntry.length; b++)
          defaultListModel.addElement(arrayOfPolicyEntry[b].headerToString());  
      JTextField jTextField1 = (JTextField)getComponent(1);
      jTextField1.setText(str);
      initPolicyList(jList);
    } catch (FileNotFoundException fileNotFoundException) {
      JList jList = new JList(new DefaultListModel());
      jList.setVisibleRowCount(15);
      jList.setSelectionMode(0);
      jList.addMouseListener(new PolicyListListener(this.tool, this));
      initPolicyList(jList);
      this.tool.setPolicyFileName(null);
      this.tool.modified = false;
      this.tool.warnings.addElement(fileNotFoundException.toString());
    } catch (Exception exception) {
      JList jList = new JList(new DefaultListModel());
      jList.setVisibleRowCount(15);
      jList.setSelectionMode(0);
      jList.addMouseListener(new PolicyListListener(this.tool, this));
      initPolicyList(jList);
      this.tool.setPolicyFileName(null);
      this.tool.modified = false;
      MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
      Object[] arrayOfObject = { str, exception.toString() };
      displayErrorDialog(null, messageFormat.format(arrayOfObject));
    } 
  }
  
  private void addMenuItem(JMenu paramJMenu, String paramString1, ActionListener paramActionListener, String paramString2) {
    JMenuItem jMenuItem = new JMenuItem();
    configureButton(jMenuItem, paramString1);
    if (PolicyTool.rb.containsKey(paramString1 + ".accelerator"))
      paramString2 = PolicyTool.getMessage(paramString1 + ".accelerator"); 
    if (paramString2 != null && !paramString2.isEmpty()) {
      KeyStroke keyStroke;
      if (paramString2.length() == 1) {
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(paramString2.charAt(0)), this.shortCutModifier);
      } else {
        keyStroke = KeyStroke.getKeyStroke(paramString2);
      } 
      jMenuItem.setAccelerator(keyStroke);
    } 
    jMenuItem.addActionListener(paramActionListener);
    paramJMenu.add(jMenuItem);
  }
  
  static void configureButton(AbstractButton paramAbstractButton, String paramString) {
    paramAbstractButton.setText(PolicyTool.getMessage(paramString));
    paramAbstractButton.setActionCommand(paramString);
    int i = PolicyTool.getMnemonicInt(paramString);
    if (i > 0) {
      paramAbstractButton.setMnemonic(i);
      paramAbstractButton.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(paramString));
    } 
  }
  
  static void configureLabelFor(JLabel paramJLabel, JComponent paramJComponent, String paramString) {
    paramJLabel.setText(PolicyTool.getMessage(paramString));
    paramJLabel.setLabelFor(paramJComponent);
    int i = PolicyTool.getMnemonicInt(paramString);
    if (i > 0) {
      paramJLabel.setDisplayedMnemonic(i);
      paramJLabel.setDisplayedMnemonicIndex(PolicyTool.getDisplayedMnemonicIndex(paramString));
    } 
  }
  
  void addNewComponent(Container paramContainer, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, int paramInt6, Insets paramInsets) {
    if (paramContainer instanceof JFrame) {
      paramContainer = ((JFrame)paramContainer).getContentPane();
    } else if (paramContainer instanceof JDialog) {
      paramContainer = ((JDialog)paramContainer).getContentPane();
    } 
    paramContainer.add(paramJComponent, paramInt1);
    GridBagLayout gridBagLayout = (GridBagLayout)paramContainer.getLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = paramInt2;
    gridBagConstraints.gridy = paramInt3;
    gridBagConstraints.gridwidth = paramInt4;
    gridBagConstraints.gridheight = paramInt5;
    gridBagConstraints.weightx = paramDouble1;
    gridBagConstraints.weighty = paramDouble2;
    gridBagConstraints.fill = paramInt6;
    if (paramInsets != null)
      gridBagConstraints.insets = paramInsets; 
    gridBagLayout.setConstraints(paramJComponent, gridBagConstraints);
  }
  
  void addNewComponent(Container paramContainer, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, int paramInt6) { addNewComponent(paramContainer, paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramInt6, null); }
  
  void initPolicyList(JList paramJList) {
    JScrollPane jScrollPane = new JScrollPane(paramJList);
    addNewComponent(this, jScrollPane, 3, 0, 3, 2, 1, 1.0D, 1.0D, 1);
  }
  
  void replacePolicyList(JList paramJList) {
    JList jList = (JList)getComponent(3);
    jList.setModel(paramJList.getModel());
  }
  
  void displayToolWindow(String[] paramArrayOfString) {
    setTitle(PolicyTool.getMessage("Policy.Tool"));
    setResizable(true);
    addWindowListener(new ToolWindowListener(this.tool, this));
    getContentPane().setLayout(new GridBagLayout());
    initWindow();
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
    if (this.tool.newWarning == true)
      displayStatusDialog(this, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.")); 
  }
  
  void displayErrorDialog(Window paramWindow, String paramString) {
    ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Error"), this.tool, this, true);
    Point point = (paramWindow == null) ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    toolDialog.setLayout(new GridBagLayout());
    JLabel jLabel = new JLabel(paramString);
    addNewComponent(toolDialog, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
    JButton jButton = new JButton(PolicyTool.getMessage("OK"));
    ErrorOKButtonListener errorOKButtonListener = new ErrorOKButtonListener(toolDialog);
    jButton.addActionListener(errorOKButtonListener);
    addNewComponent(toolDialog, jButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
    toolDialog.getRootPane().setDefaultButton(jButton);
    toolDialog.getRootPane().registerKeyboardAction(errorOKButtonListener, escKey, 2);
    toolDialog.pack();
    toolDialog.setLocationRelativeTo(paramWindow);
    toolDialog.setVisible(true);
  }
  
  void displayErrorDialog(Window paramWindow, Throwable paramThrowable) {
    if (paramThrowable instanceof NoDisplayException)
      return; 
    displayErrorDialog(paramWindow, paramThrowable.toString());
  }
  
  void displayStatusDialog(Window paramWindow, String paramString) {
    ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Status"), this.tool, this, true);
    Point point = (paramWindow == null) ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    toolDialog.setLayout(new GridBagLayout());
    JLabel jLabel = new JLabel(paramString);
    addNewComponent(toolDialog, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
    JButton jButton = new JButton(PolicyTool.getMessage("OK"));
    StatusOKButtonListener statusOKButtonListener = new StatusOKButtonListener(toolDialog);
    jButton.addActionListener(statusOKButtonListener);
    addNewComponent(toolDialog, jButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
    toolDialog.getRootPane().setDefaultButton(jButton);
    toolDialog.getRootPane().registerKeyboardAction(statusOKButtonListener, escKey, 2);
    toolDialog.pack();
    toolDialog.setLocationRelativeTo(paramWindow);
    toolDialog.setVisible(true);
  }
  
  void displayWarningLog(Window paramWindow) {
    ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Warning"), this.tool, this, true);
    Point point = (paramWindow == null) ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    toolDialog.setLayout(new GridBagLayout());
    JTextArea jTextArea = new JTextArea();
    jTextArea.setEditable(false);
    for (byte b = 0; b < this.tool.warnings.size(); b++) {
      jTextArea.append((String)this.tool.warnings.elementAt(b));
      jTextArea.append(PolicyTool.getMessage("NEWLINE"));
    } 
    addNewComponent(toolDialog, jTextArea, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, BOTTOM_PADDING);
    jTextArea.setFocusable(false);
    JButton jButton = new JButton(PolicyTool.getMessage("OK"));
    CancelButtonListener cancelButtonListener = new CancelButtonListener(toolDialog);
    jButton.addActionListener(cancelButtonListener);
    addNewComponent(toolDialog, jButton, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
    toolDialog.getRootPane().setDefaultButton(jButton);
    toolDialog.getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
    toolDialog.pack();
    toolDialog.setLocationRelativeTo(paramWindow);
    toolDialog.setVisible(true);
  }
  
  char displayYesNoDialog(Window paramWindow, String paramString1, String paramString2, String paramString3, String paramString4) {
    final ToolDialog tw = new ToolDialog(paramString1, this.tool, this, true);
    Point point = (paramWindow == null) ? getLocationOnScreen() : paramWindow.getLocationOnScreen();
    toolDialog.setLayout(new GridBagLayout());
    JTextArea jTextArea = new JTextArea(paramString2, 10, 50);
    jTextArea.setEditable(false);
    jTextArea.setLineWrap(true);
    jTextArea.setWrapStyleWord(true);
    JScrollPane jScrollPane = new JScrollPane(jTextArea, 20, 31);
    addNewComponent(toolDialog, jScrollPane, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1);
    jTextArea.setFocusable(false);
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    final StringBuffer chooseResult = new StringBuffer();
    JButton jButton = new JButton(paramString3);
    jButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            chooseResult.append('Y');
            tw.setVisible(false);
            tw.dispose();
          }
        });
    addNewComponent(jPanel, jButton, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
    jButton = new JButton(paramString4);
    jButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            chooseResult.append('N');
            tw.setVisible(false);
            tw.dispose();
          }
        });
    addNewComponent(jPanel, jButton, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, LR_PADDING);
    addNewComponent(toolDialog, jPanel, 1, 0, 1, 1, 1, 0.0D, 0.0D, 3);
    toolDialog.pack();
    toolDialog.setLocationRelativeTo(paramWindow);
    toolDialog.setVisible(true);
    return (stringBuffer.length() > 0) ? stringBuffer.charAt(0) : 78;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ToolWindow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */