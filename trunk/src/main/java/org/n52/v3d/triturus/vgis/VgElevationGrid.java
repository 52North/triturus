/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.vgis;

/**
 * Class to manage elevation grids.<br /><br />
 * <i>German:</i> Klasse zur Verwaltung eines Gitters von H&ouml;henpunkten. Ein Gitter ist wahlweise Vertex-basiert
 * (&quot;Lattices&quot;) oder Zellen-basiert (&quot;Grids&quot;).<br />
 * Die Gitterelemente m&uuml;ssen nicht notwendigerweise mit H&ouml;henwerten belegt sein . F&uuml;r jedes Element
 * l&auml;sst sich ein &quot;no data&quot;-Flag setzen.
 * @author Benno Schmidt
 */
abstract public class VgElevationGrid extends VgFeature 
{
    /**  
     * returns the number of data-points in x-direction (columns).
     */
    abstract public int numberOfColumns();

    /** 
     * returns the number of data-points in y-direction (rows).
     */
    abstract public int numberOfRows();
    
    /** 
     * sets the elevation value <tt>pZ</tt> for the row index <tt>pRow</tt> and the column index <tt>pCol</tt>.
     */
    abstract public void setValue(int pRow, int pCol, double pZ);

    /** 
     * returns the elevation value for the row index <tt>pRow</tt> and the column index <tt>pCol</tt>.
     */
    abstract public double getValue(int pRow, int pCol);

    /** 
     * returns the elevation grid's minimum value.
     */
    abstract public double minimalElevation();

    /** 
     * returns the elevation grid's maximum value.
     */
    abstract public double maximalElevation();

    /**
     * returns the difference of the maximum and minimum elevation value.
     */
    public double elevationDifference() {
        return this.maximalElevation() - this.minimalElevation();
    }
}
