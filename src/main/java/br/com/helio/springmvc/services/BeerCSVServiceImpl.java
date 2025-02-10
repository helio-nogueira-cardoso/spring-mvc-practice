package br.com.helio.springmvc.services;

import br.com.helio.springmvc.models.BeerCSVRecord;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
public class BeerCSVServiceImpl implements BeerCSVService {
    @Override
    public List<BeerCSVRecord> convertCsv(File csvFile) {

        try {
            return new CsvToBeanBuilder<BeerCSVRecord>(
                    new FileReader(csvFile)
            ).withType(BeerCSVRecord.class).build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
