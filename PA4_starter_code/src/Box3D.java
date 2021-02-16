//****************************************************************************
//      Box class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Box3D
{
	private Point3D center;
	private float h;
	private int stacks,slices;
	public Mesh3D mesh;
	
	public Box3D(float _x, float _y, float _z, float _h)
	{
		center = new Point3D(_x,_y,_z);
		h = _h;
		stacks = 4;
		slices = 5;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_height(float _h)
	{
		h = _h;
		fillMesh(); // update the triangle mesh
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
		int i, j;
		for(i = 0, phi=-PI/2; i<stacks; i++, phi+=dphi) {
			double cos_phi = Math.cos(phi);
			double sin_phi = Math.sin(phi);
			
			for(j = 0,theta = -PI; j<slices; j++, theta+=dtheta) {
				double cos_theta = Math.cos(theta);
				double sin_theta = Math.sin(theta);
				
				mesh.v[i][j].x = center.x+h*(float)cos_phi*(float)cos_theta;
				mesh.v[i][j].y = center.y+h*(float)cos_phi*(float)sin_theta;
				
				if(sin_phi==1){mesh.v[i][j].z = center.z+h/2;}
				else if(sin_phi==-1) {mesh.v[i][j].z = center.z-h/2;}
				else {mesh.v[i][j].z = center.z+h*(float)sin_phi;}
				
				
				if(sin_phi==1){mesh.n[i][j] = new Point3D(0,0,1);}
				else if(sin_phi==-1) {mesh.n[i][j] = new Point3D(0,0,-1);}
				else {
					mesh.n[i][j].x = (float)cos_phi*(float)cos_theta;
					mesh.n[i][j].y = (float)cos_phi*(float)sin_theta;
					mesh.n[i][j].z = (float)sin_phi;}
				
			}
			
		}
		// ****************Implement Code here*******************//

	}
}