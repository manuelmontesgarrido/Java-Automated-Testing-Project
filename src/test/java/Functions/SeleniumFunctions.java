package Functions;

import StepDefinitions.Hooks;
import cucumber.api.Scenario;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The SeleniumFunctions class contains
 * all the functions needed to interact
 * with the browser through Selenium.
 *
 * @author  Manuel Montes
 * @version 1.0
 * @since   2021-05-12
 */

public class SeleniumFunctions {
    static WebDriver driver;
    public static Properties prop = new Properties();
    public static InputStream in = SeleniumFunctions.class.getResourceAsStream("../test.properties");
    public static Map<String, String> ScenaryData = new HashMap<>();
    public static Map<String, String> HandleMyWindows = new HashMap<>();

    public String ElementText = "";
    public static final int EXPLICIT_TIMEOUT = 5;

    /**
     * Instances the driver from the Hooks class
     */

    public SeleniumFunctions()
    {
        driver = Hooks.driver;
    }

    /******** Scenario Attributes ********/

    Scenario scenario = null;
    public void scenario (Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * Reads the values of the properties stored in the file test.properties
     * @param property Property read from test.properties
     * @return Returns the value of the requested property
     */
    public String readProperties(String property) {
        try {
            prop.load(in);
            log.info(String.format("Get property: %s", property));
        } catch (IOException e) {
            log.error("ReadProperty: No existe la propiedad " + property);
        }
        return prop.getProperty(property);
    }

    /******** Log Attribute ********/

    private static final Logger log = Logger.getLogger(SeleniumFunctions.class);
    public static String FileName = "";
    public static String PagesFilePath = "src/test/resources/Pages/";
    public static String GetFieldBy = "";
    public static String ValueToFind = "";

    /**
     * Returns the JSON file hosted in the Pages folder
     * @return Returns a JSON parse file
     * @throws Exception If the file does not exist
     */
    public static Object readJson() throws Exception {
        FileReader reader = new FileReader(PagesFilePath + FileName);
        try {
            if (reader != null) {
                JSONParser jsonParser = new JSONParser();
                return jsonParser.parse(reader);
            } else {
                return null;
            }
        } catch (FileNotFoundException e) {
            log.error("ReadEntity: No existe el archivo " + FileName);
            return null;
        } catch (NullPointerException e) {
            log.error("ReadEntity: No existe el archivo " + FileName);
            throw new IllegalStateException("ReadEntity: No existe el archivo " + FileName, e);
        }
    }

    /**
     * Returns the path of the selected entity
     * @param element Name of the selected entity
     * @return Returns the path of the selected entity
     * @throws Exception If the entity does not exist
     */
    public static JSONObject ReadEntity(String element) throws Exception {
        JSONObject jsonObject = (JSONObject) readJson();
        JSONObject Entity = (JSONObject) jsonObject.get(element);
        log.info(Entity.toJSONString());
        return Entity;
    }

    /**
     * Take a screenshot
     * @param TestCaptura Screenshot name
     * @throws IOException If the file does not exist
     */
    public void ScreenShot(String TestCaptura) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
        String screenShotName = readProperties(
                "ScreenShotPath") +
                "\\" + readProperties("browser") +
                "\\" + TestCaptura + "_(" + dateFormat.format(GregorianCalendar.getInstance().getTime()) + ")";
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        log.info("Screenshot saved as:" + screenShotName);
        FileUtils.copyFile(scrFile, new File(String.format("%s.png", screenShotName)));
    }

    /**
     * Attach a screenshot to the report
     * @param captura Screenshot name
     * @return Screenshot
     */
    public byte[] attachScreenShot(String captura){
        log.info("Attaching Screenshot");
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(captura, new ByteArrayInputStream(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
        return screenshot;
    }

    /**
     * Assert if element is present
     * @param element Name of the entity
     * @return Returns whether an element is present or not
     * @throws Exception If the element is not present
     */
    public boolean isElementDisplayed(String element) throws Exception {
        boolean isDisplayed = Boolean.parseBoolean(null);
        try {
            By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
            log.info(String.format("Waiting Element: %s", element));
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            isDisplayed = wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement)).isDisplayed();
        }catch (NoSuchElementException | TimeoutException e){
            isDisplayed = false;
            log.info(e);
        }
        log.info(String.format("%s visibility is: %s", element, isDisplayed));
        return isDisplayed;
    }

    /**
     * Click on accept an alert
     */
    public void AcceptAlert()
    {
        try{
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            log.info("The alert was accepted successfully.");
        }catch(Throwable e){
            log.error("Error came while waiting for the alert popup. "+e.getMessage());
        }
    }

    /**
     * Click on dismiss an alert
     */
    public void dismissAlert()
    {
        try{
            WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            log.info("The alert was dismissed successfully.");
        }catch(Throwable e){
            log.error("Error came while waiting for the alert popup. "+e.getMessage());
        }
    }

    /**
     * Gets the path of the element depending on the type of locator.
     * @param element Name of the entity
     * @return Returns the path of the selected element
     * @throws Exception If the element does not exist
     */
    public static By getCompleteElement(String element) throws Exception {
        By result = null;
        JSONObject Entity = ReadEntity(element);

        GetFieldBy = (String) Entity.get("GetFieldBy");
        ValueToFind = (String) Entity.get("ValueToFind");

        if ("className".equalsIgnoreCase(GetFieldBy)) {
            result = By.className(ValueToFind);
        } else if ("cssSelector".equalsIgnoreCase(GetFieldBy)) {
            result = By.cssSelector(ValueToFind);
        } else if ("id".equalsIgnoreCase(GetFieldBy)) {
            result = By.id(ValueToFind);
        } else if ("linkText".equalsIgnoreCase(GetFieldBy)) {
            result = By.linkText(ValueToFind);
        } else if ("name".equalsIgnoreCase(GetFieldBy)) {
            result = By.name(ValueToFind);
        } else if ("link".equalsIgnoreCase(GetFieldBy)) {
            result = By.partialLinkText(ValueToFind);
        } else if ("tagName".equalsIgnoreCase(GetFieldBy)) {
            result = By.tagName(ValueToFind);
        } else if ("xpath".equalsIgnoreCase(GetFieldBy)) {
            result = By.xpath(ValueToFind);
        }
        return result;
    }

    /**
     * Select the option by index
     * @param element Select
     * @param option Index option
     * @throws Exception If element does not exist
     */
    public void selectOptionDropdownByIndex(String element, int option) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("Waiting Element: %s", element));

        Select opt = new Select(driver.findElement(SeleniumElement));
        log.info("Select option: " + option + "by text");
        opt.selectByIndex(option);
    }

    /**
     * Select the option by text
     * @param element Select
     * @param option Text option
     * @throws Exception If element does not exist
     */
    public void selectOptionDropdownByText(String element, String option) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("Waiting Element: %s", element));

        Select opt = new Select(driver.findElement(SeleniumElement));
        log.info("Select option: " + option + "by text");
        opt.selectByVisibleText(option);
    }

    /**
     * Select the option by value
     * @param element Select
     * @param option Value option
     * @throws Exception If element does not exist
     */
    public void selectOptionDropdownByValue(String element, String option) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        log.info(String.format("Waiting Element: %s", element));

        Select opt = new Select(driver.findElement(SeleniumElement));
        log.info("Select option: " + option + "by text");
        opt.selectByValue(option);
    }

    /**
     * Check a checkbox
     * @param element Name of the entity
     * @throws Exception If element does not exist
     */
    public void checkCheckbox(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean isChecked = driver.findElement(SeleniumElement).isSelected();
        if(!isChecked){
            log.info("Clicking on the checkbox to select: " + element);
            driver.findElement(SeleniumElement).click();
        }
    }

    /**
     * Uncheck a checkbox
     * @param element Name of the entity
     * @throws Exception If the element does not exist
     */
    public void UncheckCheckbox(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean isChecked = driver.findElement(SeleniumElement).isSelected();
        if(isChecked){
            log.info("Clicking on the checkbox to select: " + element);
            driver.findElement(SeleniumElement).click();
        }
    }

    /**
     * Scroll to element
     * @param element Name of the entity
     * @throws Exception if the element does not exist
     */
    public void scrollToElement(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        log.info("Scrolling to element: " + element);
        jse.executeScript("arguments[0].scrollIntoView();", driver.findElement(SeleniumElement));
    }

    /**
     * Click in javascript element
     * @param element Name of the entity
     * @throws Exception If element does not exist
     */
    public void ClickJSElement(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        log.info("Click to element: " + element);
        jse.executeScript("arguments[0].click()", driver.findElement(SeleniumElement));
    }

    /**
     * Checks if part of an element's text is not present
     * @param element Name of the entity
     * @param text Text to be checked
     * @throws Exception If the element does not exist
     */
    public void checkPartialTextElementNotPresent(String element,String text) throws Exception {
        ElementText = GetTextElement(element);
        boolean isFoundFalse = ElementText.indexOf(text) !=-1? true: false;
        Assert.assertFalse("Text is present in element: " + element + " current text is: " + ElementText, isFoundFalse);
    }

    /**
     * Checks if part of an element's text is present
     * @param element Name of the entity
     * @param text Text to be checked
     * @throws Exception If the element does not exist
     */
    public void checkPartialTextElementPresent(String element,String text) throws Exception {
        ElementText = GetTextElement(element);
        boolean isFound = ElementText.indexOf(text) !=-1? true: false;
        Assert.assertTrue("Text is not present in element: " + element + " current text is: " + ElementText, isFound);
    }

    /**
     * Checks if a text is present in a selected element
     * @param element Name of the entity
     * @param text Text to be checked
     * @throws Exception If the element does not exist
     */
    public void checkTextElementEqualTo(String element, String text) throws Exception {
        try {
            ElementText = GetTextElement(element);
            Assert.assertEquals(ElementText, text);
            log.info("The element " + ElementText + " is equal to " + text);
        } catch (NoSuchElementException e) {
            log.error("The element " + element + "is not equal to " + text);
        }
    }

    /**
     * Get text element
     * @param element Name of the entity
     * @return Returns the text contained in the element
     * @throws Exception If the element does not exist
     */
    public String GetTextElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait wait = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
        wait.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
        log.info(String.format("Esperando el elemento: %s", element));
        ElementText = driver.findElement(SeleniumElement).getText();
        return ElementText;
    }

    /**
     * Set text in the selected element
     * @param element Name of the entity
     * @param text Text to be set
     * @throws Exception If the element does not exist
     */
    public void iSetElementWithText(String element, String text) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        driver.findElement(SeleniumElement).clear();
        driver.findElement(SeleniumElement).sendKeys(text);
        log.info(String.format("Set on element %s with text %s", element, text));
    }

    /**
     * Set key value in the selected element
     * @param element Name of the entity
     * @param key Key to be set
     * @throws Exception If the element does not exist
     */
    public void iSetElementWithKeyValue(String element, String key) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        boolean exist = this.ScenaryData.containsKey(key);
        if (exist){
            String text = this.ScenaryData.get(key);
            driver.findElement(SeleniumElement).sendKeys(text);
            log.info(String.format("Set on element %s with text %s", element, text));
        }else{
            Assert.assertTrue(String.format("The given key %s do not exist in Context", key), this.ScenaryData.containsKey(key));
        }
    }

    /**
     * Double click on the selected element
     * @param element Name or the entity
     * @throws Exception If the element does not exist
     */
    public void doubleClick(String element) throws Exception
    {
        Actions action = new Actions(driver);
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        action.moveToElement(driver.findElement(SeleniumElement)).doubleClick().perform();
        log.info("Double click on element: " + element);
    }

    /**
     * Click on the selected element
     * @param element Name or the entity
     * @throws Exception If the element does not exist
     */
    public void iClicInElement(String element) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        driver.findElement(SeleniumElement).click();
        log.info("Click on element by " + element);
    }

    /**
     * Cilck on the enter key
     * @throws Exception If the element does not exist
     */
    public void iClicInEnter() throws Exception {
        driver.findElement(By.tagName("body")).sendKeys(Keys.ENTER);
        log.info("Click enter ");
    }

    /**
     * Scrolls to the top or bottom of the page
     * @param to Position on the page
     * @throws Exception If the element does not exist
     */
    public void scrollPage(String to) throws Exception
    {
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        if(to.equals("top")){
            log.info("Scrolling to the top of the page");
            jse.executeScript("scroll(0, -250);");
        }
        else if(to.equals("end")){
            log.info("Scrolling to the end of the page");
            jse.executeScript("scroll(0, 250);");
        }
    }

    /**
     * Zoom in to display the element
     * @param element Name of the entity
     * @throws Exception If the element does not exist
     */
    public void zoomTillElementDisplay(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebElement html = driver.findElement(SeleniumElement);
        html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
    }

    /**
     * Switch to selected frame
     * @param Frame Name of the entity
     * @throws Exception If the element does not exist
     */
    public void switchToFrame(String Frame) throws Exception {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(Frame);
        log.info("Switching to frame: " + Frame);
        driver.switchTo().frame(driver.findElement(SeleniumElement));
    }

    /**
     * Switch to selected parent frame
     */
    public void switchToParentFrame() {
        log.info("Switching to parent frame");
        driver.switchTo().parentFrame();
    }

    /**
     * Wait for element present
     * @param element Name of the entity
     * @throws Exception If the element does not exist
     */
    public void waitForElementPresent(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
        log.info("Waiting for the element: "+element + " to be present");
        w.until(ExpectedConditions.presenceOfElementLocated(SeleniumElement));
    }

    /**
     * Wait for element visible
     * @param element Name of the entity
     * @throws Exception If the element does not exist
     */
    public void waitForElementVisible(String element) throws Exception
    {
        By SeleniumElement = SeleniumFunctions.getCompleteElement(element);
        WebDriverWait w = new WebDriverWait(driver, EXPLICIT_TIMEOUT);
        log.info("Waiting for the element: "+element+ " to be visible");
        w.until(ExpectedConditions.visibilityOfElementLocated(SeleniumElement));
    }

    /**
     * Save in Scenario
     * @param key Key of the Scenario
     * @param text Text to save in Scenario
     */
    public void SaveInScenario(String key, String text) {
        if (!this.ScenaryData.containsKey(key)) {
            this.ScenaryData.put(key,text);
            log.info(String.format("Save as Scenario Context key: %s with value: %s ", key,text));
        } else {
            this.ScenaryData.replace(key,text);
            log.info(String.format("Update Scenario Context key: %s with value: %s ", key,text));
        }
    }

    public void pageHasLoaded () {
        String GetActual = driver.getCurrentUrl();
        log.info(String.format("Checking if %s page is loaded.", GetActual));
        new WebDriverWait(driver, EXPLICIT_TIMEOUT).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript(
                        "return document.readyState").equals("complete")
        );
    }

    public void openNewTabWithurl(String url) {
        log.info("Open new tab with URL: " + url);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript(String.format("window.open('%s','_blank');", url));
    }

    public static int randomNumberInRange(int min, int max) {
        // nextInt retorna en rango pero con límite superior exclusivo, por eso sumamos 1
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public String randomString(int lengthLimit) throws IOException {
        String randomData = readProperties("randomDataBank");
        String randomChain = "";
        for (int x = 0; x < lengthLimit; x++) {
            int randomRange = randomNumberInRange (0, randomData.length() - 1);
            char randomChar = randomData.charAt(randomRange);
            randomChain += randomChar;
        }
        return randomChain;
    }

}