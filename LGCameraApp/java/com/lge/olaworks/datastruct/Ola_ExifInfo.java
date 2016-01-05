package com.lge.olaworks.datastruct;

public class Ola_ExifInfo {

    public static class Ifd {
        public int count;
        public byte[] data;
        public int dataFormat;
        public int tag;

        public Ifd() {
            this.tag = 0;
            this.dataFormat = 0;
            this.count = 0;
            this.data = null;
        }
    }

    public static class Img {
        public int compression;
        public byte[] img;
        public int resolUnit;
        public int size;
        Ola_ExifUrational xResol;
        Ola_ExifUrational yResol;
        public int ycbcrPos;

        public Img(int ycbcrPos, int compression, byte[] img, int size, int x_numerator, int x_denominator, int y_numerator, int y_denominator, int resolUnit) {
            this.ycbcrPos = ycbcrPos;
            this.compression = compression;
            this.img = img;
            this.size = size;
            this.xResol = new Ola_ExifUrational(x_numerator, x_denominator);
            this.yResol = new Ola_ExifUrational(y_numerator, y_denominator);
            this.resolUnit = resolUnit;
        }

        public Img(byte[] img, int size) {
            this.ycbcrPos = 0;
            this.compression = 0;
            this.img = img;
            this.size = size;
            this.xResol = new Ola_ExifUrational();
            this.yResol = new Ola_ExifUrational();
            this.resolUnit = 0;
        }

        public Img() {
            this.ycbcrPos = 0;
            this.compression = 0;
            this.img = new byte[0];
            this.size = 0;
            this.xResol = new Ola_ExifUrational();
            this.yResol = new Ola_ExifUrational();
            this.resolUnit = 0;
        }
    }

    public static class Private {
        public int colorSpace;
        public int componentsConfig;
        public int exifVersion;
        public int flashPixversion;
        public int pixelXdim;
        public int pixelYdim;

        public Private(int exifVersion, int componentsConfig, int flashPixversion, int colorSpace, int pixelXdim, int pixelYdim) {
            this.exifVersion = exifVersion;
            this.componentsConfig = componentsConfig;
            this.flashPixversion = flashPixversion;
            this.colorSpace = colorSpace;
            this.pixelXdim = pixelXdim;
            this.pixelYdim = pixelYdim;
        }

        public Private() {
            this.exifVersion = 0;
            this.componentsConfig = 0;
            this.flashPixversion = 0;
            this.colorSpace = 0;
            this.pixelXdim = 0;
            this.pixelYdim = 0;
        }
    }
}
