package javax.swing;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SpinnerDateModel extends AbstractSpinnerModel implements Serializable {
  private Comparable start;
  
  private Comparable end;
  
  private Calendar value;
  
  private int calendarField;
  
  private boolean calendarFieldOK(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
        return true;
    } 
    return false;
  }
  
  public SpinnerDateModel(Date paramDate, Comparable paramComparable1, Comparable paramComparable2, int paramInt) {
    if (paramDate == null)
      throw new IllegalArgumentException("value is null"); 
    if (!calendarFieldOK(paramInt))
      throw new IllegalArgumentException("invalid calendarField"); 
    if ((paramComparable1 != null && paramComparable1.compareTo(paramDate) > 0) || (paramComparable2 != null && paramComparable2.compareTo(paramDate) < 0))
      throw new IllegalArgumentException("(start <= value <= end) is false"); 
    this.value = Calendar.getInstance();
    this.start = paramComparable1;
    this.end = paramComparable2;
    this.calendarField = paramInt;
    this.value.setTime(paramDate);
  }
  
  public SpinnerDateModel() { this(new Date(), null, null, 5); }
  
  public void setStart(Comparable paramComparable) {
    if ((paramComparable == null) ? (this.start != null) : !paramComparable.equals(this.start)) {
      this.start = paramComparable;
      fireStateChanged();
    } 
  }
  
  public Comparable getStart() { return this.start; }
  
  public void setEnd(Comparable paramComparable) {
    if ((paramComparable == null) ? (this.end != null) : !paramComparable.equals(this.end)) {
      this.end = paramComparable;
      fireStateChanged();
    } 
  }
  
  public Comparable getEnd() { return this.end; }
  
  public void setCalendarField(int paramInt) {
    if (!calendarFieldOK(paramInt))
      throw new IllegalArgumentException("invalid calendarField"); 
    if (paramInt != this.calendarField) {
      this.calendarField = paramInt;
      fireStateChanged();
    } 
  }
  
  public int getCalendarField() { return this.calendarField; }
  
  public Object getNextValue() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(this.value.getTime());
    calendar.add(this.calendarField, 1);
    Date date = calendar.getTime();
    return (this.end == null || this.end.compareTo(date) >= 0) ? date : null;
  }
  
  public Object getPreviousValue() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(this.value.getTime());
    calendar.add(this.calendarField, -1);
    Date date = calendar.getTime();
    return (this.start == null || this.start.compareTo(date) <= 0) ? date : null;
  }
  
  public Date getDate() { return this.value.getTime(); }
  
  public Object getValue() { return this.value.getTime(); }
  
  public void setValue(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Date))
      throw new IllegalArgumentException("illegal value"); 
    if (!paramObject.equals(this.value.getTime())) {
      this.value.setTime((Date)paramObject);
      fireStateChanged();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SpinnerDateModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */