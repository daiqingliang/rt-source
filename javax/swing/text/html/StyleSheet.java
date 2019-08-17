package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class StyleSheet extends StyleContext {
  static final Border noBorder = new EmptyBorder(0, 0, 0, 0);
  
  static final int DEFAULT_FONT_SIZE = 3;
  
  private CSS css;
  
  private SelectorMapping selectorMapping = new SelectorMapping(0);
  
  private Hashtable<String, ResolvedStyle> resolvedStyles = new Hashtable();
  
  private Vector<StyleSheet> linkedStyleSheets;
  
  private URL base;
  
  static final int[] sizeMapDefault = { 8, 10, 12, 14, 18, 24, 36 };
  
  private int[] sizeMap = sizeMapDefault;
  
  private boolean w3cLengthUnits = false;
  
  public StyleSheet() {
    if (this.css == null)
      this.css = new CSS(); 
  }
  
  public Style getRule(HTML.Tag paramTag, Element paramElement) {
    searchBuffer = SearchBuffer.obtainSearchBuffer();
    try {
      Vector vector = searchBuffer.getVector();
      for (Element element = paramElement; element != null; element = element.getParentElement())
        vector.addElement(element); 
      int i = vector.size();
      StringBuffer stringBuffer = searchBuffer.getStringBuffer();
      for (int j = i - 1; j >= 1; j--) {
        paramElement = (Element)vector.elementAt(j);
        AttributeSet attributeSet1 = paramElement.getAttributes();
        Object object = attributeSet1.getAttribute(StyleConstants.NameAttribute);
        String str = object.toString();
        stringBuffer.append(str);
        if (attributeSet1 != null)
          if (attributeSet1.isDefined(HTML.Attribute.ID)) {
            stringBuffer.append('#');
            stringBuffer.append(attributeSet1.getAttribute(HTML.Attribute.ID));
          } else if (attributeSet1.isDefined(HTML.Attribute.CLASS)) {
            stringBuffer.append('.');
            stringBuffer.append(attributeSet1.getAttribute(HTML.Attribute.CLASS));
          }  
        stringBuffer.append(' ');
      } 
      stringBuffer.append(paramTag.toString());
      paramElement = (Element)vector.elementAt(0);
      AttributeSet attributeSet = paramElement.getAttributes();
      if (paramElement.isLeaf()) {
        Object object = attributeSet.getAttribute(paramTag);
        if (object instanceof AttributeSet) {
          attributeSet = (AttributeSet)object;
        } else {
          attributeSet = null;
        } 
      } 
      if (attributeSet != null)
        if (attributeSet.isDefined(HTML.Attribute.ID)) {
          stringBuffer.append('#');
          stringBuffer.append(attributeSet.getAttribute(HTML.Attribute.ID));
        } else if (attributeSet.isDefined(HTML.Attribute.CLASS)) {
          stringBuffer.append('.');
          stringBuffer.append(attributeSet.getAttribute(HTML.Attribute.CLASS));
        }  
      Style style = getResolvedStyle(stringBuffer.toString(), vector, paramTag);
      return style;
    } finally {
      SearchBuffer.releaseSearchBuffer(searchBuffer);
    } 
  }
  
  public Style getRule(String paramString) {
    paramString = cleanSelectorString(paramString);
    return (paramString != null) ? getResolvedStyle(paramString) : null;
  }
  
  public void addRule(String paramString) {
    if (paramString != null)
      if (paramString == "BASE_SIZE_DISABLE") {
        this.sizeMap = sizeMapDefault;
      } else if (paramString.startsWith("BASE_SIZE ")) {
        rebaseSizeMap(Integer.parseInt(paramString.substring("BASE_SIZE ".length())));
      } else if (paramString == "W3C_LENGTH_UNITS_ENABLE") {
        this.w3cLengthUnits = true;
      } else if (paramString == "W3C_LENGTH_UNITS_DISABLE") {
        this.w3cLengthUnits = false;
      } else {
        CssParser cssParser = new CssParser();
        try {
          cssParser.parse(getBase(), new StringReader(paramString), false, false);
        } catch (IOException iOException) {}
      }  
  }
  
  public AttributeSet getDeclaration(String paramString) {
    if (paramString == null)
      return SimpleAttributeSet.EMPTY; 
    CssParser cssParser = new CssParser();
    return cssParser.parseDeclaration(paramString);
  }
  
  public void loadRules(Reader paramReader, URL paramURL) throws IOException {
    CssParser cssParser = new CssParser();
    cssParser.parse(paramURL, paramReader, false, false);
  }
  
  public AttributeSet getViewAttributes(View paramView) { return new ViewAttributeSet(paramView); }
  
  public void removeStyle(String paramString) {
    Style style = getStyle(paramString);
    if (style != null) {
      String str = cleanSelectorString(paramString);
      String[] arrayOfString = getSimpleSelectors(str);
      synchronized (this) {
        SelectorMapping selectorMapping1 = getRootSelectorMapping();
        for (int i = arrayOfString.length - 1; i >= 0; i--)
          selectorMapping1 = selectorMapping1.getChildSelectorMapping(arrayOfString[i], true); 
        Style style1 = selectorMapping1.getStyle();
        if (style1 != null) {
          selectorMapping1.setStyle(null);
          if (this.resolvedStyles.size() > 0) {
            Enumeration enumeration = this.resolvedStyles.elements();
            while (enumeration.hasMoreElements()) {
              ResolvedStyle resolvedStyle = (ResolvedStyle)enumeration.nextElement();
              resolvedStyle.removeStyle(style1);
            } 
          } 
        } 
      } 
    } 
    super.removeStyle(paramString);
  }
  
  public void addStyleSheet(StyleSheet paramStyleSheet) {
    synchronized (this) {
      if (this.linkedStyleSheets == null)
        this.linkedStyleSheets = new Vector(); 
      if (!this.linkedStyleSheets.contains(paramStyleSheet)) {
        int i = 0;
        if (paramStyleSheet instanceof javax.swing.plaf.UIResource && this.linkedStyleSheets.size() > 1)
          i = this.linkedStyleSheets.size() - 1; 
        this.linkedStyleSheets.insertElementAt(paramStyleSheet, i);
        linkStyleSheetAt(paramStyleSheet, i);
      } 
    } 
  }
  
  public void removeStyleSheet(StyleSheet paramStyleSheet) {
    synchronized (this) {
      if (this.linkedStyleSheets != null) {
        int i = this.linkedStyleSheets.indexOf(paramStyleSheet);
        if (i != -1) {
          this.linkedStyleSheets.removeElementAt(i);
          unlinkStyleSheet(paramStyleSheet, i);
          if (i == 0 && this.linkedStyleSheets.size() == 0)
            this.linkedStyleSheets = null; 
        } 
      } 
    } 
  }
  
  public StyleSheet[] getStyleSheets() {
    StyleSheet[] arrayOfStyleSheet;
    synchronized (this) {
      if (this.linkedStyleSheets != null) {
        arrayOfStyleSheet = new StyleSheet[this.linkedStyleSheets.size()];
        this.linkedStyleSheets.copyInto(arrayOfStyleSheet);
      } else {
        arrayOfStyleSheet = null;
      } 
    } 
    return arrayOfStyleSheet;
  }
  
  public void importStyleSheet(URL paramURL) {
    try {
      InputStream inputStream = paramURL.openStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      CssParser cssParser = new CssParser();
      cssParser.parse(paramURL, bufferedReader, false, true);
      bufferedReader.close();
      inputStream.close();
    } catch (Throwable throwable) {}
  }
  
  public void setBase(URL paramURL) { this.base = paramURL; }
  
  public URL getBase() { return this.base; }
  
  public void addCSSAttribute(MutableAttributeSet paramMutableAttributeSet, CSS.Attribute paramAttribute, String paramString) { this.css.addInternalCSSValue(paramMutableAttributeSet, paramAttribute, paramString); }
  
  public boolean addCSSAttributeFromHTML(MutableAttributeSet paramMutableAttributeSet, CSS.Attribute paramAttribute, String paramString) {
    Object object = this.css.getCssValue(paramAttribute, paramString);
    if (object != null) {
      paramMutableAttributeSet.addAttribute(paramAttribute, object);
      return true;
    } 
    return false;
  }
  
  public AttributeSet translateHTMLToCSS(AttributeSet paramAttributeSet) {
    AttributeSet attributeSet = this.css.translateHTMLToCSS(paramAttributeSet);
    Style style = addStyle(null, null);
    style.addAttributes(attributeSet);
    return style;
  }
  
  public AttributeSet addAttribute(AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2) {
    if (this.css == null)
      this.css = new CSS(); 
    if (paramObject1 instanceof StyleConstants) {
      HTML.Tag tag = HTML.getTagForStyleConstantsKey((StyleConstants)paramObject1);
      if (tag != null && paramAttributeSet.isDefined(tag))
        paramAttributeSet = removeAttribute(paramAttributeSet, tag); 
      Object object = this.css.styleConstantsValueToCSSValue((StyleConstants)paramObject1, paramObject2);
      if (object != null) {
        CSS.Attribute attribute = this.css.styleConstantsKeyToCSSKey((StyleConstants)paramObject1);
        if (attribute != null)
          return super.addAttribute(paramAttributeSet, attribute, object); 
      } 
    } 
    return super.addAttribute(paramAttributeSet, paramObject1, paramObject2);
  }
  
  public AttributeSet addAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2) {
    if (!(paramAttributeSet2 instanceof HTMLDocument.TaggedAttributeSet))
      paramAttributeSet1 = removeHTMLTags(paramAttributeSet1, paramAttributeSet2); 
    return super.addAttributes(paramAttributeSet1, convertAttributeSet(paramAttributeSet2));
  }
  
  public AttributeSet removeAttribute(AttributeSet paramAttributeSet, Object paramObject) {
    if (paramObject instanceof StyleConstants) {
      HTML.Tag tag = HTML.getTagForStyleConstantsKey((StyleConstants)paramObject);
      if (tag != null)
        paramAttributeSet = super.removeAttribute(paramAttributeSet, tag); 
      CSS.Attribute attribute = this.css.styleConstantsKeyToCSSKey((StyleConstants)paramObject);
      if (attribute != null)
        return super.removeAttribute(paramAttributeSet, attribute); 
    } 
    return super.removeAttribute(paramAttributeSet, paramObject);
  }
  
  public AttributeSet removeAttributes(AttributeSet paramAttributeSet, Enumeration<?> paramEnumeration) { return super.removeAttributes(paramAttributeSet, paramEnumeration); }
  
  public AttributeSet removeAttributes(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2) {
    if (paramAttributeSet1 != paramAttributeSet2)
      paramAttributeSet1 = removeHTMLTags(paramAttributeSet1, paramAttributeSet2); 
    return super.removeAttributes(paramAttributeSet1, convertAttributeSet(paramAttributeSet2));
  }
  
  protected StyleContext.SmallAttributeSet createSmallAttributeSet(AttributeSet paramAttributeSet) { return new SmallConversionSet(paramAttributeSet); }
  
  protected MutableAttributeSet createLargeAttributeSet(AttributeSet paramAttributeSet) { return new LargeConversionSet(paramAttributeSet); }
  
  private AttributeSet removeHTMLTags(AttributeSet paramAttributeSet1, AttributeSet paramAttributeSet2) {
    if (!(paramAttributeSet2 instanceof LargeConversionSet) && !(paramAttributeSet2 instanceof SmallConversionSet)) {
      Enumeration enumeration = paramAttributeSet2.getAttributeNames();
      while (enumeration.hasMoreElements()) {
        Object object = enumeration.nextElement();
        if (object instanceof StyleConstants) {
          HTML.Tag tag = HTML.getTagForStyleConstantsKey((StyleConstants)object);
          if (tag != null && paramAttributeSet1.isDefined(tag))
            paramAttributeSet1 = super.removeAttribute(paramAttributeSet1, tag); 
        } 
      } 
    } 
    return paramAttributeSet1;
  }
  
  AttributeSet convertAttributeSet(AttributeSet paramAttributeSet) {
    if (paramAttributeSet instanceof LargeConversionSet || paramAttributeSet instanceof SmallConversionSet)
      return paramAttributeSet; 
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof StyleConstants) {
        LargeConversionSet largeConversionSet = new LargeConversionSet();
        Enumeration enumeration1 = paramAttributeSet.getAttributeNames();
        while (enumeration1.hasMoreElements()) {
          Object object1 = enumeration1.nextElement();
          Object object2 = null;
          if (object1 instanceof StyleConstants) {
            CSS.Attribute attribute = this.css.styleConstantsKeyToCSSKey((StyleConstants)object1);
            if (attribute != null) {
              Object object3 = paramAttributeSet.getAttribute(object1);
              object2 = this.css.styleConstantsValueToCSSValue((StyleConstants)object1, object3);
              if (object2 != null)
                largeConversionSet.addAttribute(attribute, object2); 
            } 
          } 
          if (object2 == null)
            largeConversionSet.addAttribute(object1, paramAttributeSet.getAttribute(object1)); 
        } 
        return largeConversionSet;
      } 
    } 
    return paramAttributeSet;
  }
  
  public Font getFont(AttributeSet paramAttributeSet) { return this.css.getFont(this, paramAttributeSet, 12, this); }
  
  public Color getForeground(AttributeSet paramAttributeSet) {
    Color color = this.css.getColor(paramAttributeSet, CSS.Attribute.COLOR);
    return (color == null) ? Color.black : color;
  }
  
  public Color getBackground(AttributeSet paramAttributeSet) { return this.css.getColor(paramAttributeSet, CSS.Attribute.BACKGROUND_COLOR); }
  
  public BoxPainter getBoxPainter(AttributeSet paramAttributeSet) { return new BoxPainter(paramAttributeSet, this.css, this); }
  
  public ListPainter getListPainter(AttributeSet paramAttributeSet) { return new ListPainter(paramAttributeSet, this); }
  
  public void setBaseFontSize(int paramInt) { this.css.setBaseFontSize(paramInt); }
  
  public void setBaseFontSize(String paramString) { this.css.setBaseFontSize(paramString); }
  
  public static int getIndexOfSize(float paramFloat) { return CSS.getIndexOfSize(paramFloat, sizeMapDefault); }
  
  public float getPointSize(int paramInt) { return this.css.getPointSize(paramInt, this); }
  
  public float getPointSize(String paramString) { return this.css.getPointSize(paramString, this); }
  
  public Color stringToColor(String paramString) { return CSS.stringToColor(paramString); }
  
  ImageIcon getBackgroundImage(AttributeSet paramAttributeSet) {
    Object object = paramAttributeSet.getAttribute(CSS.Attribute.BACKGROUND_IMAGE);
    return (object != null) ? ((CSS.BackgroundImage)object).getImage(getBase()) : null;
  }
  
  void addRule(String[] paramArrayOfString, AttributeSet paramAttributeSet, boolean paramBoolean) {
    int i = paramArrayOfString.length;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramArrayOfString[0]);
    for (byte b = 1; b < i; b++) {
      stringBuilder.append(' ');
      stringBuilder.append(paramArrayOfString[b]);
    } 
    String str = stringBuilder.toString();
    Style style = getStyle(str);
    if (style == null) {
      Style style1 = addStyle(str, null);
      synchronized (this) {
        SelectorMapping selectorMapping1 = getRootSelectorMapping();
        for (int j = i - 1; j >= 0; j--)
          selectorMapping1 = selectorMapping1.getChildSelectorMapping(paramArrayOfString[j], true); 
        style = selectorMapping1.getStyle();
        if (style == null) {
          style = style1;
          selectorMapping1.setStyle(style);
          refreshResolvedRules(str, paramArrayOfString, style, selectorMapping1.getSpecificity());
        } 
      } 
    } 
    if (paramBoolean)
      style = getLinkedStyle(style); 
    style.addAttributes(paramAttributeSet);
  }
  
  private void linkStyleSheetAt(StyleSheet paramStyleSheet, int paramInt) {
    if (this.resolvedStyles.size() > 0) {
      Enumeration enumeration = this.resolvedStyles.elements();
      while (enumeration.hasMoreElements()) {
        ResolvedStyle resolvedStyle = (ResolvedStyle)enumeration.nextElement();
        resolvedStyle.insertExtendedStyleAt(paramStyleSheet.getRule(resolvedStyle.getName()), paramInt);
      } 
    } 
  }
  
  private void unlinkStyleSheet(StyleSheet paramStyleSheet, int paramInt) {
    if (this.resolvedStyles.size() > 0) {
      Enumeration enumeration = this.resolvedStyles.elements();
      while (enumeration.hasMoreElements()) {
        ResolvedStyle resolvedStyle = (ResolvedStyle)enumeration.nextElement();
        resolvedStyle.removeExtendedStyleAt(paramInt);
      } 
    } 
  }
  
  String[] getSimpleSelectors(String paramString) {
    paramString = cleanSelectorString(paramString);
    SearchBuffer searchBuffer = SearchBuffer.obtainSearchBuffer();
    Vector vector = searchBuffer.getVector();
    int i = 0;
    int j = paramString.length();
    while (i != -1) {
      int k = paramString.indexOf(' ', i);
      if (k != -1) {
        vector.addElement(paramString.substring(i, k));
        if (++k == j) {
          i = -1;
          continue;
        } 
        i = k;
        continue;
      } 
      vector.addElement(paramString.substring(i));
      i = -1;
    } 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    SearchBuffer.releaseSearchBuffer(searchBuffer);
    return arrayOfString;
  }
  
  String cleanSelectorString(String paramString) {
    boolean bool = true;
    byte b = 0;
    int i = paramString.length();
    while (b < i) {
      switch (paramString.charAt(b)) {
        case ' ':
          if (bool)
            return _cleanSelectorString(paramString); 
          bool = true;
          break;
        case '\t':
        case '\n':
        case '\r':
          return _cleanSelectorString(paramString);
        default:
          bool = false;
          break;
      } 
      b++;
    } 
    return bool ? _cleanSelectorString(paramString) : paramString;
  }
  
  private String _cleanSelectorString(String paramString) {
    searchBuffer = SearchBuffer.obtainSearchBuffer();
    StringBuffer stringBuffer = searchBuffer.getStringBuffer();
    boolean bool = true;
    int i = 0;
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length;
    String str = null;
    try {
      for (byte b = 0; b < j; b++) {
        switch (arrayOfChar[b]) {
          case ' ':
            if (!bool) {
              bool = true;
              if (i < b)
                stringBuffer.append(arrayOfChar, i, true + b - i); 
            } 
            i = b + true;
            break;
          case '\t':
          case '\n':
          case '\r':
            if (!bool) {
              bool = true;
              if (i < b) {
                stringBuffer.append(arrayOfChar, i, b - i);
                stringBuffer.append(' ');
              } 
            } 
            i = b + 1;
            break;
          default:
            bool = false;
            break;
        } 
      } 
      if (bool && stringBuffer.length() > 0) {
        stringBuffer.setLength(stringBuffer.length() - 1);
      } else if (i < j) {
        stringBuffer.append(arrayOfChar, i, j - i);
      } 
      str = stringBuffer.toString();
    } finally {
      SearchBuffer.releaseSearchBuffer(searchBuffer);
    } 
    return str;
  }
  
  private SelectorMapping getRootSelectorMapping() { return this.selectorMapping; }
  
  static int getSpecificity(String paramString) {
    byte b1 = 0;
    boolean bool = true;
    byte b2 = 0;
    int i = paramString.length();
    while (b2 < i) {
      switch (paramString.charAt(b2)) {
        case '.':
          b1 += true;
          break;
        case '#':
          b1 += true;
          break;
        case ' ':
          bool = true;
          break;
        default:
          if (bool) {
            bool = false;
            b1++;
          } 
          break;
      } 
      b2++;
    } 
    return b1;
  }
  
  private Style getLinkedStyle(Style paramStyle) {
    Style style = (Style)paramStyle.getResolveParent();
    if (style == null) {
      style = addStyle(null, null);
      paramStyle.setResolveParent(style);
    } 
    return style;
  }
  
  private Style getResolvedStyle(String paramString, Vector paramVector, HTML.Tag paramTag) {
    Style style = (Style)this.resolvedStyles.get(paramString);
    if (style == null)
      style = createResolvedStyle(paramString, paramVector, paramTag); 
    return style;
  }
  
  private Style getResolvedStyle(String paramString) {
    Style style = (Style)this.resolvedStyles.get(paramString);
    if (style == null)
      style = createResolvedStyle(paramString); 
    return style;
  }
  
  private void addSortedStyle(SelectorMapping paramSelectorMapping, Vector<SelectorMapping> paramVector) {
    int i = paramVector.size();
    if (i > 0) {
      int j = paramSelectorMapping.getSpecificity();
      for (byte b = 0; b < i; b++) {
        if (j >= ((SelectorMapping)paramVector.elementAt(b)).getSpecificity()) {
          paramVector.insertElementAt(paramSelectorMapping, b);
          return;
        } 
      } 
    } 
    paramVector.addElement(paramSelectorMapping);
  }
  
  private void getStyles(SelectorMapping paramSelectorMapping, Vector<SelectorMapping> paramVector, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, int paramInt1, int paramInt2, Hashtable<SelectorMapping, SelectorMapping> paramHashtable) {
    if (paramHashtable.contains(paramSelectorMapping))
      return; 
    paramHashtable.put(paramSelectorMapping, paramSelectorMapping);
    Style style = paramSelectorMapping.getStyle();
    if (style != null)
      addSortedStyle(paramSelectorMapping, paramVector); 
    for (int i = paramInt1; i < paramInt2; i++) {
      String str = paramArrayOfString1[i];
      if (str != null) {
        SelectorMapping selectorMapping1 = paramSelectorMapping.getChildSelectorMapping(str, false);
        if (selectorMapping1 != null)
          getStyles(selectorMapping1, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable); 
        if (paramArrayOfString3[i] != null) {
          String str1 = paramArrayOfString3[i];
          selectorMapping1 = paramSelectorMapping.getChildSelectorMapping(str + "." + str1, false);
          if (selectorMapping1 != null)
            getStyles(selectorMapping1, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable); 
          selectorMapping1 = paramSelectorMapping.getChildSelectorMapping("." + str1, false);
          if (selectorMapping1 != null)
            getStyles(selectorMapping1, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable); 
        } 
        if (paramArrayOfString2[i] != null) {
          String str1 = paramArrayOfString2[i];
          selectorMapping1 = paramSelectorMapping.getChildSelectorMapping(str + "#" + str1, false);
          if (selectorMapping1 != null)
            getStyles(selectorMapping1, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable); 
          selectorMapping1 = paramSelectorMapping.getChildSelectorMapping("#" + str1, false);
          if (selectorMapping1 != null)
            getStyles(selectorMapping1, paramVector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, i + 1, paramInt2, paramHashtable); 
        } 
      } 
    } 
  }
  
  private Style createResolvedStyle(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3) {
    searchBuffer = SearchBuffer.obtainSearchBuffer();
    Vector vector = searchBuffer.getVector();
    Hashtable hashtable = searchBuffer.getHashtable();
    try {
      SelectorMapping selectorMapping1 = getRootSelectorMapping();
      int i = paramArrayOfString1.length;
      String str = paramArrayOfString1[0];
      SelectorMapping selectorMapping2 = selectorMapping1.getChildSelectorMapping(str, false);
      if (selectorMapping2 != null)
        getStyles(selectorMapping2, vector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, hashtable); 
      if (paramArrayOfString3[false] != null) {
        String str1 = paramArrayOfString3[0];
        selectorMapping2 = selectorMapping1.getChildSelectorMapping(str + "." + str1, false);
        if (selectorMapping2 != null)
          getStyles(selectorMapping2, vector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, hashtable); 
        selectorMapping2 = selectorMapping1.getChildSelectorMapping("." + str1, false);
        if (selectorMapping2 != null)
          getStyles(selectorMapping2, vector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, hashtable); 
      } 
      if (paramArrayOfString2[false] != null) {
        String str1 = paramArrayOfString2[0];
        selectorMapping2 = selectorMapping1.getChildSelectorMapping(str + "#" + str1, false);
        if (selectorMapping2 != null)
          getStyles(selectorMapping2, vector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, hashtable); 
        selectorMapping2 = selectorMapping1.getChildSelectorMapping("#" + str1, false);
        if (selectorMapping2 != null)
          getStyles(selectorMapping2, vector, paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, 1, i, hashtable); 
      } 
      int j = (this.linkedStyleSheets != null) ? this.linkedStyleSheets.size() : 0;
      int k = vector.size();
      AttributeSet[] arrayOfAttributeSet = new AttributeSet[k + j];
      int m;
      for (m = 0; m < k; m++)
        arrayOfAttributeSet[m] = ((SelectorMapping)vector.elementAt(m)).getStyle(); 
      for (m = 0; m < j; m++) {
        Style style = ((StyleSheet)this.linkedStyleSheets.elementAt(m)).getRule(paramString);
        if (style == null) {
          arrayOfAttributeSet[m + k] = SimpleAttributeSet.EMPTY;
        } else {
          arrayOfAttributeSet[m + k] = style;
        } 
      } 
      ResolvedStyle resolvedStyle = new ResolvedStyle(paramString, arrayOfAttributeSet, k);
      this.resolvedStyles.put(paramString, resolvedStyle);
      return resolvedStyle;
    } finally {
      SearchBuffer.releaseSearchBuffer(searchBuffer);
    } 
  }
  
  private Style createResolvedStyle(String paramString, Vector paramVector, HTML.Tag paramTag) {
    int i = paramVector.size();
    String[] arrayOfString1 = new String[i];
    String[] arrayOfString2 = new String[i];
    String[] arrayOfString3 = new String[i];
    for (byte b = 0; b < i; b++) {
      Element element = (Element)paramVector.elementAt(b);
      AttributeSet attributeSet = element.getAttributes();
      if (b == 0 && element.isLeaf()) {
        Object object = attributeSet.getAttribute(paramTag);
        if (object instanceof AttributeSet) {
          attributeSet = (AttributeSet)object;
        } else {
          attributeSet = null;
        } 
      } 
      if (attributeSet != null) {
        HTML.Tag tag = (HTML.Tag)attributeSet.getAttribute(StyleConstants.NameAttribute);
        if (tag != null) {
          arrayOfString1[b] = tag.toString();
        } else {
          arrayOfString1[b] = null;
        } 
        if (attributeSet.isDefined(HTML.Attribute.CLASS)) {
          arrayOfString3[b] = attributeSet.getAttribute(HTML.Attribute.CLASS).toString();
        } else {
          arrayOfString3[b] = null;
        } 
        if (attributeSet.isDefined(HTML.Attribute.ID)) {
          arrayOfString2[b] = attributeSet.getAttribute(HTML.Attribute.ID).toString();
        } else {
          arrayOfString2[b] = null;
        } 
      } else {
        arrayOfString3[b] = null;
        arrayOfString2[b] = null;
        arrayOfString1[b] = null;
      } 
    } 
    arrayOfString1[0] = paramTag.toString();
    return createResolvedStyle(paramString, arrayOfString1, arrayOfString2, arrayOfString3);
  }
  
  private Style createResolvedStyle(String paramString) {
    searchBuffer = SearchBuffer.obtainSearchBuffer();
    Vector vector = searchBuffer.getVector();
    try {
      int i = 0;
      int j = 0;
      int k = 0;
      int m = paramString.length();
      while (k < m) {
        if (i == k)
          i = paramString.indexOf('.', k); 
        if (j == k)
          j = paramString.indexOf('#', k); 
        int i3 = paramString.indexOf(' ', k);
        if (i3 == -1)
          i3 = m; 
        if (i != -1 && j != -1 && i < i3 && j < i3) {
          if (j < i) {
            if (k == j) {
              vector.addElement("");
            } else {
              vector.addElement(paramString.substring(k, j));
            } 
            if (i + 1 < i3) {
              vector.addElement(paramString.substring(i + 1, i3));
            } else {
              vector.addElement(null);
            } 
            if (j + 1 == i) {
              vector.addElement(null);
            } else {
              vector.addElement(paramString.substring(j + 1, i));
            } 
          } else if (j < i3) {
            if (k == i) {
              vector.addElement("");
            } else {
              vector.addElement(paramString.substring(k, i));
            } 
            if (i + 1 < j) {
              vector.addElement(paramString.substring(i + 1, j));
            } else {
              vector.addElement(null);
            } 
            if (j + 1 == i3) {
              vector.addElement(null);
            } else {
              vector.addElement(paramString.substring(j + 1, i3));
            } 
          } 
          i = j = i3 + 1;
        } else if (i != -1 && i < i3) {
          if (i == k) {
            vector.addElement("");
          } else {
            vector.addElement(paramString.substring(k, i));
          } 
          if (i + 1 == i3) {
            vector.addElement(null);
          } else {
            vector.addElement(paramString.substring(i + 1, i3));
          } 
          vector.addElement(null);
          i = i3 + 1;
        } else if (j != -1 && j < i3) {
          if (j == k) {
            vector.addElement("");
          } else {
            vector.addElement(paramString.substring(k, j));
          } 
          vector.addElement(null);
          if (j + 1 == i3) {
            vector.addElement(null);
          } else {
            vector.addElement(paramString.substring(j + 1, i3));
          } 
          j = i3 + 1;
        } else {
          vector.addElement(paramString.substring(k, i3));
          vector.addElement(null);
          vector.addElement(null);
        } 
        k = i3 + 1;
      } 
      int n = vector.size();
      int i1 = n / 3;
      String[] arrayOfString1 = new String[i1];
      String[] arrayOfString2 = new String[i1];
      String[] arrayOfString3 = new String[i1];
      byte b = 0;
      for (int i2 = n - 3; b < i1; i2 -= 3) {
        arrayOfString1[b] = (String)vector.elementAt(i2);
        arrayOfString3[b] = (String)vector.elementAt(i2 + 1);
        arrayOfString2[b] = (String)vector.elementAt(i2 + 2);
        b++;
      } 
      return createResolvedStyle(paramString, arrayOfString1, arrayOfString2, arrayOfString3);
    } finally {
      SearchBuffer.releaseSearchBuffer(searchBuffer);
    } 
  }
  
  private void refreshResolvedRules(String paramString, String[] paramArrayOfString, Style paramStyle, int paramInt) {
    if (this.resolvedStyles.size() > 0) {
      Enumeration enumeration = this.resolvedStyles.elements();
      while (enumeration.hasMoreElements()) {
        ResolvedStyle resolvedStyle = (ResolvedStyle)enumeration.nextElement();
        if (resolvedStyle.matches(paramString))
          resolvedStyle.insertStyle(paramStyle, paramInt); 
      } 
    } 
  }
  
  void rebaseSizeMap(int paramInt) {
    this.sizeMap = new int[sizeMapDefault.length];
    for (byte b = 0; b < sizeMapDefault.length; b++)
      this.sizeMap[b] = Math.max(paramInt * sizeMapDefault[b] / sizeMapDefault[CSS.baseFontSizeIndex], 4); 
  }
  
  int[] getSizeMap() { return this.sizeMap; }
  
  boolean isW3CLengthUnits() { return this.w3cLengthUnits; }
  
  static class BackgroundImagePainter implements Serializable {
    ImageIcon backgroundImage;
    
    float hPosition;
    
    float vPosition;
    
    short flags;
    
    private int paintX;
    
    private int paintY;
    
    private int paintMaxX;
    
    private int paintMaxY;
    
    BackgroundImagePainter(AttributeSet param1AttributeSet, CSS param1CSS, StyleSheet param1StyleSheet) {
      this.backgroundImage = param1StyleSheet.getBackgroundImage(param1AttributeSet);
      CSS.BackgroundPosition backgroundPosition = (CSS.BackgroundPosition)param1AttributeSet.getAttribute(CSS.Attribute.BACKGROUND_POSITION);
      if (backgroundPosition != null) {
        this.hPosition = backgroundPosition.getHorizontalPosition();
        this.vPosition = backgroundPosition.getVerticalPosition();
        if (backgroundPosition.isHorizontalPositionRelativeToSize()) {
          this.flags = (short)(this.flags | 0x4);
        } else if (backgroundPosition.isHorizontalPositionRelativeToSize()) {
          this.hPosition *= param1CSS.getFontSize(param1AttributeSet, 12, param1StyleSheet);
        } 
        if (backgroundPosition.isVerticalPositionRelativeToSize()) {
          this.flags = (short)(this.flags | 0x8);
        } else if (backgroundPosition.isVerticalPositionRelativeToFontSize()) {
          this.vPosition *= param1CSS.getFontSize(param1AttributeSet, 12, param1StyleSheet);
        } 
      } 
      CSS.Value value = (CSS.Value)param1AttributeSet.getAttribute(CSS.Attribute.BACKGROUND_REPEAT);
      if (value == null || value == CSS.Value.BACKGROUND_REPEAT) {
        this.flags = (short)(this.flags | 0x3);
      } else if (value == CSS.Value.BACKGROUND_REPEAT_X) {
        this.flags = (short)(this.flags | true);
      } else if (value == CSS.Value.BACKGROUND_REPEAT_Y) {
        this.flags = (short)(this.flags | 0x2);
      } 
    }
    
    void paint(Graphics param1Graphics, float param1Float1, float param1Float2, float param1Float3, float param1Float4, View param1View) {
      Rectangle rectangle = param1Graphics.getClipRect();
      if (rectangle != null)
        param1Graphics.clipRect((int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4); 
      if ((this.flags & 0x3) == 0) {
        int i = this.backgroundImage.getIconWidth();
        int j = this.backgroundImage.getIconWidth();
        if ((this.flags & 0x4) == 4) {
          this.paintX = (int)(param1Float1 + param1Float3 * this.hPosition - i * this.hPosition);
        } else {
          this.paintX = (int)param1Float1 + (int)this.hPosition;
        } 
        if ((this.flags & 0x8) == 8) {
          this.paintY = (int)(param1Float2 + param1Float4 * this.vPosition - j * this.vPosition);
        } else {
          this.paintY = (int)param1Float2 + (int)this.vPosition;
        } 
        if (rectangle == null || (this.paintX + i > rectangle.x && this.paintY + j > rectangle.y && this.paintX < rectangle.x + rectangle.width && this.paintY < rectangle.y + rectangle.height))
          this.backgroundImage.paintIcon(null, param1Graphics, this.paintX, this.paintY); 
      } else {
        int i = this.backgroundImage.getIconWidth();
        int j = this.backgroundImage.getIconHeight();
        if (i > 0 && j > 0) {
          this.paintX = (int)param1Float1;
          this.paintY = (int)param1Float2;
          this.paintMaxX = (int)(param1Float1 + param1Float3);
          this.paintMaxY = (int)(param1Float2 + param1Float4);
          if (updatePaintCoordinates(rectangle, i, j))
            while (this.paintX < this.paintMaxX) {
              int k;
              for (k = this.paintY; k < this.paintMaxY; k += j)
                this.backgroundImage.paintIcon(null, param1Graphics, this.paintX, k); 
              this.paintX += i;
            }  
        } 
      } 
      if (rectangle != null)
        param1Graphics.setClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height); 
    }
    
    private boolean updatePaintCoordinates(Rectangle param1Rectangle, int param1Int1, int param1Int2) {
      if ((this.flags & 0x3) == 1) {
        this.paintMaxY = this.paintY + 1;
      } else if ((this.flags & 0x3) == 2) {
        this.paintMaxX = this.paintX + 1;
      } 
      if (param1Rectangle != null) {
        if ((this.flags & 0x3) == 1 && (this.paintY + param1Int2 <= param1Rectangle.y || this.paintY > param1Rectangle.y + param1Rectangle.height))
          return false; 
        if ((this.flags & 0x3) == 2 && (this.paintX + param1Int1 <= param1Rectangle.x || this.paintX > param1Rectangle.x + param1Rectangle.width))
          return false; 
        if ((this.flags & true) == 1) {
          if (param1Rectangle.x + param1Rectangle.width < this.paintMaxX)
            if ((param1Rectangle.x + param1Rectangle.width - this.paintX) % param1Int1 == 0) {
              this.paintMaxX = param1Rectangle.x + param1Rectangle.width;
            } else {
              this.paintMaxX = ((param1Rectangle.x + param1Rectangle.width - this.paintX) / param1Int1 + 1) * param1Int1 + this.paintX;
            }  
          if (param1Rectangle.x > this.paintX)
            this.paintX = (param1Rectangle.x - this.paintX) / param1Int1 * param1Int1 + this.paintX; 
        } 
        if ((this.flags & 0x2) == 2) {
          if (param1Rectangle.y + param1Rectangle.height < this.paintMaxY)
            if ((param1Rectangle.y + param1Rectangle.height - this.paintY) % param1Int2 == 0) {
              this.paintMaxY = param1Rectangle.y + param1Rectangle.height;
            } else {
              this.paintMaxY = ((param1Rectangle.y + param1Rectangle.height - this.paintY) / param1Int2 + 1) * param1Int2 + this.paintY;
            }  
          if (param1Rectangle.y > this.paintY)
            this.paintY = (param1Rectangle.y - this.paintY) / param1Int2 * param1Int2 + this.paintY; 
        } 
      } 
      return true;
    }
  }
  
  public static class BoxPainter implements Serializable {
    float topMargin;
    
    float bottomMargin;
    
    float leftMargin;
    
    float rightMargin;
    
    short marginFlags;
    
    Border border;
    
    Insets binsets;
    
    CSS css;
    
    StyleSheet ss;
    
    Color bg;
    
    StyleSheet.BackgroundImagePainter bgPainter;
    
    BoxPainter(AttributeSet param1AttributeSet, CSS param1CSS, StyleSheet param1StyleSheet) {
      this.ss = param1StyleSheet;
      this.css = param1CSS;
      this.border = getBorder(param1AttributeSet);
      this.binsets = this.border.getBorderInsets(null);
      this.topMargin = getLength(CSS.Attribute.MARGIN_TOP, param1AttributeSet);
      this.bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, param1AttributeSet);
      this.leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, param1AttributeSet);
      this.rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, param1AttributeSet);
      this.bg = param1StyleSheet.getBackground(param1AttributeSet);
      if (param1StyleSheet.getBackgroundImage(param1AttributeSet) != null)
        this.bgPainter = new StyleSheet.BackgroundImagePainter(param1AttributeSet, param1CSS, param1StyleSheet); 
    }
    
    Border getBorder(AttributeSet param1AttributeSet) { return new CSSBorder(param1AttributeSet); }
    
    Color getBorderColor(AttributeSet param1AttributeSet) {
      Color color = this.css.getColor(param1AttributeSet, CSS.Attribute.BORDER_COLOR);
      if (color == null) {
        color = this.css.getColor(param1AttributeSet, CSS.Attribute.COLOR);
        if (color == null)
          return Color.black; 
      } 
      return color;
    }
    
    public float getInset(int param1Int, View param1View) {
      AttributeSet attributeSet = param1View.getAttributes();
      null = 0.0F;
      switch (param1Int) {
        case 2:
          null += getOrientationMargin(HorizontalMargin.LEFT, this.leftMargin, attributeSet, isLeftToRight(param1View));
          null += this.binsets.left;
          return getLength(CSS.Attribute.PADDING_LEFT, attributeSet);
        case 4:
          null += getOrientationMargin(HorizontalMargin.RIGHT, this.rightMargin, attributeSet, isLeftToRight(param1View));
          null += this.binsets.right;
          return getLength(CSS.Attribute.PADDING_RIGHT, attributeSet);
        case 1:
          null += this.topMargin;
          null += this.binsets.top;
          return getLength(CSS.Attribute.PADDING_TOP, attributeSet);
        case 3:
          null += this.bottomMargin;
          null += this.binsets.bottom;
          return getLength(CSS.Attribute.PADDING_BOTTOM, attributeSet);
      } 
      throw new IllegalArgumentException("Invalid side: " + param1Int);
    }
    
    public void paint(Graphics param1Graphics, float param1Float1, float param1Float2, float param1Float3, float param1Float4, View param1View) {
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      AttributeSet attributeSet = param1View.getAttributes();
      boolean bool = isLeftToRight(param1View);
      float f5 = getOrientationMargin(HorizontalMargin.LEFT, this.leftMargin, attributeSet, bool);
      float f6 = getOrientationMargin(HorizontalMargin.RIGHT, this.rightMargin, attributeSet, bool);
      if (!(param1View instanceof HTMLEditorKit.HTMLFactory.BodyBlockView)) {
        f1 = f5;
        f2 = this.topMargin;
        f3 = -(f5 + f6);
        f4 = -(this.topMargin + this.bottomMargin);
      } 
      if (this.bg != null) {
        param1Graphics.setColor(this.bg);
        param1Graphics.fillRect((int)(param1Float1 + f1), (int)(param1Float2 + f2), (int)(param1Float3 + f3), (int)(param1Float4 + f4));
      } 
      if (this.bgPainter != null)
        this.bgPainter.paint(param1Graphics, param1Float1 + f1, param1Float2 + f2, param1Float3 + f3, param1Float4 + f4, param1View); 
      param1Float1 += f5;
      param1Float2 += this.topMargin;
      param1Float3 -= f5 + f6;
      param1Float4 -= this.topMargin + this.bottomMargin;
      if (this.border instanceof javax.swing.border.BevelBorder) {
        int i = (int)getLength(CSS.Attribute.BORDER_TOP_WIDTH, attributeSet);
        for (int j = i - 1; j >= 0; j--)
          this.border.paintBorder(null, param1Graphics, (int)param1Float1 + j, (int)param1Float2 + j, (int)param1Float3 - 2 * j, (int)param1Float4 - 2 * j); 
      } else {
        this.border.paintBorder(null, param1Graphics, (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4);
      } 
    }
    
    float getLength(CSS.Attribute param1Attribute, AttributeSet param1AttributeSet) { return this.css.getLength(param1AttributeSet, param1Attribute, this.ss); }
    
    static boolean isLeftToRight(View param1View) {
      boolean bool = true;
      Container container;
      if (isOrientationAware(param1View) && param1View != null && (container = param1View.getContainer()) != null)
        bool = container.getComponentOrientation().isLeftToRight(); 
      return bool;
    }
    
    static boolean isOrientationAware(View param1View) {
      boolean bool = false;
      AttributeSet attributeSet;
      Object object;
      if (param1View != null && (attributeSet = param1View.getElement().getAttributes()) != null && object = attributeSet.getAttribute(StyleConstants.NameAttribute) instanceof HTML.Tag && (object == HTML.Tag.DIR || object == HTML.Tag.MENU || object == HTML.Tag.UL || object == HTML.Tag.OL))
        bool = true; 
      return bool;
    }
    
    float getOrientationMargin(HorizontalMargin param1HorizontalMargin, float param1Float, AttributeSet param1AttributeSet, boolean param1Boolean) { // Byte code:
      //   0: fload_2
      //   1: fstore #5
      //   3: fload_2
      //   4: fstore #6
      //   6: aconst_null
      //   7: astore #7
      //   9: getstatic javax/swing/text/html/StyleSheet$1.$SwitchMap$javax$swing$text$html$StyleSheet$BoxPainter$HorizontalMargin : [I
      //   12: aload_1
      //   13: invokevirtual ordinal : ()I
      //   16: iaload
      //   17: lookupswitch default -> 121, 1 -> 44, 2 -> 84
      //   44: iload #4
      //   46: ifeq -> 60
      //   49: aload_0
      //   50: getstatic javax/swing/text/html/CSS$Attribute.MARGIN_RIGHT_LTR : Ljavax/swing/text/html/CSS$Attribute;
      //   53: aload_3
      //   54: invokevirtual getLength : (Ljavax/swing/text/html/CSS$Attribute;Ljavax/swing/text/AttributeSet;)F
      //   57: goto -> 68
      //   60: aload_0
      //   61: getstatic javax/swing/text/html/CSS$Attribute.MARGIN_RIGHT_RTL : Ljavax/swing/text/html/CSS$Attribute;
      //   64: aload_3
      //   65: invokevirtual getLength : (Ljavax/swing/text/html/CSS$Attribute;Ljavax/swing/text/AttributeSet;)F
      //   68: fstore #6
      //   70: aload_3
      //   71: getstatic javax/swing/text/html/CSS$Attribute.MARGIN_RIGHT : Ljavax/swing/text/html/CSS$Attribute;
      //   74: invokeinterface getAttribute : (Ljava/lang/Object;)Ljava/lang/Object;
      //   79: astore #7
      //   81: goto -> 121
      //   84: iload #4
      //   86: ifeq -> 100
      //   89: aload_0
      //   90: getstatic javax/swing/text/html/CSS$Attribute.MARGIN_LEFT_LTR : Ljavax/swing/text/html/CSS$Attribute;
      //   93: aload_3
      //   94: invokevirtual getLength : (Ljavax/swing/text/html/CSS$Attribute;Ljavax/swing/text/AttributeSet;)F
      //   97: goto -> 108
      //   100: aload_0
      //   101: getstatic javax/swing/text/html/CSS$Attribute.MARGIN_LEFT_RTL : Ljavax/swing/text/html/CSS$Attribute;
      //   104: aload_3
      //   105: invokevirtual getLength : (Ljavax/swing/text/html/CSS$Attribute;Ljavax/swing/text/AttributeSet;)F
      //   108: fstore #6
      //   110: aload_3
      //   111: getstatic javax/swing/text/html/CSS$Attribute.MARGIN_LEFT : Ljavax/swing/text/html/CSS$Attribute;
      //   114: invokeinterface getAttribute : (Ljava/lang/Object;)Ljava/lang/Object;
      //   119: astore #7
      //   121: aload #7
      //   123: ifnonnull -> 138
      //   126: fload #6
      //   128: ldc -2.14748365E9
      //   130: fcmpl
      //   131: ifeq -> 138
      //   134: fload #6
      //   136: fstore #5
      //   138: fload #5
      //   140: freturn }
    
    enum HorizontalMargin {
      LEFT, RIGHT;
    }
  }
  
  class CssParser implements CSSParser.CSSParserCallback {
    Vector<String[]> selectors = new Vector();
    
    Vector<String> selectorTokens = new Vector();
    
    String propertyName;
    
    MutableAttributeSet declaration = new SimpleAttributeSet();
    
    boolean parsingDeclaration;
    
    boolean isLink;
    
    URL base;
    
    CSSParser parser = new CSSParser();
    
    public AttributeSet parseDeclaration(String param1String) {
      try {
        return parseDeclaration(new StringReader(param1String));
      } catch (IOException iOException) {
        return null;
      } 
    }
    
    public AttributeSet parseDeclaration(Reader param1Reader) throws IOException {
      parse(this.base, param1Reader, true, false);
      return this.declaration.copyAttributes();
    }
    
    public void parse(URL param1URL, Reader param1Reader, boolean param1Boolean1, boolean param1Boolean2) throws IOException {
      this.base = param1URL;
      this.isLink = param1Boolean2;
      this.parsingDeclaration = param1Boolean1;
      this.declaration.removeAttributes(this.declaration);
      this.selectorTokens.removeAllElements();
      this.selectors.removeAllElements();
      this.propertyName = null;
      this.parser.parse(param1Reader, this, param1Boolean1);
    }
    
    public void handleImport(String param1String) {
      URL uRL = CSS.getURL(this.base, param1String);
      if (uRL != null)
        StyleSheet.this.importStyleSheet(uRL); 
    }
    
    public void handleSelector(String param1String) {
      if (!param1String.startsWith(".") && !param1String.startsWith("#"))
        param1String = param1String.toLowerCase(); 
      int i = param1String.length();
      if (param1String.endsWith(",")) {
        if (i > 1) {
          param1String = param1String.substring(0, i - 1);
          this.selectorTokens.addElement(param1String);
        } 
        addSelector();
      } else if (i > 0) {
        this.selectorTokens.addElement(param1String);
      } 
    }
    
    public void startRule() {
      if (this.selectorTokens.size() > 0)
        addSelector(); 
      this.propertyName = null;
    }
    
    public void handleProperty(String param1String) { this.propertyName = param1String; }
    
    public void handleValue(String param1String) {
      if (this.propertyName != null && param1String != null && param1String.length() > 0) {
        CSS.Attribute attribute = CSS.getAttribute(this.propertyName);
        if (attribute != null) {
          if (attribute == CSS.Attribute.LIST_STYLE_IMAGE && param1String != null && !param1String.equals("none")) {
            URL uRL = CSS.getURL(this.base, param1String);
            if (uRL != null)
              param1String = uRL.toString(); 
          } 
          StyleSheet.this.addCSSAttribute(this.declaration, attribute, param1String);
        } 
        this.propertyName = null;
      } 
    }
    
    public void endRule() {
      int i = this.selectors.size();
      for (byte b = 0; b < i; b++) {
        String[] arrayOfString = (String[])this.selectors.elementAt(b);
        if (arrayOfString.length > 0)
          StyleSheet.this.addRule(arrayOfString, this.declaration, this.isLink); 
      } 
      this.declaration.removeAttributes(this.declaration);
      this.selectors.removeAllElements();
    }
    
    private void addSelector() {
      String[] arrayOfString = new String[this.selectorTokens.size()];
      this.selectorTokens.copyInto(arrayOfString);
      this.selectors.addElement(arrayOfString);
      this.selectorTokens.removeAllElements();
    }
  }
  
  class LargeConversionSet extends SimpleAttributeSet {
    public LargeConversionSet(AttributeSet param1AttributeSet) { super(param1AttributeSet); }
    
    public LargeConversionSet() {}
    
    public boolean isDefined(Object param1Object) {
      if (param1Object instanceof StyleConstants) {
        CSS.Attribute attribute = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)param1Object);
        if (attribute != null)
          return super.isDefined(attribute); 
      } 
      return super.isDefined(param1Object);
    }
    
    public Object getAttribute(Object param1Object) {
      if (param1Object instanceof StyleConstants) {
        CSS.Attribute attribute = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)param1Object);
        if (attribute != null) {
          Object object = super.getAttribute(attribute);
          if (object != null)
            return StyleSheet.this.css.cssValueToStyleConstantsValue((StyleConstants)param1Object, object); 
        } 
      } 
      return super.getAttribute(param1Object);
    }
  }
  
  public static class ListPainter implements Serializable {
    static final char[][] romanChars = { { 'i', 'v' }, { 'x', 'l' }, { 'c', 'd' }, { 'm', '?' } };
    
    private Rectangle paintRect;
    
    private boolean checkedForStart;
    
    private int start;
    
    private CSS.Value type;
    
    URL imageurl;
    
    private StyleSheet ss = null;
    
    Icon img = null;
    
    private int bulletgap = 5;
    
    private boolean isLeftToRight;
    
    ListPainter(AttributeSet param1AttributeSet, StyleSheet param1StyleSheet) {
      this.ss = param1StyleSheet;
      String str = (String)param1AttributeSet.getAttribute(CSS.Attribute.LIST_STYLE_IMAGE);
      this.type = null;
      if (str != null && !str.equals("none")) {
        String str1 = null;
        try {
          StringTokenizer stringTokenizer = new StringTokenizer(str, "()");
          if (stringTokenizer.hasMoreTokens())
            str1 = stringTokenizer.nextToken(); 
          if (stringTokenizer.hasMoreTokens())
            str1 = stringTokenizer.nextToken(); 
          URL uRL = new URL(str1);
          this.img = new ImageIcon(uRL);
        } catch (MalformedURLException malformedURLException) {
          if (str1 != null && param1StyleSheet != null && param1StyleSheet.getBase() != null) {
            try {
              URL uRL = new URL(param1StyleSheet.getBase(), str1);
              this.img = new ImageIcon(uRL);
            } catch (MalformedURLException malformedURLException1) {
              this.img = null;
            } 
          } else {
            this.img = null;
          } 
        } 
      } 
      if (this.img == null)
        this.type = (CSS.Value)param1AttributeSet.getAttribute(CSS.Attribute.LIST_STYLE_TYPE); 
      this.start = 1;
      this.paintRect = new Rectangle();
    }
    
    private CSS.Value getChildType(View param1View) {
      CSS.Value value = (CSS.Value)param1View.getAttributes().getAttribute(CSS.Attribute.LIST_STYLE_TYPE);
      if (value == null)
        if (this.type == null) {
          View view = param1View.getParent();
          HTMLDocument hTMLDocument = (HTMLDocument)view.getDocument();
          if (hTMLDocument.matchNameAttribute(view.getElement().getAttributes(), HTML.Tag.OL)) {
            value = CSS.Value.DECIMAL;
          } else {
            value = CSS.Value.DISC;
          } 
        } else {
          value = this.type;
        }  
      return value;
    }
    
    private void getStart(View param1View) {
      this.checkedForStart = true;
      Element element = param1View.getElement();
      if (element != null) {
        AttributeSet attributeSet = element.getAttributes();
        Object object;
        if (attributeSet != null && attributeSet.isDefined(HTML.Attribute.START) && (object = attributeSet.getAttribute(HTML.Attribute.START)) != null && object instanceof String)
          try {
            this.start = Integer.parseInt((String)object);
          } catch (NumberFormatException numberFormatException) {} 
      } 
    }
    
    private int getRenderIndex(View param1View, int param1Int) {
      if (!this.checkedForStart)
        getStart(param1View); 
      int i = param1Int;
      for (int j = param1Int; j >= 0; j--) {
        AttributeSet attributeSet = param1View.getElement().getElement(j).getAttributes();
        if (attributeSet.getAttribute(StyleConstants.NameAttribute) != HTML.Tag.LI) {
          i--;
        } else if (attributeSet.isDefined(HTML.Attribute.VALUE)) {
          Object object = attributeSet.getAttribute(HTML.Attribute.VALUE);
          if (object != null && object instanceof String)
            try {
              int k = Integer.parseInt((String)object);
              return i - j + k;
            } catch (NumberFormatException numberFormatException) {} 
        } 
      } 
      return i + this.start;
    }
    
    public void paint(Graphics param1Graphics, float param1Float1, float param1Float2, float param1Float3, float param1Float4, View param1View, int param1Int) {
      View view = param1View.getView(param1Int);
      Container container = param1View.getContainer();
      Object object = view.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (!(object instanceof HTML.Tag) || object != HTML.Tag.LI)
        return; 
      this.isLeftToRight = container.getComponentOrientation().isLeftToRight();
      float f = 0.0F;
      if (view.getViewCount() > 0) {
        View view1 = view.getView(0);
        Object object1 = view1.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
        if ((object1 == HTML.Tag.P || object1 == HTML.Tag.IMPLIED) && view1.getViewCount() > 0) {
          this.paintRect.setBounds((int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4);
          Shape shape = view.getChildAllocation(0, this.paintRect);
          if (shape != null && (shape = view1.getView(false).getChildAllocation(false, shape)) != null) {
            Rectangle rectangle = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
            f = view1.getView(0).getAlignment(1);
            param1Float2 = rectangle.y;
            param1Float4 = rectangle.height;
          } 
        } 
      } 
      Color color = container.isEnabled() ? ((this.ss != null) ? this.ss.getForeground(view.getAttributes()) : container.getForeground()) : UIManager.getColor("textInactiveText");
      param1Graphics.setColor(color);
      if (this.img != null) {
        drawIcon(param1Graphics, (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f, container);
        return;
      } 
      CSS.Value value = getChildType(view);
      Font font = ((StyledDocument)view.getDocument()).getFont(view.getAttributes());
      if (font != null)
        param1Graphics.setFont(font); 
      if (value == CSS.Value.SQUARE || value == CSS.Value.CIRCLE || value == CSS.Value.DISC) {
        drawShape(param1Graphics, value, (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f);
      } else if (value == CSS.Value.DECIMAL) {
        drawLetter(param1Graphics, '1', (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f, getRenderIndex(param1View, param1Int));
      } else if (value == CSS.Value.LOWER_ALPHA) {
        drawLetter(param1Graphics, 'a', (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f, getRenderIndex(param1View, param1Int));
      } else if (value == CSS.Value.UPPER_ALPHA) {
        drawLetter(param1Graphics, 'A', (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f, getRenderIndex(param1View, param1Int));
      } else if (value == CSS.Value.LOWER_ROMAN) {
        drawLetter(param1Graphics, 'i', (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f, getRenderIndex(param1View, param1Int));
      } else if (value == CSS.Value.UPPER_ROMAN) {
        drawLetter(param1Graphics, 'I', (int)param1Float1, (int)param1Float2, (int)param1Float3, (int)param1Float4, f, getRenderIndex(param1View, param1Int));
      } 
    }
    
    void drawIcon(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, float param1Float, Component param1Component) {
      int i = this.isLeftToRight ? -(this.img.getIconWidth() + this.bulletgap) : (param1Int3 + this.bulletgap);
      int j = param1Int1 + i;
      int k = Math.max(param1Int2, param1Int2 + (int)(param1Float * param1Int4) - this.img.getIconHeight());
      this.img.paintIcon(param1Component, param1Graphics, j, k);
    }
    
    void drawShape(Graphics param1Graphics, CSS.Value param1Value, int param1Int1, int param1Int2, int param1Int3, int param1Int4, float param1Float) {
      int i = this.isLeftToRight ? -(this.bulletgap + 8) : (param1Int3 + this.bulletgap);
      int j = param1Int1 + i;
      int k = Math.max(param1Int2, param1Int2 + (int)(param1Float * param1Int4) - 8);
      if (param1Value == CSS.Value.SQUARE) {
        param1Graphics.drawRect(j, k, 8, 8);
      } else if (param1Value == CSS.Value.CIRCLE) {
        param1Graphics.drawOval(j, k, 8, 8);
      } else {
        param1Graphics.fillOval(j, k, 8, 8);
      } 
    }
    
    void drawLetter(Graphics param1Graphics, char param1Char, int param1Int1, int param1Int2, int param1Int3, int param1Int4, float param1Float, int param1Int5) {
      String str = formatItemNum(param1Int5, param1Char);
      str = this.isLeftToRight ? (str + ".") : ("." + str);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(null, param1Graphics);
      int i = SwingUtilities2.stringWidth(null, fontMetrics, str);
      int j = this.isLeftToRight ? -(i + this.bulletgap) : (param1Int3 + this.bulletgap);
      int k = param1Int1 + j;
      int m = Math.max(param1Int2 + fontMetrics.getAscent(), param1Int2 + (int)(param1Int4 * param1Float));
      SwingUtilities2.drawString(null, param1Graphics, str, k, m);
    }
    
    String formatItemNum(int param1Int, char param1Char) {
      String str2;
      String str1 = "1";
      boolean bool = false;
      switch (param1Char) {
        default:
          str2 = String.valueOf(param1Int);
          break;
        case 'A':
          bool = true;
        case 'a':
          str2 = formatAlphaNumerals(param1Int);
          break;
        case 'I':
          bool = true;
        case 'i':
          str2 = formatRomanNumerals(param1Int);
          break;
      } 
      if (bool)
        str2 = str2.toUpperCase(); 
      return str2;
    }
    
    String formatAlphaNumerals(int param1Int) {
      String str;
      if (param1Int > 26) {
        str = formatAlphaNumerals(param1Int / 26) + formatAlphaNumerals(param1Int % 26);
      } else {
        str = String.valueOf((char)(97 + param1Int - 1));
      } 
      return str;
    }
    
    String formatRomanNumerals(int param1Int) { return formatRomanNumerals(0, param1Int); }
    
    String formatRomanNumerals(int param1Int1, int param1Int2) { return (param1Int2 < 10) ? formatRomanDigit(param1Int1, param1Int2) : (formatRomanNumerals(param1Int1 + 1, param1Int2 / 10) + formatRomanDigit(param1Int1, param1Int2 % 10)); }
    
    String formatRomanDigit(int param1Int1, int param1Int2) {
      String str = "";
      if (param1Int2 == 9) {
        str = str + romanChars[param1Int1][0];
        return str + romanChars[param1Int1 + 1][0];
      } 
      if (param1Int2 == 4) {
        str = str + romanChars[param1Int1][0];
        return str + romanChars[param1Int1][1];
      } 
      if (param1Int2 >= 5) {
        str = str + romanChars[param1Int1][1];
        param1Int2 -= 5;
      } 
      for (byte b = 0; b < param1Int2; b++)
        str = str + romanChars[param1Int1][0]; 
      return str;
    }
  }
  
  static class ResolvedStyle extends MuxingAttributeSet implements Serializable, Style {
    String name;
    
    private int extendedIndex;
    
    ResolvedStyle(String param1String, AttributeSet[] param1ArrayOfAttributeSet, int param1Int) {
      super(param1ArrayOfAttributeSet);
      this.name = param1String;
      this.extendedIndex = param1Int;
    }
    
    void insertStyle(Style param1Style, int param1Int) {
      AttributeSet[] arrayOfAttributeSet = getAttributes();
      int i = arrayOfAttributeSet.length;
      byte b;
      for (b = 0; b < this.extendedIndex && param1Int <= StyleSheet.getSpecificity(((Style)arrayOfAttributeSet[b]).getName()); b++);
      insertAttributeSetAt(param1Style, b);
      this.extendedIndex++;
    }
    
    void removeStyle(Style param1Style) {
      AttributeSet[] arrayOfAttributeSet = getAttributes();
      for (int i = arrayOfAttributeSet.length - 1; i >= 0; i--) {
        if (arrayOfAttributeSet[i] == param1Style) {
          removeAttributeSetAt(i);
          if (i < this.extendedIndex)
            this.extendedIndex--; 
          break;
        } 
      } 
    }
    
    void insertExtendedStyleAt(Style param1Style, int param1Int) { insertAttributeSetAt(param1Style, this.extendedIndex + param1Int); }
    
    void addExtendedStyle(Style param1Style) { insertAttributeSetAt(param1Style, getAttributes().length); }
    
    void removeExtendedStyleAt(int param1Int) { removeAttributeSetAt(this.extendedIndex + param1Int); }
    
    protected boolean matches(String param1String) {
      int i = param1String.length();
      if (i == 0)
        return false; 
      int j = this.name.length();
      int k = param1String.lastIndexOf(' ');
      int m = this.name.lastIndexOf(' ');
      if (k >= 0)
        k++; 
      if (m >= 0)
        m++; 
      if (!matches(param1String, k, i, m, j))
        return false; 
      while (k != -1) {
        i = k - 1;
        k = param1String.lastIndexOf(' ', i - 1);
        if (k >= 0)
          k++; 
        boolean bool;
        for (bool = false; !bool && m != -1; bool = matches(param1String, k, i, m, j)) {
          j = m - 1;
          m = this.name.lastIndexOf(' ', j - 1);
          if (m >= 0)
            m++; 
        } 
        if (!bool)
          return false; 
      } 
      return true;
    }
    
    boolean matches(String param1String, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Int1 = Math.max(param1Int1, 0);
      param1Int3 = Math.max(param1Int3, 0);
      int i = boundedIndexOf(this.name, '.', param1Int3, param1Int4);
      int j = boundedIndexOf(this.name, '#', param1Int3, param1Int4);
      int k = boundedIndexOf(param1String, '.', param1Int1, param1Int2);
      int m = boundedIndexOf(param1String, '#', param1Int1, param1Int2);
      if (k != -1) {
        if (i == -1)
          return false; 
        if (param1Int1 == k) {
          if (param1Int4 - i != param1Int2 - k || !param1String.regionMatches(param1Int1, this.name, i, param1Int4 - i))
            return false; 
        } else if (param1Int2 - param1Int1 != param1Int4 - param1Int3 || !param1String.regionMatches(param1Int1, this.name, param1Int3, param1Int4 - param1Int3)) {
          return false;
        } 
        return true;
      } 
      if (m != -1) {
        if (j == -1)
          return false; 
        if (param1Int1 == m) {
          if (param1Int4 - j != param1Int2 - m || !param1String.regionMatches(param1Int1, this.name, j, param1Int4 - j))
            return false; 
        } else if (param1Int2 - param1Int1 != param1Int4 - param1Int3 || !param1String.regionMatches(param1Int1, this.name, param1Int3, param1Int4 - param1Int3)) {
          return false;
        } 
        return true;
      } 
      return (i != -1) ? ((i - param1Int3 == param1Int2 - param1Int1 && param1String.regionMatches(param1Int1, this.name, param1Int3, i - param1Int3))) : ((j != -1) ? ((j - param1Int3 == param1Int2 - param1Int1 && param1String.regionMatches(param1Int1, this.name, param1Int3, j - param1Int3))) : ((param1Int4 - param1Int3 == param1Int2 - param1Int1 && param1String.regionMatches(param1Int1, this.name, param1Int3, param1Int4 - param1Int3))));
    }
    
    int boundedIndexOf(String param1String, char param1Char, int param1Int1, int param1Int2) {
      int i = param1String.indexOf(param1Char, param1Int1);
      return (i >= param1Int2) ? -1 : i;
    }
    
    public void addAttribute(Object param1Object1, Object param1Object2) {}
    
    public void addAttributes(AttributeSet param1AttributeSet) {}
    
    public void removeAttribute(Object param1Object) {}
    
    public void removeAttributes(Enumeration<?> param1Enumeration) {}
    
    public void removeAttributes(AttributeSet param1AttributeSet) {}
    
    public void setResolveParent(AttributeSet param1AttributeSet) {}
    
    public String getName() { return this.name; }
    
    public void addChangeListener(ChangeListener param1ChangeListener) {}
    
    public void removeChangeListener(ChangeListener param1ChangeListener) {}
    
    public ChangeListener[] getChangeListeners() { return new ChangeListener[0]; }
  }
  
  private static class SearchBuffer {
    static Stack<SearchBuffer> searchBuffers = new Stack();
    
    Vector vector = null;
    
    StringBuffer stringBuffer = null;
    
    Hashtable hashtable = null;
    
    static SearchBuffer obtainSearchBuffer() {
      SearchBuffer searchBuffer;
      try {
        if (!searchBuffers.empty()) {
          searchBuffer = (SearchBuffer)searchBuffers.pop();
        } else {
          searchBuffer = new SearchBuffer();
        } 
      } catch (EmptyStackException emptyStackException) {
        searchBuffer = new SearchBuffer();
      } 
      return searchBuffer;
    }
    
    static void releaseSearchBuffer(SearchBuffer param1SearchBuffer) {
      param1SearchBuffer.empty();
      searchBuffers.push(param1SearchBuffer);
    }
    
    StringBuffer getStringBuffer() {
      if (this.stringBuffer == null)
        this.stringBuffer = new StringBuffer(); 
      return this.stringBuffer;
    }
    
    Vector getVector() {
      if (this.vector == null)
        this.vector = new Vector(); 
      return this.vector;
    }
    
    Hashtable getHashtable() {
      if (this.hashtable == null)
        this.hashtable = new Hashtable(); 
      return this.hashtable;
    }
    
    void empty() {
      if (this.stringBuffer != null)
        this.stringBuffer.setLength(0); 
      if (this.vector != null)
        this.vector.removeAllElements(); 
      if (this.hashtable != null)
        this.hashtable.clear(); 
    }
  }
  
  static class SelectorMapping implements Serializable {
    private int specificity;
    
    private Style style;
    
    private HashMap<String, SelectorMapping> children;
    
    public SelectorMapping(int param1Int) { this.specificity = param1Int; }
    
    public int getSpecificity() { return this.specificity; }
    
    public void setStyle(Style param1Style) { this.style = param1Style; }
    
    public Style getStyle() { return this.style; }
    
    public SelectorMapping getChildSelectorMapping(String param1String, boolean param1Boolean) {
      SelectorMapping selectorMapping = null;
      if (this.children != null) {
        selectorMapping = (SelectorMapping)this.children.get(param1String);
      } else if (param1Boolean) {
        this.children = new HashMap(7);
      } 
      if (selectorMapping == null && param1Boolean) {
        int i = getChildSpecificity(param1String);
        selectorMapping = createChildSelectorMapping(i);
        this.children.put(param1String, selectorMapping);
      } 
      return selectorMapping;
    }
    
    protected SelectorMapping createChildSelectorMapping(int param1Int) { return new SelectorMapping(param1Int); }
    
    protected int getChildSpecificity(String param1String) {
      char c = param1String.charAt(0);
      int i = getSpecificity();
      if (c == '.') {
        i += 100;
      } else if (c == '#') {
        i += 10000;
      } else {
        i++;
        if (param1String.indexOf('.') != -1)
          i += 100; 
        if (param1String.indexOf('#') != -1)
          i += 10000; 
      } 
      return i;
    }
  }
  
  class SmallConversionSet extends StyleContext.SmallAttributeSet {
    public SmallConversionSet(AttributeSet param1AttributeSet) { super(StyleSheet.this, param1AttributeSet); }
    
    public boolean isDefined(Object param1Object) {
      if (param1Object instanceof StyleConstants) {
        CSS.Attribute attribute = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)param1Object);
        if (attribute != null)
          return super.isDefined(attribute); 
      } 
      return super.isDefined(param1Object);
    }
    
    public Object getAttribute(Object param1Object) {
      if (param1Object instanceof StyleConstants) {
        CSS.Attribute attribute = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)param1Object);
        if (attribute != null) {
          Object object = super.getAttribute(attribute);
          if (object != null)
            return StyleSheet.this.css.cssValueToStyleConstantsValue((StyleConstants)param1Object, object); 
        } 
      } 
      return super.getAttribute(param1Object);
    }
  }
  
  class ViewAttributeSet extends MuxingAttributeSet {
    View host;
    
    ViewAttributeSet(View param1View) {
      this.host = param1View;
      Document document = param1View.getDocument();
      searchBuffer = StyleSheet.SearchBuffer.obtainSearchBuffer();
      Vector vector = searchBuffer.getVector();
      try {
        if (document instanceof HTMLDocument) {
          StyleSheet styleSheet = this$0;
          Element element = param1View.getElement();
          AttributeSet attributeSet1 = element.getAttributes();
          AttributeSet attributeSet2 = styleSheet.translateHTMLToCSS(attributeSet1);
          if (attributeSet2.getAttributeCount() != 0)
            vector.addElement(attributeSet2); 
          if (element.isLeaf()) {
            Enumeration enumeration = attributeSet1.getAttributeNames();
            while (enumeration.hasMoreElements()) {
              Object object = enumeration.nextElement();
              if (object instanceof HTML.Tag) {
                if (object == HTML.Tag.A) {
                  Object object1 = attributeSet1.getAttribute(object);
                  if (object1 != null && object1 instanceof AttributeSet) {
                    AttributeSet attributeSet = (AttributeSet)object1;
                    if (attributeSet.getAttribute(HTML.Attribute.HREF) == null)
                      continue; 
                  } 
                } 
                Style style = styleSheet.getRule((HTML.Tag)object, element);
                if (style != null)
                  vector.addElement(style); 
              } 
            } 
          } else {
            HTML.Tag tag = (HTML.Tag)attributeSet1.getAttribute(StyleConstants.NameAttribute);
            Style style = styleSheet.getRule(tag, element);
            if (style != null)
              vector.addElement(style); 
          } 
        } 
        AttributeSet[] arrayOfAttributeSet = new AttributeSet[vector.size()];
        vector.copyInto(arrayOfAttributeSet);
        setAttributes(arrayOfAttributeSet);
      } finally {
        StyleSheet.SearchBuffer.releaseSearchBuffer(searchBuffer);
      } 
    }
    
    public boolean isDefined(Object param1Object) {
      if (param1Object instanceof StyleConstants) {
        CSS.Attribute attribute = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)param1Object);
        if (attribute != null)
          param1Object = attribute; 
      } 
      return super.isDefined(param1Object);
    }
    
    public Object getAttribute(Object param1Object) {
      if (param1Object instanceof StyleConstants) {
        CSS.Attribute attribute = StyleSheet.this.css.styleConstantsKeyToCSSKey((StyleConstants)param1Object);
        if (attribute != null) {
          Object object = doGetAttribute(attribute);
          if (object instanceof CSS.CssValue)
            return ((CSS.CssValue)object).toStyleConstants((StyleConstants)param1Object, this.host); 
        } 
      } 
      return doGetAttribute(param1Object);
    }
    
    Object doGetAttribute(Object param1Object) {
      Object object = super.getAttribute(param1Object);
      if (object != null)
        return object; 
      if (param1Object instanceof CSS.Attribute) {
        CSS.Attribute attribute = (CSS.Attribute)param1Object;
        if (attribute.isInherited()) {
          AttributeSet attributeSet = getResolveParent();
          if (attributeSet != null)
            return attributeSet.getAttribute(param1Object); 
        } 
      } 
      return null;
    }
    
    public AttributeSet getResolveParent() {
      if (this.host == null)
        return null; 
      View view = this.host.getParent();
      return (view != null) ? view.getAttributes() : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\StyleSheet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */