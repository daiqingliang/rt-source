package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.ConstructorProperties;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.TreeUI;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import sun.awt.AWTAccessor;
import sun.swing.SwingUtilities2;

public class JTree extends JComponent implements Scrollable, Accessible {
  private static final String uiClassID = "TreeUI";
  
  protected TreeModel treeModel;
  
  protected TreeSelectionModel selectionModel;
  
  protected boolean rootVisible;
  
  protected TreeCellRenderer cellRenderer;
  
  protected int rowHeight;
  
  private boolean rowHeightSet = false;
  
  private Hashtable<TreePath, Boolean> expandedState = new Hashtable();
  
  protected boolean showsRootHandles;
  
  private boolean showsRootHandlesSet = false;
  
  protected TreeSelectionRedirector selectionRedirector;
  
  protected TreeCellEditor cellEditor;
  
  protected boolean editable;
  
  protected boolean largeModel;
  
  protected int visibleRowCount;
  
  protected boolean invokesStopCellEditing;
  
  protected boolean scrollsOnExpand;
  
  private boolean scrollsOnExpandSet = false;
  
  protected int toggleClickCount = 2;
  
  protected TreeModelListener treeModelListener;
  
  private Stack<Stack<TreePath>> expandedStack = new Stack();
  
  private TreePath leadPath;
  
  private TreePath anchorPath;
  
  private boolean expandsSelectedPaths;
  
  private boolean settingUI;
  
  private boolean dragEnabled;
  
  private DropMode dropMode = DropMode.USE_SELECTION;
  
  private DropLocation dropLocation;
  
  private int expandRow = -1;
  
  private TreeTimer dropTimer;
  
  private TreeExpansionListener uiTreeExpansionListener;
  
  private static int TEMP_STACK_SIZE = 11;
  
  public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
  
  public static final String TREE_MODEL_PROPERTY = "model";
  
  public static final String ROOT_VISIBLE_PROPERTY = "rootVisible";
  
  public static final String SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
  
  public static final String ROW_HEIGHT_PROPERTY = "rowHeight";
  
  public static final String CELL_EDITOR_PROPERTY = "cellEditor";
  
  public static final String EDITABLE_PROPERTY = "editable";
  
  public static final String LARGE_MODEL_PROPERTY = "largeModel";
  
  public static final String SELECTION_MODEL_PROPERTY = "selectionModel";
  
  public static final String VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
  
  public static final String INVOKES_STOP_CELL_EDITING_PROPERTY = "invokesStopCellEditing";
  
  public static final String SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
  
  public static final String TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";
  
  public static final String LEAD_SELECTION_PATH_PROPERTY = "leadSelectionPath";
  
  public static final String ANCHOR_SELECTION_PATH_PROPERTY = "anchorSelectionPath";
  
  public static final String EXPANDS_SELECTED_PATHS_PROPERTY = "expandsSelectedPaths";
  
  protected static TreeModel getDefaultTreeModel() {
    DefaultMutableTreeNode defaultMutableTreeNode1 = new DefaultMutableTreeNode("JTree");
    DefaultMutableTreeNode defaultMutableTreeNode2 = new DefaultMutableTreeNode("colors");
    defaultMutableTreeNode1.add(defaultMutableTreeNode2);
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("blue"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("violet"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("red"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("yellow"));
    defaultMutableTreeNode2 = new DefaultMutableTreeNode("sports");
    defaultMutableTreeNode1.add(defaultMutableTreeNode2);
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("basketball"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("soccer"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("football"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("hockey"));
    defaultMutableTreeNode2 = new DefaultMutableTreeNode("food");
    defaultMutableTreeNode1.add(defaultMutableTreeNode2);
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("hot dogs"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("pizza"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("ravioli"));
    defaultMutableTreeNode2.add(new DefaultMutableTreeNode("bananas"));
    return new DefaultTreeModel(defaultMutableTreeNode1);
  }
  
  protected static TreeModel createTreeModel(Object paramObject) {
    DynamicUtilTreeNode dynamicUtilTreeNode;
    if (paramObject instanceof Object[] || paramObject instanceof Hashtable || paramObject instanceof Vector) {
      dynamicUtilTreeNode = new DefaultMutableTreeNode("root");
      DynamicUtilTreeNode.createChildren(dynamicUtilTreeNode, paramObject);
    } else {
      dynamicUtilTreeNode = new DynamicUtilTreeNode("root", paramObject);
    } 
    return new DefaultTreeModel(dynamicUtilTreeNode, false);
  }
  
  public JTree() { this(getDefaultTreeModel()); }
  
  public JTree(Object[] paramArrayOfObject) {
    this(createTreeModel(paramArrayOfObject));
    setRootVisible(false);
    setShowsRootHandles(true);
    expandRoot();
  }
  
  public JTree(Vector<?> paramVector) {
    this(createTreeModel(paramVector));
    setRootVisible(false);
    setShowsRootHandles(true);
    expandRoot();
  }
  
  public JTree(Hashtable<?, ?> paramHashtable) {
    this(createTreeModel(paramHashtable));
    setRootVisible(false);
    setShowsRootHandles(true);
    expandRoot();
  }
  
  public JTree(TreeNode paramTreeNode) { this(paramTreeNode, false); }
  
  public JTree(TreeNode paramTreeNode, boolean paramBoolean) { this(new DefaultTreeModel(paramTreeNode, paramBoolean)); }
  
  @ConstructorProperties({"model"})
  public JTree(TreeModel paramTreeModel) {
    setLayout(null);
    this.rowHeight = 16;
    this.visibleRowCount = 20;
    this.rootVisible = true;
    this.selectionModel = new DefaultTreeSelectionModel();
    this.cellRenderer = null;
    this.scrollsOnExpand = true;
    setOpaque(true);
    this.expandsSelectedPaths = true;
    updateUI();
    setModel(paramTreeModel);
  }
  
  public TreeUI getUI() { return (TreeUI)this.ui; }
  
  public void setUI(TreeUI paramTreeUI) {
    if (this.ui != paramTreeUI) {
      this.settingUI = true;
      this.uiTreeExpansionListener = null;
      try {
        setUI(paramTreeUI);
      } finally {
        this.settingUI = false;
      } 
    } 
  }
  
  public void updateUI() {
    setUI((TreeUI)UIManager.getUI(this));
    SwingUtilities.updateRendererOrEditorUI(getCellRenderer());
    SwingUtilities.updateRendererOrEditorUI(getCellEditor());
  }
  
  public String getUIClassID() { return "TreeUI"; }
  
  public TreeCellRenderer getCellRenderer() { return this.cellRenderer; }
  
  public void setCellRenderer(TreeCellRenderer paramTreeCellRenderer) {
    TreeCellRenderer treeCellRenderer = this.cellRenderer;
    this.cellRenderer = paramTreeCellRenderer;
    firePropertyChange("cellRenderer", treeCellRenderer, this.cellRenderer);
    invalidate();
  }
  
  public void setEditable(boolean paramBoolean) {
    boolean bool = this.editable;
    this.editable = paramBoolean;
    firePropertyChange("editable", bool, paramBoolean);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.EDITABLE : null, paramBoolean ? AccessibleState.EDITABLE : null); 
  }
  
  public boolean isEditable() { return this.editable; }
  
  public void setCellEditor(TreeCellEditor paramTreeCellEditor) {
    TreeCellEditor treeCellEditor = this.cellEditor;
    this.cellEditor = paramTreeCellEditor;
    firePropertyChange("cellEditor", treeCellEditor, paramTreeCellEditor);
    invalidate();
  }
  
  public TreeCellEditor getCellEditor() { return this.cellEditor; }
  
  public TreeModel getModel() { return this.treeModel; }
  
  public void setModel(TreeModel paramTreeModel) {
    clearSelection();
    TreeModel treeModel1 = this.treeModel;
    if (this.treeModel != null && this.treeModelListener != null)
      this.treeModel.removeTreeModelListener(this.treeModelListener); 
    if (this.accessibleContext != null) {
      if (this.treeModel != null)
        this.treeModel.removeTreeModelListener((TreeModelListener)this.accessibleContext); 
      if (paramTreeModel != null)
        paramTreeModel.addTreeModelListener((TreeModelListener)this.accessibleContext); 
    } 
    this.treeModel = paramTreeModel;
    clearToggledPaths();
    if (this.treeModel != null) {
      if (this.treeModelListener == null)
        this.treeModelListener = createTreeModelListener(); 
      if (this.treeModelListener != null)
        this.treeModel.addTreeModelListener(this.treeModelListener); 
      Object object = this.treeModel.getRoot();
      if (object != null && !this.treeModel.isLeaf(object))
        this.expandedState.put(new TreePath(object), Boolean.TRUE); 
    } 
    firePropertyChange("model", treeModel1, this.treeModel);
    invalidate();
  }
  
  public boolean isRootVisible() { return this.rootVisible; }
  
  public void setRootVisible(boolean paramBoolean) {
    boolean bool = this.rootVisible;
    this.rootVisible = paramBoolean;
    firePropertyChange("rootVisible", bool, this.rootVisible);
    if (this.accessibleContext != null)
      ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
  }
  
  public void setShowsRootHandles(boolean paramBoolean) {
    boolean bool = this.showsRootHandles;
    TreeModel treeModel1 = getModel();
    this.showsRootHandles = paramBoolean;
    this.showsRootHandlesSet = true;
    firePropertyChange("showsRootHandles", bool, this.showsRootHandles);
    if (this.accessibleContext != null)
      ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
    invalidate();
  }
  
  public boolean getShowsRootHandles() { return this.showsRootHandles; }
  
  public void setRowHeight(int paramInt) {
    int i = this.rowHeight;
    this.rowHeight = paramInt;
    this.rowHeightSet = true;
    firePropertyChange("rowHeight", i, this.rowHeight);
    invalidate();
  }
  
  public int getRowHeight() { return this.rowHeight; }
  
  public boolean isFixedRowHeight() { return (this.rowHeight > 0); }
  
  public void setLargeModel(boolean paramBoolean) {
    boolean bool = this.largeModel;
    this.largeModel = paramBoolean;
    firePropertyChange("largeModel", bool, paramBoolean);
  }
  
  public boolean isLargeModel() { return this.largeModel; }
  
  public void setInvokesStopCellEditing(boolean paramBoolean) {
    boolean bool = this.invokesStopCellEditing;
    this.invokesStopCellEditing = paramBoolean;
    firePropertyChange("invokesStopCellEditing", bool, paramBoolean);
  }
  
  public boolean getInvokesStopCellEditing() { return this.invokesStopCellEditing; }
  
  public void setScrollsOnExpand(boolean paramBoolean) {
    boolean bool = this.scrollsOnExpand;
    this.scrollsOnExpand = paramBoolean;
    this.scrollsOnExpandSet = true;
    firePropertyChange("scrollsOnExpand", bool, paramBoolean);
  }
  
  public boolean getScrollsOnExpand() { return this.scrollsOnExpand; }
  
  public void setToggleClickCount(int paramInt) {
    int i = this.toggleClickCount;
    this.toggleClickCount = paramInt;
    firePropertyChange("toggleClickCount", i, paramInt);
  }
  
  public int getToggleClickCount() { return this.toggleClickCount; }
  
  public void setExpandsSelectedPaths(boolean paramBoolean) {
    boolean bool = this.expandsSelectedPaths;
    this.expandsSelectedPaths = paramBoolean;
    firePropertyChange("expandsSelectedPaths", bool, paramBoolean);
  }
  
  public boolean getExpandsSelectedPaths() { return this.expandsSelectedPaths; }
  
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
        case ON:
        case INSERT:
        case ON_OR_INSERT:
          this.dropMode = paramDropMode;
          return;
      }  
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for tree");
  }
  
  public final DropMode getDropMode() { return this.dropMode; }
  
  DropLocation dropLocationForPoint(Point paramPoint) {
    SwingUtilities2.Section section;
    boolean bool1;
    TreePath treePath3;
    TreePath treePath2;
    DropLocation dropLocation1 = null;
    int i = getClosestRowForLocation(paramPoint.x, paramPoint.y);
    Rectangle rectangle = getRowBounds(i);
    TreeModel treeModel1 = getModel();
    Object object = (treeModel1 == null) ? null : treeModel1.getRoot();
    TreePath treePath1 = (object == null) ? null : new TreePath(object);
    boolean bool = (i == -1 || paramPoint.y < rectangle.y || paramPoint.y >= rectangle.y + rectangle.height) ? 1 : 0;
    switch (this.dropMode) {
      case USE_SELECTION:
      case ON:
        if (bool) {
          dropLocation1 = new DropLocation(paramPoint, null, -1, null);
          break;
        } 
        dropLocation1 = new DropLocation(paramPoint, getPathForRow(i), -1, null);
        break;
      case INSERT:
      case ON_OR_INSERT:
        if (i == -1) {
          if (object != null && !treeModel1.isLeaf(object) && isExpanded(treePath1)) {
            dropLocation1 = new DropLocation(paramPoint, treePath1, 0, null);
            break;
          } 
          dropLocation1 = new DropLocation(paramPoint, null, -1, null);
          break;
        } 
        bool1 = (this.dropMode == DropMode.ON_OR_INSERT || !treeModel1.isLeaf(getPathForRow(i).getLastPathComponent()));
        section = SwingUtilities2.liesInVertical(rectangle, paramPoint, bool1);
        if (section == SwingUtilities2.Section.LEADING) {
          treePath2 = getPathForRow(i);
          treePath3 = treePath2.getParentPath();
        } else if (section == SwingUtilities2.Section.TRAILING) {
          int j = i + 1;
          if (j >= getRowCount()) {
            if (treeModel1.isLeaf(object) || !isExpanded(treePath1)) {
              dropLocation1 = new DropLocation(paramPoint, null, -1, null);
              break;
            } 
            TreePath treePath = treePath1;
            j = treeModel1.getChildCount(object);
            dropLocation1 = new DropLocation(paramPoint, treePath, j, null);
            break;
          } 
          treePath2 = getPathForRow(j);
          treePath3 = treePath2.getParentPath();
        } else {
          assert bool1;
          dropLocation1 = new DropLocation(paramPoint, getPathForRow(i), -1, null);
          break;
        } 
        if (treePath3 != null) {
          dropLocation1 = new DropLocation(paramPoint, treePath3, treeModel1.getIndexOfChild(treePath3.getLastPathComponent(), treePath2.getLastPathComponent()), null);
          break;
        } 
        if (bool1 || !treeModel1.isLeaf(object)) {
          dropLocation1 = new DropLocation(paramPoint, treePath1, -1, null);
          break;
        } 
        dropLocation1 = new DropLocation(paramPoint, null, -1, null);
        break;
      default:
        assert false : "Unexpected drop mode";
        break;
    } 
    if (bool || i != this.expandRow)
      cancelDropTimer(); 
    if (!bool && i != this.expandRow && isCollapsed(i)) {
      this.expandRow = i;
      startDropTimer();
    } 
    return dropLocation1;
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean) {
    TreePath[][] arrayOfTreePath = null;
    DropLocation dropLocation1 = (DropLocation)paramDropLocation;
    if (this.dropMode == DropMode.USE_SELECTION)
      if (dropLocation1 == null) {
        if (!paramBoolean && paramObject != null) {
          setSelectionPaths((TreePath[][])paramObject[0]);
          setAnchorSelectionPath((TreePath[][])paramObject[1][0]);
          setLeadSelectionPath((TreePath[][])paramObject[1][1]);
        } 
      } else {
        if (this.dropLocation == null) {
          TreePath[] arrayOfTreePath1 = getSelectionPaths();
          if (arrayOfTreePath1 == null)
            arrayOfTreePath1 = new TreePath[0]; 
          arrayOfTreePath = new TreePath[][] { arrayOfTreePath1, { getAnchorSelectionPath(), getLeadSelectionPath() } };
        } else {
          arrayOfTreePath = paramObject;
        } 
        setSelectionPath(dropLocation1.getPath());
      }  
    DropLocation dropLocation2 = this.dropLocation;
    this.dropLocation = dropLocation1;
    firePropertyChange("dropLocation", dropLocation2, this.dropLocation);
    return arrayOfTreePath;
  }
  
  void dndDone() {
    cancelDropTimer();
    this.dropTimer = null;
  }
  
  public final DropLocation getDropLocation() { return this.dropLocation; }
  
  private void startDropTimer() {
    if (this.dropTimer == null)
      this.dropTimer = new TreeTimer(); 
    this.dropTimer.start();
  }
  
  private void cancelDropTimer() {
    if (this.dropTimer != null && this.dropTimer.isRunning()) {
      this.expandRow = -1;
      this.dropTimer.stop();
    } 
  }
  
  public boolean isPathEditable(TreePath paramTreePath) { return isEditable(); }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    String str = null;
    if (paramMouseEvent != null) {
      Point point = paramMouseEvent.getPoint();
      int i = getRowForLocation(point.x, point.y);
      TreeCellRenderer treeCellRenderer = getCellRenderer();
      if (i != -1 && treeCellRenderer != null) {
        TreePath treePath = getPathForRow(i);
        Object object = treePath.getLastPathComponent();
        Component component = treeCellRenderer.getTreeCellRendererComponent(this, object, isRowSelected(i), isExpanded(i), getModel().isLeaf(object), i, true);
        if (component instanceof JComponent) {
          Rectangle rectangle = getPathBounds(treePath);
          point.translate(-rectangle.x, -rectangle.y);
          MouseEvent mouseEvent = new MouseEvent(component, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
          AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
          mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
          str = ((JComponent)component).getToolTipText(mouseEvent);
        } 
      } 
    } 
    if (str == null)
      str = getToolTipText(); 
    return str;
  }
  
  public String convertValueToText(Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4) {
    if (paramObject != null) {
      String str = paramObject.toString();
      if (str != null)
        return str; 
    } 
    return "";
  }
  
  public int getRowCount() {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.getRowCount(this) : 0;
  }
  
  public void setSelectionPath(TreePath paramTreePath) { getSelectionModel().setSelectionPath(paramTreePath); }
  
  public void setSelectionPaths(TreePath[] paramArrayOfTreePath) { getSelectionModel().setSelectionPaths(paramArrayOfTreePath); }
  
  public void setLeadSelectionPath(TreePath paramTreePath) {
    TreePath treePath = this.leadPath;
    this.leadPath = paramTreePath;
    firePropertyChange("leadSelectionPath", treePath, paramTreePath);
    if (this.accessibleContext != null)
      ((AccessibleJTree)this.accessibleContext).fireActiveDescendantPropertyChange(treePath, paramTreePath); 
  }
  
  public void setAnchorSelectionPath(TreePath paramTreePath) {
    TreePath treePath = this.anchorPath;
    this.anchorPath = paramTreePath;
    firePropertyChange("anchorSelectionPath", treePath, paramTreePath);
  }
  
  public void setSelectionRow(int paramInt) {
    int[] arrayOfInt = { paramInt };
    setSelectionRows(arrayOfInt);
  }
  
  public void setSelectionRows(int[] paramArrayOfInt) {
    TreeUI treeUI = getUI();
    if (treeUI != null && paramArrayOfInt != null) {
      int i = paramArrayOfInt.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (byte b = 0; b < i; b++)
        arrayOfTreePath[b] = treeUI.getPathForRow(this, paramArrayOfInt[b]); 
      setSelectionPaths(arrayOfTreePath);
    } 
  }
  
  public void addSelectionPath(TreePath paramTreePath) { getSelectionModel().addSelectionPath(paramTreePath); }
  
  public void addSelectionPaths(TreePath[] paramArrayOfTreePath) { getSelectionModel().addSelectionPaths(paramArrayOfTreePath); }
  
  public void addSelectionRow(int paramInt) {
    int[] arrayOfInt = { paramInt };
    addSelectionRows(arrayOfInt);
  }
  
  public void addSelectionRows(int[] paramArrayOfInt) {
    TreeUI treeUI = getUI();
    if (treeUI != null && paramArrayOfInt != null) {
      int i = paramArrayOfInt.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (byte b = 0; b < i; b++)
        arrayOfTreePath[b] = treeUI.getPathForRow(this, paramArrayOfInt[b]); 
      addSelectionPaths(arrayOfTreePath);
    } 
  }
  
  public Object getLastSelectedPathComponent() {
    TreePath treePath = getSelectionModel().getSelectionPath();
    return (treePath != null) ? treePath.getLastPathComponent() : null;
  }
  
  public TreePath getLeadSelectionPath() { return this.leadPath; }
  
  public TreePath getAnchorSelectionPath() { return this.anchorPath; }
  
  public TreePath getSelectionPath() { return getSelectionModel().getSelectionPath(); }
  
  public TreePath[] getSelectionPaths() {
    TreePath[] arrayOfTreePath = getSelectionModel().getSelectionPaths();
    return (arrayOfTreePath != null && arrayOfTreePath.length > 0) ? arrayOfTreePath : null;
  }
  
  public int[] getSelectionRows() { return getSelectionModel().getSelectionRows(); }
  
  public int getSelectionCount() { return this.selectionModel.getSelectionCount(); }
  
  public int getMinSelectionRow() { return getSelectionModel().getMinSelectionRow(); }
  
  public int getMaxSelectionRow() { return getSelectionModel().getMaxSelectionRow(); }
  
  public int getLeadSelectionRow() {
    TreePath treePath = getLeadSelectionPath();
    return (treePath != null) ? getRowForPath(treePath) : -1;
  }
  
  public boolean isPathSelected(TreePath paramTreePath) { return getSelectionModel().isPathSelected(paramTreePath); }
  
  public boolean isRowSelected(int paramInt) { return getSelectionModel().isRowSelected(paramInt); }
  
  public Enumeration<TreePath> getExpandedDescendants(TreePath paramTreePath) {
    if (!isExpanded(paramTreePath))
      return null; 
    Enumeration enumeration = this.expandedState.keys();
    Vector vector = null;
    if (enumeration != null)
      while (enumeration.hasMoreElements()) {
        TreePath treePath = (TreePath)enumeration.nextElement();
        Object object = this.expandedState.get(treePath);
        if (treePath != paramTreePath && object != null && ((Boolean)object).booleanValue() && paramTreePath.isDescendant(treePath) && isVisible(treePath)) {
          if (vector == null)
            vector = new Vector(); 
          vector.addElement(treePath);
        } 
      }  
    if (vector == null) {
      Set set = Collections.emptySet();
      return Collections.enumeration(set);
    } 
    return vector.elements();
  }
  
  public boolean hasBeenExpanded(TreePath paramTreePath) { return (paramTreePath != null && this.expandedState.get(paramTreePath) != null); }
  
  public boolean isExpanded(TreePath paramTreePath) {
    if (paramTreePath == null)
      return false; 
    do {
      Object object = this.expandedState.get(paramTreePath);
      if (object == null || !((Boolean)object).booleanValue())
        return false; 
    } while ((paramTreePath = paramTreePath.getParentPath()) != null);
    return true;
  }
  
  public boolean isExpanded(int paramInt) {
    TreeUI treeUI = getUI();
    if (treeUI != null) {
      TreePath treePath = treeUI.getPathForRow(this, paramInt);
      if (treePath != null) {
        Boolean bool = (Boolean)this.expandedState.get(treePath);
        return (bool != null && bool.booleanValue());
      } 
    } 
    return false;
  }
  
  public boolean isCollapsed(TreePath paramTreePath) { return !isExpanded(paramTreePath); }
  
  public boolean isCollapsed(int paramInt) { return !isExpanded(paramInt); }
  
  public void makeVisible(TreePath paramTreePath) {
    if (paramTreePath != null) {
      TreePath treePath = paramTreePath.getParentPath();
      if (treePath != null)
        expandPath(treePath); 
    } 
  }
  
  public boolean isVisible(TreePath paramTreePath) {
    if (paramTreePath != null) {
      TreePath treePath = paramTreePath.getParentPath();
      return (treePath != null) ? isExpanded(treePath) : 1;
    } 
    return false;
  }
  
  public Rectangle getPathBounds(TreePath paramTreePath) {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.getPathBounds(this, paramTreePath) : null;
  }
  
  public Rectangle getRowBounds(int paramInt) { return getPathBounds(getPathForRow(paramInt)); }
  
  public void scrollPathToVisible(TreePath paramTreePath) {
    if (paramTreePath != null) {
      makeVisible(paramTreePath);
      Rectangle rectangle = getPathBounds(paramTreePath);
      if (rectangle != null) {
        scrollRectToVisible(rectangle);
        if (this.accessibleContext != null)
          ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
      } 
    } 
  }
  
  public void scrollRowToVisible(int paramInt) { scrollPathToVisible(getPathForRow(paramInt)); }
  
  public TreePath getPathForRow(int paramInt) {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.getPathForRow(this, paramInt) : null;
  }
  
  public int getRowForPath(TreePath paramTreePath) {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.getRowForPath(this, paramTreePath) : -1;
  }
  
  public void expandPath(TreePath paramTreePath) {
    TreeModel treeModel1 = getModel();
    if (paramTreePath != null && treeModel1 != null && !treeModel1.isLeaf(paramTreePath.getLastPathComponent()))
      setExpandedState(paramTreePath, true); 
  }
  
  public void expandRow(int paramInt) { expandPath(getPathForRow(paramInt)); }
  
  public void collapsePath(TreePath paramTreePath) { setExpandedState(paramTreePath, false); }
  
  public void collapseRow(int paramInt) { collapsePath(getPathForRow(paramInt)); }
  
  public TreePath getPathForLocation(int paramInt1, int paramInt2) {
    TreePath treePath = getClosestPathForLocation(paramInt1, paramInt2);
    if (treePath != null) {
      Rectangle rectangle = getPathBounds(treePath);
      if (rectangle != null && paramInt1 >= rectangle.x && paramInt1 < rectangle.x + rectangle.width && paramInt2 >= rectangle.y && paramInt2 < rectangle.y + rectangle.height)
        return treePath; 
    } 
    return null;
  }
  
  public int getRowForLocation(int paramInt1, int paramInt2) { return getRowForPath(getPathForLocation(paramInt1, paramInt2)); }
  
  public TreePath getClosestPathForLocation(int paramInt1, int paramInt2) {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.getClosestPathForLocation(this, paramInt1, paramInt2) : null;
  }
  
  public int getClosestRowForLocation(int paramInt1, int paramInt2) { return getRowForPath(getClosestPathForLocation(paramInt1, paramInt2)); }
  
  public boolean isEditing() {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.isEditing(this) : 0;
  }
  
  public boolean stopEditing() {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.stopEditing(this) : 0;
  }
  
  public void cancelEditing() {
    TreeUI treeUI = getUI();
    if (treeUI != null)
      treeUI.cancelEditing(this); 
  }
  
  public void startEditingAtPath(TreePath paramTreePath) {
    TreeUI treeUI = getUI();
    if (treeUI != null)
      treeUI.startEditingAtPath(this, paramTreePath); 
  }
  
  public TreePath getEditingPath() {
    TreeUI treeUI = getUI();
    return (treeUI != null) ? treeUI.getEditingPath(this) : null;
  }
  
  public void setSelectionModel(TreeSelectionModel paramTreeSelectionModel) {
    if (paramTreeSelectionModel == null)
      paramTreeSelectionModel = EmptySelectionModel.sharedInstance(); 
    TreeSelectionModel treeSelectionModel = this.selectionModel;
    if (this.selectionModel != null && this.selectionRedirector != null)
      this.selectionModel.removeTreeSelectionListener(this.selectionRedirector); 
    if (this.accessibleContext != null) {
      this.selectionModel.removeTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
      paramTreeSelectionModel.addTreeSelectionListener((TreeSelectionListener)this.accessibleContext);
    } 
    this.selectionModel = paramTreeSelectionModel;
    if (this.selectionRedirector != null)
      this.selectionModel.addTreeSelectionListener(this.selectionRedirector); 
    firePropertyChange("selectionModel", treeSelectionModel, this.selectionModel);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true)); 
  }
  
  public TreeSelectionModel getSelectionModel() { return this.selectionModel; }
  
  protected TreePath[] getPathBetweenRows(int paramInt1, int paramInt2) {
    TreeUI treeUI = getUI();
    if (treeUI != null) {
      int i = getRowCount();
      if (i > 0 && (paramInt1 >= 0 || paramInt2 >= 0) && (paramInt1 < i || paramInt2 < i)) {
        paramInt1 = Math.min(i - 1, Math.max(paramInt1, 0));
        paramInt2 = Math.min(i - 1, Math.max(paramInt2, 0));
        int j = Math.min(paramInt1, paramInt2);
        int k = Math.max(paramInt1, paramInt2);
        TreePath[] arrayOfTreePath = new TreePath[k - j + 1];
        for (int m = j; m <= k; m++)
          arrayOfTreePath[m - j] = treeUI.getPathForRow(this, m); 
        return arrayOfTreePath;
      } 
    } 
    return new TreePath[0];
  }
  
  public void setSelectionInterval(int paramInt1, int paramInt2) {
    TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
    getSelectionModel().setSelectionPaths(arrayOfTreePath);
  }
  
  public void addSelectionInterval(int paramInt1, int paramInt2) {
    TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
    if (arrayOfTreePath != null && arrayOfTreePath.length > 0)
      getSelectionModel().addSelectionPaths(arrayOfTreePath); 
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2) {
    TreePath[] arrayOfTreePath = getPathBetweenRows(paramInt1, paramInt2);
    if (arrayOfTreePath != null && arrayOfTreePath.length > 0)
      getSelectionModel().removeSelectionPaths(arrayOfTreePath); 
  }
  
  public void removeSelectionPath(TreePath paramTreePath) { getSelectionModel().removeSelectionPath(paramTreePath); }
  
  public void removeSelectionPaths(TreePath[] paramArrayOfTreePath) { getSelectionModel().removeSelectionPaths(paramArrayOfTreePath); }
  
  public void removeSelectionRow(int paramInt) {
    int[] arrayOfInt = { paramInt };
    removeSelectionRows(arrayOfInt);
  }
  
  public void removeSelectionRows(int[] paramArrayOfInt) {
    TreeUI treeUI = getUI();
    if (treeUI != null && paramArrayOfInt != null) {
      int i = paramArrayOfInt.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (byte b = 0; b < i; b++)
        arrayOfTreePath[b] = treeUI.getPathForRow(this, paramArrayOfInt[b]); 
      removeSelectionPaths(arrayOfTreePath);
    } 
  }
  
  public void clearSelection() { getSelectionModel().clearSelection(); }
  
  public boolean isSelectionEmpty() { return getSelectionModel().isSelectionEmpty(); }
  
  public void addTreeExpansionListener(TreeExpansionListener paramTreeExpansionListener) {
    if (this.settingUI)
      this.uiTreeExpansionListener = paramTreeExpansionListener; 
    this.listenerList.add(TreeExpansionListener.class, paramTreeExpansionListener);
  }
  
  public void removeTreeExpansionListener(TreeExpansionListener paramTreeExpansionListener) {
    this.listenerList.remove(TreeExpansionListener.class, paramTreeExpansionListener);
    if (this.uiTreeExpansionListener == paramTreeExpansionListener)
      this.uiTreeExpansionListener = null; 
  }
  
  public TreeExpansionListener[] getTreeExpansionListeners() { return (TreeExpansionListener[])this.listenerList.getListeners(TreeExpansionListener.class); }
  
  public void addTreeWillExpandListener(TreeWillExpandListener paramTreeWillExpandListener) { this.listenerList.add(TreeWillExpandListener.class, paramTreeWillExpandListener); }
  
  public void removeTreeWillExpandListener(TreeWillExpandListener paramTreeWillExpandListener) { this.listenerList.remove(TreeWillExpandListener.class, paramTreeWillExpandListener); }
  
  public TreeWillExpandListener[] getTreeWillExpandListeners() { return (TreeWillExpandListener[])this.listenerList.getListeners(TreeWillExpandListener.class); }
  
  public void fireTreeExpanded(TreePath paramTreePath) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeExpansionEvent treeExpansionEvent = null;
    if (this.uiTreeExpansionListener != null) {
      treeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
      this.uiTreeExpansionListener.treeExpanded(treeExpansionEvent);
    } 
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeExpansionListener.class && arrayOfObject[i + true] != this.uiTreeExpansionListener) {
        if (treeExpansionEvent == null)
          treeExpansionEvent = new TreeExpansionEvent(this, paramTreePath); 
        ((TreeExpansionListener)arrayOfObject[i + 1]).treeExpanded(treeExpansionEvent);
      } 
    } 
  }
  
  public void fireTreeCollapsed(TreePath paramTreePath) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeExpansionEvent treeExpansionEvent = null;
    if (this.uiTreeExpansionListener != null) {
      treeExpansionEvent = new TreeExpansionEvent(this, paramTreePath);
      this.uiTreeExpansionListener.treeCollapsed(treeExpansionEvent);
    } 
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeExpansionListener.class && arrayOfObject[i + true] != this.uiTreeExpansionListener) {
        if (treeExpansionEvent == null)
          treeExpansionEvent = new TreeExpansionEvent(this, paramTreePath); 
        ((TreeExpansionListener)arrayOfObject[i + 1]).treeCollapsed(treeExpansionEvent);
      } 
    } 
  }
  
  public void fireTreeWillExpand(TreePath paramTreePath) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeExpansionEvent treeExpansionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeWillExpandListener.class) {
        if (treeExpansionEvent == null)
          treeExpansionEvent = new TreeExpansionEvent(this, paramTreePath); 
        ((TreeWillExpandListener)arrayOfObject[i + 1]).treeWillExpand(treeExpansionEvent);
      } 
    } 
  }
  
  public void fireTreeWillCollapse(TreePath paramTreePath) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    TreeExpansionEvent treeExpansionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeWillExpandListener.class) {
        if (treeExpansionEvent == null)
          treeExpansionEvent = new TreeExpansionEvent(this, paramTreePath); 
        ((TreeWillExpandListener)arrayOfObject[i + 1]).treeWillCollapse(treeExpansionEvent);
      } 
    } 
  }
  
  public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener) {
    this.listenerList.add(TreeSelectionListener.class, paramTreeSelectionListener);
    if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0 && this.selectionRedirector == null) {
      this.selectionRedirector = new TreeSelectionRedirector();
      this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
    } 
  }
  
  public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener) {
    this.listenerList.remove(TreeSelectionListener.class, paramTreeSelectionListener);
    if (this.listenerList.getListenerCount(TreeSelectionListener.class) == 0 && this.selectionRedirector != null) {
      this.selectionModel.removeTreeSelectionListener(this.selectionRedirector);
      this.selectionRedirector = null;
    } 
  }
  
  public TreeSelectionListener[] getTreeSelectionListeners() { return (TreeSelectionListener[])this.listenerList.getListeners(TreeSelectionListener.class); }
  
  protected void fireValueChanged(TreeSelectionEvent paramTreeSelectionEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeSelectionListener.class)
        ((TreeSelectionListener)arrayOfObject[i + 1]).valueChanged(paramTreeSelectionEvent); 
    } 
  }
  
  public void treeDidChange() {
    revalidate();
    repaint();
  }
  
  public void setVisibleRowCount(int paramInt) {
    int i = this.visibleRowCount;
    this.visibleRowCount = paramInt;
    firePropertyChange("visibleRowCount", i, this.visibleRowCount);
    invalidate();
    if (this.accessibleContext != null)
      ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
  }
  
  public int getVisibleRowCount() { return this.visibleRowCount; }
  
  private void expandRoot() {
    TreeModel treeModel1 = getModel();
    if (treeModel1 != null && treeModel1.getRoot() != null)
      expandPath(new TreePath(treeModel1.getRoot())); 
  }
  
  public TreePath getNextMatch(String paramString, int paramInt, Position.Bias paramBias) {
    int i = getRowCount();
    if (paramString == null)
      throw new IllegalArgumentException(); 
    if (paramInt < 0 || paramInt >= i)
      throw new IllegalArgumentException(); 
    paramString = paramString.toUpperCase();
    int j = (paramBias == Position.Bias.Forward) ? 1 : -1;
    int k = paramInt;
    do {
      TreePath treePath = getPathForRow(k);
      String str = convertValueToText(treePath.getLastPathComponent(), isRowSelected(k), isExpanded(k), true, k, false);
      if (str.toUpperCase().startsWith(paramString))
        return treePath; 
      k = (k + j + i) % i;
    } while (k != paramInt);
    return null;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Vector vector = new Vector();
    paramObjectOutputStream.defaultWriteObject();
    if (this.cellRenderer != null && this.cellRenderer instanceof Serializable) {
      vector.addElement("cellRenderer");
      vector.addElement(this.cellRenderer);
    } 
    if (this.cellEditor != null && this.cellEditor instanceof Serializable) {
      vector.addElement("cellEditor");
      vector.addElement(this.cellEditor);
    } 
    if (this.treeModel != null && this.treeModel instanceof Serializable) {
      vector.addElement("treeModel");
      vector.addElement(this.treeModel);
    } 
    if (this.selectionModel != null && this.selectionModel instanceof Serializable) {
      vector.addElement("selectionModel");
      vector.addElement(this.selectionModel);
    } 
    Object object = getArchivableExpandedState();
    if (object != null) {
      vector.addElement("expandedState");
      vector.addElement(object);
    } 
    paramObjectOutputStream.writeObject(vector);
    if (getUIClassID().equals("TreeUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.expandedState = new Hashtable();
    this.expandedStack = new Stack();
    Vector vector = (Vector)paramObjectInputStream.readObject();
    byte b = 0;
    int i = vector.size();
    if (b < i && vector.elementAt(b).equals("cellRenderer")) {
      this.cellRenderer = (TreeCellRenderer)vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("cellEditor")) {
      this.cellEditor = (TreeCellEditor)vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("treeModel")) {
      this.treeModel = (TreeModel)vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("selectionModel")) {
      this.selectionModel = (TreeSelectionModel)vector.elementAt(++b);
      b++;
    } 
    if (b < i && vector.elementAt(b).equals("expandedState")) {
      unarchiveExpandedState(vector.elementAt(++b));
      b++;
    } 
    if (this.listenerList.getListenerCount(TreeSelectionListener.class) != 0) {
      this.selectionRedirector = new TreeSelectionRedirector();
      this.selectionModel.addTreeSelectionListener(this.selectionRedirector);
    } 
    if (this.treeModel != null) {
      this.treeModelListener = createTreeModelListener();
      if (this.treeModelListener != null)
        this.treeModel.addTreeModelListener(this.treeModelListener); 
    } 
  }
  
  private Object getArchivableExpandedState() {
    TreeModel treeModel1 = getModel();
    if (treeModel1 != null) {
      Enumeration enumeration = this.expandedState.keys();
      if (enumeration != null) {
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
          Object object;
          TreePath treePath = (TreePath)enumeration.nextElement();
          try {
            object = getModelIndexsForPath(treePath);
          } catch (Error error) {
            object = null;
          } 
          if (object != null) {
            vector.addElement(object);
            vector.addElement(this.expandedState.get(treePath));
          } 
        } 
        return vector;
      } 
    } 
    return null;
  }
  
  private void unarchiveExpandedState(Object paramObject) {
    if (paramObject instanceof Vector) {
      Vector vector = (Vector)paramObject;
      for (int i = vector.size() - 1; i >= 0; i--) {
        Boolean bool = (Boolean)vector.elementAt(i--);
        try {
          TreePath treePath = getPathForIndexs((int[])vector.elementAt(i));
          if (treePath != null)
            this.expandedState.put(treePath, bool); 
        } catch (Error error) {}
      } 
    } 
  }
  
  private int[] getModelIndexsForPath(TreePath paramTreePath) {
    if (paramTreePath != null) {
      TreeModel treeModel1 = getModel();
      int i = paramTreePath.getPathCount();
      int[] arrayOfInt = new int[i - 1];
      Object object = treeModel1.getRoot();
      for (byte b = 1; b < i; b++) {
        arrayOfInt[b - true] = treeModel1.getIndexOfChild(object, paramTreePath.getPathComponent(b));
        object = paramTreePath.getPathComponent(b);
        if (arrayOfInt[b - 1] < 0)
          return null; 
      } 
      return arrayOfInt;
    } 
    return null;
  }
  
  private TreePath getPathForIndexs(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null)
      return null; 
    TreeModel treeModel1 = getModel();
    if (treeModel1 == null)
      return null; 
    int i = paramArrayOfInt.length;
    Object object = treeModel1.getRoot();
    if (object == null)
      return null; 
    TreePath treePath = new TreePath(object);
    for (byte b = 0; b < i; b++) {
      object = treeModel1.getChild(object, paramArrayOfInt[b]);
      if (object == null)
        return null; 
      treePath = treePath.pathByAddingChild(object);
    } 
    return treePath;
  }
  
  public Dimension getPreferredScrollableViewportSize() {
    int i = (getPreferredSize()).width;
    int j = getVisibleRowCount();
    int k = -1;
    if (isFixedRowHeight()) {
      k = j * getRowHeight();
    } else {
      TreeUI treeUI = getUI();
      if (treeUI != null && j > 0) {
        int m = treeUI.getRowCount(this);
        if (m >= j) {
          Rectangle rectangle = getRowBounds(j - 1);
          if (rectangle != null)
            k = rectangle.y + rectangle.height; 
        } else if (m > 0) {
          Rectangle rectangle = getRowBounds(0);
          if (rectangle != null)
            k = rectangle.height * j; 
        } 
      } 
      if (k == -1)
        k = 16 * j; 
    } 
    return new Dimension(i, k);
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (paramInt1 == 1) {
      int i = getClosestRowForLocation(0, paramRectangle.y);
      if (i != -1) {
        Rectangle rectangle = getRowBounds(i);
        if (rectangle.y != paramRectangle.y)
          return (paramInt2 < 0) ? Math.max(0, paramRectangle.y - rectangle.y) : (rectangle.y + rectangle.height - paramRectangle.y); 
        if (paramInt2 < 0) {
          if (i != 0) {
            rectangle = getRowBounds(i - 1);
            return rectangle.height;
          } 
        } else {
          return rectangle.height;
        } 
      } 
      return 0;
    } 
    return 4;
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) { return (paramInt1 == 1) ? paramRectangle.height : paramRectangle.width; }
  
  public boolean getScrollableTracksViewportWidth() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (container instanceof JViewport) ? ((container.getWidth() > (getPreferredSize()).width)) : false;
  }
  
  public boolean getScrollableTracksViewportHeight() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (container instanceof JViewport) ? ((container.getHeight() > (getPreferredSize()).height)) : false;
  }
  
  protected void setExpandedState(TreePath paramTreePath, boolean paramBoolean) {
    TreePath treePath;
    Stack stack;
    if (paramTreePath != null) {
      treePath = paramTreePath.getParentPath();
      if (this.expandedStack.size() == 0) {
        stack = new Stack();
      } else {
        stack = (Stack)this.expandedStack.pop();
      } 
    } else {
      return;
    } 
    while (treePath != null) {
      if (isExpanded(treePath)) {
        treePath = null;
        continue;
      } 
      stack.push(treePath);
      treePath = treePath.getParentPath();
    } 
    for (int i = stack.size() - 1; i >= 0; i--) {
      treePath = (TreePath)stack.pop();
      if (!isExpanded(treePath)) {
        try {
          fireTreeWillExpand(treePath);
        } catch (ExpandVetoException expandVetoException) {
          if (this.expandedStack.size() < TEMP_STACK_SIZE) {
            stack.removeAllElements();
            this.expandedStack.push(stack);
          } 
          return;
        } 
        this.expandedState.put(treePath, Boolean.TRUE);
        fireTreeExpanded(treePath);
        if (this.accessibleContext != null)
          ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
      } 
    } 
    if (this.expandedStack.size() < TEMP_STACK_SIZE) {
      stack.removeAllElements();
      this.expandedStack.push(stack);
    } 
    if (!paramBoolean) {
      Object object = this.expandedState.get(paramTreePath);
      if (object != null && ((Boolean)object).booleanValue()) {
        try {
          fireTreeWillCollapse(paramTreePath);
        } catch (ExpandVetoException expandVetoException) {
          return;
        } 
        this.expandedState.put(paramTreePath, Boolean.FALSE);
        fireTreeCollapsed(paramTreePath);
        if (removeDescendantSelectedPaths(paramTreePath, false) && !isPathSelected(paramTreePath))
          addSelectionPath(paramTreePath); 
        if (this.accessibleContext != null)
          ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
      } 
    } else {
      Object object = this.expandedState.get(paramTreePath);
      if (object == null || !((Boolean)object).booleanValue()) {
        try {
          fireTreeWillExpand(paramTreePath);
        } catch (ExpandVetoException expandVetoException) {
          return;
        } 
        this.expandedState.put(paramTreePath, Boolean.TRUE);
        fireTreeExpanded(paramTreePath);
        if (this.accessibleContext != null)
          ((AccessibleJTree)this.accessibleContext).fireVisibleDataPropertyChange(); 
      } 
    } 
  }
  
  protected Enumeration<TreePath> getDescendantToggledPaths(TreePath paramTreePath) {
    if (paramTreePath == null)
      return null; 
    Vector vector = new Vector();
    Enumeration enumeration = this.expandedState.keys();
    while (enumeration.hasMoreElements()) {
      TreePath treePath = (TreePath)enumeration.nextElement();
      if (paramTreePath.isDescendant(treePath))
        vector.addElement(treePath); 
    } 
    return vector.elements();
  }
  
  protected void removeDescendantToggledPaths(Enumeration<TreePath> paramEnumeration) {
    if (paramEnumeration != null)
      while (paramEnumeration.hasMoreElements()) {
        Enumeration enumeration = getDescendantToggledPaths((TreePath)paramEnumeration.nextElement());
        if (enumeration != null)
          while (enumeration.hasMoreElements())
            this.expandedState.remove(enumeration.nextElement());  
      }  
  }
  
  protected void clearToggledPaths() { this.expandedState.clear(); }
  
  protected TreeModelListener createTreeModelListener() { return new TreeModelHandler(); }
  
  protected boolean removeDescendantSelectedPaths(TreePath paramTreePath, boolean paramBoolean) {
    TreePath[] arrayOfTreePath = getDescendantSelectedPaths(paramTreePath, paramBoolean);
    if (arrayOfTreePath != null) {
      getSelectionModel().removeSelectionPaths(arrayOfTreePath);
      return true;
    } 
    return false;
  }
  
  private TreePath[] getDescendantSelectedPaths(TreePath paramTreePath, boolean paramBoolean) {
    TreeSelectionModel treeSelectionModel = getSelectionModel();
    TreePath[] arrayOfTreePath = (treeSelectionModel != null) ? treeSelectionModel.getSelectionPaths() : null;
    if (arrayOfTreePath != null) {
      boolean bool = false;
      for (int i = arrayOfTreePath.length - 1; i >= 0; i--) {
        if (arrayOfTreePath[i] != null && paramTreePath.isDescendant(arrayOfTreePath[i]) && (!paramTreePath.equals(arrayOfTreePath[i]) || paramBoolean)) {
          bool = true;
        } else {
          arrayOfTreePath[i] = null;
        } 
      } 
      if (!bool)
        arrayOfTreePath = null; 
      return arrayOfTreePath;
    } 
    return null;
  }
  
  void removeDescendantSelectedPaths(TreeModelEvent paramTreeModelEvent) {
    TreePath treePath = SwingUtilities2.getTreePath(paramTreeModelEvent, getModel());
    Object[] arrayOfObject = paramTreeModelEvent.getChildren();
    TreeSelectionModel treeSelectionModel = getSelectionModel();
    if (treeSelectionModel != null && treePath != null && arrayOfObject != null && arrayOfObject.length > 0)
      for (int i = arrayOfObject.length - 1; i >= 0; i--)
        removeDescendantSelectedPaths(treePath.pathByAddingChild(arrayOfObject[i]), true);  
  }
  
  void setUIProperty(String paramString, Object paramObject) {
    if (paramString == "rowHeight") {
      if (!this.rowHeightSet) {
        setRowHeight(((Number)paramObject).intValue());
        this.rowHeightSet = false;
      } 
    } else if (paramString == "scrollsOnExpand") {
      if (!this.scrollsOnExpandSet) {
        setScrollsOnExpand(((Boolean)paramObject).booleanValue());
        this.scrollsOnExpandSet = false;
      } 
    } else if (paramString == "showsRootHandles") {
      if (!this.showsRootHandlesSet) {
        setShowsRootHandles(((Boolean)paramObject).booleanValue());
        this.showsRootHandlesSet = false;
      } 
    } else {
      super.setUIProperty(paramString, paramObject);
    } 
  }
  
  protected String paramString() {
    String str1 = this.rootVisible ? "true" : "false";
    String str2 = this.showsRootHandles ? "true" : "false";
    String str3 = this.editable ? "true" : "false";
    String str4 = this.largeModel ? "true" : "false";
    String str5 = this.invokesStopCellEditing ? "true" : "false";
    String str6 = this.scrollsOnExpand ? "true" : "false";
    return super.paramString() + ",editable=" + str3 + ",invokesStopCellEditing=" + str5 + ",largeModel=" + str4 + ",rootVisible=" + str1 + ",rowHeight=" + this.rowHeight + ",scrollsOnExpand=" + str6 + ",showsRootHandles=" + str2 + ",toggleClickCount=" + this.toggleClickCount + ",visibleRowCount=" + this.visibleRowCount;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJTree(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJTree extends JComponent.AccessibleJComponent implements AccessibleSelection, TreeSelectionListener, TreeModelListener, TreeExpansionListener {
    TreePath leadSelectionPath;
    
    Accessible leadSelectionAccessible;
    
    public AccessibleJTree() {
      super(JTree.this);
      TreeModel treeModel = this$0.getModel();
      if (treeModel != null)
        treeModel.addTreeModelListener(this); 
      this$0.addTreeExpansionListener(this);
      this$0.addTreeSelectionListener(this);
      this.leadSelectionPath = this$0.getLeadSelectionPath();
      this.leadSelectionAccessible = (this.leadSelectionPath != null) ? new AccessibleJTreeNode(this$0, this.leadSelectionPath, this$0) : null;
    }
    
    public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) { firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true)); }
    
    public void fireVisibleDataPropertyChange() { firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true)); }
    
    public void treeNodesChanged(TreeModelEvent param1TreeModelEvent) { fireVisibleDataPropertyChange(); }
    
    public void treeNodesInserted(TreeModelEvent param1TreeModelEvent) { fireVisibleDataPropertyChange(); }
    
    public void treeNodesRemoved(TreeModelEvent param1TreeModelEvent) { fireVisibleDataPropertyChange(); }
    
    public void treeStructureChanged(TreeModelEvent param1TreeModelEvent) { fireVisibleDataPropertyChange(); }
    
    public void treeCollapsed(TreeExpansionEvent param1TreeExpansionEvent) {
      fireVisibleDataPropertyChange();
      TreePath treePath = param1TreeExpansionEvent.getPath();
      if (treePath != null) {
        AccessibleJTreeNode accessibleJTreeNode = new AccessibleJTreeNode(JTree.this, treePath, null);
        PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(accessibleJTreeNode, "AccessibleState", AccessibleState.EXPANDED, AccessibleState.COLLAPSED);
        firePropertyChange("AccessibleState", null, propertyChangeEvent);
      } 
    }
    
    public void treeExpanded(TreeExpansionEvent param1TreeExpansionEvent) {
      fireVisibleDataPropertyChange();
      TreePath treePath = param1TreeExpansionEvent.getPath();
      if (treePath != null) {
        AccessibleJTreeNode accessibleJTreeNode = new AccessibleJTreeNode(JTree.this, treePath, null);
        PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(accessibleJTreeNode, "AccessibleState", AccessibleState.COLLAPSED, AccessibleState.EXPANDED);
        firePropertyChange("AccessibleState", null, propertyChangeEvent);
      } 
    }
    
    void fireActiveDescendantPropertyChange(TreePath param1TreePath1, TreePath param1TreePath2) {
      if (param1TreePath1 != param1TreePath2) {
        AccessibleJTreeNode accessibleJTreeNode1 = (param1TreePath1 != null) ? new AccessibleJTreeNode(JTree.this, param1TreePath1, null) : null;
        AccessibleJTreeNode accessibleJTreeNode2 = (param1TreePath2 != null) ? new AccessibleJTreeNode(JTree.this, param1TreePath2, null) : null;
        firePropertyChange("AccessibleActiveDescendant", accessibleJTreeNode1, accessibleJTreeNode2);
      } 
    }
    
    private AccessibleContext getCurrentAccessibleContext() {
      Component component = getCurrentComponent();
      return (component instanceof Accessible) ? component.getAccessibleContext() : null;
    }
    
    private Component getCurrentComponent() {
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel == null)
        return null; 
      Object object = treeModel.getRoot();
      if (object == null)
        return null; 
      TreePath treePath = new TreePath(object);
      if (JTree.this.isVisible(treePath)) {
        TreeCellRenderer treeCellRenderer = JTree.this.getCellRenderer();
        TreeUI treeUI = JTree.this.getUI();
        if (treeUI != null) {
          int i = treeUI.getRowForPath(JTree.this, treePath);
          int j = JTree.this.getLeadSelectionRow();
          boolean bool1 = (JTree.this.isFocusOwner() && j == i);
          boolean bool2 = JTree.this.isPathSelected(treePath);
          boolean bool3 = JTree.this.isExpanded(treePath);
          return treeCellRenderer.getTreeCellRendererComponent(JTree.this, object, bool2, bool3, treeModel.isLeaf(object), i, bool1);
        } 
      } 
      return null;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TREE; }
    
    public Accessible getAccessibleAt(Point param1Point) {
      TreePath treePath = JTree.this.getClosestPathForLocation(param1Point.x, param1Point.y);
      return (treePath != null) ? new AccessibleJTreeNode(JTree.this, treePath, null) : null;
    }
    
    public int getAccessibleChildrenCount() {
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel == null)
        return 0; 
      if (JTree.this.isRootVisible())
        return 1; 
      Object object = treeModel.getRoot();
      return (object == null) ? 0 : treeModel.getChildCount(object);
    }
    
    public Accessible getAccessibleChild(int param1Int) {
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel == null)
        return null; 
      Object object1 = treeModel.getRoot();
      if (object1 == null)
        return null; 
      if (JTree.this.isRootVisible()) {
        if (param1Int == 0) {
          Object[] arrayOfObject1 = { object1 };
          if (arrayOfObject1[false] == null)
            return null; 
          TreePath treePath1 = new TreePath(arrayOfObject1);
          return new AccessibleJTreeNode(JTree.this, treePath1, JTree.this);
        } 
        return null;
      } 
      int i = treeModel.getChildCount(object1);
      if (param1Int < 0 || param1Int >= i)
        return null; 
      Object object2 = treeModel.getChild(object1, param1Int);
      if (object2 == null)
        return null; 
      Object[] arrayOfObject = { object1, object2 };
      TreePath treePath = new TreePath(arrayOfObject);
      return new AccessibleJTreeNode(JTree.this, treePath, JTree.this);
    }
    
    public int getAccessibleIndexInParent() { return super.getAccessibleIndexInParent(); }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public int getAccessibleSelectionCount() {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = JTree.this.treeModel.getRoot();
      if (arrayOfObject[false] == null)
        return 0; 
      TreePath treePath = new TreePath(arrayOfObject);
      return JTree.this.isPathSelected(treePath) ? 1 : 0;
    }
    
    public Accessible getAccessibleSelection(int param1Int) {
      if (param1Int == 0) {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = JTree.this.treeModel.getRoot();
        if (arrayOfObject[false] == null)
          return null; 
        TreePath treePath = new TreePath(arrayOfObject);
        if (JTree.this.isPathSelected(treePath))
          return new AccessibleJTreeNode(JTree.this, treePath, JTree.this); 
      } 
      return null;
    }
    
    public boolean isAccessibleChildSelected(int param1Int) {
      if (param1Int == 0) {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = JTree.this.treeModel.getRoot();
        if (arrayOfObject[false] == null)
          return false; 
        TreePath treePath = new TreePath(arrayOfObject);
        return JTree.this.isPathSelected(treePath);
      } 
      return false;
    }
    
    public void addAccessibleSelection(int param1Int) {
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel != null && param1Int == 0) {
        Object[] arrayOfObject = { treeModel.getRoot() };
        if (arrayOfObject[false] == null)
          return; 
        TreePath treePath = new TreePath(arrayOfObject);
        JTree.this.addSelectionPath(treePath);
      } 
    }
    
    public void removeAccessibleSelection(int param1Int) {
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel != null && param1Int == 0) {
        Object[] arrayOfObject = { treeModel.getRoot() };
        if (arrayOfObject[false] == null)
          return; 
        TreePath treePath = new TreePath(arrayOfObject);
        JTree.this.removeSelectionPath(treePath);
      } 
    }
    
    public void clearAccessibleSelection() {
      int i = getAccessibleChildrenCount();
      for (byte b = 0; b < i; b++)
        removeAccessibleSelection(b); 
    }
    
    public void selectAllAccessibleSelection() {
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel != null) {
        Object[] arrayOfObject = { treeModel.getRoot() };
        if (arrayOfObject[false] == null)
          return; 
        TreePath treePath = new TreePath(arrayOfObject);
        JTree.this.addSelectionPath(treePath);
      } 
    }
    
    protected class AccessibleJTreeNode extends AccessibleContext implements Accessible, AccessibleComponent, AccessibleSelection, AccessibleAction {
      private JTree tree = null;
      
      private TreeModel treeModel = null;
      
      private Object obj = null;
      
      private TreePath path = null;
      
      private Accessible accessibleParent = null;
      
      private int index = 0;
      
      private boolean isLeaf = false;
      
      public AccessibleJTreeNode(JTree param2JTree, TreePath param2TreePath, Accessible param2Accessible) {
        this.tree = param2JTree;
        this.path = param2TreePath;
        this.accessibleParent = param2Accessible;
        this.treeModel = param2JTree.getModel();
        this.obj = param2TreePath.getLastPathComponent();
        if (this.treeModel != null)
          this.isLeaf = this.treeModel.isLeaf(this.obj); 
      }
      
      private TreePath getChildTreePath(int param2Int) {
        if (param2Int < 0 || param2Int >= getAccessibleChildrenCount())
          return null; 
        Object object = this.treeModel.getChild(this.obj, param2Int);
        Object[] arrayOfObject1 = this.path.getPath();
        Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
        arrayOfObject2[arrayOfObject2.length - 1] = object;
        return new TreePath(arrayOfObject2);
      }
      
      public AccessibleContext getAccessibleContext() { return this; }
      
      private AccessibleContext getCurrentAccessibleContext() {
        Component component = getCurrentComponent();
        return (component instanceof Accessible) ? component.getAccessibleContext() : null;
      }
      
      private Component getCurrentComponent() {
        if (this.tree.isVisible(this.path)) {
          TreeCellRenderer treeCellRenderer = this.tree.getCellRenderer();
          if (treeCellRenderer == null)
            return null; 
          TreeUI treeUI = this.tree.getUI();
          if (treeUI != null) {
            int i = treeUI.getRowForPath(JTree.AccessibleJTree.this.this$0, this.path);
            boolean bool1 = this.tree.isPathSelected(this.path);
            boolean bool2 = this.tree.isExpanded(this.path);
            boolean bool3 = false;
            return treeCellRenderer.getTreeCellRendererComponent(this.tree, this.obj, bool1, bool2, this.isLeaf, i, bool3);
          } 
        } 
        return null;
      }
      
      public String getAccessibleName() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          String str = accessibleContext.getAccessibleName();
          return (str != null && str != "") ? accessibleContext.getAccessibleName() : null;
        } 
        return (this.accessibleName != null && this.accessibleName != "") ? this.accessibleName : (String)JTree.AccessibleJTree.this.this$0.getClientProperty("AccessibleName");
      }
      
      public void setAccessibleName(String param2String) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.setAccessibleName(param2String);
        } else {
          super.setAccessibleName(param2String);
        } 
      }
      
      public String getAccessibleDescription() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getAccessibleDescription() : super.getAccessibleDescription();
      }
      
      public void setAccessibleDescription(String param2String) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.setAccessibleDescription(param2String);
        } else {
          super.setAccessibleDescription(param2String);
        } 
      }
      
      public AccessibleRole getAccessibleRole() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getAccessibleRole() : AccessibleRole.UNKNOWN;
      }
      
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet accessibleStateSet;
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleStateSet = accessibleContext.getAccessibleStateSet();
        } else {
          accessibleStateSet = new AccessibleStateSet();
        } 
        if (isShowing()) {
          accessibleStateSet.add(AccessibleState.SHOWING);
        } else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
          accessibleStateSet.remove(AccessibleState.SHOWING);
        } 
        if (isVisible()) {
          accessibleStateSet.add(AccessibleState.VISIBLE);
        } else if (accessibleStateSet.contains(AccessibleState.VISIBLE)) {
          accessibleStateSet.remove(AccessibleState.VISIBLE);
        } 
        if (this.tree.isPathSelected(this.path))
          accessibleStateSet.add(AccessibleState.SELECTED); 
        if (this.path == JTree.AccessibleJTree.this.this$0.getLeadSelectionPath())
          accessibleStateSet.add(AccessibleState.ACTIVE); 
        if (!this.isLeaf)
          accessibleStateSet.add(AccessibleState.EXPANDABLE); 
        if (this.tree.isExpanded(this.path)) {
          accessibleStateSet.add(AccessibleState.EXPANDED);
        } else {
          accessibleStateSet.add(AccessibleState.COLLAPSED);
        } 
        if (this.tree.isEditable())
          accessibleStateSet.add(AccessibleState.EDITABLE); 
        return accessibleStateSet;
      }
      
      public Accessible getAccessibleParent() {
        if (this.accessibleParent == null) {
          Object[] arrayOfObject = this.path.getPath();
          if (arrayOfObject.length > 1) {
            Object object = arrayOfObject[arrayOfObject.length - 2];
            if (this.treeModel != null)
              this.index = this.treeModel.getIndexOfChild(object, this.obj); 
            Object[] arrayOfObject1 = new Object[arrayOfObject.length - 1];
            System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, arrayOfObject.length - 1);
            TreePath treePath = new TreePath(arrayOfObject1);
            this.accessibleParent = new AccessibleJTreeNode(JTree.AccessibleJTree.this, this.tree, treePath, null);
            setAccessibleParent(this.accessibleParent);
          } else if (this.treeModel != null) {
            this.accessibleParent = this.tree;
            this.index = 0;
            setAccessibleParent(this.accessibleParent);
          } 
        } 
        return this.accessibleParent;
      }
      
      public int getAccessibleIndexInParent() {
        if (this.accessibleParent == null)
          getAccessibleParent(); 
        Object[] arrayOfObject = this.path.getPath();
        if (arrayOfObject.length > 1) {
          Object object = arrayOfObject[arrayOfObject.length - 2];
          if (this.treeModel != null)
            this.index = this.treeModel.getIndexOfChild(object, this.obj); 
        } 
        return this.index;
      }
      
      public int getAccessibleChildrenCount() { return this.treeModel.getChildCount(this.obj); }
      
      public Accessible getAccessibleChild(int param2Int) {
        if (param2Int < 0 || param2Int >= getAccessibleChildrenCount())
          return null; 
        Object object = this.treeModel.getChild(this.obj, param2Int);
        Object[] arrayOfObject1 = this.path.getPath();
        Object[] arrayOfObject2 = new Object[arrayOfObject1.length + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
        arrayOfObject2[arrayOfObject2.length - 1] = object;
        TreePath treePath = new TreePath(arrayOfObject2);
        return new AccessibleJTreeNode(JTree.AccessibleJTree.this, JTree.AccessibleJTree.this.this$0, treePath, this);
      }
      
      public Locale getLocale() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getLocale() : this.tree.getLocale();
      }
      
      public void addPropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.addPropertyChangeListener(param2PropertyChangeListener);
        } else {
          super.addPropertyChangeListener(param2PropertyChangeListener);
        } 
      }
      
      public void removePropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.removePropertyChangeListener(param2PropertyChangeListener);
        } else {
          super.removePropertyChangeListener(param2PropertyChangeListener);
        } 
      }
      
      public AccessibleAction getAccessibleAction() { return this; }
      
      public AccessibleComponent getAccessibleComponent() { return this; }
      
      public AccessibleSelection getAccessibleSelection() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null && this.isLeaf) ? getCurrentAccessibleContext().getAccessibleSelection() : this;
      }
      
      public AccessibleText getAccessibleText() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? getCurrentAccessibleContext().getAccessibleText() : null;
      }
      
      public AccessibleValue getAccessibleValue() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? getCurrentAccessibleContext().getAccessibleValue() : null;
      }
      
      public Color getBackground() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getBackground(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getBackground() : null;
      }
      
      public void setBackground(Color param2Color) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setBackground(param2Color);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setBackground(param2Color); 
        } 
      }
      
      public Color getForeground() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getForeground(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getForeground() : null;
      }
      
      public void setForeground(Color param2Color) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setForeground(param2Color);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setForeground(param2Color); 
        } 
      }
      
      public Cursor getCursor() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getCursor(); 
        Component component = getCurrentComponent();
        if (component != null)
          return component.getCursor(); 
        Accessible accessible = getAccessibleParent();
        return (accessible instanceof AccessibleComponent) ? ((AccessibleComponent)accessible).getCursor() : null;
      }
      
      public void setCursor(Cursor param2Cursor) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setCursor(param2Cursor);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setCursor(param2Cursor); 
        } 
      }
      
      public Font getFont() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getFont(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getFont() : null;
      }
      
      public void setFont(Font param2Font) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setFont(param2Font);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setFont(param2Font); 
        } 
      }
      
      public FontMetrics getFontMetrics(Font param2Font) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getFontMetrics(param2Font); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getFontMetrics(param2Font) : null;
      }
      
      public boolean isEnabled() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).isEnabled(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isEnabled() : 0;
      }
      
      public void setEnabled(boolean param2Boolean) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setEnabled(param2Boolean);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setEnabled(param2Boolean); 
        } 
      }
      
      public boolean isVisible() {
        Rectangle rectangle1 = this.tree.getPathBounds(this.path);
        Rectangle rectangle2 = this.tree.getVisibleRect();
        return (rectangle1 != null && rectangle2 != null && rectangle2.intersects(rectangle1));
      }
      
      public void setVisible(boolean param2Boolean) {}
      
      public boolean isShowing() { return (this.tree.isShowing() && isVisible()); }
      
      public boolean contains(Point param2Point) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          Rectangle rectangle = ((AccessibleComponent)accessibleContext).getBounds();
          return rectangle.contains(param2Point);
        } 
        Component component = getCurrentComponent();
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          return rectangle.contains(param2Point);
        } 
        return getBounds().contains(param2Point);
      }
      
      public Point getLocationOnScreen() {
        if (this.tree != null) {
          Point point = this.tree.getLocationOnScreen();
          Rectangle rectangle = this.tree.getPathBounds(this.path);
          if (point != null && rectangle != null) {
            Point point1 = new Point(rectangle.x, rectangle.y);
            point1.translate(point.x, point.y);
            return point1;
          } 
          return null;
        } 
        return null;
      }
      
      protected Point getLocationInJTree() {
        Rectangle rectangle = this.tree.getPathBounds(this.path);
        return (rectangle != null) ? rectangle.getLocation() : null;
      }
      
      public Point getLocation() {
        Rectangle rectangle = getBounds();
        return (rectangle != null) ? rectangle.getLocation() : null;
      }
      
      public void setLocation(Point param2Point) {}
      
      public Rectangle getBounds() {
        Rectangle rectangle = this.tree.getPathBounds(this.path);
        Accessible accessible = getAccessibleParent();
        if (accessible != null && accessible instanceof AccessibleJTreeNode) {
          Point point = ((AccessibleJTreeNode)accessible).getLocationInJTree();
          if (point != null && rectangle != null) {
            rectangle.translate(-point.x, -point.y);
          } else {
            return null;
          } 
        } 
        return rectangle;
      }
      
      public void setBounds(Rectangle param2Rectangle) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setBounds(param2Rectangle);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setBounds(param2Rectangle); 
        } 
      }
      
      public Dimension getSize() { return getBounds().getSize(); }
      
      public void setSize(Dimension param2Dimension) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setSize(param2Dimension);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setSize(param2Dimension); 
        } 
      }
      
      public Accessible getAccessibleAt(Point param2Point) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext instanceof AccessibleComponent) ? ((AccessibleComponent)accessibleContext).getAccessibleAt(param2Point) : null;
      }
      
      public boolean isFocusTraversable() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).isFocusTraversable(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isFocusTraversable() : 0;
      }
      
      public void requestFocus() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).requestFocus();
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.requestFocus(); 
        } 
      }
      
      public void addFocusListener(FocusListener param2FocusListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).addFocusListener(param2FocusListener);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.addFocusListener(param2FocusListener); 
        } 
      }
      
      public void removeFocusListener(FocusListener param2FocusListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).removeFocusListener(param2FocusListener);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.removeFocusListener(param2FocusListener); 
        } 
      }
      
      public int getAccessibleSelectionCount() {
        byte b1 = 0;
        int i = getAccessibleChildrenCount();
        for (byte b2 = 0; b2 < i; b2++) {
          TreePath treePath = getChildTreePath(b2);
          if (this.tree.isPathSelected(treePath))
            b1++; 
        } 
        return b1;
      }
      
      public Accessible getAccessibleSelection(int param2Int) {
        int i = getAccessibleChildrenCount();
        if (param2Int < 0 || param2Int >= i)
          return null; 
        byte b1 = 0;
        for (byte b2 = 0; b2 < i && param2Int >= b1; b2++) {
          TreePath treePath = getChildTreePath(b2);
          if (this.tree.isPathSelected(treePath)) {
            if (b1 == param2Int)
              return new AccessibleJTreeNode(JTree.AccessibleJTree.this, this.tree, treePath, this); 
            b1++;
          } 
        } 
        return null;
      }
      
      public boolean isAccessibleChildSelected(int param2Int) {
        int i = getAccessibleChildrenCount();
        if (param2Int < 0 || param2Int >= i)
          return false; 
        TreePath treePath = getChildTreePath(param2Int);
        return this.tree.isPathSelected(treePath);
      }
      
      public void addAccessibleSelection(int param2Int) {
        TreeModel treeModel1 = JTree.AccessibleJTree.this.this$0.getModel();
        if (treeModel1 != null && param2Int >= 0 && param2Int < getAccessibleChildrenCount()) {
          TreePath treePath = getChildTreePath(param2Int);
          JTree.AccessibleJTree.this.this$0.addSelectionPath(treePath);
        } 
      }
      
      public void removeAccessibleSelection(int param2Int) {
        TreeModel treeModel1 = JTree.AccessibleJTree.this.this$0.getModel();
        if (treeModel1 != null && param2Int >= 0 && param2Int < getAccessibleChildrenCount()) {
          TreePath treePath = getChildTreePath(param2Int);
          JTree.AccessibleJTree.this.this$0.removeSelectionPath(treePath);
        } 
      }
      
      public void clearAccessibleSelection() {
        int i = getAccessibleChildrenCount();
        for (byte b = 0; b < i; b++)
          removeAccessibleSelection(b); 
      }
      
      public void selectAllAccessibleSelection() {
        TreeModel treeModel1 = JTree.AccessibleJTree.this.this$0.getModel();
        if (treeModel1 != null) {
          int i = getAccessibleChildrenCount();
          for (byte b = 0; b < i; b++) {
            TreePath treePath = getChildTreePath(b);
            JTree.AccessibleJTree.this.this$0.addSelectionPath(treePath);
          } 
        } 
      }
      
      public int getAccessibleActionCount() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
          if (accessibleAction != null)
            return accessibleAction.getAccessibleActionCount() + (this.isLeaf ? 0 : 1); 
        } 
        return this.isLeaf ? 0 : 1;
      }
      
      public String getAccessibleActionDescription(int param2Int) {
        if (param2Int < 0 || param2Int >= getAccessibleActionCount())
          return null; 
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (param2Int == 0)
          return AccessibleAction.TOGGLE_EXPAND; 
        if (accessibleContext != null) {
          AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
          if (accessibleAction != null)
            return accessibleAction.getAccessibleActionDescription(param2Int - 1); 
        } 
        return null;
      }
      
      public boolean doAccessibleAction(int param2Int) {
        if (param2Int < 0 || param2Int >= getAccessibleActionCount())
          return false; 
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (param2Int == 0) {
          if (JTree.AccessibleJTree.this.this$0.isExpanded(this.path)) {
            JTree.AccessibleJTree.this.this$0.collapsePath(this.path);
          } else {
            JTree.AccessibleJTree.this.this$0.expandPath(this.path);
          } 
          return true;
        } 
        if (accessibleContext != null) {
          AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
          if (accessibleAction != null)
            return accessibleAction.doAccessibleAction(param2Int - 1); 
        } 
        return false;
      }
    }
  }
  
  public static final class DropLocation extends TransferHandler.DropLocation {
    private final TreePath path;
    
    private final int index;
    
    private DropLocation(Point param1Point, TreePath param1TreePath, int param1Int) {
      super(param1Point);
      this.path = param1TreePath;
      this.index = param1Int;
    }
    
    public int getChildIndex() { return this.index; }
    
    public TreePath getPath() { return this.path; }
    
    public String toString() { return getClass().getName() + "[dropPoint=" + getDropPoint() + ",path=" + this.path + ",childIndex=" + this.index + "]"; }
  }
  
  public static class DynamicUtilTreeNode extends DefaultMutableTreeNode {
    protected boolean hasChildren;
    
    protected Object childValue;
    
    protected boolean loadedChildren = false;
    
    public static void createChildren(DefaultMutableTreeNode param1DefaultMutableTreeNode, Object param1Object) {
      if (param1Object instanceof Vector) {
        Vector vector = (Vector)param1Object;
        byte b = 0;
        int i = vector.size();
        while (b < i) {
          param1DefaultMutableTreeNode.add(new DynamicUtilTreeNode(vector.elementAt(b), vector.elementAt(b)));
          b++;
        } 
      } else if (param1Object instanceof Hashtable) {
        Hashtable hashtable = (Hashtable)param1Object;
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
          Object object = enumeration.nextElement();
          param1DefaultMutableTreeNode.add(new DynamicUtilTreeNode(object, hashtable.get(object)));
        } 
      } else if (param1Object instanceof Object[]) {
        Object[] arrayOfObject = (Object[])param1Object;
        byte b = 0;
        int i = arrayOfObject.length;
        while (b < i) {
          param1DefaultMutableTreeNode.add(new DynamicUtilTreeNode(arrayOfObject[b], arrayOfObject[b]));
          b++;
        } 
      } 
    }
    
    public DynamicUtilTreeNode(Object param1Object1, Object param1Object2) {
      super(param1Object1);
      this.childValue = param1Object2;
      if (param1Object2 != null) {
        if (param1Object2 instanceof Vector) {
          setAllowsChildren(true);
        } else if (param1Object2 instanceof Hashtable) {
          setAllowsChildren(true);
        } else if (param1Object2 instanceof Object[]) {
          setAllowsChildren(true);
        } else {
          setAllowsChildren(false);
        } 
      } else {
        setAllowsChildren(false);
      } 
    }
    
    public boolean isLeaf() { return !getAllowsChildren(); }
    
    public int getChildCount() {
      if (!this.loadedChildren)
        loadChildren(); 
      return super.getChildCount();
    }
    
    protected void loadChildren() {
      this.loadedChildren = true;
      createChildren(this, this.childValue);
    }
    
    public TreeNode getChildAt(int param1Int) {
      if (!this.loadedChildren)
        loadChildren(); 
      return super.getChildAt(param1Int);
    }
    
    public Enumeration children() {
      if (!this.loadedChildren)
        loadChildren(); 
      return super.children();
    }
  }
  
  protected static class EmptySelectionModel extends DefaultTreeSelectionModel {
    protected static final EmptySelectionModel sharedInstance = new EmptySelectionModel();
    
    public static EmptySelectionModel sharedInstance() { return sharedInstance; }
    
    public void setSelectionPaths(TreePath[] param1ArrayOfTreePath) {}
    
    public void addSelectionPaths(TreePath[] param1ArrayOfTreePath) {}
    
    public void removeSelectionPaths(TreePath[] param1ArrayOfTreePath) {}
    
    public void setSelectionMode(int param1Int) {}
    
    public void setRowMapper(RowMapper param1RowMapper) {}
    
    public void addTreeSelectionListener(TreeSelectionListener param1TreeSelectionListener) {}
    
    public void removeTreeSelectionListener(TreeSelectionListener param1TreeSelectionListener) {}
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
  }
  
  protected class TreeModelHandler implements TreeModelListener {
    public void treeNodesChanged(TreeModelEvent param1TreeModelEvent) {}
    
    public void treeNodesInserted(TreeModelEvent param1TreeModelEvent) {}
    
    public void treeStructureChanged(TreeModelEvent param1TreeModelEvent) {
      if (param1TreeModelEvent == null)
        return; 
      TreePath treePath = SwingUtilities2.getTreePath(param1TreeModelEvent, JTree.this.getModel());
      if (treePath == null)
        return; 
      if (treePath.getPathCount() == 1) {
        JTree.this.clearToggledPaths();
        Object object = JTree.this.treeModel.getRoot();
        if (object != null && !JTree.this.treeModel.isLeaf(object))
          JTree.this.expandedState.put(treePath, Boolean.TRUE); 
      } else if (JTree.this.expandedState.get(treePath) != null) {
        Vector vector = new Vector(1);
        boolean bool = JTree.this.isExpanded(treePath);
        vector.addElement(treePath);
        JTree.this.removeDescendantToggledPaths(vector.elements());
        if (bool) {
          TreeModel treeModel = JTree.this.getModel();
          if (treeModel == null || treeModel.isLeaf(treePath.getLastPathComponent())) {
            JTree.this.collapsePath(treePath);
          } else {
            JTree.this.expandedState.put(treePath, Boolean.TRUE);
          } 
        } 
      } 
      JTree.this.removeDescendantSelectedPaths(treePath, false);
    }
    
    public void treeNodesRemoved(TreeModelEvent param1TreeModelEvent) {
      if (param1TreeModelEvent == null)
        return; 
      TreePath treePath = SwingUtilities2.getTreePath(param1TreeModelEvent, JTree.this.getModel());
      Object[] arrayOfObject = param1TreeModelEvent.getChildren();
      if (arrayOfObject == null)
        return; 
      Vector vector = new Vector(Math.max(1, arrayOfObject.length));
      for (int i = arrayOfObject.length - 1; i >= 0; i--) {
        TreePath treePath1 = treePath.pathByAddingChild(arrayOfObject[i]);
        if (JTree.this.expandedState.get(treePath1) != null)
          vector.addElement(treePath1); 
      } 
      if (vector.size() > 0)
        JTree.this.removeDescendantToggledPaths(vector.elements()); 
      TreeModel treeModel = JTree.this.getModel();
      if (treeModel == null || treeModel.isLeaf(treePath.getLastPathComponent()))
        JTree.this.expandedState.remove(treePath); 
      JTree.this.removeDescendantSelectedPaths(param1TreeModelEvent);
    }
  }
  
  protected class TreeSelectionRedirector implements Serializable, TreeSelectionListener {
    public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) {
      TreeSelectionEvent treeSelectionEvent = (TreeSelectionEvent)param1TreeSelectionEvent.cloneWithSource(JTree.this);
      JTree.this.fireValueChanged(treeSelectionEvent);
    }
  }
  
  private class TreeTimer extends Timer {
    public TreeTimer() {
      super(2000, null);
      setRepeats(false);
    }
    
    public void fireActionPerformed(ActionEvent param1ActionEvent) { JTree.this.expandRow(JTree.this.expandRow); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */