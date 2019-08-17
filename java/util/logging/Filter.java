package java.util.logging;

@FunctionalInterface
public interface Filter {
  boolean isLoggable(LogRecord paramLogRecord);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */