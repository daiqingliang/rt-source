package sun.awt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import sun.font.CompositeFontDescriptor;
import sun.font.FontUtilities;
import sun.font.SunFontManager;
import sun.util.logging.PlatformLogger;

public abstract class FontConfiguration {
  protected static String osVersion;
  
  protected static String osName;
  
  protected static String encoding;
  
  protected static Locale startupLocale = null;
  
  protected static Hashtable localeMap = null;
  
  private static FontConfiguration fontConfig;
  
  private static PlatformLogger logger;
  
  protected static boolean isProperties = true;
  
  protected SunFontManager fontManager;
  
  protected boolean preferLocaleFonts;
  
  protected boolean preferPropFonts;
  
  private File fontConfigFile;
  
  private boolean foundOsSpecificFile;
  
  private boolean inited;
  
  private String javaLib;
  
  private static short stringIDNum;
  
  private static short[] stringIDs;
  
  private static StringBuilder stringTable;
  
  public static boolean verbose;
  
  private short initELC = -1;
  
  private Locale initLocale;
  
  private String initEncoding;
  
  private String alphabeticSuffix;
  
  private short[][][] compFontNameIDs = new short[5][4][];
  
  private int[][][] compExclusions = new int[5][][];
  
  private int[] compCoreNum = new int[5];
  
  private Set<Short> coreFontNameIDs = new HashSet();
  
  private Set<Short> fallbackFontNameIDs = new HashSet();
  
  protected static final int NUM_FONTS = 5;
  
  protected static final int NUM_STYLES = 4;
  
  protected static final String[] fontNames = { "serif", "sansserif", "monospaced", "dialog", "dialoginput" };
  
  protected static final String[] publicFontNames = { "Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput" };
  
  protected static final String[] styleNames = { "plain", "bold", "italic", "bolditalic" };
  
  protected static String[] installedFallbackFontFiles = null;
  
  protected HashMap reorderMap = null;
  
  private Hashtable charsetRegistry = new Hashtable(5);
  
  private FontDescriptor[][][] fontDescriptors = new FontDescriptor[5][4][];
  
  HashMap<String, Boolean> existsMap;
  
  private int numCoreFonts = -1;
  
  private String[] componentFonts = null;
  
  HashMap<String, String> filenamesMap = new HashMap();
  
  HashSet<String> coreFontFileNames = new HashSet();
  
  private static final int HEAD_LENGTH = 20;
  
  private static final int INDEX_scriptIDs = 0;
  
  private static final int INDEX_scriptFonts = 1;
  
  private static final int INDEX_elcIDs = 2;
  
  private static final int INDEX_sequences = 3;
  
  private static final int INDEX_fontfileNameIDs = 4;
  
  private static final int INDEX_componentFontNameIDs = 5;
  
  private static final int INDEX_filenames = 6;
  
  private static final int INDEX_awtfontpaths = 7;
  
  private static final int INDEX_exclusions = 8;
  
  private static final int INDEX_proportionals = 9;
  
  private static final int INDEX_scriptFontsMotif = 10;
  
  private static final int INDEX_alphabeticSuffix = 11;
  
  private static final int INDEX_stringIDs = 12;
  
  private static final int INDEX_stringTable = 13;
  
  private static final int INDEX_TABLEEND = 14;
  
  private static final int INDEX_fallbackScripts = 15;
  
  private static final int INDEX_appendedfontpath = 16;
  
  private static final int INDEX_version = 17;
  
  private static short[] head;
  
  private static short[] table_scriptIDs;
  
  private static short[] table_scriptFonts;
  
  private static short[] table_elcIDs;
  
  private static short[] table_sequences;
  
  private static short[] table_fontfileNameIDs;
  
  private static short[] table_componentFontNameIDs;
  
  private static short[] table_filenames;
  
  protected static short[] table_awtfontpaths;
  
  private static short[] table_exclusions;
  
  private static short[] table_proportionals;
  
  private static short[] table_scriptFontsMotif;
  
  private static short[] table_alphabeticSuffix;
  
  private static short[] table_stringIDs;
  
  private static char[] table_stringTable;
  
  private HashMap<String, Short> reorderScripts;
  
  private static String[] stringCache;
  
  private static final int[] EMPTY_INT_ARRAY = new int[0];
  
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private static final short[] EMPTY_SHORT_ARRAY = new short[0];
  
  private static final String UNDEFINED_COMPONENT_FONT = "unknown";
  
  public FontConfiguration(SunFontManager paramSunFontManager) {
    if (FontUtilities.debugFonts())
      FontUtilities.getLogger().info("Creating standard Font Configuration"); 
    if (FontUtilities.debugFonts() && logger == null)
      logger = PlatformLogger.getLogger("sun.awt.FontConfiguration"); 
    this.fontManager = paramSunFontManager;
    setOsNameAndVersion();
    setEncoding();
    findFontConfigFile();
  }
  
  public boolean init() {
    if (!this.inited) {
      this.preferLocaleFonts = false;
      this.preferPropFonts = false;
      setFontConfiguration();
      readFontConfigFile(this.fontConfigFile);
      initFontConfig();
      this.inited = true;
    } 
    return true;
  }
  
  public FontConfiguration(SunFontManager paramSunFontManager, boolean paramBoolean1, boolean paramBoolean2) {
    this.fontManager = paramSunFontManager;
    if (FontUtilities.debugFonts())
      FontUtilities.getLogger().info("Creating alternate Font Configuration"); 
    this.preferLocaleFonts = paramBoolean1;
    this.preferPropFonts = paramBoolean2;
    initFontConfig();
  }
  
  protected void setOsNameAndVersion() {
    osName = System.getProperty("os.name");
    osVersion = System.getProperty("os.version");
  }
  
  private void setEncoding() {
    encoding = Charset.defaultCharset().name();
    startupLocale = SunToolkit.getStartupLocale();
  }
  
  public boolean foundOsSpecificFile() { return this.foundOsSpecificFile; }
  
  public boolean fontFilesArePresent() {
    init();
    short s1 = this.compFontNameIDs[0][0][0];
    short s2 = getComponentFileID(s1);
    final String fileName = mapFileName(getComponentFileName(s2));
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              File file = new File(fileName);
              return Boolean.valueOf(file.exists());
            } catch (Exception exception) {
              return Boolean.valueOf(false);
            } 
          }
        });
    return bool.booleanValue();
  }
  
  private void findFontConfigFile() {
    this.foundOsSpecificFile = true;
    String str1 = System.getProperty("java.home");
    if (str1 == null)
      throw new Error("java.home property not set"); 
    this.javaLib = str1 + File.separator + "lib";
    String str2 = System.getProperty("sun.awt.fontconfig");
    if (str2 != null) {
      this.fontConfigFile = new File(str2);
    } else {
      this.fontConfigFile = findFontConfigFile(this.javaLib);
    } 
  }
  
  private void readFontConfigFile(File paramFile) {
    getInstalledFallbackFonts(this.javaLib);
    if (paramFile != null)
      try {
        FileInputStream fileInputStream = new FileInputStream(paramFile.getPath());
        if (isProperties) {
          loadProperties(fileInputStream);
        } else {
          loadBinary(fileInputStream);
        } 
        fileInputStream.close();
        if (FontUtilities.debugFonts())
          logger.config("Read logical font configuration from " + paramFile); 
      } catch (IOException iOException) {
        if (FontUtilities.debugFonts())
          logger.config("Failed to read logical font configuration from " + paramFile); 
      }  
    String str = getVersion();
    if (!"1".equals(str) && FontUtilities.debugFonts())
      logger.config("Unsupported fontconfig version: " + str); 
  }
  
  protected void getInstalledFallbackFonts(String paramString) {
    String str = paramString + File.separator + "fonts" + File.separator + "fallback";
    File file = new File(str);
    if (file.exists() && file.isDirectory()) {
      String[] arrayOfString1 = file.list(this.fontManager.getTrueTypeFilter());
      String[] arrayOfString2 = file.list(this.fontManager.getType1Filter());
      byte b1 = (arrayOfString1 == null) ? 0 : arrayOfString1.length;
      byte b2 = (arrayOfString2 == null) ? 0 : arrayOfString2.length;
      boolean bool = b1 + b2;
      if (b1 + b2 == 0)
        return; 
      installedFallbackFontFiles = new String[bool];
      byte b3;
      for (b3 = 0; b3 < b1; b3++)
        installedFallbackFontFiles[b3] = file + File.separator + arrayOfString1[b3]; 
      for (b3 = 0; b3 < b2; b3++)
        installedFallbackFontFiles[b3 + b1] = file + File.separator + arrayOfString2[b3]; 
      this.fontManager.registerFontsInDir(str);
    } 
  }
  
  private File findImpl(String paramString) {
    File file = new File(paramString + ".properties");
    if (file.canRead()) {
      isProperties = true;
      return file;
    } 
    file = new File(paramString + ".bfc");
    if (file.canRead()) {
      isProperties = false;
      return file;
    } 
    return null;
  }
  
  private File findFontConfigFile(String paramString) {
    String str1 = paramString + File.separator + "fontconfig";
    String str2 = null;
    if (osVersion != null && osName != null) {
      File file1 = findImpl(str1 + "." + osName + "." + osVersion);
      if (file1 != null)
        return file1; 
      int i = osVersion.indexOf(".");
      if (i != -1) {
        str2 = osVersion.substring(0, osVersion.indexOf("."));
        file1 = findImpl(str1 + "." + osName + "." + str2);
        if (file1 != null)
          return file1; 
      } 
    } 
    if (osName != null) {
      File file1 = findImpl(str1 + "." + osName);
      if (file1 != null)
        return file1; 
    } 
    if (osVersion != null) {
      File file1 = findImpl(str1 + "." + osVersion);
      if (file1 != null)
        return file1; 
      if (str2 != null) {
        file1 = findImpl(str1 + "." + str2);
        if (file1 != null)
          return file1; 
      } 
    } 
    this.foundOsSpecificFile = false;
    File file = findImpl(str1);
    return (file != null) ? file : null;
  }
  
  public static void loadBinary(InputStream paramInputStream) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    head = readShortTable(dataInputStream, 20);
    int[] arrayOfInt = new int[14];
    int i;
    for (i = 0; i < 14; i++)
      arrayOfInt[i] = head[i + true] - head[i]; 
    table_scriptIDs = readShortTable(dataInputStream, arrayOfInt[0]);
    table_scriptFonts = readShortTable(dataInputStream, arrayOfInt[1]);
    table_elcIDs = readShortTable(dataInputStream, arrayOfInt[2]);
    table_sequences = readShortTable(dataInputStream, arrayOfInt[3]);
    table_fontfileNameIDs = readShortTable(dataInputStream, arrayOfInt[4]);
    table_componentFontNameIDs = readShortTable(dataInputStream, arrayOfInt[5]);
    table_filenames = readShortTable(dataInputStream, arrayOfInt[6]);
    table_awtfontpaths = readShortTable(dataInputStream, arrayOfInt[7]);
    table_exclusions = readShortTable(dataInputStream, arrayOfInt[8]);
    table_proportionals = readShortTable(dataInputStream, arrayOfInt[9]);
    table_scriptFontsMotif = readShortTable(dataInputStream, arrayOfInt[10]);
    table_alphabeticSuffix = readShortTable(dataInputStream, arrayOfInt[11]);
    table_stringIDs = readShortTable(dataInputStream, arrayOfInt[12]);
    stringCache = new String[table_stringIDs.length + 1];
    i = arrayOfInt[13];
    byte[] arrayOfByte = new byte[i * 2];
    table_stringTable = new char[i];
    dataInputStream.read(arrayOfByte);
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i)
      table_stringTable[b1++] = (char)(arrayOfByte[b2++] << 8 | arrayOfByte[b2++] & 0xFF); 
    if (verbose)
      dump(); 
  }
  
  public static void saveBinary(OutputStream paramOutputStream) throws IOException {
    sanityCheck();
    DataOutputStream dataOutputStream = new DataOutputStream(paramOutputStream);
    writeShortTable(dataOutputStream, head);
    writeShortTable(dataOutputStream, table_scriptIDs);
    writeShortTable(dataOutputStream, table_scriptFonts);
    writeShortTable(dataOutputStream, table_elcIDs);
    writeShortTable(dataOutputStream, table_sequences);
    writeShortTable(dataOutputStream, table_fontfileNameIDs);
    writeShortTable(dataOutputStream, table_componentFontNameIDs);
    writeShortTable(dataOutputStream, table_filenames);
    writeShortTable(dataOutputStream, table_awtfontpaths);
    writeShortTable(dataOutputStream, table_exclusions);
    writeShortTable(dataOutputStream, table_proportionals);
    writeShortTable(dataOutputStream, table_scriptFontsMotif);
    writeShortTable(dataOutputStream, table_alphabeticSuffix);
    writeShortTable(dataOutputStream, table_stringIDs);
    dataOutputStream.writeChars(new String(table_stringTable));
    paramOutputStream.close();
    if (verbose)
      dump(); 
  }
  
  public static void loadProperties(InputStream paramInputStream) throws IOException {
    stringIDNum = 1;
    stringIDs = new short[1000];
    stringTable = new StringBuilder(4096);
    if (verbose && logger == null)
      logger = PlatformLogger.getLogger("sun.awt.FontConfiguration"); 
    (new PropertiesHandler()).load(paramInputStream);
    stringIDs = null;
    stringTable = null;
  }
  
  private void initFontConfig() {
    this.initLocale = startupLocale;
    this.initEncoding = encoding;
    if (this.preferLocaleFonts && !willReorderForStartupLocale())
      this.preferLocaleFonts = false; 
    this.initELC = getInitELC();
    initAllComponentFonts();
  }
  
  private short getInitELC() {
    if (this.initELC != -1)
      return this.initELC; 
    HashMap hashMap = new HashMap();
    for (byte b = 0; b < table_elcIDs.length; b++)
      hashMap.put(getString(table_elcIDs[b]), Integer.valueOf(b)); 
    String str1 = this.initLocale.getLanguage();
    String str2 = this.initLocale.getCountry();
    String str3;
    if (hashMap.containsKey(str3 = this.initEncoding + "." + str1 + "." + str2) || hashMap.containsKey(str3 = this.initEncoding + "." + str1) || hashMap.containsKey(str3 = this.initEncoding)) {
      this.initELC = ((Integer)hashMap.get(str3)).shortValue();
    } else {
      this.initELC = ((Integer)hashMap.get("NULL.NULL.NULL")).shortValue();
    } 
    for (boolean bool = false; bool < table_alphabeticSuffix.length; bool += true) {
      if (this.initELC == table_alphabeticSuffix[bool]) {
        this.alphabeticSuffix = getString(table_alphabeticSuffix[bool + true]);
        return this.initELC;
      } 
    } 
    return this.initELC;
  }
  
  private void initAllComponentFonts() {
    short[] arrayOfShort = getFallbackScripts();
    for (byte b = 0; b < 5; b++) {
      short[] arrayOfShort1 = getCoreScripts(b);
      this.compCoreNum[b] = arrayOfShort1.length;
      int[][] arrayOfInt = new int[arrayOfShort1.length][];
      byte b1;
      for (b1 = 0; b1 < arrayOfShort1.length; b1++)
        arrayOfInt[b1] = getExclusionRanges(arrayOfShort1[b1]); 
      this.compExclusions[b] = arrayOfInt;
      for (b1 = 0; b1 < 4; b1++) {
        short[] arrayOfShort2 = new short[arrayOfShort1.length + arrayOfShort.length];
        byte b2;
        for (b2 = 0; b2 < arrayOfShort1.length; b2++) {
          arrayOfShort2[b2] = getComponentFontID(arrayOfShort1[b2], b, b1);
          if (this.preferLocaleFonts && localeMap != null && this.fontManager.usingAlternateFontforJALocales())
            arrayOfShort2[b2] = remapLocaleMap(b, b1, arrayOfShort1[b2], arrayOfShort2[b2]); 
          if (this.preferPropFonts)
            arrayOfShort2[b2] = remapProportional(b, arrayOfShort2[b2]); 
          this.coreFontNameIDs.add(Short.valueOf(arrayOfShort2[b2]));
        } 
        for (byte b3 = 0; b3 < arrayOfShort.length; b3++) {
          short s = getComponentFontID(arrayOfShort[b3], b, b1);
          if (this.preferLocaleFonts && localeMap != null && this.fontManager.usingAlternateFontforJALocales())
            s = remapLocaleMap(b, b1, arrayOfShort[b3], s); 
          if (this.preferPropFonts)
            s = remapProportional(b, s); 
          if (!contains(arrayOfShort2, s, b2)) {
            this.fallbackFontNameIDs.add(Short.valueOf(s));
            arrayOfShort2[b2++] = s;
          } 
        } 
        if (b2 < arrayOfShort2.length) {
          short[] arrayOfShort3 = new short[b2];
          System.arraycopy(arrayOfShort2, 0, arrayOfShort3, 0, b2);
          arrayOfShort2 = arrayOfShort3;
        } 
        this.compFontNameIDs[b][b1] = arrayOfShort2;
      } 
    } 
  }
  
  private short remapLocaleMap(int paramInt1, int paramInt2, short paramShort1, short paramShort2) {
    String str1 = getString(table_scriptIDs[paramShort1]);
    String str2 = (String)localeMap.get(str1);
    if (str2 == null) {
      String str3 = fontNames[paramInt1];
      String str4 = styleNames[paramInt2];
      str2 = (String)localeMap.get(str3 + "." + str4 + "." + str1);
    } 
    if (str2 == null)
      return paramShort2; 
    for (byte b = 0; b < table_componentFontNameIDs.length; b++) {
      String str = getString(table_componentFontNameIDs[b]);
      if (str2.equalsIgnoreCase(str)) {
        paramShort2 = (short)b;
        break;
      } 
    } 
    return paramShort2;
  }
  
  public static boolean hasMonoToPropMap() { return (table_proportionals != null && table_proportionals.length != 0); }
  
  private short remapProportional(int paramInt, short paramShort) {
    if (this.preferPropFonts && table_proportionals.length != 0 && paramInt != 2 && paramInt != 4)
      for (boolean bool = false; bool < table_proportionals.length; bool += true) {
        if (table_proportionals[bool] == paramShort)
          return table_proportionals[bool + true]; 
      }  
    return paramShort;
  }
  
  public static boolean isLogicalFontFamilyName(String paramString) { return isLogicalFontFamilyNameLC(paramString.toLowerCase(Locale.ENGLISH)); }
  
  public static boolean isLogicalFontFamilyNameLC(String paramString) {
    for (byte b = 0; b < fontNames.length; b++) {
      if (paramString.equals(fontNames[b]))
        return true; 
    } 
    return false;
  }
  
  private static boolean isLogicalFontStyleName(String paramString) {
    for (byte b = 0; b < styleNames.length; b++) {
      if (paramString.equals(styleNames[b]))
        return true; 
    } 
    return false;
  }
  
  public static boolean isLogicalFontFaceName(String paramString) { return isLogicalFontFaceNameLC(paramString.toLowerCase(Locale.ENGLISH)); }
  
  public static boolean isLogicalFontFaceNameLC(String paramString) {
    int i = paramString.indexOf('.');
    if (i >= 0) {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      return (isLogicalFontFamilyName(str1) && isLogicalFontStyleName(str2));
    } 
    return isLogicalFontFamilyName(paramString);
  }
  
  protected static int getFontIndex(String paramString) { return getArrayIndex(fontNames, paramString); }
  
  protected static int getStyleIndex(String paramString) { return getArrayIndex(styleNames, paramString); }
  
  private static int getArrayIndex(String[] paramArrayOfString, String paramString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equals(paramArrayOfString[b]))
        return b; 
    } 
    assert false;
    return 0;
  }
  
  protected static int getStyleIndex(int paramInt) {
    switch (paramInt) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 2:
        return 2;
      case 3:
        return 3;
    } 
    return 0;
  }
  
  protected static String getFontName(int paramInt) { return fontNames[paramInt]; }
  
  protected static String getStyleName(int paramInt) { return styleNames[paramInt]; }
  
  public static String getLogicalFontFaceName(String paramString, int paramInt) {
    assert isLogicalFontFamilyName(paramString);
    return paramString.toLowerCase(Locale.ENGLISH) + "." + getStyleString(paramInt);
  }
  
  public static String getStyleString(int paramInt) { return getStyleName(getStyleIndex(paramInt)); }
  
  public abstract String getFallbackFamilyName(String paramString1, String paramString2);
  
  protected String getCompatibilityFamilyName(String paramString) {
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    return paramString.equals("timesroman") ? "serif" : (paramString.equals("helvetica") ? "sansserif" : (paramString.equals("courier") ? "monospaced" : null));
  }
  
  protected String mapFileName(String paramString) { return paramString; }
  
  protected abstract void initReorderMap();
  
  private void shuffle(String[] paramArrayOfString, int paramInt1, int paramInt2) {
    if (paramInt2 >= paramInt1)
      return; 
    String str = paramArrayOfString[paramInt1];
    for (int i = paramInt1; i > paramInt2; i--)
      paramArrayOfString[i] = paramArrayOfString[i - 1]; 
    paramArrayOfString[paramInt2] = str;
  }
  
  public static boolean willReorderForStartupLocale() { return (getReorderSequence() != null); }
  
  private static Object getReorderSequence() {
    if (fontConfig.reorderMap == null)
      fontConfig.initReorderMap(); 
    HashMap hashMap = fontConfig.reorderMap;
    String str1 = startupLocale.getLanguage();
    String str2 = startupLocale.getCountry();
    Object object = hashMap.get(encoding + "." + str1 + "." + str2);
    if (object == null)
      object = hashMap.get(encoding + "." + str1); 
    if (object == null)
      object = hashMap.get(encoding); 
    return object;
  }
  
  private void reorderSequenceForLocale(String[] paramArrayOfString) {
    Object object = getReorderSequence();
    if (object instanceof String) {
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        if (paramArrayOfString[b].equals(object)) {
          shuffle(paramArrayOfString, b, 0);
          return;
        } 
      } 
    } else if (object instanceof String[]) {
      String[] arrayOfString = (String[])object;
      for (byte b = 0; b < arrayOfString.length; b++) {
        for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
          if (paramArrayOfString[b1].equals(arrayOfString[b]))
            shuffle(paramArrayOfString, b1, b); 
        } 
      } 
    } 
  }
  
  private static Vector splitSequence(String paramString) {
    Vector vector = new Vector();
    int i;
    int j;
    for (i = 0; (j = paramString.indexOf(',', i)) >= 0; i = j + 1)
      vector.add(paramString.substring(i, j)); 
    if (paramString.length() > i)
      vector.add(paramString.substring(i, paramString.length())); 
    return vector;
  }
  
  protected String[] split(String paramString) {
    Vector vector = splitSequence(paramString);
    return (String[])vector.toArray(new String[0]);
  }
  
  public FontDescriptor[] getFontDescriptors(String paramString, int paramInt) {
    assert isLogicalFontFamilyName(paramString);
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    int i = getFontIndex(paramString);
    int j = getStyleIndex(paramInt);
    return getFontDescriptors(i, j);
  }
  
  private FontDescriptor[] getFontDescriptors(int paramInt1, int paramInt2) {
    FontDescriptor[] arrayOfFontDescriptor = this.fontDescriptors[paramInt1][paramInt2];
    if (arrayOfFontDescriptor == null) {
      arrayOfFontDescriptor = buildFontDescriptors(paramInt1, paramInt2);
      this.fontDescriptors[paramInt1][paramInt2] = arrayOfFontDescriptor;
    } 
    return arrayOfFontDescriptor;
  }
  
  protected FontDescriptor[] buildFontDescriptors(int paramInt1, int paramInt2) {
    String str1 = fontNames[paramInt1];
    String str2 = styleNames[paramInt2];
    short[] arrayOfShort1 = getCoreScripts(paramInt1);
    short[] arrayOfShort2 = this.compFontNameIDs[paramInt1][paramInt2];
    String[] arrayOfString1 = new String[arrayOfShort1.length];
    String[] arrayOfString2 = new String[arrayOfShort1.length];
    for (byte b1 = 0; b1 < arrayOfString1.length; b1++) {
      arrayOfString2[b1] = getComponentFontName(arrayOfShort2[b1]);
      arrayOfString1[b1] = getScriptName(arrayOfShort1[b1]);
      if (this.alphabeticSuffix != null && "alphabetic".equals(arrayOfString1[b1]))
        arrayOfString1[b1] = arrayOfString1[b1] + "/" + this.alphabeticSuffix; 
    } 
    int[][] arrayOfInt = this.compExclusions[paramInt1];
    FontDescriptor[] arrayOfFontDescriptor = new FontDescriptor[arrayOfString2.length];
    for (byte b2 = 0; b2 < arrayOfString2.length; b2++) {
      String str3 = makeAWTFontName(arrayOfString2[b2], arrayOfString1[b2]);
      String str4 = getEncoding(arrayOfString2[b2], arrayOfString1[b2]);
      if (str4 == null)
        str4 = "default"; 
      CharsetEncoder charsetEncoder = getFontCharsetEncoder(str4.trim(), str3);
      int[] arrayOfInt1 = arrayOfInt[b2];
      arrayOfFontDescriptor[b2] = new FontDescriptor(str3, charsetEncoder, arrayOfInt1);
    } 
    return arrayOfFontDescriptor;
  }
  
  protected String makeAWTFontName(String paramString1, String paramString2) { return paramString1; }
  
  protected abstract String getEncoding(String paramString1, String paramString2);
  
  private CharsetEncoder getFontCharsetEncoder(final String charsetName, String paramString2) {
    Charset charset = null;
    if (paramString1.equals("default")) {
      charset = (Charset)this.charsetRegistry.get(paramString2);
    } else {
      charset = (Charset)this.charsetRegistry.get(paramString1);
    } 
    if (charset != null)
      return charset.newEncoder(); 
    if (!paramString1.startsWith("sun.awt.") && !paramString1.equals("default")) {
      charset = Charset.forName(paramString1);
    } else {
      Class clazz = (Class)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              try {
                return Class.forName(charsetName, true, ClassLoader.getSystemClassLoader());
              } catch (ClassNotFoundException classNotFoundException) {
                return null;
              } 
            }
          });
      if (clazz != null)
        try {
          charset = (Charset)clazz.newInstance();
        } catch (Exception exception) {} 
    } 
    if (charset == null)
      charset = getDefaultFontCharset(paramString2); 
    if (paramString1.equals("default")) {
      this.charsetRegistry.put(paramString2, charset);
    } else {
      this.charsetRegistry.put(paramString1, charset);
    } 
    return charset.newEncoder();
  }
  
  protected abstract Charset getDefaultFontCharset(String paramString);
  
  public HashSet<String> getAWTFontPathSet() { return null; }
  
  public CompositeFontDescriptor[] get2DCompositeFontInfo() {
    CompositeFontDescriptor[] arrayOfCompositeFontDescriptor = new CompositeFontDescriptor[20];
    String str1 = this.fontManager.getDefaultFontFile();
    String str2 = this.fontManager.getDefaultFontFaceName();
    for (byte b = 0; b < 5; b++) {
      String str = publicFontNames[b];
      int[][] arrayOfInt = this.compExclusions[b];
      int i = 0;
      for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
        i += arrayOfInt[b1].length; 
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[arrayOfInt.length];
      byte b2 = 0;
      boolean bool = false;
      byte b3;
      for (b3 = 0; b3 < arrayOfInt.length; b3++) {
        int[] arrayOfInt3 = arrayOfInt[b3];
        byte b4 = 0;
        while (b4 < arrayOfInt3.length) {
          int j = arrayOfInt3[b4];
          arrayOfInt1[b2++] = arrayOfInt3[b4++];
          arrayOfInt1[b2++] = arrayOfInt3[b4++];
        } 
        arrayOfInt2[b3] = b2;
      } 
      for (b3 = 0; b3 < 4; b3++) {
        int j = this.compFontNameIDs[b][b3].length;
        boolean bool1 = false;
        if (installedFallbackFontFiles != null)
          j += installedFallbackFontFiles.length; 
        String str3 = str + "." + styleNames[b3];
        String[] arrayOfString1 = new String[j];
        String[] arrayOfString2 = new String[j];
        int k;
        for (k = 0; k < this.compFontNameIDs[b][b3].length; k++) {
          short s1 = this.compFontNameIDs[b][b3][k];
          short s2 = getComponentFileID(s1);
          arrayOfString1[k] = getFaceNameFromComponentFontName(getComponentFontName(s1));
          arrayOfString2[k] = mapFileName(getComponentFileName(s2));
          if (arrayOfString2[k] == null || needToSearchForFile(arrayOfString2[k]))
            arrayOfString2[k] = getFileNameFromComponentFontName(getComponentFontName(s1)); 
          if (!bool1 && str1.equals(arrayOfString2[k]))
            bool1 = true; 
        } 
        if (!bool1) {
          int m = 0;
          if (installedFallbackFontFiles != null)
            m = installedFallbackFontFiles.length; 
          if (k + m == j) {
            String[] arrayOfString3 = new String[j + 1];
            System.arraycopy(arrayOfString1, 0, arrayOfString3, 0, k);
            arrayOfString1 = arrayOfString3;
            String[] arrayOfString4 = new String[j + 1];
            System.arraycopy(arrayOfString2, 0, arrayOfString4, 0, k);
            arrayOfString2 = arrayOfString4;
          } 
          arrayOfString1[k] = str2;
          arrayOfString2[k] = str1;
          k++;
        } 
        if (installedFallbackFontFiles != null)
          for (byte b4 = 0; b4 < installedFallbackFontFiles.length; b4++) {
            arrayOfString1[k] = null;
            arrayOfString2[k] = installedFallbackFontFiles[b4];
            k++;
          }  
        if (k < j) {
          String[] arrayOfString3 = new String[k];
          System.arraycopy(arrayOfString1, 0, arrayOfString3, 0, k);
          arrayOfString1 = arrayOfString3;
          String[] arrayOfString4 = new String[k];
          System.arraycopy(arrayOfString2, 0, arrayOfString4, 0, k);
          arrayOfString2 = arrayOfString4;
        } 
        int[] arrayOfInt3 = arrayOfInt2;
        if (k != arrayOfInt3.length) {
          int m = arrayOfInt2.length;
          arrayOfInt3 = new int[k];
          System.arraycopy(arrayOfInt2, 0, arrayOfInt3, 0, m);
          for (int n = m; n < k; n++)
            arrayOfInt3[n] = arrayOfInt1.length; 
        } 
        arrayOfCompositeFontDescriptor[b * 4 + b3] = new CompositeFontDescriptor(str3, this.compCoreNum[b], arrayOfString1, arrayOfString2, arrayOfInt1, arrayOfInt3);
      } 
    } 
    return arrayOfCompositeFontDescriptor;
  }
  
  protected abstract String getFaceNameFromComponentFontName(String paramString);
  
  protected abstract String getFileNameFromComponentFontName(String paramString);
  
  public boolean needToSearchForFile(String paramString) {
    if (!FontUtilities.isLinux)
      return false; 
    if (this.existsMap == null)
      this.existsMap = new HashMap(); 
    Boolean bool = (Boolean)this.existsMap.get(paramString);
    if (bool == null) {
      getNumberCoreFonts();
      if (!this.coreFontFileNames.contains(paramString)) {
        bool = Boolean.TRUE;
      } else {
        bool = Boolean.valueOf((new File(paramString)).exists());
        this.existsMap.put(paramString, bool);
        if (FontUtilities.debugFonts() && bool == Boolean.FALSE)
          logger.warning("Couldn't locate font file " + paramString); 
      } 
    } 
    return (bool == Boolean.FALSE);
  }
  
  public int getNumberCoreFonts() {
    if (this.numCoreFonts == -1) {
      this.numCoreFonts = this.coreFontNameIDs.size();
      Short[] arrayOfShort1 = new Short[0];
      Short[] arrayOfShort2 = (Short[])this.coreFontNameIDs.toArray(arrayOfShort1);
      Short[] arrayOfShort3 = (Short[])this.fallbackFontNameIDs.toArray(arrayOfShort1);
      int i = 0;
      byte b1;
      for (b1 = 0; b1 < arrayOfShort3.length; b1++) {
        if (this.coreFontNameIDs.contains(arrayOfShort3[b1])) {
          arrayOfShort3[b1] = null;
        } else {
          i++;
        } 
      } 
      this.componentFonts = new String[this.numCoreFonts + i];
      Object object = null;
      for (b1 = 0; b1 < arrayOfShort2.length; b1++) {
        short s1 = arrayOfShort2[b1].shortValue();
        short s2 = getComponentFileID(s1);
        this.componentFonts[b1] = getComponentFontName(s1);
        String str = getComponentFileName(s2);
        if (str != null)
          this.coreFontFileNames.add(str); 
        this.filenamesMap.put(this.componentFonts[b1], mapFileName(str));
      } 
      for (byte b2 = 0; b2 < arrayOfShort3.length; b2++) {
        if (arrayOfShort3[b2] != null) {
          short s1 = arrayOfShort3[b2].shortValue();
          short s2 = getComponentFileID(s1);
          this.componentFonts[b1] = getComponentFontName(s1);
          this.filenamesMap.put(this.componentFonts[b1], mapFileName(getComponentFileName(s2)));
          b1++;
        } 
      } 
    } 
    return this.numCoreFonts;
  }
  
  public String[] getPlatformFontNames() {
    if (this.numCoreFonts == -1)
      getNumberCoreFonts(); 
    return this.componentFonts;
  }
  
  public String getFileNameFromPlatformName(String paramString) { return (String)this.filenamesMap.get(paramString); }
  
  public String getExtraFontPath() { return getString(head[16]); }
  
  public String getVersion() { return getString(head[17]); }
  
  protected static FontConfiguration getFontConfiguration() { return fontConfig; }
  
  protected void setFontConfiguration() { fontConfig = this; }
  
  private static void sanityCheck() {
    byte b1 = 0;
    String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty("os.name"); }
        });
    byte b2;
    for (b2 = 1; b2 < table_filenames.length; b2++) {
      if (table_filenames[b2] == -1)
        if (str.contains("Windows")) {
          System.err.println("\n Error: <filename." + getString(table_componentFontNameIDs[b2]) + "> entry is missing!!!");
          b1++;
        } else if (verbose && !isEmpty(table_filenames)) {
          System.err.println("\n Note: 'filename' entry is undefined for \"" + getString(table_componentFontNameIDs[b2]) + "\"");
        }  
    } 
    for (b2 = 0; b2 < table_scriptIDs.length; b2++) {
      short s = table_scriptFonts[b2];
      if (s == 0) {
        System.out.println("\n Error: <allfonts." + getString(table_scriptIDs[b2]) + "> entry is missing!!!");
        b1++;
      } else if (s < 0) {
        s = (short)-s;
        for (byte b = 0; b < 5; b++) {
          for (byte b3 = 0; b3 < 4; b3++) {
            short s1 = b * 4 + b3;
            short s2 = table_scriptFonts[s + s1];
            if (s2 == 0) {
              System.err.println("\n Error: <" + getFontName(b) + "." + getStyleName(b3) + "." + getString(table_scriptIDs[b2]) + "> entry is missing!!!");
              b1++;
            } 
          } 
        } 
      } 
    } 
    if ("SunOS".equals(str))
      for (b2 = 0; b2 < table_awtfontpaths.length; b2++) {
        if (table_awtfontpaths[b2] == 0) {
          String str1 = getString(table_scriptIDs[b2]);
          if (!str1.contains("lucida") && !str1.contains("dingbats") && !str1.contains("symbol")) {
            System.err.println("\nError: <awtfontpath." + str1 + "> entry is missing!!!");
            b1++;
          } 
        } 
      }  
    if (b1 != 0) {
      System.err.println("!!THERE ARE " + b1 + " ERROR(S) IN THE FONTCONFIG FILE, PLEASE CHECK ITS CONTENT!!\n");
      System.exit(1);
    } 
  }
  
  private static boolean isEmpty(short[] paramArrayOfShort) {
    for (short s : paramArrayOfShort) {
      if (s != -1)
        return false; 
    } 
    return true;
  }
  
  private static void dump() {
    System.out.println("\n----Head Table------------");
    byte b1;
    for (b1 = 0; b1 < 20; b1++)
      System.out.println("  " + b1 + " : " + head[b1]); 
    System.out.println("\n----scriptIDs-------------");
    printTable(table_scriptIDs, 0);
    System.out.println("\n----scriptFonts----------------");
    for (b1 = 0; b1 < table_scriptIDs.length; b1++) {
      short s = table_scriptFonts[b1];
      if (s >= 0)
        System.out.println("  allfonts." + getString(table_scriptIDs[b1]) + "=" + getString(table_componentFontNameIDs[s])); 
    } 
    for (b1 = 0; b1 < table_scriptIDs.length; b1++) {
      short s = table_scriptFonts[b1];
      if (s < 0) {
        s = (short)-s;
        for (byte b = 0; b < 5; b++) {
          for (byte b3 = 0; b3 < 4; b3++) {
            short s1 = b * 4 + b3;
            short s2 = table_scriptFonts[s + s1];
            System.out.println("  " + getFontName(b) + "." + getStyleName(b3) + "." + getString(table_scriptIDs[b1]) + "=" + getString(table_componentFontNameIDs[s2]));
          } 
        } 
      } 
    } 
    System.out.println("\n----elcIDs----------------");
    printTable(table_elcIDs, 0);
    System.out.println("\n----sequences-------------");
    for (b1 = 0; b1 < table_elcIDs.length; b1++) {
      System.out.println("  " + b1 + "/" + getString(table_elcIDs[b1]));
      short[] arrayOfShort1 = getShortArray(table_sequences[b1 * 5 + 0]);
      for (byte b = 0; b < arrayOfShort1.length; b++)
        System.out.println("     " + getString(table_scriptIDs[arrayOfShort1[b]])); 
    } 
    System.out.println("\n----fontfileNameIDs-------");
    printTable(table_fontfileNameIDs, 0);
    System.out.println("\n----componentFontNameIDs--");
    printTable(table_componentFontNameIDs, 1);
    System.out.println("\n----filenames-------------");
    for (b1 = 0; b1 < table_filenames.length; b1++) {
      if (table_filenames[b1] == -1) {
        System.out.println("  " + b1 + " : null");
      } else {
        System.out.println("  " + b1 + " : " + getString(table_fontfileNameIDs[table_filenames[b1]]));
      } 
    } 
    System.out.println("\n----awtfontpaths---------");
    for (b1 = 0; b1 < table_awtfontpaths.length; b1++)
      System.out.println("  " + getString(table_scriptIDs[b1]) + " : " + getString(table_awtfontpaths[b1])); 
    System.out.println("\n----proportionals--------");
    for (b1 = 0; b1 < table_proportionals.length; b1++)
      System.out.println("  " + getString(table_componentFontNameIDs[table_proportionals[b1++]]) + " -> " + getString(table_componentFontNameIDs[table_proportionals[b1]])); 
    b1 = 0;
    System.out.println("\n----alphabeticSuffix----");
    while (b1 < table_alphabeticSuffix.length)
      System.out.println("    " + getString(table_elcIDs[table_alphabeticSuffix[b1++]]) + " -> " + getString(table_alphabeticSuffix[b1++])); 
    System.out.println("\n----String Table---------");
    System.out.println("    stringID:    Num =" + table_stringIDs.length);
    System.out.println("    stringTable: Size=" + (table_stringTable.length * 2));
    System.out.println("\n----fallbackScriptIDs---");
    short[] arrayOfShort = getShortArray(head[15]);
    for (byte b2 = 0; b2 < arrayOfShort.length; b2++)
      System.out.println("  " + getString(table_scriptIDs[arrayOfShort[b2]])); 
    System.out.println("\n----appendedfontpath-----");
    System.out.println("  " + getString(head[16]));
    System.out.println("\n----Version--------------");
    System.out.println("  " + getString(head[17]));
  }
  
  protected static short getComponentFontID(short paramShort, int paramInt1, int paramInt2) {
    short s = table_scriptFonts[paramShort];
    return (s >= 0) ? s : table_scriptFonts[-s + paramInt1 * 4 + paramInt2];
  }
  
  protected static short getComponentFontIDMotif(short paramShort, int paramInt1, int paramInt2) {
    if (table_scriptFontsMotif.length == 0)
      return 0; 
    short s = table_scriptFontsMotif[paramShort];
    return (s >= 0) ? s : table_scriptFontsMotif[-s + paramInt1 * 4 + paramInt2];
  }
  
  private static int[] getExclusionRanges(short paramShort) {
    short s = table_exclusions[paramShort];
    if (s == 0)
      return EMPTY_INT_ARRAY; 
    char[] arrayOfChar = getString(s).toCharArray();
    int[] arrayOfInt = new int[arrayOfChar.length / 2];
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfInt.length; b2++)
      arrayOfInt[b2] = (arrayOfChar[b1++] << '\020') + (arrayOfChar[b1++] & 0xFFFF); 
    return arrayOfInt;
  }
  
  private static boolean contains(short[] paramArrayOfShort, short paramShort, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      if (paramArrayOfShort[b] == paramShort)
        return true; 
    } 
    return false;
  }
  
  protected static String getComponentFontName(short paramShort) { return (paramShort < 0) ? null : getString(table_componentFontNameIDs[paramShort]); }
  
  private static String getComponentFileName(short paramShort) { return (paramShort < 0) ? null : getString(table_fontfileNameIDs[paramShort]); }
  
  private static short getComponentFileID(short paramShort) { return table_filenames[paramShort]; }
  
  private static String getScriptName(short paramShort) { return getString(table_scriptIDs[paramShort]); }
  
  protected short[] getCoreScripts(int paramInt) {
    short s = getInitELC();
    short[] arrayOfShort = getShortArray(table_sequences[s * 5 + paramInt]);
    if (this.preferLocaleFonts) {
      if (this.reorderScripts == null)
        this.reorderScripts = new HashMap(); 
      String[] arrayOfString = new String[arrayOfShort.length];
      byte b;
      for (b = 0; b < arrayOfString.length; b++) {
        arrayOfString[b] = getScriptName(arrayOfShort[b]);
        this.reorderScripts.put(arrayOfString[b], Short.valueOf(arrayOfShort[b]));
      } 
      reorderSequenceForLocale(arrayOfString);
      for (b = 0; b < arrayOfString.length; b++)
        arrayOfShort[b] = ((Short)this.reorderScripts.get(arrayOfString[b])).shortValue(); 
    } 
    return arrayOfShort;
  }
  
  private static short[] getFallbackScripts() { return getShortArray(head[15]); }
  
  private static void printTable(short[] paramArrayOfShort, int paramInt) {
    for (int i = paramInt; i < paramArrayOfShort.length; i++)
      System.out.println("  " + i + " : " + getString(paramArrayOfShort[i])); 
  }
  
  private static short[] readShortTable(DataInputStream paramDataInputStream, int paramInt) throws IOException {
    if (paramInt == 0)
      return EMPTY_SHORT_ARRAY; 
    short[] arrayOfShort = new short[paramInt];
    byte[] arrayOfByte = new byte[paramInt * 2];
    paramDataInputStream.read(arrayOfByte);
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < paramInt)
      arrayOfShort[b1++] = (short)(arrayOfByte[b2++] << 8 | arrayOfByte[b2++] & 0xFF); 
    return arrayOfShort;
  }
  
  private static void writeShortTable(DataOutputStream paramDataOutputStream, short[] paramArrayOfShort) throws IOException {
    for (short s : paramArrayOfShort)
      paramDataOutputStream.writeShort(s); 
  }
  
  private static short[] toList(HashMap<String, Short> paramHashMap) {
    short[] arrayOfShort = new short[paramHashMap.size()];
    Arrays.fill(arrayOfShort, (short)-1);
    for (Map.Entry entry : paramHashMap.entrySet())
      arrayOfShort[((Short)entry.getValue()).shortValue()] = getStringID((String)entry.getKey()); 
    return arrayOfShort;
  }
  
  protected static String getString(short paramShort) {
    if (paramShort == 0)
      return null; 
    if (stringCache[paramShort] == null)
      stringCache[paramShort] = new String(table_stringTable, table_stringIDs[paramShort], table_stringIDs[paramShort + 1] - table_stringIDs[paramShort]); 
    return stringCache[paramShort];
  }
  
  private static short[] getShortArray(short paramShort) {
    String str = getString(paramShort);
    char[] arrayOfChar = str.toCharArray();
    short[] arrayOfShort = new short[arrayOfChar.length];
    for (byte b = 0; b < arrayOfChar.length; b++)
      arrayOfShort[b] = (short)(arrayOfChar[b] & 0xFFFF); 
    return arrayOfShort;
  }
  
  private static short getStringID(String paramString) {
    if (paramString == null)
      return 0; 
    short s1 = (short)stringTable.length();
    stringTable.append(paramString);
    short s2 = (short)stringTable.length();
    stringIDs[stringIDNum] = s1;
    stringIDs[stringIDNum + 1] = s2;
    stringIDNum = (short)(stringIDNum + 1);
    if (stringIDNum + 1 >= stringIDs.length) {
      short[] arrayOfShort = new short[stringIDNum + 1000];
      System.arraycopy(stringIDs, 0, arrayOfShort, 0, stringIDNum);
      stringIDs = arrayOfShort;
    } 
    return (short)(stringIDNum - 1);
  }
  
  private static short getShortArrayID(short[] paramArrayOfShort) {
    char[] arrayOfChar = new char[paramArrayOfShort.length];
    for (byte b = 0; b < paramArrayOfShort.length; b++)
      arrayOfChar[b] = (char)paramArrayOfShort[b]; 
    String str = new String(arrayOfChar);
    return getStringID(str);
  }
  
  static class PropertiesHandler {
    private HashMap<String, Short> scriptIDs;
    
    private HashMap<String, Short> elcIDs;
    
    private HashMap<String, Short> componentFontNameIDs;
    
    private HashMap<String, Short> fontfileNameIDs;
    
    private HashMap<String, Integer> logicalFontIDs;
    
    private HashMap<String, Integer> fontStyleIDs;
    
    private HashMap<Short, Short> filenames;
    
    private HashMap<Short, short[]> sequences;
    
    private HashMap<Short, Short[]> scriptFonts;
    
    private HashMap<Short, Short> scriptAllfonts;
    
    private HashMap<Short, int[]> exclusions;
    
    private HashMap<Short, Short> awtfontpaths;
    
    private HashMap<Short, Short> proportionals;
    
    private HashMap<Short, Short> scriptAllfontsMotif;
    
    private HashMap<Short, Short[]> scriptFontsMotif;
    
    private HashMap<Short, Short> alphabeticSuffix;
    
    private short[] fallbackScriptIDs;
    
    private String version;
    
    private String appendedfontpath;
    
    public void load(InputStream param1InputStream) throws IOException {
      initLogicalNameStyle();
      initHashMaps();
      FontProperties fontProperties = new FontProperties();
      fontProperties.load(param1InputStream);
      initBinaryTable();
    }
    
    private void initBinaryTable() {
      head = new short[20];
      head[0] = 20;
      table_scriptIDs = FontConfiguration.toList(this.scriptIDs);
      head[1] = (short)(head[0] + table_scriptIDs.length);
      int i = table_scriptIDs.length + this.scriptFonts.size() * 20;
      table_scriptFonts = new short[i];
      for (Map.Entry entry : this.scriptAllfonts.entrySet())
        table_scriptFonts[((Short)entry.getKey()).intValue()] = ((Short)entry.getValue()).shortValue(); 
      int j = table_scriptIDs.length;
      for (Map.Entry entry : this.scriptFonts.entrySet()) {
        table_scriptFonts[((Short)entry.getKey()).intValue()] = (short)-j;
        Short[] arrayOfShort = (Short[])entry.getValue();
        for (byte b1 = 0; b1 < 20; b1++) {
          if (arrayOfShort[b1] != null) {
            table_scriptFonts[j++] = arrayOfShort[b1].shortValue();
          } else {
            table_scriptFonts[j++] = 0;
          } 
        } 
      } 
      head[2] = (short)(head[1] + table_scriptFonts.length);
      table_elcIDs = FontConfiguration.toList(this.elcIDs);
      head[3] = (short)(head[2] + table_elcIDs.length);
      table_sequences = new short[this.elcIDs.size() * 5];
      for (Map.Entry entry : this.sequences.entrySet()) {
        int k = ((Short)entry.getKey()).intValue();
        short[] arrayOfShort = (short[])entry.getValue();
        if (arrayOfShort.length == 1) {
          for (int n = 0; n < 5; n++)
            table_sequences[k * 5 + n] = arrayOfShort[0]; 
          continue;
        } 
        for (int m = 0; m < 5; m++)
          table_sequences[k * 5 + m] = arrayOfShort[m]; 
      } 
      head[4] = (short)(head[3] + table_sequences.length);
      table_fontfileNameIDs = FontConfiguration.toList(this.fontfileNameIDs);
      head[5] = (short)(head[4] + table_fontfileNameIDs.length);
      table_componentFontNameIDs = FontConfiguration.toList(this.componentFontNameIDs);
      head[6] = (short)(head[5] + table_componentFontNameIDs.length);
      table_filenames = new short[table_componentFontNameIDs.length];
      Arrays.fill(table_filenames, (short)-1);
      for (Map.Entry entry : this.filenames.entrySet())
        table_filenames[((Short)entry.getKey()).shortValue()] = ((Short)entry.getValue()).shortValue(); 
      head[7] = (short)(head[6] + table_filenames.length);
      FontConfiguration.table_awtfontpaths = new short[table_scriptIDs.length];
      for (Map.Entry entry : this.awtfontpaths.entrySet())
        FontConfiguration.table_awtfontpaths[((Short)entry.getKey()).shortValue()] = ((Short)entry.getValue()).shortValue(); 
      head[8] = (short)(head[7] + FontConfiguration.table_awtfontpaths.length);
      table_exclusions = new short[this.scriptIDs.size()];
      for (Map.Entry entry : this.exclusions.entrySet()) {
        int[] arrayOfInt = (int[])entry.getValue();
        char[] arrayOfChar = new char[arrayOfInt.length * 2];
        byte b1 = 0;
        for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
          arrayOfChar[b1++] = (char)(arrayOfInt[b2] >> 16);
          arrayOfChar[b1++] = (char)(arrayOfInt[b2] & 0xFFFF);
        } 
        table_exclusions[((Short)entry.getKey()).shortValue()] = FontConfiguration.getStringID(new String(arrayOfChar));
      } 
      head[9] = (short)(head[8] + table_exclusions.length);
      table_proportionals = new short[this.proportionals.size() * 2];
      byte b = 0;
      for (Map.Entry entry : this.proportionals.entrySet()) {
        table_proportionals[b++] = ((Short)entry.getKey()).shortValue();
        table_proportionals[b++] = ((Short)entry.getValue()).shortValue();
      } 
      head[10] = (short)(head[9] + table_proportionals.length);
      if (this.scriptAllfontsMotif.size() != 0 || this.scriptFontsMotif.size() != 0) {
        i = table_scriptIDs.length + this.scriptFontsMotif.size() * 20;
        table_scriptFontsMotif = new short[i];
        for (Map.Entry entry : this.scriptAllfontsMotif.entrySet())
          table_scriptFontsMotif[((Short)entry.getKey()).intValue()] = ((Short)entry.getValue()).shortValue(); 
        j = table_scriptIDs.length;
        for (Map.Entry entry : this.scriptFontsMotif.entrySet()) {
          table_scriptFontsMotif[((Short)entry.getKey()).intValue()] = (short)-j;
          Short[] arrayOfShort = (Short[])entry.getValue();
          for (byte b1 = 0; b1 < 20; b1++) {
            if (arrayOfShort[b1] != null) {
              table_scriptFontsMotif[j++] = arrayOfShort[b1].shortValue();
            } else {
              table_scriptFontsMotif[j++] = 0;
            } 
          } 
        } 
      } else {
        table_scriptFontsMotif = EMPTY_SHORT_ARRAY;
      } 
      head[11] = (short)(head[10] + table_scriptFontsMotif.length);
      table_alphabeticSuffix = new short[this.alphabeticSuffix.size() * 2];
      b = 0;
      for (Map.Entry entry : this.alphabeticSuffix.entrySet()) {
        table_alphabeticSuffix[b++] = ((Short)entry.getKey()).shortValue();
        table_alphabeticSuffix[b++] = ((Short)entry.getValue()).shortValue();
      } 
      head[15] = FontConfiguration.getShortArrayID(this.fallbackScriptIDs);
      head[16] = FontConfiguration.getStringID(this.appendedfontpath);
      head[17] = FontConfiguration.getStringID(this.version);
      head[12] = (short)(head[11] + table_alphabeticSuffix.length);
      table_stringIDs = new short[stringIDNum + 1];
      System.arraycopy(stringIDs, 0, table_stringIDs, 0, stringIDNum + 1);
      head[13] = (short)(head[12] + stringIDNum + 1);
      table_stringTable = stringTable.toString().toCharArray();
      head[14] = (short)(head[13] + stringTable.length());
      stringCache = new String[table_stringIDs.length];
    }
    
    private void initLogicalNameStyle() {
      this.logicalFontIDs = new HashMap();
      this.fontStyleIDs = new HashMap();
      this.logicalFontIDs.put("serif", Integer.valueOf(0));
      this.logicalFontIDs.put("sansserif", Integer.valueOf(1));
      this.logicalFontIDs.put("monospaced", Integer.valueOf(2));
      this.logicalFontIDs.put("dialog", Integer.valueOf(3));
      this.logicalFontIDs.put("dialoginput", Integer.valueOf(4));
      this.fontStyleIDs.put("plain", Integer.valueOf(0));
      this.fontStyleIDs.put("bold", Integer.valueOf(1));
      this.fontStyleIDs.put("italic", Integer.valueOf(2));
      this.fontStyleIDs.put("bolditalic", Integer.valueOf(3));
    }
    
    private void initHashMaps() {
      this.scriptIDs = new HashMap();
      this.elcIDs = new HashMap();
      this.componentFontNameIDs = new HashMap();
      this.componentFontNameIDs.put("", Short.valueOf((short)0));
      this.fontfileNameIDs = new HashMap();
      this.filenames = new HashMap();
      this.sequences = new HashMap();
      this.scriptFonts = new HashMap();
      this.scriptAllfonts = new HashMap();
      this.exclusions = new HashMap();
      this.awtfontpaths = new HashMap();
      this.proportionals = new HashMap();
      this.scriptFontsMotif = new HashMap();
      this.scriptAllfontsMotif = new HashMap();
      this.alphabeticSuffix = new HashMap();
      this.fallbackScriptIDs = EMPTY_SHORT_ARRAY;
    }
    
    private int[] parseExclusions(String param1String1, String param1String2) {
      if (param1String2 == null)
        return EMPTY_INT_ARRAY; 
      byte b1 = 1;
      int i;
      for (i = 0; (i = param1String2.indexOf(',', i)) != -1; i++)
        b1++; 
      int[] arrayOfInt = new int[b1 * 2];
      i = 0;
      int j = 0;
      byte b2 = 0;
      while (b2 < b1 * 2) {
        int k = 0;
        int m = 0;
        try {
          j = param1String2.indexOf('-', i);
          String str1 = param1String2.substring(i, j);
          i = j + 1;
          j = param1String2.indexOf(',', i);
          if (j == -1)
            j = param1String2.length(); 
          String str2 = param1String2.substring(i, j);
          i = j + 1;
          int n = str1.length();
          int i1 = str2.length();
          if ((n != 4 && n != 6) || (i1 != 4 && i1 != 6))
            throw new Exception(); 
          k = Integer.parseInt(str1, 16);
          m = Integer.parseInt(str2, 16);
          if (k > m)
            throw new Exception(); 
        } catch (Exception exception) {
          if (FontUtilities.debugFonts() && logger != null)
            logger.config("Failed parsing " + param1String1 + " property of font configuration."); 
          return EMPTY_INT_ARRAY;
        } 
        arrayOfInt[b2++] = k;
        arrayOfInt[b2++] = m;
      } 
      return arrayOfInt;
    }
    
    private Short getID(HashMap<String, Short> param1HashMap, String param1String) {
      Short short = (Short)param1HashMap.get(param1String);
      if (short == null) {
        param1HashMap.put(param1String, Short.valueOf((short)param1HashMap.size()));
        return (Short)param1HashMap.get(param1String);
      } 
      return short;
    }
    
    private void parseProperty(String param1String1, String param1String2) {
      if (param1String1.startsWith("filename.")) {
        param1String1 = param1String1.substring(9);
        if (!"MingLiU_HKSCS".equals(param1String1))
          param1String1 = param1String1.replace('_', ' '); 
        Short short1 = getID(this.componentFontNameIDs, param1String1);
        Short short2 = getID(this.fontfileNameIDs, param1String2);
        this.filenames.put(short1, short2);
      } else if (param1String1.startsWith("exclusion.")) {
        param1String1 = param1String1.substring(10);
        this.exclusions.put(getID(this.scriptIDs, param1String1), parseExclusions(param1String1, param1String2));
      } else if (param1String1.startsWith("sequence.")) {
        param1String1 = param1String1.substring(9);
        boolean bool1 = false;
        boolean bool2 = false;
        String[] arrayOfString = (String[])FontConfiguration.splitSequence(param1String2).toArray(EMPTY_STRING_ARRAY);
        short[] arrayOfShort1 = new short[arrayOfString.length];
        short s;
        for (s = 0; s < arrayOfString.length; s++) {
          if ("alphabetic/default".equals(arrayOfString[s])) {
            arrayOfString[s] = "alphabetic";
            bool1 = true;
          } else if ("alphabetic/1252".equals(arrayOfString[s])) {
            arrayOfString[s] = "alphabetic";
            bool2 = true;
          } 
          arrayOfShort1[s] = getID(this.scriptIDs, arrayOfString[s]).shortValue();
        } 
        s = FontConfiguration.getShortArrayID(arrayOfShort1);
        Short short = null;
        int i = param1String1.indexOf('.');
        if (i == -1) {
          if ("fallback".equals(param1String1)) {
            this.fallbackScriptIDs = arrayOfShort1;
            return;
          } 
          if ("allfonts".equals(param1String1)) {
            short = getID(this.elcIDs, "NULL.NULL.NULL");
          } else {
            if (logger != null)
              logger.config("Error sequence def: <sequence." + param1String1 + ">"); 
            return;
          } 
        } else {
          short = getID(this.elcIDs, param1String1.substring(i + 1));
          param1String1 = param1String1.substring(0, i);
        } 
        short[] arrayOfShort2 = null;
        if ("allfonts".equals(param1String1)) {
          arrayOfShort2 = new short[1];
          arrayOfShort2[0] = s;
        } else {
          arrayOfShort2 = (short[])this.sequences.get(short);
          if (arrayOfShort2 == null)
            arrayOfShort2 = new short[5]; 
          Integer integer = (Integer)this.logicalFontIDs.get(param1String1);
          if (integer == null) {
            if (logger != null)
              logger.config("Unrecognizable logicfont name " + param1String1); 
            return;
          } 
          arrayOfShort2[integer.intValue()] = s;
        } 
        this.sequences.put(short, arrayOfShort2);
        if (bool1) {
          this.alphabeticSuffix.put(short, Short.valueOf(FontConfiguration.getStringID("default")));
        } else if (bool2) {
          this.alphabeticSuffix.put(short, Short.valueOf(FontConfiguration.getStringID("1252")));
        } 
      } else if (param1String1.startsWith("allfonts.")) {
        param1String1 = param1String1.substring(9);
        if (param1String1.endsWith(".motif")) {
          param1String1 = param1String1.substring(0, param1String1.length() - 6);
          this.scriptAllfontsMotif.put(getID(this.scriptIDs, param1String1), getID(this.componentFontNameIDs, param1String2));
        } else {
          this.scriptAllfonts.put(getID(this.scriptIDs, param1String1), getID(this.componentFontNameIDs, param1String2));
        } 
      } else if (param1String1.startsWith("awtfontpath.")) {
        param1String1 = param1String1.substring(12);
        this.awtfontpaths.put(getID(this.scriptIDs, param1String1), Short.valueOf(FontConfiguration.getStringID(param1String2)));
      } else if ("version".equals(param1String1)) {
        this.version = param1String2;
      } else if ("appendedfontpath".equals(param1String1)) {
        this.appendedfontpath = param1String2;
      } else if (param1String1.startsWith("proportional.")) {
        param1String1 = param1String1.substring(13).replace('_', ' ');
        this.proportionals.put(getID(this.componentFontNameIDs, param1String1), getID(this.componentFontNameIDs, param1String2));
      } else {
        Short[] arrayOfShort;
        boolean bool = false;
        int i = param1String1.indexOf('.');
        if (i == -1) {
          if (logger != null)
            logger.config("Failed parsing " + param1String1 + " property of font configuration."); 
          return;
        } 
        int j = param1String1.indexOf('.', i + 1);
        if (j == -1) {
          if (logger != null)
            logger.config("Failed parsing " + param1String1 + " property of font configuration."); 
          return;
        } 
        if (param1String1.endsWith(".motif")) {
          param1String1 = param1String1.substring(0, param1String1.length() - 6);
          bool = true;
        } 
        Integer integer1 = (Integer)this.logicalFontIDs.get(param1String1.substring(0, i));
        Integer integer2 = (Integer)this.fontStyleIDs.get(param1String1.substring(i + 1, j));
        Short short = getID(this.scriptIDs, param1String1.substring(j + 1));
        if (integer1 == null || integer2 == null) {
          if (logger != null)
            logger.config("unrecognizable logicfont name/style at " + param1String1); 
          return;
        } 
        if (bool) {
          arrayOfShort = (Short[])this.scriptFontsMotif.get(short);
        } else {
          arrayOfShort = (Short[])this.scriptFonts.get(short);
        } 
        if (arrayOfShort == null)
          arrayOfShort = new Short[20]; 
        arrayOfShort[integer1.intValue() * 4 + integer2.intValue()] = getID(this.componentFontNameIDs, param1String2);
        if (bool) {
          this.scriptFontsMotif.put(short, arrayOfShort);
        } else {
          this.scriptFonts.put(short, arrayOfShort);
        } 
      } 
    }
    
    class FontProperties extends Properties {
      public Object put(Object param2Object1, Object param2Object2) {
        FontConfiguration.PropertiesHandler.this.parseProperty((String)param2Object1, (String)param2Object2);
        return null;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\FontConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */