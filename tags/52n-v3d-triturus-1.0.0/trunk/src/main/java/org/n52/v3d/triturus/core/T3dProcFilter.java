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
 * Abstract base class for filter objects. <i>Filter objects</i> transform features (i.e., geo-objects) into other
 * features (e.g., <tt>VgFeature -&gt; VgFeature</tt>); ct. visualization pipeline concept. Filter transformations will
 * be performed by suitable <tt>transform</tt>-methods. Usually, these methods have to following signature:
 * <tt>public Object transform(Object pInput)</tt>, where <tt>pInput</tt> holds the input feature and the resulting
 * output-object is a feature (geo-object), too.<br />
 * Note: Realizations of this abstract base class usually are begin with the prefix &quot;Flt&quot; as class name.
 * <br /><br />
 * <i>German:</i> Abstrakte Basisklasse f&uuml;r Filter-Objekte. Filter-Objekte dienen zur Transformation von
 * Geoobjekten in Geoobjekte (z. B. <tt>VgFeature -&gt; VgFeature</tt>); vgl. Konzept der Visualisierungs-Pipeline.
 * <br />
 * Die Transformationen erfolgt in den Implementierungen &uuml;ber geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte &uuml;bergeben werden und das
 * Ergebnisobjekt ebenfalls Geoobjekte umfasst.<br />
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach M&ouml;glichkeit mit dem Pr&auml;fix
 * &quot;Flt&quot; versehen werden.
 * @see T3dProcMapper
 * @see IoObject
 * @author Benno Schmidt
 */
abstract public class T3dProcFilter
{
  	/** protocols the transformation.<br /><br />
     * <i>German:</i> protokolliert die durchgef&uuml;hrte Transformation.
     * @return Text to be logged
     */
	abstract public String log();
}