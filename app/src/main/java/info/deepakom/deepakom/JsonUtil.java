package info.deepakom.deepakom;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;


public class JsonUtil {
	private static Gson M_GSON = new Gson();

//	static Gson gson = new GsonBuilder().serializeNulls().create();


	public static String jsonify(Object object) {
		return M_GSON.toJson(object);
	}

	public static Object objectify(String jsonString, Class<?> cls) {
		try {
			return M_GSON.fromJson(jsonString, cls);
		}
		catch (JsonSyntaxException e) {
		}
		return null;
	}

	public static <cls> Object arrayObjectify(String jsonString, Class<?> cls) {
		try {
			return M_GSON.fromJson(jsonString, new TypeToken<cls>() {}.getType());
		}
		catch (Exception e) {
		}
		return null;
	}

	public static Object objectify(String jsonString, Type collectionType) {
		try {
			return M_GSON.fromJson(jsonString, collectionType);

		}
		catch (JsonSyntaxException e) {
		}
		return null;
	}

	public static HashMap<String, String> jsontoHashMap(String json) {
		try {
			HashMap<String, String> map = M_GSON.fromJson(json,
					new TypeToken<HashMap<String, String>>() {}.getType());
			return map;
		}
		catch (Exception e) {
		}
		return null;
	}

	public static Object objectify(DataSnapshot dataSnapshot, Type collectionType) {
		try {
			String json = jsonify(dataSnapshot.getValue(true));
			return M_GSON.fromJson(json, collectionType);

		}
		catch (JsonSyntaxException e) {
		}
		return null;
	}
}
