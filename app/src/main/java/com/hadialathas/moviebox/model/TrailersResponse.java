package com.hadialathas.moviebox.model;

/**
 * Created by hadialathas on 7/30/17.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailersResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailers> trailers = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailers> getResults() {
        return trailers;
    }

    public void setResults(List<Trailers> results) {
        this.trailers = results;
    }

}
