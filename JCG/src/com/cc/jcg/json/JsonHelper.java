package com.cc.jcg.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelper {

    public static String prettyPrint(String jsonString) {
	JsonParser parser = new JsonParser();
	JsonObject json = parser.parse(jsonString).getAsJsonObject();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String prettyJson = gson.toJson(json);
	return prettyJson;
    }
}
