package java.time;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

public abstract class ZoneId implements Serializable {
  public static final Map<String, String> SHORT_IDS;
  
  private static final long serialVersionUID = 8352817235686L;
  
  public static ZoneId systemDefault() { return TimeZone.getDefault().toZoneId(); }
  
  public static Set<String> getAvailableZoneIds() { return ZoneRulesProvider.getAvailableZoneIds(); }
  
  public static ZoneId of(String paramString, Map<String, String> paramMap) {
    Objects.requireNonNull(paramString, "zoneId");
    Objects.requireNonNull(paramMap, "aliasMap");
    String str = (String)paramMap.get(paramString);
    str = (str != null) ? str : paramString;
    return of(str);
  }
  
  public static ZoneId of(String paramString) { return of(paramString, true); }
  
  public static ZoneId ofOffset(String paramString, ZoneOffset paramZoneOffset) {
    Objects.requireNonNull(paramString, "prefix");
    Objects.requireNonNull(paramZoneOffset, "offset");
    if (paramString.length() == 0)
      return paramZoneOffset; 
    if (!paramString.equals("GMT") && !paramString.equals("UTC") && !paramString.equals("UT"))
      throw new IllegalArgumentException("prefix should be GMT, UTC or UT, is: " + paramString); 
    if (paramZoneOffset.getTotalSeconds() != 0)
      paramString = paramString.concat(paramZoneOffset.getId()); 
    return new ZoneRegion(paramString, paramZoneOffset.getRules());
  }
  
  static ZoneId of(String paramString, boolean paramBoolean) {
    Objects.requireNonNull(paramString, "zoneId");
    return (paramString.length() <= 1 || paramString.startsWith("+") || paramString.startsWith("-")) ? ZoneOffset.of(paramString) : ((paramString.startsWith("UTC") || paramString.startsWith("GMT")) ? ofWithPrefix(paramString, 3, paramBoolean) : (paramString.startsWith("UT") ? ofWithPrefix(paramString, 2, paramBoolean) : ZoneRegion.ofId(paramString, paramBoolean)));
  }
  
  private static ZoneId ofWithPrefix(String paramString, int paramInt, boolean paramBoolean) {
    String str = paramString.substring(0, paramInt);
    if (paramString.length() == paramInt)
      return ofOffset(str, ZoneOffset.UTC); 
    if (paramString.charAt(paramInt) != '+' && paramString.charAt(paramInt) != '-')
      return ZoneRegion.ofId(paramString, paramBoolean); 
    try {
      ZoneOffset zoneOffset = ZoneOffset.of(paramString.substring(paramInt));
      return (zoneOffset == ZoneOffset.UTC) ? ofOffset(str, zoneOffset) : ofOffset(str, zoneOffset);
    } catch (DateTimeException dateTimeException) {
      throw new DateTimeException("Invalid ID for offset-based ZoneId: " + paramString, dateTimeException);
    } 
  }
  
  public static ZoneId from(TemporalAccessor paramTemporalAccessor) {
    ZoneId zoneId = (ZoneId)paramTemporalAccessor.query(TemporalQueries.zone());
    if (zoneId == null)
      throw new DateTimeException("Unable to obtain ZoneId from TemporalAccessor: " + paramTemporalAccessor + " of type " + paramTemporalAccessor.getClass().getName()); 
    return zoneId;
  }
  
  ZoneId() {
    if (getClass() != ZoneOffset.class && getClass() != ZoneRegion.class)
      throw new AssertionError("Invalid subclass"); 
  }
  
  public abstract String getId();
  
  public String getDisplayName(TextStyle paramTextStyle, Locale paramLocale) { return (new DateTimeFormatterBuilder()).appendZoneText(paramTextStyle).toFormatter(paramLocale).format(toTemporal()); }
  
  private TemporalAccessor toTemporal() { return new TemporalAccessor() {
        public boolean isSupported(TemporalField param1TemporalField) { return false; }
        
        public long getLong(TemporalField param1TemporalField) { throw new UnsupportedTemporalTypeException("Unsupported field: " + param1TemporalField); }
        
        public <R> R query(TemporalQuery<R> param1TemporalQuery) { return (param1TemporalQuery == TemporalQueries.zoneId()) ? (R)ZoneId.this : (R)super.query(param1TemporalQuery); }
      }; }
  
  public abstract ZoneRules getRules();
  
  public ZoneId normalized() {
    try {
      ZoneRules zoneRules = getRules();
      if (zoneRules.isFixedOffset())
        return zoneRules.getOffset(Instant.EPOCH); 
    } catch (ZoneRulesException zoneRulesException) {}
    return this;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ZoneId) {
      ZoneId zoneId = (ZoneId)paramObject;
      return getId().equals(zoneId.getId());
    } 
    return false;
  }
  
  public int hashCode() { return getId().hashCode(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  public String toString() { return getId(); }
  
  private Object writeReplace() { return new Ser((byte)7, this); }
  
  abstract void write(DataOutput paramDataOutput) throws IOException;
  
  static  {
    HashMap hashMap = new HashMap(64);
    hashMap.put("ACT", "Australia/Darwin");
    hashMap.put("AET", "Australia/Sydney");
    hashMap.put("AGT", "America/Argentina/Buenos_Aires");
    hashMap.put("ART", "Africa/Cairo");
    hashMap.put("AST", "America/Anchorage");
    hashMap.put("BET", "America/Sao_Paulo");
    hashMap.put("BST", "Asia/Dhaka");
    hashMap.put("CAT", "Africa/Harare");
    hashMap.put("CNT", "America/St_Johns");
    hashMap.put("CST", "America/Chicago");
    hashMap.put("CTT", "Asia/Shanghai");
    hashMap.put("EAT", "Africa/Addis_Ababa");
    hashMap.put("ECT", "Europe/Paris");
    hashMap.put("IET", "America/Indiana/Indianapolis");
    hashMap.put("IST", "Asia/Kolkata");
    hashMap.put("JST", "Asia/Tokyo");
    hashMap.put("MIT", "Pacific/Apia");
    hashMap.put("NET", "Asia/Yerevan");
    hashMap.put("NST", "Pacific/Auckland");
    hashMap.put("PLT", "Asia/Karachi");
    hashMap.put("PNT", "America/Phoenix");
    hashMap.put("PRT", "America/Puerto_Rico");
    hashMap.put("PST", "America/Los_Angeles");
    hashMap.put("SST", "Pacific/Guadalcanal");
    hashMap.put("VST", "Asia/Ho_Chi_Minh");
    hashMap.put("EST", "-05:00");
    hashMap.put("MST", "-07:00");
    hashMap.put("HST", "-10:00");
    SHORT_IDS = Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\ZoneId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */