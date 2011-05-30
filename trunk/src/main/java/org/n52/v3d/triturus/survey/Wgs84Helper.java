package org.n52.v3d.triturus.survey;

/**
 * Hilfsklasse mit WGS84-Konstanten.<p>
 * @author Benno Schmidt<br>
 * (c) 2005, con terra GmbH<br>
 */
public class Wgs84Helper
{
    /**
     * Erdradius für WGS-84-Geoid in Metern.<p>
     */
    public static final double radius = 6378137;

    /**
     * Erdumfang für WGS-84-Geoid in Metern.<p>
     */
    public static final double circumference = 2. * Math.PI * Wgs84Helper.radius;

    /**
     * Ausdehnung eines Winkelgrades am Äquator in Metern.<p>
     */
    public static final double degree2meter = Wgs84Helper.circumference / 360.; // entspr. 111319 m
}
