package org.example;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVWriter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: TODO
 * @author: Jianjun Gao
 * {@code @date:} 2025/3/5 11:22
 * @version: 1.0
 */
public class Utility {
    private static final Logger LOG = LoggerFactory.getLogger(Utility.class);
//    private static final Logger logger = LogManager.getLogger(Main.class);
    public static String formatNow() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String changeExtName2PDF(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + ".pdf";
    }

    public static String changeExtName2Txt(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";
    }

    //返回目录下所有的文件名
    public static List<File> getAllFilesBypath(String path) {
        List<File> fileList = new ArrayList<>();

        File file = new File(path);		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中

        if (fs != null) {
            for(File f:fs){					//遍历File[]数组
                if(!f.isDirectory()) {//若非目录(即文件)，则打印
                    fileList.add(f);
//                    LOG.info(String.valueOf(f));
                }
            }
        }
        return fileList;
    }

    //返回首个数字的位置。已经增加了业务逻辑，将会移动到Leatlet的处理逻辑中。- 2025.03.08
    public static int findFirstIndexNumberOfStr(String str){
        int i = -1,j  = -1;
        String text = str;
        Matcher ruleYear = Pattern.compile("[0-9] Years").matcher(text);
        if(ruleYear.find()) {
            j = ruleYear.end();
            text = text.substring(j+1);
        }
        
        Matcher matcher = Pattern.compile("[0-9]").matcher(text);
        if(matcher.find()) {
            i = matcher.start() + j + 1;
        }
        return i;
    }

    //将dataList写到目标位置的CSV文件中
    public static boolean write2Csv(ArrayList dataList, String outputFile){
        try (
                CSVWriter writer = new CSVWriter(new FileWriter(outputFile));
        ) {
            // 批量写入
            writer.writeAll(dataList);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeToCSV(ArrayList<String[]> data, String filePath) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw);
             CSVWriter writer = new CSVWriter(bw,
                     CSVWriter.DEFAULT_SEPARATOR,
                     CSVWriter.NO_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END)) {

            // 写入所有数据
            writer.writeAll(data);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 不使用第三方库的实现版本
    public static boolean writeToCSVWithoutLib(ArrayList<String[]> data, String filePath) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                if (row != null) {
                    // 构建CSV行
                    StringBuilder line = new StringBuilder();
                    for (int i = 0; i < row.length; i++) {
                        // 处理null值
                        String value = row[i] == null ? "" : row[i];

                        // 如果值包含逗号、引号或换行符，则需要用引号包围
                        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                            value = "\"" + value.replace("\"", "\"\"") + "\"";
                        }

                        line.append(value);

                        // 如果不是最后一个元素，添加逗号
                        if (i < row.length - 1) {
                            line.append(",");
                        }
                    }
                    writer.write(line.toString());
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //去换行符号以及逗号，避免生成csv的时候出现错误
    public static String processString(String input) {
        if (input == null) {
            return null;
        }

        // 去除所有换行符（包括 \n 和 \r\n）并将逗号替换为空格
        return input.replaceAll("\\r?\\n", "")  // 去除换行符
                .replace(",", " ");           // 将逗号替换为空格
    }

    public static String readNextLineAfterKeyword(String content, String keyword) {
        if (content == null || keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            List<String> lines = new ArrayList<>();

            // 先将所有行读入列表中
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // 从后向前查找关键词
            for (int i = lines.size() - 1; i >= 0; i--) {
                if (lines.get(i).contains(keyword)) {
                    // 如果找到关键词且不是最后一行，返回下一行
                    if (i < lines.size() - 1) {
                        return lines.get(i + 1);
                    }
                    break;
                }
            }

            // 如果没有找到关键词或关键词在最后一行
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTxtContent(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readString(path);
    }

    public static String getIdFromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            LOG.error("文件名为空");
            return null;
        }

        // 匹配最后一个中文或英文括号中的数字
        Pattern pattern = Pattern.compile(".*[\\(（](\\d+)[\\)）][^()（）]*$");
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            try {
                return matcher.group(1);
            } catch (NumberFormatException e) {
                LOG.error("无法将括号中的内容转换为数字：{}", fileName);
                return null;
            }
        } else {
            LOG.debug("文件名中未找到括号内的数字：{}", fileName);
            return null;
        }
    }

    //检查并创建目录
    public static boolean createIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return directory.isDirectory();
    }

    public static boolean convertPDF2TXT(String pdfFilename, String outputFilename) {
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFilename));
            StringBuilder fullContent = new StringBuilder();

            int numberOfPages = pdfDoc.getNumberOfPages();

            // 遍历每一页，提取文本
            for (int i = 1; i <= numberOfPages; i++) {
                // 创建文本提取策略
                ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();

                // 提取当前页面的文本
                String pageContent = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i), strategy);
                fullContent.append(pageContent);
            }
            FileWriter writer;
            try {
                writer = new FileWriter(outputFilename);
                writer.write(fullContent.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
//                throw new RuntimeException(e);
                LOG.error(e.getMessage());
                pdfDoc.close();
                return false;
            }
            // 关闭 PDF 文档
            pdfDoc.close();
            return true;

        } catch (Exception e) {
            LOG.error(e.getMessage());
            LOG.error("Err file: {}", pdfFilename);
            return false;
        }
    }

    public static String getContentBetween2Words(String inputText, String word1, String word2) {
        if (inputText == null || word1 == null || word2 == null) {
            LOG.warn("输入参数不能为空");
            return "";
        }

        try {
            // 查找第一个单词
            Matcher firstWord = Pattern.compile(word1).matcher(inputText);
            if (!firstWord.find()) {
                LOG.warn("未找到第一个单词: {}", word1);
                return "";
            }
            int startPosition = firstWord.start();

            // 查找第二个单词的第一次出现位置（在第一个单词之后）
            Matcher secondWord = Pattern.compile(word2).matcher(inputText);
            if (!secondWord.find(startPosition)) {
                LOG.warn("在第一个单词之后未找到第二个单词: {}", word2);
                return "";
            }
            int endPosition = secondWord.start();

            // 返回两个单词之间的文本
            return inputText.substring(startPosition + word1.length(), endPosition).trim();

        } catch (Exception e) {
            LOG.error("处理文本时发生错误: {}", e.getMessage());
            return "";
        }
    }
}
