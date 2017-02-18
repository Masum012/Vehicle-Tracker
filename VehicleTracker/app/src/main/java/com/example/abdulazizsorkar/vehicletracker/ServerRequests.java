package com.example.abdulazizsorkar.vehicletracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.widget.Toast.*;


/**
 * Created by Abdul Aziz Sorkar on 1/16/2016.
 */

public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static int counter = 0;
    public static final String SERVER_ADDRESS = "http://abdulazizsorkar.netau.net/";

    public ServerRequests(Context context){
       progressDialog = new ProgressDialog(context);
       progressDialog.setCancelable(false);
       progressDialog.setTitle("Progressing");
       progressDialog.setMessage("Please wait...");
   }

    /*public void storeUserDataInBackground(VehicleLocation location,GetUserCallback locationCallBack){
        progressDialog.show();

        new StoreUserDataAsyncTask(location,locationCallBack).execute();

    }*/

    public void fetchUserDataInBackground(GetUserCallback callBack){
        progressDialog.show();
        new FetchUserDataAsyncTask(callBack).execute();
    }

    /*public class StoreUserDataAsyncTask extends AsyncTask<Void,Void,Void> {
        VehicleLocation location;
        GetUserCallback locationCallback;

        public StoreUserDataAsyncTask(VehicleLocation location, GetUserCallback locationCallback) {
            this.location = location;
            this.locationCallback = locationCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("latitude",location.latitide + ""));
            dataToSend.add(new BasicNameValuePair("longitude", location.longitude + ""));
           *//* dataToSend.add(new BasicNameValuePair("username", user.userName));
            dataToSend.add(new BasicNameValuePair("password", user.password));*//*

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+"VehicleLocation.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            locationCallback.done(null);

            super.onPostExecute(aVoid);
        }
    }*/


    public class FetchUserDataAsyncTask extends AsyncTask<Void,Void,VehicleLocation> {

        GetUserCallback locationCallback;

        public FetchUserDataAsyncTask(GetUserCallback locationCallback) {
            this.locationCallback = locationCallback;
        }

        @Override
        protected VehicleLocation doInBackground(Void... voids) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            //dataToSend.add(new BasicNameValuePair("latitide", location.latitide+""));
            //dataToSend.add(new BasicNameValuePair("longitude", location.longitude+""));

            dataToSend.add(new BasicNameValuePair("counter", counter + ""));

            HttpParams httpRequestParams =  new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+"FetchLocationData.php");

            VehicleLocation returnedLocation = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                if(jObject.length()==0)
                    return returnedLocation;
                else{

                    double latitude = jObject.getDouble("latitude");
                    double longitude = jObject.getDouble("longitude");
                    String dateTime = jObject.getString("dateTime");

                    returnedLocation = new VehicleLocation(latitude,longitude,dateTime);
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedLocation;
        }

        @Override
        protected void onPostExecute(VehicleLocation returnedLocation) {
            progressDialog.dismiss();
            locationCallback.done(returnedLocation);
            super.onPostExecute(returnedLocation);
        }
    }

}
