package mapleglory.provider.wz.property;

public final class WzVectorProperty extends WzProperty {
    private final int x;
    private final int y;

    public WzVectorProperty(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
