package money.mezu.mezu;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelReader {

    private static final int NUMBER_OF_CATEGORIES = 5;

    static Map<String, Category> initDictionary(String pathToExcelFile){
        //NUMBER_OF_CATEGORIES = Category.values().length-1;
        Map<String, Category> dict = new HashMap<>();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(pathToExcelFile));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            int rows = sheet.getPhysicalNumberOfRows(); // Number of rows
            HSSFRow titles = sheet.getRow(0);
            for (int i=0; i<NUMBER_OF_CATEGORIES; i++){
                Category category = Category.getCategoryFromString(stringFromCell(titles.getCell(i)));
                for(int j = 1; j < rows; j++) {
                    row = sheet.getRow(j);
                    if (row!=null){
                        String word = stringFromCell(row.getCell(i));
                        if (!word.isEmpty()){
                            dict.put(word, category);
                        }
                    }
                }
            }
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
        return dict;
    }


    private static String stringFromCell(HSSFCell cell){
        String str = cell.getStringCellValue();
        str = str.trim();
        //str = str.toLowerCase();
        return str;
    }



}
