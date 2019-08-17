package java.time.zone;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

final class TzdbZoneRulesProvider extends ZoneRulesProvider {
  private List<String> regionIds;
  
  private String versionId;
  
  private final Map<String, Object> regionToRules = new ConcurrentHashMap();
  
  public TzdbZoneRulesProvider() {
    try {
      String str = System.getProperty("java.home") + File.separator + "lib";
      try (DataInputStream null = new DataInputStream(new BufferedInputStream(new FileInputStream(new File(str, "tzdb.dat"))))) {
        load(dataInputStream);
      } 
    } catch (Exception exception) {
      throw new ZoneRulesException("Unable to load TZDB time-zone rules", exception);
    } 
  }
  
  protected Set<String> provideZoneIds() { return new HashSet(this.regionIds); }
  
  protected ZoneRules provideRules(String paramString, boolean paramBoolean) {
    Object object = this.regionToRules.get(paramString);
    if (object == null)
      throw new ZoneRulesException("Unknown time-zone ID: " + paramString); 
    try {
      if (object instanceof byte[]) {
        byte[] arrayOfByte = (byte[])object;
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
        object = Ser.read(dataInputStream);
        this.regionToRules.put(paramString, object);
      } 
      return (ZoneRules)object;
    } catch (Exception exception) {
      throw new ZoneRulesException("Invalid binary time-zone data: TZDB:" + paramString + ", version: " + this.versionId, exception);
    } 
  }
  
  protected NavigableMap<String, ZoneRules> provideVersions(String paramString) {
    TreeMap treeMap = new TreeMap();
    ZoneRules zoneRules = getRules(paramString, false);
    if (zoneRules != null)
      treeMap.put(this.versionId, zoneRules); 
    return treeMap;
  }
  
  private void load(DataInputStream paramDataInputStream) throws Exception {
    if (paramDataInputStream.readByte() != 1)
      throw new StreamCorruptedException("File format not recognised"); 
    String str = paramDataInputStream.readUTF();
    if (!"TZDB".equals(str))
      throw new StreamCorruptedException("File format not recognised"); 
    short s = paramDataInputStream.readShort();
    short s1;
    for (s1 = 0; s1 < s; s1++)
      this.versionId = paramDataInputStream.readUTF(); 
    s1 = paramDataInputStream.readShort();
    String[] arrayOfString = new String[s1];
    short s2;
    for (s2 = 0; s2 < s1; s2++)
      arrayOfString[s2] = paramDataInputStream.readUTF(); 
    this.regionIds = Arrays.asList(arrayOfString);
    s2 = paramDataInputStream.readShort();
    Object[] arrayOfObject = new Object[s2];
    byte b;
    for (b = 0; b < s2; b++) {
      byte[] arrayOfByte = new byte[paramDataInputStream.readShort()];
      paramDataInputStream.readFully(arrayOfByte);
      arrayOfObject[b] = arrayOfByte;
    } 
    for (b = 0; b < s; b++) {
      short s3 = paramDataInputStream.readShort();
      this.regionToRules.clear();
      for (byte b1 = 0; b1 < s3; b1++) {
        String str1 = arrayOfString[paramDataInputStream.readShort()];
        Object object = arrayOfObject[paramDataInputStream.readShort() & 0xFFFF];
        this.regionToRules.put(str1, object);
      } 
    } 
  }
  
  public String toString() { return "TZDB[" + this.versionId + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\zone\TzdbZoneRulesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */