package org.n52.v3d.triturus.vgis;

/**
 * /** A simple Extension of {@link VgGeomObject0d} for collections of
 * 0-dimensional geometries.
 * 
 * @author Christian Danowski
 * 
 */
public abstract class VgCollection0d extends VgGeomObject0d implements
		VgCollection {

	/**
	 * <b>0D-implementation that only supports 0-dimensional objects</b> <br/>
	 * <br/>
	 * {@inheritDoc}
	 */
	public abstract VgGeomObject0d getGeometry(int i);

	public abstract int getNumberOfGeometries();

}
