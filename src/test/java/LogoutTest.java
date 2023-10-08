import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class LogoutTest {

    WebDriver driver;
    WebDriverWait wait;
    WebElement logOutBtn;
    final String BaseURL = "http://training.skillo-bg.com:4200";
    final String HomeURL = BaseURL + "/posts/all";
    final String ProfileURL = BaseURL + "/users/";
    final String NewPostURL = BaseURL + "/posts/create";
    final String Login_URL = BaseURL + "/users/login";

    @BeforeMethod
    public void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(6));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @DataProvider(name = "getUserData")
    public Object[][] getUsers() {
        return new Object[][]{
                {"SimonaSLS", "Monika987321*"}
        };
    }

    @Test(dataProvider = "getUserData")

    public void Login(String username, String password) {
        System.out.println("Navigate to the Home page");
        driver.get(BaseURL);

        System.out.println(" Click  the Login button");
        WebElement loginBtn = driver.findElement(By.id("nav-link-login"));
        clickElement(loginBtn);

        System.out.println(" Validate that the URL is correct ");
        wait.until(ExpectedConditions.urlToBe(Login_URL));

        System.out.println("Verify that the login form has appeared");
        WebElement signInForm = driver.findElement(By.cssSelector("p.h4"));
        wait.until(ExpectedConditions.visibilityOf(signInForm));

        System.out.println("Populate username field with 'username' + password field with password'");
        WebElement usernameField = driver.findElement(By.id("defaultLoginFormUsername"));
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        usernameField.sendKeys(username);

        WebElement passwordField = driver.findElement(By.id("defaultLoginFormPassword"));
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.sendKeys(password);

        System.out.println("Click the sign in button");
        WebElement Signin = driver.findElement(By.id("sign-in-button"));
        clickElement(Signin);

        System.out.println("Validate the URL is correct - Homepage");
        wait.until(ExpectedConditions.urlToBe(HomeURL));
    }

    @Test(dataProvider = "getUserData")
    public void Logout(String username, String password) {

        System.out.println("Logout from the Homepage");
        Login("SimonaSLS", "Monika987321*");
        logOutBtn = driver.findElement(By.cssSelector("i.fa-sign-out-alt"));
        clickElement(logOutBtn);

        System.out.println("Validate that the Login form has appeared and logOut button is not displayed");
        successfulLogout();

        System.out.println("Login and navigate to the Profile page");
        Login("SimonaSLS", "Monika987321*");
        WebElement ProfileBtn = driver.findElement(By.id("nav-link-profile"));
        clickElement(ProfileBtn);

        System.out.println("Validate that Profile page is loaded and profile picture has appeared ");
        wait.until(ExpectedConditions.urlContains(ProfileURL));
        WebElement usernameHeader = driver.findElement(By.cssSelector(".profile-user-settings h2"));
        String UsernameTitle = usernameHeader.getText();
        Assert.assertEquals(UsernameTitle, username, "Username title is incorrect.");

        System.out.println("Logout from Profile page");
        logOutBtn = driver.findElement(By.cssSelector("i.fa-sign-out-alt"));
        clickElement(logOutBtn);
        successfulLogout();

        System.out.println("Login and navigate to the New Post page");
        Login("SimonaSLS", "Monika987321*");
        WebElement newPost = driver.findElement(By.id("nav-link-new-post"));
        clickElement(newPost);

        System.out.println("Validate that New Post page is loaded and the text 'Post a picture to share with your awesome followers' is visible ");
        wait.until(ExpectedConditions.urlContains(NewPostURL));
        String expectedText = "Post a picture to share with your awesome followers";
        WebElement newPostHeader = driver.findElement(By.cssSelector("h3.text-center"));
        String actualText = newPostHeader.getText();
        Assert.assertEquals(actualText, expectedText, "New post could not be uploaded");

        System.out.println("Logout from the New Post page");
        logOutBtn = driver.findElement(By.cssSelector("i.fa-sign-out-alt"));
        clickElement(logOutBtn);
        successfulLogout();
    }


    @AfterTest
    public void teardown() {
        driver.close();
    }

    public void clickElement(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public void successfulLogout() {
        try {
            wait.until(ExpectedConditions.urlToBe(Login_URL));
            if (logOutBtn.isDisplayed()) {
                System.out.println("The user is not signed out");
            } else {
                System.out.println("Successful sign out");
            }
        } catch (StaleElementReferenceException e) {
            System.out.println("Successful sign out");
        }
//        Assert.assertFalse(logOutBtn.isDisplayed()," The logout button must not be displayed, but it is");
    }
}
