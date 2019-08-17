package sun.swing;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.font.FontDesignMetrics;
import sun.font.FontUtilities;
import sun.java2d.SunGraphicsEnvironment;
import sun.print.ProxyPrintGraphics;
import sun.security.util.SecurityConstants;

public class SwingUtilities2 {
  public static final Object LAF_STATE_KEY = new StringBuffer("LookAndFeel State");
  
  public static final Object MENU_SELECTION_MANAGER_LISTENER_KEY = new StringBuffer("MenuSelectionManager listener key");
  
  private static LSBCacheEntry[] fontCache;
  
  private static final int CACHE_SIZE = 6;
  
  private static int nextIndex;
  
  private static LSBCacheEntry searchKey;
  
  private static final int MIN_CHAR_INDEX = 87;
  
  private static final int MAX_CHAR_INDEX = 88;
  
  public static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
  
  public static final Object AA_TEXT_PROPERTY_KEY = new StringBuffer("AATextInfoPropertyKey");
  
  public static final String IMPLIED_CR = "CR";
  
  private static final StringBuilder SKIP_CLICK_COUNT = new StringBuilder("skipClickCount");
  
  public static final Object COMPONENT_UI_PROPERTY_KEY = new StringBuffer("ComponentUIPropertyKey");
  
  public static final StringUIClientPropertyKey BASICMENUITEMUI_MAX_TEXT_OFFSET = new StringUIClientPropertyKey("maxTextOffset");
  
  private static Field inputEvent_CanAccessSystemClipboard_Field = null;
  
  private static final String UntrustedClipboardAccess = "UNTRUSTED_CLIPBOARD_ACCESS_KEY";
  
  private static final int CHAR_BUFFER_SIZE = 100;
  
  private static final Object charsBufferLock = new Object();
  
  private static char[] charsBuffer = new char[100];
  
  private static int syncCharsBuffer(String paramString) {
    int i = paramString.length();
    if (charsBuffer == null || charsBuffer.length < i) {
      charsBuffer = paramString.toCharArray();
    } else {
      paramString.getChars(0, i, charsBuffer, 0);
    } 
    return i;
  }
  
  public static final boolean isComplexLayout(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return FontUtilities.isComplexText(paramArrayOfChar, paramInt1, paramInt2); }
  
  public static AATextInfo drawTextAntialiased(JComponent paramJComponent) { return (paramJComponent != null) ? (AATextInfo)paramJComponent.getClientProperty(AA_TEXT_PROPERTY_KEY) : null; }
  
  public static int getLeftSideBearing(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString) { return (paramString == null || paramString.length() == 0) ? 0 : getLeftSideBearing(paramJComponent, paramFontMetrics, paramString.charAt(0)); }
  
  public static int getLeftSideBearing(JComponent paramJComponent, FontMetrics paramFontMetrics, char paramChar) {
    char c = paramChar;
    if (c < 'X' && c >= 'W') {
      Object object = null;
      FontRenderContext fontRenderContext = getFontRenderContext(paramJComponent, paramFontMetrics);
      Font font = paramFontMetrics.getFont();
      synchronized (SwingUtilities2.class) {
        LSBCacheEntry lSBCacheEntry = null;
        if (searchKey == null) {
          searchKey = new LSBCacheEntry(fontRenderContext, font);
        } else {
          searchKey.reset(fontRenderContext, font);
        } 
        for (LSBCacheEntry lSBCacheEntry1 : fontCache) {
          if (searchKey.equals(lSBCacheEntry1)) {
            lSBCacheEntry = lSBCacheEntry1;
            break;
          } 
        } 
        if (lSBCacheEntry == null) {
          lSBCacheEntry = searchKey;
          fontCache[nextIndex] = searchKey;
          searchKey = null;
          nextIndex = (nextIndex + 1) % 6;
        } 
        return lSBCacheEntry.getLeftSideBearing(paramChar);
      } 
    } 
    return 0;
  }
  
  public static FontMetrics getFontMetrics(JComponent paramJComponent, Graphics paramGraphics) { return getFontMetrics(paramJComponent, paramGraphics, paramGraphics.getFont()); }
  
  public static FontMetrics getFontMetrics(JComponent paramJComponent, Graphics paramGraphics, Font paramFont) { return (paramJComponent != null) ? paramJComponent.getFontMetrics(paramFont) : Toolkit.getDefaultToolkit().getFontMetrics(paramFont); }
  
  public static int stringWidth(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString) {
    if (paramString == null || paramString.equals(""))
      return 0; 
    boolean bool = (paramJComponent != null && paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null);
    if (bool)
      synchronized (charsBufferLock) {
        int i = syncCharsBuffer(paramString);
        bool = isComplexLayout(charsBuffer, 0, i);
      }  
    if (bool) {
      TextLayout textLayout = createTextLayout(paramJComponent, paramString, paramFontMetrics.getFont(), paramFontMetrics.getFontRenderContext());
      return (int)textLayout.getAdvance();
    } 
    return paramFontMetrics.stringWidth(paramString);
  }
  
  public static String clipStringIfNecessary(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, int paramInt) {
    if (paramString == null || paramString.equals(""))
      return ""; 
    int i = stringWidth(paramJComponent, paramFontMetrics, paramString);
    return (i > paramInt) ? clipString(paramJComponent, paramFontMetrics, paramString, paramInt) : paramString;
  }
  
  public static String clipString(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, int paramInt) {
    boolean bool;
    String str = "...";
    paramInt -= stringWidth(paramJComponent, paramFontMetrics, str);
    if (paramInt <= 0)
      return str; 
    synchronized (charsBufferLock) {
      int i = syncCharsBuffer(paramString);
      bool = isComplexLayout(charsBuffer, 0, i);
      if (!bool) {
        int j = 0;
        for (byte b = 0; b < i; b++) {
          j += paramFontMetrics.charWidth(charsBuffer[b]);
          if (j > paramInt) {
            paramString = paramString.substring(0, b);
            break;
          } 
        } 
      } 
    } 
    if (bool) {
      AttributedString attributedString = new AttributedString(paramString);
      if (paramJComponent != null)
        attributedString.addAttribute(TextAttribute.NUMERIC_SHAPING, paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING)); 
      LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(attributedString.getIterator(), BreakIterator.getCharacterInstance(), getFontRenderContext(paramJComponent, paramFontMetrics));
      paramString = paramString.substring(0, lineBreakMeasurer.nextOffset(paramInt));
    } 
    return paramString + str;
  }
  
  public static void drawString(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2) {
    if (paramString == null || paramString.length() <= 0)
      return; 
    if (isPrinting(paramGraphics)) {
      Graphics2D graphics2D = getGraphics2D(paramGraphics);
      if (graphics2D != null) {
        String str = trimTrailingSpaces(paramString);
        if (!str.isEmpty()) {
          float f = (float)graphics2D.getFont().getStringBounds(str, DEFAULT_FRC).getWidth();
          TextLayout textLayout = createTextLayout(paramJComponent, paramString, graphics2D.getFont(), graphics2D.getFontRenderContext());
          textLayout = textLayout.getJustifiedLayout(f);
          Color color = graphics2D.getColor();
          if (color instanceof PrintColorUIResource)
            graphics2D.setColor(((PrintColorUIResource)color).getPrintColor()); 
          textLayout.draw(graphics2D, paramInt1, paramInt2);
          graphics2D.setColor(color);
        } 
        return;
      } 
    } 
    if (paramGraphics instanceof Graphics2D) {
      AATextInfo aATextInfo = drawTextAntialiased(paramJComponent);
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      boolean bool = (paramJComponent != null && paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null);
      if (bool)
        synchronized (charsBufferLock) {
          int i = syncCharsBuffer(paramString);
          bool = isComplexLayout(charsBuffer, 0, i);
        }  
      if (aATextInfo != null) {
        Object object1 = null;
        Object object2 = graphics2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        if (aATextInfo.aaHint != object2) {
          graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aATextInfo.aaHint);
        } else {
          object2 = null;
        } 
        if (aATextInfo.lcdContrastHint != null) {
          object1 = graphics2D.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
          if (aATextInfo.lcdContrastHint.equals(object1)) {
            object1 = null;
          } else {
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, aATextInfo.lcdContrastHint);
          } 
        } 
        if (bool) {
          TextLayout textLayout = createTextLayout(paramJComponent, paramString, graphics2D.getFont(), graphics2D.getFontRenderContext());
          textLayout.draw(graphics2D, paramInt1, paramInt2);
        } else {
          paramGraphics.drawString(paramString, paramInt1, paramInt2);
        } 
        if (object2 != null)
          graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, object2); 
        if (object1 != null)
          graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, object1); 
        return;
      } 
      if (bool) {
        TextLayout textLayout = createTextLayout(paramJComponent, paramString, graphics2D.getFont(), graphics2D.getFontRenderContext());
        textLayout.draw(graphics2D, paramInt1, paramInt2);
        return;
      } 
    } 
    paramGraphics.drawString(paramString, paramInt1, paramInt2);
  }
  
  public static void drawStringUnderlineCharAt(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3) {
    if (paramString == null || paramString.length() <= 0)
      return; 
    drawString(paramJComponent, paramGraphics, paramString, paramInt2, paramInt3);
    int i = paramString.length();
    if (paramInt1 >= 0 && paramInt1 < i) {
      int j = paramInt3;
      byte b = 1;
      int k = 0;
      int m = 0;
      boolean bool1 = isPrinting(paramGraphics);
      boolean bool2 = bool1;
      if (!bool2)
        synchronized (charsBufferLock) {
          syncCharsBuffer(paramString);
          bool2 = isComplexLayout(charsBuffer, 0, i);
        }  
      if (!bool2) {
        FontMetrics fontMetrics = paramGraphics.getFontMetrics();
        k = paramInt2 + stringWidth(paramJComponent, fontMetrics, paramString.substring(0, paramInt1));
        m = fontMetrics.charWidth(paramString.charAt(paramInt1));
      } else {
        Graphics2D graphics2D = getGraphics2D(paramGraphics);
        if (graphics2D != null) {
          TextLayout textLayout = createTextLayout(paramJComponent, paramString, graphics2D.getFont(), graphics2D.getFontRenderContext());
          if (bool1) {
            float f = (float)graphics2D.getFont().getStringBounds(paramString, DEFAULT_FRC).getWidth();
            textLayout = textLayout.getJustifiedLayout(f);
          } 
          TextHitInfo textHitInfo1;
          TextHitInfo textHitInfo2 = (textHitInfo1 = TextHitInfo.leading(paramInt1)).trailing(paramInt1);
          Shape shape = textLayout.getVisualHighlightShape(textHitInfo1, textHitInfo2);
          Rectangle rectangle = shape.getBounds();
          k = paramInt2 + rectangle.x;
          m = rectangle.width;
        } 
      } 
      paramGraphics.fillRect(k, j + 1, m, b);
    } 
  }
  
  public static int loc2IndexFileList(JList paramJList, Point paramPoint) {
    int i = paramJList.locationToIndex(paramPoint);
    if (i != -1) {
      Object object = paramJList.getClientProperty("List.isFileList");
      if (object instanceof Boolean && ((Boolean)object).booleanValue() && !pointIsInActualBounds(paramJList, i, paramPoint))
        i = -1; 
    } 
    return i;
  }
  
  private static boolean pointIsInActualBounds(JList paramJList, int paramInt, Point paramPoint) {
    ListCellRenderer listCellRenderer = paramJList.getCellRenderer();
    ListModel listModel = paramJList.getModel();
    Object object = listModel.getElementAt(paramInt);
    Component component = listCellRenderer.getListCellRendererComponent(paramJList, object, paramInt, false, false);
    Dimension dimension = component.getPreferredSize();
    Rectangle rectangle = paramJList.getCellBounds(paramInt, paramInt);
    if (!component.getComponentOrientation().isLeftToRight())
      rectangle.x += rectangle.width - dimension.width; 
    rectangle.width = dimension.width;
    return rectangle.contains(paramPoint);
  }
  
  public static boolean pointOutsidePrefSize(JTable paramJTable, int paramInt1, int paramInt2, Point paramPoint) {
    if (paramJTable.convertColumnIndexToModel(paramInt2) != 0 || paramInt1 == -1)
      return true; 
    TableCellRenderer tableCellRenderer = paramJTable.getCellRenderer(paramInt1, paramInt2);
    Object object = paramJTable.getValueAt(paramInt1, paramInt2);
    Component component = tableCellRenderer.getTableCellRendererComponent(paramJTable, object, false, false, paramInt1, paramInt2);
    Dimension dimension = component.getPreferredSize();
    Rectangle rectangle = paramJTable.getCellRect(paramInt1, paramInt2, false);
    rectangle.width = dimension.width;
    rectangle.height = dimension.height;
    assert paramPoint.x >= rectangle.x && paramPoint.y >= rectangle.y;
    return (paramPoint.x > rectangle.x + rectangle.width || paramPoint.y > rectangle.y + rectangle.height);
  }
  
  public static void setLeadAnchorWithoutSelection(ListSelectionModel paramListSelectionModel, int paramInt1, int paramInt2) {
    if (paramInt2 == -1)
      paramInt2 = paramInt1; 
    if (paramInt1 == -1) {
      paramListSelectionModel.setAnchorSelectionIndex(-1);
      paramListSelectionModel.setLeadSelectionIndex(-1);
    } else {
      if (paramListSelectionModel.isSelectedIndex(paramInt1)) {
        paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
      } else {
        paramListSelectionModel.removeSelectionInterval(paramInt1, paramInt1);
      } 
      paramListSelectionModel.setAnchorSelectionIndex(paramInt2);
    } 
  }
  
  public static boolean shouldIgnore(MouseEvent paramMouseEvent, JComponent paramJComponent) { return (paramJComponent == null || !paramJComponent.isEnabled() || !SwingUtilities.isLeftMouseButton(paramMouseEvent) || paramMouseEvent.isConsumed()); }
  
  public static void adjustFocus(JComponent paramJComponent) {
    if (!paramJComponent.hasFocus() && paramJComponent.isRequestFocusEnabled())
      paramJComponent.requestFocus(); 
  }
  
  public static int drawChars(JComponent paramJComponent, Graphics paramGraphics, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt2 <= 0)
      return paramInt3; 
    int i = paramInt3 + getFontMetrics(paramJComponent, paramGraphics).charsWidth(paramArrayOfChar, paramInt1, paramInt2);
    if (isPrinting(paramGraphics)) {
      Graphics2D graphics2D = getGraphics2D(paramGraphics);
      if (graphics2D != null) {
        FontRenderContext fontRenderContext1 = graphics2D.getFontRenderContext();
        FontRenderContext fontRenderContext2 = getFontRenderContext(paramJComponent);
        if (fontRenderContext2 != null && !isFontRenderContextPrintCompatible(fontRenderContext1, fontRenderContext2)) {
          String str1 = new String(paramArrayOfChar, paramInt1, paramInt2);
          TextLayout textLayout = new TextLayout(str1, graphics2D.getFont(), fontRenderContext1);
          String str2 = trimTrailingSpaces(str1);
          if (!str2.isEmpty()) {
            float f = (float)graphics2D.getFont().getStringBounds(str2, fontRenderContext2).getWidth();
            textLayout = textLayout.getJustifiedLayout(f);
            Color color = graphics2D.getColor();
            if (color instanceof PrintColorUIResource)
              graphics2D.setColor(((PrintColorUIResource)color).getPrintColor()); 
            textLayout.draw(graphics2D, paramInt3, paramInt4);
            graphics2D.setColor(color);
          } 
          return i;
        } 
      } 
    } 
    AATextInfo aATextInfo = drawTextAntialiased(paramJComponent);
    if (aATextInfo != null && paramGraphics instanceof Graphics2D) {
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      Object object1 = null;
      Object object2 = graphics2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
      if (aATextInfo.aaHint != null && aATextInfo.aaHint != object2) {
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aATextInfo.aaHint);
      } else {
        object2 = null;
      } 
      if (aATextInfo.lcdContrastHint != null) {
        object1 = graphics2D.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
        if (aATextInfo.lcdContrastHint.equals(object1)) {
          object1 = null;
        } else {
          graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, aATextInfo.lcdContrastHint);
        } 
      } 
      paramGraphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
      if (object2 != null)
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, object2); 
      if (object1 != null)
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, object1); 
    } else {
      paramGraphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
    return i;
  }
  
  public static float drawString(JComponent paramJComponent, Graphics paramGraphics, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) {
    float f;
    boolean bool = isPrinting(paramGraphics);
    Color color = paramGraphics.getColor();
    if (bool && color instanceof PrintColorUIResource)
      paramGraphics.setColor(((PrintColorUIResource)color).getPrintColor()); 
    Graphics2D graphics2D = getGraphics2D(paramGraphics);
    if (graphics2D == null) {
      paramGraphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
      f = paramInt1;
    } else {
      TextLayout textLayout;
      FontRenderContext fontRenderContext;
      if (bool) {
        fontRenderContext = getFontRenderContext(paramJComponent);
        if (fontRenderContext.isAntiAliased() || fontRenderContext.usesFractionalMetrics())
          fontRenderContext = new FontRenderContext(fontRenderContext.getTransform(), false, false); 
      } else if ((fontRenderContext = getFRCProperty(paramJComponent)) == null) {
        fontRenderContext = graphics2D.getFontRenderContext();
      } 
      if (bool) {
        FontRenderContext fontRenderContext1 = graphics2D.getFontRenderContext();
        if (!isFontRenderContextPrintCompatible(fontRenderContext, fontRenderContext1)) {
          textLayout = new TextLayout(paramAttributedCharacterIterator, fontRenderContext1);
          AttributedCharacterIterator attributedCharacterIterator = getTrimmedTrailingSpacesIterator(paramAttributedCharacterIterator);
          if (attributedCharacterIterator != null) {
            float f1 = (new TextLayout(attributedCharacterIterator, fontRenderContext)).getAdvance();
            textLayout = textLayout.getJustifiedLayout(f1);
          } 
        } else {
          textLayout = new TextLayout(paramAttributedCharacterIterator, fontRenderContext);
        } 
      } else {
        textLayout = new TextLayout(paramAttributedCharacterIterator, fontRenderContext);
      } 
      textLayout.draw(graphics2D, paramInt1, paramInt2);
      f = textLayout.getAdvance();
    } 
    if (bool)
      paramGraphics.setColor(color); 
    return f;
  }
  
  public static void drawVLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 < paramInt2) {
      int i = paramInt3;
      paramInt3 = paramInt2;
      paramInt2 = i;
    } 
    paramGraphics.fillRect(paramInt1, paramInt2, 1, paramInt3 - paramInt2 + 1);
  }
  
  public static void drawHLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 < paramInt1) {
      int i = paramInt2;
      paramInt2 = paramInt1;
      paramInt1 = i;
    } 
    paramGraphics.fillRect(paramInt1, paramInt3, paramInt2 - paramInt1 + 1, 1);
  }
  
  public static void drawRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 < 0 || paramInt4 < 0)
      return; 
    if (paramInt4 == 0 || paramInt3 == 0) {
      paramGraphics.fillRect(paramInt1, paramInt2, paramInt3 + 1, paramInt4 + 1);
    } else {
      paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, 1);
      paramGraphics.fillRect(paramInt1 + paramInt3, paramInt2, 1, paramInt4);
      paramGraphics.fillRect(paramInt1 + 1, paramInt2 + paramInt4, paramInt3, 1);
      paramGraphics.fillRect(paramInt1, paramInt2 + 1, 1, paramInt4);
    } 
  }
  
  private static TextLayout createTextLayout(JComponent paramJComponent, String paramString, Font paramFont, FontRenderContext paramFontRenderContext) {
    Object object = (paramJComponent == null) ? null : paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING);
    if (object == null)
      return new TextLayout(paramString, paramFont, paramFontRenderContext); 
    HashMap hashMap = new HashMap();
    hashMap.put(TextAttribute.FONT, paramFont);
    hashMap.put(TextAttribute.NUMERIC_SHAPING, object);
    return new TextLayout(paramString, hashMap, paramFontRenderContext);
  }
  
  private static boolean isFontRenderContextPrintCompatible(FontRenderContext paramFontRenderContext1, FontRenderContext paramFontRenderContext2) {
    if (paramFontRenderContext1 == paramFontRenderContext2)
      return true; 
    if (paramFontRenderContext1 == null || paramFontRenderContext2 == null)
      return false; 
    if (paramFontRenderContext1.getFractionalMetricsHint() != paramFontRenderContext2.getFractionalMetricsHint())
      return false; 
    if (!paramFontRenderContext1.isTransformed() && !paramFontRenderContext2.isTransformed())
      return true; 
    double[] arrayOfDouble1 = new double[4];
    double[] arrayOfDouble2 = new double[4];
    paramFontRenderContext1.getTransform().getMatrix(arrayOfDouble1);
    paramFontRenderContext2.getTransform().getMatrix(arrayOfDouble2);
    return (arrayOfDouble1[0] == arrayOfDouble2[0] && arrayOfDouble1[1] == arrayOfDouble2[1] && arrayOfDouble1[2] == arrayOfDouble2[2] && arrayOfDouble1[3] == arrayOfDouble2[3]);
  }
  
  public static Graphics2D getGraphics2D(Graphics paramGraphics) { return (paramGraphics instanceof Graphics2D) ? (Graphics2D)paramGraphics : ((paramGraphics instanceof ProxyPrintGraphics) ? (Graphics2D)((ProxyPrintGraphics)paramGraphics).getGraphics() : null); }
  
  public static FontRenderContext getFontRenderContext(Component paramComponent) {
    assert paramComponent != null;
    return (paramComponent == null) ? DEFAULT_FRC : paramComponent.getFontMetrics(paramComponent.getFont()).getFontRenderContext();
  }
  
  private static FontRenderContext getFontRenderContext(Component paramComponent, FontMetrics paramFontMetrics) {
    assert paramFontMetrics != null || paramComponent != null;
    return (paramFontMetrics != null) ? paramFontMetrics.getFontRenderContext() : getFontRenderContext(paramComponent);
  }
  
  public static FontMetrics getFontMetrics(JComponent paramJComponent, Font paramFont) {
    FontRenderContext fontRenderContext = getFRCProperty(paramJComponent);
    if (fontRenderContext == null)
      fontRenderContext = DEFAULT_FRC; 
    return FontDesignMetrics.getMetrics(paramFont, fontRenderContext);
  }
  
  private static FontRenderContext getFRCProperty(JComponent paramJComponent) {
    if (paramJComponent != null) {
      AATextInfo aATextInfo = (AATextInfo)paramJComponent.getClientProperty(AA_TEXT_PROPERTY_KEY);
      if (aATextInfo != null)
        return aATextInfo.frc; 
    } 
    return null;
  }
  
  static boolean isPrinting(Graphics paramGraphics) { return (paramGraphics instanceof java.awt.print.PrinterGraphics || paramGraphics instanceof java.awt.PrintGraphics); }
  
  private static String trimTrailingSpaces(String paramString) {
    int i;
    for (i = paramString.length() - 1; i >= 0 && Character.isWhitespace(paramString.charAt(i)); i--);
    return paramString.substring(0, i + 1);
  }
  
  private static AttributedCharacterIterator getTrimmedTrailingSpacesIterator(AttributedCharacterIterator paramAttributedCharacterIterator) {
    int i = paramAttributedCharacterIterator.getIndex();
    char c;
    for (c = paramAttributedCharacterIterator.last(); c != Character.MAX_VALUE && Character.isWhitespace(c); c = paramAttributedCharacterIterator.previous());
    if (c != Character.MAX_VALUE) {
      int j = paramAttributedCharacterIterator.getIndex();
      if (j == paramAttributedCharacterIterator.getEndIndex() - 1) {
        paramAttributedCharacterIterator.setIndex(i);
        return paramAttributedCharacterIterator;
      } 
      AttributedString attributedString = new AttributedString(paramAttributedCharacterIterator, paramAttributedCharacterIterator.getBeginIndex(), j + 1);
      return attributedString.getIterator();
    } 
    return null;
  }
  
  public static boolean useSelectedTextColor(Highlighter.Highlight paramHighlight, JTextComponent paramJTextComponent) {
    Highlighter.HighlightPainter highlightPainter = paramHighlight.getPainter();
    String str = highlightPainter.getClass().getName();
    if (str.indexOf("javax.swing.text.DefaultHighlighter") != 0 && str.indexOf("com.sun.java.swing.plaf.windows.WindowsTextUI") != 0)
      return false; 
    try {
      DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = (DefaultHighlighter.DefaultHighlightPainter)highlightPainter;
      if (defaultHighlightPainter.getColor() != null && !defaultHighlightPainter.getColor().equals(paramJTextComponent.getSelectionColor()))
        return false; 
    } catch (ClassCastException classCastException) {
      return false;
    } 
    return true;
  }
  
  public static boolean canAccessSystemClipboard() {
    boolean bool = false;
    if (!GraphicsEnvironment.isHeadless()) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager == null) {
        bool = true;
      } else {
        try {
          securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
          bool = true;
        } catch (SecurityException securityException) {}
        if (bool && !isTrustedContext())
          bool = canCurrentEventAccessSystemClipboard(true); 
      } 
    } 
    return bool;
  }
  
  public static boolean canCurrentEventAccessSystemClipboard() { return (isTrustedContext() || canCurrentEventAccessSystemClipboard(false)); }
  
  public static boolean canEventAccessSystemClipboard(AWTEvent paramAWTEvent) { return (isTrustedContext() || canEventAccessSystemClipboard(paramAWTEvent, false)); }
  
  private static boolean inputEvent_canAccessSystemClipboard(InputEvent paramInputEvent) {
    if (inputEvent_CanAccessSystemClipboard_Field == null)
      inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
              try {
                Field field = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
                field.setAccessible(true);
                return field;
              } catch (SecurityException securityException) {
              
              } catch (NoSuchFieldException noSuchFieldException) {}
              return null;
            }
          }); 
    if (inputEvent_CanAccessSystemClipboard_Field == null)
      return false; 
    boolean bool = false;
    try {
      bool = inputEvent_CanAccessSystemClipboard_Field.getBoolean(paramInputEvent);
    } catch (IllegalAccessException illegalAccessException) {}
    return bool;
  }
  
  private static boolean isAccessClipboardGesture(InputEvent paramInputEvent) {
    boolean bool = false;
    if (paramInputEvent instanceof KeyEvent) {
      KeyEvent keyEvent = (KeyEvent)paramInputEvent;
      int i = keyEvent.getKeyCode();
      int j = keyEvent.getModifiers();
      switch (i) {
        case 67:
        case 86:
        case 88:
          bool = (j == 2);
          break;
        case 155:
          bool = (j == 2 || j == 1);
          break;
        case 65485:
        case 65487:
        case 65489:
          bool = true;
          break;
        case 127:
          bool = (j == 1);
          break;
      } 
    } 
    return bool;
  }
  
  private static boolean canEventAccessSystemClipboard(AWTEvent paramAWTEvent, boolean paramBoolean) { return EventQueue.isDispatchThread() ? ((paramAWTEvent instanceof InputEvent && (!paramBoolean || isAccessClipboardGesture((InputEvent)paramAWTEvent))) ? inputEvent_canAccessSystemClipboard((InputEvent)paramAWTEvent) : 0) : 1; }
  
  public static void checkAccess(int paramInt) {
    if (System.getSecurityManager() != null && !Modifier.isPublic(paramInt))
      throw new SecurityException("Resource is not accessible"); 
  }
  
  private static boolean canCurrentEventAccessSystemClipboard(boolean paramBoolean) {
    AWTEvent aWTEvent = EventQueue.getCurrentEvent();
    return canEventAccessSystemClipboard(aWTEvent, paramBoolean);
  }
  
  private static boolean isTrustedContext() { return (System.getSecurityManager() == null || AppContext.getAppContext().get("UNTRUSTED_CLIPBOARD_ACCESS_KEY") == null); }
  
  public static String displayPropertiesToCSS(Font paramFont, Color paramColor) {
    StringBuffer stringBuffer = new StringBuffer("body {");
    if (paramFont != null) {
      stringBuffer.append(" font-family: ");
      stringBuffer.append(paramFont.getFamily());
      stringBuffer.append(" ; ");
      stringBuffer.append(" font-size: ");
      stringBuffer.append(paramFont.getSize());
      stringBuffer.append("pt ;");
      if (paramFont.isBold())
        stringBuffer.append(" font-weight: 700 ; "); 
      if (paramFont.isItalic())
        stringBuffer.append(" font-style: italic ; "); 
    } 
    if (paramColor != null) {
      stringBuffer.append(" color: #");
      if (paramColor.getRed() < 16)
        stringBuffer.append('0'); 
      stringBuffer.append(Integer.toHexString(paramColor.getRed()));
      if (paramColor.getGreen() < 16)
        stringBuffer.append('0'); 
      stringBuffer.append(Integer.toHexString(paramColor.getGreen()));
      if (paramColor.getBlue() < 16)
        stringBuffer.append('0'); 
      stringBuffer.append(Integer.toHexString(paramColor.getBlue()));
      stringBuffer.append(" ; ");
    } 
    stringBuffer.append(" }");
    return stringBuffer.toString();
  }
  
  public static Object makeIcon(final Class<?> baseClass, final Class<?> rootClass, final String imageFile) { return new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) {
          byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedAction<byte[]>() {
                public byte[] run() {
                  try {
                    InputStream inputStream = null;
                    for (Class clazz = baseClass; clazz != null; clazz = clazz.getSuperclass()) {
                      inputStream = clazz.getResourceAsStream(imageFile);
                      if (inputStream != null || clazz == rootClass)
                        break; 
                    } 
                    if (inputStream == null)
                      return null; 
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    byte[] arrayOfByte = new byte[1024];
                    int i;
                    while ((i = bufferedInputStream.read(arrayOfByte)) > 0)
                      byteArrayOutputStream.write(arrayOfByte, 0, i); 
                    bufferedInputStream.close();
                    byteArrayOutputStream.flush();
                    return byteArrayOutputStream.toByteArray();
                  } catch (IOException iOException) {
                    System.err.println(iOException.toString());
                    return null;
                  } 
                }
              });
          if (arrayOfByte == null)
            return null; 
          if (arrayOfByte.length == 0) {
            System.err.println("warning: " + imageFile + " is zero-length");
            return null;
          } 
          return new ImageIconUIResource(arrayOfByte);
        }
      }; }
  
  public static boolean isLocalDisplay() {
    boolean bool;
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (graphicsEnvironment instanceof SunGraphicsEnvironment) {
      bool = ((SunGraphicsEnvironment)graphicsEnvironment).isDisplayLocal();
    } else {
      bool = true;
    } 
    return bool;
  }
  
  public static int getUIDefaultsInt(Object paramObject) { return getUIDefaultsInt(paramObject, 0); }
  
  public static int getUIDefaultsInt(Object paramObject, Locale paramLocale) { return getUIDefaultsInt(paramObject, paramLocale, 0); }
  
  public static int getUIDefaultsInt(Object paramObject, int paramInt) { return getUIDefaultsInt(paramObject, null, paramInt); }
  
  public static int getUIDefaultsInt(Object paramObject, Locale paramLocale, int paramInt) {
    Object object = UIManager.get(paramObject, paramLocale);
    if (object instanceof Integer)
      return ((Integer)object).intValue(); 
    if (object instanceof String)
      try {
        return Integer.parseInt((String)object);
      } catch (NumberFormatException numberFormatException) {} 
    return paramInt;
  }
  
  public static Component compositeRequestFocus(Component paramComponent) {
    if (paramComponent instanceof Container) {
      Container container1 = (Container)paramComponent;
      if (container1.isFocusCycleRoot()) {
        FocusTraversalPolicy focusTraversalPolicy = container1.getFocusTraversalPolicy();
        Component component = focusTraversalPolicy.getDefaultComponent(container1);
        if (component != null) {
          component.requestFocus();
          return component;
        } 
      } 
      Container container2 = container1.getFocusCycleRootAncestor();
      if (container2 != null) {
        FocusTraversalPolicy focusTraversalPolicy = container2.getFocusTraversalPolicy();
        Component component = focusTraversalPolicy.getComponentAfter(container2, container1);
        if (component != null && SwingUtilities.isDescendingFrom(component, container1)) {
          component.requestFocus();
          return component;
        } 
      } 
    } 
    if (paramComponent.isFocusable()) {
      paramComponent.requestFocus();
      return paramComponent;
    } 
    return null;
  }
  
  public static boolean tabbedPaneChangeFocusTo(Component paramComponent) {
    if (paramComponent != null) {
      if (paramComponent.isFocusTraversable()) {
        compositeRequestFocus(paramComponent);
        return true;
      } 
      if (paramComponent instanceof JComponent && ((JComponent)paramComponent).requestDefaultFocus())
        return true; 
    } 
    return false;
  }
  
  public static <V> Future<V> submit(Callable<V> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    FutureTask futureTask = new FutureTask(paramCallable);
    execute(futureTask);
    return futureTask;
  }
  
  public static <V> Future<V> submit(Runnable paramRunnable, V paramV) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    FutureTask futureTask = new FutureTask(paramRunnable, paramV);
    execute(futureTask);
    return futureTask;
  }
  
  private static void execute(Runnable paramRunnable) { SwingUtilities.invokeLater(paramRunnable); }
  
  public static void setSkipClickCount(Component paramComponent, int paramInt) {
    if (paramComponent instanceof JTextComponent && ((JTextComponent)paramComponent).getCaret() instanceof javax.swing.text.DefaultCaret)
      ((JTextComponent)paramComponent).putClientProperty(SKIP_CLICK_COUNT, Integer.valueOf(paramInt)); 
  }
  
  public static int getAdjustedClickCount(JTextComponent paramJTextComponent, MouseEvent paramMouseEvent) {
    int i = paramMouseEvent.getClickCount();
    if (i == 1) {
      paramJTextComponent.putClientProperty(SKIP_CLICK_COUNT, null);
    } else {
      Integer integer = (Integer)paramJTextComponent.getClientProperty(SKIP_CLICK_COUNT);
      if (integer != null)
        return i - integer.intValue(); 
    } 
    return i;
  }
  
  private static Section liesIn(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    boolean bool;
    int k;
    int j;
    int i;
    if (paramBoolean1) {
      i = paramRectangle.x;
      j = paramPoint.x;
      k = paramRectangle.width;
      bool = paramBoolean2;
    } else {
      i = paramRectangle.y;
      j = paramPoint.y;
      k = paramRectangle.height;
      bool = true;
    } 
    if (paramBoolean3) {
      int n = (k >= 30) ? 10 : (k / 3);
      return (j < i + n) ? (bool ? Section.LEADING : Section.TRAILING) : ((j >= i + k - n) ? (bool ? Section.TRAILING : Section.LEADING) : Section.MIDDLE);
    } 
    int m = i + k / 2;
    return bool ? ((j >= m) ? Section.TRAILING : Section.LEADING) : ((j < m) ? Section.TRAILING : Section.LEADING);
  }
  
  public static Section liesInHorizontal(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean1, boolean paramBoolean2) { return liesIn(paramRectangle, paramPoint, true, paramBoolean1, paramBoolean2); }
  
  public static Section liesInVertical(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean) { return liesIn(paramRectangle, paramPoint, false, false, paramBoolean); }
  
  public static int convertColumnIndexToModel(TableColumnModel paramTableColumnModel, int paramInt) { return (paramInt < 0) ? paramInt : paramTableColumnModel.getColumn(paramInt).getModelIndex(); }
  
  public static int convertColumnIndexToView(TableColumnModel paramTableColumnModel, int paramInt) {
    if (paramInt < 0)
      return paramInt; 
    for (byte b = 0; b < paramTableColumnModel.getColumnCount(); b++) {
      if (paramTableColumnModel.getColumn(b).getModelIndex() == paramInt)
        return b; 
    } 
    return -1;
  }
  
  public static int getSystemMnemonicKeyMask() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return (toolkit instanceof SunToolkit) ? ((SunToolkit)toolkit).getFocusAcceleratorKeyMask() : 8;
  }
  
  public static TreePath getTreePath(TreeModelEvent paramTreeModelEvent, TreeModel paramTreeModel) {
    TreePath treePath = paramTreeModelEvent.getTreePath();
    if (treePath == null && paramTreeModel != null) {
      Object object = paramTreeModel.getRoot();
      if (object != null)
        treePath = new TreePath(object); 
    } 
    return treePath;
  }
  
  static  {
    fontCache = new LSBCacheEntry[6];
  }
  
  public static class AATextInfo {
    Object aaHint;
    
    Integer lcdContrastHint;
    
    FontRenderContext frc;
    
    private static AATextInfo getAATextInfoFromMap(Map param1Map) {
      Object object1 = param1Map.get(RenderingHints.KEY_TEXT_ANTIALIASING);
      Object object2 = param1Map.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
      return (object1 == null || object1 == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || object1 == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) ? null : new AATextInfo(object1, (Integer)object2);
    }
    
    public static AATextInfo getAATextInfo(boolean param1Boolean) {
      SunToolkit.setAAFontSettingsCondition(param1Boolean);
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Object object = toolkit.getDesktopProperty("awt.font.desktophints");
      return (object instanceof Map) ? getAATextInfoFromMap((Map)object) : null;
    }
    
    public AATextInfo(Object param1Object, Integer param1Integer) {
      if (param1Object == null)
        throw new InternalError("null not allowed here"); 
      if (param1Object == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || param1Object == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)
        throw new InternalError("AA must be on"); 
      this.aaHint = param1Object;
      this.lcdContrastHint = param1Integer;
      this.frc = new FontRenderContext(null, param1Object, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    }
  }
  
  private static class LSBCacheEntry {
    private static final byte UNSET = 127;
    
    private static final char[] oneChar = new char[1];
    
    private byte[] lsbCache = new byte[1];
    
    private Font font;
    
    private FontRenderContext frc;
    
    public LSBCacheEntry(FontRenderContext param1FontRenderContext, Font param1Font) { reset(param1FontRenderContext, param1Font); }
    
    public void reset(FontRenderContext param1FontRenderContext, Font param1Font) {
      this.font = param1Font;
      this.frc = param1FontRenderContext;
      for (int i = this.lsbCache.length - 1; i >= 0; i--)
        this.lsbCache[i] = Byte.MAX_VALUE; 
    }
    
    public int getLeftSideBearing(char param1Char) {
      char c = param1Char - 'W';
      assert c >= '\000' && c < '\001';
      byte b = this.lsbCache[c];
      if (b == Byte.MAX_VALUE) {
        oneChar[0] = param1Char;
        GlyphVector glyphVector = this.font.createGlyphVector(this.frc, oneChar);
        b = (byte)(glyphVector.getGlyphPixelBounds(0, this.frc, 0.0F, 0.0F)).x;
        if (b < 0) {
          Object object = this.frc.getAntiAliasingHint();
          if (object == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || object == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR)
            b = (byte)(b + 1); 
        } 
        this.lsbCache[c] = b;
      } 
      return b;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof LSBCacheEntry))
        return false; 
      LSBCacheEntry lSBCacheEntry = (LSBCacheEntry)param1Object;
      return (this.font.equals(lSBCacheEntry.font) && this.frc.equals(lSBCacheEntry.frc));
    }
    
    public int hashCode() {
      int i = 17;
      if (this.font != null)
        i = 37 * i + this.font.hashCode(); 
      if (this.frc != null)
        i = 37 * i + this.frc.hashCode(); 
      return i;
    }
  }
  
  public static interface RepaintListener {
    void repaintPerformed(JComponent param1JComponent, int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
  
  public enum Section {
    LEADING, MIDDLE, TRAILING;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\SwingUtilities2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */