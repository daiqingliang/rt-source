package javax.swing.text.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import javax.swing.text.AbstractWriter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class HTMLWriter extends AbstractWriter {
  private Stack<Element> blockElementStack = new Stack();
  
  private boolean inContent = false;
  
  private boolean inPre = false;
  
  private int preEndOffset;
  
  private boolean inTextArea = false;
  
  private boolean newlineOutputed = false;
  
  private boolean completeDoc;
  
  private Vector<HTML.Tag> tags = new Vector(10);
  
  private Vector<Object> tagValues = new Vector(10);
  
  private Segment segment;
  
  private Vector<HTML.Tag> tagsToRemove = new Vector(10);
  
  private boolean wroteHead;
  
  private boolean replaceEntities;
  
  private char[] tempChars;
  
  private boolean indentNext = false;
  
  private boolean writeCSS = false;
  
  private MutableAttributeSet convAttr = new SimpleAttributeSet();
  
  private MutableAttributeSet oConvAttr = new SimpleAttributeSet();
  
  private boolean indented = false;
  
  public HTMLWriter(Writer paramWriter, HTMLDocument paramHTMLDocument) { this(paramWriter, paramHTMLDocument, 0, paramHTMLDocument.getLength()); }
  
  public HTMLWriter(Writer paramWriter, HTMLDocument paramHTMLDocument, int paramInt1, int paramInt2) {
    super(paramWriter, paramHTMLDocument, paramInt1, paramInt2);
    this.completeDoc = (paramInt1 == 0 && paramInt2 == paramHTMLDocument.getLength());
    setLineLength(80);
  }
  
  public void write() throws IOException, BadLocationException {
    ElementIterator elementIterator = getElementIterator();
    Element element1 = null;
    this.wroteHead = false;
    setCurrentLineLength(0);
    this.replaceEntities = false;
    setCanWrapLines(false);
    if (this.segment == null)
      this.segment = new Segment(); 
    this.inPre = false;
    boolean bool = false;
    Element element2;
    while ((element2 = elementIterator.next()) != null) {
      if (!inRange(element2))
        if (this.completeDoc && element2.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
          bool = true;
        } else {
          continue;
        }  
      if (element1 != null)
        if (indentNeedsIncrementing(element1, element2)) {
          incrIndent();
        } else if (element1.getParentElement() != element2.getParentElement()) {
          for (Element element = (Element)this.blockElementStack.peek(); element != element2.getParentElement(); element = (Element)this.blockElementStack.peek()) {
            this.blockElementStack.pop();
            if (!synthesizedElement(element)) {
              AttributeSet attributeSet = element.getAttributes();
              if (!matchNameAttribute(attributeSet, HTML.Tag.PRE) && !isFormElementWithContent(attributeSet))
                decrIndent(); 
              endTag(element);
            } 
          } 
        } else if (element1.getParentElement() == element2.getParentElement()) {
          Element element = (Element)this.blockElementStack.peek();
          if (element == element1) {
            this.blockElementStack.pop();
            endTag(element);
          } 
        }  
      if (!element2.isLeaf() || isFormElementWithContent(element2.getAttributes())) {
        this.blockElementStack.push(element2);
        startTag(element2);
      } else {
        emptyTag(element2);
      } 
      element1 = element2;
    } 
    closeOutUnwantedEmbeddedTags(null);
    if (bool) {
      this.blockElementStack.pop();
      endTag(element1);
    } 
    while (!this.blockElementStack.empty()) {
      element1 = (Element)this.blockElementStack.pop();
      if (!synthesizedElement(element1)) {
        AttributeSet attributeSet = element1.getAttributes();
        if (!matchNameAttribute(attributeSet, HTML.Tag.PRE) && !isFormElementWithContent(attributeSet))
          decrIndent(); 
        endTag(element1);
      } 
    } 
    if (this.completeDoc)
      writeAdditionalComments(); 
    this.segment.array = null;
  }
  
  protected void writeAttributes(AttributeSet paramAttributeSet) throws IOException {
    this.convAttr.removeAttributes(this.convAttr);
    convertToHTML32(paramAttributeSet, this.convAttr);
    Enumeration enumeration = this.convAttr.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof HTML.Tag || object instanceof StyleConstants || object == HTML.Attribute.ENDTAG)
        continue; 
      write(" " + object + "=\"" + this.convAttr.getAttribute(object) + "\"");
    } 
  }
  
  protected void emptyTag(Element paramElement) throws BadLocationException, IOException {
    if (!this.inContent && !this.inPre)
      indentSmart(); 
    AttributeSet attributeSet = paramElement.getAttributes();
    closeOutUnwantedEmbeddedTags(attributeSet);
    writeEmbeddedTags(attributeSet);
    if (matchNameAttribute(attributeSet, HTML.Tag.CONTENT)) {
      this.inContent = true;
      text(paramElement);
    } else if (matchNameAttribute(attributeSet, HTML.Tag.COMMENT)) {
      comment(paramElement);
    } else {
      boolean bool = isBlockTag(paramElement.getAttributes());
      if (this.inContent && bool) {
        writeLineSeparator();
        indentSmart();
      } 
      Object object1 = (attributeSet != null) ? attributeSet.getAttribute(StyleConstants.NameAttribute) : null;
      Object object2 = (attributeSet != null) ? attributeSet.getAttribute(HTML.Attribute.ENDTAG) : null;
      boolean bool1 = false;
      if (object1 != null && object2 != null && object2 instanceof String && object2.equals("true"))
        bool1 = true; 
      if (this.completeDoc && matchNameAttribute(attributeSet, HTML.Tag.HEAD)) {
        if (bool1)
          writeStyles(((HTMLDocument)getDocument()).getStyleSheet()); 
        this.wroteHead = true;
      } 
      write('<');
      if (bool1)
        write('/'); 
      write(paramElement.getName());
      writeAttributes(attributeSet);
      write('>');
      if (matchNameAttribute(attributeSet, HTML.Tag.TITLE) && !bool1) {
        Document document = paramElement.getDocument();
        String str = (String)document.getProperty("title");
        write(str);
      } else if (!this.inContent || bool) {
        writeLineSeparator();
        if (bool && this.inContent)
          indentSmart(); 
      } 
    } 
  }
  
  protected boolean isBlockTag(AttributeSet paramAttributeSet) {
    Object object = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.Tag) {
      HTML.Tag tag = (HTML.Tag)object;
      return tag.isBlock();
    } 
    return false;
  }
  
  protected void startTag(Element paramElement) throws BadLocationException, IOException {
    Object object1;
    if (synthesizedElement(paramElement))
      return; 
    AttributeSet attributeSet = paramElement.getAttributes();
    Object object = attributeSet.getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.Tag) {
      object1 = (HTML.Tag)object;
    } else {
      object1 = null;
    } 
    if (object1 == HTML.Tag.PRE) {
      this.inPre = true;
      this.preEndOffset = paramElement.getEndOffset();
    } 
    closeOutUnwantedEmbeddedTags(attributeSet);
    if (this.inContent) {
      writeLineSeparator();
      this.inContent = false;
      this.newlineOutputed = false;
    } 
    if (this.completeDoc && object1 == HTML.Tag.BODY && !this.wroteHead) {
      this.wroteHead = true;
      indentSmart();
      write("<head>");
      writeLineSeparator();
      incrIndent();
      writeStyles(((HTMLDocument)getDocument()).getStyleSheet());
      decrIndent();
      writeLineSeparator();
      indentSmart();
      write("</head>");
      writeLineSeparator();
    } 
    indentSmart();
    write('<');
    write(paramElement.getName());
    writeAttributes(attributeSet);
    write('>');
    if (object1 != HTML.Tag.PRE)
      writeLineSeparator(); 
    if (object1 == HTML.Tag.TEXTAREA) {
      textAreaContent(paramElement.getAttributes());
    } else if (object1 == HTML.Tag.SELECT) {
      selectContent(paramElement.getAttributes());
    } else if (this.completeDoc && object1 == HTML.Tag.BODY) {
      writeMaps(((HTMLDocument)getDocument()).getMaps());
    } else if (object1 == HTML.Tag.HEAD) {
      HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
      this.wroteHead = true;
      incrIndent();
      writeStyles(hTMLDocument.getStyleSheet());
      if (hTMLDocument.hasBaseTag()) {
        indentSmart();
        write("<base href=\"" + hTMLDocument.getBase() + "\">");
        writeLineSeparator();
      } 
      decrIndent();
    } 
  }
  
  protected void textAreaContent(AttributeSet paramAttributeSet) throws IOException {
    Document document = (Document)paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    if (document != null && document.getLength() > 0) {
      if (this.segment == null)
        this.segment = new Segment(); 
      document.getText(0, document.getLength(), this.segment);
      if (this.segment.count > 0) {
        this.inTextArea = true;
        incrIndent();
        indentSmart();
        setCanWrapLines(true);
        this.replaceEntities = true;
        write(this.segment.array, this.segment.offset, this.segment.count);
        this.replaceEntities = false;
        setCanWrapLines(false);
        writeLineSeparator();
        this.inTextArea = false;
        decrIndent();
      } 
    } 
  }
  
  protected void text(Element paramElement) throws BadLocationException, IOException {
    int i = Math.max(getStartOffset(), paramElement.getStartOffset());
    int j = Math.min(getEndOffset(), paramElement.getEndOffset());
    if (i < j) {
      if (this.segment == null)
        this.segment = new Segment(); 
      getDocument().getText(i, j - i, this.segment);
      this.newlineOutputed = false;
      if (this.segment.count > 0) {
        if (this.segment.array[this.segment.offset + this.segment.count - 1] == '\n')
          this.newlineOutputed = true; 
        if (this.inPre && j == this.preEndOffset)
          if (this.segment.count > 1) {
            this.segment.count--;
          } else {
            return;
          }  
        this.replaceEntities = true;
        setCanWrapLines(!this.inPre);
        write(this.segment.array, this.segment.offset, this.segment.count);
        setCanWrapLines(false);
        this.replaceEntities = false;
      } 
    } 
  }
  
  protected void selectContent(AttributeSet paramAttributeSet) throws IOException {
    Object object = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    incrIndent();
    if (object instanceof OptionListModel) {
      OptionListModel optionListModel = (OptionListModel)object;
      int i = optionListModel.getSize();
      for (byte b = 0; b < i; b++) {
        Option option = (Option)optionListModel.getElementAt(b);
        writeOption(option);
      } 
    } else if (object instanceof OptionComboBoxModel) {
      OptionComboBoxModel optionComboBoxModel = (OptionComboBoxModel)object;
      int i = optionComboBoxModel.getSize();
      for (byte b = 0; b < i; b++) {
        Option option = (Option)optionComboBoxModel.getElementAt(b);
        writeOption(option);
      } 
    } 
    decrIndent();
  }
  
  protected void writeOption(Option paramOption) throws IOException {
    indentSmart();
    write('<');
    write("option");
    Object object = paramOption.getAttributes().getAttribute(HTML.Attribute.VALUE);
    if (object != null)
      write(" value=" + object); 
    if (paramOption.isSelected())
      write(" selected"); 
    write('>');
    if (paramOption.getLabel() != null)
      write(paramOption.getLabel()); 
    writeLineSeparator();
  }
  
  protected void endTag(Element paramElement) throws BadLocationException, IOException {
    if (synthesizedElement(paramElement))
      return; 
    closeOutUnwantedEmbeddedTags(paramElement.getAttributes());
    if (this.inContent) {
      if (!this.newlineOutputed && !this.inPre)
        writeLineSeparator(); 
      this.newlineOutputed = false;
      this.inContent = false;
    } 
    if (!this.inPre)
      indentSmart(); 
    if (matchNameAttribute(paramElement.getAttributes(), HTML.Tag.PRE))
      this.inPre = false; 
    write('<');
    write('/');
    write(paramElement.getName());
    write('>');
    writeLineSeparator();
  }
  
  protected void comment(Element paramElement) throws BadLocationException, IOException {
    AttributeSet attributeSet = paramElement.getAttributes();
    if (matchNameAttribute(attributeSet, HTML.Tag.COMMENT)) {
      Object object = attributeSet.getAttribute(HTML.Attribute.COMMENT);
      if (object instanceof String) {
        writeComment((String)object);
      } else {
        writeComment(null);
      } 
    } 
  }
  
  void writeComment(String paramString) throws IOException {
    write("<!--");
    if (paramString != null)
      write(paramString); 
    write("-->");
    writeLineSeparator();
    indentSmart();
  }
  
  void writeAdditionalComments() throws IOException, BadLocationException {
    Object object = getDocument().getProperty("AdditionalComments");
    if (object instanceof Vector) {
      Vector vector = (Vector)object;
      byte b = 0;
      int i = vector.size();
      while (b < i) {
        writeComment(vector.elementAt(b).toString());
        b++;
      } 
    } 
  }
  
  protected boolean synthesizedElement(Element paramElement) { return matchNameAttribute(paramElement.getAttributes(), HTML.Tag.IMPLIED); }
  
  protected boolean matchNameAttribute(AttributeSet paramAttributeSet, HTML.Tag paramTag) {
    Object object = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.Tag) {
      HTML.Tag tag = (HTML.Tag)object;
      if (tag == paramTag)
        return true; 
    } 
    return false;
  }
  
  protected void writeEmbeddedTags(AttributeSet paramAttributeSet) throws IOException {
    paramAttributeSet = convertToHTML(paramAttributeSet, this.oConvAttr);
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof HTML.Tag) {
        HTML.Tag tag = (HTML.Tag)object;
        if (tag == HTML.Tag.FORM || this.tags.contains(tag))
          continue; 
        write('<');
        write(tag.toString());
        Object object1 = paramAttributeSet.getAttribute(tag);
        if (object1 != null && object1 instanceof AttributeSet)
          writeAttributes((AttributeSet)object1); 
        write('>');
        this.tags.addElement(tag);
        this.tagValues.addElement(object1);
      } 
    } 
  }
  
  private boolean noMatchForTagInAttributes(AttributeSet paramAttributeSet, HTML.Tag paramTag, Object paramObject) {
    if (paramAttributeSet != null && paramAttributeSet.isDefined(paramTag)) {
      Object object = paramAttributeSet.getAttribute(paramTag);
      if ((paramObject == null) ? (object == null) : (object != null && paramObject.equals(object)))
        return false; 
    } 
    return true;
  }
  
  protected void closeOutUnwantedEmbeddedTags(AttributeSet paramAttributeSet) throws IOException {
    this.tagsToRemove.removeAllElements();
    paramAttributeSet = convertToHTML(paramAttributeSet, null);
    int i = -1;
    int j = this.tags.size();
    int k;
    for (k = j - 1; k >= 0; k--) {
      HTML.Tag tag = (HTML.Tag)this.tags.elementAt(k);
      Object object = this.tagValues.elementAt(k);
      if (paramAttributeSet == null || noMatchForTagInAttributes(paramAttributeSet, tag, object)) {
        i = k;
        this.tagsToRemove.addElement(tag);
      } 
    } 
    if (i != -1) {
      k = (j - i == this.tagsToRemove.size()) ? 1 : 0;
      int m;
      for (m = j - 1; m >= i; m--) {
        HTML.Tag tag = (HTML.Tag)this.tags.elementAt(m);
        if (k != 0 || this.tagsToRemove.contains(tag)) {
          this.tags.removeElementAt(m);
          this.tagValues.removeElementAt(m);
        } 
        write('<');
        write('/');
        write(tag.toString());
        write('>');
      } 
      j = this.tags.size();
      for (m = i; m < j; m++) {
        HTML.Tag tag = (HTML.Tag)this.tags.elementAt(m);
        write('<');
        write(tag.toString());
        Object object = this.tagValues.elementAt(m);
        if (object != null && object instanceof AttributeSet)
          writeAttributes((AttributeSet)object); 
        write('>');
      } 
    } 
  }
  
  private boolean isFormElementWithContent(AttributeSet paramAttributeSet) { return (matchNameAttribute(paramAttributeSet, HTML.Tag.TEXTAREA) || matchNameAttribute(paramAttributeSet, HTML.Tag.SELECT)); }
  
  private boolean indentNeedsIncrementing(Element paramElement1, Element paramElement2) {
    if (paramElement2.getParentElement() == paramElement1 && !this.inPre) {
      if (this.indentNext) {
        this.indentNext = false;
        return true;
      } 
      if (synthesizedElement(paramElement2)) {
        this.indentNext = true;
      } else if (!synthesizedElement(paramElement1)) {
        return true;
      } 
    } 
    return false;
  }
  
  void writeMaps(Enumeration paramEnumeration) throws IOException {
    if (paramEnumeration != null)
      while (paramEnumeration.hasMoreElements()) {
        Map map = (Map)paramEnumeration.nextElement();
        String str = map.getName();
        incrIndent();
        indentSmart();
        write("<map");
        if (str != null) {
          write(" name=\"");
          write(str);
          write("\">");
        } else {
          write('>');
        } 
        writeLineSeparator();
        incrIndent();
        AttributeSet[] arrayOfAttributeSet = map.getAreas();
        if (arrayOfAttributeSet != null) {
          byte b = 0;
          int i = arrayOfAttributeSet.length;
          while (b < i) {
            indentSmart();
            write("<area");
            writeAttributes(arrayOfAttributeSet[b]);
            write("></area>");
            writeLineSeparator();
            b++;
          } 
        } 
        decrIndent();
        indentSmart();
        write("</map>");
        writeLineSeparator();
        decrIndent();
      }  
  }
  
  void writeStyles(StyleSheet paramStyleSheet) throws IOException {
    if (paramStyleSheet != null) {
      Enumeration enumeration = paramStyleSheet.getStyleNames();
      if (enumeration != null) {
        boolean bool = false;
        while (enumeration.hasMoreElements()) {
          String str = (String)enumeration.nextElement();
          if (!"default".equals(str) && writeStyle(str, paramStyleSheet.getStyle(str), bool))
            bool = true; 
        } 
        if (bool)
          writeStyleEndTag(); 
      } 
    } 
  }
  
  boolean writeStyle(String paramString, Style paramStyle, boolean paramBoolean) throws IOException {
    boolean bool = false;
    Enumeration enumeration = paramStyle.getAttributeNames();
    if (enumeration != null)
      while (enumeration.hasMoreElements()) {
        Object object = enumeration.nextElement();
        if (object instanceof CSS.Attribute) {
          String str = paramStyle.getAttribute(object).toString();
          if (str != null) {
            if (!paramBoolean) {
              writeStyleStartTag();
              paramBoolean = true;
            } 
            if (!bool) {
              bool = true;
              indentSmart();
              write(paramString);
              write(" {");
            } else {
              write(";");
            } 
            write(' ');
            write(object.toString());
            write(": ");
            write(str);
          } 
        } 
      }  
    if (bool) {
      write(" }");
      writeLineSeparator();
    } 
    return bool;
  }
  
  void writeStyleStartTag() throws IOException, BadLocationException {
    indentSmart();
    write("<style type=\"text/css\">");
    incrIndent();
    writeLineSeparator();
    indentSmart();
    write("<!--");
    incrIndent();
    writeLineSeparator();
  }
  
  void writeStyleEndTag() throws IOException, BadLocationException {
    decrIndent();
    indentSmart();
    write("-->");
    writeLineSeparator();
    decrIndent();
    indentSmart();
    write("</style>");
    writeLineSeparator();
    indentSmart();
  }
  
  AttributeSet convertToHTML(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet) {
    if (paramMutableAttributeSet == null)
      paramMutableAttributeSet = this.convAttr; 
    paramMutableAttributeSet.removeAttributes(paramMutableAttributeSet);
    if (this.writeCSS) {
      convertToHTML40(paramAttributeSet, paramMutableAttributeSet);
    } else {
      convertToHTML32(paramAttributeSet, paramMutableAttributeSet);
    } 
    return paramMutableAttributeSet;
  }
  
  private static void convertToHTML32(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet) {
    if (paramAttributeSet == null)
      return; 
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    String str = "";
    while (enumeration.hasMoreElements()) {
      Object object1 = enumeration.nextElement();
      if (object1 instanceof CSS.Attribute) {
        if (object1 == CSS.Attribute.FONT_FAMILY || object1 == CSS.Attribute.FONT_SIZE || object1 == CSS.Attribute.COLOR) {
          createFontAttribute((CSS.Attribute)object1, paramAttributeSet, paramMutableAttributeSet);
          continue;
        } 
        if (object1 == CSS.Attribute.FONT_WEIGHT) {
          CSS.FontWeight fontWeight = (CSS.FontWeight)paramAttributeSet.getAttribute(CSS.Attribute.FONT_WEIGHT);
          if (fontWeight != null && fontWeight.getValue() > 400)
            addAttribute(paramMutableAttributeSet, HTML.Tag.B, SimpleAttributeSet.EMPTY); 
          continue;
        } 
        if (object1 == CSS.Attribute.FONT_STYLE) {
          String str1 = paramAttributeSet.getAttribute(object1).toString();
          if (str1.indexOf("italic") >= 0)
            addAttribute(paramMutableAttributeSet, HTML.Tag.I, SimpleAttributeSet.EMPTY); 
          continue;
        } 
        if (object1 == CSS.Attribute.TEXT_DECORATION) {
          String str1 = paramAttributeSet.getAttribute(object1).toString();
          if (str1.indexOf("underline") >= 0)
            addAttribute(paramMutableAttributeSet, HTML.Tag.U, SimpleAttributeSet.EMPTY); 
          if (str1.indexOf("line-through") >= 0)
            addAttribute(paramMutableAttributeSet, HTML.Tag.STRIKE, SimpleAttributeSet.EMPTY); 
          continue;
        } 
        if (object1 == CSS.Attribute.VERTICAL_ALIGN) {
          String str1 = paramAttributeSet.getAttribute(object1).toString();
          if (str1.indexOf("sup") >= 0)
            addAttribute(paramMutableAttributeSet, HTML.Tag.SUP, SimpleAttributeSet.EMPTY); 
          if (str1.indexOf("sub") >= 0)
            addAttribute(paramMutableAttributeSet, HTML.Tag.SUB, SimpleAttributeSet.EMPTY); 
          continue;
        } 
        if (object1 == CSS.Attribute.TEXT_ALIGN) {
          addAttribute(paramMutableAttributeSet, HTML.Attribute.ALIGN, paramAttributeSet.getAttribute(object1).toString());
          continue;
        } 
        if (str.length() > 0)
          str = str + "; "; 
        str = str + object1 + ": " + paramAttributeSet.getAttribute(object1);
        continue;
      } 
      Object object2 = paramAttributeSet.getAttribute(object1);
      if (object2 instanceof AttributeSet)
        object2 = ((AttributeSet)object2).copyAttributes(); 
      addAttribute(paramMutableAttributeSet, object1, object2);
    } 
    if (str.length() > 0)
      paramMutableAttributeSet.addAttribute(HTML.Attribute.STYLE, str); 
  }
  
  private static void addAttribute(MutableAttributeSet paramMutableAttributeSet, Object paramObject1, Object paramObject2) {
    Object object = paramMutableAttributeSet.getAttribute(paramObject1);
    if (object == null || object == SimpleAttributeSet.EMPTY) {
      paramMutableAttributeSet.addAttribute(paramObject1, paramObject2);
    } else if (object instanceof MutableAttributeSet && paramObject2 instanceof AttributeSet) {
      ((MutableAttributeSet)object).addAttributes((AttributeSet)paramObject2);
    } 
  }
  
  private static void createFontAttribute(CSS.Attribute paramAttribute, AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet) {
    MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)paramMutableAttributeSet.getAttribute(HTML.Tag.FONT);
    if (mutableAttributeSet == null) {
      mutableAttributeSet = new SimpleAttributeSet();
      paramMutableAttributeSet.addAttribute(HTML.Tag.FONT, mutableAttributeSet);
    } 
    String str = paramAttributeSet.getAttribute(paramAttribute).toString();
    if (paramAttribute == CSS.Attribute.FONT_FAMILY) {
      mutableAttributeSet.addAttribute(HTML.Attribute.FACE, str);
    } else if (paramAttribute == CSS.Attribute.FONT_SIZE) {
      mutableAttributeSet.addAttribute(HTML.Attribute.SIZE, str);
    } else if (paramAttribute == CSS.Attribute.COLOR) {
      mutableAttributeSet.addAttribute(HTML.Attribute.COLOR, str);
    } 
  }
  
  private static void convertToHTML40(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet) {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    String str = "";
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof CSS.Attribute) {
        str = str + " " + object + "=" + paramAttributeSet.getAttribute(object) + ";";
        continue;
      } 
      paramMutableAttributeSet.addAttribute(object, paramAttributeSet.getAttribute(object));
    } 
    if (str.length() > 0)
      paramMutableAttributeSet.addAttribute(HTML.Attribute.STYLE, str); 
  }
  
  protected void writeLineSeparator() throws IOException, BadLocationException {
    boolean bool = this.replaceEntities;
    this.replaceEntities = false;
    super.writeLineSeparator();
    this.replaceEntities = bool;
    this.indented = false;
  }
  
  protected void output(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    if (!this.replaceEntities) {
      super.output(paramArrayOfChar, paramInt1, paramInt2);
      return;
    } 
    int i = paramInt1;
    paramInt2 += paramInt1;
    for (int j = paramInt1; j < paramInt2; j++) {
      switch (paramArrayOfChar[j]) {
        case '<':
          if (j > i)
            super.output(paramArrayOfChar, i, j - i); 
          i = j + 1;
          output("&lt;");
          break;
        case '>':
          if (j > i)
            super.output(paramArrayOfChar, i, j - i); 
          i = j + 1;
          output("&gt;");
          break;
        case '&':
          if (j > i)
            super.output(paramArrayOfChar, i, j - i); 
          i = j + 1;
          output("&amp;");
          break;
        case '"':
          if (j > i)
            super.output(paramArrayOfChar, i, j - i); 
          i = j + 1;
          output("&quot;");
          break;
        case '\t':
        case '\n':
        case '\r':
          break;
        default:
          if (paramArrayOfChar[j] < ' ' || paramArrayOfChar[j] > '') {
            if (j > i)
              super.output(paramArrayOfChar, i, j - i); 
            i = j + 1;
            output("&#");
            output(String.valueOf(paramArrayOfChar[j]));
            output(";");
          } 
          break;
      } 
    } 
    if (i < paramInt2)
      super.output(paramArrayOfChar, i, paramInt2 - i); 
  }
  
  private void output(String paramString) throws IOException {
    int i = paramString.length();
    if (this.tempChars == null || this.tempChars.length < i)
      this.tempChars = new char[i]; 
    paramString.getChars(0, i, this.tempChars, 0);
    super.output(this.tempChars, 0, i);
  }
  
  private void indentSmart() throws IOException, BadLocationException {
    if (!this.indented) {
      indent();
      this.indented = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\HTMLWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */