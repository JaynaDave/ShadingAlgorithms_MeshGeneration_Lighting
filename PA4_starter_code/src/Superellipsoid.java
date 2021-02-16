//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Superellipsoid
{
	private Point3D center;
	private float rx, ry, rz, s1, s2;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Superellipsoid(float _x, float _y, float _z, float _rx, float _ry, float _rz, float _s1, float _s2, int _stacks, int _slices)
	{
		center = new Point3D(_x,_y,_z);
		rx = _rx;
		ry = _ry;
		rz = _rz;
		s1 = _s1;
		s2 = _s2;
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
		//r = _r;
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
		double dphi = PI/(stacks-1);
		double dtheta = 2*PI/(slices-1);
		
		Point3D d_theta = new Point3D();
		Point3D d_phi = new Point3D();
		
		
		
		int i, j;
		for(i = 0, phi=-PI/2; i<stacks; i++, phi+=dphi) {
			double cos_phi = Math.cos(phi);
			double sin_phi = Math.sin(phi);
			
			for(j = 0,theta = -PI; j<slices; j++, theta+=dtheta) {
				double cos_theta = Math.cos(theta);
				double sin_theta = Math.sin(theta);
				
				mesh.v[i][j].x = center.x+rx* (float)Math.signum(cos_phi)*(float)Math.pow((float)Math.abs(cos_phi), s1)*(float)Math.signum(cos_theta)*(float)Math.pow((float)Math.abs(cos_theta), s2);
				mesh.v[i][j].y = center.y+ry*(float)Math.signum(cos_phi)*(float)Math.pow((float)Math.abs(cos_phi), s1)*(float)Math.signum(sin_theta)*(float)Math.pow((float)Math.abs(sin_theta), s2);
				mesh.v[i][j].z = center.z+rz*(float)Math.signum(sin_phi)*(float)Math.pow((float)Math.abs(sin_phi), s1);
				
				
				d_phi.x = (float)(-s1*rx*(float)Math.pow((float)cos_phi, (s1-1))*(float)sin_phi*Math.pow((float)cos_theta, s2));
				d_phi.y = (float)(-s1*ry*(float)Math.pow((float)cos_phi, (s1-1))*(float)sin_phi*Math.pow((float)sin_theta, s2));
				d_phi.z = (float)(s1*rz*(float)Math.pow((float)sin_phi, (s1-1))*cos_phi);
				
				
				d_theta.x = (float)(-s2*rx*(float)Math.pow((float)cos_phi, s1)*(float)Math.pow((float)cos_theta, (s2 -1))*sin_theta);
				d_theta.y = (float)(s2*rx*(float)Math.pow((float)cos_phi, s1)*(float)Math.pow((float)sin_theta, (s2 -1))*cos_theta);
				d_theta.z = 0;
				
				d_theta.crossProduct(d_phi, mesh.n[i][j]);

				
				mesh.n[i][j].normalize();
				
			}
			
		}
		// ****************Implement Code here*******************//

	}
}