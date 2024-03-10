package com.example.mosawebapp.fileuploadservice;

import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.domain.ThreadTypeRepository;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {
  @Autowired
  private final ThreadTypeRepository threadTypeRepository;

  public FileUploadService(ThreadTypeRepository threadTypeRepository) {
    this.threadTypeRepository = threadTypeRepository;
  }

  public static boolean isFileValid(MultipartFile file){
    return Objects.equals(file.getContentType(),"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
        Objects.equals(file.getContentType(), "text/csv");
  }

  public static List<Brand> getBrandsFromFile(InputStream inputStream){
    List<Brand> brands = new ArrayList<>();

    XSSFWorkbook workbook = null;

    try{
      workbook = new XSSFWorkbook(inputStream);
      XSSFSheet sheet = workbook.getSheet("Brands");

      int rowIndex = 0;
      for(Row row: sheet){
        if(rowIndex == 0){
          rowIndex++;
          continue;
        }

        Iterator<Cell> cellIterator = row.iterator();
        int cellIndex = 0;
        Brand brand = new Brand();

        while(cellIterator.hasNext()){
          Cell cell = cellIterator.next();

          if (cellIndex == 0) {
            brand.setName(cell.getStringCellValue());
          }
          cellIndex++;
        }
        brands.add(brand);
      }
    } catch (NullPointerException ne){
      throw new NullPointerException(ne.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    return brands;
  }

  public List<ThreadTypeDetails> getThreadTypeDetailsFromFile(InputStream inputStream){
    List<ThreadTypeDetails> detailsList = new ArrayList<>();

    XSSFWorkbook workbook = null;

    try{
      workbook = new XSSFWorkbook(inputStream);
      XSSFSheet sheet = workbook.getSheet("Details");

      int rowIndex = 0;
      for(Row row: sheet){
        if(rowIndex == 0){
          rowIndex++;
          continue;
        }

        Iterator<Cell> cellIterator = row.iterator();
        int cellIndex = 0;
        ThreadTypeDetails details = new ThreadTypeDetails();

        while(cellIterator.hasNext()){
          Cell cell = cellIterator.next();

          switch(cellIndex){
            case 0 -> details.setThreadType(validateThreadType(cell.getStringCellValue()));
            case 1 -> details.setWidth(cell.getStringCellValue());
            case 2 -> details.setAspectRatio(cell.getStringCellValue());
            case 3 -> details.setDiameter(cell.getStringCellValue());
            case 4 -> details.setPrice((long) cell.getNumericCellValue());
            case 5 -> details.setStocks((long) cell.getNumericCellValue());
            default -> {}
          }
          cellIndex++;
        }
        detailsList.add(details);
      }
    } catch (NullPointerException ne){
      throw new NullPointerException(ne.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    return detailsList;
  }

  public ThreadType validateThreadType(String type){
    ThreadType threadType = threadTypeRepository.findByTypeIgnoreCase(type);

    if(threadType == null){
      threadType = threadTypeRepository.findById(type).orElseThrow(() -> new NotFoundException("Thread Type does not exists"));
    }

    return threadType;
  }
}
