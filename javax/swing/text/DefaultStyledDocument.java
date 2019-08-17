package javax.swing.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

public class DefaultStyledDocument extends AbstractDocument implements StyledDocument {
  public static final int BUFFER_SIZE_DEFAULT = 4096;
  
  protected ElementBuffer buffer = new ElementBuffer(createDefaultRoot());
  
  private Vector<Style> listeningStyles = new Vector();
  
  private ChangeListener styleChangeListener;
  
  private ChangeListener styleContextChangeListener;
  
  private ChangeUpdateRunnable updateRunnable;
  
  public DefaultStyledDocument(AbstractDocument.Content paramContent, StyleContext paramStyleContext) {
    super(paramContent, paramStyleContext);
    Style style = paramStyleContext.getStyle("default");
    setLogicalStyle(0, style);
  }
  
  public DefaultStyledDocument(StyleContext paramStyleContext) { this(new GapContent(4096), paramStyleContext); }
  
  public DefaultStyledDocument() { this(new GapContent(4096), new StyleContext()); }
  
  public Element getDefaultRootElement() { return this.buffer.getRootElement(); }
  
  protected void create(ElementSpec[] paramArrayOfElementSpec) {
    try {
      if (getLength() != 0)
        remove(0, getLength()); 
      writeLock();
      AbstractDocument.Content content = getContent();
      int i = paramArrayOfElementSpec.length;
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < i; b++) {
        ElementSpec elementSpec = paramArrayOfElementSpec[b];
        if (elementSpec.getLength() > 0)
          stringBuilder.append(elementSpec.getArray(), elementSpec.getOffset(), elementSpec.getLength()); 
      } 
      UndoableEdit undoableEdit = content.insertString(0, stringBuilder.toString());
      int j = stringBuilder.length();
      AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, 0, j, DocumentEvent.EventType.INSERT);
      defaultDocumentEvent.addEdit(undoableEdit);
      this.buffer.create(j, paramArrayOfElementSpec, defaultDocumentEvent);
      super.insertUpdate(defaultDocumentEvent, null);
      defaultDocumentEvent.end();
      fireInsertUpdate(defaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
    } catch (BadLocationException badLocationException) {
      throw new StateInvariantError("problem initializing");
    } finally {
      writeUnlock();
    } 
  }
  
  protected void insert(int paramInt, ElementSpec[] paramArrayOfElementSpec) throws BadLocationException {
    if (paramArrayOfElementSpec == null || paramArrayOfElementSpec.length == 0)
      return; 
    try {
      writeLock();
      AbstractDocument.Content content = getContent();
      int i = paramArrayOfElementSpec.length;
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < i; b++) {
        ElementSpec elementSpec = paramArrayOfElementSpec[b];
        if (elementSpec.getLength() > 0)
          stringBuilder.append(elementSpec.getArray(), elementSpec.getOffset(), elementSpec.getLength()); 
      } 
      if (stringBuilder.length() == 0)
        return; 
      UndoableEdit undoableEdit = content.insertString(paramInt, stringBuilder.toString());
      int j = stringBuilder.length();
      AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt, j, DocumentEvent.EventType.INSERT);
      defaultDocumentEvent.addEdit(undoableEdit);
      this.buffer.insert(paramInt, j, paramArrayOfElementSpec, defaultDocumentEvent);
      super.insertUpdate(defaultDocumentEvent, null);
      defaultDocumentEvent.end();
      fireInsertUpdate(defaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
    } finally {
      writeUnlock();
    } 
  }
  
  public void removeElement(Element paramElement) {
    try {
      writeLock();
      removeElementImpl(paramElement);
    } finally {
      writeUnlock();
    } 
  }
  
  private void removeElementImpl(Element paramElement) {
    if (paramElement.getDocument() != this)
      throw new IllegalArgumentException("element doesn't belong to document"); 
    AbstractDocument.BranchElement branchElement = (AbstractDocument.BranchElement)paramElement.getParentElement();
    if (branchElement == null)
      throw new IllegalArgumentException("can't remove the root element"); 
    int i = paramElement.getStartOffset();
    int j = i;
    int k = paramElement.getEndOffset();
    int m = k;
    int n = getLength() + 1;
    AbstractDocument.Content content = getContent();
    boolean bool = false;
    boolean bool1 = Utilities.isComposedTextElement(paramElement);
    if (k >= n) {
      if (i <= 0)
        throw new IllegalArgumentException("can't remove the whole content"); 
      m = n - 1;
      try {
        if (content.getString(i - 1, 1).charAt(0) == '\n')
          j--; 
      } catch (BadLocationException badLocationException) {
        throw new IllegalStateException(badLocationException);
      } 
      bool = true;
    } 
    int i1 = m - j;
    AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, j, i1, DocumentEvent.EventType.REMOVE);
    UndoableEdit undoableEdit = null;
    while (branchElement.getElementCount() == 1) {
      paramElement = branchElement;
      branchElement = (AbstractDocument.BranchElement)branchElement.getParentElement();
      if (branchElement == null)
        throw new IllegalStateException("invalid element structure"); 
    } 
    Element[] arrayOfElement1 = { paramElement };
    Element[] arrayOfElement2 = new Element[0];
    int i2 = branchElement.getElementIndex(i);
    branchElement.replace(i2, 1, arrayOfElement2);
    defaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(branchElement, i2, arrayOfElement1, arrayOfElement2));
    if (i1 > 0) {
      try {
        undoableEdit = content.remove(j, i1);
        if (undoableEdit != null)
          defaultDocumentEvent.addEdit(undoableEdit); 
      } catch (BadLocationException badLocationException) {
        throw new IllegalStateException(badLocationException);
      } 
      n -= i1;
    } 
    if (bool) {
      Element element1;
      for (element1 = branchElement.getElement(branchElement.getElementCount() - 1); element1 != null && !element1.isLeaf(); element1 = element1.getElement(element1.getElementCount() - 1));
      if (element1 == null)
        throw new IllegalStateException("invalid element structure"); 
      int i3 = element1.getStartOffset();
      AbstractDocument.BranchElement branchElement1 = (AbstractDocument.BranchElement)element1.getParentElement();
      int i4 = branchElement1.getElementIndex(i3);
      Element element2 = createLeafElement(branchElement1, element1.getAttributes(), i3, n);
      Element[] arrayOfElement3 = { element1 };
      Element[] arrayOfElement4 = { element2 };
      branchElement1.replace(i4, 1, arrayOfElement4);
      defaultDocumentEvent.addEdit(new AbstractDocument.ElementEdit(branchElement1, i4, arrayOfElement3, arrayOfElement4));
    } 
    postRemoveUpdate(defaultDocumentEvent);
    defaultDocumentEvent.end();
    fireRemoveUpdate(defaultDocumentEvent);
    if (!bool1 || undoableEdit == null)
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent)); 
  }
  
  public Style addStyle(String paramString, Style paramStyle) {
    StyleContext styleContext = (StyleContext)getAttributeContext();
    return styleContext.addStyle(paramString, paramStyle);
  }
  
  public void removeStyle(String paramString) {
    StyleContext styleContext = (StyleContext)getAttributeContext();
    styleContext.removeStyle(paramString);
  }
  
  public Style getStyle(String paramString) {
    StyleContext styleContext = (StyleContext)getAttributeContext();
    return styleContext.getStyle(paramString);
  }
  
  public Enumeration<?> getStyleNames() { return ((StyleContext)getAttributeContext()).getStyleNames(); }
  
  public void setLogicalStyle(int paramInt, Style paramStyle) {
    Element element = getParagraphElement(paramInt);
    if (element != null && element instanceof AbstractDocument.AbstractElement)
      try {
        writeLock();
        StyleChangeUndoableEdit styleChangeUndoableEdit = new StyleChangeUndoableEdit((AbstractDocument.AbstractElement)element, paramStyle);
        ((AbstractDocument.AbstractElement)element).setResolveParent(paramStyle);
        int i = element.getStartOffset();
        int j = element.getEndOffset();
        AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, i, j - i, DocumentEvent.EventType.CHANGE);
        defaultDocumentEvent.addEdit(styleChangeUndoableEdit);
        defaultDocumentEvent.end();
        fireChangedUpdate(defaultDocumentEvent);
        fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
      } finally {
        writeUnlock();
      }  
  }
  
  public Style getLogicalStyle(int paramInt) {
    Style style = null;
    Element element = getParagraphElement(paramInt);
    if (element != null) {
      AttributeSet attributeSet1 = element.getAttributes();
      AttributeSet attributeSet2 = attributeSet1.getResolveParent();
      if (attributeSet2 instanceof Style)
        style = (Style)attributeSet2; 
    } 
    return style;
  }
  
  public void setCharacterAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean) {
    if (paramInt2 == 0)
      return; 
    try {
      writeLock();
      AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt1, paramInt2, DocumentEvent.EventType.CHANGE);
      this.buffer.change(paramInt1, paramInt2, defaultDocumentEvent);
      AttributeSet attributeSet = paramAttributeSet.copyAttributes();
      int i;
      for (i = paramInt1; i < paramInt1 + paramInt2; i = j) {
        Element element = getCharacterElement(i);
        int j = element.getEndOffset();
        if (i == j)
          break; 
        MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)element.getAttributes();
        defaultDocumentEvent.addEdit(new AttributeUndoableEdit(element, attributeSet, paramBoolean));
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
  
  public void setParagraphAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean) {
    try {
      writeLock();
      AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(this, paramInt1, paramInt2, DocumentEvent.EventType.CHANGE);
      AttributeSet attributeSet = paramAttributeSet.copyAttributes();
      Element element = getDefaultRootElement();
      int i = element.getElementIndex(paramInt1);
      int j = element.getElementIndex(paramInt1 + ((paramInt2 > 0) ? (paramInt2 - 1) : 0));
      boolean bool = Boolean.TRUE.equals(getProperty("i18n"));
      boolean bool1 = false;
      for (int k = i; k <= j; k++) {
        Element element1 = element.getElement(k);
        MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)element1.getAttributes();
        defaultDocumentEvent.addEdit(new AttributeUndoableEdit(element1, attributeSet, paramBoolean));
        if (paramBoolean)
          mutableAttributeSet.removeAttributes(mutableAttributeSet); 
        mutableAttributeSet.addAttributes(paramAttributeSet);
        if (bool && !bool1)
          bool1 = (mutableAttributeSet.getAttribute(TextAttribute.RUN_DIRECTION) != null) ? 1 : 0; 
      } 
      if (bool1)
        updateBidi(defaultDocumentEvent); 
      defaultDocumentEvent.end();
      fireChangedUpdate(defaultDocumentEvent);
      fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
    } finally {
      writeUnlock();
    } 
  }
  
  public Element getParagraphElement(int paramInt) {
    Element element;
    for (element = getDefaultRootElement(); !element.isLeaf(); element = element.getElement(i))
      int i = element.getElementIndex(paramInt); 
    return (element != null) ? element.getParentElement() : element;
  }
  
  public Element getCharacterElement(int paramInt) {
    Element element;
    for (element = getDefaultRootElement(); !element.isLeaf(); element = element.getElement(i))
      int i = element.getElementIndex(paramInt); 
    return element;
  }
  
  protected void insertUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent, AttributeSet paramAttributeSet) {
    int i = paramDefaultDocumentEvent.getOffset();
    int j = paramDefaultDocumentEvent.getLength();
    if (paramAttributeSet == null)
      paramAttributeSet = SimpleAttributeSet.EMPTY; 
    Element element1 = getParagraphElement(i + j);
    AttributeSet attributeSet1 = element1.getAttributes();
    Element element2 = getParagraphElement(i);
    Element element3 = element2.getElement(element2.getElementIndex(i));
    int k = i + j;
    boolean bool = (element3.getEndOffset() == k) ? 1 : 0;
    AttributeSet attributeSet2 = element3.getAttributes();
    try {
      Segment segment = new Segment();
      Vector vector = new Vector();
      ElementSpec elementSpec1 = null;
      boolean bool1 = false;
      short s = 6;
      if (i > 0) {
        getText(i - 1, 1, segment);
        if (segment.array[segment.offset] == '\n') {
          bool1 = true;
          s = createSpecsForInsertAfterNewline(element1, element2, attributeSet1, vector, i, k);
          for (int i3 = vector.size() - 1; i3 >= 0; i3--) {
            ElementSpec elementSpec = (ElementSpec)vector.elementAt(i3);
            if (elementSpec.getType() == 1) {
              elementSpec1 = elementSpec;
              break;
            } 
          } 
        } 
      } 
      if (!bool1)
        attributeSet1 = element2.getAttributes(); 
      getText(i, j, segment);
      char[] arrayOfChar = segment.array;
      int m = segment.offset + segment.count;
      int n = segment.offset;
      for (int i1 = segment.offset; i1 < m; i1++) {
        if (arrayOfChar[i1] == '\n') {
          int i3 = i1 + 1;
          vector.addElement(new ElementSpec(paramAttributeSet, (short)3, i3 - n));
          vector.addElement(new ElementSpec(null, (short)2));
          elementSpec1 = new ElementSpec(attributeSet1, (short)1);
          vector.addElement(elementSpec1);
          n = i3;
        } 
      } 
      if (n < m)
        vector.addElement(new ElementSpec(paramAttributeSet, (short)3, m - n)); 
      ElementSpec elementSpec2 = (ElementSpec)vector.firstElement();
      int i2 = getLength();
      if (elementSpec2.getType() == 3 && attributeSet2.isEqual(paramAttributeSet))
        elementSpec2.setDirection((short)4); 
      if (elementSpec1 != null)
        if (bool1) {
          elementSpec1.setDirection(s);
        } else if (element2.getEndOffset() != k) {
          elementSpec1.setDirection((short)7);
        } else {
          Element element = element2.getParentElement();
          int i3 = element.getElementIndex(i);
          if (i3 + 1 < element.getElementCount() && !element.getElement(i3 + 1).isLeaf())
            elementSpec1.setDirection((short)5); 
        }  
      if (bool && k < i2) {
        ElementSpec elementSpec = (ElementSpec)vector.lastElement();
        if (elementSpec.getType() == 3 && elementSpec.getDirection() != 4 && ((elementSpec1 == null && (element1 == element2 || bool1)) || (elementSpec1 != null && elementSpec1.getDirection() != 6))) {
          Element element = element1.getElement(element1.getElementIndex(k));
          if (element.isLeaf() && paramAttributeSet.isEqual(element.getAttributes()))
            elementSpec.setDirection((short)5); 
        } 
      } else if (!bool && elementSpec1 != null && elementSpec1.getDirection() == 7) {
        ElementSpec elementSpec = (ElementSpec)vector.lastElement();
        if (elementSpec.getType() == 3 && elementSpec.getDirection() != 4 && paramAttributeSet.isEqual(attributeSet2))
          elementSpec.setDirection((short)5); 
      } 
      if (Utilities.isComposedTextAttributeDefined(paramAttributeSet)) {
        MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)paramAttributeSet;
        mutableAttributeSet.addAttributes(attributeSet2);
        mutableAttributeSet.addAttribute("$ename", "content");
        mutableAttributeSet.addAttribute(StyleConstants.NameAttribute, "content");
        if (mutableAttributeSet.isDefined("CR"))
          mutableAttributeSet.removeAttribute("CR"); 
      } 
      ElementSpec[] arrayOfElementSpec = new ElementSpec[vector.size()];
      vector.copyInto(arrayOfElementSpec);
      this.buffer.insert(i, j, arrayOfElementSpec, paramDefaultDocumentEvent);
    } catch (BadLocationException badLocationException) {}
    super.insertUpdate(paramDefaultDocumentEvent, paramAttributeSet);
  }
  
  short createSpecsForInsertAfterNewline(Element paramElement1, Element paramElement2, AttributeSet paramAttributeSet, Vector<ElementSpec> paramVector, int paramInt1, int paramInt2) {
    if (paramElement1.getParentElement() == paramElement2.getParentElement()) {
      ElementSpec elementSpec = new ElementSpec(paramAttributeSet, (short)2);
      paramVector.addElement(elementSpec);
      elementSpec = new ElementSpec(paramAttributeSet, (short)1);
      paramVector.addElement(elementSpec);
      if (paramElement2.getEndOffset() != paramInt2)
        return 7; 
      Element element = paramElement2.getParentElement();
      if (element.getElementIndex(paramInt1) + 1 < element.getElementCount())
        return 5; 
    } else {
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Element element;
      for (element = paramElement2; element != null; element = element.getParentElement())
        vector1.addElement(element); 
      element = paramElement1;
      int i = -1;
      while (element != null && (i = vector1.indexOf(element)) == -1) {
        vector2.addElement(element);
        element = element.getParentElement();
      } 
      if (element != null) {
        for (byte b = 0; b < i; b++)
          paramVector.addElement(new ElementSpec(null, (short)2)); 
        for (int j = vector2.size() - 1; j >= 0; j--) {
          ElementSpec elementSpec = new ElementSpec(((Element)vector2.elementAt(j)).getAttributes(), (short)1);
          if (j > 0)
            elementSpec.setDirection((short)5); 
          paramVector.addElement(elementSpec);
        } 
        return (vector2.size() > 0) ? 5 : 7;
      } 
    } 
    return 6;
  }
  
  protected void removeUpdate(AbstractDocument.DefaultDocumentEvent paramDefaultDocumentEvent) {
    super.removeUpdate(paramDefaultDocumentEvent);
    this.buffer.remove(paramDefaultDocumentEvent.getOffset(), paramDefaultDocumentEvent.getLength(), paramDefaultDocumentEvent);
  }
  
  protected AbstractDocument.AbstractElement createDefaultRoot() {
    writeLock();
    SectionElement sectionElement = new SectionElement();
    AbstractDocument.BranchElement branchElement = new AbstractDocument.BranchElement(this, sectionElement, null);
    AbstractDocument.LeafElement leafElement = new AbstractDocument.LeafElement(this, branchElement, null, 0, 1);
    Element[] arrayOfElement = new Element[1];
    arrayOfElement[0] = leafElement;
    branchElement.replace(0, 0, arrayOfElement);
    arrayOfElement[0] = branchElement;
    sectionElement.replace(0, 0, arrayOfElement);
    writeUnlock();
    return sectionElement;
  }
  
  public Color getForeground(AttributeSet paramAttributeSet) {
    StyleContext styleContext = (StyleContext)getAttributeContext();
    return styleContext.getForeground(paramAttributeSet);
  }
  
  public Color getBackground(AttributeSet paramAttributeSet) {
    StyleContext styleContext = (StyleContext)getAttributeContext();
    return styleContext.getBackground(paramAttributeSet);
  }
  
  public Font getFont(AttributeSet paramAttributeSet) {
    StyleContext styleContext = (StyleContext)getAttributeContext();
    return styleContext.getFont(paramAttributeSet);
  }
  
  protected void styleChanged(Style paramStyle) {
    if (getLength() != 0) {
      if (this.updateRunnable == null)
        this.updateRunnable = new ChangeUpdateRunnable(); 
      synchronized (this.updateRunnable) {
        if (!this.updateRunnable.isPending) {
          SwingUtilities.invokeLater(this.updateRunnable);
          this.updateRunnable.isPending = true;
        } 
      } 
    } 
  }
  
  public void addDocumentListener(DocumentListener paramDocumentListener) {
    synchronized (this.listeningStyles) {
      int i = this.listenerList.getListenerCount(DocumentListener.class);
      super.addDocumentListener(paramDocumentListener);
      if (i == 0) {
        if (this.styleContextChangeListener == null)
          this.styleContextChangeListener = createStyleContextChangeListener(); 
        if (this.styleContextChangeListener != null) {
          StyleContext styleContext = (StyleContext)getAttributeContext();
          List list = AbstractChangeHandler.getStaleListeners(this.styleContextChangeListener);
          for (ChangeListener changeListener : list)
            styleContext.removeChangeListener(changeListener); 
          styleContext.addChangeListener(this.styleContextChangeListener);
        } 
        updateStylesListeningTo();
      } 
    } 
  }
  
  public void removeDocumentListener(DocumentListener paramDocumentListener) {
    synchronized (this.listeningStyles) {
      super.removeDocumentListener(paramDocumentListener);
      if (this.listenerList.getListenerCount(DocumentListener.class) == 0) {
        for (int i = this.listeningStyles.size() - 1; i >= 0; i--)
          ((Style)this.listeningStyles.elementAt(i)).removeChangeListener(this.styleChangeListener); 
        this.listeningStyles.removeAllElements();
        if (this.styleContextChangeListener != null) {
          StyleContext styleContext = (StyleContext)getAttributeContext();
          styleContext.removeChangeListener(this.styleContextChangeListener);
        } 
      } 
    } 
  }
  
  ChangeListener createStyleChangeListener() { return new StyleChangeHandler(this); }
  
  ChangeListener createStyleContextChangeListener() { return new StyleContextChangeHandler(this); }
  
  void updateStylesListeningTo() {
    synchronized (this.listeningStyles) {
      StyleContext styleContext = (StyleContext)getAttributeContext();
      if (this.styleChangeListener == null)
        this.styleChangeListener = createStyleChangeListener(); 
      if (this.styleChangeListener != null && styleContext != null) {
        Enumeration enumeration = styleContext.getStyleNames();
        Vector vector = (Vector)this.listeningStyles.clone();
        this.listeningStyles.removeAllElements();
        List list = AbstractChangeHandler.getStaleListeners(this.styleChangeListener);
        while (enumeration.hasMoreElements()) {
          String str = (String)enumeration.nextElement();
          Style style = styleContext.getStyle(str);
          int j = vector.indexOf(style);
          this.listeningStyles.addElement(style);
          if (j == -1) {
            for (ChangeListener changeListener : list)
              style.removeChangeListener(changeListener); 
            style.addChangeListener(this.styleChangeListener);
            continue;
          } 
          vector.removeElementAt(j);
        } 
        for (int i = vector.size() - 1; i >= 0; i--) {
          Style style = (Style)vector.elementAt(i);
          style.removeChangeListener(this.styleChangeListener);
        } 
        if (this.listeningStyles.size() == 0)
          this.styleChangeListener = null; 
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.listeningStyles = new Vector();
    paramObjectInputStream.defaultReadObject();
    if (this.styleContextChangeListener == null && this.listenerList.getListenerCount(DocumentListener.class) > 0) {
      this.styleContextChangeListener = createStyleContextChangeListener();
      if (this.styleContextChangeListener != null) {
        StyleContext styleContext = (StyleContext)getAttributeContext();
        styleContext.addChangeListener(this.styleContextChangeListener);
      } 
      updateStylesListeningTo();
    } 
  }
  
  static abstract class AbstractChangeHandler implements ChangeListener {
    private static final Map<Class, ReferenceQueue<DefaultStyledDocument>> queueMap = new HashMap();
    
    private DocReference doc;
    
    AbstractChangeHandler(DefaultStyledDocument param1DefaultStyledDocument) {
      Class clazz = getClass();
      synchronized (queueMap) {
        referenceQueue = (ReferenceQueue)queueMap.get(clazz);
        if (referenceQueue == null) {
          referenceQueue = new ReferenceQueue();
          queueMap.put(clazz, referenceQueue);
        } 
      } 
      this.doc = new DocReference(param1DefaultStyledDocument, referenceQueue);
    }
    
    static List<ChangeListener> getStaleListeners(ChangeListener param1ChangeListener) {
      ArrayList arrayList = new ArrayList();
      ReferenceQueue referenceQueue = (ReferenceQueue)queueMap.get(param1ChangeListener.getClass());
      if (referenceQueue != null)
        synchronized (referenceQueue) {
          DocReference docReference;
          while ((docReference = (DocReference)referenceQueue.poll()) != null)
            arrayList.add(docReference.getListener()); 
        }  
      return arrayList;
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      DefaultStyledDocument defaultStyledDocument = (DefaultStyledDocument)this.doc.get();
      if (defaultStyledDocument != null)
        fireStateChanged(defaultStyledDocument, param1ChangeEvent); 
    }
    
    abstract void fireStateChanged(DefaultStyledDocument param1DefaultStyledDocument, ChangeEvent param1ChangeEvent);
    
    private class DocReference extends WeakReference<DefaultStyledDocument> {
      DocReference(DefaultStyledDocument param2DefaultStyledDocument, ReferenceQueue<DefaultStyledDocument> param2ReferenceQueue) { super(param2DefaultStyledDocument, param2ReferenceQueue); }
      
      ChangeListener getListener() { return DefaultStyledDocument.AbstractChangeHandler.this; }
    }
  }
  
  public static class AttributeUndoableEdit extends AbstractUndoableEdit {
    protected AttributeSet newAttributes;
    
    protected AttributeSet copy;
    
    protected boolean isReplacing;
    
    protected Element element;
    
    public AttributeUndoableEdit(Element param1Element, AttributeSet param1AttributeSet, boolean param1Boolean) {
      this.element = param1Element;
      this.newAttributes = param1AttributeSet;
      this.isReplacing = param1Boolean;
      this.copy = param1Element.getAttributes().copyAttributes();
    }
    
    public void redo() {
      super.redo();
      MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)this.element.getAttributes();
      if (this.isReplacing)
        mutableAttributeSet.removeAttributes(mutableAttributeSet); 
      mutableAttributeSet.addAttributes(this.newAttributes);
    }
    
    public void undo() {
      super.undo();
      MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)this.element.getAttributes();
      mutableAttributeSet.removeAttributes(mutableAttributeSet);
      mutableAttributeSet.addAttributes(this.copy);
    }
  }
  
  class ChangeUpdateRunnable implements Runnable {
    boolean isPending = false;
    
    public void run() {
      synchronized (this) {
        this.isPending = false;
      } 
      try {
        DefaultStyledDocument.this.writeLock();
        AbstractDocument.DefaultDocumentEvent defaultDocumentEvent = new AbstractDocument.DefaultDocumentEvent(DefaultStyledDocument.this, 0, DefaultStyledDocument.this.getLength(), DocumentEvent.EventType.CHANGE);
        defaultDocumentEvent.end();
        DefaultStyledDocument.this.fireChangedUpdate(defaultDocumentEvent);
      } finally {
        DefaultStyledDocument.this.writeUnlock();
      } 
    }
  }
  
  public class ElementBuffer implements Serializable {
    Element root;
    
    int pos;
    
    int offset;
    
    int length;
    
    int endOffset;
    
    Vector<ElemChanges> changes;
    
    Stack<ElemChanges> path;
    
    boolean insertOp;
    
    boolean recreateLeafs;
    
    ElemChanges[] insertPath;
    
    boolean createdFracture;
    
    Element fracturedParent;
    
    Element fracturedChild;
    
    boolean offsetLastIndex;
    
    boolean offsetLastIndexOnReplace;
    
    public ElementBuffer(Element param1Element) {
      this.root = param1Element;
      this.changes = new Vector();
      this.path = new Stack();
    }
    
    public Element getRootElement() { return this.root; }
    
    public void insert(int param1Int1, int param1Int2, DefaultStyledDocument.ElementSpec[] param1ArrayOfElementSpec, AbstractDocument.DefaultDocumentEvent param1DefaultDocumentEvent) {
      if (param1Int2 == 0)
        return; 
      this.insertOp = true;
      beginEdits(param1Int1, param1Int2);
      insertUpdate(param1ArrayOfElementSpec);
      endEdits(param1DefaultDocumentEvent);
      this.insertOp = false;
    }
    
    void create(int param1Int, DefaultStyledDocument.ElementSpec[] param1ArrayOfElementSpec, AbstractDocument.DefaultDocumentEvent param1DefaultDocumentEvent) {
      this.insertOp = true;
      beginEdits(this.offset, param1Int);
      Element element1 = this.root;
      int i;
      for (i = element1.getElementIndex(0); !element1.isLeaf(); i = element1.getElementIndex(0)) {
        Element element = element1.getElement(i);
        push(element1, i);
        element1 = element;
      } 
      ElemChanges elemChanges = (ElemChanges)this.path.peek();
      Element element2 = elemChanges.parent.getElement(elemChanges.index);
      elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), DefaultStyledDocument.this.getLength(), element2.getEndOffset()));
      elemChanges.removed.addElement(element2);
      while (this.path.size() > 1)
        pop(); 
      int j = param1ArrayOfElementSpec.length;
      AttributeSet attributeSet = null;
      if (j > 0 && param1ArrayOfElementSpec[0].getType() == 1)
        attributeSet = param1ArrayOfElementSpec[0].getAttributes(); 
      if (attributeSet == null)
        attributeSet = SimpleAttributeSet.EMPTY; 
      MutableAttributeSet mutableAttributeSet = (MutableAttributeSet)this.root.getAttributes();
      param1DefaultDocumentEvent.addEdit(new DefaultStyledDocument.AttributeUndoableEdit(this.root, attributeSet, true));
      mutableAttributeSet.removeAttributes(mutableAttributeSet);
      mutableAttributeSet.addAttributes(attributeSet);
      for (byte b = 1; b < j; b++)
        insertElement(param1ArrayOfElementSpec[b]); 
      while (this.path.size() != 0)
        pop(); 
      endEdits(param1DefaultDocumentEvent);
      this.insertOp = false;
    }
    
    public void remove(int param1Int1, int param1Int2, AbstractDocument.DefaultDocumentEvent param1DefaultDocumentEvent) {
      beginEdits(param1Int1, param1Int2);
      removeUpdate();
      endEdits(param1DefaultDocumentEvent);
    }
    
    public void change(int param1Int1, int param1Int2, AbstractDocument.DefaultDocumentEvent param1DefaultDocumentEvent) {
      beginEdits(param1Int1, param1Int2);
      changeUpdate();
      endEdits(param1DefaultDocumentEvent);
    }
    
    protected void insertUpdate(DefaultStyledDocument.ElementSpec[] param1ArrayOfElementSpec) {
      byte b;
      Element element = this.root;
      for (int i = element.getElementIndex(this.offset); !element.isLeaf(); i = element.getElementIndex(this.offset)) {
        Element element1 = element.getElement(i);
        push(element, element1.isLeaf() ? i : (i + 1));
        element = element1;
      } 
      this.insertPath = new ElemChanges[this.path.size()];
      this.path.copyInto(this.insertPath);
      this.createdFracture = false;
      this.recreateLeafs = false;
      if (param1ArrayOfElementSpec[0].getType() == 3) {
        insertFirstContent(param1ArrayOfElementSpec);
        this.pos += param1ArrayOfElementSpec[0].getLength();
        b = 1;
      } else {
        fractureDeepestLeaf(param1ArrayOfElementSpec);
        b = 0;
      } 
      int j = param1ArrayOfElementSpec.length;
      while (b < j) {
        insertElement(param1ArrayOfElementSpec[b]);
        b++;
      } 
      if (!this.createdFracture)
        fracture(-1); 
      while (this.path.size() != 0)
        pop(); 
      if (this.offsetLastIndex && this.offsetLastIndexOnReplace)
        (this.insertPath[this.insertPath.length - 1]).index++; 
      int k;
      for (k = this.insertPath.length - 1; k >= 0; k--) {
        ElemChanges elemChanges = this.insertPath[k];
        if (elemChanges.parent == this.fracturedParent)
          elemChanges.added.addElement(this.fracturedChild); 
        if ((elemChanges.added.size() > 0 || elemChanges.removed.size() > 0) && !this.changes.contains(elemChanges))
          this.changes.addElement(elemChanges); 
      } 
      if (this.offset == 0 && this.fracturedParent != null && param1ArrayOfElementSpec[0].getType() == 2) {
        for (k = 0; k < param1ArrayOfElementSpec.length && param1ArrayOfElementSpec[k].getType() == 2; k++);
        ElemChanges elemChanges = this.insertPath[this.insertPath.length - k - 1];
        elemChanges.removed.insertElementAt(elemChanges.parent.getElement(--elemChanges.index), 0);
      } 
    }
    
    protected void removeUpdate() { removeElements(this.root, this.offset, this.offset + this.length); }
    
    protected void changeUpdate() {
      boolean bool = split(this.offset, this.length);
      if (!bool) {
        while (this.path.size() != 0)
          pop(); 
        split(this.offset + this.length, 0);
      } 
      while (this.path.size() != 0)
        pop(); 
    }
    
    boolean split(int param1Int1, int param1Int2) {
      boolean bool = false;
      Element element1 = this.root;
      int i;
      for (i = element1.getElementIndex(param1Int1); !element1.isLeaf(); i = element1.getElementIndex(param1Int1)) {
        push(element1, i);
        element1 = element1.getElement(i);
      } 
      ElemChanges elemChanges = (ElemChanges)this.path.peek();
      Element element2 = elemChanges.parent.getElement(elemChanges.index);
      if (element2.getStartOffset() < param1Int1 && param1Int1 < element2.getEndOffset()) {
        int j = elemChanges.index;
        int k = j;
        if (param1Int1 + param1Int2 < elemChanges.parent.getEndOffset() && param1Int2 != 0) {
          k = elemChanges.parent.getElementIndex(param1Int1 + param1Int2);
          if (k == j) {
            elemChanges.removed.addElement(element2);
            element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), element2.getStartOffset(), param1Int1);
            elemChanges.added.addElement(element1);
            element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), param1Int1, param1Int1 + param1Int2);
            elemChanges.added.addElement(element1);
            element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), param1Int1 + param1Int2, element2.getEndOffset());
            elemChanges.added.addElement(element1);
            return true;
          } 
          element2 = elemChanges.parent.getElement(k);
          if (param1Int1 + param1Int2 == element2.getStartOffset())
            k = j; 
          bool = true;
        } 
        this.pos = param1Int1;
        element2 = elemChanges.parent.getElement(j);
        elemChanges.removed.addElement(element2);
        element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), element2.getStartOffset(), this.pos);
        elemChanges.added.addElement(element1);
        element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.pos, element2.getEndOffset());
        elemChanges.added.addElement(element1);
        for (int m = j + 1; m < k; m++) {
          element2 = elemChanges.parent.getElement(m);
          elemChanges.removed.addElement(element2);
          elemChanges.added.addElement(element2);
        } 
        if (k != j) {
          element2 = elemChanges.parent.getElement(k);
          this.pos = param1Int1 + param1Int2;
          elemChanges.removed.addElement(element2);
          element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), element2.getStartOffset(), this.pos);
          elemChanges.added.addElement(element1);
          element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.pos, element2.getEndOffset());
          elemChanges.added.addElement(element1);
        } 
      } 
      return bool;
    }
    
    void endEdits(AbstractDocument.DefaultDocumentEvent param1DefaultDocumentEvent) {
      int i = this.changes.size();
      for (byte b = 0; b < i; b++) {
        ElemChanges elemChanges = (ElemChanges)this.changes.elementAt(b);
        Element[] arrayOfElement1 = new Element[elemChanges.removed.size()];
        elemChanges.removed.copyInto(arrayOfElement1);
        Element[] arrayOfElement2 = new Element[elemChanges.added.size()];
        elemChanges.added.copyInto(arrayOfElement2);
        int j = elemChanges.index;
        ((AbstractDocument.BranchElement)elemChanges.parent).replace(j, arrayOfElement1.length, arrayOfElement2);
        AbstractDocument.ElementEdit elementEdit = new AbstractDocument.ElementEdit(elemChanges.parent, j, arrayOfElement1, arrayOfElement2);
        param1DefaultDocumentEvent.addEdit(elementEdit);
      } 
      this.changes.removeAllElements();
      this.path.removeAllElements();
    }
    
    void beginEdits(int param1Int1, int param1Int2) {
      this.offset = param1Int1;
      this.length = param1Int2;
      this.endOffset = param1Int1 + param1Int2;
      this.pos = param1Int1;
      if (this.changes == null) {
        this.changes = new Vector();
      } else {
        this.changes.removeAllElements();
      } 
      if (this.path == null) {
        this.path = new Stack();
      } else {
        this.path.removeAllElements();
      } 
      this.fracturedParent = null;
      this.fracturedChild = null;
      this.offsetLastIndex = this.offsetLastIndexOnReplace = false;
    }
    
    void push(Element param1Element, int param1Int, boolean param1Boolean) {
      ElemChanges elemChanges = new ElemChanges(param1Element, param1Int, param1Boolean);
      this.path.push(elemChanges);
    }
    
    void push(Element param1Element, int param1Int) { push(param1Element, param1Int, false); }
    
    void pop() {
      ElemChanges elemChanges = (ElemChanges)this.path.peek();
      this.path.pop();
      if (elemChanges.added.size() > 0 || elemChanges.removed.size() > 0) {
        this.changes.addElement(elemChanges);
      } else if (!this.path.isEmpty()) {
        Element element = elemChanges.parent;
        if (element.getElementCount() == 0) {
          elemChanges = (ElemChanges)this.path.peek();
          elemChanges.added.removeElement(element);
        } 
      } 
    }
    
    void advance(int param1Int) { this.pos += param1Int; }
    
    void insertElement(DefaultStyledDocument.ElementSpec param1ElementSpec) {
      Element element2;
      Element element1;
      int i;
      ElemChanges elemChanges = (ElemChanges)this.path.peek();
      switch (param1ElementSpec.getType()) {
        case 1:
          switch (param1ElementSpec.getDirection()) {
            case 5:
              element1 = elemChanges.parent.getElement(elemChanges.index);
              if (element1.isLeaf())
                if (elemChanges.index + 1 < elemChanges.parent.getElementCount()) {
                  element1 = elemChanges.parent.getElement(elemChanges.index + 1);
                } else {
                  throw new StateInvariantError("Join next to leaf");
                }  
              push(element1, 0, true);
              break;
            case 7:
              if (!this.createdFracture)
                fracture(this.path.size() - 1); 
              if (!elemChanges.isFracture) {
                push(this.fracturedChild, 0, true);
                break;
              } 
              push(elemChanges.parent.getElement(0), 0, true);
              break;
          } 
          element2 = DefaultStyledDocument.this.createBranchElement(elemChanges.parent, param1ElementSpec.getAttributes());
          elemChanges.added.addElement(element2);
          push(element2, 0);
          break;
        case 2:
          pop();
          break;
        case 3:
          i = param1ElementSpec.getLength();
          if (param1ElementSpec.getDirection() != 5) {
            element2 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, param1ElementSpec.getAttributes(), this.pos, this.pos + i);
            elemChanges.added.addElement(element2);
          } else if (!elemChanges.isFracture) {
            element2 = null;
            if (this.insertPath != null)
              for (int j = this.insertPath.length - 1; j >= 0; j--) {
                if (this.insertPath[j] == elemChanges) {
                  if (j != this.insertPath.length - 1)
                    element2 = elemChanges.parent.getElement(elemChanges.index); 
                  break;
                } 
              }  
            if (element2 == null)
              element2 = elemChanges.parent.getElement(elemChanges.index + 1); 
            Element element = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.pos, element2.getEndOffset());
            elemChanges.added.addElement(element);
            elemChanges.removed.addElement(element2);
          } else {
            element2 = elemChanges.parent.getElement(0);
            Element element = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.pos, element2.getEndOffset());
            elemChanges.added.addElement(element);
            elemChanges.removed.addElement(element2);
          } 
          this.pos += i;
          break;
      } 
    }
    
    boolean removeElements(Element param1Element, int param1Int1, int param1Int2) {
      if (!param1Element.isLeaf()) {
        int i = param1Element.getElementIndex(param1Int1);
        int j = param1Element.getElementIndex(param1Int2);
        push(param1Element, i);
        ElemChanges elemChanges = (ElemChanges)this.path.peek();
        if (i == j) {
          Element element = param1Element.getElement(i);
          if (param1Int1 <= element.getStartOffset() && param1Int2 >= element.getEndOffset()) {
            elemChanges.removed.addElement(element);
          } else if (removeElements(element, param1Int1, param1Int2)) {
            elemChanges.removed.addElement(element);
          } 
        } else {
          Element element1 = param1Element.getElement(i);
          Element element2 = param1Element.getElement(j);
          boolean bool = (param1Int2 < param1Element.getEndOffset()) ? 1 : 0;
          if (bool && canJoin(element1, element2)) {
            for (int k = i; k <= j; k++)
              elemChanges.removed.addElement(param1Element.getElement(k)); 
            Element element = join(param1Element, element1, element2, param1Int1, param1Int2);
            elemChanges.added.addElement(element);
          } else {
            int k = i + 1;
            int m = j - 1;
            if (element1.getStartOffset() == param1Int1 || (i == 0 && element1.getStartOffset() > param1Int1 && element1.getEndOffset() <= param1Int2)) {
              element1 = null;
              k = i;
            } 
            if (!bool) {
              element2 = null;
              m++;
            } else if (element2.getStartOffset() == param1Int2) {
              element2 = null;
            } 
            if (k <= m)
              elemChanges.index = k; 
            for (int n = k; n <= m; n++)
              elemChanges.removed.addElement(param1Element.getElement(n)); 
            if (element1 != null && removeElements(element1, param1Int1, param1Int2)) {
              elemChanges.removed.insertElementAt(element1, 0);
              elemChanges.index = i;
            } 
            if (element2 != null && removeElements(element2, param1Int1, param1Int2))
              elemChanges.removed.addElement(element2); 
          } 
        } 
        pop();
        if (param1Element.getElementCount() == elemChanges.removed.size() - elemChanges.added.size())
          return true; 
      } 
      return false;
    }
    
    boolean canJoin(Element param1Element1, Element param1Element2) {
      if (param1Element1 == null || param1Element2 == null)
        return false; 
      boolean bool1 = param1Element1.isLeaf();
      boolean bool2 = param1Element2.isLeaf();
      if (bool1 != bool2)
        return false; 
      if (bool1)
        return param1Element1.getAttributes().isEqual(param1Element2.getAttributes()); 
      String str1 = param1Element1.getName();
      String str2 = param1Element2.getName();
      return (str1 != null) ? str1.equals(str2) : ((str2 != null) ? str2.equals(str1) : 1);
    }
    
    Element join(Element param1Element1, Element param1Element2, Element param1Element3, int param1Int1, int param1Int2) {
      if (param1Element2.isLeaf() && param1Element3.isLeaf())
        return DefaultStyledDocument.this.createLeafElement(param1Element1, param1Element2.getAttributes(), param1Element2.getStartOffset(), param1Element3.getEndOffset()); 
      if (!param1Element2.isLeaf() && !param1Element3.isLeaf()) {
        Element element1 = DefaultStyledDocument.this.createBranchElement(param1Element1, param1Element2.getAttributes());
        int i = param1Element2.getElementIndex(param1Int1);
        int j = param1Element3.getElementIndex(param1Int2);
        Element element2 = param1Element2.getElement(i);
        if (element2.getStartOffset() >= param1Int1)
          element2 = null; 
        Element element3 = param1Element3.getElement(j);
        if (element3.getStartOffset() == param1Int2)
          element3 = null; 
        Vector vector = new Vector();
        int k;
        for (k = 0; k < i; k++)
          vector.addElement(clone(element1, param1Element2.getElement(k))); 
        if (canJoin(element2, element3)) {
          Element element = join(element1, element2, element3, param1Int1, param1Int2);
          vector.addElement(element);
        } else {
          if (element2 != null)
            vector.addElement(cloneAsNecessary(element1, element2, param1Int1, param1Int2)); 
          if (element3 != null)
            vector.addElement(cloneAsNecessary(element1, element3, param1Int1, param1Int2)); 
        } 
        k = param1Element3.getElementCount();
        for (int m = (element3 == null) ? j : (j + 1); m < k; m++)
          vector.addElement(clone(element1, param1Element3.getElement(m))); 
        Element[] arrayOfElement = new Element[vector.size()];
        vector.copyInto(arrayOfElement);
        ((AbstractDocument.BranchElement)element1).replace(0, 0, arrayOfElement);
        return element1;
      } 
      throw new StateInvariantError("No support to join leaf element with non-leaf element");
    }
    
    public Element clone(Element param1Element1, Element param1Element2) {
      if (param1Element2.isLeaf())
        return DefaultStyledDocument.this.createLeafElement(param1Element1, param1Element2.getAttributes(), param1Element2.getStartOffset(), param1Element2.getEndOffset()); 
      Element element = DefaultStyledDocument.this.createBranchElement(param1Element1, param1Element2.getAttributes());
      int i = param1Element2.getElementCount();
      Element[] arrayOfElement = new Element[i];
      for (byte b = 0; b < i; b++)
        arrayOfElement[b] = clone(element, param1Element2.getElement(b)); 
      ((AbstractDocument.BranchElement)element).replace(0, 0, arrayOfElement);
      return element;
    }
    
    Element cloneAsNecessary(Element param1Element1, Element param1Element2, int param1Int1, int param1Int2) {
      if (param1Element2.isLeaf())
        return DefaultStyledDocument.this.createLeafElement(param1Element1, param1Element2.getAttributes(), param1Element2.getStartOffset(), param1Element2.getEndOffset()); 
      Element element = DefaultStyledDocument.this.createBranchElement(param1Element1, param1Element2.getAttributes());
      int i = param1Element2.getElementCount();
      ArrayList arrayList = new ArrayList(i);
      for (byte b = 0; b < i; b++) {
        Element element1 = param1Element2.getElement(b);
        if (element1.getStartOffset() < param1Int1 || element1.getEndOffset() > param1Int2)
          arrayList.add(cloneAsNecessary(element, element1, param1Int1, param1Int2)); 
      } 
      Element[] arrayOfElement = new Element[arrayList.size()];
      arrayOfElement = (Element[])arrayList.toArray(arrayOfElement);
      ((AbstractDocument.BranchElement)element).replace(0, 0, arrayOfElement);
      return element;
    }
    
    void fracture(int param1Int) {
      int i = this.insertPath.length;
      int j = -1;
      boolean bool = this.recreateLeafs;
      ElemChanges elemChanges = this.insertPath[i - 1];
      boolean bool1 = (elemChanges.index + 1 < elemChanges.parent.getElementCount()) ? 1 : 0;
      int k = bool ? i : -1;
      int m = i - 1;
      this.createdFracture = true;
      for (int n = i - 2; n >= 0; n--) {
        ElemChanges elemChanges1 = this.insertPath[n];
        if (elemChanges1.added.size() > 0 || n == param1Int) {
          j = n;
          if (!bool && bool1) {
            bool = true;
            if (k == -1)
              k = m + 1; 
          } 
        } 
        if (!bool1 && elemChanges1.index < elemChanges1.parent.getElementCount()) {
          bool1 = true;
          m = n;
        } 
      } 
      if (bool) {
        if (j == -1)
          j = i - 1; 
        fractureFrom(this.insertPath, j, k);
      } 
    }
    
    void fractureFrom(ElemChanges[] param1ArrayOfElemChanges, int param1Int1, int param1Int2) {
      Element element2;
      Element element1;
      ElemChanges elemChanges = param1ArrayOfElemChanges[param1Int1];
      int i = param1ArrayOfElemChanges.length;
      if (param1Int1 + 1 == i) {
        element1 = elemChanges.parent.getElement(elemChanges.index);
      } else {
        element1 = elemChanges.parent.getElement(elemChanges.index - 1);
      } 
      if (element1.isLeaf()) {
        element2 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element1.getAttributes(), Math.max(this.endOffset, element1.getStartOffset()), element1.getEndOffset());
      } else {
        element2 = DefaultStyledDocument.this.createBranchElement(elemChanges.parent, element1.getAttributes());
      } 
      this.fracturedParent = elemChanges.parent;
      this.fracturedChild = element2;
      for (Element element3 = element2; ++param1Int1 < param1Int2; element3 = element2) {
        int k;
        Element[] arrayOfElement;
        boolean bool1 = (param1Int1 + 1 == param1Int2) ? 1 : 0;
        boolean bool2 = (param1Int1 + 1 == i) ? 1 : 0;
        elemChanges = param1ArrayOfElemChanges[param1Int1];
        if (bool1) {
          if (this.offsetLastIndex || !bool2) {
            element1 = null;
          } else {
            element1 = elemChanges.parent.getElement(elemChanges.index);
          } 
        } else {
          element1 = elemChanges.parent.getElement(elemChanges.index - 1);
        } 
        if (element1 != null) {
          if (element1.isLeaf()) {
            element2 = DefaultStyledDocument.this.createLeafElement(element3, element1.getAttributes(), Math.max(this.endOffset, element1.getStartOffset()), element1.getEndOffset());
          } else {
            element2 = DefaultStyledDocument.this.createBranchElement(element3, element1.getAttributes());
          } 
        } else {
          element2 = null;
        } 
        int j = elemChanges.parent.getElementCount() - elemChanges.index;
        boolean bool3 = true;
        if (element2 == null) {
          if (bool2) {
            j--;
            k = elemChanges.index + 1;
          } else {
            k = elemChanges.index;
          } 
          bool3 = false;
          arrayOfElement = new Element[j];
        } else {
          if (!bool1) {
            j++;
            k = elemChanges.index;
          } else {
            k = elemChanges.index + 1;
          } 
          arrayOfElement = new Element[j];
          arrayOfElement[0] = element2;
        } 
        for (byte b = bool3; b < j; b++) {
          Element element = elemChanges.parent.getElement(k++);
          arrayOfElement[b] = recreateFracturedElement(element3, element);
          elemChanges.removed.addElement(element);
        } 
        ((AbstractDocument.BranchElement)element3).replace(0, 0, arrayOfElement);
      } 
    }
    
    Element recreateFracturedElement(Element param1Element1, Element param1Element2) {
      if (param1Element2.isLeaf())
        return DefaultStyledDocument.this.createLeafElement(param1Element1, param1Element2.getAttributes(), Math.max(param1Element2.getStartOffset(), this.endOffset), param1Element2.getEndOffset()); 
      Element element = DefaultStyledDocument.this.createBranchElement(param1Element1, param1Element2.getAttributes());
      int i = param1Element2.getElementCount();
      Element[] arrayOfElement = new Element[i];
      for (byte b = 0; b < i; b++)
        arrayOfElement[b] = recreateFracturedElement(element, param1Element2.getElement(b)); 
      ((AbstractDocument.BranchElement)element).replace(0, 0, arrayOfElement);
      return element;
    }
    
    void fractureDeepestLeaf(DefaultStyledDocument.ElementSpec[] param1ArrayOfElementSpec) {
      ElemChanges elemChanges = (ElemChanges)this.path.peek();
      Element element = elemChanges.parent.getElement(elemChanges.index);
      if (this.offset != 0) {
        Element element1 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element.getAttributes(), element.getStartOffset(), this.offset);
        elemChanges.added.addElement(element1);
      } 
      elemChanges.removed.addElement(element);
      if (element.getEndOffset() != this.endOffset) {
        this.recreateLeafs = true;
      } else {
        this.offsetLastIndex = true;
      } 
    }
    
    void insertFirstContent(DefaultStyledDocument.ElementSpec[] param1ArrayOfElementSpec) {
      DefaultStyledDocument.ElementSpec elementSpec = param1ArrayOfElementSpec[0];
      ElemChanges elemChanges = (ElemChanges)this.path.peek();
      Element element1 = elemChanges.parent.getElement(elemChanges.index);
      int i = this.offset + elementSpec.getLength();
      boolean bool = (param1ArrayOfElementSpec.length == 1) ? 1 : 0;
      switch (elementSpec.getDirection()) {
        case 4:
          if (element1.getEndOffset() != i && !bool) {
            Element element = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element1.getAttributes(), element1.getStartOffset(), i);
            elemChanges.added.addElement(element);
            elemChanges.removed.addElement(element1);
            if (element1.getEndOffset() != this.endOffset) {
              this.recreateLeafs = true;
            } else {
              this.offsetLastIndex = true;
            } 
          } else {
            this.offsetLastIndex = true;
            this.offsetLastIndexOnReplace = true;
          } 
          return;
        case 5:
          if (this.offset != 0) {
            Element element3 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element1.getAttributes(), element1.getStartOffset(), this.offset);
            elemChanges.added.addElement(element3);
            Element element4 = elemChanges.parent.getElement(elemChanges.index + 1);
            if (bool) {
              element3 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element4.getAttributes(), this.offset, element4.getEndOffset());
            } else {
              element3 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element4.getAttributes(), this.offset, i);
            } 
            elemChanges.added.addElement(element3);
            elemChanges.removed.addElement(element1);
            elemChanges.removed.addElement(element4);
          } 
          return;
      } 
      if (element1.getStartOffset() != this.offset) {
        Element element = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element1.getAttributes(), element1.getStartOffset(), this.offset);
        elemChanges.added.addElement(element);
      } 
      elemChanges.removed.addElement(element1);
      Element element2 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, elementSpec.getAttributes(), this.offset, i);
      elemChanges.added.addElement(element2);
      if (element1.getEndOffset() != this.endOffset) {
        this.recreateLeafs = true;
      } else {
        this.offsetLastIndex = true;
      } 
    }
    
    class ElemChanges {
      Element parent;
      
      int index;
      
      Vector<Element> added;
      
      Vector<Element> removed;
      
      boolean isFracture;
      
      ElemChanges(Element param2Element, int param2Int, boolean param2Boolean) {
        this.parent = param2Element;
        this.index = param2Int;
        this.isFracture = param2Boolean;
        this.added = new Vector();
        this.removed = new Vector();
      }
      
      public String toString() { return "added: " + this.added + "\nremoved: " + this.removed + "\n"; }
    }
  }
  
  public static class ElementSpec {
    public static final short StartTagType = 1;
    
    public static final short EndTagType = 2;
    
    public static final short ContentType = 3;
    
    public static final short JoinPreviousDirection = 4;
    
    public static final short JoinNextDirection = 5;
    
    public static final short OriginateDirection = 6;
    
    public static final short JoinFractureDirection = 7;
    
    private AttributeSet attr;
    
    private int len;
    
    private short type;
    
    private short direction;
    
    private int offs;
    
    private char[] data;
    
    public ElementSpec(AttributeSet param1AttributeSet, short param1Short) { this(param1AttributeSet, param1Short, null, 0, 0); }
    
    public ElementSpec(AttributeSet param1AttributeSet, short param1Short, int param1Int) { this(param1AttributeSet, param1Short, null, 0, param1Int); }
    
    public ElementSpec(AttributeSet param1AttributeSet, short param1Short, char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      this.attr = param1AttributeSet;
      this.type = param1Short;
      this.data = param1ArrayOfChar;
      this.offs = param1Int1;
      this.len = param1Int2;
      this.direction = 6;
    }
    
    public void setType(short param1Short) { this.type = param1Short; }
    
    public short getType() { return this.type; }
    
    public void setDirection(short param1Short) { this.direction = param1Short; }
    
    public short getDirection() { return this.direction; }
    
    public AttributeSet getAttributes() { return this.attr; }
    
    public char[] getArray() { return this.data; }
    
    public int getOffset() { return this.offs; }
    
    public int getLength() { return this.len; }
    
    public String toString() {
      String str1 = "??";
      String str2 = "??";
      switch (this.type) {
        case 1:
          str1 = "StartTag";
          break;
        case 3:
          str1 = "Content";
          break;
        case 2:
          str1 = "EndTag";
          break;
      } 
      switch (this.direction) {
        case 4:
          str2 = "JoinPrevious";
          break;
        case 5:
          str2 = "JoinNext";
          break;
        case 6:
          str2 = "Originate";
          break;
        case 7:
          str2 = "Fracture";
          break;
      } 
      return str1 + ":" + str2 + ":" + getLength();
    }
  }
  
  protected class SectionElement extends AbstractDocument.BranchElement {
    public SectionElement() { super(DefaultStyledDocument.this, null, null); }
    
    public String getName() { return "section"; }
  }
  
  static class StyleChangeHandler extends AbstractChangeHandler {
    StyleChangeHandler(DefaultStyledDocument param1DefaultStyledDocument) { super(param1DefaultStyledDocument); }
    
    void fireStateChanged(DefaultStyledDocument param1DefaultStyledDocument, ChangeEvent param1ChangeEvent) {
      Object object = param1ChangeEvent.getSource();
      if (object instanceof Style) {
        param1DefaultStyledDocument.styleChanged((Style)object);
      } else {
        param1DefaultStyledDocument.styleChanged(null);
      } 
    }
  }
  
  static class StyleChangeUndoableEdit extends AbstractUndoableEdit {
    protected AbstractDocument.AbstractElement element;
    
    protected Style newStyle;
    
    protected AttributeSet oldStyle;
    
    public StyleChangeUndoableEdit(AbstractDocument.AbstractElement param1AbstractElement, Style param1Style) {
      this.element = param1AbstractElement;
      this.newStyle = param1Style;
      this.oldStyle = param1AbstractElement.getResolveParent();
    }
    
    public void redo() {
      super.redo();
      this.element.setResolveParent(this.newStyle);
    }
    
    public void undo() {
      super.undo();
      this.element.setResolveParent(this.oldStyle);
    }
  }
  
  static class StyleContextChangeHandler extends AbstractChangeHandler {
    StyleContextChangeHandler(DefaultStyledDocument param1DefaultStyledDocument) { super(param1DefaultStyledDocument); }
    
    void fireStateChanged(DefaultStyledDocument param1DefaultStyledDocument, ChangeEvent param1ChangeEvent) { param1DefaultStyledDocument.updateStylesListeningTo(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DefaultStyledDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */