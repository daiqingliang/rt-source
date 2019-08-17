package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;
import java.time.zone.ZoneRulesProvider;
import java.util.Objects;

final class ZoneRegion extends ZoneId implements Serializable {
  private static final long serialVersionUID = 8386373296231747096L;
  
  private final String id;
  
  private final ZoneRules rules;
  
  static ZoneRegion ofId(String paramString, boolean paramBoolean) {
    Objects.requireNonNull(paramString, "zoneId");
    checkName(paramString);
    ZoneRules zoneRules = null;
    try {
      zoneRules = ZoneRulesProvider.getRules(paramString, true);
    } catch (ZoneRulesException zoneRulesException) {
      if (paramBoolean)
        throw zoneRulesException; 
    } 
    return new ZoneRegion(paramString, zoneRules);
  }
  
  private static void checkName(String paramString) {
    int i = paramString.length();
    if (i < 2)
      throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + paramString); 
    byte b = 0;
    while (b < i) {
      char c = paramString.charAt(b);
      if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '/' && b != 0) || (c >= '0' && c <= '9' && b != 0) || (c == '~' && b != 0) || (c == '.' && b != 0) || (c == '_' && b != 0) || (c == '+' && b != 0) || (c == '-' && b != 0)) {
        b++;
        continue;
      } 
      throw new DateTimeException("Invalid ID for region-based ZoneId, invalid format: " + paramString);
    } 
  }
  
  ZoneRegion(String paramString, ZoneRules paramZoneRules) {
    this.id = paramString;
    this.rules = paramZoneRules;
  }
  
  public String getId() { return this.id; }
  
  public ZoneRules getRules() { return (this.rules != null) ? this.rules : ZoneRulesProvider.getRules(this.id, false); }
  
  private Object writeReplace() { return new Ser((byte)7, this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Deserialization via serialization delegate"); }
  
  void write(DataOutput paramDataOutput) throws IOException {
    paramDataOutput.writeByte(7);
    writeExternal(paramDataOutput);
  }
  
  void writeExternal(DataOutput paramDataOutput) throws IOException { paramDataOutput.writeUTF(this.id); }
  
  static ZoneId readExternal(DataInput paramDataInput) throws IOException {
    String str = paramDataInput.readUTF();
    return ZoneId.of(str, false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\ZoneRegion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */