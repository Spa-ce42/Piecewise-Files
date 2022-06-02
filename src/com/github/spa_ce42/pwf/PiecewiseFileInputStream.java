package com.github.spa_ce42.pwf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PiecewiseFileInputStream {
    private File directory;
    private int fileId;
    private BufferedOutputStream bos;
    private int numberLength;
    private BufferedInputStream bis;

    private String intToStringSpecial(int i) {
        String s = String.valueOf(i);
        int delta = this.numberLength - s.length();

        if(delta != 0) {
            s = "0".repeat(delta) + s;
            System.out.println(s);
        }

        return s;
    }

    public PiecewiseFileInputStream(File directory, File target) throws FileNotFoundException {
        this.directory = directory;
        this.bos = new BufferedOutputStream(new FileOutputStream(target.getAbsolutePath() + File.separatorChar + "output.zip"));
        String s = this.directory.getAbsolutePath();
        File f = new File(s + File.separatorChar + this.intToStringSpecial(this.numberLength) + ".piece");

        while(!f.exists()) {
            if(++this.numberLength > 100) {
                throw new RuntimeException("Cannot find a piece file sequence.");
            }

            f = new File(s + File.separatorChar + this.intToStringSpecial(this.numberLength) + ".piece");
        }
    }
}
