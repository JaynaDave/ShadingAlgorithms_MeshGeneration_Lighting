import java.util.Random;

public class AmbientLight extends Light{
private Random rnd=new Random();
	
	public AmbientLight(ColorType _c) 
	{
		color = new ColorType(_c);
		position = new Point3D(0, 0, 0); // Not used in this class
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	public ColorType applyLight(Material mat, Point3D v, Point3D n){
		ColorType res = new ColorType();
		// ****************Implement Code here*******************//
		ColorType I_amb = new ColorType();
		I_amb.r = mat.ka.r*color.r;
		I_amb.g = mat.ka.g*color.g;
		I_amb.b = mat.ka.b*color.b;
		
		res.r = I_amb.r ;
		res.g = I_amb.g ;
		res.b = I_amb.b ;
		
		res.clamp();
		
		return res;

	}
}
