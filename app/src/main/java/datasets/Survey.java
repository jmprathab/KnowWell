package datasets;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by jmprathab on 10/12/15.
 */
public class Survey {
    int id;
    TextDrawable image;
    String title, organization;

    public Survey(int id, String title, String organization) {
        this.id = id;
        this.title = title;
        this.organization = organization;
    }

    public Survey(int id, TextDrawable image, String title, String organization) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.organization = organization;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TextDrawable getImage() {
        if (image == null) {
            ColorGenerator generator = ColorGenerator.MATERIAL;
            this.image = TextDrawable.builder()
                    .beginConfig()
                    .toUpperCase()
                    .endConfig()
                    .round().
                            build(String.valueOf(organization.charAt(0)), generator.getRandomColor());
        }
        return image;
    }

    public void setImage(TextDrawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Survey)) return false;

        Survey survey = (Survey) o;

        return id == survey.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
