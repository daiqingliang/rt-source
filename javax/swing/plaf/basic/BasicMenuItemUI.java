package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.text.View;
import sun.swing.MenuItemCheckIconFactory;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicMenuItemUI extends MenuItemUI {
  protected JMenuItem menuItem = null;
  
  protected Color selectionBackground;
  
  protected Color selectionForeground;
  
  protected Color disabledForeground;
  
  protected Color acceleratorForeground;
  
  protected Color acceleratorSelectionForeground;
  
  protected String acceleratorDelimiter;
  
  protected int defaultTextIconGap;
  
  protected Font acceleratorFont;
  
  protected MouseInputListener mouseInputListener;
  
  protected MenuDragMouseListener menuDragMouseListener;
  
  protected MenuKeyListener menuKeyListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  Handler handler;
  
  protected Icon arrowIcon = null;
  
  protected Icon checkIcon = null;
  
  protected boolean oldBorderPainted;
  
  private static final boolean TRACE = false;
  
  private static final boolean VERBOSE = false;
  
  private static final boolean DEBUG = false;
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("doClick"));
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicMenuItemUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.menuItem = (JMenuItem)paramJComponent;
    installDefaults();
    installComponents(this.menuItem);
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults() {
    String str = getPropertyPrefix();
    this.acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
    if (this.acceleratorFont == null)
      this.acceleratorFont = UIManager.getFont("MenuItem.font"); 
    Object object = UIManager.get(getPropertyPrefix() + ".opaque");
    if (object != null) {
      LookAndFeel.installProperty(this.menuItem, "opaque", object);
    } else {
      LookAndFeel.installProperty(this.menuItem, "opaque", Boolean.TRUE);
    } 
    if (this.menuItem.getMargin() == null || this.menuItem.getMargin() instanceof javax.swing.plaf.UIResource)
      this.menuItem.setMargin(UIManager.getInsets(str + ".margin")); 
    LookAndFeel.installProperty(this.menuItem, "iconTextGap", Integer.valueOf(4));
    this.defaultTextIconGap = this.menuItem.getIconTextGap();
    LookAndFeel.installBorder(this.menuItem, str + ".border");
    this.oldBorderPainted = this.menuItem.isBorderPainted();
    LookAndFeel.installProperty(this.menuItem, "borderPainted", Boolean.valueOf(UIManager.getBoolean(str + ".borderPainted")));
    LookAndFeel.installColorsAndFont(this.menuItem, str + ".background", str + ".foreground", str + ".font");
    if (this.selectionBackground == null || this.selectionBackground instanceof javax.swing.plaf.UIResource)
      this.selectionBackground = UIManager.getColor(str + ".selectionBackground"); 
    if (this.selectionForeground == null || this.selectionForeground instanceof javax.swing.plaf.UIResource)
      this.selectionForeground = UIManager.getColor(str + ".selectionForeground"); 
    if (this.disabledForeground == null || this.disabledForeground instanceof javax.swing.plaf.UIResource)
      this.disabledForeground = UIManager.getColor(str + ".disabledForeground"); 
    if (this.acceleratorForeground == null || this.acceleratorForeground instanceof javax.swing.plaf.UIResource)
      this.acceleratorForeground = UIManager.getColor(str + ".acceleratorForeground"); 
    if (this.acceleratorSelectionForeground == null || this.acceleratorSelectionForeground instanceof javax.swing.plaf.UIResource)
      this.acceleratorSelectionForeground = UIManager.getColor(str + ".acceleratorSelectionForeground"); 
    this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
    if (this.acceleratorDelimiter == null)
      this.acceleratorDelimiter = "+"; 
    if (this.arrowIcon == null || this.arrowIcon instanceof javax.swing.plaf.UIResource)
      this.arrowIcon = UIManager.getIcon(str + ".arrowIcon"); 
    updateCheckIcon();
  }
  
  private void updateCheckIcon() {
    String str = getPropertyPrefix();
    if (this.checkIcon == null || this.checkIcon instanceof javax.swing.plaf.UIResource) {
      this.checkIcon = UIManager.getIcon(str + ".checkIcon");
      boolean bool = MenuItemLayoutHelper.isColumnLayout(BasicGraphicsUtils.isLeftToRight(this.menuItem), this.menuItem);
      if (bool) {
        MenuItemCheckIconFactory menuItemCheckIconFactory = (MenuItemCheckIconFactory)UIManager.get(str + ".checkIconFactory");
        if (menuItemCheckIconFactory != null && MenuItemLayoutHelper.useCheckAndArrow(this.menuItem) && menuItemCheckIconFactory.isCompatible(this.checkIcon, str))
          this.checkIcon = menuItemCheckIconFactory.getIcon(this.menuItem); 
      } 
    } 
  }
  
  protected void installComponents(JMenuItem paramJMenuItem) { BasicHTML.updateRenderer(paramJMenuItem, paramJMenuItem.getText()); }
  
  protected String getPropertyPrefix() { return "MenuItem"; }
  
  protected void installListeners() {
    if ((this.mouseInputListener = createMouseInputListener(this.menuItem)) != null) {
      this.menuItem.addMouseListener(this.mouseInputListener);
      this.menuItem.addMouseMotionListener(this.mouseInputListener);
    } 
    if ((this.menuDragMouseListener = createMenuDragMouseListener(this.menuItem)) != null)
      this.menuItem.addMenuDragMouseListener(this.menuDragMouseListener); 
    if ((this.menuKeyListener = createMenuKeyListener(this.menuItem)) != null)
      this.menuItem.addMenuKeyListener(this.menuKeyListener); 
    if ((this.propertyChangeListener = createPropertyChangeListener(this.menuItem)) != null)
      this.menuItem.addPropertyChangeListener(this.propertyChangeListener); 
  }
  
  protected void installKeyboardActions() {
    installLazyActionMap();
    updateAcceleratorBinding();
  }
  
  void installLazyActionMap() { LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuItemUI.class, getPropertyPrefix() + ".actionMap"); }
  
  public void uninstallUI(JComponent paramJComponent) {
    this.menuItem = (JMenuItem)paramJComponent;
    uninstallDefaults();
    uninstallComponents(this.menuItem);
    uninstallListeners();
    uninstallKeyboardActions();
    MenuItemLayoutHelper.clearUsedParentClientProperties(this.menuItem);
    this.menuItem = null;
  }
  
  protected void uninstallDefaults() {
    LookAndFeel.uninstallBorder(this.menuItem);
    LookAndFeel.installProperty(this.menuItem, "borderPainted", Boolean.valueOf(this.oldBorderPainted));
    if (this.menuItem.getMargin() instanceof javax.swing.plaf.UIResource)
      this.menuItem.setMargin(null); 
    if (this.arrowIcon instanceof javax.swing.plaf.UIResource)
      this.arrowIcon = null; 
    if (this.checkIcon instanceof javax.swing.plaf.UIResource)
      this.checkIcon = null; 
  }
  
  protected void uninstallComponents(JMenuItem paramJMenuItem) { BasicHTML.updateRenderer(paramJMenuItem, ""); }
  
  protected void uninstallListeners() {
    if (this.mouseInputListener != null) {
      this.menuItem.removeMouseListener(this.mouseInputListener);
      this.menuItem.removeMouseMotionListener(this.mouseInputListener);
    } 
    if (this.menuDragMouseListener != null)
      this.menuItem.removeMenuDragMouseListener(this.menuDragMouseListener); 
    if (this.menuKeyListener != null)
      this.menuItem.removeMenuKeyListener(this.menuKeyListener); 
    if (this.propertyChangeListener != null)
      this.menuItem.removePropertyChangeListener(this.propertyChangeListener); 
    this.mouseInputListener = null;
    this.menuDragMouseListener = null;
    this.menuKeyListener = null;
    this.propertyChangeListener = null;
    this.handler = null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.menuItem, null);
    SwingUtilities.replaceUIInputMap(this.menuItem, 2, null);
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent) { return getHandler(); }
  
  protected MenuDragMouseListener createMenuDragMouseListener(JComponent paramJComponent) { return getHandler(); }
  
  protected MenuKeyListener createMenuKeyListener(JComponent paramJComponent) { return null; }
  
  protected PropertyChangeListener createPropertyChangeListener(JComponent paramJComponent) { return getHandler(); }
  
  Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(); 
    return this.handler;
  }
  
  InputMap createInputMap(int paramInt) { return (paramInt == 2) ? new ComponentInputMapUIResource(this.menuItem) : null; }
  
  void updateAcceleratorBinding() {
    KeyStroke keyStroke = this.menuItem.getAccelerator();
    InputMap inputMap = SwingUtilities.getUIInputMap(this.menuItem, 2);
    if (inputMap != null)
      inputMap.clear(); 
    if (keyStroke != null) {
      if (inputMap == null) {
        inputMap = createInputMap(2);
        SwingUtilities.replaceUIInputMap(this.menuItem, 2, inputMap);
      } 
      inputMap.put(keyStroke, "doClick");
    } 
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = null;
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null) {
      dimension = getPreferredSize(paramJComponent);
      dimension.width = (int)(dimension.width - view.getPreferredSpan(0) - view.getMinimumSpan(0));
    } 
    return dimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return getPreferredMenuItemSize(paramJComponent, this.checkIcon, this.arrowIcon, this.defaultTextIconGap); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = null;
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null) {
      dimension = getPreferredSize(paramJComponent);
      dimension.width = (int)(dimension.width + view.getMaximumSpan(0) - view.getPreferredSpan(0));
    } 
    return dimension;
  }
  
  protected Dimension getPreferredMenuItemSize(JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt) {
    JMenuItem jMenuItem = (JMenuItem)paramJComponent;
    MenuItemLayoutHelper menuItemLayoutHelper = new MenuItemLayoutHelper(jMenuItem, paramIcon1, paramIcon2, MenuItemLayoutHelper.createMaxRect(), paramInt, this.acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(jMenuItem), jMenuItem.getFont(), this.acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), getPropertyPrefix());
    Dimension dimension = new Dimension();
    dimension.width = menuItemLayoutHelper.getLeadingGap();
    MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getCheckSize(), menuItemLayoutHelper.getAfterCheckIconGap(), dimension);
    if (!menuItemLayoutHelper.isTopLevelMenu() && menuItemLayoutHelper.getMinTextOffset() > 0 && dimension.width < menuItemLayoutHelper.getMinTextOffset())
      dimension.width = menuItemLayoutHelper.getMinTextOffset(); 
    MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getLabelSize(), menuItemLayoutHelper.getGap(), dimension);
    MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getAccSize(), menuItemLayoutHelper.getGap(), dimension);
    MenuItemLayoutHelper.addMaxWidth(menuItemLayoutHelper.getArrowSize(), menuItemLayoutHelper.getGap(), dimension);
    dimension.height = MenuItemLayoutHelper.max(new int[] { menuItemLayoutHelper.getCheckSize().getHeight(), menuItemLayoutHelper.getLabelSize().getHeight(), menuItemLayoutHelper.getAccSize().getHeight(), menuItemLayoutHelper.getArrowSize().getHeight() });
    Insets insets = menuItemLayoutHelper.getMenuItem().getInsets();
    if (insets != null) {
      dimension.width += insets.left + insets.right;
      dimension.height += insets.top + insets.bottom;
    } 
    if (dimension.width % 2 == 0)
      dimension.width++; 
    if (dimension.height % 2 == 0 && Boolean.TRUE != UIManager.get(getPropertyPrefix() + ".evenHeight"))
      dimension.height++; 
    return dimension;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) { paint(paramGraphics, paramJComponent); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) { paintMenuItem(paramGraphics, paramJComponent, this.checkIcon, this.arrowIcon, this.selectionBackground, this.selectionForeground, this.defaultTextIconGap); }
  
  protected void paintMenuItem(Graphics paramGraphics, JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, Color paramColor1, Color paramColor2, int paramInt) {
    Font font = paramGraphics.getFont();
    Color color = paramGraphics.getColor();
    JMenuItem jMenuItem = (JMenuItem)paramJComponent;
    paramGraphics.setFont(jMenuItem.getFont());
    Rectangle rectangle = new Rectangle(0, 0, jMenuItem.getWidth(), jMenuItem.getHeight());
    applyInsets(rectangle, jMenuItem.getInsets());
    MenuItemLayoutHelper menuItemLayoutHelper = new MenuItemLayoutHelper(jMenuItem, paramIcon1, paramIcon2, rectangle, paramInt, this.acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(jMenuItem), jMenuItem.getFont(), this.acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(this.menuItem), getPropertyPrefix());
    MenuItemLayoutHelper.LayoutResult layoutResult = menuItemLayoutHelper.layoutMenuItem();
    paintBackground(paramGraphics, jMenuItem, paramColor1);
    paintCheckIcon(paramGraphics, menuItemLayoutHelper, layoutResult, color, paramColor2);
    paintIcon(paramGraphics, menuItemLayoutHelper, layoutResult, color);
    paintText(paramGraphics, menuItemLayoutHelper, layoutResult);
    paintAccText(paramGraphics, menuItemLayoutHelper, layoutResult);
    paintArrowIcon(paramGraphics, menuItemLayoutHelper, layoutResult, paramColor2);
    paramGraphics.setColor(color);
    paramGraphics.setFont(font);
  }
  
  private void paintIcon(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult, Color paramColor) {
    if (paramMenuItemLayoutHelper.getIcon() != null) {
      Icon icon;
      ButtonModel buttonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      if (!buttonModel.isEnabled()) {
        icon = paramMenuItemLayoutHelper.getMenuItem().getDisabledIcon();
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        icon = paramMenuItemLayoutHelper.getMenuItem().getPressedIcon();
        if (icon == null)
          icon = paramMenuItemLayoutHelper.getMenuItem().getIcon(); 
      } else {
        icon = paramMenuItemLayoutHelper.getMenuItem().getIcon();
      } 
      if (icon != null) {
        icon.paintIcon(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, (paramLayoutResult.getIconRect()).x, (paramLayoutResult.getIconRect()).y);
        paramGraphics.setColor(paramColor);
      } 
    } 
  }
  
  private void paintCheckIcon(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult, Color paramColor1, Color paramColor2) {
    if (paramMenuItemLayoutHelper.getCheckIcon() != null) {
      ButtonModel buttonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      if (buttonModel.isArmed() || (paramMenuItemLayoutHelper.getMenuItem() instanceof javax.swing.JMenu && buttonModel.isSelected())) {
        paramGraphics.setColor(paramColor2);
      } else {
        paramGraphics.setColor(paramColor1);
      } 
      if (paramMenuItemLayoutHelper.useCheckAndArrow())
        paramMenuItemLayoutHelper.getCheckIcon().paintIcon(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, (paramLayoutResult.getCheckRect()).x, (paramLayoutResult.getCheckRect()).y); 
      paramGraphics.setColor(paramColor1);
    } 
  }
  
  private void paintAccText(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    if (!paramMenuItemLayoutHelper.getAccText().equals("")) {
      ButtonModel buttonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      paramGraphics.setFont(paramMenuItemLayoutHelper.getAccFontMetrics().getFont());
      if (!buttonModel.isEnabled()) {
        if (this.disabledForeground != null) {
          paramGraphics.setColor(this.disabledForeground);
          SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), (paramLayoutResult.getAccRect()).x, (paramLayoutResult.getAccRect()).y + paramMenuItemLayoutHelper.getAccFontMetrics().getAscent());
        } else {
          paramGraphics.setColor(paramMenuItemLayoutHelper.getMenuItem().getBackground().brighter());
          SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), (paramLayoutResult.getAccRect()).x, (paramLayoutResult.getAccRect()).y + paramMenuItemLayoutHelper.getAccFontMetrics().getAscent());
          paramGraphics.setColor(paramMenuItemLayoutHelper.getMenuItem().getBackground().darker());
          SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), (paramLayoutResult.getAccRect()).x - 1, (paramLayoutResult.getAccRect()).y + paramMenuItemLayoutHelper.getFontMetrics().getAscent() - 1);
        } 
      } else {
        if (buttonModel.isArmed() || (paramMenuItemLayoutHelper.getMenuItem() instanceof javax.swing.JMenu && buttonModel.isSelected())) {
          paramGraphics.setColor(this.acceleratorSelectionForeground);
        } else {
          paramGraphics.setColor(this.acceleratorForeground);
        } 
        SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), (paramLayoutResult.getAccRect()).x, (paramLayoutResult.getAccRect()).y + paramMenuItemLayoutHelper.getAccFontMetrics().getAscent());
      } 
    } 
  }
  
  private void paintText(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    if (!paramMenuItemLayoutHelper.getText().equals(""))
      if (paramMenuItemLayoutHelper.getHtmlView() != null) {
        paramMenuItemLayoutHelper.getHtmlView().paint(paramGraphics, paramLayoutResult.getTextRect());
      } else {
        paintText(paramGraphics, paramMenuItemLayoutHelper.getMenuItem(), paramLayoutResult.getTextRect(), paramMenuItemLayoutHelper.getText());
      }  
  }
  
  private void paintArrowIcon(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult, Color paramColor) {
    if (paramMenuItemLayoutHelper.getArrowIcon() != null) {
      ButtonModel buttonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      if (buttonModel.isArmed() || (paramMenuItemLayoutHelper.getMenuItem() instanceof javax.swing.JMenu && buttonModel.isSelected()))
        paramGraphics.setColor(paramColor); 
      if (paramMenuItemLayoutHelper.useCheckAndArrow())
        paramMenuItemLayoutHelper.getArrowIcon().paintIcon(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, (paramLayoutResult.getArrowRect()).x, (paramLayoutResult.getArrowRect()).y); 
    } 
  }
  
  private void applyInsets(Rectangle paramRectangle, Insets paramInsets) {
    if (paramInsets != null) {
      paramRectangle.x += paramInsets.left;
      paramRectangle.y += paramInsets.top;
      paramRectangle.width -= paramInsets.right + paramRectangle.x;
      paramRectangle.height -= paramInsets.bottom + paramRectangle.y;
    } 
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor) {
    ButtonModel buttonModel = paramJMenuItem.getModel();
    Color color = paramGraphics.getColor();
    int i = paramJMenuItem.getWidth();
    int j = paramJMenuItem.getHeight();
    if (paramJMenuItem.isOpaque()) {
      if (buttonModel.isArmed() || (paramJMenuItem instanceof javax.swing.JMenu && buttonModel.isSelected())) {
        paramGraphics.setColor(paramColor);
        paramGraphics.fillRect(0, 0, i, j);
      } else {
        paramGraphics.setColor(paramJMenuItem.getBackground());
        paramGraphics.fillRect(0, 0, i, j);
      } 
      paramGraphics.setColor(color);
    } else if (buttonModel.isArmed() || (paramJMenuItem instanceof javax.swing.JMenu && buttonModel.isSelected())) {
      paramGraphics.setColor(paramColor);
      paramGraphics.fillRect(0, 0, i, j);
      paramGraphics.setColor(color);
    } 
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString) {
    ButtonModel buttonModel = paramJMenuItem.getModel();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJMenuItem, paramGraphics);
    int i = paramJMenuItem.getDisplayedMnemonicIndex();
    if (!buttonModel.isEnabled()) {
      if (UIManager.get("MenuItem.disabledForeground") instanceof Color) {
        paramGraphics.setColor(UIManager.getColor("MenuItem.disabledForeground"));
        SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent());
      } else {
        paramGraphics.setColor(paramJMenuItem.getBackground().brighter());
        SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent());
        paramGraphics.setColor(paramJMenuItem.getBackground().darker());
        SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + fontMetrics.getAscent() - 1);
      } 
    } else {
      if (buttonModel.isArmed() || (paramJMenuItem instanceof javax.swing.JMenu && buttonModel.isSelected()))
        paramGraphics.setColor(this.selectionForeground); 
      SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent());
    } 
  }
  
  public MenuElement[] getPath() {
    MenuElement[] arrayOfMenuElement2;
    MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement1 = menuSelectionManager.getSelectedPath();
    int i = arrayOfMenuElement1.length;
    if (i == 0)
      return new MenuElement[0]; 
    Container container = this.menuItem.getParent();
    if (arrayOfMenuElement1[i - true].getComponent() == container) {
      arrayOfMenuElement2 = new MenuElement[i + 1];
      System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, i);
      arrayOfMenuElement2[i] = this.menuItem;
    } else {
      int j;
      for (j = arrayOfMenuElement1.length - 1; j >= 0 && arrayOfMenuElement1[j].getComponent() != container; j--);
      arrayOfMenuElement2 = new MenuElement[j + 2];
      System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, j + 1);
      arrayOfMenuElement2[j + 1] = this.menuItem;
    } 
    return arrayOfMenuElement2;
  }
  
  void printMenuElementArray(MenuElement[] paramArrayOfMenuElement, boolean paramBoolean) {
    System.out.println("Path is(");
    byte b = 0;
    int i = paramArrayOfMenuElement.length;
    while (b < i) {
      for (byte b1 = 0; b1 <= b; b1++)
        System.out.print("  "); 
      MenuElement menuElement = paramArrayOfMenuElement[b];
      if (menuElement instanceof JMenuItem) {
        System.out.println(((JMenuItem)menuElement).getText() + ", ");
      } else if (menuElement == null) {
        System.out.println("NULL , ");
      } else {
        System.out.println("" + menuElement + ", ");
      } 
      b++;
    } 
    System.out.println(")");
    if (paramBoolean == true)
      Thread.dumpStack(); 
  }
  
  protected void doClick(MenuSelectionManager paramMenuSelectionManager) {
    if (!isInternalFrameSystemMenu())
      BasicLookAndFeel.playSound(this.menuItem, getPropertyPrefix() + ".commandSound"); 
    if (paramMenuSelectionManager == null)
      paramMenuSelectionManager = MenuSelectionManager.defaultManager(); 
    paramMenuSelectionManager.clearSelectedPath();
    this.menuItem.doClick(0);
  }
  
  private boolean isInternalFrameSystemMenu() {
    String str = this.menuItem.getActionCommand();
    return (str == "Close" || str == "Minimize" || str == "Restore" || str == "Maximize");
  }
  
  private static class Actions extends UIAction {
    private static final String CLICK = "doClick";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JMenuItem jMenuItem = (JMenuItem)param1ActionEvent.getSource();
      MenuSelectionManager.defaultManager().clearSelectedPath();
      jMenuItem.doClick();
    }
  }
  
  class Handler implements MenuDragMouseListener, MouseInputListener, PropertyChangeListener {
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (!BasicMenuItemUI.this.menuItem.isEnabled())
        return; 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      Point point = param1MouseEvent.getPoint();
      if (point.x >= 0 && point.x < BasicMenuItemUI.this.menuItem.getWidth() && point.y >= 0 && point.y < BasicMenuItemUI.this.menuItem.getHeight()) {
        BasicMenuItemUI.this.doClick(menuSelectionManager);
      } else {
        menuSelectionManager.processMouseEvent(param1MouseEvent);
      } 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      int i = param1MouseEvent.getModifiers();
      if ((i & 0x1C) != 0) {
        MenuSelectionManager.defaultManager().processMouseEvent(param1MouseEvent);
      } else {
        menuSelectionManager.setSelectedPath(BasicMenuItemUI.this.getPath());
      } 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      int i = param1MouseEvent.getModifiers();
      if ((i & 0x1C) != 0) {
        MenuSelectionManager.defaultManager().processMouseEvent(param1MouseEvent);
      } else {
        MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
        if (arrayOfMenuElement.length > 1 && arrayOfMenuElement[arrayOfMenuElement.length - true] == BasicMenuItemUI.this.menuItem) {
          MenuElement[] arrayOfMenuElement1 = new MenuElement[arrayOfMenuElement.length - 1];
          byte b = 0;
          int j = arrayOfMenuElement.length - 1;
          while (b < j) {
            arrayOfMenuElement1[b] = arrayOfMenuElement[b];
            b++;
          } 
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
        } 
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void menuDragMouseEntered(MenuDragMouseEvent param1MenuDragMouseEvent) {
      MenuSelectionManager menuSelectionManager = param1MenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = param1MenuDragMouseEvent.getPath();
      menuSelectionManager.setSelectedPath(arrayOfMenuElement);
    }
    
    public void menuDragMouseDragged(MenuDragMouseEvent param1MenuDragMouseEvent) {
      MenuSelectionManager menuSelectionManager = param1MenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = param1MenuDragMouseEvent.getPath();
      menuSelectionManager.setSelectedPath(arrayOfMenuElement);
    }
    
    public void menuDragMouseExited(MenuDragMouseEvent param1MenuDragMouseEvent) {}
    
    public void menuDragMouseReleased(MenuDragMouseEvent param1MenuDragMouseEvent) {
      if (!BasicMenuItemUI.this.menuItem.isEnabled())
        return; 
      MenuSelectionManager menuSelectionManager = param1MenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = param1MenuDragMouseEvent.getPath();
      Point point = param1MenuDragMouseEvent.getPoint();
      if (point.x >= 0 && point.x < BasicMenuItemUI.this.menuItem.getWidth() && point.y >= 0 && point.y < BasicMenuItemUI.this.menuItem.getHeight()) {
        BasicMenuItemUI.this.doClick(menuSelectionManager);
      } else {
        menuSelectionManager.clearSelectedPath();
      } 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "labelFor" || str == "displayedMnemonic" || str == "accelerator") {
        BasicMenuItemUI.this.updateAcceleratorBinding();
      } else if (str == "text" || "font" == str || "foreground" == str) {
        JMenuItem jMenuItem = (JMenuItem)param1PropertyChangeEvent.getSource();
        String str1 = jMenuItem.getText();
        BasicHTML.updateRenderer(jMenuItem, str1);
      } else if (str == "iconTextGap") {
        BasicMenuItemUI.this.defaultTextIconGap = ((Number)param1PropertyChangeEvent.getNewValue()).intValue();
      } else if (str == "horizontalTextPosition") {
        BasicMenuItemUI.this.updateCheckIcon();
      } 
    }
  }
  
  protected class MouseInputHandler implements MouseInputListener {
    public void mouseClicked(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mouseClicked(param1MouseEvent); }
    
    public void mousePressed(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mouseReleased(param1MouseEvent); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mouseEntered(param1MouseEvent); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mouseExited(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mouseDragged(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicMenuItemUI.this.getHandler().mouseMoved(param1MouseEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */