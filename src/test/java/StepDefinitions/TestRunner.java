package StepDefinitions;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:Features",
        glue = "classpath:StepDefinitions",
        tags = "@test"
        /*
        tags = "@test" solo correrá los steps definitions con el tag @test
        Se pueden definir varios tags para que corran varias steps definitions con el formato:
        tags = "@test, @otherTag"
        También se pueden ignorar escenarios con una tag concreta con el formato:
        tags = "not @smoke"
        */
)

public class TestRunner {
}
