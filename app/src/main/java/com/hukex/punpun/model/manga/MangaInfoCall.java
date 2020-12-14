package com.hukex.punpun.model.manga;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hukex.punpun.model.anime.Genre;
import com.hukex.punpun.model.anime.Related;

import java.util.List;

import lombok.Data;

@Data
public class MangaInfoCall {
    @SerializedName("request_hash")
    @Expose
    public String requestHash;
    @SerializedName("request_cached")
    @Expose
    public Boolean requestCached;
    @SerializedName("request_cache_expiry")
    @Expose
    public Integer requestCacheExpiry;
    @SerializedName("mal_id")
    @Expose
    public Integer malId;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("title_english")
    @Expose
    public String titleEnglish;
    @SerializedName("title_synonyms")
    @Expose
    public List<Object> titleSynonyms = null;
    @SerializedName("title_japanese")
    @Expose
    public String titleJapanese;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("volumes")
    @Expose
    public Integer volumes;
    @SerializedName("chapters")
    @Expose
    public Integer chapters;
    @SerializedName("publishing")
    @Expose
    public Boolean publishing;
    @SerializedName("published")
    @Expose
    public Published published;
    @SerializedName("rank")
    @Expose
    public Integer rank;
    @SerializedName("score")
    @Expose
    public Double score;
    @SerializedName("scored_by")
    @Expose
    public Integer scoredBy;
    @SerializedName("popularity")
    @Expose
    public Integer popularity;
    @SerializedName("members")
    @Expose
    public Integer members;
    @SerializedName("favorites")
    @Expose
    public Integer favorites;
    @SerializedName("synopsis")
    @Expose
    public String synopsis;
    @SerializedName("background")
    @Expose
    public String background;
    @SerializedName("related")
    @Expose
    public Related related;
    @SerializedName("genres")
    @Expose
    public List<Genre> genres = null;
    @SerializedName("authors")
    @Expose
    public List<Author> authors = null;
    @SerializedName("serializations")
    @Expose
    public List<Serialization> serializations = null;

}
