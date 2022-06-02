package com.github.spa_ce42.pwf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PiecewiseFileInputStream {
    private final File directory;
    private int fileId;
    private final BufferedOutputStream bos;
    private int numberLength;

    private String intToStringSpecial(int i) {
        String s = String.valueOf(i);
        int delta = Math.max(0, this.numberLength - s.length());

        if(delta != 0) {
            s = "0".repeat(delta) + s;
        }

        return s;
    }

    public PiecewiseFileInputStream(File directory, File target) throws FileNotFoundException {
        this.directory = directory;
        this.bos = new BufferedOutputStream(new FileOutputStream(target.getAbsolutePath() + File.separatorChar + "output.zip"));
        String s = this.directory.getAbsolutePath();
        File f = new File(s + File.separatorChar + this.intToStringSpecial(this.fileId) + ".piece");

        while(!f.exists()) {
            if(++this.numberLength > 100) {
                throw new RuntimeException("Cannot find a piece file sequence.");
            }

            f = new File(s + File.separatorChar + this.intToStringSpecial(this.fileId) + ".piece");
        }
    }

    private boolean hasNextPieceFile() {
        File f = new File(this.directory.getAbsolutePath() + File.separatorChar + this.intToStringSpecial(this.fileId) + ".piece");
        return f.exists();
    }

    private BufferedInputStream getNextPieceFileReader() throws FileNotFoundException {
        File f = new File(this.directory.getAbsolutePath() + File.separatorChar + this.intToStringSpecial(this.fileId++) + ".piece");
        return new BufferedInputStream(new FileInputStream(f));
    }

    public void write() throws IOException {
        while(this.hasNextPieceFile()) {
            BufferedInputStream bis = this.getNextPieceFileReader();
            byte[] buffer = new byte[8192];
            int i;
            while((i = bis.read(buffer)) > 0) {
                if(i < 8192) {
                    byte[] temp = new byte[i];
                    System.arraycopy(buffer, 0, temp, 0, i);
                    this.bos.write(temp);
                    continue;
                }

                this.bos.write(buffer);
            }

            bis.close();
        }
    }

    public void close() throws IOException {
        this.bos.close();
    }
}
