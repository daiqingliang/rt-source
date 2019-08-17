package javax.swing.text.html;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.GapContent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoableEdit;
import sun.swing.SwingUtilities2;

public class HTMLDocument extends DefaultStyledDocument {
  private boolean frameDocument = false;
  
  private boolean preservesUnknownTags = true;
  
  private HashMap<String, ButtonGroup> radioButtonGroupsMap;
  
  static final String TokenThreshold = "token threshold";
  
  private static final int MaxThreshold = 10000;
  
  private static final int StepThreshold = 5;
  
  public static final String AdditionalComments = "AdditionalComments";
  
  static final String StyleType = "StyleType";
  
  URL base;
  
  boolean hasBaseTag = false;
  
  private String baseTarget = null;
  
  private HTMLEditorKit.Parser parser;
  
  private static AttributeSet contentAttributeSet;
  
  static String MAP_PROPERTY = "__MAP__";
  
  private static char[] NEWLINE;
  
  private boolean insertInBody = false;
  
  private static final String I18NProperty = "i18n";
  
  public HTMLDocument() { this(new GapContent(4096), new StyleSheet()); }
  
  public HTMLDocument(StyleSheet paramStyleSheet) { this(new GapContent(4096), paramStyleSheet); }
  
  public HTMLDocument(AbstractDocument.Content paramContent, StyleSheet paramStyleSheet) { super(paramContent, paramStyleSheet); }
  
  public HTMLEditorKit.ParserCallback getReader(int paramInt) {
    Object object = getProperty("stream");
    if (object instanceof URL)
      setBase((URL)object); 
    return new HTMLReader(paramInt);
  }
  
  public HTMLEditorKit.ParserCallback getReader(int paramInt1, int paramInt2, int paramInt3, HTML.Tag paramTag) { return getReader(paramInt1, paramInt2, paramInt3, paramTag, true); }
  
  HTMLEditorKit.ParserCallback getReader(int paramInt1, int paramInt2, int paramInt3, HTML.Tag paramTag, boolean paramBoolean) {
    Object object = getProperty("stream");
    if (object instanceof URL)
      setBase((URL)object); 
    return new HTMLReader(paramInt1, paramInt2, paramInt3, paramTag, paramBoolean, false, true);
  }
  
  public URL getBase() { return this.base; }
  
  public void setBase(URL paramURL) {
    this.base = paramURL;
    getStyleSheet().setBase(paramURL);
  }
  
  protected void insert(int paramInt, DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec) throws BadLocationException { super.insert(paramInt, paramArrayOfElementSpec); }
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet) {
    if (paramAttributeSet == null) {
      paramAttributeSet = contentAttributeSet;
    } else if (paramAttributeSet.isDefined(StyleConstants.ComposedTextAttribute)) {
      ((MutableAttributeSet)paramAttributeSet).addAttributes(contentAttributeSet);
    } 
    if (paramAttributeSet.isDefined("CR"))
      ((MutableAttributeSet)paramAttributeSet).removeAttribute("CR"); 
    super.insertUpdate(paramDefaultDocumentEvent, paramAttributeSet);
  }
  
  protected void create(DefaultStyledDocument.ElementSpec[] paramArrayOfElementSpec) { super.create(paramArrayOfElementSpec); }
  
  public void setParagraphAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean) {
    try {
      writeLock();
      int i = Math.min(paramInt1 + paramInt2, getLength());
      Element element = getParagraphElement(paramInt1);
      paramInt1 = element.getStartOffset();
      element = getParagraphElement(i);
      paramInt2 = Math.max(0, element.getEndOffset() - paramInt1);
      AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt1, paramInt2, DocumentEvent.EventType.CHANGE);
      AttributeSet attributeSet = paramAttributeSet.copyAttributes();
      int j = Integer.MAX_VALUE;
      int k;
      for (k = paramInt1; k <= i; k = j) {
        Element element1 = getParagraphElement(k);
        if (j == element1.getEndOffset()) {
          j++;
        } else {
          j = element1.getEndOffset();
        } 
        MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)element1.getAttributes();
        defaultDocumentEvent.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(element1, attributeSet, paramBoolean));
        if (paramBoolean)
          mutableAttributeSet.removeAttributes(mutableAttributeSet); 
        mutableAttributeSet.addAttributes(paramAttributeSet);
      } 
      defaultDocumentEvent.end();
      fireChangedUpdate(defaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
    } finally {
      writeUnlock();
    } 
  }
  
  public StyleSheet getStyleSheet() { return (StyleSheet)getAttributeContext(); }
  
  public Iterator getIterator(HTML.Tag paramTag) { return paramTag.isBlock() ? null : new LeafIterator(paramTag, this); }
  
  protected Element createLeafElement(Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) { return new RunElement(paramElement, paramAttributeSet, paramInt1, paramInt2); }
  
  protected Element createBranchElement(Element paramElement, AttributeSet paramAttributeSet) { return new BlockElement(paramElement, paramAttributeSet); }
  
  protected AbstractDocument.AbstractElement createDefaultRoot() {
    writeLock();
    SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
    simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HTML);
    BlockElement blockElement1 = new BlockElement(null, simpleAttributeSet.copyAttributes());
    simpleAttributeSet.removeAttributes(simpleAttributeSet);
    simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.BODY);
    BlockElement blockElement2 = new BlockElement(blockElement1, simpleAttributeSet.copyAttributes());
    simpleAttributeSet.removeAttributes(simpleAttributeSet);
    simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
    getStyleSheet().addCSSAttributeFromHTML(simpleAttributeSet, CSS.Attribute.MARGIN_TOP, "0");
    BlockElement blockElement3 = new BlockElement(blockElement2, simpleAttributeSet.copyAttributes());
    simpleAttributeSet.removeAttributes(simpleAttributeSet);
    simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
    RunElement runElement = new RunElement(blockElement3, simpleAttributeSet, 0, 1);
    Element[] arrayOfElement = new Element[1];
    arrayOfElement[0] = runElement;
    blockElement3.replace(0, 0, arrayOfElement);
    arrayOfElement[0] = blockElement3;
    blockElement2.replace(0, 0, arrayOfElement);
    arrayOfElement[0] = blockElement2;
    blockElement1.replace(0, 0, arrayOfElement);
    writeUnlock();
    return blockElement1;
  }
  
  public void setTokenThreshold(int paramInt) { putProperty("token threshold", new Integer(paramInt)); }
  
  public int getTokenThreshold() {
    Integer integer = (Integer)getProperty("token threshold");
    return (integer != null) ? integer.intValue() : Integer.MAX_VALUE;
  }
  
  public void setPreservesUnknownTags(boolean paramBoolean) { this.preservesUnknownTags = paramBoolean; }
  
  public boolean getPreservesUnknownTags() { return this.preservesUnknownTags; }
  
  public void processHTMLFrameHyperlinkEvent(HTMLFrameHyperlinkEvent paramHTMLFrameHyperlinkEvent) {
    String str1 = paramHTMLFrameHyperlinkEvent.getTarget();
    Element element = paramHTMLFrameHyperlinkEvent.getSourceElement();
    String str2 = paramHTMLFrameHyperlinkEvent.getURL().toString();
    if (str1.equals("_self")) {
      updateFrame(element, str2);
    } else if (str1.equals("_parent")) {
      updateFrameSet(element.getParentElement(), str2);
    } else {
      Element element1 = findFrame(str1);
      if (element1 != null)
        updateFrame(element1, str2); 
    } 
  }
  
  private Element findFrame(String paramString) {
    ElementIterator elementIterator = new ElementIterator(this);
    Element element;
    while ((element = elementIterator.next()) != null) {
      AttributeSet attributeSet = element.getAttributes();
      if (matchNameAttribute(attributeSet, HTML.Tag.FRAME)) {
        String str = (String)attributeSet.getAttribute(HTML.Attribute.NAME);
        if (str != null && str.equals(paramString))
          break; 
      } 
    } 
    return element;
  }
  
  static boolean matchNameAttribute(AttributeSet paramAttributeSet, HTML.Tag paramTag) {
    Object object = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.Tag) {
      HTML.Tag tag = (HTML.Tag)object;
      if (tag == paramTag)
        return true; 
    } 
    return false;
  }
  
  private void updateFrameSet(Element paramElement, String paramString) {
    try {
      int i = paramElement.getStartOffset();
      int j = Math.min(getLength(), paramElement.getEndOffset());
      String str = "<frame";
      if (paramString != null)
        str = str + " src=\"" + paramString + "\""; 
      str = str + ">";
      installParserIfNecessary();
      setOuterHTML(paramElement, str);
    } catch (BadLocationException badLocationException) {
    
    } catch (IOException iOException) {}
  }
  
  private void updateFrame(Element paramElement, String paramString) {
    try {
      writeLock();
      AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramElement.getStartOffset(), 1, DocumentEvent.EventType.CHANGE);
      AttributeSet attributeSet = paramElement.getAttributes().copyAttributes();
      MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)paramElement.getAttributes();
      defaultDocumentEvent.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(paramElement, attributeSet, false));
      mutableAttributeSet.removeAttribute(HTML.Attribute.SRC);
      mutableAttributeSet.addAttribute(HTML.Attribute.SRC, paramString);
      defaultDocumentEvent.end();
      fireChangedUpdate(defaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
    } finally {
      writeUnlock();
    } 
  }
  
  boolean isFrameDocument() { return this.frameDocument; }
  
  void setFrameDocumentState(boolean paramBoolean) { this.frameDocument = paramBoolean; }
  
  void addMap(Map paramMap) {
    String str = paramMap.getName();
    if (str != null) {
      Object object = getProperty(MAP_PROPERTY);
      if (object == null) {
        object = new Hashtable(11);
        putProperty(MAP_PROPERTY, object);
      } 
      if (object instanceof Hashtable)
        ((Hashtable)object).put("#" + str, paramMap); 
    } 
  }
  
  void removeMap(Map paramMap) {
    String str = paramMap.getName();
    if (str != null) {
      Object object = getProperty(MAP_PROPERTY);
      if (object instanceof Hashtable)
        ((Hashtable)object).remove("#" + str); 
    } 
  }
  
  Map getMap(String paramString) {
    if (paramString != null) {
      Object object = getProperty(MAP_PROPERTY);
      if (object != null && object instanceof Hashtable)
        return (Map)((Hashtable)object).get(paramString); 
    } 
    return null;
  }
  
  Enumeration getMaps() {
    Object object = getProperty(MAP_PROPERTY);
    return (object instanceof Hashtable) ? ((Hashtable)object).elements() : null;
  }
  
  void setDefaultStyleSheetType(String paramString) { putProperty("StyleType", paramString); }
  
  String getDefaultStyleSheetType() {
    String str = (String)getProperty("StyleType");
    return (str == null) ? "text/css" : str;
  }
  
  public void setParser(HTMLEditorKit.Parser paramParser) {
    this.parser = paramParser;
    putProperty("__PARSER__", null);
  }
  
  public HTMLEditorKit.Parser getParser() {
    Object object = getProperty("__PARSER__");
    return (object instanceof HTMLEditorKit.Parser) ? (HTMLEditorKit.Parser)object : this.parser;
  }
  
  public void setInnerHTML(Element paramElement, String paramString) {
    verifyParser();
    if (paramElement != null && paramElement.isLeaf())
      throw new IllegalArgumentException("Can not set inner HTML of a leaf"); 
    if (paramElement != null && paramString != null) {
      int i = paramElement.getElementCount();
      int j = paramElement.getStartOffset();
      insertHTML(paramElement, paramElement.getStartOffset(), paramString, true);
      if (paramElement.getElementCount() > i)
        removeElements(paramElement, paramElement.getElementCount() - i, i); 
    } 
  }
  
  public void setOuterHTML(Element paramElement, String paramString) {
    verifyParser();
    if (paramElement != null && paramElement.getParentElement() != null && paramString != null) {
      int i = paramElement.getStartOffset();
      int j = paramElement.getEndOffset();
      int k = getLength();
      boolean bool = !paramElement.isLeaf();
      if (!bool && (j > k || getText(j - 1, 1).charAt(0) == NEWLINE[0]))
        bool = true; 
      Element element = paramElement.getParentElement();
      int m = element.getElementCount();
      insertHTML(element, i, paramString, bool);
      int n = getLength();
      if (m != element.getElementCount()) {
        int i1 = element.getElementIndex(i + n - k);
        removeElements(element, i1, 1);
      } 
    } 
  }
  
  public void insertAfterStart(Element paramElement, String paramString) {
    verifyParser();
    if (paramElement == null || paramString == null)
      return; 
    if (paramElement.isLeaf())
      throw new IllegalArgumentException("Can not insert HTML after start of a leaf"); 
    insertHTML(paramElement, paramElement.getStartOffset(), paramString, false);
  }
  
  public void insertBeforeEnd(Element paramElement, String paramString) {
    verifyParser();
    if (paramElement != null && paramElement.isLeaf())
      throw new IllegalArgumentException("Can not set inner HTML before end of leaf"); 
    if (paramElement != null) {
      int i = paramElement.getEndOffset();
      if (paramElement.getElement(paramElement.getElementIndex(i - 1)).isLeaf() && getText(i - 1, 1).charAt(0) == NEWLINE[0])
        i--; 
      insertHTML(paramElement, i, paramString, false);
    } 
  }
  
  public void insertBeforeStart(Element paramElement, String paramString) {
    verifyParser();
    if (paramElement != null) {
      Element element = paramElement.getParentElement();
      if (element != null)
        insertHTML(element, paramElement.getStartOffset(), paramString, false); 
    } 
  }
  
  public void insertAfterEnd(Element paramElement, String paramString) {
    verifyParser();
    if (paramElement != null) {
      Element element = paramElement.getParentElement();
      if (element != null) {
        if (HTML.Tag.BODY.name.equals(element.getName()))
          this.insertInBody = true; 
        int i = paramElement.getEndOffset();
        if (i > getLength() + 1) {
          i--;
        } else if (paramElement.isLeaf() && getText(i - 1, 1).charAt(0) == NEWLINE[0]) {
          i--;
        } 
        insertHTML(element, i, paramString, false);
        if (this.insertInBody)
          this.insertInBody = false; 
      } 
    } 
  }
  
  public Element getElement(String paramString) { return (paramString == null) ? null : getElement(getDefaultRootElement(), HTML.Attribute.ID, paramString, true); }
  
  public Element getElement(Element paramElement, Object paramObject1, Object paramObject2) { return getElement(paramElement, paramObject1, paramObject2, true); }
  
  private Element getElement(Element paramElement, Object paramObject1, Object paramObject2, boolean paramBoolean) {
    AttributeSet attributeSet = paramElement.getAttributes();
    if (attributeSet != null && attributeSet.isDefined(paramObject1) && paramObject2.equals(attributeSet.getAttribute(paramObject1)))
      return paramElement; 
    if (!paramElement.isLeaf()) {
      byte b = 0;
      int i = paramElement.getElementCount();
      while (b < i) {
        Element element = getElement(paramElement.getElement(b), paramObject1, paramObject2, paramBoolean);
        if (element != null)
          return element; 
        b++;
      } 
    } else if (paramBoolean && attributeSet != null) {
      Enumeration enumeration = attributeSet.getAttributeNames();
      if (enumeration != null)
        while (enumeration.hasMoreElements()) {
          Object object = enumeration.nextElement();
          if (object instanceof HTML.Tag && attributeSet.getAttribute(object) instanceof AttributeSet) {
            AttributeSet attributeSet1 = (AttributeSet)attributeSet.getAttribute(object);
            if (attributeSet1.isDefined(paramObject1) && paramObject2.equals(attributeSet1.getAttribute(paramObject1)))
              return paramElement; 
          } 
        }  
    } 
    return null;
  }
  
  private void verifyParser() {
    if (getParser() == null)
      throw new IllegalStateException("No HTMLEditorKit.Parser"); 
  }
  
  private void installParserIfNecessary() {
    if (getParser() == null)
      setParser((new HTMLEditorKit()).getParser()); 
  }
  
  private void insertHTML(Element paramElement, int paramInt, String paramString, boolean paramBoolean) throws BadLocationException, IOException {
    if (paramElement != null && paramString != null) {
      HTMLEditorKit.Parser parser1 = getParser();
      if (parser1 != null) {
        int i = Math.max(0, paramInt - 1);
        Element element1 = getCharacterElement(i);
        Element element2 = paramElement;
        byte b1 = 0;
        byte b2 = 0;
        if (paramElement.getStartOffset() > i) {
          while (element2 != null && element2.getStartOffset() > i) {
            element2 = element2.getParentElement();
            b2++;
          } 
          if (element2 == null)
            throw new BadLocationException("No common parent", paramInt); 
        } 
        while (element1 != null && element1 != element2) {
          b1++;
          element1 = element1.getParentElement();
        } 
        if (element1 != null) {
          HTMLReader hTMLReader = new HTMLReader(paramInt, b1 - 1, b2, null, false, true, paramBoolean);
          parser1.parse(new StringReader(paramString), hTMLReader, true);
          hTMLReader.flush();
        } 
      } 
    } 
  }
  
  private void removeElements(Element paramElement, int paramInt1, int paramInt2) throws BadLocationException {
    writeLock();
    try {
      int i = paramElement.getElement(paramInt1).getStartOffset();
      int j = paramElement.getElement(paramInt1 + paramInt2 - 1).getEndOffset();
      if (j > getLength()) {
        removeElementsAtEnd(paramElement, paramInt1, paramInt2, i, j);
      } else {
        removeElements(paramElement, paramInt1, paramInt2, i, j);
      } 
    } finally {
      writeUnlock();
    } 
  }
  
  private void removeElementsAtEnd(Element paramElement, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    boolean bool = paramElement.getElement(paramInt1 - 1).isLeaf();
    AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt3 - 1, paramInt4 - paramInt3 + 1, DocumentEvent.EventType.REMOVE);
    if (bool) {
      Element element = getCharacterElement(getLength());
      paramInt1--;
      if (element.getParentElement() != paramElement) {
        replace(defaultDocumentEvent, paramElement, paramInt1, ++paramInt2, paramInt3, paramInt4, true, true);
      } else {
        replace(defaultDocumentEvent, paramElement, paramInt1, paramInt2, paramInt3, paramInt4, true, false);
      } 
    } else {
      Element element;
      for (element = paramElement.getElement(paramInt1 - 1); !element.isLeaf(); element = element.getElement(element.getElementCount() - 1));
      element = element.getParentElement();
      replace(defaultDocumentEvent, paramElement, paramInt1, paramInt2, paramInt3, paramInt4, false, false);
      replace(defaultDocumentEvent, element, element.getElementCount() - 1, 1, paramInt3, paramInt4, true, true);
    } 
    postRemoveUpdate(defaultDocumentEvent);
    defaultDocumentEvent.end();
    fireRemoveUpdate(defaultDocumentEvent);
    fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
  }
  
  private void replace(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, Element paramElement, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2) throws BadLocationException {
    Element[] arrayOfElement1;
    AttributeSet attributeSet = paramElement.getElement(paramInt1).getAttributes();
    Element[] arrayOfElement2 = new Element[paramInt2];
    for (int i = 0; i < paramInt2; i++)
      arrayOfElement2[i] = paramElement.getElement(i + paramInt1); 
    if (paramBoolean1) {
      UndoableEdit undoableEdit = getContent().remove(paramInt3 - 1, paramInt4 - paramInt3);
      if (undoableEdit != null)
        paramDefaultDocumentEvent.addEdit(undoableEdit); 
    } 
    if (paramBoolean2) {
      arrayOfElement1 = new Element[1];
      arrayOfElement1[0] = createLeafElement(paramElement, attributeSet, paramInt3 - 1, paramInt3);
    } else {
      arrayOfElement1 = new Element[0];
    } 
    paramDefaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(paramElement, paramInt1, arrayOfElement2, arrayOfElement1));
    ((AbstractDocument.BranchElement)paramElement).replace(paramInt1, arrayOfElement2.length, arrayOfElement1);
  }
  
  private void removeElements(Element paramElement, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    Element[] arrayOfElement1 = new Element[paramInt2];
    Element[] arrayOfElement2 = new Element[0];
    for (int i = 0; i < paramInt2; i++)
      arrayOfElement1[i] = paramElement.getElement(i + paramInt1); 
    AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt3, paramInt4 - paramInt3, DocumentEvent.EventType.REMOVE);
    ((AbstractDocument.BranchElement)paramElement).replace(paramInt1, arrayOfElement1.length, arrayOfElement2);
    defaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(paramElement, paramInt1, arrayOfElement1, arrayOfElement2));
    UndoableEdit undoableEdit = getContent().remove(paramInt3, paramInt4 - paramInt3);
    if (undoableEdit != null)
      defaultDocumentEvent.addEdit(undoableEdit); 
    postRemoveUpdate(defaultDocumentEvent);
    defaultDocumentEvent.end();
    fireRemoveUpdate(defaultDocumentEvent);
    if (undoableEdit != null)
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent)); 
  }
  
  void obtainLock() { writeLock(); }
  
  void releaseLock() { writeUnlock(); }
  
  protected void fireChangedUpdate(DocumentEvent paramDocumentEvent) { super.fireChangedUpdate(paramDocumentEvent); }
  
  protected void fireUndoableEditUpdate(UndoableEditEvent paramUndoableEditEvent) { super.fireUndoableEditUpdate(paramUndoableEditEvent); }
  
  boolean hasBaseTag() { return this.hasBaseTag; }
  
  String getBaseTarget() { return this.baseTarget; }
  
  static  {
    contentAttributeSet = new SimpleAttributeSet();
    ((MutableAttributeSet)contentAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
    NEWLINE = new char[1];
    NEWLINE[0] = '\n';
  }
  
  public class BlockElement extends AbstractDocument.BranchElement {
    public BlockElement(Element param1Element, AttributeSet param1AttributeSet) { super(HTMLDocument.this, param1Element, param1AttributeSet); }
    
    public String getName() {
      Object object = getAttribute(StyleConstants.NameAttribute);
      return (object != null) ? object.toString() : super.getName();
    }
    
    public AttributeSet getResolveParent() { return null; }
  }
  
  private static class FixedLengthDocument extends PlainDocument {
    private int maxLength;
    
    public FixedLengthDocument(int param1Int) { this.maxLength = param1Int; }
    
    public void insertString(int param1Int, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
      if (param1String != null && param1String.length() + getLength() <= this.maxLength)
        super.insertString(param1Int, param1String, param1AttributeSet); 
    }
  }
  
  public class HTMLReader extends HTMLEditorKit.ParserCallback {
    private boolean receivedEndHTML;
    
    private int flushCount;
    
    private boolean insertAfterImplied;
    
    private boolean wantsTrailingNewline;
    
    int threshold;
    
    int offset;
    
    boolean inParagraph = false;
    
    boolean impliedP = false;
    
    boolean inPre = false;
    
    boolean inTextArea = false;
    
    TextAreaDocument textAreaDocument = null;
    
    boolean inTitle = false;
    
    boolean lastWasNewline = true;
    
    boolean emptyAnchor;
    
    boolean midInsert;
    
    boolean inBody;
    
    HTML.Tag insertTag;
    
    boolean insertInsertTag;
    
    boolean foundInsertTag;
    
    int insertTagDepthDelta;
    
    int popDepth;
    
    int pushDepth;
    
    Map lastMap;
    
    boolean inStyle = false;
    
    String defaultStyle;
    
    Vector<Object> styles;
    
    boolean inHead = false;
    
    boolean isStyleCSS;
    
    boolean emptyDocument;
    
    AttributeSet styleAttributes;
    
    Option option;
    
    protected Vector<DefaultStyledDocument.ElementSpec> parseBuffer = new Vector();
    
    protected MutableAttributeSet charAttr = new HTMLDocument.TaggedAttributeSet();
    
    Stack<AttributeSet> charAttrStack = new Stack();
    
    Hashtable<HTML.Tag, TagAction> tagMap;
    
    int inBlock = 0;
    
    private HTML.Tag nextTagAfterPImplied = null;
    
    public HTMLReader(HTMLDocument this$0, int param1Int) { this(param1Int, 0, 0, null); }
    
    public HTMLReader(HTMLDocument this$0, int param1Int1, int param1Int2, int param1Int3, HTML.Tag param1Tag) { this(param1Int1, param1Int2, param1Int3, param1Tag, true, false, true); }
    
    HTMLReader(int param1Int1, int param1Int2, int param1Int3, HTML.Tag param1Tag, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3) {
      this.emptyDocument = (this$0.getLength() == 0);
      this.isStyleCSS = "text/css".equals(this$0.getDefaultStyleSheetType());
      this.offset = param1Int1;
      this.threshold = this$0.getTokenThreshold();
      this.tagMap = new Hashtable(57);
      TagAction tagAction = new TagAction();
      BlockAction blockAction = new BlockAction();
      ParagraphAction paragraphAction = new ParagraphAction();
      CharacterAction characterAction = new CharacterAction();
      SpecialAction specialAction = new SpecialAction();
      FormAction formAction = new FormAction();
      HiddenAction hiddenAction = new HiddenAction();
      ConvertAction convertAction = new ConvertAction();
      this.tagMap.put(HTML.Tag.A, new AnchorAction());
      this.tagMap.put(HTML.Tag.ADDRESS, characterAction);
      this.tagMap.put(HTML.Tag.APPLET, hiddenAction);
      this.tagMap.put(HTML.Tag.AREA, new AreaAction());
      this.tagMap.put(HTML.Tag.B, convertAction);
      this.tagMap.put(HTML.Tag.BASE, new BaseAction());
      this.tagMap.put(HTML.Tag.BASEFONT, characterAction);
      this.tagMap.put(HTML.Tag.BIG, characterAction);
      this.tagMap.put(HTML.Tag.BLOCKQUOTE, blockAction);
      this.tagMap.put(HTML.Tag.BODY, blockAction);
      this.tagMap.put(HTML.Tag.BR, specialAction);
      this.tagMap.put(HTML.Tag.CAPTION, blockAction);
      this.tagMap.put(HTML.Tag.CENTER, blockAction);
      this.tagMap.put(HTML.Tag.CITE, characterAction);
      this.tagMap.put(HTML.Tag.CODE, characterAction);
      this.tagMap.put(HTML.Tag.DD, blockAction);
      this.tagMap.put(HTML.Tag.DFN, characterAction);
      this.tagMap.put(HTML.Tag.DIR, blockAction);
      this.tagMap.put(HTML.Tag.DIV, blockAction);
      this.tagMap.put(HTML.Tag.DL, blockAction);
      this.tagMap.put(HTML.Tag.DT, paragraphAction);
      this.tagMap.put(HTML.Tag.EM, characterAction);
      this.tagMap.put(HTML.Tag.FONT, convertAction);
      this.tagMap.put(HTML.Tag.FORM, new FormTagAction(null));
      this.tagMap.put(HTML.Tag.FRAME, specialAction);
      this.tagMap.put(HTML.Tag.FRAMESET, blockAction);
      this.tagMap.put(HTML.Tag.H1, paragraphAction);
      this.tagMap.put(HTML.Tag.H2, paragraphAction);
      this.tagMap.put(HTML.Tag.H3, paragraphAction);
      this.tagMap.put(HTML.Tag.H4, paragraphAction);
      this.tagMap.put(HTML.Tag.H5, paragraphAction);
      this.tagMap.put(HTML.Tag.H6, paragraphAction);
      this.tagMap.put(HTML.Tag.HEAD, new HeadAction());
      this.tagMap.put(HTML.Tag.HR, specialAction);
      this.tagMap.put(HTML.Tag.HTML, blockAction);
      this.tagMap.put(HTML.Tag.I, convertAction);
      this.tagMap.put(HTML.Tag.IMG, specialAction);
      this.tagMap.put(HTML.Tag.INPUT, formAction);
      this.tagMap.put(HTML.Tag.ISINDEX, new IsindexAction());
      this.tagMap.put(HTML.Tag.KBD, characterAction);
      this.tagMap.put(HTML.Tag.LI, blockAction);
      this.tagMap.put(HTML.Tag.LINK, new LinkAction());
      this.tagMap.put(HTML.Tag.MAP, new MapAction());
      this.tagMap.put(HTML.Tag.MENU, blockAction);
      this.tagMap.put(HTML.Tag.META, new MetaAction());
      this.tagMap.put(HTML.Tag.NOBR, characterAction);
      this.tagMap.put(HTML.Tag.NOFRAMES, blockAction);
      this.tagMap.put(HTML.Tag.OBJECT, specialAction);
      this.tagMap.put(HTML.Tag.OL, blockAction);
      this.tagMap.put(HTML.Tag.OPTION, formAction);
      this.tagMap.put(HTML.Tag.P, paragraphAction);
      this.tagMap.put(HTML.Tag.PARAM, new ObjectAction());
      this.tagMap.put(HTML.Tag.PRE, new PreAction());
      this.tagMap.put(HTML.Tag.SAMP, characterAction);
      this.tagMap.put(HTML.Tag.SCRIPT, hiddenAction);
      this.tagMap.put(HTML.Tag.SELECT, formAction);
      this.tagMap.put(HTML.Tag.SMALL, characterAction);
      this.tagMap.put(HTML.Tag.SPAN, characterAction);
      this.tagMap.put(HTML.Tag.STRIKE, convertAction);
      this.tagMap.put(HTML.Tag.S, characterAction);
      this.tagMap.put(HTML.Tag.STRONG, characterAction);
      this.tagMap.put(HTML.Tag.STYLE, new StyleAction());
      this.tagMap.put(HTML.Tag.SUB, convertAction);
      this.tagMap.put(HTML.Tag.SUP, convertAction);
      this.tagMap.put(HTML.Tag.TABLE, blockAction);
      this.tagMap.put(HTML.Tag.TD, blockAction);
      this.tagMap.put(HTML.Tag.TEXTAREA, formAction);
      this.tagMap.put(HTML.Tag.TH, blockAction);
      this.tagMap.put(HTML.Tag.TITLE, new TitleAction());
      this.tagMap.put(HTML.Tag.TR, blockAction);
      this.tagMap.put(HTML.Tag.TT, characterAction);
      this.tagMap.put(HTML.Tag.U, convertAction);
      this.tagMap.put(HTML.Tag.UL, blockAction);
      this.tagMap.put(HTML.Tag.VAR, characterAction);
      if (param1Tag != null) {
        this.insertTag = param1Tag;
        this.popDepth = param1Int2;
        this.pushDepth = param1Int3;
        this.insertInsertTag = param1Boolean1;
        this.foundInsertTag = false;
      } else {
        this.foundInsertTag = true;
      } 
      if (param1Boolean2) {
        this.popDepth = param1Int2;
        this.pushDepth = param1Int3;
        this.insertAfterImplied = true;
        this.foundInsertTag = false;
        this.midInsert = false;
        this.insertInsertTag = true;
        this.wantsTrailingNewline = param1Boolean3;
      } else {
        this.midInsert = (!this.emptyDocument && param1Tag == null);
        if (this.midInsert)
          generateEndsSpecsForMidInsert(); 
      } 
      if (!this.emptyDocument && !this.midInsert) {
        int i = Math.max(this.offset - 1, 0);
        Element element = this$0.getCharacterElement(i);
        byte b;
        for (b = 0; b <= this.popDepth; b++)
          element = element.getParentElement(); 
        for (b = 0; b < this.pushDepth; b++) {
          int j = element.getElementIndex(this.offset);
          element = element.getElement(j);
        } 
        AttributeSet attributeSet = element.getAttributes();
        if (attributeSet != null) {
          HTML.Tag tag = (HTML.Tag)attributeSet.getAttribute(StyleConstants.NameAttribute);
          if (tag != null)
            this.inParagraph = tag.isParagraph(); 
        } 
      } 
    }
    
    private void generateEndsSpecsForMidInsert() {
      int i = heightToElementWithName(HTML.Tag.BODY, Math.max(0, this.offset - 1));
      boolean bool = false;
      if (i == -1 && this.offset > 0) {
        i = heightToElementWithName(HTML.Tag.BODY, this.offset);
        if (i != -1) {
          i = depthTo(this.offset - 1) - 1;
          bool = true;
        } 
      } 
      if (i == -1)
        throw new RuntimeException("Must insert new content into body element-"); 
      if (i != -1) {
        try {
          if (!bool && this.offset > 0 && !HTMLDocument.this.getText(this.offset - 1, 1).equals("\n")) {
            SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
            simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(simpleAttributeSet, (short)3, NEWLINE, 0, 1);
            this.parseBuffer.addElement(elementSpec);
          } 
        } catch (BadLocationException badLocationException) {}
        while (i-- > 0)
          this.parseBuffer.addElement(new DefaultStyledDocument.ElementSpec(null, (short)2)); 
        if (bool) {
          DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(null, (short)1);
          elementSpec.setDirection((short)5);
          this.parseBuffer.addElement(elementSpec);
        } 
      } 
    }
    
    private int depthTo(int param1Int) {
      Element element = HTMLDocument.this.getDefaultRootElement();
      byte b = 0;
      while (!element.isLeaf()) {
        b++;
        element = element.getElement(element.getElementIndex(param1Int));
      } 
      return b;
    }
    
    private int heightToElementWithName(Object param1Object, int param1Int) {
      Element element = HTMLDocument.this.getCharacterElement(param1Int).getParentElement();
      byte b = 0;
      while (element != null && element.getAttributes().getAttribute(StyleConstants.NameAttribute) != param1Object) {
        b++;
        element = element.getParentElement();
      } 
      return (element == null) ? -1 : b;
    }
    
    private void adjustEndElement() {
      int i = HTMLDocument.this.getLength();
      if (i == 0)
        return; 
      HTMLDocument.this.obtainLock();
      try {
        Element[] arrayOfElement = getPathTo(i - 1);
        int j = arrayOfElement.length;
        if (j > 1 && arrayOfElement[true].getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY && arrayOfElement[1].getEndOffset() == i) {
          String str = HTMLDocument.this.getText(i - 1, 1);
          Element[] arrayOfElement1 = new Element[0];
          Element[] arrayOfElement2 = new Element[1];
          int k = arrayOfElement[0].getElementIndex(i);
          arrayOfElement2[0] = arrayOfElement[0].getElement(k);
          ((AbstractDocument.BranchElement)arrayOfElement[0]).replace(k, 1, arrayOfElement1);
          AbstractDocument.ElementEdit elementEdit = new AbstractDocument.ElementEdit(arrayOfElement[0], k, arrayOfElement2, arrayOfElement1);
          SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
          simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
          simpleAttributeSet.addAttribute("CR", Boolean.TRUE);
          arrayOfElement1 = new Element[1];
          arrayOfElement1[0] = HTMLDocument.this.createLeafElement(arrayOfElement[j - 1], simpleAttributeSet, i, i + 1);
          k = arrayOfElement[j - 1].getElementCount();
          ((AbstractDocument.BranchElement)arrayOfElement[j - 1]).replace(k, 0, arrayOfElement1);
          AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(HTMLDocument.this, i, 1, DocumentEvent.EventType.CHANGE);
          defaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(arrayOfElement[j - 1], k, new Element[0], arrayOfElement1));
          defaultDocumentEvent.addEdit(elementEdit);
          defaultDocumentEvent.end();
          HTMLDocument.this.fireChangedUpdate(defaultDocumentEvent);
          HTMLDocument.this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
          if (str.equals("\n")) {
            defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(HTMLDocument.this, i - 1, 1, DocumentEvent.EventType.REMOVE);
            HTMLDocument.this.removeUpdate(defaultDocumentEvent);
            UndoableEdit undoableEdit = HTMLDocument.this.getContent().remove(i - 1, 1);
            if (undoableEdit != null)
              defaultDocumentEvent.addEdit(undoableEdit); 
            HTMLDocument.this.postRemoveUpdate(defaultDocumentEvent);
            defaultDocumentEvent.end();
            HTMLDocument.this.fireRemoveUpdate(defaultDocumentEvent);
            HTMLDocument.this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
          } 
        } 
      } catch (BadLocationException badLocationException) {
      
      } finally {
        HTMLDocument.this.releaseLock();
      } 
    }
    
    private Element[] getPathTo(int param1Int) {
      Stack stack = new Stack();
      for (Element element = HTMLDocument.this.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(param1Int)))
        stack.push(element); 
      Element[] arrayOfElement = new Element[stack.size()];
      stack.copyInto(arrayOfElement);
      return arrayOfElement;
    }
    
    public void flush() {
      if (this.emptyDocument && !this.insertAfterImplied) {
        if (HTMLDocument.this.getLength() > 0 || this.parseBuffer.size() > 0) {
          flushBuffer(true);
          adjustEndElement();
        } 
      } else {
        flushBuffer(true);
      } 
    }
    
    public void handleText(char[] param1ArrayOfChar, int param1Int) {
      if (this.receivedEndHTML || (this.midInsert && !this.inBody))
        return; 
      if (HTMLDocument.this.getProperty("i18n").equals(Boolean.FALSE)) {
        Object object = HTMLDocument.this.getProperty(TextAttribute.RUN_DIRECTION);
        if (object != null && object.equals(TextAttribute.RUN_DIRECTION_RTL)) {
          HTMLDocument.this.putProperty("i18n", Boolean.TRUE);
        } else if (SwingUtilities2.isComplexLayout(param1ArrayOfChar, 0, param1ArrayOfChar.length)) {
          HTMLDocument.this.putProperty("i18n", Boolean.TRUE);
        } 
      } 
      if (this.inTextArea) {
        textAreaContent(param1ArrayOfChar);
      } else if (this.inPre) {
        preContent(param1ArrayOfChar);
      } else if (this.inTitle) {
        HTMLDocument.this.putProperty("title", new String(param1ArrayOfChar));
      } else if (this.option != null) {
        this.option.setLabel(new String(param1ArrayOfChar));
      } else if (this.inStyle) {
        if (this.styles != null)
          this.styles.addElement(new String(param1ArrayOfChar)); 
      } else if (this.inBlock > 0) {
        if (!this.foundInsertTag && this.insertAfterImplied) {
          foundInsertTag(false);
          this.foundInsertTag = true;
          this.inParagraph = this.impliedP = !HTMLDocument.this.insertInBody;
        } 
        if (param1ArrayOfChar.length >= 1)
          addContent(param1ArrayOfChar, 0, param1ArrayOfChar.length); 
      } 
    }
    
    public void handleStartTag(HTML.Tag param1Tag, MutableAttributeSet param1MutableAttributeSet, int param1Int) {
      if (this.receivedEndHTML)
        return; 
      if (this.midInsert && !this.inBody) {
        if (param1Tag == HTML.Tag.BODY) {
          this.inBody = true;
          this.inBlock++;
        } 
        return;
      } 
      if (!this.inBody && param1Tag == HTML.Tag.BODY)
        this.inBody = true; 
      if (this.isStyleCSS && param1MutableAttributeSet.isDefined(HTML.Attribute.STYLE)) {
        String str = (String)param1MutableAttributeSet.getAttribute(HTML.Attribute.STYLE);
        param1MutableAttributeSet.removeAttribute(HTML.Attribute.STYLE);
        this.styleAttributes = HTMLDocument.this.getStyleSheet().getDeclaration(str);
        param1MutableAttributeSet.addAttributes(this.styleAttributes);
      } else {
        this.styleAttributes = null;
      } 
      TagAction tagAction = (TagAction)this.tagMap.get(param1Tag);
      if (tagAction != null)
        tagAction.start(param1Tag, param1MutableAttributeSet); 
    }
    
    public void handleComment(char[] param1ArrayOfChar, int param1Int) {
      if (this.receivedEndHTML) {
        addExternalComment(new String(param1ArrayOfChar));
        return;
      } 
      if (this.inStyle) {
        if (this.styles != null)
          this.styles.addElement(new String(param1ArrayOfChar)); 
      } else if (HTMLDocument.this.getPreservesUnknownTags()) {
        if (this.inBlock == 0 && (this.foundInsertTag || this.insertTag != HTML.Tag.COMMENT)) {
          addExternalComment(new String(param1ArrayOfChar));
          return;
        } 
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        simpleAttributeSet.addAttribute(HTML.Attribute.COMMENT, new String(param1ArrayOfChar));
        addSpecialElement(HTML.Tag.COMMENT, simpleAttributeSet);
      } 
      TagAction tagAction = (TagAction)this.tagMap.get(HTML.Tag.COMMENT);
      if (tagAction != null) {
        tagAction.start(HTML.Tag.COMMENT, new SimpleAttributeSet());
        tagAction.end(HTML.Tag.COMMENT);
      } 
    }
    
    private void addExternalComment(String param1String) {
      Object object = HTMLDocument.this.getProperty("AdditionalComments");
      if (object != null && !(object instanceof Vector))
        return; 
      if (object == null) {
        object = new Vector();
        HTMLDocument.this.putProperty("AdditionalComments", object);
      } 
      ((Vector)object).addElement(param1String);
    }
    
    public void handleEndTag(HTML.Tag param1Tag, int param1Int) {
      if (this.receivedEndHTML || (this.midInsert && !this.inBody))
        return; 
      if (param1Tag == HTML.Tag.HTML)
        this.receivedEndHTML = true; 
      if (param1Tag == HTML.Tag.BODY) {
        this.inBody = false;
        if (this.midInsert)
          this.inBlock--; 
      } 
      TagAction tagAction = (TagAction)this.tagMap.get(param1Tag);
      if (tagAction != null)
        tagAction.end(param1Tag); 
    }
    
    public void handleSimpleTag(HTML.Tag param1Tag, MutableAttributeSet param1MutableAttributeSet, int param1Int) {
      if (this.receivedEndHTML || (this.midInsert && !this.inBody))
        return; 
      if (this.isStyleCSS && param1MutableAttributeSet.isDefined(HTML.Attribute.STYLE)) {
        String str = (String)param1MutableAttributeSet.getAttribute(HTML.Attribute.STYLE);
        param1MutableAttributeSet.removeAttribute(HTML.Attribute.STYLE);
        this.styleAttributes = HTMLDocument.this.getStyleSheet().getDeclaration(str);
        param1MutableAttributeSet.addAttributes(this.styleAttributes);
      } else {
        this.styleAttributes = null;
      } 
      TagAction tagAction = (TagAction)this.tagMap.get(param1Tag);
      if (tagAction != null) {
        tagAction.start(param1Tag, param1MutableAttributeSet);
        tagAction.end(param1Tag);
      } else if (HTMLDocument.this.getPreservesUnknownTags()) {
        addSpecialElement(param1Tag, param1MutableAttributeSet);
      } 
    }
    
    public void handleEndOfLineString(String param1String) {
      if (this.emptyDocument && param1String != null)
        HTMLDocument.this.putProperty("__EndOfLine__", param1String); 
    }
    
    protected void registerTag(HTML.Tag param1Tag, TagAction param1TagAction) { this.tagMap.put(param1Tag, param1TagAction); }
    
    protected void pushCharacterStyle() { this.charAttrStack.push(this.charAttr.copyAttributes()); }
    
    protected void popCharacterStyle() {
      if (!this.charAttrStack.empty()) {
        this.charAttr = (MutableAttributeSet)this.charAttrStack.peek();
        this.charAttrStack.pop();
      } 
    }
    
    protected void textAreaContent(char[] param1ArrayOfChar) {
      try {
        this.textAreaDocument.insertString(this.textAreaDocument.getLength(), new String(param1ArrayOfChar), null);
      } catch (BadLocationException badLocationException) {}
    }
    
    protected void preContent(char[] param1ArrayOfChar) {
      int i = 0;
      for (byte b = 0; b < param1ArrayOfChar.length; b++) {
        if (param1ArrayOfChar[b] == '\n') {
          addContent(param1ArrayOfChar, i, b - i + true);
          blockClose(HTML.Tag.IMPLIED);
          SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
          simpleAttributeSet.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
          blockOpen(HTML.Tag.IMPLIED, simpleAttributeSet);
          i = b + 1;
        } 
      } 
      if (i < param1ArrayOfChar.length)
        addContent(param1ArrayOfChar, i, param1ArrayOfChar.length - i); 
    }
    
    protected void blockOpen(HTML.Tag param1Tag, MutableAttributeSet param1MutableAttributeSet) {
      if (this.impliedP)
        blockClose(HTML.Tag.IMPLIED); 
      this.inBlock++;
      if (!canInsertTag(param1Tag, param1MutableAttributeSet, true))
        return; 
      if (param1MutableAttributeSet.isDefined(IMPLIED))
        param1MutableAttributeSet.removeAttribute(IMPLIED); 
      this.lastWasNewline = false;
      param1MutableAttributeSet.addAttribute(StyleConstants.NameAttribute, param1Tag);
      DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(param1MutableAttributeSet.copyAttributes(), (short)1);
      this.parseBuffer.addElement(elementSpec);
    }
    
    protected void blockClose(HTML.Tag param1Tag) {
      this.inBlock--;
      if (!this.foundInsertTag)
        return; 
      if (!this.lastWasNewline) {
        pushCharacterStyle();
        this.charAttr.addAttribute("CR", Boolean.TRUE);
        addContent(NEWLINE, 0, 1, true);
        popCharacterStyle();
        this.lastWasNewline = true;
      } 
      if (this.impliedP) {
        this.impliedP = false;
        this.inParagraph = false;
        if (param1Tag != HTML.Tag.IMPLIED)
          blockClose(HTML.Tag.IMPLIED); 
      } 
      DefaultStyledDocument.ElementSpec elementSpec1 = (this.parseBuffer.size() > 0) ? (DefaultStyledDocument.ElementSpec)this.parseBuffer.lastElement() : null;
      if (elementSpec1 != null && elementSpec1.getType() == 1) {
        char[] arrayOfChar = new char[1];
        arrayOfChar[0] = ' ';
        addContent(arrayOfChar, 0, 1);
      } 
      DefaultStyledDocument.ElementSpec elementSpec2 = new DefaultStyledDocument.ElementSpec(null, (short)2);
      this.parseBuffer.addElement(elementSpec2);
    }
    
    protected void addContent(char[] param1ArrayOfChar, int param1Int1, int param1Int2) { addContent(param1ArrayOfChar, param1Int1, param1Int2, true); }
    
    protected void addContent(char[] param1ArrayOfChar, int param1Int1, int param1Int2, boolean param1Boolean) {
      if (!this.foundInsertTag)
        return; 
      if (param1Boolean && !this.inParagraph && !this.inPre) {
        blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
        this.inParagraph = true;
        this.impliedP = true;
      } 
      this.emptyAnchor = false;
      this.charAttr.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      AttributeSet attributeSet = this.charAttr.copyAttributes();
      DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(attributeSet, (short)3, param1ArrayOfChar, param1Int1, param1Int2);
      this.parseBuffer.addElement(elementSpec);
      if (this.parseBuffer.size() > this.threshold) {
        if (this.threshold <= 10000)
          this.threshold *= 5; 
        try {
          flushBuffer(false);
        } catch (BadLocationException badLocationException) {}
      } 
      if (param1Int2 > 0)
        this.lastWasNewline = (param1ArrayOfChar[param1Int1 + param1Int2 - 1] == '\n'); 
    }
    
    protected void addSpecialElement(HTML.Tag param1Tag, MutableAttributeSet param1MutableAttributeSet) {
      if (param1Tag != HTML.Tag.FRAME && !this.inParagraph && !this.inPre) {
        this.nextTagAfterPImplied = param1Tag;
        blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
        this.nextTagAfterPImplied = null;
        this.inParagraph = true;
        this.impliedP = true;
      } 
      if (!canInsertTag(param1Tag, param1MutableAttributeSet, param1Tag.isBlock()))
        return; 
      if (param1MutableAttributeSet.isDefined(IMPLIED))
        param1MutableAttributeSet.removeAttribute(IMPLIED); 
      this.emptyAnchor = false;
      param1MutableAttributeSet.addAttributes(this.charAttr);
      param1MutableAttributeSet.addAttribute(StyleConstants.NameAttribute, param1Tag);
      char[] arrayOfChar = new char[1];
      arrayOfChar[0] = ' ';
      DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(param1MutableAttributeSet.copyAttributes(), (short)3, arrayOfChar, 0, 1);
      this.parseBuffer.addElement(elementSpec);
      if (param1Tag == HTML.Tag.FRAME)
        this.lastWasNewline = true; 
    }
    
    void flushBuffer(boolean param1Boolean) {
      int i = HTMLDocument.this.getLength();
      int j = this.parseBuffer.size();
      if (param1Boolean && (this.insertTag != null || this.insertAfterImplied) && j > 0) {
        adjustEndSpecsForPartialInsert();
        j = this.parseBuffer.size();
      } 
      DefaultStyledDocument.ElementSpec[] arrayOfElementSpec = new DefaultStyledDocument.ElementSpec[j];
      this.parseBuffer.copyInto(arrayOfElementSpec);
      if (i == 0 && this.insertTag == null && !this.insertAfterImplied) {
        HTMLDocument.this.create(arrayOfElementSpec);
      } else {
        HTMLDocument.this.insert(this.offset, arrayOfElementSpec);
      } 
      this.parseBuffer.removeAllElements();
      this.offset += HTMLDocument.this.getLength() - i;
      this.flushCount++;
    }
    
    private void adjustEndSpecsForPartialInsert() {
      int i = this.parseBuffer.size();
      if (this.insertTagDepthDelta < 0)
        for (int j = this.insertTagDepthDelta; j < 0 && i >= 0 && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(i - 1)).getType() == 2; j++)
          this.parseBuffer.removeElementAt(--i);  
      if (this.flushCount == 0 && (!this.insertAfterImplied || !this.wantsTrailingNewline)) {
        int j = 0;
        if (this.pushDepth > 0 && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(0)).getType() == 3)
          j++; 
        j += this.popDepth + this.pushDepth;
        int k = 0;
        int m = j;
        while (j < i && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(j)).getType() == 3) {
          j++;
          k++;
        } 
        if (k > 1) {
          while (j < i && ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(j)).getType() == 2)
            j++; 
          if (j == i) {
            char[] arrayOfChar = ((DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(m + k - 1)).getArray();
            if (arrayOfChar.length == 1 && arrayOfChar[0] == NEWLINE[0]) {
              j = m + k - 1;
              while (i > j)
                this.parseBuffer.removeElementAt(--i); 
            } 
          } 
        } 
      } 
      if (this.wantsTrailingNewline)
        for (int j = this.parseBuffer.size() - 1; j >= 0; j--) {
          DefaultStyledDocument.ElementSpec elementSpec = (DefaultStyledDocument.ElementSpec)this.parseBuffer.elementAt(j);
          if (elementSpec.getType() == 3) {
            if (elementSpec.getArray()[elementSpec.getLength() - 1] != '\n') {
              SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
              simpleAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
              this.parseBuffer.insertElementAt(new DefaultStyledDocument.ElementSpec(simpleAttributeSet, (short)3, NEWLINE, 0, 1), j + 1);
            } 
            break;
          } 
        }  
    }
    
    void addCSSRules(String param1String) {
      StyleSheet styleSheet = HTMLDocument.this.getStyleSheet();
      styleSheet.addRule(param1String);
    }
    
    void linkCSSStyleSheet(String param1String) {
      URL uRL;
      try {
        uRL = new URL(HTMLDocument.this.base, param1String);
      } catch (MalformedURLException malformedURLException) {
        try {
          uRL = new URL(param1String);
        } catch (MalformedURLException malformedURLException1) {
          uRL = null;
        } 
      } 
      if (uRL != null)
        HTMLDocument.this.getStyleSheet().importStyleSheet(uRL); 
    }
    
    private boolean canInsertTag(HTML.Tag param1Tag, AttributeSet param1AttributeSet, boolean param1Boolean) {
      if (!this.foundInsertTag) {
        boolean bool = (param1Tag == HTML.Tag.IMPLIED && !this.inParagraph && !this.inPre) ? 1 : 0;
        if (bool && this.nextTagAfterPImplied != null) {
          if (this.insertTag != null) {
            boolean bool1 = isInsertTag(this.nextTagAfterPImplied);
            if (!bool1 || !this.insertInsertTag)
              return false; 
          } 
        } else if ((this.insertTag != null && !isInsertTag(param1Tag)) || (this.insertAfterImplied && (param1AttributeSet == null || param1AttributeSet.isDefined(IMPLIED) || param1Tag == HTML.Tag.IMPLIED))) {
          return false;
        } 
        foundInsertTag(param1Boolean);
        if (!this.insertInsertTag)
          return false; 
      } 
      return true;
    }
    
    private boolean isInsertTag(HTML.Tag param1Tag) { return (this.insertTag == param1Tag); }
    
    private void foundInsertTag(boolean param1Boolean) {
      this.foundInsertTag = true;
      if (!this.insertAfterImplied && (this.popDepth > 0 || this.pushDepth > 0))
        try {
          if (this.offset == 0 || !HTMLDocument.this.getText(this.offset - 1, 1).equals("\n")) {
            SimpleAttributeSet simpleAttributeSet = null;
            boolean bool = true;
            if (this.offset != 0) {
              Element element = HTMLDocument.this.getCharacterElement(this.offset - 1);
              AttributeSet attributeSet = element.getAttributes();
              if (attributeSet.isDefined(StyleConstants.ComposedTextAttribute)) {
                bool = false;
              } else {
                Object object = attributeSet.getAttribute(StyleConstants.NameAttribute);
                if (object instanceof HTML.Tag) {
                  HTML.Tag tag = (HTML.Tag)object;
                  if (tag == HTML.Tag.IMG || tag == HTML.Tag.HR || tag == HTML.Tag.COMMENT || tag instanceof HTML.UnknownTag)
                    bool = false; 
                } 
              } 
            } 
            if (!bool) {
              simpleAttributeSet = new SimpleAttributeSet();
              ((SimpleAttributeSet)simpleAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
            } 
            DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(simpleAttributeSet, (short)3, NEWLINE, 0, NEWLINE.length);
            if (bool)
              elementSpec.setDirection((short)4); 
            this.parseBuffer.addElement(elementSpec);
          } 
        } catch (BadLocationException badLocationException) {} 
      byte b;
      for (b = 0; b < this.popDepth; b++)
        this.parseBuffer.addElement(new DefaultStyledDocument.ElementSpec(null, (short)2)); 
      for (b = 0; b < this.pushDepth; b++) {
        DefaultStyledDocument.ElementSpec elementSpec = new DefaultStyledDocument.ElementSpec(null, (short)1);
        elementSpec.setDirection((short)5);
        this.parseBuffer.addElement(elementSpec);
      } 
      this.insertTagDepthDelta = depthTo(Math.max(0, this.offset - 1)) - this.popDepth + this.pushDepth - this.inBlock;
      if (param1Boolean) {
        this.insertTagDepthDelta++;
      } else {
        this.insertTagDepthDelta--;
        this.inParagraph = true;
        this.lastWasNewline = false;
      } 
    }
    
    class AnchorAction extends CharacterAction {
      AnchorAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.emptyAnchor = true;
        super.start(param2Tag, param2MutableAttributeSet);
      }
      
      public void end(HTML.Tag param2Tag) {
        if (HTMLDocument.HTMLReader.this.emptyAnchor) {
          char[] arrayOfChar = new char[1];
          arrayOfChar[0] = '\n';
          HTMLDocument.HTMLReader.this.addContent(arrayOfChar, 0, 1);
        } 
        super.end(param2Tag);
      }
    }
    
    class AreaAction extends TagAction {
      AreaAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        if (HTMLDocument.HTMLReader.this.lastMap != null)
          HTMLDocument.HTMLReader.this.lastMap.addArea(param2MutableAttributeSet.copyAttributes()); 
      }
      
      public void end(HTML.Tag param2Tag) {}
    }
    
    class BaseAction extends TagAction {
      BaseAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        String str = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.HREF);
        if (str != null)
          try {
            URL uRL = new URL(this.this$1.this$0.base, str);
            HTMLDocument.HTMLReader.this.this$0.setBase(uRL);
            this.this$1.this$0.hasBaseTag = true;
          } catch (MalformedURLException malformedURLException) {} 
        HTMLDocument.HTMLReader.this.this$0.baseTarget = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.TARGET);
      }
    }
    
    public class BlockAction extends TagAction {
      public BlockAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) { HTMLDocument.HTMLReader.this.blockOpen(param2Tag, param2MutableAttributeSet); }
      
      public void end(HTML.Tag param2Tag) { HTMLDocument.HTMLReader.this.blockClose(param2Tag); }
    }
    
    public class CharacterAction extends TagAction {
      public CharacterAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.pushCharacterStyle();
        if (!HTMLDocument.HTMLReader.this.foundInsertTag) {
          boolean bool = HTMLDocument.HTMLReader.this.canInsertTag(param2Tag, param2MutableAttributeSet, false);
          if (HTMLDocument.HTMLReader.this.foundInsertTag && !HTMLDocument.HTMLReader.this.inParagraph)
            HTMLDocument.HTMLReader.this.inParagraph = HTMLDocument.HTMLReader.this.impliedP = true; 
          if (!bool)
            return; 
        } 
        if (param2MutableAttributeSet.isDefined(HTMLEditorKit.ParserCallback.IMPLIED))
          param2MutableAttributeSet.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED); 
        HTMLDocument.HTMLReader.this.charAttr.addAttribute(param2Tag, param2MutableAttributeSet.copyAttributes());
        if (HTMLDocument.HTMLReader.this.styleAttributes != null)
          HTMLDocument.HTMLReader.this.charAttr.addAttributes(HTMLDocument.HTMLReader.this.styleAttributes); 
      }
      
      public void end(HTML.Tag param2Tag) { HTMLDocument.HTMLReader.this.popCharacterStyle(); }
    }
    
    class ConvertAction extends TagAction {
      ConvertAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.pushCharacterStyle();
        if (!HTMLDocument.HTMLReader.this.foundInsertTag) {
          boolean bool = HTMLDocument.HTMLReader.this.canInsertTag(param2Tag, param2MutableAttributeSet, false);
          if (HTMLDocument.HTMLReader.this.foundInsertTag && !HTMLDocument.HTMLReader.this.inParagraph)
            HTMLDocument.HTMLReader.this.inParagraph = HTMLDocument.HTMLReader.this.impliedP = true; 
          if (!bool)
            return; 
        } 
        if (param2MutableAttributeSet.isDefined(HTMLEditorKit.ParserCallback.IMPLIED))
          param2MutableAttributeSet.removeAttribute(HTMLEditorKit.ParserCallback.IMPLIED); 
        if (HTMLDocument.HTMLReader.this.styleAttributes != null)
          HTMLDocument.HTMLReader.this.charAttr.addAttributes(HTMLDocument.HTMLReader.this.styleAttributes); 
        HTMLDocument.HTMLReader.this.charAttr.addAttribute(param2Tag, param2MutableAttributeSet.copyAttributes());
        StyleSheet styleSheet = HTMLDocument.HTMLReader.this.this$0.getStyleSheet();
        if (param2Tag == HTML.Tag.B) {
          styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.FONT_WEIGHT, "bold");
        } else if (param2Tag == HTML.Tag.I) {
          styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.FONT_STYLE, "italic");
        } else if (param2Tag == HTML.Tag.U) {
          Object object = HTMLDocument.HTMLReader.this.charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
          String str = "underline";
          str = (object != null) ? (str + "," + object.toString()) : str;
          styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.TEXT_DECORATION, str);
        } else if (param2Tag == HTML.Tag.STRIKE) {
          Object object = HTMLDocument.HTMLReader.this.charAttr.getAttribute(CSS.Attribute.TEXT_DECORATION);
          String str = "line-through";
          str = (object != null) ? (str + "," + object.toString()) : str;
          styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.TEXT_DECORATION, str);
        } else if (param2Tag == HTML.Tag.SUP) {
          Object object = HTMLDocument.HTMLReader.this.charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
          String str = "sup";
          str = (object != null) ? (str + "," + object.toString()) : str;
          styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.VERTICAL_ALIGN, str);
        } else if (param2Tag == HTML.Tag.SUB) {
          Object object = HTMLDocument.HTMLReader.this.charAttr.getAttribute(CSS.Attribute.VERTICAL_ALIGN);
          String str = "sub";
          str = (object != null) ? (str + "," + object.toString()) : str;
          styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.VERTICAL_ALIGN, str);
        } else if (param2Tag == HTML.Tag.FONT) {
          String str1 = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.COLOR);
          if (str1 != null)
            styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.COLOR, str1); 
          String str2 = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.FACE);
          if (str2 != null)
            styleSheet.addCSSAttribute(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.FONT_FAMILY, str2); 
          String str3 = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.SIZE);
          if (str3 != null)
            styleSheet.addCSSAttributeFromHTML(HTMLDocument.HTMLReader.this.charAttr, CSS.Attribute.FONT_SIZE, str3); 
        } 
      }
      
      public void end(HTML.Tag param2Tag) { HTMLDocument.HTMLReader.this.popCharacterStyle(); }
    }
    
    public class FormAction extends SpecialAction {
      Object selectModel;
      
      int optionCount;
      
      public FormAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        if (param2Tag == HTML.Tag.INPUT) {
          String str = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.TYPE);
          if (str == null) {
            str = "text";
            param2MutableAttributeSet.addAttribute(HTML.Attribute.TYPE, "text");
          } 
          setModel(str, param2MutableAttributeSet);
        } else if (param2Tag == HTML.Tag.TEXTAREA) {
          HTMLDocument.HTMLReader.this.inTextArea = true;
          HTMLDocument.HTMLReader.this.textAreaDocument = new TextAreaDocument();
          param2MutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, HTMLDocument.HTMLReader.this.textAreaDocument);
        } else if (param2Tag == HTML.Tag.SELECT) {
          int i = HTML.getIntegerAttributeValue(param2MutableAttributeSet, HTML.Attribute.SIZE, 1);
          boolean bool = (param2MutableAttributeSet.getAttribute(HTML.Attribute.MULTIPLE) != null) ? 1 : 0;
          if (i > 1 || bool) {
            OptionListModel optionListModel = new OptionListModel();
            if (bool)
              optionListModel.setSelectionMode(2); 
            this.selectModel = optionListModel;
          } else {
            this.selectModel = new OptionComboBoxModel();
          } 
          param2MutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, this.selectModel);
        } 
        if (param2Tag == HTML.Tag.OPTION) {
          HTMLDocument.HTMLReader.this.option = new Option(param2MutableAttributeSet);
          if (this.selectModel instanceof OptionListModel) {
            OptionListModel optionListModel = (OptionListModel)this.selectModel;
            optionListModel.addElement(HTMLDocument.HTMLReader.this.option);
            if (HTMLDocument.HTMLReader.this.option.isSelected()) {
              optionListModel.addSelectionInterval(this.optionCount, this.optionCount);
              optionListModel.setInitialSelection(this.optionCount);
            } 
          } else if (this.selectModel instanceof OptionComboBoxModel) {
            OptionComboBoxModel optionComboBoxModel = (OptionComboBoxModel)this.selectModel;
            optionComboBoxModel.addElement(HTMLDocument.HTMLReader.this.option);
            if (HTMLDocument.HTMLReader.this.option.isSelected()) {
              optionComboBoxModel.setSelectedItem(HTMLDocument.HTMLReader.this.option);
              optionComboBoxModel.setInitialSelection(HTMLDocument.HTMLReader.this.option);
            } 
          } 
          this.optionCount++;
        } else {
          super.start(param2Tag, param2MutableAttributeSet);
        } 
      }
      
      public void end(HTML.Tag param2Tag) {
        if (param2Tag == HTML.Tag.OPTION) {
          HTMLDocument.HTMLReader.this.option = null;
        } else {
          if (param2Tag == HTML.Tag.SELECT) {
            this.selectModel = null;
            this.optionCount = 0;
          } else if (param2Tag == HTML.Tag.TEXTAREA) {
            HTMLDocument.HTMLReader.this.inTextArea = false;
            HTMLDocument.HTMLReader.this.textAreaDocument.storeInitialText();
          } 
          super.end(param2Tag);
        } 
      }
      
      void setModel(String param2String, MutableAttributeSet param2MutableAttributeSet) {
        if (param2String.equals("submit") || param2String.equals("reset") || param2String.equals("image")) {
          param2MutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, new DefaultButtonModel());
        } else if (param2String.equals("text") || param2String.equals("password")) {
          PlainDocument plainDocument;
          int i = HTML.getIntegerAttributeValue(param2MutableAttributeSet, HTML.Attribute.MAXLENGTH, -1);
          if (i > 0) {
            plainDocument = new HTMLDocument.FixedLengthDocument(i);
          } else {
            plainDocument = new PlainDocument();
          } 
          String str = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.VALUE);
          try {
            plainDocument.insertString(0, str, null);
          } catch (BadLocationException badLocationException) {}
          param2MutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, plainDocument);
        } else if (param2String.equals("file")) {
          param2MutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, new PlainDocument());
        } else if (param2String.equals("checkbox") || param2String.equals("radio")) {
          JToggleButton.ToggleButtonModel toggleButtonModel = new JToggleButton.ToggleButtonModel();
          if (param2String.equals("radio")) {
            String str = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.NAME);
            if (HTMLDocument.HTMLReader.this.this$0.radioButtonGroupsMap == null)
              HTMLDocument.HTMLReader.this.this$0.radioButtonGroupsMap = new HashMap(); 
            ButtonGroup buttonGroup = (ButtonGroup)HTMLDocument.HTMLReader.this.this$0.radioButtonGroupsMap.get(str);
            if (buttonGroup == null) {
              buttonGroup = new ButtonGroup();
              HTMLDocument.HTMLReader.this.this$0.radioButtonGroupsMap.put(str, buttonGroup);
            } 
            toggleButtonModel.setGroup(buttonGroup);
          } 
          boolean bool = (param2MutableAttributeSet.getAttribute(HTML.Attribute.CHECKED) != null);
          toggleButtonModel.setSelected(bool);
          param2MutableAttributeSet.addAttribute(StyleConstants.ModelAttribute, toggleButtonModel);
        } 
      }
    }
    
    private class FormTagAction extends BlockAction {
      private FormTagAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        super.start(param2Tag, param2MutableAttributeSet);
        HTMLDocument.HTMLReader.this.this$0.radioButtonGroupsMap = new HashMap();
      }
      
      public void end(HTML.Tag param2Tag) {
        super.end(param2Tag);
        HTMLDocument.HTMLReader.this.this$0.radioButtonGroupsMap = null;
      }
    }
    
    class HeadAction extends BlockAction {
      HeadAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.inHead = true;
        if ((HTMLDocument.HTMLReader.this.insertTag == null && !HTMLDocument.HTMLReader.this.insertAfterImplied) || HTMLDocument.HTMLReader.this.insertTag == HTML.Tag.HEAD || (HTMLDocument.HTMLReader.this.insertAfterImplied && (HTMLDocument.HTMLReader.this.foundInsertTag || !param2MutableAttributeSet.isDefined(HTMLEditorKit.ParserCallback.IMPLIED))))
          super.start(param2Tag, param2MutableAttributeSet); 
      }
      
      public void end(HTML.Tag param2Tag) {
        HTMLDocument.HTMLReader.this.inHead = HTMLDocument.HTMLReader.this.inStyle = false;
        if (HTMLDocument.HTMLReader.this.styles != null) {
          boolean bool = HTMLDocument.HTMLReader.this.isStyleCSS;
          byte b = 0;
          int i = HTMLDocument.HTMLReader.this.styles.size();
          while (b < i) {
            Object object = HTMLDocument.HTMLReader.this.styles.elementAt(b);
            if (object == HTML.Tag.LINK) {
              handleLink((AttributeSet)HTMLDocument.HTMLReader.this.styles.elementAt(++b));
              b++;
              continue;
            } 
            String str = (String)HTMLDocument.HTMLReader.this.styles.elementAt(++b);
            boolean bool1 = (str == null) ? bool : str.equals("text/css");
            while (++b < i && HTMLDocument.HTMLReader.this.styles.elementAt(b) instanceof String) {
              if (bool1)
                HTMLDocument.HTMLReader.this.addCSSRules((String)HTMLDocument.HTMLReader.this.styles.elementAt(b)); 
            } 
          } 
        } 
        if ((HTMLDocument.HTMLReader.this.insertTag == null && !HTMLDocument.HTMLReader.this.insertAfterImplied) || HTMLDocument.HTMLReader.this.insertTag == HTML.Tag.HEAD || (HTMLDocument.HTMLReader.this.insertAfterImplied && HTMLDocument.HTMLReader.this.foundInsertTag))
          super.end(param2Tag); 
      }
      
      boolean isEmpty(HTML.Tag param2Tag) { return false; }
      
      private void handleLink(AttributeSet param2AttributeSet) {
        String str = (String)param2AttributeSet.getAttribute(HTML.Attribute.TYPE);
        if (str == null)
          str = HTMLDocument.HTMLReader.this.this$0.getDefaultStyleSheetType(); 
        if (str.equals("text/css")) {
          String str1 = (String)param2AttributeSet.getAttribute(HTML.Attribute.REL);
          String str2 = (String)param2AttributeSet.getAttribute(HTML.Attribute.TITLE);
          String str3 = (String)param2AttributeSet.getAttribute(HTML.Attribute.MEDIA);
          if (str3 == null) {
            str3 = "all";
          } else {
            str3 = str3.toLowerCase();
          } 
          if (str1 != null) {
            str1 = str1.toLowerCase();
            if ((str3.indexOf("all") != -1 || str3.indexOf("screen") != -1) && (str1.equals("stylesheet") || (str1.equals("alternate stylesheet") && str2.equals(HTMLDocument.HTMLReader.this.defaultStyle))))
              HTMLDocument.HTMLReader.this.linkCSSStyleSheet((String)param2AttributeSet.getAttribute(HTML.Attribute.HREF)); 
          } 
        } 
      }
    }
    
    public class HiddenAction extends TagAction {
      public HiddenAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) { HTMLDocument.HTMLReader.this.addSpecialElement(param2Tag, param2MutableAttributeSet); }
      
      public void end(HTML.Tag param2Tag) {
        if (!isEmpty(param2Tag)) {
          SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
          simpleAttributeSet.addAttribute(HTML.Attribute.ENDTAG, "true");
          HTMLDocument.HTMLReader.this.addSpecialElement(param2Tag, simpleAttributeSet);
        } 
      }
      
      boolean isEmpty(HTML.Tag param2Tag) { return !(param2Tag == HTML.Tag.APPLET || param2Tag == HTML.Tag.SCRIPT); }
    }
    
    public class IsindexAction extends TagAction {
      public IsindexAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.blockOpen(HTML.Tag.IMPLIED, new SimpleAttributeSet());
        HTMLDocument.HTMLReader.this.addSpecialElement(param2Tag, param2MutableAttributeSet);
        HTMLDocument.HTMLReader.this.blockClose(HTML.Tag.IMPLIED);
      }
    }
    
    class LinkAction extends HiddenAction {
      LinkAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        String str = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.REL);
        if (str != null) {
          str = str.toLowerCase();
          if (str.equals("stylesheet") || str.equals("alternate stylesheet")) {
            if (HTMLDocument.HTMLReader.this.styles == null)
              HTMLDocument.HTMLReader.this.styles = new Vector(3); 
            HTMLDocument.HTMLReader.this.styles.addElement(param2Tag);
            HTMLDocument.HTMLReader.this.styles.addElement(param2MutableAttributeSet.copyAttributes());
          } 
        } 
        super.start(param2Tag, param2MutableAttributeSet);
      }
    }
    
    class MapAction extends TagAction {
      MapAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.lastMap = new Map((String)param2MutableAttributeSet.getAttribute(HTML.Attribute.NAME));
        HTMLDocument.HTMLReader.this.this$0.addMap(HTMLDocument.HTMLReader.this.lastMap);
      }
      
      public void end(HTML.Tag param2Tag) {}
    }
    
    class MetaAction extends HiddenAction {
      MetaAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        Object object = param2MutableAttributeSet.getAttribute(HTML.Attribute.HTTPEQUIV);
        if (object != null) {
          object = ((String)object).toLowerCase();
          if (object.equals("content-style-type")) {
            String str = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.CONTENT);
            HTMLDocument.HTMLReader.this.this$0.setDefaultStyleSheetType(str);
            HTMLDocument.HTMLReader.this.isStyleCSS = "text/css".equals(HTMLDocument.HTMLReader.this.this$0.getDefaultStyleSheetType());
          } else if (object.equals("default-style")) {
            HTMLDocument.HTMLReader.this.defaultStyle = (String)param2MutableAttributeSet.getAttribute(HTML.Attribute.CONTENT);
          } 
        } 
        super.start(param2Tag, param2MutableAttributeSet);
      }
      
      boolean isEmpty(HTML.Tag param2Tag) { return true; }
    }
    
    class ObjectAction extends SpecialAction {
      ObjectAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        if (param2Tag == HTML.Tag.PARAM) {
          addParameter(param2MutableAttributeSet);
        } else {
          super.start(param2Tag, param2MutableAttributeSet);
        } 
      }
      
      public void end(HTML.Tag param2Tag) {
        if (param2Tag != HTML.Tag.PARAM)
          super.end(param2Tag); 
      }
      
      void addParameter(AttributeSet param2AttributeSet) {
        String str1 = (String)param2AttributeSet.getAttribute(HTML.Attribute.NAME);
        String str2 = (String)param2AttributeSet.getAttribute(HTML.Attribute.VALUE);
        if (str1 != null && str2 != null) {
          DefaultStyledDocument.ElementSpec elementSpec = (DefaultStyledDocument.ElementSpec)HTMLDocument.HTMLReader.this.parseBuffer.lastElement();
          MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)elementSpec.getAttributes();
          mutableAttributeSet.addAttribute(str1, str2);
        } 
      }
    }
    
    public class ParagraphAction extends BlockAction {
      public ParagraphAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        super.start(param2Tag, param2MutableAttributeSet);
        HTMLDocument.HTMLReader.this.inParagraph = true;
      }
      
      public void end(HTML.Tag param2Tag) {
        super.end(param2Tag);
        HTMLDocument.HTMLReader.this.inParagraph = false;
      }
    }
    
    public class PreAction extends BlockAction {
      public PreAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.inPre = true;
        HTMLDocument.HTMLReader.this.blockOpen(param2Tag, param2MutableAttributeSet);
        param2MutableAttributeSet.addAttribute(CSS.Attribute.WHITE_SPACE, "pre");
        HTMLDocument.HTMLReader.this.blockOpen(HTML.Tag.IMPLIED, param2MutableAttributeSet);
      }
      
      public void end(HTML.Tag param2Tag) {
        HTMLDocument.HTMLReader.this.blockClose(HTML.Tag.IMPLIED);
        HTMLDocument.HTMLReader.this.inPre = false;
        HTMLDocument.HTMLReader.this.blockClose(param2Tag);
      }
    }
    
    public class SpecialAction extends TagAction {
      public SpecialAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) { HTMLDocument.HTMLReader.this.addSpecialElement(param2Tag, param2MutableAttributeSet); }
    }
    
    class StyleAction extends TagAction {
      StyleAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        if (HTMLDocument.HTMLReader.this.inHead) {
          if (HTMLDocument.HTMLReader.this.styles == null)
            HTMLDocument.HTMLReader.this.styles = new Vector(3); 
          HTMLDocument.HTMLReader.this.styles.addElement(param2Tag);
          HTMLDocument.HTMLReader.this.styles.addElement(param2MutableAttributeSet.getAttribute(HTML.Attribute.TYPE));
          HTMLDocument.HTMLReader.this.inStyle = true;
        } 
      }
      
      public void end(HTML.Tag param2Tag) { HTMLDocument.HTMLReader.this.inStyle = false; }
      
      boolean isEmpty(HTML.Tag param2Tag) { return false; }
    }
    
    public class TagAction {
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {}
      
      public void end(HTML.Tag param2Tag) {}
    }
    
    class TitleAction extends HiddenAction {
      TitleAction() { super(HTMLDocument.HTMLReader.this); }
      
      public void start(HTML.Tag param2Tag, MutableAttributeSet param2MutableAttributeSet) {
        HTMLDocument.HTMLReader.this.inTitle = true;
        super.start(param2Tag, param2MutableAttributeSet);
      }
      
      public void end(HTML.Tag param2Tag) {
        HTMLDocument.HTMLReader.this.inTitle = false;
        super.end(param2Tag);
      }
      
      boolean isEmpty(HTML.Tag param2Tag) { return false; }
    }
  }
  
  public static abstract class Iterator {
    public abstract AttributeSet getAttributes();
    
    public abstract int getStartOffset();
    
    public abstract int getEndOffset();
    
    public abstract void next();
    
    public abstract boolean isValid();
    
    public abstract HTML.Tag getTag();
  }
  
  static class LeafIterator extends Iterator {
    private int endOffset;
    
    private HTML.Tag tag;
    
    private ElementIterator pos;
    
    LeafIterator(HTML.Tag param1Tag, Document param1Document) {
      this.tag = param1Tag;
      this.pos = new ElementIterator(param1Document);
      this.endOffset = 0;
      next();
    }
    
    public AttributeSet getAttributes() {
      Element element = this.pos.current();
      if (element != null) {
        AttributeSet attributeSet = (AttributeSet)element.getAttributes().getAttribute(this.tag);
        if (attributeSet == null)
          attributeSet = element.getAttributes(); 
        return attributeSet;
      } 
      return null;
    }
    
    public int getStartOffset() {
      Element element = this.pos.current();
      return (element != null) ? element.getStartOffset() : -1;
    }
    
    public int getEndOffset() { return this.endOffset; }
    
    public void next() {
      nextLeaf(this.pos);
      while (isValid()) {
        Element element = this.pos.current();
        if (element.getStartOffset() >= this.endOffset) {
          AttributeSet attributeSet = this.pos.current().getAttributes();
          if (attributeSet.isDefined(this.tag) || attributeSet.getAttribute(StyleConstants.NameAttribute) == this.tag) {
            setEndOffset();
            break;
          } 
        } 
        nextLeaf(this.pos);
      } 
    }
    
    public HTML.Tag getTag() { return this.tag; }
    
    public boolean isValid() { return (this.pos.current() != null); }
    
    void nextLeaf(ElementIterator param1ElementIterator) {
      param1ElementIterator.next();
      while (param1ElementIterator.current() != null) {
        Element element = param1ElementIterator.current();
        if (element.isLeaf())
          break; 
        param1ElementIterator.next();
      } 
    }
    
    void setEndOffset() {
      AttributeSet attributeSet = getAttributes();
      this.endOffset = this.pos.current().getEndOffset();
      ElementIterator elementIterator = (ElementIterator)this.pos.clone();
      nextLeaf(elementIterator);
      while (elementIterator.current() != null) {
        Element element = elementIterator.current();
        AttributeSet attributeSet1 = (AttributeSet)element.getAttributes().getAttribute(this.tag);
        if (attributeSet1 == null || !attributeSet1.equals(attributeSet))
          break; 
        this.endOffset = element.getEndOffset();
        nextLeaf(elementIterator);
      } 
    }
  }
  
  public class RunElement extends AbstractDocument.LeafElement {
    public RunElement(Element param1Element, AttributeSet param1AttributeSet, int param1Int1, int param1Int2) { super(HTMLDocument.this, param1Element, param1AttributeSet, param1Int1, param1Int2); }
    
    public String getName() {
      Object object = getAttribute(StyleConstants.NameAttribute);
      return (object != null) ? object.toString() : super.getName();
    }
    
    public AttributeSet getResolveParent() { return null; }
  }
  
  static class TaggedAttributeSet extends SimpleAttributeSet {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\HTMLDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */