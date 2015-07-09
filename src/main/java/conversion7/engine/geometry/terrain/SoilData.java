package conversion7.engine.geometry.terrain;

public class SoilData {

    public float dirt;
    public float sand;
    public float stone;
    public float reserved;

    public SoilData(float dirt, float sand, float stone, float reserved) {
        this.dirt = dirt;
        this.sand = sand;
        this.stone = stone;
        this.reserved = reserved;
    }

    public SoilData() {

    }

    public void append(SoilData soil, double alpha) {
        dirt += soil.dirt * alpha;
        sand += soil.sand * alpha;
        stone += soil.stone * alpha;
        reserved += soil.reserved * alpha;
    }

    public void append(SoilData soil) {
        dirt += soil.dirt;
        sand += soil.sand;
        stone += soil.stone;
        reserved += soil.reserved;
    }

    public void minus(SoilData soil) {
        dirt -= soil.dirt;
        sand -= soil.sand;
        stone -= soil.stone;
        reserved -= soil.reserved;
    }

    public void divide(int onValue) {
        dirt /= onValue;
        sand /= onValue;
        stone /= onValue;
        reserved /= onValue;
    }

    public void multiply(float onValue) {
        dirt *= onValue;
        sand *= onValue;
        stone *= onValue;
        reserved *= onValue;
    }

    public void set(SoilData soil) {
        dirt = soil.dirt;
        sand = soil.sand;
        stone = soil.stone;
        reserved = soil.reserved;
    }
}
