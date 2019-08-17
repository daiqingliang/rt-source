package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleTextSequence;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

public class JPasswordField extends JTextField {
  private static final String uiClassID = "PasswordFieldUI";
  
  private char echoChar;
  
  private boolean echoCharSet = false;
  
  public JPasswordField() { this(null, null, 0); }
  
  public JPasswordField(String paramString) { this(null, paramString, 0); }
  
  public JPasswordField(int paramInt) { this(null, null, paramInt); }
  
  public JPasswordField(String paramString, int paramInt) { this(null, paramString, paramInt); }
  
  public JPasswordField(Document paramDocument, String paramString, int paramInt) {
    super(paramDocument, paramString, paramInt);
    enableInputMethods(false);
  }
  
  public String getUIClassID() { return "PasswordFieldUI"; }
  
  public void updateUI() {
    if (!this.echoCharSet)
      this.echoChar = '*'; 
    super.updateUI();
  }
  
  public char getEchoChar() { return this.echoChar; }
  
  public void setEchoChar(char paramChar) {
    this.echoChar = paramChar;
    this.echoCharSet = true;
    repaint();
    revalidate();
  }
  
  public boolean echoCharIsSet() { return (this.echoChar != '\000'); }
  
  public void cut() {
    if (getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } else {
      super.cut();
    } 
  }
  
  public void copy() {
    if (getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } else {
      super.copy();
    } 
  }
  
  @Deprecated
  public String getText() { return super.getText(); }
  
  @Deprecated
  public String getText(int paramInt1, int paramInt2) throws BadLocationException { return super.getText(paramInt1, paramInt2); }
  
  public char[] getPassword() {
    Document document = getDocument();
    Segment segment = new Segment();
    try {
      document.getText(0, document.getLength(), segment);
    } catch (BadLocationException badLocationException) {
      return null;
    } 
    char[] arrayOfChar = new char[segment.count];
    System.arraycopy(segment.array, segment.offset, arrayOfChar, 0, segment.count);
    return arrayOfChar;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("PasswordFieldUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() { return super.paramString() + ",echoChar=" + this.echoChar; }
  
  boolean customSetUIProperty(String paramString, Object paramObject) {
    if (paramString == "echoChar") {
      if (!this.echoCharSet) {
        setEchoChar(((Character)paramObject).charValue());
        this.echoCharSet = false;
      } 
      return true;
    } 
    return false;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJPasswordField(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJPasswordField extends JTextField.AccessibleJTextField {
    protected AccessibleJPasswordField() { super(JPasswordField.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PASSWORD_TEXT; }
    
    public AccessibleText getAccessibleText() { return this; }
    
    private String getEchoString(String param1String) {
      if (param1String == null)
        return null; 
      char[] arrayOfChar = new char[param1String.length()];
      Arrays.fill(arrayOfChar, JPasswordField.this.getEchoChar());
      return new String(arrayOfChar);
    }
    
    public String getAtIndex(int param1Int1, int param1Int2) throws BadLocationException {
      String str = null;
      if (param1Int1 == 1) {
        str = super.getAtIndex(param1Int1, param1Int2);
      } else {
        char[] arrayOfChar = JPasswordField.this.getPassword();
        if (arrayOfChar == null || param1Int2 < 0 || param1Int2 >= arrayOfChar.length)
          return null; 
        str = new String(arrayOfChar);
      } 
      return getEchoString(str);
    }
    
    public String getAfterIndex(int param1Int1, int param1Int2) throws BadLocationException {
      if (param1Int1 == 1) {
        String str = super.getAfterIndex(param1Int1, param1Int2);
        return getEchoString(str);
      } 
      return null;
    }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) throws BadLocationException {
      if (param1Int1 == 1) {
        String str = super.getBeforeIndex(param1Int1, param1Int2);
        return getEchoString(str);
      } 
      return null;
    }
    
    public String getTextRange(int param1Int1, int param1Int2) throws BadLocationException {
      String str = super.getTextRange(param1Int1, param1Int2);
      return getEchoString(str);
    }
    
    public AccessibleTextSequence getTextSequenceAt(int param1Int1, int param1Int2) {
      if (param1Int1 == 1) {
        AccessibleTextSequence accessibleTextSequence = super.getTextSequenceAt(param1Int1, param1Int2);
        return (accessibleTextSequence == null) ? null : new AccessibleTextSequence(accessibleTextSequence.startIndex, accessibleTextSequence.endIndex, getEchoString(accessibleTextSequence.text));
      } 
      char[] arrayOfChar = JPasswordField.this.getPassword();
      if (arrayOfChar == null || param1Int2 < 0 || param1Int2 >= arrayOfChar.length)
        return null; 
      String str = new String(arrayOfChar);
      return new AccessibleTextSequence(0, arrayOfChar.length - 1, getEchoString(str));
    }
    
    public AccessibleTextSequence getTextSequenceAfter(int param1Int1, int param1Int2) {
      if (param1Int1 == 1) {
        AccessibleTextSequence accessibleTextSequence = super.getTextSequenceAfter(param1Int1, param1Int2);
        return (accessibleTextSequence == null) ? null : new AccessibleTextSequence(accessibleTextSequence.startIndex, accessibleTextSequence.endIndex, getEchoString(accessibleTextSequence.text));
      } 
      return null;
    }
    
    public AccessibleTextSequence getTextSequenceBefore(int param1Int1, int param1Int2) {
      if (param1Int1 == 1) {
        AccessibleTextSequence accessibleTextSequence = super.getTextSequenceBefore(param1Int1, param1Int2);
        return (accessibleTextSequence == null) ? null : new AccessibleTextSequence(accessibleTextSequence.startIndex, accessibleTextSequence.endIndex, getEchoString(accessibleTextSequence.text));
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JPasswordField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */