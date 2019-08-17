package sun.management;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import sun.management.counter.Counter;
import sun.management.counter.Units;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;

public class ConnectorAddressLink {
  private static final String CONNECTOR_ADDRESS_COUNTER = "sun.management.JMXConnectorServer.address";
  
  private static final String REMOTE_CONNECTOR_COUNTER_PREFIX = "sun.management.JMXConnectorServer.";
  
  private static AtomicInteger counter = new AtomicInteger();
  
  public static void export(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("address not specified"); 
    Perf perf = Perf.getPerf();
    perf.createString("sun.management.JMXConnectorServer.address", 1, Units.STRING.intValue(), paramString);
  }
  
  public static String importFrom(int paramInt) throws IOException {
    ByteBuffer byteBuffer;
    Perf perf = Perf.getPerf();
    try {
      byteBuffer = perf.attach(paramInt, "r");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException(illegalArgumentException.getMessage());
    } 
    List list = (new PerfInstrumentation(byteBuffer)).findByPattern("sun.management.JMXConnectorServer.address");
    Iterator iterator = list.iterator();
    if (iterator.hasNext()) {
      Counter counter1 = (Counter)iterator.next();
      return (String)counter1.getValue();
    } 
    return null;
  }
  
  public static void exportRemote(Map<String, String> paramMap) {
    int i = counter.getAndIncrement();
    Perf perf = Perf.getPerf();
    for (Map.Entry entry : paramMap.entrySet())
      perf.createString("sun.management.JMXConnectorServer." + i + "." + (String)entry.getKey(), 1, Units.STRING.intValue(), (String)entry.getValue()); 
  }
  
  public static Map<String, String> importRemoteFrom(int paramInt) throws IOException {
    ByteBuffer byteBuffer;
    Perf perf = Perf.getPerf();
    try {
      byteBuffer = perf.attach(paramInt, "r");
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException(illegalArgumentException.getMessage());
    } 
    List list = (new PerfInstrumentation(byteBuffer)).getAllCounters();
    HashMap hashMap = new HashMap();
    for (Counter counter1 : list) {
      String str = counter1.getName();
      if (str.startsWith("sun.management.JMXConnectorServer.") && !str.equals("sun.management.JMXConnectorServer.address"))
        hashMap.put(str, counter1.getValue().toString()); 
    } 
    return hashMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\ConnectorAddressLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */