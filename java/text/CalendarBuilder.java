package java.text;

import java.util.Calendar;

class CalendarBuilder {
  private static final int UNSET = 0;
  
  private static final int COMPUTED = 1;
  
  private static final int MINIMUM_USER_STAMP = 2;
  
  private static final int MAX_FIELD = 18;
  
  public static final int WEEK_YEAR = 17;
  
  public static final int ISO_DAY_OF_WEEK = 1000;
  
  private final int[] field = new int[36];
  
  private int nextStamp = 2;
  
  private int maxFieldIndex = -1;
  
  CalendarBuilder set(int paramInt1, int paramInt2) {
    if (paramInt1 == 1000) {
      paramInt1 = 7;
      paramInt2 = toCalendarDayOfWeek(paramInt2);
    } 
    this.field[paramInt1] = this.nextStamp++;
    this.field[18 + paramInt1] = paramInt2;
    if (paramInt1 > this.maxFieldIndex && paramInt1 < 17)
      this.maxFieldIndex = paramInt1; 
    return this;
  }
  
  CalendarBuilder addYear(int paramInt) {
    this.field[19] = this.field[19] + paramInt;
    this.field[35] = this.field[35] + paramInt;
    return this;
  }
  
  boolean isSet(int paramInt) {
    if (paramInt == 1000)
      paramInt = 7; 
    return (this.field[paramInt] > 0);
  }
  
  CalendarBuilder clear(int paramInt) {
    if (paramInt == 1000)
      paramInt = 7; 
    this.field[paramInt] = 0;
    this.field[18 + paramInt] = 0;
    return this;
  }
  
  Calendar establish(Calendar paramCalendar) {
    boolean bool = (isSet(17) && this.field[17] > this.field[1]) ? 1 : 0;
    if (bool && !paramCalendar.isWeekDateSupported()) {
      if (!isSet(1))
        set(1, this.field[35]); 
      bool = false;
    } 
    paramCalendar.clear();
    int i;
    for (i = 2; i < this.nextStamp; i++) {
      for (byte b = 0; b <= this.maxFieldIndex; b++) {
        if (this.field[b] == i) {
          paramCalendar.set(b, this.field[18 + b]);
          break;
        } 
      } 
    } 
    if (bool) {
      i = isSet(3) ? this.field[21] : 1;
      int j = isSet(7) ? this.field[25] : paramCalendar.getFirstDayOfWeek();
      if (!isValidDayOfWeek(j) && paramCalendar.isLenient()) {
        if (j >= 8) {
          i += --j / 7;
          j = j % 7 + 1;
        } else {
          while (j <= 0) {
            j += 7;
            i--;
          } 
        } 
        j = toCalendarDayOfWeek(j);
      } 
      paramCalendar.setWeekDate(this.field[35], i, j);
    } 
    return paramCalendar;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("CalendarBuilder:[");
    int i;
    for (i = 0; i < this.field.length; i++) {
      if (isSet(i))
        stringBuilder.append(i).append('=').append(this.field[18 + i]).append(','); 
    } 
    i = stringBuilder.length() - 1;
    if (stringBuilder.charAt(i) == ',')
      stringBuilder.setLength(i); 
    stringBuilder.append(']');
    return stringBuilder.toString();
  }
  
  static int toISODayOfWeek(int paramInt) { return (paramInt == 1) ? 7 : (paramInt - 1); }
  
  static int toCalendarDayOfWeek(int paramInt) { return !isValidDayOfWeek(paramInt) ? paramInt : ((paramInt == 7) ? 1 : (paramInt + 1)); }
  
  static boolean isValidDayOfWeek(int paramInt) { return (paramInt > 0 && paramInt <= 7); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\CalendarBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */