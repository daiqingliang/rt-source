package javax.xml.bind;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public final class DatatypeConverter {
  private static final JAXBPermission SET_DATATYPE_CONVERTER_PERMISSION = new JAXBPermission("setDatatypeConverter");
  
  public static void setDatatypeConverter(DatatypeConverterInterface paramDatatypeConverterInterface) {
    if (paramDatatypeConverterInterface == null)
      throw new IllegalArgumentException(Messages.format("DatatypeConverter.ConverterMustNotBeNull")); 
    if (theConverter == null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SET_DATATYPE_CONVERTER_PERMISSION); 
      theConverter = paramDatatypeConverterInterface;
    } 
  }
  
  private static void initConverter() { theConverter = new DatatypeConverterImpl(); }
  
  public static String parseString(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseString(paramString);
  }
  
  public static BigInteger parseInteger(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseInteger(paramString);
  }
  
  public static int parseInt(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseInt(paramString);
  }
  
  public static long parseLong(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseLong(paramString);
  }
  
  public static short parseShort(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseShort(paramString);
  }
  
  public static BigDecimal parseDecimal(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseDecimal(paramString);
  }
  
  public static float parseFloat(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseFloat(paramString);
  }
  
  public static double parseDouble(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseDouble(paramString);
  }
  
  public static boolean parseBoolean(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseBoolean(paramString);
  }
  
  public static byte parseByte(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseByte(paramString);
  }
  
  public static QName parseQName(String paramString, NamespaceContext paramNamespaceContext) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseQName(paramString, paramNamespaceContext);
  }
  
  public static Calendar parseDateTime(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseDateTime(paramString);
  }
  
  public static byte[] parseBase64Binary(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseBase64Binary(paramString);
  }
  
  public static byte[] parseHexBinary(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseHexBinary(paramString);
  }
  
  public static long parseUnsignedInt(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseUnsignedInt(paramString);
  }
  
  public static int parseUnsignedShort(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseUnsignedShort(paramString);
  }
  
  public static Calendar parseTime(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseTime(paramString);
  }
  
  public static Calendar parseDate(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseDate(paramString);
  }
  
  public static String parseAnySimpleType(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.parseAnySimpleType(paramString);
  }
  
  public static String printString(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printString(paramString);
  }
  
  public static String printInteger(BigInteger paramBigInteger) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printInteger(paramBigInteger);
  }
  
  public static String printInt(int paramInt) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printInt(paramInt);
  }
  
  public static String printLong(long paramLong) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printLong(paramLong);
  }
  
  public static String printShort(short paramShort) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printShort(paramShort);
  }
  
  public static String printDecimal(BigDecimal paramBigDecimal) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printDecimal(paramBigDecimal);
  }
  
  public static String printFloat(float paramFloat) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printFloat(paramFloat);
  }
  
  public static String printDouble(double paramDouble) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printDouble(paramDouble);
  }
  
  public static String printBoolean(boolean paramBoolean) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printBoolean(paramBoolean);
  }
  
  public static String printByte(byte paramByte) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printByte(paramByte);
  }
  
  public static String printQName(QName paramQName, NamespaceContext paramNamespaceContext) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printQName(paramQName, paramNamespaceContext);
  }
  
  public static String printDateTime(Calendar paramCalendar) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printDateTime(paramCalendar);
  }
  
  public static String printBase64Binary(byte[] paramArrayOfByte) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printBase64Binary(paramArrayOfByte);
  }
  
  public static String printHexBinary(byte[] paramArrayOfByte) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printHexBinary(paramArrayOfByte);
  }
  
  public static String printUnsignedInt(long paramLong) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printUnsignedInt(paramLong);
  }
  
  public static String printUnsignedShort(int paramInt) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printUnsignedShort(paramInt);
  }
  
  public static String printTime(Calendar paramCalendar) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printTime(paramCalendar);
  }
  
  public static String printDate(Calendar paramCalendar) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printDate(paramCalendar);
  }
  
  public static String printAnySimpleType(String paramString) {
    if (theConverter == null)
      initConverter(); 
    return theConverter.printAnySimpleType(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\DatatypeConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */