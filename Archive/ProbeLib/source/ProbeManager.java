/*
Copyright (C) 2001 Concord Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


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








