package org.example;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        String originPath = "C:\\WorkSpace\\data\\Medicine-Leaflet\\download\\";
        String[] StrArray= {"O","P","Q","R","S","T","U","V","W","X","Y","Z"};
                //{"0-9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        String url = "https://www.medicines.org.uk/emc/product/7969/smpc/print";

        //"C:\\WorkSpace\\Data\\Medicine-Leaflet\\smpc\\PDF\\A\\Abacavir 300mg Film-coated tablets（12475）.pdf"
        String sourcePath = "C:\\WorkSpace\\Data\\Medicine-Leaflet\\smpc\\PDF\\A\\Abacavir 300mg Film-coated tablets（12475）.pdf";
        String destPath = Utility.formatNow() + "pdf.txt";

        boolean b = Utility.convertPDF2TXT(sourcePath, destPath);

        Path path = Paths.get(destPath);
        String fullText = Files.readString(path);
        String fileName =  Paths.get(sourcePath).getFileName().toString();
        ExtractSMPCTxt(fullText, fileName);

//        Web2PDF web2pdf = new Web2PDF();
//        LOG.info("Starting....");
//        web2pdf.Convert(url, outputPath);
//        LOG.info("Finished....");

//        for (String preFix : StrArray) {
//            String filesPath = originPath + preFix + "\\";
//            LOG.info(filesPath);
//
//            List<File> fileList = new ArrayList<>();
//            fileList = Utility.getAllFilesBypath(filesPath);
//
//            String outputPath  = "C:\\WorkSpace\\Data\\Medicine-Leaflet\\smpc\\" + preFix + "\\";
//            if (Utility.createIfNotExists(outputPath))
//            {
//                GetSMPCByExistProductID(fileList, outputPath);
//            };
//        }

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

    public static void ExtractSMPCTxt(String fullText, String fileName) {
        //
        LOG.info("Extracting {} ...", fileName);
        LOG.info("getContentBetween2Words: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "1. Name of the medicinal product",
                "2. Qualitative and quantitative composition")));
        LOG.info("Get Next line: %s".formatted(Utility.readNextLineAfterKeyword(fullText, "1. Name of the medicinal product") ));

        //
        LOG.info("========== Begin ============");
        LOG.info("Manufacturer: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "\\|",
                "1. Name of the medicinal product")));
        LOG.info("ID: %s".formatted(Utility.getIdFromFileName(fileName) ));
        LOG.info("Common name: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "1. Name of the medicinal product",
                "2. Qualitative and quantitative composition")));
        LOG.info("Dosage form: %s".formatted(Utility.readNextLineAfterKeyword(
                fullText,
                "3. Pharmaceutical form")));
        LOG.info("Dose Frequency: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "4.2 Posology and method of administration",
                "4.3 Contraindications")));
        LOG.info("Date of first marketing authorization: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "9. Date of first authorisation/renewal of the authorisation",
                "10. Date of revision of the text")));
        LOG.info("Take More: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "4.9 Overdose",
                "5. Pharmacological properties")));
        LOG.info("Forget: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "4.2 Posology and method of administration",
                "4.3 Contraindications")));
        LOG.info("Stop: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "4.2 Posology and method of administration",
                "4.3 Contraindications")));
        LOG.info("whether these instructions were supported by evidence(SmPC）: %s".formatted(Utility.getContentBetween2Words(
                fullText,
                "5.1 Pharmacodynamic properties",
                "6. Pharmaceutical particulars")));


    }

}