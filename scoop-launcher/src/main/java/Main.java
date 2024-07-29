import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.openqa.selenium.safari.SafariDriver;

public class Main {

    static final String SECRET_KEY = "your_mom_lol";
    static final String SALTVALUE = "123_my_glock_17_is_with_me";

    static String eMail;
    static String pw;
    static int browserOption;

    static WebDriver driver;

    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
        //Yes, im serious about this. Even though this is evident for most of you folks, it's not for Karens.
        System.out.println("""
                ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                Seamless CO-OP Mod Launcher - Not endorsed-from or related-to the creator of the mod!
                The creator of this launcher doesn't guarantee safety for either your game or your given (Nexus-Mod) credentials and therefore is not to be hold liable/accountable for any damage.
                When launched, the launcher automatically makes connections to nexusmods.com with your given account-credentials to check for updates.
                If any update is found, the launcher will download and install the updated version of the Seamless CO-OP mod.
                For more general information take a look at the README file.
                By continuing to use this launcher you've read and agree to these Terms of Service.
                ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------""");


        if(Files.exists(Path.of("C:\\settings.json"))){
            jsonParse();
            process();
        }
        else{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select between (option will be saved and can be changed in the 'settings.json' file located in the same folder of this application):\n" +
                    "1) Chrome\n" +
                    "2) Firefox\n" +
                    "3) Safari\n");
            int option_for_webDriver = scanner.nextInt();

        }
    }


    static void jsonParse() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("C:\\settings.json"));
        browserOption = (int) jsonObject.get("browserOption");
        eMail = (String) jsonObject.get("eMail");
        pw = (String) jsonObject.get("pw");
    }


    static void process() throws InterruptedException {
        System.out.println("Loading your settings. This may take a while!");
        String dec_eMail = decrypt(eMail);
        String dec_pw = decrypt(pw);
        switch(browserOption){
            case 1 -> {
                System.out.println("Starting Chrome...");
                driver = new ChromeDriver();
            }
            case 2 -> {
                System.out.println("Starting Firefox...");
                driver = new FirefoxDriver();
            }
            case 3 -> {
                System.out.println("Starting Safari...");
                driver = new SafariDriver();
            }
        }
        driver.get("https://www.nexusmods.com/eldenring/mods/510?tab=files&file_id=26225");
        //Navigating to the login page
        WebElement checkInput = driver.findElement(By.className("rj-btn rj-btn-clear"));
        checkInput.click();
        //Entering email and password credentials
        checkInput = driver.findElement(By.name("user[login]"));
        checkInput.sendKeys(dec_eMail);
        checkInput = driver.findElement(By.name("user[password]"));
        checkInput.sendKeys(dec_pw);
        checkInput = driver.findElement(By.className("btn btn-primary"));
        checkInput.click();
        //Waiting to get back to download page
        checkInput = driver.findElement(By.className("site-nexusmods-b scheme-theme-ReskinOrange game-theme-4333  apage modpage new-head")); //this is the html-body of the download page
        if(checkInput.isDisplayed()){
            //Slow-Download
            checkInput = driver.findElement(By.className("rj-btn rj-btn-secondary rj-btn-full"));
            checkInput.click();
            checkInput.wait(3000);
            driver.quit();
        }
        else{
            checkInput.wait(500);
        }
        //TODO: Extract .zip and install in right directory (replace). After that ask for password to be set (.ini-format).
    }



    //Origin of code: https://www.javatpoint.com/aes-256-encryption-in-java
    static String encryptInput(String input){
        /* Encryption Method */
        try {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            /* Retruns encrypted value. */
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(input.getBytes(StandardCharsets.UTF_8)));
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException |
               InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println("Error occured during encryption: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            /* Retruns decrypted value. */
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println("Error occured during decryption: " + e.toString());
        }
        return null;
    }

}