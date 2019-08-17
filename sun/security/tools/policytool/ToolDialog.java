package sun.security.tools.policytool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.InvalidParameterException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import sun.security.provider.PolicyParser;

class ToolDialog extends JDialog {
  private static final long serialVersionUID = -372244357011301190L;
  
  static final KeyStroke escKey = KeyStroke.getKeyStroke(27, 0);
  
  public static final int NOACTION = 0;
  
  public static final int QUIT = 1;
  
  public static final int NEW = 2;
  
  public static final int OPEN = 3;
  
  public static final String ALL_PERM_CLASS = "java.security.AllPermission";
  
  public static final String FILE_PERM_CLASS = "java.io.FilePermission";
  
  public static final String X500_PRIN_CLASS = "javax.security.auth.x500.X500Principal";
  
  public static final String PERM = PolicyTool.getMessage("Permission.");
  
  public static final String PRIN_TYPE = PolicyTool.getMessage("Principal.Type.");
  
  public static final String PRIN_NAME = PolicyTool.getMessage("Principal.Name.");
  
  public static final String PERM_NAME = PolicyTool.getMessage("Target.Name.");
  
  public static final String PERM_ACTIONS = PolicyTool.getMessage("Actions.");
  
  public static final int PE_CODEBASE_LABEL = 0;
  
  public static final int PE_CODEBASE_TEXTFIELD = 1;
  
  public static final int PE_SIGNEDBY_LABEL = 2;
  
  public static final int PE_SIGNEDBY_TEXTFIELD = 3;
  
  public static final int PE_PANEL0 = 4;
  
  public static final int PE_ADD_PRIN_BUTTON = 0;
  
  public static final int PE_EDIT_PRIN_BUTTON = 1;
  
  public static final int PE_REMOVE_PRIN_BUTTON = 2;
  
  public static final int PE_PRIN_LABEL = 5;
  
  public static final int PE_PRIN_LIST = 6;
  
  public static final int PE_PANEL1 = 7;
  
  public static final int PE_ADD_PERM_BUTTON = 0;
  
  public static final int PE_EDIT_PERM_BUTTON = 1;
  
  public static final int PE_REMOVE_PERM_BUTTON = 2;
  
  public static final int PE_PERM_LIST = 8;
  
  public static final int PE_PANEL2 = 9;
  
  public static final int PE_CANCEL_BUTTON = 1;
  
  public static final int PE_DONE_BUTTON = 0;
  
  public static final int PRD_DESC_LABEL = 0;
  
  public static final int PRD_PRIN_CHOICE = 1;
  
  public static final int PRD_PRIN_TEXTFIELD = 2;
  
  public static final int PRD_NAME_LABEL = 3;
  
  public static final int PRD_NAME_TEXTFIELD = 4;
  
  public static final int PRD_CANCEL_BUTTON = 6;
  
  public static final int PRD_OK_BUTTON = 5;
  
  public static final int PD_DESC_LABEL = 0;
  
  public static final int PD_PERM_CHOICE = 1;
  
  public static final int PD_PERM_TEXTFIELD = 2;
  
  public static final int PD_NAME_CHOICE = 3;
  
  public static final int PD_NAME_TEXTFIELD = 4;
  
  public static final int PD_ACTIONS_CHOICE = 5;
  
  public static final int PD_ACTIONS_TEXTFIELD = 6;
  
  public static final int PD_SIGNEDBY_LABEL = 7;
  
  public static final int PD_SIGNEDBY_TEXTFIELD = 8;
  
  public static final int PD_CANCEL_BUTTON = 10;
  
  public static final int PD_OK_BUTTON = 9;
  
  public static final int EDIT_KEYSTORE = 0;
  
  public static final int KSD_NAME_LABEL = 0;
  
  public static final int KSD_NAME_TEXTFIELD = 1;
  
  public static final int KSD_TYPE_LABEL = 2;
  
  public static final int KSD_TYPE_TEXTFIELD = 3;
  
  public static final int KSD_PROVIDER_LABEL = 4;
  
  public static final int KSD_PROVIDER_TEXTFIELD = 5;
  
  public static final int KSD_PWD_URL_LABEL = 6;
  
  public static final int KSD_PWD_URL_TEXTFIELD = 7;
  
  public static final int KSD_CANCEL_BUTTON = 9;
  
  public static final int KSD_OK_BUTTON = 8;
  
  public static final int USC_LABEL = 0;
  
  public static final int USC_PANEL = 1;
  
  public static final int USC_YES_BUTTON = 0;
  
  public static final int USC_NO_BUTTON = 1;
  
  public static final int USC_CANCEL_BUTTON = 2;
  
  public static final int CRPE_LABEL1 = 0;
  
  public static final int CRPE_LABEL2 = 1;
  
  public static final int CRPE_PANEL = 2;
  
  public static final int CRPE_PANEL_OK = 0;
  
  public static final int CRPE_PANEL_CANCEL = 1;
  
  private static final int PERMISSION = 0;
  
  private static final int PERMISSION_NAME = 1;
  
  private static final int PERMISSION_ACTIONS = 2;
  
  private static final int PERMISSION_SIGNEDBY = 3;
  
  private static final int PRINCIPAL_TYPE = 4;
  
  private static final int PRINCIPAL_NAME = 5;
  
  static final int TEXTFIELD_HEIGHT = ((new JComboBox()).getPreferredSize()).height;
  
  public static ArrayList<Perm> PERM_ARRAY = new ArrayList();
  
  public static ArrayList<Prin> PRIN_ARRAY;
  
  PolicyTool tool;
  
  ToolWindow tw;
  
  ToolDialog(String paramString, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, boolean paramBoolean) {
    super(paramToolWindow, paramBoolean);
    setTitle(paramString);
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    addWindowListener(new ChildWindowListener(this));
    ((JPanel)getContentPane()).setBorder(new EmptyBorder(6, 6, 6, 6));
  }
  
  public Component getComponent(int paramInt) {
    Component component = getContentPane().getComponent(paramInt);
    if (component instanceof JScrollPane)
      component = ((JScrollPane)component).getViewport().getView(); 
    return component;
  }
  
  static Perm getPerm(String paramString, boolean paramBoolean) {
    for (byte b = 0; b < PERM_ARRAY.size(); b++) {
      Perm perm = (Perm)PERM_ARRAY.get(b);
      if (paramBoolean) {
        if (perm.FULL_CLASS.equals(paramString))
          return perm; 
      } else if (perm.CLASS.equals(paramString)) {
        return perm;
      } 
    } 
    return null;
  }
  
  static Prin getPrin(String paramString, boolean paramBoolean) {
    for (byte b = 0; b < PRIN_ARRAY.size(); b++) {
      Prin prin = (Prin)PRIN_ARRAY.get(b);
      if (paramBoolean) {
        if (prin.FULL_CLASS.equals(paramString))
          return prin; 
      } else if (prin.CLASS.equals(paramString)) {
        return prin;
      } 
    } 
    return null;
  }
  
  void displayPolicyEntryDialog(boolean paramBoolean) {
    int i = 0;
    PolicyEntry[] arrayOfPolicyEntry = null;
    TaggedList taggedList1 = new TaggedList(3, false);
    taggedList1.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Principal.List"));
    taggedList1.addMouseListener(new EditPrinButtonListener(this.tool, this.tw, this, paramBoolean));
    TaggedList taggedList2 = new TaggedList(10, false);
    taggedList2.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Permission.List"));
    taggedList2.addMouseListener(new EditPermButtonListener(this.tool, this.tw, this, paramBoolean));
    Point point = this.tw.getLocationOnScreen();
    setLayout(new GridBagLayout());
    setResizable(true);
    if (paramBoolean) {
      arrayOfPolicyEntry = this.tool.getEntry();
      JList jList = (JList)this.tw.getComponent(3);
      i = jList.getSelectedIndex();
      LinkedList linkedList = (arrayOfPolicyEntry[i].getGrantEntry()).principals;
      for (byte b1 = 0; b1 < linkedList.size(); b1++) {
        Object object = null;
        PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)linkedList.get(b1);
        taggedList1.addTaggedItem(PrincipalEntryToUserFriendlyString(principalEntry), principalEntry);
      } 
      Vector vector = (arrayOfPolicyEntry[i].getGrantEntry()).permissionEntries;
      for (byte b2 = 0; b2 < vector.size(); b2++) {
        Object object = null;
        PolicyParser.PermissionEntry permissionEntry = (PolicyParser.PermissionEntry)vector.elementAt(b2);
        taggedList2.addTaggedItem(PermissionEntryToUserFriendlyString(permissionEntry), permissionEntry);
      } 
    } 
    JLabel jLabel = new JLabel();
    this.tw.addNewComponent(this, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
    JTextField jTextField = paramBoolean ? new JTextField((arrayOfPolicyEntry[i].getGrantEntry()).codeBase) : new JTextField();
    ToolWindow.configureLabelFor(jLabel, jTextField, "CodeBase.");
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Code.Base"));
    this.tw.addNewComponent(this, jTextField, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1);
    jLabel = new JLabel();
    this.tw.addNewComponent(this, jLabel, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_PADDING);
    jTextField = paramBoolean ? new JTextField((arrayOfPolicyEntry[i].getGrantEntry()).signedBy) : new JTextField();
    ToolWindow.configureLabelFor(jLabel, jTextField, "SignedBy.");
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
    this.tw.addNewComponent(this, jTextField, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1);
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    JButton jButton1 = new JButton();
    ToolWindow.configureButton(jButton1, "Add.Principal");
    jButton1.addActionListener(new AddPrinButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton1, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
    jButton1 = new JButton();
    ToolWindow.configureButton(jButton1, "Edit.Principal");
    jButton1.addActionListener(new EditPrinButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton1, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
    jButton1 = new JButton();
    ToolWindow.configureButton(jButton1, "Remove.Principal");
    jButton1.addActionListener(new RemovePrinButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton1, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
    this.tw.addNewComponent(this, jPanel, 4, 1, 2, 1, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
    jLabel = new JLabel();
    this.tw.addNewComponent(this, jLabel, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
    JScrollPane jScrollPane = new JScrollPane(taggedList1);
    ToolWindow.configureLabelFor(jLabel, jScrollPane, "Principals.");
    this.tw.addNewComponent(this, jScrollPane, 6, 1, 3, 3, 1, 0.0D, taggedList1.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
    jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    jButton1 = new JButton();
    ToolWindow.configureButton(jButton1, ".Add.Permission");
    jButton1.addActionListener(new AddPermButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton1, 0, 0, 0, 1, 1, 100.0D, 0.0D, 2);
    jButton1 = new JButton();
    ToolWindow.configureButton(jButton1, ".Edit.Permission");
    jButton1.addActionListener(new EditPermButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton1, 1, 1, 0, 1, 1, 100.0D, 0.0D, 2);
    jButton1 = new JButton();
    ToolWindow.configureButton(jButton1, "Remove.Permission");
    jButton1.addActionListener(new RemovePermButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton1, 2, 2, 0, 1, 1, 100.0D, 0.0D, 2);
    this.tw.addNewComponent(this, jPanel, 7, 0, 4, 2, 1, 0.0D, 0.0D, 2, ToolWindow.LITE_BOTTOM_PADDING);
    jScrollPane = new JScrollPane(taggedList2);
    this.tw.addNewComponent(this, jScrollPane, 8, 0, 5, 3, 1, 0.0D, taggedList2.getVisibleRowCount(), 1, ToolWindow.BOTTOM_PADDING);
    jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    JButton jButton2 = new JButton(PolicyTool.getMessage("Done"));
    jButton2.addActionListener(new AddEntryDoneButtonListener(this.tool, this.tw, this, paramBoolean));
    this.tw.addNewComponent(jPanel, jButton2, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    JButton jButton3 = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
    jButton3.addActionListener(cancelButtonListener);
    this.tw.addNewComponent(jPanel, jButton3, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    this.tw.addNewComponent(this, jPanel, 9, 0, 6, 2, 1, 0.0D, 0.0D, 3);
    getRootPane().setDefaultButton(jButton2);
    getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
    pack();
    setLocationRelativeTo(this.tw);
    setVisible(true);
  }
  
  PolicyEntry getPolicyEntryFromDialog() throws InvalidParameterException, MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, Exception {
    JTextField jTextField = (JTextField)getComponent(1);
    String str1 = null;
    if (!jTextField.getText().trim().equals(""))
      str1 = new String(jTextField.getText().trim()); 
    jTextField = (JTextField)getComponent(3);
    String str2 = null;
    if (!jTextField.getText().trim().equals(""))
      str2 = new String(jTextField.getText().trim()); 
    PolicyParser.GrantEntry grantEntry = new PolicyParser.GrantEntry(str2, str1);
    LinkedList linkedList = new LinkedList();
    TaggedList taggedList1 = (TaggedList)getComponent(6);
    for (byte b1 = 0; b1 < taggedList1.getModel().getSize(); b1++)
      linkedList.add((PolicyParser.PrincipalEntry)taggedList1.getObject(b1)); 
    grantEntry.principals = linkedList;
    Vector vector = new Vector();
    TaggedList taggedList2 = (TaggedList)getComponent(8);
    for (byte b2 = 0; b2 < taggedList2.getModel().getSize(); b2++)
      vector.addElement((PolicyParser.PermissionEntry)taggedList2.getObject(b2)); 
    grantEntry.permissionEntries = vector;
    return new PolicyEntry(this.tool, grantEntry);
  }
  
  void keyStoreDialog(int paramInt) {
    Point point = this.tw.getLocationOnScreen();
    setLayout(new GridBagLayout());
    if (paramInt == 0) {
      JLabel jLabel = new JLabel();
      this.tw.addNewComponent(this, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      JTextField jTextField = new JTextField(this.tool.getKeyStoreName(), 30);
      ToolWindow.configureLabelFor(jLabel, jTextField, "KeyStore.URL.");
      jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
      jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.U.R.L."));
      this.tw.addNewComponent(this, jTextField, 1, 1, 0, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      jLabel = new JLabel();
      this.tw.addNewComponent(this, jLabel, 2, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      jTextField = new JTextField(this.tool.getKeyStoreType(), 30);
      ToolWindow.configureLabelFor(jLabel, jTextField, "KeyStore.Type.");
      jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
      jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Type."));
      this.tw.addNewComponent(this, jTextField, 3, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      jLabel = new JLabel();
      this.tw.addNewComponent(this, jLabel, 4, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      jTextField = new JTextField(this.tool.getKeyStoreProvider(), 30);
      ToolWindow.configureLabelFor(jLabel, jTextField, "KeyStore.Provider.");
      jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
      jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Provider."));
      this.tw.addNewComponent(this, jTextField, 5, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      jLabel = new JLabel();
      this.tw.addNewComponent(this, jLabel, 6, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.R_BOTTOM_PADDING);
      jTextField = new JTextField(this.tool.getKeyStorePwdURL(), 30);
      ToolWindow.configureLabelFor(jLabel, jTextField, "KeyStore.Password.URL.");
      jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
      jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("KeyStore.Password.U.R.L."));
      this.tw.addNewComponent(this, jTextField, 7, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      JButton jButton1 = new JButton(PolicyTool.getMessage("OK"));
      jButton1.addActionListener(new ChangeKeyStoreOKButtonListener(this.tool, this.tw, this));
      this.tw.addNewComponent(this, jButton1, 8, 0, 4, 1, 1, 0.0D, 0.0D, 3);
      JButton jButton2 = new JButton(PolicyTool.getMessage("Cancel"));
      CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
      jButton2.addActionListener(cancelButtonListener);
      this.tw.addNewComponent(this, jButton2, 9, 1, 4, 1, 1, 0.0D, 0.0D, 3);
      getRootPane().setDefaultButton(jButton1);
      getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
    } 
    pack();
    setLocationRelativeTo(this.tw);
    setVisible(true);
  }
  
  void displayPrincipalDialog(boolean paramBoolean1, boolean paramBoolean2) {
    PolicyParser.PrincipalEntry principalEntry = null;
    TaggedList taggedList = (TaggedList)getComponent(6);
    int i = taggedList.getSelectedIndex();
    if (paramBoolean2)
      principalEntry = (PolicyParser.PrincipalEntry)taggedList.getObject(i); 
    ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Principals"), this.tool, this.tw, true);
    toolDialog.addWindowListener(new ChildWindowListener(toolDialog));
    Point point = getLocationOnScreen();
    toolDialog.setLayout(new GridBagLayout());
    toolDialog.setResizable(true);
    JLabel jLabel = paramBoolean2 ? new JLabel(PolicyTool.getMessage(".Edit.Principal.")) : new JLabel(PolicyTool.getMessage(".Add.New.Principal."));
    this.tw.addNewComponent(toolDialog, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
    JComboBox jComboBox = new JComboBox();
    jComboBox.addItem(PRIN_TYPE);
    jComboBox.getAccessibleContext().setAccessibleName(PRIN_TYPE);
    for (byte b = 0; b < PRIN_ARRAY.size(); b++) {
      Prin prin = (Prin)PRIN_ARRAY.get(b);
      jComboBox.addItem(prin.CLASS);
    } 
    if (paramBoolean2)
      if ("WILDCARD_PRINCIPAL_CLASS".equals(principalEntry.getPrincipalClass())) {
        jComboBox.setSelectedItem(PRIN_TYPE);
      } else {
        Prin prin = getPrin(principalEntry.getPrincipalClass(), true);
        if (prin != null)
          jComboBox.setSelectedItem(prin.CLASS); 
      }  
    jComboBox.addItemListener(new PrincipalTypeMenuListener(toolDialog));
    this.tw.addNewComponent(toolDialog, jComboBox, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    JTextField jTextField = paramBoolean2 ? new JTextField(principalEntry.getDisplayClass(), 30) : new JTextField(30);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PRIN_TYPE);
    this.tw.addNewComponent(toolDialog, jTextField, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    jLabel = new JLabel(PRIN_NAME);
    jTextField = paramBoolean2 ? new JTextField(principalEntry.getDisplayName(), 40) : new JTextField(40);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PRIN_NAME);
    this.tw.addNewComponent(toolDialog, jLabel, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    this.tw.addNewComponent(toolDialog, jTextField, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_PADDING);
    JButton jButton1 = new JButton(PolicyTool.getMessage("OK"));
    jButton1.addActionListener(new NewPolicyPrinOKButtonListener(this.tool, this.tw, this, toolDialog, paramBoolean2));
    this.tw.addNewComponent(toolDialog, jButton1, 5, 0, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    JButton jButton2 = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener cancelButtonListener = new CancelButtonListener(toolDialog);
    jButton2.addActionListener(cancelButtonListener);
    this.tw.addNewComponent(toolDialog, jButton2, 6, 1, 3, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    toolDialog.getRootPane().setDefaultButton(jButton1);
    toolDialog.getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
    toolDialog.pack();
    toolDialog.setLocationRelativeTo(this.tw);
    toolDialog.setVisible(true);
  }
  
  void displayPermissionDialog(boolean paramBoolean1, boolean paramBoolean2) {
    PolicyParser.PermissionEntry permissionEntry = null;
    TaggedList taggedList = (TaggedList)getComponent(8);
    int i = taggedList.getSelectedIndex();
    if (paramBoolean2)
      permissionEntry = (PolicyParser.PermissionEntry)taggedList.getObject(i); 
    ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Permissions"), this.tool, this.tw, true);
    toolDialog.addWindowListener(new ChildWindowListener(toolDialog));
    Point point = getLocationOnScreen();
    toolDialog.setLayout(new GridBagLayout());
    toolDialog.setResizable(true);
    JLabel jLabel = paramBoolean2 ? new JLabel(PolicyTool.getMessage(".Edit.Permission.")) : new JLabel(PolicyTool.getMessage(".Add.New.Permission."));
    this.tw.addNewComponent(toolDialog, jLabel, 0, 0, 0, 1, 1, 0.0D, 0.0D, 1, ToolWindow.TOP_BOTTOM_PADDING);
    JComboBox jComboBox = new JComboBox();
    jComboBox.addItem(PERM);
    jComboBox.getAccessibleContext().setAccessibleName(PERM);
    for (byte b = 0; b < PERM_ARRAY.size(); b++) {
      Perm perm = (Perm)PERM_ARRAY.get(b);
      jComboBox.addItem(perm.CLASS);
    } 
    this.tw.addNewComponent(toolDialog, jComboBox, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    JTextField jTextField = paramBoolean2 ? new JTextField(permissionEntry.permission, 30) : new JTextField(30);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PERM);
    if (paramBoolean2) {
      Perm perm = getPerm(permissionEntry.permission, true);
      if (perm != null)
        jComboBox.setSelectedItem(perm.CLASS); 
    } 
    this.tw.addNewComponent(toolDialog, jTextField, 2, 1, 1, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    jComboBox.addItemListener(new PermissionMenuListener(toolDialog));
    jComboBox = new JComboBox();
    jComboBox.addItem(PERM_NAME);
    jComboBox.getAccessibleContext().setAccessibleName(PERM_NAME);
    jTextField = paramBoolean2 ? new JTextField(permissionEntry.name, 40) : new JTextField(40);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PERM_NAME);
    if (paramBoolean2)
      setPermissionNames(getPerm(permissionEntry.permission, true), jComboBox, jTextField); 
    this.tw.addNewComponent(toolDialog, jComboBox, 3, 0, 2, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    this.tw.addNewComponent(toolDialog, jTextField, 4, 1, 2, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    jComboBox.addItemListener(new PermissionNameMenuListener(toolDialog));
    jComboBox = new JComboBox();
    jComboBox.addItem(PERM_ACTIONS);
    jComboBox.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
    jTextField = paramBoolean2 ? new JTextField(permissionEntry.action, 40) : new JTextField(40);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PERM_ACTIONS);
    if (paramBoolean2)
      setPermissionActions(getPerm(permissionEntry.permission, true), jComboBox, jTextField); 
    this.tw.addNewComponent(toolDialog, jComboBox, 5, 0, 3, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    this.tw.addNewComponent(toolDialog, jTextField, 6, 1, 3, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    jComboBox.addItemListener(new PermissionActionsMenuListener(toolDialog));
    jLabel = new JLabel(PolicyTool.getMessage("Signed.By."));
    this.tw.addNewComponent(toolDialog, jLabel, 7, 0, 4, 1, 1, 0.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    jTextField = paramBoolean2 ? new JTextField(permissionEntry.signedBy, 40) : new JTextField(40);
    jTextField.setPreferredSize(new Dimension((jTextField.getPreferredSize()).width, TEXTFIELD_HEIGHT));
    jTextField.getAccessibleContext().setAccessibleName(PolicyTool.getMessage("Signed.By."));
    this.tw.addNewComponent(toolDialog, jTextField, 8, 1, 4, 1, 1, 1.0D, 0.0D, 1, ToolWindow.LR_BOTTOM_PADDING);
    JButton jButton1 = new JButton(PolicyTool.getMessage("OK"));
    jButton1.addActionListener(new NewPolicyPermOKButtonListener(this.tool, this.tw, this, toolDialog, paramBoolean2));
    this.tw.addNewComponent(toolDialog, jButton1, 9, 0, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    JButton jButton2 = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener cancelButtonListener = new CancelButtonListener(toolDialog);
    jButton2.addActionListener(cancelButtonListener);
    this.tw.addNewComponent(toolDialog, jButton2, 10, 1, 5, 1, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    toolDialog.getRootPane().setDefaultButton(jButton1);
    toolDialog.getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
    toolDialog.pack();
    toolDialog.setLocationRelativeTo(this.tw);
    toolDialog.setVisible(true);
  }
  
  PolicyParser.PrincipalEntry getPrinFromDialog() throws Exception {
    JTextField jTextField = (JTextField)getComponent(2);
    String str1 = new String(jTextField.getText().trim());
    jTextField = (JTextField)getComponent(4);
    String str2 = new String(jTextField.getText().trim());
    if (str1.equals("*"))
      str1 = "WILDCARD_PRINCIPAL_CLASS"; 
    if (str2.equals("*"))
      str2 = "WILDCARD_PRINCIPAL_NAME"; 
    Object object = null;
    if (str1.equals("WILDCARD_PRINCIPAL_CLASS") && !str2.equals("WILDCARD_PRINCIPAL_NAME"))
      throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name")); 
    if (str2.equals(""))
      throw new Exception(PolicyTool.getMessage("Cannot.Specify.Principal.without.a.Name")); 
    if (str1.equals("")) {
      str1 = "PolicyParser.REPLACE_NAME";
      this.tool.warnings.addElement("Warning: Principal name '" + str2 + "' specified without a Principal class.\n\t'" + str2 + "' will be interpreted as a key store alias.\n\tThe final principal class will be " + "javax.security.auth.x500.X500Principal" + ".\n\tThe final principal name will be determined by the following:\n\n\tIf the key store entry identified by '" + str2 + "'\n\tis a key entry, then the principal name will be\n\tthe subject distinguished name from the first\n\tcertificate in the entry's certificate chain.\n\n\tIf the key store entry identified by '" + str2 + "'\n\tis a trusted certificate entry, then the\n\tprincipal name will be the subject distinguished\n\tname from the trusted public key certificate.");
      this.tw.displayStatusDialog(this, "'" + str2 + "' will be interpreted as a key store alias.  View Warning Log for details.");
    } 
    return new PolicyParser.PrincipalEntry(str1, str2);
  }
  
  PolicyParser.PermissionEntry getPermFromDialog() {
    JTextField jTextField = (JTextField)getComponent(2);
    String str1 = new String(jTextField.getText().trim());
    jTextField = (JTextField)getComponent(4);
    String str2 = null;
    if (!jTextField.getText().trim().equals(""))
      str2 = new String(jTextField.getText().trim()); 
    if (str1.equals("") || (!str1.equals("java.security.AllPermission") && str2 == null))
      throw new InvalidParameterException(PolicyTool.getMessage("Permission.and.Target.Name.must.have.a.value")); 
    if (str1.equals("java.io.FilePermission") && str2.lastIndexOf("\\\\") > 0) {
      char c = this.tw.displayYesNoDialog(this, PolicyTool.getMessage("Warning"), PolicyTool.getMessage("Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes"), PolicyTool.getMessage("Retain"), PolicyTool.getMessage("Edit"));
      if (c != 'Y')
        throw new NoDisplayException(); 
    } 
    jTextField = (JTextField)getComponent(6);
    String str3 = null;
    if (!jTextField.getText().trim().equals(""))
      str3 = new String(jTextField.getText().trim()); 
    jTextField = (JTextField)getComponent(8);
    String str4 = null;
    if (!jTextField.getText().trim().equals(""))
      str4 = new String(jTextField.getText().trim()); 
    PolicyParser.PermissionEntry permissionEntry = new PolicyParser.PermissionEntry(str1, str2, str3);
    permissionEntry.signedBy = str4;
    if (str4 != null) {
      String[] arrayOfString = this.tool.parseSigners(permissionEntry.signedBy);
      for (byte b = 0; b < arrayOfString.length; b++) {
        try {
          PublicKey publicKey = this.tool.getPublicKeyAlias(arrayOfString[b]);
          if (publicKey == null) {
            MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
            Object[] arrayOfObject = { arrayOfString[b] };
            this.tool.warnings.addElement(messageFormat.format(arrayOfObject));
            this.tw.displayStatusDialog(this, messageFormat.format(arrayOfObject));
          } 
        } catch (Exception exception) {
          this.tw.displayErrorDialog(this, exception);
        } 
      } 
    } 
    return permissionEntry;
  }
  
  void displayConfirmRemovePolicyEntry() {
    JList jList = (JList)this.tw.getComponent(3);
    int i = jList.getSelectedIndex();
    PolicyEntry[] arrayOfPolicyEntry = this.tool.getEntry();
    Point point = this.tw.getLocationOnScreen();
    setLayout(new GridBagLayout());
    JLabel jLabel = new JLabel(PolicyTool.getMessage("Remove.this.Policy.Entry."));
    this.tw.addNewComponent(this, jLabel, 0, 0, 0, 2, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
    jLabel = new JLabel(arrayOfPolicyEntry[i].codebaseToString());
    this.tw.addNewComponent(this, jLabel, 1, 0, 1, 2, 1, 0.0D, 0.0D, 1);
    jLabel = new JLabel(arrayOfPolicyEntry[i].principalsToString().trim());
    this.tw.addNewComponent(this, jLabel, 2, 0, 2, 2, 1, 0.0D, 0.0D, 1);
    Vector vector = (arrayOfPolicyEntry[i].getGrantEntry()).permissionEntries;
    for (byte b = 0; b < vector.size(); b++) {
      PolicyParser.PermissionEntry permissionEntry = (PolicyParser.PermissionEntry)vector.elementAt(b);
      String str = PermissionEntryToUserFriendlyString(permissionEntry);
      jLabel = new JLabel("    " + str);
      if (b == vector.size() - 1) {
        this.tw.addNewComponent(this, jLabel, 3 + b, 1, 3 + b, 1, 1, 0.0D, 0.0D, 1, ToolWindow.BOTTOM_PADDING);
      } else {
        this.tw.addNewComponent(this, jLabel, 3 + b, 1, 3 + b, 1, 1, 0.0D, 0.0D, 1);
      } 
    } 
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    JButton jButton1 = new JButton(PolicyTool.getMessage("OK"));
    jButton1.addActionListener(new ConfirmRemovePolicyEntryOKButtonListener(this.tool, this.tw, this));
    this.tw.addNewComponent(jPanel, jButton1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    JButton jButton2 = new JButton(PolicyTool.getMessage("Cancel"));
    CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
    jButton2.addActionListener(cancelButtonListener);
    this.tw.addNewComponent(jPanel, jButton2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_PADDING);
    this.tw.addNewComponent(this, jPanel, 3 + vector.size(), 0, 3 + vector.size(), 2, 1, 0.0D, 0.0D, 3, ToolWindow.TOP_BOTTOM_PADDING);
    getRootPane().setDefaultButton(jButton1);
    getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
    pack();
    setLocationRelativeTo(this.tw);
    setVisible(true);
  }
  
  void displaySaveAsDialog(int paramInt) {
    FileDialog fileDialog = new FileDialog(this.tw, PolicyTool.getMessage("Save.As"), 1);
    fileDialog.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent param1WindowEvent) { param1WindowEvent.getWindow().setVisible(false); }
        });
    fileDialog.setVisible(true);
    if (fileDialog.getFile() == null || fileDialog.getFile().equals(""))
      return; 
    File file = new File(fileDialog.getDirectory(), fileDialog.getFile());
    String str = file.getPath();
    fileDialog.dispose();
    try {
      this.tool.savePolicy(str);
      MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
      Object[] arrayOfObject = { str };
      this.tw.displayStatusDialog(null, messageFormat.format(arrayOfObject));
      JTextField jTextField = (JTextField)this.tw.getComponent(1);
      jTextField.setText(str);
      this.tw.setVisible(true);
      userSaveContinue(this.tool, this.tw, this, paramInt);
    } catch (FileNotFoundException fileNotFoundException) {
      if (str == null || str.equals("")) {
        this.tw.displayErrorDialog(null, new FileNotFoundException(PolicyTool.getMessage("null.filename")));
      } else {
        this.tw.displayErrorDialog(null, fileNotFoundException);
      } 
    } catch (Exception exception) {
      this.tw.displayErrorDialog(null, exception);
    } 
  }
  
  void displayUserSave(int paramInt) {
    if (this.tool.modified == true) {
      Point point = this.tw.getLocationOnScreen();
      setLayout(new GridBagLayout());
      JLabel jLabel = new JLabel(PolicyTool.getMessage("Save.changes."));
      this.tw.addNewComponent(this, jLabel, 0, 0, 0, 3, 1, 0.0D, 0.0D, 1, ToolWindow.L_TOP_BOTTOM_PADDING);
      JPanel jPanel = new JPanel();
      jPanel.setLayout(new GridBagLayout());
      JButton jButton1 = new JButton();
      ToolWindow.configureButton(jButton1, "Yes");
      jButton1.addActionListener(new UserSaveYesButtonListener(this, this.tool, this.tw, paramInt));
      this.tw.addNewComponent(jPanel, jButton1, 0, 0, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
      JButton jButton2 = new JButton();
      ToolWindow.configureButton(jButton2, "No");
      jButton2.addActionListener(new UserSaveNoButtonListener(this, this.tool, this.tw, paramInt));
      this.tw.addNewComponent(jPanel, jButton2, 1, 1, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
      JButton jButton3 = new JButton();
      ToolWindow.configureButton(jButton3, "Cancel");
      CancelButtonListener cancelButtonListener = new CancelButtonListener(this);
      jButton3.addActionListener(cancelButtonListener);
      this.tw.addNewComponent(jPanel, jButton3, 2, 2, 0, 1, 1, 0.0D, 0.0D, 3, ToolWindow.LR_BOTTOM_PADDING);
      this.tw.addNewComponent(this, jPanel, 1, 0, 1, 1, 1, 0.0D, 0.0D, 1);
      getRootPane().registerKeyboardAction(cancelButtonListener, escKey, 2);
      pack();
      setLocationRelativeTo(this.tw);
      setVisible(true);
    } else {
      userSaveContinue(this.tool, this.tw, this, paramInt);
    } 
  }
  
  void userSaveContinue(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, int paramInt) {
    String str;
    FileDialog fileDialog;
    JTextField jTextField;
    JList jList;
    switch (paramInt) {
      case 1:
        paramToolWindow.setVisible(false);
        paramToolWindow.dispose();
        System.exit(0);
      case 2:
        try {
          paramPolicyTool.openPolicy(null);
        } catch (Exception exception) {
          paramPolicyTool.modified = false;
          paramToolWindow.displayErrorDialog(null, exception);
        } 
        jList = new JList(new DefaultListModel());
        jList.setVisibleRowCount(15);
        jList.setSelectionMode(0);
        jList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
        paramToolWindow.replacePolicyList(jList);
        jTextField = (JTextField)paramToolWindow.getComponent(1);
        jTextField.setText("");
        paramToolWindow.setVisible(true);
        break;
      case 3:
        fileDialog = new FileDialog(paramToolWindow, PolicyTool.getMessage("Open"), 0);
        fileDialog.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent param1WindowEvent) { param1WindowEvent.getWindow().setVisible(false); }
            });
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null || fileDialog.getFile().equals(""))
          return; 
        str = (new File(fileDialog.getDirectory(), fileDialog.getFile())).getPath();
        try {
          paramPolicyTool.openPolicy(str);
          DefaultListModel defaultListModel = new DefaultListModel();
          jList = new JList(defaultListModel);
          jList.setVisibleRowCount(15);
          jList.setSelectionMode(0);
          jList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
          PolicyEntry[] arrayOfPolicyEntry = paramPolicyTool.getEntry();
          if (arrayOfPolicyEntry != null)
            for (byte b = 0; b < arrayOfPolicyEntry.length; b++)
              defaultListModel.addElement(arrayOfPolicyEntry[b].headerToString());  
          paramToolWindow.replacePolicyList(jList);
          paramPolicyTool.modified = false;
          jTextField = (JTextField)paramToolWindow.getComponent(1);
          jTextField.setText(str);
          paramToolWindow.setVisible(true);
          if (paramPolicyTool.newWarning == true)
            paramToolWindow.displayStatusDialog(null, PolicyTool.getMessage("Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.")); 
        } catch (Exception exception) {
          jList = new JList(new DefaultListModel());
          jList.setVisibleRowCount(15);
          jList.setSelectionMode(0);
          jList.addMouseListener(new PolicyListListener(paramPolicyTool, paramToolWindow));
          paramToolWindow.replacePolicyList(jList);
          paramPolicyTool.setPolicyFileName(null);
          paramPolicyTool.modified = false;
          jTextField = (JTextField)paramToolWindow.getComponent(1);
          jTextField.setText("");
          paramToolWindow.setVisible(true);
          MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Could.not.open.policy.file.policyFile.e.toString."));
          Object[] arrayOfObject = { str, exception.toString() };
          paramToolWindow.displayErrorDialog(null, messageFormat.format(arrayOfObject));
        } 
        break;
    } 
  }
  
  void setPermissionNames(Perm paramPerm, JComboBox paramJComboBox, JTextField paramJTextField) {
    paramJComboBox.removeAllItems();
    paramJComboBox.addItem(PERM_NAME);
    if (paramPerm == null) {
      paramJTextField.setEditable(true);
    } else if (paramPerm.TARGETS == null) {
      paramJTextField.setEditable(false);
    } else {
      paramJTextField.setEditable(true);
      for (byte b = 0; b < paramPerm.TARGETS.length; b++)
        paramJComboBox.addItem(paramPerm.TARGETS[b]); 
    } 
  }
  
  void setPermissionActions(Perm paramPerm, JComboBox paramJComboBox, JTextField paramJTextField) {
    paramJComboBox.removeAllItems();
    paramJComboBox.addItem(PERM_ACTIONS);
    if (paramPerm == null) {
      paramJTextField.setEditable(true);
    } else if (paramPerm.ACTIONS == null) {
      paramJTextField.setEditable(false);
    } else {
      paramJTextField.setEditable(true);
      for (byte b = 0; b < paramPerm.ACTIONS.length; b++)
        paramJComboBox.addItem(paramPerm.ACTIONS[b]); 
    } 
  }
  
  static String PermissionEntryToUserFriendlyString(PolicyParser.PermissionEntry paramPermissionEntry) {
    String str = paramPermissionEntry.permission;
    if (paramPermissionEntry.name != null)
      str = str + " " + paramPermissionEntry.name; 
    if (paramPermissionEntry.action != null)
      str = str + ", \"" + paramPermissionEntry.action + "\""; 
    if (paramPermissionEntry.signedBy != null)
      str = str + ", signedBy " + paramPermissionEntry.signedBy; 
    return str;
  }
  
  static String PrincipalEntryToUserFriendlyString(PolicyParser.PrincipalEntry paramPrincipalEntry) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    paramPrincipalEntry.write(printWriter);
    return stringWriter.toString();
  }
  
  static  {
    PERM_ARRAY.add(new AllPerm());
    PERM_ARRAY.add(new AudioPerm());
    PERM_ARRAY.add(new AuthPerm());
    PERM_ARRAY.add(new AWTPerm());
    PERM_ARRAY.add(new DelegationPerm());
    PERM_ARRAY.add(new FilePerm());
    PERM_ARRAY.add(new URLPerm());
    PERM_ARRAY.add(new InqSecContextPerm());
    PERM_ARRAY.add(new LogPerm());
    PERM_ARRAY.add(new MgmtPerm());
    PERM_ARRAY.add(new MBeanPerm());
    PERM_ARRAY.add(new MBeanSvrPerm());
    PERM_ARRAY.add(new MBeanTrustPerm());
    PERM_ARRAY.add(new NetPerm());
    PERM_ARRAY.add(new PrivCredPerm());
    PERM_ARRAY.add(new PropPerm());
    PERM_ARRAY.add(new ReflectPerm());
    PERM_ARRAY.add(new RuntimePerm());
    PERM_ARRAY.add(new SecurityPerm());
    PERM_ARRAY.add(new SerialPerm());
    PERM_ARRAY.add(new ServicePerm());
    PERM_ARRAY.add(new SocketPerm());
    PERM_ARRAY.add(new SQLPerm());
    PERM_ARRAY.add(new SSLPerm());
    PERM_ARRAY.add(new SubjDelegPerm());
    PRIN_ARRAY = new ArrayList();
    PRIN_ARRAY.add(new KrbPrin());
    PRIN_ARRAY.add(new X500Prin());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ToolDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */