/***************************************************************************************
 * Copyright (C) 2012 by 52 North Initiative for Geospatial Open Source Software GmbH  *
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
package org.n52.v3d.triturus.t3dutil;

/**
 * Abstract base class to derive hypsometric color-values.
 *
 * @see org.n52.v3d.triturus.t3dutil.MpSimpleHypsometricColor
 * @see org.n52.v3d.triturus.t3dutil.MpGMTHypsometricColor
 * @author Benno Schmidt
 */
abstract public class MpHypsometricColor extends MpQuantitativeValue2Color
{
    /**
     * provides the color that is assigned to an elevation-value.
     *
     * @param pElevation elevation-value
     * @return Color assigned to the given elevation
     */
    abstract public T3dColor transform(double pElevation);
}