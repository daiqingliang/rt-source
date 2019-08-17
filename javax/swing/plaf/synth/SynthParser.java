package javax.swing.plaf.synth;

import com.sun.beans.decoder.DocumentHandler;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;
import sun.swing.plaf.synth.DefaultSynthStyle.StateInfo;

class SynthParser extends DefaultHandler {
  private static final String ELEMENT_SYNTH = "synth";
  
  private static final String ELEMENT_STYLE = "style";
  
  private static final String ELEMENT_STATE = "state";
  
  private static final String ELEMENT_FONT = "font";
  
  private static final String ELEMENT_COLOR = "color";
  
  private static final String ELEMENT_IMAGE_PAINTER = "imagePainter";
  
  private static final String ELEMENT_PAINTER = "painter";
  
  private static final String ELEMENT_PROPERTY = "property";
  
  private static final String ELEMENT_SYNTH_GRAPHICS = "graphicsUtils";
  
  private static final String ELEMENT_IMAGE_ICON = "imageIcon";
  
  private static final String ELEMENT_BIND = "bind";
  
  private static final String ELEMENT_BIND_KEY = "bindKey";
  
  private static final String ELEMENT_INSETS = "insets";
  
  private static final String ELEMENT_OPAQUE = "opaque";
  
  private static final String ELEMENT_DEFAULTS_PROPERTY = "defaultsProperty";
  
  private static final String ELEMENT_INPUT_MAP = "inputMap";
  
  private static final String ATTRIBUTE_ACTION = "action";
  
  private static final String ATTRIBUTE_ID = "id";
  
  private static final String ATTRIBUTE_IDREF = "idref";
  
  private static final String ATTRIBUTE_CLONE = "clone";
  
  private static final String ATTRIBUTE_VALUE = "value";
  
  private static final String ATTRIBUTE_NAME = "name";
  
  private static final String ATTRIBUTE_STYLE = "style";
  
  private static final String ATTRIBUTE_SIZE = "size";
  
  private static final String ATTRIBUTE_TYPE = "type";
  
  private static final String ATTRIBUTE_TOP = "top";
  
  private static final String ATTRIBUTE_LEFT = "left";
  
  private static final String ATTRIBUTE_BOTTOM = "bottom";
  
  private static final String ATTRIBUTE_RIGHT = "right";
  
  private static final String ATTRIBUTE_KEY = "key";
  
  private static final String ATTRIBUTE_SOURCE_INSETS = "sourceInsets";
  
  private static final String ATTRIBUTE_DEST_INSETS = "destinationInsets";
  
  private static final String ATTRIBUTE_PATH = "path";
  
  private static final String ATTRIBUTE_STRETCH = "stretch";
  
  private static final String ATTRIBUTE_PAINT_CENTER = "paintCenter";
  
  private static final String ATTRIBUTE_METHOD = "method";
  
  private static final String ATTRIBUTE_DIRECTION = "direction";
  
  private static final String ATTRIBUTE_CENTER = "center";
  
  private DocumentHandler _handler;
  
  private int _depth;
  
  private DefaultSynthStyleFactory _factory;
  
  private List<ParsedSynthStyle.StateInfo> _stateInfos = new ArrayList();
  
  private ParsedSynthStyle _style;
  
  private ParsedSynthStyle.StateInfo _stateInfo;
  
  private List<String> _inputMapBindings = new ArrayList();
  
  private String _inputMapID;
  
  private Map<String, Object> _mapping = new HashMap();
  
  private URL _urlResourceBase;
  
  private Class<?> _classResourceBase;
  
  private List<ColorType> _colorTypes = new ArrayList();
  
  private Map<String, Object> _defaultsMap;
  
  private List<ParsedSynthStyle.PainterInfo> _stylePainters = new ArrayList();
  
  private List<ParsedSynthStyle.PainterInfo> _statePainters = new ArrayList();
  
  public void parse(InputStream paramInputStream, DefaultSynthStyleFactory paramDefaultSynthStyleFactory, URL paramURL, Class<?> paramClass, Map<String, Object> paramMap) throws ParseException, IllegalArgumentException {
    if (paramInputStream == null || paramDefaultSynthStyleFactory == null || (paramURL == null && paramClass == null))
      throw new IllegalArgumentException("You must supply an InputStream, StyleFactory and Class or URL"); 
    assert paramURL == null || paramClass == null;
    this._factory = paramDefaultSynthStyleFactory;
    this._classResourceBase = paramClass;
    this._urlResourceBase = paramURL;
    this._defaultsMap = paramMap;
    try {
      try {
        SAXParser sAXParser = SAXParserFactory.newInstance().newSAXParser();
        sAXParser.parse(new BufferedInputStream(paramInputStream), this);
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new ParseException("Error parsing: " + parserConfigurationException, 0);
      } catch (SAXException sAXException) {
        throw new ParseException("Error parsing: " + sAXException + " " + sAXException.getException(), 0);
      } catch (IOException iOException) {
        throw new ParseException("Error parsing: " + iOException, 0);
      } 
    } finally {
      reset();
    } 
  }
  
  private URL getResource(String paramString) {
    if (this._classResourceBase != null)
      return this._classResourceBase.getResource(paramString); 
    try {
      return new URL(this._urlResourceBase, paramString);
    } catch (MalformedURLException malformedURLException) {
      return null;
    } 
  }
  
  private void reset() {
    this._handler = null;
    this._depth = 0;
    this._mapping.clear();
    this._stateInfos.clear();
    this._colorTypes.clear();
    this._statePainters.clear();
    this._stylePainters.clear();
  }
  
  private boolean isForwarding() { return (this._depth > 0); }
  
  private DocumentHandler getHandler() {
    if (this._handler == null) {
      this._handler = new DocumentHandler();
      if (this._urlResourceBase != null) {
        URL[] arrayOfURL = { getResource(".") };
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader uRLClassLoader = new URLClassLoader(arrayOfURL, classLoader);
        this._handler.setClassLoader(uRLClassLoader);
      } else {
        this._handler.setClassLoader(this._classResourceBase.getClassLoader());
      } 
      for (String str : this._mapping.keySet())
        this._handler.setVariable(str, this._mapping.get(str)); 
    } 
    return this._handler;
  }
  
  private Object checkCast(Object paramObject, Class paramClass) throws SAXException {
    if (!paramClass.isInstance(paramObject))
      throw new SAXException("Expected type " + paramClass + " got " + paramObject.getClass()); 
    return paramObject;
  }
  
  private Object lookup(String paramString, Class paramClass) throws SAXException {
    if (this._handler != null && this._handler.hasVariable(paramString))
      return checkCast(this._handler.getVariable(paramString), paramClass); 
    Object object = this._mapping.get(paramString);
    if (object == null)
      throw new SAXException("ID " + paramString + " has not been defined"); 
    return checkCast(object, paramClass);
  }
  
  private void register(String paramString, Object paramObject) throws SAXException {
    if (paramString != null) {
      if (this._mapping.get(paramString) != null || (this._handler != null && this._handler.hasVariable(paramString)))
        throw new SAXException("ID " + paramString + " is already defined"); 
      if (this._handler != null) {
        this._handler.setVariable(paramString, paramObject);
      } else {
        this._mapping.put(paramString, paramObject);
      } 
    } 
  }
  
  private int nextInt(StringTokenizer paramStringTokenizer, String paramString) throws SAXException {
    if (!paramStringTokenizer.hasMoreTokens())
      throw new SAXException(paramString); 
    try {
      return Integer.parseInt(paramStringTokenizer.nextToken());
    } catch (NumberFormatException numberFormatException) {
      throw new SAXException(paramString);
    } 
  }
  
  private Insets parseInsets(String paramString1, String paramString2) throws SAXException {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString1);
    return new Insets(nextInt(stringTokenizer, paramString2), nextInt(stringTokenizer, paramString2), nextInt(stringTokenizer, paramString2), nextInt(stringTokenizer, paramString2));
  }
  
  private void startStyle(Attributes paramAttributes) throws SAXException {
    String str = null;
    this._style = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str1 = paramAttributes.getQName(i);
      if (str1.equals("clone")) {
        this._style = (ParsedSynthStyle)((ParsedSynthStyle)lookup(paramAttributes.getValue(i), ParsedSynthStyle.class)).clone();
      } else if (str1.equals("id")) {
        str = paramAttributes.getValue(i);
      } 
    } 
    if (this._style == null)
      this._style = new ParsedSynthStyle(); 
    register(str, this._style);
  }
  
  private void endStyle() {
    int i = this._stylePainters.size();
    if (i > 0) {
      this._style.setPainters((PainterInfo[])this._stylePainters.toArray(new ParsedSynthStyle.PainterInfo[i]));
      this._stylePainters.clear();
    } 
    i = this._stateInfos.size();
    if (i > 0) {
      this._style.setStateInfo((StateInfo[])this._stateInfos.toArray(new ParsedSynthStyle.StateInfo[i]));
      this._stateInfos.clear();
    } 
    this._style = null;
  }
  
  private void startState(Attributes paramAttributes) throws SAXException {
    Object object = null;
    char c = Character.MIN_VALUE;
    String str = null;
    this._stateInfo = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str1 = paramAttributes.getQName(i);
      if (str1.equals("id")) {
        str = paramAttributes.getValue(i);
      } else if (str1.equals("idref")) {
        this._stateInfo = (ParsedSynthStyle.StateInfo)lookup(paramAttributes.getValue(i), ParsedSynthStyle.StateInfo.class);
      } else if (str1.equals("clone")) {
        this._stateInfo = (ParsedSynthStyle.StateInfo)((ParsedSynthStyle.StateInfo)lookup(paramAttributes.getValue(i), ParsedSynthStyle.StateInfo.class)).clone();
      } else if (str1.equals("value")) {
        StringTokenizer stringTokenizer = new StringTokenizer(paramAttributes.getValue(i));
        while (stringTokenizer.hasMoreTokens()) {
          String str2 = stringTokenizer.nextToken().toUpperCase().intern();
          if (str2 == "ENABLED") {
            c |= true;
            continue;
          } 
          if (str2 == "MOUSE_OVER") {
            c |= 0x2;
            continue;
          } 
          if (str2 == "PRESSED") {
            c |= 0x4;
            continue;
          } 
          if (str2 == "DISABLED") {
            c |= 0x8;
            continue;
          } 
          if (str2 == "FOCUSED") {
            c |= 0x100;
            continue;
          } 
          if (str2 == "SELECTED") {
            c |= 0x200;
            continue;
          } 
          if (str2 == "DEFAULT") {
            c |= 0x400;
            continue;
          } 
          if (str2 != "AND")
            throw new SAXException("Unknown state: " + c); 
        } 
      } 
    } 
    if (this._stateInfo == null)
      this._stateInfo = new ParsedSynthStyle.StateInfo(); 
    this._stateInfo.setComponentState(c);
    register(str, this._stateInfo);
    this._stateInfos.add(this._stateInfo);
  }
  
  private void endState() {
    int i = this._statePainters.size();
    if (i > 0) {
      this._stateInfo.setPainters((PainterInfo[])this._statePainters.toArray(new ParsedSynthStyle.PainterInfo[i]));
      this._statePainters.clear();
    } 
    this._stateInfo = null;
  }
  
  private void startFont(Attributes paramAttributes) throws SAXException {
    Font font = null;
    byte b = 0;
    int i = 0;
    String str1 = null;
    String str2 = null;
    for (int j = paramAttributes.getLength() - 1; j >= 0; j--) {
      String str = paramAttributes.getQName(j);
      if (str.equals("id")) {
        str1 = paramAttributes.getValue(j);
      } else if (str.equals("idref")) {
        font = (Font)lookup(paramAttributes.getValue(j), Font.class);
      } else if (str.equals("name")) {
        str2 = paramAttributes.getValue(j);
      } else if (str.equals("size")) {
        try {
          i = Integer.parseInt(paramAttributes.getValue(j));
        } catch (NumberFormatException numberFormatException) {
          throw new SAXException("Invalid font size: " + paramAttributes.getValue(j));
        } 
      } else if (str.equals("style")) {
        StringTokenizer stringTokenizer = new StringTokenizer(paramAttributes.getValue(j));
        while (stringTokenizer.hasMoreTokens()) {
          String str3 = stringTokenizer.nextToken().intern();
          if (str3 == "BOLD") {
            b = (b | false) ^ false | true;
            continue;
          } 
          if (str3 == "ITALIC")
            b |= 0x2; 
        } 
      } 
    } 
    if (font == null) {
      if (str2 == null)
        throw new SAXException("You must define a name for the font"); 
      if (i == 0)
        throw new SAXException("You must define a size for the font"); 
      font = new FontUIResource(str2, b, i);
    } else if (str2 != null || i != 0 || b != 0) {
      throw new SAXException("Name, size and style are not for use with idref");
    } 
    register(str1, font);
    if (this._stateInfo != null) {
      this._stateInfo.setFont(font);
    } else if (this._style != null) {
      this._style.setFont(font);
    } 
  }
  
  private void startColor(Attributes paramAttributes) throws SAXException {
    Color color = null;
    String str = null;
    this._colorTypes.clear();
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str1 = paramAttributes.getQName(i);
      if (str1.equals("id")) {
        str = paramAttributes.getValue(i);
      } else if (str1.equals("idref")) {
        color = (Color)lookup(paramAttributes.getValue(i), Color.class);
      } else if (!str1.equals("name")) {
        if (str1.equals("value")) {
          String str2 = paramAttributes.getValue(i);
          if (str2.startsWith("#")) {
            try {
              boolean bool;
              int j;
              int k = str2.length();
              if (k < 8) {
                j = Integer.decode(str2).intValue();
                bool = false;
              } else if (k == 8) {
                j = Integer.decode(str2).intValue();
                bool = true;
              } else if (k == 9) {
                int m = Integer.decode('#' + str2.substring(3, 9)).intValue();
                int n = Integer.decode(str2.substring(0, 3)).intValue();
                j = n << 24 | m;
                bool = true;
              } else {
                throw new SAXException("Invalid Color value: " + str2);
              } 
              color = new ColorUIResource(new Color(j, bool));
            } catch (NumberFormatException numberFormatException) {
              throw new SAXException("Invalid Color value: " + str2);
            } 
          } else {
            try {
              color = new ColorUIResource((Color)Color.class.getField(str2.toUpperCase()).get(Color.class));
            } catch (NoSuchFieldException noSuchFieldException) {
              throw new SAXException("Invalid color name: " + str2);
            } catch (IllegalAccessException illegalAccessException) {
              throw new SAXException("Invalid color name: " + str2);
            } 
          } 
        } else if (str1.equals("type")) {
          StringTokenizer stringTokenizer = new StringTokenizer(paramAttributes.getValue(i));
          while (stringTokenizer.hasMoreTokens()) {
            Class clazz;
            String str2 = stringTokenizer.nextToken();
            int j = str2.lastIndexOf('.');
            if (j == -1) {
              clazz = ColorType.class;
              j = 0;
            } else {
              try {
                clazz = ReflectUtil.forName(str2.substring(0, j));
              } catch (ClassNotFoundException classNotFoundException) {
                throw new SAXException("Unknown class: " + str2.substring(0, j));
              } 
              j++;
            } 
            try {
              this._colorTypes.add((ColorType)checkCast(clazz.getField(str2.substring(j)).get(clazz), ColorType.class));
            } catch (NoSuchFieldException noSuchFieldException) {
              throw new SAXException("Unable to find color type: " + str2);
            } catch (IllegalAccessException illegalAccessException) {
              throw new SAXException("Unable to find color type: " + str2);
            } 
          } 
        } 
      } 
    } 
    if (color == null)
      throw new SAXException("color: you must specificy a value"); 
    register(str, color);
    if (this._stateInfo != null && this._colorTypes.size() > 0) {
      Color[] arrayOfColor = this._stateInfo.getColors();
      int j = 0;
      int k;
      for (k = this._colorTypes.size() - 1; k >= 0; k--)
        j = Math.max(j, ((ColorType)this._colorTypes.get(k)).getID()); 
      if (arrayOfColor == null || arrayOfColor.length <= j) {
        Color[] arrayOfColor1 = new Color[j + 1];
        if (arrayOfColor != null)
          System.arraycopy(arrayOfColor, 0, arrayOfColor1, 0, arrayOfColor.length); 
        arrayOfColor = arrayOfColor1;
      } 
      for (k = this._colorTypes.size() - 1; k >= 0; k--)
        arrayOfColor[((ColorType)this._colorTypes.get(k)).getID()] = color; 
      this._stateInfo.setColors(arrayOfColor);
    } 
  }
  
  private void startProperty(Attributes paramAttributes, Object paramObject) throws SAXException {
    Object object = null;
    String str1 = null;
    byte b = 0;
    String str2 = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str = paramAttributes.getQName(i);
      if (str.equals("type")) {
        String str3 = paramAttributes.getValue(i).toUpperCase();
        if (str3.equals("IDREF")) {
          b = 0;
        } else if (str3.equals("BOOLEAN")) {
          b = 1;
        } else if (str3.equals("DIMENSION")) {
          b = 2;
        } else if (str3.equals("INSETS")) {
          b = 3;
        } else if (str3.equals("INTEGER")) {
          b = 4;
        } else if (str3.equals("STRING")) {
          b = 5;
        } else {
          throw new SAXException(paramObject + " unknown type, useidref, boolean, dimension, insets or integer");
        } 
      } else if (str.equals("value")) {
        str2 = paramAttributes.getValue(i);
      } else if (str.equals("key")) {
        str1 = paramAttributes.getValue(i);
      } 
    } 
    if (str2 != null) {
      StringTokenizer stringTokenizer;
      switch (b) {
        case 0:
          object = lookup(str2, Object.class);
          break;
        case 1:
          if (str2.toUpperCase().equals("TRUE")) {
            object = Boolean.TRUE;
            break;
          } 
          object = Boolean.FALSE;
          break;
        case 2:
          stringTokenizer = new StringTokenizer(str2);
          object = new DimensionUIResource(nextInt(stringTokenizer, "Invalid dimension"), nextInt(stringTokenizer, "Invalid dimension"));
          break;
        case 3:
          object = parseInsets(str2, paramObject + " invalid insets");
          break;
        case 4:
          try {
            object = new Integer(Integer.parseInt(str2));
          } catch (NumberFormatException numberFormatException) {
            throw new SAXException(paramObject + " invalid value");
          } 
          break;
        case 5:
          object = str2;
          break;
      } 
    } 
    if (object == null || str1 == null)
      throw new SAXException(paramObject + ": you must supply a key and value"); 
    if (paramObject == "defaultsProperty") {
      this._defaultsMap.put(str1, object);
    } else if (this._stateInfo != null) {
      if (this._stateInfo.getData() == null)
        this._stateInfo.setData(new HashMap()); 
      this._stateInfo.getData().put(str1, object);
    } else if (this._style != null) {
      if (this._style.getData() == null)
        this._style.setData(new HashMap()); 
      this._style.getData().put(str1, object);
    } 
  }
  
  private void startGraphics(Attributes paramAttributes) throws SAXException {
    SynthGraphicsUtils synthGraphicsUtils = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str = paramAttributes.getQName(i);
      if (str.equals("idref"))
        synthGraphicsUtils = (SynthGraphicsUtils)lookup(paramAttributes.getValue(i), SynthGraphicsUtils.class); 
    } 
    if (synthGraphicsUtils == null)
      throw new SAXException("graphicsUtils: you must supply an idref"); 
    if (this._style != null)
      this._style.setGraphicsUtils(synthGraphicsUtils); 
  }
  
  private void startInsets(Attributes paramAttributes) throws SAXException {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    Insets insets = null;
    String str = null;
    for (int n = paramAttributes.getLength() - 1; n >= 0; n--) {
      String str1 = paramAttributes.getQName(n);
      try {
        if (str1.equals("idref")) {
          insets = (Insets)lookup(paramAttributes.getValue(n), Insets.class);
        } else if (str1.equals("id")) {
          str = paramAttributes.getValue(n);
        } else if (str1.equals("top")) {
          i = Integer.parseInt(paramAttributes.getValue(n));
        } else if (str1.equals("left")) {
          k = Integer.parseInt(paramAttributes.getValue(n));
        } else if (str1.equals("bottom")) {
          j = Integer.parseInt(paramAttributes.getValue(n));
        } else if (str1.equals("right")) {
          m = Integer.parseInt(paramAttributes.getValue(n));
        } 
      } catch (NumberFormatException numberFormatException) {
        throw new SAXException("insets: bad integer value for " + paramAttributes.getValue(n));
      } 
    } 
    if (insets == null)
      insets = new InsetsUIResource(i, k, j, m); 
    register(str, insets);
    if (this._style != null)
      this._style.setInsets(insets); 
  }
  
  private void startBind(Attributes paramAttributes) throws SAXException {
    ParsedSynthStyle parsedSynthStyle = null;
    String str = null;
    byte b = -1;
    for (i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str1 = paramAttributes.getQName(i);
      if (str1.equals("style")) {
        parsedSynthStyle = (ParsedSynthStyle)lookup(paramAttributes.getValue(i), ParsedSynthStyle.class);
      } else if (str1.equals("type")) {
        String str2 = paramAttributes.getValue(i).toUpperCase();
        if (str2.equals("NAME")) {
          b = 0;
        } else if (str2.equals("REGION")) {
          b = 1;
        } else {
          throw new SAXException("bind: unknown type " + str2);
        } 
      } else if (str1.equals("key")) {
        str = paramAttributes.getValue(i);
      } 
    } 
    if (parsedSynthStyle == null || str == null || b == -1)
      throw new SAXException("bind: you must specify a style, type and key"); 
    try {
      this._factory.addStyle(parsedSynthStyle, str, b);
    } catch (PatternSyntaxException i) {
      PatternSyntaxException patternSyntaxException;
      throw new SAXException("bind: " + str + " is not a valid regular expression");
    } 
  }
  
  private void startPainter(Attributes paramAttributes, String paramString) throws SAXException {
    Insets insets1 = null;
    Insets insets2 = null;
    String str1 = null;
    boolean bool1 = true;
    boolean bool2 = true;
    SynthPainter synthPainter = null;
    String str2 = null;
    String str3 = null;
    byte b = -1;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str4 = paramAttributes.getQName(i);
      String str5 = paramAttributes.getValue(i);
      if (str4.equals("id")) {
        str3 = str5;
      } else if (str4.equals("method")) {
        str2 = str5.toLowerCase(Locale.ENGLISH);
      } else if (str4.equals("idref")) {
        synthPainter = (SynthPainter)lookup(str5, SynthPainter.class);
      } else if (str4.equals("path")) {
        str1 = str5;
      } else if (str4.equals("sourceInsets")) {
        insets1 = parseInsets(str5, paramString + ": sourceInsets must be top left bottom right");
      } else if (str4.equals("destinationInsets")) {
        insets2 = parseInsets(str5, paramString + ": destinationInsets must be top left bottom right");
      } else if (str4.equals("paintCenter")) {
        bool1 = str5.toLowerCase().equals("true");
        bool5 = true;
      } else if (str4.equals("stretch")) {
        bool2 = str5.toLowerCase().equals("true");
        bool4 = true;
      } else if (str4.equals("direction")) {
        str5 = str5.toUpperCase().intern();
        if (str5 == "EAST") {
          b = 3;
        } else if (str5 == "NORTH") {
          b = 1;
        } else if (str5 == "SOUTH") {
          b = 5;
        } else if (str5 == "WEST") {
          b = 7;
        } else if (str5 == "TOP") {
          b = 1;
        } else if (str5 == "LEFT") {
          b = 2;
        } else if (str5 == "BOTTOM") {
          b = 3;
        } else if (str5 == "RIGHT") {
          b = 4;
        } else if (str5 == "HORIZONTAL") {
          b = 0;
        } else if (str5 == "VERTICAL") {
          b = 1;
        } else if (str5 == "HORIZONTAL_SPLIT") {
          b = 1;
        } else if (str5 == "VERTICAL_SPLIT") {
          b = 0;
        } else {
          throw new SAXException(paramString + ": unknown direction");
        } 
      } else if (str4.equals("center")) {
        bool3 = str5.toLowerCase().equals("true");
      } 
    } 
    if (synthPainter == null) {
      if (paramString == "painter")
        throw new SAXException(paramString + ": you must specify an idref"); 
      if (insets1 == null && !bool3)
        throw new SAXException("property: you must specify sourceInsets"); 
      if (str1 == null)
        throw new SAXException("property: you must specify a path"); 
      if (bool3 && (insets1 != null || insets2 != null || bool5 || bool4))
        throw new SAXException("The attributes: sourceInsets, destinationInsets, paintCenter and stretch  are not legal when center is true"); 
      synthPainter = new ImagePainter(!bool2, bool1, insets1, insets2, getResource(str1), bool3);
    } 
    register(str3, synthPainter);
    if (this._stateInfo != null) {
      addPainterOrMerge(this._statePainters, str2, synthPainter, b);
    } else if (this._style != null) {
      addPainterOrMerge(this._stylePainters, str2, synthPainter, b);
    } 
  }
  
  private void addPainterOrMerge(List<ParsedSynthStyle.PainterInfo> paramList, String paramString, SynthPainter paramSynthPainter, int paramInt) {
    ParsedSynthStyle.PainterInfo painterInfo = new ParsedSynthStyle.PainterInfo(paramString, paramSynthPainter, paramInt);
    for (Object object : paramList) {
      ParsedSynthStyle.PainterInfo painterInfo1 = (ParsedSynthStyle.PainterInfo)object;
      if (painterInfo.equalsPainter(painterInfo1)) {
        painterInfo1.addPainter(paramSynthPainter);
        return;
      } 
    } 
    paramList.add(painterInfo);
  }
  
  private void startImageIcon(Attributes paramAttributes) throws SAXException {
    String str1 = null;
    String str2 = null;
    for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
      String str = paramAttributes.getQName(i);
      if (str.equals("id")) {
        str2 = paramAttributes.getValue(i);
      } else if (str.equals("path")) {
        str1 = paramAttributes.getValue(i);
      } 
    } 
    if (str1 == null)
      throw new SAXException("imageIcon: you must specify a path"); 
    register(str2, new LazyImageIcon(getResource(str1)));
  }
  
  private void startOpaque(Attributes paramAttributes) throws SAXException {
    if (this._style != null) {
      this._style.setOpaque(true);
      for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
        String str = paramAttributes.getQName(i);
        if (str.equals("value"))
          this._style.setOpaque("true".equals(paramAttributes.getValue(i).toLowerCase())); 
      } 
    } 
  }
  
  private void startInputMap(Attributes paramAttributes) throws SAXException {
    this._inputMapBindings.clear();
    this._inputMapID = null;
    if (this._style != null)
      for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
        String str = paramAttributes.getQName(i);
        if (str.equals("id"))
          this._inputMapID = paramAttributes.getValue(i); 
      }  
  }
  
  private void endInputMap() {
    if (this._inputMapID != null)
      register(this._inputMapID, new UIDefaults.LazyInputMap(this._inputMapBindings.toArray(new Object[this._inputMapBindings.size()]))); 
    this._inputMapBindings.clear();
    this._inputMapID = null;
  }
  
  private void startBindKey(Attributes paramAttributes) throws SAXException {
    if (this._inputMapID == null)
      return; 
    if (this._style != null) {
      String str1 = null;
      String str2 = null;
      for (int i = paramAttributes.getLength() - 1; i >= 0; i--) {
        String str = paramAttributes.getQName(i);
        if (str.equals("key")) {
          str1 = paramAttributes.getValue(i);
        } else if (str.equals("action")) {
          str2 = paramAttributes.getValue(i);
        } 
      } 
      if (str1 == null || str2 == null)
        throw new SAXException("bindKey: you must supply a key and action"); 
      this._inputMapBindings.add(str1);
      this._inputMapBindings.add(str2);
    } 
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2) throws IOException, SAXException { return isForwarding() ? getHandler().resolveEntity(paramString1, paramString2) : null; }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (isForwarding())
      getHandler().notationDecl(paramString1, paramString2, paramString3); 
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    if (isForwarding())
      getHandler().unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4); 
  }
  
  public void setDocumentLocator(Locator paramLocator) {
    if (isForwarding())
      getHandler().setDocumentLocator(paramLocator); 
  }
  
  public void startDocument() {
    if (isForwarding())
      getHandler().startDocument(); 
  }
  
  public void endDocument() {
    if (isForwarding())
      getHandler().endDocument(); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    paramString3 = paramString3.intern();
    if (paramString3 == "style") {
      startStyle(paramAttributes);
    } else if (paramString3 == "state") {
      startState(paramAttributes);
    } else if (paramString3 == "font") {
      startFont(paramAttributes);
    } else if (paramString3 == "color") {
      startColor(paramAttributes);
    } else if (paramString3 == "painter") {
      startPainter(paramAttributes, paramString3);
    } else if (paramString3 == "imagePainter") {
      startPainter(paramAttributes, paramString3);
    } else if (paramString3 == "property") {
      startProperty(paramAttributes, "property");
    } else if (paramString3 == "defaultsProperty") {
      startProperty(paramAttributes, "defaultsProperty");
    } else if (paramString3 == "graphicsUtils") {
      startGraphics(paramAttributes);
    } else if (paramString3 == "insets") {
      startInsets(paramAttributes);
    } else if (paramString3 == "bind") {
      startBind(paramAttributes);
    } else if (paramString3 == "bindKey") {
      startBindKey(paramAttributes);
    } else if (paramString3 == "imageIcon") {
      startImageIcon(paramAttributes);
    } else if (paramString3 == "opaque") {
      startOpaque(paramAttributes);
    } else if (paramString3 == "inputMap") {
      startInputMap(paramAttributes);
    } else if (paramString3 != "synth") {
      if (this._depth++ == 0)
        getHandler().startDocument(); 
      getHandler().startElement(paramString1, paramString2, paramString3, paramAttributes);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (isForwarding()) {
      getHandler().endElement(paramString1, paramString2, paramString3);
      this._depth--;
      if (!isForwarding())
        getHandler().startDocument(); 
    } else {
      paramString3 = paramString3.intern();
      if (paramString3 == "style") {
        endStyle();
      } else if (paramString3 == "state") {
        endState();
      } else if (paramString3 == "inputMap") {
        endInputMap();
      } 
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (isForwarding())
      getHandler().characters(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (isForwarding())
      getHandler().ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (isForwarding())
      getHandler().processingInstruction(paramString1, paramString2); 
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    if (isForwarding())
      getHandler().warning(paramSAXParseException); 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    if (isForwarding())
      getHandler().error(paramSAXParseException); 
  }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    if (isForwarding())
      getHandler().fatalError(paramSAXParseException); 
    throw paramSAXParseException;
  }
  
  private static class LazyImageIcon extends ImageIcon implements UIResource {
    private URL location;
    
    public LazyImageIcon(URL param1URL) { this.location = param1URL; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (getImage() != null)
        super.paintIcon(param1Component, param1Graphics, param1Int1, param1Int2); 
    }
    
    public int getIconWidth() { return (getImage() != null) ? super.getIconWidth() : 0; }
    
    public int getIconHeight() { return (getImage() != null) ? super.getIconHeight() : 0; }
    
    public Image getImage() {
      if (this.location != null) {
        setImage(Toolkit.getDefaultToolkit().getImage(this.location));
        this.location = null;
      } 
      return super.getImage();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */