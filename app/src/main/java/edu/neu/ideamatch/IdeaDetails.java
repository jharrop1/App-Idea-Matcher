package edu.neu.ideamatch;

public class IdeaDetails {
    private int image;
    private String ideaName, ideaDescription, creatorName, desiredSkills, contactInfo;

    public IdeaDetails() {
    }

    public IdeaDetails(int cimage,
                       String cideaName,
                       String ccontactInfo,
                       String cideaDescription,
                       String ccreatorName,
                       String cdesiredSkills) {
        this.image = cimage;
        this.ideaName = cideaName;
        this.contactInfo = ccontactInfo;
        this.ideaDescription = cideaDescription;
        this.creatorName = ccreatorName;
        this.desiredSkills = cdesiredSkills;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getIdeaName() {
        return ideaName;
    }

    public void setIdeaName(String ideaName) {
        this.ideaName = ideaName;
    }

    public String getIdeaDescription() {
        return ideaDescription;
    }

    public void setIdeaDescription(String ideaDescription) {
        this.ideaDescription = ideaDescription;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getDesiredSkills() {
        return desiredSkills;
    }

    public void setDesiredSkills(String desiredSkills) {
        this.desiredSkills = desiredSkills;
    }
}
