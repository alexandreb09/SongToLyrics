package com.example.songtolyrics.model;

import java.util.List;

public class ResponseOrionSuggestion {

    private boolean success;

    private List<Music> result;

    public ResponseOrionSuggestion(boolean success, List<Music> result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Music> getResult() {
        return result;
    }
}
