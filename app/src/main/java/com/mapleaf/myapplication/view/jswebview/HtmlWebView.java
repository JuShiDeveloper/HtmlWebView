package com.mapleaf.myapplication.view.jswebview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * create time 2018/10/18
 *
 * @author jushi
 * 可设置占位图并且实现图片点击的加载html字符串的WebView
 */
public class HtmlWebView extends WebView {
    private final String UTF_8 = "UTF-8";
    private String imageListener = "imageListener";
    private String baseUrl = "file:///android_asset/";
    private String mineType = "text/html;charset=UTF-8";
    private String defaultImage;
    private String htmlText;

    public HtmlWebView(Context context) {
        super(context);
        initWebViewSet();
    }

    public HtmlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebViewSet();
    }

    public HtmlWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebViewSet();
    }

    private void initWebViewSet() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName(UTF_8);
        settings.setBlockNetworkImage(false);
    }

    /**
     * 设置占位图，在原图片加载之前显示
     *
     * @param assetsImageName assets目录下的图片文件 （如：xxx.png）
     */
    public void setPlaceholderImage(String assetsImageName) {
        this.defaultImage = assetsImageName;
    }

    /**
     * 设置点击图片的回调监听
     *
     * @param listener
     */
    public void setOnHtmlWebViewListener(OnHtmlWebViewListener listener) {
        setWebViewClient(new HtmlWebViewClient(listener));
        addJavascriptInterface(new HtmlJavascriptInterface(listener), imageListener);
    }

    /**
     * 加载数据
     *
     * @param htmlText html字符串
     */
    public void loadDataWithHtml(String htmlText) {
        this.htmlText = htmlText;
        loadDataWithBaseURL(baseUrl, HtmlUtils.getNewContent(htmlText, defaultImage), mineType, null, null);
    }

    /**
     * 获取当前html字符串中所有图片地址
     * 必须在调用loadDataWithHtml() 方法之后数组中才会有值，
     * 如果当前html页面没有图片，则数组的length为0。
     *
     * @return 装有当前html所有图片url的数组
     */
    public String[] getImageUrls() {
        return HtmlUtils.getImageUrls(htmlText);
    }

    /**
     * 获取当前html字符串中所有图片地址
     * 如果当前html页面没有图片，则数组的length为0。
     *
     * @param htmlText html字符串
     * @return 装有当前html所有图片url的数组
     */
    public String[] getImageUrls(String htmlText) {
        return HtmlUtils.getImageUrls(htmlText);
    }

    /***************************** 当前WebView Method End ***************************************/

    /***************************** 当前WebView的Client类 Start *********************************/

    private class HtmlWebViewClient extends WebViewClient {

        private OnHtmlWebViewListener listener;

        public HtmlWebViewClient(OnHtmlWebViewListener listener) {
            this.listener = listener;
        }

        @Override//调用外部浏览器打开超链接
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            if (listener != null) {
                listener.onPageStarted();
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            view.getSettings().setBlockNetworkImage(false);
            //判断WebView是否加载图片资源
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(false);
            }
            super.onPageFinished(view, url);
            replaceImageUrl(view);
            addImageClickListener(view);
            if (listener != null) {
                listener.onPageFinished();
            }
        }

        /**
         * 网页内容加载完成之后，将真实图片的值替换回src
         *
         * @param webView
         */
        private void replaceImageUrl(WebView webView) {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "         window." + imageListener + ".log(objs.length);" +
                    "for(var i=0;i<objs.length;i++)  {"
                    + "     if(objs[i].src.indexOf(\"" + defaultImage + "\")<0) {"
                    + "     } else {"
                    + "         objs[i].src = objs[i].alt;" +
                    "} } })()");
        }

        /**
         * 设置图片的点击监听
         *
         * @param webView
         */
        private void addImageClickListener(WebView webView) {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  {" +
                    "    objs[i].onclick=function()  { " +
                    "           window." + imageListener + ".openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                    "    } } })()");
        }
    }
    /***************************** 当前WebView的Client类 End *********************************/

    /***************************** 当前WebView的JavascriptInterface类 Start *****************/

    private class HtmlJavascriptInterface {
        private OnHtmlWebViewListener listener;

        public HtmlJavascriptInterface(OnHtmlWebViewListener listener) {
            this.listener = listener;
        }

        /**
         * 点击图片时通过js回调
         *
         * @param imageUrl 被点击图片的url
         */
        @JavascriptInterface
        public void openImage(String imageUrl) {
            String[] imageUrls = getImageUrls();
            for (int i = 0; i < imageUrls.length; i++) {
                if (imageUrl.equals(imageUrls[i])) {
                    if (listener != null && imageUrl.contains("http")) {
                        listener.onClickImage(imageUrl, i);
                    } else {
                        listener.onClickImage("image load failed", i);
                    }
                }
            }
        }

        @JavascriptInterface //测试使用
        public void log(String arrayLength) {
//            Log.v("htmlWebView","arrayLength = "+arrayLength);
        }
    }

    /***************************** 当前WebView的JavascriptInterface类 End *****************/

    public interface OnHtmlWebViewListener {

        void onPageStarted();

        void onPageFinished();

        /**
         * 图片加载成功后点击图片回调的方法
         *
         * @param imageUrl 图片加载成功后传入被点击的图片网址，未加载成功时传入参数为 image load filed
         * @param position 当前被点击的图片地址在数组中的位置
         */
        void onClickImage(String imageUrl, int position);
    }

    private static class HtmlUtils {
        private static final String PATTERN_MODE = "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n" +
                "\n\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|" +
                "\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>";

        /**
         * 获取当前html字符串中的所有图片url
         *
         * @param htmlText html字符串
         * @return imageUrl数组
         */
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
                return imageUrls.toArray(new String[0]);
            }
            return imageUrls.toArray(new String[imageUrls.size()]);
        }

        /**
         * 获取替换html字符串中的图片地址
         *
         * @param htmltext         html字符串
         * @param placeholderImage 占位图名称
         * @return 替换了图片地址的html字符串（图片显示为占位图）
         */
        public static String getNewContent(String htmltext, String placeholderImage) {
            //jsoup解析html数据
            Document doc = Jsoup.parse(htmltext);
            Elements elements = doc.getElementsByTag("img");
            for (Element element : elements) {
                //没有加载css，所以会出现图片很大导致可以左右滑动。这里将所有图片的宽度设为100%，解决这个问题
                element.attr("width", "100%").attr("height", "auto");
                //将src的地址放入alt，可以替换成其他的属性
                //但是注意不能放到自定义的如_src中，会导致js代码中this._src取到undefine
                //我不懂js，所以只能放在原有的属性中
                element.attr("alt", element.attr("src"));
                //将src的值替换为assets文件夹下loading_default_image.png
                element.attr("src", placeholderImage);
            }
            return doc.toString();
        }
    }
}
