package com.example.t4_peliculas.model;

import java.util.List;

public class Pelicula {



    String original_title, release_date, poster_path, overview;
    int popularity, vote_average, id;




    private List<Integer> genreIds;

    boolean adult;




    public Pelicula(String original_title, String release_date, int popularity, int vote_average, List<Integer> genreIds, boolean adult, String poster_path,String overview, int id) {
        this.original_title = original_title;
        this.release_date = release_date;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.genreIds = genreIds;
        this.adult = adult;
        this.poster_path = poster_path;
        this.overview = overview;
        this.id = id;

    }
    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }
    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }
    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getVote_average() {
        return vote_average;
    }

    public void setVote_average(int vote_average) {
        this.vote_average = vote_average;
    }
    public int getId() {
        return id;
    }
    @Override
    public String toString() {
        return original_title + release_date + popularity + vote_average;
    }
}
