import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by anuradha on 8/9/17.
 */
public class LogFileParser {
    public static void main(String[] args) {
        String filePath = "D:/semester 8/FYP/final project/test.txt";
        String strLine;
        List<String> dataList;
        try{
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(filePath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            //Read File Line By Line
/*            while ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                System.out.println (strLine);
            }
*/
            strLine = br.readLine();
            System.out.println(strLine);

            //Close the input stream
            in.close();
            dataList= Arrays.asList(strLine.split(";")); // one line seperated by comma

            LogFileParser logFileParser = new LogFileParser();
            System.out.println(dataList.get(2));
            if( ! dataList.get(2).endsWith("/")){ //check whether it is a file or folder
                logFileParser.createDayOfWeek(dataList.get(1));
                logFileParser.createTime(dataList.get(0));
            }




        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public String createDayOfWeek(String strTime){
        Long longTime = Long.valueOf(strTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(longTime);
        String dayOfWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)); // sunday=0 and saturday=7
        System.out.println(dayOfWeek);

        return dayOfWeek;
    }

    public String createTime(String timeInfo){
        DateFormat readFormat = new SimpleDateFormat( "dd MMM yyyy hh:mm:ss aa");
        DateFormat writeFormat = new SimpleDateFormat( "HHmmss");
        Date date = null;
        try {
            date = readFormat.parse(timeInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            String formattedDate = writeFormat.format(date);
            System.out.println(formattedDate);
            return formattedDate;
        }
        return null;
    }
}
