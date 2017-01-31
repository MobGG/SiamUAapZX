package siamumap.dto;

import android.graphics.Bitmap;

/**
 * Created by Mob on 11-Oct-15.
 */
public class Post {
    protected String postID;
    protected String postTitle;
    protected String postPlace;
    protected String postDescription;
    protected String postDate;
    protected Bitmap postImage;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostPlace() {
        return postPlace;
    }

    public void setPostPlace(String postPlace) {
        this.postPlace = postPlace;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public Bitmap getPostImage() {
        return postImage;
    }

    public void setPostImage(Bitmap postImage) {
        this.postImage = postImage;
    }
}
