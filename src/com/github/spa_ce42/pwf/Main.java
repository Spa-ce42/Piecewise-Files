package com.github.spa_ce42.pwf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static final String SLS = System.lineSeparator();
    private static final Character SC = File.separatorChar;

    private static String input(String prompt) {
        System.out.print(prompt);

        try {
            return br.readLine();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String input(PrintStream ps, String prompt) {
        ps.print(prompt);

        try {
            return br.readLine();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void readPieceWiseFile() throws IOException {

    }

    private static boolean isEmpty(File f) throws IOException {
        String[] s = f.list();

        if(s == null) {
            throw new IOException("Abstract path: " + f.getAbsolutePath() + " does not denote a directory.");
        }

        return s.length == 0;
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private static void writePieceWiseFile() throws IOException {
        String target = input("Please specify a target file/directory: ");
        File f = new File(target);

        while(!f.exists()) {
            target = input(System.err, "The file/directory specified does not exist, please try again: ");
            f = new File(target);
        }

        String out = input("Please specify an output directory: ");
        File g = new File(out);

        if(!g.exists()) {
            boolean b = g.mkdirs();

            if(!b) {
                throw new IOException("Failed to make directory: " + g.getAbsolutePath());
            }
        }

        while(!g.isDirectory()) {
            out = input(System.err, "The path specified is not a directory, please try again: ");
            g = new File(out);
        }

        while(!isEmpty(g)) {
            out = input(System.err, "The path specified is not empty, please try again: ");
            g = new File(out);
        }

        File h = new File(g.getAbsolutePath() + SC + "temp.zip");
        h.deleteOnExit();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(h));
        zipFile(f, f.getName(), zos);
        zos.close();

        BufferedInputStream zipReader = new BufferedInputStream(new FileInputStream(h));
    }

    public static void main(String[] args) {
        String io = input("Are we reading pieces or writing pieces? (R/W)" + SLS).toUpperCase();

        while(!io.equals("R") && !io.equals("W")) {
            io = input(System.err, "Unable to recognize input, please try again:" + SLS + "  -  Enter \"R\"(exclude quotations marks) for the reading option." + SLS + "  -  Enter \"W\"(exclude quotation marks) for the writing option." + SLS);
        }

        try {
            if(io.equals("R")) {
                readPieceWiseFile();
                return;
            }

            writePieceWiseFile();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}