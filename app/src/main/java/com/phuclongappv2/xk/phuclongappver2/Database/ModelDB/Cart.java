package com.phuclongappv2.xk.phuclongappver2.Database.ModelDB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Cart")
public class Cart {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int cId;
    @ColumnInfo(name = "idDrink")
    public int cIdDrink;
    @ColumnInfo(name = "idUser")
    public String uId;
    @ColumnInfo(name = "name")
    public String cName;
    @ColumnInfo(name = "imagecold")
    public String cImageCold;
    @ColumnInfo(name = "imagehot")
    public String cImageHot;
    @ColumnInfo(name = "quanity")
    public int cQuanity;
    @ColumnInfo(name = "price")
    public int cPrice;
    @ColumnInfo(name = "priceItem")
    public int cPriceItem;
    @ColumnInfo(name = "status")
    public String cStatus;
    @ColumnInfo(name = "sugar")
    public String cSugar;
    @ColumnInfo(name = "ice")
    public String cIce;
    @ColumnInfo(name = "topping")
    public String cTopping;
    @ColumnInfo(name = "comment")
    public String cComment;
    @ColumnInfo(name = "pricetopping")
    public int cPriceTopping;
}
