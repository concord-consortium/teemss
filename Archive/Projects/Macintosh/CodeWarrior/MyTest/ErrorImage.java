import java.awt.Color;
public class ErrorImage{
public int w = 31;
public int h = 32;
public Color[] colorMap = 		{
							new Color(255,255,255),
							new Color(200,200,200),
							new Color(255,0,0),
							new Color(128,128,128),
							new Color(128,0,0)
						};
public byte[] pixels =		{
							1,1,1,1,1,1,1,1,1,1,1,4,4,4,4,4,4,4,4,1,
							1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,4,
							4,4,2,2,2,2,2,2,2,2,4,4,4,1,1,1,1,1,1,1,
							1,1,1,1,1,1,1,1,1,4,2,2,2,2,2,2,2,2,2,2,
							2,2,2,2,4,1,1,1,1,1,1,1,1,1,1,1,1,1,4,4,
							2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,4,1,1,
							1,1,1,1,1,1,1,1,4,2,2,2,2,2,2,2,2,2,2,2,
							2,2,2,2,2,2,2,2,2,4,1,1,1,1,1,1,1,1,4,2,
							2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
							2,4,1,1,1,1,1,1,1,4,2,2,2,2,2,2,2,2,2,2,
							2,2,2,2,2,2,2,2,2,2,2,2,4,3,1,1,1,1,1,4,
							2,2,2,2,2,2,0,2,2,2,2,2,2,2,2,2,2,0,2,2,
							2,2,2,2,4,3,1,1,1,4,2,2,2,2,2,2,0,0,0,2,
							2,2,2,2,2,2,2,0,0,0,2,2,2,2,2,2,4,1,1,1,
							4,2,2,2,2,2,0,0,0,0,0,2,2,2,2,2,2,0,0,0,
							0,0,2,2,2,2,2,4,3,1,1,4,2,2,2,2,2,2,0,0,
							0,0,0,2,2,2,2,0,0,0,0,0,2,2,2,2,2,2,4,3,
							3,4,2,2,2,2,2,2,2,2,0,0,0,0,0,2,2,0,0,0,
							0,0,2,2,2,2,2,2,2,2,4,3,4,2,2,2,2,2,2,2,
							2,2,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,
							2,4,3,4,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,
							0,0,2,2,2,2,2,2,2,2,2,2,4,3,4,2,2,2,2,2,
							2,2,2,2,2,2,0,0,0,0,0,0,2,2,2,2,2,2,2,2,
							2,2,2,4,3,4,2,2,2,2,2,2,2,2,2,2,2,0,0,0,
							0,0,0,2,2,2,2,2,2,2,2,2,2,2,4,3,4,2,2,2,
							2,2,2,2,2,2,2,0,0,0,0,0,0,0,0,2,2,2,2,2,
							2,2,2,2,2,4,3,4,2,2,2,2,2,2,2,2,2,0,0,0,
							0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,4,3,4,2,
							2,2,2,2,2,2,2,0,0,0,0,0,2,2,0,0,0,0,0,2,
							2,2,2,2,2,2,2,4,3,1,4,2,2,2,2,2,2,0,0,0,
							0,0,2,2,2,2,0,0,0,0,0,2,2,2,2,2,2,4,3,3,
							1,4,2,2,2,2,2,0,0,0,0,0,2,2,2,2,2,2,0,0,
							0,0,0,2,2,2,2,2,4,3,3,1,4,2,2,2,2,2,2,0,
							0,0,2,2,2,2,2,2,2,2,0,0,0,2,2,2,2,2,2,4,
							3,3,1,1,4,2,2,2,2,2,2,0,2,2,2,2,2,2,2,2,
							2,2,0,2,2,2,2,2,2,4,3,3,3,1,1,1,4,2,2,2,
							2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,
							3,3,3,3,1,1,1,4,2,2,2,2,2,2,2,2,2,2,2,2,
							2,2,2,2,2,2,2,2,2,2,4,3,3,3,1,1,1,1,1,4,
							2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
							4,3,3,3,1,1,1,1,1,1,1,4,4,2,2,2,2,2,2,2,
							2,2,2,2,2,2,2,2,2,4,4,3,3,3,3,1,1,1,1,1,
							1,1,1,3,4,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,
							3,3,3,3,3,1,1,1,1,1,1,1,1,1,1,3,4,4,4,2,
							2,2,2,2,2,2,2,4,4,4,3,3,3,3,3,1,1,1,1,1,
							1,1,1,1,1,1,1,1,3,3,4,4,4,4,4,4,4,4,3,3,
							3,3,3,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
							3,3,3,3,3,3,3,3,3,3,3,3,3,3,1,1,1,1,1,1,
							1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,3,3,3,3,3,
							3,3,1,1,1,1,1,1,1,1,1,1
						};
}