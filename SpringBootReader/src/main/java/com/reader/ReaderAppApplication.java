package com.reader;

import com.monitorjbl.xlsx.StreamingReader;
import com.reader.entity.Customer;
import com.reader.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class ReaderAppApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReaderAppApplication.class, args);
    }

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {

        long startTimeRead = System.currentTimeMillis();
        log.info("-> Reading file");
        InputStream is = new FileInputStream("../customers.xlsx");
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(500000)
                .bufferSize(131072)
                .open(is);

//        List<Customer> customers = new ArrayList<>();
//		for (Sheet sheet : workbook){
//            boolean isHeader = true;
//            for (Row row : sheet) {
//                if (isHeader) {
//                    isHeader = false;
//                    continue;
//                }
//                Customer customer = new Customer();
//                customer.setId((long) row.getCell(0).getNumericCellValue());
//                customer.setName(row.getCell(1).getStringCellValue());
//                customer.setLastName(row.getCell(2).getStringCellValue());
//                customer.setAddress(row.getCell(3).getStringCellValue());
//                customer.setEmail(row.getCell(4).getStringCellValue());
//                customers.add(customer);
//            }
//		}


        List<Customer> customers = StreamSupport.stream(workbook.spliterator(), false)
                .flatMap(sheet -> StreamSupport.stream(sheet.spliterator(), false)
                        .skip(1)
                        .map(con -> {
                            Customer customer = new Customer();
                            customer.setId((long) con.getCell(0).getNumericCellValue());
                            customer.setName(con.getCell(1).getStringCellValue());
                            customer.setLastName(con.getCell(2).getStringCellValue());
                            customer.setAddress(con.getCell(3).getStringCellValue());
                            customer.setEmail(con.getCell(4).getStringCellValue());
                            return customer;
                        }))
                .collect(Collectors.toList());

        long endTimeRead = System.currentTimeMillis();
        log.info("-> Reading finished, time " + (endTimeRead - startTimeRead) + " ms");

        log.info("-> Inserting");



        long startTimeWrite = System.currentTimeMillis();
        customerRepository.saveAll(customers);
        long endTimeWrite = System.currentTimeMillis();
        log.info("-> Write finished, time " + (endTimeWrite - startTimeWrite) + " ms");
    }

    public synchronized void insertData(List<Customer> customers) {
        this.customerRepository.saveAll(customers);
    }
}
