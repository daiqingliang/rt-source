package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JViewport;
import javax.swing.SizeRequirements;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Highlighter;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.awt.AppContext;

public class HTMLEditorKit extends StyledEditorKit implements Accessible {
  private JEditorPane theEditor;
  
  public static final String DEFAULT_CSS = "default.css";
  
  private AccessibleContext accessibleContext;
  
  private static final Cursor MoveCursor;
  
  private static final Cursor DefaultCursor = (MoveCursor = Cursor.getPredefinedCursor(12)).getPredefinedCursor(0);
  
  private static final ViewFactory defaultFactory = new HTMLFactory();
  
  MutableAttributeSet input;
  
  private static final Object DEFAULT_STYLES_KEY = new Object();
  
  private LinkController linkHandler = new LinkController();
  
  private static Parser defaultParser = null;
  
  private Cursor defaultCursor = DefaultCursor;
  
  private Cursor linkCursor = MoveCursor;
  
  private boolean isAutoFormSubmission = true;
  
  public static final String BOLD_ACTION = "html-bold-action";
  
  public static final String ITALIC_ACTION = "html-italic-action";
  
  public static final String PARA_INDENT_LEFT = "html-para-indent-left";
  
  public static final String PARA_INDENT_RIGHT = "html-para-indent-right";
  
  public static final String FONT_CHANGE_BIGGER = "html-font-bigger";
  
  public static final String FONT_CHANGE_SMALLER = "html-font-smaller";
  
  public static final String COLOR_ACTION = "html-color-action";
  
  public static final String LOGICAL_STYLE_ACTION = "html-logical-style-action";
  
  public static final String IMG_ALIGN_TOP = "html-image-align-top";
  
  public static final String IMG_ALIGN_MIDDLE = "html-image-align-middle";
  
  public static final String IMG_ALIGN_BOTTOM = "html-image-align-bottom";
  
  public static final String IMG_BORDER = "html-image-border";
  
  private static final String INSERT_TABLE_HTML = "<table border=1><tr><td></td></tr></table>";
  
  private static final String INSERT_UL_HTML = "<ul><li></li></ul>";
  
  private static final String INSERT_OL_HTML = "<ol><li></li></ol>";
  
  private static final String INSERT_HR_HTML = "<hr>";
  
  private static final String INSERT_PRE_HTML = "<pre></pre>";
  
  private static final NavigateLinkAction nextLinkAction = new NavigateLinkAction("next-link-action");
  
  private static final NavigateLinkAction previousLinkAction = new NavigateLinkAction("previous-link-action");
  
  private static final ActivateLinkAction activateLinkAction = new ActivateLinkAction("activate-link-action");
  
  private static final Action[] defaultActions = { 
      new InsertHTMLTextAction("InsertTable", "<table border=1><tr><td></td></tr></table>", HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertTableRow", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TABLE, HTML.Tag.TR, HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertTableDataCell", "<table border=1><tr><td></td></tr></table>", HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.BODY, HTML.Tag.TABLE), new InsertHTMLTextAction("InsertUnorderedList", "<ul><li></li></ul>", HTML.Tag.BODY, HTML.Tag.UL), new InsertHTMLTextAction("InsertUnorderedListItem", "<ul><li></li></ul>", HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL), new InsertHTMLTextAction("InsertOrderedList", "<ol><li></li></ol>", HTML.Tag.BODY, HTML.Tag.OL), new InsertHTMLTextAction("InsertOrderedListItem", "<ol><li></li></ol>", HTML.Tag.OL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.OL), new InsertHRAction(), new InsertHTMLTextAction("InsertPre", "<pre></pre>", HTML.Tag.BODY, HTML.Tag.PRE), nextLinkAction, 
      previousLinkAction, activateLinkAction, new BeginAction("caret-begin", false), new BeginAction("selection-begin", true) };
  
  private boolean foundLink = false;
  
  private int prevHypertextOffset = -1;
  
  private Object linkNavigationTag;
  
  public String getContentType() { return "text/html"; }
  
  public ViewFactory getViewFactory() { return defaultFactory; }
  
  public Document createDefaultDocument() {
    StyleSheet styleSheet1 = getStyleSheet();
    StyleSheet styleSheet2 = new StyleSheet();
    styleSheet2.addStyleSheet(styleSheet1);
    HTMLDocument hTMLDocument = new HTMLDocument(styleSheet2);
    hTMLDocument.setParser(getParser());
    hTMLDocument.setAsynchronousLoadPriority(4);
    hTMLDocument.setTokenThreshold(100);
    return hTMLDocument;
  }
  
  private Parser ensureParser(HTMLDocument paramHTMLDocument) throws IOException {
    Parser parser = paramHTMLDocument.getParser();
    if (parser == null)
      parser = getParser(); 
    if (parser == null)
      throw new IOException("Can't load parser"); 
    return parser;
  }
  
  public void read(Reader paramReader, Document paramDocument, int paramInt) throws IOException, BadLocationException {
    if (paramDocument instanceof HTMLDocument) {
      HTMLDocument hTMLDocument = (HTMLDocument)paramDocument;
      if (paramInt > paramDocument.getLength())
        throw new BadLocationException("Invalid location", paramInt); 
      Parser parser = ensureParser(hTMLDocument);
      ParserCallback parserCallback = hTMLDocument.getReader(paramInt);
      Boolean bool = (Boolean)paramDocument.getProperty("IgnoreCharsetDirective");
      parser.parse(paramReader, parserCallback, (bool == null) ? false : bool.booleanValue());
      parserCallback.flush();
    } else {
      super.read(paramReader, paramDocument, paramInt);
    } 
  }
  
  public void insertHTML(HTMLDocument paramHTMLDocument, int paramInt1, String paramString, int paramInt2, int paramInt3, HTML.Tag paramTag) throws BadLocationException, IOException {
    if (paramInt1 > paramHTMLDocument.getLength())
      throw new BadLocationException("Invalid location", paramInt1); 
    Parser parser = ensureParser(paramHTMLDocument);
    ParserCallback parserCallback = paramHTMLDocument.getReader(paramInt1, paramInt2, paramInt3, paramTag);
    Boolean bool = (Boolean)paramHTMLDocument.getProperty("IgnoreCharsetDirective");
    parser.parse(new StringReader(paramString), parserCallback, (bool == null) ? false : bool.booleanValue());
    parserCallback.flush();
  }
  
  public void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2) throws IOException, BadLocationException {
    if (paramDocument instanceof HTMLDocument) {
      HTMLWriter hTMLWriter = new HTMLWriter(paramWriter, (HTMLDocument)paramDocument, paramInt1, paramInt2);
      hTMLWriter.write();
    } else if (paramDocument instanceof StyledDocument) {
      MinimalHTMLWriter minimalHTMLWriter = new MinimalHTMLWriter(paramWriter, (StyledDocument)paramDocument, paramInt1, paramInt2);
      minimalHTMLWriter.write();
    } else {
      super.write(paramWriter, paramDocument, paramInt1, paramInt2);
    } 
  }
  
  public void install(JEditorPane paramJEditorPane) {
    paramJEditorPane.addMouseListener(this.linkHandler);
    paramJEditorPane.addMouseMotionListener(this.linkHandler);
    paramJEditorPane.addCaretListener(nextLinkAction);
    super.install(paramJEditorPane);
    this.theEditor = paramJEditorPane;
  }
  
  public void deinstall(JEditorPane paramJEditorPane) {
    paramJEditorPane.removeMouseListener(this.linkHandler);
    paramJEditorPane.removeMouseMotionListener(this.linkHandler);
    paramJEditorPane.removeCaretListener(nextLinkAction);
    super.deinstall(paramJEditorPane);
    this.theEditor = null;
  }
  
  public void setStyleSheet(StyleSheet paramStyleSheet) {
    if (paramStyleSheet == null) {
      AppContext.getAppContext().remove(DEFAULT_STYLES_KEY);
    } else {
      AppContext.getAppContext().put(DEFAULT_STYLES_KEY, paramStyleSheet);
    } 
  }
  
  public StyleSheet getStyleSheet() {
    AppContext appContext = AppContext.getAppContext();
    StyleSheet styleSheet = (StyleSheet)appContext.get(DEFAULT_STYLES_KEY);
    if (styleSheet == null) {
      styleSheet = new StyleSheet();
      appContext.put(DEFAULT_STYLES_KEY, styleSheet);
      try {
        InputStream inputStream = getResourceAsStream("default.css");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-1"));
        styleSheet.loadRules(bufferedReader, null);
        bufferedReader.close();
      } catch (Throwable throwable) {}
    } 
    return styleSheet;
  }
  
  static InputStream getResourceAsStream(final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
          public InputStream run() { return HTMLEditorKit.class.getResourceAsStream(name); }
        }); }
  
  public Action[] getActions() {
    this;
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  
  protected void createInputAttributes(Element paramElement, MutableAttributeSet paramMutableAttributeSet) {
    paramMutableAttributeSet.removeAttributes(paramMutableAttributeSet);
    paramMutableAttributeSet.addAttributes(paramElement.getAttributes());
    paramMutableAttributeSet.removeAttribute(StyleConstants.ComposedTextAttribute);
    Object object = paramMutableAttributeSet.getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.Tag) {
      HTML.Tag tag = (HTML.Tag)object;
      if (tag == HTML.Tag.IMG) {
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.SRC);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.HEIGHT);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.WIDTH);
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      } else if (tag == HTML.Tag.HR || tag == HTML.Tag.BR) {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
      } else if (tag == HTML.Tag.COMMENT) {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.COMMENT);
      } else if (tag == HTML.Tag.INPUT) {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        paramMutableAttributeSet.removeAttribute(HTML.Tag.INPUT);
      } else if (tag instanceof HTML.UnknownTag) {
        paramMutableAttributeSet.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
        paramMutableAttributeSet.removeAttribute(HTML.Attribute.ENDTAG);
      } 
    } 
  }
  
  public MutableAttributeSet getInputAttributes() {
    if (this.input == null)
      this.input = getStyleSheet().addStyle(null, null); 
    return this.input;
  }
  
  public void setDefaultCursor(Cursor paramCursor) { this.defaultCursor = paramCursor; }
  
  public Cursor getDefaultCursor() { return this.defaultCursor; }
  
  public void setLinkCursor(Cursor paramCursor) { this.linkCursor = paramCursor; }
  
  public Cursor getLinkCursor() { return this.linkCursor; }
  
  public boolean isAutoFormSubmission() { return this.isAutoFormSubmission; }
  
  public void setAutoFormSubmission(boolean paramBoolean) { this.isAutoFormSubmission = paramBoolean; }
  
  public Object clone() {
    HTMLEditorKit hTMLEditorKit = (HTMLEditorKit)super.clone();
    if (hTMLEditorKit != null) {
      hTMLEditorKit.input = null;
      hTMLEditorKit.linkHandler = new LinkController();
    } 
    return hTMLEditorKit;
  }
  
  protected Parser getParser() {
    if (defaultParser == null)
      try {
        Class clazz = Class.forName("javax.swing.text.html.parser.ParserDelegator");
        defaultParser = (Parser)clazz.newInstance();
      } catch (Throwable throwable) {} 
    return defaultParser;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.theEditor == null)
      return null; 
    if (this.accessibleContext == null) {
      AccessibleHTML accessibleHTML = new AccessibleHTML(this.theEditor);
      this.accessibleContext = accessibleHTML.getAccessibleContext();
    } 
    return this.accessibleContext;
  }
  
  private static Object getAttrValue(AttributeSet paramAttributeSet, HTML.Attribute paramAttribute) {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object1 = enumeration.nextElement();
      Object object2 = paramAttributeSet.getAttribute(object1);
      if (object2 instanceof AttributeSet) {
        Object object = getAttrValue((AttributeSet)object2, paramAttribute);
        if (object != null)
          return object; 
        continue;
      } 
      if (object1 == paramAttribute)
        return object2; 
    } 
    return null;
  }
  
  private static int getBodyElementStart(JTextComponent paramJTextComponent) {
    Element element = paramJTextComponent.getDocument().getRootElements()[0];
    for (byte b = 0; b < element.getElementCount(); b++) {
      Element element1 = element.getElement(b);
      if ("body".equals(element1.getName()))
        return element1.getStartOffset(); 
    } 
    return 0;
  }
  
  static class ActivateLinkAction extends TextAction {
    public ActivateLinkAction(String param1String) { super(param1String); }
    
    private void activateLink(String param1String, HTMLDocument param1HTMLDocument, JEditorPane param1JEditorPane, int param1Int) {
      try {
        URL uRL1 = (URL)param1HTMLDocument.getProperty("stream");
        URL uRL2 = new URL(uRL1, param1String);
        HyperlinkEvent hyperlinkEvent = new HyperlinkEvent(param1JEditorPane, HyperlinkEvent.EventType.ACTIVATED, uRL2, uRL2.toExternalForm(), param1HTMLDocument.getCharacterElement(param1Int));
        param1JEditorPane.fireHyperlinkUpdate(hyperlinkEvent);
      } catch (MalformedURLException malformedURLException) {}
    }
    
    private void doObjectAction(JEditorPane param1JEditorPane, Element param1Element) {
      View view = getView(param1JEditorPane, param1Element);
      if (view != null && view instanceof ObjectView) {
        Component component = ((ObjectView)view).getComponent();
        if (component != null && component instanceof Accessible) {
          AccessibleContext accessibleContext = component.getAccessibleContext();
          if (accessibleContext != null) {
            AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
            if (accessibleAction != null)
              accessibleAction.doAccessibleAction(0); 
          } 
        } 
      } 
    }
    
    private View getRootView(JEditorPane param1JEditorPane) { return param1JEditorPane.getUI().getRootView(param1JEditorPane); }
    
    private View getView(JEditorPane param1JEditorPane, Element param1Element) {
      object = lock(param1JEditorPane);
      try {
        View view = getRootView(param1JEditorPane);
        int i = param1Element.getStartOffset();
        if (view != null)
          return getView(view, param1Element, i); 
        return null;
      } finally {
        unlock(object);
      } 
    }
    
    private View getView(View param1View, Element param1Element, int param1Int) {
      if (param1View.getElement() == param1Element)
        return param1View; 
      int i = param1View.getViewIndex(param1Int, Position.Bias.Forward);
      return (i != -1 && i < param1View.getViewCount()) ? getView(param1View.getView(i), param1Element, param1Int) : null;
    }
    
    private Object lock(JEditorPane param1JEditorPane) {
      Document document = param1JEditorPane.getDocument();
      if (document instanceof AbstractDocument) {
        ((AbstractDocument)document).readLock();
        return document;
      } 
      return null;
    }
    
    private void unlock(Object param1Object) {
      if (param1Object != null)
        ((AbstractDocument)param1Object).readUnlock(); 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent.isEditable() || !(jTextComponent instanceof JEditorPane))
        return; 
      JEditorPane jEditorPane = (JEditorPane)jTextComponent;
      Document document = jEditorPane.getDocument();
      if (document == null || !(document instanceof HTMLDocument))
        return; 
      HTMLDocument hTMLDocument = (HTMLDocument)document;
      ElementIterator elementIterator = new ElementIterator(hTMLDocument);
      int i = jEditorPane.getCaretPosition();
      Object object1 = null;
      Object object2 = null;
      Element element;
      while ((element = elementIterator.next()) != null) {
        String str = element.getName();
        AttributeSet attributeSet = element.getAttributes();
        Object object = HTMLEditorKit.getAttrValue(attributeSet, HTML.Attribute.HREF);
        if (object != null) {
          if (i >= element.getStartOffset() && i <= element.getEndOffset()) {
            activateLink((String)object, hTMLDocument, jEditorPane, i);
            return;
          } 
          continue;
        } 
        if (str.equals(HTML.Tag.OBJECT.toString())) {
          Object object3 = HTMLEditorKit.getAttrValue(attributeSet, HTML.Attribute.CLASSID);
          if (object3 != null && i >= element.getStartOffset() && i <= element.getEndOffset()) {
            doObjectAction(jEditorPane, element);
            return;
          } 
        } 
      } 
    }
  }
  
  static class BeginAction extends TextAction {
    private boolean select;
    
    BeginAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      int i = HTMLEditorKit.getBodyElementStart(jTextComponent);
      if (jTextComponent != null)
        if (this.select) {
          jTextComponent.moveCaretPosition(i);
        } else {
          jTextComponent.setCaretPosition(i);
        }  
    }
  }
  
  public static class HTMLFactory implements ViewFactory {
    public View create(Element param1Element) {
      AttributeSet attributeSet = param1Element.getAttributes();
      Object object1 = attributeSet.getAttribute("$ename");
      Object object2 = (object1 != null) ? null : attributeSet.getAttribute(StyleConstants.NameAttribute);
      if (object2 instanceof HTML.Tag) {
        HTML.Tag tag = (HTML.Tag)object2;
        if (tag == HTML.Tag.CONTENT)
          return new InlineView(param1Element); 
        if (tag == HTML.Tag.IMPLIED) {
          String str1 = (String)param1Element.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
          return (str1 != null && str1.equals("pre")) ? new LineView(param1Element) : new ParagraphView(param1Element);
        } 
        if (tag == HTML.Tag.P || tag == HTML.Tag.H1 || tag == HTML.Tag.H2 || tag == HTML.Tag.H3 || tag == HTML.Tag.H4 || tag == HTML.Tag.H5 || tag == HTML.Tag.H6 || tag == HTML.Tag.DT)
          return new ParagraphView(param1Element); 
        if (tag == HTML.Tag.MENU || tag == HTML.Tag.DIR || tag == HTML.Tag.UL || tag == HTML.Tag.OL)
          return new ListView(param1Element); 
        if (tag == HTML.Tag.BODY)
          return new BodyBlockView(param1Element); 
        if (tag == HTML.Tag.HTML)
          return new BlockView(param1Element, 1); 
        if (tag == HTML.Tag.LI || tag == HTML.Tag.CENTER || tag == HTML.Tag.DL || tag == HTML.Tag.DD || tag == HTML.Tag.DIV || tag == HTML.Tag.BLOCKQUOTE || tag == HTML.Tag.PRE || tag == HTML.Tag.FORM)
          return new BlockView(param1Element, 1); 
        if (tag == HTML.Tag.NOFRAMES)
          return new NoFramesView(param1Element, 1); 
        if (tag == HTML.Tag.IMG)
          return new ImageView(param1Element); 
        if (tag == HTML.Tag.ISINDEX)
          return new IsindexView(param1Element); 
        if (tag == HTML.Tag.HR)
          return new HRuleView(param1Element); 
        if (tag == HTML.Tag.BR)
          return new BRView(param1Element); 
        if (tag == HTML.Tag.TABLE)
          return new TableView(param1Element); 
        if (tag == HTML.Tag.INPUT || tag == HTML.Tag.SELECT || tag == HTML.Tag.TEXTAREA)
          return new FormView(param1Element); 
        if (tag == HTML.Tag.OBJECT)
          return new ObjectView(param1Element); 
        if (tag == HTML.Tag.FRAMESET) {
          if (param1Element.getAttributes().isDefined(HTML.Attribute.ROWS))
            return new FrameSetView(param1Element, 1); 
          if (param1Element.getAttributes().isDefined(HTML.Attribute.COLS))
            return new FrameSetView(param1Element, 0); 
          throw new RuntimeException("Can't build a" + tag + ", " + param1Element + ":no ROWS or COLS defined.");
        } 
        if (tag == HTML.Tag.FRAME)
          return new FrameView(param1Element); 
        if (tag instanceof HTML.UnknownTag)
          return new HiddenTagView(param1Element); 
        if (tag == HTML.Tag.COMMENT)
          return new CommentView(param1Element); 
        if (tag == HTML.Tag.HEAD)
          return new BlockView(param1Element, 0) {
              public float getPreferredSpan(int param2Int) { return 0.0F; }
              
              public float getMinimumSpan(int param2Int) { return 0.0F; }
              
              public float getMaximumSpan(int param2Int) { return 0.0F; }
              
              protected void loadChildren(ViewFactory param2ViewFactory) {}
              
              public Shape modelToView(int param2Int, Shape param2Shape, Position.Bias param2Bias) throws BadLocationException { return param2Shape; }
              
              public int getNextVisualPositionFrom(int param2Int1, Position.Bias param2Bias, Shape param2Shape, int param2Int2, Position.Bias[] param2ArrayOfBias) { return getElement().getEndOffset(); }
            }; 
        if (tag == HTML.Tag.TITLE || tag == HTML.Tag.META || tag == HTML.Tag.LINK || tag == HTML.Tag.STYLE || tag == HTML.Tag.SCRIPT || tag == HTML.Tag.AREA || tag == HTML.Tag.MAP || tag == HTML.Tag.PARAM || tag == HTML.Tag.APPLET)
          return new HiddenTagView(param1Element); 
      } 
      String str = (object1 != null) ? (String)object1 : param1Element.getName();
      if (str != null) {
        if (str.equals("content"))
          return new LabelView(param1Element); 
        if (str.equals("paragraph"))
          return new ParagraphView(param1Element); 
        if (str.equals("section"))
          return new BoxView(param1Element, 1); 
        if (str.equals("component"))
          return new ComponentView(param1Element); 
        if (str.equals("icon"))
          return new IconView(param1Element); 
      } 
      return new LabelView(param1Element);
    }
    
    static class BodyBlockView extends BlockView implements ComponentListener {
      private Reference<JViewport> cachedViewPort = null;
      
      private boolean isListening = false;
      
      private int viewVisibleWidth = Integer.MAX_VALUE;
      
      private int componentVisibleWidth = Integer.MAX_VALUE;
      
      public BodyBlockView(Element param2Element) { super(param2Element, 1); }
      
      protected SizeRequirements calculateMajorAxisRequirements(int param2Int, SizeRequirements param2SizeRequirements) {
        param2SizeRequirements = super.calculateMajorAxisRequirements(param2Int, param2SizeRequirements);
        param2SizeRequirements.maximum = Integer.MAX_VALUE;
        return param2SizeRequirements;
      }
      
      protected void layoutMinorAxis(int param2Int1, int param2Int2, int[] param2ArrayOfInt1, int[] param2ArrayOfInt2) {
        Container container1 = getContainer();
        Container container2;
        if (container1 != null && container1 instanceof JEditorPane && (container2 = container1.getParent()) != null && container2 instanceof JViewport) {
          JViewport jViewport = (JViewport)container2;
          if (this.cachedViewPort != null) {
            JViewport jViewport1 = (JViewport)this.cachedViewPort.get();
            if (jViewport1 != null) {
              if (jViewport1 != jViewport)
                jViewport1.removeComponentListener(this); 
            } else {
              this.cachedViewPort = null;
            } 
          } 
          if (this.cachedViewPort == null) {
            jViewport.addComponentListener(this);
            this.cachedViewPort = new WeakReference(jViewport);
          } 
          this.componentVisibleWidth = (jViewport.getExtentSize()).width;
          if (this.componentVisibleWidth > 0) {
            Insets insets = container1.getInsets();
            this.viewVisibleWidth = this.componentVisibleWidth - insets.left - getLeftInset();
            param2Int1 = Math.min(param2Int1, this.viewVisibleWidth);
          } 
        } else if (this.cachedViewPort != null) {
          JViewport jViewport = (JViewport)this.cachedViewPort.get();
          if (jViewport != null)
            jViewport.removeComponentListener(this); 
          this.cachedViewPort = null;
        } 
        super.layoutMinorAxis(param2Int1, param2Int2, param2ArrayOfInt1, param2ArrayOfInt2);
      }
      
      public void setParent(View param2View) {
        if (param2View == null && this.cachedViewPort != null) {
          Object object;
          if ((object = this.cachedViewPort.get()) != null)
            ((JComponent)object).removeComponentListener(this); 
          this.cachedViewPort = null;
        } 
        super.setParent(param2View);
      }
      
      public void componentResized(ComponentEvent param2ComponentEvent) {
        if (!(param2ComponentEvent.getSource() instanceof JViewport))
          return; 
        JViewport jViewport = (JViewport)param2ComponentEvent.getSource();
        if (this.componentVisibleWidth != (jViewport.getExtentSize()).width) {
          Document document = getDocument();
          if (document instanceof AbstractDocument) {
            abstractDocument = (AbstractDocument)getDocument();
            abstractDocument.readLock();
            try {
              layoutChanged(0);
              preferenceChanged(null, true, true);
            } finally {
              abstractDocument.readUnlock();
            } 
          } 
        } 
      }
      
      public void componentHidden(ComponentEvent param2ComponentEvent) {}
      
      public void componentMoved(ComponentEvent param2ComponentEvent) {}
      
      public void componentShown(ComponentEvent param2ComponentEvent) {}
    }
  }
  
  public static abstract class HTMLTextAction extends StyledEditorKit.StyledTextAction {
    public HTMLTextAction(String param1String) { super(param1String); }
    
    protected HTMLDocument getHTMLDocument(JEditorPane param1JEditorPane) {
      Document document = param1JEditorPane.getDocument();
      if (document instanceof HTMLDocument)
        return (HTMLDocument)document; 
      throw new IllegalArgumentException("document must be HTMLDocument");
    }
    
    protected HTMLEditorKit getHTMLEditorKit(JEditorPane param1JEditorPane) {
      EditorKit editorKit = param1JEditorPane.getEditorKit();
      if (editorKit instanceof HTMLEditorKit)
        return (HTMLEditorKit)editorKit; 
      throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
    }
    
    protected Element[] getElementsAt(HTMLDocument param1HTMLDocument, int param1Int) { return getElementsAt(param1HTMLDocument.getDefaultRootElement(), param1Int, 0); }
    
    private Element[] getElementsAt(Element param1Element, int param1Int1, int param1Int2) {
      if (param1Element.isLeaf()) {
        Element[] arrayOfElement1 = new Element[param1Int2 + 1];
        arrayOfElement1[param1Int2] = param1Element;
        return arrayOfElement1;
      } 
      Element[] arrayOfElement = getElementsAt(param1Element.getElement(param1Element.getElementIndex(param1Int1)), param1Int1, param1Int2 + 1);
      arrayOfElement[param1Int2] = param1Element;
      return arrayOfElement;
    }
    
    protected int elementCountToTag(HTMLDocument param1HTMLDocument, int param1Int, HTML.Tag param1Tag) {
      byte b = -1;
      Element element = param1HTMLDocument.getCharacterElement(param1Int);
      while (element != null && element.getAttributes().getAttribute(StyleConstants.NameAttribute) != param1Tag) {
        element = element.getParentElement();
        b++;
      } 
      return (element == null) ? -1 : b;
    }
    
    protected Element findElementMatchingTag(HTMLDocument param1HTMLDocument, int param1Int, HTML.Tag param1Tag) {
      Element element1 = param1HTMLDocument.getDefaultRootElement();
      Element element2 = null;
      while (element1 != null) {
        if (element1.getAttributes().getAttribute(StyleConstants.NameAttribute) == param1Tag)
          element2 = element1; 
        element1 = element1.getElement(element1.getElementIndex(param1Int));
      } 
      return element2;
    }
  }
  
  static class InsertHRAction extends InsertHTMLTextAction {
    InsertHRAction() { super("InsertHR", "<hr>", null, HTML.Tag.IMPLIED, null, null, false); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        HTMLDocument hTMLDocument = getHTMLDocument(jEditorPane);
        int i = jEditorPane.getSelectionStart();
        Element element = hTMLDocument.getParagraphElement(i);
        if (element.getParentElement() != null) {
          this.parentTag = (HTML.Tag)element.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
          super.actionPerformed(param1ActionEvent);
        } 
      } 
    }
  }
  
  public static class InsertHTMLTextAction extends HTMLTextAction {
    protected String html;
    
    protected HTML.Tag parentTag;
    
    protected HTML.Tag addTag;
    
    protected HTML.Tag alternateParentTag;
    
    protected HTML.Tag alternateAddTag;
    
    boolean adjustSelection;
    
    public InsertHTMLTextAction(String param1String1, String param1String2, HTML.Tag param1Tag1, HTML.Tag param1Tag2) { this(param1String1, param1String2, param1Tag1, param1Tag2, null, null); }
    
    public InsertHTMLTextAction(String param1String1, String param1String2, HTML.Tag param1Tag1, HTML.Tag param1Tag2, HTML.Tag param1Tag3, HTML.Tag param1Tag4) { this(param1String1, param1String2, param1Tag1, param1Tag2, param1Tag3, param1Tag4, true); }
    
    InsertHTMLTextAction(String param1String1, String param1String2, HTML.Tag param1Tag1, HTML.Tag param1Tag2, HTML.Tag param1Tag3, HTML.Tag param1Tag4, boolean param1Boolean) {
      super(param1String1);
      this.html = param1String2;
      this.parentTag = param1Tag1;
      this.addTag = param1Tag2;
      this.alternateParentTag = param1Tag3;
      this.alternateAddTag = param1Tag4;
      this.adjustSelection = param1Boolean;
    }
    
    protected void insertHTML(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, int param1Int1, String param1String, int param1Int2, int param1Int3, HTML.Tag param1Tag) {
      try {
        getHTMLEditorKit(param1JEditorPane).insertHTML(param1HTMLDocument, param1Int1, param1String, param1Int2, param1Int3, param1Tag);
      } catch (IOException iOException) {
        throw new RuntimeException("Unable to insert: " + iOException);
      } catch (BadLocationException badLocationException) {
        throw new RuntimeException("Unable to insert: " + badLocationException);
      } 
    }
    
    protected void insertAtBoundary(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, int param1Int, Element param1Element, String param1String, HTML.Tag param1Tag1, HTML.Tag param1Tag2) { insertAtBoundry(param1JEditorPane, param1HTMLDocument, param1Int, param1Element, param1String, param1Tag1, param1Tag2); }
    
    @Deprecated
    protected void insertAtBoundry(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, int param1Int, Element param1Element, String param1String, HTML.Tag param1Tag1, HTML.Tag param1Tag2) {
      Element element;
      boolean bool = (param1Int == 0) ? 1 : 0;
      if (param1Int > 0 || param1Element == null) {
        Element element1;
        for (element1 = param1HTMLDocument.getDefaultRootElement(); element1 != null && element1.getStartOffset() != param1Int && !element1.isLeaf(); element1 = element1.getElement(element1.getElementIndex(param1Int)));
        element = (element1 != null) ? element1.getParentElement() : null;
      } else {
        element = param1Element;
      } 
      if (element != null) {
        int i = 0;
        byte b = 0;
        if (bool && param1Element != null) {
          Element element1 = element;
          while (element1 != null && !element1.isLeaf()) {
            element1 = element1.getElement(element1.getElementIndex(param1Int));
            i++;
          } 
        } else {
          Element element1 = element;
          param1Int--;
          while (element1 != null && !element1.isLeaf()) {
            element1 = element1.getElement(element1.getElementIndex(param1Int));
            i++;
          } 
          element1 = element;
          param1Int++;
          while (element1 != null && element1 != param1Element) {
            element1 = element1.getElement(element1.getElementIndex(param1Int));
            b++;
          } 
        } 
        i = Math.max(0, i - 1);
        insertHTML(param1JEditorPane, param1HTMLDocument, param1Int, param1String, i, b, param1Tag2);
      } 
    }
    
    boolean insertIntoTag(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, int param1Int, HTML.Tag param1Tag1, HTML.Tag param1Tag2) {
      Element element = findElementMatchingTag(param1HTMLDocument, param1Int, param1Tag1);
      if (element != null && element.getStartOffset() == param1Int) {
        insertAtBoundary(param1JEditorPane, param1HTMLDocument, param1Int, element, this.html, param1Tag1, param1Tag2);
        return true;
      } 
      if (param1Int > 0) {
        int i = elementCountToTag(param1HTMLDocument, param1Int - 1, param1Tag1);
        if (i != -1) {
          insertHTML(param1JEditorPane, param1HTMLDocument, param1Int, this.html, i, 0, param1Tag2);
          return true;
        } 
      } 
      return false;
    }
    
    void adjustSelection(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, int param1Int1, int param1Int2) {
      int i = param1HTMLDocument.getLength();
      if (i != param1Int2 && param1Int1 < i)
        if (param1Int1 > 0) {
          Object object;
          try {
            object = param1HTMLDocument.getText(param1Int1 - 1, 1);
          } catch (BadLocationException badLocationException) {
            object = null;
          } 
          if (object != null && object.length() > 0 && object.charAt(0) == '\n') {
            param1JEditorPane.select(param1Int1, param1Int1);
          } else {
            param1JEditorPane.select(param1Int1 + 1, param1Int1 + 1);
          } 
        } else {
          param1JEditorPane.select(1, 1);
        }  
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        boolean bool;
        HTMLDocument hTMLDocument = getHTMLDocument(jEditorPane);
        int i = jEditorPane.getSelectionStart();
        int j = hTMLDocument.getLength();
        if (!insertIntoTag(jEditorPane, hTMLDocument, i, this.parentTag, this.addTag) && this.alternateParentTag != null) {
          bool = insertIntoTag(jEditorPane, hTMLDocument, i, this.alternateParentTag, this.alternateAddTag);
        } else {
          bool = true;
        } 
        if (this.adjustSelection && bool)
          adjustSelection(jEditorPane, hTMLDocument, i, j); 
      } 
    }
  }
  
  public static class LinkController extends MouseAdapter implements MouseMotionListener, Serializable {
    private Element curElem = null;
    
    private boolean curElemImage = false;
    
    private String href = null;
    
    private Position.Bias[] bias = new Position.Bias[1];
    
    private int curOffset;
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      JEditorPane jEditorPane = (JEditorPane)param1MouseEvent.getSource();
      if (!jEditorPane.isEditable() && jEditorPane.isEnabled() && SwingUtilities.isLeftMouseButton(param1MouseEvent)) {
        Point point = new Point(param1MouseEvent.getX(), param1MouseEvent.getY());
        int i = jEditorPane.viewToModel(point);
        if (i >= 0)
          activateLink(i, jEditorPane, param1MouseEvent); 
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      JEditorPane jEditorPane = (JEditorPane)param1MouseEvent.getSource();
      if (!jEditorPane.isEnabled())
        return; 
      HTMLEditorKit hTMLEditorKit = (HTMLEditorKit)jEditorPane.getEditorKit();
      boolean bool = true;
      Cursor cursor = hTMLEditorKit.getDefaultCursor();
      if (!jEditorPane.isEditable()) {
        Point point = new Point(param1MouseEvent.getX(), param1MouseEvent.getY());
        int i = jEditorPane.getUI().viewToModel(jEditorPane, point, this.bias);
        if (this.bias[false] == Position.Bias.Backward && i > 0)
          i--; 
        if (i >= 0 && jEditorPane.getDocument() instanceof HTMLDocument) {
          HTMLDocument hTMLDocument = (HTMLDocument)jEditorPane.getDocument();
          Element element = hTMLDocument.getCharacterElement(i);
          if (!doesElementContainLocation(jEditorPane, element, i, param1MouseEvent.getX(), param1MouseEvent.getY()))
            element = null; 
          if (this.curElem != element || this.curElemImage) {
            Element element1 = this.curElem;
            this.curElem = element;
            String str = null;
            this.curElemImage = false;
            if (element != null) {
              AttributeSet attributeSet1 = element.getAttributes();
              AttributeSet attributeSet2 = (AttributeSet)attributeSet1.getAttribute(HTML.Tag.A);
              if (attributeSet2 == null) {
                this.curElemImage = (attributeSet1.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.IMG);
                if (this.curElemImage)
                  str = getMapHREF(jEditorPane, hTMLDocument, element, attributeSet1, i, param1MouseEvent.getX(), param1MouseEvent.getY()); 
              } else {
                str = (String)attributeSet2.getAttribute(HTML.Attribute.HREF);
              } 
            } 
            if (str != this.href) {
              fireEvents(jEditorPane, hTMLDocument, str, element1, param1MouseEvent);
              this.href = str;
              if (str != null)
                cursor = hTMLEditorKit.getLinkCursor(); 
            } else {
              bool = false;
            } 
          } else {
            bool = false;
          } 
          this.curOffset = i;
        } 
      } 
      if (bool && jEditorPane.getCursor() != cursor)
        jEditorPane.setCursor(cursor); 
    }
    
    private String getMapHREF(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, Element param1Element, AttributeSet param1AttributeSet, int param1Int1, int param1Int2, int param1Int3) {
      Object object = param1AttributeSet.getAttribute(HTML.Attribute.USEMAP);
      if (object != null && object instanceof String) {
        Map map = param1HTMLDocument.getMap((String)object);
        if (map != null && param1Int1 < param1HTMLDocument.getLength()) {
          Object object1;
          TextUI textUI = param1JEditorPane.getUI();
          try {
            Rectangle rectangle1 = textUI.modelToView(param1JEditorPane, param1Int1, Position.Bias.Forward);
            Rectangle rectangle2 = textUI.modelToView(param1JEditorPane, param1Int1 + 1, Position.Bias.Backward);
            object1 = rectangle1.getBounds();
            object1.add((rectangle2 instanceof Rectangle) ? (Rectangle)rectangle2 : rectangle2.getBounds());
          } catch (BadLocationException badLocationException) {
            object1 = null;
          } 
          if (object1 != null) {
            AttributeSet attributeSet = map.getArea(param1Int2 - object1.x, param1Int3 - object1.y, object1.width, object1.height);
            if (attributeSet != null)
              return (String)attributeSet.getAttribute(HTML.Attribute.HREF); 
          } 
        } 
      } 
      return null;
    }
    
    private boolean doesElementContainLocation(JEditorPane param1JEditorPane, Element param1Element, int param1Int1, int param1Int2, int param1Int3) {
      if (param1Element != null && param1Int1 > 0 && param1Element.getStartOffset() == param1Int1)
        try {
          TextUI textUI = param1JEditorPane.getUI();
          Rectangle rectangle1 = textUI.modelToView(param1JEditorPane, param1Int1, Position.Bias.Forward);
          if (rectangle1 == null)
            return false; 
          Rectangle rectangle2 = (rectangle1 instanceof Rectangle) ? (Rectangle)rectangle1 : rectangle1.getBounds();
          Rectangle rectangle3 = textUI.modelToView(param1JEditorPane, param1Element.getEndOffset(), Position.Bias.Backward);
          if (rectangle3 != null) {
            Rectangle rectangle = (rectangle3 instanceof Rectangle) ? (Rectangle)rectangle3 : rectangle3.getBounds();
            rectangle2.add(rectangle);
          } 
          return rectangle2.contains(param1Int2, param1Int3);
        } catch (BadLocationException badLocationException) {} 
      return true;
    }
    
    protected void activateLink(int param1Int, JEditorPane param1JEditorPane) { activateLink(param1Int, param1JEditorPane, null); }
    
    void activateLink(int param1Int, JEditorPane param1JEditorPane, MouseEvent param1MouseEvent) {
      Document document = param1JEditorPane.getDocument();
      if (document instanceof HTMLDocument) {
        HTMLDocument hTMLDocument = (HTMLDocument)document;
        Element element = hTMLDocument.getCharacterElement(param1Int);
        AttributeSet attributeSet1 = element.getAttributes();
        AttributeSet attributeSet2 = (AttributeSet)attributeSet1.getAttribute(HTML.Tag.A);
        HyperlinkEvent hyperlinkEvent = null;
        int i = -1;
        int j = -1;
        if (param1MouseEvent != null) {
          i = param1MouseEvent.getX();
          j = param1MouseEvent.getY();
        } 
        if (attributeSet2 == null) {
          this.href = getMapHREF(param1JEditorPane, hTMLDocument, element, attributeSet1, param1Int, i, j);
        } else {
          this.href = (String)attributeSet2.getAttribute(HTML.Attribute.HREF);
        } 
        if (this.href != null)
          hyperlinkEvent = createHyperlinkEvent(param1JEditorPane, hTMLDocument, this.href, attributeSet2, element, param1MouseEvent); 
        if (hyperlinkEvent != null)
          param1JEditorPane.fireHyperlinkUpdate(hyperlinkEvent); 
      } 
    }
    
    HyperlinkEvent createHyperlinkEvent(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, String param1String, AttributeSet param1AttributeSet, Element param1Element, MouseEvent param1MouseEvent) {
      HTMLFrameHyperlinkEvent hTMLFrameHyperlinkEvent;
      URL uRL;
      try {
        hTMLFrameHyperlinkEvent = param1HTMLDocument.getBase();
        uRL = new URL(hTMLFrameHyperlinkEvent, param1String);
        if (param1String != null && "file".equals(uRL.getProtocol()) && param1String.startsWith("#")) {
          String str1 = hTMLFrameHyperlinkEvent.getFile();
          String str2 = uRL.getFile();
          if (str1 != null && str2 != null && !str2.startsWith(str1))
            uRL = new URL(hTMLFrameHyperlinkEvent, str1 + param1String); 
        } 
      } catch (MalformedURLException null) {
        uRL = null;
      } 
      if (!param1HTMLDocument.isFrameDocument()) {
        hTMLFrameHyperlinkEvent = new HyperlinkEvent(param1JEditorPane, HyperlinkEvent.EventType.ACTIVATED, uRL, param1String, param1Element, param1MouseEvent);
      } else {
        String str = (param1AttributeSet != null) ? (String)param1AttributeSet.getAttribute(HTML.Attribute.TARGET) : null;
        if (str == null || str.equals(""))
          str = param1HTMLDocument.getBaseTarget(); 
        if (str == null || str.equals(""))
          str = "_self"; 
        hTMLFrameHyperlinkEvent = new HTMLFrameHyperlinkEvent(param1JEditorPane, HyperlinkEvent.EventType.ACTIVATED, uRL, param1String, param1Element, param1MouseEvent, str);
      } 
      return hTMLFrameHyperlinkEvent;
    }
    
    void fireEvents(JEditorPane param1JEditorPane, HTMLDocument param1HTMLDocument, String param1String, Element param1Element, MouseEvent param1MouseEvent) {
      if (this.href != null) {
        URL uRL;
        try {
          uRL = new URL(param1HTMLDocument.getBase(), this.href);
        } catch (MalformedURLException malformedURLException) {
          uRL = null;
        } 
        HyperlinkEvent hyperlinkEvent = new HyperlinkEvent(param1JEditorPane, HyperlinkEvent.EventType.EXITED, uRL, this.href, param1Element, param1MouseEvent);
        param1JEditorPane.fireHyperlinkUpdate(hyperlinkEvent);
      } 
      if (param1String != null) {
        URL uRL;
        try {
          uRL = new URL(param1HTMLDocument.getBase(), param1String);
        } catch (MalformedURLException malformedURLException) {
          uRL = null;
        } 
        HyperlinkEvent hyperlinkEvent = new HyperlinkEvent(param1JEditorPane, HyperlinkEvent.EventType.ENTERED, uRL, param1String, this.curElem, param1MouseEvent);
        param1JEditorPane.fireHyperlinkUpdate(hyperlinkEvent);
      } 
    }
  }
  
  static class NavigateLinkAction extends TextAction implements CaretListener {
    private static final FocusHighlightPainter focusPainter = new FocusHighlightPainter(null);
    
    private final boolean focusBack;
    
    public NavigateLinkAction(String param1String) {
      super(param1String);
      this.focusBack = "previous-link-action".equals(param1String);
    }
    
    public void caretUpdate(CaretEvent param1CaretEvent) {
      Object object = param1CaretEvent.getSource();
      if (object instanceof JTextComponent) {
        JTextComponent jTextComponent = (JTextComponent)object;
        HTMLEditorKit hTMLEditorKit;
        if (hTMLEditorKit != null && hTMLEditorKit.foundLink) {
          hTMLEditorKit.foundLink = false;
          jTextComponent.getAccessibleContext().firePropertyChange("AccessibleHypertextOffset", Integer.valueOf(hTMLEditorKit.prevHypertextOffset), Integer.valueOf(param1CaretEvent.getDot()));
        } 
      } 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent == null || jTextComponent.isEditable())
        return; 
      Document document = jTextComponent.getDocument();
      HTMLEditorKit hTMLEditorKit = getHTMLEditorKit(jTextComponent);
      if (document == null || hTMLEditorKit == null)
        return; 
      ElementIterator elementIterator = new ElementIterator(document);
      int i = jTextComponent.getCaretPosition();
      int j = -1;
      int k;
      Element element;
      for (k = -1; (element = elementIterator.next()) != null; k = element.getEndOffset()) {
        String str = element.getName();
        AttributeSet attributeSet = element.getAttributes();
        Object object = HTMLEditorKit.getAttrValue(attributeSet, HTML.Attribute.HREF);
        if (!str.equals(HTML.Tag.OBJECT.toString()) && object == null)
          continue; 
        int m = element.getStartOffset();
        if (this.focusBack) {
          if (m >= i && j >= 0) {
            hTMLEditorKit.foundLink = true;
            jTextComponent.setCaretPosition(j);
            moveCaretPosition(jTextComponent, hTMLEditorKit, j, k);
            hTMLEditorKit.prevHypertextOffset = j;
            return;
          } 
        } else if (m > i) {
          hTMLEditorKit.foundLink = true;
          jTextComponent.setCaretPosition(m);
          moveCaretPosition(jTextComponent, hTMLEditorKit, m, element.getEndOffset());
          hTMLEditorKit.prevHypertextOffset = m;
          return;
        } 
        j = element.getStartOffset();
      } 
      if (this.focusBack && j >= 0) {
        hTMLEditorKit.foundLink = true;
        jTextComponent.setCaretPosition(j);
        moveCaretPosition(jTextComponent, hTMLEditorKit, j, k);
        hTMLEditorKit.prevHypertextOffset = j;
      } 
    }
    
    private void moveCaretPosition(JTextComponent param1JTextComponent, HTMLEditorKit param1HTMLEditorKit, int param1Int1, int param1Int2) {
      Highlighter highlighter = param1JTextComponent.getHighlighter();
      if (highlighter != null) {
        int i = Math.min(param1Int2, param1Int1);
        int j = Math.max(param1Int2, param1Int1);
        try {
          if (param1HTMLEditorKit.linkNavigationTag != null) {
            highlighter.changeHighlight(param1HTMLEditorKit.linkNavigationTag, i, j);
          } else {
            param1HTMLEditorKit.linkNavigationTag = highlighter.addHighlight(i, j, focusPainter);
          } 
        } catch (BadLocationException badLocationException) {}
      } 
    }
    
    private HTMLEditorKit getHTMLEditorKit(JTextComponent param1JTextComponent) {
      if (param1JTextComponent instanceof JEditorPane) {
        EditorKit editorKit = ((JEditorPane)param1JTextComponent).getEditorKit();
        if (editorKit instanceof HTMLEditorKit)
          return (HTMLEditorKit)editorKit; 
      } 
      return null;
    }
    
    static class FocusHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
      FocusHighlightPainter(Color param2Color) { super(param2Color); }
      
      public Shape paintLayer(Graphics param2Graphics, int param2Int1, int param2Int2, Shape param2Shape, JTextComponent param2JTextComponent, View param2View) {
        Color color = getColor();
        if (color == null) {
          param2Graphics.setColor(param2JTextComponent.getSelectionColor());
        } else {
          param2Graphics.setColor(color);
        } 
        if (param2Int1 == param2View.getStartOffset() && param2Int2 == param2View.getEndOffset()) {
          Rectangle rectangle;
          if (param2Shape instanceof Rectangle) {
            rectangle = (Rectangle)param2Shape;
          } else {
            rectangle = param2Shape.getBounds();
          } 
          param2Graphics.drawRect(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height);
          return rectangle;
        } 
        try {
          Shape shape = param2View.modelToView(param2Int1, Position.Bias.Forward, param2Int2, Position.Bias.Backward, param2Shape);
          Rectangle rectangle = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
          param2Graphics.drawRect(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height);
          return rectangle;
        } catch (BadLocationException badLocationException) {
          return null;
        } 
      }
    }
  }
  
  public static abstract class Parser {
    public abstract void parse(Reader param1Reader, HTMLEditorKit.ParserCallback param1ParserCallback, boolean param1Boolean) throws IOException;
  }
  
  public static class ParserCallback {
    public static final Object IMPLIED = "_implied_";
    
    public void flush() {}
    
    public void handleText(char[] param1ArrayOfChar, int param1Int) {}
    
    public void handleComment(char[] param1ArrayOfChar, int param1Int) {}
    
    public void handleStartTag(HTML.Tag param1Tag, MutableAttributeSet param1MutableAttributeSet, int param1Int) {}
    
    public void handleEndTag(HTML.Tag param1Tag, int param1Int) {}
    
    public void handleSimpleTag(HTML.Tag param1Tag, MutableAttributeSet param1MutableAttributeSet, int param1Int) {}
    
    public void handleError(String param1String, int param1Int) {}
    
    public void handleEndOfLineString(String param1String) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\HTMLEditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */