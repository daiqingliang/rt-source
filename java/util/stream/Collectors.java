package java.util.stream;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.EnumSet;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public final class Collectors {
  static final Set<Collector.Characteristics> CH_CONCURRENT_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
  
  static final Set<Collector.Characteristics> CH_CONCURRENT_NOID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED));
  
  static final Set<Collector.Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
  
  static final Set<Collector.Characteristics> CH_UNORDERED_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
  
  static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();
  
  private static <T> BinaryOperator<T> throwingMerger() { return (paramObject1, paramObject2) -> {
        throw new IllegalStateException(String.format("Duplicate key %s", new Object[] { paramObject1 }));
      }; }
  
  private static <I, R> Function<I, R> castingIdentity() { return paramObject -> paramObject; }
  
  public static <T, C extends Collection<T>> Collector<T, ?, C> toCollection(Supplier<C> paramSupplier) { return new CollectorImpl(paramSupplier, Collection::add, (paramCollection1, paramCollection2) -> {
          paramCollection1.addAll(paramCollection2);
          return paramCollection1;
        }CH_ID); }
  
  public static <T> Collector<T, ?, List<T>> toList() { return new CollectorImpl(java.util.ArrayList::new, List::add, (paramList1, paramList2) -> {
          paramList1.addAll(paramList2);
          return paramList1;
        }CH_ID); }
  
  public static <T> Collector<T, ?, Set<T>> toSet() { return new CollectorImpl(java.util.HashSet::new, Set::add, (paramSet1, paramSet2) -> {
          paramSet1.addAll(paramSet2);
          return paramSet1;
        }CH_UNORDERED_ID); }
  
  public static Collector<CharSequence, ?, String> joining() { return new CollectorImpl(StringBuilder::new, StringBuilder::append, (paramStringBuilder1, paramStringBuilder2) -> {
          paramStringBuilder1.append(paramStringBuilder2);
          return paramStringBuilder1;
        }StringBuilder::toString, CH_NOID); }
  
  public static Collector<CharSequence, ?, String> joining(CharSequence paramCharSequence) { return joining(paramCharSequence, "", ""); }
  
  public static Collector<CharSequence, ?, String> joining(CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3) { return new CollectorImpl(() -> new StringJoiner(paramCharSequence1, paramCharSequence2, paramCharSequence3), StringJoiner::add, StringJoiner::merge, StringJoiner::toString, CH_NOID); }
  
  private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> paramBinaryOperator) { return (paramMap1, paramMap2) -> {
        for (Map.Entry entry : paramMap2.entrySet())
          paramMap1.merge(entry.getKey(), entry.getValue(), paramBinaryOperator); 
        return paramMap1;
      }; }
  
  public static <T, U, A, R> Collector<T, ?, R> mapping(Function<? super T, ? extends U> paramFunction, Collector<? super U, A, R> paramCollector) {
    BiConsumer biConsumer = paramCollector.accumulator();
    return new CollectorImpl(paramCollector.supplier(), (paramObject1, paramObject2) -> paramBiConsumer.accept(paramObject1, paramFunction.apply(paramObject2)), paramCollector.combiner(), paramCollector.finisher(), paramCollector.characteristics());
  }
  
  public static <T, A, R, RR> Collector<T, A, RR> collectingAndThen(Collector<T, A, R> paramCollector, Function<R, RR> paramFunction) {
    Set set = paramCollector.characteristics();
    if (set.contains(Collector.Characteristics.IDENTITY_FINISH))
      if (set.size() == 1) {
        set = CH_NOID;
      } else {
        set = EnumSet.copyOf(set);
        set.remove(Collector.Characteristics.IDENTITY_FINISH);
        set = Collections.unmodifiableSet(set);
      }  
    return new CollectorImpl(paramCollector.supplier(), paramCollector.accumulator(), paramCollector.combiner(), paramCollector.finisher().andThen(paramFunction), set);
  }
  
  public static <T> Collector<T, ?, Long> counting() { return reducing(Long.valueOf(0L), paramObject -> Long.valueOf(1L), Long::sum); }
  
  public static <T> Collector<T, ?, Optional<T>> minBy(Comparator<? super T> paramComparator) { return reducing(BinaryOperator.minBy(paramComparator)); }
  
  public static <T> Collector<T, ?, Optional<T>> maxBy(Comparator<? super T> paramComparator) { return reducing(BinaryOperator.maxBy(paramComparator)); }
  
  public static <T> Collector<T, ?, Integer> summingInt(ToIntFunction<? super T> paramToIntFunction) { return new CollectorImpl(() -> new int[1], (paramArrayOfInt, paramObject) -> paramArrayOfInt[0] = paramArrayOfInt[0] + paramToIntFunction.applyAsInt(paramObject), (paramArrayOfInt1, paramArrayOfInt2) -> {
          paramArrayOfInt1[0] = paramArrayOfInt1[0] + paramArrayOfInt2[0];
          return paramArrayOfInt1;
        }paramArrayOfInt -> Integer.valueOf(paramArrayOfInt[0]), CH_NOID); }
  
  public static <T> Collector<T, ?, Long> summingLong(ToLongFunction<? super T> paramToLongFunction) { return new CollectorImpl(() -> new long[1], (paramArrayOfLong, paramObject) -> paramArrayOfLong[0] = paramArrayOfLong[0] + paramToLongFunction.applyAsLong(paramObject), (paramArrayOfLong1, paramArrayOfLong2) -> {
          paramArrayOfLong1[0] = paramArrayOfLong1[0] + paramArrayOfLong2[0];
          return paramArrayOfLong1;
        }paramArrayOfLong -> Long.valueOf(paramArrayOfLong[0]), CH_NOID); }
  
  public static <T> Collector<T, ?, Double> summingDouble(ToDoubleFunction<? super T> paramToDoubleFunction) { return new CollectorImpl(() -> new double[3], (paramArrayOfDouble, paramObject) -> {
          sumWithCompensation(paramArrayOfDouble, paramToDoubleFunction.applyAsDouble(paramObject));
          paramArrayOfDouble[2] = paramArrayOfDouble[2] + paramToDoubleFunction.applyAsDouble(paramObject);
        }(paramArrayOfDouble1, paramArrayOfDouble2) -> {
          sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[0]);
          paramArrayOfDouble1[2] = paramArrayOfDouble1[2] + paramArrayOfDouble2[2];
          return sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[1]);
        }paramArrayOfDouble -> Double.valueOf(computeFinalSum(paramArrayOfDouble)), CH_NOID); }
  
  static double[] sumWithCompensation(double[] paramArrayOfDouble, double paramDouble) {
    double d1 = paramDouble - paramArrayOfDouble[1];
    double d2 = paramArrayOfDouble[0];
    double d3 = d2 + d1;
    paramArrayOfDouble[1] = d3 - d2 - d1;
    paramArrayOfDouble[0] = d3;
    return paramArrayOfDouble;
  }
  
  static double computeFinalSum(double[] paramArrayOfDouble) {
    double d1 = paramArrayOfDouble[0] + paramArrayOfDouble[1];
    double d2 = paramArrayOfDouble[paramArrayOfDouble.length - 1];
    return (Double.isNaN(d1) && Double.isInfinite(d2)) ? d2 : d1;
  }
  
  public static <T> Collector<T, ?, Double> averagingInt(ToIntFunction<? super T> paramToIntFunction) { return new CollectorImpl(() -> new long[2], (paramArrayOfLong, paramObject) -> {
          paramArrayOfLong[0] = paramArrayOfLong[0] + paramToIntFunction.applyAsInt(paramObject);
          paramArrayOfLong[1] = paramArrayOfLong[1] + 1L;
        }(paramArrayOfLong1, paramArrayOfLong2) -> {
          paramArrayOfLong1[0] = paramArrayOfLong1[0] + paramArrayOfLong2[0];
          paramArrayOfLong1[1] = paramArrayOfLong1[1] + paramArrayOfLong2[1];
          return paramArrayOfLong1;
        }paramArrayOfLong -> Double.valueOf((paramArrayOfLong[1] == 0L) ? 0.0D : (paramArrayOfLong[0] / paramArrayOfLong[1])), CH_NOID); }
  
  public static <T> Collector<T, ?, Double> averagingLong(ToLongFunction<? super T> paramToLongFunction) { return new CollectorImpl(() -> new long[2], (paramArrayOfLong, paramObject) -> {
          paramArrayOfLong[0] = paramArrayOfLong[0] + paramToLongFunction.applyAsLong(paramObject);
          paramArrayOfLong[1] = paramArrayOfLong[1] + 1L;
        }(paramArrayOfLong1, paramArrayOfLong2) -> {
          paramArrayOfLong1[0] = paramArrayOfLong1[0] + paramArrayOfLong2[0];
          paramArrayOfLong1[1] = paramArrayOfLong1[1] + paramArrayOfLong2[1];
          return paramArrayOfLong1;
        }paramArrayOfLong -> Double.valueOf((paramArrayOfLong[1] == 0L) ? 0.0D : (paramArrayOfLong[0] / paramArrayOfLong[1])), CH_NOID); }
  
  public static <T> Collector<T, ?, Double> averagingDouble(ToDoubleFunction<? super T> paramToDoubleFunction) { return new CollectorImpl(() -> new double[4], (paramArrayOfDouble, paramObject) -> {
          sumWithCompensation(paramArrayOfDouble, paramToDoubleFunction.applyAsDouble(paramObject));
          paramArrayOfDouble[2] = paramArrayOfDouble[2] + 1.0D;
          paramArrayOfDouble[3] = paramArrayOfDouble[3] + paramToDoubleFunction.applyAsDouble(paramObject);
        }(paramArrayOfDouble1, paramArrayOfDouble2) -> {
          sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[0]);
          sumWithCompensation(paramArrayOfDouble1, paramArrayOfDouble2[1]);
          paramArrayOfDouble1[2] = paramArrayOfDouble1[2] + paramArrayOfDouble2[2];
          paramArrayOfDouble1[3] = paramArrayOfDouble1[3] + paramArrayOfDouble2[3];
          return paramArrayOfDouble1;
        }paramArrayOfDouble -> Double.valueOf((paramArrayOfDouble[2] == 0.0D) ? 0.0D : (computeFinalSum(paramArrayOfDouble) / paramArrayOfDouble[2])), CH_NOID); }
  
  public static <T> Collector<T, ?, T> reducing(T paramT, BinaryOperator<T> paramBinaryOperator) { return new CollectorImpl(boxSupplier(paramT), (paramArrayOfObject, paramObject) -> paramArrayOfObject[0] = paramBinaryOperator.apply(paramArrayOfObject[0], paramObject), (paramArrayOfObject1, paramArrayOfObject2) -> {
          paramArrayOfObject1[0] = paramBinaryOperator.apply(paramArrayOfObject1[0], paramArrayOfObject2[0]);
          return paramArrayOfObject1;
        }paramArrayOfObject -> paramArrayOfObject[0], CH_NOID); }
  
  private static <T> Supplier<T[]> boxSupplier(T paramT) { return () -> {
        new Object[1][0] = paramObject;
        return (Object[])new Object[1];
      }; }
  
  public static <T> Collector<T, ?, Optional<T>> reducing(BinaryOperator<T> paramBinaryOperator) { return new CollectorImpl(() -> new OptionalBox(), OptionalBox::accept, (paramOptionalBox1, paramOptionalBox2) -> {
          if (paramOptionalBox2.present)
            paramOptionalBox1.accept(paramOptionalBox2.value); 
          return paramOptionalBox1;
        }paramOptionalBox -> Optional.ofNullable(paramOptionalBox.value), CH_NOID); }
  
  public static <T, U> Collector<T, ?, U> reducing(U paramU, Function<? super T, ? extends U> paramFunction, BinaryOperator<U> paramBinaryOperator) { return new CollectorImpl(boxSupplier(paramU), (paramArrayOfObject, paramObject) -> paramArrayOfObject[0] = paramBinaryOperator.apply(paramArrayOfObject[0], paramFunction.apply(paramObject)), (paramArrayOfObject1, paramArrayOfObject2) -> {
          paramArrayOfObject1[0] = paramBinaryOperator.apply(paramArrayOfObject1[0], paramArrayOfObject2[0]);
          return paramArrayOfObject1;
        }paramArrayOfObject -> paramArrayOfObject[0], CH_NOID); }
  
  public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> paramFunction) { return groupingBy(paramFunction, toList()); }
  
  public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> paramFunction, Collector<? super T, A, D> paramCollector) { return groupingBy(paramFunction, java.util.HashMap::new, paramCollector); }
  
  public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> paramFunction, Supplier<M> paramSupplier, Collector<? super T, A, D> paramCollector) {
    Supplier supplier1 = paramCollector.supplier();
    BiConsumer biConsumer1 = paramCollector.accumulator();
    BiConsumer biConsumer2 = (paramMap, paramObject) -> {
        Object object1 = Objects.requireNonNull(paramFunction.apply(paramObject), "element cannot be mapped to a null key");
        Object object2 = paramMap.computeIfAbsent(object1, ());
        paramBiConsumer.accept(object2, paramObject);
      };
    BinaryOperator binaryOperator = mapMerger(paramCollector.combiner());
    Supplier<M> supplier2 = paramSupplier;
    if (paramCollector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH))
      return new CollectorImpl(supplier2, biConsumer2, binaryOperator, CH_ID); 
    Function function1 = paramCollector.finisher();
    Function function2 = paramMap -> {
        paramMap.replaceAll(());
        return paramMap;
      };
    return new CollectorImpl(supplier2, biConsumer2, binaryOperator, function2, CH_NOID);
  }
  
  public static <T, K> Collector<T, ?, ConcurrentMap<K, List<T>>> groupingByConcurrent(Function<? super T, ? extends K> paramFunction) { return groupingByConcurrent(paramFunction, java.util.concurrent.ConcurrentHashMap::new, toList()); }
  
  public static <T, K, A, D> Collector<T, ?, ConcurrentMap<K, D>> groupingByConcurrent(Function<? super T, ? extends K> paramFunction, Collector<? super T, A, D> paramCollector) { return groupingByConcurrent(paramFunction, java.util.concurrent.ConcurrentHashMap::new, paramCollector); }
  
  public static <T, K, A, D, M extends ConcurrentMap<K, D>> Collector<T, ?, M> groupingByConcurrent(Function<? super T, ? extends K> paramFunction, Supplier<M> paramSupplier, Collector<? super T, A, D> paramCollector) {
    BiConsumer biConsumer2;
    Supplier supplier1 = paramCollector.supplier();
    BiConsumer biConsumer1 = paramCollector.accumulator();
    BinaryOperator binaryOperator = mapMerger(paramCollector.combiner());
    Supplier<M> supplier2 = paramSupplier;
    if (paramCollector.characteristics().contains(Collector.Characteristics.CONCURRENT)) {
      biConsumer2 = ((paramConcurrentMap, paramObject) -> {
          Object object1 = Objects.requireNonNull(paramFunction.apply(paramObject), "element cannot be mapped to a null key");
          Object object2 = paramConcurrentMap.computeIfAbsent(object1, ());
          paramBiConsumer.accept(object2, paramObject);
        });
    } else {
      biConsumer2 = ((paramConcurrentMap, paramObject) -> {
          Object object1 = Objects.requireNonNull(paramFunction.apply(paramObject), "element cannot be mapped to a null key");
          Object object2 = paramConcurrentMap.computeIfAbsent(object1, ());
          synchronized (object2) {
            paramBiConsumer.accept(object2, paramObject);
          } 
        });
    } 
    if (paramCollector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH))
      return new CollectorImpl(supplier2, biConsumer2, binaryOperator, CH_CONCURRENT_ID); 
    Function function1 = paramCollector.finisher();
    Function function2 = paramConcurrentMap -> {
        paramConcurrentMap.replaceAll(());
        return paramConcurrentMap;
      };
    return new CollectorImpl(supplier2, biConsumer2, binaryOperator, function2, CH_CONCURRENT_NOID);
  }
  
  public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> paramPredicate) { return partitioningBy(paramPredicate, toList()); }
  
  public static <T, D, A> Collector<T, ?, Map<Boolean, D>> partitioningBy(Predicate<? super T> paramPredicate, Collector<? super T, A, D> paramCollector) {
    BiConsumer biConsumer1 = paramCollector.accumulator();
    BiConsumer biConsumer2 = (paramPartition, paramObject) -> paramBiConsumer.accept(paramPredicate.test(paramObject) ? paramPartition.forTrue : paramPartition.forFalse, paramObject);
    BinaryOperator binaryOperator1 = paramCollector.combiner();
    BinaryOperator binaryOperator2 = (paramPartition1, paramPartition2) -> new Partition(paramBinaryOperator.apply(paramPartition1.forTrue, paramPartition2.forTrue), paramBinaryOperator.apply(paramPartition1.forFalse, paramPartition2.forFalse));
    Supplier supplier = () -> new Partition(paramCollector.supplier().get(), paramCollector.supplier().get());
    if (paramCollector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH))
      return new CollectorImpl(supplier, biConsumer2, binaryOperator2, CH_ID); 
    Function function = paramPartition -> new Partition(paramCollector.finisher().apply(paramPartition.forTrue), paramCollector.finisher().apply(paramPartition.forFalse));
    return new CollectorImpl(supplier, biConsumer2, binaryOperator2, function, CH_NOID);
  }
  
  public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> paramFunction1, Function<? super T, ? extends U> paramFunction2) { return toMap(paramFunction1, paramFunction2, throwingMerger(), java.util.HashMap::new); }
  
  public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> paramFunction1, Function<? super T, ? extends U> paramFunction2, BinaryOperator<U> paramBinaryOperator) { return toMap(paramFunction1, paramFunction2, paramBinaryOperator, java.util.HashMap::new); }
  
  public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> paramFunction1, Function<? super T, ? extends U> paramFunction2, BinaryOperator<U> paramBinaryOperator, Supplier<M> paramSupplier) {
    BiConsumer biConsumer = (paramMap, paramObject) -> paramMap.merge(paramFunction1.apply(paramObject), paramFunction2.apply(paramObject), paramBinaryOperator);
    return new CollectorImpl(paramSupplier, biConsumer, mapMerger(paramBinaryOperator), CH_ID);
  }
  
  public static <T, K, U> Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> paramFunction1, Function<? super T, ? extends U> paramFunction2) { return toConcurrentMap(paramFunction1, paramFunction2, throwingMerger(), java.util.concurrent.ConcurrentHashMap::new); }
  
  public static <T, K, U> Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(Function<? super T, ? extends K> paramFunction1, Function<? super T, ? extends U> paramFunction2, BinaryOperator<U> paramBinaryOperator) { return toConcurrentMap(paramFunction1, paramFunction2, paramBinaryOperator, java.util.concurrent.ConcurrentHashMap::new); }
  
  public static <T, K, U, M extends ConcurrentMap<K, U>> Collector<T, ?, M> toConcurrentMap(Function<? super T, ? extends K> paramFunction1, Function<? super T, ? extends U> paramFunction2, BinaryOperator<U> paramBinaryOperator, Supplier<M> paramSupplier) {
    BiConsumer biConsumer = (paramConcurrentMap, paramObject) -> paramConcurrentMap.merge(paramFunction1.apply(paramObject), paramFunction2.apply(paramObject), paramBinaryOperator);
    return new CollectorImpl(paramSupplier, biConsumer, mapMerger(paramBinaryOperator), CH_CONCURRENT_ID);
  }
  
  public static <T> Collector<T, ?, IntSummaryStatistics> summarizingInt(ToIntFunction<? super T> paramToIntFunction) { return new CollectorImpl(IntSummaryStatistics::new, (paramIntSummaryStatistics, paramObject) -> paramIntSummaryStatistics.accept(paramToIntFunction.applyAsInt(paramObject)), (paramIntSummaryStatistics1, paramIntSummaryStatistics2) -> {
          paramIntSummaryStatistics1.combine(paramIntSummaryStatistics2);
          return paramIntSummaryStatistics1;
        }CH_ID); }
  
  public static <T> Collector<T, ?, LongSummaryStatistics> summarizingLong(ToLongFunction<? super T> paramToLongFunction) { return new CollectorImpl(LongSummaryStatistics::new, (paramLongSummaryStatistics, paramObject) -> paramLongSummaryStatistics.accept(paramToLongFunction.applyAsLong(paramObject)), (paramLongSummaryStatistics1, paramLongSummaryStatistics2) -> {
          paramLongSummaryStatistics1.combine(paramLongSummaryStatistics2);
          return paramLongSummaryStatistics1;
        }CH_ID); }
  
  public static <T> Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(ToDoubleFunction<? super T> paramToDoubleFunction) { return new CollectorImpl(DoubleSummaryStatistics::new, (paramDoubleSummaryStatistics, paramObject) -> paramDoubleSummaryStatistics.accept(paramToDoubleFunction.applyAsDouble(paramObject)), (paramDoubleSummaryStatistics1, paramDoubleSummaryStatistics2) -> {
          paramDoubleSummaryStatistics1.combine(paramDoubleSummaryStatistics2);
          return paramDoubleSummaryStatistics1;
        }CH_ID); }
  
  static class CollectorImpl<T, A, R> extends Object implements Collector<T, A, R> {
    private final Supplier<A> supplier;
    
    private final BiConsumer<A, T> accumulator;
    
    private final BinaryOperator<A> combiner;
    
    private final Function<A, R> finisher;
    
    private final Set<Collector.Characteristics> characteristics;
    
    CollectorImpl(Supplier<A> param1Supplier, BiConsumer<A, T> param1BiConsumer, BinaryOperator<A> param1BinaryOperator, Function<A, R> param1Function, Set<Collector.Characteristics> param1Set) {
      this.supplier = param1Supplier;
      this.accumulator = param1BiConsumer;
      this.combiner = param1BinaryOperator;
      this.finisher = param1Function;
      this.characteristics = param1Set;
    }
    
    CollectorImpl(Supplier<A> param1Supplier, BiConsumer<A, T> param1BiConsumer, BinaryOperator<A> param1BinaryOperator, Set<Collector.Characteristics> param1Set) { this(param1Supplier, param1BiConsumer, param1BinaryOperator, Collectors.castingIdentity(), param1Set); }
    
    public BiConsumer<A, T> accumulator() { return this.accumulator; }
    
    public Supplier<A> supplier() { return this.supplier; }
    
    public BinaryOperator<A> combiner() { return this.combiner; }
    
    public Function<A, R> finisher() { return this.finisher; }
    
    public Set<Collector.Characteristics> characteristics() { return this.characteristics; }
  }
  
  private static final class Partition<T> extends AbstractMap<Boolean, T> implements Map<Boolean, T> {
    final T forTrue;
    
    final T forFalse;
    
    Partition(T param1T1, T param1T2) {
      this.forTrue = param1T1;
      this.forFalse = param1T2;
    }
    
    public Set<Map.Entry<Boolean, T>> entrySet() { return new AbstractSet<Map.Entry<Boolean, T>>() {
          public Iterator<Map.Entry<Boolean, T>> iterator() {
            AbstractMap.SimpleImmutableEntry simpleImmutableEntry1 = new AbstractMap.SimpleImmutableEntry(Boolean.valueOf(false), Collectors.Partition.this.forFalse);
            AbstractMap.SimpleImmutableEntry simpleImmutableEntry2 = new AbstractMap.SimpleImmutableEntry(Boolean.valueOf(true), Collectors.Partition.this.forTrue);
            return Arrays.asList(new Map.Entry[] { simpleImmutableEntry1, simpleImmutableEntry2 }).iterator();
          }
          
          public int size() { return 2; }
        }; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Collectors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */