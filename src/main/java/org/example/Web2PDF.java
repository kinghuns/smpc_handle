package org.example;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
* @description: TODO
* @author: Jianjun
* @date: 2025/7/4 14:58
* @version: 1.0
*/
public class Web2PDF {
    private static final Logger LOG = LoggerFactory.getLogger(Web2PDF.class);

    public boolean Convert(String url, String outputPath)
    {
        LOG.info("正在获取网页内容...");
//        System.out.println("正在获取网页内容...");
        // 使用Jsoup获取网页内容
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();
        } catch (IOException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
//            return false;
        }
        // 转换为PDF
        LOG.info("正在转换为PDF...");
        try (OutputStream os = new FileOutputStream(outputPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withW3cDocument(new W3CDom().fromJsoup(doc), url);
            builder.toStream(os);
            builder.run();
        } catch (FileNotFoundException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
        LOG.info("PDF文件已保存到: {}", outputPath);
        return true;
    }


}
