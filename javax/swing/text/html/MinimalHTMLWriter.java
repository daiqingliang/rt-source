package javax.swing.text.html;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class MinimalHTMLWriter extends AbstractWriter {
  private static final int BOLD = 1;
  
  private static final int ITALIC = 2;
  
  private static final int UNDERLINE = 4;
  
  private static final CSS css = new CSS();
  
  private int fontMask = 0;
  
  int startOffset = 0;
  
  int endOffset = 0;
  
  private AttributeSet fontAttributes;
  
  private Hashtable<String, String> styleNameMapping;
  
  public MinimalHTMLWriter(Writer paramWriter, StyledDocument paramStyledDocument) { super(paramWriter, paramStyledDocument); }
  
  public MinimalHTMLWriter(Writer paramWriter, StyledDocument paramStyledDocument, int paramInt1, int paramInt2) { super(paramWriter, paramStyledDocument, paramInt1, paramInt2); }
  
  public void write() throws IOException, BadLocationException {
    this.styleNameMapping = new Hashtable();
    writeStartTag("<html>");
    writeHeader();
    writeBody();
    writeEndTag("</html>");
  }
  
  protected void writeAttributes(AttributeSet paramAttributeSet) throws IOException {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof StyleConstants.ParagraphConstants || object instanceof StyleConstants.CharacterConstants || object instanceof StyleConstants.FontConstants || object instanceof StyleConstants.ColorConstants) {
        indent();
        write(object.toString());
        write(':');
        write(css.styleConstantsValueToCSSValue((StyleConstants)object, paramAttributeSet.getAttribute(object)).toString());
        write(';');
        write('\n');
      } 
    } 
  }
  
  protected void text(Element paramElement) throws IOException, BadLocationException {
    String str = getText(paramElement);
    if (str.length() > 0 && str.charAt(str.length() - 1) == '\n')
      str = str.substring(0, str.length() - 1); 
    if (str.length() > 0)
      write(str); 
  }
  
  protected void writeStartTag(String paramString) throws IOException {
    indent();
    write(paramString);
    write('\n');
    incrIndent();
  }
  
  protected void writeEndTag(String paramString) throws IOException {
    decrIndent();
    indent();
    write(paramString);
    write('\n');
  }
  
  protected void writeHeader() throws IOException, BadLocationException {
    writeStartTag("<head>");
    writeStartTag("<style>");
    writeStartTag("<!--");
    writeStyles();
    writeEndTag("-->");
    writeEndTag("</style>");
    writeEndTag("</head>");
  }
  
  protected void writeStyles() throws IOException, BadLocationException {
    DefaultStyledDocument defaultStyledDocument = (DefaultStyledDocument)getDocument();
    Enumeration enumeration = defaultStyledDocument.getStyleNames();
    while (enumeration.hasMoreElements()) {
      Style style = defaultStyledDocument.getStyle((String)enumeration.nextElement());
      if (style.getAttributeCount() == 1 && style.isDefined(StyleConstants.NameAttribute))
        continue; 
      indent();
      write("p." + addStyleName(style.getName()));
      write(" {\n");
      incrIndent();
      writeAttributes(style);
      decrIndent();
      indent();
      write("}\n");
    } 
  }
  
  protected void writeBody() throws IOException, BadLocationException {
    ElementIterator elementIterator = getElementIterator();
    elementIterator.current();
    writeStartTag("<body>");
    Element element;
    boolean bool;
    for (bool = false; (element = elementIterator.next()) != null; bool = true) {
      if (!inRange(element))
        continue; 
      if (element instanceof javax.swing.text.AbstractDocument.BranchElement) {
        if (bool) {
          writeEndParagraph();
          bool = false;
          this.fontMask = 0;
        } 
        writeStartParagraph(element);
        continue;
      } 
      if (isText(element)) {
        writeContent(element, !bool);
        bool = true;
        continue;
      } 
      writeLeaf(element);
    } 
    if (bool)
      writeEndParagraph(); 
    writeEndTag("</body>");
  }
  
  protected void writeEndParagraph() throws IOException, BadLocationException {
    writeEndMask(this.fontMask);
    if (inFontTag()) {
      endSpanTag();
    } else {
      write('\n');
    } 
    writeEndTag("</p>");
  }
  
  protected void writeStartParagraph(Element paramElement) throws IOException, BadLocationException {
    AttributeSet attributeSet = paramElement.getAttributes();
    Object object = attributeSet.getAttribute(StyleConstants.ResolveAttribute);
    if (object instanceof StyleContext.NamedStyle) {
      writeStartTag("<p class=" + mapStyleName(((StyleContext.NamedStyle)object).getName()) + ">");
    } else {
      writeStartTag("<p>");
    } 
  }
  
  protected void writeLeaf(Element paramElement) throws IOException, BadLocationException {
    indent();
    if (paramElement.getName() == "icon") {
      writeImage(paramElement);
    } else if (paramElement.getName() == "component") {
      writeComponent(paramElement);
    } 
  }
  
  protected void writeImage(Element paramElement) throws IOException, BadLocationException {}
  
  protected void writeComponent(Element paramElement) throws IOException, BadLocationException {}
  
  protected boolean isText(Element paramElement) { return (paramElement.getName() == "content"); }
  
  protected void writeContent(Element paramElement, boolean paramBoolean) throws IOException, BadLocationException {
    AttributeSet attributeSet = paramElement.getAttributes();
    writeNonHTMLAttributes(attributeSet);
    if (paramBoolean)
      indent(); 
    writeHTMLTags(attributeSet);
    text(paramElement);
  }
  
  protected void writeHTMLTags(AttributeSet paramAttributeSet) throws IOException {
    int i = this.fontMask;
    setFontMask(paramAttributeSet);
    byte b1 = 0;
    byte b2 = 0;
    if ((i & true) != 0) {
      if ((this.fontMask & true) == 0)
        b1 |= true; 
    } else if ((this.fontMask & true) != 0) {
      b2 |= true;
    } 
    if ((i & 0x2) != 0) {
      if ((this.fontMask & 0x2) == 0)
        b1 |= 0x2; 
    } else if ((this.fontMask & 0x2) != 0) {
      b2 |= 0x2;
    } 
    if ((i & 0x4) != 0) {
      if ((this.fontMask & 0x4) == 0)
        b1 |= 0x4; 
    } else if ((this.fontMask & 0x4) != 0) {
      b2 |= 0x4;
    } 
    writeEndMask(b1);
    writeStartMask(b2);
  }
  
  private void setFontMask(AttributeSet paramAttributeSet) throws IOException {
    if (StyleConstants.isBold(paramAttributeSet))
      this.fontMask |= 0x1; 
    if (StyleConstants.isItalic(paramAttributeSet))
      this.fontMask |= 0x2; 
    if (StyleConstants.isUnderline(paramAttributeSet))
      this.fontMask |= 0x4; 
  }
  
  private void writeStartMask(int paramInt) throws IOException {
    if (paramInt != 0) {
      if ((paramInt & 0x4) != 0)
        write("<u>"); 
      if ((paramInt & 0x2) != 0)
        write("<i>"); 
      if ((paramInt & true) != 0)
        write("<b>"); 
    } 
  }
  
  private void writeEndMask(int paramInt) throws IOException {
    if (paramInt != 0) {
      if ((paramInt & true) != 0)
        write("</b>"); 
      if ((paramInt & 0x2) != 0)
        write("</i>"); 
      if ((paramInt & 0x4) != 0)
        write("</u>"); 
    } 
  }
  
  protected void writeNonHTMLAttributes(AttributeSet paramAttributeSet) throws IOException {
    String str1 = "";
    String str2 = "; ";
    if (inFontTag() && this.fontAttributes.isEqual(paramAttributeSet))
      return; 
    boolean bool = true;
    Color color = (Color)paramAttributeSet.getAttribute(StyleConstants.Foreground);
    if (color != null) {
      str1 = str1 + "color: " + css.styleConstantsValueToCSSValue((StyleConstants)StyleConstants.Foreground, color);
      bool = false;
    } 
    Integer integer = (Integer)paramAttributeSet.getAttribute(StyleConstants.FontSize);
    if (integer != null) {
      if (!bool)
        str1 = str1 + str2; 
      str1 = str1 + "font-size: " + integer.intValue() + "pt";
      bool = false;
    } 
    String str3 = (String)paramAttributeSet.getAttribute(StyleConstants.FontFamily);
    if (str3 != null) {
      if (!bool)
        str1 = str1 + str2; 
      str1 = str1 + "font-family: " + str3;
      bool = false;
    } 
    if (str1.length() > 0) {
      if (this.fontMask != 0) {
        writeEndMask(this.fontMask);
        this.fontMask = 0;
      } 
      startSpanTag(str1);
      this.fontAttributes = paramAttributeSet;
    } else if (this.fontAttributes != null) {
      writeEndMask(this.fontMask);
      this.fontMask = 0;
      endSpanTag();
    } 
  }
  
  protected boolean inFontTag() { return (this.fontAttributes != null); }
  
  protected void endFontTag() throws IOException, BadLocationException {
    write('\n');
    writeEndTag("</font>");
    this.fontAttributes = null;
  }
  
  protected void startFontTag(String paramString) throws IOException {
    boolean bool = false;
    if (inFontTag()) {
      endFontTag();
      bool = true;
    } 
    writeStartTag("<font style=\"" + paramString + "\">");
    if (bool)
      indent(); 
  }
  
  private void startSpanTag(String paramString) throws IOException {
    boolean bool = false;
    if (inFontTag()) {
      endSpanTag();
      bool = true;
    } 
    writeStartTag("<span style=\"" + paramString + "\">");
    if (bool)
      indent(); 
  }
  
  private void endSpanTag() throws IOException, BadLocationException {
    write('\n');
    writeEndTag("</span>");
    this.fontAttributes = null;
  }
  
  private String addStyleName(String paramString) {
    if (this.styleNameMapping == null)
      return paramString; 
    StringBuilder stringBuilder = null;
    for (int i = paramString.length() - 1; i >= 0; i--) {
      if (!isValidCharacter(paramString.charAt(i))) {
        if (stringBuilder == null)
          stringBuilder = new StringBuilder(paramString); 
        stringBuilder.setCharAt(i, 'a');
      } 
    } 
    String str;
    for (str = (stringBuilder != null) ? stringBuilder.toString() : paramString; this.styleNameMapping.get(str) != null; str = str + 'x');
    this.styleNameMapping.put(paramString, str);
    return str;
  }
  
  private String mapStyleName(String paramString) {
    if (this.styleNameMapping == null)
      return paramString; 
    String str = (String)this.styleNameMapping.get(paramString);
    return (str == null) ? paramString : str;
  }
  
  private boolean isValidCharacter(char paramChar) { return ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z')); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\MinimalHTMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */