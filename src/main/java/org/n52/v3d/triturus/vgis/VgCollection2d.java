package org.n52.v3d.triturus.vgis;

import org.n52.v3d.triturus.vgis.VgGeomObject2d;

/**
 * A simple Extension of {@link VgGeomObject2d} for collections of 2-dimensional
 * geometries.
 * 
 * @author Christian Danowski
 * 
 */
public abstract class VgCollection2d extends VgGeomObject2d implements
		VgCollection {

	/**
	 * <b>2D-implementation that only supports 2-dimensional objects</b> <br/>
	 * <br/>
	 * {@inheritDoc}
	 */
	public abstract VgGeomObject2d getGeometry(int i);

	public abstract int getNumberOfGeometries();

}
