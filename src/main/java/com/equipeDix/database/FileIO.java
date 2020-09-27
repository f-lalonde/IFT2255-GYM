package com.equipeDix.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileIO {
    private Object imported;
    private final Path dataDir;
    /**
     * Importe les fichiers s'ils existent
     * @param filename nom du fichier à importer
     */
    public FileIO(String filename, String directory){
        dataDir = Paths.get(".", directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path file = Paths.get(dataDir.toString(), filename + ".bin").toAbsolutePath().normalize();

        imported = null;
        try{
            ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(file.toFile()));
            imported = objIn.readObject();
            objIn.close();
        } catch (Exception e) {
            System.out.println("Impossible d'importer le fichier : " +filename+". Un nouveau fichier sera créé.");
        }
    }

    /**
     * Exporte les HashMap pour utilisation future
     * @param object Objet à stocker
     * @param filename nom du fichier de stockage
     */
    public FileIO(Object object, String filename, String directory) {
        dataDir = Paths.get(".", directory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path file = Paths.get(dataDir.toString(), filename + ".bin").toAbsolutePath().normalize();
        Path fileBkp = Paths.get(dataDir.toString(), "bak_"+filename +".bin").toAbsolutePath().normalize();

        try {
            if (Files.exists(file)) {
                Files.copy(file, fileBkp, StandardCopyOption.REPLACE_EXISTING);
            }
            ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(file.toFile()));
            objOut.writeObject(object);
            objOut.flush();
            objOut.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public FileIO(ServiceTransactionRecord strec, String filename, String directory) {
        dataDir = Paths.get(".", directory).toAbsolutePath().normalize();

        String toExport =
                strec.getRecordCreatedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ;\n" +
                    strec.getProID() + " ;\n" + strec.getMemberID() + " ;\n" + strec.getSeanceID();
        if(!strec.getComment().isBlank()){
            toExport = toExport.concat(" ;\n"+strec.getComment());
        }
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = Paths.get(dataDir.toString(), filename + ".txt").toFile();
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.write(toExport);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getImported() {
        return imported;
    }

    public static void exportTEFtoFile(FileTEF tef){
        String toExport =  tef.getFirstName() + "; " + tef.getLastName() + "; " + tef.getProID() + "; " +
                tef.getAmountToTransfer();

        try {
            Path directory = Paths.get(".", "data/tef/"+tef.getProID()+"/").toAbsolutePath().normalize();
            Files.createDirectories(directory);

            String filePath = directory.toString()+LocalDate.now().toString();
            File file = new File(filePath+".tef");

            // Si un fichier portant le même nom existe :
            int exists = 2;
            while(file.exists()){
                file = new File(filePath+"_"+(exists++)+".tef");
            }

            PrintWriter pw = new PrintWriter(file);
            pw.write(toExport);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void exportInvoice(String toExport, String accountID, String nom){

        try {
            Path directory = Paths.get(".", "data/"+accountID+"/").toAbsolutePath().normalize();
            Files.createDirectories(directory);

            String filePath = directory.toString() + nom + "_" + LocalDate.now().toString();
            File file = new File(filePath);

            PrintWriter pw = new PrintWriter(file);
            pw.write(toExport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportInvoice(String toExport, String accountID, String nom, String type){

        try {
            Path directory = Paths.get(".", "data/"+accountID+"/").toAbsolutePath().normalize();
            Files.createDirectories(directory);

            String filePath = directory.toString() + nom + "_" + LocalDate.now().toString();
            File file = new File(filePath + "_" + type + ".txt");

            PrintWriter pw = new PrintWriter(file);
            pw.write(toExport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
