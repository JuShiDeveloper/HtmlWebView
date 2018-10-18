## 加载html字符串的WebView
### 特性：
* 1、显示图片的位置添加占位图。
* 2、图片点击事件
* 3、可做普通WebView使用

### 使用
* 设置占位图
    > htmlWebView.setPlaceholderImage("loading_default_image.png");
* 设置Listener
    > htmlWebView.setOnHtmlWebViewListener(new HtmlWebView.OnHtmlWebViewListener())
* 设置加载html字符串
    > htmlWebView.loadDataWithHtml(htmlText);

* 获取当前html页面的所有图片地址 (可选)
    > htmlWebView.getImageUrls(); 此方法需要在loadDataWithHtml调用之后才能获取到图片地址

    > htmlWebView.getImageUrls(htmlText);


