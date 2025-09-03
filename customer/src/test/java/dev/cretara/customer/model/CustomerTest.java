package dev.cretara.customer.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class CustomerTest {

    private static final Settings stringNullableSettings = Settings.create()
            .set(Keys.STRING_NULLABLE, false);

    private static final Model<Customer> CUSTOMER_MODEL = Instancio.of(Customer.class)
            .generate(field(Customer::getFirstName), gen -> gen.string().length(3, 20))
            .generate(field(Customer::getLastName), gen -> gen.string().length(3, 25))
            .generate(field(Customer::getEmail), gen -> gen.net().email())
            .generate(field(Customer::getPhoneNumber), gen -> gen.string().digits().length(10, 15))
            .generate(field(Customer::getAddress), gen -> gen.text().pattern("#d #C Street"))
            .generate(field(Customer::getCity), gen -> gen.string().length(3, 30))
            .generate(field(Customer::getCountry), gen -> gen.string().length(3, 30))
            .generate(field(Customer::getPostalCode), gen -> gen.string().digits().length(5, 10))
            .generate(field(Customer::getCreatedAt), gen -> gen.temporal().localDateTime().past())
            .generate(field(Customer::getCreatedBy), gen -> gen.string().digits().length(5, 10))
            .generate(field(Customer::getUpdatedBy), gen -> gen.string().digits().length(5, 10))
            .withSettings(stringNullableSettings)
            .toModel();

    @Test
    void testCustomerCreation() {
        Customer customer = Instancio.of(CUSTOMER_MODEL)
                .create();

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getFirstName()).isNotNull();
        assertThat(customer.getLastName()).isNotNull();
        assertThat(customer.getEmail()).isNotNull();
        assertThat(customer.getPhoneNumber()).isNotNull();
        assertThat(customer.getAddress()).isNotNull();
        assertThat(customer.getCity()).isNotNull();
        assertThat(customer.getCountry()).isNotNull();
        assertThat(customer.getPostalCode()).isNotNull();
        assertThat(customer.getCreatedBy()).isNotNull();
        assertThat(customer.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(customer.getFirstName()).isNotNull().hasSizeBetween(3, 20);
        assertThat(customer.getLastName()).isNotNull().hasSizeBetween(3, 25);
        assertThat(customer.getEmail()).isNotNull().contains("@");
    }

    @Test
    void testCustomerListCreation() {
        List<Customer> customers = Instancio.ofList(CUSTOMER_MODEL)
                .size(10)
                .withSettings(stringNullableSettings)
                .create();

        assertThat(customers).hasSize(10);
        customers.forEach(customer -> {
            assertThat(customer).isNotNull();
            assertThat(customer.getId()).isNotNull();
            assertThat(customer.getFirstName()).isNotNull();
            assertThat(customer.getLastName()).isNotNull();
            assertThat(customer.getEmail()).isNotNull();
            assertThat(customer.getPhoneNumber()).isNotNull();
            assertThat(customer.getAddress()).isNotNull();
            assertThat(customer.getCity()).isNotNull();
            assertThat(customer.getCountry()).isNotNull();
            assertThat(customer.getPostalCode()).isNotNull();
            assertThat(customer.getCreatedBy()).isNotNull();
            assertThat(customer.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        });
    }

    @Test
    void shouldCreateCustomer_withRandomValues() {
        Customer customer = Instancio.of(Customer.class)
                .generate(field(Customer::getFirstName), gen -> gen.string().length(3, 20))
                .generate(field(Customer::getLastName), gen -> gen.string().length(3, 25))
                .generate(field(Customer::getEmail), gen -> gen.net().email())
                .generate(field(Customer::getPhoneNumber), gen -> gen.string().digits().length(10))
                .generate(field(Customer::getAddress), gen -> gen.text().pattern("#d #C Street"))
                .generate(field(Customer::getCity), gen -> gen.string().length(3, 30))
                .generate(field(Customer::getCountry), gen -> gen.string().length(3, 30))
                .generate(field(Customer::getPostalCode), gen -> gen.string().digits().length(5))
                .create();

        assertThat(customer.getFirstName()).isNotNull().hasSizeBetween(3, 20);
        assertThat(customer.getLastName()).isNotNull().hasSizeBetween(3, 25);
        assertThat(customer.getEmail()).isNotNull().contains("@");
        assertThat(customer.getPhoneNumber()).isNotNull().hasSize(10).matches("\\d+");
        assertThat(customer.getAddress()).isNotNull().contains("Street");
        assertThat(customer.getCity()).isNotNull().hasSizeBetween(3, 30);
        assertThat(customer.getCountry()).isNotNull().hasSizeBetween(3, 30);
        assertThat(customer.getPostalCode()).isNotNull().hasSize(5).matches("\\d+");
    }

    @Test
    void shouldCreateMultipleCustomers_withDifferentRandomValues() {
        Customer customer1 = Instancio.create(Customer.class);
        Customer customer2 = Instancio.create(Customer.class);

        assertThat(customer1.getFirstName()).isNotEqualTo(customer2.getFirstName());
        assertThat(customer1.getLastName()).isNotEqualTo(customer2.getLastName());
        assertThat(customer1.getEmail()).isNotEqualTo(customer2.getEmail());
        assertThat(customer1.getPhoneNumber()).isNotEqualTo(customer2.getPhoneNumber());
        assertThat(customer1.getAddress()).isNotEqualTo(customer2.getAddress());
    }

    @Test
    void shouldCreateCustomer_withSpecificEmailDomain() {
        String emailDomain = "@gmail.com";
        Customer customer = Instancio.of(Customer.class)
                .generate(field(Customer::getFirstName), gen -> gen.string().length(5, 15))
                .generate(field(Customer::getLastName), gen -> gen.string().length(5, 15))
                .generate(field(Customer::getEmail), gen -> gen.net().email().as(email ->
                        email.substring(0, email.indexOf('@')) + emailDomain))
                .create();

        assertThat(customer.getEmail()).endsWith(emailDomain);
        assertThat(customer.getFirstName()).hasSizeBetween(5, 15);
        assertThat(customer.getLastName()).hasSizeBetween(5, 15);
    }

    @Test
    void shouldCreateCustomer_withSpecificCountryAndPostalCode() {
        Customer customer = Instancio.of(Customer.class)
                .set(field(Customer::getCountry), "USA")
                .generate(field(Customer::getPostalCode), gen -> gen.string().digits().length(5))
                .create();

        assertThat(customer.getCountry()).isEqualTo("USA");
        assertThat(customer.getPostalCode()).hasSize(5).matches("\\d+");
    }

    @Test
    void shouldHandleNullableFields_withNullValues() {
        Customer customer = Instancio.of(Customer.class)
                .withSettings(stringNullableSettings)
                .generate(field(Customer::getPhoneNumber), gen -> gen.string().nullable())
                .set(field(Customer::getAddress), null)
                .create();

        assertThat(customer.getAddress()).isNull();
    }

    @Test
    void shouldCreateCustomerList_withDifferentValues() {
        List<Customer> customers = Instancio.ofList(Customer.class)
                .size(3)
                .generate(field(Customer::getCountry), gen -> gen.oneOf("USA", "Canada", "Mexico"))
                .create();

        assertThat(customers).hasSize(3)
                .allSatisfy(customer -> {
                    assertThat(customer.getFirstName()).isNotNull();
                    assertThat(customer.getLastName()).isNotNull();
                    assertThat(customer.getEmail()).isNotNull();
                    assertThat(customer.getCountry()).isIn("USA", "Canada", "Mexico");
                });
    }

    @Test
    void shouldCreateCustomer_withConditionalGeneration() {
        Customer customer = Instancio.of(Customer.class)
                .generate(field(Customer::getFirstName), gen -> gen.string().length(10))
                .filter(field(Customer::getFirstName), (String value) -> value.startsWith("J"))
                .create();

        assertThat(customer.getFirstName()).startsWith("J");
    }

    @Test
    void shouldInheritBaseEntityProperties() {
        Customer customer = Instancio.of(Customer.class)
                .generate(field(Customer::getCreatedAt), gen -> gen.temporal().localDateTime().past())
                .generate(field(Customer::getUpdatedAt), gen -> gen.temporal().localDateTime().past())
                .generate(field(Customer::getCreatedBy), gen -> gen.string().length(5, 15))
                .generate(field(Customer::getUpdatedBy), gen -> gen.string().length(5, 15))
                .create();

        assertThat(customer.getCreatedAt()).isNotNull().isBefore(LocalDateTime.now());
        assertThat(customer.getUpdatedAt()).isNotNull().isBefore(LocalDateTime.now());
        assertThat(customer.getCreatedBy()).isNotNull().hasSizeBetween(5, 15);
        assertThat(customer.getUpdatedBy()).isNotNull().hasSizeBetween(5, 15);
    }

}