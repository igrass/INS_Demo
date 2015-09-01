package cc.minieye.instoolkit.drag3;

public class RowItem {
    private String desc;
    private int imageId;
    private String title;

    public RowItem(int i, String str, String str2) {
        this.imageId = i;
        this.title = str;
        this.desc = str2;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getImageId() {
        return this.imageId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDesc(String str) {
        this.desc = str;
    }

    public void setImageId(int i) {
        this.imageId = i;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String toString() {
        return this.title + "\n" + this.desc;
    }
}
