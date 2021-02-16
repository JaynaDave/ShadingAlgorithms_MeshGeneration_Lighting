//****************************************************************************
// SketchBase.  
//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)

import java.awt.image.BufferedImage;
import java.util.*;

public class SketchBase 
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	/**********************************************************************
	 * Draws a point.
	 * This is achieved by changing the color of the buffer at the location
	 * corresponding to the point. 
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p
	 *          Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, Point3D _p)
	{
		Point2D p = new Point2D((int)_p.x,(int) _p.y, _p.c);
		if(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight())
			buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());	
	}
	
	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly 
	 * interpolating RGB color along line segment.
	 * This method only uses integer arithmetic.
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given endpoint of the line.
	 * @param p2
	 *          Second given endpoint of the line.
	 */
	public static int[][] drawLine(BufferedImage buff, Point3D p1, Point3D p2,  int[][] _depthbuffer)
	{
	    int x0=(int)p1.x, y0=(int)p1.y, z0=(int)p1.z;
	    int xEnd=(int)p2.x, yEnd=(int)p2.y, zEnd=(int)p2.z;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0), dz = Math.abs(zEnd-z0);

	    if(dx==0 && dy==0)
	    {
	    	
	    		
	    	if(z0 > _depthbuffer[x0][y0]) {
	    		drawPoint(buff,p1);
	    		_depthbuffer[x0][y0] = z0;
	    	}
	    	
	    	
	    	return _depthbuffer;
	    }
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {
	    	x0=(int)p1.y; 
	    	y0=(int)p1.x;
	    	z0=(int)p1.z;
	    	xEnd=(int)p2.y; 
	    	yEnd=(int)p2.x;
	    	zEnd=(int)p2.z;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    	dz = Math.abs(zEnd - z0);
	    }
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0, z = z0;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    int step_z = z0<zEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get r,g,b integer values at the end points
	    int r0=p1.c.getR_int(), rEnd=p2.c.getR_int();
	    int g0=p1.c.getG_int(), gEnd=p2.c.getG_int();
	    int b0=p1.c.getB_int(), bEnd=p2.c.getB_int();
	    
	    // compute the change in r,g,b 
	    int dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);
	    
	    // set step increment to be positive or negative 
	    int step_r = r0<rEnd ? 1 : -1;
	    int step_g = g0<gEnd ? 1 : -1;
	    int step_b = b0<bEnd ? 1 : -1;
	    
	    // compute whole step in each color that is taken each time through loop
	    int whole_step_r = step_r*(dr/dx);
	    int whole_step_g = step_g*(dg/dx);
	    int whole_step_b = step_b*(db/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dr=dr%dx;
	    dg=dg%dx; 
	    db=db%dx;
	    
	    // initialize decision parameters for red, green, and blue
	    int p_r = 2 * dr - dx;
	    int twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
	    int r=r0;
	    
	    int p_g = 2 * dg - dx;
	    int twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
	    int g=g0;
	    
	    int p_b = 2 * db - dx;
	    int twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
	    int b=b0;
	    
	    // draw start pixel
	    
	    
	    
	    	if(x_y_role_swapped)
		    {
	    		
	    		if(z > _depthbuffer[y][x]) {
	    			if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
	    				buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);}
		    	_depthbuffer[y][x] = z;
	    		}
		    }
		    else
		    {
		    	if(z > _depthbuffer[x][y]) {
		    			if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()) {
		    					buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);}
		    	
		    			_depthbuffer[x][y] = z;
		    	}

		    }
		    	
		    	
	    
	    
	    while (x != xEnd) 
	    {
	    	
	    	
	    	
	    	// increment x and y
	    	x+=step_x;
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y+=step_y;
	    		z+= step_z;
	    		p += twoDyMinusDx;
	    	}
		        
	    	// increment r by whole amount slope_r, and correct for accumulated error if needed
	    	r+=whole_step_r;
	    	if (p_r < 0)
	    		p_r += twoDr;
	    	else 
	    	{
	    		r+=step_r;
	    		p_r += twoDrMinusDx;
	    	}
		    
	    	// increment g by whole amount slope_b, and correct for accumulated error if needed  
	    	g+=whole_step_g;
	    	if (p_g < 0)
	    		p_g += twoDg;
	    	else 
	    	{
	    		g+=step_g;
	    		p_g += twoDgMinusDx;
	    	}
		    
	    	// increment b by whole amount slope_b, and correct for accumulated error if needed
	    	b+=whole_step_b;
	    	if (p_b < 0)
	    		p_b += twoDb;
	    	else 
	    	{
	    		b+=step_b;
	    		p_b += twoDbMinusDx;
	    	}
		    
	    	
	    	
	    	
	    	
	    	
	    		
		    	if(x_y_role_swapped)
		    	{
		    		if(z > _depthbuffer[y][x]) {
		    			if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
		    				buff.setRGB(y, buff.getHeight()-x-1, (r<<16) | (g<<8) | b);}
		    			_depthbuffer[y][x] = z;
		    		}
		    	}
		    	else
		    	{
		    		if(z > _depthbuffer[x][y]) {
		    			if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()) {
		    				buff.setRGB(x, buff.getHeight()-y-1, (r<<16) | (g<<8) | b);}
		    			_depthbuffer[x][y] = z;
		    		}
		    	}
		    		
	    	
	    	
	    	
	    	
	    }
	    return _depthbuffer;
	}

	/**********************************************************************
	 * Draws a filled triangle. 
	 * The triangle may be filled using flat fill or smooth fill. 
	 * This routine fills columns of pixels within the left-hand part, 
	 * and then the right-hand part of the triangle.
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */
	public static int[][] drawTriangle(BufferedImage buff, Point3D p1, Point3D p2, Point3D p3, boolean do_smooth, int[][] _depthbuffer) {
	
		int[][] depth_buffer = _depthbuffer;
	    // sort the triangle vertices by ascending x value
	    Point3D p[] = sortTriangleVerts(p1,p2,p3);
	    
	    int x; 
	    float y_a, y_b, z_a, z_b;
	    float dy_a, dy_b, dz_a, dz_b;
	    float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;
	    
	    Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);
	    
	    if(!do_smooth)
	    {
	    	side_a.c = new ColorType(p1.c);
	    	side_b.c = new ColorType(p1.c);
	    	
	    }
	    
	    y_b = p[0].y;z_b = p[0].z;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    dz_b = ((float)(p[2].z - p[0].z))/(p[2].x - p[0].x);
	    
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for segment b
	    	dr_b = ((float)(p[2].c.r - p[0].c.r))/(p[2].x - p[0].x);
	    	dg_b = ((float)(p[2].c.g - p[0].c.g))/(p[2].x - p[0].x);
	    	db_b = ((float)(p[2].c.b - p[0].c.b))/(p[2].x - p[0].x);
	    	
	    	
	    }
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y;z_a = p[0].z;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);
	    	dz_a = ((float)(p[1].z - p[0].z))/(p[1].x - p[0].x);
		    
	    	if(do_smooth)
	    	{
	    		// calculate slopes in r, g, b for segment a
	    		dr_a = ((float)(p[1].c.r - p[0].c.r))/(p[1].x - p[0].x);
	    		dg_a = ((float)(p[1].c.g - p[0].c.g))/(p[1].x - p[0].x);
	    		db_a = ((float)(p[1].c.b - p[0].c.b))/(p[1].x - p[0].x);
	    	}
		    
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = (int)p[0].x; x < p[1].x; ++x)
		    {
		    	drawLine(buff, side_a, side_b, _depthbuffer);

		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	z_a += dz_a;
		    	z_b += dz_b;
		    	side_a.y = (int)y_a; side_a.z = z_a;
		    	side_b.y = (int)y_b; side_b.z = z_b;
		    	if(do_smooth)
		    	{
		    		side_a.c.r +=dr_a;
		    		side_b.c.r +=dr_b;
		    		side_a.c.g +=dg_a;
		    		side_b.c.g +=dg_b;
		    		side_a.c.b +=db_a;
		    		side_b.c.b +=db_b;
		    	}
		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return depth_buffer;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new Point3D(p[1]);
	    if(!do_smooth)
	    	side_a.c =new ColorType(p1.c);
	    
	    y_a = p[1].y; z_a = p[1].z;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);
	    dz_a = ((float)(p[2].z - p[1].z))/(p[2].x - p[1].x);
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for replacement for segment a
	    	dr_a = ((float)(p[2].c.r - p[1].c.r))/(p[2].x - p[1].x);
	    	dg_a = ((float)(p[2].c.g - p[1].c.g))/(p[2].x - p[1].x);
	    	db_a = ((float)(p[2].c.b - p[1].c.b))/(p[2].x - p[1].x);
	    }

	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = (int)p[1].x; x <= p[2].x; ++x)
	    {
	    	depth_buffer = drawLine(buff, side_a, side_b, depth_buffer);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	z_b += dz_b;
	    	z_a += dz_a;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;
	    	side_a.z = (int)z_a;
	    	side_b.z = (int)z_b;
	    	if(do_smooth)
	    	{
	    		side_a.c.r +=dr_a;
	    		side_b.c.r +=dr_b;
	    		side_a.c.g +=dg_a;
	    		side_b.c.g +=dg_b;
	    		side_a.c.b +=db_a;
	    		side_b.c.b +=db_b;
	    	}
	    }
	    return depth_buffer;
	}
	
	
	
	
	public static void drawPoint1(BufferedImage buff, Point2D p)
	{
		//Point2D p = new Point2D((int)_p.x,(int) _p.y, _p.c);
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());
		
		
	}
	
	//////////////////////////////////////////////////
	//	Implement the following two functions
	//////////////////////////////////////////////////
	
	public static ColorType colorValues(Point3D p1, Point3D p2, Point3D current) {
		Point2D colorPoint = new Point2D();
		double p1_distance = Math.sqrt(Math.pow(p1.y - current.y, 2) + Math.pow(p1.x - current.x,2));
		double p2_distance = Math.sqrt(Math.pow(p2.y - current.y, 2) + Math.pow(p2.x - current.x,2));
		double total_distance = Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x,2));
		
		colorPoint.c.r = (float)(p2.c.r*(p1_distance/total_distance) + p1.c.r*(p2_distance/total_distance));
		colorPoint.c.b = (float)(p2.c.b*(p1_distance/total_distance) + p1.c.b*(p2_distance/total_distance));
		colorPoint.c.g  = (float)(p2.c.g*(p1_distance/total_distance) + p1.c.g*(p2_distance/total_distance));
		
		return colorPoint.c;
	}
	
	public static float calcZ(Point3D p1, Point3D p2, Point3D current) {
	
		double p1_distance = Math.sqrt(Math.pow(p1.y - current.y, 2) + Math.pow(p1.x - current.x,2));
		double p2_distance = Math.sqrt(Math.pow(p2.y - current.y, 2) + Math.pow(p2.x - current.x,2));
		double total_distance = Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x,2));
		
		float z = (float)(p2.z*(p1_distance/total_distance) + p1.z*(p2_distance/total_distance));
		
		
		return z;
	}
	
	
	// draw a line segment
	public static int[][] drawLine1(BufferedImage buff, Point3D p1, Point3D p2, int[][] depthBuffer)
	{
		// replace the following line with your implementation
		

		
		//determine which is the leftmost pixel the following code will run from line being drawn left to right
		Point3D leftPoint = new Point3D();
		Point3D rightPoint = new Point3D();
		if((p1.x - p2.x) < 0) {
			leftPoint.x = p1.x;
			leftPoint.y = p1.y;
			leftPoint.z= p1.z;
			rightPoint.x = p2.x;
			rightPoint.y = p2.y;
			rightPoint.z = p2.z;
			  
		}else {
			leftPoint.x = p2.x;
			leftPoint.y = p2.y;
			leftPoint.z = p2.z;
			rightPoint.x = p1.x;
			rightPoint.y = p1.y;
			rightPoint.z = p1.z;
		}
		//System.out.println("(" + rightPoint.x + ", " + rightPoint.y + ")");
		//System.out.println("(" + leftPoint.x + ", " + leftPoint.y + ")");
		
		//point that will trace the line
		Point3D point = new Point3D();
		point.x = leftPoint.x;
		point.y = leftPoint.y;
		point.z = leftPoint.z; 
		
		//undefined slope
		if(rightPoint.x - leftPoint.x == 0) {		
			if(rightPoint.y - leftPoint.y >0) {			
				point.y = leftPoint.y;
				while(point.y <= rightPoint.y) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
					drawPoint1(buff, new Point2D((int)point.x,(int) point.y, colorValues(p1, p2, point)));
					depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
					}
				
					point.y++;
					point.z = calcZ(rightPoint, leftPoint,point);
			}
			}else if(rightPoint.y - leftPoint.y <0){
				point.y= rightPoint.y;
				while(point.y <= leftPoint.y) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						drawPoint1(buff, new Point2D((int)point.x,(int) point.y, colorValues(p1, p2, point)));
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.y++;
					point.z = calcZ(rightPoint, leftPoint,point);
			}
			}
			
		}else{
			float slope = (float)(rightPoint.y - leftPoint.y)/(rightPoint.x - leftPoint.x);
			//System.out.println(slope);
			if (slope< 1 && slope >=0) {
				int p_k = 2*((int)rightPoint.y - (int)leftPoint.y) - ((int)rightPoint.x - (int)leftPoint.x);
				while (point.x <= rightPoint.x) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						drawPoint1(buff, new Point2D((int)point.x,(int) point.y, colorValues(p1, p2, point)));
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					
					point.x++;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)rightPoint.y - (int)leftPoint.y);
					}else {
						p_k = p_k + 2*((int)rightPoint.y - (int)leftPoint.y) - 2*((int)rightPoint.x - (int)leftPoint.x);
						point.y++;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
				}
			
			}else if(slope >= 1) {
				int p_k = 2*((int)rightPoint.x - (int)leftPoint.x)- ((int)rightPoint.y - (int)leftPoint.y);
				while (point.y <= rightPoint.y) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						drawPoint1(buff, new Point2D((int)point.x,(int) point.y, colorValues(p1, p2, point)));
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.y++;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x);
					}else {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x) - 2*((int)rightPoint.y - (int)leftPoint.y);
						point.x++;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
				}
				
			}else if(slope <0 && slope >-1) {
				int p_k = 2*((int)leftPoint.y - (int)rightPoint.y) - ((int)rightPoint.x - (int)leftPoint.x);
				while (point.x <= rightPoint.x) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						drawPoint1(buff, new Point2D((int)point.x,(int) point.y, colorValues(p1, p2, point)));
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.x++;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)leftPoint.y - (int)rightPoint.y);
					}else {
						p_k = p_k + 2*((int)leftPoint.y - (int)rightPoint.y) - 2*((int)rightPoint.x - (int)leftPoint.x);
						point.y--;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
			}
			}
			else{
				
				int p_k = 2*((int)rightPoint.x - (int)leftPoint.x) - ((int)leftPoint.y - (int)rightPoint.y);
				while (point.y >= rightPoint.y) {
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						drawPoint1(buff, new Point2D((int)point.x,(int) point.y, colorValues(p1, p2, point)));
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.y--;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x);
					}else {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x) - 2*((int)leftPoint.y - (int)rightPoint.y);
						point.x++;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
			}
				
			
			}
		}
		
		return depthBuffer;
	}
	
	// draw a triangle
	public static int[][] drawTriangle1(BufferedImage buff, Point3D p1, Point3D p2, Point3D p3, boolean do_smooth, int[][] _depthBuffer )
	{
		
		int[][] dBuff =  _depthBuffer;

		
		
		//System.out.println("(" + p1.x + "," + p1.y + ")");
		//System.out.println("(" + p2.x + "," + p2.y + ")");
		//System.out.println("(" + p3.x + "," + p3.y + ")");
		//System.out.println();

		

	
		//calculate the incenter of the triangle to then determine where the vertices in relation to that point
		double y_coord =((Math.sqrt(Math.pow(p2.x-p3.x, 2) + Math.pow(p2.y - p3.y, 2))*p1.y) + (Math.sqrt(Math.pow(p1.x-p3.x,2) + Math.pow(p1.y - p3.y,2))*p2.y) + (Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y - p2.y,2))*p3.y))/(Math.sqrt(Math.pow(p2.x-p3.x,2) + Math.pow(p2.y - p3.y,2))+ Math.sqrt(Math.pow(p1.x-p3.x,2) + Math.pow(p1.y - p3.y,2))+ Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y - p2.y,2)));
	
		
		//determine the distances between the vertices to the incenter y coordinate to determine where the divide the triangle
		double p1_distance = Math.abs(p1.y - y_coord);
		double p2_distance = Math.abs(p2.y - y_coord);
		double p3_distance = Math.abs(p3.y - y_coord);
		
		Point3D top = new Point3D();
		Point3D mid_left = new Point3D();
		Point3D mid_right = new Point3D();
		Point3D bottom = new Point3D();
		
		
		if(p2_distance <= p1_distance && p2_distance <= p3_distance) {
			mid_right.x = p2.x; mid_right.y = p2.y;  mid_right.z = p2.z; mid_right.c = p2.c;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p1.y - p3.y == 0) {intersect_x = p1.x;}else {intersect_x = ((p2.y - p1.y)*(p1.x-p3.x)/(p1.y - p3.y)) + p1.x;}
			int intersect = (int)(intersect_x);
					
		
			mid_left.y = p2.y; mid_left.x = intersect; mid_left.z = calcZ(p1, p3, mid_left); ;mid_left.c = colorValues(p1, p3, mid_left);
			if(p1.y - p3.y > 0) {
				top.y = p1.y; top.x = p1.x;  top.z = p1.z; top.c = p1.c;
				bottom.y = p3.y; bottom.x = p3.x; bottom.z = p3.z; bottom.c = p3.c; 
			}else {
				top.y = p3.y; top.x = p3.x; top.z = p3.z; top.c = p3.c; 
				bottom.y = p1.y; bottom.x = p1.x;bottom.z = p1.z; bottom.c = p1.c; 
			}
		}else if(p1_distance <= p2_distance && p1_distance <= p3_distance){
			mid_right.x = p1.x; mid_right.y = p1.y;  mid_right.z = p1.z; mid_right.c= p1.c;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p2.y - p3.y == 0) { intersect_x = p2.x; }else {intersect_x = ((p1.y - p2.y)*(p2.x-p3.x)/(p2.y - p3.y)) + p2.x;}
			int intersect = (int)(intersect_x);
			
			mid_left.y = p1.y; mid_left.x = intersect; mid_left.z = calcZ(p2, p3, mid_left); mid_left.c = colorValues(p2, p3, mid_left);
			if(p2.y - p3.y > 0) {
				top.y = p2.y; top.x = p2.x; top.z = p2.z; top.c = p2.c; 
				bottom.y = p3.y; bottom.x = p3.x;bottom.z = p3.z; bottom.c = p3.c;
			}else {
				top.y = p3.y; top.x = p3.x; top.z = p3.z; top.c = p3.c; 
				bottom.y = p2.y; bottom.x = p2.x; bottom.z = p2.z; bottom.c = p2.c;
			}
			
		}else {
			mid_right.x = p3.x; mid_right.y = p3.y; mid_right.z = p3.z; mid_right.c = p3.c;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p2.y - p1.y == 0) { intersect_x = p3.x; }else {intersect_x = ((p3.y - p2.y)*(p2.x-p1.x)/(p2.y - p1.y)) + p2.x;}
			int intersect = (int)(intersect_x);
			
			mid_left.y = p3.y; mid_left.x = intersect; mid_left.z = calcZ(p1, p2, mid_left); mid_left.c = colorValues(p1, p2, mid_left);
			if(p2.y - p1.y > 0) {
				top.y = p2.y; top.x = p2.x; top.z = p2.z; top.c = p2.c;
				bottom.y = p1.y; bottom.x = p1.x; bottom.z = p1.z; bottom.c = p1.c; 
			}else {
				top.y = p1.y; top.x = p1.x; top.z = p1.z;  top.c = p1.c; 
				bottom.y = p2.y; bottom.x = p2.x;bottom.z = p2.z; bottom.c = p2.c;
			}
		}
		
		
		//System.out.println("top "+ "(" + top.x + "," + top.y + ")");
		//System.out.println("mid " + "(" + mid_right.x + "," + mid_right.y + ")");
		//System.out.println("mid " + "(" + mid_left.x + "," + mid_left.y + ")");
		//System.out.println("bottom " + "(" + bottom.x + "," + bottom.y + ")");
		//System.out.println();



		
		//top point flat bottom triangle	
		
		//initialize values that will trace the sides of the triangle to fill it in
		Point3D trace1 = new Point3D(top);
		Point3D trace2 = new Point3D(top);
	
		
		
		float slope1;//calculate inverses of slopes to determine the next side point to trace to 
		if((top.y - mid_right.y) == 0) {slope1 = 0;}else {slope1 = (float)(top.x - mid_right.x)/(top.y - mid_right.y);}
		float slope2;
		if(top.y - mid_left.y == 0) {slope2 = 0;}else {slope2 = (float)(top.x - mid_left.x)/(top.y - mid_left.y);}
		for(int i = 0; i <=(top.y - mid_right.y); i++) {
			
			//check the do_smooth boolean
			if(do_smooth) {trace1.c = colorValues(top, mid_right, trace1); trace2.c = colorValues(top, mid_left, trace2);}
			else {trace1.c = p1.c; trace2.c = p1.c; }
			
			trace1.z = calcZ(top, mid_right, trace1);
			trace2.z = calcZ(top, mid_left, trace2);
			
			 dBuff = drawLine1(buff, trace1, trace2, dBuff);
			trace1.y--;
			trace1.x = Math.round((trace1.y - top.y)*slope1 + top.x);
			trace2.y--;
			trace2.x = Math.round((trace2.y - top.y)*slope2 + top.x);
			
			
		}
		
		//flat top, point bottom triangle
		
		//initialize values that will trace the sides of the triangle to fill it in
		Point3D trace3 = new Point3D(bottom);
		Point3D trace4 = new Point3D(bottom);
		
		
		float slope_left;//calculate inverses of slopes to determine the next side point to trace to 
		if(mid_right.y - bottom.y == 0){slope_left = 0;}else {slope_left = (float)(mid_right.x - bottom.x)/(mid_right.y - bottom.y);}
		float slope_right;
		if(mid_left.y - bottom.y == 0){slope_right = 0;}else {slope_right = (float)(mid_left.x - bottom.x)/(mid_left.y - bottom.y);}
		for(int i = 0; i <=(mid_right.y - bottom.y); i++) {
			
			
			//check do_smooth boolean
			if(do_smooth) {trace3.c = colorValues(bottom, mid_right, trace3); trace4.c = colorValues(bottom, mid_left, trace4);}
			else {trace3.c = p1.c; trace4.c = p1.c; }
			
			trace3.z = calcZ(bottom, mid_right, trace3);
			trace4.z = calcZ(bottom, mid_left, trace4);
		
			
			 dBuff = drawLine1(buff, trace3, trace4, dBuff);
			trace3.y++;
			trace3.x = Math.round((trace3.y - bottom.y)*slope_left + bottom.x);
			trace4.y++;
			trace4.x = Math.round((trace4.y - bottom.y)*slope_right + bottom.x);
			
			
		}
		
	return dBuff;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void drawPointPhong(BufferedImage buff, Point3D _p)
	{
		Point2D p = new Point2D((int)_p.x,(int) _p.y, _p.c);
		buff.setRGB(p.x, buff.getHeight()-p.y-1, p.c.getRGB_int());
		
		
	}
	
	//////////////////////////////////////////////////
	//	Implement the following two functions
	//////////////////////////////////////////////////
	
	public static Point3D newNorm(Point3D p1, Point3D n1 , Point3D p2, Point3D n2, Point3D current) {
		Point3D newNormal = new Point3D();
		double p1_distance = Math.sqrt(Math.pow(p1.y - current.y, 2) + Math.pow(p1.x - current.x,2));
		double p2_distance = Math.sqrt(Math.pow(p2.y - current.y, 2) + Math.pow(p2.x - current.x,2));
		double total_distance = Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x,2));
		
		newNormal.x = (float)(n2.x*(p1_distance/total_distance) + n1.x*(p2_distance/total_distance));
		newNormal.y = (float)(n2.y*(p1_distance/total_distance) + n1.y*(p2_distance/total_distance));
		newNormal.z  = (float)(n2.z*(p1_distance/total_distance) + n1.z*(p2_distance/total_distance));
		
		newNormal.normalize();
		
		return newNormal;
	}
	
	
	
	// draw a line segment
	public static int[][] drawLinePhong(BufferedImage buff, Point3D p1, Point3D n1, Point3D p2,Point3D n2, int[][] depthBuffer, PointLight pointLight, InfiniteLight infiniteLight, AmbientLight ambientLight, Material mat, Point3D view_vector)
	{
		// replace the following line with your implementation
		

		
		//determine which is the leftmost pixel the following code will run from line being drawn left to right
		Point3D leftPoint = new Point3D(); 	Point3D leftPointNorm = new Point3D();
		Point3D rightPoint = new Point3D(); Point3D rightPointNorm = new Point3D();
		if((p1.x - p2.x) < 0) {
			leftPoint.x = p1.x;
			leftPoint.y = p1.y;
			leftPoint.z= p1.z;
			leftPointNorm = n1;
			
			rightPoint.x = p2.x;
			rightPoint.y = p2.y;
			rightPoint.z = p2.z;
			rightPointNorm = n2;
			  
		}else {
			leftPoint.x = p2.x;
			leftPoint.y = p2.y;
			leftPoint.z = p2.z;
			leftPointNorm = n2;
			
			rightPoint.x = p1.x;
			rightPoint.y = p1.y;
			rightPoint.z = p1.z;
			rightPointNorm = n1;
		}
		//System.out.println("(" + rightPoint.x + ", " + rightPoint.y + ")");
		//System.out.println("(" + leftPoint.x + ", " + leftPoint.y + ")");
		
		//point that will trace the line
		Point3D point = new Point3D(); Point3D pointNorm = new Point3D();
		point.x = leftPoint.x;
		point.y = leftPoint.y;
		point.z = leftPoint.z; 
		pointNorm = leftPointNorm;
		
		//undefined slope
		if(rightPoint.x - leftPoint.x == 0) {		
			if(rightPoint.y - leftPoint.y >0) {			
				point.y = leftPoint.y;
				while(point.y <= rightPoint.y) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						
						pointNorm = newNorm(p1, n1, p2, n2,point);
						
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).r + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).r + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).r;
						point.c.g = ambientLight.applyLight(mat, view_vector, pointNorm).g + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).g + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).g;
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).b + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).b + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).b;

						drawPointPhong(buff, point);
					
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
					
					
					
					
					}
				
					point.y++;
					point.z = calcZ(rightPoint, leftPoint,point);
			}
			}else if(rightPoint.y - leftPoint.y <0){
				point.y= rightPoint.y;
				while(point.y <= leftPoint.y) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						
						pointNorm = newNorm(p1, n1, p2, n2,point);
						
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).r + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).r + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).r;
						point.c.g = ambientLight.applyLight(mat, view_vector, pointNorm).g + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).g + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).g;
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).b + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).b + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).b;

						drawPointPhong(buff, point);
						
						
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.y++;
					point.z = calcZ(rightPoint, leftPoint,point);
			}
			}
			
		}else{
			float slope = (float)(rightPoint.y - leftPoint.y)/(rightPoint.x - leftPoint.x);
			//System.out.println(slope);
			if (slope< 1 && slope >=0) {
				int p_k = 2*((int)rightPoint.y - (int)leftPoint.y) - ((int)rightPoint.x - (int)leftPoint.x);
				while (point.x <= rightPoint.x) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						
						pointNorm = newNorm(p1, n1, p2, n2,point);
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).r + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).r + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).r;
						point.c.g = ambientLight.applyLight(mat, view_vector, pointNorm).g + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).g + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).g;
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).b + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).b + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).b;

						drawPointPhong(buff, point);
						
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					
					point.x++;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)rightPoint.y - (int)leftPoint.y);
					}else {
						p_k = p_k + 2*((int)rightPoint.y - (int)leftPoint.y) - 2*((int)rightPoint.x - (int)leftPoint.x);
						point.y++;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
				}
			
			}else if(slope >= 1) {
				int p_k = 2*((int)rightPoint.x - (int)leftPoint.x)- ((int)rightPoint.y - (int)leftPoint.y);
				while (point.y <= rightPoint.y) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						
						pointNorm = newNorm(p1, n1, p2, n2,point);
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).r + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).r + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).r;
						point.c.g = ambientLight.applyLight(mat, view_vector, pointNorm).g + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).g + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).g;
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).b + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).b + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).b;

						drawPointPhong(buff, point);
						
						
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.y++;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x);
					}else {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x) - 2*((int)rightPoint.y - (int)leftPoint.y);
						point.x++;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
				}
				
			}else if(slope <0 && slope >-1) {
				int p_k = 2*((int)leftPoint.y - (int)rightPoint.y) - ((int)rightPoint.x - (int)leftPoint.x);
				while (point.x <= rightPoint.x) {
					
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						
						pointNorm = newNorm(p1, n1, p2, n2,point);
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).r + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).r + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).r;
						point.c.g = ambientLight.applyLight(mat, view_vector, pointNorm).g + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).g + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).g;
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).b + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).b + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).b;

						drawPointPhong(buff, point);
						
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.x++;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)leftPoint.y - (int)rightPoint.y);
					}else {
						p_k = p_k + 2*((int)leftPoint.y - (int)rightPoint.y) - 2*((int)rightPoint.x - (int)leftPoint.x);
						point.y--;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
			}
			}
			else{
				
				int p_k = 2*((int)rightPoint.x - (int)leftPoint.x) - ((int)leftPoint.y - (int)rightPoint.y);
				while (point.y >= rightPoint.y) {
					
					if(point.z > depthBuffer[(int)point.x][(int)point.y]) {
						pointNorm = newNorm(p1, n1, p2, n2,point);
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).r + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).r + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).r;
						point.c.g = ambientLight.applyLight(mat, view_vector, pointNorm).g + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).g + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).g;
						point.c.r = ambientLight.applyLight(mat, view_vector, pointNorm).b + pointLight.applyLight(mat, view_vector, pointNorm, point, true,true, false).b + infiniteLight.applyLight(mat, view_vector, pointNorm, true, true).b;

						drawPointPhong(buff, point);
						
						
						depthBuffer[(int)point.x][(int)point.y] = (int)point.z;
						}
					
					point.y--;
					//p_k value that determines if we should round up or down based on where the lines passes through
					if(p_k < 0) {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x);
					}else {
						p_k = p_k + 2*((int)rightPoint.x - (int)leftPoint.x) - 2*((int)leftPoint.y - (int)rightPoint.y);
						point.x++;
					}
					point.z = calcZ(rightPoint, leftPoint,point);
			}
				
			
			}
		}
		
		return depthBuffer;
	}
	
	// draw a triangle
	public static int[][] drawTrianglePhong(BufferedImage buff, Point3D p1, Point3D p2, Point3D p3, Point3D n1, Point3D n2, Point3D n3, boolean do_smooth, int[][] _depthBuffer, PointLight pointlight, InfiniteLight infiniteLight, AmbientLight ambientLight, Material mat, Point3D view_vector )
	{
		
		int[][] dBuff =  _depthBuffer;

		
		
		//System.out.println("(" + p1.x + "," + p1.y + ")");
		//System.out.println("(" + p2.x + "," + p2.y + ")");
		//System.out.println("(" + p3.x + "," + p3.y + ")");
		//System.out.println();

		

	
		//calculate the incenter of the triangle to then determine where the vertices in relation to that point
		double y_coord =((Math.sqrt(Math.pow(p2.x-p3.x, 2) + Math.pow(p2.y - p3.y, 2))*p1.y) + (Math.sqrt(Math.pow(p1.x-p3.x,2) + Math.pow(p1.y - p3.y,2))*p2.y) + (Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y - p2.y,2))*p3.y))/(Math.sqrt(Math.pow(p2.x-p3.x,2) + Math.pow(p2.y - p3.y,2))+ Math.sqrt(Math.pow(p1.x-p3.x,2) + Math.pow(p1.y - p3.y,2))+ Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y - p2.y,2)));
	
		
		//determine the distances between the vertices to the incenter y coordinate to determine where the divide the triangle
		double p1_distance = Math.abs(p1.y - y_coord);
		double p2_distance = Math.abs(p2.y - y_coord);
		double p3_distance = Math.abs(p3.y - y_coord);
		
		Point3D top = new Point3D();  	Point3D topNorm = new Point3D();
		Point3D mid_left = new Point3D(); Point3D mid_leftNorm = new Point3D();
		Point3D mid_right = new Point3D(); Point3D mid_rightNorm = new Point3D();
		Point3D bottom = new Point3D();  	Point3D bottomNorm = new Point3D();
		
		
		if(p2_distance <= p1_distance && p2_distance <= p3_distance) {
			mid_right.x = p2.x; mid_right.y = p2.y;  mid_right.z = p2.z; mid_rightNorm = n2;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p1.y - p3.y == 0) {intersect_x = p1.x;}else {intersect_x = ((p2.y - p1.y)*(p1.x-p3.x)/(p1.y - p3.y)) + p1.x;}
			int intersect = (int)(intersect_x);
					
		
			mid_left.y = p2.y; mid_left.x = intersect; mid_left.z = calcZ(p1, p3, mid_left); ;mid_leftNorm = newNorm(p1, n1, p3, n3, mid_left);
			if(p1.y - p3.y > 0) {
				top.y = p1.y; top.x = p1.x;  top.z = p1.z; topNorm = n1;
				bottom.y = p3.y; bottom.x = p3.x; bottom.z = p3.z; bottomNorm = n3; 
			}else {
				top.y = p3.y; top.x = p3.x; top.z = p3.z; topNorm = n3; 
				bottom.y = p1.y; bottom.x = p1.x;bottom.z = p1.z; bottomNorm = n1; 
			}
		}else if(p1_distance <= p2_distance && p1_distance <= p3_distance){
			mid_right.x = p1.x; mid_right.y = p1.y;  mid_right.z = p1.z; mid_rightNorm= n1;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p2.y - p3.y == 0) { intersect_x = p2.x; }else {intersect_x = ((p1.y - p2.y)*(p2.x-p3.x)/(p2.y - p3.y)) + p2.x;}
			int intersect = (int)(intersect_x);
			
			mid_left.y = p1.y; mid_left.x = intersect; mid_left.z = calcZ(p2, p3, mid_left); mid_leftNorm = newNorm(p2,n2, p3, n3, mid_left);
			if(p2.y - p3.y > 0) {
				top.y = p2.y; top.x = p2.x; top.z = p2.z; topNorm = n2; 
				bottom.y = p3.y; bottom.x = p3.x;bottom.z = p3.z; bottomNorm = n3;
			}else {
				top.y = p3.y; top.x = p3.x; top.z = p3.z; topNorm = n3; 
				bottom.y = p2.y; bottom.x = p2.x; bottom.z = p2.z; bottomNorm = n2;
			}
			
		}else {
			mid_right.x = p3.x; mid_right.y = p3.y; mid_right.z = p3.z; mid_rightNorm = n3;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p2.y - p1.y == 0) { intersect_x = p3.x; }else {intersect_x = ((p3.y - p2.y)*(p2.x-p1.x)/(p2.y - p1.y)) + p2.x;}
			int intersect = (int)(intersect_x);
			
			mid_left.y = p3.y; mid_left.x = intersect; mid_left.z = calcZ(p1, p2, mid_left); mid_leftNorm = newNorm(p1, n2, p2, n2,mid_left);
			if(p2.y - p1.y > 0) {
				top.y = p2.y; top.x = p2.x; top.z = p2.z; topNorm = n2;
				bottom.y = p1.y; bottom.x = p1.x; bottom.z = p1.z; bottomNorm = n1; 
			}else {
				top.y = p1.y; top.x = p1.x; top.z = p1.z;  topNorm = n1; 
				bottom.y = p2.y; bottom.x = p2.x;bottom.z = p2.z; bottomNorm = n2;
			}
		}
		
		
		//System.out.println("top "+ "(" + top.x + "," + top.y + ")");
		//System.out.println("mid " + "(" + mid_right.x + "," + mid_right.y + ")");
		//System.out.println("mid " + "(" + mid_left.x + "," + mid_left.y + ")");
		//System.out.println("bottom " + "(" + bottom.x + "," + bottom.y + ")");
		//System.out.println();



		
		//top point flat bottom triangle	
		
		//initialize values that will trace the sides of the triangle to fill it in
		Point3D trace1 = new Point3D(top); Point3D trace1Norm = new Point3D(topNorm);
		Point3D trace2 = new Point3D(top); Point3D trace2Norm = new Point3D(topNorm);
		
	
		
		
		float slope1;//calculate inverses of slopes to determine the next side point to trace to 
		if((top.y - mid_right.y) == 0) {slope1 = 0;}else {slope1 = (float)(top.x - mid_right.x)/(top.y - mid_right.y);}
		float slope2;
		if(top.y - mid_left.y == 0) {slope2 = 0;}else {slope2 = (float)(top.x - mid_left.x)/(top.y - mid_left.y);}
		for(int i = 0; i <=(top.y - mid_right.y); i++) {
			
			//check the do_smooth boolean
			trace1Norm = newNorm(top,topNorm, mid_right, mid_rightNorm, trace1); trace2Norm = newNorm(top,topNorm,  mid_left,mid_leftNorm, trace2);
		
			
			trace1.z = calcZ(top, mid_right, trace1);
			trace2.z = calcZ(top, mid_left, trace2);
			
			 dBuff = drawLinePhong(buff, trace1,trace1Norm, trace2, trace2Norm, dBuff,  pointlight, infiniteLight, ambientLight, mat , view_vector);
			trace1.y--;
			trace1.x = Math.round((trace1.y - top.y)*slope1 + top.x);
			trace2.y--;
			trace2.x = Math.round((trace2.y - top.y)*slope2 + top.x);
			
			
		}
		
		//flat top, point bottom triangle
		
		//initialize values that will trace the sides of the triangle to fill it in
		Point3D trace3 = new Point3D(bottom); 	Point3D trace3Norm = new Point3D(bottomNorm);
		Point3D trace4 = new Point3D(bottom);   Point3D trace4Norm = new Point3D(bottomNorm);
		
		
		float slope_left;//calculate inverses of slopes to determine the next side point to trace to 
		if(mid_right.y - bottom.y == 0){slope_left = 0;}else {slope_left = (float)(mid_right.x - bottom.x)/(mid_right.y - bottom.y);}
		float slope_right;
		if(mid_left.y - bottom.y == 0){slope_right = 0;}else {slope_right = (float)(mid_left.x - bottom.x)/(mid_left.y - bottom.y);}
		for(int i = 0; i <=(mid_right.y - bottom.y); i++) {
			
			
			//check do_smooth boolean
			trace3Norm = newNorm(bottom,bottomNorm, mid_right, mid_rightNorm, trace3); trace4Norm = newNorm(bottom,bottomNorm, mid_left,mid_leftNorm, trace4);
			
			
			trace3.z = calcZ(bottom, mid_right, trace3);
			trace4.z = calcZ(bottom, mid_left, trace4);
		
			
			dBuff = drawLinePhong(buff, trace3, trace3Norm, trace4,trace4Norm, dBuff, pointlight,  infiniteLight, ambientLight , mat , view_vector);
			trace3.y++;
			trace3.x = Math.round((trace3.y - bottom.y)*slope_left + bottom.x);
			trace4.y++;
			trace4.x = Math.round((trace4.y - bottom.y)*slope_right + bottom.x);
			
			
		}
		
	return dBuff;
	}
	
/**public static Point3D colorValues(Point3D p1, Point3D p2, Point3D current, Point3D n1, Point3D n2) {
		//Point2D colorPoint = new Point2D();
		Point3D norm = new Point3D();
		double p1_distance = Math.sqrt(Math.pow(p1.y - current.y, 2) + Math.pow(p1.x - current.x,2));
		double p2_distance = Math.sqrt(Math.pow(p2.y - current.y, 2) + Math.pow(p2.x - current.x,2));
		double total_distance = Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x,2));
		
		norm.x = (float)(n2.x*(p1_distance/total_distance) + n1.x*(p2_distance/total_distance));
		norm.y = (float)(n2.y*(p1_distance/total_distance) + n1.y*(p2_distance/total_distance));
		norm.z  = (float)(n2.z*(p1_distance/total_distance) + n1.z*(p2_distance/total_distance));
		
		norm.normalize();
		
		return norm;
	}*/

/**
public static void phongRendering(BufferedImage buff, Point2D t1_, Point2D t2_, Point2D t3_, Point3D n1_, Point3D n2_, Point3D n3_, Material mat, Point3D view_vector, AmbientLight ambL, InfiniteLight infL, PointLight pntL, boolean[] lightlist) {
		
		
		Point3D p1= new Point3D(t1_.x, t1_.y, 0);
		Point3D p2= new Point3D(t2_.x, t2_.y, 0);
		Point3D p3= new Point3D(t3_.x, t3_.y, 0);
		
		//calculate the incenter of the triangle to then determine where the vertices in relation to that point
		double y_coord =((Math.sqrt(Math.pow(p2.x-p3.x, 2) + Math.pow(p2.y - p3.y, 2))*p1.y) + (Math.sqrt(Math.pow(p1.x-p3.x,2) + Math.pow(p1.y - p3.y,2))*p2.y) + (Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y - p2.y,2))*p3.y))/(Math.sqrt(Math.pow(p2.x-p3.x,2) + Math.pow(p2.y - p3.y,2))+ Math.sqrt(Math.pow(p1.x-p3.x,2) + Math.pow(p1.y - p3.y,2))+ Math.sqrt(Math.pow(p1.x-p2.x,2) + Math.pow(p1.y - p2.y,2)));
	
		
		//determine the distances between the vertices to the incenter y coordinate to determine where the divide the triangle
		double p1_distance = Math.abs(p1.y - y_coord);
		double p2_distance = Math.abs(p2.y - y_coord);
		double p3_distance = Math.abs(p3.y - y_coord);
		
		Point3D top = new Point3D();
		Point3D mid_left = new Point3D();
		Point3D mid_right = new Point3D();
		Point3D bottom = new Point3D();
		
		Point3D top_norm = new Point3D();
		Point3D bottom_norm = new Point3D();
		Point3D mid_Lnorm = new Point3D();
		Point3D mid_Rnorm = new Point3D();
		
		
		
		if(p2_distance <= p1_distance && p2_distance <= p3_distance) {
			mid_right.x = p2.x; mid_right.y = p2.y;  mid_Rnorm = n2_;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p1.y - p3.y == 0) {intersect_x = p1.x;}else {intersect_x = ((p2.y - p1.y)*(p1.x-p3.x)/(p1.y - p3.y)) + p1.x;}
			int intersect = (int)(intersect_x);
					
		
			mid_left.y = p2.y; mid_left.x = intersect; mid_Lnorm = colorValues(p1, p3, mid_left, n1_, n3_);
			if(p1.y - p3.y > 0) {
				top.y = p1.y; top.x = p1.x;  top_norm= n1_;
				bottom.y = p3.y; bottom.x = p3.x;bottom_norm = n3_; 
			}else {
				top.y = p3.y; top.x = p3.x; top_norm = n3_; 
				bottom.y = p1.y; bottom.x = p1.x;  bottom_norm = n1_; 
			}
		}else if(p1_distance <= p2_distance && p1_distance <= p3_distance){
			mid_right.x = p1.x; mid_right.y = p1.y;  mid_Rnorm = n1_;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p2.y - p3.y == 0) { intersect_x = p2.x; }else {intersect_x = ((p1.y - p2.y)*(p2.x-p3.x)/(p2.y - p3.y)) + p2.x;}
			int intersect = (int)(intersect_x);
			
			mid_left.y = p1.y; mid_left.x = intersect; mid_Lnorm = colorValues(p2, p3, mid_left, n2_, n3_);
			if(p2.y - p3.y > 0) {
				top.y = p2.y; top.x = p2.x;  top_norm = n2_; 
				bottom.y = p3.y; bottom.x = p3.x;  bottom_norm = n3_;
			}else {
				top.y = p3.y; top.x = p3.x; top_norm = n3_; 
				bottom.y = p2.y; bottom.x = p2.x; bottom_norm = n2_;
			}
			
		}else {
			mid_right.x = p3.x; mid_right.y = p3.y; mid_Rnorm = n3_;
			//calculate x coordinate of intersecting line
			float intersect_x;
			if(p2.y - p1.y == 0) { intersect_x = p3.x; }else {intersect_x = ((p3.y - p2.y)*(p2.x-p1.x)/(p2.y - p1.y)) + p2.x;}
			int intersect = (int)(intersect_x);
			
			mid_left.y = p3.y; mid_left.x = intersect; mid_Lnorm = colorValues(p1, p2, mid_left, n1_, n2_);
			if(p2.y - p1.y > 0) {
				top.y = p2.y; top.x = p2.x; top_norm = n2_;
				bottom.y = p1.y; bottom.x = p1.x; bottom_norm = n1_; 
			}else {
				top.y = p1.y; top.x = p1.x; top_norm = n1_; 
				bottom.y = p2.y; bottom.x = p2.x; bottom_norm = n2_;
			}
		}
		
		
		//System.out.println("top "+ "(" + top.x + "," + top.y + ")");
		//System.out.println("mid " + "(" + mid_right.x + "," + mid_right.y + ")");
		//System.out.println("mid " + "(" + mid_left.x + "," + mid_left.y + ")");
		//System.out.println("bottom " + "(" + bottom.x + "," + bottom.y + ")");
		//System.out.println();



		
		//top point flat bottom triangle	
		
		//initialize values that will trace the sides of the triangle to fill it in
		Point3D trace1 = new Point3D(top.x, top.y, 0);
		Point3D trace2 = new Point3D(top.x, top.y, 0);
	
		Point3D trace1_norm = new Point3D();
		Point3D trace2_norm = new Point3D();
		
		
		float slope1;//calculate inverses of slopes to determine the next side point to trace to 
		if((top.y - mid_right.y) == 0) {slope1 = 0;}else {slope1 = (float)(top.x - mid_right.x)/(top.y - mid_right.y);}
		float slope2;
		if(top.y - mid_left.y == 0) {slope2 = 0;}else {slope2 = (float)(top.x - mid_left.x)/(top.y - mid_left.y);}
		for(int i = 0; i <=(top.y - mid_right.y); i++) {
			
			//check the do_smooth boolean
			trace1_norm = colorValues(top, mid_right, trace1, top_norm, mid_Rnorm); trace2_norm = colorValues(top, mid_left, trace2, top_norm, mid_Lnorm);
			
			
			drawLinePhong(buff, trace1, trace2, trace1_norm, trace2_norm, mat, view_vector, ambL, infL, pntL, lightlist);
			trace1.y--;
			trace1.x = Math.round((trace1.y - top.y)*slope1 + top.x);
			trace2.y--;
			trace2.x = Math.round((trace2.y - top.y)*slope2 + top.x);
			
			
		}
		
		//flat top, point bottom triangle
		
		//initialize values that will trace the sides of the triangle to fill it in
		Point3D trace3 = new Point3D(bottom.x, bottom.y,0);
		Point3D trace4 = new Point3D(bottom.x, bottom.y,0);
		
		Point3D trace3_norm = new Point3D();
		Point3D trace4_norm = new Point3D();
		
		float slope_left;//calculate inverses of slopes to determine the next side point to trace to 
		if(mid_right.y - bottom.y == 0){slope_left = 0;}else {slope_left = (float)(mid_right.x - bottom.x)/(mid_right.y - bottom.y);}
		float slope_right;
		if(mid_left.y - bottom.y == 0){slope_right = 0;}else {slope_right = (float)(mid_left.x - bottom.x)/(mid_left.y - bottom.y);}
		for(int i = 0; i <=(mid_right.y - bottom.y); i++) {
			
			
			//check do_smooth boolean
			trace3_norm = colorValues(bottom, mid_right, trace3, bottom_norm, mid_Rnorm); trace4_norm = colorValues(bottom, mid_left, trace4, bottom_norm, mid_Lnorm);
		
			
			drawLinePhong(buff, trace3, trace4, trace3_norm, trace4_norm, mat, view_vector, ambL, infL, pntL, lightlist);
			trace3.y++;
			trace3.x = Math.round((trace3.y - bottom.y)*slope_left + bottom.x);
			trace4.y++;
			trace4.x = Math.round((trace4.y - bottom.y)*slope_right + bottom.x);
		 
		}
	} 
	
	public static void drawLinePhong(BufferedImage buff, Point3D p1, Point3D p2, Point3D n1, Point3D n2, Material mat, Point3D view_vector, AmbientLight ambL, InfiniteLight infL, PointLight pntL, boolean[] lightlist )
		{
			// replace the following line with your implementation
			

			
			//determine which is the leftmost pixel the following code will run from line being drawn left to right
			Point3D leftPoint = new Point3D();
			Point3D rightPoint = new Point3D();
			
			Point3D leftPoint_norm = new Point3D();
			Point3D rightPoint_norm = new Point3D();
			
			
			if((p1.x - p2.x) < 0) {
				leftPoint.x = p1.x;
				leftPoint.y = p1.y;
				leftPoint_norm = n1;
				
				rightPoint.x = p2.x;
				rightPoint.y = p2.y;
				rightPoint_norm = n2;
				  
			}else {
				leftPoint.x = p2.x;
				leftPoint.y = p2.y;
				leftPoint_norm = n2;
				
				rightPoint.x = p1.x;
				rightPoint.y = p1.y;
				rightPoint_norm = n1;
				
			}
			//System.out.println("(" + rightPoint.x + ", " + rightPoint.y + ")");
			//System.out.println("(" + leftPoint.x + ", " + leftPoint.y + ")");
			
			//point that will trace the line
			Point3D point = new Point3D();
			
			point.x = leftPoint.x;
			point.y = leftPoint.y;
			
			Point3D color_norm = new Point3D();
			Point2D coloring = new Point2D();
			
			
			//undefined slope
			if(rightPoint.x - leftPoint.x == 0) {
				if(rightPoint.y - leftPoint.y >0) {
					point.y = leftPoint.y;
					while(point.y <= rightPoint.y) {
						color_norm = colorValues(p1, p2, point, n1, n2);

						
						if(lightlist[0]) {
							coloring.c.r = ambL.applyLight(mat, view_vector, color_norm).r;
							coloring.c.g = ambL.applyLight(mat, view_vector, color_norm).g;
							coloring.c.b = ambL.applyLight(mat, view_vector, color_norm).b;

						}
						if(lightlist[1]) {
							coloring.c.r = infL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;

						}
						if(lightlist[2]) {
							coloring.c.r = pntL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;


						}
						
						coloring.x = (int)point.x; coloring.y = (int)point.y;
						drawPoint(buff, coloring);
						point.y++;
				}
				}else if(rightPoint.y - leftPoint.y <0){
					point.y= rightPoint.y;
					while(point.y <= leftPoint.y) {
						color_norm = colorValues(p1, p2, point, n1, n2);
						if(lightlist[0]) {
							coloring.c.r = ambL.applyLight(mat, view_vector, color_norm).r;
							coloring.c.g = ambL.applyLight(mat, view_vector, color_norm).g;
							coloring.c.b = ambL.applyLight(mat, view_vector, color_norm).b;

						}
						if(lightlist[1]) {
							coloring.c.r = infL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;

						}
						if(lightlist[2]) {
							coloring.c.r = pntL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;


						}
						
						coloring.x = (int)point.x; coloring.y = (int)point.y;
						drawPoint(buff, coloring);
						point.y++;
				}
				}
				
			}else{
				float slope = (float)(rightPoint.y - leftPoint.y)/(rightPoint.x - leftPoint.x);
				//System.out.println(slope);
				if (slope< 1 && slope >=0) {
					int p_k = (int)(2*(rightPoint.y - leftPoint.y) - (rightPoint.x - leftPoint.x));
					while (point.x <= rightPoint.x) {
						color_norm = colorValues(p1, p2, point, n1, n2);
						if(lightlist[0]) {
							coloring.c.r = ambL.applyLight(mat, view_vector, color_norm).r;
							coloring.c.g = ambL.applyLight(mat, view_vector, color_norm).g;
							coloring.c.b = ambL.applyLight(mat, view_vector, color_norm).b;

						}
						if(lightlist[1]) {
							coloring.c.r = infL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;

						}
						if(lightlist[2]) {
							coloring.c.r = pntL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;


						}
						coloring.x = (int)point.x;coloring.y = (int)point.y;
						drawPoint(buff, coloring);
						point.x++;
						//p_k value that determines if we should round up or down based on where the lines passes through
						if(p_k < 0) {
							p_k = p_k + 2*(int)(rightPoint.y - leftPoint.y);
						}else {
							p_k = p_k + 2*(int)(rightPoint.y - leftPoint.y) - 2*(int)(rightPoint.x - leftPoint.x);
							point.y++;
						}
					}
				
				}else if(slope >= 1) {
					int p_k = 2*(int)(rightPoint.x - leftPoint.x)- (int)(rightPoint.y - leftPoint.y);
					while (point.y <= rightPoint.y) {
						color_norm = colorValues(p1, p2, point, n1, n2);
						if(lightlist[0]) {
							coloring.c.r = ambL.applyLight(mat, view_vector, color_norm).r;
							coloring.c.g = ambL.applyLight(mat, view_vector, color_norm).g;
							coloring.c.b = ambL.applyLight(mat, view_vector, color_norm).b;

						}
						if(lightlist[1]) {
							coloring.c.r = infL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;

						}
						if(lightlist[2]) {
							coloring.c.r = pntL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;


						}
						coloring.x = (int)point.x;coloring.y = (int)point.y;
						drawPoint(buff, coloring);
						//drawPoint(buff, new Point2D(point.x, point.y, colorValues(p1, p2, point)));
						point.y++;
						//p_k value that determines if we should round up or down based on where the lines passes through
						if(p_k < 0) {
							p_k = p_k + 2*(int)(rightPoint.x - leftPoint.x);
						}else {
							p_k = p_k + 2*(int)(rightPoint.x -leftPoint.x) - 2*(int)(rightPoint.y - leftPoint.y);
							point.x++;
						}
					}
					
				}else if(slope <0 && slope >-1) {
					int p_k = 2*(int)(leftPoint.y - rightPoint.y) - (int)(rightPoint.x - leftPoint.x);
					while (point.x <= rightPoint.x) {
						color_norm = colorValues(p1, p2, point, n1, n2);
						if(lightlist[0]) {
							coloring.c.r = ambL.applyLight(mat, view_vector, color_norm).r;
							coloring.c.g = ambL.applyLight(mat, view_vector, color_norm).g;
							coloring.c.b = ambL.applyLight(mat, view_vector, color_norm).b;

						}
						if(lightlist[1]) {
							coloring.c.r = infL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;

						}
						if(lightlist[2]) {
							coloring.c.r = pntL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;


						}
						coloring.x = (int)point.x;coloring.y = (int)point.y;
						drawPoint(buff, coloring);
						//drawPoint(buff, new Point2D(point.x, point.y, colorValues(p1, p2, point)));
						point.x++;
						//p_k value that determines if we should round up or down based on where the lines passes through
						if(p_k < 0) {
							p_k = p_k + 2*(int)(leftPoint.y - rightPoint.y);
						}else {
							p_k = p_k + 2*(int)(leftPoint.y - rightPoint.y) - 2*(int)(rightPoint.x - leftPoint.x);
							point.y--;
						}
					
				}
				}
				else{
					
					int p_k = 2*(int)(rightPoint.x - leftPoint.x) - (int)(leftPoint.y - rightPoint.y);
					while (point.y >= rightPoint.y) {
						color_norm = colorValues(p1, p2, point, n1, n2);
						if(lightlist[0]) {
							coloring.c.r = ambL.applyLight(mat, view_vector, color_norm).r;
							coloring.c.g = ambL.applyLight(mat, view_vector, color_norm).g;
							coloring.c.b = ambL.applyLight(mat, view_vector, color_norm).b;

						}
						if(lightlist[1]) {
							coloring.c.r = infL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = infL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;

						}
						if(lightlist[2]) {
							coloring.c.r = pntL.applyLight(mat, view_vector, color_norm,lightlist[3], lightlist[4]).r;
							coloring.c.g = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).g;
							coloring.c.b = pntL.applyLight(mat, view_vector, color_norm, lightlist[3], lightlist[4]).b;


						}
						coloring.x = (int)point.x;coloring.y = (int)point.y;
						drawPoint(buff, coloring);
						//drawPoint(buff, new Point2D(point.x, point.y, colorValues(p1, p2, point)));
						point.y--;
						//p_k value that determines if we should round up or down based on where the lines passes through
						if(p_k < 0) {
							p_k = p_k + 2*(int)(rightPoint.x - leftPoint.x);
						}else {
							p_k = p_k + 2*(int)(rightPoint.x - leftPoint.x) - 2*(int)(leftPoint.y - rightPoint.y);
							point.x++;
						}
					
				}
					
				
				}
			}
		}
	
	

	
	

*/
	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static Point3D[] sortTriangleVerts(Point3D p1, Point3D p2, Point3D p3)
	{
	    Point3D pts[] = {p1, p2, p3};
	    Point3D tmp;
	    int j=0;
	    boolean swapped = true;
	         
	    while (swapped) 
	    {
	    	swapped = false;
	    	j++;
	    	for (int i = 0; i < 3 - j; i++) 
	    	{                                       
	    		if (pts[i].x > pts[i + 1].x) 
	    		{                          
	    			tmp = pts[i];
	    			pts[i] = pts[i + 1];
	    			pts[i + 1] = tmp;
	    			swapped = true;
	    		}
	    	}                
	    }
	    return(pts);
	}

}