package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Collections {
  private static final int BINARYSEARCH_THRESHOLD = 5000;
  
  private static final int REVERSE_THRESHOLD = 18;
  
  private static final int SHUFFLE_THRESHOLD = 5;
  
  private static final int FILL_THRESHOLD = 25;
  
  private static final int ROTATE_THRESHOLD = 100;
  
  private static final int COPY_THRESHOLD = 10;
  
  private static final int REPLACEALL_THRESHOLD = 11;
  
  private static final int INDEXOFSUBLIST_THRESHOLD = 35;
  
  private static Random r;
  
  public static final Set EMPTY_SET = new EmptySet(null);
  
  public static final List EMPTY_LIST = new EmptyList(null);
  
  public static final Map EMPTY_MAP = new EmptyMap(null);
  
  public static <T extends Comparable<? super T>> void sort(List<T> paramList) { paramList.sort(null); }
  
  public static <T> void sort(List<T> paramList, Comparator<? super T> paramComparator) { paramList.sort(paramComparator); }
  
  public static <T> int binarySearch(List<? extends Comparable<? super T>> paramList, T paramT) { return (paramList instanceof RandomAccess || paramList.size() < 5000) ? indexedBinarySearch(paramList, paramT) : iteratorBinarySearch(paramList, paramT); }
  
  private static <T> int indexedBinarySearch(List<? extends Comparable<? super T>> paramList, T paramT) {
    int i = 0;
    int j = paramList.size() - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      Comparable comparable = (Comparable)paramList.get(k);
      int m = comparable.compareTo(paramT);
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
  
  private static <T> int iteratorBinarySearch(List<? extends Comparable<? super T>> paramList, T paramT) {
    int i = 0;
    int j = paramList.size() - 1;
    ListIterator listIterator = paramList.listIterator();
    while (i <= j) {
      int k = i + j >>> 1;
      Comparable comparable = (Comparable)get(listIterator, k);
      int m = comparable.compareTo(paramT);
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
  
  private static <T> T get(ListIterator<? extends T> paramListIterator, int paramInt) {
    Object object = null;
    int i = paramListIterator.nextIndex();
    if (i <= paramInt) {
      do {
        object = paramListIterator.next();
      } while (i++ < paramInt);
    } else {
      do {
        object = paramListIterator.previous();
      } while (--i > paramInt);
    } 
    return (T)object;
  }
  
  public static <T> int binarySearch(List<? extends T> paramList, T paramT, Comparator<? super T> paramComparator) { return (paramComparator == null) ? binarySearch(paramList, paramT) : ((paramList instanceof RandomAccess || paramList.size() < 5000) ? indexedBinarySearch(paramList, paramT, paramComparator) : iteratorBinarySearch(paramList, paramT, paramComparator)); }
  
  private static <T> int indexedBinarySearch(List<? extends T> paramList, T paramT, Comparator<? super T> paramComparator) {
    int i = 0;
    int j = paramList.size() - 1;
    while (i <= j) {
      int k = i + j >>> 1;
      Object object = paramList.get(k);
      int m = paramComparator.compare(object, paramT);
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
  
  private static <T> int iteratorBinarySearch(List<? extends T> paramList, T paramT, Comparator<? super T> paramComparator) {
    int i = 0;
    int j = paramList.size() - 1;
    ListIterator listIterator = paramList.listIterator();
    while (i <= j) {
      int k = i + j >>> 1;
      Object object = get(listIterator, k);
      int m = paramComparator.compare(object, paramT);
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
  
  public static void reverse(List<?> paramList) {
    int i = paramList.size();
    if (i < 18 || paramList instanceof RandomAccess) {
      byte b = 0;
      int j = i >> 1;
      for (int k = i - 1; b < j; k--) {
        swap(paramList, b, k);
        b++;
      } 
    } else {
      ListIterator listIterator1 = paramList.listIterator();
      ListIterator listIterator2 = paramList.listIterator(i);
      byte b = 0;
      int j = paramList.size() >> 1;
      while (b < j) {
        Object object = listIterator1.next();
        listIterator1.set(listIterator2.previous());
        listIterator2.set(object);
        b++;
      } 
    } 
  }
  
  public static void shuffle(List<?> paramList) {
    Random random = r;
    if (random == null)
      r = random = new Random(); 
    shuffle(paramList, random);
  }
  
  public static void shuffle(List<?> paramList, Random paramRandom) {
    int i = paramList.size();
    if (i < 5 || paramList instanceof RandomAccess) {
      for (int j = i; j > 1; j--)
        swap(paramList, j - 1, paramRandom.nextInt(j)); 
    } else {
      Object[] arrayOfObject = paramList.toArray();
      for (int j = i; j > 1; j--)
        swap(arrayOfObject, j - 1, paramRandom.nextInt(j)); 
      ListIterator listIterator = paramList.listIterator();
      for (byte b = 0; b < arrayOfObject.length; b++) {
        listIterator.next();
        listIterator.set(arrayOfObject[b]);
      } 
    } 
  }
  
  public static void swap(List<?> paramList, int paramInt1, int paramInt2) {
    List<?> list = paramList;
    list.set(paramInt1, list.set(paramInt2, list.get(paramInt1)));
  }
  
  private static void swap(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    Object object = paramArrayOfObject[paramInt1];
    paramArrayOfObject[paramInt1] = paramArrayOfObject[paramInt2];
    paramArrayOfObject[paramInt2] = object;
  }
  
  public static <T> void fill(List<? super T> paramList, T paramT) {
    int i = paramList.size();
    if (i < 25 || paramList instanceof RandomAccess) {
      for (byte b = 0; b < i; b++)
        paramList.set(b, paramT); 
    } else {
      ListIterator listIterator = paramList.listIterator();
      for (byte b = 0; b < i; b++) {
        listIterator.next();
        listIterator.set(paramT);
      } 
    } 
  }
  
  public static <T> void copy(List<? super T> paramList1, List<? extends T> paramList2) {
    int i = paramList2.size();
    if (i > paramList1.size())
      throw new IndexOutOfBoundsException("Source does not fit in dest"); 
    if (i < 10 || (paramList2 instanceof RandomAccess && paramList1 instanceof RandomAccess)) {
      for (byte b = 0; b < i; b++)
        paramList1.set(b, paramList2.get(b)); 
    } else {
      ListIterator listIterator1 = paramList1.listIterator();
      ListIterator listIterator2 = paramList2.listIterator();
      for (byte b = 0; b < i; b++) {
        listIterator1.next();
        listIterator1.set(listIterator2.next());
      } 
    } 
  }
  
  public static <T extends Comparable<? super T>> T min(Collection<? extends T> paramCollection) {
    Iterator iterator = paramCollection.iterator();
    Object object = iterator.next();
    while (iterator.hasNext()) {
      Object object1 = iterator.next();
      if (((Comparable)object1).compareTo(object) < 0)
        object = object1; 
    } 
    return (T)object;
  }
  
  public static <T> T min(Collection<? extends T> paramCollection, Comparator<? super T> paramComparator) {
    if (paramComparator == null)
      return (T)min(paramCollection); 
    Iterator iterator = paramCollection.iterator();
    Object object = iterator.next();
    while (iterator.hasNext()) {
      Object object1 = iterator.next();
      if (paramComparator.compare(object1, object) < 0)
        object = object1; 
    } 
    return (T)object;
  }
  
  public static <T extends Comparable<? super T>> T max(Collection<? extends T> paramCollection) {
    Iterator iterator = paramCollection.iterator();
    Object object = iterator.next();
    while (iterator.hasNext()) {
      Object object1 = iterator.next();
      if (((Comparable)object1).compareTo(object) > 0)
        object = object1; 
    } 
    return (T)object;
  }
  
  public static <T> T max(Collection<? extends T> paramCollection, Comparator<? super T> paramComparator) {
    if (paramComparator == null)
      return (T)max(paramCollection); 
    Iterator iterator = paramCollection.iterator();
    Object object = iterator.next();
    while (iterator.hasNext()) {
      Object object1 = iterator.next();
      if (paramComparator.compare(object1, object) > 0)
        object = object1; 
    } 
    return (T)object;
  }
  
  public static void rotate(List<?> paramList, int paramInt) {
    if (paramList instanceof RandomAccess || paramList.size() < 100) {
      rotate1(paramList, paramInt);
    } else {
      rotate2(paramList, paramInt);
    } 
  }
  
  private static <T> void rotate1(List<T> paramList, int paramInt) {
    int i = paramList.size();
    if (i == 0)
      return; 
    paramInt %= i;
    if (paramInt < 0)
      paramInt += i; 
    if (paramInt == 0)
      return; 
    byte b1 = 0;
    byte b2 = 0;
    while (b2 != i) {
      Object object = paramList.get(b1);
      int j = b1;
      do {
        j += paramInt;
        if (j >= i)
          j -= i; 
        object = paramList.set(j, object);
        b2++;
      } while (j != b1);
      b1++;
    } 
  }
  
  private static void rotate2(List<?> paramList, int paramInt) {
    int i = paramList.size();
    if (i == 0)
      return; 
    int j = -paramInt % i;
    if (j < 0)
      j += i; 
    if (j == 0)
      return; 
    reverse(paramList.subList(0, j));
    reverse(paramList.subList(j, i));
    reverse(paramList);
  }
  
  public static <T> boolean replaceAll(List<T> paramList, T paramT1, T paramT2) {
    boolean bool = false;
    int i = paramList.size();
    if (i < 11 || paramList instanceof RandomAccess) {
      if (paramT1 == null) {
        for (byte b = 0; b < i; b++) {
          if (paramList.get(b) == null) {
            paramList.set(b, paramT2);
            bool = true;
          } 
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          if (paramT1.equals(paramList.get(b))) {
            paramList.set(b, paramT2);
            bool = true;
          } 
        } 
      } 
    } else {
      ListIterator listIterator = paramList.listIterator();
      if (paramT1 == null) {
        for (byte b = 0; b < i; b++) {
          if (listIterator.next() == null) {
            listIterator.set(paramT2);
            bool = true;
          } 
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          if (paramT1.equals(listIterator.next())) {
            listIterator.set(paramT2);
            bool = true;
          } 
        } 
      } 
    } 
    return bool;
  }
  
  public static int indexOfSubList(List<?> paramList1, List<?> paramList2) {
    int i = paramList1.size();
    int j = paramList2.size();
    int k = i - j;
    if (i < 35 || (paramList1 instanceof RandomAccess && paramList2 instanceof RandomAccess)) {
      for (byte b = 0; b <= k; b++) {
        byte b1 = 0;
        byte b2 = b;
        while (true) {
          if (b1 < j) {
            if (!eq(paramList2.get(b1), paramList1.get(b2)))
              break; 
            b1++;
            b2++;
            continue;
          } 
          return b;
        } 
      } 
    } else {
      ListIterator listIterator = paramList1.listIterator();
      for (byte b = 0; b <= k; b++) {
        ListIterator listIterator1 = paramList2.listIterator();
        byte b1 = 0;
        while (true) {
          if (b1 < j) {
            if (!eq(listIterator1.next(), listIterator.next())) {
              for (byte b2 = 0; b2 < b1; b2++)
                listIterator.previous(); 
              break;
            } 
            b1++;
            continue;
          } 
          return b;
        } 
      } 
    } 
    return -1;
  }
  
  public static int lastIndexOfSubList(List<?> paramList1, List<?> paramList2) {
    int i = paramList1.size();
    int j = paramList2.size();
    int k = i - j;
    if (i < 35 || paramList1 instanceof RandomAccess) {
      for (int m = k; m >= 0; m--) {
        byte b = 0;
        int n = m;
        while (true) {
          if (b < j) {
            if (!eq(paramList2.get(b), paramList1.get(n)))
              break; 
            b++;
            n++;
            continue;
          } 
          return m;
        } 
      } 
    } else {
      if (k < 0)
        return -1; 
      ListIterator listIterator = paramList1.listIterator(k);
      for (int m = k; m >= 0; m--) {
        ListIterator listIterator1 = paramList2.listIterator();
        byte b = 0;
        while (true) {
          if (b < j) {
            if (!eq(listIterator1.next(), listIterator.next())) {
              if (m != 0)
                for (byte b1 = 0; b1 <= b + true; b1++)
                  listIterator.previous();  
              break;
            } 
            b++;
            continue;
          } 
          return m;
        } 
      } 
    } 
    return -1;
  }
  
  public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> paramCollection) { return new UnmodifiableCollection(paramCollection); }
  
  public static <T> Set<T> unmodifiableSet(Set<? extends T> paramSet) { return new UnmodifiableSet(paramSet); }
  
  public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<T> paramSortedSet) { return new UnmodifiableSortedSet(paramSortedSet); }
  
  public static <T> NavigableSet<T> unmodifiableNavigableSet(NavigableSet<T> paramNavigableSet) { return new UnmodifiableNavigableSet(paramNavigableSet); }
  
  public static <T> List<T> unmodifiableList(List<? extends T> paramList) { return (paramList instanceof RandomAccess) ? new UnmodifiableRandomAccessList(paramList) : new UnmodifiableList(paramList); }
  
  public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> paramMap) { return new UnmodifiableMap(paramMap); }
  
  public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> paramSortedMap) { return new UnmodifiableSortedMap(paramSortedMap); }
  
  public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> paramNavigableMap) { return new UnmodifiableNavigableMap(paramNavigableMap); }
  
  public static <T> Collection<T> synchronizedCollection(Collection<T> paramCollection) { return new SynchronizedCollection(paramCollection); }
  
  static <T> Collection<T> synchronizedCollection(Collection<T> paramCollection, Object paramObject) { return new SynchronizedCollection(paramCollection, paramObject); }
  
  public static <T> Set<T> synchronizedSet(Set<T> paramSet) { return new SynchronizedSet(paramSet); }
  
  static <T> Set<T> synchronizedSet(Set<T> paramSet, Object paramObject) { return new SynchronizedSet(paramSet, paramObject); }
  
  public static <T> SortedSet<T> synchronizedSortedSet(SortedSet<T> paramSortedSet) { return new SynchronizedSortedSet(paramSortedSet); }
  
  public static <T> NavigableSet<T> synchronizedNavigableSet(NavigableSet<T> paramNavigableSet) { return new SynchronizedNavigableSet(paramNavigableSet); }
  
  public static <T> List<T> synchronizedList(List<T> paramList) { return (paramList instanceof RandomAccess) ? new SynchronizedRandomAccessList(paramList) : new SynchronizedList(paramList); }
  
  static <T> List<T> synchronizedList(List<T> paramList, Object paramObject) { return (paramList instanceof RandomAccess) ? new SynchronizedRandomAccessList(paramList, paramObject) : new SynchronizedList(paramList, paramObject); }
  
  public static <K, V> Map<K, V> synchronizedMap(Map<K, V> paramMap) { return new SynchronizedMap(paramMap); }
  
  public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> paramSortedMap) { return new SynchronizedSortedMap(paramSortedMap); }
  
  public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> paramNavigableMap) { return new SynchronizedNavigableMap(paramNavigableMap); }
  
  public static <E> Collection<E> checkedCollection(Collection<E> paramCollection, Class<E> paramClass) { return new CheckedCollection(paramCollection, paramClass); }
  
  static <T> T[] zeroLengthArray(Class<T> paramClass) { return (T[])(Object[])Array.newInstance(paramClass, 0); }
  
  public static <E> Queue<E> checkedQueue(Queue<E> paramQueue, Class<E> paramClass) { return new CheckedQueue(paramQueue, paramClass); }
  
  public static <E> Set<E> checkedSet(Set<E> paramSet, Class<E> paramClass) { return new CheckedSet(paramSet, paramClass); }
  
  public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> paramSortedSet, Class<E> paramClass) { return new CheckedSortedSet(paramSortedSet, paramClass); }
  
  public static <E> NavigableSet<E> checkedNavigableSet(NavigableSet<E> paramNavigableSet, Class<E> paramClass) { return new CheckedNavigableSet(paramNavigableSet, paramClass); }
  
  public static <E> List<E> checkedList(List<E> paramList, Class<E> paramClass) { return (paramList instanceof RandomAccess) ? new CheckedRandomAccessList(paramList, paramClass) : new CheckedList(paramList, paramClass); }
  
  public static <K, V> Map<K, V> checkedMap(Map<K, V> paramMap, Class<K> paramClass1, Class<V> paramClass2) { return new CheckedMap(paramMap, paramClass1, paramClass2); }
  
  public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> paramSortedMap, Class<K> paramClass1, Class<V> paramClass2) { return new CheckedSortedMap(paramSortedMap, paramClass1, paramClass2); }
  
  public static <K, V> NavigableMap<K, V> checkedNavigableMap(NavigableMap<K, V> paramNavigableMap, Class<K> paramClass1, Class<V> paramClass2) { return new CheckedNavigableMap(paramNavigableMap, paramClass1, paramClass2); }
  
  public static <T> Iterator<T> emptyIterator() { return EmptyIterator.EMPTY_ITERATOR; }
  
  public static <T> ListIterator<T> emptyListIterator() { return EmptyListIterator.EMPTY_ITERATOR; }
  
  public static <T> Enumeration<T> emptyEnumeration() { return EmptyEnumeration.EMPTY_ENUMERATION; }
  
  public static final <T> Set<T> emptySet() { return EMPTY_SET; }
  
  public static <E> SortedSet<E> emptySortedSet() { return EMPTY_NAVIGABLE_SET; }
  
  public static <E> NavigableSet<E> emptyNavigableSet() { return EMPTY_NAVIGABLE_SET; }
  
  public static final <T> List<T> emptyList() { return EMPTY_LIST; }
  
  public static final <K, V> Map<K, V> emptyMap() { return EMPTY_MAP; }
  
  public static final <K, V> SortedMap<K, V> emptySortedMap() { return EMPTY_NAVIGABLE_MAP; }
  
  public static final <K, V> NavigableMap<K, V> emptyNavigableMap() { return EMPTY_NAVIGABLE_MAP; }
  
  public static <T> Set<T> singleton(T paramT) { return new SingletonSet(paramT); }
  
  static <E> Iterator<E> singletonIterator(final E e) { return new Iterator<E>() {
        private boolean hasNext = true;
        
        public boolean hasNext() { return this.hasNext; }
        
        public E next() {
          if (this.hasNext) {
            this.hasNext = false;
            return (E)e;
          } 
          throw new NoSuchElementException();
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
        
        public void forEachRemaining(Consumer<? super E> param1Consumer) {
          Objects.requireNonNull(param1Consumer);
          if (this.hasNext) {
            param1Consumer.accept(e);
            this.hasNext = false;
          } 
        }
      }; }
  
  static <T> Spliterator<T> singletonSpliterator(final T element) { return new Spliterator<T>() {
        long est = 1L;
        
        public Spliterator<T> trySplit() { return null; }
        
        public boolean tryAdvance(Consumer<? super T> param1Consumer) {
          Objects.requireNonNull(param1Consumer);
          if (this.est > 0L) {
            this.est--;
            param1Consumer.accept(element);
            return true;
          } 
          return false;
        }
        
        public void forEachRemaining(Consumer<? super T> param1Consumer) { tryAdvance(param1Consumer); }
        
        public long estimateSize() { return this.est; }
        
        public int characteristics() {
          char c = (element != null) ? 256 : 0;
          return c | 0x40 | 0x4000 | 0x400 | true | 0x10;
        }
      }; }
  
  public static <T> List<T> singletonList(T paramT) { return new SingletonList(paramT); }
  
  public static <K, V> Map<K, V> singletonMap(K paramK, V paramV) { return new SingletonMap(paramK, paramV); }
  
  public static <T> List<T> nCopies(int paramInt, T paramT) {
    if (paramInt < 0)
      throw new IllegalArgumentException("List length = " + paramInt); 
    return new CopiesList(paramInt, paramT);
  }
  
  public static <T> Comparator<T> reverseOrder() { return ReverseComparator.REVERSE_ORDER; }
  
  public static <T> Comparator<T> reverseOrder(Comparator<T> paramComparator) { return (paramComparator == null) ? reverseOrder() : ((paramComparator instanceof ReverseComparator2) ? ((ReverseComparator2)paramComparator).cmp : new ReverseComparator2(paramComparator)); }
  
  public static <T> Enumeration<T> enumeration(final Collection<T> c) { return new Enumeration<T>() {
        private final Iterator<T> i = c.iterator();
        
        public boolean hasMoreElements() { return this.i.hasNext(); }
        
        public T nextElement() { return (T)this.i.next(); }
      }; }
  
  public static <T> ArrayList<T> list(Enumeration<T> paramEnumeration) {
    ArrayList arrayList = new ArrayList();
    while (paramEnumeration.hasMoreElements())
      arrayList.add(paramEnumeration.nextElement()); 
    return arrayList;
  }
  
  static boolean eq(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  public static int frequency(Collection<?> paramCollection, Object paramObject) {
    byte b = 0;
    if (paramObject == null) {
      for (Object object : paramCollection) {
        if (object == null)
          b++; 
      } 
    } else {
      for (Object object : paramCollection) {
        if (paramObject.equals(object))
          b++; 
      } 
    } 
    return b;
  }
  
  public static boolean disjoint(Collection<?> paramCollection1, Collection<?> paramCollection2) {
    Collection<?> collection1 = paramCollection2;
    Collection<?> collection2 = paramCollection1;
    if (paramCollection1 instanceof Set) {
      collection2 = paramCollection2;
      collection1 = paramCollection1;
    } else if (!(paramCollection2 instanceof Set)) {
      int i = paramCollection1.size();
      int j = paramCollection2.size();
      if (i == 0 || j == 0)
        return true; 
      if (i > j) {
        collection2 = paramCollection2;
        collection1 = paramCollection1;
      } 
    } 
    for (Object object : collection2) {
      if (collection1.contains(object))
        return false; 
    } 
    return true;
  }
  
  @SafeVarargs
  public static <T> boolean addAll(Collection<? super T> paramCollection, T... paramVarArgs) {
    boolean bool = false;
    for (T t : paramVarArgs)
      bool |= paramCollection.add(t); 
    return bool;
  }
  
  public static <E> Set<E> newSetFromMap(Map<E, Boolean> paramMap) { return new SetFromMap(paramMap); }
  
  public static <T> Queue<T> asLifoQueue(Deque<T> paramDeque) { return new AsLIFOQueue(paramDeque); }
  
  static class AsLIFOQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
    private static final long serialVersionUID = 1802017725587941708L;
    
    private final Deque<E> q;
    
    AsLIFOQueue(Deque<E> param1Deque) { this.q = param1Deque; }
    
    public boolean add(E param1E) {
      this.q.addFirst(param1E);
      return true;
    }
    
    public boolean offer(E param1E) { return this.q.offerFirst(param1E); }
    
    public E poll() { return (E)this.q.pollFirst(); }
    
    public E remove() { return (E)this.q.removeFirst(); }
    
    public E peek() { return (E)this.q.peekFirst(); }
    
    public E element() { return (E)this.q.getFirst(); }
    
    public void clear() { this.q.clear(); }
    
    public int size() { return this.q.size(); }
    
    public boolean isEmpty() { return this.q.isEmpty(); }
    
    public boolean contains(Object param1Object) { return this.q.contains(param1Object); }
    
    public boolean remove(Object param1Object) { return this.q.remove(param1Object); }
    
    public Iterator<E> iterator() { return this.q.iterator(); }
    
    public Object[] toArray() { return this.q.toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])this.q.toArray(param1ArrayOfT); }
    
    public String toString() { return this.q.toString(); }
    
    public boolean containsAll(Collection<?> param1Collection) { return this.q.containsAll(param1Collection); }
    
    public boolean removeAll(Collection<?> param1Collection) { return this.q.removeAll(param1Collection); }
    
    public boolean retainAll(Collection<?> param1Collection) { return this.q.retainAll(param1Collection); }
    
    public void forEach(Consumer<? super E> param1Consumer) { this.q.forEach(param1Consumer); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) { return this.q.removeIf(param1Predicate); }
    
    public Spliterator<E> spliterator() { return this.q.spliterator(); }
    
    public Stream<E> stream() { return this.q.stream(); }
    
    public Stream<E> parallelStream() { return this.q.parallelStream(); }
  }
  
  static class CheckedCollection<E> extends Object implements Collection<E>, Serializable {
    private static final long serialVersionUID = 1578914078182001775L;
    
    final Collection<E> c;
    
    final Class<E> type;
    
    private E[] zeroLengthElementArray;
    
    E typeCheck(Object param1Object) {
      if (param1Object != null && !this.type.isInstance(param1Object))
        throw new ClassCastException(badElementMsg(param1Object)); 
      return (E)param1Object;
    }
    
    private String badElementMsg(Object param1Object) { return "Attempt to insert " + param1Object.getClass() + " element into collection with element type " + this.type; }
    
    CheckedCollection(Collection<E> param1Collection, Class<E> param1Class) {
      this.c = (Collection)Objects.requireNonNull(param1Collection, "c");
      this.type = (Class)Objects.requireNonNull(param1Class, "type");
    }
    
    public int size() { return this.c.size(); }
    
    public boolean isEmpty() { return this.c.isEmpty(); }
    
    public boolean contains(Object param1Object) { return this.c.contains(param1Object); }
    
    public Object[] toArray() { return this.c.toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])this.c.toArray(param1ArrayOfT); }
    
    public String toString() { return this.c.toString(); }
    
    public boolean remove(Object param1Object) { return this.c.remove(param1Object); }
    
    public void clear() { this.c.clear(); }
    
    public boolean containsAll(Collection<?> param1Collection) { return this.c.containsAll(param1Collection); }
    
    public boolean removeAll(Collection<?> param1Collection) { return this.c.removeAll(param1Collection); }
    
    public boolean retainAll(Collection<?> param1Collection) { return this.c.retainAll(param1Collection); }
    
    public Iterator<E> iterator() {
      final Iterator it = this.c.iterator();
      return new Iterator<E>() {
          public boolean hasNext() { return it.hasNext(); }
          
          public E next() { return (E)it.next(); }
          
          public void remove() { it.remove(); }
        };
    }
    
    public boolean add(E param1E) { return this.c.add(typeCheck(param1E)); }
    
    private E[] zeroLengthElementArray() { return (E[])((this.zeroLengthElementArray != null) ? this.zeroLengthElementArray : (this.zeroLengthElementArray = Collections.zeroLengthArray(this.type))); }
    
    Collection<E> checkedCopyOf(Collection<? extends E> param1Collection) {
      Object[] arrayOfObject;
      try {
        Object[] arrayOfObject1 = zeroLengthElementArray();
        arrayOfObject = param1Collection.toArray(arrayOfObject1);
        if (arrayOfObject.getClass() != arrayOfObject1.getClass())
          arrayOfObject = Arrays.copyOf(arrayOfObject, arrayOfObject.length, arrayOfObject1.getClass()); 
      } catch (ArrayStoreException arrayStoreException) {
        arrayOfObject = (Object[])param1Collection.toArray().clone();
        for (Object object : arrayOfObject)
          typeCheck(object); 
      } 
      return Arrays.asList(arrayOfObject);
    }
    
    public boolean addAll(Collection<? extends E> param1Collection) { return this.c.addAll(checkedCopyOf(param1Collection)); }
    
    public void forEach(Consumer<? super E> param1Consumer) { this.c.forEach(param1Consumer); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) { return this.c.removeIf(param1Predicate); }
    
    public Spliterator<E> spliterator() { return this.c.spliterator(); }
    
    public Stream<E> stream() { return this.c.stream(); }
    
    public Stream<E> parallelStream() { return this.c.parallelStream(); }
  }
  
  static class CheckedList<E> extends CheckedCollection<E> implements List<E> {
    private static final long serialVersionUID = 65247728283967356L;
    
    final List<E> list;
    
    CheckedList(List<E> param1List, Class<E> param1Class) {
      super(param1List, param1Class);
      this.list = param1List;
    }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.list.equals(param1Object)); }
    
    public int hashCode() { return this.list.hashCode(); }
    
    public E get(int param1Int) { return (E)this.list.get(param1Int); }
    
    public E remove(int param1Int) { return (E)this.list.remove(param1Int); }
    
    public int indexOf(Object param1Object) { return this.list.indexOf(param1Object); }
    
    public int lastIndexOf(Object param1Object) { return this.list.lastIndexOf(param1Object); }
    
    public E set(int param1Int, E param1E) { return (E)this.list.set(param1Int, typeCheck(param1E)); }
    
    public void add(int param1Int, E param1E) { this.list.add(param1Int, typeCheck(param1E)); }
    
    public boolean addAll(int param1Int, Collection<? extends E> param1Collection) { return this.list.addAll(param1Int, checkedCopyOf(param1Collection)); }
    
    public ListIterator<E> listIterator() { return listIterator(0); }
    
    public ListIterator<E> listIterator(int param1Int) {
      final ListIterator i = this.list.listIterator(param1Int);
      return new ListIterator<E>() {
          public boolean hasNext() { return i.hasNext(); }
          
          public E next() { return (E)i.next(); }
          
          public boolean hasPrevious() { return i.hasPrevious(); }
          
          public E previous() { return (E)i.previous(); }
          
          public int nextIndex() { return i.nextIndex(); }
          
          public int previousIndex() { return i.previousIndex(); }
          
          public void remove() { i.remove(); }
          
          public void set(E param2E) { i.set(Collections.CheckedList.this.typeCheck(param2E)); }
          
          public void add(E param2E) { i.add(Collections.CheckedList.this.typeCheck(param2E)); }
          
          public void forEachRemaining(Consumer<? super E> param2Consumer) { i.forEachRemaining(param2Consumer); }
        };
    }
    
    public List<E> subList(int param1Int1, int param1Int2) { return new CheckedList(this.list.subList(param1Int1, param1Int2), this.type); }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) {
      Objects.requireNonNull(param1UnaryOperator);
      this.list.replaceAll(param1Object -> typeCheck(param1UnaryOperator.apply(param1Object)));
    }
    
    public void sort(Comparator<? super E> param1Comparator) { this.list.sort(param1Comparator); }
  }
  
  private static class CheckedMap<K, V> extends Object implements Map<K, V>, Serializable {
    private static final long serialVersionUID = 5742860141034234728L;
    
    private final Map<K, V> m;
    
    final Class<K> keyType;
    
    final Class<V> valueType;
    
    private Set<Map.Entry<K, V>> entrySet;
    
    private void typeCheck(Object param1Object1, Object param1Object2) {
      if (param1Object1 != null && !this.keyType.isInstance(param1Object1))
        throw new ClassCastException(badKeyMsg(param1Object1)); 
      if (param1Object2 != null && !this.valueType.isInstance(param1Object2))
        throw new ClassCastException(badValueMsg(param1Object2)); 
    }
    
    private BiFunction<? super K, ? super V, ? extends V> typeCheck(BiFunction<? super K, ? super V, ? extends V> param1BiFunction) {
      Objects.requireNonNull(param1BiFunction);
      return (param1Object1, param1Object2) -> {
          Object object = param1BiFunction.apply(param1Object1, param1Object2);
          typeCheck(param1Object1, object);
          return object;
        };
    }
    
    private String badKeyMsg(Object param1Object) { return "Attempt to insert " + param1Object.getClass() + " key into map with key type " + this.keyType; }
    
    private String badValueMsg(Object param1Object) { return "Attempt to insert " + param1Object.getClass() + " value into map with value type " + this.valueType; }
    
    CheckedMap(Map<K, V> param1Map, Class<K> param1Class1, Class<V> param1Class2) {
      this.m = (Map)Objects.requireNonNull(param1Map);
      this.keyType = (Class)Objects.requireNonNull(param1Class1);
      this.valueType = (Class)Objects.requireNonNull(param1Class2);
    }
    
    public int size() { return this.m.size(); }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public boolean containsKey(Object param1Object) { return this.m.containsKey(param1Object); }
    
    public boolean containsValue(Object param1Object) { return this.m.containsValue(param1Object); }
    
    public V get(Object param1Object) { return (V)this.m.get(param1Object); }
    
    public V remove(Object param1Object) { return (V)this.m.remove(param1Object); }
    
    public void clear() { this.m.clear(); }
    
    public Set<K> keySet() { return this.m.keySet(); }
    
    public Collection<V> values() { return this.m.values(); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.m.equals(param1Object)); }
    
    public int hashCode() { return this.m.hashCode(); }
    
    public String toString() { return this.m.toString(); }
    
    public V put(K param1K, V param1V) {
      typeCheck(param1K, param1V);
      return (V)this.m.put(param1K, param1V);
    }
    
    public void putAll(Map<? extends K, ? extends V> param1Map) {
      Object[] arrayOfObject = param1Map.entrySet().toArray();
      ArrayList arrayList = new ArrayList(arrayOfObject.length);
      for (Object object1 : arrayOfObject) {
        Map.Entry entry = (Map.Entry)object1;
        Object object2 = entry.getKey();
        Object object3 = entry.getValue();
        typeCheck(object2, object3);
        arrayList.add(new AbstractMap.SimpleImmutableEntry(object2, object3));
      } 
      for (Map.Entry entry : arrayList)
        this.m.put(entry.getKey(), entry.getValue()); 
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null)
        this.entrySet = new CheckedEntrySet(this.m.entrySet(), this.valueType); 
      return this.entrySet;
    }
    
    public void forEach(BiConsumer<? super K, ? super V> param1BiConsumer) { this.m.forEach(param1BiConsumer); }
    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { this.m.replaceAll(typeCheck(param1BiFunction)); }
    
    public V putIfAbsent(K param1K, V param1V) {
      typeCheck(param1K, param1V);
      return (V)this.m.putIfAbsent(param1K, param1V);
    }
    
    public boolean remove(Object param1Object1, Object param1Object2) { return this.m.remove(param1Object1, param1Object2); }
    
    public boolean replace(K param1K, V param1V1, V param1V2) {
      typeCheck(param1K, param1V2);
      return this.m.replace(param1K, param1V1, param1V2);
    }
    
    public V replace(K param1K, V param1V) {
      typeCheck(param1K, param1V);
      return (V)this.m.replace(param1K, param1V);
    }
    
    public V computeIfAbsent(K param1K, Function<? super K, ? extends V> param1Function) {
      Objects.requireNonNull(param1Function);
      return (V)this.m.computeIfAbsent(param1K, param1Object -> {
            Object object = param1Function.apply(param1Object);
            typeCheck(param1Object, object);
            return object;
          });
    }
    
    public V computeIfPresent(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { return (V)this.m.computeIfPresent(param1K, typeCheck(param1BiFunction)); }
    
    public V compute(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { return (V)this.m.compute(param1K, typeCheck(param1BiFunction)); }
    
    public V merge(K param1K, V param1V, BiFunction<? super V, ? super V, ? extends V> param1BiFunction) {
      Objects.requireNonNull(param1BiFunction);
      return (V)this.m.merge(param1K, param1V, (param1Object1, param1Object2) -> {
            Object object = param1BiFunction.apply(param1Object1, param1Object2);
            typeCheck(null, object);
            return object;
          });
    }
    
    static class CheckedEntrySet<K, V> extends Object implements Set<Map.Entry<K, V>> {
      private final Set<Map.Entry<K, V>> s;
      
      private final Class<V> valueType;
      
      CheckedEntrySet(Set<Map.Entry<K, V>> param2Set, Class<V> param2Class) {
        this.s = param2Set;
        this.valueType = param2Class;
      }
      
      public int size() { return this.s.size(); }
      
      public boolean isEmpty() { return this.s.isEmpty(); }
      
      public String toString() { return this.s.toString(); }
      
      public int hashCode() { return this.s.hashCode(); }
      
      public void clear() { this.s.clear(); }
      
      public boolean add(Map.Entry<K, V> param2Entry) { throw new UnsupportedOperationException(); }
      
      public boolean addAll(Collection<? extends Map.Entry<K, V>> param2Collection) { throw new UnsupportedOperationException(); }
      
      public Iterator<Map.Entry<K, V>> iterator() {
        final Iterator i = this.s.iterator();
        final Class valueType = this.valueType;
        return new Iterator<Map.Entry<K, V>>() {
            public boolean hasNext() { return i.hasNext(); }
            
            public void remove() { i.remove(); }
            
            public Map.Entry<K, V> next() { return Collections.CheckedMap.CheckedEntrySet.checkedEntry((Map.Entry)i.next(), valueType); }
          };
      }
      
      public Object[] toArray() {
        Object[] arrayOfObject1 = this.s.toArray();
        Object[] arrayOfObject2 = CheckedEntry.class.isInstance(arrayOfObject1.getClass().getComponentType()) ? arrayOfObject1 : new Object[arrayOfObject1.length];
        for (byte b = 0; b < arrayOfObject1.length; b++)
          arrayOfObject2[b] = checkedEntry((Map.Entry)arrayOfObject1[b], this.valueType); 
        return arrayOfObject2;
      }
      
      public <T> T[] toArray(T[] param2ArrayOfT) {
        Object[] arrayOfObject = this.s.toArray((param2ArrayOfT.length == 0) ? param2ArrayOfT : Arrays.copyOf(param2ArrayOfT, 0));
        for (byte b = 0; b < arrayOfObject.length; b++)
          arrayOfObject[b] = checkedEntry((Map.Entry)arrayOfObject[b], this.valueType); 
        if (arrayOfObject.length > param2ArrayOfT.length)
          return (T[])arrayOfObject; 
        System.arraycopy(arrayOfObject, 0, param2ArrayOfT, 0, arrayOfObject.length);
        if (param2ArrayOfT.length > arrayOfObject.length)
          param2ArrayOfT[arrayOfObject.length] = null; 
        return param2ArrayOfT;
      }
      
      public boolean contains(Object param2Object) {
        if (!(param2Object instanceof Map.Entry))
          return false; 
        Map.Entry entry = (Map.Entry)param2Object;
        return this.s.contains((entry instanceof CheckedEntry) ? entry : checkedEntry(entry, this.valueType));
      }
      
      public boolean containsAll(Collection<?> param2Collection) {
        for (Object object : param2Collection) {
          if (!contains(object))
            return false; 
        } 
        return true;
      }
      
      public boolean remove(Object param2Object) { return !(param2Object instanceof Map.Entry) ? false : this.s.remove(new AbstractMap.SimpleImmutableEntry((Map.Entry)param2Object)); }
      
      public boolean removeAll(Collection<?> param2Collection) { return batchRemove(param2Collection, false); }
      
      public boolean retainAll(Collection<?> param2Collection) { return batchRemove(param2Collection, true); }
      
      private boolean batchRemove(Collection<?> param2Collection, boolean param2Boolean) {
        Objects.requireNonNull(param2Collection);
        boolean bool = false;
        Iterator iterator = iterator();
        while (iterator.hasNext()) {
          if (param2Collection.contains(iterator.next()) != param2Boolean) {
            iterator.remove();
            bool = true;
          } 
        } 
        return bool;
      }
      
      public boolean equals(Object param2Object) {
        if (param2Object == this)
          return true; 
        if (!(param2Object instanceof Set))
          return false; 
        Set set = (Set)param2Object;
        return (set.size() == this.s.size() && containsAll(set));
      }
      
      static <K, V, T> CheckedEntry<K, V, T> checkedEntry(Map.Entry<K, V> param2Entry, Class<T> param2Class) { return new CheckedEntry(param2Entry, param2Class); }
      
      private static class CheckedEntry<K, V, T> extends Object implements Map.Entry<K, V> {
        private final Map.Entry<K, V> e;
        
        private final Class<T> valueType;
        
        CheckedEntry(Map.Entry<K, V> param3Entry, Class<T> param3Class) {
          this.e = (Map.Entry)Objects.requireNonNull(param3Entry);
          this.valueType = (Class)Objects.requireNonNull(param3Class);
        }
        
        public K getKey() { return (K)this.e.getKey(); }
        
        public V getValue() { return (V)this.e.getValue(); }
        
        public int hashCode() { return this.e.hashCode(); }
        
        public String toString() { return this.e.toString(); }
        
        public V setValue(V param3V) {
          if (param3V != null && !this.valueType.isInstance(param3V))
            throw new ClassCastException(badValueMsg(param3V)); 
          return (V)this.e.setValue(param3V);
        }
        
        private String badValueMsg(Object param3Object) { return "Attempt to insert " + param3Object.getClass() + " value into map with value type " + this.valueType; }
        
        public boolean equals(Object param3Object) { return (param3Object == this) ? true : (!(param3Object instanceof Map.Entry) ? false : this.e.equals(new AbstractMap.SimpleImmutableEntry((Map.Entry)param3Object))); }
      }
    }
  }
  
  static class CheckedNavigableMap<K, V> extends CheckedSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
    private static final long serialVersionUID = -4852462692372534096L;
    
    private final NavigableMap<K, V> nm;
    
    CheckedNavigableMap(NavigableMap<K, V> param1NavigableMap, Class<K> param1Class1, Class<V> param1Class2) {
      super(param1NavigableMap, param1Class1, param1Class2);
      this.nm = param1NavigableMap;
    }
    
    public Comparator<? super K> comparator() { return this.nm.comparator(); }
    
    public K firstKey() { return (K)this.nm.firstKey(); }
    
    public K lastKey() { return (K)this.nm.lastKey(); }
    
    public Map.Entry<K, V> lowerEntry(K param1K) {
      Map.Entry entry = this.nm.lowerEntry(param1K);
      return (null != entry) ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType) : null;
    }
    
    public K lowerKey(K param1K) { return (K)this.nm.lowerKey(param1K); }
    
    public Map.Entry<K, V> floorEntry(K param1K) {
      Map.Entry entry = this.nm.floorEntry(param1K);
      return (null != entry) ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType) : null;
    }
    
    public K floorKey(K param1K) { return (K)this.nm.floorKey(param1K); }
    
    public Map.Entry<K, V> ceilingEntry(K param1K) {
      Map.Entry entry = this.nm.ceilingEntry(param1K);
      return (null != entry) ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType) : null;
    }
    
    public K ceilingKey(K param1K) { return (K)this.nm.ceilingKey(param1K); }
    
    public Map.Entry<K, V> higherEntry(K param1K) {
      Map.Entry entry = this.nm.higherEntry(param1K);
      return (null != entry) ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType) : null;
    }
    
    public K higherKey(K param1K) { return (K)this.nm.higherKey(param1K); }
    
    public Map.Entry<K, V> firstEntry() {
      Map.Entry entry = this.nm.firstEntry();
      return (null != entry) ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType) : null;
    }
    
    public Map.Entry<K, V> lastEntry() {
      Map.Entry entry = this.nm.lastEntry();
      return (null != entry) ? new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType) : null;
    }
    
    public Map.Entry<K, V> pollFirstEntry() {
      Map.Entry entry = this.nm.pollFirstEntry();
      return (null == entry) ? null : new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType);
    }
    
    public Map.Entry<K, V> pollLastEntry() {
      Map.Entry entry = this.nm.pollLastEntry();
      return (null == entry) ? null : new Collections.CheckedMap.CheckedEntrySet.CheckedEntry(entry, this.valueType);
    }
    
    public NavigableMap<K, V> descendingMap() { return Collections.checkedNavigableMap(this.nm.descendingMap(), this.keyType, this.valueType); }
    
    public NavigableSet<K> keySet() { return navigableKeySet(); }
    
    public NavigableSet<K> navigableKeySet() { return Collections.checkedNavigableSet(this.nm.navigableKeySet(), this.keyType); }
    
    public NavigableSet<K> descendingKeySet() { return Collections.checkedNavigableSet(this.nm.descendingKeySet(), this.keyType); }
    
    public NavigableMap<K, V> subMap(K param1K1, K param1K2) { return Collections.checkedNavigableMap(this.nm.subMap(param1K1, true, param1K2, false), this.keyType, this.valueType); }
    
    public NavigableMap<K, V> headMap(K param1K) { return Collections.checkedNavigableMap(this.nm.headMap(param1K, false), this.keyType, this.valueType); }
    
    public NavigableMap<K, V> tailMap(K param1K) { return Collections.checkedNavigableMap(this.nm.tailMap(param1K, true), this.keyType, this.valueType); }
    
    public NavigableMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) { return Collections.checkedNavigableMap(this.nm.subMap(param1K1, param1Boolean1, param1K2, param1Boolean2), this.keyType, this.valueType); }
    
    public NavigableMap<K, V> headMap(K param1K, boolean param1Boolean) { return Collections.checkedNavigableMap(this.nm.headMap(param1K, param1Boolean), this.keyType, this.valueType); }
    
    public NavigableMap<K, V> tailMap(K param1K, boolean param1Boolean) { return Collections.checkedNavigableMap(this.nm.tailMap(param1K, param1Boolean), this.keyType, this.valueType); }
  }
  
  static class CheckedNavigableSet<E> extends CheckedSortedSet<E> implements NavigableSet<E>, Serializable {
    private static final long serialVersionUID = -5429120189805438922L;
    
    private final NavigableSet<E> ns;
    
    CheckedNavigableSet(NavigableSet<E> param1NavigableSet, Class<E> param1Class) {
      super(param1NavigableSet, param1Class);
      this.ns = param1NavigableSet;
    }
    
    public E lower(E param1E) { return (E)this.ns.lower(param1E); }
    
    public E floor(E param1E) { return (E)this.ns.floor(param1E); }
    
    public E ceiling(E param1E) { return (E)this.ns.ceiling(param1E); }
    
    public E higher(E param1E) { return (E)this.ns.higher(param1E); }
    
    public E pollFirst() { return (E)this.ns.pollFirst(); }
    
    public E pollLast() { return (E)this.ns.pollLast(); }
    
    public NavigableSet<E> descendingSet() { return Collections.checkedNavigableSet(this.ns.descendingSet(), this.type); }
    
    public Iterator<E> descendingIterator() { return Collections.checkedNavigableSet(this.ns.descendingSet(), this.type).iterator(); }
    
    public NavigableSet<E> subSet(E param1E1, E param1E2) { return Collections.checkedNavigableSet(this.ns.subSet(param1E1, true, param1E2, false), this.type); }
    
    public NavigableSet<E> headSet(E param1E) { return Collections.checkedNavigableSet(this.ns.headSet(param1E, false), this.type); }
    
    public NavigableSet<E> tailSet(E param1E) { return Collections.checkedNavigableSet(this.ns.tailSet(param1E, true), this.type); }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) { return Collections.checkedNavigableSet(this.ns.subSet(param1E1, param1Boolean1, param1E2, param1Boolean2), this.type); }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) { return Collections.checkedNavigableSet(this.ns.headSet(param1E, param1Boolean), this.type); }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) { return Collections.checkedNavigableSet(this.ns.tailSet(param1E, param1Boolean), this.type); }
  }
  
  static class CheckedQueue<E> extends CheckedCollection<E> implements Queue<E>, Serializable {
    private static final long serialVersionUID = 1433151992604707767L;
    
    final Queue<E> queue;
    
    CheckedQueue(Queue<E> param1Queue, Class<E> param1Class) {
      super(param1Queue, param1Class);
      this.queue = param1Queue;
    }
    
    public E element() { return (E)this.queue.element(); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.c.equals(param1Object)); }
    
    public int hashCode() { return this.c.hashCode(); }
    
    public E peek() { return (E)this.queue.peek(); }
    
    public E poll() { return (E)this.queue.poll(); }
    
    public E remove() { return (E)this.queue.remove(); }
    
    public boolean offer(E param1E) { return this.queue.offer(typeCheck(param1E)); }
  }
  
  static class CheckedRandomAccessList<E> extends CheckedList<E> implements RandomAccess {
    private static final long serialVersionUID = 1638200125423088369L;
    
    CheckedRandomAccessList(List<E> param1List, Class<E> param1Class) { super(param1List, param1Class); }
    
    public List<E> subList(int param1Int1, int param1Int2) { return new CheckedRandomAccessList(this.list.subList(param1Int1, param1Int2), this.type); }
  }
  
  static class CheckedSet<E> extends CheckedCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = 4694047833775013803L;
    
    CheckedSet(Set<E> param1Set, Class<E> param1Class) { super(param1Set, param1Class); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.c.equals(param1Object)); }
    
    public int hashCode() { return this.c.hashCode(); }
  }
  
  static class CheckedSortedMap<K, V> extends CheckedMap<K, V> implements SortedMap<K, V>, Serializable {
    private static final long serialVersionUID = 1599671320688067438L;
    
    private final SortedMap<K, V> sm;
    
    CheckedSortedMap(SortedMap<K, V> param1SortedMap, Class<K> param1Class1, Class<V> param1Class2) {
      super(param1SortedMap, param1Class1, param1Class2);
      this.sm = param1SortedMap;
    }
    
    public Comparator<? super K> comparator() { return this.sm.comparator(); }
    
    public K firstKey() { return (K)this.sm.firstKey(); }
    
    public K lastKey() { return (K)this.sm.lastKey(); }
    
    public SortedMap<K, V> subMap(K param1K1, K param1K2) { return Collections.checkedSortedMap(this.sm.subMap(param1K1, param1K2), this.keyType, this.valueType); }
    
    public SortedMap<K, V> headMap(K param1K) { return Collections.checkedSortedMap(this.sm.headMap(param1K), this.keyType, this.valueType); }
    
    public SortedMap<K, V> tailMap(K param1K) { return Collections.checkedSortedMap(this.sm.tailMap(param1K), this.keyType, this.valueType); }
  }
  
  static class CheckedSortedSet<E> extends CheckedSet<E> implements SortedSet<E>, Serializable {
    private static final long serialVersionUID = 1599911165492914959L;
    
    private final SortedSet<E> ss;
    
    CheckedSortedSet(SortedSet<E> param1SortedSet, Class<E> param1Class) {
      super(param1SortedSet, param1Class);
      this.ss = param1SortedSet;
    }
    
    public Comparator<? super E> comparator() { return this.ss.comparator(); }
    
    public E first() { return (E)this.ss.first(); }
    
    public E last() { return (E)this.ss.last(); }
    
    public SortedSet<E> subSet(E param1E1, E param1E2) { return Collections.checkedSortedSet(this.ss.subSet(param1E1, param1E2), this.type); }
    
    public SortedSet<E> headSet(E param1E) { return Collections.checkedSortedSet(this.ss.headSet(param1E), this.type); }
    
    public SortedSet<E> tailSet(E param1E) { return Collections.checkedSortedSet(this.ss.tailSet(param1E), this.type); }
  }
  
  private static class CopiesList<E> extends AbstractList<E> implements RandomAccess, Serializable {
    private static final long serialVersionUID = 2739099268398711800L;
    
    final int n;
    
    final E element;
    
    CopiesList(int param1Int, E param1E) {
      assert param1Int >= 0;
      this.n = param1Int;
      this.element = param1E;
    }
    
    public int size() { return this.n; }
    
    public boolean contains(Object param1Object) { return (this.n != 0 && Collections.eq(param1Object, this.element)); }
    
    public int indexOf(Object param1Object) { return contains(param1Object) ? 0 : -1; }
    
    public int lastIndexOf(Object param1Object) { return contains(param1Object) ? (this.n - 1) : -1; }
    
    public E get(int param1Int) {
      if (param1Int < 0 || param1Int >= this.n)
        throw new IndexOutOfBoundsException("Index: " + param1Int + ", Size: " + this.n); 
      return (E)this.element;
    }
    
    public Object[] toArray() {
      Object[] arrayOfObject = new Object[this.n];
      if (this.element != null)
        Arrays.fill(arrayOfObject, 0, this.n, this.element); 
      return arrayOfObject;
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      int i = this.n;
      if (param1ArrayOfT.length < i) {
        param1ArrayOfT = (T[])(Object[])Array.newInstance(param1ArrayOfT.getClass().getComponentType(), i);
        if (this.element != null)
          Arrays.fill(param1ArrayOfT, 0, i, this.element); 
      } else {
        Arrays.fill(param1ArrayOfT, 0, i, this.element);
        if (param1ArrayOfT.length > i)
          param1ArrayOfT[i] = null; 
      } 
      return param1ArrayOfT;
    }
    
    public List<E> subList(int param1Int1, int param1Int2) {
      if (param1Int1 < 0)
        throw new IndexOutOfBoundsException("fromIndex = " + param1Int1); 
      if (param1Int2 > this.n)
        throw new IndexOutOfBoundsException("toIndex = " + param1Int2); 
      if (param1Int1 > param1Int2)
        throw new IllegalArgumentException("fromIndex(" + param1Int1 + ") > toIndex(" + param1Int2 + ")"); 
      return new CopiesList(param1Int2 - param1Int1, this.element);
    }
    
    public Stream<E> stream() { return IntStream.range(0, this.n).mapToObj(param1Int -> this.element); }
    
    public Stream<E> parallelStream() { return IntStream.range(0, this.n).parallel().mapToObj(param1Int -> this.element); }
    
    public Spliterator<E> spliterator() { return stream().spliterator(); }
  }
  
  private static class EmptyEnumeration<E> extends Object implements Enumeration<E> {
    static final EmptyEnumeration<Object> EMPTY_ENUMERATION = new EmptyEnumeration();
    
    public boolean hasMoreElements() { return false; }
    
    public E nextElement() { throw new NoSuchElementException(); }
  }
  
  private static class EmptyIterator<E> extends Object implements Iterator<E> {
    static final EmptyIterator<Object> EMPTY_ITERATOR = new EmptyIterator();
    
    private EmptyIterator() {}
    
    public boolean hasNext() { return false; }
    
    public E next() { throw new NoSuchElementException(); }
    
    public void remove() { throw new IllegalStateException(); }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) { Objects.requireNonNull(param1Consumer); }
  }
  
  private static class EmptyList<E> extends AbstractList<E> implements RandomAccess, Serializable {
    private static final long serialVersionUID = 8842843931221139166L;
    
    private EmptyList() {}
    
    public Iterator<E> iterator() { return Collections.emptyIterator(); }
    
    public ListIterator<E> listIterator() { return Collections.emptyListIterator(); }
    
    public int size() { return 0; }
    
    public boolean isEmpty() { return true; }
    
    public boolean contains(Object param1Object) { return false; }
    
    public boolean containsAll(Collection<?> param1Collection) { return param1Collection.isEmpty(); }
    
    public Object[] toArray() { return new Object[0]; }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      if (param1ArrayOfT.length > 0)
        param1ArrayOfT[0] = null; 
      return param1ArrayOfT;
    }
    
    public E get(int param1Int) { throw new IndexOutOfBoundsException("Index: " + param1Int); }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof List && ((List)param1Object).isEmpty()); }
    
    public int hashCode() { return 1; }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) {
      Objects.requireNonNull(param1Predicate);
      return false;
    }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) { Objects.requireNonNull(param1UnaryOperator); }
    
    public void sort(Comparator<? super E> param1Comparator) {}
    
    public void forEach(Consumer<? super E> param1Consumer) { Objects.requireNonNull(param1Consumer); }
    
    public Spliterator<E> spliterator() { return Spliterators.emptySpliterator(); }
    
    private Object readResolve() { return Collections.EMPTY_LIST; }
  }
  
  private static class EmptyListIterator<E> extends EmptyIterator<E> implements ListIterator<E> {
    static final EmptyListIterator<Object> EMPTY_ITERATOR = new EmptyListIterator();
    
    private EmptyListIterator() { super(null); }
    
    public boolean hasPrevious() { return false; }
    
    public E previous() { throw new NoSuchElementException(); }
    
    public int nextIndex() { return 0; }
    
    public int previousIndex() { return -1; }
    
    public void set(E param1E) { throw new IllegalStateException(); }
    
    public void add(E param1E) { throw new UnsupportedOperationException(); }
  }
  
  private static class EmptyMap<K, V> extends AbstractMap<K, V> implements Serializable {
    private static final long serialVersionUID = 6428348081105594320L;
    
    private EmptyMap() {}
    
    public int size() { return 0; }
    
    public boolean isEmpty() { return true; }
    
    public boolean containsKey(Object param1Object) { return false; }
    
    public boolean containsValue(Object param1Object) { return false; }
    
    public V get(Object param1Object) { return null; }
    
    public Set<K> keySet() { return Collections.emptySet(); }
    
    public Collection<V> values() { return Collections.emptySet(); }
    
    public Set<Map.Entry<K, V>> entrySet() { return Collections.emptySet(); }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof Map && ((Map)param1Object).isEmpty()); }
    
    public int hashCode() { return 0; }
    
    public V getOrDefault(Object param1Object, V param1V) { return param1V; }
    
    public void forEach(BiConsumer<? super K, ? super V> param1BiConsumer) { Objects.requireNonNull(param1BiConsumer); }
    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { Objects.requireNonNull(param1BiFunction); }
    
    public V putIfAbsent(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public boolean remove(Object param1Object1, Object param1Object2) { throw new UnsupportedOperationException(); }
    
    public boolean replace(K param1K, V param1V1, V param1V2) { throw new UnsupportedOperationException(); }
    
    public V replace(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public V computeIfAbsent(K param1K, Function<? super K, ? extends V> param1Function) { throw new UnsupportedOperationException(); }
    
    public V computeIfPresent(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V compute(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V merge(K param1K, V param1V, BiFunction<? super V, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    private Object readResolve() { return Collections.EMPTY_MAP; }
  }
  
  private static class EmptySet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 1582296315990362920L;
    
    private EmptySet() {}
    
    public Iterator<E> iterator() { return Collections.emptyIterator(); }
    
    public int size() { return 0; }
    
    public boolean isEmpty() { return true; }
    
    public boolean contains(Object param1Object) { return false; }
    
    public boolean containsAll(Collection<?> param1Collection) { return param1Collection.isEmpty(); }
    
    public Object[] toArray() { return new Object[0]; }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      if (param1ArrayOfT.length > 0)
        param1ArrayOfT[0] = null; 
      return param1ArrayOfT;
    }
    
    public void forEach(Consumer<? super E> param1Consumer) { Objects.requireNonNull(param1Consumer); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) {
      Objects.requireNonNull(param1Predicate);
      return false;
    }
    
    public Spliterator<E> spliterator() { return Spliterators.emptySpliterator(); }
    
    private Object readResolve() { return Collections.EMPTY_SET; }
  }
  
  private static class ReverseComparator extends Object implements Comparator<Comparable<Object>>, Serializable {
    private static final long serialVersionUID = 7207038068494060240L;
    
    static final ReverseComparator REVERSE_ORDER = new ReverseComparator();
    
    public int compare(Comparable<Object> param1Comparable1, Comparable<Object> param1Comparable2) { return param1Comparable2.compareTo(param1Comparable1); }
    
    private Object readResolve() { return Collections.reverseOrder(); }
    
    public Comparator<Comparable<Object>> reversed() { return Comparator.naturalOrder(); }
  }
  
  private static class ReverseComparator2<T> extends Object implements Comparator<T>, Serializable {
    private static final long serialVersionUID = 4374092139857L;
    
    final Comparator<T> cmp;
    
    ReverseComparator2(Comparator<T> param1Comparator) {
      assert param1Comparator != null;
      this.cmp = param1Comparator;
    }
    
    public int compare(T param1T1, T param1T2) { return this.cmp.compare(param1T2, param1T1); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || (param1Object instanceof ReverseComparator2 && this.cmp.equals(((ReverseComparator2)param1Object).cmp))); }
    
    public int hashCode() { return this.cmp.hashCode() ^ 0x80000000; }
    
    public Comparator<T> reversed() { return this.cmp; }
  }
  
  private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable {
    private final Map<E, Boolean> m;
    
    private Set<E> s;
    
    private static final long serialVersionUID = 2454657854757543876L;
    
    SetFromMap(Map<E, Boolean> param1Map) {
      if (!param1Map.isEmpty())
        throw new IllegalArgumentException("Map is non-empty"); 
      this.m = param1Map;
      this.s = param1Map.keySet();
    }
    
    public void clear() { this.m.clear(); }
    
    public int size() { return this.m.size(); }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public boolean contains(Object param1Object) { return this.m.containsKey(param1Object); }
    
    public boolean remove(Object param1Object) { return (this.m.remove(param1Object) != null); }
    
    public boolean add(E param1E) { return (this.m.put(param1E, Boolean.TRUE) == null); }
    
    public Iterator<E> iterator() { return this.s.iterator(); }
    
    public Object[] toArray() { return this.s.toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])this.s.toArray(param1ArrayOfT); }
    
    public String toString() { return this.s.toString(); }
    
    public int hashCode() { return this.s.hashCode(); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.s.equals(param1Object)); }
    
    public boolean containsAll(Collection<?> param1Collection) { return this.s.containsAll(param1Collection); }
    
    public boolean removeAll(Collection<?> param1Collection) { return this.s.removeAll(param1Collection); }
    
    public boolean retainAll(Collection<?> param1Collection) { return this.s.retainAll(param1Collection); }
    
    public void forEach(Consumer<? super E> param1Consumer) { this.s.forEach(param1Consumer); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) { return this.s.removeIf(param1Predicate); }
    
    public Spliterator<E> spliterator() { return this.s.spliterator(); }
    
    public Stream<E> stream() { return this.s.stream(); }
    
    public Stream<E> parallelStream() { return this.s.parallelStream(); }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws IOException, ClassNotFoundException {
      param1ObjectInputStream.defaultReadObject();
      this.s = this.m.keySet();
    }
  }
  
  private static class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
    private static final long serialVersionUID = 3093736618740652951L;
    
    private final E element;
    
    SingletonList(E param1E) { this.element = param1E; }
    
    public Iterator<E> iterator() { return Collections.singletonIterator(this.element); }
    
    public int size() { return 1; }
    
    public boolean contains(Object param1Object) { return Collections.eq(param1Object, this.element); }
    
    public E get(int param1Int) {
      if (param1Int != 0)
        throw new IndexOutOfBoundsException("Index: " + param1Int + ", Size: 1"); 
      return (E)this.element;
    }
    
    public void forEach(Consumer<? super E> param1Consumer) { param1Consumer.accept(this.element); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) { throw new UnsupportedOperationException(); }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) { throw new UnsupportedOperationException(); }
    
    public void sort(Comparator<? super E> param1Comparator) {}
    
    public Spliterator<E> spliterator() { return Collections.singletonSpliterator(this.element); }
  }
  
  private static class SingletonMap<K, V> extends AbstractMap<K, V> implements Serializable {
    private static final long serialVersionUID = -6979724477215052911L;
    
    private final K k;
    
    private final V v;
    
    private Set<K> keySet;
    
    private Set<Map.Entry<K, V>> entrySet;
    
    private Collection<V> values;
    
    SingletonMap(K param1K, V param1V) {
      this.k = param1K;
      this.v = param1V;
    }
    
    public int size() { return 1; }
    
    public boolean isEmpty() { return false; }
    
    public boolean containsKey(Object param1Object) { return Collections.eq(param1Object, this.k); }
    
    public boolean containsValue(Object param1Object) { return Collections.eq(param1Object, this.v); }
    
    public V get(Object param1Object) { return (V)(Collections.eq(param1Object, this.k) ? this.v : null); }
    
    public Set<K> keySet() {
      if (this.keySet == null)
        this.keySet = Collections.singleton(this.k); 
      return this.keySet;
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null)
        this.entrySet = Collections.singleton(new AbstractMap.SimpleImmutableEntry(this.k, this.v)); 
      return this.entrySet;
    }
    
    public Collection<V> values() {
      if (this.values == null)
        this.values = Collections.singleton(this.v); 
      return this.values;
    }
    
    public V getOrDefault(Object param1Object, V param1V) { return (V)(Collections.eq(param1Object, this.k) ? this.v : param1V); }
    
    public void forEach(BiConsumer<? super K, ? super V> param1BiConsumer) { param1BiConsumer.accept(this.k, this.v); }
    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V putIfAbsent(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public boolean remove(Object param1Object1, Object param1Object2) { throw new UnsupportedOperationException(); }
    
    public boolean replace(K param1K, V param1V1, V param1V2) { throw new UnsupportedOperationException(); }
    
    public V replace(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public V computeIfAbsent(K param1K, Function<? super K, ? extends V> param1Function) { throw new UnsupportedOperationException(); }
    
    public V computeIfPresent(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V compute(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V merge(K param1K, V param1V, BiFunction<? super V, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
  }
  
  private static class SingletonSet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 3193687207550431679L;
    
    private final E element;
    
    SingletonSet(E param1E) { this.element = param1E; }
    
    public Iterator<E> iterator() { return Collections.singletonIterator(this.element); }
    
    public int size() { return 1; }
    
    public boolean contains(Object param1Object) { return Collections.eq(param1Object, this.element); }
    
    public void forEach(Consumer<? super E> param1Consumer) { param1Consumer.accept(this.element); }
    
    public Spliterator<E> spliterator() { return Collections.singletonSpliterator(this.element); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) { throw new UnsupportedOperationException(); }
  }
  
  static class SynchronizedCollection<E> extends Object implements Collection<E>, Serializable {
    private static final long serialVersionUID = 3053995032091335093L;
    
    final Collection<E> c;
    
    final Object mutex;
    
    SynchronizedCollection(Collection<E> param1Collection) {
      this.c = (Collection)Objects.requireNonNull(param1Collection);
      this.mutex = this;
    }
    
    SynchronizedCollection(Collection<E> param1Collection, Object param1Object) {
      this.c = (Collection)Objects.requireNonNull(param1Collection);
      this.mutex = Objects.requireNonNull(param1Object);
    }
    
    public int size() {
      synchronized (this.mutex) {
        return this.c.size();
      } 
    }
    
    public boolean isEmpty() {
      synchronized (this.mutex) {
        return this.c.isEmpty();
      } 
    }
    
    public boolean contains(Object param1Object) {
      synchronized (this.mutex) {
        return this.c.contains(param1Object);
      } 
    }
    
    public Object[] toArray() {
      synchronized (this.mutex) {
        return this.c.toArray();
      } 
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      synchronized (this.mutex) {
        return (T[])this.c.toArray(param1ArrayOfT);
      } 
    }
    
    public Iterator<E> iterator() { return this.c.iterator(); }
    
    public boolean add(E param1E) {
      synchronized (this.mutex) {
        return this.c.add(param1E);
      } 
    }
    
    public boolean remove(Object param1Object) {
      synchronized (this.mutex) {
        return this.c.remove(param1Object);
      } 
    }
    
    public boolean containsAll(Collection<?> param1Collection) {
      synchronized (this.mutex) {
        return this.c.containsAll(param1Collection);
      } 
    }
    
    public boolean addAll(Collection<? extends E> param1Collection) {
      synchronized (this.mutex) {
        return this.c.addAll(param1Collection);
      } 
    }
    
    public boolean removeAll(Collection<?> param1Collection) {
      synchronized (this.mutex) {
        return this.c.removeAll(param1Collection);
      } 
    }
    
    public boolean retainAll(Collection<?> param1Collection) {
      synchronized (this.mutex) {
        return this.c.retainAll(param1Collection);
      } 
    }
    
    public void clear() {
      synchronized (this.mutex) {
        this.c.clear();
      } 
    }
    
    public String toString() {
      synchronized (this.mutex) {
        return this.c.toString();
      } 
    }
    
    public void forEach(Consumer<? super E> param1Consumer) {
      synchronized (this.mutex) {
        this.c.forEach(param1Consumer);
      } 
    }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) {
      synchronized (this.mutex) {
        return this.c.removeIf(param1Predicate);
      } 
    }
    
    public Spliterator<E> spliterator() { return this.c.spliterator(); }
    
    public Stream<E> stream() { return this.c.stream(); }
    
    public Stream<E> parallelStream() { return this.c.parallelStream(); }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      synchronized (this.mutex) {
        param1ObjectOutputStream.defaultWriteObject();
      } 
    }
  }
  
  static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
    private static final long serialVersionUID = -7754090372962971524L;
    
    final List<E> list;
    
    SynchronizedList(List<E> param1List) {
      super(param1List);
      this.list = param1List;
    }
    
    SynchronizedList(List<E> param1List, Object param1Object) {
      super(param1List, param1Object);
      this.list = param1List;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      synchronized (this.mutex) {
        return this.list.equals(param1Object);
      } 
    }
    
    public int hashCode() {
      synchronized (this.mutex) {
        return this.list.hashCode();
      } 
    }
    
    public E get(int param1Int) {
      synchronized (this.mutex) {
        return (E)this.list.get(param1Int);
      } 
    }
    
    public E set(int param1Int, E param1E) {
      synchronized (this.mutex) {
        return (E)this.list.set(param1Int, param1E);
      } 
    }
    
    public void add(int param1Int, E param1E) {
      synchronized (this.mutex) {
        this.list.add(param1Int, param1E);
      } 
    }
    
    public E remove(int param1Int) {
      synchronized (this.mutex) {
        return (E)this.list.remove(param1Int);
      } 
    }
    
    public int indexOf(Object param1Object) {
      synchronized (this.mutex) {
        return this.list.indexOf(param1Object);
      } 
    }
    
    public int lastIndexOf(Object param1Object) {
      synchronized (this.mutex) {
        return this.list.lastIndexOf(param1Object);
      } 
    }
    
    public boolean addAll(int param1Int, Collection<? extends E> param1Collection) {
      synchronized (this.mutex) {
        return this.list.addAll(param1Int, param1Collection);
      } 
    }
    
    public ListIterator<E> listIterator() { return this.list.listIterator(); }
    
    public ListIterator<E> listIterator(int param1Int) { return this.list.listIterator(param1Int); }
    
    public List<E> subList(int param1Int1, int param1Int2) {
      synchronized (this.mutex) {
        return new SynchronizedList(this.list.subList(param1Int1, param1Int2), this.mutex);
      } 
    }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) {
      synchronized (this.mutex) {
        this.list.replaceAll(param1UnaryOperator);
      } 
    }
    
    public void sort(Comparator<? super E> param1Comparator) {
      synchronized (this.mutex) {
        this.list.sort(param1Comparator);
      } 
    }
    
    private Object readResolve() { return (this.list instanceof RandomAccess) ? new Collections.SynchronizedRandomAccessList(this.list) : this; }
  }
  
  private static class SynchronizedMap<K, V> extends Object implements Map<K, V>, Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    
    private final Map<K, V> m;
    
    final Object mutex;
    
    private Set<K> keySet;
    
    private Set<Map.Entry<K, V>> entrySet;
    
    private Collection<V> values;
    
    SynchronizedMap(Map<K, V> param1Map) {
      this.m = (Map)Objects.requireNonNull(param1Map);
      this.mutex = this;
    }
    
    SynchronizedMap(Map<K, V> param1Map, Object param1Object) {
      this.m = param1Map;
      this.mutex = param1Object;
    }
    
    public int size() {
      synchronized (this.mutex) {
        return this.m.size();
      } 
    }
    
    public boolean isEmpty() {
      synchronized (this.mutex) {
        return this.m.isEmpty();
      } 
    }
    
    public boolean containsKey(Object param1Object) {
      synchronized (this.mutex) {
        return this.m.containsKey(param1Object);
      } 
    }
    
    public boolean containsValue(Object param1Object) {
      synchronized (this.mutex) {
        return this.m.containsValue(param1Object);
      } 
    }
    
    public V get(Object param1Object) {
      synchronized (this.mutex) {
        return (V)this.m.get(param1Object);
      } 
    }
    
    public V put(K param1K, V param1V) {
      synchronized (this.mutex) {
        return (V)this.m.put(param1K, param1V);
      } 
    }
    
    public V remove(Object param1Object) {
      synchronized (this.mutex) {
        return (V)this.m.remove(param1Object);
      } 
    }
    
    public void putAll(Map<? extends K, ? extends V> param1Map) {
      synchronized (this.mutex) {
        this.m.putAll(param1Map);
      } 
    }
    
    public void clear() {
      synchronized (this.mutex) {
        this.m.clear();
      } 
    }
    
    public Set<K> keySet() {
      synchronized (this.mutex) {
        if (this.keySet == null)
          this.keySet = new Collections.SynchronizedSet(this.m.keySet(), this.mutex); 
        return this.keySet;
      } 
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      synchronized (this.mutex) {
        if (this.entrySet == null)
          this.entrySet = new Collections.SynchronizedSet(this.m.entrySet(), this.mutex); 
        return this.entrySet;
      } 
    }
    
    public Collection<V> values() {
      synchronized (this.mutex) {
        if (this.values == null)
          this.values = new Collections.SynchronizedCollection(this.m.values(), this.mutex); 
        return this.values;
      } 
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      synchronized (this.mutex) {
        return this.m.equals(param1Object);
      } 
    }
    
    public int hashCode() {
      synchronized (this.mutex) {
        return this.m.hashCode();
      } 
    }
    
    public String toString() {
      synchronized (this.mutex) {
        return this.m.toString();
      } 
    }
    
    public V getOrDefault(Object param1Object, V param1V) {
      synchronized (this.mutex) {
        return (V)this.m.getOrDefault(param1Object, param1V);
      } 
    }
    
    public void forEach(BiConsumer<? super K, ? super V> param1BiConsumer) {
      synchronized (this.mutex) {
        this.m.forEach(param1BiConsumer);
      } 
    }
    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> param1BiFunction) {
      synchronized (this.mutex) {
        this.m.replaceAll(param1BiFunction);
      } 
    }
    
    public V putIfAbsent(K param1K, V param1V) {
      synchronized (this.mutex) {
        return (V)this.m.putIfAbsent(param1K, param1V);
      } 
    }
    
    public boolean remove(Object param1Object1, Object param1Object2) {
      synchronized (this.mutex) {
        return this.m.remove(param1Object1, param1Object2);
      } 
    }
    
    public boolean replace(K param1K, V param1V1, V param1V2) {
      synchronized (this.mutex) {
        return this.m.replace(param1K, param1V1, param1V2);
      } 
    }
    
    public V replace(K param1K, V param1V) {
      synchronized (this.mutex) {
        return (V)this.m.replace(param1K, param1V);
      } 
    }
    
    public V computeIfAbsent(K param1K, Function<? super K, ? extends V> param1Function) {
      synchronized (this.mutex) {
        return (V)this.m.computeIfAbsent(param1K, param1Function);
      } 
    }
    
    public V computeIfPresent(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) {
      synchronized (this.mutex) {
        return (V)this.m.computeIfPresent(param1K, param1BiFunction);
      } 
    }
    
    public V compute(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) {
      synchronized (this.mutex) {
        return (V)this.m.compute(param1K, param1BiFunction);
      } 
    }
    
    public V merge(K param1K, V param1V, BiFunction<? super V, ? super V, ? extends V> param1BiFunction) {
      synchronized (this.mutex) {
        return (V)this.m.merge(param1K, param1V, param1BiFunction);
      } 
    }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      synchronized (this.mutex) {
        param1ObjectOutputStream.defaultWriteObject();
      } 
    }
  }
  
  static class SynchronizedNavigableMap<K, V> extends SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {
    private static final long serialVersionUID = 699392247599746807L;
    
    private final NavigableMap<K, V> nm;
    
    SynchronizedNavigableMap(NavigableMap<K, V> param1NavigableMap) {
      super(param1NavigableMap);
      this.nm = param1NavigableMap;
    }
    
    SynchronizedNavigableMap(NavigableMap<K, V> param1NavigableMap, Object param1Object) {
      super(param1NavigableMap, param1Object);
      this.nm = param1NavigableMap;
    }
    
    public Map.Entry<K, V> lowerEntry(K param1K) {
      synchronized (this.mutex) {
        return this.nm.lowerEntry(param1K);
      } 
    }
    
    public K lowerKey(K param1K) {
      synchronized (this.mutex) {
        return (K)this.nm.lowerKey(param1K);
      } 
    }
    
    public Map.Entry<K, V> floorEntry(K param1K) {
      synchronized (this.mutex) {
        return this.nm.floorEntry(param1K);
      } 
    }
    
    public K floorKey(K param1K) {
      synchronized (this.mutex) {
        return (K)this.nm.floorKey(param1K);
      } 
    }
    
    public Map.Entry<K, V> ceilingEntry(K param1K) {
      synchronized (this.mutex) {
        return this.nm.ceilingEntry(param1K);
      } 
    }
    
    public K ceilingKey(K param1K) {
      synchronized (this.mutex) {
        return (K)this.nm.ceilingKey(param1K);
      } 
    }
    
    public Map.Entry<K, V> higherEntry(K param1K) {
      synchronized (this.mutex) {
        return this.nm.higherEntry(param1K);
      } 
    }
    
    public K higherKey(K param1K) {
      synchronized (this.mutex) {
        return (K)this.nm.higherKey(param1K);
      } 
    }
    
    public Map.Entry<K, V> firstEntry() {
      synchronized (this.mutex) {
        return this.nm.firstEntry();
      } 
    }
    
    public Map.Entry<K, V> lastEntry() {
      synchronized (this.mutex) {
        return this.nm.lastEntry();
      } 
    }
    
    public Map.Entry<K, V> pollFirstEntry() {
      synchronized (this.mutex) {
        return this.nm.pollFirstEntry();
      } 
    }
    
    public Map.Entry<K, V> pollLastEntry() {
      synchronized (this.mutex) {
        return this.nm.pollLastEntry();
      } 
    }
    
    public NavigableMap<K, V> descendingMap() {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.descendingMap(), this.mutex);
      } 
    }
    
    public NavigableSet<K> keySet() { return navigableKeySet(); }
    
    public NavigableSet<K> navigableKeySet() {
      synchronized (this.mutex) {
        return new Collections.SynchronizedNavigableSet(this.nm.navigableKeySet(), this.mutex);
      } 
    }
    
    public NavigableSet<K> descendingKeySet() {
      synchronized (this.mutex) {
        return new Collections.SynchronizedNavigableSet(this.nm.descendingKeySet(), this.mutex);
      } 
    }
    
    public SortedMap<K, V> subMap(K param1K1, K param1K2) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.subMap(param1K1, true, param1K2, false), this.mutex);
      } 
    }
    
    public SortedMap<K, V> headMap(K param1K) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.headMap(param1K, false), this.mutex);
      } 
    }
    
    public SortedMap<K, V> tailMap(K param1K) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.tailMap(param1K, true), this.mutex);
      } 
    }
    
    public NavigableMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.subMap(param1K1, param1Boolean1, param1K2, param1Boolean2), this.mutex);
      } 
    }
    
    public NavigableMap<K, V> headMap(K param1K, boolean param1Boolean) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.headMap(param1K, param1Boolean), this.mutex);
      } 
    }
    
    public NavigableMap<K, V> tailMap(K param1K, boolean param1Boolean) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableMap(this.nm.tailMap(param1K, param1Boolean), this.mutex);
      } 
    }
  }
  
  static class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E> {
    private static final long serialVersionUID = -5505529816273629798L;
    
    private final NavigableSet<E> ns;
    
    SynchronizedNavigableSet(NavigableSet<E> param1NavigableSet) {
      super(param1NavigableSet);
      this.ns = param1NavigableSet;
    }
    
    SynchronizedNavigableSet(NavigableSet<E> param1NavigableSet, Object param1Object) {
      super(param1NavigableSet, param1Object);
      this.ns = param1NavigableSet;
    }
    
    public E lower(E param1E) {
      synchronized (this.mutex) {
        return (E)this.ns.lower(param1E);
      } 
    }
    
    public E floor(E param1E) {
      synchronized (this.mutex) {
        return (E)this.ns.floor(param1E);
      } 
    }
    
    public E ceiling(E param1E) {
      synchronized (this.mutex) {
        return (E)this.ns.ceiling(param1E);
      } 
    }
    
    public E higher(E param1E) {
      synchronized (this.mutex) {
        return (E)this.ns.higher(param1E);
      } 
    }
    
    public E pollFirst() {
      synchronized (this.mutex) {
        return (E)this.ns.pollFirst();
      } 
    }
    
    public E pollLast() {
      synchronized (this.mutex) {
        return (E)this.ns.pollLast();
      } 
    }
    
    public NavigableSet<E> descendingSet() {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.descendingSet(), this.mutex);
      } 
    }
    
    public Iterator<E> descendingIterator() {
      synchronized (this.mutex) {
        return descendingSet().iterator();
      } 
    }
    
    public NavigableSet<E> subSet(E param1E1, E param1E2) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.subSet(param1E1, true, param1E2, false), this.mutex);
      } 
    }
    
    public NavigableSet<E> headSet(E param1E) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.headSet(param1E, false), this.mutex);
      } 
    }
    
    public NavigableSet<E> tailSet(E param1E) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.tailSet(param1E, true), this.mutex);
      } 
    }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.subSet(param1E1, param1Boolean1, param1E2, param1Boolean2), this.mutex);
      } 
    }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.headSet(param1E, param1Boolean), this.mutex);
      } 
    }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) {
      synchronized (this.mutex) {
        return new SynchronizedNavigableSet(this.ns.tailSet(param1E, param1Boolean), this.mutex);
      } 
    }
  }
  
  static class SynchronizedRandomAccessList<E> extends SynchronizedList<E> implements RandomAccess {
    private static final long serialVersionUID = 1530674583602358482L;
    
    SynchronizedRandomAccessList(List<E> param1List) { super(param1List); }
    
    SynchronizedRandomAccessList(List<E> param1List, Object param1Object) { super(param1List, param1Object); }
    
    public List<E> subList(int param1Int1, int param1Int2) {
      synchronized (this.mutex) {
        return new SynchronizedRandomAccessList(this.list.subList(param1Int1, param1Int2), this.mutex);
      } 
    }
    
    private Object writeReplace() { return new Collections.SynchronizedList(this.list); }
  }
  
  static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
    private static final long serialVersionUID = 487447009682186044L;
    
    SynchronizedSet(Set<E> param1Set) { super(param1Set); }
    
    SynchronizedSet(Set<E> param1Set, Object param1Object) { super(param1Set, param1Object); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      synchronized (this.mutex) {
        return this.c.equals(param1Object);
      } 
    }
    
    public int hashCode() {
      synchronized (this.mutex) {
        return this.c.hashCode();
      } 
    }
  }
  
  static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
    private static final long serialVersionUID = -8798146769416483793L;
    
    private final SortedMap<K, V> sm;
    
    SynchronizedSortedMap(SortedMap<K, V> param1SortedMap) {
      super(param1SortedMap);
      this.sm = param1SortedMap;
    }
    
    SynchronizedSortedMap(SortedMap<K, V> param1SortedMap, Object param1Object) {
      super(param1SortedMap, param1Object);
      this.sm = param1SortedMap;
    }
    
    public Comparator<? super K> comparator() {
      synchronized (this.mutex) {
        return this.sm.comparator();
      } 
    }
    
    public SortedMap<K, V> subMap(K param1K1, K param1K2) {
      synchronized (this.mutex) {
        return new SynchronizedSortedMap(this.sm.subMap(param1K1, param1K2), this.mutex);
      } 
    }
    
    public SortedMap<K, V> headMap(K param1K) {
      synchronized (this.mutex) {
        return new SynchronizedSortedMap(this.sm.headMap(param1K), this.mutex);
      } 
    }
    
    public SortedMap<K, V> tailMap(K param1K) {
      synchronized (this.mutex) {
        return new SynchronizedSortedMap(this.sm.tailMap(param1K), this.mutex);
      } 
    }
    
    public K firstKey() {
      synchronized (this.mutex) {
        return (K)this.sm.firstKey();
      } 
    }
    
    public K lastKey() {
      synchronized (this.mutex) {
        return (K)this.sm.lastKey();
      } 
    }
  }
  
  static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
    private static final long serialVersionUID = 8695801310862127406L;
    
    private final SortedSet<E> ss;
    
    SynchronizedSortedSet(SortedSet<E> param1SortedSet) {
      super(param1SortedSet);
      this.ss = param1SortedSet;
    }
    
    SynchronizedSortedSet(SortedSet<E> param1SortedSet, Object param1Object) {
      super(param1SortedSet, param1Object);
      this.ss = param1SortedSet;
    }
    
    public Comparator<? super E> comparator() {
      synchronized (this.mutex) {
        return this.ss.comparator();
      } 
    }
    
    public SortedSet<E> subSet(E param1E1, E param1E2) {
      synchronized (this.mutex) {
        return new SynchronizedSortedSet(this.ss.subSet(param1E1, param1E2), this.mutex);
      } 
    }
    
    public SortedSet<E> headSet(E param1E) {
      synchronized (this.mutex) {
        return new SynchronizedSortedSet(this.ss.headSet(param1E), this.mutex);
      } 
    }
    
    public SortedSet<E> tailSet(E param1E) {
      synchronized (this.mutex) {
        return new SynchronizedSortedSet(this.ss.tailSet(param1E), this.mutex);
      } 
    }
    
    public E first() {
      synchronized (this.mutex) {
        return (E)this.ss.first();
      } 
    }
    
    public E last() {
      synchronized (this.mutex) {
        return (E)this.ss.last();
      } 
    }
  }
  
  static class UnmodifiableCollection<E> extends Object implements Collection<E>, Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    
    final Collection<? extends E> c;
    
    UnmodifiableCollection(Collection<? extends E> param1Collection) {
      if (param1Collection == null)
        throw new NullPointerException(); 
      this.c = param1Collection;
    }
    
    public int size() { return this.c.size(); }
    
    public boolean isEmpty() { return this.c.isEmpty(); }
    
    public boolean contains(Object param1Object) { return this.c.contains(param1Object); }
    
    public Object[] toArray() { return this.c.toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])this.c.toArray(param1ArrayOfT); }
    
    public String toString() { return this.c.toString(); }
    
    public Iterator<E> iterator() { return new Iterator<E>() {
          private final Iterator<? extends E> i = Collections.UnmodifiableCollection.this.c.iterator();
          
          public boolean hasNext() { return this.i.hasNext(); }
          
          public E next() { return (E)this.i.next(); }
          
          public void remove() { throw new UnsupportedOperationException(); }
          
          public void forEachRemaining(Consumer<? super E> param2Consumer) { this.i.forEachRemaining(param2Consumer); }
        }; }
    
    public boolean add(E param1E) { throw new UnsupportedOperationException(); }
    
    public boolean remove(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public boolean containsAll(Collection<?> param1Collection) { return this.c.containsAll(param1Collection); }
    
    public boolean addAll(Collection<? extends E> param1Collection) { throw new UnsupportedOperationException(); }
    
    public boolean removeAll(Collection<?> param1Collection) { throw new UnsupportedOperationException(); }
    
    public boolean retainAll(Collection<?> param1Collection) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
    
    public void forEach(Consumer<? super E> param1Consumer) { this.c.forEach(param1Consumer); }
    
    public boolean removeIf(Predicate<? super E> param1Predicate) { throw new UnsupportedOperationException(); }
    
    public Spliterator<E> spliterator() { return this.c.spliterator(); }
    
    public Stream<E> stream() { return this.c.stream(); }
    
    public Stream<E> parallelStream() { return this.c.parallelStream(); }
  }
  
  static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
    private static final long serialVersionUID = -283967356065247728L;
    
    final List<? extends E> list;
    
    UnmodifiableList(List<? extends E> param1List) {
      super(param1List);
      this.list = param1List;
    }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.list.equals(param1Object)); }
    
    public int hashCode() { return this.list.hashCode(); }
    
    public E get(int param1Int) { return (E)this.list.get(param1Int); }
    
    public E set(int param1Int, E param1E) { throw new UnsupportedOperationException(); }
    
    public void add(int param1Int, E param1E) { throw new UnsupportedOperationException(); }
    
    public E remove(int param1Int) { throw new UnsupportedOperationException(); }
    
    public int indexOf(Object param1Object) { return this.list.indexOf(param1Object); }
    
    public int lastIndexOf(Object param1Object) { return this.list.lastIndexOf(param1Object); }
    
    public boolean addAll(int param1Int, Collection<? extends E> param1Collection) { throw new UnsupportedOperationException(); }
    
    public void replaceAll(UnaryOperator<E> param1UnaryOperator) { throw new UnsupportedOperationException(); }
    
    public void sort(Comparator<? super E> param1Comparator) { throw new UnsupportedOperationException(); }
    
    public ListIterator<E> listIterator() { return listIterator(0); }
    
    public ListIterator<E> listIterator(final int index) { return new ListIterator<E>() {
          private final ListIterator<? extends E> i = Collections.UnmodifiableList.this.list.listIterator(index);
          
          public boolean hasNext() { return this.i.hasNext(); }
          
          public E next() { return (E)this.i.next(); }
          
          public boolean hasPrevious() { return this.i.hasPrevious(); }
          
          public E previous() { return (E)this.i.previous(); }
          
          public int nextIndex() { return this.i.nextIndex(); }
          
          public int previousIndex() { return this.i.previousIndex(); }
          
          public void remove() { throw new UnsupportedOperationException(); }
          
          public void set(E param2E) { throw new UnsupportedOperationException(); }
          
          public void add(E param2E) { throw new UnsupportedOperationException(); }
          
          public void forEachRemaining(Consumer<? super E> param2Consumer) { this.i.forEachRemaining(param2Consumer); }
        }; }
    
    public List<E> subList(int param1Int1, int param1Int2) { return new UnmodifiableList(this.list.subList(param1Int1, param1Int2)); }
    
    private Object readResolve() { return (this.list instanceof RandomAccess) ? new Collections.UnmodifiableRandomAccessList(this.list) : this; }
  }
  
  private static class UnmodifiableMap<K, V> extends Object implements Map<K, V>, Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    
    private final Map<? extends K, ? extends V> m;
    
    private Set<K> keySet;
    
    private Set<Map.Entry<K, V>> entrySet;
    
    private Collection<V> values;
    
    UnmodifiableMap(Map<? extends K, ? extends V> param1Map) {
      if (param1Map == null)
        throw new NullPointerException(); 
      this.m = param1Map;
    }
    
    public int size() { return this.m.size(); }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public boolean containsKey(Object param1Object) { return this.m.containsKey(param1Object); }
    
    public boolean containsValue(Object param1Object) { return this.m.containsValue(param1Object); }
    
    public V get(Object param1Object) { return (V)this.m.get(param1Object); }
    
    public V put(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public V remove(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public void putAll(Map<? extends K, ? extends V> param1Map) { throw new UnsupportedOperationException(); }
    
    public void clear() { throw new UnsupportedOperationException(); }
    
    public Set<K> keySet() {
      if (this.keySet == null)
        this.keySet = Collections.unmodifiableSet(this.m.keySet()); 
      return this.keySet;
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null)
        this.entrySet = new UnmodifiableEntrySet(this.m.entrySet()); 
      return this.entrySet;
    }
    
    public Collection<V> values() {
      if (this.values == null)
        this.values = Collections.unmodifiableCollection(this.m.values()); 
      return this.values;
    }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.m.equals(param1Object)); }
    
    public int hashCode() { return this.m.hashCode(); }
    
    public String toString() { return this.m.toString(); }
    
    public V getOrDefault(Object param1Object, V param1V) { return (V)this.m.getOrDefault(param1Object, param1V); }
    
    public void forEach(BiConsumer<? super K, ? super V> param1BiConsumer) { this.m.forEach(param1BiConsumer); }
    
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V putIfAbsent(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public boolean remove(Object param1Object1, Object param1Object2) { throw new UnsupportedOperationException(); }
    
    public boolean replace(K param1K, V param1V1, V param1V2) { throw new UnsupportedOperationException(); }
    
    public V replace(K param1K, V param1V) { throw new UnsupportedOperationException(); }
    
    public V computeIfAbsent(K param1K, Function<? super K, ? extends V> param1Function) { throw new UnsupportedOperationException(); }
    
    public V computeIfPresent(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V compute(K param1K, BiFunction<? super K, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    public V merge(K param1K, V param1V, BiFunction<? super V, ? super V, ? extends V> param1BiFunction) { throw new UnsupportedOperationException(); }
    
    static class UnmodifiableEntrySet<K, V> extends Collections.UnmodifiableSet<Map.Entry<K, V>> {
      private static final long serialVersionUID = 7854390611657943733L;
      
      UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> param2Set) { super(param2Set); }
      
      static <K, V> Consumer<Map.Entry<K, V>> entryConsumer(Consumer<? super Map.Entry<K, V>> param2Consumer) { return param2Entry -> param2Consumer.accept(new UnmodifiableEntry(param2Entry)); }
      
      public void forEach(Consumer<? super Map.Entry<K, V>> param2Consumer) {
        Objects.requireNonNull(param2Consumer);
        this.c.forEach(entryConsumer(param2Consumer));
      }
      
      public Spliterator<Map.Entry<K, V>> spliterator() { return new UnmodifiableEntrySetSpliterator(this.c.spliterator()); }
      
      public Stream<Map.Entry<K, V>> stream() { return StreamSupport.stream(spliterator(), false); }
      
      public Stream<Map.Entry<K, V>> parallelStream() { return StreamSupport.stream(spliterator(), true); }
      
      public Iterator<Map.Entry<K, V>> iterator() { return new Iterator<Map.Entry<K, V>>() {
            private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = Collections.UnmodifiableMap.UnmodifiableEntrySet.this.c.iterator();
            
            public boolean hasNext() { return this.i.hasNext(); }
            
            public Map.Entry<K, V> next() { return new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry((Map.Entry)this.i.next()); }
            
            public void remove() { throw new UnsupportedOperationException(); }
          }; }
      
      public Object[] toArray() {
        Object[] arrayOfObject = this.c.toArray();
        for (byte b = 0; b < arrayOfObject.length; b++)
          arrayOfObject[b] = new UnmodifiableEntry((Map.Entry)arrayOfObject[b]); 
        return arrayOfObject;
      }
      
      public <T> T[] toArray(T[] param2ArrayOfT) {
        Object[] arrayOfObject = this.c.toArray((param2ArrayOfT.length == 0) ? param2ArrayOfT : Arrays.copyOf(param2ArrayOfT, 0));
        for (byte b = 0; b < arrayOfObject.length; b++)
          arrayOfObject[b] = new UnmodifiableEntry((Map.Entry)arrayOfObject[b]); 
        if (arrayOfObject.length > param2ArrayOfT.length)
          return (T[])(Object[])arrayOfObject; 
        System.arraycopy(arrayOfObject, 0, param2ArrayOfT, 0, arrayOfObject.length);
        if (param2ArrayOfT.length > arrayOfObject.length)
          param2ArrayOfT[arrayOfObject.length] = null; 
        return param2ArrayOfT;
      }
      
      public boolean contains(Object param2Object) { return !(param2Object instanceof Map.Entry) ? false : this.c.contains(new UnmodifiableEntry((Map.Entry)param2Object)); }
      
      public boolean containsAll(Collection<?> param2Collection) {
        for (Object object : param2Collection) {
          if (!contains(object))
            return false; 
        } 
        return true;
      }
      
      public boolean equals(Object param2Object) {
        if (param2Object == this)
          return true; 
        if (!(param2Object instanceof Set))
          return false; 
        Set set = (Set)param2Object;
        return (set.size() != this.c.size()) ? false : containsAll(set);
      }
      
      private static class UnmodifiableEntry<K, V> extends Object implements Map.Entry<K, V> {
        private Map.Entry<? extends K, ? extends V> e;
        
        UnmodifiableEntry(Map.Entry<? extends K, ? extends V> param3Entry) { this.e = (Map.Entry)Objects.requireNonNull(param3Entry); }
        
        public K getKey() { return (K)this.e.getKey(); }
        
        public V getValue() { return (V)this.e.getValue(); }
        
        public V setValue(V param3V) { throw new UnsupportedOperationException(); }
        
        public int hashCode() { return this.e.hashCode(); }
        
        public boolean equals(Object param3Object) {
          if (this == param3Object)
            return true; 
          if (!(param3Object instanceof Map.Entry))
            return false; 
          Map.Entry entry = (Map.Entry)param3Object;
          return (Collections.eq(this.e.getKey(), entry.getKey()) && Collections.eq(this.e.getValue(), entry.getValue()));
        }
        
        public String toString() { return this.e.toString(); }
      }
      
      static final class UnmodifiableEntrySetSpliterator<K, V> extends Object implements Spliterator<Map.Entry<K, V>> {
        final Spliterator<Map.Entry<K, V>> s;
        
        UnmodifiableEntrySetSpliterator(Spliterator<Map.Entry<K, V>> param3Spliterator) { this.s = param3Spliterator; }
        
        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param3Consumer) {
          Objects.requireNonNull(param3Consumer);
          return this.s.tryAdvance(Collections.UnmodifiableMap.UnmodifiableEntrySet.entryConsumer(param3Consumer));
        }
        
        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param3Consumer) {
          Objects.requireNonNull(param3Consumer);
          this.s.forEachRemaining(Collections.UnmodifiableMap.UnmodifiableEntrySet.entryConsumer(param3Consumer));
        }
        
        public Spliterator<Map.Entry<K, V>> trySplit() {
          Spliterator spliterator = this.s.trySplit();
          return (spliterator == null) ? null : new UnmodifiableEntrySetSpliterator(spliterator);
        }
        
        public long estimateSize() { return this.s.estimateSize(); }
        
        public long getExactSizeIfKnown() { return this.s.getExactSizeIfKnown(); }
        
        public int characteristics() { return this.s.characteristics(); }
        
        public boolean hasCharacteristics(int param3Int) { return this.s.hasCharacteristics(param3Int); }
        
        public Comparator<? super Map.Entry<K, V>> getComparator() { return this.s.getComparator(); }
      }
    }
  }
  
  static class UnmodifiableNavigableMap<K, V> extends UnmodifiableSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
    private static final long serialVersionUID = -4858195264774772197L;
    
    private static final EmptyNavigableMap<?, ?> EMPTY_NAVIGABLE_MAP = new EmptyNavigableMap();
    
    private final NavigableMap<K, ? extends V> nm;
    
    UnmodifiableNavigableMap(NavigableMap<K, ? extends V> param1NavigableMap) {
      super(param1NavigableMap);
      this.nm = param1NavigableMap;
    }
    
    public K lowerKey(K param1K) { return (K)this.nm.lowerKey(param1K); }
    
    public K floorKey(K param1K) { return (K)this.nm.floorKey(param1K); }
    
    public K ceilingKey(K param1K) { return (K)this.nm.ceilingKey(param1K); }
    
    public K higherKey(K param1K) { return (K)this.nm.higherKey(param1K); }
    
    public Map.Entry<K, V> lowerEntry(K param1K) {
      Map.Entry entry = this.nm.lowerEntry(param1K);
      return (null != entry) ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(entry) : null;
    }
    
    public Map.Entry<K, V> floorEntry(K param1K) {
      Map.Entry entry = this.nm.floorEntry(param1K);
      return (null != entry) ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(entry) : null;
    }
    
    public Map.Entry<K, V> ceilingEntry(K param1K) {
      Map.Entry entry = this.nm.ceilingEntry(param1K);
      return (null != entry) ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(entry) : null;
    }
    
    public Map.Entry<K, V> higherEntry(K param1K) {
      Map.Entry entry = this.nm.higherEntry(param1K);
      return (null != entry) ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(entry) : null;
    }
    
    public Map.Entry<K, V> firstEntry() {
      Map.Entry entry = this.nm.firstEntry();
      return (null != entry) ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(entry) : null;
    }
    
    public Map.Entry<K, V> lastEntry() {
      Map.Entry entry = this.nm.lastEntry();
      return (null != entry) ? new Collections.UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry(entry) : null;
    }
    
    public Map.Entry<K, V> pollFirstEntry() { throw new UnsupportedOperationException(); }
    
    public Map.Entry<K, V> pollLastEntry() { throw new UnsupportedOperationException(); }
    
    public NavigableMap<K, V> descendingMap() { return Collections.unmodifiableNavigableMap(this.nm.descendingMap()); }
    
    public NavigableSet<K> navigableKeySet() { return Collections.unmodifiableNavigableSet(this.nm.navigableKeySet()); }
    
    public NavigableSet<K> descendingKeySet() { return Collections.unmodifiableNavigableSet(this.nm.descendingKeySet()); }
    
    public NavigableMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) { return Collections.unmodifiableNavigableMap(this.nm.subMap(param1K1, param1Boolean1, param1K2, param1Boolean2)); }
    
    public NavigableMap<K, V> headMap(K param1K, boolean param1Boolean) { return Collections.unmodifiableNavigableMap(this.nm.headMap(param1K, param1Boolean)); }
    
    public NavigableMap<K, V> tailMap(K param1K, boolean param1Boolean) { return Collections.unmodifiableNavigableMap(this.nm.tailMap(param1K, param1Boolean)); }
    
    private static class EmptyNavigableMap<K, V> extends UnmodifiableNavigableMap<K, V> implements Serializable {
      private static final long serialVersionUID = -2239321462712562324L;
      
      EmptyNavigableMap() { super(new TreeMap()); }
      
      public NavigableSet<K> navigableKeySet() { return Collections.emptyNavigableSet(); }
      
      private Object readResolve() { return EMPTY_NAVIGABLE_MAP; }
    }
  }
  
  static class UnmodifiableNavigableSet<E> extends UnmodifiableSortedSet<E> implements NavigableSet<E>, Serializable {
    private static final long serialVersionUID = -6027448201786391929L;
    
    private static final NavigableSet<?> EMPTY_NAVIGABLE_SET = new EmptyNavigableSet();
    
    private final NavigableSet<E> ns;
    
    UnmodifiableNavigableSet(NavigableSet<E> param1NavigableSet) {
      super(param1NavigableSet);
      this.ns = param1NavigableSet;
    }
    
    public E lower(E param1E) { return (E)this.ns.lower(param1E); }
    
    public E floor(E param1E) { return (E)this.ns.floor(param1E); }
    
    public E ceiling(E param1E) { return (E)this.ns.ceiling(param1E); }
    
    public E higher(E param1E) { return (E)this.ns.higher(param1E); }
    
    public E pollFirst() { throw new UnsupportedOperationException(); }
    
    public E pollLast() { throw new UnsupportedOperationException(); }
    
    public NavigableSet<E> descendingSet() { return new UnmodifiableNavigableSet(this.ns.descendingSet()); }
    
    public Iterator<E> descendingIterator() { return descendingSet().iterator(); }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) { return new UnmodifiableNavigableSet(this.ns.subSet(param1E1, param1Boolean1, param1E2, param1Boolean2)); }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) { return new UnmodifiableNavigableSet(this.ns.headSet(param1E, param1Boolean)); }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) { return new UnmodifiableNavigableSet(this.ns.tailSet(param1E, param1Boolean)); }
    
    private static class EmptyNavigableSet<E> extends UnmodifiableNavigableSet<E> implements Serializable {
      private static final long serialVersionUID = -6291252904449939134L;
      
      public EmptyNavigableSet() { super(new TreeSet()); }
      
      private Object readResolve() { return EMPTY_NAVIGABLE_SET; }
    }
  }
  
  static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E> implements RandomAccess {
    private static final long serialVersionUID = -2542308836966382001L;
    
    UnmodifiableRandomAccessList(List<? extends E> param1List) { super(param1List); }
    
    public List<E> subList(int param1Int1, int param1Int2) { return new UnmodifiableRandomAccessList(this.list.subList(param1Int1, param1Int2)); }
    
    private Object writeReplace() { return new Collections.UnmodifiableList(this.list); }
  }
  
  static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements Set<E>, Serializable {
    private static final long serialVersionUID = -9215047833775013803L;
    
    UnmodifiableSet(Set<? extends E> param1Set) { super(param1Set); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || this.c.equals(param1Object)); }
    
    public int hashCode() { return this.c.hashCode(); }
  }
  
  static class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V> implements SortedMap<K, V>, Serializable {
    private static final long serialVersionUID = -8806743815996713206L;
    
    private final SortedMap<K, ? extends V> sm;
    
    UnmodifiableSortedMap(SortedMap<K, ? extends V> param1SortedMap) {
      super(param1SortedMap);
      this.sm = param1SortedMap;
    }
    
    public Comparator<? super K> comparator() { return this.sm.comparator(); }
    
    public SortedMap<K, V> subMap(K param1K1, K param1K2) { return new UnmodifiableSortedMap(this.sm.subMap(param1K1, param1K2)); }
    
    public SortedMap<K, V> headMap(K param1K) { return new UnmodifiableSortedMap(this.sm.headMap(param1K)); }
    
    public SortedMap<K, V> tailMap(K param1K) { return new UnmodifiableSortedMap(this.sm.tailMap(param1K)); }
    
    public K firstKey() { return (K)this.sm.firstKey(); }
    
    public K lastKey() { return (K)this.sm.lastKey(); }
  }
  
  static class UnmodifiableSortedSet<E> extends UnmodifiableSet<E> implements SortedSet<E>, Serializable {
    private static final long serialVersionUID = -4929149591599911165L;
    
    private final SortedSet<E> ss;
    
    UnmodifiableSortedSet(SortedSet<E> param1SortedSet) {
      super(param1SortedSet);
      this.ss = param1SortedSet;
    }
    
    public Comparator<? super E> comparator() { return this.ss.comparator(); }
    
    public SortedSet<E> subSet(E param1E1, E param1E2) { return new UnmodifiableSortedSet(this.ss.subSet(param1E1, param1E2)); }
    
    public SortedSet<E> headSet(E param1E) { return new UnmodifiableSortedSet(this.ss.headSet(param1E)); }
    
    public SortedSet<E> tailSet(E param1E) { return new UnmodifiableSortedSet(this.ss.tailSet(param1E)); }
    
    public E first() { return (E)this.ss.first(); }
    
    public E last() { return (E)this.ss.last(); }
  }
  
  private static class EmptyNavigableMap<K, V> extends UnmodifiableNavigableMap<K, V> implements Serializable {
    private static final long serialVersionUID = -2239321462712562324L;
    
    EmptyNavigableMap() { super(new TreeMap()); }
    
    public NavigableSet<K> navigableKeySet() { return Collections.emptyNavigableSet(); }
    
    private Object readResolve() { return EMPTY_NAVIGABLE_MAP; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Collections.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */