package javax.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public interface DatatypeConverterInterface {
  String parseString(String paramString);
  
  BigInteger parseInteger(String paramString);
  
  int parseInt(String paramString);
  
  long parseLong(String paramString);
  
  short parseShort(String paramString);
  
  BigDecimal parseDecimal(String paramString);
  
  float parseFloat(String paramString);
  
  double parseDouble(String paramString);
  
  boolean parseBoolean(String paramString);
  
  byte parseByte(String paramString);
  
  QName parseQName(String paramString, NamespaceContext paramNamespaceContext);
  
  Calendar parseDateTime(String paramString);
  
  byte[] parseBase64Binary(String paramString);
  
  byte[] parseHexBinary(String paramString);
  
  long parseUnsignedInt(String paramString);
  
  int parseUnsignedShort(String paramString);
  
  Calendar parseTime(String paramString);
  
  Calendar parseDate(String paramString);
  
  String parseAnySimpleType(String paramString);
  
  String printString(String paramString);
  
  String printInteger(BigInteger paramBigInteger);
  
  String printInt(int paramInt);
  
  String printLong(long paramLong);
  
  String printShort(short paramShort);
  
  String printDecimal(BigDecimal paramBigDecimal);
  
  String printFloat(float paramFloat);
  
  String printDouble(double paramDouble);
  
  String printBoolean(boolean paramBoolean);
  
  String printByte(byte paramByte);
  
  String printQName(QName paramQName, NamespaceContext paramNamespaceContext);
  
  String printDateTime(Calendar paramCalendar);
  
  String printBase64Binary(byte[] paramArrayOfByte);
  
  String printHexBinary(byte[] paramArrayOfByte);
  
  String printUnsignedInt(long paramLong);
  
  String printUnsignedShort(int paramInt);
  
  String printTime(Calendar paramCalendar);
  
  String printDate(Calendar paramCalendar);
  
  String printAnySimpleType(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\DatatypeConverterInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */