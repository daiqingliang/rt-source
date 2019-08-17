package javax.swing.text.html;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleText;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

class AccessibleHTML implements Accessible {
  private JEditorPane editor;
  
  private Document model;
  
  private DocumentListener docListener;
  
  private PropertyChangeListener propChangeListener;
  
  private ElementInfo rootElementInfo;
  
  private RootHTMLAccessibleContext rootHTMLAccessibleContext;
  
  public AccessibleHTML(JEditorPane paramJEditorPane) {
    this.editor = paramJEditorPane;
    this.propChangeListener = new PropertyChangeHandler(null);
    setDocument(this.editor.getDocument());
    this.docListener = new DocumentHandler(null);
  }
  
  private void setDocument(Document paramDocument) {
    if (this.model != null)
      this.model.removeDocumentListener(this.docListener); 
    if (this.editor != null)
      this.editor.removePropertyChangeListener(this.propChangeListener); 
    this.model = paramDocument;
    if (this.model != null) {
      if (this.rootElementInfo != null)
        this.rootElementInfo.invalidate(false); 
      buildInfo();
      this.model.addDocumentListener(this.docListener);
    } else {
      this.rootElementInfo = null;
    } 
    if (this.editor != null)
      this.editor.addPropertyChangeListener(this.propChangeListener); 
  }
  
  private Document getDocument() { return this.model; }
  
  private JEditorPane getTextComponent() { return this.editor; }
  
  private ElementInfo getRootInfo() { return this.rootElementInfo; }
  
  private View getRootView() { return getTextComponent().getUI().getRootView(getTextComponent()); }
  
  private Rectangle getRootEditorRect() {
    Rectangle rectangle = getTextComponent().getBounds();
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
  
  private Object lock() {
    Document document = getDocument();
    if (document instanceof AbstractDocument) {
      ((AbstractDocument)document).readLock();
      return document;
    } 
    return null;
  }
  
  private void unlock(Object paramObject) {
    if (paramObject != null)
      ((AbstractDocument)paramObject).readUnlock(); 
  }
  
  private void buildInfo() {
    object = lock();
    try {
      Document document = getDocument();
      Element element = document.getDefaultRootElement();
      this.rootElementInfo = new ElementInfo(element);
      this.rootElementInfo.validate();
    } finally {
      unlock(object);
    } 
  }
  
  ElementInfo createElementInfo(Element paramElement, ElementInfo paramElementInfo) {
    AttributeSet attributeSet = paramElement.getAttributes();
    if (attributeSet != null) {
      Object object = attributeSet.getAttribute(StyleConstants.NameAttribute);
      if (object == HTML.Tag.IMG)
        return new IconElementInfo(paramElement, paramElementInfo); 
      if (object == HTML.Tag.CONTENT || object == HTML.Tag.CAPTION)
        return new TextElementInfo(paramElement, paramElementInfo); 
      if (object == HTML.Tag.TABLE)
        return new TableElementInfo(paramElement, paramElementInfo); 
    } 
    return null;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.rootHTMLAccessibleContext == null)
      this.rootHTMLAccessibleContext = new RootHTMLAccessibleContext(this.rootElementInfo); 
    return this.rootHTMLAccessibleContext;
  }
  
  private class DocumentHandler implements DocumentListener {
    private DocumentHandler() {}
    
    public void insertUpdate(DocumentEvent param1DocumentEvent) { AccessibleHTML.this.getRootInfo().update(param1DocumentEvent); }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent) { AccessibleHTML.this.getRootInfo().update(param1DocumentEvent); }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent) { AccessibleHTML.this.getRootInfo().update(param1DocumentEvent); }
  }
  
  private class ElementInfo {
    private ArrayList<ElementInfo> children;
    
    private Element element;
    
    private ElementInfo parent;
    
    private boolean isValid;
    
    private boolean canBeValid;
    
    ElementInfo(AccessibleHTML this$0, Element param1Element) { this(param1Element, null); }
    
    ElementInfo(Element param1Element, ElementInfo param1ElementInfo) {
      this.element = param1Element;
      this.parent = param1ElementInfo;
      this.isValid = false;
      this.canBeValid = true;
    }
    
    protected void validate() {
      this.isValid = true;
      loadChildren(getElement());
    }
    
    protected void loadChildren(Element param1Element) {
      if (!param1Element.isLeaf()) {
        byte b = 0;
        int i = param1Element.getElementCount();
        while (b < i) {
          Element element1 = param1Element.getElement(b);
          ElementInfo elementInfo = AccessibleHTML.this.createElementInfo(element1, this);
          if (elementInfo != null) {
            addChild(elementInfo);
          } else {
            loadChildren(element1);
          } 
          b++;
        } 
      } 
    }
    
    public int getIndexInParent() { return (this.parent == null || !this.parent.isValid()) ? -1 : this.parent.indexOf(this); }
    
    public Element getElement() { return this.element; }
    
    public ElementInfo getParent() { return this.parent; }
    
    public int indexOf(ElementInfo param1ElementInfo) {
      ArrayList arrayList = this.children;
      return (arrayList != null) ? arrayList.indexOf(param1ElementInfo) : -1;
    }
    
    public ElementInfo getChild(int param1Int) {
      if (validateIfNecessary()) {
        ArrayList arrayList = this.children;
        if (arrayList != null && param1Int >= 0 && param1Int < arrayList.size())
          return (ElementInfo)arrayList.get(param1Int); 
      } 
      return null;
    }
    
    public int getChildCount() {
      validateIfNecessary();
      return (this.children == null) ? 0 : this.children.size();
    }
    
    protected void addChild(ElementInfo param1ElementInfo) {
      if (this.children == null)
        this.children = new ArrayList(); 
      this.children.add(param1ElementInfo);
    }
    
    protected View getView() {
      if (!validateIfNecessary())
        return null; 
      object = AccessibleHTML.this.lock();
      try {
        View view = AccessibleHTML.this.getRootView();
        Element element1 = getElement();
        int i = element1.getStartOffset();
        if (view != null)
          return getView(view, element1, i); 
        return null;
      } finally {
        AccessibleHTML.this.unlock(object);
      } 
    }
    
    public Rectangle getBounds() {
      if (!validateIfNecessary())
        return null; 
      object = AccessibleHTML.this.lock();
      try {
        Rectangle rectangle = AccessibleHTML.this.getRootEditorRect();
        View view = AccessibleHTML.this.getRootView();
        Element element1 = getElement();
        if (rectangle != null && view != null)
          try {
            return view.modelToView(element1.getStartOffset(), Position.Bias.Forward, element1.getEndOffset(), Position.Bias.Backward, rectangle).getBounds();
          } catch (BadLocationException badLocationException) {} 
      } finally {
        AccessibleHTML.this.unlock(object);
      } 
      return null;
    }
    
    protected boolean isValid() { return this.isValid; }
    
    protected AttributeSet getAttributes() { return validateIfNecessary() ? getElement().getAttributes() : null; }
    
    protected AttributeSet getViewAttributes() {
      if (validateIfNecessary()) {
        View view = getView();
        return (view != null) ? view.getElement().getAttributes() : getElement().getAttributes();
      } 
      return null;
    }
    
    protected int getIntAttr(AttributeSet param1AttributeSet, Object param1Object, int param1Int) {
      if (param1AttributeSet != null && param1AttributeSet.isDefined(param1Object)) {
        int i;
        String str = (String)param1AttributeSet.getAttribute(param1Object);
        if (str == null) {
          i = param1Int;
        } else {
          try {
            i = Math.max(0, Integer.parseInt(str));
          } catch (NumberFormatException numberFormatException) {
            i = param1Int;
          } 
        } 
        return i;
      } 
      return param1Int;
    }
    
    protected boolean validateIfNecessary() {
      if (!isValid() && this.canBeValid) {
        this.children = null;
        object = AccessibleHTML.this.lock();
        try {
          validate();
        } finally {
          AccessibleHTML.this.unlock(object);
        } 
      } 
      return isValid();
    }
    
    protected void invalidate(boolean param1Boolean) {
      if (!isValid()) {
        if (this.canBeValid && !param1Boolean)
          this.canBeValid = false; 
        return;
      } 
      this.isValid = false;
      this.canBeValid = param1Boolean;
      if (this.children != null) {
        for (ElementInfo elementInfo : this.children)
          elementInfo.invalidate(false); 
        this.children = null;
      } 
    }
    
    private View getView(View param1View, Element param1Element, int param1Int) {
      if (param1View.getElement() == param1Element)
        return param1View; 
      int i = param1View.getViewIndex(param1Int, Position.Bias.Forward);
      return (i != -1 && i < param1View.getViewCount()) ? getView(param1View.getView(i), param1Element, param1Int) : null;
    }
    
    private int getClosestInfoIndex(int param1Int) {
      for (byte b = 0; b < getChildCount(); b++) {
        ElementInfo elementInfo = getChild(b);
        if (param1Int < elementInfo.getElement().getEndOffset() || param1Int == elementInfo.getElement().getStartOffset())
          return b; 
      } 
      return -1;
    }
    
    private void update(DocumentEvent param1DocumentEvent) {
      if (!isValid())
        return; 
      ElementInfo elementInfo = getParent();
      Element element1 = getElement();
      do {
        DocumentEvent.ElementChange elementChange = param1DocumentEvent.getChange(element1);
        if (elementChange != null) {
          if (element1 == getElement()) {
            invalidate(true);
          } else if (elementInfo != null) {
            elementInfo.invalidate((elementInfo == AccessibleHTML.this.getRootInfo()));
          } 
          return;
        } 
        element1 = element1.getParentElement();
      } while (elementInfo != null && element1 != null && element1 != elementInfo.getElement());
      if (getChildCount() > 0) {
        int k;
        Element element2 = getElement();
        int i = param1DocumentEvent.getOffset();
        int j = getClosestInfoIndex(i);
        if (j == -1 && param1DocumentEvent.getType() == DocumentEvent.EventType.REMOVE && i >= element2.getEndOffset())
          j = getChildCount() - 1; 
        ElementInfo elementInfo1 = (j >= 0) ? getChild(j) : null;
        if (elementInfo1 != null && elementInfo1.getElement().getStartOffset() == i && i > 0)
          j = Math.max(j - 1, 0); 
        if (param1DocumentEvent.getType() != DocumentEvent.EventType.REMOVE) {
          k = getClosestInfoIndex(i + param1DocumentEvent.getLength());
          if (k < 0)
            k = getChildCount() - 1; 
        } else {
          for (k = j; k + 1 < getChildCount() && getChild(k + 1).getElement().getEndOffset() == getChild(k + 1).getElement().getStartOffset(); k++);
        } 
        j = Math.max(j, 0);
        for (int m = j; m <= k && isValid(); m++)
          getChild(m).update(param1DocumentEvent); 
      } 
    }
  }
  
  protected abstract class HTMLAccessibleContext extends AccessibleContext implements Accessible, AccessibleComponent {
    protected AccessibleHTML.ElementInfo elementInfo;
    
    public HTMLAccessibleContext(AccessibleHTML.ElementInfo param1ElementInfo) { this.elementInfo = param1ElementInfo; }
    
    public AccessibleContext getAccessibleContext() { return this; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = new AccessibleStateSet();
      JEditorPane jEditorPane = AccessibleHTML.this.getTextComponent();
      if (jEditorPane.isEnabled())
        accessibleStateSet.add(AccessibleState.ENABLED); 
      if (jEditorPane instanceof JTextComponent && ((JTextComponent)jEditorPane).isEditable()) {
        accessibleStateSet.add(AccessibleState.EDITABLE);
        accessibleStateSet.add(AccessibleState.FOCUSABLE);
      } 
      if (jEditorPane.isVisible())
        accessibleStateSet.add(AccessibleState.VISIBLE); 
      if (jEditorPane.isShowing())
        accessibleStateSet.add(AccessibleState.SHOWING); 
      return accessibleStateSet;
    }
    
    public int getAccessibleIndexInParent() { return this.elementInfo.getIndexInParent(); }
    
    public int getAccessibleChildrenCount() { return this.elementInfo.getChildCount(); }
    
    public Accessible getAccessibleChild(int param1Int) {
      AccessibleHTML.ElementInfo elementInfo1 = this.elementInfo.getChild(param1Int);
      return (elementInfo1 != null && elementInfo1 instanceof Accessible) ? (Accessible)elementInfo1 : null;
    }
    
    public Locale getLocale() throws IllegalComponentStateException { return AccessibleHTML.this.editor.getLocale(); }
    
    public AccessibleComponent getAccessibleComponent() { return this; }
    
    public Color getBackground() { return AccessibleHTML.this.getTextComponent().getBackground(); }
    
    public void setBackground(Color param1Color) { AccessibleHTML.this.getTextComponent().setBackground(param1Color); }
    
    public Color getForeground() { return AccessibleHTML.this.getTextComponent().getForeground(); }
    
    public void setForeground(Color param1Color) { AccessibleHTML.this.getTextComponent().setForeground(param1Color); }
    
    public Cursor getCursor() { return AccessibleHTML.this.getTextComponent().getCursor(); }
    
    public void setCursor(Cursor param1Cursor) { AccessibleHTML.this.getTextComponent().setCursor(param1Cursor); }
    
    public Font getFont() { return AccessibleHTML.this.getTextComponent().getFont(); }
    
    public void setFont(Font param1Font) { AccessibleHTML.this.getTextComponent().setFont(param1Font); }
    
    public FontMetrics getFontMetrics(Font param1Font) { return AccessibleHTML.this.getTextComponent().getFontMetrics(param1Font); }
    
    public boolean isEnabled() { return AccessibleHTML.this.getTextComponent().isEnabled(); }
    
    public void setEnabled(boolean param1Boolean) { AccessibleHTML.this.getTextComponent().setEnabled(param1Boolean); }
    
    public boolean isVisible() { return AccessibleHTML.this.getTextComponent().isVisible(); }
    
    public void setVisible(boolean param1Boolean) { AccessibleHTML.this.getTextComponent().setVisible(param1Boolean); }
    
    public boolean isShowing() { return AccessibleHTML.this.getTextComponent().isShowing(); }
    
    public boolean contains(Point param1Point) {
      Rectangle rectangle = getBounds();
      return (rectangle != null) ? rectangle.contains(param1Point.x, param1Point.y) : 0;
    }
    
    public Point getLocationOnScreen() {
      Point point = AccessibleHTML.this.getTextComponent().getLocationOnScreen();
      Rectangle rectangle = getBounds();
      return (rectangle != null) ? new Point(point.x + rectangle.x, point.y + rectangle.y) : null;
    }
    
    public Point getLocation() {
      Rectangle rectangle = getBounds();
      return (rectangle != null) ? new Point(rectangle.x, rectangle.y) : null;
    }
    
    public void setLocation(Point param1Point) {}
    
    public Rectangle getBounds() { return this.elementInfo.getBounds(); }
    
    public void setBounds(Rectangle param1Rectangle) {}
    
    public Dimension getSize() {
      Rectangle rectangle = getBounds();
      return (rectangle != null) ? new Dimension(rectangle.width, rectangle.height) : null;
    }
    
    public void setSize(Dimension param1Dimension) {
      JEditorPane jEditorPane = AccessibleHTML.this.getTextComponent();
      jEditorPane.setSize(param1Dimension);
    }
    
    public Accessible getAccessibleAt(Point param1Point) {
      AccessibleHTML.ElementInfo elementInfo1 = getElementInfoAt(AccessibleHTML.this.rootElementInfo, param1Point);
      return (elementInfo1 instanceof Accessible) ? (Accessible)elementInfo1 : null;
    }
    
    private AccessibleHTML.ElementInfo getElementInfoAt(AccessibleHTML.ElementInfo param1ElementInfo, Point param1Point) {
      if (param1ElementInfo.getBounds() == null)
        return null; 
      if (param1ElementInfo.getChildCount() == 0 && param1ElementInfo.getBounds().contains(param1Point))
        return param1ElementInfo; 
      if (param1ElementInfo instanceof AccessibleHTML.TableElementInfo) {
        AccessibleHTML.ElementInfo elementInfo1 = ((AccessibleHTML.TableElementInfo)param1ElementInfo).getCaptionInfo();
        if (elementInfo1 != null) {
          Rectangle rectangle = elementInfo1.getBounds();
          if (rectangle != null && rectangle.contains(param1Point))
            return elementInfo1; 
        } 
      } 
      for (byte b = 0; b < param1ElementInfo.getChildCount(); b++) {
        AccessibleHTML.ElementInfo elementInfo1 = param1ElementInfo.getChild(b);
        AccessibleHTML.ElementInfo elementInfo2 = getElementInfoAt(elementInfo1, param1Point);
        if (elementInfo2 != null)
          return elementInfo2; 
      } 
      return null;
    }
    
    public boolean isFocusTraversable() {
      JEditorPane jEditorPane = AccessibleHTML.this.getTextComponent();
      return (jEditorPane instanceof JTextComponent && ((JTextComponent)jEditorPane).isEditable());
    }
    
    public void requestFocus() {
      if (!isFocusTraversable())
        return; 
      JEditorPane jEditorPane = AccessibleHTML.this.getTextComponent();
      if (jEditorPane instanceof JTextComponent) {
        jEditorPane.requestFocusInWindow();
        try {
          if (this.elementInfo.validateIfNecessary()) {
            Element element = this.elementInfo.getElement();
            ((JTextComponent)jEditorPane).setCaretPosition(element.getStartOffset());
            AccessibleContext accessibleContext = AccessibleHTML.this.editor.getAccessibleContext();
            PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, "AccessibleState", null, AccessibleState.FOCUSED);
            accessibleContext.firePropertyChange("AccessibleState", null, propertyChangeEvent);
          } 
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
    }
    
    public void addFocusListener(FocusListener param1FocusListener) { AccessibleHTML.this.getTextComponent().addFocusListener(param1FocusListener); }
    
    public void removeFocusListener(FocusListener param1FocusListener) { AccessibleHTML.this.getTextComponent().removeFocusListener(param1FocusListener); }
  }
  
  private class IconElementInfo extends ElementInfo implements Accessible {
    private int width = -1;
    
    private int height = -1;
    
    private AccessibleContext accessibleContext;
    
    IconElementInfo(Element param1Element, AccessibleHTML.ElementInfo param1ElementInfo) { super(AccessibleHTML.this, param1Element, param1ElementInfo); }
    
    protected void invalidate(boolean param1Boolean) {
      super.invalidate(param1Boolean);
      this.width = this.height = -1;
    }
    
    private int getImageSize(Object param1Object) {
      if (validateIfNecessary()) {
        int i = getIntAttr(getAttributes(), param1Object, -1);
        if (i == -1) {
          View view = getView();
          i = 0;
          if (view instanceof ImageView) {
            Image image = ((ImageView)view).getImage();
            if (image != null)
              if (param1Object == HTML.Attribute.WIDTH) {
                i = image.getWidth(null);
              } else {
                i = image.getHeight(null);
              }  
          } 
        } 
        return i;
      } 
      return 0;
    }
    
    public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null)
        this.accessibleContext = new IconAccessibleContext(this); 
      return this.accessibleContext;
    }
    
    protected class IconAccessibleContext extends AccessibleHTML.HTMLAccessibleContext implements AccessibleIcon {
      public IconAccessibleContext(AccessibleHTML.ElementInfo param2ElementInfo) { super(AccessibleHTML.IconElementInfo.this.this$0, param2ElementInfo); }
      
      public String getAccessibleName() { return getAccessibleIconDescription(); }
      
      public String getAccessibleDescription() { return AccessibleHTML.IconElementInfo.this.this$0.editor.getContentType(); }
      
      public AccessibleRole getAccessibleRole() { return AccessibleRole.ICON; }
      
      public AccessibleIcon[] getAccessibleIcon() {
        AccessibleIcon[] arrayOfAccessibleIcon = new AccessibleIcon[1];
        arrayOfAccessibleIcon[0] = this;
        return arrayOfAccessibleIcon;
      }
      
      public String getAccessibleIconDescription() { return ((ImageView)AccessibleHTML.IconElementInfo.this.getView()).getAltText(); }
      
      public void setAccessibleIconDescription(String param2String) {}
      
      public int getAccessibleIconWidth() {
        if (AccessibleHTML.IconElementInfo.this.width == -1)
          AccessibleHTML.IconElementInfo.this.width = AccessibleHTML.IconElementInfo.this.getImageSize(HTML.Attribute.WIDTH); 
        return AccessibleHTML.IconElementInfo.this.width;
      }
      
      public int getAccessibleIconHeight() {
        if (AccessibleHTML.IconElementInfo.this.height == -1)
          AccessibleHTML.IconElementInfo.this.height = AccessibleHTML.IconElementInfo.this.getImageSize(HTML.Attribute.HEIGHT); 
        return AccessibleHTML.IconElementInfo.this.height;
      }
    }
  }
  
  private class PropertyChangeHandler implements PropertyChangeListener {
    private PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getPropertyName().equals("document"))
        AccessibleHTML.this.setDocument(AccessibleHTML.this.editor.getDocument()); 
    }
  }
  
  private class RootHTMLAccessibleContext extends HTMLAccessibleContext {
    public RootHTMLAccessibleContext(AccessibleHTML.ElementInfo param1ElementInfo) { super(AccessibleHTML.this, param1ElementInfo); }
    
    public String getAccessibleName() { return (AccessibleHTML.this.model != null) ? (String)AccessibleHTML.this.model.getProperty("title") : null; }
    
    public String getAccessibleDescription() { return AccessibleHTML.this.editor.getContentType(); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TEXT; }
  }
  
  private class TableElementInfo extends ElementInfo implements Accessible {
    protected AccessibleHTML.ElementInfo caption;
    
    private TableCellElementInfo[][] grid;
    
    private AccessibleContext accessibleContext;
    
    TableElementInfo(Element param1Element, AccessibleHTML.ElementInfo param1ElementInfo) { super(AccessibleHTML.this, param1Element, param1ElementInfo); }
    
    public AccessibleHTML.ElementInfo getCaptionInfo() { return this.caption; }
    
    protected void validate() {
      super.validate();
      updateGrid();
    }
    
    protected void loadChildren(Element param1Element) {
      for (byte b = 0; b < param1Element.getElementCount(); b++) {
        Element element = param1Element.getElement(b);
        AttributeSet attributeSet = element.getAttributes();
        if (attributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TR) {
          addChild(new TableRowElementInfo(element, this, b));
        } else if (attributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.CAPTION) {
          this.caption = AccessibleHTML.this.createElementInfo(element, this);
        } 
      } 
    }
    
    private void updateGrid() {
      int i = 0;
      int j = 0;
      byte b;
      for (b = 0; b < getChildCount(); b++) {
        TableRowElementInfo tableRowElementInfo = getRow(b);
        int m = 0;
        for (byte b1 = 0; b1 < i; b1++)
          m = Math.max(m, getRow(b - b1 - 1).getColumnCount(b1 + 2)); 
        i = Math.max(tableRowElementInfo.getRowCount(), i);
        i--;
        j = Math.max(j, tableRowElementInfo.getColumnCount() + m);
      } 
      int k = getChildCount() + i;
      this.grid = new TableCellElementInfo[k][];
      for (b = 0; b < k; b++)
        this.grid[b] = new TableCellElementInfo[j]; 
      for (b = 0; b < k; b++)
        getRow(b).updateGrid(b); 
    }
    
    public TableRowElementInfo getRow(int param1Int) { return (TableRowElementInfo)getChild(param1Int); }
    
    public TableCellElementInfo getCell(int param1Int1, int param1Int2) { return (validateIfNecessary() && param1Int1 < this.grid.length && param1Int2 < this.grid[0].length) ? this.grid[param1Int1][param1Int2] : null; }
    
    public int getRowExtentAt(int param1Int1, int param1Int2) {
      TableCellElementInfo tableCellElementInfo = getCell(param1Int1, param1Int2);
      if (tableCellElementInfo != null) {
        int i = tableCellElementInfo.getRowCount();
        int j;
        for (j = 1; param1Int1 - j >= 0 && this.grid[param1Int1 - j][param1Int2] == tableCellElementInfo; j++);
        return i - j + 1;
      } 
      return 0;
    }
    
    public int getColumnExtentAt(int param1Int1, int param1Int2) {
      TableCellElementInfo tableCellElementInfo = getCell(param1Int1, param1Int2);
      if (tableCellElementInfo != null) {
        int i = tableCellElementInfo.getColumnCount();
        int j;
        for (j = 1; param1Int2 - j >= 0 && this.grid[param1Int1][param1Int2 - j] == tableCellElementInfo; j++);
        return i - j + 1;
      } 
      return 0;
    }
    
    public int getRowCount() { return validateIfNecessary() ? this.grid.length : 0; }
    
    public int getColumnCount() { return (validateIfNecessary() && this.grid.length > 0) ? this.grid[0].length : 0; }
    
    public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null)
        this.accessibleContext = new TableAccessibleContext(this); 
      return this.accessibleContext;
    }
    
    public class TableAccessibleContext extends AccessibleHTML.HTMLAccessibleContext implements AccessibleTable {
      private AccessibleHeadersTable rowHeadersTable;
      
      public TableAccessibleContext(AccessibleHTML.ElementInfo param2ElementInfo) { super(AccessibleHTML.TableElementInfo.this.this$0, param2ElementInfo); }
      
      public String getAccessibleName() { return getAccessibleRole().toString(); }
      
      public String getAccessibleDescription() { return AccessibleHTML.TableElementInfo.this.this$0.editor.getContentType(); }
      
      public AccessibleRole getAccessibleRole() { return AccessibleRole.TABLE; }
      
      public int getAccessibleIndexInParent() { return this.elementInfo.getIndexInParent(); }
      
      public int getAccessibleChildrenCount() { return ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowCount() * ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnCount(); }
      
      public Accessible getAccessibleChild(int param2Int) {
        int i = ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowCount();
        int j = ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnCount();
        int k = param2Int / i;
        int m = param2Int % j;
        return (k < 0 || k >= i || m < 0 || m >= j) ? null : getAccessibleAt(k, m);
      }
      
      public AccessibleTable getAccessibleTable() { return this; }
      
      public Accessible getAccessibleCaption() {
        AccessibleHTML.ElementInfo elementInfo = AccessibleHTML.TableElementInfo.this.getCaptionInfo();
        return (elementInfo instanceof Accessible) ? (Accessible)AccessibleHTML.TableElementInfo.this.caption : null;
      }
      
      public void setAccessibleCaption(Accessible param2Accessible) {}
      
      public Accessible getAccessibleSummary() { return null; }
      
      public void setAccessibleSummary(Accessible param2Accessible) {}
      
      public int getAccessibleRowCount() { return ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowCount(); }
      
      public int getAccessibleColumnCount() { return ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnCount(); }
      
      public Accessible getAccessibleAt(int param2Int1, int param2Int2) {
        AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = AccessibleHTML.TableElementInfo.this.getCell(param2Int1, param2Int2);
        return (tableCellElementInfo != null) ? tableCellElementInfo.getAccessible() : null;
      }
      
      public int getAccessibleRowExtentAt(int param2Int1, int param2Int2) { return ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowExtentAt(param2Int1, param2Int2); }
      
      public int getAccessibleColumnExtentAt(int param2Int1, int param2Int2) { return ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnExtentAt(param2Int1, param2Int2); }
      
      public AccessibleTable getAccessibleRowHeader() { return this.rowHeadersTable; }
      
      public void setAccessibleRowHeader(AccessibleTable param2AccessibleTable) {}
      
      public AccessibleTable getAccessibleColumnHeader() { return null; }
      
      public void setAccessibleColumnHeader(AccessibleTable param2AccessibleTable) {}
      
      public Accessible getAccessibleRowDescription(int param2Int) { return null; }
      
      public void setAccessibleRowDescription(int param2Int, Accessible param2Accessible) {}
      
      public Accessible getAccessibleColumnDescription(int param2Int) { return null; }
      
      public void setAccessibleColumnDescription(int param2Int, Accessible param2Accessible) {}
      
      public boolean isAccessibleSelected(int param2Int1, int param2Int2) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          if (param2Int1 < 0 || param2Int1 >= getAccessibleRowCount() || param2Int2 < 0 || param2Int2 >= getAccessibleColumnCount())
            return false; 
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = AccessibleHTML.TableElementInfo.this.getCell(param2Int1, param2Int2);
          if (tableCellElementInfo != null) {
            Element element = tableCellElementInfo.getElement();
            int i = element.getStartOffset();
            int j = element.getEndOffset();
            return (i >= AccessibleHTML.TableElementInfo.this.this$0.editor.getSelectionStart() && j <= AccessibleHTML.TableElementInfo.this.this$0.editor.getSelectionEnd());
          } 
        } 
        return false;
      }
      
      public boolean isAccessibleRowSelected(int param2Int) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          if (param2Int < 0 || param2Int >= getAccessibleRowCount())
            return false; 
          int i = getAccessibleColumnCount();
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo1 = AccessibleHTML.TableElementInfo.this.getCell(param2Int, 0);
          if (tableCellElementInfo1 == null)
            return false; 
          int j = tableCellElementInfo1.getElement().getStartOffset();
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo2 = AccessibleHTML.TableElementInfo.this.getCell(param2Int, i - 1);
          if (tableCellElementInfo2 == null)
            return false; 
          int k = tableCellElementInfo2.getElement().getEndOffset();
          return (j >= AccessibleHTML.TableElementInfo.this.this$0.editor.getSelectionStart() && k <= AccessibleHTML.TableElementInfo.this.this$0.editor.getSelectionEnd());
        } 
        return false;
      }
      
      public boolean isAccessibleColumnSelected(int param2Int) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          if (param2Int < 0 || param2Int >= getAccessibleColumnCount())
            return false; 
          int i = getAccessibleRowCount();
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo1 = AccessibleHTML.TableElementInfo.this.getCell(0, param2Int);
          if (tableCellElementInfo1 == null)
            return false; 
          int j = tableCellElementInfo1.getElement().getStartOffset();
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo2 = AccessibleHTML.TableElementInfo.this.getCell(i - 1, param2Int);
          if (tableCellElementInfo2 == null)
            return false; 
          int k = tableCellElementInfo2.getElement().getEndOffset();
          return (j >= AccessibleHTML.TableElementInfo.this.this$0.editor.getSelectionStart() && k <= AccessibleHTML.TableElementInfo.this.this$0.editor.getSelectionEnd());
        } 
        return false;
      }
      
      public int[] getSelectedAccessibleRows() {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          int i = getAccessibleRowCount();
          Vector vector = new Vector();
          for (byte b1 = 0; b1 < i; b1++) {
            if (isAccessibleRowSelected(b1))
              vector.addElement(Integer.valueOf(b1)); 
          } 
          int[] arrayOfInt = new int[vector.size()];
          for (byte b2 = 0; b2 < arrayOfInt.length; b2++)
            arrayOfInt[b2] = ((Integer)vector.elementAt(b2)).intValue(); 
          return arrayOfInt;
        } 
        return new int[0];
      }
      
      public int[] getSelectedAccessibleColumns() {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          int i = getAccessibleRowCount();
          Vector vector = new Vector();
          for (byte b1 = 0; b1 < i; b1++) {
            if (isAccessibleColumnSelected(b1))
              vector.addElement(Integer.valueOf(b1)); 
          } 
          int[] arrayOfInt = new int[vector.size()];
          for (byte b2 = 0; b2 < arrayOfInt.length; b2++)
            arrayOfInt[b2] = ((Integer)vector.elementAt(b2)).intValue(); 
          return arrayOfInt;
        } 
        return new int[0];
      }
      
      public int getAccessibleRow(int param2Int) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          int i = getAccessibleColumnCount() * getAccessibleRowCount();
          return (param2Int >= i) ? -1 : (param2Int / getAccessibleColumnCount());
        } 
        return -1;
      }
      
      public int getAccessibleColumn(int param2Int) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          int i = getAccessibleColumnCount() * getAccessibleRowCount();
          return (param2Int >= i) ? -1 : (param2Int % getAccessibleColumnCount());
        } 
        return -1;
      }
      
      public int getAccessibleIndex(int param2Int1, int param2Int2) { return AccessibleHTML.TableElementInfo.this.validateIfNecessary() ? ((param2Int1 >= getAccessibleRowCount() || param2Int2 >= getAccessibleColumnCount()) ? -1 : (param2Int1 * getAccessibleColumnCount() + param2Int2)) : -1; }
      
      public String getAccessibleRowHeader(int param2Int) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = AccessibleHTML.TableElementInfo.this.getCell(param2Int, 0);
          if (tableCellElementInfo.isHeaderCell()) {
            View view = tableCellElementInfo.getView();
            if (view != null && AccessibleHTML.TableElementInfo.this.this$0.model != null)
              try {
                return AccessibleHTML.TableElementInfo.this.this$0.model.getText(view.getStartOffset(), view.getEndOffset() - view.getStartOffset());
              } catch (BadLocationException badLocationException) {
                return null;
              }  
          } 
        } 
        return null;
      }
      
      public String getAccessibleColumnHeader(int param2Int) {
        if (AccessibleHTML.TableElementInfo.this.validateIfNecessary()) {
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = AccessibleHTML.TableElementInfo.this.getCell(0, param2Int);
          if (tableCellElementInfo.isHeaderCell()) {
            View view = tableCellElementInfo.getView();
            if (view != null && AccessibleHTML.TableElementInfo.this.this$0.model != null)
              try {
                return AccessibleHTML.TableElementInfo.this.this$0.model.getText(view.getStartOffset(), view.getEndOffset() - view.getStartOffset());
              } catch (BadLocationException badLocationException) {
                return null;
              }  
          } 
        } 
        return null;
      }
      
      public void addRowHeader(AccessibleHTML.TableElementInfo.TableCellElementInfo param2TableCellElementInfo, int param2Int) {
        if (this.rowHeadersTable == null)
          this.rowHeadersTable = new AccessibleHeadersTable(); 
        this.rowHeadersTable.addHeader(param2TableCellElementInfo, param2Int);
      }
      
      protected class AccessibleHeadersTable implements AccessibleTable {
        private Hashtable<Integer, ArrayList<AccessibleHTML.TableElementInfo.TableCellElementInfo>> headers = new Hashtable();
        
        private int rowCount = 0;
        
        private int columnCount = 0;
        
        public void addHeader(AccessibleHTML.TableElementInfo.TableCellElementInfo param3TableCellElementInfo, int param3Int) {
          Integer integer = Integer.valueOf(param3Int);
          ArrayList arrayList = (ArrayList)this.headers.get(integer);
          if (arrayList == null) {
            arrayList = new ArrayList();
            this.headers.put(integer, arrayList);
          } 
          arrayList.add(param3TableCellElementInfo);
        }
        
        public Accessible getAccessibleCaption() { return null; }
        
        public void setAccessibleCaption(Accessible param3Accessible) {}
        
        public Accessible getAccessibleSummary() { return null; }
        
        public void setAccessibleSummary(Accessible param3Accessible) {}
        
        public int getAccessibleRowCount() { return this.rowCount; }
        
        public int getAccessibleColumnCount() { return this.columnCount; }
        
        private AccessibleHTML.TableElementInfo.TableCellElementInfo getElementInfoAt(int param3Int1, int param3Int2) {
          ArrayList arrayList = (ArrayList)this.headers.get(Integer.valueOf(param3Int1));
          return (arrayList != null) ? (AccessibleHTML.TableElementInfo.TableCellElementInfo)arrayList.get(param3Int2) : null;
        }
        
        public Accessible getAccessibleAt(int param3Int1, int param3Int2) {
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = getElementInfoAt(param3Int1, param3Int2);
          return (tableCellElementInfo instanceof Accessible) ? (Accessible)tableCellElementInfo : null;
        }
        
        public int getAccessibleRowExtentAt(int param3Int1, int param3Int2) {
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = getElementInfoAt(param3Int1, param3Int2);
          return (tableCellElementInfo != null) ? tableCellElementInfo.getRowCount() : 0;
        }
        
        public int getAccessibleColumnExtentAt(int param3Int1, int param3Int2) {
          AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = getElementInfoAt(param3Int1, param3Int2);
          return (tableCellElementInfo != null) ? tableCellElementInfo.getRowCount() : 0;
        }
        
        public AccessibleTable getAccessibleRowHeader() { return null; }
        
        public void setAccessibleRowHeader(AccessibleTable param3AccessibleTable) {}
        
        public AccessibleTable getAccessibleColumnHeader() { return null; }
        
        public void setAccessibleColumnHeader(AccessibleTable param3AccessibleTable) {}
        
        public Accessible getAccessibleRowDescription(int param3Int) { return null; }
        
        public void setAccessibleRowDescription(int param3Int, Accessible param3Accessible) {}
        
        public Accessible getAccessibleColumnDescription(int param3Int) { return null; }
        
        public void setAccessibleColumnDescription(int param3Int, Accessible param3Accessible) {}
        
        public boolean isAccessibleSelected(int param3Int1, int param3Int2) { return false; }
        
        public boolean isAccessibleRowSelected(int param3Int) { return false; }
        
        public boolean isAccessibleColumnSelected(int param3Int) { return false; }
        
        public int[] getSelectedAccessibleRows() { return new int[0]; }
        
        public int[] getSelectedAccessibleColumns() { return new int[0]; }
      }
    }
    
    private class TableCellElementInfo extends AccessibleHTML.ElementInfo {
      private Accessible accessible;
      
      private boolean isHeaderCell = false;
      
      TableCellElementInfo(Element param2Element, AccessibleHTML.ElementInfo param2ElementInfo) { super(AccessibleHTML.TableElementInfo.this.this$0, param2Element, param2ElementInfo); }
      
      TableCellElementInfo(Element param2Element, AccessibleHTML.ElementInfo param2ElementInfo, boolean param2Boolean) { super(AccessibleHTML.TableElementInfo.this.this$0, param2Element, param2ElementInfo); }
      
      public boolean isHeaderCell() { return this.isHeaderCell; }
      
      public Accessible getAccessible() {
        this.accessible = null;
        getAccessible(this);
        return this.accessible;
      }
      
      private void getAccessible(AccessibleHTML.ElementInfo param2ElementInfo) {
        if (param2ElementInfo instanceof Accessible) {
          this.accessible = (Accessible)param2ElementInfo;
        } else {
          for (byte b = 0; b < param2ElementInfo.getChildCount(); b++)
            getAccessible(param2ElementInfo.getChild(b)); 
        } 
      }
      
      public int getRowCount() { return validateIfNecessary() ? Math.max(1, getIntAttr(getAttributes(), HTML.Attribute.ROWSPAN, 1)) : 0; }
      
      public int getColumnCount() { return validateIfNecessary() ? Math.max(1, getIntAttr(getAttributes(), HTML.Attribute.COLSPAN, 1)) : 0; }
      
      protected void invalidate(boolean param2Boolean) {
        super.invalidate(param2Boolean);
        getParent().invalidate(true);
      }
    }
    
    private class TableRowElementInfo extends AccessibleHTML.ElementInfo {
      private AccessibleHTML.TableElementInfo parent;
      
      private int rowNumber;
      
      TableRowElementInfo(Element param2Element, AccessibleHTML.TableElementInfo param2TableElementInfo1, int param2Int) {
        super(AccessibleHTML.TableElementInfo.this.this$0, param2Element, param2TableElementInfo1);
        this.parent = param2TableElementInfo1;
        this.rowNumber = param2Int;
      }
      
      protected void loadChildren(Element param2Element) {
        for (byte b = 0; b < param2Element.getElementCount(); b++) {
          AttributeSet attributeSet = param2Element.getElement(b).getAttributes();
          if (attributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TH) {
            AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = new AccessibleHTML.TableElementInfo.TableCellElementInfo(AccessibleHTML.TableElementInfo.this, param2Element.getElement(b), this, true);
            addChild(tableCellElementInfo);
            AccessibleTable accessibleTable = this.parent.getAccessibleContext().getAccessibleTable();
            AccessibleHTML.TableElementInfo.TableAccessibleContext tableAccessibleContext = (AccessibleHTML.TableElementInfo.TableAccessibleContext)accessibleTable;
            tableAccessibleContext.addRowHeader(tableCellElementInfo, this.rowNumber);
          } else if (attributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TD) {
            addChild(new AccessibleHTML.TableElementInfo.TableCellElementInfo(AccessibleHTML.TableElementInfo.this, param2Element.getElement(b), this, false));
          } 
        } 
      }
      
      public int getRowCount() {
        int i = 1;
        if (validateIfNecessary())
          for (byte b = 0; b < getChildCount(); b++) {
            AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(b);
            if (tableCellElementInfo.validateIfNecessary())
              i = Math.max(i, tableCellElementInfo.getRowCount()); 
          }  
        return i;
      }
      
      public int getColumnCount() {
        int i = 0;
        if (validateIfNecessary())
          for (byte b = 0; b < getChildCount(); b++) {
            AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(b);
            if (tableCellElementInfo.validateIfNecessary())
              i += tableCellElementInfo.getColumnCount(); 
          }  
        return i;
      }
      
      protected void invalidate(boolean param2Boolean) {
        super.invalidate(param2Boolean);
        getParent().invalidate(true);
      }
      
      private void updateGrid(int param2Int) {
        if (validateIfNecessary()) {
          boolean bool = false;
          while (!bool) {
            for (byte b1 = 0; b1 < AccessibleHTML.TableElementInfo.this.grid[param2Int].length; b1++) {
              if (AccessibleHTML.TableElementInfo.this.grid[param2Int][b1] == null) {
                bool = true;
                break;
              } 
            } 
            if (!bool)
              param2Int++; 
          } 
          int i = 0;
          for (byte b = 0; b < getChildCount(); b++) {
            AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(b);
            while (AccessibleHTML.TableElementInfo.this.grid[param2Int][i] != null)
              i++; 
            for (int j = tableCellElementInfo.getRowCount() - 1; j >= 0; j--) {
              for (int k = tableCellElementInfo.getColumnCount() - 1; k >= 0; k--)
                AccessibleHTML.TableElementInfo.this.grid[param2Int + j][i + k] = tableCellElementInfo; 
            } 
            i += tableCellElementInfo.getColumnCount();
          } 
        } 
      }
      
      private int getColumnCount(int param2Int) {
        if (validateIfNecessary()) {
          int i = 0;
          for (byte b = 0; b < getChildCount(); b++) {
            AccessibleHTML.TableElementInfo.TableCellElementInfo tableCellElementInfo = (AccessibleHTML.TableElementInfo.TableCellElementInfo)getChild(b);
            if (tableCellElementInfo.getRowCount() >= param2Int)
              i += tableCellElementInfo.getColumnCount(); 
          } 
          return i;
        } 
        return 0;
      }
    }
  }
  
  class TextElementInfo extends ElementInfo implements Accessible {
    private AccessibleContext accessibleContext;
    
    TextElementInfo(Element param1Element, AccessibleHTML.ElementInfo param1ElementInfo) { super(AccessibleHTML.this, param1Element, param1ElementInfo); }
    
    public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null)
        this.accessibleContext = new TextAccessibleContext(this); 
      return this.accessibleContext;
    }
    
    public class TextAccessibleContext extends AccessibleHTML.HTMLAccessibleContext implements AccessibleText {
      public TextAccessibleContext(AccessibleHTML.ElementInfo param2ElementInfo) { super(AccessibleHTML.TextElementInfo.this.this$0, param2ElementInfo); }
      
      public AccessibleText getAccessibleText() { return this; }
      
      public String getAccessibleName() { return (AccessibleHTML.TextElementInfo.this.this$0.model != null) ? (String)AccessibleHTML.TextElementInfo.this.this$0.model.getProperty("title") : null; }
      
      public String getAccessibleDescription() { return AccessibleHTML.TextElementInfo.this.this$0.editor.getContentType(); }
      
      public AccessibleRole getAccessibleRole() { return AccessibleRole.TEXT; }
      
      public int getIndexAtPoint(Point param2Point) {
        View view = AccessibleHTML.TextElementInfo.this.getView();
        return (view != null) ? view.viewToModel(param2Point.x, param2Point.y, getBounds()) : -1;
      }
      
      public Rectangle getCharacterBounds(int param2Int) {
        try {
          return AccessibleHTML.TextElementInfo.this.this$0.editor.getUI().modelToView(AccessibleHTML.TextElementInfo.this.this$0.editor, param2Int);
        } catch (BadLocationException badLocationException) {
          return null;
        } 
      }
      
      public int getCharCount() {
        if (AccessibleHTML.TextElementInfo.this.validateIfNecessary()) {
          Element element = this.elementInfo.getElement();
          return element.getEndOffset() - element.getStartOffset();
        } 
        return 0;
      }
      
      public int getCaretPosition() {
        View view = AccessibleHTML.TextElementInfo.this.getView();
        if (view == null)
          return -1; 
        Container container = view.getContainer();
        return (container == null) ? -1 : ((container instanceof JTextComponent) ? ((JTextComponent)container).getCaretPosition() : -1);
      }
      
      public String getAtIndex(int param2Int1, int param2Int2) { return getAtIndex(param2Int1, param2Int2, 0); }
      
      public String getAfterIndex(int param2Int1, int param2Int2) { return getAtIndex(param2Int1, param2Int2, 1); }
      
      public String getBeforeIndex(int param2Int1, int param2Int2) { return getAtIndex(param2Int1, param2Int2, -1); }
      
      private String getAtIndex(int param2Int1, int param2Int2, int param2Int3) {
        if (AccessibleHTML.TextElementInfo.this.this$0.model instanceof AbstractDocument)
          ((AbstractDocument)AccessibleHTML.TextElementInfo.this.this$0.model).readLock(); 
        try {
          IndexedSegment indexedSegment;
          if (param2Int2 < 0 || param2Int2 >= AccessibleHTML.TextElementInfo.this.this$0.model.getLength())
            return null; 
          switch (param2Int1) {
            case 1:
              if (param2Int2 + param2Int3 < AccessibleHTML.TextElementInfo.this.this$0.model.getLength() && param2Int2 + param2Int3 >= 0)
                return AccessibleHTML.TextElementInfo.this.this$0.model.getText(param2Int2 + param2Int3, 1); 
              break;
            case 2:
            case 3:
              indexedSegment = getSegmentAt(param2Int1, param2Int2);
              if (indexedSegment != null) {
                if (param2Int3 != 0) {
                  int i;
                  if (param2Int3 < 0) {
                    i = indexedSegment.modelOffset - 1;
                  } else {
                    i = indexedSegment.modelOffset + param2Int3 * indexedSegment.count;
                  } 
                  if (i >= 0 && i <= AccessibleHTML.TextElementInfo.this.this$0.model.getLength()) {
                    indexedSegment = getSegmentAt(param2Int1, i);
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
          if (AccessibleHTML.TextElementInfo.this.this$0.model instanceof AbstractDocument)
            ((AbstractDocument)AccessibleHTML.TextElementInfo.this.this$0.model).readUnlock(); 
        } 
        return null;
      }
      
      private Element getParagraphElement(int param2Int) {
        if (AccessibleHTML.TextElementInfo.this.this$0.model instanceof PlainDocument) {
          PlainDocument plainDocument = (PlainDocument)AccessibleHTML.TextElementInfo.this.this$0.model;
          return plainDocument.getParagraphElement(param2Int);
        } 
        if (AccessibleHTML.TextElementInfo.this.this$0.model instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)AccessibleHTML.TextElementInfo.this.this$0.model;
          return styledDocument.getParagraphElement(param2Int);
        } 
        Element element;
        for (element = AccessibleHTML.TextElementInfo.this.this$0.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(i))
          int i = element.getElementIndex(param2Int); 
        return (element == null) ? null : element.getParentElement();
      }
      
      private IndexedSegment getParagraphElementText(int param2Int) throws BadLocationException {
        Element element = getParagraphElement(param2Int);
        if (element != null) {
          IndexedSegment indexedSegment = new IndexedSegment(null);
          try {
            int i = element.getEndOffset() - element.getStartOffset();
            AccessibleHTML.TextElementInfo.this.this$0.model.getText(element.getStartOffset(), i, indexedSegment);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
          indexedSegment.modelOffset = element.getStartOffset();
          return indexedSegment;
        } 
        return null;
      }
      
      private IndexedSegment getSegmentAt(int param2Int1, int param2Int2) throws BadLocationException {
        BreakIterator breakIterator;
        IndexedSegment indexedSegment = getParagraphElementText(param2Int2);
        if (indexedSegment == null)
          return null; 
        switch (param2Int1) {
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
        int i = breakIterator.following(param2Int2 - indexedSegment.modelOffset + indexedSegment.offset);
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
      
      public AttributeSet getCharacterAttribute(int param2Int) {
        if (AccessibleHTML.TextElementInfo.this.this$0.model instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)AccessibleHTML.TextElementInfo.this.this$0.model;
          Element element = styledDocument.getCharacterElement(param2Int);
          if (element != null)
            return element.getAttributes(); 
        } 
        return null;
      }
      
      public int getSelectionStart() { return AccessibleHTML.TextElementInfo.this.this$0.editor.getSelectionStart(); }
      
      public int getSelectionEnd() { return AccessibleHTML.TextElementInfo.this.this$0.editor.getSelectionEnd(); }
      
      public String getSelectedText() { return AccessibleHTML.TextElementInfo.this.this$0.editor.getSelectedText(); }
      
      private String getText(int param2Int1, int param2Int2) {
        if (AccessibleHTML.TextElementInfo.this.this$0.model != null && AccessibleHTML.TextElementInfo.this.this$0.model instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)AccessibleHTML.TextElementInfo.this.this$0.model;
          return AccessibleHTML.TextElementInfo.this.this$0.model.getText(param2Int1, param2Int2);
        } 
        return null;
      }
      
      private class IndexedSegment extends Segment {
        public int modelOffset;
        
        private IndexedSegment() {}
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\AccessibleHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */