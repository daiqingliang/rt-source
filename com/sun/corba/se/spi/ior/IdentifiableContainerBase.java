package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.FreezableList;
import java.util.ArrayList;
import java.util.Iterator;

public class IdentifiableContainerBase extends FreezableList {
  public IdentifiableContainerBase() { super(new ArrayList()); }
  
  public Iterator iteratorById(final int id) { return new Iterator() {
        Iterator iter = IdentifiableContainerBase.this.iterator();
        
        Object current = advance();
        
        private Object advance() {
          while (this.iter.hasNext()) {
            Identifiable identifiable = (Identifiable)this.iter.next();
            if (identifiable.getId() == id)
              return identifiable; 
          } 
          return null;
        }
        
        public boolean hasNext() { return (this.current != null); }
        
        public Object next() {
          Object object = this.current;
          this.current = advance();
          return object;
        }
        
        public void remove() { this.iter.remove(); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableContainerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */