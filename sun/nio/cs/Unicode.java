package sun.nio.cs;

import java.nio.charset.Charset;

abstract class Unicode extends Charset implements HistoricallyNamedCharset {
  public Unicode(String paramString, String[] paramArrayOfString) { super(paramString, paramArrayOfString); }
  
  public boolean contains(Charset paramCharset) { return (paramCharset instanceof US_ASCII || paramCharset instanceof ISO_8859_1 || paramCharset instanceof ISO_8859_15 || paramCharset instanceof MS1252 || paramCharset instanceof UTF_8 || paramCharset instanceof UTF_16 || paramCharset instanceof UTF_16BE || paramCharset instanceof UTF_16LE || paramCharset instanceof UTF_16LE_BOM || paramCharset.name().equals("GBK") || paramCharset.name().equals("GB18030") || paramCharset.name().equals("ISO-8859-2") || paramCharset.name().equals("ISO-8859-3") || paramCharset.name().equals("ISO-8859-4") || paramCharset.name().equals("ISO-8859-5") || paramCharset.name().equals("ISO-8859-6") || paramCharset.name().equals("ISO-8859-7") || paramCharset.name().equals("ISO-8859-8") || paramCharset.name().equals("ISO-8859-9") || paramCharset.name().equals("ISO-8859-13") || paramCharset.name().equals("JIS_X0201") || paramCharset.name().equals("x-JIS0208") || paramCharset.name().equals("JIS_X0212-1990") || paramCharset.name().equals("GB2312") || paramCharset.name().equals("EUC-KR") || paramCharset.name().equals("x-EUC-TW") || paramCharset.name().equals("EUC-JP") || paramCharset.name().equals("x-euc-jp-linux") || paramCharset.name().equals("KOI8-R") || paramCharset.name().equals("TIS-620") || paramCharset.name().equals("x-ISCII91") || paramCharset.name().equals("windows-1251") || paramCharset.name().equals("windows-1253") || paramCharset.name().equals("windows-1254") || paramCharset.name().equals("windows-1255") || paramCharset.name().equals("windows-1256") || paramCharset.name().equals("windows-1257") || paramCharset.name().equals("windows-1258") || paramCharset.name().equals("windows-932") || paramCharset.name().equals("x-mswin-936") || paramCharset.name().equals("x-windows-949") || paramCharset.name().equals("x-windows-950") || paramCharset.name().equals("windows-31j") || paramCharset.name().equals("Big5") || paramCharset.name().equals("Big5-HKSCS") || paramCharset.name().equals("x-MS950-HKSCS") || paramCharset.name().equals("ISO-2022-JP") || paramCharset.name().equals("ISO-2022-KR") || paramCharset.name().equals("x-ISO-2022-CN-CNS") || paramCharset.name().equals("x-ISO-2022-CN-GB") || paramCharset.name().equals("Big5-HKSCS") || paramCharset.name().equals("x-Johab") || paramCharset.name().equals("Shift_JIS")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\Unicode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */