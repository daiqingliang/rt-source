package sun.net.httpserver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

class ServerConfig {
  private static final int DEFAULT_CLOCK_TICK = 10000;
  
  private static final long DEFAULT_IDLE_INTERVAL = 30L;
  
  private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 200;
  
  private static final long DEFAULT_MAX_REQ_TIME = -1L;
  
  private static final long DEFAULT_MAX_RSP_TIME = -1L;
  
  private static final long DEFAULT_TIMER_MILLIS = 1000L;
  
  private static final int DEFAULT_MAX_REQ_HEADERS = 200;
  
  private static final long DEFAULT_DRAIN_AMOUNT = 65536L;
  
  private static int clockTick;
  
  private static long idleInterval;
  
  private static long drainAmount;
  
  private static int maxIdleConnections;
  
  private static int maxReqHeaders;
  
  private static long maxReqTime;
  
  private static long maxRspTime;
  
  private static long timerMillis;
  
  private static boolean debug;
  
  private static boolean noDelay;
  
  static void checkLegacyProperties(final Logger logger) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            if (System.getProperty("sun.net.httpserver.readTimeout") != null)
              logger.warning("sun.net.httpserver.readTimeout property is no longer used. Use sun.net.httpserver.maxReqTime instead."); 
            if (System.getProperty("sun.net.httpserver.writeTimeout") != null)
              logger.warning("sun.net.httpserver.writeTimeout property is no longer used. Use sun.net.httpserver.maxRspTime instead."); 
            if (System.getProperty("sun.net.httpserver.selCacheTimeout") != null)
              logger.warning("sun.net.httpserver.selCacheTimeout property is no longer used."); 
            return null;
          }
        }); }
  
  static boolean debugEnabled() { return debug; }
  
  static long getIdleInterval() { return idleInterval; }
  
  static int getClockTick() { return clockTick; }
  
  static int getMaxIdleConnections() { return maxIdleConnections; }
  
  static long getDrainAmount() { return drainAmount; }
  
  static int getMaxReqHeaders() { return maxReqHeaders; }
  
  static long getMaxReqTime() { return maxReqTime; }
  
  static long getMaxRspTime() { return maxRspTime; }
  
  static long getTimerMillis() { return timerMillis; }
  
  static boolean noDelay() { return noDelay; }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            idleInterval = Long.getLong("sun.net.httpserver.idleInterval", 30L).longValue() * 1000L;
            clockTick = Integer.getInteger("sun.net.httpserver.clockTick", 10000).intValue();
            maxIdleConnections = Integer.getInteger("sun.net.httpserver.maxIdleConnections", 200).intValue();
            drainAmount = Long.getLong("sun.net.httpserver.drainAmount", 65536L).longValue();
            maxReqHeaders = Integer.getInteger("sun.net.httpserver.maxReqHeaders", 200).intValue();
            maxReqTime = Long.getLong("sun.net.httpserver.maxReqTime", -1L).longValue();
            maxRspTime = Long.getLong("sun.net.httpserver.maxRspTime", -1L).longValue();
            timerMillis = Long.getLong("sun.net.httpserver.timerMillis", 1000L).longValue();
            debug = Boolean.getBoolean("sun.net.httpserver.debug");
            noDelay = Boolean.getBoolean("sun.net.httpserver.nodelay");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\ServerConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */