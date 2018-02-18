package id.pekma.pekmavii.FragmentHome;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.RootElement;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import id.pekma.pekmavii.FragmentNews.AdapterNews;
import id.pekma.pekmavii.FragmentNews.NewsData;
import id.pekma.pekmavii.FragmentNews.NewsDataHome;
import id.pekma.pekmavii.FragmentNews.NewsFragment;
import id.pekma.pekmavii.MainActivity;
import id.pekma.pekmavii.R;

/**
 * Created by Muhammad Taqi on 2/13/2018.
 */

public class HomeFragment extends Fragment implements NewsFragment.SendMessage{
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    TextView mnewst;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView mnewsiv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview =inflater.inflate(R.layout.fragment_home, container,false);

        View bottomSheet = rootview.findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        final LinearLayout linearLayout1 = rootview.findViewById(R.id.line1);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior1.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
                else {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });


        MainActivity mainActivity = (MainActivity)getActivity();
        String myDataFromActivity = mainActivity.getMyData();
        String myIvDataFromActivity = mainActivity.getMyIvData();
        mnewst = rootview.findViewById(R.id.homenewsdesctxt);
        mnewst = rootview.findViewById(R.id.homenewsdesctxt);
        mnewsiv = rootview.findViewById(R.id.ivHomeNews);
        Picasso.with(getContext())
                .load(myIvDataFromActivity)
                .into(mnewsiv);
        mnewst.setText(myDataFromActivity);
        return rootview;


    }
    protected void displayReceivedData(String message) {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {



        if (getArguments()!=null) {
            Bundle bundle = getArguments();
            String mnews1 = bundle.getString("txtnews");
            new AsyncFetch3().execute();
        }
        super.onCreate(savedInstanceState);
        new AsyncFetch3().execute();

    }


    @Override
    public void sendNewsData(String message) {
        Log.e("hello",message);

    }


    public class AsyncFetch3 extends AsyncTask<String,String,String> {
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

                url = new URL("https://taqiabdulaziz.com/news.php");

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
            //this method will be running on UI thread

            pdLoading.dismiss();
            List<HomeData> data=new ArrayList<>();

            pdLoading.dismiss();

            try {

                JSONArray jsonArray = new JSONArray(result);

                for (int i=0;i<jsonArray.length();i++){
                JSONObject json_data = jsonArray.getJSONObject(i);
                HomeData homeData = new HomeData();
                homeData.playerA = json_data.getString("playera");
                homeData.playerB = json_data.getString("playerb");

                data.add(homeData);

                }

                RecyclerView recyclerView = getView().findViewById(R.id.rvHome);
                AdapterHome mAdapter = new AdapterHome(getActivity(), data);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

            } catch (JSONException e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }

        }
    }


}
