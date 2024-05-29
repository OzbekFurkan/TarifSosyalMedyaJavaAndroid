package com.example.recipeapp.feature;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetPriceAndSeller extends AsyncTask<Ingredient, Void, Task<Void>> {

    @Override
    protected Task<Void> doInBackground(Ingredient... strings) {
        String url = strings[0].price_link;
        String price="";
        String seller="";
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select(".OfferCard_firstOffer__GUZ2a");
            for(Element e:elements)
            {
                price = e.select(".OfferCard_unitPrice__L2YHY").text();
                Element element = e.select(".OfferCard_imageWrapper__2pHo6 > div > img").first();
                seller = element.attr("alt");
            }
            strings[0].price=price;
            strings[0].seller=seller;
            Log.d("ben", "fiyat: "+price);
            Log.d("ben", "satıcı: "+seller);

        }catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
        return Tasks.forResult(null);
    }
}
