package com.travel721;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeConverters {

    private static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<String> stringToSomeObjectList(String data) {
        if (data == null) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<List<String>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(ArrayList<String> someObjects) {
        return gson.toJson(someObjects);
    }
}