package java.lang;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public interface CharSequence {
  int length();
  
  char charAt(int paramInt);
  
  CharSequence subSequence(int paramInt1, int paramInt2);
  
  String toString();
  
  default IntStream chars() {
    class CharIterator implements PrimitiveIterator.OfInt {
      int cur = 0;
      
      public boolean hasNext() { return (this.cur < CharSequence.this.length()); }
      
      public int nextInt() {
        if (hasNext())
          return CharSequence.this.charAt(this.cur++); 
        throw new NoSuchElementException();
      }
      
      public void forEachRemaining(IntConsumer param1IntConsumer) {
        while (this.cur < CharSequence.this.length()) {
          param1IntConsumer.accept(CharSequence.this.charAt(this.cur));
          this.cur++;
        } 
      }
    };
    return StreamSupport.intStream(() -> Spliterators.spliterator(new CharIterator(), length(), 16), 16464, false);
  }
  
  default IntStream codePoints() {
    class CodePointIterator implements PrimitiveIterator.OfInt {
      int cur = 0;
      
      public void forEachRemaining(IntConsumer param1IntConsumer) {
        int i = CharSequence.this.length();
        j = this.cur;
        try {
          while (j < i) {
            char c1 = CharSequence.this.charAt(j++);
            if (!Character.isHighSurrogate(c1) || j >= i) {
              param1IntConsumer.accept(c1);
              continue;
            } 
            char c2 = CharSequence.this.charAt(j);
            if (Character.isLowSurrogate(c2)) {
              j++;
              param1IntConsumer.accept(Character.toCodePoint(c1, c2));
              continue;
            } 
            param1IntConsumer.accept(c1);
          } 
        } finally {
          this.cur = j;
        } 
      }
      
      public boolean hasNext() { return (this.cur < CharSequence.this.length()); }
      
      public int nextInt() {
        int i = CharSequence.this.length();
        if (this.cur >= i)
          throw new NoSuchElementException(); 
        char c = CharSequence.this.charAt(this.cur++);
        if (Character.isHighSurrogate(c) && this.cur < i) {
          char c1 = CharSequence.this.charAt(this.cur);
          if (Character.isLowSurrogate(c1)) {
            this.cur++;
            return Character.toCodePoint(c, c1);
          } 
        } 
        return c;
      }
    };
    return StreamSupport.intStream(() -> Spliterators.spliteratorUnknownSize(new CodePointIterator(), 16), 16, false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\CharSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */