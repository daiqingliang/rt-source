package sun.rmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.util.Date;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetPropertyAction;

class PipeWriter implements Runnable {
  private ByteArrayOutputStream bufOut;
  
  private int cLast;
  
  private byte[] currSep;
  
  private PrintWriter out;
  
  private InputStream in;
  
  private String pipeString;
  
  private String execString;
  
  private static String lineSeparator;
  
  private static int lineSeparatorLength;
  
  private static int numExecs = 0;
  
  private PipeWriter(InputStream paramInputStream, OutputStream paramOutputStream, String paramString, int paramInt) {
    this.in = paramInputStream;
    this.out = new PrintWriter(paramOutputStream);
    this.bufOut = new ByteArrayOutputStream();
    this.currSep = new byte[lineSeparatorLength];
    this.execString = ":ExecGroup-" + Integer.toString(paramInt) + ':' + paramString + ':';
  }
  
  public void run() {
    byte[] arrayOfByte = new byte[256];
    try {
      int i;
      while ((i = this.in.read(arrayOfByte)) != -1)
        write(arrayOfByte, 0, i); 
      String str = this.bufOut.toString();
      this.bufOut.reset();
      if (str.length() > 0) {
        this.out.println(createAnnotation() + str);
        this.out.flush();
      } 
    } catch (IOException iOException) {}
  }
  
  private void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt2); 
    for (int i = 0; i < paramInt2; i++)
      write(paramArrayOfByte[paramInt1 + i]); 
  }
  
  private void write(byte paramByte) throws IOException {
    byte b = 0;
    for (b = 1; b < this.currSep.length; b++)
      this.currSep[b - true] = this.currSep[b]; 
    this.currSep[b - 1] = paramByte;
    this.bufOut.write(paramByte);
    if (this.cLast >= lineSeparatorLength - 1 && lineSeparator.equals(new String(this.currSep))) {
      this.cLast = 0;
      this.out.print(createAnnotation() + this.bufOut.toString());
      this.out.flush();
      this.bufOut.reset();
      if (this.out.checkError())
        throw new IOException("PipeWriter: IO Exception when writing to output stream."); 
    } else {
      this.cLast++;
    } 
  }
  
  private String createAnnotation() { return (new Date()).toString() + this.execString; }
  
  static void plugTogetherPair(InputStream paramInputStream1, OutputStream paramOutputStream1, InputStream paramInputStream2, OutputStream paramOutputStream2) {
    Thread thread1 = null;
    Thread thread2 = null;
    int i = getNumExec();
    thread1 = (Thread)AccessController.doPrivileged(new NewThreadAction(new PipeWriter(paramInputStream1, paramOutputStream1, "out", i), "out", true));
    thread2 = (Thread)AccessController.doPrivileged(new NewThreadAction(new PipeWriter(paramInputStream2, paramOutputStream2, "err", i), "err", true));
    thread1.start();
    thread2.start();
  }
  
  private static int getNumExec() { return numExecs++; }
  
  static  {
    lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
    lineSeparatorLength = lineSeparator.length();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\PipeWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */