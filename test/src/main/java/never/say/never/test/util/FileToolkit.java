package never.say.never.test.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import never.say.never.test.xml.XmlObjectCollector;
import never.say.never.test.xml.XmlStreamToolkit;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.openxml4j.util.ZipArchiveThresholdInputStream;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.util.IOUtils;
import org.ehcache.sizeof.SizeOf;
import org.ehcache.sizeof.SizeOfFilterSource;

import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static java.security.AccessController.doPrivileged;


/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-13
 */
@Slf4j
public class FileToolkit {
    public static final String TMP_DIR_SYS_PROPERTY_NAME = "java.filetoolkit.tmpdir";
    public static final boolean IS_WIN_SYS = System.getProperty("os.name").toLowerCase(Locale.getDefault())
            .contains("windows");
    public static final String DIR_SEPARATOR = FileSystems.getDefault().getSeparator();
    public static final Path TMP_DIR;
    public static final int BUFFER_SIZE = 4096;
    private static final SAXParserFactory SAX_FACTORY = SAXParserFactory.newInstance();

    private static final SizeOf sizeOf = SizeOf.newInstance(new SizeOfFilterSource(true).getFilters());

    static {
        if (StringUtils.isBlank(System.getProperty(TMP_DIR_SYS_PROPERTY_NAME))) {
            String tmpDirName = FileToolkit.class.getSimpleName().toLowerCase(Locale.getDefault());
            System.setProperty(TMP_DIR_SYS_PROPERTY_NAME,
                    FileSystems.getDefault().getRootDirectories().iterator().next() + tmpDirName);
        }
        TMP_DIR = Paths.get(doPrivileged((PrivilegedAction<String>) () -> System.getProperty(TMP_DIR_SYS_PROPERTY_NAME)));
        if (!Files.exists(TMP_DIR)) {
            try {
                Files.createDirectories(TMP_DIR);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    public static void releaseCloseables(List<AutoCloseable> closeables) throws Exception {
        if (closeables == null || closeables.isEmpty()) {
            return;
        }
        for (AutoCloseable c : closeables) {
            if (c == null) {
                continue;
            }
            try (c) {
            }
        }
    }

    public static void releaseCloseables(AutoCloseable... closeables) throws Exception {
        if (closeables == null || closeables.length == 0) {
            return;
        }
        for (AutoCloseable c : closeables) {
            if (c == null) {
                continue;
            }
            try (c) {
            }
        }
    }

    public static int readTo(File inFile, File outFile) throws IOException {
        Objects.requireNonNull(inFile, "No input File specified");
        Objects.requireNonNull(outFile, "No output File specified");
        InputStream in = new FileInputStream(inFile);
        OutputStream out = new FileOutputStream(outFile);
        return readTo(in, out);
    }

    public static int readTo(File inFile, OutputStream out) throws IOException {
        Objects.requireNonNull(inFile, "No input File specified");
        Objects.requireNonNull(out, "No OutputStream specified");
        InputStream in = new FileInputStream(inFile);
        return readTo(in, out);
    }

    public static int readTo(InputStream in, File outFile) throws IOException {
        Objects.requireNonNull(in, "No InputStream specified");
        Objects.requireNonNull(outFile, "No output File specified");
        OutputStream out = new FileOutputStream(outFile);
        return readTo(in, out);
    }

    public static int readTo(InputStream in, OutputStream out) throws IOException {
        try (in; out) {
            return copy(in, out);
        }
    }

    /**
     * Copy the contents of the given InputStream to the given OutputStream.
     * <p>Leaves both streams open when done.
     *
     * @param in  the InputStream to copy from
     * @param out the OutputStream to copy to
     * @return the number of bytes copied
     * @throws IOException in case of I/O errors
     * @see org.springframework.util.StreamUtils#copy(InputStream, OutputStream)
     * @see org.springframework.util.StreamUtils#copyRange(InputStream, OutputStream, long, long)
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        Objects.requireNonNull(in, "No InputStream specified");
        Objects.requireNonNull(out, "No OutputStream specified");
        int byteCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }


    public static long sizeOf(Object object) {
        return sizeOf.deepSizeOf(object);
    }

    public static ZipEntrySource openZipEntrySourceStream(InputStream fis) throws InvalidOperationException {
        final ZipArchiveThresholdInputStream zis;
        // Acquire a resource that is needed to read the next level of openZipEntrySourceStream
        try {
            // open the zip input stream
            zis = ZipHelper.openZipStream(fis); // NOSONAR
        } catch (final IOException e) {
            // If the source cannot be acquired, abort (no resources to free at this level)
            throw new InvalidOperationException("Could not open the file input stream", e);
        }

        // If an error occurs while reading the next level of openZipEntrySourceStream, free the acquired resource
        try {
            // read from the zip input stream
            return openZipEntrySourceStream(zis);
        } catch (final InvalidOperationException | UnsupportedFileFormatException e) {
            // abort: close the zip input stream
            IOUtils.closeQuietly(zis);
            throw e;
        } catch (final Exception e) {
            // abort: close the zip input stream
            IOUtils.closeQuietly(zis);
            throw new InvalidOperationException("Failed to read the zip entry source stream", e);
        }
    }

    private static ZipEntrySource openZipEntrySourceStream(ZipArchiveThresholdInputStream zis) throws InvalidOperationException {
        // Acquire the final level resource. If this is acquired successfully, the zip package was read successfully from the input stream
        try {
            // open the zip entry source stream
            return new ZipInputStreamZipEntrySource(zis);
        } catch (IOException e) {
            throw new InvalidOperationException("Could not open the specified zip entry source stream", e);
        }
    }

    public static String getSuffix(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        int len = fileName.length();
        int splitIndex = fileName.lastIndexOf(".");
        if (splitIndex < 0) {
            return null;
        }
        if (++splitIndex >= len) {
            return null;
        }
        return fileName.substring(splitIndex);
    }

    public static void readXmlLine(InputStream inputStream) throws Exception {

    }

    public static void main(String[] args) throws Exception {
        long st = System.currentTimeMillis();
        FileInputStream inputStream = new FileInputStream(new File("C:\\Users\\Ivan\\Desktop\\2023内部测试缺陷 - 副本\\xl\\worksheets\\sheet9.xml"));
        XmlObjectCollector collector;
        collector = new XmlObjectCollector(Lists.newArrayList("worksheet.sheetData.row"));
        AtomicLong st2 = new AtomicLong(System.currentTimeMillis());
        collector.onTake(xmlStreamObject -> {
            System.out.println("用时：" + (System.currentTimeMillis() - st2.get()));
            st2.set(System.currentTimeMillis());
        });
//        objectFetcher = new CustomXmlStreamObjectFetcher(Lists.newArrayList("worksheet.sheetData"));
        XmlStreamToolkit.INSTANCE.readFrom(inputStream, collector);
        System.out.println("用时：" + (System.currentTimeMillis() - st));
    }
}
