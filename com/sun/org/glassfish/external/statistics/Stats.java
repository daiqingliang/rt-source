package com.sun.org.glassfish.external.statistics;

public interface Stats {
  Statistic getStatistic(String paramString);
  
  String[] getStatisticNames();
  
  Statistic[] getStatistics();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\Stats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */