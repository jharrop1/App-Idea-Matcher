package edu.neu.ideamatch;

import android.net.Uri;

public class IdeaDetails {
//    private int image;
    private String imageURL;
    private String ideaName;
    private String ideaDescription;
    private String creatorName;
    private String desiredSkills;
    private String contactInfo;

    private String projectID;

    public IdeaDetails() {
    }

    //constructer with string for image
    public IdeaDetails(String cideaName,
                       String ccontactInfo,
                       String cideaDescription,
                       String ccreatorName,
                       String cdesiredSkills,
                       String cprojectID,
                       String cimageURL) {
        this.ideaName = cideaName;
        this.contactInfo = ccontactInfo;
        this.ideaDescription = cideaDescription;
        this.creatorName = ccreatorName;
        this.desiredSkills = cdesiredSkills;
        this.projectID = cprojectID;
        this.imageURL = cimageURL;
    }

    public IdeaDetails(
                       String cideaName,
                       String ccontactInfo,
                       String cideaDescription,
                       String ccreatorName,
                       String cdesiredSkills,
                       String cprojectID) {
        this.ideaName = cideaName;
        this.contactInfo = ccontactInfo;
        this.ideaDescription = cideaDescription;
        this.creatorName = ccreatorName;
        this.desiredSkills = cdesiredSkills;
        this.projectID = cprojectID;
    }



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

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
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
