package org.n52.v3d.triturus.vgis;

import java.util.List;

import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.gisimplm.GmEnvelope;
import org.n52.v3d.triturus.gisimplm.GmMultiPolygon;

/**
 * Simple extension for MultiPolygons (in short: a collection of
 * {@link VgPolygon}).
 * 
 * @author Christian Danowski
 * 
 */
public abstract class VgMultiPolygon extends VgCollection2d {

	protected List<VgPolygon> polygons;

	@Override
	public VgGeomObject2d getGeometry(int i) {
		if (i < 0 || i >= this.getNumberOfGeometries())
			throw new T3dException("Index out of bounds.");

		return this.polygons.get(i);
	}

	@Override
	public int getNumberOfGeometries() {
		return this.polygons.size();
	}

	@Override
	public double area() {
		double summedUpArea = 0;

		for (int i = 0; i < this.polygons.size(); i++) {
			summedUpArea += polygons.get(i).area();
		}
		return summedUpArea;
	}

	@Override
	public VgEnvelope envelope() {
		if (!(this.polygons.size() > 0))
			return null;

		GmEnvelope envelope = null;

		envelope = createEnvelopeFromFirstPoint(envelope);

		// now the envelope should be instantiated
		if (envelope == null)
			return null;

		for (VgGeomObject2d polygon : this.polygons) {

			enlargeEnvelopeForNextPolygon((VgPolygon) polygon, envelope);

		}

		return envelope;

	}

	private GmEnvelope createEnvelopeFromFirstPoint(GmEnvelope envelope) {
		VgPolygon firstPolygon = (VgPolygon) this.polygons.get(0);
		int numberOfVertices = firstPolygon.getOuterBoundary().getNumberOfVertices();

		if (numberOfVertices > 0)
			envelope = new GmEnvelope(firstPolygon.getOuterBoundary().getVertex(0));
		return envelope;
	}

	private void enlargeEnvelopeForNextPolygon(VgPolygon polygon,
			VgEnvelope envelope) {

		int numberOfVertices = polygon.getOuterBoundary().getNumberOfVertices();
		if (numberOfVertices > 0) {
			for (int i = 0; i < numberOfVertices; i++)
				envelope.letContainPoint(polygon.getOuterBoundary().getVertex(i));
		}

	}

	@Override
	public VgGeomObject footprint() {

		GmMultiPolygon multiPolygon = new GmMultiPolygon();

		for (VgPolygon polygon : this.polygons) {
			VgPolygon polygonFootprint = (VgPolygon) polygon.footprint();
			multiPolygon.addPolygon(polygonFootprint);
		}

		return multiPolygon;

	}

	@Override
	public String toString() {
		return "MultiPolygon [polygons=" + polygons + "]";
	}

}
