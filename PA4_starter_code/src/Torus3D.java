//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Torus3D
{
	private Point3D center;
	private float r, rAxial;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Torus3D(float _x, float _y, float _z, float _r,float _rAxial,  int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		r = _r;
		rAxial = _rAxial;
		stacks = _stacks;
		slices = _slices;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_stacks(int _stacks)
	{
		stacks = _stacks;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_slices(int _slices)
	{
		slices = _slices;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return slices;
	}
	
	public int get_m()
	{
		return stacks;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(stacks,slices);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		double PI = Math.PI;
		double phi;
		double theta;
		double dphi = 2*PI/(stacks-1);
		double dtheta = 2*PI/(slices-1);
		
		//for caluculating the normal 
		Point3D d_theta = new Point3D();
		Point3D d_phi = new Point3D();
		
		
		
		int i, j;
		for(i = 0, phi=-PI; i<stacks; i++, phi+=dphi) {
			double cos_phi = Math.cos(phi);
			double sin_phi = Math.sin(phi);
		
			
			
			for(j = 0,theta = -PI; j<slices; j++, theta+=dtheta) {
				double cos_theta = Math.cos(theta);
				double sin_theta = Math.sin(theta);
				
				mesh.v[i][j].x = center.x+(rAxial + r*(float)cos_phi)*(float)cos_theta;
				mesh.v[i][j].y = center.y+(rAxial + r*(float)cos_phi)*(float)sin_theta;
				mesh.v[i][j].z = center.z+r*(float)sin_phi;
				
				
				d_phi.x =  -r*(float)sin_phi*(float)cos_theta;
				d_phi.y =  -r*(float)sin_phi*(float)sin_theta;
				d_phi.z = 0;
				
				d_theta.x = -(rAxial + r*(float)cos_phi)*(float)sin_theta;
				d_theta.y = (rAxial + r*(float)cos_phi)*(float)cos_theta;
				d_theta.z = r*(float)cos_phi;
				
				d_theta.crossProduct(d_phi, mesh.n[i][j]);
				
				mesh.n[i][j].normalize();
				
			}
			
		}
		// ****************Implement Code here*******************//

	}
}