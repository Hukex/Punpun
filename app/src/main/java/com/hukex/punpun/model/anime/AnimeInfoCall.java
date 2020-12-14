package com.hukex.punpun.model.anime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class AnimeInfoCall {

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
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("trailer_url")
    @Expose
    public String trailerUrl;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("title_english")
    @Expose
    public String titleEnglish;
    @SerializedName("title_japanese")
    @Expose
    public String titleJapanese;
    @SerializedName("title_synonyms")
    @Expose
    public List<String> titleSynonyms = null;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("source")
    @Expose
    public String source;
    @SerializedName("episodes")
    @Expose
    public Integer episodes;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("airing")
    @Expose
    public Boolean airing;
    @SerializedName("aired")
    @Expose
    public Aired aired;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("rating")
    @Expose
    public String rating;
    @SerializedName("score")
    @Expose
    public Double score;
    @SerializedName("scored_by")
    @Expose
    public Integer scoredBy;
    @SerializedName("rank")
    @Expose
    public Integer rank;
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
    public Object background;
    @SerializedName("premiered")
    @Expose
    public String premiered;
    @SerializedName("broadcast")
    @Expose
    public String broadcast;
    @SerializedName("related")
    @Expose
    public Related related;
    @SerializedName("producers")
    @Expose
    public List<Producer> producers = null;
    @SerializedName("licensors")
    @Expose
    public List<Licensor> licensors = null;
    @SerializedName("studios")
    @Expose
    public List<Studio> studios = null;
    @SerializedName("genres")
    @Expose
    public List<Genre> genres = null;
    @SerializedName("opening_themes")
    @Expose
    public List<String> openingThemes = null;
    @SerializedName("ending_themes")
    @Expose
    public List<String> endingThemes = null;

}