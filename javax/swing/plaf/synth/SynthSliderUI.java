package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import sun.swing.SwingUtilities2;

public class SynthSliderUI extends BasicSliderUI implements PropertyChangeListener, SynthUI {
  private Rectangle valueRect = new Rectangle();
  
  private boolean paintValue;
  
  private Dimension lastSize;
  
  private int trackHeight;
  
  private int trackBorder;
  
  private int thumbWidth;
  
  private int thumbHeight;
  
  private SynthStyle style;
  
  private SynthStyle sliderTrackStyle;
  
  private SynthStyle sliderThumbStyle;
  
  private boolean thumbActive;
  
  private boolean thumbPressed;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthSliderUI((JSlider)paramJComponent); }
  
  protected SynthSliderUI(JSlider paramJSlider) { super(paramJSlider); }
  
  protected void installDefaults(JSlider paramJSlider) { updateStyle(paramJSlider); }
  
  protected void uninstallDefaults(JSlider paramJSlider) {
    SynthContext synthContext = getContext(paramJSlider, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    synthContext = getContext(paramJSlider, Region.SLIDER_TRACK, 1);
    this.sliderTrackStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.sliderTrackStyle = null;
    synthContext = getContext(paramJSlider, Region.SLIDER_THUMB, 1);
    this.sliderThumbStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.sliderThumbStyle = null;
  }
  
  protected void installListeners(JSlider paramJSlider) {
    super.installListeners(paramJSlider);
    paramJSlider.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners(JSlider paramJSlider) {
    paramJSlider.removePropertyChangeListener(this);
    super.uninstallListeners(paramJSlider);
  }
  
  private void updateStyle(JSlider paramJSlider) {
    SynthContext synthContext = getContext(paramJSlider, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      this.thumbWidth = this.style.getInt(synthContext, "Slider.thumbWidth", 30);
      this.thumbHeight = this.style.getInt(synthContext, "Slider.thumbHeight", 14);
      String str = (String)this.slider.getClientProperty("JComponent.sizeVariant");
      if (str != null)
        if ("large".equals(str)) {
          this.thumbWidth = (int)(this.thumbWidth * 1.15D);
          this.thumbHeight = (int)(this.thumbHeight * 1.15D);
        } else if ("small".equals(str)) {
          this.thumbWidth = (int)(this.thumbWidth * 0.857D);
          this.thumbHeight = (int)(this.thumbHeight * 0.857D);
        } else if ("mini".equals(str)) {
          this.thumbWidth = (int)(this.thumbWidth * 0.784D);
          this.thumbHeight = (int)(this.thumbHeight * 0.784D);
        }  
      this.trackBorder = this.style.getInt(synthContext, "Slider.trackBorder", 1);
      this.trackHeight = this.thumbHeight + this.trackBorder * 2;
      this.paintValue = this.style.getBoolean(synthContext, "Slider.paintValue", true);
      if (synthStyle != null) {
        uninstallKeyboardActions(paramJSlider);
        installKeyboardActions(paramJSlider);
      } 
    } 
    synthContext.dispose();
    synthContext = getContext(paramJSlider, Region.SLIDER_TRACK, 1);
    this.sliderTrackStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
    synthContext = getContext(paramJSlider, Region.SLIDER_THUMB, 1);
    this.sliderThumbStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected BasicSliderUI.TrackListener createTrackListener(JSlider paramJSlider) { return new SynthTrackListener(null); }
  
  private void updateThumbState(int paramInt1, int paramInt2) { setThumbActive(this.thumbRect.contains(paramInt1, paramInt2)); }
  
  private void updateThumbState(int paramInt1, int paramInt2, boolean paramBoolean) {
    updateThumbState(paramInt1, paramInt2);
    setThumbPressed(paramBoolean);
  }
  
  private void setThumbActive(boolean paramBoolean) {
    if (this.thumbActive != paramBoolean) {
      this.thumbActive = paramBoolean;
      this.slider.repaint(this.thumbRect);
    } 
  }
  
  private void setThumbPressed(boolean paramBoolean) {
    if (this.thumbPressed != paramBoolean) {
      this.thumbPressed = paramBoolean;
      this.slider.repaint(this.thumbRect);
    } 
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    if (paramJComponent == null)
      throw new NullPointerException("Component must be non-null"); 
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException("Width and height must be >= 0"); 
    if (this.slider.getPaintLabels() && labelsHaveSameBaselines()) {
      Insets insets = new Insets(0, 0, 0, 0);
      SynthContext synthContext = getContext(this.slider, Region.SLIDER_TRACK);
      this.style.getInsets(synthContext, insets);
      synthContext.dispose();
      if (this.slider.getOrientation() == 0) {
        int i = 0;
        if (this.paintValue) {
          SynthContext synthContext1 = getContext(this.slider);
          i = synthContext1.getStyle().getGraphicsUtils(synthContext1).getMaximumCharHeight(synthContext1);
          synthContext1.dispose();
        } 
        int j = 0;
        if (this.slider.getPaintTicks())
          j = getTickLength(); 
        int k = getHeightOfTallestLabel();
        int m = i + this.trackHeight + insets.top + insets.bottom + j + k + 4;
        int n = paramInt2 / 2 - m / 2;
        n += i + 2;
        n += this.trackHeight + insets.top + insets.bottom;
        n += j + 2;
        JComponent jComponent = (JComponent)this.slider.getLabelTable().elements().nextElement();
        Dimension dimension = jComponent.getPreferredSize();
        return n + jComponent.getBaseline(dimension.width, dimension.height);
      } 
      Integer integer = this.slider.getInverted() ? getLowestValue() : getHighestValue();
      if (integer != null) {
        int i = this.insetCache.top;
        int j = 0;
        if (this.paintValue) {
          SynthContext synthContext1 = getContext(this.slider);
          j = synthContext1.getStyle().getGraphicsUtils(synthContext1).getMaximumCharHeight(synthContext1);
          synthContext1.dispose();
        } 
        int k = paramInt2 - this.insetCache.top - this.insetCache.bottom;
        int m = i + j;
        int n = k - j;
        int i1 = yPositionForValue(integer.intValue(), m, n);
        JComponent jComponent = (JComponent)this.slider.getLabelTable().get(integer);
        Dimension dimension = jComponent.getPreferredSize();
        return i1 - dimension.height / 2 + jComponent.getBaseline(dimension.width, dimension.height);
      } 
    } 
    return -1;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    recalculateIfInsetsChanged();
    Dimension dimension = new Dimension(this.contentRect.width, this.contentRect.height);
    if (this.slider.getOrientation() == 1) {
      dimension.height = 200;
    } else {
      dimension.width = 200;
    } 
    Insets insets = this.slider.getInsets();
    dimension.width += insets.left + insets.right;
    dimension.height += insets.top + insets.bottom;
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    recalculateIfInsetsChanged();
    Dimension dimension = new Dimension(this.contentRect.width, this.contentRect.height);
    if (this.slider.getOrientation() == 1) {
      dimension.height = this.thumbRect.height + this.insetCache.top + this.insetCache.bottom;
    } else {
      dimension.width = this.thumbRect.width + this.insetCache.left + this.insetCache.right;
    } 
    return dimension;
  }
  
  protected void calculateGeometry() {
    calculateThumbSize();
    layout();
    calculateThumbLocation();
  }
  
  protected void layout() { // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: getfield slider : Ljavax/swing/JSlider;
    //   5: invokevirtual getContext : (Ljavax/swing/JComponent;)Ljavax/swing/plaf/synth/SynthContext;
    //   8: astore_1
    //   9: aload_0
    //   10: getfield style : Ljavax/swing/plaf/synth/SynthStyle;
    //   13: aload_1
    //   14: invokevirtual getGraphicsUtils : (Ljavax/swing/plaf/synth/SynthContext;)Ljavax/swing/plaf/synth/SynthGraphicsUtils;
    //   17: astore_2
    //   18: new java/awt/Insets
    //   21: dup
    //   22: iconst_0
    //   23: iconst_0
    //   24: iconst_0
    //   25: iconst_0
    //   26: invokespecial <init> : (IIII)V
    //   29: astore_3
    //   30: aload_0
    //   31: aload_0
    //   32: getfield slider : Ljavax/swing/JSlider;
    //   35: getstatic javax/swing/plaf/synth/Region.SLIDER_TRACK : Ljavax/swing/plaf/synth/Region;
    //   38: invokespecial getContext : (Ljavax/swing/JComponent;Ljavax/swing/plaf/synth/Region;)Ljavax/swing/plaf/synth/SynthContext;
    //   41: astore #4
    //   43: aload_0
    //   44: getfield style : Ljavax/swing/plaf/synth/SynthStyle;
    //   47: aload #4
    //   49: aload_3
    //   50: invokevirtual getInsets : (Ljavax/swing/plaf/synth/SynthContext;Ljava/awt/Insets;)Ljava/awt/Insets;
    //   53: pop
    //   54: aload #4
    //   56: invokevirtual dispose : ()V
    //   59: aload_0
    //   60: getfield slider : Ljavax/swing/JSlider;
    //   63: invokevirtual getOrientation : ()I
    //   66: ifne -> 629
    //   69: aload_0
    //   70: getfield valueRect : Ljava/awt/Rectangle;
    //   73: iconst_0
    //   74: putfield height : I
    //   77: aload_0
    //   78: getfield paintValue : Z
    //   81: ifeq -> 96
    //   84: aload_0
    //   85: getfield valueRect : Ljava/awt/Rectangle;
    //   88: aload_2
    //   89: aload_1
    //   90: invokevirtual getMaximumCharHeight : (Ljavax/swing/plaf/synth/SynthContext;)I
    //   93: putfield height : I
    //   96: aload_0
    //   97: getfield trackRect : Ljava/awt/Rectangle;
    //   100: aload_0
    //   101: getfield trackHeight : I
    //   104: putfield height : I
    //   107: aload_0
    //   108: getfield tickRect : Ljava/awt/Rectangle;
    //   111: iconst_0
    //   112: putfield height : I
    //   115: aload_0
    //   116: getfield slider : Ljavax/swing/JSlider;
    //   119: invokevirtual getPaintTicks : ()Z
    //   122: ifeq -> 136
    //   125: aload_0
    //   126: getfield tickRect : Ljava/awt/Rectangle;
    //   129: aload_0
    //   130: invokevirtual getTickLength : ()I
    //   133: putfield height : I
    //   136: aload_0
    //   137: getfield labelRect : Ljava/awt/Rectangle;
    //   140: iconst_0
    //   141: putfield height : I
    //   144: aload_0
    //   145: getfield slider : Ljavax/swing/JSlider;
    //   148: invokevirtual getPaintLabels : ()Z
    //   151: ifeq -> 165
    //   154: aload_0
    //   155: getfield labelRect : Ljava/awt/Rectangle;
    //   158: aload_0
    //   159: invokevirtual getHeightOfTallestLabel : ()I
    //   162: putfield height : I
    //   165: aload_0
    //   166: getfield contentRect : Ljava/awt/Rectangle;
    //   169: aload_0
    //   170: getfield valueRect : Ljava/awt/Rectangle;
    //   173: getfield height : I
    //   176: aload_0
    //   177: getfield trackRect : Ljava/awt/Rectangle;
    //   180: getfield height : I
    //   183: iadd
    //   184: aload_3
    //   185: getfield top : I
    //   188: iadd
    //   189: aload_3
    //   190: getfield bottom : I
    //   193: iadd
    //   194: aload_0
    //   195: getfield tickRect : Ljava/awt/Rectangle;
    //   198: getfield height : I
    //   201: iadd
    //   202: aload_0
    //   203: getfield labelRect : Ljava/awt/Rectangle;
    //   206: getfield height : I
    //   209: iadd
    //   210: iconst_4
    //   211: iadd
    //   212: putfield height : I
    //   215: aload_0
    //   216: getfield contentRect : Ljava/awt/Rectangle;
    //   219: aload_0
    //   220: getfield slider : Ljavax/swing/JSlider;
    //   223: invokevirtual getWidth : ()I
    //   226: aload_0
    //   227: getfield insetCache : Ljava/awt/Insets;
    //   230: getfield left : I
    //   233: isub
    //   234: aload_0
    //   235: getfield insetCache : Ljava/awt/Insets;
    //   238: getfield right : I
    //   241: isub
    //   242: putfield width : I
    //   245: iconst_0
    //   246: istore #5
    //   248: aload_0
    //   249: getfield slider : Ljavax/swing/JSlider;
    //   252: invokevirtual getPaintLabels : ()Z
    //   255: ifeq -> 418
    //   258: aload_0
    //   259: getfield trackRect : Ljava/awt/Rectangle;
    //   262: aload_0
    //   263: getfield insetCache : Ljava/awt/Insets;
    //   266: getfield left : I
    //   269: putfield x : I
    //   272: aload_0
    //   273: getfield trackRect : Ljava/awt/Rectangle;
    //   276: aload_0
    //   277: getfield contentRect : Ljava/awt/Rectangle;
    //   280: getfield width : I
    //   283: putfield width : I
    //   286: aload_0
    //   287: getfield slider : Ljavax/swing/JSlider;
    //   290: invokevirtual getLabelTable : ()Ljava/util/Dictionary;
    //   293: astore #6
    //   295: aload #6
    //   297: ifnull -> 418
    //   300: aload_0
    //   301: getfield slider : Ljavax/swing/JSlider;
    //   304: invokevirtual getMinimum : ()I
    //   307: istore #7
    //   309: aload_0
    //   310: getfield slider : Ljavax/swing/JSlider;
    //   313: invokevirtual getMaximum : ()I
    //   316: istore #8
    //   318: ldc 2147483647
    //   320: istore #9
    //   322: ldc -2147483648
    //   324: istore #10
    //   326: aload #6
    //   328: invokevirtual keys : ()Ljava/util/Enumeration;
    //   331: astore #11
    //   333: aload #11
    //   335: invokeinterface hasMoreElements : ()Z
    //   340: ifeq -> 397
    //   343: aload #11
    //   345: invokeinterface nextElement : ()Ljava/lang/Object;
    //   350: checkcast java/lang/Integer
    //   353: invokevirtual intValue : ()I
    //   356: istore #12
    //   358: iload #12
    //   360: iload #7
    //   362: if_icmplt -> 376
    //   365: iload #12
    //   367: iload #9
    //   369: if_icmpge -> 376
    //   372: iload #12
    //   374: istore #9
    //   376: iload #12
    //   378: iload #8
    //   380: if_icmpgt -> 394
    //   383: iload #12
    //   385: iload #10
    //   387: if_icmple -> 394
    //   390: iload #12
    //   392: istore #10
    //   394: goto -> 333
    //   397: aload_0
    //   398: iload #9
    //   400: invokespecial getPadForLabel : (I)I
    //   403: istore #5
    //   405: iload #5
    //   407: aload_0
    //   408: iload #10
    //   410: invokespecial getPadForLabel : (I)I
    //   413: invokestatic max : (II)I
    //   416: istore #5
    //   418: aload_0
    //   419: getfield valueRect : Ljava/awt/Rectangle;
    //   422: aload_0
    //   423: getfield trackRect : Ljava/awt/Rectangle;
    //   426: aload_0
    //   427: getfield tickRect : Ljava/awt/Rectangle;
    //   430: aload_0
    //   431: getfield labelRect : Ljava/awt/Rectangle;
    //   434: aload_0
    //   435: getfield insetCache : Ljava/awt/Insets;
    //   438: getfield left : I
    //   441: iload #5
    //   443: iadd
    //   444: dup_x1
    //   445: putfield x : I
    //   448: dup_x1
    //   449: putfield x : I
    //   452: dup_x1
    //   453: putfield x : I
    //   456: putfield x : I
    //   459: aload_0
    //   460: getfield valueRect : Ljava/awt/Rectangle;
    //   463: aload_0
    //   464: getfield trackRect : Ljava/awt/Rectangle;
    //   467: aload_0
    //   468: getfield tickRect : Ljava/awt/Rectangle;
    //   471: aload_0
    //   472: getfield labelRect : Ljava/awt/Rectangle;
    //   475: aload_0
    //   476: getfield contentRect : Ljava/awt/Rectangle;
    //   479: getfield width : I
    //   482: iload #5
    //   484: iconst_2
    //   485: imul
    //   486: isub
    //   487: dup_x1
    //   488: putfield width : I
    //   491: dup_x1
    //   492: putfield width : I
    //   495: dup_x1
    //   496: putfield width : I
    //   499: putfield width : I
    //   502: aload_0
    //   503: getfield slider : Ljavax/swing/JSlider;
    //   506: invokevirtual getHeight : ()I
    //   509: iconst_2
    //   510: idiv
    //   511: aload_0
    //   512: getfield contentRect : Ljava/awt/Rectangle;
    //   515: getfield height : I
    //   518: iconst_2
    //   519: idiv
    //   520: isub
    //   521: istore #6
    //   523: aload_0
    //   524: getfield valueRect : Ljava/awt/Rectangle;
    //   527: iload #6
    //   529: putfield y : I
    //   532: iload #6
    //   534: aload_0
    //   535: getfield valueRect : Ljava/awt/Rectangle;
    //   538: getfield height : I
    //   541: iconst_2
    //   542: iadd
    //   543: iadd
    //   544: istore #6
    //   546: aload_0
    //   547: getfield trackRect : Ljava/awt/Rectangle;
    //   550: iload #6
    //   552: aload_3
    //   553: getfield top : I
    //   556: iadd
    //   557: putfield y : I
    //   560: iload #6
    //   562: aload_0
    //   563: getfield trackRect : Ljava/awt/Rectangle;
    //   566: getfield height : I
    //   569: aload_3
    //   570: getfield top : I
    //   573: iadd
    //   574: aload_3
    //   575: getfield bottom : I
    //   578: iadd
    //   579: iadd
    //   580: istore #6
    //   582: aload_0
    //   583: getfield tickRect : Ljava/awt/Rectangle;
    //   586: iload #6
    //   588: putfield y : I
    //   591: iload #6
    //   593: aload_0
    //   594: getfield tickRect : Ljava/awt/Rectangle;
    //   597: getfield height : I
    //   600: iconst_2
    //   601: iadd
    //   602: iadd
    //   603: istore #6
    //   605: aload_0
    //   606: getfield labelRect : Ljava/awt/Rectangle;
    //   609: iload #6
    //   611: putfield y : I
    //   614: iload #6
    //   616: aload_0
    //   617: getfield labelRect : Ljava/awt/Rectangle;
    //   620: getfield height : I
    //   623: iadd
    //   624: istore #6
    //   626: goto -> 1234
    //   629: aload_0
    //   630: getfield trackRect : Ljava/awt/Rectangle;
    //   633: aload_0
    //   634: getfield trackHeight : I
    //   637: putfield width : I
    //   640: aload_0
    //   641: getfield tickRect : Ljava/awt/Rectangle;
    //   644: iconst_0
    //   645: putfield width : I
    //   648: aload_0
    //   649: getfield slider : Ljavax/swing/JSlider;
    //   652: invokevirtual getPaintTicks : ()Z
    //   655: ifeq -> 669
    //   658: aload_0
    //   659: getfield tickRect : Ljava/awt/Rectangle;
    //   662: aload_0
    //   663: invokevirtual getTickLength : ()I
    //   666: putfield width : I
    //   669: aload_0
    //   670: getfield labelRect : Ljava/awt/Rectangle;
    //   673: iconst_0
    //   674: putfield width : I
    //   677: aload_0
    //   678: getfield slider : Ljavax/swing/JSlider;
    //   681: invokevirtual getPaintLabels : ()Z
    //   684: ifeq -> 698
    //   687: aload_0
    //   688: getfield labelRect : Ljava/awt/Rectangle;
    //   691: aload_0
    //   692: invokevirtual getWidthOfWidestLabel : ()I
    //   695: putfield width : I
    //   698: aload_0
    //   699: getfield valueRect : Ljava/awt/Rectangle;
    //   702: aload_0
    //   703: getfield insetCache : Ljava/awt/Insets;
    //   706: getfield top : I
    //   709: putfield y : I
    //   712: aload_0
    //   713: getfield valueRect : Ljava/awt/Rectangle;
    //   716: iconst_0
    //   717: putfield height : I
    //   720: aload_0
    //   721: getfield paintValue : Z
    //   724: ifeq -> 739
    //   727: aload_0
    //   728: getfield valueRect : Ljava/awt/Rectangle;
    //   731: aload_2
    //   732: aload_1
    //   733: invokevirtual getMaximumCharHeight : (Ljavax/swing/plaf/synth/SynthContext;)I
    //   736: putfield height : I
    //   739: aload_0
    //   740: getfield slider : Ljavax/swing/JSlider;
    //   743: aload_0
    //   744: getfield slider : Ljavax/swing/JSlider;
    //   747: invokevirtual getFont : ()Ljava/awt/Font;
    //   750: invokevirtual getFontMetrics : (Ljava/awt/Font;)Ljava/awt/FontMetrics;
    //   753: astore #5
    //   755: aload_0
    //   756: getfield valueRect : Ljava/awt/Rectangle;
    //   759: aload_2
    //   760: aload_1
    //   761: aload_0
    //   762: getfield slider : Ljavax/swing/JSlider;
    //   765: invokevirtual getFont : ()Ljava/awt/Font;
    //   768: aload #5
    //   770: new java/lang/StringBuilder
    //   773: dup
    //   774: invokespecial <init> : ()V
    //   777: ldc ''
    //   779: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   782: aload_0
    //   783: getfield slider : Ljavax/swing/JSlider;
    //   786: invokevirtual getMaximum : ()I
    //   789: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   792: invokevirtual toString : ()Ljava/lang/String;
    //   795: invokevirtual computeStringWidth : (Ljavax/swing/plaf/synth/SynthContext;Ljava/awt/Font;Ljava/awt/FontMetrics;Ljava/lang/String;)I
    //   798: aload_2
    //   799: aload_1
    //   800: aload_0
    //   801: getfield slider : Ljavax/swing/JSlider;
    //   804: invokevirtual getFont : ()Ljava/awt/Font;
    //   807: aload #5
    //   809: new java/lang/StringBuilder
    //   812: dup
    //   813: invokespecial <init> : ()V
    //   816: ldc ''
    //   818: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   821: aload_0
    //   822: getfield slider : Ljavax/swing/JSlider;
    //   825: invokevirtual getMinimum : ()I
    //   828: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   831: invokevirtual toString : ()Ljava/lang/String;
    //   834: invokevirtual computeStringWidth : (Ljavax/swing/plaf/synth/SynthContext;Ljava/awt/Font;Ljava/awt/FontMetrics;Ljava/lang/String;)I
    //   837: invokestatic max : (II)I
    //   840: putfield width : I
    //   843: aload_0
    //   844: getfield valueRect : Ljava/awt/Rectangle;
    //   847: getfield width : I
    //   850: iconst_2
    //   851: idiv
    //   852: istore #6
    //   854: aload_3
    //   855: getfield left : I
    //   858: aload_0
    //   859: getfield trackRect : Ljava/awt/Rectangle;
    //   862: getfield width : I
    //   865: iconst_2
    //   866: idiv
    //   867: iadd
    //   868: istore #7
    //   870: aload_0
    //   871: getfield trackRect : Ljava/awt/Rectangle;
    //   874: getfield width : I
    //   877: iconst_2
    //   878: idiv
    //   879: aload_3
    //   880: getfield right : I
    //   883: iadd
    //   884: aload_0
    //   885: getfield tickRect : Ljava/awt/Rectangle;
    //   888: getfield width : I
    //   891: iadd
    //   892: aload_0
    //   893: getfield labelRect : Ljava/awt/Rectangle;
    //   896: getfield width : I
    //   899: iadd
    //   900: istore #8
    //   902: aload_0
    //   903: getfield contentRect : Ljava/awt/Rectangle;
    //   906: iload #7
    //   908: iload #6
    //   910: invokestatic max : (II)I
    //   913: iload #8
    //   915: iload #6
    //   917: invokestatic max : (II)I
    //   920: iadd
    //   921: iconst_2
    //   922: iadd
    //   923: aload_0
    //   924: getfield insetCache : Ljava/awt/Insets;
    //   927: getfield left : I
    //   930: iadd
    //   931: aload_0
    //   932: getfield insetCache : Ljava/awt/Insets;
    //   935: getfield right : I
    //   938: iadd
    //   939: putfield width : I
    //   942: aload_0
    //   943: getfield contentRect : Ljava/awt/Rectangle;
    //   946: aload_0
    //   947: getfield slider : Ljavax/swing/JSlider;
    //   950: invokevirtual getHeight : ()I
    //   953: aload_0
    //   954: getfield insetCache : Ljava/awt/Insets;
    //   957: getfield top : I
    //   960: isub
    //   961: aload_0
    //   962: getfield insetCache : Ljava/awt/Insets;
    //   965: getfield bottom : I
    //   968: isub
    //   969: putfield height : I
    //   972: aload_0
    //   973: getfield trackRect : Ljava/awt/Rectangle;
    //   976: aload_0
    //   977: getfield tickRect : Ljava/awt/Rectangle;
    //   980: aload_0
    //   981: getfield labelRect : Ljava/awt/Rectangle;
    //   984: aload_0
    //   985: getfield valueRect : Ljava/awt/Rectangle;
    //   988: getfield y : I
    //   991: aload_0
    //   992: getfield valueRect : Ljava/awt/Rectangle;
    //   995: getfield height : I
    //   998: iadd
    //   999: dup_x1
    //   1000: putfield y : I
    //   1003: dup_x1
    //   1004: putfield y : I
    //   1007: putfield y : I
    //   1010: aload_0
    //   1011: getfield trackRect : Ljava/awt/Rectangle;
    //   1014: aload_0
    //   1015: getfield tickRect : Ljava/awt/Rectangle;
    //   1018: aload_0
    //   1019: getfield labelRect : Ljava/awt/Rectangle;
    //   1022: aload_0
    //   1023: getfield contentRect : Ljava/awt/Rectangle;
    //   1026: getfield height : I
    //   1029: aload_0
    //   1030: getfield valueRect : Ljava/awt/Rectangle;
    //   1033: getfield height : I
    //   1036: isub
    //   1037: dup_x1
    //   1038: putfield height : I
    //   1041: dup_x1
    //   1042: putfield height : I
    //   1045: putfield height : I
    //   1048: aload_0
    //   1049: getfield slider : Ljavax/swing/JSlider;
    //   1052: invokevirtual getWidth : ()I
    //   1055: iconst_2
    //   1056: idiv
    //   1057: aload_0
    //   1058: getfield contentRect : Ljava/awt/Rectangle;
    //   1061: getfield width : I
    //   1064: iconst_2
    //   1065: idiv
    //   1066: isub
    //   1067: istore #9
    //   1069: aload_0
    //   1070: getfield slider : Ljavax/swing/JSlider;
    //   1073: invokestatic isLeftToRight : (Ljava/awt/Component;)Z
    //   1076: ifeq -> 1163
    //   1079: iload #6
    //   1081: iload #7
    //   1083: if_icmple -> 1096
    //   1086: iload #9
    //   1088: iload #6
    //   1090: iload #7
    //   1092: isub
    //   1093: iadd
    //   1094: istore #9
    //   1096: aload_0
    //   1097: getfield trackRect : Ljava/awt/Rectangle;
    //   1100: iload #9
    //   1102: aload_3
    //   1103: getfield left : I
    //   1106: iadd
    //   1107: putfield x : I
    //   1110: iload #9
    //   1112: aload_3
    //   1113: getfield left : I
    //   1116: aload_0
    //   1117: getfield trackRect : Ljava/awt/Rectangle;
    //   1120: getfield width : I
    //   1123: iadd
    //   1124: aload_3
    //   1125: getfield right : I
    //   1128: iadd
    //   1129: iadd
    //   1130: istore #9
    //   1132: aload_0
    //   1133: getfield tickRect : Ljava/awt/Rectangle;
    //   1136: iload #9
    //   1138: putfield x : I
    //   1141: aload_0
    //   1142: getfield labelRect : Ljava/awt/Rectangle;
    //   1145: iload #9
    //   1147: aload_0
    //   1148: getfield tickRect : Ljava/awt/Rectangle;
    //   1151: getfield width : I
    //   1154: iadd
    //   1155: iconst_2
    //   1156: iadd
    //   1157: putfield x : I
    //   1160: goto -> 1234
    //   1163: iload #6
    //   1165: iload #8
    //   1167: if_icmple -> 1180
    //   1170: iload #9
    //   1172: iload #6
    //   1174: iload #8
    //   1176: isub
    //   1177: iadd
    //   1178: istore #9
    //   1180: aload_0
    //   1181: getfield labelRect : Ljava/awt/Rectangle;
    //   1184: iload #9
    //   1186: putfield x : I
    //   1189: iload #9
    //   1191: aload_0
    //   1192: getfield labelRect : Ljava/awt/Rectangle;
    //   1195: getfield width : I
    //   1198: iconst_2
    //   1199: iadd
    //   1200: iadd
    //   1201: istore #9
    //   1203: aload_0
    //   1204: getfield tickRect : Ljava/awt/Rectangle;
    //   1207: iload #9
    //   1209: putfield x : I
    //   1212: aload_0
    //   1213: getfield trackRect : Ljava/awt/Rectangle;
    //   1216: iload #9
    //   1218: aload_0
    //   1219: getfield tickRect : Ljava/awt/Rectangle;
    //   1222: getfield width : I
    //   1225: iadd
    //   1226: aload_3
    //   1227: getfield left : I
    //   1230: iadd
    //   1231: putfield x : I
    //   1234: aload_1
    //   1235: invokevirtual dispose : ()V
    //   1238: aload_0
    //   1239: aload_0
    //   1240: getfield slider : Ljavax/swing/JSlider;
    //   1243: invokevirtual getSize : ()Ljava/awt/Dimension;
    //   1246: putfield lastSize : Ljava/awt/Dimension;
    //   1249: return }
  
  private int getPadForLabel(int paramInt) {
    int i = 0;
    JComponent jComponent = (JComponent)this.slider.getLabelTable().get(Integer.valueOf(paramInt));
    if (jComponent != null) {
      int j = xPositionForValue(paramInt);
      int k = (jComponent.getPreferredSize()).width / 2;
      if (j - k < this.insetCache.left)
        i = Math.max(i, this.insetCache.left - j - k); 
      if (j + k > this.slider.getWidth() - this.insetCache.right)
        i = Math.max(i, j + k - this.slider.getWidth() - this.insetCache.right); 
    } 
    return i;
  }
  
  protected void calculateThumbLocation() {
    super.calculateThumbLocation();
    if (this.slider.getOrientation() == 0) {
      this.thumbRect.y += this.trackBorder;
    } else {
      this.thumbRect.x += this.trackBorder;
    } 
    Point point = this.slider.getMousePosition();
    if (point != null)
      updateThumbState(point.x, point.y); 
  }
  
  public void setThumbLocation(int paramInt1, int paramInt2) {
    super.setThumbLocation(paramInt1, paramInt2);
    this.slider.repaint(this.valueRect.x, this.valueRect.y, this.valueRect.width, this.valueRect.height);
    setThumbActive(false);
  }
  
  protected int xPositionForValue(int paramInt) {
    int i = this.slider.getMinimum();
    int j = this.slider.getMaximum();
    int k = this.trackRect.x + this.thumbRect.width / 2 + this.trackBorder;
    int m = this.trackRect.x + this.trackRect.width - this.thumbRect.width / 2 - this.trackBorder;
    int n = m - k;
    double d1 = j - i;
    double d2 = n / d1;
    if (!drawInverted()) {
      null = k;
      null = (int)(null + Math.round(d2 * (paramInt - i)));
    } else {
      null = m;
      null = (int)(null - Math.round(d2 * (paramInt - i)));
    } 
    null = Math.max(k, null);
    return Math.min(m, null);
  }
  
  protected int yPositionForValue(int paramInt1, int paramInt2, int paramInt3) {
    int i = this.slider.getMinimum();
    int j = this.slider.getMaximum();
    int k = paramInt2 + this.thumbRect.height / 2 + this.trackBorder;
    int m = paramInt2 + paramInt3 - this.thumbRect.height / 2 - this.trackBorder;
    int n = m - k;
    double d1 = j - i;
    double d2 = n / d1;
    if (!drawInverted()) {
      null = k;
      null = (int)(null + Math.round(d2 * (j - paramInt1)));
    } else {
      null = k;
      null = (int)(null + Math.round(d2 * (paramInt1 - i)));
    } 
    null = Math.max(k, null);
    return Math.min(m, null);
  }
  
  public int valueForYPosition(int paramInt) {
    int i;
    int j = this.slider.getMinimum();
    int k = this.slider.getMaximum();
    int m = this.trackRect.y + this.thumbRect.height / 2 + this.trackBorder;
    int n = this.trackRect.y + this.trackRect.height - this.thumbRect.height / 2 - this.trackBorder;
    int i1 = n - m;
    if (paramInt <= m) {
      i = drawInverted() ? j : k;
    } else if (paramInt >= n) {
      i = drawInverted() ? k : j;
    } else {
      int i2 = paramInt - m;
      double d1 = k - j;
      double d2 = d1 / i1;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? (j + i3) : (k - i3);
    } 
    return i;
  }
  
  public int valueForXPosition(int paramInt) {
    int i;
    int j = this.slider.getMinimum();
    int k = this.slider.getMaximum();
    int m = this.trackRect.x + this.thumbRect.width / 2 + this.trackBorder;
    int n = this.trackRect.x + this.trackRect.width - this.thumbRect.width / 2 - this.trackBorder;
    int i1 = n - m;
    if (paramInt <= m) {
      i = drawInverted() ? k : j;
    } else if (paramInt >= n) {
      i = drawInverted() ? j : k;
    } else {
      int i2 = paramInt - m;
      double d1 = k - j;
      double d2 = d1 / i1;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? (k - i3) : (j + i3);
    } 
    return i;
  }
  
  protected Dimension getThumbSize() {
    Dimension dimension = new Dimension();
    if (this.slider.getOrientation() == 1) {
      dimension.width = this.thumbHeight;
      dimension.height = this.thumbWidth;
    } else {
      dimension.width = this.thumbWidth;
      dimension.height = this.thumbHeight;
    } 
    return dimension;
  }
  
  protected void recalculateIfInsetsChanged() {
    SynthContext synthContext = getContext(this.slider);
    Insets insets1 = this.style.getInsets(synthContext, null);
    Insets insets2 = this.slider.getInsets();
    insets1.left += insets2.left;
    insets1.right += insets2.right;
    insets1.top += insets2.top;
    insets1.bottom += insets2.bottom;
    if (!insets1.equals(this.insetCache)) {
      this.insetCache = insets1;
      calculateGeometry();
    } 
    synthContext.dispose();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion) { return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion)); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) {
    SynthStyle synthStyle = null;
    if (paramRegion == Region.SLIDER_TRACK) {
      synthStyle = this.sliderTrackStyle;
    } else if (paramRegion == Region.SLIDER_THUMB) {
      synthStyle = this.sliderThumbStyle;
    } 
    return SynthContext.getContext(paramJComponent, paramRegion, synthStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion) {
    if (paramRegion == Region.SLIDER_THUMB && this.thumbActive && paramJComponent.isEnabled()) {
      char c = this.thumbPressed ? '\004' : '\002';
      if (paramJComponent.isFocusOwner())
        c |= 0x100; 
      return c;
    } 
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintSliderBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), this.slider.getOrientation());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    recalculateIfInsetsChanged();
    recalculateIfOrientationChanged();
    Rectangle rectangle = paramGraphics.getClipBounds();
    if (this.lastSize == null || !this.lastSize.equals(this.slider.getSize()))
      calculateGeometry(); 
    if (this.paintValue) {
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.slider, paramGraphics);
      int i = paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, paramGraphics.getFont(), fontMetrics, "" + this.slider.getValue());
      this.thumbRect.x += (this.thumbRect.width - i) / 2;
      if (this.slider.getOrientation() == 0) {
        if (this.valueRect.x + i > this.insetCache.left + this.contentRect.width)
          this.valueRect.x = this.insetCache.left + this.contentRect.width - i; 
        this.valueRect.x = Math.max(this.valueRect.x, 0);
      } 
      paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, "" + this.slider.getValue(), this.valueRect.x, this.valueRect.y, -1);
    } 
    if (this.slider.getPaintTrack() && rectangle.intersects(this.trackRect)) {
      SynthContext synthContext = getContext(this.slider, Region.SLIDER_TRACK);
      paintTrack(synthContext, paramGraphics, this.trackRect);
      synthContext.dispose();
    } 
    if (rectangle.intersects(this.thumbRect)) {
      SynthContext synthContext = getContext(this.slider, Region.SLIDER_THUMB);
      paintThumb(synthContext, paramGraphics, this.thumbRect);
      synthContext.dispose();
    } 
    if (this.slider.getPaintTicks() && rectangle.intersects(this.tickRect))
      paintTicks(paramGraphics); 
    if (this.slider.getPaintLabels() && rectangle.intersects(this.labelRect))
      paintLabels(paramGraphics); 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintSliderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.slider.getOrientation()); }
  
  protected void paintThumb(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) {
    int i = this.slider.getOrientation();
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintSliderThumbBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
    paramSynthContext.getPainter().paintSliderThumbBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
  }
  
  protected void paintTrack(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) {
    int i = this.slider.getOrientation();
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintSliderTrackBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
    paramSynthContext.getPainter().paintSliderTrackBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JSlider)paramPropertyChangeEvent.getSource()); 
  }
  
  private class SynthTrackListener extends BasicSliderUI.TrackListener {
    private SynthTrackListener() { super(SynthSliderUI.this); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { SynthSliderUI.this.setThumbActive(false); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      super.mousePressed(param1MouseEvent);
      SynthSliderUI.this.setThumbPressed(SynthSliderUI.this.thumbRect.contains(param1MouseEvent.getX(), param1MouseEvent.getY()));
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      super.mouseReleased(param1MouseEvent);
      SynthSliderUI.this.updateThumbState(param1MouseEvent.getX(), param1MouseEvent.getY(), false);
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      int i6;
      int i5;
      int i4;
      int i3;
      int i2;
      int i1;
      int n;
      int m;
      int k;
      int j;
      int i;
      if (!SynthSliderUI.this.slider.isEnabled())
        return; 
      this.currentMouseX = param1MouseEvent.getX();
      this.currentMouseY = param1MouseEvent.getY();
      if (!SynthSliderUI.this.isDragging())
        return; 
      SynthSliderUI.this.slider.setValueIsAdjusting(true);
      switch (SynthSliderUI.this.slider.getOrientation()) {
        case 1:
          j = this.this$0.thumbRect.height / 2;
          k = param1MouseEvent.getY() - this.offset;
          m = this.this$0.trackRect.y;
          n = this.this$0.trackRect.y + this.this$0.trackRect.height - j - SynthSliderUI.this.trackBorder;
          i1 = SynthSliderUI.this.yPositionForValue(SynthSliderUI.this.slider.getMaximum() - SynthSliderUI.this.slider.getExtent());
          if (SynthSliderUI.this.drawInverted()) {
            n = i1;
            m += j;
          } else {
            m = i1;
          } 
          k = Math.max(k, m - j);
          k = Math.min(k, n - j);
          SynthSliderUI.this.setThumbLocation(this.this$0.thumbRect.x, k);
          i = k + j;
          SynthSliderUI.this.slider.setValue(SynthSliderUI.this.valueForYPosition(i));
          break;
        case 0:
          i2 = this.this$0.thumbRect.width / 2;
          i3 = param1MouseEvent.getX() - this.offset;
          i4 = this.this$0.trackRect.x + i2 + SynthSliderUI.this.trackBorder;
          i5 = this.this$0.trackRect.x + this.this$0.trackRect.width - i2 - SynthSliderUI.this.trackBorder;
          i6 = SynthSliderUI.this.xPositionForValue(SynthSliderUI.this.slider.getMaximum() - SynthSliderUI.this.slider.getExtent());
          if (SynthSliderUI.this.drawInverted()) {
            i4 = i6;
          } else {
            i5 = i6;
          } 
          i3 = Math.max(i3, i4 - i2);
          i3 = Math.min(i3, i5 - i2);
          SynthSliderUI.this.setThumbLocation(i3, this.this$0.thumbRect.y);
          i = i3 + i2;
          SynthSliderUI.this.slider.setValue(SynthSliderUI.this.valueForXPosition(i));
          break;
        default:
          return;
      } 
      if (SynthSliderUI.this.slider.getValueIsAdjusting())
        SynthSliderUI.this.setThumbActive(true); 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { SynthSliderUI.this.updateThumbState(param1MouseEvent.getX(), param1MouseEvent.getY()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */