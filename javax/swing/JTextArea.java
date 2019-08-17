package javax.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class JTextArea extends JTextComponent {
  private static final String uiClassID = "TextAreaUI";
  
  private int rows;
  
  private int columns;
  
  private int columnWidth;
  
  private int rowHeight;
  
  private boolean wrap;
  
  private boolean word;
  
  public JTextArea() { this(null, null, 0, 0); }
  
  public JTextArea(String paramString) { this(null, paramString, 0, 0); }
  
  public JTextArea(int paramInt1, int paramInt2) { this(null, null, paramInt1, paramInt2); }
  
  public JTextArea(String paramString, int paramInt1, int paramInt2) { this(null, paramString, paramInt1, paramInt2); }
  
  public JTextArea(Document paramDocument) { this(paramDocument, null, 0, 0); }
  
  public JTextArea(Document paramDocument, String paramString, int paramInt1, int paramInt2) {
    this.rows = paramInt1;
    this.columns = paramInt2;
    if (paramDocument == null)
      paramDocument = createDefaultModel(); 
    setDocument(paramDocument);
    if (paramString != null) {
      setText(paramString);
      select(0, 0);
    } 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("rows: " + paramInt1); 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("columns: " + paramInt2); 
    LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
    LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
  }
  
  public String getUIClassID() { return "TextAreaUI"; }
  
  protected Document createDefaultModel() { return new PlainDocument(); }
  
  public void setTabSize(int paramInt) {
    Document document = getDocument();
    if (document != null) {
      int i = getTabSize();
      document.putProperty("tabSize", Integer.valueOf(paramInt));
      firePropertyChange("tabSize", i, paramInt);
    } 
  }
  
  public int getTabSize() {
    int i = 8;
    Document document = getDocument();
    if (document != null) {
      Integer integer = (Integer)document.getProperty("tabSize");
      if (integer != null)
        i = integer.intValue(); 
    } 
    return i;
  }
  
  public void setLineWrap(boolean paramBoolean) {
    boolean bool = this.wrap;
    this.wrap = paramBoolean;
    firePropertyChange("lineWrap", bool, paramBoolean);
  }
  
  public boolean getLineWrap() { return this.wrap; }
  
  public void setWrapStyleWord(boolean paramBoolean) {
    boolean bool = this.word;
    this.word = paramBoolean;
    firePropertyChange("wrapStyleWord", bool, paramBoolean);
  }
  
  public boolean getWrapStyleWord() { return this.word; }
  
  public int getLineOfOffset(int paramInt) throws BadLocationException {
    Document document = getDocument();
    if (paramInt < 0)
      throw new BadLocationException("Can't translate offset to line", -1); 
    if (paramInt > document.getLength())
      throw new BadLocationException("Can't translate offset to line", document.getLength() + 1); 
    Element element = getDocument().getDefaultRootElement();
    return element.getElementIndex(paramInt);
  }
  
  public int getLineCount() {
    Element element = getDocument().getDefaultRootElement();
    return element.getElementCount();
  }
  
  public int getLineStartOffset(int paramInt) throws BadLocationException {
    int i = getLineCount();
    if (paramInt < 0)
      throw new BadLocationException("Negative line", -1); 
    if (paramInt >= i)
      throw new BadLocationException("No such line", getDocument().getLength() + 1); 
    Element element1 = getDocument().getDefaultRootElement();
    Element element2 = element1.getElement(paramInt);
    return element2.getStartOffset();
  }
  
  public int getLineEndOffset(int paramInt) throws BadLocationException {
    int i = getLineCount();
    if (paramInt < 0)
      throw new BadLocationException("Negative line", -1); 
    if (paramInt >= i)
      throw new BadLocationException("No such line", getDocument().getLength() + 1); 
    Element element1 = getDocument().getDefaultRootElement();
    Element element2 = element1.getElement(paramInt);
    int j = element2.getEndOffset();
    return (paramInt == i - 1) ? (j - 1) : j;
  }
  
  public void insert(String paramString, int paramInt) {
    Document document = getDocument();
    if (document != null)
      try {
        document.insertString(paramInt, paramString, null);
      } catch (BadLocationException badLocationException) {
        throw new IllegalArgumentException(badLocationException.getMessage());
      }  
  }
  
  public void append(String paramString) {
    Document document = getDocument();
    if (document != null)
      try {
        document.insertString(document.getLength(), paramString, null);
      } catch (BadLocationException badLocationException) {} 
  }
  
  public void replaceRange(String paramString, int paramInt1, int paramInt2) {
    if (paramInt2 < paramInt1)
      throw new IllegalArgumentException("end before start"); 
    Document document = getDocument();
    if (document != null)
      try {
        if (document instanceof AbstractDocument) {
          ((AbstractDocument)document).replace(paramInt1, paramInt2 - paramInt1, paramString, null);
        } else {
          document.remove(paramInt1, paramInt2 - paramInt1);
          document.insertString(paramInt1, paramString, null);
        } 
      } catch (BadLocationException badLocationException) {
        throw new IllegalArgumentException(badLocationException.getMessage());
      }  
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
  
  protected int getRowHeight() {
    if (this.rowHeight == 0) {
      FontMetrics fontMetrics = getFontMetrics(getFont());
      this.rowHeight = fontMetrics.getHeight();
    } 
    return this.rowHeight;
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
  
  protected int getColumnWidth() {
    if (this.columnWidth == 0) {
      FontMetrics fontMetrics = getFontMetrics(getFont());
      this.columnWidth = fontMetrics.charWidth('m');
    } 
    return this.columnWidth;
  }
  
  public Dimension getPreferredSize() {
    Dimension dimension = super.getPreferredSize();
    dimension = (dimension == null) ? new Dimension(400, 400) : dimension;
    Insets insets = getInsets();
    if (this.columns != 0)
      dimension.width = Math.max(dimension.width, this.columns * getColumnWidth() + insets.left + insets.right); 
    if (this.rows != 0)
      dimension.height = Math.max(dimension.height, this.rows * getRowHeight() + insets.top + insets.bottom); 
    return dimension;
  }
  
  public void setFont(Font paramFont) {
    super.setFont(paramFont);
    this.rowHeight = 0;
    this.columnWidth = 0;
  }
  
  protected String paramString() {
    String str1 = this.wrap ? "true" : "false";
    String str2 = this.word ? "true" : "false";
    return super.paramString() + ",colums=" + this.columns + ",columWidth=" + this.columnWidth + ",rows=" + this.rows + ",rowHeight=" + this.rowHeight + ",word=" + str2 + ",wrap=" + str1;
  }
  
  public boolean getScrollableTracksViewportWidth() { return this.wrap ? true : super.getScrollableTracksViewportWidth(); }
  
  public Dimension getPreferredScrollableViewportSize() {
    Dimension dimension = super.getPreferredScrollableViewportSize();
    dimension = (dimension == null) ? new Dimension(400, 400) : dimension;
    Insets insets = getInsets();
    dimension.width = (this.columns == 0) ? dimension.width : (this.columns * getColumnWidth() + insets.left + insets.right);
    dimension.height = (this.rows == 0) ? dimension.height : (this.rows * getRowHeight() + insets.top + insets.bottom);
    return dimension;
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 1:
        return getRowHeight();
      case 0:
        return getColumnWidth();
    } 
    throw new IllegalArgumentException("Invalid orientation: " + paramInt1);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TextAreaUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJTextArea(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJTextArea extends JTextComponent.AccessibleJTextComponent {
    protected AccessibleJTextArea() { super(JTextArea.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.MULTI_LINE);
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JTextArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */