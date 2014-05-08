package sg.edu.astar.ihpc.passenger.entity;

import java.util.Date;

public class PassengerDestination {
	private Long id;
	private Date createtime;
	private String locationAddress;
	private Location location;
	private Passenger passengerid;
	public Passenger getPassengerid() {
		return passengerid;
	}
	public void setPassengerid(Passenger passengerid) {
		this.passengerid = passengerid;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getLocationAddress() {
		return locationAddress;
	}
	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
