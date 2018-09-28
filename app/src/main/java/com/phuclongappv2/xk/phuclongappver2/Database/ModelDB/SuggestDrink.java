package com.phuclongappv2.xk.phuclongappver2.Database.ModelDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "SuggestDrink")
public class SuggestDrink {
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "ID")
    public String dId;
    @ColumnInfo(name = "Name")
    public String dName;
    @ColumnInfo(name = "imageCold")
    public String dImageCold;
    @ColumnInfo(name = "imageHot")
    public String dImageHot;
    @ColumnInfo(name = "categoryID")
    public String dCategoryID;
    @ColumnInfo(name = "Price")
    public int dPrice;
}
