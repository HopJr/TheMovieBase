package com.example.blago.themoviedb.MovieModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SearchMovie implements Parcelable {

    public int page;
    public int total_results;
    public int total_pages;
    public List<Result> results;


    protected SearchMovie(Parcel in) {
        page = in.readInt();
        total_results = in.readInt();
        total_pages = in.readInt();
        results = in.createTypedArrayList(Result.CREATOR);
    }

    public static final Creator<SearchMovie> CREATOR = new Creator<SearchMovie>() {
        @Override
        public SearchMovie createFromParcel(Parcel in) {
            return new SearchMovie(in);
        }

        @Override
        public SearchMovie[] newArray(int size) {
            return new SearchMovie[size];
        }
    };

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeInt(total_results);
        dest.writeInt(total_pages);
        dest.writeTypedList(results);
    }
}
