package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.Statistic;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StatisticImpl implements Statistic {
  private final String statisticName;
  
  private final String statisticUnit;
  
  private final String statisticDesc;
  
  protected long sampleTime = -1L;
  
  private long startTime;
  
  public static final String UNIT_COUNT = "count";
  
  public static final String UNIT_SECOND = "second";
  
  public static final String UNIT_MILLISECOND = "millisecond";
  
  public static final String UNIT_MICROSECOND = "microsecond";
  
  public static final String UNIT_NANOSECOND = "nanosecond";
  
  public static final String START_TIME = "starttime";
  
  public static final String LAST_SAMPLE_TIME = "lastsampletime";
  
  protected final Map<String, Object> statMap = new ConcurrentHashMap();
  
  protected static final String NEWLINE = System.getProperty("line.separator");
  
  protected StatisticImpl(String paramString1, String paramString2, String paramString3, long paramLong1, long paramLong2) {
    if (isValidString(paramString1)) {
      this.statisticName = paramString1;
    } else {
      this.statisticName = "name";
    } 
    if (isValidString(paramString2)) {
      this.statisticUnit = paramString2;
    } else {
      this.statisticUnit = "unit";
    } 
    if (isValidString(paramString3)) {
      this.statisticDesc = paramString3;
    } else {
      this.statisticDesc = "description";
    } 
    this.startTime = paramLong1;
    this.sampleTime = paramLong2;
  }
  
  protected StatisticImpl(String paramString1, String paramString2, String paramString3) { this(paramString1, paramString2, paramString3, System.currentTimeMillis(), System.currentTimeMillis()); }
  
  public Map getStaticAsMap() {
    if (isValidString(this.statisticName))
      this.statMap.put("name", this.statisticName); 
    if (isValidString(this.statisticUnit))
      this.statMap.put("unit", this.statisticUnit); 
    if (isValidString(this.statisticDesc))
      this.statMap.put("description", this.statisticDesc); 
    this.statMap.put("starttime", Long.valueOf(this.startTime));
    this.statMap.put("lastsampletime", Long.valueOf(this.sampleTime));
    return this.statMap;
  }
  
  public String getName() { return this.statisticName; }
  
  public String getDescription() { return this.statisticDesc; }
  
  public String getUnit() { return this.statisticUnit; }
  
  public long getLastSampleTime() { return this.sampleTime; }
  
  public long getStartTime() { return this.startTime; }
  
  public void reset() { this.startTime = System.currentTimeMillis(); }
  
  public String toString() { return "Statistic " + getClass().getName() + NEWLINE + "Name: " + getName() + NEWLINE + "Description: " + getDescription() + NEWLINE + "Unit: " + getUnit() + NEWLINE + "LastSampleTime: " + getLastSampleTime() + NEWLINE + "StartTime: " + getStartTime(); }
  
  protected static boolean isValidString(String paramString) { return (paramString != null && paramString.length() > 0); }
  
  protected void checkMethod(Method paramMethod) {
    if (paramMethod == null || paramMethod.getDeclaringClass() == null || !Statistic.class.isAssignableFrom(paramMethod.getDeclaringClass()) || Modifier.isStatic(paramMethod.getModifiers()))
      throw new RuntimeException("Invalid method on invoke"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\StatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */