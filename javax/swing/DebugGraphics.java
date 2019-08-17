package javax.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;

public class DebugGraphics extends Graphics {
  Graphics graphics;
  
  Image buffer = null;
  
  int debugOptions;
  
  int graphicsID = graphicsCount++;
  
  int xOffset = this.yOffset = 0;
  
  int yOffset;
  
  private static int graphicsCount = 0;
  
  private static ImageIcon imageLoadingIcon = new ImageIcon();
  
  public static final int LOG_OPTION = 1;
  
  public static final int FLASH_OPTION = 2;
  
  public static final int BUFFERED_OPTION = 4;
  
  public static final int NONE_OPTION = -1;
  
  private static final Class debugGraphicsInfoKey;
  
  public DebugGraphics() {}
  
  public DebugGraphics(Graphics paramGraphics, JComponent paramJComponent) {
    this(paramGraphics);
    setDebugOptions(paramJComponent.shouldDebugGraphics());
  }
  
  public DebugGraphics(Graphics paramGraphics) {
    this();
    this.graphics = paramGraphics;
  }
  
  public Graphics create() {
    DebugGraphics debugGraphics = new DebugGraphics();
    debugGraphics.graphics = this.graphics.create();
    debugGraphics.debugOptions = this.debugOptions;
    debugGraphics.buffer = this.buffer;
    return debugGraphics;
  }
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphics debugGraphics = new DebugGraphics();
    debugGraphics.graphics = this.graphics.create(paramInt1, paramInt2, paramInt3, paramInt4);
    debugGraphics.debugOptions = this.debugOptions;
    debugGraphics.buffer = this.buffer;
    this.xOffset += paramInt1;
    this.yOffset += paramInt2;
    return debugGraphics;
  }
  
  public static void setFlashColor(Color paramColor) { (info()).flashColor = paramColor; }
  
  public static Color flashColor() { return (info()).flashColor; }
  
  public static void setFlashTime(int paramInt) { (info()).flashTime = paramInt; }
  
  public static int flashTime() { return (info()).flashTime; }
  
  public static void setFlashCount(int paramInt) { (info()).flashCount = paramInt; }
  
  public static int flashCount() { return (info()).flashCount; }
  
  public static void setLogStream(PrintStream paramPrintStream) { (info()).stream = paramPrintStream; }
  
  public static PrintStream logStream() { return (info()).stream; }
  
  public void setFont(Font paramFont) {
    if (debugLog())
      info().log(toShortString() + " Setting font: " + paramFont); 
    this.graphics.setFont(paramFont);
  }
  
  public Font getFont() { return this.graphics.getFont(); }
  
  public void setColor(Color paramColor) {
    if (debugLog())
      info().log(toShortString() + " Setting color: " + paramColor); 
    this.graphics.setColor(paramColor);
  }
  
  public Color getColor() { return this.graphics.getColor(); }
  
  public FontMetrics getFontMetrics() { return this.graphics.getFontMetrics(); }
  
  public FontMetrics getFontMetrics(Font paramFont) { return this.graphics.getFontMetrics(paramFont); }
  
  public void translate(int paramInt1, int paramInt2) {
    if (debugLog())
      info().log(toShortString() + " Translating by: " + new Point(paramInt1, paramInt2)); 
    this.xOffset += paramInt1;
    this.yOffset += paramInt2;
    this.graphics.translate(paramInt1, paramInt2);
  }
  
  public void setPaintMode() {
    if (debugLog())
      info().log(toShortString() + " Setting paint mode"); 
    this.graphics.setPaintMode();
  }
  
  public void setXORMode(Color paramColor) {
    if (debugLog())
      info().log(toShortString() + " Setting XOR mode: " + paramColor); 
    this.graphics.setXORMode(paramColor);
  }
  
  public Rectangle getClipBounds() { return this.graphics.getClipBounds(); }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.graphics.clipRect(paramInt1, paramInt2, paramInt3, paramInt4);
    if (debugLog())
      info().log(toShortString() + " Setting clipRect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " New clipRect: " + this.graphics.getClip()); 
  }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.graphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
    if (debugLog())
      info().log(toShortString() + " Setting new clipRect: " + this.graphics.getClip()); 
  }
  
  public Shape getClip() { return this.graphics.getClip(); }
  
  public void setClip(Shape paramShape) {
    this.graphics.setClip(paramShape);
    if (debugLog())
      info().log(toShortString() + " Setting new clipRect: " + this.graphics.getClip()); 
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Filling rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Clearing rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing round rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " arcWidth: " + paramInt5 + " archHeight: " + paramInt6); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Filling round rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " arcWidth: " + paramInt5 + " archHeight: " + paramInt6); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing line: from " + pointToString(paramInt1, paramInt2) + " to " + pointToString(paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing 3D rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " Raised bezel: " + paramBoolean); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Filling 3D rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " Raised bezel: " + paramBoolean); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing oval: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Filling oval: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing arc: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " startAngle: " + paramInt5 + " arcAngle: " + paramInt6); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Filling arc: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " startAngle: " + paramInt5 + " arcAngle: " + paramInt6); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing polyline:  nPoints: " + paramInt + " X's: " + paramArrayOfInt1 + " Y's: " + paramArrayOfInt2); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing polygon:  nPoints: " + paramInt + " X's: " + paramArrayOfInt1 + " Y's: " + paramArrayOfInt2); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Filling polygon:  nPoints: " + paramInt + " X's: " + paramArrayOfInt1 + " Y's: " + paramArrayOfInt2); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing string: \"" + paramString + "\" at: " + new Point(paramInt1, paramInt2)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawString(paramString, paramInt1, paramInt2);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawString(paramString, paramInt1, paramInt2);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawString(paramString, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      info().log(toShortString() + " Drawing text: \"" + paramAttributedCharacterIterator + "\" at: " + new Point(paramInt1, paramInt2)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
  }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    Font font = this.graphics.getFont();
    if (debugLog())
      info().log(toShortString() + " Drawing bytes at: " + new Point(paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    Font font = this.graphics.getFont();
    if (debugLog())
      info().log(toShortString() + " Drawing chars at " + new Point(paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      Color color = getColor();
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      for (byte b = 0; b < i; b++) {
        this.graphics.setColor((b % 2 == 0) ? debugGraphicsInfo.flashColor : color);
        this.graphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
      this.graphics.setColor(color);
    } 
    this.graphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      debugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Point(paramInt1, paramInt2)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      ImageProducer imageProducer = paramImage.getSource();
      FilteredImageSource filteredImageSource = new FilteredImageSource(imageProducer, new DebugGraphicsFilter(debugGraphicsInfo.flashColor));
      Image image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
      DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
      for (byte b = 0; b < i; b++) {
        Image image1 = (b % 2 == 0) ? image : paramImage;
        loadImage(image1);
        this.graphics.drawImage(image1, paramInt1, paramInt2, debugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
    } 
    return this.graphics.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      debugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      ImageProducer imageProducer = paramImage.getSource();
      FilteredImageSource filteredImageSource = new FilteredImageSource(imageProducer, new DebugGraphicsFilter(debugGraphicsInfo.flashColor));
      Image image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
      DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
      for (byte b = 0; b < i; b++) {
        Image image1 = (b % 2 == 0) ? image : paramImage;
        loadImage(image1);
        this.graphics.drawImage(image1, paramInt1, paramInt2, paramInt3, paramInt4, debugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
    } 
    return this.graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      debugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Point(paramInt1, paramInt2) + ", bgcolor: " + paramColor); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      ImageProducer imageProducer = paramImage.getSource();
      FilteredImageSource filteredImageSource = new FilteredImageSource(imageProducer, new DebugGraphicsFilter(debugGraphicsInfo.flashColor));
      Image image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
      DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
      for (byte b = 0; b < i; b++) {
        Image image1 = (b % 2 == 0) ? image : paramImage;
        loadImage(image1);
        this.graphics.drawImage(image1, paramInt1, paramInt2, paramColor, debugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
    } 
    return this.graphics.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      debugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + ", bgcolor: " + paramColor); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      ImageProducer imageProducer = paramImage.getSource();
      FilteredImageSource filteredImageSource = new FilteredImageSource(imageProducer, new DebugGraphicsFilter(debugGraphicsInfo.flashColor));
      Image image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
      DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
      for (byte b = 0; b < i; b++) {
        Image image1 = (b % 2 == 0) ? image : paramImage;
        loadImage(image1);
        this.graphics.drawImage(image1, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, debugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
    } 
    return this.graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      debugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " destination: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " source: " + new Rectangle(paramInt5, paramInt6, paramInt7, paramInt8)); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      ImageProducer imageProducer = paramImage.getSource();
      FilteredImageSource filteredImageSource = new FilteredImageSource(imageProducer, new DebugGraphicsFilter(debugGraphicsInfo.flashColor));
      Image image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
      DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
      for (byte b = 0; b < i; b++) {
        Image image1 = (b % 2 == 0) ? image : paramImage;
        loadImage(image1);
        this.graphics.drawImage(image1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, debugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
    } 
    return this.graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugLog())
      debugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " destination: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " source: " + new Rectangle(paramInt5, paramInt6, paramInt7, paramInt8) + ", bgcolor: " + paramColor); 
    if (isDrawingBuffer()) {
      if (debugBuffered()) {
        Graphics graphics1 = debugGraphics();
        graphics1.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
        graphics1.dispose();
      } 
    } else if (debugFlash()) {
      int i = debugGraphicsInfo.flashCount * 2 - 1;
      ImageProducer imageProducer = paramImage.getSource();
      FilteredImageSource filteredImageSource = new FilteredImageSource(imageProducer, new DebugGraphicsFilter(debugGraphicsInfo.flashColor));
      Image image = Toolkit.getDefaultToolkit().createImage(filteredImageSource);
      DebugGraphicsObserver debugGraphicsObserver = new DebugGraphicsObserver();
      for (byte b = 0; b < i; b++) {
        Image image1 = (b % 2 == 0) ? image : paramImage;
        loadImage(image1);
        this.graphics.drawImage(image1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, debugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(debugGraphicsInfo.flashTime);
      } 
    } 
    return this.graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
  }
  
  static void loadImage(Image paramImage) { imageLoadingIcon.loadImage(paramImage); }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (debugLog())
      info().log(toShortString() + " Copying area from: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " to: " + new Point(paramInt5, paramInt6)); 
    this.graphics.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  final void sleep(int paramInt) {
    try {
      Thread.sleep(paramInt);
    } catch (Exception exception) {}
  }
  
  public void dispose() {
    this.graphics.dispose();
    this.graphics = null;
  }
  
  public boolean isDrawingBuffer() { return (this.buffer != null); }
  
  String toShortString() { return "Graphics" + (isDrawingBuffer() ? "<B>" : "") + "(" + this.graphicsID + "-" + this.debugOptions + ")"; }
  
  String pointToString(int paramInt1, int paramInt2) { return "(" + paramInt1 + ", " + paramInt2 + ")"; }
  
  public void setDebugOptions(int paramInt) {
    if (paramInt != 0)
      if (paramInt == -1) {
        if (this.debugOptions != 0) {
          System.err.println(toShortString() + " Disabling debug");
          this.debugOptions = 0;
        } 
      } else if (this.debugOptions != paramInt) {
        this.debugOptions |= paramInt;
        if (debugLog())
          System.err.println(toShortString() + " Enabling debug"); 
      }  
  }
  
  public int getDebugOptions() { return this.debugOptions; }
  
  static void setDebugOptions(JComponent paramJComponent, int paramInt) { info().setDebugOptions(paramJComponent, paramInt); }
  
  static int getDebugOptions(JComponent paramJComponent) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    return (debugGraphicsInfo == null) ? 0 : debugGraphicsInfo.getDebugOptions(paramJComponent);
  }
  
  static int shouldComponentDebug(JComponent paramJComponent) {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugGraphicsInfo == null)
      return 0; 
    JComponent jComponent = paramJComponent;
    int i = 0;
    while (jComponent != null && jComponent instanceof JComponent) {
      i |= debugGraphicsInfo.getDebugOptions((JComponent)jComponent);
      Container container = jComponent.getParent();
    } 
    return i;
  }
  
  static int debugComponentCount() {
    DebugGraphicsInfo debugGraphicsInfo = info();
    return (debugGraphicsInfo != null && debugGraphicsInfo.componentToDebug != null) ? debugGraphicsInfo.componentToDebug.size() : 0;
  }
  
  boolean debugLog() { return ((this.debugOptions & true) == 1); }
  
  boolean debugFlash() { return ((this.debugOptions & 0x2) == 2); }
  
  boolean debugBuffered() { return ((this.debugOptions & 0x4) == 4); }
  
  private Graphics debugGraphics() {
    DebugGraphicsInfo debugGraphicsInfo = info();
    if (debugGraphicsInfo.debugFrame == null) {
      debugGraphicsInfo.debugFrame = new JFrame();
      debugGraphicsInfo.debugFrame.setSize(500, 500);
    } 
    JFrame jFrame = debugGraphicsInfo.debugFrame;
    jFrame.show();
    DebugGraphics debugGraphics = new DebugGraphics(jFrame.getGraphics());
    debugGraphics.setFont(getFont());
    debugGraphics.setColor(getColor());
    debugGraphics.translate(this.xOffset, this.yOffset);
    debugGraphics.setClip(getClipBounds());
    if (debugFlash())
      debugGraphics.setDebugOptions(2); 
    return debugGraphics;
  }
  
  static DebugGraphicsInfo info() {
    DebugGraphicsInfo debugGraphicsInfo = (DebugGraphicsInfo)SwingUtilities.appContextGet(debugGraphicsInfoKey);
    if (debugGraphicsInfo == null) {
      debugGraphicsInfo = new DebugGraphicsInfo();
      SwingUtilities.appContextPut(debugGraphicsInfoKey, debugGraphicsInfo);
    } 
    return debugGraphicsInfo;
  }
  
  static  {
    JComponent.DEBUG_GRAPHICS_LOADED = true;
    debugGraphicsInfoKey = DebugGraphicsInfo.class;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DebugGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */