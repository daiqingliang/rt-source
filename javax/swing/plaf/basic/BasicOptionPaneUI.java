package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicOptionPaneUI extends OptionPaneUI {
  public static final int MinimumWidth = 262;
  
  public static final int MinimumHeight = 90;
  
  private static String newline = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  
  protected JOptionPane optionPane;
  
  protected Dimension minimumSize;
  
  protected JComponent inputComponent;
  
  protected Component initialFocusComponent;
  
  protected boolean hasCustomComponents;
  
  protected PropertyChangeListener propertyChangeListener;
  
  private Handler handler;
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("close"));
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicOptionPaneUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.optionPane = (JOptionPane)paramJComponent;
    installDefaults();
    this.optionPane.setLayout(createLayoutManager());
    installComponents();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallComponents();
    this.optionPane.setLayout(null);
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    this.optionPane = null;
  }
  
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(this.optionPane, "OptionPane.background", "OptionPane.foreground", "OptionPane.font");
    LookAndFeel.installBorder(this.optionPane, "OptionPane.border");
    this.minimumSize = UIManager.getDimension("OptionPane.minimumSize");
    LookAndFeel.installProperty(this.optionPane, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults() { LookAndFeel.uninstallBorder(this.optionPane); }
  
  protected void installComponents() {
    this.optionPane.add(createMessageArea());
    Container container = createSeparator();
    if (container != null)
      this.optionPane.add(container); 
    this.optionPane.add(createButtonArea());
    this.optionPane.applyComponentOrientation(this.optionPane.getComponentOrientation());
  }
  
  protected void uninstallComponents() {
    this.hasCustomComponents = false;
    this.inputComponent = null;
    this.initialFocusComponent = null;
    this.optionPane.removeAll();
  }
  
  protected LayoutManager createLayoutManager() { return new BoxLayout(this.optionPane, 1); }
  
  protected void installListeners() {
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
      this.optionPane.addPropertyChangeListener(this.propertyChangeListener); 
  }
  
  protected void uninstallListeners() {
    if (this.propertyChangeListener != null) {
      this.optionPane.removePropertyChangeListener(this.propertyChangeListener);
      this.propertyChangeListener = null;
    } 
    this.handler = null;
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(2);
    SwingUtilities.replaceUIInputMap(this.optionPane, 2, inputMap);
    LazyActionMap.installLazyActionMap(this.optionPane, BasicOptionPaneUI.class, "OptionPane.actionMap");
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(this.optionPane, 2, null);
    SwingUtilities.replaceUIActionMap(this.optionPane, null);
  }
  
  InputMap getInputMap(int paramInt) {
    if (paramInt == 2) {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(this.optionPane, this, "OptionPane.windowBindings");
      if (arrayOfObject != null)
        return LookAndFeel.makeComponentInputMap(this.optionPane, arrayOfObject); 
    } 
    return null;
  }
  
  public Dimension getMinimumOptionPaneSize() { return (this.minimumSize == null) ? new Dimension(262, 90) : new Dimension(this.minimumSize.width, this.minimumSize.height); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    if (paramJComponent == this.optionPane) {
      Dimension dimension = getMinimumOptionPaneSize();
      LayoutManager layoutManager = paramJComponent.getLayout();
      if (layoutManager != null) {
        Dimension dimension1 = layoutManager.preferredLayoutSize(paramJComponent);
        return (dimension != null) ? new Dimension(Math.max(dimension1.width, dimension.width), Math.max(dimension1.height, dimension.height)) : dimension1;
      } 
      return dimension;
    } 
    return null;
  }
  
  protected Container createMessageArea() {
    JPanel jPanel1 = new JPanel();
    Border border = (Border)DefaultLookup.get(this.optionPane, this, "OptionPane.messageAreaBorder");
    if (border != null)
      jPanel1.setBorder(border); 
    jPanel1.setLayout(new BorderLayout());
    JPanel jPanel2 = new JPanel(new GridBagLayout());
    JPanel jPanel3 = new JPanel(new BorderLayout());
    jPanel2.setName("OptionPane.body");
    jPanel3.setName("OptionPane.realBody");
    if (getIcon() != null) {
      JPanel jPanel = new JPanel();
      jPanel.setName("OptionPane.separator");
      jPanel.setPreferredSize(new Dimension(15, 1));
      jPanel3.add(jPanel, "Before");
    } 
    jPanel3.add(jPanel2, "Center");
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.anchor = DefaultLookup.getInt(this.optionPane, this, "OptionPane.messageAnchor", 10);
    gridBagConstraints.insets = new Insets(0, 0, 3, 0);
    addMessageComponents(jPanel2, gridBagConstraints, getMessage(), getMaxCharactersPerLineCount(), false);
    jPanel1.add(jPanel3, "Center");
    addIcon(jPanel1);
    return jPanel1;
  }
  
  protected void addMessageComponents(Container paramContainer, GridBagConstraints paramGridBagConstraints, Object paramObject, int paramInt, boolean paramBoolean) {
    if (paramObject == null)
      return; 
    if (paramObject instanceof Component) {
      if (paramObject instanceof JScrollPane || paramObject instanceof JPanel) {
        paramGridBagConstraints.fill = 1;
        paramGridBagConstraints.weighty = 1.0D;
      } else {
        paramGridBagConstraints.fill = 2;
      } 
      paramGridBagConstraints.weightx = 1.0D;
      paramContainer.add((Component)paramObject, paramGridBagConstraints);
      paramGridBagConstraints.weightx = 0.0D;
      paramGridBagConstraints.weighty = 0.0D;
      paramGridBagConstraints.fill = 0;
      paramGridBagConstraints.gridy++;
      if (!paramBoolean)
        this.hasCustomComponents = true; 
    } else if (paramObject instanceof Object[]) {
      Object[] arrayOfObject = (Object[])paramObject;
      for (Object object : arrayOfObject)
        addMessageComponents(paramContainer, paramGridBagConstraints, object, paramInt, false); 
    } else if (paramObject instanceof Icon) {
      JLabel jLabel = new JLabel((Icon)paramObject, 0);
      configureMessageLabel(jLabel);
      addMessageComponents(paramContainer, paramGridBagConstraints, jLabel, paramInt, true);
    } else {
      String str = paramObject.toString();
      int i = str.length();
      if (i <= 0)
        return; 
      int k = 0;
      int j;
      if ((j = str.indexOf(newline)) >= 0) {
        k = newline.length();
      } else if ((j = str.indexOf("\r\n")) >= 0) {
        k = 2;
      } else if ((j = str.indexOf('\n')) >= 0) {
        k = 1;
      } 
      if (j >= 0) {
        if (j == 0) {
          JPanel jPanel = new JPanel() {
              public Dimension getPreferredSize() {
                Font font = getFont();
                return (font != null) ? new Dimension(1, font.getSize() + 2) : new Dimension(0, 0);
              }
            };
          jPanel.setName("OptionPane.break");
          addMessageComponents(paramContainer, paramGridBagConstraints, jPanel, paramInt, true);
        } else {
          addMessageComponents(paramContainer, paramGridBagConstraints, str.substring(0, j), paramInt, false);
        } 
        addMessageComponents(paramContainer, paramGridBagConstraints, str.substring(j + k), paramInt, false);
      } else if (i > paramInt) {
        Box box = Box.createVerticalBox();
        box.setName("OptionPane.verticalBox");
        burstStringInto(box, str, paramInt);
        addMessageComponents(paramContainer, paramGridBagConstraints, box, paramInt, true);
      } else {
        JLabel jLabel = new JLabel(str, 10);
        jLabel.setName("OptionPane.label");
        configureMessageLabel(jLabel);
        addMessageComponents(paramContainer, paramGridBagConstraints, jLabel, paramInt, true);
      } 
    } 
  }
  
  protected Object getMessage() {
    this.inputComponent = null;
    if (this.optionPane != null) {
      if (this.optionPane.getWantsInput()) {
        Object[] arrayOfObject2;
        JComponent jComponent;
        Object object1 = this.optionPane.getMessage();
        Object[] arrayOfObject1 = this.optionPane.getSelectionValues();
        Object object2 = this.optionPane.getInitialSelectionValue();
        if (arrayOfObject1 != null) {
          if (arrayOfObject1.length < 20) {
            arrayOfObject2 = new JComboBox();
            arrayOfObject2.setName("OptionPane.comboBox");
            byte b = 0;
            int i = arrayOfObject1.length;
            while (b < i) {
              arrayOfObject2.addItem(arrayOfObject1[b]);
              b++;
            } 
            if (object2 != null)
              arrayOfObject2.setSelectedItem(object2); 
            this.inputComponent = arrayOfObject2;
            jComponent = arrayOfObject2;
          } else {
            arrayOfObject2 = new JList(arrayOfObject1);
            JScrollPane jScrollPane = new JScrollPane(arrayOfObject2);
            jScrollPane.setName("OptionPane.scrollPane");
            arrayOfObject2.setName("OptionPane.list");
            arrayOfObject2.setVisibleRowCount(10);
            arrayOfObject2.setSelectionMode(0);
            if (object2 != null)
              arrayOfObject2.setSelectedValue(object2, true); 
            arrayOfObject2.addMouseListener(getHandler());
            jComponent = jScrollPane;
            this.inputComponent = arrayOfObject2;
          } 
        } else {
          arrayOfObject2 = new MultiplexingTextField(20);
          arrayOfObject2.setName("OptionPane.textField");
          arrayOfObject2.setKeyStrokes(new KeyStroke[] { KeyStroke.getKeyStroke("ENTER") });
          if (object2 != null) {
            String str = object2.toString();
            arrayOfObject2.setText(str);
            arrayOfObject2.setSelectionStart(0);
            arrayOfObject2.setSelectionEnd(str.length());
          } 
          arrayOfObject2.addActionListener(getHandler());
          jComponent = this.inputComponent = arrayOfObject2;
        } 
        if (object1 == null) {
          arrayOfObject2 = new Object[1];
          arrayOfObject2[0] = jComponent;
        } else {
          arrayOfObject2 = new Object[2];
          arrayOfObject2[0] = object1;
          arrayOfObject2[1] = jComponent;
        } 
        return arrayOfObject2;
      } 
      return this.optionPane.getMessage();
    } 
    return null;
  }
  
  protected void addIcon(Container paramContainer) {
    Icon icon = getIcon();
    if (icon != null) {
      JLabel jLabel = new JLabel(icon);
      jLabel.setName("OptionPane.iconLabel");
      jLabel.setVerticalAlignment(1);
      paramContainer.add(jLabel, "Before");
    } 
  }
  
  protected Icon getIcon() {
    Icon icon = (this.optionPane == null) ? null : this.optionPane.getIcon();
    if (icon == null && this.optionPane != null)
      icon = getIconForType(this.optionPane.getMessageType()); 
    return icon;
  }
  
  protected Icon getIconForType(int paramInt) {
    if (paramInt < 0 || paramInt > 3)
      return null; 
    String str = null;
    switch (paramInt) {
      case 0:
        str = "OptionPane.errorIcon";
        break;
      case 1:
        str = "OptionPane.informationIcon";
        break;
      case 2:
        str = "OptionPane.warningIcon";
        break;
      case 3:
        str = "OptionPane.questionIcon";
        break;
    } 
    return (str != null) ? (Icon)DefaultLookup.get(this.optionPane, this, str) : null;
  }
  
  protected int getMaxCharactersPerLineCount() { return this.optionPane.getMaxCharactersPerLineCount(); }
  
  protected void burstStringInto(Container paramContainer, String paramString, int paramInt) {
    int i = paramString.length();
    if (i <= 0)
      return; 
    if (i > paramInt) {
      int j = paramString.lastIndexOf(' ', paramInt);
      if (j <= 0)
        j = paramString.indexOf(' ', paramInt); 
      if (j > 0 && j < i) {
        burstStringInto(paramContainer, paramString.substring(0, j), paramInt);
        burstStringInto(paramContainer, paramString.substring(j + 1), paramInt);
        return;
      } 
    } 
    JLabel jLabel = new JLabel(paramString, 2);
    jLabel.setName("OptionPane.label");
    configureMessageLabel(jLabel);
    paramContainer.add(jLabel);
  }
  
  protected Container createSeparator() { return null; }
  
  protected Container createButtonArea() {
    JPanel jPanel = new JPanel();
    Border border = (Border)DefaultLookup.get(this.optionPane, this, "OptionPane.buttonAreaBorder");
    jPanel.setName("OptionPane.buttonArea");
    if (border != null)
      jPanel.setBorder(border); 
    jPanel.setLayout(new ButtonAreaLayout(DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.sameSizeButtons", true), DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonPadding", 6), DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonOrientation", 0), DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.isYesLast", false)));
    addButtonComponents(jPanel, getButtons(), getInitialValueIndex());
    return jPanel;
  }
  
  protected void addButtonComponents(Container paramContainer, Object[] paramArrayOfObject, int paramInt) {
    if (paramArrayOfObject != null && paramArrayOfObject.length > 0) {
      boolean bool = getSizeButtonsToSameWidth();
      boolean bool1 = true;
      int i = paramArrayOfObject.length;
      JButton[] arrayOfJButton = null;
      int j = 0;
      if (bool)
        arrayOfJButton = new JButton[i]; 
      for (byte b = 0; b < i; b++) {
        JButton jButton;
        Object object = paramArrayOfObject[b];
        if (object instanceof Component) {
          bool1 = false;
          jButton = (Component)object;
          paramContainer.add(jButton);
          this.hasCustomComponents = true;
        } else {
          JButton jButton1;
          if (object instanceof ButtonFactory) {
            jButton1 = ((ButtonFactory)object).createButton();
          } else if (object instanceof Icon) {
            jButton1 = new JButton((Icon)object);
          } else {
            jButton1 = new JButton(object.toString());
          } 
          jButton1.setName("OptionPane.button");
          jButton1.setMultiClickThreshhold(DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonClickThreshhold", 0));
          configureButton(jButton1);
          paramContainer.add(jButton1);
          ActionListener actionListener = createButtonActionListener(b);
          if (actionListener != null)
            jButton1.addActionListener(actionListener); 
          jButton = jButton1;
        } 
        if (bool && bool1 && jButton instanceof JButton) {
          arrayOfJButton[b] = (JButton)jButton;
          j = Math.max(j, (jButton.getMinimumSize()).width);
        } 
        if (b == paramInt) {
          this.initialFocusComponent = jButton;
          if (this.initialFocusComponent instanceof JButton) {
            JButton jButton1 = (JButton)this.initialFocusComponent;
            jButton1.addHierarchyListener(new HierarchyListener() {
                  public void hierarchyChanged(HierarchyEvent param1HierarchyEvent) {
                    if ((param1HierarchyEvent.getChangeFlags() & 0x1L) != 0L) {
                      JButton jButton = (JButton)param1HierarchyEvent.getComponent();
                      JRootPane jRootPane = SwingUtilities.getRootPane(jButton);
                      if (jRootPane != null)
                        jRootPane.setDefaultButton(jButton); 
                    } 
                  }
                });
          } 
        } 
      } 
      ((ButtonAreaLayout)paramContainer.getLayout()).setSyncAllWidths((bool && bool1));
      if (DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.setButtonMargin", true) && bool && bool1) {
        byte b1 = (i <= 2) ? 8 : 4;
        for (byte b2 = 0; b2 < i; b2++) {
          JButton jButton = arrayOfJButton[b2];
          jButton.setMargin(new Insets(2, b1, 2, b1));
        } 
      } 
    } 
  }
  
  protected ActionListener createButtonActionListener(int paramInt) { return new ButtonActionListener(paramInt); }
  
  protected Object[] getButtons() {
    if (this.optionPane != null) {
      Object[] arrayOfObject = this.optionPane.getOptions();
      if (arrayOfObject == null) {
        ButtonFactory[] arrayOfButtonFactory;
        int i = this.optionPane.getOptionType();
        Locale locale = this.optionPane.getLocale();
        int j = DefaultLookup.getInt(this.optionPane, this, "OptionPane.buttonMinimumWidth", -1);
        if (i == 0) {
          arrayOfButtonFactory = new ButtonFactory[2];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.yesButtonText", locale), getMnemonic("OptionPane.yesButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.yesIcon"), j);
          arrayOfButtonFactory[1] = new ButtonFactory(UIManager.getString("OptionPane.noButtonText", locale), getMnemonic("OptionPane.noButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.noIcon"), j);
        } else if (i == 1) {
          arrayOfButtonFactory = new ButtonFactory[3];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.yesButtonText", locale), getMnemonic("OptionPane.yesButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.yesIcon"), j);
          arrayOfButtonFactory[1] = new ButtonFactory(UIManager.getString("OptionPane.noButtonText", locale), getMnemonic("OptionPane.noButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.noIcon"), j);
          arrayOfButtonFactory[2] = new ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", locale), getMnemonic("OptionPane.cancelButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.cancelIcon"), j);
        } else if (i == 2) {
          arrayOfButtonFactory = new ButtonFactory[2];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.okButtonText", locale), getMnemonic("OptionPane.okButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.okIcon"), j);
          arrayOfButtonFactory[1] = new ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", locale), getMnemonic("OptionPane.cancelButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.cancelIcon"), j);
        } else {
          arrayOfButtonFactory = new ButtonFactory[1];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.okButtonText", locale), getMnemonic("OptionPane.okButtonMnemonic", locale), (Icon)DefaultLookup.get(this.optionPane, this, "OptionPane.okIcon"), j);
        } 
        return arrayOfButtonFactory;
      } 
      return arrayOfObject;
    } 
    return null;
  }
  
  private int getMnemonic(String paramString, Locale paramLocale) {
    String str = (String)UIManager.get(paramString, paramLocale);
    if (str == null)
      return 0; 
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException numberFormatException) {
      return 0;
    } 
  }
  
  protected boolean getSizeButtonsToSameWidth() { return true; }
  
  protected int getInitialValueIndex() {
    if (this.optionPane != null) {
      Object object = this.optionPane.getInitialValue();
      Object[] arrayOfObject = this.optionPane.getOptions();
      if (arrayOfObject == null)
        return 0; 
      if (object != null)
        for (int i = arrayOfObject.length - 1; i >= 0; i--) {
          if (arrayOfObject[i].equals(object))
            return i; 
        }  
    } 
    return -1;
  }
  
  protected void resetInputValue() {
    if (this.inputComponent != null && this.inputComponent instanceof JTextField) {
      this.optionPane.setInputValue(((JTextField)this.inputComponent).getText());
    } else if (this.inputComponent != null && this.inputComponent instanceof JComboBox) {
      this.optionPane.setInputValue(((JComboBox)this.inputComponent).getSelectedItem());
    } else if (this.inputComponent != null) {
      this.optionPane.setInputValue(((JList)this.inputComponent).getSelectedValue());
    } 
  }
  
  public void selectInitialValue(JOptionPane paramJOptionPane) {
    if (this.inputComponent != null) {
      this.inputComponent.requestFocus();
    } else {
      if (this.initialFocusComponent != null)
        this.initialFocusComponent.requestFocus(); 
      if (this.initialFocusComponent instanceof JButton) {
        JRootPane jRootPane = SwingUtilities.getRootPane(this.initialFocusComponent);
        if (jRootPane != null)
          jRootPane.setDefaultButton((JButton)this.initialFocusComponent); 
      } 
    } 
  }
  
  public boolean containsCustomComponents(JOptionPane paramJOptionPane) { return this.hasCustomComponents; }
  
  private void configureMessageLabel(JLabel paramJLabel) {
    Color color = (Color)DefaultLookup.get(this.optionPane, this, "OptionPane.messageForeground");
    if (color != null)
      paramJLabel.setForeground(color); 
    Font font = (Font)DefaultLookup.get(this.optionPane, this, "OptionPane.messageFont");
    if (font != null)
      paramJLabel.setFont(font); 
  }
  
  private void configureButton(JButton paramJButton) {
    Font font = (Font)DefaultLookup.get(this.optionPane, this, "OptionPane.buttonFont");
    if (font != null)
      paramJButton.setFont(font); 
  }
  
  static  {
    if (newline == null)
      newline = "\n"; 
  }
  
  private static class Actions extends UIAction {
    private static final String CLOSE = "close";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (getName() == "close") {
        JOptionPane jOptionPane = (JOptionPane)param1ActionEvent.getSource();
        jOptionPane.setValue(Integer.valueOf(-1));
      } 
    }
  }
  
  public class ButtonActionListener implements ActionListener {
    protected int buttonIndex;
    
    public ButtonActionListener(int param1Int) { this.buttonIndex = param1Int; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicOptionPaneUI.this.optionPane != null) {
        int i = BasicOptionPaneUI.this.optionPane.getOptionType();
        Object[] arrayOfObject = BasicOptionPaneUI.this.optionPane.getOptions();
        if (BasicOptionPaneUI.this.inputComponent != null && (arrayOfObject != null || i == -1 || ((i == 0 || i == 1 || i == 2) && this.buttonIndex == 0)))
          BasicOptionPaneUI.this.resetInputValue(); 
        if (arrayOfObject == null) {
          if (i == 2 && this.buttonIndex == 1) {
            BasicOptionPaneUI.this.optionPane.setValue(Integer.valueOf(2));
          } else {
            BasicOptionPaneUI.this.optionPane.setValue(Integer.valueOf(this.buttonIndex));
          } 
        } else {
          BasicOptionPaneUI.this.optionPane.setValue(arrayOfObject[this.buttonIndex]);
        } 
      } 
    }
  }
  
  public static class ButtonAreaLayout implements LayoutManager {
    protected boolean syncAllWidths;
    
    protected int padding;
    
    protected boolean centersChildren;
    
    private int orientation;
    
    private boolean reverseButtons;
    
    private boolean useOrientation;
    
    public ButtonAreaLayout(boolean param1Boolean, int param1Int) {
      this.syncAllWidths = param1Boolean;
      this.padding = param1Int;
      this.centersChildren = true;
      this.useOrientation = false;
    }
    
    ButtonAreaLayout(boolean param1Boolean1, int param1Int1, int param1Int2, boolean param1Boolean2) {
      this(param1Boolean1, param1Int1);
      this.useOrientation = true;
      this.orientation = param1Int2;
      this.reverseButtons = param1Boolean2;
    }
    
    public void setSyncAllWidths(boolean param1Boolean) { this.syncAllWidths = param1Boolean; }
    
    public boolean getSyncAllWidths() { return this.syncAllWidths; }
    
    public void setPadding(int param1Int) { this.padding = param1Int; }
    
    public int getPadding() { return this.padding; }
    
    public void setCentersChildren(boolean param1Boolean) {
      this.centersChildren = param1Boolean;
      this.useOrientation = false;
    }
    
    public boolean getCentersChildren() { return this.centersChildren; }
    
    private int getOrientation(Container param1Container) {
      if (!this.useOrientation)
        return 0; 
      if (param1Container.getComponentOrientation().isLeftToRight())
        return this.orientation; 
      switch (this.orientation) {
        case 2:
          return 4;
        case 4:
          return 2;
        case 0:
          return 0;
      } 
      return 2;
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void layoutContainer(Container param1Container) {
      Component[] arrayOfComponent = param1Container.getComponents();
      if (arrayOfComponent != null && arrayOfComponent.length > 0) {
        int i = arrayOfComponent.length;
        Insets insets = param1Container.getInsets();
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        boolean bool1 = param1Container.getComponentOrientation().isLeftToRight();
        boolean bool2 = bool1 ? this.reverseButtons : (!this.reverseButtons ? 1 : 0);
        int i2;
        for (i2 = 0; i2 < i; i2++) {
          Dimension dimension = arrayOfComponent[i2].getPreferredSize();
          j = Math.max(j, dimension.width);
          k = Math.max(k, dimension.height);
          m += dimension.width;
        } 
        if (getSyncAllWidths())
          m = j * i; 
        m += (i - 1) * this.padding;
        switch (getOrientation(param1Container)) {
          case 2:
            n = insets.left;
            break;
          case 4:
            n = param1Container.getWidth() - insets.right - m;
            break;
          case 0:
            if (getCentersChildren() || i < 2) {
              n = (param1Container.getWidth() - m) / 2;
              break;
            } 
            n = insets.left;
            if (getSyncAllWidths()) {
              i1 = (param1Container.getWidth() - insets.left - insets.right - m) / (i - 1) + j;
              break;
            } 
            i1 = (param1Container.getWidth() - insets.left - insets.right - m) / (i - 1);
            break;
        } 
        for (i2 = 0; i2 < i; i2++) {
          int i3 = bool2 ? (i - i2 - 1) : i2;
          Dimension dimension = arrayOfComponent[i3].getPreferredSize();
          if (getSyncAllWidths()) {
            arrayOfComponent[i3].setBounds(n, insets.top, j, k);
          } else {
            arrayOfComponent[i3].setBounds(n, insets.top, dimension.width, dimension.height);
          } 
          if (i1 != 0) {
            n += i1;
          } else {
            n += arrayOfComponent[i3].getWidth() + this.padding;
          } 
        } 
      } 
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      if (param1Container != null) {
        Component[] arrayOfComponent = param1Container.getComponents();
        if (arrayOfComponent != null && arrayOfComponent.length > 0) {
          int i = arrayOfComponent.length;
          int j = 0;
          Insets insets = param1Container.getInsets();
          int k = insets.top + insets.bottom;
          int m = insets.left + insets.right;
          if (this.syncAllWidths) {
            int i1 = 0;
            for (byte b1 = 0; b1 < i; b1++) {
              Dimension dimension = arrayOfComponent[b1].getPreferredSize();
              j = Math.max(j, dimension.height);
              i1 = Math.max(i1, dimension.width);
            } 
            return new Dimension(m + i1 * i + (i - 1) * this.padding, k + j);
          } 
          int n = 0;
          for (byte b = 0; b < i; b++) {
            Dimension dimension = arrayOfComponent[b].getPreferredSize();
            j = Math.max(j, dimension.height);
            n += dimension.width;
          } 
          n += (i - 1) * this.padding;
          return new Dimension(m + n, k + j);
        } 
      } 
      return new Dimension(0, 0);
    }
    
    public Dimension preferredLayoutSize(Container param1Container) { return minimumLayoutSize(param1Container); }
    
    public void removeLayoutComponent(Component param1Component) {}
  }
  
  private static class ButtonFactory {
    private String text;
    
    private int mnemonic;
    
    private Icon icon;
    
    private int minimumWidth = -1;
    
    ButtonFactory(String param1String, int param1Int1, Icon param1Icon, int param1Int2) {
      this.text = param1String;
      this.mnemonic = param1Int1;
      this.icon = param1Icon;
      this.minimumWidth = param1Int2;
    }
    
    JButton createButton() {
      JButton jButton;
      if (this.minimumWidth > 0) {
        jButton = new ConstrainedButton(this.text, this.minimumWidth);
      } else {
        jButton = new JButton(this.text);
      } 
      if (this.icon != null)
        jButton.setIcon(this.icon); 
      if (this.mnemonic != 0)
        jButton.setMnemonic(this.mnemonic); 
      return jButton;
    }
    
    private static class ConstrainedButton extends JButton {
      int minimumWidth;
      
      ConstrainedButton(String param2String, int param2Int) {
        super(param2String);
        this.minimumWidth = param2Int;
      }
      
      public Dimension getMinimumSize() {
        Dimension dimension = super.getMinimumSize();
        dimension.width = Math.max(dimension.width, this.minimumWidth);
        return dimension;
      }
      
      public Dimension getPreferredSize() {
        Dimension dimension = super.getPreferredSize();
        dimension.width = Math.max(dimension.width, this.minimumWidth);
        return dimension;
      }
    }
  }
  
  private class Handler implements ActionListener, MouseListener, PropertyChangeListener {
    private Handler() {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) { BasicOptionPaneUI.this.optionPane.setInputValue(((JTextField)param1ActionEvent.getSource()).getText()); }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getClickCount() == 2) {
        JList jList = (JList)param1MouseEvent.getSource();
        int i = jList.locationToIndex(param1MouseEvent.getPoint());
        BasicOptionPaneUI.this.optionPane.setInputValue(jList.getModel().getElementAt(i));
        BasicOptionPaneUI.this.optionPane.setValue(Integer.valueOf(0));
      } 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getSource() == BasicOptionPaneUI.this.optionPane) {
        if ("ancestor" == param1PropertyChangeEvent.getPropertyName()) {
          boolean bool;
          JOptionPane jOptionPane = (JOptionPane)param1PropertyChangeEvent.getSource();
          if (param1PropertyChangeEvent.getOldValue() == null) {
            bool = true;
          } else {
            bool = false;
          } 
          switch (jOptionPane.getMessageType()) {
            case -1:
              if (bool)
                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.informationSound"); 
              break;
            case 3:
              if (bool)
                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.questionSound"); 
              break;
            case 1:
              if (bool)
                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.informationSound"); 
              break;
            case 2:
              if (bool)
                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.warningSound"); 
              break;
            case 0:
              if (bool)
                BasicLookAndFeel.playSound(BasicOptionPaneUI.this.optionPane, "OptionPane.errorSound"); 
              break;
            default:
              System.err.println("Undefined JOptionPane type: " + jOptionPane.getMessageType());
              break;
          } 
        } 
        String str = param1PropertyChangeEvent.getPropertyName();
        if (str == "options" || str == "initialValue" || str == "icon" || str == "messageType" || str == "optionType" || str == "message" || str == "selectionValues" || str == "initialSelectionValue" || str == "wantsInput") {
          BasicOptionPaneUI.this.uninstallComponents();
          BasicOptionPaneUI.this.installComponents();
          BasicOptionPaneUI.this.optionPane.validate();
        } else if (str == "componentOrientation") {
          ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
          JOptionPane jOptionPane = (JOptionPane)param1PropertyChangeEvent.getSource();
          if (componentOrientation != param1PropertyChangeEvent.getOldValue())
            jOptionPane.applyComponentOrientation(componentOrientation); 
        } 
      } 
    }
  }
  
  private static class MultiplexingTextField extends JTextField {
    private KeyStroke[] strokes;
    
    MultiplexingTextField(int param1Int) { super(param1Int); }
    
    void setKeyStrokes(KeyStroke[] param1ArrayOfKeyStroke) { this.strokes = param1ArrayOfKeyStroke; }
    
    protected boolean processKeyBinding(KeyStroke param1KeyStroke, KeyEvent param1KeyEvent, int param1Int, boolean param1Boolean) {
      boolean bool = super.processKeyBinding(param1KeyStroke, param1KeyEvent, param1Int, param1Boolean);
      if (bool && param1Int != 2)
        for (int i = this.strokes.length - 1; i >= 0; i--) {
          if (this.strokes[i].equals(param1KeyStroke))
            return false; 
        }  
      return bool;
    }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicOptionPaneUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicOptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */