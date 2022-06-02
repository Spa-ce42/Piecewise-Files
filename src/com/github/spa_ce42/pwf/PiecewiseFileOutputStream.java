package com.github.spa_ce42.pwf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PiecewiseFileOutputStream extends OutputStream {
    private final File targetDirectory;
    private BufferedOutputStream bos;
    private int fileId = -1;
    private final int numberLength;
    private final int pieceSize;
    private int spaceLeft;

    private String intToStringSpecial(int i) {
        String s = String.valueOf(i);
        int delta = Math.max(0, this.numberLength - s.length());

        if(delta != 0) {
            s = "0".repeat(delta) + s;
        }

        return s;
    }

    private void nextBufferedOutputStream() throws IOException {
        if(this.bos != null) {
            this.bos.close();
        }

        File h = new File(targetDirectory.getAbsolutePath() + File.separatorChar + this.intToStringSpecial(++this.fileId) + ".piece");
        this.bos = new BufferedOutputStream(new FileOutputStream(h));
        this.spaceLeft = this.pieceSize;
    }

    public PiecewiseFileOutputStream(File directory, File zip, int pieceSize) throws IOException {
        this.targetDirectory = directory;
        this.numberLength = Math.max(0, (int)Math.log10((double)zip.length() / (double)pieceSize)) + 1;
        this.pieceSize = pieceSize;
        this.nextBufferedOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        this.bos.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        int futureSpaceLeft = this.spaceLeft - b.length;

        if(futureSpaceLeft > 0) {
            this.bos.write(b);
            this.spaceLeft = futureSpaceLeft;
            return;
        }

        int position = 0;

        while(position < b.length) {
            int delta = b.length - position;

            if(delta > this.spaceLeft) {
                this.bos.write(b, position, this.spaceLeft);
                position = position + this.spaceLeft;
                this.nextBufferedOutputStream();
                continue;
            }

            this.bos.write(b, position, delta);
            position = position + delta;
            this.spaceLeft = this.spaceLeft - delta;
        }
    }

    @Override
    public void close() throws IOException {
        this.bos.close();
    }
}
