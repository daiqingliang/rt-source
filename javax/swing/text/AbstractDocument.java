package javax.swing.text;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.Bidi;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import sun.font.BidiUtils;
import sun.swing.SwingUtilities2;

public abstract class AbstractDocument implements Document, Serializable {
  private int numReaders;
  
  private Thread currWriter;
  
  private int numWriters;
  
  private boolean notifyingListeners;
  
  private static Boolean defaultI18NProperty;
  
  private Dictionary<Object, Object> documentProperties = null;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  private Content data;
  
  private AttributeContext context;
  
  private BranchElement bidiRoot;
  
  private DocumentFilter documentFilter;
  
  private DocumentFilter.FilterBypass filterBypass;
  
  private static final String BAD_LOCK_STATE = "document lock failure";
  
  protected static final String BAD_LOCATION = "document location failure";
  
  public static final String ParagraphElementName = "paragraph";
  
  public static final String ContentElementName = "content";
  
  public static final String SectionElementName = "section";
  
  public static final String BidiElementName = "bidi level";
  
  public static final String ElementNameAttribute = "$ename";
  
  static final String I18NProperty = "i18n";
  
  static final Object MultiByteProperty = "multiByte";
  
  static final String AsyncLoadPriority = "load priority";
  
  protected AbstractDocument(Content paramContent) { this(paramContent, StyleContext.getDefaultStyleContext()); }
  
  protected AbstractDocument(Content paramContent, AttributeContext paramAttributeContext) {
    this.data = paramContent;
    this.context = paramAttributeContext;
    this.bidiRoot = new BidiRootElement();
    if (defaultI18NProperty == null) {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getProperty("i18n"); }
          });
      if (str != null) {
        defaultI18NProperty = Boolean.valueOf(str);
      } else {
        defaultI18NProperty = Boolean.FALSE;
      } 
    } 
    putProperty("i18n", defaultI18NProperty);
    writeLock();
    try {
      Element[] arrayOfElement = new Element[1];
      arrayOfElement[0] = new BidiElement(this.bidiRoot, 0, 1, 0);
      this.bidiRoot.replace(0, 0, arrayOfElement);
    } finally {
      writeUnlock();
    } 
  }
  
  public Dictionary<Object, Object> getDocumentProperties() {
    if (this.documentProperties == null)
      this.documentProperties = new Hashtable(2); 
    return this.documentProperties;
  }
  
  public void setDocumentProperties(Dictionary<Object, Object> paramDictionary) { this.documentProperties = paramDictionary; }
  
  protected void fireInsertUpdate(DocumentEvent paramDocumentEvent) {
    this.notifyingListeners = true;
    try {
      Object[] arrayOfObject = this.listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == DocumentListener.class)
          ((DocumentListener)arrayOfObject[i + 1]).insertUpdate(paramDocumentEvent); 
      } 
    } finally {
      this.notifyingListeners = false;
    } 
  }
  
  protected void fireChangedUpdate(DocumentEvent paramDocumentEvent) {
    this.notifyingListeners = true;
    try {
      Object[] arrayOfObject = this.listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == DocumentListener.class)
          ((DocumentListener)arrayOfObject[i + 1]).changedUpdate(paramDocumentEvent); 
      } 
    } finally {
      this.notifyingListeners = false;
    } 
  }
  
  protected void fireRemoveUpdate(DocumentEvent paramDocumentEvent) {
    this.notifyingListeners = true;
    try {
      Object[] arrayOfObject = this.listenerList.getListenerList();
      for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
        if (arrayOfObject[i] == DocumentListener.class)
          ((DocumentListener)arrayOfObject[i + 1]).removeUpdate(paramDocumentEvent); 
      } 
    } finally {
      this.notifyingListeners = false;
    } 
  }
  
  protected void fireUndoableEditUpdate(UndoableEditEvent paramUndoableEditEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == UndoableEditListener.class)
        ((UndoableEditListener)arrayOfObject[i + 1]).undoableEditHappened(paramUndoableEditEvent); 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  public int getAsynchronousLoadPriority() {
    Integer integer = (Integer)getProperty("load priority");
    return (integer != null) ? integer.intValue() : -1;
  }
  
  public void setAsynchronousLoadPriority(int paramInt) {
    Integer integer = (paramInt >= 0) ? Integer.valueOf(paramInt) : null;
    putProperty("load priority", integer);
  }
  
  public void setDocumentFilter(DocumentFilter paramDocumentFilter) { this.documentFilter = paramDocumentFilter; }
  
  public DocumentFilter getDocumentFilter() { return this.documentFilter; }
  
  public void render(Runnable paramRunnable) {
    readLock();
    try {
      paramRunnable.run();
    } finally {
      readUnlock();
    } 
  }
  
  public int getLength() { return this.data.length() - 1; }
  
  public void addDocumentListener(DocumentListener paramDocumentListener) { this.listenerList.add(DocumentListener.class, paramDocumentListener); }
  
  public void removeDocumentListener(DocumentListener paramDocumentListener) { this.listenerList.remove(DocumentListener.class, paramDocumentListener); }
  
  public DocumentListener[] getDocumentListeners() { return (DocumentListener[])this.listenerList.getListeners(DocumentListener.class); }
  
  public void addUndoableEditListener(UndoableEditListener paramUndoableEditListener) { this.listenerList.add(UndoableEditListener.class, paramUndoableEditListener); }
  
  public void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener) { this.listenerList.remove(UndoableEditListener.class, paramUndoableEditListener); }
  
  public UndoableEditListener[] getUndoableEditListeners() { return (UndoableEditListener[])this.listenerList.getListeners(UndoableEditListener.class); }
  
  public final Object getProperty(Object paramObject) { return getDocumentProperties().get(paramObject); }
  
  public final void putProperty(Object paramObject1, Object paramObject2) {
    if (paramObject2 != null) {
      getDocumentProperties().put(paramObject1, paramObject2);
    } else {
      getDocumentProperties().remove(paramObject1);
    } 
    if (paramObject1 == TextAttribute.RUN_DIRECTION && Boolean.TRUE.equals(getProperty("i18n"))) {
      writeLock();
      try {
        DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(0, getLength(), DocumentEvent.EventType.INSERT);
        updateBidi(defaultDocumentEvent);
      } finally {
        writeUnlock();
      } 
    } 
  }
  
  public void remove(int paramInt1, int paramInt2) throws BadLocationException {
    DocumentFilter documentFilter1 = getDocumentFilter();
    writeLock();
    try {
      if (documentFilter1 != null) {
        documentFilter1.remove(getFilterBypass(), paramInt1, paramInt2);
      } else {
        handleRemove(paramInt1, paramInt2);
      } 
    } finally {
      writeUnlock();
    } 
  }
  
  void handleRemove(int paramInt1, int paramInt2) throws BadLocationException {
    if (paramInt2 > 0) {
      if (paramInt1 < 0 || paramInt1 + paramInt2 > getLength())
        throw new BadLocationException("Invalid remove", getLength() + 1); 
      DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(paramInt1, paramInt2, DocumentEvent.EventType.REMOVE);
      boolean bool = Utilities.isComposedTextElement(this, paramInt1);
      removeUpdate(defaultDocumentEvent);
      UndoableEdit undoableEdit = this.data.remove(paramInt1, paramInt2);
      if (undoableEdit != null)
        defaultDocumentEvent.addEdit(undoableEdit); 
      postRemoveUpdate(defaultDocumentEvent);
      defaultDocumentEvent.end();
      fireRemoveUpdate(defaultDocumentEvent);
      if (undoableEdit != null && !bool)
        fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent)); 
    } 
  }
  
  public void replace(int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet) throws BadLocationException {
    if (paramInt2 == 0 && (paramString == null || paramString.length() == 0))
      return; 
    DocumentFilter documentFilter1 = getDocumentFilter();
    writeLock();
    try {
      if (documentFilter1 != null) {
        documentFilter1.replace(getFilterBypass(), paramInt1, paramInt2, paramString, paramAttributeSet);
      } else {
        if (paramInt2 > 0)
          remove(paramInt1, paramInt2); 
        if (paramString != null && paramString.length() > 0)
          insertString(paramInt1, paramString, paramAttributeSet); 
      } 
    } finally {
      writeUnlock();
    } 
  }
  
  public void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet) throws BadLocationException {
    if (paramString == null || paramString.length() == 0)
      return; 
    DocumentFilter documentFilter1 = getDocumentFilter();
    writeLock();
    try {
      if (documentFilter1 != null) {
        documentFilter1.insertString(getFilterBypass(), paramInt, paramString, paramAttributeSet);
      } else {
        handleInsertString(paramInt, paramString, paramAttributeSet);
      } 
    } finally {
      writeUnlock();
    } 
  }
  
  private void handleInsertString(int paramInt, String paramString, AttributeSet paramAttributeSet) throws BadLocationException {
    if (paramString == null || paramString.length() == 0)
      return; 
    UndoableEdit undoableEdit = this.data.insertString(paramInt, paramString);
    DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(paramInt, paramString.length(), DocumentEvent.EventType.INSERT);
    if (undoableEdit != null)
      defaultDocumentEvent.addEdit(undoableEdit); 
    if (getProperty("i18n").equals(Boolean.FALSE)) {
      Object object = getProperty(TextAttribute.RUN_DIRECTION);
      if (object != null && object.equals(TextAttribute.RUN_DIRECTION_RTL)) {
        putProperty("i18n", Boolean.TRUE);
      } else {
        char[] arrayOfChar = paramString.toCharArray();
        if (SwingUtilities2.isComplexLayout(arrayOfChar, 0, arrayOfChar.length))
          putProperty("i18n", Boolean.TRUE); 
      } 
    } 
    insertUpdate(defaultDocumentEvent, paramAttributeSet);
    defaultDocumentEvent.end();
    fireInsertUpdate(defaultDocumentEvent);
    if (undoableEdit != null && (paramAttributeSet == null || !paramAttributeSet.isDefined(StyleConstants.ComposedTextAttribute)))
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent)); 
  }
  
  public String getText(int paramInt1, int paramInt2) throws BadLocationException {
    if (paramInt2 < 0)
      throw new BadLocationException("Length must be positive", paramInt2); 
    return this.data.getString(paramInt1, paramInt2);
  }
  
  public void getText(int paramInt1, int paramInt2, Segment paramSegment) throws BadLocationException {
    if (paramInt2 < 0)
      throw new BadLocationException("Length must be positive", paramInt2); 
    this.data.getChars(paramInt1, paramInt2, paramSegment);
  }
  
  public Position createPosition(int paramInt) throws BadLocationException { return this.data.createPosition(paramInt); }
  
  public final Position getStartPosition() {
    Position position;
    try {
      position = createPosition(0);
    } catch (BadLocationException badLocationException) {
      position = null;
    } 
    return position;
  }
  
  public final Position getEndPosition() {
    Position position;
    try {
      position = createPosition(this.data.length());
    } catch (BadLocationException badLocationException) {
      position = null;
    } 
    return position;
  }
  
  public Element[] getRootElements() {
    Element[] arrayOfElement = new Element[2];
    arrayOfElement[0] = getDefaultRootElement();
    arrayOfElement[1] = getBidiRootElement();
    return arrayOfElement;
  }
  
  public abstract Element getDefaultRootElement();
  
  private DocumentFilter.FilterBypass getFilterBypass() {
    if (this.filterBypass == null)
      this.filterBypass = new DefaultFilterBypass(null); 
    return this.filterBypass;
  }
  
  public Element getBidiRootElement() { return this.bidiRoot; }
  
  static boolean isLeftToRight(Document paramDocument, int paramInt1, int paramInt2) {
    if (Boolean.TRUE.equals(paramDocument.getProperty("i18n")) && paramDocument instanceof AbstractDocument) {
      AbstractDocument abstractDocument = (AbstractDocument)paramDocument;
      Element element1 = abstractDocument.getBidiRootElement();
      int i = element1.getElementIndex(paramInt1);
      Element element2 = element1.getElement(i);
      if (element2.getEndOffset() >= paramInt2) {
        AttributeSet attributeSet = element2.getAttributes();
        return (StyleConstants.getBidiLevel(attributeSet) % 2 == 0);
      } 
    } 
    return true;
  }
  
  public abstract Element getParagraphElement(int paramInt);
  
  protected final AttributeContext getAttributeContext() { return this.context; }
  
  protected void insertUpdate(DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet) {
    if (getProperty("i18n").equals(Boolean.TRUE))
      updateBidi(paramDefaultDocumentEvent); 
    if (paramDefaultDocumentEvent.type == DocumentEvent.EventType.INSERT && paramDefaultDocumentEvent.getLength() > 0 && !Boolean.TRUE.equals(getProperty(MultiByteProperty))) {
      Segment segment = SegmentCache.getSharedSegment();
      try {
        getText(paramDefaultDocumentEvent.getOffset(), paramDefaultDocumentEvent.getLength(), segment);
        segment.first();
        do {
          if (segment.current() > 'ÿ') {
            putProperty(MultiByteProperty, Boolean.TRUE);
            break;
          } 
        } while (segment.next() != Character.MAX_VALUE);
      } catch (BadLocationException badLocationException) {}
      SegmentCache.releaseSharedSegment(segment);
    } 
  }
  
  protected void removeUpdate(DefaultDocumentEvent paramDefaultDocumentEvent) {}
  
  protected void postRemoveUpdate(DefaultDocumentEvent paramDefaultDocumentEvent) {
    if (getProperty("i18n").equals(Boolean.TRUE))
      updateBidi(paramDefaultDocumentEvent); 
  }
  
  void updateBidi(DefaultDocumentEvent paramDefaultDocumentEvent) {
    int j;
    int i;
    if (paramDefaultDocumentEvent.type == DocumentEvent.EventType.INSERT || paramDefaultDocumentEvent.type == DocumentEvent.EventType.CHANGE) {
      int i6 = paramDefaultDocumentEvent.getOffset();
      int i7 = i6 + paramDefaultDocumentEvent.getLength();
      i = getParagraphElement(i6).getStartOffset();
      j = getParagraphElement(i7).getEndOffset();
    } else if (paramDefaultDocumentEvent.type == DocumentEvent.EventType.REMOVE) {
      Element element = getParagraphElement(paramDefaultDocumentEvent.getOffset());
      i = element.getStartOffset();
      j = element.getEndOffset();
    } else {
      throw new Error("Internal error: unknown event type.");
    } 
    byte[] arrayOfByte = calculateBidiLevels(i, j);
    Vector vector = new Vector();
    int k = i;
    int m = 0;
    if (k > 0) {
      int i6 = this.bidiRoot.getElementIndex(i - 1);
      m = i6;
      Element element = this.bidiRoot.getElement(i6);
      int i7 = StyleConstants.getBidiLevel(element.getAttributes());
      if (i7 == arrayOfByte[0]) {
        k = element.getStartOffset();
      } else if (element.getEndOffset() > i) {
        vector.addElement(new BidiElement(this.bidiRoot, element.getStartOffset(), i, i7));
      } else {
        m++;
      } 
    } 
    int n;
    for (n = 0; n < arrayOfByte.length && arrayOfByte[n] == arrayOfByte[0]; n++);
    int i1 = j;
    BidiElement bidiElement = null;
    int i2 = this.bidiRoot.getElementCount() - 1;
    if (i1 <= getLength()) {
      int i6 = this.bidiRoot.getElementIndex(j);
      i2 = i6;
      Element element = this.bidiRoot.getElement(i6);
      int i7 = StyleConstants.getBidiLevel(element.getAttributes());
      if (i7 == arrayOfByte[arrayOfByte.length - 1]) {
        i1 = element.getEndOffset();
      } else if (element.getStartOffset() < j) {
        bidiElement = new BidiElement(this.bidiRoot, j, element.getEndOffset(), i7);
      } else {
        i2--;
      } 
    } 
    int i3;
    for (i3 = arrayOfByte.length; i3 > n && arrayOfByte[i3 - 1] == arrayOfByte[arrayOfByte.length - 1]; i3--);
    if (n == i3 && arrayOfByte[0] == arrayOfByte[arrayOfByte.length - 1]) {
      vector.addElement(new BidiElement(this.bidiRoot, k, i1, arrayOfByte[0]));
    } else {
      vector.addElement(new BidiElement(this.bidiRoot, k, n + i, arrayOfByte[0]));
      int i6;
      for (i6 = n; i6 < i3; i6 = i7) {
        int i7;
        for (i7 = i6; i7 < arrayOfByte.length && arrayOfByte[i7] == arrayOfByte[i6]; i7++);
        vector.addElement(new BidiElement(this.bidiRoot, i + i6, i + i7, arrayOfByte[i6]));
      } 
      vector.addElement(new BidiElement(this.bidiRoot, i3 + i, i1, arrayOfByte[arrayOfByte.length - 1]));
    } 
    if (bidiElement != null)
      vector.addElement(bidiElement); 
    int i4 = 0;
    if (this.bidiRoot.getElementCount() > 0)
      i4 = i2 - m + 1; 
    Element[] arrayOfElement1 = new Element[i4];
    for (int i5 = 0; i5 < i4; i5++)
      arrayOfElement1[i5] = this.bidiRoot.getElement(m + i5); 
    Element[] arrayOfElement2 = new Element[vector.size()];
    vector.copyInto(arrayOfElement2);
    ElementEdit elementEdit = new ElementEdit(this.bidiRoot, m, arrayOfElement1, arrayOfElement2);
    paramDefaultDocumentEvent.addEdit(elementEdit);
    this.bidiRoot.replace(m, arrayOfElement1.length, arrayOfElement2);
  }
  
  private byte[] calculateBidiLevels(int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[paramInt2 - paramInt1];
    int i = 0;
    Boolean bool = null;
    Object object = getProperty(TextAttribute.RUN_DIRECTION);
    if (object instanceof Boolean)
      bool = (Boolean)object; 
    int j = paramInt1;
    while (j < paramInt2) {
      Element element = getParagraphElement(j);
      int k = element.getStartOffset();
      int m = element.getEndOffset();
      Boolean bool1 = bool;
      object = element.getAttributes().getAttribute(TextAttribute.RUN_DIRECTION);
      if (object instanceof Boolean)
        bool1 = (Boolean)object; 
      Segment segment = SegmentCache.getSharedSegment();
      try {
        getText(k, m - k, segment);
      } catch (BadLocationException badLocationException) {
        throw new Error("Internal error: " + badLocationException.toString());
      } 
      byte b = -2;
      if (bool1 != null)
        if (TextAttribute.RUN_DIRECTION_LTR.equals(bool1)) {
          b = 0;
        } else {
          b = 1;
        }  
      Bidi bidi = new Bidi(segment.array, segment.offset, null, 0, segment.count, b);
      BidiUtils.getLevels(bidi, arrayOfByte, i);
      i += bidi.getLength();
      j = element.getEndOffset();
      SegmentCache.releaseSharedSegment(segment);
    } 
    if (i != arrayOfByte.length)
      throw new Error("levelsEnd assertion failed."); 
    return arrayOfByte;
  }
  
  public void dump(PrintStream paramPrintStream) {
    Element element = getDefaultRootElement();
    if (element instanceof AbstractElement)
      ((AbstractElement)element).dump(paramPrintStream, 0); 
    this.bidiRoot.dump(paramPrintStream, 0);
  }
  
  protected final Content getContent() { return this.data; }
  
  protected Element createLeafElement(Element paramElement, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) { return new LeafElement(paramElement, paramAttributeSet, paramInt1, paramInt2); }
  
  protected Element createBranchElement(Element paramElement, AttributeSet paramAttributeSet) { return new BranchElement(paramElement, paramAttributeSet); }
  
  protected final Thread getCurrentWriter() { return this.currWriter; }
  
  protected final void writeLock() {
    try {
      while (this.numReaders > 0 || this.currWriter != null) {
        if (Thread.currentThread() == this.currWriter) {
          if (this.notifyingListeners)
            throw new IllegalStateException("Attempt to mutate in notification"); 
          this.numWriters++;
          return;
        } 
        wait();
      } 
      this.currWriter = Thread.currentThread();
      this.numWriters = 1;
    } catch (InterruptedException interruptedException) {
      throw new Error("Interrupted attempt to acquire write lock");
    } 
  }
  
  protected final void writeUnlock() {
    if (--this.numWriters <= 0) {
      this.numWriters = 0;
      this.currWriter = null;
      notifyAll();
    } 
  }
  
  public final void readLock() {
    try {
      while (this.currWriter != null) {
        if (this.currWriter == Thread.currentThread())
          return; 
        wait();
      } 
      this.numReaders++;
    } catch (InterruptedException interruptedException) {
      throw new Error("Interrupted attempt to acquire read lock");
    } 
  }
  
  public final void readUnlock() {
    if (this.currWriter == Thread.currentThread())
      return; 
    if (this.numReaders <= 0)
      throw new StateInvariantError("document lock failure"); 
    this.numReaders--;
    notify();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    this.listenerList = new EventListenerList();
    this.bidiRoot = new BidiRootElement();
    try {
      writeLock();
      Element[] arrayOfElement = new Element[1];
      arrayOfElement[0] = new BidiElement(this.bidiRoot, 0, 1, 0);
      this.bidiRoot.replace(0, 0, arrayOfElement);
    } finally {
      writeUnlock();
    } 
    paramObjectInputStream.registerValidation(new ObjectInputValidation() {
          public void validateObject() {
            try {
              AbstractDocument.this.writeLock();
              AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(AbstractDocument.this, 0, AbstractDocument.this.getLength(), DocumentEvent.EventType.INSERT);
              AbstractDocument.this.updateBidi(defaultDocumentEvent);
            } finally {
              AbstractDocument.this.writeUnlock();
            } 
          }
        }0);
  }
  
  public abstract class AbstractElement implements Element, MutableAttributeSet, Serializable, TreeNode {
    private Element parent;
    
    private AttributeSet attributes;
    
    public AbstractElement(Element param1Element, AttributeSet param1AttributeSet) {
      this.parent = param1Element;
      this.attributes = this$0.getAttributeContext().getEmptySet();
      if (param1AttributeSet != null)
        addAttributes(param1AttributeSet); 
    }
    
    private final void indent(PrintWriter param1PrintWriter, int param1Int) {
      for (byte b = 0; b < param1Int; b++)
        param1PrintWriter.print("  "); 
    }
    
    public void dump(PrintStream param1PrintStream, int param1Int) {
      PrintWriter printWriter;
      try {
        printWriter = new PrintWriter(new OutputStreamWriter(param1PrintStream, "JavaEsc"), true);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        printWriter = new PrintWriter(param1PrintStream, true);
      } 
      indent(printWriter, param1Int);
      if (getName() == null) {
        printWriter.print("<??");
      } else {
        printWriter.print("<" + getName());
      } 
      if (getAttributeCount() > 0) {
        printWriter.println("");
        Enumeration enumeration = this.attributes.getAttributeNames();
        while (enumeration.hasMoreElements()) {
          Object object = enumeration.nextElement();
          indent(printWriter, param1Int + 1);
          printWriter.println(object + "=" + getAttribute(object));
        } 
        indent(printWriter, param1Int);
      } 
      printWriter.println(">");
      if (isLeaf()) {
        indent(printWriter, param1Int + 1);
        printWriter.print("[" + getStartOffset() + "," + getEndOffset() + "]");
        AbstractDocument.Content content = AbstractDocument.this.getContent();
        try {
          String str = content.getString(getStartOffset(), getEndOffset() - getStartOffset());
          if (str.length() > 40)
            str = str.substring(0, 40) + "..."; 
          printWriter.println("[" + str + "]");
        } catch (BadLocationException badLocationException) {}
      } else {
        int i = getElementCount();
        for (byte b = 0; b < i; b++) {
          AbstractElement abstractElement = (AbstractElement)getElement(b);
          abstractElement.dump(param1PrintStream, param1Int + 1);
        } 
      } 
    }
    
    public int getAttributeCount() { return this.attributes.getAttributeCount(); }
    
    public boolean isDefined(Object param1Object) { return this.attributes.isDefined(param1Object); }
    
    public boolean isEqual(AttributeSet param1AttributeSet) { return this.attributes.isEqual(param1AttributeSet); }
    
    public AttributeSet copyAttributes() { return this.attributes.copyAttributes(); }
    
    public Object getAttribute(Object param1Object) {
      Object object = this.attributes.getAttribute(param1Object);
      if (object == null) {
        AttributeSet attributeSet = (this.parent != null) ? this.parent.getAttributes() : null;
        if (attributeSet != null)
          object = attributeSet.getAttribute(param1Object); 
      } 
      return object;
    }
    
    public Enumeration<?> getAttributeNames() { return this.attributes.getAttributeNames(); }
    
    public boolean containsAttribute(Object param1Object1, Object param1Object2) { return this.attributes.containsAttribute(param1Object1, param1Object2); }
    
    public boolean containsAttributes(AttributeSet param1AttributeSet) { return this.attributes.containsAttributes(param1AttributeSet); }
    
    public AttributeSet getResolveParent() {
      AttributeSet attributeSet = this.attributes.getResolveParent();
      if (attributeSet == null && this.parent != null)
        attributeSet = this.parent.getAttributes(); 
      return attributeSet;
    }
    
    public void addAttribute(Object param1Object1, Object param1Object2) {
      checkForIllegalCast();
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      this.attributes = attributeContext.addAttribute(this.attributes, param1Object1, param1Object2);
    }
    
    public void addAttributes(AttributeSet param1AttributeSet) {
      checkForIllegalCast();
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      this.attributes = attributeContext.addAttributes(this.attributes, param1AttributeSet);
    }
    
    public void removeAttribute(Object param1Object) {
      checkForIllegalCast();
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      this.attributes = attributeContext.removeAttribute(this.attributes, param1Object);
    }
    
    public void removeAttributes(Enumeration<?> param1Enumeration) {
      checkForIllegalCast();
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      this.attributes = attributeContext.removeAttributes(this.attributes, param1Enumeration);
    }
    
    public void removeAttributes(AttributeSet param1AttributeSet) {
      checkForIllegalCast();
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      if (param1AttributeSet == this) {
        this.attributes = attributeContext.getEmptySet();
      } else {
        this.attributes = attributeContext.removeAttributes(this.attributes, param1AttributeSet);
      } 
    }
    
    public void setResolveParent(AttributeSet param1AttributeSet) {
      checkForIllegalCast();
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      if (param1AttributeSet != null) {
        this.attributes = attributeContext.addAttribute(this.attributes, StyleConstants.ResolveAttribute, param1AttributeSet);
      } else {
        this.attributes = attributeContext.removeAttribute(this.attributes, StyleConstants.ResolveAttribute);
      } 
    }
    
    private final void checkForIllegalCast() {
      Thread thread;
      if (thread == null || thread != (thread = AbstractDocument.this.getCurrentWriter()).currentThread())
        throw new StateInvariantError("Illegal cast to MutableAttributeSet"); 
    }
    
    public Document getDocument() { return AbstractDocument.this; }
    
    public Element getParentElement() { return this.parent; }
    
    public AttributeSet getAttributes() { return this; }
    
    public String getName() { return this.attributes.isDefined("$ename") ? (String)this.attributes.getAttribute("$ename") : null; }
    
    public abstract int getStartOffset();
    
    public abstract int getEndOffset();
    
    public abstract Element getElement(int param1Int);
    
    public abstract int getElementCount();
    
    public abstract int getElementIndex(int param1Int);
    
    public abstract boolean isLeaf();
    
    public TreeNode getChildAt(int param1Int) { return (TreeNode)getElement(param1Int); }
    
    public int getChildCount() { return getElementCount(); }
    
    public TreeNode getParent() { return (TreeNode)getParentElement(); }
    
    public int getIndex(TreeNode param1TreeNode) {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        if (getChildAt(i) == param1TreeNode)
          return i; 
      } 
      return -1;
    }
    
    public abstract boolean getAllowsChildren();
    
    public abstract Enumeration children();
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      param1ObjectOutputStream.defaultWriteObject();
      StyleContext.writeAttributeSet(param1ObjectOutputStream, this.attributes);
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException {
      param1ObjectInputStream.defaultReadObject();
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
      StyleContext.readAttributeSet(param1ObjectInputStream, simpleAttributeSet);
      AbstractDocument.AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
      this.attributes = attributeContext.addAttributes(SimpleAttributeSet.EMPTY, simpleAttributeSet);
    }
  }
  
  public static interface AttributeContext {
    AttributeSet addAttribute(AttributeSet param1AttributeSet, Object param1Object1, Object param1Object2);
    
    AttributeSet addAttributes(AttributeSet param1AttributeSet1, AttributeSet param1AttributeSet2);
    
    AttributeSet removeAttribute(AttributeSet param1AttributeSet, Object param1Object);
    
    AttributeSet removeAttributes(AttributeSet param1AttributeSet, Enumeration<?> param1Enumeration);
    
    AttributeSet removeAttributes(AttributeSet param1AttributeSet1, AttributeSet param1AttributeSet2);
    
    AttributeSet getEmptySet();
    
    void reclaim(AttributeSet param1AttributeSet);
  }
  
  class BidiElement extends LeafElement {
    BidiElement(Element param1Element, int param1Int1, int param1Int2, int param1Int3) {
      super(AbstractDocument.this, param1Element, new SimpleAttributeSet(), param1Int1, param1Int2);
      addAttribute(StyleConstants.BidiLevel, Integer.valueOf(param1Int3));
    }
    
    public String getName() { return "bidi level"; }
    
    int getLevel() {
      Integer integer = (Integer)getAttribute(StyleConstants.BidiLevel);
      return (integer != null) ? integer.intValue() : 0;
    }
    
    boolean isLeftToRight() { return (getLevel() % 2 == 0); }
  }
  
  class BidiRootElement extends BranchElement {
    BidiRootElement() { super(AbstractDocument.this, null, null); }
    
    public String getName() { return "bidi root"; }
  }
  
  public class BranchElement extends AbstractElement {
    private AbstractDocument.AbstractElement[] children = new AbstractDocument.AbstractElement[1];
    
    private int nchildren = 0;
    
    private int lastIndex = -1;
    
    public BranchElement(Element param1Element, AttributeSet param1AttributeSet) { super(AbstractDocument.this, param1Element, param1AttributeSet); }
    
    public Element positionToElement(int param1Int) {
      int i = getElementIndex(param1Int);
      AbstractDocument.AbstractElement abstractElement = this.children[i];
      int j = abstractElement.getStartOffset();
      int k = abstractElement.getEndOffset();
      return (param1Int >= j && param1Int < k) ? abstractElement : null;
    }
    
    public void replace(int param1Int1, int param1Int2, Element[] param1ArrayOfElement) {
      int i = param1ArrayOfElement.length - param1Int2;
      int j = param1Int1 + param1Int2;
      int k = this.nchildren - j;
      int m = j + i;
      if (this.nchildren + i >= this.children.length) {
        int n = Math.max(2 * this.children.length, this.nchildren + i);
        AbstractDocument.AbstractElement[] arrayOfAbstractElement = new AbstractDocument.AbstractElement[n];
        System.arraycopy(this.children, 0, arrayOfAbstractElement, 0, param1Int1);
        System.arraycopy(param1ArrayOfElement, 0, arrayOfAbstractElement, param1Int1, param1ArrayOfElement.length);
        System.arraycopy(this.children, j, arrayOfAbstractElement, m, k);
        this.children = arrayOfAbstractElement;
      } else {
        System.arraycopy(this.children, j, this.children, m, k);
        System.arraycopy(param1ArrayOfElement, 0, this.children, param1Int1, param1ArrayOfElement.length);
      } 
      this.nchildren += i;
    }
    
    public String toString() { return "BranchElement(" + getName() + ") " + getStartOffset() + "," + getEndOffset() + "\n"; }
    
    public String getName() {
      String str = super.getName();
      if (str == null)
        str = "paragraph"; 
      return str;
    }
    
    public int getStartOffset() { return this.children[0].getStartOffset(); }
    
    public int getEndOffset() {
      AbstractDocument.AbstractElement abstractElement = (this.nchildren > 0) ? this.children[this.nchildren - 1] : this.children[0];
      return abstractElement.getEndOffset();
    }
    
    public Element getElement(int param1Int) { return (param1Int < this.nchildren) ? this.children[param1Int] : null; }
    
    public int getElementCount() { return this.nchildren; }
    
    public int getElementIndex(int param1Int) {
      int i;
      int j = 0;
      int k = this.nchildren - 1;
      int m = 0;
      int n = getStartOffset();
      if (this.nchildren == 0)
        return 0; 
      if (param1Int >= getEndOffset())
        return this.nchildren - 1; 
      if (this.lastIndex >= j && this.lastIndex <= k) {
        AbstractDocument.AbstractElement abstractElement = this.children[this.lastIndex];
        n = abstractElement.getStartOffset();
        int i1 = abstractElement.getEndOffset();
        if (param1Int >= n && param1Int < i1)
          return this.lastIndex; 
        if (param1Int < n) {
          k = this.lastIndex;
        } else {
          j = this.lastIndex;
        } 
      } 
      while (j <= k) {
        m = j + (k - j) / 2;
        AbstractDocument.AbstractElement abstractElement = this.children[m];
        n = abstractElement.getStartOffset();
        int i1 = abstractElement.getEndOffset();
        if (param1Int >= n && param1Int < i1) {
          i = m;
          this.lastIndex = i;
          return i;
        } 
        if (param1Int < n) {
          k = m - 1;
          continue;
        } 
        j = m + 1;
      } 
      if (param1Int < n) {
        i = m;
      } else {
        i = m + 1;
      } 
      this.lastIndex = i;
      return i;
    }
    
    public boolean isLeaf() { return false; }
    
    public boolean getAllowsChildren() { return true; }
    
    public Enumeration children() {
      if (this.nchildren == 0)
        return null; 
      Vector vector = new Vector(this.nchildren);
      for (byte b = 0; b < this.nchildren; b++)
        vector.addElement(this.children[b]); 
      return vector.elements();
    }
  }
  
  public static interface Content {
    Position createPosition(int param1Int) throws BadLocationException;
    
    int length();
    
    UndoableEdit insertString(int param1Int, String param1String) throws BadLocationException;
    
    UndoableEdit remove(int param1Int1, int param1Int2) throws BadLocationException;
    
    String getString(int param1Int1, int param1Int2) throws BadLocationException;
    
    void getChars(int param1Int1, int param1Int2, Segment param1Segment) throws BadLocationException;
  }
  
  public class DefaultDocumentEvent extends CompoundEdit implements DocumentEvent {
    private int offset;
    
    private int length;
    
    private Hashtable<Element, DocumentEvent.ElementChange> changeLookup;
    
    private DocumentEvent.EventType type;
    
    public DefaultDocumentEvent(int param1Int1, int param1Int2, DocumentEvent.EventType param1EventType) {
      this.offset = param1Int1;
      this.length = param1Int2;
      this.type = param1EventType;
    }
    
    public String toString() { return this.edits.toString(); }
    
    public boolean addEdit(UndoableEdit param1UndoableEdit) {
      if (this.changeLookup == null && this.edits.size() > 10) {
        this.changeLookup = new Hashtable();
        int i = this.edits.size();
        for (byte b = 0; b < i; b++) {
          Object object = this.edits.elementAt(b);
          if (object instanceof DocumentEvent.ElementChange) {
            DocumentEvent.ElementChange elementChange = (DocumentEvent.ElementChange)object;
            this.changeLookup.put(elementChange.getElement(), elementChange);
          } 
        } 
      } 
      if (this.changeLookup != null && param1UndoableEdit instanceof DocumentEvent.ElementChange) {
        DocumentEvent.ElementChange elementChange = (DocumentEvent.ElementChange)param1UndoableEdit;
        this.changeLookup.put(elementChange.getElement(), elementChange);
      } 
      return super.addEdit(param1UndoableEdit);
    }
    
    public void redo() {
      AbstractDocument.this.writeLock();
      try {
        super.redo();
        AbstractDocument.UndoRedoDocumentEvent undoRedoDocumentEvent = new AbstractDocument.UndoRedoDocumentEvent(AbstractDocument.this, this, false);
        if (this.type == DocumentEvent.EventType.INSERT) {
          AbstractDocument.this.fireInsertUpdate(undoRedoDocumentEvent);
        } else if (this.type == DocumentEvent.EventType.REMOVE) {
          AbstractDocument.this.fireRemoveUpdate(undoRedoDocumentEvent);
        } else {
          AbstractDocument.this.fireChangedUpdate(undoRedoDocumentEvent);
        } 
      } finally {
        AbstractDocument.this.writeUnlock();
      } 
    }
    
    public void undo() {
      AbstractDocument.this.writeLock();
      try {
        super.undo();
        AbstractDocument.UndoRedoDocumentEvent undoRedoDocumentEvent = new AbstractDocument.UndoRedoDocumentEvent(AbstractDocument.this, this, true);
        if (this.type == DocumentEvent.EventType.REMOVE) {
          AbstractDocument.this.fireInsertUpdate(undoRedoDocumentEvent);
        } else if (this.type == DocumentEvent.EventType.INSERT) {
          AbstractDocument.this.fireRemoveUpdate(undoRedoDocumentEvent);
        } else {
          AbstractDocument.this.fireChangedUpdate(undoRedoDocumentEvent);
        } 
      } finally {
        AbstractDocument.this.writeUnlock();
      } 
    }
    
    public boolean isSignificant() { return true; }
    
    public String getPresentationName() {
      DocumentEvent.EventType eventType = getType();
      return (eventType == DocumentEvent.EventType.INSERT) ? UIManager.getString("AbstractDocument.additionText") : ((eventType == DocumentEvent.EventType.REMOVE) ? UIManager.getString("AbstractDocument.deletionText") : UIManager.getString("AbstractDocument.styleChangeText"));
    }
    
    public String getUndoPresentationName() { return UIManager.getString("AbstractDocument.undoText") + " " + getPresentationName(); }
    
    public String getRedoPresentationName() { return UIManager.getString("AbstractDocument.redoText") + " " + getPresentationName(); }
    
    public DocumentEvent.EventType getType() { return this.type; }
    
    public int getOffset() { return this.offset; }
    
    public int getLength() { return this.length; }
    
    public Document getDocument() { return AbstractDocument.this; }
    
    public DocumentEvent.ElementChange getChange(Element param1Element) {
      if (this.changeLookup != null)
        return (DocumentEvent.ElementChange)this.changeLookup.get(param1Element); 
      int i = this.edits.size();
      for (byte b = 0; b < i; b++) {
        Object object = this.edits.elementAt(b);
        if (object instanceof DocumentEvent.ElementChange) {
          DocumentEvent.ElementChange elementChange = (DocumentEvent.ElementChange)object;
          if (param1Element.equals(elementChange.getElement()))
            return elementChange; 
        } 
      } 
      return null;
    }
  }
  
  private class DefaultFilterBypass extends DocumentFilter.FilterBypass {
    private DefaultFilterBypass() {}
    
    public Document getDocument() { return AbstractDocument.this; }
    
    public void remove(int param1Int1, int param1Int2) throws BadLocationException { AbstractDocument.this.handleRemove(param1Int1, param1Int2); }
    
    public void insertString(int param1Int, String param1String, AttributeSet param1AttributeSet) throws BadLocationException { AbstractDocument.this.handleInsertString(param1Int, param1String, param1AttributeSet); }
    
    public void replace(int param1Int1, int param1Int2, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
      AbstractDocument.this.handleRemove(param1Int1, param1Int2);
      AbstractDocument.this.handleInsertString(param1Int1, param1String, param1AttributeSet);
    }
  }
  
  public static class ElementEdit extends AbstractUndoableEdit implements DocumentEvent.ElementChange {
    private Element e;
    
    private int index;
    
    private Element[] removed;
    
    private Element[] added;
    
    public ElementEdit(Element param1Element, int param1Int, Element[] param1ArrayOfElement1, Element[] param1ArrayOfElement2) {
      this.e = param1Element;
      this.index = param1Int;
      this.removed = param1ArrayOfElement1;
      this.added = param1ArrayOfElement2;
    }
    
    public Element getElement() { return this.e; }
    
    public int getIndex() { return this.index; }
    
    public Element[] getChildrenRemoved() { return this.removed; }
    
    public Element[] getChildrenAdded() { return this.added; }
    
    public void redo() {
      super.redo();
      Element[] arrayOfElement = this.removed;
      this.removed = this.added;
      this.added = arrayOfElement;
      ((AbstractDocument.BranchElement)this.e).replace(this.index, this.removed.length, this.added);
    }
    
    public void undo() {
      super.undo();
      ((AbstractDocument.BranchElement)this.e).replace(this.index, this.added.length, this.removed);
      Element[] arrayOfElement = this.removed;
      this.removed = this.added;
      this.added = arrayOfElement;
    }
  }
  
  public class LeafElement extends AbstractElement {
    private Position p0;
    
    private Position p1;
    
    public LeafElement(Element param1Element, AttributeSet param1AttributeSet, int param1Int1, int param1Int2) {
      super(AbstractDocument.this, param1Element, param1AttributeSet);
      try {
        this.p0 = this$0.createPosition(param1Int1);
        this.p1 = this$0.createPosition(param1Int2);
      } catch (BadLocationException badLocationException) {
        this.p0 = null;
        this.p1 = null;
        throw new StateInvariantError("Can't create Position references");
      } 
    }
    
    public String toString() { return "LeafElement(" + getName() + ") " + this.p0 + "," + this.p1 + "\n"; }
    
    public int getStartOffset() { return this.p0.getOffset(); }
    
    public int getEndOffset() { return this.p1.getOffset(); }
    
    public String getName() {
      String str = super.getName();
      if (str == null)
        str = "content"; 
      return str;
    }
    
    public int getElementIndex(int param1Int) { return -1; }
    
    public Element getElement(int param1Int) { return null; }
    
    public int getElementCount() { return 0; }
    
    public boolean isLeaf() { return true; }
    
    public boolean getAllowsChildren() { return false; }
    
    public Enumeration children() { return null; }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      param1ObjectOutputStream.defaultWriteObject();
      param1ObjectOutputStream.writeInt(this.p0.getOffset());
      param1ObjectOutputStream.writeInt(this.p1.getOffset());
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException {
      param1ObjectInputStream.defaultReadObject();
      int i = param1ObjectInputStream.readInt();
      int j = param1ObjectInputStream.readInt();
      try {
        this.p0 = AbstractDocument.this.createPosition(i);
        this.p1 = AbstractDocument.this.createPosition(j);
      } catch (BadLocationException badLocationException) {
        this.p0 = null;
        this.p1 = null;
        throw new IOException("Can't restore Position references");
      } 
    }
  }
  
  class UndoRedoDocumentEvent implements DocumentEvent {
    private AbstractDocument.DefaultDocumentEvent src = null;
    
    private DocumentEvent.EventType type = null;
    
    public UndoRedoDocumentEvent(AbstractDocument.DefaultDocumentEvent param1DefaultDocumentEvent, boolean param1Boolean) {
      this.src = param1DefaultDocumentEvent;
      if (param1Boolean) {
        if (param1DefaultDocumentEvent.getType().equals(DocumentEvent.EventType.INSERT)) {
          this.type = DocumentEvent.EventType.REMOVE;
        } else if (param1DefaultDocumentEvent.getType().equals(DocumentEvent.EventType.REMOVE)) {
          this.type = DocumentEvent.EventType.INSERT;
        } else {
          this.type = param1DefaultDocumentEvent.getType();
        } 
      } else {
        this.type = param1DefaultDocumentEvent.getType();
      } 
    }
    
    public AbstractDocument.DefaultDocumentEvent getSource() { return this.src; }
    
    public int getOffset() { return this.src.getOffset(); }
    
    public int getLength() { return this.src.getLength(); }
    
    public Document getDocument() { return this.src.getDocument(); }
    
    public DocumentEvent.EventType getType() { return this.type; }
    
    public DocumentEvent.ElementChange getChange(Element param1Element) { return this.src.getChange(param1Element); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\AbstractDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */