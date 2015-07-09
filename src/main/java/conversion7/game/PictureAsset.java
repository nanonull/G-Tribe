package conversion7.game;

public enum PictureAsset {

    STAND_MAN("stand_man.png"),
    ROCK_DRAW("rock_draw.png");

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
