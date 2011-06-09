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
 * Abstract base class for mapper objects. <i>Mapper objects</i> transform features (i.e., geo-objects) into abstract,
 * Renderer-independent visualization objects (e.g., <tt>VgFeature -&gt;</tt> abstract shape); ct. visualization
 * pipeline concept. Mapping transformations will be performed by suitable <tt>transform</tt>-methods. Usually, these
 * methods have to following signature: <tt>public Object transform(Object pInput)</tt>, where <tt>pInput</tt> holds the
 * input feature and the resulting output-object is an abstract shape.<br />
 * Note: Realizations of this abstract base class usually are begin with the prefix &quot;Mp&quot; as class name.<br />
 * The package <tt>org.n52.v3d.triturus.vscene</tt> offers a special model to operate on abstract visualization objects.
 * <br /><br />
 * <i>German:</i> Abstrakte Basisklasse f&uml;r Mapper-Objekte. Mapper-Objekte dienen zur Transformation von Geoobjekten
 * in abstrakte, Renderer-unabh&auml;ngige Visualisierungsobjekte (z. B. <tt>VgFeature -&gt;</tt> abstraktes Shape);
 * vgl. Konzept der Visualisierungs-Pipeline.<br />
 * Die Transformationen erfolgt in den Implementierungen &uuml;ber geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte &uuml;bergeben werden und das
 * Ergebnisobjekt Visualisierungsobjekte umfasst.<br />
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach M&ouml;glichkeit mit dem Pr&auml;fix
 * &quot;Mp&quot; versehen werden.<br />
 * Als spezielle Modellierung f&uml;r abstrakte Visualisierungsobjekte ist z. B. das Paket
 * <tt>org.n52.v3d.triturus.vscene</tt> nutzbar.
 * @see T3dProcFilter
 * @see T3dProcRendererMapper
 * @see IoObject
 * @author Benno Schmidt
 */
abstract public class T3dProcMapper
{
    /** protocols the transformation.<br /><br />
    * <i>German:</i> protokolliert die durchgef&uuml;hrte Transformation.
    * @return Text to be logged
    */
    abstract public String log();
}