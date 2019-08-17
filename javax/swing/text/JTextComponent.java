package javax.swing.text;

import com.sun.beans.util.Cache;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleExtendedText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleTextSequence;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import sun.awt.AppContext;
import sun.swing.PrintingStatus;
import sun.swing.SwingAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.text.TextComponentPrintable;

public abstract class JTextComponent extends JComponent implements Scrollable, Accessible {
  public static final String FOCUS_ACCELERATOR_KEY = "focusAcceleratorKey";
  
  private Document model;
  
  private Caret caret;
  
  private NavigationFilter navigationFilter;
  
  private Highlighter highlighter;
  
  private Keymap keymap;
  
  private MutableCaretEvent caretEvent;
  
  private Color caretColor;
  
  private Color selectionColor;
  
  private Color selectedTextColor;
  
  private Color disabledTextColor;
  
  private boolean editable;
  
  private Insets margin;
  
  private char focusAccelerator;
  
  private boolean dragEnabled;
  
  private DropMode dropMode = DropMode.USE_SELECTION;
  
  private DropLocation dropLocation;
  
  private static DefaultTransferHandler defaultTransferHandler;
  
  private static Cache<Class<?>, Boolean> METHOD_OVERRIDDEN;
  
  private static final Object KEYMAP_TABLE;
  
  private InputMethodRequests inputMethodRequestsHandler;
  
  private SimpleAttributeSet composedTextAttribute;
  
  private String composedTextContent;
  
  private Position composedTextStart;
  
  private Position composedTextEnd;
  
  private Position latestCommittedTextStart;
  
  private Position latestCommittedTextEnd;
  
  private ComposedTextCaret composedTextCaret;
  
  private Caret originalCaret;
  
  private boolean checkedInputOverride;
  
  private boolean needToSendKeyTypedEvent;
  
  private static final Object FOCUSED_COMPONENT;
  
  public static final String DEFAULT_KEYMAP = "default";
  
  public JTextComponent() {
    enableEvents(2056L);
    this.caretEvent = new MutableCaretEvent(this);
    addMouseListener(this.caretEvent);
    addFocusListener(this.caretEvent);
    setEditable(true);
    setDragEnabled(false);
    setLayout(null);
    updateUI();
  }
  
  public TextUI getUI() { return (TextUI)this.ui; }
  
  public void setUI(TextUI paramTextUI) { setUI(paramTextUI); }
  
  public void updateUI() {
    setUI((TextUI)UIManager.getUI(this));
    invalidate();
  }
  
  public void addCaretListener(CaretListener paramCaretListener) { this.listenerList.add(CaretListener.class, paramCaretListener); }
  
  public void removeCaretListener(CaretListener paramCaretListener) { this.listenerList.remove(CaretListener.class, paramCaretListener); }
  
  public CaretListener[] getCaretListeners() { return (CaretListener[])this.listenerList.getListeners(CaretListener.class); }
  
  protected void fireCaretUpdate(CaretEvent paramCaretEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == CaretListener.class)
        ((CaretListener)arrayOfObject[i + 1]).caretUpdate(paramCaretEvent); 
    } 
  }
  
  public void setDocument(Document paramDocument) {
    document = this.model;
    try {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readLock(); 
      if (this.accessibleContext != null)
        this.model.removeDocumentListener((AccessibleJTextComponent)this.accessibleContext); 
      if (this.inputMethodRequestsHandler != null)
        this.model.removeDocumentListener((DocumentListener)this.inputMethodRequestsHandler); 
      this.model = paramDocument;
      Boolean bool = getComponentOrientation().isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
      if (bool != paramDocument.getProperty(TextAttribute.RUN_DIRECTION))
        paramDocument.putProperty(TextAttribute.RUN_DIRECTION, bool); 
      firePropertyChange("document", document, paramDocument);
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    revalidate();
    repaint();
    if (this.accessibleContext != null)
      this.model.addDocumentListener((AccessibleJTextComponent)this.accessibleContext); 
    if (this.inputMethodRequestsHandler != null)
      this.model.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler); 
  }
  
  public Document getDocument() { return this.model; }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation) {
    Document document = getDocument();
    if (document != null) {
      Boolean bool = paramComponentOrientation.isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
      document.putProperty(TextAttribute.RUN_DIRECTION, bool);
    } 
    super.setComponentOrientation(paramComponentOrientation);
  }
  
  public Action[] getActions() { return getUI().getEditorKit(this).getActions(); }
  
  public void setMargin(Insets paramInsets) {
    Insets insets = this.margin;
    this.margin = paramInsets;
    firePropertyChange("margin", insets, paramInsets);
    invalidate();
  }
  
  public Insets getMargin() { return this.margin; }
  
  public void setNavigationFilter(NavigationFilter paramNavigationFilter) { this.navigationFilter = paramNavigationFilter; }
  
  public NavigationFilter getNavigationFilter() { return this.navigationFilter; }
  
  @Transient
  public Caret getCaret() { return this.caret; }
  
  public void setCaret(Caret paramCaret) {
    if (this.caret != null) {
      this.caret.removeChangeListener(this.caretEvent);
      this.caret.deinstall(this);
    } 
    Caret caret1 = this.caret;
    this.caret = paramCaret;
    if (this.caret != null) {
      this.caret.install(this);
      this.caret.addChangeListener(this.caretEvent);
    } 
    firePropertyChange("caret", caret1, this.caret);
  }
  
  public Highlighter getHighlighter() { return this.highlighter; }
  
  public void setHighlighter(Highlighter paramHighlighter) {
    if (this.highlighter != null)
      this.highlighter.deinstall(this); 
    Highlighter highlighter1 = this.highlighter;
    this.highlighter = paramHighlighter;
    if (this.highlighter != null)
      this.highlighter.install(this); 
    firePropertyChange("highlighter", highlighter1, paramHighlighter);
  }
  
  public void setKeymap(Keymap paramKeymap) {
    Keymap keymap1 = this.keymap;
    this.keymap = paramKeymap;
    firePropertyChange("keymap", keymap1, this.keymap);
    updateInputMap(keymap1, paramKeymap);
  }
  
  public void setDragEnabled(boolean paramBoolean) {
    if (paramBoolean && GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    this.dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled() { return this.dragEnabled; }
  
  public final void setDropMode(DropMode paramDropMode) {
    if (paramDropMode != null)
      switch (paramDropMode) {
        case USE_SELECTION:
        case INSERT:
          this.dropMode = paramDropMode;
          return;
      }  
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for text");
  }
  
  public final DropMode getDropMode() { return this.dropMode; }
  
  DropLocation dropLocationForPoint(Point paramPoint) {
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = getUI().viewToModel(this, paramPoint, arrayOfBias);
    if (arrayOfBias[false] == null)
      arrayOfBias[0] = Position.Bias.Forward; 
    return new DropLocation(paramPoint, i, arrayOfBias[0], null);
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean) {
    Object object = null;
    DropLocation dropLocation1 = (DropLocation)paramDropLocation;
    if (this.dropMode == DropMode.USE_SELECTION) {
      if (dropLocation1 == null) {
        if (paramObject != null) {
          Object[] arrayOfObject = (Object[])paramObject;
          if (!paramBoolean)
            if (this.caret instanceof DefaultCaret) {
              ((DefaultCaret)this.caret).setDot(((Integer)arrayOfObject[0]).intValue(), (Position.Bias)arrayOfObject[3]);
              ((DefaultCaret)this.caret).moveDot(((Integer)arrayOfObject[1]).intValue(), (Position.Bias)arrayOfObject[4]);
            } else {
              this.caret.setDot(((Integer)arrayOfObject[0]).intValue());
              this.caret.moveDot(((Integer)arrayOfObject[1]).intValue());
            }  
          this.caret.setVisible(((Boolean)arrayOfObject[2]).booleanValue());
        } 
      } else {
        if (this.dropLocation == null) {
          if (this.caret instanceof DefaultCaret) {
            DefaultCaret defaultCaret = (DefaultCaret)this.caret;
            boolean bool = defaultCaret.isActive();
            object = new Object[] { Integer.valueOf(defaultCaret.getMark()), Integer.valueOf(defaultCaret.getDot()), Boolean.valueOf(bool), defaultCaret.getMarkBias(), defaultCaret.getDotBias() };
          } else {
            boolean bool = this.caret.isVisible();
            object = new Object[] { Integer.valueOf(this.caret.getMark()), Integer.valueOf(this.caret.getDot()), Boolean.valueOf(bool) };
          } 
          this.caret.setVisible(true);
        } else {
          object = paramObject;
        } 
        if (this.caret instanceof DefaultCaret) {
          ((DefaultCaret)this.caret).setDot(dropLocation1.getIndex(), dropLocation1.getBias());
        } else {
          this.caret.setDot(dropLocation1.getIndex());
        } 
      } 
    } else if (dropLocation1 == null) {
      if (paramObject != null)
        this.caret.setVisible(((Boolean)paramObject).booleanValue()); 
    } else if (this.dropLocation == null) {
      boolean bool1 = (this.caret instanceof DefaultCaret) ? ((DefaultCaret)this.caret).isActive() : this.caret.isVisible();
      Boolean bool = Boolean.valueOf(bool1);
      this.caret.setVisible(false);
    } else {
      object = paramObject;
    } 
    DropLocation dropLocation2 = this.dropLocation;
    this.dropLocation = dropLocation1;
    firePropertyChange("dropLocation", dropLocation2, this.dropLocation);
    return object;
  }
  
  public final DropLocation getDropLocation() { return this.dropLocation; }
  
  void updateInputMap(Keymap paramKeymap1, Keymap paramKeymap2) {
    InputMap inputMap1 = getInputMap(0);
    InputMap inputMap2 = inputMap1;
    while (inputMap1 != null && !(inputMap1 instanceof KeymapWrapper)) {
      inputMap2 = inputMap1;
      inputMap1 = inputMap1.getParent();
    } 
    if (inputMap1 != null) {
      if (paramKeymap2 == null) {
        if (inputMap2 != inputMap1) {
          inputMap2.setParent(inputMap1.getParent());
        } else {
          inputMap2.setParent(null);
        } 
      } else {
        KeymapWrapper keymapWrapper = new KeymapWrapper(paramKeymap2);
        inputMap2.setParent(keymapWrapper);
        if (inputMap2 != inputMap1)
          keymapWrapper.setParent(inputMap1.getParent()); 
      } 
    } else if (paramKeymap2 != null) {
      inputMap1 = getInputMap(0);
      if (inputMap1 != null) {
        KeymapWrapper keymapWrapper = new KeymapWrapper(paramKeymap2);
        keymapWrapper.setParent(inputMap1.getParent());
        inputMap1.setParent(keymapWrapper);
      } 
    } 
    ActionMap actionMap1 = getActionMap();
    ActionMap actionMap2 = actionMap1;
    while (actionMap1 != null && !(actionMap1 instanceof KeymapActionMap)) {
      actionMap2 = actionMap1;
      actionMap1 = actionMap1.getParent();
    } 
    if (actionMap1 != null) {
      if (paramKeymap2 == null) {
        if (actionMap2 != actionMap1) {
          actionMap2.setParent(actionMap1.getParent());
        } else {
          actionMap2.setParent(null);
        } 
      } else {
        KeymapActionMap keymapActionMap = new KeymapActionMap(paramKeymap2);
        actionMap2.setParent(keymapActionMap);
        if (actionMap2 != actionMap1)
          keymapActionMap.setParent(actionMap1.getParent()); 
      } 
    } else if (paramKeymap2 != null) {
      actionMap1 = getActionMap();
      if (actionMap1 != null) {
        KeymapActionMap keymapActionMap = new KeymapActionMap(paramKeymap2);
        keymapActionMap.setParent(actionMap1.getParent());
        actionMap1.setParent(keymapActionMap);
      } 
    } 
  }
  
  public Keymap getKeymap() { return this.keymap; }
  
  public static Keymap addKeymap(String paramString, Keymap paramKeymap) {
    DefaultKeymap defaultKeymap = new DefaultKeymap(paramString, paramKeymap);
    if (paramString != null)
      getKeymapTable().put(paramString, defaultKeymap); 
    return defaultKeymap;
  }
  
  public static Keymap removeKeymap(String paramString) { return (Keymap)getKeymapTable().remove(paramString); }
  
  public static Keymap getKeymap(String paramString) { return (Keymap)getKeymapTable().get(paramString); }
  
  private static HashMap<String, Keymap> getKeymapTable() {
    synchronized (KEYMAP_TABLE) {
      AppContext appContext = AppContext.getAppContext();
      HashMap hashMap = (HashMap)appContext.get(KEYMAP_TABLE);
      if (hashMap == null) {
        hashMap = new HashMap(17);
        appContext.put(KEYMAP_TABLE, hashMap);
        Keymap keymap1 = addKeymap("default", null);
        keymap1.setDefaultAction(new DefaultEditorKit.DefaultKeyTypedAction());
      } 
      return hashMap;
    } 
  }
  
  public static void loadKeymap(Keymap paramKeymap, KeyBinding[] paramArrayOfKeyBinding, Action[] paramArrayOfAction) {
    Hashtable hashtable = new Hashtable();
    for (Action action : paramArrayOfAction) {
      String str = (String)action.getValue("Name");
      hashtable.put((str != null) ? str : "", action);
    } 
    for (KeyBinding keyBinding : paramArrayOfKeyBinding) {
      Action action = (Action)hashtable.get(keyBinding.actionName);
      if (action != null)
        paramKeymap.addActionForKeyStroke(keyBinding.key, action); 
    } 
  }
  
  public Color getCaretColor() { return this.caretColor; }
  
  public void setCaretColor(Color paramColor) {
    Color color = this.caretColor;
    this.caretColor = paramColor;
    firePropertyChange("caretColor", color, this.caretColor);
  }
  
  public Color getSelectionColor() { return this.selectionColor; }
  
  public void setSelectionColor(Color paramColor) {
    Color color = this.selectionColor;
    this.selectionColor = paramColor;
    firePropertyChange("selectionColor", color, this.selectionColor);
  }
  
  public Color getSelectedTextColor() { return this.selectedTextColor; }
  
  public void setSelectedTextColor(Color paramColor) {
    Color color = this.selectedTextColor;
    this.selectedTextColor = paramColor;
    firePropertyChange("selectedTextColor", color, this.selectedTextColor);
  }
  
  public Color getDisabledTextColor() { return this.disabledTextColor; }
  
  public void setDisabledTextColor(Color paramColor) {
    Color color = this.disabledTextColor;
    this.disabledTextColor = paramColor;
    firePropertyChange("disabledTextColor", color, this.disabledTextColor);
  }
  
  public void replaceSelection(String paramString) {
    Document document = getDocument();
    if (document != null)
      try {
        boolean bool = saveComposedText(this.caret.getDot());
        int i = Math.min(this.caret.getDot(), this.caret.getMark());
        int j = Math.max(this.caret.getDot(), this.caret.getMark());
        if (document instanceof AbstractDocument) {
          ((AbstractDocument)document).replace(i, j - i, paramString, null);
        } else {
          if (i != j)
            document.remove(i, j - i); 
          if (paramString != null && paramString.length() > 0)
            document.insertString(i, paramString, null); 
        } 
        if (bool)
          restoreComposedText(); 
      } catch (BadLocationException badLocationException) {
        UIManager.getLookAndFeel().provideErrorFeedback(this);
      }  
  }
  
  public String getText(int paramInt1, int paramInt2) throws BadLocationException { return getDocument().getText(paramInt1, paramInt2); }
  
  public Rectangle modelToView(int paramInt) throws BadLocationException { return getUI().modelToView(this, paramInt); }
  
  public int viewToModel(Point paramPoint) { return getUI().viewToModel(this, paramPoint); }
  
  public void cut() {
    if (isEditable() && isEnabled())
      invokeAction("cut", TransferHandler.getCutAction()); 
  }
  
  public void copy() { invokeAction("copy", TransferHandler.getCopyAction()); }
  
  public void paste() {
    if (isEditable() && isEnabled())
      invokeAction("paste", TransferHandler.getPasteAction()); 
  }
  
  private void invokeAction(String paramString, Action paramAction) {
    ActionMap actionMap = getActionMap();
    Action action = null;
    if (actionMap != null)
      action = actionMap.get(paramString); 
    if (action == null) {
      installDefaultTransferHandlerIfNecessary();
      action = paramAction;
    } 
    action.actionPerformed(new ActionEvent(this, 1001, (String)action.getValue("Name"), EventQueue.getMostRecentEventTime(), getCurrentEventModifiers()));
  }
  
  private void installDefaultTransferHandlerIfNecessary() {
    if (getTransferHandler() == null) {
      if (defaultTransferHandler == null)
        defaultTransferHandler = new DefaultTransferHandler(); 
      setTransferHandler(defaultTransferHandler);
    } 
  }
  
  public void moveCaretPosition(int paramInt) {
    Document document = getDocument();
    if (document != null) {
      if (paramInt > document.getLength() || paramInt < 0)
        throw new IllegalArgumentException("bad position: " + paramInt); 
      this.caret.moveDot(paramInt);
    } 
  }
  
  public void setFocusAccelerator(char paramChar) {
    paramChar = Character.toUpperCase(paramChar);
    char c = this.focusAccelerator;
    this.focusAccelerator = paramChar;
    firePropertyChange("focusAcceleratorKey", c, this.focusAccelerator);
    firePropertyChange("focusAccelerator", c, this.focusAccelerator);
  }
  
  public char getFocusAccelerator() { return this.focusAccelerator; }
  
  public void read(Reader paramReader, Object paramObject) throws IOException {
    EditorKit editorKit = getUI().getEditorKit(this);
    Document document = editorKit.createDefaultDocument();
    if (paramObject != null)
      document.putProperty("stream", paramObject); 
    try {
      editorKit.read(paramReader, document, 0);
      setDocument(document);
    } catch (BadLocationException badLocationException) {
      throw new IOException(badLocationException.getMessage());
    } 
  }
  
  public void write(Writer paramWriter) throws IOException {
    Document document = getDocument();
    try {
      getUI().getEditorKit(this).write(paramWriter, document, 0, document.getLength());
    } catch (BadLocationException badLocationException) {
      throw new IOException(badLocationException.getMessage());
    } 
  }
  
  public void removeNotify() {
    super.removeNotify();
    if (getFocusedComponent() == this)
      AppContext.getAppContext().remove(FOCUSED_COMPONENT); 
  }
  
  public void setCaretPosition(int paramInt) {
    Document document = getDocument();
    if (document != null) {
      if (paramInt > document.getLength() || paramInt < 0)
        throw new IllegalArgumentException("bad position: " + paramInt); 
      this.caret.setDot(paramInt);
    } 
  }
  
  @Transient
  public int getCaretPosition() { return this.caret.getDot(); }
  
  public void setText(String paramString) {
    try {
      Document document = getDocument();
      if (document instanceof AbstractDocument) {
        ((AbstractDocument)document).replace(0, document.getLength(), paramString, null);
      } else {
        document.remove(0, document.getLength());
        document.insertString(0, paramString, null);
      } 
    } catch (BadLocationException badLocationException) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } 
  }
  
  public String getText() {
    String str;
    Document document = getDocument();
    try {
      str = document.getText(0, document.getLength());
    } catch (BadLocationException badLocationException) {
      str = null;
    } 
    return str;
  }
  
  public String getSelectedText() {
    String str = null;
    int i = Math.min(this.caret.getDot(), this.caret.getMark());
    int j = Math.max(this.caret.getDot(), this.caret.getMark());
    if (i != j)
      try {
        Document document = getDocument();
        str = document.getText(i, j - i);
      } catch (BadLocationException badLocationException) {
        throw new IllegalArgumentException(badLocationException.getMessage());
      }  
    return str;
  }
  
  public boolean isEditable() { return this.editable; }
  
  public void setEditable(boolean paramBoolean) {
    if (paramBoolean != this.editable) {
      boolean bool = this.editable;
      this.editable = paramBoolean;
      enableInputMethods(this.editable);
      firePropertyChange("editable", Boolean.valueOf(bool), Boolean.valueOf(this.editable));
      repaint();
    } 
  }
  
  @Transient
  public int getSelectionStart() { return Math.min(this.caret.getDot(), this.caret.getMark()); }
  
  public void setSelectionStart(int paramInt) { select(paramInt, getSelectionEnd()); }
  
  @Transient
  public int getSelectionEnd() { return Math.max(this.caret.getDot(), this.caret.getMark()); }
  
  public void setSelectionEnd(int paramInt) { select(getSelectionStart(), paramInt); }
  
  public void select(int paramInt1, int paramInt2) {
    int i = getDocument().getLength();
    if (paramInt1 < 0)
      paramInt1 = 0; 
    if (paramInt1 > i)
      paramInt1 = i; 
    if (paramInt2 > i)
      paramInt2 = i; 
    if (paramInt2 < paramInt1)
      paramInt2 = paramInt1; 
    setCaretPosition(paramInt1);
    moveCaretPosition(paramInt2);
  }
  
  public void selectAll() {
    Document document = getDocument();
    if (document != null) {
      setCaretPosition(0);
      moveCaretPosition(document.getLength());
    } 
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    String str = super.getToolTipText(paramMouseEvent);
    if (str == null) {
      TextUI textUI = getUI();
      if (textUI != null)
        str = textUI.getToolTipText(this, new Point(paramMouseEvent.getX(), paramMouseEvent.getY())); 
    } 
    return str;
  }
  
  public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 1:
        return paramRectangle.height / 10;
      case 0:
        return paramRectangle.width / 10;
    } 
    throw new IllegalArgumentException("Invalid orientation: " + paramInt1);
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 1:
        return paramRectangle.height;
      case 0:
        return paramRectangle.width;
    } 
    throw new IllegalArgumentException("Invalid orientation: " + paramInt1);
  }
  
  public boolean getScrollableTracksViewportWidth() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (container instanceof javax.swing.JViewport) ? ((container.getWidth() > (getPreferredSize()).width)) : false;
  }
  
  public boolean getScrollableTracksViewportHeight() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (container instanceof javax.swing.JViewport) ? ((container.getHeight() > (getPreferredSize()).height)) : false;
  }
  
  public boolean print() { return print(null, null, true, null, null, true); }
  
  public boolean print(MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) throws PrinterException { return print(paramMessageFormat1, paramMessageFormat2, true, null, null, true); }
  
  public boolean print(MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2, boolean paramBoolean1, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet, boolean paramBoolean2) throws PrinterException {
    final PrintingStatus printingStatus;
    Printable printable1;
    final PrinterJob job = PrinterJob.getPrinterJob();
    boolean bool1 = GraphicsEnvironment.isHeadless();
    final boolean isEventDispatchThread = SwingUtilities.isEventDispatchThread();
    Printable printable2 = getPrintable(paramMessageFormat1, paramMessageFormat2);
    if (paramBoolean2 && !bool1) {
      printingStatus = PrintingStatus.createPrintingStatus(this, printerJob);
      printable1 = printingStatus.createNotificationPrintable(printable2);
    } else {
      printingStatus = null;
      printable1 = printable2;
    } 
    if (paramPrintService != null)
      printerJob.setPrintService(paramPrintService); 
    printerJob.setPrintable(printable1);
    final HashPrintRequestAttributeSet attr = (paramPrintRequestAttributeSet == null) ? new HashPrintRequestAttributeSet() : paramPrintRequestAttributeSet;
    if (paramBoolean1 && !bool1 && !printerJob.printDialog(hashPrintRequestAttributeSet))
      return false; 
    Callable<Object> callable = new Callable<Object>() {
        public Object call() {
          try {
            job.print(attr);
          } finally {
            if (printingStatus != null)
              printingStatus.dispose(); 
          } 
          return null;
        }
      };
    final FutureTask futurePrinting = new FutureTask(callable);
    Runnable runnable = new Runnable() {
        public void run() {
          boolean bool = false;
          if (isEventDispatchThread) {
            if (JTextComponent.this.isEnabled()) {
              bool = true;
              JTextComponent.this.setEnabled(false);
            } 
          } else {
            try {
              bool = ((Boolean)SwingUtilities2.submit(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                      boolean bool = JTextComponent.null.this.this$0.isEnabled();
                      if (bool)
                        JTextComponent.null.this.this$0.setEnabled(false); 
                      return Boolean.valueOf(bool);
                    }
                  }).get()).booleanValue();
            } catch (InterruptedException interruptedException) {
              throw new RuntimeException(interruptedException);
            } catch (ExecutionException executionException) {
              Throwable throwable = executionException.getCause();
              if (throwable instanceof Error)
                throw (Error)throwable; 
              if (throwable instanceof RuntimeException)
                throw (RuntimeException)throwable; 
              throw new AssertionError(throwable);
            } 
          } 
          JTextComponent.this.getDocument().render(futurePrinting);
          if (bool)
            if (isEventDispatchThread) {
              JTextComponent.this.setEnabled(true);
            } else {
              try {
                SwingUtilities2.submit(new Runnable() {
                      public void run() { JTextComponent.null.this.this$0.setEnabled(true); }
                    },  null).get();
              } catch (InterruptedException interruptedException) {
                throw new RuntimeException(interruptedException);
              } catch (ExecutionException executionException) {
                Throwable throwable = executionException.getCause();
                if (throwable instanceof Error)
                  throw (Error)throwable; 
                if (throwable instanceof RuntimeException)
                  throw (RuntimeException)throwable; 
                throw new AssertionError(throwable);
              } 
            }  
        }
      };
    if (!paramBoolean2 || bool1) {
      runnable.run();
    } else if (bool2) {
      (new Thread(runnable)).start();
      printingStatus.showModal(true);
    } else {
      printingStatus.showModal(false);
      runnable.run();
    } 
    try {
      futureTask.get();
    } catch (InterruptedException interruptedException) {
      throw new RuntimeException(interruptedException);
    } catch (ExecutionException executionException) {
      Throwable throwable = executionException.getCause();
      if (throwable instanceof PrinterAbortException) {
        if (printingStatus != null && printingStatus.isAborted())
          return false; 
        throw (PrinterAbortException)throwable;
      } 
      if (throwable instanceof PrinterException)
        throw (PrinterException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new AssertionError(throwable);
    } 
    return true;
  }
  
  public Printable getPrintable(MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) { return TextComponentPrintable.getPrintable(this, paramMessageFormat1, paramMessageFormat2); }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJTextComponent(); 
    return this.accessibleContext;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.caretEvent = new MutableCaretEvent(this);
    addMouseListener(this.caretEvent);
    addFocusListener(this.caretEvent);
  }
  
  protected String paramString() {
    String str1 = this.editable ? "true" : "false";
    String str2 = (this.caretColor != null) ? this.caretColor.toString() : "";
    String str3 = (this.selectionColor != null) ? this.selectionColor.toString() : "";
    String str4 = (this.selectedTextColor != null) ? this.selectedTextColor.toString() : "";
    String str5 = (this.disabledTextColor != null) ? this.disabledTextColor.toString() : "";
    String str6 = (this.margin != null) ? this.margin.toString() : "";
    return super.paramString() + ",caretColor=" + str2 + ",disabledTextColor=" + str5 + ",editable=" + str1 + ",margin=" + str6 + ",selectedTextColor=" + str4 + ",selectionColor=" + str3;
  }
  
  static final JTextComponent getFocusedComponent() { return (JTextComponent)AppContext.getAppContext().get(FOCUSED_COMPONENT); }
  
  private int getCurrentEventModifiers() {
    int i = 0;
    AWTEvent aWTEvent = EventQueue.getCurrentEvent();
    if (aWTEvent instanceof InputEvent) {
      i = ((InputEvent)aWTEvent).getModifiers();
    } else if (aWTEvent instanceof ActionEvent) {
      i = ((ActionEvent)aWTEvent).getModifiers();
    } 
    return i;
  }
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent) {
    super.processInputMethodEvent(paramInputMethodEvent);
    if (!paramInputMethodEvent.isConsumed()) {
      if (!isEditable())
        return; 
      switch (paramInputMethodEvent.getID()) {
        case 1100:
          replaceInputMethodText(paramInputMethodEvent);
        case 1101:
          setInputMethodCaretPosition(paramInputMethodEvent);
          break;
      } 
      paramInputMethodEvent.consume();
    } 
  }
  
  public InputMethodRequests getInputMethodRequests() {
    if (this.inputMethodRequestsHandler == null) {
      this.inputMethodRequestsHandler = new InputMethodRequestsHandler();
      Document document = getDocument();
      if (document != null)
        document.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler); 
    } 
    return this.inputMethodRequestsHandler;
  }
  
  public void addInputMethodListener(InputMethodListener paramInputMethodListener) {
    super.addInputMethodListener(paramInputMethodListener);
    if (paramInputMethodListener != null) {
      this.needToSendKeyTypedEvent = false;
      this.checkedInputOverride = true;
    } 
  }
  
  private void replaceInputMethodText(InputMethodEvent paramInputMethodEvent) {
    int i = paramInputMethodEvent.getCommittedCharacterCount();
    AttributedCharacterIterator attributedCharacterIterator = paramInputMethodEvent.getText();
    Document document = getDocument();
    if (composedTextExists()) {
      try {
        document.remove(this.composedTextStart.getOffset(), this.composedTextEnd.getOffset() - this.composedTextStart.getOffset());
      } catch (BadLocationException badLocationException) {}
      this.composedTextStart = this.composedTextEnd = null;
      this.composedTextAttribute = null;
      this.composedTextContent = null;
    } 
    if (attributedCharacterIterator != null) {
      attributedCharacterIterator.first();
      int k = 0;
      int m = 0;
      if (i > 0) {
        k = this.caret.getDot();
        if (shouldSynthensizeKeyEvents()) {
          char c = attributedCharacterIterator.current();
          while (i > 0) {
            KeyEvent keyEvent = new KeyEvent(this, 400, EventQueue.getMostRecentEventTime(), 0, 0, c);
            processKeyEvent(keyEvent);
            c = attributedCharacterIterator.next();
            i--;
          } 
        } else {
          StringBuilder stringBuilder = new StringBuilder();
          char c = attributedCharacterIterator.current();
          while (i > 0) {
            stringBuilder.append(c);
            c = attributedCharacterIterator.next();
            i--;
          } 
          mapCommittedTextToAction(stringBuilder.toString());
        } 
        m = this.caret.getDot();
      } 
      int j = attributedCharacterIterator.getIndex();
      if (j < attributedCharacterIterator.getEndIndex()) {
        createComposedTextAttribute(j, attributedCharacterIterator);
        try {
          replaceSelection(null);
          document.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
          this.composedTextStart = document.createPosition(this.caret.getDot() - this.composedTextContent.length());
          this.composedTextEnd = document.createPosition(this.caret.getDot());
        } catch (BadLocationException badLocationException) {
          this.composedTextStart = this.composedTextEnd = null;
          this.composedTextAttribute = null;
          this.composedTextContent = null;
        } 
      } 
      if (k != m) {
        try {
          this.latestCommittedTextStart = document.createPosition(k);
          this.latestCommittedTextEnd = document.createPosition(m);
        } catch (BadLocationException badLocationException) {
          this.latestCommittedTextStart = this.latestCommittedTextEnd = null;
        } 
      } else {
        this.latestCommittedTextStart = this.latestCommittedTextEnd = null;
      } 
    } 
  }
  
  private void createComposedTextAttribute(int paramInt, AttributedCharacterIterator paramAttributedCharacterIterator) {
    Document document = getDocument();
    StringBuilder stringBuilder = new StringBuilder();
    char c;
    for (c = paramAttributedCharacterIterator.setIndex(paramInt); c != Character.MAX_VALUE; c = paramAttributedCharacterIterator.next())
      stringBuilder.append(c); 
    this.composedTextContent = stringBuilder.toString();
    this.composedTextAttribute = new SimpleAttributeSet();
    this.composedTextAttribute.addAttribute(StyleConstants.ComposedTextAttribute, new AttributedString(paramAttributedCharacterIterator, paramInt, paramAttributedCharacterIterator.getEndIndex()));
  }
  
  protected boolean saveComposedText(int paramInt) {
    if (composedTextExists()) {
      int i = this.composedTextStart.getOffset();
      int j = this.composedTextEnd.getOffset() - this.composedTextStart.getOffset();
      if (paramInt >= i && paramInt <= i + j)
        try {
          getDocument().remove(i, j);
          return true;
        } catch (BadLocationException badLocationException) {} 
    } 
    return false;
  }
  
  protected void restoreComposedText() {
    Document document = getDocument();
    try {
      document.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
      this.composedTextStart = document.createPosition(this.caret.getDot() - this.composedTextContent.length());
      this.composedTextEnd = document.createPosition(this.caret.getDot());
    } catch (BadLocationException badLocationException) {}
  }
  
  private void mapCommittedTextToAction(String paramString) {
    Keymap keymap1 = getKeymap();
    if (keymap1 != null) {
      Action action = null;
      if (paramString.length() == 1) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(paramString.charAt(0));
        action = keymap1.getAction(keyStroke);
      } 
      if (action == null)
        action = keymap1.getDefaultAction(); 
      if (action != null) {
        ActionEvent actionEvent = new ActionEvent(this, 1001, paramString, EventQueue.getMostRecentEventTime(), getCurrentEventModifiers());
        action.actionPerformed(actionEvent);
      } 
    } 
  }
  
  private void setInputMethodCaretPosition(InputMethodEvent paramInputMethodEvent) {
    if (composedTextExists()) {
      int i = this.composedTextStart.getOffset();
      if (!(this.caret instanceof ComposedTextCaret)) {
        if (this.composedTextCaret == null)
          this.composedTextCaret = new ComposedTextCaret(); 
        this.originalCaret = this.caret;
        exchangeCaret(this.originalCaret, this.composedTextCaret);
      } 
      TextHitInfo textHitInfo = paramInputMethodEvent.getCaret();
      if (textHitInfo != null) {
        int j = textHitInfo.getInsertionIndex();
        i += j;
        if (j == 0)
          try {
            Rectangle rectangle1 = modelToView(i);
            Rectangle rectangle2 = modelToView(this.composedTextEnd.getOffset());
            Rectangle rectangle3 = getBounds();
            rectangle1.x += Math.min(rectangle2.x - rectangle1.x, rectangle3.width);
            scrollRectToVisible(rectangle1);
          } catch (BadLocationException badLocationException) {} 
      } 
      this.caret.setDot(i);
    } else if (this.caret instanceof ComposedTextCaret) {
      int i = this.caret.getDot();
      exchangeCaret(this.caret, this.originalCaret);
      this.caret.setDot(i);
    } 
  }
  
  private void exchangeCaret(Caret paramCaret1, Caret paramCaret2) {
    int i = paramCaret1.getBlinkRate();
    setCaret(paramCaret2);
    this.caret.setBlinkRate(i);
    this.caret.setVisible(hasFocus());
  }
  
  private boolean shouldSynthensizeKeyEvents() {
    if (!this.checkedInputOverride) {
      this.needToSendKeyTypedEvent = !((Boolean)METHOD_OVERRIDDEN.get(getClass())).booleanValue();
      this.checkedInputOverride = true;
    } 
    return this.needToSendKeyTypedEvent;
  }
  
  boolean composedTextExists() { return (this.composedTextStart != null); }
  
  static  {
    SwingAccessor.setJTextComponentAccessor(new SwingAccessor.JTextComponentAccessor() {
          public TransferHandler.DropLocation dropLocationForPoint(JTextComponent param1JTextComponent, Point param1Point) { return param1JTextComponent.dropLocationForPoint(param1Point); }
          
          public Object setDropLocation(JTextComponent param1JTextComponent, TransferHandler.DropLocation param1DropLocation, Object param1Object, boolean param1Boolean) { return param1JTextComponent.setDropLocation(param1DropLocation, param1Object, param1Boolean); }
        });
    METHOD_OVERRIDDEN = new Cache<Class<?>, Boolean>(Cache.Kind.WEAK, Cache.Kind.STRONG) {
        public Boolean create(final Class<?> type) { return (JTextComponent.class == param1Class) ? Boolean.FALSE : (((Boolean)get(param1Class.getSuperclass())).booleanValue() ? Boolean.TRUE : (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                public Boolean run() throws Exception {
                  try {
                    type.getDeclaredMethod("processInputMethodEvent", new Class[] { InputMethodEvent.class });
                    return Boolean.TRUE;
                  } catch (NoSuchMethodException noSuchMethodException) {
                    return Boolean.FALSE;
                  } 
                }
              })); }
      };
    KEYMAP_TABLE = new StringBuilder("JTextComponent_KeymapTable");
    FOCUSED_COMPONENT = new StringBuilder("JTextComponent_FocusedComponent");
  }
  
  public class AccessibleJTextComponent extends JComponent.AccessibleJComponent implements AccessibleText, CaretListener, DocumentListener, AccessibleAction, AccessibleEditableText, AccessibleExtendedText {
    int caretPos;
    
    Point oldLocationOnScreen;
    
    public AccessibleJTextComponent() {
      super(JTextComponent.this);
      Document document = this$0.getDocument();
      if (document != null)
        document.addDocumentListener(this); 
      this$0.addCaretListener(this);
      this.caretPos = getCaretPosition();
      try {
        this.oldLocationOnScreen = getLocationOnScreen();
      } catch (IllegalComponentStateException illegalComponentStateException) {}
      this$0.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent param2ComponentEvent) {
              try {
                Point point = JTextComponent.AccessibleJTextComponent.this.getLocationOnScreen();
                JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleVisibleData", JTextComponent.AccessibleJTextComponent.this.oldLocationOnScreen, point);
                JTextComponent.AccessibleJTextComponent.this.oldLocationOnScreen = point;
              } catch (IllegalComponentStateException illegalComponentStateException) {}
            }
          });
    }
    
    public void caretUpdate(CaretEvent param1CaretEvent) {
      int i = param1CaretEvent.getDot();
      int j = param1CaretEvent.getMark();
      if (this.caretPos != i) {
        firePropertyChange("AccessibleCaret", new Integer(this.caretPos), new Integer(i));
        this.caretPos = i;
        try {
          this.oldLocationOnScreen = getLocationOnScreen();
        } catch (IllegalComponentStateException illegalComponentStateException) {}
      } 
      if (j != i)
        firePropertyChange("AccessibleSelection", null, getSelectedText()); 
    }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent) {
      final Integer pos = new Integer(param1DocumentEvent.getOffset());
      if (SwingUtilities.isEventDispatchThread()) {
        firePropertyChange("AccessibleText", null, integer);
      } else {
        Runnable runnable = new Runnable() {
            public void run() { JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, pos); }
          };
        SwingUtilities.invokeLater(runnable);
      } 
    }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent) {
      final Integer pos = new Integer(param1DocumentEvent.getOffset());
      if (SwingUtilities.isEventDispatchThread()) {
        firePropertyChange("AccessibleText", null, integer);
      } else {
        Runnable runnable = new Runnable() {
            public void run() { JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, pos); }
          };
        SwingUtilities.invokeLater(runnable);
      } 
    }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent) {
      final Integer pos = new Integer(param1DocumentEvent.getOffset());
      if (SwingUtilities.isEventDispatchThread()) {
        firePropertyChange("AccessibleText", null, integer);
      } else {
        Runnable runnable = new Runnable() {
            public void run() { JTextComponent.AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, pos); }
          };
        SwingUtilities.invokeLater(runnable);
      } 
    }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JTextComponent.this.isEditable())
        accessibleStateSet.add(AccessibleState.EDITABLE); 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TEXT; }
    
    public AccessibleText getAccessibleText() { return this; }
    
    public int getIndexAtPoint(Point param1Point) { return (param1Point == null) ? -1 : JTextComponent.this.viewToModel(param1Point); }
    
    Rectangle getRootEditorRect() {
      Rectangle rectangle = JTextComponent.this.getBounds();
      if (rectangle.width > 0 && rectangle.height > 0) {
        rectangle.x = rectangle.y = 0;
        Insets insets = JTextComponent.this.getInsets();
        rectangle.x += insets.left;
        rectangle.y += insets.top;
        rectangle.width -= insets.left + insets.right;
        rectangle.height -= insets.top + insets.bottom;
        return rectangle;
      } 
      return null;
    }
    
    public Rectangle getCharacterBounds(int param1Int) throws BadLocationException {
      if (param1Int < 0 || param1Int > JTextComponent.this.model.getLength() - 1)
        return null; 
      TextUI textUI = JTextComponent.this.getUI();
      if (textUI == null)
        return null; 
      Rectangle rectangle1 = null;
      Rectangle rectangle2 = getRootEditorRect();
      if (rectangle2 == null)
        return null; 
      if (JTextComponent.this.model instanceof AbstractDocument)
        ((AbstractDocument)JTextComponent.this.model).readLock(); 
      try {
        View view = textUI.getRootView(JTextComponent.this);
        if (view != null) {
          view.setSize(rectangle2.width, rectangle2.height);
          Shape shape = view.modelToView(param1Int, Position.Bias.Forward, param1Int + 1, Position.Bias.Backward, rectangle2);
          rectangle1 = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
        } 
      } catch (BadLocationException badLocationException) {
      
      } finally {
        if (JTextComponent.this.model instanceof AbstractDocument)
          ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
      } 
      return rectangle1;
    }
    
    public int getCharCount() { return JTextComponent.this.model.getLength(); }
    
    public int getCaretPosition() { return JTextComponent.this.getCaretPosition(); }
    
    public AttributeSet getCharacterAttribute(int param1Int) {
      Element element = null;
      if (JTextComponent.this.model instanceof AbstractDocument)
        ((AbstractDocument)JTextComponent.this.model).readLock(); 
      try {
        for (element = JTextComponent.this.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(i))
          int i = element.getElementIndex(param1Int); 
      } finally {
        if (JTextComponent.this.model instanceof AbstractDocument)
          ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
      } 
      return element.getAttributes();
    }
    
    public int getSelectionStart() { return JTextComponent.this.getSelectionStart(); }
    
    public int getSelectionEnd() { return JTextComponent.this.getSelectionEnd(); }
    
    public String getSelectedText() { return JTextComponent.this.getSelectedText(); }
    
    public String getAtIndex(int param1Int1, int param1Int2) throws BadLocationException { return getAtIndex(param1Int1, param1Int2, 0); }
    
    public String getAfterIndex(int param1Int1, int param1Int2) throws BadLocationException { return getAtIndex(param1Int1, param1Int2, 1); }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) throws BadLocationException { return getAtIndex(param1Int1, param1Int2, -1); }
    
    private String getAtIndex(int param1Int1, int param1Int2, int param1Int3) {
      if (JTextComponent.this.model instanceof AbstractDocument)
        ((AbstractDocument)JTextComponent.this.model).readLock(); 
      try {
        IndexedSegment indexedSegment;
        if (param1Int2 < 0 || param1Int2 >= JTextComponent.this.model.getLength())
          return null; 
        switch (param1Int1) {
          case 1:
            if (param1Int2 + param1Int3 < JTextComponent.this.model.getLength() && param1Int2 + param1Int3 >= 0)
              return JTextComponent.this.model.getText(param1Int2 + param1Int3, 1); 
            break;
          case 2:
          case 3:
            indexedSegment = getSegmentAt(param1Int1, param1Int2);
            if (indexedSegment != null) {
              if (param1Int3 != 0) {
                int i;
                if (param1Int3 < 0) {
                  i = indexedSegment.modelOffset - 1;
                } else {
                  i = indexedSegment.modelOffset + param1Int3 * indexedSegment.count;
                } 
                if (i >= 0 && i <= JTextComponent.this.model.getLength()) {
                  indexedSegment = getSegmentAt(param1Int1, i);
                } else {
                  indexedSegment = null;
                } 
              } 
              if (indexedSegment != null)
                return new String(indexedSegment.array, indexedSegment.offset, indexedSegment.count); 
            } 
            break;
        } 
      } catch (BadLocationException badLocationException) {
      
      } finally {
        if (JTextComponent.this.model instanceof AbstractDocument)
          ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
      } 
      return null;
    }
    
    private Element getParagraphElement(int param1Int) {
      if (JTextComponent.this.model instanceof PlainDocument) {
        PlainDocument plainDocument = (PlainDocument)JTextComponent.this.model;
        return plainDocument.getParagraphElement(param1Int);
      } 
      if (JTextComponent.this.model instanceof StyledDocument) {
        StyledDocument styledDocument = (StyledDocument)JTextComponent.this.model;
        return styledDocument.getParagraphElement(param1Int);
      } 
      Element element;
      for (element = JTextComponent.this.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(i))
        int i = element.getElementIndex(param1Int); 
      return (element == null) ? null : element.getParentElement();
    }
    
    private IndexedSegment getParagraphElementText(int param1Int) throws BadLocationException {
      Element element = getParagraphElement(param1Int);
      if (element != null) {
        IndexedSegment indexedSegment = new IndexedSegment(null);
        try {
          int i = element.getEndOffset() - element.getStartOffset();
          JTextComponent.this.model.getText(element.getStartOffset(), i, indexedSegment);
        } catch (BadLocationException badLocationException) {
          return null;
        } 
        indexedSegment.modelOffset = element.getStartOffset();
        return indexedSegment;
      } 
      return null;
    }
    
    private IndexedSegment getSegmentAt(int param1Int1, int param1Int2) throws BadLocationException {
      BreakIterator breakIterator;
      IndexedSegment indexedSegment = getParagraphElementText(param1Int2);
      if (indexedSegment == null)
        return null; 
      switch (param1Int1) {
        case 2:
          breakIterator = BreakIterator.getWordInstance(getLocale());
          break;
        case 3:
          breakIterator = BreakIterator.getSentenceInstance(getLocale());
          break;
        default:
          return null;
      } 
      indexedSegment.first();
      breakIterator.setText(indexedSegment);
      int i = breakIterator.following(param1Int2 - indexedSegment.modelOffset + indexedSegment.offset);
      if (i == -1)
        return null; 
      if (i > indexedSegment.offset + indexedSegment.count)
        return null; 
      int j = breakIterator.previous();
      if (j == -1 || j >= indexedSegment.offset + indexedSegment.count)
        return null; 
      indexedSegment.modelOffset = indexedSegment.modelOffset + j - indexedSegment.offset;
      indexedSegment.offset = j;
      indexedSegment.count = i - j;
      return indexedSegment;
    }
    
    public AccessibleEditableText getAccessibleEditableText() { return this; }
    
    public void setTextContents(String param1String) { JTextComponent.this.setText(param1String); }
    
    public void insertTextAtIndex(int param1Int, String param1String) {
      Document document = JTextComponent.this.getDocument();
      if (document != null)
        try {
          if (param1String != null && param1String.length() > 0) {
            boolean bool = JTextComponent.this.saveComposedText(param1Int);
            document.insertString(param1Int, param1String, null);
            if (bool)
              JTextComponent.this.restoreComposedText(); 
          } 
        } catch (BadLocationException badLocationException) {
          UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
        }  
    }
    
    public String getTextRange(int param1Int1, int param1Int2) throws BadLocationException {
      String str = null;
      int i = Math.min(param1Int1, param1Int2);
      int j = Math.max(param1Int1, param1Int2);
      if (i != j)
        try {
          Document document = JTextComponent.this.getDocument();
          str = document.getText(i, j - i);
        } catch (BadLocationException badLocationException) {
          throw new IllegalArgumentException(badLocationException.getMessage());
        }  
      return str;
    }
    
    public void delete(int param1Int1, int param1Int2) {
      if (JTextComponent.this.isEditable() && isEnabled()) {
        try {
          int i = Math.min(param1Int1, param1Int2);
          int j = Math.max(param1Int1, param1Int2);
          if (i != j) {
            Document document = JTextComponent.this.getDocument();
            document.remove(i, j - i);
          } 
        } catch (BadLocationException badLocationException) {}
      } else {
        UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
      } 
    }
    
    public void cut(int param1Int1, int param1Int2) {
      selectText(param1Int1, param1Int2);
      JTextComponent.this.cut();
    }
    
    public void paste(int param1Int) {
      JTextComponent.this.setCaretPosition(param1Int);
      JTextComponent.this.paste();
    }
    
    public void replaceText(int param1Int1, int param1Int2, String param1String) {
      selectText(param1Int1, param1Int2);
      JTextComponent.this.replaceSelection(param1String);
    }
    
    public void selectText(int param1Int1, int param1Int2) { JTextComponent.this.select(param1Int1, param1Int2); }
    
    public void setAttributes(int param1Int1, int param1Int2, AttributeSet param1AttributeSet) {
      Document document = JTextComponent.this.getDocument();
      if (document != null && document instanceof StyledDocument) {
        StyledDocument styledDocument = (StyledDocument)document;
        int i = param1Int1;
        int j = param1Int2 - param1Int1;
        styledDocument.setCharacterAttributes(i, j, param1AttributeSet, true);
      } 
    }
    
    private AccessibleTextSequence getSequenceAtIndex(int param1Int1, int param1Int2, int param1Int3) {
      String str;
      int j;
      int i;
      AccessibleTextSequence accessibleTextSequence3;
      AccessibleTextSequence accessibleTextSequence2;
      AccessibleTextSequence accessibleTextSequence1;
      if (param1Int2 < 0 || param1Int2 >= JTextComponent.this.model.getLength())
        return null; 
      if (param1Int3 < -1 || param1Int3 > 1)
        return null; 
      switch (param1Int1) {
        case 1:
          if (JTextComponent.this.model instanceof AbstractDocument)
            ((AbstractDocument)JTextComponent.this.model).readLock(); 
          accessibleTextSequence1 = null;
          try {
            if (param1Int2 + param1Int3 < JTextComponent.this.model.getLength() && param1Int2 + param1Int3 >= 0)
              accessibleTextSequence1 = new AccessibleTextSequence(param1Int2 + param1Int3, param1Int2 + param1Int3 + 1, JTextComponent.this.model.getText(param1Int2 + param1Int3, 1)); 
          } catch (BadLocationException badLocationException) {
          
          } finally {
            if (JTextComponent.this.model instanceof AbstractDocument)
              ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
          } 
          return accessibleTextSequence1;
        case 2:
        case 3:
          if (JTextComponent.this.model instanceof AbstractDocument)
            ((AbstractDocument)JTextComponent.this.model).readLock(); 
          accessibleTextSequence2 = null;
          try {
            IndexedSegment indexedSegment = getSegmentAt(param1Int1, param1Int2);
            if (indexedSegment != null) {
              if (param1Int3 != 0) {
                if (param1Int3 < 0) {
                  i = indexedSegment.modelOffset - 1;
                } else {
                  i = indexedSegment.modelOffset + indexedSegment.count;
                } 
                if (i >= 0 && i <= JTextComponent.this.model.getLength()) {
                  indexedSegment = getSegmentAt(param1Int1, i);
                } else {
                  indexedSegment = null;
                } 
              } 
              if (indexedSegment != null && indexedSegment.offset + indexedSegment.count <= JTextComponent.this.model.getLength())
                accessibleTextSequence2 = new AccessibleTextSequence(indexedSegment.offset, indexedSegment.offset + indexedSegment.count, new String(indexedSegment.array, indexedSegment.offset, indexedSegment.count)); 
            } 
          } catch (BadLocationException badLocationException) {
          
          } finally {
            if (JTextComponent.this.model instanceof AbstractDocument)
              ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
          } 
          return accessibleTextSequence2;
        case 4:
          accessibleTextSequence3 = null;
          if (JTextComponent.this.model instanceof AbstractDocument)
            ((AbstractDocument)JTextComponent.this.model).readLock(); 
          try {
            i = Utilities.getRowStart(JTextComponent.this, param1Int2);
            j = Utilities.getRowEnd(JTextComponent.this, param1Int2);
            if (i >= 0 && j >= i)
              if (param1Int3 == 0) {
                accessibleTextSequence3 = new AccessibleTextSequence(i, j, JTextComponent.this.model.getText(i, j - i + 1));
              } else if (param1Int3 == -1 && i > 0) {
                j = Utilities.getRowEnd(JTextComponent.this, i - 1);
                i = Utilities.getRowStart(JTextComponent.this, i - 1);
                if (i >= 0 && j >= i)
                  accessibleTextSequence3 = new AccessibleTextSequence(i, j, JTextComponent.this.model.getText(i, j - i + 1)); 
              } else if (param1Int3 == 1 && j < JTextComponent.this.model.getLength()) {
                i = Utilities.getRowStart(JTextComponent.this, j + 1);
                j = Utilities.getRowEnd(JTextComponent.this, j + 1);
                if (i >= 0 && j >= i)
                  accessibleTextSequence3 = new AccessibleTextSequence(i, j, JTextComponent.this.model.getText(i, j - i + 1)); 
              }  
          } catch (BadLocationException badLocationException) {
          
          } finally {
            if (JTextComponent.this.model instanceof AbstractDocument)
              ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
          } 
          return accessibleTextSequence3;
        case 5:
          str = null;
          if (JTextComponent.this.model instanceof AbstractDocument)
            ((AbstractDocument)JTextComponent.this.model).readLock(); 
          try {
            i = j = Integer.MIN_VALUE;
            int k = param1Int2;
            switch (param1Int3) {
              case -1:
                j = getRunEdge(param1Int2, param1Int3);
                k = j - 1;
                break;
              case 1:
                i = getRunEdge(param1Int2, param1Int3);
                k = i;
                break;
              case 0:
                break;
              default:
                throw new AssertionError(param1Int3);
            } 
            i = (i != Integer.MIN_VALUE) ? i : getRunEdge(k, -1);
            j = (j != Integer.MIN_VALUE) ? j : getRunEdge(k, 1);
            str = JTextComponent.this.model.getText(i, j - i);
          } catch (BadLocationException badLocationException) {
            return null;
          } finally {
            if (JTextComponent.this.model instanceof AbstractDocument)
              ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
          } 
          return new AccessibleTextSequence(i, j, str);
      } 
      return null;
    }
    
    private int getRunEdge(int param1Int1, int param1Int2) throws BadLocationException {
      int k;
      int j;
      Element element3;
      if (param1Int1 < 0 || param1Int1 >= JTextComponent.this.model.getLength())
        throw new BadLocationException("Location out of bounds", param1Int1); 
      int i = -1;
      Element element1;
      for (element1 = JTextComponent.this.model.getDefaultRootElement(); !element1.isLeaf(); element1 = element1.getElement(i))
        i = element1.getElementIndex(param1Int1); 
      if (i == -1)
        throw new AssertionError(param1Int1); 
      AttributeSet attributeSet = element1.getAttributes();
      Element element2 = element1.getParentElement();
      switch (param1Int2) {
        case -1:
        case 1:
          j = i;
          k = element2.getElementCount();
          while (j + param1Int2 > 0 && j + param1Int2 < k && element2.getElement(j + param1Int2).getAttributes().isEqual(attributeSet))
            j += param1Int2; 
          element3 = element2.getElement(j);
          break;
        default:
          throw new AssertionError(param1Int2);
      } 
      switch (param1Int2) {
        case -1:
          return element3.getStartOffset();
        case 1:
          return element3.getEndOffset();
      } 
      return Integer.MIN_VALUE;
    }
    
    public AccessibleTextSequence getTextSequenceAt(int param1Int1, int param1Int2) { return getSequenceAtIndex(param1Int1, param1Int2, 0); }
    
    public AccessibleTextSequence getTextSequenceAfter(int param1Int1, int param1Int2) { return getSequenceAtIndex(param1Int1, param1Int2, 1); }
    
    public AccessibleTextSequence getTextSequenceBefore(int param1Int1, int param1Int2) { return getSequenceAtIndex(param1Int1, param1Int2, -1); }
    
    public Rectangle getTextBounds(int param1Int1, int param1Int2) {
      if (param1Int1 < 0 || param1Int1 > JTextComponent.this.model.getLength() - 1 || param1Int2 < 0 || param1Int2 > JTextComponent.this.model.getLength() - 1 || param1Int1 > param1Int2)
        return null; 
      TextUI textUI = JTextComponent.this.getUI();
      if (textUI == null)
        return null; 
      Rectangle rectangle1 = null;
      Rectangle rectangle2 = getRootEditorRect();
      if (rectangle2 == null)
        return null; 
      if (JTextComponent.this.model instanceof AbstractDocument)
        ((AbstractDocument)JTextComponent.this.model).readLock(); 
      try {
        View view = textUI.getRootView(JTextComponent.this);
        if (view != null) {
          Shape shape = view.modelToView(param1Int1, Position.Bias.Forward, param1Int2, Position.Bias.Backward, rectangle2);
          rectangle1 = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
        } 
      } catch (BadLocationException badLocationException) {
      
      } finally {
        if (JTextComponent.this.model instanceof AbstractDocument)
          ((AbstractDocument)JTextComponent.this.model).readUnlock(); 
      } 
      return rectangle1;
    }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public int getAccessibleActionCount() {
      Action[] arrayOfAction = JTextComponent.this.getActions();
      return arrayOfAction.length;
    }
    
    public String getAccessibleActionDescription(int param1Int) {
      Action[] arrayOfAction = JTextComponent.this.getActions();
      return (param1Int < 0 || param1Int >= arrayOfAction.length) ? null : (String)arrayOfAction[param1Int].getValue("Name");
    }
    
    public boolean doAccessibleAction(int param1Int) {
      Action[] arrayOfAction = JTextComponent.this.getActions();
      if (param1Int < 0 || param1Int >= arrayOfAction.length)
        return false; 
      ActionEvent actionEvent = new ActionEvent(JTextComponent.this, 1001, null, EventQueue.getMostRecentEventTime(), JTextComponent.this.getCurrentEventModifiers());
      arrayOfAction[param1Int].actionPerformed(actionEvent);
      return true;
    }
    
    private class IndexedSegment extends Segment {
      public int modelOffset;
      
      private IndexedSegment() {}
    }
  }
  
  class ComposedTextCaret extends DefaultCaret implements Serializable {
    Color bg;
    
    public void install(JTextComponent param1JTextComponent) {
      super.install(param1JTextComponent);
      Document document = param1JTextComponent.getDocument();
      if (document instanceof StyledDocument) {
        StyledDocument styledDocument = (StyledDocument)document;
        Element element = styledDocument.getCharacterElement(param1JTextComponent.composedTextStart.getOffset());
        AttributeSet attributeSet = element.getAttributes();
        this.bg = styledDocument.getBackground(attributeSet);
      } 
      if (this.bg == null)
        this.bg = param1JTextComponent.getBackground(); 
    }
    
    public void paint(Graphics param1Graphics) {
      if (isVisible())
        try {
          Rectangle rectangle = this.component.modelToView(getDot());
          param1Graphics.setXORMode(this.bg);
          param1Graphics.drawLine(rectangle.x, rectangle.y, rectangle.x, rectangle.y + rectangle.height - 1);
          param1Graphics.setPaintMode();
        } catch (BadLocationException badLocationException) {} 
    }
    
    protected void positionCaret(MouseEvent param1MouseEvent) {
      JTextComponent jTextComponent = this.component;
      Point point = new Point(param1MouseEvent.getX(), param1MouseEvent.getY());
      int i = jTextComponent.viewToModel(point);
      int j = jTextComponent.composedTextStart.getOffset();
      if (i < j || i > JTextComponent.this.composedTextEnd.getOffset()) {
        try {
          Position position = jTextComponent.getDocument().createPosition(i);
          jTextComponent.getInputContext().endComposition();
          EventQueue.invokeLater(new JTextComponent.DoSetCaretPosition(JTextComponent.this, jTextComponent, position));
        } catch (BadLocationException badLocationException) {
          System.err.println(badLocationException);
        } 
      } else {
        super.positionCaret(param1MouseEvent);
      } 
    }
  }
  
  static class DefaultKeymap implements Keymap {
    String nm;
    
    Keymap parent;
    
    Hashtable<KeyStroke, Action> bindings;
    
    Action defaultAction;
    
    DefaultKeymap(String param1String, Keymap param1Keymap) {
      this.nm = param1String;
      this.parent = param1Keymap;
      this.bindings = new Hashtable();
    }
    
    public Action getDefaultAction() { return (this.defaultAction != null) ? this.defaultAction : ((this.parent != null) ? this.parent.getDefaultAction() : null); }
    
    public void setDefaultAction(Action param1Action) { this.defaultAction = param1Action; }
    
    public String getName() { return this.nm; }
    
    public Action getAction(KeyStroke param1KeyStroke) {
      Action action = (Action)this.bindings.get(param1KeyStroke);
      if (action == null && this.parent != null)
        action = this.parent.getAction(param1KeyStroke); 
      return action;
    }
    
    public KeyStroke[] getBoundKeyStrokes() {
      KeyStroke[] arrayOfKeyStroke = new KeyStroke[this.bindings.size()];
      byte b = 0;
      Enumeration enumeration = this.bindings.keys();
      while (enumeration.hasMoreElements())
        arrayOfKeyStroke[b++] = (KeyStroke)enumeration.nextElement(); 
      return arrayOfKeyStroke;
    }
    
    public Action[] getBoundActions() {
      Action[] arrayOfAction = new Action[this.bindings.size()];
      byte b = 0;
      Enumeration enumeration = this.bindings.elements();
      while (enumeration.hasMoreElements())
        arrayOfAction[b++] = (Action)enumeration.nextElement(); 
      return arrayOfAction;
    }
    
    public KeyStroke[] getKeyStrokesForAction(Action param1Action) {
      if (param1Action == null)
        return null; 
      KeyStroke[] arrayOfKeyStroke = null;
      Vector vector = null;
      Enumeration enumeration = this.bindings.keys();
      while (enumeration.hasMoreElements()) {
        KeyStroke keyStroke = (KeyStroke)enumeration.nextElement();
        if (this.bindings.get(keyStroke) == param1Action) {
          if (vector == null)
            vector = new Vector(); 
          vector.addElement(keyStroke);
        } 
      } 
      if (this.parent != null) {
        KeyStroke[] arrayOfKeyStroke1 = this.parent.getKeyStrokesForAction(param1Action);
        if (arrayOfKeyStroke1 != null) {
          byte b = 0;
          int i;
          for (i = arrayOfKeyStroke1.length - 1; i >= 0; i--) {
            if (isLocallyDefined(arrayOfKeyStroke1[i])) {
              arrayOfKeyStroke1[i] = null;
              b++;
            } 
          } 
          if (b > 0 && b < arrayOfKeyStroke1.length) {
            if (vector == null)
              vector = new Vector(); 
            for (i = arrayOfKeyStroke1.length - 1; i >= 0; i--) {
              if (arrayOfKeyStroke1[i] != null)
                vector.addElement(arrayOfKeyStroke1[i]); 
            } 
          } else if (b == 0) {
            if (vector == null) {
              arrayOfKeyStroke = arrayOfKeyStroke1;
            } else {
              arrayOfKeyStroke = new KeyStroke[vector.size() + arrayOfKeyStroke1.length];
              vector.copyInto(arrayOfKeyStroke);
              System.arraycopy(arrayOfKeyStroke1, 0, arrayOfKeyStroke, vector.size(), arrayOfKeyStroke1.length);
              vector = null;
            } 
          } 
        } 
      } 
      if (vector != null) {
        arrayOfKeyStroke = new KeyStroke[vector.size()];
        vector.copyInto(arrayOfKeyStroke);
      } 
      return arrayOfKeyStroke;
    }
    
    public boolean isLocallyDefined(KeyStroke param1KeyStroke) { return this.bindings.containsKey(param1KeyStroke); }
    
    public void addActionForKeyStroke(KeyStroke param1KeyStroke, Action param1Action) { this.bindings.put(param1KeyStroke, param1Action); }
    
    public void removeKeyStrokeBinding(KeyStroke param1KeyStroke) { this.bindings.remove(param1KeyStroke); }
    
    public void removeBindings() { this.bindings.clear(); }
    
    public Keymap getResolveParent() { return this.parent; }
    
    public void setResolveParent(Keymap param1Keymap) { this.parent = param1Keymap; }
    
    public String toString() { return "Keymap[" + this.nm + "]" + this.bindings; }
  }
  
  static class DefaultTransferHandler extends TransferHandler implements UIResource {
    public void exportToClipboard(JComponent param1JComponent, Clipboard param1Clipboard, int param1Int) throws IllegalStateException {
      if (param1JComponent instanceof JTextComponent) {
        JTextComponent jTextComponent = (JTextComponent)param1JComponent;
        int i = jTextComponent.getSelectionStart();
        int j = jTextComponent.getSelectionEnd();
        if (i != j)
          try {
            Document document = jTextComponent.getDocument();
            String str = document.getText(i, j - i);
            StringSelection stringSelection = new StringSelection(str);
            param1Clipboard.setContents(stringSelection, null);
            if (param1Int == 2)
              document.remove(i, j - i); 
          } catch (BadLocationException badLocationException) {} 
      } 
    }
    
    public boolean importData(JComponent param1JComponent, Transferable param1Transferable) {
      if (param1JComponent instanceof JTextComponent) {
        DataFlavor dataFlavor = getFlavor(param1Transferable.getTransferDataFlavors());
        if (dataFlavor != null) {
          InputContext inputContext = param1JComponent.getInputContext();
          if (inputContext != null)
            inputContext.endComposition(); 
          try {
            String str = (String)param1Transferable.getTransferData(dataFlavor);
            ((JTextComponent)param1JComponent).replaceSelection(str);
            return true;
          } catch (UnsupportedFlavorException unsupportedFlavorException) {
          
          } catch (IOException iOException) {}
        } 
      } 
      return false;
    }
    
    public boolean canImport(JComponent param1JComponent, DataFlavor[] param1ArrayOfDataFlavor) {
      JTextComponent jTextComponent = (JTextComponent)param1JComponent;
      return (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) ? false : ((getFlavor(param1ArrayOfDataFlavor) != null));
    }
    
    public int getSourceActions(JComponent param1JComponent) { return 0; }
    
    private DataFlavor getFlavor(DataFlavor[] param1ArrayOfDataFlavor) {
      if (param1ArrayOfDataFlavor != null)
        for (DataFlavor dataFlavor : param1ArrayOfDataFlavor) {
          if (dataFlavor.equals(DataFlavor.stringFlavor))
            return dataFlavor; 
        }  
      return null;
    }
  }
  
  private class DoSetCaretPosition implements Runnable {
    JTextComponent host;
    
    Position newPos;
    
    DoSetCaretPosition(JTextComponent param1JTextComponent1, Position param1Position) {
      this.host = param1JTextComponent1;
      this.newPos = param1Position;
    }
    
    public void run() { this.host.setCaretPosition(this.newPos.getOffset()); }
  }
  
  public static final class DropLocation extends TransferHandler.DropLocation {
    private final int index;
    
    private final Position.Bias bias;
    
    private DropLocation(Point param1Point, int param1Int, Position.Bias param1Bias) {
      super(param1Point);
      this.index = param1Int;
      this.bias = param1Bias;
    }
    
    public int getIndex() { return this.index; }
    
    public Position.Bias getBias() { return this.bias; }
    
    public String toString() { return getClass().getName() + "[dropPoint=" + getDropPoint() + ",index=" + this.index + ",bias=" + this.bias + "]"; }
  }
  
  class InputMethodRequestsHandler implements InputMethodRequests, DocumentListener {
    public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] param1ArrayOfAttribute) {
      Document document = JTextComponent.this.getDocument();
      if (document != null && JTextComponent.this.latestCommittedTextStart != null && !JTextComponent.this.latestCommittedTextStart.equals(JTextComponent.this.latestCommittedTextEnd))
        try {
          int i = JTextComponent.this.latestCommittedTextStart.getOffset();
          int j = JTextComponent.this.latestCommittedTextEnd.getOffset();
          String str = document.getText(i, j - i);
          document.remove(i, j - i);
          return (new AttributedString(str)).getIterator();
        } catch (BadLocationException badLocationException) {} 
      return null;
    }
    
    public AttributedCharacterIterator getCommittedText(int param1Int1, int param1Int2, AttributedCharacterIterator.Attribute[] param1ArrayOfAttribute) {
      String str;
      int i = 0;
      int j = 0;
      if (JTextComponent.this.composedTextExists()) {
        i = JTextComponent.this.composedTextStart.getOffset();
        j = JTextComponent.this.composedTextEnd.getOffset();
      } 
      try {
        if (param1Int1 < i) {
          if (param1Int2 <= i) {
            str = JTextComponent.this.getText(param1Int1, param1Int2 - param1Int1);
          } else {
            int k = i - param1Int1;
            str = JTextComponent.this.getText(param1Int1, k) + JTextComponent.this.getText(j, param1Int2 - param1Int1 - k);
          } 
        } else {
          str = JTextComponent.this.getText(param1Int1 + j - i, param1Int2 - param1Int1);
        } 
      } catch (BadLocationException badLocationException) {
        throw new IllegalArgumentException("Invalid range");
      } 
      return (new AttributedString(str)).getIterator();
    }
    
    public int getCommittedTextLength() {
      Document document = JTextComponent.this.getDocument();
      int i = 0;
      if (document != null) {
        i = document.getLength();
        if (JTextComponent.this.composedTextContent != null)
          if (JTextComponent.this.composedTextEnd == null || JTextComponent.this.composedTextStart == null) {
            i -= JTextComponent.this.composedTextContent.length();
          } else {
            i -= JTextComponent.this.composedTextEnd.getOffset() - JTextComponent.this.composedTextStart.getOffset();
          }  
      } 
      return i;
    }
    
    public int getInsertPositionOffset() {
      int i = 0;
      int j = 0;
      if (JTextComponent.this.composedTextExists()) {
        i = JTextComponent.this.composedTextStart.getOffset();
        j = JTextComponent.this.composedTextEnd.getOffset();
      } 
      int k = JTextComponent.this.getCaretPosition();
      return (k < i) ? k : ((k < j) ? i : (k - j - i));
    }
    
    public TextHitInfo getLocationOffset(int param1Int1, int param1Int2) {
      if (JTextComponent.this.composedTextAttribute == null)
        return null; 
      Point point = JTextComponent.this.getLocationOnScreen();
      point.x = param1Int1 - point.x;
      point.y = param1Int2 - point.y;
      int i = JTextComponent.this.viewToModel(point);
      return (i >= JTextComponent.this.composedTextStart.getOffset() && i <= JTextComponent.this.composedTextEnd.getOffset()) ? TextHitInfo.leading(i - JTextComponent.this.composedTextStart.getOffset()) : null;
    }
    
    public Rectangle getTextLocation(TextHitInfo param1TextHitInfo) {
      Rectangle rectangle;
      try {
        rectangle = JTextComponent.this.modelToView(JTextComponent.this.getCaretPosition());
        if (rectangle != null) {
          Point point = JTextComponent.this.getLocationOnScreen();
          rectangle.translate(point.x, point.y);
        } 
      } catch (BadLocationException badLocationException) {
        rectangle = null;
      } 
      if (rectangle == null)
        rectangle = new Rectangle(); 
      return rectangle;
    }
    
    public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] param1ArrayOfAttribute) {
      String str = JTextComponent.this.getSelectedText();
      return (str != null) ? (new AttributedString(str)).getIterator() : null;
    }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent) { JTextComponent.this.latestCommittedTextStart = JTextComponent.this.latestCommittedTextEnd = null; }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent) { JTextComponent.this.latestCommittedTextStart = JTextComponent.this.latestCommittedTextEnd = null; }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent) { JTextComponent.this.latestCommittedTextStart = JTextComponent.this.latestCommittedTextEnd = null; }
  }
  
  public static class KeyBinding {
    public KeyStroke key;
    
    public String actionName;
    
    public KeyBinding(KeyStroke param1KeyStroke, String param1String) {
      this.key = param1KeyStroke;
      this.actionName = param1String;
    }
  }
  
  static class KeymapActionMap extends ActionMap {
    private Keymap keymap;
    
    KeymapActionMap(Keymap param1Keymap) { this.keymap = param1Keymap; }
    
    public Object[] keys() {
      Object[] arrayOfObject1 = super.keys();
      Action[] arrayOfAction = this.keymap.getBoundActions();
      byte b1 = (arrayOfObject1 == null) ? 0 : arrayOfObject1.length;
      byte b2 = (arrayOfAction == null) ? 0 : arrayOfAction.length;
      boolean bool = (this.keymap.getDefaultAction() != null) ? 1 : 0;
      if (bool)
        b2++; 
      if (!b1) {
        if (bool) {
          Object[] arrayOfObject = new Object[b2];
          if (b2 > 1)
            System.arraycopy(arrayOfAction, 0, arrayOfObject, 0, b2 - 1); 
          arrayOfObject[b2 - 1] = JTextComponent.KeymapWrapper.DefaultActionKey;
          return arrayOfObject;
        } 
        return arrayOfAction;
      } 
      if (b2 == 0)
        return arrayOfObject1; 
      Object[] arrayOfObject2 = new Object[b1 + b2];
      System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, b1);
      if (bool) {
        if (b2 > 1)
          System.arraycopy(arrayOfAction, 0, arrayOfObject2, b1, b2 - 1); 
        arrayOfObject2[b1 + b2 - 1] = JTextComponent.KeymapWrapper.DefaultActionKey;
      } else {
        System.arraycopy(arrayOfAction, 0, arrayOfObject2, b1, b2);
      } 
      return arrayOfObject2;
    }
    
    public int size() {
      Action[] arrayOfAction = this.keymap.getBoundActions();
      int i = (arrayOfAction == null) ? 0 : arrayOfAction.length;
      if (this.keymap.getDefaultAction() != null)
        i++; 
      return super.size() + i;
    }
    
    public Action get(Object param1Object) {
      Action action = super.get(param1Object);
      if (action == null)
        if (param1Object == JTextComponent.KeymapWrapper.DefaultActionKey) {
          action = this.keymap.getDefaultAction();
        } else if (param1Object instanceof Action) {
          action = (Action)param1Object;
        }  
      return action;
    }
  }
  
  static class KeymapWrapper extends InputMap {
    static final Object DefaultActionKey = new Object();
    
    private Keymap keymap;
    
    KeymapWrapper(Keymap param1Keymap) { this.keymap = param1Keymap; }
    
    public KeyStroke[] keys() {
      KeyStroke[] arrayOfKeyStroke1 = super.keys();
      KeyStroke[] arrayOfKeyStroke2 = this.keymap.getBoundKeyStrokes();
      byte b1 = (arrayOfKeyStroke1 == null) ? 0 : arrayOfKeyStroke1.length;
      byte b2 = (arrayOfKeyStroke2 == null) ? 0 : arrayOfKeyStroke2.length;
      if (!b1)
        return arrayOfKeyStroke2; 
      if (!b2)
        return arrayOfKeyStroke1; 
      KeyStroke[] arrayOfKeyStroke3 = new KeyStroke[b1 + b2];
      System.arraycopy(arrayOfKeyStroke1, 0, arrayOfKeyStroke3, 0, b1);
      System.arraycopy(arrayOfKeyStroke2, 0, arrayOfKeyStroke3, b1, b2);
      return arrayOfKeyStroke3;
    }
    
    public int size() {
      KeyStroke[] arrayOfKeyStroke = this.keymap.getBoundKeyStrokes();
      int i = (arrayOfKeyStroke == null) ? 0 : arrayOfKeyStroke.length;
      return super.size() + i;
    }
    
    public Object get(KeyStroke param1KeyStroke) {
      Object object = this.keymap.getAction(param1KeyStroke);
      if (object == null) {
        object = super.get(param1KeyStroke);
        if (object == null && param1KeyStroke.getKeyChar() != Character.MAX_VALUE && this.keymap.getDefaultAction() != null)
          object = DefaultActionKey; 
      } 
      return object;
    }
  }
  
  static class MutableCaretEvent extends CaretEvent implements ChangeListener, FocusListener, MouseListener {
    private boolean dragActive;
    
    private int dot;
    
    private int mark;
    
    MutableCaretEvent(JTextComponent param1JTextComponent) { super(param1JTextComponent); }
    
    final void fire() {
      JTextComponent jTextComponent = (JTextComponent)getSource();
      if (jTextComponent != null) {
        Caret caret = jTextComponent.getCaret();
        this.dot = caret.getDot();
        this.mark = caret.getMark();
        jTextComponent.fireCaretUpdate(this);
      } 
    }
    
    public final String toString() { return "dot=" + this.dot + ",mark=" + this.mark; }
    
    public final int getDot() { return this.dot; }
    
    public final int getMark() { return this.mark; }
    
    public final void stateChanged(ChangeEvent param1ChangeEvent) {
      if (!this.dragActive)
        fire(); 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) { AppContext.getAppContext().put(FOCUSED_COMPONENT, param1FocusEvent.getSource()); }
    
    public void focusLost(FocusEvent param1FocusEvent) {}
    
    public final void mousePressed(MouseEvent param1MouseEvent) { this.dragActive = true; }
    
    public final void mouseReleased(MouseEvent param1MouseEvent) {
      this.dragActive = false;
      fire();
    }
    
    public final void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public final void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public final void mouseExited(MouseEvent param1MouseEvent) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\JTextComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */