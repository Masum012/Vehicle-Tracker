package com.example.abdulazizsorkar.devicehandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by Abdul Aziz Sorkar on 1/16/2016.
 */

public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://abdulazizsorkar.netau.net/";

    public ServerRequests(Context context){
       //progressDialog = new ProgressDialog(context);
       //progressDialog.setCancelable(false);
       //progressDialog.setTitle("Progressing");
       //progressDialog.setMessage("Please wait...");
   }

    public void storeUserDataInBackground(VehicleLocation location,GetUserCallback locationCallBack){
//        progressDialog.show();
        new StoreUserDataAsyncTask(location,locationCallBack).execute();
    }

    /*public void fetchUserDataInBackground(VehicleLocation location,GetUserCallback callBack){
        progressDialog.show();
        new FetchUserDataAsyncTask(location,callBack).execute();
    }*/

    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void,Void>{
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
            dataToSend.add(new BasicNameValuePair("dateTime",location.dateTime));
           /* dataToSend.add(new BasicNameValuePair("username", user.userName));
            dataToSend.add(new BasicNameValuePair("password", user.password));*/

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
           // progressDialog.dismiss();
            locationCallback.done(null);

            super.onPostExecute(aVoid);
        }
    }


  /*  public class FetchUserDataAsyncTask extends AsyncTask<Void,Void,VehicleLocation> {
        VehicleLocation location;
        GetUserCallback locationCallback;

        public FetchUserDataAsyncTask(VehicleLocation location, GetUserCallback locationCallback) {
            this.location = location;
            this.locationCallback = locationCallback;
        }

        @Override
        protected VehicleLocation doInBackground(Void... voids) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("latitide", location.latitide+""));
            dataToSend.add(new BasicNameValuePair("longitude", location.longitude+""));

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
                    double latitide = jObject.getDouble("latitide");
                    double longitude = jObject.getDouble("longitude");

                    returnedLocation = new VehicleLocation(latitide,longitude);
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
    }*/
}
