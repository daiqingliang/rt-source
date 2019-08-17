package javax.swing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RowFilter<M, I> extends Object {
  private static void checkIndices(int[] paramArrayOfInt) {
    for (int i = paramArrayOfInt.length - 1; i >= 0; i--) {
      if (paramArrayOfInt[i] < 0)
        throw new IllegalArgumentException("Index must be >= 0"); 
    } 
  }
  
  public static <M, I> RowFilter<M, I> regexFilter(String paramString, int... paramVarArgs) { return new RegexFilter(Pattern.compile(paramString), paramVarArgs); }
  
  public static <M, I> RowFilter<M, I> dateFilter(ComparisonType paramComparisonType, Date paramDate, int... paramVarArgs) { return new DateFilter(paramComparisonType, paramDate.getTime(), paramVarArgs); }
  
  public static <M, I> RowFilter<M, I> numberFilter(ComparisonType paramComparisonType, Number paramNumber, int... paramVarArgs) { return new NumberFilter(paramComparisonType, paramNumber, paramVarArgs); }
  
  public static <M, I> RowFilter<M, I> orFilter(Iterable<? extends RowFilter<? super M, ? super I>> paramIterable) { return new OrFilter(paramIterable); }
  
  public static <M, I> RowFilter<M, I> andFilter(Iterable<? extends RowFilter<? super M, ? super I>> paramIterable) { return new AndFilter(paramIterable); }
  
  public static <M, I> RowFilter<M, I> notFilter(RowFilter<M, I> paramRowFilter) { return new NotFilter(paramRowFilter); }
  
  public abstract boolean include(Entry<? extends M, ? extends I> paramEntry);
  
  private static class AndFilter<M, I> extends OrFilter<M, I> {
    AndFilter(Iterable<? extends RowFilter<? super M, ? super I>> param1Iterable) { super(param1Iterable); }
    
    public boolean include(RowFilter.Entry<? extends M, ? extends I> param1Entry) {
      for (RowFilter rowFilter : this.filters) {
        if (!rowFilter.include(param1Entry))
          return false; 
      } 
      return true;
    }
  }
  
  public enum ComparisonType {
    BEFORE, AFTER, EQUAL, NOT_EQUAL;
  }
  
  private static class DateFilter extends GeneralFilter {
    private long date;
    
    private RowFilter.ComparisonType type;
    
    DateFilter(RowFilter.ComparisonType param1ComparisonType, long param1Long, int[] param1ArrayOfInt) {
      super(param1ArrayOfInt);
      if (param1ComparisonType == null)
        throw new IllegalArgumentException("type must be non-null"); 
      this.type = param1ComparisonType;
      this.date = param1Long;
    }
    
    protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> param1Entry, int param1Int) {
      Object object = param1Entry.getValue(param1Int);
      if (object instanceof Date) {
        long l = ((Date)object).getTime();
        switch (RowFilter.null.$SwitchMap$javax$swing$RowFilter$ComparisonType[this.type.ordinal()]) {
          case 1:
            return (l < this.date);
          case 2:
            return (l > this.date);
          case 3:
            return (l == this.date);
          case 4:
            return (l != this.date);
        } 
      } 
      return false;
    }
  }
  
  public static abstract class Entry<M, I> extends Object {
    public abstract M getModel();
    
    public abstract int getValueCount();
    
    public abstract Object getValue(int param1Int);
    
    public String getStringValue(int param1Int) {
      Object object = getValue(param1Int);
      return (object == null) ? "" : object.toString();
    }
    
    public abstract I getIdentifier();
  }
  
  private static abstract class GeneralFilter extends RowFilter<Object, Object> {
    private int[] columns;
    
    GeneralFilter(int[] param1ArrayOfInt) {
      RowFilter.checkIndices(param1ArrayOfInt);
      this.columns = param1ArrayOfInt;
    }
    
    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> param1Entry) {
      int i = param1Entry.getValueCount();
      if (this.columns.length > 0) {
        for (int j = this.columns.length - 1; j >= 0; j--) {
          int k = this.columns[j];
          if (k < i && include(param1Entry, k))
            return true; 
        } 
      } else {
        while (--i >= 0) {
          if (include(param1Entry, i))
            return true; 
        } 
      } 
      return false;
    }
    
    protected abstract boolean include(RowFilter.Entry<? extends Object, ? extends Object> param1Entry, int param1Int);
  }
  
  private static class NotFilter<M, I> extends RowFilter<M, I> {
    private RowFilter<M, I> filter;
    
    NotFilter(RowFilter<M, I> param1RowFilter) {
      if (param1RowFilter == null)
        throw new IllegalArgumentException("filter must be non-null"); 
      this.filter = param1RowFilter;
    }
    
    public boolean include(RowFilter.Entry<? extends M, ? extends I> param1Entry) { return !this.filter.include(param1Entry); }
  }
  
  private static class NumberFilter extends GeneralFilter {
    private boolean isComparable;
    
    private Number number;
    
    private RowFilter.ComparisonType type;
    
    NumberFilter(RowFilter.ComparisonType param1ComparisonType, Number param1Number, int[] param1ArrayOfInt) {
      super(param1ArrayOfInt);
      if (param1ComparisonType == null || param1Number == null)
        throw new IllegalArgumentException("type and number must be non-null"); 
      this.type = param1ComparisonType;
      this.number = param1Number;
      this.isComparable = param1Number instanceof Comparable;
    }
    
    protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> param1Entry, int param1Int) {
      Object object = param1Entry.getValue(param1Int);
      if (object instanceof Number) {
        int i;
        boolean bool = true;
        Class clazz = object.getClass();
        if (this.number.getClass() == clazz && this.isComparable) {
          i = ((Comparable)this.number).compareTo(object);
        } else {
          i = longCompare((Number)object);
        } 
        switch (RowFilter.null.$SwitchMap$javax$swing$RowFilter$ComparisonType[this.type.ordinal()]) {
          case 1:
            return (i > 0);
          case 2:
            return (i < 0);
          case 3:
            return (i == 0);
          case 4:
            return (i != 0);
        } 
      } 
      return false;
    }
    
    private int longCompare(Number param1Number) {
      long l = this.number.longValue() - param1Number.longValue();
      return (l < 0L) ? -1 : ((l > 0L) ? 1 : 0);
    }
  }
  
  private static class OrFilter<M, I> extends RowFilter<M, I> {
    List<RowFilter<? super M, ? super I>> filters = new ArrayList();
    
    OrFilter(Iterable<? extends RowFilter<? super M, ? super I>> param1Iterable) {
      for (RowFilter rowFilter : param1Iterable) {
        if (rowFilter == null)
          throw new IllegalArgumentException("Filter must be non-null"); 
        this.filters.add(rowFilter);
      } 
    }
    
    public boolean include(RowFilter.Entry<? extends M, ? extends I> param1Entry) {
      for (RowFilter rowFilter : this.filters) {
        if (rowFilter.include(param1Entry))
          return true; 
      } 
      return false;
    }
  }
  
  private static class RegexFilter extends GeneralFilter {
    private Matcher matcher;
    
    RegexFilter(Pattern param1Pattern, int[] param1ArrayOfInt) {
      super(param1ArrayOfInt);
      if (param1Pattern == null)
        throw new IllegalArgumentException("Pattern must be non-null"); 
      this.matcher = param1Pattern.matcher("");
    }
    
    protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> param1Entry, int param1Int) {
      this.matcher.reset(param1Entry.getStringValue(param1Int));
      return this.matcher.find();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\RowFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */