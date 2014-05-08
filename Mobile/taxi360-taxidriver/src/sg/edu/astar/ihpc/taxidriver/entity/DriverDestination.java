package sg.edu.astar.ihpc.taxidriver.entity;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverDestination {
	private Long id;
	private Date createtime;
	private String locationAddress;
	private Driver driverid;
	private Location location;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getLocationAddress() {
		return locationAddress;
	}
	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}
	public Driver getDriverid() {
		return driverid;
	}
	public void setDriverid(Driver driverid) {
		this.driverid = driverid;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
}
