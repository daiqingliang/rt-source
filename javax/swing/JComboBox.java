package javax.swing;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.IllegalComponentStateException;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class JComboBox<E> extends JComponent implements ItemSelectable, ListDataListener, ActionListener, Accessible {
  private static final String uiClassID = "ComboBoxUI";
  
  protected ComboBoxModel<E> dataModel;
  
  protected ListCellRenderer<? super E> renderer;
  
  protected ComboBoxEditor editor;
  
  protected int maximumRowCount = 8;
  
  protected boolean isEditable = false;
  
  protected KeySelectionManager keySelectionManager = null;
  
  protected String actionCommand = "comboBoxChanged";
  
  protected boolean lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
  
  protected Object selectedItemReminder = null;
  
  private E prototypeDisplayValue;
  
  private boolean firingActionEvent = false;
  
  private boolean selectingItem = false;
  
  private Action action;
  
  private PropertyChangeListener actionPropertyChangeListener;
  
  public JComboBox(ComboBoxModel<E> paramComboBoxModel) {
    setModel(paramComboBoxModel);
    init();
  }
  
  public JComboBox(E[] paramArrayOfE) {
    setModel(new DefaultComboBoxModel(paramArrayOfE));
    init();
  }
  
  public JComboBox(Vector<E> paramVector) {
    setModel(new DefaultComboBoxModel(paramVector));
    init();
  }
  
  public JComboBox() {
    setModel(new DefaultComboBoxModel());
    init();
  }
  
  private void init() {
    installAncestorListener();
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
  }
  
  protected void installAncestorListener() { addAncestorListener(new AncestorListener() {
          public void ancestorAdded(AncestorEvent param1AncestorEvent) { JComboBox.this.hidePopup(); }
          
          public void ancestorRemoved(AncestorEvent param1AncestorEvent) { JComboBox.this.hidePopup(); }
          
          public void ancestorMoved(AncestorEvent param1AncestorEvent) {
            if (param1AncestorEvent.getSource() != JComboBox.this)
              JComboBox.this.hidePopup(); 
          }
        }); }
  
  public void setUI(ComboBoxUI paramComboBoxUI) { setUI(paramComboBoxUI); }
  
  public void updateUI() {
    setUI((ComboBoxUI)UIManager.getUI(this));
    ListCellRenderer listCellRenderer = getRenderer();
    if (listCellRenderer instanceof Component)
      SwingUtilities.updateComponentTreeUI((Component)listCellRenderer); 
  }
  
  public String getUIClassID() { return "ComboBoxUI"; }
  
  public ComboBoxUI getUI() { return (ComboBoxUI)this.ui; }
  
  public void setModel(ComboBoxModel<E> paramComboBoxModel) {
    ComboBoxModel comboBoxModel = this.dataModel;
    if (comboBoxModel != null)
      comboBoxModel.removeListDataListener(this); 
    this.dataModel = paramComboBoxModel;
    this.dataModel.addListDataListener(this);
    this.selectedItemReminder = this.dataModel.getSelectedItem();
    firePropertyChange("model", comboBoxModel, this.dataModel);
  }
  
  public ComboBoxModel<E> getModel() { return this.dataModel; }
  
  public void setLightWeightPopupEnabled(boolean paramBoolean) {
    boolean bool = this.lightWeightPopupEnabled;
    this.lightWeightPopupEnabled = paramBoolean;
    firePropertyChange("lightWeightPopupEnabled", bool, this.lightWeightPopupEnabled);
  }
  
  public boolean isLightWeightPopupEnabled() { return this.lightWeightPopupEnabled; }
  
  public void setEditable(boolean paramBoolean) {
    boolean bool = this.isEditable;
    this.isEditable = paramBoolean;
    firePropertyChange("editable", bool, this.isEditable);
  }
  
  public boolean isEditable() { return this.isEditable; }
  
  public void setMaximumRowCount(int paramInt) {
    int i = this.maximumRowCount;
    this.maximumRowCount = paramInt;
    firePropertyChange("maximumRowCount", i, this.maximumRowCount);
  }
  
  public int getMaximumRowCount() { return this.maximumRowCount; }
  
  public void setRenderer(ListCellRenderer<? super E> paramListCellRenderer) {
    ListCellRenderer listCellRenderer = this.renderer;
    this.renderer = paramListCellRenderer;
    firePropertyChange("renderer", listCellRenderer, this.renderer);
    invalidate();
  }
  
  public ListCellRenderer<? super E> getRenderer() { return this.renderer; }
  
  public void setEditor(ComboBoxEditor paramComboBoxEditor) {
    ComboBoxEditor comboBoxEditor = this.editor;
    if (this.editor != null)
      this.editor.removeActionListener(this); 
    this.editor = paramComboBoxEditor;
    if (this.editor != null)
      this.editor.addActionListener(this); 
    firePropertyChange("editor", comboBoxEditor, this.editor);
  }
  
  public ComboBoxEditor getEditor() { return this.editor; }
  
  public void setSelectedItem(Object paramObject) {
    Object object1 = this.selectedItemReminder;
    Object object2 = paramObject;
    if (object1 == null || !object1.equals(paramObject)) {
      if (paramObject != null && !isEditable()) {
        boolean bool = false;
        for (byte b = 0; b < this.dataModel.getSize(); b++) {
          Object object = this.dataModel.getElementAt(b);
          if (paramObject.equals(object)) {
            bool = true;
            object2 = object;
            break;
          } 
        } 
        if (!bool)
          return; 
      } 
      this.selectingItem = true;
      this.dataModel.setSelectedItem(object2);
      this.selectingItem = false;
      if (this.selectedItemReminder != this.dataModel.getSelectedItem())
        selectedItemChanged(); 
    } 
    fireActionEvent();
  }
  
  public Object getSelectedItem() { return this.dataModel.getSelectedItem(); }
  
  public void setSelectedIndex(int paramInt) {
    int i = this.dataModel.getSize();
    if (paramInt == -1) {
      setSelectedItem(null);
    } else {
      if (paramInt < -1 || paramInt >= i)
        throw new IllegalArgumentException("setSelectedIndex: " + paramInt + " out of bounds"); 
      setSelectedItem(this.dataModel.getElementAt(paramInt));
    } 
  }
  
  @Transient
  public int getSelectedIndex() {
    Object object = this.dataModel.getSelectedItem();
    byte b = 0;
    int i = this.dataModel.getSize();
    while (b < i) {
      Object object1 = this.dataModel.getElementAt(b);
      if (object1 != null && object1.equals(object))
        return b; 
      b++;
    } 
    return -1;
  }
  
  public E getPrototypeDisplayValue() { return (E)this.prototypeDisplayValue; }
  
  public void setPrototypeDisplayValue(E paramE) {
    Object object = this.prototypeDisplayValue;
    this.prototypeDisplayValue = paramE;
    firePropertyChange("prototypeDisplayValue", object, paramE);
  }
  
  public void addItem(E paramE) {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)this.dataModel).addElement(paramE);
  }
  
  public void insertItemAt(E paramE, int paramInt) {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)this.dataModel).insertElementAt(paramE, paramInt);
  }
  
  public void removeItem(Object paramObject) {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)this.dataModel).removeElement(paramObject);
  }
  
  public void removeItemAt(int paramInt) {
    checkMutableComboBoxModel();
    ((MutableComboBoxModel)this.dataModel).removeElementAt(paramInt);
  }
  
  public void removeAllItems() {
    checkMutableComboBoxModel();
    MutableComboBoxModel mutableComboBoxModel = (MutableComboBoxModel)this.dataModel;
    int i = mutableComboBoxModel.getSize();
    if (mutableComboBoxModel instanceof DefaultComboBoxModel) {
      ((DefaultComboBoxModel)mutableComboBoxModel).removeAllElements();
    } else {
      for (byte b = 0; b < i; b++) {
        Object object = mutableComboBoxModel.getElementAt(0);
        mutableComboBoxModel.removeElement(object);
      } 
    } 
    this.selectedItemReminder = null;
    if (isEditable())
      this.editor.setItem(null); 
  }
  
  void checkMutableComboBoxModel() {
    if (!(this.dataModel instanceof MutableComboBoxModel))
      throw new RuntimeException("Cannot use this method with a non-Mutable data model."); 
  }
  
  public void showPopup() { setPopupVisible(true); }
  
  public void hidePopup() { setPopupVisible(false); }
  
  public void setPopupVisible(boolean paramBoolean) { getUI().setPopupVisible(this, paramBoolean); }
  
  public boolean isPopupVisible() { return getUI().isPopupVisible(this); }
  
  public void addItemListener(ItemListener paramItemListener) { this.listenerList.add(ItemListener.class, paramItemListener); }
  
  public void removeItemListener(ItemListener paramItemListener) { this.listenerList.remove(ItemListener.class, paramItemListener); }
  
  public ItemListener[] getItemListeners() { return (ItemListener[])this.listenerList.getListeners(ItemListener.class); }
  
  public void addActionListener(ActionListener paramActionListener) { this.listenerList.add(ActionListener.class, paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener != null && getAction() == paramActionListener) {
      setAction(null);
    } else {
      this.listenerList.remove(ActionListener.class, paramActionListener);
    } 
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])this.listenerList.getListeners(ActionListener.class); }
  
  public void addPopupMenuListener(PopupMenuListener paramPopupMenuListener) { this.listenerList.add(PopupMenuListener.class, paramPopupMenuListener); }
  
  public void removePopupMenuListener(PopupMenuListener paramPopupMenuListener) { this.listenerList.remove(PopupMenuListener.class, paramPopupMenuListener); }
  
  public PopupMenuListener[] getPopupMenuListeners() { return (PopupMenuListener[])this.listenerList.getListeners(PopupMenuListener.class); }
  
  public void firePopupMenuWillBecomeVisible() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    PopupMenuEvent popupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class) {
        if (popupMenuEvent == null)
          popupMenuEvent = new PopupMenuEvent(this); 
        ((PopupMenuListener)arrayOfObject[i + 1]).popupMenuWillBecomeVisible(popupMenuEvent);
      } 
    } 
  }
  
  public void firePopupMenuWillBecomeInvisible() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    PopupMenuEvent popupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class) {
        if (popupMenuEvent == null)
          popupMenuEvent = new PopupMenuEvent(this); 
        ((PopupMenuListener)arrayOfObject[i + 1]).popupMenuWillBecomeInvisible(popupMenuEvent);
      } 
    } 
  }
  
  public void firePopupMenuCanceled() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    PopupMenuEvent popupMenuEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == PopupMenuListener.class) {
        if (popupMenuEvent == null)
          popupMenuEvent = new PopupMenuEvent(this); 
        ((PopupMenuListener)arrayOfObject[i + 1]).popupMenuCanceled(popupMenuEvent);
      } 
    } 
  }
  
  public void setActionCommand(String paramString) { this.actionCommand = paramString; }
  
  public String getActionCommand() { return this.actionCommand; }
  
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
    AbstractAction.setEnabledFromAction(this, paramAction);
    AbstractAction.setToolTipTextFromAction(this, paramAction);
    setActionCommandFromAction(paramAction);
  }
  
  protected PropertyChangeListener createActionPropertyChangeListener(Action paramAction) { return new ComboBoxActionPropertyChangeListener(this, paramAction); }
  
  protected void actionPropertyChanged(Action paramAction, String paramString) {
    if (paramString == "ActionCommandKey") {
      setActionCommandFromAction(paramAction);
    } else if (paramString == "enabled") {
      AbstractAction.setEnabledFromAction(this, paramAction);
    } else if ("ShortDescription" == paramString) {
      AbstractAction.setToolTipTextFromAction(this, paramAction);
    } 
  }
  
  private void setActionCommandFromAction(Action paramAction) { setActionCommand((paramAction != null) ? (String)paramAction.getValue("ActionCommandKey") : null); }
  
  protected void fireItemStateChanged(ItemEvent paramItemEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ItemListener.class)
        ((ItemListener)arrayOfObject[i + 1]).itemStateChanged(paramItemEvent); 
    } 
  }
  
  protected void fireActionEvent() {
    if (!this.firingActionEvent) {
      this.firingActionEvent = true;
      ActionEvent actionEvent = null;
      Object[] arrayOfObject = this.listenerList.getListenerList();
      long l = EventQueue.getMostRecentEventTime();
      int i = 0;
      AWTEvent aWTEvent = EventQueue.getCurrentEvent();
      if (aWTEvent instanceof InputEvent) {
        i = ((InputEvent)aWTEvent).getModifiers();
      } else if (aWTEvent instanceof ActionEvent) {
        i = ((ActionEvent)aWTEvent).getModifiers();
      } 
      for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
        if (arrayOfObject[j] == ActionListener.class) {
          if (actionEvent == null)
            actionEvent = new ActionEvent(this, 1001, getActionCommand(), l, i); 
          ((ActionListener)arrayOfObject[j + 1]).actionPerformed(actionEvent);
        } 
      } 
      this.firingActionEvent = false;
    } 
  }
  
  protected void selectedItemChanged() {
    if (this.selectedItemReminder != null)
      fireItemStateChanged(new ItemEvent(this, 701, this.selectedItemReminder, 2)); 
    this.selectedItemReminder = this.dataModel.getSelectedItem();
    if (this.selectedItemReminder != null)
      fireItemStateChanged(new ItemEvent(this, 701, this.selectedItemReminder, 1)); 
  }
  
  public Object[] getSelectedObjects() {
    Object object = getSelectedItem();
    if (object == null)
      return new Object[0]; 
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = object;
    return arrayOfObject;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    ComboBoxEditor comboBoxEditor = getEditor();
    if (comboBoxEditor != null && paramActionEvent != null && (comboBoxEditor == paramActionEvent.getSource() || comboBoxEditor.getEditorComponent() == paramActionEvent.getSource())) {
      setPopupVisible(false);
      getModel().setSelectedItem(comboBoxEditor.getItem());
      String str = getActionCommand();
      setActionCommand("comboBoxEdited");
      fireActionEvent();
      setActionCommand(str);
    } 
  }
  
  public void contentsChanged(ListDataEvent paramListDataEvent) {
    Object object1 = this.selectedItemReminder;
    Object object2 = this.dataModel.getSelectedItem();
    if (object1 == null || !object1.equals(object2)) {
      selectedItemChanged();
      if (!this.selectingItem)
        fireActionEvent(); 
    } 
  }
  
  public void intervalAdded(ListDataEvent paramListDataEvent) {
    if (this.selectedItemReminder != this.dataModel.getSelectedItem())
      selectedItemChanged(); 
  }
  
  public void intervalRemoved(ListDataEvent paramListDataEvent) { contentsChanged(paramListDataEvent); }
  
  public boolean selectWithKeyChar(char paramChar) {
    if (this.keySelectionManager == null)
      this.keySelectionManager = createDefaultKeySelectionManager(); 
    int i = this.keySelectionManager.selectionForKey(paramChar, getModel());
    if (i != -1) {
      setSelectedIndex(i);
      return true;
    } 
    return false;
  }
  
  public void setEnabled(boolean paramBoolean) {
    super.setEnabled(paramBoolean);
    firePropertyChange("enabled", !isEnabled(), isEnabled());
  }
  
  public void configureEditor(ComboBoxEditor paramComboBoxEditor, Object paramObject) { paramComboBoxEditor.setItem(paramObject); }
  
  public void processKeyEvent(KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getKeyCode() == 9)
      hidePopup(); 
    super.processKeyEvent(paramKeyEvent);
  }
  
  protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean) {
    if (super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean))
      return true; 
    if (!isEditable() || paramInt != 0 || getEditor() == null || !Boolean.TRUE.equals(getClientProperty("JComboBox.isTableCellEditor")))
      return false; 
    Component component = getEditor().getEditorComponent();
    if (component instanceof JComponent) {
      JComponent jComponent = (JComponent)component;
      return jComponent.processKeyBinding(paramKeyStroke, paramKeyEvent, 0, paramBoolean);
    } 
    return false;
  }
  
  public void setKeySelectionManager(KeySelectionManager paramKeySelectionManager) { this.keySelectionManager = paramKeySelectionManager; }
  
  public KeySelectionManager getKeySelectionManager() { return this.keySelectionManager; }
  
  public int getItemCount() { return this.dataModel.getSize(); }
  
  public E getItemAt(int paramInt) { return (E)this.dataModel.getElementAt(paramInt); }
  
  protected KeySelectionManager createDefaultKeySelectionManager() { return new DefaultKeySelectionManager(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ComboBoxUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1 = (this.selectedItemReminder != null) ? this.selectedItemReminder.toString() : "";
    String str2 = this.isEditable ? "true" : "false";
    String str3 = this.lightWeightPopupEnabled ? "true" : "false";
    return super.paramString() + ",isEditable=" + str2 + ",lightWeightPopupEnabled=" + str3 + ",maximumRowCount=" + this.maximumRowCount + ",selectedItemReminder=" + str1;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJComboBox(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJComboBox extends JComponent.AccessibleJComponent implements AccessibleAction, AccessibleSelection {
    private JList popupList;
    
    private Accessible previousSelectedAccessible = null;
    
    private JComboBox<E>.AccessibleJComboBox.EditorAccessibleContext editorAccessibleContext = null;
    
    public AccessibleJComboBox() {
      super(JComboBox.this);
      this$0.addPropertyChangeListener(new AccessibleJComboBoxPropertyChangeListener(null));
      setEditorNameAndDescription();
      Accessible accessible = this$0.getUI().getAccessibleChild(this$0, 0);
      if (accessible instanceof ComboPopup) {
        this.popupList = ((ComboPopup)accessible).getList();
        this.popupList.addListSelectionListener(new AccessibleJComboBoxListSelectionListener(null));
      } 
      this$0.addPopupMenuListener(new AccessibleJComboBoxPopupMenuListener(null));
    }
    
    private void setEditorNameAndDescription() {
      ComboBoxEditor comboBoxEditor = JComboBox.this.getEditor();
      if (comboBoxEditor != null) {
        Component component = comboBoxEditor.getEditorComponent();
        if (component instanceof Accessible) {
          AccessibleContext accessibleContext = component.getAccessibleContext();
          if (accessibleContext != null) {
            accessibleContext.setAccessibleName(getAccessibleName());
            accessibleContext.setAccessibleDescription(getAccessibleDescription());
          } 
        } 
      } 
    }
    
    public int getAccessibleChildrenCount() { return (JComboBox.this.ui != null) ? JComboBox.this.ui.getAccessibleChildrenCount(JComboBox.this) : super.getAccessibleChildrenCount(); }
    
    public Accessible getAccessibleChild(int param1Int) { return (JComboBox.this.ui != null) ? JComboBox.this.ui.getAccessibleChild(JComboBox.this, param1Int) : super.getAccessibleChild(param1Int); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.COMBO_BOX; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (accessibleStateSet == null)
        accessibleStateSet = new AccessibleStateSet(); 
      if (JComboBox.this.isPopupVisible()) {
        accessibleStateSet.add(AccessibleState.EXPANDED);
      } else {
        accessibleStateSet.add(AccessibleState.COLLAPSED);
      } 
      return accessibleStateSet;
    }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public String getAccessibleActionDescription(int param1Int) { return (param1Int == 0) ? UIManager.getString("ComboBox.togglePopupText") : null; }
    
    public int getAccessibleActionCount() { return 1; }
    
    public boolean doAccessibleAction(int param1Int) {
      if (param1Int == 0) {
        JComboBox.this.setPopupVisible(!JComboBox.this.isPopupVisible());
        return true;
      } 
      return false;
    }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public int getAccessibleSelectionCount() {
      Object object = JComboBox.this.getSelectedItem();
      return (object != null) ? 1 : 0;
    }
    
    public Accessible getAccessibleSelection(int param1Int) {
      Accessible accessible = JComboBox.this.getUI().getAccessibleChild(JComboBox.this, 0);
      if (accessible != null && accessible instanceof ComboPopup) {
        JList jList = ((ComboPopup)accessible).getList();
        AccessibleContext accessibleContext = jList.getAccessibleContext();
        if (accessibleContext != null) {
          AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
          if (accessibleSelection != null)
            return accessibleSelection.getAccessibleSelection(param1Int); 
        } 
      } 
      return null;
    }
    
    public boolean isAccessibleChildSelected(int param1Int) { return (JComboBox.this.getSelectedIndex() == param1Int); }
    
    public void addAccessibleSelection(int param1Int) {
      clearAccessibleSelection();
      JComboBox.this.setSelectedIndex(param1Int);
    }
    
    public void removeAccessibleSelection(int param1Int) {
      if (JComboBox.this.getSelectedIndex() == param1Int)
        clearAccessibleSelection(); 
    }
    
    public void clearAccessibleSelection() { JComboBox.this.setSelectedIndex(-1); }
    
    public void selectAllAccessibleSelection() {}
    
    private class AccessibleEditor implements Accessible {
      public AccessibleContext getAccessibleContext() {
        if (JComboBox.AccessibleJComboBox.this.editorAccessibleContext == null) {
          Component component = JComboBox.AccessibleJComboBox.this.this$0.getEditor().getEditorComponent();
          if (component instanceof Accessible)
            JComboBox.AccessibleJComboBox.this.editorAccessibleContext = new JComboBox.AccessibleJComboBox.EditorAccessibleContext(JComboBox.AccessibleJComboBox.this, (Accessible)component); 
        } 
        return JComboBox.AccessibleJComboBox.this.editorAccessibleContext;
      }
    }
    
    private class AccessibleJComboBoxListSelectionListener implements ListSelectionListener {
      private AccessibleJComboBoxListSelectionListener() {}
      
      public void valueChanged(ListSelectionEvent param2ListSelectionEvent) {
        if (JComboBox.AccessibleJComboBox.this.popupList == null)
          return; 
        int i = JComboBox.AccessibleJComboBox.this.popupList.getSelectedIndex();
        if (i < 0)
          return; 
        Accessible accessible = JComboBox.AccessibleJComboBox.this.popupList.getAccessibleContext().getAccessibleChild(i);
        if (accessible == null)
          return; 
        if (JComboBox.AccessibleJComboBox.this.previousSelectedAccessible != null) {
          PropertyChangeEvent propertyChangeEvent1 = new PropertyChangeEvent(JComboBox.AccessibleJComboBox.this.previousSelectedAccessible, "AccessibleState", AccessibleState.FOCUSED, null);
          JComboBox.AccessibleJComboBox.this.firePropertyChange("AccessibleState", null, propertyChangeEvent1);
        } 
        PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(accessible, "AccessibleState", null, AccessibleState.FOCUSED);
        JComboBox.AccessibleJComboBox.this.firePropertyChange("AccessibleState", null, propertyChangeEvent);
        JComboBox.AccessibleJComboBox.this.firePropertyChange("AccessibleActiveDescendant", JComboBox.AccessibleJComboBox.this.previousSelectedAccessible, accessible);
        JComboBox.AccessibleJComboBox.this.previousSelectedAccessible = accessible;
      }
    }
    
    private class AccessibleJComboBoxPopupMenuListener implements PopupMenuListener {
      private AccessibleJComboBoxPopupMenuListener() {}
      
      public void popupMenuWillBecomeVisible(PopupMenuEvent param2PopupMenuEvent) {
        if (JComboBox.AccessibleJComboBox.this.popupList == null)
          return; 
        int i = JComboBox.AccessibleJComboBox.this.popupList.getSelectedIndex();
        if (i < 0)
          return; 
        JComboBox.AccessibleJComboBox.this.previousSelectedAccessible = JComboBox.AccessibleJComboBox.this.popupList.getAccessibleContext().getAccessibleChild(i);
      }
      
      public void popupMenuWillBecomeInvisible(PopupMenuEvent param2PopupMenuEvent) {}
      
      public void popupMenuCanceled(PopupMenuEvent param2PopupMenuEvent) {}
    }
    
    private class AccessibleJComboBoxPropertyChangeListener implements PropertyChangeListener {
      private AccessibleJComboBoxPropertyChangeListener() {}
      
      public void propertyChange(PropertyChangeEvent param2PropertyChangeEvent) {
        if (param2PropertyChangeEvent.getPropertyName() == "editor")
          JComboBox.AccessibleJComboBox.this.setEditorNameAndDescription(); 
      }
    }
    
    private class EditorAccessibleContext extends AccessibleContext {
      private AccessibleContext ac;
      
      private EditorAccessibleContext() {}
      
      EditorAccessibleContext(Accessible param2Accessible) { this.ac = param2Accessible.getAccessibleContext(); }
      
      public String getAccessibleName() { return this.ac.getAccessibleName(); }
      
      public void setAccessibleName(String param2String) { this.ac.setAccessibleName(param2String); }
      
      public String getAccessibleDescription() { return this.ac.getAccessibleDescription(); }
      
      public void setAccessibleDescription(String param2String) { this.ac.setAccessibleDescription(param2String); }
      
      public AccessibleRole getAccessibleRole() { return this.ac.getAccessibleRole(); }
      
      public AccessibleStateSet getAccessibleStateSet() { return this.ac.getAccessibleStateSet(); }
      
      public Accessible getAccessibleParent() { return this.ac.getAccessibleParent(); }
      
      public void setAccessibleParent(Accessible param2Accessible) { this.ac.setAccessibleParent(param2Accessible); }
      
      public int getAccessibleIndexInParent() { return JComboBox.AccessibleJComboBox.this.this$0.getSelectedIndex(); }
      
      public int getAccessibleChildrenCount() { return this.ac.getAccessibleChildrenCount(); }
      
      public Accessible getAccessibleChild(int param2Int) { return this.ac.getAccessibleChild(param2Int); }
      
      public Locale getLocale() throws IllegalComponentStateException { return this.ac.getLocale(); }
      
      public void addPropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) { this.ac.addPropertyChangeListener(param2PropertyChangeListener); }
      
      public void removePropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) { this.ac.removePropertyChangeListener(param2PropertyChangeListener); }
      
      public AccessibleAction getAccessibleAction() { return this.ac.getAccessibleAction(); }
      
      public AccessibleComponent getAccessibleComponent() { return this.ac.getAccessibleComponent(); }
      
      public AccessibleSelection getAccessibleSelection() { return this.ac.getAccessibleSelection(); }
      
      public AccessibleText getAccessibleText() { return this.ac.getAccessibleText(); }
      
      public AccessibleEditableText getAccessibleEditableText() { return this.ac.getAccessibleEditableText(); }
      
      public AccessibleValue getAccessibleValue() { return this.ac.getAccessibleValue(); }
      
      public AccessibleIcon[] getAccessibleIcon() { return this.ac.getAccessibleIcon(); }
      
      public AccessibleRelationSet getAccessibleRelationSet() { return this.ac.getAccessibleRelationSet(); }
      
      public AccessibleTable getAccessibleTable() { return this.ac.getAccessibleTable(); }
      
      public void firePropertyChange(String param2String, Object param2Object1, Object param2Object2) { this.ac.firePropertyChange(param2String, param2Object1, param2Object2); }
    }
  }
  
  private static class ComboBoxActionPropertyChangeListener extends ActionPropertyChangeListener<JComboBox<?>> {
    ComboBoxActionPropertyChangeListener(JComboBox<?> param1JComboBox, Action param1Action) { super(param1JComboBox, param1Action); }
    
    protected void actionPropertyChanged(JComboBox<?> param1JComboBox, Action param1Action, PropertyChangeEvent param1PropertyChangeEvent) {
      if (AbstractAction.shouldReconfigure(param1PropertyChangeEvent)) {
        param1JComboBox.configurePropertiesFromAction(param1Action);
      } else {
        param1JComboBox.actionPropertyChanged(param1Action, param1PropertyChangeEvent.getPropertyName());
      } 
    }
  }
  
  class DefaultKeySelectionManager implements KeySelectionManager, Serializable {
    public int selectionForKey(char param1Char, ComboBoxModel param1ComboBoxModel) {
      byte b2 = -1;
      Object object = param1ComboBoxModel.getSelectedItem();
      if (object != null) {
        byte b = 0;
        int j = param1ComboBoxModel.getSize();
        while (b < j) {
          if (object == param1ComboBoxModel.getElementAt(b)) {
            b2 = b;
            break;
          } 
          b++;
        } 
      } 
      String str = ("" + param1Char).toLowerCase();
      param1Char = str.charAt(0);
      byte b1 = ++b2;
      int i = param1ComboBoxModel.getSize();
      while (b1 < i) {
        Object object1 = param1ComboBoxModel.getElementAt(b1);
        if (object1 != null && object1.toString() != null) {
          String str1 = object1.toString().toLowerCase();
          if (str1.length() > 0 && str1.charAt(0) == param1Char)
            return b1; 
        } 
        b1++;
      } 
      for (b1 = 0; b1 < b2; b1++) {
        Object object1 = param1ComboBoxModel.getElementAt(b1);
        if (object1 != null && object1.toString() != null) {
          String str1 = object1.toString().toLowerCase();
          if (str1.length() > 0 && str1.charAt(0) == param1Char)
            return b1; 
        } 
      } 
      return -1;
    }
  }
  
  public static interface KeySelectionManager {
    int selectionForKey(char param1Char, ComboBoxModel param1ComboBoxModel);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JComboBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */