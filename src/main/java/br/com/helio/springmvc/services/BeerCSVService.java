package br.com.helio.springmvc.services;

import br.com.helio.springmvc.models.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCSVService {
    List<BeerCSVRecord> convertCsv(File csvFile);
}
