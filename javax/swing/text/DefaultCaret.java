package javax.swing.text;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TextUI;
import sun.swing.SwingUtilities2;

public class DefaultCaret extends Rectangle implements Caret, FocusListener, MouseListener, MouseMotionListener {
  public static final int UPDATE_WHEN_ON_EDT = 0;
  
  public static final int NEVER_UPDATE = 1;
  
  public static final int ALWAYS_UPDATE = 2;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  protected ChangeEvent changeEvent = null;
  
  JTextComponent component;
  
  int updatePolicy = 0;
  
  boolean visible;
  
  boolean active;
  
  int dot;
  
  int mark;
  
  Object selectionTag;
  
  boolean selectionVisible;
  
  Timer flasher;
  
  Point magicCaretPosition;
  
  Position.Bias dotBias;
  
  Position.Bias markBias;
  
  boolean dotLTR;
  
  boolean markLTR;
  
  Handler handler = new Handler();
  
  private int[] flagXPoints = new int[3];
  
  private int[] flagYPoints = new int[3];
  
  private NavigationFilter.FilterBypass filterBypass;
  
  private static Action selectWord = null;
  
  private static Action selectLine = null;
  
  private boolean ownsSelection;
  
  private boolean forceCaretPositionChange;
  
  private boolean shouldHandleRelease;
  
  private MouseEvent selectedWordEvent = null;
  
  private int caretWidth = -1;
  
  private float aspectRatio = -1.0F;
  
  public void setUpdatePolicy(int paramInt) { this.updatePolicy = paramInt; }
  
  public int getUpdatePolicy() { return this.updatePolicy; }
  
  protected final JTextComponent getComponent() { return this.component; }
  
  protected final void repaint() {
    if (this.component != null)
      this.component.repaint(this.x, this.y, this.width, this.height); 
  }
  
  protected void damage(Rectangle paramRectangle) {
    if (paramRectangle != null) {
      int i = getCaretWidth(paramRectangle.height);
      this.x = paramRectangle.x - 4 - (i >> 1);
      this.y = paramRectangle.y;
      this.width = 9 + i;
      this.height = paramRectangle.height;
      repaint();
    } 
  }
  
  protected void adjustVisibility(Rectangle paramRectangle) {
    if (this.component == null)
      return; 
    if (SwingUtilities.isEventDispatchThread()) {
      this.component.scrollRectToVisible(paramRectangle);
    } else {
      SwingUtilities.invokeLater(new SafeScroller(paramRectangle));
    } 
  }
  
  protected Highlighter.HighlightPainter getSelectionPainter() { return DefaultHighlighter.DefaultPainter; }
  
  protected void positionCaret(MouseEvent paramMouseEvent) {
    Point point = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = this.component.getUI().viewToModel(this.component, point, arrayOfBias);
    if (arrayOfBias[false] == null)
      arrayOfBias[0] = Position.Bias.Forward; 
    if (i >= 0)
      setDot(i, arrayOfBias[0]); 
  }
  
  protected void moveCaret(MouseEvent paramMouseEvent) {
    Point point = new Point(paramMouseEvent.getX(), paramMouseEvent.getY());
    Position.Bias[] arrayOfBias = new Position.Bias[1];
    int i = this.component.getUI().viewToModel(this.component, point, arrayOfBias);
    if (arrayOfBias[false] == null)
      arrayOfBias[0] = Position.Bias.Forward; 
    if (i >= 0)
      moveDot(i, arrayOfBias[0]); 
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {
    if (this.component.isEnabled()) {
      if (this.component.isEditable())
        setVisible(true); 
      setSelectionVisible(true);
    } 
  }
  
  public void focusLost(FocusEvent paramFocusEvent) {
    setVisible(false);
    setSelectionVisible((this.ownsSelection || paramFocusEvent.isTemporary()));
  }
  
  private void selectWord(MouseEvent paramMouseEvent) {
    if (this.selectedWordEvent != null && this.selectedWordEvent.getX() == paramMouseEvent.getX() && this.selectedWordEvent.getY() == paramMouseEvent.getY())
      return; 
    Action action = null;
    ActionMap actionMap = getComponent().getActionMap();
    if (actionMap != null)
      action = actionMap.get("select-word"); 
    if (action == null) {
      if (selectWord == null)
        selectWord = new DefaultEditorKit.SelectWordAction(); 
      action = selectWord;
    } 
    action.actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
    this.selectedWordEvent = paramMouseEvent;
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    if (getComponent() == null)
      return; 
    int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
    if (!paramMouseEvent.isConsumed())
      if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
        if (i == 1) {
          this.selectedWordEvent = null;
        } else if (i == 2 && SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)) {
          selectWord(paramMouseEvent);
          this.selectedWordEvent = null;
        } else if (i == 3 && SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)) {
          Action action = null;
          ActionMap actionMap = getComponent().getActionMap();
          if (actionMap != null)
            action = actionMap.get("select-line"); 
          if (action == null) {
            if (selectLine == null)
              selectLine = new DefaultEditorKit.SelectLineAction(); 
            action = selectLine;
          } 
          action.actionPerformed(new ActionEvent(getComponent(), 1001, null, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers()));
        } 
      } else if (SwingUtilities.isMiddleMouseButton(paramMouseEvent) && i == 1 && this.component.isEditable() && this.component.isEnabled() && SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent)) {
        JTextComponent jTextComponent = (JTextComponent)paramMouseEvent.getSource();
        if (jTextComponent != null)
          try {
            Toolkit toolkit = jTextComponent.getToolkit();
            Clipboard clipboard = toolkit.getSystemSelection();
            if (clipboard != null) {
              adjustCaret(paramMouseEvent);
              TransferHandler transferHandler = jTextComponent.getTransferHandler();
              if (transferHandler != null) {
                Transferable transferable = null;
                try {
                  transferable = clipboard.getContents(null);
                } catch (IllegalStateException illegalStateException) {
                  UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
                } 
                if (transferable != null)
                  transferHandler.importData(jTextComponent, transferable); 
              } 
              adjustFocus(true);
            } 
          } catch (HeadlessException headlessException) {} 
      }  
  }
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    int i = SwingUtilities2.getAdjustedClickCount(getComponent(), paramMouseEvent);
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent))
      if (paramMouseEvent.isConsumed()) {
        this.shouldHandleRelease = true;
      } else {
        this.shouldHandleRelease = false;
        adjustCaretAndFocus(paramMouseEvent);
        if (i == 2 && SwingUtilities2.canEventAccessSystemClipboard(paramMouseEvent))
          selectWord(paramMouseEvent); 
      }  
  }
  
  void adjustCaretAndFocus(MouseEvent paramMouseEvent) {
    adjustCaret(paramMouseEvent);
    adjustFocus(false);
  }
  
  private void adjustCaret(MouseEvent paramMouseEvent) {
    if ((paramMouseEvent.getModifiers() & true) != 0 && getDot() != -1) {
      moveCaret(paramMouseEvent);
    } else if (!paramMouseEvent.isPopupTrigger()) {
      positionCaret(paramMouseEvent);
    } 
  }
  
  private void adjustFocus(boolean paramBoolean) {
    if (this.component != null && this.component.isEnabled() && this.component.isRequestFocusEnabled())
      if (paramBoolean) {
        this.component.requestFocusInWindow();
      } else {
        this.component.requestFocus();
      }  
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    if (!paramMouseEvent.isConsumed() && this.shouldHandleRelease && SwingUtilities.isLeftMouseButton(paramMouseEvent))
      adjustCaretAndFocus(paramMouseEvent); 
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    if (!paramMouseEvent.isConsumed() && SwingUtilities.isLeftMouseButton(paramMouseEvent))
      moveCaret(paramMouseEvent); 
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void paint(Graphics paramGraphics) {
    if (isVisible())
      try {
        TextUI textUI = this.component.getUI();
        Rectangle rectangle = textUI.modelToView(this.component, this.dot, this.dotBias);
        if (rectangle == null || (rectangle.width == 0 && rectangle.height == 0))
          return; 
        if (this.width > 0 && this.height > 0 && !_contains(rectangle.x, rectangle.y, rectangle.width, rectangle.height)) {
          Rectangle rectangle1 = paramGraphics.getClipBounds();
          if (rectangle1 != null && !rectangle1.contains(this))
            repaint(); 
          damage(rectangle);
        } 
        paramGraphics.setColor(this.component.getCaretColor());
        int i = getCaretWidth(rectangle.height);
        rectangle.x -= (i >> 1);
        paramGraphics.fillRect(rectangle.x, rectangle.y, i, rectangle.height);
        Document document = this.component.getDocument();
        if (document instanceof AbstractDocument) {
          Element element = ((AbstractDocument)document).getBidiRootElement();
          if (element != null && element.getElementCount() > 1) {
            this.flagXPoints[0] = rectangle.x + (this.dotLTR ? i : 0);
            this.flagYPoints[0] = rectangle.y;
            this.flagXPoints[1] = this.flagXPoints[0];
            this.flagYPoints[1] = this.flagYPoints[0] + 4;
            this.flagXPoints[2] = this.flagXPoints[0] + (this.dotLTR ? 4 : -4);
            this.flagYPoints[2] = this.flagYPoints[0];
            paramGraphics.fillPolygon(this.flagXPoints, this.flagYPoints, 3);
          } 
        } 
      } catch (BadLocationException badLocationException) {} 
  }
  
  public void install(JTextComponent paramJTextComponent) {
    this.component = paramJTextComponent;
    Document document = paramJTextComponent.getDocument();
    this.dot = this.mark = 0;
    this.dotLTR = this.markLTR = true;
    this.dotBias = this.markBias = Position.Bias.Forward;
    if (document != null)
      document.addDocumentListener(this.handler); 
    paramJTextComponent.addPropertyChangeListener(this.handler);
    paramJTextComponent.addFocusListener(this);
    paramJTextComponent.addMouseListener(this);
    paramJTextComponent.addMouseMotionListener(this);
    if (this.component.hasFocus())
      focusGained(null); 
    Number number = (Number)paramJTextComponent.getClientProperty("caretAspectRatio");
    if (number != null) {
      this.aspectRatio = number.floatValue();
    } else {
      this.aspectRatio = -1.0F;
    } 
    Integer integer = (Integer)paramJTextComponent.getClientProperty("caretWidth");
    if (integer != null) {
      this.caretWidth = integer.intValue();
    } else {
      this.caretWidth = -1;
    } 
  }
  
  public void deinstall(JTextComponent paramJTextComponent) {
    paramJTextComponent.removeMouseListener(this);
    paramJTextComponent.removeMouseMotionListener(this);
    paramJTextComponent.removeFocusListener(this);
    paramJTextComponent.removePropertyChangeListener(this.handler);
    Document document = paramJTextComponent.getDocument();
    if (document != null)
      document.removeDocumentListener(this.handler); 
    synchronized (this) {
      this.component = null;
    } 
    if (this.flasher != null)
      this.flasher.stop(); 
  }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  public void setSelectionVisible(boolean paramBoolean) {
    if (paramBoolean != this.selectionVisible) {
      this.selectionVisible = paramBoolean;
      if (this.selectionVisible) {
        Highlighter highlighter = this.component.getHighlighter();
        if (this.dot != this.mark && highlighter != null && this.selectionTag == null) {
          int i = Math.min(this.dot, this.mark);
          int j = Math.max(this.dot, this.mark);
          Highlighter.HighlightPainter highlightPainter = getSelectionPainter();
          try {
            this.selectionTag = highlighter.addHighlight(i, j, highlightPainter);
          } catch (BadLocationException badLocationException) {
            this.selectionTag = null;
          } 
        } 
      } else if (this.selectionTag != null) {
        Highlighter highlighter = this.component.getHighlighter();
        highlighter.removeHighlight(this.selectionTag);
        this.selectionTag = null;
      } 
    } 
  }
  
  public boolean isSelectionVisible() { return this.selectionVisible; }
  
  public boolean isActive() { return this.active; }
  
  public boolean isVisible() { return this.visible; }
  
  public void setVisible(boolean paramBoolean) {
    this.active = paramBoolean;
    if (this.component != null) {
      TextUI textUI = this.component.getUI();
      if (this.visible != paramBoolean) {
        this.visible = paramBoolean;
        try {
          Rectangle rectangle = textUI.modelToView(this.component, this.dot, this.dotBias);
          damage(rectangle);
        } catch (BadLocationException badLocationException) {}
      } 
    } 
    if (this.flasher != null)
      if (this.visible) {
        this.flasher.start();
      } else {
        this.flasher.stop();
      }  
  }
  
  public void setBlinkRate(int paramInt) {
    if (paramInt != 0) {
      if (this.flasher == null)
        this.flasher = new Timer(paramInt, this.handler); 
      this.flasher.setDelay(paramInt);
    } else if (this.flasher != null) {
      this.flasher.stop();
      this.flasher.removeActionListener(this.handler);
      this.flasher = null;
    } 
  }
  
  public int getBlinkRate() { return (this.flasher == null) ? 0 : this.flasher.getDelay(); }
  
  public int getDot() { return this.dot; }
  
  public int getMark() { return this.mark; }
  
  public void setDot(int paramInt) { setDot(paramInt, Position.Bias.Forward); }
  
  public void moveDot(int paramInt) { moveDot(paramInt, Position.Bias.Forward); }
  
  public void moveDot(int paramInt, Position.Bias paramBias) {
    if (paramBias == null)
      throw new IllegalArgumentException("null bias"); 
    if (!this.component.isEnabled()) {
      setDot(paramInt, paramBias);
      return;
    } 
    if (paramInt != this.dot) {
      NavigationFilter navigationFilter = this.component.getNavigationFilter();
      if (navigationFilter != null) {
        navigationFilter.moveDot(getFilterBypass(), paramInt, paramBias);
      } else {
        handleMoveDot(paramInt, paramBias);
      } 
    } 
  }
  
  void handleMoveDot(int paramInt, Position.Bias paramBias) {
    changeCaretPosition(paramInt, paramBias);
    if (this.selectionVisible) {
      Highlighter highlighter = this.component.getHighlighter();
      if (highlighter != null) {
        int i = Math.min(paramInt, this.mark);
        int j = Math.max(paramInt, this.mark);
        if (i == j) {
          if (this.selectionTag != null) {
            highlighter.removeHighlight(this.selectionTag);
            this.selectionTag = null;
          } 
        } else {
          try {
            if (this.selectionTag != null) {
              highlighter.changeHighlight(this.selectionTag, i, j);
            } else {
              Highlighter.HighlightPainter highlightPainter = getSelectionPainter();
              this.selectionTag = highlighter.addHighlight(i, j, highlightPainter);
            } 
          } catch (BadLocationException badLocationException) {
            throw new StateInvariantError("Bad caret position");
          } 
        } 
      } 
    } 
  }
  
  public void setDot(int paramInt, Position.Bias paramBias) {
    if (paramBias == null)
      throw new IllegalArgumentException("null bias"); 
    NavigationFilter navigationFilter = this.component.getNavigationFilter();
    if (navigationFilter != null) {
      navigationFilter.setDot(getFilterBypass(), paramInt, paramBias);
    } else {
      handleSetDot(paramInt, paramBias);
    } 
  }
  
  void handleSetDot(int paramInt, Position.Bias paramBias) {
    Document document = this.component.getDocument();
    if (document != null)
      paramInt = Math.min(paramInt, document.getLength()); 
    paramInt = Math.max(paramInt, 0);
    if (paramInt == 0)
      paramBias = Position.Bias.Forward; 
    this.mark = paramInt;
    if (this.dot != paramInt || this.dotBias != paramBias || this.selectionTag != null || this.forceCaretPositionChange)
      changeCaretPosition(paramInt, paramBias); 
    this.markBias = this.dotBias;
    this.markLTR = this.dotLTR;
    Highlighter highlighter = this.component.getHighlighter();
    if (highlighter != null && this.selectionTag != null) {
      highlighter.removeHighlight(this.selectionTag);
      this.selectionTag = null;
    } 
  }
  
  public Position.Bias getDotBias() { return this.dotBias; }
  
  public Position.Bias getMarkBias() { return this.markBias; }
  
  boolean isDotLeftToRight() { return this.dotLTR; }
  
  boolean isMarkLeftToRight() { return this.markLTR; }
  
  boolean isPositionLTR(int paramInt, Position.Bias paramBias) {
    Document document = this.component.getDocument();
    if (paramBias == Position.Bias.Backward && --paramInt < 0)
      paramInt = 0; 
    return AbstractDocument.isLeftToRight(document, paramInt, paramInt);
  }
  
  Position.Bias guessBiasForOffset(int paramInt, Position.Bias paramBias, boolean paramBoolean) {
    if (paramBoolean != isPositionLTR(paramInt, paramBias)) {
      paramBias = Position.Bias.Backward;
    } else if (paramBias != Position.Bias.Backward && paramBoolean != isPositionLTR(paramInt, Position.Bias.Backward)) {
      paramBias = Position.Bias.Backward;
    } 
    if (paramBias == Position.Bias.Backward && paramInt > 0)
      try {
        Segment segment = new Segment();
        this.component.getDocument().getText(paramInt - 1, 1, segment);
        if (segment.count > 0 && segment.array[segment.offset] == '\n')
          paramBias = Position.Bias.Forward; 
      } catch (BadLocationException badLocationException) {} 
    return paramBias;
  }
  
  void changeCaretPosition(int paramInt, Position.Bias paramBias) {
    repaint();
    if (this.flasher != null && this.flasher.isRunning()) {
      this.visible = true;
      this.flasher.restart();
    } 
    this.dot = paramInt;
    this.dotBias = paramBias;
    this.dotLTR = isPositionLTR(paramInt, paramBias);
    fireStateChanged();
    updateSystemSelection();
    setMagicCaretPosition(null);
    Runnable runnable = new Runnable() {
        public void run() { DefaultCaret.this.repaintNewCaret(); }
      };
    SwingUtilities.invokeLater(runnable);
  }
  
  void repaintNewCaret() {
    if (this.component != null) {
      TextUI textUI = this.component.getUI();
      Document document = this.component.getDocument();
      if (textUI != null && document != null) {
        Rectangle rectangle;
        try {
          rectangle = textUI.modelToView(this.component, this.dot, this.dotBias);
        } catch (BadLocationException badLocationException) {
          rectangle = null;
        } 
        if (rectangle != null) {
          adjustVisibility(rectangle);
          if (getMagicCaretPosition() == null)
            setMagicCaretPosition(new Point(rectangle.x, rectangle.y)); 
        } 
        damage(rectangle);
      } 
    } 
  }
  
  private void updateSystemSelection() {
    if (!SwingUtilities2.canCurrentEventAccessSystemClipboard())
      return; 
    if (this.dot != this.mark && this.component != null && this.component.hasFocus()) {
      Clipboard clipboard = getSystemSelection();
      if (clipboard != null) {
        String str;
        if (this.component instanceof JPasswordField && this.component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
          StringBuilder stringBuilder = null;
          char c = ((JPasswordField)this.component).getEchoChar();
          int i = Math.min(getDot(), getMark());
          int j = Math.max(getDot(), getMark());
          for (int k = i; k < j; k++) {
            if (stringBuilder == null)
              stringBuilder = new StringBuilder(); 
            stringBuilder.append(c);
          } 
          str = (stringBuilder != null) ? stringBuilder.toString() : null;
        } else {
          str = this.component.getSelectedText();
        } 
        try {
          clipboard.setContents(new StringSelection(str), getClipboardOwner());
          this.ownsSelection = true;
        } catch (IllegalStateException illegalStateException) {}
      } 
    } 
  }
  
  private Clipboard getSystemSelection() {
    try {
      return this.component.getToolkit().getSystemSelection();
    } catch (HeadlessException headlessException) {
    
    } catch (SecurityException securityException) {}
    return null;
  }
  
  private ClipboardOwner getClipboardOwner() { return this.handler; }
  
  private void ensureValidPosition() {
    int i = this.component.getDocument().getLength();
    if (this.dot > i || this.mark > i)
      handleSetDot(i, Position.Bias.Forward); 
  }
  
  public void setMagicCaretPosition(Point paramPoint) { this.magicCaretPosition = paramPoint; }
  
  public Point getMagicCaretPosition() { return this.magicCaretPosition; }
  
  public boolean equals(Object paramObject) { return (this == paramObject); }
  
  public String toString() {
    null = "Dot=(" + this.dot + ", " + this.dotBias + ")";
    return null + " Mark=(" + this.mark + ", " + this.markBias + ")";
  }
  
  private NavigationFilter.FilterBypass getFilterBypass() {
    if (this.filterBypass == null)
      this.filterBypass = new DefaultFilterBypass(null); 
    return this.filterBypass;
  }
  
  private boolean _contains(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = this.width;
    int j = this.height;
    if ((i | j | paramInt3 | paramInt4) < 0)
      return false; 
    int k = this.x;
    int m = this.y;
    if (paramInt1 < k || paramInt2 < m)
      return false; 
    if (paramInt3 > 0) {
      i += k;
      paramInt3 += paramInt1;
      if (paramInt3 <= paramInt1) {
        if (i >= k || paramInt3 > i)
          return false; 
      } else if (i >= k && paramInt3 > i) {
        return false;
      } 
    } else if (k + i < paramInt1) {
      return false;
    } 
    if (paramInt4 > 0) {
      j += m;
      paramInt4 += paramInt2;
      if (paramInt4 <= paramInt2) {
        if (j >= m || paramInt4 > j)
          return false; 
      } else if (j >= m && paramInt4 > j) {
        return false;
      } 
    } else if (m + j < paramInt2) {
      return false;
    } 
    return true;
  }
  
  int getCaretWidth(int paramInt) {
    if (this.aspectRatio > -1.0F)
      return (int)(this.aspectRatio * paramInt) + 1; 
    if (this.caretWidth > -1)
      return this.caretWidth; 
    Object object = UIManager.get("Caret.width");
    return (object instanceof Integer) ? ((Integer)object).intValue() : 1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    this.handler = new Handler();
    if (!paramObjectInputStream.readBoolean()) {
      this.dotBias = Position.Bias.Forward;
    } else {
      this.dotBias = Position.Bias.Backward;
    } 
    if (!paramObjectInputStream.readBoolean()) {
      this.markBias = Position.Bias.Forward;
    } else {
      this.markBias = Position.Bias.Backward;
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeBoolean((this.dotBias == Position.Bias.Backward));
    paramObjectOutputStream.writeBoolean((this.markBias == Position.Bias.Backward));
  }
  
  private class DefaultFilterBypass extends NavigationFilter.FilterBypass {
    private DefaultFilterBypass() {}
    
    public Caret getCaret() { return DefaultCaret.this; }
    
    public void setDot(int param1Int, Position.Bias param1Bias) { DefaultCaret.this.handleSetDot(param1Int, param1Bias); }
    
    public void moveDot(int param1Int, Position.Bias param1Bias) { DefaultCaret.this.handleMoveDot(param1Int, param1Bias); }
  }
  
  class Handler implements PropertyChangeListener, DocumentListener, ActionListener, ClipboardOwner {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if ((DefaultCaret.this.width == 0 || DefaultCaret.this.height == 0) && DefaultCaret.this.component != null) {
        TextUI textUI = DefaultCaret.this.component.getUI();
        try {
          Rectangle rectangle = textUI.modelToView(DefaultCaret.this.component, DefaultCaret.this.dot, DefaultCaret.this.dotBias);
          if (rectangle != null && rectangle.width != 0 && rectangle.height != 0)
            DefaultCaret.this.damage(rectangle); 
        } catch (BadLocationException badLocationException) {}
      } 
      DefaultCaret.this.visible = !DefaultCaret.this.visible;
      DefaultCaret.this.repaint();
    }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent) {
      if (DefaultCaret.this.getUpdatePolicy() == 1 || (DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread())) {
        if ((param1DocumentEvent.getOffset() <= DefaultCaret.this.dot || param1DocumentEvent.getOffset() <= DefaultCaret.this.mark) && DefaultCaret.this.selectionTag != null)
          try {
            DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
          } catch (BadLocationException badLocationException) {
            badLocationException.printStackTrace();
          }  
        return;
      } 
      int i = param1DocumentEvent.getOffset();
      int j = param1DocumentEvent.getLength();
      int k = DefaultCaret.this.dot;
      short s = 0;
      if (param1DocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent) {
        DefaultCaret.this.setDot(i + j);
        return;
      } 
      if (k >= i) {
        k += j;
        s = (short)(s | true);
      } 
      int m = DefaultCaret.this.mark;
      if (m >= i) {
        m += j;
        s = (short)(s | 0x2);
      } 
      if (s != 0) {
        Position.Bias bias = DefaultCaret.this.dotBias;
        if (DefaultCaret.this.dot == i) {
          boolean bool;
          Document document = DefaultCaret.this.component.getDocument();
          try {
            Segment segment = new Segment();
            document.getText(k - 1, 1, segment);
            bool = (segment.count > 0 && segment.array[segment.offset] == '\n') ? 1 : 0;
          } catch (BadLocationException badLocationException) {
            bool = false;
          } 
          if (bool) {
            bias = Position.Bias.Forward;
          } else {
            bias = Position.Bias.Backward;
          } 
        } 
        if (m == k) {
          DefaultCaret.this.setDot(k, bias);
          DefaultCaret.this.ensureValidPosition();
        } else {
          DefaultCaret.this.setDot(m, DefaultCaret.this.markBias);
          if (DefaultCaret.this.getDot() == m)
            DefaultCaret.this.moveDot(k, bias); 
          DefaultCaret.this.ensureValidPosition();
        } 
      } 
    }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent) {
      if (DefaultCaret.this.getUpdatePolicy() == 1 || (DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread())) {
        int n = DefaultCaret.this.component.getDocument().getLength();
        DefaultCaret.this.dot = Math.min(DefaultCaret.this.dot, n);
        DefaultCaret.this.mark = Math.min(DefaultCaret.this.mark, n);
        if ((param1DocumentEvent.getOffset() < DefaultCaret.this.dot || param1DocumentEvent.getOffset() < DefaultCaret.this.mark) && DefaultCaret.this.selectionTag != null)
          try {
            DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
          } catch (BadLocationException badLocationException) {
            badLocationException.printStackTrace();
          }  
        return;
      } 
      int i = param1DocumentEvent.getOffset();
      int j = i + param1DocumentEvent.getLength();
      int k = DefaultCaret.this.dot;
      boolean bool1 = false;
      int m = DefaultCaret.this.mark;
      boolean bool2 = false;
      if (param1DocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent) {
        DefaultCaret.this.setDot(i);
        return;
      } 
      if (k >= j) {
        k -= j - i;
        if (k == j)
          bool1 = true; 
      } else if (k >= i) {
        k = i;
        bool1 = true;
      } 
      if (m >= j) {
        m -= j - i;
        if (m == j)
          bool2 = true; 
      } else if (m >= i) {
        m = i;
        bool2 = true;
      } 
      if (m == k) {
        DefaultCaret.this.forceCaretPositionChange = true;
        try {
          DefaultCaret.this.setDot(k, DefaultCaret.this.guessBiasForOffset(k, DefaultCaret.this.dotBias, DefaultCaret.this.dotLTR));
        } finally {
          DefaultCaret.this.forceCaretPositionChange = false;
        } 
        DefaultCaret.this.ensureValidPosition();
      } else {
        Position.Bias bias1 = DefaultCaret.this.dotBias;
        Position.Bias bias2 = DefaultCaret.this.markBias;
        if (bool1)
          bias1 = DefaultCaret.this.guessBiasForOffset(k, bias1, DefaultCaret.this.dotLTR); 
        if (bool2)
          bias2 = DefaultCaret.this.guessBiasForOffset(DefaultCaret.this.mark, bias2, DefaultCaret.this.markLTR); 
        DefaultCaret.this.setDot(m, bias2);
        if (DefaultCaret.this.getDot() == m)
          DefaultCaret.this.moveDot(k, bias1); 
        DefaultCaret.this.ensureValidPosition();
      } 
    }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent) {
      if (DefaultCaret.this.getUpdatePolicy() == 1 || (DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread()))
        return; 
      if (param1DocumentEvent instanceof AbstractDocument.UndoRedoDocumentEvent)
        DefaultCaret.this.setDot(param1DocumentEvent.getOffset() + param1DocumentEvent.getLength()); 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      Object object1 = param1PropertyChangeEvent.getOldValue();
      Object object2 = param1PropertyChangeEvent.getNewValue();
      if (object1 instanceof Document || object2 instanceof Document) {
        DefaultCaret.this.setDot(0);
        if (object1 != null)
          ((Document)object1).removeDocumentListener(this); 
        if (object2 != null)
          ((Document)object2).addDocumentListener(this); 
      } else if ("enabled".equals(param1PropertyChangeEvent.getPropertyName())) {
        Boolean bool = (Boolean)param1PropertyChangeEvent.getNewValue();
        if (DefaultCaret.this.component.isFocusOwner())
          if (bool == Boolean.TRUE) {
            if (DefaultCaret.this.component.isEditable())
              DefaultCaret.this.setVisible(true); 
            DefaultCaret.this.setSelectionVisible(true);
          } else {
            DefaultCaret.this.setVisible(false);
            DefaultCaret.this.setSelectionVisible(false);
          }  
      } else if ("caretWidth".equals(param1PropertyChangeEvent.getPropertyName())) {
        Integer integer = (Integer)param1PropertyChangeEvent.getNewValue();
        if (integer != null) {
          DefaultCaret.this.caretWidth = integer.intValue();
        } else {
          DefaultCaret.this.caretWidth = -1;
        } 
        DefaultCaret.this.repaint();
      } else if ("caretAspectRatio".equals(param1PropertyChangeEvent.getPropertyName())) {
        Number number = (Number)param1PropertyChangeEvent.getNewValue();
        if (number != null) {
          DefaultCaret.this.aspectRatio = number.floatValue();
        } else {
          DefaultCaret.this.aspectRatio = -1.0F;
        } 
        DefaultCaret.this.repaint();
      } 
    }
    
    public void lostOwnership(Clipboard param1Clipboard, Transferable param1Transferable) {
      if (DefaultCaret.this.ownsSelection) {
        DefaultCaret.this.ownsSelection = false;
        if (DefaultCaret.this.component != null && !DefaultCaret.this.component.hasFocus())
          DefaultCaret.this.setSelectionVisible(false); 
      } 
    }
  }
  
  class SafeScroller implements Runnable {
    Rectangle r;
    
    SafeScroller(Rectangle param1Rectangle) { this.r = param1Rectangle; }
    
    public void run() {
      if (DefaultCaret.this.component != null)
        DefaultCaret.this.component.scrollRectToVisible(this.r); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DefaultCaret.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */