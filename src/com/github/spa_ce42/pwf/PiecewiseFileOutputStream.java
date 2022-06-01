package com.github.spa_ce42.pwf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PiecewiseFileOutputStream extends OutputStream {
    private final File targetDirectory;
    private BufferedOutputStream bos;
    private int fileId;
    private final int numberLength;
    private final long pieceSize;
    private long spaceLeft;

    private String intToStringSpecial(int i) {
        String s = String.valueOf(i);
        int delta = this.numberLength - s.length();

        if(delta != 0) {
            s = "0".repeat(delta) + s;
        }

        return s;
    }

    private void nextBufferedOutputStream() throws IOException {
        if(this.bos != null) {
            this.bos.close();
        }

        File h = new File(targetDirectory.getAbsolutePath() + File.separatorChar + this.intToStringSpecial(++this.fileId));
        this.bos = new BufferedOutputStream(new FileOutputStream(h));
        this.spaceLeft = this.pieceSize;
    }

    public PiecewiseFileOutputStream(File directory, File zip, long pieceSize) throws IOException {
        this.targetDirectory = directory;
        this.numberLength = (int)Math.log10((double)zip.length() / (double)pieceSize) + 1;
        this.pieceSize = pieceSize;
        this.nextBufferedOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        this.bos.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        long futureSpaceLeft = this.spaceLeft - b.length;

        if(futureSpaceLeft > 0) {
            this.bos.write(b);
            return;
        }
    }
}
