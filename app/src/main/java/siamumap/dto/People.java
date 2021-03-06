package siamumap.dto;

import android.graphics.Bitmap;

/**
 * Created by Mob on 19-Dec-15.
 */
public class People {
    protected String peopleID;
    protected String peopleName;
    protected String peopleFaculty;
    protected String peopleDepartment;
    protected String peopleBuilding;
    protected String peopleRoom;
    protected String peopleEmail;
    protected String peopleTel;
    protected Bitmap peopleImage;
    protected Double latitude;
    protected Double longitude;

    public String getPeopleID() {
        return peopleID;
    }

    public void setPeopleID(String peopleID) {
        this.peopleID = peopleID;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getPeopleFaculty() {
        return peopleFaculty;
    }

    public void setPeopleFaculty(String peopleFaculty) {
        this.peopleFaculty = peopleFaculty;
    }

    public String getPeopleRoom() {
        return peopleRoom;
    }

    public void setPeopleRoom(String peopleRoom) {
        this.peopleRoom = peopleRoom;
    }

    public Bitmap getPeopleImage() {
        return peopleImage;
    }

    public void setPeopleImage(Bitmap peopleImage) {
        this.peopleImage = peopleImage;
    }

    public String getPeopleDepartment() {
        return peopleDepartment;
    }

    public void setPeopleDepartment(String peopleDepartment) {
        this.peopleDepartment = peopleDepartment;
    }

    public String getPeopleBuilding() {
        return peopleBuilding;
    }

    public void setPeopleBuilding(String peopleBuilding) {
        this.peopleBuilding = peopleBuilding;
    }

    public String getPeopleEmail() {
        return peopleEmail;
    }

    public void setPeopleEmail(String peopleEmail) {
        this.peopleEmail = peopleEmail;
    }

    public String getPeopleTel() {
        return peopleTel;
    }

    public void setPeopleTel(String peopleTel) {
        this.peopleTel = peopleTel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
