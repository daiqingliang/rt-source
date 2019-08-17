package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.peer.FontPeer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import sun.font.AttributeMap;
import sun.font.AttributeValues;
import sun.font.CompositeFont;
import sun.font.CoreMetrics;
import sun.font.CreatedFontTracker;
import sun.font.EAttribute;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontAccess;
import sun.font.FontLineMetrics;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.FontUtilities;
import sun.font.GlyphLayout;
import sun.font.StandardGlyphVector;

public class Font implements Serializable {
  private Hashtable<Object, Object> fRequestedAttributes;
  
  public static final String DIALOG = "Dialog";
  
  public static final String DIALOG_INPUT = "DialogInput";
  
  public static final String SANS_SERIF = "SansSerif";
  
  public static final String SERIF = "Serif";
  
  public static final String MONOSPACED = "Monospaced";
  
  public static final int PLAIN = 0;
  
  public static final int BOLD = 1;
  
  public static final int ITALIC = 2;
  
  public static final int ROMAN_BASELINE = 0;
  
  public static final int CENTER_BASELINE = 1;
  
  public static final int HANGING_BASELINE = 2;
  
  public static final int TRUETYPE_FONT = 0;
  
  public static final int TYPE1_FONT = 1;
  
  protected String name;
  
  protected int style;
  
  protected int size;
  
  protected float pointSize;
  
  private FontPeer peer;
  
  private long pData;
  
  private Font2DHandle font2DHandle;
  
  private AttributeValues values;
  
  private boolean hasLayoutAttributes;
  
  private boolean createdFont = false;
  
  private boolean nonIdentityTx;
  
  private static final AffineTransform identityTx;
  
  private static final long serialVersionUID = -4206021311591459213L;
  
  private static final int RECOGNIZED_MASK;
  
  private static final int PRIMARY_MASK;
  
  private static final int SECONDARY_MASK;
  
  private static final int LAYOUT_MASK;
  
  private static final int EXTRA_MASK;
  
  private static final float[] ssinfo;
  
  int hash;
  
  private int fontSerializedDataVersion = 1;
  
  private SoftReference<FontLineMetrics> flmref;
  
  public static final int LAYOUT_LEFT_TO_RIGHT = 0;
  
  public static final int LAYOUT_RIGHT_TO_LEFT = 1;
  
  public static final int LAYOUT_NO_START_CONTEXT = 2;
  
  public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;
  
  @Deprecated
  public FontPeer getPeer() { return getPeer_NoClientCode(); }
  
  final FontPeer getPeer_NoClientCode() {
    if (this.peer == null) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      this.peer = toolkit.getFontPeer(this.name, this.style);
    } 
    return this.peer;
  }
  
  private AttributeValues getAttributeValues() {
    if (this.values == null) {
      AttributeValues attributeValues = new AttributeValues();
      attributeValues.setFamily(this.name);
      attributeValues.setSize(this.pointSize);
      if ((this.style & true) != 0)
        attributeValues.setWeight(2.0F); 
      if ((this.style & 0x2) != 0)
        attributeValues.setPosture(0.2F); 
      attributeValues.defineAll(PRIMARY_MASK);
      this.values = attributeValues;
    } 
    return this.values;
  }
  
  private Font2D getFont2D() {
    FontManager fontManager = FontManagerFactory.getInstance();
    if (fontManager.usingPerAppContextComposites() && this.font2DHandle != null && this.font2DHandle.font2D instanceof CompositeFont && ((CompositeFont)this.font2DHandle.font2D).isStdComposite())
      return fontManager.findFont2D(this.name, this.style, 2); 
    if (this.font2DHandle == null)
      this.font2DHandle = (fontManager.findFont2D(this.name, this.style, 2)).handle; 
    return this.font2DHandle.font2D;
  }
  
  public Font(String paramString, int paramInt1, int paramInt2) {
    this.name = (paramString != null) ? paramString : "Default";
    this.style = ((paramInt1 & 0xFFFFFFFC) == 0) ? paramInt1 : 0;
    this.size = paramInt2;
    this.pointSize = paramInt2;
  }
  
  private Font(String paramString, int paramInt, float paramFloat) {
    this.name = (paramString != null) ? paramString : "Default";
    this.style = ((paramInt & 0xFFFFFFFC) == 0) ? paramInt : 0;
    this.size = (int)(paramFloat + 0.5D);
    this.pointSize = paramFloat;
  }
  
  private Font(String paramString, int paramInt, float paramFloat, boolean paramBoolean, Font2DHandle paramFont2DHandle) {
    this(paramString, paramInt, paramFloat);
    this.createdFont = paramBoolean;
    if (paramBoolean)
      if (paramFont2DHandle.font2D instanceof CompositeFont && paramFont2DHandle.font2D.getStyle() != paramInt) {
        FontManager fontManager = FontManagerFactory.getInstance();
        this.font2DHandle = fontManager.getNewComposite(null, paramInt, paramFont2DHandle);
      } else {
        this.font2DHandle = paramFont2DHandle;
      }  
  }
  
  private Font(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker) throws FontFormatException {
    this.createdFont = true;
    FontManager fontManager = FontManagerFactory.getInstance();
    this.font2DHandle = (fontManager.createFont2D(paramFile, paramInt, paramBoolean, paramCreatedFontTracker)).handle;
    this.name = this.font2DHandle.font2D.getFontName(Locale.getDefault());
    this.style = 0;
    this.size = 1;
    this.pointSize = 1.0F;
  }
  
  private Font(AttributeValues paramAttributeValues, String paramString, int paramInt, boolean paramBoolean, Font2DHandle paramFont2DHandle) {
    this.createdFont = paramBoolean;
    if (paramBoolean) {
      this.font2DHandle = paramFont2DHandle;
      String str = null;
      if (paramString != null) {
        str = paramAttributeValues.getFamily();
        if (paramString.equals(str))
          str = null; 
      } 
      byte b = 0;
      if (paramInt == -1) {
        b = -1;
      } else {
        if (paramAttributeValues.getWeight() >= 2.0F)
          b = 1; 
        if (paramAttributeValues.getPosture() >= 0.2F)
          b |= 0x2; 
        if (paramInt == b)
          b = -1; 
      } 
      if (paramFont2DHandle.font2D instanceof CompositeFont) {
        if (b != -1 || str != null) {
          FontManager fontManager = FontManagerFactory.getInstance();
          this.font2DHandle = fontManager.getNewComposite(str, b, paramFont2DHandle);
        } 
      } else if (str != null) {
        this.createdFont = false;
        this.font2DHandle = null;
      } 
    } 
    initFromValues(paramAttributeValues);
  }
  
  public Font(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) { initFromValues(AttributeValues.fromMap(paramMap, RECOGNIZED_MASK)); }
  
  protected Font(Font paramFont) {
    if (paramFont.values != null) {
      initFromValues(paramFont.getAttributeValues().clone());
    } else {
      this.name = paramFont.name;
      this.style = paramFont.style;
      this.size = paramFont.size;
      this.pointSize = paramFont.pointSize;
    } 
    this.font2DHandle = paramFont.font2DHandle;
    this.createdFont = paramFont.createdFont;
  }
  
  private void initFromValues(AttributeValues paramAttributeValues) {
    this.values = paramAttributeValues;
    paramAttributeValues.defineAll(PRIMARY_MASK);
    this.name = paramAttributeValues.getFamily();
    this.pointSize = paramAttributeValues.getSize();
    this.size = (int)(paramAttributeValues.getSize() + 0.5D);
    if (paramAttributeValues.getWeight() >= 2.0F)
      this.style |= 0x1; 
    if (paramAttributeValues.getPosture() >= 0.2F)
      this.style |= 0x2; 
    this.nonIdentityTx = paramAttributeValues.anyNonDefault(EXTRA_MASK);
    this.hasLayoutAttributes = paramAttributeValues.anyNonDefault(LAYOUT_MASK);
  }
  
  public static Font getFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    if (paramMap instanceof AttributeMap && ((AttributeMap)paramMap).getValues() != null) {
      AttributeValues attributeValues = ((AttributeMap)paramMap).getValues();
      if (attributeValues.isNonDefault(EAttribute.EFONT)) {
        Font font1 = attributeValues.getFont();
        if (!attributeValues.anyDefined(SECONDARY_MASK))
          return font1; 
        attributeValues = font1.getAttributeValues().clone();
        attributeValues.merge(paramMap, SECONDARY_MASK);
        return new Font(attributeValues, font1.name, font1.style, font1.createdFont, font1.font2DHandle);
      } 
      return new Font(paramMap);
    } 
    Font font = (Font)paramMap.get(TextAttribute.FONT);
    if (font != null) {
      if (paramMap.size() > 1) {
        AttributeValues attributeValues = font.getAttributeValues().clone();
        attributeValues.merge(paramMap, SECONDARY_MASK);
        return new Font(attributeValues, font.name, font.style, font.createdFont, font.font2DHandle);
      } 
      return font;
    } 
    return new Font(paramMap);
  }
  
  private static boolean hasTempPermission() {
    if (System.getSecurityManager() == null)
      return true; 
    File file = null;
    boolean bool = false;
    try {
      file = Files.createTempFile("+~JT", ".tmp", new java.nio.file.attribute.FileAttribute[0]).toFile();
      file.delete();
      file = null;
      bool = true;
    } catch (Throwable throwable) {}
    return bool;
  }
  
  public static Font createFont(int paramInt, InputStream paramInputStream) throws FontFormatException, IOException {
    if (hasTempPermission())
      return createFont0(paramInt, paramInputStream, null); 
    createdFontTracker = CreatedFontTracker.getTracker();
    bool = false;
    try {
      bool = createdFontTracker.acquirePermit();
      if (!bool)
        throw new IOException("Timed out waiting for resources."); 
      return createFont0(paramInt, paramInputStream, createdFontTracker);
    } catch (InterruptedException interruptedException) {
      throw new IOException("Problem reading font data.");
    } finally {
      if (bool)
        createdFontTracker.releasePermit(); 
    } 
  }
  
  private static Font createFont0(int paramInt, InputStream paramInputStream, CreatedFontTracker paramCreatedFontTracker) throws FontFormatException, IOException {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("font format not recognized"); 
    bool = false;
    try {
      file = (File)AccessController.doPrivileged(new PrivilegedExceptionAction<File>() {
            public File run() throws IOException { return Files.createTempFile("+~JF", ".tmp", new java.nio.file.attribute.FileAttribute[0]).toFile(); }
          });
      if (paramCreatedFontTracker != null)
        paramCreatedFontTracker.add(file); 
      i = 0;
      try {
        outputStream = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
              public OutputStream run() throws IOException { return new FileOutputStream(tFile); }
            });
        if (paramCreatedFontTracker != null)
          paramCreatedFontTracker.set(file, outputStream); 
        try {
          byte[] arrayOfByte = new byte[8192];
          while (true) {
            int j = paramInputStream.read(arrayOfByte);
            if (j < 0)
              break; 
            if (paramCreatedFontTracker != null) {
              if (i + j > 33554432)
                throw new IOException("File too big."); 
              if (i + paramCreatedFontTracker.getNumBytes() > 335544320)
                throw new IOException("Total files too big."); 
              i += j;
              paramCreatedFontTracker.addBytes(j);
            } 
            outputStream.write(arrayOfByte, 0, j);
          } 
        } finally {
          outputStream.close();
        } 
        bool = true;
        Font font = new Font(file, paramInt, true, paramCreatedFontTracker);
        return font;
      } finally {
        if (paramCreatedFontTracker != null)
          paramCreatedFontTracker.remove(file); 
        if (!bool) {
          if (paramCreatedFontTracker != null)
            paramCreatedFontTracker.subBytes(i); 
          AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() {
                  tFile.delete();
                  return null;
                }
              });
        } 
      } 
    } catch (Throwable throwable1) {
      if (throwable1 instanceof FontFormatException)
        throw (FontFormatException)throwable1; 
      if (throwable1 instanceof IOException)
        throw (IOException)throwable1; 
      Throwable throwable2 = throwable1.getCause();
      if (throwable2 instanceof FontFormatException)
        throw (FontFormatException)throwable2; 
      throw new IOException("Problem reading font data.");
    } 
  }
  
  public static Font createFont(int paramInt, File paramFile) throws FontFormatException, IOException {
    paramFile = new File(paramFile.getPath());
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("font format not recognized"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      FilePermission filePermission = new FilePermission(paramFile.getPath(), "read");
      securityManager.checkPermission(filePermission);
    } 
    if (!paramFile.canRead())
      throw new IOException("Can't read " + paramFile); 
    return new Font(paramFile, paramInt, false, null);
  }
  
  public AffineTransform getTransform() {
    if (this.nonIdentityTx) {
      AttributeValues attributeValues = getAttributeValues();
      AffineTransform affineTransform = attributeValues.isNonDefault(EAttribute.ETRANSFORM) ? new AffineTransform(attributeValues.getTransform()) : new AffineTransform();
      if (attributeValues.getSuperscript() != 0) {
        int i = attributeValues.getSuperscript();
        double d1 = 0.0D;
        int j = 0;
        boolean bool = (i > 0) ? 1 : 0;
        byte b = bool ? -1 : 1;
        int k = bool ? i : -i;
        while ((k & 0x7) > j) {
          int m = k & 0x7;
          d1 += (b * (ssinfo[m] - ssinfo[j]));
          k >>= 3;
          b = -b;
          j = m;
        } 
        d1 *= this.pointSize;
        double d2 = Math.pow(0.6666666666666666D, j);
        affineTransform.preConcatenate(AffineTransform.getTranslateInstance(0.0D, d1));
        affineTransform.scale(d2, d2);
      } 
      if (attributeValues.isNonDefault(EAttribute.EWIDTH))
        affineTransform.scale(attributeValues.getWidth(), 1.0D); 
      return affineTransform;
    } 
    return new AffineTransform();
  }
  
  public String getFamily() { return getFamily_NoClientCode(); }
  
  final String getFamily_NoClientCode() { return getFamily(Locale.getDefault()); }
  
  public String getFamily(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException("null locale doesn't mean default"); 
    return getFont2D().getFamilyName(paramLocale);
  }
  
  public String getPSName() { return getFont2D().getPostscriptName(); }
  
  public String getName() { return this.name; }
  
  public String getFontName() { return getFontName(Locale.getDefault()); }
  
  public String getFontName(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException("null locale doesn't mean default"); 
    return getFont2D().getFontName(paramLocale);
  }
  
  public int getStyle() { return this.style; }
  
  public int getSize() { return this.size; }
  
  public float getSize2D() { return this.pointSize; }
  
  public boolean isPlain() { return (this.style == 0); }
  
  public boolean isBold() { return ((this.style & true) != 0); }
  
  public boolean isItalic() { return ((this.style & 0x2) != 0); }
  
  public boolean isTransformed() { return this.nonIdentityTx; }
  
  public boolean hasLayoutAttributes() { return this.hasLayoutAttributes; }
  
  public static Font getFont(String paramString) { return getFont(paramString, null); }
  
  public static Font decode(String paramString) {
    String str1 = paramString;
    String str2 = "";
    int i = 12;
    byte b1 = 0;
    if (paramString == null)
      return new Font("Dialog", b1, i); 
    int j = paramString.lastIndexOf('-');
    int k = paramString.lastIndexOf(' ');
    byte b2 = (j > k) ? 45 : 32;
    int m = paramString.lastIndexOf(b2);
    int n = paramString.lastIndexOf(b2, m - 1);
    int i1 = paramString.length();
    if (m > 0 && m + 1 < i1)
      try {
        i = Integer.valueOf(paramString.substring(m + 1)).intValue();
        if (i <= 0)
          i = 12; 
      } catch (NumberFormatException numberFormatException) {
        n = m;
        m = i1;
        if (paramString.charAt(m - 1) == b2)
          m--; 
      }  
    if (n >= 0 && n + 1 < i1) {
      str2 = paramString.substring(n + 1, m);
      str2 = str2.toLowerCase(Locale.ENGLISH);
      if (str2.equals("bolditalic")) {
        b1 = 3;
      } else if (str2.equals("italic")) {
        b1 = 2;
      } else if (str2.equals("bold")) {
        b1 = 1;
      } else if (str2.equals("plain")) {
        b1 = 0;
      } else {
        n = m;
        if (paramString.charAt(n - 1) == b2)
          n--; 
      } 
      str1 = paramString.substring(0, n);
    } else {
      int i2 = i1;
      if (n > 0) {
        i2 = n;
      } else if (m > 0) {
        i2 = m;
      } 
      if (i2 > 0 && paramString.charAt(i2 - 1) == b2)
        i2--; 
      str1 = paramString.substring(0, i2);
    } 
    return new Font(str1, b1, i);
  }
  
  public static Font getFont(String paramString, Font paramFont) {
    String str = null;
    try {
      str = System.getProperty(paramString);
    } catch (SecurityException securityException) {}
    return (str == null) ? paramFont : decode(str);
  }
  
  public int hashCode() {
    if (this.hash == 0) {
      this.hash = this.name.hashCode() ^ this.style ^ this.size;
      if (this.nonIdentityTx && this.values != null && this.values.getTransform() != null)
        this.hash ^= this.values.getTransform().hashCode(); 
    } 
    return this.hash;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject != null)
      try {
        Font font = (Font)paramObject;
        if (this.size == font.size && this.style == font.style && this.nonIdentityTx == font.nonIdentityTx && this.hasLayoutAttributes == font.hasLayoutAttributes && this.pointSize == font.pointSize && this.name.equals(font.name))
          return (this.values == null) ? ((font.values == null) ? true : getAttributeValues().equals(font.values)) : this.values.equals(font.getAttributeValues()); 
      } catch (ClassCastException classCastException) {} 
    return false;
  }
  
  public String toString() {
    String str;
    if (isBold()) {
      str = isItalic() ? "bolditalic" : "bold";
    } else {
      str = isItalic() ? "italic" : "plain";
    } 
    return getClass().getName() + "[family=" + getFamily() + ",name=" + this.name + ",style=" + str + ",size=" + this.size + "]";
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws ClassNotFoundException, IOException {
    if (this.values != null) {
      synchronized (this.values) {
        this.fRequestedAttributes = this.values.toSerializableHashtable();
        paramObjectOutputStream.defaultWriteObject();
        this.fRequestedAttributes = null;
      } 
    } else {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    if (this.pointSize == 0.0F)
      this.pointSize = this.size; 
    if (this.fRequestedAttributes != null) {
      AttributeValues attributeValues;
      if (!(attributeValues = (this.values = getAttributeValues()).fromSerializableHashtable(this.fRequestedAttributes)).is16Hashtable(this.fRequestedAttributes))
        attributeValues.unsetDefault(); 
      this.values = getAttributeValues().merge(attributeValues);
      this.nonIdentityTx = this.values.anyNonDefault(EXTRA_MASK);
      this.hasLayoutAttributes = this.values.anyNonDefault(LAYOUT_MASK);
      this.fRequestedAttributes = null;
    } 
  }
  
  public int getNumGlyphs() { return getFont2D().getNumGlyphs(); }
  
  public int getMissingGlyphCode() { return getFont2D().getMissingGlyphCode(); }
  
  public byte getBaselineFor(char paramChar) { return getFont2D().getBaselineFor(paramChar); }
  
  public Map<TextAttribute, ?> getAttributes() { return new AttributeMap(getAttributeValues()); }
  
  public AttributedCharacterIterator.Attribute[] getAvailableAttributes() { return new AttributedCharacterIterator.Attribute[] { 
        TextAttribute.FAMILY, TextAttribute.WEIGHT, TextAttribute.WIDTH, TextAttribute.POSTURE, TextAttribute.SIZE, TextAttribute.TRANSFORM, TextAttribute.SUPERSCRIPT, TextAttribute.CHAR_REPLACEMENT, TextAttribute.FOREGROUND, TextAttribute.BACKGROUND, 
        TextAttribute.UNDERLINE, TextAttribute.STRIKETHROUGH, TextAttribute.RUN_DIRECTION, TextAttribute.BIDI_EMBEDDING, TextAttribute.JUSTIFICATION, TextAttribute.INPUT_METHOD_HIGHLIGHT, TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.SWAP_COLORS, TextAttribute.NUMERIC_SHAPING, TextAttribute.KERNING, 
        TextAttribute.LIGATURES, TextAttribute.TRACKING }; }
  
  public Font deriveFont(int paramInt, float paramFloat) {
    if (this.values == null)
      return new Font(this.name, paramInt, paramFloat, this.createdFont, this.font2DHandle); 
    AttributeValues attributeValues = getAttributeValues().clone();
    int i = (this.style != paramInt) ? this.style : -1;
    applyStyle(paramInt, attributeValues);
    attributeValues.setSize(paramFloat);
    return new Font(attributeValues, null, i, this.createdFont, this.font2DHandle);
  }
  
  public Font deriveFont(int paramInt, AffineTransform paramAffineTransform) {
    AttributeValues attributeValues = getAttributeValues().clone();
    int i = (this.style != paramInt) ? this.style : -1;
    applyStyle(paramInt, attributeValues);
    applyTransform(paramAffineTransform, attributeValues);
    return new Font(attributeValues, null, i, this.createdFont, this.font2DHandle);
  }
  
  public Font deriveFont(float paramFloat) {
    if (this.values == null)
      return new Font(this.name, this.style, paramFloat, this.createdFont, this.font2DHandle); 
    AttributeValues attributeValues = getAttributeValues().clone();
    attributeValues.setSize(paramFloat);
    return new Font(attributeValues, null, -1, this.createdFont, this.font2DHandle);
  }
  
  public Font deriveFont(AffineTransform paramAffineTransform) {
    AttributeValues attributeValues = getAttributeValues().clone();
    applyTransform(paramAffineTransform, attributeValues);
    return new Font(attributeValues, null, -1, this.createdFont, this.font2DHandle);
  }
  
  public Font deriveFont(int paramInt) {
    if (this.values == null)
      return new Font(this.name, paramInt, this.size, this.createdFont, this.font2DHandle); 
    AttributeValues attributeValues = getAttributeValues().clone();
    int i = (this.style != paramInt) ? this.style : -1;
    applyStyle(paramInt, attributeValues);
    return new Font(attributeValues, null, i, this.createdFont, this.font2DHandle);
  }
  
  public Font deriveFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    if (paramMap == null)
      return this; 
    AttributeValues attributeValues = getAttributeValues().clone();
    attributeValues.merge(paramMap, RECOGNIZED_MASK);
    return new Font(attributeValues, this.name, this.style, this.createdFont, this.font2DHandle);
  }
  
  public boolean canDisplay(char paramChar) { return getFont2D().canDisplay(paramChar); }
  
  public boolean canDisplay(int paramInt) {
    if (!Character.isValidCodePoint(paramInt))
      throw new IllegalArgumentException("invalid code point: " + Integer.toHexString(paramInt)); 
    return getFont2D().canDisplay(paramInt);
  }
  
  public int canDisplayUpTo(String paramString) {
    Font2D font2D = getFont2D();
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (!font2D.canDisplay(c)) {
        if (!Character.isHighSurrogate(c))
          return b; 
        if (!font2D.canDisplay(paramString.codePointAt(b)))
          return b; 
        b++;
      } 
    } 
    return -1;
  }
  
  public int canDisplayUpTo(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    Font2D font2D = getFont2D();
    for (int i = paramInt1; i < paramInt2; i++) {
      char c = paramArrayOfChar[i];
      if (!font2D.canDisplay(c)) {
        if (!Character.isHighSurrogate(c))
          return i; 
        if (!font2D.canDisplay(Character.codePointAt(paramArrayOfChar, i, paramInt2)))
          return i; 
        i++;
      } 
    } 
    return -1;
  }
  
  public int canDisplayUpTo(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2) {
    Font2D font2D = getFont2D();
    char c = paramCharacterIterator.setIndex(paramInt1);
    int i = paramInt1;
    while (i < paramInt2) {
      if (!font2D.canDisplay(c)) {
        if (!Character.isHighSurrogate(c))
          return i; 
        char c1 = paramCharacterIterator.next();
        if (!Character.isLowSurrogate(c1))
          return i; 
        if (!font2D.canDisplay(Character.toCodePoint(c, c1)))
          return i; 
        i++;
      } 
      i++;
      c = paramCharacterIterator.next();
    } 
    return -1;
  }
  
  public float getItalicAngle() { return getItalicAngle(null); }
  
  private float getItalicAngle(FontRenderContext paramFontRenderContext) {
    Object object2;
    Object object1;
    if (paramFontRenderContext == null) {
      object1 = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      object2 = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    } else {
      object1 = paramFontRenderContext.getAntiAliasingHint();
      object2 = paramFontRenderContext.getFractionalMetricsHint();
    } 
    return getFont2D().getItalicAngle(this, identityTx, object1, object2);
  }
  
  public boolean hasUniformLineMetrics() { return false; }
  
  private FontLineMetrics defaultLineMetrics(FontRenderContext paramFontRenderContext) {
    FontLineMetrics fontLineMetrics = null;
    if (this.flmref == null || (fontLineMetrics = (FontLineMetrics)this.flmref.get()) == null || !fontLineMetrics.frc.equals(paramFontRenderContext)) {
      float[] arrayOfFloat1 = new float[8];
      getFont2D().getFontMetrics(this, identityTx, paramFontRenderContext.getAntiAliasingHint(), paramFontRenderContext.getFractionalMetricsHint(), arrayOfFloat1);
      float f1 = arrayOfFloat1[0];
      float f2 = arrayOfFloat1[1];
      float f3 = arrayOfFloat1[2];
      float f4 = 0.0F;
      if (this.values != null && this.values.getSuperscript() != 0) {
        f4 = (float)getTransform().getTranslateY();
        f1 -= f4;
        f2 += f4;
      } 
      float f5 = f1 + f2 + f3;
      byte b = 0;
      float[] arrayOfFloat2 = { 0.0F, (f2 / 2.0F - f1) / 2.0F, -f1 };
      float f6 = arrayOfFloat1[4];
      float f7 = arrayOfFloat1[5];
      float f8 = arrayOfFloat1[6];
      float f9 = arrayOfFloat1[7];
      float f10 = getItalicAngle(paramFontRenderContext);
      if (isTransformed()) {
        AffineTransform affineTransform = this.values.getCharTransform();
        if (affineTransform != null) {
          Point2D.Float float = new Point2D.Float();
          float.setLocation(0.0F, f6);
          affineTransform.deltaTransform(float, float);
          f6 = float.y;
          float.setLocation(0.0F, f7);
          affineTransform.deltaTransform(float, float);
          f7 = float.y;
          float.setLocation(0.0F, f8);
          affineTransform.deltaTransform(float, float);
          f8 = float.y;
          float.setLocation(0.0F, f9);
          affineTransform.deltaTransform(float, float);
          f9 = float.y;
        } 
      } 
      f6 += f4;
      f8 += f4;
      CoreMetrics coreMetrics = new CoreMetrics(f1, f2, f3, f5, b, arrayOfFloat2, f6, f7, f8, f9, f4, f10);
      fontLineMetrics = new FontLineMetrics(0, coreMetrics, paramFontRenderContext);
      this.flmref = new SoftReference(fontLineMetrics);
    } 
    return (FontLineMetrics)fontLineMetrics.clone();
  }
  
  public LineMetrics getLineMetrics(String paramString, FontRenderContext paramFontRenderContext) {
    FontLineMetrics fontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    fontLineMetrics.numchars = paramString.length();
    return fontLineMetrics;
  }
  
  public LineMetrics getLineMetrics(String paramString, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    FontLineMetrics fontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    int i = paramInt2 - paramInt1;
    fontLineMetrics.numchars = (i < 0) ? 0 : i;
    return fontLineMetrics;
  }
  
  public LineMetrics getLineMetrics(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    FontLineMetrics fontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    int i = paramInt2 - paramInt1;
    fontLineMetrics.numchars = (i < 0) ? 0 : i;
    return fontLineMetrics;
  }
  
  public LineMetrics getLineMetrics(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    FontLineMetrics fontLineMetrics = defaultLineMetrics(paramFontRenderContext);
    int i = paramInt2 - paramInt1;
    fontLineMetrics.numchars = (i < 0) ? 0 : i;
    return fontLineMetrics;
  }
  
  public Rectangle2D getStringBounds(String paramString, FontRenderContext paramFontRenderContext) {
    char[] arrayOfChar = paramString.toCharArray();
    return getStringBounds(arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext);
  }
  
  public Rectangle2D getStringBounds(String paramString, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    String str = paramString.substring(paramInt1, paramInt2);
    return getStringBounds(str, paramFontRenderContext);
  }
  
  public Rectangle2D getStringBounds(char[] paramArrayOfChar, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("beginIndex: " + paramInt1); 
    if (paramInt2 > paramArrayOfChar.length)
      throw new IndexOutOfBoundsException("limit: " + paramInt2); 
    if (paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException("range length: " + (paramInt2 - paramInt1)); 
    boolean bool = (this.values == null || (this.values.getKerning() == 0 && this.values.getLigatures() == 0 && this.values.getBaselineTransform() == null)) ? 1 : 0;
    if (bool)
      bool = !FontUtilities.isComplexText(paramArrayOfChar, paramInt1, paramInt2) ? 1 : 0; 
    if (bool) {
      StandardGlyphVector standardGlyphVector = new StandardGlyphVector(this, paramArrayOfChar, paramInt1, paramInt2 - paramInt1, paramFontRenderContext);
      return standardGlyphVector.getLogicalBounds();
    } 
    String str = new String(paramArrayOfChar, paramInt1, paramInt2 - paramInt1);
    TextLayout textLayout = new TextLayout(str, this, paramFontRenderContext);
    return new Rectangle2D.Float(0.0F, -textLayout.getAscent(), textLayout.getAdvance(), textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading());
  }
  
  public Rectangle2D getStringBounds(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, FontRenderContext paramFontRenderContext) {
    int i = paramCharacterIterator.getBeginIndex();
    int j = paramCharacterIterator.getEndIndex();
    if (paramInt1 < i)
      throw new IndexOutOfBoundsException("beginIndex: " + paramInt1); 
    if (paramInt2 > j)
      throw new IndexOutOfBoundsException("limit: " + paramInt2); 
    if (paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException("range length: " + (paramInt2 - paramInt1)); 
    char[] arrayOfChar = new char[paramInt2 - paramInt1];
    paramCharacterIterator.setIndex(paramInt1);
    for (byte b = 0; b < arrayOfChar.length; b++) {
      arrayOfChar[b] = paramCharacterIterator.current();
      paramCharacterIterator.next();
    } 
    return getStringBounds(arrayOfChar, 0, arrayOfChar.length, paramFontRenderContext);
  }
  
  public Rectangle2D getMaxCharBounds(FontRenderContext paramFontRenderContext) {
    float[] arrayOfFloat = new float[4];
    getFont2D().getFontMetrics(this, paramFontRenderContext, arrayOfFloat);
    return new Rectangle2D.Float(0.0F, -arrayOfFloat[0], arrayOfFloat[3], arrayOfFloat[0] + arrayOfFloat[1] + arrayOfFloat[2]);
  }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, String paramString) { return new StandardGlyphVector(this, paramString, paramFontRenderContext); }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar) { return new StandardGlyphVector(this, paramArrayOfChar, paramFontRenderContext); }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, CharacterIterator paramCharacterIterator) { return new StandardGlyphVector(this, paramCharacterIterator, paramFontRenderContext); }
  
  public GlyphVector createGlyphVector(FontRenderContext paramFontRenderContext, int[] paramArrayOfInt) { return new StandardGlyphVector(this, paramArrayOfInt, paramFontRenderContext); }
  
  public GlyphVector layoutGlyphVector(FontRenderContext paramFontRenderContext, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3) {
    GlyphLayout glyphLayout = GlyphLayout.get(null);
    StandardGlyphVector standardGlyphVector = glyphLayout.layout(this, paramFontRenderContext, paramArrayOfChar, paramInt1, paramInt2 - paramInt1, paramInt3, null);
    GlyphLayout.done(glyphLayout);
    return standardGlyphVector;
  }
  
  private static void applyTransform(AffineTransform paramAffineTransform, AttributeValues paramAttributeValues) {
    if (paramAffineTransform == null)
      throw new IllegalArgumentException("transform must not be null"); 
    paramAttributeValues.setTransform(paramAffineTransform);
  }
  
  private static void applyStyle(int paramInt, AttributeValues paramAttributeValues) {
    paramAttributeValues.setWeight(((paramInt & true) != 0) ? 2.0F : 1.0F);
    paramAttributeValues.setPosture(((paramInt & 0x2) != 0) ? 0.2F : 0.0F);
  }
  
  private static native void initIDs();
  
  static  {
    Toolkit.loadLibraries();
    initIDs();
    FontAccess.setFontAccess(new FontAccessImpl(null));
    identityTx = new AffineTransform();
    RECOGNIZED_MASK = AttributeValues.MASK_ALL & (AttributeValues.getMask(EAttribute.EFONT) ^ 0xFFFFFFFF);
    PRIMARY_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.EFAMILY, EAttribute.EWEIGHT, EAttribute.EWIDTH, EAttribute.EPOSTURE, EAttribute.ESIZE, EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.ETRACKING });
    SECONDARY_MASK = RECOGNIZED_MASK & (PRIMARY_MASK ^ 0xFFFFFFFF);
    LAYOUT_MASK = AttributeValues.getMask(new EAttribute[] { 
          EAttribute.ECHAR_REPLACEMENT, EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.EUNDERLINE, EAttribute.ESTRIKETHROUGH, EAttribute.ERUN_DIRECTION, EAttribute.EBIDI_EMBEDDING, EAttribute.EJUSTIFICATION, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE, 
          EAttribute.ESWAP_COLORS, EAttribute.ENUMERIC_SHAPING, EAttribute.EKERNING, EAttribute.ELIGATURES, EAttribute.ETRACKING, EAttribute.ESUPERSCRIPT });
    EXTRA_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.EWIDTH });
    ssinfo = new float[] { 0.0F, 0.375F, 0.625F, 0.7916667F, 0.9027778F, 0.9768519F, 1.0262346F, 1.0591564F };
  }
  
  private static class FontAccessImpl extends FontAccess {
    private FontAccessImpl() {}
    
    public Font2D getFont2D(Font param1Font) { return param1Font.getFont2D(); }
    
    public void setFont2D(Font param1Font, Font2DHandle param1Font2DHandle) { param1Font.font2DHandle = param1Font2DHandle; }
    
    public void setCreatedFont(Font param1Font) { param1Font.createdFont = true; }
    
    public boolean isCreatedFont(Font param1Font) { return param1Font.createdFont; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Font.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */