package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class IBM850 extends Charset implements HistoricallyNamedCharset {
  private static final String b2cTable = "ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤ÁÂÀ©╣║╗╝¢¥┐└┴┬├─┼ãÃ╚╔╩╦╠═╬¤ðÐÊËÈıÍÎÏ┘┌█▄¦Ì▀ÓßÔÒõÕµþÞÚÛÙýÝ¯´­±‗¾¶§÷¸°¨·¹³²■ \000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
  
  private static final char[] b2c = "ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤ÁÂÀ©╣║╗╝¢¥┐└┴┬├─┼ãÃ╚╔╩╦╠═╬¤ðÐÊËÈıÍÎÏ┘┌█▄¦Ì▀ÓßÔÒõÕµþÞÚÛÙýÝ¯´­±‗¾¶§÷¸°¨·¹³²■ \000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~".toCharArray();
  
  private static final char[] c2b = new char[1024];
  
  private static final char[] c2bIndex = new char[256];
  
  public IBM850() { super("IBM850", StandardCharsets.aliases_IBM850); }
  
  public String historicalName() { return "Cp850"; }
  
  public boolean contains(Charset paramCharset) { return paramCharset instanceof IBM850; }
  
  public CharsetDecoder newDecoder() { return new SingleByte.Decoder(this, b2c); }
  
  public CharsetEncoder newEncoder() { return new SingleByte.Encoder(this, c2b, c2bIndex); }
  
  static  {
    char[] arrayOfChar1 = b2c;
    char[] arrayOfChar2 = null;
    SingleByte.initC2B(arrayOfChar1, arrayOfChar2, c2b, c2bIndex);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\IBM850.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */