package com.example.tomal.jupitarplatform;

public class NoteModel {
    private String Id;
    private String Name;
    private String Date;
    private String Note;
    private String FileType;
    private String photo;
    private String video;

    public NoteModel(String id, String name, String date, String note, String fileType, String photo, String video) {
        Id = id;
        Name = name;
        Date = date;
        Note = note;
        FileType = fileType;
        this.photo = photo;
        this.video = video;
    }

    public NoteModel() {
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public NoteModel(String id, String name, String date, String note) {
        Id = id;
        Name = name;
        Date = date;
        Note = note;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

}
