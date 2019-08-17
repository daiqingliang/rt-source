package java.lang.ref;

import java.lang.ref.Finalizer;
import java.lang.ref.FinalizerHistogram;
import java.lang.ref.FinalizerHistogram.Entry;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

final class FinalizerHistogram {
  static Entry[] getFinalizerHistogram() {
    HashMap hashMap = new HashMap();
    ReferenceQueue referenceQueue = Finalizer.getQueue();
    referenceQueue.forEach(paramReference -> {
          Object object = paramReference.get();
          if (object != null) {
            ((Entry)paramMap.computeIfAbsent(object.getClass().getName(), Entry::new)).increment();
            object = null;
          } 
        });
    Entry[] arrayOfEntry = (Entry[])hashMap.values().toArray(new Entry[hashMap.size()]);
    Arrays.sort(arrayOfEntry, Comparator.comparingInt(Entry::getInstanceCount).reversed());
    return arrayOfEntry;
  }
  
  private static final class Entry {
    private int instanceCount;
    
    private final String className;
    
    int getInstanceCount() { return this.instanceCount; }
    
    void increment() { this.instanceCount++; }
    
    Entry(String param1String) { this.className = param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ref\FinalizerHistogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */