package java.util.function;

@FunctionalInterface
public interface UnaryOperator<T> extends Function<T, T> {
  static <T> UnaryOperator<T> identity() { return paramObject -> paramObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\UnaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */