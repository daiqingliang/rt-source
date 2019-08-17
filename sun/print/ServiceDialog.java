package sun.print;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobPriority;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.NumberFormatter;

public class ServiceDialog extends JDialog implements ActionListener {
  public static final int WAITING = 0;
  
  public static final int APPROVE = 1;
  
  public static final int CANCEL = 2;
  
  private static final String strBundle = "sun.print.resources.serviceui";
  
  private static final Insets panelInsets = new Insets(6, 6, 6, 6);
  
  private static final Insets compInsets = new Insets(3, 6, 3, 6);
  
  private static ResourceBundle messageRB;
  
  private JTabbedPane tpTabs;
  
  private JButton btnCancel;
  
  private JButton btnApprove;
  
  private PrintService[] services;
  
  private int defaultServiceIndex;
  
  private PrintRequestAttributeSet asOriginal;
  
  private HashPrintRequestAttributeSet asCurrent;
  
  private PrintService psCurrent;
  
  private DocFlavor docFlavor;
  
  private int status;
  
  private ValidatingFileChooser jfc;
  
  private GeneralPanel pnlGeneral;
  
  private PageSetupPanel pnlPageSetup;
  
  private AppearancePanel pnlAppearance;
  
  private boolean isAWT = false;
  
  static Class _keyEventClazz;
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog) {
    super(paramDialog, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
    initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame) {
    super(paramFrame, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
    initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  void initPrintDialog(int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    this.services = paramArrayOfPrintService;
    this.defaultServiceIndex = paramInt3;
    this.asOriginal = paramPrintRequestAttributeSet;
    this.asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
    this.psCurrent = paramArrayOfPrintService[paramInt3];
    this.docFlavor = paramDocFlavor;
    SunPageSelection sunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
    if (sunPageSelection != null)
      this.isAWT = true; 
    if (paramPrintRequestAttributeSet.get(DialogOnTop.class) != null)
      setAlwaysOnTop(true); 
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    this.tpTabs = new JTabbedPane();
    this.tpTabs.setBorder(new EmptyBorder(5, 5, 5, 5));
    String str1 = getMsg("tab.general");
    int i = getVKMnemonic("tab.general");
    this.pnlGeneral = new GeneralPanel();
    this.tpTabs.add(str1, this.pnlGeneral);
    this.tpTabs.setMnemonicAt(0, i);
    String str2 = getMsg("tab.pagesetup");
    int j = getVKMnemonic("tab.pagesetup");
    this.pnlPageSetup = new PageSetupPanel();
    this.tpTabs.add(str2, this.pnlPageSetup);
    this.tpTabs.setMnemonicAt(1, j);
    String str3 = getMsg("tab.appearance");
    int k = getVKMnemonic("tab.appearance");
    this.pnlAppearance = new AppearancePanel();
    this.tpTabs.add(str3, this.pnlAppearance);
    this.tpTabs.setMnemonicAt(2, k);
    container.add(this.tpTabs, "Center");
    updatePanels();
    JPanel jPanel = new JPanel(new FlowLayout(4));
    this.btnApprove = createExitButton("button.print", this);
    jPanel.add(this.btnApprove);
    getRootPane().setDefaultButton(this.btnApprove);
    this.btnCancel = createExitButton("button.cancel", this);
    handleEscKey(this.btnCancel);
    jPanel.add(this.btnCancel);
    container.add(jPanel, "South");
    addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent param1WindowEvent) { ServiceDialog.this.dispose(2); }
        });
    getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
    setResizable(false);
    setLocation(paramInt1, paramInt2);
    pack();
  }
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog) {
    super(paramDialog, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
    initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame) {
    super(paramFrame, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
    initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  void initPageDialog(int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    this.psCurrent = paramPrintService;
    this.docFlavor = paramDocFlavor;
    this.asOriginal = paramPrintRequestAttributeSet;
    this.asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
    if (paramPrintRequestAttributeSet.get(DialogOnTop.class) != null)
      setAlwaysOnTop(true); 
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    this.pnlPageSetup = new PageSetupPanel();
    container.add(this.pnlPageSetup, "Center");
    this.pnlPageSetup.updateInfo();
    JPanel jPanel = new JPanel(new FlowLayout(4));
    this.btnApprove = createExitButton("button.ok", this);
    jPanel.add(this.btnApprove);
    getRootPane().setDefaultButton(this.btnApprove);
    this.btnCancel = createExitButton("button.cancel", this);
    handleEscKey(this.btnCancel);
    jPanel.add(this.btnCancel);
    container.add(jPanel, "South");
    addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent param1WindowEvent) { ServiceDialog.this.dispose(2); }
        });
    getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
    setResizable(false);
    setLocation(paramInt1, paramInt2);
    pack();
  }
  
  private void handleEscKey(JButton paramJButton) {
    AbstractAction abstractAction = new AbstractAction() {
        public void actionPerformed(ActionEvent param1ActionEvent) { ServiceDialog.this.dispose(2); }
      };
    KeyStroke keyStroke = KeyStroke.getKeyStroke(27, 0);
    InputMap inputMap = paramJButton.getInputMap(2);
    ActionMap actionMap = paramJButton.getActionMap();
    if (inputMap != null && actionMap != null) {
      inputMap.put(keyStroke, "cancel");
      actionMap.put("cancel", abstractAction);
    } 
  }
  
  public int getStatus() { return this.status; }
  
  public PrintRequestAttributeSet getAttributes() { return (this.status == 1) ? this.asCurrent : this.asOriginal; }
  
  public PrintService getPrintService() { return (this.status == 1) ? this.psCurrent : null; }
  
  public void dispose(int paramInt) {
    this.status = paramInt;
    dispose();
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    boolean bool = false;
    if (object == this.btnApprove) {
      bool = true;
      if (this.pnlGeneral != null)
        if (this.pnlGeneral.isPrintToFileRequested()) {
          bool = showFileChooser();
        } else {
          this.asCurrent.remove(Destination.class);
        }  
    } 
    dispose(bool ? 1 : 2);
  }
  
  private boolean showFileChooser() {
    File file;
    Class clazz = Destination.class;
    Destination destination = (Destination)this.asCurrent.get(clazz);
    if (destination == null) {
      destination = (Destination)this.asOriginal.get(clazz);
      if (destination == null) {
        destination = (Destination)this.psCurrent.getDefaultAttributeValue(clazz);
        if (destination == null)
          try {
            destination = new Destination(new URI("file:out.prn"));
          } catch (URISyntaxException null) {} 
      } 
    } 
    if (destination != null) {
      try {
        file = new File(destination.getURI());
      } catch (Exception exception) {
        file = new File("out.prn");
      } 
    } else {
      file = new File("out.prn");
    } 
    ValidatingFileChooser validatingFileChooser = new ValidatingFileChooser(null);
    validatingFileChooser.setApproveButtonText(getMsg("button.ok"));
    validatingFileChooser.setDialogTitle(getMsg("dialog.printtofile"));
    validatingFileChooser.setDialogType(1);
    validatingFileChooser.setSelectedFile(file);
    int i = validatingFileChooser.showDialog(this, null);
    if (i == 0) {
      file = validatingFileChooser.getSelectedFile();
      try {
        this.asCurrent.add(new Destination(file.toURI()));
      } catch (Exception exception) {
        this.asCurrent.remove(clazz);
      } 
    } else {
      this.asCurrent.remove(clazz);
    } 
    return (i == 0);
  }
  
  private void updatePanels() {
    this.pnlGeneral.updateInfo();
    this.pnlPageSetup.updateInfo();
    this.pnlAppearance.updateInfo();
  }
  
  public static void initResource() { AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              messageRB = ResourceBundle.getBundle("sun.print.resources.serviceui");
              return null;
            } catch (MissingResourceException missingResourceException) {
              throw new Error("Fatal: Resource for ServiceUI is missing");
            } 
          }
        }); }
  
  public static String getMsg(String paramString) {
    try {
      return removeMnemonics(messageRB.getString(paramString));
    } catch (MissingResourceException missingResourceException) {
      throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
    } 
  }
  
  private static String removeMnemonics(String paramString) {
    int i = paramString.indexOf('&');
    int j = paramString.length();
    if (i < 0 || i == j - 1)
      return paramString; 
    int k = paramString.indexOf('&', i + 1);
    return (k == i + 1) ? ((k + 1 == j) ? paramString.substring(0, i + 1) : (paramString.substring(0, i + 1) + removeMnemonics(paramString.substring(k + 1)))) : ((i == 0) ? removeMnemonics(paramString.substring(1)) : (paramString.substring(0, i) + removeMnemonics(paramString.substring(i + 1))));
  }
  
  private static char getMnemonic(String paramString) {
    String str = messageRB.getString(paramString).replace("&&", "");
    int i = str.indexOf('&');
    if (0 <= i && i < str.length() - 1) {
      char c = str.charAt(i + 1);
      return Character.toUpperCase(c);
    } 
    return Character.MIN_VALUE;
  }
  
  private static int getVKMnemonic(String paramString) {
    String str1 = String.valueOf(getMnemonic(paramString));
    if (str1 == null || str1.length() != 1)
      return 0; 
    String str2 = "VK_" + str1.toUpperCase();
    try {
      if (_keyEventClazz == null)
        _keyEventClazz = Class.forName("java.awt.event.KeyEvent", true, ServiceDialog.class.getClassLoader()); 
      Field field = _keyEventClazz.getDeclaredField(str2);
      return field.getInt(null);
    } catch (Exception exception) {
      return 0;
    } 
  }
  
  private static URL getImageResource(final String key) {
    URL uRL = (URL)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return ServiceDialog.class.getResource("resources/" + key); }
        });
    if (uRL == null)
      throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource"); 
    return uRL;
  }
  
  private static JButton createButton(String paramString, ActionListener paramActionListener) {
    JButton jButton = new JButton(getMsg(paramString));
    jButton.setMnemonic(getMnemonic(paramString));
    jButton.addActionListener(paramActionListener);
    return jButton;
  }
  
  private static JButton createExitButton(String paramString, ActionListener paramActionListener) {
    String str = getMsg(paramString);
    JButton jButton = new JButton(str);
    jButton.addActionListener(paramActionListener);
    jButton.getAccessibleContext().setAccessibleDescription(str);
    return jButton;
  }
  
  private static JCheckBox createCheckBox(String paramString, ActionListener paramActionListener) {
    JCheckBox jCheckBox = new JCheckBox(getMsg(paramString));
    jCheckBox.setMnemonic(getMnemonic(paramString));
    jCheckBox.addActionListener(paramActionListener);
    return jCheckBox;
  }
  
  private static JRadioButton createRadioButton(String paramString, ActionListener paramActionListener) {
    JRadioButton jRadioButton = new JRadioButton(getMsg(paramString));
    jRadioButton.setMnemonic(getMnemonic(paramString));
    jRadioButton.addActionListener(paramActionListener);
    return jRadioButton;
  }
  
  public static void showNoPrintService(GraphicsConfiguration paramGraphicsConfiguration) {
    Frame frame = new Frame(paramGraphicsConfiguration);
    JOptionPane.showMessageDialog(frame, getMsg("dialog.noprintermsg"));
    frame.dispose();
  }
  
  private static void addToGB(Component paramComponent, Container paramContainer, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints) {
    paramGridBagLayout.setConstraints(paramComponent, paramGridBagConstraints);
    paramContainer.add(paramComponent);
  }
  
  private static void addToBG(AbstractButton paramAbstractButton, Container paramContainer, ButtonGroup paramButtonGroup) {
    paramButtonGroup.add(paramAbstractButton);
    paramContainer.add(paramAbstractButton);
  }
  
  static  {
    initResource();
    _keyEventClazz = null;
  }
  
  private class AppearancePanel extends JPanel {
    private ServiceDialog.ChromaticityPanel pnlChromaticity;
    
    private ServiceDialog.QualityPanel pnlQuality;
    
    private ServiceDialog.JobAttributesPanel pnlJobAttributes;
    
    private ServiceDialog.SidesPanel pnlSides;
    
    public AppearancePanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = panelInsets;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridwidth = -1;
      this.pnlChromaticity = new ServiceDialog.ChromaticityPanel(this$0);
      ServiceDialog.addToGB(this.pnlChromaticity, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.pnlQuality = new ServiceDialog.QualityPanel(this$0);
      ServiceDialog.addToGB(this.pnlQuality, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 1;
      this.pnlSides = new ServiceDialog.SidesPanel(this$0);
      ServiceDialog.addToGB(this.pnlSides, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.pnlJobAttributes = new ServiceDialog.JobAttributesPanel(this$0);
      ServiceDialog.addToGB(this.pnlJobAttributes, this, gridBagLayout, gridBagConstraints);
    }
    
    public void updateInfo() {
      this.pnlChromaticity.updateInfo();
      this.pnlQuality.updateInfo();
      this.pnlSides.updateInfo();
      this.pnlJobAttributes.updateInfo();
    }
  }
  
  private class ChromaticityPanel extends JPanel implements ActionListener {
    private final String strTitle = ServiceDialog.getMsg("border.chromaticity");
    
    private JRadioButton rbMonochrome;
    
    private JRadioButton rbColor;
    
    public ChromaticityPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weighty = 1.0D;
      ButtonGroup buttonGroup = new ButtonGroup();
      this.rbMonochrome = ServiceDialog.createRadioButton("radiobutton.monochrome", this);
      this.rbMonochrome.setSelected(true);
      buttonGroup.add(this.rbMonochrome);
      ServiceDialog.addToGB(this.rbMonochrome, this, gridBagLayout, gridBagConstraints);
      this.rbColor = ServiceDialog.createRadioButton("radiobutton.color", this);
      buttonGroup.add(this.rbColor);
      ServiceDialog.addToGB(this.rbColor, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      if (object == this.rbMonochrome) {
        ServiceDialog.this.asCurrent.add(Chromaticity.MONOCHROME);
      } else if (object == this.rbColor) {
        ServiceDialog.this.asCurrent.add(Chromaticity.COLOR);
      } 
    }
    
    public void updateInfo() {
      Class clazz = Chromaticity.class;
      boolean bool1 = false;
      boolean bool2 = false;
      if (ServiceDialog.this.isAWT) {
        bool1 = true;
        bool2 = true;
      } else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
        if (object instanceof Chromaticity[]) {
          Chromaticity[] arrayOfChromaticity = (Chromaticity[])object;
          for (byte b = 0; b < arrayOfChromaticity.length; b++) {
            Chromaticity chromaticity1 = arrayOfChromaticity[b];
            if (chromaticity1 == Chromaticity.MONOCHROME) {
              bool1 = true;
            } else if (chromaticity1 == Chromaticity.COLOR) {
              bool2 = true;
            } 
          } 
        } 
      } 
      this.rbMonochrome.setEnabled(bool1);
      this.rbColor.setEnabled(bool2);
      Chromaticity chromaticity = (Chromaticity)ServiceDialog.this.asCurrent.get(clazz);
      if (chromaticity == null) {
        chromaticity = (Chromaticity)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
        if (chromaticity == null)
          chromaticity = Chromaticity.MONOCHROME; 
      } 
      if (chromaticity == Chromaticity.MONOCHROME) {
        this.rbMonochrome.setSelected(true);
      } else {
        this.rbColor.setSelected(true);
      } 
    }
  }
  
  private class CopiesPanel extends JPanel implements ActionListener, ChangeListener {
    private final String strTitle = ServiceDialog.getMsg("border.copies");
    
    private SpinnerNumberModel snModel;
    
    private JSpinner spinCopies;
    
    private JLabel lblCopies;
    
    private JCheckBox cbCollate;
    
    private boolean scSupported;
    
    public CopiesPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = compInsets;
      this.lblCopies = new JLabel(ServiceDialog.getMsg("label.numcopies"), 11);
      this.lblCopies.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.numcopies"));
      this.lblCopies.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.numcopies"));
      ServiceDialog.addToGB(this.lblCopies, this, gridBagLayout, gridBagConstraints);
      this.snModel = new SpinnerNumberModel(1, 1, 999, 1);
      this.spinCopies = new JSpinner(this.snModel);
      this.lblCopies.setLabelFor(this.spinCopies);
      ((JSpinner.NumberEditor)this.spinCopies.getEditor()).getTextField().setColumns(3);
      this.spinCopies.addChangeListener(this);
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.spinCopies, this, gridBagLayout, gridBagConstraints);
      this.cbCollate = ServiceDialog.createCheckBox("checkbox.collate", this);
      this.cbCollate.setEnabled(false);
      ServiceDialog.addToGB(this.cbCollate, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.cbCollate.isSelected()) {
        ServiceDialog.this.asCurrent.add(SheetCollate.COLLATED);
      } else {
        ServiceDialog.this.asCurrent.add(SheetCollate.UNCOLLATED);
      } 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      updateCollateCB();
      ServiceDialog.this.asCurrent.add(new Copies(this.snModel.getNumber().intValue()));
    }
    
    private void updateCollateCB() {
      int i = this.snModel.getNumber().intValue();
      if (ServiceDialog.this.isAWT) {
        this.cbCollate.setEnabled(true);
      } else {
        this.cbCollate.setEnabled((i > 1 && this.scSupported));
      } 
    }
    
    public void updateInfo() {
      int j;
      int i;
      Class clazz1 = Copies.class;
      Class clazz2 = CopiesSupported.class;
      Class clazz3 = SheetCollate.class;
      boolean bool = false;
      this.scSupported = false;
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz1))
        bool = true; 
      CopiesSupported copiesSupported = (CopiesSupported)ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz1, null, null);
      if (copiesSupported == null)
        copiesSupported = new CopiesSupported(1, 999); 
      Copies copies = (Copies)ServiceDialog.this.asCurrent.get(clazz1);
      if (copies == null) {
        copies = (Copies)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz1);
        if (copies == null)
          copies = new Copies(1); 
      } 
      this.spinCopies.setEnabled(bool);
      this.lblCopies.setEnabled(bool);
      int[][] arrayOfInt = copiesSupported.getMembers();
      if (arrayOfInt.length > 0 && arrayOfInt[0].length > 0) {
        i = arrayOfInt[0][0];
        j = arrayOfInt[0][1];
      } else {
        i = 1;
        j = Integer.MAX_VALUE;
      } 
      this.snModel.setMinimum(new Integer(i));
      this.snModel.setMaximum(new Integer(j));
      int k = copies.getValue();
      if (k < i || k > j)
        k = i; 
      this.snModel.setValue(new Integer(k));
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz3))
        this.scSupported = true; 
      SheetCollate sheetCollate = (SheetCollate)ServiceDialog.this.asCurrent.get(clazz3);
      if (sheetCollate == null) {
        sheetCollate = (SheetCollate)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz3);
        if (sheetCollate == null)
          sheetCollate = SheetCollate.UNCOLLATED; 
      } 
      this.cbCollate.setSelected((sheetCollate == SheetCollate.COLLATED));
      updateCollateCB();
    }
  }
  
  private class GeneralPanel extends JPanel {
    private ServiceDialog.PrintServicePanel pnlPrintService;
    
    private ServiceDialog.PrintRangePanel pnlPrintRange;
    
    private ServiceDialog.CopiesPanel pnlCopies;
    
    public GeneralPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = panelInsets;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridwidth = 0;
      this.pnlPrintService = new ServiceDialog.PrintServicePanel(this$0);
      ServiceDialog.addToGB(this.pnlPrintService, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = -1;
      this.pnlPrintRange = new ServiceDialog.PrintRangePanel(this$0);
      ServiceDialog.addToGB(this.pnlPrintRange, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.pnlCopies = new ServiceDialog.CopiesPanel(this$0);
      ServiceDialog.addToGB(this.pnlCopies, this, gridBagLayout, gridBagConstraints);
    }
    
    public boolean isPrintToFileRequested() { return this.pnlPrintService.isPrintToFileSelected(); }
    
    public void updateInfo() {
      this.pnlPrintService.updateInfo();
      this.pnlPrintRange.updateInfo();
      this.pnlCopies.updateInfo();
    }
  }
  
  private class IconRadioButton extends JPanel {
    private JRadioButton rb;
    
    private JLabel lbl;
    
    public IconRadioButton(String param1String1, String param1String2, boolean param1Boolean, ButtonGroup param1ButtonGroup, ActionListener param1ActionListener) {
      super(new FlowLayout(3));
      final URL imgURL = ServiceDialog.getImageResource(param1String2);
      Icon icon = (Icon)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return new ImageIcon(imgURL); }
          });
      this.lbl = new JLabel(icon);
      add(this.lbl);
      this.rb = ServiceDialog.createRadioButton(param1String1, param1ActionListener);
      this.rb.setSelected(param1Boolean);
      ServiceDialog.addToBG(this.rb, this, param1ButtonGroup);
    }
    
    public void addActionListener(ActionListener param1ActionListener) { this.rb.addActionListener(param1ActionListener); }
    
    public boolean isSameAs(Object param1Object) { return (this.rb == param1Object); }
    
    public void setEnabled(boolean param1Boolean) {
      this.rb.setEnabled(param1Boolean);
      this.lbl.setEnabled(param1Boolean);
    }
    
    public boolean isSelected() { return this.rb.isSelected(); }
    
    public void setSelected(boolean param1Boolean) { this.rb.setSelected(param1Boolean); }
  }
  
  private class JobAttributesPanel extends JPanel implements ActionListener, ChangeListener, FocusListener {
    private final String strTitle = ServiceDialog.getMsg("border.jobattributes");
    
    private JLabel lblPriority;
    
    private JLabel lblJobName;
    
    private JLabel lblUserName;
    
    private JSpinner spinPriority;
    
    private SpinnerNumberModel snModel;
    
    private JCheckBox cbJobSheets;
    
    private JTextField tfJobName;
    
    private JTextField tfUserName;
    
    public JobAttributesPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 0;
      gridBagConstraints.insets = compInsets;
      gridBagConstraints.weighty = 1.0D;
      this.cbJobSheets = ServiceDialog.createCheckBox("checkbox.jobsheets", this);
      gridBagConstraints.anchor = 21;
      ServiceDialog.addToGB(this.cbJobSheets, this, gridBagLayout, gridBagConstraints);
      JPanel jPanel = new JPanel();
      this.lblPriority = new JLabel(ServiceDialog.getMsg("label.priority"), 11);
      this.lblPriority.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.priority"));
      jPanel.add(this.lblPriority);
      this.snModel = new SpinnerNumberModel(1, 1, 100, 1);
      this.spinPriority = new JSpinner(this.snModel);
      this.lblPriority.setLabelFor(this.spinPriority);
      ((JSpinner.NumberEditor)this.spinPriority.getEditor()).getTextField().setColumns(3);
      this.spinPriority.addChangeListener(this);
      jPanel.add(this.spinPriority);
      gridBagConstraints.anchor = 22;
      gridBagConstraints.gridwidth = 0;
      jPanel.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.priority"));
      ServiceDialog.addToGB(jPanel, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 10;
      gridBagConstraints.weightx = 0.0D;
      gridBagConstraints.gridwidth = 1;
      char c1 = ServiceDialog.getMnemonic("label.jobname");
      this.lblJobName = new JLabel(ServiceDialog.getMsg("label.jobname"), 11);
      this.lblJobName.setDisplayedMnemonic(c1);
      ServiceDialog.addToGB(this.lblJobName, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.gridwidth = 0;
      this.tfJobName = new JTextField();
      this.lblJobName.setLabelFor(this.tfJobName);
      this.tfJobName.addFocusListener(this);
      this.tfJobName.setFocusAccelerator(c1);
      this.tfJobName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.jobname"));
      ServiceDialog.addToGB(this.tfJobName, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weightx = 0.0D;
      gridBagConstraints.gridwidth = 1;
      char c2 = ServiceDialog.getMnemonic("label.username");
      this.lblUserName = new JLabel(ServiceDialog.getMsg("label.username"), 11);
      this.lblUserName.setDisplayedMnemonic(c2);
      ServiceDialog.addToGB(this.lblUserName, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.tfUserName = new JTextField();
      this.lblUserName.setLabelFor(this.tfUserName);
      this.tfUserName.addFocusListener(this);
      this.tfUserName.setFocusAccelerator(c2);
      this.tfUserName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.username"));
      ServiceDialog.addToGB(this.tfUserName, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.cbJobSheets.isSelected()) {
        ServiceDialog.this.asCurrent.add(JobSheets.STANDARD);
      } else {
        ServiceDialog.this.asCurrent.add(JobSheets.NONE);
      } 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) { ServiceDialog.this.asCurrent.add(new JobPriority(this.snModel.getNumber().intValue())); }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      Object object = param1FocusEvent.getSource();
      if (object == this.tfJobName) {
        ServiceDialog.this.asCurrent.add(new JobName(this.tfJobName.getText(), Locale.getDefault()));
      } else if (object == this.tfUserName) {
        ServiceDialog.this.asCurrent.add(new RequestingUserName(this.tfUserName.getText(), Locale.getDefault()));
      } 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {}
    
    public void updateInfo() {
      Class clazz1 = JobSheets.class;
      Class clazz2 = JobPriority.class;
      Class clazz3 = JobName.class;
      Class clazz4 = RequestingUserName.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz1))
        bool1 = true; 
      JobSheets jobSheets = (JobSheets)ServiceDialog.this.asCurrent.get(clazz1);
      if (jobSheets == null) {
        jobSheets = (JobSheets)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz1);
        if (jobSheets == null)
          jobSheets = JobSheets.NONE; 
      } 
      this.cbJobSheets.setSelected((jobSheets != JobSheets.NONE));
      this.cbJobSheets.setEnabled(bool1);
      if (!ServiceDialog.this.isAWT && ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz2))
        bool2 = true; 
      JobPriority jobPriority = (JobPriority)ServiceDialog.this.asCurrent.get(clazz2);
      if (jobPriority == null) {
        jobPriority = (JobPriority)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz2);
        if (jobPriority == null)
          jobPriority = new JobPriority(1); 
      } 
      int i = jobPriority.getValue();
      if (i < 1 || i > 100)
        i = 1; 
      this.snModel.setValue(new Integer(i));
      this.lblPriority.setEnabled(bool2);
      this.spinPriority.setEnabled(bool2);
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz3))
        bool3 = true; 
      JobName jobName = (JobName)ServiceDialog.this.asCurrent.get(clazz3);
      if (jobName == null) {
        jobName = (JobName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz3);
        if (jobName == null)
          jobName = new JobName("", Locale.getDefault()); 
      } 
      this.tfJobName.setText(jobName.getValue());
      this.tfJobName.setEnabled(bool3);
      this.lblJobName.setEnabled(bool3);
      if (!ServiceDialog.this.isAWT && ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz4))
        bool4 = true; 
      RequestingUserName requestingUserName = (RequestingUserName)ServiceDialog.this.asCurrent.get(clazz4);
      if (requestingUserName == null) {
        requestingUserName = (RequestingUserName)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz4);
        if (requestingUserName == null)
          requestingUserName = new RequestingUserName("", Locale.getDefault()); 
      } 
      this.tfUserName.setText(requestingUserName.getValue());
      this.tfUserName.setEnabled(bool4);
      this.lblUserName.setEnabled(bool4);
    }
  }
  
  private class MarginsPanel extends JPanel implements ActionListener, FocusListener {
    private final String strTitle = ServiceDialog.getMsg("border.margins");
    
    private JFormattedTextField leftMargin;
    
    private JFormattedTextField rightMargin;
    
    private JFormattedTextField topMargin;
    
    private JFormattedTextField bottomMargin;
    
    private JLabel lblLeft;
    
    private JLabel lblRight;
    
    private JLabel lblTop;
    
    private JLabel lblBottom;
    
    private int units = 1000;
    
    private float lmVal = -1.0F;
    
    private float rmVal = -1.0F;
    
    private float tmVal = -1.0F;
    
    private float bmVal = -1.0F;
    
    private Float lmObj;
    
    private Float rmObj;
    
    private Float tmObj;
    
    private Float bmObj;
    
    public MarginsPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 0.0D;
      gridBagConstraints.insets = compInsets;
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      String str1 = "label.millimetres";
      String str2 = Locale.getDefault().getCountry();
      if (str2 != null && (str2.equals("") || str2.equals(Locale.US.getCountry()) || str2.equals(Locale.CANADA.getCountry()))) {
        str1 = "label.inches";
        this.units = 25400;
      } 
      String str3 = ServiceDialog.getMsg(str1);
      if (this.units == 1000) {
        decimalFormat = new DecimalFormat("###.##");
        decimalFormat.setMaximumIntegerDigits(3);
      } else {
        decimalFormat = new DecimalFormat("##.##");
        decimalFormat.setMaximumIntegerDigits(2);
      } 
      decimalFormat.setMinimumFractionDigits(1);
      decimalFormat.setMaximumFractionDigits(2);
      decimalFormat.setMinimumIntegerDigits(1);
      decimalFormat.setParseIntegerOnly(false);
      decimalFormat.setDecimalSeparatorAlwaysShown(true);
      NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
      numberFormatter.setMinimum(new Float(0.0F));
      numberFormatter.setMaximum(new Float(999.0F));
      numberFormatter.setAllowsInvalid(true);
      numberFormatter.setCommitsOnValidEdit(true);
      this.leftMargin = new JFormattedTextField(numberFormatter);
      this.leftMargin.addFocusListener(this);
      this.leftMargin.addActionListener(this);
      this.leftMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.leftmargin"));
      this.rightMargin = new JFormattedTextField(numberFormatter);
      this.rightMargin.addFocusListener(this);
      this.rightMargin.addActionListener(this);
      this.rightMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rightmargin"));
      this.topMargin = new JFormattedTextField(numberFormatter);
      this.topMargin.addFocusListener(this);
      this.topMargin.addActionListener(this);
      this.topMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.topmargin"));
      this.topMargin = new JFormattedTextField(numberFormatter);
      this.bottomMargin = new JFormattedTextField(numberFormatter);
      this.bottomMargin.addFocusListener(this);
      this.bottomMargin.addActionListener(this);
      this.bottomMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.bottommargin"));
      this.topMargin = new JFormattedTextField(numberFormatter);
      gridBagConstraints.gridwidth = -1;
      this.lblLeft = new JLabel(ServiceDialog.getMsg("label.leftmargin") + " " + str3, 10);
      this.lblLeft.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.leftmargin"));
      this.lblLeft.setLabelFor(this.leftMargin);
      ServiceDialog.addToGB(this.lblLeft, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.lblRight = new JLabel(ServiceDialog.getMsg("label.rightmargin") + " " + str3, 10);
      this.lblRight.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.rightmargin"));
      this.lblRight.setLabelFor(this.rightMargin);
      ServiceDialog.addToGB(this.lblRight, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = -1;
      ServiceDialog.addToGB(this.leftMargin, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.rightMargin, this, gridBagLayout, gridBagConstraints);
      ServiceDialog.addToGB(new JPanel(), this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = -1;
      this.lblTop = new JLabel(ServiceDialog.getMsg("label.topmargin") + " " + str3, 10);
      this.lblTop.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.topmargin"));
      this.lblTop.setLabelFor(this.topMargin);
      ServiceDialog.addToGB(this.lblTop, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.lblBottom = new JLabel(ServiceDialog.getMsg("label.bottommargin") + " " + str3, 10);
      this.lblBottom.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.bottommargin"));
      this.lblBottom.setLabelFor(this.bottomMargin);
      ServiceDialog.addToGB(this.lblBottom, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = -1;
      ServiceDialog.addToGB(this.topMargin, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.bottomMargin, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      updateMargins(object);
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      Object object = param1FocusEvent.getSource();
      updateMargins(object);
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {}
    
    public void updateMargins(Object param1Object) {
      if (!(param1Object instanceof JFormattedTextField))
        return; 
      JFormattedTextField jFormattedTextField = (JFormattedTextField)param1Object;
      Float float2 = (Float)jFormattedTextField.getValue();
      if (float2 == null)
        return; 
      if (jFormattedTextField == this.leftMargin && float2.equals(this.lmObj))
        return; 
      if (jFormattedTextField == this.rightMargin && float2.equals(this.rmObj))
        return; 
      if (jFormattedTextField == this.topMargin && float2.equals(this.tmObj))
        return; 
      if (jFormattedTextField == this.bottomMargin && float2.equals(this.bmObj))
        return; 
      Float float1 = (Float)this.leftMargin.getValue();
      float2 = (Float)this.rightMargin.getValue();
      Float float3 = (Float)this.topMargin.getValue();
      Float float4 = (Float)this.bottomMargin.getValue();
      float f1 = float1.floatValue();
      float f2 = float2.floatValue();
      float f3 = float3.floatValue();
      float f4 = float4.floatValue();
      Class clazz = OrientationRequested.class;
      OrientationRequested orientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(clazz);
      if (orientationRequested == null)
        orientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz); 
      if (orientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
        float f = f1;
        f1 = f2;
        f2 = f;
        f = f3;
        f3 = f4;
        f4 = f;
      } else if (orientationRequested == OrientationRequested.LANDSCAPE) {
        float f = f1;
        f1 = f3;
        f3 = f2;
        f2 = f4;
        f4 = f;
      } else if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
        float f = f1;
        f1 = f4;
        f4 = f2;
        f2 = f3;
        f3 = f;
      } 
      MediaPrintableArea mediaPrintableArea;
      if ((mediaPrintableArea = validateMargins(f1, f2, f3, f4)) != null) {
        ServiceDialog.this.asCurrent.add(mediaPrintableArea);
        this.lmVal = f1;
        this.rmVal = f2;
        this.tmVal = f3;
        this.bmVal = f4;
        this.lmObj = float1;
        this.rmObj = float2;
        this.tmObj = float3;
        this.bmObj = float4;
      } else {
        if (this.lmObj == null || this.rmObj == null || this.tmObj == null || this.rmObj == null)
          return; 
        this.leftMargin.setValue(this.lmObj);
        this.rightMargin.setValue(this.rmObj);
        this.topMargin.setValue(this.tmObj);
        this.bottomMargin.setValue(this.bmObj);
      } 
    }
    
    private MediaPrintableArea validateMargins(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      Class clazz = MediaPrintableArea.class;
      MediaPrintableArea mediaPrintableArea = null;
      MediaSize mediaSize = null;
      Media media = (Media)ServiceDialog.this.asCurrent.get(Media.class);
      if (media == null || !(media instanceof MediaSizeName))
        media = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class); 
      if (media != null && media instanceof MediaSizeName) {
        MediaSizeName mediaSizeName = (MediaSizeName)media;
        mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
      } 
      if (mediaSize == null)
        mediaSize = new MediaSize(8.5F, 11.0F, 25400); 
      if (media != null) {
        HashPrintRequestAttributeSet hashPrintRequestAttributeSet = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
        hashPrintRequestAttributeSet.add(media);
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, hashPrintRequestAttributeSet);
        if (object instanceof MediaPrintableArea[] && (MediaPrintableArea[])object.length > 0)
          mediaPrintableArea = (MediaPrintableArea[])object[0]; 
      } 
      if (mediaPrintableArea == null)
        mediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, mediaSize.getX(this.units), mediaSize.getY(this.units), this.units); 
      float f1 = mediaSize.getX(this.units);
      float f2 = mediaSize.getY(this.units);
      float f3 = param1Float1;
      float f4 = param1Float3;
      float f5 = f1 - param1Float1 - param1Float2;
      float f6 = f2 - param1Float3 - param1Float4;
      return (f5 <= 0.0F || f6 <= 0.0F || f3 < 0.0F || f4 < 0.0F || f3 < mediaPrintableArea.getX(this.units) || f5 > mediaPrintableArea.getWidth(this.units) || f4 < mediaPrintableArea.getY(this.units) || f6 > mediaPrintableArea.getHeight(this.units)) ? null : new MediaPrintableArea(param1Float1, param1Float3, f5, f6, this.units);
    }
    
    public void updateInfo() {
      float f5;
      float f4;
      if (ServiceDialog.this.isAWT) {
        this.leftMargin.setEnabled(false);
        this.rightMargin.setEnabled(false);
        this.topMargin.setEnabled(false);
        this.bottomMargin.setEnabled(false);
        this.lblLeft.setEnabled(false);
        this.lblRight.setEnabled(false);
        this.lblTop.setEnabled(false);
        this.lblBottom.setEnabled(false);
        return;
      } 
      Class clazz1 = MediaPrintableArea.class;
      MediaPrintableArea mediaPrintableArea1 = (MediaPrintableArea)ServiceDialog.this.asCurrent.get(clazz1);
      MediaPrintableArea mediaPrintableArea2 = null;
      MediaSize mediaSize = null;
      Media media = (Media)ServiceDialog.this.asCurrent.get(Media.class);
      if (media == null || !(media instanceof MediaSizeName))
        media = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class); 
      if (media != null && media instanceof MediaSizeName) {
        MediaSizeName mediaSizeName = (MediaSizeName)media;
        mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
      } 
      if (mediaSize == null)
        mediaSize = new MediaSize(8.5F, 11.0F, 25400); 
      if (media != null) {
        HashPrintRequestAttributeSet hashPrintRequestAttributeSet = new HashPrintRequestAttributeSet(ServiceDialog.this.asCurrent);
        hashPrintRequestAttributeSet.add(media);
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz1, ServiceDialog.this.docFlavor, hashPrintRequestAttributeSet);
        if (object instanceof MediaPrintableArea[] && (MediaPrintableArea[])object.length > 0) {
          mediaPrintableArea2 = (MediaPrintableArea[])object[0];
        } else if (object instanceof MediaPrintableArea) {
          mediaPrintableArea2 = (MediaPrintableArea)object;
        } 
      } 
      if (mediaPrintableArea2 == null)
        mediaPrintableArea2 = new MediaPrintableArea(0.0F, 0.0F, mediaSize.getX(this.units), mediaSize.getY(this.units), this.units); 
      float f1 = mediaSize.getX(25400);
      float f2 = mediaSize.getY(25400);
      float f3 = 5.0F;
      if (f1 > f3) {
        f4 = 1.0F;
      } else {
        f4 = f1 / f3;
      } 
      if (f2 > f3) {
        f5 = 1.0F;
      } else {
        f5 = f2 / f3;
      } 
      if (mediaPrintableArea1 == null) {
        mediaPrintableArea1 = new MediaPrintableArea(f4, f5, f1 - 2.0F * f4, f2 - 2.0F * f5, 25400);
        ServiceDialog.this.asCurrent.add(mediaPrintableArea1);
      } 
      float f6 = mediaPrintableArea1.getX(this.units);
      float f7 = mediaPrintableArea1.getY(this.units);
      float f8 = mediaPrintableArea1.getWidth(this.units);
      float f9 = mediaPrintableArea1.getHeight(this.units);
      float f10 = mediaPrintableArea2.getX(this.units);
      float f11 = mediaPrintableArea2.getY(this.units);
      float f12 = mediaPrintableArea2.getWidth(this.units);
      float f13 = mediaPrintableArea2.getHeight(this.units);
      boolean bool = false;
      f1 = mediaSize.getX(this.units);
      f2 = mediaSize.getY(this.units);
      if (this.lmVal >= 0.0F) {
        bool = true;
        if (this.lmVal + this.rmVal > f1) {
          if (f8 > f12)
            f8 = f12; 
          f6 = (f1 - f8) / 2.0F;
        } else {
          f6 = (this.lmVal >= f10) ? this.lmVal : f10;
          f8 = f1 - f6 - this.rmVal;
        } 
        if (this.tmVal + this.bmVal > f2) {
          if (f9 > f13)
            f9 = f13; 
          f7 = (f2 - f9) / 2.0F;
        } else {
          f7 = (this.tmVal >= f11) ? this.tmVal : f11;
          f9 = f2 - f7 - this.bmVal;
        } 
      } 
      if (f6 < f10) {
        bool = true;
        f6 = f10;
      } 
      if (f7 < f11) {
        bool = true;
        f7 = f11;
      } 
      if (f8 > f12) {
        bool = true;
        f8 = f12;
      } 
      if (f9 > f13) {
        bool = true;
        f9 = f13;
      } 
      if (f6 + f8 > f10 + f12 || f8 <= 0.0F) {
        bool = true;
        f6 = f10;
        f8 = f12;
      } 
      if (f7 + f9 > f11 + f13 || f9 <= 0.0F) {
        bool = true;
        f7 = f11;
        f9 = f13;
      } 
      if (bool) {
        mediaPrintableArea1 = new MediaPrintableArea(f6, f7, f8, f9, this.units);
        ServiceDialog.this.asCurrent.add(mediaPrintableArea1);
      } 
      this.lmVal = f6;
      this.tmVal = f7;
      this.rmVal = mediaSize.getX(this.units) - f6 - f8;
      this.bmVal = mediaSize.getY(this.units) - f7 - f9;
      this.lmObj = new Float(this.lmVal);
      this.rmObj = new Float(this.rmVal);
      this.tmObj = new Float(this.tmVal);
      this.bmObj = new Float(this.bmVal);
      Class clazz2 = OrientationRequested.class;
      OrientationRequested orientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(clazz2);
      if (orientationRequested == null)
        orientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz2); 
      if (orientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
        Float float = this.lmObj;
        this.lmObj = this.rmObj;
        this.rmObj = float;
        float = this.tmObj;
        this.tmObj = this.bmObj;
        this.bmObj = float;
      } else if (orientationRequested == OrientationRequested.LANDSCAPE) {
        Float float = this.lmObj;
        this.lmObj = this.bmObj;
        this.bmObj = this.rmObj;
        this.rmObj = this.tmObj;
        this.tmObj = float;
      } else if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
        Float float = this.lmObj;
        this.lmObj = this.tmObj;
        this.tmObj = this.rmObj;
        this.rmObj = this.bmObj;
        this.bmObj = float;
      } 
      this.leftMargin.setValue(this.lmObj);
      this.rightMargin.setValue(this.rmObj);
      this.topMargin.setValue(this.tmObj);
      this.bottomMargin.setValue(this.bmObj);
    }
  }
  
  private class MediaPanel extends JPanel implements ItemListener {
    private final String strTitle = ServiceDialog.getMsg("border.media");
    
    private JLabel lblSize;
    
    private JLabel lblSource;
    
    private JComboBox cbSize;
    
    private JComboBox cbSource;
    
    private Vector sizes = new Vector();
    
    private Vector sources = new Vector();
    
    private ServiceDialog.MarginsPanel pnlMargins = null;
    
    public MediaPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      this.cbSize = new JComboBox();
      this.cbSource = new JComboBox();
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = compInsets;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.weightx = 0.0D;
      this.lblSize = new JLabel(ServiceDialog.getMsg("label.size"), 11);
      this.lblSize.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.size"));
      this.lblSize.setLabelFor(this.cbSize);
      ServiceDialog.addToGB(this.lblSize, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.cbSize, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weightx = 0.0D;
      gridBagConstraints.gridwidth = 1;
      this.lblSource = new JLabel(ServiceDialog.getMsg("label.source"), 11);
      this.lblSource.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.source"));
      this.lblSource.setLabelFor(this.cbSource);
      ServiceDialog.addToGB(this.lblSource, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.cbSource, this, gridBagLayout, gridBagConstraints);
    }
    
    private String getMediaName(String param1String) {
      try {
        String str = param1String.replace(' ', '-');
        str = str.replace('#', 'n');
        return messageRB.getString(str);
      } catch (MissingResourceException missingResourceException) {
        return param1String;
      } 
    }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {
      Object object = param1ItemEvent.getSource();
      if (param1ItemEvent.getStateChange() == 1) {
        if (object == this.cbSize) {
          int i = this.cbSize.getSelectedIndex();
          if (i >= 0 && i < this.sizes.size()) {
            if (this.cbSource.getItemCount() > 1 && this.cbSource.getSelectedIndex() >= 1) {
              int j = this.cbSource.getSelectedIndex() - 1;
              MediaTray mediaTray = (MediaTray)this.sources.get(j);
              ServiceDialog.this.asCurrent.add(new SunAlternateMedia(mediaTray));
            } 
            ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(i));
          } 
        } else if (object == this.cbSource) {
          int i = this.cbSource.getSelectedIndex();
          if (i >= 1 && i < this.sources.size() + 1) {
            ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
            MediaTray mediaTray = (MediaTray)this.sources.get(i - 1);
            Media media = (Media)ServiceDialog.this.asCurrent.get(Media.class);
            if (media == null || media instanceof MediaTray) {
              ServiceDialog.this.asCurrent.add(mediaTray);
            } else if (media instanceof MediaSizeName) {
              MediaSizeName mediaSizeName = (MediaSizeName)media;
              Media media1 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Media.class);
              if (media1 instanceof MediaSizeName && media1.equals(mediaSizeName)) {
                ServiceDialog.this.asCurrent.add(mediaTray);
              } else {
                ServiceDialog.this.asCurrent.add(new SunAlternateMedia(mediaTray));
              } 
            } 
          } else if (i == 0) {
            ServiceDialog.this.asCurrent.remove(SunAlternateMedia.class);
            if (this.cbSize.getItemCount() > 0) {
              int j = this.cbSize.getSelectedIndex();
              ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(j));
            } 
          } 
        } 
        if (this.pnlMargins != null)
          this.pnlMargins.updateInfo(); 
      } 
    }
    
    public void addMediaListener(ServiceDialog.MarginsPanel param1MarginsPanel) { this.pnlMargins = param1MarginsPanel; }
    
    public void updateInfo() {
      Class clazz1 = Media.class;
      Class clazz2 = SunAlternateMedia.class;
      boolean bool1 = false;
      this.cbSize.removeItemListener(this);
      this.cbSize.removeAllItems();
      this.cbSource.removeItemListener(this);
      this.cbSource.removeAllItems();
      this.cbSource.addItem(getMediaName("auto-select"));
      this.sizes.clear();
      this.sources.clear();
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz1)) {
        bool1 = true;
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
        if (object instanceof Media[]) {
          Media[] arrayOfMedia = (Media[])object;
          for (byte b = 0; b < arrayOfMedia.length; b++) {
            Media media = arrayOfMedia[b];
            if (media instanceof MediaSizeName) {
              this.sizes.add(media);
              this.cbSize.addItem(getMediaName(media.toString()));
            } else if (media instanceof MediaTray) {
              this.sources.add(media);
              this.cbSource.addItem(getMediaName(media.toString()));
            } 
          } 
        } 
      } 
      boolean bool2 = (bool1 && this.sizes.size() > 0);
      this.lblSize.setEnabled(bool2);
      this.cbSize.setEnabled(bool2);
      if (ServiceDialog.this.isAWT) {
        this.cbSource.setEnabled(false);
        this.lblSource.setEnabled(false);
      } else {
        this.cbSource.setEnabled(bool1);
      } 
      if (bool1) {
        Media media1 = (Media)ServiceDialog.this.asCurrent.get(clazz1);
        Media media2 = (Media)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz1);
        if (media2 instanceof MediaSizeName)
          this.cbSize.setSelectedIndex((this.sizes.size() > 0) ? this.sizes.indexOf(media2) : -1); 
        if (media1 == null || !ServiceDialog.this.psCurrent.isAttributeValueSupported(media1, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
          media1 = media2;
          if (media1 == null && this.sizes.size() > 0)
            media1 = (Media)this.sizes.get(0); 
          if (media1 != null)
            ServiceDialog.this.asCurrent.add(media1); 
        } 
        if (media1 != null) {
          if (media1 instanceof MediaSizeName) {
            MediaSizeName mediaSizeName = (MediaSizeName)media1;
            this.cbSize.setSelectedIndex(this.sizes.indexOf(mediaSizeName));
          } else if (media1 instanceof MediaTray) {
            MediaTray mediaTray = (MediaTray)media1;
            this.cbSource.setSelectedIndex(this.sources.indexOf(mediaTray) + 1);
          } 
        } else {
          this.cbSize.setSelectedIndex((this.sizes.size() > 0) ? 0 : -1);
          this.cbSource.setSelectedIndex(0);
        } 
        SunAlternateMedia sunAlternateMedia = (SunAlternateMedia)ServiceDialog.this.asCurrent.get(clazz2);
        if (sunAlternateMedia != null) {
          Media media = sunAlternateMedia.getMedia();
          if (media instanceof MediaTray) {
            MediaTray mediaTray = (MediaTray)media;
            this.cbSource.setSelectedIndex(this.sources.indexOf(mediaTray) + 1);
          } 
        } 
        int i = this.cbSize.getSelectedIndex();
        if (i >= 0 && i < this.sizes.size())
          ServiceDialog.this.asCurrent.add((MediaSizeName)this.sizes.get(i)); 
        i = this.cbSource.getSelectedIndex();
        if (i >= 1 && i < this.sources.size() + 1) {
          MediaTray mediaTray = (MediaTray)this.sources.get(i - 1);
          if (media1 instanceof MediaTray) {
            ServiceDialog.this.asCurrent.add(mediaTray);
          } else {
            ServiceDialog.this.asCurrent.add(new SunAlternateMedia(mediaTray));
          } 
        } 
      } 
      this.cbSize.addItemListener(this);
      this.cbSource.addItemListener(this);
    }
  }
  
  private class OrientationPanel extends JPanel implements ActionListener {
    private final String strTitle = ServiceDialog.getMsg("border.orientation");
    
    private ServiceDialog.IconRadioButton rbPortrait;
    
    private ServiceDialog.IconRadioButton rbLandscape;
    
    private ServiceDialog.IconRadioButton rbRevPortrait;
    
    private ServiceDialog.IconRadioButton rbRevLandscape;
    
    private ServiceDialog.MarginsPanel pnlMargins = null;
    
    public OrientationPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = compInsets;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridwidth = 0;
      ButtonGroup buttonGroup = new ButtonGroup();
      this.rbPortrait = new ServiceDialog.IconRadioButton(this$0, "radiobutton.portrait", "orientPortrait.png", true, buttonGroup, this);
      this.rbPortrait.addActionListener(this);
      ServiceDialog.addToGB(this.rbPortrait, this, gridBagLayout, gridBagConstraints);
      this.rbLandscape = new ServiceDialog.IconRadioButton(this$0, "radiobutton.landscape", "orientLandscape.png", false, buttonGroup, this);
      this.rbLandscape.addActionListener(this);
      ServiceDialog.addToGB(this.rbLandscape, this, gridBagLayout, gridBagConstraints);
      this.rbRevPortrait = new ServiceDialog.IconRadioButton(this$0, "radiobutton.revportrait", "orientRevPortrait.png", false, buttonGroup, this);
      this.rbRevPortrait.addActionListener(this);
      ServiceDialog.addToGB(this.rbRevPortrait, this, gridBagLayout, gridBagConstraints);
      this.rbRevLandscape = new ServiceDialog.IconRadioButton(this$0, "radiobutton.revlandscape", "orientRevLandscape.png", false, buttonGroup, this);
      this.rbRevLandscape.addActionListener(this);
      ServiceDialog.addToGB(this.rbRevLandscape, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      if (this.rbPortrait.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(OrientationRequested.PORTRAIT);
      } else if (this.rbLandscape.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(OrientationRequested.LANDSCAPE);
      } else if (this.rbRevPortrait.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
      } else if (this.rbRevLandscape.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
      } 
      if (this.pnlMargins != null)
        this.pnlMargins.updateInfo(); 
    }
    
    void addOrientationListener(ServiceDialog.MarginsPanel param1MarginsPanel) { this.pnlMargins = param1MarginsPanel; }
    
    public void updateInfo() {
      Class clazz = OrientationRequested.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      if (ServiceDialog.this.isAWT) {
        bool1 = true;
        bool2 = true;
      } else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
        if (object instanceof OrientationRequested[]) {
          OrientationRequested[] arrayOfOrientationRequested = (OrientationRequested[])object;
          for (byte b = 0; b < arrayOfOrientationRequested.length; b++) {
            OrientationRequested orientationRequested1 = arrayOfOrientationRequested[b];
            if (orientationRequested1 == OrientationRequested.PORTRAIT) {
              bool1 = true;
            } else if (orientationRequested1 == OrientationRequested.LANDSCAPE) {
              bool2 = true;
            } else if (orientationRequested1 == OrientationRequested.REVERSE_PORTRAIT) {
              bool3 = true;
            } else if (orientationRequested1 == OrientationRequested.REVERSE_LANDSCAPE) {
              bool4 = true;
            } 
          } 
        } 
      } 
      this.rbPortrait.setEnabled(bool1);
      this.rbLandscape.setEnabled(bool2);
      this.rbRevPortrait.setEnabled(bool3);
      this.rbRevLandscape.setEnabled(bool4);
      OrientationRequested orientationRequested = (OrientationRequested)ServiceDialog.this.asCurrent.get(clazz);
      if (orientationRequested == null || !ServiceDialog.this.psCurrent.isAttributeValueSupported(orientationRequested, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
        orientationRequested = (OrientationRequested)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
        if (orientationRequested != null && !ServiceDialog.this.psCurrent.isAttributeValueSupported(orientationRequested, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent)) {
          orientationRequested = null;
          Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
          if (object instanceof OrientationRequested[]) {
            OrientationRequested[] arrayOfOrientationRequested = (OrientationRequested[])object;
            if (arrayOfOrientationRequested.length > 1)
              orientationRequested = arrayOfOrientationRequested[0]; 
          } 
        } 
        if (orientationRequested == null)
          orientationRequested = OrientationRequested.PORTRAIT; 
        ServiceDialog.this.asCurrent.add(orientationRequested);
      } 
      if (orientationRequested == OrientationRequested.PORTRAIT) {
        this.rbPortrait.setSelected(true);
      } else if (orientationRequested == OrientationRequested.LANDSCAPE) {
        this.rbLandscape.setSelected(true);
      } else if (orientationRequested == OrientationRequested.REVERSE_PORTRAIT) {
        this.rbRevPortrait.setSelected(true);
      } else {
        this.rbRevLandscape.setSelected(true);
      } 
    }
  }
  
  private class PageSetupPanel extends JPanel {
    private ServiceDialog.MediaPanel pnlMedia;
    
    private ServiceDialog.OrientationPanel pnlOrientation;
    
    private ServiceDialog.MarginsPanel pnlMargins;
    
    public PageSetupPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = panelInsets;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridwidth = 0;
      this.pnlMedia = new ServiceDialog.MediaPanel(this$0);
      ServiceDialog.addToGB(this.pnlMedia, this, gridBagLayout, gridBagConstraints);
      this.pnlOrientation = new ServiceDialog.OrientationPanel(this$0);
      gridBagConstraints.gridwidth = -1;
      ServiceDialog.addToGB(this.pnlOrientation, this, gridBagLayout, gridBagConstraints);
      this.pnlMargins = new ServiceDialog.MarginsPanel(this$0);
      this.pnlOrientation.addOrientationListener(this.pnlMargins);
      this.pnlMedia.addMediaListener(this.pnlMargins);
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.pnlMargins, this, gridBagLayout, gridBagConstraints);
    }
    
    public void updateInfo() {
      this.pnlMedia.updateInfo();
      this.pnlOrientation.updateInfo();
      this.pnlMargins.updateInfo();
    }
  }
  
  private class PrintRangePanel extends JPanel implements ActionListener, FocusListener {
    private final String strTitle = ServiceDialog.getMsg("border.printrange");
    
    private final PageRanges prAll = new PageRanges(1, 2147483647);
    
    private JRadioButton rbAll;
    
    private JRadioButton rbPages;
    
    private JRadioButton rbSelect;
    
    private JFormattedTextField tfRangeFrom;
    
    private JFormattedTextField tfRangeTo;
    
    private JLabel lblRangeTo;
    
    private boolean prSupported;
    
    public PrintRangePanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = compInsets;
      gridBagConstraints.gridwidth = 0;
      ButtonGroup buttonGroup = new ButtonGroup();
      JPanel jPanel1 = new JPanel(new FlowLayout(3));
      this.rbAll = ServiceDialog.createRadioButton("radiobutton.rangeall", this);
      this.rbAll.setSelected(true);
      buttonGroup.add(this.rbAll);
      jPanel1.add(this.rbAll);
      ServiceDialog.addToGB(jPanel1, this, gridBagLayout, gridBagConstraints);
      JPanel jPanel2 = new JPanel(new FlowLayout(3));
      this.rbPages = ServiceDialog.createRadioButton("radiobutton.rangepages", this);
      buttonGroup.add(this.rbPages);
      jPanel2.add(this.rbPages);
      DecimalFormat decimalFormat = new DecimalFormat("####0");
      decimalFormat.setMinimumFractionDigits(0);
      decimalFormat.setMaximumFractionDigits(0);
      decimalFormat.setMinimumIntegerDigits(0);
      decimalFormat.setMaximumIntegerDigits(5);
      decimalFormat.setParseIntegerOnly(true);
      decimalFormat.setDecimalSeparatorAlwaysShown(false);
      NumberFormatter numberFormatter1 = new NumberFormatter(decimalFormat);
      numberFormatter1.setMinimum(new Integer(1));
      numberFormatter1.setMaximum(new Integer(2147483647));
      numberFormatter1.setAllowsInvalid(true);
      numberFormatter1.setCommitsOnValidEdit(true);
      this.tfRangeFrom = new JFormattedTextField(numberFormatter1);
      this.tfRangeFrom.setColumns(4);
      this.tfRangeFrom.setEnabled(false);
      this.tfRangeFrom.addActionListener(this);
      this.tfRangeFrom.addFocusListener(this);
      this.tfRangeFrom.setFocusLostBehavior(3);
      this.tfRangeFrom.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("radiobutton.rangepages"));
      jPanel2.add(this.tfRangeFrom);
      this.lblRangeTo = new JLabel(ServiceDialog.getMsg("label.rangeto"));
      this.lblRangeTo.setEnabled(false);
      jPanel2.add(this.lblRangeTo);
      try {
        numberFormatter2 = (NumberFormatter)numberFormatter1.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        numberFormatter2 = new NumberFormatter();
      } 
      this.tfRangeTo = new JFormattedTextField(numberFormatter2);
      this.tfRangeTo.setColumns(4);
      this.tfRangeTo.setEnabled(false);
      this.tfRangeTo.addFocusListener(this);
      this.tfRangeTo.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rangeto"));
      jPanel2.add(this.tfRangeTo);
      ServiceDialog.addToGB(jPanel2, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      SunPageSelection sunPageSelection = SunPageSelection.ALL;
      setupRangeWidgets();
      if (object == this.rbAll) {
        ServiceDialog.this.asCurrent.add(this.prAll);
      } else if (object == this.rbSelect) {
        sunPageSelection = SunPageSelection.SELECTION;
      } else if (object == this.rbPages || object == this.tfRangeFrom || object == this.tfRangeTo) {
        updateRangeAttribute();
        sunPageSelection = SunPageSelection.RANGE;
      } 
      if (ServiceDialog.this.isAWT)
        ServiceDialog.this.asCurrent.add(sunPageSelection); 
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      Object object = param1FocusEvent.getSource();
      if (object == this.tfRangeFrom || object == this.tfRangeTo)
        updateRangeAttribute(); 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {}
    
    private void setupRangeWidgets() {
      boolean bool = (this.rbPages.isSelected() && this.prSupported);
      this.tfRangeFrom.setEnabled(bool);
      this.tfRangeTo.setEnabled(bool);
      this.lblRangeTo.setEnabled(bool);
    }
    
    private void updateRangeAttribute() {
      byte b2;
      byte b1;
      String str1 = this.tfRangeFrom.getText();
      String str2 = this.tfRangeTo.getText();
      try {
        b1 = Integer.parseInt(str1);
      } catch (NumberFormatException numberFormatException) {
        b1 = 1;
      } 
      try {
        b2 = Integer.parseInt(str2);
      } catch (NumberFormatException numberFormatException) {
        b2 = b1;
      } 
      if (b1 < 1) {
        b1 = 1;
        this.tfRangeFrom.setValue(new Integer(1));
      } 
      if (b2 < b1) {
        b2 = b1;
        this.tfRangeTo.setValue(new Integer(b1));
      } 
      PageRanges pageRanges = new PageRanges(b1, b2);
      ServiceDialog.this.asCurrent.add(pageRanges);
    }
    
    public void updateInfo() {
      Class clazz = PageRanges.class;
      this.prSupported = false;
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz) || ServiceDialog.this.isAWT)
        this.prSupported = true; 
      SunPageSelection sunPageSelection = SunPageSelection.ALL;
      int i = 1;
      int j = 1;
      PageRanges pageRanges = (PageRanges)ServiceDialog.this.asCurrent.get(clazz);
      if (pageRanges != null && !pageRanges.equals(this.prAll)) {
        sunPageSelection = SunPageSelection.RANGE;
        int[][] arrayOfInt = pageRanges.getMembers();
        if (arrayOfInt.length > 0 && arrayOfInt[0].length > 1) {
          i = arrayOfInt[0][0];
          j = arrayOfInt[0][1];
        } 
      } 
      if (ServiceDialog.this.isAWT)
        sunPageSelection = (SunPageSelection)ServiceDialog.this.asCurrent.get(SunPageSelection.class); 
      if (sunPageSelection == SunPageSelection.ALL) {
        this.rbAll.setSelected(true);
      } else if (sunPageSelection != SunPageSelection.SELECTION) {
        this.rbPages.setSelected(true);
      } 
      this.tfRangeFrom.setValue(new Integer(i));
      this.tfRangeTo.setValue(new Integer(j));
      this.rbAll.setEnabled(this.prSupported);
      this.rbPages.setEnabled(this.prSupported);
      setupRangeWidgets();
    }
  }
  
  private class PrintServicePanel extends JPanel implements ActionListener, ItemListener, PopupMenuListener {
    private final String strTitle = ServiceDialog.getMsg("border.printservice");
    
    private FilePermission printToFilePermission;
    
    private JButton btnProperties;
    
    private JCheckBox cbPrintToFile;
    
    private JComboBox cbName;
    
    private JLabel lblType;
    
    private JLabel lblStatus;
    
    private JLabel lblInfo;
    
    private ServiceUIFactory uiFactory;
    
    private boolean changedService = false;
    
    private boolean filePermission;
    
    public PrintServicePanel() {
      this.uiFactory = this$0.psCurrent.getServiceUIFactory();
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      String[] arrayOfString = new String[this$0.services.length];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString[b] = this$0.services[b].getName(); 
      this.cbName = new JComboBox(arrayOfString);
      this.cbName.setSelectedIndex(this$0.defaultServiceIndex);
      this.cbName.addItemListener(this);
      this.cbName.addPopupMenuListener(this);
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = compInsets;
      gridBagConstraints.weightx = 0.0D;
      JLabel jLabel = new JLabel(ServiceDialog.getMsg("label.psname"), 11);
      jLabel.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.psname"));
      jLabel.setLabelFor(this.cbName);
      ServiceDialog.addToGB(jLabel, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.gridwidth = -1;
      ServiceDialog.addToGB(this.cbName, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weightx = 0.0D;
      gridBagConstraints.gridwidth = 0;
      this.btnProperties = ServiceDialog.createButton("button.properties", this);
      ServiceDialog.addToGB(this.btnProperties, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.weighty = 1.0D;
      this.lblStatus = addLabel(ServiceDialog.getMsg("label.status"), gridBagLayout, gridBagConstraints);
      this.lblStatus.setLabelFor(null);
      this.lblType = addLabel(ServiceDialog.getMsg("label.pstype"), gridBagLayout, gridBagConstraints);
      this.lblType.setLabelFor(null);
      gridBagConstraints.gridwidth = 1;
      ServiceDialog.addToGB(new JLabel(ServiceDialog.getMsg("label.info"), 11), this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = -1;
      this.lblInfo = new JLabel();
      this.lblInfo.setLabelFor(null);
      ServiceDialog.addToGB(this.lblInfo, this, gridBagLayout, gridBagConstraints);
      gridBagConstraints.gridwidth = 0;
      this.cbPrintToFile = ServiceDialog.createCheckBox("checkbox.printtofile", this);
      ServiceDialog.addToGB(this.cbPrintToFile, this, gridBagLayout, gridBagConstraints);
      this.filePermission = allowedToPrintToFile();
    }
    
    public boolean isPrintToFileSelected() { return this.cbPrintToFile.isSelected(); }
    
    private JLabel addLabel(String param1String, GridBagLayout param1GridBagLayout, GridBagConstraints param1GridBagConstraints) {
      param1GridBagConstraints.gridwidth = 1;
      ServiceDialog.addToGB(new JLabel(param1String, 11), this, param1GridBagLayout, param1GridBagConstraints);
      param1GridBagConstraints.gridwidth = 0;
      JLabel jLabel = new JLabel();
      ServiceDialog.addToGB(jLabel, this, param1GridBagLayout, param1GridBagConstraints);
      return jLabel;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      if (object == this.btnProperties && this.uiFactory != null) {
        JDialog jDialog = (JDialog)this.uiFactory.getUI(3, "javax.swing.JDialog");
        if (jDialog != null) {
          jDialog.show();
        } else {
          DocumentPropertiesUI documentPropertiesUI = null;
          try {
            documentPropertiesUI = (DocumentPropertiesUI)this.uiFactory.getUI(199, DocumentPropertiesUI.DOCPROPERTIESCLASSNAME);
          } catch (Exception exception) {}
          if (documentPropertiesUI != null) {
            PrinterJobWrapper printerJobWrapper = (PrinterJobWrapper)ServiceDialog.this.asCurrent.get(PrinterJobWrapper.class);
            if (printerJobWrapper == null)
              return; 
            PrinterJob printerJob = printerJobWrapper.getPrinterJob();
            if (printerJob == null)
              return; 
            PrintRequestAttributeSet printRequestAttributeSet = documentPropertiesUI.showDocumentProperties(printerJob, ServiceDialog.this, ServiceDialog.this.psCurrent, ServiceDialog.this.asCurrent);
            if (printRequestAttributeSet != null) {
              ServiceDialog.this.asCurrent.addAll(printRequestAttributeSet);
              ServiceDialog.this.updatePanels();
            } 
          } 
        } 
      } 
    }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {
      if (param1ItemEvent.getStateChange() == 1) {
        int i = this.cbName.getSelectedIndex();
        if (i >= 0 && i < ServiceDialog.this.services.length && !ServiceDialog.this.services[i].equals(ServiceDialog.this.psCurrent)) {
          ServiceDialog.this.psCurrent = ServiceDialog.this.services[i];
          this.uiFactory = ServiceDialog.this.psCurrent.getServiceUIFactory();
          this.changedService = true;
          Destination destination = (Destination)ServiceDialog.this.asOriginal.get(Destination.class);
          if ((destination != null || isPrintToFileSelected()) && ServiceDialog.this.psCurrent.isAttributeCategorySupported(Destination.class)) {
            if (destination != null) {
              ServiceDialog.this.asCurrent.add(destination);
            } else {
              destination = (Destination)ServiceDialog.this.psCurrent.getDefaultAttributeValue(Destination.class);
              if (destination == null)
                try {
                  destination = new Destination(new URI("file:out.prn"));
                } catch (URISyntaxException uRISyntaxException) {} 
              if (destination != null)
                ServiceDialog.this.asCurrent.add(destination); 
            } 
          } else {
            ServiceDialog.this.asCurrent.remove(Destination.class);
          } 
        } 
      } 
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent param1PopupMenuEvent) { this.changedService = false; }
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent param1PopupMenuEvent) {
      if (this.changedService) {
        this.changedService = false;
        ServiceDialog.this.updatePanels();
      } 
    }
    
    public void popupMenuCanceled(PopupMenuEvent param1PopupMenuEvent) {}
    
    private boolean allowedToPrintToFile() {
      try {
        throwPrintToFile();
        return true;
      } catch (SecurityException securityException) {
        return false;
      } 
    }
    
    private void throwPrintToFile() {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        if (this.printToFilePermission == null)
          this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write"); 
        securityManager.checkPermission(this.printToFilePermission);
      } 
    }
    
    public void updateInfo() {
      Class clazz = Destination.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool = this.filePermission ? allowedToPrintToFile() : 0;
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz))
        bool1 = true; 
      Destination destination = (Destination)ServiceDialog.this.asCurrent.get(clazz);
      if (destination != null)
        bool2 = true; 
      this.cbPrintToFile.setEnabled((bool1 && bool));
      this.cbPrintToFile.setSelected((bool2 && bool && bool1));
      PrintServiceAttribute printServiceAttribute1 = ServiceDialog.this.psCurrent.getAttribute(javax.print.attribute.standard.PrinterMakeAndModel.class);
      if (printServiceAttribute1 != null)
        this.lblType.setText(printServiceAttribute1.toString()); 
      PrintServiceAttribute printServiceAttribute2 = ServiceDialog.this.psCurrent.getAttribute(javax.print.attribute.standard.PrinterIsAcceptingJobs.class);
      if (printServiceAttribute2 != null)
        this.lblStatus.setText(ServiceDialog.getMsg(printServiceAttribute2.toString())); 
      PrintServiceAttribute printServiceAttribute3 = ServiceDialog.this.psCurrent.getAttribute(javax.print.attribute.standard.PrinterInfo.class);
      if (printServiceAttribute3 != null)
        this.lblInfo.setText(printServiceAttribute3.toString()); 
      this.btnProperties.setEnabled((this.uiFactory != null));
    }
  }
  
  private class QualityPanel extends JPanel implements ActionListener {
    private final String strTitle = ServiceDialog.getMsg("border.quality");
    
    private JRadioButton rbDraft;
    
    private JRadioButton rbNormal;
    
    private JRadioButton rbHigh;
    
    public QualityPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weighty = 1.0D;
      ButtonGroup buttonGroup = new ButtonGroup();
      this.rbDraft = ServiceDialog.createRadioButton("radiobutton.draftq", this);
      buttonGroup.add(this.rbDraft);
      ServiceDialog.addToGB(this.rbDraft, this, gridBagLayout, gridBagConstraints);
      this.rbNormal = ServiceDialog.createRadioButton("radiobutton.normalq", this);
      this.rbNormal.setSelected(true);
      buttonGroup.add(this.rbNormal);
      ServiceDialog.addToGB(this.rbNormal, this, gridBagLayout, gridBagConstraints);
      this.rbHigh = ServiceDialog.createRadioButton("radiobutton.highq", this);
      buttonGroup.add(this.rbHigh);
      ServiceDialog.addToGB(this.rbHigh, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      if (object == this.rbDraft) {
        ServiceDialog.this.asCurrent.add(PrintQuality.DRAFT);
      } else if (object == this.rbNormal) {
        ServiceDialog.this.asCurrent.add(PrintQuality.NORMAL);
      } else if (object == this.rbHigh) {
        ServiceDialog.this.asCurrent.add(PrintQuality.HIGH);
      } 
    }
    
    public void updateInfo() {
      Class clazz = PrintQuality.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      if (ServiceDialog.this.isAWT) {
        bool1 = true;
        bool2 = true;
        bool3 = true;
      } else if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
        if (object instanceof PrintQuality[]) {
          PrintQuality[] arrayOfPrintQuality = (PrintQuality[])object;
          for (byte b = 0; b < arrayOfPrintQuality.length; b++) {
            PrintQuality printQuality1 = arrayOfPrintQuality[b];
            if (printQuality1 == PrintQuality.DRAFT) {
              bool1 = true;
            } else if (printQuality1 == PrintQuality.NORMAL) {
              bool2 = true;
            } else if (printQuality1 == PrintQuality.HIGH) {
              bool3 = true;
            } 
          } 
        } 
      } 
      this.rbDraft.setEnabled(bool1);
      this.rbNormal.setEnabled(bool2);
      this.rbHigh.setEnabled(bool3);
      PrintQuality printQuality = (PrintQuality)ServiceDialog.this.asCurrent.get(clazz);
      if (printQuality == null) {
        printQuality = (PrintQuality)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
        if (printQuality == null)
          printQuality = PrintQuality.NORMAL; 
      } 
      if (printQuality == PrintQuality.DRAFT) {
        this.rbDraft.setSelected(true);
      } else if (printQuality == PrintQuality.NORMAL) {
        this.rbNormal.setSelected(true);
      } else {
        this.rbHigh.setSelected(true);
      } 
    }
  }
  
  private class SidesPanel extends JPanel implements ActionListener {
    private final String strTitle = ServiceDialog.getMsg("border.sides");
    
    private ServiceDialog.IconRadioButton rbOneSide;
    
    private ServiceDialog.IconRadioButton rbTumble;
    
    private ServiceDialog.IconRadioButton rbDuplex;
    
    public SidesPanel() {
      GridBagLayout gridBagLayout = new GridBagLayout();
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      setLayout(gridBagLayout);
      setBorder(BorderFactory.createTitledBorder(this.strTitle));
      gridBagConstraints.fill = 1;
      gridBagConstraints.insets = compInsets;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.gridwidth = 0;
      ButtonGroup buttonGroup = new ButtonGroup();
      this.rbOneSide = new ServiceDialog.IconRadioButton(this$0, "radiobutton.oneside", "oneside.png", true, buttonGroup, this);
      this.rbOneSide.addActionListener(this);
      ServiceDialog.addToGB(this.rbOneSide, this, gridBagLayout, gridBagConstraints);
      this.rbTumble = new ServiceDialog.IconRadioButton(this$0, "radiobutton.tumble", "tumble.png", false, buttonGroup, this);
      this.rbTumble.addActionListener(this);
      ServiceDialog.addToGB(this.rbTumble, this, gridBagLayout, gridBagConstraints);
      this.rbDuplex = new ServiceDialog.IconRadioButton(this$0, "radiobutton.duplex", "duplex.png", false, buttonGroup, this);
      this.rbDuplex.addActionListener(this);
      gridBagConstraints.gridwidth = 0;
      ServiceDialog.addToGB(this.rbDuplex, this, gridBagLayout, gridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      if (this.rbOneSide.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(Sides.ONE_SIDED);
      } else if (this.rbTumble.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(Sides.TUMBLE);
      } else if (this.rbDuplex.isSameAs(object)) {
        ServiceDialog.this.asCurrent.add(Sides.DUPLEX);
      } 
    }
    
    public void updateInfo() {
      Class clazz = Sides.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      if (ServiceDialog.this.psCurrent.isAttributeCategorySupported(clazz)) {
        Object object = ServiceDialog.this.psCurrent.getSupportedAttributeValues(clazz, ServiceDialog.this.docFlavor, ServiceDialog.this.asCurrent);
        if (object instanceof Sides[]) {
          Sides[] arrayOfSides = (Sides[])object;
          for (byte b = 0; b < arrayOfSides.length; b++) {
            Sides sides1 = arrayOfSides[b];
            if (sides1 == Sides.ONE_SIDED) {
              bool1 = true;
            } else if (sides1 == Sides.TUMBLE) {
              bool2 = true;
            } else if (sides1 == Sides.DUPLEX) {
              bool3 = true;
            } 
          } 
        } 
      } 
      this.rbOneSide.setEnabled(bool1);
      this.rbTumble.setEnabled(bool2);
      this.rbDuplex.setEnabled(bool3);
      Sides sides = (Sides)ServiceDialog.this.asCurrent.get(clazz);
      if (sides == null) {
        sides = (Sides)ServiceDialog.this.psCurrent.getDefaultAttributeValue(clazz);
        if (sides == null)
          sides = Sides.ONE_SIDED; 
      } 
      if (sides == Sides.ONE_SIDED) {
        this.rbOneSide.setSelected(true);
      } else if (sides == Sides.TUMBLE) {
        this.rbTumble.setSelected(true);
      } else {
        this.rbDuplex.setSelected(true);
      } 
    }
  }
  
  private class ValidatingFileChooser extends JFileChooser {
    private ValidatingFileChooser() {}
    
    public void approveSelection() {
      boolean bool;
      File file1 = getSelectedFile();
      try {
        bool = file1.exists();
      } catch (SecurityException securityException) {
        bool = false;
      } 
      if (bool) {
        int i = JOptionPane.showConfirmDialog(this, ServiceDialog.getMsg("dialog.overwrite"), ServiceDialog.getMsg("dialog.owtitle"), 0);
        if (i != 0)
          return; 
      } 
      try {
        if (file1.createNewFile())
          file1.delete(); 
      } catch (IOException iOException) {
        JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + file1, ServiceDialog.getMsg("dialog.owtitle"), 2);
        return;
      } catch (SecurityException securityException) {}
      File file2 = file1.getParentFile();
      if ((file1.exists() && (!file1.isFile() || !file1.canWrite())) || (file2 != null && (!file2.exists() || (file2.exists() && !file2.canWrite())))) {
        JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + file1, ServiceDialog.getMsg("dialog.owtitle"), 2);
        return;
      } 
      super.approveSelection();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\ServiceDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */