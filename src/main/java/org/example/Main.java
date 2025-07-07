package org.example;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Main {

    public static final String TAB = "    ";
    public static final int TAB_SIZE = 4;
    public static final String FILE_LOCATION = "target/result";

    public static void main(String[] args){
        boolean isCreated = createFolderStructure();
        if(isCreated) {
            System.out.println("CREATE FOLDER STRUCTURE SUCCESSFULLY");
        }else
            System.err.println("CANNOT CREATE FOLDER STRUCTURE");

        System.out.println("PRINT FOLDER STRUCTURE");
        printFolderStructure(new File(FILE_LOCATION), 0);
    }

    /*
        About this function, I will read the folder and file structure from the 'folder_structure.txt' file,
        which is located in the resource.

        For each line in the file:
        - I calculate the level of folder or file by counting the tab space.
        - Then I will get the parent folder from the stack which have level = (current file or folder level - 1),  create the current file or folder inside the parent folder get from stack.
        - If the level is the same or higher, the stack is popped to the appropriate parent level before creation.
        - From the name, I will create folder if it ended with "\", otherwise I will create file for it.

        The entire structure is created inside the folder defined by FILE_LOCATION, so after run project, please check the target/result folder to see the result.
     */

    private static boolean createFolderStructure() {
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("folder_structure.txt");
            if (inputStream == null) {
                System.err.println("File not found!");
                return false;
            }

            List<String> lines;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                lines = reader.lines().toList();
            }

            Deque<File> stack = new ArrayDeque<>();

            //The folder structure will be created in target/result folder
            File parent = new File(FILE_LOCATION);
            stack.push(parent);

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                int level = getLevel(line);
                String rawName = line.strip();

                String name = rawName.endsWith("\\") ? rawName.substring(0, rawName.length() - 1) : rawName;

                while (stack.size() > level + 1) {
                    stack.pop();
                }

                parent = stack.peek();
                File current = new File(parent, name);

                boolean isCreated;
                if (rawName.endsWith("\\")) {
                    isCreated = current.mkdirs();
                    stack.push(current);
                } else {
                    isCreated = current.createNewFile();
                }

                if (!isCreated) {
                    System.err.println("Cannot create: " + current.getAbsolutePath());
                    return false;
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static int getLevel(String line) {
        int level = 0;
        while(line.startsWith(TAB)) {
            line = line.substring(TAB_SIZE);
            level++;
        }
        return level;
    }


    /*
        I use function recursively to print the folder and file structure
        For each file or directory in 'rootDir':
        - I print tab space based on the level using the TAB constant.
        - Then I check if it is a directory, it appends "/" and recursively calls this function again to print this folder's contents.
        If it is a file, I just print the file name.
     */
    private static void printFolderStructure(File rootDir, int level) {
        if (!rootDir.exists() || !rootDir.isDirectory())
            return;

        File[] files = rootDir.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            int i = 0;
            while(i++ < level) {
                System.out.print(TAB);
            }

            if (file.isDirectory()) {
                System.out.println(file.getName() + "/");
                printFolderStructure(file, level + 1);
            } else {
                System.out.println(file.getName());
            }
        }
    }

}