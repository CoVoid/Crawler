import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * Created by Stephen on 11/4/2016.
 *
 * -Crawler-
 * This application will
 * -Allow a user to provide a path to a directory x
 * -Find all text files in that directory and it's children x
 * -Open and process the contents of compressed archives that it encounters
 * -Output a histogram of the word counts for the files
 *
 **/


public class Crawler {

    public Map<Integer, Integer> wordCounts = new HashMap();
    //Tree map of word counts. Key = word count, Value = # of files with that many words

    public static void main(String args[]) {
        new Crawler();
    }

    public Crawler() {
        crawl(getDirectory());

        //Tests for directories containing no text files
        if (!wordCounts.isEmpty()){
            drawHistogram();
        } else {
            System.out.println("No .txt files found");
        }
    }

    //Main crawling method
    public void crawl(File root) {
        try {
            if (root.getName().endsWith(".zip")){
                FileInputStream inStream = new FileInputStream(root);
                parseZip(inStream, root.toString());
            } else if (root.getName().endsWith(".txt")) {
                FileInputStream inStream = new FileInputStream(root);
                countWords(inStream);
            } else if (root.isDirectory()) {
                String[] fileList = root.list();
                for (String s : fileList) {
                    File file = new File(root, s);
                    crawl(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Adds the word count of the Inputstream to the wordCounts hashmap
    public void countWords(InputStream inStream) throws IOException {
        StringBuilder s = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = inStream.read(buffer,0,1024)) >= 0){
            s.append(new String(buffer,0,read));
        }
        int wordCount = s.toString().split("\\s").length;
        if (wordCounts.containsKey(wordCount)){
            wordCounts.put(wordCount, (wordCounts.get(wordCount)+ 1));
        } else {
            wordCounts.put(wordCount, 1);
        }
    }

    //crawl and process zip directories
    public void parseZip(InputStream compressedInput, String name) throws IOException {
        ZipInputStream input = new ZipInputStream(compressedInput);
        ZipEntry entry;
        while ((entry = input.getNextEntry()) != null){
            if (entry.getName().endsWith(".zip")) {
                parseZip(input, name + "/" + entry.getName());
            } else if (entry.getName().endsWith(".txt")) {
                countWords(input);
            }
        }
    }

    //generates a histogram of wordCounts
    public void drawHistogram() {
        JFrame frame = new JFrame("Word Counts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(new Graph(wordCounts)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //allows the user to enter a directory
    public File getDirectory() {
        String directoryPath;
        File f;
        System.out.println("Please enter a filepath to the desired directory.");
        System.out.print(">>");
        Scanner myScanner = new Scanner(System.in);
        directoryPath = myScanner.nextLine();

        //ensures provided directory is valid
        f = new File(directoryPath);
        if (f.isDirectory()) {
            return f;
        } else {
            System.out.println("Invalid Directory");
            return (getDirectory());
        }
    }
}
