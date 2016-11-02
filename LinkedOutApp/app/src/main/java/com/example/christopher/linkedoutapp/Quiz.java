package com.example.christopher.linkedoutapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Quiz extends AppCompatActivity {

    private TextView textView;
    private RadioButton rb;
    private RadioGroup rg;

    private class HTTPAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection serverConnection = null;
            InputStream is = null;

            Log.d("Debug:", "Attempting to connect to: " + params[0]);

            try {
                URL url = new URL(params[0]);
                serverConnection = (HttpURLConnection) url.openConnection();
                serverConnection.setRequestMethod(params[1]);
                if (params[1].equals("POST") ||
                        params[1].equals("PUT") ||
                        params[1].equals("DELETE")) {
                    Log.d("DEBUG POST/PUT/DELETE:", "In post: params[0]=" + params[0] + ", params[1]=" + params[1] + ", params[2]=" + params[2]);
                    serverConnection.setDoOutput(true);
                    serverConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    // params[2] contains the JSON String to send, make sure we send the
                    // content length to be the json string length
                    serverConnection.setRequestProperty("Content-Length", "" +
                            Integer.toString(params[2].toString().getBytes().length));

                    // Send POST data that was provided.
                    DataOutputStream out = new DataOutputStream(serverConnection.getOutputStream());
                    out.writeBytes(params[2].toString());
                    out.flush();
                    out.close();
                }

                int responseCode = serverConnection.getResponseCode();
                Log.d("Debug:", "\nSending " + params[1] + " request to URL : " + params[0]);
                Log.d("Debug: ", "Response Code : " + responseCode);

                is = serverConnection.getInputStream();

                if (params[1] == "GET" || params[1] == "POST" || params[1] == "PUT" || params[1] == "DELETE") {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    try {
                        JSONObject jsonData = new JSONObject(sb.toString()); //json object
                        return jsonData.toString();                          //returns json data
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serverConnection.disconnect();
            }

            return "Should not get to this if the data has been sent/received correctly!";
        }

        /**
         * The following executes after the Asynchronous task is finished executing
         *
         * @param result the result from the query
         */
        protected void onPostExecute(String result) {

            //this whole section is basically taking in the jsondata and setting to different buttons to be displayed. Hopefully dynamically.
            JSONObject jsonData = new JSONObject(); //holds the json data pulled from server

            try {
                //This gets the hardcoded quiz question of type "math"
                jsonData = new JSONObject(new JSONObject(result).getString("math"));

                //will grab the format as a string
                String quizFormat = jsonData.getString("quizFormat");

                //This grabs the hardcoded question and puts it into the XML this is the radio group
                String quizQuestion = jsonData.getString("quizQuestion");
                textView.setText(quizQuestion);

                //This grabs the hardcoded question and puts it into the XML this is the radio group
                //RadioGroup radioGroup = jsonData.getString("quizQuestion");
                //textView.setText(quizQuestion);

                //this  sectionfinds out how the question should be formatted, and connects the
                //correct strings to the radio buttons

                if(quizFormat == "True/False"){
                    isTrueFalse(jsonData);

                }
                else if(quizFormat == "Mulitple Choice"){
                    isMultipleChoice(jsonData);
                }
                else if(quizFormat == "Short Answer"){
                    //create a textbox input

                }


                /*
                //Generates a random number to determine the choice (A, B, C, or D) of the answer
                Random j = new Random();
                int answer = 1+j.nextInt(4);
                String radio = "radioButton" + answer;
                //sets the answer to that radio button that was selected above
                int resID = getResources().getIdentifier(radio, "id", getPackageName());
                RadioButton b = (RadioButton)findViewById(resID);
                b.setText(jsonData.getString("answer"));
                //Sets the remaining unused radio buttons to random numbers from 1 to 10
                for(int i = 1; i <= 4; i++){
                    if(i!=answer){
                        resID = getResources().getIdentifier("radioButton"+i, "id", getPackageName());
                        int response = 1+j.nextInt(10);
                        while(jsonData.getString("answer").equals(""+response)){
                            response = 1+j.nextInt(10);
                        }
                        b = ((RadioButton)findViewById(resID));
                        //For some reason the setText doesn't work with pure integers, so that's why I'm adding empty string
                        b.setText("" + response);

                    }
                }

                */
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /** This function will find out which radio button was clicked
         * @Param: View v
         */
        public void rbClick(View view){
            int radioButtonid = rg.getCheckedRadioButtonId();
            rb = (RadioButton) findViewById(radioButtonid);
        }


    }//****************End AsyncTask*****************
    /**
     * Acts as the onClick callback for the REST GET Button. The code will generate a REST GET
     * action to the REST Server.
     */
    public void restGET() {
        new HTTPAsyncTask().execute("http://192.168.0.22:4321/quizData", "GET");
    }


    /**
     * Acts as the onClick callback for the REST POST Button. The code will generate a REST POST
     * action to the REST Server.
     *
     * @param view
     */
    public void restPOST(View view) {

        JSONObject jsonParam = null;
        try {
            //Create JSONObject here
            jsonParam = new JSONObject();
            jsonParam.put("answer", rg.getCheckedRadioButtonId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DEBUG:", jsonParam.toString());
        new HTTPAsyncTask().execute("http://192.168.0.22:4321/quizData", "POST", jsonParam.toString());
    }

    /**This function creates a true/false question format
     * @param: String quizFormat
     *
     */
    void isTrueFalse(JSONObject jsonData){
        //create two buttons one for true and one for false
        String quizAnswerChoiceOne = null;
        try {
            quizAnswerChoiceOne = jsonData.getString("quizAnswerChoiceOne");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(quizAnswerChoiceOne);

        String quizAnswerChoiceTwo = null;
        try {
            quizAnswerChoiceTwo = jsonData.getString("quizAnswerChoiceTwo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(quizAnswerChoiceTwo);
    }

    /**This function creates a multiple choice question format
     * @param: String quizFormat
     *
     *
     */
    void isMultipleChoice(JSONObject jsonData){
        //create four buttons
        String quizAnswerChoiceOne = null;
        try {
            quizAnswerChoiceOne = jsonData.getString("quizAnswerChoiceOne");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //RadioButton buttonAnswerChoiceOne = (RadioButton)findViewById();
        textView.setText(quizAnswerChoiceOne);

        String quizAnswerChoiceTwo = null;
        try {
            quizAnswerChoiceTwo = jsonData.getString("quizAnswerChoiceTwo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(quizAnswerChoiceTwo);

        String quizAnswerChoiceThree = null;
        try {
            quizAnswerChoiceThree = jsonData.getString("quizAnswerChoiceThree");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(quizAnswerChoiceThree);

        String quizAnswerChoiceFour = null;
        try {
            quizAnswerChoiceFour = jsonData.getString("quizAnswerChoiceFour");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(quizAnswerChoiceFour);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        textView = (TextView) findViewById(R.id.quizQuestion);
        restGET();

        //rg = (RadioGroup) findViewById(R.id.rGroup);
    }
}
