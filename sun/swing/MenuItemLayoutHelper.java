package sun.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;

public class MenuItemLayoutHelper {
  public static final StringUIClientPropertyKey MAX_ARROW_WIDTH = new StringUIClientPropertyKey("maxArrowWidth");
  
  public static final StringUIClientPropertyKey MAX_CHECK_WIDTH = new StringUIClientPropertyKey("maxCheckWidth");
  
  public static final StringUIClientPropertyKey MAX_ICON_WIDTH = new StringUIClientPropertyKey("maxIconWidth");
  
  public static final StringUIClientPropertyKey MAX_TEXT_WIDTH = new StringUIClientPropertyKey("maxTextWidth");
  
  public static final StringUIClientPropertyKey MAX_ACC_WIDTH = new StringUIClientPropertyKey("maxAccWidth");
  
  public static final StringUIClientPropertyKey MAX_LABEL_WIDTH = new StringUIClientPropertyKey("maxLabelWidth");
  
  private JMenuItem mi;
  
  private JComponent miParent;
  
  private Font font;
  
  private Font accFont;
  
  private FontMetrics fm;
  
  private FontMetrics accFm;
  
  private Icon icon;
  
  private Icon checkIcon;
  
  private Icon arrowIcon;
  
  private String text;
  
  private String accText;
  
  private boolean isColumnLayout;
  
  private boolean useCheckAndArrow;
  
  private boolean isLeftToRight;
  
  private boolean isTopLevelMenu;
  
  private View htmlView;
  
  private int verticalAlignment;
  
  private int horizontalAlignment;
  
  private int verticalTextPosition;
  
  private int horizontalTextPosition;
  
  private int gap;
  
  private int leadingGap;
  
  private int afterCheckIconGap;
  
  private int minTextOffset;
  
  private int leftTextExtraWidth;
  
  private Rectangle viewRect;
  
  private RectSize iconSize;
  
  private RectSize textSize;
  
  private RectSize accSize;
  
  private RectSize checkSize;
  
  private RectSize arrowSize;
  
  private RectSize labelSize;
  
  protected MenuItemLayoutHelper() {}
  
  public MenuItemLayoutHelper(JMenuItem paramJMenuItem, Icon paramIcon1, Icon paramIcon2, Rectangle paramRectangle, int paramInt, String paramString1, boolean paramBoolean1, Font paramFont1, Font paramFont2, boolean paramBoolean2, String paramString2) { reset(paramJMenuItem, paramIcon1, paramIcon2, paramRectangle, paramInt, paramString1, paramBoolean1, paramFont1, paramFont2, paramBoolean2, paramString2); }
  
  protected void reset(JMenuItem paramJMenuItem, Icon paramIcon1, Icon paramIcon2, Rectangle paramRectangle, int paramInt, String paramString1, boolean paramBoolean1, Font paramFont1, Font paramFont2, boolean paramBoolean2, String paramString2) {
    this.mi = paramJMenuItem;
    this.miParent = getMenuItemParent(paramJMenuItem);
    this.accText = getAccText(paramString1);
    this.verticalAlignment = paramJMenuItem.getVerticalAlignment();
    this.horizontalAlignment = paramJMenuItem.getHorizontalAlignment();
    this.verticalTextPosition = paramJMenuItem.getVerticalTextPosition();
    this.horizontalTextPosition = paramJMenuItem.getHorizontalTextPosition();
    this.useCheckAndArrow = paramBoolean2;
    this.font = paramFont1;
    this.accFont = paramFont2;
    this.fm = paramJMenuItem.getFontMetrics(paramFont1);
    this.accFm = paramJMenuItem.getFontMetrics(paramFont2);
    this.isLeftToRight = paramBoolean1;
    this.isColumnLayout = isColumnLayout(paramBoolean1, this.horizontalAlignment, this.horizontalTextPosition, this.verticalTextPosition);
    this.isTopLevelMenu = (this.miParent == null);
    this.checkIcon = paramIcon1;
    this.icon = getIcon(paramString2);
    this.arrowIcon = paramIcon2;
    this.text = paramJMenuItem.getText();
    this.gap = paramInt;
    this.afterCheckIconGap = getAfterCheckIconGap(paramString2);
    this.minTextOffset = getMinTextOffset(paramString2);
    this.htmlView = (View)paramJMenuItem.getClientProperty("html");
    this.viewRect = paramRectangle;
    this.iconSize = new RectSize();
    this.textSize = new RectSize();
    this.accSize = new RectSize();
    this.checkSize = new RectSize();
    this.arrowSize = new RectSize();
    this.labelSize = new RectSize();
    calcExtraWidths();
    calcWidthsAndHeights();
    setOriginalWidths();
    calcMaxWidths();
    this.leadingGap = getLeadingGap(paramString2);
    calcMaxTextOffset(paramRectangle);
  }
  
  private void calcExtraWidths() { this.leftTextExtraWidth = getLeftExtraWidth(this.text); }
  
  private int getLeftExtraWidth(String paramString) {
    int i = SwingUtilities2.getLeftSideBearing(this.mi, this.fm, paramString);
    return (i < 0) ? -i : 0;
  }
  
  private void setOriginalWidths() {
    this.iconSize.origWidth = this.iconSize.width;
    this.textSize.origWidth = this.textSize.width;
    this.accSize.origWidth = this.accSize.width;
    this.checkSize.origWidth = this.checkSize.width;
    this.arrowSize.origWidth = this.arrowSize.width;
  }
  
  private String getAccText(String paramString) {
    String str = "";
    KeyStroke keyStroke = this.mi.getAccelerator();
    if (keyStroke != null) {
      int i = keyStroke.getModifiers();
      if (i > 0) {
        str = KeyEvent.getKeyModifiersText(i);
        str = str + paramString;
      } 
      int j = keyStroke.getKeyCode();
      if (j != 0) {
        str = str + KeyEvent.getKeyText(j);
      } else {
        str = str + keyStroke.getKeyChar();
      } 
    } 
    return str;
  }
  
  private Icon getIcon(String paramString) {
    Icon icon1 = null;
    MenuItemCheckIconFactory menuItemCheckIconFactory = (MenuItemCheckIconFactory)UIManager.get(paramString + ".checkIconFactory");
    if (!this.isColumnLayout || !this.useCheckAndArrow || menuItemCheckIconFactory == null || !menuItemCheckIconFactory.isCompatible(this.checkIcon, paramString))
      icon1 = this.mi.getIcon(); 
    return icon1;
  }
  
  private int getMinTextOffset(String paramString) {
    int i = 0;
    Object object = UIManager.get(paramString + ".minimumTextOffset");
    if (object instanceof Integer)
      i = ((Integer)object).intValue(); 
    return i;
  }
  
  private int getAfterCheckIconGap(String paramString) {
    int i = this.gap;
    Object object = UIManager.get(paramString + ".afterCheckIconGap");
    if (object instanceof Integer)
      i = ((Integer)object).intValue(); 
    return i;
  }
  
  private int getLeadingGap(String paramString) { return (this.checkSize.getMaxWidth() > 0) ? getCheckOffset(paramString) : this.gap; }
  
  private int getCheckOffset(String paramString) {
    int i = this.gap;
    Object object = UIManager.get(paramString + ".checkIconOffset");
    if (object instanceof Integer)
      i = ((Integer)object).intValue(); 
    return i;
  }
  
  protected void calcWidthsAndHeights() {
    if (this.icon != null) {
      this.iconSize.width = this.icon.getIconWidth();
      this.iconSize.height = this.icon.getIconHeight();
    } 
    if (!this.accText.equals("")) {
      this.accSize.width = SwingUtilities2.stringWidth(this.mi, this.accFm, this.accText);
      this.accSize.height = this.accFm.getHeight();
    } 
    if (this.text == null) {
      this.text = "";
    } else if (!this.text.equals("")) {
      if (this.htmlView != null) {
        this.textSize.width = (int)this.htmlView.getPreferredSpan(0);
        this.textSize.height = (int)this.htmlView.getPreferredSpan(1);
      } else {
        this.textSize.width = SwingUtilities2.stringWidth(this.mi, this.fm, this.text);
        this.textSize.height = this.fm.getHeight();
      } 
    } 
    if (this.useCheckAndArrow) {
      if (this.checkIcon != null) {
        this.checkSize.width = this.checkIcon.getIconWidth();
        this.checkSize.height = this.checkIcon.getIconHeight();
      } 
      if (this.arrowIcon != null) {
        this.arrowSize.width = this.arrowIcon.getIconWidth();
        this.arrowSize.height = this.arrowIcon.getIconHeight();
      } 
    } 
    if (this.isColumnLayout) {
      this.labelSize.width = this.iconSize.width + this.textSize.width + this.gap;
      this.labelSize.height = max(new int[] { RectSize.access$200(this.checkSize), RectSize.access$200(this.iconSize), RectSize.access$200(this.textSize), RectSize.access$200(this.accSize), RectSize.access$200(this.arrowSize) });
    } else {
      Rectangle rectangle1 = new Rectangle();
      Rectangle rectangle2 = new Rectangle();
      SwingUtilities.layoutCompoundLabel(this.mi, this.fm, this.text, this.icon, this.verticalAlignment, this.horizontalAlignment, this.verticalTextPosition, this.horizontalTextPosition, this.viewRect, rectangle2, rectangle1, this.gap);
      rectangle1.width += this.leftTextExtraWidth;
      Rectangle rectangle3 = rectangle2.union(rectangle1);
      this.labelSize.height = rectangle3.height;
      this.labelSize.width = rectangle3.width;
    } 
  }
  
  protected void calcMaxWidths() {
    calcMaxWidth(this.checkSize, MAX_CHECK_WIDTH);
    calcMaxWidth(this.arrowSize, MAX_ARROW_WIDTH);
    calcMaxWidth(this.accSize, MAX_ACC_WIDTH);
    if (this.isColumnLayout) {
      calcMaxWidth(this.iconSize, MAX_ICON_WIDTH);
      calcMaxWidth(this.textSize, MAX_TEXT_WIDTH);
      int i = this.gap;
      if (this.iconSize.getMaxWidth() == 0 || this.textSize.getMaxWidth() == 0)
        i = 0; 
      this.labelSize.maxWidth = calcMaxValue(MAX_LABEL_WIDTH, this.iconSize.maxWidth + this.textSize.maxWidth + i);
    } else {
      this.iconSize.maxWidth = getParentIntProperty(MAX_ICON_WIDTH);
      calcMaxWidth(this.labelSize, MAX_LABEL_WIDTH);
      int i = this.labelSize.maxWidth - this.iconSize.maxWidth;
      if (this.iconSize.maxWidth > 0)
        i -= this.gap; 
      this.textSize.maxWidth = calcMaxValue(MAX_TEXT_WIDTH, i);
    } 
  }
  
  protected void calcMaxWidth(RectSize paramRectSize, Object paramObject) { paramRectSize.maxWidth = calcMaxValue(paramObject, paramRectSize.width); }
  
  protected int calcMaxValue(Object paramObject, int paramInt) {
    int i = getParentIntProperty(paramObject);
    if (paramInt > i) {
      if (this.miParent != null)
        this.miParent.putClientProperty(paramObject, Integer.valueOf(paramInt)); 
      return paramInt;
    } 
    return i;
  }
  
  protected int getParentIntProperty(Object paramObject) {
    Object object = null;
    if (this.miParent != null)
      object = this.miParent.getClientProperty(paramObject); 
    if (object == null || !(object instanceof Integer))
      object = Integer.valueOf(0); 
    return ((Integer)object).intValue();
  }
  
  public static boolean isColumnLayout(boolean paramBoolean, JMenuItem paramJMenuItem) {
    assert paramJMenuItem != null;
    return isColumnLayout(paramBoolean, paramJMenuItem.getHorizontalAlignment(), paramJMenuItem.getHorizontalTextPosition(), paramJMenuItem.getVerticalTextPosition());
  }
  
  public static boolean isColumnLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 != 0)
      return false; 
    if (paramBoolean) {
      if (paramInt1 != 10 && paramInt1 != 2)
        return false; 
      if (paramInt2 != 11 && paramInt2 != 4)
        return false; 
    } else {
      if (paramInt1 != 10 && paramInt1 != 4)
        return false; 
      if (paramInt2 != 11 && paramInt2 != 2)
        return false; 
    } 
    return true;
  }
  
  private void calcMaxTextOffset(Rectangle paramRectangle) {
    if (!this.isColumnLayout || !this.isLeftToRight)
      return; 
    int i = paramRectangle.x + this.leadingGap + this.checkSize.maxWidth + this.afterCheckIconGap + this.iconSize.maxWidth + this.gap;
    if (this.checkSize.maxWidth == 0)
      i -= this.afterCheckIconGap; 
    if (this.iconSize.maxWidth == 0)
      i -= this.gap; 
    if (i < this.minTextOffset)
      i = this.minTextOffset; 
    calcMaxValue(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, i);
  }
  
  public LayoutResult layoutMenuItem() {
    LayoutResult layoutResult = createLayoutResult();
    prepareForLayout(layoutResult);
    if (isColumnLayout()) {
      if (isLeftToRight()) {
        doLTRColumnLayout(layoutResult, getLTRColumnAlignment());
      } else {
        doRTLColumnLayout(layoutResult, getRTLColumnAlignment());
      } 
    } else if (isLeftToRight()) {
      doLTRComplexLayout(layoutResult, getLTRColumnAlignment());
    } else {
      doRTLComplexLayout(layoutResult, getRTLColumnAlignment());
    } 
    alignAccCheckAndArrowVertically(layoutResult);
    return layoutResult;
  }
  
  private LayoutResult createLayoutResult() { return new LayoutResult(new Rectangle(this.iconSize.width, this.iconSize.height), new Rectangle(this.textSize.width, this.textSize.height), new Rectangle(this.accSize.width, this.accSize.height), new Rectangle(this.checkSize.width, this.checkSize.height), new Rectangle(this.arrowSize.width, this.arrowSize.height), new Rectangle(this.labelSize.width, this.labelSize.height)); }
  
  public ColumnAlignment getLTRColumnAlignment() { return ColumnAlignment.LEFT_ALIGNMENT; }
  
  public ColumnAlignment getRTLColumnAlignment() { return ColumnAlignment.RIGHT_ALIGNMENT; }
  
  protected void prepareForLayout(LayoutResult paramLayoutResult) {
    paramLayoutResult.checkRect.width = this.checkSize.maxWidth;
    paramLayoutResult.accRect.width = this.accSize.maxWidth;
    paramLayoutResult.arrowRect.width = this.arrowSize.maxWidth;
  }
  
  private void alignAccCheckAndArrowVertically(LayoutResult paramLayoutResult) {
    paramLayoutResult.accRect.y = (int)(paramLayoutResult.labelRect.y + paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.accRect.height / 2.0F);
    fixVerticalAlignment(paramLayoutResult, paramLayoutResult.accRect);
    if (this.useCheckAndArrow) {
      paramLayoutResult.arrowRect.y = (int)(paramLayoutResult.labelRect.y + paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.arrowRect.height / 2.0F);
      paramLayoutResult.checkRect.y = (int)(paramLayoutResult.labelRect.y + paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.checkRect.height / 2.0F);
      fixVerticalAlignment(paramLayoutResult, paramLayoutResult.arrowRect);
      fixVerticalAlignment(paramLayoutResult, paramLayoutResult.checkRect);
    } 
  }
  
  private void fixVerticalAlignment(LayoutResult paramLayoutResult, Rectangle paramRectangle) {
    int i = 0;
    if (paramRectangle.y < this.viewRect.y) {
      i = this.viewRect.y - paramRectangle.y;
    } else if (paramRectangle.y + paramRectangle.height > this.viewRect.y + this.viewRect.height) {
      i = this.viewRect.y + this.viewRect.height - paramRectangle.y - paramRectangle.height;
    } 
    if (i != 0) {
      paramLayoutResult.checkRect.y += i;
      paramLayoutResult.iconRect.y += i;
      paramLayoutResult.textRect.y += i;
      paramLayoutResult.accRect.y += i;
      paramLayoutResult.arrowRect.y += i;
      paramLayoutResult.labelRect.y += i;
    } 
  }
  
  private void doLTRColumnLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment) {
    paramLayoutResult.iconRect.width = this.iconSize.maxWidth;
    paramLayoutResult.textRect.width = this.textSize.maxWidth;
    calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$400(paramLayoutResult), LayoutResult.access$800(paramLayoutResult), LayoutResult.access$900(paramLayoutResult) });
    if (paramLayoutResult.checkRect.width > 0) {
      paramLayoutResult.iconRect.x += this.afterCheckIconGap - this.gap;
      paramLayoutResult.textRect.x += this.afterCheckIconGap - this.gap;
    } 
    calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$600(paramLayoutResult), LayoutResult.access$500(paramLayoutResult) });
    int i = paramLayoutResult.textRect.x - this.viewRect.x;
    if (!this.isTopLevelMenu && i < this.minTextOffset)
      paramLayoutResult.textRect.x += this.minTextOffset - i; 
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcTextAndIconYPositions(paramLayoutResult);
    paramLayoutResult.setLabelRect(paramLayoutResult.textRect.union(paramLayoutResult.iconRect));
  }
  
  private void doLTRComplexLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment) {
    paramLayoutResult.labelRect.width = this.labelSize.maxWidth;
    calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$400(paramLayoutResult), LayoutResult.access$700(paramLayoutResult) });
    if (paramLayoutResult.checkRect.width > 0)
      paramLayoutResult.labelRect.x += this.afterCheckIconGap - this.gap; 
    calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$600(paramLayoutResult), LayoutResult.access$500(paramLayoutResult) });
    int i = paramLayoutResult.labelRect.x - this.viewRect.x;
    if (!this.isTopLevelMenu && i < this.minTextOffset)
      paramLayoutResult.labelRect.x += this.minTextOffset - i; 
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcLabelYPosition(paramLayoutResult);
    layoutIconAndTextInLabelRect(paramLayoutResult);
  }
  
  private void doRTLColumnLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment) {
    paramLayoutResult.iconRect.width = this.iconSize.maxWidth;
    paramLayoutResult.textRect.width = this.textSize.maxWidth;
    calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$400(paramLayoutResult), LayoutResult.access$800(paramLayoutResult), LayoutResult.access$900(paramLayoutResult) });
    if (paramLayoutResult.checkRect.width > 0) {
      paramLayoutResult.iconRect.x -= this.afterCheckIconGap - this.gap;
      paramLayoutResult.textRect.x -= this.afterCheckIconGap - this.gap;
    } 
    calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$600(paramLayoutResult), LayoutResult.access$500(paramLayoutResult) });
    int i = this.viewRect.x + this.viewRect.width - paramLayoutResult.textRect.x + paramLayoutResult.textRect.width;
    if (!this.isTopLevelMenu && i < this.minTextOffset)
      paramLayoutResult.textRect.x -= this.minTextOffset - i; 
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcTextAndIconYPositions(paramLayoutResult);
    paramLayoutResult.setLabelRect(paramLayoutResult.textRect.union(paramLayoutResult.iconRect));
  }
  
  private void doRTLComplexLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment) {
    paramLayoutResult.labelRect.width = this.labelSize.maxWidth;
    calcXPositionsRTL(this.viewRect.x + this.viewRect.width, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$400(paramLayoutResult), LayoutResult.access$700(paramLayoutResult) });
    if (paramLayoutResult.checkRect.width > 0)
      paramLayoutResult.labelRect.x -= this.afterCheckIconGap - this.gap; 
    calcXPositionsLTR(this.viewRect.x, this.leadingGap, this.gap, new Rectangle[] { LayoutResult.access$600(paramLayoutResult), LayoutResult.access$500(paramLayoutResult) });
    int i = this.viewRect.x + this.viewRect.width - paramLayoutResult.labelRect.x + paramLayoutResult.labelRect.width;
    if (!this.isTopLevelMenu && i < this.minTextOffset)
      paramLayoutResult.labelRect.x -= this.minTextOffset - i; 
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcLabelYPosition(paramLayoutResult);
    layoutIconAndTextInLabelRect(paramLayoutResult);
  }
  
  private void alignRects(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment) {
    alignRect(paramLayoutResult.checkRect, paramColumnAlignment.getCheckAlignment(), this.checkSize.getOrigWidth());
    alignRect(paramLayoutResult.iconRect, paramColumnAlignment.getIconAlignment(), this.iconSize.getOrigWidth());
    alignRect(paramLayoutResult.textRect, paramColumnAlignment.getTextAlignment(), this.textSize.getOrigWidth());
    alignRect(paramLayoutResult.accRect, paramColumnAlignment.getAccAlignment(), this.accSize.getOrigWidth());
    alignRect(paramLayoutResult.arrowRect, paramColumnAlignment.getArrowAlignment(), this.arrowSize.getOrigWidth());
  }
  
  private void alignRect(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (paramInt1 == 4)
      paramRectangle.x = paramRectangle.x + paramRectangle.width - paramInt2; 
    paramRectangle.width = paramInt2;
  }
  
  protected void layoutIconAndTextInLabelRect(LayoutResult paramLayoutResult) {
    paramLayoutResult.setTextRect(new Rectangle());
    paramLayoutResult.setIconRect(new Rectangle());
    SwingUtilities.layoutCompoundLabel(this.mi, this.fm, this.text, this.icon, this.verticalAlignment, this.horizontalAlignment, this.verticalTextPosition, this.horizontalTextPosition, paramLayoutResult.labelRect, paramLayoutResult.iconRect, paramLayoutResult.textRect, this.gap);
  }
  
  private void calcXPositionsLTR(int paramInt1, int paramInt2, int paramInt3, Rectangle... paramVarArgs) {
    int i = paramInt1 + paramInt2;
    for (Rectangle rectangle : paramVarArgs) {
      rectangle.x = i;
      if (rectangle.width > 0)
        i += rectangle.width + paramInt3; 
    } 
  }
  
  private void calcXPositionsRTL(int paramInt1, int paramInt2, int paramInt3, Rectangle... paramVarArgs) {
    int i = paramInt1 - paramInt2;
    for (Rectangle rectangle : paramVarArgs) {
      rectangle.x = i - rectangle.width;
      if (rectangle.width > 0)
        i -= rectangle.width + paramInt3; 
    } 
  }
  
  private void calcTextAndIconYPositions(LayoutResult paramLayoutResult) {
    if (this.verticalAlignment == 1) {
      paramLayoutResult.textRect.y = (int)(this.viewRect.y + paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.textRect.height / 2.0F);
      paramLayoutResult.iconRect.y = (int)(this.viewRect.y + paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.iconRect.height / 2.0F);
    } else if (this.verticalAlignment == 0) {
      paramLayoutResult.textRect.y = (int)(this.viewRect.y + this.viewRect.height / 2.0F - paramLayoutResult.textRect.height / 2.0F);
      paramLayoutResult.iconRect.y = (int)(this.viewRect.y + this.viewRect.height / 2.0F - paramLayoutResult.iconRect.height / 2.0F);
    } else if (this.verticalAlignment == 3) {
      paramLayoutResult.textRect.y = (int)((this.viewRect.y + this.viewRect.height) - paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.textRect.height / 2.0F);
      paramLayoutResult.iconRect.y = (int)((this.viewRect.y + this.viewRect.height) - paramLayoutResult.labelRect.height / 2.0F - paramLayoutResult.iconRect.height / 2.0F);
    } 
  }
  
  private void calcLabelYPosition(LayoutResult paramLayoutResult) {
    if (this.verticalAlignment == 1) {
      paramLayoutResult.labelRect.y = this.viewRect.y;
    } else if (this.verticalAlignment == 0) {
      paramLayoutResult.labelRect.y = (int)(this.viewRect.y + this.viewRect.height / 2.0F - paramLayoutResult.labelRect.height / 2.0F);
    } else if (this.verticalAlignment == 3) {
      paramLayoutResult.labelRect.y = this.viewRect.y + this.viewRect.height - paramLayoutResult.labelRect.height;
    } 
  }
  
  public static JComponent getMenuItemParent(JMenuItem paramJMenuItem) {
    Container container = paramJMenuItem.getParent();
    return (container instanceof JComponent && (!(paramJMenuItem instanceof JMenu) || !((JMenu)paramJMenuItem).isTopLevelMenu())) ? (JComponent)container : null;
  }
  
  public static void clearUsedParentClientProperties(JMenuItem paramJMenuItem) { clearUsedClientProperties(getMenuItemParent(paramJMenuItem)); }
  
  public static void clearUsedClientProperties(JComponent paramJComponent) {
    if (paramJComponent != null) {
      paramJComponent.putClientProperty(MAX_ARROW_WIDTH, null);
      paramJComponent.putClientProperty(MAX_CHECK_WIDTH, null);
      paramJComponent.putClientProperty(MAX_ACC_WIDTH, null);
      paramJComponent.putClientProperty(MAX_TEXT_WIDTH, null);
      paramJComponent.putClientProperty(MAX_ICON_WIDTH, null);
      paramJComponent.putClientProperty(MAX_LABEL_WIDTH, null);
      paramJComponent.putClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, null);
    } 
  }
  
  public static int max(int... paramVarArgs) {
    int i = Integer.MIN_VALUE;
    for (int j : paramVarArgs) {
      if (j > i)
        i = j; 
    } 
    return i;
  }
  
  public static Rectangle createMaxRect() { return new Rectangle(0, 0, 2147483647, 2147483647); }
  
  public static void addMaxWidth(RectSize paramRectSize, int paramInt, Dimension paramDimension) {
    if (paramRectSize.maxWidth > 0)
      paramDimension.width += paramRectSize.maxWidth + paramInt; 
  }
  
  public static void addWidth(int paramInt1, int paramInt2, Dimension paramDimension) {
    if (paramInt1 > 0)
      paramDimension.width += paramInt1 + paramInt2; 
  }
  
  public JMenuItem getMenuItem() { return this.mi; }
  
  public JComponent getMenuItemParent() { return this.miParent; }
  
  public Font getFont() { return this.font; }
  
  public Font getAccFont() { return this.accFont; }
  
  public FontMetrics getFontMetrics() { return this.fm; }
  
  public FontMetrics getAccFontMetrics() { return this.accFm; }
  
  public Icon getIcon() { return this.icon; }
  
  public Icon getCheckIcon() { return this.checkIcon; }
  
  public Icon getArrowIcon() { return this.arrowIcon; }
  
  public String getText() { return this.text; }
  
  public String getAccText() { return this.accText; }
  
  public boolean isColumnLayout() { return this.isColumnLayout; }
  
  public boolean useCheckAndArrow() { return this.useCheckAndArrow; }
  
  public boolean isLeftToRight() { return this.isLeftToRight; }
  
  public boolean isTopLevelMenu() { return this.isTopLevelMenu; }
  
  public View getHtmlView() { return this.htmlView; }
  
  public int getVerticalAlignment() { return this.verticalAlignment; }
  
  public int getHorizontalAlignment() { return this.horizontalAlignment; }
  
  public int getVerticalTextPosition() { return this.verticalTextPosition; }
  
  public int getHorizontalTextPosition() { return this.horizontalTextPosition; }
  
  public int getGap() { return this.gap; }
  
  public int getLeadingGap() { return this.leadingGap; }
  
  public int getAfterCheckIconGap() { return this.afterCheckIconGap; }
  
  public int getMinTextOffset() { return this.minTextOffset; }
  
  public Rectangle getViewRect() { return this.viewRect; }
  
  public RectSize getIconSize() { return this.iconSize; }
  
  public RectSize getTextSize() { return this.textSize; }
  
  public RectSize getAccSize() { return this.accSize; }
  
  public RectSize getCheckSize() { return this.checkSize; }
  
  public RectSize getArrowSize() { return this.arrowSize; }
  
  public RectSize getLabelSize() { return this.labelSize; }
  
  protected void setMenuItem(JMenuItem paramJMenuItem) { this.mi = paramJMenuItem; }
  
  protected void setMenuItemParent(JComponent paramJComponent) { this.miParent = paramJComponent; }
  
  protected void setFont(Font paramFont) { this.font = paramFont; }
  
  protected void setAccFont(Font paramFont) { this.accFont = paramFont; }
  
  protected void setFontMetrics(FontMetrics paramFontMetrics) { this.fm = paramFontMetrics; }
  
  protected void setAccFontMetrics(FontMetrics paramFontMetrics) { this.accFm = paramFontMetrics; }
  
  protected void setIcon(Icon paramIcon) { this.icon = paramIcon; }
  
  protected void setCheckIcon(Icon paramIcon) { this.checkIcon = paramIcon; }
  
  protected void setArrowIcon(Icon paramIcon) { this.arrowIcon = paramIcon; }
  
  protected void setText(String paramString) { this.text = paramString; }
  
  protected void setAccText(String paramString) { this.accText = paramString; }
  
  protected void setColumnLayout(boolean paramBoolean) { this.isColumnLayout = paramBoolean; }
  
  protected void setUseCheckAndArrow(boolean paramBoolean) { this.useCheckAndArrow = paramBoolean; }
  
  protected void setLeftToRight(boolean paramBoolean) { this.isLeftToRight = paramBoolean; }
  
  protected void setTopLevelMenu(boolean paramBoolean) { this.isTopLevelMenu = paramBoolean; }
  
  protected void setHtmlView(View paramView) { this.htmlView = paramView; }
  
  protected void setVerticalAlignment(int paramInt) { this.verticalAlignment = paramInt; }
  
  protected void setHorizontalAlignment(int paramInt) { this.horizontalAlignment = paramInt; }
  
  protected void setVerticalTextPosition(int paramInt) { this.verticalTextPosition = paramInt; }
  
  protected void setHorizontalTextPosition(int paramInt) { this.horizontalTextPosition = paramInt; }
  
  protected void setGap(int paramInt) { this.gap = paramInt; }
  
  protected void setLeadingGap(int paramInt) { this.leadingGap = paramInt; }
  
  protected void setAfterCheckIconGap(int paramInt) { this.afterCheckIconGap = paramInt; }
  
  protected void setMinTextOffset(int paramInt) { this.minTextOffset = paramInt; }
  
  protected void setViewRect(Rectangle paramRectangle) { this.viewRect = paramRectangle; }
  
  protected void setIconSize(RectSize paramRectSize) { this.iconSize = paramRectSize; }
  
  protected void setTextSize(RectSize paramRectSize) { this.textSize = paramRectSize; }
  
  protected void setAccSize(RectSize paramRectSize) { this.accSize = paramRectSize; }
  
  protected void setCheckSize(RectSize paramRectSize) { this.checkSize = paramRectSize; }
  
  protected void setArrowSize(RectSize paramRectSize) { this.arrowSize = paramRectSize; }
  
  protected void setLabelSize(RectSize paramRectSize) { this.labelSize = paramRectSize; }
  
  public int getLeftTextExtraWidth() { return this.leftTextExtraWidth; }
  
  public static boolean useCheckAndArrow(JMenuItem paramJMenuItem) {
    boolean bool = true;
    if (paramJMenuItem instanceof JMenu && ((JMenu)paramJMenuItem).isTopLevelMenu())
      bool = false; 
    return bool;
  }
  
  public static class ColumnAlignment {
    private int checkAlignment;
    
    private int iconAlignment;
    
    private int textAlignment;
    
    private int accAlignment;
    
    private int arrowAlignment;
    
    public static final ColumnAlignment LEFT_ALIGNMENT = new ColumnAlignment(2, 2, 2, 2, 2);
    
    public static final ColumnAlignment RIGHT_ALIGNMENT = new ColumnAlignment(4, 4, 4, 4, 4);
    
    public ColumnAlignment(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      this.checkAlignment = param1Int1;
      this.iconAlignment = param1Int2;
      this.textAlignment = param1Int3;
      this.accAlignment = param1Int4;
      this.arrowAlignment = param1Int5;
    }
    
    public int getCheckAlignment() { return this.checkAlignment; }
    
    public int getIconAlignment() { return this.iconAlignment; }
    
    public int getTextAlignment() { return this.textAlignment; }
    
    public int getAccAlignment() { return this.accAlignment; }
    
    public int getArrowAlignment() { return this.arrowAlignment; }
  }
  
  public static class LayoutResult {
    private Rectangle iconRect = new Rectangle();
    
    private Rectangle textRect = new Rectangle();
    
    private Rectangle accRect = new Rectangle();
    
    private Rectangle checkRect = new Rectangle();
    
    private Rectangle arrowRect = new Rectangle();
    
    private Rectangle labelRect = new Rectangle();
    
    public LayoutResult() {}
    
    public LayoutResult(Rectangle param1Rectangle1, Rectangle param1Rectangle2, Rectangle param1Rectangle3, Rectangle param1Rectangle4, Rectangle param1Rectangle5, Rectangle param1Rectangle6) {}
    
    public Rectangle getIconRect() { return this.iconRect; }
    
    public void setIconRect(Rectangle param1Rectangle) { this.iconRect = param1Rectangle; }
    
    public Rectangle getTextRect() { return this.textRect; }
    
    public void setTextRect(Rectangle param1Rectangle) { this.textRect = param1Rectangle; }
    
    public Rectangle getAccRect() { return this.accRect; }
    
    public void setAccRect(Rectangle param1Rectangle) { this.accRect = param1Rectangle; }
    
    public Rectangle getCheckRect() { return this.checkRect; }
    
    public void setCheckRect(Rectangle param1Rectangle) { this.checkRect = param1Rectangle; }
    
    public Rectangle getArrowRect() { return this.arrowRect; }
    
    public void setArrowRect(Rectangle param1Rectangle) { this.arrowRect = param1Rectangle; }
    
    public Rectangle getLabelRect() { return this.labelRect; }
    
    public void setLabelRect(Rectangle param1Rectangle) { this.labelRect = param1Rectangle; }
    
    public Map<String, Rectangle> getAllRects() {
      HashMap hashMap = new HashMap();
      hashMap.put("checkRect", this.checkRect);
      hashMap.put("iconRect", this.iconRect);
      hashMap.put("textRect", this.textRect);
      hashMap.put("accRect", this.accRect);
      hashMap.put("arrowRect", this.arrowRect);
      hashMap.put("labelRect", this.labelRect);
      return hashMap;
    }
  }
  
  public static class RectSize {
    private int width;
    
    private int height;
    
    private int origWidth;
    
    private int maxWidth;
    
    public RectSize() {}
    
    public RectSize(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.width = param1Int1;
      this.height = param1Int2;
      this.origWidth = param1Int3;
      this.maxWidth = param1Int4;
    }
    
    public int getWidth() { return this.width; }
    
    public int getHeight() { return this.height; }
    
    public int getOrigWidth() { return this.origWidth; }
    
    public int getMaxWidth() { return this.maxWidth; }
    
    public void setWidth(int param1Int) { this.width = param1Int; }
    
    public void setHeight(int param1Int) { this.height = param1Int; }
    
    public void setOrigWidth(int param1Int) { this.origWidth = param1Int; }
    
    public void setMaxWidth(int param1Int) { this.maxWidth = param1Int; }
    
    public String toString() { return "[w=" + this.width + ",h=" + this.height + ",ow=" + this.origWidth + ",mw=" + this.maxWidth + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\MenuItemLayoutHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */