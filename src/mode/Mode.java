package src.mode;

public enum Mode {
	START('`'),
	DEFAULT('.'),
	ZOOM_IN('+'), 
	ZOOM_OUT('-'),
	VRP_POSITIVE_U('j'),
	VRP_POSITIVE_V('k'),
	VRP_POSITIVE_N('l'),
	VRP_NEGATIVE_U('n'),
	VRP_NEGATIVE_V('m'),
	VRP_NEGATIVE_N(','),
	ROTATION_CLOCKWISE_X('a'),
	ROTATION_CLOCKWISE_Y('s'),
	ROTATION_CLOCKWISE_Z('d'),
	ROTATION_COUNTER_CLOCKWISE_X('z'),
	ROTATION_COUNTER_CLOCKWISE_Y('x'),
	ROTATION_COUNTER_CLOCKWISE_Z('c'),
	QUIT('q');
	
	char key;
	
	Mode(char k)
	{
		this.key = k;
	}
	
	public char getKey()
	{
		return key;
	}
	
	public String toString()
	{
		return (""+getKey());
	}
}
