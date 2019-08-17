package java.awt.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.BidiUtils;
import sun.font.CoreMetrics;
import sun.font.Decoration;
import sun.font.ExtendedTextLabel;
import sun.font.FontResolver;
import sun.font.GraphicComponent;
import sun.font.LayoutPathImpl;
import sun.font.TextLabelFactory;
import sun.font.TextLineComponent;
import sun.text.CodePointIterator;

final class TextLine {
  private TextLineComponent[] fComponents;
  
  private float[] fBaselineOffsets;
  
  private int[] fComponentVisualOrder;
  
  private float[] locs;
  
  private char[] fChars;
  
  private int fCharsStart;
  
  private int fCharsLimit;
  
  private int[] fCharVisualOrder;
  
  private int[] fCharLogicalOrder;
  
  private byte[] fCharLevels;
  
  private boolean fIsDirectionLTR;
  
  private LayoutPathImpl lp;
  
  private boolean isSimple;
  
  private Rectangle pixelBounds;
  
  private FontRenderContext frc;
  
  private TextLineMetrics fMetrics = null;
  
  private static Function fgPosAdvF = new Function() {
      float computeFunction(TextLine param1TextLine, int param1Int1, int param1Int2) {
        TextLineComponent textLineComponent = param1TextLine.fComponents[param1Int1];
        int i = param1TextLine.getComponentVisualIndex(param1Int1);
        return param1TextLine.locs[i * 2] + textLineComponent.getCharX(param1Int2) + textLineComponent.getCharAdvance(param1Int2);
      }
    };
  
  private static Function fgAdvanceF = new Function() {
      float computeFunction(TextLine param1TextLine, int param1Int1, int param1Int2) {
        TextLineComponent textLineComponent = param1TextLine.fComponents[param1Int1];
        return textLineComponent.getCharAdvance(param1Int2);
      }
    };
  
  private static Function fgXPositionF = new Function() {
      float computeFunction(TextLine param1TextLine, int param1Int1, int param1Int2) {
        int i = param1TextLine.getComponentVisualIndex(param1Int1);
        TextLineComponent textLineComponent = param1TextLine.fComponents[param1Int1];
        return param1TextLine.locs[i * 2] + textLineComponent.getCharX(param1Int2);
      }
    };
  
  private static Function fgYPositionF = new Function() {
      float computeFunction(TextLine param1TextLine, int param1Int1, int param1Int2) {
        TextLineComponent textLineComponent = param1TextLine.fComponents[param1Int1];
        float f = textLineComponent.getCharY(param1Int2);
        return f + param1TextLine.getComponentShift(param1Int1);
      }
    };
  
  public TextLine(FontRenderContext paramFontRenderContext, TextLineComponent[] paramArrayOfTextLineComponent, float[] paramArrayOfFloat, char[] paramArrayOfChar, int paramInt1, int paramInt2, int[] paramArrayOfInt, byte[] paramArrayOfByte, boolean paramBoolean) {
    int[] arrayOfInt = computeComponentOrder(paramArrayOfTextLineComponent, paramArrayOfInt);
    this.frc = paramFontRenderContext;
    this.fComponents = paramArrayOfTextLineComponent;
    this.fBaselineOffsets = paramArrayOfFloat;
    this.fComponentVisualOrder = arrayOfInt;
    this.fChars = paramArrayOfChar;
    this.fCharsStart = paramInt1;
    this.fCharsLimit = paramInt2;
    this.fCharLogicalOrder = paramArrayOfInt;
    this.fCharLevels = paramArrayOfByte;
    this.fIsDirectionLTR = paramBoolean;
    checkCtorArgs();
    init();
  }
  
  private void checkCtorArgs() {
    int i = 0;
    for (byte b = 0; b < this.fComponents.length; b++)
      i += this.fComponents[b].getNumCharacters(); 
    if (i != characterCount())
      throw new IllegalArgumentException("Invalid TextLine!  char count is different from sum of char counts of components."); 
  }
  
  private void init() { // Byte code:
    //   0: fconst_0
    //   1: fstore_1
    //   2: fconst_0
    //   3: fstore_2
    //   4: fconst_0
    //   5: fstore_3
    //   6: fconst_0
    //   7: fstore #4
    //   9: fconst_0
    //   10: fstore #5
    //   12: fconst_0
    //   13: fstore #6
    //   15: iconst_0
    //   16: istore #8
    //   18: aload_0
    //   19: iconst_1
    //   20: putfield isSimple : Z
    //   23: iconst_0
    //   24: istore #9
    //   26: iload #9
    //   28: aload_0
    //   29: getfield fComponents : [Lsun/font/TextLineComponent;
    //   32: arraylength
    //   33: if_icmpge -> 189
    //   36: aload_0
    //   37: getfield fComponents : [Lsun/font/TextLineComponent;
    //   40: iload #9
    //   42: aaload
    //   43: astore #7
    //   45: aload_0
    //   46: dup
    //   47: getfield isSimple : Z
    //   50: aload #7
    //   52: invokeinterface isSimple : ()Z
    //   57: iand
    //   58: putfield isSimple : Z
    //   61: aload #7
    //   63: invokeinterface getCoreMetrics : ()Lsun/font/CoreMetrics;
    //   68: astore #10
    //   70: aload #10
    //   72: getfield baselineIndex : I
    //   75: i2b
    //   76: istore #11
    //   78: iload #11
    //   80: iflt -> 139
    //   83: aload_0
    //   84: getfield fBaselineOffsets : [F
    //   87: iload #11
    //   89: faload
    //   90: fstore #12
    //   92: fload_1
    //   93: fload #12
    //   95: fneg
    //   96: aload #10
    //   98: getfield ascent : F
    //   101: fadd
    //   102: invokestatic max : (FF)F
    //   105: fstore_1
    //   106: fload #12
    //   108: aload #10
    //   110: getfield descent : F
    //   113: fadd
    //   114: fstore #13
    //   116: fload_2
    //   117: fload #13
    //   119: invokestatic max : (FF)F
    //   122: fstore_2
    //   123: fload_3
    //   124: fload #13
    //   126: aload #10
    //   128: getfield leading : F
    //   131: fadd
    //   132: invokestatic max : (FF)F
    //   135: fstore_3
    //   136: goto -> 183
    //   139: iconst_1
    //   140: istore #8
    //   142: aload #10
    //   144: getfield ascent : F
    //   147: aload #10
    //   149: getfield descent : F
    //   152: fadd
    //   153: fstore #12
    //   155: fload #12
    //   157: aload #10
    //   159: getfield leading : F
    //   162: fadd
    //   163: fstore #13
    //   165: fload #5
    //   167: fload #12
    //   169: invokestatic max : (FF)F
    //   172: fstore #5
    //   174: fload #6
    //   176: fload #13
    //   178: invokestatic max : (FF)F
    //   181: fstore #6
    //   183: iinc #9, 1
    //   186: goto -> 26
    //   189: iload #8
    //   191: ifeq -> 222
    //   194: fload #5
    //   196: fload_1
    //   197: fload_2
    //   198: fadd
    //   199: fcmpl
    //   200: ifle -> 208
    //   203: fload #5
    //   205: fload_1
    //   206: fsub
    //   207: fstore_2
    //   208: fload #6
    //   210: fload_1
    //   211: fload_3
    //   212: fadd
    //   213: fcmpl
    //   214: ifle -> 222
    //   217: fload #6
    //   219: fload_1
    //   220: fsub
    //   221: fstore_3
    //   222: fload_3
    //   223: fload_2
    //   224: fsub
    //   225: fstore_3
    //   226: iload #8
    //   228: ifeq -> 274
    //   231: aload_0
    //   232: iconst_5
    //   233: newarray float
    //   235: dup
    //   236: iconst_0
    //   237: aload_0
    //   238: getfield fBaselineOffsets : [F
    //   241: iconst_0
    //   242: faload
    //   243: fastore
    //   244: dup
    //   245: iconst_1
    //   246: aload_0
    //   247: getfield fBaselineOffsets : [F
    //   250: iconst_1
    //   251: faload
    //   252: fastore
    //   253: dup
    //   254: iconst_2
    //   255: aload_0
    //   256: getfield fBaselineOffsets : [F
    //   259: iconst_2
    //   260: faload
    //   261: fastore
    //   262: dup
    //   263: iconst_3
    //   264: fload_2
    //   265: fastore
    //   266: dup
    //   267: iconst_4
    //   268: fload_1
    //   269: fneg
    //   270: fastore
    //   271: putfield fBaselineOffsets : [F
    //   274: fconst_0
    //   275: fstore #9
    //   277: fconst_0
    //   278: fstore #10
    //   280: aconst_null
    //   281: astore #11
    //   283: iconst_0
    //   284: istore #12
    //   286: aload_0
    //   287: aload_0
    //   288: getfield fComponents : [Lsun/font/TextLineComponent;
    //   291: arraylength
    //   292: iconst_2
    //   293: imul
    //   294: iconst_2
    //   295: iadd
    //   296: newarray float
    //   298: putfield locs : [F
    //   301: iconst_0
    //   302: istore #13
    //   304: iconst_0
    //   305: istore #14
    //   307: iload #13
    //   309: aload_0
    //   310: getfield fComponents : [Lsun/font/TextLineComponent;
    //   313: arraylength
    //   314: if_icmpge -> 650
    //   317: aload_0
    //   318: getfield fComponents : [Lsun/font/TextLineComponent;
    //   321: aload_0
    //   322: iload #13
    //   324: invokespecial getComponentLogicalIndex : (I)I
    //   327: aaload
    //   328: astore #7
    //   330: aload #7
    //   332: invokeinterface getCoreMetrics : ()Lsun/font/CoreMetrics;
    //   337: astore #15
    //   339: aload #11
    //   341: ifnull -> 574
    //   344: aload #11
    //   346: getfield italicAngle : F
    //   349: fconst_0
    //   350: fcmpl
    //   351: ifne -> 364
    //   354: aload #15
    //   356: getfield italicAngle : F
    //   359: fconst_0
    //   360: fcmpl
    //   361: ifeq -> 574
    //   364: aload #11
    //   366: getfield italicAngle : F
    //   369: aload #15
    //   371: getfield italicAngle : F
    //   374: fcmpl
    //   375: ifne -> 405
    //   378: aload #11
    //   380: getfield baselineIndex : I
    //   383: aload #15
    //   385: getfield baselineIndex : I
    //   388: if_icmpne -> 405
    //   391: aload #11
    //   393: getfield ssOffset : F
    //   396: aload #15
    //   398: getfield ssOffset : F
    //   401: fcmpl
    //   402: ifeq -> 574
    //   405: aload #11
    //   407: aload_0
    //   408: getfield fBaselineOffsets : [F
    //   411: invokevirtual effectiveBaselineOffset : ([F)F
    //   414: fstore #16
    //   416: fload #16
    //   418: aload #11
    //   420: getfield ascent : F
    //   423: fsub
    //   424: fstore #17
    //   426: fload #16
    //   428: aload #11
    //   430: getfield descent : F
    //   433: fadd
    //   434: fstore #18
    //   436: aload #15
    //   438: aload_0
    //   439: getfield fBaselineOffsets : [F
    //   442: invokevirtual effectiveBaselineOffset : ([F)F
    //   445: fstore #19
    //   447: fload #19
    //   449: aload #15
    //   451: getfield ascent : F
    //   454: fsub
    //   455: fstore #20
    //   457: fload #19
    //   459: aload #15
    //   461: getfield descent : F
    //   464: fadd
    //   465: fstore #21
    //   467: fload #17
    //   469: fload #20
    //   471: invokestatic max : (FF)F
    //   474: fstore #22
    //   476: fload #18
    //   478: fload #21
    //   480: invokestatic min : (FF)F
    //   483: fstore #23
    //   485: aload #11
    //   487: getfield italicAngle : F
    //   490: fload #16
    //   492: fload #22
    //   494: fsub
    //   495: fmul
    //   496: fstore #24
    //   498: aload #11
    //   500: getfield italicAngle : F
    //   503: fload #16
    //   505: fload #23
    //   507: fsub
    //   508: fmul
    //   509: fstore #25
    //   511: aload #15
    //   513: getfield italicAngle : F
    //   516: fload #19
    //   518: fload #22
    //   520: fsub
    //   521: fmul
    //   522: fstore #26
    //   524: aload #15
    //   526: getfield italicAngle : F
    //   529: fload #19
    //   531: fload #23
    //   533: fsub
    //   534: fmul
    //   535: fstore #27
    //   537: fload #24
    //   539: fload #26
    //   541: fsub
    //   542: fstore #28
    //   544: fload #25
    //   546: fload #27
    //   548: fsub
    //   549: fstore #29
    //   551: fload #28
    //   553: fload #29
    //   555: invokestatic max : (FF)F
    //   558: fstore #30
    //   560: fload #9
    //   562: fload #30
    //   564: fadd
    //   565: fstore #9
    //   567: fload #19
    //   569: fstore #10
    //   571: goto -> 585
    //   574: aload #15
    //   576: aload_0
    //   577: getfield fBaselineOffsets : [F
    //   580: invokevirtual effectiveBaselineOffset : ([F)F
    //   583: fstore #10
    //   585: aload_0
    //   586: getfield locs : [F
    //   589: iload #14
    //   591: fload #9
    //   593: fastore
    //   594: aload_0
    //   595: getfield locs : [F
    //   598: iload #14
    //   600: iconst_1
    //   601: iadd
    //   602: fload #10
    //   604: fastore
    //   605: fload #9
    //   607: aload #7
    //   609: invokeinterface getAdvance : ()F
    //   614: fadd
    //   615: fstore #9
    //   617: aload #15
    //   619: astore #11
    //   621: iload #12
    //   623: aload #7
    //   625: invokeinterface getBaselineTransform : ()Ljava/awt/geom/AffineTransform;
    //   630: ifnull -> 637
    //   633: iconst_1
    //   634: goto -> 638
    //   637: iconst_0
    //   638: ior
    //   639: istore #12
    //   641: iinc #13, 1
    //   644: iinc #14, 2
    //   647: goto -> 307
    //   650: aload #11
    //   652: getfield italicAngle : F
    //   655: fconst_0
    //   656: fcmpl
    //   657: ifeq -> 751
    //   660: aload #11
    //   662: aload_0
    //   663: getfield fBaselineOffsets : [F
    //   666: invokevirtual effectiveBaselineOffset : ([F)F
    //   669: fstore #13
    //   671: fload #13
    //   673: aload #11
    //   675: getfield ascent : F
    //   678: fsub
    //   679: fstore #14
    //   681: fload #13
    //   683: aload #11
    //   685: getfield descent : F
    //   688: fadd
    //   689: fstore #15
    //   691: fload #13
    //   693: aload #11
    //   695: getfield ssOffset : F
    //   698: fadd
    //   699: fstore #13
    //   701: aload #11
    //   703: getfield italicAngle : F
    //   706: fconst_0
    //   707: fcmpl
    //   708: ifle -> 724
    //   711: fload #13
    //   713: aload #11
    //   715: getfield ascent : F
    //   718: fadd
    //   719: fstore #16
    //   721: goto -> 734
    //   724: fload #13
    //   726: aload #11
    //   728: getfield descent : F
    //   731: fsub
    //   732: fstore #16
    //   734: fload #16
    //   736: aload #11
    //   738: getfield italicAngle : F
    //   741: fmul
    //   742: fstore #16
    //   744: fload #9
    //   746: fload #16
    //   748: fadd
    //   749: fstore #9
    //   751: aload_0
    //   752: getfield locs : [F
    //   755: aload_0
    //   756: getfield locs : [F
    //   759: arraylength
    //   760: iconst_2
    //   761: isub
    //   762: fload #9
    //   764: fastore
    //   765: fload #9
    //   767: fstore #4
    //   769: aload_0
    //   770: new java/awt/font/TextLine$TextLineMetrics
    //   773: dup
    //   774: fload_1
    //   775: fload_2
    //   776: fload_3
    //   777: fload #4
    //   779: invokespecial <init> : (FFFF)V
    //   782: putfield fMetrics : Ljava/awt/font/TextLine$TextLineMetrics;
    //   785: iload #12
    //   787: ifeq -> 1055
    //   790: aload_0
    //   791: iconst_0
    //   792: putfield isSimple : Z
    //   795: new java/awt/geom/Point2D$Double
    //   798: dup
    //   799: invokespecial <init> : ()V
    //   802: astore #13
    //   804: dconst_0
    //   805: dstore #14
    //   807: dconst_0
    //   808: dstore #16
    //   810: new sun/font/LayoutPathImpl$SegmentPathBuilder
    //   813: dup
    //   814: invokespecial <init> : ()V
    //   817: astore #18
    //   819: aload #18
    //   821: aload_0
    //   822: getfield locs : [F
    //   825: iconst_0
    //   826: faload
    //   827: f2d
    //   828: dconst_0
    //   829: invokevirtual moveTo : (DD)V
    //   832: iconst_0
    //   833: istore #19
    //   835: iconst_0
    //   836: istore #20
    //   838: iload #19
    //   840: aload_0
    //   841: getfield fComponents : [Lsun/font/TextLineComponent;
    //   844: arraylength
    //   845: if_icmpge -> 1000
    //   848: aload_0
    //   849: getfield fComponents : [Lsun/font/TextLineComponent;
    //   852: aload_0
    //   853: iload #19
    //   855: invokespecial getComponentLogicalIndex : (I)I
    //   858: aaload
    //   859: astore #7
    //   861: aload #7
    //   863: invokeinterface getBaselineTransform : ()Ljava/awt/geom/AffineTransform;
    //   868: astore #21
    //   870: aload #21
    //   872: ifnull -> 920
    //   875: aload #21
    //   877: invokevirtual getType : ()I
    //   880: iconst_1
    //   881: iand
    //   882: ifeq -> 920
    //   885: aload #21
    //   887: invokevirtual getTranslateX : ()D
    //   890: dstore #22
    //   892: aload #21
    //   894: invokevirtual getTranslateY : ()D
    //   897: dstore #24
    //   899: aload #18
    //   901: dload #14
    //   903: dload #22
    //   905: dadd
    //   906: dup2
    //   907: dstore #14
    //   909: dload #16
    //   911: dload #24
    //   913: dadd
    //   914: dup2
    //   915: dstore #16
    //   917: invokevirtual moveTo : (DD)V
    //   920: aload #13
    //   922: aload_0
    //   923: getfield locs : [F
    //   926: iload #20
    //   928: iconst_2
    //   929: iadd
    //   930: faload
    //   931: aload_0
    //   932: getfield locs : [F
    //   935: iload #20
    //   937: faload
    //   938: fsub
    //   939: f2d
    //   940: putfield x : D
    //   943: aload #13
    //   945: dconst_0
    //   946: putfield y : D
    //   949: aload #21
    //   951: ifnull -> 964
    //   954: aload #21
    //   956: aload #13
    //   958: aload #13
    //   960: invokevirtual deltaTransform : (Ljava/awt/geom/Point2D;Ljava/awt/geom/Point2D;)Ljava/awt/geom/Point2D;
    //   963: pop
    //   964: aload #18
    //   966: dload #14
    //   968: aload #13
    //   970: getfield x : D
    //   973: dadd
    //   974: dup2
    //   975: dstore #14
    //   977: dload #16
    //   979: aload #13
    //   981: getfield y : D
    //   984: dadd
    //   985: dup2
    //   986: dstore #16
    //   988: invokevirtual lineTo : (DD)V
    //   991: iinc #19, 1
    //   994: iinc #20, 2
    //   997: goto -> 838
    //   1000: aload_0
    //   1001: aload #18
    //   1003: invokevirtual complete : ()Lsun/font/LayoutPathImpl$SegmentPath;
    //   1006: putfield lp : Lsun/font/LayoutPathImpl;
    //   1009: aload_0
    //   1010: getfield lp : Lsun/font/LayoutPathImpl;
    //   1013: ifnonnull -> 1055
    //   1016: aload_0
    //   1017: getfield fComponents : [Lsun/font/TextLineComponent;
    //   1020: aload_0
    //   1021: iconst_0
    //   1022: invokespecial getComponentLogicalIndex : (I)I
    //   1025: aaload
    //   1026: astore #7
    //   1028: aload #7
    //   1030: invokeinterface getBaselineTransform : ()Ljava/awt/geom/AffineTransform;
    //   1035: astore #19
    //   1037: aload #19
    //   1039: ifnull -> 1055
    //   1042: aload_0
    //   1043: new sun/font/LayoutPathImpl$EmptyPath
    //   1046: dup
    //   1047: aload #19
    //   1049: invokespecial <init> : (Ljava/awt/geom/AffineTransform;)V
    //   1052: putfield lp : Lsun/font/LayoutPathImpl;
    //   1055: return }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) {
    Rectangle rectangle = null;
    if (paramFontRenderContext != null && paramFontRenderContext.equals(this.frc))
      paramFontRenderContext = null; 
    int i = (int)Math.floor(paramFloat1);
    int j = (int)Math.floor(paramFloat2);
    float f1 = paramFloat1 - i;
    float f2 = paramFloat2 - j;
    boolean bool = (paramFontRenderContext == null && f1 == 0.0F && f2 == 0.0F) ? 1 : 0;
    if (bool && this.pixelBounds != null) {
      rectangle = new Rectangle(this.pixelBounds);
      rectangle.x += i;
      rectangle.y += j;
      return rectangle;
    } 
    if (this.isSimple) {
      byte b = 0;
      for (boolean bool1 = false; b < this.fComponents.length; bool1 += true) {
        TextLineComponent textLineComponent = this.fComponents[getComponentLogicalIndex(b)];
        Rectangle rectangle1 = textLineComponent.getPixelBounds(paramFontRenderContext, this.locs[bool1] + f1, this.locs[bool1 + true] + f2);
        if (!rectangle1.isEmpty())
          if (rectangle == null) {
            rectangle = rectangle1;
          } else {
            rectangle.add(rectangle1);
          }  
        b++;
      } 
      if (rectangle == null)
        rectangle = new Rectangle(0, 0, 0, 0); 
    } else {
      Rectangle2D rectangle2D = getVisualBounds();
      if (this.lp != null)
        rectangle2D = this.lp.mapShape(rectangle2D).getBounds(); 
      Rectangle rectangle1 = rectangle2D.getBounds();
      BufferedImage bufferedImage = new BufferedImage(rectangle1.width + 6, rectangle1.height + 6, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      graphics2D.setColor(Color.WHITE);
      graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      graphics2D.setColor(Color.BLACK);
      draw(graphics2D, f1 + 3.0F - rectangle1.x, f2 + 3.0F - rectangle1.y);
      rectangle = computePixelBounds(bufferedImage);
      rectangle.x -= 3 - rectangle1.x;
      rectangle.y -= 3 - rectangle1.y;
    } 
    if (bool)
      this.pixelBounds = new Rectangle(rectangle); 
    rectangle.x += i;
    rectangle.y += j;
    return rectangle;
  }
  
  static Rectangle computePixelBounds(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    int k = -1;
    int m = -1;
    int n = i;
    int i1 = j;
    int[] arrayOfInt = new int[i];
    label47: while (++m < j) {
      paramBufferedImage.getRGB(0, m, arrayOfInt.length, 1, arrayOfInt, 0, i);
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (arrayOfInt[b] != -1)
          break label47; 
      } 
    } 
    arrayOfInt = new int[i];
    label48: while (--i1 > m) {
      paramBufferedImage.getRGB(0, i1, arrayOfInt.length, 1, arrayOfInt, 0, i);
      for (byte b = 0; b < arrayOfInt.length; b++) {
        if (arrayOfInt[b] != -1)
          break label48; 
      } 
    } 
    i1++;
    label49: while (++k < n) {
      for (byte b = m; b < i1; b++) {
        int i2 = paramBufferedImage.getRGB(k, b);
        if (i2 != -1)
          break label49; 
      } 
    } 
    label50: while (--n > k) {
      for (byte b = m; b < i1; b++) {
        int i2 = paramBufferedImage.getRGB(n, b);
        if (i2 != -1)
          break label50; 
      } 
    } 
    return new Rectangle(k, m, ++n - k, i1 - m);
  }
  
  public int characterCount() { return this.fCharsLimit - this.fCharsStart; }
  
  public boolean isDirectionLTR() { return this.fIsDirectionLTR; }
  
  public TextLineMetrics getMetrics() { return this.fMetrics; }
  
  public int visualToLogical(int paramInt) {
    if (this.fCharLogicalOrder == null)
      return paramInt; 
    if (this.fCharVisualOrder == null)
      this.fCharVisualOrder = BidiUtils.createInverseMap(this.fCharLogicalOrder); 
    return this.fCharVisualOrder[paramInt];
  }
  
  public int logicalToVisual(int paramInt) { return (this.fCharLogicalOrder == null) ? paramInt : this.fCharLogicalOrder[paramInt]; }
  
  public byte getCharLevel(int paramInt) { return (this.fCharLevels == null) ? 0 : this.fCharLevels[paramInt]; }
  
  public boolean isCharLTR(int paramInt) { return ((getCharLevel(paramInt) & true) == 0); }
  
  public int getCharType(int paramInt) { return Character.getType(this.fChars[paramInt + this.fCharsStart]); }
  
  public boolean isCharSpace(int paramInt) { return Character.isSpaceChar(this.fChars[paramInt + this.fCharsStart]); }
  
  public boolean isCharWhitespace(int paramInt) { return Character.isWhitespace(this.fChars[paramInt + this.fCharsStart]); }
  
  public float getCharAngle(int paramInt) { return (getCoreMetricsAt(paramInt)).italicAngle; }
  
  public CoreMetrics getCoreMetricsAt(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative logicalIndex."); 
    if (paramInt > this.fCharsLimit - this.fCharsStart)
      throw new IllegalArgumentException("logicalIndex too large."); 
    byte b = 0;
    int i = 0;
    int j = 0;
    do {
      j += this.fComponents[b].getNumCharacters();
      if (j > paramInt)
        break; 
      b++;
      i = j;
    } while (b < this.fComponents.length);
    return this.fComponents[b].getCoreMetrics();
  }
  
  public float getCharAscent(int paramInt) { return (getCoreMetricsAt(paramInt)).ascent; }
  
  public float getCharDescent(int paramInt) { return (getCoreMetricsAt(paramInt)).descent; }
  
  public float getCharShift(int paramInt) { return (getCoreMetricsAt(paramInt)).ssOffset; }
  
  private float applyFunctionAtIndex(int paramInt, Function paramFunction) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative logicalIndex."); 
    int i = 0;
    for (byte b = 0; b < this.fComponents.length; b++) {
      int j = i + this.fComponents[b].getNumCharacters();
      if (j > paramInt)
        return paramFunction.computeFunction(this, b, paramInt - i); 
      i = j;
    } 
    throw new IllegalArgumentException("logicalIndex too large.");
  }
  
  public float getCharAdvance(int paramInt) { return applyFunctionAtIndex(paramInt, fgAdvanceF); }
  
  public float getCharXPosition(int paramInt) { return applyFunctionAtIndex(paramInt, fgXPositionF); }
  
  public float getCharYPosition(int paramInt) { return applyFunctionAtIndex(paramInt, fgYPositionF); }
  
  public float getCharLinePosition(int paramInt) { return getCharXPosition(paramInt); }
  
  public float getCharLinePosition(int paramInt, boolean paramBoolean) {
    Function function = (isCharLTR(paramInt) == paramBoolean) ? fgXPositionF : fgPosAdvF;
    return applyFunctionAtIndex(paramInt, function);
  }
  
  public boolean caretAtOffsetIsValid(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative offset."); 
    int i = 0;
    for (byte b = 0; b < this.fComponents.length; b++) {
      int j = i + this.fComponents[b].getNumCharacters();
      if (j > paramInt)
        return this.fComponents[b].caretAtOffsetIsValid(paramInt - i); 
      i = j;
    } 
    throw new IllegalArgumentException("logicalIndex too large.");
  }
  
  private int getComponentLogicalIndex(int paramInt) { return (this.fComponentVisualOrder == null) ? paramInt : this.fComponentVisualOrder[paramInt]; }
  
  private int getComponentVisualIndex(int paramInt) {
    if (this.fComponentVisualOrder == null)
      return paramInt; 
    for (byte b = 0; b < this.fComponentVisualOrder.length; b++) {
      if (this.fComponentVisualOrder[b] == paramInt)
        return b; 
    } 
    throw new IndexOutOfBoundsException("bad component index: " + paramInt);
  }
  
  public Rectangle2D getCharBounds(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative logicalIndex."); 
    int i = 0;
    for (byte b = 0; b < this.fComponents.length; b++) {
      int j = i + this.fComponents[b].getNumCharacters();
      if (j > paramInt) {
        TextLineComponent textLineComponent = this.fComponents[b];
        int k = paramInt - i;
        Rectangle2D rectangle2D = textLineComponent.getCharVisualBounds(k);
        int m = getComponentVisualIndex(b);
        rectangle2D.setRect(rectangle2D.getX() + this.locs[m * 2], rectangle2D.getY() + this.locs[m * 2 + 1], rectangle2D.getWidth(), rectangle2D.getHeight());
        return rectangle2D;
      } 
      i = j;
    } 
    throw new IllegalArgumentException("logicalIndex too large.");
  }
  
  private float getComponentShift(int paramInt) {
    CoreMetrics coreMetrics = this.fComponents[paramInt].getCoreMetrics();
    return coreMetrics.effectiveBaselineOffset(this.fBaselineOffsets);
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) {
    if (this.lp == null) {
      byte b = 0;
      for (boolean bool = false; b < this.fComponents.length; bool += true) {
        TextLineComponent textLineComponent = this.fComponents[getComponentLogicalIndex(b)];
        textLineComponent.draw(paramGraphics2D, this.locs[bool] + paramFloat1, this.locs[bool + true] + paramFloat2);
        b++;
      } 
    } else {
      AffineTransform affineTransform = paramGraphics2D.getTransform();
      Point2D.Float float = new Point2D.Float();
      byte b = 0;
      for (boolean bool = false; b < this.fComponents.length; bool += true) {
        TextLineComponent textLineComponent = this.fComponents[getComponentLogicalIndex(b)];
        this.lp.pathToPoint(this.locs[bool], this.locs[bool + true], false, float);
        float.x += paramFloat1;
        float.y += paramFloat2;
        AffineTransform affineTransform1 = textLineComponent.getBaselineTransform();
        if (affineTransform1 != null) {
          paramGraphics2D.translate(float.x - affineTransform1.getTranslateX(), float.y - affineTransform1.getTranslateY());
          paramGraphics2D.transform(affineTransform1);
          textLineComponent.draw(paramGraphics2D, 0.0F, 0.0F);
          paramGraphics2D.setTransform(affineTransform);
        } else {
          textLineComponent.draw(paramGraphics2D, float.x, float.y);
        } 
        b++;
      } 
    } 
  }
  
  public Rectangle2D getVisualBounds() {
    Rectangle2D rectangle2D = null;
    byte b = 0;
    for (boolean bool = false; b < this.fComponents.length; bool += true) {
      TextLineComponent textLineComponent = this.fComponents[getComponentLogicalIndex(b)];
      Rectangle2D rectangle2D1 = textLineComponent.getVisualBounds();
      Point2D.Float float = new Point2D.Float(this.locs[bool], this.locs[bool + true]);
      if (this.lp == null) {
        rectangle2D1.setRect(rectangle2D1.getMinX() + float.x, rectangle2D1.getMinY() + float.y, rectangle2D1.getWidth(), rectangle2D1.getHeight());
      } else {
        this.lp.pathToPoint(float, false, float);
        AffineTransform affineTransform = textLineComponent.getBaselineTransform();
        if (affineTransform != null) {
          AffineTransform affineTransform1 = AffineTransform.getTranslateInstance(float.x - affineTransform.getTranslateX(), float.y - affineTransform.getTranslateY());
          affineTransform1.concatenate(affineTransform);
          rectangle2D1 = affineTransform1.createTransformedShape(rectangle2D1).getBounds2D();
        } else {
          rectangle2D1.setRect(rectangle2D1.getMinX() + float.x, rectangle2D1.getMinY() + float.y, rectangle2D1.getWidth(), rectangle2D1.getHeight());
        } 
      } 
      if (rectangle2D == null) {
        rectangle2D = rectangle2D1;
      } else {
        rectangle2D.add(rectangle2D1);
      } 
      b++;
    } 
    if (rectangle2D == null)
      rectangle2D = new Rectangle2D.Float(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE); 
    return rectangle2D;
  }
  
  public Rectangle2D getItalicBounds() {
    float f1 = Float.MAX_VALUE;
    float f2 = -3.4028235E38F;
    float f3 = Float.MAX_VALUE;
    float f4 = -3.4028235E38F;
    byte b = 0;
    for (boolean bool = false; b < this.fComponents.length; bool += true) {
      TextLineComponent textLineComponent = this.fComponents[getComponentLogicalIndex(b)];
      Rectangle2D rectangle2D = textLineComponent.getItalicBounds();
      float f5 = this.locs[bool];
      float f6 = this.locs[bool + true];
      f1 = Math.min(f1, f5 + (float)rectangle2D.getX());
      f2 = Math.max(f2, f5 + (float)rectangle2D.getMaxX());
      f3 = Math.min(f3, f6 + (float)rectangle2D.getY());
      f4 = Math.max(f4, f6 + (float)rectangle2D.getMaxY());
      b++;
    } 
    return new Rectangle2D.Float(f1, f3, f2 - f1, f4 - f3);
  }
  
  public Shape getOutline(AffineTransform paramAffineTransform) {
    GeneralPath generalPath = new GeneralPath(1);
    byte b = 0;
    for (boolean bool = false; b < this.fComponents.length; bool += true) {
      TextLineComponent textLineComponent = this.fComponents[getComponentLogicalIndex(b)];
      generalPath.append(textLineComponent.getOutline(this.locs[bool], this.locs[bool + true]), false);
      b++;
    } 
    if (paramAffineTransform != null)
      generalPath.transform(paramAffineTransform); 
    return generalPath;
  }
  
  public int hashCode() { return this.fComponents.length << 16 ^ this.fComponents[0].hashCode() << 3 ^ this.fCharsLimit - this.fCharsStart; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < this.fComponents.length; b++)
      stringBuilder.append(this.fComponents[b]); 
    return stringBuilder.toString();
  }
  
  public static TextLine fastCreateTextLine(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, Font paramFont, CoreMetrics paramCoreMetrics, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    boolean bool1 = true;
    byte[] arrayOfByte1 = null;
    int[] arrayOfInt = null;
    Bidi bidi = null;
    int i = paramArrayOfChar.length;
    boolean bool2 = false;
    byte[] arrayOfByte2 = null;
    AttributeValues attributeValues = null;
    if (paramMap != null) {
      attributeValues = AttributeValues.fromMap(paramMap);
      if (attributeValues.getRunDirection() >= 0) {
        bool1 = (attributeValues.getRunDirection() == 0);
        bool2 = !bool1;
      } 
      if (attributeValues.getBidiEmbedding() != 0) {
        bool2 = true;
        byte b1 = (byte)attributeValues.getBidiEmbedding();
        arrayOfByte2 = new byte[i];
        for (byte b2 = 0; b2 < arrayOfByte2.length; b2++)
          arrayOfByte2[b2] = b1; 
      } 
    } 
    if (!bool2)
      bool2 = Bidi.requiresBidi(paramArrayOfChar, 0, paramArrayOfChar.length); 
    if (bool2) {
      byte b1 = (attributeValues == null) ? -2 : attributeValues.getRunDirection();
      bidi = new Bidi(paramArrayOfChar, 0, arrayOfByte2, 0, paramArrayOfChar.length, b1);
      if (!bidi.isLeftToRight()) {
        arrayOfByte1 = BidiUtils.getLevels(bidi);
        int[] arrayOfInt1 = BidiUtils.createVisualToLogicalMap(arrayOfByte1);
        arrayOfInt = BidiUtils.createInverseMap(arrayOfInt1);
        bool1 = bidi.baseIsLeftToRight();
      } 
    } 
    Decoration decoration = Decoration.getDecoration(attributeValues);
    byte b = 0;
    TextLabelFactory textLabelFactory = new TextLabelFactory(paramFontRenderContext, paramArrayOfChar, bidi, b);
    TextLineComponent[] arrayOfTextLineComponent = new TextLineComponent[1];
    arrayOfTextLineComponent = createComponentsOnRun(0, paramArrayOfChar.length, paramArrayOfChar, arrayOfInt, arrayOfByte1, textLabelFactory, paramFont, paramCoreMetrics, paramFontRenderContext, decoration, arrayOfTextLineComponent, 0);
    int j;
    for (j = arrayOfTextLineComponent.length; arrayOfTextLineComponent[j - true] == null; j--);
    if (j != arrayOfTextLineComponent.length) {
      TextLineComponent[] arrayOfTextLineComponent1 = new TextLineComponent[j];
      System.arraycopy(arrayOfTextLineComponent, 0, arrayOfTextLineComponent1, 0, j);
      arrayOfTextLineComponent = arrayOfTextLineComponent1;
    } 
    return new TextLine(paramFontRenderContext, arrayOfTextLineComponent, paramCoreMetrics.baselineOffsets, paramArrayOfChar, 0, paramArrayOfChar.length, arrayOfInt, arrayOfByte1, bool1);
  }
  
  private static TextLineComponent[] expandArray(TextLineComponent[] paramArrayOfTextLineComponent) {
    TextLineComponent[] arrayOfTextLineComponent = new TextLineComponent[paramArrayOfTextLineComponent.length + 8];
    System.arraycopy(paramArrayOfTextLineComponent, 0, arrayOfTextLineComponent, 0, paramArrayOfTextLineComponent.length);
    return arrayOfTextLineComponent;
  }
  
  public static TextLineComponent[] createComponentsOnRun(int paramInt1, int paramInt2, char[] paramArrayOfChar, int[] paramArrayOfInt, byte[] paramArrayOfByte, TextLabelFactory paramTextLabelFactory, Font paramFont, CoreMetrics paramCoreMetrics, FontRenderContext paramFontRenderContext, Decoration paramDecoration, TextLineComponent[] paramArrayOfTextLineComponent, int paramInt3) {
    int i = paramInt1;
    do {
      int j = firstVisualChunk(paramArrayOfInt, paramArrayOfByte, i, paramInt2);
      do {
        int m;
        int k = i;
        if (paramCoreMetrics == null) {
          LineMetrics lineMetrics = paramFont.getLineMetrics(paramArrayOfChar, k, j, paramFontRenderContext);
          paramCoreMetrics = CoreMetrics.get(lineMetrics);
          m = lineMetrics.getNumChars();
        } else {
          m = j - k;
        } 
        ExtendedTextLabel extendedTextLabel = paramTextLabelFactory.createExtended(paramFont, paramCoreMetrics, paramDecoration, k, k + m);
        if (++paramInt3 >= paramArrayOfTextLineComponent.length)
          paramArrayOfTextLineComponent = expandArray(paramArrayOfTextLineComponent); 
        paramArrayOfTextLineComponent[paramInt3 - 1] = extendedTextLabel;
        i += m;
      } while (i < j);
    } while (i < paramInt2);
    return paramArrayOfTextLineComponent;
  }
  
  public static TextLineComponent[] getComponents(StyledParagraph paramStyledParagraph, char[] paramArrayOfChar, int paramInt1, int paramInt2, int[] paramArrayOfInt, byte[] paramArrayOfByte, TextLabelFactory paramTextLabelFactory) {
    TextLineComponent[] arrayOfTextLineComponent2;
    FontRenderContext fontRenderContext = paramTextLabelFactory.getFontRenderContext();
    int i = 0;
    TextLineComponent[] arrayOfTextLineComponent1 = new TextLineComponent[1];
    int j = paramInt1;
    do {
      int k = Math.min(paramStyledParagraph.getRunLimit(j), paramInt2);
      Decoration decoration = paramStyledParagraph.getDecorationAt(j);
      Object object = paramStyledParagraph.getFontOrGraphicAt(j);
      if (object instanceof GraphicAttribute) {
        AffineTransform affineTransform = null;
        GraphicAttribute graphicAttribute = (GraphicAttribute)object;
        do {
          int m = firstVisualChunk(paramArrayOfInt, paramArrayOfByte, j, k);
          GraphicComponent graphicComponent = new GraphicComponent(graphicAttribute, decoration, paramArrayOfInt, paramArrayOfByte, j, m, affineTransform);
          j = m;
          if (++i >= arrayOfTextLineComponent1.length)
            arrayOfTextLineComponent1 = expandArray(arrayOfTextLineComponent1); 
          arrayOfTextLineComponent1[i - 1] = graphicComponent;
        } while (j < k);
      } else {
        Font font = (Font)object;
        arrayOfTextLineComponent1 = createComponentsOnRun(j, k, paramArrayOfChar, paramArrayOfInt, paramArrayOfByte, paramTextLabelFactory, font, null, fontRenderContext, decoration, arrayOfTextLineComponent1, i);
        j = k;
        for (i = arrayOfTextLineComponent1.length; arrayOfTextLineComponent1[i - true] == null; i--);
      } 
    } while (j < paramInt2);
    if (arrayOfTextLineComponent1.length == i) {
      arrayOfTextLineComponent2 = arrayOfTextLineComponent1;
    } else {
      arrayOfTextLineComponent2 = new TextLineComponent[i];
      System.arraycopy(arrayOfTextLineComponent1, 0, arrayOfTextLineComponent2, 0, i);
    } 
    return arrayOfTextLineComponent2;
  }
  
  public static TextLine createLineFromText(char[] paramArrayOfChar, StyledParagraph paramStyledParagraph, TextLabelFactory paramTextLabelFactory, boolean paramBoolean, float[] paramArrayOfFloat) {
    paramTextLabelFactory.setLineContext(0, paramArrayOfChar.length);
    Bidi bidi = paramTextLabelFactory.getLineBidi();
    int[] arrayOfInt = null;
    byte[] arrayOfByte = null;
    if (bidi != null) {
      arrayOfByte = BidiUtils.getLevels(bidi);
      int[] arrayOfInt1 = BidiUtils.createVisualToLogicalMap(arrayOfByte);
      arrayOfInt = BidiUtils.createInverseMap(arrayOfInt1);
    } 
    TextLineComponent[] arrayOfTextLineComponent = getComponents(paramStyledParagraph, paramArrayOfChar, 0, paramArrayOfChar.length, arrayOfInt, arrayOfByte, paramTextLabelFactory);
    return new TextLine(paramTextLabelFactory.getFontRenderContext(), arrayOfTextLineComponent, paramArrayOfFloat, paramArrayOfChar, 0, paramArrayOfChar.length, arrayOfInt, arrayOfByte, paramBoolean);
  }
  
  private static int[] computeComponentOrder(TextLineComponent[] paramArrayOfTextLineComponent, int[] paramArrayOfInt) {
    int[] arrayOfInt = null;
    if (paramArrayOfInt != null && paramArrayOfTextLineComponent.length > 1) {
      arrayOfInt = new int[paramArrayOfTextLineComponent.length];
      int i = 0;
      for (byte b = 0; b < paramArrayOfTextLineComponent.length; b++) {
        arrayOfInt[b] = paramArrayOfInt[i];
        i += paramArrayOfTextLineComponent[b].getNumCharacters();
      } 
      arrayOfInt = BidiUtils.createContiguousOrder(arrayOfInt);
      arrayOfInt = BidiUtils.createInverseMap(arrayOfInt);
    } 
    return arrayOfInt;
  }
  
  public static TextLine standardCreateTextLine(FontRenderContext paramFontRenderContext, AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, float[] paramArrayOfFloat) {
    StyledParagraph styledParagraph = new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar);
    Bidi bidi = new Bidi(paramAttributedCharacterIterator);
    if (bidi.isLeftToRight())
      bidi = null; 
    byte b = 0;
    TextLabelFactory textLabelFactory = new TextLabelFactory(paramFontRenderContext, paramArrayOfChar, bidi, b);
    boolean bool = true;
    if (bidi != null)
      bool = bidi.baseIsLeftToRight(); 
    return createLineFromText(paramArrayOfChar, styledParagraph, textLabelFactory, bool, paramArrayOfFloat);
  }
  
  static boolean advanceToFirstFont(AttributedCharacterIterator paramAttributedCharacterIterator) {
    for (char c = paramAttributedCharacterIterator.first(); c != Character.MAX_VALUE; c = paramAttributedCharacterIterator.setIndex(paramAttributedCharacterIterator.getRunLimit())) {
      if (paramAttributedCharacterIterator.getAttribute(TextAttribute.CHAR_REPLACEMENT) == null)
        return true; 
    } 
    return false;
  }
  
  static float[] getNormalizedOffsets(float[] paramArrayOfFloat, byte paramByte) {
    if (paramArrayOfFloat[paramByte] != 0.0F) {
      float f = paramArrayOfFloat[paramByte];
      float[] arrayOfFloat = new float[paramArrayOfFloat.length];
      for (byte b = 0; b < arrayOfFloat.length; b++)
        arrayOfFloat[b] = paramArrayOfFloat[b] - f; 
      paramArrayOfFloat = arrayOfFloat;
    } 
    return paramArrayOfFloat;
  }
  
  static Font getFontAtCurrentPos(AttributedCharacterIterator paramAttributedCharacterIterator) {
    Object object = paramAttributedCharacterIterator.getAttribute(TextAttribute.FONT);
    if (object != null)
      return (Font)object; 
    if (paramAttributedCharacterIterator.getAttribute(TextAttribute.FAMILY) != null)
      return Font.getFont(paramAttributedCharacterIterator.getAttributes()); 
    int i = CodePointIterator.create(paramAttributedCharacterIterator).next();
    if (i != -1) {
      FontResolver fontResolver = FontResolver.getInstance();
      return fontResolver.getFont(fontResolver.getFontIndex(i), paramAttributedCharacterIterator.getAttributes());
    } 
    return null;
  }
  
  private static int firstVisualChunk(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfInt != null && paramArrayOfByte != null) {
      byte b = paramArrayOfByte[paramInt1];
      while (++paramInt1 < paramInt2 && paramArrayOfByte[paramInt1] == b);
      return paramInt1;
    } 
    return paramInt2;
  }
  
  public TextLine getJustifiedLine(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2) {
    TextLineComponent[] arrayOfTextLineComponent = new TextLineComponent[this.fComponents.length];
    System.arraycopy(this.fComponents, 0, arrayOfTextLineComponent, 0, this.fComponents.length);
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    boolean bool = false;
    do {
      f2 = getAdvanceBetween(arrayOfTextLineComponent, 0, characterCount());
      float f = getAdvanceBetween(arrayOfTextLineComponent, paramInt1, paramInt2);
      f3 = (paramFloat1 - f) * paramFloat2;
      int[] arrayOfInt = new int[arrayOfTextLineComponent.length];
      int i = 0;
      for (byte b1 = 0; b1 < arrayOfTextLineComponent.length; b1++) {
        int m = getComponentLogicalIndex(b1);
        arrayOfInt[m] = i;
        i += arrayOfTextLineComponent[m].getNumJustificationInfos();
      } 
      GlyphJustificationInfo[] arrayOfGlyphJustificationInfo = new GlyphJustificationInfo[i];
      int j = 0;
      byte b2;
      for (b2 = 0; b2 < arrayOfTextLineComponent.length; b2++) {
        TextLineComponent textLineComponent = arrayOfTextLineComponent[b2];
        int m = textLineComponent.getNumCharacters();
        int n = j + m;
        if (n > paramInt1) {
          int i1 = Math.max(0, paramInt1 - j);
          int i2 = Math.min(m, paramInt2 - j);
          textLineComponent.getJustificationInfos(arrayOfGlyphJustificationInfo, arrayOfInt[b2], i1, i2);
          if (n >= paramInt2)
            break; 
        } 
      } 
      b2 = 0;
      int k = i;
      while (b2 < k && arrayOfGlyphJustificationInfo[b2] == null)
        b2++; 
      while (k > b2 && arrayOfGlyphJustificationInfo[k - true] == null)
        k--; 
      TextJustifier textJustifier = new TextJustifier(arrayOfGlyphJustificationInfo, b2, k);
      float[] arrayOfFloat = textJustifier.justify(f3);
      boolean bool1 = !bool ? 1 : 0;
      boolean bool2 = false;
      boolean[] arrayOfBoolean = new boolean[1];
      j = 0;
      for (byte b3 = 0; b3 < arrayOfTextLineComponent.length; b3++) {
        TextLineComponent textLineComponent = arrayOfTextLineComponent[b3];
        int m = textLineComponent.getNumCharacters();
        int n = j + m;
        if (n > paramInt1) {
          int i1 = Math.max(0, paramInt1 - j);
          int i2 = Math.min(m, paramInt2 - j);
          arrayOfTextLineComponent[b3] = textLineComponent.applyJustificationDeltas(arrayOfFloat, arrayOfInt[b3] * 2, arrayOfBoolean);
          bool2 |= arrayOfBoolean[0];
          if (n >= paramInt2)
            break; 
        } 
      } 
      bool = (bool2 && !bool) ? 1 : 0;
    } while (bool);
    return new TextLine(this.frc, arrayOfTextLineComponent, this.fBaselineOffsets, this.fChars, this.fCharsStart, this.fCharsLimit, this.fCharLogicalOrder, this.fCharLevels, this.fIsDirectionLTR);
  }
  
  public static float getAdvanceBetween(TextLineComponent[] paramArrayOfTextLineComponent, int paramInt1, int paramInt2) {
    float f = 0.0F;
    int i = 0;
    for (byte b = 0; b < paramArrayOfTextLineComponent.length; b++) {
      TextLineComponent textLineComponent = paramArrayOfTextLineComponent[b];
      int j = textLineComponent.getNumCharacters();
      int k = i + j;
      if (k > paramInt1) {
        int m = Math.max(0, paramInt1 - i);
        int n = Math.min(j, paramInt2 - i);
        f += textLineComponent.getAdvanceBetween(m, n);
        if (k >= paramInt2)
          break; 
      } 
      i = k;
    } 
    return f;
  }
  
  LayoutPathImpl getLayoutPath() { return this.lp; }
  
  private static abstract class Function {
    private Function() {}
    
    abstract float computeFunction(TextLine param1TextLine, int param1Int1, int param1Int2);
  }
  
  static final class TextLineMetrics {
    public final float ascent;
    
    public final float descent;
    
    public final float leading;
    
    public final float advance;
    
    public TextLineMetrics(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      this.ascent = param1Float1;
      this.descent = param1Float2;
      this.leading = param1Float3;
      this.advance = param1Float4;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TextLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */