import java.util.Random;

public class PointLight extends Light{
private Random rnd=new Random();
	
	public Point3D position;

	public PointLight(ColorType _c, Point3D _position) 
	{
		color = new ColorType(_c);
		position = _position; // Not used in this class
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v: viewing vector
	// n: face normal
	public ColorType applyLight(Material mat, Point3D v, Point3D n, Point3D ps, boolean spec_light , boolean diffuse_light, boolean attenuationLight){
		ColorType res = new ColorType();
		// ****************Implement Code here*******************//
		/**ColorType I_amb = new ColorType();
		I_amb.r = mat.ka.r*color.r;
		I_amb.g = mat.ka.g*color.g;
		I_amb.b = mat.ka.b*color.b;*/
		
		Point3D L = new Point3D();
		
		float magnitude = (float)Math.sqrt(Math.pow((ps.x - position.x), 2) + Math.pow((ps.y - position.y), 2) + Math.pow((ps.z - position.z), 2));
		
		int thetal = 45; //spot angle
		double cos_thetal = Math.cos(thetal);
		
		L.x = (position.x - ps.x)/magnitude;
		L.y = (position.y - ps.y)/magnitude;
		L.z = (position.z - ps.z)/magnitude;
		
		
		ColorType I_dif = new ColorType();
		I_dif.r = mat.kd.r*color.r*n.dotProduct(L);
		I_dif.g = mat.kd.g*color.g*n.dotProduct(L);
		I_dif.b = mat.kd.b*color.b*n.dotProduct(L);
		
		
		ColorType I_spec = new ColorType();
		Point3D r = L.reflect(n);
		float temp = (float)Math.pow(v.dotProduct(r), mat.ns);
		I_spec.r = mat.ks.r*color.r*temp;
		I_spec.g = mat.ks.g*color.g*temp;
		I_spec.b = mat.ks.b*color.b*temp;
		
		
		//attenuation angular
		Point3D Vobj = new Point3D(-L.x,-L.y,-L.z);
		double Fang = 1; //not a spotlight
		double Frad = 1;
		
		
		if(attenuationLight) {
		if(Vobj.dotProduct(v) >= cos_thetal) {
		
		Fang = Math.pow(Vobj.dotProduct(v), 2); 
		}
		
		//attenuation radial 
		
		Frad =1/(.0000003 + .0000003*(magnitude) + .00000003*Math.pow(magnitude,2));
		}
		
		double attenLightfactor = Frad*Fang;
		//System.out.println(attenLightfactor);
		
		res.r = 0;
		res.g = 0;
		res.b = 0;
		
		
		if(diffuse_light) {
			res.r += I_dif.r;
			res.g +=  I_dif.g;
			res.b +=  I_dif.b;
			}
		
		if(spec_light) {
			res.r +=  I_spec.r;
			res.g +=  I_spec.g;
			res.b +=  I_spec.b;

		}
		
		res.r = res.r*(float)attenLightfactor;
		res.g = res.g*(float)attenLightfactor;
		res.b = res.b*(float)attenLightfactor; 
		
		res.clamp();
		
		return res;

	}
	
	public void rotateLight(Quaternion q, Point3D center)
	{
		Quaternion q_inv = q.conjugate();
		Point3D vec;
		
		Quaternion p;
		

				// apply pivot rotation to vertices, given center point
				p = new Quaternion((float)0.0,position.minus(center)); 
				p=q.multiply(p);
				p=p.multiply(q_inv);
				vec = p.get_v();
				position=vec.plus(center);
				//System.out.println(position.x);
				//System.out.println(position.y);
				//System.out.println(position.z);
				//System.out.println();
				
				
	}
}
