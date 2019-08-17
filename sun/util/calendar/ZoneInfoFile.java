package sun.util.calendar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;
import sun.security.action.GetPropertyAction;

public final class ZoneInfoFile {
  private static String versionId;
  
  private static final Map<String, ZoneInfo> zones = new ConcurrentHashMap();
  
  private static Map<String, String> aliases = new HashMap();
  
  private static byte[][] ruleArray;
  
  private static String[] regions;
  
  private static int[] indices;
  
  private static final boolean USE_OLDMAPPING;
  
  private static String[][] oldMappings = { 
      { "ACT", "Australia/Darwin" }, { "AET", "Australia/Sydney" }, { "AGT", "America/Argentina/Buenos_Aires" }, { "ART", "Africa/Cairo" }, { "AST", "America/Anchorage" }, { "BET", "America/Sao_Paulo" }, { "BST", "Asia/Dhaka" }, { "CAT", "Africa/Harare" }, { "CNT", "America/St_Johns" }, { "CST", "America/Chicago" }, 
      { "CTT", "Asia/Shanghai" }, { "EAT", "Africa/Addis_Ababa" }, { "ECT", "Europe/Paris" }, { "IET", "America/Indiana/Indianapolis" }, { "IST", "Asia/Kolkata" }, { "JST", "Asia/Tokyo" }, { "MIT", "Pacific/Apia" }, { "NET", "Asia/Yerevan" }, { "NST", "Pacific/Auckland" }, { "PLT", "Asia/Karachi" }, 
      { "PNT", "America/Phoenix" }, { "PRT", "America/Puerto_Rico" }, { "PST", "America/Los_Angeles" }, { "SST", "Pacific/Guadalcanal" }, { "VST", "Asia/Ho_Chi_Minh" } };
  
  private static final long UTC1900 = -2208988800L;
  
  private static final long UTC2037 = 2145916799L;
  
  private static final long LDT2037 = 2114380800L;
  
  private static final long CURRT;
  
  static final int SECONDS_PER_DAY = 86400;
  
  static final int DAYS_PER_CYCLE = 146097;
  
  static final long DAYS_0000_TO_1970 = 719528L;
  
  private static final int[] toCalendarDOW;
  
  private static final int[] toSTZTime;
  
  private static final long OFFSET_MASK = 15L;
  
  private static final long DST_MASK = 240L;
  
  private static final int DST_NSHIFT = 4;
  
  private static final int TRANSITION_NSHIFT = 12;
  
  private static final int LASTYEAR = 2037;
  
  public static String[] getZoneIds() {
    int i = regions.length + oldMappings.length;
    if (!USE_OLDMAPPING)
      i += 3; 
    String[] arrayOfString = (String[])Arrays.copyOf(regions, i);
    int j = regions.length;
    if (!USE_OLDMAPPING) {
      arrayOfString[j++] = "EST";
      arrayOfString[j++] = "HST";
      arrayOfString[j++] = "MST";
    } 
    for (byte b = 0; b < oldMappings.length; b++)
      arrayOfString[j++] = oldMappings[b][0]; 
    return arrayOfString;
  }
  
  public static String[] getZoneIds(int paramInt) {
    ArrayList arrayList = new ArrayList();
    for (String str : getZoneIds()) {
      ZoneInfo zoneInfo = getZoneInfo(str);
      if (zoneInfo.getRawOffset() == paramInt)
        arrayList.add(str); 
    } 
    String[] arrayOfString = (String[])arrayList.toArray(new String[arrayList.size()]);
    Arrays.sort(arrayOfString);
    return arrayOfString;
  }
  
  public static ZoneInfo getZoneInfo(String paramString) {
    if (paramString == null)
      return null; 
    ZoneInfo zoneInfo = getZoneInfo0(paramString);
    if (zoneInfo != null) {
      zoneInfo = (ZoneInfo)zoneInfo.clone();
      zoneInfo.setID(paramString);
    } 
    return zoneInfo;
  }
  
  private static ZoneInfo getZoneInfo0(String paramString) {
    try {
      ZoneInfo zoneInfo = (ZoneInfo)zones.get(paramString);
      if (zoneInfo != null)
        return zoneInfo; 
      String str = paramString;
      if (aliases.containsKey(paramString))
        str = (String)aliases.get(paramString); 
      int i = Arrays.binarySearch(regions, str);
      if (i < 0)
        return null; 
      byte[] arrayOfByte = ruleArray[indices[i]];
      DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
      zoneInfo = getZoneInfo(dataInputStream, str);
      zones.put(paramString, zoneInfo);
      return zoneInfo;
    } catch (Exception exception) {
      throw new RuntimeException("Invalid binary time-zone data: TZDB:" + paramString + ", version: " + versionId, exception);
    } 
  }
  
  public static Map<String, String> getAliasMap() { return Collections.unmodifiableMap(aliases); }
  
  public static String getVersion() { return versionId; }
  
  public static ZoneInfo getCustomTimeZone(String paramString, int paramInt) {
    String str = toCustomID(paramInt);
    return new ZoneInfo(str, paramInt);
  }
  
  public static String toCustomID(int paramInt) {
    byte b;
    int i = paramInt / 60000;
    if (i >= 0) {
      b = 43;
    } else {
      b = 45;
      i = -i;
    } 
    int j = i / 60;
    int k = i % 60;
    char[] arrayOfChar = { 'G', 'M', 'T', b, '0', '0', ':', '0', '0' };
    if (j >= 10)
      arrayOfChar[4] = (char)(arrayOfChar[4] + j / 10); 
    arrayOfChar[5] = (char)(arrayOfChar[5] + j % 10);
    if (k != 0) {
      arrayOfChar[7] = (char)(arrayOfChar[7] + k / 10);
      arrayOfChar[8] = (char)(arrayOfChar[8] + k % 10);
    } 
    return new String(arrayOfChar);
  }
  
  private static void addOldMapping() {
    for (String[] arrayOfString : oldMappings)
      aliases.put(arrayOfString[0], arrayOfString[1]); 
    if (USE_OLDMAPPING) {
      aliases.put("EST", "America/New_York");
      aliases.put("MST", "America/Denver");
      aliases.put("HST", "Pacific/Honolulu");
    } else {
      zones.put("EST", new ZoneInfo("EST", -18000000));
      zones.put("MST", new ZoneInfo("MST", -25200000));
      zones.put("HST", new ZoneInfo("HST", -36000000));
    } 
  }
  
  public static boolean useOldMapping() { return USE_OLDMAPPING; }
  
  private static void load(DataInputStream paramDataInputStream) throws ClassNotFoundException, IOException {
    if (paramDataInputStream.readByte() != 1)
      throw new StreamCorruptedException("File format not recognised"); 
    String str = paramDataInputStream.readUTF();
    if (!"TZDB".equals(str))
      throw new StreamCorruptedException("File format not recognised"); 
    short s = paramDataInputStream.readShort();
    short s1;
    for (s1 = 0; s1 < s; s1++)
      versionId = paramDataInputStream.readUTF(); 
    s1 = paramDataInputStream.readShort();
    String[] arrayOfString = new String[s1];
    short s2;
    for (s2 = 0; s2 < s1; s2++)
      arrayOfString[s2] = paramDataInputStream.readUTF(); 
    s2 = paramDataInputStream.readShort();
    ruleArray = new byte[s2][];
    byte b;
    for (b = 0; b < s2; b++) {
      byte[] arrayOfByte = new byte[paramDataInputStream.readShort()];
      paramDataInputStream.readFully(arrayOfByte);
      ruleArray[b] = arrayOfByte;
    } 
    for (b = 0; b < s; b++) {
      s1 = paramDataInputStream.readShort();
      regions = new String[s1];
      indices = new int[s1];
      for (byte b1 = 0; b1 < s1; b1++) {
        regions[b1] = arrayOfString[paramDataInputStream.readShort()];
        indices[b1] = paramDataInputStream.readShort();
      } 
    } 
    zones.remove("ROC");
    for (b = 0; b < s; b++) {
      short s3 = paramDataInputStream.readShort();
      aliases.clear();
      for (byte b1 = 0; b1 < s3; b1++) {
        String str1 = arrayOfString[paramDataInputStream.readShort()];
        String str2 = arrayOfString[paramDataInputStream.readShort()];
        aliases.put(str1, str2);
      } 
    } 
    addOldMapping();
  }
  
  public static ZoneInfo getZoneInfo(DataInput paramDataInput, String paramString) throws Exception {
    byte b = paramDataInput.readByte();
    int i = paramDataInput.readInt();
    long[] arrayOfLong1 = new long[i];
    for (byte b1 = 0; b1 < i; b1++)
      arrayOfLong1[b1] = readEpochSec(paramDataInput); 
    int[] arrayOfInt1 = new int[i + 1];
    int j;
    for (j = 0; j < arrayOfInt1.length; j++)
      arrayOfInt1[j] = readOffset(paramDataInput); 
    j = paramDataInput.readInt();
    long[] arrayOfLong2 = new long[j];
    for (byte b2 = 0; b2 < j; b2++)
      arrayOfLong2[b2] = readEpochSec(paramDataInput); 
    int[] arrayOfInt2 = new int[j + 1];
    byte b3;
    for (b3 = 0; b3 < arrayOfInt2.length; b3++)
      arrayOfInt2[b3] = readOffset(paramDataInput); 
    b3 = paramDataInput.readByte();
    ZoneOffsetTransitionRule[] arrayOfZoneOffsetTransitionRule = new ZoneOffsetTransitionRule[b3];
    for (byte b4 = 0; b4 < b3; b4++)
      arrayOfZoneOffsetTransitionRule[b4] = new ZoneOffsetTransitionRule(paramDataInput); 
    return getZoneInfo(paramString, arrayOfLong1, arrayOfInt1, arrayOfLong2, arrayOfInt2, arrayOfZoneOffsetTransitionRule);
  }
  
  public static int readOffset(DataInput paramDataInput) throws IOException {
    byte b = paramDataInput.readByte();
    return (b == Byte.MAX_VALUE) ? paramDataInput.readInt() : (b * 900);
  }
  
  static long readEpochSec(DataInput paramDataInput) throws IOException {
    byte b1 = paramDataInput.readByte() & 0xFF;
    if (b1 == 255)
      return paramDataInput.readLong(); 
    byte b2 = paramDataInput.readByte() & 0xFF;
    byte b3 = paramDataInput.readByte() & 0xFF;
    long l = ((b1 << 16) + (b2 << 8) + b3);
    return l * 900L - 4575744000L;
  }
  
  private static ZoneInfo getZoneInfo(String paramString, long[] paramArrayOfLong1, int[] paramArrayOfInt1, long[] paramArrayOfLong2, int[] paramArrayOfInt2, ZoneOffsetTransitionRule[] paramArrayOfZoneOffsetTransitionRule) {
    int i = 0;
    int j = 0;
    int k = 0;
    int[] arrayOfInt1 = null;
    boolean bool = false;
    if (paramArrayOfLong1.length > 0) {
      i = paramArrayOfInt1[paramArrayOfInt1.length - 1] * 1000;
      bool = (paramArrayOfLong1[paramArrayOfLong1.length - 1] > CURRT);
    } else {
      i = paramArrayOfInt1[0] * 1000;
    } 
    long[] arrayOfLong = null;
    int[] arrayOfInt2 = null;
    int m = 0;
    byte b = 0;
    if (paramArrayOfLong2.length != 0) {
      arrayOfLong = new long[250];
      arrayOfInt2 = new int[100];
      int n = getYear(paramArrayOfLong2[paramArrayOfLong2.length - 1], paramArrayOfInt2[paramArrayOfLong2.length - 1]);
      byte b1 = 0;
      byte b2 = 1;
      while (b1 < paramArrayOfLong2.length && paramArrayOfLong2[b1] < -2208988800L)
        b1++; 
      if (b1 < paramArrayOfLong2.length) {
        if (b1 < paramArrayOfLong2.length) {
          arrayOfInt2[0] = paramArrayOfInt1[paramArrayOfInt1.length - 1] * 1000;
          m = 1;
        } 
        m = addTrans(arrayOfLong, b++, arrayOfInt2, m, -2208988800L, paramArrayOfInt2[b1], getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, -2208988800L));
      } 
      while (b1 < paramArrayOfLong2.length) {
        long l = paramArrayOfLong2[b1];
        if (l > 2145916799L) {
          n = 2037;
          break;
        } 
        while (b2 < paramArrayOfLong1.length) {
          long l1 = paramArrayOfLong1[b2];
          if (l1 >= -2208988800L) {
            if (l1 > l)
              break; 
            if (l1 < l) {
              if (m + 2 >= arrayOfInt2.length)
                arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100); 
              if (b + 1 >= arrayOfLong.length)
                arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100); 
              m = addTrans(arrayOfLong, b++, arrayOfInt2, m, l1, paramArrayOfInt2[b1], paramArrayOfInt1[b2 + true]);
            } 
          } 
          b2++;
        } 
        if (m + 2 >= arrayOfInt2.length)
          arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100); 
        if (b + 1 >= arrayOfLong.length)
          arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100); 
        m = addTrans(arrayOfLong, b++, arrayOfInt2, m, l, paramArrayOfInt2[b1 + 1], getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l));
        b1++;
      } 
      while (b2 < paramArrayOfLong1.length) {
        long l = paramArrayOfLong1[b2];
        if (l >= -2208988800L) {
          int i1 = paramArrayOfInt2[b1];
          int i2 = indexOf(arrayOfInt2, 0, m, i1);
          if (i2 == m)
            m++; 
          arrayOfLong[b++] = l * 1000L << 12 | i2 & 0xFL;
        } 
        b2++;
      } 
      if (paramArrayOfZoneOffsetTransitionRule.length > 1) {
        while (n++ < 2037) {
          for (ZoneOffsetTransitionRule zoneOffsetTransitionRule : paramArrayOfZoneOffsetTransitionRule) {
            long l = zoneOffsetTransitionRule.getTransitionEpochSecond(n);
            if (m + 2 >= arrayOfInt2.length)
              arrayOfInt2 = Arrays.copyOf(arrayOfInt2, arrayOfInt2.length + 100); 
            if (b + 1 >= arrayOfLong.length)
              arrayOfLong = Arrays.copyOf(arrayOfLong, arrayOfLong.length + 100); 
            m = addTrans(arrayOfLong, b++, arrayOfInt2, m, l, zoneOffsetTransitionRule.offsetAfter, zoneOffsetTransitionRule.standardOffset);
          } 
        } 
        ZoneOffsetTransitionRule zoneOffsetTransitionRule1 = paramArrayOfZoneOffsetTransitionRule[paramArrayOfZoneOffsetTransitionRule.length - 2];
        ZoneOffsetTransitionRule zoneOffsetTransitionRule2 = paramArrayOfZoneOffsetTransitionRule[paramArrayOfZoneOffsetTransitionRule.length - 1];
        arrayOfInt1 = new int[10];
        if (zoneOffsetTransitionRule1.offsetAfter - zoneOffsetTransitionRule1.offsetBefore < 0 && zoneOffsetTransitionRule2.offsetAfter - zoneOffsetTransitionRule2.offsetBefore > 0) {
          ZoneOffsetTransitionRule zoneOffsetTransitionRule = zoneOffsetTransitionRule1;
          zoneOffsetTransitionRule1 = zoneOffsetTransitionRule2;
          zoneOffsetTransitionRule2 = zoneOffsetTransitionRule;
        } 
        arrayOfInt1[0] = zoneOffsetTransitionRule1.month - 1;
        byte b3 = zoneOffsetTransitionRule1.dom;
        int i1 = zoneOffsetTransitionRule1.dow;
        if (i1 == -1) {
          arrayOfInt1[1] = b3;
          arrayOfInt1[2] = 0;
        } else if (b3 < 0 || b3 >= 24) {
          arrayOfInt1[1] = -1;
          arrayOfInt1[2] = toCalendarDOW[i1];
        } else {
          arrayOfInt1[1] = b3;
          arrayOfInt1[2] = -toCalendarDOW[i1];
        } 
        arrayOfInt1[3] = zoneOffsetTransitionRule1.secondOfDay * 1000;
        arrayOfInt1[4] = toSTZTime[zoneOffsetTransitionRule1.timeDefinition];
        arrayOfInt1[5] = zoneOffsetTransitionRule2.month - 1;
        b3 = zoneOffsetTransitionRule2.dom;
        i1 = zoneOffsetTransitionRule2.dow;
        if (i1 == -1) {
          arrayOfInt1[6] = b3;
          arrayOfInt1[7] = 0;
        } else if (b3 < 0 || b3 >= 24) {
          arrayOfInt1[6] = -1;
          arrayOfInt1[7] = toCalendarDOW[i1];
        } else {
          arrayOfInt1[6] = b3;
          arrayOfInt1[7] = -toCalendarDOW[i1];
        } 
        arrayOfInt1[8] = zoneOffsetTransitionRule2.secondOfDay * 1000;
        arrayOfInt1[9] = toSTZTime[zoneOffsetTransitionRule2.timeDefinition];
        j = (zoneOffsetTransitionRule1.offsetAfter - zoneOffsetTransitionRule1.offsetBefore) * 1000;
        if (arrayOfInt1[2] == 6 && arrayOfInt1[3] == 0 && (paramString.equals("Asia/Amman") || paramString.equals("Asia/Gaza") || paramString.equals("Asia/Hebron"))) {
          arrayOfInt1[2] = 5;
          arrayOfInt1[3] = 86400000;
        } 
        if (arrayOfInt1[2] == 7 && arrayOfInt1[3] == 0 && (paramString.equals("Asia/Amman") || paramString.equals("Asia/Gaza") || paramString.equals("Asia/Hebron"))) {
          arrayOfInt1[2] = 6;
          arrayOfInt1[3] = 86400000;
        } 
        if (arrayOfInt1[7] == 6 && arrayOfInt1[8] == 0 && paramString.equals("Africa/Cairo")) {
          arrayOfInt1[7] = 5;
          arrayOfInt1[8] = 86400000;
        } 
      } else if (b > 0) {
        if (n < 2037) {
          long l = 2114380800L - (i / 1000);
          int i1 = indexOf(arrayOfInt2, 0, m, i / 1000);
          if (i1 == m)
            m++; 
          arrayOfLong[b++] = l * 1000L << 12 | i1 & 0xFL;
        } else if (paramArrayOfLong2.length > 2) {
          int i1 = paramArrayOfLong2.length;
          long l1 = paramArrayOfLong2[i1 - 2];
          int i2 = paramArrayOfInt2[i1 - 2 + 1];
          int i3 = getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l1);
          long l2 = paramArrayOfLong2[i1 - 1];
          int i4 = paramArrayOfInt2[i1 - 1 + 1];
          int i5 = getStandardOffset(paramArrayOfLong1, paramArrayOfInt1, l2);
          if (i2 > i3 && i4 == i5) {
            LocalDateTime localDateTime3;
            LocalDateTime localDateTime2;
            i1 = paramArrayOfLong2.length - 2;
            ZoneOffset zoneOffset1;
            ZoneOffset zoneOffset2 = (zoneOffset1 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[i1])).ofTotalSeconds(paramArrayOfInt2[i1 + 1]);
            LocalDateTime localDateTime1 = LocalDateTime.ofEpochSecond(paramArrayOfLong2[i1], 0, zoneOffset1);
            if (zoneOffset2.getTotalSeconds() > zoneOffset1.getTotalSeconds()) {
              localDateTime2 = localDateTime1;
            } else {
              localDateTime2 = localDateTime1.plusSeconds((paramArrayOfInt2[i1 + 1] - paramArrayOfInt2[i1]));
            } 
            i1 = paramArrayOfLong2.length - 1;
            zoneOffset2 = (zoneOffset1 = ZoneOffset.ofTotalSeconds(paramArrayOfInt2[i1])).ofTotalSeconds(paramArrayOfInt2[i1 + 1]);
            localDateTime1 = LocalDateTime.ofEpochSecond(paramArrayOfLong2[i1], 0, zoneOffset1);
            if (zoneOffset2.getTotalSeconds() > zoneOffset1.getTotalSeconds()) {
              localDateTime3 = localDateTime1.plusSeconds((paramArrayOfInt2[i1 + 1] - paramArrayOfInt2[i1]));
            } else {
              localDateTime3 = localDateTime1;
            } 
            arrayOfInt1 = new int[10];
            arrayOfInt1[0] = localDateTime2.getMonthValue() - 1;
            arrayOfInt1[1] = localDateTime2.getDayOfMonth();
            arrayOfInt1[2] = 0;
            arrayOfInt1[3] = localDateTime2.toLocalTime().toSecondOfDay() * 1000;
            arrayOfInt1[4] = 0;
            arrayOfInt1[5] = localDateTime3.getMonthValue() - 1;
            arrayOfInt1[6] = localDateTime3.getDayOfMonth();
            arrayOfInt1[7] = 0;
            arrayOfInt1[8] = localDateTime3.toLocalTime().toSecondOfDay() * 1000;
            arrayOfInt1[9] = 0;
            j = (i2 - i3) * 1000;
          } 
        } 
      } 
      if (arrayOfLong != null && arrayOfLong.length != b)
        if (b == 0) {
          arrayOfLong = null;
        } else {
          arrayOfLong = Arrays.copyOf(arrayOfLong, b);
        }  
      if (arrayOfInt2 != null && arrayOfInt2.length != m)
        if (m == 0) {
          arrayOfInt2 = null;
        } else {
          arrayOfInt2 = Arrays.copyOf(arrayOfInt2, m);
        }  
      if (arrayOfLong != null) {
        Checksum checksum = new Checksum(null);
        for (b1 = 0; b1 < arrayOfLong.length; b1++) {
          long l1 = arrayOfLong[b1];
          int i1 = (int)(l1 >>> 4 & 0xFL);
          boolean bool1 = (i1 == 0) ? 0 : arrayOfInt2[i1];
          int i2 = (int)(l1 & 0xFL);
          int i3 = arrayOfInt2[i2];
          long l2 = l1 >> 12;
          checksum.update(l2 + i2);
          checksum.update(i2);
          checksum.update((i1 == 0) ? -1 : i1);
        } 
        k = (int)checksum.getValue();
      } 
    } 
    return new ZoneInfo(paramString, i, j, k, arrayOfLong, arrayOfInt2, arrayOfInt1, bool);
  }
  
  private static int getStandardOffset(long[] paramArrayOfLong, int[] paramArrayOfInt, long paramLong) {
    byte b;
    for (b = 0; b < paramArrayOfLong.length && paramLong >= paramArrayOfLong[b]; b++);
    return paramArrayOfInt[b];
  }
  
  private static int getYear(long paramLong, int paramInt) {
    long l1 = paramLong + paramInt;
    long l2 = Math.floorDiv(l1, 86400L);
    long l3 = l2 + 719528L;
    l3 -= 60L;
    long l4 = 0L;
    if (l3 < 0L) {
      long l = (l3 + 1L) / 146097L - 1L;
      l4 = l * 400L;
      l3 += -l * 146097L;
    } 
    long l5 = (400L * l3 + 591L) / 146097L;
    long l6 = l3 - 365L * l5 + l5 / 4L - l5 / 100L + l5 / 400L;
    if (l6 < 0L) {
      l5--;
      l6 = l3 - 365L * l5 + l5 / 4L - l5 / 100L + l5 / 400L;
    } 
    l5 += l4;
    int i = (int)l6;
    int j = (i * 5 + 2) / 153;
    int k = (j + 2) % 12 + 1;
    int m = i - (j * 306 + 5) / 10 + 1;
    l5 += (j / 10);
    return (int)l5;
  }
  
  private static int indexOf(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    paramInt3 *= 1000;
    while (paramInt1 < paramInt2) {
      if (paramArrayOfInt[paramInt1] == paramInt3)
        return paramInt1; 
      paramInt1++;
    } 
    paramArrayOfInt[paramInt1] = paramInt3;
    return paramInt1;
  }
  
  private static int addTrans(long[] paramArrayOfLong, int paramInt1, int[] paramArrayOfInt, int paramInt2, long paramLong, int paramInt3, int paramInt4) {
    int i = indexOf(paramArrayOfInt, 0, paramInt2, paramInt3);
    if (i == paramInt2)
      paramInt2++; 
    int j = 0;
    if (paramInt3 != paramInt4) {
      j = indexOf(paramArrayOfInt, 1, paramInt2, paramInt3 - paramInt4);
      if (j == paramInt2)
        paramInt2++; 
    } 
    paramArrayOfLong[paramInt1] = paramLong * 1000L << 12 | (j << 4) & 0xF0L | i & 0xFL;
    return paramInt2;
  }
  
  static  {
    String str = ((String)AccessController.doPrivileged(new GetPropertyAction("sun.timezone.ids.oldmapping", "false"))).toLowerCase(Locale.ROOT);
    USE_OLDMAPPING = (str.equals("yes") || str.equals("true"));
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            try {
              String str = System.getProperty("java.home") + File.separator + "lib";
              try (DataInputStream null = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(str, "tzdb.dat"))))) {
                ZoneInfoFile.load(dataInputStream);
              } 
            } catch (Exception exception) {
              throw new Error(exception);
            } 
            return null;
          }
        });
    CURRT = System.currentTimeMillis() / 1000L;
    toCalendarDOW = new int[] { -1, 2, 3, 4, 5, 6, 7, 1 };
    toSTZTime = new int[] { 2, 0, 1 };
  }
  
  private static class Checksum extends CRC32 {
    private Checksum() {}
    
    public void update(int param1Int) {
      byte[] arrayOfByte = new byte[4];
      arrayOfByte[0] = (byte)(param1Int >>> 24);
      arrayOfByte[1] = (byte)(param1Int >>> 16);
      arrayOfByte[2] = (byte)(param1Int >>> 8);
      arrayOfByte[3] = (byte)param1Int;
      update(arrayOfByte);
    }
    
    void update(long param1Long) {
      byte[] arrayOfByte = new byte[8];
      arrayOfByte[0] = (byte)(int)(param1Long >>> 56);
      arrayOfByte[1] = (byte)(int)(param1Long >>> 48);
      arrayOfByte[2] = (byte)(int)(param1Long >>> 40);
      arrayOfByte[3] = (byte)(int)(param1Long >>> 32);
      arrayOfByte[4] = (byte)(int)(param1Long >>> 24);
      arrayOfByte[5] = (byte)(int)(param1Long >>> 16);
      arrayOfByte[6] = (byte)(int)(param1Long >>> 8);
      arrayOfByte[7] = (byte)(int)param1Long;
      update(arrayOfByte);
    }
  }
  
  private static class ZoneOffsetTransitionRule {
    private final int month;
    
    private final byte dom;
    
    private final int dow;
    
    private final int secondOfDay;
    
    private final boolean timeEndOfDay;
    
    private final int timeDefinition;
    
    private final int standardOffset;
    
    private final int offsetBefore;
    
    private final int offsetAfter;
    
    ZoneOffsetTransitionRule(DataInput param1DataInput) throws IOException {
      int i = param1DataInput.readInt();
      int j = (i & 0x380000) >>> 19;
      int k = (i & 0x7C000) >>> 14;
      int m = (i & 0xFF0) >>> 4;
      int n = (i & 0xC) >>> 2;
      int i1 = i & 0x3;
      this.month = i >>> 28;
      this.dom = (byte)(((i & 0xFC00000) >>> 22) - 32);
      this.dow = (j == 0) ? -1 : j;
      this.secondOfDay = (k == 31) ? param1DataInput.readInt() : (k * 3600);
      this.timeEndOfDay = (k == 24);
      this.timeDefinition = (i & 0x3000) >>> 12;
      this.standardOffset = (m == 255) ? param1DataInput.readInt() : ((m - 128) * 900);
      this.offsetBefore = (n == 3) ? param1DataInput.readInt() : (this.standardOffset + n * 1800);
      this.offsetAfter = (i1 == 3) ? param1DataInput.readInt() : (this.standardOffset + i1 * 1800);
    }
    
    long getTransitionEpochSecond(int param1Int) {
      long l = 0L;
      if (this.dom < 0) {
        l = toEpochDay(param1Int, this.month, lengthOfMonth(param1Int, this.month) + 1 + this.dom);
        if (this.dow != -1)
          l = previousOrSame(l, this.dow); 
      } else {
        l = toEpochDay(param1Int, this.month, this.dom);
        if (this.dow != -1)
          l = nextOrSame(l, this.dow); 
      } 
      if (this.timeEndOfDay)
        l++; 
      int i = 0;
      switch (this.timeDefinition) {
        case 0:
          i = 0;
          break;
        case 1:
          i = -this.offsetBefore;
          break;
        case 2:
          i = -this.standardOffset;
          break;
      } 
      return l * 86400L + this.secondOfDay + i;
    }
    
    static final boolean isLeapYear(int param1Int) { return ((param1Int & 0x3) == 0 && (param1Int % 100 != 0 || param1Int % 400 == 0)); }
    
    static final int lengthOfMonth(int param1Int1, int param1Int2) {
      switch (param1Int2) {
        case 2:
          return isLeapYear(param1Int1) ? 29 : 28;
        case 4:
        case 6:
        case 9:
        case 11:
          return 30;
      } 
      return 31;
    }
    
    static final long toEpochDay(int param1Int1, int param1Int2, int param1Int3) {
      long l1 = param1Int1;
      long l2 = param1Int2;
      long l3 = 0L;
      l3 += 365L * l1;
      if (l1 >= 0L) {
        l3 += (l1 + 3L) / 4L - (l1 + 99L) / 100L + (l1 + 399L) / 400L;
      } else {
        l3 -= l1 / -4L - l1 / -100L + l1 / -400L;
      } 
      l3 += (367L * l2 - 362L) / 12L;
      l3 += (param1Int3 - 1);
      if (l2 > 2L) {
        l3--;
        if (!isLeapYear(param1Int1))
          l3--; 
      } 
      return l3 - 719528L;
    }
    
    static final long previousOrSame(long param1Long, int param1Int) { return adjust(param1Long, param1Int, 1); }
    
    static final long nextOrSame(long param1Long, int param1Int) { return adjust(param1Long, param1Int, 0); }
    
    static final long adjust(long param1Long, int param1Int1, int param1Int2) {
      int i = (int)Math.floorMod(param1Long + 3L, 7L) + 1;
      if (param1Int2 < 2 && i == param1Int1)
        return param1Long; 
      if ((param1Int2 & true) == 0) {
        int k = i - param1Int1;
        return param1Long + ((k >= 0) ? (7 - k) : -k);
      } 
      int j = param1Int1 - i;
      return param1Long - ((j >= 0) ? (7 - j) : -j);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\calendar\ZoneInfoFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */