public interface LabBookDB 
{
    public boolean close();
    
    public boolean getError();
 
    public byte [] readObjectBytes(int devId, int objId);
  
    public boolean writeObjectBytes(int devId, int objId, byte [] buffer, int start,
			     int count);

    public int getDevId();
    
    public int getNewObjId();
       
}
