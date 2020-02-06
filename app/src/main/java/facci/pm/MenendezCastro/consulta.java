package facci.pm.MenendezCastro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class consulta extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    String cedula = "";
    ArrayList<HashMap<String, String>> contactList;

    TextView nombre;

    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        nombre = (TextView) findViewById(R.id.Name);

        cedula = getIntent().getStringExtra("cedula");

        Log.e("cedula: ", cedula);

        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(consulta.this, "Json Data is downloading", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://backend-posts.herokuapp.com/operative/"+cedula;
            String jsonStr = sh.makeServiceCall(url);

            String url2 = "https://backend-posts.herokuapp.com/checkin/"+cedula;
            String jsonStr2 = sh.makeServiceCall(url2);

            Log.e(TAG, "Response from url: " + jsonStr);
            Log.e(TAG, "Response from url2: " + jsonStr2);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray  jsonObj2 = new JSONArray (jsonStr2);

                    Log.i("json", jsonObj.getString("nombres").toString() +" "+ jsonObj.getString("titulo").toString());
                    text = jsonObj.getString("nombres").toString() +" "+ jsonObj.getString("titulo").toString();



                    for (int i=0; i<jsonObj2.length(); i++){
                        JSONObject c = jsonObj2.getJSONObject(i);
                        String hora = c.getString("fecha");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("fecha", hora);

                        // adding contact to contact list
                        contactList.add(contact);

                    }

                    // Getting JSON Array node
                    /*JSONArray contacts = jsonObj.getJSONArray();

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        // adding contact to contact list
                        contactList.add(contact);
                    }*/
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            nombre.setText(text);
            ListAdapter adapter = new SimpleAdapter(consulta.this, contactList,
                    R.layout.list_item, new String[]{ "fecha"},
                    new int[]{R.id.email});
            lv.setAdapter(adapter);
        }
    }
}
