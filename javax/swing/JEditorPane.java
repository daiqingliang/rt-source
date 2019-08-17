package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleHyperlink;
import javax.accessibility.AccessibleHypertext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Caret;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.CompositeView;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.jd.gui.api.API;

public class JEditorPane extends JTextComponent {
  private SwingWorker<URL, Object> pageLoader;
  
  private EditorKit kit;
  
  private boolean isUserSetEditorKit;
  
  private Hashtable<String, Object> pageProperties;
  
  static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
  
  private Hashtable<String, EditorKit> typeHandlers;
  
  private static final Object kitRegistryKey = new StringBuffer("JEditorPane.kitRegistry");
  
  private static final Object kitTypeRegistryKey = new StringBuffer("JEditorPane.kitTypeRegistry");
  
  private static final Object kitLoaderRegistryKey = new StringBuffer("JEditorPane.kitLoaderRegistry");
  
  private static final String uiClassID = "EditorPaneUI";
  
  public static final String W3C_LENGTH_UNITS = "JEditorPane.w3cLengthUnits";
  
  public static final String HONOR_DISPLAY_PROPERTIES = "JEditorPane.honorDisplayProperties";
  
  static final Map<String, String> defaultEditorKitMap = new HashMap(0);
  
  public JEditorPane() {
    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
          public Component getComponentAfter(Container param1Container, Component param1Component) {
            if (param1Container != JEditorPane.this || (!JEditorPane.this.isEditable() && JEditorPane.this.getComponentCount() > 0))
              return super.getComponentAfter(param1Container, param1Component); 
            Container container = JEditorPane.this.getFocusCycleRootAncestor();
            return (container != null) ? container.getFocusTraversalPolicy().getComponentAfter(container, JEditorPane.this) : null;
          }
          
          public Component getComponentBefore(Container param1Container, Component param1Component) {
            if (param1Container != JEditorPane.this || (!JEditorPane.this.isEditable() && JEditorPane.this.getComponentCount() > 0))
              return super.getComponentBefore(param1Container, param1Component); 
            Container container = JEditorPane.this.getFocusCycleRootAncestor();
            return (container != null) ? container.getFocusTraversalPolicy().getComponentBefore(container, JEditorPane.this) : null;
          }
          
          public Component getDefaultComponent(Container param1Container) { return (param1Container != JEditorPane.this || (!JEditorPane.this.isEditable() && JEditorPane.this.getComponentCount() > 0)) ? super.getDefaultComponent(param1Container) : null; }
          
          protected boolean accept(Component param1Component) { return (param1Component != JEditorPane.this) ? super.accept(param1Component) : 0; }
        });
    LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
    LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
  }
  
  public JEditorPane(URL paramURL) throws IOException {
    this();
    setPage(paramURL);
  }
  
  public JEditorPane(String paramString) throws IOException {
    this();
    setPage(paramString);
  }
  
  public JEditorPane(String paramString1, String paramString2) {
    this();
    setContentType(paramString1);
    setText(paramString2);
  }
  
  public void addHyperlinkListener(HyperlinkListener paramHyperlinkListener) { this.listenerList.add(HyperlinkListener.class, paramHyperlinkListener); }
  
  public void removeHyperlinkListener(HyperlinkListener paramHyperlinkListener) { this.listenerList.remove(HyperlinkListener.class, paramHyperlinkListener); }
  
  public HyperlinkListener[] getHyperlinkListeners() { return (HyperlinkListener[])this.listenerList.getListeners(HyperlinkListener.class); }
  
  public void fireHyperlinkUpdate(HyperlinkEvent paramHyperlinkEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == HyperlinkListener.class)
        ((HyperlinkListener)arrayOfObject[i + 1]).hyperlinkUpdate(paramHyperlinkEvent); 
    } 
  }
  
  public void setPage(URL paramURL) throws IOException {
    if (paramURL == null)
      throw new IOException("invalid url"); 
    URL uRL = getPage();
    if (!paramURL.equals(uRL) && paramURL.getRef() == null)
      scrollRectToVisible(new Rectangle(0, 0, 1, 1)); 
    boolean bool = false;
    Object object = getPostData();
    if (uRL == null || !uRL.sameFile(paramURL) || object != null) {
      int i = getAsynchronousLoadPriority(getDocument());
      if (i < 0) {
        InputStream inputStream = getStream(paramURL);
        if (this.kit != null) {
          Document document = initializeModel(this.kit, paramURL);
          i = getAsynchronousLoadPriority(document);
          if (i >= 0) {
            setDocument(document);
            synchronized (this) {
              this.pageLoader = new PageLoader(document, inputStream, uRL, paramURL);
              this.pageLoader.execute();
            } 
            return;
          } 
          read(inputStream, document);
          setDocument(document);
          bool = true;
        } 
      } else {
        if (this.pageLoader != null)
          this.pageLoader.cancel(true); 
        this.pageLoader = new PageLoader(null, null, uRL, paramURL);
        this.pageLoader.execute();
        return;
      } 
    } 
    final String reference = paramURL.getRef();
    if (str != null) {
      if (!bool) {
        scrollToReference(str);
      } else {
        SwingUtilities.invokeLater(new Runnable() {
              public void run() { JEditorPane.this.scrollToReference(reference); }
            });
      } 
      getDocument().putProperty("stream", paramURL);
    } 
    firePropertyChange("page", uRL, paramURL);
  }
  
  private Document initializeModel(EditorKit paramEditorKit, URL paramURL) {
    Document document = paramEditorKit.createDefaultDocument();
    if (this.pageProperties != null) {
      Enumeration enumeration = this.pageProperties.keys();
      while (enumeration.hasMoreElements()) {
        String str = (String)enumeration.nextElement();
        document.putProperty(str, this.pageProperties.get(str));
      } 
      this.pageProperties.clear();
    } 
    if (document.getProperty("stream") == null)
      document.putProperty("stream", paramURL); 
    return document;
  }
  
  private int getAsynchronousLoadPriority(Document paramDocument) { return (paramDocument instanceof AbstractDocument) ? ((AbstractDocument)paramDocument).getAsynchronousLoadPriority() : -1; }
  
  public void read(InputStream paramInputStream, Object paramObject) throws IOException {
    if (paramObject instanceof HTMLDocument && this.kit instanceof HTMLEditorKit) {
      HTMLDocument hTMLDocument = (HTMLDocument)paramObject;
      setDocument(hTMLDocument);
      read(paramInputStream, hTMLDocument);
    } else {
      String str = (String)getClientProperty("charset");
      InputStreamReader inputStreamReader = (str != null) ? new InputStreamReader(paramInputStream, str) : new InputStreamReader(paramInputStream);
      read(inputStreamReader, paramObject);
    } 
  }
  
  void read(InputStream paramInputStream, Document paramDocument) throws IOException {
    if (!Boolean.TRUE.equals(paramDocument.getProperty("IgnoreCharsetDirective"))) {
      paramInputStream = new BufferedInputStream(paramInputStream, 10240);
      paramInputStream.mark(10240);
    } 
    try {
      String str = (String)getClientProperty("charset");
      InputStreamReader inputStreamReader = (str != null) ? new InputStreamReader(paramInputStream, str) : new InputStreamReader(paramInputStream);
      this.kit.read(inputStreamReader, paramDocument, 0);
    } catch (BadLocationException badLocationException) {
      throw new IOException(badLocationException.getMessage());
    } catch (ChangedCharSetException changedCharSetException) {
      String str = changedCharSetException.getCharSetSpec();
      if (changedCharSetException.keyEqualsCharSet()) {
        putClientProperty("charset", str);
      } else {
        setCharsetFromContentTypeParameters(str);
      } 
      try {
        paramInputStream.reset();
      } catch (IOException iOException) {
        paramInputStream.close();
        URL uRL = (URL)paramDocument.getProperty("stream");
        if (uRL != null) {
          URLConnection uRLConnection = uRL.openConnection();
          paramInputStream = uRLConnection.getInputStream();
        } else {
          throw changedCharSetException;
        } 
      } 
      try {
        paramDocument.remove(0, paramDocument.getLength());
      } catch (BadLocationException badLocationException) {}
      paramDocument.putProperty("IgnoreCharsetDirective", Boolean.valueOf(true));
      read(paramInputStream, paramDocument);
    } 
  }
  
  protected InputStream getStream(URL paramURL) throws IOException {
    final URLConnection conn = paramURL.openConnection();
    if (uRLConnection instanceof HttpURLConnection) {
      HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
      httpURLConnection.setInstanceFollowRedirects(false);
      Object object = getPostData();
      if (object != null)
        handlePostData(httpURLConnection, object); 
      int i = httpURLConnection.getResponseCode();
      boolean bool = (i >= 300 && i <= 399) ? 1 : 0;
      if (bool) {
        String str = uRLConnection.getHeaderField("Location");
        if (str.startsWith("http", 0)) {
          paramURL = new URL(str);
        } else {
          paramURL = new URL(paramURL, str);
        } 
        return getStream(paramURL);
      } 
    } 
    if (SwingUtilities.isEventDispatchThread()) {
      handleConnectionProperties(uRLConnection);
    } else {
      try {
        SwingUtilities.invokeAndWait(new Runnable() {
              public void run() { JEditorPane.this.handleConnectionProperties(conn); }
            });
      } catch (InterruptedException interruptedException) {
        throw new RuntimeException(interruptedException);
      } catch (InvocationTargetException invocationTargetException) {
        throw new RuntimeException(invocationTargetException);
      } 
    } 
    return uRLConnection.getInputStream();
  }
  
  private void handleConnectionProperties(URLConnection paramURLConnection) {
    if (this.pageProperties == null)
      this.pageProperties = new Hashtable(); 
    String str1 = paramURLConnection.getContentType();
    if (str1 != null) {
      setContentType(str1);
      this.pageProperties.put("content-type", str1);
    } 
    this.pageProperties.put("stream", paramURLConnection.getURL());
    String str2 = paramURLConnection.getContentEncoding();
    if (str2 != null)
      this.pageProperties.put("content-encoding", str2); 
  }
  
  private Object getPostData() { return getDocument().getProperty("javax.swing.JEditorPane.postdata"); }
  
  private void handlePostData(HttpURLConnection paramHttpURLConnection, Object paramObject) throws IOException {
    paramHttpURLConnection.setDoOutput(true);
    dataOutputStream = null;
    try {
      paramHttpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      dataOutputStream = new DataOutputStream(paramHttpURLConnection.getOutputStream());
      dataOutputStream.writeBytes((String)paramObject);
    } finally {
      if (dataOutputStream != null)
        dataOutputStream.close(); 
    } 
  }
  
  public void scrollToReference(String paramString) throws IOException {
    Document document = getDocument();
    if (document instanceof HTMLDocument) {
      HTMLDocument hTMLDocument = (HTMLDocument)document;
      HTMLDocument.Iterator iterator = hTMLDocument.getIterator(HTML.Tag.A);
      while (iterator.isValid()) {
        AttributeSet attributeSet = iterator.getAttributes();
        String str = (String)attributeSet.getAttribute(HTML.Attribute.NAME);
        if (str != null && str.equals(paramString))
          try {
            int i = iterator.getStartOffset();
            Rectangle rectangle = modelToView(i);
            if (rectangle != null) {
              Rectangle rectangle1 = getVisibleRect();
              rectangle.height = rectangle1.height;
              scrollRectToVisible(rectangle);
              setCaretPosition(i);
            } 
          } catch (BadLocationException badLocationException) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
          }  
        iterator.next();
      } 
    } 
  }
  
  public URL getPage() { return (URL)getDocument().getProperty("stream"); }
  
  public void setPage(String paramString) throws IOException {
    if (paramString == null)
      throw new IOException("invalid url"); 
    URL uRL = new URL(paramString);
    setPage(uRL);
  }
  
  public String getUIClassID() { return "EditorPaneUI"; }
  
  protected EditorKit createDefaultEditorKit() { return new PlainEditorKit(); }
  
  public EditorKit getEditorKit() {
    if (this.kit == null) {
      this.kit = createDefaultEditorKit();
      this.isUserSetEditorKit = false;
    } 
    return this.kit;
  }
  
  public final String getContentType() { return (this.kit != null) ? this.kit.getContentType() : null; }
  
  public final void setContentType(String paramString) throws IOException {
    int i = paramString.indexOf(";");
    if (i > -1) {
      String str = paramString.substring(i);
      paramString = paramString.substring(0, i).trim();
      if (paramString.toLowerCase().startsWith("text/"))
        setCharsetFromContentTypeParameters(str); 
    } 
    if (this.kit == null || !paramString.equals(this.kit.getContentType()) || !this.isUserSetEditorKit) {
      EditorKit editorKit = getEditorKitForContentType(paramString);
      if (editorKit != null && editorKit != this.kit) {
        setEditorKit(editorKit);
        this.isUserSetEditorKit = false;
      } 
    } 
  }
  
  private void setCharsetFromContentTypeParameters(String paramString) throws IOException {
    try {
      int i = paramString.indexOf(';');
      if (i > -1 && i < paramString.length() - 1)
        paramString = paramString.substring(i + 1); 
      if (paramString.length() > 0) {
        HeaderParser headerParser = new HeaderParser(paramString);
        String str = headerParser.findValue("charset");
        if (str != null)
          putClientProperty("charset", str); 
      } 
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
    
    } catch (NullPointerException nullPointerException) {
    
    } catch (Exception exception) {
      System.err.println("JEditorPane.getCharsetFromContentTypeParameters failed on: " + paramString);
      exception.printStackTrace();
    } 
  }
  
  public void setEditorKit(EditorKit paramEditorKit) {
    EditorKit editorKit = this.kit;
    this.isUserSetEditorKit = true;
    if (editorKit != null)
      editorKit.deinstall(this); 
    this.kit = paramEditorKit;
    if (this.kit != null) {
      this.kit.install(this);
      setDocument(this.kit.createDefaultDocument());
    } 
    firePropertyChange("editorKit", editorKit, paramEditorKit);
  }
  
  public EditorKit getEditorKitForContentType(String paramString) {
    if (this.typeHandlers == null)
      this.typeHandlers = new Hashtable(3); 
    EditorKit editorKit = (EditorKit)this.typeHandlers.get(paramString);
    if (editorKit == null) {
      editorKit = createEditorKitForContentType(paramString);
      if (editorKit != null)
        setEditorKitForContentType(paramString, editorKit); 
    } 
    if (editorKit == null)
      editorKit = createDefaultEditorKit(); 
    return editorKit;
  }
  
  public void setEditorKitForContentType(String paramString, EditorKit paramEditorKit) {
    if (this.typeHandlers == null)
      this.typeHandlers = new Hashtable(3); 
    this.typeHandlers.put(paramString, paramEditorKit);
  }
  
  public void replaceSelection(String paramString) throws IOException {
    if (!isEditable()) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
      return;
    } 
    EditorKit editorKit = getEditorKit();
    if (editorKit instanceof StyledEditorKit) {
      try {
        Document document = getDocument();
        Caret caret = getCaret();
        boolean bool = saveComposedText(caret.getDot());
        int i = Math.min(caret.getDot(), caret.getMark());
        int j = Math.max(caret.getDot(), caret.getMark());
        if (document instanceof AbstractDocument) {
          ((AbstractDocument)document).replace(i, j - i, paramString, ((StyledEditorKit)editorKit).getInputAttributes());
        } else {
          if (i != j)
            document.remove(i, j - i); 
          if (paramString != null && paramString.length() > 0)
            document.insertString(i, paramString, ((StyledEditorKit)editorKit).getInputAttributes()); 
        } 
        if (bool)
          restoreComposedText(); 
      } catch (BadLocationException badLocationException) {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
      } 
    } else {
      super.replaceSelection(paramString);
    } 
  }
  
  public static EditorKit createEditorKitForContentType(String paramString) {
    Hashtable hashtable = getKitRegisty();
    EditorKit editorKit = (EditorKit)hashtable.get(paramString);
    if (editorKit == null) {
      String str = (String)getKitTypeRegistry().get(paramString);
      ClassLoader classLoader = (ClassLoader)getKitLoaderRegistry().get(paramString);
      try {
        Class clazz;
        if (classLoader != null) {
          clazz = classLoader.loadClass(str);
        } else {
          clazz = Class.forName(str, true, Thread.currentThread().getContextClassLoader());
        } 
        editorKit = (EditorKit)clazz.newInstance();
        hashtable.put(paramString, editorKit);
      } catch (Throwable throwable) {
        editorKit = null;
      } 
    } 
    return (editorKit != null) ? (EditorKit)editorKit.clone() : null;
  }
  
  public static void registerEditorKitForContentType(String paramString1, String paramString2) { registerEditorKitForContentType(paramString1, paramString2, Thread.currentThread().getContextClassLoader()); }
  
  public static void registerEditorKitForContentType(String paramString1, String paramString2, ClassLoader paramClassLoader) {
    getKitTypeRegistry().put(paramString1, paramString2);
    if (paramClassLoader != null) {
      getKitLoaderRegistry().put(paramString1, paramClassLoader);
    } else {
      getKitLoaderRegistry().remove(paramString1);
    } 
    getKitRegisty().remove(paramString1);
  }
  
  public static String getEditorKitClassNameForContentType(String paramString) { return (String)getKitTypeRegistry().get(paramString); }
  
  private static Hashtable<String, String> getKitTypeRegistry() {
    loadDefaultKitsIfNecessary();
    return (Hashtable)SwingUtilities.appContextGet(kitTypeRegistryKey);
  }
  
  private static Hashtable<String, ClassLoader> getKitLoaderRegistry() {
    loadDefaultKitsIfNecessary();
    return (Hashtable)SwingUtilities.appContextGet(kitLoaderRegistryKey);
  }
  
  private static Hashtable<String, EditorKit> getKitRegisty() {
    Hashtable hashtable = (Hashtable)SwingUtilities.appContextGet(kitRegistryKey);
    if (hashtable == null) {
      hashtable = new Hashtable(3);
      SwingUtilities.appContextPut(kitRegistryKey, hashtable);
    } 
    return hashtable;
  }
  
  private static void loadDefaultKitsIfNecessary() {
    if (SwingUtilities.appContextGet(kitTypeRegistryKey) == null) {
      synchronized (defaultEditorKitMap) {
        if (defaultEditorKitMap.size() == 0) {
          defaultEditorKitMap.put("text/plain", "javax.swing.JEditorPane$PlainEditorKit");
          defaultEditorKitMap.put("text/html", "javax.swing.text.html.HTMLEditorKit");
          defaultEditorKitMap.put("text/rtf", "javax.swing.text.rtf.RTFEditorKit");
          defaultEditorKitMap.put("application/rtf", "javax.swing.text.rtf.RTFEditorKit");
        } 
      } 
      Hashtable hashtable = new Hashtable();
      SwingUtilities.appContextPut(kitTypeRegistryKey, hashtable);
      hashtable = new Hashtable();
      SwingUtilities.appContextPut(kitLoaderRegistryKey, hashtable);
      for (String str : defaultEditorKitMap.keySet())
        registerEditorKitForContentType(str, (String)defaultEditorKitMap.get(str)); 
    } 
  }
  
  public Dimension getPreferredSize() {
    Dimension dimension = super.getPreferredSize();
    Container container = SwingUtilities.getUnwrappedParent(this);
    if (container instanceof JViewport) {
      JViewport jViewport = (JViewport)container;
      TextUI textUI = getUI();
      int i = dimension.width;
      int j = dimension.height;
      if (!getScrollableTracksViewportWidth()) {
        int k = jViewport.getWidth();
        Dimension dimension1 = textUI.getMinimumSize(this);
        if (k != 0 && k < dimension1.width)
          i = dimension1.width; 
      } 
      if (!getScrollableTracksViewportHeight()) {
        int k = jViewport.getHeight();
        Dimension dimension1 = textUI.getMinimumSize(this);
        if (k != 0 && k < dimension1.height)
          j = dimension1.height; 
      } 
      if (i != dimension.width || j != dimension.height)
        dimension = new Dimension(i, j); 
    } 
    return dimension;
  }
  
  public void setText(String paramString) throws IOException {
    try {
      Document document = getDocument();
      document.remove(0, document.getLength());
      if (paramString == null || paramString.equals(""))
        return; 
      StringReader stringReader = new StringReader(paramString);
      EditorKit editorKit = getEditorKit();
      editorKit.read(stringReader, document, 0);
    } catch (IOException iOException) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } catch (BadLocationException badLocationException) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } 
  }
  
  public String getText() {
    String str;
    try {
      StringWriter stringWriter = new StringWriter();
      write(stringWriter);
      str = stringWriter.toString();
    } catch (IOException iOException) {
      str = null;
    } 
    return str;
  }
  
  public boolean getScrollableTracksViewportWidth() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    if (container instanceof JViewport) {
      JViewport jViewport = (JViewport)container;
      TextUI textUI = getUI();
      int i = jViewport.getWidth();
      Dimension dimension1 = textUI.getMinimumSize(this);
      Dimension dimension2 = textUI.getMaximumSize(this);
      if (i >= dimension1.width && i <= dimension2.width)
        return true; 
    } 
    return false;
  }
  
  public boolean getScrollableTracksViewportHeight() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    if (container instanceof JViewport) {
      JViewport jViewport = (JViewport)container;
      TextUI textUI = getUI();
      int i = jViewport.getHeight();
      Dimension dimension = textUI.getMinimumSize(this);
      if (i >= dimension.height) {
        Dimension dimension1 = textUI.getMaximumSize(this);
        if (i <= dimension1.height)
          return true; 
      } 
    } 
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("EditorPaneUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1 = (this.kit != null) ? this.kit.toString() : "";
    String str2 = (this.typeHandlers != null) ? this.typeHandlers.toString() : "";
    return super.paramString() + ",kit=" + str1 + ",typeHandlers=" + str2;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (getEditorKit() instanceof HTMLEditorKit) {
      if (this.accessibleContext == null || this.accessibleContext.getClass() != AccessibleJEditorPaneHTML.class)
        this.accessibleContext = new AccessibleJEditorPaneHTML(); 
    } else if (this.accessibleContext == null || this.accessibleContext.getClass() != AccessibleJEditorPane.class) {
      this.accessibleContext = new AccessibleJEditorPane();
    } 
    return this.accessibleContext;
  }
  
  protected class AccessibleJEditorPane extends JTextComponent.AccessibleJTextComponent {
    protected AccessibleJEditorPane() { super(JEditorPane.this); }
    
    public String getAccessibleDescription() {
      String str = this.accessibleDescription;
      if (str == null)
        str = (String)JEditorPane.this.getClientProperty("AccessibleDescription"); 
      if (str == null)
        str = JEditorPane.this.getContentType(); 
      return str;
    }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.MULTI_LINE);
      return accessibleStateSet;
    }
  }
  
  protected class AccessibleJEditorPaneHTML extends AccessibleJEditorPane {
    private AccessibleContext accessibleContext;
    
    public AccessibleText getAccessibleText() { return new JEditorPane.JEditorPaneAccessibleHypertextSupport(JEditorPane.this); }
    
    protected AccessibleJEditorPaneHTML() {
      super(JEditorPane.this);
      HTMLEditorKit hTMLEditorKit = (HTMLEditorKit)this$0.getEditorKit();
      this.accessibleContext = hTMLEditorKit.getAccessibleContext();
    }
    
    public int getAccessibleChildrenCount() { return (this.accessibleContext != null) ? this.accessibleContext.getAccessibleChildrenCount() : 0; }
    
    public Accessible getAccessibleChild(int param1Int) { return (this.accessibleContext != null) ? this.accessibleContext.getAccessibleChild(param1Int) : null; }
    
    public Accessible getAccessibleAt(Point param1Point) {
      if (this.accessibleContext != null && param1Point != null)
        try {
          AccessibleComponent accessibleComponent = this.accessibleContext.getAccessibleComponent();
          return (accessibleComponent != null) ? accessibleComponent.getAccessibleAt(param1Point) : null;
        } catch (IllegalComponentStateException illegalComponentStateException) {
          return null;
        }  
      return null;
    }
  }
  
  static class HeaderParser {
    String raw;
    
    String[][] tab;
    
    public HeaderParser(String param1String) throws IOException {
      this.raw = param1String;
      this.tab = new String[10][2];
      parse();
    }
    
    private void parse() {
      if (this.raw != null) {
        this.raw = this.raw.trim();
        char[] arrayOfChar = this.raw.toCharArray();
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        boolean bool1 = true;
        boolean bool2 = false;
        int i = arrayOfChar.length;
        while (b2 < i) {
          char c = arrayOfChar[b2];
          if (c == '=') {
            this.tab[b3][0] = (new String(arrayOfChar, b1, b2 - b1)).toLowerCase();
            bool1 = false;
            b1 = ++b2;
            continue;
          } 
          if (c == '"') {
            if (bool2) {
              this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1);
              bool2 = false;
              do {
              
              } while (++b2 < i && (arrayOfChar[b2] == ' ' || arrayOfChar[b2] == ','));
              bool1 = true;
              b1 = b2;
              continue;
            } 
            bool2 = true;
            b1 = ++b2;
            continue;
          } 
          if (c == ' ' || c == ',') {
            if (bool2) {
              b2++;
              continue;
            } 
            if (bool1) {
              this.tab[b3++][0] = (new String(arrayOfChar, b1, b2 - b1)).toLowerCase();
            } else {
              this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1);
            } 
            while (b2 < i && (arrayOfChar[b2] == ' ' || arrayOfChar[b2] == ','))
              b2++; 
            bool1 = true;
            b1 = b2;
            continue;
          } 
          b2++;
        } 
        if (--b2 > b1) {
          if (!bool1) {
            if (arrayOfChar[b2] == '"') {
              this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1);
            } else {
              this.tab[b3++][1] = new String(arrayOfChar, b1, b2 - b1 + 1);
            } 
          } else {
            this.tab[b3][0] = (new String(arrayOfChar, b1, b2 - b1 + 1)).toLowerCase();
          } 
        } else if (b2 == b1) {
          if (!bool1) {
            if (arrayOfChar[b2] == '"') {
              this.tab[b3++][1] = String.valueOf(arrayOfChar[b2 - 1]);
            } else {
              this.tab[b3++][1] = String.valueOf(arrayOfChar[b2]);
            } 
          } else {
            this.tab[b3][0] = String.valueOf(arrayOfChar[b2]).toLowerCase();
          } 
        } 
      } 
    }
    
    public String findKey(int param1Int) { return (param1Int < 0 || param1Int > 10) ? null : this.tab[param1Int][0]; }
    
    public String findValue(int param1Int) { return (param1Int < 0 || param1Int > 10) ? null : this.tab[param1Int][1]; }
    
    public String findValue(String param1String) { return findValue(param1String, null); }
    
    public String findValue(String param1String1, String param1String2) {
      if (param1String1 == null)
        return param1String2; 
      param1String1 = param1String1.toLowerCase();
      for (byte b = 0; b < 10; b++) {
        if (this.tab[b][false] == null)
          return param1String2; 
        if (param1String1.equals(this.tab[b][0]))
          return this.tab[b][1]; 
      } 
      return param1String2;
    }
    
    public int findInt(String param1String, int param1Int) {
      try {
        return Integer.parseInt(findValue(param1String, String.valueOf(param1Int)));
      } catch (Throwable throwable) {
        return param1Int;
      } 
    }
  }
  
  protected class JEditorPaneAccessibleHypertextSupport extends AccessibleJEditorPane implements AccessibleHypertext {
    LinkVector hyperlinks = new LinkVector(null);
    
    boolean linksValid = false;
    
    private void buildLinkTable() {
      this.hyperlinks.removeAllElements();
      Document document = JEditorPane.this.getDocument();
      if (document != null) {
        ElementIterator elementIterator = new ElementIterator(document);
        Element element;
        while ((element = elementIterator.next()) != null) {
          if (element.isLeaf()) {
            AttributeSet attributeSet1 = element.getAttributes();
            AttributeSet attributeSet2 = (AttributeSet)attributeSet1.getAttribute(HTML.Tag.A);
            String str = (attributeSet2 != null) ? (String)attributeSet2.getAttribute(HTML.Attribute.HREF) : null;
            if (str != null)
              this.hyperlinks.addElement(new HTMLLink(element)); 
          } 
        } 
      } 
      this.linksValid = true;
    }
    
    public JEditorPaneAccessibleHypertextSupport() {
      super(JEditorPane.this);
      Document document = this$0.getDocument();
      if (document != null)
        document.addDocumentListener(new DocumentListener() {
              public void changedUpdate(DocumentEvent param2DocumentEvent) { JEditorPane.JEditorPaneAccessibleHypertextSupport.this.linksValid = false; }
              
              public void insertUpdate(DocumentEvent param2DocumentEvent) { JEditorPane.JEditorPaneAccessibleHypertextSupport.this.linksValid = false; }
              
              public void removeUpdate(DocumentEvent param2DocumentEvent) { JEditorPane.JEditorPaneAccessibleHypertextSupport.this.linksValid = false; }
            }); 
    }
    
    public int getLinkCount() {
      if (!this.linksValid)
        buildLinkTable(); 
      return this.hyperlinks.size();
    }
    
    public int getLinkIndex(int param1Int) {
      if (!this.linksValid)
        buildLinkTable(); 
      Element element = null;
      Document document = JEditorPane.this.getDocument();
      if (document != null)
        for (element = document.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(i))
          int i = element.getElementIndex(param1Int);  
      return this.hyperlinks.baseElementIndex(element);
    }
    
    public AccessibleHyperlink getLink(int param1Int) {
      if (!this.linksValid)
        buildLinkTable(); 
      return (param1Int >= 0 && param1Int < this.hyperlinks.size()) ? (AccessibleHyperlink)this.hyperlinks.elementAt(param1Int) : null;
    }
    
    public String getLinkText(int param1Int) {
      if (!this.linksValid)
        buildLinkTable(); 
      Element element = (Element)this.hyperlinks.elementAt(param1Int);
      if (element != null) {
        Document document = JEditorPane.this.getDocument();
        if (document != null)
          try {
            return document.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
          } catch (BadLocationException badLocationException) {
            return null;
          }  
      } 
      return null;
    }
    
    public class HTMLLink extends AccessibleHyperlink {
      Element element;
      
      public HTMLLink(Element param2Element) { this.element = param2Element; }
      
      public boolean isValid() { return JEditorPane.JEditorPaneAccessibleHypertextSupport.this.linksValid; }
      
      public int getAccessibleActionCount() { return 1; }
      
      public boolean doAccessibleAction(int param2Int) {
        if (param2Int == 0 && isValid() == true) {
          URL uRL = (URL)getAccessibleActionObject(param2Int);
          if (uRL != null) {
            HyperlinkEvent hyperlinkEvent = new HyperlinkEvent(JEditorPane.JEditorPaneAccessibleHypertextSupport.this.this$0, HyperlinkEvent.EventType.ACTIVATED, uRL);
            JEditorPane.JEditorPaneAccessibleHypertextSupport.this.this$0.fireHyperlinkUpdate(hyperlinkEvent);
            return true;
          } 
        } 
        return false;
      }
      
      public String getAccessibleActionDescription(int param2Int) {
        if (param2Int == 0 && isValid() == true) {
          Document document = JEditorPane.JEditorPaneAccessibleHypertextSupport.this.this$0.getDocument();
          if (document != null)
            try {
              return document.getText(getStartIndex(), getEndIndex() - getStartIndex());
            } catch (BadLocationException badLocationException) {
              return null;
            }  
        } 
        return null;
      }
      
      public Object getAccessibleActionObject(int param2Int) {
        if (param2Int == 0 && isValid() == true) {
          AttributeSet attributeSet1 = this.element.getAttributes();
          AttributeSet attributeSet2 = (AttributeSet)attributeSet1.getAttribute(HTML.Tag.A);
          String str = (attributeSet2 != null) ? (String)attributeSet2.getAttribute(HTML.Attribute.HREF) : null;
          if (str != null) {
            Object object;
            try {
              object = new URL(JEditorPane.JEditorPaneAccessibleHypertextSupport.this.this$0.getPage(), str);
            } catch (MalformedURLException malformedURLException) {
              object = null;
            } 
            return object;
          } 
        } 
        return null;
      }
      
      public Object getAccessibleActionAnchor(int param2Int) { return getAccessibleActionDescription(param2Int); }
      
      public int getStartIndex() { return this.element.getStartOffset(); }
      
      public int getEndIndex() { return this.element.getEndOffset(); }
    }
    
    private class LinkVector extends Vector<HTMLLink> {
      private LinkVector() {}
      
      public int baseElementIndex(Element param2Element) {
        for (byte b = 0; b < this.elementCount; b++) {
          JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink hTMLLink = (JEditorPane.JEditorPaneAccessibleHypertextSupport.HTMLLink)elementAt(b);
          if (hTMLLink.element == param2Element)
            return b; 
        } 
        return -1;
      }
    }
  }
  
  class PageLoader extends SwingWorker<URL, Object> {
    InputStream in;
    
    URL old;
    
    URL page;
    
    Document doc;
    
    PageLoader(Document param1Document, InputStream param1InputStream, URL param1URL1, URL param1URL2) {
      this.in = param1InputStream;
      this.old = param1URL1;
      this.page = param1URL2;
      this.doc = param1Document;
    }
    
    protected URL doInBackground() {
      bool = false;
      try {
        if (this.in == null) {
          this.in = JEditorPane.this.getStream(this.page);
          if (JEditorPane.this.kit == null) {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
            return bool ? this.page : this.old;
          } 
        } 
        if (this.doc == null)
          try {
            SwingUtilities.invokeAndWait(new Runnable() {
                  public void run() {
                    JEditorPane.PageLoader.this.doc = JEditorPane.PageLoader.this.this$0.initializeModel(JEditorPane.PageLoader.this.this$0.kit, JEditorPane.PageLoader.this.page);
                    JEditorPane.PageLoader.this.this$0.setDocument(JEditorPane.PageLoader.this.doc);
                  }
                });
          } catch (InvocationTargetException invocationTargetException) {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
            return bool ? this.page : this.old;
          } catch (InterruptedException interruptedException) {
            UIManager.getLookAndFeel().provideErrorFeedback(JEditorPane.this);
            return bool ? this.page : this.old;
          }  
        JEditorPane.this.read(this.in, this.doc);
        URL uRL = (URL)this.doc.getProperty("stream");
        String str = uRL.getRef();
        if (str != null) {
          Runnable runnable = new Runnable() {
              public void run() {
                URL uRL = (URL)JEditorPane.PageLoader.this.this$0.getDocument().getProperty("stream");
                String str = uRL.getRef();
                JEditorPane.PageLoader.this.this$0.scrollToReference(str);
              }
            };
          SwingUtilities.invokeLater(runnable);
        } 
        return bool ? this.page : this.old;
      } catch (IOException iOException) {
        return bool ? this.page : this.old;
      } finally {
        API aPI = null;
        if (bool)
          SwingUtilities.invokeLater(new Runnable() {
                public void run() { JEditorPane.PageLoader.this.this$0.firePropertyChange("page", JEditorPane.PageLoader.this.old, JEditorPane.PageLoader.this.page); }
              }); 
      } 
    }
  }
  
  static class PlainEditorKit extends DefaultEditorKit implements ViewFactory {
    public ViewFactory getViewFactory() { return this; }
    
    public View create(Element param1Element) {
      Document document = param1Element.getDocument();
      Object object = document.getProperty("i18n");
      return (object != null && object.equals(Boolean.TRUE)) ? createI18N(param1Element) : new WrappedPlainView(param1Element);
    }
    
    View createI18N(Element param1Element) {
      String str = param1Element.getName();
      if (str != null) {
        if (str.equals("content"))
          return new PlainParagraph(param1Element); 
        if (str.equals("paragraph"))
          return new BoxView(param1Element, 1); 
      } 
      return null;
    }
    
    static class PlainParagraph extends ParagraphView {
      PlainParagraph(Element param2Element) {
        super(param2Element);
        this.layoutPool = new LogicalView(param2Element);
        this.layoutPool.setParent(this);
      }
      
      protected void setPropertiesFromAttributes() {
        Container container = getContainer();
        if (container != null && !container.getComponentOrientation().isLeftToRight()) {
          setJustification(2);
        } else {
          setJustification(0);
        } 
      }
      
      public int getFlowSpan(int param2Int) {
        Container container = getContainer();
        if (container instanceof JTextArea) {
          JTextArea jTextArea = (JTextArea)container;
          if (!jTextArea.getLineWrap())
            return Integer.MAX_VALUE; 
        } 
        return super.getFlowSpan(param2Int);
      }
      
      protected SizeRequirements calculateMinorAxisRequirements(int param2Int, SizeRequirements param2SizeRequirements) {
        SizeRequirements sizeRequirements = super.calculateMinorAxisRequirements(param2Int, param2SizeRequirements);
        Container container = getContainer();
        if (container instanceof JTextArea) {
          JTextArea jTextArea = (JTextArea)container;
          if (!jTextArea.getLineWrap())
            sizeRequirements.minimum = sizeRequirements.preferred; 
        } 
        return sizeRequirements;
      }
      
      static class LogicalView extends CompositeView {
        LogicalView(Element param3Element) { super(param3Element); }
        
        protected int getViewIndexAtPosition(int param3Int) {
          Element element = getElement();
          return (element.getElementCount() > 0) ? element.getElementIndex(param3Int) : 0;
        }
        
        protected boolean updateChildren(DocumentEvent.ElementChange param3ElementChange, DocumentEvent param3DocumentEvent, ViewFactory param3ViewFactory) { return false; }
        
        protected void loadChildren(ViewFactory param3ViewFactory) {
          Element element = getElement();
          if (element.getElementCount() > 0) {
            super.loadChildren(param3ViewFactory);
          } else {
            GlyphView glyphView = new GlyphView(element);
            append(glyphView);
          } 
        }
        
        public float getPreferredSpan(int param3Int) {
          if (getViewCount() != 1)
            throw new Error("One child view is assumed."); 
          View view = getView(0);
          return view.getPreferredSpan(param3Int);
        }
        
        protected void forwardUpdateToView(View param3View, DocumentEvent param3DocumentEvent, Shape param3Shape, ViewFactory param3ViewFactory) {
          param3View.setParent(this);
          super.forwardUpdateToView(param3View, param3DocumentEvent, param3Shape, param3ViewFactory);
        }
        
        public void paint(Graphics param3Graphics, Shape param3Shape) {}
        
        protected boolean isBefore(int param3Int1, int param3Int2, Rectangle param3Rectangle) { return false; }
        
        protected boolean isAfter(int param3Int1, int param3Int2, Rectangle param3Rectangle) { return false; }
        
        protected View getViewAtPoint(int param3Int1, int param3Int2, Rectangle param3Rectangle) { return null; }
        
        protected void childAllocation(int param3Int, Rectangle param3Rectangle) {}
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JEditorPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */