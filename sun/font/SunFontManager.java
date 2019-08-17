package sun.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.plaf.FontUIResource;
import sun.awt.AppContext;
import sun.awt.FontConfiguration;
import sun.awt.SunToolkit;
import sun.java2d.FontSupport;
import sun.misc.ThreadGroupUtils;
import sun.util.logging.PlatformLogger;

public abstract class SunFontManager implements FontSupport, FontManagerForSGE {
  public static final int FONTFORMAT_NONE = -1;
  
  public static final int FONTFORMAT_TRUETYPE = 0;
  
  public static final int FONTFORMAT_TYPE1 = 1;
  
  public static final int FONTFORMAT_T2K = 2;
  
  public static final int FONTFORMAT_TTC = 3;
  
  public static final int FONTFORMAT_COMPOSITE = 4;
  
  public static final int FONTFORMAT_NATIVE = 5;
  
  protected static final int CHANNELPOOLSIZE = 20;
  
  protected FileFont[] fontFileCache = new FileFont[20];
  
  private int lastPoolIndex = 0;
  
  private int maxCompFont = 0;
  
  private CompositeFont[] compFonts = new CompositeFont[20];
  
  private ConcurrentHashMap<String, CompositeFont> compositeFonts = new ConcurrentHashMap();
  
  private ConcurrentHashMap<String, PhysicalFont> physicalFonts = new ConcurrentHashMap();
  
  private ConcurrentHashMap<String, PhysicalFont> registeredFonts = new ConcurrentHashMap();
  
  protected ConcurrentHashMap<String, Font2D> fullNameToFont = new ConcurrentHashMap();
  
  private HashMap<String, TrueTypeFont> localeFullNamesToFont;
  
  private PhysicalFont defaultPhysicalFont;
  
  static boolean longAddresses;
  
  private boolean loaded1dot0Fonts = false;
  
  boolean loadedAllFonts = false;
  
  boolean loadedAllFontFiles = false;
  
  HashMap<String, String> jreFontMap;
  
  HashSet<String> jreLucidaFontFiles;
  
  String[] jreOtherFontFiles;
  
  boolean noOtherJREFontFiles = false;
  
  public static final String lucidaFontName = "Lucida Sans Regular";
  
  public static String jreLibDirName;
  
  public static String jreFontDirName;
  
  private static HashSet<String> missingFontFiles = null;
  
  private String defaultFontName;
  
  private String defaultFontFileName;
  
  protected HashSet registeredFontFiles = new HashSet();
  
  private ArrayList badFonts;
  
  protected String fontPath;
  
  private FontConfiguration fontConfig;
  
  private boolean discoveredAllFonts = false;
  
  private static final FilenameFilter ttFilter = new TTFilter(null);
  
  private static final FilenameFilter t1Filter = new T1Filter(null);
  
  private Font[] allFonts;
  
  private String[] allFamilies;
  
  private Locale lastDefaultLocale;
  
  public static boolean noType1Font;
  
  private static String[] STR_ARRAY = new String[0];
  
  private boolean usePlatformFontMetrics = false;
  
  private final ConcurrentHashMap<String, FontRegistrationInfo> deferredFontFiles = new ConcurrentHashMap();
  
  private final ConcurrentHashMap<String, Font2DHandle> initialisedFonts = new ConcurrentHashMap();
  
  private HashMap<String, String> fontToFileMap = null;
  
  private HashMap<String, String> fontToFamilyNameMap = null;
  
  private HashMap<String, ArrayList<String>> familyToFontListMap = null;
  
  private String[] pathDirs = null;
  
  private boolean haveCheckedUnreferencedFontFiles;
  
  static HashMap<String, FamilyDescription> platformFontMap;
  
  private ConcurrentHashMap<String, Font2D> fontNameCache = new ConcurrentHashMap();
  
  protected Thread fileCloser = null;
  
  Vector<File> tmpFontFiles = null;
  
  private static final Object altJAFontKey;
  
  private static final Object localeFontKey;
  
  private static final Object proportionalFontKey;
  
  private boolean _usingPerAppContextComposites = false;
  
  private boolean _usingAlternateComposites = false;
  
  private static boolean gAltJAFont;
  
  private boolean gLocalePref = false;
  
  private boolean gPropPref = false;
  
  private static HashSet<String> installedNames;
  
  private static final Object regFamilyKey;
  
  private static final Object regFullNameKey;
  
  private Hashtable<String, FontFamily> createdByFamilyName;
  
  private Hashtable<String, Font2D> createdByFullName;
  
  private boolean fontsAreRegistered = false;
  
  private boolean fontsAreRegisteredPerAppContext = false;
  
  private static Locale systemLocale;
  
  public static SunFontManager getInstance() {
    FontManager fontManager = FontManagerFactory.getInstance();
    return (SunFontManager)fontManager;
  }
  
  public FilenameFilter getTrueTypeFilter() { return ttFilter; }
  
  public FilenameFilter getType1Filter() { return t1Filter; }
  
  public boolean usingPerAppContextComposites() { return this._usingPerAppContextComposites; }
  
  private void initJREFontMap() {
    this.jreFontMap = new HashMap();
    this.jreLucidaFontFiles = new HashSet();
    if (isOpenJDK())
      return; 
    this.jreFontMap.put("lucida sans0", "LucidaSansRegular.ttf");
    this.jreFontMap.put("lucida sans1", "LucidaSansDemiBold.ttf");
    this.jreFontMap.put("lucida sans regular0", "LucidaSansRegular.ttf");
    this.jreFontMap.put("lucida sans regular1", "LucidaSansDemiBold.ttf");
    this.jreFontMap.put("lucida sans bold1", "LucidaSansDemiBold.ttf");
    this.jreFontMap.put("lucida sans demibold1", "LucidaSansDemiBold.ttf");
    this.jreFontMap.put("lucida sans typewriter0", "LucidaTypewriterRegular.ttf");
    this.jreFontMap.put("lucida sans typewriter1", "LucidaTypewriterBold.ttf");
    this.jreFontMap.put("lucida sans typewriter regular0", "LucidaTypewriter.ttf");
    this.jreFontMap.put("lucida sans typewriter regular1", "LucidaTypewriterBold.ttf");
    this.jreFontMap.put("lucida sans typewriter bold1", "LucidaTypewriterBold.ttf");
    this.jreFontMap.put("lucida sans typewriter demibold1", "LucidaTypewriterBold.ttf");
    this.jreFontMap.put("lucida bright0", "LucidaBrightRegular.ttf");
    this.jreFontMap.put("lucida bright1", "LucidaBrightDemiBold.ttf");
    this.jreFontMap.put("lucida bright2", "LucidaBrightItalic.ttf");
    this.jreFontMap.put("lucida bright3", "LucidaBrightDemiItalic.ttf");
    this.jreFontMap.put("lucida bright regular0", "LucidaBrightRegular.ttf");
    this.jreFontMap.put("lucida bright regular1", "LucidaBrightDemiBold.ttf");
    this.jreFontMap.put("lucida bright regular2", "LucidaBrightItalic.ttf");
    this.jreFontMap.put("lucida bright regular3", "LucidaBrightDemiItalic.ttf");
    this.jreFontMap.put("lucida bright bold1", "LucidaBrightDemiBold.ttf");
    this.jreFontMap.put("lucida bright bold3", "LucidaBrightDemiItalic.ttf");
    this.jreFontMap.put("lucida bright demibold1", "LucidaBrightDemiBold.ttf");
    this.jreFontMap.put("lucida bright demibold3", "LucidaBrightDemiItalic.ttf");
    this.jreFontMap.put("lucida bright italic2", "LucidaBrightItalic.ttf");
    this.jreFontMap.put("lucida bright italic3", "LucidaBrightDemiItalic.ttf");
    this.jreFontMap.put("lucida bright bold italic3", "LucidaBrightDemiItalic.ttf");
    this.jreFontMap.put("lucida bright demibold italic3", "LucidaBrightDemiItalic.ttf");
    for (String str : this.jreFontMap.values())
      this.jreLucidaFontFiles.add(str); 
  }
  
  public TrueTypeFont getEUDCFont() { return null; }
  
  private static native void initIDs();
  
  protected SunFontManager() {
    initJREFontMap();
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            File file = new File(SunFontManager.jreFontDirName + File.separator + "badfonts.txt");
            if (file.exists()) {
              FileInputStream fileInputStream = null;
              try {
                SunFontManager.this.badFonts = new ArrayList();
                fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                while (true) {
                  String str = bufferedReader.readLine();
                  if (str == null)
                    break; 
                  if (FontUtilities.debugFonts())
                    FontUtilities.getLogger().warning("read bad font: " + str); 
                  SunFontManager.this.badFonts.add(str);
                } 
              } catch (IOException iOException) {
                try {
                  if (fileInputStream != null)
                    fileInputStream.close(); 
                } catch (IOException iOException1) {}
              } 
            } 
            if (FontUtilities.isLinux)
              SunFontManager.this.registerFontDir(SunFontManager.jreFontDirName); 
            SunFontManager.this.registerFontsInDir(SunFontManager.jreFontDirName, true, 2, true, false);
            SunFontManager.this.fontConfig = SunFontManager.this.createFontConfiguration();
            if (SunFontManager.isOpenJDK()) {
              String[] arrayOfString = SunFontManager.this.getDefaultPlatformFont();
              SunFontManager.this.defaultFontName = arrayOfString[0];
              SunFontManager.this.defaultFontFileName = arrayOfString[1];
            } 
            String str1 = SunFontManager.this.fontConfig.getExtraFontPath();
            boolean bool1 = false;
            boolean bool2 = false;
            String str2 = System.getProperty("sun.java2d.fontpath");
            if (str2 != null)
              if (str2.startsWith("prepend:")) {
                bool1 = true;
                str2 = str2.substring("prepend:".length());
              } else if (str2.startsWith("append:")) {
                bool2 = true;
                str2 = str2.substring("append:".length());
              }  
            if (FontUtilities.debugFonts()) {
              PlatformLogger platformLogger = FontUtilities.getLogger();
              platformLogger.info("JRE font directory: " + SunFontManager.jreFontDirName);
              platformLogger.info("Extra font path: " + str1);
              platformLogger.info("Debug font path: " + str2);
            } 
            if (str2 != null) {
              SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
              if (str1 != null)
                SunFontManager.this.fontPath = str1 + File.pathSeparator + SunFontManager.this.fontPath; 
              if (bool2) {
                SunFontManager.this.fontPath += File.pathSeparator + str2;
              } else if (bool1) {
                SunFontManager.this.fontPath = str2 + File.pathSeparator + SunFontManager.this.fontPath;
              } else {
                SunFontManager.this.fontPath = str2;
              } 
              SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
            } else if (str1 != null) {
              SunFontManager.this.registerFontDirs(str1);
            } 
            if (FontUtilities.isSolaris && Locale.JAPAN.equals(Locale.getDefault()))
              SunFontManager.this.registerFontDir("/usr/openwin/lib/locale/ja/X11/fonts/TT"); 
            SunFontManager.this.initCompositeFonts(SunFontManager.this.fontConfig, null);
            return null;
          }
        });
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            String str1 = System.getProperty("java2d.font.usePlatformFont");
            String str2 = System.getenv("JAVA2D_USEPLATFORMFONT");
            return Boolean.valueOf(("true".equals(str1) || str2 != null));
          }
        })).booleanValue();
    if (bool) {
      this.usePlatformFontMetrics = true;
      System.out.println("Enabling platform font metrics for win32. This is an unsupported option.");
      System.out.println("This yields incorrect composite font metrics as reported by 1.1.x releases.");
      System.out.println("It is appropriate only for use by applications which do not use any Java 2");
      System.out.println("functionality. This property will be removed in a later release.");
    } 
  }
  
  public Font2DHandle getNewComposite(String paramString, int paramInt, Font2DHandle paramFont2DHandle) {
    if (!(paramFont2DHandle.font2D instanceof CompositeFont))
      return paramFont2DHandle; 
    CompositeFont compositeFont1 = (CompositeFont)paramFont2DHandle.font2D;
    PhysicalFont physicalFont1 = compositeFont1.getSlotFont(0);
    if (paramString == null)
      paramString = physicalFont1.getFamilyName(null); 
    if (paramInt == -1)
      paramInt = compositeFont1.getStyle(); 
    Font2D font2D = findFont2D(paramString, paramInt, 0);
    if (!(font2D instanceof PhysicalFont))
      font2D = physicalFont1; 
    PhysicalFont physicalFont2 = (PhysicalFont)font2D;
    CompositeFont compositeFont2 = (CompositeFont)findFont2D("dialog", paramInt, 0);
    if (compositeFont2 == null)
      return paramFont2DHandle; 
    CompositeFont compositeFont3 = new CompositeFont(physicalFont2, compositeFont2);
    return new Font2DHandle(compositeFont3);
  }
  
  protected void registerCompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean) {
    CompositeFont compositeFont = new CompositeFont(paramString, paramArrayOfString1, paramArrayOfString2, paramInt, paramArrayOfInt1, paramArrayOfInt2, paramBoolean, this);
    addCompositeToFontList(compositeFont, 2);
    synchronized (this.compFonts) {
      this.compFonts[this.maxCompFont++] = compositeFont;
    } 
  }
  
  protected static void registerCompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean, ConcurrentHashMap<String, Font2D> paramConcurrentHashMap) {
    CompositeFont compositeFont = new CompositeFont(paramString, paramArrayOfString1, paramArrayOfString2, paramInt, paramArrayOfInt1, paramArrayOfInt2, paramBoolean, getInstance());
    Font2D font2D = (Font2D)paramConcurrentHashMap.get(paramString.toLowerCase(Locale.ENGLISH));
    if (font2D instanceof CompositeFont)
      font2D.handle.font2D = compositeFont; 
    paramConcurrentHashMap.put(paramString.toLowerCase(Locale.ENGLISH), compositeFont);
  }
  
  private void addCompositeToFontList(CompositeFont paramCompositeFont, int paramInt) {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Add to Family " + paramCompositeFont.familyName + ", Font " + paramCompositeFont.fullName + " rank=" + paramInt); 
    paramCompositeFont.setRank(paramInt);
    this.compositeFonts.put(paramCompositeFont.fullName, paramCompositeFont);
    this.fullNameToFont.put(paramCompositeFont.fullName.toLowerCase(Locale.ENGLISH), paramCompositeFont);
    FontFamily fontFamily = FontFamily.getFamily(paramCompositeFont.familyName);
    if (fontFamily == null)
      fontFamily = new FontFamily(paramCompositeFont.familyName, true, paramInt); 
    fontFamily.setFont(paramCompositeFont, paramCompositeFont.style);
  }
  
  protected PhysicalFont addToFontList(PhysicalFont paramPhysicalFont, int paramInt) {
    String str1 = paramPhysicalFont.fullName;
    String str2 = paramPhysicalFont.familyName;
    if (str1 == null || "".equals(str1))
      return null; 
    if (this.compositeFonts.containsKey(str1))
      return null; 
    paramPhysicalFont.setRank(paramInt);
    if (!this.physicalFonts.containsKey(str1)) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Add to Family " + str2 + ", Font " + str1 + " rank=" + paramInt); 
      this.physicalFonts.put(str1, paramPhysicalFont);
      FontFamily fontFamily = FontFamily.getFamily(str2);
      if (fontFamily == null) {
        fontFamily = new FontFamily(str2, false, paramInt);
        fontFamily.setFont(paramPhysicalFont, paramPhysicalFont.style);
      } else {
        fontFamily.setFont(paramPhysicalFont, paramPhysicalFont.style);
      } 
      this.fullNameToFont.put(str1.toLowerCase(Locale.ENGLISH), paramPhysicalFont);
      return paramPhysicalFont;
    } 
    PhysicalFont physicalFont1 = paramPhysicalFont;
    PhysicalFont physicalFont2 = (PhysicalFont)this.physicalFonts.get(str1);
    if (physicalFont2 == null)
      return null; 
    if (physicalFont2.getRank() >= paramInt) {
      if (physicalFont2.mapper != null && paramInt > 2)
        return physicalFont2; 
      if (physicalFont2.getRank() == paramInt)
        if (physicalFont2 instanceof TrueTypeFont && physicalFont1 instanceof TrueTypeFont) {
          TrueTypeFont trueTypeFont1 = (TrueTypeFont)physicalFont2;
          TrueTypeFont trueTypeFont2 = (TrueTypeFont)physicalFont1;
          if (trueTypeFont1.fileSize >= trueTypeFont2.fileSize)
            return physicalFont2; 
        } else {
          return physicalFont2;
        }  
      if (physicalFont2.platName.startsWith(jreFontDirName)) {
        if (FontUtilities.isLogging())
          FontUtilities.getLogger().warning("Unexpected attempt to replace a JRE  font " + str1 + " from " + physicalFont2.platName + " with " + physicalFont1.platName); 
        return physicalFont2;
      } 
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Replace in Family " + str2 + ",Font " + str1 + " new rank=" + paramInt + " from " + physicalFont2.platName + " with " + physicalFont1.platName); 
      replaceFont(physicalFont2, physicalFont1);
      this.physicalFonts.put(str1, physicalFont1);
      this.fullNameToFont.put(str1.toLowerCase(Locale.ENGLISH), physicalFont1);
      FontFamily fontFamily = FontFamily.getFamily(str2);
      if (fontFamily == null) {
        fontFamily = new FontFamily(str2, false, paramInt);
        fontFamily.setFont(physicalFont1, physicalFont1.style);
      } else {
        fontFamily.setFont(physicalFont1, physicalFont1.style);
      } 
      return physicalFont1;
    } 
    return physicalFont2;
  }
  
  public Font2D[] getRegisteredFonts() {
    PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
    int i = this.maxCompFont;
    Font2D[] arrayOfFont2D = new Font2D[arrayOfPhysicalFont.length + i];
    System.arraycopy(this.compFonts, 0, arrayOfFont2D, 0, i);
    System.arraycopy(arrayOfPhysicalFont, 0, arrayOfFont2D, i, arrayOfPhysicalFont.length);
    return arrayOfFont2D;
  }
  
  protected PhysicalFont[] getPhysicalFonts() { return (PhysicalFont[])this.physicalFonts.values().toArray(new PhysicalFont[0]); }
  
  protected void initialiseDeferredFonts() {
    for (String str : this.deferredFontFiles.keySet())
      initialiseDeferredFont(str); 
  }
  
  protected void registerDeferredJREFonts(String paramString) {
    for (FontRegistrationInfo fontRegistrationInfo : this.deferredFontFiles.values()) {
      if (fontRegistrationInfo.fontFilePath != null && fontRegistrationInfo.fontFilePath.startsWith(paramString))
        initialiseDeferredFont(fontRegistrationInfo.fontFilePath); 
    } 
  }
  
  public boolean isDeferredFont(String paramString) { return this.deferredFontFiles.containsKey(paramString); }
  
  public PhysicalFont findJREDeferredFont(String paramString, int paramInt) {
    String str1 = paramString.toLowerCase(Locale.ENGLISH) + paramInt;
    String str2 = (String)this.jreFontMap.get(str1);
    if (str2 != null) {
      str2 = jreFontDirName + File.separator + str2;
      if (this.deferredFontFiles.get(str2) != null) {
        PhysicalFont physicalFont = initialiseDeferredFont(str2);
        if (physicalFont != null && (physicalFont.getFontName(null).equalsIgnoreCase(paramString) || physicalFont.getFamilyName(null).equalsIgnoreCase(paramString)) && physicalFont.style == paramInt)
          return physicalFont; 
      } 
    } 
    if (this.noOtherJREFontFiles)
      return null; 
    synchronized (this.jreLucidaFontFiles) {
      if (this.jreOtherFontFiles == null) {
        HashSet hashSet = new HashSet();
        for (String str3 : this.deferredFontFiles.keySet()) {
          File file = new File(str3);
          String str4 = file.getParent();
          String str5 = file.getName();
          if (str4 == null || !str4.equals(jreFontDirName) || this.jreLucidaFontFiles.contains(str5))
            continue; 
          hashSet.add(str3);
        } 
        this.jreOtherFontFiles = (String[])hashSet.toArray(STR_ARRAY);
        if (this.jreOtherFontFiles.length == 0)
          this.noOtherJREFontFiles = true; 
      } 
      for (byte b = 0; b < this.jreOtherFontFiles.length; b++) {
        str2 = this.jreOtherFontFiles[b];
        if (str2 != null) {
          this.jreOtherFontFiles[b] = null;
          PhysicalFont physicalFont = initialiseDeferredFont(str2);
          if (physicalFont != null && (physicalFont.getFontName(null).equalsIgnoreCase(paramString) || physicalFont.getFamilyName(null).equalsIgnoreCase(paramString)) && physicalFont.style == paramInt)
            return physicalFont; 
        } 
      } 
    } 
    return null;
  }
  
  private PhysicalFont findOtherDeferredFont(String paramString, int paramInt) {
    for (String str1 : this.deferredFontFiles.keySet()) {
      File file = new File(str1);
      String str2 = file.getParent();
      String str3 = file.getName();
      if (str2 != null && str2.equals(jreFontDirName) && this.jreLucidaFontFiles.contains(str3))
        continue; 
      PhysicalFont physicalFont = initialiseDeferredFont(str1);
      if (physicalFont != null && (physicalFont.getFontName(null).equalsIgnoreCase(paramString) || physicalFont.getFamilyName(null).equalsIgnoreCase(paramString)) && physicalFont.style == paramInt)
        return physicalFont; 
    } 
    return null;
  }
  
  private PhysicalFont findDeferredFont(String paramString, int paramInt) {
    PhysicalFont physicalFont = findJREDeferredFont(paramString, paramInt);
    return (physicalFont != null) ? physicalFont : findOtherDeferredFont(paramString, paramInt);
  }
  
  public void registerDeferredFont(String paramString1, String paramString2, String[] paramArrayOfString, int paramInt1, boolean paramBoolean, int paramInt2) {
    FontRegistrationInfo fontRegistrationInfo = new FontRegistrationInfo(paramString2, paramArrayOfString, paramInt1, paramBoolean, paramInt2);
    this.deferredFontFiles.put(paramString1, fontRegistrationInfo);
  }
  
  public PhysicalFont initialiseDeferredFont(String paramString) {
    PhysicalFont physicalFont;
    if (paramString == null)
      return null; 
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Opening deferred font file " + paramString); 
    FontRegistrationInfo fontRegistrationInfo = (FontRegistrationInfo)this.deferredFontFiles.get(paramString);
    if (fontRegistrationInfo != null) {
      this.deferredFontFiles.remove(paramString);
      physicalFont = registerFontFile(fontRegistrationInfo.fontFilePath, fontRegistrationInfo.nativeNames, fontRegistrationInfo.fontFormat, fontRegistrationInfo.javaRasterizer, fontRegistrationInfo.fontRank);
      if (physicalFont != null) {
        this.initialisedFonts.put(paramString, physicalFont.handle);
      } else {
        this.initialisedFonts.put(paramString, (getDefaultPhysicalFont()).handle);
      } 
    } else {
      Font2DHandle font2DHandle = (Font2DHandle)this.initialisedFonts.get(paramString);
      if (font2DHandle == null) {
        physicalFont = getDefaultPhysicalFont();
      } else {
        physicalFont = (PhysicalFont)font2DHandle.font2D;
      } 
    } 
    return physicalFont;
  }
  
  public boolean isRegisteredFontFile(String paramString) { return this.registeredFonts.containsKey(paramString); }
  
  public PhysicalFont getRegisteredFontFile(String paramString) { return (PhysicalFont)this.registeredFonts.get(paramString); }
  
  public PhysicalFont registerFontFile(String paramString, String[] paramArrayOfString, int paramInt1, boolean paramBoolean, int paramInt2) {
    PhysicalFont physicalFont1 = (PhysicalFont)this.registeredFonts.get(paramString);
    if (physicalFont1 != null)
      return physicalFont1; 
    PhysicalFont physicalFont2 = null;
    try {
      NativeFont nativeFont;
      Type1Font type1Font;
      TrueTypeFont trueTypeFont;
      byte b;
      switch (paramInt1) {
        case 0:
          b = 0;
          do {
            trueTypeFont = new TrueTypeFont(paramString, paramArrayOfString, b++, paramBoolean);
            PhysicalFont physicalFont = addToFontList(trueTypeFont, paramInt2);
            if (physicalFont2 != null)
              continue; 
            physicalFont2 = physicalFont;
          } while (b < trueTypeFont.getFontCount());
          break;
        case 1:
          type1Font = new Type1Font(paramString, paramArrayOfString);
          physicalFont2 = addToFontList(type1Font, paramInt2);
          break;
        case 5:
          nativeFont = new NativeFont(paramString, false);
          physicalFont2 = addToFontList(nativeFont, paramInt2);
          break;
      } 
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Registered file " + paramString + " as font " + physicalFont2 + " rank=" + paramInt2); 
    } catch (FontFormatException fontFormatException) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().warning("Unusable font: " + paramString + " " + fontFormatException.toString()); 
    } 
    if (physicalFont2 != null && paramInt1 != 5)
      this.registeredFonts.put(paramString, physicalFont2); 
    return physicalFont2;
  }
  
  public void registerFonts(String[] paramArrayOfString, String[][] paramArrayOfString1, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2) {
    for (byte b = 0; b < paramInt1; b++) {
      if (paramBoolean2) {
        registerDeferredFont(paramArrayOfString[b], paramArrayOfString[b], paramArrayOfString1[b], paramInt2, paramBoolean1, paramInt3);
      } else {
        registerFontFile(paramArrayOfString[b], paramArrayOfString1[b], paramInt2, paramBoolean1, paramInt3);
      } 
    } 
  }
  
  public PhysicalFont getDefaultPhysicalFont() {
    if (this.defaultPhysicalFont == null) {
      this.defaultPhysicalFont = (PhysicalFont)findFont2D("Lucida Sans Regular", 0, 0);
      if (this.defaultPhysicalFont == null)
        this.defaultPhysicalFont = (PhysicalFont)findFont2D("Arial", 0, 0); 
      if (this.defaultPhysicalFont == null) {
        Iterator iterator = this.physicalFonts.values().iterator();
        if (iterator.hasNext()) {
          this.defaultPhysicalFont = (PhysicalFont)iterator.next();
        } else {
          throw new Error("Probable fatal error:No fonts found.");
        } 
      } 
    } 
    return this.defaultPhysicalFont;
  }
  
  public Font2D getDefaultLogicalFont(int paramInt) { return findFont2D("dialog", paramInt, 0); }
  
  private static String dotStyleStr(int paramInt) {
    switch (paramInt) {
      case 1:
        return ".bold";
      case 2:
        return ".italic";
      case 3:
        return ".bolditalic";
    } 
    return ".plain";
  }
  
  protected void populateFontFileNameMap(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap3, Locale paramLocale) {}
  
  private String[] getFontFilesFromPath(boolean paramBoolean) {
    final TTorT1Filter filter;
    if (paramBoolean) {
      tTorT1Filter = ttFilter;
    } else {
      tTorT1Filter = new TTorT1Filter(null);
    } 
    return (String[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            if (SunFontManager.this.pathDirs.length == 1) {
              File file = new File(SunFontManager.this.pathDirs[0]);
              String[] arrayOfString = file.list(filter);
              if (arrayOfString == null)
                return new String[0]; 
              for (byte b1 = 0; b1 < arrayOfString.length; b1++)
                arrayOfString[b1] = arrayOfString[b1].toLowerCase(); 
              return arrayOfString;
            } 
            ArrayList arrayList = new ArrayList();
            for (byte b = 0; b < SunFontManager.this.pathDirs.length; b++) {
              File file = new File(SunFontManager.this.pathDirs[b]);
              String[] arrayOfString = file.list(filter);
              if (arrayOfString != null)
                for (byte b1 = 0; b1 < arrayOfString.length; b1++)
                  arrayList.add(arrayOfString[b1].toLowerCase());  
            } 
            return arrayList.toArray(STR_ARRAY);
          }
        });
  }
  
  private void resolveWindowsFonts() {
    ArrayList arrayList = null;
    for (String str1 : this.fontToFamilyNameMap.keySet()) {
      String str2 = (String)this.fontToFileMap.get(str1);
      if (str2 == null) {
        if (str1.indexOf("  ") > 0) {
          String str = str1.replaceFirst("  ", " ");
          str2 = (String)this.fontToFileMap.get(str);
          if (str2 != null && !this.fontToFamilyNameMap.containsKey(str)) {
            this.fontToFileMap.remove(str);
            this.fontToFileMap.put(str1, str2);
          } 
          continue;
        } 
        if (str1.equals("marlett")) {
          this.fontToFileMap.put(str1, "marlett.ttf");
          continue;
        } 
        if (str1.equals("david")) {
          str2 = (String)this.fontToFileMap.get("david regular");
          if (str2 != null) {
            this.fontToFileMap.remove("david regular");
            this.fontToFileMap.put("david", str2);
          } 
          continue;
        } 
        if (arrayList == null)
          arrayList = new ArrayList(); 
        arrayList.add(str1);
      } 
    } 
    if (arrayList != null) {
      HashSet hashSet = new HashSet();
      HashMap hashMap = (HashMap)this.fontToFileMap.clone();
      for (String str : this.fontToFamilyNameMap.keySet())
        hashMap.remove(str); 
      for (String str : hashMap.keySet()) {
        hashSet.add(hashMap.get(str));
        this.fontToFileMap.remove(str);
      } 
      resolveFontFiles(hashSet, arrayList);
      if (arrayList.size() > 0) {
        ArrayList arrayList1 = new ArrayList();
        for (String str : this.fontToFileMap.values())
          arrayList1.add(str.toLowerCase()); 
        for (String str : getFontFilesFromPath(true)) {
          if (!arrayList1.contains(str))
            hashSet.add(str); 
        } 
        resolveFontFiles(hashSet, arrayList);
      } 
      if (arrayList.size() > 0) {
        int i = arrayList.size();
        for (byte b = 0; b < i; b++) {
          String str1 = (String)arrayList.get(b);
          String str2 = (String)this.fontToFamilyNameMap.get(str1);
          if (str2 != null) {
            ArrayList arrayList1 = (ArrayList)this.familyToFontListMap.get(str2);
            if (arrayList1 != null && arrayList1.size() <= 1)
              this.familyToFontListMap.remove(str2); 
          } 
          this.fontToFamilyNameMap.remove(str1);
          if (FontUtilities.isLogging())
            FontUtilities.getLogger().info("No file for font:" + str1); 
        } 
      } 
    } 
  }
  
  private void checkForUnreferencedFontFiles() {
    if (this.haveCheckedUnreferencedFontFiles)
      return; 
    this.haveCheckedUnreferencedFontFiles = true;
    if (!FontUtilities.isWindows)
      return; 
    ArrayList arrayList = new ArrayList();
    for (String str : this.fontToFileMap.values())
      arrayList.add(str.toLowerCase()); 
    HashMap hashMap1 = null;
    HashMap hashMap2 = null;
    HashMap hashMap3 = null;
    for (String str : getFontFilesFromPath(false)) {
      if (!arrayList.contains(str)) {
        if (FontUtilities.isLogging())
          FontUtilities.getLogger().info("Found non-registry file : " + str); 
        PhysicalFont physicalFont = registerFontFile(getPathName(str));
        if (physicalFont != null) {
          if (hashMap1 == null) {
            hashMap1 = new HashMap(this.fontToFileMap);
            hashMap2 = new HashMap(this.fontToFamilyNameMap);
            hashMap3 = new HashMap(this.familyToFontListMap);
          } 
          String str1 = physicalFont.getFontName(null);
          String str2 = physicalFont.getFamilyName(null);
          String str3 = str2.toLowerCase();
          hashMap2.put(str1, str2);
          hashMap1.put(str1, str);
          ArrayList arrayList1 = (ArrayList)hashMap3.get(str3);
          if (arrayList1 == null) {
            arrayList1 = new ArrayList();
          } else {
            arrayList1 = new ArrayList(arrayList1);
          } 
          arrayList1.add(str1);
          hashMap3.put(str3, arrayList1);
        } 
      } 
    } 
    if (hashMap1 != null) {
      this.fontToFileMap = hashMap1;
      this.familyToFontListMap = hashMap3;
      this.fontToFamilyNameMap = hashMap2;
    } 
  }
  
  private void resolveFontFiles(HashSet<String> paramHashSet, ArrayList<String> paramArrayList) {
    Locale locale = SunToolkit.getStartupLocale();
    label20: for (String str : paramHashSet) {
      try {
        byte b = 0;
        String str1 = getPathName(str);
        if (FontUtilities.isLogging())
          FontUtilities.getLogger().info("Trying to resolve file " + str1); 
        while (true) {
          TrueTypeFont trueTypeFont = new TrueTypeFont(str1, null, b++, false);
          String str2 = trueTypeFont.getFontName(locale).toLowerCase();
          if (paramArrayList.contains(str2)) {
            this.fontToFileMap.put(str2, str);
            paramArrayList.remove(str2);
            if (FontUtilities.isLogging())
              FontUtilities.getLogger().info("Resolved absent registry entry for " + str2 + " located in " + str1); 
          } 
          if (b >= trueTypeFont.getFontCount())
            continue label20; 
        } 
      } catch (Exception exception) {}
    } 
  }
  
  public HashMap<String, FamilyDescription> populateHardcodedFileNameMap() { return new HashMap(0); }
  
  Font2D findFontFromPlatformMap(String paramString, int paramInt) {
    if (platformFontMap == null)
      platformFontMap = populateHardcodedFileNameMap(); 
    if (platformFontMap == null || platformFontMap.size() == 0)
      return null; 
    int i = paramString.indexOf(' ');
    String str1 = paramString;
    if (i > 0)
      str1 = paramString.substring(0, i); 
    FamilyDescription familyDescription = (FamilyDescription)platformFontMap.get(str1);
    if (familyDescription == null)
      return null; 
    byte b = -1;
    if (paramString.equalsIgnoreCase(familyDescription.plainFullName)) {
      b = 0;
    } else if (paramString.equalsIgnoreCase(familyDescription.boldFullName)) {
      b = 1;
    } else if (paramString.equalsIgnoreCase(familyDescription.italicFullName)) {
      b = 2;
    } else if (paramString.equalsIgnoreCase(familyDescription.boldItalicFullName)) {
      b = 3;
    } 
    if (b == -1 && !paramString.equalsIgnoreCase(familyDescription.familyName))
      return null; 
    String str2 = null;
    String str3 = null;
    String str4 = null;
    String str5 = null;
    boolean bool = false;
    getPlatformFontDirs(noType1Font);
    if (familyDescription.plainFileName != null) {
      str2 = getPathName(familyDescription.plainFileName);
      if (str2 == null)
        bool = true; 
    } 
    if (familyDescription.boldFileName != null) {
      str3 = getPathName(familyDescription.boldFileName);
      if (str3 == null)
        bool = true; 
    } 
    if (familyDescription.italicFileName != null) {
      str4 = getPathName(familyDescription.italicFileName);
      if (str4 == null)
        bool = true; 
    } 
    if (familyDescription.boldItalicFileName != null) {
      str5 = getPathName(familyDescription.boldItalicFileName);
      if (str5 == null)
        bool = true; 
    } 
    if (bool) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Hardcoded file missing looking for " + paramString); 
      platformFontMap.remove(str1);
      return null;
    } 
    final String[] files = { str2, str3, str4, str5 };
    bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            for (byte b = 0; b < files.length; b++) {
              if (files[b] != null) {
                File file = new File(files[b]);
                if (!file.exists())
                  return Boolean.TRUE; 
              } 
            } 
            return Boolean.FALSE;
          }
        })).booleanValue();
    if (bool) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Hardcoded file missing looking for " + paramString); 
      platformFontMap.remove(str1);
      return null;
    } 
    Font2D font2D = null;
    for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
      if (arrayOfString[b1] != null) {
        PhysicalFont physicalFont = registerFontFile(arrayOfString[b1], null, 0, false, 3);
        if (b1 == b)
          font2D = physicalFont; 
      } 
    } 
    FontFamily fontFamily = FontFamily.getFamily(familyDescription.familyName);
    if (fontFamily != null)
      if (font2D == null) {
        font2D = fontFamily.getFont(paramInt);
        if (font2D == null)
          font2D = fontFamily.getClosestStyle(paramInt); 
      } else if (paramInt > 0 && paramInt != font2D.style) {
        paramInt |= font2D.style;
        font2D = fontFamily.getFont(paramInt);
        if (font2D == null)
          font2D = fontFamily.getClosestStyle(paramInt); 
      }  
    return font2D;
  }
  
  private HashMap<String, String> getFullNameToFileMap() {
    if (this.fontToFileMap == null) {
      this.pathDirs = getPlatformFontDirs(noType1Font);
      this.fontToFileMap = new HashMap(100);
      this.fontToFamilyNameMap = new HashMap(100);
      this.familyToFontListMap = new HashMap(50);
      populateFontFileNameMap(this.fontToFileMap, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
      if (FontUtilities.isWindows)
        resolveWindowsFonts(); 
      if (FontUtilities.isLogging())
        logPlatformFontInfo(); 
    } 
    return this.fontToFileMap;
  }
  
  private void logPlatformFontInfo() {
    PlatformLogger platformLogger = FontUtilities.getLogger();
    for (byte b = 0; b < this.pathDirs.length; b++)
      platformLogger.info("fontdir=" + this.pathDirs[b]); 
    for (String str : this.fontToFileMap.keySet())
      platformLogger.info("font=" + str + " file=" + (String)this.fontToFileMap.get(str)); 
    for (String str : this.fontToFamilyNameMap.keySet())
      platformLogger.info("font=" + str + " family=" + (String)this.fontToFamilyNameMap.get(str)); 
    for (String str : this.familyToFontListMap.keySet())
      platformLogger.info("family=" + str + " fonts=" + this.familyToFontListMap.get(str)); 
  }
  
  protected String[] getFontNamesFromPlatform() {
    if (getFullNameToFileMap().size() == 0)
      return null; 
    checkForUnreferencedFontFiles();
    ArrayList arrayList = new ArrayList();
    for (ArrayList arrayList1 : this.familyToFontListMap.values()) {
      for (String str : arrayList1)
        arrayList.add(str); 
    } 
    return (String[])arrayList.toArray(STR_ARRAY);
  }
  
  public boolean gotFontsFromPlatform() { return (getFullNameToFileMap().size() != 0); }
  
  public String getFileNameForFontName(String paramString) {
    String str = paramString.toLowerCase(Locale.ENGLISH);
    return (String)this.fontToFileMap.get(str);
  }
  
  private PhysicalFont registerFontFile(String paramString) {
    if ((new File(paramString)).isAbsolute() && !this.registeredFonts.contains(paramString)) {
      byte b = -1;
      byte b1 = 6;
      if (ttFilter.accept(null, paramString)) {
        b = 0;
        b1 = 3;
      } else if (t1Filter.accept(null, paramString)) {
        b = 1;
        b1 = 4;
      } 
      return (b == -1) ? null : registerFontFile(paramString, null, b, false, b1);
    } 
    return null;
  }
  
  protected void registerOtherFontFiles(HashSet paramHashSet) {
    if (getFullNameToFileMap().size() == 0)
      return; 
    for (String str : this.fontToFileMap.values())
      registerFontFile(str); 
  }
  
  public boolean getFamilyNamesFromPlatform(TreeMap<String, String> paramTreeMap, Locale paramLocale) {
    if (getFullNameToFileMap().size() == 0)
      return false; 
    checkForUnreferencedFontFiles();
    for (String str : this.fontToFamilyNameMap.values())
      paramTreeMap.put(str.toLowerCase(paramLocale), str); 
    return true;
  }
  
  private String getPathName(final String s) {
    File file = new File(paramString);
    if (file.isAbsolute())
      return paramString; 
    if (this.pathDirs.length == 1)
      return this.pathDirs[0] + File.separator + paramString; 
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            for (byte b = 0; b < SunFontManager.this.pathDirs.length; b++) {
              File file = new File(SunFontManager.this.pathDirs[b] + File.separator + s);
              if (file.exists())
                return file.getAbsolutePath(); 
            } 
            return null;
          }
        });
    return (str != null) ? str : paramString;
  }
  
  private Font2D findFontFromPlatform(String paramString, int paramInt) {
    if (getFullNameToFileMap().size() == 0)
      return null; 
    ArrayList arrayList = null;
    String str1 = null;
    String str2 = (String)this.fontToFamilyNameMap.get(paramString);
    if (str2 != null) {
      str1 = (String)this.fontToFileMap.get(paramString);
      arrayList = (ArrayList)this.familyToFontListMap.get(str2.toLowerCase(Locale.ENGLISH));
    } else {
      arrayList = (ArrayList)this.familyToFontListMap.get(paramString);
      if (arrayList != null && arrayList.size() > 0) {
        String str = ((String)arrayList.get(0)).toLowerCase(Locale.ENGLISH);
        if (str != null)
          str2 = (String)this.fontToFamilyNameMap.get(str); 
      } 
    } 
    if (arrayList == null || str2 == null)
      return null; 
    String[] arrayOfString = (String[])arrayList.toArray(STR_ARRAY);
    if (arrayOfString.length == 0)
      return null; 
    for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
      String str3 = arrayOfString[b1].toLowerCase(Locale.ENGLISH);
      String str4 = (String)this.fontToFileMap.get(str3);
      if (str4 == null) {
        if (FontUtilities.isLogging())
          FontUtilities.getLogger().info("Platform lookup : No file for font " + arrayOfString[b1] + " in family " + str2); 
        return null;
      } 
    } 
    PhysicalFont physicalFont = null;
    if (str1 != null)
      physicalFont = registerFontFile(getPathName(str1), null, 0, false, 3); 
    for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
      String str3 = arrayOfString[b2].toLowerCase(Locale.ENGLISH);
      String str4 = (String)this.fontToFileMap.get(str3);
      if (str1 == null || !str1.equals(str4))
        registerFontFile(getPathName(str4), null, 0, false, 3); 
    } 
    Font2D font2D = null;
    FontFamily fontFamily = FontFamily.getFamily(str2);
    if (physicalFont != null)
      paramInt |= physicalFont.style; 
    if (fontFamily != null) {
      font2D = fontFamily.getFont(paramInt);
      if (font2D == null)
        font2D = fontFamily.getClosestStyle(paramInt); 
    } 
    return font2D;
  }
  
  public Font2D findFont2D(String paramString, int paramInt1, int paramInt2) {
    String str1 = paramString.toLowerCase(Locale.ENGLISH);
    String str2 = str1 + dotStyleStr(paramInt1);
    if (this._usingPerAppContextComposites) {
      ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap)AppContext.getAppContext().get(CompositeFont.class);
      if (concurrentHashMap != null) {
        font2D = (Font2D)concurrentHashMap.get(str2);
      } else {
        font2D = null;
      } 
    } else {
      font2D = (Font2D)this.fontNameCache.get(str2);
    } 
    if (font2D != null)
      return font2D; 
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Search for font: " + paramString); 
    if (FontUtilities.isWindows)
      if (str1.equals("ms sans serif")) {
        paramString = "sansserif";
      } else if (str1.equals("ms serif")) {
        paramString = "serif";
      }  
    if (str1.equals("default"))
      paramString = "dialog"; 
    FontFamily fontFamily = FontFamily.getFamily(paramString);
    if (fontFamily != null) {
      font2D = fontFamily.getFontWithExactStyleMatch(paramInt1);
      if (font2D == null)
        font2D = findDeferredFont(paramString, paramInt1); 
      if (font2D == null)
        font2D = fontFamily.getFont(paramInt1); 
      if (font2D == null)
        font2D = fontFamily.getClosestStyle(paramInt1); 
      if (font2D != null) {
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
    } 
    Font2D font2D = (Font2D)this.fullNameToFont.get(str1);
    if (font2D != null) {
      if (font2D.style == paramInt1 || paramInt1 == 0) {
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
      fontFamily = FontFamily.getFamily(font2D.getFamilyName(null));
      if (fontFamily != null) {
        Font2D font2D1 = fontFamily.getFont(paramInt1 | font2D.style);
        if (font2D1 != null) {
          this.fontNameCache.put(str2, font2D1);
          return font2D1;
        } 
        font2D1 = fontFamily.getClosestStyle(paramInt1 | font2D.style);
        if (font2D1 != null && font2D1.canDoStyle(paramInt1 | font2D.style)) {
          this.fontNameCache.put(str2, font2D1);
          return font2D1;
        } 
      } 
    } 
    if (FontUtilities.isWindows) {
      font2D = findFontFromPlatformMap(str1, paramInt1);
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("findFontFromPlatformMap returned " + font2D); 
      if (font2D != null) {
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
      if (this.deferredFontFiles.size() > 0) {
        font2D = findJREDeferredFont(str1, paramInt1);
        if (font2D != null) {
          this.fontNameCache.put(str2, font2D);
          return font2D;
        } 
      } 
      font2D = findFontFromPlatform(str1, paramInt1);
      if (font2D != null) {
        if (FontUtilities.isLogging())
          FontUtilities.getLogger().info("Found font via platform API for request:\"" + paramString + "\":, style=" + paramInt1 + " found font: " + font2D); 
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
    } 
    if (this.deferredFontFiles.size() > 0) {
      font2D = findDeferredFont(paramString, paramInt1);
      if (font2D != null) {
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
    } 
    if (FontUtilities.isSolaris && !this.loaded1dot0Fonts) {
      if (str1.equals("timesroman")) {
        font2D = findFont2D("serif", paramInt1, paramInt2);
        this.fontNameCache.put(str2, font2D);
      } 
      register1dot0Fonts();
      this.loaded1dot0Fonts = true;
      return findFont2D(paramString, paramInt1, paramInt2);
    } 
    if (this.fontsAreRegistered || this.fontsAreRegisteredPerAppContext) {
      Hashtable hashtable2;
      Hashtable hashtable1 = null;
      if (this.fontsAreRegistered) {
        hashtable1 = this.createdByFamilyName;
        hashtable2 = this.createdByFullName;
      } else {
        AppContext appContext = AppContext.getAppContext();
        hashtable1 = (Hashtable)appContext.get(regFamilyKey);
        hashtable2 = (Hashtable)appContext.get(regFullNameKey);
      } 
      fontFamily = (FontFamily)hashtable1.get(str1);
      if (fontFamily != null) {
        font2D = fontFamily.getFontWithExactStyleMatch(paramInt1);
        if (font2D == null)
          font2D = fontFamily.getFont(paramInt1); 
        if (font2D == null)
          font2D = fontFamily.getClosestStyle(paramInt1); 
        if (font2D != null) {
          if (this.fontsAreRegistered)
            this.fontNameCache.put(str2, font2D); 
          return font2D;
        } 
      } 
      font2D = (Font2D)hashtable2.get(str1);
      if (font2D != null) {
        if (this.fontsAreRegistered)
          this.fontNameCache.put(str2, font2D); 
        return font2D;
      } 
    } 
    if (!this.loadedAllFonts) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Load fonts looking for:" + paramString); 
      loadFonts();
      this.loadedAllFonts = true;
      return findFont2D(paramString, paramInt1, paramInt2);
    } 
    if (!this.loadedAllFontFiles) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().info("Load font files looking for:" + paramString); 
      loadFontFiles();
      this.loadedAllFontFiles = true;
      return findFont2D(paramString, paramInt1, paramInt2);
    } 
    if ((font2D = findFont2DAllLocales(paramString, paramInt1)) != null) {
      this.fontNameCache.put(str2, font2D);
      return font2D;
    } 
    if (FontUtilities.isWindows) {
      String str = getFontConfiguration().getFallbackFamilyName(paramString, null);
      if (str != null) {
        font2D = findFont2D(str, paramInt1, paramInt2);
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
    } else {
      if (str1.equals("timesroman")) {
        font2D = findFont2D("serif", paramInt1, paramInt2);
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
      if (str1.equals("helvetica")) {
        font2D = findFont2D("sansserif", paramInt1, paramInt2);
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
      if (str1.equals("courier")) {
        font2D = findFont2D("monospaced", paramInt1, paramInt2);
        this.fontNameCache.put(str2, font2D);
        return font2D;
      } 
    } 
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("No font found for:" + paramString); 
    switch (paramInt2) {
      case 1:
        return getDefaultPhysicalFont();
      case 2:
        return getDefaultLogicalFont(paramInt1);
    } 
    return null;
  }
  
  public boolean usePlatformFontMetrics() { return this.usePlatformFontMetrics; }
  
  public int getNumFonts() { return this.physicalFonts.size() + this.maxCompFont; }
  
  private static boolean fontSupportsEncoding(Font paramFont, String paramString) { return FontUtilities.getFont2D(paramFont).supportsEncoding(paramString); }
  
  protected abstract String getFontPath(boolean paramBoolean);
  
  public Font2D createFont2D(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker) throws FontFormatException {
    String str = paramFile.getPath();
    Type1Font type1Font = null;
    final File fFile = paramFile;
    final CreatedFontTracker _tracker = paramCreatedFontTracker;
    try {
      switch (paramInt) {
        case 0:
          type1Font = new TrueTypeFont(str, null, 0, true);
          break;
        case 1:
          type1Font = new Type1Font(str, null, paramBoolean);
          break;
        default:
          throw new FontFormatException("Unrecognised Font Format");
      } 
    } catch (FontFormatException fontFormatException) {
      if (paramBoolean)
        AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() {
                if (_tracker != null)
                  _tracker.subBytes((int)fFile.length()); 
                fFile.delete();
                return null;
              }
            }); 
      throw fontFormatException;
    } 
    if (paramBoolean) {
      type1Font.setFileToRemove(paramFile, paramCreatedFontTracker);
      synchronized (FontManager.class) {
        if (this.tmpFontFiles == null)
          this.tmpFontFiles = new Vector(); 
        this.tmpFontFiles.add(paramFile);
        if (this.fileCloser == null) {
          Runnable runnable = new Runnable() {
              public void run() { AccessController.doPrivileged(new PrivilegedAction() {
                      public Object run() {
                        for (byte b = 0; b < 20; b++) {
                          if (SunFontManager.this.fontFileCache[b] != null)
                            try {
                              SunFontManager.this.fontFileCache[b].close();
                            } catch (Exception exception) {} 
                        } 
                        if (SunFontManager.this.tmpFontFiles != null) {
                          File[] arrayOfFile = new File[SunFontManager.this.tmpFontFiles.size()];
                          arrayOfFile = (File[])SunFontManager.this.tmpFontFiles.toArray(arrayOfFile);
                          for (byte b1 = 0; b1 < arrayOfFile.length; b1++) {
                            try {
                              arrayOfFile[b1].delete();
                            } catch (Exception exception) {}
                          } 
                        } 
                        return null;
                      }
                    }); }
            };
          AccessController.doPrivileged(() -> {
                ThreadGroup threadGroup = ThreadGroupUtils.getRootThreadGroup();
                this.fileCloser = new Thread(threadGroup, paramRunnable);
                this.fileCloser.setContextClassLoader(null);
                Runtime.getRuntime().addShutdownHook(this.fileCloser);
                return null;
              });
        } 
      } 
    } 
    return type1Font;
  }
  
  public String getFullNameByFileName(String paramString) {
    PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
    for (byte b = 0; b < arrayOfPhysicalFont.length; b++) {
      if ((arrayOfPhysicalFont[b]).platName.equals(paramString))
        return arrayOfPhysicalFont[b].getFontName(null); 
    } 
    return null;
  }
  
  public void deRegisterBadFont(Font2D paramFont2D) {
    if (!(paramFont2D instanceof PhysicalFont))
      return; 
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().severe("Deregister bad font: " + paramFont2D); 
    replaceFont((PhysicalFont)paramFont2D, getDefaultPhysicalFont());
  }
  
  public void replaceFont(PhysicalFont paramPhysicalFont1, PhysicalFont paramPhysicalFont2) {
    if (paramPhysicalFont1.handle.font2D != paramPhysicalFont1)
      return; 
    if (paramPhysicalFont1 == paramPhysicalFont2) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().severe("Can't replace bad font with itself " + paramPhysicalFont1); 
      PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
      for (byte b1 = 0; b1 < arrayOfPhysicalFont.length; b1++) {
        if (arrayOfPhysicalFont[b1] != paramPhysicalFont2) {
          paramPhysicalFont2 = arrayOfPhysicalFont[b1];
          break;
        } 
      } 
      if (paramPhysicalFont1 == paramPhysicalFont2) {
        if (FontUtilities.isLogging())
          FontUtilities.getLogger().severe("This is bad. No good physicalFonts found."); 
        return;
      } 
    } 
    paramPhysicalFont1.handle.font2D = paramPhysicalFont2;
    this.physicalFonts.remove(paramPhysicalFont1.fullName);
    this.fullNameToFont.remove(paramPhysicalFont1.fullName.toLowerCase(Locale.ENGLISH));
    FontFamily.remove(paramPhysicalFont1);
    if (this.localeFullNamesToFont != null) {
      Entry[] arrayOfEntry = (Entry[])this.localeFullNamesToFont.entrySet().toArray(new java.util.Map.Entry[0]);
      for (byte b1 = 0; b1 < arrayOfEntry.length; b1++) {
        if (arrayOfEntry[b1].getValue() == paramPhysicalFont1)
          try {
            arrayOfEntry[b1].setValue(paramPhysicalFont2);
          } catch (Exception exception) {
            this.localeFullNamesToFont.remove(arrayOfEntry[b1].getKey());
          }  
      } 
    } 
    for (byte b = 0; b < this.maxCompFont; b++) {
      if (paramPhysicalFont2.getRank() > 2)
        this.compFonts[b].replaceComponentFont(paramPhysicalFont1, paramPhysicalFont2); 
    } 
  }
  
  private void loadLocaleNames() {
    if (this.localeFullNamesToFont != null)
      return; 
    this.localeFullNamesToFont = new HashMap();
    Font2D[] arrayOfFont2D = getRegisteredFonts();
    for (byte b = 0; b < arrayOfFont2D.length; b++) {
      if (arrayOfFont2D[b] instanceof TrueTypeFont) {
        TrueTypeFont trueTypeFont = (TrueTypeFont)arrayOfFont2D[b];
        String[] arrayOfString = trueTypeFont.getAllFullNames();
        for (byte b1 = 0; b1 < arrayOfString.length; b1++)
          this.localeFullNamesToFont.put(arrayOfString[b1], trueTypeFont); 
        FontFamily fontFamily = FontFamily.getFamily(trueTypeFont.familyName);
        if (fontFamily != null)
          FontFamily.addLocaleNames(fontFamily, trueTypeFont.getAllFamilyNames()); 
      } 
    } 
  }
  
  private Font2D findFont2DAllLocales(String paramString, int paramInt) {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Searching localised font names for:" + paramString); 
    if (this.localeFullNamesToFont == null)
      loadLocaleNames(); 
    String str = paramString.toLowerCase();
    Font2D font2D = null;
    FontFamily fontFamily = FontFamily.getLocaleFamily(str);
    if (fontFamily != null) {
      font2D = fontFamily.getFont(paramInt);
      if (font2D == null)
        font2D = fontFamily.getClosestStyle(paramInt); 
      if (font2D != null)
        return font2D; 
    } 
    synchronized (this) {
      font2D = (Font2D)this.localeFullNamesToFont.get(paramString);
    } 
    if (font2D != null) {
      if (font2D.style == paramInt || paramInt == 0)
        return font2D; 
      fontFamily = FontFamily.getFamily(font2D.getFamilyName(null));
      if (fontFamily != null) {
        Font2D font2D1 = fontFamily.getFont(paramInt);
        if (font2D1 != null)
          return font2D1; 
        font2D1 = fontFamily.getClosestStyle(paramInt);
        if (font2D1 != null) {
          if (!font2D1.canDoStyle(paramInt))
            font2D1 = null; 
          return font2D1;
        } 
      } 
    } 
    return font2D;
  }
  
  public boolean maybeUsingAlternateCompositeFonts() { return (this._usingAlternateComposites || this._usingPerAppContextComposites); }
  
  public boolean usingAlternateCompositeFonts() { return (this._usingAlternateComposites || (this._usingPerAppContextComposites && AppContext.getAppContext().get(CompositeFont.class) != null)); }
  
  private static boolean maybeMultiAppContext() {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            SecurityManager securityManager = System.getSecurityManager();
            return new Boolean(securityManager instanceof sun.applet.AppletSecurity);
          }
        });
    return bool.booleanValue();
  }
  
  public void useAlternateFontforJALocales() {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Entered useAlternateFontforJALocales()."); 
    if (!FontUtilities.isWindows)
      return; 
    if (!maybeMultiAppContext()) {
      gAltJAFont = true;
    } else {
      AppContext appContext = AppContext.getAppContext();
      appContext.put(altJAFontKey, altJAFontKey);
    } 
  }
  
  public boolean usingAlternateFontforJALocales() {
    if (!maybeMultiAppContext())
      return gAltJAFont; 
    AppContext appContext = AppContext.getAppContext();
    return (appContext.get(altJAFontKey) == altJAFontKey);
  }
  
  public void preferLocaleFonts() {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Entered preferLocaleFonts()."); 
    if (!FontConfiguration.willReorderForStartupLocale())
      return; 
    if (!maybeMultiAppContext()) {
      if (this.gLocalePref == true)
        return; 
      this.gLocalePref = true;
      createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
      this._usingAlternateComposites = true;
    } else {
      AppContext appContext = AppContext.getAppContext();
      if (appContext.get(localeFontKey) == localeFontKey)
        return; 
      appContext.put(localeFontKey, localeFontKey);
      boolean bool = (appContext.get(proportionalFontKey) == proportionalFontKey);
      ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
      appContext.put(CompositeFont.class, concurrentHashMap);
      this._usingPerAppContextComposites = true;
      createCompositeFonts(concurrentHashMap, true, bool);
    } 
  }
  
  public void preferProportionalFonts() {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Entered preferProportionalFonts()."); 
    if (!FontConfiguration.hasMonoToPropMap())
      return; 
    if (!maybeMultiAppContext()) {
      if (this.gPropPref == true)
        return; 
      this.gPropPref = true;
      createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
      this._usingAlternateComposites = true;
    } else {
      AppContext appContext = AppContext.getAppContext();
      if (appContext.get(proportionalFontKey) == proportionalFontKey)
        return; 
      appContext.put(proportionalFontKey, proportionalFontKey);
      boolean bool = (appContext.get(localeFontKey) == localeFontKey);
      ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
      appContext.put(CompositeFont.class, concurrentHashMap);
      this._usingPerAppContextComposites = true;
      createCompositeFonts(concurrentHashMap, bool, true);
    } 
  }
  
  private static HashSet<String> getInstalledNames() {
    if (installedNames == null) {
      Locale locale = getSystemStartupLocale();
      SunFontManager sunFontManager = getInstance();
      String[] arrayOfString = sunFontManager.getInstalledFontFamilyNames(locale);
      Font[] arrayOfFont = sunFontManager.getAllInstalledFonts();
      HashSet hashSet = new HashSet();
      byte b;
      for (b = 0; b < arrayOfString.length; b++)
        hashSet.add(arrayOfString[b].toLowerCase(locale)); 
      for (b = 0; b < arrayOfFont.length; b++)
        hashSet.add(arrayOfFont[b].getFontName(locale).toLowerCase(locale)); 
      installedNames = hashSet;
    } 
    return installedNames;
  }
  
  public boolean registerFont(Font paramFont) {
    Hashtable hashtable2;
    Hashtable hashtable1;
    if (paramFont == null)
      return false; 
    synchronized (regFamilyKey) {
      if (this.createdByFamilyName == null) {
        this.createdByFamilyName = new Hashtable();
        this.createdByFullName = new Hashtable();
      } 
    } 
    if (!FontAccess.getFontAccess().isCreatedFont(paramFont))
      return false; 
    HashSet hashSet = getInstalledNames();
    Locale locale = getSystemStartupLocale();
    String str1 = paramFont.getFamily(locale).toLowerCase();
    String str2 = paramFont.getFontName(locale).toLowerCase();
    if (hashSet.contains(str1) || hashSet.contains(str2))
      return false; 
    if (!maybeMultiAppContext()) {
      hashtable1 = this.createdByFamilyName;
      hashtable2 = this.createdByFullName;
      this.fontsAreRegistered = true;
    } else {
      AppContext appContext = AppContext.getAppContext();
      hashtable1 = (Hashtable)appContext.get(regFamilyKey);
      hashtable2 = (Hashtable)appContext.get(regFullNameKey);
      if (hashtable1 == null) {
        hashtable1 = new Hashtable();
        hashtable2 = new Hashtable();
        appContext.put(regFamilyKey, hashtable1);
        appContext.put(regFullNameKey, hashtable2);
      } 
      this.fontsAreRegisteredPerAppContext = true;
    } 
    Font2D font2D = FontUtilities.getFont2D(paramFont);
    int i = font2D.getStyle();
    FontFamily fontFamily = (FontFamily)hashtable1.get(str1);
    if (fontFamily == null) {
      fontFamily = new FontFamily(paramFont.getFamily(locale));
      hashtable1.put(str1, fontFamily);
    } 
    if (this.fontsAreRegistered) {
      removeFromCache(fontFamily.getFont(0));
      removeFromCache(fontFamily.getFont(1));
      removeFromCache(fontFamily.getFont(2));
      removeFromCache(fontFamily.getFont(3));
      removeFromCache((Font2D)hashtable2.get(str2));
    } 
    fontFamily.setFont(font2D, i);
    hashtable2.put(str2, font2D);
    return true;
  }
  
  private void removeFromCache(Font2D paramFont2D) {
    if (paramFont2D == null)
      return; 
    String[] arrayOfString = (String[])this.fontNameCache.keySet().toArray(STR_ARRAY);
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (this.fontNameCache.get(arrayOfString[b]) == paramFont2D)
        this.fontNameCache.remove(arrayOfString[b]); 
    } 
  }
  
  public TreeMap<String, String> getCreatedFontFamilyNames() {
    Hashtable hashtable;
    if (this.fontsAreRegistered) {
      hashtable = this.createdByFamilyName;
    } else if (this.fontsAreRegisteredPerAppContext) {
      AppContext appContext = AppContext.getAppContext();
      hashtable = (Hashtable)appContext.get(regFamilyKey);
    } else {
      return null;
    } 
    Locale locale = getSystemStartupLocale();
    synchronized (hashtable) {
      TreeMap treeMap = new TreeMap();
      for (FontFamily fontFamily : hashtable.values()) {
        Font2D font2D = fontFamily.getFont(0);
        if (font2D == null)
          font2D = fontFamily.getClosestStyle(0); 
        String str = font2D.getFamilyName(locale);
        treeMap.put(str.toLowerCase(locale), str);
      } 
      return treeMap;
    } 
  }
  
  public Font[] getCreatedFonts() {
    Hashtable hashtable;
    if (this.fontsAreRegistered) {
      hashtable = this.createdByFullName;
    } else if (this.fontsAreRegisteredPerAppContext) {
      AppContext appContext = AppContext.getAppContext();
      hashtable = (Hashtable)appContext.get(regFullNameKey);
    } else {
      return null;
    } 
    Locale locale = getSystemStartupLocale();
    synchronized (hashtable) {
      Font[] arrayOfFont = new Font[hashtable.size()];
      byte b = 0;
      for (Font2D font2D : hashtable.values())
        arrayOfFont[b++] = new Font(font2D.getFontName(locale), 0, 1); 
      return arrayOfFont;
    } 
  }
  
  protected String[] getPlatformFontDirs(boolean paramBoolean) {
    if (this.pathDirs != null)
      return this.pathDirs; 
    String str = getPlatformFontPath(paramBoolean);
    StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
    ArrayList arrayList = new ArrayList();
    try {
      while (stringTokenizer.hasMoreTokens())
        arrayList.add(stringTokenizer.nextToken()); 
    } catch (NoSuchElementException noSuchElementException) {}
    this.pathDirs = (String[])arrayList.toArray(new String[0]);
    return this.pathDirs;
  }
  
  protected abstract String[] getDefaultPlatformFont();
  
  private void addDirFonts(String paramString, File paramFile, FilenameFilter paramFilenameFilter, int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3) {
    String[] arrayOfString1 = paramFile.list(paramFilenameFilter);
    if (arrayOfString1 == null || arrayOfString1.length == 0)
      return; 
    String[] arrayOfString2 = new String[arrayOfString1.length];
    String[][] arrayOfString = new String[arrayOfString1.length][];
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfString1.length; b2++) {
      File file = new File(paramFile, arrayOfString1[b2]);
      String str = null;
      if (paramBoolean3)
        try {
          str = file.getCanonicalPath();
        } catch (IOException iOException) {} 
      if (str == null)
        str = paramString + File.separator + arrayOfString1[b2]; 
      if (!this.registeredFontFiles.contains(str))
        if (this.badFonts != null && this.badFonts.contains(str)) {
          if (FontUtilities.debugFonts())
            FontUtilities.getLogger().warning("skip bad font " + str); 
        } else {
          this.registeredFontFiles.add(str);
          if (FontUtilities.debugFonts() && FontUtilities.getLogger().isLoggable(PlatformLogger.Level.INFO)) {
            String str1 = "Registering font " + str;
            String[] arrayOfString3 = getNativeNames(str, null);
            if (arrayOfString3 == null) {
              str1 = str1 + " with no native name";
            } else {
              str1 = str1 + " with native name(s) " + arrayOfString3[0];
              for (byte b = 1; b < arrayOfString3.length; b++)
                str1 = str1 + ", " + arrayOfString3[b]; 
            } 
            FontUtilities.getLogger().info(str1);
          } 
          arrayOfString2[b1] = str;
          arrayOfString[b1++] = getNativeNames(str, null);
        }  
    } 
    registerFonts(arrayOfString2, arrayOfString, b1, paramInt1, paramBoolean1, paramInt2, paramBoolean2);
  }
  
  protected String[] getNativeNames(String paramString1, String paramString2) { return null; }
  
  protected String getFileNameFromPlatformName(String paramString) { return this.fontConfig.getFileNameFromPlatformName(paramString); }
  
  public FontConfiguration getFontConfiguration() { return this.fontConfig; }
  
  public String getPlatformFontPath(boolean paramBoolean) {
    if (this.fontPath == null)
      this.fontPath = getFontPath(paramBoolean); 
    return this.fontPath;
  }
  
  public static boolean isOpenJDK() { return FontUtilities.isOpenJDK; }
  
  protected void loadFonts() {
    if (this.discoveredAllFonts)
      return; 
    synchronized (this) {
      if (FontUtilities.debugFonts()) {
        Thread.dumpStack();
        FontUtilities.getLogger().info("SunGraphicsEnvironment.loadFonts() called");
      } 
      initialiseDeferredFonts();
      AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              if (SunFontManager.this.fontPath == null) {
                SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
                SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
              } 
              if (SunFontManager.this.fontPath != null && !SunFontManager.this.gotFontsFromPlatform()) {
                SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
                SunFontManager.this.loadedAllFontFiles = true;
              } 
              SunFontManager.this.registerOtherFontFiles(SunFontManager.this.registeredFontFiles);
              SunFontManager.this.discoveredAllFonts = true;
              return null;
            }
          });
    } 
  }
  
  protected void registerFontDirs(String paramString) {}
  
  private void registerFontsOnPath(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
    try {
      while (stringTokenizer.hasMoreTokens())
        registerFontsInDir(stringTokenizer.nextToken(), paramBoolean1, paramInt, paramBoolean2, paramBoolean3); 
    } catch (NoSuchElementException noSuchElementException) {}
  }
  
  public void registerFontsInDir(String paramString) { registerFontsInDir(paramString, true, 2, true, false); }
  
  protected void registerFontsInDir(String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2, boolean paramBoolean3) {
    File file = new File(paramString);
    addDirFonts(paramString, file, ttFilter, 0, paramBoolean1, (paramInt == 6) ? 3 : paramInt, paramBoolean2, paramBoolean3);
    addDirFonts(paramString, file, t1Filter, 1, paramBoolean1, (paramInt == 6) ? 4 : paramInt, paramBoolean2, paramBoolean3);
  }
  
  protected void registerFontDir(String paramString) {}
  
  public String getDefaultFontFile() {
    if (this.defaultFontFileName == null)
      initDefaultFonts(); 
    return this.defaultFontFileName;
  }
  
  private void initDefaultFonts() {
    if (!isOpenJDK()) {
      this.defaultFontName = "Lucida Sans Regular";
      if (useAbsoluteFontFileNames()) {
        this.defaultFontFileName = jreFontDirName + File.separator + "LucidaSansRegular.ttf";
      } else {
        this.defaultFontFileName = "LucidaSansRegular.ttf";
      } 
    } 
  }
  
  protected boolean useAbsoluteFontFileNames() { return true; }
  
  protected abstract FontConfiguration createFontConfiguration();
  
  public abstract FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2);
  
  public String getDefaultFontFaceName() {
    if (this.defaultFontName == null)
      initDefaultFonts(); 
    return this.defaultFontName;
  }
  
  public void loadFontFiles() {
    loadFonts();
    if (this.loadedAllFontFiles)
      return; 
    synchronized (this) {
      if (FontUtilities.debugFonts()) {
        Thread.dumpStack();
        FontUtilities.getLogger().info("loadAllFontFiles() called");
      } 
      AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              if (SunFontManager.this.fontPath == null)
                SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font); 
              if (SunFontManager.this.fontPath != null)
                SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true); 
              SunFontManager.this.loadedAllFontFiles = true;
              return null;
            }
          });
    } 
  }
  
  private void initCompositeFonts(FontConfiguration paramFontConfiguration, ConcurrentHashMap<String, Font2D> paramConcurrentHashMap) {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().info("Initialising composite fonts"); 
    int i = paramFontConfiguration.getNumberCoreFonts();
    String[] arrayOfString = paramFontConfiguration.getPlatformFontNames();
    for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
      String str1 = arrayOfString[b1];
      String str2 = getFileNameFromPlatformName(str1);
      String[] arrayOfString1 = null;
      if (str2 == null || str2.equals(str1)) {
        str2 = str1;
      } else {
        if (b1 < i)
          addFontToPlatformFontPath(str1); 
        arrayOfString1 = getNativeNames(str2, str1);
      } 
      registerFontFile(str2, arrayOfString1, 2, true);
    } 
    registerPlatformFontsUsedByFontConfiguration();
    CompositeFontDescriptor[] arrayOfCompositeFontDescriptor = paramFontConfiguration.get2DCompositeFontInfo();
    for (byte b2 = 0; b2 < arrayOfCompositeFontDescriptor.length; b2++) {
      CompositeFontDescriptor compositeFontDescriptor = arrayOfCompositeFontDescriptor[b2];
      String[] arrayOfString1 = compositeFontDescriptor.getComponentFileNames();
      String[] arrayOfString2 = compositeFontDescriptor.getComponentFaceNames();
      if (missingFontFiles != null)
        for (byte b = 0; b < arrayOfString1.length; b++) {
          if (missingFontFiles.contains(arrayOfString1[b])) {
            arrayOfString1[b] = getDefaultFontFile();
            arrayOfString2[b] = getDefaultFontFaceName();
          } 
        }  
      if (paramConcurrentHashMap != null) {
        registerCompositeFont(compositeFontDescriptor.getFaceName(), arrayOfString1, arrayOfString2, compositeFontDescriptor.getCoreComponentCount(), compositeFontDescriptor.getExclusionRanges(), compositeFontDescriptor.getExclusionRangeLimits(), true, paramConcurrentHashMap);
      } else {
        registerCompositeFont(compositeFontDescriptor.getFaceName(), arrayOfString1, arrayOfString2, compositeFontDescriptor.getCoreComponentCount(), compositeFontDescriptor.getExclusionRanges(), compositeFontDescriptor.getExclusionRangeLimits(), true);
      } 
      if (FontUtilities.debugFonts())
        FontUtilities.getLogger().info("registered " + compositeFontDescriptor.getFaceName()); 
    } 
  }
  
  protected void addFontToPlatformFontPath(String paramString) {}
  
  protected void registerFontFile(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean) {
    byte b;
    if (this.registeredFontFiles.contains(paramString))
      return; 
    if (ttFilter.accept(null, paramString)) {
      b = 0;
    } else if (t1Filter.accept(null, paramString)) {
      b = 1;
    } else {
      b = 5;
    } 
    this.registeredFontFiles.add(paramString);
    if (paramBoolean) {
      registerDeferredFont(paramString, paramString, paramArrayOfString, b, false, paramInt);
    } else {
      registerFontFile(paramString, paramArrayOfString, b, false, paramInt);
    } 
  }
  
  protected void registerPlatformFontsUsedByFontConfiguration() {}
  
  protected void addToMissingFontFileList(String paramString) {
    if (missingFontFiles == null)
      missingFontFiles = new HashSet(); 
    missingFontFiles.add(paramString);
  }
  
  private boolean isNameForRegisteredFile(String paramString) {
    String str = getFileNameForFontName(paramString);
    return (str == null) ? false : this.registeredFontFiles.contains(str);
  }
  
  public void createCompositeFonts(ConcurrentHashMap<String, Font2D> paramConcurrentHashMap, boolean paramBoolean1, boolean paramBoolean2) {
    FontConfiguration fontConfiguration = createFontConfiguration(paramBoolean1, paramBoolean2);
    initCompositeFonts(fontConfiguration, paramConcurrentHashMap);
  }
  
  public Font[] getAllInstalledFonts() {
    if (this.allFonts == null) {
      loadFonts();
      TreeMap treeMap = new TreeMap();
      Font2D[] arrayOfFont2D = getRegisteredFonts();
      for (byte b1 = 0; b1 < arrayOfFont2D.length; b1++) {
        if (!(arrayOfFont2D[b1] instanceof NativeFont))
          treeMap.put(arrayOfFont2D[b1].getFontName(null), arrayOfFont2D[b1]); 
      } 
      String[] arrayOfString1 = getFontNamesFromPlatform();
      if (arrayOfString1 != null)
        for (byte b = 0; b < arrayOfString1.length; b++) {
          if (!isNameForRegisteredFile(arrayOfString1[b]))
            treeMap.put(arrayOfString1[b], null); 
        }  
      String[] arrayOfString2 = null;
      if (treeMap.size() > 0) {
        arrayOfString2 = new String[treeMap.size()];
        Object[] arrayOfObject = treeMap.keySet().toArray();
        for (byte b = 0; b < arrayOfObject.length; b++)
          arrayOfString2[b] = (String)arrayOfObject[b]; 
      } 
      Font[] arrayOfFont1 = new Font[arrayOfString2.length];
      for (byte b2 = 0; b2 < arrayOfString2.length; b2++) {
        arrayOfFont1[b2] = new Font(arrayOfString2[b2], 0, 1);
        Font2D font2D = (Font2D)treeMap.get(arrayOfString2[b2]);
        if (font2D != null)
          FontAccess.getFontAccess().setFont2D(arrayOfFont1[b2], font2D.handle); 
      } 
      this.allFonts = arrayOfFont1;
    } 
    Font[] arrayOfFont = new Font[this.allFonts.length];
    System.arraycopy(this.allFonts, 0, arrayOfFont, 0, this.allFonts.length);
    return arrayOfFont;
  }
  
  public String[] getInstalledFontFamilyNames(Locale paramLocale) {
    if (paramLocale == null)
      paramLocale = Locale.getDefault(); 
    if (this.allFamilies != null && this.lastDefaultLocale != null && paramLocale.equals(this.lastDefaultLocale)) {
      String[] arrayOfString1 = new String[this.allFamilies.length];
      System.arraycopy(this.allFamilies, 0, arrayOfString1, 0, this.allFamilies.length);
      return arrayOfString1;
    } 
    TreeMap treeMap = new TreeMap();
    String str = "Serif";
    treeMap.put(str.toLowerCase(), str);
    str = "SansSerif";
    treeMap.put(str.toLowerCase(), str);
    str = "Monospaced";
    treeMap.put(str.toLowerCase(), str);
    str = "Dialog";
    treeMap.put(str.toLowerCase(), str);
    str = "DialogInput";
    treeMap.put(str.toLowerCase(), str);
    if (paramLocale.equals(getSystemStartupLocale()) && getFamilyNamesFromPlatform(treeMap, paramLocale)) {
      getJREFontFamilyNames(treeMap, paramLocale);
    } else {
      loadFontFiles();
      PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
      for (byte b1 = 0; b1 < arrayOfPhysicalFont.length; b1++) {
        if (!(arrayOfPhysicalFont[b1] instanceof NativeFont)) {
          String str1 = arrayOfPhysicalFont[b1].getFamilyName(paramLocale);
          treeMap.put(str1.toLowerCase(paramLocale), str1);
        } 
      } 
    } 
    addNativeFontFamilyNames(treeMap, paramLocale);
    String[] arrayOfString = new String[treeMap.size()];
    Object[] arrayOfObject = treeMap.keySet().toArray();
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfString[b] = (String)treeMap.get(arrayOfObject[b]); 
    if (paramLocale.equals(Locale.getDefault())) {
      this.lastDefaultLocale = paramLocale;
      this.allFamilies = new String[arrayOfString.length];
      System.arraycopy(arrayOfString, 0, this.allFamilies, 0, this.allFamilies.length);
    } 
    return arrayOfString;
  }
  
  protected void addNativeFontFamilyNames(TreeMap<String, String> paramTreeMap, Locale paramLocale) {}
  
  public void register1dot0Fonts() { AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str = "/usr/openwin/lib/X11/fonts/Type1";
            SunFontManager.this.registerFontsInDir(str, true, 4, false, false);
            return null;
          }
        }); }
  
  protected void getJREFontFamilyNames(TreeMap<String, String> paramTreeMap, Locale paramLocale) {
    registerDeferredJREFonts(jreFontDirName);
    PhysicalFont[] arrayOfPhysicalFont = getPhysicalFonts();
    for (byte b = 0; b < arrayOfPhysicalFont.length; b++) {
      if (!(arrayOfPhysicalFont[b] instanceof NativeFont)) {
        String str = arrayOfPhysicalFont[b].getFamilyName(paramLocale);
        paramTreeMap.put(str.toLowerCase(paramLocale), str);
      } 
    } 
  }
  
  private static Locale getSystemStartupLocale() {
    if (systemLocale == null)
      systemLocale = (Locale)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              String str1 = System.getProperty("file.encoding", "");
              String str2 = System.getProperty("sun.jnu.encoding");
              if (str2 != null && !str2.equals(str1))
                return Locale.ROOT; 
              String str3 = System.getProperty("user.language", "en");
              String str4 = System.getProperty("user.country", "");
              String str5 = System.getProperty("user.variant", "");
              return new Locale(str3, str4, str5);
            }
          }); 
    return systemLocale;
  }
  
  void addToPool(FileFont paramFileFont) {
    FileFont fileFont = null;
    byte b = -1;
    synchronized (this.fontFileCache) {
      for (byte b1 = 0; b1 < 20; b1++) {
        if (this.fontFileCache[b1] == paramFileFont)
          return; 
        if (this.fontFileCache[b1] == null && b < 0)
          b = b1; 
      } 
      if (b >= 0) {
        this.fontFileCache[b] = paramFileFont;
        return;
      } 
      fileFont = this.fontFileCache[this.lastPoolIndex];
      this.fontFileCache[this.lastPoolIndex] = paramFileFont;
      this.lastPoolIndex = (this.lastPoolIndex + 1) % 20;
    } 
    if (fileFont != null)
      fileFont.close(); 
  }
  
  protected FontUIResource getFontConfigFUIR(String paramString, int paramInt1, int paramInt2) { return new FontUIResource(paramString, paramInt1, paramInt2); }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            File file;
            FontManagerNativeLibrary.load();
            SunFontManager.initIDs();
            switch (StrikeCache.nativeAddressSize) {
              case 8:
                SunFontManager.longAddresses = true;
                SunFontManager.noType1Font = "true".equals(System.getProperty("sun.java2d.noType1Font"));
                SunFontManager.jreLibDirName = System.getProperty("java.home", "") + File.separator + "lib";
                SunFontManager.jreFontDirName = SunFontManager.jreLibDirName + File.separator + "fonts";
                file = new File(SunFontManager.jreFontDirName + File.separator + "LucidaSansRegular.ttf");
                return null;
              case 4:
                SunFontManager.longAddresses = false;
                SunFontManager.noType1Font = "true".equals(System.getProperty("sun.java2d.noType1Font"));
                SunFontManager.jreLibDirName = System.getProperty("java.home", "") + File.separator + "lib";
                SunFontManager.jreFontDirName = SunFontManager.jreLibDirName + File.separator + "fonts";
                file = new File(SunFontManager.jreFontDirName + File.separator + "LucidaSansRegular.ttf");
                return null;
            } 
            throw new RuntimeException("Unexpected address size");
          }
        });
    altJAFontKey = new Object();
    localeFontKey = new Object();
    proportionalFontKey = new Object();
    gAltJAFont = false;
    installedNames = null;
    regFamilyKey = new Object();
    regFullNameKey = new Object();
    systemLocale = null;
  }
  
  public static class FamilyDescription {
    public String familyName;
    
    public String plainFullName;
    
    public String boldFullName;
    
    public String italicFullName;
    
    public String boldItalicFullName;
    
    public String plainFileName;
    
    public String boldFileName;
    
    public String italicFileName;
    
    public String boldItalicFileName;
  }
  
  private static final class FontRegistrationInfo {
    String fontFilePath;
    
    String[] nativeNames;
    
    int fontFormat;
    
    boolean javaRasterizer;
    
    int fontRank;
    
    FontRegistrationInfo(String param1String, String[] param1ArrayOfString, int param1Int1, boolean param1Boolean, int param1Int2) {
      this.fontFilePath = param1String;
      this.nativeNames = param1ArrayOfString;
      this.fontFormat = param1Int1;
      this.javaRasterizer = param1Boolean;
      this.fontRank = param1Int2;
    }
  }
  
  private static class T1Filter implements FilenameFilter {
    private T1Filter() {}
    
    public boolean accept(File param1File, String param1String) {
      if (SunFontManager.noType1Font)
        return false; 
      int i = param1String.length() - 4;
      return (i <= 0) ? false : ((param1String.startsWith(".pfa", i) || param1String.startsWith(".pfb", i) || param1String.startsWith(".PFA", i) || param1String.startsWith(".PFB", i)));
    }
  }
  
  private static class TTFilter implements FilenameFilter {
    private TTFilter() {}
    
    public boolean accept(File param1File, String param1String) {
      int i = param1String.length() - 4;
      return (i <= 0) ? false : ((param1String.startsWith(".ttf", i) || param1String.startsWith(".TTF", i) || param1String.startsWith(".ttc", i) || param1String.startsWith(".TTC", i) || param1String.startsWith(".otf", i) || param1String.startsWith(".OTF", i)));
    }
  }
  
  private static class TTorT1Filter implements FilenameFilter {
    private TTorT1Filter() {}
    
    public boolean accept(File param1File, String param1String) {
      int i = param1String.length() - 4;
      if (i <= 0)
        return false; 
      boolean bool = (param1String.startsWith(".ttf", i) || param1String.startsWith(".TTF", i) || param1String.startsWith(".ttc", i) || param1String.startsWith(".TTC", i) || param1String.startsWith(".otf", i) || param1String.startsWith(".OTF", i)) ? 1 : 0;
      return bool ? true : (SunFontManager.noType1Font ? false : ((param1String.startsWith(".pfa", i) || param1String.startsWith(".pfb", i) || param1String.startsWith(".PFA", i) || param1String.startsWith(".PFB", i))));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\SunFontManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */