package com.example.hadar.AcadeMovie.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailsValidation
{
    public void verifyName(String i_fullName)throws Exception
    {
        String RegEx = "^[a-zA-Z\\s]*$";

        if (i_fullName.matches(""))
        {
            throw new Exception("Name field is empty");
        }

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_fullName);

        if(!matcher.matches())
        {
            throw new Exception("Invalid Name, only character");
        }
        else if(i_fullName.length() < 3)
        {
            throw new Exception("Name must contain at least 3 characters");
        }
    }

    public void verifyEmail(String i_email)throws Exception
    {
        String RegEx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (i_email.matches(""))
        {
            throw new Exception("Email field is empty");
        }

        Pattern pattern = Pattern.compile(RegEx);
        Matcher matcher = pattern.matcher(i_email);

        if(!matcher.matches())
        {
            throw new Exception("Invalid Email, doesn't match to email format ");
        }
    }

    public void verifyPassword(String i_password)throws Exception
    {
        if(i_password == null || i_password.isEmpty())
        {
            throw new Exception("Password field is empty");
        }
        else if(i_password.length() < 6 )
        {
            throw new Exception("Password must contain at least 6 characters");
        }
    }
}