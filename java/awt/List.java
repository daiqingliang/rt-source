package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.ListPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class List extends Component implements ItemSelectable, Accessible {
  Vector<String> items = new Vector();
  
  int rows = 0;
  
  boolean multipleMode = false;
  
  int[] selected = new int[0];
  
  int visibleIndex = -1;
  
  ActionListener actionListener;
  
  ItemListener itemListener;
  
  private static final String base = "list";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = -3304312411574666869L;
  
  static final int DEFAULT_VISIBLE_ROWS = 4;
  
  private int listSerializedDataVersion = 1;
  
  public List() throws HeadlessException { this(0, false); }
  
  public List(int paramInt) throws HeadlessException { this(paramInt, false); }
  
  public List(int paramInt, boolean paramBoolean) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.rows = (paramInt != 0) ? paramInt : 4;
    this.multipleMode = paramBoolean;
  }
  
  String constructComponentName() {
    synchronized (List.class) {
      return "list" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createList(this); 
      super.addNotify();
    } 
  }
  
  public void removeNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      ListPeer listPeer = (ListPeer)this.peer;
      if (listPeer != null)
        this.selected = listPeer.getSelectedIndexes(); 
      super.removeNotify();
    } 
  }
  
  public int getItemCount() { return countItems(); }
  
  @Deprecated
  public int countItems() { return this.items.size(); }
  
  public String getItem(int paramInt) { return getItemImpl(paramInt); }
  
  final String getItemImpl(int paramInt) { return (String)this.items.elementAt(paramInt); }
  
  public String[] getItems() {
    String[] arrayOfString = new String[this.items.size()];
    this.items.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public void add(String paramString) { addItem(paramString); }
  
  @Deprecated
  public void addItem(String paramString) { addItem(paramString, -1); }
  
  public void add(String paramString, int paramInt) { addItem(paramString, paramInt); }
  
  @Deprecated
  public void addItem(String paramString, int paramInt) {
    if (paramInt < -1 || paramInt >= this.items.size())
      paramInt = -1; 
    if (paramString == null)
      paramString = ""; 
    if (paramInt == -1) {
      this.items.addElement(paramString);
    } else {
      this.items.insertElementAt(paramString, paramInt);
    } 
    ListPeer listPeer = (ListPeer)this.peer;
    if (listPeer != null)
      listPeer.add(paramString, paramInt); 
  }
  
  public void replaceItem(String paramString, int paramInt) {
    remove(paramInt);
    add(paramString, paramInt);
  }
  
  public void removeAll() throws HeadlessException { clear(); }
  
  @Deprecated
  public void clear() throws HeadlessException {
    ListPeer listPeer = (ListPeer)this.peer;
    if (listPeer != null)
      listPeer.removeAll(); 
    this.items = new Vector();
    this.selected = new int[0];
  }
  
  public void remove(String paramString) {
    int i = this.items.indexOf(paramString);
    if (i < 0)
      throw new IllegalArgumentException("item " + paramString + " not found in list"); 
    remove(i);
  }
  
  public void remove(int paramInt) throws HeadlessException { delItem(paramInt); }
  
  @Deprecated
  public void delItem(int paramInt) throws HeadlessException { delItems(paramInt, paramInt); }
  
  public int getSelectedIndex() {
    int[] arrayOfInt = getSelectedIndexes();
    return (arrayOfInt.length == 1) ? arrayOfInt[0] : -1;
  }
  
  public int[] getSelectedIndexes() {
    ListPeer listPeer = (ListPeer)this.peer;
    if (listPeer != null)
      this.selected = listPeer.getSelectedIndexes(); 
    return (int[])this.selected.clone();
  }
  
  public String getSelectedItem() {
    int i = getSelectedIndex();
    return (i < 0) ? null : getItem(i);
  }
  
  public String[] getSelectedItems() {
    int[] arrayOfInt = getSelectedIndexes();
    String[] arrayOfString = new String[arrayOfInt.length];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfString[b] = getItem(arrayOfInt[b]); 
    return arrayOfString;
  }
  
  public Object[] getSelectedObjects() { return getSelectedItems(); }
  
  public void select(int paramInt) throws HeadlessException {
    ListPeer listPeer;
    do {
      listPeer = (ListPeer)this.peer;
      if (listPeer != null) {
        listPeer.select(paramInt);
        return;
      } 
      synchronized (this) {
        boolean bool = false;
        for (byte b = 0; b < this.selected.length; b++) {
          if (this.selected[b] == paramInt) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          if (!this.multipleMode) {
            this.selected = new int[1];
            this.selected[0] = paramInt;
          } else {
            int[] arrayOfInt = new int[this.selected.length + 1];
            System.arraycopy(this.selected, 0, arrayOfInt, 0, this.selected.length);
            arrayOfInt[this.selected.length] = paramInt;
            this.selected = arrayOfInt;
          }  
      } 
    } while (listPeer != this.peer);
  }
  
  public void deselect(int paramInt) throws HeadlessException {
    ListPeer listPeer = (ListPeer)this.peer;
    if (listPeer != null && (isMultipleMode() || getSelectedIndex() == paramInt))
      listPeer.deselect(paramInt); 
    for (byte b = 0; b < this.selected.length; b++) {
      if (this.selected[b] == paramInt) {
        int[] arrayOfInt = new int[this.selected.length - 1];
        System.arraycopy(this.selected, 0, arrayOfInt, 0, b);
        System.arraycopy(this.selected, b + 1, arrayOfInt, b, this.selected.length - b + 1);
        this.selected = arrayOfInt;
        return;
      } 
    } 
  }
  
  public boolean isIndexSelected(int paramInt) { return isSelected(paramInt); }
  
  @Deprecated
  public boolean isSelected(int paramInt) {
    int[] arrayOfInt = getSelectedIndexes();
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (arrayOfInt[b] == paramInt)
        return true; 
    } 
    return false;
  }
  
  public int getRows() { return this.rows; }
  
  public boolean isMultipleMode() { return allowsMultipleSelections(); }
  
  @Deprecated
  public boolean allowsMultipleSelections() { return this.multipleMode; }
  
  public void setMultipleMode(boolean paramBoolean) { setMultipleSelections(paramBoolean); }
  
  @Deprecated
  public void setMultipleSelections(boolean paramBoolean) {
    if (paramBoolean != this.multipleMode) {
      this.multipleMode = paramBoolean;
      ListPeer listPeer = (ListPeer)this.peer;
      if (listPeer != null)
        listPeer.setMultipleMode(paramBoolean); 
    } 
  }
  
  public int getVisibleIndex() { return this.visibleIndex; }
  
  public void makeVisible(int paramInt) throws HeadlessException {
    this.visibleIndex = paramInt;
    ListPeer listPeer = (ListPeer)this.peer;
    if (listPeer != null)
      listPeer.makeVisible(paramInt); 
  }
  
  public Dimension getPreferredSize(int paramInt) { return preferredSize(paramInt); }
  
  @Deprecated
  public Dimension preferredSize(int paramInt) {
    synchronized (getTreeLock()) {
      ListPeer listPeer = (ListPeer)this.peer;
      return (listPeer != null) ? listPeer.getPreferredSize(paramInt) : super.preferredSize();
    } 
  }
  
  public Dimension getPreferredSize() { return preferredSize(); }
  
  @Deprecated
  public Dimension preferredSize() {
    synchronized (getTreeLock()) {
      return (this.rows > 0) ? preferredSize(this.rows) : super.preferredSize();
    } 
  }
  
  public Dimension getMinimumSize(int paramInt) { return minimumSize(paramInt); }
  
  @Deprecated
  public Dimension minimumSize(int paramInt) {
    synchronized (getTreeLock()) {
      ListPeer listPeer = (ListPeer)this.peer;
      return (listPeer != null) ? listPeer.getMinimumSize(paramInt) : super.minimumSize();
    } 
  }
  
  public Dimension getMinimumSize() { return minimumSize(); }
  
  @Deprecated
  public Dimension minimumSize() {
    synchronized (getTreeLock()) {
      return (this.rows > 0) ? minimumSize(this.rows) : super.minimumSize();
    } 
  }
  
  public void addItemListener(ItemListener paramItemListener) {
    if (paramItemListener == null)
      return; 
    this.itemListener = AWTEventMulticaster.add(this.itemListener, paramItemListener);
    this.newEventsOnly = true;
  }
  
  public void removeItemListener(ItemListener paramItemListener) {
    if (paramItemListener == null)
      return; 
    this.itemListener = AWTEventMulticaster.remove(this.itemListener, paramItemListener);
  }
  
  public ItemListener[] getItemListeners() { return (ItemListener[])getListeners(ItemListener.class); }
  
  public void addActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.add(this.actionListener, paramActionListener);
    this.newEventsOnly = true;
  }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, paramActionListener);
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])getListeners(ActionListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    ItemListener itemListener1 = null;
    if (paramClass == ActionListener.class) {
      itemListener1 = this.actionListener;
    } else if (paramClass == ItemListener.class) {
      itemListener1 = this.itemListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(itemListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) {
    switch (paramAWTEvent.id) {
      case 1001:
        return ((this.eventMask & 0x80L) != 0L || this.actionListener != null);
      case 701:
        return ((this.eventMask & 0x200L) != 0L || this.itemListener != null);
    } 
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ItemEvent) {
      processItemEvent((ItemEvent)paramAWTEvent);
      return;
    } 
    if (paramAWTEvent instanceof ActionEvent) {
      processActionEvent((ActionEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processItemEvent(ItemEvent paramItemEvent) {
    ItemListener itemListener1 = this.itemListener;
    if (itemListener1 != null)
      itemListener1.itemStateChanged(paramItemEvent); 
  }
  
  protected void processActionEvent(ActionEvent paramActionEvent) {
    ActionListener actionListener1 = this.actionListener;
    if (actionListener1 != null)
      actionListener1.actionPerformed(paramActionEvent); 
  }
  
  protected String paramString() { return super.paramString() + ",selected=" + getSelectedItem(); }
  
  @Deprecated
  public void delItems(int paramInt1, int paramInt2) {
    for (int i = paramInt2; i >= paramInt1; i--)
      this.items.removeElementAt(i); 
    ListPeer listPeer = (ListPeer)this.peer;
    if (listPeer != null)
      listPeer.delItems(paramInt1, paramInt2); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    synchronized (this) {
      ListPeer listPeer = (ListPeer)this.peer;
      if (listPeer != null)
        this.selected = listPeer.getSelectedIndexes(); 
    } 
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "itemL", this.itemListener);
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", this.actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("itemL" == str) {
        addItemListener((ItemListener)paramObjectInputStream.readObject());
        continue;
      } 
      if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTList(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleAWTList extends Component.AccessibleAWTComponent implements AccessibleSelection, ItemListener, ActionListener {
    private static final long serialVersionUID = 7924617370136012829L;
    
    public AccessibleAWTList() {
      super(List.this);
      this$0.addActionListener(this);
      this$0.addItemListener(this);
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {}
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {}
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (List.this.isMultipleMode())
        accessibleStateSet.add(AccessibleState.MULTISELECTABLE); 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.LIST; }
    
    public Accessible getAccessibleAt(Point param1Point) { return null; }
    
    public int getAccessibleChildrenCount() { return List.this.getItemCount(); }
    
    public Accessible getAccessibleChild(int param1Int) {
      synchronized (List.this) {
        if (param1Int >= List.this.getItemCount())
          return null; 
        return new AccessibleAWTListChild(List.this, param1Int);
      } 
    }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public int getAccessibleSelectionCount() { return List.this.getSelectedIndexes().length; }
    
    public Accessible getAccessibleSelection(int param1Int) {
      synchronized (List.this) {
        int i = getAccessibleSelectionCount();
        if (param1Int < 0 || param1Int >= i)
          return null; 
        return getAccessibleChild(List.this.getSelectedIndexes()[param1Int]);
      } 
    }
    
    public boolean isAccessibleChildSelected(int param1Int) { return List.this.isIndexSelected(param1Int); }
    
    public void addAccessibleSelection(int param1Int) throws HeadlessException { List.this.select(param1Int); }
    
    public void removeAccessibleSelection(int param1Int) throws HeadlessException { List.this.deselect(param1Int); }
    
    public void clearAccessibleSelection() throws HeadlessException {
      synchronized (List.this) {
        int[] arrayOfInt = List.this.getSelectedIndexes();
        if (arrayOfInt == null)
          return; 
        for (int i = arrayOfInt.length - 1; i >= 0; i--)
          List.this.deselect(arrayOfInt[i]); 
      } 
    }
    
    public void selectAllAccessibleSelection() throws HeadlessException {
      synchronized (List.this) {
        for (int i = List.this.getItemCount() - 1; i >= 0; i--)
          List.this.select(i); 
      } 
    }
    
    protected class AccessibleAWTListChild extends Component.AccessibleAWTComponent implements Accessible {
      private static final long serialVersionUID = 4412022926028300317L;
      
      private List parent;
      
      private int indexInParent;
      
      public AccessibleAWTListChild(List param2List, int param2Int) {
        super(List.AccessibleAWTList.this.this$0);
        this.parent = param2List;
        setAccessibleParent(param2List);
        this.indexInParent = param2Int;
      }
      
      public AccessibleContext getAccessibleContext() { return this; }
      
      public AccessibleRole getAccessibleRole() { return AccessibleRole.LIST_ITEM; }
      
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
        if (this.parent.isIndexSelected(this.indexInParent))
          accessibleStateSet.add(AccessibleState.SELECTED); 
        return accessibleStateSet;
      }
      
      public Locale getLocale() { return this.parent.getLocale(); }
      
      public int getAccessibleIndexInParent() { return this.indexInParent; }
      
      public int getAccessibleChildrenCount() { return 0; }
      
      public Accessible getAccessibleChild(int param2Int) { return null; }
      
      public Color getBackground() { return this.parent.getBackground(); }
      
      public void setBackground(Color param2Color) { this.parent.setBackground(param2Color); }
      
      public Color getForeground() { return this.parent.getForeground(); }
      
      public void setForeground(Color param2Color) { this.parent.setForeground(param2Color); }
      
      public Cursor getCursor() { return this.parent.getCursor(); }
      
      public void setCursor(Cursor param2Cursor) { this.parent.setCursor(param2Cursor); }
      
      public Font getFont() { return this.parent.getFont(); }
      
      public void setFont(Font param2Font) { this.parent.setFont(param2Font); }
      
      public FontMetrics getFontMetrics(Font param2Font) { return this.parent.getFontMetrics(param2Font); }
      
      public boolean isEnabled() { return this.parent.isEnabled(); }
      
      public void setEnabled(boolean param2Boolean) { this.parent.setEnabled(param2Boolean); }
      
      public boolean isVisible() { return false; }
      
      public void setVisible(boolean param2Boolean) { this.parent.setVisible(param2Boolean); }
      
      public boolean isShowing() { return false; }
      
      public boolean contains(Point param2Point) { return false; }
      
      public Point getLocationOnScreen() { return null; }
      
      public Point getLocation() { return null; }
      
      public void setLocation(Point param2Point) {}
      
      public Rectangle getBounds() { return null; }
      
      public void setBounds(Rectangle param2Rectangle) {}
      
      public Dimension getSize() { return null; }
      
      public void setSize(Dimension param2Dimension) {}
      
      public Accessible getAccessibleAt(Point param2Point) { return null; }
      
      public boolean isFocusTraversable() { return false; }
      
      public void requestFocus() throws HeadlessException {}
      
      public void addFocusListener(FocusListener param2FocusListener) {}
      
      public void removeFocusListener(FocusListener param2FocusListener) {}
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\List.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */