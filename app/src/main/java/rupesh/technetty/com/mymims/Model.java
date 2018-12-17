package rupesh.technetty.com.mymims;

public class Model {
    String title, image, description; //this name mut be the same a the firebasse databae attributess;
    public Model(){ }

    public String getTitle() {
        return title;
    }
//create gatter and setter
    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
