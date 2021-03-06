/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gamecomponents;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import gamecomponents.memory.IMemory;
import gamecomponents.memory.IMemoryTile;
import gamecomponents.memory.MemoryFactory;
import gamecomponents.memory.MemoryTile;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 *
 * @author wolff
 */
public class MemoryTurn {

    public static final String TAG = "EBTurn";
    public String playerOneName;
    public String playerTwoName;
    public int playerOneScore= 0;
    public int playerTwoScore= 0;
    public int playerturn;
    public IMemory data;
    public MemoryTurn() {
        playerOneName = "testtester";
        playerTwoName = "testtester";
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONArray retVal = new JSONArray();
        for(IMemoryTile memoryTile: data.getTiles()) {
            retVal.put(toJson(memoryTile));
        }
        JSONObject object = new JSONObject();
        try {
            object.put("playerOneName", playerOneName);
            object.put("playerTwoName", playerTwoName);
            object.put("playerOneScore", playerOneScore);
            object.put("playerTwoScore", playerTwoScore);
            object.put("playerTurn", playerturn);
        } catch (JSONException e){
            Log.e("MemoryPlayers", "There was an issue writing JSON!", e);
        }
        retVal.put(object);
        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    public JSONObject toJson(IMemoryTile memoryTile) {
        JSONObject object = new JSONObject();

        try {
            object.put("Type", memoryTile.getType());
            object.put("Checked", memoryTile.getChecked());
            object.put("clickedNumber", memoryTile.getClickedNumber());
        } catch (JSONException e) {
            Log.e("MemoryTurn", "There was an issue writing JSON!", e);
        }
        return object;
    }

    // Creates a new instance of MemoryTurn.
    public void unpersist(byte[] byteArray) throws JSONException {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            data.setTiles(MemoryFactory.getInstance().createMemory().getTiles());
            return;
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        List<IMemoryTile> list = new ArrayList<>();

        JSONArray array = new JSONArray(st);
        for(int i = 0; i<array.length()-1;i++ ) {
            JSONObject object = array.getJSONObject(i);
            list.add(toObject(object));
        }
        JSONObject object = array.getJSONObject(array.length()-1);
        try {
            this.playerOneName = object.getString("playerOneName");
            this.playerTwoName = object.getString("playerTwoName");
            this.playerOneScore = object.getInt("playerOneScore");
            this.playerTwoScore = object.getInt("playerTwoScore");
            this.playerturn = object.getInt("playerTurn");

        } catch (JSONException e) {
            Log.e("MemoryPlayer", "There was an issue parsing JSON!", e);
        }

        data.setTiles(list);
    }

    public static IMemoryTile toObject(JSONObject object) {
        IMemoryTile memoryTile = new MemoryTile();
        try {
                memoryTile.setType(object.getInt("Type"));
                memoryTile.setChecked(object.getBoolean("Checked"));
                memoryTile.setClickedNumber(object.getInt("clickedNumber"));
        } catch (JSONException e) {
            Log.e("MemoryTurn", "There was an issue parsing JSON!", e);
        }




        return memoryTile;
    }
}
