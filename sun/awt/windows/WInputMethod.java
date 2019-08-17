package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodHighlight;
import java.awt.im.InputSubset;
import java.awt.im.spi.InputMethodContext;
import java.awt.peer.ComponentPeer;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import sun.awt.im.InputMethodAdapter;

final class WInputMethod extends InputMethodAdapter {
  private InputMethodContext inputContext;
  
  private Component awtFocussedComponent;
  
  private WComponentPeer awtFocussedComponentPeer = null;
  
  private WComponentPeer lastFocussedComponentPeer = null;
  
  private boolean isLastFocussedActiveClient = false;
  
  private boolean isActive;
  
  private int context = createNativeContext();
  
  private boolean open = getOpenStatus(this.context);
  
  private int cmode = getConversionStatus(this.context);
  
  private Locale currentLocale = getNativeLocale();
  
  private boolean statusWindowHidden = false;
  
  public static final byte ATTR_INPUT = 0;
  
  public static final byte ATTR_TARGET_CONVERTED = 1;
  
  public static final byte ATTR_CONVERTED = 2;
  
  public static final byte ATTR_TARGET_NOTCONVERTED = 3;
  
  public static final byte ATTR_INPUT_ERROR = 4;
  
  public static final int IME_CMODE_ALPHANUMERIC = 0;
  
  public static final int IME_CMODE_NATIVE = 1;
  
  public static final int IME_CMODE_KATAKANA = 2;
  
  public static final int IME_CMODE_LANGUAGE = 3;
  
  public static final int IME_CMODE_FULLSHAPE = 8;
  
  public static final int IME_CMODE_HANJACONVERT = 64;
  
  public static final int IME_CMODE_ROMAN = 16;
  
  private static final boolean COMMIT_INPUT = true;
  
  private static final boolean DISCARD_INPUT = false;
  
  private static Map<TextAttribute, Object>[] highlightStyles;
  
  public WInputMethod() {
    if (this.currentLocale == null)
      this.currentLocale = Locale.getDefault(); 
  }
  
  protected void finalize() {
    if (this.context != 0) {
      destroyNativeContext(this.context);
      this.context = 0;
    } 
    super.finalize();
  }
  
  public void setInputMethodContext(InputMethodContext paramInputMethodContext) { this.inputContext = paramInputMethodContext; }
  
  public final void dispose() {}
  
  public Object getControlObject() { return null; }
  
  public boolean setLocale(Locale paramLocale) { return setLocale(paramLocale, false); }
  
  private boolean setLocale(Locale paramLocale, boolean paramBoolean) {
    Locale[] arrayOfLocale = WInputMethodDescriptor.getAvailableLocalesInternal();
    for (byte b = 0; b < arrayOfLocale.length; b++) {
      Locale locale = arrayOfLocale[b];
      if (paramLocale.equals(locale) || (locale.equals(Locale.JAPAN) && paramLocale.equals(Locale.JAPANESE)) || (locale.equals(Locale.KOREA) && paramLocale.equals(Locale.KOREAN))) {
        if (this.isActive)
          setNativeLocale(locale.toLanguageTag(), paramBoolean); 
        this.currentLocale = locale;
        return true;
      } 
    } 
    return false;
  }
  
  public Locale getLocale() {
    if (this.isActive) {
      this.currentLocale = getNativeLocale();
      if (this.currentLocale == null)
        this.currentLocale = Locale.getDefault(); 
    } 
    return this.currentLocale;
  }
  
  public void setCharacterSubsets(Character.Subset[] paramArrayOfSubset) {
    if (paramArrayOfSubset == null) {
      setConversionStatus(this.context, this.cmode);
      setOpenStatus(this.context, this.open);
      return;
    } 
    Character.Subset subset = paramArrayOfSubset[0];
    Locale locale = getNativeLocale();
    if (locale == null)
      return; 
    if (locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
      if (subset == Character.UnicodeBlock.BASIC_LATIN || subset == InputSubset.LATIN_DIGITS) {
        setOpenStatus(this.context, false);
      } else {
        int i;
        if (subset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || subset == InputSubset.KANJI || subset == Character.UnicodeBlock.HIRAGANA) {
          i = 9;
        } else if (subset == Character.UnicodeBlock.KATAKANA) {
          i = 11;
        } else if (subset == InputSubset.HALFWIDTH_KATAKANA) {
          i = 3;
        } else if (subset == InputSubset.FULLWIDTH_LATIN) {
          i = 8;
        } else {
          return;
        } 
        setOpenStatus(this.context, true);
        i |= getConversionStatus(this.context) & 0x10;
        setConversionStatus(this.context, i);
      } 
    } else if (locale.getLanguage().equals(Locale.KOREAN.getLanguage())) {
      if (subset == Character.UnicodeBlock.BASIC_LATIN || subset == InputSubset.LATIN_DIGITS) {
        setOpenStatus(this.context, false);
      } else {
        byte b;
        if (subset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || subset == InputSubset.HANJA || subset == Character.UnicodeBlock.HANGUL_SYLLABLES || subset == Character.UnicodeBlock.HANGUL_JAMO || subset == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
          b = 1;
        } else if (subset == InputSubset.FULLWIDTH_LATIN) {
          b = 8;
        } else {
          return;
        } 
        setOpenStatus(this.context, true);
        setConversionStatus(this.context, b);
      } 
    } else if (locale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
      if (subset == Character.UnicodeBlock.BASIC_LATIN || subset == InputSubset.LATIN_DIGITS) {
        setOpenStatus(this.context, false);
      } else {
        byte b;
        if (subset == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || subset == InputSubset.TRADITIONAL_HANZI || subset == InputSubset.SIMPLIFIED_HANZI) {
          b = 1;
        } else if (subset == InputSubset.FULLWIDTH_LATIN) {
          b = 8;
        } else {
          return;
        } 
        setOpenStatus(this.context, true);
        setConversionStatus(this.context, b);
      } 
    } 
  }
  
  public void dispatchEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ComponentEvent) {
      Component component = ((ComponentEvent)paramAWTEvent).getComponent();
      if (component == this.awtFocussedComponent) {
        if (this.awtFocussedComponentPeer == null || this.awtFocussedComponentPeer.isDisposed())
          this.awtFocussedComponentPeer = getNearestNativePeer(component); 
        if (this.awtFocussedComponentPeer != null)
          handleNativeIMEEvent(this.awtFocussedComponentPeer, paramAWTEvent); 
      } 
    } 
  }
  
  public void activate() {
    boolean bool = haveActiveClient();
    if (this.lastFocussedComponentPeer != this.awtFocussedComponentPeer || this.isLastFocussedActiveClient != bool) {
      if (this.lastFocussedComponentPeer != null)
        disableNativeIME(this.lastFocussedComponentPeer); 
      if (this.awtFocussedComponentPeer != null)
        enableNativeIME(this.awtFocussedComponentPeer, this.context, !bool); 
      this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
      this.isLastFocussedActiveClient = bool;
    } 
    this.isActive = true;
    if (this.currentLocale != null)
      setLocale(this.currentLocale, true); 
    if (this.statusWindowHidden) {
      setStatusWindowVisible(this.awtFocussedComponentPeer, true);
      this.statusWindowHidden = false;
    } 
  }
  
  public void deactivate(boolean paramBoolean) {
    getLocale();
    if (this.awtFocussedComponentPeer != null) {
      this.lastFocussedComponentPeer = this.awtFocussedComponentPeer;
      this.isLastFocussedActiveClient = haveActiveClient();
    } 
    this.isActive = false;
  }
  
  public void disableInputMethod() {
    if (this.lastFocussedComponentPeer != null) {
      disableNativeIME(this.lastFocussedComponentPeer);
      this.lastFocussedComponentPeer = null;
      this.isLastFocussedActiveClient = false;
    } 
  }
  
  public String getNativeInputMethodInfo() { return getNativeIMMDescription(); }
  
  protected void stopListening() { disableInputMethod(); }
  
  protected void setAWTFocussedComponent(Component paramComponent) {
    if (paramComponent == null)
      return; 
    WComponentPeer wComponentPeer = getNearestNativePeer(paramComponent);
    if (this.isActive) {
      if (this.awtFocussedComponentPeer != null)
        disableNativeIME(this.awtFocussedComponentPeer); 
      if (wComponentPeer != null)
        enableNativeIME(wComponentPeer, this.context, !haveActiveClient()); 
    } 
    this.awtFocussedComponent = paramComponent;
    this.awtFocussedComponentPeer = wComponentPeer;
  }
  
  public void hideWindows() {
    if (this.awtFocussedComponentPeer != null) {
      setStatusWindowVisible(this.awtFocussedComponentPeer, false);
      this.statusWindowHidden = true;
    } 
  }
  
  public void removeNotify() {
    endCompositionNative(this.context, false);
    this.awtFocussedComponent = null;
    this.awtFocussedComponentPeer = null;
  }
  
  static Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) {
    byte b;
    int i = paramInputMethodHighlight.getState();
    if (i == 0) {
      b = 0;
    } else if (i == 1) {
      b = 2;
    } else {
      return null;
    } 
    if (paramInputMethodHighlight.isSelected())
      b++; 
    return highlightStyles[b];
  }
  
  protected boolean supportsBelowTheSpot() { return true; }
  
  public void endComposition() { endCompositionNative(this.context, haveActiveClient()); }
  
  public void setCompositionEnabled(boolean paramBoolean) { setOpenStatus(this.context, paramBoolean); }
  
  public boolean isCompositionEnabled() { return getOpenStatus(this.context); }
  
  public void sendInputMethodEvent(int paramInt1, long paramLong, String paramString, int[] paramArrayOfInt1, String[] paramArrayOfString, int[] paramArrayOfInt2, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4) {
    AttributedCharacterIterator attributedCharacterIterator = null;
    if (paramString != null) {
      AttributedString attributedString = new AttributedString(paramString);
      attributedString.addAttribute(AttributedCharacterIterator.Attribute.LANGUAGE, Locale.getDefault(), 0, paramString.length());
      if (paramArrayOfInt1 != null && paramArrayOfString != null && paramArrayOfString.length != 0 && paramArrayOfInt1.length == paramArrayOfString.length + 1 && paramArrayOfInt1[0] == 0 && paramArrayOfInt1[paramArrayOfString.length] <= paramString.length()) {
        for (byte b = 0; b < paramArrayOfInt1.length - 1; b++) {
          attributedString.addAttribute(AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT, new Annotation(null), paramArrayOfInt1[b], paramArrayOfInt1[b + true]);
          attributedString.addAttribute(AttributedCharacterIterator.Attribute.READING, new Annotation(paramArrayOfString[b]), paramArrayOfInt1[b], paramArrayOfInt1[b + true]);
        } 
      } else {
        attributedString.addAttribute(AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT, new Annotation(null), 0, paramString.length());
        attributedString.addAttribute(AttributedCharacterIterator.Attribute.READING, new Annotation(""), 0, paramString.length());
      } 
      if (paramArrayOfInt2 != null && paramArrayOfByte != null && paramArrayOfByte.length != 0 && paramArrayOfInt2.length == paramArrayOfByte.length + 1 && paramArrayOfInt2[0] == 0 && paramArrayOfInt2[paramArrayOfByte.length] == paramString.length()) {
        for (byte b = 0; b < paramArrayOfInt2.length - 1; b++) {
          InputMethodHighlight inputMethodHighlight;
          switch (paramArrayOfByte[b]) {
            case 1:
              inputMethodHighlight = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
              break;
            case 2:
              inputMethodHighlight = InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT;
              break;
            case 3:
              inputMethodHighlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
              break;
            default:
              inputMethodHighlight = InputMethodHighlight.UNSELECTED_RAW_TEXT_HIGHLIGHT;
              break;
          } 
          attributedString.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, inputMethodHighlight, paramArrayOfInt2[b], paramArrayOfInt2[b + true]);
        } 
      } else {
        attributedString.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, InputMethodHighlight.UNSELECTED_CONVERTED_TEXT_HIGHLIGHT, 0, paramString.length());
      } 
      attributedCharacterIterator = attributedString.getIterator();
    } 
    Component component = getClientComponent();
    if (component == null)
      return; 
    InputMethodEvent inputMethodEvent = new InputMethodEvent(component, paramInt1, paramLong, attributedCharacterIterator, paramInt2, TextHitInfo.leading(paramInt3), TextHitInfo.leading(paramInt4));
    WToolkit.postEvent(WToolkit.targetToAppContext(component), inputMethodEvent);
  }
  
  public void inquireCandidatePosition() {
    Component component = getClientComponent();
    if (component == null)
      return; 
    Runnable runnable = new Runnable() {
        public void run() {
          int i = 0;
          int j = 0;
          Component component = WInputMethod.this.getClientComponent();
          if (component != null) {
            if (!component.isShowing())
              return; 
            if (WInputMethod.this.haveActiveClient()) {
              Rectangle rectangle = WInputMethod.this.inputContext.getTextLocation(TextHitInfo.leading(0));
              i = rectangle.x;
              j = rectangle.y + rectangle.height;
            } else {
              Point point = component.getLocationOnScreen();
              Dimension dimension = component.getSize();
              i = point.x;
              j = point.y + dimension.height;
            } 
          } 
          WInputMethod.this.openCandidateWindow(WInputMethod.this.awtFocussedComponentPeer, i, j);
        }
      };
    WToolkit.postEvent(WToolkit.targetToAppContext(component), new InvocationEvent(component, runnable));
  }
  
  private WComponentPeer getNearestNativePeer(Component paramComponent) {
    if (paramComponent == null)
      return null; 
    ComponentPeer componentPeer = paramComponent.getPeer();
    if (componentPeer == null)
      return null; 
    while (componentPeer instanceof java.awt.peer.LightweightPeer) {
      paramComponent = paramComponent.getParent();
      if (paramComponent == null)
        return null; 
      componentPeer = paramComponent.getPeer();
      if (componentPeer == null)
        return null; 
    } 
    return (componentPeer instanceof WComponentPeer) ? (WComponentPeer)componentPeer : null;
  }
  
  private native int createNativeContext();
  
  private native void destroyNativeContext(int paramInt);
  
  private native void enableNativeIME(WComponentPeer paramWComponentPeer, int paramInt, boolean paramBoolean);
  
  private native void disableNativeIME(WComponentPeer paramWComponentPeer);
  
  private native void handleNativeIMEEvent(WComponentPeer paramWComponentPeer, AWTEvent paramAWTEvent);
  
  private native void endCompositionNative(int paramInt, boolean paramBoolean);
  
  private native void setConversionStatus(int paramInt1, int paramInt2);
  
  private native int getConversionStatus(int paramInt);
  
  private native void setOpenStatus(int paramInt, boolean paramBoolean);
  
  private native boolean getOpenStatus(int paramInt);
  
  private native void setStatusWindowVisible(WComponentPeer paramWComponentPeer, boolean paramBoolean);
  
  private native String getNativeIMMDescription();
  
  static native Locale getNativeLocale();
  
  static native boolean setNativeLocale(String paramString, boolean paramBoolean);
  
  private native void openCandidateWindow(WComponentPeer paramWComponentPeer, int paramInt1, int paramInt2);
  
  static  {
    Map[] arrayOfMap = new Map[4];
    HashMap hashMap = new HashMap(1);
    hashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
    arrayOfMap[0] = Collections.unmodifiableMap(hashMap);
    hashMap = new HashMap(1);
    hashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
    arrayOfMap[1] = Collections.unmodifiableMap(hashMap);
    hashMap = new HashMap(1);
    hashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_DOTTED);
    arrayOfMap[2] = Collections.unmodifiableMap(hashMap);
    hashMap = new HashMap(4);
    Color color = new Color(0, 0, 128);
    hashMap.put(TextAttribute.FOREGROUND, color);
    hashMap.put(TextAttribute.BACKGROUND, Color.white);
    hashMap.put(TextAttribute.SWAP_COLORS, TextAttribute.SWAP_COLORS_ON);
    hashMap.put(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
    arrayOfMap[3] = Collections.unmodifiableMap(hashMap);
    highlightStyles = arrayOfMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WInputMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */