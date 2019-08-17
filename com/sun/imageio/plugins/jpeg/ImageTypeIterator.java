package com.sun.imageio.plugins.jpeg;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.imageio.ImageTypeSpecifier;

class ImageTypeIterator extends Object implements Iterator<ImageTypeSpecifier> {
  private Iterator<ImageTypeProducer> producers;
  
  private ImageTypeSpecifier theNext = null;
  
  public ImageTypeIterator(Iterator<ImageTypeProducer> paramIterator) { this.producers = paramIterator; }
  
  public boolean hasNext() {
    if (this.theNext != null)
      return true; 
    if (!this.producers.hasNext())
      return false; 
    do {
      this.theNext = ((ImageTypeProducer)this.producers.next()).getType();
    } while (this.theNext == null && this.producers.hasNext());
    return (this.theNext != null);
  }
  
  public ImageTypeSpecifier next() {
    if (this.theNext != null || hasNext()) {
      ImageTypeSpecifier imageTypeSpecifier = this.theNext;
      this.theNext = null;
      return imageTypeSpecifier;
    } 
    throw new NoSuchElementException();
  }
  
  public void remove() { this.producers.remove(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\ImageTypeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */