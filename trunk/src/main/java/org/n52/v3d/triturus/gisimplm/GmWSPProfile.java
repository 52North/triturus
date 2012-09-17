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
package org.n52.v3d.triturus.gisimplm;

import java.util.ArrayList;

import org.n52.v3d.triturus.t3dutil.GKTransform;
import org.n52.v3d.triturus.t3dutil.T3dVector;

/**
 * class to represent a station's cross-section.<br /><br />
 * <i>German:</i> Repr�sentiert das Profile einer Station (Messstelle).
 * TODO: noch zu optimieren...
 * @see IoWSPReader
 * @author Torsten Heinen
 */
public class GmWSPProfile {
	int slopeNeighbors = 6;
	double slopeFlatToleranz = 0.15;
	double slopeSteepToleranz = 0.40;
	double heightToleranz = 0.15;

	private int stationID;
	private T3dVector profileReferencePoint;
	private double gon;
	public double altZeroPosition = 0;
	public double altBearingPosition = 0;
	public double altWSPPosition = 0;

	//erstmal nur die reinen H�hendaten
	//vorerst public zur schnelleren bearbeitung
	public ProfileRecord[] profileRecords;
	public int lSohleIndex;
	public int rSohleIndex;
	//sp�ter vielleicht noch Abfluss, LL, LF, usw. (siehe Datei)

	public GmWSPProfile(int stationID, int recCount) {
		this.stationID = stationID;
		profileRecords = new ProfileRecord[recCount];
		for (int i = 0; i < recCount; i++) {
			profileRecords[i] = new ProfileRecord();
		}
	}
	

	private int[] uferIndizes() {
		int lu = 0;
		int ru = 0;
		for (int i = 0; i < getProfileCount(); i++) {
			if (profileRecords[i].getKz().equalsIgnoreCase("ru"))
				ru = i;
			if (profileRecords[i].getKz().equalsIgnoreCase("lu"))
				lu = i;
		}
		return new int[] { lu, ru };
	}

	private int[] flussbettIndizes(double[] s) {
		double localMax = Double.MIN_VALUE;
		double localMin = Double.MAX_VALUE;
		int startIndex = Integer.MAX_VALUE;
		int endeIndex = Integer.MIN_VALUE;
		double delta1 = -0.15;
		double delta2 = 0.15;

		for (int i = uferIndizes()[0]; i < uferIndizes()[1]; i++) {
			double absMean = (s[i] + s[i + 1] + s[i + 2]) / 3;
			if (startIndex == Integer.MAX_VALUE
				&& absMean > delta1) { //(meanPos/numPos++)
				localMin = s[i];
				startIndex = i;
			}
			if (startIndex != Integer.MAX_VALUE && absMean > delta2) {
				localMax = s[i];
				endeIndex = i;
				break;
			}

		}
		//System.out.println("FLUSSBETT start: "+ startIndex + "/"+localMin + " ende: " + endeIndex + "/"+localMax);
		return new int[] { startIndex, endeIndex };
	}

	private double[] getHeightMinMax() {
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		for (int i = 0; i < getProfileCount(); i++) {
			double h = profileRecords[i].getHeight();
			if (h > maxValue) {
				maxValue = h;
			}
			if (h < minValue) {
				minValue = h;
			}
		}
		double[] ret = new double[2];
		ret[0] = minValue;
		ret[1] = maxValue;
		return ret;
	}

	private int[] getHeightMinMaxIndizes() {
		double minValue = Double.MAX_VALUE;
		double maxValue = Double.MIN_VALUE;
		int min = 0;
		int max = getProfileCount();
		for (int i = 0; i < getProfileCount(); i++) {
			double h = profileRecords[i].getHeight();
			if (h > maxValue) {
				maxValue = h;
				max = i;
			}
			if (h < minValue) {
				minValue = h;
				min = i;
			}
		}
		int[] ret = new int[2];
		ret[0] = min;
		ret[1] = max;
		return ret;
	}

	public double[] getProfilePoint(int index) {
		double[] ret = new double[3];
		float dist = (profileRecords[index]).getDistance();
		double degree = (gon / 10) * 9;
		ret[0] =
			profileReferencePoint.getX()
				+ (Math.sin((degree * Math.PI) / 180) * dist);
		ret[1] =
			profileReferencePoint.getY()
				+ (Math.cos((degree * Math.PI) / 180) * dist);
		ret[2] = profileRecords[index].getHeight();
		return ret;
	}

	public double[][] getSegmentedProfilePoints(boolean withDGMvalues) {
		ArrayList lDGM = new ArrayList(30);
		ArrayList lUfer = new ArrayList(15);
		ArrayList lHang = new ArrayList(15);
		ArrayList flussbett = new ArrayList(30);
		ArrayList rHang = new ArrayList(15);
		ArrayList rUfer = new ArrayList(15);
		ArrayList rDGM = new ArrayList(30);

		boolean newArea = false;
		double[] slope = new double[getProfileCount()];
		double degree = (gon / 10) * 9;
		for (int i = 0; i < getProfileCount() - 1; i++) {
			double[] current = getProfilePoint(i);
			double[] next = getProfilePoint(i + 1);
			T3dVector p1 = new T3dVector(current[0], current[1], 0);
			double xd = next[0]-current[0];
			double yd = next[1] - current[1];
			double dist = Math.sqrt(xd*xd + yd*yd);
			double hDiff = next[2] - current[2];
			slope[i] = hDiff / dist;
			//System.out.println("Steigung an P." + i + " = " + slope[i]);			
		}

		int[] flussInd = flussbettIndizes(slope);
		int[] uferInd = uferIndizes();

		for (int i = 0; i < getProfileCount(); i++) {
			float dist = (profileRecords[i]).getDistance();
			String note = (profileRecords[i]).getNote();
			String kenn = (profileRecords[i]).getKz();

			if (i <= uferInd[0]) {
				if (withDGMvalues && note.equalsIgnoreCase("dgm")) {
					lDGM.add(
						new Double(
							profileReferencePoint.getX()
								+ (Math.sin((degree * Math.PI) / 180) * dist)));
					lDGM.add(
						new Double(
							profileReferencePoint.getY()
								+ (Math.cos((degree * Math.PI) / 180) * dist)));
					lDGM.add(new Double(profileRecords[i].getHeight()));
				} else if (!note.equalsIgnoreCase("dgm")) {
					if (lUfer.size() == 0 && lDGM.size() != 0) {
						lDGM.add(
							new Double(
								profileReferencePoint.getX()
									+ (Math.sin((degree * Math.PI) / 180)
										* dist)));
						lDGM.add(
							new Double(
								profileReferencePoint.getY()
									+ (Math.cos((degree * Math.PI) / 180)
										* dist)));
						lDGM.add(new Double(profileRecords[i].getHeight()));
					}
					lUfer.add(
						new Double(
							profileReferencePoint.getX()
								+ (Math.sin((degree * Math.PI) / 180) * dist)));
					lUfer.add(
						new Double(
							profileReferencePoint.getY()
								+ (Math.cos((degree * Math.PI) / 180) * dist)));
					lUfer.add(new Double(profileRecords[i].getHeight()));
				}
			}
			if (i >= uferInd[0] && i <= flussInd[0]) {
				rHang.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				rHang.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				rHang.add(new Double(profileRecords[i].getHeight()));
			}
			if (i >= flussInd[0] && i <= flussInd[1]) {
				flussbett.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				flussbett.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				flussbett.add(new Double(profileRecords[i].getHeight()));
			}
			if (i >= flussInd[1] && i <= uferInd[1]) {
				lHang.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				lHang.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				lHang.add(new Double(profileRecords[i].getHeight()));
			}
			if (i >= uferInd[1]) {
				if (withDGMvalues && note.equalsIgnoreCase("dgm")) {
					rDGM.add(
						new Double(
							profileReferencePoint.getX()
								+ (Math.sin((degree * Math.PI) / 180) * dist)));
					rDGM.add(
						new Double(
							profileReferencePoint.getY()
								+ (Math.cos((degree * Math.PI) / 180) * dist)));
					rDGM.add(new Double(profileRecords[i].getHeight()));
				} else if (!note.equalsIgnoreCase("dgm")) {
					if (rUfer.size() != 0 && rDGM.size() == 0) {
						rDGM.add(
							new Double(
								profileReferencePoint.getX()
									+ (Math.sin((degree * Math.PI) / 180)
										* dist)));
						rDGM.add(
							new Double(
								profileReferencePoint.getY()
									+ (Math.cos((degree * Math.PI) / 180)
										* dist)));
						rDGM.add(new Double(profileRecords[i].getHeight()));
					}
					rUfer.add(
						new Double(
							profileReferencePoint.getX()
								+ (Math.sin((degree * Math.PI) / 180) * dist)));
					rUfer.add(
						new Double(
							profileReferencePoint.getY()
								+ (Math.cos((degree * Math.PI) / 180) * dist)));
					rUfer.add(new Double(profileRecords[i].getHeight()));
				}
			}
		}
		double[][] ret = new double[7][];
		ret[0] = new double[lDGM.size()];
		for (int i = 0; i < lDGM.size(); i++)
			ret[0][i] = ((Double) lDGM.get(i)).doubleValue();

		ret[1] = new double[lUfer.size()];
		for (int i = 0; i < lUfer.size(); i++)
			ret[1][i] = ((Double) lUfer.get(i)).doubleValue();

		ret[2] = new double[lHang.size()];
		for (int i = 0; i < lHang.size(); i++)
			ret[2][i] = ((Double) lHang.get(i)).doubleValue();

		ret[3] = new double[flussbett.size()];
		for (int i = 0; i < flussbett.size(); i++)
			ret[3][i] = ((Double) flussbett.get(i)).doubleValue();

		ret[4] = new double[rHang.size()];
		for (int i = 0; i < rHang.size(); i++)
			ret[4][i] = ((Double) rHang.get(i)).doubleValue();

		ret[5] = new double[rUfer.size()];
		for (int i = 0; i < rUfer.size(); i++)
			ret[5][i] = ((Double) rUfer.get(i)).doubleValue();

		ret[6] = new double[rDGM.size()];
		for (int i = 0; i < rDGM.size(); i++)
			ret[6][i] = ((Double) rDGM.get(i)).doubleValue();

		//		System.out.println("lDGM: " + ret[0].length/3);
		//		System.out.println("lUfer: " + ret[1].length/3);
		//		System.out.println("lHang: " + ret[2].length/3);
		//		System.out.println("fluss: " + ret[3].length/3);
		//		System.out.println("rHang: " + ret[4].length/3);
		//		System.out.println("rUfer: " + ret[5].length/3);
		//		System.out.println("rDGM: " + ret[6].length/3);	

		return ret;
	}
	/**
	 * @param i
	 * @return
	 */
	private boolean isFlat(int index, double[] slope) {
		int startInd;
		int endInd;
		double slopeMean = 0;

		if (index - slopeNeighbors / 2 < 0)
			startInd = 0;
		else
			startInd = index - slopeNeighbors / 2;
		if (index + slopeNeighbors / 2 > getProfileCount())
			endInd = getProfileCount() - 1;
		else
			endInd = index + slopeNeighbors / 2;

		for (int i = startInd; i < endInd; i++) {
			slopeMean = slopeMean + slope[i];
		}
		slopeMean = Math.abs(slopeMean / (endInd - startInd));

		if (slopeMean > slopeFlatToleranz)
			return false;
		else
			return true;
	}

	/**
	 * @param i
	 * @return
	 */
	private boolean isSteepSlope(int index, double[] slope) {
		int startInd;
		int endInd;
		double slopeMean = 0;

		if (index - slopeNeighbors / 2 < 0)
			startInd = 0;
		else
			startInd = index - slopeNeighbors / 2;
		if (index + slopeNeighbors / 2 > getProfileCount())
			endInd = getProfileCount() - 1;
		else
			endInd = index + slopeNeighbors / 2;

		for (int i = startInd; i < endInd; i++) {
			slopeMean = slopeMean + slope[i];
		}
		if (Math.abs(slopeMean) > slopeSteepToleranz) {
			return true;
		} else
			return false;
	}

	/**
	 * returns the cross-section's points.<br /><br />
	 * <i>German:</i> Gibt die Punkte des Profils zur�ck. Z.B.: double[0][2*n] = [x1,y1,x2,y2,..getX()n,yn]
	 * double[0][0..n] = Punkte links des Flusses mit Bemerkung "DGM"
	 * double[1][0..n] = Punkte links des Flusses bis zum Uferbereich 
	 * double[2][0..n] = Punkte des Flussbettes
	 * double[3][0..n] = Punkte rechts des Flusses bis zum Uferbereich 
	 * double[4][0..n] = Punkte rechts des Flusses mit Bemerkung "DGM"
	 */
	public double[][] getProfilePoints() {
		ArrayList lDGM = new ArrayList(30);
		ArrayList lUfer = new ArrayList(15);
		ArrayList lHang = new ArrayList(15);
		ArrayList flussbett = new ArrayList(30);
		ArrayList rHang = new ArrayList(15);
		ArrayList rUfer = new ArrayList(15);
		ArrayList rDGM = new ArrayList(30);

		// berechne jeden Punkt des Profils
		// Math.sin() brauch radians als input --> convert degree nach radians
		// radians = (degree * PI) /180
		boolean left = true;
		boolean right = false;
		int area = 0; // = DGM links
		double degree = (gon / 10) * 9;
		for (int i = 0; i < getProfileCount(); i++) {
			float dist = (profileRecords[i]).getDistance();
			String note = (profileRecords[i]).getNote();
			String kenn = (profileRecords[i]).getKz();
			//System.out.println("Note " + i + ": " + note + " / Kenn : " + kenn);
			if (note.equalsIgnoreCase("dgm") && area == 0) {
				//				lDGM.add(new Double( profileReferencePoint.getX() + (Math.sin( (degree*Math.PI)/180 ) * dist)));
				//				lDGM.add(new Double( profileReferencePoint.getY() + (Math.cos( (degree*Math.PI)/180 ) * dist)));
				//				lDGM.add(new Double( profileRecords[i].getHeight() ));
			} else if (area == 0) {
				area++;
			} else if (kenn.equalsIgnoreCase("lu")) {
				lHang.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				lHang.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				lHang.add(new Double(profileRecords[i].getHeight()));
				area++;
			} else if (kenn.equalsIgnoreCase("ru")) {
				lHang.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				lHang.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				lHang.add(new Double(profileRecords[i].getHeight()));
				area++;
			}
			//			else if (area==1) {
			//				lUfer.add(new Double( profileReferencePoint.getX() + (Math.sin( (degree*Math.PI)/180 ) * dist)));
			//				lUfer.add(new Double( profileReferencePoint.getY() + (Math.cos( (degree*Math.PI)/180 ) * dist)));
			//				lUfer.add(new Double( profileRecords[i].getHeight() ));
			//			}
			else if (area == 2) {
				lHang.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				lHang.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				lHang.add(new Double(profileRecords[i].getHeight()));
			} else if (area == 3) {
				flussbett.add(
					new Double(
						profileReferencePoint.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist)));
				flussbett.add(
					new Double(
						profileReferencePoint.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist)));
				flussbett.add(new Double(profileRecords[i].getHeight()));
				//check �bergang			
			}
			//			else if (note.equalsIgnoreCase("dgm") && area!=0) {
			//				rDGM.add(new Double( profileReferencePoint.getX() + (Math.sin( (degree*Math.PI)/180 ) * dist)));
			//				rDGM.add(new Double( profileReferencePoint.getY() + (Math.cos( (degree*Math.PI)/180 ) * dist)));
			//				rDGM.add(new Double( profileRecords[i].getHeight() ));
			//			}			
		}

		double[][] ret = new double[5][];
		ret[0] = new double[lDGM.size()];
		for (int i = 0; i < lDGM.size(); i++) {
			ret[0][i] = ((Double) lDGM.get(i)).doubleValue();
		}
		//System.out.println("LDGM: " + ret[0].length/3);

		ret[1] = new double[lUfer.size()];
		for (int i = 0; i < lUfer.size(); i++) {
			ret[1][i] = ((Double) lUfer.get(i)).doubleValue();
		}
		//System.out.println("LUfer: " + ret[1].length/3);

		ret[2] = new double[flussbett.size()];
		for (int i = 0; i < flussbett.size(); i++) {
			ret[2][i] = ((Double) flussbett.get(i)).doubleValue();
		}
		//System.out.println("Flussbett: " + ret[2].length/3);

		ret[3] = new double[rUfer.size()];
		for (int i = 0; i < rUfer.size(); i++) {
			ret[3][i] = ((Double) rUfer.get(i)).doubleValue();
		}
		//System.out.println("RUfer: " + ret[3].length/3);

		ret[4] = new double[rDGM.size()];
		for (int i = 0; i < rDGM.size(); i++) {
			ret[4][i] = ((Double) rDGM.get(i)).doubleValue();
		}
		//System.out.println("RDGM: " + ret[4].length/3);

		return ret;
	}

	public GmPoint getCenterPoint() {
		float height = -999;

		//XY steht schon fest, aber Z (H�he) muss noch gesucht werden...
		for (int i = 0; i < profileRecords.length; i++) {
			if (altZeroPosition != 0) {
				//System.out.println(altZeroPosition);
				if (profileRecords[i].distance == altZeroPosition)
					height = profileRecords[i].height;
			} else if (profileRecords[i].distance == 0) {
				height = profileRecords[i].height;
			}
		}

		if (height != -999)
			return new GmPoint(
				profileReferencePoint.getX(),
				profileReferencePoint.getY(),
				height);
		else
			return null;
	}

	public void setProfileRecord(
		int index,
		float distance,
		float height,
		String kz,
		String note) {
		//System.out.println("profileRecords.length: "+ profileRecords.length + " setting: "+ distance+" "+ height+" "+kz+" "+note);
		if (index < 0 || index > profileRecords.length - 1) {
			System.out.println(
				"Falscher Stationseintragindex! "
					+ profileRecords.length
					+ " "
					+ index);
			System.exit(0);
		} else {
			profileRecords[index].set(distance, height, kz, note);
		}

	}

	ProfileRecord[] getProfileRecords() {
		return profileRecords;
	}

	/**
	 * @return
	 */
	public int getStationID() {
		return stationID;
	}

	public String toString() {
		String ret = "";
		ret = "Stationsprofil: " + stationID;
		ret = ret + "\nReferenzpunkt: " + profileReferencePoint;
		ret = ret + "\nGrad (in GON): " + gon;
		ret = ret + "\nAlternative Punkte: ";
		if (altZeroPosition != 0)
			ret = ret + "Null-Punkt=" + altZeroPosition + " ";
		if (altBearingPosition != 0)
			ret = ret + "Peilprofil=" + altBearingPosition + " ";
		if (altWSPPosition != 0)
			ret = ret + "WSP=" + altWSPPosition + " ";
		ret = ret + "\n" + profileRecords.length + " Eintr�ge:\n";
		ret = ret + "ID;Distanz;WSP-Lage[�NN];Kennzahl;Bemerkung\n";
		for (int i = 0; i < profileRecords.length; i++) {
			ret = ret + i + ":\t" + profileRecords[i].toString() + "\n";
		}
		return ret;
	}

	//	Stationseintrag
	protected class ProfileRecord {
		int id;
		float distance;
		float height;
		String kz;
		String note;

		public ProfileRecord() {
		};
		public ProfileRecord(
			int id,
			float distance,
			float height,
			String kz,
			String note) {
			this.id = id;
			this.distance = distance;
			this.height = height;
			this.kz = kz;
			this.note = note;
		}

		public void set(float distance, float height, String kz, String note) {
			this.distance = distance;
			this.height = height;
			this.kz = kz;
			this.note = note;
		}

		/**
		 * @return
		 */
		public float getDistance() {
			return distance;
		}

		/**
		 * @return
		 */
		public float getHeight() {
			return height;
		}

		/**
		 * @return
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return
		 */
		public String getKz() {
			return kz;
		}

		/**
		 * @return
		 */
		public String getNote() {
			return note;
		}

		/**
		 * @param f
		 */
		public void setDistance(float f) {
			distance = f;
		}

		/**
		 * @param f
		 */
		public void setHeight(float f) {
			height = f;
		}

		/**
		 * @param i
		 */
		public void setId(int i) {
			id = i;
		}

		/**
		 * @param string
		 */
		public void setKz(String string) {
			kz = string;
		}

		/**
		 * @param string
		 */
		public void setNote(String string) {
			note = string;
		}

		public String toString() {
			return distance
				+ "m;\t"
				+ height
				+ "m;\t"
				+ kz
				+ ";\t"
				+ note
				+ ";";
		}

	}

	/**
	 * @return
	 */
	public double getGon() {
		return gon;
	}

	/**
	 * @return
	 */
	public T3dVector getProfileReferencePoint() {
		return profileReferencePoint;
	}

	/**
	 * @param d
	 */
	public void setGon(double d) {
		gon = d;
	}

	/**
	 * @param point2d
	 */
	public void setProfileReference(T3dVector point2d) {
		profileReferencePoint = point2d;
	}

	public int getProfileCount() {
		return profileRecords.length;
	}


	/**
     * Transformation to Gau&szlig;-Kr&uuml;ger Streifen 3
	 * @param dreierStreifen
	 */
	public void dreierStreifen(boolean dreierStreifen) {
		double[] convertedPoint = new double[2];
		GKTransform.gaussToEll(
				profileReferencePoint.getX(),
				profileReferencePoint.getY(), 
				2, convertedPoint);
		GKTransform.ellToGauss(
				convertedPoint[0], 
				convertedPoint[1], 
				3, convertedPoint);
		profileReferencePoint.setX(convertedPoint[0]);
		profileReferencePoint.setY(convertedPoint[1]);	
	}
	
}
