package com.lge.morpho.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileOperator {
    public static boolean isFileExists(String filePath) {
        if (filePath == null) {
            return false;
        }
        return new File(filePath).exists();
    }

    public static boolean copyFile(String srcPath, String dstPath) {
        if (srcPath == null || dstPath == null) {
            return 0;
        }
        File src = new File(srcPath);
        File dst = new File(dstPath);
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dst).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return true;
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e222) {
                    e222.printStackTrace();
                }
            }
            return 0;
        } catch (IOException e2222) {
            e2222.printStackTrace();
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e22222) {
                    e22222.printStackTrace();
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e222222) {
                    e222222.printStackTrace();
                }
            }
            return 0;
        } catch (Throwable th) {
            if (srcChannel != null) {
                try {
                    srcChannel.close();
                } catch (IOException e2222222) {
                    e2222222.printStackTrace();
                }
            }
            if (dstChannel != null) {
                try {
                    dstChannel.close();
                } catch (IOException e22222222) {
                    e22222222.printStackTrace();
                }
            }
        }
    }

    public static boolean renameFile(String srcPath, String dstPath) {
        boolean result = false;
        if (srcPath == null || dstPath == null) {
            return 0;
        }
        File src = new File(srcPath);
        File dst = new File(dstPath);
        if (src.exists()) {
            result = src.renameTo(dst);
        }
        return result;
    }

    public static boolean deleteFile(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void cleanDir(File dir) {
        if (dir != null) {
            String[] children = dir.list();
            if (children != null) {
                for (String file : children) {
                    File file2 = new File(dir, file);
                    if (file2.isFile()) {
                        file2.delete();
                    }
                }
            }
        }
    }

    public static void outputData(byte[] data, String path) {
        IOException e;
        FileNotFoundException e2;
        Throwable th;
        FileOutputStream o_stream = null;
        try {
            FileOutputStream o_stream2 = new FileOutputStream(new File(path));
            try {
                o_stream2.write(data);
                if (o_stream2 != null) {
                    try {
                        o_stream2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                        o_stream = o_stream2;
                        return;
                    }
                }
                o_stream = o_stream2;
            } catch (FileNotFoundException e4) {
                e2 = e4;
                o_stream = o_stream2;
                try {
                    e2.printStackTrace();
                    if (o_stream != null) {
                        try {
                            o_stream.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (o_stream != null) {
                        try {
                            o_stream.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e322 = e5;
                o_stream = o_stream2;
                e322.printStackTrace();
                if (o_stream != null) {
                    try {
                        o_stream.close();
                    } catch (IOException e3222) {
                        e3222.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                o_stream = o_stream2;
                if (o_stream != null) {
                    o_stream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            e2 = e6;
            e2.printStackTrace();
            if (o_stream != null) {
                o_stream.close();
            }
        } catch (IOException e7) {
            e3222 = e7;
            e3222.printStackTrace();
            if (o_stream != null) {
                o_stream.close();
            }
        }
    }

    public static void outputData(ByteBuffer data, String path) {
        IOException e;
        FileNotFoundException e2;
        Throwable th;
        FileOutputStream o_stream = null;
        FileChannel outChannel = null;
        try {
            FileOutputStream o_stream2 = new FileOutputStream(new File(path));
            try {
                outChannel = o_stream2.getChannel();
                outChannel.write(data);
                if (o_stream2 != null) {
                    try {
                        o_stream2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                if (outChannel != null) {
                    try {
                        outChannel.close();
                        o_stream = o_stream2;
                        return;
                    } catch (IOException e32) {
                        e32.printStackTrace();
                        o_stream = o_stream2;
                        return;
                    }
                }
            } catch (FileNotFoundException e4) {
                e2 = e4;
                o_stream = o_stream2;
                try {
                    e2.printStackTrace();
                    if (o_stream != null) {
                        try {
                            o_stream.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    if (outChannel != null) {
                        try {
                            outChannel.close();
                        } catch (IOException e3222) {
                            e3222.printStackTrace();
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (o_stream != null) {
                        try {
                            o_stream.close();
                        } catch (IOException e32222) {
                            e32222.printStackTrace();
                        }
                    }
                    if (outChannel != null) {
                        try {
                            outChannel.close();
                        } catch (IOException e322222) {
                            e322222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e322222 = e5;
                o_stream = o_stream2;
                e322222.printStackTrace();
                if (o_stream != null) {
                    try {
                        o_stream.close();
                    } catch (IOException e3222222) {
                        e3222222.printStackTrace();
                    }
                }
                if (outChannel != null) {
                    try {
                        outChannel.close();
                    } catch (IOException e32222222) {
                        e32222222.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                o_stream = o_stream2;
                if (o_stream != null) {
                    o_stream.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            e2 = e6;
            e2.printStackTrace();
            if (o_stream != null) {
                o_stream.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        } catch (IOException e7) {
            e32222222 = e7;
            e32222222.printStackTrace();
            if (o_stream != null) {
                o_stream.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    public static byte[] read(String path) {
        IOException e;
        FileNotFoundException e2;
        Throwable th;
        byte[] buf = new byte[((int) new File(path).length())];
        FileInputStream fis = null;
        try {
            FileInputStream fis2 = new FileInputStream(path);
            try {
                if (fis2.read(buf) != -1) {
                    if (fis2 != null) {
                        try {
                            fis2.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    fis = fis2;
                    return buf;
                }
                if (fis2 != null) {
                    try {
                        fis2.close();
                    } catch (IOException e32) {
                        e32.printStackTrace();
                        fis = fis2;
                    }
                }
                fis = fis2;
                return null;
            } catch (FileNotFoundException e4) {
                e2 = e4;
                fis = fis2;
                try {
                    e2.printStackTrace();
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e322) {
                            e322.printStackTrace();
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e3222) {
                            e3222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e5) {
                e3222 = e5;
                fis = fis2;
                e3222.printStackTrace();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e32222) {
                        e32222.printStackTrace();
                    }
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                fis = fis2;
                if (fis != null) {
                    fis.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e6) {
            e2 = e6;
            e2.printStackTrace();
            if (fis != null) {
                fis.close();
            }
            return null;
        } catch (IOException e7) {
            e32222 = e7;
            e32222.printStackTrace();
            if (fis != null) {
                fis.close();
            }
            return null;
        }
    }
}
