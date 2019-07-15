package kds.skaui.businessturns;

/**
 * Created by shaikarniro on 6.4.2018.
 */

public class Notification {
    private String body;
    private String date;

    public Notification(String body, String date) {
        this.body = body;
        this.date = date;
    }

    public Notification() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
