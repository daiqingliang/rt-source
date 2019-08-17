package java.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.AccessController;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.security.action.GetBooleanAction;

public class Arrays {
  private static final int MIN_ARRAY_SORT_GRAN = 8192;
  
  private static final int INSERTIONSORT_THRESHOLD = 7;
  
  private static void rangeCheck(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 > paramInt3)
      throw new IllegalArgumentException("fromIndex(" + paramInt2 + ") > toIndex(" + paramInt3 + ")"); 
    if (paramInt2 < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt2); 
    if (paramInt3 > paramInt1)
      throw new ArrayIndexOutOfBoundsException(paramInt3); 
  }
  
  public static void sort(int[] paramArrayOfInt) { DualPivotQuicksort.sort(paramArrayOfInt, 0, paramArrayOfInt.length - 1, null, 0, 0); }
  
  public static void sort(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfInt.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfInt, paramInt1, paramInt2 - 1, null, 0, 0);
  }
  
  public static void sort(long[] paramArrayOfLong) { DualPivotQuicksort.sort(paramArrayOfLong, 0, paramArrayOfLong.length - 1, null, 0, 0); }
  
  public static void sort(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfLong.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfLong, paramInt1, paramInt2 - 1, null, 0, 0);
  }
  
  public static void sort(short[] paramArrayOfShort) { DualPivotQuicksort.sort(paramArrayOfShort, 0, paramArrayOfShort.length - 1, null, 0, 0); }
  
  public static void sort(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfShort.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfShort, paramInt1, paramInt2 - 1, null, 0, 0);
  }
  
  public static void sort(char[] paramArrayOfChar) { DualPivotQuicksort.sort(paramArrayOfChar, 0, paramArrayOfChar.length - 1, null, 0, 0); }
  
  public static void sort(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfChar.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfChar, paramInt1, paramInt2 - 1, null, 0, 0);
  }
  
  public static void sort(byte[] paramArrayOfByte) { DualPivotQuicksort.sort(paramArrayOfByte, 0, paramArrayOfByte.length - 1); }
  
  public static void sort(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfByte.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfByte, paramInt1, paramInt2 - 1);
  }
  
  public static void sort(float[] paramArrayOfFloat) { DualPivotQuicksort.sort(paramArrayOfFloat, 0, paramArrayOfFloat.length - 1, null, 0, 0); }
  
  public static void sort(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfFloat.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfFloat, paramInt1, paramInt2 - 1, null, 0, 0);
  }
  
  public static void sort(double[] paramArrayOfDouble) { DualPivotQuicksort.sort(paramArrayOfDouble, 0, paramArrayOfDouble.length - 1, null, 0, 0); }
  
  public static void sort(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfDouble.length, paramInt1, paramInt2);
    DualPivotQuicksort.sort(paramArrayOfDouble, paramInt1, paramInt2 - 1, null, 0, 0);
  }
  
  public static void parallelSort(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfByte, 0, i - 1);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJByte.Sorter(null, paramArrayOfByte, new byte[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfByte.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfByte, paramInt1, paramInt2 - 1);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJByte.Sorter(null, paramArrayOfByte, new byte[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(char[] paramArrayOfChar) {
    int i = paramArrayOfChar.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfChar, 0, i - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJChar.Sorter(null, paramArrayOfChar, new char[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfChar.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfChar, paramInt1, paramInt2 - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJChar.Sorter(null, paramArrayOfChar, new char[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(short[] paramArrayOfShort) {
    int i = paramArrayOfShort.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfShort, 0, i - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJShort.Sorter(null, paramArrayOfShort, new short[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfShort.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfShort, paramInt1, paramInt2 - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJShort.Sorter(null, paramArrayOfShort, new short[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfInt, 0, i - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJInt.Sorter(null, paramArrayOfInt, new int[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfInt.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfInt, paramInt1, paramInt2 - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJInt.Sorter(null, paramArrayOfInt, new int[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(long[] paramArrayOfLong) {
    int i = paramArrayOfLong.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfLong, 0, i - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJLong.Sorter(null, paramArrayOfLong, new long[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfLong.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfLong, paramInt1, paramInt2 - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJLong.Sorter(null, paramArrayOfLong, new long[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(float[] paramArrayOfFloat) {
    int i = paramArrayOfFloat.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfFloat, 0, i - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJFloat.Sorter(null, paramArrayOfFloat, new float[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfFloat.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfFloat, paramInt1, paramInt2 - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJFloat.Sorter(null, paramArrayOfFloat, new float[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(double[] paramArrayOfDouble) {
    int i = paramArrayOfDouble.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfDouble, 0, i - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJDouble.Sorter(null, paramArrayOfDouble, new double[i], 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static void parallelSort(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfDouble.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      DualPivotQuicksort.sort(paramArrayOfDouble, paramInt1, paramInt2 - 1, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJDouble.Sorter(null, paramArrayOfDouble, new double[i], paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k)).invoke();
    } 
  }
  
  public static <T extends Comparable<? super T>> void parallelSort(T[] paramArrayOfT) {
    int i = paramArrayOfT.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      TimSort.sort(paramArrayOfT, 0, i, NaturalOrder.INSTANCE, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJObject.Sorter(null, paramArrayOfT, (Comparable[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i), 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k, NaturalOrder.INSTANCE)).invoke();
    } 
  }
  
  public static <T extends Comparable<? super T>> void parallelSort(T[] paramArrayOfT, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfT.length, paramInt1, paramInt2);
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      TimSort.sort(paramArrayOfT, paramInt1, paramInt2, NaturalOrder.INSTANCE, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJObject.Sorter(null, paramArrayOfT, (Comparable[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i), paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k, NaturalOrder.INSTANCE)).invoke();
    } 
  }
  
  public static <T> void parallelSort(T[] paramArrayOfT, Comparator<? super T> paramComparator) {
    if (paramComparator == null)
      paramComparator = NaturalOrder.INSTANCE; 
    int i = paramArrayOfT.length;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      TimSort.sort(paramArrayOfT, 0, i, paramComparator, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJObject.Sorter(null, paramArrayOfT, (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i), 0, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k, paramComparator)).invoke();
    } 
  }
  
  public static <T> void parallelSort(T[] paramArrayOfT, int paramInt1, int paramInt2, Comparator<? super T> paramComparator) {
    rangeCheck(paramArrayOfT.length, paramInt1, paramInt2);
    if (paramComparator == null)
      paramComparator = NaturalOrder.INSTANCE; 
    int i = paramInt2 - paramInt1;
    int j;
    if (i <= 8192 || (j = ForkJoinPool.getCommonPoolParallelism()) == 1) {
      TimSort.sort(paramArrayOfT, paramInt1, paramInt2, paramComparator, null, 0, 0);
    } else {
      int k;
      (new ArraysParallelSortHelpers.FJObject.Sorter(null, paramArrayOfT, (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i), paramInt1, i, 0, ((k = i / (j << 2)) <= 8192) ? 8192 : k, paramComparator)).invoke();
    } 
  }
  
  public static void sort(Object[] paramArrayOfObject) {
    if (userRequested) {
      legacyMergeSort(paramArrayOfObject);
    } else {
      ComparableTimSort.sort(paramArrayOfObject, 0, paramArrayOfObject.length, null, 0, 0);
    } 
  }
  
  private static void legacyMergeSort(Object[] paramArrayOfObject) {
    Object[] arrayOfObject = (Object[])paramArrayOfObject.clone();
    mergeSort(arrayOfObject, paramArrayOfObject, 0, paramArrayOfObject.length, 0);
  }
  
  public static void sort(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    rangeCheck(paramArrayOfObject.length, paramInt1, paramInt2);
    if (userRequested) {
      legacyMergeSort(paramArrayOfObject, paramInt1, paramInt2);
    } else {
      ComparableTimSort.sort(paramArrayOfObject, paramInt1, paramInt2, null, 0, 0);
    } 
  }
  
  private static void legacyMergeSort(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    Object[] arrayOfObject = copyOfRange(paramArrayOfObject, paramInt1, paramInt2);
    mergeSort(arrayOfObject, paramArrayOfObject, paramInt1, paramInt2, -paramInt1);
  }
  
  private static void mergeSort(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt2 - paramInt1;
    if (i < 7) {
      for (int i3 = paramInt1; i3 < paramInt2; i3++) {
        for (int i4 = i3; i4 > paramInt1 && ((Comparable)paramArrayOfObject2[i4 - 1]).compareTo(paramArrayOfObject2[i4]) > 0; i4--)
          swap(paramArrayOfObject2, i4, i4 - 1); 
      } 
      return;
    } 
    int j = paramInt1;
    int k = paramInt2;
    paramInt1 += paramInt3;
    paramInt2 += paramInt3;
    int m = paramInt1 + paramInt2 >>> 1;
    mergeSort(paramArrayOfObject2, paramArrayOfObject1, paramInt1, m, -paramInt3);
    mergeSort(paramArrayOfObject2, paramArrayOfObject1, m, paramInt2, -paramInt3);
    if (((Comparable)paramArrayOfObject1[m - 1]).compareTo(paramArrayOfObject1[m]) <= 0) {
      System.arraycopy(paramArrayOfObject1, paramInt1, paramArrayOfObject2, j, i);
      return;
    } 
    int n = j;
    int i1 = paramInt1;
    int i2 = m;
    while (n < k) {
      if (i2 >= paramInt2 || (i1 < m && ((Comparable)paramArrayOfObject1[i1]).compareTo(paramArrayOfObject1[i2]) <= 0)) {
        paramArrayOfObject2[n] = paramArrayOfObject1[i1++];
      } else {
        paramArrayOfObject2[n] = paramArrayOfObject1[i2++];
      } 
      n++;
    } 
  }
  
  private static void swap(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    Object object = paramArrayOfObject[paramInt1];
    paramArrayOfObject[paramInt1] = paramArrayOfObject[paramInt2];
    paramArrayOfObject[paramInt2] = object;
  }
  
  public static <T> void sort(T[] paramArrayOfT, Comparator<? super T> paramComparator) {
    if (paramComparator == null) {
      sort(paramArrayOfT);
    } else if (userRequested) {
      legacyMergeSort(paramArrayOfT, paramComparator);
    } else {
      TimSort.sort(paramArrayOfT, 0, paramArrayOfT.length, paramComparator, null, 0, 0);
    } 
  }
  
  private static <T> void legacyMergeSort(T[] paramArrayOfT, Comparator<? super T> paramComparator) {
    Object[] arrayOfObject = (Object[])paramArrayOfT.clone();
    if (paramComparator == null) {
      mergeSort(arrayOfObject, paramArrayOfT, 0, paramArrayOfT.length, 0);
    } else {
      mergeSort(arrayOfObject, paramArrayOfT, 0, paramArrayOfT.length, 0, paramComparator);
    } 
  }
  
  public static <T> void sort(T[] paramArrayOfT, int paramInt1, int paramInt2, Comparator<? super T> paramComparator) {
    if (paramComparator == null) {
      sort(paramArrayOfT, paramInt1, paramInt2);
    } else {
      rangeCheck(paramArrayOfT.length, paramInt1, paramInt2);
      if (userRequested) {
        legacyMergeSort(paramArrayOfT, paramInt1, paramInt2, paramComparator);
      } else {
        TimSort.sort(paramArrayOfT, paramInt1, paramInt2, paramComparator, null, 0, 0);
      } 
    } 
  }
  
  private static <T> void legacyMergeSort(T[] paramArrayOfT, int paramInt1, int paramInt2, Comparator<? super T> paramComparator) {
    Object[] arrayOfObject = copyOfRange(paramArrayOfT, paramInt1, paramInt2);
    if (paramComparator == null) {
      mergeSort(arrayOfObject, paramArrayOfT, paramInt1, paramInt2, -paramInt1);
    } else {
      mergeSort(arrayOfObject, paramArrayOfT, paramInt1, paramInt2, -paramInt1, paramComparator);
    } 
  }
  
  private static void mergeSort(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, int paramInt1, int paramInt2, int paramInt3, Comparator paramComparator) {
    int i = paramInt2 - paramInt1;
    if (i < 7) {
      for (int i3 = paramInt1; i3 < paramInt2; i3++) {
        for (int i4 = i3; i4 > paramInt1 && paramComparator.compare(paramArrayOfObject2[i4 - 1], paramArrayOfObject2[i4]) > 0; i4--)
          swap(paramArrayOfObject2, i4, i4 - 1); 
      } 
      return;
    } 
    int j = paramInt1;
    int k = paramInt2;
    paramInt1 += paramInt3;
    paramInt2 += paramInt3;
    int m = paramInt1 + paramInt2 >>> 1;
    mergeSort(paramArrayOfObject2, paramArrayOfObject1, paramInt1, m, -paramInt3, paramComparator);
    mergeSort(paramArrayOfObject2, paramArrayOfObject1, m, paramInt2, -paramInt3, paramComparator);
    if (paramComparator.compare(paramArrayOfObject1[m - 1], paramArrayOfObject1[m]) <= 0) {
      System.arraycopy(paramArrayOfObject1, paramInt1, paramArrayOfObject2, j, i);
      return;
    } 
    int n = j;
    int i1 = paramInt1;
    int i2 = m;
    while (n < k) {
      if (i2 >= paramInt2 || (i1 < m && paramComparator.compare(paramArrayOfObject1[i1], paramArrayOfObject1[i2]) <= 0)) {
        paramArrayOfObject2[n] = paramArrayOfObject1[i1++];
      } else {
        paramArrayOfObject2[n] = paramArrayOfObject1[i2++];
      } 
      n++;
    } 
  }
  
  public static <T> void parallelPrefix(T[] paramArrayOfT, BinaryOperator<T> paramBinaryOperator) {
    Objects.requireNonNull(paramBinaryOperator);
    if (paramArrayOfT.length > 0)
      (new ArrayPrefixHelpers.CumulateTask(null, paramBinaryOperator, paramArrayOfT, 0, paramArrayOfT.length)).invoke(); 
  }
  
  public static <T> void parallelPrefix(T[] paramArrayOfT, int paramInt1, int paramInt2, BinaryOperator<T> paramBinaryOperator) {
    Objects.requireNonNull(paramBinaryOperator);
    rangeCheck(paramArrayOfT.length, paramInt1, paramInt2);
    if (paramInt1 < paramInt2)
      (new ArrayPrefixHelpers.CumulateTask(null, paramBinaryOperator, paramArrayOfT, paramInt1, paramInt2)).invoke(); 
  }
  
  public static void parallelPrefix(long[] paramArrayOfLong, LongBinaryOperator paramLongBinaryOperator) {
    Objects.requireNonNull(paramLongBinaryOperator);
    if (paramArrayOfLong.length > 0)
      (new ArrayPrefixHelpers.LongCumulateTask(null, paramLongBinaryOperator, paramArrayOfLong, 0, paramArrayOfLong.length)).invoke(); 
  }
  
  public static void parallelPrefix(long[] paramArrayOfLong, int paramInt1, int paramInt2, LongBinaryOperator paramLongBinaryOperator) {
    Objects.requireNonNull(paramLongBinaryOperator);
    rangeCheck(paramArrayOfLong.length, paramInt1, paramInt2);
    if (paramInt1 < paramInt2)
      (new ArrayPrefixHelpers.LongCumulateTask(null, paramLongBinaryOperator, paramArrayOfLong, paramInt1, paramInt2)).invoke(); 
  }
  
  public static void parallelPrefix(double[] paramArrayOfDouble, DoubleBinaryOperator paramDoubleBinaryOperator) {
    Objects.requireNonNull(paramDoubleBinaryOperator);
    if (paramArrayOfDouble.length > 0)
      (new ArrayPrefixHelpers.DoubleCumulateTask(null, paramDoubleBinaryOperator, paramArrayOfDouble, 0, paramArrayOfDouble.length)).invoke(); 
  }
  
  public static void parallelPrefix(double[] paramArrayOfDouble, int paramInt1, int paramInt2, DoubleBinaryOperator paramDoubleBinaryOperator) {
    Objects.requireNonNull(paramDoubleBinaryOperator);
    rangeCheck(paramArrayOfDouble.length, paramInt1, paramInt2);
    if (paramInt1 < paramInt2)
      (new ArrayPrefixHelpers.DoubleCumulateTask(null, paramDoubleBinaryOperator, paramArrayOfDouble, paramInt1, paramInt2)).invoke(); 
  }
  
  public static void parallelPrefix(int[] paramArrayOfInt, IntBinaryOperator paramIntBinaryOperator) {
    Objects.requireNonNull(paramIntBinaryOperator);
    if (paramArrayOfInt.length > 0)
      (new ArrayPrefixHelpers.IntCumulateTask(null, paramIntBinaryOperator, paramArrayOfInt, 0, paramArrayOfInt.length)).invoke(); 
  }
  
  public static void parallelPrefix(int[] paramArrayOfInt, int paramInt1, int paramInt2, IntBinaryOperator paramIntBinaryOperator) {
    Objects.requireNonNull(paramIntBinaryOperator);
    rangeCheck(paramArrayOfInt.length, paramInt1, paramInt2);
    if (paramInt1 < paramInt2)
      (new ArrayPrefixHelpers.IntCumulateTask(null, paramIntBinaryOperator, paramArrayOfInt, paramInt1, paramInt2)).invoke(); 
  }
  
  public static int binarySearch(long[] paramArrayOfLong, long paramLong) { return binarySearch0(paramArrayOfLong, 0, paramArrayOfLong.length, paramLong); }
  
  public static int binarySearch(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong) {
    rangeCheck(paramArrayOfLong.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfLong, paramInt1, paramInt2, paramLong);
  }
  
  private static int binarySearch0(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong) {
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      long l = paramArrayOfLong[k];
      if (l < paramLong) {
        i = k + 1;
        continue;
      } 
      if (l > paramLong) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(int[] paramArrayOfInt, int paramInt) { return binarySearch0(paramArrayOfInt, 0, paramArrayOfInt.length, paramInt); }
  
  public static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    rangeCheck(paramArrayOfInt.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfInt, paramInt1, paramInt2, paramInt3);
  }
  
  private static int binarySearch0(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      int m = paramArrayOfInt[k];
      if (m < paramInt3) {
        i = k + 1;
        continue;
      } 
      if (m > paramInt3) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(short[] paramArrayOfShort, short paramShort) { return binarySearch0(paramArrayOfShort, 0, paramArrayOfShort.length, paramShort); }
  
  public static int binarySearch(short[] paramArrayOfShort, int paramInt1, int paramInt2, short paramShort) {
    rangeCheck(paramArrayOfShort.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfShort, paramInt1, paramInt2, paramShort);
  }
  
  private static int binarySearch0(short[] paramArrayOfShort, int paramInt1, int paramInt2, short paramShort) {
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      short s = paramArrayOfShort[k];
      if (s < paramShort) {
        i = k + 1;
        continue;
      } 
      if (s > paramShort) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(char[] paramArrayOfChar, char paramChar) { return binarySearch0(paramArrayOfChar, 0, paramArrayOfChar.length, paramChar); }
  
  public static int binarySearch(char[] paramArrayOfChar, int paramInt1, int paramInt2, char paramChar) {
    rangeCheck(paramArrayOfChar.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfChar, paramInt1, paramInt2, paramChar);
  }
  
  private static int binarySearch0(char[] paramArrayOfChar, int paramInt1, int paramInt2, char paramChar) {
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      char c = paramArrayOfChar[k];
      if (c < paramChar) {
        i = k + 1;
        continue;
      } 
      if (c > paramChar) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(byte[] paramArrayOfByte, byte paramByte) { return binarySearch0(paramArrayOfByte, 0, paramArrayOfByte.length, paramByte); }
  
  public static int binarySearch(byte[] paramArrayOfByte, int paramInt1, int paramInt2, byte paramByte) {
    rangeCheck(paramArrayOfByte.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfByte, paramInt1, paramInt2, paramByte);
  }
  
  private static int binarySearch0(byte[] paramArrayOfByte, int paramInt1, int paramInt2, byte paramByte) {
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      byte b = paramArrayOfByte[k];
      if (b < paramByte) {
        i = k + 1;
        continue;
      } 
      if (b > paramByte) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(double[] paramArrayOfDouble, double paramDouble) { return binarySearch0(paramArrayOfDouble, 0, paramArrayOfDouble.length, paramDouble); }
  
  public static int binarySearch(double[] paramArrayOfDouble, int paramInt1, int paramInt2, double paramDouble) {
    rangeCheck(paramArrayOfDouble.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfDouble, paramInt1, paramInt2, paramDouble);
  }
  
  private static int binarySearch0(double[] paramArrayOfDouble, int paramInt1, int paramInt2, double paramDouble) {
    int i = paramInt1;
    int j;
    for (j = paramInt2 - 1; i <= j; j = k - 1) {
      int k = i + j >>> 1;
      double d = paramArrayOfDouble[k];
      if (d < paramDouble) {
        i = k + 1;
        continue;
      } 
      if (d > paramDouble) {
        j = k - 1;
        continue;
      } 
      long l1 = Double.doubleToLongBits(d);
      long l2 = Double.doubleToLongBits(paramDouble);
      if (l1 == l2)
        return k; 
      if (l1 < l2) {
        i = k + 1;
        continue;
      } 
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(float[] paramArrayOfFloat, float paramFloat) { return binarySearch0(paramArrayOfFloat, 0, paramArrayOfFloat.length, paramFloat); }
  
  public static int binarySearch(float[] paramArrayOfFloat, int paramInt1, int paramInt2, float paramFloat) {
    rangeCheck(paramArrayOfFloat.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfFloat, paramInt1, paramInt2, paramFloat);
  }
  
  private static int binarySearch0(float[] paramArrayOfFloat, int paramInt1, int paramInt2, float paramFloat) {
    int i = paramInt1;
    int j;
    for (j = paramInt2 - 1; i <= j; j = k - 1) {
      int k = i + j >>> 1;
      float f = paramArrayOfFloat[k];
      if (f < paramFloat) {
        i = k + 1;
        continue;
      } 
      if (f > paramFloat) {
        j = k - 1;
        continue;
      } 
      int m = Float.floatToIntBits(f);
      int n = Float.floatToIntBits(paramFloat);
      if (m == n)
        return k; 
      if (m < n) {
        i = k + 1;
        continue;
      } 
    } 
    return -(i + 1);
  }
  
  public static int binarySearch(Object[] paramArrayOfObject, Object paramObject) { return binarySearch0(paramArrayOfObject, 0, paramArrayOfObject.length, paramObject); }
  
  public static int binarySearch(Object[] paramArrayOfObject, int paramInt1, int paramInt2, Object paramObject) {
    rangeCheck(paramArrayOfObject.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfObject, paramInt1, paramInt2, paramObject);
  }
  
  private static int binarySearch0(Object[] paramArrayOfObject, int paramInt1, int paramInt2, Object paramObject) {
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      Comparable comparable = (Comparable)paramArrayOfObject[k];
      int m = comparable.compareTo(paramObject);
      if (m < 0) {
        i = k + 1;
        continue;
      } 
      if (m > 0) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static <T> int binarySearch(T[] paramArrayOfT, T paramT, Comparator<? super T> paramComparator) { return binarySearch0(paramArrayOfT, 0, paramArrayOfT.length, paramT, paramComparator); }
  
  public static <T> int binarySearch(T[] paramArrayOfT, int paramInt1, int paramInt2, T paramT, Comparator<? super T> paramComparator) {
    rangeCheck(paramArrayOfT.length, paramInt1, paramInt2);
    return binarySearch0(paramArrayOfT, paramInt1, paramInt2, paramT, paramComparator);
  }
  
  private static <T> int binarySearch0(T[] paramArrayOfT, int paramInt1, int paramInt2, T paramT, Comparator<? super T> paramComparator) {
    if (paramComparator == null)
      return binarySearch0(paramArrayOfT, paramInt1, paramInt2, paramT); 
    int i = paramInt1;
    int j = paramInt2 - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      T t = paramArrayOfT[k];
      int m = paramComparator.compare(t, paramT);
      if (m < 0) {
        i = k + 1;
        continue;
      } 
      if (m > 0) {
        j = k - 1;
        continue;
      } 
      return k;
    } 
    return -(i + 1);
  }
  
  public static boolean equals(long[] paramArrayOfLong1, long[] paramArrayOfLong2) {
    if (paramArrayOfLong1 == paramArrayOfLong2)
      return true; 
    if (paramArrayOfLong1 == null || paramArrayOfLong2 == null)
      return false; 
    int i = paramArrayOfLong1.length;
    if (paramArrayOfLong2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfLong1[b] != paramArrayOfLong2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (paramArrayOfInt1 == paramArrayOfInt2)
      return true; 
    if (paramArrayOfInt1 == null || paramArrayOfInt2 == null)
      return false; 
    int i = paramArrayOfInt1.length;
    if (paramArrayOfInt2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfInt1[b] != paramArrayOfInt2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(short[] paramArrayOfShort1, short[] paramArrayOfShort2) {
    if (paramArrayOfShort1 == paramArrayOfShort2)
      return true; 
    if (paramArrayOfShort1 == null || paramArrayOfShort2 == null)
      return false; 
    int i = paramArrayOfShort1.length;
    if (paramArrayOfShort2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfShort1[b] != paramArrayOfShort2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(char[] paramArrayOfChar1, char[] paramArrayOfChar2) {
    if (paramArrayOfChar1 == paramArrayOfChar2)
      return true; 
    if (paramArrayOfChar1 == null || paramArrayOfChar2 == null)
      return false; 
    int i = paramArrayOfChar1.length;
    if (paramArrayOfChar2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfChar1[b] != paramArrayOfChar2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    if (paramArrayOfByte1 == paramArrayOfByte2)
      return true; 
    if (paramArrayOfByte1 == null || paramArrayOfByte2 == null)
      return false; 
    int i = paramArrayOfByte1.length;
    if (paramArrayOfByte2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfByte1[b] != paramArrayOfByte2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2) {
    if (paramArrayOfBoolean1 == paramArrayOfBoolean2)
      return true; 
    if (paramArrayOfBoolean1 == null || paramArrayOfBoolean2 == null)
      return false; 
    int i = paramArrayOfBoolean1.length;
    if (paramArrayOfBoolean2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfBoolean1[b] != paramArrayOfBoolean2[b])
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2) {
    if (paramArrayOfDouble1 == paramArrayOfDouble2)
      return true; 
    if (paramArrayOfDouble1 == null || paramArrayOfDouble2 == null)
      return false; 
    int i = paramArrayOfDouble1.length;
    if (paramArrayOfDouble2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (Double.doubleToLongBits(paramArrayOfDouble1[b]) != Double.doubleToLongBits(paramArrayOfDouble2[b]))
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    if (paramArrayOfFloat1 == paramArrayOfFloat2)
      return true; 
    if (paramArrayOfFloat1 == null || paramArrayOfFloat2 == null)
      return false; 
    int i = paramArrayOfFloat1.length;
    if (paramArrayOfFloat2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (Float.floatToIntBits(paramArrayOfFloat1[b]) != Float.floatToIntBits(paramArrayOfFloat2[b]))
        return false; 
    } 
    return true;
  }
  
  public static boolean equals(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    if (paramArrayOfObject1 == paramArrayOfObject2)
      return true; 
    if (paramArrayOfObject1 == null || paramArrayOfObject2 == null)
      return false; 
    int i = paramArrayOfObject1.length;
    if (paramArrayOfObject2.length != i)
      return false; 
    byte b = 0;
    while (b < i) {
      Object object1 = paramArrayOfObject1[b];
      Object object2 = paramArrayOfObject2[b];
      if ((object1 == null) ? (object2 == null) : object1.equals(object2)) {
        b++;
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public static void fill(long[] paramArrayOfLong, long paramLong) {
    byte b = 0;
    int i = paramArrayOfLong.length;
    while (b < i) {
      paramArrayOfLong[b] = paramLong;
      b++;
    } 
  }
  
  public static void fill(long[] paramArrayOfLong, int paramInt1, int paramInt2, long paramLong) {
    rangeCheck(paramArrayOfLong.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfLong[i] = paramLong; 
  }
  
  public static void fill(int[] paramArrayOfInt, int paramInt) {
    byte b = 0;
    int i = paramArrayOfInt.length;
    while (b < i) {
      paramArrayOfInt[b] = paramInt;
      b++;
    } 
  }
  
  public static void fill(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    rangeCheck(paramArrayOfInt.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfInt[i] = paramInt3; 
  }
  
  public static void fill(short[] paramArrayOfShort, short paramShort) {
    byte b = 0;
    int i = paramArrayOfShort.length;
    while (b < i) {
      paramArrayOfShort[b] = paramShort;
      b++;
    } 
  }
  
  public static void fill(short[] paramArrayOfShort, int paramInt1, int paramInt2, short paramShort) {
    rangeCheck(paramArrayOfShort.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfShort[i] = paramShort; 
  }
  
  public static void fill(char[] paramArrayOfChar, char paramChar) {
    byte b = 0;
    int i = paramArrayOfChar.length;
    while (b < i) {
      paramArrayOfChar[b] = paramChar;
      b++;
    } 
  }
  
  public static void fill(char[] paramArrayOfChar, int paramInt1, int paramInt2, char paramChar) {
    rangeCheck(paramArrayOfChar.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfChar[i] = paramChar; 
  }
  
  public static void fill(byte[] paramArrayOfByte, byte paramByte) {
    byte b = 0;
    int i = paramArrayOfByte.length;
    while (b < i) {
      paramArrayOfByte[b] = paramByte;
      b++;
    } 
  }
  
  public static void fill(byte[] paramArrayOfByte, int paramInt1, int paramInt2, byte paramByte) {
    rangeCheck(paramArrayOfByte.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfByte[i] = paramByte; 
  }
  
  public static void fill(boolean[] paramArrayOfBoolean, boolean paramBoolean) {
    byte b = 0;
    int i = paramArrayOfBoolean.length;
    while (b < i) {
      paramArrayOfBoolean[b] = paramBoolean;
      b++;
    } 
  }
  
  public static void fill(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2, boolean paramBoolean) {
    rangeCheck(paramArrayOfBoolean.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfBoolean[i] = paramBoolean; 
  }
  
  public static void fill(double[] paramArrayOfDouble, double paramDouble) {
    byte b = 0;
    int i = paramArrayOfDouble.length;
    while (b < i) {
      paramArrayOfDouble[b] = paramDouble;
      b++;
    } 
  }
  
  public static void fill(double[] paramArrayOfDouble, int paramInt1, int paramInt2, double paramDouble) {
    rangeCheck(paramArrayOfDouble.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfDouble[i] = paramDouble; 
  }
  
  public static void fill(float[] paramArrayOfFloat, float paramFloat) {
    byte b = 0;
    int i = paramArrayOfFloat.length;
    while (b < i) {
      paramArrayOfFloat[b] = paramFloat;
      b++;
    } 
  }
  
  public static void fill(float[] paramArrayOfFloat, int paramInt1, int paramInt2, float paramFloat) {
    rangeCheck(paramArrayOfFloat.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfFloat[i] = paramFloat; 
  }
  
  public static void fill(Object[] paramArrayOfObject, Object paramObject) {
    byte b = 0;
    int i = paramArrayOfObject.length;
    while (b < i) {
      paramArrayOfObject[b] = paramObject;
      b++;
    } 
  }
  
  public static void fill(Object[] paramArrayOfObject, int paramInt1, int paramInt2, Object paramObject) {
    rangeCheck(paramArrayOfObject.length, paramInt1, paramInt2);
    for (int i = paramInt1; i < paramInt2; i++)
      paramArrayOfObject[i] = paramObject; 
  }
  
  public static <T> T[] copyOf(T[] paramArrayOfT, int paramInt) { return (T[])(Object[])copyOf(paramArrayOfT, paramInt, paramArrayOfT.getClass()); }
  
  public static <T, U> T[] copyOf(U[] paramArrayOfU, int paramInt, Class<? extends T[]> paramClass) {
    Object[] arrayOfObject = (paramClass == Object[].class) ? (Object[])new Object[paramInt] : (Object[])Array.newInstance(paramClass.getComponentType(), paramInt);
    System.arraycopy(paramArrayOfU, 0, arrayOfObject, 0, Math.min(paramArrayOfU.length, paramInt));
    return (T[])arrayOfObject;
  }
  
  public static byte[] copyOf(byte[] paramArrayOfByte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, paramInt));
    return arrayOfByte;
  }
  
  public static short[] copyOf(short[] paramArrayOfShort, int paramInt) {
    short[] arrayOfShort = new short[paramInt];
    System.arraycopy(paramArrayOfShort, 0, arrayOfShort, 0, Math.min(paramArrayOfShort.length, paramInt));
    return arrayOfShort;
  }
  
  public static int[] copyOf(int[] paramArrayOfInt, int paramInt) {
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, Math.min(paramArrayOfInt.length, paramInt));
    return arrayOfInt;
  }
  
  public static long[] copyOf(long[] paramArrayOfLong, int paramInt) {
    long[] arrayOfLong = new long[paramInt];
    System.arraycopy(paramArrayOfLong, 0, arrayOfLong, 0, Math.min(paramArrayOfLong.length, paramInt));
    return arrayOfLong;
  }
  
  public static char[] copyOf(char[] paramArrayOfChar, int paramInt) {
    char[] arrayOfChar = new char[paramInt];
    System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, Math.min(paramArrayOfChar.length, paramInt));
    return arrayOfChar;
  }
  
  public static float[] copyOf(float[] paramArrayOfFloat, int paramInt) {
    float[] arrayOfFloat = new float[paramInt];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, Math.min(paramArrayOfFloat.length, paramInt));
    return arrayOfFloat;
  }
  
  public static double[] copyOf(double[] paramArrayOfDouble, int paramInt) {
    double[] arrayOfDouble = new double[paramInt];
    System.arraycopy(paramArrayOfDouble, 0, arrayOfDouble, 0, Math.min(paramArrayOfDouble.length, paramInt));
    return arrayOfDouble;
  }
  
  public static boolean[] copyOf(boolean[] paramArrayOfBoolean, int paramInt) {
    boolean[] arrayOfBoolean = new boolean[paramInt];
    System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, Math.min(paramArrayOfBoolean.length, paramInt));
    return arrayOfBoolean;
  }
  
  public static <T> T[] copyOfRange(T[] paramArrayOfT, int paramInt1, int paramInt2) { return (T[])copyOfRange(paramArrayOfT, paramInt1, paramInt2, paramArrayOfT.getClass()); }
  
  public static <T, U> T[] copyOfRange(U[] paramArrayOfU, int paramInt1, int paramInt2, Class<? extends T[]> paramClass) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    Object[] arrayOfObject = (paramClass == Object[].class) ? (Object[])new Object[i] : (Object[])Array.newInstance(paramClass.getComponentType(), i);
    System.arraycopy(paramArrayOfU, paramInt1, arrayOfObject, 0, Math.min(paramArrayOfU.length - paramInt1, i));
    return (T[])arrayOfObject;
  }
  
  public static byte[] copyOfRange(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, Math.min(paramArrayOfByte.length - paramInt1, i));
    return arrayOfByte;
  }
  
  public static short[] copyOfRange(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    short[] arrayOfShort = new short[i];
    System.arraycopy(paramArrayOfShort, paramInt1, arrayOfShort, 0, Math.min(paramArrayOfShort.length - paramInt1, i));
    return arrayOfShort;
  }
  
  public static int[] copyOfRange(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    int[] arrayOfInt = new int[i];
    System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, 0, Math.min(paramArrayOfInt.length - paramInt1, i));
    return arrayOfInt;
  }
  
  public static long[] copyOfRange(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    long[] arrayOfLong = new long[i];
    System.arraycopy(paramArrayOfLong, paramInt1, arrayOfLong, 0, Math.min(paramArrayOfLong.length - paramInt1, i));
    return arrayOfLong;
  }
  
  public static char[] copyOfRange(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    char[] arrayOfChar = new char[i];
    System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, Math.min(paramArrayOfChar.length - paramInt1, i));
    return arrayOfChar;
  }
  
  public static float[] copyOfRange(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    float[] arrayOfFloat = new float[i];
    System.arraycopy(paramArrayOfFloat, paramInt1, arrayOfFloat, 0, Math.min(paramArrayOfFloat.length - paramInt1, i));
    return arrayOfFloat;
  }
  
  public static double[] copyOfRange(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    double[] arrayOfDouble = new double[i];
    System.arraycopy(paramArrayOfDouble, paramInt1, arrayOfDouble, 0, Math.min(paramArrayOfDouble.length - paramInt1, i));
    return arrayOfDouble;
  }
  
  public static boolean[] copyOfRange(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    if (i < 0)
      throw new IllegalArgumentException(paramInt1 + " > " + paramInt2); 
    boolean[] arrayOfBoolean = new boolean[i];
    System.arraycopy(paramArrayOfBoolean, paramInt1, arrayOfBoolean, 0, Math.min(paramArrayOfBoolean.length - paramInt1, i));
    return arrayOfBoolean;
  }
  
  @SafeVarargs
  public static <T> List<T> asList(T... paramVarArgs) { return new ArrayList(paramVarArgs); }
  
  public static int hashCode(long[] paramArrayOfLong) {
    if (paramArrayOfLong == null)
      return 0; 
    int i = 1;
    for (long l : paramArrayOfLong) {
      int j = (int)(l ^ l >>> 32);
      i = 31 * i + j;
    } 
    return i;
  }
  
  public static int hashCode(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null)
      return 0; 
    int i = 1;
    for (int j : paramArrayOfInt)
      i = 31 * i + j; 
    return i;
  }
  
  public static int hashCode(short[] paramArrayOfShort) {
    if (paramArrayOfShort == null)
      return 0; 
    short s = 1;
    for (short s1 : paramArrayOfShort)
      s = 31 * s + s1; 
    return s;
  }
  
  public static int hashCode(char[] paramArrayOfChar) {
    if (paramArrayOfChar == null)
      return 0; 
    char c = '\001';
    for (char c1 : paramArrayOfChar)
      c = 31 * c + c1; 
    return c;
  }
  
  public static int hashCode(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return 0; 
    byte b = 1;
    for (byte b1 : paramArrayOfByte)
      b = 31 * b + b1; 
    return b;
  }
  
  public static int hashCode(boolean[] paramArrayOfBoolean) {
    if (paramArrayOfBoolean == null)
      return 0; 
    char c = '\001';
    for (boolean bool : paramArrayOfBoolean)
      c = 31 * c + (bool ? 1231 : 1237); 
    return c;
  }
  
  public static int hashCode(float[] paramArrayOfFloat) {
    if (paramArrayOfFloat == null)
      return 0; 
    int i = 1;
    for (float f : paramArrayOfFloat)
      i = 31 * i + Float.floatToIntBits(f); 
    return i;
  }
  
  public static int hashCode(double[] paramArrayOfDouble) {
    if (paramArrayOfDouble == null)
      return 0; 
    int i = 1;
    for (double d : paramArrayOfDouble) {
      long l = Double.doubleToLongBits(d);
      i = 31 * i + (int)(l ^ l >>> 32);
    } 
    return i;
  }
  
  public static int hashCode(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return 0; 
    byte b = 1;
    for (Object object : paramArrayOfObject)
      b = 31 * b + ((object == null) ? 0 : object.hashCode()); 
    return b;
  }
  
  public static int deepHashCode(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return 0; 
    int i = 1;
    for (Object object : paramArrayOfObject) {
      int j = 0;
      if (object instanceof Object[]) {
        j = deepHashCode((Object[])object);
      } else if (object instanceof byte[]) {
        j = hashCode((byte[])object);
      } else if (object instanceof short[]) {
        j = hashCode((short[])object);
      } else if (object instanceof int[]) {
        j = hashCode((int[])object);
      } else if (object instanceof long[]) {
        j = hashCode((long[])object);
      } else if (object instanceof char[]) {
        j = hashCode((char[])object);
      } else if (object instanceof float[]) {
        j = hashCode((float[])object);
      } else if (object instanceof double[]) {
        j = hashCode((double[])object);
      } else if (object instanceof boolean[]) {
        j = hashCode((boolean[])object);
      } else if (object != null) {
        j = object.hashCode();
      } 
      i = 31 * i + j;
    } 
    return i;
  }
  
  public static boolean deepEquals(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    if (paramArrayOfObject1 == paramArrayOfObject2)
      return true; 
    if (paramArrayOfObject1 == null || paramArrayOfObject2 == null)
      return false; 
    int i = paramArrayOfObject1.length;
    if (paramArrayOfObject2.length != i)
      return false; 
    for (byte b = 0; b < i; b++) {
      Object object1 = paramArrayOfObject1[b];
      Object object2 = paramArrayOfObject2[b];
      if (object1 != object2) {
        if (object1 == null)
          return false; 
        boolean bool = deepEquals0(object1, object2);
        if (!bool)
          return false; 
      } 
    } 
    return true;
  }
  
  static boolean deepEquals0(Object paramObject1, Object paramObject2) {
    boolean bool;
    assert paramObject1 != null;
    if (paramObject1 instanceof Object[] && paramObject2 instanceof Object[]) {
      bool = deepEquals((Object[])paramObject1, (Object[])paramObject2);
    } else if (paramObject1 instanceof byte[] && paramObject2 instanceof byte[]) {
      bool = equals((byte[])paramObject1, (byte[])paramObject2);
    } else if (paramObject1 instanceof short[] && paramObject2 instanceof short[]) {
      bool = equals((short[])paramObject1, (short[])paramObject2);
    } else if (paramObject1 instanceof int[] && paramObject2 instanceof int[]) {
      bool = equals((int[])paramObject1, (int[])paramObject2);
    } else if (paramObject1 instanceof long[] && paramObject2 instanceof long[]) {
      bool = equals((long[])paramObject1, (long[])paramObject2);
    } else if (paramObject1 instanceof char[] && paramObject2 instanceof char[]) {
      bool = equals((char[])paramObject1, (char[])paramObject2);
    } else if (paramObject1 instanceof float[] && paramObject2 instanceof float[]) {
      bool = equals((float[])paramObject1, (float[])paramObject2);
    } else if (paramObject1 instanceof double[] && paramObject2 instanceof double[]) {
      bool = equals((double[])paramObject1, (double[])paramObject2);
    } else if (paramObject1 instanceof boolean[] && paramObject2 instanceof boolean[]) {
      bool = equals((boolean[])paramObject1, (boolean[])paramObject2);
    } else {
      bool = paramObject1.equals(paramObject2);
    } 
    return bool;
  }
  
  public static String toString(long[] paramArrayOfLong) {
    if (paramArrayOfLong == null)
      return "null"; 
    int i = paramArrayOfLong.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfLong[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null)
      return "null"; 
    int i = paramArrayOfInt.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfInt[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(short[] paramArrayOfShort) {
    if (paramArrayOfShort == null)
      return "null"; 
    int i = paramArrayOfShort.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfShort[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(char[] paramArrayOfChar) {
    if (paramArrayOfChar == null)
      return "null"; 
    int i = paramArrayOfChar.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfChar[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return "null"; 
    int i = paramArrayOfByte.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfByte[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(boolean[] paramArrayOfBoolean) {
    if (paramArrayOfBoolean == null)
      return "null"; 
    int i = paramArrayOfBoolean.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfBoolean[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(float[] paramArrayOfFloat) {
    if (paramArrayOfFloat == null)
      return "null"; 
    int i = paramArrayOfFloat.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfFloat[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(double[] paramArrayOfDouble) {
    if (paramArrayOfDouble == null)
      return "null"; 
    int i = paramArrayOfDouble.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(paramArrayOfDouble[b]);
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String toString(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return "null"; 
    int i = paramArrayOfObject.length - 1;
    if (i == -1)
      return "[]"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('[');
    for (byte b = 0;; b++) {
      stringBuilder.append(String.valueOf(paramArrayOfObject[b]));
      if (b == i)
        return stringBuilder.append(']').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  public static String deepToString(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return "null"; 
    int i = 20 * paramArrayOfObject.length;
    if (paramArrayOfObject.length != 0 && i <= 0)
      i = Integer.MAX_VALUE; 
    StringBuilder stringBuilder = new StringBuilder(i);
    deepToString(paramArrayOfObject, stringBuilder, new HashSet());
    return stringBuilder.toString();
  }
  
  private static void deepToString(Object[] paramArrayOfObject, StringBuilder paramStringBuilder, Set<Object[]> paramSet) {
    if (paramArrayOfObject == null) {
      paramStringBuilder.append("null");
      return;
    } 
    int i = paramArrayOfObject.length - 1;
    if (i == -1) {
      paramStringBuilder.append("[]");
      return;
    } 
    paramSet.add(paramArrayOfObject);
    paramStringBuilder.append('[');
    for (byte b = 0;; b++) {
      Object object = paramArrayOfObject[b];
      if (object == null) {
        paramStringBuilder.append("null");
      } else {
        Class clazz = object.getClass();
        if (clazz.isArray()) {
          if (clazz == byte[].class) {
            paramStringBuilder.append(toString((byte[])object));
          } else if (clazz == short[].class) {
            paramStringBuilder.append(toString((short[])object));
          } else if (clazz == int[].class) {
            paramStringBuilder.append(toString((int[])object));
          } else if (clazz == long[].class) {
            paramStringBuilder.append(toString((long[])object));
          } else if (clazz == char[].class) {
            paramStringBuilder.append(toString((char[])object));
          } else if (clazz == float[].class) {
            paramStringBuilder.append(toString((float[])object));
          } else if (clazz == double[].class) {
            paramStringBuilder.append(toString((double[])object));
          } else if (clazz == boolean[].class) {
            paramStringBuilder.append(toString((boolean[])object));
          } else if (paramSet.contains(object)) {
            paramStringBuilder.append("[...]");
          } else {
            deepToString((Object[])object, paramStringBuilder, paramSet);
          } 
        } else {
          paramStringBuilder.append(object.toString());
        } 
      } 
      if (b == i)
        break; 
      paramStringBuilder.append(", ");
    } 
    paramStringBuilder.append(']');
    paramSet.remove(paramArrayOfObject);
  }
  
  public static <T> void setAll(T[] paramArrayOfT, IntFunction<? extends T> paramIntFunction) {
    Objects.requireNonNull(paramIntFunction);
    for (byte b = 0; b < paramArrayOfT.length; b++)
      paramArrayOfT[b] = paramIntFunction.apply(b); 
  }
  
  public static <T> void parallelSetAll(T[] paramArrayOfT, IntFunction<? extends T> paramIntFunction) {
    Objects.requireNonNull(paramIntFunction);
    IntStream.range(0, paramArrayOfT.length).parallel().forEach(paramInt -> paramArrayOfObject[paramInt] = paramIntFunction.apply(paramInt));
  }
  
  public static void setAll(int[] paramArrayOfInt, IntUnaryOperator paramIntUnaryOperator) {
    Objects.requireNonNull(paramIntUnaryOperator);
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      paramArrayOfInt[b] = paramIntUnaryOperator.applyAsInt(b); 
  }
  
  public static void parallelSetAll(int[] paramArrayOfInt, IntUnaryOperator paramIntUnaryOperator) {
    Objects.requireNonNull(paramIntUnaryOperator);
    IntStream.range(0, paramArrayOfInt.length).parallel().forEach(paramInt -> paramArrayOfInt[paramInt] = paramIntUnaryOperator.applyAsInt(paramInt));
  }
  
  public static void setAll(long[] paramArrayOfLong, IntToLongFunction paramIntToLongFunction) {
    Objects.requireNonNull(paramIntToLongFunction);
    for (byte b = 0; b < paramArrayOfLong.length; b++)
      paramArrayOfLong[b] = paramIntToLongFunction.applyAsLong(b); 
  }
  
  public static void parallelSetAll(long[] paramArrayOfLong, IntToLongFunction paramIntToLongFunction) {
    Objects.requireNonNull(paramIntToLongFunction);
    IntStream.range(0, paramArrayOfLong.length).parallel().forEach(paramInt -> paramArrayOfLong[paramInt] = paramIntToLongFunction.applyAsLong(paramInt));
  }
  
  public static void setAll(double[] paramArrayOfDouble, IntToDoubleFunction paramIntToDoubleFunction) {
    Objects.requireNonNull(paramIntToDoubleFunction);
    for (byte b = 0; b < paramArrayOfDouble.length; b++)
      paramArrayOfDouble[b] = paramIntToDoubleFunction.applyAsDouble(b); 
  }
  
  public static void parallelSetAll(double[] paramArrayOfDouble, IntToDoubleFunction paramIntToDoubleFunction) {
    Objects.requireNonNull(paramIntToDoubleFunction);
    IntStream.range(0, paramArrayOfDouble.length).parallel().forEach(paramInt -> paramArrayOfDouble[paramInt] = paramIntToDoubleFunction.applyAsDouble(paramInt));
  }
  
  public static <T> Spliterator<T> spliterator(T[] paramArrayOfT) { return Spliterators.spliterator(paramArrayOfT, 1040); }
  
  public static <T> Spliterator<T> spliterator(T[] paramArrayOfT, int paramInt1, int paramInt2) { return Spliterators.spliterator(paramArrayOfT, paramInt1, paramInt2, 1040); }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt) { return Spliterators.spliterator(paramArrayOfInt, 1040); }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt1, int paramInt2) { return Spliterators.spliterator(paramArrayOfInt, paramInt1, paramInt2, 1040); }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong) { return Spliterators.spliterator(paramArrayOfLong, 1040); }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt1, int paramInt2) { return Spliterators.spliterator(paramArrayOfLong, paramInt1, paramInt2, 1040); }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble) { return Spliterators.spliterator(paramArrayOfDouble, 1040); }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { return Spliterators.spliterator(paramArrayOfDouble, paramInt1, paramInt2, 1040); }
  
  public static <T> Stream<T> stream(T[] paramArrayOfT) { return stream(paramArrayOfT, 0, paramArrayOfT.length); }
  
  public static <T> Stream<T> stream(T[] paramArrayOfT, int paramInt1, int paramInt2) { return StreamSupport.stream(spliterator(paramArrayOfT, paramInt1, paramInt2), false); }
  
  public static IntStream stream(int[] paramArrayOfInt) { return stream(paramArrayOfInt, 0, paramArrayOfInt.length); }
  
  public static IntStream stream(int[] paramArrayOfInt, int paramInt1, int paramInt2) { return StreamSupport.intStream(spliterator(paramArrayOfInt, paramInt1, paramInt2), false); }
  
  public static LongStream stream(long[] paramArrayOfLong) { return stream(paramArrayOfLong, 0, paramArrayOfLong.length); }
  
  public static LongStream stream(long[] paramArrayOfLong, int paramInt1, int paramInt2) { return StreamSupport.longStream(spliterator(paramArrayOfLong, paramInt1, paramInt2), false); }
  
  public static DoubleStream stream(double[] paramArrayOfDouble) { return stream(paramArrayOfDouble, 0, paramArrayOfDouble.length); }
  
  public static DoubleStream stream(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { return StreamSupport.doubleStream(spliterator(paramArrayOfDouble, paramInt1, paramInt2), false); }
  
  private static class ArrayList<E> extends AbstractList<E> implements RandomAccess, Serializable {
    private static final long serialVersionUID = -2764017481108945198L;
    
    private final E[] a;
    
    ArrayList(E[] param1ArrayOfE) { this.a = (Object[])Objects.requireNonNull(param1ArrayOfE); }
    
    public int size() { return this.a.length; }
    
    public Object[] toArray() { return (Object[])this.a.clone(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      int i = size();
      if (param1ArrayOfT.length < i)
        return (T[])Arrays.copyOf(this.a, i, param1ArrayOfT.getClass()); 
      System.arraycopy(this.a, 0, param1ArrayOfT, 0, i);
      if (param1ArrayOfT.length > i)
        param1ArrayOfT[i] = null; 
      return param1ArrayOfT;
    }
    
    public E get(int param1Int) { return (E)this.a[param1Int]; }
    
    public E set(int param1Int, E param1E) {
      Object object = this.a[param1Int];
      this.a[param1Int] = param1E;
      return (E)object;
    }
    
    public int indexOf(Object param1Object) {
      Object[] arrayOfObject = this.a;
      if (param1Object == null) {
        for (byte b = 0; b < arrayOfObject.length; b++) {
          if (arrayOfObject[b] == null)
            return b; 
        } 
      } else {
        for (byte b = 0; b < arrayOfObject.length; b++) {
          if (param1Object.equals(arrayOfObject[b]))
            return b; 
        } 
      } 
      return -1;
    }
    
    public boolean contains(Object param1Object) { return (indexOf(param1Object) != -1); }
    
    public Spliterator<E> spliterator() { return Spliterators.spliterator(this.a, 16); }
    
    public void forEach(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      for (Object object : this.a)
        param1Consumer.accept(object); 
    }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) {
      Objects.requireNonNull(param1UnaryOperator);
      Object[] arrayOfObject = this.a;
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfObject[b] = param1UnaryOperator.apply(arrayOfObject[b]); 
    }
    
    public void sort(Comparator<? super E> param1Comparator) { Arrays.sort(this.a, param1Comparator); }
  }
  
  static final class LegacyMergeSort {
    private static final boolean userRequested = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.util.Arrays.useLegacyMergeSort"))).booleanValue();
  }
  
  static final class NaturalOrder extends Object implements Comparator<Object> {
    static final NaturalOrder INSTANCE = new NaturalOrder();
    
    public int compare(Object param1Object1, Object param1Object2) { return ((Comparable)param1Object1).compareTo(param1Object2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Arrays.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */