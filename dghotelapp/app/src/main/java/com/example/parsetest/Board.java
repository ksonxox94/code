package com.example.parsetest;

public class Board {
    private String hotel_name;
    private String title;
    private String contents;
    private String name;

    public Board(String hotel_name, String title, String contents, String name) {
        this.hotel_name = hotel_name;
        this.title = title;
        this.contents = contents;
        this.name = name;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_id) {
        this.hotel_name = hotel_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Board{" +
                "hotel_name='" + hotel_name + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
