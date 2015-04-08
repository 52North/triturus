package org.n52.v3d.triturus.vgis;

/**
 * A simple Extension of {@link VgGeomObject3d} for collections of 3-dimensional
 * geometries.
 * 
 * @author Christian Danowski
 *
 */
public abstract class VgCollection3d extends VgGeomObject3d implements
		VgCollection {

	/**
	 * <b>3D-implementation that only supports 3-dimensional objects</b> <br/>
	 * <br/>
	 * {@inheritDoc}
	 */
	public abstract VgGeomObject3d getGeometry(int i);

	public abstract int getNumberOfGeometries();

}
