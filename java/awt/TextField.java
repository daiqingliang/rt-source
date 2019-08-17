package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.TextFieldPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;

public class TextField extends TextComponent {
  int columns;
  
  char echoChar;
  
  ActionListener actionListener;
  
  private static final String base = "textfield";
  
  private static int nameCounter = 0;
  
  private static final long serialVersionUID = -2966288784432217853L;
  
  private int textFieldSerializedDataVersion = 1;
  
  private static native void initIDs();
  
  public TextField() { this("", 0); }
  
  public TextField(String paramString) throws HeadlessException { this(paramString, (paramString != null) ? paramString.length() : 0); }
  
  public TextField(int paramInt) throws HeadlessException { this("", paramInt); }
  
  public TextField(String paramString, int paramInt) throws HeadlessException {
    super(paramString);
    this.columns = (paramInt >= 0) ? paramInt : 0;
  }
  
  String constructComponentName() {
    synchronized (TextField.class) {
      return "textfield" + nameCounter++;
    } 
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createTextField(this); 
      super.addNotify();
    } 
  }
  
  public char getEchoChar() { return this.echoChar; }
  
  public void setEchoChar(char paramChar) { setEchoCharacter(paramChar); }
  
  @Deprecated
  public void setEchoCharacter(char paramChar) {
    if (this.echoChar != paramChar) {
      this.echoChar = paramChar;
      TextFieldPeer textFieldPeer = (TextFieldPeer)this.peer;
      if (textFieldPeer != null)
        textFieldPeer.setEchoChar(paramChar); 
    } 
  }
  
  public void setText(String paramString) throws HeadlessException {
    super.setText(paramString);
    invalidateIfValid();
  }
  
  public boolean echoCharIsSet() { return (this.echoChar != '\000'); }
  
  public int getColumns() { return this.columns; }
  
  public void setColumns(int paramInt) throws HeadlessException {
    int i;
    synchronized (this) {
      i = this.columns;
      if (paramInt < 0)
        throw new IllegalArgumentException("columns less than zero."); 
      if (paramInt != i)
        this.columns = paramInt; 
    } 
    if (paramInt != i)
      invalidate(); 
  }
  
  public Dimension getPreferredSize(int paramInt) { return preferredSize(paramInt); }
  
  @Deprecated
  public Dimension preferredSize(int paramInt) {
    synchronized (getTreeLock()) {
      TextFieldPeer textFieldPeer = (TextFieldPeer)this.peer;
      return (textFieldPeer != null) ? textFieldPeer.getPreferredSize(paramInt) : super.preferredSize();
    } 
  }
  
  public Dimension getPreferredSize() { return preferredSize(); }
  
  @Deprecated
  public Dimension preferredSize() {
    synchronized (getTreeLock()) {
      return (this.columns > 0) ? preferredSize(this.columns) : super.preferredSize();
    } 
  }
  
  public Dimension getMinimumSize(int paramInt) { return minimumSize(paramInt); }
  
  @Deprecated
  public Dimension minimumSize(int paramInt) {
    synchronized (getTreeLock()) {
      TextFieldPeer textFieldPeer = (TextFieldPeer)this.peer;
      return (textFieldPeer != null) ? textFieldPeer.getMinimumSize(paramInt) : super.minimumSize();
    } 
  }
  
  public Dimension getMinimumSize() { return minimumSize(); }
  
  @Deprecated
  public Dimension minimumSize() {
    synchronized (getTreeLock()) {
      return (this.columns > 0) ? minimumSize(this.columns) : super.minimumSize();
    } 
  }
  
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
    ActionListener actionListener1 = null;
    if (paramClass == ActionListener.class) {
      actionListener1 = this.actionListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(actionListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return (paramAWTEvent.id == 1001) ? (((this.eventMask & 0x80L) != 0L || this.actionListener != null)) : super.eventEnabled(paramAWTEvent); }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ActionEvent) {
      processActionEvent((ActionEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processActionEvent(ActionEvent paramActionEvent) {
    ActionListener actionListener1 = this.actionListener;
    if (actionListener1 != null)
      actionListener1.actionPerformed(paramActionEvent); 
  }
  
  protected String paramString() {
    String str = super.paramString();
    if (this.echoChar != '\000')
      str = str + ",echo=" + this.echoChar; 
    return str;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", this.actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    paramObjectInputStream.defaultReadObject();
    if (this.columns < 0)
      this.columns = 0; 
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTTextField(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
  
  protected class AccessibleAWTTextField extends TextComponent.AccessibleAWTTextComponent {
    private static final long serialVersionUID = 6219164359235943158L;
    
    protected AccessibleAWTTextField() { super(TextField.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.SINGLE_LINE);
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\TextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */