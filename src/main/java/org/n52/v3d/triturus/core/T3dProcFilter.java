/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
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
 *
 * @see T3dProcMapper
 * @see IoObject
 * @author Benno Schmidt
 */
abstract public class T3dProcFilter
{
    /**
     * protocols the transformation.
     *
     * @return Text to be logged
     */
	abstract public String log();
}