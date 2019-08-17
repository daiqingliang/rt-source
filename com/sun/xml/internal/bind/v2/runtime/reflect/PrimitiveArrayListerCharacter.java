package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerCharacter<BeanT> extends Lister<BeanT, char[], Character, PrimitiveArrayListerCharacter.CharacterArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(char.class, new PrimitiveArrayListerCharacter()); }
  
  public ListIterator<Character> iterator(final char[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Character>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Character next() { return Character.valueOf(objects[this.idx++]); }
      }; }
  
  public CharacterArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor) { return new CharacterArrayPack(); }
  
  public void addToPack(CharacterArrayPack paramCharacterArrayPack, Character paramCharacter) { paramCharacterArrayPack.add(paramCharacter); }
  
  public void endPacking(CharacterArrayPack paramCharacterArrayPack, BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramCharacterArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new char[0]); }
  
  static final class CharacterArrayPack {
    char[] buf = new char[16];
    
    int size;
    
    void add(Character param1Character) {
      if (this.buf.length == this.size) {
        char[] arrayOfChar = new char[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfChar, 0, this.buf.length);
        this.buf = arrayOfChar;
      } 
      if (param1Character != null)
        this.buf[this.size++] = param1Character.charValue(); 
    }
    
    char[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      char[] arrayOfChar = new char[this.size];
      System.arraycopy(this.buf, 0, arrayOfChar, 0, this.size);
      return arrayOfChar;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerCharacter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */