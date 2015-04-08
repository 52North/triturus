package org.n52.v3d.triturus.vgis;

/**
 * A simple Extension of {@link VgGeomObject1d} for collections of 1-dimensional
 * geometries.
 * 
 * @author Christian Danowski
 *
 */
public abstract class VgCollection1d extends VgGeomObject1d implements
		VgCollection {

	/**
	 * <b>1D-implementation that only supports 1-dimensional objects</b> <br/>
	 * <br/>
	 * {@inheritDoc}
	 */
	public abstract VgGeomObject1d getGeometry(int i);

	public abstract int getNumberOfGeometries();

}
