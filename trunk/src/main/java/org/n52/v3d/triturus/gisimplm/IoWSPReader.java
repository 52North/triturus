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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Vector;

import org.n52.v3d.triturus.core.IoObject;
import org.n52.v3d.triturus.core.T3dException;
import org.n52.v3d.triturus.t3dutil.T3dVector;

/**
 * reads in application-special cross-section data.<br /><br />
 * <i>German:</i> Einlesen wasserbaulicher Querprofile.<p>
 * @see GmWSPProfile
 * @author Torsten Heinen
 */
public class IoWSPReader extends IoObject
{
	private String fName;
	private int tokType = 0;
	private Vector wspProfiles = new Vector(100,20);
	
	public IoWSPReader(String fName) {
		this.fName = fName;
		readProfilesFromFile(this.fName);	
	}
	
	public int numProfiles() {
		return wspProfiles.size();
	}

	/**
	 * returns the number of section-points.<br /><br />
	 * <i>German:</i> Gibt die Punkte der Profile zur�ck. Z.B.: double[0][2*n] = [x1,y1,x2,y2,..xn,yn]
	 * double[0][0..n] = Punkte links des Flusses mit Bemerkung "DGM"
	 * double[1][0..n] = Punkte links des Flusses bis zum Uferbereich 
	 * double[2][0..n] = Punkte des Flussbettes
	 * double[3][0..n] = Punkte rechts des Flusses bis zum Uferbereich 
	 * double[4][0..n] = Punkte rechts des Flusses mit Bemerkung "DGM"
	 */
	public double[][] getProfiles(int start, int end) {
		double[][] ret =
			((GmWSPProfile) wspProfiles.get(start)).getProfilePoints();
		double[][] current = new double[5][0];
		for (int i = start + 1; i < end + 1; i++) {
			current = ((GmWSPProfile) wspProfiles.get(i)).getProfilePoints();
			for (int j = 0; j < 5; j++) {
				double temp[] = new double[ret[j].length + current[j].length];
				System.arraycopy(ret[j], 0, temp, 0, ret[j].length);
				System.arraycopy(
					current[j],
					0,
					temp,
					ret[j].length,
					current[j].length);
				ret[j] = temp;
			}

		}
		return ret;

	}

	public GmLineString getFlowLine(boolean dreierStreifen) {
		GmPoint point;
		GmLineString flow = new GmLineString();
		GmWSPProfile profile;

		for (int i = 0; i < wspProfiles.size(); i++) {
			profile = (GmWSPProfile) wspProfiles.get(i);
			point = profile.getCenterPoint();
			if (point != null)
				flow.addVertex(point);
		}
		if (flow.numberOfVertices() > 2) {
			if (dreierStreifen)
				return flow.getConverted();
			else
				return flow;
		} else
			return null;
	}

	public GmWSPProfile[] getWSPProfiles(boolean dreierStreifen) {
		GmWSPProfile[] ret = new GmWSPProfile[wspProfiles.size()];
		for (int i = 0; i < wspProfiles.size(); i++) {
			GmWSPProfile profile = (GmWSPProfile) wspProfiles.get(i);
			if (dreierStreifen)
				profile.dreierStreifen(dreierStreifen);
			ret[i] = profile;
		}
		return ret;
	}

	// Umwandlung von gon -> grad
	public GmLineString[] getProfileLines(boolean dreierStreifen, boolean withDGMvalues) {
		GmLineString[] profLines = new GmLineString[wspProfiles.size()];
		for (int j = 0; j < profLines.length; j++) {
			GmLineString prof = new GmLineString();
			GmWSPProfile profil = (GmWSPProfile) wspProfiles.get(j);
			//System.out.println("LINESTRING: " + (GmWSPProfile)wspProfiles.get(0));
			GmPoint center = profil.getCenterPoint();
			GmPoint newPoint;
			double gon = profil.getGon();
			double degree = (gon / 10) * 9;
			// System.out.println("Profil " + j + ": " + degree + " degrees / "+ (degree*Math.PI)/180 + " radians; Center:" + center);		
			// berechne jeden Punkt des Profils
			// Math.sin() brauch radians als input --> convert degree nach radians
			// radians = (degree * PI) /180
			for (int i = 0; i < profil.getProfileCount(); i++) {
				float dist = (profil.profileRecords[i]).getDistance();
				String note = (profil.profileRecords[i]).getNote();
				//System.out.println("Note " + j + "." + i + ": " + note);
				newPoint =
					new GmPoint(
						center.getX()
							+ (Math.sin((degree * Math.PI) / 180) * dist),
						center.getY()
							+ (Math.cos((degree * Math.PI) / 180) * dist),
						(profil.profileRecords[i]).getHeight());
				if (withDGMvalues) {
					prof.addVertex(newPoint);
				} else if (!note.equalsIgnoreCase("dgm"))
					prof.addVertex(newPoint);

			}
			if (dreierStreifen)
				profLines[j] = prof.getConverted();
			else
				profLines[j] = prof;
			//System.out.println("profLines " + profLines[j].toString());
		}
		return profLines;
	}

	/**
	 * @param string
	 */
	private void readProfilesFromFile(String string) {
		System.out.println("Loading WSP file: " + fName);
		int lineNumber = 0;
		try {
			// init StreamTokenizer
			FileReader fileRead = new FileReader(fName);
			BufferedReader buffRead = new BufferedReader(fileRead);
			StreamTokenizer tokenizer = new StreamTokenizer(buffRead);

			tokenizer.lowerCaseMode(true);
			//alle Tokens in Kleinbuchstaben holen
			tokenizer.wordChars('_', '_');
			tokenizer.wordChars('-', '-');
			tokenizer.wordChars(':', ':');
			tokenizer.eolIsSignificant(false);

			//Flussmittelpunkt: x/y= Gauss-Kr�ger; z Koordinate bezeichnet Profilewinkel in GON 
			T3dVector temp = new T3dVector();
			// im CC Bereich gibt es mit unter bis zu drei Zusatzpunkte...
			T3dVector altZero = new T3dVector();
			//"Null-Punkt" -> Datensatz enth�lt kein 0.00 Punkt => X/Y bezieht sich wahrscheinlich auf ALTernativen "Nullpunkt"
			T3dVector altBearing = new T3dVector();
			//"Peilprofil" -> Datensatz enth�lt kein 0.00 Punkt => Grad GON bezieht sich wahrscheinlich auf "Peilprofil"
			T3dVector altWSP = new T3dVector(); //"WSP" ?????
			//			double d0; //wird zwar ausgelesen aber noch nicht von readCC(...) weitergegeben
			// ...wobei: point2d.x = Distance; point2d.y = height;
			GmWSPProfile profiles = null;

			while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
				boolean isData =
					readCC(tokenizer, temp, altZero, altBearing, altWSP);
				if (isData) {

					profiles = readProfileInfo(tokenizer);
					profiles.setProfileReference(new T3dVector(temp.getX(), temp.getY(), 0));
					profiles.setGon(temp.getZ());

					//Zus�tzliche Punkte ausm CC Block ins profil schreiben...
					//wenn keine vorhanden sind, dann einfach �berschreiben. 
					//Standardm�ssig sind die eh alle auf 0
					profiles.altZeroPosition = (float) altZero.getX();
					profiles.altBearingPosition = (float) altBearing.getX();
					profiles.altWSPPosition = (float) altWSP.getX();
					//System.out.println(altZero + "/" + altBearing + "/" + altWSP);

					readPoints(tokenizer, profiles);

					wspProfiles.add(profiles);
					//System.out.println(profiles.toString());
				}
			} //while
			fileRead.close();

		} catch (FileNotFoundException e) {
			throw new T3dException("Could not access file \"" + fName + "\"");
		} catch (Exception e) {
			throw new T3dException(
				"Parser error in \"" + fName + "\":" + lineNumber);
		}

	}

	/**
	 * @param tokenizer
	 * @param profile
	 */
	private boolean readPoints(StreamTokenizer tok, GmWSPProfile profiles) {
		boolean readComplete = false;
		int read = 0;
		float distance, height;
		String kz = "", note = "";

		try {
			do {
				tokType = tok.nextToken();
				if (tokType == StreamTokenizer.TT_NUMBER) {
					//System.out.println("1="+tok.nval);
					do {
						if (tok.nval > 3.0E11) {
							tok.nextToken();
							distance = (float) tok.nval;
							tok.nextToken();
							height = (float) tok.nval;
							tokType = tok.nextToken();
							// wenn noch Bemerkungen und KennZahl... (es werden nur Strings zugelassen)
							//System.out.println("!"+tokType+tok.nval+tok.sval+"!");
							if (tokType == StreamTokenizer.TT_WORD) {
								//System.out.println("Word: "+tok.sval+"!!");
								if (tok.sval.equalsIgnoreCase("LU")
									|| tok.sval.equalsIgnoreCase("RU")
									|| tok.sval.equalsIgnoreCase("PA")
									|| tok.sval.equalsIgnoreCase("PE")) {
									kz = tok.sval;
									//evtl. kommt noch ne Bemerkung
									tokType = tok.nextToken();
									if (tokType == StreamTokenizer.TT_WORD)
										note = tok.sval;

								} else {
									kz = "";
									note = tok.sval;
								}

							} else if (tokType == StreamTokenizer.TT_NUMBER) {
								kz = "";
								note = "";
							}
							//System.out.println(read+"="+distance + "/"+height + "/"+kz + "/"+note + "/");
							profiles.setProfileRecord(
								read,
								distance,
								height,
								kz,
								note);
							read++;

						}
						tok.nextToken();

					} while (read < profiles.getProfileCount());

					readComplete = true;
				}
			}
			while (readComplete == false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readComplete;

	}

	/**
	 * @return
	 */
	private GmWSPProfile readProfileInfo(StreamTokenizer tok) {
		boolean readComplete = false;
		int profilePoints = 0;
		int profileID = 0;

		try {
			do {
				tokType = tok.nextToken();
				if (tokType == StreamTokenizer.TT_NUMBER) {
					// ?????
					//System.out.println("1="+tok.nval);
					if (tok.nval > 2.0E11) {
						profileID = (int) (tok.nval - 2.0E11) / 10000;
						tok.nextToken();
						profilePoints = (int) tok.nval;
						readComplete = true;
					}

					//System.out.println("Profile "+profileID +" = " + profilePoints + " Profilepunkte");  			
				}
			} while (
				readComplete == false && tok.ttype != StreamTokenizer.TT_EOF);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new GmWSPProfile(profileID, profilePoints);
	}

	private boolean readCC(
		StreamTokenizer tok,
		T3dVector point,
		T3dVector altZero,
		T3dVector altProfil,
		T3dVector altWSP) {
		boolean readComplete = false;
		boolean ccEnd = false;
		double temp1 = 0, temp2 = 0;

		try {
			do {
				tokType = tok.nextToken();
				if (tokType == StreamTokenizer.TT_WORD) {
					if (tok.sval.endsWith("_x:")) {
						tokType = tok.nextToken();
						//System.out.println(tok.nval);
						point.setX( tok.nval );
					} else if (tok.sval.endsWith("_y:")) {
						tokType = tok.nextToken();
						//System.out.println(tok.nval);
						point.setY( tok.nval );
					} else if (
						tok.sval.endsWith("gn") || tok.sval.endsWith("gon")) {
						tokType = tok.nextToken(); // -> :
						tokType = tok.nextToken();
						//System.out.println(tok.nval);
						point.setZ( tok.nval );
						readComplete = true;
					} else if (tok.sval.equals("d")) {
						tokType = tok.nextToken(); // -> :
						tokType = tok.nextToken();
						//System.out.println(tok.nval);
						//d0 = tok.nval;							
					}

				}
			} while (
				readComplete == false && tok.ttype != StreamTokenizer.TT_EOF);
			//System.out.println(point);

			//completed, but check for additional points in CC-block...
			if (readComplete) {
				while (!ccEnd) {
					tokType = tok.nextToken();
					if (tokType == StreamTokenizer.TT_WORD) {
						//System.out.println(tok.sval);
						if (tok.sval.endsWith("-----")) {
							ccEnd = true;
						} else if (tok.sval.endsWith("punkt")) {
							//System.out.println("nullpunkt in " + tok.lineno());					  
							altZero.setX(temp1);
							altZero.setY(temp2);
						} else if (tok.sval.endsWith("peilprofil")) {
							//System.out.println("peilpunkt in " + tok.lineno());
							altProfil.setX(temp1);
							altProfil.setY(temp2);
						} else if (tok.sval.endsWith("wsp")) {
							//System.out.println("WSP in " + tok.lineno());
							altWSP.setX(temp1);
							altWSP.setY(temp2);
						}

					} else if (tokType == StreamTokenizer.TT_NUMBER) {
						//System.out.println(tok.nval);
						temp1 = temp2;
						temp2 = tok.nval;
						//tokType = tok.nextToken();
						//temp2 = tok.nval;					
					}
					//System.out.println(temp1 + " " + temp2);
					//System.out.println(tok.nval + " " + tok.sval);
				}
			} //if
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readComplete;
	}

	/* (non-Javadoc)
	 * @see org.n52.v3d.triturus.vgis.IoObject#log()
	 */
	public String log() {
		// TODO Auto-generated method stub
		return null;
	}

}
