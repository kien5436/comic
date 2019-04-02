package phamkien.comic.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import phamkien.comic.Services.DataService;

public class Comic implements Parcelable {

    public static final Creator<Comic> CREATOR = new Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel in) {
            return new Comic(in);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("cover")
    @Expose
    private String cover;
    @SerializedName("description")
    @Expose
    private String description;
    private String parentId;
    private boolean like;

    public Comic(String id, String title, String category, String cover, String description, boolean like) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.cover = cover;
        this.description = description;
        this.like = like;
    }

    protected Comic(Parcel in) {
        id = in.readString();
        title = in.readString();
        category = in.readString();
        cover = in.readString();
        description = in.readString();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCover(boolean online) {
        return online ? DataService.getBaseUrl() + cover : cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(category);
        dest.writeString(cover);
        dest.writeString(description);
    }
}