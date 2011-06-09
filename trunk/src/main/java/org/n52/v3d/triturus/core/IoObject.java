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
 * Abstract base class for I/O objects. I/O objects allow read and write access to external data-sources. Method calls
 * to I/O objects result <tt>VgFeature</tt> objects; cf. visualization pipeline concept.<br /><br />
 * <i>German: </i> Abstrakte Basisklasse f&uuml;r I/O-Objekte. I/O-Objekte erm&ouml;glichen den lesenden und
 * schreibenden Zugriff auf externe Datenquellen. Resultat sind Objekte vom Typ <tt>VgFeature</tt>; vgl. Konzept der
 * Visualisierungs-Pipeline.
 * @see T3dProcFilter
 * @see T3dProcMapper
 * @author Benno Schmidt<br>
 */
abstract public class IoObject 
{
	/** protocols the transformation.<br /><br />
     * <i>German:</i> protokolliert die durchgef&uuml;hrte Transformation.
     * @return Text to be logged
     */
	abstract public String log();
}