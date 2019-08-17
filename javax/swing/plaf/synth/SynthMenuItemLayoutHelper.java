package javax.swing.plaf.synth;

import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.plaf.synth.SynthIcon;

class SynthMenuItemLayoutHelper extends MenuItemLayoutHelper {
  public static final StringUIClientPropertyKey MAX_ACC_OR_ARROW_WIDTH = new StringUIClientPropertyKey("maxAccOrArrowWidth");
  
  public static final MenuItemLayoutHelper.ColumnAlignment LTR_ALIGNMENT_1 = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 4, 4);
  
  public static final MenuItemLayoutHelper.ColumnAlignment LTR_ALIGNMENT_2 = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 2, 4);
  
  public static final MenuItemLayoutHelper.ColumnAlignment RTL_ALIGNMENT_1 = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 2, 2);
  
  public static final MenuItemLayoutHelper.ColumnAlignment RTL_ALIGNMENT_2 = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 4, 2);
  
  private SynthContext context;
  
  private SynthContext accContext;
  
  private SynthStyle style;
  
  private SynthStyle accStyle;
  
  private SynthGraphicsUtils gu;
  
  private SynthGraphicsUtils accGu;
  
  private boolean alignAcceleratorText;
  
  private int maxAccOrArrowWidth;
  
  public SynthMenuItemLayoutHelper(SynthContext paramSynthContext1, SynthContext paramSynthContext2, JMenuItem paramJMenuItem, Icon paramIcon1, Icon paramIcon2, Rectangle paramRectangle, int paramInt, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2) {
    this.context = paramSynthContext1;
    this.accContext = paramSynthContext2;
    this.style = paramSynthContext1.getStyle();
    this.accStyle = paramSynthContext2.getStyle();
    this.gu = this.style.getGraphicsUtils(paramSynthContext1);
    this.accGu = this.accStyle.getGraphicsUtils(paramSynthContext2);
    this.alignAcceleratorText = getAlignAcceleratorText(paramString2);
    reset(paramJMenuItem, paramIcon1, paramIcon2, paramRectangle, paramInt, paramString1, paramBoolean1, this.style.getFont(paramSynthContext1), this.accStyle.getFont(paramSynthContext2), paramBoolean2, paramString2);
    setLeadingGap(0);
  }
  
  private boolean getAlignAcceleratorText(String paramString) { return this.style.getBoolean(this.context, paramString + ".alignAcceleratorText", true); }
  
  protected void calcWidthsAndHeights() {
    if (getIcon() != null) {
      getIconSize().setWidth(SynthIcon.getIconWidth(getIcon(), this.context));
      getIconSize().setHeight(SynthIcon.getIconHeight(getIcon(), this.context));
    } 
    if (!getAccText().equals("")) {
      getAccSize().setWidth(this.accGu.computeStringWidth(getAccContext(), getAccFontMetrics().getFont(), getAccFontMetrics(), getAccText()));
      getAccSize().setHeight(getAccFontMetrics().getHeight());
    } 
    if (getText() == null) {
      setText("");
    } else if (!getText().equals("")) {
      if (getHtmlView() != null) {
        getTextSize().setWidth((int)getHtmlView().getPreferredSpan(0));
        getTextSize().setHeight((int)getHtmlView().getPreferredSpan(1));
      } else {
        getTextSize().setWidth(this.gu.computeStringWidth(this.context, getFontMetrics().getFont(), getFontMetrics(), getText()));
        getTextSize().setHeight(getFontMetrics().getHeight());
      } 
    } 
    if (useCheckAndArrow()) {
      if (getCheckIcon() != null) {
        getCheckSize().setWidth(SynthIcon.getIconWidth(getCheckIcon(), this.context));
        getCheckSize().setHeight(SynthIcon.getIconHeight(getCheckIcon(), this.context));
      } 
      if (getArrowIcon() != null) {
        getArrowSize().setWidth(SynthIcon.getIconWidth(getArrowIcon(), this.context));
        getArrowSize().setHeight(SynthIcon.getIconHeight(getArrowIcon(), this.context));
      } 
    } 
    if (isColumnLayout()) {
      getLabelSize().setWidth(getIconSize().getWidth() + getTextSize().getWidth() + getGap());
      getLabelSize().setHeight(MenuItemLayoutHelper.max(new int[] { getCheckSize().getHeight(), getIconSize().getHeight(), getTextSize().getHeight(), getAccSize().getHeight(), getArrowSize().getHeight() }));
    } else {
      Rectangle rectangle1 = new Rectangle();
      Rectangle rectangle2 = new Rectangle();
      this.gu.layoutText(this.context, getFontMetrics(), getText(), getIcon(), getHorizontalAlignment(), getVerticalAlignment(), getHorizontalTextPosition(), getVerticalTextPosition(), getViewRect(), rectangle2, rectangle1, getGap());
      rectangle1.width += getLeftTextExtraWidth();
      Rectangle rectangle3 = rectangle2.union(rectangle1);
      getLabelSize().setHeight(rectangle3.height);
      getLabelSize().setWidth(rectangle3.width);
    } 
  }
  
  protected void calcMaxWidths() {
    calcMaxWidth(getCheckSize(), MAX_CHECK_WIDTH);
    this.maxAccOrArrowWidth = calcMaxValue(MAX_ACC_OR_ARROW_WIDTH, getArrowSize().getWidth());
    this.maxAccOrArrowWidth = calcMaxValue(MAX_ACC_OR_ARROW_WIDTH, getAccSize().getWidth());
    if (isColumnLayout()) {
      calcMaxWidth(getIconSize(), MAX_ICON_WIDTH);
      calcMaxWidth(getTextSize(), MAX_TEXT_WIDTH);
      int i = getGap();
      if (getIconSize().getMaxWidth() == 0 || getTextSize().getMaxWidth() == 0)
        i = 0; 
      getLabelSize().setMaxWidth(calcMaxValue(MAX_LABEL_WIDTH, getIconSize().getMaxWidth() + getTextSize().getMaxWidth() + i));
    } else {
      getIconSize().setMaxWidth(getParentIntProperty(MAX_ICON_WIDTH));
      calcMaxWidth(getLabelSize(), MAX_LABEL_WIDTH);
      int i = getLabelSize().getMaxWidth() - getIconSize().getMaxWidth();
      if (getIconSize().getMaxWidth() > 0)
        i -= getGap(); 
      getTextSize().setMaxWidth(calcMaxValue(MAX_TEXT_WIDTH, i));
    } 
  }
  
  public SynthContext getContext() { return this.context; }
  
  public SynthContext getAccContext() { return this.accContext; }
  
  public SynthStyle getStyle() { return this.style; }
  
  public SynthStyle getAccStyle() { return this.accStyle; }
  
  public SynthGraphicsUtils getGraphicsUtils() { return this.gu; }
  
  public SynthGraphicsUtils getAccGraphicsUtils() { return this.accGu; }
  
  public boolean alignAcceleratorText() { return this.alignAcceleratorText; }
  
  public int getMaxAccOrArrowWidth() { return this.maxAccOrArrowWidth; }
  
  protected void prepareForLayout(MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    (paramLayoutResult.getCheckRect()).width = getCheckSize().getMaxWidth();
    if (useCheckAndArrow() && !"".equals(getAccText())) {
      (paramLayoutResult.getAccRect()).width = this.maxAccOrArrowWidth;
    } else {
      (paramLayoutResult.getArrowRect()).width = this.maxAccOrArrowWidth;
    } 
  }
  
  public MenuItemLayoutHelper.ColumnAlignment getLTRColumnAlignment() { return alignAcceleratorText() ? LTR_ALIGNMENT_2 : LTR_ALIGNMENT_1; }
  
  public MenuItemLayoutHelper.ColumnAlignment getRTLColumnAlignment() { return alignAcceleratorText() ? RTL_ALIGNMENT_2 : RTL_ALIGNMENT_1; }
  
  protected void layoutIconAndTextInLabelRect(MenuItemLayoutHelper.LayoutResult paramLayoutResult) {
    paramLayoutResult.setTextRect(new Rectangle());
    paramLayoutResult.setIconRect(new Rectangle());
    this.gu.layoutText(this.context, getFontMetrics(), getText(), getIcon(), getHorizontalAlignment(), getVerticalAlignment(), getHorizontalTextPosition(), getVerticalTextPosition(), paramLayoutResult.getLabelRect(), paramLayoutResult.getIconRect(), paramLayoutResult.getTextRect(), getGap());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthMenuItemLayoutHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */