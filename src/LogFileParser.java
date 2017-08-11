import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;

/**
 * Created by anuradha on 8/9/17.
 */
public class LogFileParser {
    public static int lineNumber=0;

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

            int count=0;// want to remove. this is used only to test 100 lines.

            logFileParser.writeToCSV("dayOfWeek , formattedTime , operationType" +
                    " ,fileType ,parentFolder ,fileSize,,predessorFile1,predessorFile2,predessorFile3,predessorFile4" +
                    ", filename,successorFile1,successorFile2,successorFile3,successorFile4\n");

            while((strLine = br.readLine())!=null && count!=6000){
                count++;
                lineNumber++;
//                System.out.println(strLine);
                dataList = Arrays.asList(strLine.split(";")); // one line seperated by comma

                if( ! dataList.get(2).endsWith("/")){ //check whether it is a file or folder then do the rest
                    String dayOfWeek = logFileParser.createDayOfWeek(dataList.get(1));
                    String formattedTime = logFileParser.createTime(dataList.get(0));
                    String operationType = String.valueOf(logFileParser.checkOperationType(dataList.get(3)));
                    String filename = logFileParser.getFileName(dataList.get(2));
                    String fileType = String.valueOf(logFileParser.getFileType(dataList.get(2)));
                    String parentFolder = logFileParser.getParentFolder(dataList.get(2));
                    String fileSize = String.valueOf(logFileParser.getFileSize(dataList));
                    String successorFiles = logFileParser.getSuccessorFiles(targetFilePath);
                    String predessorFiles = logFileParser.getPredecessorFiles((targetFilePath));

                    String output = dayOfWeek+","+formattedTime +","+ operationType+","+fileType +","+parentFolder +","+fileSize+","+predessorFiles+","+ filename +","+successorFiles+"\n";
                    logFileParser.writeToCSV(output);
                }
            }
            System.out.println("Total line numbers "+lineNumber);
            //Close the input stream
            br.close();

        }catch (Exception e){//Catch exception if any
            System.err.println("Error in main() : " + e);
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
        String fileValue=null;
        try {
            DBOperation dbo = DBOperation.getInstance();
            fileValue=String.valueOf(dbo.getFileNameValue(filePath));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fileValue;
    }

    public int getFileType(String filePath){ // to find file type using extension. if there is no extension then type will be 0.
        int fileType = 0;
        String[] pathList = filePath.split("/");
        String filename = pathList[pathList.length-1];
        if(filename.contains(".")){
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
                case "png":
                    fileType = 5;
                    break;
                case "txt":
                    fileType = 6;
                    break;
            }
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
        String fileValue=null;
        try {
            DBOperation dbo = DBOperation.getInstance();
            fileValue=String.valueOf(dbo.getFileNameValue(parentFolder));
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("parent folder "+parentFolder);
        return fileValue;
    }

    public int  getFileSize(List<String> dataList){ // to find the file size. need to include file size which are not modified
        int fileSize=0;
        if(dataList.get(3).equals("MODIFY")){ // only the modified files will give the size.
            fileSize = Integer.valueOf(dataList.get(4));
        } else{
            try {
                DBOperation dbo = DBOperation.getInstance();
                fileSize=dbo.getFileSize(dataList.get(2));
            } catch (SQLException e) {
                System.out.println(e);
            }

        }
 //       System.out.println("file size "+fileSize);
        return fileSize;
    }

    public String getSuccessorFiles(String targetFilePath) {
        int count = 0;
        String successorPath = null;
        String fileValue="";
        String[] successorArray=new String[4];
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(targetFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;

            for (int i=1;i<=lineNumber;i++){
                br.readLine();
            }
            while((line=br.readLine())!=null){
                List<String> dataList = Arrays.asList(line.split(";")); // one line seperated by comma
                if(!(dataList.get(2).endsWith("/")) && count!=4){
                    successorPath = dataList.get(2);

                    try {
                        DBOperation dbo = DBOperation.getInstance();
                        successorArray[count]=String.valueOf(dbo.getFileNameValue(successorPath));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    count++;
                }

            }
            fileValue+=successorArray[0]+","+successorArray[1]+","+successorArray[2]+","+successorArray[3];
            br.close();

        }catch(Exception e){//Catch exception if any
            System.err.println("Error: " + e);
        }
//        System.out.println("successor file "+successorPath);
        return fileValue;

    }

    public String getPredecessorFiles(String targetFilePath){
        Queue<String> predQueue = new LinkedList();
        String predecessorPath ;
        String predecessorFiles = "";
        try {
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(targetFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            int lineCount=0;
            int predSize=0;
            while((line=br.readLine())!=null && lineCount<lineNumber-1){
                lineCount++;
                List<String> dataList = Arrays.asList(line.split(";")); // one line seperated by comma
                if(!(dataList.get(2).endsWith("/"))){
                    predecessorPath = dataList.get(2);
                    if(predSize<4){
                        predQueue.add(predecessorPath);
                        predSize++;
                    } else {
                        predQueue.remove();
                        predQueue.add(predecessorPath);
                    }
                }
            }
            br.close();
//            System.out.println("size of queue "+predQueue.size());
        }catch(Exception e){//Catch exception if any
            System.err.println("Error: " + e);
        }
        String[] predeessorArray=new String[4];
        if (predQueue.size()==4){
            for (int i=0;i<4;i++) {
                //predecessorFiles+= ":"+predQueue.element(); // need to add comma instead colon
                try {
                    DBOperation dbo = DBOperation.getInstance();
                    predeessorArray[i]=String.valueOf(dbo.getFileNameValue(predQueue.element()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        predecessorFiles+=predeessorArray[0]+","+predeessorArray[1]+","+predeessorArray[2]+","+predeessorArray[3];
        return predecessorFiles;
    }


}
