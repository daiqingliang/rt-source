package com.sun.corba.se.impl.encoding;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BufferQueue {
  private LinkedList list = new LinkedList();
  
  public void enqueue(ByteBufferWithInfo paramByteBufferWithInfo) { this.list.addLast(paramByteBufferWithInfo); }
  
  public ByteBufferWithInfo dequeue() throws NoSuchElementException { return (ByteBufferWithInfo)this.list.removeFirst(); }
  
  public int size() { return this.list.size(); }
  
  public void push(ByteBufferWithInfo paramByteBufferWithInfo) { this.list.addFirst(paramByteBufferWithInfo); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */