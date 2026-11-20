package de.seuhd.campuscoffee.tests.acceptance;

import de.seuhd.campuscoffee.tests.ConsumptionRequests;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Step definitions for the coffee count correction scenarios. */
public class CucumberCorrectionSteps {
    private static final String UNDO_SCENARIO_NOTE = "Correction before attempting to undo the last coffee";

    private final SettableClock clock;
    private final CucumberConsumptionSteps consumptionSteps;
    private String loginName;
    private String adminLoginName;
    private String note;
    private int initialCount;
    private int lastStatus;

    public CucumberCorrectionSteps(SettableClock clock, CucumberConsumptionSteps consumptionSteps) {
        this.clock = clock;
        this.consumptionSteps = consumptionSteps;
    }

    @Given("the admin {string} and the user {string} with a coffee count of {int} and a note {string}")
    public void theAdminAndTheUserWithACoffeeCountAndANote(
            String adminLoginName,
            String loginName,
            int count,
            String note
    ) {
        this.adminLoginName = adminLoginName;
        this.loginName = loginName;
        this.initialCount = count;
        this.note = note;
        addCoffees(loginName, count);
    }

    @Given("the user {string} with a coffee count of {int}")
    public void theUserWithACoffeeCount(String loginName, int count) {
        this.loginName = loginName;
        this.initialCount = count;
        addCoffees(loginName, count);
    }

    @When("the admin corrects the coffee count to {int} with the note")
    public void theAdminCorrectsTheCoffeeCountToWithTheNote(int count) {
        lastStatus = ConsumptionRequests.INSTANCE.correctCoffeeCount(adminLoginName, loginName, count, note);
    }

    @When("{word} tries to correct the users coffee count to {int} with the note {string}")
    public void aUserTriesToCorrectTheUsersCoffeeCountToWithTheNote(
            String correctorLoginName,
            int count,
            String note
    ) {
        lastStatus = ConsumptionRequests.INSTANCE.correctCoffeeCount(correctorLoginName, loginName, count, note);
    }

    @Then("the request succeeds and the coffee count is {int}, and the corrections list contain before, after, note and admin")
    public void theRequestSucceedsAndTheCoffeeCountIsAndTheCorrectionsListContainsBeforeAfterNoteAndAdmin(int count) {
        assertThat(lastStatus).isEqualTo(HttpStatus.OK.value());
        assertThat(ConsumptionRequests.INSTANCE.coffeeCount(loginName)).isEqualTo(count);
        assertThat(ConsumptionRequests.INSTANCE.corrections(loginName))
                .containsExactly(new ConsumptionRequests.Correction(initialCount, count, note, adminLoginName));
    }

    @Then("^the request returns (\\d+) (.+)$")
    public void theRequestReturns(int status, String reasonPhrase) {
        assertResponseStatus(status, reasonPhrase, lastStatus);
        assertThat(ConsumptionRequests.INSTANCE.coffeeCount(loginName)).isEqualTo(initialCount);
    }

    @Then("the admin {string} corrects the coffee count to {int} at {time}")
    public void theAdminCorrectsTheCoffeeCountToAt(String adminLoginName, int count, LocalTime time) {
        clock.setTime(time);
        String userLoginName = consumptionSteps.currentLoginName();
        lastStatus = ConsumptionRequests.INSTANCE.correctCoffeeCount(
                adminLoginName,
                userLoginName,
                count,
                UNDO_SCENARIO_NOTE
        );
        assertThat(lastStatus).isEqualTo(HttpStatus.OK.value());
        assertThat(ConsumptionRequests.INSTANCE.coffeeCount(userLoginName)).isEqualTo(count);
    }

    @Then("^the request returns is (\\d+) (.+) and the coffee count stays (\\d+)$")
    public void theRequestReturnsAndTheCoffeeCountStays(int status, String reasonPhrase, int count) {
        assertResponseStatus(status, reasonPhrase, consumptionSteps.currentStatus());
        assertThat(ConsumptionRequests.INSTANCE.coffeeCount(consumptionSteps.currentLoginName())).isEqualTo(count);
    }

    private void addCoffees(String loginName, int count) {
        for (int coffee = 0; coffee < count; coffee++) {
            assertThat(ConsumptionRequests.INSTANCE.addCoffee(loginName)).isEqualTo(HttpStatus.OK.value());
        }
        assertThat(ConsumptionRequests.INSTANCE.coffeeCount(loginName)).isEqualTo(count);
    }

    private void assertResponseStatus(int expectedStatus, String reasonPhrase, int actualStatus) {
        assertThat(HttpStatus.valueOf(expectedStatus).getReasonPhrase()).isEqualTo(reasonPhrase);
        assertThat(actualStatus).isEqualTo(expectedStatus);
    }
}
