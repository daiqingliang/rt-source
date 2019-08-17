package javax.swing.text.rtf;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabStop;

class RTFReader extends RTFParser {
  StyledDocument target;
  
  Dictionary<Object, Object> parserState;
  
  Destination rtfDestination;
  
  MutableAttributeSet documentAttributes;
  
  Dictionary<Integer, String> fontTable;
  
  Color[] colorTable;
  
  Style[] characterStyles;
  
  Style[] paragraphStyles;
  
  Style[] sectionStyles;
  
  int rtfversion;
  
  boolean ignoreGroupIfUnknownKeyword;
  
  int skippingCharacters;
  
  private static Dictionary<String, RTFAttribute> straightforwardAttributes = RTFAttributes.attributesByKeyword();
  
  private MockAttributeSet mockery;
  
  static Dictionary<String, String> textKeywords = null;
  
  static final String TabAlignmentKey = "tab_alignment";
  
  static final String TabLeaderKey = "tab_leader";
  
  static Dictionary<String, char[]> characterSets;
  
  static boolean useNeXTForAnsi;
  
  public RTFReader(StyledDocument paramStyledDocument) {
    this.target = paramStyledDocument;
    this.parserState = new Hashtable();
    this.fontTable = new Hashtable();
    this.rtfversion = -1;
    this.mockery = new MockAttributeSet();
    this.documentAttributes = new SimpleAttributeSet();
  }
  
  public void handleBinaryBlob(byte[] paramArrayOfByte) {
    if (this.skippingCharacters > 0) {
      this.skippingCharacters--;
      return;
    } 
  }
  
  public void handleText(String paramString) {
    if (this.skippingCharacters > 0) {
      if (this.skippingCharacters >= paramString.length()) {
        this.skippingCharacters -= paramString.length();
        return;
      } 
      paramString = paramString.substring(this.skippingCharacters);
      this.skippingCharacters = 0;
    } 
    if (this.rtfDestination != null) {
      this.rtfDestination.handleText(paramString);
      return;
    } 
    warning("Text with no destination. oops.");
  }
  
  Color defaultColor() { return Color.black; }
  
  public void begingroup() {
    if (this.skippingCharacters > 0)
      this.skippingCharacters = 0; 
    Object object = this.parserState.get("_savedState");
    if (object != null)
      this.parserState.remove("_savedState"); 
    Dictionary dictionary = (Dictionary)((Hashtable)this.parserState).clone();
    if (object != null)
      dictionary.put("_savedState", object); 
    this.parserState.put("_savedState", dictionary);
    if (this.rtfDestination != null)
      this.rtfDestination.begingroup(); 
  }
  
  public void endgroup() {
    if (this.skippingCharacters > 0)
      this.skippingCharacters = 0; 
    Dictionary dictionary1 = (Dictionary)this.parserState.get("_savedState");
    Destination destination = (Destination)dictionary1.get("dst");
    if (destination != this.rtfDestination) {
      this.rtfDestination.close();
      this.rtfDestination = destination;
    } 
    Dictionary dictionary2 = this.parserState;
    this.parserState = dictionary1;
    if (this.rtfDestination != null)
      this.rtfDestination.endgroup(dictionary2); 
  }
  
  protected void setRTFDestination(Destination paramDestination) {
    Dictionary dictionary = (Dictionary)this.parserState.get("_savedState");
    if (dictionary != null && this.rtfDestination != dictionary.get("dst")) {
      warning("Warning, RTF destination overridden, invalid RTF.");
      this.rtfDestination.close();
    } 
    this.rtfDestination = paramDestination;
    this.parserState.put("dst", this.rtfDestination);
  }
  
  public void close() {
    Enumeration enumeration = this.documentAttributes.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      this.target.putProperty(object, this.documentAttributes.getAttribute(object));
    } 
    warning("RTF filter done.");
    super.close();
  }
  
  public boolean handleKeyword(String paramString) {
    boolean bool = this.ignoreGroupIfUnknownKeyword;
    if (this.skippingCharacters > 0) {
      this.skippingCharacters--;
      return true;
    } 
    this.ignoreGroupIfUnknownKeyword = false;
    String str;
    if ((str = (String)textKeywords.get(paramString)) != null) {
      handleText(str);
      return true;
    } 
    if (paramString.equals("fonttbl")) {
      setRTFDestination(new FonttblDestination());
      return true;
    } 
    if (paramString.equals("colortbl")) {
      setRTFDestination(new ColortblDestination());
      return true;
    } 
    if (paramString.equals("stylesheet")) {
      setRTFDestination(new StylesheetDestination());
      return true;
    } 
    if (paramString.equals("info")) {
      setRTFDestination(new InfoDestination());
      return false;
    } 
    if (paramString.equals("mac")) {
      setCharacterSet("mac");
      return true;
    } 
    if (paramString.equals("ansi")) {
      if (useNeXTForAnsi) {
        setCharacterSet("NeXT");
      } else {
        setCharacterSet("ansi");
      } 
      return true;
    } 
    if (paramString.equals("next")) {
      setCharacterSet("NeXT");
      return true;
    } 
    if (paramString.equals("pc")) {
      setCharacterSet("cpg437");
      return true;
    } 
    if (paramString.equals("pca")) {
      setCharacterSet("cpg850");
      return true;
    } 
    if (paramString.equals("*")) {
      this.ignoreGroupIfUnknownKeyword = true;
      return true;
    } 
    if (this.rtfDestination != null && this.rtfDestination.handleKeyword(paramString))
      return true; 
    if (paramString.equals("aftncn") || paramString.equals("aftnsep") || paramString.equals("aftnsepc") || paramString.equals("annotation") || paramString.equals("atnauthor") || paramString.equals("atnicn") || paramString.equals("atnid") || paramString.equals("atnref") || paramString.equals("atntime") || paramString.equals("atrfend") || paramString.equals("atrfstart") || paramString.equals("bkmkend") || paramString.equals("bkmkstart") || paramString.equals("datafield") || paramString.equals("do") || paramString.equals("dptxbxtext") || paramString.equals("falt") || paramString.equals("field") || paramString.equals("file") || paramString.equals("filetbl") || paramString.equals("fname") || paramString.equals("fontemb") || paramString.equals("fontfile") || paramString.equals("footer") || paramString.equals("footerf") || paramString.equals("footerl") || paramString.equals("footerr") || paramString.equals("footnote") || paramString.equals("ftncn") || paramString.equals("ftnsep") || paramString.equals("ftnsepc") || paramString.equals("header") || paramString.equals("headerf") || paramString.equals("headerl") || paramString.equals("headerr") || paramString.equals("keycode") || paramString.equals("nextfile") || paramString.equals("object") || paramString.equals("pict") || paramString.equals("pn") || paramString.equals("pnseclvl") || paramString.equals("pntxtb") || paramString.equals("pntxta") || paramString.equals("revtbl") || paramString.equals("rxe") || paramString.equals("tc") || paramString.equals("template") || paramString.equals("txe") || paramString.equals("xe"))
      bool = true; 
    if (bool)
      setRTFDestination(new DiscardingDestination()); 
    return false;
  }
  
  public boolean handleKeyword(String paramString, int paramInt) {
    boolean bool = this.ignoreGroupIfUnknownKeyword;
    if (this.skippingCharacters > 0) {
      this.skippingCharacters--;
      return true;
    } 
    this.ignoreGroupIfUnknownKeyword = false;
    if (paramString.equals("uc")) {
      this.parserState.put("UnicodeSkip", Integer.valueOf(paramInt));
      return true;
    } 
    if (paramString.equals("u")) {
      if (paramInt < 0)
        paramInt += 65536; 
      handleText((char)paramInt);
      Number number = (Number)this.parserState.get("UnicodeSkip");
      if (number != null) {
        this.skippingCharacters = number.intValue();
      } else {
        this.skippingCharacters = 1;
      } 
      return true;
    } 
    if (paramString.equals("rtf")) {
      this.rtfversion = paramInt;
      setRTFDestination(new DocumentDestination());
      return true;
    } 
    if (paramString.startsWith("NeXT") || paramString.equals("private"))
      bool = true; 
    if (this.rtfDestination != null && this.rtfDestination.handleKeyword(paramString, paramInt))
      return true; 
    if (bool)
      setRTFDestination(new DiscardingDestination()); 
    return false;
  }
  
  private void setTargetAttribute(String paramString, Object paramObject) {}
  
  public void setCharacterSet(String paramString) {
    Object object;
    try {
      object = getCharacterSet(paramString);
    } catch (Exception exception) {
      warning("Exception loading RTF character set \"" + paramString + "\": " + exception);
      object = null;
    } 
    if (object != null) {
      this.translationTable = (char[])object;
    } else {
      warning("Unknown RTF character set \"" + paramString + "\"");
      if (!paramString.equals("ansi"))
        try {
          this.translationTable = (char[])getCharacterSet("ansi");
        } catch (IOException iOException) {
          throw new InternalError("RTFReader: Unable to find character set resources (" + iOException + ")", iOException);
        }  
    } 
    setTargetAttribute("rtfCharacterSet", paramString);
  }
  
  public static void defineCharacterSet(String paramString, char[] paramArrayOfChar) {
    if (paramArrayOfChar.length < 256)
      throw new IllegalArgumentException("Translation table must have 256 entries."); 
    characterSets.put(paramString, paramArrayOfChar);
  }
  
  public static Object getCharacterSet(final String name) throws IOException {
    char[] arrayOfChar = (char[])characterSets.get(paramString);
    if (arrayOfChar == null) {
      InputStream inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() { return RTFReader.class.getResourceAsStream("charsets/" + name + ".txt"); }
          });
      arrayOfChar = readCharset(inputStream);
      defineCharacterSet(paramString, arrayOfChar);
    } 
    return arrayOfChar;
  }
  
  static char[] readCharset(InputStream paramInputStream) throws IOException {
    char[] arrayOfChar = new char[256];
    StreamTokenizer streamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(paramInputStream, "ISO-8859-1")));
    streamTokenizer.eolIsSignificant(false);
    streamTokenizer.commentChar(35);
    streamTokenizer.slashSlashComments(true);
    streamTokenizer.slashStarComments(true);
    for (byte b = 0; b < 'Ā'; b++) {
      int i;
      try {
        i = streamTokenizer.nextToken();
      } catch (Exception exception) {
        throw new IOException("Unable to read from character set file (" + exception + ")");
      } 
      streamTokenizer;
      if (i != -2)
        throw new IOException("Unexpected token in character set file"); 
      arrayOfChar[b] = (char)(int)streamTokenizer.nval;
    } 
    return arrayOfChar;
  }
  
  static char[] readCharset(URL paramURL) throws IOException { return readCharset(paramURL.openStream()); }
  
  static  {
    textKeywords = new Hashtable();
    textKeywords.put("\\", "\\");
    textKeywords.put("{", "{");
    textKeywords.put("}", "}");
    textKeywords.put(" ", " ");
    textKeywords.put("~", " ");
    textKeywords.put("_", "‑");
    textKeywords.put("bullet", "•");
    textKeywords.put("emdash", "—");
    textKeywords.put("emspace", " ");
    textKeywords.put("endash", "–");
    textKeywords.put("enspace", " ");
    textKeywords.put("ldblquote", "“");
    textKeywords.put("lquote", "‘");
    textKeywords.put("ltrmark", "‎");
    textKeywords.put("rdblquote", "”");
    textKeywords.put("rquote", "’");
    textKeywords.put("rtlmark", "‏");
    textKeywords.put("tab", "\t");
    textKeywords.put("zwj", "‍");
    textKeywords.put("zwnj", "‌");
    textKeywords.put("-", "‧");
    useNeXTForAnsi = false;
    characterSets = new Hashtable();
  }
  
  abstract class AttributeTrackingDestination implements Destination {
    MutableAttributeSet characterAttributes = rootCharacterAttributes();
    
    MutableAttributeSet paragraphAttributes;
    
    MutableAttributeSet sectionAttributes;
    
    public AttributeTrackingDestination() {
      RTFReader.this.parserState.put("chr", this.characterAttributes);
      this.paragraphAttributes = rootParagraphAttributes();
      RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
      this.sectionAttributes = rootSectionAttributes();
      RTFReader.this.parserState.put("sec", this.sectionAttributes);
    }
    
    public abstract void handleText(String param1String);
    
    public void handleBinaryBlob(byte[] param1ArrayOfByte) { RTFReader.this.warning("Unexpected binary data in RTF file."); }
    
    public void begingroup() {
      MutableAttributeSet mutableAttributeSet1 = currentTextAttributes();
      MutableAttributeSet mutableAttributeSet2 = currentParagraphAttributes();
      AttributeSet attributeSet = currentSectionAttributes();
      this.characterAttributes = new SimpleAttributeSet();
      this.characterAttributes.addAttributes(mutableAttributeSet1);
      RTFReader.this.parserState.put("chr", this.characterAttributes);
      this.paragraphAttributes = new SimpleAttributeSet();
      this.paragraphAttributes.addAttributes(mutableAttributeSet2);
      RTFReader.this.parserState.put("pgf", this.paragraphAttributes);
      this.sectionAttributes = new SimpleAttributeSet();
      this.sectionAttributes.addAttributes(attributeSet);
      RTFReader.this.parserState.put("sec", this.sectionAttributes);
    }
    
    public void endgroup(Dictionary param1Dictionary) {
      this.characterAttributes = (MutableAttributeSet)RTFReader.this.parserState.get("chr");
      this.paragraphAttributes = (MutableAttributeSet)RTFReader.this.parserState.get("pgf");
      this.sectionAttributes = (MutableAttributeSet)RTFReader.this.parserState.get("sec");
    }
    
    public void close() {}
    
    public boolean handleKeyword(String param1String) {
      if (param1String.equals("ulnone"))
        return handleKeyword("ul", 0); 
      RTFAttribute rTFAttribute = (RTFAttribute)straightforwardAttributes.get(param1String);
      if (rTFAttribute != null) {
        boolean bool;
        switch (rTFAttribute.domain()) {
          case 0:
            bool = rTFAttribute.set(this.characterAttributes);
            break;
          case 1:
            bool = rTFAttribute.set(this.paragraphAttributes);
            break;
          case 2:
            bool = rTFAttribute.set(this.sectionAttributes);
            break;
          case 4:
            this.this$0.mockery.backing = RTFReader.this.parserState;
            bool = rTFAttribute.set(RTFReader.this.mockery);
            this.this$0.mockery.backing = null;
            break;
          case 3:
            bool = rTFAttribute.set(RTFReader.this.documentAttributes);
            break;
          default:
            bool = false;
            break;
        } 
        if (bool)
          return true; 
      } 
      if (param1String.equals("plain")) {
        resetCharacterAttributes();
        return true;
      } 
      if (param1String.equals("pard")) {
        resetParagraphAttributes();
        return true;
      } 
      if (param1String.equals("sectd")) {
        resetSectionAttributes();
        return true;
      } 
      return false;
    }
    
    public boolean handleKeyword(String param1String, int param1Int) {
      boolean bool = (param1Int != 0) ? 1 : 0;
      if (param1String.equals("fc"))
        param1String = "cf"; 
      if (param1String.equals("f")) {
        RTFReader.this.parserState.put(param1String, Integer.valueOf(param1Int));
        return true;
      } 
      if (param1String.equals("cf")) {
        RTFReader.this.parserState.put(param1String, Integer.valueOf(param1Int));
        return true;
      } 
      RTFAttribute rTFAttribute = (RTFAttribute)straightforwardAttributes.get(param1String);
      if (rTFAttribute != null) {
        boolean bool1;
        switch (rTFAttribute.domain()) {
          case 0:
            bool1 = rTFAttribute.set(this.characterAttributes, param1Int);
            break;
          case 1:
            bool1 = rTFAttribute.set(this.paragraphAttributes, param1Int);
            break;
          case 2:
            bool1 = rTFAttribute.set(this.sectionAttributes, param1Int);
            break;
          case 4:
            this.this$0.mockery.backing = RTFReader.this.parserState;
            bool1 = rTFAttribute.set(RTFReader.this.mockery, param1Int);
            this.this$0.mockery.backing = null;
            break;
          case 3:
            bool1 = rTFAttribute.set(RTFReader.this.documentAttributes, param1Int);
            break;
          default:
            bool1 = false;
            break;
        } 
        if (bool1)
          return true; 
      } 
      if (param1String.equals("fs")) {
        StyleConstants.setFontSize(this.characterAttributes, param1Int / 2);
        return true;
      } 
      if (param1String.equals("sl")) {
        if (param1Int == 1000) {
          this.characterAttributes.removeAttribute(StyleConstants.LineSpacing);
        } else {
          StyleConstants.setLineSpacing(this.characterAttributes, param1Int / 20.0F);
        } 
        return true;
      } 
      if (param1String.equals("tx") || param1String.equals("tb")) {
        Integer integer;
        float f = param1Int / 20.0F;
        int i = 0;
        Number number = (Number)RTFReader.this.parserState.get("tab_alignment");
        if (number != null)
          i = number.intValue(); 
        int j = 0;
        number = (Number)RTFReader.this.parserState.get("tab_leader");
        if (number != null)
          j = number.intValue(); 
        if (param1String.equals("tb"))
          i = 5; 
        RTFReader.this.parserState.remove("tab_alignment");
        RTFReader.this.parserState.remove("tab_leader");
        TabStop tabStop = new TabStop(f, i, j);
        Dictionary dictionary = (Dictionary)RTFReader.this.parserState.get("_tabs");
        if (dictionary == null) {
          dictionary = new Hashtable();
          RTFReader.this.parserState.put("_tabs", dictionary);
          integer = Integer.valueOf(1);
        } else {
          integer = (integer = (Integer)dictionary.get("stop count")).valueOf(1 + integer.intValue());
        } 
        dictionary.put(integer, tabStop);
        dictionary.put("stop count", integer);
        RTFReader.this.parserState.remove("_tabs_immutable");
        return true;
      } 
      if (param1String.equals("s") && RTFReader.this.paragraphStyles != null) {
        RTFReader.this.parserState.put("paragraphStyle", RTFReader.this.paragraphStyles[param1Int]);
        return true;
      } 
      if (param1String.equals("cs") && RTFReader.this.characterStyles != null) {
        RTFReader.this.parserState.put("characterStyle", RTFReader.this.characterStyles[param1Int]);
        return true;
      } 
      if (param1String.equals("ds") && RTFReader.this.sectionStyles != null) {
        RTFReader.this.parserState.put("sectionStyle", RTFReader.this.sectionStyles[param1Int]);
        return true;
      } 
      return false;
    }
    
    protected MutableAttributeSet rootCharacterAttributes() {
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
      StyleConstants.setItalic(simpleAttributeSet, false);
      StyleConstants.setBold(simpleAttributeSet, false);
      StyleConstants.setUnderline(simpleAttributeSet, false);
      StyleConstants.setForeground(simpleAttributeSet, RTFReader.this.defaultColor());
      return simpleAttributeSet;
    }
    
    protected MutableAttributeSet rootParagraphAttributes() {
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
      StyleConstants.setLeftIndent(simpleAttributeSet, 0.0F);
      StyleConstants.setRightIndent(simpleAttributeSet, 0.0F);
      StyleConstants.setFirstLineIndent(simpleAttributeSet, 0.0F);
      simpleAttributeSet.setResolveParent(RTFReader.this.target.getStyle("default"));
      return simpleAttributeSet;
    }
    
    protected MutableAttributeSet rootSectionAttributes() { return new SimpleAttributeSet(); }
    
    MutableAttributeSet currentTextAttributes() {
      String str;
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet(this.characterAttributes);
      Integer integer = (Integer)RTFReader.this.parserState.get("f");
      if (integer != null) {
        str = (String)RTFReader.this.fontTable.get(integer);
      } else {
        str = null;
      } 
      if (str != null) {
        StyleConstants.setFontFamily(simpleAttributeSet, str);
      } else {
        simpleAttributeSet.removeAttribute(StyleConstants.FontFamily);
      } 
      if (RTFReader.this.colorTable != null) {
        Integer integer1 = (Integer)RTFReader.this.parserState.get("cf");
        if (integer1 != null) {
          Color color = RTFReader.this.colorTable[integer1.intValue()];
          StyleConstants.setForeground(simpleAttributeSet, color);
        } else {
          simpleAttributeSet.removeAttribute(StyleConstants.Foreground);
        } 
      } 
      if (RTFReader.this.colorTable != null) {
        Integer integer1 = (Integer)RTFReader.this.parserState.get("cb");
        if (integer1 != null) {
          Color color = RTFReader.this.colorTable[integer1.intValue()];
          simpleAttributeSet.addAttribute(StyleConstants.Background, color);
        } else {
          simpleAttributeSet.removeAttribute(StyleConstants.Background);
        } 
      } 
      Style style = (Style)RTFReader.this.parserState.get("characterStyle");
      if (style != null)
        simpleAttributeSet.setResolveParent(style); 
      return simpleAttributeSet;
    }
    
    MutableAttributeSet currentParagraphAttributes() {
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet(this.paragraphAttributes);
      TabStop[] arrayOfTabStop = (TabStop[])RTFReader.this.parserState.get("_tabs_immutable");
      if (arrayOfTabStop == null) {
        Dictionary dictionary = (Dictionary)RTFReader.this.parserState.get("_tabs");
        if (dictionary != null) {
          int i = ((Integer)dictionary.get("stop count")).intValue();
          arrayOfTabStop = new TabStop[i];
          for (byte b = 1; b <= i; b++)
            arrayOfTabStop[b - true] = (TabStop)dictionary.get(Integer.valueOf(b)); 
          RTFReader.this.parserState.put("_tabs_immutable", arrayOfTabStop);
        } 
      } 
      if (arrayOfTabStop != null)
        simpleAttributeSet.addAttribute("tabs", arrayOfTabStop); 
      Style style = (Style)RTFReader.this.parserState.get("paragraphStyle");
      if (style != null)
        simpleAttributeSet.setResolveParent(style); 
      return simpleAttributeSet;
    }
    
    public AttributeSet currentSectionAttributes() {
      SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet(this.sectionAttributes);
      Style style = (Style)RTFReader.this.parserState.get("sectionStyle");
      if (style != null)
        simpleAttributeSet.setResolveParent(style); 
      return simpleAttributeSet;
    }
    
    protected void resetCharacterAttributes() {
      handleKeyword("f", 0);
      handleKeyword("cf", 0);
      handleKeyword("fs", 24);
      Enumeration enumeration = straightforwardAttributes.elements();
      while (enumeration.hasMoreElements()) {
        RTFAttribute rTFAttribute = (RTFAttribute)enumeration.nextElement();
        if (rTFAttribute.domain() == 0)
          rTFAttribute.setDefault(this.characterAttributes); 
      } 
      handleKeyword("sl", 1000);
      RTFReader.this.parserState.remove("characterStyle");
    }
    
    protected void resetParagraphAttributes() {
      RTFReader.this.parserState.remove("_tabs");
      RTFReader.this.parserState.remove("_tabs_immutable");
      RTFReader.this.parserState.remove("paragraphStyle");
      StyleConstants.setAlignment(this.paragraphAttributes, 0);
      Enumeration enumeration = straightforwardAttributes.elements();
      while (enumeration.hasMoreElements()) {
        RTFAttribute rTFAttribute = (RTFAttribute)enumeration.nextElement();
        if (rTFAttribute.domain() == 1)
          rTFAttribute.setDefault(this.characterAttributes); 
      } 
    }
    
    protected void resetSectionAttributes() {
      Enumeration enumeration = straightforwardAttributes.elements();
      while (enumeration.hasMoreElements()) {
        RTFAttribute rTFAttribute = (RTFAttribute)enumeration.nextElement();
        if (rTFAttribute.domain() == 2)
          rTFAttribute.setDefault(this.characterAttributes); 
      } 
      RTFReader.this.parserState.remove("sectionStyle");
    }
  }
  
  class ColortblDestination implements Destination {
    int red = 0;
    
    int green = 0;
    
    int blue = 0;
    
    Vector<Color> proTemTable = new Vector();
    
    public void handleText(String param1String) {
      for (byte b = 0; b < param1String.length(); b++) {
        if (param1String.charAt(b) == ';') {
          Color color = new Color(this.red, this.green, this.blue);
          this.proTemTable.addElement(color);
        } 
      } 
    }
    
    public void close() {
      int i = this.proTemTable.size();
      RTFReader.this.warning("Done reading color table, " + i + " entries.");
      RTFReader.this.colorTable = new Color[i];
      this.proTemTable.copyInto(RTFReader.this.colorTable);
    }
    
    public boolean handleKeyword(String param1String, int param1Int) {
      if (param1String.equals("red")) {
        this.red = param1Int;
      } else if (param1String.equals("green")) {
        this.green = param1Int;
      } else if (param1String.equals("blue")) {
        this.blue = param1Int;
      } else {
        return false;
      } 
      return true;
    }
    
    public boolean handleKeyword(String param1String) { return false; }
    
    public void begingroup() {}
    
    public void endgroup(Dictionary param1Dictionary) {}
    
    public void handleBinaryBlob(byte[] param1ArrayOfByte) {}
  }
  
  static interface Destination {
    void handleBinaryBlob(byte[] param1ArrayOfByte);
    
    void handleText(String param1String);
    
    boolean handleKeyword(String param1String);
    
    boolean handleKeyword(String param1String, int param1Int);
    
    void begingroup();
    
    void endgroup(Dictionary param1Dictionary);
    
    void close();
  }
  
  class DiscardingDestination implements Destination {
    public void handleBinaryBlob(byte[] param1ArrayOfByte) {}
    
    public void handleText(String param1String) {}
    
    public boolean handleKeyword(String param1String) { return true; }
    
    public boolean handleKeyword(String param1String, int param1Int) { return true; }
    
    public void begingroup() {}
    
    public void endgroup(Dictionary param1Dictionary) {}
    
    public void close() {}
  }
  
  class DocumentDestination extends TextHandlingDestination implements Destination {
    DocumentDestination() { super(RTFReader.this); }
    
    public void deliverText(String param1String, AttributeSet param1AttributeSet) {
      try {
        RTFReader.this.target.insertString(RTFReader.this.target.getLength(), param1String, currentTextAttributes());
      } catch (BadLocationException badLocationException) {
        throw new InternalError(badLocationException.getMessage(), badLocationException);
      } 
    }
    
    public void finishParagraph(AttributeSet param1AttributeSet1, AttributeSet param1AttributeSet2) {
      int i = RTFReader.this.target.getLength();
      try {
        RTFReader.this.target.insertString(i, "\n", param1AttributeSet2);
        RTFReader.this.target.setParagraphAttributes(i, 1, param1AttributeSet1, true);
      } catch (BadLocationException badLocationException) {
        throw new InternalError(badLocationException.getMessage(), badLocationException);
      } 
    }
    
    public void endSection() {}
  }
  
  class FonttblDestination implements Destination {
    int nextFontNumber;
    
    Integer fontNumberKey = null;
    
    String nextFontFamily;
    
    public void handleBinaryBlob(byte[] param1ArrayOfByte) {}
    
    public void handleText(String param1String) {
      String str;
      int i = param1String.indexOf(';');
      if (i > -1) {
        str = param1String.substring(0, i);
      } else {
        str = param1String;
      } 
      if (this.nextFontNumber == -1 && this.fontNumberKey != null) {
        str = (String)RTFReader.this.fontTable.get(this.fontNumberKey) + str;
      } else {
        this.fontNumberKey = Integer.valueOf(this.nextFontNumber);
      } 
      RTFReader.this.fontTable.put(this.fontNumberKey, str);
      this.nextFontNumber = -1;
      this.nextFontFamily = null;
    }
    
    public boolean handleKeyword(String param1String) {
      if (param1String.charAt(0) == 'f') {
        this.nextFontFamily = param1String.substring(1);
        return true;
      } 
      return false;
    }
    
    public boolean handleKeyword(String param1String, int param1Int) {
      if (param1String.equals("f")) {
        this.nextFontNumber = param1Int;
        return true;
      } 
      return false;
    }
    
    public void begingroup() {}
    
    public void endgroup(Dictionary param1Dictionary) {}
    
    public void close() {
      Enumeration enumeration = RTFReader.this.fontTable.keys();
      RTFReader.this.warning("Done reading font table.");
      while (enumeration.hasMoreElements()) {
        Integer integer = (Integer)enumeration.nextElement();
        RTFReader.this.warning("Number " + integer + ": " + (String)RTFReader.this.fontTable.get(integer));
      } 
    }
  }
  
  class InfoDestination extends DiscardingDestination implements Destination {
    InfoDestination() { super(RTFReader.this); }
  }
  
  class StylesheetDestination extends DiscardingDestination implements Destination {
    Dictionary<Integer, StyleDefiningDestination> definedStyles = new Hashtable();
    
    public StylesheetDestination() { super(RTFReader.this); }
    
    public void begingroup() { RTFReader.this.setRTFDestination(new StyleDefiningDestination()); }
    
    public void close() {
      Vector vector1 = new Vector();
      Vector vector2 = new Vector();
      Vector vector3 = new Vector();
      Enumeration enumeration = this.definedStyles.elements();
      while (enumeration.hasMoreElements()) {
        Vector vector;
        StyleDefiningDestination styleDefiningDestination = (StyleDefiningDestination)enumeration.nextElement();
        Style style = styleDefiningDestination.realize();
        RTFReader.this.warning("Style " + styleDefiningDestination.number + " (" + styleDefiningDestination.styleName + "): " + style);
        String str = (String)style.getAttribute("style:type");
        if (str.equals("section")) {
          vector = vector3;
        } else if (str.equals("character")) {
          vector = vector1;
        } else {
          vector = vector2;
        } 
        if (vector.size() <= styleDefiningDestination.number)
          vector.setSize(styleDefiningDestination.number + 1); 
        vector.setElementAt(style, styleDefiningDestination.number);
      } 
      if (!vector1.isEmpty()) {
        Style[] arrayOfStyle = new Style[vector1.size()];
        vector1.copyInto(arrayOfStyle);
        RTFReader.this.characterStyles = arrayOfStyle;
      } 
      if (!vector2.isEmpty()) {
        Style[] arrayOfStyle = new Style[vector2.size()];
        vector2.copyInto(arrayOfStyle);
        RTFReader.this.paragraphStyles = arrayOfStyle;
      } 
      if (!vector3.isEmpty()) {
        Style[] arrayOfStyle = new Style[vector3.size()];
        vector3.copyInto(arrayOfStyle);
        RTFReader.this.sectionStyles = arrayOfStyle;
      } 
    }
    
    class StyleDefiningDestination extends RTFReader.AttributeTrackingDestination implements RTFReader.Destination {
      final int STYLENUMBER_NONE = 222;
      
      boolean additive = false;
      
      boolean characterStyle = false;
      
      boolean sectionStyle = false;
      
      public String styleName = null;
      
      public int number = 0;
      
      int basedOn = 222;
      
      int nextStyle = 222;
      
      boolean hidden = false;
      
      Style realizedStyle;
      
      public StyleDefiningDestination() { super(RTFReader.StylesheetDestination.this.this$0); }
      
      public void handleText(String param2String) {
        if (this.styleName != null) {
          this.styleName += param2String;
        } else {
          this.styleName = param2String;
        } 
      }
      
      public void close() {
        byte b = (this.styleName == null) ? 0 : this.styleName.indexOf(';');
        if (b)
          this.styleName = this.styleName.substring(0, b); 
        RTFReader.StylesheetDestination.this.definedStyles.put(Integer.valueOf(this.number), this);
        super.close();
      }
      
      public boolean handleKeyword(String param2String) {
        if (param2String.equals("additive")) {
          this.additive = true;
          return true;
        } 
        if (param2String.equals("shidden")) {
          this.hidden = true;
          return true;
        } 
        return super.handleKeyword(param2String);
      }
      
      public boolean handleKeyword(String param2String, int param2Int) {
        if (param2String.equals("s")) {
          this.characterStyle = false;
          this.sectionStyle = false;
          this.number = param2Int;
        } else if (param2String.equals("cs")) {
          this.characterStyle = true;
          this.sectionStyle = false;
          this.number = param2Int;
        } else if (param2String.equals("ds")) {
          this.characterStyle = false;
          this.sectionStyle = true;
          this.number = param2Int;
        } else if (param2String.equals("sbasedon")) {
          this.basedOn = param2Int;
        } else if (param2String.equals("snext")) {
          this.nextStyle = param2Int;
        } else {
          return super.handleKeyword(param2String, param2Int);
        } 
        return true;
      }
      
      public Style realize() {
        Style style1 = null;
        Style style2 = null;
        if (this.realizedStyle != null)
          return this.realizedStyle; 
        if (this.basedOn != 222) {
          StyleDefiningDestination styleDefiningDestination = (StyleDefiningDestination)RTFReader.StylesheetDestination.this.definedStyles.get(Integer.valueOf(this.basedOn));
          if (styleDefiningDestination != null && styleDefiningDestination != this)
            style1 = styleDefiningDestination.realize(); 
        } 
        this.realizedStyle = this.this$1.this$0.target.addStyle(this.styleName, style1);
        if (this.characterStyle) {
          this.realizedStyle.addAttributes(currentTextAttributes());
          this.realizedStyle.addAttribute("style:type", "character");
        } else if (this.sectionStyle) {
          this.realizedStyle.addAttributes(currentSectionAttributes());
          this.realizedStyle.addAttribute("style:type", "section");
        } else {
          this.realizedStyle.addAttributes(currentParagraphAttributes());
          this.realizedStyle.addAttribute("style:type", "paragraph");
        } 
        if (this.nextStyle != 222) {
          StyleDefiningDestination styleDefiningDestination = (StyleDefiningDestination)RTFReader.StylesheetDestination.this.definedStyles.get(Integer.valueOf(this.nextStyle));
          if (styleDefiningDestination != null)
            style2 = styleDefiningDestination.realize(); 
        } 
        if (style2 != null)
          this.realizedStyle.addAttribute("style:nextStyle", style2); 
        this.realizedStyle.addAttribute("style:additive", Boolean.valueOf(this.additive));
        this.realizedStyle.addAttribute("style:hidden", Boolean.valueOf(this.hidden));
        return this.realizedStyle;
      }
    }
  }
  
  abstract class TextHandlingDestination extends AttributeTrackingDestination implements Destination {
    boolean inParagraph = false;
    
    public TextHandlingDestination() { super(RTFReader.this); }
    
    public void handleText(String param1String) {
      if (!this.inParagraph)
        beginParagraph(); 
      deliverText(param1String, currentTextAttributes());
    }
    
    abstract void deliverText(String param1String, AttributeSet param1AttributeSet);
    
    public void close() {
      if (this.inParagraph)
        endParagraph(); 
      super.close();
    }
    
    public boolean handleKeyword(String param1String) {
      if (param1String.equals("\r") || param1String.equals("\n"))
        param1String = "par"; 
      if (param1String.equals("par")) {
        endParagraph();
        return true;
      } 
      if (param1String.equals("sect")) {
        endSection();
        return true;
      } 
      return super.handleKeyword(param1String);
    }
    
    protected void beginParagraph() { this.inParagraph = true; }
    
    protected void endParagraph() {
      MutableAttributeSet mutableAttributeSet1 = currentParagraphAttributes();
      MutableAttributeSet mutableAttributeSet2 = currentTextAttributes();
      finishParagraph(mutableAttributeSet1, mutableAttributeSet2);
      this.inParagraph = false;
    }
    
    abstract void finishParagraph(AttributeSet param1AttributeSet1, AttributeSet param1AttributeSet2);
    
    abstract void endSection();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\RTFReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */