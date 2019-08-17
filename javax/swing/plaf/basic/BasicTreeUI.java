package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.FixedHeightLayoutCache;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.VariableHeightLayoutCache;
import sun.awt.AWTAccessor;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTreeUI extends TreeUI {
  private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Tree.baselineComponent");
  
  private static final Actions SHARED_ACTION = new Actions();
  
  protected Icon collapsedIcon;
  
  protected Icon expandedIcon;
  
  private Color hashColor;
  
  protected int leftChildIndent;
  
  protected int rightChildIndent;
  
  protected int totalChildIndent;
  
  protected Dimension preferredMinSize;
  
  protected int lastSelectedRow;
  
  protected JTree tree;
  
  protected TreeCellRenderer currentCellRenderer;
  
  protected boolean createdRenderer;
  
  protected TreeCellEditor cellEditor;
  
  protected boolean createdCellEditor;
  
  protected boolean stopEditingInCompleteEditing;
  
  protected CellRendererPane rendererPane;
  
  protected Dimension preferredSize;
  
  protected boolean validCachedPreferredSize;
  
  protected AbstractLayoutCache treeState;
  
  protected Hashtable<TreePath, Boolean> drawingCache;
  
  protected boolean largeModel;
  
  protected AbstractLayoutCache.NodeDimensions nodeDimensions;
  
  protected TreeModel treeModel;
  
  protected TreeSelectionModel treeSelectionModel;
  
  protected int depthOffset;
  
  protected Component editingComponent;
  
  protected TreePath editingPath;
  
  protected int editingRow;
  
  protected boolean editorHasDifferentSize;
  
  private int leadRow;
  
  private boolean ignoreLAChange;
  
  private boolean leftToRight;
  
  private PropertyChangeListener propertyChangeListener;
  
  private PropertyChangeListener selectionModelPropertyChangeListener;
  
  private MouseListener mouseListener;
  
  private FocusListener focusListener;
  
  private KeyListener keyListener;
  
  private ComponentListener componentListener;
  
  private CellEditorListener cellEditorListener;
  
  private TreeSelectionListener treeSelectionListener;
  
  private TreeModelListener treeModelListener;
  
  private TreeExpansionListener treeExpansionListener;
  
  private boolean paintLines = true;
  
  private boolean lineTypeDashed;
  
  private long timeFactor = 1000L;
  
  private Handler handler;
  
  private MouseEvent releaseEvent;
  
  private static final TransferHandler defaultTransferHandler = new TreeTransferHandler();
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTreeUI(); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("selectPrevious"));
    paramLazyActionMap.put(new Actions("selectPreviousChangeLead"));
    paramLazyActionMap.put(new Actions("selectPreviousExtendSelection"));
    paramLazyActionMap.put(new Actions("selectNext"));
    paramLazyActionMap.put(new Actions("selectNextChangeLead"));
    paramLazyActionMap.put(new Actions("selectNextExtendSelection"));
    paramLazyActionMap.put(new Actions("selectChild"));
    paramLazyActionMap.put(new Actions("selectChildChangeLead"));
    paramLazyActionMap.put(new Actions("selectParent"));
    paramLazyActionMap.put(new Actions("selectParentChangeLead"));
    paramLazyActionMap.put(new Actions("scrollUpChangeSelection"));
    paramLazyActionMap.put(new Actions("scrollUpChangeLead"));
    paramLazyActionMap.put(new Actions("scrollUpExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollDownChangeSelection"));
    paramLazyActionMap.put(new Actions("scrollDownExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollDownChangeLead"));
    paramLazyActionMap.put(new Actions("selectFirst"));
    paramLazyActionMap.put(new Actions("selectFirstChangeLead"));
    paramLazyActionMap.put(new Actions("selectFirstExtendSelection"));
    paramLazyActionMap.put(new Actions("selectLast"));
    paramLazyActionMap.put(new Actions("selectLastChangeLead"));
    paramLazyActionMap.put(new Actions("selectLastExtendSelection"));
    paramLazyActionMap.put(new Actions("toggle"));
    paramLazyActionMap.put(new Actions("cancel"));
    paramLazyActionMap.put(new Actions("startEditing"));
    paramLazyActionMap.put(new Actions("selectAll"));
    paramLazyActionMap.put(new Actions("clearSelection"));
    paramLazyActionMap.put(new Actions("scrollLeft"));
    paramLazyActionMap.put(new Actions("scrollRight"));
    paramLazyActionMap.put(new Actions("scrollLeftExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollRightExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollRightChangeLead"));
    paramLazyActionMap.put(new Actions("scrollLeftChangeLead"));
    paramLazyActionMap.put(new Actions("expand"));
    paramLazyActionMap.put(new Actions("collapse"));
    paramLazyActionMap.put(new Actions("moveSelectionToParent"));
    paramLazyActionMap.put(new Actions("addToSelection"));
    paramLazyActionMap.put(new Actions("toggleAndAnchor"));
    paramLazyActionMap.put(new Actions("extendTo"));
    paramLazyActionMap.put(new Actions("moveSelectionTo"));
    paramLazyActionMap.put(TransferHandler.getCutAction());
    paramLazyActionMap.put(TransferHandler.getCopyAction());
    paramLazyActionMap.put(TransferHandler.getPasteAction());
  }
  
  protected Color getHashColor() { return this.hashColor; }
  
  protected void setHashColor(Color paramColor) { this.hashColor = paramColor; }
  
  public void setLeftChildIndent(int paramInt) {
    this.leftChildIndent = paramInt;
    this.totalChildIndent = this.leftChildIndent + this.rightChildIndent;
    if (this.treeState != null)
      this.treeState.invalidateSizes(); 
    updateSize();
  }
  
  public int getLeftChildIndent() { return this.leftChildIndent; }
  
  public void setRightChildIndent(int paramInt) {
    this.rightChildIndent = paramInt;
    this.totalChildIndent = this.leftChildIndent + this.rightChildIndent;
    if (this.treeState != null)
      this.treeState.invalidateSizes(); 
    updateSize();
  }
  
  public int getRightChildIndent() { return this.rightChildIndent; }
  
  public void setExpandedIcon(Icon paramIcon) { this.expandedIcon = paramIcon; }
  
  public Icon getExpandedIcon() { return this.expandedIcon; }
  
  public void setCollapsedIcon(Icon paramIcon) { this.collapsedIcon = paramIcon; }
  
  public Icon getCollapsedIcon() { return this.collapsedIcon; }
  
  protected void setLargeModel(boolean paramBoolean) {
    if (getRowHeight() < 1)
      paramBoolean = false; 
    if (this.largeModel != paramBoolean) {
      completeEditing();
      this.largeModel = paramBoolean;
      this.treeState = createLayoutCache();
      configureLayoutCache();
      updateLayoutCacheExpandedNodesIfNecessary();
      updateSize();
    } 
  }
  
  protected boolean isLargeModel() { return this.largeModel; }
  
  protected void setRowHeight(int paramInt) {
    completeEditing();
    if (this.treeState != null) {
      setLargeModel(this.tree.isLargeModel());
      this.treeState.setRowHeight(paramInt);
      updateSize();
    } 
  }
  
  protected int getRowHeight() { return (this.tree == null) ? -1 : this.tree.getRowHeight(); }
  
  protected void setCellRenderer(TreeCellRenderer paramTreeCellRenderer) {
    completeEditing();
    updateRenderer();
    if (this.treeState != null) {
      this.treeState.invalidateSizes();
      updateSize();
    } 
  }
  
  protected TreeCellRenderer getCellRenderer() { return this.currentCellRenderer; }
  
  protected void setModel(TreeModel paramTreeModel) {
    completeEditing();
    if (this.treeModel != null && this.treeModelListener != null)
      this.treeModel.removeTreeModelListener(this.treeModelListener); 
    this.treeModel = paramTreeModel;
    if (this.treeModel != null && this.treeModelListener != null)
      this.treeModel.addTreeModelListener(this.treeModelListener); 
    if (this.treeState != null) {
      this.treeState.setModel(paramTreeModel);
      updateLayoutCacheExpandedNodesIfNecessary();
      updateSize();
    } 
  }
  
  protected TreeModel getModel() { return this.treeModel; }
  
  protected void setRootVisible(boolean paramBoolean) {
    completeEditing();
    updateDepthOffset();
    if (this.treeState != null) {
      this.treeState.setRootVisible(paramBoolean);
      this.treeState.invalidateSizes();
      updateSize();
    } 
  }
  
  protected boolean isRootVisible() { return (this.tree != null) ? this.tree.isRootVisible() : 0; }
  
  protected void setShowsRootHandles(boolean paramBoolean) {
    completeEditing();
    updateDepthOffset();
    if (this.treeState != null) {
      this.treeState.invalidateSizes();
      updateSize();
    } 
  }
  
  protected boolean getShowsRootHandles() { return (this.tree != null) ? this.tree.getShowsRootHandles() : 0; }
  
  protected void setCellEditor(TreeCellEditor paramTreeCellEditor) { updateCellEditor(); }
  
  protected TreeCellEditor getCellEditor() { return (this.tree != null) ? this.tree.getCellEditor() : null; }
  
  protected void setEditable(boolean paramBoolean) { updateCellEditor(); }
  
  protected boolean isEditable() { return (this.tree != null) ? this.tree.isEditable() : 0; }
  
  protected void setSelectionModel(TreeSelectionModel paramTreeSelectionModel) {
    completeEditing();
    if (this.selectionModelPropertyChangeListener != null && this.treeSelectionModel != null)
      this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener); 
    if (this.treeSelectionListener != null && this.treeSelectionModel != null)
      this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener); 
    this.treeSelectionModel = paramTreeSelectionModel;
    if (this.treeSelectionModel != null) {
      if (this.selectionModelPropertyChangeListener != null)
        this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener); 
      if (this.treeSelectionListener != null)
        this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener); 
      if (this.treeState != null)
        this.treeState.setSelectionModel(this.treeSelectionModel); 
    } else if (this.treeState != null) {
      this.treeState.setSelectionModel(null);
    } 
    if (this.tree != null)
      this.tree.repaint(); 
  }
  
  protected TreeSelectionModel getSelectionModel() { return this.treeSelectionModel; }
  
  public Rectangle getPathBounds(JTree paramJTree, TreePath paramTreePath) { return (paramJTree != null && this.treeState != null) ? getPathBounds(paramTreePath, paramJTree.getInsets(), new Rectangle()) : null; }
  
  private Rectangle getPathBounds(TreePath paramTreePath, Insets paramInsets, Rectangle paramRectangle) {
    paramRectangle = this.treeState.getBounds(paramTreePath, paramRectangle);
    if (paramRectangle != null) {
      if (this.leftToRight) {
        paramRectangle.x += paramInsets.left;
      } else {
        paramRectangle.x = this.tree.getWidth() - paramRectangle.x + paramRectangle.width - paramInsets.right;
      } 
      paramRectangle.y += paramInsets.top;
    } 
    return paramRectangle;
  }
  
  public TreePath getPathForRow(JTree paramJTree, int paramInt) { return (this.treeState != null) ? this.treeState.getPathForRow(paramInt) : null; }
  
  public int getRowForPath(JTree paramJTree, TreePath paramTreePath) { return (this.treeState != null) ? this.treeState.getRowForPath(paramTreePath) : -1; }
  
  public int getRowCount(JTree paramJTree) { return (this.treeState != null) ? this.treeState.getRowCount() : 0; }
  
  public TreePath getClosestPathForLocation(JTree paramJTree, int paramInt1, int paramInt2) {
    if (paramJTree != null && this.treeState != null) {
      paramInt2 -= (paramJTree.getInsets()).top;
      return this.treeState.getPathClosestTo(paramInt1, paramInt2);
    } 
    return null;
  }
  
  public boolean isEditing(JTree paramJTree) { return (this.editingComponent != null); }
  
  public boolean stopEditing(JTree paramJTree) {
    if (this.editingComponent != null && this.cellEditor.stopCellEditing()) {
      completeEditing(false, false, true);
      return true;
    } 
    return false;
  }
  
  public void cancelEditing(JTree paramJTree) {
    if (this.editingComponent != null)
      completeEditing(false, true, false); 
  }
  
  public void startEditingAtPath(JTree paramJTree, TreePath paramTreePath) {
    paramJTree.scrollPathToVisible(paramTreePath);
    if (paramTreePath != null && paramJTree.isVisible(paramTreePath))
      startEditing(paramTreePath, null); 
  }
  
  public TreePath getEditingPath(JTree paramJTree) { return this.editingPath; }
  
  public void installUI(JComponent paramJComponent) {
    if (paramJComponent == null)
      throw new NullPointerException("null component passed to BasicTreeUI.installUI()"); 
    this.tree = (JTree)paramJComponent;
    prepareForUIInstall();
    installDefaults();
    installKeyboardActions();
    installComponents();
    installListeners();
    completeUIInstall();
  }
  
  protected void prepareForUIInstall() {
    this.drawingCache = new Hashtable(7);
    this.leftToRight = BasicGraphicsUtils.isLeftToRight(this.tree);
    this.stopEditingInCompleteEditing = true;
    this.lastSelectedRow = -1;
    this.leadRow = -1;
    this.preferredSize = new Dimension();
    this.largeModel = this.tree.isLargeModel();
    if (getRowHeight() <= 0)
      this.largeModel = false; 
    setModel(this.tree.getModel());
  }
  
  protected void completeUIInstall() {
    setShowsRootHandles(this.tree.getShowsRootHandles());
    updateRenderer();
    updateDepthOffset();
    setSelectionModel(this.tree.getSelectionModel());
    this.treeState = createLayoutCache();
    configureLayoutCache();
    updateSize();
  }
  
  protected void installDefaults() {
    if (this.tree.getBackground() == null || this.tree.getBackground() instanceof UIResource)
      this.tree.setBackground(UIManager.getColor("Tree.background")); 
    if (getHashColor() == null || getHashColor() instanceof UIResource)
      setHashColor(UIManager.getColor("Tree.hash")); 
    if (this.tree.getFont() == null || this.tree.getFont() instanceof UIResource)
      this.tree.setFont(UIManager.getFont("Tree.font")); 
    setExpandedIcon((Icon)UIManager.get("Tree.expandedIcon"));
    setCollapsedIcon((Icon)UIManager.get("Tree.collapsedIcon"));
    setLeftChildIndent(((Integer)UIManager.get("Tree.leftChildIndent")).intValue());
    setRightChildIndent(((Integer)UIManager.get("Tree.rightChildIndent")).intValue());
    LookAndFeel.installProperty(this.tree, "rowHeight", UIManager.get("Tree.rowHeight"));
    this.largeModel = (this.tree.isLargeModel() && this.tree.getRowHeight() > 0);
    Object object1 = UIManager.get("Tree.scrollsOnExpand");
    if (object1 != null)
      LookAndFeel.installProperty(this.tree, "scrollsOnExpand", object1); 
    this.paintLines = UIManager.getBoolean("Tree.paintLines");
    this.lineTypeDashed = UIManager.getBoolean("Tree.lineTypeDashed");
    Long long = (Long)UIManager.get("Tree.timeFactor");
    this.timeFactor = (long != null) ? long.longValue() : 1000L;
    Object object2 = UIManager.get("Tree.showsRootHandles");
    if (object2 != null)
      LookAndFeel.installProperty(this.tree, "showsRootHandles", object2); 
  }
  
  protected void installListeners() {
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
      this.tree.addPropertyChangeListener(this.propertyChangeListener); 
    if ((this.mouseListener = createMouseListener()) != null) {
      this.tree.addMouseListener(this.mouseListener);
      if (this.mouseListener instanceof MouseMotionListener)
        this.tree.addMouseMotionListener((MouseMotionListener)this.mouseListener); 
    } 
    if ((this.focusListener = createFocusListener()) != null)
      this.tree.addFocusListener(this.focusListener); 
    if ((this.keyListener = createKeyListener()) != null)
      this.tree.addKeyListener(this.keyListener); 
    if ((this.treeExpansionListener = createTreeExpansionListener()) != null)
      this.tree.addTreeExpansionListener(this.treeExpansionListener); 
    if ((this.treeModelListener = createTreeModelListener()) != null && this.treeModel != null)
      this.treeModel.addTreeModelListener(this.treeModelListener); 
    if ((this.selectionModelPropertyChangeListener = createSelectionModelPropertyChangeListener()) != null && this.treeSelectionModel != null)
      this.treeSelectionModel.addPropertyChangeListener(this.selectionModelPropertyChangeListener); 
    if ((this.treeSelectionListener = createTreeSelectionListener()) != null && this.treeSelectionModel != null)
      this.treeSelectionModel.addTreeSelectionListener(this.treeSelectionListener); 
    TransferHandler transferHandler = this.tree.getTransferHandler();
    if (transferHandler == null || transferHandler instanceof UIResource) {
      this.tree.setTransferHandler(defaultTransferHandler);
      if (this.tree.getDropTarget() instanceof UIResource)
        this.tree.setDropTarget(null); 
    } 
    LookAndFeel.installProperty(this.tree, "opaque", Boolean.TRUE);
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.tree, 1, inputMap);
    inputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(this.tree, 0, inputMap);
    LazyActionMap.installLazyActionMap(this.tree, BasicTreeUI.class, "Tree.actionMap");
  }
  
  InputMap getInputMap(int paramInt) {
    if (paramInt == 1)
      return (InputMap)DefaultLookup.get(this.tree, this, "Tree.ancestorInputMap"); 
    if (paramInt == 0) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap");
      InputMap inputMap2;
      if (this.tree.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(this.tree, this, "Tree.focusInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    return null;
  }
  
  protected void installComponents() {
    if ((this.rendererPane = createCellRendererPane()) != null)
      this.tree.add(this.rendererPane); 
  }
  
  protected AbstractLayoutCache.NodeDimensions createNodeDimensions() { return new NodeDimensionsHandler(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected MouseListener createMouseListener() { return getHandler(); }
  
  protected FocusListener createFocusListener() { return getHandler(); }
  
  protected KeyListener createKeyListener() { return getHandler(); }
  
  protected PropertyChangeListener createSelectionModelPropertyChangeListener() { return getHandler(); }
  
  protected TreeSelectionListener createTreeSelectionListener() { return getHandler(); }
  
  protected CellEditorListener createCellEditorListener() { return getHandler(); }
  
  protected ComponentListener createComponentListener() { return new ComponentHandler(); }
  
  protected TreeExpansionListener createTreeExpansionListener() { return getHandler(); }
  
  protected AbstractLayoutCache createLayoutCache() { return (isLargeModel() && getRowHeight() > 0) ? new FixedHeightLayoutCache() : new VariableHeightLayoutCache(); }
  
  protected CellRendererPane createCellRendererPane() { return new CellRendererPane(); }
  
  protected TreeCellEditor createDefaultCellEditor() { return (this.currentCellRenderer != null && this.currentCellRenderer instanceof DefaultTreeCellRenderer) ? new DefaultTreeCellEditor(this.tree, (DefaultTreeCellRenderer)this.currentCellRenderer) : new DefaultTreeCellEditor(this.tree, null); }
  
  protected TreeCellRenderer createDefaultCellRenderer() { return new DefaultTreeCellRenderer(); }
  
  protected TreeModelListener createTreeModelListener() { return getHandler(); }
  
  public void uninstallUI(JComponent paramJComponent) {
    completeEditing();
    prepareForUIUninstall();
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    uninstallComponents();
    completeUIUninstall();
  }
  
  protected void prepareForUIUninstall() {}
  
  protected void completeUIUninstall() {
    if (this.createdRenderer)
      this.tree.setCellRenderer(null); 
    if (this.createdCellEditor)
      this.tree.setCellEditor(null); 
    this.cellEditor = null;
    this.currentCellRenderer = null;
    this.rendererPane = null;
    this.componentListener = null;
    this.propertyChangeListener = null;
    this.mouseListener = null;
    this.focusListener = null;
    this.keyListener = null;
    setSelectionModel(null);
    this.treeState = null;
    this.drawingCache = null;
    this.selectionModelPropertyChangeListener = null;
    this.tree = null;
    this.treeModel = null;
    this.treeSelectionModel = null;
    this.treeSelectionListener = null;
    this.treeExpansionListener = null;
  }
  
  protected void uninstallDefaults() {
    if (this.tree.getTransferHandler() instanceof UIResource)
      this.tree.setTransferHandler(null); 
  }
  
  protected void uninstallListeners() {
    if (this.componentListener != null)
      this.tree.removeComponentListener(this.componentListener); 
    if (this.propertyChangeListener != null)
      this.tree.removePropertyChangeListener(this.propertyChangeListener); 
    if (this.mouseListener != null) {
      this.tree.removeMouseListener(this.mouseListener);
      if (this.mouseListener instanceof MouseMotionListener)
        this.tree.removeMouseMotionListener((MouseMotionListener)this.mouseListener); 
    } 
    if (this.focusListener != null)
      this.tree.removeFocusListener(this.focusListener); 
    if (this.keyListener != null)
      this.tree.removeKeyListener(this.keyListener); 
    if (this.treeExpansionListener != null)
      this.tree.removeTreeExpansionListener(this.treeExpansionListener); 
    if (this.treeModel != null && this.treeModelListener != null)
      this.treeModel.removeTreeModelListener(this.treeModelListener); 
    if (this.selectionModelPropertyChangeListener != null && this.treeSelectionModel != null)
      this.treeSelectionModel.removePropertyChangeListener(this.selectionModelPropertyChangeListener); 
    if (this.treeSelectionListener != null && this.treeSelectionModel != null)
      this.treeSelectionModel.removeTreeSelectionListener(this.treeSelectionListener); 
    this.handler = null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.tree, null);
    SwingUtilities.replaceUIInputMap(this.tree, 1, null);
    SwingUtilities.replaceUIInputMap(this.tree, 0, null);
  }
  
  protected void uninstallComponents() {
    if (this.rendererPane != null)
      this.tree.remove(this.rendererPane); 
  }
  
  private void redoTheLayout() {
    if (this.treeState != null)
      this.treeState.invalidateSizes(); 
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    int j;
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    Component component = (Component)uIDefaults.get(BASELINE_COMPONENT_KEY);
    if (component == null) {
      TreeCellRenderer treeCellRenderer = createDefaultCellRenderer();
      component = treeCellRenderer.getTreeCellRendererComponent(this.tree, "a", false, false, false, -1, false);
      uIDefaults.put(BASELINE_COMPONENT_KEY, component);
    } 
    int i = this.tree.getRowHeight();
    if (i > 0) {
      j = component.getBaseline(2147483647, i);
    } else {
      Dimension dimension = component.getPreferredSize();
      j = component.getBaseline(dimension.width, dimension.height);
    } 
    return j + (this.tree.getInsets()).top;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (this.tree != paramJComponent)
      throw new InternalError("incorrect component"); 
    if (this.treeState == null)
      return; 
    Rectangle rectangle = paramGraphics.getClipBounds();
    Insets insets = this.tree.getInsets();
    TreePath treePath = getClosestPathForLocation(this.tree, 0, rectangle.y);
    Enumeration enumeration = this.treeState.getVisiblePathsFrom(treePath);
    int i = this.treeState.getRowForPath(treePath);
    int j = rectangle.y + rectangle.height;
    this.drawingCache.clear();
    if (treePath != null && enumeration != null) {
      TreePath treePath1 = treePath;
      for (treePath1 = treePath1.getParentPath(); treePath1 != null; treePath1 = treePath1.getParentPath()) {
        paintVerticalPartOfLeg(paramGraphics, rectangle, insets, treePath1);
        this.drawingCache.put(treePath1, Boolean.TRUE);
      } 
      boolean bool = false;
      Rectangle rectangle1 = new Rectangle();
      boolean bool1 = isRootVisible();
      while (!bool && enumeration.hasMoreElements()) {
        TreePath treePath2 = (TreePath)enumeration.nextElement();
        if (treePath2 != null) {
          boolean bool3;
          boolean bool2;
          boolean bool4 = this.treeModel.isLeaf(treePath2.getLastPathComponent());
          if (bool4) {
            bool2 = bool3 = false;
          } else {
            bool2 = this.treeState.getExpandedState(treePath2);
            bool3 = this.tree.hasBeenExpanded(treePath2);
          } 
          Rectangle rectangle2 = getPathBounds(treePath2, insets, rectangle1);
          if (rectangle2 == null)
            return; 
          treePath1 = treePath2.getParentPath();
          if (treePath1 != null) {
            if (this.drawingCache.get(treePath1) == null) {
              paintVerticalPartOfLeg(paramGraphics, rectangle, insets, treePath1);
              this.drawingCache.put(treePath1, Boolean.TRUE);
            } 
            paintHorizontalPartOfLeg(paramGraphics, rectangle, insets, rectangle2, treePath2, i, bool2, bool3, bool4);
          } else if (bool1 && i == 0) {
            paintHorizontalPartOfLeg(paramGraphics, rectangle, insets, rectangle2, treePath2, i, bool2, bool3, bool4);
          } 
          if (shouldPaintExpandControl(treePath2, i, bool2, bool3, bool4))
            paintExpandControl(paramGraphics, rectangle, insets, rectangle2, treePath2, i, bool2, bool3, bool4); 
          paintRow(paramGraphics, rectangle, insets, rectangle2, treePath2, i, bool2, bool3, bool4);
          if (rectangle2.y + rectangle2.height >= j)
            bool = true; 
        } else {
          bool = true;
        } 
        i++;
      } 
    } 
    paintDropLine(paramGraphics);
    this.rendererPane.removeAll();
    this.drawingCache.clear();
  }
  
  protected boolean isDropLine(JTree.DropLocation paramDropLocation) { return (paramDropLocation != null && paramDropLocation.getPath() != null && paramDropLocation.getChildIndex() != -1); }
  
  protected void paintDropLine(Graphics paramGraphics) {
    JTree.DropLocation dropLocation = this.tree.getDropLocation();
    if (!isDropLine(dropLocation))
      return; 
    Color color = UIManager.getColor("Tree.dropLineColor");
    if (color != null) {
      paramGraphics.setColor(color);
      Rectangle rectangle = getDropLineRect(dropLocation);
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  protected Rectangle getDropLineRect(JTree.DropLocation paramDropLocation) {
    Rectangle rectangle;
    TreePath treePath = paramDropLocation.getPath();
    int i = paramDropLocation.getChildIndex();
    boolean bool = this.leftToRight;
    Insets insets = this.tree.getInsets();
    if (this.tree.getRowCount() == 0) {
      rectangle = new Rectangle(insets.left, insets.top, this.tree.getWidth() - insets.left - insets.right, 0);
    } else {
      TreeModel treeModel1 = getModel();
      Object object = treeModel1.getRoot();
      if (treePath.getLastPathComponent() == object && i >= treeModel1.getChildCount(object)) {
        Rectangle rectangle1;
        rectangle = this.tree.getRowBounds(this.tree.getRowCount() - 1);
        rectangle.y += rectangle.height;
        if (!this.tree.isRootVisible()) {
          rectangle1 = this.tree.getRowBounds(0);
        } else if (treeModel1.getChildCount(object) == 0) {
          rectangle1 = this.tree.getRowBounds(0);
          rectangle1.x += this.totalChildIndent;
          rectangle1.width -= this.totalChildIndent + this.totalChildIndent;
        } else {
          TreePath treePath1 = treePath.pathByAddingChild(treeModel1.getChild(object, treeModel1.getChildCount(object) - 1));
          rectangle1 = this.tree.getPathBounds(treePath1);
        } 
        rectangle.x = rectangle1.x;
        rectangle.width = rectangle1.width;
      } else {
        rectangle = this.tree.getPathBounds(treePath.pathByAddingChild(treeModel1.getChild(treePath.getLastPathComponent(), i)));
      } 
    } 
    if (rectangle.y != 0)
      rectangle.y--; 
    if (!bool)
      rectangle.x = rectangle.x + rectangle.width - 100; 
    rectangle.width = 100;
    rectangle.height = 2;
    return rectangle;
  }
  
  protected void paintHorizontalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (!this.paintLines)
      return; 
    int i = paramTreePath.getPathCount() - 1;
    if ((i == 0 || (i == 1 && !isRootVisible())) && !getShowsRootHandles())
      return; 
    int j = paramRectangle1.x;
    int k = paramRectangle1.x + paramRectangle1.width;
    int m = paramRectangle1.y;
    int n = paramRectangle1.y + paramRectangle1.height;
    int i1 = paramRectangle2.y + paramRectangle2.height / 2;
    if (this.leftToRight) {
      int i2 = paramRectangle2.x - getRightChildIndent();
      int i3 = paramRectangle2.x - getHorizontalLegBuffer();
      if (i1 >= m && i1 < n && i3 >= j && i2 < k && i2 < i3) {
        paramGraphics.setColor(getHashColor());
        paintHorizontalLine(paramGraphics, this.tree, i1, i2, i3 - 1);
      } 
    } else {
      int i2 = paramRectangle2.x + paramRectangle2.width + getHorizontalLegBuffer();
      int i3 = paramRectangle2.x + paramRectangle2.width + getRightChildIndent();
      if (i1 >= m && i1 < n && i3 >= j && i2 < k && i2 < i3) {
        paramGraphics.setColor(getHashColor());
        paintHorizontalLine(paramGraphics, this.tree, i1, i2, i3 - 1);
      } 
    } 
  }
  
  protected void paintVerticalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle, Insets paramInsets, TreePath paramTreePath) {
    if (!this.paintLines)
      return; 
    int i = paramTreePath.getPathCount() - 1;
    if (i == 0 && !getShowsRootHandles() && !isRootVisible())
      return; 
    int j = getRowX(-1, i + 1);
    if (this.leftToRight) {
      j = j - getRightChildIndent() + paramInsets.left;
    } else {
      j = this.tree.getWidth() - j - paramInsets.right + getRightChildIndent() - 1;
    } 
    int k = paramRectangle.x;
    int m = paramRectangle.x + paramRectangle.width - 1;
    if (j >= k && j <= m) {
      int i2;
      int n = paramRectangle.y;
      int i1 = paramRectangle.y + paramRectangle.height;
      Rectangle rectangle1 = getPathBounds(this.tree, paramTreePath);
      Rectangle rectangle2 = getPathBounds(this.tree, getLastChildPath(paramTreePath));
      if (rectangle2 == null)
        return; 
      if (rectangle1 == null) {
        i2 = Math.max(paramInsets.top + getVerticalLegBuffer(), n);
      } else {
        i2 = Math.max(rectangle1.y + rectangle1.height + getVerticalLegBuffer(), n);
      } 
      if (i == 0 && !isRootVisible()) {
        TreeModel treeModel1 = getModel();
        if (treeModel1 != null) {
          Object object = treeModel1.getRoot();
          if (treeModel1.getChildCount(object) > 0) {
            rectangle1 = getPathBounds(this.tree, paramTreePath.pathByAddingChild(treeModel1.getChild(object, 0)));
            if (rectangle1 != null)
              i2 = Math.max(paramInsets.top + getVerticalLegBuffer(), rectangle1.y + rectangle1.height / 2); 
          } 
        } 
      } 
      int i3 = Math.min(rectangle2.y + rectangle2.height / 2, i1);
      if (i2 <= i3) {
        paramGraphics.setColor(getHashColor());
        paintVerticalLine(paramGraphics, this.tree, j, i2, i3);
      } 
    } 
  }
  
  protected void paintExpandControl(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    Object object = paramTreePath.getLastPathComponent();
    if (!paramBoolean3 && (!paramBoolean2 || this.treeModel.getChildCount(object) > 0)) {
      int i;
      if (this.leftToRight) {
        i = paramRectangle2.x - getRightChildIndent() + 1;
      } else {
        i = paramRectangle2.x + paramRectangle2.width + getRightChildIndent() - 1;
      } 
      int j = paramRectangle2.y + paramRectangle2.height / 2;
      if (paramBoolean1) {
        Icon icon = getExpandedIcon();
        if (icon != null)
          drawCentered(this.tree, paramGraphics, icon, i, j); 
      } else {
        Icon icon = getCollapsedIcon();
        if (icon != null)
          drawCentered(this.tree, paramGraphics, icon, i, j); 
      } 
    } 
  }
  
  protected void paintRow(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    byte b;
    if (this.editingComponent != null && this.editingRow == paramInt)
      return; 
    if (this.tree.hasFocus()) {
      b = getLeadSelectionRow();
    } else {
      b = -1;
    } 
    Component component = this.currentCellRenderer.getTreeCellRendererComponent(this.tree, paramTreePath.getLastPathComponent(), this.tree.isRowSelected(paramInt), paramBoolean1, paramBoolean3, paramInt, (b == paramInt));
    this.rendererPane.paintComponent(paramGraphics, component, this.tree, paramRectangle2.x, paramRectangle2.y, paramRectangle2.width, paramRectangle2.height, true);
  }
  
  protected boolean shouldPaintExpandControl(TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (paramBoolean3)
      return false; 
    int i = paramTreePath.getPathCount() - 1;
    return !((i == 0 || (i == 1 && !isRootVisible())) && !getShowsRootHandles());
  }
  
  protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3) {
    if (this.lineTypeDashed) {
      drawDashedVerticalLine(paramGraphics, paramInt1, paramInt2, paramInt3);
    } else {
      paramGraphics.drawLine(paramInt1, paramInt2, paramInt1, paramInt3);
    } 
  }
  
  protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3) {
    if (this.lineTypeDashed) {
      drawDashedHorizontalLine(paramGraphics, paramInt1, paramInt2, paramInt3);
    } else {
      paramGraphics.drawLine(paramInt2, paramInt1, paramInt3, paramInt1);
    } 
  }
  
  protected int getVerticalLegBuffer() { return 0; }
  
  protected int getHorizontalLegBuffer() { return 0; }
  
  private int findCenteredX(int paramInt1, int paramInt2) { return this.leftToRight ? (paramInt1 - (int)Math.ceil(paramInt2 / 2.0D)) : (paramInt1 - (int)Math.floor(paramInt2 / 2.0D)); }
  
  protected void drawCentered(Component paramComponent, Graphics paramGraphics, Icon paramIcon, int paramInt1, int paramInt2) { paramIcon.paintIcon(paramComponent, paramGraphics, findCenteredX(paramInt1, paramIcon.getIconWidth()), paramInt2 - paramIcon.getIconHeight() / 2); }
  
  protected void drawDashedHorizontalLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
    paramInt2 += paramInt2 % 2;
    for (int i = paramInt2; i <= paramInt3; i += 2)
      paramGraphics.drawLine(i, paramInt1, i, paramInt1); 
  }
  
  protected void drawDashedVerticalLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
    paramInt2 += paramInt2 % 2;
    for (int i = paramInt2; i <= paramInt3; i += 2)
      paramGraphics.drawLine(paramInt1, i, paramInt1, i); 
  }
  
  protected int getRowX(int paramInt1, int paramInt2) { return this.totalChildIndent * (paramInt2 + this.depthOffset); }
  
  protected void updateLayoutCacheExpandedNodes() {
    if (this.treeModel != null && this.treeModel.getRoot() != null)
      updateExpandedDescendants(new TreePath(this.treeModel.getRoot())); 
  }
  
  private void updateLayoutCacheExpandedNodesIfNecessary() {
    if (this.treeModel != null && this.treeModel.getRoot() != null) {
      TreePath treePath = new TreePath(this.treeModel.getRoot());
      if (this.tree.isExpanded(treePath)) {
        updateLayoutCacheExpandedNodes();
      } else {
        this.treeState.setExpandedState(treePath, false);
      } 
    } 
  }
  
  protected void updateExpandedDescendants(TreePath paramTreePath) {
    completeEditing();
    if (this.treeState != null) {
      this.treeState.setExpandedState(paramTreePath, true);
      Enumeration enumeration = this.tree.getExpandedDescendants(paramTreePath);
      if (enumeration != null)
        while (enumeration.hasMoreElements()) {
          paramTreePath = (TreePath)enumeration.nextElement();
          this.treeState.setExpandedState(paramTreePath, true);
        }  
      updateLeadSelectionRow();
      updateSize();
    } 
  }
  
  protected TreePath getLastChildPath(TreePath paramTreePath) {
    if (this.treeModel != null) {
      int i = this.treeModel.getChildCount(paramTreePath.getLastPathComponent());
      if (i > 0)
        return paramTreePath.pathByAddingChild(this.treeModel.getChild(paramTreePath.getLastPathComponent(), i - 1)); 
    } 
    return null;
  }
  
  protected void updateDepthOffset() {
    if (isRootVisible()) {
      if (getShowsRootHandles()) {
        this.depthOffset = 1;
      } else {
        this.depthOffset = 0;
      } 
    } else if (!getShowsRootHandles()) {
      this.depthOffset = -1;
    } else {
      this.depthOffset = 0;
    } 
  }
  
  protected void updateCellEditor() {
    Object object;
    completeEditing();
    if (this.tree == null) {
      object = null;
    } else if (this.tree.isEditable()) {
      object = this.tree.getCellEditor();
      if (object == null) {
        object = createDefaultCellEditor();
        if (object != null) {
          this.tree.setCellEditor(object);
          this.createdCellEditor = true;
        } 
      } 
    } else {
      object = null;
    } 
    if (object != this.cellEditor) {
      if (this.cellEditor != null && this.cellEditorListener != null)
        this.cellEditor.removeCellEditorListener(this.cellEditorListener); 
      this.cellEditor = object;
      if (this.cellEditorListener == null)
        this.cellEditorListener = createCellEditorListener(); 
      if (object != null && this.cellEditorListener != null)
        object.addCellEditorListener(this.cellEditorListener); 
      this.createdCellEditor = false;
    } 
  }
  
  protected void updateRenderer() {
    if (this.tree != null) {
      TreeCellRenderer treeCellRenderer = this.tree.getCellRenderer();
      if (treeCellRenderer == null) {
        this.tree.setCellRenderer(createDefaultCellRenderer());
        this.createdRenderer = true;
      } else {
        this.createdRenderer = false;
        this.currentCellRenderer = treeCellRenderer;
        if (this.createdCellEditor)
          this.tree.setCellEditor(null); 
      } 
    } else {
      this.createdRenderer = false;
      this.currentCellRenderer = null;
    } 
    updateCellEditor();
  }
  
  protected void configureLayoutCache() {
    if (this.treeState != null && this.tree != null) {
      if (this.nodeDimensions == null)
        this.nodeDimensions = createNodeDimensions(); 
      this.treeState.setNodeDimensions(this.nodeDimensions);
      this.treeState.setRootVisible(this.tree.isRootVisible());
      this.treeState.setRowHeight(this.tree.getRowHeight());
      this.treeState.setSelectionModel(getSelectionModel());
      if (this.treeState.getModel() != this.tree.getModel())
        this.treeState.setModel(this.tree.getModel()); 
      updateLayoutCacheExpandedNodesIfNecessary();
      if (isLargeModel()) {
        if (this.componentListener == null) {
          this.componentListener = createComponentListener();
          if (this.componentListener != null)
            this.tree.addComponentListener(this.componentListener); 
        } 
      } else if (this.componentListener != null) {
        this.tree.removeComponentListener(this.componentListener);
        this.componentListener = null;
      } 
    } else if (this.componentListener != null) {
      this.tree.removeComponentListener(this.componentListener);
      this.componentListener = null;
    } 
  }
  
  protected void updateSize() {
    this.validCachedPreferredSize = false;
    this.tree.treeDidChange();
  }
  
  private void updateSize0() {
    this.validCachedPreferredSize = false;
    this.tree.revalidate();
  }
  
  protected void updateCachedPreferredSize() {
    if (this.treeState != null) {
      Insets insets = this.tree.getInsets();
      if (isLargeModel()) {
        Rectangle rectangle = this.tree.getVisibleRect();
        if (rectangle.x == 0 && rectangle.y == 0 && rectangle.width == 0 && rectangle.height == 0 && this.tree.getVisibleRowCount() > 0) {
          rectangle.width = 1;
          rectangle.height = this.tree.getRowHeight() * this.tree.getVisibleRowCount();
        } else {
          rectangle.x -= insets.left;
          rectangle.y -= insets.top;
        } 
        Container container = SwingUtilities.getUnwrappedParent(this.tree);
        if (container instanceof javax.swing.JViewport) {
          container = container.getParent();
          if (container instanceof JScrollPane) {
            JScrollPane jScrollPane = (JScrollPane)container;
            JScrollBar jScrollBar = jScrollPane.getHorizontalScrollBar();
            if (jScrollBar != null && jScrollBar.isVisible()) {
              int i = jScrollBar.getHeight();
              rectangle.y -= i;
              rectangle.height += i;
            } 
          } 
        } 
        this.preferredSize.width = this.treeState.getPreferredWidth(rectangle);
      } else {
        this.preferredSize.width = this.treeState.getPreferredWidth(null);
      } 
      this.preferredSize.height = this.treeState.getPreferredHeight();
      this.preferredSize.width += insets.left + insets.right;
      this.preferredSize.height += insets.top + insets.bottom;
    } 
    this.validCachedPreferredSize = true;
  }
  
  protected void pathWasExpanded(TreePath paramTreePath) {
    if (this.tree != null)
      this.tree.fireTreeExpanded(paramTreePath); 
  }
  
  protected void pathWasCollapsed(TreePath paramTreePath) {
    if (this.tree != null)
      this.tree.fireTreeCollapsed(paramTreePath); 
  }
  
  protected void ensureRowsAreVisible(int paramInt1, int paramInt2) {
    if (this.tree != null && paramInt1 >= 0 && paramInt2 < getRowCount(this.tree)) {
      boolean bool = DefaultLookup.getBoolean(this.tree, this, "Tree.scrollsHorizontallyAndVertically", false);
      if (paramInt1 == paramInt2) {
        Rectangle rectangle = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
        if (rectangle != null) {
          if (!bool) {
            rectangle.x = (this.tree.getVisibleRect()).x;
            rectangle.width = 1;
          } 
          this.tree.scrollRectToVisible(rectangle);
        } 
      } else {
        Rectangle rectangle = getPathBounds(this.tree, getPathForRow(this.tree, paramInt1));
        if (rectangle != null) {
          Rectangle rectangle1 = this.tree.getVisibleRect();
          Rectangle rectangle2 = rectangle;
          int i = rectangle.y;
          int j = i + rectangle1.height;
          for (int k = paramInt1 + 1; k <= paramInt2; k++) {
            rectangle2 = getPathBounds(this.tree, getPathForRow(this.tree, k));
            if (rectangle2 == null)
              return; 
            if (rectangle2.y + rectangle2.height > j)
              k = paramInt2; 
          } 
          this.tree.scrollRectToVisible(new Rectangle(rectangle1.x, i, 1, rectangle2.y + rectangle2.height - i));
        } 
      } 
    } 
  }
  
  public void setPreferredMinSize(Dimension paramDimension) { this.preferredMinSize = paramDimension; }
  
  public Dimension getPreferredMinSize() { return (this.preferredMinSize == null) ? null : new Dimension(this.preferredMinSize); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return getPreferredSize(paramJComponent, true); }
  
  public Dimension getPreferredSize(JComponent paramJComponent, boolean paramBoolean) {
    Dimension dimension = getPreferredMinSize();
    if (!this.validCachedPreferredSize)
      updateCachedPreferredSize(); 
    return (this.tree != null) ? ((dimension != null) ? new Dimension(Math.max(dimension.width, this.preferredSize.width), Math.max(dimension.height, this.preferredSize.height)) : new Dimension(this.preferredSize.width, this.preferredSize.height)) : ((dimension != null) ? dimension : new Dimension(0, 0));
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return (getPreferredMinSize() != null) ? getPreferredMinSize() : new Dimension(0, 0); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return (this.tree != null) ? getPreferredSize(this.tree) : ((getPreferredMinSize() != null) ? getPreferredMinSize() : new Dimension(0, 0)); }
  
  protected void completeEditing() {
    if (this.tree.getInvokesStopCellEditing() && this.stopEditingInCompleteEditing && this.editingComponent != null)
      this.cellEditor.stopCellEditing(); 
    completeEditing(false, true, false);
  }
  
  protected void completeEditing(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (this.stopEditingInCompleteEditing && this.editingComponent != null) {
      Component component = this.editingComponent;
      TreePath treePath = this.editingPath;
      TreeCellEditor treeCellEditor = this.cellEditor;
      Object object = treeCellEditor.getCellEditorValue();
      Rectangle rectangle = getPathBounds(this.tree, this.editingPath);
      boolean bool = (this.tree != null && (this.tree.hasFocus() || SwingUtilities.findFocusOwner(this.editingComponent) != null)) ? 1 : 0;
      this.editingComponent = null;
      this.editingPath = null;
      if (paramBoolean1) {
        treeCellEditor.stopCellEditing();
      } else if (paramBoolean2) {
        treeCellEditor.cancelCellEditing();
      } 
      this.tree.remove(component);
      if (this.editorHasDifferentSize) {
        this.treeState.invalidatePathBounds(treePath);
        updateSize();
      } else if (rectangle != null) {
        rectangle.x = 0;
        rectangle.width = (this.tree.getSize()).width;
        this.tree.repaint(rectangle);
      } 
      if (bool)
        this.tree.requestFocus(); 
      if (paramBoolean3)
        this.treeModel.valueForPathChanged(treePath, object); 
    } 
  }
  
  private boolean startEditingOnRelease(TreePath paramTreePath, MouseEvent paramMouseEvent1, MouseEvent paramMouseEvent2) {
    this.releaseEvent = paramMouseEvent2;
    try {
      return startEditing(paramTreePath, paramMouseEvent1);
    } finally {
      this.releaseEvent = null;
    } 
  }
  
  protected boolean startEditing(TreePath paramTreePath, MouseEvent paramMouseEvent) {
    if (isEditing(this.tree) && this.tree.getInvokesStopCellEditing() && !stopEditing(this.tree))
      return false; 
    completeEditing();
    if (this.cellEditor != null && this.tree.isPathEditable(paramTreePath)) {
      int i = getRowForPath(this.tree, paramTreePath);
      if (this.cellEditor.isCellEditable(paramMouseEvent)) {
        this.editingComponent = this.cellEditor.getTreeCellEditorComponent(this.tree, paramTreePath.getLastPathComponent(), this.tree.isPathSelected(paramTreePath), this.tree.isExpanded(paramTreePath), this.treeModel.isLeaf(paramTreePath.getLastPathComponent()), i);
        Rectangle rectangle = getPathBounds(this.tree, paramTreePath);
        if (rectangle == null)
          return false; 
        this.editingRow = i;
        Dimension dimension = this.editingComponent.getPreferredSize();
        if (dimension.height != rectangle.height && getRowHeight() > 0)
          dimension.height = getRowHeight(); 
        if (dimension.width != rectangle.width || dimension.height != rectangle.height) {
          this.editorHasDifferentSize = true;
          this.treeState.invalidatePathBounds(paramTreePath);
          updateSize();
          rectangle = getPathBounds(this.tree, paramTreePath);
          if (rectangle == null)
            return false; 
        } else {
          this.editorHasDifferentSize = false;
        } 
        this.tree.add(this.editingComponent);
        this.editingComponent.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        this.editingPath = paramTreePath;
        AWTAccessor.getComponentAccessor().revalidateSynchronously(this.editingComponent);
        this.editingComponent.repaint();
        if (this.cellEditor.shouldSelectCell(paramMouseEvent)) {
          this.stopEditingInCompleteEditing = false;
          this.tree.setSelectionRow(i);
          this.stopEditingInCompleteEditing = true;
        } 
        Component component = SwingUtilities2.compositeRequestFocus(this.editingComponent);
        boolean bool = true;
        if (paramMouseEvent != null) {
          Point point = SwingUtilities.convertPoint(this.tree, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()), this.editingComponent);
          Component component1 = SwingUtilities.getDeepestComponentAt(this.editingComponent, point.x, point.y);
          if (component1 != null) {
            MouseInputHandler mouseInputHandler = new MouseInputHandler(this.tree, component1, paramMouseEvent, component);
            if (this.releaseEvent != null)
              mouseInputHandler.mouseReleased(this.releaseEvent); 
            bool = false;
          } 
        } 
        if (bool && component instanceof JTextField)
          ((JTextField)component).selectAll(); 
        return true;
      } 
      this.editingComponent = null;
    } 
    return false;
  }
  
  protected void checkForClickInExpandControl(TreePath paramTreePath, int paramInt1, int paramInt2) {
    if (isLocationInExpandControl(paramTreePath, paramInt1, paramInt2))
      handleExpandControlClick(paramTreePath, paramInt1, paramInt2); 
  }
  
  protected boolean isLocationInExpandControl(TreePath paramTreePath, int paramInt1, int paramInt2) {
    if (paramTreePath != null && !this.treeModel.isLeaf(paramTreePath.getLastPathComponent())) {
      int i;
      Insets insets = this.tree.getInsets();
      if (getExpandedIcon() != null) {
        i = getExpandedIcon().getIconWidth();
      } else {
        i = 8;
      } 
      int j = getRowX(this.tree.getRowForPath(paramTreePath), paramTreePath.getPathCount() - 1);
      if (this.leftToRight) {
        j = j + insets.left - getRightChildIndent() + 1;
      } else {
        j = this.tree.getWidth() - j - insets.right + getRightChildIndent() - 1;
      } 
      j = findCenteredX(j, i);
      return (paramInt1 >= j && paramInt1 < j + i);
    } 
    return false;
  }
  
  protected void handleExpandControlClick(TreePath paramTreePath, int paramInt1, int paramInt2) { toggleExpandState(paramTreePath); }
  
  protected void toggleExpandState(TreePath paramTreePath) {
    if (!this.tree.isExpanded(paramTreePath)) {
      int i = getRowForPath(this.tree, paramTreePath);
      this.tree.expandPath(paramTreePath);
      updateSize();
      if (i != -1)
        if (this.tree.getScrollsOnExpand()) {
          ensureRowsAreVisible(i, i + this.treeState.getVisibleChildCount(paramTreePath));
        } else {
          ensureRowsAreVisible(i, i);
        }  
    } else {
      this.tree.collapsePath(paramTreePath);
      updateSize();
    } 
  }
  
  protected boolean isToggleSelectionEvent(MouseEvent paramMouseEvent) { return (SwingUtilities.isLeftMouseButton(paramMouseEvent) && BasicGraphicsUtils.isMenuShortcutKeyDown(paramMouseEvent)); }
  
  protected boolean isMultiSelectEvent(MouseEvent paramMouseEvent) { return (SwingUtilities.isLeftMouseButton(paramMouseEvent) && paramMouseEvent.isShiftDown()); }
  
  protected boolean isToggleEvent(MouseEvent paramMouseEvent) {
    if (!SwingUtilities.isLeftMouseButton(paramMouseEvent))
      return false; 
    int i = this.tree.getToggleClickCount();
    return (i <= 0) ? false : ((paramMouseEvent.getClickCount() % i == 0));
  }
  
  protected void selectPathForEvent(TreePath paramTreePath, MouseEvent paramMouseEvent) {
    if (isMultiSelectEvent(paramMouseEvent)) {
      TreePath treePath = getAnchorSelectionPath();
      byte b = (treePath == null) ? -1 : getRowForPath(this.tree, treePath);
      if (b == -1 || this.tree.getSelectionModel().getSelectionMode() == 1) {
        this.tree.setSelectionPath(paramTreePath);
      } else {
        int i = getRowForPath(this.tree, paramTreePath);
        TreePath treePath1 = treePath;
        if (isToggleSelectionEvent(paramMouseEvent)) {
          if (this.tree.isRowSelected(b)) {
            this.tree.addSelectionInterval(b, i);
          } else {
            this.tree.removeSelectionInterval(b, i);
            this.tree.addSelectionInterval(i, i);
          } 
        } else if (i < b) {
          this.tree.setSelectionInterval(i, b);
        } else {
          this.tree.setSelectionInterval(b, i);
        } 
        this.lastSelectedRow = i;
        setAnchorSelectionPath(treePath1);
        setLeadSelectionPath(paramTreePath);
      } 
    } else if (isToggleSelectionEvent(paramMouseEvent)) {
      if (this.tree.isPathSelected(paramTreePath)) {
        this.tree.removeSelectionPath(paramTreePath);
      } else {
        this.tree.addSelectionPath(paramTreePath);
      } 
      this.lastSelectedRow = getRowForPath(this.tree, paramTreePath);
      setAnchorSelectionPath(paramTreePath);
      setLeadSelectionPath(paramTreePath);
    } else if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
      this.tree.setSelectionPath(paramTreePath);
      if (isToggleEvent(paramMouseEvent))
        toggleExpandState(paramTreePath); 
    } 
  }
  
  protected boolean isLeaf(int paramInt) {
    TreePath treePath = getPathForRow(this.tree, paramInt);
    return (treePath != null) ? this.treeModel.isLeaf(treePath.getLastPathComponent()) : 1;
  }
  
  private void setAnchorSelectionPath(TreePath paramTreePath) {
    this.ignoreLAChange = true;
    try {
      this.tree.setAnchorSelectionPath(paramTreePath);
    } finally {
      this.ignoreLAChange = false;
    } 
  }
  
  private TreePath getAnchorSelectionPath() { return this.tree.getAnchorSelectionPath(); }
  
  private void setLeadSelectionPath(TreePath paramTreePath) { setLeadSelectionPath(paramTreePath, false); }
  
  private void setLeadSelectionPath(TreePath paramTreePath, boolean paramBoolean) {
    Rectangle rectangle = paramBoolean ? getPathBounds(this.tree, getLeadSelectionPath()) : null;
    this.ignoreLAChange = true;
    try {
      this.tree.setLeadSelectionPath(paramTreePath);
    } finally {
      this.ignoreLAChange = false;
    } 
    this.leadRow = getRowForPath(this.tree, paramTreePath);
    if (paramBoolean) {
      if (rectangle != null)
        this.tree.repaint(getRepaintPathBounds(rectangle)); 
      rectangle = getPathBounds(this.tree, paramTreePath);
      if (rectangle != null)
        this.tree.repaint(getRepaintPathBounds(rectangle)); 
    } 
  }
  
  private Rectangle getRepaintPathBounds(Rectangle paramRectangle) {
    if (UIManager.getBoolean("Tree.repaintWholeRow")) {
      paramRectangle.x = 0;
      paramRectangle.width = this.tree.getWidth();
    } 
    return paramRectangle;
  }
  
  private TreePath getLeadSelectionPath() { return this.tree.getLeadSelectionPath(); }
  
  protected void updateLeadSelectionRow() { this.leadRow = getRowForPath(this.tree, getLeadSelectionPath()); }
  
  protected int getLeadSelectionRow() { return this.leadRow; }
  
  private void extendSelection(TreePath paramTreePath) {
    TreePath treePath = getAnchorSelectionPath();
    byte b = (treePath == null) ? -1 : getRowForPath(this.tree, treePath);
    int i = getRowForPath(this.tree, paramTreePath);
    if (b == -1) {
      this.tree.setSelectionRow(i);
    } else {
      if (b < i) {
        this.tree.setSelectionInterval(b, i);
      } else {
        this.tree.setSelectionInterval(i, b);
      } 
      setAnchorSelectionPath(treePath);
      setLeadSelectionPath(paramTreePath);
    } 
  }
  
  private void repaintPath(TreePath paramTreePath) {
    if (paramTreePath != null) {
      Rectangle rectangle = getPathBounds(this.tree, paramTreePath);
      if (rectangle != null)
        this.tree.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height); 
    } 
  }
  
  private static class Actions extends UIAction {
    private static final String SELECT_PREVIOUS = "selectPrevious";
    
    private static final String SELECT_PREVIOUS_CHANGE_LEAD = "selectPreviousChangeLead";
    
    private static final String SELECT_PREVIOUS_EXTEND_SELECTION = "selectPreviousExtendSelection";
    
    private static final String SELECT_NEXT = "selectNext";
    
    private static final String SELECT_NEXT_CHANGE_LEAD = "selectNextChangeLead";
    
    private static final String SELECT_NEXT_EXTEND_SELECTION = "selectNextExtendSelection";
    
    private static final String SELECT_CHILD = "selectChild";
    
    private static final String SELECT_CHILD_CHANGE_LEAD = "selectChildChangeLead";
    
    private static final String SELECT_PARENT = "selectParent";
    
    private static final String SELECT_PARENT_CHANGE_LEAD = "selectParentChangeLead";
    
    private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
    
    private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
    
    private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
    
    private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
    
    private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
    
    private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
    
    private static final String SELECT_FIRST = "selectFirst";
    
    private static final String SELECT_FIRST_CHANGE_LEAD = "selectFirstChangeLead";
    
    private static final String SELECT_FIRST_EXTEND_SELECTION = "selectFirstExtendSelection";
    
    private static final String SELECT_LAST = "selectLast";
    
    private static final String SELECT_LAST_CHANGE_LEAD = "selectLastChangeLead";
    
    private static final String SELECT_LAST_EXTEND_SELECTION = "selectLastExtendSelection";
    
    private static final String TOGGLE = "toggle";
    
    private static final String CANCEL_EDITING = "cancel";
    
    private static final String START_EDITING = "startEditing";
    
    private static final String SELECT_ALL = "selectAll";
    
    private static final String CLEAR_SELECTION = "clearSelection";
    
    private static final String SCROLL_LEFT = "scrollLeft";
    
    private static final String SCROLL_RIGHT = "scrollRight";
    
    private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
    
    private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
    
    private static final String SCROLL_RIGHT_CHANGE_LEAD = "scrollRightChangeLead";
    
    private static final String SCROLL_LEFT_CHANGE_LEAD = "scrollLeftChangeLead";
    
    private static final String EXPAND = "expand";
    
    private static final String COLLAPSE = "collapse";
    
    private static final String MOVE_SELECTION_TO_PARENT = "moveSelectionToParent";
    
    private static final String ADD_TO_SELECTION = "addToSelection";
    
    private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
    
    private static final String EXTEND_TO = "extendTo";
    
    private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    
    Actions() { super(null); }
    
    Actions(String param1String) { super(param1String); }
    
    public boolean isEnabled(Object param1Object) { return (param1Object instanceof JTree && getName() == "cancel") ? ((JTree)param1Object).isEditing() : 1; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTree jTree = (JTree)param1ActionEvent.getSource();
      BasicTreeUI basicTreeUI = (BasicTreeUI)BasicLookAndFeel.getUIOfType(jTree.getUI(), BasicTreeUI.class);
      if (basicTreeUI == null)
        return; 
      String str = getName();
      if (str == "selectPrevious") {
        increment(jTree, basicTreeUI, -1, false, true);
      } else if (str == "selectPreviousChangeLead") {
        increment(jTree, basicTreeUI, -1, false, false);
      } else if (str == "selectPreviousExtendSelection") {
        increment(jTree, basicTreeUI, -1, true, true);
      } else if (str == "selectNext") {
        increment(jTree, basicTreeUI, 1, false, true);
      } else if (str == "selectNextChangeLead") {
        increment(jTree, basicTreeUI, 1, false, false);
      } else if (str == "selectNextExtendSelection") {
        increment(jTree, basicTreeUI, 1, true, true);
      } else if (str == "selectChild") {
        traverse(jTree, basicTreeUI, 1, true);
      } else if (str == "selectChildChangeLead") {
        traverse(jTree, basicTreeUI, 1, false);
      } else if (str == "selectParent") {
        traverse(jTree, basicTreeUI, -1, true);
      } else if (str == "selectParentChangeLead") {
        traverse(jTree, basicTreeUI, -1, false);
      } else if (str == "scrollUpChangeSelection") {
        page(jTree, basicTreeUI, -1, false, true);
      } else if (str == "scrollUpChangeLead") {
        page(jTree, basicTreeUI, -1, false, false);
      } else if (str == "scrollUpExtendSelection") {
        page(jTree, basicTreeUI, -1, true, true);
      } else if (str == "scrollDownChangeSelection") {
        page(jTree, basicTreeUI, 1, false, true);
      } else if (str == "scrollDownExtendSelection") {
        page(jTree, basicTreeUI, 1, true, true);
      } else if (str == "scrollDownChangeLead") {
        page(jTree, basicTreeUI, 1, false, false);
      } else if (str == "selectFirst") {
        home(jTree, basicTreeUI, -1, false, true);
      } else if (str == "selectFirstChangeLead") {
        home(jTree, basicTreeUI, -1, false, false);
      } else if (str == "selectFirstExtendSelection") {
        home(jTree, basicTreeUI, -1, true, true);
      } else if (str == "selectLast") {
        home(jTree, basicTreeUI, 1, false, true);
      } else if (str == "selectLastChangeLead") {
        home(jTree, basicTreeUI, 1, false, false);
      } else if (str == "selectLastExtendSelection") {
        home(jTree, basicTreeUI, 1, true, true);
      } else if (str == "toggle") {
        toggle(jTree, basicTreeUI);
      } else if (str == "cancel") {
        cancelEditing(jTree, basicTreeUI);
      } else if (str == "startEditing") {
        startEditing(jTree, basicTreeUI);
      } else if (str == "selectAll") {
        selectAll(jTree, basicTreeUI, true);
      } else if (str == "clearSelection") {
        selectAll(jTree, basicTreeUI, false);
      } else if (str == "addToSelection") {
        if (basicTreeUI.getRowCount(jTree) > 0) {
          int i = basicTreeUI.getLeadSelectionRow();
          if (!jTree.isRowSelected(i)) {
            TreePath treePath = basicTreeUI.getAnchorSelectionPath();
            jTree.addSelectionRow(i);
            basicTreeUI.setAnchorSelectionPath(treePath);
          } 
        } 
      } else if (str == "toggleAndAnchor") {
        if (basicTreeUI.getRowCount(jTree) > 0) {
          int i = basicTreeUI.getLeadSelectionRow();
          TreePath treePath = basicTreeUI.getLeadSelectionPath();
          if (!jTree.isRowSelected(i)) {
            jTree.addSelectionRow(i);
          } else {
            jTree.removeSelectionRow(i);
            basicTreeUI.setLeadSelectionPath(treePath);
          } 
          basicTreeUI.setAnchorSelectionPath(treePath);
        } 
      } else if (str == "extendTo") {
        extendSelection(jTree, basicTreeUI);
      } else if (str == "moveSelectionTo") {
        if (basicTreeUI.getRowCount(jTree) > 0) {
          int i = basicTreeUI.getLeadSelectionRow();
          jTree.setSelectionInterval(i, i);
        } 
      } else if (str == "scrollLeft") {
        scroll(jTree, basicTreeUI, 0, -10);
      } else if (str == "scrollRight") {
        scroll(jTree, basicTreeUI, 0, 10);
      } else if (str == "scrollLeftExtendSelection") {
        scrollChangeSelection(jTree, basicTreeUI, -1, true, true);
      } else if (str == "scrollRightExtendSelection") {
        scrollChangeSelection(jTree, basicTreeUI, 1, true, true);
      } else if (str == "scrollRightChangeLead") {
        scrollChangeSelection(jTree, basicTreeUI, 1, false, false);
      } else if (str == "scrollLeftChangeLead") {
        scrollChangeSelection(jTree, basicTreeUI, -1, false, false);
      } else if (str == "expand") {
        expand(jTree, basicTreeUI);
      } else if (str == "collapse") {
        collapse(jTree, basicTreeUI);
      } else if (str == "moveSelectionToParent") {
        moveSelectionToParent(jTree, basicTreeUI);
      } 
    }
    
    private void scrollChangeSelection(JTree param1JTree, BasicTreeUI param1BasicTreeUI, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      int i;
      if ((i = param1BasicTreeUI.getRowCount(param1JTree)) > 0 && param1BasicTreeUI.treeSelectionModel != null) {
        TreePath treePath;
        Rectangle rectangle = param1JTree.getVisibleRect();
        if (param1Int == -1) {
          treePath = param1BasicTreeUI.getClosestPathForLocation(param1JTree, rectangle.x, rectangle.y);
          rectangle.x = Math.max(0, rectangle.x - rectangle.width);
        } else {
          rectangle.x = Math.min(Math.max(0, param1JTree.getWidth() - rectangle.width), rectangle.x + rectangle.width);
          treePath = param1BasicTreeUI.getClosestPathForLocation(param1JTree, rectangle.x, rectangle.y + rectangle.height);
        } 
        param1JTree.scrollRectToVisible(rectangle);
        if (param1Boolean1) {
          param1BasicTreeUI.extendSelection(treePath);
        } else if (param1Boolean2) {
          param1JTree.setSelectionPath(treePath);
        } else {
          param1BasicTreeUI.setLeadSelectionPath(treePath, true);
        } 
      } 
    }
    
    private void scroll(JTree param1JTree, BasicTreeUI param1BasicTreeUI, int param1Int1, int param1Int2) {
      Rectangle rectangle = param1JTree.getVisibleRect();
      Dimension dimension = param1JTree.getSize();
      if (param1Int1 == 0) {
        rectangle.x += param1Int2;
        rectangle.x = Math.max(0, rectangle.x);
        rectangle.x = Math.min(Math.max(0, dimension.width - rectangle.width), rectangle.x);
      } else {
        rectangle.y += param1Int2;
        rectangle.y = Math.max(0, rectangle.y);
        rectangle.y = Math.min(Math.max(0, dimension.width - rectangle.height), rectangle.y);
      } 
      param1JTree.scrollRectToVisible(rectangle);
    }
    
    private void extendSelection(JTree param1JTree, BasicTreeUI param1BasicTreeUI) {
      if (param1BasicTreeUI.getRowCount(param1JTree) > 0) {
        int i = param1BasicTreeUI.getLeadSelectionRow();
        if (i != -1) {
          TreePath treePath1 = param1BasicTreeUI.getLeadSelectionPath();
          TreePath treePath2 = param1BasicTreeUI.getAnchorSelectionPath();
          int j = param1BasicTreeUI.getRowForPath(param1JTree, treePath2);
          if (j == -1)
            j = 0; 
          param1JTree.setSelectionInterval(j, i);
          param1BasicTreeUI.setLeadSelectionPath(treePath1);
          param1BasicTreeUI.setAnchorSelectionPath(treePath2);
        } 
      } 
    }
    
    private void selectAll(JTree param1JTree, BasicTreeUI param1BasicTreeUI, boolean param1Boolean) {
      int i = param1BasicTreeUI.getRowCount(param1JTree);
      if (i > 0)
        if (param1Boolean) {
          if (param1JTree.getSelectionModel().getSelectionMode() == 1) {
            int j = param1BasicTreeUI.getLeadSelectionRow();
            if (j != -1) {
              param1JTree.setSelectionRow(j);
            } else if (param1JTree.getMinSelectionRow() == -1) {
              param1JTree.setSelectionRow(0);
              param1BasicTreeUI.ensureRowsAreVisible(0, 0);
            } 
            return;
          } 
          TreePath treePath1 = param1BasicTreeUI.getLeadSelectionPath();
          TreePath treePath2 = param1BasicTreeUI.getAnchorSelectionPath();
          if (treePath1 != null && !param1JTree.isVisible(treePath1))
            treePath1 = null; 
          param1JTree.setSelectionInterval(0, i - 1);
          if (treePath1 != null)
            param1BasicTreeUI.setLeadSelectionPath(treePath1); 
          if (treePath2 != null && param1JTree.isVisible(treePath2))
            param1BasicTreeUI.setAnchorSelectionPath(treePath2); 
        } else {
          TreePath treePath1 = param1BasicTreeUI.getLeadSelectionPath();
          TreePath treePath2 = param1BasicTreeUI.getAnchorSelectionPath();
          param1JTree.clearSelection();
          param1BasicTreeUI.setAnchorSelectionPath(treePath2);
          param1BasicTreeUI.setLeadSelectionPath(treePath1);
        }  
    }
    
    private void startEditing(JTree param1JTree, BasicTreeUI param1BasicTreeUI) {
      TreePath treePath = param1BasicTreeUI.getLeadSelectionPath();
      int i = (treePath != null) ? param1BasicTreeUI.getRowForPath(param1JTree, treePath) : -1;
      if (i != -1)
        param1JTree.startEditingAtPath(treePath); 
    }
    
    private void cancelEditing(JTree param1JTree, BasicTreeUI param1BasicTreeUI) { param1JTree.cancelEditing(); }
    
    private void toggle(JTree param1JTree, BasicTreeUI param1BasicTreeUI) {
      int i = param1BasicTreeUI.getLeadSelectionRow();
      if (i != -1 && !param1BasicTreeUI.isLeaf(i)) {
        TreePath treePath1 = param1BasicTreeUI.getAnchorSelectionPath();
        TreePath treePath2 = param1BasicTreeUI.getLeadSelectionPath();
        param1BasicTreeUI.toggleExpandState(param1BasicTreeUI.getPathForRow(param1JTree, i));
        param1BasicTreeUI.setAnchorSelectionPath(treePath1);
        param1BasicTreeUI.setLeadSelectionPath(treePath2);
      } 
    }
    
    private void expand(JTree param1JTree, BasicTreeUI param1BasicTreeUI) {
      int i = param1BasicTreeUI.getLeadSelectionRow();
      param1JTree.expandRow(i);
    }
    
    private void collapse(JTree param1JTree, BasicTreeUI param1BasicTreeUI) {
      int i = param1BasicTreeUI.getLeadSelectionRow();
      param1JTree.collapseRow(i);
    }
    
    private void increment(JTree param1JTree, BasicTreeUI param1BasicTreeUI, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      if (!param1Boolean1 && !param1Boolean2 && param1JTree.getSelectionModel().getSelectionMode() != 4)
        param1Boolean2 = true; 
      int i;
      if (param1BasicTreeUI.treeSelectionModel != null && (i = param1JTree.getRowCount()) > 0) {
        int k;
        int j = param1BasicTreeUI.getLeadSelectionRow();
        if (j == -1) {
          if (param1Int == 1) {
            k = 0;
          } else {
            k = i - 1;
          } 
        } else {
          k = Math.min(i - 1, Math.max(0, j + param1Int));
        } 
        if (param1Boolean1 && param1BasicTreeUI.treeSelectionModel.getSelectionMode() != 1) {
          param1BasicTreeUI.extendSelection(param1JTree.getPathForRow(k));
        } else if (param1Boolean2) {
          param1JTree.setSelectionInterval(k, k);
        } else {
          param1BasicTreeUI.setLeadSelectionPath(param1JTree.getPathForRow(k), true);
        } 
        param1BasicTreeUI.ensureRowsAreVisible(k, k);
        param1BasicTreeUI.lastSelectedRow = k;
      } 
    }
    
    private void traverse(JTree param1JTree, BasicTreeUI param1BasicTreeUI, int param1Int, boolean param1Boolean) {
      if (!param1Boolean && param1JTree.getSelectionModel().getSelectionMode() != 4)
        param1Boolean = true; 
      int i;
      if ((i = param1JTree.getRowCount()) > 0) {
        byte b;
        int j = param1BasicTreeUI.getLeadSelectionRow();
        if (j == -1) {
          b = 0;
        } else if (param1Int == 1) {
          TreePath treePath = param1BasicTreeUI.getPathForRow(param1JTree, j);
          int k = param1JTree.getModel().getChildCount(treePath.getLastPathComponent());
          b = -1;
          if (!param1BasicTreeUI.isLeaf(j))
            if (!param1JTree.isExpanded(j)) {
              param1BasicTreeUI.toggleExpandState(treePath);
            } else if (k > 0) {
              b = Math.min(j + 1, i - 1);
            }  
        } else if (!param1BasicTreeUI.isLeaf(j) && param1JTree.isExpanded(j)) {
          param1BasicTreeUI.toggleExpandState(param1BasicTreeUI.getPathForRow(param1JTree, j));
          b = -1;
        } else {
          TreePath treePath = param1BasicTreeUI.getPathForRow(param1JTree, j);
          if (treePath != null && treePath.getPathCount() > 1) {
            b = param1BasicTreeUI.getRowForPath(param1JTree, treePath.getParentPath());
          } else {
            b = -1;
          } 
        } 
        if (b != -1) {
          if (param1Boolean) {
            param1JTree.setSelectionInterval(b, b);
          } else {
            param1BasicTreeUI.setLeadSelectionPath(param1BasicTreeUI.getPathForRow(param1JTree, b), true);
          } 
          param1BasicTreeUI.ensureRowsAreVisible(b, b);
        } 
      } 
    }
    
    private void moveSelectionToParent(JTree param1JTree, BasicTreeUI param1BasicTreeUI) {
      int i = param1BasicTreeUI.getLeadSelectionRow();
      TreePath treePath = param1BasicTreeUI.getPathForRow(param1JTree, i);
      if (treePath != null && treePath.getPathCount() > 1) {
        int j = param1BasicTreeUI.getRowForPath(param1JTree, treePath.getParentPath());
        if (j != -1) {
          param1JTree.setSelectionInterval(j, j);
          param1BasicTreeUI.ensureRowsAreVisible(j, j);
        } 
      } 
    }
    
    private void page(JTree param1JTree, BasicTreeUI param1BasicTreeUI, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      if (!param1Boolean1 && !param1Boolean2 && param1JTree.getSelectionModel().getSelectionMode() != 4)
        param1Boolean2 = true; 
      int i;
      if ((i = param1BasicTreeUI.getRowCount(param1JTree)) > 0 && param1BasicTreeUI.treeSelectionModel != null) {
        TreePath treePath2;
        Dimension dimension = param1JTree.getSize();
        TreePath treePath1 = param1BasicTreeUI.getLeadSelectionPath();
        Rectangle rectangle1 = param1JTree.getVisibleRect();
        if (param1Int == -1) {
          treePath2 = param1BasicTreeUI.getClosestPathForLocation(param1JTree, rectangle1.x, rectangle1.y);
          if (treePath2.equals(treePath1)) {
            rectangle1.y = Math.max(0, rectangle1.y - rectangle1.height);
            treePath2 = param1JTree.getClosestPathForLocation(rectangle1.x, rectangle1.y);
          } 
        } else {
          rectangle1.y = Math.min(dimension.height, rectangle1.y + rectangle1.height - 1);
          treePath2 = param1JTree.getClosestPathForLocation(rectangle1.x, rectangle1.y);
          if (treePath2.equals(treePath1)) {
            rectangle1.y = Math.min(dimension.height, rectangle1.y + rectangle1.height - 1);
            treePath2 = param1JTree.getClosestPathForLocation(rectangle1.x, rectangle1.y);
          } 
        } 
        Rectangle rectangle2 = param1BasicTreeUI.getPathBounds(param1JTree, treePath2);
        if (rectangle2 != null) {
          rectangle2.x = rectangle1.x;
          rectangle2.width = rectangle1.width;
          if (param1Int == -1) {
            rectangle2.height = rectangle1.height;
          } else {
            rectangle2.y -= rectangle1.height - rectangle2.height;
            rectangle2.height = rectangle1.height;
          } 
          if (param1Boolean1) {
            param1BasicTreeUI.extendSelection(treePath2);
          } else if (param1Boolean2) {
            param1JTree.setSelectionPath(treePath2);
          } else {
            param1BasicTreeUI.setLeadSelectionPath(treePath2, true);
          } 
          param1JTree.scrollRectToVisible(rectangle2);
        } 
      } 
    }
    
    private void home(JTree param1JTree, final BasicTreeUI ui, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      if (!param1Boolean1 && !param1Boolean2 && param1JTree.getSelectionModel().getSelectionMode() != 4)
        param1Boolean2 = true; 
      final int rowCount = param1BasicTreeUI.getRowCount(param1JTree);
      if (i > 0)
        if (param1Int == -1) {
          param1BasicTreeUI.ensureRowsAreVisible(0, 0);
          if (param1Boolean1) {
            TreePath treePath = param1BasicTreeUI.getAnchorSelectionPath();
            byte b = (treePath == null) ? -1 : param1BasicTreeUI.getRowForPath(param1JTree, treePath);
            if (b == -1) {
              param1JTree.setSelectionInterval(0, 0);
            } else {
              param1JTree.setSelectionInterval(0, b);
              param1BasicTreeUI.setAnchorSelectionPath(treePath);
              param1BasicTreeUI.setLeadSelectionPath(param1BasicTreeUI.getPathForRow(param1JTree, 0));
            } 
          } else if (param1Boolean2) {
            param1JTree.setSelectionInterval(0, 0);
          } else {
            param1BasicTreeUI.setLeadSelectionPath(param1BasicTreeUI.getPathForRow(param1JTree, 0), true);
          } 
        } else {
          param1BasicTreeUI.ensureRowsAreVisible(i - 1, i - 1);
          if (param1Boolean1) {
            TreePath treePath = param1BasicTreeUI.getAnchorSelectionPath();
            byte b = (treePath == null) ? -1 : param1BasicTreeUI.getRowForPath(param1JTree, treePath);
            if (b == -1) {
              param1JTree.setSelectionInterval(i - 1, i - 1);
            } else {
              param1JTree.setSelectionInterval(b, i - 1);
              param1BasicTreeUI.setAnchorSelectionPath(treePath);
              param1BasicTreeUI.setLeadSelectionPath(param1BasicTreeUI.getPathForRow(param1JTree, i - 1));
            } 
          } else if (param1Boolean2) {
            param1JTree.setSelectionInterval(i - 1, i - 1);
          } else {
            param1BasicTreeUI.setLeadSelectionPath(param1BasicTreeUI.getPathForRow(param1JTree, i - 1), true);
          } 
          if (param1BasicTreeUI.isLargeModel())
            SwingUtilities.invokeLater(new Runnable() {
                  public void run() { ui.ensureRowsAreVisible(rowCount - 1, rowCount - 1); }
                }); 
        }  
    }
  }
  
  public class CellEditorHandler implements CellEditorListener {
    public void editingStopped(ChangeEvent param1ChangeEvent) { BasicTreeUI.this.getHandler().editingStopped(param1ChangeEvent); }
    
    public void editingCanceled(ChangeEvent param1ChangeEvent) { BasicTreeUI.this.getHandler().editingCanceled(param1ChangeEvent); }
  }
  
  public class ComponentHandler extends ComponentAdapter implements ActionListener {
    protected Timer timer;
    
    protected JScrollBar scrollBar;
    
    public void componentMoved(ComponentEvent param1ComponentEvent) {
      if (this.timer == null) {
        JScrollPane jScrollPane = getScrollPane();
        if (jScrollPane == null) {
          BasicTreeUI.this.updateSize();
        } else {
          this.scrollBar = jScrollPane.getVerticalScrollBar();
          if (this.scrollBar == null || !this.scrollBar.getValueIsAdjusting()) {
            if ((this.scrollBar = jScrollPane.getHorizontalScrollBar()) != null && this.scrollBar.getValueIsAdjusting()) {
              startTimer();
            } else {
              BasicTreeUI.this.updateSize();
            } 
          } else {
            startTimer();
          } 
        } 
      } 
    }
    
    protected void startTimer() {
      if (this.timer == null) {
        this.timer = new Timer(200, this);
        this.timer.setRepeats(true);
      } 
      this.timer.start();
    }
    
    protected JScrollPane getScrollPane() {
      Container container;
      for (container = BasicTreeUI.this.tree.getParent(); container != null && !(container instanceof JScrollPane); container = container.getParent());
      return (container instanceof JScrollPane) ? (JScrollPane)container : null;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.scrollBar == null || !this.scrollBar.getValueIsAdjusting()) {
        if (this.timer != null)
          this.timer.stop(); 
        BasicTreeUI.this.updateSize();
        this.timer = null;
        this.scrollBar = null;
      } 
    }
  }
  
  public class FocusHandler implements FocusListener {
    public void focusGained(FocusEvent param1FocusEvent) { BasicTreeUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicTreeUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements CellEditorListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, PropertyChangeListener, TreeExpansionListener, TreeModelListener, TreeSelectionListener, DragRecognitionSupport.BeforeDrag {
    private String prefix = "";
    
    private String typedString = "";
    
    private long lastTime = 0L;
    
    private boolean dragPressDidSelection;
    
    private boolean dragStarted;
    
    private TreePath pressedPath;
    
    private MouseEvent pressedEvent;
    
    private boolean valueChangedOnPress;
    
    private Handler() {}
    
    public void keyTyped(KeyEvent param1KeyEvent) {
      if (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.getRowCount() > 0 && BasicTreeUI.this.tree.hasFocus() && BasicTreeUI.this.tree.isEnabled()) {
        if (param1KeyEvent.isAltDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(param1KeyEvent) || isNavigationKey(param1KeyEvent))
          return; 
        boolean bool = true;
        char c = param1KeyEvent.getKeyChar();
        long l = param1KeyEvent.getWhen();
        int i = BasicTreeUI.this.tree.getLeadSelectionRow();
        if (l - this.lastTime < BasicTreeUI.this.timeFactor) {
          this.typedString += c;
          if (this.prefix.length() == 1 && c == this.prefix.charAt(0)) {
            i++;
          } else {
            this.prefix = this.typedString;
          } 
        } else {
          i++;
          this.typedString = "" + c;
          this.prefix = this.typedString;
        } 
        this.lastTime = l;
        if (i < 0 || i >= BasicTreeUI.this.tree.getRowCount()) {
          bool = false;
          i = 0;
        } 
        TreePath treePath = BasicTreeUI.this.tree.getNextMatch(this.prefix, i, Position.Bias.Forward);
        if (treePath != null) {
          BasicTreeUI.this.tree.setSelectionPath(treePath);
          int j = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, treePath);
          BasicTreeUI.this.ensureRowsAreVisible(j, j);
        } else if (bool) {
          treePath = BasicTreeUI.this.tree.getNextMatch(this.prefix, 0, Position.Bias.Forward);
          if (treePath != null) {
            BasicTreeUI.this.tree.setSelectionPath(treePath);
            int j = BasicTreeUI.this.getRowForPath(BasicTreeUI.this.tree, treePath);
            BasicTreeUI.this.ensureRowsAreVisible(j, j);
          } 
        } 
      } 
    }
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (BasicTreeUI.this.tree != null && isNavigationKey(param1KeyEvent)) {
        this.prefix = "";
        this.typedString = "";
        this.lastTime = 0L;
      } 
    }
    
    public void keyReleased(KeyEvent param1KeyEvent) {}
    
    private boolean isNavigationKey(KeyEvent param1KeyEvent) {
      InputMap inputMap = BasicTreeUI.this.tree.getInputMap(1);
      KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(param1KeyEvent);
      return (inputMap != null && inputMap.get(keyStroke) != null);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getSource() == BasicTreeUI.this.treeSelectionModel) {
        BasicTreeUI.this.treeSelectionModel.resetRowSelection();
      } else if (param1PropertyChangeEvent.getSource() == BasicTreeUI.this.tree) {
        String str = param1PropertyChangeEvent.getPropertyName();
        if (str == "leadSelectionPath") {
          if (!BasicTreeUI.this.ignoreLAChange) {
            BasicTreeUI.this.updateLeadSelectionRow();
            BasicTreeUI.this.repaintPath((TreePath)param1PropertyChangeEvent.getOldValue());
            BasicTreeUI.this.repaintPath((TreePath)param1PropertyChangeEvent.getNewValue());
          } 
        } else if (str == "anchorSelectionPath" && !BasicTreeUI.this.ignoreLAChange) {
          BasicTreeUI.this.repaintPath((TreePath)param1PropertyChangeEvent.getOldValue());
          BasicTreeUI.this.repaintPath((TreePath)param1PropertyChangeEvent.getNewValue());
        } 
        if (str == "cellRenderer") {
          BasicTreeUI.this.setCellRenderer((TreeCellRenderer)param1PropertyChangeEvent.getNewValue());
          BasicTreeUI.this.redoTheLayout();
        } else if (str == "model") {
          BasicTreeUI.this.setModel((TreeModel)param1PropertyChangeEvent.getNewValue());
        } else if (str == "rootVisible") {
          BasicTreeUI.this.setRootVisible(((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue());
        } else if (str == "showsRootHandles") {
          BasicTreeUI.this.setShowsRootHandles(((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue());
        } else if (str == "rowHeight") {
          BasicTreeUI.this.setRowHeight(((Integer)param1PropertyChangeEvent.getNewValue()).intValue());
        } else if (str == "cellEditor") {
          BasicTreeUI.this.setCellEditor((TreeCellEditor)param1PropertyChangeEvent.getNewValue());
        } else if (str == "editable") {
          BasicTreeUI.this.setEditable(((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue());
        } else if (str == "largeModel") {
          BasicTreeUI.this.setLargeModel(BasicTreeUI.this.tree.isLargeModel());
        } else if (str == "selectionModel") {
          BasicTreeUI.this.setSelectionModel(BasicTreeUI.this.tree.getSelectionModel());
        } else if (str == "font") {
          BasicTreeUI.this.completeEditing();
          if (BasicTreeUI.this.treeState != null)
            BasicTreeUI.this.treeState.invalidateSizes(); 
          BasicTreeUI.this.updateSize();
        } else if (str == "componentOrientation") {
          if (BasicTreeUI.this.tree != null) {
            BasicTreeUI.this.leftToRight = BasicGraphicsUtils.isLeftToRight(BasicTreeUI.this.tree);
            BasicTreeUI.this.redoTheLayout();
            BasicTreeUI.this.tree.treeDidChange();
            InputMap inputMap = BasicTreeUI.this.getInputMap(0);
            SwingUtilities.replaceUIInputMap(BasicTreeUI.this.tree, 0, inputMap);
          } 
        } else if ("dropLocation" == str) {
          JTree.DropLocation dropLocation = (JTree.DropLocation)param1PropertyChangeEvent.getOldValue();
          repaintDropLocation(dropLocation);
          repaintDropLocation(BasicTreeUI.this.tree.getDropLocation());
        } 
      } 
    }
    
    private void repaintDropLocation(JTree.DropLocation param1DropLocation) {
      Rectangle rectangle;
      if (param1DropLocation == null)
        return; 
      if (BasicTreeUI.this.isDropLine(param1DropLocation)) {
        rectangle = BasicTreeUI.this.getDropLineRect(param1DropLocation);
      } else {
        rectangle = BasicTreeUI.this.tree.getPathBounds(param1DropLocation.getPath());
      } 
      if (rectangle != null)
        BasicTreeUI.this.tree.repaint(rectangle); 
    }
    
    private boolean isActualPath(TreePath param1TreePath, int param1Int1, int param1Int2) {
      if (param1TreePath == null)
        return false; 
      Rectangle rectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, param1TreePath);
      return (rectangle == null || param1Int2 > rectangle.y + rectangle.height) ? false : ((param1Int1 >= rectangle.x && param1Int1 <= rectangle.x + rectangle.width));
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicTreeUI.this.tree))
        return; 
      if (BasicTreeUI.this.isEditing(BasicTreeUI.this.tree) && BasicTreeUI.this.tree.getInvokesStopCellEditing() && !BasicTreeUI.this.stopEditing(BasicTreeUI.this.tree))
        return; 
      BasicTreeUI.this.completeEditing();
      this.pressedPath = BasicTreeUI.this.getClosestPathForLocation(BasicTreeUI.this.tree, param1MouseEvent.getX(), param1MouseEvent.getY());
      if (BasicTreeUI.this.tree.getDragEnabled()) {
        mousePressedDND(param1MouseEvent);
      } else {
        SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
        handleSelection(param1MouseEvent);
      } 
    }
    
    private void mousePressedDND(MouseEvent param1MouseEvent) {
      this.pressedEvent = param1MouseEvent;
      boolean bool = true;
      this.dragStarted = false;
      this.valueChangedOnPress = false;
      if (isActualPath(this.pressedPath, param1MouseEvent.getX(), param1MouseEvent.getY()) && DragRecognitionSupport.mousePressed(param1MouseEvent)) {
        this.dragPressDidSelection = false;
        if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent))
          return; 
        if (!param1MouseEvent.isShiftDown() && BasicTreeUI.this.tree.isPathSelected(this.pressedPath)) {
          BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
          BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
          return;
        } 
        this.dragPressDidSelection = true;
        bool = false;
      } 
      if (bool)
        SwingUtilities2.adjustFocus(BasicTreeUI.this.tree); 
      handleSelection(param1MouseEvent);
    }
    
    void handleSelection(MouseEvent param1MouseEvent) {
      if (this.pressedPath != null) {
        Rectangle rectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, this.pressedPath);
        if (rectangle == null || param1MouseEvent.getY() >= rectangle.y + rectangle.height)
          return; 
        if (SwingUtilities.isLeftMouseButton(param1MouseEvent))
          BasicTreeUI.this.checkForClickInExpandControl(this.pressedPath, param1MouseEvent.getX(), param1MouseEvent.getY()); 
        int i = param1MouseEvent.getX();
        if (i >= rectangle.x && i < rectangle.x + rectangle.width && (BasicTreeUI.this.tree.getDragEnabled() || !BasicTreeUI.this.startEditing(this.pressedPath, param1MouseEvent)))
          BasicTreeUI.this.selectPathForEvent(this.pressedPath, param1MouseEvent); 
      } 
    }
    
    public void dragStarting(MouseEvent param1MouseEvent) {
      this.dragStarted = true;
      if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent)) {
        BasicTreeUI.this.tree.addSelectionPath(this.pressedPath);
        BasicTreeUI.this.setAnchorSelectionPath(this.pressedPath);
        BasicTreeUI.this.setLeadSelectionPath(this.pressedPath, true);
      } 
      this.pressedEvent = null;
      this.pressedPath = null;
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicTreeUI.this.tree))
        return; 
      if (BasicTreeUI.this.tree.getDragEnabled())
        DragRecognitionSupport.mouseDragged(param1MouseEvent, this); 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicTreeUI.this.tree))
        return; 
      if (BasicTreeUI.this.tree.getDragEnabled())
        mouseReleasedDND(param1MouseEvent); 
      this.pressedEvent = null;
      this.pressedPath = null;
    }
    
    private void mouseReleasedDND(MouseEvent param1MouseEvent) {
      MouseEvent mouseEvent = DragRecognitionSupport.mouseReleased(param1MouseEvent);
      if (mouseEvent != null) {
        SwingUtilities2.adjustFocus(BasicTreeUI.this.tree);
        if (!this.dragPressDidSelection)
          handleSelection(mouseEvent); 
      } 
      if (!this.dragStarted && this.pressedPath != null && !this.valueChangedOnPress && isActualPath(this.pressedPath, this.pressedEvent.getX(), this.pressedEvent.getY()))
        BasicTreeUI.this.startEditingOnRelease(this.pressedPath, this.pressedEvent, param1MouseEvent); 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {
      if (BasicTreeUI.this.tree != null) {
        Rectangle rectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.tree.getLeadSelectionPath());
        if (rectangle != null)
          BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(rectangle)); 
        rectangle = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, BasicTreeUI.this.getLeadSelectionPath());
        if (rectangle != null)
          BasicTreeUI.this.tree.repaint(BasicTreeUI.this.getRepaintPathBounds(rectangle)); 
      } 
    }
    
    public void focusLost(FocusEvent param1FocusEvent) { focusGained(param1FocusEvent); }
    
    public void editingStopped(ChangeEvent param1ChangeEvent) { BasicTreeUI.this.completeEditing(false, false, true); }
    
    public void editingCanceled(ChangeEvent param1ChangeEvent) { BasicTreeUI.this.completeEditing(false, false, false); }
    
    public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) {
      this.valueChangedOnPress = true;
      BasicTreeUI.this.completeEditing();
      if (BasicTreeUI.this.tree.getExpandsSelectedPaths() && BasicTreeUI.this.treeSelectionModel != null) {
        TreePath[] arrayOfTreePath1 = BasicTreeUI.this.treeSelectionModel.getSelectionPaths();
        if (arrayOfTreePath1 != null)
          for (int j = arrayOfTreePath1.length - 1; j >= 0; j--) {
            TreePath treePath = arrayOfTreePath1[j].getParentPath();
            boolean bool1 = true;
            while (treePath != null) {
              if (BasicTreeUI.this.treeModel.isLeaf(treePath.getLastPathComponent())) {
                bool1 = false;
                treePath = null;
                continue;
              } 
              treePath = treePath.getParentPath();
            } 
            if (bool1)
              BasicTreeUI.this.tree.makeVisible(arrayOfTreePath1[j]); 
          }  
      } 
      TreePath treePath1 = BasicTreeUI.this.getLeadSelectionPath();
      BasicTreeUI.this.lastSelectedRow = BasicTreeUI.this.tree.getMinSelectionRow();
      TreePath treePath2 = BasicTreeUI.this.tree.getSelectionModel().getLeadSelectionPath();
      BasicTreeUI.this.setAnchorSelectionPath(treePath2);
      BasicTreeUI.this.setLeadSelectionPath(treePath2);
      TreePath[] arrayOfTreePath = param1TreeSelectionEvent.getPaths();
      Rectangle rectangle = BasicTreeUI.this.tree.getVisibleRect();
      boolean bool = true;
      int i = BasicTreeUI.this.tree.getWidth();
      if (arrayOfTreePath != null) {
        int j = arrayOfTreePath.length;
        if (j > 4) {
          BasicTreeUI.this.tree.repaint();
          bool = false;
        } else {
          for (byte b = 0; b < j; b++) {
            Rectangle rectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, arrayOfTreePath[b]);
            if (rectangle1 != null && rectangle.intersects(rectangle1))
              BasicTreeUI.this.tree.repaint(0, rectangle1.y, i, rectangle1.height); 
          } 
        } 
      } 
      if (bool) {
        Rectangle rectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, treePath1);
        if (rectangle1 != null && rectangle.intersects(rectangle1))
          BasicTreeUI.this.tree.repaint(0, rectangle1.y, i, rectangle1.height); 
        rectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, treePath2);
        if (rectangle1 != null && rectangle.intersects(rectangle1))
          BasicTreeUI.this.tree.repaint(0, rectangle1.y, i, rectangle1.height); 
      } 
    }
    
    public void treeExpanded(TreeExpansionEvent param1TreeExpansionEvent) {
      if (param1TreeExpansionEvent != null && BasicTreeUI.this.tree != null) {
        TreePath treePath = param1TreeExpansionEvent.getPath();
        BasicTreeUI.this.updateExpandedDescendants(treePath);
      } 
    }
    
    public void treeCollapsed(TreeExpansionEvent param1TreeExpansionEvent) {
      if (param1TreeExpansionEvent != null && BasicTreeUI.this.tree != null) {
        TreePath treePath = param1TreeExpansionEvent.getPath();
        BasicTreeUI.this.completeEditing();
        if (treePath != null && BasicTreeUI.this.tree.isVisible(treePath)) {
          BasicTreeUI.this.treeState.setExpandedState(treePath, false);
          BasicTreeUI.this.updateLeadSelectionRow();
          BasicTreeUI.this.updateSize();
        } 
      } 
    }
    
    public void treeNodesChanged(TreeModelEvent param1TreeModelEvent) {
      if (BasicTreeUI.this.treeState != null && param1TreeModelEvent != null) {
        TreePath treePath = SwingUtilities2.getTreePath(param1TreeModelEvent, BasicTreeUI.this.getModel());
        int[] arrayOfInt = param1TreeModelEvent.getChildIndices();
        if (arrayOfInt == null || arrayOfInt.length == 0) {
          BasicTreeUI.this.treeState.treeNodesChanged(param1TreeModelEvent);
          BasicTreeUI.this.updateSize();
        } else if (BasicTreeUI.this.treeState.isExpanded(treePath)) {
          int i = arrayOfInt[0];
          for (int j = arrayOfInt.length - 1; j > 0; j--)
            i = Math.min(arrayOfInt[j], i); 
          Object object = BasicTreeUI.this.treeModel.getChild(treePath.getLastPathComponent(), i);
          TreePath treePath1 = treePath.pathByAddingChild(object);
          Rectangle rectangle1 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, treePath1);
          BasicTreeUI.this.treeState.treeNodesChanged(param1TreeModelEvent);
          BasicTreeUI.this.updateSize0();
          Rectangle rectangle2 = BasicTreeUI.this.getPathBounds(BasicTreeUI.this.tree, treePath1);
          if (rectangle1 == null || rectangle2 == null)
            return; 
          if (arrayOfInt.length == 1 && rectangle2.height == rectangle1.height) {
            BasicTreeUI.this.tree.repaint(0, rectangle1.y, BasicTreeUI.this.tree.getWidth(), rectangle1.height);
          } else {
            BasicTreeUI.this.tree.repaint(0, rectangle1.y, BasicTreeUI.this.tree.getWidth(), BasicTreeUI.this.tree.getHeight() - rectangle1.y);
          } 
        } else {
          BasicTreeUI.this.treeState.treeNodesChanged(param1TreeModelEvent);
        } 
      } 
    }
    
    public void treeNodesInserted(TreeModelEvent param1TreeModelEvent) {
      if (BasicTreeUI.this.treeState != null && param1TreeModelEvent != null) {
        BasicTreeUI.this.treeState.treeNodesInserted(param1TreeModelEvent);
        BasicTreeUI.this.updateLeadSelectionRow();
        TreePath treePath = SwingUtilities2.getTreePath(param1TreeModelEvent, BasicTreeUI.this.getModel());
        if (BasicTreeUI.this.treeState.isExpanded(treePath)) {
          BasicTreeUI.this.updateSize();
        } else {
          int[] arrayOfInt = param1TreeModelEvent.getChildIndices();
          int i = BasicTreeUI.this.treeModel.getChildCount(treePath.getLastPathComponent());
          if (arrayOfInt != null && i - arrayOfInt.length == 0)
            BasicTreeUI.this.updateSize(); 
        } 
      } 
    }
    
    public void treeNodesRemoved(TreeModelEvent param1TreeModelEvent) {
      if (BasicTreeUI.this.treeState != null && param1TreeModelEvent != null) {
        BasicTreeUI.this.treeState.treeNodesRemoved(param1TreeModelEvent);
        BasicTreeUI.this.updateLeadSelectionRow();
        TreePath treePath = SwingUtilities2.getTreePath(param1TreeModelEvent, BasicTreeUI.this.getModel());
        if (BasicTreeUI.this.treeState.isExpanded(treePath) || BasicTreeUI.this.treeModel.getChildCount(treePath.getLastPathComponent()) == 0)
          BasicTreeUI.this.updateSize(); 
      } 
    }
    
    public void treeStructureChanged(TreeModelEvent param1TreeModelEvent) {
      if (BasicTreeUI.this.treeState != null && param1TreeModelEvent != null) {
        BasicTreeUI.this.treeState.treeStructureChanged(param1TreeModelEvent);
        BasicTreeUI.this.updateLeadSelectionRow();
        TreePath treePath = SwingUtilities2.getTreePath(param1TreeModelEvent, BasicTreeUI.this.getModel());
        if (treePath != null)
          treePath = treePath.getParentPath(); 
        if (treePath == null || BasicTreeUI.this.treeState.isExpanded(treePath))
          BasicTreeUI.this.updateSize(); 
      } 
    }
  }
  
  public class KeyHandler extends KeyAdapter {
    protected Action repeatKeyAction;
    
    protected boolean isKeyDown;
    
    public void keyTyped(KeyEvent param1KeyEvent) { BasicTreeUI.this.getHandler().keyTyped(param1KeyEvent); }
    
    public void keyPressed(KeyEvent param1KeyEvent) { BasicTreeUI.this.getHandler().keyPressed(param1KeyEvent); }
    
    public void keyReleased(KeyEvent param1KeyEvent) { BasicTreeUI.this.getHandler().keyReleased(param1KeyEvent); }
  }
  
  public class MouseHandler extends MouseAdapter implements MouseMotionListener {
    public void mousePressed(MouseEvent param1MouseEvent) { BasicTreeUI.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicTreeUI.this.getHandler().mouseDragged(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicTreeUI.this.getHandler().mouseMoved(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicTreeUI.this.getHandler().mouseReleased(param1MouseEvent); }
  }
  
  public class MouseInputHandler implements MouseInputListener {
    protected Component source;
    
    protected Component destination;
    
    private Component focusComponent;
    
    private boolean dispatchedEvent;
    
    public MouseInputHandler(BasicTreeUI this$0, Component param1Component1, Component param1Component2, MouseEvent param1MouseEvent) { this(param1Component1, param1Component2, param1MouseEvent, null); }
    
    MouseInputHandler(Component param1Component1, Component param1Component2, MouseEvent param1MouseEvent, Component param1Component3) {
      this.source = param1Component1;
      this.destination = param1Component2;
      this.source.addMouseListener(this);
      this.source.addMouseMotionListener(this);
      SwingUtilities2.setSkipClickCount(param1Component2, param1MouseEvent.getClickCount() - 1);
      param1Component2.dispatchEvent(SwingUtilities.convertMouseEvent(param1Component1, param1MouseEvent, param1Component2));
      this.focusComponent = param1Component3;
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      if (this.destination != null) {
        this.dispatchedEvent = true;
        this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, param1MouseEvent, this.destination));
      } 
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (this.destination != null)
        this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, param1MouseEvent, this.destination)); 
      removeFromSource();
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      if (!SwingUtilities.isLeftMouseButton(param1MouseEvent))
        removeFromSource(); 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      if (!SwingUtilities.isLeftMouseButton(param1MouseEvent))
        removeFromSource(); 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (this.destination != null) {
        this.dispatchedEvent = true;
        this.destination.dispatchEvent(SwingUtilities.convertMouseEvent(this.source, param1MouseEvent, this.destination));
      } 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { removeFromSource(); }
    
    protected void removeFromSource() {
      if (this.source != null) {
        this.source.removeMouseListener(this);
        this.source.removeMouseMotionListener(this);
        if (this.focusComponent != null && this.focusComponent == this.destination && !this.dispatchedEvent && this.focusComponent instanceof JTextField)
          ((JTextField)this.focusComponent).selectAll(); 
      } 
      this.source = this.destination = null;
    }
  }
  
  public class NodeDimensionsHandler extends AbstractLayoutCache.NodeDimensions {
    public Rectangle getNodeDimensions(Object param1Object, int param1Int1, int param1Int2, boolean param1Boolean, Rectangle param1Rectangle) {
      if (BasicTreeUI.this.editingComponent != null && BasicTreeUI.this.editingRow == param1Int1) {
        Dimension dimension = BasicTreeUI.this.editingComponent.getPreferredSize();
        int i = BasicTreeUI.this.getRowHeight();
        if (i > 0 && i != dimension.height)
          dimension.height = i; 
        if (param1Rectangle != null) {
          param1Rectangle.x = getRowX(param1Int1, param1Int2);
          param1Rectangle.width = dimension.width;
          param1Rectangle.height = dimension.height;
        } else {
          param1Rectangle = new Rectangle(getRowX(param1Int1, param1Int2), 0, dimension.width, dimension.height);
        } 
        return param1Rectangle;
      } 
      if (BasicTreeUI.this.currentCellRenderer != null) {
        Component component = BasicTreeUI.this.currentCellRenderer.getTreeCellRendererComponent(BasicTreeUI.this.tree, param1Object, BasicTreeUI.this.tree.isRowSelected(param1Int1), param1Boolean, BasicTreeUI.this.treeModel.isLeaf(param1Object), param1Int1, false);
        if (BasicTreeUI.this.tree != null) {
          BasicTreeUI.this.rendererPane.add(component);
          component.validate();
        } 
        Dimension dimension = component.getPreferredSize();
        if (param1Rectangle != null) {
          param1Rectangle.x = getRowX(param1Int1, param1Int2);
          param1Rectangle.width = dimension.width;
          param1Rectangle.height = dimension.height;
        } else {
          param1Rectangle = new Rectangle(getRowX(param1Int1, param1Int2), 0, dimension.width, dimension.height);
        } 
        return param1Rectangle;
      } 
      return null;
    }
    
    protected int getRowX(int param1Int1, int param1Int2) { return BasicTreeUI.this.getRowX(param1Int1, param1Int2); }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicTreeUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  public class SelectionModelPropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicTreeUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  public class TreeCancelEditingAction extends AbstractAction {
    public TreeCancelEditingAction(String param1String) {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicTreeUI.this.tree != null)
        SHARED_ACTION.cancelEditing(BasicTreeUI.this.tree, BasicTreeUI.this); 
    }
    
    public boolean isEnabled() { return (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled() && BasicTreeUI.this.isEditing(BasicTreeUI.this.tree)); }
  }
  
  public class TreeExpansionHandler implements TreeExpansionListener {
    public void treeExpanded(TreeExpansionEvent param1TreeExpansionEvent) { BasicTreeUI.this.getHandler().treeExpanded(param1TreeExpansionEvent); }
    
    public void treeCollapsed(TreeExpansionEvent param1TreeExpansionEvent) { BasicTreeUI.this.getHandler().treeCollapsed(param1TreeExpansionEvent); }
  }
  
  public class TreeHomeAction extends AbstractAction {
    protected int direction;
    
    private boolean addToSelection;
    
    private boolean changeSelection;
    
    public TreeHomeAction(BasicTreeUI this$0, int param1Int, String param1String) { this(param1Int, param1String, false, true); }
    
    private TreeHomeAction(int param1Int, String param1String, boolean param1Boolean1, boolean param1Boolean2) {
      this.direction = param1Int;
      this.changeSelection = param1Boolean2;
      this.addToSelection = param1Boolean1;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicTreeUI.this.tree != null)
        SHARED_ACTION.home(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection); 
    }
    
    public boolean isEnabled() { return (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled()); }
  }
  
  public class TreeIncrementAction extends AbstractAction {
    protected int direction;
    
    private boolean addToSelection;
    
    private boolean changeSelection;
    
    public TreeIncrementAction(BasicTreeUI this$0, int param1Int, String param1String) { this(param1Int, param1String, false, true); }
    
    private TreeIncrementAction(int param1Int, String param1String, boolean param1Boolean1, boolean param1Boolean2) {
      this.direction = param1Int;
      this.addToSelection = param1Boolean1;
      this.changeSelection = param1Boolean2;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicTreeUI.this.tree != null)
        SHARED_ACTION.increment(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection); 
    }
    
    public boolean isEnabled() { return (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled()); }
  }
  
  public class TreeModelHandler implements TreeModelListener {
    public void treeNodesChanged(TreeModelEvent param1TreeModelEvent) { BasicTreeUI.this.getHandler().treeNodesChanged(param1TreeModelEvent); }
    
    public void treeNodesInserted(TreeModelEvent param1TreeModelEvent) { BasicTreeUI.this.getHandler().treeNodesInserted(param1TreeModelEvent); }
    
    public void treeNodesRemoved(TreeModelEvent param1TreeModelEvent) { BasicTreeUI.this.getHandler().treeNodesRemoved(param1TreeModelEvent); }
    
    public void treeStructureChanged(TreeModelEvent param1TreeModelEvent) { BasicTreeUI.this.getHandler().treeStructureChanged(param1TreeModelEvent); }
  }
  
  public class TreePageAction extends AbstractAction {
    protected int direction;
    
    private boolean addToSelection;
    
    private boolean changeSelection;
    
    public TreePageAction(BasicTreeUI this$0, int param1Int, String param1String) { this(param1Int, param1String, false, true); }
    
    private TreePageAction(int param1Int, String param1String, boolean param1Boolean1, boolean param1Boolean2) {
      this.direction = param1Int;
      this.addToSelection = param1Boolean1;
      this.changeSelection = param1Boolean2;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicTreeUI.this.tree != null)
        SHARED_ACTION.page(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.addToSelection, this.changeSelection); 
    }
    
    public boolean isEnabled() { return (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled()); }
  }
  
  public class TreeSelectionHandler implements TreeSelectionListener {
    public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) { BasicTreeUI.this.getHandler().valueChanged(param1TreeSelectionEvent); }
  }
  
  public class TreeToggleAction extends AbstractAction {
    public TreeToggleAction(String param1String) {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicTreeUI.this.tree != null)
        SHARED_ACTION.toggle(BasicTreeUI.this.tree, BasicTreeUI.this); 
    }
    
    public boolean isEnabled() { return (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled()); }
  }
  
  static class TreeTransferHandler extends TransferHandler implements UIResource, Comparator<TreePath> {
    private JTree tree;
    
    protected Transferable createTransferable(JComponent param1JComponent) {
      if (param1JComponent instanceof JTree) {
        this.tree = (JTree)param1JComponent;
        TreePath[] arrayOfTreePath1 = this.tree.getSelectionPaths();
        if (arrayOfTreePath1 == null || arrayOfTreePath1.length == 0)
          return null; 
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("<html>\n<body>\n<ul>\n");
        TreeModel treeModel = this.tree.getModel();
        Object object = null;
        TreePath[] arrayOfTreePath2 = getDisplayOrderPaths(arrayOfTreePath1);
        for (TreePath treePath : arrayOfTreePath2) {
          Object object1 = treePath.getLastPathComponent();
          boolean bool = treeModel.isLeaf(object1);
          String str = getDisplayString(treePath, true, bool);
          stringBuffer1.append(str + "\n");
          stringBuffer2.append("  <li>" + str + "\n");
        } 
        stringBuffer1.deleteCharAt(stringBuffer1.length() - 1);
        stringBuffer2.append("</ul>\n</body>\n</html>");
        this.tree = null;
        return new BasicTransferable(stringBuffer1.toString(), stringBuffer2.toString());
      } 
      return null;
    }
    
    public int compare(TreePath param1TreePath1, TreePath param1TreePath2) {
      int i = this.tree.getRowForPath(param1TreePath1);
      int j = this.tree.getRowForPath(param1TreePath2);
      return i - j;
    }
    
    String getDisplayString(TreePath param1TreePath, boolean param1Boolean1, boolean param1Boolean2) {
      int i = this.tree.getRowForPath(param1TreePath);
      boolean bool = (this.tree.getLeadSelectionRow() == i);
      Object object = param1TreePath.getLastPathComponent();
      return this.tree.convertValueToText(object, param1Boolean1, this.tree.isExpanded(i), param1Boolean2, i, bool);
    }
    
    TreePath[] getDisplayOrderPaths(TreePath[] param1ArrayOfTreePath) {
      ArrayList arrayList = new ArrayList();
      for (TreePath treePath : param1ArrayOfTreePath)
        arrayList.add(treePath); 
      Collections.sort(arrayList, this);
      int i = arrayList.size();
      TreePath[] arrayOfTreePath = new TreePath[i];
      for (byte b = 0; b < i; b++)
        arrayOfTreePath[b] = (TreePath)arrayList.get(b); 
      return arrayOfTreePath;
    }
    
    public int getSourceActions(JComponent param1JComponent) { return 1; }
  }
  
  public class TreeTraverseAction extends AbstractAction {
    protected int direction;
    
    private boolean changeSelection;
    
    public TreeTraverseAction(BasicTreeUI this$0, int param1Int, String param1String) { this(param1Int, param1String, true); }
    
    private TreeTraverseAction(int param1Int, String param1String, boolean param1Boolean) {
      this.direction = param1Int;
      this.changeSelection = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicTreeUI.this.tree != null)
        SHARED_ACTION.traverse(BasicTreeUI.this.tree, BasicTreeUI.this, this.direction, this.changeSelection); 
    }
    
    public boolean isEnabled() { return (BasicTreeUI.this.tree != null && BasicTreeUI.this.tree.isEnabled()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */