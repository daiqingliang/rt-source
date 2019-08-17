package sun.management.counter.perf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.management.counter.Counter;
import sun.management.counter.Units;

public class PerfInstrumentation {
  private ByteBuffer buffer;
  
  private Prologue prologue;
  
  private long lastModificationTime;
  
  private long lastUsed;
  
  private int nextEntry;
  
  private SortedMap<String, Counter> map;
  
  public PerfInstrumentation(ByteBuffer paramByteBuffer) {
    this.prologue = new Prologue(paramByteBuffer);
    this.buffer = paramByteBuffer;
    this.buffer.order(this.prologue.getByteOrder());
    int i = getMajorVersion();
    int j = getMinorVersion();
    if (i < 2)
      throw new InstrumentationException("Unsupported version: " + i + "." + j); 
    rewind();
  }
  
  public int getMajorVersion() { return this.prologue.getMajorVersion(); }
  
  public int getMinorVersion() { return this.prologue.getMinorVersion(); }
  
  public long getModificationTimeStamp() { return this.prologue.getModificationTimeStamp(); }
  
  void rewind() {
    this.buffer.rewind();
    this.buffer.position(this.prologue.getEntryOffset());
    this.nextEntry = this.buffer.position();
    this.map = new TreeMap();
  }
  
  boolean hasNext() { return (this.nextEntry < this.prologue.getUsed()); }
  
  Counter getNextCounter() {
    if (!hasNext())
      return null; 
    if (this.nextEntry % 4 != 0)
      throw new InstrumentationException("Entry index not properly aligned: " + this.nextEntry); 
    if (this.nextEntry < 0 || this.nextEntry > this.buffer.limit())
      throw new InstrumentationException("Entry index out of bounds: nextEntry = " + this.nextEntry + ", limit = " + this.buffer.limit()); 
    this.buffer.position(this.nextEntry);
    PerfDataEntry perfDataEntry = new PerfDataEntry(this.buffer);
    this.nextEntry += perfDataEntry.size();
    PerfLongArrayCounter perfLongArrayCounter = null;
    PerfDataType perfDataType = perfDataEntry.type();
    if (perfDataType == PerfDataType.BYTE) {
      if (perfDataEntry.units() == Units.STRING && perfDataEntry.vectorLength() > 0) {
        perfLongArrayCounter = new PerfStringCounter(perfDataEntry.name(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.vectorLength(), perfDataEntry.byteData());
      } else if (perfDataEntry.vectorLength() > 0) {
        PerfByteArrayCounter perfByteArrayCounter = new PerfByteArrayCounter(perfDataEntry.name(), perfDataEntry.units(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.vectorLength(), perfDataEntry.byteData());
      } else {
        assert false;
      } 
    } else if (perfDataType == PerfDataType.LONG) {
      if (perfDataEntry.vectorLength() == 0) {
        PerfLongCounter perfLongCounter = new PerfLongCounter(perfDataEntry.name(), perfDataEntry.units(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.longData());
      } else {
        perfLongArrayCounter = new PerfLongArrayCounter(perfDataEntry.name(), perfDataEntry.units(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.vectorLength(), perfDataEntry.longData());
      } 
    } else {
      assert false;
    } 
    return perfLongArrayCounter;
  }
  
  public List<Counter> getAllCounters() {
    while (hasNext()) {
      Counter counter = getNextCounter();
      if (counter != null)
        this.map.put(counter.getName(), counter); 
    } 
    return new ArrayList(this.map.values());
  }
  
  public List<Counter> findByPattern(String paramString) {
    while (hasNext()) {
      Counter counter = getNextCounter();
      if (counter != null)
        this.map.put(counter.getName(), counter); 
    } 
    Pattern pattern = Pattern.compile(paramString);
    Matcher matcher = pattern.matcher("");
    ArrayList arrayList = new ArrayList();
    for (Map.Entry entry : this.map.entrySet()) {
      String str = (String)entry.getKey();
      matcher.reset(str);
      if (matcher.lookingAt())
        arrayList.add(entry.getValue()); 
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\perf\PerfInstrumentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */