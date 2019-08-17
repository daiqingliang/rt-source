package sun.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransferableProxy implements Transferable {
  protected final Transferable transferable;
  
  protected final boolean isLocal;
  
  public TransferableProxy(Transferable paramTransferable, boolean paramBoolean) {
    this.transferable = paramTransferable;
    this.isLocal = paramBoolean;
  }
  
  public DataFlavor[] getTransferDataFlavors() { return this.transferable.getTransferDataFlavors(); }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) { return this.transferable.isDataFlavorSupported(paramDataFlavor); }
  
  public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    Object object = this.transferable.getTransferData(paramDataFlavor);
    if (object != null && this.isLocal && paramDataFlavor.isFlavorSerializedObjectType()) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ClassLoaderObjectOutputStream classLoaderObjectOutputStream = new ClassLoaderObjectOutputStream(byteArrayOutputStream);
      classLoaderObjectOutputStream.writeObject(object);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      try {
        ClassLoaderObjectInputStream classLoaderObjectInputStream = new ClassLoaderObjectInputStream(byteArrayInputStream, classLoaderObjectOutputStream.getClassLoaderMap());
        object = classLoaderObjectInputStream.readObject();
      } catch (ClassNotFoundException classNotFoundException) {
        throw (IOException)(new IOException()).initCause(classNotFoundException);
      } 
    } 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\TransferableProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */