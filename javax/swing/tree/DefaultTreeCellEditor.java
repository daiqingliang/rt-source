package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class DefaultTreeCellEditor implements ActionListener, TreeCellEditor, TreeSelectionListener {
  protected TreeCellEditor realEditor;
  
  protected DefaultTreeCellRenderer renderer;
  
  protected Container editingContainer;
  
  protected Component editingComponent;
  
  protected boolean canEdit;
  
  protected int offset;
  
  protected JTree tree;
  
  protected TreePath lastPath;
  
  protected Timer timer;
  
  protected int lastRow;
  
  protected Color borderSelectionColor;
  
  protected Icon editingIcon;
  
  protected Font font;
  
  public DefaultTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer) { this(paramJTree, paramDefaultTreeCellRenderer, null); }
  
  public DefaultTreeCellEditor(JTree paramJTree, DefaultTreeCellRenderer paramDefaultTreeCellRenderer, TreeCellEditor paramTreeCellEditor) {
    this.renderer = paramDefaultTreeCellRenderer;
    this.realEditor = paramTreeCellEditor;
    if (this.realEditor == null)
      this.realEditor = createTreeCellEditor(); 
    this.editingContainer = createContainer();
    setTree(paramJTree);
    setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
  }
  
  public void setBorderSelectionColor(Color paramColor) { this.borderSelectionColor = paramColor; }
  
  public Color getBorderSelectionColor() { return this.borderSelectionColor; }
  
  public void setFont(Font paramFont) { this.font = paramFont; }
  
  public Font getFont() { return this.font; }
  
  public Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt) {
    setTree(paramJTree);
    this.lastRow = paramInt;
    determineOffset(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    if (this.editingComponent != null)
      this.editingContainer.remove(this.editingComponent); 
    this.editingComponent = this.realEditor.getTreeCellEditorComponent(paramJTree, paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt);
    TreePath treePath = paramJTree.getPathForRow(paramInt);
    this.canEdit = (this.lastPath != null && treePath != null && this.lastPath.equals(treePath));
    Font font1 = getFont();
    if (font1 == null) {
      if (this.renderer != null)
        font1 = this.renderer.getFont(); 
      if (font1 == null)
        font1 = paramJTree.getFont(); 
    } 
    this.editingContainer.setFont(font1);
    prepareForEditing();
    return this.editingContainer;
  }
  
  public Object getCellEditorValue() { return this.realEditor.getCellEditorValue(); }
  
  public boolean isCellEditable(EventObject paramEventObject) {
    boolean bool = false;
    boolean bool1 = false;
    if (paramEventObject != null && paramEventObject.getSource() instanceof JTree) {
      setTree((JTree)paramEventObject.getSource());
      if (paramEventObject instanceof MouseEvent) {
        TreePath treePath = this.tree.getPathForLocation(((MouseEvent)paramEventObject).getX(), ((MouseEvent)paramEventObject).getY());
        bool1 = (this.lastPath != null && treePath != null && this.lastPath.equals(treePath)) ? 1 : 0;
        if (treePath != null) {
          this.lastRow = this.tree.getRowForPath(treePath);
          Object object = treePath.getLastPathComponent();
          boolean bool2 = this.tree.isRowSelected(this.lastRow);
          boolean bool3 = this.tree.isExpanded(treePath);
          TreeModel treeModel = this.tree.getModel();
          boolean bool4 = treeModel.isLeaf(object);
          determineOffset(this.tree, object, bool2, bool3, bool4, this.lastRow);
        } 
      } 
    } 
    if (!this.realEditor.isCellEditable(paramEventObject))
      return false; 
    if (canEditImmediately(paramEventObject)) {
      bool = true;
    } else if (bool1 && shouldStartEditingTimer(paramEventObject)) {
      startEditingTimer();
    } else if (this.timer != null && this.timer.isRunning()) {
      this.timer.stop();
    } 
    if (bool)
      prepareForEditing(); 
    return bool;
  }
  
  public boolean shouldSelectCell(EventObject paramEventObject) { return this.realEditor.shouldSelectCell(paramEventObject); }
  
  public boolean stopCellEditing() {
    if (this.realEditor.stopCellEditing()) {
      cleanupAfterEditing();
      return true;
    } 
    return false;
  }
  
  public void cancelCellEditing() {
    this.realEditor.cancelCellEditing();
    cleanupAfterEditing();
  }
  
  public void addCellEditorListener(CellEditorListener paramCellEditorListener) { this.realEditor.addCellEditorListener(paramCellEditorListener); }
  
  public void removeCellEditorListener(CellEditorListener paramCellEditorListener) { this.realEditor.removeCellEditorListener(paramCellEditorListener); }
  
  public CellEditorListener[] getCellEditorListeners() { return ((DefaultCellEditor)this.realEditor).getCellEditorListeners(); }
  
  public void valueChanged(TreeSelectionEvent paramTreeSelectionEvent) {
    if (this.tree != null)
      if (this.tree.getSelectionCount() == 1) {
        this.lastPath = this.tree.getSelectionPath();
      } else {
        this.lastPath = null;
      }  
    if (this.timer != null)
      this.timer.stop(); 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (this.tree != null && this.lastPath != null)
      this.tree.startEditingAtPath(this.lastPath); 
  }
  
  protected void setTree(JTree paramJTree) {
    if (this.tree != paramJTree) {
      if (this.tree != null)
        this.tree.removeTreeSelectionListener(this); 
      this.tree = paramJTree;
      if (this.tree != null)
        this.tree.addTreeSelectionListener(this); 
      if (this.timer != null)
        this.timer.stop(); 
    } 
  }
  
  protected boolean shouldStartEditingTimer(EventObject paramEventObject) {
    if (paramEventObject instanceof MouseEvent && SwingUtilities.isLeftMouseButton((MouseEvent)paramEventObject)) {
      MouseEvent mouseEvent = (MouseEvent)paramEventObject;
      return (mouseEvent.getClickCount() == 1 && inHitRegion(mouseEvent.getX(), mouseEvent.getY()));
    } 
    return false;
  }
  
  protected void startEditingTimer() {
    if (this.timer == null) {
      this.timer = new Timer(1200, this);
      this.timer.setRepeats(false);
    } 
    this.timer.start();
  }
  
  protected boolean canEditImmediately(EventObject paramEventObject) {
    if (paramEventObject instanceof MouseEvent && SwingUtilities.isLeftMouseButton((MouseEvent)paramEventObject)) {
      MouseEvent mouseEvent = (MouseEvent)paramEventObject;
      return (mouseEvent.getClickCount() > 2 && inHitRegion(mouseEvent.getX(), mouseEvent.getY()));
    } 
    return (paramEventObject == null);
  }
  
  protected boolean inHitRegion(int paramInt1, int paramInt2) {
    if (this.lastRow != -1 && this.tree != null) {
      Rectangle rectangle = this.tree.getRowBounds(this.lastRow);
      ComponentOrientation componentOrientation = this.tree.getComponentOrientation();
      if (componentOrientation.isLeftToRight()) {
        if (rectangle != null && paramInt1 <= rectangle.x + this.offset && this.offset < rectangle.width - 5)
          return false; 
      } else if (rectangle != null && (paramInt1 >= rectangle.x + rectangle.width - this.offset + 5 || paramInt1 <= rectangle.x + 5) && this.offset < rectangle.width - 5) {
        return false;
      } 
    } 
    return true;
  }
  
  protected void determineOffset(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt) {
    if (this.renderer != null) {
      if (paramBoolean3) {
        this.editingIcon = this.renderer.getLeafIcon();
      } else if (paramBoolean2) {
        this.editingIcon = this.renderer.getOpenIcon();
      } else {
        this.editingIcon = this.renderer.getClosedIcon();
      } 
      if (this.editingIcon != null) {
        this.offset = this.renderer.getIconTextGap() + this.editingIcon.getIconWidth();
      } else {
        this.offset = this.renderer.getIconTextGap();
      } 
    } else {
      this.editingIcon = null;
      this.offset = 0;
    } 
  }
  
  protected void prepareForEditing() {
    if (this.editingComponent != null)
      this.editingContainer.add(this.editingComponent); 
  }
  
  protected Container createContainer() { return new EditorContainer(); }
  
  protected TreeCellEditor createTreeCellEditor() {
    Border border = UIManager.getBorder("Tree.editorBorder");
    DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new DefaultTextField(this, border)) {
        public boolean shouldSelectCell(EventObject param1EventObject) { return super.shouldSelectCell(param1EventObject); }
      };
    defaultCellEditor.setClickCountToStart(1);
    return defaultCellEditor;
  }
  
  private void cleanupAfterEditing() {
    if (this.editingComponent != null)
      this.editingContainer.remove(this.editingComponent); 
    this.editingComponent = null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if (this.realEditor != null && this.realEditor instanceof java.io.Serializable) {
      vector.addElement("realEditor");
      vector.addElement(this.realEditor);
    } 
    paramObjectOutputStream.writeObject(vector);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Vector vector = (Vector)paramObjectInputStream.readObject();
    byte b = 0;
    int i = vector.size();
    if (b < i && vector.elementAt(b).equals("realEditor")) {
      this.realEditor = (TreeCellEditor)vector.elementAt(++b);
      b++;
    } 
  }
  
  public class DefaultTextField extends JTextField {
    protected Border border;
    
    public DefaultTextField(Border param1Border) { setBorder(param1Border); }
    
    public void setBorder(Border param1Border) {
      super.setBorder(param1Border);
      this.border = param1Border;
    }
    
    public Border getBorder() { return this.border; }
    
    public Font getFont() {
      Font font = super.getFont();
      if (font instanceof javax.swing.plaf.FontUIResource) {
        Container container = getParent();
        if (container != null && container.getFont() != null)
          font = container.getFont(); 
      } 
      return font;
    }
    
    public Dimension getPreferredSize() {
      Dimension dimension = super.getPreferredSize();
      if (DefaultTreeCellEditor.this.renderer != null && DefaultTreeCellEditor.this.getFont() == null) {
        Dimension dimension1 = DefaultTreeCellEditor.this.renderer.getPreferredSize();
        dimension.height = dimension1.height;
      } 
      return dimension;
    }
  }
  
  public class EditorContainer extends Container {
    public EditorContainer() { setLayout(null); }
    
    public void EditorContainer() { setLayout(null); }
    
    public void paint(Graphics param1Graphics) {
      int i = getWidth();
      int j = getHeight();
      if (DefaultTreeCellEditor.this.editingIcon != null) {
        int k = calculateIconY(DefaultTreeCellEditor.this.editingIcon);
        if (getComponentOrientation().isLeftToRight()) {
          DefaultTreeCellEditor.this.editingIcon.paintIcon(this, param1Graphics, 0, k);
        } else {
          DefaultTreeCellEditor.this.editingIcon.paintIcon(this, param1Graphics, i - DefaultTreeCellEditor.this.editingIcon.getIconWidth(), k);
        } 
      } 
      Color color = DefaultTreeCellEditor.this.getBorderSelectionColor();
      if (color != null) {
        param1Graphics.setColor(color);
        param1Graphics.drawRect(0, 0, i - 1, j - 1);
      } 
      super.paint(param1Graphics);
    }
    
    public void doLayout() {
      if (DefaultTreeCellEditor.this.editingComponent != null) {
        int i = getWidth();
        int j = getHeight();
        if (getComponentOrientation().isLeftToRight()) {
          DefaultTreeCellEditor.this.editingComponent.setBounds(DefaultTreeCellEditor.this.offset, 0, i - DefaultTreeCellEditor.this.offset, j);
        } else {
          DefaultTreeCellEditor.this.editingComponent.setBounds(0, 0, i - DefaultTreeCellEditor.this.offset, j);
        } 
      } 
    }
    
    private int calculateIconY(Icon param1Icon) {
      int i = param1Icon.getIconHeight();
      int j = DefaultTreeCellEditor.this.editingComponent.getFontMetrics(DefaultTreeCellEditor.this.editingComponent.getFont()).getHeight();
      int k = i / 2 - j / 2;
      int m = Math.min(0, k);
      int n = Math.max(i, k + j) - m;
      return getHeight() / 2 - m + n / 2;
    }
    
    public Dimension getPreferredSize() {
      if (DefaultTreeCellEditor.this.editingComponent != null) {
        Dimension dimension1 = DefaultTreeCellEditor.this.editingComponent.getPreferredSize();
        dimension1.width += DefaultTreeCellEditor.this.offset + 5;
        Dimension dimension2 = (DefaultTreeCellEditor.this.renderer != null) ? DefaultTreeCellEditor.this.renderer.getPreferredSize() : null;
        if (dimension2 != null)
          dimension1.height = Math.max(dimension1.height, dimension2.height); 
        if (DefaultTreeCellEditor.this.editingIcon != null)
          dimension1.height = Math.max(dimension1.height, DefaultTreeCellEditor.this.editingIcon.getIconHeight()); 
        dimension1.width = Math.max(dimension1.width, 100);
        return dimension1;
      } 
      return new Dimension(0, 0);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\DefaultTreeCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */