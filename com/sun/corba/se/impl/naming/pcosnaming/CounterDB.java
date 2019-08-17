package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class CounterDB implements Serializable {
  private Integer counter;
  
  private static String counterFileName = "counter";
  
  private File counterFile;
  
  public static final int rootCounter = 0;
  
  CounterDB(File paramFile) {
    counterFileName = "counter";
    this.counterFile = new File(paramFile, counterFileName);
    if (!this.counterFile.exists()) {
      this.counter = new Integer(0);
      writeCounter();
    } else {
      readCounter();
    } 
  }
  
  private void readCounter() {
    try {
      FileInputStream fileInputStream = new FileInputStream(this.counterFile);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      this.counter = (Integer)objectInputStream.readObject();
      objectInputStream.close();
    } catch (Exception exception) {}
  }
  
  private void writeCounter() {
    try {
      this.counterFile.delete();
      FileOutputStream fileOutputStream = new FileOutputStream(this.counterFile);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(this.counter);
      objectOutputStream.flush();
      objectOutputStream.close();
    } catch (Exception exception) {}
  }
  
  public int getNextCounter() {
    int i = this.counter.intValue();
    this.counter = new Integer(++i);
    writeCounter();
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\CounterDB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */