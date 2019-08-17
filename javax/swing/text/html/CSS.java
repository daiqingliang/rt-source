package javax.swing.text.html;

import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.View;

public class CSS implements Serializable {
  private static final Hashtable<String, Attribute> attributeMap = new Hashtable();
  
  private static final Hashtable<String, Value> valueMap = new Hashtable();
  
  private static final Hashtable<HTML.Attribute, Attribute[]> htmlAttrToCssAttrMap = new Hashtable(20);
  
  private static final Hashtable<Object, Attribute> styleConstantToCssMap = new Hashtable(17);
  
  private static final Hashtable<String, Value> htmlValueToCssValueMap = new Hashtable(8);
  
  private static final Hashtable<String, Value> cssValueToInternalValueMap = new Hashtable(13);
  
  private Hashtable<Object, Object> valueConvertor = new Hashtable();
  
  private int baseFontSize = baseFontSizeIndex + 1;
  
  private StyleSheet styleSheet = null;
  
  static int baseFontSizeIndex;
  
  public CSS() {
    this.valueConvertor.put(Attribute.FONT_SIZE, new FontSize());
    this.valueConvertor.put(Attribute.FONT_FAMILY, new FontFamily());
    this.valueConvertor.put(Attribute.FONT_WEIGHT, new FontWeight());
    BorderStyle borderStyle = new BorderStyle();
    this.valueConvertor.put(Attribute.BORDER_TOP_STYLE, borderStyle);
    this.valueConvertor.put(Attribute.BORDER_RIGHT_STYLE, borderStyle);
    this.valueConvertor.put(Attribute.BORDER_BOTTOM_STYLE, borderStyle);
    this.valueConvertor.put(Attribute.BORDER_LEFT_STYLE, borderStyle);
    ColorValue colorValue = new ColorValue();
    this.valueConvertor.put(Attribute.COLOR, colorValue);
    this.valueConvertor.put(Attribute.BACKGROUND_COLOR, colorValue);
    this.valueConvertor.put(Attribute.BORDER_TOP_COLOR, colorValue);
    this.valueConvertor.put(Attribute.BORDER_RIGHT_COLOR, colorValue);
    this.valueConvertor.put(Attribute.BORDER_BOTTOM_COLOR, colorValue);
    this.valueConvertor.put(Attribute.BORDER_LEFT_COLOR, colorValue);
    LengthValue lengthValue1 = new LengthValue();
    this.valueConvertor.put(Attribute.MARGIN_TOP, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_BOTTOM, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_LEFT, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_LEFT_LTR, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_LEFT_RTL, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_RIGHT, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_RIGHT_LTR, lengthValue1);
    this.valueConvertor.put(Attribute.MARGIN_RIGHT_RTL, lengthValue1);
    this.valueConvertor.put(Attribute.PADDING_TOP, lengthValue1);
    this.valueConvertor.put(Attribute.PADDING_BOTTOM, lengthValue1);
    this.valueConvertor.put(Attribute.PADDING_LEFT, lengthValue1);
    this.valueConvertor.put(Attribute.PADDING_RIGHT, lengthValue1);
    BorderWidthValue borderWidthValue = new BorderWidthValue(null, 0);
    this.valueConvertor.put(Attribute.BORDER_TOP_WIDTH, borderWidthValue);
    this.valueConvertor.put(Attribute.BORDER_BOTTOM_WIDTH, borderWidthValue);
    this.valueConvertor.put(Attribute.BORDER_LEFT_WIDTH, borderWidthValue);
    this.valueConvertor.put(Attribute.BORDER_RIGHT_WIDTH, borderWidthValue);
    LengthValue lengthValue2 = new LengthValue(true);
    this.valueConvertor.put(Attribute.TEXT_INDENT, lengthValue2);
    this.valueConvertor.put(Attribute.WIDTH, lengthValue1);
    this.valueConvertor.put(Attribute.HEIGHT, lengthValue1);
    this.valueConvertor.put(Attribute.BORDER_SPACING, lengthValue1);
    StringValue stringValue = new StringValue();
    this.valueConvertor.put(Attribute.FONT_STYLE, stringValue);
    this.valueConvertor.put(Attribute.TEXT_DECORATION, stringValue);
    this.valueConvertor.put(Attribute.TEXT_ALIGN, stringValue);
    this.valueConvertor.put(Attribute.VERTICAL_ALIGN, stringValue);
    CssValueMapper cssValueMapper = new CssValueMapper();
    this.valueConvertor.put(Attribute.LIST_STYLE_TYPE, cssValueMapper);
    this.valueConvertor.put(Attribute.BACKGROUND_IMAGE, new BackgroundImage());
    this.valueConvertor.put(Attribute.BACKGROUND_POSITION, new BackgroundPosition());
    this.valueConvertor.put(Attribute.BACKGROUND_REPEAT, cssValueMapper);
    this.valueConvertor.put(Attribute.BACKGROUND_ATTACHMENT, cssValueMapper);
    CssValue cssValue = new CssValue();
    int i = Attribute.allAttributes.length;
    for (byte b = 0; b < i; b++) {
      Attribute attribute = Attribute.allAttributes[b];
      if (this.valueConvertor.get(attribute) == null)
        this.valueConvertor.put(attribute, cssValue); 
    } 
  }
  
  void setBaseFontSize(int paramInt) {
    if (paramInt < 1) {
      this.baseFontSize = 0;
    } else if (paramInt > 7) {
      this.baseFontSize = 7;
    } else {
      this.baseFontSize = paramInt;
    } 
  }
  
  void setBaseFontSize(String paramString) {
    if (paramString != null)
      if (paramString.startsWith("+")) {
        int i = Integer.valueOf(paramString.substring(1)).intValue();
        setBaseFontSize(this.baseFontSize + i);
      } else if (paramString.startsWith("-")) {
        int i = -Integer.valueOf(paramString.substring(1)).intValue();
        setBaseFontSize(this.baseFontSize + i);
      } else {
        setBaseFontSize(Integer.valueOf(paramString).intValue());
      }  
  }
  
  int getBaseFontSize() { return this.baseFontSize; }
  
  void addInternalCSSValue(MutableAttributeSet paramMutableAttributeSet, Attribute paramAttribute, String paramString) {
    if (paramAttribute == Attribute.FONT) {
      ShorthandFontParser.parseShorthandFont(this, paramString, paramMutableAttributeSet);
    } else if (paramAttribute == Attribute.BACKGROUND) {
      ShorthandBackgroundParser.parseShorthandBackground(this, paramString, paramMutableAttributeSet);
    } else if (paramAttribute == Attribute.MARGIN) {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, ALL_MARGINS);
    } else if (paramAttribute == Attribute.PADDING) {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, ALL_PADDING);
    } else if (paramAttribute == Attribute.BORDER_WIDTH) {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, ALL_BORDER_WIDTHS);
    } else if (paramAttribute == Attribute.BORDER_COLOR) {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, ALL_BORDER_COLORS);
    } else if (paramAttribute == Attribute.BORDER_STYLE) {
      ShorthandMarginParser.parseShorthandMargin(this, paramString, paramMutableAttributeSet, ALL_BORDER_STYLES);
    } else if (paramAttribute == Attribute.BORDER || paramAttribute == Attribute.BORDER_TOP || paramAttribute == Attribute.BORDER_RIGHT || paramAttribute == Attribute.BORDER_BOTTOM || paramAttribute == Attribute.BORDER_LEFT) {
      ShorthandBorderParser.parseShorthandBorder(paramMutableAttributeSet, paramAttribute, paramString);
    } else {
      Object object = getInternalCSSValue(paramAttribute, paramString);
      if (object != null)
        paramMutableAttributeSet.addAttribute(paramAttribute, object); 
    } 
  }
  
  Object getInternalCSSValue(Attribute paramAttribute, String paramString) {
    CssValue cssValue = (CssValue)this.valueConvertor.get(paramAttribute);
    Object object = cssValue.parseCssValue(paramString);
    return (object != null) ? object : cssValue.parseCssValue(paramAttribute.getDefaultValue());
  }
  
  Attribute styleConstantsKeyToCSSKey(StyleConstants paramStyleConstants) { return (Attribute)styleConstantToCssMap.get(paramStyleConstants); }
  
  Object styleConstantsValueToCSSValue(StyleConstants paramStyleConstants, Object paramObject) {
    Attribute attribute = styleConstantsKeyToCSSKey(paramStyleConstants);
    if (attribute != null) {
      CssValue cssValue = (CssValue)this.valueConvertor.get(attribute);
      return cssValue.fromStyleConstants(paramStyleConstants, paramObject);
    } 
    return null;
  }
  
  Object cssValueToStyleConstantsValue(StyleConstants paramStyleConstants, Object paramObject) { return (paramObject instanceof CssValue) ? ((CssValue)paramObject).toStyleConstants(paramStyleConstants, null) : null; }
  
  Font getFont(StyleContext paramStyleContext, AttributeSet paramAttributeSet, int paramInt, StyleSheet paramStyleSheet) {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    int i = getFontSize(paramAttributeSet, paramInt, paramStyleSheet);
    StringValue stringValue = (StringValue)paramAttributeSet.getAttribute(Attribute.VERTICAL_ALIGN);
    if (stringValue != null) {
      String str1 = stringValue.toString();
      if (str1.indexOf("sup") >= 0 || str1.indexOf("sub") >= 0)
        i -= 2; 
    } 
    FontFamily fontFamily = (FontFamily)paramAttributeSet.getAttribute(Attribute.FONT_FAMILY);
    String str = (fontFamily != null) ? fontFamily.getValue() : "SansSerif";
    byte b = 0;
    FontWeight fontWeight = (FontWeight)paramAttributeSet.getAttribute(Attribute.FONT_WEIGHT);
    if (fontWeight != null && fontWeight.getValue() > 400)
      b |= true; 
    Object object = paramAttributeSet.getAttribute(Attribute.FONT_STYLE);
    if (object != null && object.toString().indexOf("italic") >= 0)
      b |= 0x2; 
    if (str.equalsIgnoreCase("monospace"))
      str = "Monospaced"; 
    Font font = paramStyleContext.getFont(str, b, i);
    if (font == null || (font.getFamily().equals("Dialog") && !str.equalsIgnoreCase("Dialog"))) {
      str = "SansSerif";
      font = paramStyleContext.getFont(str, b, i);
    } 
    return font;
  }
  
  static int getFontSize(AttributeSet paramAttributeSet, int paramInt, StyleSheet paramStyleSheet) {
    FontSize fontSize = (FontSize)paramAttributeSet.getAttribute(Attribute.FONT_SIZE);
    return (fontSize != null) ? fontSize.getValue(paramAttributeSet, paramStyleSheet) : paramInt;
  }
  
  Color getColor(AttributeSet paramAttributeSet, Attribute paramAttribute) {
    ColorValue colorValue = (ColorValue)paramAttributeSet.getAttribute(paramAttribute);
    return (colorValue != null) ? colorValue.getValue() : null;
  }
  
  float getPointSize(String paramString, StyleSheet paramStyleSheet) {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    if (paramString != null) {
      if (paramString.startsWith("+")) {
        int j = Integer.valueOf(paramString.substring(1)).intValue();
        return getPointSize(this.baseFontSize + j, paramStyleSheet);
      } 
      if (paramString.startsWith("-")) {
        int j = -Integer.valueOf(paramString.substring(1)).intValue();
        return getPointSize(this.baseFontSize + j, paramStyleSheet);
      } 
      int i = Integer.valueOf(paramString).intValue();
      return getPointSize(i, paramStyleSheet);
    } 
    return 0.0F;
  }
  
  float getLength(AttributeSet paramAttributeSet, Attribute paramAttribute, StyleSheet paramStyleSheet) {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    LengthValue lengthValue = (LengthValue)paramAttributeSet.getAttribute(paramAttribute);
    boolean bool = (paramStyleSheet == null) ? false : paramStyleSheet.isW3CLengthUnits();
    return (lengthValue != null) ? lengthValue.getValue(bool) : 0.0F;
  }
  
  AttributeSet translateHTMLToCSS(AttributeSet paramAttributeSet) {
    SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
    Element element = (Element)paramAttributeSet;
    HTML.Tag tag = getHTMLTag(paramAttributeSet);
    if (tag == HTML.Tag.TD || tag == HTML.Tag.TH) {
      AttributeSet attributeSet = element.getParentElement().getParentElement().getAttributes();
      int i = getTableBorder(attributeSet);
      if (i > 0)
        translateAttribute(HTML.Attribute.BORDER, "1", simpleAttributeSet); 
      String str = (String)attributeSet.getAttribute(HTML.Attribute.CELLPADDING);
      if (str != null) {
        LengthValue lengthValue = (LengthValue)getInternalCSSValue(Attribute.PADDING_TOP, str);
        lengthValue.span = (lengthValue.span < 0.0F) ? 0.0F : lengthValue.span;
        simpleAttributeSet.addAttribute(Attribute.PADDING_TOP, lengthValue);
        simpleAttributeSet.addAttribute(Attribute.PADDING_BOTTOM, lengthValue);
        simpleAttributeSet.addAttribute(Attribute.PADDING_LEFT, lengthValue);
        simpleAttributeSet.addAttribute(Attribute.PADDING_RIGHT, lengthValue);
      } 
    } 
    if (element.isLeaf()) {
      translateEmbeddedAttributes(paramAttributeSet, simpleAttributeSet);
    } else {
      translateAttributes(tag, paramAttributeSet, simpleAttributeSet);
    } 
    if (tag == HTML.Tag.CAPTION) {
      Object object = paramAttributeSet.getAttribute(HTML.Attribute.ALIGN);
      if (object != null && (object.equals("top") || object.equals("bottom"))) {
        simpleAttributeSet.addAttribute(Attribute.CAPTION_SIDE, object);
        simpleAttributeSet.removeAttribute(Attribute.TEXT_ALIGN);
      } else {
        object = paramAttributeSet.getAttribute(HTML.Attribute.VALIGN);
        if (object != null)
          simpleAttributeSet.addAttribute(Attribute.CAPTION_SIDE, object); 
      } 
    } 
    return simpleAttributeSet;
  }
  
  private static int getTableBorder(AttributeSet paramAttributeSet) {
    String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.BORDER);
    if (str == "#DEFAULT" || "".equals(str))
      return 1; 
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException numberFormatException) {
      return 0;
    } 
  }
  
  public static Attribute[] getAllAttributeKeys() {
    Attribute[] arrayOfAttribute = new Attribute[Attribute.allAttributes.length];
    System.arraycopy(Attribute.allAttributes, 0, arrayOfAttribute, 0, Attribute.allAttributes.length);
    return arrayOfAttribute;
  }
  
  public static final Attribute getAttribute(String paramString) { return (Attribute)attributeMap.get(paramString); }
  
  static final Value getValue(String paramString) { return (Value)valueMap.get(paramString); }
  
  static URL getURL(URL paramURL, String paramString) {
    if (paramString == null)
      return null; 
    if (paramString.startsWith("url(") && paramString.endsWith(")"))
      paramString = paramString.substring(4, paramString.length() - 1); 
    try {
      URL uRL = new URL(paramString);
      if (uRL != null)
        return uRL; 
    } catch (MalformedURLException malformedURLException) {}
    if (paramURL != null)
      try {
        return new URL(paramURL, paramString);
      } catch (MalformedURLException malformedURLException) {} 
    return null;
  }
  
  static String colorToHex(Color paramColor) {
    String str1 = "#";
    String str2 = Integer.toHexString(paramColor.getRed());
    if (str2.length() > 2) {
      str2 = str2.substring(0, 2);
    } else if (str2.length() < 2) {
      str1 = str1 + "0" + str2;
    } else {
      str1 = str1 + str2;
    } 
    str2 = Integer.toHexString(paramColor.getGreen());
    if (str2.length() > 2) {
      str2 = str2.substring(0, 2);
    } else if (str2.length() < 2) {
      str1 = str1 + "0" + str2;
    } else {
      str1 = str1 + str2;
    } 
    str2 = Integer.toHexString(paramColor.getBlue());
    if (str2.length() > 2) {
      str2 = str2.substring(0, 2);
    } else if (str2.length() < 2) {
      str1 = str1 + "0" + str2;
    } else {
      str1 = str1 + str2;
    } 
    return str1;
  }
  
  static final Color hexToColor(String paramString) {
    Color color;
    String str1;
    int i = paramString.length();
    if (paramString.startsWith("#")) {
      str1 = paramString.substring(1, Math.min(paramString.length(), 7));
    } else {
      str1 = paramString;
    } 
    String str2 = "0x" + str1;
    try {
      color = Color.decode(str2);
    } catch (NumberFormatException numberFormatException) {
      color = null;
    } 
    return color;
  }
  
  static Color stringToColor(String paramString) {
    Color color;
    if (paramString == null)
      return null; 
    if (paramString.length() == 0) {
      color = Color.black;
    } else if (paramString.startsWith("rgb(")) {
      color = parseRGB(paramString);
    } else if (paramString.charAt(0) == '#') {
      color = hexToColor(paramString);
    } else if (paramString.equalsIgnoreCase("Black")) {
      color = hexToColor("#000000");
    } else if (paramString.equalsIgnoreCase("Silver")) {
      color = hexToColor("#C0C0C0");
    } else if (paramString.equalsIgnoreCase("Gray")) {
      color = hexToColor("#808080");
    } else if (paramString.equalsIgnoreCase("White")) {
      color = hexToColor("#FFFFFF");
    } else if (paramString.equalsIgnoreCase("Maroon")) {
      color = hexToColor("#800000");
    } else if (paramString.equalsIgnoreCase("Red")) {
      color = hexToColor("#FF0000");
    } else if (paramString.equalsIgnoreCase("Purple")) {
      color = hexToColor("#800080");
    } else if (paramString.equalsIgnoreCase("Fuchsia")) {
      color = hexToColor("#FF00FF");
    } else if (paramString.equalsIgnoreCase("Green")) {
      color = hexToColor("#008000");
    } else if (paramString.equalsIgnoreCase("Lime")) {
      color = hexToColor("#00FF00");
    } else if (paramString.equalsIgnoreCase("Olive")) {
      color = hexToColor("#808000");
    } else if (paramString.equalsIgnoreCase("Yellow")) {
      color = hexToColor("#FFFF00");
    } else if (paramString.equalsIgnoreCase("Navy")) {
      color = hexToColor("#000080");
    } else if (paramString.equalsIgnoreCase("Blue")) {
      color = hexToColor("#0000FF");
    } else if (paramString.equalsIgnoreCase("Teal")) {
      color = hexToColor("#008080");
    } else if (paramString.equalsIgnoreCase("Aqua")) {
      color = hexToColor("#00FFFF");
    } else if (paramString.equalsIgnoreCase("Orange")) {
      color = hexToColor("#FF8000");
    } else {
      color = hexToColor(paramString);
    } 
    return color;
  }
  
  private static Color parseRGB(String paramString) {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 4;
    int i = getColorComponent(paramString, arrayOfInt);
    int j = getColorComponent(paramString, arrayOfInt);
    int k = getColorComponent(paramString, arrayOfInt);
    return new Color(i, j, k);
  }
  
  private static int getColorComponent(String paramString, int[] paramArrayOfInt) {
    int i = paramString.length();
    char c;
    while (paramArrayOfInt[0] < i && (c = paramString.charAt(paramArrayOfInt[0])) != '-' && !Character.isDigit(c) && c != '.')
      paramArrayOfInt[0] = paramArrayOfInt[0] + 1; 
    int j = paramArrayOfInt[0];
    if (j < i && paramString.charAt(paramArrayOfInt[0]) == '-')
      paramArrayOfInt[0] = paramArrayOfInt[0] + 1; 
    while (paramArrayOfInt[0] < i && Character.isDigit(paramString.charAt(paramArrayOfInt[0])))
      paramArrayOfInt[0] = paramArrayOfInt[0] + 1; 
    if (paramArrayOfInt[0] < i && paramString.charAt(paramArrayOfInt[0]) == '.') {
      paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
      while (paramArrayOfInt[0] < i && Character.isDigit(paramString.charAt(paramArrayOfInt[0])))
        paramArrayOfInt[0] = paramArrayOfInt[0] + 1; 
    } 
    if (j != paramArrayOfInt[0])
      try {
        float f = Float.parseFloat(paramString.substring(j, paramArrayOfInt[0]));
        if (paramArrayOfInt[0] < i && paramString.charAt(paramArrayOfInt[0]) == '%') {
          paramArrayOfInt[0] = paramArrayOfInt[0] + 1;
          f = f * 255.0F / 100.0F;
        } 
        return Math.min(255, Math.max(0, (int)f));
      } catch (NumberFormatException numberFormatException) {} 
    return 0;
  }
  
  static int getIndexOfSize(float paramFloat, int[] paramArrayOfInt) {
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      if (paramFloat <= paramArrayOfInt[b])
        return b + true; 
    } 
    return paramArrayOfInt.length;
  }
  
  static int getIndexOfSize(float paramFloat, StyleSheet paramStyleSheet) {
    int[] arrayOfInt = (paramStyleSheet != null) ? paramStyleSheet.getSizeMap() : StyleSheet.sizeMapDefault;
    return getIndexOfSize(paramFloat, arrayOfInt);
  }
  
  static String[] parseStrings(String paramString) {
    boolean bool = (paramString == null) ? 0 : paramString.length();
    Vector vector = new Vector(4);
    for (byte b = 0; b < bool; b++) {
      while (b < bool && Character.isWhitespace(paramString.charAt(b)))
        b++; 
      byte b1 = b;
      while (b < bool && !Character.isWhitespace(paramString.charAt(b)))
        b++; 
      if (b1 != b)
        vector.addElement(paramString.substring(b1, b)); 
    } 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  float getPointSize(int paramInt, StyleSheet paramStyleSheet) {
    paramStyleSheet = getStyleSheet(paramStyleSheet);
    int[] arrayOfInt = (paramStyleSheet != null) ? paramStyleSheet.getSizeMap() : StyleSheet.sizeMapDefault;
    return (--paramInt < 0) ? arrayOfInt[0] : ((paramInt > arrayOfInt.length - 1) ? arrayOfInt[arrayOfInt.length - 1] : arrayOfInt[paramInt]);
  }
  
  private void translateEmbeddedAttributes(AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet) {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    if (paramAttributeSet.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.HR)
      translateAttributes(HTML.Tag.HR, paramAttributeSet, paramMutableAttributeSet); 
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof HTML.Tag) {
        HTML.Tag tag = (HTML.Tag)object;
        Object object1 = paramAttributeSet.getAttribute(tag);
        if (object1 != null && object1 instanceof AttributeSet)
          translateAttributes(tag, (AttributeSet)object1, paramMutableAttributeSet); 
        continue;
      } 
      if (object instanceof Attribute)
        paramMutableAttributeSet.addAttribute(object, paramAttributeSet.getAttribute(object)); 
    } 
  }
  
  private void translateAttributes(HTML.Tag paramTag, AttributeSet paramAttributeSet, MutableAttributeSet paramMutableAttributeSet) {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof HTML.Attribute) {
        HTML.Attribute attribute = (HTML.Attribute)object;
        if (attribute == HTML.Attribute.ALIGN) {
          String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.ALIGN);
          if (str != null) {
            Attribute attribute1 = getCssAlignAttribute(paramTag, paramAttributeSet);
            if (attribute1 != null) {
              Object object1 = getCssValue(attribute1, str);
              if (object1 != null)
                paramMutableAttributeSet.addAttribute(attribute1, object1); 
            } 
          } 
          continue;
        } 
        if (attribute == HTML.Attribute.SIZE && !isHTMLFontTag(paramTag))
          continue; 
        if (paramTag == HTML.Tag.TABLE && attribute == HTML.Attribute.BORDER) {
          int i = getTableBorder(paramAttributeSet);
          if (i > 0)
            translateAttribute(HTML.Attribute.BORDER, Integer.toString(i), paramMutableAttributeSet); 
          continue;
        } 
        translateAttribute(attribute, (String)paramAttributeSet.getAttribute(attribute), paramMutableAttributeSet);
        continue;
      } 
      if (object instanceof Attribute)
        paramMutableAttributeSet.addAttribute(object, paramAttributeSet.getAttribute(object)); 
    } 
  }
  
  private void translateAttribute(HTML.Attribute paramAttribute, String paramString, MutableAttributeSet paramMutableAttributeSet) {
    Attribute[] arrayOfAttribute = getCssAttribute(paramAttribute);
    if (arrayOfAttribute == null || paramString == null)
      return; 
    for (Attribute attribute : arrayOfAttribute) {
      Object object = getCssValue(attribute, paramString);
      if (object != null)
        paramMutableAttributeSet.addAttribute(attribute, object); 
    } 
  }
  
  Object getCssValue(Attribute paramAttribute, String paramString) {
    CssValue cssValue = (CssValue)this.valueConvertor.get(paramAttribute);
    return cssValue.parseHtmlValue(paramString);
  }
  
  private Attribute[] getCssAttribute(HTML.Attribute paramAttribute) { return (Attribute[])htmlAttrToCssAttrMap.get(paramAttribute); }
  
  private Attribute getCssAlignAttribute(HTML.Tag paramTag, AttributeSet paramAttributeSet) { return Attribute.TEXT_ALIGN; }
  
  private HTML.Tag getHTMLTag(AttributeSet paramAttributeSet) {
    Object object = paramAttributeSet.getAttribute(StyleConstants.NameAttribute);
    return (object instanceof HTML.Tag) ? (HTML.Tag)object : null;
  }
  
  private boolean isHTMLFontTag(HTML.Tag paramTag) { return (paramTag != null && (paramTag == HTML.Tag.FONT || paramTag == HTML.Tag.BASEFONT)); }
  
  private boolean isFloater(String paramString) { return (paramString.equals("left") || paramString.equals("right")); }
  
  private boolean validTextAlignValue(String paramString) { return (isFloater(paramString) || paramString.equals("center")); }
  
  static SizeRequirements calculateTiledRequirements(LayoutIterator paramLayoutIterator, SizeRequirements paramSizeRequirements) {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    int i = 0;
    int j = 0;
    int k = paramLayoutIterator.getCount();
    for (byte b = 0; b < k; b++) {
      paramLayoutIterator.setIndex(b);
      byte b1 = i;
      int m = (int)paramLayoutIterator.getLeadingCollapseSpan();
      j += Math.max(b1, m);
      l3 += (int)paramLayoutIterator.getPreferredSpan(0.0F);
      l1 = (long)((float)l1 + paramLayoutIterator.getMinimumSpan(0.0F));
      l2 = (long)((float)l2 + paramLayoutIterator.getMaximumSpan(0.0F));
      i = (int)paramLayoutIterator.getTrailingCollapseSpan();
    } 
    j += i;
    j = (int)(j + 2.0F * paramLayoutIterator.getBorderWidth());
    l1 += j;
    l3 += j;
    l2 += j;
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    paramSizeRequirements.minimum = (l1 > 2147483647L) ? Integer.MAX_VALUE : (int)l1;
    paramSizeRequirements.preferred = (l3 > 2147483647L) ? Integer.MAX_VALUE : (int)l3;
    paramSizeRequirements.maximum = (l2 > 2147483647L) ? Integer.MAX_VALUE : (int)l2;
    return paramSizeRequirements;
  }
  
  static void calculateTiledLayout(LayoutIterator paramLayoutIterator, int paramInt) { // Byte code:
    //   0: lconst_0
    //   1: lstore_2
    //   2: iconst_0
    //   3: istore #6
    //   5: iconst_0
    //   6: istore #7
    //   8: aload_0
    //   9: invokeinterface getCount : ()I
    //   14: istore #8
    //   16: iconst_3
    //   17: istore #9
    //   19: iload #9
    //   21: newarray long
    //   23: astore #10
    //   25: iload #9
    //   27: newarray long
    //   29: astore #11
    //   31: iconst_0
    //   32: istore #12
    //   34: iload #12
    //   36: iload #9
    //   38: if_icmpge -> 59
    //   41: aload #10
    //   43: iload #12
    //   45: aload #11
    //   47: iload #12
    //   49: lconst_0
    //   50: dup2_x2
    //   51: lastore
    //   52: lastore
    //   53: iinc #12, 1
    //   56: goto -> 34
    //   59: iconst_0
    //   60: istore #12
    //   62: iload #12
    //   64: iload #8
    //   66: if_icmpge -> 202
    //   69: aload_0
    //   70: iload #12
    //   72: invokeinterface setIndex : (I)V
    //   77: iload #6
    //   79: istore #13
    //   81: aload_0
    //   82: invokeinterface getLeadingCollapseSpan : ()F
    //   87: f2i
    //   88: istore #14
    //   90: aload_0
    //   91: iload #13
    //   93: iload #14
    //   95: invokestatic max : (II)I
    //   98: invokeinterface setOffset : (I)V
    //   103: iload #7
    //   105: aload_0
    //   106: invokeinterface getOffset : ()I
    //   111: iadd
    //   112: istore #7
    //   114: aload_0
    //   115: iload_1
    //   116: i2f
    //   117: invokeinterface getPreferredSpan : (F)F
    //   122: f2l
    //   123: lstore #4
    //   125: aload_0
    //   126: lload #4
    //   128: l2i
    //   129: invokeinterface setSpan : (I)V
    //   134: lload_2
    //   135: lload #4
    //   137: ladd
    //   138: lstore_2
    //   139: aload #10
    //   141: aload_0
    //   142: invokeinterface getAdjustmentWeight : ()I
    //   147: dup2
    //   148: laload
    //   149: aload_0
    //   150: iload_1
    //   151: i2f
    //   152: invokeinterface getMaximumSpan : (F)F
    //   157: f2l
    //   158: lload #4
    //   160: lsub
    //   161: ladd
    //   162: lastore
    //   163: aload #11
    //   165: aload_0
    //   166: invokeinterface getAdjustmentWeight : ()I
    //   171: dup2
    //   172: laload
    //   173: lload #4
    //   175: aload_0
    //   176: iload_1
    //   177: i2f
    //   178: invokeinterface getMinimumSpan : (F)F
    //   183: f2l
    //   184: lsub
    //   185: ladd
    //   186: lastore
    //   187: aload_0
    //   188: invokeinterface getTrailingCollapseSpan : ()F
    //   193: f2i
    //   194: istore #6
    //   196: iinc #12, 1
    //   199: goto -> 62
    //   202: iload #7
    //   204: iload #6
    //   206: iadd
    //   207: istore #7
    //   209: iload #7
    //   211: i2f
    //   212: fconst_2
    //   213: aload_0
    //   214: invokeinterface getBorderWidth : ()F
    //   219: fmul
    //   220: fadd
    //   221: f2i
    //   222: istore #7
    //   224: iconst_1
    //   225: istore #12
    //   227: iload #12
    //   229: iload #9
    //   231: if_icmpge -> 270
    //   234: aload #10
    //   236: iload #12
    //   238: dup2
    //   239: laload
    //   240: aload #10
    //   242: iload #12
    //   244: iconst_1
    //   245: isub
    //   246: laload
    //   247: ladd
    //   248: lastore
    //   249: aload #11
    //   251: iload #12
    //   253: dup2
    //   254: laload
    //   255: aload #11
    //   257: iload #12
    //   259: iconst_1
    //   260: isub
    //   261: laload
    //   262: ladd
    //   263: lastore
    //   264: iinc #12, 1
    //   267: goto -> 227
    //   270: iload_1
    //   271: iload #7
    //   273: isub
    //   274: istore #12
    //   276: iload #12
    //   278: i2l
    //   279: lload_2
    //   280: lsub
    //   281: lstore #13
    //   283: lload #13
    //   285: lconst_0
    //   286: lcmp
    //   287: ifle -> 295
    //   290: aload #10
    //   292: goto -> 297
    //   295: aload #11
    //   297: astore #15
    //   299: lload #13
    //   301: invokestatic abs : (J)J
    //   304: lstore #13
    //   306: iconst_0
    //   307: istore #16
    //   309: iload #16
    //   311: iconst_2
    //   312: if_icmpgt -> 335
    //   315: aload #15
    //   317: iload #16
    //   319: laload
    //   320: lload #13
    //   322: lcmp
    //   323: iflt -> 329
    //   326: goto -> 335
    //   329: iinc #16, 1
    //   332: goto -> 309
    //   335: fconst_0
    //   336: fstore #17
    //   338: iload #16
    //   340: iconst_2
    //   341: if_icmpgt -> 405
    //   344: lload #13
    //   346: iload #16
    //   348: ifle -> 361
    //   351: aload #15
    //   353: iload #16
    //   355: iconst_1
    //   356: isub
    //   357: laload
    //   358: goto -> 362
    //   361: lconst_0
    //   362: lsub
    //   363: lstore #13
    //   365: lload #13
    //   367: lconst_0
    //   368: lcmp
    //   369: ifeq -> 405
    //   372: aload #15
    //   374: iload #16
    //   376: laload
    //   377: iload #16
    //   379: ifle -> 392
    //   382: aload #15
    //   384: iload #16
    //   386: iconst_1
    //   387: isub
    //   388: laload
    //   389: goto -> 393
    //   392: lconst_0
    //   393: lsub
    //   394: l2f
    //   395: fstore #18
    //   397: lload #13
    //   399: l2f
    //   400: fload #18
    //   402: fdiv
    //   403: fstore #17
    //   405: aload_0
    //   406: invokeinterface getBorderWidth : ()F
    //   411: f2i
    //   412: istore #18
    //   414: iconst_0
    //   415: istore #19
    //   417: iload #19
    //   419: iload #8
    //   421: if_icmpge -> 631
    //   424: aload_0
    //   425: iload #19
    //   427: invokeinterface setIndex : (I)V
    //   432: aload_0
    //   433: aload_0
    //   434: invokeinterface getOffset : ()I
    //   439: iload #18
    //   441: iadd
    //   442: invokeinterface setOffset : (I)V
    //   447: aload_0
    //   448: invokeinterface getAdjustmentWeight : ()I
    //   453: iload #16
    //   455: if_icmpge -> 503
    //   458: aload_0
    //   459: iload #12
    //   461: i2l
    //   462: lload_2
    //   463: lcmp
    //   464: ifle -> 482
    //   467: aload_0
    //   468: iload_1
    //   469: i2f
    //   470: invokeinterface getMaximumSpan : (F)F
    //   475: f2d
    //   476: invokestatic floor : (D)D
    //   479: goto -> 494
    //   482: aload_0
    //   483: iload_1
    //   484: i2f
    //   485: invokeinterface getMinimumSpan : (F)F
    //   490: f2d
    //   491: invokestatic ceil : (D)D
    //   494: d2i
    //   495: invokeinterface setSpan : (I)V
    //   500: goto -> 601
    //   503: aload_0
    //   504: invokeinterface getAdjustmentWeight : ()I
    //   509: iload #16
    //   511: if_icmpne -> 601
    //   514: iload #12
    //   516: i2l
    //   517: lload_2
    //   518: lcmp
    //   519: ifle -> 541
    //   522: aload_0
    //   523: iload_1
    //   524: i2f
    //   525: invokeinterface getMaximumSpan : (F)F
    //   530: f2i
    //   531: aload_0
    //   532: invokeinterface getSpan : ()I
    //   537: isub
    //   538: goto -> 557
    //   541: aload_0
    //   542: invokeinterface getSpan : ()I
    //   547: aload_0
    //   548: iload_1
    //   549: i2f
    //   550: invokeinterface getMinimumSpan : (F)F
    //   555: f2i
    //   556: isub
    //   557: istore #20
    //   559: fload #17
    //   561: iload #20
    //   563: i2f
    //   564: fmul
    //   565: f2d
    //   566: invokestatic floor : (D)D
    //   569: d2i
    //   570: istore #21
    //   572: aload_0
    //   573: aload_0
    //   574: invokeinterface getSpan : ()I
    //   579: iload #12
    //   581: i2l
    //   582: lload_2
    //   583: lcmp
    //   584: ifle -> 592
    //   587: iload #21
    //   589: goto -> 595
    //   592: iload #21
    //   594: ineg
    //   595: iadd
    //   596: invokeinterface setSpan : (I)V
    //   601: aload_0
    //   602: invokeinterface getOffset : ()I
    //   607: i2l
    //   608: aload_0
    //   609: invokeinterface getSpan : ()I
    //   614: i2l
    //   615: ladd
    //   616: ldc2_w 2147483647
    //   619: invokestatic min : (JJ)J
    //   622: l2i
    //   623: istore #18
    //   625: iinc #19, 1
    //   628: goto -> 417
    //   631: iload_1
    //   632: iload #18
    //   634: isub
    //   635: aload_0
    //   636: invokeinterface getTrailingCollapseSpan : ()F
    //   641: f2i
    //   642: isub
    //   643: aload_0
    //   644: invokeinterface getBorderWidth : ()F
    //   649: f2i
    //   650: isub
    //   651: istore #19
    //   653: iload #19
    //   655: ifle -> 662
    //   658: iconst_1
    //   659: goto -> 663
    //   662: iconst_m1
    //   663: istore #20
    //   665: iload #19
    //   667: iload #20
    //   669: imul
    //   670: istore #19
    //   672: iconst_1
    //   673: istore #21
    //   675: iload #19
    //   677: ifle -> 818
    //   680: iload #21
    //   682: ifeq -> 818
    //   685: iconst_0
    //   686: istore #21
    //   688: iconst_0
    //   689: istore #22
    //   691: iconst_0
    //   692: istore #23
    //   694: iload #23
    //   696: iload #8
    //   698: if_icmpge -> 815
    //   701: aload_0
    //   702: iload #23
    //   704: invokeinterface setIndex : (I)V
    //   709: aload_0
    //   710: aload_0
    //   711: invokeinterface getOffset : ()I
    //   716: iload #22
    //   718: iadd
    //   719: invokeinterface setOffset : (I)V
    //   724: aload_0
    //   725: invokeinterface getSpan : ()I
    //   730: istore #24
    //   732: iload #19
    //   734: ifle -> 809
    //   737: iload #20
    //   739: ifle -> 761
    //   742: aload_0
    //   743: iload_1
    //   744: i2f
    //   745: invokeinterface getMaximumSpan : (F)F
    //   750: f2d
    //   751: invokestatic floor : (D)D
    //   754: d2i
    //   755: iload #24
    //   757: isub
    //   758: goto -> 777
    //   761: iload #24
    //   763: aload_0
    //   764: iload_1
    //   765: i2f
    //   766: invokeinterface getMinimumSpan : (F)F
    //   771: f2d
    //   772: invokestatic ceil : (D)D
    //   775: d2i
    //   776: isub
    //   777: istore #25
    //   779: iload #25
    //   781: iconst_1
    //   782: if_icmplt -> 809
    //   785: iconst_1
    //   786: istore #21
    //   788: aload_0
    //   789: iload #24
    //   791: iload #20
    //   793: iadd
    //   794: invokeinterface setSpan : (I)V
    //   799: iload #22
    //   801: iload #20
    //   803: iadd
    //   804: istore #22
    //   806: iinc #19, -1
    //   809: iinc #23, 1
    //   812: goto -> 694
    //   815: goto -> 675
    //   818: return }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    Enumeration enumeration = this.valueConvertor.keys();
    paramObjectOutputStream.writeInt(this.valueConvertor.size());
    if (enumeration != null)
      while (enumeration.hasMoreElements()) {
        Object object1 = enumeration.nextElement();
        Object object2 = this.valueConvertor.get(object1);
        if (!(object1 instanceof Serializable) && (object1 = StyleContext.getStaticAttributeKey(object1)) == null) {
          object1 = null;
          object2 = null;
        } else if (!(object2 instanceof Serializable) && (object2 = StyleContext.getStaticAttributeKey(object2)) == null) {
          object1 = null;
          object2 = null;
        } 
        paramObjectOutputStream.writeObject(object1);
        paramObjectOutputStream.writeObject(object2);
      }  
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    this.valueConvertor = new Hashtable();
    while (i-- > 0) {
      Object object1 = paramObjectInputStream.readObject();
      Object object2 = paramObjectInputStream.readObject();
      Object object3 = StyleContext.getStaticAttribute(object1);
      if (object3 != null)
        object1 = object3; 
      Object object4 = StyleContext.getStaticAttribute(object2);
      if (object4 != null)
        object2 = object4; 
      if (object1 != null && object2 != null)
        this.valueConvertor.put(object1, object2); 
    } 
  }
  
  private StyleSheet getStyleSheet(StyleSheet paramStyleSheet) {
    if (paramStyleSheet != null)
      this.styleSheet = paramStyleSheet; 
    return this.styleSheet;
  }
  
  static  {
    byte b;
    for (b = 0; b < Attribute.allAttributes.length; b++)
      attributeMap.put(Attribute.allAttributes[b].toString(), Attribute.allAttributes[b]); 
    for (b = 0; b < Value.allValues.length; b++)
      valueMap.put(Value.allValues[b].toString(), Value.allValues[b]); 
    htmlAttrToCssAttrMap.put(HTML.Attribute.COLOR, new Attribute[] { Attribute.COLOR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.TEXT, new Attribute[] { Attribute.COLOR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.CLEAR, new Attribute[] { Attribute.CLEAR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.BACKGROUND, new Attribute[] { Attribute.BACKGROUND_IMAGE });
    htmlAttrToCssAttrMap.put(HTML.Attribute.BGCOLOR, new Attribute[] { Attribute.BACKGROUND_COLOR });
    htmlAttrToCssAttrMap.put(HTML.Attribute.WIDTH, new Attribute[] { Attribute.WIDTH });
    htmlAttrToCssAttrMap.put(HTML.Attribute.HEIGHT, new Attribute[] { Attribute.HEIGHT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.BORDER, new Attribute[] { Attribute.BORDER_TOP_WIDTH, Attribute.BORDER_RIGHT_WIDTH, Attribute.BORDER_BOTTOM_WIDTH, Attribute.BORDER_LEFT_WIDTH });
    htmlAttrToCssAttrMap.put(HTML.Attribute.CELLPADDING, new Attribute[] { Attribute.PADDING });
    htmlAttrToCssAttrMap.put(HTML.Attribute.CELLSPACING, new Attribute[] { Attribute.BORDER_SPACING });
    htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINWIDTH, new Attribute[] { Attribute.MARGIN_LEFT, Attribute.MARGIN_RIGHT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.MARGINHEIGHT, new Attribute[] { Attribute.MARGIN_TOP, Attribute.MARGIN_BOTTOM });
    htmlAttrToCssAttrMap.put(HTML.Attribute.HSPACE, new Attribute[] { Attribute.PADDING_LEFT, Attribute.PADDING_RIGHT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.VSPACE, new Attribute[] { Attribute.PADDING_BOTTOM, Attribute.PADDING_TOP });
    htmlAttrToCssAttrMap.put(HTML.Attribute.FACE, new Attribute[] { Attribute.FONT_FAMILY });
    htmlAttrToCssAttrMap.put(HTML.Attribute.SIZE, new Attribute[] { Attribute.FONT_SIZE });
    htmlAttrToCssAttrMap.put(HTML.Attribute.VALIGN, new Attribute[] { Attribute.VERTICAL_ALIGN });
    htmlAttrToCssAttrMap.put(HTML.Attribute.ALIGN, new Attribute[] { Attribute.VERTICAL_ALIGN, Attribute.TEXT_ALIGN, Attribute.FLOAT });
    htmlAttrToCssAttrMap.put(HTML.Attribute.TYPE, new Attribute[] { Attribute.LIST_STYLE_TYPE });
    htmlAttrToCssAttrMap.put(HTML.Attribute.NOWRAP, new Attribute[] { Attribute.WHITE_SPACE });
    styleConstantToCssMap.put(StyleConstants.FontFamily, Attribute.FONT_FAMILY);
    styleConstantToCssMap.put(StyleConstants.FontSize, Attribute.FONT_SIZE);
    styleConstantToCssMap.put(StyleConstants.Bold, Attribute.FONT_WEIGHT);
    styleConstantToCssMap.put(StyleConstants.Italic, Attribute.FONT_STYLE);
    styleConstantToCssMap.put(StyleConstants.Underline, Attribute.TEXT_DECORATION);
    styleConstantToCssMap.put(StyleConstants.StrikeThrough, Attribute.TEXT_DECORATION);
    styleConstantToCssMap.put(StyleConstants.Superscript, Attribute.VERTICAL_ALIGN);
    styleConstantToCssMap.put(StyleConstants.Subscript, Attribute.VERTICAL_ALIGN);
    styleConstantToCssMap.put(StyleConstants.Foreground, Attribute.COLOR);
    styleConstantToCssMap.put(StyleConstants.Background, Attribute.BACKGROUND_COLOR);
    styleConstantToCssMap.put(StyleConstants.FirstLineIndent, Attribute.TEXT_INDENT);
    styleConstantToCssMap.put(StyleConstants.LeftIndent, Attribute.MARGIN_LEFT);
    styleConstantToCssMap.put(StyleConstants.RightIndent, Attribute.MARGIN_RIGHT);
    styleConstantToCssMap.put(StyleConstants.SpaceAbove, Attribute.MARGIN_TOP);
    styleConstantToCssMap.put(StyleConstants.SpaceBelow, Attribute.MARGIN_BOTTOM);
    styleConstantToCssMap.put(StyleConstants.Alignment, Attribute.TEXT_ALIGN);
    htmlValueToCssValueMap.put("disc", Value.DISC);
    htmlValueToCssValueMap.put("square", Value.SQUARE);
    htmlValueToCssValueMap.put("circle", Value.CIRCLE);
    htmlValueToCssValueMap.put("1", Value.DECIMAL);
    htmlValueToCssValueMap.put("a", Value.LOWER_ALPHA);
    htmlValueToCssValueMap.put("A", Value.UPPER_ALPHA);
    htmlValueToCssValueMap.put("i", Value.LOWER_ROMAN);
    htmlValueToCssValueMap.put("I", Value.UPPER_ROMAN);
    cssValueToInternalValueMap.put("none", Value.NONE);
    cssValueToInternalValueMap.put("disc", Value.DISC);
    cssValueToInternalValueMap.put("square", Value.SQUARE);
    cssValueToInternalValueMap.put("circle", Value.CIRCLE);
    cssValueToInternalValueMap.put("decimal", Value.DECIMAL);
    cssValueToInternalValueMap.put("lower-roman", Value.LOWER_ROMAN);
    cssValueToInternalValueMap.put("upper-roman", Value.UPPER_ROMAN);
    cssValueToInternalValueMap.put("lower-alpha", Value.LOWER_ALPHA);
    cssValueToInternalValueMap.put("upper-alpha", Value.UPPER_ALPHA);
    cssValueToInternalValueMap.put("repeat", Value.BACKGROUND_REPEAT);
    cssValueToInternalValueMap.put("no-repeat", Value.BACKGROUND_NO_REPEAT);
    cssValueToInternalValueMap.put("repeat-x", Value.BACKGROUND_REPEAT_X);
    cssValueToInternalValueMap.put("repeat-y", Value.BACKGROUND_REPEAT_Y);
    cssValueToInternalValueMap.put("scroll", Value.BACKGROUND_SCROLL);
    cssValueToInternalValueMap.put("fixed", Value.BACKGROUND_FIXED);
    Attribute[] arrayOfAttribute = Attribute.allAttributes;
    try {
      for (Attribute attribute : arrayOfAttribute)
        StyleContext.registerStaticAttributeKey(attribute); 
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
    Value[] arrayOfValue = Value.allValues;
    try {
      for (Value value : arrayOfValue)
        StyleContext.registerStaticAttributeKey(value); 
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
    baseFontSizeIndex = 3;
  }
  
  public static final class Attribute {
    private String name;
    
    private String defaultValue;
    
    private boolean inherited;
    
    public static final Attribute BACKGROUND = new Attribute("background", null, false);
    
    public static final Attribute BACKGROUND_ATTACHMENT = new Attribute("background-attachment", "scroll", false);
    
    public static final Attribute BACKGROUND_COLOR = new Attribute("background-color", "transparent", false);
    
    public static final Attribute BACKGROUND_IMAGE = new Attribute("background-image", "none", false);
    
    public static final Attribute BACKGROUND_POSITION = new Attribute("background-position", null, false);
    
    public static final Attribute BACKGROUND_REPEAT = new Attribute("background-repeat", "repeat", false);
    
    public static final Attribute BORDER = new Attribute("border", null, false);
    
    public static final Attribute BORDER_BOTTOM = new Attribute("border-bottom", null, false);
    
    public static final Attribute BORDER_BOTTOM_COLOR = new Attribute("border-bottom-color", null, false);
    
    public static final Attribute BORDER_BOTTOM_STYLE = new Attribute("border-bottom-style", "none", false);
    
    public static final Attribute BORDER_BOTTOM_WIDTH = new Attribute("border-bottom-width", "medium", false);
    
    public static final Attribute BORDER_COLOR = new Attribute("border-color", null, false);
    
    public static final Attribute BORDER_LEFT = new Attribute("border-left", null, false);
    
    public static final Attribute BORDER_LEFT_COLOR = new Attribute("border-left-color", null, false);
    
    public static final Attribute BORDER_LEFT_STYLE = new Attribute("border-left-style", "none", false);
    
    public static final Attribute BORDER_LEFT_WIDTH = new Attribute("border-left-width", "medium", false);
    
    public static final Attribute BORDER_RIGHT = new Attribute("border-right", null, false);
    
    public static final Attribute BORDER_RIGHT_COLOR = new Attribute("border-right-color", null, false);
    
    public static final Attribute BORDER_RIGHT_STYLE = new Attribute("border-right-style", "none", false);
    
    public static final Attribute BORDER_RIGHT_WIDTH = new Attribute("border-right-width", "medium", false);
    
    public static final Attribute BORDER_STYLE = new Attribute("border-style", "none", false);
    
    public static final Attribute BORDER_TOP = new Attribute("border-top", null, false);
    
    public static final Attribute BORDER_TOP_COLOR = new Attribute("border-top-color", null, false);
    
    public static final Attribute BORDER_TOP_STYLE = new Attribute("border-top-style", "none", false);
    
    public static final Attribute BORDER_TOP_WIDTH = new Attribute("border-top-width", "medium", false);
    
    public static final Attribute BORDER_WIDTH = new Attribute("border-width", "medium", false);
    
    public static final Attribute CLEAR = new Attribute("clear", "none", false);
    
    public static final Attribute COLOR = new Attribute("color", "black", true);
    
    public static final Attribute DISPLAY = new Attribute("display", "block", false);
    
    public static final Attribute FLOAT = new Attribute("float", "none", false);
    
    public static final Attribute FONT = new Attribute("font", null, true);
    
    public static final Attribute FONT_FAMILY = new Attribute("font-family", null, true);
    
    public static final Attribute FONT_SIZE = new Attribute("font-size", "medium", true);
    
    public static final Attribute FONT_STYLE = new Attribute("font-style", "normal", true);
    
    public static final Attribute FONT_VARIANT = new Attribute("font-variant", "normal", true);
    
    public static final Attribute FONT_WEIGHT = new Attribute("font-weight", "normal", true);
    
    public static final Attribute HEIGHT = new Attribute("height", "auto", false);
    
    public static final Attribute LETTER_SPACING = new Attribute("letter-spacing", "normal", true);
    
    public static final Attribute LINE_HEIGHT = new Attribute("line-height", "normal", true);
    
    public static final Attribute LIST_STYLE = new Attribute("list-style", null, true);
    
    public static final Attribute LIST_STYLE_IMAGE = new Attribute("list-style-image", "none", true);
    
    public static final Attribute LIST_STYLE_POSITION = new Attribute("list-style-position", "outside", true);
    
    public static final Attribute LIST_STYLE_TYPE = new Attribute("list-style-type", "disc", true);
    
    public static final Attribute MARGIN = new Attribute("margin", null, false);
    
    public static final Attribute MARGIN_BOTTOM = new Attribute("margin-bottom", "0", false);
    
    public static final Attribute MARGIN_LEFT = new Attribute("margin-left", "0", false);
    
    public static final Attribute MARGIN_RIGHT = new Attribute("margin-right", "0", false);
    
    static final Attribute MARGIN_LEFT_LTR = new Attribute("margin-left-ltr", Integer.toString(-2147483648), false);
    
    static final Attribute MARGIN_LEFT_RTL = new Attribute("margin-left-rtl", Integer.toString(-2147483648), false);
    
    static final Attribute MARGIN_RIGHT_LTR = new Attribute("margin-right-ltr", Integer.toString(-2147483648), false);
    
    static final Attribute MARGIN_RIGHT_RTL = new Attribute("margin-right-rtl", Integer.toString(-2147483648), false);
    
    public static final Attribute MARGIN_TOP = new Attribute("margin-top", "0", false);
    
    public static final Attribute PADDING = new Attribute("padding", null, false);
    
    public static final Attribute PADDING_BOTTOM = new Attribute("padding-bottom", "0", false);
    
    public static final Attribute PADDING_LEFT = new Attribute("padding-left", "0", false);
    
    public static final Attribute PADDING_RIGHT = new Attribute("padding-right", "0", false);
    
    public static final Attribute PADDING_TOP = new Attribute("padding-top", "0", false);
    
    public static final Attribute TEXT_ALIGN = new Attribute("text-align", null, true);
    
    public static final Attribute TEXT_DECORATION = new Attribute("text-decoration", "none", true);
    
    public static final Attribute TEXT_INDENT = new Attribute("text-indent", "0", true);
    
    public static final Attribute TEXT_TRANSFORM = new Attribute("text-transform", "none", true);
    
    public static final Attribute VERTICAL_ALIGN = new Attribute("vertical-align", "baseline", false);
    
    public static final Attribute WORD_SPACING = new Attribute("word-spacing", "normal", true);
    
    public static final Attribute WHITE_SPACE = new Attribute("white-space", "normal", true);
    
    public static final Attribute WIDTH = new Attribute("width", "auto", false);
    
    static final Attribute BORDER_SPACING = new Attribute("border-spacing", "0", true);
    
    static final Attribute CAPTION_SIDE = new Attribute("caption-side", "left", true);
    
    static final Attribute[] allAttributes = { 
        BACKGROUND, BACKGROUND_ATTACHMENT, BACKGROUND_COLOR, BACKGROUND_IMAGE, BACKGROUND_POSITION, BACKGROUND_REPEAT, BORDER, BORDER_BOTTOM, BORDER_BOTTOM_WIDTH, BORDER_COLOR, 
        BORDER_LEFT, BORDER_LEFT_WIDTH, BORDER_RIGHT, BORDER_RIGHT_WIDTH, BORDER_STYLE, BORDER_TOP, BORDER_TOP_WIDTH, BORDER_WIDTH, BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, 
        BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE, BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR, CLEAR, COLOR, DISPLAY, FLOAT, 
        FONT, FONT_FAMILY, FONT_SIZE, FONT_STYLE, FONT_VARIANT, FONT_WEIGHT, HEIGHT, LETTER_SPACING, LINE_HEIGHT, LIST_STYLE, 
        LIST_STYLE_IMAGE, LIST_STYLE_POSITION, LIST_STYLE_TYPE, MARGIN, MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, PADDING, PADDING_BOTTOM, 
        PADDING_LEFT, PADDING_RIGHT, PADDING_TOP, TEXT_ALIGN, TEXT_DECORATION, TEXT_INDENT, TEXT_TRANSFORM, VERTICAL_ALIGN, WORD_SPACING, WHITE_SPACE, 
        WIDTH, BORDER_SPACING, CAPTION_SIDE, MARGIN_LEFT_LTR, MARGIN_LEFT_RTL, MARGIN_RIGHT_LTR, MARGIN_RIGHT_RTL };
    
    private static final Attribute[] ALL_MARGINS = { MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT };
    
    private static final Attribute[] ALL_PADDING = { PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT };
    
    private static final Attribute[] ALL_BORDER_WIDTHS = { BORDER_TOP_WIDTH, BORDER_RIGHT_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH };
    
    private static final Attribute[] ALL_BORDER_STYLES = { BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE };
    
    private static final Attribute[] ALL_BORDER_COLORS = { BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR };
    
    private Attribute(String param1String1, String param1String2, boolean param1Boolean) {
      this.name = param1String1;
      this.defaultValue = param1String2;
      this.inherited = param1Boolean;
    }
    
    public String toString() { return this.name; }
    
    public String getDefaultValue() { return this.defaultValue; }
    
    public boolean isInherited() { return this.inherited; }
  }
  
  static class BackgroundImage extends CssValue {
    private boolean loadedImage;
    
    private ImageIcon image;
    
    Object parseCssValue(String param1String) {
      BackgroundImage backgroundImage = new BackgroundImage();
      backgroundImage.svalue = param1String;
      return backgroundImage;
    }
    
    Object parseHtmlValue(String param1String) { return parseCssValue(param1String); }
    
    ImageIcon getImage(URL param1URL) {
      if (!this.loadedImage)
        synchronized (this) {
          if (!this.loadedImage) {
            URL uRL = CSS.getURL(param1URL, this.svalue);
            this.loadedImage = true;
            if (uRL != null) {
              this.image = new ImageIcon();
              Image image1 = Toolkit.getDefaultToolkit().createImage(uRL);
              if (image1 != null)
                this.image.setImage(image1); 
            } 
          } 
        }  
      return this.image;
    }
  }
  
  static class BackgroundPosition extends CssValue {
    float horizontalPosition;
    
    float verticalPosition;
    
    short relative;
    
    Object parseCssValue(String param1String) {
      String[] arrayOfString = CSS.parseStrings(param1String);
      int i = arrayOfString.length;
      BackgroundPosition backgroundPosition = new BackgroundPosition();
      backgroundPosition.relative = 5;
      backgroundPosition.svalue = param1String;
      if (i > 0) {
        short s = 0;
        byte b = 0;
        while (b < i) {
          String str = arrayOfString[b++];
          if (str.equals("center")) {
            s = (short)(s | 0x4);
            continue;
          } 
          if ((s & true) == 0)
            if (str.equals("top")) {
              s = (short)(s | true);
            } else if (str.equals("bottom")) {
              s = (short)(s | true);
              backgroundPosition.verticalPosition = 1.0F;
              continue;
            }  
          if ((s & 0x2) == 0) {
            if (str.equals("left")) {
              s = (short)(s | 0x2);
              backgroundPosition.horizontalPosition = 0.0F;
              continue;
            } 
            if (str.equals("right")) {
              s = (short)(s | 0x2);
              backgroundPosition.horizontalPosition = 1.0F;
            } 
          } 
        } 
        if (s != 0) {
          if ((s & true) == 1) {
            if ((s & 0x2) == 0)
              backgroundPosition.horizontalPosition = 0.5F; 
          } else if ((s & 0x2) == 2) {
            backgroundPosition.verticalPosition = 0.5F;
          } else {
            backgroundPosition.horizontalPosition = backgroundPosition.verticalPosition = 0.5F;
          } 
        } else {
          CSS.LengthUnit lengthUnit = new CSS.LengthUnit(arrayOfString[0], (short)0, 0.0F);
          if (lengthUnit.type == 0) {
            backgroundPosition.horizontalPosition = lengthUnit.value;
            backgroundPosition.relative = (short)(true ^ backgroundPosition.relative);
          } else if (lengthUnit.type == 1) {
            backgroundPosition.horizontalPosition = lengthUnit.value;
          } else if (lengthUnit.type == 3) {
            backgroundPosition.horizontalPosition = lengthUnit.value;
            backgroundPosition.relative = (short)(true ^ backgroundPosition.relative | 0x2);
          } 
          if (i > 1) {
            lengthUnit = new CSS.LengthUnit(arrayOfString[1], (short)0, 0.0F);
            if (lengthUnit.type == 0) {
              backgroundPosition.verticalPosition = lengthUnit.value;
              backgroundPosition.relative = (short)(0x4 ^ backgroundPosition.relative);
            } else if (lengthUnit.type == 1) {
              backgroundPosition.verticalPosition = lengthUnit.value;
            } else if (lengthUnit.type == 3) {
              backgroundPosition.verticalPosition = lengthUnit.value;
              backgroundPosition.relative = (short)(0x4 ^ backgroundPosition.relative | 0x8);
            } 
          } else {
            backgroundPosition.verticalPosition = 0.5F;
          } 
        } 
      } 
      return backgroundPosition;
    }
    
    boolean isHorizontalPositionRelativeToSize() { return ((this.relative & true) == 1); }
    
    boolean isHorizontalPositionRelativeToFontSize() { return ((this.relative & 0x2) == 2); }
    
    float getHorizontalPosition() { return this.horizontalPosition; }
    
    boolean isVerticalPositionRelativeToSize() { return ((this.relative & 0x4) == 4); }
    
    boolean isVerticalPositionRelativeToFontSize() { return ((this.relative & 0x8) == 8); }
    
    float getVerticalPosition() { return this.verticalPosition; }
  }
  
  static class BorderStyle extends CssValue {
    private CSS.Value style;
    
    CSS.Value getValue() { return this.style; }
    
    Object parseCssValue(String param1String) {
      CSS.Value value = CSS.getValue(param1String);
      if (value != null && (value == CSS.Value.INSET || value == CSS.Value.OUTSET || value == CSS.Value.NONE || value == CSS.Value.DOTTED || value == CSS.Value.DASHED || value == CSS.Value.SOLID || value == CSS.Value.DOUBLE || value == CSS.Value.GROOVE || value == CSS.Value.RIDGE)) {
        BorderStyle borderStyle = new BorderStyle();
        borderStyle.svalue = param1String;
        borderStyle.style = value;
        return borderStyle;
      } 
      return null;
    }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      param1ObjectOutputStream.defaultWriteObject();
      if (this.style == null) {
        param1ObjectOutputStream.writeObject(null);
      } else {
        param1ObjectOutputStream.writeObject(this.style.toString());
      } 
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException {
      param1ObjectInputStream.defaultReadObject();
      Object object = param1ObjectInputStream.readObject();
      if (object != null)
        this.style = CSS.getValue((String)object); 
    }
  }
  
  static class BorderWidthValue extends LengthValue {
    private static final float[] values = { 1.0F, 2.0F, 4.0F };
    
    BorderWidthValue(String param1String, int param1Int) {
      this.svalue = param1String;
      this.span = values[param1Int];
      this.percentage = false;
    }
    
    Object parseCssValue(String param1String) {
      if (param1String != null) {
        if (param1String.equals("thick"))
          return new BorderWidthValue(param1String, 2); 
        if (param1String.equals("medium"))
          return new BorderWidthValue(param1String, 1); 
        if (param1String.equals("thin"))
          return new BorderWidthValue(param1String, 0); 
      } 
      return super.parseCssValue(param1String);
    }
    
    Object parseHtmlValue(String param1String) { return (param1String == "#DEFAULT") ? parseCssValue("medium") : parseCssValue(param1String); }
  }
  
  static class ColorValue extends CssValue {
    Color c;
    
    Color getValue() { return this.c; }
    
    Object parseCssValue(String param1String) {
      Color color = CSS.stringToColor(param1String);
      if (color != null) {
        ColorValue colorValue = new ColorValue();
        colorValue.svalue = param1String;
        colorValue.c = color;
        return colorValue;
      } 
      return null;
    }
    
    Object parseHtmlValue(String param1String) { return parseCssValue(param1String); }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) {
      ColorValue colorValue = new ColorValue();
      colorValue.c = (Color)param1Object;
      colorValue.svalue = CSS.colorToHex(colorValue.c);
      return colorValue;
    }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return this.c; }
  }
  
  static class CssValue implements Serializable {
    String svalue;
    
    Object parseCssValue(String param1String) { return param1String; }
    
    Object parseHtmlValue(String param1String) { return parseCssValue(param1String); }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) { return null; }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return null; }
    
    public String toString() { return this.svalue; }
  }
  
  static class CssValueMapper extends CssValue {
    Object parseCssValue(String param1String) {
      Object object = cssValueToInternalValueMap.get(param1String);
      if (object == null)
        object = cssValueToInternalValueMap.get(param1String.toLowerCase()); 
      return object;
    }
    
    Object parseHtmlValue(String param1String) {
      Object object = htmlValueToCssValueMap.get(param1String);
      if (object == null)
        object = htmlValueToCssValueMap.get(param1String.toLowerCase()); 
      return object;
    }
  }
  
  static class FontFamily extends CssValue {
    String family;
    
    String getValue() { return this.family; }
    
    Object parseCssValue(String param1String) {
      int i = param1String.indexOf(',');
      FontFamily fontFamily = new FontFamily();
      fontFamily.svalue = param1String;
      fontFamily.family = null;
      if (i == -1) {
        setFontName(fontFamily, param1String);
      } else {
        boolean bool = false;
        int j = param1String.length();
        i = 0;
        while (!bool) {
          while (i < j && Character.isWhitespace(param1String.charAt(i)))
            i++; 
          int k = i;
          i = param1String.indexOf(',', i);
          if (i == -1)
            i = j; 
          if (k < j) {
            if (k != i) {
              int m = i;
              if (i > 0 && param1String.charAt(i - 1) == ' ')
                m--; 
              setFontName(fontFamily, param1String.substring(k, m));
              bool = (fontFamily.family != null) ? 1 : 0;
            } 
            i++;
            continue;
          } 
          bool = true;
        } 
      } 
      if (fontFamily.family == null)
        fontFamily.family = "SansSerif"; 
      return fontFamily;
    }
    
    private void setFontName(FontFamily param1FontFamily, String param1String) { param1FontFamily.family = param1String; }
    
    Object parseHtmlValue(String param1String) { return parseCssValue(param1String); }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) { return parseCssValue(param1Object.toString()); }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return this.family; }
  }
  
  class FontSize extends CssValue {
    float value;
    
    boolean index;
    
    CSS.LengthUnit lu;
    
    int getValue(AttributeSet param1AttributeSet, StyleSheet param1StyleSheet) {
      param1StyleSheet = CSS.this.getStyleSheet(param1StyleSheet);
      if (this.index)
        return Math.round(CSS.this.getPointSize((int)this.value, param1StyleSheet)); 
      if (this.lu == null)
        return Math.round(this.value); 
      if (this.lu.type == 0) {
        boolean bool = (param1StyleSheet == null) ? false : param1StyleSheet.isW3CLengthUnits();
        return Math.round(this.lu.getValue(bool));
      } 
      if (param1AttributeSet != null) {
        AttributeSet attributeSet = param1AttributeSet.getResolveParent();
        if (attributeSet != null) {
          float f;
          int i = StyleConstants.getFontSize(attributeSet);
          if (this.lu.type == 1 || this.lu.type == 3) {
            f = this.lu.value * i;
          } else {
            f = this.lu.value + i;
          } 
          return Math.round(f);
        } 
      } 
      return 12;
    }
    
    Object parseCssValue(String param1String) {
      FontSize fontSize = new FontSize(CSS.this);
      fontSize.svalue = param1String;
      try {
        if (param1String.equals("xx-small")) {
          fontSize.value = 1.0F;
          fontSize.index = true;
        } else if (param1String.equals("x-small")) {
          fontSize.value = 2.0F;
          fontSize.index = true;
        } else if (param1String.equals("small")) {
          fontSize.value = 3.0F;
          fontSize.index = true;
        } else if (param1String.equals("medium")) {
          fontSize.value = 4.0F;
          fontSize.index = true;
        } else if (param1String.equals("large")) {
          fontSize.value = 5.0F;
          fontSize.index = true;
        } else if (param1String.equals("x-large")) {
          fontSize.value = 6.0F;
          fontSize.index = true;
        } else if (param1String.equals("xx-large")) {
          fontSize.value = 7.0F;
          fontSize.index = true;
        } else {
          fontSize.lu = new CSS.LengthUnit(param1String, (short)1, 1.0F);
        } 
      } catch (NumberFormatException numberFormatException) {
        fontSize = null;
      } 
      return fontSize;
    }
    
    Object parseHtmlValue(String param1String) {
      if (param1String == null || param1String.length() == 0)
        return null; 
      FontSize fontSize = new FontSize(CSS.this);
      fontSize.svalue = param1String;
      try {
        int i = CSS.this.getBaseFontSize();
        if (param1String.charAt(0) == '+') {
          int j = Integer.valueOf(param1String.substring(1)).intValue();
          fontSize.value = (i + j);
          fontSize.index = true;
        } else if (param1String.charAt(0) == '-') {
          int j = -Integer.valueOf(param1String.substring(1)).intValue();
          fontSize.value = (i + j);
          fontSize.index = true;
        } else {
          fontSize.value = Integer.parseInt(param1String);
          if (fontSize.value > 7.0F) {
            fontSize.value = 7.0F;
          } else if (fontSize.value < 0.0F) {
            fontSize.value = 0.0F;
          } 
          fontSize.index = true;
        } 
      } catch (NumberFormatException numberFormatException) {
        fontSize = null;
      } 
      return fontSize;
    }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) {
      if (param1Object instanceof Number) {
        FontSize fontSize = new FontSize(CSS.this);
        fontSize.value = CSS.getIndexOfSize(((Number)param1Object).floatValue(), StyleSheet.sizeMapDefault);
        fontSize.svalue = Integer.toString((int)fontSize.value);
        fontSize.index = true;
        return fontSize;
      } 
      return parseCssValue(param1Object.toString());
    }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return (param1View != null) ? Integer.valueOf(getValue(param1View.getAttributes(), null)) : Integer.valueOf(getValue(null, null)); }
  }
  
  static class FontWeight extends CssValue {
    int weight;
    
    int getValue() { return this.weight; }
    
    Object parseCssValue(String param1String) {
      FontWeight fontWeight = new FontWeight();
      fontWeight.svalue = param1String;
      if (param1String.equals("bold")) {
        fontWeight.weight = 700;
      } else if (param1String.equals("normal")) {
        fontWeight.weight = 400;
      } else {
        try {
          fontWeight.weight = Integer.parseInt(param1String);
        } catch (NumberFormatException numberFormatException) {
          fontWeight = null;
        } 
      } 
      return fontWeight;
    }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) { return param1Object.equals(Boolean.TRUE) ? parseCssValue("bold") : parseCssValue("normal"); }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return (this.weight > 500) ? Boolean.TRUE : Boolean.FALSE; }
    
    boolean isBold() { return (this.weight > 500); }
  }
  
  static interface LayoutIterator {
    public static final int WorstAdjustmentWeight = 2;
    
    void setOffset(int param1Int);
    
    int getOffset();
    
    void setSpan(int param1Int);
    
    int getSpan();
    
    int getCount();
    
    void setIndex(int param1Int);
    
    float getMinimumSpan(float param1Float);
    
    float getPreferredSpan(float param1Float);
    
    float getMaximumSpan(float param1Float);
    
    int getAdjustmentWeight();
    
    float getBorderWidth();
    
    float getLeadingCollapseSpan();
    
    float getTrailingCollapseSpan();
  }
  
  static class LengthUnit implements Serializable {
    static Hashtable<String, Float> lengthMapping = new Hashtable(6);
    
    static Hashtable<String, Float> w3cLengthMapping = new Hashtable(6);
    
    short type;
    
    float value;
    
    String units = null;
    
    static final short UNINITALIZED_LENGTH = 10;
    
    LengthUnit(String param1String, short param1Short, float param1Float) { parse(param1String, param1Short, param1Float); }
    
    void parse(String param1String, short param1Short, float param1Float) {
      this.type = param1Short;
      this.value = param1Float;
      int i = param1String.length();
      if (i > 0 && param1String.charAt(i - 1) == '%')
        try {
          this.value = Float.valueOf(param1String.substring(0, i - 1)).floatValue() / 100.0F;
          this.type = 1;
        } catch (NumberFormatException numberFormatException) {} 
      if (i >= 2) {
        this.units = param1String.substring(i - 2, i);
        Float float = (Float)lengthMapping.get(this.units);
        if (float != null) {
          try {
            this.value = Float.valueOf(param1String.substring(0, i - 2)).floatValue();
            this.type = 0;
          } catch (NumberFormatException numberFormatException) {}
        } else if (this.units.equals("em") || this.units.equals("ex")) {
          try {
            this.value = Float.valueOf(param1String.substring(0, i - 2)).floatValue();
            this.type = 3;
          } catch (NumberFormatException numberFormatException) {}
        } else if (param1String.equals("larger")) {
          this.value = 2.0F;
          this.type = 2;
        } else if (param1String.equals("smaller")) {
          this.value = -2.0F;
          this.type = 2;
        } else {
          try {
            this.value = Float.valueOf(param1String).floatValue();
            this.type = 0;
          } catch (NumberFormatException numberFormatException) {}
        } 
      } else if (i > 0) {
        try {
          this.value = Float.valueOf(param1String).floatValue();
          this.type = 0;
        } catch (NumberFormatException numberFormatException) {}
      } 
    }
    
    float getValue(boolean param1Boolean) {
      Hashtable hashtable = param1Boolean ? w3cLengthMapping : lengthMapping;
      float f = 1.0F;
      if (this.units != null) {
        Float float = (Float)hashtable.get(this.units);
        if (float != null)
          f = float.floatValue(); 
      } 
      return this.value * f;
    }
    
    static float getValue(float param1Float, String param1String, Boolean param1Boolean) {
      Hashtable hashtable = param1Boolean.booleanValue() ? w3cLengthMapping : lengthMapping;
      float f = 1.0F;
      if (param1String != null) {
        Float float = (Float)hashtable.get(param1String);
        if (float != null)
          f = float.floatValue(); 
      } 
      return param1Float * f;
    }
    
    public String toString() { return this.type + " " + this.value; }
    
    static  {
      lengthMapping.put("pt", new Float(1.0F));
      lengthMapping.put("px", new Float(1.3F));
      lengthMapping.put("mm", new Float(2.83464F));
      lengthMapping.put("cm", new Float(28.3464F));
      lengthMapping.put("pc", new Float(12.0F));
      lengthMapping.put("in", new Float(72.0F));
      int i = 72;
      try {
        i = Toolkit.getDefaultToolkit().getScreenResolution();
      } catch (HeadlessException headlessException) {}
      w3cLengthMapping.put("pt", new Float(i / 72.0F));
      w3cLengthMapping.put("px", new Float(1.0F));
      w3cLengthMapping.put("mm", new Float(i / 25.4F));
      w3cLengthMapping.put("cm", new Float(i / 2.54F));
      w3cLengthMapping.put("pc", new Float(i / 6.0F));
      w3cLengthMapping.put("in", new Float(i));
    }
  }
  
  static class LengthValue extends CssValue {
    boolean mayBeNegative;
    
    boolean percentage;
    
    float span;
    
    String units = null;
    
    LengthValue() { this(false); }
    
    LengthValue(boolean param1Boolean) { this.mayBeNegative = param1Boolean; }
    
    float getValue() { return getValue(false); }
    
    float getValue(boolean param1Boolean) { return getValue(0.0F, param1Boolean); }
    
    float getValue(float param1Float) { return getValue(param1Float, false); }
    
    float getValue(float param1Float, boolean param1Boolean) { return this.percentage ? (this.span * param1Float) : CSS.LengthUnit.getValue(this.span, this.units, Boolean.valueOf(param1Boolean)); }
    
    boolean isPercentage() { return this.percentage; }
    
    Object parseCssValue(String param1String) {
      LengthValue lengthValue;
      try {
        float f = Float.valueOf(param1String).floatValue();
        lengthValue = new LengthValue();
        lengthValue.span = f;
      } catch (NumberFormatException numberFormatException) {
        CSS.LengthUnit lengthUnit = new CSS.LengthUnit(param1String, (short)10, 0.0F);
        switch (lengthUnit.type) {
          case 0:
            lengthValue = new LengthValue();
            lengthValue.span = this.mayBeNegative ? lengthUnit.value : Math.max(0.0F, lengthUnit.value);
            lengthValue.units = lengthUnit.units;
            lengthValue.svalue = param1String;
            return lengthValue;
          case 1:
            lengthValue = new LengthValue();
            lengthValue.span = Math.max(0.0F, Math.min(1.0F, lengthUnit.value));
            lengthValue.percentage = true;
            lengthValue.svalue = param1String;
            return lengthValue;
        } 
        return null;
      } 
      lengthValue.svalue = param1String;
      return lengthValue;
    }
    
    Object parseHtmlValue(String param1String) {
      if (param1String.equals("#DEFAULT"))
        param1String = "1"; 
      return parseCssValue(param1String);
    }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) {
      LengthValue lengthValue = new LengthValue();
      lengthValue.svalue = param1Object.toString();
      lengthValue.span = ((Float)param1Object).floatValue();
      return lengthValue;
    }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return new Float(getValue(false)); }
  }
  
  static class ShorthandBackgroundParser {
    static void parseShorthandBackground(CSS param1CSS, String param1String, MutableAttributeSet param1MutableAttributeSet) {
      String[] arrayOfString = CSS.parseStrings(param1String);
      int i = arrayOfString.length;
      byte b = 0;
      short s = 0;
      while (b < i) {
        String str = arrayOfString[b++];
        if (!(s & true) && isImage(str)) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_IMAGE, str);
          s = (short)(s | true);
          continue;
        } 
        if ((s & 0x2) == 0 && isRepeat(str)) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_REPEAT, str);
          s = (short)(s | 0x2);
          continue;
        } 
        if ((s & 0x4) == 0 && isAttachment(str)) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_ATTACHMENT, str);
          s = (short)(s | 0x4);
          continue;
        } 
        if ((s & 0x8) == 0 && isPosition(str)) {
          if (b < i && isPosition(arrayOfString[b])) {
            param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_POSITION, str + " " + arrayOfString[b++]);
          } else {
            param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_POSITION, str);
          } 
          s = (short)(s | 0x8);
          continue;
        } 
        if ((s & 0x10) == 0 && isColor(str)) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_COLOR, str);
          s = (short)(s | 0x10);
        } 
      } 
      if ((s & true) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_IMAGE, null); 
      if ((s & 0x2) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_REPEAT, "repeat"); 
      if ((s & 0x4) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_ATTACHMENT, "scroll"); 
      if ((s & 0x8) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.BACKGROUND_POSITION, null); 
    }
    
    static boolean isImage(String param1String) { return (param1String.startsWith("url(") && param1String.endsWith(")")); }
    
    static boolean isRepeat(String param1String) { return (param1String.equals("repeat-x") || param1String.equals("repeat-y") || param1String.equals("repeat") || param1String.equals("no-repeat")); }
    
    static boolean isAttachment(String param1String) { return (param1String.equals("fixed") || param1String.equals("scroll")); }
    
    static boolean isPosition(String param1String) { return (param1String.equals("top") || param1String.equals("bottom") || param1String.equals("left") || param1String.equals("right") || param1String.equals("center") || (param1String.length() > 0 && Character.isDigit(param1String.charAt(0)))); }
    
    static boolean isColor(String param1String) { return (CSS.stringToColor(param1String) != null); }
  }
  
  static class ShorthandBorderParser {
    static CSS.Attribute[] keys = { CSS.Attribute.BORDER_TOP, CSS.Attribute.BORDER_RIGHT, CSS.Attribute.BORDER_BOTTOM, CSS.Attribute.BORDER_LEFT };
    
    static void parseShorthandBorder(MutableAttributeSet param1MutableAttributeSet, CSS.Attribute param1Attribute, String param1String) {
      Object[] arrayOfObject = new Object[CSSBorder.PARSERS.length];
      String[] arrayOfString = CSS.parseStrings(param1String);
      for (String str : arrayOfString) {
        boolean bool = false;
        for (byte b1 = 0; b1 < arrayOfObject.length; b1++) {
          Object object = CSSBorder.PARSERS[b1].parseCssValue(str);
          if (object != null) {
            if (arrayOfObject[b1] == null) {
              arrayOfObject[b1] = object;
              bool = true;
            } 
            break;
          } 
        } 
        if (!bool)
          return; 
      } 
      byte b;
      for (b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] == null)
          arrayOfObject[b] = CSSBorder.DEFAULTS[b]; 
      } 
      for (b = 0; b < keys.length; b++) {
        if (param1Attribute == CSS.Attribute.BORDER || param1Attribute == keys[b])
          for (byte b1 = 0; b1 < arrayOfObject.length; b1++)
            param1MutableAttributeSet.addAttribute(CSSBorder.ATTRIBUTES[b1][b], arrayOfObject[b1]);  
      } 
    }
  }
  
  static class ShorthandFontParser {
    static void parseShorthandFont(CSS param1CSS, String param1String, MutableAttributeSet param1MutableAttributeSet) {
      String[] arrayOfString = CSS.parseStrings(param1String);
      int i = arrayOfString.length;
      byte b = 0;
      short s = 0;
      int j = Math.min(3, i);
      while (b < j) {
        if (!(s & true) && isFontStyle(arrayOfString[b])) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_STYLE, arrayOfString[b++]);
          s = (short)(s | true);
          continue;
        } 
        if ((s & 0x2) == 0 && isFontVariant(arrayOfString[b])) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_VARIANT, arrayOfString[b++]);
          s = (short)(s | 0x2);
          continue;
        } 
        if ((s & 0x4) == 0 && isFontWeight(arrayOfString[b])) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_WEIGHT, arrayOfString[b++]);
          s = (short)(s | 0x4);
          continue;
        } 
        if (arrayOfString[b].equals("normal"))
          b++; 
      } 
      if ((s & true) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_STYLE, "normal"); 
      if ((s & 0x2) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_VARIANT, "normal"); 
      if ((s & 0x4) == 0)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_WEIGHT, "normal"); 
      if (b < i) {
        String str = arrayOfString[b];
        int k = str.indexOf('/');
        if (k != -1) {
          str = str.substring(0, k);
          arrayOfString[b] = arrayOfString[b].substring(k);
        } else {
          b++;
        } 
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_SIZE, str);
      } else {
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_SIZE, "medium");
      } 
      if (b < i && arrayOfString[b].startsWith("/")) {
        String str = null;
        if (arrayOfString[b].equals("/")) {
          if (++b < i)
            str = arrayOfString[b++]; 
        } else {
          str = arrayOfString[b++].substring(1);
        } 
        if (str != null) {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.LINE_HEIGHT, str);
        } else {
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.LINE_HEIGHT, "normal");
        } 
      } else {
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.LINE_HEIGHT, "normal");
      } 
      if (b < i) {
        String str;
        for (str = arrayOfString[b++]; b < i; str = str + " " + arrayOfString[b++]);
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_FAMILY, str);
      } else {
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, CSS.Attribute.FONT_FAMILY, "SansSerif");
      } 
    }
    
    private static boolean isFontStyle(String param1String) { return (param1String.equals("italic") || param1String.equals("oblique")); }
    
    private static boolean isFontVariant(String param1String) { return param1String.equals("small-caps"); }
    
    private static boolean isFontWeight(String param1String) { return (param1String.equals("bold") || param1String.equals("bolder") || param1String.equals("italic") || param1String.equals("lighter")) ? true : ((param1String.length() == 3 && param1String.charAt(0) >= '1' && param1String.charAt(0) <= '9' && param1String.charAt(1) == '0' && param1String.charAt(2) == '0')); }
  }
  
  static class ShorthandMarginParser {
    static void parseShorthandMargin(CSS param1CSS, String param1String, MutableAttributeSet param1MutableAttributeSet, CSS.Attribute[] param1ArrayOfAttribute) {
      String[] arrayOfString = CSS.parseStrings(param1String);
      int i = arrayOfString.length;
      boolean bool = false;
      switch (i) {
        case 0:
          return;
        case 1:
          for (b = 0; b < 4; b++)
            param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[b], arrayOfString[0]); 
          return;
        case 2:
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[0], arrayOfString[0]);
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[2], arrayOfString[0]);
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[1], arrayOfString[1]);
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[3], arrayOfString[1]);
          return;
        case 3:
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[0], arrayOfString[0]);
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[1], arrayOfString[1]);
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[2], arrayOfString[2]);
          param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[3], arrayOfString[1]);
          return;
      } 
      for (byte b = 0; b < 4; b++)
        param1CSS.addInternalCSSValue(param1MutableAttributeSet, param1ArrayOfAttribute[b], arrayOfString[b]); 
    }
  }
  
  static class StringValue extends CssValue {
    Object parseCssValue(String param1String) {
      StringValue stringValue = new StringValue();
      stringValue.svalue = param1String;
      return stringValue;
    }
    
    Object fromStyleConstants(StyleConstants param1StyleConstants, Object param1Object) {
      if (param1StyleConstants == StyleConstants.Italic)
        return param1Object.equals(Boolean.TRUE) ? parseCssValue("italic") : parseCssValue(""); 
      if (param1StyleConstants == StyleConstants.Underline)
        return param1Object.equals(Boolean.TRUE) ? parseCssValue("underline") : parseCssValue(""); 
      if (param1StyleConstants == StyleConstants.Alignment) {
        int i = ((Integer)param1Object).intValue();
        switch (i) {
          case 0:
            str = "left";
            return parseCssValue(str);
          case 2:
            str = "right";
            return parseCssValue(str);
          case 1:
            str = "center";
            return parseCssValue(str);
          case 3:
            str = "justify";
            return parseCssValue(str);
        } 
        String str = "left";
        return parseCssValue(str);
      } 
      return (param1StyleConstants == StyleConstants.StrikeThrough) ? (param1Object.equals(Boolean.TRUE) ? parseCssValue("line-through") : parseCssValue("")) : ((param1StyleConstants == StyleConstants.Superscript) ? (param1Object.equals(Boolean.TRUE) ? parseCssValue("super") : parseCssValue("")) : ((param1StyleConstants == StyleConstants.Subscript) ? (param1Object.equals(Boolean.TRUE) ? parseCssValue("sub") : parseCssValue("")) : null));
    }
    
    Object toStyleConstants(StyleConstants param1StyleConstants, View param1View) { return (param1StyleConstants == StyleConstants.Italic) ? ((this.svalue.indexOf("italic") >= 0) ? Boolean.TRUE : Boolean.FALSE) : ((param1StyleConstants == StyleConstants.Underline) ? ((this.svalue.indexOf("underline") >= 0) ? Boolean.TRUE : Boolean.FALSE) : ((param1StyleConstants == StyleConstants.Alignment) ? (this.svalue.equals("right") ? new Integer(2) : (this.svalue.equals("center") ? new Integer(1) : (this.svalue.equals("justify") ? new Integer(3) : new Integer(0)))) : ((param1StyleConstants == StyleConstants.StrikeThrough) ? ((this.svalue.indexOf("line-through") >= 0) ? Boolean.TRUE : Boolean.FALSE) : ((param1StyleConstants == StyleConstants.Superscript) ? ((this.svalue.indexOf("super") >= 0) ? Boolean.TRUE : Boolean.FALSE) : ((param1StyleConstants == StyleConstants.Subscript) ? ((this.svalue.indexOf("sub") >= 0) ? Boolean.TRUE : Boolean.FALSE) : null))))); }
    
    boolean isItalic() { return (this.svalue.indexOf("italic") != -1); }
    
    boolean isStrike() { return (this.svalue.indexOf("line-through") != -1); }
    
    boolean isUnderline() { return (this.svalue.indexOf("underline") != -1); }
    
    boolean isSub() { return (this.svalue.indexOf("sub") != -1); }
    
    boolean isSup() { return (this.svalue.indexOf("sup") != -1); }
  }
  
  static final class Value {
    static final Value INHERITED = new Value("inherited");
    
    static final Value NONE = new Value("none");
    
    static final Value HIDDEN = new Value("hidden");
    
    static final Value DOTTED = new Value("dotted");
    
    static final Value DASHED = new Value("dashed");
    
    static final Value SOLID = new Value("solid");
    
    static final Value DOUBLE = new Value("double");
    
    static final Value GROOVE = new Value("groove");
    
    static final Value RIDGE = new Value("ridge");
    
    static final Value INSET = new Value("inset");
    
    static final Value OUTSET = new Value("outset");
    
    static final Value DISC = new Value("disc");
    
    static final Value CIRCLE = new Value("circle");
    
    static final Value SQUARE = new Value("square");
    
    static final Value DECIMAL = new Value("decimal");
    
    static final Value LOWER_ROMAN = new Value("lower-roman");
    
    static final Value UPPER_ROMAN = new Value("upper-roman");
    
    static final Value LOWER_ALPHA = new Value("lower-alpha");
    
    static final Value UPPER_ALPHA = new Value("upper-alpha");
    
    static final Value BACKGROUND_NO_REPEAT = new Value("no-repeat");
    
    static final Value BACKGROUND_REPEAT = new Value("repeat");
    
    static final Value BACKGROUND_REPEAT_X = new Value("repeat-x");
    
    static final Value BACKGROUND_REPEAT_Y = new Value("repeat-y");
    
    static final Value BACKGROUND_SCROLL = new Value("scroll");
    
    static final Value BACKGROUND_FIXED = new Value("fixed");
    
    private String name;
    
    static final Value[] allValues = { 
        INHERITED, NONE, DOTTED, DASHED, SOLID, DOUBLE, GROOVE, RIDGE, INSET, OUTSET, 
        DISC, CIRCLE, SQUARE, DECIMAL, LOWER_ROMAN, UPPER_ROMAN, LOWER_ALPHA, UPPER_ALPHA, BACKGROUND_NO_REPEAT, BACKGROUND_REPEAT, 
        BACKGROUND_REPEAT_X, BACKGROUND_REPEAT_Y, BACKGROUND_FIXED, BACKGROUND_FIXED };
    
    private Value(String param1String) { this.name = param1String; }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\CSS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */