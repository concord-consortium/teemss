

public abstract class ProbeManager
{
    public final static int ERROR = -1;
    public final static int OK = 0;
    public final static int NEW_PROBE = 1;
    public final static int PROBE_INFO = 2;
    public final static int PROBE_DATA = 3;
    public final static int ACK = 6;

    int response;
    int probeId;
    ProbeInfo curInfo;
    float [] curData;
    String msg;
    ProbeInfo probes[] = new ProbeInfo[1];
    String name;
    boolean activeUpdate = true;
    boolean started;

    ProbeInfo [] probeInfo;
    int value;
    int posInfo;

    public ProbeManager()
    {
    }

    abstract void close();

    abstract ProbeInfo [] getProbes();

    abstract void requestAck();

    abstract boolean step();

    abstract boolean start(int id, Transform t);

    abstract boolean stop(int id);

    abstract boolean readInfo(int id);

}








