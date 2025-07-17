package org.example;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String originPath = "C:\\WorkSpace\\data\\Medicine-Leaflet\\download\\";
        String[] StrArray= {"O","P","Q","R","S","T","U","V","W","X","Y","Z"};
                //{"0-9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        String url = "https://www.medicines.org.uk/emc/product/7969/smpc/print";

//        Web2PDF web2pdf = new Web2PDF();
//        LOG.info("Starting....");
//        web2pdf.Convert(url, outputPath);
//        LOG.info("Finished....");
        for (String preFix : StrArray) {
            String filesPath = originPath + preFix + "\\";
            LOG.info(filesPath);

            List<File> fileList = new ArrayList<>();
            fileList = Utility.getAllFilesBypath(filesPath);

            String outputPath  = "C:\\WorkSpace\\Data\\Medicine-Leaflet\\smpc\\" + preFix + "\\";
            if (Utility.createIfNotExists(outputPath))
            {
                GetSMPCByExistProductID(fileList, outputPath);
            };
        }
    }

    public static void GetSMPCByExistProductID(List<File> fileList, String outputPath){
        String fileName, productId, productUrl , outputFilename= "";
        Web2PDF web2pdf = new Web2PDF();
//        LOG.info("Starting....");
//        web2pdf.Convert(url, outputPath);
//        LOG.info("Finished....");
        for (File file : fileList) {
//            System.out.println(file.getAbsolutePath());
            fileName = file.getName();
            productId = Utility.getIdFromFileName(fileName);
            productUrl = "https://www.medicines.org.uk/emc/product/" + productId + "/smpc/print";
            LOG.info("{}\t{}", fileName, productId);
            LOG.info(productUrl);
            outputFilename = outputPath +  Utility.changeExtName2PDF(fileName);;
            LOG.info(outputFilename);
            LOG.info("Starting convert {}....", productUrl);
            try {
                web2pdf.Convert(productUrl, outputFilename);
                LOG.info("Finished.... {} already converted ", outputFilename);
            }
            catch (Exception e) {
                LOG.error("An error has occurred, {} was not converted. {} ", outputFilename, e.getMessage());
            }
        }
        LOG.info("Total files: {} " ,fileList.size());
    }
}