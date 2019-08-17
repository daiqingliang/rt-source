package java.awt;

import java.awt.peer.TextAreaPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class TextArea extends TextComponent {
  int rows;
  
  int columns;
  
  private static final String base = "text";
  
  private static int nameCounter = 0;
  
  public static final int SCROLLBARS_BOTH = 0;
  
  public static final int SCROLLBARS_VERTICAL_ONLY = 1;
  
  public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;
  
  public static final int SCROLLBARS_NONE = 3;
  
  private int scrollbarVisibility;
  
  private static Set<AWTKeyStroke> forwardTraversalKeys;
  
  private static Set<AWTKeyStroke> backwardTraversalKeys;
  
  private static final long serialVersionUID = 3692302836626095722L;
  
  private int textAreaSerializedDataVersion = 2;
  
  private static native void initIDs();
  
  public TextArea() { this("", 0, 0, 0); }
  
  public TextArea(String paramString) throws HeadlessException { this(paramString, 0, 0, 0); }
  
  public TextArea(int paramInt1, int paramInt2) throws HeadlessException { this("", paramInt1, paramInt2, 0); }
  
  public TextArea(String paramString, int paramInt1, int paramInt2) throws HeadlessException { this(paramString, paramInt1, paramInt2, 0); }
  
  public TextArea(String paramString, int paramInt1, int paramInt2, int paramInt3) throws HeadlessException {
    super(paramString);
    this.rows = (paramInt1 >= 0) ? paramInt1 : 0;
    this.columns = (paramInt2 >= 0) ? paramInt2 : 0;
    if (paramInt3 >= 0 && paramInt3 <= 3) {
      this.scrollbarVisibility = paramInt3;
    } else {
      this.scrollbarVisibility = 0;
    } 
    setFocusTraversalKeys(0, forwardTraversalKeys);
    setFocusTraversalKeys(1, backwardTraversalKeys);
  }
  
  String constructComponentName() {
    synchronized (TextArea.class) {
      return "text" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createTextArea(this); 
      super.addNotify();
    } 
  }
  
  public void insert(String paramString, int paramInt) { insertText(paramString, paramInt); }
  
  @Deprecated
  public void insertText(String paramString, int paramInt) {
    TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
    if (textAreaPeer != null)
      textAreaPeer.insert(paramString, paramInt); 
    this.text = this.text.substring(0, paramInt) + paramString + this.text.substring(paramInt);
  }
  
  public void append(String paramString) throws HeadlessException { appendText(paramString); }
  
  @Deprecated
  public void appendText(String paramString) throws HeadlessException { insertText(paramString, getText().length()); }
  
  public void replaceRange(String paramString, int paramInt1, int paramInt2) throws HeadlessException { replaceText(paramString, paramInt1, paramInt2); }
  
  @Deprecated
  public void replaceText(String paramString, int paramInt1, int paramInt2) throws HeadlessException {
    TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
    if (textAreaPeer != null)
      textAreaPeer.replaceRange(paramString, paramInt1, paramInt2); 
    this.text = this.text.substring(0, paramInt1) + paramString + this.text.substring(paramInt2);
  }
  
  public int getRows() { return this.rows; }
  
  public void setRows(int paramInt) {
    int i = this.rows;
    if (paramInt < 0)
      throw new IllegalArgumentException("rows less than zero."); 
    if (paramInt != i) {
      this.rows = paramInt;
      invalidate();
    } 
  }
  
  public int getColumns() { return this.columns; }
  
  public void setColumns(int paramInt) {
    int i = this.columns;
    if (paramInt < 0)
      throw new IllegalArgumentException("columns less than zero."); 
    if (paramInt != i) {
      this.columns = paramInt;
      invalidate();
    } 
  }
  
  public int getScrollbarVisibility() { return this.scrollbarVisibility; }
  
  public Dimension getPreferredSize(int paramInt1, int paramInt2) { return preferredSize(paramInt1, paramInt2); }
  
  @Deprecated
  public Dimension preferredSize(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
      return (textAreaPeer != null) ? textAreaPeer.getPreferredSize(paramInt1, paramInt2) : super.preferredSize();
    } 
  }
  
  public Dimension getPreferredSize() { return preferredSize(); }
  
  @Deprecated
  public Dimension preferredSize() {
    synchronized (getTreeLock()) {
      return (this.rows > 0 && this.columns > 0) ? preferredSize(this.rows, this.columns) : super.preferredSize();
    } 
  }
  
  public Dimension getMinimumSize(int paramInt1, int paramInt2) { return minimumSize(paramInt1, paramInt2); }
  
  @Deprecated
  public Dimension minimumSize(int paramInt1, int paramInt2) {
    synchronized (getTreeLock()) {
      TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
      return (textAreaPeer != null) ? textAreaPeer.getMinimumSize(paramInt1, paramInt2) : super.minimumSize();
    } 
  }
  
  public Dimension getMinimumSize() { return minimumSize(); }
  
  @Deprecated
  public Dimension minimumSize() {
    synchronized (getTreeLock()) {
      return (this.rows > 0 && this.columns > 0) ? minimumSize(this.rows, this.columns) : super.minimumSize();
    } 
  }
  
  protected String paramString() {
    switch (this.scrollbarVisibility) {
      case 0:
        str = "both";
        return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + str;
      case 1:
        str = "vertical-only";
        return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + str;
      case 2:
        str = "horizontal-only";
        return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + str;
      case 3:
        str = "none";
        return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + str;
    } 
    String str = "invalid display policy";
    return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + str;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    paramObjectInputStream.defaultReadObject();
    if (this.columns < 0)
      this.columns = 0; 
    if (this.rows < 0)
      this.rows = 0; 
    if (this.scrollbarVisibility < 0 || this.scrollbarVisibility > 3)
      this.scrollbarVisibility = 0; 
    if (this.textAreaSerializedDataVersion < 2) {
      setFocusTraversalKeys(0, forwardTraversalKeys);
      setFocusTraversalKeys(1, backwardTraversalKeys);
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTTextArea(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    forwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl TAB", new HashSet());
    backwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl shift TAB", new HashSet());
  }
  
  protected class AccessibleAWTTextArea extends TextComponent.AccessibleAWTTextComponent {
    private static final long serialVersionUID = 3472827823632144419L;
    
    protected AccessibleAWTTextArea() { super(TextArea.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.MULTI_LINE);
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\TextArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */