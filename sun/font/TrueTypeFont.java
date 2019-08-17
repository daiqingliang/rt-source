package sun.font;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import sun.awt.SunToolkit;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.action.GetPropertyAction;

public class TrueTypeFont extends FileFont {
  public static final int cmapTag = 1668112752;
  
  public static final int glyfTag = 1735162214;
  
  public static final int headTag = 1751474532;
  
  public static final int hheaTag = 1751672161;
  
  public static final int hmtxTag = 1752003704;
  
  public static final int locaTag = 1819239265;
  
  public static final int maxpTag = 1835104368;
  
  public static final int nameTag = 1851878757;
  
  public static final int postTag = 1886352244;
  
  public static final int os_2Tag = 1330851634;
  
  public static final int GDEFTag = 1195656518;
  
  public static final int GPOSTag = 1196445523;
  
  public static final int GSUBTag = 1196643650;
  
  public static final int mortTag = 1836020340;
  
  public static final int fdscTag = 1717859171;
  
  public static final int fvarTag = 1719034226;
  
  public static final int featTag = 1717920116;
  
  public static final int EBLCTag = 1161972803;
  
  public static final int gaspTag = 1734439792;
  
  public static final int ttcfTag = 1953784678;
  
  public static final int v1ttTag = 65536;
  
  public static final int trueTag = 1953658213;
  
  public static final int ottoTag = 1330926671;
  
  public static final int MS_PLATFORM_ID = 3;
  
  public static final short ENGLISH_LOCALE_ID = 1033;
  
  public static final int FAMILY_NAME_ID = 1;
  
  public static final int FULL_NAME_ID = 4;
  
  public static final int POSTSCRIPT_NAME_ID = 6;
  
  private static final short US_LCID = 1033;
  
  private static Map<String, Short> lcidMap;
  
  TTDisposerRecord disposerRecord = new TTDisposerRecord(null);
  
  int fontIndex = 0;
  
  int directoryCount = 1;
  
  int directoryOffset;
  
  int numTables;
  
  DirectoryEntry[] tableDirectory;
  
  private boolean supportsJA;
  
  private boolean supportsCJK;
  
  private Locale nameLocale;
  
  private String localeFamilyName;
  
  private String localeFullName;
  
  private static final int TTCHEADERSIZE = 12;
  
  private static final int DIRECTORYHEADERSIZE = 12;
  
  private static final int DIRECTORYENTRYSIZE = 16;
  
  static final String[] encoding_mapping = { 
      "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "", "", 
      "", "", "", "", "", "", "ms874", "ms932", "gbk", "ms949", 
      "ms950", "ms1361", "", "", "", "", "", "", "", "", 
      "", "" };
  
  private static final String[][] languages = { 
      { 
        "en", "ca", "da", "de", "es", "fi", "fr", "is", "it", "nl", 
        "no", "pt", "sq", "sv" }, { 
        "cs", "cz", "et", "hr", "hu", "nr", "pl", "ro", "sk", "sl", 
        "sq", "sr" }, { "bg", "mk", "ru", "sh", "uk" }, { "el" }, { "tr" }, { "he" }, { "ar" }, { "et", "lt", "lv" }, { "th" }, { "ja" }, 
      { "zh", "zh_CN" }, { "ko" }, { "zh_HK", "zh_TW" }, { "ko" } };
  
  private static final String[] codePages = { 
      "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "ms874", "ms932", 
      "gbk", "ms949", "ms950", "ms1361" };
  
  private static String defaultCodePage = null;
  
  public static final int reserved_bits1 = -2147483648;
  
  public static final int reserved_bits2 = 65535;
  
  private int fontWidth = 0;
  
  private int fontWeight = 0;
  
  private static final int fsSelectionItalicBit = 1;
  
  private static final int fsSelectionBoldBit = 32;
  
  private static final int fsSelectionRegularBit = 64;
  
  private float stSize;
  
  private float stPos;
  
  private float ulSize;
  
  private float ulPos;
  
  private char[] gaspTable;
  
  public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean) throws FontFormatException { this(paramString, paramObject, paramInt, paramBoolean, true); }
  
  public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2) throws FontFormatException {
    super(paramString, paramObject);
    this.useJavaRasterizer = paramBoolean1;
    this.fontRank = 3;
    try {
      verify(paramBoolean2);
      init(paramInt);
      if (!paramBoolean2)
        close(); 
    } catch (Throwable throwable) {
      close();
      if (throwable instanceof FontFormatException)
        throw (FontFormatException)throwable; 
      throw new FontFormatException("Unexpected runtime exception.");
    } 
    Disposer.addObjectRecord(this, this.disposerRecord);
  }
  
  protected boolean checkUseNatives() {
    if (this.checkedNatives)
      return this.useNatives; 
    if (!FontUtilities.isSolaris || this.useJavaRasterizer || FontUtilities.useT2K || this.nativeNames == null || getDirectoryEntry(1161972803) != null || GraphicsEnvironment.isHeadless()) {
      this.checkedNatives = true;
      return false;
    } 
    if (this.nativeNames instanceof String) {
      String str = (String)this.nativeNames;
      if (str.indexOf("8859") > 0) {
        this.checkedNatives = true;
        return false;
      } 
      if (NativeFont.hasExternalBitmaps(str)) {
        this.nativeFonts = new NativeFont[1];
        try {
          this.nativeFonts[0] = new NativeFont(str, true);
          this.useNatives = true;
        } catch (FontFormatException fontFormatException) {
          this.nativeFonts = null;
        } 
      } 
    } else if (this.nativeNames instanceof String[]) {
      String[] arrayOfString = (String[])this.nativeNames;
      int i = arrayOfString.length;
      boolean bool = false;
      byte b;
      for (b = 0; b < i; b++) {
        if (arrayOfString[b].indexOf("8859") > 0) {
          this.checkedNatives = true;
          return false;
        } 
        if (NativeFont.hasExternalBitmaps(arrayOfString[b]))
          bool = true; 
      } 
      if (!bool) {
        this.checkedNatives = true;
        return false;
      } 
      this.useNatives = true;
      this.nativeFonts = new NativeFont[i];
      for (b = 0; b < i; b++) {
        try {
          this.nativeFonts[b] = new NativeFont(arrayOfString[b], true);
        } catch (FontFormatException fontFormatException) {
          this.useNatives = false;
          this.nativeFonts = null;
        } 
      } 
    } 
    if (this.useNatives)
      this.glyphToCharMap = new char[getMapper().getNumGlyphs()]; 
    this.checkedNatives = true;
    return this.useNatives;
  }
  
  private FileChannel open() throws FontFormatException { return open(true); }
  
  private FileChannel open(boolean paramBoolean) throws FontFormatException {
    if (this.disposerRecord.channel == null) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("open TTF: " + this.platName); 
      try {
        RandomAccessFile randomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() {
                try {
                  return new RandomAccessFile(TrueTypeFont.this.platName, "r");
                } catch (FileNotFoundException fileNotFoundException) {
                  return null;
                } 
              }
            });
        this.disposerRecord.channel = randomAccessFile.getChannel();
        this.fileSize = (int)this.disposerRecord.channel.size();
        if (paramBoolean) {
          FontManager fontManager = FontManagerFactory.getInstance();
          if (fontManager instanceof SunFontManager)
            ((SunFontManager)fontManager).addToPool(this); 
        } 
      } catch (NullPointerException nullPointerException) {
        close();
        throw new FontFormatException(nullPointerException.toString());
      } catch (ClosedChannelException closedChannelException) {
        Thread.interrupted();
        close();
        open();
      } catch (IOException iOException) {
        close();
        throw new FontFormatException(iOException.toString());
      } 
    } 
    return this.disposerRecord.channel;
  }
  
  protected void close() { this.disposerRecord.dispose(); }
  
  int readBlock(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2) {
    int i = 0;
    try {
      synchronized (this) {
        if (this.disposerRecord.channel == null)
          open(); 
        if (paramInt1 + paramInt2 > this.fileSize) {
          if (paramInt1 >= this.fileSize) {
            if (FontUtilities.isLogging()) {
              String str = "Read offset is " + paramInt1 + " file size is " + this.fileSize + " file is " + this.platName;
              FontUtilities.getLogger().severe(str);
            } 
            return -1;
          } 
          paramInt2 = this.fileSize - paramInt1;
        } 
        paramByteBuffer.clear();
        this.disposerRecord.channel.position(paramInt1);
        while (i < paramInt2) {
          int j = this.disposerRecord.channel.read(paramByteBuffer);
          if (j == -1) {
            String str = "Unexpected EOF " + this;
            int k = (int)this.disposerRecord.channel.size();
            if (k != this.fileSize)
              str = str + " File size was " + this.fileSize + " and now is " + k; 
            if (FontUtilities.isLogging())
              FontUtilities.getLogger().severe(str); 
            if (i > paramInt2 / 2 || i > 16384) {
              paramByteBuffer.flip();
              if (FontUtilities.isLogging()) {
                str = "Returning " + i + " bytes instead of " + paramInt2;
                FontUtilities.getLogger().severe(str);
              } 
            } else {
              i = -1;
            } 
            throw new IOException(str);
          } 
          i += j;
        } 
        paramByteBuffer.flip();
        if (i > paramInt2)
          i = paramInt2; 
      } 
    } catch (FontFormatException fontFormatException) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().severe("While reading " + this.platName, fontFormatException); 
      i = -1;
      deregisterFontAndClearStrikeCache();
    } catch (ClosedChannelException closedChannelException) {
      Thread.interrupted();
      close();
      return readBlock(paramByteBuffer, paramInt1, paramInt2);
    } catch (IOException iOException) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().severe("While reading " + this.platName, iOException); 
      if (i == 0) {
        i = -1;
        deregisterFontAndClearStrikeCache();
      } 
    } 
    return i;
  }
  
  ByteBuffer readBlock(int paramInt1, int paramInt2) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(paramInt2);
    try {
      synchronized (this) {
        if (this.disposerRecord.channel == null)
          open(); 
        if (paramInt1 + paramInt2 > this.fileSize) {
          if (paramInt1 > this.fileSize)
            return null; 
          byteBuffer = ByteBuffer.allocate(this.fileSize - paramInt1);
        } 
        this.disposerRecord.channel.position(paramInt1);
        this.disposerRecord.channel.read(byteBuffer);
        byteBuffer.flip();
      } 
    } catch (FontFormatException fontFormatException) {
      return null;
    } catch (ClosedChannelException closedChannelException) {
      Thread.interrupted();
      close();
      readBlock(byteBuffer, paramInt1, paramInt2);
    } catch (IOException iOException) {
      return null;
    } 
    return byteBuffer;
  }
  
  byte[] readBytes(int paramInt1, int paramInt2) {
    ByteBuffer byteBuffer = readBlock(paramInt1, paramInt2);
    if (byteBuffer.hasArray())
      return byteBuffer.array(); 
    byte[] arrayOfByte = new byte[byteBuffer.limit()];
    byteBuffer.get(arrayOfByte);
    return arrayOfByte;
  }
  
  private void verify(boolean paramBoolean) throws FontFormatException { open(paramBoolean); }
  
  protected void init(int paramInt) throws FontFormatException {
    int i = 0;
    ByteBuffer byteBuffer1 = readBlock(0, 12);
    try {
      switch (byteBuffer1.getInt()) {
        case 1953784678:
          byteBuffer1.getInt();
          this.directoryCount = byteBuffer1.getInt();
          if (paramInt >= this.directoryCount)
            throw new FontFormatException("Bad collection index"); 
          this.fontIndex = paramInt;
          byteBuffer1 = readBlock(12 + 4 * paramInt, 4);
          i = byteBuffer1.getInt();
          break;
        case 65536:
        case 1330926671:
        case 1953658213:
          break;
        default:
          throw new FontFormatException("Unsupported sfnt " + getPublicFileName());
      } 
      byteBuffer1 = readBlock(i + 4, 2);
      this.numTables = byteBuffer1.getShort();
      this.directoryOffset = i + 12;
      ByteBuffer byteBuffer = readBlock(this.directoryOffset, this.numTables * 16);
      IntBuffer intBuffer = byteBuffer.asIntBuffer();
      this.tableDirectory = new DirectoryEntry[this.numTables];
      for (byte b = 0; b < this.numTables; b++) {
        DirectoryEntry directoryEntry = new DirectoryEntry();
        this.tableDirectory[b] = directoryEntry;
        directoryEntry.tag = intBuffer.get();
        intBuffer.get();
        directoryEntry.offset = intBuffer.get();
        directoryEntry.length = intBuffer.get();
        if (directoryEntry.offset + directoryEntry.length > this.fileSize)
          throw new FontFormatException("bad table, tag=" + directoryEntry.tag); 
      } 
      if (getDirectoryEntry(1751474532) == null)
        throw new FontFormatException("missing head table"); 
      if (getDirectoryEntry(1835104368) == null)
        throw new FontFormatException("missing maxp table"); 
      if (getDirectoryEntry(1752003704) != null && getDirectoryEntry(1751672161) == null)
        throw new FontFormatException("missing hhea table"); 
      initNames();
    } catch (Exception exception) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().severe(exception.toString()); 
      if (exception instanceof FontFormatException)
        throw (FontFormatException)exception; 
      throw new FontFormatException(exception.toString());
    } 
    if (this.familyName == null || this.fullName == null)
      throw new FontFormatException("Font name not found"); 
    ByteBuffer byteBuffer2 = getTableBuffer(1330851634);
    setStyle(byteBuffer2);
    setCJKSupport(byteBuffer2);
  }
  
  static String getCodePage() {
    if (defaultCodePage != null)
      return defaultCodePage; 
    if (FontUtilities.isWindows) {
      defaultCodePage = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
    } else {
      if (languages.length != codePages.length)
        throw new InternalError("wrong code pages array length"); 
      Locale locale = SunToolkit.getStartupLocale();
      String str = locale.getLanguage();
      if (str != null) {
        if (str.equals("zh")) {
          String str1 = locale.getCountry();
          if (str1 != null)
            str = str + "_" + str1; 
        } 
        for (byte b = 0; b < languages.length; b++) {
          for (byte b1 = 0; b1 < languages[b].length; b1++) {
            if (str.equals(languages[b][b1])) {
              defaultCodePage = codePages[b];
              return defaultCodePage;
            } 
          } 
        } 
      } 
    } 
    if (defaultCodePage == null)
      defaultCodePage = ""; 
    return defaultCodePage;
  }
  
  boolean supportsEncoding(String paramString) {
    if (paramString == null)
      paramString = getCodePage(); 
    if ("".equals(paramString))
      return false; 
    paramString = paramString.toLowerCase();
    if (paramString.equals("gb18030")) {
      paramString = "gbk";
    } else if (paramString.equals("ms950_hkscs")) {
      paramString = "ms950";
    } 
    ByteBuffer byteBuffer = getTableBuffer(1330851634);
    if (byteBuffer == null || byteBuffer.capacity() < 86)
      return false; 
    int i = byteBuffer.getInt(78);
    int j = byteBuffer.getInt(82);
    for (byte b = 0; b < encoding_mapping.length; b++) {
      if (encoding_mapping[b].equals(paramString) && (true << b & i) != 0)
        return true; 
    } 
    return false;
  }
  
  private void setCJKSupport(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer == null || paramByteBuffer.capacity() < 50)
      return; 
    int i = paramByteBuffer.getInt(46);
    this.supportsCJK = ((i & 0x29BF0000) != 0);
    this.supportsJA = ((i & 0x60000) != 0);
  }
  
  boolean supportsJA() { return this.supportsJA; }
  
  ByteBuffer getTableBuffer(int paramInt) {
    DirectoryEntry directoryEntry = null;
    int i;
    for (i = 0; i < this.numTables; i++) {
      if ((this.tableDirectory[i]).tag == paramInt) {
        directoryEntry = this.tableDirectory[i];
        break;
      } 
    } 
    if (directoryEntry == null || directoryEntry.length == 0 || directoryEntry.offset + directoryEntry.length > this.fileSize)
      return null; 
    i = 0;
    ByteBuffer byteBuffer = ByteBuffer.allocate(directoryEntry.length);
    synchronized (this) {
      try {
        if (this.disposerRecord.channel == null)
          open(); 
        this.disposerRecord.channel.position(directoryEntry.offset);
        i = this.disposerRecord.channel.read(byteBuffer);
        byteBuffer.flip();
      } catch (ClosedChannelException closedChannelException) {
        Thread.interrupted();
        close();
        return getTableBuffer(paramInt);
      } catch (IOException iOException) {
        return null;
      } catch (FontFormatException fontFormatException) {
        return null;
      } 
      if (i < directoryEntry.length)
        return null; 
      return byteBuffer;
    } 
  }
  
  protected long getLayoutTableCache() {
    try {
      return getScaler().getLayoutTableCache();
    } catch (FontScalerException fontScalerException) {
      return 0L;
    } 
  }
  
  protected byte[] getTableBytes(int paramInt) {
    ByteBuffer byteBuffer = getTableBuffer(paramInt);
    if (byteBuffer == null)
      return null; 
    if (byteBuffer.hasArray())
      try {
        return byteBuffer.array();
      } catch (Exception exception) {} 
    byte[] arrayOfByte = new byte[getTableSize(paramInt)];
    byteBuffer.get(arrayOfByte);
    return arrayOfByte;
  }
  
  int getTableSize(int paramInt) {
    for (byte b = 0; b < this.numTables; b++) {
      if ((this.tableDirectory[b]).tag == paramInt)
        return (this.tableDirectory[b]).length; 
    } 
    return 0;
  }
  
  int getTableOffset(int paramInt) {
    for (byte b = 0; b < this.numTables; b++) {
      if ((this.tableDirectory[b]).tag == paramInt)
        return (this.tableDirectory[b]).offset; 
    } 
    return 0;
  }
  
  DirectoryEntry getDirectoryEntry(int paramInt) {
    for (byte b = 0; b < this.numTables; b++) {
      if ((this.tableDirectory[b]).tag == paramInt)
        return this.tableDirectory[b]; 
    } 
    return null;
  }
  
  boolean useEmbeddedBitmapsForSize(int paramInt) {
    if (!this.supportsCJK)
      return false; 
    if (getDirectoryEntry(1161972803) == null)
      return false; 
    ByteBuffer byteBuffer = getTableBuffer(1161972803);
    int i = byteBuffer.getInt(4);
    for (byte b = 0; b < i; b++) {
      byte b1 = byteBuffer.get(8 + b * 48 + 45) & 0xFF;
      if (b1 == paramInt)
        return true; 
    } 
    return false;
  }
  
  public String getFullName() { return this.fullName; }
  
  protected void setStyle() { setStyle(getTableBuffer(1330851634)); }
  
  public int getWidth() { return (this.fontWidth > 0) ? this.fontWidth : super.getWidth(); }
  
  public int getWeight() { return (this.fontWeight > 0) ? this.fontWeight : super.getWeight(); }
  
  private void setStyle(ByteBuffer paramByteBuffer) {
    if (paramByteBuffer == null)
      return; 
    if (paramByteBuffer.capacity() >= 8) {
      this.fontWeight = paramByteBuffer.getChar(4) & 0xFFFF;
      this.fontWidth = paramByteBuffer.getChar(6) & 0xFFFF;
    } 
    if (paramByteBuffer.capacity() < 64) {
      super.setStyle();
      return;
    } 
    char c1 = paramByteBuffer.getChar(62) & 0xFFFF;
    char c2 = c1 & true;
    char c3 = c1 & 0x20;
    char c4 = c1 & 0x40;
    if (c4 != '\000' && (c2 | c3) != '\000') {
      super.setStyle();
      return;
    } 
    if ((c4 | c2 | c3) == '\000') {
      super.setStyle();
      return;
    } 
    switch (c3 | c2) {
      case '\001':
        this.style = 2;
        break;
      case ' ':
        if (FontUtilities.isSolaris && this.platName.endsWith("HG-GothicB.ttf")) {
          this.style = 0;
          break;
        } 
        this.style = 1;
        break;
      case '!':
        this.style = 3;
        break;
    } 
  }
  
  private void setStrikethroughMetrics(ByteBuffer paramByteBuffer, int paramInt) {
    if (paramByteBuffer == null || paramByteBuffer.capacity() < 30 || paramInt < 0) {
      this.stSize = 0.05F;
      this.stPos = -0.4F;
      return;
    } 
    ShortBuffer shortBuffer = paramByteBuffer.asShortBuffer();
    this.stSize = shortBuffer.get(13) / paramInt;
    this.stPos = -shortBuffer.get(14) / paramInt;
  }
  
  private void setUnderlineMetrics(ByteBuffer paramByteBuffer, int paramInt) {
    if (paramByteBuffer == null || paramByteBuffer.capacity() < 12 || paramInt < 0) {
      this.ulSize = 0.05F;
      this.ulPos = 0.1F;
      return;
    } 
    ShortBuffer shortBuffer = paramByteBuffer.asShortBuffer();
    this.ulSize = shortBuffer.get(5) / paramInt;
    this.ulPos = -shortBuffer.get(4) / paramInt;
  }
  
  public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt) {
    if (this.ulSize == 0.0F && this.ulPos == 0.0F) {
      ByteBuffer byteBuffer1 = getTableBuffer(1751474532);
      short s = -1;
      if (byteBuffer1 != null && byteBuffer1.capacity() >= 18) {
        ShortBuffer shortBuffer = byteBuffer1.asShortBuffer();
        s = shortBuffer.get(9) & 0xFFFF;
        if (s < 16 || s > 16384)
          s = 2048; 
      } 
      ByteBuffer byteBuffer2 = getTableBuffer(1330851634);
      setStrikethroughMetrics(byteBuffer2, s);
      ByteBuffer byteBuffer3 = getTableBuffer(1886352244);
      setUnderlineMetrics(byteBuffer3, s);
    } 
    paramArrayOfFloat[paramInt] = this.stPos * paramFloat;
    paramArrayOfFloat[paramInt + 1] = this.stSize * paramFloat;
    paramArrayOfFloat[paramInt + 2] = this.ulPos * paramFloat;
    paramArrayOfFloat[paramInt + 3] = this.ulSize * paramFloat;
  }
  
  private String makeString(byte[] paramArrayOfByte, int paramInt, short paramShort) {
    String str;
    if (paramShort >= 2 && paramShort <= 6) {
      byte[] arrayOfByte = paramArrayOfByte;
      int i = paramInt;
      paramArrayOfByte = new byte[i];
      paramInt = 0;
      for (byte b = 0; b < i; b++) {
        if (arrayOfByte[b] != 0)
          paramArrayOfByte[paramInt++] = arrayOfByte[b]; 
      } 
    } 
    switch (paramShort) {
      case 1:
        str = "UTF-16";
        break;
      case 0:
        str = "UTF-16";
        break;
      case 2:
        str = "SJIS";
        break;
      case 3:
        str = "GBK";
        break;
      case 4:
        str = "MS950";
        break;
      case 5:
        str = "EUC_KR";
        break;
      case 6:
        str = "Johab";
        break;
      default:
        str = "UTF-16";
        break;
    } 
    try {
      return new String(paramArrayOfByte, 0, paramInt, str);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().warning(unsupportedEncodingException + " EncodingID=" + paramShort); 
      return new String(paramArrayOfByte, 0, paramInt);
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  protected void initNames() {
    byte[] arrayOfByte = new byte[256];
    ByteBuffer byteBuffer = getTableBuffer(1851878757);
    if (byteBuffer != null) {
      ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
      shortBuffer.get();
      short s1 = shortBuffer.get();
      short s2 = shortBuffer.get() & 0xFFFF;
      this.nameLocale = SunToolkit.getStartupLocale();
      short s3 = getLCIDFromLocale(this.nameLocale);
      for (byte b = 0; b < s1; b++) {
        short s = shortBuffer.get();
        if (s != 3) {
          shortBuffer.position(shortBuffer.position() + 5);
        } else {
          short s4 = shortBuffer.get();
          short s5 = shortBuffer.get();
          short s6 = shortBuffer.get();
          short s7 = shortBuffer.get() & 0xFFFF;
          short s8 = (shortBuffer.get() & 0xFFFF) + s2;
          String str = null;
          switch (s6) {
            case 1:
              if (this.familyName == null || s5 == 1033 || s5 == s3) {
                byteBuffer.position(s8);
                byteBuffer.get(arrayOfByte, 0, s7);
                str = makeString(arrayOfByte, s7, s4);
                if (this.familyName == null || s5 == 1033)
                  this.familyName = str; 
                if (s5 == s3)
                  this.localeFamilyName = str; 
              } 
              break;
            case 4:
              if (this.fullName == null || s5 == 1033 || s5 == s3) {
                byteBuffer.position(s8);
                byteBuffer.get(arrayOfByte, 0, s7);
                str = makeString(arrayOfByte, s7, s4);
                if (this.fullName == null || s5 == 1033)
                  this.fullName = str; 
                if (s5 == s3)
                  this.localeFullName = str; 
              } 
              break;
          } 
        } 
      } 
      if (this.localeFamilyName == null)
        this.localeFamilyName = this.familyName; 
      if (this.localeFullName == null)
        this.localeFullName = this.fullName; 
    } 
  }
  
  protected String lookupName(short paramShort, int paramInt) {
    String str = null;
    byte[] arrayOfByte = new byte[1024];
    ByteBuffer byteBuffer = getTableBuffer(1851878757);
    if (byteBuffer != null) {
      ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
      shortBuffer.get();
      short s1 = shortBuffer.get();
      short s2 = shortBuffer.get() & 0xFFFF;
      for (byte b = 0; b < s1; b++) {
        short s = shortBuffer.get();
        if (s != 3) {
          shortBuffer.position(shortBuffer.position() + 5);
        } else {
          short s3 = shortBuffer.get();
          short s4 = shortBuffer.get();
          short s5 = shortBuffer.get();
          short s6 = shortBuffer.get() & 0xFFFF;
          short s7 = (shortBuffer.get() & 0xFFFF) + s2;
          if (s5 == paramInt && ((str == null && s4 == 1033) || s4 == paramShort)) {
            byteBuffer.position(s7);
            byteBuffer.get(arrayOfByte, 0, s6);
            str = makeString(arrayOfByte, s6, s3);
            if (s4 == paramShort)
              return str; 
          } 
        } 
      } 
    } 
    return str;
  }
  
  public int getFontCount() { return this.directoryCount; }
  
  protected FontScaler getScaler() {
    if (this.scaler == null)
      this.scaler = FontScaler.getScaler(this, this.fontIndex, this.supportsCJK, this.fileSize); 
    return this.scaler;
  }
  
  public String getPostscriptName() {
    String str = lookupName((short)1033, 6);
    return (str == null) ? this.fullName : str;
  }
  
  public String getFontName(Locale paramLocale) {
    if (paramLocale == null)
      return this.fullName; 
    if (paramLocale.equals(this.nameLocale) && this.localeFullName != null)
      return this.localeFullName; 
    short s = getLCIDFromLocale(paramLocale);
    String str = lookupName(s, 4);
    return (str == null) ? this.fullName : str;
  }
  
  private static void addLCIDMapEntry(Map<String, Short> paramMap, String paramString, short paramShort) { paramMap.put(paramString, Short.valueOf(paramShort)); }
  
  private static void createLCIDMap() {
    if (lcidMap != null)
      return; 
    HashMap hashMap = new HashMap(200);
    addLCIDMapEntry(hashMap, "ar", (short)1025);
    addLCIDMapEntry(hashMap, "bg", (short)1026);
    addLCIDMapEntry(hashMap, "ca", (short)1027);
    addLCIDMapEntry(hashMap, "zh", (short)1028);
    addLCIDMapEntry(hashMap, "cs", (short)1029);
    addLCIDMapEntry(hashMap, "da", (short)1030);
    addLCIDMapEntry(hashMap, "de", (short)1031);
    addLCIDMapEntry(hashMap, "el", (short)1032);
    addLCIDMapEntry(hashMap, "es", (short)1034);
    addLCIDMapEntry(hashMap, "fi", (short)1035);
    addLCIDMapEntry(hashMap, "fr", (short)1036);
    addLCIDMapEntry(hashMap, "iw", (short)1037);
    addLCIDMapEntry(hashMap, "hu", (short)1038);
    addLCIDMapEntry(hashMap, "is", (short)1039);
    addLCIDMapEntry(hashMap, "it", (short)1040);
    addLCIDMapEntry(hashMap, "ja", (short)1041);
    addLCIDMapEntry(hashMap, "ko", (short)1042);
    addLCIDMapEntry(hashMap, "nl", (short)1043);
    addLCIDMapEntry(hashMap, "no", (short)1044);
    addLCIDMapEntry(hashMap, "pl", (short)1045);
    addLCIDMapEntry(hashMap, "pt", (short)1046);
    addLCIDMapEntry(hashMap, "rm", (short)1047);
    addLCIDMapEntry(hashMap, "ro", (short)1048);
    addLCIDMapEntry(hashMap, "ru", (short)1049);
    addLCIDMapEntry(hashMap, "hr", (short)1050);
    addLCIDMapEntry(hashMap, "sk", (short)1051);
    addLCIDMapEntry(hashMap, "sq", (short)1052);
    addLCIDMapEntry(hashMap, "sv", (short)1053);
    addLCIDMapEntry(hashMap, "th", (short)1054);
    addLCIDMapEntry(hashMap, "tr", (short)1055);
    addLCIDMapEntry(hashMap, "ur", (short)1056);
    addLCIDMapEntry(hashMap, "in", (short)1057);
    addLCIDMapEntry(hashMap, "uk", (short)1058);
    addLCIDMapEntry(hashMap, "be", (short)1059);
    addLCIDMapEntry(hashMap, "sl", (short)1060);
    addLCIDMapEntry(hashMap, "et", (short)1061);
    addLCIDMapEntry(hashMap, "lv", (short)1062);
    addLCIDMapEntry(hashMap, "lt", (short)1063);
    addLCIDMapEntry(hashMap, "fa", (short)1065);
    addLCIDMapEntry(hashMap, "vi", (short)1066);
    addLCIDMapEntry(hashMap, "hy", (short)1067);
    addLCIDMapEntry(hashMap, "eu", (short)1069);
    addLCIDMapEntry(hashMap, "mk", (short)1071);
    addLCIDMapEntry(hashMap, "tn", (short)1074);
    addLCIDMapEntry(hashMap, "xh", (short)1076);
    addLCIDMapEntry(hashMap, "zu", (short)1077);
    addLCIDMapEntry(hashMap, "af", (short)1078);
    addLCIDMapEntry(hashMap, "ka", (short)1079);
    addLCIDMapEntry(hashMap, "fo", (short)1080);
    addLCIDMapEntry(hashMap, "hi", (short)1081);
    addLCIDMapEntry(hashMap, "mt", (short)1082);
    addLCIDMapEntry(hashMap, "se", (short)1083);
    addLCIDMapEntry(hashMap, "gd", (short)1084);
    addLCIDMapEntry(hashMap, "ms", (short)1086);
    addLCIDMapEntry(hashMap, "kk", (short)1087);
    addLCIDMapEntry(hashMap, "ky", (short)1088);
    addLCIDMapEntry(hashMap, "sw", (short)1089);
    addLCIDMapEntry(hashMap, "tt", (short)1092);
    addLCIDMapEntry(hashMap, "bn", (short)1093);
    addLCIDMapEntry(hashMap, "pa", (short)1094);
    addLCIDMapEntry(hashMap, "gu", (short)1095);
    addLCIDMapEntry(hashMap, "ta", (short)1097);
    addLCIDMapEntry(hashMap, "te", (short)1098);
    addLCIDMapEntry(hashMap, "kn", (short)1099);
    addLCIDMapEntry(hashMap, "ml", (short)1100);
    addLCIDMapEntry(hashMap, "mr", (short)1102);
    addLCIDMapEntry(hashMap, "sa", (short)1103);
    addLCIDMapEntry(hashMap, "mn", (short)1104);
    addLCIDMapEntry(hashMap, "cy", (short)1106);
    addLCIDMapEntry(hashMap, "gl", (short)1110);
    addLCIDMapEntry(hashMap, "dv", (short)1125);
    addLCIDMapEntry(hashMap, "qu", (short)1131);
    addLCIDMapEntry(hashMap, "mi", (short)1153);
    addLCIDMapEntry(hashMap, "ar_IQ", (short)2049);
    addLCIDMapEntry(hashMap, "zh_CN", (short)2052);
    addLCIDMapEntry(hashMap, "de_CH", (short)2055);
    addLCIDMapEntry(hashMap, "en_GB", (short)2057);
    addLCIDMapEntry(hashMap, "es_MX", (short)2058);
    addLCIDMapEntry(hashMap, "fr_BE", (short)2060);
    addLCIDMapEntry(hashMap, "it_CH", (short)2064);
    addLCIDMapEntry(hashMap, "nl_BE", (short)2067);
    addLCIDMapEntry(hashMap, "no_NO_NY", (short)2068);
    addLCIDMapEntry(hashMap, "pt_PT", (short)2070);
    addLCIDMapEntry(hashMap, "ro_MD", (short)2072);
    addLCIDMapEntry(hashMap, "ru_MD", (short)2073);
    addLCIDMapEntry(hashMap, "sr_CS", (short)2074);
    addLCIDMapEntry(hashMap, "sv_FI", (short)2077);
    addLCIDMapEntry(hashMap, "az_AZ", (short)2092);
    addLCIDMapEntry(hashMap, "se_SE", (short)2107);
    addLCIDMapEntry(hashMap, "ga_IE", (short)2108);
    addLCIDMapEntry(hashMap, "ms_BN", (short)2110);
    addLCIDMapEntry(hashMap, "uz_UZ", (short)2115);
    addLCIDMapEntry(hashMap, "qu_EC", (short)2155);
    addLCIDMapEntry(hashMap, "ar_EG", (short)3073);
    addLCIDMapEntry(hashMap, "zh_HK", (short)3076);
    addLCIDMapEntry(hashMap, "de_AT", (short)3079);
    addLCIDMapEntry(hashMap, "en_AU", (short)3081);
    addLCIDMapEntry(hashMap, "fr_CA", (short)3084);
    addLCIDMapEntry(hashMap, "sr_CS", (short)3098);
    addLCIDMapEntry(hashMap, "se_FI", (short)3131);
    addLCIDMapEntry(hashMap, "qu_PE", (short)3179);
    addLCIDMapEntry(hashMap, "ar_LY", (short)4097);
    addLCIDMapEntry(hashMap, "zh_SG", (short)4100);
    addLCIDMapEntry(hashMap, "de_LU", (short)4103);
    addLCIDMapEntry(hashMap, "en_CA", (short)4105);
    addLCIDMapEntry(hashMap, "es_GT", (short)4106);
    addLCIDMapEntry(hashMap, "fr_CH", (short)4108);
    addLCIDMapEntry(hashMap, "hr_BA", (short)4122);
    addLCIDMapEntry(hashMap, "ar_DZ", (short)5121);
    addLCIDMapEntry(hashMap, "zh_MO", (short)5124);
    addLCIDMapEntry(hashMap, "de_LI", (short)5127);
    addLCIDMapEntry(hashMap, "en_NZ", (short)5129);
    addLCIDMapEntry(hashMap, "es_CR", (short)5130);
    addLCIDMapEntry(hashMap, "fr_LU", (short)5132);
    addLCIDMapEntry(hashMap, "bs_BA", (short)5146);
    addLCIDMapEntry(hashMap, "ar_MA", (short)6145);
    addLCIDMapEntry(hashMap, "en_IE", (short)6153);
    addLCIDMapEntry(hashMap, "es_PA", (short)6154);
    addLCIDMapEntry(hashMap, "fr_MC", (short)6156);
    addLCIDMapEntry(hashMap, "sr_BA", (short)6170);
    addLCIDMapEntry(hashMap, "ar_TN", (short)7169);
    addLCIDMapEntry(hashMap, "en_ZA", (short)7177);
    addLCIDMapEntry(hashMap, "es_DO", (short)7178);
    addLCIDMapEntry(hashMap, "sr_BA", (short)7194);
    addLCIDMapEntry(hashMap, "ar_OM", (short)8193);
    addLCIDMapEntry(hashMap, "en_JM", (short)8201);
    addLCIDMapEntry(hashMap, "es_VE", (short)8202);
    addLCIDMapEntry(hashMap, "ar_YE", (short)9217);
    addLCIDMapEntry(hashMap, "es_CO", (short)9226);
    addLCIDMapEntry(hashMap, "ar_SY", (short)10241);
    addLCIDMapEntry(hashMap, "en_BZ", (short)10249);
    addLCIDMapEntry(hashMap, "es_PE", (short)10250);
    addLCIDMapEntry(hashMap, "ar_JO", (short)11265);
    addLCIDMapEntry(hashMap, "en_TT", (short)11273);
    addLCIDMapEntry(hashMap, "es_AR", (short)11274);
    addLCIDMapEntry(hashMap, "ar_LB", (short)12289);
    addLCIDMapEntry(hashMap, "en_ZW", (short)12297);
    addLCIDMapEntry(hashMap, "es_EC", (short)12298);
    addLCIDMapEntry(hashMap, "ar_KW", (short)13313);
    addLCIDMapEntry(hashMap, "en_PH", (short)13321);
    addLCIDMapEntry(hashMap, "es_CL", (short)13322);
    addLCIDMapEntry(hashMap, "ar_AE", (short)14337);
    addLCIDMapEntry(hashMap, "es_UY", (short)14346);
    addLCIDMapEntry(hashMap, "ar_BH", (short)15361);
    addLCIDMapEntry(hashMap, "es_PY", (short)15370);
    addLCIDMapEntry(hashMap, "ar_QA", (short)16385);
    addLCIDMapEntry(hashMap, "es_BO", (short)16394);
    addLCIDMapEntry(hashMap, "es_SV", (short)17418);
    addLCIDMapEntry(hashMap, "es_HN", (short)18442);
    addLCIDMapEntry(hashMap, "es_NI", (short)19466);
    addLCIDMapEntry(hashMap, "es_PR", (short)20490);
    lcidMap = hashMap;
  }
  
  private static short getLCIDFromLocale(Locale paramLocale) {
    if (paramLocale.equals(Locale.US))
      return 1033; 
    if (lcidMap == null)
      createLCIDMap(); 
    for (String str = paramLocale.toString(); !"".equals(str); str = str.substring(0, i)) {
      Short short = (Short)lcidMap.get(str);
      if (short != null)
        return short.shortValue(); 
      int i = str.lastIndexOf('_');
      if (i < 1)
        return 1033; 
    } 
    return 1033;
  }
  
  public String getFamilyName(Locale paramLocale) {
    if (paramLocale == null)
      return this.familyName; 
    if (paramLocale.equals(this.nameLocale) && this.localeFamilyName != null)
      return this.localeFamilyName; 
    short s = getLCIDFromLocale(paramLocale);
    String str = lookupName(s, 1);
    return (str == null) ? this.familyName : str;
  }
  
  public CharToGlyphMapper getMapper() {
    if (this.mapper == null)
      this.mapper = new TrueTypeGlyphMapper(this); 
    return this.mapper;
  }
  
  protected void initAllNames(int paramInt, HashSet paramHashSet) {
    byte[] arrayOfByte = new byte[256];
    ByteBuffer byteBuffer = getTableBuffer(1851878757);
    if (byteBuffer != null) {
      ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
      shortBuffer.get();
      short s1 = shortBuffer.get();
      short s2 = shortBuffer.get() & 0xFFFF;
      for (byte b = 0; b < s1; b++) {
        short s = shortBuffer.get();
        if (s != 3) {
          shortBuffer.position(shortBuffer.position() + 5);
        } else {
          short s3 = shortBuffer.get();
          short s4 = shortBuffer.get();
          short s5 = shortBuffer.get();
          short s6 = shortBuffer.get() & 0xFFFF;
          short s7 = (shortBuffer.get() & 0xFFFF) + s2;
          if (s5 == paramInt) {
            byteBuffer.position(s7);
            byteBuffer.get(arrayOfByte, 0, s6);
            paramHashSet.add(makeString(arrayOfByte, s6, s3));
          } 
        } 
      } 
    } 
  }
  
  String[] getAllFamilyNames() {
    HashSet hashSet = new HashSet();
    try {
      initAllNames(1, hashSet);
    } catch (Exception exception) {}
    return (String[])hashSet.toArray(new String[0]);
  }
  
  String[] getAllFullNames() {
    HashSet hashSet = new HashSet();
    try {
      initAllNames(4, hashSet);
    } catch (Exception exception) {}
    return (String[])hashSet.toArray(new String[0]);
  }
  
  Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) {
    try {
      return getScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
    } catch (FontScalerException fontScalerException) {
      return null;
    } 
  }
  
  private char[] getGaspTable() {
    if (this.gaspTable != null)
      return this.gaspTable; 
    ByteBuffer byteBuffer = getTableBuffer(1734439792);
    if (byteBuffer == null)
      return this.gaspTable = new char[0]; 
    CharBuffer charBuffer = byteBuffer.asCharBuffer();
    char c1 = charBuffer.get();
    if (c1 > '\001')
      return this.gaspTable = new char[0]; 
    char c2 = charBuffer.get();
    if ('\004' + c2 * '\004' > getTableSize(1734439792))
      return this.gaspTable = new char[0]; 
    this.gaspTable = new char['\002' * c2];
    charBuffer.get(this.gaspTable);
    return this.gaspTable;
  }
  
  public boolean useAAForPtSize(int paramInt) {
    char[] arrayOfChar = getGaspTable();
    if (arrayOfChar.length > 0) {
      for (boolean bool = false; bool < arrayOfChar.length; bool += true) {
        if (paramInt <= arrayOfChar[bool])
          return ((arrayOfChar[bool + true] & 0x2) != '\000'); 
      } 
      return true;
    } 
    return (this.style == 1) ? true : ((paramInt <= 8 || paramInt >= 18));
  }
  
  public boolean hasSupplementaryChars() { return ((TrueTypeGlyphMapper)getMapper()).hasSupplementaryChars(); }
  
  public String toString() { return "** TrueType Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + getPublicFileName(); }
  
  static class DirectoryEntry {
    int tag;
    
    int offset;
    
    int length;
  }
  
  private static class TTDisposerRecord implements DisposerRecord {
    FileChannel channel = null;
    
    private TTDisposerRecord() {}
    
    public void dispose() {
      try {
        if (this.channel != null)
          this.channel.close(); 
      } catch (IOException iOException) {
      
      } finally {
        this.channel = null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\TrueTypeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */