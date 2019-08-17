package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class KOI8_R extends Charset implements HistoricallyNamedCharset {
  private static final String b2cTable = "─│┌┐└┘├┤┬┴┼▀▄█▌▐░▒▓⌠■∙√≈≤≥ ⌡°²·÷═║╒ё╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡Ё╢╣╤╥╦╧╨╩╪╫╬©юабцдефгхийклмнопярстужвьызшэщчъЮАБЦДЕФГХИЙКЛМНОПЯРСТУЖВЬЫЗШЭЩЧЪ\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
  
  private static final char[] b2c = "─│┌┐└┘├┤┬┴┼▀▄█▌▐░▒▓⌠■∙√≈≤≥ ⌡°²·÷═║╒ё╓╔╕╖╗╘╙╚╛╜╝╞╟╠╡Ё╢╣╤╥╦╧╨╩╪╫╬©юабцдефгхийклмнопярстужвьызшэщчъЮАБЦДЕФГХИЙКЛМНОПЯРСТУЖВЬЫЗШЭЩЧЪ\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".toCharArray();
  
  private static final char[] c2b = new char[1280];
  
  private static final char[] c2bIndex = new char[256];
  
  public KOI8_R() { super("KOI8-R", StandardCharsets.aliases_KOI8_R); }
  
  public String historicalName() { return "KOI8_R"; }
  
  public boolean contains(Charset paramCharset) { return (paramCharset.name().equals("US-ASCII") || paramCharset instanceof KOI8_R); }
  
  public CharsetDecoder newDecoder() { return new SingleByte.Decoder(this, b2c); }
  
  public CharsetEncoder newEncoder() { return new SingleByte.Encoder(this, c2b, c2bIndex); }
  
  static  {
    char[] arrayOfChar1 = b2c;
    char[] arrayOfChar2 = null;
    SingleByte.initC2B(arrayOfChar1, arrayOfChar2, c2b, c2bIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\KOI8_R.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */