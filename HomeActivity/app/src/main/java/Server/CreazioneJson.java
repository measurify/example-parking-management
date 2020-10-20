package Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreazioneJson {


    //Versione alternativa per la creazione di oggetti Json
    //Nel pimo array vanno scritti i nomi dei campi da creare per il

    public static JSONObject convert2Json(String[] nomi, String...strings)
    {
        JSONObject jsonObject=new JSONObject();

        try {
            for (int i=0; i<strings.length; i++)
            {
                jsonObject.put(nomi[i], strings[i]);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return  jsonObject;
    }


    public static JSONObject createJSONObject(String[] nomi, Object...objects) throws JSONException {
        JSONObject post= new JSONObject();
        for (int i=0; i<objects.length; i++) {
            post.put(nomi[i], objects[i]);
        }
        return  post;
    }



}

