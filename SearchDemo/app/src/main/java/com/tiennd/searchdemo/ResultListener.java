package com.tiennd.searchdemo;

import com.tiennd.searchdemo.model.Product;

import java.util.List;

/**
 * Created by nguyentien on 12/19/17.
 */

public interface ResultListener {
   void onComplete(List<Product> productList);
}
