package com.sun.org.glassfish.external.statistics.impl;

import com.sun.org.glassfish.external.statistics.CountStatistic;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class CountStatisticImpl extends StatisticImpl implements CountStatistic, InvocationHandler {
  private long count = 0L;
  
  private final long initCount;
  
  private final CountStatistic cs = (CountStatistic)Proxy.newProxyInstance(CountStatistic.class.getClassLoader(), new Class[] { CountStatistic.class }, this);
  
  public CountStatisticImpl(long paramLong1, String paramString1, String paramString2, String paramString3, long paramLong2, long paramLong3) {
    super(paramString1, paramString2, paramString3, paramLong3, paramLong2);
    this.count = paramLong1;
    this.initCount = paramLong1;
  }
  
  public CountStatisticImpl(String paramString1, String paramString2, String paramString3) { this(0L, paramString1, paramString2, paramString3, -1L, System.currentTimeMillis()); }
  
  public CountStatistic getStatistic() { return this.cs; }
  
  public Map getStaticAsMap() {
    Map map = super.getStaticAsMap();
    map.put("count", Long.valueOf(getCount()));
    return map;
  }
  
  public String toString() { return super.toString() + NEWLINE + "Count: " + getCount(); }
  
  public long getCount() { return this.count; }
  
  public void setCount(long paramLong) {
    this.count = paramLong;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public void increment() {
    this.count++;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public void increment(long paramLong) {
    this.count += paramLong;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public void decrement() {
    this.count--;
    this.sampleTime = System.currentTimeMillis();
  }
  
  public void reset() {
    super.reset();
    this.count = this.initCount;
    this.sampleTime = -1L;
  }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\impl\CountStatisticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */