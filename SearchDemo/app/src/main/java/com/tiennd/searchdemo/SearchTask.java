package com.tiennd.searchdemo;

import android.os.AsyncTask;
import android.util.Log;

import com.tiennd.searchdemo.model.Product;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyentien on 12/19/17.
 */

public class SearchTask extends AsyncTask<String, String, List<Product>> {
    private static final String AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Mobile Safari/537.36";
    private static final String LINK = "https://touch.vatgia.com/ajax_v2/load_quick_search_result.php";
    private static final String TAG = "SearchTask";
    private ResultListener resultListener;

    public SearchTask(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected List<Product> doInBackground(String... keyword) {
        List<Product> productList = new ArrayList<Product>();
        try {
            Document doc = Jsoup.connect(LINK)
                    .userAgent(AGENT)
                    .data("keyword", keyword[0])
                    .data("page", keyword[1])
                    .method(Connection.Method.POST)
                    .timeout(0)
                    .get();

            Elements elements = doc.getElementsByAttributeValue("class", "new_product_item_standard ");
            Elements elementsThumnail = doc.getElementsByAttributeValue("class", "new_product_item_picture ");
            Elements elementsLink = doc.select("a[href]");


            Log.i(TAG, "SIZE 1 elements= " + elements.size());
            Log.i(TAG, "SIZE 2 elementsThumnail= " + elementsThumnail.size());
            Log.i(TAG, "SIZE 3 elementsLink= " + elementsLink.size());

            for (int i = 0; i < elements.size(); i++) {
                String thumbnail = "";


                String title = "" + elements.get(i).select("div > div").first().text();
                String price = "" + elements.get(i).select("div > div > div[class = price]").first().text();
                if (i < elementsThumnail.size())
                thumbnail = "https:" + elementsThumnail.get(i).select("span > img").attr("src");
                String link = "https://touch.vatgia.com" + elementsLink.get(i).attr("href");
                productList.add(new Product(link,thumbnail,title, price));
            }
            return productList;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Product> productList) {
        super.onPostExecute(productList);
        resultListener.onComplete(productList);
    }
}
