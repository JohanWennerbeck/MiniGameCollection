package gamecomponents;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class MemoryPlayers {
    public String playerOneName;
    public String playerTwoName;
    public int playerOneScore;
    public int playerTwoScore;

    public MemoryPlayers(){}

    public byte[] persist(){
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("playerOneName", playerOneName);
            retVal.put("playerTwoName", playerTwoName);
            retVal.put("playerOneScore", playerOneScore);
            retVal.put("playerTwoScore", playerTwoScore);

        } catch (JSONException e){
            Log.e("MemoryPlayers", "There was an issue writing JSON!", e);
        }
        String st = retVal.toString();

        Log.d("MP", "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));

    }

    static public MemoryPlayers unpersist(byte[] byteArray){
        if (byteArray == null){
            return new MemoryPlayers();
        }
        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        Log.d("MP", "====UNPERSIST \n" + st);

        MemoryPlayers retVal = new MemoryPlayers();

        try {
            JSONObject obj = new JSONObject(st);
            if (obj.has("playerOneName")) {
                retVal.playerOneName = obj.getString("playerOneName");
            }
            if (obj.has("playerTwoName")) {
                retVal.playerTwoName = obj.getString("playerTwoName");
            }
            if (obj.has("playerOneScore")) {
                retVal.playerOneScore = obj.getInt("playerOneScore");
            }
            if (obj.has("playerTwoScore")) {
                retVal.playerTwoScore = obj.getInt("playerTwoScore");
            }

        } catch (JSONException e) {
            Log.e("MP", "There was an issue parsing JSON!", e);
        }
        return retVal;
    }




}
