package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;

public class Main {

	static String titleOfHackathon = "FixedHackUMass"; // for output file (no
														// spaces)
	static int schoolCode = 166629; // first column in universities.csv
	static double yourSchoolLongitude = -72.532821; // second-to-last
													// column in
													// universities.csv
	static double yourSchoolLatitude = 42.390164; // last column in
													// universities.csv

	public static void main(String[] args) {

		final int MAX_NUM = 2400; // Google API download limit is 2500

		Scanner scan = new Scanner(System.in);

		System.out.println("Enter the name of the hackathon (NO SPACES)");

		String tempName;

		do {
			tempName = scan.nextLine();

			if (tempName.contains(" ")) {
				System.out.println("No spaces allowed!");
			}
		} while (tempName.contains(" "));

		if (!tempName.equals("0"))
			titleOfHackathon = tempName;

		System.out.println("Enter the school code from universities.csv");
		int tempCode = scan.nextInt();
		if (tempCode != 0)
			schoolCode = tempCode;

		System.out.println("Enter the longitude of your school:");
		double testLon = scan.nextDouble();
		if (testLon != 0)
			yourSchoolLongitude = testLon;

		System.out.println("Enter the latitude of your school:");
		double testLat = scan.nextDouble();
		if (testLat != 0)
			yourSchoolLatitude = testLat;

		try {

			Hashtable<Integer, Integer> hash = getHashtable();

			ArrayList<School> list = new ArrayList<>();
			BufferedReader br = new BufferedReader(new FileReader(new File(
					"universities.csv")));
			String line = br.readLine();

			while ((line = br.readLine()) != null) {
				String[] lineSplit = line.split(",");
				int id = Integer.parseInt(lineSplit[0]);
				String name = lineSplit[1].substring(1,
						lineSplit[1].length() - 1);
				String address;

				// fixes when commas occur in address for suites
				int increment = 0;
				if (lineSplit[3].contains("uite")) {
					address = (lineSplit[2].substring(1,
							lineSplit[2].length() - 1))
							+ " "
							+ lineSplit[3].substring(0,
									lineSplit[3].length() - 1);
					increment = 1;
				} else {
					address = lineSplit[2].substring(1,
							lineSplit[2].length() - 1);
				}

				String city = lineSplit[3 + increment];
				String state = lineSplit[4 + increment];

				int pop = 0;
				if (hash.containsKey(new Integer(id))) {
					pop = hash.get(id);
				}

				double lon = new Double(lineSplit[lineSplit.length - 2]);
				double lat = new Double(lineSplit[lineSplit.length - 1]);
				School sc = new School(id, name, address, city, state, lon,
						lat, pop, distance(yourSchoolLatitude,
								yourSchoolLongitude, lat, lon, 'm'));
				list.add(sc);
			}
			br.close();

			Collections.sort(list);

			File f = new File(titleOfHackathon + "SchoolsData.csv");
			f.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					(titleOfHackathon + "SchoolsData.csv"), true));
			bw.write("Name,Address,City,State,Undergraduate Enrollment,Road Distance (mi),Travel Time One Way (hr)");
			int i = 1;

			ArrayList<String> done = getDone();
			int size = done.size();

			for (School s : list) {
				if (!done.contains(s.getName())) {
					if (!(s.getId() == schoolCode)) {
						String distStr = googleDistance(s.getLat(), s.getLon());
						String[] split = distStr.split(",");
						s.setDist(split[0]);
						s.setTravelTime(split[1]);
					} else {
						s.setDist("0");
						s.setTravelTime("0");
					}
					System.out.println((i + size) + " " + s.getName());
					String toWrite = "\n" + s.getName() + "," + s.getAddress()
							+ "," + s.getCity() + "," + s.getState() + ","
							+ s.getNumStudents() + "," + s.getDist() + ","
							+ s.getTravelTime();
					bw.write(toWrite);

					if (i >= MAX_NUM) {
						bw.close();
						break;
					}
					i++;
				}
			}
			bw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static ArrayList<String> getDone() throws IOException {
		ArrayList<String> done = new ArrayList<>();
		File f = new File(titleOfHackathon + "SchoolsData.csv");
		if (f.isFile()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				done.add(line.split(",")[0]);
			}
			br.close();
		}
		return done;
	}

	private static String googleDistance(double lat, double lon)
			throws Exception {
		// Thread.sleep(100);
		String toReturn = "";
		URL url = new URL(
				"https://maps.googleapis.com/maps/api/distancematrix/json?origins="
						+ lat + "," + lon + "&destinations="
						+ yourSchoolLatitude + "," + yourSchoolLongitude);

		URLConnection urlc = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlc.getInputStream()));

		String l;
		while ((l = in.readLine()) != null) {
			if (l.contains("distance")) {
				l = in.readLine();
				// arrives as km, convert to mi
				String[] lSplit = l.split("\\s");
				String kmsStr = lSplit[lSplit.length - 2].substring(1);
				kmsStr = kmsStr.replace(",", "");
				Double kms = new Double(kmsStr);
				Double miles = kms * 0.621371;
				toReturn += miles.toString();

			} else if (l.contains("duration")) {
				l = in.readLine();
				// time text, parse to get hours
				String[] lSplit2 = l.split("\\s");
				String hrsStr = "0";
				String minsStr = lSplit2[lSplit2.length - 2];

				if (lSplit2.length == 24) {
					hrsStr = lSplit2[lSplit2.length - 4].substring(1);
				} else {
					minsStr = minsStr.substring(1);
				}

				Double hrs = new Double(hrsStr);
				Double mins = new Double(minsStr);
				mins = mins / new Double(60);

				Double time = hrs + mins;
				toReturn += "," + time.toString();
			}
		}

		in.close();

		return toReturn;
	}

	private static Hashtable<Integer, Integer> getHashtable() {
		// TODO Auto-generated method stub
		Hashtable<Integer, Integer> hash = new Hashtable<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					"univ_pop.txt")));
			String line;
			while ((line = br.readLine()) != null) {
				String[] lineSplit = line.split(" ");
				int key = Integer.parseInt(lineSplit[0]);

				// 420194 Amerian National College Houston TX 202UnitID Name
				// City State Value
				String valStr = lineSplit[lineSplit.length - 1];

				if (valStr.equals("Value")) {
					String chg = lineSplit[lineSplit.length - 5];
					valStr = chg.substring(0, chg.length() - 6);
				}

				int value = Integer.parseInt(valStr);
				hash.put(key, value);
			}
			br.close();
			return hash;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static double distance(double lat1, double lon1, double lat2,
			double lon2, char unit) {

		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));

		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;

		if (unit == 'k' || unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'm' || unit == 'M') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
}