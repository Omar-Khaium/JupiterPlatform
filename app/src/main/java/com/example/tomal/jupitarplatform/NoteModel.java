package com.example.tomal.jupitarplatform;

public class NoteModel {
    private String Id;
    private String Name;
    private String Date;
    private String Note;

    public NoteModel() {
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
