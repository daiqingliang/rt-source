package java.time;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

final class Ser implements Externalizable {
  private static final long serialVersionUID = -7683839454370182990L;
  
  static final byte DURATION_TYPE = 1;
  
  static final byte INSTANT_TYPE = 2;
  
  static final byte LOCAL_DATE_TYPE = 3;
  
  static final byte LOCAL_TIME_TYPE = 4;
  
  static final byte LOCAL_DATE_TIME_TYPE = 5;
  
  static final byte ZONE_DATE_TIME_TYPE = 6;
  
  static final byte ZONE_REGION_TYPE = 7;
  
  static final byte ZONE_OFFSET_TYPE = 8;
  
  static final byte OFFSET_TIME_TYPE = 9;
  
  static final byte OFFSET_DATE_TIME_TYPE = 10;
  
  static final byte YEAR_TYPE = 11;
  
  static final byte YEAR_MONTH_TYPE = 12;
  
  static final byte MONTH_DAY_TYPE = 13;
  
  static final byte PERIOD_TYPE = 14;
  
  private byte type;
  
  private Object object;
  
  public Ser() {}
  
  Ser(byte paramByte, Object paramObject) {
    this.type = paramByte;
    this.object = paramObject;
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException { writeInternal(this.type, this.object, paramObjectOutput); }
  
  static void writeInternal(byte paramByte, Object paramObject, ObjectOutput paramObjectOutput) throws IOException {
    paramObjectOutput.writeByte(paramByte);
    switch (paramByte) {
      case 1:
        ((Duration)paramObject).writeExternal(paramObjectOutput);
        return;
      case 2:
        ((Instant)paramObject).writeExternal(paramObjectOutput);
        return;
      case 3:
        ((LocalDate)paramObject).writeExternal(paramObjectOutput);
        return;
      case 5:
        ((LocalDateTime)paramObject).writeExternal(paramObjectOutput);
        return;
      case 4:
        ((LocalTime)paramObject).writeExternal(paramObjectOutput);
        return;
      case 7:
        ((ZoneRegion)paramObject).writeExternal(paramObjectOutput);
        return;
      case 8:
        ((ZoneOffset)paramObject).writeExternal(paramObjectOutput);
        return;
      case 6:
        ((ZonedDateTime)paramObject).writeExternal(paramObjectOutput);
        return;
      case 9:
        ((OffsetTime)paramObject).writeExternal(paramObjectOutput);
        return;
      case 10:
        ((OffsetDateTime)paramObject).writeExternal(paramObjectOutput);
        return;
      case 11:
        ((Year)paramObject).writeExternal(paramObjectOutput);
        return;
      case 12:
        ((YearMonth)paramObject).writeExternal(paramObjectOutput);
        return;
      case 13:
        ((MonthDay)paramObject).writeExternal(paramObjectOutput);
        return;
      case 14:
        ((Period)paramObject).writeExternal(paramObjectOutput);
        return;
    } 
    throw new InvalidClassException("Unknown serialized type");
  }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    this.type = paramObjectInput.readByte();
    this.object = readInternal(this.type, paramObjectInput);
  }
  
  static Object read(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    byte b = paramObjectInput.readByte();
    return readInternal(b, paramObjectInput);
  }
  
  private static Object readInternal(byte paramByte, ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    switch (paramByte) {
      case 1:
        return Duration.readExternal(paramObjectInput);
      case 2:
        return Instant.readExternal(paramObjectInput);
      case 3:
        return LocalDate.readExternal(paramObjectInput);
      case 5:
        return LocalDateTime.readExternal(paramObjectInput);
      case 4:
        return LocalTime.readExternal(paramObjectInput);
      case 6:
        return ZonedDateTime.readExternal(paramObjectInput);
      case 8:
        return ZoneOffset.readExternal(paramObjectInput);
      case 7:
        return ZoneRegion.readExternal(paramObjectInput);
      case 9:
        return OffsetTime.readExternal(paramObjectInput);
      case 10:
        return OffsetDateTime.readExternal(paramObjectInput);
      case 11:
        return Year.readExternal(paramObjectInput);
      case 12:
        return YearMonth.readExternal(paramObjectInput);
      case 13:
        return MonthDay.readExternal(paramObjectInput);
      case 14:
        return Period.readExternal(paramObjectInput);
    } 
    throw new StreamCorruptedException("Unknown serialized type");
  }
  
  private Object readResolve() { return this.object; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\Ser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */