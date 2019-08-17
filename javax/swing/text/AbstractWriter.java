package javax.swing.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

public abstract class AbstractWriter {
  private ElementIterator it;
  
  private Writer out;
  
  private int indentLevel = 0;
  
  private int indentSpace = 2;
  
  private Document doc = null;
  
  private int maxLineLength = 100;
  
  private int currLength = 0;
  
  private int startOffset = 0;
  
  private int endOffset = 0;
  
  private int offsetIndent = 0;
  
  private String lineSeparator;
  
  private boolean canWrapLines;
  
  private boolean isLineEmpty;
  
  private char[] indentChars;
  
  private char[] tempChars;
  
  private char[] newlineChars;
  
  private Segment segment;
  
  protected static final char NEWLINE = '\n';
  
  protected AbstractWriter(Writer paramWriter, Document paramDocument) { this(paramWriter, paramDocument, 0, paramDocument.getLength()); }
  
  protected AbstractWriter(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2) {
    this.doc = paramDocument;
    this.it = new ElementIterator(paramDocument.getDefaultRootElement());
    this.out = paramWriter;
    this.startOffset = paramInt1;
    this.endOffset = paramInt1 + paramInt2;
    Object object = paramDocument.getProperty("__EndOfLine__");
    if (object instanceof String) {
      setLineSeparator((String)object);
    } else {
      String str = null;
      try {
        str = System.getProperty("line.separator");
      } catch (SecurityException securityException) {}
      if (str == null)
        str = "\n"; 
      setLineSeparator(str);
    } 
    this.canWrapLines = true;
  }
  
  protected AbstractWriter(Writer paramWriter, Element paramElement) { this(paramWriter, paramElement, 0, paramElement.getEndOffset()); }
  
  protected AbstractWriter(Writer paramWriter, Element paramElement, int paramInt1, int paramInt2) {
    this.doc = paramElement.getDocument();
    this.it = new ElementIterator(paramElement);
    this.out = paramWriter;
    this.startOffset = paramInt1;
    this.endOffset = paramInt1 + paramInt2;
    this.canWrapLines = true;
  }
  
  public int getStartOffset() { return this.startOffset; }
  
  public int getEndOffset() { return this.endOffset; }
  
  protected ElementIterator getElementIterator() { return this.it; }
  
  protected Writer getWriter() { return this.out; }
  
  protected Document getDocument() { return this.doc; }
  
  protected boolean inRange(Element paramElement) {
    int i = getStartOffset();
    int j = getEndOffset();
    return ((paramElement.getStartOffset() >= i && paramElement.getStartOffset() < j) || (i >= paramElement.getStartOffset() && i < paramElement.getEndOffset()));
  }
  
  protected abstract void write() throws IOException, BadLocationException;
  
  protected String getText(Element paramElement) throws BadLocationException { return this.doc.getText(paramElement.getStartOffset(), paramElement.getEndOffset() - paramElement.getStartOffset()); }
  
  protected void text(Element paramElement) throws BadLocationException, IOException {
    int i = Math.max(getStartOffset(), paramElement.getStartOffset());
    int j = Math.min(getEndOffset(), paramElement.getEndOffset());
    if (i < j) {
      if (this.segment == null)
        this.segment = new Segment(); 
      getDocument().getText(i, j - i, this.segment);
      if (this.segment.count > 0)
        write(this.segment.array, this.segment.offset, this.segment.count); 
    } 
  }
  
  protected void setLineLength(int paramInt) { this.maxLineLength = paramInt; }
  
  protected int getLineLength() { return this.maxLineLength; }
  
  protected void setCurrentLineLength(int paramInt) {
    this.currLength = paramInt;
    this.isLineEmpty = (this.currLength == 0);
  }
  
  protected int getCurrentLineLength() { return this.currLength; }
  
  protected boolean isLineEmpty() { return this.isLineEmpty; }
  
  protected void setCanWrapLines(boolean paramBoolean) { this.canWrapLines = paramBoolean; }
  
  protected boolean getCanWrapLines() { return this.canWrapLines; }
  
  protected void setIndentSpace(int paramInt) { this.indentSpace = paramInt; }
  
  protected int getIndentSpace() { return this.indentSpace; }
  
  public void setLineSeparator(String paramString) { this.lineSeparator = paramString; }
  
  public String getLineSeparator() { return this.lineSeparator; }
  
  protected void incrIndent() throws IOException, BadLocationException {
    if (this.offsetIndent > 0) {
      this.offsetIndent++;
    } else if (++this.indentLevel * getIndentSpace() >= getLineLength()) {
      this.offsetIndent++;
      this.indentLevel--;
    } 
  }
  
  protected void decrIndent() throws IOException, BadLocationException {
    if (this.offsetIndent > 0) {
      this.offsetIndent--;
    } else {
      this.indentLevel--;
    } 
  }
  
  protected int getIndentLevel() { return this.indentLevel; }
  
  protected void indent() throws IOException, BadLocationException {
    int i = getIndentLevel() * getIndentSpace();
    if (this.indentChars == null || i > this.indentChars.length) {
      this.indentChars = new char[i];
      for (byte b = 0; b < i; b++)
        this.indentChars[b] = ' '; 
    } 
    int j = getCurrentLineLength();
    boolean bool = isLineEmpty();
    output(this.indentChars, 0, i);
    if (bool && j == 0)
      this.isLineEmpty = true; 
  }
  
  protected void write(char paramChar) throws IOException {
    if (this.tempChars == null)
      this.tempChars = new char[128]; 
    this.tempChars[0] = paramChar;
    write(this.tempChars, 0, 1);
  }
  
  protected void write(String paramString) {
    if (paramString == null)
      return; 
    int i = paramString.length();
    if (this.tempChars == null || this.tempChars.length < i)
      this.tempChars = new char[i]; 
    paramString.getChars(0, i, this.tempChars, 0);
    write(this.tempChars, 0, i);
  }
  
  protected void writeLineSeparator() throws IOException, BadLocationException {
    String str = getLineSeparator();
    int i = str.length();
    if (this.newlineChars == null || this.newlineChars.length < i)
      this.newlineChars = new char[i]; 
    str.getChars(0, i, this.newlineChars, 0);
    output(this.newlineChars, 0, i);
    setCurrentLineLength(0);
  }
  
  protected void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (!getCanWrapLines()) {
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      int k;
      for (k = indexOf(paramArrayOfChar, '\n', paramInt1, j); k != -1; k = indexOf(paramArrayOfChar, '\n', i, j)) {
        if (k > i)
          output(paramArrayOfChar, i, k - i); 
        writeLineSeparator();
        i = k + 1;
      } 
      if (i < j)
        output(paramArrayOfChar, i, j - i); 
    } else {
      int i = paramInt1;
      int j = paramInt1 + paramInt2;
      int k = getCurrentLineLength();
      int m = getLineLength();
      while (i < j) {
        int n = indexOf(paramArrayOfChar, '\n', i, j);
        boolean bool1 = false;
        boolean bool2 = false;
        k = getCurrentLineLength();
        if (n != -1 && k + n - i < m) {
          if (n > i)
            output(paramArrayOfChar, i, n - i); 
          i = n + 1;
          bool2 = true;
        } else if (n == -1 && k + j - i < m) {
          if (j > i)
            output(paramArrayOfChar, i, j - i); 
          i = j;
        } else {
          int i1 = -1;
          int i2 = Math.min(j - i, m - k - 1);
          int i3;
          for (i3 = 0; i3 < i2; i3++) {
            if (Character.isWhitespace(paramArrayOfChar[i3 + i]))
              i1 = i3; 
          } 
          if (i1 != -1) {
            i1 += i + 1;
            output(paramArrayOfChar, i, i1 - i);
            i = i1;
            bool1 = true;
          } else {
            i3 = Math.max(0, i2);
            i2 = j - i;
            while (i3 < i2) {
              if (Character.isWhitespace(paramArrayOfChar[i3 + i])) {
                i1 = i3;
                break;
              } 
              i3++;
            } 
            if (i1 == -1) {
              output(paramArrayOfChar, i, j - i);
              i1 = j;
            } else {
              i1 += i;
              if (paramArrayOfChar[i1] == '\n') {
                output(paramArrayOfChar, i, i1++ - i);
                bool2 = true;
              } else {
                output(paramArrayOfChar, i, ++i1 - i);
                bool1 = true;
              } 
            } 
            i = i1;
          } 
        } 
        if (bool2 || bool1 || i < j) {
          writeLineSeparator();
          if (i < j || !bool2)
            indent(); 
        } 
      } 
    } 
  }
  
  protected void writeAttributes(AttributeSet paramAttributeSet) throws IOException {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      write(" " + object + "=" + paramAttributeSet.getAttribute(object));
    } 
  }
  
  protected void output(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    getWriter().write(paramArrayOfChar, paramInt1, paramInt2);
    setCurrentLineLength(getCurrentLineLength() + paramInt2);
  }
  
  private int indexOf(char[] paramArrayOfChar, char paramChar, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      if (paramArrayOfChar[paramInt1] == paramChar)
        return paramInt1; 
      paramInt1++;
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\AbstractWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */