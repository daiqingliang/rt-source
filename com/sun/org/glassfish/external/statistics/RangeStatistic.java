package com.sun.org.glassfish.external.statistics;

public interface RangeStatistic extends Statistic {
  long getHighWaterMark();
  
  long getLowWaterMark();
  
  long getCurrent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\RangeStatistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */