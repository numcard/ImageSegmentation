package app;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class AppException extends Exception
{
    private AppException()
    {}

    public static void Throw(Exception exception)
    {
        try(FileWriter fw = new FileWriter("src/app/log/log.txt", true);
            PrintWriter pw = new PrintWriter(fw))
        {
            exception.printStackTrace(); // debug mode
            pw.write(exception.getMessage());
            pw.flush();
        } catch(IOException e)
        {
            AppException.Throw(e);
        }
    }
}