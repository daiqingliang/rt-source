package java.time.temporal;

@FunctionalInterface
public interface TemporalQuery<R> {
  R queryFrom(TemporalAccessor paramTemporalAccessor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\temporal\TemporalQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */