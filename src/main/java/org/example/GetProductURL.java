package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;

public class GetProductURL {
    public static String URL_STR="https://www.medicines.org.uk";
    public static String DOWNLOAD_STR="https://www.medicines.org.uk/emc/files/pil.";
    public static String FILE_PATH="E:\\download\\";//文件保存地址
    public static Map<String,String> HEARD =  new HashMap<String,String>();
    public static  String[] StrArry={"0-9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    //public static  String[] StrArry={"Q"};
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {

        HEARD.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
        HEARD.put("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        GetProductURL test= new GetProductURL();
        //test.getOnePage("A");
        //test.getNextPage("/emc/ingredient/1388");
        test.start();

    }

    public void start() {
        for(String str : StrArry){
            System.out.println(str+ "  :开始 "+sdf.format(new Date()));
            List<String> oneList = this.getOnePage(str);
            for(String oneUrl: oneList){
                System.out.println(URL_STR + oneUrl + "?offset=0&limit=200");
                this.getNextPage(URL_STR + oneUrl + "?offset=0&limit=200");
            }
            System.out.println(str+ "  :结束 "+sdf.format(new Date()));




        }

    }

    public List<String> getOnePage(String de) {
        List<String> reList = new ArrayList<String>();
        String url=URL_STR+"/emc/browse-ingredients/"+de;
        try {
            Document doc = Jsoup.connect(url)
                    .headers(HEARD)
                    .get();
            //循环列表下载
            Elements resultsDiv = doc.getElementsByClass("browse-results");
            if (resultsDiv != null && resultsDiv.size() > 0) {
                Elements prodcutList = resultsDiv.get(0).getElementsByClass("emc-link");
                if (prodcutList != null && prodcutList.size() > 0) {
                    for (Element product : prodcutList) {
                        String nextUrl = product.attribute("href").getValue();
                        reList.add(nextUrl);
                        //System.out.println(nextUrl);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("搜索1级页面报错："+de);
            e.printStackTrace();
        }
        return reList;
    }

    public void getNextPage(String url){
        try{
            Document doc = Jsoup.connect(URL_STR+url)
                    .headers(HEARD)
                    .get();
            //循环列表下载
            Elements prodcutList=doc.getElementsByClass("search-results-product-info-title-link emc-link");
            if(prodcutList!=null && prodcutList.size()>0){
                for(Element product : prodcutList){///emc/product/305/smpc
                    String productUrl= product.attribute("href").getValue();
                    if(productUrl.indexOf("/smpc")>0){
                        String productName =product.html();
                        System.out.println(productName+" "+productUrl);
                    }


                }
            }
            //获取分页信息
            Elements paginates=doc.getElementsByClass("paginate");
            if(paginates!=null && paginates.size()>0){//存在分页
                Element paginate = paginates.first();//取第一个
                Elements paging=paginate.getElementsByClass("search-paging-next");

                if(paging!=null && paging.size()>0) {
                    Element nextPag = paging.first();
                    if(nextPag.attribute("class").getValue().trim().equals("search-paging-next")){
                        this.getNextPage(URL_STR+nextPag.attribute("href").getValue());
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
