package id.pekma.pekmavii.FragmentResult.Akademik;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import id.pekma.pekmavii.FragmentHome.HomeData;
import id.pekma.pekmavii.FragmentResult.ResultFragment;
import id.pekma.pekmavii.MainActivity;
import id.pekma.pekmavii.R;

public class FragmentResultOlahraga extends Fragment implements ResultFragment.SendCabol{
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    View rootview;
    int cabolPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_fragment_olahraga,null);
        MainActivity mainActivity = (MainActivity) getActivity();

        final int myDataFromActivity = mainActivity.getCabolData();
        cabolPosition = myDataFromActivity;
        return rootview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AsyncFetch().execute();
    }

    @Override
    public void sendCabolData(int message) {
    }

    public class AsyncFetch extends AsyncTask<String,String,String> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data

                url = new URL("https://pekma.id/result2.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            int ideventRaw,ideventTrue;

            //this method will be running on UI thread
            pdLoading.dismiss();
            List<HomeData> data=new ArrayList<>();

            pdLoading.dismiss();

            try {

                JSONArray jsonArray = new JSONArray(result);

                for (int i=0;i<jsonArray.length();i++){
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    HomeData homeData = new HomeData();
                    homeData.teama = json_data.getString("teama");
                    homeData.jurta = json_data.getString("jurta");
                    homeData.resultta = json_data.optString("resultta");
                    homeData.category=json_data.optString("category");
                    homeData.idcat=json_data.optInt("idcat");

                    homeData.cabolData1 = cabolPosition ;

                    System.out.println(cabolPosition +"HEHE");
                    data.add(homeData);
                }

                RecyclerView recyclerView = getView().findViewById(R.id.rvResult);
                AdapterResultOlahraga mAdapter = new AdapterResultOlahraga(getActivity(), data);

                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

            } catch (JSONException e) {

                    Toast.makeText(getActivity(), "Menunggu hasil final lomba", Toast.LENGTH_LONG).show();
            }
        }
    }
}
