package com.mapleaf.myapplication.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImagesUtils {

    private static final String PATTERN_MODE = "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n" +
            "\n\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|" +
            "\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>";

    public static String[] getImageUrls(String htmlText) {
        List<String> imageUrls = new ArrayList<>();
        Pattern p = Pattern.compile(PATTERN_MODE, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlText);
        String quote;
        String src;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageUrls.add(src);
        }
        if (imageUrls.size() == 0) {
            Log.e("imageSrcList","资讯中未匹配到图片链接");
            return null;
        }
        return imageUrls.toArray(new String[imageUrls.size()]);
    }

    public static String getNewContent(String htmltext) {
        //jsoup解析body数据
        Document doc = Jsoup.parse(htmltext);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            //没有加载css，所以会出现图片很大导致可以左右滑动。这里将所有图片的宽度设为100%，解决这个问题
            element.attr("width", "100%").attr("height", "auto");
            //将src的地址放入alt，可以替换成其他的属性
            //但是注意不能放到自定义的如_src中，会导致js代码中this._src取到undefine
            //我不懂js，所以只能放在原有的属性中
            element.attr("alt", element.attr("src"));
            //将src的值替换为assets文件夹下loading_image_default.png
            element.attr("src", "loading_default_image.png");
        }
        return doc.toString();
    }
}
