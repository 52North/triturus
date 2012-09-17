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
package org.n52.v3d.triturus.core;

/**
 * @deprecated
 * Abstract base class for renderer-mapper objects. <i>Renderer-mapper objects</i> transform abstract,
 * Renderer-independent visualization objects into Renderer-specific shapes (e.g. abstract visualization object
 * <tt>-></tt> Java 3D shape).<br />
 * Note: Realizations of this abstract base class usually are begin with the prefix &quot;Mpr&quot; as class name.
 * <br /><br />
 * <i>German:</i> Abstrakte Basisklasse f&uml;r Renderer-Mapper-Objekte. Renderer-Mapper-Objekte dienen zur
 * Transformation von abstrakten, Renderer-unabh&auml;ngigen Visualisierungsobjekten in Renderer-spezifische Shapes
 * (z. B. abstracktes Visualisierungsobjekt <tt>-&gt;</tt> Java 3D-Shape).<br /><br />
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach M&ouml;glichkeit mit dem Pr&auml;fix
 * &quot;Mpr&quot; versehen werden.<br />
 * @see T3dProcMapper
 * @see T3dProcFilter
 * @see IoObject
 * @author Benno Schmidt, Torsten Heinen
 */
abstract public class T3dProcRendererMapper
{
    /** protocols the transformation.<br /><br />
    * <i>German:</i> protokolliert die durchgef&uuml;hrte Transformation.
    * @return Text to be logged
    */
	abstract public String log();
}