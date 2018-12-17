package rupesh.technetty.com.mymims;

public class ImageUploadInfo {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    String title;
    String description;
    String image;
    String search;

    public ImageUploadInfo() {
    }

    public ImageUploadInfo(String title, String description, String image, String search) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.search = search;
    }
}
