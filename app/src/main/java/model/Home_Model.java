package model;

/**
 * Created by wolfsoft4 on 24/9/18.
 */

public class Home_Model {

    Integer bitmap1,imagers;
    String textdji,textprice;

    public Integer getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Integer bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Integer getImagers() {
        return imagers;
    }

    public void setImagers(Integer imagers) {
        this.imagers = imagers;
    }

    public String getTextdji() {
        return textdji;
    }

    public void setTextdji(String textdji) {
        this.textdji = textdji;
    }

    public String getTextprice() {
        return textprice;
    }

    public void setTextprice(String textprice) {
        this.textprice = textprice;
    }

    public Home_Model(Integer bitmap1, Integer imagers, String textdji, String textprice) {
        this.bitmap1 = bitmap1;
        this.imagers = imagers;
        this.textdji = textdji;
        this.textprice = textprice;
    }
}
