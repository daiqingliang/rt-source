package com.sun.corba.se.spi.monitoring;

public class StatisticMonitoredAttribute extends MonitoredAttributeBase {
  private StatisticsAccumulator statisticsAccumulator;
  
  private Object mutex;
  
  public StatisticMonitoredAttribute(String paramString1, String paramString2, StatisticsAccumulator paramStatisticsAccumulator, Object paramObject) {
    super(paramString1);
    MonitoredAttributeInfoFactory monitoredAttributeInfoFactory = MonitoringFactories.getMonitoredAttributeInfoFactory();
    MonitoredAttributeInfo monitoredAttributeInfo = monitoredAttributeInfoFactory.createMonitoredAttributeInfo(paramString2, String.class, false, true);
    setMonitoredAttributeInfo(monitoredAttributeInfo);
    this.statisticsAccumulator = paramStatisticsAccumulator;
    this.mutex = paramObject;
  }
  
  public Object getValue() {
    synchronized (this.mutex) {
      return this.statisticsAccumulator.getValue();
    } 
  }
  
  public void clearState() {
    synchronized (this.mutex) {
      this.statisticsAccumulator.clearState();
    } 
  }
  
  public StatisticsAccumulator getStatisticsAccumulator() { return this.statisticsAccumulator; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\monitoring\StatisticMonitoredAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */