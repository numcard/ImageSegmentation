package app.service;

import app.MainApp;

import java.io.File;
import java.util.prefs.Preferences;

public class PreferenceService
{
    private static PreferenceService instance;
    private final Preferences prefs = Preferences.userNodeForPackage(MainApp.class);

    private PreferenceService()
    {}

    private String getDirectoryPath(String filePath)
    {
        return filePath.split(new File(filePath).getName())[0];
    }

    public static PreferenceService getInstance()
    {
        if(instance == null)
            instance = new PreferenceService();
        return instance;
    }

    public String getLastOpenDirectory()
    {
        return prefs.get("lastOpenDirectory", "");
    }
    public void setLastOpenDirectory(String filePath)
    {
        prefs.put("lastOpenDirectory", getDirectoryPath(filePath));
    }
}
