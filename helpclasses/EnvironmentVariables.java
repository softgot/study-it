/**
 * Klassen används för att hämta absolut sökväg till databasfil i hemmapp.
 * Fungerar både för Windows och Unix system. Sökvägen till databasen är 
 * $HOME/studyit/db/study_it
 */
package helpclasses;

import java.util.Map;

/**
 * @author Kristian Mörling
 */
public class EnvironmentVariables {
    
    /**
     * Returnerar absolut sökväg till databasfil i användarens hemmapp.
     * @return - absolut sökväg till databasfil i användarens hemmapp.
     */
    public static String getDbDir() {
        String os = getOS();
        String dbDir = "";
        String fs = System.getProperty("file.separator");
        
        if (os.equals("windows")) {
            dbDir = getWindowsHomePath();
        } else {
            dbDir = getUnixHomePath();
        }
        
        dbDir = dbDir + fs + "studyit" + fs + "db" + fs + "study_it";
        return dbDir;
    }
    
    /**
     * Tar reda på vilket os programmet exekveras i.
     * @return <br>
     * "windows" - Windows system <br>
     * "OSX/Linux" - Unix system
     */
    private static String getOS() {
        String os = System.getProperty("os.name");
        
        if (os.toLowerCase().indexOf("windows") != -1) {
            os = "windows";
        } else {
            os = "OSX/Linux";
        }
        
        return os;
    }
    
    /**
     * Hämtar sökväg till databasfil i en Unix miljö.
     * @return - sökväg till databasfil
     */
    private static String getUnixHomePath() {
        String unixHomePath = "";
        
        Map<String, String> env = System.getenv();
        unixHomePath = env.get("HOME");
        
        return unixHomePath;
    }
    
    /**
     * Hämtar sökväg till databasfil i Windows miljö.
     * @return - sökväg till databasfil
     */
    private static String getWindowsHomePath() {
        String winHomePath = "";
        
        Map<String, String> env = System.getenv();
        
        winHomePath = env.get("HOMEDRIVE");
        winHomePath += env.get("HOMEPATH");
        
        return winHomePath;
    }
}
