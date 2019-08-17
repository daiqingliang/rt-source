package sun.misc;

import java.io.IOException;
import java.io.ObjectInputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

public interface JavaxCryptoSealedObjectAccess {
  ObjectInputStream getExtObjectInputStream(SealedObject paramSealedObject, Cipher paramCipher) throws BadPaddingException, IllegalBlockSizeException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JavaxCryptoSealedObjectAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */