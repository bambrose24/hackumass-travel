package main;

import java.util.Comparator;

public class School implements Comparator<School>, Comparable<School> {

	String name, address, city, state, travelTime, dist;
	double lon, lat, basicDist;

	

	int numStudents, id;

	public School(int id, String name, String address, String city,
			String state, double lon, double lat, int numStudents,
			double basicDist) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.city = city;
		this.state = state;
		this.lon = lon;
		this.lat = lat;
		this.numStudents = numStudents;
		this.basicDist = basicDist;
	}
	
	public double getBasicDist() {
		return basicDist;
	}

	public void setBasicDist(double basicDist) {
		this.basicDist = basicDist;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getNumStudents() {
		return numStudents;
	}

	public void setNumStudents(int numStudents) {
		this.numStudents = numStudents;
	}

	public String getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDist() {
		return dist;
	}

	public void setDist(String dist) {
		this.dist = dist;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(School s) {
		// TODO Auto-generated method stub
		double s1d = this.getBasicDist();
		double s2d = s.getBasicDist();
		if (s1d > s2d) return 1;
		else if (s1d < s2d) return -1;
		else return 0;
	}

	@Override
	public int compare(School s1, School s2) {
		// TODO Auto-generated method stub
		double s1d = s1.getBasicDist();
		double s2d = s2.getBasicDist();
		if (s1d > s2d) return 1;
		else if (s1d < s2d) return -1;
		else return 0;
	}
}
