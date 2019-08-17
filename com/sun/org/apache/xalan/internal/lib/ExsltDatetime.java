package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ExsltDatetime {
  static final String dt = "yyyy-MM-dd'T'HH:mm:ss";
  
  static final String d = "yyyy-MM-dd";
  
  static final String gym = "yyyy-MM";
  
  static final String gy = "yyyy";
  
  static final String gmd = "--MM-dd";
  
  static final String gm = "--MM--";
  
  static final String gd = "---dd";
  
  static final String t = "HH:mm:ss";
  
  static final String EMPTY_STR = "";
  
  public static String dateTime() {
    Calendar calendar = Calendar.getInstance();
    Date date = calendar.getTime();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    StringBuffer stringBuffer = new StringBuffer(simpleDateFormat.format(date));
    int i = calendar.get(15) + calendar.get(16);
    if (i == 0) {
      stringBuffer.append("Z");
    } else {
      int j = i / 3600000;
      int k = i % 3600000;
      char c = (j < 0) ? '-' : '+';
      stringBuffer.append(c).append(formatDigits(j)).append(':').append(formatDigits(k));
    } 
    return stringBuffer.toString();
  }
  
  private static String formatDigits(int paramInt) {
    String str = String.valueOf(Math.abs(paramInt));
    return (str.length() == 1) ? ('0' + str) : str;
  }
  
  public static String date(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[0];
    String str2 = arrayOfString1[1];
    String str3 = arrayOfString1[2];
    if (str2 == null || str3 == null)
      return ""; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    String str4 = "yyyy-MM-dd";
    Date date = testFormats(str2, arrayOfString2);
    if (date == null)
      return ""; 
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str4);
    simpleDateFormat.setLenient(false);
    String str5 = simpleDateFormat.format(date);
    return (str5.length() == 0) ? "" : (str1 + str5 + str3);
  }
  
  public static String date() {
    String str1 = dateTime().toString();
    String str2 = str1.substring(0, str1.indexOf("T"));
    String str3 = str1.substring(getZoneStart(str1));
    return str2 + str3;
  }
  
  public static String time(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    String str2 = arrayOfString1[2];
    if (str1 == null || str2 == null)
      return ""; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss" };
    String str3 = "HH:mm:ss";
    Date date = testFormats(str1, arrayOfString2);
    if (date == null)
      return ""; 
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str3);
    String str4 = simpleDateFormat.format(date);
    return str4 + str2;
  }
  
  public static String time() {
    String str = dateTime().toString();
    return str.substring(str.indexOf("T") + 1);
  }
  
  public static double year(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    boolean bool = (arrayOfString1[0].length() == 0) ? 1 : 0;
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "yyyy" };
    double d1 = getNumber(str, arrayOfString2, 1);
    return (bool || d1 == NaND) ? d1 : -d1;
  }
  
  public static double year() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(1);
  }
  
  public static double monthInYear(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--", "--MM-dd" };
    return getNumber(str, arrayOfString2, 2) + 1.0D;
  }
  
  public static double monthInYear() {
    Calendar calendar = Calendar.getInstance();
    return (calendar.get(2) + 1);
  }
  
  public static double weekInYear(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 3);
  }
  
  public static double weekInYear() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(3);
  }
  
  public static double dayInYear(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 6);
  }
  
  public static double dayInYear() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(6);
  }
  
  public static double dayInMonth(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "--MM-dd", "---dd" };
    return getNumber(str, arrayOfString2, 5);
  }
  
  public static double dayInMonth() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(5);
  }
  
  public static double dayOfWeekInMonth(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 8);
  }
  
  public static double dayOfWeekInMonth() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(8);
  }
  
  public static double dayInWeek(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    return getNumber(str, arrayOfString2, 7);
  }
  
  public static double dayInWeek() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(7);
  }
  
  public static double hourInDay(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
    return getNumber(str, arrayOfString2, 11);
  }
  
  public static double hourInDay() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(11);
  }
  
  public static double minuteInHour(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
    return getNumber(str, arrayOfString2, 12);
  }
  
  public static double minuteInHour() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(12);
  }
  
  public static double secondInMinute(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return NaND; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
    return getNumber(str, arrayOfString2, 13);
  }
  
  public static double secondInMinute() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(13);
  }
  
  public static XObject leapYear(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str = arrayOfString1[1];
    if (str == null)
      return new XNumber(NaND); 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "yyyy" };
    double d1 = getNumber(str, arrayOfString2, 1);
    if (d1 == NaND)
      return new XNumber(NaND); 
    int i = (int)d1;
    return new XBoolean((i % 400 == 0 || (i % 100 != 0 && i % 4 == 0)));
  }
  
  public static boolean leapYear() {
    Calendar calendar = Calendar.getInstance();
    int i = calendar.get(1);
    return (i % 400 == 0 || (i % 100 != 0 && i % 4 == 0));
  }
  
  public static String monthName(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null)
      return ""; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--" };
    String str2 = "MMMM";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String monthName() {
    Calendar calendar = Calendar.getInstance();
    String str = "MMMM";
    return getNameOrAbbrev(str);
  }
  
  public static String monthAbbreviation(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null)
      return ""; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--" };
    String str2 = "MMM";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String monthAbbreviation() {
    String str = "MMM";
    return getNameOrAbbrev(str);
  }
  
  public static String dayName(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null)
      return ""; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    String str2 = "EEEE";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String dayName() {
    String str = "EEEE";
    return getNameOrAbbrev(str);
  }
  
  public static String dayAbbreviation(String paramString) throws ParseException {
    String[] arrayOfString1 = getEraDatetimeZone(paramString);
    String str1 = arrayOfString1[1];
    if (str1 == null)
      return ""; 
    String[] arrayOfString2 = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
    String str2 = "EEE";
    return getNameOrAbbrev(paramString, arrayOfString2, str2);
  }
  
  public static String dayAbbreviation() {
    String str = "EEE";
    return getNameOrAbbrev(str);
  }
  
  private static String[] getEraDatetimeZone(String paramString) {
    String str1 = "";
    String str2 = paramString;
    String str3 = "";
    if (paramString.charAt(0) == '-' && !paramString.startsWith("--")) {
      str1 = "-";
      str2 = paramString.substring(1);
    } 
    int i = getZoneStart(str2);
    if (i > 0) {
      str3 = str2.substring(i);
      str2 = str2.substring(0, i);
    } else if (i == -2) {
      str3 = null;
    } 
    return new String[] { str1, str2, str3 };
  }
  
  private static int getZoneStart(String paramString) {
    if (paramString.indexOf("Z") == paramString.length() - 1)
      return paramString.length() - 1; 
    if (paramString.length() >= 6 && paramString.charAt(paramString.length() - 3) == ':' && (paramString.charAt(paramString.length() - 6) == '+' || paramString.charAt(paramString.length() - 6) == '-'))
      try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setLenient(false);
        Date date = simpleDateFormat.parse(paramString.substring(paramString.length() - 5));
        return paramString.length() - 6;
      } catch (ParseException parseException) {
        System.out.println("ParseException " + parseException.getErrorOffset());
        return -2;
      }  
    return -1;
  }
  
  private static Date testFormats(String paramString, String[] paramArrayOfString) throws ParseException {
    byte b = 0;
    while (b < paramArrayOfString.length) {
      try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramArrayOfString[b]);
        simpleDateFormat.setLenient(false);
        return simpleDateFormat.parse(paramString);
      } catch (ParseException parseException) {
        b++;
      } 
    } 
    return null;
  }
  
  private static double getNumber(String paramString, String[] paramArrayOfString, int paramInt) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    calendar.setLenient(false);
    Date date = testFormats(paramString, paramArrayOfString);
    if (date == null)
      return NaND; 
    calendar.setTime(date);
    return calendar.get(paramInt);
  }
  
  private static String getNameOrAbbrev(String paramString1, String[] paramArrayOfString, String paramString2) throws ParseException {
    byte b = 0;
    while (b < paramArrayOfString.length) {
      try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramArrayOfString[b], Locale.ENGLISH);
        simpleDateFormat.setLenient(false);
        Date date = simpleDateFormat.parse(paramString1);
        simpleDateFormat.applyPattern(paramString2);
        return simpleDateFormat.format(date);
      } catch (ParseException parseException) {
        b++;
      } 
    } 
    return "";
  }
  
  private static String getNameOrAbbrev(String paramString) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramString, Locale.ENGLISH);
    return simpleDateFormat.format(calendar.getTime());
  }
  
  public static String formatDate(String paramString1, String paramString2) {
    String str4;
    TimeZone timeZone;
    String str1 = "Gy";
    String str2 = "M";
    String str3 = "dDEFwW";
    if (paramString1.endsWith("Z") || paramString1.endsWith("z")) {
      timeZone = TimeZone.getTimeZone("GMT");
      paramString1 = paramString1.substring(0, paramString1.length() - 1) + "GMT";
      str4 = "z";
    } else if (paramString1.length() >= 6 && paramString1.charAt(paramString1.length() - 3) == ':' && (paramString1.charAt(paramString1.length() - 6) == '+' || paramString1.charAt(paramString1.length() - 6) == '-')) {
      String str = paramString1.substring(paramString1.length() - 6);
      if ("+00:00".equals(str) || "-00:00".equals(str)) {
        timeZone = TimeZone.getTimeZone("GMT");
      } else {
        timeZone = TimeZone.getTimeZone("GMT" + str);
      } 
      str4 = "z";
      paramString1 = paramString1.substring(0, paramString1.length() - 6) + "GMT" + str;
    } else {
      timeZone = TimeZone.getDefault();
      str4 = "";
    } 
    String[] arrayOfString = { "yyyy-MM-dd'T'HH:mm:ss" + str4, "yyyy-MM-dd", "yyyy-MM", "yyyy" };
    try {
      SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss" + str4);
      simpleDateFormat1.setLenient(false);
      Date date = simpleDateFormat1.parse(paramString1);
      SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(strip("GyMdDEFwW", paramString2));
      simpleDateFormat2.setTimeZone(timeZone);
      return simpleDateFormat2.format(date);
    } catch (ParseException parseException) {
      b = 0;
      while (b < arrayOfString.length) {
        try {
          SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(arrayOfString[b]);
          simpleDateFormat1.setLenient(false);
          Date date = simpleDateFormat1.parse(paramString1);
          SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(paramString2);
          simpleDateFormat2.setTimeZone(timeZone);
          return simpleDateFormat2.format(date);
        } catch (ParseException parseException1) {
          b++;
        } 
      } 
      try {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("--MM-dd");
        simpleDateFormat1.setLenient(false);
        Date date = simpleDateFormat1.parse(paramString1);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(strip("Gy", paramString2));
        simpleDateFormat2.setTimeZone(timeZone);
        return simpleDateFormat2.format(date);
      } catch (ParseException b) {
        try {
          SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("--MM--");
          simpleDateFormat1.setLenient(false);
          Date date = simpleDateFormat1.parse(paramString1);
          SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(strip("Gy", paramString2));
          simpleDateFormat2.setTimeZone(timeZone);
          return simpleDateFormat2.format(date);
        } catch (ParseException parseException1) {
          try {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("---dd");
            simpleDateFormat1.setLenient(false);
            Date date = simpleDateFormat1.parse(paramString1);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(strip("GyM", paramString2));
            simpleDateFormat2.setTimeZone(timeZone);
            return simpleDateFormat2.format(date);
          } catch (ParseException parseException1) {
            return "";
          } 
        } 
      } 
    } 
  }
  
  private static String strip(String paramString1, String paramString2) {
    boolean bool = false;
    int i = 0;
    StringBuffer stringBuffer = new StringBuffer(paramString2.length());
    while (i < paramString2.length()) {
      char c = paramString2.charAt(i);
      if (c == '\'') {
        int j = paramString2.indexOf('\'', i + 1);
        if (j == -1)
          j = paramString2.length(); 
        stringBuffer.append(paramString2.substring(i, j));
        i = j++;
        continue;
      } 
      if (paramString1.indexOf(c) > -1) {
        i++;
        continue;
      } 
      stringBuffer.append(c);
      i++;
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltDatetime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */