//****************************************************************************
//       Example Main Program for CS480 PA1
//****************************************************************************
// Description: 
//   
//   This is a template program for the sketching tool.  
//
//     LEFTMOUSE: draw line segments 
//     RIGHTMOUSE: draw triangles 
//
//     The following keys control the program:
//
//		Q,q: quit 
//		C,c: clear polygon (set vertex count=0)
//		R,r: randomly change the color
//		S,s: toggle the smooth shading for triangle 
//			 (no smooth shading by default)
//		T,t: show testing examples
//		>:	 increase the step number for examples
//		<:   decrease the step number for examples
//
//****************************************************************************
// History :
//   Aug 2004 Created by Jianming Zhang based on the C
//   code by Stan Sclaroff
//   Nov 2014 modified to include test cases
//   Nov 5, 2019 Updated by Zezhou Sun
//
// Jayna Dave U77232739  (Collaborated with Sarina Simon)
// Due 12/8/2020
// program includes depth buffering, and lighting for different shapes 





import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*; 
import java.awt.image.*;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl


public class Lab_PA4 extends JFrame
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	
	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH=512;
	private final int DEFAULT_WINDOW_HEIGHT=512;
	private final float DEFAULT_LINE_WIDTH=1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	@SuppressWarnings("unused")
	private ColorType color;
	private Random rng;
	
	 // specular exponent for materials
	private int ns=5; 
	
	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private boolean doSmoothShading, phongShading;
	private boolean pointLight, infiniteLight, ambientLight, specularLight, diffuseLight, attenuationLight, lightOn;
	private int Nsteps;

	/** The quaternion which controls the rotation of the world. */
    private Quaternion viewing_quaternion = new Quaternion();
    public Point3D viewing_center = new Point3D((float)(218),(float)(218),(float)128);
    public float point_128x = 128;
    public float point_128y = 128;
    public float point_308x = 308;
    public float point_308y = 308;
    
    /** The last x and y coordinates of the mouse press. */
    private int last_x = 0, last_y = 0;
    /** Whether the world is being rotated. */
    private boolean rotate_world = false;
    
    /** Random colors **/
    private ColorType[] colorMap = new ColorType[100];
    private Random rand = new Random();
    
    
    int[][] depthBuffer = new int[DEFAULT_WINDOW_WIDTH][ DEFAULT_WINDOW_HEIGHT];
    
    

	
    
	public Lab_PA4()
	{
	    capabilities = new GLCapabilities(null);
	    capabilities.setDoubleBuffered(true);  // Enable Double buffering

	    canvas  = new GLCanvas(capabilities);
	    canvas.addGLEventListener(this);
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addKeyListener(this);
	    canvas.setAutoSwapBufferMode(true); // true by default. Just to be explicit
	    canvas.setFocusable(true);
	    getContentPane().add(canvas);

	    animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60 FPS

	    numTestCase = 2;
	    testCase = 0;
	    Nsteps = 12;

	    setTitle("CS480/680 Lab for PA4");
	    setSize( DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    setResizable(false);
	    
	    rng = new Random();
	    color = new ColorType(1.0f,0.0f,0.0f);
	    lineSegs = new ArrayList<Point2D>();
	    triangles = new ArrayList<Point2D>();
	    doSmoothShading = false;
	    pointLight = true;
	    infiniteLight = true; 
	    ambientLight = true;
	    attenuationLight = true;
	    specularLight = true; 
	    diffuseLight = true;
	    phongShading = false;
	    
	    
	    for (int i=0; i<100; i++) {
	    	this.colorMap[i] = new ColorType(i*0.005f+0.5f, i*-0.005f+1f, i*0.0025f+0.75f);
	    }
	}

	public void run()
	{
		animator.start();
	}

	public static void main( String[] args )
	{
		Lab_PA4 P = new Lab_PA4();
	    P.run();
	}

	//*********************************************** 
	//  GLEventListener Interfaces
	//*********************************************** 
	public void init( GLAutoDrawable drawable) 
	{
	    GL gl = drawable.getGL();
	    gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glLineWidth( DEFAULT_LINE_WIDTH );
	    Dimension sz = this.getContentPane().getSize();
	    buff = new BufferedImage(sz.width,sz.height,BufferedImage.TYPE_3BYTE_BGR);
	    clearPixelBuffer();
	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable)
	{
	    GL2 gl = drawable.getGL().getGL2();
	    WritableRaster wr = buff.getRaster();
	    DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
	    byte[] data = dbb.getData();

	    gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
	    gl.glDrawPixels (buff.getWidth(), buff.getHeight(),
                GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(data));
        drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		// deliberately left blank
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
	      boolean deviceChanged)
	{
		// deliberately left blank
	}
	
	void clearPixelBuffer()
	{
		lineSegs.clear();
    	triangles.clear();
		Graphics2D g = buff.createGraphics();
	    g.setColor(Color.BLACK);
	    g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
	    g.dispose();
	}
	
	// drawTest
	void drawTestCase()
	{  
		/* clear the window and vertex state */
		clearPixelBuffer();
	  
		//System.out.printf("Test case = %d\n",testCase);

		switch (testCase){
		case 0:
			shadeTest(true, depthBuffer); 
			break;
		case 1:
			shadeTest1(false, depthBuffer); /* flat shaded, sphere and torus */
			break;
		
		}	
	}


	//*********************************************** 
	//          KeyListener Interfaces
	//*********************************************** 
	public void keyTyped(KeyEvent key)
	{
	//      Q,q: quit 
	//      C,c: clear polygon (set vertex count=0)
	//		R,r: randomly change the color
	//		S,s: toggle the smooth shading
	//		T,t: show testing examples (toggles between smooth shading and flat shading test cases)
	//		>:	 increase the step number for examples
	//		<:   decrease the step number for examples
	//     +,-:  increase or decrease spectral exponent

	    switch ( key.getKeyChar() ) 
	    {
	    case 'Q' :
	    case 'q' : 
	    	new Thread()
	    	{
	          	public void run() { animator.stop(); }
	        }.start();
	        System.exit(0);
	        break;
	    case 'R' :
	    case 'r' :
	    	color = new ColorType(rng.nextFloat(),rng.nextFloat(),
	    			rng.nextFloat());
	    	break;
	    case 'C' :
	    case 'c' :
	    	clearPixelBuffer();
	    	break;
	    case 'G' :
	    case 'g' :
	    	doSmoothShading = !doSmoothShading;
	    	break;
	    case 'P' :
	    case 'p' :
	    	phongShading = !phongShading;
	    	break;
	    case 'S' :
	    case 's' :
	    	specularLight = !specularLight;
	    	break;
	    case 'D' :
	    case 'd' :
	    	diffuseLight = !diffuseLight;
	    	break;
	    case 'L' :
	    case 'l' :
	    	lightOn = !lightOn;
	    	break;
	    case '1' :
	    	if(lightOn) {ambientLight = !ambientLight;}
	    	break;
	    case '2' :
	    	if(lightOn) {infiniteLight = !infiniteLight;} 
	    	break;
	    case '3' :
	    	if(lightOn) {pointLight = !pointLight;}
	    	break;
	    case '4' :
	    	if(lightOn) {attenuationLight = !attenuationLight;}
	    	break;
	    	
	    case 'T' :
	    case 't' : 
	    	testCase = (testCase+1)%numTestCase;
	    	drawTestCase();
	        break; 
	    case '<':  
	        Nsteps = Nsteps < 4 ? Nsteps: Nsteps / 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '>':
	        Nsteps = Nsteps > 190 ? Nsteps: Nsteps * 2;
	        System.out.printf( "Nsteps = %d \n", Nsteps);
	        drawTestCase();
	        break;
	    case '+':
	    	ns++;
	        drawTestCase();
	    	break;
	    case '-':
	    	if(ns>0)
	    		ns--;
	        drawTestCase();
	    	break;

	    	
	    default :
	        break;
	    }
	}

	public void keyPressed(KeyEvent key)
	{
	    switch (key.getKeyCode()) 
	    {
	    case KeyEvent.VK_ESCAPE:
	    	new Thread()
	        {
	    		public void run()
	    		{
	    			animator.stop();
	    		}
	        }.start();
	        System.exit(0);
	        break;
	    case KeyEvent.VK_UP:
	    	if(point_128y > 90) {
	    	point_128y -=5;
	    	point_308y -=5;
	    	viewing_center.y -= 5;}
	    	break;
	    case KeyEvent.VK_DOWN:
	    	if(point_308y <  400) {
	    	point_128y +=5;
	    	point_308y +=5;
	    	viewing_center.y += 5;}
	    	break;
	    case KeyEvent.VK_RIGHT:
	    	if(point_308y <  390) {
	    	point_128x +=5;
	    	point_308x +=5;
	    	viewing_center.x += 5;}
	    	break;
	    case KeyEvent.VK_LEFT:
	    	if(point_128x > 100) {
	    	point_128x -=5;
	    	point_308x -=5;
	    	viewing_center.x -=5;}
	    	break;
	      default:
	        break;
	    }
	}

	public void keyReleased(KeyEvent key)
	{
		// deliberately left blank
	}

	//************************************************** 
	// MouseListener and MouseMotionListener Interfaces
	//************************************************** 
	public void mouseClicked(MouseEvent mouse)
	{
		// deliberately left blank
	}
	  public void mousePressed(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      last_x = mouse.getX();
	      last_y = mouse.getY();
	      rotate_world = true;
	    }
	  }

	  public void mouseReleased(MouseEvent mouse)
	  {
	    int button = mouse.getButton();
	    if ( button == MouseEvent.BUTTON1 )
	    {
	      rotate_world = false;
	    }
	  }

	public void mouseMoved( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	/**
	   * Updates the rotation quaternion as the mouse is dragged.
	   * 
	   * @param mouse
	   *          The mouse drag event object.
	   */
	  public void mouseDragged(final MouseEvent mouse) {
	    if (this.rotate_world) {
	      // get the current position of the mouse
	      final int x = mouse.getX();
	      final int y = mouse.getY();

	      // get the change in position from the previous one
	      final int dx = x - this.last_x;
	      final int dy = y - this.last_y;

	      // create a unit vector in the direction of the vector (dy, dx, 0)
	      final float magnitude = (float)Math.sqrt(dx * dx + dy * dy);
	      if(magnitude > 0.0001)
	      {
	    	  // define axis perpendicular to (dx,-dy,0)
	    	  // use -y because origin is in upper lefthand corner of the window
	    	  final float[] axis = new float[] { -(float) (dy / magnitude),
	    			  (float) (dx / magnitude), 0 };

	    	  // calculate appropriate quaternion
	    	  final float viewing_delta = 3.1415927f / 180.0f;
	    	  final float s = (float) Math.sin(0.5f * viewing_delta);
	    	  final float c = (float) Math.cos(0.5f * viewing_delta);
	    	  final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s
	    			  * axis[2]);
	    	  this.viewing_quaternion = Q.multiply(this.viewing_quaternion);

	    	  // normalize to counteract acccumulating round-off error
	    	  this.viewing_quaternion.normalize();

	    	  // save x, y as last x, y
	    	  this.last_x = x;
	    	  this.last_y = y;
	          drawTestCase();
	      }
	    }

	  }
	  
	public void mouseEntered( MouseEvent mouse)
	{
		// Deliberately left blank
	}

	public void mouseExited( MouseEvent mouse)
	{
		// Deliberately left blank
	} 


	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	//************************************************** 
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	//************************************************** 
	
	
	

	void shadeTest(boolean doSmooth, int[][] depthbuffer_){
		
		//initialize depth buffer at background 
		for(int u = 0 ; u < depthbuffer_.length; u++) {
			for(int v = 0 ; v < depthbuffer_[u].length; v++) {
				depthbuffer_[u][v] = -1000;
			}
		}
		// the simple example scene includes one sphere and one torus
		float radius = (float)50.0;
        Sphere3D sphere = new Sphere3D(point_128x, point_128y, (float)128.0, (float)1.5*radius, Nsteps, Nsteps);
        Cylinder3D cylinder = new Cylinder3D(point_128x, point_308y, (float)128.0, (float)1.5*radius, radius, Nsteps);
       // Box3D box = new Box3D(point_308x, point_128y, (float)128.0, 50f);
        Superellipsoid superE = new Superellipsoid(point_308x, point_128y, (float)128.0, 50f, 50f, 50f, 0f, 4f, Nsteps, Nsteps);
        Torus3D torus = new Torus3D( point_308x, point_308y,(float)128.0, 15.0f, 40.0f, Nsteps, Nsteps);
        Material mat_sphere = new Material(new ColorType (1.0f, 0.0f,0.0f ), new ColorType (1.0f,0.0f,0.0f ), new ColorType (0.5f,0.5f,0.5f ), 1);
        InfiniteLight inf_Light = new InfiniteLight ( new ColorType (1.0f,1.0f,1.0f ),new Point3D (1.0f, 1.0f, 1.0f));
        PointLight point_light = new PointLight(new ColorType (1.0f,1.0f,0.0f), new Point3D ((float)111, (float)111, (float)111));
        AmbientLight amb_light = new AmbientLight(new ColorType(1.0f, 1.0f,1.0f ));
       
        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Point3D view_vector = new Point3D((float)0.0,(float)0.0,(float)1.0);
        
        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Point3D triangle_normal = new Point3D();
        
        // a triangle mesh
        int numberOfShapes = 4;
        
        Mesh3D mesh;
        
  		int i, j, n, m;
  		
  		// temporary variables for triangle 3D vertices and 3D normals
  		Point3D v0,v1, v2, n0, n1, n2;
  		
  		// projected triangle, with vertex colors
  		Point3D[] tri = {new Point3D(), new Point3D(), new Point3D()};
        
        for(int q=0; q<numberOfShapes; q++) {
  

		if(q==0) {
		mesh=sphere.mesh;
		n=sphere.get_n();
		m=sphere.get_m();}
		else if(q==1) {
		mesh=cylinder.mesh;
		n=sphere.get_n();
		m=4;}
		else if(q==2){
		mesh = superE.mesh;
		n=superE.get_n();
		m=superE.get_m();
		//System.out.println("test for box mesh");
		}else {
			mesh = torus.mesh;
			n = torus.get_n();
			m = torus.get_m();
		}
		
		
		
		// rotate the surface's 3D mesh using quaternion
		mesh.rotateMesh(viewing_quaternion, viewing_center);
		point_light.rotateLight(viewing_quaternion, viewing_center);
			
		// draw triangles for the current surface, using vertex colors
		for(i=0; i < m-1; ++i)
	    {
			for(j=0; j < n-1; ++j)
			{
				// ****************Implement Code here*******************//
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j];
				v2 = mesh.v[i+1][j+1];
				
				//boolean[] lightList = {ambientLight, infiniteLight, pointLight ,specularLight, diffuseLight};

				
				triangle_normal = computeTriangleNormal(v0,v1,v2);
				
				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{	
					
					
					
					if(doSmoothShading){
						
						//smooth shading (Gouraud rendering)
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j];
						n2 = mesh.n[i+1][j+1];
						
						
						
						if(pointLight) {

							tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(infiniteLight) {

							tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(ambientLight) {

							tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}
						if(attenuationLight) {

							tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}

						
						
						tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
						
						

						tri[0].x = (int)v0.x;
						tri[0].y = (int)v0.y;
						tri[0].z = (int)v0.z;
						tri[1].x = (int)v1.x;
						tri[1].y = (int)v1.y;
						tri[1].z = (int)v1.z;
						tri[2].x = (int)v2.x;
						tri[2].y = (int)v2.y;
						tri[2].z = (int)v2.z;
						
						depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
						
						
						}
					
					
					
					/**else if (phongShading){
					
					//smooth shading (Gouraud rendering)
					n0 = mesh.n[i][j];
					n1 = mesh.n[i+1][j];
					n2 = mesh.n[i+1][j+1];
					
					
					
					if(pointLight) {

						tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
					}
					if(infiniteLight) {

						tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
					}
					if(ambientLight) {

						tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
						
					}
					if(attenuationLight) {

						tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
						
					}

					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);
					
					
					}*/
					
					
				
				
				
				
					else{
					// flat shading: use the normal to the triangle itself
					n2 = n1 = n0 =  triangle_normal;
					
					if(pointLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, false).r ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, false).g ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, false).b ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					if(infiniteLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					if(ambientLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r ;
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g ;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b ;
						
					}
					if(attenuationLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, true).r ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, true).g ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, true).b ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);

					
					
					}
					
					
					//SketchBase.drawLine(buff, tri[0], tri[1]);
					//SketchBase.drawLine(buff, tri[1], tri[1]);
					//SketchBase.drawLine(buff, tri[0], tri[2]);

					//SketchBase.drawPoint(buff, tri[0]);
					//SketchBase.drawPoint(buff, tri[1]);
					//SketchBase.drawPoint(buff, tri[2]); 
					
					//SketchBase.phongRendering(buff, tri[1] , tri[2], tri[3],  n0, n1, n2,  mat_sphere, view_vector, amb_light, inf_Light, point_light ,lightList);
				
					//depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);

					
					
					//depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);

					
					
					//depthbuffer_ = SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
				}
				
				// ****************Implement Code here*******************//
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j+1];
				v2 = mesh.v[i][j+1];
				
				triangle_normal = computeTriangleNormal(v0,v1,v2);
				
				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{	
					if(doSmoothShading){
						
						//smooth shading (Gouraud rendering)
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j];
						n2 = mesh.n[i+1][j+1];
						
						
						
						if(pointLight) {

							tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(infiniteLight) {

							tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(ambientLight) {

							tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}
						if(attenuationLight) {

							tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}

						
						
						tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
						
						

						tri[0].x = (int)v0.x;
						tri[0].y = (int)v0.y;
						tri[0].z = (int)v0.z;
						tri[1].x = (int)v1.x;
						tri[1].y = (int)v1.y;
						tri[1].z = (int)v1.z;
						tri[2].x = (int)v2.x;
						tri[2].y = (int)v2.y;
						tri[2].z = (int)v2.z;
						
						depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
						
						
						}
					
					
					
					/**else if (phongShading){
					
					//smooth shading (Gouraud rendering)
					n0 = mesh.n[i][j];
					n1 = mesh.n[i+1][j];
					n2 = mesh.n[i+1][j+1];
					
					
					
					if(pointLight) {

						tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
					}
					if(infiniteLight) {

						tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
					}
					if(ambientLight) {

						tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
						
					}
					if(attenuationLight) {

						tri[0].c.r += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
						
					}

					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);
					
					
					}*/
					
					
				
				
				
				
					else{
					// flat shading: use the normal to the triangle itself
					n2 = n1 = n0 =  triangle_normal;
					
					if(pointLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, false).r ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, false).g ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, false).b ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					if(infiniteLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					if(ambientLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r ;
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g ;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b ;
						
					}
					if(attenuationLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, true).r ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, true).g ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = point_light.applyLight(mat_sphere, view_vector, triangle_normal, v0,  specularLight, diffuseLight, true).b ;//+ amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b + inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);

					
					
					}

					
					
					//depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);

					
					//depthbuffer_ = SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
   
				}
			}	
	    }
}
}
	
	
	
	
	
	
	void shadeTest1(boolean doSmooth, int[][] depthbuffer_){
		
		//initialize depth buffer at background 
		for(int u = 0 ; u < depthbuffer_.length; u++) {
			for(int v = 0 ; v < depthbuffer_[u].length; v++) {
				depthbuffer_[u][v] = -1000;
			}
		}
		// the simple example scene includes one sphere and one torus
		float radius = (float)50.0;
        Sphere3D sphere = new Sphere3D(point_128x, point_128y, (float)128.0, (float)1.5*radius, Nsteps, Nsteps);
        Cylinder3D cylinder = new Cylinder3D(point_128x, point_308y, (float)128.0, (float)1.5*radius, radius, Nsteps);
        Box3D box = new Box3D(point_308x, point_128y, (float)128.0, 50f);
        //Superellipsoid superE = new Superellipsoid(point_308x, point_128y, (float)128.0, 50f, 50f, 50f, 2f, 2f, Nsteps, Nsteps);
        Torus3D torus = new Torus3D( point_308x, point_308y,(float)128.0, 15.0f, 40.0f, Nsteps, Nsteps);
        Material mat_sphere = new Material(new ColorType (1.0f, 1.0f,0.0f ), new ColorType (1.0f,0.0f,1.0f ), new ColorType (0.5f,0.5f,0.5f ), 1);
        InfiniteLight inf_Light = new InfiniteLight ( new ColorType (1.0f,1.0f,0.0f ),new Point3D (1.0f, 1.0f, 1.0f));
        PointLight point_light = new PointLight(new ColorType (1.0f,1.0f,0.0f), new Point3D ((float)111, (float)111, (float)111));
        AmbientLight amb_light = new AmbientLight(new ColorType(1.0f, 1.0f,1.0f ));
       
        // view vector is defined along z axis
        // this example assumes simple othorgraphic projection
        // view vector is used in 
        //   (a) calculating specular lighting contribution
        //   (b) backface culling / backface rejection
        Point3D view_vector = new Point3D((float)0.0,(float)0.0,(float)1.0);
        
        // normal to the plane of a triangle
        // to be used in backface culling / backface rejection
        Point3D triangle_normal = new Point3D();
        
        // a triangle mesh
        int numberOfShapes = 4;
        
        Mesh3D mesh;
        
  		int i, j, n, m;
  		
  		// temporary variables for triangle 3D vertices and 3D normals
  		Point3D v0,v1, v2, n0, n1, n2;
  		
  		// projected triangle, with vertex colors
  		Point3D[] tri = {new Point3D(), new Point3D(), new Point3D()};
        
        for(int q=0; q<numberOfShapes; q++) {
  

		if(q==0) {
		mesh=sphere.mesh;
		n=sphere.get_n();
		m=sphere.get_m();}
		else if(q==1) {
		mesh=cylinder.mesh;
		n=sphere.get_n();
		m=4;}
		else if(q==2){
		mesh = box.mesh;
		n=5;
		m=4;
		//System.out.println("test for box mesh");
		}else {
			mesh = torus.mesh;
			n = torus.get_n();
			m = torus.get_m();
		}
		
		
		
		// rotate the surface's 3D mesh using quaternion
		mesh.rotateMesh(viewing_quaternion, viewing_center);
		//point_light.rotateLight(viewing_quaternion, viewing_center);
			
		// draw triangles for the current surface, using vertex colors
		for(i=0; i < m-1; ++i)
	    {
			for(j=0; j < n-1; ++j)
			{
				// ****************Implement Code here*******************//
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j];
				v2 = mesh.v[i+1][j+1];
				
				//boolean[] lightList = {ambientLight, infiniteLight, pointLight ,specularLight, diffuseLight};

				
				triangle_normal = computeTriangleNormal(v0,v1,v2);
				
				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{	
					
					
					
					if(doSmoothShading){
						
						//smooth shading (Gouraud rendering)
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j];
						n2 = mesh.n[i+1][j+1];
						
						
						
						if(pointLight) {

							tri[0].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(infiniteLight) {

							tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(ambientLight) {

							tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}
						if(attenuationLight) {

							tri[0].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}

						
						
						tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
						
						

						tri[0].x = (int)v0.x;
						tri[0].y = (int)v0.y;
						tri[0].z = (int)v0.z;
						tri[1].x = (int)v1.x;
						tri[1].y = (int)v1.y;
						tri[1].z = (int)v1.z;
						tri[2].x = (int)v2.x;
						tri[2].y = (int)v2.y;
						tri[2].z = (int)v2.z;
						
						depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
						
						
						}
					
					
					
					else if (phongShading){
					
					//smooth shading (Gouraud rendering)
					n0 = mesh.n[i][j];
					n1 = mesh.n[i+1][j];
					n2 = mesh.n[i+1][j+1];
					
					
					
					if(pointLight) {

						tri[0].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
					}
					if(infiniteLight) {

						tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
					}
					if(ambientLight) {

						tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
						
					}
					if(attenuationLight) {

						tri[0].c.r +=0;// point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[0].c.g +=0;// point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
						tri[0].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
						
						tri[1].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[1].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
						tri[1].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
						
						tri[2].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
						tri[2].c.b +=0;// point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
					
						
					}

					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);
					
					
					}
					
					
				
				
				
				
					else{
					// flat shading: use the normal to the triangle itself
					n2 = n1 = n0 =  triangle_normal;
					
					if(pointLight) {
						
					}
					if(infiniteLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					if(ambientLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r ;
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g ;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b ;
						
					}
					if(attenuationLight) {
						
					}
					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);

					
					
					}
					
					
					//SketchBase.drawLine(buff, tri[0], tri[1]);
					//SketchBase.drawLine(buff, tri[1], tri[1]);
					//SketchBase.drawLine(buff, tri[0], tri[2]);

					//SketchBase.drawPoint(buff, tri[0]);
					//SketchBase.drawPoint(buff, tri[1]);
					//SketchBase.drawPoint(buff, tri[2]); 
					
					//SketchBase.phongRendering(buff, tri[1] , tri[2], tri[3],  n0, n1, n2,  mat_sphere, view_vector, amb_light, inf_Light, point_light ,lightList);
				
					//depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);

					
					
					//depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);

					
					
					//depthbuffer_ = SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
				}
				
				// ****************Implement Code here*******************//
				v0 = mesh.v[i][j];
				v1 = mesh.v[i+1][j+1];
				v2 = mesh.v[i][j+1];
				
				triangle_normal = computeTriangleNormal(v0,v1,v2);
				
				if(view_vector.dotProduct(triangle_normal) > 0.0)  // front-facing triangle?
				{	
					if(doSmoothShading){
						
						//smooth shading (Gouraud rendering)
						n0 = mesh.n[i][j];
						n1 = mesh.n[i+1][j];
						n2 = mesh.n[i+1][j+1];
						
						
						
						if(pointLight) {

							tri[0].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g +=  0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, false).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, false).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, false).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(infiniteLight) {

							tri[0].c.r += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
						}
						if(ambientLight) {

							tri[0].c.r += amb_light.applyLight(mat_sphere, view_vector, n0).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += amb_light.applyLight(mat_sphere, view_vector, n0).g;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += amb_light.applyLight(mat_sphere, view_vector, n0).b;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += amb_light.applyLight(mat_sphere, view_vector, n1).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += amb_light.applyLight(mat_sphere, view_vector, n1).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += amb_light.applyLight(mat_sphere, view_vector, n1).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += amb_light.applyLight(mat_sphere, view_vector, n2).r;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += amb_light.applyLight(mat_sphere, view_vector, n2).g ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += amb_light.applyLight(mat_sphere, view_vector, n2).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}
						if(attenuationLight) {

							tri[0].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).r ;// +  inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[0].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).g;
							tri[0].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n0, v0, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n0, specularLight, diffuseLight).b;
							
							tri[1].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).r ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[1].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1,  specularLight, diffuseLight, true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).g;
							tri[1].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n1, v1, specularLight, diffuseLight, true).b ;//+ inf_Light.applyLight(mat_sphere, view_vector, n1, specularLight, diffuseLight).b;
							
							tri[2].c.r += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2,  specularLight, diffuseLight, true).r ;// + inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
							tri[2].c.g += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight,  true).g  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).g;
							tri[2].c.b += 0;//point_light.applyLight(mat_sphere, view_vector, n2, v2, specularLight, diffuseLight, true).b  ;//+ inf_Light.applyLight(mat_sphere, view_vector, n2, specularLight, diffuseLight).b;
						
							
						}

						
						
						tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
						
						

						tri[0].x = (int)v0.x;
						tri[0].y = (int)v0.y;
						tri[0].z = (int)v0.z;
						tri[1].x = (int)v1.x;
						tri[1].y = (int)v1.y;
						tri[1].z = (int)v1.z;
						tri[2].x = (int)v2.x;
						tri[2].y = (int)v2.y;
						tri[2].z = (int)v2.z;
						
						depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
						
						
						}
					
					
					
					else if (phongShading){
					
					//smooth shading (Gouraud rendering)
					n0 = mesh.n[i][j];
					n1 = mesh.n[i+1][j];
					n2 = mesh.n[i+1][j+1];
					

					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					

					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);
					
					
					}
					
				
				
					else{
					// flat shading: use the normal to the triangle itself
					n2 = n1 = n0 =  triangle_normal;
					
					if(pointLight) {
						
					}
					if(infiniteLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = 0;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).r;//inf_Light.applyLight(mat_sphere, view_vector, triangle_normal);
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).g;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = inf_Light.applyLight(mat_sphere, view_vector, triangle_normal, specularLight, diffuseLight).b;
						
					}
					if(ambientLight) {
						tri[2].c.r += tri[1].c.r = tri[0].c.r = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).r ;
						tri[2].c.g += tri[1].c.g= tri[0].c.g =  amb_light.applyLight(mat_sphere, view_vector, triangle_normal).g ;
						tri[2].c.b += tri[1].c.b = tri[0].c.b = amb_light.applyLight(mat_sphere, view_vector, triangle_normal).b ;
						
					}
					if(attenuationLight) {
						
					}
					
					
					tri[0].c.clamp(); tri[1].c.clamp(); tri[2].c.clamp();
					
					tri[0].x = (int)v0.x;
					tri[0].y = (int)v0.y;
					tri[0].z = (int)v0.z;
					tri[1].x = (int)v1.x;
					tri[1].y = (int)v1.y;
					tri[1].z = (int)v1.z;
					tri[2].x = (int)v2.x;
					tri[2].y = (int)v2.y;
					tri[2].z = (int)v2.z;
					
					depthbuffer_ = SketchBase.drawTriangle1(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);

					
					
					}

					
					
					//depthbuffer_ = SketchBase.drawTrianglePhong(buff, tri[0], tri[1], tri[2], n0, n1, n2, doSmoothShading, depthbuffer_, point_light, inf_Light, amb_light, mat_sphere, view_vector);

					
					//depthbuffer_ = SketchBase.drawTriangle(buff, tri[0], tri[1], tri[2], doSmoothShading, depthbuffer_);
   
				}
			}	
	    }
      }
	}
	
	
	

	
	
	
	
	

	
	
	

	
	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Point3D computeTriangleNormal(Point3D v0, Point3D v1, Point3D v2)
	{
		Point3D e0 = v1.minus(v2);
		Point3D e1 = v0.minus(v2);
		Point3D norm = e0.crossProduct(e1);
		
		if(norm.magnitude()>0.000001)
			norm.normalize();
		else 	// detect degenerate triangle and set its normal to zero
			norm.set((float)0.0,(float)0.0,(float)0.0);

		return norm;
	}

}