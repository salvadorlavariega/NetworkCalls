package co.mobilemakers.networkcalls;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * A placeholder fragment containing a simple view.
 */
public class NflNewsFragment extends Fragment {


    TextView textViewNews;
    public NflNewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_network_call, container, false);

        textViewNews = (TextView)rootView.findViewById(R.id.text_view_nfl_news);
        Button buttonGetNews =(Button)rootView.findViewById(R.id.button_get_news);
        buttonGetNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = "Getting NFL News 3";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                new FetchNewsTask().execute();
            }
        });
        return rootView;
    }

    private URL constructURLQuery() throws MalformedURLException {
        final String NFL_BASE_URL = "api.fantasy.nfl.com";
        final String _PATH= "players";
        final String _ENPOINT = "news";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority(NFL_BASE_URL)
                .appendPath(_PATH)
                .appendPath(_ENPOINT);

        Uri uri = builder.build();
        Log.d(MainActivity.LOG_TAG, "Build URI:" + uri.toString());
        return  new URL(uri.toString());
    }



    private String parseResponse(InputStream response) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(response);
        String names="";
        NodeList nodeList = doc.getDocumentElement().getChildNodes();
        Log.e(MainActivity.LOG_TAG, "Size nodeList  :" + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Log.d(MainActivity.LOG_TAG, "getNodeType= :" + node.getNodeType() );
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if(elem!=null)
                Log.d(MainActivity.LOG_TAG, "XML parse Value= :" +  elem.getAttribute("firstName"));
                names+=elem.getAttribute("firstName")+" \n";
            }

        }
        return names;
    }

    class FetchNewsTask extends AsyncTask<String ,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String  names="";

            try {
                URL url = constructURLQuery();
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                try{

                    names = parseResponse(httpURLConnection.getInputStream());

                }  catch (IOException e) {
                   // Toast.makeText(getActivity(), "FAIL!",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } finally {
                    httpURLConnection.disconnect();
                }
            }  catch (IOException e) {
                Toast.makeText(getActivity(), "FAIL!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return names;
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            textViewNews.setText(response);
        }
    }
}
