public abstract class Transform {
    Transform next = null;

    public void start()
    {
	if(next != null)
	    next.start();

    }

    public void stop()
    {
	if(next != null)
	    next.stop();
    }

    public void update()
    {
	if(next != null)
	    next.update();
    };

    public abstract boolean transform(int num, int size, float [] data);
}
