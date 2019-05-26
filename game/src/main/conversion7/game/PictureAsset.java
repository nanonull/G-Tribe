package conversion7.game;

public enum PictureAsset {

    STAND_MAN("1 stand_man.png"),
    ROCK_DRAW("2 rock_draw.png"),
    SHIP_CRASH("ship_crash.png"),
    ;

    private final String fileName;

    PictureAsset(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
