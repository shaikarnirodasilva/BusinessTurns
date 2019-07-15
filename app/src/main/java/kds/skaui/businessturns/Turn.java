package kds.skaui.businessturns;

/**
 * Created by shaikarniro on 14.1.2018.
 */

public class Turn {
    private String turnKind;
    private String firstName;
    private String lastName;
    private String date;
    private String time;
    private String UID;
    private String workerName;
    private String phoneNumber;

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerPhone() {
        return workerPhone;
    }

    public void setWorkerPhone(String workerPhone) {
        this.workerPhone = workerPhone;
    }

    private String workerPhone;
    private Boolean isTimer;


    Turn() {
    }

    Turn(String firstName, String lastName, String date, String time, String turnKind, String workerPhone, String workerName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.time = time;
        this.turnKind = turnKind;
        this.workerName = workerName;
        this.phoneNumber = phoneNumber;
        this.workerPhone = workerPhone;

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIsTimer(Boolean time) {
        isTimer = time;
    }

    public Boolean getTimer() {
        return isTimer;
    }

    public String getWorkerName() {
        return workerName;
    }

    String getTurnKind() {
        return turnKind;
    }

    public void setTurnKind(String turnKind) {
        this.turnKind = turnKind;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Turn{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", date='" + date + '\'' +
                ", UID='" + UID + '\'' +
                '}';
    }
}
