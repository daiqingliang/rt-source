package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.im.InputContext;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.swing.text.Keymap;
import javax.swing.text.Position;
import javax.swing.text.TextAction;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import sun.awt.AppContext;
import sun.swing.DefaultLookup;

public abstract class BasicTextUI extends TextUI implements ViewFactory {
  private static BasicCursor textCursor = new BasicCursor(2);
  
  private static final EditorKit defaultKit = new DefaultEditorKit();
  
  JTextComponent editor;
  
  boolean painted = false;
  
  RootView rootView = new RootView();
  
  UpdateHandler updateHandler = new UpdateHandler();
  
  private static final TransferHandler defaultTransferHandler = new TextTransferHandler();
  
  private final DragListener dragListener = getDragListener();
  
  private static final Position.Bias[] discardBias = new Position.Bias[1];
  
  private DefaultCaret dropCaret;
  
  protected Caret createCaret() { return new BasicCaret(); }
  
  protected Highlighter createHighlighter() { return new BasicHighlighter(); }
  
  protected String getKeymapName() {
    String str = getClass().getName();
    int i = str.lastIndexOf('.');
    if (i >= 0)
      str = str.substring(i + 1, str.length()); 
    return str;
  }
  
  protected Keymap createKeymap() {
    String str = getKeymapName();
    Keymap keymap = JTextComponent.getKeymap(str);
    if (keymap == null) {
      Keymap keymap1 = JTextComponent.getKeymap("default");
      keymap = JTextComponent.addKeymap(str, keymap1);
      String str1 = getPropertyPrefix();
      Object object = DefaultLookup.get(this.editor, this, str1 + ".keyBindings");
      if (object != null && object instanceof KeyBinding[]) {
        KeyBinding[] arrayOfKeyBinding = (KeyBinding[])object;
        JTextComponent.loadKeymap(keymap, arrayOfKeyBinding, getComponent().getActions());
      } 
    } 
    return keymap;
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getPropertyName().equals("editable") || paramPropertyChangeEvent.getPropertyName().equals("enabled"))
      updateBackground((JTextComponent)paramPropertyChangeEvent.getSource()); 
  }
  
  private void updateBackground(JTextComponent paramJTextComponent) {
    if (this instanceof javax.swing.plaf.synth.SynthUI || paramJTextComponent instanceof javax.swing.JTextArea)
      return; 
    Color color = paramJTextComponent.getBackground();
    if (color instanceof UIResource) {
      String str = getPropertyPrefix();
      Color color1 = DefaultLookup.getColor(paramJTextComponent, this, str + ".disabledBackground", null);
      Color color2 = DefaultLookup.getColor(paramJTextComponent, this, str + ".inactiveBackground", null);
      Color color3 = DefaultLookup.getColor(paramJTextComponent, this, str + ".background", null);
      if ((paramJTextComponent instanceof javax.swing.JTextArea || paramJTextComponent instanceof JEditorPane) && color != color1 && color != color2 && color != color3)
        return; 
      Color color4 = null;
      if (!paramJTextComponent.isEnabled())
        color4 = color1; 
      if (color4 == null && !paramJTextComponent.isEditable())
        color4 = color2; 
      if (color4 == null)
        color4 = color3; 
      if (color4 != null && color4 != color)
        paramJTextComponent.setBackground(color4); 
    } 
  }
  
  protected abstract String getPropertyPrefix();
  
  protected void installDefaults() {
    String str = getPropertyPrefix();
    Font font = this.editor.getFont();
    if (font == null || font instanceof UIResource)
      this.editor.setFont(UIManager.getFont(str + ".font")); 
    Color color1 = this.editor.getBackground();
    if (color1 == null || color1 instanceof UIResource)
      this.editor.setBackground(UIManager.getColor(str + ".background")); 
    Color color2 = this.editor.getForeground();
    if (color2 == null || color2 instanceof UIResource)
      this.editor.setForeground(UIManager.getColor(str + ".foreground")); 
    Color color3 = this.editor.getCaretColor();
    if (color3 == null || color3 instanceof UIResource)
      this.editor.setCaretColor(UIManager.getColor(str + ".caretForeground")); 
    Color color4 = this.editor.getSelectionColor();
    if (color4 == null || color4 instanceof UIResource)
      this.editor.setSelectionColor(UIManager.getColor(str + ".selectionBackground")); 
    Color color5 = this.editor.getSelectedTextColor();
    if (color5 == null || color5 instanceof UIResource)
      this.editor.setSelectedTextColor(UIManager.getColor(str + ".selectionForeground")); 
    Color color6 = this.editor.getDisabledTextColor();
    if (color6 == null || color6 instanceof UIResource)
      this.editor.setDisabledTextColor(UIManager.getColor(str + ".inactiveForeground")); 
    Border border = this.editor.getBorder();
    if (border == null || border instanceof UIResource)
      this.editor.setBorder(UIManager.getBorder(str + ".border")); 
    Insets insets = this.editor.getMargin();
    if (insets == null || insets instanceof UIResource)
      this.editor.setMargin(UIManager.getInsets(str + ".margin")); 
    updateCursor();
  }
  
  private void installDefaults2() {
    this.editor.addMouseListener(this.dragListener);
    this.editor.addMouseMotionListener(this.dragListener);
    String str = getPropertyPrefix();
    Caret caret = this.editor.getCaret();
    if (caret == null || caret instanceof UIResource) {
      caret = createCaret();
      this.editor.setCaret(caret);
      int i = DefaultLookup.getInt(getComponent(), this, str + ".caretBlinkRate", 500);
      caret.setBlinkRate(i);
    } 
    Highlighter highlighter = this.editor.getHighlighter();
    if (highlighter == null || highlighter instanceof UIResource)
      this.editor.setHighlighter(createHighlighter()); 
    TransferHandler transferHandler = this.editor.getTransferHandler();
    if (transferHandler == null || transferHandler instanceof UIResource)
      this.editor.setTransferHandler(getTransferHandler()); 
  }
  
  protected void uninstallDefaults() {
    this.editor.removeMouseListener(this.dragListener);
    this.editor.removeMouseMotionListener(this.dragListener);
    if (this.editor.getCaretColor() instanceof UIResource)
      this.editor.setCaretColor(null); 
    if (this.editor.getSelectionColor() instanceof UIResource)
      this.editor.setSelectionColor(null); 
    if (this.editor.getDisabledTextColor() instanceof UIResource)
      this.editor.setDisabledTextColor(null); 
    if (this.editor.getSelectedTextColor() instanceof UIResource)
      this.editor.setSelectedTextColor(null); 
    if (this.editor.getBorder() instanceof UIResource)
      this.editor.setBorder(null); 
    if (this.editor.getMargin() instanceof UIResource)
      this.editor.setMargin(null); 
    if (this.editor.getCaret() instanceof UIResource)
      this.editor.setCaret(null); 
    if (this.editor.getHighlighter() instanceof UIResource)
      this.editor.setHighlighter(null); 
    if (this.editor.getTransferHandler() instanceof UIResource)
      this.editor.setTransferHandler(null); 
    if (this.editor.getCursor() instanceof UIResource)
      this.editor.setCursor(null); 
  }
  
  protected void installListeners() {}
  
  protected void uninstallListeners() {}
  
  protected void installKeyboardActions() {
    this.editor.setKeymap(createKeymap());
    InputMap inputMap = getInputMap();
    if (inputMap != null)
      SwingUtilities.replaceUIInputMap(this.editor, 0, inputMap); 
    ActionMap actionMap = getActionMap();
    if (actionMap != null)
      SwingUtilities.replaceUIActionMap(this.editor, actionMap); 
    updateFocusAcceleratorBinding(false);
  }
  
  InputMap getInputMap() {
    InputMapUIResource inputMapUIResource = new InputMapUIResource();
    InputMap inputMap = (InputMap)DefaultLookup.get(this.editor, this, getPropertyPrefix() + ".focusInputMap");
    if (inputMap != null)
      inputMapUIResource.setParent(inputMap); 
    return inputMapUIResource;
  }
  
  void updateFocusAcceleratorBinding(boolean paramBoolean) {
    char c = this.editor.getFocusAccelerator();
    if (paramBoolean || c != '\000') {
      InputMap inputMap = SwingUtilities.getUIInputMap(this.editor, 2);
      if (inputMap == null && c != '\000') {
        inputMap = new ComponentInputMapUIResource(this.editor);
        SwingUtilities.replaceUIInputMap(this.editor, 2, inputMap);
        ActionMap actionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(this.editor, actionMap);
      } 
      if (inputMap != null) {
        inputMap.clear();
        if (c != '\000')
          inputMap.put(KeyStroke.getKeyStroke(c, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "requestFocus"); 
      } 
    } 
  }
  
  void updateFocusTraversalKeys() {
    EditorKit editorKit = getEditorKit(this.editor);
    if (editorKit != null && editorKit instanceof DefaultEditorKit) {
      Set set1 = this.editor.getFocusTraversalKeys(0);
      Set set2 = this.editor.getFocusTraversalKeys(1);
      HashSet hashSet1 = new HashSet(set1);
      HashSet hashSet2 = new HashSet(set2);
      if (this.editor.isEditable()) {
        hashSet1.remove(KeyStroke.getKeyStroke(9, 0));
        hashSet2.remove(KeyStroke.getKeyStroke(9, 1));
      } else {
        hashSet1.add(KeyStroke.getKeyStroke(9, 0));
        hashSet2.add(KeyStroke.getKeyStroke(9, 1));
      } 
      LookAndFeel.installProperty(this.editor, "focusTraversalKeysForward", hashSet1);
      LookAndFeel.installProperty(this.editor, "focusTraversalKeysBackward", hashSet2);
    } 
  }
  
  private void updateCursor() {
    if (!this.editor.isCursorSet() || this.editor.getCursor() instanceof UIResource) {
      BasicCursor basicCursor = this.editor.isEditable() ? textCursor : null;
      this.editor.setCursor(basicCursor);
    } 
  }
  
  TransferHandler getTransferHandler() { return defaultTransferHandler; }
  
  ActionMap getActionMap() {
    String str = getPropertyPrefix() + ".actionMap";
    ActionMap actionMap = (ActionMap)UIManager.get(str);
    if (actionMap == null) {
      actionMap = createActionMap();
      if (actionMap != null)
        UIManager.getLookAndFeelDefaults().put(str, actionMap); 
    } 
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    actionMapUIResource.put("requestFocus", new FocusAction());
    if (getEditorKit(this.editor) instanceof DefaultEditorKit && actionMap != null) {
      Action action = actionMap.get("insert-break");
      if (action != null && action instanceof DefaultEditorKit.InsertBreakAction) {
        TextActionWrapper textActionWrapper = new TextActionWrapper((TextAction)action);
        actionMapUIResource.put(textActionWrapper.getValue("Name"), textActionWrapper);
      } 
    } 
    if (actionMap != null)
      actionMapUIResource.setParent(actionMap); 
    return actionMapUIResource;
  }
  
  ActionMap createActionMap() {
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    for (Action action : this.editor.getActions())
      actionMapUIResource.put(action.getValue("Name"), action); 
    actionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    actionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    actionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    return actionMapUIResource;
  }
  
  protected void uninstallKeyboardActions() {
    this.editor.setKeymap(null);
    SwingUtilities.replaceUIInputMap(this.editor, 2, null);
    SwingUtilities.replaceUIActionMap(this.editor, null);
  }
  
  protected void paintBackground(Graphics paramGraphics) {
    paramGraphics.setColor(this.editor.getBackground());
    paramGraphics.fillRect(0, 0, this.editor.getWidth(), this.editor.getHeight());
  }
  
  protected final JTextComponent getComponent() { return this.editor; }
  
  protected void modelChanged() {
    ViewFactory viewFactory = this.rootView.getViewFactory();
    Document document = this.editor.getDocument();
    Element element = document.getDefaultRootElement();
    setView(viewFactory.create(element));
  }
  
  protected final void setView(View paramView) {
    this.rootView.setView(paramView);
    this.painted = false;
    this.editor.revalidate();
    this.editor.repaint();
  }
  
  protected void paintSafely(Graphics paramGraphics) {
    this.painted = true;
    Highlighter highlighter = this.editor.getHighlighter();
    Caret caret = this.editor.getCaret();
    if (this.editor.isOpaque())
      paintBackground(paramGraphics); 
    if (highlighter != null)
      highlighter.paint(paramGraphics); 
    Rectangle rectangle = getVisibleEditorRect();
    if (rectangle != null)
      this.rootView.paint(paramGraphics, rectangle); 
    if (caret != null)
      caret.paint(paramGraphics); 
    if (this.dropCaret != null)
      this.dropCaret.paint(paramGraphics); 
  }
  
  public void installUI(JComponent paramJComponent) {
    if (paramJComponent instanceof JTextComponent) {
      this.editor = (JTextComponent)paramJComponent;
      LookAndFeel.installProperty(this.editor, "opaque", Boolean.TRUE);
      LookAndFeel.installProperty(this.editor, "autoscrolls", Boolean.TRUE);
      installDefaults();
      installDefaults2();
      this.editor.addPropertyChangeListener(this.updateHandler);
      Document document = this.editor.getDocument();
      if (document == null) {
        this.editor.setDocument(getEditorKit(this.editor).createDefaultDocument());
      } else {
        document.addDocumentListener(this.updateHandler);
        modelChanged();
      } 
      installListeners();
      installKeyboardActions();
      LayoutManager layoutManager = this.editor.getLayout();
      if (layoutManager == null || layoutManager instanceof UIResource)
        this.editor.setLayout(this.updateHandler); 
      updateBackground(this.editor);
    } else {
      throw new Error("TextUI needs JTextComponent");
    } 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    this.editor.removePropertyChangeListener(this.updateHandler);
    this.editor.getDocument().removeDocumentListener(this.updateHandler);
    this.painted = false;
    uninstallDefaults();
    this.rootView.setView(null);
    paramJComponent.removeAll();
    LayoutManager layoutManager = paramJComponent.getLayout();
    if (layoutManager instanceof UIResource)
      paramJComponent.setLayout(null); 
    uninstallKeyboardActions();
    uninstallListeners();
    this.editor = null;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) { paint(paramGraphics, paramJComponent); }
  
  public final void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (this.rootView.getViewCount() > 0 && this.rootView.getView(false) != null) {
      document = this.editor.getDocument();
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readLock(); 
      try {
        paintSafely(paramGraphics);
      } finally {
        if (document instanceof AbstractDocument)
          ((AbstractDocument)document).readUnlock(); 
      } 
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    document = this.editor.getDocument();
    Insets insets = paramJComponent.getInsets();
    Dimension dimension = paramJComponent.getSize();
    if (document instanceof AbstractDocument)
      ((AbstractDocument)document).readLock(); 
    try {
      if (dimension.width > insets.left + insets.right && dimension.height > insets.top + insets.bottom) {
        this.rootView.setSize((dimension.width - insets.left - insets.right), (dimension.height - insets.top - insets.bottom));
      } else if (dimension.width == 0 && dimension.height == 0) {
        this.rootView.setSize(2.14748365E9F, 2.14748365E9F);
      } 
      dimension.width = (int)Math.min((long)this.rootView.getPreferredSpan(0) + insets.left + insets.right, 2147483647L);
      dimension.height = (int)Math.min((long)this.rootView.getPreferredSpan(1) + insets.top + insets.bottom, 2147483647L);
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    document = this.editor.getDocument();
    Insets insets = paramJComponent.getInsets();
    Dimension dimension = new Dimension();
    if (document instanceof AbstractDocument)
      ((AbstractDocument)document).readLock(); 
    try {
      dimension.width = (int)this.rootView.getMinimumSpan(0) + insets.left + insets.right;
      dimension.height = (int)this.rootView.getMinimumSpan(1) + insets.top + insets.bottom;
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    document = this.editor.getDocument();
    Insets insets = paramJComponent.getInsets();
    Dimension dimension = new Dimension();
    if (document instanceof AbstractDocument)
      ((AbstractDocument)document).readLock(); 
    try {
      dimension.width = (int)Math.min((long)this.rootView.getMaximumSpan(0) + insets.left + insets.right, 2147483647L);
      dimension.height = (int)Math.min((long)this.rootView.getMaximumSpan(1) + insets.top + insets.bottom, 2147483647L);
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    return dimension;
  }
  
  protected Rectangle getVisibleEditorRect() {
    Rectangle rectangle = this.editor.getBounds();
    if (rectangle.width > 0 && rectangle.height > 0) {
      rectangle.x = rectangle.y = 0;
      Insets insets = this.editor.getInsets();
      rectangle.x += insets.left;
      rectangle.y += insets.top;
      rectangle.width -= insets.left + insets.right;
      rectangle.height -= insets.top + insets.bottom;
      return rectangle;
    } 
    return null;
  }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException { return modelToView(paramJTextComponent, paramInt, Position.Bias.Forward); }
  
  public Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt, Position.Bias paramBias) throws BadLocationException {
    document = this.editor.getDocument();
    if (document instanceof AbstractDocument)
      ((AbstractDocument)document).readLock(); 
    try {
      Rectangle rectangle = getVisibleEditorRect();
      if (rectangle != null) {
        this.rootView.setSize(rectangle.width, rectangle.height);
        Shape shape = this.rootView.modelToView(paramInt, rectangle, paramBias);
        if (shape != null)
          return shape.getBounds(); 
      } 
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    return null;
  }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint) { return viewToModel(paramJTextComponent, paramPoint, discardBias); }
  
  public int viewToModel(JTextComponent paramJTextComponent, Point paramPoint, Position.Bias[] paramArrayOfBias) {
    int i = -1;
    document = this.editor.getDocument();
    if (document instanceof AbstractDocument)
      ((AbstractDocument)document).readLock(); 
    try {
      Rectangle rectangle = getVisibleEditorRect();
      if (rectangle != null) {
        this.rootView.setSize(rectangle.width, rectangle.height);
        i = this.rootView.viewToModel(paramPoint.x, paramPoint.y, rectangle, paramArrayOfBias);
      } 
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    return i;
  }
  
  public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    document = this.editor.getDocument();
    if (document instanceof AbstractDocument)
      ((AbstractDocument)document).readLock(); 
    try {
      if (this.painted) {
        Rectangle rectangle = getVisibleEditorRect();
        if (rectangle != null)
          this.rootView.setSize(rectangle.width, rectangle.height); 
        return this.rootView.getNextVisualPositionFrom(paramInt1, paramBias, rectangle, paramInt2, paramArrayOfBias);
      } 
    } finally {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readUnlock(); 
    } 
    return -1;
  }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2) { damageRange(paramJTextComponent, paramInt1, paramInt2, Position.Bias.Forward, Position.Bias.Backward); }
  
  public void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2, Position.Bias paramBias1, Position.Bias paramBias2) {
    if (this.painted) {
      Rectangle rectangle = getVisibleEditorRect();
      if (rectangle != null) {
        document = paramJTextComponent.getDocument();
        if (document instanceof AbstractDocument)
          ((AbstractDocument)document).readLock(); 
        try {
          this.rootView.setSize(rectangle.width, rectangle.height);
          Shape shape = this.rootView.modelToView(paramInt1, paramBias1, paramInt2, paramBias2, rectangle);
          Rectangle rectangle1 = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
          this.editor.repaint(rectangle1.x, rectangle1.y, rectangle1.width, rectangle1.height);
        } catch (BadLocationException badLocationException) {
        
        } finally {
          if (document instanceof AbstractDocument)
            ((AbstractDocument)document).readUnlock(); 
        } 
      } 
    } 
  }
  
  public EditorKit getEditorKit(JTextComponent paramJTextComponent) { return defaultKit; }
  
  public View getRootView(JTextComponent paramJTextComponent) { return this.rootView; }
  
  public String getToolTipText(JTextComponent paramJTextComponent, Point paramPoint) {
    if (!this.painted)
      return null; 
    document = this.editor.getDocument();
    String str = null;
    Rectangle rectangle = getVisibleEditorRect();
    if (rectangle != null) {
      if (document instanceof AbstractDocument)
        ((AbstractDocument)document).readLock(); 
      try {
        str = this.rootView.getToolTipText(paramPoint.x, paramPoint.y, rectangle);
      } finally {
        if (document instanceof AbstractDocument)
          ((AbstractDocument)document).readUnlock(); 
      } 
    } 
    return str;
  }
  
  public View create(Element paramElement) { return null; }
  
  public View create(Element paramElement, int paramInt1, int paramInt2) { return null; }
  
  private static DragListener getDragListener() {
    synchronized (DragListener.class) {
      DragListener dragListener1 = (DragListener)AppContext.getAppContext().get(DragListener.class);
      if (dragListener1 == null) {
        dragListener1 = new DragListener();
        AppContext.getAppContext().put(DragListener.class, dragListener1);
      } 
      return dragListener1;
    } 
  }
  
  public static class BasicCaret extends DefaultCaret implements UIResource {}
  
  static class BasicCursor extends Cursor implements UIResource {
    BasicCursor(int param1Int) { super(param1Int); }
    
    BasicCursor(String param1String) { super(param1String); }
  }
  
  public static class BasicHighlighter extends DefaultHighlighter implements UIResource {}
  
  static class DragListener extends MouseInputAdapter implements DragRecognitionSupport.BeforeDrag {
    private boolean dragStarted;
    
    public void dragStarting(MouseEvent param1MouseEvent) { this.dragStarted = true; }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      JTextComponent jTextComponent = (JTextComponent)param1MouseEvent.getSource();
      if (jTextComponent.getDragEnabled()) {
        this.dragStarted = false;
        if (isDragPossible(param1MouseEvent) && DragRecognitionSupport.mousePressed(param1MouseEvent))
          param1MouseEvent.consume(); 
      } 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      JTextComponent jTextComponent = (JTextComponent)param1MouseEvent.getSource();
      if (jTextComponent.getDragEnabled()) {
        if (this.dragStarted)
          param1MouseEvent.consume(); 
        DragRecognitionSupport.mouseReleased(param1MouseEvent);
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      JTextComponent jTextComponent = (JTextComponent)param1MouseEvent.getSource();
      if (jTextComponent.getDragEnabled() && (this.dragStarted || DragRecognitionSupport.mouseDragged(param1MouseEvent, this)))
        param1MouseEvent.consume(); 
    }
    
    protected boolean isDragPossible(MouseEvent param1MouseEvent) {
      JTextComponent jTextComponent = (JTextComponent)param1MouseEvent.getSource();
      if (jTextComponent.isEnabled()) {
        Caret caret = jTextComponent.getCaret();
        int i = caret.getDot();
        int j = caret.getMark();
        if (i != j) {
          Point point = new Point(param1MouseEvent.getX(), param1MouseEvent.getY());
          int k = jTextComponent.viewToModel(point);
          int m = Math.min(i, j);
          int n = Math.max(i, j);
          if (k >= m && k < n)
            return true; 
        } 
      } 
      return false;
    }
  }
  
  class FocusAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) { BasicTextUI.this.editor.requestFocus(); }
    
    public boolean isEnabled() { return BasicTextUI.this.editor.isEditable(); }
  }
  
  class RootView extends View {
    private View view;
    
    RootView() { super(null); }
    
    void setView(View param1View) {
      View view1 = this.view;
      this.view = null;
      if (view1 != null)
        view1.setParent(null); 
      if (param1View != null)
        param1View.setParent(this); 
      this.view = param1View;
    }
    
    public AttributeSet getAttributes() { return null; }
    
    public float getPreferredSpan(int param1Int) { return (this.view != null) ? this.view.getPreferredSpan(param1Int) : 10.0F; }
    
    public float getMinimumSpan(int param1Int) { return (this.view != null) ? this.view.getMinimumSpan(param1Int) : 10.0F; }
    
    public float getMaximumSpan(int param1Int) { return 2.14748365E9F; }
    
    public void preferenceChanged(View param1View, boolean param1Boolean1, boolean param1Boolean2) { BasicTextUI.this.editor.revalidate(); }
    
    public float getAlignment(int param1Int) { return (this.view != null) ? this.view.getAlignment(param1Int) : 0.0F; }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      if (this.view != null) {
        Rectangle rectangle = (param1Shape instanceof Rectangle) ? (Rectangle)param1Shape : param1Shape.getBounds();
        setSize(rectangle.width, rectangle.height);
        this.view.paint(param1Graphics, param1Shape);
      } 
    }
    
    public void setParent(View param1View) { throw new Error("Can't set parent on root view"); }
    
    public int getViewCount() { return 1; }
    
    public View getView(int param1Int) { return this.view; }
    
    public int getViewIndex(int param1Int, Position.Bias param1Bias) { return 0; }
    
    public Shape getChildAllocation(int param1Int, Shape param1Shape) { return param1Shape; }
    
    public Shape modelToView(int param1Int, Shape param1Shape, Position.Bias param1Bias) throws BadLocationException { return (this.view != null) ? this.view.modelToView(param1Int, param1Shape, param1Bias) : null; }
    
    public Shape modelToView(int param1Int1, Position.Bias param1Bias1, int param1Int2, Position.Bias param1Bias2, Shape param1Shape) throws BadLocationException { return (this.view != null) ? this.view.modelToView(param1Int1, param1Bias1, param1Int2, param1Bias2, param1Shape) : null; }
    
    public int viewToModel(float param1Float1, float param1Float2, Shape param1Shape, Position.Bias[] param1ArrayOfBias) { return (this.view != null) ? this.view.viewToModel(param1Float1, param1Float2, param1Shape, param1ArrayOfBias) : -1; }
    
    public int getNextVisualPositionFrom(int param1Int1, Position.Bias param1Bias, Shape param1Shape, int param1Int2, Position.Bias[] param1ArrayOfBias) throws BadLocationException {
      if (param1Int1 < -1)
        throw new BadLocationException("invalid position", param1Int1); 
      if (this.view != null) {
        int i = this.view.getNextVisualPositionFrom(param1Int1, param1Bias, param1Shape, param1Int2, param1ArrayOfBias);
        if (i != -1) {
          param1Int1 = i;
        } else {
          param1ArrayOfBias[0] = param1Bias;
        } 
      } 
      return param1Int1;
    }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      if (this.view != null)
        this.view.insertUpdate(param1DocumentEvent, param1Shape, param1ViewFactory); 
    }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      if (this.view != null)
        this.view.removeUpdate(param1DocumentEvent, param1Shape, param1ViewFactory); 
    }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      if (this.view != null)
        this.view.changedUpdate(param1DocumentEvent, param1Shape, param1ViewFactory); 
    }
    
    public Document getDocument() { return BasicTextUI.this.editor.getDocument(); }
    
    public int getStartOffset() { return (this.view != null) ? this.view.getStartOffset() : getElement().getStartOffset(); }
    
    public int getEndOffset() { return (this.view != null) ? this.view.getEndOffset() : getElement().getEndOffset(); }
    
    public Element getElement() { return (this.view != null) ? this.view.getElement() : BasicTextUI.this.editor.getDocument().getDefaultRootElement(); }
    
    public View breakView(int param1Int, float param1Float, Shape param1Shape) { throw new Error("Can't break root view"); }
    
    public int getResizeWeight(int param1Int) { return (this.view != null) ? this.view.getResizeWeight(param1Int) : 0; }
    
    public void setSize(float param1Float1, float param1Float2) {
      if (this.view != null)
        this.view.setSize(param1Float1, param1Float2); 
    }
    
    public Container getContainer() { return BasicTextUI.this.editor; }
    
    public ViewFactory getViewFactory() {
      EditorKit editorKit = BasicTextUI.this.getEditorKit(BasicTextUI.this.editor);
      ViewFactory viewFactory = editorKit.getViewFactory();
      return (viewFactory != null) ? viewFactory : BasicTextUI.this;
    }
  }
  
  class TextActionWrapper extends TextAction {
    TextAction action = null;
    
    public TextActionWrapper(TextAction param1TextAction) {
      super((String)param1TextAction.getValue("Name"));
      this.action = param1TextAction;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { this.action.actionPerformed(param1ActionEvent); }
    
    public boolean isEnabled() { return (BasicTextUI.this.editor == null || BasicTextUI.this.editor.isEditable()) ? this.action.isEnabled() : 0; }
  }
  
  static class TextTransferHandler extends TransferHandler implements UIResource {
    private JTextComponent exportComp;
    
    private boolean shouldRemove;
    
    private int p0;
    
    private int p1;
    
    private boolean modeBetween = false;
    
    private boolean isDrop = false;
    
    private int dropAction = 2;
    
    private Position.Bias dropBias;
    
    protected DataFlavor getImportFlavor(DataFlavor[] param1ArrayOfDataFlavor, JTextComponent param1JTextComponent) {
      DataFlavor dataFlavor1 = null;
      DataFlavor dataFlavor2 = null;
      DataFlavor dataFlavor3 = null;
      if (param1JTextComponent instanceof JEditorPane) {
        for (byte b1 = 0; b1 < param1ArrayOfDataFlavor.length; b1++) {
          String str = param1ArrayOfDataFlavor[b1].getMimeType();
          if (str.startsWith(((JEditorPane)param1JTextComponent).getEditorKit().getContentType()))
            return param1ArrayOfDataFlavor[b1]; 
          if (dataFlavor1 == null && str.startsWith("text/plain")) {
            dataFlavor1 = param1ArrayOfDataFlavor[b1];
          } else if (dataFlavor2 == null && str.startsWith("application/x-java-jvm-local-objectref") && param1ArrayOfDataFlavor[b1].getRepresentationClass() == String.class) {
            dataFlavor2 = param1ArrayOfDataFlavor[b1];
          } else if (dataFlavor3 == null && param1ArrayOfDataFlavor[b1].equals(DataFlavor.stringFlavor)) {
            dataFlavor3 = param1ArrayOfDataFlavor[b1];
          } 
        } 
        return (dataFlavor1 != null) ? dataFlavor1 : ((dataFlavor2 != null) ? dataFlavor2 : ((dataFlavor3 != null) ? dataFlavor3 : null));
      } 
      for (byte b = 0; b < param1ArrayOfDataFlavor.length; b++) {
        String str = param1ArrayOfDataFlavor[b].getMimeType();
        if (str.startsWith("text/plain"))
          return param1ArrayOfDataFlavor[b]; 
        if (dataFlavor2 == null && str.startsWith("application/x-java-jvm-local-objectref") && param1ArrayOfDataFlavor[b].getRepresentationClass() == String.class) {
          dataFlavor2 = param1ArrayOfDataFlavor[b];
        } else if (dataFlavor3 == null && param1ArrayOfDataFlavor[b].equals(DataFlavor.stringFlavor)) {
          dataFlavor3 = param1ArrayOfDataFlavor[b];
        } 
      } 
      return (dataFlavor2 != null) ? dataFlavor2 : ((dataFlavor3 != null) ? dataFlavor3 : null);
    }
    
    protected void handleReaderImport(Reader param1Reader, JTextComponent param1JTextComponent, boolean param1Boolean) throws BadLocationException, IOException {
      if (param1Boolean) {
        int i = param1JTextComponent.getSelectionStart();
        int j = param1JTextComponent.getSelectionEnd();
        int k = j - i;
        EditorKit editorKit = param1JTextComponent.getUI().getEditorKit(param1JTextComponent);
        Document document = param1JTextComponent.getDocument();
        if (k > 0)
          document.remove(i, k); 
        editorKit.read(param1Reader, document, i);
      } else {
        char[] arrayOfChar = new char[1024];
        boolean bool = false;
        StringBuffer stringBuffer = null;
        int i;
        while ((i = param1Reader.read(arrayOfChar, 0, arrayOfChar.length)) != -1) {
          if (stringBuffer == null)
            stringBuffer = new StringBuffer(i); 
          int j = 0;
          for (byte b = 0; b < i; b++) {
            switch (arrayOfChar[b]) {
              case '\r':
                if (bool) {
                  if (!b) {
                    stringBuffer.append('\n');
                    break;
                  } 
                  arrayOfChar[b - true] = '\n';
                  break;
                } 
                bool = true;
                break;
              case '\n':
                if (bool) {
                  if (b > j + true)
                    stringBuffer.append(arrayOfChar, j, b - j - true); 
                  bool = false;
                  j = b;
                } 
                break;
              default:
                if (bool) {
                  if (b == 0) {
                    stringBuffer.append('\n');
                  } else {
                    arrayOfChar[b - 1] = '\n';
                  } 
                  bool = false;
                } 
                break;
            } 
          } 
          if (j < i) {
            if (bool) {
              if (j < i - 1)
                stringBuffer.append(arrayOfChar, j, i - j - 1); 
              continue;
            } 
            stringBuffer.append(arrayOfChar, j, i - j);
          } 
        } 
        if (bool)
          stringBuffer.append('\n'); 
        param1JTextComponent.replaceSelection((stringBuffer != null) ? stringBuffer.toString() : "");
      } 
    }
    
    public int getSourceActions(JComponent param1JComponent) { return (param1JComponent instanceof javax.swing.JPasswordField && param1JComponent.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) ? 0 : (((JTextComponent)param1JComponent).isEditable() ? 3 : 1); }
    
    protected Transferable createTransferable(JComponent param1JComponent) {
      this.exportComp = (JTextComponent)param1JComponent;
      this.shouldRemove = true;
      this.p0 = this.exportComp.getSelectionStart();
      this.p1 = this.exportComp.getSelectionEnd();
      return (this.p0 != this.p1) ? new TextTransferable(this.exportComp, this.p0, this.p1) : null;
    }
    
    protected void exportDone(JComponent param1JComponent, Transferable param1Transferable, int param1Int) {
      if (this.shouldRemove && param1Int == 2) {
        TextTransferable textTransferable = (TextTransferable)param1Transferable;
        textTransferable.removeText();
      } 
      this.exportComp = null;
    }
    
    public boolean importData(TransferHandler.TransferSupport param1TransferSupport) {
      this.isDrop = param1TransferSupport.isDrop();
      if (this.isDrop) {
        this.modeBetween = (((JTextComponent)param1TransferSupport.getComponent()).getDropMode() == DropMode.INSERT);
        this.dropBias = ((JTextComponent.DropLocation)param1TransferSupport.getDropLocation()).getBias();
        this.dropAction = param1TransferSupport.getDropAction();
      } 
      try {
        return super.importData(param1TransferSupport);
      } finally {
        this.isDrop = false;
        this.modeBetween = false;
        this.dropBias = null;
        this.dropAction = 2;
      } 
    }
    
    public boolean importData(JComponent param1JComponent, Transferable param1Transferable) {
      JTextComponent jTextComponent = (JTextComponent)param1JComponent;
      int i = this.modeBetween ? jTextComponent.getDropLocation().getIndex() : jTextComponent.getCaretPosition();
      if (this.dropAction == 2 && jTextComponent == this.exportComp && i >= this.p0 && i <= this.p1) {
        this.shouldRemove = false;
        return true;
      } 
      boolean bool = false;
      DataFlavor dataFlavor = getImportFlavor(param1Transferable.getTransferDataFlavors(), jTextComponent);
      if (dataFlavor != null)
        try {
          boolean bool1 = false;
          if (param1JComponent instanceof JEditorPane) {
            JEditorPane jEditorPane = (JEditorPane)param1JComponent;
            if (!jEditorPane.getContentType().startsWith("text/plain") && dataFlavor.getMimeType().startsWith(jEditorPane.getContentType()))
              bool1 = true; 
          } 
          InputContext inputContext = jTextComponent.getInputContext();
          if (inputContext != null)
            inputContext.endComposition(); 
          Reader reader = dataFlavor.getReaderForText(param1Transferable);
          if (this.modeBetween) {
            Caret caret = jTextComponent.getCaret();
            if (caret instanceof DefaultCaret) {
              ((DefaultCaret)caret).setDot(i, this.dropBias);
            } else {
              jTextComponent.setCaretPosition(i);
            } 
          } 
          handleReaderImport(reader, jTextComponent, bool1);
          if (this.isDrop) {
            jTextComponent.requestFocus();
            Caret caret = jTextComponent.getCaret();
            if (caret instanceof DefaultCaret) {
              int j = caret.getDot();
              Position.Bias bias = ((DefaultCaret)caret).getDotBias();
              ((DefaultCaret)caret).setDot(i, this.dropBias);
              ((DefaultCaret)caret).moveDot(j, bias);
            } else {
              jTextComponent.select(i, jTextComponent.getCaretPosition());
            } 
          } 
          bool = true;
        } catch (UnsupportedFlavorException unsupportedFlavorException) {
        
        } catch (BadLocationException badLocationException) {
        
        } catch (IOException iOException) {} 
      return bool;
    }
    
    public boolean canImport(JComponent param1JComponent, DataFlavor[] param1ArrayOfDataFlavor) {
      JTextComponent jTextComponent = (JTextComponent)param1JComponent;
      return (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) ? false : ((getImportFlavor(param1ArrayOfDataFlavor, jTextComponent) != null));
    }
    
    static class TextTransferable extends BasicTransferable {
      Position p0;
      
      Position p1;
      
      String mimeType;
      
      String richText;
      
      JTextComponent c;
      
      TextTransferable(JTextComponent param2JTextComponent, int param2Int1, int param2Int2) {
        super(null, null);
        this.c = param2JTextComponent;
        Document document = param2JTextComponent.getDocument();
        try {
          this.p0 = document.createPosition(param2Int1);
          this.p1 = document.createPosition(param2Int2);
          this.plainData = param2JTextComponent.getSelectedText();
          if (param2JTextComponent instanceof JEditorPane) {
            JEditorPane jEditorPane = (JEditorPane)param2JTextComponent;
            this.mimeType = jEditorPane.getContentType();
            if (this.mimeType.startsWith("text/plain"))
              return; 
            StringWriter stringWriter = new StringWriter(this.p1.getOffset() - this.p0.getOffset());
            jEditorPane.getEditorKit().write(stringWriter, document, this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
            if (this.mimeType.startsWith("text/html")) {
              this.htmlData = stringWriter.toString();
            } else {
              this.richText = stringWriter.toString();
            } 
          } 
        } catch (BadLocationException badLocationException) {
        
        } catch (IOException iOException) {}
      }
      
      void removeText() {
        if (this.p0 != null && this.p1 != null && this.p0.getOffset() != this.p1.getOffset())
          try {
            Document document = this.c.getDocument();
            document.remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
          } catch (BadLocationException badLocationException) {} 
      }
      
      protected DataFlavor[] getRicherFlavors() {
        if (this.richText == null)
          return null; 
        try {
          DataFlavor[] arrayOfDataFlavor = new DataFlavor[3];
          arrayOfDataFlavor[0] = new DataFlavor(this.mimeType + ";class=java.lang.String");
          arrayOfDataFlavor[1] = new DataFlavor(this.mimeType + ";class=java.io.Reader");
          arrayOfDataFlavor[2] = new DataFlavor(this.mimeType + ";class=java.io.InputStream;charset=unicode");
          return arrayOfDataFlavor;
        } catch (ClassNotFoundException classNotFoundException) {
          return null;
        } 
      }
      
      protected Object getRicherData(DataFlavor param2DataFlavor) throws UnsupportedFlavorException {
        if (this.richText == null)
          return null; 
        if (String.class.equals(param2DataFlavor.getRepresentationClass()))
          return this.richText; 
        if (Reader.class.equals(param2DataFlavor.getRepresentationClass()))
          return new StringReader(this.richText); 
        if (java.io.InputStream.class.equals(param2DataFlavor.getRepresentationClass()))
          return new StringBufferInputStream(this.richText); 
        throw new UnsupportedFlavorException(param2DataFlavor);
      }
    }
  }
  
  class UpdateHandler implements PropertyChangeListener, DocumentListener, LayoutManager2, UIResource {
    private Hashtable<Component, Object> constraints;
    
    private boolean i18nView = false;
    
    public final void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      Object object1 = param1PropertyChangeEvent.getOldValue();
      Object object2 = param1PropertyChangeEvent.getNewValue();
      String str = param1PropertyChangeEvent.getPropertyName();
      if (object1 instanceof Document || object2 instanceof Document) {
        if (object1 != null) {
          ((Document)object1).removeDocumentListener(this);
          this.i18nView = false;
        } 
        if (object2 != null) {
          ((Document)object2).addDocumentListener(this);
          if ("document" == str) {
            BasicTextUI.this.setView(null);
            BasicTextUI.this.propertyChange(param1PropertyChangeEvent);
            BasicTextUI.this.modelChanged();
            return;
          } 
        } 
        BasicTextUI.this.modelChanged();
      } 
      if ("focusAccelerator" == str) {
        BasicTextUI.this.updateFocusAcceleratorBinding(true);
      } else if ("componentOrientation" == str) {
        BasicTextUI.this.modelChanged();
      } else if ("font" == str) {
        BasicTextUI.this.modelChanged();
      } else if ("dropLocation" == str) {
        dropIndexChanged();
      } else if ("editable" == str) {
        BasicTextUI.this.updateCursor();
        BasicTextUI.this.modelChanged();
      } 
      BasicTextUI.this.propertyChange(param1PropertyChangeEvent);
    }
    
    private void dropIndexChanged() {
      if (BasicTextUI.this.editor.getDropMode() == DropMode.USE_SELECTION)
        return; 
      JTextComponent.DropLocation dropLocation = BasicTextUI.this.editor.getDropLocation();
      if (dropLocation == null) {
        if (BasicTextUI.this.dropCaret != null) {
          BasicTextUI.this.dropCaret.deinstall(BasicTextUI.this.editor);
          BasicTextUI.this.editor.repaint(BasicTextUI.this.dropCaret);
          BasicTextUI.this.dropCaret = null;
        } 
      } else {
        if (BasicTextUI.this.dropCaret == null) {
          BasicTextUI.this.dropCaret = new BasicTextUI.BasicCaret();
          BasicTextUI.this.dropCaret.install(BasicTextUI.this.editor);
          BasicTextUI.this.dropCaret.setVisible(true);
        } 
        BasicTextUI.this.dropCaret.setDot(dropLocation.getIndex(), dropLocation.getBias());
      } 
    }
    
    public final void insertUpdate(DocumentEvent param1DocumentEvent) {
      Document document = param1DocumentEvent.getDocument();
      Object object = document.getProperty("i18n");
      if (object instanceof Boolean) {
        Boolean bool = (Boolean)object;
        if (bool.booleanValue() != this.i18nView) {
          this.i18nView = bool.booleanValue();
          BasicTextUI.this.modelChanged();
          return;
        } 
      } 
      Rectangle rectangle = BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null;
      BasicTextUI.this.rootView.insertUpdate(param1DocumentEvent, rectangle, BasicTextUI.this.rootView.getViewFactory());
    }
    
    public final void removeUpdate(DocumentEvent param1DocumentEvent) {
      Rectangle rectangle = BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null;
      BasicTextUI.this.rootView.removeUpdate(param1DocumentEvent, rectangle, BasicTextUI.this.rootView.getViewFactory());
    }
    
    public final void changedUpdate(DocumentEvent param1DocumentEvent) {
      Rectangle rectangle = BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null;
      BasicTextUI.this.rootView.changedUpdate(param1DocumentEvent, rectangle, BasicTextUI.this.rootView.getViewFactory());
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {
      if (this.constraints != null)
        this.constraints.remove(param1Component); 
    }
    
    public Dimension preferredLayoutSize(Container param1Container) { return null; }
    
    public Dimension minimumLayoutSize(Container param1Container) { return null; }
    
    public void layoutContainer(Container param1Container) {
      if (this.constraints != null && !this.constraints.isEmpty()) {
        Rectangle rectangle = BasicTextUI.this.getVisibleEditorRect();
        if (rectangle != null) {
          document = BasicTextUI.this.editor.getDocument();
          if (document instanceof AbstractDocument)
            ((AbstractDocument)document).readLock(); 
          try {
            BasicTextUI.this.rootView.setSize(rectangle.width, rectangle.height);
            Enumeration enumeration = this.constraints.keys();
            while (enumeration.hasMoreElements()) {
              Component component = (Component)enumeration.nextElement();
              View view = (View)this.constraints.get(component);
              Shape shape = calculateViewPosition(rectangle, view);
              if (shape != null) {
                Rectangle rectangle1 = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
                component.setBounds(rectangle1);
              } 
            } 
          } finally {
            if (document instanceof AbstractDocument)
              ((AbstractDocument)document).readUnlock(); 
          } 
        } 
      } 
    }
    
    Shape calculateViewPosition(Shape param1Shape, View param1View) {
      int i = param1View.getStartOffset();
      View view = null;
      BasicTextUI.RootView rootView = BasicTextUI.this.rootView;
      while (rootView != null && rootView != param1View) {
        int j = rootView.getViewIndex(i, Position.Bias.Forward);
        param1Shape = rootView.getChildAllocation(j, param1Shape);
        view = rootView.getView(j);
        View view1 = view;
      } 
      return (view != null) ? param1Shape : null;
    }
    
    public void addLayoutComponent(Component param1Component, Object param1Object) {
      if (param1Object instanceof View) {
        if (this.constraints == null)
          this.constraints = new Hashtable(7); 
        this.constraints.put(param1Component, param1Object);
      } 
    }
    
    public Dimension maximumLayoutSize(Container param1Container) { return null; }
    
    public float getLayoutAlignmentX(Container param1Container) { return 0.5F; }
    
    public float getLayoutAlignmentY(Container param1Container) { return 0.5F; }
    
    public void invalidateLayout(Container param1Container) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */