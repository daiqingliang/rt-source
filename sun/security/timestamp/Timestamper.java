package sun.security.timestamp;

import java.io.IOException;

public interface Timestamper {
  TSResponse generateTimestamp(TSRequest paramTSRequest) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\timestamp\Timestamper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */