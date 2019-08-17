package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class IBM855 extends Charset implements HistoricallyNamedCharset {
  private static final String b2cTable = "ђЂѓЃёЁєЄѕЅіІїЇјЈљЉњЊћЋќЌўЎџЏюЮъЪаАбБцЦдДеЕфФгГ«»░▒▓│┤хХиИ╣║╗╝йЙ┐└┴┬├─┼кК╚╔╩╦╠═╬¤лЛмМнНоОп┘┌█▄Пя▀ЯрРсСтТуУжЖвВьЬ№­ыЫзЗшШэЭщЩчЧ§■ \000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
  
  private static final char[] b2c = "ђЂѓЃёЁєЄѕЅіІїЇјЈљЉњЊћЋќЌўЎџЏюЮъЪаАбБцЦдДеЕфФгГ«»░▒▓│┤хХиИ╣║╗╝йЙ┐└┴┬├─┼кК╚╔╩╦╠═╬¤лЛмМнНоОп┘┌█▄Пя▀ЯрРсСтТуУжЖвВьЬ№­ыЫзЗшШэЭщЩчЧ§■ \000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".toCharArray();
  
  private static final char[] c2b = new char[1024];
  
  private static final char[] c2bIndex = new char[256];
  
  public IBM855() { super("IBM855", StandardCharsets.aliases_IBM855); }
  
  public String historicalName() { return "Cp855"; }
  
  public boolean contains(Charset paramCharset) { return paramCharset instanceof IBM855; }
  
  public CharsetDecoder newDecoder() { return new SingleByte.Decoder(this, b2c); }
  
  public CharsetEncoder newEncoder() { return new SingleByte.Encoder(this, c2b, c2bIndex); }
  
  static  {
    char[] arrayOfChar1 = b2c;
    char[] arrayOfChar2 = null;
    SingleByte.initC2B(arrayOfChar1, arrayOfChar2, c2b, c2bIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\IBM855.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */