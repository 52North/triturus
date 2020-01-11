package org.n52.v3d.triturus.t3dutil;

import java.util.List;

import org.n52.v3d.triturus.core.T3dNotYetImplException;
import org.n52.v3d.triturus.vgis.VgPoint;

/** 
 * Simple Delaunay triangulation implementation.<br/>
 * <b>Note:</b> This non-efficient O(N^2) implementation has been removed. 
 * @see SimpleDelaunay
 */
public class Delaunay 
{
	/**
	 * returns an index set containing a Delaunay triangulation for the given 
	 * set of points. The result set is organized as follows: For the input 
	 * points <i>p<sub>0</sub> .. p<sub>N-1</sub></i> the indices (point 
	 * numbers) of the i-th triangle of the result set are stored at the output
	 * array's positions <i>3i, 3i+1</i> and <i>3i+2</i>. Thus, the output 
	 * array will consist of 3 * M elements, where M gives the number of 
	 * triangles.
	 * 
	 * @param points Point list
	 * @return Triangle index set as described above 
	 */
	public static int[] triangulate(List<VgPoint> points) {
    	return new SimpleDelaunay(points).getIndices();
	}

	// Deprecated stuff:
	static private String deprMsg = 
		"Method is no longer implemented; see source-code to find a substitute.";
	/** @deprecated */
	public Delaunay(final double[] p) {
		throw new T3dNotYetImplException(deprMsg);		
	}
	/** @deprecated */
	public static int[] triangulate(final double[] p) {
		throw new T3dNotYetImplException(deprMsg);		
	}
	/** @deprecated */
	public double getBound() { 
		throw new T3dNotYetImplException(deprMsg); 
		}	
	/** @deprecated */
	public void getPoint(int index, double[] point) { 
		throw new T3dNotYetImplException(deprMsg);
	}
	/** @deprecated */
	public double[] getPoints() { 
		throw new T3dNotYetImplException(deprMsg);		
	}
	/** @deprecated */
	public void getPoints(double[] points, int offset) { 
		throw new T3dNotYetImplException(deprMsg);		
	}
	/** @deprecated */
	public int getNumFaces() { 		
		throw new T3dNotYetImplException(deprMsg);		
	}
	/** @deprecated */
	public int getNumPoints() { 		
		throw new T3dNotYetImplException(deprMsg);		
	}
}
