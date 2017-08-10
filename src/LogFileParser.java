import java.io.*;
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
        String targetFilePath = "D:/semester 8/FYP/final project/test.txt";
        String strLine;
        List<String> dataList;
        try{
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(targetFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            LogFileParser logFileParser = new LogFileParser();

            for (int i=0 ;i<500;i++){
                br.readLine();
            }
            for (int i=0 ;i<10;i++){
                strLine = br.readLine();
                dataList= Arrays.asList(strLine.split(";")); // one line seperated by comma

                if( ! dataList.get(2).endsWith("/")){ //check whether it is a file or folder then do the rest
                    String dayOfWeek = logFileParser.createDayOfWeek(dataList.get(1));
                    String formattedTime = logFileParser.createTime(dataList.get(0));
                    String operationType = String.valueOf(logFileParser.checkOperationType(dataList.get(3)));
                    String filename = logFileParser.getFileName(dataList.get(2));
                    String fileType = String.valueOf(logFileParser.getFileType(dataList.get(2)));
                    String parentFolder = logFileParser.getParentFolder(dataList.get(2));
                    String fileSize = String.valueOf(logFileParser.getFileSize(dataList));

                    String output = dayOfWeek+","+formattedTime +","+ operationType+","+ filename +","+fileType +","+parentFolder +","+fileSize+"\n";
                    logFileParser.writeToCSV(output);
                }
            }

            //Close the input stream
            br.close();

        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void writeToCSV(String line){
        try {
            FileWriter pw = new FileWriter("D:/semester 8/FYP/final project/test.csv", true);
            pw.append(line);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String createDayOfWeek(String strTime){ // to find the day of the week
        Long longTime = Long.valueOf(strTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(longTime);
        String dayOfWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)); // sunday=0 and saturday=7
//        System.out.println("day of week "+dayOfWeek);

        return dayOfWeek;
    }

    public String createTime(String timeInfo){ // to find time of the day in 24 hour format
        DateFormat readFormat = new SimpleDateFormat( "dd MMM yyyy hh:mm:ss aa");
        DateFormat writeFormat = new SimpleDateFormat( "HHmmss");
        Date date = null;
        try {
            date = readFormat.parse(timeInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            String formattedTime = writeFormat.format(date);
//            System.out.println("formatted time "+formattedTime);
            return formattedTime;
        }
        return null;
    }

    public int checkOperationType(String operation){ // to find the operation type done to the file
        int operationType = 0;
        switch (operation){
            case "CLOSE_NOWRITE":
                operationType = 1;
                break;
            case  "ACCESS":
                operationType =2;
                break;
            case "OPEN":
                operationType = 3;
                break;
            case "MODIFY":
                operationType = 4;
                break;
            case "CREATE":
                operationType = 5;
                break;
            case "CLOSE_WRITE":
                operationType = 6;
                break;
        }
//        System.out.println("file operation "+ operationType);
        return operationType;
    }

    public String getFileName(String filePath){ // to find the name of the file. should be modified with file's unique number using file info table
//        System.out.println("file name "+filePath);
        return filePath;
    }

    public int getFileType(String filePath){ // to find file type using extension. if there is no extension then type will be 0.
        int fileType = 0;
        String[] pathList = filePath.split("/");
        String filename = pathList[pathList.length-1];
        String fData = filename.split("\\.")[1];

        switch (fData){ // need to add more types.
            case "3gp":
                fileType = 1;
                break;
            case "mp3":
                fileType = 2;
                break;
            case "mp4":
                fileType = 3;
                break;
            case "jpg":
                fileType = 4;
                break;
        }
//        System.out.println("file type "+fileType);
        return fileType;
    }

    public String getParentFolder(String filePath){ // to find the immediate parent folder. need to return unique number using info table
        String[] pathList = filePath.split("/");
        String parentFolder ="";
        for (int i=1;i<pathList.length-1;i++){
            parentFolder+= "/"+pathList[i];
        }
//        System.out.println("parent folder "+parentFolder);
        return parentFolder;
    }

    public int  getFileSize(List<String> dataList){ // to find the file size. need to include file size which are not modified
        int fileSize;
        if(dataList.get(3).equals("MODIFY")){ // only the modified files will give the size.
            fileSize = Integer.valueOf(dataList.get(4));
        } else{
            // file size can get using info table. need to implement
            fileSize =0;
        }
 //       System.out.println("file size "+fileSize);
        return fileSize;
    }
}
