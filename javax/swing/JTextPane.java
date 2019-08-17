package javax.swing;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

public class JTextPane extends JEditorPane {
  private static final String uiClassID = "TextPaneUI";
  
  public JTextPane() {
    EditorKit editorKit = createDefaultEditorKit();
    String str = editorKit.getContentType();
    if (str != null && getEditorKitClassNameForContentType(str) == defaultEditorKitMap.get(str))
      setEditorKitForContentType(str, editorKit); 
    setEditorKit(editorKit);
  }
  
  public JTextPane(StyledDocument paramStyledDocument) {
    this();
    setStyledDocument(paramStyledDocument);
  }
  
  public String getUIClassID() { return "TextPaneUI"; }
  
  public void setDocument(Document paramDocument) {
    if (paramDocument instanceof StyledDocument) {
      super.setDocument(paramDocument);
    } else {
      throw new IllegalArgumentException("Model must be StyledDocument");
    } 
  }
  
  public void setStyledDocument(StyledDocument paramStyledDocument) { super.setDocument(paramStyledDocument); }
  
  public StyledDocument getStyledDocument() { return (StyledDocument)getDocument(); }
  
  public void replaceSelection(String paramString) { replaceSelection(paramString, true); }
  
  private void replaceSelection(String paramString, boolean paramBoolean) {
    if (paramBoolean && !isEditable()) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
      return;
    } 
    StyledDocument styledDocument = getStyledDocument();
    if (styledDocument != null)
      try {
        Caret caret = getCaret();
        boolean bool = saveComposedText(caret.getDot());
        int i = Math.min(caret.getDot(), caret.getMark());
        int j = Math.max(caret.getDot(), caret.getMark());
        AttributeSet attributeSet = getInputAttributes().copyAttributes();
        if (styledDocument instanceof AbstractDocument) {
          ((AbstractDocument)styledDocument).replace(i, j - i, paramString, attributeSet);
        } else {
          if (i != j)
            styledDocument.remove(i, j - i); 
          if (paramString != null && paramString.length() > 0)
            styledDocument.insertString(i, paramString, attributeSet); 
        } 
        if (bool)
          restoreComposedText(); 
      } catch (BadLocationException badLocationException) {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
      }  
  }
  
  public void insertComponent(Component paramComponent) {
    MutableAttributeSet mutableAttributeSet = getInputAttributes();
    mutableAttributeSet.removeAttributes(mutableAttributeSet);
    StyleConstants.setComponent(mutableAttributeSet, paramComponent);
    replaceSelection(" ", false);
    mutableAttributeSet.removeAttributes(mutableAttributeSet);
  }
  
  public void insertIcon(Icon paramIcon) {
    MutableAttributeSet mutableAttributeSet = getInputAttributes();
    mutableAttributeSet.removeAttributes(mutableAttributeSet);
    StyleConstants.setIcon(mutableAttributeSet, paramIcon);
    replaceSelection(" ", false);
    mutableAttributeSet.removeAttributes(mutableAttributeSet);
  }
  
  public Style addStyle(String paramString, Style paramStyle) {
    StyledDocument styledDocument = getStyledDocument();
    return styledDocument.addStyle(paramString, paramStyle);
  }
  
  public void removeStyle(String paramString) {
    StyledDocument styledDocument = getStyledDocument();
    styledDocument.removeStyle(paramString);
  }
  
  public Style getStyle(String paramString) {
    StyledDocument styledDocument = getStyledDocument();
    return styledDocument.getStyle(paramString);
  }
  
  public void setLogicalStyle(Style paramStyle) {
    StyledDocument styledDocument = getStyledDocument();
    styledDocument.setLogicalStyle(getCaretPosition(), paramStyle);
  }
  
  public Style getLogicalStyle() {
    StyledDocument styledDocument = getStyledDocument();
    return styledDocument.getLogicalStyle(getCaretPosition());
  }
  
  public AttributeSet getCharacterAttributes() {
    StyledDocument styledDocument = getStyledDocument();
    Element element = styledDocument.getCharacterElement(getCaretPosition());
    return (element != null) ? element.getAttributes() : null;
  }
  
  public void setCharacterAttributes(AttributeSet paramAttributeSet, boolean paramBoolean) {
    int i = getSelectionStart();
    int j = getSelectionEnd();
    if (i != j) {
      StyledDocument styledDocument = getStyledDocument();
      styledDocument.setCharacterAttributes(i, j - i, paramAttributeSet, paramBoolean);
    } else {
      MutableAttributeSet mutableAttributeSet = getInputAttributes();
      if (paramBoolean)
        mutableAttributeSet.removeAttributes(mutableAttributeSet); 
      mutableAttributeSet.addAttributes(paramAttributeSet);
    } 
  }
  
  public AttributeSet getParagraphAttributes() {
    StyledDocument styledDocument = getStyledDocument();
    Element element = styledDocument.getParagraphElement(getCaretPosition());
    return (element != null) ? element.getAttributes() : null;
  }
  
  public void setParagraphAttributes(AttributeSet paramAttributeSet, boolean paramBoolean) {
    int i = getSelectionStart();
    int j = getSelectionEnd();
    StyledDocument styledDocument = getStyledDocument();
    styledDocument.setParagraphAttributes(i, j - i, paramAttributeSet, paramBoolean);
  }
  
  public MutableAttributeSet getInputAttributes() { return getStyledEditorKit().getInputAttributes(); }
  
  protected final StyledEditorKit getStyledEditorKit() { return (StyledEditorKit)getEditorKit(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TextPaneUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected EditorKit createDefaultEditorKit() { return new StyledEditorKit(); }
  
  public final void setEditorKit(EditorKit paramEditorKit) {
    if (paramEditorKit instanceof StyledEditorKit) {
      super.setEditorKit(paramEditorKit);
    } else {
      throw new IllegalArgumentException("Must be StyledEditorKit");
    } 
  }
  
  protected String paramString() { return super.paramString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JTextPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */