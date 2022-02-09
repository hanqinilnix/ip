package ari.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import ari.command.Command;
import ari.command.MarkCommand;
import ari.parser.Parser;
import ari.tasks.Task;
import ari.tasks.TaskList;

/**
 * Deals with loading tasks from file and saving tasks into file
 */
public class Storage {
    protected File filePath;
    private String path;

    public Storage() {
    }

    /**
     * Sets path to save/load file
     *
     * @param filePath file path to save file or load file
     */
    public void setFile(String filePath) {
        path = filePath;
        assert filePath.endsWith(".txt") : "Filename should have .txt extension";

        this.filePath = new File(filePath);

        if (!this.filePath.exists()) {
            createFile();
        }
    }

    /**
     * Creates file at specified location
     */
    private void createFile() {
        String[] paths = path.split("/");
        String tempPath = "";
        for (String p : paths) {
            tempPath += p + "/";
            File file = new File(tempPath);
            if (p.contains(".")) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    System.out.println("An error has occurred in Storage class");
                    break;
                }
            } else {
                file.mkdir();
            }
        }
    }

    /**
     * Saves tasks in file
     *
     * @param taskList list of Tasks to save
     */
    public void save(TaskList taskList) {
        ArrayList<Task> tasks = taskList.getAllTasks();

        try {
            FileWriter wFile = new FileWriter(this.filePath);
            for (Task task : tasks) {
                wFile.write(task.writeToFile());
                wFile.write(System.lineSeparator());
            }
            wFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads the task into a list if the file exists
     *
     * @return list of Tasks present in the file
     */
    public TaskList load() {
        File file = new File(path);
        TaskList taskList = new TaskList();

        try {
            Scanner reader = new Scanner(file);
            Parser parser = new Parser();
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] todos = parser.fileBreakdown(line);

                Command cmd = parser.fileParse(todos[0], todos[2]);
                cmd.setTaskList(taskList);
                cmd.execute();

                if (todos[1].equals("1")) {
                    cmd = new MarkCommand(taskList.getSize());
                    cmd.setTaskList(taskList);
                    cmd.execute();
                    assert taskList.getTask(taskList.getSize()).hasDone() : "Task should be marked here";
                }
            }

            reader.close();
        } catch (FileNotFoundException fileEx) {
            // if file is not found don't do anything
        }

        return taskList;
    }
}
