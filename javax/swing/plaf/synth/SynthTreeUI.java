package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sun.swing.plaf.synth.SynthIcon;

public class SynthTreeUI extends BasicTreeUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private SynthStyle cellStyle;
  
  private SynthContext paintContext;
  
  private boolean drawHorizontalLines;
  
  private boolean drawVerticalLines;
  
  private Object linesStyle;
  
  private int padding;
  
  private boolean useTreeColors;
  
  private Icon expandedIconWrapper = new ExpandedIconWrapper(null);
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTreeUI(); }
  
  public Icon getExpandedIcon() { return this.expandedIconWrapper; }
  
  protected void installDefaults() { updateStyle(this.tree); }
  
  private void updateStyle(JTree paramJTree) {
    SynthContext synthContext = getContext(paramJTree, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      setExpandedIcon(this.style.getIcon(synthContext, "Tree.expandedIcon"));
      setCollapsedIcon(this.style.getIcon(synthContext, "Tree.collapsedIcon"));
      setLeftChildIndent(this.style.getInt(synthContext, "Tree.leftChildIndent", 0));
      setRightChildIndent(this.style.getInt(synthContext, "Tree.rightChildIndent", 0));
      this.drawHorizontalLines = this.style.getBoolean(synthContext, "Tree.drawHorizontalLines", true);
      this.drawVerticalLines = this.style.getBoolean(synthContext, "Tree.drawVerticalLines", true);
      this.linesStyle = this.style.get(synthContext, "Tree.linesStyle");
      Object object = this.style.get(synthContext, "Tree.rowHeight");
      if (object != null)
        LookAndFeel.installProperty(paramJTree, "rowHeight", object); 
      object = this.style.get(synthContext, "Tree.scrollsOnExpand");
      LookAndFeel.installProperty(paramJTree, "scrollsOnExpand", (object != null) ? object : Boolean.TRUE);
      this.padding = this.style.getInt(synthContext, "Tree.padding", 0);
      this.largeModel = (paramJTree.isLargeModel() && paramJTree.getRowHeight() > 0);
      this.useTreeColors = this.style.getBoolean(synthContext, "Tree.rendererUseTreeColors", true);
      Boolean bool = Boolean.valueOf(this.style.getBoolean(synthContext, "Tree.showsRootHandles", Boolean.TRUE.booleanValue()));
      LookAndFeel.installProperty(paramJTree, "showsRootHandles", bool);
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
    synthContext = getContext(paramJTree, Region.TREE_CELL, 1);
    this.cellStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected void installListeners() {
    super.installListeners();
    this.tree.addPropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion) { return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion)); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) { return SynthContext.getContext(paramJComponent, paramRegion, this.cellStyle, paramInt); }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion) { return 513; }
  
  protected TreeCellEditor createDefaultCellEditor() {
    SynthTreeCellEditor synthTreeCellEditor;
    TreeCellRenderer treeCellRenderer = this.tree.getCellRenderer();
    if (treeCellRenderer != null && treeCellRenderer instanceof DefaultTreeCellRenderer) {
      synthTreeCellEditor = new SynthTreeCellEditor(this.tree, (DefaultTreeCellRenderer)treeCellRenderer);
    } else {
      synthTreeCellEditor = new SynthTreeCellEditor(this.tree, null);
    } 
    return synthTreeCellEditor;
  }
  
  protected TreeCellRenderer createDefaultCellRenderer() { return new SynthTreeCellRenderer(); }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.tree, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    synthContext = getContext(this.tree, Region.TREE_CELL, 1);
    this.cellStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.cellStyle = null;
    if (this.tree.getTransferHandler() instanceof UIResource)
      this.tree.setTransferHandler(null); 
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.tree.removePropertyChangeListener(this);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintTreeBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTreeBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    this.paintContext = paramSynthContext;
    updateLeadSelectionRow();
    Rectangle rectangle = paramGraphics.getClipBounds();
    Insets insets = this.tree.getInsets();
    TreePath treePath = getClosestPathForLocation(this.tree, 0, rectangle.y);
    Enumeration enumeration = this.treeState.getVisiblePathsFrom(treePath);
    int i = this.treeState.getRowForPath(treePath);
    int j = rectangle.y + rectangle.height;
    TreeModel treeModel = this.tree.getModel();
    SynthContext synthContext = getContext(this.tree, Region.TREE_CELL);
    this.drawingCache.clear();
    setHashColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.FOREGROUND));
    if (enumeration != null) {
      boolean bool = false;
      Rectangle rectangle1 = new Rectangle(0, 0, this.tree.getWidth(), 0);
      TreeCellRenderer treeCellRenderer = this.tree.getCellRenderer();
      DefaultTreeCellRenderer defaultTreeCellRenderer = (treeCellRenderer instanceof DefaultTreeCellRenderer) ? (DefaultTreeCellRenderer)treeCellRenderer : null;
      configureRenderer(synthContext);
      while (!bool && enumeration.hasMoreElements()) {
        TreePath treePath2 = (TreePath)enumeration.nextElement();
        Rectangle rectangle2 = getPathBounds(this.tree, treePath2);
        if (treePath2 != null && rectangle2 != null) {
          boolean bool3;
          boolean bool2;
          boolean bool4 = treeModel.isLeaf(treePath2.getLastPathComponent());
          if (bool4) {
            bool2 = bool3 = false;
          } else {
            bool2 = this.treeState.getExpandedState(treePath2);
            bool3 = this.tree.hasBeenExpanded(treePath2);
          } 
          rectangle1.y = rectangle2.y;
          rectangle1.height = rectangle2.height;
          paintRow(treeCellRenderer, defaultTreeCellRenderer, paramSynthContext, synthContext, paramGraphics, rectangle, insets, rectangle2, rectangle1, treePath2, i, bool2, bool3, bool4);
          if (rectangle2.y + rectangle2.height >= j)
            bool = true; 
        } else {
          bool = true;
        } 
        i++;
      } 
      boolean bool1 = this.tree.isRootVisible();
      TreePath treePath1 = treePath;
      for (treePath1 = treePath1.getParentPath(); treePath1 != null; treePath1 = treePath1.getParentPath()) {
        paintVerticalPartOfLeg(paramGraphics, rectangle, insets, treePath1);
        this.drawingCache.put(treePath1, Boolean.TRUE);
      } 
      bool = false;
      enumeration = this.treeState.getVisiblePathsFrom(treePath);
      while (!bool && enumeration.hasMoreElements()) {
        TreePath treePath2 = (TreePath)enumeration.nextElement();
        Rectangle rectangle2 = getPathBounds(this.tree, treePath2);
        if (treePath2 != null && rectangle2 != null) {
          boolean bool3;
          boolean bool2;
          boolean bool4 = treeModel.isLeaf(treePath2.getLastPathComponent());
          if (bool4) {
            bool2 = bool3 = false;
          } else {
            bool2 = this.treeState.getExpandedState(treePath2);
            bool3 = this.tree.hasBeenExpanded(treePath2);
          } 
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
          if (rectangle2.y + rectangle2.height >= j)
            bool = true; 
        } else {
          bool = true;
        } 
        i++;
      } 
    } 
    synthContext.dispose();
    paintDropLine(paramGraphics);
    this.rendererPane.removeAll();
    this.paintContext = null;
  }
  
  private void configureRenderer(SynthContext paramSynthContext) {
    TreeCellRenderer treeCellRenderer = this.tree.getCellRenderer();
    if (treeCellRenderer instanceof DefaultTreeCellRenderer) {
      DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer)treeCellRenderer;
      SynthStyle synthStyle = paramSynthContext.getStyle();
      paramSynthContext.setComponentState(513);
      Color color = defaultTreeCellRenderer.getTextSelectionColor();
      if (color == null || color instanceof UIResource)
        defaultTreeCellRenderer.setTextSelectionColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND)); 
      color = defaultTreeCellRenderer.getBackgroundSelectionColor();
      if (color == null || color instanceof UIResource)
        defaultTreeCellRenderer.setBackgroundSelectionColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_BACKGROUND)); 
      paramSynthContext.setComponentState(1);
      color = defaultTreeCellRenderer.getTextNonSelectionColor();
      if (color == null || color instanceof UIResource)
        defaultTreeCellRenderer.setTextNonSelectionColor(synthStyle.getColorForState(paramSynthContext, ColorType.TEXT_FOREGROUND)); 
      color = defaultTreeCellRenderer.getBackgroundNonSelectionColor();
      if (color == null || color instanceof UIResource)
        defaultTreeCellRenderer.setBackgroundNonSelectionColor(synthStyle.getColorForState(paramSynthContext, ColorType.TEXT_BACKGROUND)); 
    } 
  }
  
  protected void paintHorizontalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (this.drawHorizontalLines)
      super.paintHorizontalPartOfLeg(paramGraphics, paramRectangle1, paramInsets, paramRectangle2, paramTreePath, paramInt, paramBoolean1, paramBoolean2, paramBoolean3); 
  }
  
  protected void paintHorizontalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3) { this.paintContext.getStyle().getGraphicsUtils(this.paintContext).drawLine(this.paintContext, "Tree.horizontalLine", paramGraphics, paramInt2, paramInt1, paramInt3, paramInt1, this.linesStyle); }
  
  protected void paintVerticalPartOfLeg(Graphics paramGraphics, Rectangle paramRectangle, Insets paramInsets, TreePath paramTreePath) {
    if (this.drawVerticalLines)
      super.paintVerticalPartOfLeg(paramGraphics, paramRectangle, paramInsets, paramTreePath); 
  }
  
  protected void paintVerticalLine(Graphics paramGraphics, JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3) { this.paintContext.getStyle().getGraphicsUtils(this.paintContext).drawLine(this.paintContext, "Tree.verticalLine", paramGraphics, paramInt1, paramInt2, paramInt1, paramInt3, this.linesStyle); }
  
  private void paintRow(TreeCellRenderer paramTreeCellRenderer, DefaultTreeCellRenderer paramDefaultTreeCellRenderer, SynthContext paramSynthContext1, SynthContext paramSynthContext2, Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, Rectangle paramRectangle3, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    byte b;
    boolean bool = this.tree.isRowSelected(paramInt);
    JTree.DropLocation dropLocation = this.tree.getDropLocation();
    boolean bool1 = (dropLocation != null && dropLocation.getChildIndex() == -1 && paramTreePath == dropLocation.getPath()) ? 1 : 0;
    char c = '\001';
    if (bool || bool1)
      c |= 0x200; 
    if (this.tree.isFocusOwner() && paramInt == getLeadSelectionRow())
      c |= 0x100; 
    paramSynthContext2.setComponentState(c);
    if (paramDefaultTreeCellRenderer != null && paramDefaultTreeCellRenderer.getBorderSelectionColor() instanceof UIResource)
      paramDefaultTreeCellRenderer.setBorderSelectionColor(this.style.getColor(paramSynthContext2, ColorType.FOCUS)); 
    SynthLookAndFeel.updateSubregion(paramSynthContext2, paramGraphics, paramRectangle3);
    paramSynthContext2.getPainter().paintTreeCellBackground(paramSynthContext2, paramGraphics, paramRectangle3.x, paramRectangle3.y, paramRectangle3.width, paramRectangle3.height);
    paramSynthContext2.getPainter().paintTreeCellBorder(paramSynthContext2, paramGraphics, paramRectangle3.x, paramRectangle3.y, paramRectangle3.width, paramRectangle3.height);
    if (this.editingComponent != null && this.editingRow == paramInt)
      return; 
    if (this.tree.hasFocus()) {
      b = getLeadSelectionRow();
    } else {
      b = -1;
    } 
    Component component = paramTreeCellRenderer.getTreeCellRendererComponent(this.tree, paramTreePath.getLastPathComponent(), bool, paramBoolean1, paramBoolean3, paramInt, (b == paramInt));
    this.rendererPane.paintComponent(paramGraphics, component, this.tree, paramRectangle2.x, paramRectangle2.y, paramRectangle2.width, paramRectangle2.height, true);
  }
  
  private int findCenteredX(int paramInt1, int paramInt2) { return this.tree.getComponentOrientation().isLeftToRight() ? (paramInt1 - (int)Math.ceil(paramInt2 / 2.0D)) : (paramInt1 - (int)Math.floor(paramInt2 / 2.0D)); }
  
  protected void paintExpandControl(Graphics paramGraphics, Rectangle paramRectangle1, Insets paramInsets, Rectangle paramRectangle2, TreePath paramTreePath, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    boolean bool = this.tree.getSelectionModel().isPathSelected(paramTreePath);
    int i = this.paintContext.getComponentState();
    if (bool)
      this.paintContext.setComponentState(i | 0x200); 
    super.paintExpandControl(paramGraphics, paramRectangle1, paramInsets, paramRectangle2, paramTreePath, paramInt, paramBoolean1, paramBoolean2, paramBoolean3);
    this.paintContext.setComponentState(i);
  }
  
  protected void drawCentered(Component paramComponent, Graphics paramGraphics, Icon paramIcon, int paramInt1, int paramInt2) {
    int i = SynthIcon.getIconWidth(paramIcon, this.paintContext);
    int j = SynthIcon.getIconHeight(paramIcon, this.paintContext);
    SynthIcon.paintIcon(paramIcon, this.paintContext, paramGraphics, findCenteredX(paramInt1, i), paramInt2 - j / 2, i, j);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JTree)paramPropertyChangeEvent.getSource()); 
    if ("dropLocation" == paramPropertyChangeEvent.getPropertyName()) {
      JTree.DropLocation dropLocation = (JTree.DropLocation)paramPropertyChangeEvent.getOldValue();
      repaintDropLocation(dropLocation);
      repaintDropLocation(this.tree.getDropLocation());
    } 
  }
  
  protected void paintDropLine(Graphics paramGraphics) {
    JTree.DropLocation dropLocation = this.tree.getDropLocation();
    if (!isDropLine(dropLocation))
      return; 
    Color color = (Color)this.style.get(this.paintContext, "Tree.dropLineColor");
    if (color != null) {
      paramGraphics.setColor(color);
      Rectangle rectangle = getDropLineRect(dropLocation);
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  private void repaintDropLocation(JTree.DropLocation paramDropLocation) {
    Rectangle rectangle;
    if (paramDropLocation == null)
      return; 
    if (isDropLine(paramDropLocation)) {
      rectangle = getDropLineRect(paramDropLocation);
    } else {
      rectangle = this.tree.getPathBounds(paramDropLocation.getPath());
      if (rectangle != null) {
        rectangle.x = 0;
        rectangle.width = this.tree.getWidth();
      } 
    } 
    if (rectangle != null)
      this.tree.repaint(rectangle); 
  }
  
  protected int getRowX(int paramInt1, int paramInt2) { return super.getRowX(paramInt1, paramInt2) + this.padding; }
  
  private class ExpandedIconWrapper extends SynthIcon {
    private ExpandedIconWrapper() {}
    
    public void paintIcon(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1SynthContext == null) {
        param1SynthContext = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
        SynthIcon.paintIcon(SynthTreeUI.this.expandedIcon, param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        param1SynthContext.dispose();
      } else {
        SynthIcon.paintIcon(SynthTreeUI.this.expandedIcon, param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
      } 
    }
    
    public int getIconWidth(SynthContext param1SynthContext) {
      int i;
      if (param1SynthContext == null) {
        param1SynthContext = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
        i = SynthIcon.getIconWidth(SynthTreeUI.this.expandedIcon, param1SynthContext);
        param1SynthContext.dispose();
      } else {
        i = SynthIcon.getIconWidth(SynthTreeUI.this.expandedIcon, param1SynthContext);
      } 
      return i;
    }
    
    public int getIconHeight(SynthContext param1SynthContext) {
      int i;
      if (param1SynthContext == null) {
        param1SynthContext = SynthTreeUI.this.getContext(SynthTreeUI.this.tree);
        i = SynthIcon.getIconHeight(SynthTreeUI.this.expandedIcon, param1SynthContext);
        param1SynthContext.dispose();
      } else {
        i = SynthIcon.getIconHeight(SynthTreeUI.this.expandedIcon, param1SynthContext);
      } 
      return i;
    }
  }
  
  private static class SynthTreeCellEditor extends DefaultTreeCellEditor {
    public SynthTreeCellEditor(JTree param1JTree, DefaultTreeCellRenderer param1DefaultTreeCellRenderer) {
      super(param1JTree, param1DefaultTreeCellRenderer);
      setBorderSelectionColor(null);
    }
    
    protected TreeCellEditor createTreeCellEditor() {
      JTextField jTextField = new JTextField() {
          public String getName() { return "Tree.cellEditor"; }
        };
      DefaultCellEditor defaultCellEditor = new DefaultCellEditor(jTextField);
      defaultCellEditor.setClickCountToStart(1);
      return defaultCellEditor;
    }
  }
  
  private class SynthTreeCellRenderer extends DefaultTreeCellRenderer implements UIResource {
    public String getName() { return "Tree.cellRenderer"; }
    
    public Component getTreeCellRendererComponent(JTree param1JTree, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3, int param1Int, boolean param1Boolean4) {
      if (!SynthTreeUI.this.useTreeColors && (param1Boolean1 || param1Boolean4)) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), param1Boolean1, param1Boolean4, param1JTree.isEnabled(), false);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      } 
      return super.getTreeCellRendererComponent(param1JTree, param1Object, param1Boolean1, param1Boolean2, param1Boolean3, param1Int, param1Boolean4);
    }
    
    public void paint(Graphics param1Graphics) {
      paintComponent(param1Graphics);
      if (this.hasFocus) {
        SynthContext synthContext = SynthTreeUI.this.getContext(SynthTreeUI.this.tree, Region.TREE_CELL);
        if (synthContext.getStyle() == null) {
          assert false : "SynthTreeCellRenderer is being used outside of UI that created it";
          return;
        } 
        int i = 0;
        Icon icon = getIcon();
        if (icon != null && getText() != null)
          i = icon.getIconWidth() + Math.max(0, getIconTextGap() - 1); 
        if (this.selected) {
          synthContext.setComponentState(513);
        } else {
          synthContext.setComponentState(1);
        } 
        if (getComponentOrientation().isLeftToRight()) {
          synthContext.getPainter().paintTreeCellFocus(synthContext, param1Graphics, i, 0, getWidth() - i, getHeight());
        } else {
          synthContext.getPainter().paintTreeCellFocus(synthContext, param1Graphics, 0, 0, getWidth() - i, getHeight());
        } 
        synthContext.dispose();
      } 
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTreeUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */