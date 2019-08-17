package java.awt;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.swing.text.AttributeSet;
import sun.awt.InputMethodSupport;
import sun.security.util.SecurityConstants;

public class TextComponent extends Component implements Accessible {
  String text;
  
  boolean editable = true;
  
  int selectionStart;
  
  int selectionEnd;
  
  boolean backgroundSetByClientCode = false;
  
  protected TextListener textListener;
  
  private static final long serialVersionUID = -2214773872412987419L;
  
  private int textComponentSerializedDataVersion = 1;
  
  private boolean checkForEnableIM = true;
  
  TextComponent(String paramString) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.text = (paramString != null) ? paramString : "";
    setCursor(Cursor.getPredefinedCursor(2));
  }
  
  private void enableInputMethodsIfNecessary() {
    if (this.checkForEnableIM) {
      this.checkForEnableIM = false;
      try {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        boolean bool = false;
        if (toolkit instanceof InputMethodSupport)
          bool = ((InputMethodSupport)toolkit).enableInputMethodsForTextComponent(); 
        enableInputMethods(bool);
      } catch (Exception exception) {}
    } 
  }
  
  public void enableInputMethods(boolean paramBoolean) {
    this.checkForEnableIM = false;
    super.enableInputMethods(paramBoolean);
  }
  
  boolean areInputMethodsEnabled() {
    if (this.checkForEnableIM)
      enableInputMethodsIfNecessary(); 
    return ((this.eventMask & 0x1000L) != 0L);
  }
  
  public InputMethodRequests getInputMethodRequests() {
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    return (textComponentPeer != null) ? textComponentPeer.getInputMethodRequests() : null;
  }
  
  public void addNotify() {
    super.addNotify();
    enableInputMethodsIfNecessary();
  }
  
  public void removeNotify() {
    synchronized (getTreeLock()) {
      TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
      if (textComponentPeer != null) {
        this.text = textComponentPeer.getText();
        this.selectionStart = textComponentPeer.getSelectionStart();
        this.selectionEnd = textComponentPeer.getSelectionEnd();
      } 
      super.removeNotify();
    } 
  }
  
  public void setText(String paramString) throws HeadlessException {
    boolean bool = ((this.text == null || this.text.isEmpty()) && (paramString == null || paramString.isEmpty())) ? 1 : 0;
    this.text = (paramString != null) ? paramString : "";
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null && !bool)
      textComponentPeer.setText(this.text); 
  }
  
  public String getText() {
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null)
      this.text = textComponentPeer.getText(); 
    return this.text;
  }
  
  public String getSelectedText() { return getText().substring(getSelectionStart(), getSelectionEnd()); }
  
  public boolean isEditable() { return this.editable; }
  
  public void setEditable(boolean paramBoolean) {
    if (this.editable == paramBoolean)
      return; 
    this.editable = paramBoolean;
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null)
      textComponentPeer.setEditable(paramBoolean); 
  }
  
  public Color getBackground() { return (!this.editable && !this.backgroundSetByClientCode) ? SystemColor.control : super.getBackground(); }
  
  public void setBackground(Color paramColor) {
    this.backgroundSetByClientCode = true;
    super.setBackground(paramColor);
  }
  
  public int getSelectionStart() {
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null)
      this.selectionStart = textComponentPeer.getSelectionStart(); 
    return this.selectionStart;
  }
  
  public void setSelectionStart(int paramInt) { select(paramInt, getSelectionEnd()); }
  
  public int getSelectionEnd() {
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null)
      this.selectionEnd = textComponentPeer.getSelectionEnd(); 
    return this.selectionEnd;
  }
  
  public void setSelectionEnd(int paramInt) { select(getSelectionStart(), paramInt); }
  
  public void select(int paramInt1, int paramInt2) {
    String str = getText();
    if (paramInt1 < 0)
      paramInt1 = 0; 
    if (paramInt1 > str.length())
      paramInt1 = str.length(); 
    if (paramInt2 > str.length())
      paramInt2 = str.length(); 
    if (paramInt2 < paramInt1)
      paramInt2 = paramInt1; 
    this.selectionStart = paramInt1;
    this.selectionEnd = paramInt2;
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null)
      textComponentPeer.select(paramInt1, paramInt2); 
  }
  
  public void selectAll() {
    this.selectionStart = 0;
    this.selectionEnd = getText().length();
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null)
      textComponentPeer.select(this.selectionStart, this.selectionEnd); 
  }
  
  public void setCaretPosition(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("position less than zero."); 
    int i = getText().length();
    if (paramInt > i)
      paramInt = i; 
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null) {
      textComponentPeer.setCaretPosition(paramInt);
    } else {
      select(paramInt, paramInt);
    } 
  }
  
  public int getCaretPosition() {
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    int i = 0;
    if (textComponentPeer != null) {
      i = textComponentPeer.getCaretPosition();
    } else {
      i = this.selectionStart;
    } 
    int j = getText().length();
    if (i > j)
      i = j; 
    return i;
  }
  
  public void addTextListener(TextListener paramTextListener) {
    if (paramTextListener == null)
      return; 
    this.textListener = AWTEventMulticaster.add(this.textListener, paramTextListener);
    this.newEventsOnly = true;
  }
  
  public void removeTextListener(TextListener paramTextListener) {
    if (paramTextListener == null)
      return; 
    this.textListener = AWTEventMulticaster.remove(this.textListener, paramTextListener);
  }
  
  public TextListener[] getTextListeners() { return (TextListener[])getListeners(TextListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    TextListener textListener1 = null;
    if (paramClass == TextListener.class) {
      textListener1 = this.textListener;
    } else {
      return (T[])super.getListeners(paramClass);
    } 
    return (T[])AWTEventMulticaster.getListeners(textListener1, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return (paramAWTEvent.id == 900) ? (((this.eventMask & 0x400L) != 0L || this.textListener != null)) : super.eventEnabled(paramAWTEvent); }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof TextEvent) {
      processTextEvent((TextEvent)paramAWTEvent);
      return;
    } 
    super.processEvent(paramAWTEvent);
  }
  
  protected void processTextEvent(TextEvent paramTextEvent) {
    TextListener textListener1 = this.textListener;
    if (textListener1 != null) {
      int i = paramTextEvent.getID();
      switch (i) {
        case 900:
          textListener1.textValueChanged(paramTextEvent);
          break;
      } 
    } 
  }
  
  protected String paramString() {
    String str = super.paramString() + ",text=" + getText();
    if (this.editable)
      str = str + ",editable"; 
    return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
  }
  
  private boolean canAccessClipboard() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return true; 
    try {
      securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
      return true;
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
    if (textComponentPeer != null) {
      this.text = textComponentPeer.getText();
      this.selectionStart = textComponentPeer.getSelectionStart();
      this.selectionEnd = textComponentPeer.getSelectionEnd();
    } 
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "textL", this.textListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    this.text = (this.text != null) ? this.text : "";
    select(this.selectionStart, this.selectionEnd);
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("textL" == str) {
        addTextListener((TextListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
    enableInputMethodsIfNecessary();
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTTextComponent(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleAWTTextComponent extends Component.AccessibleAWTComponent implements AccessibleText, TextListener {
    private static final long serialVersionUID = 3631432373506317811L;
    
    private static final boolean NEXT = true;
    
    private static final boolean PREVIOUS = false;
    
    public AccessibleAWTTextComponent() {
      super(TextComponent.this);
      this$0.addTextListener(this);
    }
    
    public void textValueChanged(TextEvent param1TextEvent) {
      Integer integer = Integer.valueOf(TextComponent.this.getCaretPosition());
      firePropertyChange("AccessibleText", null, integer);
    }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (TextComponent.this.isEditable())
        accessibleStateSet.add(AccessibleState.EDITABLE); 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TEXT; }
    
    public AccessibleText getAccessibleText() { return this; }
    
    public int getIndexAtPoint(Point param1Point) { return -1; }
    
    public Rectangle getCharacterBounds(int param1Int) { return null; }
    
    public int getCharCount() { return TextComponent.this.getText().length(); }
    
    public int getCaretPosition() { return TextComponent.this.getCaretPosition(); }
    
    public AttributeSet getCharacterAttribute(int param1Int) { return null; }
    
    public int getSelectionStart() { return TextComponent.this.getSelectionStart(); }
    
    public int getSelectionEnd() { return TextComponent.this.getSelectionEnd(); }
    
    public String getSelectedText() {
      String str = TextComponent.this.getSelectedText();
      return (str == null || str.equals("")) ? null : str;
    }
    
    public String getAtIndex(int param1Int1, int param1Int2) {
      int i;
      BreakIterator breakIterator;
      String str;
      if (param1Int2 < 0 || param1Int2 >= TextComponent.this.getText().length())
        return null; 
      switch (param1Int1) {
        case 1:
          return TextComponent.this.getText().substring(param1Int2, param1Int2 + 1);
        case 2:
          str = TextComponent.this.getText();
          breakIterator = BreakIterator.getWordInstance();
          breakIterator.setText(str);
          i = breakIterator.following(param1Int2);
          return str.substring(breakIterator.previous(), i);
        case 3:
          str = TextComponent.this.getText();
          breakIterator = BreakIterator.getSentenceInstance();
          breakIterator.setText(str);
          i = breakIterator.following(param1Int2);
          return str.substring(breakIterator.previous(), i);
      } 
      return null;
    }
    
    private int findWordLimit(int param1Int, BreakIterator param1BreakIterator, boolean param1Boolean, String param1String) {
      int i = (param1Boolean == true) ? param1BreakIterator.following(param1Int) : param1BreakIterator.preceding(param1Int);
      int j;
      for (j = (param1Boolean == true) ? param1BreakIterator.next() : param1BreakIterator.previous(); j != -1; j = (param1Boolean == true) ? param1BreakIterator.next() : param1BreakIterator.previous()) {
        for (int k = Math.min(i, j); k < Math.max(i, j); k++) {
          if (Character.isLetter(param1String.charAt(k)))
            return i; 
        } 
        i = j;
      } 
      return -1;
    }
    
    public String getAfterIndex(int param1Int1, int param1Int2) {
      int j;
      int i;
      BreakIterator breakIterator;
      String str;
      if (param1Int2 < 0 || param1Int2 >= TextComponent.this.getText().length())
        return null; 
      switch (param1Int1) {
        case 1:
          return (param1Int2 + 1 >= TextComponent.this.getText().length()) ? null : TextComponent.this.getText().substring(param1Int2 + 1, param1Int2 + 2);
        case 2:
          str = TextComponent.this.getText();
          breakIterator = BreakIterator.getWordInstance();
          breakIterator.setText(str);
          i = findWordLimit(param1Int2, breakIterator, true, str);
          if (i == -1 || i >= str.length())
            return null; 
          j = breakIterator.following(i);
          return (j == -1 || j >= str.length()) ? null : str.substring(i, j);
        case 3:
          str = TextComponent.this.getText();
          breakIterator = BreakIterator.getSentenceInstance();
          breakIterator.setText(str);
          i = breakIterator.following(param1Int2);
          if (i == -1 || i >= str.length())
            return null; 
          j = breakIterator.following(i);
          return (j == -1 || j >= str.length()) ? null : str.substring(i, j);
      } 
      return null;
    }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) {
      int j;
      int i;
      BreakIterator breakIterator;
      String str;
      if (param1Int2 < 0 || param1Int2 > TextComponent.this.getText().length() - 1)
        return null; 
      switch (param1Int1) {
        case 1:
          return (param1Int2 == 0) ? null : TextComponent.this.getText().substring(param1Int2 - 1, param1Int2);
        case 2:
          str = TextComponent.this.getText();
          breakIterator = BreakIterator.getWordInstance();
          breakIterator.setText(str);
          i = findWordLimit(param1Int2, breakIterator, false, str);
          if (i == -1)
            return null; 
          j = breakIterator.preceding(i);
          return (j == -1) ? null : str.substring(j, i);
        case 3:
          str = TextComponent.this.getText();
          breakIterator = BreakIterator.getSentenceInstance();
          breakIterator.setText(str);
          i = breakIterator.following(param1Int2);
          i = breakIterator.previous();
          j = breakIterator.previous();
          return (j == -1) ? null : str.substring(j, i);
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\TextComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */