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
package org.n52.v3d.triturus.gisimplm;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.vgis.T3dSRSException;
import org.n52.v3d.triturus.vgis.VgLineString;
import org.n52.v3d.triturus.vgis.VgPoint;

import java.util.ArrayList;

/**
 * Class to hold a measurement path consisting of time-stamped locations with thematic measurement-value vectors.
 *
 * @author Benno Schmidt
 */
public class GmMeasurementPath
{
    private ArrayList<VgPoint> mLocations = null;
    private ArrayList<Long> mTimeStamps = null;
    private ArrayList<double[]> mMeasurements = null;
    private int mDim = 0;

    /**
     * Constructor
     *
     * @param pDimension Measurement vector size (greater or equal than 0)
     */
    public GmMeasurementPath(int pDimension)
    {
        mLocations = new ArrayList<VgPoint>();
        mTimeStamps = new ArrayList<Long>();
        mMeasurements = new ArrayList<double[]>();
        mDim = pDimension;
    }

    /**
     * adds a measurement. Note that all way-points must refer to the same coordinate reference system; otherwise an
     * exception will be thrown.
     *
     * @param pLocation Spatial location
     * @param pTimeStamp Time-stamp
     * @param pMeasurementVector Measurement vector
     * @return Measurement index
     */
    public int addMeasurement(VgPoint pLocation, long pTimeStamp, double[] pMeasurementVector)
        throws T3dException, T3dSRSException
    {
        if (pLocation == null) {
            throw new T3dSRSException("Missing location specification for measurement path...");
        }

        // Check consistency:
        if (! mLocations.isEmpty()) {
            if (! mLocations.get(0).getSRS().equalsIgnoreCase(pLocation.getSRS())) {
                throw new T3dException("Tried to use incompatible coordinate reference systems within measurement path...");
            }
            if (pMeasurementVector == null) {
                if (mDim != 0) {
                    throw new T3dException("Tried to pass incompatible measurement vector to measurement path...");
                }
            } else {
                if (pMeasurementVector.length != mDim) {
                    throw new T3dException("Tried to pass incompatible measurement vector to measurement path...");
                }
            }
        }

        // Add data:
        mLocations.add(pLocation);
        mTimeStamps.add(pTimeStamp);
        mMeasurements.add(pMeasurementVector);

        return mLocations.size() - 1;
    }

    /**
     * gets the number of measurements held in the measurement path object.
     *
     * @return Number of added measurements
     */
    public int numberOfMeasurements() {
        return mLocations.size();
    }

    /**
     * gets the i-th location.
     *
     * @param i Measurement index
     * @return Location
     */
    public VgPoint getLocation(int i) {
        if (i < 0 || i > mLocations.size() - 1)
            throw new T3dException("Tried to access non-existing measurement location (index out of bounds)");
        return mLocations.get(i);
    }

    /**
     * gets the i-th time-stamp.
     *
     * @param i Measurement index
     * @return Time-stamp
     */
    public long getTimeStamp(int i) {
        if (i < 0 || i > mLocations.size() - 1)
            throw new T3dException("Tried to access non-existing measurement time-stamp (index out of bounds)");
        return mTimeStamps.get(i);
    }

    /**
     * gets the i-th measurement.
     *
     * @param i Measurement index
     * @return Measurement vector
     */
    public double[] getMeasurement(int i) {
        if (i < 0 || i > mLocations.size() - 1)
            throw new T3dException("Tried to access non-existing measurement vector (index out of bounds)");
        return mMeasurements.get(i);
    }

    /**
     * gets the measurement path's geometry.
     *
     * @return Measurement path as line string (polyline in 3D space)
     */
    public VgLineString getGeometry() {
        VgLineString lPath = new GmLineString();
        for (int i = 0; i < mLocations.size(); i++) {
            ((GmLineString) lPath).addVertex(mLocations.get(i));
        }
        return lPath;
    }

    public String toString() {
        return "[" + mLocations.size() + " measurements]";
    }
}
