package com.sun.corba.se.impl.activation;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

public class ProcessMonitorThread extends Thread {
  private HashMap serverTable;
  
  private int sleepTime;
  
  private static ProcessMonitorThread instance = null;
  
  private ProcessMonitorThread(HashMap paramHashMap, int paramInt) {
    this.serverTable = paramHashMap;
    this.sleepTime = paramInt;
  }
  
  public void run() {
    while (true) {
      Iterator iterator;
      try {
        Thread.sleep(this.sleepTime);
      } catch (InterruptedException null) {
        break;
      } 
      synchronized (this.serverTable) {
        iterator = this.serverTable.values().iterator();
      } 
      try {
        checkServerHealth(iterator);
      } catch (ConcurrentModificationException concurrentModificationException) {
        break;
      } 
    } 
  }
  
  private void checkServerHealth(Iterator paramIterator) {
    if (paramIterator == null)
      return; 
    while (paramIterator.hasNext()) {
      ServerTableEntry serverTableEntry = (ServerTableEntry)paramIterator.next();
      serverTableEntry.checkProcessHealth();
    } 
  }
  
  static void start(HashMap paramHashMap) {
    int i = 1000;
    String str = System.getProperties().getProperty("com.sun.CORBA.activation.ServerPollingTime");
    if (str != null)
      try {
        i = Integer.parseInt(str);
      } catch (Exception exception) {} 
    instance = new ProcessMonitorThread(paramHashMap, i);
    instance.setDaemon(true);
    instance.start();
  }
  
  static void interruptThread() { instance.interrupt(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ProcessMonitorThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */