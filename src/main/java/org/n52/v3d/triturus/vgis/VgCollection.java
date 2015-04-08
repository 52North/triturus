package org.n52.v3d.triturus.vgis;

/**
 * Interface for geometry collections
 * 
 * @author Christian Danowski
 *
 */
public interface VgCollection {

	/**
	 * Gets the i-th geometry of the collection.<br />
	 * Note: The following condition must always be ensured: <b>0 &lt;= i &lt;
	 * {@link VgCollection#getNumberOfGeometries()}</b>.
	 * 
	 * @param i
	 *            geometry index
	 * @return the i-th geometry of the collection
	 */
	public abstract VgGeomObject getGeometry(int i);

	/**
	 * Gets the number of geometries that are part of the collection.
	 * 
	 * @return the number of geometries
	 */
	public abstract int getNumberOfGeometries();

}
