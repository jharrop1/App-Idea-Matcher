package edu.neu.ideamatch;

public class IdeaDetails {
//    private int image;
    private String imageURL;
    private String ideaName, ideaDescription, creatorName, desiredSkills, contactInfo;

    public IdeaDetails() {
    }

    //constructer with string for image
    public IdeaDetails(String cimageURL,
                       String cideaName,
                       String ccontactInfo,
                       String cideaDescription,
                       String ccreatorName,
                       String cdesiredSkills) {
        this.imageURL = cimageURL;
        this.ideaName = cideaName;
        this.contactInfo = ccontactInfo;
        this.ideaDescription = cideaDescription;
        this.creatorName = ccreatorName;
        this.desiredSkills = cdesiredSkills;
    }

//    //Constructer with an int for image
//    public IdeaDetails(int cimage,
//                       String cideaName,
//                       String ccontactInfo,
//                       String cideaDescription,
//                       String ccreatorName,
//                       String cdesiredSkills) {
//        this.image = cimage;
//        this.ideaName = cideaName;
//        this.contactInfo = ccontactInfo;
//        this.ideaDescription = cideaDescription;
//        this.creatorName = ccreatorName;
//        this.desiredSkills = cdesiredSkills;
//    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

//    public int getImage() {
//        return image;
//    }
//
//    public void setImage(int image) {
//        this.image = image;
//    }

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
