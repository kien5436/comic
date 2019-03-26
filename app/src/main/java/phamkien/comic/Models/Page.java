package phamkien.comic.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import phamkien.comic.Services.DataService;

public class Page {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("content")
    @Expose
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return DataService.getBaseUrl() + content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
