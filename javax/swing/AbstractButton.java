package javax.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.Serializable;
import java.text.BreakIterator;
import java.util.Enumeration;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

public abstract class AbstractButton extends JComponent implements ItemSelectable, SwingConstants {
  public static final String MODEL_CHANGED_PROPERTY = "model";
  
  public static final String TEXT_CHANGED_PROPERTY = "text";
  
  public static final String MNEMONIC_CHANGED_PROPERTY = "mnemonic";
  
  public static final String MARGIN_CHANGED_PROPERTY = "margin";
  
  public static final String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment";
  
  public static final String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment";
  
  public static final String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition";
  
  public static final String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition";
  
  public static final String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
  
  public static final String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
  
  public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
  
  public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";
  
  public static final String ICON_CHANGED_PROPERTY = "icon";
  
  public static final String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
  
  public static final String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";
  
  public static final String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
  
  public static final String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";
  
  public static final String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
  
  public static final String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon";
  
  protected ButtonModel model = null;
  
  private String text = "";
  
  private Insets margin = null;
  
  private Insets defaultMargin = null;
  
  private Icon defaultIcon = null;
  
  private Icon pressedIcon = null;
  
  private Icon disabledIcon = null;
  
  private Icon selectedIcon = null;
  
  private Icon disabledSelectedIcon = null;
  
  private Icon rolloverIcon = null;
  
  private Icon rolloverSelectedIcon = null;
  
  private boolean paintBorder = true;
  
  private boolean paintFocus = true;
  
  private boolean rolloverEnabled = false;
  
  private boolean contentAreaFilled = true;
  
  private int verticalAlignment = 0;
  
  private int horizontalAlignment = 0;
  
  private int verticalTextPosition = 0;
  
  private int horizontalTextPosition = 11;
  
  private int iconTextGap = 4;
  
  private int mnemonic;
  
  private int mnemonicIndex = -1;
  
  private long multiClickThreshhold = 0L;
  
  private boolean borderPaintedSet = false;
  
  private boolean rolloverEnabledSet = false;
  
  private boolean iconTextGapSet = false;
  
  private boolean contentAreaFilledSet = false;
  
  private boolean setLayout = false;
  
  boolean defaultCapable = true;
  
  private Handler handler;
  
  protected ChangeListener changeListener = null;
  
  protected ActionListener actionListener = null;
  
  protected ItemListener itemListener = null;
  
  protected ChangeEvent changeEvent;
  
  private boolean hideActionText = false;
  
  private Action action;
  
  private PropertyChangeListener actionPropertyChangeListener;
  
  public void setHideActionText(boolean paramBoolean) {
    if (paramBoolean != this.hideActionText) {
      this.hideActionText = paramBoolean;
      if (getAction() != null)
        setTextFromAction(getAction(), false); 
      firePropertyChange("hideActionText", !paramBoolean, paramBoolean);
    } 
  }
  
  public boolean getHideActionText() { return this.hideActionText; }
  
  public String getText() { return this.text; }
  
  public void setText(String paramString) {
    String str = this.text;
    this.text = paramString;
    firePropertyChange("text", str, paramString);
    updateDisplayedMnemonicIndex(paramString, getMnemonic());
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", str, paramString); 
    if (paramString == null || str == null || !paramString.equals(str)) {
      revalidate();
      repaint();
    } 
  }
  
  public boolean isSelected() { return this.model.isSelected(); }
  
  public void setSelected(boolean paramBoolean) {
    boolean bool = isSelected();
    this.model.setSelected(paramBoolean);
  }
  
  public void doClick() { doClick(68); }
  
  public void doClick(int paramInt) {
    Dimension dimension = getSize();
    this.model.setArmed(true);
    this.model.setPressed(true);
    paintImmediately(new Rectangle(0, 0, dimension.width, dimension.height));
    try {
      Thread.currentThread().sleep(paramInt);
    } catch (InterruptedException interruptedException) {}
    this.model.setPressed(false);
    this.model.setArmed(false);
  }
  
  public void setMargin(Insets paramInsets) {
    if (paramInsets instanceof javax.swing.plaf.UIResource) {
      this.defaultMargin = paramInsets;
    } else if (this.margin instanceof javax.swing.plaf.UIResource) {
      this.defaultMargin = this.margin;
    } 
    if (paramInsets == null && this.defaultMargin != null)
      paramInsets = this.defaultMargin; 
    Insets insets = this.margin;
    this.margin = paramInsets;
    firePropertyChange("margin", insets, paramInsets);
    if (insets == null || !insets.equals(paramInsets)) {
      revalidate();
      repaint();
    } 
  }
  
  public Insets getMargin() { return (this.margin == null) ? null : (Insets)this.margin.clone(); }
  
  public Icon getIcon() { return this.defaultIcon; }
  
  public void setIcon(Icon paramIcon) {
    Icon icon = this.defaultIcon;
    this.defaultIcon = paramIcon;
    if (paramIcon != icon && this.disabledIcon instanceof javax.swing.plaf.UIResource)
      this.disabledIcon = null; 
    firePropertyChange("icon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    if (paramIcon != icon) {
      if (paramIcon == null || icon == null || paramIcon.getIconWidth() != icon.getIconWidth() || paramIcon.getIconHeight() != icon.getIconHeight())
        revalidate(); 
      repaint();
    } 
  }
  
  public Icon getPressedIcon() { return this.pressedIcon; }
  
  public void setPressedIcon(Icon paramIcon) {
    Icon icon = this.pressedIcon;
    this.pressedIcon = paramIcon;
    firePropertyChange("pressedIcon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    if (paramIcon != icon && getModel().isPressed())
      repaint(); 
  }
  
  public Icon getSelectedIcon() { return this.selectedIcon; }
  
  public void setSelectedIcon(Icon paramIcon) {
    Icon icon = this.selectedIcon;
    this.selectedIcon = paramIcon;
    if (paramIcon != icon && this.disabledSelectedIcon instanceof javax.swing.plaf.UIResource)
      this.disabledSelectedIcon = null; 
    firePropertyChange("selectedIcon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    if (paramIcon != icon && isSelected())
      repaint(); 
  }
  
  public Icon getRolloverIcon() { return this.rolloverIcon; }
  
  public void setRolloverIcon(Icon paramIcon) {
    Icon icon = this.rolloverIcon;
    this.rolloverIcon = paramIcon;
    firePropertyChange("rolloverIcon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    setRolloverEnabled(true);
    if (paramIcon != icon)
      repaint(); 
  }
  
  public Icon getRolloverSelectedIcon() { return this.rolloverSelectedIcon; }
  
  public void setRolloverSelectedIcon(Icon paramIcon) {
    Icon icon = this.rolloverSelectedIcon;
    this.rolloverSelectedIcon = paramIcon;
    firePropertyChange("rolloverSelectedIcon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    setRolloverEnabled(true);
    if (paramIcon != icon && isSelected())
      repaint(); 
  }
  
  @Transient
  public Icon getDisabledIcon() {
    if (this.disabledIcon == null) {
      this.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, getIcon());
      if (this.disabledIcon != null)
        firePropertyChange("disabledIcon", null, this.disabledIcon); 
    } 
    return this.disabledIcon;
  }
  
  public void setDisabledIcon(Icon paramIcon) {
    Icon icon = this.disabledIcon;
    this.disabledIcon = paramIcon;
    firePropertyChange("disabledIcon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    if (paramIcon != icon && !isEnabled())
      repaint(); 
  }
  
  public Icon getDisabledSelectedIcon() {
    if (this.disabledSelectedIcon == null)
      if (this.selectedIcon != null) {
        this.disabledSelectedIcon = UIManager.getLookAndFeel().getDisabledSelectedIcon(this, getSelectedIcon());
      } else {
        return getDisabledIcon();
      }  
    return this.disabledSelectedIcon;
  }
  
  public void setDisabledSelectedIcon(Icon paramIcon) {
    Icon icon = this.disabledSelectedIcon;
    this.disabledSelectedIcon = paramIcon;
    firePropertyChange("disabledSelectedIcon", icon, paramIcon);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, paramIcon); 
    if (paramIcon != icon) {
      if (paramIcon == null || icon == null || paramIcon.getIconWidth() != icon.getIconWidth() || paramIcon.getIconHeight() != icon.getIconHeight())
        revalidate(); 
      if (!isEnabled() && isSelected())
        repaint(); 
    } 
  }
  
  public int getVerticalAlignment() { return this.verticalAlignment; }
  
  public void setVerticalAlignment(int paramInt) {
    if (paramInt == this.verticalAlignment)
      return; 
    int i = this.verticalAlignment;
    this.verticalAlignment = checkVerticalKey(paramInt, "verticalAlignment");
    firePropertyChange("verticalAlignment", i, this.verticalAlignment);
    repaint();
  }
  
  public int getHorizontalAlignment() { return this.horizontalAlignment; }
  
  public void setHorizontalAlignment(int paramInt) {
    if (paramInt == this.horizontalAlignment)
      return; 
    int i = this.horizontalAlignment;
    this.horizontalAlignment = checkHorizontalKey(paramInt, "horizontalAlignment");
    firePropertyChange("horizontalAlignment", i, this.horizontalAlignment);
    repaint();
  }
  
  public int getVerticalTextPosition() { return this.verticalTextPosition; }
  
  public void setVerticalTextPosition(int paramInt) {
    if (paramInt == this.verticalTextPosition)
      return; 
    int i = this.verticalTextPosition;
    this.verticalTextPosition = checkVerticalKey(paramInt, "verticalTextPosition");
    firePropertyChange("verticalTextPosition", i, this.verticalTextPosition);
    revalidate();
    repaint();
  }
  
  public int getHorizontalTextPosition() { return this.horizontalTextPosition; }
  
  public void setHorizontalTextPosition(int paramInt) {
    if (paramInt == this.horizontalTextPosition)
      return; 
    int i = this.horizontalTextPosition;
    this.horizontalTextPosition = checkHorizontalKey(paramInt, "horizontalTextPosition");
    firePropertyChange("horizontalTextPosition", i, this.horizontalTextPosition);
    revalidate();
    repaint();
  }
  
  public int getIconTextGap() { return this.iconTextGap; }
  
  public void setIconTextGap(int paramInt) {
    int i = this.iconTextGap;
    this.iconTextGap = paramInt;
    this.iconTextGapSet = true;
    firePropertyChange("iconTextGap", i, paramInt);
    if (paramInt != i) {
      revalidate();
      repaint();
    } 
  }
  
  protected int checkHorizontalKey(int paramInt, String paramString) {
    if (paramInt == 2 || paramInt == 0 || paramInt == 4 || paramInt == 10 || paramInt == 11)
      return paramInt; 
    throw new IllegalArgumentException(paramString);
  }
  
  protected int checkVerticalKey(int paramInt, String paramString) {
    if (paramInt == 1 || paramInt == 0 || paramInt == 3)
      return paramInt; 
    throw new IllegalArgumentException(paramString);
  }
  
  public void removeNotify() {
    super.removeNotify();
    if (isRolloverEnabled())
      getModel().setRollover(false); 
  }
  
  public void setActionCommand(String paramString) { getModel().setActionCommand(paramString); }
  
  public String getActionCommand() {
    String str = getModel().getActionCommand();
    if (str == null)
      str = getText(); 
    return str;
  }
  
  public void setAction(Action paramAction) {
    Action action1 = getAction();
    if (this.action == null || !this.action.equals(paramAction)) {
      this.action = paramAction;
      if (action1 != null) {
        removeActionListener(action1);
        action1.removePropertyChangeListener(this.actionPropertyChangeListener);
        this.actionPropertyChangeListener = null;
      } 
      configurePropertiesFromAction(this.action);
      if (this.action != null) {
        if (!isListener(ActionListener.class, this.action))
          addActionListener(this.action); 
        this.actionPropertyChangeListener = createActionPropertyChangeListener(this.action);
        this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
      } 
      firePropertyChange("action", action1, this.action);
    } 
  }
  
  private boolean isListener(Class paramClass, ActionListener paramActionListener) {
    boolean bool = false;
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == paramClass && arrayOfObject[i + true] == paramActionListener)
        bool = true; 
    } 
    return bool;
  }
  
  public Action getAction() { return this.action; }
  
  protected void configurePropertiesFromAction(Action paramAction) {
    setMnemonicFromAction(paramAction);
    setTextFromAction(paramAction, false);
    AbstractAction.setToolTipTextFromAction(this, paramAction);
    setIconFromAction(paramAction);
    setActionCommandFromAction(paramAction);
    AbstractAction.setEnabledFromAction(this, paramAction);
    if (AbstractAction.hasSelectedKey(paramAction) && shouldUpdateSelectedStateFromAction())
      setSelectedFromAction(paramAction); 
    setDisplayedMnemonicIndexFromAction(paramAction, false);
  }
  
  void clientPropertyChanged(Object paramObject1, Object paramObject2, Object paramObject3) {
    if (paramObject1 == "hideActionText") {
      boolean bool = (paramObject3 instanceof Boolean) ? ((Boolean)paramObject3).booleanValue() : 0;
      if (getHideActionText() != bool)
        setHideActionText(bool); 
    } 
  }
  
  boolean shouldUpdateSelectedStateFromAction() { return false; }
  
  protected void actionPropertyChanged(Action paramAction, String paramString) {
    if (paramString == "Name") {
      setTextFromAction(paramAction, true);
    } else if (paramString == "enabled") {
      AbstractAction.setEnabledFromAction(this, paramAction);
    } else if (paramString == "ShortDescription") {
      AbstractAction.setToolTipTextFromAction(this, paramAction);
    } else if (paramString == "SmallIcon") {
      smallIconChanged(paramAction);
    } else if (paramString == "MnemonicKey") {
      setMnemonicFromAction(paramAction);
    } else if (paramString == "ActionCommandKey") {
      setActionCommandFromAction(paramAction);
    } else if (paramString == "SwingSelectedKey" && AbstractAction.hasSelectedKey(paramAction) && shouldUpdateSelectedStateFromAction()) {
      setSelectedFromAction(paramAction);
    } else if (paramString == "SwingDisplayedMnemonicIndexKey") {
      setDisplayedMnemonicIndexFromAction(paramAction, true);
    } else if (paramString == "SwingLargeIconKey") {
      largeIconChanged(paramAction);
    } 
  }
  
  private void setDisplayedMnemonicIndexFromAction(Action paramAction, boolean paramBoolean) {
    Integer integer = (paramAction == null) ? null : (Integer)paramAction.getValue("SwingDisplayedMnemonicIndexKey");
    if (paramBoolean || integer != null) {
      int i;
      if (integer == null) {
        i = -1;
      } else {
        i = integer.intValue();
        String str = getText();
        if (str == null || i >= str.length())
          i = -1; 
      } 
      setDisplayedMnemonicIndex(i);
    } 
  }
  
  private void setMnemonicFromAction(Action paramAction) {
    Integer integer = (paramAction == null) ? null : (Integer)paramAction.getValue("MnemonicKey");
    setMnemonic((integer == null) ? 0 : integer.intValue());
  }
  
  private void setTextFromAction(Action paramAction, boolean paramBoolean) {
    boolean bool = getHideActionText();
    if (!paramBoolean) {
      setText((paramAction != null && !bool) ? (String)paramAction.getValue("Name") : null);
    } else if (!bool) {
      setText((String)paramAction.getValue("Name"));
    } 
  }
  
  void setIconFromAction(Action paramAction) {
    Icon icon = null;
    if (paramAction != null) {
      icon = (Icon)paramAction.getValue("SwingLargeIconKey");
      if (icon == null)
        icon = (Icon)paramAction.getValue("SmallIcon"); 
    } 
    setIcon(icon);
  }
  
  void smallIconChanged(Action paramAction) {
    if (paramAction.getValue("SwingLargeIconKey") == null)
      setIconFromAction(paramAction); 
  }
  
  void largeIconChanged(Action paramAction) { setIconFromAction(paramAction); }
  
  private void setActionCommandFromAction(Action paramAction) { setActionCommand((paramAction != null) ? (String)paramAction.getValue("ActionCommandKey") : null); }
  
  private void setSelectedFromAction(Action paramAction) {
    boolean bool = false;
    if (paramAction != null)
      bool = AbstractAction.isSelected(paramAction); 
    if (bool != isSelected()) {
      setSelected(bool);
      if (!bool && isSelected() && getModel() instanceof DefaultButtonModel) {
        ButtonGroup buttonGroup = ((DefaultButtonModel)getModel()).getGroup();
        if (buttonGroup != null)
          buttonGroup.clearSelection(); 
      } 
    } 
  }
  
  protected PropertyChangeListener createActionPropertyChangeListener(Action paramAction) { return createActionPropertyChangeListener0(paramAction); }
  
  PropertyChangeListener createActionPropertyChangeListener0(Action paramAction) { return new ButtonActionPropertyChangeListener(this, paramAction); }
  
  public boolean isBorderPainted() { return this.paintBorder; }
  
  public void setBorderPainted(boolean paramBoolean) {
    boolean bool = this.paintBorder;
    this.paintBorder = paramBoolean;
    this.borderPaintedSet = true;
    firePropertyChange("borderPainted", bool, this.paintBorder);
    if (paramBoolean != bool) {
      revalidate();
      repaint();
    } 
  }
  
  protected void paintBorder(Graphics paramGraphics) {
    if (isBorderPainted())
      super.paintBorder(paramGraphics); 
  }
  
  public boolean isFocusPainted() { return this.paintFocus; }
  
  public void setFocusPainted(boolean paramBoolean) {
    boolean bool = this.paintFocus;
    this.paintFocus = paramBoolean;
    firePropertyChange("focusPainted", bool, this.paintFocus);
    if (paramBoolean != bool && isFocusOwner()) {
      revalidate();
      repaint();
    } 
  }
  
  public boolean isContentAreaFilled() { return this.contentAreaFilled; }
  
  public void setContentAreaFilled(boolean paramBoolean) {
    boolean bool = this.contentAreaFilled;
    this.contentAreaFilled = paramBoolean;
    this.contentAreaFilledSet = true;
    firePropertyChange("contentAreaFilled", bool, this.contentAreaFilled);
    if (paramBoolean != bool)
      repaint(); 
  }
  
  public boolean isRolloverEnabled() { return this.rolloverEnabled; }
  
  public void setRolloverEnabled(boolean paramBoolean) {
    boolean bool = this.rolloverEnabled;
    this.rolloverEnabled = paramBoolean;
    this.rolloverEnabledSet = true;
    firePropertyChange("rolloverEnabled", bool, this.rolloverEnabled);
    if (paramBoolean != bool)
      repaint(); 
  }
  
  public int getMnemonic() { return this.mnemonic; }
  
  public void setMnemonic(int paramInt) {
    int i = getMnemonic();
    this.model.setMnemonic(paramInt);
    updateMnemonicProperties();
  }
  
  public void setMnemonic(char paramChar) {
    char c = paramChar;
    if (c >= 'a' && c <= 'z')
      c -= ' '; 
    setMnemonic(c);
  }
  
  public void setDisplayedMnemonicIndex(int paramInt) {
    int i = this.mnemonicIndex;
    if (paramInt == -1) {
      this.mnemonicIndex = -1;
    } else {
      String str = getText();
      boolean bool = (str == null) ? 0 : str.length();
      if (paramInt < -1 || paramInt >= bool)
        throw new IllegalArgumentException("index == " + paramInt); 
    } 
    this.mnemonicIndex = paramInt;
    firePropertyChange("displayedMnemonicIndex", i, paramInt);
    if (paramInt != i) {
      revalidate();
      repaint();
    } 
  }
  
  public int getDisplayedMnemonicIndex() { return this.mnemonicIndex; }
  
  private void updateDisplayedMnemonicIndex(String paramString, int paramInt) { setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(paramString, paramInt)); }
  
  private void updateMnemonicProperties() {
    int i = this.model.getMnemonic();
    if (this.mnemonic != i) {
      int j = this.mnemonic;
      this.mnemonic = i;
      firePropertyChange("mnemonic", j, this.mnemonic);
      updateDisplayedMnemonicIndex(getText(), this.mnemonic);
      revalidate();
      repaint();
    } 
  }
  
  public void setMultiClickThreshhold(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("threshhold must be >= 0"); 
    this.multiClickThreshhold = paramLong;
  }
  
  public long getMultiClickThreshhold() { return this.multiClickThreshhold; }
  
  public ButtonModel getModel() { return this.model; }
  
  public void setModel(ButtonModel paramButtonModel) {
    ButtonModel buttonModel = getModel();
    if (buttonModel != null) {
      buttonModel.removeChangeListener(this.changeListener);
      buttonModel.removeActionListener(this.actionListener);
      buttonModel.removeItemListener(this.itemListener);
      this.changeListener = null;
      this.actionListener = null;
      this.itemListener = null;
    } 
    this.model = paramButtonModel;
    if (paramButtonModel != null) {
      this.changeListener = createChangeListener();
      this.actionListener = createActionListener();
      this.itemListener = createItemListener();
      paramButtonModel.addChangeListener(this.changeListener);
      paramButtonModel.addActionListener(this.actionListener);
      paramButtonModel.addItemListener(this.itemListener);
      updateMnemonicProperties();
      super.setEnabled(paramButtonModel.isEnabled());
    } else {
      this.mnemonic = 0;
    } 
    updateDisplayedMnemonicIndex(getText(), this.mnemonic);
    firePropertyChange("model", buttonModel, paramButtonModel);
    if (paramButtonModel != buttonModel) {
      revalidate();
      repaint();
    } 
  }
  
  public ButtonUI getUI() { return (ButtonUI)this.ui; }
  
  public void setUI(ButtonUI paramButtonUI) {
    setUI(paramButtonUI);
    if (this.disabledIcon instanceof javax.swing.plaf.UIResource)
      setDisabledIcon(null); 
    if (this.disabledSelectedIcon instanceof javax.swing.plaf.UIResource)
      setDisabledSelectedIcon(null); 
  }
  
  public void updateUI() {}
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    if (!this.setLayout)
      setLayout(new OverlayLayout(this)); 
    super.addImpl(paramComponent, paramObject, paramInt);
  }
  
  public void setLayout(LayoutManager paramLayoutManager) {
    this.setLayout = true;
    super.setLayout(paramLayoutManager);
  }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public void addActionListener(ActionListener paramActionListener) { this.listenerList.add(ActionListener.class, paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener != null && getAction() == paramActionListener) {
      setAction(null);
    } else {
      this.listenerList.remove(ActionListener.class, paramActionListener);
    } 
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])this.listenerList.getListeners(ActionListener.class); }
  
  protected ChangeListener createChangeListener() { return getHandler(); }
  
  protected void fireActionPerformed(ActionEvent paramActionEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ActionEvent actionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class) {
        if (actionEvent == null) {
          String str = paramActionEvent.getActionCommand();
          if (str == null)
            str = getActionCommand(); 
          actionEvent = new ActionEvent(this, 1001, str, paramActionEvent.getWhen(), paramActionEvent.getModifiers());
        } 
        ((ActionListener)arrayOfObject[i + 1]).actionPerformed(actionEvent);
      } 
    } 
  }
  
  protected void fireItemStateChanged(ItemEvent paramItemEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ItemEvent itemEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ItemListener.class) {
        if (itemEvent == null)
          itemEvent = new ItemEvent(this, 701, this, paramItemEvent.getStateChange()); 
        ((ItemListener)arrayOfObject[i + 1]).itemStateChanged(itemEvent);
      } 
    } 
    if (this.accessibleContext != null)
      if (paramItemEvent.getStateChange() == 1) {
        this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED);
        this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(0), Integer.valueOf(1));
      } else {
        this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
        this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(1), Integer.valueOf(0));
      }  
  }
  
  protected ActionListener createActionListener() { return getHandler(); }
  
  protected ItemListener createItemListener() { return getHandler(); }
  
  public void setEnabled(boolean paramBoolean) {
    if (!paramBoolean && this.model.isRollover())
      this.model.setRollover(false); 
    super.setEnabled(paramBoolean);
    this.model.setEnabled(paramBoolean);
  }
  
  @Deprecated
  public String getLabel() { return getText(); }
  
  @Deprecated
  public void setLabel(String paramString) { setText(paramString); }
  
  public void addItemListener(ItemListener paramItemListener) { this.listenerList.add(ItemListener.class, paramItemListener); }
  
  public void removeItemListener(ItemListener paramItemListener) { this.listenerList.remove(ItemListener.class, paramItemListener); }
  
  public ItemListener[] getItemListeners() { return (ItemListener[])this.listenerList.getListeners(ItemListener.class); }
  
  public Object[] getSelectedObjects() {
    if (!isSelected())
      return null; 
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = getText();
    return arrayOfObject;
  }
  
  protected void init(String paramString, Icon paramIcon) {
    if (paramString != null)
      setText(paramString); 
    if (paramIcon != null)
      setIcon(paramIcon); 
    updateUI();
    setAlignmentX(0.0F);
    setAlignmentY(0.5F);
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    Icon icon = null;
    if (!this.model.isEnabled()) {
      if (this.model.isSelected()) {
        icon = getDisabledSelectedIcon();
      } else {
        icon = getDisabledIcon();
      } 
    } else if (this.model.isPressed() && this.model.isArmed()) {
      icon = getPressedIcon();
    } else if (isRolloverEnabled() && this.model.isRollover()) {
      if (this.model.isSelected()) {
        icon = getRolloverSelectedIcon();
      } else {
        icon = getRolloverIcon();
      } 
    } else if (this.model.isSelected()) {
      icon = getSelectedIcon();
    } 
    if (icon == null)
      icon = getIcon(); 
    return (icon == null || !SwingUtilities.doesIconReferenceImage(icon, paramImage)) ? false : super.imageUpdate(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  void setUIProperty(String paramString, Object paramObject) {
    if (paramString == "borderPainted") {
      if (!this.borderPaintedSet) {
        setBorderPainted(((Boolean)paramObject).booleanValue());
        this.borderPaintedSet = false;
      } 
    } else if (paramString == "rolloverEnabled") {
      if (!this.rolloverEnabledSet) {
        setRolloverEnabled(((Boolean)paramObject).booleanValue());
        this.rolloverEnabledSet = false;
      } 
    } else if (paramString == "iconTextGap") {
      if (!this.iconTextGapSet) {
        setIconTextGap(((Number)paramObject).intValue());
        this.iconTextGapSet = false;
      } 
    } else if (paramString == "contentAreaFilled") {
      if (!this.contentAreaFilledSet) {
        setContentAreaFilled(((Boolean)paramObject).booleanValue());
        this.contentAreaFilledSet = false;
      } 
    } else {
      super.setUIProperty(paramString, paramObject);
    } 
  }
  
  protected String paramString() {
    String str1 = (this.defaultIcon != null && this.defaultIcon != this) ? this.defaultIcon.toString() : "";
    String str2 = (this.pressedIcon != null && this.pressedIcon != this) ? this.pressedIcon.toString() : "";
    String str3 = (this.disabledIcon != null && this.disabledIcon != this) ? this.disabledIcon.toString() : "";
    String str4 = (this.selectedIcon != null && this.selectedIcon != this) ? this.selectedIcon.toString() : "";
    String str5 = (this.disabledSelectedIcon != null && this.disabledSelectedIcon != this) ? this.disabledSelectedIcon.toString() : "";
    String str6 = (this.rolloverIcon != null && this.rolloverIcon != this) ? this.rolloverIcon.toString() : "";
    String str7 = (this.rolloverSelectedIcon != null && this.rolloverSelectedIcon != this) ? this.rolloverSelectedIcon.toString() : "";
    String str8 = this.paintBorder ? "true" : "false";
    String str9 = this.paintFocus ? "true" : "false";
    String str10 = this.rolloverEnabled ? "true" : "false";
    return super.paramString() + ",defaultIcon=" + str1 + ",disabledIcon=" + str3 + ",disabledSelectedIcon=" + str5 + ",margin=" + this.margin + ",paintBorder=" + str8 + ",paintFocus=" + str9 + ",pressedIcon=" + str2 + ",rolloverEnabled=" + str10 + ",rolloverIcon=" + str6 + ",rolloverSelectedIcon=" + str7 + ",selectedIcon=" + str4 + ",text=" + this.text;
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(); 
    return this.handler;
  }
  
  protected abstract class AccessibleAbstractButton extends JComponent.AccessibleJComponent implements AccessibleAction, AccessibleValue, AccessibleText, AccessibleExtendedComponent {
    protected AccessibleAbstractButton() { super(AbstractButton.this); }
    
    public String getAccessibleName() {
      String str = this.accessibleName;
      if (str == null)
        str = (String)AbstractButton.this.getClientProperty("AccessibleName"); 
      if (str == null)
        str = AbstractButton.this.getText(); 
      if (str == null)
        str = super.getAccessibleName(); 
      return str;
    }
    
    public AccessibleIcon[] getAccessibleIcon() {
      Icon icon = AbstractButton.this.getIcon();
      if (icon instanceof Accessible) {
        AccessibleContext accessibleContext = ((Accessible)icon).getAccessibleContext();
        if (accessibleContext != null && accessibleContext instanceof AccessibleIcon)
          return new AccessibleIcon[] { (AccessibleIcon)accessibleContext }; 
      } 
      return null;
    }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (AbstractButton.this.getModel().isArmed())
        accessibleStateSet.add(AccessibleState.ARMED); 
      if (AbstractButton.this.isFocusOwner())
        accessibleStateSet.add(AccessibleState.FOCUSED); 
      if (AbstractButton.this.getModel().isPressed())
        accessibleStateSet.add(AccessibleState.PRESSED); 
      if (AbstractButton.this.isSelected())
        accessibleStateSet.add(AccessibleState.CHECKED); 
      return accessibleStateSet;
    }
    
    public AccessibleRelationSet getAccessibleRelationSet() {
      AccessibleRelationSet accessibleRelationSet = super.getAccessibleRelationSet();
      if (!accessibleRelationSet.contains(AccessibleRelation.MEMBER_OF)) {
        ButtonModel buttonModel = AbstractButton.this.getModel();
        if (buttonModel != null && buttonModel instanceof DefaultButtonModel) {
          ButtonGroup buttonGroup = ((DefaultButtonModel)buttonModel).getGroup();
          if (buttonGroup != null) {
            int i = buttonGroup.getButtonCount();
            Object[] arrayOfObject = new Object[i];
            Enumeration enumeration = buttonGroup.getElements();
            for (byte b = 0; b < i; b++) {
              if (enumeration.hasMoreElements())
                arrayOfObject[b] = enumeration.nextElement(); 
            } 
            AccessibleRelation accessibleRelation = new AccessibleRelation(AccessibleRelation.MEMBER_OF);
            accessibleRelation.setTarget(arrayOfObject);
            accessibleRelationSet.add(accessibleRelation);
          } 
        } 
      } 
      return accessibleRelationSet;
    }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public int getAccessibleActionCount() { return 1; }
    
    public String getAccessibleActionDescription(int param1Int) { return (param1Int == 0) ? UIManager.getString("AbstractButton.clickText") : null; }
    
    public boolean doAccessibleAction(int param1Int) {
      if (param1Int == 0) {
        AbstractButton.this.doClick();
        return true;
      } 
      return false;
    }
    
    public Number getCurrentAccessibleValue() { return AbstractButton.this.isSelected() ? Integer.valueOf(1) : Integer.valueOf(0); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number == null)
        return false; 
      int i = param1Number.intValue();
      if (i == 0) {
        AbstractButton.this.setSelected(false);
      } else {
        AbstractButton.this.setSelected(true);
      } 
      return true;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(0); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(1); }
    
    public AccessibleText getAccessibleText() {
      View view = (View)AbstractButton.this.getClientProperty("html");
      return (view != null) ? this : null;
    }
    
    public int getIndexAtPoint(Point param1Point) {
      View view = (View)AbstractButton.this.getClientProperty("html");
      if (view != null) {
        Rectangle rectangle = getTextRectangle();
        if (rectangle == null)
          return -1; 
        Rectangle2D.Float float = new Rectangle2D.Float(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        Position.Bias[] arrayOfBias = new Position.Bias[1];
        return view.viewToModel(param1Point.x, param1Point.y, float, arrayOfBias);
      } 
      return -1;
    }
    
    public Rectangle getCharacterBounds(int param1Int) {
      View view = (View)AbstractButton.this.getClientProperty("html");
      if (view != null) {
        Rectangle rectangle = getTextRectangle();
        if (rectangle == null)
          return null; 
        Rectangle2D.Float float = new Rectangle2D.Float(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        try {
          Shape shape = view.modelToView(param1Int, float, Position.Bias.Forward);
          return shape.getBounds();
        } catch (BadLocationException badLocationException) {
          return null;
        } 
      } 
      return null;
    }
    
    public int getCharCount() {
      View view = (View)AbstractButton.this.getClientProperty("html");
      if (view != null) {
        Document document = view.getDocument();
        if (document instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)document;
          return styledDocument.getLength();
        } 
      } 
      return AbstractButton.this.accessibleContext.getAccessibleName().length();
    }
    
    public int getCaretPosition() { return -1; }
    
    public String getAtIndex(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 >= getCharCount())
        return null; 
      switch (param1Int1) {
        case 1:
          try {
            return getText(param1Int2, 1);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 2:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getWordInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            return str.substring(breakIterator.previous(), i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 3:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getSentenceInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            return str.substring(breakIterator.previous(), i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
      } 
      return null;
    }
    
    public String getAfterIndex(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 >= getCharCount())
        return null; 
      switch (param1Int1) {
        case 1:
          if (param1Int2 + 1 >= getCharCount())
            return null; 
          try {
            return getText(param1Int2 + 1, 1);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 2:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getWordInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            if (i == -1 || i >= str.length())
              return null; 
            int j = breakIterator.following(i);
            return (j == -1 || j >= str.length()) ? null : str.substring(i, j);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 3:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getSentenceInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            if (i == -1 || i > str.length())
              return null; 
            int j = breakIterator.following(i);
            return (j == -1 || j > str.length()) ? null : str.substring(i, j);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
      } 
      return null;
    }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 > getCharCount() - 1)
        return null; 
      switch (param1Int1) {
        case 1:
          if (param1Int2 == 0)
            return null; 
          try {
            return getText(param1Int2 - 1, 1);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 2:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getWordInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            i = breakIterator.previous();
            int j = breakIterator.previous();
            return (j == -1) ? null : str.substring(j, i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 3:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getSentenceInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            i = breakIterator.previous();
            int j = breakIterator.previous();
            return (j == -1) ? null : str.substring(j, i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
      } 
      return null;
    }
    
    public AttributeSet getCharacterAttribute(int param1Int) {
      View view = (View)AbstractButton.this.getClientProperty("html");
      if (view != null) {
        Document document = view.getDocument();
        if (document instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)document;
          Element element = styledDocument.getCharacterElement(param1Int);
          if (element != null)
            return element.getAttributes(); 
        } 
      } 
      return null;
    }
    
    public int getSelectionStart() { return -1; }
    
    public int getSelectionEnd() { return -1; }
    
    public String getSelectedText() { return null; }
    
    private String getText(int param1Int1, int param1Int2) {
      View view = (View)AbstractButton.this.getClientProperty("html");
      if (view != null) {
        Document document = view.getDocument();
        if (document instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)document;
          return styledDocument.getText(param1Int1, param1Int2);
        } 
      } 
      return null;
    }
    
    private Rectangle getTextRectangle() {
      String str1 = AbstractButton.this.getText();
      Icon icon = AbstractButton.this.isEnabled() ? AbstractButton.this.getIcon() : AbstractButton.this.getDisabledIcon();
      if (icon == null && str1 == null)
        return null; 
      Rectangle rectangle1 = new Rectangle();
      Rectangle rectangle2 = new Rectangle();
      Rectangle rectangle3 = new Rectangle();
      Insets insets = new Insets(0, 0, 0, 0);
      insets = AbstractButton.this.getInsets(insets);
      rectangle3.x = insets.left;
      rectangle3.y = insets.top;
      rectangle3.width = AbstractButton.this.getWidth() - insets.left + insets.right;
      rectangle3.height = AbstractButton.this.getHeight() - insets.top + insets.bottom;
      String str2 = SwingUtilities.layoutCompoundLabel(AbstractButton.this, getFontMetrics(getFont()), str1, icon, AbstractButton.this.getVerticalAlignment(), AbstractButton.this.getHorizontalAlignment(), AbstractButton.this.getVerticalTextPosition(), AbstractButton.this.getHorizontalTextPosition(), rectangle3, rectangle1, rectangle2, 0);
      return rectangle2;
    }
    
    AccessibleExtendedComponent getAccessibleExtendedComponent() { return this; }
    
    public String getToolTipText() { return AbstractButton.this.getToolTipText(); }
    
    public String getTitledBorderText() { return super.getTitledBorderText(); }
    
    public AccessibleKeyBinding getAccessibleKeyBinding() {
      int i = AbstractButton.this.getMnemonic();
      return (i == 0) ? null : new ButtonKeyBinding(i);
    }
    
    class ButtonKeyBinding implements AccessibleKeyBinding {
      int mnemonic;
      
      ButtonKeyBinding(int param2Int) { this.mnemonic = param2Int; }
      
      public int getAccessibleKeyBindingCount() { return 1; }
      
      public Object getAccessibleKeyBinding(int param2Int) {
        if (param2Int != 0)
          throw new IllegalArgumentException(); 
        return KeyStroke.getKeyStroke(this.mnemonic, 0);
      }
    }
  }
  
  private static class ButtonActionPropertyChangeListener extends ActionPropertyChangeListener<AbstractButton> {
    ButtonActionPropertyChangeListener(AbstractButton param1AbstractButton, Action param1Action) { super(param1AbstractButton, param1Action); }
    
    protected void actionPropertyChanged(AbstractButton param1AbstractButton, Action param1Action, PropertyChangeEvent param1PropertyChangeEvent) {
      if (AbstractAction.shouldReconfigure(param1PropertyChangeEvent)) {
        param1AbstractButton.configurePropertiesFromAction(param1Action);
      } else {
        param1AbstractButton.actionPropertyChanged(param1Action, param1PropertyChangeEvent.getPropertyName());
      } 
    }
  }
  
  protected class ButtonChangeListener implements ChangeListener, Serializable {
    public void stateChanged(ChangeEvent param1ChangeEvent) { AbstractButton.this.getHandler().stateChanged(param1ChangeEvent); }
  }
  
  class Handler implements ActionListener, ChangeListener, ItemListener, Serializable {
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      Object object = param1ChangeEvent.getSource();
      AbstractButton.this.updateMnemonicProperties();
      if (AbstractButton.this.isEnabled() != AbstractButton.this.model.isEnabled())
        AbstractButton.this.setEnabled(AbstractButton.this.model.isEnabled()); 
      AbstractButton.this.fireStateChanged();
      AbstractButton.this.repaint();
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { AbstractButton.this.fireActionPerformed(param1ActionEvent); }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {
      AbstractButton.this.fireItemStateChanged(param1ItemEvent);
      if (AbstractButton.this.shouldUpdateSelectedStateFromAction()) {
        Action action = AbstractButton.this.getAction();
        if (action != null && AbstractAction.hasSelectedKey(action)) {
          boolean bool1 = AbstractButton.this.isSelected();
          boolean bool2 = AbstractAction.isSelected(action);
          if (bool2 != bool1)
            action.putValue("SwingSelectedKey", Boolean.valueOf(bool1)); 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\AbstractButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */