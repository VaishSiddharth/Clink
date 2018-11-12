package model;

/**
 * Created by wolfsoft4 on 21/9/18.
 */

public class AllModel {

    Integer image;
    String name,time,txt;

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public AllModel(Integer image, String name, String time, String txt) {
        this.image = image;
        this.name = name;
        this.time = time;
        this.txt = txt;
    }
}
