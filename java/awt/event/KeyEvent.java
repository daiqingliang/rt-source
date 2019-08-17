package java.awt.event;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import sun.awt.AWTAccessor;
import sun.awt.ExtendedKeyCodes;

public class KeyEvent extends InputEvent {
  private boolean isProxyActive = false;
  
  public static final int KEY_FIRST = 400;
  
  public static final int KEY_LAST = 402;
  
  public static final int KEY_TYPED = 400;
  
  public static final int KEY_PRESSED = 401;
  
  public static final int KEY_RELEASED = 402;
  
  public static final int VK_ENTER = 10;
  
  public static final int VK_BACK_SPACE = 8;
  
  public static final int VK_TAB = 9;
  
  public static final int VK_CANCEL = 3;
  
  public static final int VK_CLEAR = 12;
  
  public static final int VK_SHIFT = 16;
  
  public static final int VK_CONTROL = 17;
  
  public static final int VK_ALT = 18;
  
  public static final int VK_PAUSE = 19;
  
  public static final int VK_CAPS_LOCK = 20;
  
  public static final int VK_ESCAPE = 27;
  
  public static final int VK_SPACE = 32;
  
  public static final int VK_PAGE_UP = 33;
  
  public static final int VK_PAGE_DOWN = 34;
  
  public static final int VK_END = 35;
  
  public static final int VK_HOME = 36;
  
  public static final int VK_LEFT = 37;
  
  public static final int VK_UP = 38;
  
  public static final int VK_RIGHT = 39;
  
  public static final int VK_DOWN = 40;
  
  public static final int VK_COMMA = 44;
  
  public static final int VK_MINUS = 45;
  
  public static final int VK_PERIOD = 46;
  
  public static final int VK_SLASH = 47;
  
  public static final int VK_0 = 48;
  
  public static final int VK_1 = 49;
  
  public static final int VK_2 = 50;
  
  public static final int VK_3 = 51;
  
  public static final int VK_4 = 52;
  
  public static final int VK_5 = 53;
  
  public static final int VK_6 = 54;
  
  public static final int VK_7 = 55;
  
  public static final int VK_8 = 56;
  
  public static final int VK_9 = 57;
  
  public static final int VK_SEMICOLON = 59;
  
  public static final int VK_EQUALS = 61;
  
  public static final int VK_A = 65;
  
  public static final int VK_B = 66;
  
  public static final int VK_C = 67;
  
  public static final int VK_D = 68;
  
  public static final int VK_E = 69;
  
  public static final int VK_F = 70;
  
  public static final int VK_G = 71;
  
  public static final int VK_H = 72;
  
  public static final int VK_I = 73;
  
  public static final int VK_J = 74;
  
  public static final int VK_K = 75;
  
  public static final int VK_L = 76;
  
  public static final int VK_M = 77;
  
  public static final int VK_N = 78;
  
  public static final int VK_O = 79;
  
  public static final int VK_P = 80;
  
  public static final int VK_Q = 81;
  
  public static final int VK_R = 82;
  
  public static final int VK_S = 83;
  
  public static final int VK_T = 84;
  
  public static final int VK_U = 85;
  
  public static final int VK_V = 86;
  
  public static final int VK_W = 87;
  
  public static final int VK_X = 88;
  
  public static final int VK_Y = 89;
  
  public static final int VK_Z = 90;
  
  public static final int VK_OPEN_BRACKET = 91;
  
  public static final int VK_BACK_SLASH = 92;
  
  public static final int VK_CLOSE_BRACKET = 93;
  
  public static final int VK_NUMPAD0 = 96;
  
  public static final int VK_NUMPAD1 = 97;
  
  public static final int VK_NUMPAD2 = 98;
  
  public static final int VK_NUMPAD3 = 99;
  
  public static final int VK_NUMPAD4 = 100;
  
  public static final int VK_NUMPAD5 = 101;
  
  public static final int VK_NUMPAD6 = 102;
  
  public static final int VK_NUMPAD7 = 103;
  
  public static final int VK_NUMPAD8 = 104;
  
  public static final int VK_NUMPAD9 = 105;
  
  public static final int VK_MULTIPLY = 106;
  
  public static final int VK_ADD = 107;
  
  public static final int VK_SEPARATER = 108;
  
  public static final int VK_SEPARATOR = 108;
  
  public static final int VK_SUBTRACT = 109;
  
  public static final int VK_DECIMAL = 110;
  
  public static final int VK_DIVIDE = 111;
  
  public static final int VK_DELETE = 127;
  
  public static final int VK_NUM_LOCK = 144;
  
  public static final int VK_SCROLL_LOCK = 145;
  
  public static final int VK_F1 = 112;
  
  public static final int VK_F2 = 113;
  
  public static final int VK_F3 = 114;
  
  public static final int VK_F4 = 115;
  
  public static final int VK_F5 = 116;
  
  public static final int VK_F6 = 117;
  
  public static final int VK_F7 = 118;
  
  public static final int VK_F8 = 119;
  
  public static final int VK_F9 = 120;
  
  public static final int VK_F10 = 121;
  
  public static final int VK_F11 = 122;
  
  public static final int VK_F12 = 123;
  
  public static final int VK_F13 = 61440;
  
  public static final int VK_F14 = 61441;
  
  public static final int VK_F15 = 61442;
  
  public static final int VK_F16 = 61443;
  
  public static final int VK_F17 = 61444;
  
  public static final int VK_F18 = 61445;
  
  public static final int VK_F19 = 61446;
  
  public static final int VK_F20 = 61447;
  
  public static final int VK_F21 = 61448;
  
  public static final int VK_F22 = 61449;
  
  public static final int VK_F23 = 61450;
  
  public static final int VK_F24 = 61451;
  
  public static final int VK_PRINTSCREEN = 154;
  
  public static final int VK_INSERT = 155;
  
  public static final int VK_HELP = 156;
  
  public static final int VK_META = 157;
  
  public static final int VK_BACK_QUOTE = 192;
  
  public static final int VK_QUOTE = 222;
  
  public static final int VK_KP_UP = 224;
  
  public static final int VK_KP_DOWN = 225;
  
  public static final int VK_KP_LEFT = 226;
  
  public static final int VK_KP_RIGHT = 227;
  
  public static final int VK_DEAD_GRAVE = 128;
  
  public static final int VK_DEAD_ACUTE = 129;
  
  public static final int VK_DEAD_CIRCUMFLEX = 130;
  
  public static final int VK_DEAD_TILDE = 131;
  
  public static final int VK_DEAD_MACRON = 132;
  
  public static final int VK_DEAD_BREVE = 133;
  
  public static final int VK_DEAD_ABOVEDOT = 134;
  
  public static final int VK_DEAD_DIAERESIS = 135;
  
  public static final int VK_DEAD_ABOVERING = 136;
  
  public static final int VK_DEAD_DOUBLEACUTE = 137;
  
  public static final int VK_DEAD_CARON = 138;
  
  public static final int VK_DEAD_CEDILLA = 139;
  
  public static final int VK_DEAD_OGONEK = 140;
  
  public static final int VK_DEAD_IOTA = 141;
  
  public static final int VK_DEAD_VOICED_SOUND = 142;
  
  public static final int VK_DEAD_SEMIVOICED_SOUND = 143;
  
  public static final int VK_AMPERSAND = 150;
  
  public static final int VK_ASTERISK = 151;
  
  public static final int VK_QUOTEDBL = 152;
  
  public static final int VK_LESS = 153;
  
  public static final int VK_GREATER = 160;
  
  public static final int VK_BRACELEFT = 161;
  
  public static final int VK_BRACERIGHT = 162;
  
  public static final int VK_AT = 512;
  
  public static final int VK_COLON = 513;
  
  public static final int VK_CIRCUMFLEX = 514;
  
  public static final int VK_DOLLAR = 515;
  
  public static final int VK_EURO_SIGN = 516;
  
  public static final int VK_EXCLAMATION_MARK = 517;
  
  public static final int VK_INVERTED_EXCLAMATION_MARK = 518;
  
  public static final int VK_LEFT_PARENTHESIS = 519;
  
  public static final int VK_NUMBER_SIGN = 520;
  
  public static final int VK_PLUS = 521;
  
  public static final int VK_RIGHT_PARENTHESIS = 522;
  
  public static final int VK_UNDERSCORE = 523;
  
  public static final int VK_WINDOWS = 524;
  
  public static final int VK_CONTEXT_MENU = 525;
  
  public static final int VK_FINAL = 24;
  
  public static final int VK_CONVERT = 28;
  
  public static final int VK_NONCONVERT = 29;
  
  public static final int VK_ACCEPT = 30;
  
  public static final int VK_MODECHANGE = 31;
  
  public static final int VK_KANA = 21;
  
  public static final int VK_KANJI = 25;
  
  public static final int VK_ALPHANUMERIC = 240;
  
  public static final int VK_KATAKANA = 241;
  
  public static final int VK_HIRAGANA = 242;
  
  public static final int VK_FULL_WIDTH = 243;
  
  public static final int VK_HALF_WIDTH = 244;
  
  public static final int VK_ROMAN_CHARACTERS = 245;
  
  public static final int VK_ALL_CANDIDATES = 256;
  
  public static final int VK_PREVIOUS_CANDIDATE = 257;
  
  public static final int VK_CODE_INPUT = 258;
  
  public static final int VK_JAPANESE_KATAKANA = 259;
  
  public static final int VK_JAPANESE_HIRAGANA = 260;
  
  public static final int VK_JAPANESE_ROMAN = 261;
  
  public static final int VK_KANA_LOCK = 262;
  
  public static final int VK_INPUT_METHOD_ON_OFF = 263;
  
  public static final int VK_CUT = 65489;
  
  public static final int VK_COPY = 65485;
  
  public static final int VK_PASTE = 65487;
  
  public static final int VK_UNDO = 65483;
  
  public static final int VK_AGAIN = 65481;
  
  public static final int VK_FIND = 65488;
  
  public static final int VK_PROPS = 65482;
  
  public static final int VK_STOP = 65480;
  
  public static final int VK_COMPOSE = 65312;
  
  public static final int VK_ALT_GRAPH = 65406;
  
  public static final int VK_BEGIN = 65368;
  
  public static final int VK_UNDEFINED = 0;
  
  public static final char CHAR_UNDEFINED = '￿';
  
  public static final int KEY_LOCATION_UNKNOWN = 0;
  
  public static final int KEY_LOCATION_STANDARD = 1;
  
  public static final int KEY_LOCATION_LEFT = 2;
  
  public static final int KEY_LOCATION_RIGHT = 3;
  
  public static final int KEY_LOCATION_NUMPAD = 4;
  
  int keyCode;
  
  char keyChar;
  
  int keyLocation;
  
  private long rawCode = 0L;
  
  private long primaryLevelUnicode = 0L;
  
  private long scancode = 0L;
  
  private long extendedKeyCode = 0L;
  
  private static final long serialVersionUID = -2352130953028126954L;
  
  private Component originalSource;
  
  private static native void initIDs();
  
  private KeyEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, char paramChar, int paramInt4, boolean paramBoolean) {
    this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramChar, paramInt4);
    this.isProxyActive = paramBoolean;
  }
  
  public KeyEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, char paramChar, int paramInt4) {
    super(paramComponent, paramInt1, paramLong, paramInt2);
    if (paramInt1 == 400) {
      if (paramChar == Character.MAX_VALUE)
        throw new IllegalArgumentException("invalid keyChar"); 
      if (paramInt3 != 0)
        throw new IllegalArgumentException("invalid keyCode"); 
      if (paramInt4 != 0)
        throw new IllegalArgumentException("invalid keyLocation"); 
    } 
    this.keyCode = paramInt3;
    this.keyChar = paramChar;
    if (paramInt4 < 0 || paramInt4 > 4)
      throw new IllegalArgumentException("invalid keyLocation"); 
    this.keyLocation = paramInt4;
    if (getModifiers() != 0 && getModifiersEx() == 0) {
      setNewModifiers();
    } else if (getModifiers() == 0 && getModifiersEx() != 0) {
      setOldModifiers();
    } 
    this.originalSource = paramComponent;
  }
  
  public KeyEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, char paramChar) { this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramChar, 0); }
  
  @Deprecated
  public KeyEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3) { this(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, (char)paramInt3); }
  
  public int getKeyCode() { return this.keyCode; }
  
  public void setKeyCode(int paramInt) { this.keyCode = paramInt; }
  
  public char getKeyChar() { return this.keyChar; }
  
  public void setKeyChar(char paramChar) { this.keyChar = paramChar; }
  
  @Deprecated
  public void setModifiers(int paramInt) {
    this.modifiers = paramInt;
    if (getModifiers() != 0 && getModifiersEx() == 0) {
      setNewModifiers();
    } else if (getModifiers() == 0 && getModifiersEx() != 0) {
      setOldModifiers();
    } 
  }
  
  public int getKeyLocation() { return this.keyLocation; }
  
  public static String getKeyText(int paramInt) {
    if ((paramInt >= 48 && paramInt <= 57) || (paramInt >= 65 && paramInt <= 90))
      return String.valueOf((char)paramInt); 
    switch (paramInt) {
      case 10:
        return Toolkit.getProperty("AWT.enter", "Enter");
      case 8:
        return Toolkit.getProperty("AWT.backSpace", "Backspace");
      case 9:
        return Toolkit.getProperty("AWT.tab", "Tab");
      case 3:
        return Toolkit.getProperty("AWT.cancel", "Cancel");
      case 12:
        return Toolkit.getProperty("AWT.clear", "Clear");
      case 65312:
        return Toolkit.getProperty("AWT.compose", "Compose");
      case 19:
        return Toolkit.getProperty("AWT.pause", "Pause");
      case 20:
        return Toolkit.getProperty("AWT.capsLock", "Caps Lock");
      case 27:
        return Toolkit.getProperty("AWT.escape", "Escape");
      case 32:
        return Toolkit.getProperty("AWT.space", "Space");
      case 33:
        return Toolkit.getProperty("AWT.pgup", "Page Up");
      case 34:
        return Toolkit.getProperty("AWT.pgdn", "Page Down");
      case 35:
        return Toolkit.getProperty("AWT.end", "End");
      case 36:
        return Toolkit.getProperty("AWT.home", "Home");
      case 37:
        return Toolkit.getProperty("AWT.left", "Left");
      case 38:
        return Toolkit.getProperty("AWT.up", "Up");
      case 39:
        return Toolkit.getProperty("AWT.right", "Right");
      case 40:
        return Toolkit.getProperty("AWT.down", "Down");
      case 65368:
        return Toolkit.getProperty("AWT.begin", "Begin");
      case 16:
        return Toolkit.getProperty("AWT.shift", "Shift");
      case 17:
        return Toolkit.getProperty("AWT.control", "Control");
      case 18:
        return Toolkit.getProperty("AWT.alt", "Alt");
      case 157:
        return Toolkit.getProperty("AWT.meta", "Meta");
      case 65406:
        return Toolkit.getProperty("AWT.altGraph", "Alt Graph");
      case 44:
        return Toolkit.getProperty("AWT.comma", "Comma");
      case 46:
        return Toolkit.getProperty("AWT.period", "Period");
      case 47:
        return Toolkit.getProperty("AWT.slash", "Slash");
      case 59:
        return Toolkit.getProperty("AWT.semicolon", "Semicolon");
      case 61:
        return Toolkit.getProperty("AWT.equals", "Equals");
      case 91:
        return Toolkit.getProperty("AWT.openBracket", "Open Bracket");
      case 92:
        return Toolkit.getProperty("AWT.backSlash", "Back Slash");
      case 93:
        return Toolkit.getProperty("AWT.closeBracket", "Close Bracket");
      case 106:
        return Toolkit.getProperty("AWT.multiply", "NumPad *");
      case 107:
        return Toolkit.getProperty("AWT.add", "NumPad +");
      case 108:
        return Toolkit.getProperty("AWT.separator", "NumPad ,");
      case 109:
        return Toolkit.getProperty("AWT.subtract", "NumPad -");
      case 110:
        return Toolkit.getProperty("AWT.decimal", "NumPad .");
      case 111:
        return Toolkit.getProperty("AWT.divide", "NumPad /");
      case 127:
        return Toolkit.getProperty("AWT.delete", "Delete");
      case 144:
        return Toolkit.getProperty("AWT.numLock", "Num Lock");
      case 145:
        return Toolkit.getProperty("AWT.scrollLock", "Scroll Lock");
      case 524:
        return Toolkit.getProperty("AWT.windows", "Windows");
      case 525:
        return Toolkit.getProperty("AWT.context", "Context Menu");
      case 112:
        return Toolkit.getProperty("AWT.f1", "F1");
      case 113:
        return Toolkit.getProperty("AWT.f2", "F2");
      case 114:
        return Toolkit.getProperty("AWT.f3", "F3");
      case 115:
        return Toolkit.getProperty("AWT.f4", "F4");
      case 116:
        return Toolkit.getProperty("AWT.f5", "F5");
      case 117:
        return Toolkit.getProperty("AWT.f6", "F6");
      case 118:
        return Toolkit.getProperty("AWT.f7", "F7");
      case 119:
        return Toolkit.getProperty("AWT.f8", "F8");
      case 120:
        return Toolkit.getProperty("AWT.f9", "F9");
      case 121:
        return Toolkit.getProperty("AWT.f10", "F10");
      case 122:
        return Toolkit.getProperty("AWT.f11", "F11");
      case 123:
        return Toolkit.getProperty("AWT.f12", "F12");
      case 61440:
        return Toolkit.getProperty("AWT.f13", "F13");
      case 61441:
        return Toolkit.getProperty("AWT.f14", "F14");
      case 61442:
        return Toolkit.getProperty("AWT.f15", "F15");
      case 61443:
        return Toolkit.getProperty("AWT.f16", "F16");
      case 61444:
        return Toolkit.getProperty("AWT.f17", "F17");
      case 61445:
        return Toolkit.getProperty("AWT.f18", "F18");
      case 61446:
        return Toolkit.getProperty("AWT.f19", "F19");
      case 61447:
        return Toolkit.getProperty("AWT.f20", "F20");
      case 61448:
        return Toolkit.getProperty("AWT.f21", "F21");
      case 61449:
        return Toolkit.getProperty("AWT.f22", "F22");
      case 61450:
        return Toolkit.getProperty("AWT.f23", "F23");
      case 61451:
        return Toolkit.getProperty("AWT.f24", "F24");
      case 154:
        return Toolkit.getProperty("AWT.printScreen", "Print Screen");
      case 155:
        return Toolkit.getProperty("AWT.insert", "Insert");
      case 156:
        return Toolkit.getProperty("AWT.help", "Help");
      case 192:
        return Toolkit.getProperty("AWT.backQuote", "Back Quote");
      case 222:
        return Toolkit.getProperty("AWT.quote", "Quote");
      case 224:
        return Toolkit.getProperty("AWT.up", "Up");
      case 225:
        return Toolkit.getProperty("AWT.down", "Down");
      case 226:
        return Toolkit.getProperty("AWT.left", "Left");
      case 227:
        return Toolkit.getProperty("AWT.right", "Right");
      case 128:
        return Toolkit.getProperty("AWT.deadGrave", "Dead Grave");
      case 129:
        return Toolkit.getProperty("AWT.deadAcute", "Dead Acute");
      case 130:
        return Toolkit.getProperty("AWT.deadCircumflex", "Dead Circumflex");
      case 131:
        return Toolkit.getProperty("AWT.deadTilde", "Dead Tilde");
      case 132:
        return Toolkit.getProperty("AWT.deadMacron", "Dead Macron");
      case 133:
        return Toolkit.getProperty("AWT.deadBreve", "Dead Breve");
      case 134:
        return Toolkit.getProperty("AWT.deadAboveDot", "Dead Above Dot");
      case 135:
        return Toolkit.getProperty("AWT.deadDiaeresis", "Dead Diaeresis");
      case 136:
        return Toolkit.getProperty("AWT.deadAboveRing", "Dead Above Ring");
      case 137:
        return Toolkit.getProperty("AWT.deadDoubleAcute", "Dead Double Acute");
      case 138:
        return Toolkit.getProperty("AWT.deadCaron", "Dead Caron");
      case 139:
        return Toolkit.getProperty("AWT.deadCedilla", "Dead Cedilla");
      case 140:
        return Toolkit.getProperty("AWT.deadOgonek", "Dead Ogonek");
      case 141:
        return Toolkit.getProperty("AWT.deadIota", "Dead Iota");
      case 142:
        return Toolkit.getProperty("AWT.deadVoicedSound", "Dead Voiced Sound");
      case 143:
        return Toolkit.getProperty("AWT.deadSemivoicedSound", "Dead Semivoiced Sound");
      case 150:
        return Toolkit.getProperty("AWT.ampersand", "Ampersand");
      case 151:
        return Toolkit.getProperty("AWT.asterisk", "Asterisk");
      case 152:
        return Toolkit.getProperty("AWT.quoteDbl", "Double Quote");
      case 153:
        return Toolkit.getProperty("AWT.Less", "Less");
      case 160:
        return Toolkit.getProperty("AWT.greater", "Greater");
      case 161:
        return Toolkit.getProperty("AWT.braceLeft", "Left Brace");
      case 162:
        return Toolkit.getProperty("AWT.braceRight", "Right Brace");
      case 512:
        return Toolkit.getProperty("AWT.at", "At");
      case 513:
        return Toolkit.getProperty("AWT.colon", "Colon");
      case 514:
        return Toolkit.getProperty("AWT.circumflex", "Circumflex");
      case 515:
        return Toolkit.getProperty("AWT.dollar", "Dollar");
      case 516:
        return Toolkit.getProperty("AWT.euro", "Euro");
      case 517:
        return Toolkit.getProperty("AWT.exclamationMark", "Exclamation Mark");
      case 518:
        return Toolkit.getProperty("AWT.invertedExclamationMark", "Inverted Exclamation Mark");
      case 519:
        return Toolkit.getProperty("AWT.leftParenthesis", "Left Parenthesis");
      case 520:
        return Toolkit.getProperty("AWT.numberSign", "Number Sign");
      case 45:
        return Toolkit.getProperty("AWT.minus", "Minus");
      case 521:
        return Toolkit.getProperty("AWT.plus", "Plus");
      case 522:
        return Toolkit.getProperty("AWT.rightParenthesis", "Right Parenthesis");
      case 523:
        return Toolkit.getProperty("AWT.underscore", "Underscore");
      case 24:
        return Toolkit.getProperty("AWT.final", "Final");
      case 28:
        return Toolkit.getProperty("AWT.convert", "Convert");
      case 29:
        return Toolkit.getProperty("AWT.noconvert", "No Convert");
      case 30:
        return Toolkit.getProperty("AWT.accept", "Accept");
      case 31:
        return Toolkit.getProperty("AWT.modechange", "Mode Change");
      case 21:
        return Toolkit.getProperty("AWT.kana", "Kana");
      case 25:
        return Toolkit.getProperty("AWT.kanji", "Kanji");
      case 240:
        return Toolkit.getProperty("AWT.alphanumeric", "Alphanumeric");
      case 241:
        return Toolkit.getProperty("AWT.katakana", "Katakana");
      case 242:
        return Toolkit.getProperty("AWT.hiragana", "Hiragana");
      case 243:
        return Toolkit.getProperty("AWT.fullWidth", "Full-Width");
      case 244:
        return Toolkit.getProperty("AWT.halfWidth", "Half-Width");
      case 245:
        return Toolkit.getProperty("AWT.romanCharacters", "Roman Characters");
      case 256:
        return Toolkit.getProperty("AWT.allCandidates", "All Candidates");
      case 257:
        return Toolkit.getProperty("AWT.previousCandidate", "Previous Candidate");
      case 258:
        return Toolkit.getProperty("AWT.codeInput", "Code Input");
      case 259:
        return Toolkit.getProperty("AWT.japaneseKatakana", "Japanese Katakana");
      case 260:
        return Toolkit.getProperty("AWT.japaneseHiragana", "Japanese Hiragana");
      case 261:
        return Toolkit.getProperty("AWT.japaneseRoman", "Japanese Roman");
      case 262:
        return Toolkit.getProperty("AWT.kanaLock", "Kana Lock");
      case 263:
        return Toolkit.getProperty("AWT.inputMethodOnOff", "Input Method On/Off");
      case 65481:
        return Toolkit.getProperty("AWT.again", "Again");
      case 65483:
        return Toolkit.getProperty("AWT.undo", "Undo");
      case 65485:
        return Toolkit.getProperty("AWT.copy", "Copy");
      case 65487:
        return Toolkit.getProperty("AWT.paste", "Paste");
      case 65489:
        return Toolkit.getProperty("AWT.cut", "Cut");
      case 65488:
        return Toolkit.getProperty("AWT.find", "Find");
      case 65482:
        return Toolkit.getProperty("AWT.props", "Props");
      case 65480:
        return Toolkit.getProperty("AWT.stop", "Stop");
    } 
    if (paramInt >= 96 && paramInt <= 105) {
      String str1 = Toolkit.getProperty("AWT.numpad", "NumPad");
      char c = (char)(paramInt - 96 + 48);
      return str1 + "-" + c;
    } 
    if ((paramInt & 0x1000000) != 0)
      return String.valueOf((char)(paramInt ^ 0x1000000)); 
    String str = Toolkit.getProperty("AWT.unknown", "Unknown");
    return str + " keyCode: 0x" + Integer.toString(paramInt, 16);
  }
  
  public static String getKeyModifiersText(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    if ((paramInt & 0x4) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.meta", "Meta"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x2) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.control", "Ctrl"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x8) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.alt", "Alt"));
      stringBuilder.append("+");
    } 
    if ((paramInt & true) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.shift", "Shift"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x20) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.altGraph", "Alt Graph"));
      stringBuilder.append("+");
    } 
    if ((paramInt & 0x10) != 0) {
      stringBuilder.append(Toolkit.getProperty("AWT.button1", "Button1"));
      stringBuilder.append("+");
    } 
    if (stringBuilder.length() > 0)
      stringBuilder.setLength(stringBuilder.length() - 1); 
    return stringBuilder.toString();
  }
  
  public boolean isActionKey() {
    switch (this.keyCode) {
      case 19:
      case 20:
      case 21:
      case 24:
      case 25:
      case 28:
      case 29:
      case 30:
      case 31:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 112:
      case 113:
      case 114:
      case 115:
      case 116:
      case 117:
      case 118:
      case 119:
      case 120:
      case 121:
      case 122:
      case 123:
      case 144:
      case 145:
      case 154:
      case 155:
      case 156:
      case 224:
      case 225:
      case 226:
      case 227:
      case 240:
      case 241:
      case 242:
      case 243:
      case 244:
      case 245:
      case 256:
      case 257:
      case 258:
      case 259:
      case 260:
      case 261:
      case 262:
      case 263:
      case 524:
      case 525:
      case 61440:
      case 61441:
      case 61442:
      case 61443:
      case 61444:
      case 61445:
      case 61446:
      case 61447:
      case 61448:
      case 61449:
      case 61450:
      case 61451:
      case 65368:
      case 65480:
      case 65481:
      case 65482:
      case 65483:
      case 65485:
      case 65487:
      case 65488:
      case 65489:
        return true;
    } 
    return false;
  }
  
  public String paramString() {
    StringBuilder stringBuilder = new StringBuilder(100);
    switch (this.id) {
      case 401:
        stringBuilder.append("KEY_PRESSED");
        break;
      case 402:
        stringBuilder.append("KEY_RELEASED");
        break;
      case 400:
        stringBuilder.append("KEY_TYPED");
        break;
      default:
        stringBuilder.append("unknown type");
        break;
    } 
    stringBuilder.append(",keyCode=").append(this.keyCode);
    stringBuilder.append(",keyText=").append(getKeyText(this.keyCode));
    stringBuilder.append(",keyChar=");
    switch (this.keyChar) {
      case '\b':
        stringBuilder.append(getKeyText(8));
        break;
      case '\t':
        stringBuilder.append(getKeyText(9));
        break;
      case '\n':
        stringBuilder.append(getKeyText(10));
        break;
      case '\030':
        stringBuilder.append(getKeyText(3));
        break;
      case '\033':
        stringBuilder.append(getKeyText(27));
        break;
      case '':
        stringBuilder.append(getKeyText(127));
        break;
      case '￿':
        stringBuilder.append(Toolkit.getProperty("AWT.undefined", "Undefined"));
        stringBuilder.append(" keyChar");
        break;
      default:
        stringBuilder.append("'").append(this.keyChar).append("'");
        break;
    } 
    if (getModifiers() != 0)
      stringBuilder.append(",modifiers=").append(getKeyModifiersText(this.modifiers)); 
    if (getModifiersEx() != 0)
      stringBuilder.append(",extModifiers=").append(getModifiersExText(this.modifiers)); 
    stringBuilder.append(",keyLocation=");
    switch (this.keyLocation) {
      case 0:
        stringBuilder.append("KEY_LOCATION_UNKNOWN");
        stringBuilder.append(",rawCode=").append(this.rawCode);
        stringBuilder.append(",primaryLevelUnicode=").append(this.primaryLevelUnicode);
        stringBuilder.append(",scancode=").append(this.scancode);
        stringBuilder.append(",extendedKeyCode=0x").append(Long.toHexString(this.extendedKeyCode));
        return stringBuilder.toString();
      case 1:
        stringBuilder.append("KEY_LOCATION_STANDARD");
        stringBuilder.append(",rawCode=").append(this.rawCode);
        stringBuilder.append(",primaryLevelUnicode=").append(this.primaryLevelUnicode);
        stringBuilder.append(",scancode=").append(this.scancode);
        stringBuilder.append(",extendedKeyCode=0x").append(Long.toHexString(this.extendedKeyCode));
        return stringBuilder.toString();
      case 2:
        stringBuilder.append("KEY_LOCATION_LEFT");
        stringBuilder.append(",rawCode=").append(this.rawCode);
        stringBuilder.append(",primaryLevelUnicode=").append(this.primaryLevelUnicode);
        stringBuilder.append(",scancode=").append(this.scancode);
        stringBuilder.append(",extendedKeyCode=0x").append(Long.toHexString(this.extendedKeyCode));
        return stringBuilder.toString();
      case 3:
        stringBuilder.append("KEY_LOCATION_RIGHT");
        stringBuilder.append(",rawCode=").append(this.rawCode);
        stringBuilder.append(",primaryLevelUnicode=").append(this.primaryLevelUnicode);
        stringBuilder.append(",scancode=").append(this.scancode);
        stringBuilder.append(",extendedKeyCode=0x").append(Long.toHexString(this.extendedKeyCode));
        return stringBuilder.toString();
      case 4:
        stringBuilder.append("KEY_LOCATION_NUMPAD");
        stringBuilder.append(",rawCode=").append(this.rawCode);
        stringBuilder.append(",primaryLevelUnicode=").append(this.primaryLevelUnicode);
        stringBuilder.append(",scancode=").append(this.scancode);
        stringBuilder.append(",extendedKeyCode=0x").append(Long.toHexString(this.extendedKeyCode));
        return stringBuilder.toString();
    } 
    stringBuilder.append("KEY_LOCATION_UNKNOWN");
    stringBuilder.append(",rawCode=").append(this.rawCode);
    stringBuilder.append(",primaryLevelUnicode=").append(this.primaryLevelUnicode);
    stringBuilder.append(",scancode=").append(this.scancode);
    stringBuilder.append(",extendedKeyCode=0x").append(Long.toHexString(this.extendedKeyCode));
    return stringBuilder.toString();
  }
  
  public int getExtendedKeyCode() { return (int)this.extendedKeyCode; }
  
  public static int getExtendedKeyCodeForChar(int paramInt) { return ExtendedKeyCodes.getExtendedKeyCodeForChar(paramInt); }
  
  private void setNewModifiers() {
    if ((this.modifiers & true) != 0)
      this.modifiers |= 0x40; 
    if ((this.modifiers & 0x8) != 0)
      this.modifiers |= 0x200; 
    if ((this.modifiers & 0x2) != 0)
      this.modifiers |= 0x80; 
    if ((this.modifiers & 0x4) != 0)
      this.modifiers |= 0x100; 
    if ((this.modifiers & 0x20) != 0)
      this.modifiers |= 0x2000; 
    if ((this.modifiers & 0x10) != 0)
      this.modifiers |= 0x400; 
  }
  
  private void setOldModifiers() {
    if ((this.modifiers & 0x40) != 0)
      this.modifiers |= 0x1; 
    if ((this.modifiers & 0x200) != 0)
      this.modifiers |= 0x8; 
    if ((this.modifiers & 0x80) != 0)
      this.modifiers |= 0x2; 
    if ((this.modifiers & 0x100) != 0)
      this.modifiers |= 0x4; 
    if ((this.modifiers & 0x2000) != 0)
      this.modifiers |= 0x20; 
    if ((this.modifiers & 0x400) != 0)
      this.modifiers |= 0x10; 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (getModifiers() != 0 && getModifiersEx() == 0)
      setNewModifiers(); 
  }
  
  static  {
    NativeLibLoader.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setKeyEventAccessor(new AWTAccessor.KeyEventAccessor() {
          public void setRawCode(KeyEvent param1KeyEvent, long param1Long) { param1KeyEvent.rawCode = param1Long; }
          
          public void setPrimaryLevelUnicode(KeyEvent param1KeyEvent, long param1Long) { param1KeyEvent.primaryLevelUnicode = param1Long; }
          
          public void setExtendedKeyCode(KeyEvent param1KeyEvent, long param1Long) { param1KeyEvent.extendedKeyCode = param1Long; }
          
          public Component getOriginalSource(KeyEvent param1KeyEvent) { return param1KeyEvent.originalSource; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\KeyEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */