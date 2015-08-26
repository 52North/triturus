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
 * Abstract base class for mapper objects. <i>Mapper objects</i> transform features (i.e., geo-objects) into abstract,
 * Renderer-independent visualization objects (e.g., <tt>VgFeature -&gt;</tt> abstract shape); cf. visualization
 * pipeline concept. Mapping transformations will be performed by suitable <tt>transform</tt>-methods. Usually, these
 * methods have to following signature: <tt>public Object transform(Object pInput)</tt>, where <tt>pInput</tt> holds the
 * input feature and the resulting output-object is an abstract shape.<br />
 * Note: Realizations of this abstract base class usually begin with the prefix &quot;Mp&quot; as class name.<br />
 * The package <tt>org.n52.v3d.triturus.vscene</tt> offers a specialized model to operate on abstract visualization
 * objects.
 * <br /><br />
 * <i>German:</i> Abstrakte Basisklasse f&uml;r Mapper-Objekte. Mapper-Objekte dienen zur Transformation von Geoobjekten
 * in abstrakte, Renderer-unabh&auml;ngige Visualisierungsobjekte (z. B. <tt>VgFeature -&gt;</tt> abstraktes Shape);
 * vgl. Konzept der Visualisierungs-Pipeline.<br />
 * Die Transformation erfolgt in den Implementierungen &uuml;ber geeignete <tt>transform</tt>-Methoden der Art
 * <tt>public Object transform(Object pInput)</tt>, wobei in <tt>pInput</tt> Geoobjekte &uuml;bergeben werden und das
 * Ergebnisobjekt Visualisierungsobjekte umfasst.<br />
 * Hinweis: Realisierungen dieser abstrakten Basisklasse sollten nach M&ouml;glichkeit mit dem Pr&auml;fix
 * &quot;Mp&quot; versehen werden.<br />
 * Als spezielle Modellierung f&uml;r abstrakte Visualisierungsobjekte ist z. B. das Paket
 * <tt>org.n52.v3d.triturus.vscene</tt> nutzbar.
 *
 * @see T3dProcFilter
 * @see T3dProcRendererMapper
 * @see IoObject
 * @author Benno Schmidt
 */
abstract public class T3dProcMapper
{
    /**
     * protocols the transformation.
     *
     * @return Text to be logged
     */
    abstract public String log();
}