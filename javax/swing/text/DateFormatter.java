package javax.swing.text;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DateFormatter extends InternationalFormatter {
  public DateFormatter() { this(DateFormat.getDateInstance()); }
  
  public DateFormatter(DateFormat paramDateFormat) {
    super(paramDateFormat);
    setFormat(paramDateFormat);
  }
  
  public void setFormat(DateFormat paramDateFormat) { setFormat(paramDateFormat); }
  
  private Calendar getCalendar() {
    Format format = getFormat();
    return (format instanceof DateFormat) ? ((DateFormat)format).getCalendar() : Calendar.getInstance();
  }
  
  boolean getSupportsIncrement() { return true; }
  
  Object getAdjustField(int paramInt, Map paramMap) {
    for (Object object : paramMap.keySet()) {
      if (object instanceof DateFormat.Field && (object == DateFormat.Field.HOUR1 || ((DateFormat.Field)object).getCalendarField() != -1))
        return object; 
    } 
    return null;
  }
  
  Object adjustValue(Object paramObject1, Map paramMap, Object paramObject2, int paramInt) throws BadLocationException, ParseException {
    if (paramObject2 != null) {
      if (paramObject2 == DateFormat.Field.HOUR1)
        paramObject2 = DateFormat.Field.HOUR0; 
      int i = ((DateFormat.Field)paramObject2).getCalendarField();
      Calendar calendar = getCalendar();
      if (calendar != null) {
        calendar.setTime((Date)paramObject1);
        int j = calendar.get(i);
        try {
          calendar.add(i, paramInt);
          paramObject1 = calendar.getTime();
        } catch (Throwable throwable) {
          paramObject1 = null;
        } 
        return paramObject1;
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DateFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */