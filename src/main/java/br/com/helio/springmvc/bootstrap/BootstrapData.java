package br.com.helio.springmvc.bootstrap;

import br.com.helio.springmvc.entities.Beer;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.models.BeerCSVRecord;
import br.com.helio.springmvc.models.BeerStyle;
import br.com.helio.springmvc.repositories.BeerRepository;
import br.com.helio.springmvc.repositories.CustomerRepository;
import br.com.helio.springmvc.services.BeerCSVService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final BeerRepository beerRepository;
    private final BeerCSVService beerCSVService;

    private final List<UUID> registeredCustomerIds = new ArrayList<>();

    private void addNewCustomer(Customer.CustomerBuilder newCustomerBuilder) {
        newCustomerBuilder.createdDate(LocalDateTime.now());
        newCustomerBuilder.lastModifiedDate(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(newCustomerBuilder.build());
        registeredCustomerIds.add(savedCustomer.getId());
        log.debug(" ## Adding bootstrap customer | id = {}", savedCustomer.getId());
    }

    @Override
    public void run(String... args) throws FileNotFoundException {
        addNewCustomer(Customer.builder().name("Customer 1").email("customer1@mail.com"));
        addNewCustomer(Customer.builder().name("Customer 2").email("customer2@mail.com"));
        addNewCustomer(Customer.builder().name("Customer 3 lorem").email("customer3@mail.com"));
        addNewCustomer(Customer.builder().name("Customer 4").email("customer4@mail.com"));
        addNewCustomer(Customer.builder().name("Customer 5 lorem ipsum").email("customer5@mail.com"));

        if (beerRepository.count() < 10) {
            File beerCsv = ResourceUtils.getFile("classpath:csvdata/beers.csv");
            List<BeerCSVRecord> beerCSVRecords = beerCSVService.convertCsv(beerCsv);

            beerCSVRecords.forEach(beerCSVRecord -> {
                BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                    case "American Pale Lager" -> BeerStyle.LAGER;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                            BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    case "English Pale Ale" -> BeerStyle.PALE_ALE;
                    default -> BeerStyle.PILSNER;
                };

                beerRepository.save(
                    Beer.builder()
                        .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
                        .beerStyle(beerStyle)
                        .price(BigDecimal.TEN)
                        .upc(beerCSVRecord.getRow().toString())
                        .quantityOnHand(beerCSVRecord.getCount())
                    .build()
                );
            });
        }
    }

    /**
     * Maybe not necessary, because we are now using an in-memory H2 database.
     * However, it was nice to practice, and might be handful for something in
     * the future.
     */
    @PreDestroy
    public void destroyAllBootstrappedData() {
        log.debug("## Destroying all bootstrapped data");
        customerRepository.deleteAllById(registeredCustomerIds);
    }
}
