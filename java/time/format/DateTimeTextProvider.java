package java.time.format;

import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

class DateTimeTextProvider {
  private static final ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> CACHE = new ConcurrentHashMap(16, 0.75F, 2);
  
  private static final Comparator<Map.Entry<String, Long>> COMPARATOR = new Comparator<Map.Entry<String, Long>>() {
      public int compare(Map.Entry<String, Long> param1Entry1, Map.Entry<String, Long> param1Entry2) { return ((String)param1Entry2.getKey()).length() - ((String)param1Entry1.getKey()).length(); }
    };
  
  static DateTimeTextProvider getInstance() { return new DateTimeTextProvider(); }
  
  public String getText(TemporalField paramTemporalField, long paramLong, TextStyle paramTextStyle, Locale paramLocale) {
    Object object = findStore(paramTemporalField, paramLocale);
    return (object instanceof LocaleStore) ? ((LocaleStore)object).getText(paramLong, paramTextStyle) : null;
  }
  
  public String getText(Chronology paramChronology, TemporalField paramTemporalField, long paramLong, TextStyle paramTextStyle, Locale paramLocale) {
    int i;
    byte b;
    if (paramChronology == IsoChronology.INSTANCE || !(paramTemporalField instanceof ChronoField))
      return getText(paramTemporalField, paramLong, paramTextStyle, paramLocale); 
    if (paramTemporalField == ChronoField.ERA) {
      b = 0;
      if (paramChronology == JapaneseChronology.INSTANCE) {
        if (paramLong == -999L) {
          i = 0;
        } else {
          i = (int)paramLong + 2;
        } 
      } else {
        i = (int)paramLong;
      } 
    } else if (paramTemporalField == ChronoField.MONTH_OF_YEAR) {
      b = 2;
      i = (int)paramLong - 1;
    } else if (paramTemporalField == ChronoField.DAY_OF_WEEK) {
      b = 7;
      i = (int)paramLong + 1;
      if (i > 7)
        i = 1; 
    } else if (paramTemporalField == ChronoField.AMPM_OF_DAY) {
      b = 9;
      i = (int)paramLong;
    } else {
      return null;
    } 
    return CalendarDataUtility.retrieveJavaTimeFieldValueName(paramChronology.getCalendarType(), b, i, paramTextStyle.toCalendarStyle(), paramLocale);
  }
  
  public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField paramTemporalField, TextStyle paramTextStyle, Locale paramLocale) {
    Object object = findStore(paramTemporalField, paramLocale);
    return (object instanceof LocaleStore) ? ((LocaleStore)object).getTextIterator(paramTextStyle) : null;
  }
  
  public Iterator<Map.Entry<String, Long>> getTextIterator(Chronology paramChronology, TemporalField paramTemporalField, TextStyle paramTextStyle, Locale paramLocale) {
    byte b1;
    if (paramChronology == IsoChronology.INSTANCE || !(paramTemporalField instanceof ChronoField))
      return getTextIterator(paramTemporalField, paramTextStyle, paramLocale); 
    switch ((ChronoField)paramTemporalField) {
      case ERA:
        b1 = 0;
        break;
      case MONTH_OF_YEAR:
        b1 = 2;
        break;
      case DAY_OF_WEEK:
        b1 = 7;
        break;
      case AMPM_OF_DAY:
        b1 = 9;
        break;
      default:
        return null;
    } 
    byte b2 = (paramTextStyle == null) ? 0 : paramTextStyle.toCalendarStyle();
    Map map = CalendarDataUtility.retrieveJavaTimeFieldValueNames(paramChronology.getCalendarType(), b1, b2, paramLocale);
    if (map == null)
      return null; 
    ArrayList arrayList = new ArrayList(map.size());
    switch (b1) {
      case 0:
        for (Map.Entry entry : map.entrySet()) {
          int i = ((Integer)entry.getValue()).intValue();
          if (paramChronology == JapaneseChronology.INSTANCE)
            if (i == 0) {
              i = -999;
            } else {
              i -= 2;
            }  
          arrayList.add(createEntry(entry.getKey(), Long.valueOf(i)));
        } 
        return arrayList.iterator();
      case 2:
        for (Map.Entry entry : map.entrySet())
          arrayList.add(createEntry(entry.getKey(), Long.valueOf((((Integer)entry.getValue()).intValue() + 1)))); 
        return arrayList.iterator();
      case 7:
        for (Map.Entry entry : map.entrySet())
          arrayList.add(createEntry(entry.getKey(), Long.valueOf(toWeekDay(((Integer)entry.getValue()).intValue())))); 
        return arrayList.iterator();
    } 
    for (Map.Entry entry : map.entrySet())
      arrayList.add(createEntry(entry.getKey(), Long.valueOf(((Integer)entry.getValue()).intValue()))); 
    return arrayList.iterator();
  }
  
  private Object findStore(TemporalField paramTemporalField, Locale paramLocale) {
    Map.Entry entry = createEntry(paramTemporalField, paramLocale);
    Object object = CACHE.get(entry);
    if (object == null) {
      object = createStore(paramTemporalField, paramLocale);
      CACHE.putIfAbsent(entry, object);
      object = CACHE.get(entry);
    } 
    return object;
  }
  
  private static int toWeekDay(int paramInt) { return (paramInt == 1) ? 7 : (paramInt - 1); }
  
  private Object createStore(TemporalField paramTemporalField, Locale paramLocale) {
    HashMap hashMap = new HashMap();
    if (paramTemporalField == ChronoField.ERA) {
      for (TextStyle textStyle : TextStyle.values()) {
        if (!textStyle.isStandalone()) {
          Map map = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 0, textStyle.toCalendarStyle(), paramLocale);
          if (map != null) {
            HashMap hashMap1 = new HashMap();
            for (Map.Entry entry : map.entrySet())
              hashMap1.put(Long.valueOf(((Integer)entry.getValue()).intValue()), entry.getKey()); 
            if (!hashMap1.isEmpty())
              hashMap.put(textStyle, hashMap1); 
          } 
        } 
      } 
      return new LocaleStore(hashMap);
    } 
    if (paramTemporalField == ChronoField.MONTH_OF_YEAR) {
      for (TextStyle textStyle : TextStyle.values()) {
        Map map = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 2, textStyle.toCalendarStyle(), paramLocale);
        HashMap hashMap1 = new HashMap();
        if (map != null) {
          for (Map.Entry entry : map.entrySet())
            hashMap1.put(Long.valueOf((((Integer)entry.getValue()).intValue() + 1)), entry.getKey()); 
        } else {
          for (byte b = 0; b <= 11; b++) {
            String str = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 2, b, textStyle.toCalendarStyle(), paramLocale);
            if (str == null)
              break; 
            hashMap1.put(Long.valueOf((b + 1)), str);
          } 
        } 
        if (!hashMap1.isEmpty())
          hashMap.put(textStyle, hashMap1); 
      } 
      return new LocaleStore(hashMap);
    } 
    if (paramTemporalField == ChronoField.DAY_OF_WEEK) {
      for (TextStyle textStyle : TextStyle.values()) {
        Map map = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 7, textStyle.toCalendarStyle(), paramLocale);
        HashMap hashMap1 = new HashMap();
        if (map != null) {
          for (Map.Entry entry : map.entrySet())
            hashMap1.put(Long.valueOf(toWeekDay(((Integer)entry.getValue()).intValue())), entry.getKey()); 
        } else {
          for (byte b = 1; b <= 7; b++) {
            String str = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 7, b, textStyle.toCalendarStyle(), paramLocale);
            if (str == null)
              break; 
            hashMap1.put(Long.valueOf(toWeekDay(b)), str);
          } 
        } 
        if (!hashMap1.isEmpty())
          hashMap.put(textStyle, hashMap1); 
      } 
      return new LocaleStore(hashMap);
    } 
    if (paramTemporalField == ChronoField.AMPM_OF_DAY) {
      for (TextStyle textStyle : TextStyle.values()) {
        if (!textStyle.isStandalone()) {
          Map map = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 9, textStyle.toCalendarStyle(), paramLocale);
          if (map != null) {
            HashMap hashMap1 = new HashMap();
            for (Map.Entry entry : map.entrySet())
              hashMap1.put(Long.valueOf(((Integer)entry.getValue()).intValue()), entry.getKey()); 
            if (!hashMap1.isEmpty())
              hashMap.put(textStyle, hashMap1); 
          } 
        } 
      } 
      return new LocaleStore(hashMap);
    } 
    if (paramTemporalField == IsoFields.QUARTER_OF_YEAR) {
      String[] arrayOfString = { "QuarterNames", "standalone.QuarterNames", "QuarterAbbreviations", "standalone.QuarterAbbreviations", "QuarterNarrows", "standalone.QuarterNarrows" };
      for (byte b = 0; b < arrayOfString.length; b++) {
        String[] arrayOfString1 = (String[])getLocalizedResource(arrayOfString[b], paramLocale);
        if (arrayOfString1 != null) {
          HashMap hashMap1 = new HashMap();
          for (byte b1 = 0; b1 < arrayOfString1.length; b1++)
            hashMap1.put(Long.valueOf((b1 + true)), arrayOfString1[b1]); 
          hashMap.put(TextStyle.values()[b], hashMap1);
        } 
      } 
      return new LocaleStore(hashMap);
    } 
    return "";
  }
  
  private static <A, B> Map.Entry<A, B> createEntry(A paramA, B paramB) { return new AbstractMap.SimpleImmutableEntry(paramA, paramB); }
  
  static <T> T getLocalizedResource(String paramString, Locale paramLocale) {
    LocaleResources localeResources = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(paramLocale);
    ResourceBundle resourceBundle = localeResources.getJavaTimeFormatData();
    return (T)(resourceBundle.containsKey(paramString) ? resourceBundle.getObject(paramString) : null);
  }
  
  static final class LocaleStore {
    private final Map<TextStyle, Map<Long, String>> valueTextMap;
    
    private final Map<TextStyle, List<Map.Entry<String, Long>>> parsable;
    
    LocaleStore(Map<TextStyle, Map<Long, String>> param1Map) {
      this.valueTextMap = param1Map;
      HashMap hashMap = new HashMap();
      ArrayList arrayList = new ArrayList();
      for (Map.Entry entry : param1Map.entrySet()) {
        HashMap hashMap1 = new HashMap();
        for (Map.Entry entry1 : ((Map)entry.getValue()).entrySet()) {
          if (hashMap1.put(entry1.getValue(), DateTimeTextProvider.createEntry(entry1.getValue(), entry1.getKey())) != null);
        } 
        ArrayList arrayList1 = new ArrayList(hashMap1.values());
        Collections.sort(arrayList1, COMPARATOR);
        hashMap.put(entry.getKey(), arrayList1);
        arrayList.addAll(arrayList1);
        hashMap.put(null, arrayList);
      } 
      Collections.sort(arrayList, COMPARATOR);
      this.parsable = hashMap;
    }
    
    String getText(long param1Long, TextStyle param1TextStyle) {
      Map map = (Map)this.valueTextMap.get(param1TextStyle);
      return (map != null) ? (String)map.get(Long.valueOf(param1Long)) : null;
    }
    
    Iterator<Map.Entry<String, Long>> getTextIterator(TextStyle param1TextStyle) {
      List list = (List)this.parsable.get(param1TextStyle);
      return (list != null) ? list.iterator() : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\DateTimeTextProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */