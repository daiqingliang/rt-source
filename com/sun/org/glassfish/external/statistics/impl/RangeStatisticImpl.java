package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.RangeStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class RangeStatisticImpl extends StatisticImpl implements RangeStatistic, InvocationHandler {
  private long currentVal = 0L;
  
  private long highWaterMark = Float.MIN_VALUE;
  
  private long lowWaterMark = Float.MAX_VALUE;
  
  private final long initCurrentVal;
  
  private final long initHighWaterMark;
  
  private final long initLowWaterMark;
  
  private final RangeStatistic rs = (RangeStatistic)Proxy.newProxyInstance(RangeStatistic.class.getClassLoader(), new Class[] { RangeStatistic.class }, this);
  
  public RangeStatisticImpl(long paramLong1, long paramLong2, long paramLong3, String paramString1, String paramString2, String paramString3, long paramLong4, long paramLong5) {
    super(paramString1, paramString2, paramString3, paramLong4, paramLong5);
    this.currentVal = paramLong1;
    this.initCurrentVal = paramLong1;
    this.highWaterMark = paramLong2;
    this.initHighWaterMark = paramLong2;
    this.lowWaterMark = paramLong3;
    this.initLowWaterMark = paramLong3;
  }
  
  public RangeStatistic getStatistic() { return this.rs; }
  
  public Map getStaticAsMap() {
    Map map = super.getStaticAsMap();
    map.put("current", Long.valueOf(getCurrent()));
    map.put("lowwatermark", Long.valueOf(getLowWaterMark()));
    map.put("highwatermark", Long.valueOf(getHighWaterMark()));
    return map;
  }
  
  public long getCurrent() { return this.currentVal; }
  
  public void setCurrent(long paramLong) {
    this.currentVal = paramLong;
    this.lowWaterMark = (paramLong >= this.lowWaterMark) ? this.lowWaterMark : paramLong;
    this.highWaterMark = (paramLong >= this.highWaterMark) ? paramLong : this.highWaterMark;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public long getHighWaterMark() { return this.highWaterMark; }
  
  public void setHighWaterMark(long paramLong) { this.highWaterMark = paramLong; }
  
  public long getLowWaterMark() { return this.lowWaterMark; }
  
  public void setLowWaterMark(long paramLong) { this.lowWaterMark = paramLong; }
  
  public void reset() {
    super.reset();
    this.currentVal = this.initCurrentVal;
    this.highWaterMark = this.initHighWaterMark;
    this.lowWaterMark = this.initLowWaterMark;
    this.sampleTime = -1L;
  }
  
  public String toString() { return super.toString() + NEWLINE + "Current: " + getCurrent() + NEWLINE + "LowWaterMark: " + getLowWaterMark() + NEWLINE + "HighWaterMark: " + getHighWaterMark(); }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    Object object;
    checkMethod(paramMethod);
    try {
      object = paramMethod.invoke(this, paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      throw invocationTargetException.getTargetException();
    } catch (Exception exception) {
      throw new RuntimeException("unexpected invocation exception: " + exception.getMessage());
    } 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\RangeStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */