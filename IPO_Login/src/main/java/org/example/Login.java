package org.example;
import java.util.regex.Pattern;

public class Login {
    private String username;
    private String password;
    private String cellphone;

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Login() {
    }

    public Login(String username, String password, String cellphone) {
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
    }

    public boolean isValidUsername() {
        // Username input and validation
        return (this.username.length() <= 5 && this.username.contains("_"));
    }

    public boolean isValidPassword() {


        int Case = 0;
        int Digit = 0;

        int special = 0;

        if (this.password.length() >= 8) {

            for (int i = 0; i < this.password.length(); i++) {
                char ch = this.password.charAt(i);

                {
                    if (Character.isUpperCase(ch)) {
                        Case++;
                    } else if (Character.isDigit(ch)) {
                        Digit++;
                    } else if (!(Character.isLetterOrDigit(ch)) || Character.isWhitespace(ch)) {
                        special++;
                    }
                }
            }


        }

        return Case > 0 && (Digit > 0) && (special > 0);
    }

    public boolean isValidCellphone() {
        //code attribution
        //this method was taken from chatgpt.com
        //OpenAI. (2025). ChatGPT (Apr 19 version) [Large language model].
        //https://chatgpt.com/share/6807fb9e-93cc-8001-9535-d2701a11c63a
        String regex = "^\\+\\d{1,4}\\d{1,10}$";

        return (Pattern.matches(regex, this.cellphone));
    }

    public boolean login(String loginUsername, String loginPassword) {
        boolean resultLogin;

        //            login(loginUsername, loginPassword);
        resultLogin = this.username.equals(loginUsername) && this.password.equals(loginPassword);
        return resultLogin;

    }

    private void reset(){
        setUsername(null);
        setPassword(null);
        setCellphone(null);
    }

    public String registerUser() {
//        error string
        String errors = "";
//        Success string
        final String success = "The user has been registered successfully.";

        if (!isValidPassword()) {
            errors = errors + "Password is not\n" +
                    "correctly formatted;\n" +
                    "please ensure that the password\n" +
                    "contains at least eight characters, a capital letter, a number, and a special character.\n";
        }
        if (!isValidUsername()) {
            errors = errors + "Username is not correctly formatted,\n" +
                    "please ensure that\n" +
                    "your username\n" +
                    "contains an\n" +
                    "underscore and is no more that five characters in length.\n";
        }
        if (!isValidCellphone()) {
            errors = errors + "Cell phone number incorrectly formatted or does not contain international code.\n";
        }

        if (errors.isEmpty()) return success;
        else {
            reset();
            return errors;
        }
    }


}